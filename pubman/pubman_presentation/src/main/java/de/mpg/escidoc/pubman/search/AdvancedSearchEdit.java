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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.search;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.myfaces.trinidad.component.UIXIterator;

import de.mpg.escidoc.pubman.search.bean.AnyFieldCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.DateCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.EventCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.GenreCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.IdentifierCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.LanguageCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.OrganizationCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.PersonCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.SourceCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;

import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Provides a set of search type query masks, which can be dynamically increased and combined 
 * by logical operators.
 * 
 * @author Hugo Niedermaier, endres
 * @version $Revision: 1687 $ $LastChangedDate: 2007-12-17 15:29:08 +0100 (Mo, 17 Dez 2007) $
 */
public class AdvancedSearchEdit extends SearchResultList
{
    private static final String PROPERTY_CONTENT_MODEL = 
        "escidoc.framework_access.content-model.id.publication";
    
    public static final String BEAN_NAME = "AdvancedSearchEdit";
    /** faces navigation string */
    public static final String LOAD_SEARCHPAGE = "displaySearchPage";
    
    private String languageString;

    // delegated internal collections
	private AnyFieldCriterionCollection anyFieldCriterionCollection = null;
    private PersonCriterionCollection personCriterionCollection = null ;
    private OrganizationCriterionCollection organizationCriterionCollection = null;
    private GenreCriterionCollection genreCriterionCollection = null;
    private DateCriterionCollection dateCriterionCollection = null;
    private SourceCriterionCollection sourceCriterionCollection = null;
    private EventCriterionCollection eventCriterionCollection = null;
    private IdentifierCriterionCollection identifierCriterionCollection = null;
    private LanguageCriterionCollection languageCriterionCollection = null;
    
    private UIXIterator anyFieldCriterionIterator = new UIXIterator();
    private UIXIterator personCriterionIterator = new UIXIterator();
    private UIXIterator dateCriterionIterator = new UIXIterator();
    private UIXIterator genreCriterionIterator = new UIXIterator();
    private UIXIterator organizationCriterionIterator = new UIXIterator();
    private UIXIterator eventCriterionIterator = new UIXIterator();
    private UIXIterator sourceCriterionIterator = new UIXIterator();
    private UIXIterator identifierCriterionIterator = new UIXIterator();
    private UIXIterator languageCriterionIterator = new UIXIterator();
    
    private String suggestConeUrl = null;
   /**
    * Create a new instance. Set the buttons and the search type masks.
    *
    */ 
    public AdvancedSearchEdit()
    {
    	// delegated internal collections
    	anyFieldCriterionCollection = new AnyFieldCriterionCollection();
        personCriterionCollection = new PersonCriterionCollection();
        organizationCriterionCollection = new OrganizationCriterionCollection();
        genreCriterionCollection = new GenreCriterionCollection();
        dateCriterionCollection = new DateCriterionCollection();
        sourceCriterionCollection = new SourceCriterionCollection();
        eventCriterionCollection = new EventCriterionCollection();
        identifierCriterionCollection = new IdentifierCriterionCollection();
        languageCriterionCollection = new LanguageCriterionCollection();
        
        this.init();
        
    }
    
    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. 
     */
    public void init()
    {
    	super.init();
    }
    
    public void clearAndInitializeAllForms() {
    	anyFieldCriterionCollection = new AnyFieldCriterionCollection();
        personCriterionCollection = new PersonCriterionCollection();
        organizationCriterionCollection = new OrganizationCriterionCollection();
        genreCriterionCollection = new GenreCriterionCollection();
        dateCriterionCollection = new DateCriterionCollection();
        sourceCriterionCollection = new SourceCriterionCollection();
        eventCriterionCollection = new EventCriterionCollection();
        identifierCriterionCollection = new IdentifierCriterionCollection();
        languageCriterionCollection = new LanguageCriterionCollection();
    }

    /**
     * Action handler to reset all forms.
     */
    public String clearAllForms()
    {        
    	
    	// delegate clearAllForms to internal collections
    	anyFieldCriterionCollection.clearAllForms();
        personCriterionCollection.clearAllForms();
        organizationCriterionCollection.clearAllForms();
        genreCriterionCollection.clearAllForms();
        dateCriterionCollection.clearAllForms();
        sourceCriterionCollection.clearAllForms();
        eventCriterionCollection.clearAllForms();
        identifierCriterionCollection.clearAllForms();
        languageCriterionCollection.clearAllForms();
        return null;
	}
	
