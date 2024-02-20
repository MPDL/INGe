/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.citationmanager.data;

import java.awt.Color;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An instance of the class represents a font style definition
 *
 * @author makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */

public class FontStyle {
  private static final Logger logger = LogManager.getLogger(FontStyle.class);

  private boolean def; // font is default
  private String name; // name of font
  private int fontSize; // font size
  private String fontName; // font TypeFace
  private boolean isBold;
  private boolean isItalic;
  private boolean isUnderline;
  private boolean isStrikeThrough;
  private String pdfFontName; // fontname for PDF
  private String foreColor; // foreground color of the font
  private String backColor; // background color of the font
  private String pdfEncoding; // encoding for PDF
  private String cssClass; // Alternative class name for css
  private boolean isPdfEmbedded;
  private boolean isPdfSimulatedBold;
  private boolean isPdfSimulatedItalic;

  public static final String CSS_CLASS_REPORT_TAG = "\"[span class=\\\"%s\\\"]\"+%s+\"[/span]\"";
  public static final String CSS_CLASS_REGEXP = "\\[span class=&quot;(\\w+?)&quot;\\](.*?)\\[/span\\]";
  public static final String CSS_CLASS_SUBST = "<span class=\"$1\">$2</span>";

  /**
   * Constructor
   */
  public FontStyle() {
    setDefault();
  }

  /**
   * Sets default values for the font TODO: to be moved to properties
   */
  public void setDefault() {
    this.def = false;
    this.name = null;
    this.fontSize = 12;
    this.fontName = "Arial";
    this.isBold = false;
    this.isItalic = false;
    this.isUnderline = false;
    this.isStrikeThrough = false;
    this.pdfFontName = "ARIAL.TTF";
    this.foreColor = "black";
    this.backColor = "white";
    this.pdfEncoding = "Identity-H";
    this.cssClass = "";
    this.isPdfEmbedded = false;
    this.isPdfSimulatedBold = false;
    this.isPdfSimulatedItalic = false;
  }

  /**
   * def setter
   *
   * @param newDef
   */
  public void setDef(boolean newDef) {
    this.def = newDef;
  }

  /**
   * def getter
   *
   * @return def
   */
  public boolean getDef() {
    return this.def;
  }

  /**
   * name setter
   *
   * @param newName
   */
  public void setName(String newName) {
    this.name = newName;
  }

  /**
   * name getter
   *
   * @return name
   */
  public String getName() {
    return this.name;
  }

  /**
   * fontSize setter
   *
   * @param newFontSize
   */
  public void setFontSize(int newFontSize) {
    this.fontSize = 0 <= newFontSize ? newFontSize : this.fontSize;
  }

  /**
   * fontSize getter
   *
   * @return fontSize
   */
  public int getFontSize() {
    return this.fontSize;
  }

  /**
   * fontName setter
   *
   * @param newFontName
   */
  public void setFontName(String newFontName) {
    this.fontName = null != newFontName ? newFontName : this.fontName;
  }

  /**
   * fontName getter
   *
   * @return fontName
   */
  public String getFontName() {
    return this.fontName;
  }

  /**
   * isBold setter
   *
   * @param newIsBold
   */
  public void setIsBold(boolean newIsBold) {
    this.isBold = newIsBold;
  }

  /**
   * isBold getter
   *
   * @return isBold
   */
  public boolean getIsBold() {
    return this.isBold;
  }

  /**
   * isItalic setter
   *
   * @param newIsItalic
   */
  public void setIsItalic(boolean newIsItalic) {
    this.isItalic = newIsItalic;
  }

  /**
   * isItalic getter
   *
   * @return isItalic
   */
  public boolean getIsItalic() {
    return this.isItalic;
  }

  /**
   * isUnderline setter
   *
   * @param newIsUnderline
   */
  public void setIsUnderline(boolean newIsUnderline) {
    this.isUnderline = newIsUnderline;
  }

  /**
   * isUnderline getter
   *
   * @return isUnderline
   */
  public boolean getIsUnderline() {
    return this.isUnderline;
  }

  /**
   * isStrikeThrogh setter
   *
   * @param newIsStrikeThrough
   */
  public void setIsStrikeThrough(boolean newIsStrikeThrough) {
    this.isStrikeThrough = newIsStrikeThrough;
  }

  /**
   * isStrikeThrough getter
   *
   * @return isStrikeThrough
   */
  public boolean getIsStrikeThrough() {
    return this.isStrikeThrough;
  }

  /**
   * pdfFontName setter
   *
   * @param newPdfFontName
   */
  public void setPdfFontName(String newPdfFontName) {
    this.pdfFontName = null != newPdfFontName ? newPdfFontName : this.pdfFontName;
  }

  /**
   * pdfFontName getter
   *
   * @return pdfFontName
   */
  public String getPdfFontName() {
    return this.pdfFontName;
  }

  /**
   * foreColor setter
   *
   * @param newForeColor
   */
  public void setForeColor(String newForeColor) {
    this.foreColor = null != newForeColor ? newForeColor : this.foreColor;
  }

  /**
   * foreColor getter
   *
   * @return foreColor
   */
  public String getForeColor() {
    return this.foreColor;
  }

