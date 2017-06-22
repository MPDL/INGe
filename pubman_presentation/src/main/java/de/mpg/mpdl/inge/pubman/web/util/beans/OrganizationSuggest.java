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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.OrganizationVOPresentation;

/**
 * @author franke
 * 
 */
@ManagedBean(name = "OrganizationSuggest")
@SuppressWarnings("serial")
public class OrganizationSuggest extends EditItemBean {


  public OrganizationSuggest() throws Exception {
    // Get query from URL parameters
    final Map<String, String> parameters = FacesTools.getExternalContext().getRequestParameterMap();
    final String query = parameters.get("q");

    // Perform search request
    if (query != null) {
      QueryBuilder qb =
          QueryBuilders.boolQuery().should(
              QueryBuilders.multiMatchQuery(query, "defaultMetadata.name",
                  "defaultMetadata.alternativeNames"));

      SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, 50, 0);

      SearchRetrieveResponseVO<AffiliationVO> response =
          ApplicationBean.INSTANCE.getOrganizationService().search(srr, null);

      for (final SearchRetrieveRecordVO<AffiliationVO> rec : response.getRecords()) {
        final AffiliationVO affiliationVO = rec.getData();
        final List<AffiliationVO> initList = new ArrayList<AffiliationVO>();
        initList.add(affiliationVO);
        final List<List<AffiliationVO>> pathList = this.getPaths(initList);

        for (final List<AffiliationVO> path : pathList) {
          final OrganizationVOPresentation organizationVOPresentation =
              new OrganizationVOPresentation();
          organizationVOPresentation.setIdentifier(affiliationVO.getReference().getObjectId());

          final String city = affiliationVO.getDefaultMetadata().getCity();
          final String countryCode = affiliationVO.getDefaultMetadata().getCountryCode();
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
          for (final AffiliationVO affVO : path) {
            if (!"".equals(name)) {
              name = name + ", ";
            }
            name = name + affVO.getDefaultMetadata().getName();
          }
          organizationVOPresentation.setName(name);
          organizationVOPresentation.setBean(this);

          this.getCreatorOrganizations().add(organizationVOPresentation);
        }
      }
    }
  }

  private List<List<AffiliationVO>> getPaths(List<AffiliationVO> currentPath) throws Exception {
    final List<List<AffiliationVO>> result = new ArrayList<List<AffiliationVO>>();
    final AffiliationVO affiliationVO = currentPath.get(currentPath.size() - 1);

    if (affiliationVO != null) {
      if (affiliationVO.getParentAffiliations().isEmpty()) {
        result.add(currentPath);
      } else {
        for (final AffiliationRO parent : affiliationVO.getParentAffiliations()) {
          final List<AffiliationVO> list = new ArrayList<AffiliationVO>();
          list.addAll(currentPath);
          final AffiliationVO parentVO = this.getAffiliation(parent);
          list.add(parentVO);
          result.addAll(this.getPaths(list));
        }
      }
    }

    return result;
  }

  private AffiliationVO getAffiliation(AffiliationRO affiliationRO) throws Exception {
    final ApplicationBean applicationBean =
        ((ApplicationBean) FacesTools.findBean("ApplicationBean"));

    for (final AffiliationVO element : applicationBean.getOuList()) {
      if (element.getReference().equals(affiliationRO)) {
        return element;
      }
    }

    return ApplicationBean.INSTANCE.getOrganizationService().get(affiliationRO.getObjectId(), null);
  }
}
