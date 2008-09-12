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

package de.mpg.escidoc.services.edoc;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ImportHandler extends DefaultHandler
{

    private static final String ITEM_NAMESPACE = "http://www.escidoc.de/schemas/item/0.7";
    StringWriter newXml = new StringWriter();
    Map<String, String> nameSpaces = new HashMap<String, String>();
    String defaultNameSpace = null;
    boolean inItem = false;

    List<String> items = new ArrayList<String>();

    public List<String> getItems()
    {
        return items;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException
    {
        nameSpaces.put(prefix, uri);
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (inItem)
        {
            String string = new String(ch, start, length);
            newXml.append(escape(string));
        }
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {

        if (inItem)
        {
            newXml.append("</");
            newXml.append(name);
            newXml.append(">");
        }
        
        String[] tag = name.split(":");
        String prefix = "";
        String tagName = null;
        if (tag.length == 2)
        {
            prefix = tag[0];
            tagName = tag[1];
        }
        else
        {
            tagName = tag[0];
        }
        if ("ei:item".equals(name))
        {
            inItem = false;
            items.add(newXml.toString());
            newXml = null;
        }

    }

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {

        for (int i = 0; i < attributes.getLength(); i++)
        {
            if (attributes.getQName(i).startsWith("xmlns:"))
            {
                nameSpaces.put(attributes.getQName(i).split(":")[1], attributes.getValue(i));
            }
            else if (attributes.getQName(i).equals("xmlns"))
            {
                defaultNameSpace = attributes.getValue(i);
            }
        }
        
        String[] tag = name.split(":");
        String prefix = "";
        String tagName = null;
        if (tag.length == 2)
        {
            prefix = tag[0];
            tagName = tag[1];
        }
        else
        {
            tagName = tag[0];
        }
        if ("ei:item".equals(name))
        {
            inItem = true;
            
            newXml = new StringWriter();
            
            newXml.append("<");
            newXml.append(name);
            for (int i = 0; i < attributes.getLength(); i++) {
                if (!attributes.getQName(i).startsWith("xmlns"))
                {
                    newXml.append(" ");
                    newXml.append(attributes.getQName(i));
                    newXml.append("=\"");
                    newXml.append(escape(attributes.getValue(i)));
                    newXml.append("\"");
                }
            }
            
            if (defaultNameSpace != null)
            {
                newXml.append(" xmlns=\"");
                newXml.append(escape(defaultNameSpace));
                newXml.append("\"");
            }
            
            for (String nameSpace : nameSpaces.keySet())
            {
                newXml.append(" xmlns:");
                newXml.append(nameSpace);
                newXml.append("=\"");
                newXml.append(escape(nameSpaces.get(nameSpace)));
                newXml.append("\"");
            }
            
            newXml.append(">");
        }
        else if (inItem)
        {
            newXml.append("<");
            newXml.append(name);
            for (int i = 0; i < attributes.getLength(); i++) {
                if (!attributes.getQName(i).startsWith("xmlns"))
                {
                    newXml.append(" ");
                    newXml.append(attributes.getQName(i));
                    newXml.append("=\"");
                    newXml.append(escape(attributes.getValue(i)));
                    newXml.append("\"");
                }
            }
            newXml.append(">");
        }
    }
    
    public String escape(String input)
    {
        if(input != null){
            input = input.replace("&", "&amp;");
            input = input.replace("<", "&lt;");
            input = input.replace("\"", "&quot;");
        }
        return input;
    }

        
}
