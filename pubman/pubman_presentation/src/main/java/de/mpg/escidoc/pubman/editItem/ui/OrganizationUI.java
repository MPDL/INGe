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
import javax.faces.application.Application;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.component.Button;
import com.sun.rave.web.ui.component.Label;
import com.sun.rave.web.ui.component.TextArea;
import com.sun.rave.web.ui.component.TextField;
import de.mpg.escidoc.pubman.affiliation.AffiliationSessionBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * UI component for editing organizations. 
 * 
 * @author: Thomas Diebäcker, created 27.06.2007
 * @version: $Revision: 1632 $ $LastChangedDate: 2007-11-29 15:01:44 +0100 (Thu, 29 Nov 2007) $
 * Revised by DiT: 14.08.2007
 */
public class OrganizationUI extends AbstractUI
{
    private static Logger logger = Logger.getLogger(OrganizationUI.class);

    // for handling the resource bundles (i18n)
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    private ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    
    // GUI components
    private HtmlPanelGrid panAttributes = new HtmlPanelGrid();
    private Label lblOrganization = new Label();
    private Label lblOrganizationName = new Label();
    private Label lblOrganizationAddress = new Label();
    private TextField txtOrganizationName = new TextField();
    private TextArea txtaOrganizationAddress = new TextArea();
    private Button btAdd = new Button();
    private Button btSelect = new Button();

    /**
     * Public constructor.
     * Initializes the UI.
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     * @param parentVO the parent ValueObject where the values of the UI have to be set.
     * @param parentValueBinding the string for the valueBinding of the UI.
     * @param indexComponent index of the identifier in the ValueObject.
     * 
     */    
    public OrganizationUI(HtmlPanelGrid panDynamicParentPanel, List<OrganizationVO> parentVO, String parentValueBinding, int indexComponent)
    {
        super(panDynamicParentPanel, parentVO, parentValueBinding, indexComponent);
                
        if (logger.isDebugEnabled())
        {
            logger.debug("Constructing UI with index: " + indexComponent + ", parentVO: " + parentVO.getClass() + ", valueBinding: " + parentValueBinding);
        }
        
        this.i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        this.labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        
        //NiH: read only flag for organization name and address
        boolean readOnly = false;
        OrganizationVO organizationVO = parentVO.get(indexComponent);
        if (organizationVO.getName() == null && indexComponent == 0)
        {
            readOnly = true;
        }
        
        // set attributes for all GUI components
        this.lblOrganization.setId(this.createUniqueId(this.lblOrganization));
        this.lblOrganization.setValue(labelBundle.getString("EditItem_lblOrganization"));
        this.lblOrganization.setLabelLevel(3);
        this.getChildren().add(0, this.lblOrganization); // place label in front of components from superclass, therefore position "0" is given here

        this.panAttributes.setId(this.createUniqueId(this.panAttributes));
        this.panAttributes.setColumns(2);
        this.panAttributes.setColumnClasses("editItemLabelColumn, editItemFieldColumn");
        this.panAttributes.setCellspacing("0");
        this.panAttributes.setCellpadding("0");
        
        this.lblOrganizationName.setId(this.createUniqueId(this.lblOrganizationName));
        this.lblOrganizationName.setLabeledComponent(this.txtOrganizationName);
        this.lblOrganizationName.setValue(labelBundle.getString("EditItem_lblOrganizationName"));
        this.lblOrganizationName.setLabelLevel(3);
        this.panAttributes.getChildren().add(this.lblOrganizationName);
        
        this.txtOrganizationName.setId(this.createUniqueId(this.txtOrganizationName));
        this.txtOrganizationName.setStyleClass("editItemTextFieldMedium");
        //NiH: disable organization name
        if (readOnly)
        {
            this.txtOrganizationName.setDisabled(true);
        }
        else
        {
            this.txtOrganizationName.setDisabled(false);
        }
        this.panAttributes.getChildren().add(this.txtOrganizationName);
        
        this.lblOrganizationAddress.setId(this.createUniqueId(this.lblOrganizationAddress));
        this.lblOrganizationAddress.setLabeledComponent(this.txtaOrganizationAddress);
        this.lblOrganizationAddress.setValue(labelBundle.getString("EditItem_lblOrganizationAddress"));
        this.lblOrganizationAddress.setLabelLevel(3);
        this.panAttributes.getChildren().add(this.lblOrganizationAddress);
        
        this.txtaOrganizationAddress.setId(this.createUniqueId(this.txtaOrganizationAddress));
        this.txtaOrganizationAddress.setStyleClass("editItemTextArea");
        //NiH: disable organization address
        if (readOnly)
        {
            this.txtaOrganizationAddress.setDisabled(true);
        }
        else
        {
            this.txtaOrganizationAddress.setDisabled(false);
        }
        this.panAttributes.getChildren().add(this.txtaOrganizationAddress);

        this.getChildren().add(this.panAttributes);

        Application application = FacesContext.getCurrentInstance().getApplication();

        super.btAdd.setVisible(false);
        this.btAdd.setId(this.createUniqueId(this.btAdd));
        this.btAdd.setValue(labelBundle.getString("EditItem_btAdd"));
        this.btAdd.setImmediate(false);
        this.btAdd.addActionListener(this);        
        this.btAdd.setAction(application.createMethodBinding("#{editItem$EditItem.loadAffiliationTree}", null));

        this.btSelect.setId(this.createUniqueId(this.btSelect));
        this.btSelect.setImmediate(false);
        this.btSelect.addActionListener(this);
        this.btSelect.setValue(labelBundle.getString("EditItem_btSelect"));
        this.btSelect.setStyleClass("editDynamicButton");
        this.btSelect.setAction(application.createMethodBinding("#{editItem$EditItem.loadAffiliationTree}", null));
        this.panButtons.getChildren().add(this.btSelect);
                
        //NiH: disable button Add
        if (readOnly)
        {
            this.btAdd.setDisabled(true);
        }
        else
        {
            this.btAdd.setDisabled(false);
        }
        this.btAdd.setStyleClass("editDynamicButton");
        this.panButtons.getChildren().add(this.btAdd);
        
        // initialize the parentVO for valueBinding if needed
        this.initializeParentVO();
        
        // set the valueBinding for all fields
        this.createValueBinding();
        
        // initializes the AffiliationTree with the parentVO of the UI
        this.initializeAffiliationTree();

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
        else if (event.getSource().equals(this.btSelect))
        {
            this.selectComponent();                
        }
        else if (event.getSource().equals(this.btRemove))
        {
            this.removeComponent();                
        }
    }
    
