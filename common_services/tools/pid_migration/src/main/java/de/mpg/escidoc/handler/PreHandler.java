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

    private String lastCreatedRelsExtTimestamp = "";
    private String lastCreatedRelsExtId = null;
    private Type objectType = null;
    private String publicStatus = "";
    
    private String lastVersionHistoryTimeStamp = "";
    
    private boolean inRelsExt = false;
    private boolean inRelsExtAndPublicStatus = false;
    // to get the publicStatus in the last RELS-EXT
    private boolean lastRelsExtModified = false; 
    
    private StringBuffer currentContent;
        
    public enum Type { ITEM, COMPONENT, CONTEXT, CONTENTMODEL, UNKNOWN }
    public enum PublicStatus { PENDING, SUBMITTED, RELEASED, WITHDRAWN, UNKNOWN }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        logger.debug("startElement uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + "> attributes = <" + attributes + ">");
        
        if ("foxml:datastream".equals(qName) && "RELS-EXT".equals(attributes.getValue("ID")))
        { 
            inRelsExt = true;
            logger.debug(" startElement inRelsExt= " + inRelsExt);
        }
        else if ("foxml:datastreamVersion".equals(qName) && inRelsExt)
        {
            String createdString = attributes.getValue("CREATED");
            if (createdString != null && createdString.compareTo(lastCreatedRelsExtTimestamp) > 0)
            {
                lastCreatedRelsExtTimestamp = createdString;
                lastCreatedRelsExtId = attributes.getValue("ID");
                lastRelsExtModified = true;
                
                logger.debug("startElement lastCreatedRelsExtTimeStamp = " + lastCreatedRelsExtTimestamp);               
            }
            else
            {
                lastRelsExtModified = false;
            }
        }
        else if ("prop:public-status".equals(qName) && lastRelsExtModified)
        {
            inRelsExtAndPublicStatus = true;
        }
        else if ("escidocVersions:pid".equals(qName))
        {
            String timestamp = attributes.getValue("timestamp");
            if (timestamp != null && timestamp.compareTo(lastVersionHistoryTimeStamp) > 0)
            {
                lastVersionHistoryTimeStamp = timestamp;
                
                logger.debug("startElement lastVersionHistoryTimeStamp = " + lastVersionHistoryTimeStamp);               
            }
        }
        else if ("foxml:property".equals(qName) && "info:fedora/fedora-system:def/model#label".equals(attributes.getValue("NAME")))
        {
            String type = attributes.getValue("VALUE");
            
            if (type != null )
            {
                getObjectType(type);
            }
        }
        
        currentContent = new StringBuffer();
    }

    private Type getObjectType(String type)
    {
        if (type.startsWith("Component"))
        {
            return objectType = Type.COMPONENT;
        } 
        else if (type.startsWith("Item"))
        {
            return objectType = Type.ITEM;
        }
        else if (type.startsWith("Context"))
        {
            return objectType = Type.CONTEXT;
        }
        else if (type.contains("Content"))
        {
            return objectType = Type.CONTENTMODEL;
        }
        return Type.UNKNOWN;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("endElement   uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + ">");
        if ("foxml:datastream".equals(qName))
        {
            inRelsExt = false;
        }
        if ("prop:public-status".equals(qName))
        {
            inRelsExtAndPublicStatus = false;
        }
        currentContent = null;
    }
    
    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException
    {
        logger.debug("characters   start=<" + start + "> length = <" + length + ">");
        
        if (currentContent != null && inRelsExtAndPublicStatus)
        {
            currentContent.append(ch, start, length);
            publicStatus = currentContent.toString();
            logger.info("publicStatus =<" + publicStatus + ">");
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
    
    public PublicStatus getPublicStatus()
    {
        logger.info("getPublicStatus returning = " + publicStatus);  
        
        if (publicStatus.equalsIgnoreCase("pending"))
            return PublicStatus.PENDING;
        else if (publicStatus.equalsIgnoreCase("submitted"))
            return PublicStatus.SUBMITTED;
        else if (publicStatus.equalsIgnoreCase("released"))
            return PublicStatus.RELEASED;
        else if (publicStatus.equalsIgnoreCase("withdrawn"))
            return PublicStatus.WITHDRAWN;
        
        return PublicStatus.UNKNOWN;
    }
    
    
}
