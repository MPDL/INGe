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
package de.mpg.mpdl.inge.pubman.web.search.criterions;

import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.pubman.web.search.SearchParseException;
import de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox.AffiliatedContextListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox.EmbargoDateAvailableSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox.EventInvitationSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox.ItemStateListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox.PublicationStatusListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.ComponentContentCategoryListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.ComponentOaStatusListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.ComponentVisibilityListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.FileAvailableSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.FileSectionSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.LocatorAvailableSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.dates.DateSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.enums.GenreSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.enums.ReviewMethodSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.enums.StateSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.genre.GenreListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.Parenthesis;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.AnyFieldAndFulltextSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.AnyFieldSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.ClassificationSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.CollectionSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.ComponentContentCategory;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.ComponentVisibilitySearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.DegreeSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.EventTitleSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.FlexibleStandardSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.FulltextSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.IdentifierSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.JournalSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.KeywordSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.LanguageSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.LocalTagSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.OrcidSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.ProjectInfoSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.SourceSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.TitleSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.CreatedBySearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.ModifiedBySearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.OrganizationSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.PersonSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.service.util.SearchUtils;

@SuppressWarnings("serial")
public abstract class SearchCriterionBase implements Serializable {
  private static final Logger logger = LogManager.getLogger(SearchCriterionBase.class);

  public enum Index
  {
    ESCIDOC_ALL,
    ITEM_CONTAINER_ADMIN
  }

  public enum SearchCriterion
  {
    TITLE(TitleSearchCriterion.class, DisplayType.STANDARD),
    KEYWORD(KeywordSearchCriterion.class, DisplayType.STANDARD),
    CLASSIFICATION(ClassificationSearchCriterion.class, null),
    ANY(AnyFieldSearchCriterion.class, DisplayType.STANDARD),
    ANYFULLTEXT(AnyFieldAndFulltextSearchCriterion.class, DisplayType.STANDARD),
    FULLTEXT(FulltextSearchCriterion.class, DisplayType.STANDARD),
    ANYPERSON(PersonSearchCriterion.class, DisplayType.PERSON),

    // Person enum names should be the same as role names in CreatorVO.CreatorRole
    AUTHOR(PersonSearchCriterion.class, DisplayType.PERSON),

    ORGUNIT(OrganizationSearchCriterion.class, null),
    ANYDATE(DateSearchCriterion.class, DisplayType.DATE),
    PUBLISHEDPRINT(DateSearchCriterion.class, DisplayType.DATE),
    PUBLISHED(DateSearchCriterion.class, DisplayType.DATE),
    ACCEPTED(DateSearchCriterion.class, DisplayType.DATE),
    SUBMITTED(DateSearchCriterion.class, DisplayType.DATE),
    MODIFIED(DateSearchCriterion.class, DisplayType.DATE),
    CREATED(DateSearchCriterion.class, DisplayType.DATE),
    LANG(LanguageSearchCriterion.class, null),
    EVENT(EventTitleSearchCriterion.class, DisplayType.STANDARD),
    EVENT_STARTDATE(DateSearchCriterion.class, DisplayType.DATE),
    EVENT_ENDDATE(DateSearchCriterion.class, DisplayType.DATE),
    EVENT_INVITATION(EventInvitationSearchCriterion.class, null),
    SOURCE(SourceSearchCriterion.class, DisplayType.STANDARD),
    JOURNAL(JournalSearchCriterion.class, null),
    LOCAL(LocalTagSearchCriterion.class, DisplayType.STANDARD),
    ORCID(OrcidSearchCriterion.class, DisplayType.STANDARD),
    IDENTIFIER(IdentifierSearchCriterion.class, null),
    COLLECTION(CollectionSearchCriterion.class, null),
    PROJECT_INFO(ProjectInfoSearchCriterion.class, DisplayType.STANDARD),

