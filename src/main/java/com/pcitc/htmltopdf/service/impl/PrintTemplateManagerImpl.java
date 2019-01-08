package com.pcitc.htmltopdf.service.impl;

import com.pcitc.htmltopdf.dto.PrintTempDto;
import com.pcitc.htmltopdf.entity.ImageEntity;
import com.pcitc.htmltopdf.entity.PrintTempEntity;
import com.pcitc.htmltopdf.service.PrintTemplateManager;
import com.pcitc.htmltopdf.util.print.DateUtil;
import com.pcitc.htmltopdf.util.print.FormatUtil;
import com.pcitc.htmltopdf.util.print.MoneyToUppercase;
import com.pcitc.htmltopdf.util.print.ReflectUtil;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class PrintTemplateManagerImpl implements PrintTemplateManager {

	private final Logger log = LoggerFactory.getLogger(PrintTemplateManagerImpl.class);

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String FORMAT_TIME = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final String FORMAT_TIME2 = "yyyy年MM月dd日 HH:mm:ss.SSS";

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
	
	@Override
	public Map<String, Object> getTableHtml(Map<String, Object> paramMap){
		// 根据 传来的数据 确定指定模板
		String printTempId = paramMap.get("printTempId").toString();

		// 0.模板数据
		String printSql = String.format("select * from PRINT_TEMP  where ID = '%s' ", printTempId);
		Map<String, Object> printTempMap = jdbcTemplate.queryForMap(printSql);
		if (printTempMap == null || printTempMap.get("content") == null) {
			return null;
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("tableHtml", printTempMap.get("content").toString());
		return resMap ;
	}

	@Override
	public int delete(String id) {
		String sql = " delete from PRINT_TEMP where id = ? ";
		return this.jdbcTemplate.update(sql, id);
	}

	@Override
	public List<Map<String, Object>> queryMapByPage(String sql, PrintTempDto pagerDto) {
		long count = jdbcTemplate.queryForObject("select count(*) from ( " + sql + " ) ", Long.class);
		pagerDto.init(count);
		String pageSql = "SELECT TB_ROWNUM1.* FROM (SELECT TB_ROWNUM.*, ROWNUM RN FROM ("
						+ sql + ") TB_ROWNUM WHERE ROWNUM <= "
						+ (pagerDto.getPageNo() * pagerDto.getPerPage()) + ") TB_ROWNUM1 WHERE RN > "
						+ ((pagerDto.getPageNo() - 1L) * pagerDto.getPerPage());
		return jdbcTemplate.queryForList(pageSql);
	}

	@Override
	public List<ImageEntity> listImage() {
		String sql = " select * from print_image ";
		List<ImageEntity> list = ReflectUtil.listAutoReflect(jdbcTemplate.queryForList(sql), ImageEntity.class);
		return list;
	}

	@Override
	public Map<String, Object> queryList(String id, String name, String pageNo) {
		PrintTempDto dto = new PrintTempDto();
		if (pageNo != null) {
			dto.setPageNo(StringUtils.isBlank(pageNo) ? null : Long.valueOf(pageNo));
		}
		dto.setId(id);
		dto.setName(name);
		dto.setPerPage(10);
		StringBuilder sql = new StringBuilder("select * from PRINT_TEMP where 1=1 ");
		if (StringUtils.isNotBlank(id)) {
			sql.append(" and REGEXP_LIKE(ID, '(" + id + "|" + id.toUpperCase() +")')");
		}
		if (StringUtils.isNotBlank(name)) {
			sql.append(" and REGEXP_LIKE(NAME, '(" + name + "|" + name.toUpperCase() +")')");
//			sql.append(" and NAME like '%").append(name).append("%' ");
		}
		dto.setOrderBy(" create_time desc ");

		List<Map<String, Object>> list = this.queryMapByPage(sql.toString(), dto);
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("list", list);
		resMap.put("_page", dto);

		// 分页所需数据
		// _pager.pageSize _pager.pageNo _pagerUrl
		resMap.put("_pager", dto);
		resMap.put("_pagerUrl", "queryList?pageNo=#pageNo");
		return resMap;
	}

	@Override
	public Map<String, Object> edit(String printTempId) {
		String sql = "select  count(*)  from PRINT_TEMP  where ID like '" + printTempId + "'";
		int count = jdbcTemplate.queryForObject(sql, Integer.class);
		Map<String, Object> resMap = new HashMap<String, Object>();
		if (count > 0) {
			sql = "select * from PRINT_TEMP where ID = '" + printTempId + "' ";
			PrintTempEntity entity = ReflectUtil.autoReflect(jdbcTemplate.queryForMap(sql), PrintTempEntity.class);
			resMap.put("entity", entity);

			// 相关sql
			String sqlContent = "select * from PRINT_TEMP_SQL where PRINT_TEMP_ID = '" + printTempId + "' order by ID ";
			List<Map<String, Object>> sqlContents = jdbcTemplate.queryForList(sqlContent);
			resMap.put("sqlContents", sqlContents);

			// 涉及字段
			String sqlDatatype = "select * from PRINT_TEMP_DATATYPE where PRINT_TEMP_ID = '" + printTempId + "' order by ID ";
			List<Map<String, Object>> sqlDatatypes = jdbcTemplate.queryForList(sqlDatatype);
			resMap.put("sqlDatatypes", sqlDatatypes);

		}

		return resMap;
	}

	@Override
	public String save(PrintTempEntity printTempEntity) {
		if (StringUtils.isNotBlank(printTempEntity.getOldId()) && !(printTempEntity.getOldId().equals(printTempEntity.getId()))) {
			int counter = jdbcTemplate.queryForObject("select count(*) from PRINT_TEMP where id='" + printTempEntity.getId() + "'", Integer.class);
			if (counter > 0) {
				return "模板ID已存在";
			}
		}

		try {
			printTempEntity.setUpdateTime(DateUtil.formatDate(new Date(), FORMAT_TIME));
			printTempEntity.setUpdateUser("");

			//更新 print_temp
			List<Object> list = new ArrayList<Object>();
			list.add(printTempEntity.getId());
			list.add(printTempEntity.getName());
			list.add(printTempEntity.getContent());
			list.add(printTempEntity.getUpdateTime());
			list.add(printTempEntity.getUpdateUser());
			list.add(printTempEntity.getImgIdSeal());
			list.add(printTempEntity.getImgIdLogo());
			list.add(printTempEntity.getPageSize());
			list.add(printTempEntity.getPageNumber());
			list.add(printTempEntity.getPageCalculation());
			list.add(printTempEntity.getPageDirection());
			if (StringUtils.isNotBlank(printTempEntity.getOldId())) {
				jdbcTemplate.update(" update PRINT_TEMP set ID = ?, NAME=?, CONTENT=?,  UPDATE_TIME=?, UPDATE_USER=?,  IMG_ID_SEAL=?, IMG_ID_LOGO=?,  PAGE_SIZE=?, PAGE_NUMBER=?, PAGE_CALCULATION=?, PAGE_DIRECTION=? where id='" + printTempEntity.getOldId() + "'", list.toArray());
			} else {
				list.add(DateUtil.formatDate(new Date(), FORMAT_TIME));
				list.add("");
				jdbcTemplate.update(" insert into PRINT_TEMP (ID , NAME , CONTENT, update_user, update_time, img_id_seal, img_id_logo, page_size, page_number, page_calculation, page_direction, create_time,create_user) values(?,?,?,?,?,?,?,?,?,?,?,?,?)", list.toArray());
			}

			// // 保存 sql
			// 1.取所有sql //sql语句 取结果集类型

			// 更新所有sql, 先删除，再插入(注：如果修改了id，需要用oldId 去删除)
			String printTempSqlDelete;
			if (StringUtils.isNotBlank(printTempEntity.getOldId())) {
				 printTempSqlDelete = "delete from PRINT_TEMP_SQL where PRINT_TEMP_ID = '" + printTempEntity.getOldId() + "' ";
			} else {
				printTempSqlDelete = "delete from PRINT_TEMP_SQL where PRINT_TEMP_ID = '" + printTempEntity.getId() + "' ";
			}
			jdbcTemplate.update(printTempSqlDelete);
			if (printTempEntity.getSqlContent() != null && printTempEntity.getSqlContent().length > 0) {
				for (int i = 0; i < printTempEntity.getSqlContent().length; i++) {
					String printTempSqlInsert = " insert into PRINT_TEMP_SQL(ID, PRINT_TEMP_ID, CONTENT, RESULT_DATA_TYPE) values(?,?,?,?)";
					jdbcTemplate.update(printTempSqlInsert, new Object[]{printTempEntity.getId() + "_" + (i + 1), printTempEntity.getId(), printTempEntity.getSqlContent()[i], printTempEntity.getResultDataType()[i]});
				}
			}

			// 保存 数据类型

			// 更新所有 数据字段信息
			String fieldSqlDelete;
			if(StringUtils.isNotBlank(printTempEntity.getOldId())) {
				fieldSqlDelete = "delete from PRINT_TEMP_DATATYPE where PRINT_TEMP_ID = '" + printTempEntity.getOldId() + "' ";
			} else {
				fieldSqlDelete = "delete from PRINT_TEMP_DATATYPE where PRINT_TEMP_ID = '" + printTempEntity.getId() + "' ";
			}
			jdbcTemplate.update(fieldSqlDelete);
			if (printTempEntity.getFieldName() != null && printTempEntity.getFieldName().length > 0) {
				for (int i = 0; i < printTempEntity.getFieldName().length; i++) {
					//参数中，如果fileSource 长度不够的问题
					
					String fieldSource = "";
					if(printTempEntity.getFieldSource().length > i) {
						fieldSource = printTempEntity.getFieldSource()[i];
					} 
					String printTempSqlInsert = " insert into PRINT_TEMP_DATATYPE(ID, PRINT_TEMP_ID, FIELD_NAME, FIELD_TYPE, FIELD_SOURCE) values(?,?,?,?,?)";
					// 1.保存时 去空;2.转为大写
					jdbcTemplate.update(printTempSqlInsert, new Object[]{printTempEntity.getId() + "_" + (i + 1), printTempEntity.getId(), printTempEntity.getFieldName()[i].replaceAll(" ", "").toUpperCase(), printTempEntity.getFieldType()[i], fieldSource});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "success";
	}

	@Override
	public int delSQL(String id) {
			String sql = " delete from PRINT_TEMP_SQL where id = ? ";
			return this.jdbcTemplate.update(sql, new Object[]{id});
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
			// 日期
			Date t1 = (Date) headerDataMap.get(fieldName);
			t =  DateUtil.convertDateToString(t1);
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
        	String paramValue = headerDataMap.get(key).toString();
        	String pamam = "@"+key+"@";
        	tableHtml = tableHtml.replaceAll(pamam.toUpperCase(), paramValue);
        }
		return tableHtml;
	}
	
	@Override
	public String upload(MultipartFile file, String rootPath) {
        if (!file.isEmpty()) {
            try {
                // 文件存放服务端的位置
                if(null == rootPath) throw new RuntimeException("rootPath is null");
                File dir = new File(rootPath);
                if (!dir.exists())
                    dir.mkdirs();
                // 写文件到服务器
                String fileName = createFileName(file.getOriginalFilename());
                File serverFile = new File(dir.getAbsolutePath() + File.separator + fileName);
                if(fileName.isEmpty()) {
                    fileName = createFileName(file.getOriginalFilename());
                    serverFile = new File(dir.getAbsolutePath() + File.separator + fileName);
                }
                file.transferTo(serverFile);
                return fileName;
            } catch (Exception e) {
            	 throw new RuntimeException("You failed to upload " +  file.getOriginalFilename() + " => " + e.getMessage());
            }
        } else {
        	throw new RuntimeException("You failed to upload " +  file.getOriginalFilename() + " because the file was empty.");
        }
    }
	
	private String createFileName(String originalFilename) {
        return System.currentTimeMillis()/1000 + originalFilename;
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
