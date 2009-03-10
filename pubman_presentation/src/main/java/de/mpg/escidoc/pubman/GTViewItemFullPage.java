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

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.viewItem.ViewItemSessionBean;

/**
 * GTviewItemPage.java Backing bean for GTviewItemPage.jsp This is for the GUI tool mode. The pubman frame will not be
 * displayed.
 * 
 * @author: Tobias Schraut, created 13.11.2007
 * @version: $Revision$ $LastChangedDate: 2007-08-23 10:56:18 +0200 (Do, 23 Aug 2007)
 */
public class GTViewItemFullPage extends FacesBean
{
    final public static String BEAN_NAME = "GTViewItemFullPage";
    private static Logger logger = Logger.getLogger(GTViewItemFullPage.class);

    /**
     * Public constructor
     */
    public GTViewItemFullPage()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page is navigated to, either directly via a URL, or indirectly via page
     * navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();

        // Set the current session to GUI Tool
        CommonSessionBean sessionBean = getSessionBean();
        //this.getViewItemSessionBean().setHasBeenRedirected(true);
        sessionBean.setRunAsGUITool(true);
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

}
