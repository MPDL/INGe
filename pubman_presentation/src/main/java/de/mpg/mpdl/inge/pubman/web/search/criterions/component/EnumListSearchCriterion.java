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

import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;

@SuppressWarnings("serial")
public abstract class EnumListSearchCriterion<T extends Enum<T>> extends SearchCriterionBase {


  private Map<T, Boolean> enumMap = new LinkedHashMap<>();

  private final Class<T> enumClass;

  public EnumListSearchCriterion(Class<T> clazz) {
    this.enumClass = clazz;
    this.initEnumMap();
  }


  public void initEnumMap() {

    for (T v : this.enumClass.getEnumConstants()) {
      this.enumMap.put(v, false);
    }

  }



  public List<T> getEnumList() {
    List<T> list = new ArrayList<>(this.enumMap.keySet());
    return list;
  }



  //  @Override
  //  public String toCqlString(Index indexName) {
  //
  //    final StringBuffer sb = new StringBuffer();
  //    boolean enumSelected = false;
  //    boolean enumDeselected = false;
  //
  //
  //
  //    // List<SearchCriterionBase> returnList = new ArrayList<SearchCriterionBase>();
  //
  //    // returnList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
  //    sb.append("(");
  //
  //    int i = 0;
  //    for (final Entry<T, Boolean> entry : this.enumMap.entrySet()) {
  //      if (entry.getValue() && i > 0) {
  //        sb.append(" OR ");
  //        // /returnList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
  //      }
  //
  //      if (entry.getValue()) {
  //
  //
  //        enumSelected = true;
  //        // ComponentVisibilitySearchCriterion gc = new ComponentVisibilitySearchCriterion();
  //        // gc.setSearchString(entry.getKey().name().toLowerCase());
  //        sb.append(this.getSearchValue(entry.getKey()));
  //        i++;
  //
  //
  //      } else {
  //        enumDeselected = true;
  //        // allGenres = false;
  //      }
  //
  //    }
  //
  //    // returnList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));
  //    sb.append(")");
  //
  //    if ((enumSelected && enumDeselected)) {
  //      return sb.toString();
  //    }
  //
  //    return null;
  //  }

  @Override
  public String toQueryString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getSearchCriterion() + "=\"");

    boolean allChecked = true;
    int i = 0;
    for (Map.Entry<T, Boolean> entry : this.enumMap.entrySet()) {
      if (entry.getValue()) {
        if (0 < i) {
          sb.append("|");
        }
        sb.append(entry.getKey().name());
        i++;
      } else {
        allChecked = false;
      }
    }

    sb.append("\"");
    if (!allChecked) {
      return sb.toString();
    }

    return null;
  }

  @Override
  public void parseQueryStringContent(String content) {

    for (Map.Entry<T, Boolean> e : this.enumMap.entrySet()) {
      e.setValue(false);
    }


    // Split by '|', which have no backslash before and no other '|' after
    String[] enumParts = content.split("(?<!\\\\)\\|(?!\\|)");
    for (String part : enumParts) {

      T v = Enum.valueOf(this.enumClass, part);
      if (null == v) {
        throw new RuntimeException("Invalid visibility: " + part);
      }
      this.enumMap.put(v, true);
    }

  }

  /**
   * List is empty if either all genres or degrees are selected or all are deselected
   */
  @Override
  public boolean isEmpty(QueryType queryType) {

    boolean anySelected = false;
    boolean anyDeselected = false;
    for (Map.Entry<T, Boolean> entry : this.enumMap.entrySet()) {
      if (entry.getValue()) {
        anySelected = true;
      } else {
        anyDeselected = true;
      }
    }



    return !(anySelected && anyDeselected);
  }



  public Map<T, Boolean> getEnumMap() {
    return this.enumMap;
  }

  public void setEnumMap(Map<T, Boolean> enumMap) {
    this.enumMap = enumMap;
  }

  public abstract String getSearchValue(T enumConstant);



}
