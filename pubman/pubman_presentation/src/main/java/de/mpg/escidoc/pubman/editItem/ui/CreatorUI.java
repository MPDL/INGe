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
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html. HtmlOutputLabel;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.component.html.HtmlInputText;
import javax.faces.model.SelectItem;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * UI component for editing creators. 
 * 
 * @author: Thomas Diebäcker, created 26.06.2007
 * @version: $Revision: 1632 $ $LastChangedDate: 2007-11-29 15:01:44 +0100 (Do, 29 Nov 2007) $
 * Revised by DiT: 09.08.2007
 */
public class CreatorUI extends AbstractUI
{
    private static Logger logger = Logger.getLogger(CreatorUI.class);

    // for handling the resource bundles (i18n)
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    private ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
    
    // GUI components
    private HtmlPanelGrid panAttributes = new HtmlPanelGrid();
    private HtmlPanelGrid panPersonOrganizations = new HtmlPanelGrid();

    // common
    private HtmlOutputLabel lblCreator = new HtmlOutputLabel();
    private HtmlOutputLabel lblCreatorRole = new HtmlOutputLabel();
    private HtmlOutputLabel lblCreatorType = new HtmlOutputLabel();
    private HtmlSelectOneMenu cboCreatorRole = new HtmlSelectOneMenu();
    private HtmlSelectOneMenu cboCreatorType = new HtmlSelectOneMenu();
    private HtmlCommandButton btHandleCreatorTypeChange = new HtmlCommandButton();

    // person
    private HtmlOutputLabel lblPersonGivenName = new HtmlOutputLabel();
    private HtmlOutputLabel lblPersonFamilyName = new HtmlOutputLabel();
    private HtmlInputText txtPersonGivenName = new HtmlInputText();
    private HtmlInputText txtPersonFamilyName = new HtmlInputText();

    // organization
    private HtmlOutputLabel lblOrganizationName = new HtmlOutputLabel();
    private HtmlOutputLabel lblOrganizationAddress = new HtmlOutputLabel();
    private HtmlInputText txtOrganizationName = new HtmlInputText();
    private HtmlInputTextarea txtaOrganizationAddress = new HtmlInputTextarea();
    //NiH: prepared for affiliation selection
    private HtmlCommandButton btSelect = new HtmlCommandButton();

    // constants for comboBoxes
    public SelectItem CREATORTYPE_PERSON = new SelectItem(CreatorVO.CreatorType.PERSON.toString(), labelBundle.getString("ENUM_CREATORTYPE_PERSON"));
    public SelectItem CREATORTYPE_ORGANIZATION = new SelectItem(CreatorVO.CreatorType.ORGANIZATION.toString(), labelBundle.getString("ENUM_CREATORTYPE_ORGANIZATION"));    
    private SelectItem[] CREATORTYPE_OPTIONS = new SelectItem[] { CREATORTYPE_PERSON, CREATORTYPE_ORGANIZATION };
    
