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
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class IdentifierSearchCriterion extends StandardSearchCriterion {


  private IdentifierVO.IdType selectedIdentifierType;



  @Override
  public Query toElasticSearchQuery() throws IngeTechnicalException {


    if (null == this.selectedIdentifierType) {
      return super.toElasticSearchQuery();
    } else {

      BoolQuery.Builder idQueryBuilder = new BoolQuery.Builder();
      idQueryBuilder
          .must(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_METADATA_IDENTIFIERS_TYPE, this.selectedIdentifierType.name()));
      idQueryBuilder.must(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_METADATA_IDENTIFIERS_ID, getSearchString()));

      BoolQuery.Builder sourceIdQueryBuilder = new BoolQuery.Builder();
      sourceIdQueryBuilder.must(
          baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_METADATA_SOURCES_IDENTIFIERS_TYPE, this.selectedIdentifierType.name()));
      sourceIdQueryBuilder
          .must(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_METADATA_SOURCES_IDENTIFIERS_ID, getSearchString()));

      return BoolQuery.of(b -> b
          .should(NestedQuery.of(n -> n.path("metadata.identifiers").query(idQueryBuilder.build()._toQuery()).scoreMode(ChildScoreMode.Avg))
              ._toQuery())
          .should(NestedQuery
              .of(n -> n.path("metadata.sources.identifiers").query(sourceIdQueryBuilder.build()._toQuery()).scoreMode(ChildScoreMode.Avg))
              ._toQuery())

      )._toQuery();
      /*
      return QueryBuilders.boolQuery().should(QueryBuilders.nestedQuery("metadata.identifiers", idQueryBuilder, ScoreMode.Avg))
          .should(QueryBuilders.nestedQuery("metadata.sources.identifiers", sourceIdQueryBuilder, ScoreMode.Avg));
      
       */

    }



  }


  //  @Override
  //  public String[] getCqlIndexes(Index indexName) {
  //
  //    switch (indexName) {
  //      case ESCIDOC_ALL:
  //        return new String[] {"escidoc.any-identifier", "escidoc.property.latest-release.objid"};
  //      case ITEM_CONTAINER_ADMIN:
  //        return new String[] {"\"/any-identifier\"", "\"/properties/latest-release/id\""};
  //    }
  //    return null;
  //
  //
  //  }

  /*
   * @Override public SearchCriterion getSearchCriterion() { return SearchCriterion.IDENTIFIER; }
   */
  @Override
  public String[] getElasticIndexes() {
    return new String[] {PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, PubItemServiceDbImpl.INDEX_PID,
        PubItemServiceDbImpl.INDEX_VERSION_PID, PubItemServiceDbImpl.INDEX_METADATA_IDENTIFIERS_ID,
        PubItemServiceDbImpl.INDEX_METADATA_SOURCES_IDENTIFIERS_ID};

  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }

  public IdentifierVO.IdType getSelectedIdentifierType() {
    return this.selectedIdentifierType;
  }

  public void setSelectedIdentifierType(IdentifierVO.IdType selectedIdentifierType) {
    this.selectedIdentifierType = selectedIdentifierType;
  }

  @Override
  public String getQueryStringContent() {
    if (null == this.selectedIdentifierType) {
      return super.getQueryStringContent();
    } else {
      return (null == this.selectedIdentifierType ? "" : SearchCriterionBase.escapeForQueryString(this.selectedIdentifierType.name()))
          + "||" + super.getQueryStringContent();
    }
  }

  @Override
  public void parseQueryStringContent(String content) {
    if (content.contains("||")) {
      String[] parts = content.split("(?<!\\\\)\\|\\|");
      this.selectedIdentifierType = IdentifierVO.IdType.valueOf(unescapeForQueryString(parts[0]));
      super.parseQueryStringContent(parts[1]);

    } else {
      super.parseQueryStringContent(content);
    }

  }

}