    GENRE_DEGREE_LIST(GenreListSearchCriterion.class, null),
    GENRE(GenreSearchCriterion.class, null),
    STATE(StateSearchCriterion.class, null),
    REVIEW_METHOD(ReviewMethodSearchCriterion.class, null),
    DEGREE(DegreeSearchCriterion.class, null),
    FILE_AVAILABLE(FileAvailableSearchCriterion.class, null),
    LOCATOR_AVAILABLE(LocatorAvailableSearchCriterion.class, null),
    EMBARGO_DATE_AVAILABLE(EmbargoDateAvailableSearchCriterion.class, null),
    COMPONENT_CONTENT_CATEGORY(ComponentContentCategory.class, null),
    COMPONENT_VISIBILITY(ComponentVisibilitySearchCriterion.class, null),
    COMPONENT_VISIBILITY_LIST(ComponentVisibilityListSearchCriterion.class, null),
    COMPONENT_CONTENT_CATEGORY_LIST(ComponentContentCategoryListSearchCriterion.class, null),
    COMPONENT_EMBARGO_DATE(DateSearchCriterion.class, DisplayType.DATE),
    COMPONENT_OA_STATUS_LIST(ComponentOaStatusListSearchCriterion.class, null),
    ITEMSTATE_LIST(ItemStateListSearchCriterion.class, null),
    AFFILIATED_CONTEXT_LIST(AffiliatedContextListSearchCriterion.class, null),
    PUBLICATION_STATUS_LIST(PublicationStatusListSearchCriterion.class, null),

    MODIFIED_INTERNAL(DateSearchCriterion.class, DisplayType.DATE),
    CREATED_INTERNAL(DateSearchCriterion.class, DisplayType.DATE),
    CREATED_BY(CreatedBySearchCriterion.class, null),
    MODIFIED_BY(ModifiedBySearchCriterion.class, null),

    AND_OPERATOR(LogicalOperator.class, DisplayType.OPERATOR),
    OR_OPERATOR(LogicalOperator.class, DisplayType.OPERATOR),
    NOT_OPERATOR(LogicalOperator.class, DisplayType.OPERATOR),

    OPENING_PARENTHESIS(Parenthesis.class, DisplayType.PARENTHESIS),
    CLOSING_PARENTHESIS(Parenthesis.class, DisplayType.PARENTHESIS),

    FLEXIBLE(FlexibleStandardSearchCriterion.class, null),
    FILE_SECTION(FileSectionSearchCriterion.class, null),
    LOCATOR_SECTION(FileSectionSearchCriterion.class, null);

  private Class<?> relatedClass;
  private DisplayType displayType;

  SearchCriterion(Class<?> classToInstantiate, DisplayType dt) {
      this.relatedClass = classToInstantiate;
      this.displayType = dt;
    }

  public Class<?> getRelatedClass() {
      return this.relatedClass;
    }

  public void setRelatedClass(Class<?> relatedClass) {
      this.relatedClass = relatedClass;
    }

  public DisplayType getDisplayType() {
      return this.displayType;
    }

  public void setDisplayType(DisplayType displayType) {
    this.displayType = displayType;
  }}

  public enum DisplayType{STANDARD,DATE,PERSON,OPERATOR,PARENTHESIS}

  public enum QueryType{CQL,INTERNAL}

  private String queryValue;

  protected SearchCriterion searchCriterion;

  public abstract Query toElasticSearchQuery() throws SearchParseException, IngeTechnicalException;

  public abstract String getElasticSearchNestedPath();

  public String toQueryString() {
    return this.getSearchCriterion().name() + "=\"" + getQueryStringContent() + "\"";
  }

  public abstract String getQueryStringContent();

  public abstract void parseQueryStringContent(String content);

  public abstract boolean isEmpty(QueryType queryType);

  private boolean parenthesisCanBeOpened;

  private boolean parenthesisCanBeClosed;

  private int level = 0;

  public SearchCriterionBase() {
    // Find the Enum which belongs to this class
    for (final SearchCriterion sc : SearchCriterion.values()) {
      if (sc.getRelatedClass() == this.getClass()) {
        this.setSearchCriterion(sc);
        break;
      }
    }
  }

  public SearchCriterionBase(SearchCriterion type) {
    this.searchCriterion = type;
  }


  public void setSearchCriterion(SearchCriterion sc) {
    // logger.info("Set search criterion "+ this.hashCode() +" from " + this.searchCriterion +
    // " to: " + sc);
    this.searchCriterion = sc;
  }

  public SearchCriterion getSearchCriterion() {
    return this.searchCriterion;
  }

  public String getQueryValue() {
    return this.queryValue;
  }

  public void setQueryValue(String queryValue) {
    this.queryValue = queryValue;
  }

