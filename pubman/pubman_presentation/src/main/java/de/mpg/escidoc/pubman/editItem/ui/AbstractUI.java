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

package de.mpg.escidoc.pubman.editItem.ui;

import java.util.List;
import java.util.ResourceBundle;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.component.Button;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;

/**
 * Abstract UI component for inheriting subclasses. 
 * 
 * @author: Thomas Diebäcker, Hugo Niedermaier, created 20.06.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 * Revised by DiT: 08.08.2007
 */
public abstract class AbstractUI extends HtmlPanelGrid implements ActionListener
{
    private static Logger logger = Logger.getLogger(AbstractUI.class);
    
    protected HtmlPanelGrid panDynamicParentPanel = null;
    protected Object parentVO = null;
    protected String parentValueBinding = null;
    protected int indexComponent = 0;
    protected boolean isRequired;

    // for handling the resource bundles (i18n)
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    private ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());

    // GUI components
    protected HtmlPanelGrid panButtons = new HtmlPanelGrid();
    public Button btAdd = new Button();
    public Button btRemove = new Button();    

    /**
     * Public constructor.
     * Initializes the UI.
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     * @param parentVO the parent ValueObject where the values of the UI have to be set.
     * @param parentValueBinding the string for the valueBinding of the UI.
     * @param indexComponent index of the title in the ValueObject.
     */    
    public AbstractUI(HtmlPanelGrid panDynamicParentPanel, Object parentVO, String parentValueBinding, int indexComponent)
    {
        this(panDynamicParentPanel, parentVO, parentValueBinding, indexComponent, false);
    }

    /**
     * Public constructor.
     * Initializes the UI.
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     * @param parentVO the parent ValueObject where the values of the UI have to be set.
     * @param parentValueBinding the string for the valueBinding of the UI.
     * @param indexComponent index of the title in the ValueObject.
     * @param isRequired required fields should be validated as required by JSF. A creator for example should be marked as 
     *        required if it is the creator of the item but not if it's the creator of a source (where the validation package 
     *        will do the validation and not JSF). 
     */    
    public AbstractUI(HtmlPanelGrid panDynamicParentPanel, Object parentVO, String parentValueBinding, int indexComponent, boolean isRequired)
    {
        this.i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        this.labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        
        this.panDynamicParentPanel = panDynamicParentPanel;
        this.parentVO = parentVO;
        this.parentValueBinding = parentValueBinding;
        this.indexComponent = indexComponent;
        this.isRequired = isRequired;
        
        this.setId(this.createUniqueId(this));
        this.setColumns(2);
        this.setColumnClasses("editItemButtonColumn, editItemButtonColumn");
        this.setCellspacing("0");
        
        this.panButtons.setId(this.createUniqueId(this.panButtons));
        this.panButtons.setColumns(2);
        this.panButtons.setColumnClasses("editItemButtonColumn, editItemButtonColumn");
        this.panButtons.setCellspacing("0");

        this.btAdd.setId(this.createUniqueId(this.btAdd));
        this.btAdd.setValue(labelBundle.getString("EditItem_btAdd"));
        this.btAdd.setImmediate(false);
        this.btAdd.addActionListener(this);
        this.btAdd.setStyleClass("editDynamicButton");
        this.panButtons.getChildren().add(this.btAdd);
        
        this.btRemove.setId(this.createUniqueId(this.btRemove));
        this.btRemove.setImmediate(true);
        this.btRemove.addActionListener(this);
        this.btRemove.setValue(labelBundle.getString("EditItem_btRemove"));
        this.btRemove.setVisible(this.isRemoveButtonVisible());            
        this.btRemove.setStyleClass("editDynamicButton");
        this.panButtons.getChildren().add(this.btRemove);
        
        this.getChildren().add(this.panButtons);
    }

    /**
     * Action handler for user actions.
     * @param ActionEvent event
     */
    public void processAction(ActionEvent event)
    {
        if (event.getSource().equals(this.btAdd))
        {
            this.addComponent();
        }
        else if (event.getSource().equals(this.btRemove))
        {
            this.removeComponent();                
        }
    }

    /**
     * Initializes the parent ValueObject with a new sub ValueObject for valueBinding if needed.
     *
     */
    protected abstract void initializeParentVO();
        
    /**
     * Sets the valueBinding for all values of the UI to the corresponding VO. 
     */
    protected abstract void createValueBinding();

    /**
     * Eventmethod that is being called whenever the user chooses to add a component.
     * The method adds a component to the ValueObject and adds new UI to the enclosing panel.
     */
    protected abstract void addComponent();

    /**
     * Eventmethod that is being called whenever the user chooses to remove a component.
     * The method removes a component from the ValueObject and removes the UI from the enclosing panel.
     */
    protected abstract void removeComponent();
        
    /**
     * Stores all values of the UI in the corresponding VO.
     */
    public abstract void storeValues();

    /**
     * Reindex the following components, if a new one has been added in between
     * @param indexNewComponent the index of the newly added component
     */
    protected void reindexFollowingComponents(int indexNewComponent)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Reindexing following components...");
        }

        for (int i=(indexNewComponent + 1); i<this.panDynamicParentPanel.getChildCount(); i++)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Reindexing component " + this.getClass() + " with old index " + ((AbstractUI)this.panDynamicParentPanel.getChildren().get(i)).getIndexComponent() + " to new index " + i);
            }
            
            ((AbstractUI)this.panDynamicParentPanel.getChildren().get(i)).setIndexComponent(i);
        }
    }

    /**
     * Refresh the visibility of all remove buttons as their visibility may change when adding a new component (see PUBMAN-110). 
     */
    protected void refreshVisibilityOfRemoveButtons()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Refresh visibility of remove buttons...");
        }

        for (int i=0; i<this.panDynamicParentPanel.getChildCount(); i++)
        {
            // refresh visibility of all the remove buttons (see PUBMAN-110)
            ((AbstractUI)this.panDynamicParentPanel.getChildren().get(i)).btRemove.setVisible(this.isRemoveButtonVisible());
        }
    }

    /**
     * Stores all values of the current parent panel.
     */
    protected void storeAllValuesOfDynamicParentPanel()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Storing all values of parent panel...");
        }

        for (int i=0; i<this.panDynamicParentPanel.getChildCount(); i++)
        {
            AbstractUI componentUI = (AbstractUI)this.panDynamicParentPanel.getChildren().get(i);
            componentUI.storeValues();
        }
    }

    /**
     * Creates a unique id for GUI components.
     * @return a unique id
     */
    protected String createUniqueId(UIComponent uiComponent)
    {
        return CommonUtils.createUniqueId(uiComponent);
    }
    
    /**
     * Determines if the remove button should be visible. 
     * The remove button should only be invisble if there is only one component (it should be possible to remove the 
     * first component, too if there are others following (see PUBMAN-110)).
     * This method covers most of the UIs as most of them have a list as parentVO. If a UI has not a list as parentVO,
     * (e.g. TitleUI) this method has to be overwritten. 
     * @return true if the remove button should be visible, otherwise false
     */
    protected boolean isRemoveButtonVisible()
    {        
        if (logger.isDebugEnabled())
        {
            if (!(this.parentVO instanceof List))
            {
                logger.debug("!!!!!!!!!!!!!!!!!!!!!!ParentVO is instance of " + parentVO.getClass() + " and has to be overwritten!");
            }
        }
        
        return (this.indexComponent != 0 || (this.parentVO instanceof List && ((List)this.parentVO).size() > 1));          
    }

    public String getParentValueBinding()
    {
        return parentValueBinding;
    }

    public void setParentValueBinding(String parentValueBinding)
    {
        this.parentValueBinding = parentValueBinding;
        
        // ValueBinding has to be set with the parentValueBinding
        this.createValueBinding();
    }
    
    public int getIndexComponent()
    {
        return indexComponent;
    }

    public void setIndexComponent(int indexComponent)
    {
        this.indexComponent = indexComponent;

        // ValueBinding has to be set with the new index
        this.createValueBinding();
    }
}
