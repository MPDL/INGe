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

package de.mpg.escidoc.pubman.search.ui;

import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import de.mpg.escidoc.pubman.ui.CollapsiblePanelUI;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * class to hold the detail information of an affiliation.
 * @author Hugo Niedermaier, Thomas Endres
 */
public class AffiliationDetailUI extends CollapsiblePanelUI
{
    private HtmlOutputText txtDescription = new HtmlOutputText();
    private HtmlOutputText txtAddress = new HtmlOutputText();
    private HtmlOutputText txtCity = new HtmlOutputText();
    private HtmlOutputText txtComElements = new HtmlOutputText();
    private HtmlOutputText txtRegion = new HtmlOutputText();
    private HtmlPanelGroup panelGroup = new HtmlPanelGroup();
    private HTMLElementUI htmlElement = new HTMLElementUI();
       
    /**
     * constructor.
     */
    public AffiliationDetailUI( String[] detailList )
    {
        super();
        
        // this direct access has to be done to get the collapsible buttons inside the title box
        this.panTitleBar.getChildren().add(0, this.htmlElementUI.getStartTagWithStyleClass("div", "listHeader dark"));
        this.panTitleBar.getChildren().add(htmlElementUI.getEndTag("div"));  
        
        this.panelGroup.setId( CommonUtils.createUniqueId( this.panelGroup) );
        this.panelGroup.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "formField"));
        this.txtDescription.setId(CommonUtils.createUniqueId(this.txtDescription));
        this.txtDescription.setValue(detailList[0]);
        this.panelGroup.getChildren().add(this.txtDescription);        
        
        this.panelGroup.getChildren().add(htmlElement.getStartTag("br/"));
        this.panelGroup.getChildren().add(htmlElement.getStartTag("br/"));
        this.txtAddress.setId(CommonUtils.createUniqueId(this.txtAddress));
        this.txtAddress.setValue(detailList[1]);
        this.panelGroup.getChildren().add(this.txtAddress);        
        
        this.panelGroup.getChildren().add(htmlElement.getStartTag("br/"));
        this.txtCity.setId(CommonUtils.createUniqueId(this.txtCity));
        this.txtCity.setValue(detailList[2]);
        this.panelGroup.getChildren().add(this.txtCity);        
        
        this.panelGroup.getChildren().add(htmlElement.getStartTag("br/"));
        this.panelGroup.getChildren().add(htmlElement.getStartTag("br/"));
        this.txtComElements.setId(CommonUtils.createUniqueId(this.txtComElements));
        this.txtComElements.setValue(detailList[3]);
        this.panelGroup.getChildren().add(this.txtComElements);        
        
        this.panelGroup.getChildren().add(htmlElement.getStartTag("br/"));
        this.panelGroup.getChildren().add(htmlElement.getStartTag("br/"));
        this.txtRegion.setId(CommonUtils.createUniqueId(this.txtRegion));
        this.txtRegion.setValue(detailList[4]);
        this.panelGroup.getChildren().add(this.txtRegion);        
        
        this.panelGroup.getChildren().add(htmlElement.getEndTag("div"));
        
        this.addToContainer(this.panelGroup);
    }
}
