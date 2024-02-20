package de.mpg.mpdl.inge.util;


import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ShortContentHandler extends DefaultHandler {
  protected final XMLStack stack = new XMLStack();
  protected final XMLStack localStack = new XMLStack();

  private StringBuilder currentContent;
  private final Map<String, Map<String, String>> namespacesMap = new HashMap<>();
  private Map<String, String> namespaces = null;

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
  public void content(String uri, String localName, String name, String content) throws SAXException {
    // Do nothing by default
  }

  // /**
  // * Encodes an XML attribute. Replaces characters that might break the XML into XML entities.
  // * Includes &quot; and &apos;.
  // *
  // * @param str The string that shall be encoded
  // * @return The encoded string
  // */
  // public String encodeAttribute(String str) {
  // return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
  // .replace("\"", "&quot;").replace("'", "&apos;");
  // }

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

  // public XMLStack getLocalStack() {
  // return localStack;
  // }

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
