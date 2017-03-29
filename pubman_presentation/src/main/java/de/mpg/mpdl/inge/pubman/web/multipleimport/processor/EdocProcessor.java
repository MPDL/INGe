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

package de.mpg.mpdl.inge.pubman.web.multipleimport.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.axis.encoding.Base64;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.util.IdentityHandler;

/**
 * Format processor for eDoc XML files.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class EdocProcessor extends FormatProcessor {

  private boolean init = false;
  private final List<String> items = new ArrayList<String>();
  private int counter = -1;
  private int length = -1;
  private byte[] originalData = null;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasNext() {
    if (!this.init) {
      this.initialize();
    }
    return (this.originalData != null && this.counter < this.length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String next() throws NoSuchElementException {
    if (!this.init) {
      this.initialize();
    }
    if (this.originalData != null && this.counter < this.length) {
      return this.items.get(this.counter++);
    } else {
      throw new NoSuchElementException("No more entries left");
    }

  }

  /**
   * Not implemented.
   */
  @Override
  @Deprecated
  public void remove() {
    throw new RuntimeException("Method not implemented");
  }

  private void initialize() {
    this.init = true;

    try {

      final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      final EdocHandler edocHandler = new EdocHandler();
      parser.parse(this.getSourceFile(), edocHandler);

      this.originalData = edocHandler.getResult().getBytes(this.getEncoding());

      this.length = this.items.size();

      this.counter = 0;

    } catch (final Exception e) {
      throw new RuntimeException("Error reading input stream", e);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getLength() {
    return this.length;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDataAsBase64() {
    if (this.originalData == null) {
      return null;
    }

    return Base64.encode(this.originalData);
  }

  /**
   * SAX parser to extract the items out of the XML.
   * 
   * @author franke (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   * 
   */
  public class EdocHandler extends IdentityHandler {

    private StringBuilder builder;
    private boolean inItem = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes)
        throws SAXException {
      if ("edoc".equals(this.getStack().toString())) {
        this.builder = new StringBuilder();
        this.inItem = true;
      }
      super.startElement(uri, localName, name, attributes);

      if (this.inItem) {
        this.builder.append("<");
        this.builder.append(name);
        for (int i = 0; i < attributes.getLength(); i++) {

          this.builder.append(" ");
          this.builder.append(attributes.getQName(i));
          this.builder.append("=\"");
          this.builder.append(this.escape(attributes.getValue(i)));
          this.builder.append("\"");
        }
        this.builder.append(">");
      }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
      super.endElement(uri, localName, name);

      if (this.inItem) {
        this.builder.append("</");
        this.builder.append(name);
        this.builder.append(">");
      }

      if ("edoc".equals(this.getStack().toString())) {
        EdocProcessor.this.items.add(this.builder.toString());
        this.builder = null;
        this.inItem = false;
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void content(String uri, String localName, String name, String content)
        throws SAXException {
      super.content(uri, localName, name, content);
      if (this.inItem) {
        this.builder.append(this.escape(content));
      }
    }
  }

}
