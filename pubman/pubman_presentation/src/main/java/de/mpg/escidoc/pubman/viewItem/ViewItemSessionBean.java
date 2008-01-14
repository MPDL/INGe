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

import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import com.sun.rave.web.ui.component.Hyperlink;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.desktop.Login;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;

/**
 * Keeps all attributes that are used for the whole session by ViewItem.
 * 
 * @author: Thomas Diebäcker, created 30.05.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $ Revised by ScT: 22.08.2007
 */
public class ViewItemSessionBean extends AbstractSessionBean
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
    private Hyperlink lnkEdit = new Hyperlink();
    private Hyperlink lnkSubmit = new Hyperlink();
    private Hyperlink lnkDelete = new Hyperlink();
    private Hyperlink lnkWithdraw = new Hyperlink();
    private Hyperlink lnkNewSubmission = new Hyperlink();
    private Hyperlink lnkModify = new Hyperlink();
    private Hyperlink lnkCreateNewRevision = new Hyperlink();    
    
    // Flag if view item has already been redirected
    private boolean hasBeenRedirected = false;

    
    /**
     * Public constructor.
     */
    public ViewItemSessionBean()
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
            Login login = (Login)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "desktop$Login");
            login.forceLogout();
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
        return (ViewItemFull)getBean(ViewItemFull.BEAN_NAME);
    }
    
    /**
     * Returns the ItemControllerSessionBean.
     * 
     * @return a reference to the scoped data bean (ItemControllerSessionBean)
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.BEAN_NAME);
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

    public void setItemListSessionBean(ItemListSessionBean itemListSessionBean)
    {
        this.itemListSessionBean = itemListSessionBean;
    }
    
    public Hyperlink getLnkDelete()
    {
        return lnkDelete;
    }

    public void setLnkDelete(Hyperlink lnkDelete)
    {
        this.lnkDelete = lnkDelete;
    }

    public Hyperlink getLnkEdit()
    {
        return lnkEdit;
    }

    public void setLnkEdit(Hyperlink lnkEdit)
    {
        this.lnkEdit = lnkEdit;
    }

    public Hyperlink getLnkNewSubmission()
    {
        return lnkNewSubmission;
    }

    public void setLnkNewSubmission(Hyperlink lnkNewSubmission)
    {
        this.lnkNewSubmission = lnkNewSubmission;
    }

    public Hyperlink getLnkSubmit()
    {
        return lnkSubmit;
    }

    public void setLnkSubmit(Hyperlink lnkSubmit)
    {
        this.lnkSubmit = lnkSubmit;
    }

    public Hyperlink getLnkWithdraw()
    {
        return lnkWithdraw;
    }

    public void setLnkWithdraw(Hyperlink lnkWithdraw)
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

    public Hyperlink getLnkModify()
    {
        return lnkModify;
    }

    public void setLnkModify(Hyperlink lnkModify)
    {
        this.lnkModify = lnkModify;
    }

    public Hyperlink getLnkCreateNewRevision()
    {
        return lnkCreateNewRevision;
    }

    public void setLnkCreateNewRevision(Hyperlink lnkCreateNewRevision)
    {
        this.lnkCreateNewRevision = lnkCreateNewRevision;
    }        
}
