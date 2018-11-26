package com.pcitc.htmltopdf.utils;

import com.pcitc.htmltopdf.entity.PageSizeEnum;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

/**
 * @author baitao
 * @date 2018/11/13 10:21
 */
public class HtmlToPDF {

	private static final Logger logger = LoggerFactory.getLogger(HtmlToPDF.class);

	/**
	 * 不需要印章
	 * @param htmlStr html文本
	 * @param calc 是否计算  calc：计算； nocalc不计算
	 * @param pageSize 纸张大小 A4、A5
	 * @param direction 纸张方向 hx 横向、zx 纵向
	 * @param rootPath pdf的存放目录
	 * @return String 生成的pdf的文件名字
	 */
	public static String htmlFileToPdf(String htmlStr, String calc, String  pageSize, String direction,  String rootPath) {
		return htmlFileToPdf(htmlStr, calc,  pageSize, direction, null, null, rootPath, null);
	}

	/**
	 * 不需要传ttf
	 * @param htmlStr html文本
	 * @param calc 是否计算  calc：计算； nocalc不计算
	 * @param pageSize 纸张大小 A4、A5
	 * @param direction 纸张方向 hx 横向、zx 纵向
	 * @param imageUrl 印章图片路径
	 * @param imagePosition 印章位置 zs、zx、ys、yx
	 * @param rootPath pdf的存放目录
	 * @return String 生成的pdf的文件名字
	 */
	public static String htmlFileToPdf(String htmlStr, String calc, String  pageSize, String direction, String imageUrl, String imagePosition, String rootPath) {
		return htmlFileToPdf(htmlStr, calc,  pageSize, direction, imageUrl, imagePosition, rootPath, null);
	}


	/**
	 * 可以自定义ttf文件
	 * @param htmlStr html文本
	 * @param calc 是否计算  calc：计算； nocalc不计算
	 * @param pageSize 纸张大小 A4、A5
	 * @param direction 纸张方向 hx 横向、zx 纵向
	 * @param imageUrl 印章图片路径
	 * @param imagePosition 印章位置 zs、zx、ys、yx
	 * @param rootPath pdf的存放目录
	 * @param ttfPath ttf文件路径
	 * @return String
	 */
	public static String htmlFileToPdf(String htmlStr, String calc, String  pageSize, String direction, String imageUrl, String imagePosition, String rootPath, String ttfPath) {
		String pdfName = CommonUtils.getUUID() + ".pdf";
		Rectangle rectangle = PageSizeEnum.getPageSize(pageSize);
		if("hx".equalsIgnoreCase(direction)) {
			rectangle.rotate();
		}

		if(StringUtils.isEmpty(ttfPath)) {
			ttfPath = "./src/main/resources/static/WeiRuanYaHei-1.ttf";
		}

		PDFBuilder pdfBuilder = new PDFBuilder();
		pdfBuilder.imageUrl = imageUrl;
		pdfBuilder.position = imagePosition;

		if (StringUtils.isNotEmpty(calc) && calc.equalsIgnoreCase("calc")) {
			HtmlToPDF.tableToPdf(htmlStr, rootPath, pdfName, ttfPath, rectangle, pdfBuilder);
		} else {
			HtmlToPDF.htmlStrToPdf(htmlStr, rootPath, pdfName, ttfPath, rectangle, pdfBuilder);
		}
		return pdfName;
	}

