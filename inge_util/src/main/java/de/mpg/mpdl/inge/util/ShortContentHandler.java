package de.mpg.mpdl.inge.util;


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
 * Important: This class is not useful for XMLs with mixed contents: <a><b/>xyz</a>
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ShortContentHandler extends DefaultHandler {
  protected XMLStack stack = new XMLStack();
  protected XMLStack localStack = new XMLStack();

  private StringBuffer currentContent;
  private Map<String, Map<String, String>> namespacesMap = new HashMap<String, Map<String, String>>();
  private Map<String, String> namespaces = null;

  /**
   * Manage stack and namespaces.
   */
  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    Map<String, String> formerNamespaces;
    if (namespacesMap.get(stack.toString()) != null) {
      formerNamespaces = namespacesMap.get(stack.toString());
    } else {
      formerNamespaces = new HashMap<String, String>();
    }

    stack.push(name);
    if (name.contains(":")) {
      localStack.push(name.substring(name.indexOf(":") + 1));
    } else {
      localStack.push(name);
    }

    Map<String, String> currentNamespaces = new HashMap<String, String>();
    currentNamespaces.putAll(formerNamespaces);

    for (int i = 0; i < attributes.getLength(); i++) {
      if (attributes.getQName(i).startsWith("xmlns:")) {
        String prefix = attributes.getQName(i).substring(6);
        String nsUri = attributes.getValue(i);
        currentNamespaces.put(prefix, nsUri);
      } else if (attributes.getQName(i).equals("xmlns")) {
        String nsUri = attributes.getValue(i);
        currentNamespaces.put("", nsUri);
      }
    }

    namespacesMap.put(stack.toString(), currentNamespaces);
    this.namespaces = currentNamespaces;

    currentContent = new StringBuffer();
  }

  /**
   * Call {@link ShortContentHandler.content} if there is some. Then delete Current content.
   */
  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (currentContent != null) {
      content(uri, localName, name, currentContent.toString());
    }
    currentContent = null;
    stack.pop();
    localStack.pop();
  }

  /**
   * Append characters to current content.
   */
  @Override
  public final void characters(char[] ch, int start, int length) throws SAXException {
    if (currentContent != null) {
      currentContent.append(ch, start, length);
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
    return stack;
  }

  // public XMLStack getLocalStack() {
  // return localStack;
  // }

  public Map<String, String> getNamespaces() {
    return namespaces;
  }

  /**
   * A {@link Stack} extension to facilitate XML navigation.
   * 
   * @author franke (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   */
  @SuppressWarnings("serial")
  public class XMLStack extends Stack<String> {
    /**
     * @return A String representation of the Stack in an XPath like way (e.g.
     *         "root/subtag/subsub"):
     */
    @Override
    public synchronized String toString() {
      StringWriter writer = new StringWriter();
      for (Iterator<String> iterator = this.iterator(); iterator.hasNext();) {
        String element = (String) iterator.next();
        writer.append(element);
        if (iterator.hasNext()) {
          writer.append("/");
        }
      }
      return writer.toString();
    }
  }
}
