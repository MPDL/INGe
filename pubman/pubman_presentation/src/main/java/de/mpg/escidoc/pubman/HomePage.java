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
import javax.xml.rpc.ServiceException;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;

/**
 * BackingBean for HomePage.jsp.
 * 
 * @author: Thomas Diebäcker, created 24.01.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 * Revised by DiT: 14.08.2007
 */
public class HomePage extends AbstractPageBean
{
    private static Logger logger = Logger.getLogger(HomePage.class);
    public static final String BEAN_NAME = "HomePage";

    /**
     * Public constructor.
     */
    public HomePage()
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
        
        // ScT: set the current session to non GUI Tool
        CommonSessionBean sessionBean = getSessionBean();
        sessionBean.setRunAsGUITool(false);
        
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
    }
    
    /**
     * Returns the CommonSessionBean.
     * @return a reference to the scoped data bean (CmmonSessionBean)
     */
    protected CommonSessionBean getSessionBean()
    {
        return (CommonSessionBean)getBean(CommonSessionBean.BEAN_NAME);
    }
}
