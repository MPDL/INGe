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

package de.mpg.escidoc.pubman.viewItem.ui;

import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ui.CollapsiblePanelUI;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.ui.ListUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubItemVOWrapper;

/**
 * ContainerPanelUI for keeping ViewItemUIs.
 * 
 * @author: Thomas Diebäcker, created 30.08.2007
 * @version: $Revision: 1598 $ $LastChangedDate: 2007-11-21 20:24:31 +0100 (Mi, 21 Nov 2007) $
 */
public class ViewItemPanelUI extends CollapsiblePanelUI implements ActionListener, ValueChangeListener
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ViewItemPanelUI.class);
    
    private PubItemVOWrapper pubItemVOWrapper = null;
    
    // item list controls
    private HTMLElementUI htmlElementUI = new HTMLElementUI();
    private HtmlPanelGroup panViewItemPanelControls = new HtmlPanelGroup();
    private HtmlSelectBooleanCheckbox chkSelectItem = new HtmlSelectBooleanCheckbox();
    private HtmlCommandButton btViewItemShort = new HtmlCommandButton();
    private HtmlCommandButton btViewItemMedium = new HtmlCommandButton();

    public ViewItemPanelUI()
    {     
    	
    }
    
    public Object processSaveState(FacesContext context) 
    {
        Object superState = super.processSaveState(context);
        return new Object[] {superState, new Integer(getChildCount())};
    }
    
    public void processRestoreState(FacesContext context, Object state) 
    {
        // At this point in time the tree has already been restored, but not before our ctor added the default children.
        // Since we saved the number of children in processSaveState, we know how many children should remain within
        // this component. We assume that the saved tree will have been restored 'behind' the children we put into it
        // from within the ctor.
        Object[] values = (Object[]) state;
        Integer savedChildCount = (Integer) values[1];
        for (int i = getChildCount() - savedChildCount.intValue(); i > 0; i--) 
        {
            getChildren().remove(0);
        }
        super.processRestoreState(context, values[0]);
    }

    /**
     * Public constructor.
     */
    public ViewItemPanelUI(PubItemVOWrapper pubItemVOWrapper, String actionMethodForTitleLink)
    {
        // call constructor of super class
        super();
        
        this.pubItemVOWrapper = pubItemVOWrapper;
        
        // set link in the title bar
        this.setTitle(actionMethodForTitleLink, this.pubItemVOWrapper.getValueObject().getVersion().getObjectId(), this.pubItemVOWrapper.getValueObject().getMetadata().getTitle().getValue());        

        // initialize list controls        
        this.chkSelectItem.setId(CommonUtils.createUniqueId(this.chkSelectItem));
        this.chkSelectItem.setSelected(pubItemVOWrapper.getSelected());
        this.chkSelectItem.addValueChangeListener(this);
        // add directly to title bar in front of the title
        this.panTitleBar.getChildren().add(0, this.htmlElementUI.getStartTagWithStyleClass("div", "listItemHeader odd")); // add at the right side of the title bar, so use the method of the super class
        this.panTitleBar.getChildren().add(1, this.chkSelectItem);
        this.panTitleBar.getChildren().add(htmlElementUI.getEndTag("div"));

        this.panViewItemPanelControls.setId(CommonUtils.createUniqueId(this.panViewItemPanelControls));
        
        this.btViewItemShort.setId(CommonUtils.createUniqueId(this.btViewItemShort));
        this.btViewItemShort.setImage("images/short_view_item.jpg");
        this.btViewItemShort.setImmediate(true);
        this.btViewItemShort.addActionListener(this);
        this.panViewItemPanelControls.getChildren().add(this.btViewItemShort);
        
        this.btViewItemMedium.setId(CommonUtils.createUniqueId(this.btViewItemMedium));
        this.btViewItemMedium.setImage("images/medium_view_item.jpg");
        this.btViewItemMedium.setImmediate(true);
        this.btViewItemMedium.addActionListener(this);
        this.panViewItemPanelControls.getChildren().add(this.btViewItemMedium);
        
        this.addToControls(panViewItemPanelControls);
        
        // use the view that is given in the wrapper
        this.showItem();
    }

    /**
     * Shows the panel in the view that is stored in the corresponding PubItemVOWrapper.
     * With this method you can create the panel newly but keep the old view that is stored in the wrapper.
     */
    public void showItem()
    {
        // show as expanded or collapsed
        this.isContainerVisible = this.pubItemVOWrapper.isExpanded();
        this.refreshPanelVisibility();
        
        // show the given view
        switch (this.pubItemVOWrapper.getItemView())
        {
            case PubItemVOWrapper.SHOW_AS_SHORT:
                this.showAsViewItemShort();
                break;
            case PubItemVOWrapper.SHOW_AS_MEDIUM:
                this.showAsViewItemMedium();
                break;
        }
    }
    
    /**
     * Show the current item directly in short view and stores this value in the wrapper.
     */
    public void showAsViewItemShort()
    {
        // store the view in the wrapper
        this.pubItemVOWrapper.setItemView(PubItemVOWrapper.SHOW_AS_SHORT);

        // instanciate the new view
        ViewItemShortUI viewItemShortUI = new ViewItemShortUI(this.pubItemVOWrapper);
        this.clearContainer();
        this.addToContainer(viewItemShortUI);
    }

    /**
     * Show the current item directly in medium view and stores this value in the wrapper.
     */
    public void showAsViewItemMedium()
    {
        // store the view in the wrapper
        this.pubItemVOWrapper.setItemView(PubItemVOWrapper.SHOW_AS_MEDIUM);

        // instanciate the new view
        ViewItemMediumUI viewItemMediumUI = new ViewItemMediumUI(this.pubItemVOWrapper);
        this.clearContainer();
        this.addToContainer(viewItemMediumUI);
    }

    /**
     * Action handler for user actions.
     * @param ActionEvent event
     */
    public void processAction(ActionEvent event)
    {
       // call method of super class
       super.processAction(event);

       if (event.getComponent() == this.btViewItemShort)
       {
           this.showAsViewItemShort();
       }
       else if (event.getComponent() == this.btViewItemMedium)
       {           
           this.showAsViewItemMedium();
       }
       else if (event.getComponent() == this.btCollapse)
       {
           // additionally to the processAction method in the superclass, store the value of the visibility (expanded/collapsed) 
           // in the PubItemVOWrapper
           this.pubItemVOWrapper.setExpanded(this.isContainerVisible);
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
                logger.debug("New value of chkSelectItem: " + event.getNewValue() + " [" + event.getNewValue().getClass() + "], wrapper: " + this.pubItemVOWrapper.toString());
            }
            
            this.setSelected(((Boolean)event.getNewValue()).booleanValue());
        }
    }
    
    public void setSelected(boolean selected)
    {
        this.pubItemVOWrapper.setSelected(selected);
        this.chkSelectItem.setSelected(selected);

        // Inserted by FrM, 8.11.07: Set number of selected items for delete confirmation.
        try
        {
            ListUI listUI = (ListUI)this.getParent().getParent();
            logger.debug("Setting noso to " + listUI.getNumberOfSelectedObjects());
            listUI.getNumberSelectedObjects().setValue(listUI.getNumberOfSelectedObjects());
        }
        catch (Exception e)
        {
            logger.warn("Error while getting list ui object", e);
        }
    }
}
