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

package de.mpg.mpdl.inge.aa.old;

import org.xml.sax.helpers.DefaultHandler;

/**
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class AaServerConfiguration extends DefaultHandler {
  //  Map<String, String> map = new LinkedHashMap<String, String>();
  //  boolean newEntry = false;
  //  boolean isKey = false;
  //  boolean isValue = false;
  //  StringWriter currentKey = null;
  //  StringWriter currentValue = null;
  //
  //  public AaServerConfiguration() throws Exception {
  //    String file = PropertyReader.getProperty(PropertyReader.INGE_AA_CONFIG_FILE);
  //
  //    InputStream inputStream = ResourceUtil.getResourceAsStream(file, AaServerConfiguration.class.getClassLoader());
  //    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
  //    parser.parse(inputStream, this);
  //    inputStream.close();
  //  }
  //
  //  @Override
  //  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
  //    if ("authentication".equals(qName)) {
  //      newEntry = true;
  //    } else if (newEntry && !isKey && !isValue && "name".equals(qName)) {
  //      isKey = true;
  //      currentKey = new StringWriter();
  //    } else if (newEntry && !isKey && !isValue && "url".equals(qName)) {
  //      isValue = true;
  //      currentValue = new StringWriter();
  //    } else if (!"auth-config".equals(qName)) {
  //      throw new SAXException("Error reading configuration at  '" + qName + "'");
  //    }
  //
  //  }
  //
  //  @Override
  //  public void endElement(String uri, String localName, String qName) throws SAXException {
  //    if ("authentication".equals(qName)) {
  //      newEntry = false;
  //      map.put(currentKey.toString(), currentValue.toString());
  //      currentKey = null;
  //      currentValue = null;
  //    } else if ("name".equals(qName)) {
  //      isKey = false;
  //    } else if ("url".equals(qName)) {
  //      isValue = false;
  //    } else if (!"auth-config".equals(qName)) {
  //      throw new SAXException("Error reading configuration at  '" + qName + "'");
  //    }
  //  }
  //
  //  @Override
  //  public void characters(char[] ch, int start, int length) throws SAXException {
  //    if (isKey) {
  //      currentKey.write(ch, start, length);
  //    } else if (isValue) {
  //      currentValue.write(ch, start, length);
  //    }
  //  }
  //
  //  public Map<String, String> getMap() {
  //    return map;
  //  }
  //
  //
}