  public static SearchCriterionBase initSearchCriterion(SearchCriterion sc) {
    try {
      final Constructor<?> ctor = sc.getRelatedClass().getDeclaredConstructor(SearchCriterion.class);
      ctor.setAccessible(true);
      final SearchCriterionBase scb = (SearchCriterionBase) ctor.newInstance(sc);
      scb.setSearchCriterion(sc);
      return scb;

    } catch (final Exception e) {
      SearchCriterionBase.logger.debug("No one-argument constructor with SearchCriterion found for " + sc.getRelatedClass(), e);
      // return search criterion with default constructor
    }

    try {
      return (SearchCriterionBase) sc.getRelatedClass().newInstance();
    } catch (final Exception e) {
      SearchCriterionBase.logger.debug("Problem while instantiating class " + sc.getRelatedClass());
    }

    return null;
  }

  //  protected static String escapeForCql(String escapeMe) {
  //    String result = escapeMe.replace("<", "\\<");
  //    result = result.replace(">", "\\>");
  //    result = result.replace("+", "\\+");
  //    result = result.replace("-", "\\-");
  //    result = result.replace("&", "\\&");
  //    result = result.replace("%", "\\%");
  //    result = result.replace("|", "\\|");
  //    result = result.replace("(", "\\(");
  //    result = result.replace(")", "\\)");
  //    result = result.replace("[", "\\[");
  //    result = result.replace("]", "\\]");
  //    result = result.replace("^", "\\^");
  //    result = result.replace("~", "\\~");
  //    result = result.replace("!", "\\!");
  //    result = result.replace("{", "\\{");
  //    result = result.replace("}", "\\}");
  //    result = result.replace("\"", "\\\"");
  //    return result;
  //  }

  protected static String escapeForQueryString(String escapeMe) {
    String result = escapeMe.replace("\\", "\\\\");
    result = result.replace("=", "\\=");
    result = result.replace("|", "\\|");
    result = result.replace("(", "\\(");
    result = result.replace(")", "\\)");
    result = result.replace("\"", "\\\"");
    return result;
  }

  protected static String unescapeForQueryString(String escapeMe) {
    String result = escapeMe.replace("\\=", "=");
    result = result.replace("\\\"", "\"");
    result = result.replace("\\|", "|");
    result = result.replace("\\(", "(");
    result = result.replace("\\)", ")");
    result = result.replace("\\\\", "\\");
    return result;
  }

  //  /**
  //   * Creates a cql string out of one or several search indexes and an search string. The search
  //   * string is splitted into single words, except they are in quotes. The special characters of the
  //   * search string parts are escaped.
  //   *
  //   * Example: cqlIndexes={escidoc.title, escidoc.fulltext} searchString = book "john grisham"
  //   *
  //   * Resulting cql string: escidoc.title=("book" and "john grisham") or escioc.fulltext=("book" and
  //   * "john grisham")
  //   *
  //   * @param cqlIndexes
  //   * @param searchString
  //   * @return the cql string or null, if no search string or indexes are given
  //   */
  //  protected String baseCqlBuilder(String[] cqlIndexes, String searchString) throws SearchParseException {
  //
  //    if (searchString != null) {
  //      // Bugfix for PUBMAN-2221: Remove Questionmark at the end
  //      if (searchString.trim().endsWith("?")) {
  //        searchString = searchString.trim().substring(0, searchString.length() - 1);
  //
  //      }
  //
  //      // Bugfix for pubman PUBMAN-248: Search: error using percent symbol in search
  //      if (searchString.contains("%")) {
  //        throw new SearchParseException("search string contains %");
  //      }
  //      if (searchString.trim().startsWith("*")) {
  //        throw new SearchParseException("search string starts with *");
  //      }
  //      if (searchString.trim().startsWith("?")) {
  //        throw new SearchParseException("search string starts with ?");
  //      }
  //
  //
  //    }
  //
  //
  //
  //    if (searchString != null && !searchString.trim().isEmpty()) {
  //
  //
  //      // split the search string into single words, except if they are in quotes
  //      final List<String> splittedSearchStrings = new ArrayList<String>();
  //      // List<String> splittedOperators = new ArrayList<String>();
  //
  //      // Pattern pattern = Pattern.compile("(?<=\\s|^)\"(.*?)\"(?=\\s|$)|(\\S+)");
  //
  //      // /u3000 is the Unicode for Japanese full-width-space
  //      final Pattern pattern = Pattern.compile("(?<=[\\s\\u3000]|^)\"(.*?)\"(?=[\\s\\u3000]|$)|([^\\s\\u3000]+)");
  //      final Matcher m = pattern.matcher(searchString);
  //
  //      while (m.find()) {
  //        String subSearchString = m.group();
  //
  //        if (subSearchString != null && !subSearchString.trim().isEmpty()) {
  //          subSearchString = subSearchString.trim();
  //
  //          // Remove quotes at beginning and end
  //          if (subSearchString.startsWith("\"")) {
  //            subSearchString = subSearchString.substring(1, subSearchString.length());
  //          }
  //
  //          if (subSearchString.endsWith("\"")) {
  //            subSearchString = subSearchString.substring(0, subSearchString.length() - 1);
  //          }
  //        }
  //        if (!subSearchString.trim().isEmpty()) {
  //          splittedSearchStrings.add(subSearchString.trim());
  //        }
  //
  //
  //      }
  //
  //
  //      final StringBuilder cqlStringBuilder = new StringBuilder();
  //
  //      if (cqlIndexes.length > 1) {
  //        cqlStringBuilder.append("(");
  //      }
  //
  //      for (int j = 0; j < cqlIndexes.length; j++) {
  //        cqlStringBuilder.append(cqlIndexes[j]);
  //        cqlStringBuilder.append("=");
  //
  //        if (splittedSearchStrings.size() > 1) {
  //          cqlStringBuilder.append("(");
  //        }
  //
  //        for (int i = 0; i < splittedSearchStrings.size(); i++) {
  //          final String subSearchString = splittedSearchStrings.get(i);
  //          cqlStringBuilder.append("\"");
  //          cqlStringBuilder.append(SearchCriterionBase.escapeForCql(subSearchString));
  //          cqlStringBuilder.append("\"");
  //
  //          if (splittedSearchStrings.size() > i + 1) {
  //            if (splittedSearchStrings.get(i + 1).matches("AND|OR|NOT")) {
  //              cqlStringBuilder.append(" " + splittedSearchStrings.get(i + 1) + " ");
  //              i++;
  //            } else {
  //              cqlStringBuilder.append(" AND ");
  //            }
  //
  //          }
  //
  //        }
  //        if (splittedSearchStrings.size() > 1) {
  //          cqlStringBuilder.append(")");
  //        }
  //
  //
  //
  //        if (cqlIndexes.length > j + 1) {
  //          cqlStringBuilder.append(" OR ");
  //        }
  //      }
  //
  //      if (cqlIndexes.length > 1) {
  //        cqlStringBuilder.append(")");
  //      }
  //
  //      return cqlStringBuilder.toString();
  //    }
  //
  //    return null;
  //
  //
  //  }

