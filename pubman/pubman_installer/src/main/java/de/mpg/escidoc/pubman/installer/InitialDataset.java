/*
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
 * Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.pubman.installer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.escidoc.www.services.sm.AggregationDefinitionHandler;
import de.escidoc.www.services.sm.ReportDefinitionHandler;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * @author endres
 * 
 */
public class InitialDataset
{
    /** logging instance */
    private Logger logger = null;
    /** URL of the framework instance */
    private URL frameworkUrl = null;
    /** user handle from framework */
    private String userHandle = null;
    /** number of tokens needed by the login method */
    private static final int NUMBER_OF_URL_TOKENS = 2;
    
    private static final String OBJECTID_SUBSTITUTE_IDENTIFIER = "template_objectid_substituted_by_installer";
    private static final String CONTEXTID_SUBSTITUTE_IDENTIFIER = "template_contextid_substituted_by_installer";
        
    public InitialDataset() {
        
    }

    public InitialDataset(URL frameworkUrl, String username, String password) throws ServiceException, IOException, URISyntaxException
    {
        logger = Logger.getLogger(Installer.class);
        this.frameworkUrl = frameworkUrl;
        this.userHandle = AdminHelper.loginUser(username, password);
        logger.info("Connection to coreservice <" + frameworkUrl.toString() +"> established, using handle" +
        		" <" + userHandle + ">.");
    }

