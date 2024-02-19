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
package de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox;

import co.elastic.clients.elasticsearch._types.query_dsl.ExistsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class EmbargoDateAvailableSearchCriterion extends SearchCriterionBase {

  private boolean withEmbargoDate = false;

  //  @Override
  //  public String toCqlString(Index indexName) {
  //    if (this.withEmbargoDate) {
  //      switch (indexName) {
  //        case ESCIDOC_ALL:
  //          return "escidoc.component.file.available>\"''\"";
  //        case ITEM_CONTAINER_ADMIN:
  //          return "\"/components/component/md-records/md-record/file/available\">\"''\"";
  //      }
  //    }
  //
  //    return null;
  //  }

  @Override
  public String getQueryStringContent() {
    return String.valueOf(this.withEmbargoDate);
  }

  @Override
  public void parseQueryStringContent(String content) {
    this.withEmbargoDate = Boolean.parseBoolean(content);
  }

  @Override
  public boolean isEmpty(QueryType queryType) {
    return !this.withEmbargoDate;
  }

  public boolean isWithEmbargoDate() {
    return this.withEmbargoDate;
  }

  public void setWithEmbargoDate(boolean withEmbargoDate) {
    this.withEmbargoDate = withEmbargoDate;
  }

  @Override
  public Query toElasticSearchQuery() {
    return ExistsQuery.of(eq -> eq.field(PubItemServiceDbImpl.INDEX_FILE_METADATA_EMBARGO_UNTIL))._toQuery();
    //return null;
  }

  @Override
  public String getElasticSearchNestedPath() {

    return "files";
  }
}
