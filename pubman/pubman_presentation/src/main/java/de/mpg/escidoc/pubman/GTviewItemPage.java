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
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import com.sun.rave.web.ui.component.Page;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.viewItem.ViewItem;
import de.mpg.escidoc.pubman.viewItem.ViewItemSessionBean;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;

/**
 * GTviewItemPage.java Backing bean for GTviewItemPage.jsp This is for the GUI tool mode. The pubman frame will not be
 * displayed.
 * 
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision: 1687 $ $LastChangedDate: 2007-12-17 15:29:08 +0100 (Mon, 17 Dec 2007) $ Revised by ScT: 20.08.2007
 */
public class GTviewItemPage extends AbstractPageBean
{
    final public static String BEAN_NAME = "viewItemPage";
    private static Logger logger = Logger.getLogger(viewItemPage.class);
    public static final String PARAMETERNAME_ITEM_ID = "itemId";
    // For handling the resource bundles (i18n)
    private Application application = FacesContext.getCurrentInstance().getApplication();
    // get the selected language...
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)application.getVariableResolver()
            .resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    // ... and set the refering resource bundle
    private ResourceBundle bundleMessage = ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle());
    private Page page = new Page();
    /**
     * Question for confirming the deletion of the item
     */
    private String deleteConfirmation;

    /**
     * Public constructor
     */
    public GTviewItemPage()
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
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
        String itemID = request.getParameter(viewItemPage.PARAMETERNAME_ITEM_ID);
        // Set the delete confirmation question in the desired language
        this.deleteConfirmation = this.bundleMessage.getString("ViewItem_deleteConfirmation");
        // initialize viewItem
        ViewItem viewItem = (ViewItem)getBean(ViewItem.BEAN_NAME);
        // insert the itemID into the view item session bean
        if (itemID != null && !itemID.equals(""))
        {
            this.getViewItemSessionBean().setItemIdViaURLParam(itemID);
            viewItem.loadItem();
        }
        // insert the login information (user handle) if possible
        LoginHelper loginHelper = (LoginHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver()
                .resolveVariable(FacesContext.getCurrentInstance(), "LoginHelper");
        if (loginHelper == null)
        {
            loginHelper = new LoginHelper();
        }
        if (loginHelper != null)
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
        // Set the current session to GUI Tool
        CommonSessionBean sessionBean = getSessionBean();
        sessionBean.setRunAsGUITool(true);
    }

    /**
     * Returns a reference to the scoped data bean (the ViewItemSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected ViewItemSessionBean getViewItemSessionBean()
    {
        return (ViewItemSessionBean)getBean(ViewItemSessionBean.BEAN_NAME);
    }

    /**
     * Returns the CommonSessionBean.
     * 
     * @return a reference to the scoped data bean (CmmonSessionBean)
     */
    protected CommonSessionBean getSessionBean()
    {
        return (CommonSessionBean)getBean(CommonSessionBean.BEAN_NAME);
    }

    /**
     * Returns a reference to the scoped data bean (the ViewItemBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected ViewItem getViewItem()
    {
        return (ViewItem)getBean(ViewItem.BEAN_NAME);
    }

    // Getters and Setters
    public String getDeleteConfirmation()
    {
        return deleteConfirmation;
    }

    public void setDeleteConfirmation(String deleteConfirmation)
    {
        this.deleteConfirmation = deleteConfirmation;
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
