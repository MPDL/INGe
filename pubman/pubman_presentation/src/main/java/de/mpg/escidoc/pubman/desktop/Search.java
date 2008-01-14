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

package de.mpg.escidoc.pubman.desktop;

import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractFragmentBean;
import de.mpg.escidoc.pubman.CommonSessionBean;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.search.SearchResultListSessionBean;

/**
 * Search.java Backing bean for the Search.jspf takes the serach values of the jsp page and calls the serach method of
 * pubman_logic
 * 
 * @author: Tobias Schraut, created 24.01.2007
 * @version: $Revision: 1647 $ $LastChangedDate: 2007-12-06 13:28:26 +0100 (Thu, 06 Dec 2007) $ Revised by ScT: 21.08.2007
 */
public class Search extends AbstractFragmentBean
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(Search.class);
    
    // the two fields in the search.jspf
    private HtmlInputText txtSearch = new HtmlInputText();
    private HtmlSelectBooleanCheckbox chkIncludeFiles = new HtmlSelectBooleanCheckbox();
    
    /**
     * Public constructor
     */
    public Search()
    {
    }

    /**
     * Callback method that is called whenever a page is navigated to, either directly via a URL, or indirectly via page
     * navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }

    /**
     * Starts a new search with the binded search criteria in SearchResultListSessionBean.
     * 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String search()
    {
        String retVal = this.getSearchResultList().startSearch();
        CommonSessionBean sessionBean = getCommonSessionBean();
        // if search returns an error, force JSF to load the ErrorPage
        if (retVal == ErrorPage.LOAD_ERRORPAGE)
        {
            // if search has been run as GUI Tool go to the GUI Tool error page.
            if (sessionBean.isRunAsGUITool() == true)
            {
                retVal = ErrorPage.GT_LOAD_ERRORPAGE;
            }
        }
        return retVal;
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.BEAN_NAME);
    }

    /**
     * Returns the SearchResultListSessionBean.
     * 
     * @return a reference to the scoped data bean (SearchResultListSessionBean)
     */
    protected SearchResultListSessionBean getSearchResultListSessionBean()
    {
        return (SearchResultListSessionBean)getBean(SearchResultListSessionBean.BEAN_NAME);
    }

    /**
     * Returns the CommonSessionBean.
     * 
     * @return a reference to the scoped data bean (CmmonSessionBean)
     */
    protected CommonSessionBean getCommonSessionBean()
    {
        return (CommonSessionBean)getBean(CommonSessionBean.BEAN_NAME);
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

    // Getters and Setters
    public HtmlSelectBooleanCheckbox getChkIncludeFiles()
    {
        return chkIncludeFiles;
    }

    public void setChkIncludeFiles(HtmlSelectBooleanCheckbox chkIncludeFiles)
    {
        this.chkIncludeFiles = chkIncludeFiles;
    }

    public HtmlInputText getTxtSearch()
    {
        return txtSearch;
    }

    public void setTxtSearch(HtmlInputText txtSearch)
    {
        this.txtSearch = txtSearch;
    }
}
