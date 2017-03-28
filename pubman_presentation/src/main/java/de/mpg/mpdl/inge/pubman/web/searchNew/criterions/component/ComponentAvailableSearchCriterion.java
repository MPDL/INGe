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
package de.mpg.mpdl.inge.pubman.web.searchNew.criterions.component;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.ElasticSearchIndexField;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.SearchCriterionBase;

@SuppressWarnings("serial")
public abstract class ComponentAvailableSearchCriterion extends SearchCriterionBase {

  private ComponentAvailability selectedAvailability = ComponentAvailability.WHATEVER;

  private String forcedOperator;

  public enum ComponentAvailability {
    YES, NO, WHATEVER
  }



  @Override
  public String toCqlString(Index indexName) {

    String indexField = null;


    switch (indexName) {
      case ESCIDOC_ALL: {
        indexField = "escidoc.component.content.storage";
        break;
      }
      case ITEM_CONTAINER_ADMIN: {
        indexField = "\"/components/component/content/storage\"";
        break;
      }


    }


    switch (selectedAvailability) {
      case YES: {
        return indexField + "=\"" + escapeForCql(getStorageType()) + "\"";
      }

      case NO: {
        return indexField + "<>\"" + escapeForCql(getStorageType()) + "\"";
      }

      case WHATEVER:
        return null;
    }

    return null;

  }

  public QueryBuilder toElasticSearchQuery() {
    switch (selectedAvailability) {
      case YES: {
        return baseElasticSearchQueryBuilder(
            new ElasticSearchIndexField[] {new ElasticSearchIndexField("files.storage", true,
                "files")}, getStorageType());
      }

      case NO: {
        return QueryBuilders.boolQuery().mustNot(
            QueryBuilders.matchQuery("files.storage", getStorageType()));
      }

      case WHATEVER:
        return null;
    }
    return null;


  }

  @Override
  public String getElasticSearchNestedPath() {
    return "files";
  }

  public abstract String getStorageType();

  @Override
  public String toQueryString() {
    return getSearchCriterion() + "=\"" + getSelectedAvailability() + "\"";
  }

  @Override
  public void parseQueryStringContent(String content) {
    this.selectedAvailability = ComponentAvailability.valueOf(content);
  }



  @Override
  public boolean isEmpty(QueryType queryType) {
    return ComponentAvailability.WHATEVER.equals(selectedAvailability);
  }

  public ComponentAvailability getSelectedAvailability() {
    return selectedAvailability;
  }

  public void setSelectedAvailability(ComponentAvailability selectedAvailability) {
    this.selectedAvailability = selectedAvailability;
  }

  public String getForcedOperator() {
    return forcedOperator;
  }

  public void setForcedOperator(String forcedOperator) {
    this.forcedOperator = forcedOperator;
  }

}
