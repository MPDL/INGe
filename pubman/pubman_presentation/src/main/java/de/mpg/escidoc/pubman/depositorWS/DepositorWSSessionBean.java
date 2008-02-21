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

package de.mpg.escidoc.pubman.depositorWS;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;

/**
 * Keeps all attributes that are used for the whole session by the DepositorWS.
 * @author:  Thomas Diebäcker, created 10.01.2007
 * @version: $Revision: 1675 $ $LastChangedDate: 2007-12-14 13:47:11 +0100 (Fr, 14 Dez 2007) $
 * Revised by DiT: 09.08.2007
 */
public class DepositorWSSessionBean extends FacesBean
{
    public static final String BEAN_NAME = "DepositorWSSessionBean";
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(DepositorWSSessionBean.class);

    /** default value for the selected item state */
    private String selectedItemState = "PENDING";

    /**
     * ScT: the main menu topics on the left side.
     */
    private boolean myWorkspace = false;
    private boolean depositorWS = false;
    private boolean newSubmission = false;

    /**
     * Public constructor.
     */
    public DepositorWSSessionBean()
    {
    	
        //this.init();
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

    public String getSelectedItemState()
    {
        return selectedItemState;
    }

    public void setSelectedItemState(String selectedItemState)
    {
        this.selectedItemState = selectedItemState;
    }

    public boolean getDepositorWS()
    {
        return depositorWS;
    }

    public void setDepositorWS(boolean depositorWS)
    {
        this.depositorWS = depositorWS;
    }

    public boolean getMyWorkspace()
    {
        return myWorkspace;
    }

    public void setMyWorkspace(boolean myWorkspace)
    {
        this.myWorkspace = myWorkspace;
    }

    public boolean getNewSubmission()
    {
        return newSubmission;
    }

    public void setNewSubmission(boolean newSubmission)
    {
        this.newSubmission = newSubmission;
    }
}
