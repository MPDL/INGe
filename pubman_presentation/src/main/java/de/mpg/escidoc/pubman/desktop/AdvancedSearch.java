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
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.desktop;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.RightsManagementSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.search.AdvancedSearchEdit;

/**
 * Fragment bean that corresponds to a similarly named JSP page fragment. This class contains component definitions (and
 * initialization code) for all components that you have defined on this fragment, as well as lifecycle methods and
 * event handlers where you may add behavior to respond to incoming events.
 * @author: Hugo Niedermaier
 * Revised by NiH: 20.09.2007
 */
public class AdvancedSearch extends FacesBean
{
    private static Logger logger = Logger.getLogger(AdvancedSearch.class);
    // constant for the function AdvancedSearch to check the rights and/or if the function has to be disabled (DiT)
    private final String FUNCTION_ADVANCED_SEARCH = "advanced_search";
    
    // The referring GUI Tool Page
    public final static String GT_ADVANCED_SEARCH_PAGE = "GTAdvancedSearchPage.jsp";
    
    /**
     * constructor.
     */
    public AdvancedSearch()
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
    }

    /**
     * displays the advanced search page where the user chooses one or more search field(s)
     * and specifies the search string
     * @return (String): the navigation string
     */
    public String showSearchPageAgain()
    {
        return "displaySearchPage";
    }

    /**
     * displays the browse by affiliation page where the user chooses an affiliation
     * @return (String): the navigation string
     */
    public String showBrowsePageAgain()
    {
        return "loadAffiliationTree";
    }
    
    /**
     * displays the advanced search page where the user chooses one or more search field(s)
     * and specifies the search string
     * @return (String): the navigation string
     */
    public String showSearchPage()
    {
        AdvancedSearchEdit bean = (AdvancedSearchEdit)getSessionBean(AdvancedSearchEdit.class);
        
        // init the bean to reset all existing data
        bean.clearAndInitializeAllForms();
        
        return "displaySearchPage";
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
            fc.getExternalContext().redirect(GT_ADVANCED_SEARCH_PAGE);
        }
        catch (IOException e)
        {
            logger.error("Could not redirect to GUI Tool Affiliation tree page."
                            + "\n" + e.toString());
        }
        return "";
    }

    /**
     * Returns the RightsManagementSessionBean.
     * @author DiT
     * @return a reference to the scoped data bean (RightsManagementSessionBean)
     */
    protected RightsManagementSessionBean getRightsManagementSessionBean()
    {
        return (RightsManagementSessionBean)getBean(RightsManagementSessionBean.class);
    }

    /**
     * Returns true if the AdvancedSearch should be disabled by the escidoc properties file.
     * @author DiT
     * @return
     */
    public boolean getDisableAdvancedSearch()
    {
        return this.getRightsManagementSessionBean().isDisabled(RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "." + this.FUNCTION_ADVANCED_SEARCH);
    }
}
