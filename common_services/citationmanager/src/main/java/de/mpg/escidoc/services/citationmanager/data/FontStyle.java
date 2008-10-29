/*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.citationmanager.data;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;


/**
 * An instance of the class represents 
 * a font style definition
 * 
 * @author makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */

public class FontStyle implements Cloneable{

    private static final Logger logger = Logger.getLogger(FontStyle.class);
	
    private boolean def;			//	font is default   
    private String name;			//  name of font     
    private int fontSize;			//  font size      
    private String fontName;		//  font TypeFace 
    private boolean isBold;			   
    private boolean isItalic;
    private boolean isUnderline;
    private boolean isStrikeThrough;
    private String pdfFontName; 	//fontname for PDF
    private String foreColor;		//foreground color of the font
    private String backColor;		//background color of the font
    private String pdfEncoding; 	//encoding for PDF
    private String cssClass;		//Alternative class name for css
    private boolean isPdfEmbedded;  
    
    public static final String CSS_CLASS_REPORT_TAG = "\"&lt;span class=\\\"%s\\\"&gt;\"+%s+\"&lt;/span&gt;\"";
    public static final String CSS_CLASS_REGEXP = "&lt;span class=&quot;(\\w+?)&quot;&gt;(.*?)&lt;/span&gt;";
    public static final String CSS_CLASS_SUBST = "<span class=\"$1\">$2</span>";
    
    /**
     * Constructor
     */
    public FontStyle() {
    	setDefault();
    }
    
    /**
     * Sets default values for the font  
     * TODO: to be moved to properties
     */
    public void setDefault() {
        def = false;   
        name = null;   
        fontSize = 12;
        fontName = "Arial";
        isBold = false;
        isItalic = false;
        isUnderline = false;
        isStrikeThrough = false;
        pdfFontName = "ARIAL.TTF";
        foreColor = "black";
        backColor = "white";
        pdfEncoding = "Identity-H";
        cssClass = "";
        isPdfEmbedded = false;
    }
     
    /**
     * def setter
     * @param newDef
     */
    public void setDef( boolean newDef ) { def = newDef; }
    /**
     * def getter
     * @return def
     */
    public boolean getDef() { return def; }

    /**
     * name setter
     * @param newName
     */
    public void setName( String newName ) { name = newName; }
    /**
     * name getter
     * @return name
     */
    public String getName() { return name; }

    /**
     * fontSize setter
     * @param newFontSize
     */
    public void setFontSize( int newFontSize ) { fontSize = newFontSize >= 0 ? newFontSize : fontSize; }
    /**
     * fontSize getter
     * @return fontSize 
     */
    public int getFontSize() { return fontSize; }

    /**
     * fontName setter
     * @param newFontName
     */
    public void setFontName( String newFontName ) { fontName = newFontName != null ? newFontName : fontName; }
    /**
     * fontName getter
     * @return fontName
     */
    public String getFontName() { return fontName; }

    /**
     * isBold setter
     * @param newIsBold
     */
    public void setIsBold( boolean newIsBold ) { isBold = newIsBold; }
    /**
     * isBold getter
     * @return isBold
     */
    public boolean getIsBold() { return isBold; }

    /**
     * isItalic setter
     * @param newIsItalic
     */
    public void setIsItalic( boolean newIsItalic ) { isItalic = newIsItalic; }
    /**
     * isItalic getter
     * @return isItalic
     */
    public boolean getIsItalic() { return isItalic; }

    /**
     * isUnderline setter
     * @param newIsUnderline
     */
    public void setIsUnderline( boolean newIsUnderline ) { isUnderline = newIsUnderline; }
    /**
     * isUnderline getter
     * @return isUnderline
     */
    public boolean getIsUnderline() { return isUnderline; }

    /**
     * isStrikeThrogh setter
     * @param newIsStrikeThrough
     */
    public void setIsStrikeThrough( boolean newIsStrikeThrough ) { isStrikeThrough = newIsStrikeThrough; }
    /**
     * isStrikeThrough getter
     * @return isStrikeThrough
     */
    public boolean getIsStrikeThrough() { return isStrikeThrough; }

    /**
     * pdfFontName setter
     * @param newPdfFontName
     */
    public void setPdfFontName( String newPdfFontName ) { pdfFontName = newPdfFontName != null ? newPdfFontName : pdfFontName; }
    /**
     * pdfFontName getter
     * @return pdfFontName
     */
    public String getPdfFontName() { return pdfFontName; }

