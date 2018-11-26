package com.pcitc.htmltopdf.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;

/**
 * 文件的字体
 * @author baitao
 * @date 2018/11/20 13:36
 */
public class MyFontProviders extends XMLWorkerFontProvider {

  private String ttf; // 字体文件完整路径

  public MyFontProviders(String ttf) {
    super();
    this.ttf = ttf;
  }

  public Font getFont(final String fontName, final String encoding, final boolean embedded, final float size, final int style, final BaseColor color) {
    BaseFont bf = null;
    try {
      bf = BaseFont.createFont(ttf, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Font font = new Font(bf, size, style, color);
    font.setColor(color);
    return font;
  }
}