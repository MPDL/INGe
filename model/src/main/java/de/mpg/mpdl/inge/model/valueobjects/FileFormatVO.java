/*
 * 
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


package de.mpg.mpdl.inge.model.valueobjects;

import java.util.HashMap;
import java.util.Map;

/**
 * Valueobject representing the export file format data needed for the export.
 * 
 * @version $Revision$ $LastChangedDate$ by $Author$
 */

@SuppressWarnings("serial")
public class FileFormatVO extends ValueObject {

  /**
   * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
   * 'de.mpg.mpdl.inge.model.valueobjects.ItemVO; local class incompatible: stream classdesc
   * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286'
   * that occur after JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to
   * be compiled twice for testing (once for the Application Server, once for the local test).
   * 
   * @author Johannes Mueller
   */

  public static final String DOCX_MIMETYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
  public static final String DOCX_NAME = "docx";
  public static final String EDOC_EXPORT_MIMETYPE = "application/xml";
  public static final String EDOC_EXPORT_NAME = "edoc_export";
  public static final String EDOC_IMPORT_MIMETYPE = "application/xml";
  public static final String EDOC_IMPORT_NAME = "edoc_import";
  public static final String ESCIDOC_SNIPPET_MIMETYPE = "application/xml";
  public static final String ESCIDOC_SNIPPET_NAME = "escidoc_snippet";
  public static final String ESCIDOC_XML_MIMETYPE = "application/xml";
  public static final String ESCIDOC_XML_NAME = "escidoc_xml";
  public static final String HTML_LINKED_MIMETYPE = "text/html";
  public static final String HTML_LINKED_NAME = "html_linked";
  public static final String HTML_PLAIN_MIMETYPE = "text/html";
  public static final String HTML_PLAIN_NAME = "html_plain";
  public static final String HTML_STYLED_MIMETYPE = "text/html";
  public static final String HTML_STYLED_NAME = "html_styled";
  public static final String JSON_MIMETYPE = "application/json";
  public static final String JSON_NAME = "json";
  public static final String ODT_MIMETYPE = "application/vnd.oasis.opendocument.text";
  public static final String ODT_NAME = "odt";
  public static final String PDF_MIMETYPE = "application/pdf";
  public static final String PDF_NAME = "pdf";
  public static final String PS_MIMETYPE = "application/gzip";
  public static final String PS_NAME = "ps";
  public static final String RTF_MIMETYPE = "application/rtf";
  public static final String RTF_NAME = "rtf";
  public static final String SNIPPET_MIMETYPE = "application/xml";
  public static final String SNIPPET_NAME = "snippet";
  public static final String TXT_MIMETYPE = "text/plain";
  public static final String TXT_NAME = "txt";
  public static final String XML_MIMETYPE = "application/xml";
  public static final String XML_NAME = "xml";

  public static final String DEFAULT_MIMETYPE = PDF_MIMETYPE;
  public static final String DEFAULT_NAME = PDF_NAME;
  public static final String DEFAULT_CHARSET = "utf-8";

  private static final Map<String, String> formatExtensions = new HashMap<String, String>() {
    {
      put(DEFAULT_NAME, "pdf");
      put(DOCX_NAME, "docx");
      put(EDOC_EXPORT_NAME, "xml");
      put(EDOC_IMPORT_NAME, "xml");
      put(ESCIDOC_SNIPPET_NAME, "xml");
      put(ESCIDOC_XML_NAME, "xml");
      put(HTML_LINKED_NAME, "html");
      put(HTML_PLAIN_NAME, "html");
      put(HTML_STYLED_NAME, "html");
      put(JSON_NAME, "json");
      put(ODT_NAME, "odt");
      put(PDF_NAME, "pdf");
      put(PS_NAME, "ps");
      put(RTF_NAME, "rtf");
      put(SNIPPET_NAME, "xml");
      put(TXT_NAME, "txt");
      put(XML_NAME, "xml");
    }
  };

  private static final Map<String, String> mimeTypeExtensions = new HashMap<String, String>() {
    {
      put(DEFAULT_MIMETYPE, "pdf");
      put(DOCX_MIMETYPE, "docx");
      put(EDOC_EXPORT_MIMETYPE, "xml");
      put(EDOC_IMPORT_MIMETYPE, "xml");
      put(ESCIDOC_SNIPPET_MIMETYPE, "xml");
      put(ESCIDOC_XML_MIMETYPE, "xml");
      put(HTML_LINKED_MIMETYPE, "html");
      put(HTML_PLAIN_MIMETYPE, "html");
      put(HTML_STYLED_MIMETYPE, "html");
      put(JSON_MIMETYPE, "json");
      put(ODT_MIMETYPE, "odt");
      put(PDF_MIMETYPE, "pdf");
      put(PS_MIMETYPE, "ps");
      put(RTF_MIMETYPE, "rtf");
      put(SNIPPET_MIMETYPE, "xml");
      put(TXT_MIMETYPE, "txt");
      put(XML_MIMETYPE, "xml");
    }
  };