    private SelectItem NO_ITEM_SET = new SelectItem("", labelBundle.getString("ENUM_NO_ITEM_SET"));
    private SelectItem CREATORROLE_ARTIST = new SelectItem(CreatorVO.CreatorRole.ARTIST.toString(), labelBundle.getString("ENUM_CREATORROLE_ARTIST"));
    private SelectItem CREATORROLE_AUTHOR = new SelectItem(CreatorVO.CreatorRole.AUTHOR.toString(), labelBundle.getString("ENUM_CREATORROLE_AUTHOR"));
    private SelectItem CREATORROLE_EDITOR = new SelectItem(CreatorVO.CreatorRole.EDITOR.toString(), labelBundle.getString("ENUM_CREATORROLE_EDITOR"));
    private SelectItem CREATORROLE_PAINTER = new SelectItem(CreatorVO.CreatorRole.PAINTER.toString(), labelBundle.getString("ENUM_CREATORROLE_PAINTER"));
    private SelectItem CREATORROLE_ILLUSTRATOR = new SelectItem(CreatorVO.CreatorRole.ILLUSTRATOR.toString(), labelBundle.getString("ENUM_CREATORROLE_ILLUSTRATOR"));
    private SelectItem CREATORROLE_PHOTOGRAPHER = new SelectItem(CreatorVO.CreatorRole.PHOTOGRAPHER.toString(), labelBundle.getString("ENUM_CREATORROLE_PHOTOGRAPHER"));
    private SelectItem CREATORROLE_COMMENTATOR = new SelectItem(CreatorVO.CreatorRole.COMMENTATOR.toString(), labelBundle.getString("ENUM_CREATORROLE_COMMENTATOR"));
    private SelectItem CREATORROLE_TRANSCRIBER = new SelectItem(CreatorVO.CreatorRole.TRANSCRIBER.toString(), labelBundle.getString("ENUM_CREATORROLE_TRANSCRIBER"));
    private SelectItem CREATORROLE_ADVISOR = new SelectItem(CreatorVO.CreatorRole.ADVISOR.toString(), labelBundle.getString("ENUM_CREATORROLE_ADVISOR"));
    private SelectItem CREATORROLE_TRANSLATOR = new SelectItem(CreatorVO.CreatorRole.TRANSLATOR.toString(), labelBundle.getString("ENUM_CREATORROLE_TRANSLATOR"));
    private SelectItem CREATORROLE_CONTRIBUTOR = new SelectItem(CreatorVO.CreatorRole.CONTRIBUTOR.toString(), labelBundle.getString("ENUM_CREATORROLE_CONTRIBUTOR"));
    private SelectItem[] CREATORROLE_OPTIONS = new SelectItem[] { NO_ITEM_SET, CREATORROLE_ARTIST, CREATORROLE_AUTHOR, CREATORROLE_EDITOR, CREATORROLE_PAINTER, CREATORROLE_ILLUSTRATOR, CREATORROLE_PHOTOGRAPHER, CREATORROLE_COMMENTATOR, CREATORROLE_TRANSCRIBER, CREATORROLE_ADVISOR, CREATORROLE_TRANSLATOR, CREATORROLE_CONTRIBUTOR };
    
    /**
     * Public constructor.
     * Initializes the UI.
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     * @param parentVO the parent ValueObject where the values of the UI have to be set.
     * @param parentValueBinding the string for the valueBinding of the UI.
     * @param indexComponent index of the abstract in the ValueObject.
     * 
     */    
    public CreatorUI(HtmlPanelGrid panDynamicParentPanel, List<CreatorVO> parentVO, String parentValueBinding, int indexComponent, boolean isRequired)
    {
        super(panDynamicParentPanel, parentVO, parentValueBinding, indexComponent, isRequired);     
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Constructing UI with index: " + indexComponent + ", parentVO: " + parentVO.getClass() + ", valueBinding: " + parentValueBinding);
        }
        
        // ScT: re-init the combo-boxes due to direct language switch
        this.i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        this.labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
        
        this.CREATORTYPE_PERSON = new SelectItem(CreatorVO.CreatorType.PERSON.toString(), labelBundle.getString("ENUM_CREATORTYPE_PERSON"));
        this.CREATORTYPE_ORGANIZATION = new SelectItem(CreatorVO.CreatorType.ORGANIZATION.toString(), labelBundle.getString("ENUM_CREATORTYPE_ORGANIZATION"));    
        this.CREATORTYPE_OPTIONS = new SelectItem[] { CREATORTYPE_PERSON, CREATORTYPE_ORGANIZATION };
        
