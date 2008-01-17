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
import java.net.URL;
import javax.xml.rpc.ServiceException;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.log4j.Logger;
import org.apache.ws.security.handler.WSHandlerConstants;
import de.fiz.escidoc.ctm.ContentTypeHandlerRemote;
import de.fiz.escidoc.ctm.ContentTypeHandlerRemoteServiceLocator;
import de.fiz.escidoc.om.ContainerHandlerRemote;
import de.fiz.escidoc.om.ContainerHandlerRemoteServiceLocator;
import de.fiz.escidoc.om.ContextHandlerRemote;
import de.fiz.escidoc.om.ContextHandlerRemoteServiceLocator;
import de.fiz.escidoc.om.ItemHandlerRemote;
import de.fiz.escidoc.om.ItemHandlerRemoteServiceLocator;
import de.fiz.escidoc.oum.OrganizationalUnitHandlerRemote;
import de.fiz.escidoc.oum.OrganizationalUnitHandlerRemoteServiceLocator;
import de.fiz.escidoc.sm.AggregationDefinitionHandlerRemote;
import de.fiz.escidoc.sm.AggregationDefinitionHandlerRemoteServiceLocator;
import de.fiz.escidoc.sm.ReportDefinitionHandlerRemote;
import de.fiz.escidoc.sm.ReportDefinitionHandlerRemoteServiceLocator;
import de.fiz.escidoc.sm.ReportHandlerRemote;
import de.fiz.escidoc.sm.ReportHandlerRemoteServiceLocator;
import de.fiz.escidoc.sm.ScopeHandlerRemote;
import de.fiz.escidoc.sm.ScopeHandlerRemoteServiceLocator;
import de.fiz.escidoc.sm.StatisticDataHandlerRemote;
import de.fiz.escidoc.sm.StatisticDataHandlerRemoteServiceLocator;
import de.fiz.escidoc.ssh.SemanticStoreHandlerRemote;
import de.fiz.escidoc.ssh.SemanticStoreHandlerRemoteServiceLocator;
import de.fiz.escidoc.um.UserAccountHandlerRemote;
import de.fiz.escidoc.um.UserAccountHandlerRemoteServiceLocator;
import de.fiz.escidoc.um.UserManagementWrapperRemote;
import de.fiz.escidoc.um.UserManagementWrapperRemoteServiceLocator;

/**
 * This service locator has to be used for getting the handler of the framework services.<BR>
 * The URL of the framework can be configured using the system property "framework.url".
 * In an eclipse unit test this can be set as an vm argument e.g
 * -Dframework.url=http://130.183.251.122:8080/axis/services
 *
 * @author Peter Broszeit (initial creation)
 * @author $Author: wfrank $ (last modification)
 * @version $Revision: 329 $ $LastChangedDate: 2007-12-06 09:44:45 +0100 (Thu, 06 Dec 2007) $
 * @revised by BrP: 03.09.2007
 */
public class ServiceLocator
{
    private static final String CONFIGURATION_FILE = "client.wsdd";
    private static final String FRAMEWORK_PATH = "/axis/services";
    private static final String SRW_PATH = "/srw/search";
    private static final String SRW_DATABASE = "escidoc_";
    private static final String SRW_LANGUAGE = "all";

    private static UserManagementWrapperRemoteServiceLocator authorizedUserManagementWrapperServiceLocator;
    private static UserAccountHandlerRemoteServiceLocator authorizedUserAccountHandlerServiceLocator;
    private static OrganizationalUnitHandlerRemoteServiceLocator publicOrganizationalUnitHandlerServiceLocator;
    private static OrganizationalUnitHandlerRemoteServiceLocator authorizedOrganizationalUnitHandlerServiceLocator;
    private static ContentTypeHandlerRemoteServiceLocator authorizedContentTypeHandlerServiceLocator;
    private static ContextHandlerRemoteServiceLocator publicContextHandlerServiceLocator;
    private static ContextHandlerRemoteServiceLocator authorizedContextHandlerServiceLocator;
    private static ItemHandlerRemoteServiceLocator publicItemHandlerServiceLocator;
    private static ItemHandlerRemoteServiceLocator authorizedItemHandlerServiceLocator;
    private static ContainerHandlerRemoteServiceLocator authorizedContainerHandlerServiceLocator;
    private static SemanticStoreHandlerRemoteServiceLocator authorizedSemanticScoreHandlerServiceLocator;
    private static ScopeHandlerRemoteServiceLocator publicScopeHandlerServiceLocator;
    private static AggregationDefinitionHandlerRemoteServiceLocator  publicAggregationDefinitionHandlerServiceLocator;
    private static StatisticDataHandlerRemoteServiceLocator  publicStatisticDataHandlerServiceLocator;
    private static ReportDefinitionHandlerRemoteServiceLocator  publicReportDefinitionHandlerServiceLocator;
    private static ReportHandlerRemoteServiceLocator  publicReportHandlerServiceLocator;

