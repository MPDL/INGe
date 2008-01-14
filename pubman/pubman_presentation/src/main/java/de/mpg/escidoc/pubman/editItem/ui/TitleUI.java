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

import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.component.DropDown;
import com.sun.rave.web.ui.component.Label;
import com.sun.rave.web.ui.component.TextArea;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.TitleIF;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * UI component for editing titles. 
 * 
 * @author: Thomas Diebäcker, created 20.06.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 * Revised by DiT: 14.08.2007
 */
public class TitleUI extends AbstractUI
{
    private static Logger logger = Logger.getLogger(TitleUI.class);

    // for handling the resource bundles (i18n)
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    private ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    
    // GUI components
    private HtmlPanelGrid panAttributes = new HtmlPanelGrid();
    private Label lblTitel = new Label();
    private TextArea txtaTitel = new TextArea();
    private DropDown cboLanguage = new DropDown();

    /**
     * Public constructor.
     * Initializes the UI.
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     * @param parentVO the parent ValueObject where the values of the UI have to be set (has to implement TitleIF).
     * @param parentValueBinding the string for the valueBinding of the UI.
     * @param indexComponent index of the title in the ValueObject.
     * 
     */    
    public TitleUI(HtmlPanelGrid panDynamicParentPanel, TitleIF parentVO, String parentValueBinding, int indexComponent)
    {
        super(panDynamicParentPanel, parentVO, parentValueBinding, indexComponent);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Constructing UI with index: " + indexComponent + ", parentVO: " + parentVO.getClass() + ", valueBinding: " + parentValueBinding);
        }
        
        i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        
        // set attributes for all GUI components
        this.panAttributes.setId(this.createUniqueId(this.panAttributes));
        this.panAttributes.setColumns(3);
        this.panAttributes.setCellspacing("0");
        this.panAttributes.setCellpadding("0");
        this.panAttributes.setColumnClasses("editItemLabelColumn, editItemFieldColumn, editItemFieldColumn");
        
        this.lblTitel.setId(this.createUniqueId(this.lblTitel));
        this.lblTitel.setValue((this.indexComponent == 0) ? labelBundle.getString("EditItem_lblTitel") : labelBundle.getString("EditItem_lblAlternativeTitel"));
        this.lblTitel.setLabelLevel(3);
        this.lblTitel.setRequiredIndicator(parentVO instanceof MdsPublicationVO); // only normal titles should be reqired, no titles in Source
        this.panAttributes.getChildren().add(this.lblTitel);

        this.txtaTitel.setId(this.createUniqueId(this.txtaTitel));        
        this.txtaTitel.setStyleClass("editItemTextArea");
        this.panAttributes.getChildren().add(this.txtaTitel);
        
        this.cboLanguage.setId(this.createUniqueId(this.cboLanguage));
        this.cboLanguage.setItems(CommonUtils.getLanguageOptions());
        this.cboLanguage.setStyleClass("editItemComboBoxLanguage");
        this.panAttributes.getChildren().add(this.cboLanguage);
        
        this.getChildren().add(0, this.panAttributes); // place attributes in front of components from superclass, therefore position "0" is given here
        
        // initialize the parentVO for valueBinding if needed
        this.initializeParentVO();
        
