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

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.viewItem.ViewItemSessionBean;

 
/**
 * BackingBean for Quality Assurance Page (QAWSPage.jsp).
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class QAWSPage extends BreadcrumbPage
{
    private static Logger logger = Logger.getLogger(QAWSPage.class);
    public static final String BEAN_NAME = "QAWSPage";
    
    
    
    /**
     * Public constructor.
     */
    public QAWSPage()
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
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        String userHandle = request.getParameter(LoginHelper.PARAMETERNAME_USERHANDLE);

        if (logger.isDebugEnabled())
        {
            logger.debug("UserHandle: " + userHandle);
        }        
        
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        if(loginHelper == null)
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
        
        this.getViewItemSessionBean().setHasBeenRedirected(true);
        
        
    }

    
    
   
    
    
    /**
     * Returns the CommonSessionBean.
     * @return a reference to the scoped data bean (CommonSessionBean)
     */
    protected CommonSessionBean getCommonSessionBean()
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
    
    public boolean getIsModerator()
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        boolean isModerator = false;
        
        if (loginHelper.isLoggedIn()) 
        {
            isModerator = loginHelper.getAccountUser().isModerator();
        }
        
        return isModerator;
        
            
    }

	@Override
	public boolean isItemSpecific() 
	{
		return false;
	}

}
