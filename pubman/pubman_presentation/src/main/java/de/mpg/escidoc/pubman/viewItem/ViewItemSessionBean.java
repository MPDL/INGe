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

package de.mpg.escidoc.pubman.viewItem;

import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.desktop.Login;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * Keeps all attributes that are used for the whole session by ViewItem.
 * 
 * @author: Thomas Diebäcker, created 30.05.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $ Revised by ScT: 22.08.2007
 */
public class ViewItemSessionBean extends FacesBean
{
    public static final String BEAN_NAME = "ViewItemSessionBean";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ViewItemSessionBean.class);
    // navigationString to go back to the list where viewItem has been called
    // from
    private String navigationStringToGoBack = null;
    // navigationString to go back to the list where viewItem has been called
    // from
    private String itemIdViaURLParam = null;
    
    // The according ItemListSessionBean
    private ItemListSessionBean itemListSessionBean = null;
    
    // the action links
    private HtmlCommandLink lnkEdit = new HtmlCommandLink();
    private HtmlCommandLink lnkSubmit = new HtmlCommandLink();
    private HtmlCommandLink lnkRelease = new HtmlCommandLink();
    private HtmlCommandLink lnkDelete = new HtmlCommandLink();
    private HtmlCommandLink lnkWithdraw = new HtmlCommandLink();
    private HtmlCommandLink lnkNewSubmission = new HtmlCommandLink();
    private HtmlCommandLink lnkModify = new HtmlCommandLink();
    private HtmlCommandLink lnkCreateNewRevision = new HtmlCommandLink(); 
    private HtmlCommandLink lnkCreateItemFromTemplate = new HtmlCommandLink(); 
    
    //the basic links
    private HtmlCommandLink lnkViewReleaseHistory = new HtmlCommandLink();
    private HtmlCommandLink lnkViewRevisions = new HtmlCommandLink();
    private HtmlCommandLink lnkViewStatistics = new HtmlCommandLink();
    private HtmlCommandLink lnkViewLog = new HtmlCommandLink();
    
    // Flag if view item has already been redirected
    private boolean hasBeenRedirected = false;
    
    private String subMenu;

    
    /**
     * Public constructor.
     */
    public ViewItemSessionBean()
    {
        this.init();
        subMenu="ACTIONS";
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
     * View the selected item. 
     * 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String viewRelease()
    {
        String itemID = getFacesParamValue("itemID").substring(13).replace("-", ":");
        
        PubItemVO pubItemVO = null;
        
        // set the reload flag to false to force a redirecting to get a proper URL
        this.hasBeenRedirected = false;
        
        try
        {
            pubItemVO = this.getItemControllerSessionBean().retrieveItem(itemID);
        }
        catch (Exception e)
        {
            logger.error("Could not retrieve release with id " + itemID, e);
            Login login = (Login)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "Login");
            try
            {
                login.forceLogout();
            }
            catch (Exception e2) {
                logger.error("Error logging out user", e2);
            }
            return "";
        }
        
        this.getItemControllerSessionBean().setCurrentPubItem(pubItemVO);
        
        return ViewItemFull.LOAD_VIEWITEM;
    }
    
    /**
     * gets the parameters out of the faces context
     * 
     * @param name name of the parameter in the faces context
     * @return the value of the parameter as string
     */
    public static String getFacesParamValue(String name)
    {
        return (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }
    
    /**
     * Returns the ViewItemFull bean.
     * 
     * @return a reference to the scoped data bean (ViewItemFull)
     */
    protected ViewItemFull getViewItemFull()
    {
        return (ViewItemFull)getBean(ViewItemFull.class);
    }
    
    /**
     * Returns the ItemControllerSessionBean.
     * 
     * @return a reference to the scoped data bean (ItemControllerSessionBean)
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
    }

    // Getters and Setters
    public String getNavigationStringToGoBack()
    {
        return navigationStringToGoBack;
    }

    public void setNavigationStringToGoBack(String navigationStringToGoBack)
    {
        this.navigationStringToGoBack = navigationStringToGoBack;
    }

    public String getItemIdViaURLParam()
    {
        return itemIdViaURLParam;
    }

    public void setItemIdViaURLParam(String itemIdViaURLParam)
    {
        this.itemIdViaURLParam = itemIdViaURLParam;
    }

    public ItemListSessionBean getItemListSessionBean()
    {
        return itemListSessionBean;
    }

    public void setItemListSessionBean(ItemListSessionBean ItemListSessionBean)
    {
        this.itemListSessionBean = ItemListSessionBean;
    }
    
    public HtmlCommandLink getLnkDelete()
    {
        return lnkDelete;
    }

    public void setLnkDelete(HtmlCommandLink lnkDelete)
    {
        this.lnkDelete = lnkDelete;
    }

    public HtmlCommandLink getLnkEdit()
    {
        return lnkEdit;
    }

    public void setLnkEdit(HtmlCommandLink lnkEdit)
    {
        this.lnkEdit = lnkEdit;
    }

    public HtmlCommandLink getLnkNewSubmission()
    {
        return lnkNewSubmission;
    }

    public void setLnkNewSubmission(HtmlCommandLink lnkNewSubmission)
    {
        this.lnkNewSubmission = lnkNewSubmission;
    }

    public HtmlCommandLink getLnkSubmit()
    {
        return lnkSubmit;
    }

    public void setLnkSubmit(HtmlCommandLink lnkSubmit)
    {
        this.lnkSubmit = lnkSubmit;
    }

    public HtmlCommandLink getLnkWithdraw()
    {
        return lnkWithdraw;
    }

    public void setLnkWithdraw(HtmlCommandLink lnkWithdraw)
    {
        this.lnkWithdraw = lnkWithdraw;
    }

    public boolean isHasBeenRedirected()
    {
        return hasBeenRedirected;
    }

    public void setHasBeenRedirected(boolean hasBeenRedirected)
    {
        this.hasBeenRedirected = hasBeenRedirected;
    }

    public HtmlCommandLink getLnkModify()
    {
        return lnkModify;
    }

    public void setLnkModify(HtmlCommandLink lnkModify)
    {
        this.lnkModify = lnkModify;
    }

    public HtmlCommandLink getLnkCreateNewRevision()
    {
        return lnkCreateNewRevision;
    }

    public void setLnkCreateNewRevision(HtmlCommandLink lnkCreateNewRevision)
    {
        this.lnkCreateNewRevision = lnkCreateNewRevision;
    }

    public HtmlCommandLink getLnkCreateItemFromTemplate() {
		return lnkCreateItemFromTemplate;
	}

	public void setLnkCreateItemFromTemplate(
			HtmlCommandLink lnkCreateItemFromTemplate) {
		this.lnkCreateItemFromTemplate = lnkCreateItemFromTemplate;
	}

	public HtmlCommandLink getLnkViewReleaseHistory()
    {
        return lnkViewReleaseHistory;
    }

    public void setLnkViewReleaseHistory(HtmlCommandLink lnkViewReleaseHistory)
    {
        this.lnkViewReleaseHistory = lnkViewReleaseHistory;
    }

    public HtmlCommandLink getLnkViewRevisions()
    {
        return lnkViewRevisions;
    }

    public void setLnkViewRevisions(HtmlCommandLink lnkViewRevisions)
    {
        this.lnkViewRevisions = lnkViewRevisions;
    }

    public HtmlCommandLink getLnkViewStatistics()
    {
        return lnkViewStatistics;
    }

    public void setLnkViewStatistics(HtmlCommandLink lnkViewStatistics)
    {
        this.lnkViewStatistics = lnkViewStatistics;
    }

    public HtmlCommandLink getLnkViewLog()
    {
        return lnkViewLog;
    }

    public void setLnkViewLog(HtmlCommandLink lnkViewLog)
    {
        this.lnkViewLog = lnkViewLog;
    }

    public HtmlCommandLink getLnkRelease()
    {
        return lnkRelease;
    }

    public void setLnkRelease(HtmlCommandLink lnkRelease)
    {
        this.lnkRelease = lnkRelease;
    }

    public void setSubMenu(String subMenu)
    {
        this.subMenu = subMenu;
    }

    public String getSubMenu()
    {
        return subMenu;
    } 
    
    public void itemChanged()
    {
        subMenu="ACTIONS";
    }
}
