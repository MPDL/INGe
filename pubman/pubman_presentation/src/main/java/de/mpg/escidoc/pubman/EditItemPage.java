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

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;

/**
 * BackingBean for EditItemPage.jsp.
 * This one is empty because all code is implemented in the BackingBean of the fragment.
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 * Revised by DiT: 14.08.2007
 */
public class EditItemPage extends BreadcrumbPage
{
    private static Logger logger = Logger.getLogger(EditItemPage.class);

    //The referring GUI Tool Page
    public final static String GT_EDIT_ITEM_PAGE = "GTEditItemPage.jsp";
    
    /**
     * Public constructor.
     */
    public EditItemPage()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. 
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        
        //redirect to the referring GUI Tool page if the application has been started as GUI Tool
        CommonSessionBean sessionBean = getCommonSessionBean();
        if(sessionBean.isRunAsGUITool() == true)
        {
            redirectToGUITool();
        }
    }
    
    /**
     * Redirects to the referring GUI Tool page.
     * @author Tobias Schraut
     * @return a navigation string
     */
    protected String redirectToGUITool()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        try
        {
            fc.getExternalContext().redirect(GT_EDIT_ITEM_PAGE);
        }
        catch (IOException e)
        {
            logger.error("Could not redirect to GUI Tool Search result list page." + "\n" + e.toString());
        }
        return "";
    }
    
    /**
     * Returns the CommonSessionBean.
     * @return a reference to the scoped data bean (CommonSessionBean)
     */
    protected CommonSessionBean getCommonSessionBean()
    {
        return (CommonSessionBean)getSessionBean(CommonSessionBean.class);
    }

}