        this.NO_ITEM_SET = new SelectItem("", labelBundle.getString("EditItem_NO_ITEM_SET"));
        this.CREATORROLE_ARTIST = new SelectItem(CreatorVO.CreatorRole.ARTIST.toString(), labelBundle.getString("ENUM_CREATORROLE_ARTIST"));
        this.CREATORROLE_AUTHOR = new SelectItem(CreatorVO.CreatorRole.AUTHOR.toString(), labelBundle.getString("ENUM_CREATORROLE_AUTHOR"));
        this.CREATORROLE_EDITOR = new SelectItem(CreatorVO.CreatorRole.EDITOR.toString(), labelBundle.getString("ENUM_CREATORROLE_EDITOR"));
        this.CREATORROLE_PAINTER = new SelectItem(CreatorVO.CreatorRole.PAINTER.toString(), labelBundle.getString("ENUM_CREATORROLE_PAINTER"));
        this.CREATORROLE_ILLUSTRATOR = new SelectItem(CreatorVO.CreatorRole.ILLUSTRATOR.toString(), labelBundle.getString("ENUM_CREATORROLE_ILLUSTRATOR"));
        this.CREATORROLE_PHOTOGRAPHER = new SelectItem(CreatorVO.CreatorRole.PHOTOGRAPHER.toString(), labelBundle.getString("ENUM_CREATORROLE_PHOTOGRAPHER"));
        this.CREATORROLE_COMMENTATOR = new SelectItem(CreatorVO.CreatorRole.COMMENTATOR.toString(), labelBundle.getString("ENUM_CREATORROLE_COMMENTATOR"));
        this.CREATORROLE_TRANSCRIBER = new SelectItem(CreatorVO.CreatorRole.TRANSCRIBER.toString(), labelBundle.getString("ENUM_CREATORROLE_TRANSCRIBER"));
        this.CREATORROLE_ADVISOR = new SelectItem(CreatorVO.CreatorRole.ADVISOR.toString(), labelBundle.getString("ENUM_CREATORROLE_ADVISOR"));
        this.CREATORROLE_TRANSLATOR = new SelectItem(CreatorVO.CreatorRole.TRANSLATOR.toString(), labelBundle.getString("ENUM_CREATORROLE_TRANSLATOR"));
        this.CREATORROLE_CONTRIBUTOR = new SelectItem(CreatorVO.CreatorRole.CONTRIBUTOR.toString(), labelBundle.getString("ENUM_CREATORROLE_CONTRIBUTOR"));
        this.CREATORROLE_OPTIONS = new SelectItem[] { NO_ITEM_SET, CREATORROLE_ARTIST, CREATORROLE_AUTHOR, CREATORROLE_EDITOR, CREATORROLE_PAINTER, CREATORROLE_ILLUSTRATOR, CREATORROLE_PHOTOGRAPHER, CREATORROLE_COMMENTATOR, CREATORROLE_TRANSCRIBER, CREATORROLE_ADVISOR, CREATORROLE_TRANSLATOR, CREATORROLE_CONTRIBUTOR };

        // set attributes for all GUI components
        this.lblCreator.setId(this.createUniqueId(this.lblCreator));
        this.lblCreator.setValue(labelBundle.getString("EditItem_lblCreator"));
        //this.lblCreator.setLabelLevel(2);
        this.getChildren().add(0, this.lblCreator); // place label in front of components from superclass, therefore position "0" is given here

        // attributes
        this.panAttributes.setId(this.createUniqueId(this.panAttributes));
        this.panAttributes.setColumns(2);
        this.panAttributes.setCellspacing("0");
        this.panAttributes.setCellpadding("0");
        this.panAttributes.setColumnClasses("editItemLabelColumn, editItemFieldColumn, editItemFieldColumn");

        this.lblCreatorRole.setId(this.createUniqueId(this.lblCreatorRole));
        this.lblCreatorRole.setValue(labelBundle.getString("EditItem_lblCreatorRole"));
        //this.lblCreatorRole.setLabelLevel(3);
        //this.lblCreatorRole.setRequiredIndicator(this.isRequired);
        this.cboCreatorRole.getChildren().clear();
        this.panAttributes.getChildren().add(this.lblCreatorRole);

        this.cboCreatorRole.setId(this.createUniqueId(this.cboCreatorRole));
        this.cboCreatorRole.getChildren().addAll(CommonUtils.convertToSelectItemsUI(this.CREATORROLE_OPTIONS));
        this.cboCreatorRole.setStyleClass("editItemComboBoxShort");
        this.panAttributes.getChildren().add(this.cboCreatorRole);

        this.lblCreatorType.setId(this.createUniqueId(this.lblCreatorType));
        this.lblCreatorType.setValue(labelBundle.getString("EditItem_lblCreatorType"));
        //this.lblCreatorType.setLabelLevel(3);
        this.panAttributes.getChildren().add(this.lblCreatorType);

        this.btHandleCreatorTypeChange.setId(this.createUniqueId(this.btHandleCreatorTypeChange));
        this.btHandleCreatorTypeChange.setValue("HandleCreatorTypeChange");
        this.btHandleCreatorTypeChange.setRendered(false);
        this.btHandleCreatorTypeChange.setImmediate(true);
        this.btHandleCreatorTypeChange.addActionListener(this);
        // added to container at the end (see beneath)

        this.cboCreatorType.getChildren().clear();
        this.cboCreatorType.setId(this.createUniqueId(this.cboCreatorType));
        this.cboCreatorType.getChildren().addAll(CommonUtils.convertToSelectItemsUI(this.CREATORTYPE_OPTIONS));
        this.cboCreatorType.setStyleClass("editItemComboBoxShort");
        this.cboCreatorType.setOnchange("document.getElementById(\"" + this.btHandleCreatorTypeChange.getClientId(FacesContext.getCurrentInstance()) + "\").click();");
        this.cboCreatorType.setImmediate(true);
        this.panAttributes.getChildren().add(this.cboCreatorType);

