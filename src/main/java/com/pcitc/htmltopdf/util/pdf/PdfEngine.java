//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.pcitc.htmltopdf.util.pdf;

import com.pcitc.htmltopdf.entity.PrintTempEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 写个方便调用的类
 */
public class PdfEngine {

	protected String pdfPath = "";
	protected String ttfPath = "";
	protected String imgPath = "";

	public static List<String> htmlToPdfParamResolve1(Map<String, List<PrintTempEntity>> paramMap, String imgPath, String pdfPath, String ttfPath){
		 List<String> pdfList = new ArrayList<String>();
			for(List<PrintTempEntity> v : paramMap.values()) {
				List<PrintTempEntity> single = v;
				PrintTempEntity printTempEntity1 = single.get(0);
				List<String> htmlList = new ArrayList<String>();
				for (PrintTempEntity printTempEntity : single) {
					htmlList.add(printTempEntity.getHtml());
				}
				String pdfStr = HtmlToPDF.htmlStrToPdf(htmlList, printTempEntity1, imgPath, pdfPath, ttfPath);
				pdfList.add(pdfStr);
			}
			return pdfList;
	}

//	public static List<String> tableToPdfParamResolve(Map<String, List<PrintTempEntity>> paramMap,String imgPath,String pdfPath,String ttfPath){
//		List<String> pdfList = new ArrayList<String>();
//		for(List<PrintTempEntity> v : paramMap.values()) {
//			List<PrintTempEntity> single = v;
//			PrintTempEntity printTempEntity1 = single.get(0);
//			List<String> htmlList = new ArrayList<String>();
//			for (PrintTempEntity printTempEntity : single) {
//				htmlList.add(printTempEntity.getHtml());
//			}
//			String pdfStr = HtmlToPDF.htmlStrToPdf(htmlList.get(0), printTempEntity1, imgPath, pdfPath, ttfPath);
//			pdfList.add(pdfStr);
//		}
//		return pdfList;
//	}

//	public static String htmlToPdfParamResolve2(List<PrintTempEntity> printTempEntityList,String imgPath,String pdfPath,String ttfPath){
//		PrintTempEntity printTempEntity1 = printTempEntityList.get(0);
//		List<String> htmlList = new ArrayList<String>();
//		for (PrintTempEntity printTempEntity : printTempEntityList) {
//				htmlList.add(printTempEntity.getHtml());
//		}
//		return HtmlToPDF.htmlStrToPdf(htmlList, printTempEntity1, imgPath, pdfPath, ttfPath);
//	}



}