    /**
     * foreColor setter
     * @param newForeColor
     */
    public void setForeColor( String newForeColor ) { foreColor = newForeColor != null ? newForeColor : foreColor; }
    /**
     * foreColor getter
     * @return foreColor
     */
    public String getForeColor() { return foreColor; }

    /**
     * backColor setter
     * @param newBackColor
     */
    public void setBackColor( String newBackColor ) { backColor = newBackColor != null ? newBackColor : backColor; }
    /**
     * backColor getter
     * @return backColor
     */
    public String getBackColor() { return backColor; }

    /**
     * pdfEncoding setter
     * @param newPdfEncoding
     */
    public void setPdfEncoding( String newPdfEncoding ) { pdfEncoding = newPdfEncoding != null ? newPdfEncoding : pdfEncoding; }
    /**
     * pdfEncoding getter
     * @return pdfEncoding
     */
    public String getPdfEncoding() { return pdfEncoding; }

    /**
     * isPdfEmbedded setter
     * @param newIsPdfEmbedded
     */
    public void setIsPdfEmbedded( boolean newIsPdfEmbedded ) { isPdfEmbedded = newIsPdfEmbedded; }
    /**
     * isPdfEmbedded getter
     * @return isPdfEmbedded
     */
    public boolean getIsPdfEmbedded() { return isPdfEmbedded; }

    public String getCssClass() {
		return cssClass;
	}

    public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
	
	

	public Object clone() {
        Object clone = null;
        try {
          clone = super.clone();
        } catch(CloneNotSupportedException e) {
          // should never happen
        }

        ((FontStyle)clone).setFontName( getFontName() );

        return clone;
    }

 
    /**
     * Creates JasperReport representation of the style 
     * If font is default, there is nothing to define, definition will be taken
     * from the JasperReport itself. 
     * @return String of the JasperReport font tag. String.format should 
     * be used to resolve %s  
     */
    public String applyStyle(String expr) {
        return
        	def ?
        			expr :
		            "\"<style" +
		            " fontName=\\\"" + fontName + "\\\"" +
		            " fontSize=\\\"" + fontSize + "\\\"" +
		            " isBold=\\\"" + isBold + "\\\"" +
		            " isItalic=\\\"" + isItalic + "\\\"" +
		            " isUnderline=\\\"" + isUnderline + "\\\"" +
		            " isStrikeThrough=\\\"" + isStrikeThrough + "\\\"" +
		            " pdfFontName=\\\"" + pdfFontName + "\\\"" +
		            " forecolor=\\\"" + foreColor + "\\\"" +
		            " backcolor=\\\"" + backColor + "\\\"" +
		            " pdfEncoding=\\\"" + pdfEncoding + "\\\"" +
		            " isPdfEmbedded=\\\"" + isPdfEmbedded + "\\\"" +
	//	            " cssClass=\\\"" + cssClass + "\\\"" +
		            ">\"+" + expr + "+\"</style>\"";
	    }

    /**
     * Adds css class definition to an element  
     */
    
    public String applyCssClass(String expr) {
    	
    	return
    		this.cssClass==null || this.cssClass.trim().equals("") ?
    				expr : String.format(CSS_CLASS_REPORT_TAG, this.cssClass, expr);
//    			expr :
//    				"\"&lt;span" +
//    				" class=\\\"" + cssClass + "\\\"" +
//    				"&gt;\"" + expr + "\"&lt;/span&gt;\"";
    }    
    
    public String toString() {
        return "["+ def + "," + name + "," + fontSize + "," + fontName + "," + isBold + "," +
                isItalic + "," + isUnderline + "," + isStrikeThrough + "," + pdfFontName + "," +
                foreColor + "," + backColor + "," + pdfEncoding + "," + isPdfEmbedded + "," +
                cssClass + "]";
    }


    public static void main(String[] args) {

        FontStyle fs = new FontStyle();
        logger.info("Default FontStyle:" + fs);

        FontStyle fsclone = (FontStyle)fs.clone();
        logger.info("clone:" + fsclone);

        fsclone.setName("----StyleName----");

        logger.info("Default FontStyle:" + fs);
        logger.info("clone:" + fsclone);
        logger.info("toStyle:" + fsclone.applyStyle("style"));
        fsclone.setCssClass("TestCssClass");
        logger.info("CssClass:" + fsclone.applyCssClass("kuku"));

    }

}
