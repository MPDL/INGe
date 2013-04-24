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

import java.util.HashMap;
import java.util.Map;

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
    
    private String lastCreatedDCTimestamp = "";
    private String lastCreatedDCId = null;
    
    private Type objectType = null;
    private String publicStatus = "";
    
    private String lastVersionHistoryTimeStamp = "";
    
    private boolean inRelsExt = false;
    private boolean inRelsExtAndPublicStatus = false;
    private boolean inRelsExtAndVersionStatus = false;
    private boolean inRelsExtAndReleaseNumber = false;
    private boolean inRelsExtAndVersionNumber = false;
    
    private boolean inDC = false;
    private boolean inDCAndTitle = false;
    
    private Map<String, String> attributeMap = new HashMap<String, String>();
   
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
            if (createdString != null && createdString.compareTo(lastCreatedRelsExtTimestamp) > 0 && !publicStatus.equals("released"))
            {
                lastCreatedRelsExtTimestamp = createdString;
                lastCreatedRelsExtId = attributes.getValue("ID");
                
                logger.debug("startElement lastCreatedRelsExtTimeStamp = " + lastCreatedRelsExtTimestamp);               
            }
        }
        else if ("foxml:datastream".equals(qName) && "DC".equals(attributes.getValue("ID")))
        {
            inDC = true;
        }
        else if ("foxml:datastreamVersion".equals(qName) && inDC)
        {
            String createdString = attributes.getValue("CREATED");
            if (createdString != null && createdString.compareTo(lastCreatedDCTimestamp) > 0)
            {
                lastCreatedDCTimestamp = createdString;
                lastCreatedDCId = attributes.getValue("ID");
                
                logger.debug("startElement lastCreatedDCTimeStamp = " + lastCreatedDCTimestamp);               
            }
        }
        else if ("prop:public-status".equals(qName) && inRelsExt)
        {
            inRelsExtAndPublicStatus = true;
        }
        else if ("version:status".equals(qName) && inRelsExt)
        {
            inRelsExtAndVersionStatus = true;
        }
        else if ("release:number".equals(qName) && inRelsExt)
        {
            inRelsExtAndReleaseNumber = true;
        }
        else if ("version:number".equals(qName) && inRelsExt)
        {
            inRelsExtAndVersionNumber = true;
        }
        else if ("dc:title".equals(qName) && inDC)
        {
            inDCAndTitle = true;
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
        // escidoc id
        else if ("foxml:digitalObject".equals(qName))
        {
            String id = attributes.getValue("PID");
            
            attributeMap.put("id", id);
        }
        
        currentContent = new StringBuffer();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("endElement   uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + ">");
        if ("foxml:datastream".equals(qName) && inRelsExt)
        {
            inRelsExt = false;
        } else if ("foxml:datastream".equals(qName) && inDC)
        {
            inDC = false;
        } 
        else if ("prop:public-status".equals(qName))
        {
            inRelsExtAndPublicStatus = false;
        }
        else if ("version:status".equals(qName))
        {
            inRelsExtAndVersionStatus = false;
        }
        else if ("release:number".equals(qName))
        {
            inRelsExtAndReleaseNumber = false;
        }
        else if ("version:number".equals(qName))
        {
            inRelsExtAndVersionNumber = false;
        }
        else if (inDC && "dc:title".equals(qName))
        {
            inDCAndTitle = false;
        }
        currentContent = null;
    }
    
    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException
    {   
        if (currentContent == null)
            return;
        if (inRelsExtAndPublicStatus)
        {
            currentContent.append(ch, start, length);
            attributeMap.put("prop:public-status", currentContent.toString());
            logger.debug("publicStatus =<" + currentContent.toString() + ">");
        }  
        else if (inRelsExtAndVersionStatus)
        {
            currentContent.append(ch, start, length);
            attributeMap.put("version:status", currentContent.toString());
            logger.debug("versionStatus =<" + currentContent.toString() + ">");
        }    
        else if (inRelsExtAndReleaseNumber)
        {
            currentContent.append(ch, start, length);
            attributeMap.put("release:number", currentContent.toString());
            logger.debug("releaseNumber =<" + currentContent.toString() + ">");
        }  
        else if (inRelsExtAndVersionNumber)
        {
            currentContent.append(ch, start, length);
            attributeMap.put("version:number", currentContent.toString());
            logger.debug("versionNumber =<" + currentContent.toString() + ">");
        }   
        else if (inDCAndTitle)
        {
            currentContent.append(ch, start, length);
            attributeMap.put("dc:title", currentContent.toString());
            logger.debug("title =<" + currentContent.toString() + ">");
        }   
    }

    public String getLastCreatedRelsExtId()
    {
        logger.debug("getLastCreatedRelsExtId returning = " + lastCreatedRelsExtId);        
        return lastCreatedRelsExtId;
    }
    
    public String getLastCreatedRelsExtTimestamp()
    {
        logger.debug("getLastCreatedRelsExtTimestamp returning = " + lastCreatedRelsExtTimestamp);        
        return lastCreatedRelsExtTimestamp;
    }
    
    public String getLastVersionHistoryTimestamp()
    {
        logger.debug("getLastVersionHistoryTimestamp returning = " + lastVersionHistoryTimeStamp);        
        return lastVersionHistoryTimeStamp;
    }
    
    public Type getObjectType()
    {
        logger.debug("getObjectType returning = " + objectType);        
        return objectType;
    }
    
    public PublicStatus getPublicStatus()
    {
        return getStatus("prop:public-status");
    }
    
    public PublicStatus getVersionStatus()
    {
        return getStatus("version:status");
    }
    
    public String getTitle()
    {
        return attributeMap.get("dc:title") != null ? attributeMap.get("dc:title") : "";
    }
    
    public String getReleaseNumber()
    {
        return attributeMap.get("release:number") != null ? attributeMap.get("release:number") : "";
    }
    
    public String getVersionNumber()
    {
        return attributeMap.get("version:number") != null ? attributeMap.get("version:number") : "";
    }
    
    public String getEscidocId()
    {
        return attributeMap.get("id");
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

    private PublicStatus getStatus(String key)
    {
        if (attributeMap.get(key).equals("released"))
            return PublicStatus.RELEASED;
        else if (attributeMap.get(key).equals("pending"))
            return PublicStatus.PENDING;
        else if (attributeMap.get(key).equals("submitted"))
            return PublicStatus.SUBMITTED;
        else if (attributeMap.get(key).equals("withdrawn"))
            return PublicStatus.WITHDRAWN;
        
        return PublicStatus.UNKNOWN;
    }
}
