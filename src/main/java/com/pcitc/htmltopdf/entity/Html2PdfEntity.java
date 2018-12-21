package com.pcitc.htmltopdf.entity;

/**
 * @author baitao
 * @date 2018/11/19 14:22
 */
public class Html2PdfEntity {

	private String htmlStr; 
	private String original; //original 是否计算  calc：计算； nocalc不计算
	private String pageSize;//pageSize 纸张大小 A4、A5
	private String imgName;//imageUrl 印章图片路径
	private String imgX;//imageUrl 印章图片路径
	private String imgY;//imageUrl 印章图片路径
	private String direction;//direction 纸张方向 hx、zx
//	private String imagePosition;//imagePosition 印章位置 zs、zx、ys、yx

	public String getHtmlStr() {
		return htmlStr;
	}

	public Html2PdfEntity() {
	}

	public Html2PdfEntity(String htmlStr, String original, String pageSize, String imgName, String imgX, String imgY, String direction) {
		this.htmlStr = htmlStr;
		this.original = original;
		this.pageSize = pageSize;
		this.imgName = imgName;
		this.imgX = imgX;
		this.imgY = imgY;
		this.direction = direction;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public String getImgX() {
		return imgX;
	}

	public void setImgX(String imgX) {
		this.imgX = imgX;
	}

	public String getImgY() {
		return imgY;
	}

	public void setImgY(String imgY) {
		this.imgY = imgY;
	}

	public void setHtmlStr(String htmlStr) {
		this.htmlStr = htmlStr;
	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
}
