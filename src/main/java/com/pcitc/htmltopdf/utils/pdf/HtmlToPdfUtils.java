package com.pcitc.htmltopdf.utils.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFilesImpl;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.pcitc.htmltopdf.entity.PageSizeEnum;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author baitao
 * @date 2018/11/13 10:21
 */
public class HtmlToPdfUtils {

	private static final Logger logger = LoggerFactory.getLogger(HtmlToPdfUtils.class);
	private static final String THEAD = "thead";
	private static final String TFOOT = "tfoot";
	private static final String TBODY = "tbody";
	private static final String PAGE = "###";
	private static final String SUM = "##";
	private static float[] columnsWidth;
	private static int fontSize = 8; //default
	private static boolean tableHeadStrong = false;


	/**
	 * 不需要印章
	 *
	 * @param htmlStr   html文本
	 * @param calc      是否计算  calc：计算； nocalc不计算
	 * @param pageSize  纸张大小 A4、A5
	 * @param direction 纸张方向 hx 横向、zx 纵向
	 * @param rootPath  pdf的存放目录
	 * @return String 生成的pdf的文件名字
	 */
	public static String htmlFileToPdf(String htmlStr, String calc, String pageSize, String direction, String rootPath) {
		return htmlFileToPdf(htmlStr, calc, pageSize, direction, null, null, rootPath, null);
	}

	/**
	 * 不需要传ttf
	 *
	 * @param htmlStr       html文本
	 * @param calc          是否计算  calc：计算； nocalc不计算
	 * @param pageSize      纸张大小 A4、A5
	 * @param direction     纸张方向 hx 横向、zx 纵向
	 * @param imageUrl      印章图片路径
	 * @param imagePosition 印章位置 zs、zx、ys、yx
	 * @param rootPath      pdf的存放目录
	 * @return String 生成的pdf的文件名字
	 */
	public static String htmlFileToPdf(String htmlStr, String calc, String pageSize, String direction, String imageUrl, String imagePosition, String rootPath) {
		return htmlFileToPdf(htmlStr, calc, pageSize, direction, imageUrl, imagePosition, rootPath, null);
	}


