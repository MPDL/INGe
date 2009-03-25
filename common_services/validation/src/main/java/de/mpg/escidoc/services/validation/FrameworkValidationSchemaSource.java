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

package de.mpg.escidoc.services.validation;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.WrongDateException;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.util.ShortContentHandler;
import de.mpg.escidoc.services.validation.util.CacheTuple;

/**
 * This implementation of {@link ValidationSchemaSource} retrieves Validation schema items from the framework.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class FrameworkValidationSchemaSource extends ShortContentHandler implements ValidationSchemaSource
{
    private static final Logger logger = Logger.getLogger(FrameworkValidationSchemaSource.class);
    
    private String targetContentModel;
    private String schemaName;
    private StringBuffer schema;
    private Date modificationDate;
    
    /**
     * {@inheritDoc}
     */
    public Map<CacheTuple, String> retrieveNewSchemas(Date lastUpdate) throws Exception
    {
        Map<CacheTuple, String> result = new HashMap<CacheTuple, String>();
        String contentModel = PropertyReader.getProperty("escidoc.validation.content-model");
        int offset = 0;
        
        ItemHandler itemHandler = ServiceLocator.getItemHandler();
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        
        while (true)
        {
            String filter = "<param><filter name=\"http://escidoc.de/core/01/structural-relations/content-model\">" + contentModel + "</filter><order-by sorting=\"descending\">http://escidoc.de/core/01/properties/creation-date</order-by><limit>1</limit><offset>" + offset + "</offset></param>";
            String oneItem = itemHandler.retrieveItems(filter);
            
            resetHandler();
            parser.parse(new ByteArrayInputStream(oneItem.getBytes("UTF-8")), this);
            
            if (modificationDate != null && (lastUpdate == null || modificationDate.getTime() > lastUpdate.getTime()))
            {
                result.put(new CacheTuple(targetContentModel, schemaName), schema.toString());
                offset++;
            }
            else
            {
                break;
            }
        }

        return result;
    }

    private void resetHandler()
    {
        targetContentModel = null;
        schemaName = null;
        schema = null;
        modificationDate = null;
    }

    @Override
    public void content(String uri, String localName, String name, String content)
    {
        super.content(uri, localName, name, content);
        
        if ("item-list/item/properties/content-model-specific/usage-info/name".equals(getLocalStack().toString()))
        {
            schemaName = content;
        }
        else if ("item-list/item/properties/content-model-specific/usage-info/target-content-model".equals(getLocalStack().toString()))
        {
            targetContentModel = content;
        }
        else if (getLocalStack().toString().startsWith("item-list/item/md-records/md-record/schema"))
        {
            schema.append(encodeContent(content));
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException
    {
        super.endElement(uri, localName, name);
        String lName;
        if (name.contains(":"))
        {
            lName = name.substring(name.indexOf(":") + 1);
        }
        else
        {
            lName = name;
        }
        if (getLocalStack().toString().startsWith("item-list/item/md-records/md-record/schema") || ("item-list/item/md-records/md-record".equals(getLocalStack().toString()) && "schema".equals(lName)))
        {
            schema.append("</");
            schema.append(name);
            schema.append(">");
        }
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
    {
        super.startElement(uri, localName, name, attributes);
        if ("item-list/item".equals(getLocalStack().toString()))
        {
            try
            {
                modificationDate = JiBXHelper.deserializeDate(attributes.getValue("last-modification-date"));
            }
            catch (WrongDateException e) {
                throw new SAXException("Error parsing modification date: " + attributes.getValue("last-modification-date"));
            }
        }
        else if ("item-list/item/md-records/md-record/schema".equals(getLocalStack().toString()))
        {
            schema = new StringBuffer();
            schema.append("<");
            schema.append(name);
            for (String prefix : getNamespaces().keySet())
            {
                schema.append(" xmlns:");
                schema.append(prefix);
                schema.append("=\"");
                schema.append(getNamespaces().get(prefix));
                schema.append("\"");
            }
            for (int i = 0; i < attributes.getLength(); i++)
            {
                if (!attributes.getQName(i).startsWith("xmlns:"))
                {
                    schema.append(" ");
                    schema.append(attributes.getQName(i));
                    schema.append("=\"");
                    schema.append(encodeAttribute(attributes.getValue(i)));
                    schema.append("\"");
                }
            }
            schema.append(">");
        }
        else if (getLocalStack().toString().startsWith("item-list/item/md-records/md-record/schema/"))
        {
            schema.append("<");
            schema.append(name);
            for (int i = 0; i < attributes.getLength(); i++)
            {
                schema.append(" ");
                schema.append(attributes.getQName(i));
                schema.append("=\"");
                schema.append(encodeAttribute(attributes.getValue(i)));
                schema.append("\"");
            }
            schema.append(">");
        }
    }
}
