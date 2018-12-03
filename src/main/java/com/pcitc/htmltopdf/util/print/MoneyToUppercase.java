package com.pcitc.htmltopdf.util.print;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Scanner;

/* 
 * 1. 金额为整数时，只表示整数部分，后面加“整” 
 2. 连续的“0”，只写一个“零” 
 3. 整数后尾数0省略，如100表示成“壹佰元整” 
 4. 四舍五入到分 
 5. 最大范围到千亿(12位) 
 */
public class MoneyToUppercase {
	static HashMap<Integer, String> hm = new HashMap<Integer, String>();

	public static String toUpper(String num) {
		hm.put(0, "零");
		hm.put(1, "壹");
		hm.put(2, "贰");
		hm.put(3, "叁");
		hm.put(4, "肆");
		hm.put(5, "伍");
		hm.put(6, "陆");
		hm.put(7, "柒");
		hm.put(8, "捌");
		hm.put(9, "玖");
		hm.put(10, "拾");
		hm.put(100, "佰");
		hm.put(1000, "仟");
		hm.put(10000, "万");
		String snum = num;
		String intpart = null;
		String decpart = null;
		String dec0 = null;
		String dec1 = null;
		String hasdec = null;

		String[] sa = new String[2];
		// 去掉“,”
		snum = snum.replace(",", "");
		sa = snum.split("\\.");

		intpart = sa[0];
		decpart = sa[1];
		String[] sint = intpart.split(""); // 整数部分
		switch (sint.length) {
		case 2:
			snum = hm.get(Integer.parseInt(sint[1]));
			break;
		case 3:
			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[2]));
			break;
		case 4:
			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[2])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[3]));
			break;
		case 5:
			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[2])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[3])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[4]));
			break;
		case 6:
			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(10000)
					+ hm.get(Integer.parseInt(sint[2])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[3])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[4])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[5]));
			break;
		case 7:
			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[2])) + hm.get(10000)
					+ hm.get(Integer.parseInt(sint[3])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[4])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[5])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[6]));
			break;
		case 8:
			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[2])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[3])) + hm.get(10000)
					+ hm.get(Integer.parseInt(sint[4])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[5])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[6])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[7]));
			break;
		case 9:
			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[2])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[3])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[4])) + hm.get(10000)
					+ hm.get(Integer.parseInt(sint[5])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[6])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[7])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[8]));
			break;
		case 10:
			snum = hm.get(Integer.parseInt(sint[1])) + "亿"
					+ hm.get(Integer.parseInt(sint[2])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[3])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[4])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[5])) + hm.get(10000)
					+ hm.get(Integer.parseInt(sint[6])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[7])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[8])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[9]));
			break;
		case 11:
			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[2])) + "亿"
					+ hm.get(Integer.parseInt(sint[3])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[4])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[5])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[6])) + hm.get(10000)
					+ hm.get(Integer.parseInt(sint[7])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[8])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[9])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[10]));
			break;
		case 12:
			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[2])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[3])) + "亿"
					+ hm.get(Integer.parseInt(sint[4])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[5])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[6])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[7])) + hm.get(10000)
					+ hm.get(Integer.parseInt(sint[8])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[9])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[10])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[11]));
			break;
		case 13:
			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[2])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[3])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[4])) + "亿"
					+ hm.get(Integer.parseInt(sint[5])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[6])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[7])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[8])) + hm.get(10000)
					+ hm.get(Integer.parseInt(sint[9])) + hm.get(1000)
					+ hm.get(Integer.parseInt(sint[10])) + hm.get(100)
					+ hm.get(Integer.parseInt(sint[11])) + hm.get(10)
					+ hm.get(Integer.parseInt(sint[12]));
			break;
		}

		snum += "元";

		snum = snum.replaceAll("零零", "零");
		snum = snum.replaceAll("零仟", "零");
		snum = snum.replaceAll("零零", "零");
		snum = snum.replaceAll("零佰", "零");
		snum = snum.replaceAll("零零", "零");
		snum = snum.replaceAll("零拾", "零");
		snum = snum.replaceAll("零零", "零");
//		snum = snum.replaceAll("零零亿", "亿");
		snum = snum.replaceAll("零亿", "亿");
//		snum = snum.replaceAll("零零万", "万");
		snum = snum.replaceAll("零万", "万");
		snum = snum.replaceAll("亿万", "亿");
//		snum = snum.replaceAll("零零元", "元");
		snum = snum.replaceAll("零元", "元");
		snum = snum.replaceAll("零零", "零");
		
		if (snum.startsWith("元"))
			snum = "零" + snum;
		// System.out.println(snum);
		String[] sdec = decpart.split(""); // 小数部分

		if (sdec[1].equals("0") && sdec[2].equals("0")) {
			hasdec = "整";
			snum += hasdec;
		} else {
			if (sdec[1].equals("0"))
				dec0 = "零";
			else
				dec0 = hm.get(Integer.parseInt(sdec[1])) + "角";

			if (sdec[2].equals("0"))
				dec1 = "";
			else
				dec1 = hm.get(Integer.parseInt(sdec[2])) + "分";

			snum += dec0 + dec1;
		}

		return snum;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Input a number less than 1000000000000:");
		double num = new Scanner(System.in).nextDouble();

		if (num >= 1e12) {
			System.out.println("Out of range!");
		} else if (num < 0)
			System.out.println("Nagative number");
		else {
			String snum = new DecimalFormat("0.00").format(num);// 四舍五入保留两位小数
			System.out.println(snum);
			String result = MoneyToUppercase.toUpper(snum);
			System.out.println(result);
		}
	}

}