  public static Query baseElasticSearchQueryBuilder(String[] indexFields, String... searchString) throws IngeTechnicalException {
    Map<String, ElasticSearchIndexField> indexMap = ApplicationBean.INSTANCE.getPubItemService().getElasticSearchIndexFields();
    return SearchUtils.baseElasticSearchQueryBuilder(indexMap, indexFields, searchString);
  }

  public static Query baseElasticSearchQueryBuilder(String index, String... value) throws IngeTechnicalException {
    Map<String, ElasticSearchIndexField> indexMap = ApplicationBean.INSTANCE.getPubItemService().getElasticSearchIndexFields();
    return SearchUtils.baseElasticSearchQueryBuilder(indexMap, index, value);
  }

  //  /**
  //   * Creates a CQL query string out of a list of search criteria. Before, it removes empty search
  //   * criterions. Adds parenthesis around every single search criterion object.
  //   *
  //   * @param criterionList
  //   * @return
  //   */
  //  public static String scListToCql(Index indexName, List<SearchCriterionBase> criterionList, boolean appendStandardCriterions)
  //      throws SearchParseException {
  //
  //    final List<SearchCriterionBase> removedList = SearchCriterionBase.removeEmptyFields(criterionList, QueryType.CQL);
  //
  //
  //    String appendOperator = "AND";
  //
  //    final StringBuilder sb = new StringBuilder();
  //    for (int i = 0; i < removedList.size(); i++) {
  //
  //      final SearchCriterionBase criterion = removedList.get(i);
  //
  //      // if first in list is an operator, use it as concatenator to append standard criteria below,
  //      // else use default "AND"
  //      if (i == 0 && DisplayType.OPERATOR.equals(criterion.getSearchCriterion().getDisplayType())) {
  //        appendOperator = criterion.toCqlString(indexName);
  //      } else {
  //        final String cql = criterion.toCqlString(indexName);
  //        if (cql != null && !cql.trim().isEmpty()) {
  //
  //
  //
  //          if (!DisplayType.OPERATOR.equals(criterion.getSearchCriterion().getDisplayType())
  //              && !DisplayType.PARENTHESIS.equals(criterion.getSearchCriterion().getDisplayType())) {
  //            sb.append("(");
  //          }
  //
  //          sb.append(cql);
  //
  //          if (!DisplayType.OPERATOR.equals(criterion.getSearchCriterion().getDisplayType())
  //              && !DisplayType.PARENTHESIS.equals(criterion.getSearchCriterion().getDisplayType())) {
  //            sb.append(")");
  //          }
  //
  //          sb.append(" ");
  //        }
  //      }
  //
  //
  //
  //    }
  //
  //
  //
  //    if (appendStandardCriterions) {
  //      try {
  //
  //
  //
  //        final String contentModelId = PropertyReader.getProperty(SearchCriterionBase.PROPERTY_CONTENT_MODEL);
  //
  //        String standardCriterions = null;
  //        switch (indexName) {
  //          case ESCIDOC_ALL: {
  //            standardCriterions = SearchCriterionBase.INDEX_OBJECTTYPE + "=\"item\" AND " + SearchCriterionBase.INDEX_CONTENT_MODEL + "=\""
  //                + SearchCriterionBase.escapeForCql(contentModelId) + "\"";
  //            break;
  //          }
  //          case ITEM_CONTAINER_ADMIN: {
  //            standardCriterions = "\"/properties/content-model/id\"=\"" + SearchCriterionBase.escapeForCql(contentModelId) + "\"";
  //            break;
  //          }
  //
  //        }
  //
  //
  //
  //        if (!sb.toString().isEmpty()) {
  //
  //          standardCriterions = standardCriterions + " " + appendOperator + " (" + sb.toString() + ")";
  //        }
  //        return standardCriterions;
  //      } catch (final Exception e) {
  //        SearchCriterionBase.logger.error("Could not read property " + SearchCriterionBase.PROPERTY_CONTENT_MODEL, e);
  //      }
  //    }
  //
  //    return sb.toString();
  //
  //  }

