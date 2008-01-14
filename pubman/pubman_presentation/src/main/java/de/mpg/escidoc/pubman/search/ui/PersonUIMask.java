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

import java.util.ArrayList;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import com.sun.rave.web.ui.component.Label;
import com.sun.rave.web.ui.component.TextField;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.PersonCriterionVO;

/**
* This mask collects search data for a given person and a person role.
* @author endres
* @version $Revision: 1655 $ $LastChangedDate: 2007-12-10 17:56:03 +0100 (Mon, 10 Dec 2007) $
*
*/
public class PersonUIMask extends UIMask implements ActionListener
{
    
    private HTMLElementUI htmlElement = new HTMLElementUI();
    private HtmlPanelGroup panel1 = new HtmlPanelGroup();
    private HtmlPanelGroup panel2 = new HtmlPanelGroup();
    private HtmlPanelGroup panel3 = new HtmlPanelGroup();
    
    private Label lblSearchStringPerson = new Label();
    private TextField txtSearchStringPerson = new TextField();
    private Label lblChk = new Label();
    private HtmlCommandButton btAll = new HtmlCommandButton();
    
    ArrayList<PersonCheckBoxLabelUI> checkboxLabels = new ArrayList<PersonCheckBoxLabelUI>();
    
    private static final String AUTHOR_BUNDLE_KEY = "adv_search_lblSearchPerson_author";
    private static final String EDITOR_BUNDLE_KEY = "adv_search_lblSearchPerson_editor";
    private static final String ADVISOR_BUNDLE_KEY = "adv_search_lblSearchPerson_advisor";
    private static final String ARTIST_BUNDLE_KEY = "adv_search_lblSearchPerson_artist";
    private static final String COMMENTATOR_BUNDLE_KEY = "adv_search_lblSearchPerson_commentator";
    private static final String CONTRIBUTOR_BUNDLE_KEY = "adv_search_lblSearchPerson_contributor";
    private static final String ILLUSTRATOR_BUNDLE_KEY = "adv_search_lblSearchPerson_illustrator";
    private static final String PAINTER_BUNDLE_KEY = "adv_search_lblSearchPerson_painter";
    private static final String PHOTOGRAPHER_BUNDLE_KEY = "adv_search_lblSearchPerson_photographer";
    private static final String TRANSCRIBER_BUNDLE_KEY = "adv_search_lblSearchPerson_transcriber";
    private static final String TRANSLATOR_BUNDLE_KEY = "adv_search_lblSearchPerson_translator";
    
    
    
    /**
     * Implements a text input field and a checkbox.
     * @param st search type reference
     */
    public PersonUIMask( SearchTypeUI st )
    {
        super( st );
        
        this.panel1.setId(CommonUtils.createUniqueId(this.panel1));
        this.panel1.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "searchTerm"));
        this.lblSearchStringPerson.setId(CommonUtils.createUniqueId(this.panel1));
        this.lblSearchStringPerson.setValue(bundle.getString("adv_search_lblSearchPerson"));            
        this.txtSearchStringPerson.setImmediate(true);
        this.panel1.getChildren().add(lblSearchStringPerson);
        this.panel1.getChildren().add(txtSearchStringPerson);
        this.panel1.getChildren().add(htmlElement.getEndTag("div"));
        this.addPanelToCommonPanel( this.panel1 );

        this.panel3.setId(CommonUtils.createUniqueId(this.panel3)); 
