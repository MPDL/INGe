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

import java.util.List;

import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;

import de.mpg.escidoc.pubman.breadcrumb.ui.BreadcrumbUI;

/**
 * Class for creating bread crumb links acording to the requested page.
 *
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision: 1627 $ $LastChangedDate: 2007-11-28 15:05:47 +0100 (Mi, 28 Nov 2007) $ Revised by ScT:
 *           16.08.2007
 */
public class BreadcrumbNavigation extends FacesBean
{
    public static final String BEAN_NAME = "BreadcrumbNavigation";
    private BreadcrumbItemHistorySessionBean breadcrumbItemHistorySessionBean
        = (BreadcrumbItemHistorySessionBean) getSessionBean(BreadcrumbItemHistorySessionBean.class);
    private HtmlPanelGroup panBreadcrumbList = new HtmlPanelGroup();
    private static Logger logger = Logger.getLogger(BreadcrumbNavigation.class);

    /**
     * Public constructor.
     */
    public BreadcrumbNavigation()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page is navigated to, either directly via a URL, or indirectly via page
     * navigation.
     */
    public void init()
    {
        // Perform initializations inherited from the superclass
        super.init();
        String requestedPage = "";
        FacesContext fc = FacesContext.getCurrentInstance();
        requestedPage = fc.getViewRoot().getViewId().substring(1);
        // contruct a list of Breadcrumb entries for generating the Breadcrumb
        // navigation links according to the requested page
        if (requestedPage.equals(BreadcrumbItem.HOME_PAGE) || requestedPage.equals("") || requestedPage.equals("faces"))
        {
            breadcrumbItemHistorySessionBean.clear();
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.HOME);
        }
        else if (requestedPage.equals(BreadcrumbItem.ERROR_PAGE))
        {
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.ERROR);
        }
        else if (requestedPage.equals(BreadcrumbItem.AFFILIATION_TREE_PAGE))
        {
            breadcrumbItemHistorySessionBean.clear();
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.HOME);
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.AFFILIATION_TREE);
        }
        else if (requestedPage.equals(BreadcrumbItem.ADVANCED_SERACH_PAGE))
        {
            breadcrumbItemHistorySessionBean.clear();
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.HOME);
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.ADVANCED_SEARCH);
        }
        else if (requestedPage.equals(BreadcrumbItem.DEPOSITOR_WS_PAGE))
        {
            breadcrumbItemHistorySessionBean.clear();
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.HOME);
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.DEPOSITOR_WS);
        }
        else if (requestedPage.equals(BreadcrumbItem.SEARCH_RESULT_LIST_PAGE))
        {
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.SEARCH_RESULT_LIST);
        }
        else if (requestedPage.equals(BreadcrumbItem.SEARCH_RESULT_NORESULT_PAGE))
        {
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.SEARCH_RESULT_LIST);
        }
        else if (requestedPage.equals(BreadcrumbItem.EXPORT_EMAIL_PAGE))
        {
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.EXPORT_EMAIL);
        }
        else if (requestedPage.equals(BreadcrumbItem.AFFILIATION_SEARCH_RESULT_LIST_PAGE))
        {
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.AFFILIATION_SEARCH_RESULT_LIST);
        }
        else if (requestedPage.equals(BreadcrumbItem.EDIT_ITEM_PAGE))
        {
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.EDIT_ITEM);
        }
        else if (requestedPage.equals(BreadcrumbItem.WITHDRAW_ITEM_PAGE))
        {
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.WITHDRAW_ITEM);
        }
        else if (requestedPage.equals(BreadcrumbItem.VIEW_ITEM_PAGE))
        {
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.VIEW_ITEM);
        }
        else if (requestedPage.equals(BreadcrumbItem.VIEW_REVISIONS_PAGE))
        {
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.VIEW_REVISIONS);
        }
        else if (requestedPage.equals(BreadcrumbItem.VIEW_RELEASE_HISTORY_PAGE))
        {
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.VIEW_RELEASE_HISTORY);
        }
        else if (requestedPage.equals(BreadcrumbItem.CREATE_ITEM_PAGE))
        {
            breadcrumbItemHistorySessionBean.clear();
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.HOME);
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.CREATE_ITEM);
        }
        else if (requestedPage.equals(BreadcrumbItem.CREATE_REVISION_PAGE))
        {
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.CREATE_REVISION);
        }
        else if (requestedPage.equals(BreadcrumbItem.CREATE_REVISION_COLLECTION_PAGE))
        {
            breadcrumbItemHistorySessionBean.push(BreadcrumbItem.CREATE_REVISION_COLLECTION);
        }
        else
        {
            // if the requested page does not fit: it might be not entered into Breadcrumb Navigation yet
            logger.warn("Requested page '" + requestedPage
                    + "' could not be found. Did you forget to enter it into Breadcrumb Navigation?");
        }
        // generate the dynamic bread crumbs
        createDynamicBreadcrumbList();
    }

    /**
     * Creates the panel with bread crumb entries newly according requested page.
     */
    protected void createDynamicBreadcrumbList()
    {
        this.getPanBreadcrumbList().getChildren().clear();
        BreadcrumbUI BreadcrumbUI = new BreadcrumbUI(breadcrumbItemHistorySessionBean.getBreadcrumbItemHistory());
        this.getPanBreadcrumbList().getChildren().add(BreadcrumbUI.getUIComponent());
    }

    // Getters and Setters
    public HtmlPanelGroup getPanBreadcrumbList()
    {
        return panBreadcrumbList;
    }

    public void setPanBreadcrumbList(HtmlPanelGroup panBreadcrumbList)
    {
        this.panBreadcrumbList = panBreadcrumbList;
    }

    public String getDEPOSITOR_WS_PAGE()
    {
        return BreadcrumbItem.DEPOSITOR_WS_PAGE;
    }

    public String getEDIT_ITEM_PAGE()
    {
        return BreadcrumbItem.EDIT_ITEM_PAGE;
    }

    public String getERROR_PAGE()
    {
        return BreadcrumbItem.ERROR_PAGE;
    }

    public String getHOME_PAGE()
    {
        return BreadcrumbItem.HOME_PAGE;
    }

    public String getLOGIN_ERROR_PAGE()
    {
        return BreadcrumbItem.LOGIN_ERROR_PAGE;
    }

    public String getSEARCH_RESULT_LIST_PAGE()
    {
        return BreadcrumbItem.SEARCH_RESULT_LIST_PAGE;
    }

    public String getVIEW_ITEM_PAGE()
    {
        return BreadcrumbItem.VIEW_ITEM_PAGE;
    }

    public String getCREATE_ITEM_PAGE()
    {
        return BreadcrumbItem.CREATE_ITEM_PAGE;
    }

    public String getEXPORT_EMAIL_PAGE()
    {
        return BreadcrumbItem.EXPORT_EMAIL_PAGE;
    }

    public String getSEARCH_RESULT_NORESULT_PAGE()
    {
        return BreadcrumbItem.SEARCH_RESULT_NORESULT_PAGE;
    }

    public String getCREATE_REVISION_PAGE()
    {
        return BreadcrumbItem.CREATE_REVISION_PAGE;
    }

    /**
     * Return the page identifier when a return action is required.
     *
     * @return The page identifier.
     */
    public String getPreviousPageURI()
    {
        List<BreadcrumbItem> BreadcrumbItemList = breadcrumbItemHistorySessionBean.getBreadcrumbItemHistory();
        if (BreadcrumbItemList.size() > 1)
        {
            return BreadcrumbItemList.get(BreadcrumbItemList.size() - 2).getPage();
        }
        else
        {
            return BreadcrumbItem.HOME.getPage();
        }
    }

    /**
     * Return the page name when a return action is required.
     *
     * @return The page name.
     */
    public String getPreviousPageName()
    {
        List<BreadcrumbItem> BreadcrumbItemList = breadcrumbItemHistorySessionBean.getBreadcrumbItemHistory();
        if (BreadcrumbItemList.size() > 1)
        {
            return BreadcrumbItemList.get(BreadcrumbItemList.size() - 2).getDisplayValue();
        }
        else
        {
            return BreadcrumbItem.HOME.getDisplayValue();
        }
    }
}
