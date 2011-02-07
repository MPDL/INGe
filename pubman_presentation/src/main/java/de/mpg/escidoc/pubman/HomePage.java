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

package de.mpg.escidoc.pubman;

import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;
import de.mpg.escidoc.pubman.search.SearchRetrieverRequestBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.search.query.SearchQuery;
import de.mpg.escidoc.services.search.query.SearchQuery.SortingOrder;

/**
 * BackingBean for HomePage.jsp.
 *
 * @author: Thomas Diebäcker, created 24.01.2007
 * @version: $Revision$ $LastChangedDate$
 * Revised by DiT: 14.08.2007
 */
public class HomePage extends BreadcrumbPage
{
    private static Logger logger = Logger.getLogger(HomePage.class);
    public static final String BEAN_NAME = "HomePage";
    
    /**
     * Public constructor.
     */
    public HomePage()
    {
        this.init();
        
    }
    
    
    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {
        Map<String, String> parameters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (parameters.containsKey("expired"))
        {
            error(getMessage("LoginErrorPage_loggedOffFromSystem"));
        }
        else if (parameters.containsKey("logout"))
        {
            info(getMessage("LogoutMessage"));
        }
        // Perform initializations inherited from our superclass
        super.init();

    }

   
    
    /**
     * Reads the blog URL from the properties file.
     * Needed for blogintegration on homepage
     * @return blodUrl as String
     */
    public String getBlogBaseUrl()
    {
        String url = "";
        try
        {
            url = PropertyReader.getProperty("escidoc.pubman.blog.baseUrl");
        }
        catch (Exception e)
        {
            HomePage.logger.error("Could not read property: 'escidoc.pubman.blog.baseUrl' from properties file.", e);
        }

        return url;
    }
    
  
    /**
     * Reads the survey URL from the properties file.
     * @return policyUrl as String
     */
    public String getSurveyUrl()
    {
        String url = ""; 
        try
        {
            url = PropertyReader.getProperty("escidoc.pubman.survey.url");
        }
        catch (Exception e)
        {
            HomePage.logger.error("Could not read property: 'escidoc.pubman.survey.url' from properties file.", e);
        }
        return url;
    }
    
    /**
     * Reads the survey Title from the properties file.
     */
    public String getSurveyTitle()
    {
        String url = "";
        try
        {
            url = PropertyReader.getProperty("escidoc.pubman.survey.title");
        }
        catch (Exception e)
        {
            HomePage.logger.error("Could not read property: 'escidoc.pubman.survey.title' from properties file.", e);
        }
        return url;
    }
    
    /**
     * Reads the survey ToolTip from the properties file.
     */
    public String getSurveyText()
    {
        String url = "";
        try
        {
            url = PropertyReader.getProperty("escidoc.pubman.survey.text");
        }
        catch (Exception e)
        {
            HomePage.logger.error("Could not read property: 'escidoc.pubman.survey.text' from properties file.", e);
        }
        return url;
    }
    
    /**
     * Reads the survey styles from the properties file.
     */
    public String getSurveyStyles()
    {
        String url = "";
        try
        {
            url = PropertyReader.getProperty("escidoc.pubman.survey.styles");
        }
        catch (Exception e)
        {
            HomePage.logger.error("Could not read property: 'escidoc.pubman.survey.styles' from properties file.", e);
        }
        return url;
    }
    
    
   
    
    public boolean isDepositor()
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        return loginHelper.getAccountUser().isDepositor();
    }
    
    public boolean isModerator()
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        return loginHelper.getAccountUser().isModerator();
    }

    @Override
    public boolean isItemSpecific() 
    {
        return false;
    }
    
    public List<PubItemVOPresentation> getLatest() throws Exception
    {
        InitialContext ictx = new InitialContext();
        Search search = (Search)ictx.lookup(Search.SERVICE_NAME);
        String cqlQuery = "escidoc.property.content-model.objid=" + PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
        SearchQuery cql = new PlainCqlQuery(cqlQuery);
        cql.setMaximumRecords("4");
        cql.setSortKeysAndOrder("sort.escidoc.last-modification-date", SortingOrder.DESCENDING);
        ItemContainerSearchResult icsr =  search.searchForItemContainer(cql);
        List<PubItemVOPresentation> list = SearchRetrieverRequestBean.extractItemsOfSearchResult(icsr);
        return list;
    }
}
