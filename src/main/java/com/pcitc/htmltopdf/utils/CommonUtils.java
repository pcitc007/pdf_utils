package com.pcitc.htmltopdf.utils;

import java.util.UUID;

/**
 * @author baitao
 * @date 2018/11/21 11:05
 */
public class CommonUtils {

	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
