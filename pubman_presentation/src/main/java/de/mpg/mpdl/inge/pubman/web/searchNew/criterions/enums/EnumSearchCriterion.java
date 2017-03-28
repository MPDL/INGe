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
package de.mpg.mpdl.inge.pubman.web.searchNew.criterions.enums;

import org.elasticsearch.index.query.QueryBuilder;

import de.mpg.mpdl.inge.pubman.web.searchNew.SearchParseException;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.ElasticSearchIndexField;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.SearchCriterionBase;

@SuppressWarnings("serial")
public abstract class EnumSearchCriterion<T extends Enum<T>> extends SearchCriterionBase {

  private T selectedEnum;

  private Class<T> enumClass;

  public EnumSearchCriterion(Class<T> clazz) {
    this.enumClass = clazz;

  }

  @Override
  public String toCqlString(Index indexName) throws SearchParseException {
    return baseCqlBuilder(getCqlIndexes(indexName), getSearchString(getSelectedEnum()));
  }

  public QueryBuilder toElasticSearchQuery() {
    return baseElasticSearchQueryBuilder(getElasticIndexes(), getSearchString(getSelectedEnum()));
  }


  public abstract ElasticSearchIndexField[] getElasticIndexes();

  public abstract String getSearchString(T selectedEnum);

  public abstract String[] getCqlIndexes(Index indexName);

  @Override
  public String toQueryString() {
    return getSearchCriterion().name() + "=\"" + escapeForQueryString(getSelectedEnum().name())
        + "\"";
  }


  @Override
  public void parseQueryStringContent(String content) {

    this.selectedEnum = Enum.valueOf(enumClass, content);
    if (selectedEnum == null) {
      throw new RuntimeException("Invalid enum: " + content + " for class " + enumClass);
    }
  }

  /**
   * List is empty if either all genres or degrees are selected or all are deselected
   */
  @Override
  public boolean isEmpty(QueryType queryType) {
    return getSelectedEnum() == null;
  }

  public T getSelectedEnum() {
    return selectedEnum;
  }

  public void setSelectedEnum(T selectedEnum) {
    this.selectedEnum = selectedEnum;
  }

  public String getSelectedEnumString() {
    if (selectedEnum != null) {
      return selectedEnum.name();
    } else
      return null;

  }

  public void setSelectedEnumString(String name) {
    this.selectedEnum = Enum.valueOf(enumClass, name);
  }



}
