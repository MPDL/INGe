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

package de.mpg.escidoc.pubman.search;

import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;

import de.mpg.escidoc.pubman.search.ui.AffiliationDetailUI;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;

/**
 * Class for the AffiliationDetail Page.
 * This class holds the AffiliationVO to be displayed in the AffiliationDetail Page.  
 *
 * @author:  Hugo Niedermaier, created 24.07.2007
 * @version: $Revision$ $LastChangedDate$
 * Revised by NiH: 14.08.2007
 */
public class AffiliationDetail
{
    public static final String BEAN_NAME = "AffiliationDetail";
    
    private AffiliationVO affiliationVO;
    
    private boolean renderDetails = false;
    
    private HtmlPanelGroup panelAffiliationDetail = new HtmlPanelGroup();
    private AffiliationDetailUI affiliationDetailUI;
    private HTMLElementUI htmlElement = new HTMLElementUI();
    private HtmlPanelGroup panDetail = new HtmlPanelGroup();
    private HtmlOutputText txtName = new HtmlOutputText();
    
    /**
     * Public constructor.
     */
    public AffiliationDetail()
    {
    }

    public AffiliationVO getAffiliationVO()
    {
        return affiliationVO;
    }
    

    public void setAffiliationVO(AffiliationVO affiliationVO)
    {
        this.affiliationVO = affiliationVO;
    }

    public String toggleDetails()
    {
    	renderDetails = !renderDetails;
    	return null;
    }
    
    public HtmlPanelGroup getPanelAffiliationDetail()
    {
        return panelAffiliationDetail;
    }

    public void setPanelAffiliationDetail(HtmlPanelGroup panelAffiliationDetail)
    {
        this.panelAffiliationDetail = panelAffiliationDetail;
    }

	public boolean getRenderDetails() {
		return renderDetails;
	}

	public void setRenderDetails(boolean renderDetails) {
		this.renderDetails = renderDetails;
	}
    
}
