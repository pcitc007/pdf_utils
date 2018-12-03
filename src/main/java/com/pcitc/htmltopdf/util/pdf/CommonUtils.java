//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.pcitc.htmltopdf.util.pdf;

import java.util.UUID;

public class CommonUtils {
	public CommonUtils() {
	}

	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
