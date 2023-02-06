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
package de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;

@SuppressWarnings("serial")
public abstract class StringOrHiddenIdSearchCriterion extends SearchCriterionBase {

  private String hiddenId;

  private String searchString;



  public String getHiddenId() {
    return this.hiddenId;
  }

  public void setHiddenId(String hiddenId) {
    this.hiddenId = hiddenId;
  }

  public String getSearchString() {
    return this.searchString;
  }

  public void setSearchString(String searchString) {
    this.searchString = searchString;
  }

  @Override
  public boolean isEmpty(QueryType queryType) {
    return (searchString == null || searchString.trim().isEmpty()) && (hiddenId == null || hiddenId.trim().isEmpty());
    //    return (this.searchString == null || this.searchString.trim().isEmpty());
  }

  //  @Override
  //  public String toCqlString(Index indexName) throws SearchParseException {
  //    if (this.hiddenId != null && !this.hiddenId.trim().isEmpty()) {
  //      return this.baseCqlBuilder(this.getCqlIndexForHiddenId(indexName), this.hiddenId);
  //    } else {
  //      return this.baseCqlBuilder(this.getCqlIndexForSearchString(indexName), this.searchString);
  //    }
  //  }



  @Override
  public String getQueryStringContent() {

    return SearchCriterionBase.escapeForQueryString(this.searchString) + "||" + SearchCriterionBase.escapeForQueryString(this.hiddenId);


  }



  @Override
  public Query toElasticSearchQuery() {
    if (this.hiddenId != null && !this.hiddenId.trim().isEmpty()) {
      return SearchCriterionBase.baseElasticSearchQueryBuilder(this.getElasticSearchFieldForHiddenId(), this.hiddenId);
    } else {
      return SearchCriterionBase.baseElasticSearchQueryBuilder(this.getElasticSearchFieldForSearchString(), this.searchString);
    }
  }


  public abstract String[] getElasticSearchFieldForHiddenId();

  public abstract String[] getElasticSearchFieldForSearchString();


  @Override
  public void parseQueryStringContent(String content) {
    // Split by '|', which have no backslash
    final String[] parts = content.split("(?<!\\\\)\\|\\|");

    this.searchString = SearchCriterionBase.unescapeForQueryString(parts[0]);
    if (parts.length > 1) {
      this.hiddenId = SearchCriterionBase.unescapeForQueryString(parts[1]);
    }



  }


  //  public abstract String[] getCqlIndexForHiddenId(Index indexName);
  //
  //  public abstract String[] getCqlIndexForSearchString(Index indexName);



}
