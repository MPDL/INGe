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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
  public static Logger logger = Logger.getLogger(XmlComparator.class);
  private List<String> errors = new ArrayList<String>();
  private Map<String, String> attributesToIgnore = new HashMap<String, String>();
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
  
  public XmlComparator(String xml1, String xml2, String ignore) throws Exception {
	    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
	    FirstXmlHandler firstXmlHandler = new FirstXmlHandler();
	    SecondXmlHandler secondXmlHandler = new SecondXmlHandler(firstXmlHandler);
	    addElementToIgnore(ignore);
	    parser.parse(new InputSource(new StringReader(xml1)), firstXmlHandler);
	    parser.parse(new InputSource(new StringReader(xml2)), secondXmlHandler);
	  }
  public List<String> errors = new ArrayList<String>();

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

  public boolean equal() {
    return (errors.size() == 0);
  }

  public List<String> getErrors() {
    return errors;
  }

  public String listErrors() {
    return Arrays.toString(errors.toArray(new String[this.errors.size()]));
  }
  public String listErrors() {
    return Arrays.toString(errors.toArray(new String[this.errors.size()]));

  public void addElementToIgnore(String element) {
    String[] attributeKeyAndValue = StringUtils.split(element, '=');
    attributesToIgnore.put(attributeKeyAndValue[0], attributeKeyAndValue[1]);
  }
  }

  private class FirstXmlHandler extends ShortContentHandler {

    private LinkedList<Node> nodeList = new LinkedList<Node>();


    @Override
    public void content(String uri, String localName, String name, String content)
        throws SAXException {
      super.content(uri, localName, name, content);
      nodeList.add(new TextNode(content));
    }

    @Override
    public void content(String uri, String localName, String name, String content)
        throws SAXException {
      super.content(uri, localName, name, content);
      
      nodeList.add(new TextNode(content));
    }
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes)
        throws SAXException {
      super.startElement(uri, localName, name, attributes);
      Map<String, String> attributeMap = new HashMap<String, String>();
      for (int i = 0; i < attributes.getLength(); i++) {
        if (attributes.getQName(i).contains(":")) {
          if (!attributes.getQName(i).startsWith("xmlns:")) {
            String namespace =
                getNamespaces().get(
                    attributes.getQName(i).substring(0, attributes.getQName(i).indexOf(":")));
            String attributeName =
                attributes.getQName(i).substring(attributes.getQName(i).indexOf(":") + 1);

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes)
        throws SAXException {
    	
      logger.info(uri + "1" + localName + "2" + name);
      super.startElement(uri, localName, name, attributes);
      Map<String, String> attributeMap = new HashMap<String, String>();
      for (int i = 0; i < attributes.getLength(); i++) {
        logger.info(attributes.getQName(i) + "XX" + attributes.getValue(i));
        
        if (attributes.getQName(i).contains(":")) {
          if (!attributes.getQName(i).startsWith("xmlns:")) {
            String namespace =
                getNamespaces().get(
                    attributes.getQName(i).substring(0, attributes.getQName(i).indexOf(":")));
            String attributeName =
                attributes.getQName(i).substring(attributes.getQName(i).indexOf(":") + 1);
            if (!"schemaLocation".equals(attributeName)
                || !"http://www.w3.org/2001/XMLSchema-instance".equals(namespace)) {
              attributeMap.put(namespace + ":" + attributeName, attributes.getValue(i));
            }
          }
        }
        // TODO MF: Hack for md-record/@name
        else if ("name".equals(attributes.getQName(i))
            && "md-record".equals(name.substring(name.indexOf(":") + 1))) {
          // Do nothing
        } else {
          attributeMap.put(attributes.getQName(i), attributes.getValue(i));
        }

            if (!"schemaLocation".equals(attributeName)
                || !"http://www.w3.org/2001/XMLSchema-instance".equals(namespace)) {
              attributeMap.put(namespace + ":" + attributeName, attributes.getValue(i));
            }
          }
        }
        // TODO MF: Hack for md-record/@name
        else if ("name".equals(attributes.getQName(i))
            && "md-record".equals(name.substring(name.indexOf(":") + 1))) {
          // Do nothing
        }        
        else {
          attributeMap.put(attributes.getQName(i), attributes.getValue(i));
        }
      }
      if (name.contains(":")) {
        nodeList.add(new XmlNode(attributeMap, name.substring(name.indexOf(":") + 1),
            getNamespaces().get(name.substring(0, name.indexOf(":")))));
      } else {
        nodeList.add(new XmlNode(attributeMap, name, null));
      }
    }

      }
      if (name.contains(":")) {
        nodeList.add(new XmlNode(attributeMap, name.substring(name.indexOf(":") + 1),
            getNamespaces().get(name.substring(0, name.indexOf(":")))));
      } else {
        nodeList.add(new XmlNode(attributeMap, name, null));
      }
    }
  }

  }
  private class SecondXmlHandler extends ShortContentHandler {
    private FirstXmlHandler firstXmlHandler;

  private class SecondXmlHandler extends ShortContentHandler {
    private FirstXmlHandler firstXmlHandler;
    public SecondXmlHandler(FirstXmlHandler firstXmlHandler) {
      this.firstXmlHandler = firstXmlHandler;
    }

    public SecondXmlHandler(FirstXmlHandler firstXmlHandler) {
      this.firstXmlHandler = firstXmlHandler;
    }
    @Override
    public void content(String uri, String localName, String name, String content)
        throws SAXException {
      super.content(uri, localName, name, content);
      TextNode textNode = new TextNode(content);
      Node other = firstXmlHandler.nodeList.poll();

    @Override
    public void content(String uri, String localName, String name, String content)
        throws SAXException {
      super.content(uri, localName, name, content);
      TextNode textNode = new TextNode(content);
      Node other = firstXmlHandler.nodeList.poll();
    	  
      if (!textNode.equals(other) && !omit) {
        errors.add("Difference at " + stack.toString() + ": " + other + " != " + textNode);
      }
      if (omit) {
    	  omit = false;
      }
    }
      if (!textNode.equals(other)) {
        errors.add("Difference at " + stack.toString() + ": " + other + " != " + textNode);
      }
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes)
        throws SAXException {
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes)
        throws SAXException {
      super.startElement(uri, localName, name, attributes);
      XmlNode xmlNode;
      Map<String, String> attributeMap = new HashMap<String, String>();
      for (int i = 0; i < attributes.getLength(); i++) {
        if (attributes.getQName(i).contains(":")) {
          if (!attributes.getQName(i).startsWith("xmlns:")) {
            String namespace =
                getNamespaces().get(
                    attributes.getQName(i).substring(0, attributes.getQName(i).indexOf(":")));
            String tagName =
                attributes.getQName(i).substring(attributes.getQName(i).indexOf(":") + 1);

        logger.info(uri + "1" + localName + "2" + name);
      super.startElement(uri, localName, name, attributes);
      XmlNode xmlNode;
      Map<String, String> attributeMap = new HashMap<String, String>();
      for (int i = 0; i < attributes.getLength(); i++) {
    	  logger.info(attributes.getQName(i) + "XX" + attributes.getValue(i));
    	  
    	  if (attributesToIgnore.get(attributes.getQName(i)) != null 
          		&& attributesToIgnore.get(attributes.getQName(i)).equals(attributes.getValue(i))) { 
          	logger.info("omitting <" + attributes.getQName(i) + "> <" + attributes.getValue(i) + ">");
          	omit = true;
          }
    	  
        if (attributes.getQName(i).contains(":")) {
          if (!attributes.getQName(i).startsWith("xmlns:")) {
            String namespace =
                getNamespaces().get(
                    attributes.getQName(i).substring(0, attributes.getQName(i).indexOf(":")));
            String tagName =
                attributes.getQName(i).substring(attributes.getQName(i).indexOf(":") + 1);
            if (!"schemaLocation".equals(tagName)
                || !"http://www.w3.org/2001/XMLSchema-instance".equals(namespace)) {
              attributeMap.put(namespace + ":" + tagName, attributes.getValue(i));
            }
          }
        }
        // TODO MF: Hack for md-record/@name
        else if ("name".equals(attributes.getQName(i))
            && "md-record".equals(name.substring(name.indexOf(":") + 1))) {
          // Do nothing
        } else {
          attributeMap.put(attributes.getQName(i), attributes.getValue(i));
        }

            if (!"schemaLocation".equals(tagName)
                || !"http://www.w3.org/2001/XMLSchema-instance".equals(namespace)) {
              attributeMap.put(namespace + ":" + tagName, attributes.getValue(i));
            }
          }
        }
        // TODO MF: Hack for md-record/@name
        else if ("name".equals(attributes.getQName(i))
            && "md-record".equals(name.substring(name.indexOf(":") + 1))) {
          // Do nothing
        } 
        else {
          attributeMap.put(attributes.getQName(i), attributes.getValue(i));
        }
      }

      }
      if (name.contains(":")) {
        xmlNode =
            new XmlNode(attributeMap, name.substring(name.indexOf(":") + 1), getNamespaces().get(
                name.substring(0, name.indexOf(":"))));
      } else {
        xmlNode = new XmlNode(attributeMap, name, null);
      }
      Node other = firstXmlHandler.nodeList.poll();
      if (name.contains(":")) {
        xmlNode =
            new XmlNode(attributeMap, name.substring(name.indexOf(":") + 1), getNamespaces().get(
                name.substring(0, name.indexOf(":"))));
      } else {
        xmlNode = new XmlNode(attributeMap, name, null);
      }
      Node other = firstXmlHandler.nodeList.poll();

      if (!xmlNode.equals(other)) {
        errors.add("Difference at " + stack.toString() + ": " + other + " != " + xmlNode);
      }
    }

  }

  private interface Node {
    public boolean equals(Object other);
  }

  private class XmlNode implements Node {
    private Map<String, String> attributes;
    private String name;
    private String namespace;

    public XmlNode(Map<String, String> attributes, String name, String namespace) {
      this.attributes = attributes;
      this.name = name;
      this.namespace = namespace;
    }

    @Override
    public boolean equals(Object other) {
      if (other == null || !(other instanceof XmlNode)) {
        return false;
      } else {
        for (String name : attributes.keySet()) {
          if (!name.startsWith("xmlns:") && !name.equals("xsi")
              && !attributes.get(name).equals(((XmlNode) other).attributes.get(name))) {
            return false;
          }
        }

        for (String name : ((XmlNode) other).attributes.keySet()) {
          if (!name.startsWith("xmlns:")
              && !((XmlNode) other).attributes.get(name).equals(attributes.get(name))) {
            return false;
          }
        }

        boolean x1 =
            (name == null ? ((XmlNode) other).name == null : name.equals(((XmlNode) other).name));
        boolean x2 =
            (namespace == null ? ((XmlNode) other).namespace == null : namespace
                .equals(((XmlNode) other).namespace));

        return (x1 && x2);
      }
    }

  }

  private class TextNode implements Node {
    private String content;

    public TextNode(String content) {
      this.content = content.replace(">", "&gt;");
    }

    @Override
    public boolean equals(Object other) {
      if (other == null || !(other instanceof TextNode)) {
        return false;
      } else if (content == null) {
        return (((TextNode) other).content == null);
      } else if (content.matches("^\\s*$")) {
        return (((TextNode) other).content == null || ((TextNode) other).content.matches("^\\s*$"));
      } else {
        return content.equals(((TextNode) other).content);
      }
    }

    @Override
    public String toString() {
      return content;
    }

  }
}
