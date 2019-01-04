//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.pcitc.htmltopdf.util.pdf;

import com.itextpdf.text.*;
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
import com.pcitc.htmltopdf.entity.PrintTempEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlToPDF {
//	private static final Logger logger = LoggerFactory.getLogger(HtmlToPDF.class);
	private static final Logger logger = new Logger();
	private static final String THEAD = "thead";
	private static final String TFOOT = "tfoot";
	private static final String TBODY = "tbody";
	private static final String PAGE = "###";
	private static final String SUM = "##";
//	private static final String COMMON_STYLE = "<style type=\"text/css\"> * {font-size: 12px;} table {border-color=\"#000000\"; width: 100%;border-collapse: collapse;border: 1px solid black;border-style: solid;border-width: 2px 1px 1px 2px}  td, th {padding: 5px; border-style: solid;border-width: 0 1px 1px 0;}</style>";
	private static final String COMMON_STYLE = "<style type=\"text/css\">" +
			"* {font-size: 13px;}  table {width: 100%;border-collapse: collapse;border: 1px solid black;border-style: solid;border-width: 2px 1px 1px 2px;}  td, th {padding: 7px 0px;border: 0px solid black;border-style: solid;border-width: 0 1px 1px 0;}  table#thead, table#thead td, table#thead th, table#tfoot, table#tfoot td, table#tfoot th {border: 0;}" +
			"</style>";
	private static float[] columnsWidth;
	private static Map<Integer, String> columnAlign;
	private static int fontSize = 8;
	private static boolean tableHeadStrong = false;

	/**
	 *
	 * @param htmlStr
	 * @param printTempEntity
	 * @param imgPath
	 * @param pdfPath
	 * @param ttfPath
	 * @return
	 */
	public static String htmlStrToPdf(String htmlStr, PrintTempEntity printTempEntity, String imgPath, String pdfPath, String ttfPath) {
		String pdfName = CommonUtils.getUUID() + ".pdf";
		Rectangle rectangle = PageSizeEnum.getPageSize(printTempEntity.getPageSize());
		if ("1".equals(printTempEntity.getPageDirection())) {
			rectangle.rotate();
		}

		PDFBuilder pdfBuilder = new PDFBuilder();
//		if (StringUtils.isNotBlank(calc) && calc.equalsIgnoreCase("calc")) {
//			tableToPdf(htmlStr, rootPath, pdfName, ttfPath, rectangle, pdfBuilder);
//		} else {
//			htmlStrToPdf(htmlStr, rootPath, pdfName, ttfPath, rectangle, pdfBuilder);
//		}

		return pdfName;
	}

	/**
	 * 多种业务的打印，
	 * 每个html三个table，头、体、尾
	 */
	public static String htmlStrToPdf(List<String> htmlStrList, PrintTempEntity printTempEntity, String imgPath, String pdfPath, String ttfPath) {
		System.out.println(printTempEntity.toString());
		String pdfName = CommonUtils.getUUID() + ".pdf";
		Rectangle rectangle = PageSizeEnum.getPageSize(printTempEntity.getPageSize());
		if ("1".equals(printTempEntity.getPageDirection())) {
			rectangle = rectangle.rotate();
		}

		PDFBuilder pdfBuilder = new PDFBuilder();
		pdfBuilder.printTempEntity = printTempEntity;
		pdfBuilder.imgPath = imgPath;
		if("0".equals(printTempEntity.getPageCalculation())) {
			htmlToPdfListHtml(htmlStrList, pdfPath, pdfName, ttfPath, rectangle, pdfBuilder);
		} else if("1".equals(printTempEntity.getPageCalculation())) {
			tableToPdf(htmlStrList.get(0), pdfPath, pdfName, ttfPath, rectangle, pdfBuilder);
		}
		return pdfName;
	}

	/**
	 * 多业务
	 * @param htmlStrList
	 * @param pdfPath
	 * @param pdfName
	 * @param ttfPath
	 * @param rectangle
	 * @param pdfBuilder
	 * @return
	 */
	private static boolean htmlToPdfListHtml(List<String> htmlStrList, String pdfPath, String pdfName, String ttfPath, Rectangle rectangle, PDFBuilder pdfBuilder) {
		try {
			Document document = new Document(rectangle);
			document.setMargins(18, 18, 54, 36);

			PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfPath + File.separator + pdfName));
			pdfWriter.setPageEvent(pdfBuilder);
			document.open();

			XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

			for (int i = 0; i < htmlStrList.size(); i++) {
				String htmlStr = htmlStrList.get(i);
				htmlStr = htmlStrPreProcess(htmlStr);

				org.jsoup.nodes.Document document1 = Jsoup.parse(htmlStr);
				document1.outputSettings(new org.jsoup.nodes.Document.OutputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml));


				
				worker.parseXHtml(pdfWriter, document, new ByteArrayInputStream(document1.outerHtml().getBytes()), (InputStream)null, new MyFontProviders(ttfPath));
				if((i+1) < htmlStrList.size()) {
					document.newPage();
				}
			}

			document.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return true;
	}

