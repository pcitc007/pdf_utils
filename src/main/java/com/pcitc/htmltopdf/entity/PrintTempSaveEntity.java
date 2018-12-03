package com.pcitc.htmltopdf.entity;

/**
 * @author baitao
 * @date 2018/11/30 12:21
 */
public class PrintTempSaveEntity {
	String printTempId;
	String oldId;
	String name;
	String content;
	String containSubTemp;


	public String getPrintTempId() {
		return printTempId;
	}

	public void setPrintTempId(String printTempId) {
		this.printTempId = printTempId;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContainSubTemp() {
		return containSubTemp;
	}

	public void setContainSubTemp(String containSubTemp) {
		this.containSubTemp = containSubTemp;
	}
}
