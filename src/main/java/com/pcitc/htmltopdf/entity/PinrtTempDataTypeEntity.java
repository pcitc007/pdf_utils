package com.pcitc.htmltopdf.entity;
import java.io.Serializable;

public class PinrtTempDataTypeEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**  */
	private String  ID;
			
	/**  */
	private String  TYPEID;
			
	/**  */
	private String  FIELDNAME;
			
	/**  */
	private String  FIELDTYPE;
			
	/**  */
	private String  FIELDSOURCE;
			

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
	public String getFIELDNAME() {
		return FIELDNAME;
	}
	
	public void setFIELDNAME(String FIELDNAME) {
		this.FIELDNAME = FIELDNAME;
	}	
	public String getFIELDTYPE() {
		return FIELDTYPE;
	}
	
	public void setFIELDTYPE(String FIELDTYPE) {
		this.FIELDTYPE = FIELDTYPE;
	}	
	public String getFIELDSOURCE() {
		return FIELDSOURCE;
	}
	
	public void setFIELDSOURCE(String FIELDSOURCE) {
		this.FIELDSOURCE = FIELDSOURCE;
	}	
	// =========getter/setter end=========

}