  public static Query scListToElasticSearchQuery(List<SearchCriterionBase> scList) throws SearchParseException, IngeTechnicalException {
    final List<SearchCriterionBase> cleanedScList = SearchCriterionBase.removeEmptyFields(scList, QueryType.CQL);

    // Set partner parenthesis for every parenthesis
    final Stack<Parenthesis> parenthesisStack = new Stack<>();
    for (final SearchCriterionBase sc : cleanedScList) {
      if (SearchCriterion.OPENING_PARENTHESIS.equals(sc.getSearchCriterion())) {
        parenthesisStack.push((Parenthesis) sc);

      } else if (SearchCriterion.CLOSING_PARENTHESIS.equals(sc.getSearchCriterion())) {

        final Parenthesis closingParenthesis = (Parenthesis) sc;
        final Parenthesis openingParenthesis = parenthesisStack.pop();

        closingParenthesis.setPartnerParenthesis(openingParenthesis);
        openingParenthesis.setPartnerParenthesis(closingParenthesis);
      }
    }

    return SearchCriterionBase.cleanedScListToElasticSearchQuery(cleanedScList, null);
  }

  private static Query cleanedScListToElasticSearchQuery(List<SearchCriterionBase> scList, String parentNestedPath)
      throws SearchParseException, IngeTechnicalException {

    SearchCriterionBase.logger.debug("Call with list: " + scList);

    if (scList.isEmpty()) {
      return new MatchAllQuery.Builder().build()._toQuery();
    }

    Query resultedQueryBuilder = null;

    int parenthesisOpened = 0;

    final List<LogicalOperator> mainOperators = new ArrayList<>();
    LogicalOperator lastOperator = null;
    boolean mixedOrAndAnd = false;

    String sharedNestedField = "";

    final List<SearchCriterionBase> criterionList = new ArrayList<>(scList);

    SearchCriterionBase.logger.debug("List: " + criterionList);

    // Remove unnecessary parenthesis
    while (SearchCriterion.OPENING_PARENTHESIS.equals(criterionList.get(0).getSearchCriterion())
        && SearchCriterion.CLOSING_PARENTHESIS.equals(criterionList.get(criterionList.size() - 1).getSearchCriterion())
        && ((Parenthesis) criterionList.get(0)).getPartnerParenthesis() == criterionList.get(criterionList.size() - 1)) {

      criterionList.remove(0);
      criterionList.remove(criterionList.size() - 1);
    }

    SearchCriterionBase.logger.debug("List after removal: " + criterionList);

    for (final SearchCriterionBase sc : criterionList) {

      if (DisplayType.OPERATOR.equals(sc.getSearchCriterion().getDisplayType())) {

        if (0 == parenthesisOpened) {

          final LogicalOperator op = (LogicalOperator) sc;
          mainOperators.add(op);
          //Check if this operator changes from last
          if (null != lastOperator && ((lastOperator.getSearchCriterion().equals(SearchCriterion.OR_OPERATOR)
              && !op.getSearchCriterion().equals(SearchCriterion.OR_OPERATOR))
              || (!lastOperator.getSearchCriterion().equals(SearchCriterion.OR_OPERATOR)
                  && op.getSearchCriterion().equals(SearchCriterion.OR_OPERATOR))

          )) {
            mixedOrAndAnd = true;
          }
          lastOperator = op;
        }

      } else if (SearchCriterion.OPENING_PARENTHESIS.equals(sc.getSearchCriterion())) {
        parenthesisOpened++;

      } else if (SearchCriterion.CLOSING_PARENTHESIS.equals(sc.getSearchCriterion())) {
        parenthesisOpened--;

      } else {

        // if all criterias have the same nested field and if it's different from the parent
        // nested
        // criteria, set a new nested query
        if ((null != sharedNestedField && sharedNestedField.isEmpty()
            && !(null != parentNestedPath && sc.getElasticSearchNestedPath().equals(parentNestedPath)))
            || (null != sc.getElasticSearchNestedPath() && sc.getElasticSearchNestedPath().equals(sharedNestedField)
                && !sc.getElasticSearchNestedPath().equals(parentNestedPath))) {
          sharedNestedField = sc.getElasticSearchNestedPath();
        } else {
          sharedNestedField = null;
        }
      }
    }

    if (null != sharedNestedField) {
      SearchCriterionBase.logger.debug("Found common nested field: " + sharedNestedField);
    }

    if (1 == criterionList.size()) {
      resultedQueryBuilder = criterionList.get(0).toElasticSearchQuery();

    } else if (!mainOperators.isEmpty()) {

      SearchCriterionBase.logger.debug("found main operators: " + mainOperators);

      final BoolQuery.Builder bq = new BoolQuery.Builder();

      // If there are AND/NOTAND operators mixed with OR operators, divide by OR operators ->
      // Remove all AND / NOTAND operators
      if (mixedOrAndAnd) {
        mainOperators.removeIf(item -> !SearchCriterion.OR_OPERATOR.equals(item.getSearchCriterion()));
      }

      for (int i = 0; i < mainOperators.size(); i++) {
        final LogicalOperator op = mainOperators.get(i);
        final int indexOfOperator = criterionList.indexOf(op);
        final int nextIndexOfOperator =
            (mainOperators.size() > i + 1) ? criterionList.indexOf(mainOperators.get(i + 1)) : criterionList.size();

        if (0 == i) {
          final List<SearchCriterionBase> leftList = criterionList.subList(0, indexOfOperator);

          if (SearchCriterion.OR_OPERATOR.equals(op.getSearchCriterion())) {
            bq.should(SearchCriterionBase.cleanedScListToElasticSearchQuery(leftList, sharedNestedField));
          } else if (SearchCriterion.AND_OPERATOR.equals(op.getSearchCriterion())) {
            bq.must(SearchCriterionBase.cleanedScListToElasticSearchQuery(leftList, sharedNestedField));
          } else if (SearchCriterion.NOT_OPERATOR.equals(op.getSearchCriterion())) {
            bq.must(SearchCriterionBase.cleanedScListToElasticSearchQuery(leftList, sharedNestedField));
          }
        }

        final List<SearchCriterionBase> rightList = criterionList.subList(indexOfOperator + 1, nextIndexOfOperator);

        if (SearchCriterion.OR_OPERATOR.equals(op.getSearchCriterion())) {
          bq.should(SearchCriterionBase.cleanedScListToElasticSearchQuery(rightList, sharedNestedField));
        } else if (SearchCriterion.AND_OPERATOR.equals(op.getSearchCriterion())) {
          bq.must(SearchCriterionBase.cleanedScListToElasticSearchQuery(rightList, sharedNestedField));
        } else if (SearchCriterion.NOT_OPERATOR.equals(op.getSearchCriterion())) {
          bq.mustNot(SearchCriterionBase.cleanedScListToElasticSearchQuery(rightList, sharedNestedField));
        }
      }

      resultedQueryBuilder = bq.build()._toQuery();
    }

    return resultedQueryBuilder;
  }

