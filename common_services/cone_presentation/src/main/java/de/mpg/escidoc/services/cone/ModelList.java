/*
 * CDDL HEADER START The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License. You can
 * obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for the
 * specific language governing permissions and limitations under the License. When distributing Covered Code, include
 * this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the
 * following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner] CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH
 * and Max-Planck- Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */
package de.mpg.escidoc.services.cone;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.services.common.util.ResourceUtil;

/**
 * A SAX parser that reads in the servieces.xml configuration file.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ModelList
{
    private static ModelList instance = null;
    private static final Logger logger = Logger.getLogger(ModelList.class);
    private Set<Model> list = new HashSet<Model>();

    private ModelList() throws Exception
    {
        InputStream in = ResourceUtil.getResourceAsStream("explain/models.xml");
        ServiceListHandler listHandler = new ServiceListHandler();
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(in, listHandler);
        list = listHandler.getList();
        logger.debug("Length: " + list.size());
        for (Model model : list)
        {
            logger.debug("Model:" + model.getName());
        }
    }

    /**
     * Returns the singleton.
     * 
     * @return The singleton
     */
    public static ModelList getInstance() throws Exception
    {
        if (instance == null)
        {
            instance = new ModelList();
        }
        return instance;
    }

    public Set<Model> getList()
    {
        return list;
    }

    /**
     * SAX handler.
     * 
     * @author franke (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    private class ServiceListHandler extends ShortContentHandler
    {
        private Set<Model> list = new HashSet<Model>();
        private Model currentService = null;

        @Override
        public void content(String uri, String localName, String name, String content)
        {
            if ("models/model/name".equals(stack.toString()))
            {
                currentService.setName(content);
            }
            else if ("models/model/description".equals(stack.toString()))
            {
                currentService.setDescription(content);
            }
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
        {
            super.startElement(uri, localName, name, attributes);
            if ("models/model".equals(stack.toString()))
            {
                currentService = new Model();
            }
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException
        {
            if ("models/model".equals(stack.toString()))
            {
                list.add(currentService);
            }
            super.endElement(uri, localName, name);
        }

        public Set<Model> getList()
        {
            return list;
        }
    }

    /**
     * Generic SAX handler with convenience methods. Useful for XML with only short string content. Classes that extend
     * this class should always call super() at the beginning of an overridden method.
     * 
     * @author franke (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    private class ShortContentHandler extends DefaultHandler
    {
        private StringBuffer currentContent;
        protected XMLStack stack = new XMLStack();

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
        {
            stack.push(name);
            currentContent = new StringBuffer();
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException
        {
            content(uri, localName, name, currentContent.toString());
            stack.pop();
        }

        @Override
        public final void characters(char[] ch, int start, int length) throws SAXException
        {
            currentContent.append(ch, start, length);
        }

        /**
         * Called when string content was found.
         * 
         * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace
         *            processing is not being performed.
         * @param localName The local name (without prefix), or the empty string if Namespace processing is not being
         *            performed.
         * @param name The qualified name (with prefix), or the empty string if qualified names are not available.
         * @param content The string content of the current tag.
         */
        public void content(String uri, String localName, String name, String content)
        {
            // Do nothing by default
        }

        /**
         * A {@link Stack} extension to facilitate XML navigation.
         * 
         * @author franke (initial creation)
         * @author $Author$ (last modification)
         * @version $Revision$ $LastChangedDate$
         */
        private class XMLStack extends Stack<String>
        {
            /**
             * Returns a String representation of the Stack in an XPath like way (e.g. "root/subtag/subsub"):
             */
            @Override
            public synchronized String toString()
            {
                StringWriter writer = new StringWriter();
                for (Iterator<String> iterator = this.iterator(); iterator.hasNext();)
                {
                    String element = (String) iterator.next();
                    writer.append(element);
                    if (iterator.hasNext())
                    {
                        writer.append("/");
                    }
                }
                return writer.toString();
            }
        }
    }

    /**
     * A bean holding data of a CoNE service.
     * 
     * @author franke (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    public class Model
    {
        private String name;
        private String description;

        /**
         * Default constructor.
         */
        public Model()
        {
        }

        /**
         * Constructor by name.
         * 
         * @param name The service name
         */
        public Model(String name)
        {
            this.name = name;
        }

        /**
         * Constructor by name and description.
         * 
         * @param name The service name
         * @param description The description
         */
        public Model(String name, String description)
        {
            this.name = name;
            this.description = description;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        /**
         * Compares to other objects.
         * 
         * @param object The object this object is compared to
         * @return true, if the other object is a {@link Model} with the same name.
         */
        @Override
        public boolean equals(Object object)
        {
            if (object instanceof Model)
            {
                if (object == null)
                {
                    return false;
                }
                else if (((Model) object).name == null)
                {
                    return (this.name == null);
                }
                else
                {
                    return (((Model) object).name.equals(this.name));
                }
            }
            else
            {
                return false;
            }
        }

        /**
         * Returns the hashCode of the service name. This is needed for using {@link HashSet}s correctly.
         * 
         * @return The hashCode
         */
        @Override
        public int hashCode()
        {
            return (this.name == null ? 0 : this.name.hashCode());
        }
    }
}
