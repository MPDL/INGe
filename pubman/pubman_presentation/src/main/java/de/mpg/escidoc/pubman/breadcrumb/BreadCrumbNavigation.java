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

import java.util.List;

import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.sun.rave.web.ui.appbase.AbstractPageBean;

import de.mpg.escidoc.pubman.breadCrumb.ui.BreadCrumbUI;

/**
 * Class for creating bread crumb links acording to the requested page.
 * 
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision: 1627 $ $LastChangedDate: 2007-11-28 15:05:47 +0100 (Wed, 28 Nov 2007) $ Revised by ScT: 16.08.2007
 */
public class BreadCrumbNavigation extends AbstractPageBean
{
    public static final String BEAN_NAME = "breadCrumb$BreadCrumbNavigation";

    
    private BreadCrumbItemHistorySessionBean breadCrumbItemHistorySessionBean = (BreadCrumbItemHistorySessionBean)getBean(BreadCrumbItemHistorySessionBean.BEAN_NAME);
    
    private HtmlPanelGroup panBreadCrumbList = new HtmlPanelGroup();
    private static Logger logger = Logger.getLogger(BreadCrumbNavigation.class);

    /**
     * Public constructor
     */
    public BreadCrumbNavigation()
    {
        
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
        // contruct a list of breadcrumb entries for generating the breadcrumb
        // navigation links according to the requested page
        if (requestedPage.equals(BreadCrumbItem.HOME_PAGE) || requestedPage.equals("") || requestedPage.equals("faces"))
        {
            breadCrumbItemHistorySessionBean.clear();
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.HOME);
        }
        else if (requestedPage.equals(BreadCrumbItem.ERROR_PAGE))
        {
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.ERROR);
        }
        else if (requestedPage.equals(BreadCrumbItem.AFFILIATION_TREE_PAGE))
        {
            breadCrumbItemHistorySessionBean.clear();
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.HOME);
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.AFFILIATION_TREE);
        }
        else if (requestedPage.equals(BreadCrumbItem.ADVANCED_SERACH_PAGE))
        {
            breadCrumbItemHistorySessionBean.clear();
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.HOME);
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.ADVANCED_SEARCH);
        }
        else if (requestedPage.equals(BreadCrumbItem.DEPOSITOR_WS_PAGE))
        {
            breadCrumbItemHistorySessionBean.clear();
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.HOME);
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.DEPOSITOR_WS);
        }
        else if (requestedPage.equals(BreadCrumbItem.SEARCH_RESULT_LIST_PAGE))
        {
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.SEARCH_RESULT_LIST);
        }
        else if (requestedPage.equals(BreadCrumbItem.SEARCH_RESULT_NORESULT_PAGE))
        {
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.SEARCH_RESULT_LIST);
        }
        else if (requestedPage.equals(BreadCrumbItem.EXPORT_EMAIL_PAGE))
        {
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.EXPORT_EMAIL);
        }
        else if (requestedPage.equals(BreadCrumbItem.AFFILIATION_SEARCH_RESULT_LIST_PAGE))
        {
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.AFFILIATION_SEARCH_RESULT_LIST);
        }
        else if (requestedPage.equals(BreadCrumbItem.EDIT_ITEM_PAGE))
        {
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.EDIT_ITEM);
        }
        else if (requestedPage.equals(BreadCrumbItem.WITHDRAW_ITEM_PAGE))
        {
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.WITHDRAW_ITEM);
        }
        else if (requestedPage.equals(BreadCrumbItem.VIEW_ITEM_PAGE))
        {
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.VIEW_ITEM);
        }
        else if (requestedPage.equals(BreadCrumbItem.VIEW_REVISIONS_PAGE))
        {
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.VIEW_REVISIONS);
        }
        else if (requestedPage.equals(BreadCrumbItem.VIEW_RELEASE_HISTORY_PAGE))
        {
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.VIEW_RELEASE_HISTORY);
        }
        else if (requestedPage.equals(BreadCrumbItem.CREATE_ITEM_PAGE))
        {
            breadCrumbItemHistorySessionBean.clear();
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.HOME);
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.CREATE_ITEM);
        }
        else if (requestedPage.equals(BreadCrumbItem.CREATE_REVISION_PAGE))
        {
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.CREATE_REVISION);
        }
        else if (requestedPage.equals(BreadCrumbItem.CREATE_REVISION_COLLECTION_PAGE))
        {
            breadCrumbItemHistorySessionBean.push(BreadCrumbItem.CREATE_REVISION_COLLECTION);
        }
        else
        {
            // if the requested page does not fit: it might be not entered into Breadcrumb Navigation yet
            logger.warn("Requested page '" + requestedPage + "' could not be found. Did you forget to enter it into BreadCrumb Navigation?");
        }

        // generate the dynamic bread crumbs
        createDynamicBreadCrumbList();
    }

    /**
     * Creates the panel with bread crumb entries newly according requested page.
     */
    protected void createDynamicBreadCrumbList()
    {
        this.getPanBreadCrumbList().getChildren().clear();
        BreadCrumbUI breadCrumbUI = new BreadCrumbUI(breadCrumbItemHistorySessionBean.getBreadCrumbItemHistory());
        this.getPanBreadCrumbList().getChildren().add(breadCrumbUI.getUIComponent());
    }

    // Getters and Setters
    public HtmlPanelGroup getPanBreadCrumbList()
    {
        return panBreadCrumbList;
    }

    public void setPanBreadCrumbList(HtmlPanelGroup panBreadCrumbList)
    {
        this.panBreadCrumbList = panBreadCrumbList;
    }

    public String getDEPOSITOR_WS_PAGE()
    {
        return BreadCrumbItem.DEPOSITOR_WS_PAGE;
    }

    public String getEDIT_ITEM_PAGE()
    {
        return BreadCrumbItem.EDIT_ITEM_PAGE;
    }

    public String getERROR_PAGE()
    {
        return BreadCrumbItem.ERROR_PAGE;
    }

    public String getHOME_PAGE()
    {
        return BreadCrumbItem.HOME_PAGE;
    }

    public String getLOGIN_ERROR_PAGE()
    {
        return BreadCrumbItem.LOGIN_ERROR_PAGE;
    }

    public String getSEARCH_RESULT_LIST_PAGE()
    {
        return BreadCrumbItem.SEARCH_RESULT_LIST_PAGE;
    }

    public String getVIEW_ITEM_PAGE()
    {
        return BreadCrumbItem.VIEW_ITEM_PAGE;
    }

    public String getCREATE_ITEM_PAGE()
    {
        return BreadCrumbItem.CREATE_ITEM_PAGE;
    }

    public String getEXPORT_EMAIL_PAGE()
    {
        return BreadCrumbItem.EXPORT_EMAIL_PAGE;
    }

    public String getSEARCH_RESULT_NORESULT_PAGE()
    {
        return BreadCrumbItem.SEARCH_RESULT_NORESULT_PAGE;
    }

    public String getCREATE_REVISION_PAGE()
    {
        return BreadCrumbItem.CREATE_REVISION_PAGE;
    }
    

    public String getPreviousPageURI()
    {
    	List<BreadCrumbItem> breadCrumbItemList = breadCrumbItemHistorySessionBean.getBreadCrumbItemHistory();
    	if (breadCrumbItemList.size() > 1)
    	{
    		return breadCrumbItemList.get(breadCrumbItemList.size() - 2).getPage();
    	}
    	else
    	{
            return BreadCrumbItem.HOME.getPage();
    	}
    }

    public String getPreviousPageName()
    {
    	List<BreadCrumbItem> breadCrumbItemList = breadCrumbItemHistorySessionBean.getBreadCrumbItemHistory();
    	if (breadCrumbItemList.size() > 1)
    	{
    		return breadCrumbItemList.get(breadCrumbItemList.size() - 2).getDisplayValue();
    	}
    	else
    	{
            return BreadCrumbItem.HOME.getDisplayValue();
    	}
    }
    
}