    /**
     * Initializes the parent ValueObject with a new sub ValueObject for valueBinding if needed.
     */
    protected void initializeParentVO()
    {        
        // initialize identifier if none is given, so that valueBinding is possible
        if (this.getParentVO().size() == 0)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Initializing parentVO with new values for valueBinding...");
            }

            OrganizationVO newOrganizationVO = new OrganizationVO();
            newOrganizationVO.setName(new TextVO());
            this.getParentVO().add(newOrganizationVO);
        }
    }
    
    /**
     * Initializes the AffiliationTree with the current parentVO.
     * @author Hugo Niedermaier
     */
    protected void initializeAffiliationTree()
    {
        AffiliationSessionBean affiliationSessionBean = (AffiliationSessionBean)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), AffiliationSessionBean.BEAN_NAME);
        
        //set the parent panel for organization input fields on the mask
        affiliationSessionBean.setOrganizationPanDynamicParentPanel(this.panDynamicParentPanel);
        
        //set the current parent VO and value binding and component index
        affiliationSessionBean.setOrganizationParentVO(this.getParentVO());
        affiliationSessionBean.setOrganizationParentValueBinding(this.parentValueBinding);
        affiliationSessionBean.setIndexComponent(this.indexComponent);
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

        // valueBinding for an organization
        this.txtOrganizationName.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + "[" + this.indexComponent + "].name.value}"));
        this.txtaOrganizationAddress.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + "[" + this.indexComponent + "].address}"));
    }

    /**
     * Creates the panel newly according to the values in the ValueObject.
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     * @param parentVO the parent ValueObject where the values of the UI have to be set.
     * @param parentValueBinding the string for the valueBinding of the UI.
     */
    public static void createDynamicParentPanel(HtmlPanelGrid panDynamicParentPanel, List<OrganizationVO> parentVO, String parentValueBinding)
    {        
        if (logger.isDebugEnabled())
        {
            logger.debug("Create dynamic parent panel...");
        }

        // remove all components
        panDynamicParentPanel.getChildren().clear();

        // add all organization   
        for (int i = 0; i < parentVO.size(); i++)
        {
            panDynamicParentPanel.getChildren().add(new OrganizationUI(panDynamicParentPanel, parentVO, parentValueBinding, i));
        }
    }

    /**
     * Eventmethod that is being called whenever the user chooses the select button to select a organization out of the affiliation tree.
     * The method removes a component from the ValueObject and removes the UI from the enclosing panel.
     */
    protected void selectComponent()
    {        
        AffiliationSessionBean affiliationSessionBean = (AffiliationSessionBean)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), AffiliationSessionBean.BEAN_NAME);
        affiliationSessionBean.setAdd(false);
        storeAllValuesOfDynamicParentPanel();
        initializeAffiliationTree();
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
        OrganizationVO newOrganizationVO = new OrganizationVO();
        newOrganizationVO.setName(new TextVO());
        this.getParentVO().add(indexNewComponent, newOrganizationVO);
        
        storeAllValuesOfDynamicParentPanel();
        
        AffiliationSessionBean affiliationSessionBean = (AffiliationSessionBean)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), AffiliationSessionBean.BEAN_NAME);
        affiliationSessionBean.setAdd(true);
        
        // add new component to enclosing panel
        OrganizationUI newComponentUI = new OrganizationUI(this.panDynamicParentPanel, this.getParentVO(), this.parentValueBinding, indexNewComponent);
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
        this.getParentVO().remove(this.indexComponent);
        
        // recreate the panel
        OrganizationUI.createDynamicParentPanel(this.panDynamicParentPanel, this.getParentVO(), this.parentValueBinding);
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
        // TODO FrM: Replace null value for language when language can be selected in the edit item page
        this.getParentVO().get(this.indexComponent).setName(new TextVO(CommonUtils.getUIValue(this.txtOrganizationName), ""));
        this.getParentVO().get(this.indexComponent).setAddress(CommonUtils.getUIValue(this.txtaOrganizationAddress));
    }
    
    /**
     * Returns the parent ValueObject.
     * @return the parent ValueObject
     */
    private List<OrganizationVO> getParentVO()
    {
        return (List<OrganizationVO>)this.parentVO;
    }
}

