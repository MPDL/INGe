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
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.search.ui;

import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlPanelGroup;

import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;

/**
 * A standard query mask. Implements basic functionality for adding and removing masks and to 
 * clear the form.
 * @author endres
 * $Revision: 1639 $ $LastChangedDate: 2007-12-04 15:06:47 +0100 (Di, 04 Dez 2007) $
 *
 */
public abstract class UIMask extends InternationalizedImpl
{
    /** hmtl helper */
    protected HTMLElementUI htmlElement = new HTMLElementUI();
    
    /** the logicOperator at the end of mask. only used between same types of masks */
    private LogicOperatorUI logicOperator = new LogicOperatorUI();
    
    /** reference to the search type class */
    protected SearchTypeUI searchType;
    
    /** Add form button */
    private HtmlCommandButton btAdd = new HtmlCommandButton();
    /** delete form button */
    private HtmlCommandButton btDelete = new HtmlCommandButton();
    /** clear from button */
    private HtmlCommandButton btClearForm = new HtmlCommandButton();
    
    /** panel for the whole mask */
    private HtmlPanelGroup panel = new HtmlPanelGroup();
    
    /** panel for buttons */
    private HtmlPanelGroup panelButtons = new HtmlPanelGroup();
    /** panel for logicOperator */
    private HtmlPanelGroup panelLogicOperator = new HtmlPanelGroup();
    
    /**
     * Clear all the forms in a mask.
     *
     */
    abstract void clearForm();
    
    /**
     * Fetches the criterion from the input fields. The logicoperator is not included.
     * @return search criterium
     */
    abstract CriterionVO getCriterionFromArrays();
    
    /**
     * Checks if mask has useable query data.
     * @return flag if mask has data
     */
    abstract boolean hasData();
    
    /**
     * Create a new instance.
     * @param st instance of search type 
     */
    
    /**
     * Refresh the appearance of the mask. Language, blind out some fields etc.
     */
    abstract void refreshAppearance();
    
    /**
     * Implements basic services for adding, removing a form and clear the data input form.
     * The search type reference is used as an action listener for this functionality.
     * @param st search type reference
     */
    public UIMask( SearchTypeUI st )
    {
        this.searchType = st;
        
        this.panel.setId(CommonUtils.createUniqueId(this.panel));
        this.panel.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "formField"));
    }
    
    /**
     * Refresh the appearance of the buttons and of the operator. This is used mainly if the language of the 
     * search mask changes.
     *
     */
    protected void refreshAppearanceButtonsAndOp()
    {
    	this.btClearForm.setValue(getLabel("adv_search_btClear"));
    	this.btAdd.setValue(getLabel("adv_search_btAdd"));
    	this.btDelete.setValue(getLabel("adv_search_btRemove"));
    }
    
    /**
     * Show the 'Delete form' button of the mask.
     * @param showOperator  true, show the operator; false, don't show it
     */
    void showDeleteFormButton( boolean showButton ) 
    {
        this.btDelete.setRendered( showButton );
    }
    
    /**
     * Show the logical operator of the mask.
     * @param showOperator  true, show the operator; false, don't show it
     */
    void showLogicalOperator( boolean showOperator ) 
    {
        this.logicOperator.setVisible( showOperator );
    }
    
    /** 
     * Add a 'clear form', a 'Add form' and a 'delete form' button to 
     * the common root panel.
     *
     */
    void addButtonsAndLogicOperatorToPanel() 
    {        
//      id for enabling/disabling
        this.panelButtons.setId(CommonUtils.createUniqueId(this.panelButtons));
        this.panelButtons.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "clearButton"));
        this.btClearForm.setId(CommonUtils.createUniqueId(this.btClearForm));
        this.btClearForm.setValue(getLabel("adv_search_btClear"));
        this.btClearForm.setImmediate(true);
        this.btClearForm.setStyleClass("inlineButton");
        this.btClearForm.addActionListener(searchType);
        this.panelButtons.getChildren().add(this.btClearForm);
        this.panelButtons.getChildren().add(htmlElement.getEndTag("div"));
        this.panelButtons.getChildren().add(htmlElement.getEndTag("div"));
        this.panelButtons.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "objectButtons"));
        this.btAdd.setId(CommonUtils.createUniqueId(this.btAdd));
        this.btAdd.setValue(getLabel("adv_search_btAdd"));
        this.btAdd.setImmediate(true);
        this.btAdd.setStyleClass("inlineButton");
        this.btAdd.addActionListener(searchType);
        this.panelButtons.getChildren().add(this.btAdd);

        this.btDelete.setId(CommonUtils.createUniqueId(this.panel));
        this.btDelete.setValue(getLabel("adv_search_btRemove"));
        this.btDelete.setImmediate(true);
        this.btDelete.setStyleClass("inlineButton");
        this.btDelete.addActionListener(searchType);
   
        this.panelButtons.getChildren().add(this.btDelete);
        this.panelButtons.getChildren().add(htmlElement.getEndTag("div"));
        this.panel.getChildren().add(this.panelButtons);
        
        this.panelLogicOperator.setId(CommonUtils.createUniqueId(this.panel));
        this.panelLogicOperator.getChildren().add(logicOperator.getUIComponent());
        this.panel.getChildren().add(this.panelLogicOperator);
    }
    
    /**
     * Add a panel to the common root panel of the mask
     * @param p panel to be included to the common root panel
     */
    void addPanelToCommonPanel( HtmlPanelGroup p ) 
    {
        this.panel.getChildren().add( p );
    }
    
    /**
     * Fetches the criterions from the mask.
     * 
     * @param useLogicOperator include the logicOperator in the criterion
     * @return search criterions
     */
    public CriterionVO getCriterion( boolean useLogicOperator )
    {
        CriterionVO crit = this.getCriterionFromArrays();
        if( useLogicOperator == true )
        {
            crit.setLogicOperator(
                    LogicOperatorUI.getLogicOperatorByString(
                            (this.getLogicOperator().getCboLogicOperator().getValue().toString())));
        }
        return crit;
    }
      
    HtmlPanelGroup getMaskPanel()
    {
        return this.panel;
    }
    public LogicOperatorUI getLogicOperator()
    {
        return logicOperator;
    }
    public HtmlCommandButton getButtonAdd()
    {
        return btAdd;
    }
    public HtmlCommandButton getButtonClearForm()
    {
        return btClearForm;
    }
    public HtmlCommandButton getButtonDelete()
    {
        return btDelete;
    }
}
