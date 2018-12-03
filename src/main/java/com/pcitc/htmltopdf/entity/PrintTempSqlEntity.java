package com.pcitc.htmltopdf.entity;
import java.io.Serializable;

public class PrintTempSqlEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**  */
	private String  ID;
			
	/**  */
	private String  TYPEID;
			
	/**  */
	private String  CONTENT;
			

	// =========getter/setter begin=========
	public String getID() {
		return ID;
	}
	
	public void setID(String ID) {
		this.ID = ID;
	}	
	public String getTYPEID() {
		return TYPEID;
	}
	
	public void setTYPEID(String TYPEID) {
		this.TYPEID = TYPEID;
	}	
	public String getCONTENT() {
		return CONTENT;
	}
	
	public void setCONTENT(String CONTENT) {
		this.CONTENT = CONTENT;
	}	
	// =========getter/setter end=========
	
}