        // set the valueBinding for all fields
        this.createValueBinding();
    }

    /**
     * Initializes the parent ValueObject with a new sub ValueObject for valueBinding if needed.
     */
    protected void initializeParentVO()
    {        
        // initialize title if none is given, so that valueBinding is possible
        if (this.getParentVO().getTitle() == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Initializing parentVO with new values for valueBinding...");
            }

            TextVO newTitle = new TextVO();
            this.getParentVO().setTitle(newTitle);
        }
    }
        
    /**
     * Sets the valueBinding for all values of the UI to the corresponding VO. 
     */
    protected void createValueBinding()
    {
        Application application = FacesContext.getCurrentInstance().getApplication();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Create valueBinding...");
        }

        if (this.indexComponent == 0)
        {
            // valueBinding for a title
            this.txtaTitel.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + ".title.value}"));
            this.cboLanguage.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + ".title.language}"));
        }
        else
        {
            // valueBinding for an alternative title
            this.txtaTitel.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + ".alternativeTitles[" + (this.indexComponent - 1) + "].value}"));
            this.cboLanguage.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + ".alternativeTitles[" + (this.indexComponent - 1) + "].language}"));
        }
    }

    /**
     * Creates the panel newly according to the values in the ValueObject.
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     * @param parentVO the parent ValueObject where the values of the UI have to be set (has to implement TitleIF).
     * @param parentValueBinding the string for the valueBinding of the UI.
     */
    public static void createDynamicParentPanel(HtmlPanelGrid panDynamicParentPanel, TitleIF parentVO, String parentValueBinding)
    {        
        if (logger.isDebugEnabled())
        {
            logger.debug("Create dynamic parent panel...");
        }

        // remove all components
        panDynamicParentPanel.getChildren().clear();

        // add the title
        panDynamicParentPanel.getChildren().add(new TitleUI(panDynamicParentPanel, parentVO, parentValueBinding, 0));
        
        // add all alternative titles   
        for (int i = 0; i < parentVO.getAlternativeTitles().size(); i++)
        {
            panDynamicParentPanel.getChildren().add(new TitleUI(panDynamicParentPanel, parentVO, parentValueBinding, (i + 1)));
        }
    }

    /**
     * Eventmethod that is being called whenever the user chooses to add a component.
     * The method adds a component to the ValueObject and adds new UI to the enclosing panel.
     */
    protected void addComponent()
    {        
        if (logger.isDebugEnabled())
        {
            logger.debug("Adding a component...");
        }

        int indexNewComponent = this.indexComponent + 1;
        
        // add new component to VO
        this.getParentVO().getAlternativeTitles().add((indexNewComponent - 1), new TextVO());
        
        // add new component to enclosing panel
        TitleUI newComponentUI = new TitleUI(this.panDynamicParentPanel, this.getParentVO(), this.parentValueBinding, indexNewComponent);
        this.panDynamicParentPanel.getChildren().add(indexNewComponent, newComponentUI);
        
        this.reindexFollowingComponents(indexNewComponent);
        this.refreshVisibilityOfRemoveButtons();
    }

    /**
     * Eventmethod that is being called whenever the user chooses to remove a component.
     * The method removes a component from the ValueObject and removes the UI from the enclosing panel.
     */
    protected void removeComponent()
    {        
        if (logger.isDebugEnabled())
        {
            logger.debug("Removing a component...");
        }

        // store all values to VO
        this.storeAllValuesOfDynamicParentPanel();
         
        // remove component from VO
        if (this.indexComponent == 0)
        {
            // If the (first) title should be removed, get the first alternative title as new title.
            // There will always exist an alternative title otherwise there would be no remove button and this method 
            // would never be called.
            this.getParentVO().setTitle(this.getParentVO().getAlternativeTitles().get(0));
            this.getParentVO().getAlternativeTitles().remove(0);            
        }
        else
        {            
            // remove an alternative title
            this.getParentVO().getAlternativeTitles().remove(this.indexComponent - 1);
        }
        
        // recreate the panel
        TitleUI.createDynamicParentPanel(this.panDynamicParentPanel, this.getParentVO(), this.parentValueBinding);
    }
    
    /**
     * Stores all values of the UI in the corresponding VO.
     */
    public void storeValues()
    {        
        if (logger.isDebugEnabled())
        {
            logger.debug("Storing values of component with index: " + this.indexComponent);
        }

        if (this.indexComponent == 0)
        {
            this.getParentVO().getTitle().setValue(CommonUtils.getUIValue(this.txtaTitel));
            this.getParentVO().getTitle().setLanguage(CommonUtils.getUIValue(this.cboLanguage));
        }
        else
        {
            this.getParentVO().getAlternativeTitles().get(this.indexComponent - 1).setValue(CommonUtils.getUIValue(this.txtaTitel));
            this.getParentVO().getAlternativeTitles().get(this.indexComponent - 1).setLanguage(CommonUtils.getUIValue(this.cboLanguage));
        }
    }
    
    /**
     * Determines if the remove button should be visible. 
     * The remove button should only be invisble if there is only one component (it should be possible to remove the 
     * first component, too if there are others following (see PUBMAN-110)).
     * As TitleUI does not have a list as parentVO the method in the super class (which covers all UIs which have a 
     * list as parentVO) has to be overwritten.
     * @return true if the remove button should be visible, otherwise false
     */
    @Override
    protected boolean isRemoveButtonVisible()
    {       
        return (this.indexComponent != 0 || this.getParentVO().getAlternativeTitles().size() > 0);        
    }

    /**
     * Returns the parent ValueObject, casted to TitleIF.
     * @return the parent ValueObject as TitleIF
     */
    private TitleIF getParentVO()
    {
        return (TitleIF)this.parentVO;
    }
}