    public String getResourceAsXml(final String fileName) throws FileNotFoundException, Exception
    {
        StringBuffer buffer = new StringBuffer();
        InputStream is = null;
        BufferedReader br = null;
        String line;

        try
        {
            is = getClass().getClassLoader().getResourceAsStream(fileName);
            br = new BufferedReader(new InputStreamReader(is));
            while (null != (line = br.readLine()))
            {
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (br != null)
                    br.close();
                if (is != null)
                    is.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return buffer.toString();
    }
    public String createContentModel(String fileName) throws Exception
    {
        String cmXml = getResourceAsXml(fileName);
        String frameworkReturnXml =
            ServiceLocator.getContentModelHandler(userHandle, frameworkUrl).create(cmXml);
        if(frameworkReturnXml == null) {
            throw new Exception("content-model creation error");
        }
        logger.info("Creation data from framework: " + frameworkReturnXml);
        String objectId = getValueFromXml("objid=\"", frameworkReturnXml);
        String lastmodDate = "<param last-modification-date=\""+getValueFromXml("last-modification-date=\"", frameworkReturnXml)+"\"/>";
        logger.info("Created content-model with last modification-date: " + lastmodDate);     
        logger.info("Created content-model with objectid: " + objectId);

        return objectId;
        
    }
    
    public String createAndOpenOrganizationalUnit(String fileName) throws Exception
    {
        String orgXml = getResourceAsXml(fileName);
        String frameworkReturnXml =
            ServiceLocator.getOrganizationalUnitHandler(userHandle, frameworkUrl).create(orgXml);
        if(frameworkReturnXml == null) {
            throw new Exception("org-unit creation error");
        }
        logger.info("Creation data from framework: " + frameworkReturnXml);
        String objectId = getValueFromXml("objid=\"", frameworkReturnXml);
        String lastmodDate = "<param last-modification-date=\""+getValueFromXml("last-modification-date=\"", frameworkReturnXml)+"\"/>";
        logger.info("Created org-unit with last modification-date: " + lastmodDate);     
        logger.info("Created org-unit with objectid: " + objectId);
        
        String openxml = ServiceLocator.getOrganizationalUnitHandler(userHandle, frameworkUrl).open(objectId, lastmodDate);
        
        logger.info("Opened org-unit, returned xml: " + openxml);
        return objectId;
        
    }
    
    public String createAndOpenContext(String fileName, String orgObjectId) throws Exception
    {
        String contextXml = getResourceAsXml(fileName);
        contextXml = contextXml.replaceAll(OBJECTID_SUBSTITUTE_IDENTIFIER, orgObjectId);
       
        String frameworkReturnXml =
            ServiceLocator.getContextHandler(userHandle, frameworkUrl).create(contextXml);
        if(frameworkReturnXml == null) {
            throw new Exception("context creation error");
        }
        logger.info("Creation data from framework: " + frameworkReturnXml);
        String objectId = getValueFromXml("objid=\"", frameworkReturnXml);
        String lastmodDate = "<param last-modification-date=\""+getValueFromXml("last-modification-date=\"", frameworkReturnXml)+"\"/>";
        logger.info("Created context with last modification-date: " + lastmodDate);     
        logger.info("Created context with objectid: " + objectId);
        
        String openxml = ServiceLocator.getContextHandler(userHandle, frameworkUrl).open(objectId, lastmodDate);
        
        logger.info("Opened context, returned xml: " + openxml);
        return objectId;
    }
    
    public String createUser( String fileName, String password, String orgObjectId) throws Exception
    {
        String userXml = getResourceAsXml(fileName);
        userXml = userXml.replaceAll(OBJECTID_SUBSTITUTE_IDENTIFIER, orgObjectId);
       
        String frameworkReturnXml =
            ServiceLocator.getUserAccountHandler(userHandle, frameworkUrl).create(userXml);
        if(frameworkReturnXml == null) {
            throw new Exception("context creation error");
        }
        String objectId = getValueFromXml("objid=\"", frameworkReturnXml);
        String lastmodDate = "<param last-modification-date=\""
            +getValueFromXml("last-modification-date=\"", frameworkReturnXml)
            +"\">"
            +"\n<password>"+password+"</password>"
            +"</param>";
        
        ServiceLocator.getUserAccountHandler(userHandle, frameworkUrl).updatePassword(objectId, lastmodDate);
        
        logger.info("Creation data from framework: " + frameworkReturnXml);
        return objectId;
    }
    
    public String createGrantForUser( String fileName, String userObjectId, String contextId ) throws Exception
    {
        String grantXml = getResourceAsXml(fileName);
        grantXml = grantXml.replaceAll(CONTEXTID_SUBSTITUTE_IDENTIFIER, contextId);
        
        String frameworkReturnXml =
            ServiceLocator.getUserAccountHandler(userHandle, frameworkUrl).createGrant(userObjectId, grantXml);
        if(frameworkReturnXml == null) {
            throw new Exception("context creation error");
        }
        String objectId = getValueFromXml("objid=\"", frameworkReturnXml);
        return objectId;
    }
    
    public String retrieveContentModel(String contentModelId) throws Exception
    {
        String frameworkReturnXml =
            ServiceLocator.getContentModelHandler(userHandle, frameworkUrl).retrieve(contentModelId);
        return frameworkReturnXml;
    }
    
    
    
    /**
     * Search the given String for the first occurence of "objid" and return its value.
     * 
     * @param item A (XML) String
     * @return The objid value
     */
    private String getValueFromXml(String key, String item)
    {
        String result = "";
        String searchString = key;
        int index = item.indexOf(searchString);
        if (index > 0)
        {
            item = item.substring(index + searchString.length());
            index = item.indexOf('\"');
            if (index > 0)
            {
                result = item.substring(0, index);
            }
        }
        return result;
    }  
    
    public String getHandle()
    {
        return userHandle;
    }
    
    public String createAggregation(String fileName) throws Exception
    {
        String aggregationXml = getResourceAsXml(fileName);
        AggregationDefinitionHandler aggrHandler = ServiceLocator.getAggregationDefinitionHandler();
        String createdAggr = aggrHandler.create(aggregationXml);
        return getValueFromXml("objid", createdAggr);
    }
    
    public String createReportDefinition(String fileName, String aggregationId) throws Exception
    {
        String repDefXml = getResourceAsXml(fileName);
        repDefXml = repDefXml.replace("###aggrId###", aggregationId);
        ReportDefinitionHandler repDefHandler = ServiceLocator.getReportDefinitionHandler(userHandle);
        String createdRepDef = repDefHandler.create(repDefXml);
        return getValueFromXml("objid", createdRepDef);
        
        
    }
}
