package com.pcitc.htmltopdf.entity;

import java.io.Serializable;

public class PrintTempEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String oldId;
	//模板相关
	private String id;
	private String content;
	private String createUser;
	private String createTime;
	private String updateUser;
	private String updateTime;
	private String name;
	//数据类型
	private String[] sqlContent;
	private String[] resultDataType;
	private String[] fieldName;
	private String[] fieldType;
	private String[] fieldSource;

	//pdf
	private String pageNumber;
	private String pageSize;
	private String pageDirection;
	private String pageCalculation;
	//图片信息
	private String imgIdSeal;
	private String imgIdLogo;
	private ImageEntity imgSeal;
	private ImageEntity imgLogo;
	//html
	private String html;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getSqlContent() {
		return sqlContent;
	}

	public void setSqlContent(String[] sqlContent) {
		this.sqlContent = sqlContent;
	}

	public String[] getResultDataType() {
		return resultDataType;
	}

	public void setResultDataType(String[] resultDataType) {
		this.resultDataType = resultDataType;
	}

	public String[] getFieldName() {
		return fieldName;
	}

	public void setFieldName(String[] fieldName) {
		this.fieldName = fieldName;
	}

	public String[] getFieldType() {
		return fieldType;
	}

	public void setFieldType(String[] fieldType) {
		this.fieldType = fieldType;
	}

	public String[] getFieldSource() {
		return fieldSource;
	}

	public void setFieldSource(String[] fieldSource) {
		this.fieldSource = fieldSource;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getPageDirection() {
		return pageDirection;
	}

	public void setPageDirection(String pageDirection) {
		this.pageDirection = pageDirection;
	}

	public String getPageCalculation() {
		return pageCalculation;
	}

	public void setPageCalculation(String pageCalculation) {
		this.pageCalculation = pageCalculation;
	}

	public String getImgIdSeal() {
		return imgIdSeal;
	}

	public void setImgIdSeal(String imgIdSeal) {
		this.imgIdSeal = imgIdSeal;
	}

	public String getImgIdLogo() {
		return imgIdLogo;
	}

	public void setImgIdLogo(String imgIdLogo) {
		this.imgIdLogo = imgIdLogo;
	}

	public ImageEntity getImgSeal() {
		return imgSeal;
	}

	public void setImgSeal(ImageEntity imgSeal) {
		this.imgSeal = imgSeal;
	}

	public ImageEntity getImgLogo() {
		return imgLogo;
	}

	public void setImgLogo(ImageEntity imgLogo) {
		this.imgLogo = imgLogo;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}
}