  public static String scListToQueryString(List<SearchCriterionBase> criterionList) {

    final List<SearchCriterionBase> removedList = SearchCriterionBase.removeEmptyFields(criterionList, QueryType.INTERNAL);

    final StringBuilder sb = new StringBuilder();
    for (final SearchCriterionBase criterion : removedList) {

      final String query = criterion.toQueryString();
      if (null != query) {

        sb.append(query);

        sb.append(" ");
      }
    }

    return sb.toString();
  }

  public static List<SearchCriterionBase> queryStringToScList(String queryString) throws RuntimeException {
    final List<SearchCriterionBase> scList = new ArrayList<>();

    final StringReader sr = new StringReader(queryString);

    int ch;
    try {

      final StringBuilder substringBuffer = new StringBuilder();
      SearchCriterion currentSearchCriterionName = null;
      SearchCriterionBase currentSearchCriterion = null;
      final Stack<Parenthesis> parenthesisStack = new Stack<>();
      while (-1 != (ch = sr.read())) {

        if ('=' == ch && !substringBuffer.isEmpty() && '\\' != substringBuffer.charAt(substringBuffer.length() - 1)) {
          currentSearchCriterionName = SearchCriterion.valueOf(substringBuffer.toString());

          if ('"' != sr.read()) {
            throw new RuntimeException("Search criterion name must be followed by an '=' and '\"' ");
          }

          int contentChar;
          final StringBuilder contentBuffer = new StringBuilder();
          while (-1 != (contentChar = sr.read())) {

            if ('"' == contentChar && !(!contentBuffer.isEmpty() && '\\' == contentBuffer.charAt(contentBuffer.length() - 1))) {
              // end of content
              currentSearchCriterion = SearchCriterionBase.initSearchCriterion(currentSearchCriterionName);
              try {
                currentSearchCriterion.parseQueryStringContent(contentBuffer.toString());
              } catch (final Exception e) {
                throw new RuntimeException("Error while parsing query string content: " + contentBuffer, e);
              }
              scList.add(currentSearchCriterion);
              break;
            } else {
              contentBuffer.append((char) contentChar);
            }

          }

          // empty the buffer
          substringBuffer.setLength(0);
        }


        // Logical Operators
        else if (' ' == ch) {
          if (!substringBuffer.isEmpty()) {
            switch (substringBuffer.toString().toLowerCase()) {
              case "and" -> {
                scList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
                substringBuffer.setLength(0);
              }
              case "or" -> {
                scList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
                substringBuffer.setLength(0);
              }
              case "not" -> {
                scList.add(new LogicalOperator(SearchCriterion.NOT_OPERATOR));
                substringBuffer.setLength(0);
              }
            }


          }

        }

        else if ('(' == ch) {
          final Parenthesis p = new Parenthesis(SearchCriterion.OPENING_PARENTHESIS);
          scList.add(p);
          parenthesisStack.push(p);
        }

        else if (')' == ch) {
          final Parenthesis p = new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS);
          scList.add(p);
          Parenthesis openingParenthesis;
          try {
            openingParenthesis = parenthesisStack.pop();
            openingParenthesis.setPartnerParenthesis(p);
            p.setPartnerParenthesis(openingParenthesis);
          } catch (final EmptyStackException e) {
            throw new RuntimeException("Parenthesis in query string are not balanced");
          }

        }

        else {
          substringBuffer.append((char) ch);
        }

      }

    } catch (final Exception e) {
      throw new RuntimeException("Error while parsing query string", e);
    }