    /**
     * Starts the advanced search.
     * iterates a TreeMap with all criterion masks with entered data and
     * fills a list with CriterionVO's to be passed to the PubItemSearching interface.
     * @return (String): identifying the page that should be navigated to after this methodcall. 
     */
    public String startSearch()
    {
        ArrayList<Criterion> criterionList = new ArrayList<Criterion>();
        
        // collect VO's from internal collections
        // we have to ensure, that no empty criterions are moved to the criterionVOList
        criterionList.addAll( anyFieldCriterionCollection.getFilledCriterion() );
    	criterionList.addAll( personCriterionCollection.getFilledCriterion() );
    	criterionList.addAll( organizationCriterionCollection.getFilledCriterion() );
    	criterionList.addAll( genreCriterionCollection.getFilledCriterion() );
       	criterionList.addAll( dateCriterionCollection.getFilledCriterion() );
    	criterionList.addAll( sourceCriterionCollection.getFilledCriterion() );
    	criterionList.addAll( eventCriterionCollection.getFilledCriterion() );
    	criterionList.addAll( identifierCriterionCollection.getFilledCriterion() );
    	criterionList.addAll( languageCriterionCollection.getFilledCriterion() );
    	
    	 //start the advanced search in the PubItemSearching interface
        SearchResultList list = (SearchResultList)getBean(SearchResultList.class);
    	
    	ArrayList<MetadataSearchCriterion> searchCriteria = new ArrayList<MetadataSearchCriterion>();
    	
    	if( criterionList.size() == 0 ) {
    		error(getMessage("search_NoCriteria"));
    		return "";
    	}
    	
    	// transform the criteria to searchCriteria
    	try {
    		// transform first element
        	ArrayList<MetadataSearchCriterion> subset = transformToSearchCriteria( 
    				null, criterionList.get( 0 ) );
    		searchCriteria.addAll( subset );
    		for( int i = 1; i < criterionList.size(); i++ ) {
    			
    			ArrayList<MetadataSearchCriterion> sub = transformToSearchCriteria( 
    					criterionList.get( i - 1 ), criterionList.get( i ) );
    			searchCriteria.addAll( sub );	
    		}
    		
    		
    		
            //search only for items
            searchCriteria.add( new MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.OBJECT_TYPE, 
                    "item", MetadataSearchCriterion.LogicalOperator.AND ) );
            
            ArrayList<String> contentTypes = new ArrayList<String>();
            String contentTypeIdPublication = PropertyReader.getProperty( PROPERTY_CONTENT_MODEL );
            contentTypes.add( contentTypeIdPublication );
            
            MetadataSearchQuery query = new MetadataSearchQuery( contentTypes, searchCriteria );
            
            String cql = query.getCqlQuery();
            
            //redirect to SearchResultPage which processes the query
            getExternalContext().redirect("SearchResultListPage.jsp?"+SearchRetrieverRequestBean.parameterCqlQuery+"="+URLEncoder.encode(cql)+"&"+SearchRetrieverRequestBean.parameterSearchType+"=advanced");
    	}
    	catch(Exception e ) {
    		logger.error("Could not transform advanced search criteria", e);
    	}
    
    	
    	
    	
        return "";
    }
    
    private ArrayList<MetadataSearchCriterion> transformToSearchCriteria
    	( Criterion predecessor, Criterion transformMe ) throws TechnicalException {
    	
    	// we're on the first element of the criteria
    	if( predecessor == null ) {
    		ArrayList<MetadataSearchCriterion> results = transformMe.createSearchCriterion();
    		if( results.size() != 0 ) {
    			// set the first logicaloperator as unset as there is no predecessor
    			results.get( 0 ).setLogicalOperator( LogicalOperator.UNSET );
    		}
    		return results;
    		
    	}
    	else {
    		ArrayList<MetadataSearchCriterion> results = transformMe.createSearchCriterion();
    		if( results.size() != 0 ) {
    			LogicalOperator operator = predecessor.getLogicalOperator();
    			results.get( 0 ).setLogicalOperator( operator );
    		}
    		return results;
    	}
    }

    public PersonCriterionCollection getPersonCriterionCollection()
	{
		return personCriterionCollection;
	}

	public void setPersonCriterionCollection(PersonCriterionCollection personCriterionCollection)
	{
		this.personCriterionCollection = personCriterionCollection;
	}

	public GenreCriterionCollection getGenreCriterionCollection()
	{
		return genreCriterionCollection;
	}

	public void setGenreCriterionCollection(GenreCriterionCollection genreCriterionCollection)
	{
		this.genreCriterionCollection = genreCriterionCollection;
	}

	public DateCriterionCollection getDateCriterionCollection()
	{
		return dateCriterionCollection;
	}

	public void setDateCriterionCollection(DateCriterionCollection dateCriterionCollection)
	{
		this.dateCriterionCollection = dateCriterionCollection;
	}

	public AnyFieldCriterionCollection getAnyFieldCriterionCollection()
	{
		return anyFieldCriterionCollection;
	}

	public void setAnyFieldCriterionCollection(AnyFieldCriterionCollection anyFieldCriterionCollection)
	{
		this.anyFieldCriterionCollection = anyFieldCriterionCollection;
	}

	public EventCriterionCollection getEventCriterionCollection()
	{
		return eventCriterionCollection;
	}

	public void setEventCriterionCollection(EventCriterionCollection eventCriterionCollection)
	{
		this.eventCriterionCollection = eventCriterionCollection;
	}

	public IdentifierCriterionCollection getIdentifierCriterionCollection()
	{
		return identifierCriterionCollection;
	}

	public void setIdentifierCriterionCollection(IdentifierCriterionCollection identifierCriterionCollection)
	{
		this.identifierCriterionCollection = identifierCriterionCollection;
	}

	public OrganizationCriterionCollection getOrganizationCriterionCollection()
	{
		return organizationCriterionCollection;
	}

	public void setOrganizationCriterionCollection(OrganizationCriterionCollection organizationCriterionCollection)
	{
		this.organizationCriterionCollection = organizationCriterionCollection;
	}

	public SourceCriterionCollection getSourceCriterionCollection()
	{
		return sourceCriterionCollection;
	}

	public void setSourceCriterionCollection(SourceCriterionCollection sourceCriterionCollection)
	{
		this.sourceCriterionCollection = sourceCriterionCollection;
	}
	public LanguageCriterionCollection getLanguageCriterionCollection()
	{
		return languageCriterionCollection;
	}

	public void setLanguageCriterionCollection(LanguageCriterionCollection languageCriterionCollection)
	{
		this.languageCriterionCollection = languageCriterionCollection;
	}

    public UIXIterator getAnyFieldCriterionIterator()
    {
        return anyFieldCriterionIterator;
    }

    public void setAnyFieldCriterionIterator(UIXIterator anyFieldCriterionIterator)
    {
        this.anyFieldCriterionIterator = anyFieldCriterionIterator;
    }

    public UIXIterator getPersonCriterionIterator()
    {
        return personCriterionIterator;
    }

    public void setPersonCriterionIterator(UIXIterator personCriterionIterator)
    {
        this.personCriterionIterator = personCriterionIterator;
    }

    public UIXIterator getDateCriterionIterator()
    {
        return dateCriterionIterator;
    }

    public void setDateCriterionIterator(UIXIterator dateCriterionIterator)
    {
        this.dateCriterionIterator = dateCriterionIterator;
    }

    public UIXIterator getGenreCriterionIterator()
    {
        return genreCriterionIterator;
    }

    public void setGenreCriterionIterator(UIXIterator genreCriterionIterator)
    {
        this.genreCriterionIterator = genreCriterionIterator;
    }

    public UIXIterator getOrganizationCriterionIterator()
    {
        return organizationCriterionIterator;
    }

    public void setOrganizationCriterionIterator(UIXIterator organizationCriterionIterator)
    {
        this.organizationCriterionIterator = organizationCriterionIterator;
    }

    public UIXIterator getEventCriterionIterator()
    {
        return eventCriterionIterator;
    }

    public void setEventCriterionIterator(UIXIterator eventCriterionIterator)
    {
        this.eventCriterionIterator = eventCriterionIterator;
    }

    public UIXIterator getSourceCriterionIterator()
    {
        return sourceCriterionIterator;
    }

    public void setSourceCriterionIterator(UIXIterator sourceCriterionIterator)
    {
        this.sourceCriterionIterator = sourceCriterionIterator;
    }

    public UIXIterator getIdentifierCriterionIterator()
    {
        return identifierCriterionIterator;
    }

    public void setIdentifierCriterionIterator(UIXIterator identifierCriterionIterator)
    {
        this.identifierCriterionIterator = identifierCriterionIterator;
    }

    public UIXIterator getLanguageCriterionIterator()
    {
        return languageCriterionIterator;
    }

    public void setLanguageCriterionIterator(UIXIterator languageCriterionIterator)
    {
        this.languageCriterionIterator = languageCriterionIterator;
    }

    public String getSuggestConeUrl() throws Exception
    {
        if (suggestConeUrl == null)
        {
            suggestConeUrl = PropertyReader.getProperty("escidoc.cone.service.url");
        }
        return suggestConeUrl;
    }

    public void setSuggestConeUrl(String suggestConeUrl)
    {
        this.suggestConeUrl = suggestConeUrl;
    }
}
