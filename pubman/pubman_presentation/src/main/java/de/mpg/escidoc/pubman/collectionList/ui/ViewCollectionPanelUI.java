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

package de.mpg.escidoc.pubman.collectionList.ui;

import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import org.apache.log4j.Logger;
import de.mpg.escidoc.pubman.collectionList.PubCollectionVOWrapper;
import de.mpg.escidoc.pubman.ui.CollapsiblePanelUI;
import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * ContainerPanelUI for keeping ViewCollectionUIs.
 * 
 * @author: Thomas Diebäcker, created 12.10.2007
 * @version: $Revision: 1632 $ $LastChangedDate: 2007-11-29 15:01:44 +0100 (Thu, 29 Nov 2007) $
 */
public class ViewCollectionPanelUI extends CollapsiblePanelUI implements ActionListener, ValueChangeListener
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ViewCollectionPanelUI.class);
    
    private PubCollectionVOWrapper pubCollectionVOWrapper = null;
    private CollectionListUI collectionListUI = null;
    
    // item list controls
    private HtmlPanelGroup panViewItemPanelControls = new HtmlPanelGroup();
    // TODO FrM: we use a checkBox instead of a radiobutton here as radiobuttons create a table which messes up the layout;
    //           maybe we can use another renderer to make the checkbox look like a radiobutton?
    private HtmlSelectBooleanCheckbox chkSelectItem = new HtmlSelectBooleanCheckbox();

    /**
     * Public constructor.
     * @param pubCollectionVOWrapper the wrapper with the ValueObject that should be displayed
     * @param collectionListUI the parent ListUI, to be able to call the selectOneItem-method to deselect all other
     *        objects when one gets selected (emulate the radioButtonGroup)
     */
    public ViewCollectionPanelUI(PubCollectionVOWrapper pubCollectionVOWrapper, CollectionListUI collectionListUI)
    {
        // call constructor of super class
        super();
        
        this.pubCollectionVOWrapper = pubCollectionVOWrapper;
        this.collectionListUI = collectionListUI;
        
        // set collection name in the title bar
        this.setTitle(pubCollectionVOWrapper.getValueObject().getName());        

        // initialize list controls
        this.chkSelectItem.setId(CommonUtils.createUniqueId(this.chkSelectItem));
        this.chkSelectItem.setValue(pubCollectionVOWrapper.getSelected());
        this.chkSelectItem.addValueChangeListener(this);
        this.chkSelectItem.setOnchange("submit();");
        // add directly to title bar in front of the title
        this.panTitleBar.getChildren().add(0, this.htmlElementUI.getStartTagWithStyleClass("div", "listItemHeader odd")); // add at the right side of the title bar, so use the method of the super class
        this.panTitleBar.getChildren().add(1, this.chkSelectItem);
        this.panTitleBar.getChildren().add(htmlElementUI.getEndTag("div"));

        this.panViewItemPanelControls.setId(CommonUtils.createUniqueId(this.panViewItemPanelControls));
        
        this.addToTitleBar(panViewItemPanelControls);

        this.showCollection();
    }

    /**
     * Shows the panel.
     */
    public void showCollection()
    {
        // show as expanded or collapsed
        this.isContainerVisible = this.pubCollectionVOWrapper.isExpanded();
        this.refreshPanelVisibility();
        
        // instanciate the new view
        ViewCollectionUI viewCollectionUI = new ViewCollectionUI(this.pubCollectionVOWrapper);
        this.clearContainer();
        this.addToContainer(viewCollectionUI);
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
           // in the PubCollectionVOWrapper
           this.pubCollectionVOWrapper.setExpanded(this.isContainerVisible);
       }
    }
    
    /**
     * ValueChange handler for checkBoxes.
     * @param ValueChangeEvent event
     */
    public void processValueChange(ValueChangeEvent event)
    {
        if (event.getComponent() == this.chkSelectItem)            
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("New value of chkSelectItem: " + event.getNewValue() + ", wrapper: " + this.pubCollectionVOWrapper.toString());
            }
            
            this.setSelected(((Boolean)event.getNewValue()).booleanValue());
        }
    }
    
    public void setSelected(boolean selected)
    {
        // deselect all other objects as we cannot use the buttongroup for making sure that only one object is selected at a time
        this.collectionListUI.selectOneItem(pubCollectionVOWrapper, selected);
        
        this.pubCollectionVOWrapper.setSelected(selected);        
        this.chkSelectItem.setValue(selected);
    }
}

