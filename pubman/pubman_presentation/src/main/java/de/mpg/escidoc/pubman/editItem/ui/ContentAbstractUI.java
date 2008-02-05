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
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html. HtmlOutputLabel;
import javax.faces.component.html.HtmlInputTextarea;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * UI component for editing abstracts. 
 * 
 * @author: Thomas Diebäcker, created 26.06.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 * Revised by DiT: 09.08.2007
 */
public class ContentAbstractUI extends AbstractUI
{
    private static Logger logger = Logger.getLogger(ContentAbstractUI.class);

    // for handling the resource bundles (i18n)
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    private ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
    
    // GUI components
    private HtmlPanelGrid panAttributes = new HtmlPanelGrid();
    private HtmlOutputLabel lblAbstract = new HtmlOutputLabel();
    private HtmlInputTextarea txtAbstract = new HtmlInputTextarea();
    private HtmlSelectOneMenu cboLanguage = new HtmlSelectOneMenu();

    /**
     * Public constructor.
     * Initializes the UI.
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     * @param parentVO the parent ValueObject where the values of the UI have to be set.
     * @param parentValueBinding the string for the valueBinding of the UI.
     * @param indexComponent index of the abstract in the ValueObject.
     * 
     */    
    public ContentAbstractUI(HtmlPanelGrid panDynamicParentPanel, MdsPublicationVO parentVO, String parentValueBinding, int indexComponent)
    {
        super(panDynamicParentPanel, parentVO, parentValueBinding, indexComponent);        
        
        this.i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        this.labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
        
        // set attributes for all GUI components
        this.panAttributes.setId(this.createUniqueId(this.panAttributes));
        this.panAttributes.setColumns(3);
        this.panAttributes.setCellspacing("0");
        this.panAttributes.setCellpadding("0");
        this.panAttributes.setColumnClasses("editItemLabelColumn, editItemFieldColumn, editItemFieldColumn");
        
        this.lblAbstract.setId(this.createUniqueId(this.lblAbstract));
        this.lblAbstract.setFor(this.txtAbstract.getId());
        this.lblAbstract.setValue(labelBundle.getString("EditItem_lblAbstract"));
        //this.lblAbstract.setLabelLevel(3);
        this.cboLanguage.getChildren().clear();
        this.panAttributes.getChildren().add(this.lblAbstract);

        this.txtAbstract.setId(this.createUniqueId(this.txtAbstract));
        this.txtAbstract.setStyleClass("editItemTextArea");
        this.panAttributes.getChildren().add(this.txtAbstract);
        
        this.cboLanguage.setId(this.createUniqueId(this.cboLanguage));
        this.cboLanguage.getChildren().addAll(CommonUtils.convertToSelectItemsUI(CommonUtils.getLanguageOptions()));
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
        // initialize abstract if none is given, so that valueBinding is possible
        if (this.getParentVO().getAbstracts().size() == 0)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Initializing parentVO with new values for valueBinding...");
            }

            this.getParentVO().getAbstracts().add(new TextVO());
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

        // valueBinding for an abstract
        this.txtAbstract.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + ".abstracts[" + this.indexComponent + "].value}"));
        this.cboLanguage.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + ".abstracts[" + this.indexComponent + "].language}"));
    }

    /**
     * Creates the panel newly according to the values in the ValueObject.
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     * @param parentVO the parent ValueObject where the values of the UI have to be set.
     * @param parentValueBinding the string for the valueBinding of the UI.
     */
    public static void createDynamicParentPanel(HtmlPanelGrid panDynamicParentPanel, MdsPublicationVO parentVO, String parentValueBinding)
    {        
        if (logger.isDebugEnabled())
        {
            logger.debug("Create dynamic parent panel...");
        }

        // remove all components
        panDynamicParentPanel.getChildren().clear();

        // add all abstracts 
        for (int i = 0; i < parentVO.getAbstracts().size(); i++)
        {
            panDynamicParentPanel.getChildren().add(new ContentAbstractUI(panDynamicParentPanel, parentVO, parentValueBinding, i));
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
        this.getParentVO().getAbstracts().add(indexNewComponent, new TextVO());
        
        // add new component to enclosing panel
        ContentAbstractUI newComponentUI = new ContentAbstractUI(this.panDynamicParentPanel, this.getParentVO(), this.parentValueBinding, indexNewComponent);
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
        this.getParentVO().getAbstracts().remove(this.indexComponent);
        
        // recreate the panel
        ContentAbstractUI.createDynamicParentPanel(this.panDynamicParentPanel, this.getParentVO(), this.parentValueBinding);
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

        this.getParentVO().getAbstracts().get(this.indexComponent).setValue(CommonUtils.getUIValue(this.txtAbstract));
        this.getParentVO().getAbstracts().get(this.indexComponent).setLanguage(CommonUtils.getUIValue(this.cboLanguage));
    }
    
    /**
     * Determines if the remove button should be visible. 
     * The remove button should only be invisble if there is only one component (it should be possible to remove the 
     * first component, too if there are others following (see PUBMAN-110)).
     * As ContentAbstractUI does not have a list as parentVO the method in the super class (which covers all UIs which 
     * have a list as parentVO) has to be overwritten.
     * @return true if the remove button should be visible, otherwise false
     */
    @Override
    protected boolean isRemoveButtonVisible()
    {       
        return (this.indexComponent != 0 || this.getParentVO().getAbstracts().size() > 1);        
    }

    /**
     * Returns the parent ValueObject.
     * @return the parent ValueObject
     */
    private MdsPublicationVO getParentVO()
    {
        return (MdsPublicationVO)this.parentVO;
    }
}
