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

package de.mpg.escidoc.pubman.template;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;


/**
 * test class for Markus Meichau in scope request
 * @author schraut
 *
 */
public class Template extends FacesBean
{
    private static Logger logger = Logger.getLogger(Template.class);
    
    public static final String BEAN_NAME = "Template";

    // constants for comboBoxes and HtmlSelectOneRadios
    public SelectItem LISTENEINTRAG1 = new SelectItem("OPTION1", "Element1");
    public SelectItem LISTENEINTRAG2 = new SelectItem("OPTION2", "Element2");
    public SelectItem LISTENEINTRAG3 = new SelectItem("OPTION3", "Element3");
    public SelectItem LISTENEINTRAG4 = new SelectItem("OPTION4", "Element4");
    public SelectItem LISTENEINTRAG5 = new SelectItem("OPTION5", "Element5");
    public SelectItem LISTENEINTRAG6 = new SelectItem("OPTION6", "Element6");
    public SelectItem[] LISTENEINTRAG_OPTIONS = new SelectItem[]{LISTENEINTRAG1, LISTENEINTRAG2, LISTENEINTRAG3, LISTENEINTRAG4, LISTENEINTRAG5, LISTENEINTRAG6};
    
    private String selectedItemRequestBean = "OPTION4";
 
    
    /**
     * Default constructor.
     */
    public Template()
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

    }

	public SelectItem[] getLISTENEINTRAG_OPTIONS() {
		return LISTENEINTRAG_OPTIONS;
	}

	public void setLISTENEINTRAG_OPTIONS(SelectItem[] listeneintrag_options) {
		LISTENEINTRAG_OPTIONS = listeneintrag_options;
	}

	public String getSelectedItemRequestBean() {
		return selectedItemRequestBean;
	}

	public void setSelectedItemRequestBean(String selectedItemRequestBean) {
		this.selectedItemRequestBean = selectedItemRequestBean;
	}
	
	

	
       
}
