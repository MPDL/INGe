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

package de.mpg.escidoc.pubman.qaws;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;


/**
 * TODO Session Bean for the Quality Assurance Workspace, keeps all attributes
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class QAWSSessionBean extends FacesBean
{
    public static final String BEAN_NAME = "QAWSSessionBean";
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(QAWSSessionBean.class);

    /** value for the selected collection */
    private String selectedContextId = null;
    
    private AffiliationVO selectedAffiliationVO;
    
    /** value for the selected organizational unit */
    private String selectedOUId = null;
    
    private String selectedItemState = "SUBMITTED";
    
    private List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
    
    /**
     * The currently selected context filter.
     */
    private String selectedContext;
    
    /**
     * The currently selected org unit.
     */
    private String selectedOrgUnit;

 
    

    /**
     * Public constructor.
     */
    public QAWSSessionBean()
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

    

    public List<PubItemVO> getPubItemList()
    {
        return pubItemList;
    }

    public void setPubItemList(List<PubItemVO> pubItemList)
    {
        this.pubItemList = pubItemList;
    }

    public String getSelectedContextId()
    {
        return selectedContextId;
    }

    public void setSelectedContextId(String selectedContextId)
    {
        this.selectedContextId = selectedContextId;
    }

    public String getSelectedOUId()
    {
        return selectedOUId;
    }

    public void setSelectedOUId(String selectedOUId)
    {
        this.selectedOUId = selectedOUId;
    }

    public String getSelectedItemState()
    {
        return selectedItemState;
    }

    public void setSelectedItemState(String selectedItemState)
    {
        this.selectedItemState = selectedItemState;
    }

	public String getSelectedContext() {
		return selectedContext;
	}

	public void setSelectedContext(String selectedContext) {
		this.selectedContext = selectedContext;
	}

	public String getSelectedOrgUnit() {
		return selectedOrgUnit;
	}

	public void setSelectedOrgUnit(String selectedOrgUnit) {
		this.selectedOrgUnit = selectedOrgUnit;
	}
}