//      TODO endres: change this to markAll div
        this.panel3.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "clearButton"));
        
        this.btAll.setId(CommonUtils.createUniqueId(this.btAll));
        this.btAll.setValue(bundle.getString("adv_search_btAll"));
        this.btAll.setImmediate(true);
        this.btAll.setStyleClass("inlineButton");
        this.btAll.addActionListener(this);
        this.lblChk.setId(CommonUtils.createUniqueId(this.lblChk));
        this.lblChk.setValue(bundle.getString("adv_search_lblChkRole"));         
        this.panel3.getChildren().add(this.lblChk);
        this.panel3.getChildren().add(this.btAll);
        this.panel3.getChildren().add(htmlElement.getEndTag("div"));
        this.addPanelToCommonPanel( this.panel3 );
        
        
        this.panel2.setId(CommonUtils.createUniqueId(this.panel2));
        this.panel2.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "formGroupCheckboxes"));   
       
        this.createCheckBoxLabelUIs();
        
        for( int i = 0; i < this.checkboxLabels.size(); i++ )
        {
        	this.checkboxLabels.get( i ).addToPanel( panel2 );
        }
        
        this.panel2.getChildren().add(htmlElement.getEndTag("div"));
        this.addPanelToCommonPanel( panel2 );
        
        // finally add the buttons
        super.addButtonsAndLogicOperatorToPanel();
    }
    
    public void processAction(ActionEvent event) 
    {
        if (event.getSource().equals(this.btAll))
        {
            if (checkAllCheckboxes( true ) )
            {
                this.selectAllCheckboxes(false);
            }
            else
            {
                this.selectAllCheckboxes(true);
            }
        }
    }
    
    private void createCheckBoxLabelUIs() 
    {
    	this.checkboxLabels.add( 
    			new PersonCheckBoxLabelUI( CreatorRole.AUTHOR, PersonUIMask.AUTHOR_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new PersonCheckBoxLabelUI( CreatorRole.EDITOR, PersonUIMask.EDITOR_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new PersonCheckBoxLabelUI( CreatorRole.ADVISOR, PersonUIMask.ADVISOR_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new PersonCheckBoxLabelUI( CreatorRole.ARTIST, PersonUIMask.ARTIST_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new PersonCheckBoxLabelUI( CreatorRole.COMMENTATOR, PersonUIMask.COMMENTATOR_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new PersonCheckBoxLabelUI( CreatorRole.CONTRIBUTOR, PersonUIMask.CONTRIBUTOR_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new PersonCheckBoxLabelUI( CreatorRole.ILLUSTRATOR, PersonUIMask.ILLUSTRATOR_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new PersonCheckBoxLabelUI( CreatorRole.PAINTER, PersonUIMask.PAINTER_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new PersonCheckBoxLabelUI( CreatorRole.PHOTOGRAPHER, PersonUIMask.PHOTOGRAPHER_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new PersonCheckBoxLabelUI( CreatorRole.TRANSCRIBER, PersonUIMask.TRANSCRIBER_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new PersonCheckBoxLabelUI( CreatorRole.TRANSLATOR, PersonUIMask.TRANSLATOR_BUNDLE_KEY, bundle ) );
    }
    
    /**
     * tests if all checkboxes are checked.
     * @return boolean: true if all are checked, false otherwise
     */
    private boolean checkAllCheckboxes( boolean state )
    {
    	for( int i = 0; i < this.checkboxLabels.size(); i++ )
    	{
    		if( this.checkboxLabels.get( i ).isSelected() == !state )
    		{
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * selects/unselects all checkboxes.
     * @param select (boolean): true = select all, fals unselect all.
     */
    private void selectAllCheckboxes(boolean select)
    {           
    	for( int i = 0; i < this.checkboxLabels.size(); i++ )
     	{
     		this.checkboxLabels.get( i ).setSelected( select );
     	}
    }
    
    @Override
    void clearForm()
    {
        this.txtSearchStringPerson.setValue("");
        selectAllCheckboxes(false);
    }

    @Override
    CriterionVO getCriterionFromArrays()
    {
        // set search string 
        PersonCriterionVO personCriterionVO = new PersonCriterionVO();
        personCriterionVO.setSearchString((String)this.getTxtSearchStringPerson().getText());
        
        // set the creator role
        personCriterionVO.setCreatorRole(this.getCreatorRoles());
        return personCriterionVO;
    }

    @Override
    boolean hasData()
    {
        String searchString = (String)this.getTxtSearchStringPerson().getText();
        if( searchString != null && searchString.length() > 0 ) 
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private ArrayList<CreatorRole> getCreatorRoles()
    {
        ArrayList<CreatorRole> list = new ArrayList<CreatorRole>();
        for( int i = 0; i < this.checkboxLabels.size(); i++ )
    	{
    		if( this.checkboxLabels.get( i ).isSelected() == true )
    		{
    			list.add( this.checkboxLabels.get( i ).getCreatorRole() );
    		}
    	}
        return list;
    }
    
    @Override
    public void refreshAppearance() 
    {
//    	 refresh the buttons and operator
    	super.refreshAppearanceButtonsAndOp();
    	
    	this.btAll.setValue(bundle.getString("adv_search_btAll"));
    	this.lblSearchStringPerson.setValue(bundle.getString("adv_search_lblSearchPerson"));
    	this.lblChk.setValue(bundle.getString("adv_search_lblChkRole"));  
    	
    	for( int i = 0; i < this.checkboxLabels.size(); i++ )
    	{
    		this.checkboxLabels.get( i ).updateLanguage( bundle );
    	}
    }

    public TextField getTxtSearchStringPerson()
    {
        return txtSearchStringPerson;
    }
}
