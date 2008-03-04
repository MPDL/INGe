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

package de.mpg.escidoc.pubman.revisions.ui;

import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.revisions.RelationVOWrapper;
import de.mpg.escidoc.pubman.ui.CollapsiblePanelUI;
import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * ContainerPanelUI for keeping ViewRevisionUIs.
 * 
 * @author: Thomas Diebäcker, created 22.10.2007
 * @version: $Revision: 1683 $ $LastChangedDate: 2007-12-17 10:30:45 +0100 (Mo, 17 Dez 2007) $
 */
public class ViewRevisionPanelUI extends CollapsiblePanelUI implements ActionListener
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ViewRevisionPanelUI.class);

    private RelationVOWrapper relationVOWrapper = null;
    
    // UI-Components
    HtmlOutputLabel lblReleaseDate = new HtmlOutputLabel();
    HtmlOutputText releaseDate = new HtmlOutputText();
    
    /**
     * Public constructor.
     * @param relationVOWrapper the wrapper with the ValueObject that should be displayed
     */
    public ViewRevisionPanelUI(RelationVOWrapper relationVOWrapper)
    {
        // call constructor of super class
        super();
        
        this.relationVOWrapper = relationVOWrapper;
        
        // set revision name in the title bar
        this.setTitle(relationVOWrapper.getSourceItem().getMetadata().getTitle().getValue());        

        // initialize list controls
        // we don't have any list controls here but we need the divs        
        this.panTitleBar.getChildren().add(0, this.htmlElementUI.getStartTagWithStyleClass("div", "listItemHeader odd")); // add at the right side of the title bar, so use the method of the super class        
        // add the release date also to the titlePanel
        this.lblReleaseDate.setId(CommonUtils.createUniqueId(this.lblReleaseDate));
        this.lblReleaseDate.setValue(getLabel("CreateNewRevision_ChooseCollection_lblReleased"));
        this.releaseDate.setId(CommonUtils.createUniqueId(this.releaseDate));
        this.releaseDate.setValue(": " + CommonUtils.format(relationVOWrapper.getSourceItem().getModificationDate()));
        
        this.panTitleBar.getChildren().add(htmlElementUI.getStartTag("div"));
        this.panTitleBar.getChildren().add(this.lblReleaseDate);
        this.panTitleBar.getChildren().add(this.releaseDate);
        this.panTitleBar.getChildren().add(htmlElementUI.getEndTag("div"));
        // closing div from above listItemHeader_odd
        this.panTitleBar.getChildren().add(htmlElementUI.getEndTag("div"));

        this.showRevision();
    }

    /**
     * Shows the panel.
     */
    public void showRevision()
    {
        // show as expanded or collapsed
        this.isContainerVisible = this.relationVOWrapper.isExpanded();
        this.refreshPanelVisibility();
        
        // instanciate the new view
        ViewRevisionUI viewRevisionUI = new ViewRevisionUI(this.relationVOWrapper);
        this.clearContainer();
        this.addToContainer(viewRevisionUI);
    }
    
    /**
     * Action handler for user actions.
     * @param ActionEvent event
     */
    public void processAction(ActionEvent event)
    {
       // call method of super class
       super.processAction(event);
       
       if (event.getComponent() == this.btCollapse)
       {
           // additionally to the processAction method in the superclass, store the value of the visibility (expanded/collapsed) 
           // in the VOWrapper
           this.relationVOWrapper.setExpanded(this.isContainerVisible);
       }
    }
}

