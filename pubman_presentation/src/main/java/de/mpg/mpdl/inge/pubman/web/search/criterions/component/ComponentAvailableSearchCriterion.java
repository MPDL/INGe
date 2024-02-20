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

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public abstract class ComponentAvailableSearchCriterion extends SearchCriterionBase {

  private ComponentAvailability selectedAvailability = ComponentAvailability.WHATEVER;

  private String forcedOperator;

  public enum ComponentAvailability
  {
    YES,
    NO,
    WHATEVER
  }



  //  @Override
  //  public String toCqlString(Index indexName) {
  //
  //    String indexField = null;
  //
  //
  //    switch (indexName) {
  //      case ESCIDOC_ALL: {
  //        indexField = "escidoc.component.content.storage";
  //        break;
  //      }
  //      case ITEM_CONTAINER_ADMIN: {
  //        indexField = "\"/components/component/content/storage\"";
  //        break;
  //      }
  //
  //
  //    }
  //
  //
  //    switch (this.selectedAvailability) {
  //      case YES: {
  //        return indexField + "=\"" + SearchCriterionBase.escapeForCql(this.getStorageType()) + "\"";
  //      }
  //
  //      case NO: {
  //        return indexField + "<>\"" + SearchCriterionBase.escapeForCql(this.getStorageType()) + "\"";
  //      }
  //
  //      case WHATEVER:
  //        return null;
  //    }
  //
  //    return null;
  //
  //  }

  @Override
  public Query toElasticSearchQuery() throws IngeTechnicalException {
    return switch (this.selectedAvailability) {
      case YES ->
          SearchCriterionBase.baseElasticSearchQueryBuilder(new String[] {PubItemServiceDbImpl.INDEX_FILE_STORAGE}, this.getStorageType());
      case NO -> {
        Query query = SearchCriterionBase.baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_FILE_STORAGE, this.getStorageType());
        yield BoolQuery.of(b -> b.mustNot(query))._toQuery();
      }
      case WHATEVER -> null;
    };


  }

  @Override
  public String getElasticSearchNestedPath() {
    return "files";
  }

  public abstract String getStorageType();

  @Override
  public String getQueryStringContent() {
    return this.selectedAvailability.name();
  }

  @Override
  public void parseQueryStringContent(String content) {
    this.selectedAvailability = ComponentAvailability.valueOf(content);
  }



  @Override
  public boolean isEmpty(QueryType queryType) {
    return ComponentAvailability.WHATEVER.equals(this.selectedAvailability);
  }

  public ComponentAvailability getSelectedAvailability() {
    return this.selectedAvailability;
  }

  public void setSelectedAvailability(ComponentAvailability selectedAvailability) {
    this.selectedAvailability = selectedAvailability;
  }

  public String getForcedOperator() {
    return this.forcedOperator;
  }

  public void setForcedOperator(String forcedOperator) {
    this.forcedOperator = forcedOperator;
  }

}
