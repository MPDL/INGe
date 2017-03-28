/*
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web.search;

import java.net.URLEncoder;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.search.bean.AnyFieldCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.ContextCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.DateCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.DegreeCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.EventCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.FileCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.GenreCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.IdentifierCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.LanguageCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.LocalTagCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.OrganizationCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.PersonCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.SourceCriterionCollection;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.ObjectCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.LogicalOperator;
import de.mpg.mpdl.inge.search.query.MetadataSearchQuery;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Provides a set of search type query masks, which can be dynamically increased and combined by
 * logical operators.
 * 
 * @author Hugo Niedermaier, endres
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "AdvancedSearchEdit")
@SessionScoped
@SuppressWarnings("serial")
public class AdvancedSearchEdit extends FacesBean {
  private static final Logger logger = Logger.getLogger(AdvancedSearchEdit.class);

  private static final String PROPERTY_CONTENT_MODEL =
      "escidoc.framework_access.content-model.id.publication";

  public static final String LOAD_SEARCHPAGE = "displaySearchPage";

  // delegated internal collections
  private ContextCriterionCollection contextCriterionCollection = null;
  private AnyFieldCriterionCollection anyFieldCriterionCollection = null;
  private PersonCriterionCollection personCriterionCollection = null;
  private OrganizationCriterionCollection organizationCriterionCollection = null;
  private GenreCriterionCollection genreCriterionCollection = null;
  private DegreeCriterionCollection degreeCriterionCollection = null;
  private DateCriterionCollection dateCriterionCollection = null;
  private SourceCriterionCollection sourceCriterionCollection = null;
  private EventCriterionCollection eventCriterionCollection = null;
  private IdentifierCriterionCollection identifierCriterionCollection = null;
  private FileCriterionCollection fileCriterionCollection = null;
  private LanguageCriterionCollection languageCriterionCollection = null;
  private LocalTagCriterionCollection localTagCriterionCollection = null;



  private String suggestConeUrl = null;



  /**
   * Create a new instance. Set the buttons and the search type masks.
   * 
   */
  public AdvancedSearchEdit() {
    // delegated internal collections
    contextCriterionCollection = new ContextCriterionCollection();
    anyFieldCriterionCollection = new AnyFieldCriterionCollection();
    personCriterionCollection = new PersonCriterionCollection();
    organizationCriterionCollection = new OrganizationCriterionCollection();
    genreCriterionCollection = new GenreCriterionCollection();
    degreeCriterionCollection = new DegreeCriterionCollection();
    dateCriterionCollection = new DateCriterionCollection();
    sourceCriterionCollection = new SourceCriterionCollection();
    eventCriterionCollection = new EventCriterionCollection();
    identifierCriterionCollection = new IdentifierCriterionCollection();
    fileCriterionCollection = new FileCriterionCollection();
    languageCriterionCollection = new LanguageCriterionCollection();
    localTagCriterionCollection = new LocalTagCriterionCollection();
  }

  public void clearAndInitializeAllForms() {
    contextCriterionCollection = new ContextCriterionCollection();
    anyFieldCriterionCollection = new AnyFieldCriterionCollection();
    personCriterionCollection = new PersonCriterionCollection();
    organizationCriterionCollection = new OrganizationCriterionCollection();
    genreCriterionCollection = new GenreCriterionCollection();
    degreeCriterionCollection = new DegreeCriterionCollection();
    dateCriterionCollection = new DateCriterionCollection();
    sourceCriterionCollection = new SourceCriterionCollection();
    eventCriterionCollection = new EventCriterionCollection();
    identifierCriterionCollection = new IdentifierCriterionCollection();
    fileCriterionCollection = new FileCriterionCollection();
    languageCriterionCollection = new LanguageCriterionCollection();
    localTagCriterionCollection = new LocalTagCriterionCollection();
  }

  /**
   * Action handler to reset all forms.
   */
  public void clearAllForms() {
    // delegate clearAllForms to internal collections
    contextCriterionCollection.clearAllForms();
    anyFieldCriterionCollection.clearAllForms();
    personCriterionCollection.clearAllForms();
    organizationCriterionCollection.clearAllForms();
    genreCriterionCollection.clearAllForms();
    degreeCriterionCollection.clearAllForms();
    dateCriterionCollection.clearAllForms();
    sourceCriterionCollection.clearAllForms();
    eventCriterionCollection.clearAllForms();
    identifierCriterionCollection.clearAllForms();
    fileCriterionCollection.clearAllForms();
    languageCriterionCollection.clearAllForms();
    localTagCriterionCollection.clearAllForms();
  }

  /**
   * Starts the advanced search. iterates a TreeMap with all criterion masks with entered data and
   * fills a list with CriterionVO's to be passed to the PubItemSearching interface.
   * 
   * @return (String): identifying the page that should be navigated to after this method call.
   */
  public void startSearch() {
    ArrayList<Criterion> criterionList = new ArrayList<Criterion>();
    criterionList.addAll(anyFieldCriterionCollection.getFilledCriterion());
    criterionList.addAll(contextCriterionCollection.getFilledCriterion());
    criterionList.addAll(personCriterionCollection.getFilledCriterion());
    criterionList.addAll(dateCriterionCollection.getFilledCriterion());
    criterionList.addAll(genreCriterionCollection.getFilledCriterion());
    criterionList.addAll(degreeCriterionCollection.getFilledCriterion());
    criterionList.addAll(organizationCriterionCollection.getFilledCriterion());
    criterionList.addAll(fileCriterionCollection.getFilledCriterion());
    criterionList.addAll(identifierCriterionCollection.getFilledCriterion());
    criterionList.addAll(eventCriterionCollection.getFilledCriterion());
    criterionList.addAll(sourceCriterionCollection.getFilledCriterion());
    criterionList.addAll(languageCriterionCollection.getFilledCriterion());
    criterionList.addAll(localTagCriterionCollection.getFilledCriterion());

    ArrayList<MetadataSearchCriterion> searchCriteria = new ArrayList<MetadataSearchCriterion>();

    if (criterionList.size() == 0) {
      error(getMessage("search_NoCriteria"));
      return;
    }

    // add the default criterion to the top of the list
    criterionList.add(0, new ObjectCriterion());

    // transform the criteria to searchCriteria
    try {
      // transform first element
      ArrayList<MetadataSearchCriterion> subset =
          transformToSearchCriteria(null, criterionList.get(0));
      searchCriteria.addAll(subset);
      ArrayList<MetadataSearchCriterion> currentList = searchCriteria;
      for (int i = 1; i < criterionList.size(); i++) {

        ArrayList<MetadataSearchCriterion> newCriteria =
            transformToSearchCriteria(criterionList.get(i - 1), criterionList.get(i));

        Class<? extends Criterion> c = criterionList.get(i).getClass();
        Class<? extends Criterion> d = criterionList.get(i - 1).getClass();
        if (c.equals(d)) {
          currentList.addAll(newCriteria);
        } else {
          currentList.get(currentList.size() - 1).addSubCriteria(newCriteria);
          currentList = currentList.get(currentList.size() - 1).getSubCriteriaList();
        }
      }

      // add the contentType to the query
      ArrayList<String> contentTypes = new ArrayList<String>();
      String contentTypeIdPublication = PropertyReader.getProperty(PROPERTY_CONTENT_MODEL);
      contentTypes.add(contentTypeIdPublication);

      MetadataSearchQuery query = new MetadataSearchQuery(contentTypes, searchCriteria);

      String cql = query.getCqlQuery();

      try {
        String searchString = "";

        for (Criterion c : anyFieldCriterionCollection.getFilledCriterion()) {
          searchString += c.getSearchString() + " ";
        }

        for (Criterion c : personCriterionCollection.getFilledCriterion()) {
          searchString += c.getSearchString() + " ";
        }

        // TODO:What is this doing? Added the >1 test cause it throws exception if no search string
        // was enteres. e.g. search for genre
        if (searchString.length() > 1) {
          searchString = searchString.substring(0, searchString.length() - 1);
        }

        // log search for statistics
        /*
         * LoginHelper loginHelper = (LoginHelper)FacesTools.findBean(LoginHelper.class);
         * InitialContext ic = new InitialContext(); StatisticLogger sl = (StatisticLogger)
         * ic.lookup(StatisticLogger.SERVICE_NAME); sl.logSearch(getSessionId(), getIP(),
         * searchString, cql, loginHelper.getLoggedIn(), "pubman",
         * AdminHelper.getAdminUserHandle());
         */
      } catch (Exception e) {
        logger.error("Could not log statistical data", e);
      }

      // redirect to SearchResultPage which processes the query
      FacesTools.getExternalContext().redirect(
          "SearchResultListPage.jsp?" + SearchRetrieverRequestBean.parameterCqlQuery + "="
              + URLEncoder.encode(cql, "UTF-8") + "&"
              + SearchRetrieverRequestBean.parameterSearchType + "=advanced");

    } catch (de.mpg.mpdl.inge.search.parser.ParseException e) {
      logger.error("Search criteria includes some lexical error", e);
      error(getMessage("search_ParseError"));
      return;
    } catch (Exception e) {
      logger.error("Technical problem while retrieving the search results", e);
      error(getMessage("search_TechnicalError"));
      return;
    }
  }

  private ArrayList<MetadataSearchCriterion> transformToSearchCriteria(Criterion predecessor,
      Criterion transformMe) throws TechnicalException {
    ArrayList<MetadataSearchCriterion> results = transformMe.createSearchCriterion();
    // we're on the first element of the criteria
    if (predecessor == null) {
      if (results.size() != 0) {
        // set the first logical operator to unset
        results.get(0).setLogicalOperator(LogicalOperator.UNSET);
      }
      return results;
    } else {
      if (results.size() != 0) {
        LogicalOperator operator = predecessor.getLogicalOperator();
        results.get(0).setLogicalOperator(operator);

        Class<? extends Criterion> c = predecessor.getClass();
        Class<? extends Criterion> d = transformMe.getClass();

        if (c.equals(d) && c.getName().contains("OrganizationCriterion")) // hack TODO
        {
          results.get(0).setLogicalOperator(LogicalOperator.OR);
        }
      }
      return results;
    }
  }

  public PersonCriterionCollection getPersonCriterionCollection() {
    return personCriterionCollection;
  }

  public void setPersonCriterionCollection(PersonCriterionCollection personCriterionCollection) {
    this.personCriterionCollection = personCriterionCollection;
  }

  public GenreCriterionCollection getGenreCriterionCollection() {
    return genreCriterionCollection;
  }

  public void setGenreCriterionCollection(GenreCriterionCollection genreCriterionCollection) {
    this.genreCriterionCollection = genreCriterionCollection;
  }

  public DateCriterionCollection getDateCriterionCollection() {
    return dateCriterionCollection;
  }

  public void setDateCriterionCollection(DateCriterionCollection dateCriterionCollection) {
    this.dateCriterionCollection = dateCriterionCollection;
  }

  public ContextCriterionCollection getContextCriterionCollection() {
    return contextCriterionCollection;
  }

  public void setContextCriterionCollection(ContextCriterionCollection contextCriterionCollection) {
    this.contextCriterionCollection = contextCriterionCollection;
  }

  public AnyFieldCriterionCollection getAnyFieldCriterionCollection() {
    return anyFieldCriterionCollection;
  }

  public void setAnyFieldCriterionCollection(AnyFieldCriterionCollection anyFieldCriterionCollection) {
    this.anyFieldCriterionCollection = anyFieldCriterionCollection;
  }



  public EventCriterionCollection getEventCriterionCollection() {
    return eventCriterionCollection;
  }

  public void setEventCriterionCollection(EventCriterionCollection eventCriterionCollection) {
    this.eventCriterionCollection = eventCriterionCollection;
  }

  public IdentifierCriterionCollection getIdentifierCriterionCollection() {
    return identifierCriterionCollection;
  }

  public void setIdentifierCriterionCollection(
      IdentifierCriterionCollection identifierCriterionCollection) {
    this.identifierCriterionCollection = identifierCriterionCollection;
  }

  public OrganizationCriterionCollection getOrganizationCriterionCollection() {
    return organizationCriterionCollection;
  }

  public void setOrganizationCriterionCollection(
      OrganizationCriterionCollection organizationCriterionCollection) {
    this.organizationCriterionCollection = organizationCriterionCollection;
  }

  public SourceCriterionCollection getSourceCriterionCollection() {
    return sourceCriterionCollection;
  }

  public void setSourceCriterionCollection(SourceCriterionCollection sourceCriterionCollection) {
    this.sourceCriterionCollection = sourceCriterionCollection;
  }

  public LanguageCriterionCollection getLanguageCriterionCollection() {
    return languageCriterionCollection;
  }

  public void setLanguageCriterionCollection(LanguageCriterionCollection languageCriterionCollection) {
    this.languageCriterionCollection = languageCriterionCollection;
  }


  public String getSuggestConeUrl() throws Exception {
    if (suggestConeUrl == null) {
      suggestConeUrl = PropertyReader.getProperty("escidoc.cone.service.url");
    }
    return suggestConeUrl;
  }

  public void setSuggestConeUrl(String suggestConeUrl) {
    this.suggestConeUrl = suggestConeUrl;
  }

  public FileCriterionCollection getFileCriterionCollection() {
    return fileCriterionCollection;
  }

  public void setFileCriterionCollection(FileCriterionCollection fileCriterionCollection) {
    this.fileCriterionCollection = fileCriterionCollection;
  }

  /**
   * @return the localTagCriterionCollection
   */
  public LocalTagCriterionCollection getLocalTagCriterionCollection() {
    return localTagCriterionCollection;
  }

  /**
   * @param localTagCriterionCollection the localTagCriterionCollection to set
   */
  public void setLocalTagCriterionCollection(LocalTagCriterionCollection localTagCriterionCollection) {
    this.localTagCriterionCollection = localTagCriterionCollection;
  }

  public DegreeCriterionCollection getDegreeCriterionCollection() {
    return degreeCriterionCollection;
  }

  public void setDegreeCriterionCollection(DegreeCriterionCollection degreeCriterionCollection) {
    this.degreeCriterionCollection = degreeCriterionCollection;
  }
}
