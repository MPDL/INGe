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

@SuppressWarnings("serial")
public class FileFormatVO extends ValueObject {
  public static final String DOCX_MIMETYPE = "application/vnd.ms-word";
  public static final String DOCX_NAME = "docx";
  public static final String DOCX_EXTENSION = "docx";

  //  public static final String EDOC_EXPORT_MIMETYPE = "application/xml";
  //  public static final String EDOC_EXPORT_EXTENSION = "xml";
  //  public static final String EDOC_EXPORT_NAME = "edoc_export";

  //  public static final String EDOC_IMPORT_MIMETYPE = "application/xml";
  //  public static final String EDOC_IMPORT_NAME = "edoc_import";
  //  public static final String EDOC_IMPORT_EXTENSION = "xml";

  public static final String ESCIDOC_SNIPPET_MIMETYPE = "application/xml";
  public static final String ESCIDOC_SNIPPET_NAME = "escidoc_snippet";
  public static final String ESCIDOC_SNIPPET_EXTENSION = "xml";

  //  public static final String ESCIDOC_XML_MIMETYPE = "application/xml";
  //  public static final String ESCIDOC_XML_NAME = "escidoc_xml";
  //  public static final String ESCIDOC_XML_EXTENSION = "xml";

  public static final String HTML_LINKED_MIMETYPE = "text/html";
  public static final String HTML_LINKED_NAME = "html_linked";
  public static final String HTML_LINKED_EXTENSION = "html";

  public static final String HTML_PLAIN_MIMETYPE = "text/html";
  public static final String HTML_PLAIN_NAME = "html_plain";
  public static final String HTML_PLAIN_EXTENSION = "html";

  //  public static final String HTML_STYLED_MIMETYPE = "text/html";
  //  public static final String HTML_STYLED_NAME = "html_styled";
  //  public static final String HTML_STYLED_EXTENSION = "html";

  public static final String JSON_MIMETYPE = "application/json";
  public static final String JSON_NAME = "json";
  public static final String JSON_EXTENSION = "json";

  //  public static final String ODT_MIMETYPE = "application/vnd.oasis.opendocument.text";
  //  public static final String ODT_NAME = "odt";
  //  public static final String ODT_EXTENSION = "odt";

  public static final String PDF_MIMETYPE = "application/pdf";
  public static final String PDF_NAME = "pdf";
  public static final String PDF_EXTENSION = "pdf";

  public static final String PS_MIMETYPE = "application/gzip";
  public static final String PS_NAME = "ps";
  public static final String PS_EXTENSION = "ps";

  public static final String RTF_MIMETYPE = "application/rtf";
  public static final String RTF_NAME = "rtf";
  public static final String RTF_EXTENSION = "rtf";

  //  public static final String SNIPPET_MIMETYPE = "application/xml";
  //  public static final String SNIPPET_NAME = "snippet";
  //  public static final String SNIPPET_EXTENSION = "xml";

  public static final String TXT_MIMETYPE = "text/plain";
  public static final String TXT_NAME = "txt";
  public static final String TXT_EXTENSION = "txt";

  public static final String XML_MIMETYPE = "application/xml";
  public static final String XML_NAME = "xml";
  public static final String XML_EXTENSION = "xml";

  public static final String DEFAULT_CHARSET = "utf-8";
  //  public static final String DEFAULT_NAME = PDF_NAME;

  public enum FILE_FORMAT
  {
    DOCX(DOCX_NAME, DOCX_MIMETYPE, DOCX_EXTENSION, DEFAULT_CHARSET),
    //    EDOC_EXPORT(EDOC_EXPORT_NAME, EDOC_EXPORT_MIMETYPE, EDOC_EXPORT_EXTENSION, DEFAULT_CHARSET),
    //    EDOC_IMPORT(EDOC_IMPORT_NAME, EDOC_IMPORT_MIMETYPE, EDOC_IMPORT_EXTENSION, DEFAULT_CHARSET),
    ESCIDOC_SNIPPET(ESCIDOC_SNIPPET_NAME, ESCIDOC_SNIPPET_MIMETYPE, ESCIDOC_SNIPPET_EXTENSION, DEFAULT_CHARSET),
    //    ESCIDOC_XML(ESCIDOC_XML_NAME, ESCIDOC_XML_MIMETYPE, ESCIDOC_XML_EXTENSION, DEFAULT_CHARSET),
    HTML_LINKED(HTML_LINKED_NAME, HTML_LINKED_MIMETYPE, HTML_LINKED_EXTENSION, DEFAULT_CHARSET),
    HTML_PLAIN(HTML_PLAIN_NAME, HTML_PLAIN_MIMETYPE, HTML_PLAIN_EXTENSION, DEFAULT_CHARSET),
    //    HTML_STYLED(HTML_STYLED_NAME, HTML_STYLED_MIMETYPE, HTML_STYLED_EXTENSION, DEFAULT_CHARSET),
    JSON(JSON_NAME, JSON_MIMETYPE, JSON_EXTENSION, DEFAULT_CHARSET),
    //    ODT(ODT_NAME, ODT_MIMETYPE, ODT_EXTENSION, DEFAULT_CHARSET),
    PDF(PDF_NAME, PDF_MIMETYPE, PDF_EXTENSION, DEFAULT_CHARSET),
    PS(PS_NAME, PS_MIMETYPE, PS_EXTENSION, DEFAULT_CHARSET),
    RTF(RTF_NAME, RTF_MIMETYPE, RTF_EXTENSION, DEFAULT_CHARSET),
    //    SNIPPET(SNIPPET_NAME, SNIPPET_MIMETYPE, SNIPPET_EXTENSION, DEFAULT_CHARSET),
    TXT(TXT_NAME, TXT_MIMETYPE, TXT_EXTENSION, DEFAULT_CHARSET),
    XML(XML_NAME, XML_MIMETYPE, XML_EXTENSION, DEFAULT_CHARSET);

  private final String name;
  private final String mimeType;
  private final String extension;
  private final String charSet;

  FILE_FORMAT(String name, String mimeType, String extension, String charSet) {
      this.name = name;
      this.mimeType = mimeType;
      this.extension = extension;
      this.charSet = charSet;
    }

  public String getName() {
    return this.name;
  }

  public String getMimeType() {
    return this.mimeType;
  }

  public String getExtension() {
    return this.extension;
  }

  public String getCharSet() {
    return this.charSet;
  }

  }

  public static FILE_FORMAT getFileFormat(String fileFormatName) {
    for (FILE_FORMAT fileFormat : FILE_FORMAT.values()) {
      if (fileFormat.getName().equals(fileFormatName)) {
        return fileFormat;
      }
    }

    throw new IllegalArgumentException("Format " + fileFormatName + " unknown");
  }
}