	/**
	 * 可以自定义ttf文件
	 *
	 * @param htmlStr       html文本
	 * @param calc          是否计算  calc：计算； nocalc不计算
	 * @param pageSize      纸张大小 A4、A5
	 * @param direction     纸张方向 hx 横向、zx 纵向
	 * @param imageUrl      印章图片路径
	 * @param imagePosition 印章位置 zs、zx、ys、yx
	 * @param rootPath      pdf的存放目录
	 * @param ttfPath       ttf文件路径
	 * @return String
	 */
	public static String htmlFileToPdf(String htmlStr, String calc, String pageSize, String direction, String imageUrl, String imagePosition, String rootPath, String ttfPath) {
		String pdfName = CommonUtils.getUUID() + ".pdf";
		Rectangle rectangle = PageSizeEnum.getPageSize(pageSize);
		if ("hx".equalsIgnoreCase(direction)) {
			rectangle.rotate();
		}

		if (StringUtils.isEmpty(ttfPath)) {
			ttfPath = "./src/main/resources/static/WeiRuanYaHei-1.ttf";
		}

		PDFBuilder pdfBuilder = new PDFBuilder();
		pdfBuilder.imageUrl = imageUrl;
		pdfBuilder.position = imagePosition;

		if (StringUtils.isNotEmpty(calc) && calc.equalsIgnoreCase("calc")) {
			HtmlToPdfUtils.tableToPdf(htmlStr, rootPath, pdfName, ttfPath, rectangle, pdfBuilder);
		} else {
			HtmlToPdfUtils.htmlStrToPdf(htmlStr, rootPath, pdfName, ttfPath, rectangle, pdfBuilder);
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
	 * @param htmlStr   html字符串
	 * @param pdfPath   pdf文件路径
	 * @param pdfName   pdf文件名字
	 * @param ttfPath   字体文件
	 * @param rectangle 纸张大小
	 */
	private static boolean htmlStrToPdf(String htmlStr, String pdfPath, String pdfName, String ttfPath, Rectangle rectangle, PDFBuilder pdfBuilder) {
		try {
			htmlStr = htmlStrPreProcess(htmlStr);

			Document document = new Document(rectangle);
			PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfPath + File.separator + pdfName));
			pdfWriter.setPageEvent(pdfBuilder);
			document.open();
			XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
			worker.parseXHtml(pdfWriter, document, new ByteArrayInputStream(htmlStr.getBytes(StandardCharsets.UTF_8)), (InputStream) null, new MyFontProvider(ttfPath));
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
		//html 预处理，不闭合标签的处理
		htmlStr = htmlStrPreProcess(htmlStr);

		//jsoup解析
		long startParse = System.currentTimeMillis();
		org.jsoup.nodes.Document document = Jsoup.parse(htmlStr);
		logger.info("解析用了：{}", (System.currentTimeMillis() - startParse));

		//开始pdf
		long startPdf = System.currentTimeMillis();
		Document pdf = new Document(rectangle);

		try {

			//假设只有一个table, 且table的列数全都一样。
			org.jsoup.nodes.Element firstTable = document.getElementsByTag("table").get(0);

			XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
			//解析table之前， 放在每页
			String tableBeforeHtml = getTableBeforeHtmlStr(htmlStr) + "\\\n";
			//解析table之后，放在最后一页, 文档结束再放入
			String tableAfterHtml = document.select("table ~ *").outerHtml();

			org.jsoup.nodes.Element thead = firstTable.getElementsByTag(THEAD).size() > 0 ? firstTable.getElementsByTag(THEAD).get(0) : null;
			org.jsoup.nodes.Element tbody = firstTable.getElementsByTag(TBODY).size() > 0 ? firstTable.getElementsByTag(TBODY).get(0) : null;
			org.jsoup.nodes.Element tfoot = firstTable.getElementsByTag(TFOOT).size() > 0 ? firstTable.getElementsByTag(TFOOT).get(0) : null;

			//样式解析 1.表头字体加粗 2.字体大小
			HtmlToPdfUtils.fontSize = 8;
			HtmlToPdfUtils.tableHeadStrong = false;
			if (null != thead) {
				Elements elements1 = thead.select("tr td strong");
				if (elements1.size() > 0) {
					HtmlToPdfUtils.tableHeadStrong = true;
				}
			}
			Elements elements2 = tbody.select("tr td span");
			if (elements2.size() > 0) {
				int fontSize = cssStrToInt(elements2.get(0).attr("style"), "font-size");
				if (fontSize > 0) {
					HtmlToPdfUtils.fontSize = fontSize;
				}
			}

			PdfWriter pdfWriter = PdfWriter.getInstance(pdf, new FileOutputStream(pdfPath + File.separator + pdfName));
			pdfWriter.setPageEvent(pdfBuilder);
			pdf.open();
			//计算完样式，再创建font。
			Font font = getDefaultFont(ttfPath);

			/* ##每页计算 ###总的结果，tfoot每页都放。出现###的行就放到最后一页*/
			Elements pageFootTrs = null;
			Elements sumFootTrs = null;
			if(null != tfoot){
				pageFootTrs = tfoot.select("tr");
				sumFootTrs = new Elements();
				for(int i = 0; i < pageFootTrs.size(); i++) {
					if(pageFootTrs.get(i).text().contains(SUM)) {
						sumFootTrs.add(pageFootTrs.remove(i));
					}
				}
			}
			logger.info(pageFootTrs.html());
			logger.info(sumFootTrs.html());

			Map<Integer, Float> sumResultMap = getFootSign(sumFootTrs, SUM); //###
			long start1 = System.currentTimeMillis();
			sumResult(firstTable, sumResultMap);
			logger.info("计算汇总花费时间：{}", (System.currentTimeMillis() - start1));

			Map<Integer, Float> pageResultMap = getFootSign(pageFootTrs, "##"); //##

			//如果抛 NPE 就结束。
			int columnNum = tbody.getElementsByTag("tr").get(0).select("td").size();
			columnsWidth = new float[columnNum];
			Elements widthTds = tbody.getElementsByTag("tr").get(0).select("td");
			for (int i = 0; i < widthTds.size(); i++) {
				columnsWidth[i] = Float.parseFloat(widthTds.get(i).attr("width"));
			}
			float bottom = 72F;
			float footHeight = 0F;
			final PdfPTable table = newPdfPTable(columnNum, pdf);
			final PdfPTable headTable = newPdfPTable(columnNum, pdf);
			final PdfPTable bodyTable = newPdfPTable(columnNum, pdf);
			PdfPTable footTable = newPdfPTable(columnNum, pdf);
			editFootTable(pageFootTrs, footTable, pageResultMap, SUM, font);
			footHeight += footTable.calculateHeights();

			if (null != thead) {
				thead.getElementsByTag("tr").iterator().forEachRemaining(element -> {
					Font font1 = getDefaultFont(ttfPath);
					if (tableHeadStrong) {
						font1.setStyle(Font.BOLD);
					}
					putTrInTable(element, headTable, font1);
				});
			}

			//放入每页头部， 表头。自己创建解析器，临时解决太慢
			XMLParser p = selfParseXHtml(pdfWriter, pdf, new ByteArrayInputStream(tableBeforeHtml.getBytes(StandardCharsets.UTF_8)), new MyFontProvider(ttfPath));
			p.parse(new ByteArrayInputStream(tableBeforeHtml.getBytes(StandardCharsets.UTF_8)), Charset.forName("UTF-8"));
			pdf.add(headTable);

			//tbody 计算
			Elements tbodyTrs = tbody.getElementsByTag("tr");
			PdfPTable tempTable = newPdfPTable(columnNum, pdf);
			for (int i = 0; i < tbodyTrs.size(); i++) {
				//生成当前列的table
				Elements tds = tbodyTrs.get(i).select("td");
				for (int j = 0; j < tds.size(); j++) {
					commonCalc(pageResultMap, tds, j);
					tempTable.addCell(new PdfPCell(new Paragraph(tds.get(j).text(), font)));
				}
				tempTable.completeRow();
				float verticalPosition = pdfWriter.getVerticalPosition(true) - bottom - footHeight;
				float tableHeight = tempTable.calculateHeights();
				pdf.add(tempTable);
				tempTable.deleteLastRow();
				if (verticalPosition <= tableHeight || i == (tbodyTrs.size() - 1)) {
					//生成底部table, 只替换 ##
					if (pageResultMap.size() > 0) {
						footTable = newPdfPTable(columnNum, pdf);
						editFootTable(pageFootTrs, footTable, pageResultMap, PAGE, font);
						pdf.add(footTable);
						pageResultMap = getFootSign(pageFootTrs, "##");
					}

					//新建一页
					if (i != (tbodyTrs.size() - 1)) {
						pdf.newPage();
//						放入每页头部，表头
						p.parse(new ByteArrayInputStream(tableBeforeHtml.getBytes(StandardCharsets.UTF_8)), Charset.forName("UTF-8"));
						pdf.add(headTable);
					}
				}
				if (i % 10 == 0) {
					logger.info("第几行：{}", i);
				}
			}
			//放入表后的表格、文字
			if (sumResultMap.size() > 0) {
				footTable = newPdfPTable(columnNum, pdf);
				editFootTable(sumFootTrs, footTable, sumResultMap, SUM, font);
				pdf.add(footTable);
			}
			p.parse(new ByteArrayInputStream(tableAfterHtml.getBytes(StandardCharsets.UTF_8)), Charset.forName("UTF-8"));
			pdf.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;
		}
		logger.info("生成pdf耗时：{}", (System.currentTimeMillis() - startPdf));
		return true;
	}

	public static XMLParser selfParseXHtml(final PdfWriter writer, final Document doc, final InputStream in, final FontProvider fontProvider) throws IOException {
		CssFilesImpl cssFiles = new CssFilesImpl();
		StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver(cssFiles);
		HtmlPipelineContext hpc = new HtmlPipelineContext(new CssAppliersImpl(fontProvider));
		hpc.setAcceptUnknown(true).autoBookmark(true).setTagFactory(Tags.getHtmlTagProcessorFactory()).setResourcesRootPath(null);
		HtmlPipeline htmlPipeline = new HtmlPipeline(hpc, new PdfWriterPipeline(doc, writer));
		Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, htmlPipeline);
		XMLWorker worker = new XMLWorker(pipeline, true);
		Charset charset = Charset.forName("UTF-8");
		XMLParser p = new XMLParser(true, worker, charset);
		return p;
//		p.parse(in, charset);
	}

