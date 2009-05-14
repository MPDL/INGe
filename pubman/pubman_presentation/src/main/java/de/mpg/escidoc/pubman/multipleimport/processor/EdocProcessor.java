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

package de.mpg.escidoc.pubman.multipleimport.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.axis.encoding.Base64;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.common.util.IdentityHandler;

/**
 * Format processor for eDoc XML files.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 2605 $ $LastChangedDate: 2009-05-07 10:38:22 +0200 (Do, 07 Mai 2009) $
 *
 */
public class EdocProcessor extends FormatProcessor
{
    
    private boolean init = false;
    private List<String> items = new ArrayList<String>();
    private int counter = -1;
    private int length = -1;
    private byte[] originalData = null;
    
    /**
     * {@inheritDoc}
     */
    public boolean hasNext()
    {
        if (!init)
        {
            initialize();
        }
        return (this.originalData != null && this.counter < this.length);
    }

    /**
     * {@inheritDoc}
     */
    public String next() throws NoSuchElementException
    {
        if (!init)
        {
            initialize();
        }
        if (this.originalData != null && this.counter < this.length)
        {
            return items.get(counter++);
        }
        else
        {
            throw new NoSuchElementException("No more entries left");
        }
        
    }

    /**
     * Not implemented.
     */
    @Deprecated
    public void remove()
    {
        throw new RuntimeException("Method not implemented");
    }

    private void initialize()
    {
        init = true;

        try
        {

            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            EdocHandler edocHandler = new EdocHandler();
            parser.parse(getSource(), edocHandler);
            
            this.originalData = edocHandler.getResult().getBytes(getEncoding());
            
            this.length = this.items.size();
            
            counter = 0;
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error reading input stream", e);
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLength()
    {
        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDataAsBase64()
    {
        if (this.originalData == null)
        {
            return null;
        }
        else
        {
            return Base64.encode(this.originalData);
        }
    }
    
    /**
     * SAX parser to extract the items out of the XML.
     *
     * @author franke (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public class EdocHandler extends IdentityHandler
    {

        private StringBuilder builder;
        private boolean inItem = false;
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
        {
            if ("edoc".equals(getStack().toString()))
            {
                this.builder = new StringBuilder();
                inItem = true;
            }
            super.startElement(uri, localName, name, attributes);
            
            if (inItem)
            {
                this.builder.append("<");
                this.builder.append(name);
                for (int i = 0; i < attributes.getLength(); i++)
                {

                    this.builder.append(" ");
                    this.builder.append(attributes.getQName(i));
                    this.builder.append("=\"");
                    this.builder.append(escape(attributes.getValue(i)));
                    this.builder.append("\"");
                }
                this.builder.append(">");
            }
            
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void endElement(String uri, String localName, String name) throws SAXException
        {
            super.endElement(uri, localName, name);
            
            if (inItem)
            {
                this.builder.append("</");
                this.builder.append(name);
                this.builder.append(">");
            }
            
            if ("edoc".equals(getStack().toString()))
            {
                items.add(this.builder.toString());
                this.builder = null;
                inItem = false;
            }
        }
        
        /** 
         * {@inheritDoc}
         */
        @Override
        public void content(String uri, String localName, String name, String content)
        {
            super.content(uri, localName, name, content);
            if (inItem)
            {
                this.builder.append(escape(content));
            }
        }
    }
    
}
