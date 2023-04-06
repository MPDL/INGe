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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ExistsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class ComponentOaStatusListSearchCriterion extends MapListSearchCriterion<String> {

  private static final InternationalizationHelper i18nHelper = FacesTools.findBean("InternationalizationHelper");

  public ComponentOaStatusListSearchCriterion() {
    super(ComponentOaStatusListSearchCriterion.getOaStatusMap(false));
  }

  public ComponentOaStatusListSearchCriterion(boolean reducedListFlag) {
    super(ComponentOaStatusListSearchCriterion.getOaStatusMap(reducedListFlag));
  }

  private static Map<String, String> getOaStatusMap(boolean reducedListFlag) {
    final MdsFileVO.OA_STATUS[] values = MdsFileVO.OA_STATUS.values();
    final Map<String, String> oaMap = new LinkedHashMap<String, String>();
    final Map<String, String> newMap = new LinkedHashMap<String, String>();

    if (values.length > 0) {
      for (int i = 0; i < values.length; i++) {
        if (reducedListFlag == true && MdsFileVO.OA_STATUS.CLOSED_ACCESS.name().equals(values[i].name())) {

        } else {
          oaMap.put(values[i].name(), i18nHelper.convertEnumToString(values[i]));
        }
      }
    }
    for (final Entry<String, String> entry : oaMap.entrySet()) {
      newMap.put(entry.getKey(), entry.getKey().toLowerCase());
    }

    return newMap;
  }

  @Override
  public String getCqlValue(Index indexName, String value) {
    return value;
  }

  @Override
  public String[] getElasticIndexes(String value) {
    return new String[] {PubItemServiceDbImpl.INDEX_FILE_OA_STATUS};

  }

  @Override
  public String getElasticSearchNestedPath() {
    return "files";
  }

  @Override
  public Query toElasticSearchQuery() throws IngeTechnicalException {

    if (!this.isEmpty(QueryType.CQL)) {

      BoolQuery.Builder bq = new BoolQuery.Builder();
      for (final Entry<String, Boolean> entry : this.enumMap.entrySet()) {

        if (entry.getValue()) {
          final String value = this.getCqlValue(Index.ESCIDOC_ALL, this.getValueMap().get(entry.getKey()));
          final String notSpecifiedValue =
              this.getCqlValue(Index.ESCIDOC_ALL, this.getValueMap().get(MdsFileVO.OA_STATUS.NOT_SPECIFIED.toString()));
          if (notSpecifiedValue.equals(value)) {
            bq = bq.should(BoolQuery.of(b -> b.mustNot(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_FILE_OA_STATUS))._toQuery()))
                ._toQuery());
          }
          bq = bq.should(SearchCriterionBase.baseElasticSearchQueryBuilder(this.getElasticIndexes(value), value));

        }

      }

      return bq.build()._toQuery();

    }

    return null;
  }
}
