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
import java.util.Locale;

import de.mpg.escidoc.pubman.search.bean.AnyFieldCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.DateCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.EventCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.GenreCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.IdentifierCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.OrganizationCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.PersonCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.SourceCriterionCollection;
import de.mpg.escidoc.pubman.search.bean.LanguageCriterionCollection;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;

/**
 * Provides a set of search type query masks, which can be dynamically increased and combined 
 * by logical operators.
 * 
 * @author Hugo Niedermaier, endres
 * @version $Revision: 1687 $ $LastChangedDate: 2007-12-17 15:29:08 +0100 (Mo, 17 Dez 2007) $
 */
public class AdvancedSearchEdit extends SearchResultList
{
    public static final String BEAN_NAME = "AdvancedSearchEdit";
    /** faces navigation string */
    public static final String LOAD_SEARCHPAGE = "displaySearchPage";
    
    private String languageString;

    // delegated internal collections
	private AnyFieldCriterionCollection anyFieldCriterionCollection;
    private PersonCriterionCollection personCriterionCollection;
    private OrganizationCriterionCollection organizationCriterionCollection;
    private GenreCriterionCollection genreCriterionCollection;
    private DateCriterionCollection dateCriterionCollection;
    private SourceCriterionCollection sourceCriterionCollection;
    private EventCriterionCollection eventCriterionCollection;
    private IdentifierCriterionCollection identifierCriterionCollection;
    private LanguageCriterionCollection languageCriterionCollection;
    
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

    /**
     * Action handler to reset all forms.
     */
    public String clearAllForms()
    {        
    	languageString = Locale.getDefault().getLanguage();
    	
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
        ArrayList<CriterionVO> criterionVOList = new ArrayList<CriterionVO>();
        
        // collect VO's from internal collections
        // we have to ensure, that no empty criterions are moved to the criterionVOList
        criterionVOList.addAll(anyFieldCriterionCollection.getFilledCriterionVO());
    	criterionVOList.addAll(personCriterionCollection.getFilledCriterionVO());
    	criterionVOList.addAll(organizationCriterionCollection.getFilledCriterionVO());
    	criterionVOList.addAll(genreCriterionCollection.getFilledCriterionVO());
       	criterionVOList.addAll(dateCriterionCollection.getFilledCriterionVO());
    	criterionVOList.addAll(sourceCriterionCollection.getFilledCriterionVO());
    	criterionVOList.addAll(eventCriterionCollection.getFilledCriterionVO());
    	criterionVOList.addAll(identifierCriterionCollection.getFilledCriterionVO());
    	criterionVOList.addAll(languageCriterionCollection.getFilledCriterionVO());
    
        //start the advanced search in the PubItemSearching interface
        SearchResultList list = (SearchResultList)getBean(SearchResultList.class);
        
        if (languageString == null || languageString.length() == 0 || 
        		(! languageString.equalsIgnoreCase("de") && ! languageString.equalsIgnoreCase("en" )))
        {
            return list.startAdvancedSearch(criterionVOList, null);
        }
        else 
        {
            return list.startAdvancedSearch(criterionVOList, languageString);
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
	
}
