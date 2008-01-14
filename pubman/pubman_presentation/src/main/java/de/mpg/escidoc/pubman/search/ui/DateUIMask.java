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
import java.util.List;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import com.sun.rave.web.ui.component.Label;
import com.sun.rave.web.ui.component.TextField;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.DateCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.DateCriterionVO.DateType;

/**
 * This mask collects search data for date intervalls and type of date intervalls.
 * @author endres
 * @version $Revision: 1654 $ $LastChangedDate: 2007-12-10 17:55:31 +0100 (Mon, 10 Dec 2007) $
 *
 */
public class DateUIMask extends UIMask implements ActionListener
{   
    private HTMLElementUI htmlElement = new HTMLElementUI();
    private HtmlPanelGroup panel1 = new HtmlPanelGroup();
    private HtmlPanelGroup panel2 = new HtmlPanelGroup();
    private HtmlPanelGroup panel3 = new HtmlPanelGroup();
    private Label lblSearchDateFrom = new Label();
    private TextField txtSearchDateFrom = new TextField();
    private Label lblSearchDateTo = new Label();
    private TextField txtSearchDateTo = new TextField();
    private Label lblChk = new Label();
    private HtmlCommandButton btAll = new HtmlCommandButton();
    
    /** list of checkboxlabels */
    ArrayList<DateCheckBoxLabelUI> checkboxLabels = new ArrayList<DateCheckBoxLabelUI>();
    
    private static final String ACCEPTED_BUNDLE_KEY = "adv_search_lblChkType_accepted";
    private static final String CREATED_BUNDLE_KEY = "adv_search_lblChkType_created";
    private static final String MODIFIED_BUNDLE_KEY = "adv_search_lblChkType_modified";
    private static final String PUBLISHEDON_BUNDLE_KEY = "adv_search_lblChkType_publishedon";
    private static final String PUBLISHEDPR_BUNDLE_KEY = "adv_search_lblChkType_publishedpr";
    private static final String SUBMITTED_BUNDLE_KEY = "adv_search_lblChkType_submitted";
    
