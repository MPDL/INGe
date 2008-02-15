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

package de.mpg.escidoc.pubman.breadcrumb;

import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.Internationalized;
import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;

/**
 * Class for single breadcrumbs. Each breadcrumb is represented with this class.
 * 
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $ Revised by ScT: 16.08.2007
 */
public class BreadcrumbItem extends InternationalizedImpl
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

    // Constants for the different pages, internationalization for the display name will be handled by BreadcrumbItem#getDisplayValue()
    public static final BreadcrumbItem HOME = new BreadcrumbItem("HomePage", HOME_PAGE);
    public static final BreadcrumbItem DEPOSITOR_WS = new BreadcrumbItem("DepositorWSPage", DEPOSITOR_WS_PAGE);
    public static final BreadcrumbItem SEARCH_RESULT_LIST = new BreadcrumbItem("SearchResultListPage", SEARCH_RESULT_LIST_PAGE);
    public static final BreadcrumbItem SEARCH_RESULT_NORESULT_LIST = new BreadcrumbItem("NoSearchResultListPage", SEARCH_RESULT_NORESULT_PAGE);
    public static final BreadcrumbItem AFFILIATION_SEARCH_RESULT_LIST = new BreadcrumbItem("AffiliationSearchResultListPage", AFFILIATION_SEARCH_RESULT_LIST_PAGE);
    public static final BreadcrumbItem VIEW_ITEM = new BreadcrumbItem("ViewItemPage", VIEW_ITEM_PAGE);
    public static final BreadcrumbItem EDIT_ITEM = new BreadcrumbItem("EditItemPage", EDIT_ITEM_PAGE);
    public static final BreadcrumbItem ERROR = new BreadcrumbItem("ErrorPage", ERROR_PAGE);
    public static final BreadcrumbItem AFFILIATION_TREE = new BreadcrumbItem("AffiliationTreePage", AFFILIATION_TREE_PAGE);
    public static final BreadcrumbItem ADVANCED_SEARCH = new BreadcrumbItem("AdvancedSearchPage", ADVANCED_SERACH_PAGE);
    public static final BreadcrumbItem WITHDRAW_ITEM = new BreadcrumbItem("WithdrawItemPage", WITHDRAW_ITEM_PAGE);
    public static final BreadcrumbItem CREATE_ITEM = new BreadcrumbItem("CreateItemPage", CREATE_ITEM_PAGE);
    public static final BreadcrumbItem EXPORT_EMAIL = new BreadcrumbItem("ExportEmailPage", EXPORT_EMAIL_PAGE);
    public static final BreadcrumbItem CREATE_REVISION = new BreadcrumbItem("CreateRevisionPage", CREATE_REVISION_PAGE);
    public static final BreadcrumbItem CREATE_REVISION_COLLECTION = new BreadcrumbItem("CreateRevisionChooseCollectionPage", CREATE_REVISION_COLLECTION_PAGE);
    public static final BreadcrumbItem VIEW_REVISIONS = new BreadcrumbItem("ViewItemRevisionsPage", VIEW_REVISIONS_PAGE);
    public static final BreadcrumbItem VIEW_RELEASE_HISTORY = new BreadcrumbItem("ViewItemReleaseHistoryPage", VIEW_RELEASE_HISTORY_PAGE);

	
	// The String that should be displayed in the breadcrumb menu, e.g. "ViewItem"
    private String displayValue;
    
    // The jsp page that should be addressed when the link in the breadcrumb
    // navigation is clicked, e.g. "ViewItem.jsp"
    private String page;
    
    // Flag indicating that this item is the last one.
    private boolean isLast = false;

    /**
     * Default constructor.
     */
    public BreadcrumbItem()
    {
    }

    /**
     * Public constructor(with two parameters, the value to display and the page name that should be displayed).
     * You may only use one of the public static final BreadcrumbItem's defined above.
     */
    public BreadcrumbItem(String displayValue, String page)
    {
        this.displayValue = displayValue;
        this.page = page;
    }

    /**
     * Internationalization is supported by this getter.
     * @return displayValue to label this BreadcrumbItem
     */
    public String getDisplayValue()
    {
        return getLabel(displayValue);
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

    @Override
    public String toString()
    {
        return "[" + displayValue + "]";
    }

    @Override
    public boolean equals(final Object other)
    {
        if (page == null || !(other instanceof BreadcrumbItem))
        {
            return false;
        }

        return (page.equals(((BreadcrumbItem) other).getPage()));
    }

	public boolean getIsLast() {
		return isLast;
	}

	public void setIsLast(boolean isLast) {
		this.isLast = isLast;
	}
}