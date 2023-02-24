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

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class ClassificationSearchCriterion extends StandardSearchCriterion {


  private String classificationType;

  public ClassificationSearchCriterion() {

  }


  @Override
  public Query toElasticSearchQuery() {


    BoolQuery.Builder bq = new BoolQuery.Builder();
    bq.must(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_METADATA_SUBJECTS_TYPE, getClassificationType()));
    bq.must(super.toElasticSearchQuery());
    return NestedQuery.of(n -> n.path("metadata.subjects").query(bq.build()._toQuery()).scoreMode(ChildScoreMode.Avg))._toQuery();

  }

  //  @Override
  //  public String[] getCqlIndexes(Index indexName) {
  //
  //    switch (indexName) {
  //      case ESCIDOC_ALL:
  //        return new String[] {"escidoc.publication.subject"};
  //      case ITEM_CONTAINER_ADMIN:
  //        return new String[] {"\"/md-records/md-record/publication/subject\""};
  //    }
  //    return null;
  //
  //  }

  @Override
  public String[] getElasticIndexes() {
    return new String[] {PubItemServiceDbImpl.INDEX_METADATA_SUBJECTS_VALUE};

  }


  @Override
  public String toQueryString() {
    return this.getSearchCriterion().name() + "=\"" + SearchCriterionBase.escapeForQueryString(this.getClassificationType()) + "||"
        + SearchCriterionBase.escapeForQueryString(this.getSearchString()) + "\"";
  }

  @Override
  public void parseQueryStringContent(String content) {

    String[] parts = content.split("(?<!\\\\)\\|\\|");

    this.setClassificationType(SearchCriterionBase.unescapeForQueryString(parts[0]));
    this.setSearchString(SearchCriterionBase.unescapeForQueryString(parts[1]));
  }

  public String getClassificationType() {
    return this.classificationType;
  }

  public void setClassificationType(String classificationType) {
    this.classificationType = classificationType;
  }

  @Override
  public String getElasticSearchNestedPath() {
    return "metadata.subjects";
  }

  /*
   * @Override public SearchCriterion getSearchCriterion() { return SearchCriterion.CLASSIFICATION;
   * }
   */



}