	/**
	 * html 转换 pdf， 针对不规则表格。
	 *
	 * @param txt       html文件路径
	 * @param path      pdf文件路径
	 * @param pdfName   pdf文件名字
	 * @param ttf       字体文件
	 * @param rectangle 纸张大小
	 */
	private static boolean htmlFileToPdf(String txt, String path, String pdfName, String ttf, Rectangle rectangle, PDFBuilder pdfBuilder) {
		StringBuilder inputStr = new StringBuilder();
		try (FileReader reader = new FileReader(txt);
		     BufferedReader br = new BufferedReader(reader)) {
			String temStr;
			while ((temStr = br.readLine()) != null) {
				inputStr.append(temStr);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return htmlStrToPdf(inputStr.toString(), path, pdfName, ttf, rectangle, pdfBuilder);
	}

	/**
	 * html 转换 pdf， 针对不规则表格。
	 *
	 * @param htmlStr       html字符串
	 * @param pdfPath   pdf文件路径
	 * @param pdfName   pdf文件名字
	 * @param ttfPath   字体文件
	 * @param rectangle 纸张大小
	 */
	private static boolean htmlStrToPdf(String htmlStr,  String pdfPath, String pdfName, String ttfPath, Rectangle rectangle, PDFBuilder pdfBuilder) {
		try {
			htmlStr = htmlStrPreProcess(htmlStr);

			Document document = new Document(rectangle);
			PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfPath + File.separator + pdfName));
			pdfWriter.setPageEvent(pdfBuilder);
			document.open();
			XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
			worker.parseXHtml(pdfWriter, document, new ByteArrayInputStream(htmlStr.getBytes(StandardCharsets.UTF_8)), (InputStream) null, new MyFontProviders(ttfPath));
			document.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 规则的table转换pdf，针对报表
	 *
	 * @param htmlStr   html文本
	 * @param pdfPath   pdf文件生成的路径
	 * @param pdfName   pdf文件名称
	 * @param ttfPath   字体样式
	 * @param rectangle 纸张类型
	 * @return boolean 是否生成成功
	 */
	private static boolean tableToPdf(String htmlStr, String pdfPath, String pdfName, String ttfPath, Rectangle rectangle, PDFBuilder pdfBuilder) {

		htmlStr = htmlStrPreProcess(htmlStr);

		long startParse = System.currentTimeMillis();
		org.jsoup.nodes.Document document = Jsoup.parse(htmlStr);

		logger.info("解析用了：{}", (System.currentTimeMillis() - startParse));

		long startPdf = System.currentTimeMillis();
		com.itextpdf.text.Document pdf = new com.itextpdf.text.Document(rectangle);
		Font font = getDefaultFont(ttfPath);

		try {
			PdfWriter pdfWriter = PdfWriter.getInstance(pdf, new FileOutputStream(pdfPath + File.separator + pdfName));
			pdfWriter.setPageEvent(pdfBuilder);
			pdf.open();

			//假设只有一个table, 且table的列数全都一样。
			org.jsoup.nodes.Element firstTable = document.getElementsByTag("table").get(0);

			//解析table之前， 放在每页
			String tableBeforeHtml = getTableBeforeHtmlStr(htmlStr) + "\\\n";
			XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

			//解析table之后，放在最后一页, 文档结束再放入
			String tableAfterHtml = document.select("table ~ *").outerHtml();

			Elements trsALL = firstTable.select("tr");
			Elements trsBody = trsALL;
			org.jsoup.nodes.Element firstTr = trsALL.first();
			org.jsoup.nodes.Element pageTr = null;
			org.jsoup.nodes.Element sumTr = null;
			trsBody.remove(firstTr);

			/*尝试解析最后两行 ##每页计算 ###总的结果
			* 如果有###就把哪一行作为 汇总
			* */
			org.jsoup.nodes.Element last1Tr = trsALL.get(trsALL.size()-1);
			org.jsoup.nodes.Element last2Tr = trsALL.get(trsALL.size() - 2);
			for (org.jsoup.nodes.Element element : last1Tr.select("td")) {
				if(element.html().contains("###")) {
					sumTr = last1Tr;
					break;
				} else if(element.html().contains("##")) {
					pageTr = last1Tr;
					break;
				}
			}
			for (org.jsoup.nodes.Element element : last2Tr.select("td")) {
				if(element.html().contains("###")) {
					sumTr = last2Tr;
					break;
				} else if(element.html().contains("##")) {
					pageTr = last2Tr;
					break;
				}
			}
			if(pageTr != null && (pageTr.equals(last1Tr)|| pageTr.equals(last2Tr))) {
				trsBody.remove(pageTr);
			}
			if(sumTr != null && (sumTr.equals(last1Tr)|| sumTr.equals(last2Tr))) {
				trsBody.remove(sumTr);
			}
			List<String> lastHtmlList = new ArrayList<>();
			List<String> sumHtmlList = new ArrayList<>();
			if (pageTr != null)
				pageTr.select("td").iterator().forEachRemaining(element -> lastHtmlList.add(element.html()));
			if (sumTr != null)
				sumTr.select("td").iterator().forEachRemaining(element -> sumHtmlList.add(element.html()));

	    Map<Integer, Float> sumResultMap = new HashMap<>();
			Map<Integer, Float> pageResultMap = new HashMap<>();

			for (int i = 0; i < lastHtmlList.size(); i++) {
				if (!"".equalsIgnoreCase(lastHtmlList.get(i)) && lastHtmlList.get(i).contains("##")) {
					pageResultMap.put(i, 0F);
				}
			}
			for (int i = 0; i < sumHtmlList.size(); i++) {
				if (!"".equalsIgnoreCase(sumHtmlList.get(i)) && sumHtmlList.get(i).contains("###")) {
					sumResultMap.put(i, 0F);
				}
			}

			int columnNum = firstTr.select("td").size();
			float bottom = 72F;
			PdfPTable table;
			PdfPTable headTable = newPdfPTable(columnNum, pdf);
			PdfPTable footerTable = newPdfPTable(columnNum, pdf);

			//放入每页头部， 表头
			worker.parseXHtml(pdfWriter, pdf, new ByteArrayInputStream(tableBeforeHtml.getBytes(StandardCharsets.UTF_8)), (InputStream) null, new MyFontProviders(ttfPath));
			putTrInTable(firstTr, headTable, font);
			pdf.add(headTable);

			for (int i = 0; i < trsBody.size(); i++) {

				//生成当前列的table
				Elements tds = trsBody.get(i).select("td");
				table = newPdfPTable(columnNum, pdf);
				for (int j = 0; j < tds.size(); j++) {
					//计算每页结果
					calc(pageTr, pageResultMap, tds, j);
					//计算总的结果
					calc(sumTr, sumResultMap, tds, j);

					table.addCell(new PdfPCell(new Paragraph(tds.get(j).html(), font)));
				}
				float verticalPosition = pdfWriter.getVerticalPosition(true) - bottom;
				float tableHeight = table.calculateHeights();
				pdf.add(table);
				if (verticalPosition <= tableHeight || i == (trsBody.size() - 1)) {
					//生成底部table
					List<String> tempTdHtmlList = new ArrayList<>(lastHtmlList);
					pageResultMap.forEach((key, value) -> tempTdHtmlList.set(key, tempTdHtmlList.get(key).replace("##", String.valueOf(value))));
					tempTdHtmlList.forEach(s -> footerTable.addCell(new Paragraph(s, font)));
					pageResultMap.forEach((key, value) -> pageResultMap.put(key, 0F));
					pdf.add(footerTable);
					footerTable.deleteLastRow();

					//新建一页
					if (i != (trsBody.size() - 1)) {
						pdf.newPage();

						//放入每页头部，表头
						worker.parseXHtml(pdfWriter, pdf, new ByteArrayInputStream(tableBeforeHtml.getBytes(StandardCharsets.UTF_8)), (InputStream) null, new MyFontProviders(ttfPath));
						pdf.add(headTable);
					}
				}
				if(i%10 == 0) {
					logger.info("第几行：{}", i);
				}
			}
			//生成总的结果的table
			if(sumResultMap.size() > 0) {
				PdfPTable sumTable = newPdfPTable(columnNum, pdf);
				List<String> tempSumTdHtmlList = new ArrayList<>(sumHtmlList);
				sumResultMap.forEach((key, value) -> tempSumTdHtmlList.set(key, tempSumTdHtmlList.get(key).replace("###", String.valueOf(value))));
				tempSumTdHtmlList.forEach(s -> sumTable.addCell(new Paragraph(s, font)));
				pdf.add(sumTable);
			}
			//放入表后的文字
			worker.parseXHtml(pdfWriter, pdf, new ByteArrayInputStream(tableAfterHtml.getBytes(StandardCharsets.UTF_8)), (InputStream) null, new MyFontProviders(ttfPath));

			pdf.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;
		}
		logger.info("生成pdf耗时：{}", (System.currentTimeMillis() - startPdf));
		return true;
	}

	private static void calc(org.jsoup.nodes.Element pageTr, Map<Integer, Float> pageResultMap, Elements tds, int j) {
		if (pageTr != null && pageResultMap.containsKey(j)) {
			try {
				float t = Float.valueOf(tds.get(j).html());
				pageResultMap.put(j, pageResultMap.get(j) + t);
			} catch (NumberFormatException e) {
				logger.info(tds.get(j).html());
			}
		}
	}

	private static String getTableBeforeHtmlStr(String htmlStr) {
		org.jsoup.nodes.Document tempDocument = Jsoup.parse(htmlStr);
		org.jsoup.nodes.Element table = tempDocument.select("table").get(0);
		Elements tableAfter = tempDocument.select("table ~ *");
		table.remove();
		tableAfter.remove();
		Elements tableBefore = tempDocument.select("body");
		return tableBefore.html();
	}

	private static void putTrInTable(org.jsoup.nodes.Element tr, PdfPTable table, Font font) {
		tr.select("td").iterator().forEachRemaining(element -> table.addCell(new Paragraph(element.html(), font)));
	}

	private static PdfPTable newPdfPTable(int columnNum, Document pdf) {
		PdfPTable table = new PdfPTable(columnNum);
//		table.setTotalWidth(
//						(PageSize.A4.getWidth() - pdf.leftMargin() - pdf.rightMargin())
//										* table.getWidthPercentage()
//										/ 100);
		table.setTotalWidth(PageSize.A4.getWidth() - pdf.leftMargin() - pdf.rightMargin());
		table.setWidthPercentage(100F);
		return table;
	}

	private static Font getDefaultFont(String ttfPath) {
		FontFactory.register(ttfPath, "yahei");
//		Font baseFont = FontFactory.getFont("yahei", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//		Font chFont = new Font(bfCN, 12, Font.NORMAL, BaseColor.BLUE);
		return FontFactory.getFont("yahei", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16);
	}

	//预处理
	private static String htmlStrPreProcess(String htmlStr) {
		htmlStr = htmlStr.replaceAll("(<br>|<br/>)", "");
		return htmlStr;
	}

}
