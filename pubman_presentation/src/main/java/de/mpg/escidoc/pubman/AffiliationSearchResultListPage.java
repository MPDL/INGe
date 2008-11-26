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
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;
import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.search.SearchResultList;

/**
 * Fragment class for the AffiliationSearchResultListPage. This class provides
 * all functionality for choosing and viewing one or more items out of a list of
 * SearchResults.
 * 
 * @author: Tobias Schraut, created 14.08.2007
 * @version: $Revision: 1691 $ $LastChangedDate: 2007-12-18 09:30:58 +0100 (Di, 18 Dez 2007) $
 * Revised by NiH: 13.09.2007
 */
public class AffiliationSearchResultListPage extends BreadcrumbPage
{
    private static Logger logger = Logger
            .getLogger(AffiliationSearchResultListPage.class);

    // The referring GUI Tool Page
    public final static String GT_AFFILIATION_SEARCH_RESULTLIST_PAGE = "GTAffiliationSearchResultListPage.jsp";

    /**
     * Construct a new Page bean instance.
     */
    public AffiliationSearchResultListPage()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page is navigated to, either
     * directly via a URL, or indirectly via page navigation. Customize this
     * method to acquire resources that will be needed for event handlers and
     * lifecycle methods, whether or not this page is performing post back
     * processing.
     * Note that, if the current request is a postback, the property values of
     * the components do <strong>not</strong> represent any values submitted
     * with this request. Instead, they represent the property values that were
     * saved for this view when it was rendered.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();

        // redirect to the referring GUI Tool page if the application has been
        // started as GUI Tool
        
        CommonSessionBean sessionBean = getCommonSessionBean();
        if(sessionBean.isRunAsGUITool() == true)
        {
            redirectToGUITool();
        }
    }

    /**
     * Redirets to the referring GUI Tool page.
     * 
     * @return a navigation string
     */
    protected String redirectToGUITool()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        try
        {
            fc.getExternalContext().redirect(
                    GT_AFFILIATION_SEARCH_RESULTLIST_PAGE);
        }
        catch (IOException e)
        {
            logger.error("Could not redirect to GUI Tool Affiliation Search result list page."
                            + "\n" + e.toString());
        }
        return "";
    }

  

    /**
     * Returns the CommonSessionBean.
     * 
     * @return a reference to the scoped data bean (CommonSessionBean)
     */
    protected CommonSessionBean getCommonSessionBean()
    {
        return (CommonSessionBean) getBean(CommonSessionBean.class);
    }

}
