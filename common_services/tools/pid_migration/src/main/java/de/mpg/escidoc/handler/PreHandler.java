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
    private String lastCreatedRelsExtId = "";
    
    private String lastCreatedDCTimestamp = "";
    private String lastCreatedDCId = "";
    
    private Type objectType = Type.UNKNOWN;
    private String publicStatus = "";
    
    private boolean inRelsExt = false;
    private boolean inRelsExtAndPublicStatus = false;
    private boolean inRelsExtAndVersionStatus = false;
    private boolean inRelsExtAndReleaseNumber = false;
    private boolean inRelsExtAndVersionNumber = false;
    
    private boolean inDC = false;
    private boolean inDCAndTitle = false;
    
    // Map to hold the elements of a single RELS_EXT rdf:Description
    private Map<String, String> tmpAttributeMap = new HashMap<String, String>();
    
    // Map to hold the elements of a all RELS_EXT rdf:Descriptions
    // key: RELS_EX_ID, value: the AttributeMap from above, these are the elements from this special RELS_EXT rdf:Description 
    private Map<String, Map<String, String>> globalAttributeMap = new HashMap<String, Map<String, String>>();
   
    private StringBuffer currentContent;    
        
    public enum Type { ITEM, COMPONENT, CONTEXT, CONTENTMODEL, UNKNOWN }
    public enum Status { PENDING, SUBMITTED, RELEASED, WITHDRAWN, INREVISION, UNKNOWN }
    
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
                
                // we keep the data of the previous RELS-EXT rdf:description and overwrite it with the actual one, 
                // because not all elements occur in each rdf:description
                Map<String, String> h = new HashMap<String, String>(tmpAttributeMap);
                tmpAttributeMap = h;
                
                logger.debug("startElement actualRelsExtId = " + lastCreatedRelsExtId);               
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
        else if ("rdf:type".equals(qName))
        {
            String type = attributes.getValue("rdf:resource");
            
            if (type != null )
            {
                objectType = getObjectType(type);
            }
        }
        // escidoc id
        else if ("foxml:digitalObject".equals(qName))
        {
            String id = attributes.getValue("PID");
            
            tmpAttributeMap.put("id", id);
        }
        
        currentContent = new StringBuffer();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("endElement   uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + ">");
        if ("foxml:datastreamVersion".equals(qName) && inRelsExt)
        {
            globalAttributeMap.put(lastCreatedRelsExtId, tmpAttributeMap);
        } else if ("foxml:datastream".equals(qName) && inRelsExt)
        {
            inRelsExt = false;
        } 
        else if ("foxml:datastream".equals(qName) && inDC)
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
            tmpAttributeMap.put("prop:public-status", currentContent.toString());
            logger.debug("publicStatus =<" + currentContent.toString() + ">");
        }  
        else if (inRelsExtAndVersionStatus)
        {
            currentContent.append(ch, start, length);
            tmpAttributeMap.put("version:status", currentContent.toString());
            logger.debug("versionStatus =<" + currentContent.toString() + ">");
        }    
        else if (inRelsExtAndReleaseNumber)
        {
            currentContent.append(ch, start, length);
            tmpAttributeMap.put("release:number", currentContent.toString());
            logger.debug("releaseNumber =<" + currentContent.toString() + ">");
        }  
        else if (inRelsExtAndVersionNumber)
        {
            currentContent.append(ch, start, length);
            tmpAttributeMap.put("version:number", currentContent.toString());
            logger.debug("versionNumber =<" + currentContent.toString() + ">");
        }   
        else if (inDCAndTitle)
        {
            currentContent.append(ch, start, length);
            tmpAttributeMap.put("dc:title", currentContent.toString());
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

    public Type getObjectType()
    {
        logger.debug("getObjectType returning = " + objectType);        
        return objectType;
    }
    
    public Status getPublicStatus()
    {
        return getPublicStatus(lastCreatedRelsExtId);
    }
    
    public Status getVersionStatus()
    {
        return getVersionStatus(lastCreatedRelsExtId);
    }
    
    public Status getPublicStatus(String relsExtId)
    {
        String status = globalAttributeMap.get(relsExtId).get("prop:public-status") != null ? globalAttributeMap
                .get(relsExtId).get("prop:public-status") : "";
        return getStatus(status);
    }
    
    public Status getVersionStatus(String relsExtId)
    {
        String status = globalAttributeMap.get(relsExtId).get("version:status") != null ? globalAttributeMap
                .get(relsExtId).get("version:status") : "";
        return getStatus(status);
    }
    
    public String getTitle()
    {
        return globalAttributeMap.get(lastCreatedRelsExtId).get("dc:title") != null
                ? globalAttributeMap.get(lastCreatedRelsExtId).get("dc:title") : "";
    }
    
    public String getReleaseNumber()
    {
        return getReleaseNumber(lastCreatedRelsExtId);
    }
    
    public String getVersionNumber()
    {
        return getVersionNumber(lastCreatedRelsExtId);
    }
    
    public String getReleaseNumber(String relsExtId)
    {
        return globalAttributeMap.get(relsExtId).get("release:number") != null
                ? globalAttributeMap.get(relsExtId).get("release:number") : "";
    }
    
    public String getVersionNumber(String relsExtId)
    {
        return globalAttributeMap.get(relsExtId).get("version:number") != null
                ? globalAttributeMap.get(relsExtId).get("version:number") : "";
    }
    
    public String getEscidocId()
    {
        return globalAttributeMap.get(lastCreatedRelsExtId).get("id");
    }
    
    public Map<String, String> getAttributeMapFor(String relsExtId)
    {
        return globalAttributeMap.get(relsExtId);
    }
    
    private Type getObjectType(String type)
    {
        if (type.endsWith("Component"))
        {
            return objectType = Type.COMPONENT;
        } 
        else if (type.endsWith("Item"))
        {
            return objectType = Type.ITEM;
        }
        else if (type.endsWith("Context"))
        {
            return objectType = Type.CONTEXT;
        }
        else if (type.endsWith("ContentModel"))
        {
            return objectType = Type.CONTENTMODEL;
        }
        return Type.UNKNOWN;
    }

    Status getStatus(String value)
    {
        if (value.equals("released"))
            return Status.RELEASED;
        else if (value.equals("pending"))
            return Status.PENDING;
        else if (value.equals("submitted"))
            return Status.SUBMITTED;
        else if (value.equals("in-revision"))
            return Status.INREVISION;
        else if (value.equals("withdrawn"))
            return Status.WITHDRAWN;
        
        return Status.UNKNOWN;
    }
}
