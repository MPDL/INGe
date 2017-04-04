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

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.pubman.PubItemService;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase.DisplayType;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase.Index;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox.AffiliatedContextListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox.EmbargoDateAvailableSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox.ItemStateListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox.PublicationStatusListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.ComponentContentCategoryListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.ComponentVisibilityListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.FileAvailableSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.LocatorAvailableSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.dates.DateSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.genre.GenreListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.Parenthesis;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.CollectionSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.ComponentContentCategory;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.ComponentVisibilitySearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.StandardSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.TitleSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.CreatedBySearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.ModifiedBySearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.OrganizationSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.PersonSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.StringOrHiddenIdSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.LanguageChangeObserver;
import de.mpg.mpdl.inge.pubman.web.util.converter.SelectItemComparator;
import de.mpg.mpdl.inge.util.PropertyReader;

@ManagedBean(name = "AdvancedSearchBean")
@SessionScoped
@SuppressWarnings("serial")
public class AdvancedSearchBean extends FacesBean implements Serializable, LanguageChangeObserver {
  private static final Logger logger = Logger.getLogger(AdvancedSearchBean.class);

  private List<SearchCriterionBase> criterionList;

  private List<SelectItem> componentVisibilityListMenu = this.initComponentVisibilityListMenu();
  private List<SelectItem> contentCategoryListMenu = this.initContentCategoryListMenu();
  private List<SelectItem> contextListMenu;
  private List<SelectItem> criterionTypeListMenu = this
      .initCriterionTypeListMenu(Index.ESCIDOC_ALL);
  private List<SelectItem> criterionTypeListMenuAdmin = this
      .initCriterionTypeListMenu(Index.ITEM_CONTAINER_ADMIN);
  private List<SelectItem> genreListMenu = this.initGenreListMenu();
  private List<SelectItem> operatorTypeListMenu = this.initOperatorListMenu();
  private List<SelectItem> reviewMethodListMenu = this.initReviewMethodListMenu();
  private List<SelectItem> subjectTypesListMenu = this.initSubjectTypesListMenu();
  private List<SelectItem> personRoleMenu = this.initPersonRoleMenu();

  private Map<SearchCriterionBase, Boolean> possibleCriterionsForClosingParenthesisMap =
      new HashMap<SearchCriterionBase, Boolean>();
  private Map<SearchCriterionBase, Integer> balanceMap =
      new HashMap<SearchCriterionBase, Integer>();

  private Parenthesis currentlyOpenedParenthesis;

  private SearchCriterionBase affiliatedContextListSearchCriterion;
  private SearchCriterionBase componentContentCategory;
  private SearchCriterionBase componentContentCategoryListSearchCriterion;
  private SearchCriterionBase componentEmbargoDateAvailableSearchCriterion;
  private SearchCriterionBase componentEmbargoDateSearchCriterion;
  private SearchCriterionBase componentVisibilityListSearchCriterion;
  private SearchCriterionBase componentVisibilitySearchCriterion;
  private SearchCriterionBase fileAvailableSearchCriterion;
  private SearchCriterionBase genreListSearchCriterion;
  private SearchCriterionBase itemStateListSearchCriterion;
  private SearchCriterionBase locatorAvailableSearchCriterion;
  private SearchCriterionBase publicationStatusListSearchCriterion;

  private String query = "";

  private boolean excludeComponentContentCategory;
  private boolean languageChanged;

  private int numberOfSearchCriterions;

