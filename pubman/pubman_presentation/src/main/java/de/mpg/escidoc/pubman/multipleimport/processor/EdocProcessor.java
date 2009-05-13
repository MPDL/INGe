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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.axis.encoding.Base64;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.common.util.IdentityHandler;

/**
 * TODO Description
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
    
    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        if (!init)
        {
            initialize();
        }
        return (this.originalData != null && this.counter < this.length);
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
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

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
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
        catch (Exception e) {
            throw new RuntimeException("Error reading input stream", e);
        }
        
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.multipleimport.processor.FormatProcessor#getLength()
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
    
    public class EdocHandler extends IdentityHandler
    {

        private int start = -1;
        
        @Override
        public void endElement(String uri, String localName, String name) throws SAXException
        {
            super.endElement(uri, localName, name);
            if ("edoc".equals(getStack().toString()))
            {
                String record = getResult().substring(this.start);
                items.add(record);
                this.start = -1;
            }
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
        {
            if ("edoc".equals(getStack().toString()))
            {
                this.start = getResultLength();
            }
            super.startElement(uri, localName, name, attributes);
        }
        
    }
    
}
