package com.pcitc.htmltopdf.service.impl;

import com.pcitc.htmltopdf.entity.PrintTempEntity;
import com.pcitc.htmltopdf.service.PrintTemplateGenerate;
import com.pcitc.htmltopdf.util.pdf.StringUtils;
import com.pcitc.htmltopdf.util.print.DateUtil;
import com.pcitc.htmltopdf.util.print.FormatUtil;
import com.pcitc.htmltopdf.util.print.MoneyToUppercase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class PrintTemplateGenerateImpl implements PrintTemplateGenerate {

	private final Logger log = LoggerFactory.getLogger(PrintTemplateGenerateImpl.class);

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public PrintTemplateGenerateImpl() {
	}

	public PrintTemplateGenerateImpl(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(this.dataSource);
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
	}

	public PrintTemplateGenerateImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

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
	public List<Map<String, Object>> getMorePrintData(List<Map<String, Object>> paramMap) {
		List<Map<String, Object>> tableArray = new ArrayList<Map<String, Object>>();
		try {
			for (Map<String, Object> stringObjectMap : paramMap) {
				Map<String, Object> m = this.getSinglePrintData(stringObjectMap);
				if(null == m)
					break;
				tableArray.add(m);
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return tableArray;
	}

	/**
	 *
	 * 打印模板格式大致分三种类型
	 * 	 不同的类型说明见 PrintTemplateManagerImpl 开始部分
	 */
	@Override
	public Map<String, Object> getSinglePrintData(Map<String, Object> paramMap)  {
		System.out.println(paramMap);
		Map<String, Object> a = new HashMap<String, Object>();
		List<String> tableArray = null;
		List<PrintTempEntity> printTempEntityArray = null;
		try {
			if(paramMap.get("businessId") == null){
				throw new RuntimeException("业务ID为必填项");
			}
			String businessId = paramMap.get("businessId").toString();

			// print data
			Map<String, Object > headerDataMap = paramMap;
			List<Map<String, Object>> tableDataList = new ArrayList<Map<String, Object>>();

			// template
			String getTempIdSql = String.format("select TEMP_ID from PRINT_TEMP_BUSINESS where BUSINESS_ID = '%s' ", businessId);
			String printTempId = jdbcTemplate.queryForObject(getTempIdSql, String.class);

			String printSql = String.format("select * from PRINT_TEMP where ID = '%s' ", printTempId);
			Map<String, Object> printTempMap = jdbcTemplate.queryForMap(printSql);
			if (printTempMap == null || printTempMap.get("content") == null) {
				return null;
			}

			// print data
			String printTempSql = String.format(" select * from PRINT_TEMP_SQL where PRINT_TEMP_ID = '%s' ", printTempId);
			List<Map<String, Object>> printTempSqls = jdbcTemplate.queryForList(printTempSql);
			if (printTempSqls != null && !printTempSqls.isEmpty()) {
				for (Map<String, Object> map : printTempSqls) {
					String tempSql = map.get("CONTENT").toString();
					String resultDataTypes = map.get("RESULT_DATA_TYPE").toString();

					List<Map<String, Object>> tempList = namedParameterJdbcTemplate.queryForList(tempSql, paramMap);
					if (tempList == null || tempList.isEmpty()) {
						continue;
					}

					if(resultDataTypes.equals(RESULTDATATYPE_TYPE_1)){// Map
						headerDataMap.putAll(tempList.get(0));
					}else{
						tableDataList.addAll(tempList);// List
					}
				}
			}

			// format
			String printTempDatafield = " select * from PRINT_TEMP_DATATYPE where PRINT_TEMP_ID = '" + printTempId + "' ";
			List<Map<String, Object>> printTempDatafields = jdbcTemplate.queryForList(printTempDatafield);
			tableDataList = this.getFormatDataForList(printTempId, tableDataList, paramMap, printTempDatafields);
			headerDataMap = this.getFormatDataForMap(headerDataMap, paramMap, printTempDatafields);

			// map
			String tableHtml = printTempMap.get("CONTENT").toString();

			if(headerDataMap != null){
				tableHtml = this.getTableHtmlDataMap(headerDataMap, tableHtml);
			}

			// print html
			String containSubTemp = printTempMap.get("CONTAIN_SUB_TEMP")==null?"":printTempMap.get("CONTAIN_SUB_TEMP").toString();
			tableArray = this.getTotalSubPrintTempPrintDataByCurrentTempId(tableHtml, containSubTemp, paramMap);

			a.put("html", tableArray);
			a.put("imgX", printTempMap.get("IMG_X"));
			a.put("imgY", printTempMap.get("IMG_Y"));
			a.put("imgName", printTempMap.get("IMG_NAME"));
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return a;
	}

	/* 列表(不含子模板) 取打印数据 */
	private String getTempListTypeHtml(String tableHtml,
	                                   List<Map<String, Object>> tableDataList) {

		Document html=Jsoup.parse(tableHtml);
		Element trModel = html.select("tr").last();

		StringBuffer trs = new StringBuffer();
		for (Map<String, Object> map : tableDataList) {
			String newtrStr = trModel.toString();
			for (String key : map.keySet()) {
				if(newtrStr.indexOf(key) < 0){
					continue;
				}
				String paramName = "@"+key.toUpperCase()+"@";
				String value = map.get(key).toString();

				newtrStr = newtrStr.replace(paramName, value);//单列
			}
			trs.append(newtrStr);// 一行
		}
//		System.out.println("trs="+trs.toString());
		return trs.toString();
	}


	/* 完整的字模板的数据 */
	private List<String> getTotalSubPrintTempPrintDataByCurrentTempId(
					String tableHtml, String containSubTempIds, Map<String, Object> paramMap) {
		List<String> html = new ArrayList<String>();
		if(StringUtils.isBlank(containSubTempIds)){
			html.add(tableHtml);
			return html;
		}

		Document parseHtml = Jsoup.parse(tableHtml);
		Document tempHtml = Jsoup.parse(tableHtml);
		//头部
		if(parseHtml.select("#thead").size() > 0) {
			String theadTbodyHtml = tempHtml.select("#thead tbody").html();
			tempHtml.select("#thead").after("<table id=\"tbody\"><thead>"+ theadTbodyHtml +"</thead><tbody></tbody></table>");
			tempHtml.select("#thead").remove();
		}
		if(parseHtml.select("#tfoot").size() > 0) {
			String tfootTbodyHtml = tempHtml.select("#tfoot tbody").html();
			tempHtml.select("#tbody").append("<tfoot>" + tfootTbodyHtml + "</tfoot>");
			tempHtml.select("#tfoot").remove();
		}

		Map<String, Object > headerDataMap = paramMap;
		List<Map<String, Object>> tableDataList = null;

		for (String printTempId : containSubTempIds.split("_")) {
			if(printTempId.startsWith("#")){
				printTempId = printTempId.substring(1);
			}
			if(printTempId.endsWith("#")){
				printTempId = printTempId.substring(0, printTempId.length()-1);
			}


			String printSqlCount = "select count(*) from PRINT_TEMP where ID like '" + printTempId + "' ";
			int i = jdbcTemplate.queryForObject(printSqlCount, Integer.class);
			if(i < 1){
				continue;
			}

			// base info
			String printSql = "select * from PRINT_TEMP where ID like '" + printTempId + "' ";
			Map<String, Object> printTempMap = jdbcTemplate.queryForMap(printSql);
			if (printTempMap == null || printTempMap.get("content") == null) {
				return null;
			}

			headerDataMap = paramMap;
			tableDataList = new ArrayList<Map<String,Object>>();


			// print data
			String printTempSql = " select * from PRINT_TEMP_SQL where PRINT_TEMP_ID = '" + printTempId + "' ";
			List<Map<String, Object>> printTempSqls = jdbcTemplate.queryForList(printTempSql);
			if (printTempSqls != null && !printTempSqls.isEmpty()) {
				for (Map<String, Object> map : printTempSqls) {
					String tempSql = map.get("CONTENT").toString();
					String resultDataTypes = map.get("RESULT_DATA_TYPE").toString();

					List<Map<String, Object>> tempList = namedParameterJdbcTemplate.queryForList(tempSql, paramMap);
					if (tempList == null || tempList.isEmpty()) {
						continue;
					}

					// sql结果集
					if(resultDataTypes.equals(RESULTDATATYPE_TYPE_1)){
						// Map
						headerDataMap.putAll(tempList.get(0));
					}else{
						// List
						tableDataList.addAll(tempList);
					}
				}
			}

			// format
			String printTempDatafield = " select * from PRINT_TEMP_DATATYPE where PRINT_TEMP_ID = '" + printTempId + "' ";
			List<Map<String, Object>> printTempDatafields = jdbcTemplate.queryForList(printTempDatafield);
			headerDataMap = this.getFormatDataForMap(headerDataMap, paramMap, printTempDatafields);
			tableDataList = this.getFormatDataForList(printTempId, tableDataList, paramMap, printTempDatafields);

			// print html
			String subTableHtml = printTempMap.get("CONTENT").toString();
			if(headerDataMap != null){
				subTableHtml = this.getTableHtmlDataMap(headerDataMap, subTableHtml);
			}
			subTableHtml = this.getTempListTypeHtml(subTableHtml, tableDataList);

			tempHtml.select("#tbody tbody").append(subTableHtml);
//			tableHtml = tableHtml.replace("#"+printTempId+"#", subTableHtml);
//			System.out.println(tableHtml.toString());

			html.add(tempHtml.toString());
		}
		return html;
	}


	/* 列表数据格式化 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getFormatDataForList(String printTempId,
	                                                       List<Map<String, Object>> tableDataList,
	                                                       Map<String, Object> paramMap, List<Map<String, Object>> printTempDatafields) {
		// 0.数据列表
		if(tableDataList == null || tableDataList.isEmpty()
						|| printTempDatafields==null || printTempDatafields.isEmpty()){
			return tableDataList;
		}

		// 1.指定格式化字段名列表
		// printTempDatafields --> Map<String, Object>
		if(printTempDatafields == null|| printTempDatafields.isEmpty()){
			return tableDataList;
		}

		// 2.指定格式化字段名列表   ==>  Map
		// field:Map
		Map<String, Object> printTempDatafieldMap = new HashMap<String, Object>();
		for (Map<String, Object> map : printTempDatafields) {
			// 将字段名作为KEY 值
			String fieldName = map.get("FIELD_NAME").toString();
			printTempDatafieldMap.put(fieldName, map);
		}

		// 3.列表指定列 数据格式化(1.文字替换 需要显示的数据值来自另一张表,2.指定数据 按照指定格式显示)
		/*
		 * for
		 * 	 map key:vlaue key:vlaue
		 */
		for (Map<String, Object> map : tableDataList) {
			for (String key : map.keySet()) {
				// 1.格式化字段
				Object o = printTempDatafieldMap.get(key);
				if(o == null){
					continue;
				}
				Map<String, Object> t = (Map<String, Object>)o;
				if (t.get("FIELD_NAME") == null
								|| StringUtils.isBlank(t.get("FIELD_NAME").toString().replaceAll(" ", ""))) {
					continue;
				}

				// 2.格式化
				// 2.1 文字替换    TODO 当前sql参数值，有可能是当前字段的值;例:状态值  需要转换成文字方便阅读 ，其值 来自于另一张表
				if (t.get("FIELD_SOURCE") != null) {
					String fieldSource = t.get("FIELD_SOURCE").toString();
					List<Map<String, Object>> tempList = namedParameterJdbcTemplate.queryForList(fieldSource, paramMap);
					if (tempList == null || tempList.isEmpty()) {
						continue;
					}
					map.put(key, tempList.get(0).get(key));
					continue;
				}

				// 2.2 数据替换
				if (t.get("FIELD_TYPE") == null) {
					continue;
				}
				String fieldName = t.get("FIELD_NAME").toString().toUpperCase();
				if (map.get(fieldName) == null || StringUtils.isBlank(fieldName.replaceAll(" ", ""))) {
					continue;
				}
				Object fieldType = t.get("FIELD_TYPE");
				String formatData = this.getFormatData(map, fieldType, fieldName);
				map.put(key, formatData);
			}
		}

		return tableDataList;
	}


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
			// 利率
			BigDecimal a = (BigDecimal)headerDataMap.get(fieldName);
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
		Set<String> entrySet = headerDataMap.keySet();
		for (String key:entrySet) {
			if(key.equals("DETIL")){
				System.out.println(key);
			}
			String paramValue = String.valueOf(headerDataMap.get(key));
			if(null == paramValue || "null".equalsIgnoreCase(paramValue)) {
				paramValue = "";
			}
			String pamam = "@"+key+"@";
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

