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
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.SimpleQueryStringQuery;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

@SuppressWarnings("serial")
public class AnyFieldAndFulltextSearchCriterion extends FulltextSearchCriterion {



  @Override
  public Query toElasticSearchQuery() throws IngeTechnicalException {

    BoolQuery.Builder qb = new BoolQuery.Builder();
    //Use simple query for searching all fields
    qb.should(SimpleQueryStringQuery.of(s -> s.query(getSearchString()).analyzeWildcard(true).defaultOperator(Operator.And))._toQuery());
    qb.should(super.toElasticSearchQuery());
    return qb.build()._toQuery();
  }

  //  @Override
  //  public String[] getCqlIndexes(Index indexName) {
  //
  //    switch (indexName) {
  //      case ESCIDOC_ALL:
  //        return new String[] {"escidoc.metadata", "escidoc.fulltext"};
  //      case ITEM_CONTAINER_ADMIN:
  //        return new String[] {"\"/metadata\"", "\"/fulltext\""};
  //    }
  //    return null;
  //
  //  }



  /*
   * @Override public SearchCriterion getSearchCriterion() { return SearchCriterion.ANYFULLTEXT; }
   */


}