	private static boolean editFootTable(Elements trs, PdfPTable footTable, Map<Integer, Float> resultMap, String sign, Font font) {
		if (resultMap.size() == 0) return false;
		trs.iterator().forEachRemaining(element -> {
			Elements ktds = element.getElementsByTag("td");
			int k = 0;
			for (org.jsoup.nodes.Element element1 : ktds) {
				if (element1.text().contains(sign) && resultMap.containsKey(k)) {
					footTable.addCell(new PdfPCell(new Paragraph(element1.text().replace(sign, resultMap.get(k).toString()), font)));
				} else {
					footTable.addCell(new PdfPCell(new Paragraph(element1.text(), font)));
				}
				k++;
			}
		});
		return true;
	}

	private static boolean sumResult(org.jsoup.nodes.Element firstTable, Map<Integer, Float> sumResultMap) {
		if(sumResultMap.size() <= 0) {
			return false;
		}
		try {
			org.jsoup.nodes.Element tbody = firstTable.getElementsByTag(TBODY).size() > 0 ? firstTable.getElementsByTag(TBODY).get(0) : null;
			if ( null == tbody)
				return false;
			//tbody 计算
			Elements tbodyTrs = tbody.getElementsByTag("tr");
			for (int i = 0; i < tbodyTrs.size(); i++) {
				//生成当前列的table
				Elements tds = tbodyTrs.get(i).select("td");
				for (int j = 0; j < tds.size(); j++) {
					commonCalc(sumResultMap, tds, j);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return true;
	}

	private static Map<Integer, Float> getFootSign(Elements trs, String sign) {
		Map<Integer, Float> resultMap = new HashMap<>();
		if (null == trs) return resultMap;
		/* ##每页计算 ###总的结果，tfoot每页都放。*/
		for (int i = 0; i < trs.size(); i++) {
			Elements tds = trs.get(i).getElementsByTag("td");
			for (int j = 0; j < tds.size(); j++) {
				org.jsoup.nodes.Element td = tds.get(j);
				if (td.text().contains(sign)) {
					resultMap.put(j, 0F);
				}
			}
		}
		return resultMap;
	}

	private static void commonCalc(Map<Integer, Float> resultMap, Elements tds, int j) {
		try {
			//计算总的结果
			if (resultMap.size() > 0 && resultMap.containsKey(j)) {
				float t = Float.valueOf(tds.get(j).text());
				resultMap.put(j, resultMap.get(j) + t);
			}
		} catch (NumberFormatException e) {
			logger.info(tds.get(j).html());
		}
	}

	private static PdfPCell createPdfPCell(String txt, Font font) {
		return new PdfPCell(new Paragraph(txt, font));
	}

	/**
	 * 获取样式
	 *
	 * @param str  style str
	 * @param name 样式名字
	 * @return
	 */
	private static int cssStrToInt(String str, String name) {
		if (!str.contains(name)) {
			return 0;
		}
//		String s1 = str.substring(0, str.indexOf(name));
		String s2 = str.substring(str.indexOf(name));
		String s3 = s2.substring(0, s2.indexOf(";"));

		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(s3);
		return Integer.parseInt(m.replaceAll(""));
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
		tr.select("td").iterator().forEachRemaining(element -> table.addCell(new Paragraph(element.text(), font)));
	}

	private static PdfPTable newPdfPTable(int columnNum, Document pdf) throws DocumentException {
		PdfPTable table = new PdfPTable(columnNum);
		table.setTotalWidth(pdf.getPageSize().getWidth() - pdf.leftMargin() - pdf.rightMargin());
		table.setWidthPercentage(100F);
		if (columnsWidth != null) {
			table.setWidths(columnsWidth);
		}
		return table;
	}

	private static Font getDefaultFont(String ttfPath) {
		FontFactory.register(ttfPath, "yahei");
//		Font baseFont = FontFactory.getFont("yahei", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//		Font chFont = new Font(bfCN, 12, Font.NORMAL, BaseColor.BLUE);
		return FontFactory.getFont("yahei", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize);
	}

	//预处理
	private static String htmlStrPreProcess(String htmlStr) {
		htmlStr = htmlStr.replaceAll("(<br>|<br/>)", "");
		return htmlStr;
	}

}