  private String suggestConeUrl;

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
    this.componentContentCategory = new ComponentContentCategory();
    this.componentContentCategoryListSearchCriterion =
        new ComponentContentCategoryListSearchCriterion();
    this.componentEmbargoDateAvailableSearchCriterion = new EmbargoDateAvailableSearchCriterion();
    this.componentEmbargoDateSearchCriterion =
        new DateSearchCriterion(SearchCriterion.COMPONENT_EMBARGO_DATE);
    this.componentVisibilityListSearchCriterion = new ComponentVisibilityListSearchCriterion();
    this.componentVisibilitySearchCriterion = new ComponentVisibilitySearchCriterion();
    this.currentlyOpenedParenthesis = null;
    this.excludeComponentContentCategory = false;
    this.fileAvailableSearchCriterion = new FileAvailableSearchCriterion();
    this.genreListSearchCriterion = new GenreListSearchCriterion();
    this.itemStateListSearchCriterion = new ItemStateListSearchCriterion();
    this.locatorAvailableSearchCriterion = new LocatorAvailableSearchCriterion();
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
      if (SearchCriterion.FILE_AVAILABLE.equals(sc.getSearchCriterion())) {
        this.fileAvailableSearchCriterion = sc;
        toBeRemovedList.add(sc);
      } else if (SearchCriterion.LOCATOR_AVAILABLE.equals(sc.getSearchCriterion())) {
        this.locatorAvailableSearchCriterion = sc;
        toBeRemovedList.add(sc);
      } else if (SearchCriterion.EMBARGO_DATE_AVAILABLE.equals(sc.getSearchCriterion())) {
        this.componentEmbargoDateAvailableSearchCriterion = sc;
        toBeRemovedList.add(sc);
      }
      /*
       * else if(SearchCriterion.COMPONENT_CONTENT_CATEGORY.equals(sc.getSearchCriterion())) {
       * this.componentContentCategory = sc; toBeRemovedList.add(sc); if(i>0 &&
       * SearchCriterion.NOT_OPERATOR.equals(scList.get(i-1).getSearchCriterion())) {
       * this.excludeComponentContentCategory = true; }
       * 
       * }
       */
      else if (SearchCriterion.COMPONENT_CONTENT_CATEGORY_LIST.equals(sc.getSearchCriterion())) {
        this.componentContentCategoryListSearchCriterion = sc;
        toBeRemovedList.add(sc);
      }
      /*
       * else if(SearchCriterion.COMPONENT_VISIBILITY.equals(sc.getSearchCriterion())) {
       * this.componentVisibilitySearchCriterion = sc; toBeRemovedList.add(sc); }
       */
      else if (SearchCriterion.COMPONENT_VISIBILITY_LIST.equals(sc.getSearchCriterion())) {
        this.componentVisibilityListSearchCriterion = sc;
        toBeRemovedList.add(sc);
      } else if (SearchCriterion.COMPONENT_EMBARGO_DATE.equals(sc.getSearchCriterion())) {
        this.componentEmbargoDateSearchCriterion = sc;
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

  private List<SelectItem> initGenreListMenu() {
    return Arrays.asList(this.getI18nHelper().getSelectItemsGenre());
  }

  private List<SelectItem> initReviewMethodListMenu() {
    return Arrays.asList(this.getI18nHelper().getSelectItemsReviewMethod());
  }

  private List<SelectItem> initSubjectTypesListMenu() {
    final List<SelectItem> vocabs = new ArrayList<SelectItem>();
    try {
      final String vocabsStr = PropertyReader.getProperty("escidoc.cone.subjectVocab");
      final String[] vocabsArr = vocabsStr.split(";");
      for (int i = 0; i < vocabsArr.length; i++) {
        final String type = vocabsArr[i].trim().toUpperCase().replace("-", "_");
        final String label = vocabsArr[i].trim().toUpperCase();
        final SelectItem si = new SelectItem(type, label);
        vocabs.add(si);
      }
    } catch (final Exception e) {
      AdvancedSearchBean.logger.error("Could not read Property: 'escidoc.cone.subjectVocab'", e);
    }
    return vocabs;
  }


  private List<SelectItem> initPersonRoleMenu() {

    final List<SelectItem> personRoleMenu = new ArrayList<SelectItem>();

    personRoleMenu.add(new SelectItem(null, this.getLabel("adv_search_lblSearchPerson")));
    for (final CreatorRole role : CreatorRole.values()) {
      personRoleMenu.add(new SelectItem(role.name(), this.getLabel("ENUM_CREATORROLE_"
          + role.name())));
    }
    return personRoleMenu;

  }

  private List<SelectItem> initCriterionTypeListMenu(Index indexName) {
    final List<SelectItem> criterionTypeList = new ArrayList<SelectItem>();
    // General
    criterionTypeList.add(new SelectItem(SearchCriterion.TITLE, this
        .getLabel("adv_search_lblRgbTitle")));
    criterionTypeList.add(new SelectItem(SearchCriterion.KEYWORD, this
        .getLabel("adv_search_lblRgbTopic")));
    criterionTypeList.add(new SelectItem(SearchCriterion.CLASSIFICATION, this
        .getLabel("adv_search_lblClassification")));
    criterionTypeList
        .add(new SelectItem(SearchCriterion.ANY, this.getLabel("adv_search_lblRgbAny")));
    criterionTypeList.add(new SelectItem(SearchCriterion.ANYFULLTEXT, this
        .getLabel("adv_search_lblRgbAnyFulltext")));

    // AdminStuff
    if (indexName == Index.ITEM_CONTAINER_ADMIN) {
      final List<SelectItem> adminGroupList = new ArrayList<SelectItem>();
      final SelectItemGroup adminGroup =
          new SelectItemGroup(this.getLabel("adv_search_lblSearchAdmin"));
      adminGroupList.add(new SelectItem(SearchCriterion.CREATED_INTERNAL, this
          .getLabel("adv_search_lblItemCreationDate")));
      adminGroupList.add(new SelectItem(SearchCriterion.MODIFIED_INTERNAL, this
          .getLabel("adv_search_lblItemLastModificationDate")));

      adminGroupList.add(new SelectItem(SearchCriterion.CREATED_BY, this
          .getLabel("adv_search_lblItemCreatedBy")));
      adminGroupList.add(new SelectItem(SearchCriterion.MODIFIED_BY, this
          .getLabel("adv_search_lblItemModifiedBy")));

      adminGroup.setSelectItems(adminGroupList.toArray(new SelectItem[0]));
      criterionTypeList.add(adminGroup);
    }

    // Persons
    /*
     * final List<SelectItem> personGroupList = new ArrayList<SelectItem>(); personGroupList.add(new
     * SelectItem(SearchCriterion.ANYPERSON, this .getLabel("adv_search_lblSearchPerson")));
     * 
     * for (final CreatorRole role : CreatorRole.values()) { personGroupList.add(new
     * SelectItem(SearchCriterion.valueOf(role.name()), this .getLabel("ENUM_CREATORROLE_" +
     * role.name()))); }
     */

    /*
     * personGroupList.add(new SelectItem(SearchCriterion.AUTHOR,
     * getLabel("ENUM_CREATORROLE_AUTHOR"))); personGroupList.add(new
     * SelectItem(SearchCriterion.EDITOR,getLabel("ENUM_CREATORROLE_EDITOR")));
     * personGroupList.add(new
     * SelectItem(SearchCriterion.ADVISOR,getLabel("ENUM_CREATORROLE_ADVISOR")));
     * personGroupList.add(new
     * SelectItem(SearchCriterion.ARTIST,getLabel("ENUM_CREATORROLE_ARTIST")));
     * personGroupList.add(new
     * SelectItem(SearchCriterion.COMMENTATOR,getLabel("ENUM_CREATORROLE_COMMENTATOR")));
     * personGroupList.add(new
     * SelectItem(SearchCriterion.CONTRIBUTOR,getLabel("ENUM_CREATORROLE_CONTRIBUTOR")));
     * personGroupList.add(new SelectItem(SearchCriterion.ILLUSTRATOR,
     * getLabel("ENUM_CREATORROLE_ILLUSTRATOR"))); personGroupList.add(new
     * SelectItem(SearchCriterion.PAINTER, getLabel("ENUM_CREATORROLE_PAINTER")));
     * personGroupList.add(new SelectItem(SearchCriterion.PHOTOGRAPHER,
     * getLabel("ENUM_CREATORROLE_PHOTOGRAPHER"))); personGroupList.add(new
     * SelectItem(SearchCriterion.TRANSCRIBER, getLabel("ENUM_CREATORROLE_TRANSCRIBER")));
     * personGroupList.add(new SelectItem(SearchCriterion.TRANSLATOR,
     * getLabel("ENUM_CREATORROLE_TRANSLATOR"))); personGroupList.add(new
     * SelectItem(SearchCriterion.HONOREE, getLabel("ENUM_CREATORROLE_HONOREE")));
     * personGroupList.add(new SelectItem(SearchCriterion.INVENTOR,
     * getLabel("ENUM_CREATORROLE_INVENTOR"))); personGroupList.add(new
     * SelectItem(SearchCriterion.APPLICANT, getLabel("ENUM_CREATORROLE_APPLICANT")));
     */
    /*
     * final SelectItemGroup personGroup = new
     * SelectItemGroup(this.getLabel("adv_search_lblSearchPerson"));
     * personGroup.setSelectItems(personGroupList.toArray(new SelectItem[0]));
     */

    criterionTypeList.add(new SelectItem(SearchCriterion.ANYPERSON, this
        .getLabel("adv_search_lblSearchPerson")));


    // Organisation
    criterionTypeList.add(new SelectItem(SearchCriterion.ORGUNIT, this
        .getLabel("adv_search_lbHeaderOrgan")));

    // Dates
    final List<SelectItem> dateGroupList = new ArrayList<SelectItem>();
    dateGroupList.add(new SelectItem(SearchCriterion.ANYDATE, this
        .getLabel("adv_search_lbHeaderDate")));
    dateGroupList.add(new SelectItem(SearchCriterion.PUBLISHEDPRINT, this
        .getLabel("adv_search_lblChkType_abb_publishedpr")));
    dateGroupList.add(new SelectItem(SearchCriterion.PUBLISHED, this
        .getLabel("adv_search_lblChkType_publishedon")));
    dateGroupList.add(new SelectItem(SearchCriterion.ACCEPTED, this
        .getLabel("adv_search_lblChkType_accepted")));
    dateGroupList.add(new SelectItem(SearchCriterion.SUBMITTED, this
        .getLabel("adv_search_lblChkType_submitted")));
    dateGroupList.add(new SelectItem(SearchCriterion.MODIFIED, this
        .getLabel("adv_search_lblChkType_modified")));
    dateGroupList.add(new SelectItem(SearchCriterion.CREATED, this
        .getLabel("adv_search_lblChkType_created")));

    final SelectItemGroup dateGroup = new SelectItemGroup(this.getLabel("adv_search_lbHeaderDate"));
    dateGroup.setSelectItems(dateGroupList.toArray(new SelectItem[0]));
    criterionTypeList.add(dateGroup);


    // Event
    final List<SelectItem> eventGroupList = new ArrayList<SelectItem>();
    eventGroupList.add(new SelectItem(SearchCriterion.EVENT, this
        .getLabel("adv_search_lbHeaderEvent")));
    eventGroupList.add(new SelectItem(SearchCriterion.EVENT_STARTDATE, this
        .getLabel("adv_search_lblChkType_abb_event_start_date")));
    eventGroupList.add(new SelectItem(SearchCriterion.EVENT_ENDDATE, this
        .getLabel("adv_search_lblChkType_abb_event_end_date")));
    eventGroupList.add(new SelectItem(SearchCriterion.EVENT_INVITATION, this
        .getLabel("ENUM_INVITATIONSTATUS_INVITED")));

    final SelectItemGroup eventGroup =
        new SelectItemGroup(this.getLabel("adv_search_lbHeaderEvent"));
    eventGroup.setSelectItems(eventGroupList.toArray(new SelectItem[0]));
    criterionTypeList.add(eventGroup);



    // Genre
    criterionTypeList.add(new SelectItem(SearchCriterion.GENRE, this
        .getLabel("adv_search_lbHeaderGenre")));

    criterionTypeList.add(new SelectItem(SearchCriterion.REVIEW_METHOD, this
        .getLabel("ViewItemFull_lblRevisionMethod")));

    // Language
    criterionTypeList.add(new SelectItem(SearchCriterion.LANG, this
        .getLabel("adv_search_lblLanguageTerm")));

    // Source
    criterionTypeList.add(new SelectItem(SearchCriterion.SOURCE, this
        .getLabel("adv_search_lbHeaderSource")));
    criterionTypeList.add(new SelectItem(SearchCriterion.JOURNAL, " - "
        + this.getLabel("adv_search_lblSourceJournal")));

    // LocalTag
    criterionTypeList.add(new SelectItem(SearchCriterion.LOCAL, this
        .getLabel("adv_search_lbHeaderLocalTag")));

    // Identifier
    criterionTypeList.add(new SelectItem(SearchCriterion.IDENTIFIER, this
        .getLabel("adv_search_lbHeaderIdent")));


    // Collection
    criterionTypeList.add(new SelectItem(SearchCriterion.COLLECTION, this
        .getLabel("adv_search_lbHeaderCollection")));

    // ProjectInfo
    criterionTypeList.add(new SelectItem(SearchCriterion.PROJECT_INFO, this
        .getLabel("g_project_info")));

    return criterionTypeList;

  }

  private List<SelectItem> initOperatorListMenu() {
    final List<SelectItem> operatorTypeList = new ArrayList<SelectItem>();

    // General
    operatorTypeList.add(new SelectItem(SearchCriterion.AND_OPERATOR, this
        .getLabel("adv_search_logicop_and")));
    operatorTypeList.add(new SelectItem(SearchCriterion.OR_OPERATOR, this
        .getLabel("adv_search_logicop_or")));
    operatorTypeList.add(new SelectItem(SearchCriterion.NOT_OPERATOR, this
        .getLabel("adv_search_logicop_not")));


    return operatorTypeList;

  }



  /*
   * public void changeCriterion(ValueChangeEvent evt) {
   * 
   * Integer position = (Integer) evt.getComponent().getAttributes().get("indexOfCriterion");
   * SearchCriterion newValue = (SearchCriterion)evt.getNewValue(); if(newValue != null &&
   * position!=null) { logger.debug("Changing sortCriteria at position " + position + " to " +
   * newValue);
   * 
   * SearchCriterionBase oldSearchCriterion = criterionList.remove(position.intValue());
   * SearchCriterionBase newSearchCriterion = SearchCriterionBase.initSearchCriterion(newValue);
   * newSearchCriterion.setLevel(oldSearchCriterion.getLevel());
   * if(possibleCriterionsForClosingParenthesisMap.containsKey(oldSearchCriterion)) { boolean
   * oldValue = possibleCriterionsForClosingParenthesisMap.get(oldSearchCriterion);
   * possibleCriterionsForClosingParenthesisMap.remove(oldSearchCriterion);
   * possibleCriterionsForClosingParenthesisMap.put(newSearchCriterion, oldValue); }
   * criterionList.add(position, newSearchCriterion); logger.info("New criterion list:" +
   * criterionList); }
   * 
   * 
   * 
   * }
   */


  public void copyValuesFromOldToNew(SearchCriterionBase oldSc, SearchCriterionBase newSc) {
    if (oldSc instanceof PersonSearchCriterion && newSc instanceof PersonSearchCriterion) {
      ((PersonSearchCriterion) newSc).setSearchString(((PersonSearchCriterion) oldSc)
          .getSearchString());
      ((PersonSearchCriterion) newSc).setHiddenId(((PersonSearchCriterion) oldSc).getHiddenId());
    }

    else if (oldSc instanceof DateSearchCriterion && newSc instanceof DateSearchCriterion) {
      ((DateSearchCriterion) newSc).setFrom(((DateSearchCriterion) oldSc).getFrom());
      ((DateSearchCriterion) newSc).setTo(((DateSearchCriterion) oldSc).getTo());
    }

    else if (oldSc instanceof StandardSearchCriterion && newSc instanceof StandardSearchCriterion
        && !(oldSc instanceof CollectionSearchCriterion)
        && !(newSc instanceof CollectionSearchCriterion)) {
      ((StandardSearchCriterion) newSc).setSearchString(((StandardSearchCriterion) oldSc)
          .getSearchString());

    } else if ((oldSc instanceof CreatedBySearchCriterion && newSc instanceof ModifiedBySearchCriterion)
        || (oldSc instanceof ModifiedBySearchCriterion && newSc instanceof CreatedBySearchCriterion)) {
      ((StringOrHiddenIdSearchCriterion) newSc)
          .setHiddenId(((StringOrHiddenIdSearchCriterion) oldSc).getHiddenId());
      ((StringOrHiddenIdSearchCriterion) newSc)
          .setSearchString(((StringOrHiddenIdSearchCriterion) oldSc).getSearchString());
    }
  }

  public void changeCriterionAction(ValueChangeEvent evt) {
    final Integer position = (Integer) evt.getComponent().getAttributes().get("indexOfCriterion");

    if (evt.getNewValue() != null && position != null) {

      final SearchCriterion newValue = SearchCriterion.valueOf(evt.getNewValue().toString());
      AdvancedSearchBean.logger.debug("Changing sortCriteria at position " + position + " to "
          + newValue);

      final SearchCriterionBase oldSearchCriterion = this.criterionList.remove(position.intValue());
      final SearchCriterionBase newSearchCriterion =
          SearchCriterionBase.initSearchCriterion(newValue);
      newSearchCriterion.setLevel(oldSearchCriterion.getLevel());
      if (this.possibleCriterionsForClosingParenthesisMap.containsKey(oldSearchCriterion)) {
        final boolean oldValue =
            this.possibleCriterionsForClosingParenthesisMap.get(oldSearchCriterion);
        this.possibleCriterionsForClosingParenthesisMap.remove(oldSearchCriterion);
        this.possibleCriterionsForClosingParenthesisMap.put(newSearchCriterion, oldValue);
      }
      this.copyValuesFromOldToNew(oldSearchCriterion, newSearchCriterion);
      this.criterionList.add(position, newSearchCriterion);
      // logger.info("New criterion list:" + criterionList);
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
    // Integer position = (Integer) ae.getComponent().getAttributes().get("indexOfCriterion");
    final SearchCriterionBase oldSearchCriterion = this.criterionList.get(position);

    // If the add button of a Parenthesis is used, add an ANYFIELD Criterion, else add the same
    // criterion as in the line of the add button.
    SearchCriterionBase newSearchCriterion = null;
    if (DisplayType.PARENTHESIS.equals(oldSearchCriterion.getSearchCriterion().getDisplayType())) {
      newSearchCriterion = SearchCriterionBase.initSearchCriterion(SearchCriterion.ANY);
    } else {
      newSearchCriterion =
          SearchCriterionBase.initSearchCriterion(oldSearchCriterion.getSearchCriterion());
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
    // Integer position = (Integer) ae.getComponent().getAttributes().get("indexOfCriterion");
    final SearchCriterionBase sc = this.criterionList.get(position);
    SearchCriterionBase.removeSearchCriterionWithOperator(this.criterionList, sc);
    this.updateListForClosingParenthesis(this.currentlyOpenedParenthesis);

  }

  public void addOpeningParenthesis(int position) {
    // Integer position = (Integer) ae.getComponent().getAttributes().get("indexOfCriterion");
    this.currentlyOpenedParenthesis = new Parenthesis(SearchCriterion.OPENING_PARENTHESIS);
    this.currentlyOpenedParenthesis.setLevel(this.criterionList.get(position).getLevel());
    // add before criterion
    this.criterionList.add(position, this.currentlyOpenedParenthesis);
    this.updateListForClosingParenthesis(this.currentlyOpenedParenthesis);
  }

  public void addClosingParenthesis(int position) {
    // Integer position = (Integer) ae.getComponent().getAttributes().get("indexOfCriterion");
    final Parenthesis closingParenthesis = new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS);
    this.currentlyOpenedParenthesis.setPartnerParenthesis(closingParenthesis);
    closingParenthesis.setPartnerParenthesis(this.currentlyOpenedParenthesis);
    this.currentlyOpenedParenthesis = null;
    this.criterionList.add(position + 1, closingParenthesis);
    this.updateListForClosingParenthesis(null);
  }

  public void removeParenthesis(int position) {
    // Integer position = (Integer) ae.getComponent().getAttributes().get("indexOfCriterion");
    final Parenthesis parenthesis = (Parenthesis) this.criterionList.get(position);
    final Parenthesis partnerParenthesis = parenthesis.getPartnerParenthesis();

    this.criterionList.remove(parenthesis);
    this.criterionList.remove(partnerParenthesis);


    if (parenthesis.equals(this.currentlyOpenedParenthesis)) {
      this.currentlyOpenedParenthesis = null;
    }

    // this.currentlyOpenedParenthesis = null;
    this.updateListForClosingParenthesis(this.currentlyOpenedParenthesis);

  }


  private void updateListForClosingParenthesis(SearchCriterionBase startParenthesis) {
    this.possibleCriterionsForClosingParenthesisMap.clear();
    int balanceCounter = 0;
    boolean lookForClosingParenthesis = false;
    int startParenthesisBalance = 0;
    // int pos = 0;

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

      if (lookForClosingParenthesis
          && !DisplayType.OPERATOR.equals(sc.getSearchCriterion().getDisplayType())
          && balanceCounter == startParenthesisBalance + 1) {
        this.possibleCriterionsForClosingParenthesisMap.put(sc, true);
      }


      if (!DisplayType.OPERATOR.equals(sc.getSearchCriterion().getDisplayType())
          && !DisplayType.PARENTHESIS.equals(sc.getSearchCriterion().getDisplayType())) {
        this.numberOfSearchCriterions++;
      }
      // pos++;
    }

    // logger.info("number of search criterions: " + numberOfSearchCriterions);


    /*
     * this.possibleCriterionsForClosingParenthesisMap.clear(); if(startParenthesis != null) { int
     * pos = criterionList.indexOf(startParenthesis); int balanceCounter = 0;
     * 
     * for(int i=pos; i<criterionList.size(); i++) { SearchCriterionBase sc = criterionList.get(i);
     * 
     * sc.setLevel(sc.getLevel() + balanceCounter);
     * 
     * if(SearchCriterion.OPENING_PARENTHESIS.equals(sc.getSearchCriterion())) { balanceCounter++; }
     * else if (SearchCriterion.CLOSING_PARENTHESIS.equals(sc.getSearchCriterion())) {
     * balanceCounter--; }
     * 
     * 
     * 
     * 
     * 
     * if(!DisplayType.OPERATOR.equals(sc.getSearchCriterion().getDisplayType()) && balanceCounter
     * == 1) { possibleCriterionsForClosingParenthesisMap.put(sc, true); } }
     * 
     * }
     */
  }


  public List<SelectItem> getContextListMenu() throws Exception {

    if (this.contextListMenu == null) {

      try {

        final List<ContextVO> contexts = PubItemService.getPubCollectionListForDepositing();

        this.contextListMenu = new ArrayList<SelectItem>();

        for (final ContextVO c : contexts) {
          this.contextListMenu.add(new SelectItem(c.getReference().getObjectId(), c.getName()));
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

  public void startSearch(Index indexName) {
    if (this.currentlyOpenedParenthesis != null) {
      FacesBean.error(this.getMessage("search_ParenthesisNotClosed"));
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


    final List<SearchCriterionBase> componentSearchCriterions =
        this.getComponentSearchCriterions(indexName);
    allCriterions.addAll(componentSearchCriterions);

    QueryBuilder qb = null;
    try {
      // cql = SearchCriterionBase.scListToCql(indexName, allCriterions, true);

      qb = SearchCriterionBase.scListToElasticSearchQuery(allCriterions);
      AdvancedSearchBean.logger.info(qb.toString());

    } catch (final SearchParseException e1) {
      FacesBean.error(this.getMessage("search_ParseError"));

    }

    this.query = SearchCriterionBase.scListToQueryString(allCriterions);
    AdvancedSearchBean.logger.debug("Internal Query: " + this.query);

    if (this.query == null || this.query.trim().isEmpty()) {
      FacesBean.error(this.getMessage("search_NoCriteria"));
    }


    String searchType = "advanced";
    if (Index.ITEM_CONTAINER_ADMIN == indexName) {
      searchType = "admin";
    }

    try {
      final BreadcrumbItemHistorySessionBean bihsb =
          (BreadcrumbItemHistorySessionBean) FacesTools
              .findBean("BreadcrumbItemHistorySessionBean");
      if (bihsb.getCurrentItem().getDisplayValue().equals("AdvancedSearchPage")) {
        bihsb.getCurrentItem().setPage(
            "AdvancedSearchPage.jsp?q=" + URLEncoder.encode(this.query, "UTF-8"));
      } else if (bihsb.getCurrentItem().getDisplayValue().equals("AdminAdvancedSearchPage")) {
        bihsb.getCurrentItem().setPage(
            "AdminAdvancedSearchPage.jsp?q=" + URLEncoder.encode(this.query, "UTF-8"));
      }
      FacesTools.getExternalContext().redirect(
          "SearchResultListPage.jsp?esq=" + URLEncoder.encode(qb.toString(), "UTF-8") + "&q="
              + URLEncoder.encode(this.query, "UTF-8") + "&"
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
    returnList.add(this.fileAvailableSearchCriterion);
    returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    returnList.add(this.locatorAvailableSearchCriterion);
    returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    returnList.add(this.componentEmbargoDateAvailableSearchCriterion);
    returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    returnList.add(this.componentEmbargoDateSearchCriterion);

    if (this.excludeComponentContentCategory) {
      returnList.add(new LogicalOperator(SearchCriterion.NOT_OPERATOR));
    } else {
      returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    }

    returnList.add(this.componentContentCategoryListSearchCriterion);
    returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    returnList.add(this.componentVisibilityListSearchCriterion);

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

  public SearchCriterionBase getFileAvailableSearchCriterion() {
    return this.fileAvailableSearchCriterion;
  }

  public void setFileAvailableSearchCriterion(SearchCriterionBase fileAvailableSearchCriterion) {
    this.fileAvailableSearchCriterion = fileAvailableSearchCriterion;
  }


  public SearchCriterionBase getLocatorAvailableSearchCriterion() {
    return this.locatorAvailableSearchCriterion;
  }


  public void setLocatorAvailableSearchCriterion(SearchCriterionBase locatorAvailableSearchCriterion) {
    this.locatorAvailableSearchCriterion = locatorAvailableSearchCriterion;
  }



  public SearchCriterionBase getComponentContentCategory() {
    return this.componentContentCategory;
  }


  public void setComponentContentCategory(SearchCriterionBase componentContentCategory) {
    this.componentContentCategory = componentContentCategory;
  }


  public List<SelectItem> getContentCategoryListMenu() {
    return this.contentCategoryListMenu;
  }


  public void setContentCategoryListMenu(List<SelectItem> contentCategoryListMenu) {
    this.contentCategoryListMenu = contentCategoryListMenu;
  }


  public SearchCriterionBase getComponentVisibilitySearchCriterion() {
    return this.componentVisibilitySearchCriterion;
  }


  public void setComponentVisibilitySearchCriterion(
      SearchCriterionBase componentVisibilitySearchCriterion) {
    this.componentVisibilitySearchCriterion = componentVisibilitySearchCriterion;
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



  public void setPossibleCriterionsForClosingParenthesisMap(
      Map<SearchCriterionBase, Boolean> possibleCriterionsForClosingParenthesisMap) {
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
    // Integer position = (Integer) e.getComponent().getAttributes().get("indexOfCriterion");

    final SearchCriterionBase sc = this.criterionList.get(position);
    if (sc instanceof StringOrHiddenIdSearchCriterion) {
      final StringOrHiddenIdSearchCriterion hiddenSc = (StringOrHiddenIdSearchCriterion) sc;
      hiddenSc.setHiddenId(null);
      hiddenSc.setSearchString(null);
    }

  }


  public SearchCriterionBase getComponentVisibilityListSearchCriterion() {
    return this.componentVisibilityListSearchCriterion;
  }


  public void setComponentVisibilityListSearchCriterion(
      SearchCriterionBase componentVisibilityListSearchCriterion) {
    this.componentVisibilityListSearchCriterion = componentVisibilityListSearchCriterion;
  }


  public SearchCriterionBase getComponentContentCategoryListSearchCriterion() {
    return this.componentContentCategoryListSearchCriterion;
  }


  public void setComponentContentCategoryListSearchCriterion(
      SearchCriterionBase componentContentCategoryListSearchCriterion) {
    this.componentContentCategoryListSearchCriterion = componentContentCategoryListSearchCriterion;
  }


  public List<SelectItem> getReviewMethodListMenu() {
    return this.reviewMethodListMenu;
  }


  public void setReviewMethodListMenu(List<SelectItem> reviewMethodListMenu) {
    this.reviewMethodListMenu = reviewMethodListMenu;
  }

  @Override
  public void languageChanged(String oldLang, String newLang) {
    this.criterionTypeListMenu = this.initCriterionTypeListMenu(Index.ESCIDOC_ALL);
    this.setCriterionTypeListMenuAdmin(this.initCriterionTypeListMenu(Index.ITEM_CONTAINER_ADMIN));
    this.operatorTypeListMenu = this.initOperatorListMenu();
    this.genreListMenu = this.initGenreListMenu();
    this.reviewMethodListMenu = this.initReviewMethodListMenu();
    this.contentCategoryListMenu = this.initContentCategoryListMenu();
    this.componentVisibilityListMenu = this.initComponentVisibilityListMenu();
    this.subjectTypesListMenu = this.initSubjectTypesListMenu();


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

  public SearchCriterionBase getComponentEmbargoDateSearchCriterion() {
    return this.componentEmbargoDateSearchCriterion;
  }

  public void setComponentEmbargoDateSearchCriterion(
      SearchCriterionBase componentEmbargoDateSearchCriterion) {
    this.componentEmbargoDateSearchCriterion = componentEmbargoDateSearchCriterion;
  }

  public SearchCriterionBase getComponentEmbargoDateAvailableSearchCriterion() {
    return this.componentEmbargoDateAvailableSearchCriterion;
  }

  public void setComponentEmbargoDateAvailableSearchCriterion(
      SearchCriterionBase componentEmbargoDateAvailableSearchCriterion) {
    this.componentEmbargoDateAvailableSearchCriterion =
        componentEmbargoDateAvailableSearchCriterion;
  }

  public SearchCriterionBase getAffiliatedContextListSearchCriterion() {
    return this.affiliatedContextListSearchCriterion;
  }

  public void setAffiliatedContextListSearchCriterion(
      SearchCriterionBase affiliatedContextListSearchCriterion) {
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

  public void setPublicationStatusListSearchCriterion(
      SearchCriterionBase publicationStatusListSearchCriterion) {
    this.publicationStatusListSearchCriterion = publicationStatusListSearchCriterion;
  }

  public List<SelectItem> getPersonRoleMenu() {
    return personRoleMenu;
  }

  public void setPersonRoleMenu(List<SelectItem> personRoleMenu) {
    this.personRoleMenu = personRoleMenu;
  }

  public String getSuggestConeUrl() throws Exception {
    if (this.suggestConeUrl == null) {
      this.suggestConeUrl = PropertyReader.getProperty("escidoc.cone.service.url");
    }
    return this.suggestConeUrl;
  }
}
