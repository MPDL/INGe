package de.mpg.mpdl.inge.util;

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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class to compare two XMLs logically.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class XmlComparator {
  private static final Logger logger = LogManager.getLogger(XmlComparator.class);

  private final List<String> errors = new ArrayList<>();
  private final List<XmlNode> elementsToIgnore = new ArrayList<>();
  private boolean omit = false;

  /**
   * Constructor taking 2 XML strings.
   *
   * @param xml1 An XML string
   * @param xml2
   * @throws Exception
   */
  public XmlComparator(String xml1, String xml2) throws Exception {
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    FirstXmlHandler firstXmlHandler = new FirstXmlHandler();
    SecondXmlHandler secondXmlHandler = new SecondXmlHandler(firstXmlHandler);
    parser.parse(new InputSource(new StringReader(xml1)), firstXmlHandler);
    parser.parse(new InputSource(new StringReader(xml2)), secondXmlHandler);
  }

  public XmlComparator(String xml1, String xml2, List<String> ignore) throws Exception {
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    FirstXmlHandler firstXmlHandler = new FirstXmlHandler();
    SecondXmlHandler secondXmlHandler = new SecondXmlHandler(firstXmlHandler);
    addElementsToIgnore(ignore);
    parser.parse(new InputSource(new StringReader(xml1)), firstXmlHandler);
    parser.parse(new InputSource(new StringReader(xml2)), secondXmlHandler);
  }

  public boolean equal() {
    return (this.errors.isEmpty());
  }

  public List<String> getErrors() {
    return this.errors;
  }

  public String listErrors() {
    StringBuilder sb = new StringBuilder();
    for (String error : this.errors) {
      sb.append(error);
      sb.append("\n");
    }

    return sb.toString();
  }

  private void addElementsToIgnore(List<String> elements) {
    if (null == elements) {
      return;
    }

    for (String e : elements) {
      String[] components = StringUtils.split(e, ",");
      String name = components[0].trim();
      Map<String, String> attributeMap = new HashMap<>();

      if (1 <= components.length && null != components[1] && components[1].contains("=")) {
        String[] attributeListToAdd = components[1].trim().split(" ");
        for (String attributeToAdd : attributeListToAdd) {
          String[] tag = StringUtils.split(attributeToAdd.trim(), "=");
          attributeMap.put(tag[0], tag[1]);
        }
      }

      String nameSpace = null;
      if (2 <= components.length && null != components[2] && !"".contentEquals(components[2].trim())) {
        nameSpace = components[2].trim();
      }

      XmlNode node = new XmlNode(attributeMap, name, nameSpace);
      this.elementsToIgnore.add(node);
    }
  }

  private class FirstXmlHandler extends ShortContentHandler {
    private final LinkedList<Node> nodeList = new LinkedList<>();

    @Override
    public void content(String uri, String localName, String name, String content) throws SAXException {
      super.content(uri, localName, name, content);
      this.nodeList.add(new TextNode(content));
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

      super.startElement(uri, localName, name, attributes);
      Map<String, String> attributeMap = new HashMap<>();

      for (int i = 0; i < attributes.getLength(); i++) {

        super.startElement(uri, localName, name, attributes);

        if (attributes.getQName(i).contains(":")) {
          if (!attributes.getQName(i).startsWith("xmlns:")) {
            String namespace = getNamespaces().get(attributes.getQName(i).substring(0, attributes.getQName(i).indexOf(":")));
            String attributeName = attributes.getQName(i).substring(attributes.getQName(i).indexOf(":") + 1);

            if (!"schemaLocation".equals(attributeName) || !"http://www.w3.org/2001/XMLSchema-instance".equals(namespace)) {
              attributeMap.put(namespace + ":" + attributeName, attributes.getValue(i));
            }
          }
        }
        // TODO MF: Hack for md-record/@name
        else if ("name".equals(attributes.getQName(i)) && "md-record".equals(name.substring(name.indexOf(":") + 1))) {
          // Do nothing

        } else {
          attributeMap.put(attributes.getQName(i), attributes.getValue(i));
        }

      }
      if (name.contains(":")) {
        this.nodeList.add(
            new XmlNode(attributeMap, name.substring(name.indexOf(":") + 1), getNamespaces().get(name.substring(0, name.indexOf(":")))));
      } else {
        this.nodeList.add(new XmlNode(attributeMap, name, null));
      }
    }
  }

  private class SecondXmlHandler extends ShortContentHandler {
    private final FirstXmlHandler firstXmlHandler;

    private SecondXmlHandler(FirstXmlHandler firstXmlHandler) {
      this.firstXmlHandler = firstXmlHandler;
    }

    @Override
    public void content(String uri, String localName, String name, String content) throws SAXException {
      super.content(uri, localName, name, content);
      TextNode textNode = new TextNode(content);
      Node other = this.firstXmlHandler.nodeList.poll();

      if (!textNode.equals(other) && !XmlComparator.this.omit) {
        XmlComparator.this.errors.add("Difference at " + this.stack + ": " + other + " != " + textNode);
      }
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

      super.startElement(uri, localName, name, attributes);
      XmlNode xmlNode;
      Map<String, String> attributeMap = new HashMap<>();
      for (int i = 0; i < attributes.getLength(); i++) {

        if (attributes.getQName(i).contains(":")) {
          if (!attributes.getQName(i).startsWith("xmlns:")) {
            String namespace = getNamespaces().get(attributes.getQName(i).substring(0, attributes.getQName(i).indexOf(":")));
            String tagName = attributes.getQName(i).substring(attributes.getQName(i).indexOf(":") + 1);

            if (!"schemaLocation".equals(tagName) || !"http://www.w3.org/2001/XMLSchema-instance".equals(namespace)) {
              attributeMap.put(namespace + ":" + tagName, attributes.getValue(i));
            }
          }
        }
        // TODO MF: Hack for md-record/@name
        else if ("name".equals(attributes.getQName(i)) && "md-record".equals(name.substring(name.indexOf(":") + 1))) {
          // Do nothing
        } else {
          attributeMap.put(attributes.getQName(i), attributes.getValue(i));
        }
      }

      if (name.contains(":")) {
        xmlNode =
            new XmlNode(attributeMap, name.substring(name.indexOf(":") + 1), getNamespaces().get(name.substring(0, name.indexOf(":"))));
      } else {
        xmlNode = new XmlNode(attributeMap, name, null);
      }

      if (XmlComparator.this.elementsToIgnore.contains(xmlNode)) {
        logger.debug("omitting <" + xmlNode + ">");
        XmlComparator.this.omit = true;
      }

      Node other = this.firstXmlHandler.nodeList.poll();

      if (!xmlNode.equals(other) && !XmlComparator.this.omit) {
        XmlComparator.this.errors.add("Difference at " + this.stack + ": " + other + " != " + xmlNode);
      }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {

      List<String> namesOfElementsToIgnore = new ArrayList<>();

      for (XmlNode node : XmlComparator.this.elementsToIgnore) {
        namesOfElementsToIgnore.add(node.name);
      }
      super.endElement(uri, localName, name);
      if (XmlComparator.this.omit && namesOfElementsToIgnore.contains(name.substring(name.indexOf(":") + 1))) {
        XmlComparator.this.omit = false;
      }
    }


  }

  private interface Node {
    boolean equals(Object other);
  }

  private class XmlNode implements Node {
    private final Map<String, String> attributes;
    private final String name;
    private final String namespace;

    private XmlNode(Map<String, String> attributes, String name, String namespace) {
      this.attributes = attributes;
      this.name = name;
      this.namespace = namespace;
    }

    @Override
    public boolean equals(Object other) {
      if (null == other || !(other instanceof XmlNode)) {
        return false;
      } else {
        for (Map.Entry<String, String> entry : this.attributes.entrySet()) {
          String attributeName = entry.getKey();
          if (!attributeName.startsWith("xmlns:") && !"xsi".equals(attributeName)
              && !entry.getValue().equals(((XmlNode) other).attributes.get(attributeName))) {
            return false;
          }
        }

        for (Map.Entry<String, String> entry : ((XmlNode) other).attributes.entrySet()) {
          String attributeName = entry.getKey();
          if (!attributeName.startsWith("xmlns:") && !entry.getValue().equals(this.attributes.get(attributeName))) {
            return false;
          }
        }

        boolean x1 = (Objects.equals(this.name, ((XmlNode) other).name));
        boolean x2 = (Objects.equals(this.namespace, ((XmlNode) other).namespace));

        return (x1 && x2);
      }
    }
  }

  public class TextNode implements Node {
    private final String content;

    public TextNode(String content) {
      this.content = content.replace(">", "&gt;");
    }

    @Override
    public boolean equals(Object other) {
      if (null == other || !(other instanceof TextNode)) {
        return false;
      } else if (null == this.content) {
        return (null == ((TextNode) other).content);
      } else if (this.content.matches("^\\s*$")) {
        return (null == ((TextNode) other).content || ((TextNode) other).content.matches("^\\s*$"));
      } else {
        return this.content.equals(((TextNode) other).content);
      }
    }

    @Override
    public String toString() {
      return this.content;
    }
  }
}
