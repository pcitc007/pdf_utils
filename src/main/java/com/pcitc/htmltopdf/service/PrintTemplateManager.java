package com.pcitc.htmltopdf.service;

import com.pcitc.htmltopdf.dto.PrintTempDto;
import com.pcitc.htmltopdf.entity.ImageEntity;
import com.pcitc.htmltopdf.entity.PrintTempEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PrintTemplateManager {

	Map<String, Object> getTableHtml(Map<String, Object> paramMap);

	int delete(String id);

	/**
	 * 查询列表
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> queryMapByPage(String sql, PrintTempDto pagerDto);

	/**
	 * 查询 img列表
	 * @return List<Map<String, Object>>
	 */
	List<ImageEntity> listImage();

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
	 */
	String save(PrintTempEntity printTempEntity);

	/**
	 * 删除sql
	 * @param id
	 * @return
	 */
	int delSQL(String id);

	/**
	 * 文件上传
	 * @author baitao
	 * @date 2018-12-28
	 *
	 * @param file
	 * @param rootPath
	 * @return
	 */
	String upload(MultipartFile file, String rootPath);
}
