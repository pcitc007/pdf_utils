import com.pcitc.htmltopdf.entity.Html2PdfEntity;
import com.pcitc.htmltopdf.service.FileService;
import com.pcitc.htmltopdf.service.impl.FileServiceImpl;
import com.pcitc.htmltopdf.util.pdf.HtmlToPDF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author baitao
 * @date 2018/12/12 9:24
 */
public class TestHtmlToPdf {

	/**
	 * ??? htmlToPdf
	 * @param arts
	 */
	public static void main(String[] arts) {

//		FileService fileService = new FileServiceImpl();
//		Html2PdfEntity html2PdfEntity = new Html2PdfEntity();
//		html2PdfEntity.setHtmlStr();
//		String pdfPath = "";
//		String ttfPath = "";
//		String imgPath = "";
//		String fileName = fileService.html2pdf(html2PdfEntity, pdfPath, ttfPath, imgPath);
//		System.out.println(fileName);
		getPrintHtml();


	}

	public static void getPrintHtml() {
		List<String> list = new ArrayList<String>();
		list.add("<table style=\"border-collapse: collapse;\" width=\"90%\" cellspacing=\"0\" cellpadding=\"0\" bordercolor=\"#000000\" border=\"0\" align=\"center\"><tbody><tr class=\"firstRow\"><td colspan=\"6\" style=\"text-align:center;border: 0px;\"><br/><br/><span style=\"font-family:???;font-size:18px\"><strong>????????????</strong></span><br/><br/></td></tr><tr><td colspan=\"6\" style=\"text-align:center;border: 0px;\">@POST_DT_FORMAT2@<br/></td></tr><tr><td><br/></td><td><br/></td><td><br/></td><td><br/></td><td><br/></td><td><br/></td></tr><tr><td colspan=\"6\" style=\"text-align:right;border-width: 0px;\">?????0182980000004<br/></td></tr><tr><td><br/></td><td><br/></td><td><br/></td><td><br/></td><td><br/></td><td><br/></td></tr><tr><td><br/></td><td><br/></td><td><br/></td><td><br/></td><td><br/></td><td><br/></td></tr><tr><td rowspan=\"3\" style=\"text-align:center;border-width: 2px 1px 1px 2px;width: 3%; border-style: solid;\">?????/td><td style=\"text-align:center;border-width: 2px 1px 1px 1px;width: 12%; border-style: solid;\">???</td><td style=\"text-align:left;border-width: 2px 1px 1px 1px;width: 35%; border-style: solid;\">????????????????????/td><td rowspan=\"3\" style=\"text-align:center;border-width: 2px 1px 1px 1px;width: 3%; border-style: solid;\">?????/td><td style=\"text-align:center;border-width: 2px 1px 1px 1px;width: 17%; border-style: solid;\">???</td><td style=\"text-align:left;border-width: 2px 2px 1px 1px;width: 30%; border-style: solid;\">????????????????????/td></tr><tr><td style=\"text-align:center;border-width: 1px 1px 1px 1px; border-style: solid;\">???</td><td style=\"text-align:left;border-width: 1px 1px 1px 1px; border-style: solid;\">01-01-000050-01</td><td style=\"text-align:center;border-width: 1px 1px 1px 1px; border-style: solid;\">???</td><td style=\"text-align:left;border-width: 1px 2px 1px 1px; border-style: solid;\">TRMB01-00005020180010</td></tr><tr><td style=\"text-align:center;border-width: 1px 1px 1px 1px; border-style: solid;\">??????</td><td style=\"text-align:left;border-width: 1px 1px 1px 1px; border-style: solid;\">???????????????</td><td style=\"text-align:center;border-width: 1px 1px 1px 1px; border-style: solid;\">??????</td><td style=\"text-align:left;border-width: 1px 2px 1px 1px; border-style: solid;\">???????????????</td></tr><tr><td colspan=\"2\" style=\"text-align:center;border-width: 1px 1px 1px 2px; border-style: solid;\">@CURRENCY_CD_NM@??????</td><td colspan=\"3\" style=\"text-align:left;border-width: 1px 0px 1px 1px; border-style: solid;\">????????/td><td style=\"text-align:right;border-width: 0px 2px 1px 0px; border-style: solid;\">@CURRENCY_CD_SYMBOL@@AMOUNT_FORMAT@</td></tr><tr><td colspan=\"2\" style=\"text-align:center;border-width: 1px 1px 2px 2px; border-style: solid;\">???</td><td colspan=\"4\" style=\"text-align:left;border-width: 1px 2px 2px 1px; border-style: solid;\">??????;??????;TRMB01-00005020180010;</td></tr><tr><td colspan=\"4\" style=\"border: 0px;\"><br/></td><td style=\"border: 0px; border-color: black; font-size:12px; border-style: solid;text-align: right;\">[???]admin</td><td style=\"border: 0px; border-color: black; font-size:12px; border-style: solid;text-align: right;\">[???]admin</td></tr></tbody></table>\n");
		System.out.println(list);
//		String pdfName = HtmlToPDF.htmlStrToPdf(list, "A4", null, null, null, null, null, null);
//		System.out.println(pdfName);
	}
}
