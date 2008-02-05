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
import org.apache.log4j.Logger;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html. HtmlOutputLabel;
import javax.faces.component.html.HtmlInputText;
import javax.faces.model.SelectItem;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;

/**
 * UI component for editing identifiers.
 *
 * @author: Thomas Diebäcker, created 20.06.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 * Revised by DiT: 14.08.2007
 */
public class IdentifierUI extends AbstractUI
{
    private static Logger logger = Logger.getLogger(IdentifierUI.class);

    // for handling the resource bundles (i18n)
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    private ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
    
    // GUI components
    private HtmlPanelGrid panAttributes = new HtmlPanelGrid();
    private HtmlOutputLabel lblIdentifier = new HtmlOutputLabel();
    private HtmlSelectOneMenu cboIdentifierType = new HtmlSelectOneMenu();
    private HtmlInputText txtIdentifier = new HtmlInputText();

    // constants for comboBoxes
    protected final SelectItem NO_ITEM_SET = new SelectItem("", labelBundle.getString("EditItem_NO_ITEM_SET"));
    protected final SelectItem IDENTIFIERTYPE_URI = new SelectItem(IdentifierVO.IdType.URI.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_URI"));
    protected final SelectItem IDENTIFIERTYPE_ISBN = new SelectItem(IdentifierVO.IdType.ISBN.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_ISBN"));
    protected final SelectItem IDENTIFIERTYPE_ISSN = new SelectItem(IdentifierVO.IdType.ISSN.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_ISSN"));
    protected final SelectItem IDENTIFIERTYPE_DOI = new SelectItem(IdentifierVO.IdType.DOI.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_DOI"));
    protected final SelectItem IDENTIFIERTYPE_URN = new SelectItem(IdentifierVO.IdType.URN.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_URN"));
    protected final SelectItem IDENTIFIERTYPE_EDOC = new SelectItem(IdentifierVO.IdType.EDOC.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_EDOC"));
    protected final SelectItem IDENTIFIERTYPE_ESCIDOC = new SelectItem(IdentifierVO.IdType.ESCIDOC.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_ESCIDOC"));
    protected final SelectItem IDENTIFIERTYPE_ISI = new SelectItem(IdentifierVO.IdType.ISI.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_ISI"));
    protected final SelectItem IDENTIFIERTYPE_PND = new SelectItem(IdentifierVO.IdType.PND.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_PND"));
    protected final SelectItem IDENTIFIERTYPE_OTHER = new SelectItem(IdentifierVO.IdType.OTHER.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_OTHER"));
    protected final SelectItem[] IDENTIFIERTYPE_OPTIONS = new SelectItem[] { NO_ITEM_SET, IDENTIFIERTYPE_URI, IDENTIFIERTYPE_ISBN, IDENTIFIERTYPE_ISSN, IDENTIFIERTYPE_DOI, IDENTIFIERTYPE_URN, IDENTIFIERTYPE_URN, IDENTIFIERTYPE_EDOC, IDENTIFIERTYPE_ESCIDOC, IDENTIFIERTYPE_ISI, IDENTIFIERTYPE_PND, IDENTIFIERTYPE_OTHER };

    /**
     * Public constructor.
     * Initializes the UI.
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     * @param parentVO the parent ValueObject where the values of the UI have to be set.
     * @param parentValueBinding the string for the valueBinding of the UI.
     * @param indexComponent index of the identifier in the ValueObject.
     * 
     */    
    public IdentifierUI(HtmlPanelGrid panDynamicParentPanel, List<IdentifierVO> parentVO, String parentValueBinding, int indexComponent)
    {
        super(panDynamicParentPanel, parentVO, parentValueBinding, indexComponent);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Constructing UI with index: " + indexComponent + ", parentVO: " + parentVO.getClass() + ", valueBinding: " + parentValueBinding);
        }
        
        this.i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        this.labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
        
        // set attributes for all GUI components
        this.panAttributes.setId(this.createUniqueId(this.panAttributes));
        this.panAttributes.setColumns(3);
        this.panAttributes.setColumnClasses("editItemLabelColumn, editItemFieldColumn, editItemFieldColumn");
        this.panAttributes.setCellspacing("0");
        this.panAttributes.setCellpadding("0");
        
        this.lblIdentifier.setId(this.createUniqueId(this.lblIdentifier));
        this.lblIdentifier.setFor(this.txtIdentifier.getId());
        this.lblIdentifier.setValue(labelBundle.getString("EditItem_lblIdentifier"));
        //this.lblIdentifier.setLabelLevel(3);
        this.panAttributes.getChildren().add(this.lblIdentifier);
        
        this.cboIdentifierType.setId(this.createUniqueId(this.cboIdentifierType));
        this.cboIdentifierType.setStyleClass("editItemComboBoxVeryShort");
        this.cboIdentifierType.getChildren().clear();
        this.cboIdentifierType.getChildren().addAll(CommonUtils.convertToSelectItemsUI(this.IDENTIFIERTYPE_OPTIONS));
        this.panAttributes.getChildren().add(this.cboIdentifierType);
        
        this.txtIdentifier.setId(this.createUniqueId(this.txtIdentifier));
        this.txtIdentifier.setStyleClass("editItemTextFieldShort");
        this.panAttributes.getChildren().add(this.txtIdentifier);

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
        // initialize identifier if none is given, so that valueBinding is possible
        if (this.getParentVO().size() == 0)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Initializing parentVO with new values for valueBinding...");
            }

            IdentifierVO newIdentifier = new IdentifierVO();
            this.getParentVO().add(newIdentifier);
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

        // valueBinding for an identifier
        this.cboIdentifierType.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + "[" + this.indexComponent + "].typeString}"));
        this.txtIdentifier.setValueBinding("value", application.createValueBinding("#{" + this.parentValueBinding + "[" + this.indexComponent + "].id}"));
    }

    /**
     * Creates the panel newly according to the values in the ValueObject.
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     * @param parentVO the parent ValueObject where the values of the UI have to be set.
     * @param parentValueBinding the string for the valueBinding of the UI.
     */
    public static void createDynamicParentPanel(HtmlPanelGrid panDynamicParentPanel, List<IdentifierVO> parentVO, String parentValueBinding)
    {        
        if (logger.isDebugEnabled())
        {
            logger.debug("Create dynamic parent panel...");
        }

        // remove all components
        panDynamicParentPanel.getChildren().clear();

        // add all identifier   
        for (int i = 0; i < parentVO.size(); i++)
        {
            panDynamicParentPanel.getChildren().add(new IdentifierUI(panDynamicParentPanel, parentVO, parentValueBinding, i));
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
        this.getParentVO().add(indexNewComponent, new IdentifierVO());
        
        // add new component to enclosing panel
        IdentifierUI newComponentUI = new IdentifierUI(this.panDynamicParentPanel, this.getParentVO(), this.parentValueBinding, indexNewComponent);
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
        IdentifierUI.createDynamicParentPanel(this.panDynamicParentPanel, this.getParentVO(), this.parentValueBinding);
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

        this.getParentVO().get(this.indexComponent).setTypeString(CommonUtils.getUIValue(this.cboIdentifierType));
        this.getParentVO().get(this.indexComponent).setId(CommonUtils.getUIValue(this.txtIdentifier));
    }
    
    /**
     * Returns the parent ValueObject.
     * @return the parent ValueObject
     */
    private List<IdentifierVO> getParentVO()
    {
        return (List<IdentifierVO>)this.parentVO;
    }
}
