package com.pcitc.htmltopdf.service.impl;

import com.pcitc.htmltopdf.entity.ImageEntity;
import com.pcitc.htmltopdf.entity.LookupJsonModel;
import com.pcitc.htmltopdf.entity.PrintTempEntity;
import com.pcitc.htmltopdf.service.PrintTemplateGenerate;
import com.pcitc.htmltopdf.util.print.*;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.math.BigDecimal;
import java.util.*;

public class PrintTemplateGenerateImpl implements PrintTemplateGenerate {

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String FORMAT_TIME = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final String FORMAT_TIME2 = "yyyy年MM月dd日 ";

	/** sql查询结果类型   */
	// Map
	public static final String RESULTDATATYPE_TYPE_1 = "1";
	// List
	public static final String RESULTDATATYPE_TYPE_2 = "2";

	/** 自定义数据类型 */
	// 金额(千分位)
	private static final String field_type_1 = "1";
	// 金额(大写)
	private static final String field_type_2 = "2";
	// 利率
	private static final String field_type_3 = "3";
	// 日期
	private static final String field_type_4 = "4";
	// 日期(2018年01月01日)
	private static final String field_type_5 = "5";

	@Override
	public Map<String, List<PrintTempEntity>> getMorePrintData(List<Map<String, Object>> paramMap) {
		Map<String, List<PrintTempEntity>> resultMap = new HashMap<String, List<PrintTempEntity>>();
		try {
			for(Map<String, Object> stringObjectMap : paramMap) {
				PrintTempEntity printTempEntity = this.getSinglePrintData(stringObjectMap);
				if(null == printTempEntity) break;
				List<PrintTempEntity> tempList = resultMap.get(printTempEntity.getId());
				if(tempList == null) {
					tempList = new ArrayList<PrintTempEntity>();
				}
				tempList.add(printTempEntity);
				resultMap.put(printTempEntity.getId(), tempList);
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return resultMap;
	}

	/**
	 *
	 * 打印模板格式大致分三种类型
	 * 	 不同的类型说明见 PrintTemplateManagerImpl 开始部分
	 */
	@Override
	public PrintTempEntity getSinglePrintData(Map<String, Object> paramMap) {
		System.out.println(paramMap);
		try {
			if(paramMap.get("businessId") == null){
				throw new RuntimeException("业务ID为必填项");
			}
			String businessId = paramMap.get("businessId").toString();
			LookupJsonModel lookupJsonModel = (LookupJsonModel) paramMap.get("lookModel");

			//通过关联表，查找模板id
			String getTempIdSql = String.format("select TEMP_ID from PRINT_TEMP_BUSINESS where BUSINESS_ID = '%s' ", businessId);
			String printTempId = jdbcTemplate.queryForObject(getTempIdSql, String.class);
			//获取模板信息
			String printSql = String.format("select * from PRINT_TEMP where ID = '%s' ", printTempId);
			PrintTempEntity printTempEntity = ReflectUtil.autoReflect(jdbcTemplate.queryForMap(printSql), PrintTempEntity.class);
			if (printTempEntity == null || printTempEntity.getContent() == null) return null;
			//模板html
			String tableHtml = printTempEntity.getContent();
			//设置jsoup document, 解决生成的html 空标签不闭合的问题
			Document document = Jsoup.parse(tableHtml);
			document.outputSettings(new Document.OutputSettings().syntax(Document.OutputSettings.Syntax.xml));
			//子模版取消，但是需要thead，tbody，tfoot。所以这里通过有无id，进行分类。
			Elements tbodyTrs = document.select("table#tbody > tbody > tr");
			boolean flag = false; //是否出现过id
			StringBuilder thead = new StringBuilder();
			StringBuilder tbody = new StringBuilder();
			StringBuilder tfoot = new StringBuilder();
			for(int i = 0; i < tbodyTrs.size(); i++) {
				//没有id
				String tempId = tbodyTrs.get(i).attr("id");
				String outerHtml = tbodyTrs.get(i).outerHtml();
				document.select("table#tbody > tbody > tr").get(i).remove();
				System.out.println(tempId);
				//如果出现过id，全部放到tfoot
				if(flag) {
					tfoot.append(outerHtml);
				} else {
					//如果id空，放到thead
					if(StringUtils.isBlank(tempId)) {
						thead.append(outerHtml);
						//id不空，放到tbody，且flag为true
					} else {
						flag = true;
						tbody.append(outerHtml);
					}
				}
			}
			document.select("table#tbody > tbody").before("<thead>" + thead.toString() + "</thead>");
			document.select("table#tbody > tbody").after("<tfoot>" + tfoot.toString() + "</tfoot>");
			document.select("table#tbody > tbody").html(tbody.toString());

			/* print data
			 * 1.跨行 结息模板的处理，先判断list，通过list的id获取，获取 tr#id 元素，进行遍历替换，计算跨行的情况。
			 * 如果一行增加两个空行，如果两个增加一个空行，以此类推，最少三行。
			  * */

			Map<String, Object > dataMap = paramMap;
			List<Map<String, Object>> tableDataList = new ArrayList<Map<String, Object>>();

			//map 数据的处理
			String printTempSql = String.format(" select * from PRINT_TEMP_SQL where PRINT_TEMP_ID = '%s' ", printTempId);
			List<Map<String, Object>> printTempSqls = jdbcTemplate.queryForList(printTempSql);
			if (printTempSqls != null && !printTempSqls.isEmpty()) {
				for (Map<String, Object> map : printTempSqls) {
					String tempId = map.get("ID").toString();
					String tempSql = map.get("CONTENT").toString();
					tempSql = tempSql +  SqlBuilder.buildSql(lookupJsonModel);
					String resultDataType = map.get("RESULT_DATA_TYPE").toString();
					if(RESULTDATATYPE_TYPE_2.equals(resultDataType) || document.select("tr#" + tempId).size() > 0) continue;
					List<Map<String, Object>> tempList = namedParameterJdbcTemplate.queryForList(tempSql, paramMap);
					if (tempList == null || tempList.isEmpty()) continue;
					dataMap.putAll(tempList.get(0));
				}
			}
			// 格式化map数据
			String printTempDataField = " select * from PRINT_TEMP_DATATYPE where PRINT_TEMP_ID = '" + printTempEntity.getId() + "' ";
			List<Map<String, Object>> printTempDatafields = jdbcTemplate.queryForList(printTempDataField);
			dataMap = this.getFormatDataForMap(dataMap, paramMap, printTempDatafields);

			/*
			跨行指定list id 的处理
			1. 通过id,获得和sql对应的tr
			2. 生成tr之后，删除所有tr内有 delete 标记的 td，以及 tr 的 id(第一行除外)，
			3. 修改第一行的 td 的 rowSpan.
			 */
			Element element = new Element("table");
			if(null != printTempSqls) {
				for (int i = 0; i < printTempSqls.size(); i++) {
					Map<String, Object> map = printTempSqls.get(i);
					String tempId = map.get("ID").toString();
					String tempSql = map.get("CONTENT").toString();
					tempSql = tempSql +  SqlBuilder.buildSql(lookupJsonModel);
					String resultDataTypes = map.get("RESULT_DATA_TYPE").toString();
					if(!RESULTDATATYPE_TYPE_2.equals(resultDataTypes)) continue;
					Element tr = document.select("tr#" + tempId).first();
					if(null == tr) continue;

					//遍历生成行
					List<Map<String, Object>> tempList = namedParameterJdbcTemplate.queryForList(tempSql, paramMap);
					if (tempList == null || tempList.isEmpty()) continue;
					
					int blankLineRowSpan = 1;
					//设置td删除标记
					for(int m = 0; m < tr.select("td").size(); m++) {
						Element td = tr.select("td").get(m);
						String rowSpanString = td.attr("rowspan");
						if(StringUtils.isBlank(rowSpanString)) continue;
						int rowSpan = Integer.parseInt(rowSpanString);
						if(rowSpan < 2) continue;
						tr.select("td").get(m).addClass("delete");
						blankLineRowSpan = rowSpan - 1;
					}
					
					StringBuilder tempHtmlBuilder = new StringBuilder();
					for (Map<String, Object> map1 : tempList) {
						map1 = this.getFormatDataForMap(map1, dataMap, printTempDatafields);
						tempHtmlBuilder.append(this.getTableHtmlDataMap(map1, tr.outerHtml()));
					}
					element.append(tempHtmlBuilder.toString());
					//删除多余的td
					for(int k = 0; k < element.select("tr").size(); k++) {
						if(k == 0) continue;
						Elements elements = element.select("tr");
						elements.get(k).removeAttr("id");
						elements.get(k).select("td.delete").remove();
					}
					element.select("td.delete").attr("rowspan", tempList.size() + blankLineRowSpan + "");
					document.select("tr#" + tempId).after(element.select("tr").outerHtml()).remove();
					element.select("tr").remove();
				}
			}
			//删除多余的p，br等会占行的元素
			document.select("body > p").remove();
			document.select("body > br").remove();
			
			tableHtml = document.html();
			// map数据替换，必须先进行list替换
			if(dataMap != null){
				tableHtml = this.getTableHtmlDataMap(dataMap, tableHtml);
			}

			//放入 seal
			if(null != printTempEntity.getImgIdSeal()) {
				try{
					String companyLogoSql = "select * from print_image pt where pt.id='" + printTempEntity.getImgIdSeal() + "'";
					Map<String, Object> resultMap = jdbcTemplate.queryForMap(companyLogoSql);
					ImageEntity imageEntity = ReflectUtil.autoReflect(resultMap, ImageEntity.class);
					printTempEntity.setImgSeal(imageEntity);
				} catch (Exception e) {
					// TODO: handle exception
					System.err.println("seal 没有找到");
				}
			}

			//放入 logo
			if(printTempEntity.getImgIdLogo() != null) {
				try{
					String companyLogoSql = "select * from print_image pt where pt.id='" + printTempEntity.getImgIdLogo() + "'";
					Map<String, Object> resultMap = jdbcTemplate.queryForMap(companyLogoSql);
					ImageEntity imageEntity = ReflectUtil.autoReflect(resultMap, ImageEntity.class);
					printTempEntity.setImgLogo(imageEntity);
				} catch (Exception e) {
					// TODO: handle exception
					System.err.println("logo 没有找到");
				}
			}
			// 放入 html
			printTempEntity.setHtml(tableHtml);
			return printTempEntity;
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	/* 列表(不含子模板) 取打印数据 */
//	private String getTempListTypeHtml(String tableHtml,
//	                                   List<Map<String, Object>> tableDataList) {
//
//		Document html=Jsoup.parse(tableHtml);
//		Element trModel = html.select("tr").last();
//
//		StringBuffer trs = new StringBuffer();
//		for (Map<String, Object> map : tableDataList) {
//			String newtrStr = trModel.toString();
//			for (String key : map.keySet()) {
//				if(newtrStr.indexOf(key) < 0){
//					continue;
//				}
//				String paramName = "@"+key.toUpperCase()+"@";
//				String value = map.get(key).toString();
//
//				newtrStr = newtrStr.replace(paramName, value);//单列
//			}
//			trs.append(newtrStr);// 一行
//		}
////		System.out.println("trs="+trs.toString());
//		return trs.toString();
//	}


	/* 完整的字模板的数据 */
//	private List<String> getTotalSubPrintTempPrintDataByCurrentTempId(
//					String tableHtml, String containSubTempIds, Map<String, Object> paramMap) {
//		List<String> html = new ArrayList<String>();
//		if(StringUtils.isBlank(containSubTempIds)){
//			html.add(tableHtml);
//			return html;
//		}
//
//		Document parseHtml = Jsoup.parse(tableHtml);
//		Document tempHtml = Jsoup.parse(tableHtml);
//		//头部
//		if(parseHtml.select("#thead").size() > 0) {
//			String theadTbodyHtml = tempHtml.select("#thead tbody").html();
//			tempHtml.select("#thead").after("<table id=\"tbody\"><thead>"+ theadTbodyHtml +"</thead><tbody></tbody></table>");
//			tempHtml.select("#thead").remove();
//		}
//		if(parseHtml.select("#tfoot").size() > 0) {
//			String tfootTbodyHtml = tempHtml.select("#tfoot tbody").html();
//			tempHtml.select("#tbody").append("<tfoot>" + tfootTbodyHtml + "</tfoot>");
//			tempHtml.select("#tfoot").remove();
//		}
//
//		Map<String, Object > headerDataMap = paramMap;
//		List<Map<String, Object>> tableDataList = null;
//
//		for (String printTempId : containSubTempIds.split("_")) {
//			if(printTempId.startsWith("#")){
//				printTempId = printTempId.substring(1);
//			}
//			if(printTempId.endsWith("#")){
//				printTempId = printTempId.substring(0, printTempId.length()-1);
//			}
//
//
//			String printSqlCount = "select count(*) from PRINT_TEMP where ID like '" + printTempId + "' ";
//			int i = jdbcTemplate.queryForObject(printSqlCount, Integer.class);
//			if(i < 1){
//				continue;
//			}
//
//			// base info
//			String printSql = "select * from PRINT_TEMP where ID like '" + printTempId + "' ";
//			Map<String, Object> printTempMap = jdbcTemplate.queryForMap(printSql);
//			if (printTempMap == null || printTempMap.get("content") == null) {
//				return null;
//			}
//
//			headerDataMap = paramMap;
//			tableDataList = new ArrayList<Map<String,Object>>();
//
//
//			// print data
//			String printTempSql = " select * from PRINT_TEMP_SQL where PRINT_TEMP_ID = '" + printTempId + "' ";
//			List<Map<String, Object>> printTempSqls = jdbcTemplate.queryForList(printTempSql);
//			if (printTempSqls != null && !printTempSqls.isEmpty()) {
//				for (Map<String, Object> map : printTempSqls) {
//					String tempSql = map.get("CONTENT").toString();
//					String resultDataTypes = map.get("RESULT_DATA_TYPE").toString();
//
//					List<Map<String, Object>> tempList = namedParameterJdbcTemplate.queryForList(tempSql, paramMap);
//					if (tempList == null || tempList.isEmpty()) {
//						continue;
//					}
//
//					// sql结果集
//					if(resultDataTypes.equals(RESULTDATATYPE_TYPE_1)){
//						// Map
//						headerDataMap.putAll(tempList.get(0));
//					}else{
//						// List
//						tableDataList.addAll(tempList);
//					}
//				}
//			}
//
//			// format
//			String printTempDatafield = " select * from PRINT_TEMP_DATATYPE where PRINT_TEMP_ID = '" + printTempId + "' ";
//			List<Map<String, Object>> printTempDatafields = jdbcTemplate.queryForList(printTempDatafield);
//			headerDataMap = this.getFormatDataForMap(headerDataMap, paramMap, printTempDatafields);
//			tableDataList = this.getFormatDataForList(printTempId, tableDataList, paramMap, printTempDatafields);
//
//			// print html
//			String subTableHtml = printTempMap.get("CONTENT").toString();
//			if(headerDataMap != null){
//				subTableHtml = this.getTableHtmlDataMap(headerDataMap, subTableHtml);
//			}
//			subTableHtml = this.getTempListTypeHtml(subTableHtml, tableDataList);
//
//			tempHtml.select("#tbody tbody").append(subTableHtml);
////			tableHtml = tableHtml.replace("#"+printTempId+"#", subTableHtml);
////			System.out.println(tableHtml.toString());
//
//			html.add(tempHtml.toString());
//		}
//		return html;
//	}


	/* 列表数据格式化 */
	@SuppressWarnings("unchecked")
//	private List<Map<String, Object>> getFormatDataForList(String printTempId,
//	                                                       List<Map<String, Object>> tableDataList,
//	                                                       Map<String, Object> paramMap, List<Map<String, Object>> printTempDatafields) {
//		// 0.数据列表
//		if(tableDataList == null || tableDataList.isEmpty()
//						|| printTempDatafields==null || printTempDatafields.isEmpty()){
//			return tableDataList;
//		}
//
//		// 1.指定格式化字段名列表
//		// printTempDatafields --> Map<String, Object>
//		if(printTempDatafields == null|| printTempDatafields.isEmpty()){
//			return tableDataList;
//		}
//
//		// 2.指定格式化字段名列表   ==>  Map
//		// field:Map
//		Map<String, Object> printTempDatafieldMap = new HashMap<String, Object>();
//		for (Map<String, Object> map : printTempDatafields) {
//			// 将字段名作为KEY 值
//			String fieldName = map.get("FIELD_NAME").toString();
//			printTempDatafieldMap.put(fieldName, map);
//		}
//
//		// 3.列表指定列 数据格式化(1.文字替换 需要显示的数据值来自另一张表,2.指定数据 按照指定格式显示)
//		/*
//		 * for
//		 * 	 map key:vlaue key:vlaue
//		 */
//		for (Map<String, Object> map : tableDataList) {
//			for (String key : map.keySet()) {
//				// 1.格式化字段
//				Object o = printTempDatafieldMap.get(key);
//				if(o == null){
//					continue;
//				}
//				Map<String, Object> t = (Map<String, Object>)o;
//				if (t.get("FIELD_NAME") == null
//								|| StringUtils.isBlank(t.get("FIELD_NAME").toString().replaceAll(" ", ""))) {
//					continue;
//				}
//
//				// 2.格式化
//				// 2.1 文字替换    TODO 当前sql参数值，有可能是当前字段的值;例:状态值  需要转换成文字方便阅读 ，其值 来自于另一张表
//				if (t.get("FIELD_SOURCE") != null) {
//					String fieldSource = t.get("FIELD_SOURCE").toString();
//					List<Map<String, Object>> tempList = namedParameterJdbcTemplate.queryForList(fieldSource, paramMap);
//					if (tempList == null || tempList.isEmpty()) {
//						continue;
//					}
//					map.put(key, tempList.get(0).get(key));
//					continue;
//				}
//
//				// 2.2 数据替换
//				if (t.get("FIELD_TYPE") == null) {
//					continue;
//				}
//				String fieldName = t.get("FIELD_NAME").toString().toUpperCase();
//				if (map.get(fieldName) == null || StringUtils.isBlank(fieldName.replaceAll(" ", ""))) {
//					continue;
//				}
//				Object fieldType = t.get("FIELD_TYPE");
//				String formatData = this.getFormatData(map, fieldType, fieldName);
//				map.put(key, formatData);
//			}
//		}
//
//		return tableDataList;
//	}


	/* 模板  Map数据格式化 */
	private Map<String, Object> getFormatDataForMap(Map<String, Object> headerDataMap, Map<String, Object> paramMap, List<Map<String, Object>> printTempDatafields) {

		if(printTempDatafields == null || printTempDatafields.isEmpty()
						|| printTempDatafields==null || printTempDatafields.isEmpty()){
			return headerDataMap;
		}

		for (Map<String, Object> map : printTempDatafields) {
			System.out.println(map);
			if (map.get("FIELD_NAME") == null
							|| StringUtils.isBlank(map.get("FIELD_NAME").toString().replaceAll(" ", ""))) {
				continue;
			}

			// 取数据
			if (map.get("FIELD_SOURCE") != null) {
				String fieldSource = map.get("FIELD_SOURCE").toString();
				List<Map<String, Object>> tempList = namedParameterJdbcTemplate.queryForList(fieldSource, paramMap);
				if (tempList == null || tempList.isEmpty()) {
					continue;
				}
				headerDataMap.putAll(tempList.get(0));
				continue;
			}

			if (map.get("FIELD_TYPE") == null) {
				continue;
			}

			// 数据格式化
			String fieldName = map.get("FIELD_NAME").toString().toUpperCase();
			if (headerDataMap.get(fieldName) == null || StringUtils.isBlank(fieldName.replaceAll(" ", ""))) {
				continue;
			}
 			Object fieldType = map.get("FIELD_TYPE");
			String t = this.getFormatData(headerDataMap, fieldType, fieldName);

			headerDataMap.put(fieldName, t);
		}

		return headerDataMap;
	}

	// 取格式化 数据
	private String getFormatData(Map<String, Object> headerDataMap, Object fieldType, String fieldName) {
		String t = "";
		if (field_type_1.equals(fieldType)) {
			// 金额(千分位)
			Double a = Double.valueOf(headerDataMap.get(fieldName).toString());
			t = FormatUtil.formateNumberWithPrecision2(a);
		}
		if (field_type_2.equals(fieldType)) {
			// 金额(大写)
			Double a = Double.valueOf(headerDataMap.get(fieldName).toString());
			String b = FormatUtil.formateNumberWithPrecision2(a);
			t = MoneyToUppercase.toUpper(b);
		}
		if (field_type_3.equals(fieldType)) {
			// 利率  强转报了不能转换的错误
			BigDecimal a = new BigDecimal(headerDataMap.get(fieldName).toString());
			t = FormatUtil.formateNumberWithoutThousandth(a, 2);
		}
		if (field_type_4.equals(fieldType)) {
			// 日期(2018-01-01)
			Date t1 = (Date) headerDataMap.get(fieldName);
			t =  DateUtil.convertDateToString(t1);
		}
		if (field_type_5.equals(fieldType)) {
			// 日期(2018年01月01日)
			Date t1 = (Date) headerDataMap.get(fieldName);
			t =  DateUtil.formatDate(t1, FORMAT_TIME2);
		}
		return t;
	}


	/* 将数据装载至模板内容的指定位置 */
	private String getTableHtmlDataMap(Map<String, Object> headerDataMap, String tableHtml) {
		if(headerDataMap == null){
			return tableHtml;
		}
		Set<Map.Entry<String, Object>> entrySet = headerDataMap.entrySet();
		for (Map.Entry<String, Object> entry : entrySet) {
			String paramValue = String.valueOf(entry.getValue());
			if(null == paramValue || "null".equalsIgnoreCase(paramValue)) {
				paramValue = "";
			}
			String pamam = "@"+entry.getKey()+"@";
			tableHtml = tableHtml.replaceAll(pamam.toUpperCase(), paramValue);
		}
		return tableHtml;
	}

	/**
	 *
	 * 1.取模板
	 * 2.模版数据
	 * 3.数据格式化
	 * 4.自定义sql
	 * 5.默认执行模板相关sql语句
	 *
	 *
	 *
	 * 页面调用
	 * 	打印或是预览的地方 下拉框展示 根据模版类型查找的模板列表
	 * 		1.页面整合
	 * 		2.后台调用打印模版的接口
	 * 		3.自行整合返回页面
	 */

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

}

