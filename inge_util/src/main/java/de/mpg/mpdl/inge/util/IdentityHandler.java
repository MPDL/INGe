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

package de.mpg.mpdl.inge.util;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * SAX handler to write an XML that is identical to the input XML. Might be useful to make small
 * modifications in an XML.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class IdentityHandler extends ShortContentHandler {
  private final StringWriter result = new StringWriter();
  protected Map<String, String> nameSpaces = new HashMap<String, String>();
  protected String defaultNameSpace = null;
  protected int length = 0;

  public String getResult() {
    return result.toString();
  }

  // /**
  // * Appends something to the result.
  // *
  // * @param str The string to append
  // */
  // public void append(String str) {
  // result.append(str);
  // this.length += str.length();
  // }

  /**
   * {@inheritDoc}
   */
  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    super.endElement(uri, localName, name);

    result.append("</");
    this.length += 2;
    result.append(name);
    this.length += name.length();
    result.append(">");
    this.length += 1;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

    super.startElement(uri, localName, name, attributes);

    result.append("<");
    this.length += 1;
    result.append(name);
    this.length += name.length();
    for (int i = 0; i < attributes.getLength(); i++) {

      result.append(" ");
      this.length += 1;
      result.append(attributes.getQName(i));
      this.length += attributes.getQName(i).length();
      result.append("=\"");
      this.length += 2;
      result.append(XmlUtilities.escape(attributes.getValue(i)));
      this.length += XmlUtilities.escape(attributes.getValue(i)).length();
      result.append("\"");
      this.length += 1;
    }
    result.append(">");
    this.length += 1;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void content(String uri, String localName, String name, String content) throws SAXException {
    super.content(uri, localName, name, content);
    result.append(XmlUtilities.escape(content));
    this.length += XmlUtilities.escape(content).length();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processingInstruction(String name, String params) throws SAXException {
    super.processingInstruction(name, params);
    result.append("<?");
    this.length += 2;
    result.append(name);
    this.length += name.length();
    result.append(" ");
    this.length += 1;
    result.append(params);
    this.length += params.length();
    result.append("?>");
    this.length += 2;
  }

  // public int getResultLength() {
  // return this.length;
  // }
}
