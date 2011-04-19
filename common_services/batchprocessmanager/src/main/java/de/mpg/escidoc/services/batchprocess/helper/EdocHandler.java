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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.batchprocess.helper;

import java.io.StringWriter;
import java.util.HashMap;
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
public class EdocHandler extends DefaultHandler
{

    Map<String, Map<String, String>> fileMap = new HashMap<String, Map<String,String>>();
    
    String item;
    String filename;
    StringWriter url;
    Map<String, String> itemMap = new HashMap<String, String>();
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if ("record".equals(qName))
        {
            item = attributes.getValue("id");
        }
        else if ("fturl".equals(qName))
        {
            filename = attributes.getValue("filename");
            url = new StringWriter();
        }
        
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if ("record".equals(qName))
        {
            fileMap.put(item, itemMap);
            item = null;
        }
        else if ("fturl".equals(qName))
        {
            itemMap.put(filename, url.toString());
            url = null;
            filename = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        if (url != null)
        {
            url.write(ch, start, length);
        }
    }

    public Map<String, Map<String, String>> getFileMap()
    {
        return fileMap;
    }

}
