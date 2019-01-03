//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.pcitc.htmltopdf.util.pdf;

import com.pcitc.htmltopdf.entity.PrintTempEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 写个方便调用的类
 */
public class PdfEngine {

	protected String pdfPath = "";
	protected String ttfPath = "";
	protected String imgPath = "";

	public static void commonA4Vertical_preview(Map<String, Object> map){
		String calc = String.valueOf(map.get("calc"));
//		HtmlToPDF.
	}
	public static void commonA4Horizontal_preview(){

	}
	public static void commonA5Vertical_preview(){

	}
	public static void commonA5Horizontal_preview(){

	}
	public static List<String> htmlToPdfParamResolve1(Map<String, List<PrintTempEntity>> paramMap,String imgPath,String pdfPath,String ttfPath){
		 List<String> pdfList = new ArrayList<String>();
			for(List<PrintTempEntity> v : paramMap.values()) {
				List<PrintTempEntity> single = v;
				String pdfStr = htmlToPdfParamResolve2(single, imgPath, pdfPath, ttfPath);
				pdfList.add(pdfStr);
			}
			return pdfList;
	}

	public static String htmlToPdfParamResolve2(List<PrintTempEntity> printTempEntityList,String imgPath,String pdfPath,String ttfPath){
		PrintTempEntity printTempEntity1 = printTempEntityList.get(0);
		List<String> htmlList = new ArrayList<String>();
		for (PrintTempEntity printTempEntity : printTempEntityList) {
			htmlList.add(printTempEntity.getHtml());
		}
		return HtmlToPDF.htmlStrToPdf(htmlList, printTempEntity1, imgPath, pdfPath, ttfPath);
	}



}
