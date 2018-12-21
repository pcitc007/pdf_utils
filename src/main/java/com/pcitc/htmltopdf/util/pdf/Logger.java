package com.pcitc.htmltopdf.util.pdf;

/**
 * @author baitao
 * @date 2018/12/12 10:15
 */
public class Logger {

	public void info(String s) {
		System.out.println(s);
	}
	public void info(String s, Object ... strings) {
		for (Object str : strings) {
			s = s.replace("{}", String.valueOf(str));
		}
		System.out.println(s);
	}
	public void error(String s) {
		System.out.println(s);
	}
}