        // person
        this.lblPersonGivenName.setId(this.createUniqueId(this.lblPersonGivenName));
        this.lblPersonGivenName.setValue(labelBundle.getString("EditItem_lblPersonGivenName"));
        //this.lblPersonGivenName.setLabelLevel(3);
        // added in initializeUI()
        
        this.txtPersonGivenName.setId(this.createUniqueId(this.txtPersonGivenName));
        this.txtPersonGivenName.setStyleClass("editItemTextFieldShort");
        // added in initializeUI()
        
        this.lblPersonFamilyName.setId(this.createUniqueId(this.lblPersonFamilyName));
        this.lblPersonFamilyName.setValue(labelBundle.getString("EditItem_lblPersonFamilyName"));
        //this.lblPersonFamilyName.setLabelLevel(3);
        // added in initializeUI()
        
        this.txtPersonFamilyName.setId(this.createUniqueId(this.txtPersonFamilyName));
        this.txtPersonFamilyName.setStyleClass("editItemTextFieldShort");        
        // added in initializeUI()

        // organizations of the person
        this.panPersonOrganizations.setId(this.createUniqueId(this.panPersonOrganizations));
        this.panPersonOrganizations.setCellspacing("0");
        this.panPersonOrganizations.setCellpadding("0");
        // added to mainPanel in initializeUI()
        
        // organization
        this.lblOrganizationName.setId(this.createUniqueId(this.lblOrganizationName));
        this.lblOrganizationName.setValue(labelBundle.getString("EditItem_lblOrganizationName"));
        //this.lblOrganizationName.setLabelLevel(3);
        // added in initializeUI()
        
        this.txtOrganizationName.setId(this.createUniqueId(this.txtOrganizationName));
        this.txtOrganizationName.setStyleClass("editItemTextFieldMedium");
        // added in initializeUI()
        
        this.lblOrganizationAddress.setId(this.createUniqueId(this.lblOrganizationAddress));
        this.lblOrganizationAddress.setValue(labelBundle.getString("EditItem_lblOrganizationAddress"));
        //this.lblOrganizationAddress.setLabelLevel(3);
        // added in initializeUI()
        
        this.txtaOrganizationAddress.setId(this.createUniqueId(this.txtaOrganizationAddress));
        this.txtaOrganizationAddress.setStyleClass("editItemTextArea");
        // added in initializeUI()

        //NiH: prepared for affiliation selection
        Application application = FacesContext.getCurrentInstance().getApplication();
        this.btSelect.setRendered(false);
        this.btSelect.setId(this.createUniqueId(this.btSelect));
        this.btSelect.setImmediate(false);
        this.btSelect.addActionListener(this);
        this.btSelect.setValue(labelBundle.getString("EditItem_btSelect"));
        this.btSelect.setStyleClass("editDynamicButton");
        this.btSelect.setAction(application.createMethodBinding("#{EditItem.loadAffiliationTree}", null));
        // added in initializeUI()
        
        this.getChildren().add(this.panAttributes);
                      
        // has to be added here so it does not destroy the layout
        this.getChildren().add(this.btHandleCreatorTypeChange);

        // initialize the parentVO for valueBinding if needed
        this.initializeParentVO();
        
        // set the valueBinding for all fields
        this.createValueBinding();

