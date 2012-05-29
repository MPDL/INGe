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
package de.mpg.escidoc.services.framework;

import gov.loc.www.zing.srw.service.ExplainPort;
import gov.loc.www.zing.srw.service.SRWPort;
import gov.loc.www.zing.srw.service.SRWSampleServiceLocator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.log4j.Logger;
import org.apache.ws.security.handler.WSHandlerConstants;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.aa.UserAccountHandlerServiceLocator;
import de.escidoc.www.services.aa.UserGroupHandler;
import de.escidoc.www.services.aa.UserGroupHandlerServiceLocator;
import de.escidoc.www.services.aa.UserManagementWrapper;
import de.escidoc.www.services.aa.UserManagementWrapperServiceLocator;
import de.escidoc.www.services.adm.AdminHandler;
import de.escidoc.www.services.adm.AdminHandlerServiceLocator;
import de.escidoc.www.services.cmm.ContentModelHandler;
import de.escidoc.www.services.cmm.ContentModelHandlerServiceLocator;
import de.escidoc.www.services.om.ContainerHandler;
import de.escidoc.www.services.om.ContainerHandlerServiceLocator;
import de.escidoc.www.services.om.ContextHandler;
import de.escidoc.www.services.om.ContextHandlerServiceLocator;
import de.escidoc.www.services.om.IngestHandler;
import de.escidoc.www.services.om.IngestHandlerServiceLocator;
import de.escidoc.www.services.om.ItemHandler;
import de.escidoc.www.services.om.ItemHandlerServiceLocator;
import de.escidoc.www.services.om.SemanticStoreHandler;
import de.escidoc.www.services.om.SemanticStoreHandlerServiceLocator;
import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.escidoc.www.services.oum.OrganizationalUnitHandlerServiceLocator;
import de.escidoc.www.services.sm.AggregationDefinitionHandler;
import de.escidoc.www.services.sm.AggregationDefinitionHandlerServiceLocator;
import de.escidoc.www.services.sm.ReportDefinitionHandler;
import de.escidoc.www.services.sm.ReportDefinitionHandlerServiceLocator;
import de.escidoc.www.services.sm.ReportHandler;
import de.escidoc.www.services.sm.ReportHandlerServiceLocator;
import de.escidoc.www.services.sm.ScopeHandler;
import de.escidoc.www.services.sm.ScopeHandlerServiceLocator;
import de.escidoc.www.services.sm.StatisticDataHandler;
import de.escidoc.www.services.sm.StatisticDataHandlerServiceLocator;
/**
 * This service locator has to be used for getting the handler of the framework services.<BR>
 * The URL of the framework can be configured using the system property "framework.url".
 * In an eclipse unit test this can be set as an vm argument e.g
 * -Dframework.url=http://130.183.251.122:8080/axis/services
 *
 * @author Peter Broszeit (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by FrW: 10.03.2008
 */
public class ServiceLocator
{
    private static final String CONFIGURATION_FILE = "client.wsdd";
    private static final String FRAMEWORK_PATH = "/axis/services";
    private static final String SRW_PATH = "/srw/search"; 
    