    /**
     * Get the configured URL of the running framework instance.
     *
     * @return The url as a String.
     * @throws ServiceException
     */
    public static String getFrameworkUrl() throws ServiceException
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
     */
    public static UserManagementWrapperRemote getUserManagementWrapper(String userHandle) throws ServiceException
    {
        if (authorizedUserManagementWrapperServiceLocator == null)
        {
            authorizedUserManagementWrapperServiceLocator = new UserManagementWrapperRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedUserManagementWrapperServiceLocator.getUserManagementWrapperServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("UserManagementWrapper URL=" + url);
            authorizedUserManagementWrapperServiceLocator.setUserManagementWrapperServiceEndpointAddress(url);
        }
        UserManagementWrapperRemote handler = authorizedUserManagementWrapperServiceLocator.getUserManagementWrapperService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    /**
     * Gets the UserAccountHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A UserAccountHandler.
     * @throws ServiceException
     */
    public static UserAccountHandlerRemote getUserAccountHandler(String userHandle) throws ServiceException
    {
        if (authorizedUserAccountHandlerServiceLocator == null)
        {
            authorizedUserAccountHandlerServiceLocator = new UserAccountHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedUserAccountHandlerServiceLocator.getUserAccountHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("UserAccountHandler URL=" + url);
            authorizedUserAccountHandlerServiceLocator.setUserAccountHandlerServiceEndpointAddress(url);
        }
        UserAccountHandlerRemote handler = authorizedUserAccountHandlerServiceLocator.getUserAccountHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    /**
     * Gets the OrganizationalUnitHandler service for an anonymous user.
     *
     * @return An OrganizationalUnitHandler.
     * @throws ServiceException
     */
    public static OrganizationalUnitHandlerRemote getOrganizationalUnitHandler() throws ServiceException
    {
        if (publicOrganizationalUnitHandlerServiceLocator == null)
        {
            publicOrganizationalUnitHandlerServiceLocator = new OrganizationalUnitHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicOrganizationalUnitHandlerServiceLocator.getOrganizationalUnitHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("OrganizationalUnitHandler URL=" + url);
            publicOrganizationalUnitHandlerServiceLocator.setOrganizationalUnitHandlerServiceEndpointAddress(url);
        }
        OrganizationalUnitHandlerRemote handler = publicOrganizationalUnitHandlerServiceLocator.getOrganizationalUnitHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }

    /**
     * Gets the OrganizationalUnitHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return An OrganizationalUnitHandler.
     * @throws ServiceException
     */
    public static OrganizationalUnitHandlerRemote getOrganizationalUnitHandler(String userHandle) throws ServiceException
    {
        if (authorizedOrganizationalUnitHandlerServiceLocator == null)
        {
            authorizedOrganizationalUnitHandlerServiceLocator = new OrganizationalUnitHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedOrganizationalUnitHandlerServiceLocator.getOrganizationalUnitHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("OrganizationalUnitHandler URL=" + url);
            authorizedOrganizationalUnitHandlerServiceLocator.setOrganizationalUnitHandlerServiceEndpointAddress(url);
        }
        OrganizationalUnitHandlerRemote handler = authorizedOrganizationalUnitHandlerServiceLocator.getOrganizationalUnitHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    /**
     * Gets the ContentTypeHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A ContentTypeHandler.
     * @throws ServiceException
     */
    public static ContentTypeHandlerRemote getContentTypeHandler(String userHandle) throws ServiceException
    {
        if (authorizedContentTypeHandlerServiceLocator == null)
        {
            authorizedContentTypeHandlerServiceLocator = new ContentTypeHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedContentTypeHandlerServiceLocator.getContentTypeHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ContentTypeHandler URL=" + url);
            authorizedContentTypeHandlerServiceLocator.setContentTypeHandlerServiceEndpointAddress(url);
        }
        ContentTypeHandlerRemote handler = authorizedContentTypeHandlerServiceLocator.getContentTypeHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    /**
     * Gets the ContextHandler service for an anonymous user.
     *
     * @return A ContextHandler.
     * @throws ServiceException
     */
    public static ContextHandlerRemote getContextHandler() throws ServiceException
    {
        if (publicContextHandlerServiceLocator == null)
        {
            publicContextHandlerServiceLocator = new ContextHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicContextHandlerServiceLocator.getContextHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ContextHandler URL=" + url);
            publicContextHandlerServiceLocator.setContextHandlerServiceEndpointAddress(url);
        }
        ContextHandlerRemote handler = publicContextHandlerServiceLocator.getContextHandlerService();
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
     */
    public static ContextHandlerRemote getContextHandler(String userHandle) throws ServiceException
    {
        if (authorizedContextHandlerServiceLocator == null)
        {
            authorizedContextHandlerServiceLocator = new ContextHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedContextHandlerServiceLocator.getContextHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ContextHandler URL=" + url);
            authorizedContextHandlerServiceLocator.setContextHandlerServiceEndpointAddress(url);
        }
        ContextHandlerRemote handler = authorizedContextHandlerServiceLocator.getContextHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    /**
     * Gets the ItemHandler service for an anonymous user.
     *
     * @return An ItemHandler.
     * @throws ServiceException
     */
    public static ItemHandlerRemote getItemHandler() throws ServiceException
    {
        if (publicItemHandlerServiceLocator == null)
        {
            publicItemHandlerServiceLocator = new ItemHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicItemHandlerServiceLocator.getItemHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("publicItemHandlerServiceLocator URL=" + url);
            publicItemHandlerServiceLocator.setItemHandlerServiceEndpointAddress(url);
        }
        ItemHandlerRemote handler = publicItemHandlerServiceLocator.getItemHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }

    /**
     * Gets the ItemHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return An ItemHandler.
     * @throws ServiceException
     */
    public static ItemHandlerRemote getItemHandler(String userHandle) throws ServiceException
    {
        if (authorizedItemHandlerServiceLocator == null)
        {
            authorizedItemHandlerServiceLocator = new ItemHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedItemHandlerServiceLocator.getItemHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("authorizedItemHandlerServiceLocator URL=" + url);
            authorizedItemHandlerServiceLocator.setItemHandlerServiceEndpointAddress(url);
        }
        ItemHandlerRemote handler = authorizedItemHandlerServiceLocator.getItemHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    /**
     * Gets the SemanticStoreHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A SemanticStoreHandler
     * @throws ServiceException
     */
    public static SemanticStoreHandlerRemote getSemanticScoreHandler(String userHandle) throws ServiceException
    {
        if (authorizedSemanticScoreHandlerServiceLocator == null)
        {
            authorizedSemanticScoreHandlerServiceLocator = new SemanticStoreHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedSemanticScoreHandlerServiceLocator.getSemanticStoreHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("publicSemanticScoreHandlerServiceLocator URL=" + url);
            authorizedSemanticScoreHandlerServiceLocator.setSemanticStoreHandlerServiceEndpointAddress(url);
        }
        SemanticStoreHandlerRemote handler = authorizedSemanticScoreHandlerServiceLocator.getSemanticStoreHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }

    /**
     * Gets the SearchHandler service for an anonymous user.
     *
     * @param language The 2 character ISO code of the language.
     * @return A SearchHandler (SRWPort).
     * @throws ServiceException
     */
    public static SRWPort getSearchHandler(String language) throws ServiceException
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
     */
    public static ExplainPort getExplainHandler(String language) throws ServiceException, MalformedURLException
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
     */
    public static ScopeHandlerRemote getScopeHandler() throws ServiceException
    {
        if (publicScopeHandlerServiceLocator == null)
        {
            publicScopeHandlerServiceLocator = new ScopeHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicScopeHandlerServiceLocator.getScopeHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ScopeHandler URL=" + url);
            publicScopeHandlerServiceLocator.setScopeHandlerServiceEndpointAddress(url);
        }
        ScopeHandlerRemote handler = publicScopeHandlerServiceLocator.getScopeHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }

    /**
     * Gets the AggregationDefinitionHandler service for an anonymous user.
     *
     * @return A AggregationDefinitionHandler.
     * @throws ServiceException
     */
    public static AggregationDefinitionHandlerRemote getAggregationDefinitionHandler() throws ServiceException
    {
        if (publicAggregationDefinitionHandlerServiceLocator == null)
        {
            publicAggregationDefinitionHandlerServiceLocator = new AggregationDefinitionHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicAggregationDefinitionHandlerServiceLocator.getAggregationDefinitionHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("AggregationDefinitionHandler URL=" + url);
            publicScopeHandlerServiceLocator.setScopeHandlerServiceEndpointAddress(url);
        }
        AggregationDefinitionHandlerRemote handler = publicAggregationDefinitionHandlerServiceLocator.getAggregationDefinitionHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }

    /**
     * Gets the StatisticDataHandler service for an anonymous user.
     *
     * @return A StatisticDataHandler.
     * @throws ServiceException
     */
    public static StatisticDataHandlerRemote getStatisticDataHandler() throws ServiceException
    {
        if (publicStatisticDataHandlerServiceLocator == null)
        {
            publicStatisticDataHandlerServiceLocator = new StatisticDataHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicStatisticDataHandlerServiceLocator.getStatisticDataHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("StatisticDataHandler URL=" + url);
            publicStatisticDataHandlerServiceLocator.setStatisticDataHandlerServiceWSDDServiceName(url);
        }
        StatisticDataHandlerRemote handler = publicStatisticDataHandlerServiceLocator.getStatisticDataHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }

    /**
     * Gets the ReportDefinitionHandler service for an anonymous user.
     *
     * @return A ReportDefinitionHandler.
     * @throws ServiceException
     */
    public static ReportDefinitionHandlerRemote getReportDefinitionHandler() throws ServiceException
    {
        if (publicReportDefinitionHandlerServiceLocator == null)
        {
            publicReportDefinitionHandlerServiceLocator = new ReportDefinitionHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicReportDefinitionHandlerServiceLocator.getReportDefinitionHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ReportDefinitionHandler URL=" + url);
            publicReportDefinitionHandlerServiceLocator.setReportDefinitionHandlerServiceEndpointAddress(url);
        }
        ReportDefinitionHandlerRemote handler = publicReportDefinitionHandlerServiceLocator.getReportDefinitionHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }

    /**
     * Gets the ReportHandler service for an anonymous user.
     *
     * @return A ReportHandler.
     * @throws ServiceException
     */
    public static ReportHandlerRemote getReportHandler() throws ServiceException
    {
        if (publicReportHandlerServiceLocator == null)
        {
            publicReportHandlerServiceLocator = new ReportHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + publicReportHandlerServiceLocator.getReportHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("ReportHandler URL=" + url);
            publicReportHandlerServiceLocator.setReportHandlerServiceEndpointAddress(url);
        }
        ReportHandlerRemote handler = publicReportHandlerServiceLocator.getReportHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(""));
        return handler;
    }

    /**
     * Gets the ContainerHandler service for an authentificated user.
     *
     * @param userHandle The handle of the logged in user.
     * @return A ContainerHandler.
     * @throws ServiceException
     */
    public static ContainerHandlerRemote getContainerHandler(String userHandle) throws ServiceException
    {
        if (authorizedContainerHandlerServiceLocator == null)
        {
            authorizedContainerHandlerServiceLocator = new ContainerHandlerRemoteServiceLocator(new FileProvider(CONFIGURATION_FILE));
            String url = getFrameworkUrl() + FRAMEWORK_PATH + "/" + authorizedContainerHandlerServiceLocator.getContainerHandlerServiceWSDDServiceName();
            Logger.getLogger(ServiceLocator.class).info("authorizedContainerHandlerServiceLocator URL=" + url);
            authorizedContainerHandlerServiceLocator.setContainerHandlerServiceEndpointAddress(url);
        }
        ContainerHandlerRemote handler = authorizedContainerHandlerServiceLocator.getContainerHandlerService();
        ((Stub)handler)._setProperty(WSHandlerConstants.PW_CALLBACK_REF, new PWCallback(userHandle));
        return handler;
    }
}
