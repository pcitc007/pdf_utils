package com.pcitc.htmltopdf.entity;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

/**
 * @author baitao
 * @date 2018/11/14 10:43
 */
public enum PageSizeEnum {
	A4("A4", PageSize.A4),
	A5("A5", PageSize.A5);

	String code;
	Rectangle pageSize;

	PageSizeEnum(String code, Rectangle pageSize) {
		this.code = code;
		this.pageSize = pageSize;
	}

	public static Rectangle getPageSize(String code) {
    for (PageSizeEnum value : values()) {
	    if (code.equalsIgnoreCase(value.code)) {
	    	return value.pageSize;
	    }
    }
    return A4.pageSize;
	}
}