    // ServiceLocator objects connected to the standard famework
    private static final UserManagementWrapperServiceLocator authorizedUserManagementWrapperServiceLocator 
                                                = AuthorizedUserManagementWrapperServiceLocatorHolder.serviceLocator;
    private static final UserAccountHandlerServiceLocator authorizedUserAccountHandlerServiceLocator 
                                                = AuthorizedUserAccountHandlerServiceLocatorHolder.serviceLocator;
    private static final UserGroupHandlerServiceLocator authorizedUserGroupHandlerServiceLocator 
                                                = UserGroupHandlerServiceLocatorHolder.serviceLocator;
    private static final OrganizationalUnitHandlerServiceLocator authorizedOrganizationalUnitHandlerServiceLocator
                                                = OrganizationalUnitHandlerServiceLocatorHolder.serviceLocator;
    private static final ContentModelHandlerServiceLocator authorizedContentModelHandlerServiceLocator
                                                = ContentModelHandlerServiceLocatorHolder.serviceLocator;
    private static final ContextHandlerServiceLocator authorizedContextHandlerServiceLocator
                                                = ContextHandlerServiceLocatorHolder.serviceLocator;
    private static final ItemHandlerServiceLocator authorizedItemHandlerServiceLocator
                                                = ItemHandlerServiceLocatorHolder.serviceLocator;
    private static final ContainerHandlerServiceLocator authorizedContainerHandlerServiceLocator
                                                = ContainerHandlerServiceLocatorHolder.serviceLocator;
    private static final SemanticStoreHandlerServiceLocator authorizedSemanticScoreHandlerServiceLocator
                                                = SemanticStoreHandlerServiceLocatorHolder.serviceLocator;
    private static final ScopeHandlerServiceLocator publicScopeHandlerServiceLocator
                                                = ScopeHandlerServiceLocatorHolder.serviceLocator;
    private static final AggregationDefinitionHandlerServiceLocator  publicAggregationDefinitionHandlerServiceLocator
                                                = AggregationDefinitionHandlerServiceLocatorHolder.serviceLocator;
    private static final StatisticDataHandlerServiceLocator  publicStatisticDataHandlerServiceLocator
                                                = StatisticDataHandlerServiceLocatorHolder.serviceLocator;
    private static final ReportDefinitionHandlerServiceLocator  publicReportDefinitionHandlerServiceLocator
                                                = ReportDefinitionHandlerServiceLocatorHolder.serviceLocator;
    private static final ReportHandlerServiceLocator  publicReportHandlerServiceLocator
                                                = ReportHandlerServiceLocatorHolder.serviceLocator;
    private static final IngestHandlerServiceLocator authorizedIngestHandlerServiceLocator
                                                = IngestHandlerServiceLocatorHolder.serviceLocator;
    private static final AdminHandlerServiceLocator authorizedAdminHandlerServiceLocator
                                                = AdminHandlerServiceLocatorHolder.serviceLocator;
    
