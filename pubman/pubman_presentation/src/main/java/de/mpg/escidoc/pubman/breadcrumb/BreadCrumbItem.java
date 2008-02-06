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

package de.mpg.escidoc.pubman.breadCrumb;

import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.util.InternationalizationHelper;

/**
 * Class for single breadcrumbs. Each breadcrumb is represented with this class.
 * 
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $ Revised by ScT: 16.08.2007
 */
public class BreadCrumbItem
{
    // Constants for the different pages (the referring jsp page)
    public static final String DEPOSITOR_WS_PAGE = "DepositorWSPage.jsp";
    public static final String EDIT_ITEM_PAGE = "EditItemPage.jsp";
    public static final String ERROR_PAGE = "ErrorPage.jsp";
    public static final String HOME_PAGE = "HomePage.jsp";
    public static final String LOGIN_ERROR_PAGE = "LoginErrorPage.jsp";
    public static final String SEARCH_RESULT_LIST_PAGE = "SearchResultListPage.jsp";
    public static final String SEARCH_RESULT_NORESULT_PAGE = "NoSearchResultsPage.jsp";
    public static final String AFFILIATION_SEARCH_RESULT_LIST_PAGE = "AffiliationSearchResultListPage.jsp";
    public static final String VIEW_ITEM_PAGE = "viewItemFullPage.jsp";
    public static final String AFFILIATION_TREE_PAGE = "AffiliationTreePage.jsp";
    public static final String ADVANCED_SERACH_PAGE = "AdvancedSearchPage.jsp";
    public static final String WITHDRAW_ITEM_PAGE = "WithdrawItemPage.jsp";
    public static final String CREATE_ITEM_PAGE = "CreateItemPage.jsp";
    public static final String EXPORT_EMAIL_PAGE = "ExportEmailPage.jsp";
    public static final String CREATE_REVISION_PAGE = "CreateRevisionPage.jsp";
    public static final String CREATE_REVISION_COLLECTION_PAGE = "CreateRevisionChooseCollectionPage.jsp";
    public static final String VIEW_REVISIONS_PAGE = "ViewItemRevisionsPage.jsp";
    public static final String VIEW_RELEASE_HISTORY_PAGE = "ViewItemReleaseHistoryPage.jsp";

    // Constants for the different pages, internationalization for the display name will be handled by BreadCrumbItem#getDisplayValue()
    public static final BreadCrumbItem HOME = new BreadCrumbItem("HomePage", HOME_PAGE);
    public static final BreadCrumbItem DEPOSITOR_WS = new BreadCrumbItem("DepositorWSPage", DEPOSITOR_WS_PAGE);
    public static final BreadCrumbItem SEARCH_RESULT_LIST = new BreadCrumbItem("SearchResultListPage", SEARCH_RESULT_LIST_PAGE);
    public static final BreadCrumbItem SEARCH_RESULT_NORESULT_LIST = new BreadCrumbItem("NoSearchResultListPage", SEARCH_RESULT_NORESULT_PAGE);
    public static final BreadCrumbItem AFFILIATION_SEARCH_RESULT_LIST = new BreadCrumbItem("AffiliationSearchResultListPage", AFFILIATION_SEARCH_RESULT_LIST_PAGE);
    public static final BreadCrumbItem VIEW_ITEM = new BreadCrumbItem("ViewItemPage", VIEW_ITEM_PAGE);
    public static final BreadCrumbItem EDIT_ITEM = new BreadCrumbItem("EditItemPage", EDIT_ITEM_PAGE);
    public static final BreadCrumbItem ERROR = new BreadCrumbItem("ErrorPage", ERROR_PAGE);
    public static final BreadCrumbItem AFFILIATION_TREE = new BreadCrumbItem("AffiliationTreePage", AFFILIATION_TREE_PAGE);
    public static final BreadCrumbItem ADVANCED_SEARCH = new BreadCrumbItem("AdvancedSearchPage", ADVANCED_SERACH_PAGE);
    public static final BreadCrumbItem WITHDRAW_ITEM = new BreadCrumbItem("WithdrawItemPage", WITHDRAW_ITEM_PAGE);
    public static final BreadCrumbItem CREATE_ITEM = new BreadCrumbItem("CreateItemPage", CREATE_ITEM_PAGE);
    public static final BreadCrumbItem EXPORT_EMAIL = new BreadCrumbItem("ExportEmailPage", EXPORT_EMAIL_PAGE);
    public static final BreadCrumbItem CREATE_REVISION = new BreadCrumbItem("CreateRevisionPage", CREATE_REVISION_PAGE);
    public static final BreadCrumbItem CREATE_REVISION_COLLECTION = new BreadCrumbItem("CreateRevisionChooseCollectionPage", CREATE_REVISION_COLLECTION_PAGE);
    public static final BreadCrumbItem VIEW_REVISIONS = new BreadCrumbItem("ViewItemRevisionsPage", VIEW_REVISIONS_PAGE);
    public static final BreadCrumbItem VIEW_RELEASE_HISTORY = new BreadCrumbItem("ViewItemReleaseHistoryPage", VIEW_RELEASE_HISTORY_PAGE);

	
	// The String that should be displayed in the breadcrumb menu, e.g. "ViewItem"
    private String displayValue;
    
    // The jsp page that should be addressed when the link in the breadcrumb
    // navigation is clicked, e.g. "ViewItem.jsp"
    private String page;

    /**
     * Public constructor.
     */
    private BreadCrumbItem()
    {
    }

    /**
     * private constructor(with two parameters, the value to display and the page name that should be displayed).
     * You may only use one of the public static final BreadCrumbItem's defined above
     */
    private BreadCrumbItem(String displayValue, String page)
    {
        this.displayValue = displayValue;
        this.page = page;
    }

    /**
     * Internatiolization is supported by this getter
     * @return displayValue to label this BreadCrumbItem
     */
    public String getDisplayValue()
    {
        Application application = FacesContext.getCurrentInstance().getApplication();
        InternationalizationHelper i18nHelper =  (InternationalizationHelper)application.getVariableResolver()
        	.resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        ResourceBundle bundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());

        return bundle.getString(displayValue);
    }

    public void setDisplayValue(String displayValue)
    {
        this.displayValue = displayValue;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }
}
