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

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.xml.rpc.ServiceException;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import com.sun.rave.web.ui.component.Page;
import de.mpg.escidoc.pubman.CommonSessionBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;

/**
 * Backing bean for ViewItemFullPage.jsp (for viewing items in a full context). 
 * 
 * @author Tobias Schraut, created 03.09.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 */
public class ViewItemFullPage extends AbstractPageBean
{
    private static Logger logger = Logger.getLogger(ViewItemFullPage.class);
    
    // this attribute is for connecting the GTEditItemPage.jsp with this backing bean
    @SuppressWarnings("unused")
    private Page page = new Page();
    // The referring GUI Tool Page
    public final static String GT_VIEW_ITEM_FULL_PAGE = "GTViewItemFullPage.jsp";
    
    /**
     * Public constructor.
     */
    public ViewItemFullPage()
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
            this.getViewItemSessionBean().setHasBeenRedirected(true);
            fc.getExternalContext().redirect(GT_VIEW_ITEM_FULL_PAGE);
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
     * Returns the ViewItemSessionBean.
     * 
     * @return a reference to the scoped data bean (ViewItemSessionBean)
     */
    protected ViewItemSessionBean getViewItemSessionBean()
    {
        return (ViewItemSessionBean)getBean(ViewItemSessionBean.BEAN_NAME);
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
