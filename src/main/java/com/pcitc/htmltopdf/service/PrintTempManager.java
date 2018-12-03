package com.pcitc.htmltopdf.service;

import com.pcitc.htmltopdf.dto.PrintTempDto;

import java.util.List;
import java.util.Map;

public interface PrintTempManager {

	Map<String, Object> getTableHtml(Map<String, Object> paramMap);

	Map<String, Object> getSinglePrintData(Map<String, Object> paramMap) ;

	List<Map<String, Object>> getMorePrintData(List<Map<String, Object>> paramMap) ;

	int delete(String id);

	/**
	 * 查询列表
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> queryMapByPage(String sql, PrintTempDto pagerDto);

	/**
	 * 查询列表
	 * @return List<Map<String, Object>>
	 */
	Map<String, Object> queryList(String id, String name, String pageNo);

	/**
	 * 编辑
	 *
	 * @param printTempId
	 * @return
	 */
	Map<String, Object> edit(String printTempId);

	/**
	 * delSQL 保存和修改
	 * @param printTempId
	 * @param oldId
	 * @param name
	 * @param content
	 * @param containSubTemp
	 * @param sqlContents
	 * @param resultDataTypes
	 * @param fieldNames
	 * @param fieldTypes
	 * @param fieldSources
	 * @return
	 */
  String save(String printTempId, String oldId, String name, String content, String containSubTemp,
              String imgX,
              String imgY,
              String imgName,
              String[] sqlContents, String[] resultDataTypes, String[] fieldNames, String[] fieldTypes, String[] fieldSources);

	/**
	 * 删除sql
	 * @param id
	 * @return
	 */
	int delSQL(String id);
}
