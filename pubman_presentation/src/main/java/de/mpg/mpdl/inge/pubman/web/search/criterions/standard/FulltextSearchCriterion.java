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

import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.HasChildQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class FulltextSearchCriterion extends StandardSearchCriterion {



  @Override
  public Query toElasticSearchQuery() {


    Highlight hb = Highlight.of(h -> h.fields(PubItemServiceDbImpl.INDEX_FULLTEXT_CONTENT, new HighlightField.Builder().build())
        .preTags("<span class=\"searchHit\">").postTags("</span>"));

    Query childQueryBuilder = HasChildQuery.of(h -> h.type("file")
        .query(SearchCriterionBase.baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_FULLTEXT_CONTENT, getSearchString()))
        .scoreMode(ChildScoreMode.Avg).innerHits(i -> i.highlight(hb).source(sc -> sc
            //.fetch(true)
            .filter(f -> f.excludes(PubItemServiceDbImpl.INDEX_FULLTEXT_CONTENT))

    ))

    )._toQuery();

    /*
    HasChildQueryBuilder childQueryBuilder = JoinQueryBuilders.hasChildQuery("file",
        SearchCriterionBase.baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_FULLTEXT_CONTENT, getSearchString()), ScoreMode.Avg);
    
     */



    /*
    HighlightBuilder hb =
        new HighlightBuilder().field(PubItemServiceDbImpl.INDEX_FULLTEXT_CONTENT).preTags("<span class=\"searchHit\">").postTags("</span>");
    */

    //FetchSourceContext fs = new FetchSourceContext(true, null, new String[] {PubItemServiceDbImpl.INDEX_FULLTEXT_CONTENT});
    //childQueryBuilder.innerHit(new InnerHitBuilder().setHighlightBuilder(hb).setFetchSourceContext(fs));

    return childQueryBuilder;
  }

  //  @Override
  //  public String[] getCqlIndexes(Index indexName) {
  //
  //    switch (indexName) {
  //      case ESCIDOC_ALL:
  //        return new String[] {"escidoc.fulltext"};
  //      case ITEM_CONTAINER_ADMIN:
  //        return new String[] {"\"/fulltext\""};
  //    }
  //    return null;
  //
  //  }

  // TODO: Add fulltext index
  @Override
  public String[] getElasticIndexes() {
    return null;

  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }


  /*
   * @Override public SearchCriterion getSearchCriterion() { return SearchCriterion.ANYFULLTEXT; }
   */


}
