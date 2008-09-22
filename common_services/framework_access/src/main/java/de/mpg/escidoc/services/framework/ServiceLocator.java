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
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
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
import de.escidoc.www.services.aa.UserManagementWrapper;
import de.escidoc.www.services.aa.UserManagementWrapperServiceLocator;
import de.escidoc.www.services.cmm.ContentModelHandler;
import de.escidoc.www.services.cmm.ContentModelHandlerServiceLocator;
import de.escidoc.www.services.om.ContainerHandler;
import de.escidoc.www.services.om.ContainerHandlerServiceLocator;
import de.escidoc.www.services.om.ContextHandler;
import de.escidoc.www.services.om.ContextHandlerServiceLocator;
import de.escidoc.www.services.om.ItemHandler;
import de.escidoc.www.services.om.ItemHandlerServiceLocator;
import de.escidoc.www.services.om.SemanticStoreHandler;
import de.escidoc.www.services.om.SemanticStoreHandlerServiceLocator;
import de.escidoc.www.services.om.TocHandler;
import de.escidoc.www.services.om.TocHandlerServiceLocator;
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
 * @author $Author: wfrank $ (last modification)
 * @version $Revision: 329 $ $LastChangedDate: 2007-12-06 09:44:45 +0100 (Thu, 06 Dec 2007) $
 * @revised by FrW: 10.03.2008
 */
public class ServiceLocator
{
    private static final String CONFIGURATION_FILE = "client.wsdd";
    private static final String FRAMEWORK_PATH = "/axis/services";
    private static final String SRW_PATH = "/srw/search";
    private static final String SRW_DATABASE = "escidoc_";
    private static final String SRW_LANGUAGE = "all";

    private static UserManagementWrapperServiceLocator authorizedUserManagementWrapperServiceLocator;
    private static UserAccountHandlerServiceLocator authorizedUserAccountHandlerServiceLocator;
    private static OrganizationalUnitHandlerServiceLocator publicOrganizationalUnitHandlerServiceLocator;
    private static OrganizationalUnitHandlerServiceLocator authorizedOrganizationalUnitHandlerServiceLocator;
    private static ContentModelHandlerServiceLocator authorizedContentModelHandlerServiceLocator;
    private static ContextHandlerServiceLocator publicContextHandlerServiceLocator;
    private static ContextHandlerServiceLocator authorizedContextHandlerServiceLocator;
    private static ItemHandlerServiceLocator publicItemHandlerServiceLocator;
    private static ItemHandlerServiceLocator authorizedItemHandlerServiceLocator;
    private static ContainerHandlerServiceLocator authorizedContainerHandlerServiceLocator;
    private static SemanticStoreHandlerServiceLocator authorizedSemanticScoreHandlerServiceLocator;
    private static ScopeHandlerServiceLocator publicScopeHandlerServiceLocator;
    private static AggregationDefinitionHandlerServiceLocator  publicAggregationDefinitionHandlerServiceLocator;
    private static StatisticDataHandlerServiceLocator  publicStatisticDataHandlerServiceLocator;
    private static ReportDefinitionHandlerServiceLocator  publicReportDefinitionHandlerServiceLocator;
    private static ReportHandlerServiceLocator  publicReportHandlerServiceLocator;
    private static TocHandlerServiceLocator authorizedTocHandlerServiceLocator;

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