    return scList;
  }


  //  public static String queryStringToCqlString(Index indexName, String query, boolean appendStandardCqlCriteria)
  //      throws SearchParseException {
  //    final List<SearchCriterionBase> scList = SearchCriterionBase.queryStringToScList(query);
  //    return SearchCriterionBase.scListToCql(indexName, scList, appendStandardCqlCriteria);
  //  }



  public static List<SearchCriterionBase> removeEmptyFields(List<SearchCriterionBase> criterionList, QueryType queryType) {
    if (null == criterionList) {
      return new ArrayList<>();
    } else {


      final List<SearchCriterionBase> copyForRemoval = new ArrayList<>(criterionList);
      final List<SearchCriterionBase> copyForIteration = new ArrayList<>(criterionList);
      // Collections.copy(copy, criterionList);

      for (final SearchCriterionBase sc : copyForIteration) {
        if (sc.isEmpty(queryType)) {
          SearchCriterionBase.removeSearchCriterionWithOperator(copyForRemoval, sc);
          // logger.info("Remove " + sc);

        }
      }

      // if first in list is an operator except "NOT", remove it
      if (!copyForRemoval.isEmpty() && DisplayType.OPERATOR.equals(copyForRemoval.get(0).getSearchCriterion().getDisplayType())
          && !SearchCriterion.NOT_OPERATOR.equals(copyForRemoval.get(0).getSearchCriterion())) {
        copyForRemoval.remove(0);
      }
      return copyForRemoval;
    }
  }

  public static void removeSearchCriterionWithOperator(List<SearchCriterionBase> criterionList, SearchCriterionBase criterion) {

    final int position = criterionList.indexOf(criterion);
    // try to delete
    boolean deleteBefore = true;
    if (0 == position) {
      deleteBefore = false;
    } else if (0 <= position - 1) {
      final SearchCriterionBase scBefore = criterionList.get(position - 1);

      deleteBefore = !scBefore.getSearchCriterion().equals(SearchCriterion.OPENING_PARENTHESIS);

      if (!deleteBefore && position + 1 < criterionList.size()) {
        final SearchCriterionBase scAfter = criterionList.get(position + 1);
        deleteBefore = scAfter.getSearchCriterion().equals(SearchCriterion.CLOSING_PARENTHESIS);
      }
    }



    if (deleteBefore) {
      for (int i = position; 0 <= i; i--) {
        final SearchCriterion sci = criterionList.get(i).getSearchCriterion();
        if (DisplayType.OPERATOR.equals(sci.getDisplayType())) {
          criterionList.remove(position);
          criterionList.remove(i);
          break;

        }
      }
    } else {
      // delete logical operator after
      for (int i = position; i < criterionList.size(); i++) {
        final SearchCriterion sci = criterionList.get(i).getSearchCriterion();
        if (DisplayType.OPERATOR.equals(sci.getDisplayType())) {
          criterionList.remove(i);
          criterionList.remove(position);
          break;

        }
      }
    }

    // if none was found, just remove the criteria itself
    criterionList.remove(criterion);


    final List<SearchCriterionBase> parenthesisToRemove = new ArrayList<>();
    // now remove empty parenthesis
    for (int i = 0; i < criterionList.size(); i++) {
      final SearchCriterionBase sc = criterionList.get(i);
      if (SearchCriterion.OPENING_PARENTHESIS.equals(sc.getSearchCriterion())) {
        if (i + 1 < criterionList.size()) {
          final SearchCriterionBase next = criterionList.get(i + 1);
          if (SearchCriterion.CLOSING_PARENTHESIS.equals(next.getSearchCriterion())) {
            parenthesisToRemove.add(sc);
            parenthesisToRemove.add(next);
          }
        }

      }
    }

    criterionList.removeAll(parenthesisToRemove);

    // if first criterion is an operand, remove it
    if (null != criterionList && !criterionList.isEmpty()
        && DisplayType.OPERATOR.equals(criterionList.get(0).getSearchCriterion().getDisplayType())) {
      criterionList.remove(0);
    }

  }


  public static void updateParenthesisStatus(List<SearchCriterionBase> criterionList) {
    // SearchCriterionBase lastOpenedParenthesis;
    for (final SearchCriterionBase sc : criterionList) {
      if (SearchCriterion.OPENING_PARENTHESIS.equals(sc.getSearchCriterion())) {

      } else if (SearchCriterion.CLOSING_PARENTHESIS.equals(sc.getSearchCriterion())) {

      } else {

      }
    }

  }

  public boolean isParenthesisCanBeOpened() {
    return this.parenthesisCanBeOpened;
  }

  public void setParenthesisCanBeOpened(boolean parenthesisCanBeOpened) {
    this.parenthesisCanBeOpened = parenthesisCanBeOpened;
  }

  public boolean isParenthesisCanBeClosed() {
    return this.parenthesisCanBeClosed;
  }

  public void setParenthesisCanBeClosed(boolean parenthesisCanBeClosed) {
    this.parenthesisCanBeClosed = parenthesisCanBeClosed;
  }

  public int getLevel() {
    return this.level;
  }

  public void setLevel(int level) {
    this.level = level;
  }


  @Override
  public String toString() {
    try {
      return this.toQueryString() + " (" + this.hashCode() + ")";
    } catch (final Exception e) {
      return this.getSearchCriterion().name();
    }
  }



}