  private static final Map<String, String> mimeTypeName = new HashMap<String, String>() {
    {
      put(DEFAULT_MIMETYPE, DEFAULT_NAME);
      put(DOCX_MIMETYPE, DOCX_NAME);
      put(EDOC_EXPORT_MIMETYPE, EDOC_EXPORT_NAME);
      put(EDOC_IMPORT_MIMETYPE, EDOC_IMPORT_NAME);
      put(ESCIDOC_SNIPPET_MIMETYPE, ESCIDOC_SNIPPET_NAME);
      put(ESCIDOC_XML_MIMETYPE, ESCIDOC_XML_NAME);
      put(HTML_LINKED_MIMETYPE, HTML_LINKED_NAME);
      put(HTML_PLAIN_MIMETYPE, HTML_PLAIN_NAME);
      put(HTML_STYLED_MIMETYPE, HTML_STYLED_NAME);
      put(JSON_MIMETYPE, JSON_NAME);
      put(ODT_MIMETYPE, ODT_NAME);
      put(PDF_MIMETYPE, PDF_NAME);
      put(PS_MIMETYPE, PS_NAME);
      put(RTF_MIMETYPE, RTF_NAME);
      put(SNIPPET_MIMETYPE, SNIPPET_NAME);
      put(TXT_MIMETYPE, TXT_NAME);
      put(XML_MIMETYPE, XML_NAME);
    }
  };

  private static final Map<String, String> formatMimeTypes = new HashMap<String, String>() {
    {
      put(DEFAULT_NAME, DEFAULT_MIMETYPE);
      put(DOCX_NAME, DOCX_MIMETYPE);
      put(EDOC_EXPORT_NAME, EDOC_EXPORT_MIMETYPE);
      put(EDOC_IMPORT_NAME, EDOC_IMPORT_MIMETYPE);
      put(ESCIDOC_SNIPPET_NAME, ESCIDOC_SNIPPET_MIMETYPE);
      put(ESCIDOC_XML_NAME, ESCIDOC_XML_MIMETYPE);
      put(HTML_LINKED_NAME, HTML_LINKED_MIMETYPE);
      put(HTML_PLAIN_NAME, HTML_PLAIN_MIMETYPE);
      put(HTML_STYLED_NAME, HTML_STYLED_MIMETYPE);
      put(JSON_NAME, JSON_MIMETYPE);
      put(ODT_NAME, ODT_MIMETYPE);
      put(PDF_NAME, PDF_MIMETYPE);
      put(PS_NAME, PS_MIMETYPE);
      put(RTF_NAME, RTF_MIMETYPE);
      put(SNIPPET_NAME, SNIPPET_MIMETYPE);
      put(TXT_NAME, TXT_MIMETYPE);
      put(XML_NAME, XML_MIMETYPE);
    }
  };

  /**
   * The mime type of FileFormat
   */
  private String mimeType;

  /**
   * The name of FileFormat
   */
  private String name;

  /**
   * Delivers the name of the selected file according to name of format.
   */
  public static String getMimeTypeByName(String name) {
    name = name == null || name.trim().equals("") ? "" : name.trim();
    return formatMimeTypes.containsKey(name) ? formatMimeTypes.get(name) : formatMimeTypes.get(DEFAULT_NAME);
  }

  /**
   * Delivers the name of the selected file according to name of format.
   */
  public static String getExtensionByName(String name) {
    name = name == null || name.trim().equals("") ? "" : name.trim();
    return formatExtensions.containsKey(name) ? formatExtensions.get(name) : formatExtensions.get(DEFAULT_NAME);
  }

  /**
   * Delivers the extension of the selected file according to mimeType of format.
   */
  public static String getExtensionByMimeType(String mimeType) {
    mimeType = mimeType == null || mimeType.trim().equals("") ? "" : mimeType.trim();
    return mimeTypeExtensions.containsKey(mimeType) ? mimeTypeExtensions.get(mimeType) : mimeTypeExtensions.get(DEFAULT_MIMETYPE);
  }

  /**
   * Delivers the nmae of the selected file according to mimeType of format.
   */
  public static String getNameByMimeType(String mimeType) {
    mimeType = mimeType == null || mimeType.trim().equals("") ? "" : mimeType.trim();
    return mimeTypeName.containsKey(mimeType) ? mimeTypeName.get(mimeType) : mimeTypeName.get(DEFAULT_MIMETYPE);
  }

  /**
   * get mimeType
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * set mimeType
   */
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  /**
   * get name of the file format
   */
  public String getName() {
    return name;
  }

  /**
   * set name of the file format
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "FileFormatVO [mimeType=" + mimeType + ", name=" + name + "]";
  }

}
