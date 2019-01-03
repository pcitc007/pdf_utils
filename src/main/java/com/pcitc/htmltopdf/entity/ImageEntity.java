package com.pcitc.htmltopdf.entity;

/**
 * @author baitao
 * @date 2018/12/26 17:43
 */
public class ImageEntity {

	private String id;
	private String x;
	private String y;
	private String name;
	private String path;
	private String width;
	private String height;

	@Override
	public String toString() {
		return "ImageEntity{" +
						"id=" + id +
						", x=" + x +
						", y=" + y +
						", name='" + name + '\'' +
						", path='" + path + '\'' +
						", width=" + width +
						", height=" + height +
						'}';
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}
}
