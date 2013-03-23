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

package de.mpg.escidoc.handler;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * TODO Description
 *
 * @author sieders (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class PreHandler extends DefaultHandler
{
    private static Logger logger = Logger.getLogger(PreHandler.class);

    private String lastCreatedRelsExtTimestamp = "1990-01-01T00:00:00.000Z";
    private String lastCreatedRelsExtId = null;
    private Type objectType = null;
    
    private String lastVersionHistoryTimeStamp = "1990-01-01T00:00:00.000Z";
    
    private boolean inRelsExt = false;
    
    public enum Type { ITEM, COMPONENT, CONTEXT, CONTENTMODEL }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        logger.info("startElement uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + "> attributes = <" + attributes + ">");
        
        if ("foxml:datastream".equals(qName) && "RELS-EXT".equals(attributes.getValue("ID")))
        { 
            inRelsExt = true;
            logger.info(" startElement inRelsExt= " + inRelsExt);
        }
        else if ("foxml:datastreamVersion".equals(qName) && inRelsExt)
        {
            String createdString = attributes.getValue("CREATED");
            if (createdString != null && createdString.compareTo(lastCreatedRelsExtTimestamp) > 0)
            {
                lastCreatedRelsExtTimestamp = createdString;
                lastCreatedRelsExtId = attributes.getValue("ID");
                
                logger.info("startElement lastCreatedRelsExtTimeStamp = " + lastCreatedRelsExtTimestamp);               
            }
        }
        else if ("escidocVersions:pid".equals(qName))
        {
            String timestamp = attributes.getValue("timestamp");
            if (timestamp != null && timestamp.compareTo(lastVersionHistoryTimeStamp) > 0)
            {
                lastVersionHistoryTimeStamp = timestamp;
                
                logger.info("startElement lastVersionHistoryTimeStamp = " + lastVersionHistoryTimeStamp);               
            }
        }
        else if ("foxml:property".equals(qName) && "info:fedora/fedora-system:def/model#label".equals(attributes.getValue("NAME")))
        {
            String type = attributes.getValue("VALUE");
            
            if (type != null )
            {
                if (type.startsWith("Component"))
                {
                    objectType = Type.COMPONENT;
                } 
                else if (type.startsWith("Item"))
                {
                    objectType = Type.ITEM;
                }
                else if (type.startsWith("Context"))
                {
                    objectType = Type.CONTEXT;
                }
                else if (type.contains("Content"))
                {
                    objectType = Type.CONTENTMODEL;
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.info("endElement   uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + ">");
        if ("foxml:datastream".equals(localName))
        {
            inRelsExt = false;
            logger.info(" endElement inRelsExt= " + inRelsExt);
        }
    }

    public String getLastCreatedRelsExtId()
    {
        logger.info("getLastCreatedRelsExtId returning = " + lastCreatedRelsExtId);        
        return lastCreatedRelsExtId;
    }
    
    public String getLastCreatedRelsExtTimestamp()
    {
        logger.info("getLastCreatedRelsExtTimestamp returning = " + lastCreatedRelsExtTimestamp);        
        return lastCreatedRelsExtTimestamp;
    }
    
    public String getLastVersionHistoryTimestamp()
    {
        logger.info("getLastVersionHistoryTimestamp returning = " + lastVersionHistoryTimeStamp);        
        return lastVersionHistoryTimeStamp;
    }
    
    public Type getObjectType()
    {
        logger.info("getObjectType returning = " + objectType);        
        return objectType;
    }
    
    
}
