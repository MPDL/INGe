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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.common.util;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * SAX handler to write an XML that is identical to the input XML.
 * Might be useful to make small modifications in an XML.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 1743 $ $LastChangedDate: 2009-03-25 11:12:45 +0100 (Mi, 25 Mrz 2009) $
 *
 */
public class IdentityHandler extends ShortContentHandler
{
    private StringWriter result = new StringWriter();
    protected Map<String, String> nameSpaces = new HashMap<String, String>();
    protected String defaultNameSpace = null;
    protected int length = 0;
    
    public String getResult()
    {
        return result.toString();
    }
    
    /**
     * Appends something to the result.
     * 
     * @param str The string to append
     */
    public void append(String str)
    {
        result.append(str);
        this.length += str.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String name)
        throws SAXException
    {
        super.endElement(uri, localName, name);
        
        result.append("</");
        this.length += 2;
        result.append(name);
        this.length += name.length();
        result.append(">");
        this.length += 1;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException
    {

        super.startElement(uri, localName, name, attributes);

        result.append("<");
        this.length += 1;
        result.append(name);
        this.length += name.length();
        for (int i = 0; i < attributes.getLength(); i++)
        {

            result.append(" ");
            this.length += 1;
            result.append(attributes.getQName(i));
            this.length += attributes.getQName(i).length();
            result.append("=\"");
            this.length += 2;
            result.append(escape(attributes.getValue(i)));
            this.length += escape(attributes.getValue(i)).length();
            result.append("\"");
            this.length += 1;
        }
        result.append(">");
        this.length += 1;
    }
    
    /**
     * Returns an XML-escaped String that can be used for writing an XML.
     * 
     * @param input A string
     * @return The XML-escaped string 
     */
    public String escape(String input)
    {
        if (input != null)
        {
            input = input.replace("&", "&amp;");
            input = input.replace("<", "&lt;");
            input = input.replace("\"", "&quot;");
        }
        return input;
    }
    
    /** 
     * {@inheritDoc}
     */
    @Override
    public void content(String uri, String localName, String name, String content)
    {
        super.content(uri, localName, name, content);
        result.append(escape(content));
        this.length += escape(content).length();
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void processingInstruction(String name, String params) throws SAXException
    {
        super.processingInstruction(name, params);
        result.append("<?");
        this.length += 2;
        result.append(name);
        this.length += name.length();
        result.append(" ");
        this.length += 1;
        result.append(params);
        this.length += params.length();
        result.append("?>");
        this.length += 2;
    }
    
    public int getResultLength()
    {
        return this.length;
    }
}
