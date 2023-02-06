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
package de.mpg.mpdl.inge.pubman.web.search.criterions.standard;


import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;

@SuppressWarnings("serial")
public abstract class StandardSearchCriterion extends SearchCriterionBase {
  private String searchString;

  //  @Override
  //  public String toCqlString(Index indexName) throws SearchParseException {
  //    return this.baseCqlBuilder(this.getCqlIndexes(indexName), this.searchString);
  //  }

  @Override
  public String getQueryStringContent() {
    return SearchCriterionBase.escapeForQueryString(this.searchString);
  }

  @Override
  public void parseQueryStringContent(String content) {
    this.searchString = SearchCriterionBase.unescapeForQueryString(content);
  }

  //  public abstract String[] getCqlIndexes(Index indexName);

  public abstract String[] getElasticIndexes();

  public String getSearchString() {
    return this.searchString;
  }

  public void setSearchString(String searchString) {
    this.searchString = searchString;
  }

  @Override
  public boolean isEmpty(QueryType queryType) {
    return this.searchString == null || this.searchString.trim().isEmpty();
  }

  @Override
  public Query toElasticSearchQuery() {
    return SearchCriterionBase.baseElasticSearchQueryBuilder(this.getElasticIndexes(), this.searchString);
  }
}
