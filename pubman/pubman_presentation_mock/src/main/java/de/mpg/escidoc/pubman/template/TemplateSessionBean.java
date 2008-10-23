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

import java.util.ArrayList;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.FacesBean;

/**
 * Template Bean for Markus Meichau in scope session
 * @author schraut
 *
 */
public class TemplateSessionBean extends FacesBean
{
    public static final String BEAN_NAME = "TemplateSessionBean";
    
    // constants for comboBoxes and HtmlSelectOneRadios
    public SelectItem OPTION1 = new SelectItem("OPTION1", "Option1");
    public SelectItem OPTION2 = new SelectItem("OPTION2", "Option2");
    public SelectItem OPTION3 = new SelectItem("OPTION3", "Option3");
    public SelectItem OPTION4 = new SelectItem("OPTION4", "Option4");
    public SelectItem OPTION5 = new SelectItem("OPTION5", "Option5");
    public SelectItem OPTION6 = new SelectItem("OPTION6", "Option6");
    public SelectItem[] LISTE_OPTIONS = new SelectItem[]{OPTION1, OPTION2, OPTION3, OPTION4, OPTION5, OPTION6};
    
    private String selectedItemSessionBean = "OPTION3";
    
    private ArrayList<String> selectedItemList = new ArrayList<String>();
    
    /**
     * Public constructor.
     */
    public TemplateSessionBean()
    {
        this.init();
    }

       
    /**
     * This method is called when this bean is initially added to session scope. Typically, this occurs as a result of
     * evaluating a value binding or method binding expression, which utilizes the managed bean facility to instantiate
     * this bean and store it into session scope.
     */    
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }


	public SelectItem[] getLISTE_OPTIONS() {
		return LISTE_OPTIONS;
	}


	public void setLISTE_OPTIONS(SelectItem[] liste_options) {
		LISTE_OPTIONS = liste_options;
	}


	public String getSelectedItemSessionBean() {
		return selectedItemSessionBean;
	}


	public void setSelectedItemSessionBean(String selectedItemSessionBean) {
		this.selectedItemSessionBean = selectedItemSessionBean;
	}


	public ArrayList<String> getSelectedItemList() {
		return selectedItemList;
	}


	public void setSelectedItemList(ArrayList<String> selectedItemList) {
		this.selectedItemList = selectedItemList;
	}


	
    
}
