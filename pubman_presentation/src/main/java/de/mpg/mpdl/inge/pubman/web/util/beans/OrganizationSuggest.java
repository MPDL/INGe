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

package de.mpg.mpdl.inge.pubman.web.util.beans;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhrasePrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.OrganizationVOPresentation;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.impl.OrganizationServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;

import javax.faces.bean.ManagedBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author franke
 * 
 */
@ManagedBean(name = "OrganizationSuggest")
public class OrganizationSuggest {

  private List<OrganizationVOPresentation> results = new ArrayList<>();

  public OrganizationSuggest() throws Exception {
    // Get query from URL parameters
    final Map<String, String> parameters = FacesTools.getExternalContext().getRequestParameterMap();
    final String query = parameters.get("q");



    // Perform search request
    if (query != null) {


      OrganizationService organizationService = ApplicationBean.INSTANCE.getOrganizationService();

      Query qb = BoolQuery.of(b -> b
          .should(MatchPhrasePrefixQuery.of(m -> m.field(OrganizationServiceDbImpl.INDEX_METADATA_TITLE).query(query))._toQuery()).should(
              MatchPhrasePrefixQuery.of(m -> m.field(OrganizationServiceDbImpl.INDEX_METADATA_ALTERNATIVE_NAMES).query(query))._toQuery()))
          ._toQuery();

      //String[] returnFields = new String[] {OrganizationServiceDbImpl.INDEX_OBJECT_ID, OrganizationServiceDbImpl.INDEX_METADATA_TITLE, OrganizationServiceDbImpl.INDEX_METADATA_CITY};
      SearchRequest sr = SearchRequest.of(s -> s.query(qb).size(50));

      ResponseBody resp = organizationService.searchDetailed(sr, null);
      List<AffiliationDbVO> resultList = SearchUtils.getRecordListFromElasticSearchResponse(resp, AffiliationDbVO.class);

      for (final AffiliationDbVO affiliationVO : resultList) {
        final List<AffiliationDbVO> initList = new ArrayList<AffiliationDbVO>();
        initList.add(affiliationVO);
        final List<List<AffiliationDbVO>> pathList = this.getPaths(initList);

        for (final List<AffiliationDbVO> path : pathList) {
          final OrganizationVOPresentation organizationVOPresentation = new OrganizationVOPresentation();
          organizationVOPresentation.setIdentifier(affiliationVO.getObjectId());

          final String city = affiliationVO.getMetadata().getCity();
          final String countryCode = affiliationVO.getMetadata().getCountryCode();
          String address = "";

          if (city != null) {
            address += city;
          }

          if (city != null && countryCode != null) {
            address += ", ";
          }

          if (countryCode != null) {
            address += countryCode;
          }

          organizationVOPresentation.setAddress(address);

          String name = "";
          for (final AffiliationDbVO affVO : path) {
            if (!"".equals(name)) {
              name = name + ", ";
            }
            name = name + affVO.getMetadata().getName();
          }
          organizationVOPresentation.setName(name);

          this.results.add(organizationVOPresentation);

        }
      }
    }
  }

  private List<List<AffiliationDbVO>> getPaths(List<AffiliationDbVO> currentPath) throws Exception {
    final List<List<AffiliationDbVO>> result = new ArrayList<List<AffiliationDbVO>>();
    final AffiliationDbVO affiliationVO = currentPath.get(currentPath.size() - 1);

    if (affiliationVO != null) {
      if (affiliationVO.getParentAffiliation() == null) {
        result.add(currentPath);
      } else {

        final List<AffiliationDbVO> list = new ArrayList<AffiliationDbVO>();
        list.addAll(currentPath);
        final AffiliationDbVO parentVO = this.getAffiliation(affiliationVO.getParentAffiliation());
        list.add(parentVO);
        result.addAll(this.getPaths(list));
      }

    }

    return result;
  }

  private AffiliationDbVO getAffiliation(AffiliationDbRO affiliationRO) throws Exception {
    for (final AffiliationDbVO element : ApplicationBean.INSTANCE.getOuList()) {
      if (element.getObjectId().equals(affiliationRO.getObjectId())) {
        return element;
      }
    }

    return ApplicationBean.INSTANCE.getOrganizationService().get(affiliationRO.getObjectId(), null);
  }

  public List<OrganizationVOPresentation> getResults() {
    return results;
  }

  public void setResults(List<OrganizationVOPresentation> results) {
    this.results = results;
  }


}
