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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * An instance of this class represents a Collection of {@link FontStyle}s
 *
 * @author vmakarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class FontStylesCollection {

  private String name; // Name of collection
  private List<FontStyle> fontStyles; // List of font styles
  private FontStyle defaultFontStyle; // default font stlye

  // Default FONTSTYLE_NAME (hardcoded). Should be taken from from defaultFontStyle.getName()
  public static final String DEFAULT_FONTSTYLE_NAME = "NORMAL";

  // Hash for the quick search of Fonts with the Name of Style
  private final HashMap<String, FontStyle> namesMap = new HashMap<>();

  // Hash for the quick search of Fonts with the css Name of Style
  private final HashMap<String, FontStyle> cssMap = new HashMap<>();

  public FontStylesCollection() {
    setDefault();
  }

  public void setDefault() {
    setName("Default");
    setFontStyles(new ArrayList<>());
    setDefaultFontStyle(null);
  }

  public void setName(String newName) {
    name = newName;
  }

  public String getName() {
    return name;
  }

  public List<FontStyle> getFontStyles() {
    return fontStyles;
  }

  public void setFontStyles(List<FontStyle> fontStyles) {
    this.fontStyles = fontStyles;
  }

  public void addFontStyle(FontStyle fs) {
    String name = fs.getName();
    if (namesMap.containsKey(name)) {
      return;
    }
    namesMap.put(name, fs);

    fontStyles.add(fs);

    String cssn = fs.getCssClass();
    if (StringUtils.isNotEmpty(StringUtils.strip(cssn))) {
      cssMap.put(cssn, fs);
    }
  }

  public void setDefaultFontStyle(FontStyle defaultFontStyle) {
    this.defaultFontStyle = defaultFontStyle;
  }

  public FontStyle getDefaultFontStyle() {
    return defaultFontStyle;
  }

  public void findDefaultFontStyle() {
    for (FontStyle fs : fontStyles) {
      if (fs.getDef()) {
        setDefaultFontStyle(fs);
        break;
      }
    }
  }

  public FontStyle getFontStyleByName(String name) {
    return StringUtils.isNotEmpty(StringUtils.strip(name)) ? namesMap.get(name) : null;
  }

  public FontStyle getFontStyleByCssClass(String name) {
    return StringUtils.isNotEmpty(StringUtils.strip(name)) ? cssMap.get(name) : null;
  }

  public void removeCssClass() {
    for (FontStyle fs : fontStyles) {
      fs.setCssClass(null);
    }
  }

  public String toString() {
    String str = "";
    int i = 0;
    for (FontStyle fs : fontStyles) {
      i++;
      str += i + ")" + fs + "\n";
    }
    return str + "\nDefault font:" + defaultFontStyle;
  }

  /**
   * Loads from inputStream
   *
   * @param inputStream
   * @return
   * @throws IOException
   * @throws SAXException
   */
  public static FontStylesCollection loadFromXml(InputStream inputStream) throws IOException, SAXException {
    Digester digester = new Digester();
    digester.setValidating(false);

    String path = "font-styles-collection";
    digester.addObjectCreate(path, FontStylesCollection.class.getName());

    path += "/font-style";
    digester.addObjectCreate(path, FontStyle.class.getName());

    digester.addSetProperties(path);
    digester.addSetProperties(path, "font-size", "fontSize");
    digester.addSetProperties(path, "font-name", "fontName");
    digester.addSetProperties(path, "is-bold", "isBold");
    digester.addSetProperties(path, "is-italic", "isItalic");
    digester.addSetProperties(path, "is-underline", "isUnderline");
    digester.addSetProperties(path, "is-strike-through", "isStrikeThrough");
    digester.addSetProperties(path, "pdf-font-name", "pdfFontName");
    digester.addSetProperties(path, "is-underline", "isUnderline");
    digester.addSetProperties(path, "fore-color", "foreColor");
    digester.addSetProperties(path, "back-color", "backColor");
    digester.addSetProperties(path, "pdf-encoding", "pdfEncoding");
    digester.addSetProperties(path, "is-pdf-embedded", "isPdfEmbedded");
    digester.addSetProperties(path, "is-pdf-simulated-bold", "isPdfSimulatedBold");
    digester.addSetProperties(path, "is-pdf-simulated-italic", "isPdfSimulatedItalic");
    digester.addSetProperties(path, "css-class", "cssClass");

    digester.addSetNext(path, "addFontStyle");

    // FileInputStream input = new FileInputStream( xmlFileName );
    FontStylesCollection fsc = (FontStylesCollection) digester.parse(inputStream);
    fsc.findDefaultFontStyle();

    return fsc;
  }

  /**
   * Loads from xmlfile
   *
   * @param xmlFileName
   * @return
   * @throws IOException
   * @throws SAXException
   */
  public static FontStylesCollection loadFromXml(String xmlFileName) throws IOException, SAXException {
    return loadFromXml(ResourceUtil.getResourceAsStream(xmlFileName, FontStylesCollection.class.getClassLoader()));
  }
}