    /**
     * Implements two date intervall input fields and a checkbox for the type of date.
     * @param st search type reference
     */
    public DateUIMask( SearchTypeUI st ) 
    {
        super( st );
        
        this.panel1.setId(CommonUtils.createUniqueId(this.panel1));
        this.panel1.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "searchTerm"));
        this.lblSearchDateFrom.setId(CommonUtils.createUniqueId(this.lblSearchDateFrom));
        this.lblSearchDateFrom.setValue(bundle.getString("adv_search_lblFrom"));
        this.txtSearchDateFrom.setId(CommonUtils.createUniqueId(this.txtSearchDateFrom));
        this.txtSearchDateFrom.setImmediate(true);
        this.panel1.getChildren().add(lblSearchDateFrom);
        this.panel1.getChildren().add(txtSearchDateFrom);
        this.lblSearchDateTo.setId(CommonUtils.createUniqueId(this.lblSearchDateTo));
        this.lblSearchDateTo.setValue(bundle.getString("adv_search_lblTo"));
        this.txtSearchDateTo.setId(CommonUtils.createUniqueId(this.txtSearchDateTo));
        this.txtSearchDateTo.setImmediate(true);
        this.panel1.getChildren().add(lblSearchDateTo);
        this.panel1.getChildren().add(txtSearchDateTo);
        this.panel1.getChildren().add(htmlElement.getEndTag("div"));
        this.addPanelToCommonPanel( this.panel1 ); 
        
        this.panel3.setId(CommonUtils.createUniqueId(this.panel3));
        //TODO endres: change this to markAll div
        this.panel3.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "clearButton"));
        this.btAll.setId(CommonUtils.createUniqueId(this.btAll));
        this.btAll.setValue(bundle.getString("adv_search_btAll"));
        this.btAll.setImmediate(true);
        this.btAll.setStyleClass("inlineButton");
        this.btAll.addActionListener(this);
        this.lblChk.setId(CommonUtils.createUniqueId(this.lblChk));
        this.lblChk.setValue(bundle.getString("adv_search_lblChkType"));
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
        this.addPanelToCommonPanel( this.panel2 );
        
        // finally add the buttons
        super.addButtonsAndLogicOperatorToPanel();
    }
    
    public void processAction(ActionEvent event) 
    {
        if (event.getSource().equals(this.btAll))
        {
            if (checkAllCheckboxes( true ) )
            {
                this.setAllCheckboxes(false);
            }
            else
            {
                this.setAllCheckboxes(true);
            }
        }
    }
    
    private void createCheckBoxLabelUIs() 
    {
    	this.checkboxLabels.add( 
    			new DateCheckBoxLabelUI( DateType.ACCEPTED, DateUIMask.ACCEPTED_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new DateCheckBoxLabelUI( DateType.CREATED, DateUIMask.CREATED_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new DateCheckBoxLabelUI( DateType.MODIFIED, DateUIMask.MODIFIED_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new DateCheckBoxLabelUI( DateType.PUBLISHED_ONLINE, DateUIMask.PUBLISHEDON_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new DateCheckBoxLabelUI( DateType.PUBLISHED_PRINT, DateUIMask.PUBLISHEDPR_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add( 
    			new DateCheckBoxLabelUI( DateType.SUBMITTED, DateUIMask.SUBMITTED_BUNDLE_KEY, bundle ) );
    }
    
    /**
     * tests if all checkboxes are set or unset.
     * @param state state to be checked
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
     * @param select (boolean): true = select all, false unselect all.
     */
    private void setAllCheckboxes(boolean select)
    {           
    	for( int i = 0; i < this.checkboxLabels.size(); i++ )
     	{
     		this.checkboxLabels.get( i ).setSelected( select );
     	}
    }
    
    @Override
    void clearForm()
    {
        this.setAllCheckboxes( false );
    }

    @Override
    CriterionVO getCriterionFromArrays()
    {
        DateCriterionVO dateCriterionVO = new DateCriterionVO();
        dateCriterionVO.setFrom((String)this.getTxtSearchDateFrom().getText());
        dateCriterionVO.setTo((String)this.getTxtSearchDateTo().getText());
        
        dateCriterionVO.setDateType(this.getDateTypes());    
        return dateCriterionVO;
    }
    
    /**
     * returns a list of DateType according to the selected checkboxes of the date type in the mask.
     * @return (List<DateType>): list of date types.
     */
    private List<DateType> getDateTypes()
    {
    	ArrayList<DateType> list = new ArrayList<DateType>();
        for( int i = 0; i < this.checkboxLabels.size(); i++ )
    	{
    		if( this.checkboxLabels.get( i ).isSelected() == true )
    		{
    			list.add( this.checkboxLabels.get( i ).getDateType() );
    		}
    	}
        return list;
    }

    @Override
    boolean hasData()
    {
        if( this.checkAllCheckboxes( false ) ) 
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    public void refreshAppearance() 
    {
//    	 refresh the buttons
    	super.refreshAppearanceButtonsAndOp();
    	
    	this.lblSearchDateFrom.setValue(bundle.getString("adv_search_lblFrom"));
    	this.lblSearchDateTo.setValue(bundle.getString("adv_search_lblTo"));
    	this.btAll.setValue(bundle.getString("adv_search_btAll"));
    	this.lblChk.setValue(bundle.getString("adv_search_lblChkType"));
    	
    	for( int i = 0; i < this.checkboxLabels.size(); i++ )
    	{
    		this.checkboxLabels.get( i ).updateLanguage( bundle );
    	}
    }

    public TextField getTxtSearchDateFrom()
    {
        return txtSearchDateFrom;
    }

    public TextField getTxtSearchDateTo()
    {
        return txtSearchDateTo;
    }

    
}
