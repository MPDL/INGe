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

import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.viewItem.ViewItemSessionBean;

/**
 * GTDepositorWSPage.java Backing bean for GTDepositorWSPage.jsp This is for the GUI tool mode. The pubman frame will
 * not be displayed.
 * 
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision: 1687 $ $LastChangedDate: 2007-12-17 15:29:08 +0100 (Mo, 17 Dez 2007) $ Revised by ScT: 20.08.2007
 */
public class GTDepositorWSPage extends FacesBean
{
    private static Logger logger = Logger.getLogger(DepositorWSPage.class);

    /**
     * Question String for confirming the deletion of the item
     */
    private String deleteConfirmation;

    /**
     * Public constructor.
     */
    public GTDepositorWSPage()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {
        super.init();
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
        String userHandle = request.getParameter(LoginHelper.PARAMETERNAME_USERHANDLE);
        // Set the delete confirmation question in the desired language
        this.deleteConfirmation = getMessage("depositorWS_deleteConfirmation");
        if (logger.isDebugEnabled())
        {
            logger.debug("UserHandle: " + userHandle);
        }
        LoginHelper loginHelper = (LoginHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver()
                .resolveVariable(FacesContext.getCurrentInstance(), "LoginHelper");
        if (loginHelper == null)
        {
            loginHelper = new LoginHelper();
        }
        try
        {
            loginHelper.checkLogin();
        }
        catch (Exception e)
        {
            logger.error("Could not login." + "\n" + e.toString());
        }
        
        this.getViewItemSessionBean().setHasBeenRedirected(false);
        
        // Set the current session to GUI Tool
        CommonSessionBean sessionBean = getSessionBean();
        sessionBean.setRunAsGUITool(true);
    }

    /**
     * Handle messages in fragments from here to please JSF life cycle.
     */
    @Override
    public void prerender()
    {
        super.prerender();
        DepositorWS fragment = (DepositorWS)getBean(DepositorWS.class);
        fragment.handleMessage();
    }

    /**
     * Returns the CommonSessionBean.
     * 
     * @return a reference to the scoped data bean (CommonSessionBean)
     */
    protected CommonSessionBean getSessionBean()
    {
        return (CommonSessionBean)getBean(CommonSessionBean.class);
    }
    
    /**
     * Returns the ViewItemSessionBean.
     * 
     * @return a reference to the scoped data bean (ViewItemSessionBean)
     */
    protected ViewItemSessionBean getViewItemSessionBean()
    {
        return (ViewItemSessionBean)getBean(ViewItemSessionBean.class);
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
}