  /**
   * backColor setter
   *
   * @param newBackColor
   */
  public void setBackColor(String newBackColor) {
    this.backColor = null != newBackColor ? newBackColor : this.backColor;
  }

  /**
   * backColor getter
   *
   * @return backColor
   */
  public String getBackColor() {
    return this.backColor;
  }

  /**
   * @return the isPdfSimulatedBold
   */
  public boolean getIsPdfSimulatedBold() {
    return this.isPdfSimulatedBold;
  }

  /**
   * @param isPdfSimulatedBold the isPdfSimulatedBold to set
   */
  public void setIsPdfSimulatedBold(boolean isPdfSimulatedBold) {
    this.isPdfSimulatedBold = isPdfSimulatedBold;
  }

  /**
   * @return the isPdfSimulatedItalic
   */
  public boolean getIsPdfSimulatedItalic() {
    return this.isPdfSimulatedItalic;
  }

  /**
   * @param isPdfSimulatedItalic the isPdfSimulatedItalic to set
   */
  public void setIsPdfSimulatedItalic(boolean isPdfSimulatedItalic) {
    this.isPdfSimulatedItalic = isPdfSimulatedItalic;
  }

  public Color getBackColorAwt() {
    String bc = this.backColor.toUpperCase();
    return null == bc || bc.trim().isEmpty() || "WHITE".equals(bc) ? Color.WHITE
        : "BLACK".equals(bc) ? Color.BLACK : "RED".equals(bc) ? Color.RED : "BLUE".equals(bc) ? Color.BLUE : Color.WHITE; // default
  }

  public Color getForeColorAwt() {
    String fc = this.foreColor.toUpperCase();
    return null == fc || fc.trim().isEmpty() || "BLACK".equals(fc) ? Color.BLACK
        : "WHITE".equals(fc) ? Color.WHITE : "RED".equals(fc) ? Color.RED : "BLUE".equals(fc) ? Color.BLUE : Color.BLACK; // default
  }

  /**
   * pdfEncoding setter
   *
   * @param newPdfEncoding
   */
  public void setPdfEncoding(String newPdfEncoding) {
    this.pdfEncoding = null != newPdfEncoding ? newPdfEncoding : this.pdfEncoding;
  }

  /**
   * pdfEncoding getter
   *
   * @return pdfEncoding
   */
  public String getPdfEncoding() {
    return this.pdfEncoding;
  }

  /**
   * isPdfEmbedded setter
   *
   * @param newIsPdfEmbedded
   */
  public void setIsPdfEmbedded(boolean newIsPdfEmbedded) {
    this.isPdfEmbedded = newIsPdfEmbedded;
  }

  /**
   * isPdfEmbedded getter
   *
   * @return isPdfEmbedded
   */
  public boolean getIsPdfEmbedded() {
    return this.isPdfEmbedded;
  }


  public String getCssClass() {
    return this.cssClass;
  }

  public void setCssClass(String cssClass) {
    this.cssClass = cssClass;
  }


  public String getStyleAttributes() {
    return " fontName=\\\"" + this.fontName + "\\\"" + " fontSize=\\\"" + this.fontSize + "\\\"" + " isBold=\\\"" + this.isBold + "\\\""
        + " isItalic=\\\"" + this.isItalic + "\\\"" + " isUnderline=\\\"" + this.isUnderline + "\\\"" + " isStrikeThrough=\\\""
        + this.isStrikeThrough + "\\\"" + " pdfFontName=\\\"" + this.pdfFontName + "\\\"" + " forecolor=\\\"" + this.foreColor + "\\\""
        + " backcolor=\\\"" + this.backColor + "\\\"" + " pdfEncoding=\\\"" + this.pdfEncoding + "\\\"" + " isPdfEmbedded=\\\""
        + this.isPdfEmbedded + "\\\"" + " isPdfSimulatedBold=\\\"" + this.isPdfSimulatedBold + "\\\"" + " isPdfSimulatedItalic=\\\""
        + this.isPdfSimulatedItalic + "\\\"";
  }

  /**
   * Creates JasperReport representation of the style If font is default, there is nothing to
   * define, definition will be taken from the JasperReport itself.
   *
   * @return String of the JasperReport font tag. String.format should be used to resolve %s
   */
  public String applyStyle(String expr) {
    return this.def ? expr : "\"<style" + getStyleAttributes() + ">\"+" + expr + "+\"</style>\"";
  }

  /**
   * Adds css class definition to an element
   */

  public String applyCssClass(String expr) {
    return null == this.cssClass || this.cssClass.trim().isEmpty() ? expr : String.format(CSS_CLASS_REPORT_TAG, this.cssClass, expr);
  }

  public String toString() {
    return "[" + this.def + "," + this.name + "," + this.fontSize + "," + this.fontName + "," + this.isBold + "," + this.isItalic + ","
        + this.isUnderline + "," + this.isStrikeThrough + "," + this.pdfFontName + "," + this.foreColor + "," + this.backColor + ","
        + this.pdfEncoding + "," + this.isPdfEmbedded + "," + this.isPdfSimulatedBold + "," + this.isPdfSimulatedItalic + ","
        + this.cssClass + "]";
  }
}
