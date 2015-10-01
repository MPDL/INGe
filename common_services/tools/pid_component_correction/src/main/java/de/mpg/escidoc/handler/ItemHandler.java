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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.util.LocationHelper;
import de.mpg.escidoc.util.Util;

/**
 * TODO Description
 *
 * @author sieders (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ItemHandler extends DefaultHandler
{
	public static final String SREL_COMPONENT_KEY = "srel:component";
	public static final String VERSION_NUMBER_KEY = "version:number";
    public static final String RELEASE_NUMBER_KEY = "release:number";
    public static final String VERSION_STATUS_KEY = "version:status";
    public static final String PROP_PUBLIC_STATUS_KEY = "prop:public-status";

	private static Logger logger = Logger.getLogger(ItemHandler.class);
    
    private Type objectType = Type.UNKNOWN;
    
    private String actualRelsExtId = "";
    private String PID = "";
    private String oldVersionStatus = "";
    private String actualVersionStatus = "";
    private boolean inRelsExt = false;
    private boolean inRelsExtAndPublicStatus = false;
    private boolean inRelsExtAndVersionStatus = false;
    private boolean inRelsExtAndReleaseNumber = false;
    private boolean inRelsExtAndVersionNumber = false;
    
    
    
    // Map to hold the elements of a single RELS_EXT rdf:Description element
    private Map<String, Set<String>> tmpElementMap = new HashMap<String, Set<String>>();
    
    // Map to hold the elements of a all RELS_EXT rdf:Descriptions elements which concern the component pid update
    // key: RELS_EX_ID, value: the elementMap from above, these are the elements from this special RELS_EXT rdf:Description 
    private Map<String, Map<String, Set<String>>> globalElementMap = new TreeMap<String, Map<String, Set<String>>>();
   
    // Map to hold the complete component data as component pid and file name for one single item
    private Map<String, Map<String, String>> componentMap = new HashMap<String, Map<String, String>>();
    
    private StringBuffer currentContent;   
    
    private ComponentHandler componentHandler;
        
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
            	if (tmpElementMap.get(VERSION_STATUS_KEY) != null && tmpElementMap.get(VERSION_STATUS_KEY).iterator().next().length() > 1)
            	{
            		oldVersionStatus = tmpElementMap.get(VERSION_STATUS_KEY).iterator().next();
            	}
            	tmpElementMap = h;
  
            }
        }
        else if (PROP_PUBLIC_STATUS_KEY.equals(qName) && inRelsExt)
        {
            inRelsExtAndPublicStatus = true;
        }
        else if (VERSION_STATUS_KEY.equals(qName) && inRelsExt)
        {
            inRelsExtAndVersionStatus = true;
        }
        else if (RELEASE_NUMBER_KEY.equals(qName) && inRelsExt)
        {
            inRelsExtAndReleaseNumber = true;
        }
        else if (VERSION_NUMBER_KEY.equals(qName) && inRelsExt)
        {
            inRelsExtAndVersionNumber = true;
        }
        else if (SREL_COMPONENT_KEY.equals(qName) && inRelsExt)
        {          
            String componentId = attributes.getValue("rdf:resource"); 
            logger.debug("srelComponent =<" + componentId + ">");
            
            if (tmpElementMap.get(SREL_COMPONENT_KEY) != null)
            {
            	tmpElementMap.get(SREL_COMPONENT_KEY).add(componentId);
            }
            else
            {
            	Set<String> s = new HashSet<String>();
            	s.add(componentId);
            	tmpElementMap.put(SREL_COMPONENT_KEY, s);
            }
            
            // parse corresponding component foxml file
            SAXParser parser = null;
            componentHandler = new ComponentHandler();
            
			try
			{
				parser = SAXParserFactory.newInstance().newSAXParser();
				parser.parse(getComponentFile(componentId), componentHandler);
			} catch (Exception e)
			{
				logger.warn("Sax parser exception occured", e);
			}
			
			componentMap.put(componentId, componentHandler.getComponentMap());            
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
        		globalElementMap.put(actualRelsExtId, tmpElementMap);
        	}           
        } else if ("foxml:datastream".equals(qName) && inRelsExt)
        {
            inRelsExt = false;
        } 
        else if (PROP_PUBLIC_STATUS_KEY.equals(qName))
        {
            inRelsExtAndPublicStatus = false;
        }
        else if (VERSION_STATUS_KEY.equals(qName))
        {
            inRelsExtAndVersionStatus = false;
        }
        else if (RELEASE_NUMBER_KEY.equals(qName))
        {
            inRelsExtAndReleaseNumber = false;
        }
        else if (VERSION_NUMBER_KEY.equals(qName))
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
            tmpElementMap.put(PROP_PUBLIC_STATUS_KEY, s);
            logger.debug("publicStatus =<" + currentContent.toString() + ">");
        }  
        else if (inRelsExtAndVersionStatus)
        {
            tmpElementMap.put(VERSION_STATUS_KEY, s);
            actualVersionStatus = currentContent.toString();
            logger.debug("versionStatus =<" + currentContent.toString() + ">");
        }    
        else if (inRelsExtAndReleaseNumber)
        {
            tmpElementMap.put(RELEASE_NUMBER_KEY, s);
            logger.debug("releaseNumber =<" + currentContent.toString() + ">");
        }  
        else if (inRelsExtAndVersionNumber)
        {
            tmpElementMap.put(VERSION_NUMBER_KEY, s);
            logger.debug("versionNumber =<" + currentContent.toString() + ">");
        }   
    }

    public Type getObjectType()
    {
        logger.debug("getObjectType returning = " + objectType);        
        return objectType;
    }
    
    public String getPublicStatus(String relsExtId)
    {
        String status = (globalElementMap.get(relsExtId) != null ? globalElementMap.get(relsExtId).get(PROP_PUBLIC_STATUS_KEY).iterator().next() : "");
                
        return status;
    }
    
    public String getVersionStatus(String relsExtId)
    {
        String status = (String) (globalElementMap.get(relsExtId) != null ? globalElementMap.get(relsExtId).get(VERSION_STATUS_KEY).iterator().next() : "");
                
        return status;
    }
    
 
    public String getReleaseNumber(String relsExtId)
    {
        return (String) (globalElementMap.get(relsExtId) != null ? globalElementMap.get(relsExtId).get(RELEASE_NUMBER_KEY).iterator().next() : "");
                
    }
    
    public String getVersionNumber(String relsExtId)
    {
        return (String) (globalElementMap.get(relsExtId) != null
                ? globalElementMap.get(relsExtId).get(VERSION_NUMBER_KEY).iterator().next() : "");
    }
    
    public Set<String> getSrelComponent(String relsExtId)
    {
        return (globalElementMap.get(relsExtId) != null
                ? globalElementMap.get(relsExtId).get(SREL_COMPONENT_KEY) : new HashSet<String>());
    }
   
    public String getEscidocId()
    {
        return PID;
    }
    
    public boolean isObjectPidToInsert(String actRelsExtId)
    {
        return (globalElementMap.get(actRelsExtId).get("propPidExists") != null 
                && globalElementMap.get(actRelsExtId).get("propPidExists").equals("false")); 
    }
    
    public Map<String, Set<String>> getElementMapFor(String relsExtId)
    {
        return this.globalElementMap.get(relsExtId);
    }
    
    public Map<String, Map<String, Set<String>>> getGlobalElementMap()
    {
    	return this.globalElementMap;
    }
    
    public Map<String, String> getComponentMap(String componentId)
    {
    	return componentMap.get(componentId);
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
    
    // store in if version:status = released
    // if there exists an internal managed component
    // if the previous:version status has been != released
    private boolean isToStore()
	{
    	if (tmpElementMap.size() == 0)
    		return false;
    	
		if (actualVersionStatus == null || !"released".equals(actualVersionStatus))
			return false;
		
		Set<String> srelComponent = tmpElementMap.get(SREL_COMPONENT_KEY);
		if (srelComponent == null || srelComponent.isEmpty())
			return false;
		// TO DO check internal external managed
		
		
		if ("released".equals(actualVersionStatus) && !"released".equals(oldVersionStatus))
			return true;
		
		return false;
	}
    
    private InputStream getComponentFile(String componentId)
	{
    	String l = LocationHelper.getLocation(Util.getPureComponentId(componentId));
    	try
		{
			return new FileInputStream(new File(LocationHelper.getLocation(Util.getPureComponentId(componentId))));
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
	}
}
