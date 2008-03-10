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

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;
import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.viewItem.ViewItemSessionBean;

/**
 * BackingBean for SearchResultListPage.jsp.
 *
 * @author: Thomas Diebäcker, created 24.01.2007
 * @version: $Revision: 1663 $ $LastChangedDate: 2007-12-11 13:11:46 +0100 (Di, 11 Dez 2007) $
 * Revised by DiT: 14.08.2007
 */
public class SearchResultListPage extends BreadcrumbPage
{
    private static Logger logger = Logger.getLogger(SearchResultListPage.class);

    // URL params
    public static final String URL_PARAM_TXT_SEARCH = "txtSearch";
    public static final String URL_PARAM_INCLUDE_FILES = "includeFiles";

    // ScT: The referring GUI Tool Page
    public static final String GT_SEARCH_RESULTLIST_PAGE = "GTSearchResultListPage.jsp";

    /**
     * Public constructor.
     */
    public SearchResultListPage()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();

        this.getViewItemSessionBean().setHasBeenRedirected(true);

        ExportItems fragment = (ExportItems) getSessionBean(ExportItems.class);
        fragment.disableExportPanComps(false);

        //redirect to the referring GUI Tool page if the application has been started as GUI Tool
        CommonSessionBean sessionBean = getCommonSessionBean();
        if (sessionBean.isRunAsGUITool())
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
        return (SearchResultList) getBean(SearchResultList.class);
    }

    /**
     * Handle messages in fragments from here to please JSF life cycle.
     * @author: Michael Franke
     */
    @Override
    public void prerender()
    {

        logger.info(" prerender ExportItems >>>");

        super.prerender();
        SearchResultList fragment = (SearchResultList) getBean(SearchResultList.class);
        fragment.handleMessage();

        ExportItems exportItems = (ExportItems) getBean(ExportItems.class);
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
     * @return a reference to the scoped data bean (CommonSessionBean)
     */
    protected CommonSessionBean getCommonSessionBean()
    {
        return (CommonSessionBean) getBean(CommonSessionBean.class);
    }

    /**
     * Returns the ViewItemSessionBean.
     *
     * @return a reference to the scoped data bean (ViewItemSessionBean)
     */
    protected ViewItemSessionBean getViewItemSessionBean()
    {
        return (ViewItemSessionBean) getBean(ViewItemSessionBean.class);
    }

}
