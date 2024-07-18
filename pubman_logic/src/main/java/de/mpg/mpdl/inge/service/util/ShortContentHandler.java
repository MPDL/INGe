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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft fÃŒr
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur FÃ¶rderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.service.util;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Generic SAX handler with convenience methods. Useful for XML with only short string content.
 * Classes that extend this class should always call super() at the beginning of an overridden
 * method.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ShortContentHandler extends DefaultHandler {
  private StringWriter currentContent = new StringWriter();
  protected final XMLStack stack = new XMLStack();
  protected final XMLStack localStack = new XMLStack();
  protected final Map<String, String> namespaces = new HashMap<>();

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    this.stack.push(name);
    if (name.contains(":")) {
      this.localStack.push(name.substring(name.indexOf(":") + 1));
    } else {
      this.localStack.push(name);
    }

    for (int i = 0; i < attributes.getLength(); i++) {
      if (attributes.getQName(i).startsWith("xmlns:")) {
        String prefix = attributes.getQName(i).substring(6);
        String nsUri = attributes.getValue(i);
        this.namespaces.put(prefix, nsUri);
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    this.content(uri, localName, name, this.currentContent.toString());
    this.currentContent = new StringWriter();
    this.stack.pop();
    this.localStack.pop();
  }

  @Override
  public final void characters(char[] ch, int start, int length) {
    this.currentContent.write(ch, start, length);
  }

  /**
   * Called when string content was found.
   *
   * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or if
   *        Namespace processing is not being performed.
   * @param localName The local name (without prefix), or the empty string if Namespace processing
   *        is not being performed.
   * @param name The qualified name (with prefix), or the empty string if qualified names are not
   *        available.
   * @param content The string content of the current tag.
   */
  public void content(String uri, String localName, String name, String content) {
    // Do nothing by default
  }

  public String encodeAttribute(String str) {
    return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
  }

  public String encodeContent(String str) {
    return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }

  public XMLStack getStack() {
    return this.stack;
  }

  public XMLStack getLocalStack() {
    return this.localStack;
  }

  public Map<String, String> getNamespaces() {
    return this.namespaces;
  }

  /**
   * A {@link Stack} extension to facilitate XML navigation.
   *
   * @author franke (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   */
  @SuppressWarnings("serial")
  public static class XMLStack extends Stack<String> {
    /**
     * Returns a String representation of the Stack in an XPath like way (e.g.
     * "root/subtag/subsub"):
     */
    @Override
    public synchronized String toString() {
      StringWriter writer = new StringWriter();
      for (Iterator<String> iterator = this.iterator(); iterator.hasNext();) {
        String element = iterator.next();
        writer.append(element);
        if (iterator.hasNext()) {
          writer.append("/");
        }
      }
      return writer.toString();
    }
  }
}
