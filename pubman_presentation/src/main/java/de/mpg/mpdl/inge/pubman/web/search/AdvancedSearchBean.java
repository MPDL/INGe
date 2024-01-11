/*
 * 
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase.DisplayType;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase.Index;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox.AffiliatedContextListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox.ItemStateListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox.PublicationStatusListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.FileSectionSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.dates.DateSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.genre.GenreListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.Parenthesis;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.CollectionSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.StandardSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.TitleSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.CreatedBySearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.ModifiedBySearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.OrganizationSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.PersonSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.StringOrHiddenIdSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.DisplayTools;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.LanguageChangeObserver;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.converter.SelectItemComparator;
import de.mpg.mpdl.inge.service.pubman.impl.ContextServiceDbImpl;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;

@ManagedBean(name = "AdvancedSearchBean")
@SessionScoped
@SuppressWarnings("serial")
public class AdvancedSearchBean extends FacesBean implements LanguageChangeObserver {
  private static final Logger logger = Logger.getLogger(AdvancedSearchBean.class);

  private List<SearchCriterionBase> criterionList;

  private List<SelectItem> componentVisibilityListMenu = this.initComponentVisibilityListMenu();
  private List<SelectItem> contentCategoryListMenu = this.initContentCategoryListMenu();
  private List<SelectItem> oaStatusListMenu = this.initOaStatusListMenu();
  private List<SelectItem> contextListMenu;
  private List<SelectItem> criterionTypeListMenu = this.initCriterionTypeListMenu(Index.ESCIDOC_ALL);
  private List<SelectItem> criterionTypeListMenuAdmin = this.initCriterionTypeListMenu(Index.ITEM_CONTAINER_ADMIN);
  private List<SelectItem> genreListMenu = this.initGenreListMenu();
  private List<SelectItem> operatorTypeListMenu = this.initOperatorListMenu();
  private List<SelectItem> reviewMethodListMenu = this.initReviewMethodListMenu();
  private List<SelectItem> subjectTypesListMenu = this.initSubjectTypesListMenu();
  private List<SelectItem> identifierTypesListMenu = this.initIdentifierTypesListMenu();
  private List<SelectItem> personRoleMenu = this.initPersonRoleMenu();

  private Map<SearchCriterionBase, Boolean> possibleCriterionsForClosingParenthesisMap = new HashMap<SearchCriterionBase, Boolean>();
  private Map<SearchCriterionBase, Integer> balanceMap = new HashMap<SearchCriterionBase, Integer>();

  private Parenthesis currentlyOpenedParenthesis;

  private SearchCriterionBase affiliatedContextListSearchCriterion;

  private SearchCriterionBase fileSectionSearchCriterion;

  private SearchCriterionBase locatorSectionSearchCriterion;


  private SearchCriterionBase genreListSearchCriterion;
  private SearchCriterionBase itemStateListSearchCriterion;

  private SearchCriterionBase publicationStatusListSearchCriterion;

  private String query = "";

  private boolean excludeComponentContentCategory;
  private boolean languageChanged;

  private int numberOfSearchCriterions;

  public AdvancedSearchBean() {}

  @PostConstruct
  public void postConstruct() {
    this.getI18nHelper().addLanguageChangeObserver(this);
  }

  @PreDestroy
  public void preDestroy() {
    this.getI18nHelper().removeLanguageChangeObserver(this);
  }

  public void clearAndInit() {
    this.affiliatedContextListSearchCriterion = new AffiliatedContextListSearchCriterion();
    this.balanceMap.clear();
    this.setFileSectionSearchCriterion(new FileSectionSearchCriterion(SearchCriterion.FILE_SECTION));
    this.setLocatorSectionSearchCriterion(new FileSectionSearchCriterion(SearchCriterion.LOCATOR_SECTION));

    this.currentlyOpenedParenthesis = null;
    this.excludeComponentContentCategory = false;

    this.genreListSearchCriterion = new GenreListSearchCriterion();
    this.itemStateListSearchCriterion = new ItemStateListSearchCriterion();

    this.possibleCriterionsForClosingParenthesisMap.clear();
    this.publicationStatusListSearchCriterion = new PublicationStatusListSearchCriterion();

    this.initCriterionListWithEmptyValues();
  }

  private void initCriterionListWithEmptyValues() {
    this.criterionList = new ArrayList<SearchCriterionBase>();
    this.criterionList.add(new TitleSearchCriterion());
    this.criterionList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    this.criterionList.add(new PersonSearchCriterion(SearchCriterion.ANYPERSON));
    this.criterionList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    this.criterionList.add(new OrganizationSearchCriterion());
    this.criterionList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    this.criterionList.add(new DateSearchCriterion(SearchCriterion.ANYDATE));

    this.updateListForClosingParenthesis(null);
  }

  private void initWithQueryParam(String queryParam) {
    final List<SearchCriterionBase> scList = SearchCriterionBase.queryStringToScList(queryParam);

    final List<SearchCriterionBase> toBeRemovedList = new ArrayList<SearchCriterionBase>();
    for (int i = scList.size() - 1; i >= 0; i--) {

      final SearchCriterionBase sc = scList.get(i);

      if (SearchCriterion.FILE_SECTION.equals(sc.getSearchCriterion())) {
        this.setFileSectionSearchCriterion(sc);
        toBeRemovedList.add(sc);
      } else if (SearchCriterion.LOCATOR_SECTION.equals(sc.getSearchCriterion())) {
        this.setLocatorSectionSearchCriterion(sc);
        toBeRemovedList.add(sc);
      } else if (SearchCriterion.GENRE_DEGREE_LIST.equals(sc.getSearchCriterion())) {
        this.genreListSearchCriterion = sc;
        toBeRemovedList.add(sc);
      } else if (SearchCriterion.ITEMSTATE_LIST.equals(sc.getSearchCriterion())) {
        this.itemStateListSearchCriterion = sc;
        toBeRemovedList.add(sc);
      } else if (SearchCriterion.AFFILIATED_CONTEXT_LIST.equals(sc.getSearchCriterion())) {
        this.affiliatedContextListSearchCriterion = sc;
        toBeRemovedList.add(sc);
      } else if (SearchCriterion.PUBLICATION_STATUS_LIST.equals(sc.getSearchCriterion())) {
        this.publicationStatusListSearchCriterion = sc;
        toBeRemovedList.add(sc);
      }
    }

    for (final SearchCriterionBase sc : toBeRemovedList) {
      SearchCriterionBase.removeSearchCriterionWithOperator(scList, sc);
    }

    this.criterionList = scList;

    //Remove opening and closing parenthesis
    if (this.criterionList.size() > 1 && this.criterionList.get(0).getSearchCriterion().equals(SearchCriterion.OPENING_PARENTHESIS)
        && this.criterionList.get(criterionList.size() - 1).getSearchCriterion().equals(SearchCriterion.CLOSING_PARENTHESIS)) {
      criterionList.remove(criterionList.size() - 1);
      criterionList.remove(0);
    }

    if (this.criterionList.isEmpty()) {
      this.initCriterionListWithEmptyValues();
    }

    this.updateListForClosingParenthesis(null);
  }

  /**
   * Dummy getter method which reads out query parameter form url;
   * 
   * @return
   */
  public String getReadOutParams() {
    if (!this.languageChanged) {
      final FacesContext fc = FacesTools.getCurrentInstance();
      String query = fc.getExternalContext().getRequestParameterMap().get("q");
      query = CommonUtils.fixURLEncoding(query);
      final boolean isPostback = fc.getRenderKit().getResponseStateManager().isPostback(fc);

      if (!isPostback) {
        if (query != null && !query.trim().isEmpty()) {
          AdvancedSearchBean.logger.debug("Found query, initialize: " + query);
          this.clearAndInit();
          this.initWithQueryParam(query);
        } else {
          AdvancedSearchBean.logger.debug("No internal query found, initialize empty");
          this.clearAndInit();
        }
      } else {
        // logger.info("Postback, do nothing");
      }
    }

    this.languageChanged = false;

    return "";
  }

  private List<SelectItem> initComponentVisibilityListMenu() {
    return Arrays.asList(this.getI18nHelper().getSelectedItemsComponentVisibility(true));
  }

  private List<SelectItem> initContentCategoryListMenu() {
    return Arrays.asList(this.getI18nHelper().getSelectItemsContentCategory(true));
  }

  private List<SelectItem> initOaStatusListMenu() {
    return Arrays.asList(this.getI18nHelper().getSelectItemsOaStatus(true));
  }

  private List<SelectItem> initGenreListMenu() {
    return Arrays.asList(this.getI18nHelper().getSelectItemsGenre());
  }

  private List<SelectItem> initReviewMethodListMenu() {
    return Arrays.asList(this.getI18nHelper().getSelectItemsReviewMethod());
  }

  private List<SelectItem> initSubjectTypesListMenu() {
    final List<SelectItem> vocabs = new ArrayList<SelectItem>();
    try {
      final String vocabsStr = PropertyReader.getProperty(PropertyReader.INGE_CONE_SUBJECTVOCAB);
      final String[] vocabsArr = vocabsStr.split(";");
      for (int i = 0; i < vocabsArr.length; i++) {
        final String type = vocabsArr[i].trim().toUpperCase().replace("-", "_");
        final String label = vocabsArr[i].trim().toUpperCase();
        final SelectItem si = new SelectItem(type, label);
        vocabs.add(si);
      }
    } catch (final Exception e) {
      AdvancedSearchBean.logger.error("Could not read Property: '" + PropertyReader.INGE_CONE_SUBJECTVOCAB + "'", e);
    }
    return vocabs;
  }

  private List<SelectItem> initIdentifierTypesListMenu() {
    final List<SelectItem> identifierRoleMenu = new ArrayList<SelectItem>();

    identifierRoleMenu.add(new SelectItem(null, this.getLabel("EditItem_NO_ITEM_SET")));
    for (final IdType type : DisplayTools.getIdTypesToDisplay()) {
      identifierRoleMenu.add(new SelectItem(type.name(), this.getLabel("ENUM_IDENTIFIERTYPE_" + type.name())));
    }

    // Sort identifiers alphabetically
    Collections.sort(identifierRoleMenu, new Comparator<SelectItem>() {
      @Override
      public int compare(SelectItem o1, SelectItem o2) {
        return o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase());
      }
    });

    return identifierRoleMenu;
  }

  private List<SelectItem> initPersonRoleMenu() {
    final List<SelectItem> personRoleMenu = new ArrayList<SelectItem>();

    personRoleMenu.add(new SelectItem(null, this.getLabel("adv_search_lblSearchPerson")));
    for (final CreatorRole role : CreatorRole.values()) {
      personRoleMenu.add(new SelectItem(role.name(), this.getLabel("ENUM_CREATORROLE_" + role.name())));
    }

    return personRoleMenu;
  }

  private List<SelectItem> initCriterionTypeListMenu(Index indexName) {
    final List<SelectItem> criterionTypeList = new ArrayList<SelectItem>();
    // General
    criterionTypeList.add(new SelectItem(SearchCriterion.TITLE, this.getLabel("adv_search_lblRgbTitle")));
    criterionTypeList.add(new SelectItem(SearchCriterion.KEYWORD, this.getLabel("adv_search_lblRgbTopic")));
    criterionTypeList.add(new SelectItem(SearchCriterion.CLASSIFICATION, this.getLabel("adv_search_lblClassification")));
    criterionTypeList.add(new SelectItem(SearchCriterion.ANY, this.getLabel("adv_search_lblRgbAny")));
    criterionTypeList.add(new SelectItem(SearchCriterion.FULLTEXT, this.getLabel("adv_search_lblRgbFulltext")));

    // AdminStuff
    if (indexName == Index.ITEM_CONTAINER_ADMIN) {
      final List<SelectItem> adminGroupList = new ArrayList<SelectItem>();
      final SelectItemGroup adminGroup = new SelectItemGroup(this.getLabel("adv_search_lblSearchAdmin"));
      adminGroupList.add(new SelectItem(SearchCriterion.CREATED_INTERNAL, this.getLabel("adv_search_lblItemCreationDate")));
      adminGroupList.add(new SelectItem(SearchCriterion.MODIFIED_INTERNAL, this.getLabel("adv_search_lblItemLastModificationDate")));
      adminGroup.setSelectItems(adminGroupList.toArray(new SelectItem[0]));
      criterionTypeList.add(adminGroup);
    }

    // Persons
    criterionTypeList.add(new SelectItem(SearchCriterion.ANYPERSON, this.getLabel("adv_search_lblSearchPerson")));

    // Orcid
    criterionTypeList.add(new SelectItem(SearchCriterion.ORCID, this.getLabel("adv_search_lbHeaderOrcid")));

    // Organisation
    criterionTypeList.add(new SelectItem(SearchCriterion.ORGUNIT, this.getLabel("adv_search_lbHeaderOrgan")));

    // Dates
    final List<SelectItem> dateGroupList = new ArrayList<SelectItem>();
    dateGroupList.add(new SelectItem(SearchCriterion.ANYDATE, this.getLabel("adv_search_lbHeaderDate")));
    dateGroupList.add(new SelectItem(SearchCriterion.PUBLISHEDPRINT, this.getLabel("adv_search_lblChkType_abb_publishedpr")));
    dateGroupList.add(new SelectItem(SearchCriterion.PUBLISHED, this.getLabel("adv_search_lblChkType_publishedon")));
    dateGroupList.add(new SelectItem(SearchCriterion.ACCEPTED, this.getLabel("adv_search_lblChkType_accepted")));
    dateGroupList.add(new SelectItem(SearchCriterion.SUBMITTED, this.getLabel("adv_search_lblChkType_submitted")));
    dateGroupList.add(new SelectItem(SearchCriterion.MODIFIED, this.getLabel("adv_search_lblChkType_modified")));
    dateGroupList.add(new SelectItem(SearchCriterion.CREATED, this.getLabel("adv_search_lblChkType_created")));

    final SelectItemGroup dateGroup = new SelectItemGroup(this.getLabel("adv_search_lbHeaderDate"));
    dateGroup.setSelectItems(dateGroupList.toArray(new SelectItem[0]));
    criterionTypeList.add(dateGroup);

    // Event
    final List<SelectItem> eventGroupList = new ArrayList<SelectItem>();
    eventGroupList.add(new SelectItem(SearchCriterion.EVENT, this.getLabel("adv_search_lbHeaderEvent")));
    eventGroupList.add(new SelectItem(SearchCriterion.EVENT_STARTDATE, this.getLabel("adv_search_lblChkType_abb_event_start_date")));
    eventGroupList.add(new SelectItem(SearchCriterion.EVENT_ENDDATE, this.getLabel("adv_search_lblChkType_abb_event_end_date")));
    eventGroupList.add(new SelectItem(SearchCriterion.EVENT_INVITATION, this.getLabel("ENUM_INVITATIONSTATUS_INVITED")));

    final SelectItemGroup eventGroup = new SelectItemGroup(this.getLabel("adv_search_lbHeaderEvent"));
    eventGroup.setSelectItems(eventGroupList.toArray(new SelectItem[0]));
    criterionTypeList.add(eventGroup);

    // Genre
    criterionTypeList.add(new SelectItem(SearchCriterion.GENRE, this.getLabel("adv_search_lbHeaderGenre")));
    criterionTypeList.add(new SelectItem(SearchCriterion.REVIEW_METHOD, this.getLabel("ViewItemFull_lblRevisionMethod")));

    // Language
    criterionTypeList.add(new SelectItem(SearchCriterion.LANG, this.getLabel("adv_search_lblLanguageTerm")));

    // Source
    criterionTypeList.add(new SelectItem(SearchCriterion.SOURCE, this.getLabel("adv_search_lbHeaderSource")));
    criterionTypeList.add(new SelectItem(SearchCriterion.JOURNAL, " - " + this.getLabel("adv_search_lblSourceJournal")));

    // LocalTag
    criterionTypeList.add(new SelectItem(SearchCriterion.LOCAL, this.getLabel("adv_search_lbHeaderLocalTag")));

    // Identifier
    criterionTypeList.add(new SelectItem(SearchCriterion.IDENTIFIER, this.getLabel("adv_search_lbHeaderIdent")));

    // Collection
    criterionTypeList.add(new SelectItem(SearchCriterion.COLLECTION, this.getLabel("adv_search_lbHeaderCollection")));

    // ProjectInfo
    criterionTypeList.add(new SelectItem(SearchCriterion.PROJECT_INFO, this.getLabel("g_project_info")));

    // Component content category
    criterionTypeList.add(new SelectItem(SearchCriterion.COMPONENT_CONTENT_CATEGORY_LIST, this.getLabel("adv_search_fileContentCategory")));

    return criterionTypeList;
  }

  private List<SelectItem> initOperatorListMenu() {
    final List<SelectItem> operatorTypeList = new ArrayList<SelectItem>();

    // General
    operatorTypeList.add(new SelectItem(SearchCriterion.AND_OPERATOR, this.getLabel("adv_search_logicop_and")));
    operatorTypeList.add(new SelectItem(SearchCriterion.OR_OPERATOR, this.getLabel("adv_search_logicop_or")));
    operatorTypeList.add(new SelectItem(SearchCriterion.NOT_OPERATOR, this.getLabel("adv_search_logicop_not")));

    return operatorTypeList;
  }

  public void copyValuesFromOldToNew(SearchCriterionBase oldSc, SearchCriterionBase newSc) {
    if (oldSc instanceof PersonSearchCriterion && newSc instanceof PersonSearchCriterion) {
      ((PersonSearchCriterion) newSc).setSearchString(((PersonSearchCriterion) oldSc).getSearchString());
      ((PersonSearchCriterion) newSc).setHiddenId(((PersonSearchCriterion) oldSc).getHiddenId());
    }

    else if (oldSc instanceof DateSearchCriterion && newSc instanceof DateSearchCriterion) {
      ((DateSearchCriterion) newSc).setFrom(((DateSearchCriterion) oldSc).getFrom());
      ((DateSearchCriterion) newSc).setTo(((DateSearchCriterion) oldSc).getTo());
    }

    else if (oldSc instanceof StandardSearchCriterion && newSc instanceof StandardSearchCriterion
        && !(oldSc instanceof CollectionSearchCriterion) && !(newSc instanceof CollectionSearchCriterion)) {
      ((StandardSearchCriterion) newSc).setSearchString(((StandardSearchCriterion) oldSc).getSearchString());

    } else if ((oldSc instanceof CreatedBySearchCriterion && newSc instanceof ModifiedBySearchCriterion)
        || (oldSc instanceof ModifiedBySearchCriterion && newSc instanceof CreatedBySearchCriterion)) {
      ((StringOrHiddenIdSearchCriterion) newSc).setHiddenId(((StringOrHiddenIdSearchCriterion) oldSc).getHiddenId());
      ((StringOrHiddenIdSearchCriterion) newSc).setSearchString(((StringOrHiddenIdSearchCriterion) oldSc).getSearchString());
    }
  }

  public void changeCriterionAction(ValueChangeEvent evt) {
    final Integer position = (Integer) evt.getComponent().getAttributes().get("indexOfCriterion");

    if (evt.getNewValue() != null && position != null) {
      final SearchCriterion newValue = SearchCriterion.valueOf(evt.getNewValue().toString());
      AdvancedSearchBean.logger.debug("Changing sortCriteria at position " + position + " to " + newValue);

      final SearchCriterionBase oldSearchCriterion = this.criterionList.remove(position.intValue());
      final SearchCriterionBase newSearchCriterion = SearchCriterionBase.initSearchCriterion(newValue);
      newSearchCriterion.setLevel(oldSearchCriterion.getLevel());
      if (this.possibleCriterionsForClosingParenthesisMap.containsKey(oldSearchCriterion)) {
        final boolean oldValue = this.possibleCriterionsForClosingParenthesisMap.get(oldSearchCriterion);
        this.possibleCriterionsForClosingParenthesisMap.remove(oldSearchCriterion);
        this.possibleCriterionsForClosingParenthesisMap.put(newSearchCriterion, oldValue);
      }
      this.copyValuesFromOldToNew(oldSearchCriterion, newSearchCriterion);
      this.criterionList.add(position, newSearchCriterion);
    }
  }

  public List<SearchCriterionBase> getCriterionList() {
    return this.criterionList;
  }

  public void setCriterionList(List<SearchCriterionBase> criterionList) {
    this.criterionList = criterionList;
  }

  public List<SelectItem> getCriterionTypeListMenu() {
    return this.criterionTypeListMenu;
  }

  public void setCriterionTypeListMenu(List<SelectItem> criterionTypeListMenu) {
    this.criterionTypeListMenu = criterionTypeListMenu;
  }

  public void addSearchCriterion(int position) {
    final SearchCriterionBase oldSearchCriterion = this.criterionList.get(position);

    // If the add button of a Parenthesis is used, add an ANYFIELD Criterion, else add the same
    // criterion as in the line of the add button.
    SearchCriterionBase newSearchCriterion = null;
    if (DisplayType.PARENTHESIS.equals(oldSearchCriterion.getSearchCriterion().getDisplayType())) {
      newSearchCriterion = SearchCriterionBase.initSearchCriterion(SearchCriterion.ANY);
    } else {
      newSearchCriterion = SearchCriterionBase.initSearchCriterion(oldSearchCriterion.getSearchCriterion());
    }

    newSearchCriterion.setLevel(oldSearchCriterion.getLevel());
    this.criterionList.add(position + 1, newSearchCriterion);

    // If the add button of an opening parenthesis is used, the logical operator has to be added
    // after the new criterion
    if (SearchCriterion.OPENING_PARENTHESIS.equals(oldSearchCriterion.getSearchCriterion())) {
      this.criterionList.add(position + 2, new LogicalOperator(SearchCriterion.AND_OPERATOR));
    } else {
      this.criterionList.add(position + 1, new LogicalOperator(SearchCriterion.AND_OPERATOR));
    }

    this.updateListForClosingParenthesis(this.currentlyOpenedParenthesis);
  }

  public void removeSearchCriterion(int position) {
    final SearchCriterionBase sc = this.criterionList.get(position);
    SearchCriterionBase.removeSearchCriterionWithOperator(this.criterionList, sc);
    this.updateListForClosingParenthesis(this.currentlyOpenedParenthesis);
  }

  public void addOpeningParenthesis(int position) {
    this.currentlyOpenedParenthesis = new Parenthesis(SearchCriterion.OPENING_PARENTHESIS);
    this.currentlyOpenedParenthesis.setLevel(this.criterionList.get(position).getLevel());
    // add before criterion
    this.criterionList.add(position, this.currentlyOpenedParenthesis);
    this.updateListForClosingParenthesis(this.currentlyOpenedParenthesis);
  }

  public void addClosingParenthesis(int position) {
    final Parenthesis closingParenthesis = new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS);
    this.currentlyOpenedParenthesis.setPartnerParenthesis(closingParenthesis);
    closingParenthesis.setPartnerParenthesis(this.currentlyOpenedParenthesis);
    this.currentlyOpenedParenthesis = null;
    this.criterionList.add(position + 1, closingParenthesis);
    this.updateListForClosingParenthesis(null);
  }

  public void removeParenthesis(int position) {
    final Parenthesis parenthesis = (Parenthesis) this.criterionList.get(position);
    final Parenthesis partnerParenthesis = parenthesis.getPartnerParenthesis();

    this.criterionList.remove(parenthesis);
    this.criterionList.remove(partnerParenthesis);

    if (parenthesis.equals(this.currentlyOpenedParenthesis)) {
      this.currentlyOpenedParenthesis = null;
    }

    this.updateListForClosingParenthesis(this.currentlyOpenedParenthesis);
  }

  private void updateListForClosingParenthesis(SearchCriterionBase startParenthesis) {
    this.possibleCriterionsForClosingParenthesisMap.clear();
    int balanceCounter = 0;
    boolean lookForClosingParenthesis = false;
    int startParenthesisBalance = 0;

    this.numberOfSearchCriterions = 0;

    for (final SearchCriterionBase sc : this.criterionList) {

      if (SearchCriterion.CLOSING_PARENTHESIS.equals(sc.getSearchCriterion())) {
        balanceCounter--;
        if (lookForClosingParenthesis && balanceCounter <= startParenthesisBalance) {
          lookForClosingParenthesis = false;
        }
      }

      sc.setLevel(balanceCounter);

      if (SearchCriterion.OPENING_PARENTHESIS.equals(sc.getSearchCriterion())) {
        balanceCounter++;
      }

      if (sc.equals(startParenthesis)) {
        lookForClosingParenthesis = true;
        startParenthesisBalance = sc.getLevel();
      }

      if (lookForClosingParenthesis && !DisplayType.OPERATOR.equals(sc.getSearchCriterion().getDisplayType())
          && balanceCounter == startParenthesisBalance + 1) {
        this.possibleCriterionsForClosingParenthesisMap.put(sc, true);
      }


      if (!DisplayType.OPERATOR.equals(sc.getSearchCriterion().getDisplayType())
          && !DisplayType.PARENTHESIS.equals(sc.getSearchCriterion().getDisplayType())) {
        this.numberOfSearchCriterions++;
      }
    }
  }

  public List<SelectItem> getContextListMenu() throws Exception {
    if (this.contextListMenu == null) {
      try {

        Query qb =
            BoolQuery.of(b -> b.must(TermQuery.of(t -> t.field(ContextServiceDbImpl.INDEX_STATE).value("OPENED"))._toQuery()))._toQuery();

        SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, 1000, 0);
        SearchRetrieveResponseVO<ContextDbVO> result = ApplicationBean.INSTANCE.getContextService().search(srr, null);

        this.contextListMenu = new ArrayList<SelectItem>();

        for (final SearchRetrieveRecordVO<ContextDbVO> c : result.getRecords()) {
          this.contextListMenu.add(new SelectItem(c.getData().getObjectId(), c.getData().getName()));
        }

        Collections.sort(this.contextListMenu, new SelectItemComparator());
        this.contextListMenu.add(0, new SelectItem("", "--"));
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }

    return this.contextListMenu;
  }

  public List<SelectItem> getOperatorTypeListMenu() {
    return this.operatorTypeListMenu;
  }

  public void setOperatorTypeListMenu(List<SelectItem> operatorTypeListMenu) {
    this.operatorTypeListMenu = operatorTypeListMenu;
  }

  private void startSearch(Index indexName) {
    if (this.currentlyOpenedParenthesis != null) {
      this.error(this.getMessage("search_ParenthesisNotClosed"));
      return;
    }

    final List<SearchCriterionBase> allCriterions = new ArrayList<SearchCriterionBase>();

    allCriterions.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
    allCriterions.addAll(this.getCriterionList());
    allCriterions.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));

    if (Index.ITEM_CONTAINER_ADMIN == indexName) {
      allCriterions.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
      allCriterions.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
      allCriterions.add(this.itemStateListSearchCriterion);
      allCriterions.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));

      allCriterions.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
      allCriterions.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
      allCriterions.add(this.affiliatedContextListSearchCriterion);
      allCriterions.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));
    }

    allCriterions.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    allCriterions.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
    allCriterions.add(this.genreListSearchCriterion);
    allCriterions.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));

    allCriterions.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    allCriterions.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
    allCriterions.add(this.publicationStatusListSearchCriterion);
    allCriterions.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));

    final List<SearchCriterionBase> componentSearchCriterions = this.getComponentSearchCriterions(indexName);
    allCriterions.addAll(componentSearchCriterions);

    this.query = SearchCriterionBase.scListToQueryString(allCriterions);
    AdvancedSearchBean.logger.debug("Internal Query: " + this.query);

    if (this.query == null || this.query.trim().isEmpty()) {
      this.error(this.getMessage("search_NoCriteria"));
    }

    String searchType = "advanced";
    if (Index.ITEM_CONTAINER_ADMIN == indexName) {
      searchType = "admin";
    }

    try {
      final BreadcrumbItemHistorySessionBean bihsb =
          (BreadcrumbItemHistorySessionBean) FacesTools.findBean("BreadcrumbItemHistorySessionBean");
      if (bihsb.getCurrentItem().getDisplayValue().equals("AdvancedSearchPage")) {
        bihsb.getCurrentItem().setPage("AdvancedSearchPage.jsp?q=" + URLEncoder.encode(this.query, "UTF-8"));
      } else if (bihsb.getCurrentItem().getDisplayValue().equals("AdminAdvancedSearchPage")) {
        bihsb.getCurrentItem().setPage("AdminAdvancedSearchPage.jsp?q=" + URLEncoder.encode(this.query, "UTF-8"));
      }
      FacesTools.getExternalContext().redirect("SearchResultListPage.jsp?q=" + URLEncoder.encode(this.query, "UTF-8") + "&"
          + SearchRetrieverRequestBean.parameterSearchType + "=" + searchType);
    } catch (final Exception e) {
      AdvancedSearchBean.logger.error("Error while redirecting to search result page", e);
    }
  }

  public void startAdminSearch() {
    this.startSearch(Index.ITEM_CONTAINER_ADMIN);
  }

  public void startSearch() {
    this.startSearch(Index.ESCIDOC_ALL);
  }

  public List<SearchCriterionBase> getComponentSearchCriterions(Index indexName) {
    final List<SearchCriterionBase> returnList = new ArrayList<SearchCriterionBase>();
    returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    returnList.add(this.getFileSectionSearchCriterion());
    returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    returnList.add(this.getLocatorSectionSearchCriterion());

    return returnList;
  }

  public List<SearchCriterionBase> getItemStatusSearchCriterions() {
    final List<SearchCriterionBase> returnList = new ArrayList<SearchCriterionBase>();
    returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));

    return returnList;
  }

  public void setContextListMenu(List<SelectItem> contextListMenu) {
    this.contextListMenu = contextListMenu;
  }

  public List<SelectItem> getContentCategoryListMenu() {
    return this.contentCategoryListMenu;
  }

  public void setContentCategoryListMenu(List<SelectItem> contentCategoryListMenu) {
    this.contentCategoryListMenu = contentCategoryListMenu;
  }

  public List<SelectItem> getOaStatusListMenu() {
    return this.oaStatusListMenu;
  }

  public void setOaStatutsListMenu(List<SelectItem> oaStatusListMenu) {
    this.oaStatusListMenu = oaStatusListMenu;
  }

  public List<SelectItem> getComponentVisibilityListMenu() {
    return this.componentVisibilityListMenu;
  }

  public void setComponentVisibilityListMenu(List<SelectItem> componentVisibilityListMenu) {
    this.componentVisibilityListMenu = componentVisibilityListMenu;
  }

  public List<SelectItem> getSubjectTypesListMenu() {
    return this.subjectTypesListMenu;
  }

  public void setSubjectTypesListMenu(List<SelectItem> subjectTypesListMenu) {
    this.subjectTypesListMenu = subjectTypesListMenu;
  }

  public Parenthesis getCurrentlyOpenedParenthesis() {
    return this.currentlyOpenedParenthesis;
  }

  public void setCurrentlyOpenedParenthesis(Parenthesis currentlyOpenedParenthesis) {
    this.currentlyOpenedParenthesis = currentlyOpenedParenthesis;
  }

  public Map<SearchCriterionBase, Boolean> getPossibleCriterionsForClosingParenthesisMap() {
    return this.possibleCriterionsForClosingParenthesisMap;
  }

  public void setPossibleCriterionsForClosingParenthesisMap(Map<SearchCriterionBase, Boolean> possibleCriterionsForClosingParenthesisMap) {
    this.possibleCriterionsForClosingParenthesisMap = possibleCriterionsForClosingParenthesisMap;
  }

  public Map<SearchCriterionBase, Integer> getBalanceMap() {
    return this.balanceMap;
  }

  public void setBalanceMap(Map<SearchCriterionBase, Integer> balanceMap) {
    this.balanceMap = balanceMap;
  }

  public SearchCriterionBase getGenreListSearchCriterion() {
    return this.genreListSearchCriterion;
  }

  public void setGenreListSearchCriterion(SearchCriterionBase genreListSearchCriterion) {
    this.genreListSearchCriterion = genreListSearchCriterion;
  }

  public List<SelectItem> getGenreListMenu() {
    return this.genreListMenu;
  }

  public void setGenreListMenu(List<SelectItem> genreListMenu) {
    this.genreListMenu = genreListMenu;
  }

  public boolean isExcludeComponentContentCategory() {
    return this.excludeComponentContentCategory;
  }

  public void setExcludeComponentContentCategory(boolean excludeComponentContentCategory) {
    this.excludeComponentContentCategory = excludeComponentContentCategory;
  }

  public int getNumberOfSearchCriterions() {
    return this.numberOfSearchCriterions;
  }

  public void setNumberOfSearchCriterions(int numberOfSearchCriterions) {
    this.numberOfSearchCriterions = numberOfSearchCriterions;
  }

  public void removeAutoSuggestValues(int position) {
    final SearchCriterionBase sc = this.criterionList.get(position);
    if (sc instanceof StringOrHiddenIdSearchCriterion) {
      final StringOrHiddenIdSearchCriterion hiddenSc = (StringOrHiddenIdSearchCriterion) sc;
      hiddenSc.setHiddenId(null);
      hiddenSc.setSearchString(null);
    }
  }

  public List<SelectItem> getReviewMethodListMenu() {
    return this.reviewMethodListMenu;
  }

  public void setReviewMethodListMenu(List<SelectItem> reviewMethodListMenu) {
    this.reviewMethodListMenu = reviewMethodListMenu;
  }

  @Override
  public void languageChanged(String oldLang, String newLang) {
    this.clearAndInit();

    this.criterionTypeListMenu = this.initCriterionTypeListMenu(Index.ESCIDOC_ALL); //
    this.setCriterionTypeListMenuAdmin(this.initCriterionTypeListMenu(Index.ITEM_CONTAINER_ADMIN)); //
    this.operatorTypeListMenu = this.initOperatorListMenu();
    this.genreListMenu = this.initGenreListMenu(); //
    this.reviewMethodListMenu = this.initReviewMethodListMenu();
    this.contentCategoryListMenu = this.initContentCategoryListMenu(); //
    this.oaStatusListMenu = this.initOaStatusListMenu(); //
    this.componentVisibilityListMenu = this.initComponentVisibilityListMenu(); //
    this.subjectTypesListMenu = this.initSubjectTypesListMenu();
    this.identifierTypesListMenu = this.initIdentifierTypesListMenu();
    this.personRoleMenu = this.initPersonRoleMenu();

    // if langugage is changed on AdvancedSearchPage, set flag
    try {
      final String viewId = FacesTools.getCurrentInstance().getViewRoot().getViewId();
      if ("/AdvancedSearchPage.jsp".equals(viewId)) {
        this.languageChanged = true;
      }
    } catch (final Exception e) {
      AdvancedSearchBean.logger.warn("Problem reading view id", e);
    }
  }

  public String getQuery() {
    return this.query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public SearchCriterionBase getItemStateListSearchCriterion() {
    return this.itemStateListSearchCriterion;
  }

  public void setItemStateListSearchCriterion(SearchCriterionBase itemStateListSearchCriterion) {
    this.itemStateListSearchCriterion = itemStateListSearchCriterion;
  }

  public SearchCriterionBase getAffiliatedContextListSearchCriterion() {
    return this.affiliatedContextListSearchCriterion;
  }

  public void setAffiliatedContextListSearchCriterion(SearchCriterionBase affiliatedContextListSearchCriterion) {
    this.affiliatedContextListSearchCriterion = affiliatedContextListSearchCriterion;
  }

  public List<SelectItem> getCriterionTypeListMenuAdmin() {
    return this.criterionTypeListMenuAdmin;
  }

  public void setCriterionTypeListMenuAdmin(List<SelectItem> criterionTypeListMenuAdmin) {
    this.criterionTypeListMenuAdmin = criterionTypeListMenuAdmin;
  }

  public SearchCriterionBase getPublicationStatusListSearchCriterion() {
    return this.publicationStatusListSearchCriterion;
  }

  public void setPublicationStatusListSearchCriterion(SearchCriterionBase publicationStatusListSearchCriterion) {
    this.publicationStatusListSearchCriterion = publicationStatusListSearchCriterion;
  }

  public List<SelectItem> getPersonRoleMenu() {
    return personRoleMenu;
  }

  public void setPersonRoleMenu(List<SelectItem> personRoleMenu) {
    this.personRoleMenu = personRoleMenu;
  }

  public SearchCriterionBase getFileSectionSearchCriterion() {
    return fileSectionSearchCriterion;
  }

  public void setFileSectionSearchCriterion(SearchCriterionBase fileSectionSearchCriterion) {
    this.fileSectionSearchCriterion = fileSectionSearchCriterion;
  }

  public SearchCriterionBase getLocatorSectionSearchCriterion() {
    return locatorSectionSearchCriterion;
  }

  public void setLocatorSectionSearchCriterion(SearchCriterionBase locatorSectionSearchCriterion) {
    this.locatorSectionSearchCriterion = locatorSectionSearchCriterion;
  }

  public List<SelectItem> getIdentifierTypesListMenu() {
    return identifierTypesListMenu;
  }

  public void setIdentifierTypesListMenu(List<SelectItem> identifierTypesListMenu) {
    this.identifierTypesListMenu = identifierTypesListMenu;
  }
}
