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
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.viewItem.ViewItem;
import de.mpg.escidoc.pubman.viewItem.ViewItemSessionBean;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;

/**
 * viewItemPage.java Backing bean for viewItemPage.jsp Created on 24. Januar 2007, 18:15 Copyright Tobias Schraut
 * Revised by ScT: 23.08.2007
 */
public class viewItemPage extends AbstractPageBean
{
    final public static String BEAN_NAME = "viewItemPage";
    // The referring GUI Tool Page
    public final static String GT_VIEW_ITEM_PAGE = "GTviewItemPage.jsp";
    private static Logger logger = Logger.getLogger(viewItemPage.class);
    public static final String PARAMETERNAME_ITEM_ID = "itemId";

    /**
     * Public constructor.
     */
    public viewItemPage()
    {
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        // Try to get the request parameter (item ID) out of the faces context
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
        String itemID = request.getParameter(viewItemPage.PARAMETERNAME_ITEM_ID);
        // initialize viewItem
        ViewItem viewItem = (ViewItem)getViewItem();
        // insert the itemID into the view item session bean
        if (itemID != null && !itemID.equals(""))
        {
            this.getViewItemSessionBean().setItemIdViaURLParam(itemID);
            viewItem.loadItem();
        }
        // login the user if he uses the login functionality being on the view item page
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
        // redirect to the referring GUI Tool page if the application has been started as GUI Tool
        CommonSessionBean sessionBean = getCommonSessionBean();
        if (sessionBean.isRunAsGUITool() == true)
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
            fc.getExternalContext().redirect(GT_VIEW_ITEM_PAGE);
        }
        catch (IOException e)
        {
            logger.error("Could not redirect to GUI Tool View item page." + "\n" + e.toString());
        }
        return "";
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
     * Returns a reference to the scoped data bean (the ViewItemSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected ViewItemSessionBean getViewItemSessionBean()
    {
        return (ViewItemSessionBean)getBean(ViewItemSessionBean.BEAN_NAME);
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
}
