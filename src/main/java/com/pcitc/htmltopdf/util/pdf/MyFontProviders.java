//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.pcitc.htmltopdf.util.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;

public class MyFontProviders extends XMLWorkerFontProvider {
  private String ttf;

  public MyFontProviders(String ttf) {
    this.ttf = ttf;
  }

  public Font getFont(String fontName, String encoding, boolean embedded, float size, int style, BaseColor color) {
    BaseFont bf = null;

    try {
      bf = BaseFont.createFont(this.ttf, "Identity-H", false);
    } catch (Exception var9) {
      var9.printStackTrace();
    }

    Font font = new Font(bf, size, style, color);
    font.setColor(color);
    return font;
  }
}
