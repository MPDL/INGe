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

import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.GenreCriterionVO;

/**
* This mask collects search data for a given genre.
* @author endres
* @version $Revision: 1639 $ $LastChangedDate: 2007-12-04 15:06:47 +0100 (Tue, 04 Dec 2007) $
 * TODO: endres: redesign would be well advised
 */
public class GenreUIMask extends UIMask implements ActionListener
{
    
    private HTMLElementUI htmlElement = new HTMLElementUI();
    
    private HtmlPanelGroup panel1 = new HtmlPanelGroup();
    private HtmlPanelGroup panel2 = new HtmlPanelGroup();
    private HtmlPanelGroup panel3 = new HtmlPanelGroup();
    private Label lblChk = new Label();
   
    private HtmlCommandButton btAll = new HtmlCommandButton();
    
    ArrayList<GenreCheckBoxLabelUI> checkboxLabels = new ArrayList<GenreCheckBoxLabelUI>();
    
    private static final String ARTICLE_BUNDLE_KEY = "adv_search_lblChkGenre_article";
    private static final String BOOK_BUNDLE_KEY = "adv_search_lblChkGenre_book";
    private static final String BOOKITEM_BUNDLE_KEY = "adv_search_lblChkGenre_bookitem";
    private static final String COURSEWARE_BUNDLE_KEY = "adv_search_lblChkGenre_courseware";
    private static final String CONFERENCE_PAPER_BUNDLE_KEY = "adv_search_lblChkGenre_conferencepap";
    private static final String CONFERENCE_REPORT_BUNDLE_KEY = "adv_search_lblChkGenre_conferencerep";
    private static final String ISSUE_BUNDLE_KEY = "adv_search_lblChkGenre_issue";
    private static final String JOURNAL_BUNDLE_KEY = "adv_search_lblChkGenre_journal";
    private static final String MANUSCRIPT_BUNDLE_KEY = "adv_search_lblChkGenre_manuscript";
    private static final String PAPER_BUNDLE_KEY = "adv_search_lblChkGenre_paper";
    private static final String POSTER_BUNDLE_KEY = "adv_search_lblChkGenre_poster";
    private static final String PROCEEDINGS_BUNDLE_KEY = "adv_search_lblChkGenre_proceedings";
    private static final String REPORT_BUNDLE_KEY = "adv_search_lblChkGenre_report";
    private static final String SERIES_BUNDLE_KEY = "adv_search_lblChkGenre_series";
    private static final String TALKATEVENT_BUNDLE_KEY = "adv_search_lblChkGenre_talkatevent";
    private static final String THESIS_BUNDLE_KEY = "adv_search_lblChkGenre_thesis";
    private static final String OTHER_BUNDLE_KEY = "adv_search_lblChkGenre_other";
    /**
     * Implements checkboxes with all types of genres.
     * @param st search type reference
     */
    public GenreUIMask( SearchTypeUI st )
    {
        super( st );
        
        this.panel1.setId(CommonUtils.createUniqueId(this.panel1));
        this.addPanelToCommonPanel(panel1);
        
        this.panel3.setId(CommonUtils.createUniqueId(this.panel3));
//      TODO endres: change this to markAll div
        this.panel3.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "clearButton"));
        this.btAll.setId(CommonUtils.createUniqueId(this.btAll));
        this.btAll.setValue(bundle.getString("adv_search_btAll"));
        this.btAll.setImmediate(true);
        this.btAll.setStyleClass("inlineButton");
        this.btAll.addActionListener(this);
        this.lblChk.setId(CommonUtils.createUniqueId(this.lblChk));
        this.lblChk.setValue(bundle.getString("adv_search_lblChkGenre"));           
        this.panel3.getChildren().add(this.lblChk);
        this.panel3.getChildren().add(this.btAll);
        this.panel3.getChildren().add(htmlElement.getEndTag("div"));
        this.addPanelToCommonPanel(this.panel3);
        
        this.panel2.setId(CommonUtils.createUniqueId(this.panel2));
        this.panel2.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "formGroupCheckboxes"));
        
        this.createCheckBoxLabelUIs();
        for( int i = 0; i < this.checkboxLabels.size(); i++ )
        {
        	this.checkboxLabels.get( i ).addToPanel( panel2 );
        }
        
        this.panel2.getChildren().add(htmlElement.getEndTag("div"));
        this.addPanelToCommonPanel(this.panel2);
        
        // finally add the buttons
        super.addButtonsAndLogicOperatorToPanel();
    }
    
    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.search.ui.UIMask#clearForm()
     */
    @Override
    void clearForm()
    {
        this.selectAllCheckboxes(false);
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.search.ui.UIMask#getCriterionFromArrays()
     */
    @Override
    CriterionVO getCriterionFromArrays()
    {
        GenreCriterionVO genreCriterionVO = new GenreCriterionVO();
        genreCriterionVO.setGenre(this.getGenres());
        return genreCriterionVO;
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.search.ui.UIMask#hasData()
     */
    @Override
    boolean hasData()
    {
        if ( this.verifyAllCheckboxes( false ) == true )
        {
            return false;
        }
        else
        {
            return true;
        }
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
    
    private ArrayList<Genre> getGenres()
    {
        ArrayList<Genre> list = new ArrayList<Genre>();
        
        for( int i = 0; i < this.checkboxLabels.size(); i++ )
    	{
    		if( this.checkboxLabels.get( i ).isSelected() == true )
    		{
    			list.add( this.checkboxLabels.get( i ).getGenre() );
    		}
    	}
        return list;
    }
    
    private boolean verifyAllCheckboxes( boolean status )
    {
    	
    	for( int i = 0; i < this.checkboxLabels.size(); i++ )
    	{
    		if( this.checkboxLabels.get( i ).isSelected() == !status )
    		{
    			return false;
    		}
    	}
    	return true;
    }
    
    private void createCheckBoxLabelUIs()
    {
    	this.checkboxLabels.add ( 
    		new GenreCheckBoxLabelUI( Genre.ARTICLE, GenreUIMask.ARTICLE_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.BOOK, GenreUIMask.BOOK_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.BOOK_ITEM, GenreUIMask.BOOKITEM_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.COURSEWARE_LECTURE, GenreUIMask.COURSEWARE_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.CONFERENCE_PAPER, GenreUIMask.CONFERENCE_PAPER_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.CONFERENCE_REPORT, GenreUIMask.CONFERENCE_REPORT_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.ISSUE, GenreUIMask.ISSUE_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.JOURNAL, GenreUIMask.JOURNAL_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.MANUSCRIPT, GenreUIMask.MANUSCRIPT_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.PAPER, GenreUIMask.PAPER_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.POSTER, GenreUIMask.POSTER_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.PROCEEDINGS, GenreUIMask.PROCEEDINGS_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.REPORT, GenreUIMask.REPORT_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.SERIES, GenreUIMask.SERIES_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.TALK_AT_EVENT, GenreUIMask.TALKATEVENT_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.THESIS, GenreUIMask.THESIS_BUNDLE_KEY, bundle ) );
    	this.checkboxLabels.add ( 
        		new GenreCheckBoxLabelUI( Genre.OTHER, GenreUIMask.OTHER_BUNDLE_KEY, bundle ) );
    }
    
    @Override
    public void refreshAppearance() 
    {
        // refresh the buttons and operator
    	super.refreshAppearanceButtonsAndOp();
    	
    	this.lblChk.setValue(bundle.getString("adv_search_lblChkGenre")); 
    	this.btAll.setValue(bundle.getString("adv_search_btAll"));
    	
    	for( int i = 0; i < this.checkboxLabels.size(); i++ )
    	{
    		this.checkboxLabels.get( i ).updateLanguage( bundle );
    	}
    }
    
    public void processAction(ActionEvent event) 
    {
        if (event.getSource().equals(this.btAll))
        {
            if (verifyAllCheckboxes( true ) )
            {
                this.selectAllCheckboxes(false);
            }
            else
            {
                this.selectAllCheckboxes(true);
            }
        }
    }
}
