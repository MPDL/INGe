/*
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
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.export.ExportItemsSessionBean;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;

/**
 * ExportEmailPage.java Backing bean for the ExportEmailPage.jsp
 * 
 * @author: Galina Stancheva, created 07.10.2007
 * @version: $Revision:  $ $LastChangedDate:  $ 
 */
public class ExportEmailPage extends AbstractPageBean
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ExportEmailPage.class);
    // for handling the resource bundles (i18n)
    private Application application = FacesContext.getCurrentInstance().getApplication();
    // get the selected language...
    private InternationalizationHelper i18nHelper = (InternationalizationHelper) application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    protected ResourceBundle bundleMessage = ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle());

    ExportItems fragment = (ExportItems) getBean("export$ExportItems");

    /**
     * Public constructor
     */
    public ExportEmailPage()
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
 
        fragment.disableExportPanComps(true);
        
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getBean(ExportItemsSessionBean.BEAN_NAME);
        
        sb.setNavigationStringToGoBack(SearchResultList.LOAD_SEARCHRESULTLIST);    
        sb.setExportEmailTxt(bundleMessage.getString(ExportItems.MESSAGE_EXPORT_EMAIL_TEXT));
    }

    /*
     * Handle messages in fragments from here to please JSF life cycle.
     * Used to remove the last shown msg
     * @author: Michael Franke
     */
    @Override
    public void prerender()
    {
        super.prerender();
//        fragment.handleMessage();

    }
  



}