//	/**
//	 * 列表不计算
//	 * @param htmlStr
//	 * @param pdfPath
//	 * @param pdfName
//	 * @param ttfPath
//	 * @param rectangle
//	 * @param pdfBuilder
//	 * @return
//	 */
//	private static boolean htmlStrToPdf(String htmlStr, String pdfPath, String pdfName, String ttfPath, Rectangle rectangle, PDFBuilder pdfBuilder) {
//		try {
//			htmlStr = htmlStrPreProcess(htmlStr);
//			Document document = new Document(rectangle);
//			PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfPath + File.separator + pdfName));
//			pdfWriter.setPageEvent(pdfBuilder);
//			document.open();
//			XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
//			worker.parseXHtml(pdfWriter, document, new ByteArrayInputStream(htmlStr.getBytes(Charset.forName("UTF-8"))), (InputStream)null, new MyFontProviders(ttfPath));
//			document.close();
//			return true;
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return false;
//		}
//	}

	/**
	 * 列表计算
	 * @param htmlStr
	 * @param pdfPath
	 * @param pdfName
	 * @param ttfPath
	 * @param rectangle
	 * @param pdfBuilder
	 * @return
	 */
	private static boolean tableToPdf(String htmlStr, String pdfPath, String pdfName, String ttfPath, Rectangle rectangle, PDFBuilder pdfBuilder) {

		// html  预处理
		htmlStr = htmlStrPreProcess(htmlStr);

		//jsoup html 解析
		long startParse = System.currentTimeMillis();
		org.jsoup.nodes.Document document = Jsoup.parse(htmlStr);
		logger.info("解析用了：{}", System.currentTimeMillis() - startParse);
		long startPdf = System.currentTimeMillis();

		Document pdf = new Document(rectangle);
		try {

			Element firstTable = document.getElementsByTag("table").get(0);
			String tableBeforeHtml = getTableBeforeHtmlStr(htmlStr) + "\\\n";
			String tableAfterHtml = document.select("table ~ *").outerHtml();
			Element thead = firstTable.getElementsByTag("thead").size() > 0 ? firstTable.getElementsByTag("thead").get(0) : null;
			Element tbody = firstTable.getElementsByTag("tbody").size() > 0 ? firstTable.getElementsByTag("tbody").get(0) : null;
			Element tfoot = firstTable.getElementsByTag("tfoot").size() > 0 ? firstTable.getElementsByTag("tfoot").get(0) : null;

			//重置默认参数
			fontSize = 8; // 字体大小
			tableHeadStrong = false; // 表头加粗

			Elements elements2;
			if (null != thead) {
				elements2 = thead.select("tr td strong");
				if (elements2.size() > 0) {
					tableHeadStrong = true;
				}
			}

			//解析字体大小
			elements2 = tbody.select("tr td span");
			if (elements2.size() > 0) {
				int fontSize = cssStrToInt(elements2.get(0).attr("style"), "font-size");
				if (fontSize > 0) {
					HtmlToPDF.fontSize = fontSize;
				}
			}

			PdfWriter pdfWriter = PdfWriter.getInstance(pdf, new FileOutputStream(pdfPath + File.separator + pdfName));
			pdfWriter.setPageEvent(pdfBuilder);
			pdf.open();
			//默认字体样式，表头需要加粗需要再获取样式，并修改。
			Font defaultFont = getDefaultFont(ttfPath);

			//获得需要汇总的列，每页计算的列
			Elements pageFootTrs = null;
			Elements sumFootTrs = null;
			if (null != tfoot) {
				pageFootTrs = tfoot.select("tr");
				sumFootTrs = new Elements();

				for(int i = 0; i < pageFootTrs.size(); ++i) {
					if (pageFootTrs.get(i).text().contains("###")) {
						sumFootTrs.add(pageFootTrs.remove(i));
					}
				}
			}

			//计算汇总结果
			Map<Integer, BigDecimal> sumResultMap = getFootSign(sumFootTrs, "###");
			long start1 = System.currentTimeMillis();
			sumResult(firstTable, sumResultMap);
			logger.info("计算汇总花费时间：{}", System.currentTimeMillis() - start1);
			Map<Integer, BigDecimal> pageResultMap = getFootSign(pageFootTrs, "##");

			//宽度比例、局左中右解析
			int columnNum = tbody.getElementsByTag("tr").get(0).select("td").size();
			columnsWidth = new float[columnNum];
			columnAlign = new HashMap<Integer, String>();
			Elements widthTds = tbody.getElementsByTag("tr").get(0).select("td");
			for(int i = 0; i < widthTds.size(); ++i) {
				columnsWidth[i] = Float.parseFloat(widthTds.get(i).attr("width"));
				columnAlign.put(i, widthTds.get(i).attr("align"));
			}

			float bottom = 72.0F;
			float footHeight = 0.0F;
			PdfPTable headTable = newPdfPTable(columnNum, pdf);
			PdfPTable footTable = newPdfPTable(columnNum, pdf);

			//计算底部高度
			editFootTable(pageFootTrs, footTable, pageResultMap, "##", defaultFont);
			footHeight += footTable.calculateHeights();

			if (null != thead) {
				for (Element element : thead.getElementsByTag("tr")) {
					Font tempFont = getDefaultFont(ttfPath);
					if (tableHeadStrong) {
						tempFont.setStyle(1);
					}
					putTrInTable(element, headTable, tempFont);
				}
			}

			//获取xml解析对象
			XMLParser p = selfParseXHtml(pdfWriter, pdf, new MyFontProviders(ttfPath));
			p.parse(new ByteArrayInputStream(tableBeforeHtml.getBytes(Charset.forName("UTF-8"))), Charset.forName("UTF-8"));
			pdf.add(headTable);
			Elements tbodyTrs = tbody.getElementsByTag("tr");
			PdfPTable tempTable = newPdfPTable(columnNum, pdf);

			for(int i = 0; i < tbodyTrs.size(); ++i) {
				//生成tbody每一行
				Elements tds = tbodyTrs.get(i).select("td");
				for(int j = 0; j < tds.size(); ++j) {
					commonCalc(pageResultMap, tds, j);
					tempTable.addCell(createPdfPCell(tds.get(j).text(), j, defaultFont));
				}
				tempTable.completeRow();

				//判断是否超出，如果查出，把新行放入下一页。可用高度 - bottom - footHeight
				float verticalPosition = pdfWriter.getVerticalPosition(true) - bottom - footHeight;
				float tableHeight = tempTable.calculateHeights();
				if (verticalPosition <= tableHeight || i == tbodyTrs.size() - 1) {
					//每页结束、放入每页计算结果
					if (pageResultMap.size() > 0) {
						footTable = newPdfPTable(columnNum, pdf);
						editFootTable(pageFootTrs, footTable, pageResultMap, "##", defaultFont);
						pdf.add(footTable);
						pageResultMap = getFootSign(pageFootTrs, "##");
					}

					//如果不是最后一页，新建一页、放入头部、放入超出的那行
					if (i != tbodyTrs.size() - 1) {
						pdf.newPage();
						p.parse(new ByteArrayInputStream(tableBeforeHtml.getBytes(Charset.forName("UTF-8"))), Charset.forName("UTF-8"));
						pdf.add(headTable);

						pdf.add(tempTable);
						tempTable.deleteLastRow();
					}
				} else {
					pdf.add(tempTable);
					tempTable.deleteLastRow();
				}

				if (i % 10 == 0) {
					logger.info("第几行：{}", i);
				}
			}

			//放入汇总数据
			if (sumResultMap.size() > 0) {
				footTable = newPdfPTable(columnNum, pdf);
				editFootTable(sumFootTrs, footTable, sumResultMap, "###", defaultFont);
				pdf.add(footTable);
			}

			p.parse(new ByteArrayInputStream(tableAfterHtml.getBytes(Charset.forName("UTF-8"))), Charset.forName("UTF-8"));
			pdf.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;
		}

		logger.info("生成pdf耗时：{}", System.currentTimeMillis() - startPdf);
		return true;
	}

	private static PdfPCell createPdfPCell(String txt, int index, Font font) {
		PdfPCell pdfPCell = new PdfPCell(new Paragraph(txt, font));
		String align = columnAlign.get(index);
		if (null != align) {
			if ("left".equals(align)) {
				pdfPCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
			} else if ("center".equals(align)) {
				pdfPCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
			} else if ("right".equals(align)) {
				pdfPCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
			} else {
				pdfPCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
			}
		}
		return pdfPCell;
	}

	private static XMLParser selfParseXHtml(PdfWriter writer, Document doc, FontProvider fontProvider) {
		CssFilesImpl cssFiles = new CssFilesImpl();
		StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver(cssFiles);
		HtmlPipelineContext hpc = new HtmlPipelineContext(new CssAppliersImpl(fontProvider));
		hpc.setAcceptUnknown(true).autoBookmark(true).setTagFactory(Tags.getHtmlTagProcessorFactory()).setResourcesRootPath((String)null);
		HtmlPipeline htmlPipeline = new HtmlPipeline(hpc, new PdfWriterPipeline(doc, writer));
		Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, htmlPipeline);
		XMLWorker worker = new XMLWorker(pipeline, false);
		Charset charset = Charset.forName("UTF-8");
		return new XMLParser(true, worker, charset);
	}

	private static void editFootTable(Elements trs, PdfPTable footTable, Map<Integer, BigDecimal> resultMap, String sign, Font font) {
		if (resultMap.size() != 0) {
			for (Element element : trs) {
				Elements ktds = element.getElementsByTag("td");
				int k = 0;
				for (Element element1 : ktds) {
					if (element1.text().contains(sign) && resultMap.containsKey(k)) {
						footTable.addCell(createPdfPCell(element1.text().replace(sign, resultMap.get(k).toString()), k, font));
					} else {
						footTable.addCell(createPdfPCell(element1.text(), k, font));
					}
				}
			}

		}
	}

	/**
	 * 计算总的结果
	 * @param firstTable
	 * @param sumResultMap
	 */
	private static void sumResult(Element firstTable, Map<Integer, BigDecimal> sumResultMap) {
		if (sumResultMap.size() > 0) {
			try {
				Element tbody = firstTable.getElementsByTag("tbody").size() > 0 ? firstTable.getElementsByTag("tbody").get(0) : null;
				if (null == tbody) {
					return;
				}

				Elements tbodyTrs = tbody.getElementsByTag("tr");
				for (Element element : tbodyTrs) {
					Elements tds = element.select("td");
					for(int j = 0; j < tds.size(); ++j) {
						commonCalc(sumResultMap, tds, j);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}
	}

	/**
	 * 获得需要计算的列
	 * @param trs
	 * @param sign
	 * @return
	 */
	private static Map<Integer, BigDecimal> getFootSign(Elements trs, String sign) {
		Map<Integer, BigDecimal> resultMap = new HashMap<Integer, BigDecimal>();
		if (null == trs) {
			return resultMap;
		} else {
			for (Element element : trs) {
				Elements tds = element.getElementsByTag("td");
				for(int j = 0; j < tds.size(); ++j) {
					Element td = tds.get(j);
					if (td.text().contains(sign)) {
						resultMap.put(j, BigDecimal.ZERO);
					}
				}
			}
			return resultMap;
		}
	}

	/**
	 * 公共计算，主要是每页的计算 和 总共的计算。
	 * @param resultMap
	 * @param tds
	 * @param j
	 */
	private static void commonCalc(Map<Integer, BigDecimal> resultMap, Elements tds, int j) {
		try {
			if (resultMap.size() > 0 && resultMap.containsKey(j)) {
				BigDecimal t = BigDecimal.valueOf(Double.parseDouble(tds.get(j).text()));
				resultMap.put(j, resultMap.get(j).add(t));
			}
		} catch (NumberFormatException var4) {
			logger.error(var4.getMessage());
		}

	}

	/**
	 * 获得 style 中指定样式的数值
	 * @param str
	 * @param name
	 * @return
	 */
	private static int cssStrToInt(String str, String name) {
		if (!str.contains(name)) {
			return 0;
		} else {
			String s2 = str.substring(str.indexOf(name));
			String s3 = s2.substring(0, s2.indexOf(";"));
			String regEx = "[^0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(s3);
			return Integer.parseInt(m.replaceAll(""));
		}
	}

	/**
	 * 获得 table 之前的html
	 * @param htmlStr
	 * @return
	 */
	private static String getTableBeforeHtmlStr(String htmlStr) {
		org.jsoup.nodes.Document tempDocument = Jsoup.parse(htmlStr);
		Element table = tempDocument.select("table").get(0);
		Elements tableAfter = tempDocument.select("table ~ *");
		table.remove();
		tableAfter.remove();
		Elements tableBefore = tempDocument.select("body");
		return tableBefore.html();
	}

	/**
	 * 把一行 tr 放入 table
	 * @param tr
	 * @param table
	 * @param font
	 */
	private static void putTrInTable(Element tr, PdfPTable table, Font font) {
		for (Element element : tr.select("td")) {
			table.addCell(new Paragraph(element.text(), font));
		}
	}

	/**
	 * 创建新的 table
	 * @param columnNum
	 * @param pdf
	 * @return
	 * @throws DocumentException
	 */
	private static PdfPTable newPdfPTable(int columnNum, Document pdf) throws DocumentException {
		PdfPTable table = new PdfPTable(columnNum);
		table.setTotalWidth(pdf.getPageSize().getWidth() - pdf.leftMargin() - pdf.rightMargin());
		table.setWidthPercentage(100.0F);
		if (columnsWidth != null) {
			table.setWidths(columnsWidth);
		}
		return table;
	}

	/**
	 * 生成默认字体
	 * @param ttfPath
	 * @return
	 */
	private static Font getDefaultFont(String ttfPath) {
		FontFactory.register(ttfPath, "yahei");
		return FontFactory.getFont("yahei", "Identity-H", true, (float)fontSize);
	}

	/**
	 * 处理html中的特殊字符
	 * @param htmlStr
	 * @return
	 */
	private static String htmlStrPreProcess(String htmlStr) {
		//去除ueditor生成的<br/>
		htmlStr = htmlStr.replaceAll("(<br>|<br/>)", "");
		//追加公共样式
		htmlStr = htmlStr.replaceAll("border(-width)?:[0-9px ]*;", "");
		htmlStr = htmlStr.replaceAll("border=\"[0-9a-zA-Z]*\" ", "");
		htmlStr = htmlStr.replaceAll("border=0", "");
		htmlStr = COMMON_STYLE + htmlStr;
		return htmlStr;
	}
}
