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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import com.sun.rave.web.ui.component.Page;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.search.SearchResultListSessionBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.viewItem.ViewItemSessionBean;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;

/**
 * BackingBean for SearchResultListPage.jsp.
 * 
 * @author: Thomas Diebäcker, created 24.01.2007
 * @version: $Revision: 1663 $ $LastChangedDate: 2007-12-11 13:11:46 +0100 (Tue, 11 Dec 2007) $
 * Revised by DiT: 14.08.2007
 */
public class SearchResultListPage extends AbstractPageBean
{
    private static Logger logger = Logger.getLogger(SearchResultListPage.class);
    
    // this attribute is for connecting the GTEditItemPage.jsp with this backing bean
    @SuppressWarnings("unused")
    private Page page = new Page();
    
    // URL params
    public final static String URL_PARAM_TXT_SEARCH = "txtSearch";
    public final static String URL_PARAM_INCLUDE_FILES = "includeFiles";
    
    // ScT: The referring GUI Tool Page
    public final static String GT_SEARCH_RESULTLIST_PAGE = "GTSearchResultListPage.jsp";
    
    /**
     * Public constructor.
     */
    public SearchResultListPage()
    {
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. 
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
                
        LoginHelper loginHelper = (LoginHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "LoginHelper");
        if(loginHelper == null)
        {
            loginHelper = new LoginHelper();
        }
        else
        {
            try
            {
                try
                {
                    loginHelper.insertLogin();
                }
                catch (UnmarshallingException e)
                {
                    logger.debug(e.toString());
                }
                catch (TechnicalException e)
                {
                    logger.debug(e.toString());
                }
                catch (ServiceException e)
                {
                    logger.debug(e.toString());
                }
            }
            catch (IOException e1)
            {
                logger.debug(e1.toString());
            }
        }
        
        this.getViewItemSessionBean().setHasBeenRedirected(false);
        
        HttpServletRequest request = (HttpServletRequest)FacesContext
            .getCurrentInstance().getExternalContext().getRequest();

        ExportItems fragment = (ExportItems)getBean("export$ExportItems");
        fragment.disableExportPanComps(false);
                 
        
// NiH: to avoid double call of search / advanced search
// this.getSearchResultList().startSearch() is commented out
// @FrM: why do we need this here?
//        this.getSearchResultList().startSearch();
        
        //redirect to the referring GUI Tool page if the application has been started as GUI Tool
        CommonSessionBean sessionBean = getCommonSessionBean();
        if(sessionBean.isRunAsGUITool() == true)
        {
            redirectToGUITool();
        }
    }
    
    /**
     * Returns the SearchResultList.
     * 
     * @return a reference to the scoped data bean (SearchResultList)
     */
    protected SearchResultList getSearchResultList()
    {
        return (SearchResultList)getBean(SearchResultList.BEAN_NAME);
    } 
    
    /*
     * Handle messages in fragments from here to please JSF life cycle.
     * @author: Michael Franke
     */
    @Override
    public void prerender()
    {
    	
    	logger.info(" prerender ExportItems >>>");
    	
        super.prerender();
        SearchResultList fragment = (SearchResultList) getBean("search$SearchResultList");
        fragment.handleMessage();
        
    	ExportItems exportItems = (ExportItems) getBean("export$ExportItems");
    	exportItems.updateExportFormats();
    }
    
    /**
     * Redirects to the referring GUI Tool page.
     * @author Tobias Schraut
     * @return a navigation string
     */
    protected String redirectToGUITool()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try
        {
            facesContext.getExternalContext().redirect(GT_SEARCH_RESULTLIST_PAGE);
        }
        catch (IOException e)
        {
            logger.error("Could not redirect to GUI Tool Search result list page." + "\n" + e.toString());
        }
        
        return "";
    }
    
    /**
     * Returns the CommonSessionBean.
     * @return a reference to the scoped data bean (CmmonSessionBean)
     */
    protected CommonSessionBean getCommonSessionBean()
    {
        return (CommonSessionBean)getBean(CommonSessionBean.BEAN_NAME);
    }
    
    /**
     * Returns the ViewItemSessionBean.
     * 
     * @return a reference to the scoped data bean (ViewItemSessionBean)
     */
    protected ViewItemSessionBean getViewItemSessionBean()
    {
        return (ViewItemSessionBean)getBean(ViewItemSessionBean.BEAN_NAME);
    }

    public Page getPage()
    {
        return page;
    }

    public void setPage(Page page)
    {
        this.page = page;
    }
    
}