    // ServiceLocator objects connected to possibly changing or external frameworks
    private static volatile UserAccountHandlerServiceLocator extAuthorizedUserAccountHandlerServiceLocator 
                                                = ExtAuthorizedUserAccountHandlerServiceLocatorHolder.serviceLocator;
    private static volatile OrganizationalUnitHandlerServiceLocator extAuthorizedOrganizationalUnitHandlerServiceLocator 
                                                = ExtOrganizationalUnitHandlerServiceLocatorHolder.serviceLocator;
    private static volatile ContentModelHandlerServiceLocator extAuthorizedContentModelHandlerServiceLocator
                                                = ExtContentModelHandlerServiceLocatorHolder.serviceLocator;
    private static volatile ContextHandlerServiceLocator extAuthorizedContextHandlerServiceLocator
                                                = ExtContextHandlerServiceLocatorHolder.serviceLocator;
    private static volatile ItemHandlerServiceLocator extAuthorizedItemHandlerServiceLocator
                                                = ExtItemHandlerServiceLocatorHolder.serviceLocator;
    

    
    /**
     * Get the configured URL of the running framework instance.
     *
     * @return The url as a String.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static String getFrameworkUrl() throws ServiceException, URISyntaxException
    {
        String url;
        try
        {
            url = PropertyReader.getProperty("escidoc.framework_access.framework.url");
        }
        catch (IOException e)
        {
            throw new ServiceException(e);
        }
        return url;
    }

    public static String getLoginUrl() throws ServiceException, URISyntaxException
    {
        String url;
        try
        {
            url = PropertyReader.getProperty("escidoc.framework_access.login.url");
        }
        catch (IOException e)
        {
            throw new ServiceException(e);
        }
        return url;
    }
    
    /**
     * Gets the UserManagementWrapper service for an authenticated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A wrapper for the UserManagement.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static UserManagementWrapper getUserManagementWrapper(final String userHandle) throws ServiceException, URISyntaxException
    { 
        try
        {
            UserManagementWrapper handler = authorizedUserManagementWrapperServiceLocator.getUserManagementWrapperService();
            Logger.getLogger(ServiceLocator.class).info(
                    "authorizedUserManagementWrapperServiceLocator = " + authorizedUserManagementWrapperServiceLocator);
            ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
            return handler;
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
    }
    
    /**
     * Gets the UserAccountHandler service for an authenticated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A UserAccountHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static UserAccountHandler getUserAccountHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        UserAccountHandler handler = authorizedUserAccountHandlerServiceLocator.getUserAccountHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "authorizedUserAccountHandlerServiceLocator = " + authorizedUserAccountHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the UserAccountHandler service for an authenticated user and a specified framework URL
     *
     * @param userHandle The handle of the logged in user.
     * @param the framework URL to connect to.
     * @return A UserAccountHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static synchronized UserAccountHandler getUserAccountHandler(String userHandle, URL frameWorkURL) throws ServiceException, URISyntaxException
    {
        StringBuffer b = new StringBuffer(1024);
        b.append(frameWorkURL.toString()).append(FRAMEWORK_PATH).append("/").append(extAuthorizedUserAccountHandlerServiceLocator.getUserAccountHandlerServiceWSDDServiceName());
        extAuthorizedUserAccountHandlerServiceLocator.setUserAccountHandlerServiceEndpointAddress(b.toString());
        
        UserAccountHandler handler = extAuthorizedUserAccountHandlerServiceLocator.getUserAccountHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "extAuthorizedUserAccountHandlerServiceLocator = " + extAuthorizedUserAccountHandlerServiceLocator + " for " + frameWorkURL.toString());
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the UserGroupHandler service for an authenticated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A UserGroupHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static UserGroupHandler getUserGroupHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        UserGroupHandler handler = authorizedUserGroupHandlerServiceLocator.getUserGroupHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "authorizedUserGroupHandlerServiceLocator = " + authorizedUserGroupHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the OrganizationalUnitHandler service for an anonymous user.
     *
     * @return An OrganizationalUnitHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static OrganizationalUnitHandler getOrganizationalUnitHandler() throws ServiceException, URISyntaxException
    {       
         return getOrganizationalUnitHandler("");      
    }
    
    /**
     * Gets the OrganizationalUnitHandler service for an authenticated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return An OrganizationalUnitHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static OrganizationalUnitHandler getOrganizationalUnitHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        OrganizationalUnitHandler handler = authorizedOrganizationalUnitHandlerServiceLocator.getOrganizationalUnitHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "authorizedOrganizationalUnitHandlerServiceLocator = " + authorizedOrganizationalUnitHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the OrganizationalUnitHandler service for an authenticated user and a specified framework URL
     *
     * @param userHandle The handle of the logged in user.
     * @param the framework URL to connect to.
     * @return An OrganizationalUnitHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static synchronized OrganizationalUnitHandler getOrganizationalUnitHandler(String userHandle, URL frameWorkURL) throws ServiceException, URISyntaxException
    {
        StringBuffer b = new StringBuffer(1024);
        b.append(frameWorkURL.toString()).append(FRAMEWORK_PATH).append("/").append(extAuthorizedOrganizationalUnitHandlerServiceLocator.getOrganizationalUnitHandlerServiceWSDDServiceName());
        extAuthorizedOrganizationalUnitHandlerServiceLocator.setOrganizationalUnitHandlerServiceEndpointAddress(b.toString());
        
        OrganizationalUnitHandler handler = extAuthorizedOrganizationalUnitHandlerServiceLocator.getOrganizationalUnitHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "extAuthorizedOrganizationalUnitHandlerServiceLocator = " + extAuthorizedOrganizationalUnitHandlerServiceLocator + " for frameWork " + frameWorkURL.toString());
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the ContentModelHandler service for an authenticated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A ContentTypeHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ContentModelHandler getContentModelHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        ContentModelHandler handler = authorizedContentModelHandlerServiceLocator.getContentModelHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "authorizedContentModelHandlerServiceLocator = " + authorizedContentModelHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the ContentModelHandler service for an authenticated user connected to a specified framework
     *
     * @param userHandle The handle of the logged in user.
     * @param frameworkURL The URL of the framework  connect to.
     * @return A ContentHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static synchronized ContentModelHandler getContentModelHandler(String userHandle, URL frameWorkURL) throws ServiceException, URISyntaxException
    {
        StringBuffer b = new StringBuffer(1024);
        b.append(frameWorkURL.toString()).append(FRAMEWORK_PATH).append("/").append(extAuthorizedContentModelHandlerServiceLocator.getContentModelHandlerServiceWSDDServiceName());
        extAuthorizedContentModelHandlerServiceLocator.setContentModelHandlerServiceEndpointAddress(b.toString());
        
        ContentModelHandler handler = extAuthorizedContentModelHandlerServiceLocator.getContentModelHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "extAuthorizedContentModelHandlerServiceLocator = " + extAuthorizedContentModelHandlerServiceLocator + " for " + frameWorkURL.toString());
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the ContextHandler service for an anonymous user.
     *
     * @return An ContextHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ContextHandler getContextHandler() throws ServiceException, URISyntaxException
    {
        return getContextHandler("");
    }
    
    /**
     * Gets the ContextHandler service for an authenticated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A ContextHandler.
     * @throws MalformedURLException
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ContextHandler getContextHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        ContextHandler handler = authorizedContextHandlerServiceLocator.getContextHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "authorizedContextHandlerServiceLocator = " + authorizedContextHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the ContextHandler service for an authenticated user connected to a specified framework.
     *
     * @param userHandle The handle of the logged in user.
     * @param frameworkURL The URL of the framework  connect to.
     * @return A ContextHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static synchronized ContextHandler getContextHandler(String userHandle, URL frameWorkURL) throws ServiceException, URISyntaxException
    {
        StringBuffer b = new StringBuffer(1024);
        b.append(frameWorkURL.toString()).append(FRAMEWORK_PATH).append("/").append(extAuthorizedContextHandlerServiceLocator.getContextHandlerServiceWSDDServiceName());
        extAuthorizedContextHandlerServiceLocator.setContextHandlerServiceEndpointAddress(b.toString());
        
        ContextHandler handler = extAuthorizedContextHandlerServiceLocator.getContextHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "extAuthorizedContextHandlerServiceLocator = " + extAuthorizedContextHandlerServiceLocator + " for " + frameWorkURL.toString());
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the ItemHandler service for an anonymous user.
     *
     * @return An ItemHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ItemHandler getItemHandler() throws ServiceException, URISyntaxException
    {
        return getItemHandler("");
    }
    
    /**
     * Gets the ItemHandler service for an authenticated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return An ItemHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ItemHandler getItemHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        ItemHandler handler = authorizedItemHandlerServiceLocator.getItemHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "authorizedItemHandlerServiceLocator = " + authorizedItemHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets a special ItemHandler service for an authenticated user handling items in some external frameworks 
     *
     * @param userHandle The handle of the logged in user.
     * @return An ItemHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static synchronized ItemHandler getItemHandler(URL url) throws ServiceException, URISyntaxException
    {
        StringBuffer b = new StringBuffer(1024);
        b.append(url.toString()).append(FRAMEWORK_PATH).append("/").append(extAuthorizedItemHandlerServiceLocator.getItemHandlerServiceWSDDServiceName());
        
        extAuthorizedItemHandlerServiceLocator.setItemHandlerServiceEndpointAddress(b.toString());
        
        ItemHandler handler = extAuthorizedItemHandlerServiceLocator.getItemHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "extAuthorizedItemHandlerServiceLocator = " + extAuthorizedItemHandlerServiceLocator + " for " + url);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }
    
    /**
     * Gets the ContainerHandler service for an anonymous user.
     *
     * @return A ContainerHandler
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ContainerHandler getContainerHandler() throws ServiceException, URISyntaxException
    {
        return getContainerHandler("");
    }

    /**
     * Gets the ContainerHandler service for an authenticated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A ContainerHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ContainerHandler getContainerHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        ContainerHandler handler = authorizedContainerHandlerServiceLocator.getContainerHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "authorizedContainerHandlerServiceLocator = " + authorizedContainerHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the SemanticStoreHandler service for an anonymous user.
     *
     * @return A SemanticStoreHandler
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static SemanticStoreHandler getSemanticScoreHandler() throws ServiceException, URISyntaxException
    {
        return getSemanticScoreHandler("");
    }
    
    /**
     * Gets the SemanticStoreHandler service for an authenticated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A SemanticStoreHandler
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static SemanticStoreHandler getSemanticScoreHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        SemanticStoreHandler handler = authorizedSemanticScoreHandlerServiceLocator.getSemanticStoreHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "authorizedSemanticScoreHandlerServiceLocator = " + authorizedSemanticScoreHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the ScopeHandler service for an anonymous user.
     *
     * @return A ScopeHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ScopeHandler getScopeHandler() throws ServiceException, URISyntaxException
    {
        return getScopeHandler("");
    }
    
    /**
     * Gets the ScopeHandler service for  an authenticated user.
     *
     *@param userHandle The handle of the logged in user.
     * @return A ScopeHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ScopeHandler getScopeHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        ScopeHandler handler = publicScopeHandlerServiceLocator.getScopeHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "publicScopeHandlerServiceLocator = " + publicScopeHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    /**
     * Gets the AggregationDefinitionHandler service for an anonymous user.
     *
     * @return A AggregationDefinitionHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static AggregationDefinitionHandler getAggregationDefinitionHandler() throws ServiceException, URISyntaxException
    {
        AggregationDefinitionHandler handler = publicAggregationDefinitionHandlerServiceLocator.getAggregationDefinitionHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "publicAggregationDefinitionHandlerServiceLocator = " + publicAggregationDefinitionHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }
    
    /**
     * Gets the StatisticDataHandler service for an anonymous user.
     *
     * @return A StatisticDataHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static StatisticDataHandler getStatisticDataHandler() throws ServiceException, URISyntaxException
    {
        return getStatisticDataHandler("");
    }
    
    /**
     * Gets the StatisticDataHandler service for an logged in user.
     *
     * @return A StatisticDataHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static StatisticDataHandler getStatisticDataHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        StatisticDataHandler handler = publicStatisticDataHandlerServiceLocator.getStatisticDataHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "publicStatisticDataHandlerServiceLocator = " + publicStatisticDataHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the ReportDefinitionHandler service for an anonymous user.
     *
     * @return A AggregationDefinitionHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ReportDefinitionHandler getReportDefinitionHandler() throws ServiceException, URISyntaxException
    {
        return getReportDefinitionHandler("");
    }
    
    /**
     * Gets the ReportDefinitionHandler service for an authenticated user.
     *
     * @return A ReportDefinitionHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ReportDefinitionHandler getReportDefinitionHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        ReportDefinitionHandler handler = publicReportDefinitionHandlerServiceLocator.getReportDefinitionHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "publicReportDefinitionHandlerServiceLocator = " + publicReportDefinitionHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    public static ReportHandler getReportHandler() throws ServiceException, URISyntaxException
    {
        return getReportHandler("");
    }
    
    
    /**
     * Gets the ReportHandler service for an logged in user.
     *
     * @return A ReportHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ReportHandler getReportHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        ReportHandler handler = publicReportHandlerServiceLocator.getReportHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "publicReportHandlerServiceLocator = " + publicReportHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the IngestHandler service for an authenticated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A IngestHandler.
     * @throws URISyntaxException 
     * @throws ServiceException 
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static IngestHandler getIngestHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        IngestHandler handler = authorizedIngestHandlerServiceLocator.getIngestHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "authorizedIngestHandlerServiceLocator = " + authorizedIngestHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the AdminHandler service for an authenticated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return An AdminHandler.
     * @throws URISyntaxException 
     * @throws ServiceException 
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static AdminHandler getAdminHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        AdminHandler handler = authorizedAdminHandlerServiceLocator.getAdminHandlerService();
        Logger.getLogger(ServiceLocator.class).debug(
                "authorizedAdminHandlerServiceLocator = " + authorizedAdminHandlerServiceLocator);
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    public static SRWPort getSearchHandler(String databaseIdentifier) throws ServiceException, URISyntaxException
    {
        try
        {
            return getSearchHandler(databaseIdentifier, new URL(getFrameworkUrl()));
        } catch (MalformedURLException e)
        {
            throw new ServiceException(e);
        }
    }
    
    public static SRWPort getSearchHandler(String databaseIdentifier, String userHandle) throws ServiceException, URISyntaxException
    {
        try
        {
            return getSearchHandler(databaseIdentifier, new URL(getFrameworkUrl()), userHandle);
        } catch (MalformedURLException e)
        {
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the SearchHandler service for an anonymous user.
     *
     * @param databaseIdentifier  escidoc search database identifier
     * @return A SearchHandler (SRWPort).
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static SRWPort getSearchHandler(String databaseIdentifier, URL frameWorkURL) throws ServiceException, URISyntaxException
    {
        try
        {
            return getSearchHandler(databaseIdentifier, frameWorkURL, "");
        } catch (MalformedURLException e)
        {
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the SearchHandler service for an anonymous user.
     *
     * @param databaseIdentifier  escidoc search database identifier
     * @return A SearchHandler (SRWPort).
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static SRWPort getSearchHandler(String databaseIdentifier, URL frameWorkURL, String userHandle) throws ServiceException, URISyntaxException, MalformedURLException
    {
        if ( databaseIdentifier == null)
        {
            throw new ServiceException("Database identifier is not valid");
        }

        SRWSampleServiceLocator searchHandlerServiceLocator = new SRWSampleServiceLocator(new FileProvider(CONFIGURATION_FILE));
        String url = frameWorkURL.toString() + SRW_PATH + "/" + databaseIdentifier + "/" + searchHandlerServiceLocator.getSRWWSDDServiceName();
        Logger.getLogger(ServiceLocator.class).info("searchHandlerServiceLocator URL=" + url);
        searchHandlerServiceLocator.setSRWEndpointAddress(url);
        SRWPort handler = searchHandlerServiceLocator.getSRW();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    
    public static ExplainPort getExplainHandler(String databaseIdentifier) throws ServiceException, URISyntaxException
    {
        try
        {
            return getExplainHandler(databaseIdentifier, new URL(getFrameworkUrl()));
        } catch (MalformedURLException e)
        {
            throw new ServiceException(e);
        }
    }
    /**
     * Gets the ExplainHandler service for an anonymous user.
     *
     * @param databaseIdentifier  escidoc search database identifier
     * @return A ExplainHandler (ExplainPort)
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws URISyntaxException 
     */
    public static ExplainPort getExplainHandler(String databaseIdentifier, URL frameWorkURL) throws ServiceException, MalformedURLException, URISyntaxException
    {
        if (databaseIdentifier == null)
        {
            throw new ServiceException("Database identifier is not valid");
        }
        SRWSampleServiceLocator explainHandlerServiceLocator = new SRWSampleServiceLocator();
        String url = frameWorkURL.toString() + SRW_PATH + "/" + databaseIdentifier;
        Logger.getLogger(ServiceLocator.class).info("publicExplainHandlerServiceLocator URL=" + url);
        explainHandlerServiceLocator.setSRWEndpointAddress(url);
        ExplainPort handler = explainHandlerServiceLocator.getExplainSOAP(new URL(url + "?operation=explain"));
        return handler;
    }
    
