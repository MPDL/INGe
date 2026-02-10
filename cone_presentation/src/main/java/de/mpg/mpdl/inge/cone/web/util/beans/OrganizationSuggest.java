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

package de.mpg.mpdl.inge.cone.web.util.beans;

import java.util.ArrayList;
import java.util.List;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhrasePrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.impl.OrganizationServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;

public class OrganizationSuggest {

  private List<OrganizationResult> results = new ArrayList<>();
  private OrganizationService organizationService;

  public OrganizationSuggest(String query, OrganizationService organizationService) throws Exception {
    this.organizationService = organizationService;
    // Perform search request
    if (null != query && null != organizationService) {

      Query qb = BoolQuery.of(b -> b
          .should(MatchPhrasePrefixQuery.of(m -> m.field(OrganizationServiceDbImpl.INDEX_METADATA_TITLE).query(query))._toQuery()).should(
              MatchPhrasePrefixQuery.of(m -> m.field(OrganizationServiceDbImpl.INDEX_METADATA_ALTERNATIVE_NAMES).query(query))._toQuery()))
          ._toQuery();

      SearchRequest sr = SearchRequest.of(s -> s.query(qb).size(50));

      ResponseBody resp = organizationService.searchDetailed(sr, null);
      List<AffiliationDbVO> resultList = SearchUtils.getRecordListFromElasticSearchResponse(resp, AffiliationDbVO.class);

      for (AffiliationDbVO affiliationVO : resultList) {
        List<AffiliationDbVO> initList = new ArrayList<>();
        initList.add(affiliationVO);
        List<List<AffiliationDbVO>> pathList = this.getPaths(initList);

        for (List<AffiliationDbVO> path : pathList) {
          OrganizationResult organizationResult = new OrganizationResult();
          organizationResult.setIdentifier(affiliationVO.getObjectId());

          String city = affiliationVO.getMetadata().getCity();
          String countryCode = affiliationVO.getMetadata().getCountryCode();
          String address = "";

          if (null != city) {
            address += city;
          }

          if (null != city && null != countryCode) {
            address += ", ";
          }

          if (null != countryCode) {
            address += countryCode;
          }

          organizationResult.setAddress(address);

          String name = "";
          for (AffiliationDbVO affVO : path) {
            if (!name.isEmpty()) {
              name = name + ", ";
            }
            name = name + affVO.getMetadata().getName();
          }
          organizationResult.setName(name);

          this.results.add(organizationResult);

        }
      }
    }
  }

  private List<List<AffiliationDbVO>> getPaths(List<AffiliationDbVO> currentPath) throws Exception {
    List<List<AffiliationDbVO>> result = new ArrayList<>();
    AffiliationDbVO affiliationVO = currentPath.get(currentPath.size() - 1);

    if (null != affiliationVO) {
      if (null == affiliationVO.getParentAffiliation()) {
        result.add(currentPath);
      } else {

        List<AffiliationDbVO> list = new ArrayList<>(currentPath);
        AffiliationDbVO parentVO = this.getAffiliation(affiliationVO.getParentAffiliation());
        list.add(parentVO);
        result.addAll(this.getPaths(list));
      }

    }

    return result;
  }

  private AffiliationDbVO getAffiliation(AffiliationDbRO affiliationRO) throws Exception {
    return this.organizationService.get(affiliationRO.getObjectId(), null);
  }

  public List<OrganizationResult> getResults() {
    return this.results;
  }

  public void setResults(List<OrganizationResult> results) {
    this.results = results;
  }

}
