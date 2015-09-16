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
* or http://www.escidoc.org/license.
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
    
    private Type objectType = Type.UNKNOWN;
    private String publicStatus = "";
    
    private String actualRelsExtId = "";
    private String lastCreatedRelsExtId = "";
    private String PID = "";
    
    private Set<String> lastComponents = new HashSet<String>();
    
    private boolean inRelsExt = false;
    private boolean inRelsExtAndPublicStatus = false;
    private boolean inRelsExtAndVersionStatus = false;
    private boolean inRelsExtAndReleaseNumber = false;
    private boolean inRelsExtAndVersionNumber = false;
    
    
    // Map to hold the elements of a single RELS_EXT rdf:Description
    protected Map<String, Set<String>> tmpAttributeMap = new HashMap<String, Set<String>>();
    
    // Map to hold the elements of a all RELS_EXT rdf:Descriptions
    // key: RELS_EX_ID, value: the AttributeMap from above, these are the elements from this special RELS_EXT rdf:Description 
    protected Map<String, Map<String, Set<String>>> globalAttributeMap = new TreeMap<String, Map<String, Set<String>>>();
   
    private StringBuffer currentContent;    
        
    public enum Type { ITEM, COMPONENT, CONTEXT, CONTENTMODEL, ORGANIZATIONALUNIT, UNKNOWN }
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
            actualRelsExtId = attributes.getValue("ID");
            if (actualRelsExtId != null)
            {
            	Map<String, Set<String>> h = new HashMap<String, Set<String>>();
            	tmpAttributeMap = h;
  
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
        else if ("srel:component".equals(qName) && inRelsExt)
        {          
            String component = attributes.getValue("rdf:resource"); 
            logger.debug("srelComponent =<" + component + ">");
            
            if (tmpAttributeMap.get("srel:component") != null)
            {
            	tmpAttributeMap.get("srel:component").add(component);
            }
            else
            {
            	Set<String> s = new HashSet<String>();
            	s.add(component);
            	tmpAttributeMap.put("srel:component", s);
            }
            lastComponents = tmpAttributeMap.get("srel:component");
            
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
            PID = attributes.getValue("PID");
        }
        
        currentContent = new StringBuffer();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("endElement   uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + ">");
        if ("foxml:datastreamVersion".equals(qName) && inRelsExt)
        {
        	if (isToStore())
        	{
        		globalAttributeMap.put(actualRelsExtId, tmpAttributeMap);
        	}           
            lastCreatedRelsExtId = actualRelsExtId;
        } else if ("foxml:datastream".equals(qName) && inRelsExt)
        {
            inRelsExt = false;
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
        
        currentContent = null;
    }

	@Override
    public final void characters(char[] ch, int start, int length) throws SAXException
    {   
        if (currentContent == null)
            return;

        currentContent.append(ch, start, length);
        
        Set<String> s = new HashSet<String>();
        s.add(currentContent.toString());
        
        if (inRelsExtAndPublicStatus)
        {
            tmpAttributeMap.put("prop:public-status", s);
            logger.debug("publicStatus =<" + currentContent.toString() + ">");
        }  
        else if (inRelsExtAndVersionStatus)
        {
            tmpAttributeMap.put("version:status", s);
            logger.debug("versionStatus =<" + currentContent.toString() + ">");
        }    
        else if (inRelsExtAndReleaseNumber)
        {
            tmpAttributeMap.put("release:number", s);
            logger.debug("releaseNumber =<" + currentContent.toString() + ">");
        }  
        else if (inRelsExtAndVersionNumber)
        {
            tmpAttributeMap.put("version:number", s);
            logger.debug("versionNumber =<" + currentContent.toString() + ">");
        }   
    }

    public Type getObjectType()
    {
        logger.debug("getObjectType returning = " + objectType);        
        return objectType;
    }
    
    public String getPublicStatus()
    {
        return getPublicStatus(lastCreatedRelsExtId);
    }
    
    public String getVersionStatus()
    {
        return getVersionStatus(lastCreatedRelsExtId);
    }
    
    public String getPublicStatus(String relsExtId)
    {
        String status = (globalAttributeMap.get(relsExtId) != null ? globalAttributeMap.get(relsExtId).get("prop:public-status").iterator().next() : "");
                
        return status;
    }
    
    public String getVersionStatus(String relsExtId)
    {
        String status = (String) (globalAttributeMap.get(relsExtId) != null ? globalAttributeMap.get(relsExtId).get("version:status").iterator().next() : "");
                
        return status;
    }
    
    public String getReleaseNumber()
    {
        return getReleaseNumber(lastCreatedRelsExtId);
    }
    
    public String getReleaseNumber(String relsExtId)
    {
        return (String) (globalAttributeMap.get(relsExtId) != null ? globalAttributeMap.get(relsExtId).get("release:number").iterator().next() : "");
                
    }
    
    public String getVersionNumber()
    {
        return getVersionNumber(lastCreatedRelsExtId);
    }
    
    public String getVersionNumber(String relsExtId)
    {
        return (String) (globalAttributeMap.get(relsExtId) != null
                ? globalAttributeMap.get(relsExtId).get("version:number").iterator().next() : "");
    }
    
    public String getSrelComponent(String relsExtId)
    {
        return (String) (globalAttributeMap.get(relsExtId) != null
                ? globalAttributeMap.get(relsExtId).get("srel:component").toArray().toString() : "");
    }
    
    public Set<String> getSrelComponent()
    {
        return (globalAttributeMap.get(lastCreatedRelsExtId) != null
                ? globalAttributeMap.get(lastCreatedRelsExtId).get("srel:component") : new HashSet<String>());
    }
   
    public String getEscidocId()
    {
        return PID;
    }
    
    public boolean isObjectPidToInsert(String actRelsExtId)
    {
        return (globalAttributeMap.get(actRelsExtId).get("propPidExists") != null 
                && globalAttributeMap.get(actRelsExtId).get("propPidExists").equals("false")); 
    }
    
    public Map<String, Set<String>> getAttributeMapFor(String relsExtId)
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
        else if (type.endsWith("OrganizationalUnit"))
        {
            return objectType = Type.ORGANIZATIONALUNIT;
        }
        
        return Type.UNKNOWN;
    }
    
    private boolean isToStore()
	{
    	Set<String> versionStatus = tmpAttributeMap.get("version:status");
    	String s = versionStatus.iterator().next();
		if (s == null || !"released".equals(s))
			return false;
		
		Set<String> srelComponent = tmpAttributeMap.get("srel:component");
		if (srelComponent == null || srelComponent.isEmpty())
			return false;
		// check internal external managed
		// TODO
		return true;
	}
}
