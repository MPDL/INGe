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

package de.mpg.escidoc.pubman.search;

import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import com.sun.rave.web.ui.component.DropDown;
import de.mpg.escidoc.pubman.search.ui.SearchTypeUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;

/**
 * Provides a set of search type query masks, which can be dynamically increased and combined 
 * by logical operators.
 * 
 * @author Hugo Niedermaier, endres
 * @version $Revision: 1687 $ $LastChangedDate: 2007-12-17 15:29:08 +0100 (Mon, 17 Dec 2007) $
 */
public class AdvancedSearchEdit extends SearchResultList
{
    public static final String BEAN_NAME = "advancedSearchEdit";
    /** faces navigation string */
    public static final String LOAD_SEARCHPAGE = "displaySearchPage";
    
    private Application application = FacesContext.getCurrentInstance().getApplication();
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);        
    private ResourceBundle bundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
   
    private HtmlCommandButton btClearSearch = new HtmlCommandButton();
    private HtmlCommandButton btStartSearch = new HtmlCommandButton();
    
    private DropDown cboLanguage = new DropDown(); 

    private HtmlPanelGroup panelCriteria = new HtmlPanelGroup();
    
    private ArrayList<SearchTypeUI> searchTypeList = new ArrayList<SearchTypeUI>();
    
   /**
    * Create a new instance. Set the buttons and the search type masks.
    *
    */ 
    public AdvancedSearchEdit()
    {
        // chnage the common search language   
        this.cboLanguage.setItems(CommonUtils.getLanguageOptions());
        
        // button for clear all forms
        this.btClearSearch.setId(CommonUtils.createUniqueId(this.btClearSearch));
        this.btClearSearch.setValue(bundle.getString("adv_search_btClearAll"));
        this.btClearSearch.setStyleClass("inlineButton");
        this.btClearSearch.setAction(this.application.createMethodBinding("#{advancedSearchEdit.clearAllForms}", null));
        
        // button for start search
        this.btStartSearch.setId(CommonUtils.createUniqueId(this.btStartSearch));
        this.btStartSearch.setValue(bundle.getString("adv_search_btStart"));
        this.btStartSearch.setStyleClass("inlineButton");
        this.btStartSearch.setAction(this.application.createMethodBinding("#{advancedSearchEdit.startSearch}", null));
        
        //add all the needed search types
        searchTypeList.add( new SearchTypeUI( SearchTypeUI.TypeOfMask.ANYFIELD, panelCriteria, false, true ) );
        searchTypeList.add( new SearchTypeUI( SearchTypeUI.TypeOfMask.PERSON, panelCriteria, false, true ) );
        searchTypeList.add( new SearchTypeUI( SearchTypeUI.TypeOfMask.ORGANIZATION, panelCriteria, false, true ) );
        searchTypeList.add( new SearchTypeUI( SearchTypeUI.TypeOfMask.GENRE, panelCriteria, false, true ) );
        searchTypeList.add( new SearchTypeUI( SearchTypeUI.TypeOfMask.DATE, panelCriteria, false, true ) );
        searchTypeList.add( new SearchTypeUI( SearchTypeUI.TypeOfMask.SOURCE, panelCriteria, true, true ) );
        searchTypeList.add( new SearchTypeUI( SearchTypeUI.TypeOfMask.EVENT, panelCriteria, true, true ) );
        searchTypeList.add( new SearchTypeUI( SearchTypeUI.TypeOfMask.IDENTIFIER, panelCriteria, true, false ) );          
    }
    
    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. 
     */
    public void init()
    {
    	super.init();
    	
    	// update the language 
    	this.updateLanguage();
    }

    /**
     * action handler to update the AnyFieldMask (Language Info and include Files enable/disable).
     */
    public void updateAnyFieldMask()
    {
        for( int i = 0; i < searchTypeList.size(); i++ ) 
        {
            SearchTypeUI searchType = searchTypeList.get( i );
            // only needed for anyfield masks
            if( searchType.getType().equals( SearchTypeUI.TypeOfMask.ANYFIELD ) )
            {
                searchType.refreshAppearance();
            }
        }
    }
    
    /**
     * Action handler to reset all forms.
     */
    public void clearAllForms()
    {        
        // reset the language field
        this.cboLanguage.setSelected(CommonUtils.getLanguageOptions()[0]);
        
        // reset all the forms in the search types
        for( int i = 0; i < searchTypeList.size(); i++ ) 
        {
            SearchTypeUI searchType = searchTypeList.get( i );
            searchType.clearForms();
        }
    }
  
    /**
     * Starts the advanced search.
     * iterates a TreeMap with all criterion masks with entered data and
     * fills a list with CriterionVO's to be passed to the PubItemSearching interface.
     * @return (String): identifying the page that should be navigated to after this methodcall. 
     */
    public String startSearch()
    {
        ArrayList<CriterionVO> criterionVOList = new ArrayList<CriterionVO>();
        
        for( int i = 0; i < searchTypeList.size(); i++ ) 
        {
            SearchTypeUI searchType = searchTypeList.get( i );
            ArrayList<CriterionVO> list = searchType.getCriterions();
            for( int u = 0; u < list.size(); u++ )
            {
                criterionVOList.add( list.get( u ) );
            }
        }
    
        //start the advanced search in the PubItemSearching interface
        SearchResultList list = (SearchResultList)getBean(SearchResultList.BEAN_NAME);
        
        if( cboLanguage.getSelected().toString().length() == 0 || ( ! cboLanguage.getSelected().toString().equalsIgnoreCase("de") 
                   && ! cboLanguage.getSelected().toString().equalsIgnoreCase("en" ) ) )
        {
            return list.startAdvancedSearch(criterionVOList, null );
        }
        else 
        {
            String lang = cboLanguage.getSelected().toString();
            return list.startAdvancedSearch(criterionVOList, lang );
        }
    }
    
    /**
     *  Updates the language resource bundle. 
     */
    private void updateLanguageBundle()
    {
    	this.application = FacesContext.getCurrentInstance().getApplication();
        this.i18nHelper = (InternationalizationHelper)application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);        
        this.bundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());     
    }
    
    /**
     * Update the language on the request page.
     *
     */
    public void updateLanguage()
    {
    	// update bundle
    	this.updateLanguageBundle();
    	
    	// update buttons
    	this.btClearSearch.setValue(bundle.getString("adv_search_btClearAll"));
        this.btStartSearch.setValue(bundle.getString("adv_search_btStart"));
    	
    	// update the masks
    	for( int i = 0; i < searchTypeList.size(); i++ ) 
        {
            searchTypeList.get( i ).refreshAppearance();
        }
    }
    
    public HtmlPanelGroup getPanelCriteria()
    {
        return panelCriteria;
    }

    public void setPanelCriteria(HtmlPanelGroup panelAnyField)
    {
        this.panelCriteria = panelAnyField;
    }

    public DropDown getCboLanguage()
    {
        return cboLanguage;
    }

    public void setCboLanguage(DropDown cboLanguage)
    {
        this.cboLanguage = cboLanguage;
    }

    public HtmlCommandButton getBtClearSearch()
    {
        return btClearSearch;
    }

    public void setBtClearSearch(HtmlCommandButton btClearSearch)
    {
        this.btClearSearch = btClearSearch;
    }

    public HtmlCommandButton getBtStartSearch()
    {
        return btStartSearch;
    }

    public void setBtStartSearch(HtmlCommandButton btStartSearch)
    {
        this.btStartSearch = btStartSearch;
    }
}
