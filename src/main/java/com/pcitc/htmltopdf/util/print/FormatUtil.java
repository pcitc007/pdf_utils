package com.pcitc.htmltopdf.util.print;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 格式化数据
 * 
 * @author wjjie
 * 
 */
public class FormatUtil {
	/**
	 * 格式化数值类型数据。保留两位精度。有千分位标识
	 * 
	 * @param obj
	 * @return
	 */
	public static String formateNumberWithPrecision2(Object obj) {
		return formateNumberWithPrecision(obj, 2);
	}



	/**
	 * 格式化数值类型数据。保留四位精度。无千分位标识
	 * 
	 * @param obj
	 * @return
	 */
	public static String formateNumberWithoutThousandth4(Object obj) {
		return formateNumberWithoutThousandth(obj, 4);
	}



	/**
	 * 根据指定的精度，格式化数值类型数据（有千分位标识）。如将2345.567转换为精度为2的字符串2,345.57
	 * 
	 * @param obj
	 * @param precision
	 * @return
	 */
	public static String formateNumberWithPrecision(Object obj, int precision) {
		String formatStr = DataFormatUtil.numberFormatWithPrecision.substring(
				0, DataFormatUtil.numberFormatWithPrecision.indexOf(".") + 1
						+ precision);
		DecimalFormat rateFormat = new DecimalFormat(formatStr);
		return rateFormat.format(obj);
	}

	/**
	 * 根据指定的精度，格式化数值类型数据（无千分位标识）。如将2345.567转换为精度为2的字符串2345.57
	 * 
	 * @param obj
	 * @param precision
	 * @return
	 */
	public static String formateNumberWithoutThousandth(Object obj,
			int precision) {
		String formatStr = DataFormatUtil.numberFormatWithoutThousandth.substring(
				0, DataFormatUtil.numberFormatWithoutThousandth.indexOf(".") + 1
						+ precision);
		DecimalFormat rateFormat = new DecimalFormat(formatStr);
		return rateFormat.format(obj);
	}

	public static void main(String[] arg) {
		BigDecimal temp=new BigDecimal(2.34567);
		System.out.println(formateNumberWithoutThousandth4(temp));
	}
}
