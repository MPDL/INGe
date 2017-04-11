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

package de.mpg.mpdl.inge.structuredexportmanager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerCache;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * Structured Export Manager. Converts PubMan item-list to one of the structured formats.
 * 
 * @author Vlad Makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class StructuredExportService {

  private final static String PATH_TO_RESOURCES = "";
  private final static String EXPLAIN_FILE = "explain-structured-formats.xml";

  private static Map<String, FORMAT> map;
  static {
    map = new HashMap<String, FORMAT>();
    map.put("MARCXML", FORMAT.MARC_XML);
    map.put("ENDNOTE", FORMAT.ENDNOTE_STRING);
    map.put("BIBTEX", FORMAT.BIBTEX_STRING);
    map.put("ESCIDOC_XML", FORMAT.ESCIDOC_ITEM_V3_XML);
    map.put("EDOC_EXPORT", FORMAT.EDOC_XML);
    map.put("EDOC_IMPORT", FORMAT.EDOC_XML);
  }


  private StructuredExportService() {}

  /*
   * ( Takes PubMan item-list and converts it to specified exportFormat. Uses XSLT.
   * 
   * @see de.mpg.mpdl.inge.endnotemanager.StructuredExportHandler#getOutputString(java.lang.String ,
   * java.lang.String)
   */
  public static byte[] getOutput(String itemList, String exportFormat)
      throws StructuredExportXSLTNotFoundException, StructuredExportManagerException {
    // check itemList
    if (itemList == null)
      throw new StructuredExportManagerException("Item list is null");

    if ("ESCIDOC_XML_V13".equalsIgnoreCase(exportFormat)) {
      return itemList.getBytes();
    }

    // check format
    Transformer trans = null;
    StringWriter wr = new StringWriter();

    try {
      trans =
          TransformerCache.getTransformer(FORMAT.ESCIDOC_ITEMLIST_V3_XML, map.get(exportFormat));
      trans.transform(
          new TransformerStreamSource(new ByteArrayInputStream(itemList.getBytes("UTF-8"))),
          new TransformerStreamResult(wr));

      return wr.toString().getBytes("UTF-8");
    } catch (Exception e) {
      throw new StructuredExportManagerException("Error during transformation to <" + exportFormat
          + ">", e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.exportmanager.StructuredExportHandler#explainFormats()
   */
  public static String explainFormats() throws StructuredExportManagerException {
    BufferedReader br;
    try {
      br =
          new BufferedReader(new InputStreamReader(ResourceUtil.getResourceAsStream(
              PATH_TO_RESOURCES + EXPLAIN_FILE, StructuredExportService.class.getClassLoader()),
              "UTF-8"));
    } catch (Exception e) {
      throw new StructuredExportManagerException(e);
    }
    String line = null;
    String result = "";
    try {
      while ((line = br.readLine()) != null) {
        result += line + "\n";
      }
    } catch (IOException e) {
      throw new StructuredExportManagerException(e);
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.structuredexportmanager.StructuredExportHandler#isStructuredFormat(
   * java.lang.String)
   */
  public static boolean isStructuredFormat(String exportFormat)
      throws StructuredExportManagerException {
    if (exportFormat == null || exportFormat.trim().equals("")) {
      throw new StructuredExportManagerException("Empty export format");
    }

    return Arrays.asList(TransformerCache.getAllSourceFormatsFor(FORMAT.valueOf(exportFormat)))
        .contains(FORMAT.ESCIDOC_ITEMLIST_V3_XML);
  }
}
