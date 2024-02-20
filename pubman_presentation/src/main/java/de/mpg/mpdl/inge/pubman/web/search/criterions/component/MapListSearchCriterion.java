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
package de.mpg.mpdl.inge.pubman.web.search.criterions.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;

@SuppressWarnings("serial")
public abstract class MapListSearchCriterion<T> extends SearchCriterionBase {


  protected Map<String, Boolean> enumMap = new LinkedHashMap<>();

  private Map<String, T> valueMap;



  public MapListSearchCriterion(Map<String, T> m, Map<String, Boolean> preSelectionMap) {
    this.valueMap = m;
    this.initEnumMap(preSelectionMap);
  }

  //  public MapListSearchCriterion(Map<String, T> m, Map<String, Boolean> preSelectionMap, boolean removeListValues) {
  //    this.setValueMap(m);
  //    this.initEnumMap(preSelectionMap);
  //  }

  public MapListSearchCriterion(Map<String, T> m) {
    this.valueMap = m;
    this.initEnumMap(null);
  }


  public void initEnumMap(Map<String, Boolean> preSelectionMap) {

    for (final String v : this.valueMap.keySet()) {
      if (null == preSelectionMap || !preSelectionMap.containsKey(v)) {
        this.enumMap.put(v, false);
      } else {
        this.enumMap.put(v, preSelectionMap.get(v));
      }

    }

  }

  public Map<String, Boolean> initRemoveSelctedEnumMap(Map<String, Boolean> preSelectionMap) {

    for (final String v : this.valueMap.keySet()) {
      if (null == preSelectionMap || preSelectionMap.containsKey(v)) {
        this.enumMap.remove(v, preSelectionMap.get(v));
      }
    }
    return this.enumMap;

  }



  public List<String> getEnumList() {
    final List<String> list = new ArrayList<>(this.enumMap.keySet());
    return list;
  }



  //  @Override
  //  public String toCqlString(Index indexName) throws SearchParseException {
  //
  //    // StringBuffer sb = new StringBuffer();
  //    // boolean enumSelected = false;
  //    // boolean enumDeselected = false;
  //
  //    if (!this.isEmpty(QueryType.CQL)) {
  //      final List<SearchCriterionBase> returnList = new ArrayList<SearchCriterionBase>();
  //
  //      returnList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
  //      // sb.append("(");
  //
  //      int i = 0;
  //      for (final Entry<String, Boolean> entry : this.enumMap.entrySet()) {
  //        if (entry.getValue() && i > 0) {
  //          // sb.append(" OR ");
  //          returnList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
  //        }
  //
  //        if (entry.getValue()) {
  //
  //
  //          // enumSelected = true;
  //          final String value = this.getCqlValue(indexName, this.getValueMap().get(entry.getKey()));
  //
  //
  //
  //          // gc.setSearchString(entry.getKey().name().toLowerCase());
  //          returnList.addAll(this.getSearchCriterionsForValue(indexName, value));
  //          // sb.append(valueMap.get(entry.getKey()));
  //          i++;
  //
  //
  //        } else {
  //          // enumDeselected = true;
  //          // allGenres = false;
  //        }
  //
  //      }
  //
  //      returnList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));
  //      return SearchCriterionBase.scListToCql(indexName, returnList, false);
  //
  //    }
  //
  //    return null;
  //  }


  //  public List<SearchCriterionBase> getSearchCriterionsForValue(Index indexName, String searchValue) {
  //    final List<SearchCriterionBase> scList = new ArrayList<SearchCriterionBase>();
  //    final SearchCriterionBase flexSc = new FlexibleStandardSearchCriterion(this.getCqlIndexes(indexName, searchValue), searchValue);
  //    scList.add(flexSc);
  //    return scList;
  //  }

  @Override
  public String getQueryStringContent() {
    if (!this.isEmpty(QueryType.INTERNAL)) {
      return this.getQueryString();
    }

    return null;
  }

  protected String getQueryString() {
    final StringBuilder sb = new StringBuilder();

    int i = 0;
    for (final Map.Entry<String, Boolean> entry : this.enumMap.entrySet()) {
      if (entry.getValue()) {
        if (0 < i) {
          sb.append("|");
        }

        sb.append(entry.getKey());
        i++;
      }
      /*
       * else { allChecked = false; }
       */
    }
    return sb.toString();
  }

  @Override
  public void parseQueryStringContent(String content) {

    for (final Map.Entry<String, Boolean> e : this.enumMap.entrySet()) {
      e.setValue(false);
    }


    // Split by '|', which have no backslash before and no other '|' after
    final String[] enumParts = content.split("(?<!\\\\)\\|(?!\\|)");
    for (final String part : enumParts) {

      /*
       * T v = Enum.valueOf(enumClass, part); if(v==null) { throw new
       * RuntimeException("Invalid visibility: " + part); }
       */
      if (null != part && !part.trim().isEmpty()) {
        this.enumMap.put(part, true);
      }

    }

  }

  /**
   * List is empty if either all genres or degrees are selected or all are deselected
   */
  @Override
  public boolean isEmpty(QueryType queryType) {

    final boolean anySelected = this.enumMap.containsValue(true);

    final boolean anyDeselected = this.enumMap.containsValue(false);

    return !(anySelected && anyDeselected);
  }



  public Map<String, Boolean> getEnumMap() {
    return this.enumMap;
  }

  public void setEnumMap(Map<String, Boolean> enumMap) {
    this.enumMap = enumMap;
  }



  //  public abstract String[] getCqlIndexes(Index indexName, String value);

  public abstract String getCqlValue(Index indexName, T value);



  public Map<String, T> getValueMap() {
    return this.valueMap;
  }


  public void setValueMap(Map<String, T> valueMap) {
    this.valueMap = valueMap;
  }

  @Override
  public Query toElasticSearchQuery() throws IngeTechnicalException {

    if (!this.isEmpty(QueryType.CQL)) {

      BoolQuery.Builder bq = new BoolQuery.Builder();
      for (final Map.Entry<String, Boolean> entry : this.enumMap.entrySet()) {


        if (entry.getValue()) {
          final String value = this.getCqlValue(Index.ESCIDOC_ALL, this.valueMap.get(entry.getKey()));
          bq = bq.should(SearchCriterionBase.baseElasticSearchQueryBuilder(this.getElasticIndexes(value), value));
        }

      }


      return bq.build()._toQuery();

    }


    return null;
  }

  public abstract String[] getElasticIndexes(String value);


}
