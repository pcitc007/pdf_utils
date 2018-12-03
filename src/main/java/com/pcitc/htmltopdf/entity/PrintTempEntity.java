package com.pcitc.htmltopdf.entity;
import java.io.Serializable;

public class PrintTempEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**  */
	private String  id;
			
	/**  */
	private String  content;
	
	/** 其他模板标识(#模板标识1#_#模板标识2#)*/
	private String containSubTemp;
			
	/**  */
	private String  createUser;
			
	/**  */
	private String  createTime;
			
	/**  */
	private String  updateUser;
			
	/**  */
	private String updateTime;
			
	/**  */
	private String  name;
	/**  */
	private String  imgX;
	/**  */
	private String  imgY;
	/**  */
	private String  imgName;

	public String getImgX() {
		return imgX;
	}

	public void setImgX(String imgX) {
		this.imgX = imgX;
	}

	public String getImgY() {
		return imgY;
	}

	public void setImgY(String imgY) {
		this.imgY = imgY;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public String getContainSubTemp() {
		return containSubTemp;
	}

	public void setContainSubTemp(String containSubTemp) {
		this.containSubTemp = containSubTemp;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
			

	// =========getter/setter begin=========
	
	// =========getter/setter end=========

}