    /********************************************************************************************************
     * 
     * Helper classes for creating Singeltons of ServiceLocator objects connected to the default framework 
     * 
     *
     *********************************************************************************************************/  
    
    /**
     * Helper class for creating a Singelton of a UserManagementWrapperServiceLocator object
     *
     */
    private static class AuthorizedUserManagementWrapperServiceLocatorHolder
    {               
        public static UserManagementWrapperServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static UserManagementWrapperServiceLocator getServiceLocator()
        {
            UserManagementWrapperServiceLocator serviceLocator = new UserManagementWrapperServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of UserManagementWrapperServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getUserManagementWrapperServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }            
            
            Logger.getLogger(ServiceLocator.class).info("UserManagementWrapper URL=" + url);
            serviceLocator.setUserManagementWrapperServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing UserManagementWrapperServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a UserAccountHandlerServiceLocator object
     *
     */
    private static class AuthorizedUserAccountHandlerServiceLocatorHolder
    {        
        
        public static UserAccountHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static UserAccountHandlerServiceLocator getServiceLocator()
        {
            UserAccountHandlerServiceLocator serviceLocator = new UserAccountHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of UserAccountHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getUserAccountHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("UserAccountHandler URL=" + url);
            serviceLocator.setUserAccountHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing UserAccountHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a UserGroupHandlerServiceLocator object
     *
     */
    private static class UserGroupHandlerServiceLocatorHolder
    {        
        
        public static UserGroupHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static UserGroupHandlerServiceLocator getServiceLocator()
        {
            UserGroupHandlerServiceLocator serviceLocator = new UserGroupHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of UserGroupHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getUserGroupHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("UserGroupHandlerServiceLocator URL=" + url);
            serviceLocator.setUserGroupHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing UserGroupHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a OrganizationalUnitHandlerServiceLocator object
     *
     */
    private static class OrganizationalUnitHandlerServiceLocatorHolder
    {        
        
        public static OrganizationalUnitHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static OrganizationalUnitHandlerServiceLocator getServiceLocator()
        {
            OrganizationalUnitHandlerServiceLocator serviceLocator = new OrganizationalUnitHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of OrganizationalUnitHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getOrganizationalUnitHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("OrganizationalUnitHandlerServiceLocator URL=" + url);
            serviceLocator.setOrganizationalUnitHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing OrganizationalUnitHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a ContentModelHandlerServiceLocator object
     *
     */
    private static class ContentModelHandlerServiceLocatorHolder
    {        
        
        public static ContentModelHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static ContentModelHandlerServiceLocator getServiceLocator()
        {
            ContentModelHandlerServiceLocator serviceLocator = new ContentModelHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of ContentModelHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getContentModelHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("ContentModelHandlerServiceLocator URL=" + url);
            serviceLocator.setContentModelHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing ContentModelHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a ContextHandlerServiceLocator object
     *
     */
    private static class ContextHandlerServiceLocatorHolder
    {        
        
        public static ContextHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static ContextHandlerServiceLocator getServiceLocator()
        {
            ContextHandlerServiceLocator serviceLocator = new ContextHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of ContextHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getContextHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("ContextHandlerServiceLocator URL=" + url);
            serviceLocator.setContextHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing ContextHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a ItemHandlerServiceLocator object
     *
     */
    private static class ItemHandlerServiceLocatorHolder
    {        
        
        public static ItemHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static ItemHandlerServiceLocator getServiceLocator()
        {
            ItemHandlerServiceLocator serviceLocator = new ItemHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of ItemHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getItemHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("ItemHandlerServiceLocator URL=" + url);
            serviceLocator.setItemHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing ItemHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a ContainerHandlerServiceLocator object
     *
     */
    private static class ContainerHandlerServiceLocatorHolder
    {        
        
        public static ContainerHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static ContainerHandlerServiceLocator getServiceLocator()
        {
            ContainerHandlerServiceLocator serviceLocator = new ContainerHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of ContainerHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getContainerHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("ContainerHandlerServiceLocator URL=" + url);
            serviceLocator.setContainerHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing ContainerHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a SemanticStoreHandlerServiceLocator object
     *
     */
    private static class SemanticStoreHandlerServiceLocatorHolder
    {        
        
        public static SemanticStoreHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static SemanticStoreHandlerServiceLocator getServiceLocator()
        {
            SemanticStoreHandlerServiceLocator serviceLocator = new SemanticStoreHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of SemanticStoreHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getSemanticStoreHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("SemanticStoreHandlerServiceLocator URL=" + url);
            serviceLocator.setSemanticStoreHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing SemanticStoreHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a ScopeHandlerServiceLocator object
     *
     */
    private static class ScopeHandlerServiceLocatorHolder
    {        
        
        public static ScopeHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static ScopeHandlerServiceLocator getServiceLocator()
        {
            ScopeHandlerServiceLocator serviceLocator = new ScopeHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of ScopeHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getScopeHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("ScopeHandlerServiceLocator URL=" + url);
            serviceLocator.setScopeHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing ScopeHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a AggregationDefinitionHandlerServiceLocator object
     *
     */
    private static class AggregationDefinitionHandlerServiceLocatorHolder
    {        
        
        public static AggregationDefinitionHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static AggregationDefinitionHandlerServiceLocator getServiceLocator()
        {
            AggregationDefinitionHandlerServiceLocator serviceLocator = new AggregationDefinitionHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of AggregationDefinitionHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getAggregationDefinitionHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("AggregationDefinitionHandlerServiceLocator URL=" + url);
            serviceLocator.setAggregationDefinitionHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing AggregationDefinitionHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a StatisticDataHandlerServiceLocator object
     *
     */
    private static class StatisticDataHandlerServiceLocatorHolder
    {        
        
        public static StatisticDataHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static StatisticDataHandlerServiceLocator getServiceLocator()
        {
            StatisticDataHandlerServiceLocator serviceLocator = new StatisticDataHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of StatisticDataHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getStatisticDataHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("StatisticDataHandlerServiceLocator URL=" + url);
            serviceLocator.setStatisticDataHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing StatisticDataHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a ReportDefinitionHandlerServiceLocator object
     *
     */
    private static class ReportDefinitionHandlerServiceLocatorHolder
    {        
        
        public static ReportDefinitionHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static ReportDefinitionHandlerServiceLocator getServiceLocator()
        {
            ReportDefinitionHandlerServiceLocator serviceLocator = new ReportDefinitionHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of ReportDefinitionHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getReportDefinitionHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("ReportDefinitionHandlerServiceLocator URL=" + url);
            serviceLocator.setReportDefinitionHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing ReportDefinitionHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a ReportHandlerServiceLocator object
     *
     */
    private static class ReportHandlerServiceLocatorHolder
    {        
        
        public static ReportHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static ReportHandlerServiceLocator getServiceLocator()
        {
            ReportHandlerServiceLocator serviceLocator = new ReportHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of ReportHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getReportHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("ReportHandlerServiceLocator URL=" + url);
            serviceLocator.setReportHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing ReportHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a IngestHandlerServiceLocator object
     *
     */
    private static class IngestHandlerServiceLocatorHolder
    {        
        
        public static IngestHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static IngestHandlerServiceLocator getServiceLocator()
        {
            IngestHandlerServiceLocator serviceLocator = new IngestHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of IngestHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getIngestHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("IngestHandlerServiceLocator URL=" + url);
            serviceLocator.setIngestHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing IngestHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /**
     * Helper class for creating a Singelton of a AdminHandlerServiceLocator object
     *
     */
    private static class AdminHandlerServiceLocatorHolder
    {        
        
        public static AdminHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static AdminHandlerServiceLocator getServiceLocator()
        {
            AdminHandlerServiceLocator serviceLocator = new AdminHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Initialization of AdminHandlerServiceLocator started: " + serviceLocator);
            String url = null;
            
            try
            {
                url = ServiceLocator.getFrameworkUrl() + FRAMEWORK_PATH + "/" + serviceLocator.getAdminHandlerServiceWSDDServiceName();
            }
            catch (Exception e)
            {
                Logger.getLogger(ServiceLocator.class).warn("Error when reading property: escidoc.framework_access.framework.url");
            }
            
            Logger.getLogger(ServiceLocator.class).info("AdminHandlerServiceLocator URL=" + url);
            serviceLocator.setAdminHandlerServiceEndpointAddress(url);
            Logger.getLogger(ServiceLocator.class).info("Initializing AdminHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    /********************************************************************************************************
     * 
     * Helper classes for creating Singeltons of ServiceLocator objects connected to some 
     * possibly changing or external framework. 
     * 
     *
     *********************************************************************************************************/  
    
    private static class ExtAuthorizedUserAccountHandlerServiceLocatorHolder
    {
        public static UserAccountHandlerServiceLocator serviceLocator = getServiceLocator();

        synchronized private static UserAccountHandlerServiceLocator getServiceLocator()
        {
            UserAccountHandlerServiceLocator serviceLocator = new UserAccountHandlerServiceLocator(new FileProvider(
                    CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Creating UserAccountHandlerServiceLocator finished.");
            return serviceLocator;
        }
    }
    
    private static class ExtContentModelHandlerServiceLocatorHolder
    {
        public static ContentModelHandlerServiceLocator serviceLocator = getServiceLocator();

        synchronized private static ContentModelHandlerServiceLocator getServiceLocator()
        {
            ContentModelHandlerServiceLocator serviceLocator = new ContentModelHandlerServiceLocator(new FileProvider(
                    CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class)
                    .info("Creating ContentModelHandlerServiceLocator in ExtContentModelHandlerServiceLocatorHolder finished.");
            return serviceLocator;
        }
    }
    
    private static class ExtContextHandlerServiceLocatorHolder
    {
        public static ContextHandlerServiceLocator serviceLocator = getServiceLocator();

        synchronized private static ContextHandlerServiceLocator getServiceLocator()
        {
            ContextHandlerServiceLocator serviceLocator = new ContextHandlerServiceLocator(new FileProvider(
                    CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Creating ContextHandlerServiceLocator finished.");
            return serviceLocator;
        }
    }
    
    private static class ExtItemHandlerServiceLocatorHolder
    {        
        
        public static ItemHandlerServiceLocator serviceLocator = getServiceLocator();
        
        synchronized private static ItemHandlerServiceLocator getServiceLocator()
        {
            ItemHandlerServiceLocator serviceLocator = new ItemHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
           
            Logger.getLogger(ServiceLocator.class).info("Creating ItemHandlerServiceLocator finished.");
            
            return serviceLocator;
        }
    }
    
    private static class ExtOrganizationalUnitHandlerServiceLocatorHolder
    {
        public static OrganizationalUnitHandlerServiceLocator serviceLocator = getServiceLocator();

        synchronized private static OrganizationalUnitHandlerServiceLocator getServiceLocator()
        {
            OrganizationalUnitHandlerServiceLocator serviceLocator = new OrganizationalUnitHandlerServiceLocator(
                    new FileProvider(CONFIGURATION_FILE));
            Logger.getLogger(ServiceLocator.class).info("Creating OrganizationalUnitHandlerServiceLocator finished.");
            return serviceLocator;
        }
    }

    

    

}