        // initialize the UI on depending values
        this.initializeUI();
    }
    
    /**
     * Initialize the UI on depending values.
     */
    protected void initializeUI()
    {
        if (this.getParentVO().get(this.indexComponent).getPerson() != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Showing attributes of person...");
            }

            // add person attributes
            this.panAttributes.getChildren().add(this.lblPersonGivenName);
            this.panAttributes.getChildren().add(this.txtPersonGivenName);
            this.panAttributes.getChildren().add(this.lblPersonFamilyName);
            this.panAttributes.getChildren().add(this.txtPersonFamilyName);
            // set required fields
            //this.lblPersonFamilyName.setRequiredIndicator(this.isRequired);
            
            // remove organization attributes
            this.panAttributes.getChildren().remove(this.lblOrganizationName);
            this.panAttributes.getChildren().remove(this.txtOrganizationName);
            this.panAttributes.getChildren().remove(this.lblOrganizationAddress);
            this.panAttributes.getChildren().remove(this.txtaOrganizationAddress);
            //NiH: prepared for affiliation selection
            this.panAttributes.getChildren().remove(this.btSelect);
            // remove required fields
            //this.lblOrganizationName.setRequiredIndicator(false);
            
            // add organizations of the person
            this.panPersonOrganizations.getChildren().clear();
            for (int i = 0; i < this.getParentVO().get(this.indexComponent).getPerson().getOrganizations().size(); i++)
            {
                OrganizationUI newPersonOrganization = new OrganizationUI(this.panPersonOrganizations, this.getParentVO().get(this.indexComponent).getPerson().getOrganizations(), this.parentValueBinding + "[" + this.indexComponent + "].person.organizations", i);
                this.panPersonOrganizations.getChildren().add(newPersonOrganization);
            }
            this.getChildren().add(this.panPersonOrganizations);
        }
        else if (this.getParentVO().get(this.indexComponent).getOrganization() != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Showing attributes of organization...");
            }

            // add organization attributes
            this.panAttributes.getChildren().add(this.lblOrganizationName);
            this.panAttributes.getChildren().add(this.txtOrganizationName);

            this.panAttributes.getChildren().add(this.lblOrganizationAddress);
            this.panAttributes.getChildren().add(this.txtaOrganizationAddress);            

            //NiH: prepared for affiliation selection
            this.panAttributes.getChildren().add(this.btSelect);

            // set required fields
            //NiH: prepared for affiliation selection
            //this.lblOrganizationName.setRequiredIndicator(this.isRequired);

            // remove person attributes
            this.panAttributes.getChildren().remove(this.lblPersonGivenName);
            this.panAttributes.getChildren().remove(this.txtPersonGivenName);
            this.panAttributes.getChildren().remove(this.lblPersonFamilyName);
            this.panAttributes.getChildren().remove(this.txtPersonFamilyName);
            // remove required fields
            //this.lblPersonFamilyName.setRequiredIndicator(false);
            // remove organizations of the person
            this.getChildren().remove(this.panPersonOrganizations);
        }
    }
    
    /**
     * Initializes the parent ValueObject with a new sub ValueObject for valueBinding if needed.
     */
    protected void initializeParentVO()
    {        
        if (logger.isDebugEnabled())
        {
            logger.debug("Initializing parentVO with new values for valueBinding...");
        }

        // initialize creator if none is given, so that valueBinding is possible
        if (this.getParentVO().size() == 0)
        {
            this.getParentVO().add(new CreatorVO());

            if (logger.isDebugEnabled())
            {
                logger.debug("New CreatorVO added.");
            }
        }
        
        // initialize person
        for (int i=0; i<this.getParentVO().size(); i++)
        {
            if (this.getParentVO().get(i).getPerson() == null
                    && this.getParentVO().get(i).getOrganization() == null)
            {
                this.getParentVO().get(i).setPerson(new PersonVO());

                if (logger.isDebugEnabled())
                {
                    logger.debug("New PersonVO added.");
                }
            }                
            
            // initialize organization of a person
            if (this.getParentVO().get(i).getPerson() != null
                    && this.getParentVO().get(i).getPerson().getOrganizations().size() == 0)
            {
                OrganizationVO newOrganizationVO = new OrganizationVO();
                newOrganizationVO.setName(new TextVO());
                this.getParentVO().get(i).getPerson().getOrganizations().add(newOrganizationVO);

                if (logger.isDebugEnabled())
                {
                    logger.debug("New OrganizationVO added to person.");
                }
            }
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

        // valueBinding for a creator
        this.cboCreatorRole.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + "[" + this.indexComponent + "].roleString}"));        
        this.cboCreatorType.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + "[" + this.indexComponent + "].typeString}"));

        if (this.getParentVO().get(this.indexComponent).getPerson() != null)
        {
            this.txtPersonGivenName.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + "[" + this.indexComponent + "].person.givenName}"));
            this.txtPersonFamilyName.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + "[" + this.indexComponent + "].person.familyName}"));

            this.txtOrganizationName.setValueBinding("value", null);
            this.txtaOrganizationAddress.setValueBinding("value", null);
        }
        else if (this.getParentVO().get(this.indexComponent).getOrganization() != null)
        {            
            this.txtOrganizationName.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + "[" + this.indexComponent + "].organization.name.value}"));
            this.txtaOrganizationAddress.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + "[" + this.indexComponent + "].organization.address}"));

            this.txtPersonGivenName.setValueBinding("value", null);
            this.txtPersonFamilyName.setValueBinding("value", null);
        }        
    }

    /**
     * Creates the panel newly according to the values in the ValueObject.
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     * @param parentVO the parent ValueObject where the values of the UI have to be set.
     * @param parentValueBinding the string for the valueBinding of the UI.
     */
    public static void createDynamicParentPanel(HtmlPanelGrid panDynamicParentPanel, List<CreatorVO> parentVO, String parentValueBinding, boolean isRequired)
    {        
        if (logger.isDebugEnabled())
        {
            logger.debug("Create dynamic parent panel...");
        }

        // remove all components
        panDynamicParentPanel.getChildren().clear();

        // add all creators 
        for (int i = 0; i < parentVO.size(); i++)
        {
            panDynamicParentPanel.getChildren().add(new CreatorUI(panDynamicParentPanel, parentVO, parentValueBinding, i, isRequired));
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
        this.getParentVO().add(indexNewComponent, new CreatorVO());

        // initialize parentVO newly if needed
        this.initializeParentVO();
        
        // add new component to enclosing panel
        CreatorUI newComponentUI = new CreatorUI(this.panDynamicParentPanel, this.getParentVO(), this.parentValueBinding, indexNewComponent, this.isRequired);
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
        CreatorUI.createDynamicParentPanel(this.panDynamicParentPanel, this.getParentVO(), this.parentValueBinding, this.isRequired);
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

        this.getParentVO().get(this.indexComponent).setRoleString(CommonUtils.getUIValue(this.cboCreatorRole));
        this.getParentVO().get(this.indexComponent).setTypeString(CommonUtils.getUIValue(this.cboCreatorType));

        if (this.getParentVO().get(this.indexComponent).getPerson() != null)
        {
            this.getParentVO().get(this.indexComponent).getPerson().setGivenName(CommonUtils.getUIValue(this.txtPersonGivenName));
            this.getParentVO().get(this.indexComponent).getPerson().setFamilyName(CommonUtils.getUIValue(this.txtPersonFamilyName));
            
            // store organizations of person
            ((AbstractUI)this.panPersonOrganizations.getChildren().get(0)).storeAllValuesOfDynamicParentPanel();
        }
        else if (this.getParentVO().get(this.indexComponent).getOrganization() != null)
        {
            // TODO FrM: Replace null value for language when language can be selected in the edit item page
            this.getParentVO().get(this.indexComponent).getOrganization().setName(new TextVO(CommonUtils.getUIValue(this.txtOrganizationName), ""));
            this.getParentVO().get(this.indexComponent).getOrganization().setAddress(CommonUtils.getUIValue(this.txtaOrganizationAddress));
        }
        else
        {
            logger.warn("No values stored!");
        }
    }
    
    /**
     * Switches from one creater type to the other.
     * @param newCreatorType the new creator type that should be switched to
     */
    private void switchCreatorType(String newCreatorType)
    {
        if (newCreatorType.equals(CreatorUI.this.CREATORTYPE_ORGANIZATION.getValue()))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Switching to organization...");
            }

            this.getParentVO().get(this.indexComponent).setPerson(null);
            OrganizationVO newOrganizationVO = new OrganizationVO();
            newOrganizationVO.setName(new TextVO());
            this.getParentVO().get(this.indexComponent).setOrganization(newOrganizationVO);
        }
        else if (newCreatorType.equals(CreatorUI.this.CREATORTYPE_PERSON.getValue()))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Switching to person...");
            }

            this.getParentVO().get(this.indexComponent).setOrganization(null);
            this.getParentVO().get(this.indexComponent).setPerson(new PersonVO());
        }
        
        // initialize parentVO newly if needed
        this.initializeParentVO();
        
        // set the valueBinding for all fields
        this.createValueBinding();

        // initilialize UI newly
        this.initializeUI();
    }

    /**
     * Returns the parent ValueObject.
     * @return the parent ValueObject
     */
    private List<CreatorVO> getParentVO()
    {
        return (List<CreatorVO>)this.parentVO;
    }
    
    /**
     * Action handler for user actions.
     * @param ActionEvent event
     */
    public void processAction(ActionEvent event)
    {
        if (event.getSource().equals(this.btHandleCreatorTypeChange))
        {
            String newCreatorType = CommonUtils.getUIValue(this.cboCreatorType);
                        
            if (logger.isDebugEnabled())
            {                                
                logger.debug("New value for creator type is: " + newCreatorType);
            }
            
            this.switchCreatorType(newCreatorType);
        }
        else
        {
            super.processAction(event);
        }
    }
}
