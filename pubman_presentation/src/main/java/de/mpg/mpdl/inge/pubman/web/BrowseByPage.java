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
package de.mpg.mpdl.inge.pubman.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO.State;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.pubman.web.affiliation.AffiliationBean;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.browseBy.BrowseBySessionBean;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.dates.DateSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.ClassificationSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.PersonSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.LinkVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import de.mpg.mpdl.inge.util.ConeUtils;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.model.SelectItem;

/**
 * Backing Bean for Browse By
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "BrowseByPage")
@SuppressWarnings("serial")
public class BrowseByPage extends BreadcrumbPage {
  private static final Logger logger = Logger.getLogger(BrowseByPage.class);

  private final BrowseBySessionBean bbBean = (BrowseBySessionBean) FacesTools.findBean("BrowseBySessionBean");

  private List<String> creators;
  private List<String> subjects;
  private SelectItem[] dateOptions;

  private String currentCharacter = "A";

  private final String queryDdc = "dc:title";
  private final String queryPerson = "foaf:family_name";

  public BrowseByPage() {}

  @Override
  public void init() {
    super.init();
    this.creators = new ArrayList<String>();
    this.subjects = new ArrayList<String>();

    if ("year".equals(this.getSelectedValue())) {
      this.loadBrowseByYear();
    }
  }

  /**
   * Perfom search for browse by values.
   * 
   * @return navigation string for page reload
   */
  public String startCharacterSearch(String selChar) {
    final String curChar = selChar;
    final List<LinkVO> links = this.callCone(this.bbBean.getSelectedValue(), curChar);
    this.bbBean.setCurrentCharacter(curChar);
    this.bbBean.setSearchResults(links);

    return "loadBrowseByPage";
  }

  /**
   * Call the cone service to retrive the browse by values.
   * 
   * @param type, type of the cone request (persons, subjects, journals)
   * @param startChar (the character with which the value has to start)
   * @return
   */
  private List<LinkVO> callCone(String type, String startChar) {
    final List<LinkVO> links = new ArrayList<LinkVO>();
    try {
      String localLang = Locale.getDefault().getLanguage();
      if (!(localLang.equals("en") || localLang.equals("de") || localLang.equals("ja"))) {
        localLang = "en";
      }
      final URL coneUrl = new URL(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + type + "/query?f=options&"
          + this.bbBean.getQuery() + "=" + URLEncoder.encode("\"" + startChar + "*\"", "UTF-8") + "&n=0&lang=" + localLang);
      final URLConnection conn = coneUrl.openConnection();
      final HttpURLConnection httpConn = (HttpURLConnection) conn;
      final int responseCode = httpConn.getResponseCode();
      switch (responseCode) {
        case 200:
          break;
        default:
          throw new RuntimeException(
              "An error occurred while calling Cone Service: " + responseCode + ": " + httpConn.getResponseMessage());
      }
      final InputStreamReader isReader = new InputStreamReader(coneUrl.openStream(), "UTF-8");
      final BufferedReader bReader = new BufferedReader(isReader);
      String line = "";
      while ((line = bReader.readLine()) != null) {
        final String[] parts = line.split("\\|");
        if (parts.length == 2) {
          final LinkVO link;
          if (type.equals("persons")) {
            link = new LinkVO(parts[1], parts[0]);
          } else {
            link = new LinkVO(parts[1], parts[1]);
          }
          links.add(link);
        }
      }
      isReader.close();
      httpConn.disconnect();
    } catch (final Exception e) {
      BrowseByPage.logger.warn("An error occurred while calling the Cone service.", e);
      return null;
    }

    return links;
  }

  public String getSearchUrl() {
    try {
      final String instanceUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
          + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH);
      final String searchPath = "/faces/SearchResultListPage.jsp?";
      return instanceUrl + searchPath;
    } catch (final Exception e) {
      BrowseByPage.logger.warn("Could not read property: 'inge.pubman.instance.url'", e);
    }

    return "";
  }

  /**
   * loads the browse by page.
   * 
   * @return String navigation string (JSF navigation) to load the browse by page.
   */
  public String loadBrowseBy() {
    this.bbBean.clear();

    return "loadBrowseBy";
  }

  /**
   * loads the affiliation tree page.
   * 
   * @return String navigation string (JSF navigation) to load the affiliation tree page.
   */
  public String loadAffiliationTree() {
    this.setSelectedValue("org");
    ((AffiliationBean) FacesTools.findBean("AffiliationBean")).setSource("BrowseBy");

    return "loadAffiliationTree";
  }

  /**
   * loads the browse by creator page.
   * 
   * @return String navigation string (JSF navigation) to load the browse by creator page.
   */
  public String loadBrowseByCreator() {
    this.setSelectedValue("persons");
    if (this.bbBean.getSearchResults() != null) {
      this.bbBean.getSearchResults().clear();
    }
    this.bbBean.setCurrentCharacter("");
    this.bbBean.setShowChars();
    this.bbBean.setQuery(this.queryPerson);

    return "loadBrowseByPage";
  }

  /**
   * loads the browse by subject page.
   * 
   * @return String navigation string (JSF navigation) to load the browse by subject page.
   */
  public String loadBrowseBySubject(String selSubject) {
    final String curSubject = selSubject;
    this.setSelectedValue(curSubject);
    if (this.bbBean.getSearchResults() != null) {
      this.bbBean.getSearchResults().clear();
    }
    this.bbBean.setCurrentCharacter("");
    this.bbBean.setShowChars();
    this.bbBean.setQuery(this.queryDdc);

    return "loadBrowseByPage";
  }

  /**
   * loads the browse by year.
   * 
   * @return String navigation string (JSF navigation) to load the browse by year.
   * @throws IngeApplicationException
   * @throws AuthorizationException
   * @throws AuthenticationException
   * @throws IngeTechnicalException
   */
  public String loadBrowseByYear() {
    this.setSelectedValue("year");
    if (this.bbBean.getSearchResults() != null) {
      this.bbBean.getSearchResults().clear();
    }
    this.bbBean.setCurrentCharacter("");
    this.bbBean.setShowChars();
    if ("any".equals(this.bbBean.getDateMode())) {
      this.bbBean.setYearStartAny();
      this.bbBean.setDateType("any");
    } else {
      this.bbBean.setYearPublished();
      this.bbBean.setDateType("published");
    }

    return "loadBrowseByPage";
  }

  public SelectItem[] getDateOptions() {
    this.dateOptions = new SelectItem[] {new SelectItem("published", this.getLabel("dateOptionPublished")),
        new SelectItem("any", this.getLabel("dateOptionAny"))};

    return this.dateOptions;
  }

  public String getSelectedValue() {
    return this.bbBean.getSelectedValue();
  }

  public void setSelectedValue(String selectedValue) {
    this.bbBean.setSelectedValue(selectedValue);
  }

  public String getCurrentCharacter() {
    return this.currentCharacter;
  }

  public void setCurrentCharacter(String currentCharacter) {
    this.currentCharacter = currentCharacter;
  }

  public List<String> getCreators() {
    return this.creators;
  }

  public void setCreators(List<String> creators) {
    this.creators = creators;
  }

  public List<String> getSubjects() {
    return this.subjects;
  }

  public void setSubjects(List<String> subjects) {
    this.subjects = subjects;
  }

  public String getPortfolioLink() {
    return ConeUtils.getFullConePersonsLink();
  }

  public void searchForAnyYear(String year) throws Exception {
    List<SearchCriterionBase> scList = new ArrayList<>();
    DateSearchCriterion dsc1 = new DateSearchCriterion(SearchCriterion.ANYDATE);
    dsc1.setFrom(year);
    dsc1.setTo(year);
    scList.add(dsc1);

    Query qb = SearchCriterionBase.scListToElasticSearchQuery(scList);
    search(qb);
  }

  public void searchForPublishedYear(String year) throws Exception {
    List<SearchCriterionBase> scList = new ArrayList<>();
    DateSearchCriterion dsc1 = new DateSearchCriterion(SearchCriterion.PUBLISHEDPRINT);
    dsc1.setFrom(year);
    dsc1.setTo(year);
    scList.add(dsc1);
    scList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
    DateSearchCriterion dsc2 = new DateSearchCriterion(SearchCriterion.PUBLISHED);
    dsc2.setFrom(year);
    dsc2.setTo(year);
    scList.add(dsc2);

    Query qb = SearchCriterionBase.scListToElasticSearchQuery(scList);
    search(qb);
  }

  public void searchForPerson(LinkVO link) throws Exception {
    List<SearchCriterionBase> scList = new ArrayList<>();
    PersonSearchCriterion ps = new PersonSearchCriterion(SearchCriterion.ANYPERSON);
    String personsId = ConeUtils.getConePersonsIdIdentifier() + link.getValue();
    ps.setHiddenId(personsId);
    ps.setSearchString(link.getLabel());
    scList.add(ps);

    Query qb = SearchCriterionBase.scListToElasticSearchQuery(scList);
    search(qb);
  }

  public void searchForSubject(String id) throws Exception {
    List<SearchCriterionBase> scList = new ArrayList<>();
    ClassificationSearchCriterion sc = new ClassificationSearchCriterion();
    sc.setClassificationType(this.getSelectedValue().trim().toUpperCase().replace("-", "_"));
    sc.setSearchString(id);
    scList.add(sc);

    Query qb = SearchCriterionBase.scListToElasticSearchQuery(scList);
    search(qb);
  }

  //  private void search(QueryBuilder qb) throws Exception {
  //    FacesTools.getExternalContext().redirect("SearchResultListPage.jsp?esq=" + URLEncoder.encode(qb.toString(), "UTF-8") + "&"
  //        + SearchRetrieverRequestBean.parameterSearchType + "=advanced");
  //  }

  private void search(Query qb) throws Exception {
    BoolQuery.Builder bqb = new BoolQuery.Builder();
    bqb.must(SearchUtils.baseElasticSearchQueryBuilder(ApplicationBean.INSTANCE.getPubItemService().getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_PUBLIC_STATE, State.RELEASED.name()));
    bqb.must(SearchUtils.baseElasticSearchQueryBuilder(ApplicationBean.INSTANCE.getPubItemService().getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_VERSION_STATE, State.RELEASED.name()));
    bqb.must(qb);
    Query query = bqb.build()._toQuery();
    FacesTools.getExternalContext().redirect("SearchResultListPage.jsp?esq=" + URLEncoder.encode(query.toString(), "UTF-8"));
  }

  @Override
  public boolean isItemSpecific() {
    return false;
  }
}
