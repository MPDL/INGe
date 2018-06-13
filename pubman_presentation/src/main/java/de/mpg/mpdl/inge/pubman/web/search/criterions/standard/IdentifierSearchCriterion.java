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

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class IdentifierSearchCriterion extends StandardSearchCriterion {


  private IdType selectedIdentifierType;



  @Override
  public QueryBuilder toElasticSearchQuery() {


    if (getSelectedIdentifierType() == null) {
      return super.toElasticSearchQuery();
    } else {
      BoolQueryBuilder bq = QueryBuilders.boolQuery();
      bq.must(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_METADATA_IDENTIFIERS_TYPE, getSelectedIdentifierType().name()));
      bq.must(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_METADATA_IDENTIFIERS_ID, getSearchString()));
      return QueryBuilders.nestedQuery("metadata.identifiers", bq, ScoreMode.Avg);
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
        PubItemServiceDbImpl.INDEX_VERSION_PID, PubItemServiceDbImpl.INDEX_METADATA_IDENTIFIERS_ID};

  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }

  public IdType getSelectedIdentifierType() {
    return selectedIdentifierType;
  }

  public void setSelectedIdentifierType(IdType selectedIdentifierType) {
    this.selectedIdentifierType = selectedIdentifierType;
  }


}
