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
    this.contextCriterionCollection = new ContextCriterionCollection();
    this.anyFieldCriterionCollection = new AnyFieldCriterionCollection();
    this.personCriterionCollection = new PersonCriterionCollection();
    this.organizationCriterionCollection = new OrganizationCriterionCollection();
    this.genreCriterionCollection = new GenreCriterionCollection();
    this.degreeCriterionCollection = new DegreeCriterionCollection();
    this.dateCriterionCollection = new DateCriterionCollection();
    this.sourceCriterionCollection = new SourceCriterionCollection();
    this.eventCriterionCollection = new EventCriterionCollection();
    this.identifierCriterionCollection = new IdentifierCriterionCollection();
    this.fileCriterionCollection = new FileCriterionCollection();
    this.languageCriterionCollection = new LanguageCriterionCollection();
    this.localTagCriterionCollection = new LocalTagCriterionCollection();
  }

  public void clearAndInitializeAllForms() {
    this.contextCriterionCollection = new ContextCriterionCollection();
    this.anyFieldCriterionCollection = new AnyFieldCriterionCollection();
    this.personCriterionCollection = new PersonCriterionCollection();
    this.organizationCriterionCollection = new OrganizationCriterionCollection();
    this.genreCriterionCollection = new GenreCriterionCollection();
    this.degreeCriterionCollection = new DegreeCriterionCollection();
    this.dateCriterionCollection = new DateCriterionCollection();
    this.sourceCriterionCollection = new SourceCriterionCollection();
    this.eventCriterionCollection = new EventCriterionCollection();
    this.identifierCriterionCollection = new IdentifierCriterionCollection();
    this.fileCriterionCollection = new FileCriterionCollection();
    this.languageCriterionCollection = new LanguageCriterionCollection();
    this.localTagCriterionCollection = new LocalTagCriterionCollection();
  }

  /**
   * Action handler to reset all forms.
   */
  public void clearAllForms() {
    // delegate clearAllForms to internal collections
    this.contextCriterionCollection.clearAllForms();
    this.anyFieldCriterionCollection.clearAllForms();
    this.personCriterionCollection.clearAllForms();
    this.organizationCriterionCollection.clearAllForms();
    this.genreCriterionCollection.clearAllForms();
    this.degreeCriterionCollection.clearAllForms();
    this.dateCriterionCollection.clearAllForms();
    this.sourceCriterionCollection.clearAllForms();
    this.eventCriterionCollection.clearAllForms();
    this.identifierCriterionCollection.clearAllForms();
    this.fileCriterionCollection.clearAllForms();
    this.languageCriterionCollection.clearAllForms();
    this.localTagCriterionCollection.clearAllForms();
  }

  /**
   * Starts the advanced search. iterates a TreeMap with all criterion masks with entered data and
   * fills a list with CriterionVO's to be passed to the PubItemSearching interface.
   * 
   * @return (String): identifying the page that should be navigated to after this method call.
   */
  public void startSearch() {
    final ArrayList<Criterion> criterionList = new ArrayList<Criterion>();
    criterionList.addAll(this.anyFieldCriterionCollection.getFilledCriterion());
    criterionList.addAll(this.contextCriterionCollection.getFilledCriterion());
    criterionList.addAll(this.personCriterionCollection.getFilledCriterion());
    criterionList.addAll(this.dateCriterionCollection.getFilledCriterion());
    criterionList.addAll(this.genreCriterionCollection.getFilledCriterion());
    criterionList.addAll(this.degreeCriterionCollection.getFilledCriterion());
    criterionList.addAll(this.organizationCriterionCollection.getFilledCriterion());
    criterionList.addAll(this.fileCriterionCollection.getFilledCriterion());
    criterionList.addAll(this.identifierCriterionCollection.getFilledCriterion());
    criterionList.addAll(this.eventCriterionCollection.getFilledCriterion());
    criterionList.addAll(this.sourceCriterionCollection.getFilledCriterion());
    criterionList.addAll(this.languageCriterionCollection.getFilledCriterion());
    criterionList.addAll(this.localTagCriterionCollection.getFilledCriterion());

    final ArrayList<MetadataSearchCriterion> searchCriteria =
        new ArrayList<MetadataSearchCriterion>();

    if (criterionList.size() == 0) {
      FacesBean.error(this.getMessage("search_NoCriteria"));
      return;
    }

    // add the default criterion to the top of the list
    criterionList.add(0, new ObjectCriterion());

    // transform the criteria to searchCriteria
    try {
      // transform first element
      final ArrayList<MetadataSearchCriterion> subset =
          this.transformToSearchCriteria(null, criterionList.get(0));
      searchCriteria.addAll(subset);
      ArrayList<MetadataSearchCriterion> currentList = searchCriteria;
      for (int i = 1; i < criterionList.size(); i++) {

        final ArrayList<MetadataSearchCriterion> newCriteria =
            this.transformToSearchCriteria(criterionList.get(i - 1), criterionList.get(i));

        final Class<? extends Criterion> c = criterionList.get(i).getClass();
        final Class<? extends Criterion> d = criterionList.get(i - 1).getClass();
        if (c.equals(d)) {
          currentList.addAll(newCriteria);
        } else {
          currentList.get(currentList.size() - 1).addSubCriteria(newCriteria);
          currentList = currentList.get(currentList.size() - 1).getSubCriteriaList();
        }
      }

      // add the contentType to the query
      final ArrayList<String> contentTypes = new ArrayList<String>();
      final String contentTypeIdPublication =
          PropertyReader.getProperty(AdvancedSearchEdit.PROPERTY_CONTENT_MODEL);
      contentTypes.add(contentTypeIdPublication);

      final MetadataSearchQuery query = new MetadataSearchQuery(contentTypes, searchCriteria);

      final String cql = query.getCqlQuery();

      try {
        String searchString = "";

        for (final Criterion c : this.anyFieldCriterionCollection.getFilledCriterion()) {
          searchString += c.getSearchString() + " ";
        }

        for (final Criterion c : this.personCriterionCollection.getFilledCriterion()) {
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
      } catch (final Exception e) {
        AdvancedSearchEdit.logger.error("Could not log statistical data", e);
      }

      // redirect to SearchResultPage which processes the query
      FacesTools.getExternalContext().redirect(
          "SearchResultListPage.jsp?" + SearchRetrieverRequestBean.parameterCqlQuery + "="
              + URLEncoder.encode(cql, "UTF-8") + "&"
              + SearchRetrieverRequestBean.parameterSearchType + "=advanced");

    } catch (final de.mpg.mpdl.inge.search.parser.ParseException e) {
      AdvancedSearchEdit.logger.error("Search criteria includes some lexical error", e);
      FacesBean.error(this.getMessage("search_ParseError"));
      return;
    } catch (final Exception e) {
      AdvancedSearchEdit.logger.error("Technical problem while retrieving the search results", e);
      FacesBean.error(this.getMessage("search_TechnicalError"));
      return;
    }
  }

  private ArrayList<MetadataSearchCriterion> transformToSearchCriteria(Criterion predecessor,
      Criterion transformMe) throws TechnicalException {
    final ArrayList<MetadataSearchCriterion> results = transformMe.createSearchCriterion();
    // we're on the first element of the criteria
    if (predecessor == null) {
      if (results.size() != 0) {
        // set the first logical operator to unset
        results.get(0).setLogicalOperator(LogicalOperator.UNSET);
      }
      return results;
    } else {
      if (results.size() != 0) {
        final LogicalOperator operator = predecessor.getLogicalOperator();
        results.get(0).setLogicalOperator(operator);

        final Class<? extends Criterion> c = predecessor.getClass();
        final Class<? extends Criterion> d = transformMe.getClass();

        if (c.equals(d) && c.getName().contains("OrganizationCriterion")) // hack TODO
        {
          results.get(0).setLogicalOperator(LogicalOperator.OR);
        }
      }
      return results;
    }
  }

  public PersonCriterionCollection getPersonCriterionCollection() {
    return this.personCriterionCollection;
  }

  public void setPersonCriterionCollection(PersonCriterionCollection personCriterionCollection) {
    this.personCriterionCollection = personCriterionCollection;
  }

  public GenreCriterionCollection getGenreCriterionCollection() {
    return this.genreCriterionCollection;
  }

  public void setGenreCriterionCollection(GenreCriterionCollection genreCriterionCollection) {
    this.genreCriterionCollection = genreCriterionCollection;
  }

  public DateCriterionCollection getDateCriterionCollection() {
    return this.dateCriterionCollection;
  }

  public void setDateCriterionCollection(DateCriterionCollection dateCriterionCollection) {
    this.dateCriterionCollection = dateCriterionCollection;
  }

  public ContextCriterionCollection getContextCriterionCollection() {
    return this.contextCriterionCollection;
  }

  public void setContextCriterionCollection(ContextCriterionCollection contextCriterionCollection) {
    this.contextCriterionCollection = contextCriterionCollection;
  }

  public AnyFieldCriterionCollection getAnyFieldCriterionCollection() {
    return this.anyFieldCriterionCollection;
  }

  public void setAnyFieldCriterionCollection(AnyFieldCriterionCollection anyFieldCriterionCollection) {
    this.anyFieldCriterionCollection = anyFieldCriterionCollection;
  }



  public EventCriterionCollection getEventCriterionCollection() {
    return this.eventCriterionCollection;
  }

  public void setEventCriterionCollection(EventCriterionCollection eventCriterionCollection) {
    this.eventCriterionCollection = eventCriterionCollection;
  }

  public IdentifierCriterionCollection getIdentifierCriterionCollection() {
    return this.identifierCriterionCollection;
  }

  public void setIdentifierCriterionCollection(
      IdentifierCriterionCollection identifierCriterionCollection) {
    this.identifierCriterionCollection = identifierCriterionCollection;
  }

  public OrganizationCriterionCollection getOrganizationCriterionCollection() {
    return this.organizationCriterionCollection;
  }

  public void setOrganizationCriterionCollection(
      OrganizationCriterionCollection organizationCriterionCollection) {
    this.organizationCriterionCollection = organizationCriterionCollection;
  }

  public SourceCriterionCollection getSourceCriterionCollection() {
    return this.sourceCriterionCollection;
  }

  public void setSourceCriterionCollection(SourceCriterionCollection sourceCriterionCollection) {
    this.sourceCriterionCollection = sourceCriterionCollection;
  }

  public LanguageCriterionCollection getLanguageCriterionCollection() {
    return this.languageCriterionCollection;
  }

  public void setLanguageCriterionCollection(LanguageCriterionCollection languageCriterionCollection) {
    this.languageCriterionCollection = languageCriterionCollection;
  }


  public String getSuggestConeUrl() throws Exception {
    if (this.suggestConeUrl == null) {
      this.suggestConeUrl = PropertyReader.getProperty("escidoc.cone.service.url");
    }
    return this.suggestConeUrl;
  }

  public void setSuggestConeUrl(String suggestConeUrl) {
    this.suggestConeUrl = suggestConeUrl;
  }

  public FileCriterionCollection getFileCriterionCollection() {
    return this.fileCriterionCollection;
  }

  public void setFileCriterionCollection(FileCriterionCollection fileCriterionCollection) {
    this.fileCriterionCollection = fileCriterionCollection;
  }

  /**
   * @return the localTagCriterionCollection
   */
  public LocalTagCriterionCollection getLocalTagCriterionCollection() {
    return this.localTagCriterionCollection;
  }

  /**
   * @param localTagCriterionCollection the localTagCriterionCollection to set
   */
  public void setLocalTagCriterionCollection(LocalTagCriterionCollection localTagCriterionCollection) {
    this.localTagCriterionCollection = localTagCriterionCollection;
  }

  public DegreeCriterionCollection getDegreeCriterionCollection() {
    return this.degreeCriterionCollection;
  }

  public void setDegreeCriterionCollection(DegreeCriterionCollection degreeCriterionCollection) {
    this.degreeCriterionCollection = degreeCriterionCollection;
  }
}
