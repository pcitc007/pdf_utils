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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlToPDF {
	private static final Logger logger = LoggerFactory.getLogger(HtmlToPDF.class);
	private static final String THEAD = "thead";
	private static final String TFOOT = "tfoot";
	private static final String TBODY = "tbody";
	private static final String PAGE = "###";
	private static final String SUM = "##";
	private static float[] columnsWidth;
	private static Map<Integer, String> columnAlign;
	private static int fontSize = 8;
	private static boolean tableHeadStrong = false;

	/**
	 * 不需要印章， ttf默认 ./src/main/resources/static/WeiRuanYaHei-1.ttf
	 * @param htmlStr
	 * @param calc
	 * @param pageSize
	 * @param direction
	 * @param rootPath
	 * @return
	 */
	public static String htmlFileToPdf(String htmlStr, String calc, String pageSize, String direction, String rootPath, String ttfPath) {
		return htmlFileToPdf(htmlStr, calc, pageSize, direction, null, null, null, rootPath, ttfPath);
	}

	/**
	 *需要印章和位置， ttf默认 ./src/main/resources/static/WeiRuanYaHei-1.ttf
	 * @param htmlStr
	 * @param calc
	 * @param pageSize
	 * @param direction
	 * @param imgName
	 * @param imgX
	 * @param imgY
	 * @param rootPath
	 * @param ttfPath
	 * @return
	 */
	public static String htmlFileToPdf(String htmlStr, String calc, String pageSize, String direction, String imgName, String imgX, String imgY, String rootPath, String ttfPath) {
		String pdfName = CommonUtils.getUUID() + ".pdf";
		Rectangle rectangle = PageSizeEnum.getPageSize(pageSize);
		if ("hx".equalsIgnoreCase(direction)) {
			rectangle.rotate();
		}

		if (StringUtils.isBlank(ttfPath)) {
			ttfPath = "./src/main/resources/static/WeiRuanYaHei-1.ttf";
		}

		PDFBuilder pdfBuilder = new PDFBuilder();
		pdfBuilder.imgName = imgName;
		pdfBuilder.imgX = imgX;
		pdfBuilder.imgY = imgY;
		if (StringUtils.isNotBlank(calc) && calc.equalsIgnoreCase("calc")) {
			tableToPdf(htmlStr, rootPath, pdfName, ttfPath, rectangle, pdfBuilder);
		} else {
			htmlStrToPdf(htmlStr, rootPath, pdfName, ttfPath, rectangle, pdfBuilder);
		}

		return pdfName;
	}

	private static boolean htmlFileToPdf(String txt, String path, String pdfName, String ttf, Rectangle rectangle, PDFBuilder pdfBuilder) {
		StringBuilder inputStr = new StringBuilder();
		FileReader reader = null;
		try {
			reader = new FileReader(txt);
			BufferedReader br = new BufferedReader(reader);
			String temStr;
			while((temStr = br.readLine()) != null) {
				inputStr.append(temStr);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return htmlStrToPdf(inputStr.toString(), path, pdfName, ttf, rectangle, pdfBuilder);
	}

	private static boolean htmlStrToPdf(String htmlStr, String pdfPath, String pdfName, String ttfPath, Rectangle rectangle, PDFBuilder pdfBuilder) {
		try {
			htmlStr = htmlStrPreProcess(htmlStr);
			Document document = new Document(rectangle);
			PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfPath + File.separator + pdfName));
			pdfWriter.setPageEvent(pdfBuilder);
			document.open();
			XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
			worker.parseXHtml(pdfWriter, document, new ByteArrayInputStream(htmlStr.getBytes(Charset.forName("UTF-8"))), (InputStream)null, new MyFontProviders(ttfPath));
			document.close();
			return true;
		} catch (Exception var9) {
			logger.error(var9.getMessage());
			return false;
		}
	}

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
		} catch (Exception var40) {
			var40.printStackTrace();
			logger.error(var40.getMessage());
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
		XMLWorker worker = new XMLWorker(pipeline, true);
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

	private static void sumResult(Element firstTable, Map<Integer, BigDecimal> sumResultMap) {
		if (sumResultMap.size() > 0) {
			try {
				Element tbody = firstTable.getElementsByTag("tbody").size() > 0 ? (Element)firstTable.getElementsByTag("tbody").get(0) : null;
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
			} catch (Exception var8) {
				logger.error(var8.getMessage());
			}

		}
	}

	//获得需要计算的列
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

	private static String getTableBeforeHtmlStr(String htmlStr) {
		org.jsoup.nodes.Document tempDocument = Jsoup.parse(htmlStr);
		Element table = tempDocument.select("table").get(0);
		Elements tableAfter = tempDocument.select("table ~ *");
		table.remove();
		tableAfter.remove();
		Elements tableBefore = tempDocument.select("body");
		return tableBefore.html();
	}

	private static void putTrInTable(Element tr, PdfPTable table, Font font) {
		for (Element element : tr.select("td")) {
			table.addCell(new Paragraph(element.text(), font));
		}
	}

	private static PdfPTable newPdfPTable(int columnNum, Document pdf) throws DocumentException {
		PdfPTable table = new PdfPTable(columnNum);
		table.setTotalWidth(pdf.getPageSize().getWidth() - pdf.leftMargin() - pdf.rightMargin());
		table.setWidthPercentage(100.0F);
		if (columnsWidth != null) {
			table.setWidths(columnsWidth);
		}

		return table;
	}

	private static Font getDefaultFont(String ttfPath) {
		FontFactory.register(ttfPath, "yahei");
		return FontFactory.getFont("yahei", "Identity-H", true, (float)fontSize);
	}

	private static String htmlStrPreProcess(String htmlStr) {
		htmlStr = htmlStr.replaceAll("(<br>|<br/>)", "");
		return htmlStr;
	}
}
