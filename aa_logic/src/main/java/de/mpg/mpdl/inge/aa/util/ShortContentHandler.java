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

package de.mpg.mpdl.inge.aa.util;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ShortContentHandler extends DefaultHandler {
  private StringBuilder currentContent;
  protected final XMLStack stack = new XMLStack();
  protected final XMLStack localStack = new XMLStack();
  protected final Map<String, Map<String, String>> namespacesMap = new HashMap<>();
  protected Map<String, String> namespaces = null;

  /**
   * Manage stack and namespaces.
   */
  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    Map<String, String> formerNamespaces;
    if (null != this.namespacesMap.get(this.stack.toString())) {
      formerNamespaces = this.namespacesMap.get(this.stack.toString());
    } else {
      formerNamespaces = new HashMap<>();
    }

    this.stack.push(name);
    if (name.contains(":")) {
      this.localStack.push(name.substring(name.indexOf(":") + 1));
    } else {
      this.localStack.push(name);
    }

    Map<String, String> currentNamespaces = new HashMap<>(formerNamespaces);

    for (int i = 0; i < attributes.getLength(); i++) {
      if (attributes.getQName(i).startsWith("xmlns:")) {
        String prefix = attributes.getQName(i).substring(6);
        String nsUri = attributes.getValue(i);
        currentNamespaces.put(prefix, nsUri);
      } else if ("xmlns".equals(attributes.getQName(i))) {
        String nsUri = attributes.getValue(i);
        currentNamespaces.put("", nsUri);
      }
    }

    this.namespacesMap.put(this.stack.toString(), currentNamespaces);
    this.namespaces = currentNamespaces;

    this.currentContent = new StringBuilder();
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (null != this.currentContent) {
      content(uri, localName, name, this.currentContent.toString());
    }
    this.currentContent = null;
    this.stack.pop();
    this.localStack.pop();
  }

  /**
   * Append characters to current content.
   */
  @Override
  public final void characters(char[] ch, int start, int length) {
    if (null != this.currentContent) {
      this.currentContent.append(ch, start, length);
    }
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

  /**
   * Encodes an XML attribute. Replaces characters that might break the XML into XML entities.
   * Includes &quot; and &apos;.
   *
   * @param str The string that shall be encoded
   * @return The encoded string
   */
  public String encodeAttribute(String str) {
    return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
  }

  /**
   * Encodes XML string content. Replaces characters that might break the XML into XML entities.
   *
   * @param str The string that shall be encoded
   * @return The encoded string
   */
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
   * @author $Author: mfranke $ (last modification)
   * @version $Revision: 3183 $ $LastChangedDate: 2010-05-27 16:10:51 +0200 (Do, 27 Mai 2010) $
   */
  @SuppressWarnings("serial")
  public static class XMLStack extends Stack<String> {
    /**
     * @return A String representation of the Stack in an XPath like way (e.g.
     *         "root/subtag/subsub"):
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
