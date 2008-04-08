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
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;

/**
 * Class for the AffiliationDetail Page.
 * This class holds the AffiliationVO to be displayed in the AffiliationDetail Page.  
 *
 * @author:  Hugo Niedermaier, created 24.07.2007
 * @version: $Revision: 1613 $ $LastChangedDate: 2007-11-27 12:42:03 +0100 (Di, 27 Nov 2007) $
 * Revised by NiH: 14.08.2007
 */
public class AffiliationDetail
{
    public static final String BEAN_NAME = "AffiliationDetail";
    
    private AffiliationVO affiliationVO;
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

        this.panelAffiliationDetail.getChildren().clear();
        this.panelAffiliationDetail.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "advancedSearch"));
        this.panDetail.setId(CommonUtils.createUniqueId(this.panDetail));
        this.panDetail.getChildren().clear();
        this.txtName.setId(CommonUtils.createUniqueId(this.txtName));
        this.panDetail.getChildren().add(htmlElement.getStartTag("h3"));
        this.panDetail.getChildren().add(htmlElement.getStartTag("label"));
        if (affiliationVO.getPid() == null)
        {
            if( affiliationVO.getAbbreviation().length() == 0 )
            {
                this.txtName.setValue(affiliationVO.getName() );
            }
            else 
            { 
                this.txtName.setValue(affiliationVO.getName() + " (" + affiliationVO.getAbbreviation() + ")");
            }
        }
        else
        {
            if( affiliationVO.getAbbreviation().length() == 0 )
            {
                this.txtName.setValue(affiliationVO.getName() + ", " + affiliationVO.getPid());        
            }
            else 
            {
                this.txtName.setValue(affiliationVO.getName() + " (" + affiliationVO.getAbbreviation() + ")" + ", " + affiliationVO.getPid());        
            }    
        }
        this.panDetail.getChildren().add(this.txtName);
        this.panDetail.getChildren().add(htmlElement.getEndTag("label"));
        this.panDetail.getChildren().add(htmlElement.getEndTag("h3"));
        String[] detailList = new String[5];
        detailList[0] = affiliationVO.getDescription();
        
        detailList[1] = affiliationVO.getAddress();
        StringBuffer buffer = new StringBuffer();
        buffer.append(affiliationVO.getPostcode());
        buffer.append( " " ); 
        buffer.append(affiliationVO.getCity());
        buffer.append( " " ); 
        buffer.append(affiliationVO.getRegion());
        buffer.append( " " ); 
        buffer.append(affiliationVO.getCountryCode());
        detailList[2] = buffer.toString();
        buffer.setLength(0);
        if (affiliationVO.getTelephone().length() > 0)
        {
            buffer.append("Telephone: ");
            buffer.append(affiliationVO.getTelephone());
            buffer.append( ", " ); 
        }
        if (affiliationVO.getFax().length() > 0)
        {
            buffer.append("Fax: ");
            buffer.append(affiliationVO.getFax());
            buffer.append( ", " ); 
        }
        if (affiliationVO.getEmail().length() > 0)
        {
            buffer.append("Email: ");
            buffer.append(affiliationVO.getEmail());
            buffer.append( ", " ); 
        }
        if (affiliationVO.getHomepageUrl() != null && affiliationVO.getHomepageUrl().toString().length() > 0 )
        {
            buffer.append(affiliationVO.getHomepageUrl());
        }
        detailList[3] = buffer.toString();
        buffer.setLength(0);
        detailList[4] = buffer.toString();
        affiliationDetailUI = new AffiliationDetailUI( detailList );
        affiliationDetailUI.setTitelComponent(panDetail);
        this.panelAffiliationDetail.getChildren().add(affiliationDetailUI);
        this.panelAffiliationDetail.getChildren().add(htmlElement.getEndTag("div"));
    }

    public HtmlPanelGroup getPanelAffiliationDetail()
    {
        return panelAffiliationDetail;
    }

    public void setPanelAffiliationDetail(HtmlPanelGroup panelAffiliationDetail)
    {
        this.panelAffiliationDetail = panelAffiliationDetail;
    }
}
