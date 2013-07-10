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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.pubman.installer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.escidoc.www.services.sm.AggregationDefinitionHandler;
import de.escidoc.www.services.sm.ReportDefinitionHandler;
import de.mpg.escidoc.pubman.installer.util.Utils;
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
   /** Map of key - value pairs containing the filter definition */
    private HashMap<String, String[]> filterMap = new HashMap<String, String[]>();
    
    
    
    private static final String OBJECTID_SUBSTITUTE_IDENTIFIER = "template_objectid_substituted_by_installer";
    private static final String CONTEXTID_SUBSTITUTE_IDENTIFIER = "template_contextid_substituted_by_installer";
    
    public InitialDataset()
    {       
    }

    public InitialDataset(URL frameworkUrl, String username, String password) throws ServiceException, IOException, URISyntaxException
    {
        logger = Logger.getLogger(Installer.class);
        logger.info("FrameworkURL '" + frameworkUrl +"'");
        logger.info("username '" + username +"'" + " password '" + password +"'");
               
        this.frameworkUrl = frameworkUrl;
        this.userHandle = AdminHelper.loginUser(username, password);
        logger.info("Connection to coreservice '" + frameworkUrl.toString() +"' established, using handle" +
        		" '" + userHandle + "'.");
    }

   
    
    public String createContentModel(String fileName) throws Exception
    {
        String cmXml = Utils.getResourceAsXml(fileName);
        
        String cmTitle = Utils.getValueFromXml("<prop:name>", '<', cmXml);
        String cmDescription = Utils.getValueFromXml("<prop:description>", '<', cmXml);
        
        // filter for content model's name and description 
        filterMap.clear();
        filterMap.put(Utils.OPERATION, new String[]{Utils.SEARCH_RETRIEVE});
        filterMap.put(Utils.VERSION, new String[]{"1.1"});
        filterMap.put(Utils.QUERY, new String[]{"\"/properties/name\"=" + cmTitle + " and " 
                                + "\"/properties/description\"=" + "\"" + cmDescription + "\""});       

        String cms = ServiceLocator.getContentModelHandler(userHandle, frameworkUrl).retrieveContentModels(filterMap);        
        String numberOfCms = Utils.getValueFromXml("<sru-zr:numberOfRecords>", '<', cms);
        
        if (!numberOfCms.equals("0"))
        {
            String cmId = Utils.getValueFromXml("objid=\"", cms);
            logger.info("Content Model with name '" + cmTitle + "' already exists. Returning objid = " + cmId);
            return cmId;
        }
        
        String frameworkReturnXml =
            ServiceLocator.getContentModelHandler(userHandle, frameworkUrl).create(cmXml);
        if(frameworkReturnXml == null) {
            throw new Exception("content-model creation error");
        }
        logger.info("Creation data from framework: " + frameworkReturnXml);
        String objectId = Utils.getValueFromXml("objid=\"", frameworkReturnXml);
        String lastmodDate = "<param last-modification-date=\""+ Utils.getValueFromXml("last-modification-date=\"", frameworkReturnXml)+"\"/>";
        logger.info("Created content-model with last modification-date: " + lastmodDate);     
        logger.info("Created content-model with objectid: " + objectId);

        return objectId;        
    }
    
    /**
     * If an organization with the same name and comment already exists, the id is returned, otherwise the ou is created and the id returned.
     * @throws Exception
     */
    public String createAndOpenOrganizationalUnit(String fileName) throws Exception
    {
        String orgXml = Utils.getResourceAsXml(fileName);
        
        String ouTitle = Utils.getValueFromXml("<dc:title>", '<', orgXml);
        String ouDescription = Utils.getValueFromXml("<dc:description>", '<', orgXml);
        
        // filter for ou's name and description
        filterMap.clear();
        filterMap.put(Utils.OPERATION, new String[]{Utils.SEARCH_RETRIEVE});
        filterMap.put(Utils.VERSION, new String[]{"1.1"});
        filterMap.put(Utils.QUERY, new String[]{"\"/properties/name\"=" + ouTitle + " and " 
                                + "\"/properties/description\"=" + "\"" + ouDescription + "\""});       

        String ous = ServiceLocator.getOrganizationalUnitHandler().retrieveOrganizationalUnits(filterMap);        
        String numberOfOus = Utils.getValueFromXml("<sru-zr:numberOfRecords>", '<', ous);
        
        if (!numberOfOus.equals("0"))
        {
            String ouId = Utils.getValueFromXml("objid=\"", ous);
            logger.info("Organizational Unit with name '" + ouTitle + "' already exists. Returning objid = " + ouId);
            return ouId;
        }

        String frameworkReturnXml =
            ServiceLocator.getOrganizationalUnitHandler(userHandle, frameworkUrl).create(orgXml);
        if(frameworkReturnXml == null) 
        {
            throw new Exception("org-unit creation error");
        }
        logger.info("Creation data from framework: " + frameworkReturnXml);
        String objectId = Utils.getValueFromXml("objid=\"", frameworkReturnXml);
        String lastmodDate = "<param last-modification-date=\"" + Utils.getValueFromXml("last-modification-date=\"", frameworkReturnXml) + "\"/>";
        logger.info("Created org-unit with last modification-date: " + lastmodDate);     
        logger.info("Created org-unit with objectid: " + objectId);
        
        String openxml = ServiceLocator.getOrganizationalUnitHandler(userHandle, frameworkUrl).open(objectId, lastmodDate);
        
        logger.info("Opened org-unit, returned xml: " + openxml);
        return objectId;      
    }
    
    public String createAndOpenContext(String fileName, String orgObjectId) throws Exception
    {
        String contextXml = Utils.getResourceAsXml(fileName);
        String contextTitle = Utils.getValueFromXml("<prop:name>", '<', contextXml);
        
        // filter for context's name 
        filterMap.clear();
        filterMap.put(Utils.OPERATION, new String[]{Utils.SEARCH_RETRIEVE});
        filterMap.put(Utils.VERSION, new String[]{"1.1"});
        filterMap.put(Utils.QUERY, new String[]{"\"/properties/name\"=\"" + contextTitle + "\""});       

        String contexts = ServiceLocator.getContextHandler(userHandle, frameworkUrl).retrieveContexts(filterMap);        
        String numberOfContexts = Utils.getValueFromXml("<sru-zr:numberOfRecords>", '<', contexts);
        
        if (numberOfContexts.equals("1"))
        {
            String contextId = Utils.getValueFromXml("objid=\"", contexts);
            logger.info("Context with name '" + contextTitle + "' already exists. Returning objid = " + contextId);
            return contextId;
        }
        contextXml = contextXml.replaceAll(OBJECTID_SUBSTITUTE_IDENTIFIER, orgObjectId);
       
        String frameworkReturnXml =
            ServiceLocator.getContextHandler(userHandle, frameworkUrl).create(contextXml);
        if(frameworkReturnXml == null) {
            throw new Exception("context creation error");
        }
        logger.info("Creation data from framework: " + frameworkReturnXml);
        String objectId = Utils.getValueFromXml("objid=\"", frameworkReturnXml);
        String lastmodDate = "<param last-modification-date=\"" + Utils.getValueFromXml("last-modification-date=\"", frameworkReturnXml)+"\"/>";
        logger.info("Created context with last modification-date: " + lastmodDate);     
        logger.info("Created context with objectid: " + objectId);
        
        String openxml = ServiceLocator.getContextHandler(userHandle, frameworkUrl).open(objectId, lastmodDate);
        
        logger.info("Opened context, returned xml: " + openxml);
        return objectId;
    }
    
    public String createUser(String fileName, String password, String orgObjectId, String contextObjectId) throws Exception
    {
        String frameworkReturnXml = null;
        String userId = null;
        boolean created = false;
        
        String userXml = Utils.getResourceAsXml(fileName);        
        String loginName = Utils.getValueFromXml("<prop:login-name>", '<', userXml);
        
        // filter for users's login name 
        filterMap.clear();
        filterMap.put(Utils.OPERATION, new String[]{Utils.SEARCH_RETRIEVE});
        filterMap.put(Utils.VERSION, new String[]{"1.1"});
        filterMap.put(Utils.QUERY, new String[]{"\"/properties/login-name\"=\"" + loginName + "\""});       

        String users = ServiceLocator.getUserAccountHandler(userHandle, frameworkUrl).retrieveUserAccounts(filterMap);        
        String numberOfUsers = Utils.getValueFromXml("<zs:numberOfRecords>", '<', users);
        
        if (numberOfUsers.equals("1"))
        {
            userId = Utils.getValueFromXml("objid=\"", users);
            logger.info("User with login-name '" + loginName + "' already exists: objid = " + userId);
            
            frameworkReturnXml =
                    ServiceLocator.getUserAccountHandler(userHandle, frameworkUrl).retrieve(userId);
        }
        else 
        {
            userXml = userXml.replaceAll(OBJECTID_SUBSTITUTE_IDENTIFIER, orgObjectId);
            
            frameworkReturnXml =
                ServiceLocator.getUserAccountHandler(userHandle, frameworkUrl).create(userXml);
            userId = Utils.getValueFromXml("objid=\"", frameworkReturnXml);
            created = true;
            logger.info("User with login-name '" + loginName + "' created: objid = " + userId);
        }
        
        // set or modify password
        String lastmodDate = "<param last-modification-date=\""
            + Utils.getValueFromXml("last-modification-date=\"", frameworkReturnXml)
            + "\">"
            + "\n<password>"+password+"</password>"
            + "</param>";
        
        ServiceLocator.getUserAccountHandler(userHandle, frameworkUrl).updatePassword(userId, lastmodDate);
        logger.info("Passwort set / modified for user with login-name '" + loginName + "'");
        
        //set grant if user has been created
        if (!created)
        {
            return userId;
        }
        if (loginName.equals("pubman_moderator"))
        {
            this.createGrantForUser("datasetObjects/grant_moderator.xml", userId, contextObjectId);
        }
        if (loginName.equals("pubman_depositor"))
        {
            this.createGrantForUser("datasetObjects/grant_depositor.xml", userId, contextObjectId);
        }
        return userId;
    }
    
    public String createGrantForUser( String fileName, String userObjectId, String contextId ) throws Exception
    {
        String grantXml = Utils.getResourceAsXml(fileName);
        grantXml = grantXml.replaceAll(CONTEXTID_SUBSTITUTE_IDENTIFIER, contextId);
        
        String frameworkReturnXml =
            ServiceLocator.getUserAccountHandler(userHandle, frameworkUrl).createGrant(userObjectId, grantXml);
        
        String objectId = Utils.getValueFromXml("objid=\"", frameworkReturnXml);
        return objectId;
    }
    
    public String retrieveContentModel(String contentModelId) throws Exception
    {
        String frameworkReturnXml =
            ServiceLocator.getContentModelHandler(userHandle, frameworkUrl).retrieve(contentModelId);
        return frameworkReturnXml;
    }
    
    public String getHandle()
    {
        return userHandle;
    }
    
    public String createAggregation(String fileName) throws Exception
    {
        String aggregationXml = Utils.getResourceAsXml(fileName);
        AggregationDefinitionHandler aggrHandler = ServiceLocator.getAggregationDefinitionHandler();
        String createdAggr = aggrHandler.create(aggregationXml);
        return Utils.getValueFromXml("objid", createdAggr);
    }
    
    public String createReportDefinition(String fileName, String aggregationId) throws Exception
    {
        String repDefXml = Utils.getResourceAsXml(fileName);
        repDefXml = repDefXml.replace("###aggrId###", aggregationId);
        ReportDefinitionHandler repDefHandler = ServiceLocator.getReportDefinitionHandler(userHandle);
        String createdRepDef = repDefHandler.create(repDefXml);
        return Utils.getValueFromXml("objid", createdRepDef);
        
        
    }
}
