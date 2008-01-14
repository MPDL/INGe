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
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import de.mpg.escidoc.pubman.ui.CollapsiblePanelUI;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;

/**
 * Provides a search type which can include one to many search query masks which can be combined 
 * by logical operators.
 * 
 * @author endres
 * @version $Revision: 1639 $ $LastChangedDate: 2007-12-04 15:06:47 +0100 (Tue, 04 Dec 2007) $
 */
public class SearchTypeUI extends CollapsiblePanelUI implements ActionListener
{
    /** the type of search query masks*/
    public static enum TypeOfMask
    {
        /** Title, topic and anyfield search query mask */
        ANYFIELD,
        /** Person search query mask */
        PERSON,
        /** Organization search query mask*/
        ORGANIZATION,
        /** Genre search query mask*/
        GENRE,
        /** Date search query mask*/
        DATE,
        /** Source search query mask*/
        SOURCE,
        /** Event search query mask*/
        EVENT,
        /** Identifier search query mask*/
        IDENTIFIER
    }
    
    private Application application = FacesContext.getCurrentInstance().getApplication();
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);        
    private ResourceBundle bundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    
    /** typoe of the masks */
    private TypeOfMask typeOfMask;
    /** panel that groups all the masks */
    private HtmlPanelGroup panel = new HtmlPanelGroup();
    /** panel that groups the title */
    private HtmlPanelGroup panelTitle = new HtmlPanelGroup();
    /** text header of the search query type */
    private HtmlOutputText textTitle = new HtmlOutputText();
    
    /** array of the masks */
    private ArrayList<UIMask> maskList = new ArrayList<UIMask>();
    
    /** logical operator at the end of a SearchType group */
    private LogicOperatorUI logicOperator = new LogicOperatorUI( bundle );
    
    /** search type has a logical operator at the end */
    boolean useLogicalOperator;
    
    /** the panel form outside to connect to */
    HtmlPanelGroup outsidePanel; 
    
    /**
     * Creates a new instance.
     * 
     * @param type type of mask to be used
     * @param outsidePanel outside panel to attach
     * @param startingMasks how many starting masks at beginning
     * @param useOp shall logic operator be used
     */
    public SearchTypeUI( TypeOfMask type, HtmlPanelGroup outsideP, boolean toggleVis, boolean useOp ) 
    {
        super();
        
        // this direct access has to be done to get the collapsible buttons inside the title box
        this.panTitleBar.getChildren().add(0, this.htmlElementUI.getStartTagWithStyleClass("div", "listHeader dark"));
        this.panTitleBar.getChildren().add(htmlElementUI.getEndTag("div"));  
        
        this.outsidePanel = outsideP;
        this.useLogicalOperator = useOp;
        this.typeOfMask = type;
        this.textTitle.setValue( this.getIdentifierByEnum(typeOfMask) );
        
        // set the titel 
        this.setTitle();
        
        // add one initial panel       
        addMaskToPanel( 0 );
               
        // add all the search masks to the container
        this.addToContainer(this.panel);
        
        
        // add the logicOperator at the end of the panel
        if( useLogicalOperator == true ) 
        {
            outsidePanel.getChildren().add(logicOperator.getUIComponent());
        }
        
        // toggleVisability
        if( toggleVis == true ) { 
            this.toggleContainerVisibility();
        }
    }
    
    /** 
     * Set the title of a search type.
     *
     */
    private void setTitle() 
    {
        HTMLElementUI htmlElement = new HTMLElementUI();
        
        this.panelTitle.setId(CommonUtils.createUniqueId(this.panelTitle));
        this.panelTitle.setId(CommonUtils.createUniqueId(this.textTitle));
        this.panelTitle.getChildren().add(htmlElement.getStartTag("h3"));
        this.panelTitle.getChildren().add(htmlElement.getStartTag("label"));
        this.panelTitle.getChildren().add(this.textTitle);
        this.panelTitle.getChildren().add(htmlElement.getEndTag("label"));
        this.panelTitle.getChildren().add(htmlElement.getEndTag("h3"));
        this.setTitelComponent(panelTitle);
                  
          // add the instance to the outside panel
        this.outsidePanel.getChildren().add(this);     
    }
        
    /**
     * Clear all forms.
     *
     */
    public void clearForms() 
    {
        for( int i = 0; i < maskList.size(); i++ ) 
        {
            maskList.get(i).clearForm();
        }
    }
    
    /** 
     * Add another mask to the search type.
     * @param position position to insert the new mask
     */
    private void addMaskToPanel( int position )
    {
       UIMask newMask = this.getMaskByEnum(typeOfMask);
       this.maskList.add(position, newMask);
       this.panel.getChildren().add(position, newMask.getMaskPanel() );
       
       // hide unnecessary elements
       this.updateVisabilityDeleteButton();
       this.updateVisabilityLogicOperator();
    }
    
    /**
     * Remove a mask from the search type.
     * @param position position to remove the mask
     */
    private void removeMaskFromPanel( int position )
    {     
        this.maskList.remove( position );
        this.panel.getChildren().remove(position);
        
        // hide unnecessary elements
        this.updateVisabilityDeleteButton();
        this.updateVisabilityLogicOperator();
    }
    
    /** 
     * Returns a list of criterions from the search masks. An empty mask will be ignored.
     * @return search criterions from the masks
     */
    public ArrayList<CriterionVO> getCriterions()
    {
        ArrayList<CriterionVO> criterions = new ArrayList<CriterionVO>();
        for( int i = 0; i < this.maskList.size(); i++ ) 
        {
            UIMask mask = this.maskList.get( i );
            
            //check if the mask has entered data
            if( mask.hasData() == true )
            {
                // check if we reached the last element
                if( i == ( this.maskList.size() - 1 ) ) 
                {
                    // last element, use the logicoperator from here (searchtype)
                    CriterionVO crit = mask.getCriterion( false );
                
                    // check if logicoperator is used in this searchtype
                    if( useLogicalOperator == true )
                    {
                        crit.setLogicOperator(
                                LogicOperatorUI.getLogicOperatorByString((this.getLogicOperator().
                                        getCboLogicOperator().getSelected().toString())));
                    }
                    criterions.add( crit );
                }
                // not the last element, use the logicOperator from the mask
                else 
                {
                    criterions.add( mask.getCriterion( true ) );   
                }
            }
        }
        return criterions;
    }
    
    /**
     * Returns a new mask instance with a given type
     * @param type type of mask 
     * @return new mask instance
     */
    private UIMask getMaskByEnum( TypeOfMask type  )
    {
        switch( type ) 
        {
            case ANYFIELD:
                return new AnyFieldUIMask(this);
            case PERSON:
                return new PersonUIMask(this);
            case ORGANIZATION:
                return new GenericUIMask(GenericUIMask.Type.ORGANIZATION, this);
            case GENRE:
                return new GenreUIMask( this );   
            case DATE:
                return new DateUIMask( this );
            case SOURCE:
                return new GenericUIMask(GenericUIMask.Type.SOURCE, this);
            case EVENT:
                return new GenericUIMask(GenericUIMask.Type.EVENT, this);
            case IDENTIFIER:
                return new GenericUIMask(GenericUIMask.Type.IDENTIFIER, this);
            default:
                // TODO: endres substitute with an exception
                return new AnyFieldUIMask(this);
        }
    }
    
    /**
     * Returns a internationalized identifier for a mask by type  
     * @param type type of mask
     * @return internationalized identifier
     */
    private String getIdentifierByEnum( TypeOfMask type )
    {
        switch( type ) 
        {
            case ANYFIELD:
                return this.bundle.getString("adv_search_lbHeaderWoP");
            case PERSON:
                return this.bundle.getString("adv_search_lbHeaderPerson");
            case ORGANIZATION:
                return this.bundle.getString("adv_search_lbHeaderOrgan");
            case GENRE:
                return this.bundle.getString("adv_search_lbHeaderGenre");    
            case DATE:
                return this.bundle.getString("adv_search_lbHeaderDate");
            case SOURCE:
                return this.bundle.getString("adv_search_lbHeaderSource");
            case EVENT:
                return this.bundle.getString("adv_search_lbHeaderEvent");
            case IDENTIFIER:
                return this.bundle.getString("adv_search_lbHeaderIdent");
            default:
                return "UNKNOWN";
        }
    }
    
    /** 
     * Updates the visability of the logic operators in the mask. All of the masks
     * need a logic operator except the last one. 
     *
     */
    private void updateVisabilityLogicOperator() 
    {
        for( int i = 0; i < maskList.size(); i++ ) 
        {
            UIMask m = maskList.get(i);
            // last element
            if( i == maskList.size() - 1 )
            {
                m.showLogicalOperator( false );
            }
            // not last element
            else 
            {
                m.showLogicalOperator( true );
            }
        }     
    }
    
    /**
     * Updates the visability of the 'delete form' button. If there's just one form
     * the button is not needed. Otherwise all buttons are displayed.
     *
     */
    private void updateVisabilityDeleteButton() 
    {
        if( maskList.size() == 1 ) 
        {
            UIMask m = maskList.get( 0 );
            m.showDeleteFormButton( false );
        }
        else 
        {
            for (int i = 0; i < maskList.size(); i++)
            {
                UIMask m = maskList.get(i);
                    m.showDeleteFormButton(true);
            }
        }
    }
    
    /**
     * Refresh the apprearance of the masks. Some masks have to rerender their
     * language. Blind out some fields etc.
     *
     */
    public void refreshAppearance()
    {
    	// update the language bundle
    	this.updateLanguageBundle();
    	
    	// update header
    	this.textTitle.setValue( this.getIdentifierByEnum(typeOfMask) );
    	
    	// update logicOperator
    	this.logicOperator.updateLanguage( bundle );
    	
    	// refresh the masks
        for (int i = 0; i < maskList.size(); i++)
        {
            UIMask m = maskList.get(i);
                m.refreshAppearance();
        }
    }
    
    /**
     * Updates the language of the bundle.
     *
     */
    private void updateLanguageBundle()
    {
    	this.application = FacesContext.getCurrentInstance().getApplication();
        this.i18nHelper = (InternationalizationHelper)application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);        
        this.bundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    }

    /** 
     * Returns the logic operator
     * 
     * @return logic operator
     */
    public LogicOperatorUI getLogicOperator()
    {
        return logicOperator;
    }
    
    /**
     * Returns the type of the search type
     * @return
     */
    public TypeOfMask getType() 
    {
        return typeOfMask;
    }
    
    @Override
    /**
     *  Process the action if a button of a search type mask is pressed. 
     *  This includes the 'Add form', 'Delete from' and the 'Clear form' button.
     */
    public void processAction(ActionEvent event)
    {
        super.processAction( event );
        
        for( int i = 0; i < maskList.size(); i++ ) 
        {
            UIMask mask = maskList.get( i );
            if( event.getSource().equals( mask.getButtonDelete() ) )
            {
                this.removeMaskFromPanel( i );
            }
            else if( event.getSource().equals( mask.getButtonAdd() ) )
            {
                this.addMaskToPanel( i + 1 );
            }
            else if( event.getSource().equals( mask.getButtonClearForm() ) )
            {
                mask.clearForm();
            }
            else 
            {
               
            }
        }
    }
}
