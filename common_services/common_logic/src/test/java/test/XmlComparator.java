/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
public class XmlComparator
{
    public List<String> errors = new ArrayList<String>();
    
    /**
     * Constructor taking 2 XML strings.
     * 
     * @param xml1 An XML string
     * @param xml2
     * @throws Exception
     */
    public XmlComparator(String xml1, String xml2) throws Exception
    {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        FirstXmlHandler firstXmlHandler = new FirstXmlHandler();
        SecondXmlHandler secondXmlHandler = new SecondXmlHandler(firstXmlHandler);
        parser.parse(new InputSource(new StringReader(xml1)), firstXmlHandler);
        parser.parse(new InputSource(new StringReader(xml2)), secondXmlHandler);
    }
    
    public static void main(String[] args) throws Exception
    {
        XmlComparator xmlComparator = new XmlComparator(args[0], args[1]);
        if (xmlComparator.equal())
        {
            System.out.println("XMLs are equal.");
        }
        else
        {
            System.out.println("Differences detected:");
            for (String error : xmlComparator.getErrors())
            {
                System.out.println(error);
            }
        }
        
    }
    
    public boolean equal()
    {
        return (errors.size() == 0);
    }

    public List<String> getErrors()
    {
        return errors;
    }

    private class FirstXmlHandler extends ShortContentHandler
    {

        private LinkedList<Node> nodeList = new LinkedList<Node>();
        
        @Override
        public void content(String uri, String localName, String name, String content)
        {
            super.content(uri, localName, name, content);
            nodeList.add(new TextNode(content));
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
        {
            super.startElement(uri, localName, name, attributes);
            Map<String, String> attributeMap = new HashMap<String, String>();
            for (int i = 0; i < attributes.getLength(); i++)
            {
                if (attributes.getQName(i).contains(":"))
                {
                    if (!attributes.getQName(i).startsWith("xmlns:"))
                    {
                        String namespace = getNamespaces().get(attributes.getQName(i).substring(0, attributes.getQName(i).indexOf(":")));
                        String tagName = attributes.getQName(i).substring(attributes.getQName(i).indexOf(":") + 1);
                        
                        if (!"schemaLocation".equals(tagName) || !"http://www.w3.org/2001/XMLSchema-instance".equals(namespace))
                        {
                            attributeMap.put( namespace + ":" + tagName , attributes.getValue(i));
                        }
                    }
                }
                else
                {
                    attributeMap.put(attributes.getQName(i), attributes.getValue(i));
                }
                
            }
            if (name.contains(":"))
            {
                nodeList.add(new XmlNode(attributeMap, name.substring(name.indexOf(":") + 1), getNamespaces().get(name.substring(0, name.indexOf(":")))));
            }
            else
            {
                nodeList.add(new XmlNode(attributeMap, name, null));
            }
        }

        public List<Node> getNodeList()
        {
            return nodeList;
        }
        
    }
    
    private class SecondXmlHandler extends ShortContentHandler
    {
        private FirstXmlHandler firstXmlHandler;
        
        public SecondXmlHandler(FirstXmlHandler firstXmlHandler)
        {
            this.firstXmlHandler = firstXmlHandler;
        }

        @Override
        public void content(String uri, String localName, String name, String content)
        {
            super.content(uri, localName, name, content);
            TextNode textNode = new TextNode(content);
            Node other = firstXmlHandler.nodeList.poll();
            
            if (!textNode.equals(other))
            {
                errors.add("Difference at " + stack.toString() + ": " + other + " != " + textNode);
            }
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
        {
            super.startElement(uri, localName, name, attributes);
            XmlNode xmlNode;
            Map<String, String> attributeMap = new HashMap<String, String>();
            for (int i = 0; i < attributes.getLength(); i++)
            {
                if (attributes.getQName(i).contains(":"))
                {
                    if (!attributes.getQName(i).startsWith("xmlns:"))
                    {
                        String namespace = getNamespaces().get(attributes.getQName(i).substring(0, attributes.getQName(i).indexOf(":")));
                        String tagName = attributes.getQName(i).substring(attributes.getQName(i).indexOf(":") + 1);
                        
                        if (!"schemaLocation".equals(tagName) || !"http://www.w3.org/2001/XMLSchema-instance".equals(namespace))
                        {
                            attributeMap.put( namespace + ":" + tagName , attributes.getValue(i));
                        }
                    }
                }
                else
                {
                    attributeMap.put(attributes.getQName(i), attributes.getValue(i));
                }
                
            }
            if (name.contains(":"))
            {
                xmlNode = new XmlNode(attributeMap, name.substring(name.indexOf(":") + 1), getNamespaces().get(name.substring(0, name.indexOf(":"))));
            }
            else
            {
                xmlNode = new XmlNode(attributeMap, name, null);
            }
            Node other = firstXmlHandler.nodeList.poll();
            
            if (!xmlNode.equals(other))
            {
                errors.add("Difference at " + stack.toString() + ": " + other + " != " + xmlNode);
            }
        }

    }
    
    private interface Node
    {
        public boolean equals(Object other);
    }
    
    private class XmlNode implements Node
    {
        private Map<String, String> attributes;
        private String name;
        private String namespace;
        
        public XmlNode(Map<String, String> attributes, String name, String namespace)
        {
            this.attributes = attributes;
            this.name = name;
            this.namespace = namespace;
        }

        @Override
        public boolean equals(Object other)
        {
            if (other == null || !(other instanceof XmlNode))
            {
                return false;
            }
            else
            {
                for (String name : attributes.keySet())
                {
                    if (!name.startsWith("xmlns:") && !name.equals("xsi") && !attributes.get(name).equals(((XmlNode)other).attributes.get(name)))
                    {
                        return false;
                    }
                }

                for (String name : ((XmlNode)other).attributes.keySet())
                {
                    if (!name.startsWith("xmlns:") && !((XmlNode)other).attributes.get(name).equals(attributes.get(name)))
                    {
                        return false;
                    }
                }
                
                boolean x1 = (name == null ? ((XmlNode)other).name == null : name.equals(((XmlNode)other).name));
                boolean x2 = (namespace == null ? ((XmlNode)other).namespace == null : namespace.equals(((XmlNode)other).namespace));
                
                return (x1 && x2);
            }
        }
        
    }
    
    private class TextNode implements Node
    {
        private String content;

        public TextNode(String content)
        {
            this.content = content;
        }

        @Override
        public boolean equals(Object other)
        {
            if (other == null || !(other instanceof TextNode))
            {
                return false;
            }
            else if (content == null)
            {
                return (((TextNode)other).content == null);
            }
            else if (content.matches("^\\s*$"))
            {
                return (((TextNode)other).content == null || ((TextNode)other).content.matches("^\\s*$"));
            }
            else
            {
                return content.equals(((TextNode)other).content);
            }
        }
        
    }
}
