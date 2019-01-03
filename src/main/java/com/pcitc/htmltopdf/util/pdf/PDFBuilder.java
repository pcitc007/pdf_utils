package com.pcitc.htmltopdf.util.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.pcitc.htmltopdf.entity.ImageEntity;
import com.pcitc.htmltopdf.entity.PrintTempEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class PDFBuilder extends PdfPageEventHelper {
	public String header = "";
	public int presentFontSize = 12;
	public Rectangle pageSize;
	public PdfTemplate total;
	public BaseFont bf;
	public Font fontDetail;
	public int distance;
	//seal、logo
	public PrintTempEntity printTempEntity;
	public String imgPath;

	public PDFBuilder() {
		this.pageSize = PageSize.A4;
		this.bf = null;
		this.fontDetail = null;
		this.distance = 50;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setPresentFontSize(int presentFontSize) {
		this.presentFontSize = presentFontSize;
	}

	public void onOpenDocument(PdfWriter writer, Document document) {
		this.total = writer.getDirectContent().createTemplate(50.0F, 50.0F);
	}

	public void onEndPage(PdfWriter writer, Document document) {
		//页码
		if("1".equals(this.printTempEntity.getPageNumber())) {
			this.addPage(writer, document);
		}
		//印章
		ImageEntity seal = this.printTempEntity.getImgSeal();
		if (null != seal)  {
			this.addImg(document, seal);
		}
		//logo
		ImageEntity logo = this.printTempEntity.getImgLogo();
		if (null != logo) {
			this.addImg(document, logo);
		}

	}

	private void addImg(Document document, ImageEntity imageEntity) {
		try {
			Image image = Image.getInstance(this.imgPath + File.separator + imageEntity.getPath());
			//解决拉伸的问题
			if(Float.parseFloat(imageEntity.getWidth()) == 0 || Float.parseFloat(imageEntity.getHeight()) == 0) {
				float scalePercentage = 24.0F;
				image.scalePercent(scalePercentage, scalePercentage);
			} else {
				image.scaleAbsolute(Float.parseFloat(imageEntity.getWidth()), Float.parseFloat(imageEntity.getHeight()));
			}
			//设置位置
			image.setAbsolutePosition(Float.parseFloat(imageEntity.getX()), Float.parseFloat(imageEntity.getY()));
			document.add(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addPage(PdfWriter writer, Document document) {
		try {
			if (this.bf == null) {
				this.bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", false);
			}
			if (this.fontDetail == null) {
				this.fontDetail = new Font(this.bf, (float)this.presentFontSize, 0);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ColumnText.showTextAligned(writer.getDirectContent(), 0, new Phrase(this.header, this.fontDetail), document.left(), document.top() + 20.0F, 0.0F);
		int pageS = writer.getPageNumber();
		String foot1 = "第 " + pageS + " 页 /共";
		Phrase footer = new Phrase(foot1, this.fontDetail);
		float len = this.bf.getWidthPoint(foot1, (float)this.presentFontSize);
		PdfContentByte cb = writer.getDirectContent();
		ColumnText.showTextAligned(cb, 1, footer, (document.rightMargin() + document.right() + document.leftMargin() - document.left() - len) / 2.0F + 20.0F, document.bottom() - 20.0F, 0.0F);
		cb.addTemplate(this.total, (document.rightMargin() + document.right() + document.leftMargin() - document.left()) / 2.0F + 20.0F, document.bottom() - 20.0F);
	}

	public void addWatermark(PdfWriter writer) {
		File file = new File("./src/main/resources/img/123.jpg");

		try {
			Image image = Image.getInstance(String.valueOf(URI.create(file.getPath())));
			PdfContentByte content = writer.getDirectContentUnder();
			content.beginText();

			for(int k = 0; k < 5; ++k) {
				for(int j = 0; j < 4; ++j) {
					image.setAbsolutePosition((float)(150 * j), (float)(170 * k));
					content.addImage(image);
				}
			}

			content.endText();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onCloseDocument(PdfWriter writer, Document document) {
		if("1".equals(this.printTempEntity.getPageNumber())) {
			this.total.beginText();
			this.total.setFontAndSize(this.bf, (float)this.presentFontSize);
			String foot2 = " " + writer.getPageNumber() + " 页";
			this.total.showText(foot2);
			this.total.endText();
			this.total.closePath();
		}
	}
}