    /**
     * Gets the UserManagementWrapper service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A wrapper for the UserManagement.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static UserManagementWrapper getUserManagementWrapper(String userHandle) throws ServiceException, URISyntaxException
    {
        if (authorizedUserManagementWrapperServiceLocator == null)
        {
            authorizedUserManagementWrapperServiceLocator = new UserManagementWrapperServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedUserManagementWrapperServiceLocator.getUserManagementWrapperServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("UserManagementWrapper URL=" + url);
            authorizedUserManagementWrapperServiceLocator.setUserManagementWrapperServiceEndpointAddress(url);
        }
        UserManagementWrapper handler = authorizedUserManagementWrapperServiceLocator.getUserManagementWrapperService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
         return handler;
    }

    /**
     * Gets the UserAccountHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A UserAccountHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static UserAccountHandler getUserAccountHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        if (authorizedUserAccountHandlerServiceLocator == null)
        {
            authorizedUserAccountHandlerServiceLocator = new UserAccountHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedUserAccountHandlerServiceLocator.getUserAccountHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("UserAccountHandler URL=" + url);
            authorizedUserAccountHandlerServiceLocator.setUserAccountHandlerServiceEndpointAddress(url);
        }
        UserAccountHandler handler = authorizedUserAccountHandlerServiceLocator.getUserAccountHandlerService();
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
        if (publicOrganizationalUnitHandlerServiceLocator == null)
        {
            publicOrganizationalUnitHandlerServiceLocator = new OrganizationalUnitHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicOrganizationalUnitHandlerServiceLocator.getOrganizationalUnitHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("OrganizationalUnitHandler URL=" + url);
            publicOrganizationalUnitHandlerServiceLocator.setOrganizationalUnitHandlerServiceEndpointAddress(url);
        }
        OrganizationalUnitHandler handler = publicOrganizationalUnitHandlerServiceLocator.getOrganizationalUnitHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }

    /**
     * Gets the OrganizationalUnitHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return An OrganizationalUnitHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static OrganizationalUnitHandler getOrganizationalUnitHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        if (authorizedOrganizationalUnitHandlerServiceLocator == null)
        {
            authorizedOrganizationalUnitHandlerServiceLocator = new OrganizationalUnitHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedOrganizationalUnitHandlerServiceLocator.getOrganizationalUnitHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("OrganizationalUnitHandler URL=" + url);
            authorizedOrganizationalUnitHandlerServiceLocator.setOrganizationalUnitHandlerServiceEndpointAddress(url);
        }
        OrganizationalUnitHandler handler = authorizedOrganizationalUnitHandlerServiceLocator.getOrganizationalUnitHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    /**
     * Gets the ContentTypeHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A ContentTypeHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ContentModelHandler getContentModelHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        if (authorizedContentModelHandlerServiceLocator == null)
        {
            authorizedContentModelHandlerServiceLocator = new ContentModelHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedContentModelHandlerServiceLocator.getContentModelHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ContentModelHandler URL=" + url);
            authorizedContentModelHandlerServiceLocator.setContentModelHandlerServiceEndpointAddress(url);
        }
        ContentModelHandler handler = authorizedContentModelHandlerServiceLocator.getContentModelHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    /**
     * Gets the ContextHandler service for an anonymous user.
     *
     * @return A ContextHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ContextHandler getContextHandler() throws ServiceException, URISyntaxException
    {
        if (publicContextHandlerServiceLocator == null)
        {
            publicContextHandlerServiceLocator = new ContextHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicContextHandlerServiceLocator.getContextHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ContextHandler URL=" + url);
            publicContextHandlerServiceLocator.setContextHandlerServiceEndpointAddress(url);
        }
        ContextHandler handler = publicContextHandlerServiceLocator.getContextHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }

    /**
     * Gets the ContextHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A ContextHandler.
     * @throws MalformedURLException
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ContextHandler getContextHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        if (authorizedContextHandlerServiceLocator == null)
        {
            authorizedContextHandlerServiceLocator = new ContextHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedContextHandlerServiceLocator.getContextHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ContextHandler URL=" + url);
            authorizedContextHandlerServiceLocator.setContextHandlerServiceEndpointAddress(url);
        }
        ContextHandler handler = authorizedContextHandlerServiceLocator.getContextHandlerService();
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
        if (publicItemHandlerServiceLocator == null)
        {
            publicItemHandlerServiceLocator = new ItemHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicItemHandlerServiceLocator.getItemHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("publicItemHandlerServiceLocator URL=" + url);
            publicItemHandlerServiceLocator.setItemHandlerServiceEndpointAddress(url);
        }
        ItemHandler handler = publicItemHandlerServiceLocator.getItemHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }

    /**
     * Gets the ItemHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return An ItemHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ItemHandler getItemHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        if (authorizedItemHandlerServiceLocator == null)
        {
            authorizedItemHandlerServiceLocator = new ItemHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedItemHandlerServiceLocator.getItemHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("authorizedItemHandlerServiceLocator URL=" + url);
            authorizedItemHandlerServiceLocator.setItemHandlerServiceEndpointAddress(url);
        }
        ItemHandler handler = authorizedItemHandlerServiceLocator.getItemHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    /**
     * Gets the SemanticStoreHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A SemanticStoreHandler
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static SemanticStoreHandler getSemanticScoreHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        if (authorizedSemanticScoreHandlerServiceLocator == null)
        {
            authorizedSemanticScoreHandlerServiceLocator = new SemanticStoreHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedSemanticScoreHandlerServiceLocator.getSemanticStoreHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("publicSemanticScoreHandlerServiceLocator URL=" + url);
            authorizedSemanticScoreHandlerServiceLocator.setSemanticStoreHandlerServiceEndpointAddress(url);
        }
        SemanticStoreHandler handler = authorizedSemanticScoreHandlerServiceLocator.getSemanticStoreHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    /**
     * Gets the SearchHandler service for an anonymous user.
     *
     * @param language The 2 character ISO code of the language.
     * @return A SearchHandler (SRWPort).
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static SRWPort getSearchHandler(String language) throws ServiceException, URISyntaxException
    {
        if (language == null)
        {
            language = SRW_LANGUAGE;
        }
        SRWSampleServiceLocator searchHandlerServiceLocator = new SRWSampleServiceLocator();
        String url = getFrameworkUrl() + SRW_PATH + "/" + SRW_DATABASE + language + "/" + searchHandlerServiceLocator.getSRWWSDDServiceName();
        Logger.getLogger(ServiceLocator.class).info("publicSearchHandlerServiceLocator URL=" + url);
        searchHandlerServiceLocator.setSRWEndpointAddress(url);
        SRWPort handler = searchHandlerServiceLocator.getSRW();
        return handler;
    }

    /**
     * Gets the ExplainHandler service for an anonymous user.
     *
     * @param language The 2 character ISO code of the language.
     * @return A ExplainHandler (ExplainPort)
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws URISyntaxException 
     */
    public static ExplainPort getExplainHandler(String language) throws ServiceException, MalformedURLException, URISyntaxException
    {
        if (language == null)
        {
            language = SRW_LANGUAGE;
        }
        SRWSampleServiceLocator explainHandlerServiceLocator = new SRWSampleServiceLocator();
        String url = getFrameworkUrl() + SRW_PATH + "/" + SRW_DATABASE + language;
        Logger.getLogger(ServiceLocator.class).info("publicExplainHandlerServiceLocator URL=" + url);
        explainHandlerServiceLocator.setSRWEndpointAddress(url);
        ExplainPort handler = explainHandlerServiceLocator.getExplainSOAP(new URL(url + "?operation=explain"));
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
        if (publicScopeHandlerServiceLocator == null)
        {
            publicScopeHandlerServiceLocator = new ScopeHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicScopeHandlerServiceLocator.getScopeHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ScopeHandler URL=" + url);
            publicScopeHandlerServiceLocator.setScopeHandlerServiceEndpointAddress(url);
        }
        ScopeHandler handler = publicScopeHandlerServiceLocator.getScopeHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
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
        if (publicAggregationDefinitionHandlerServiceLocator == null)
        {
            publicAggregationDefinitionHandlerServiceLocator = new AggregationDefinitionHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicAggregationDefinitionHandlerServiceLocator.getAggregationDefinitionHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("AggregationDefinitionHandler URL=" + url);
            publicScopeHandlerServiceLocator.setScopeHandlerServiceEndpointAddress(url);
        }
        AggregationDefinitionHandler handler = publicAggregationDefinitionHandlerServiceLocator.getAggregationDefinitionHandlerService();
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
        if (publicStatisticDataHandlerServiceLocator == null)
        {
            publicStatisticDataHandlerServiceLocator = new StatisticDataHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicStatisticDataHandlerServiceLocator.getStatisticDataHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("StatisticDataHandler URL=" + url);
            publicStatisticDataHandlerServiceLocator.setStatisticDataHandlerServiceWSDDServiceName(url);
        }
        StatisticDataHandler handler = publicStatisticDataHandlerServiceLocator.getStatisticDataHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }

    /**
     * Gets the ReportDefinitionHandler service for an anonymous user.
     *
     * @return A ReportDefinitionHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ReportDefinitionHandler getReportDefinitionHandler() throws ServiceException, URISyntaxException
    {
        if (publicReportDefinitionHandlerServiceLocator == null)
        {
            publicReportDefinitionHandlerServiceLocator = new ReportDefinitionHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicReportDefinitionHandlerServiceLocator.getReportDefinitionHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ReportDefinitionHandler URL=" + url);
            publicReportDefinitionHandlerServiceLocator.setReportDefinitionHandlerServiceEndpointAddress(url);
        }
        ReportDefinitionHandler handler = publicReportDefinitionHandlerServiceLocator.getReportDefinitionHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
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
        if (publicReportDefinitionHandlerServiceLocator == null)
        {
            publicReportDefinitionHandlerServiceLocator = new ReportDefinitionHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicReportDefinitionHandlerServiceLocator.getReportDefinitionHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ReportDefinitionHandler URL=" + url);
            publicReportDefinitionHandlerServiceLocator.setReportDefinitionHandlerServiceEndpointAddress(url);
        }
        ReportDefinitionHandler handler = publicReportDefinitionHandlerServiceLocator.getReportDefinitionHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
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
        if (publicReportHandlerServiceLocator == null)
        {
            publicReportHandlerServiceLocator = new ReportHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicReportHandlerServiceLocator.getReportHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ReportHandler URL=" + url);
            publicReportHandlerServiceLocator.setReportHandlerServiceEndpointAddress(url);
        }
        ReportHandler handler = publicReportHandlerServiceLocator.getReportHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the ReportHandler service for an anonymous user.
     *
     * @return A ReportHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ReportHandler getReportHandler() throws ServiceException, URISyntaxException
    {
        if (publicReportHandlerServiceLocator == null)
        {
            publicReportHandlerServiceLocator = new ReportHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicReportHandlerServiceLocator.getReportHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ReportHandler URL=" + url);
            publicReportHandlerServiceLocator.setReportHandlerServiceEndpointAddress(url);
        }
        ReportHandler handler = publicReportHandlerServiceLocator.getReportHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }

    /**
     * Gets the ContainerHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A ContainerHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static ContainerHandler getContainerHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        if (authorizedContainerHandlerServiceLocator == null)
        {
            authorizedContainerHandlerServiceLocator = new ContainerHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedContainerHandlerServiceLocator.getContainerHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("authorizedContainerHandlerServiceLocator URL=" + url);
            authorizedContainerHandlerServiceLocator.setContainerHandlerServiceEndpointAddress(url);
        }
        ContainerHandler handler = authorizedContainerHandlerServiceLocator.getContainerHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
    
    /**
     * Gets the TocHandler service for an authenticated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A TocHandler.
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public static TocHandler getTocHandler(String userHandle) throws ServiceException, URISyntaxException
    {
        if (authorizedTocHandlerServiceLocator == null)
        {
            authorizedTocHandlerServiceLocator = new TocHandlerServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedTocHandlerServiceLocator.getTocHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("authorizedTocHandlerServiceLocator URL=" + url);
            authorizedTocHandlerServiceLocator.setTocHandlerServiceEndpointAddress(url);
        }
        TocHandler handler = authorizedTocHandlerServiceLocator.getTocHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
}
