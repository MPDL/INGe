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
package de.mpg.mpdl.inge.pubman.web.searchNew.criterions.stringOrHiddenId;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.web.searchNew.SearchParseException;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.ElasticSearchIndexField;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.operators.Parenthesis;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;

@SuppressWarnings("serial")
public class OrganizationSearchCriterion extends StringOrHiddenIdSearchCriterion {

  // private Logger logger = Logger.getLogger(OrganizationSearchCriterion.class);

  private boolean includePredecessorsAndSuccessors;

  @Override
  public String[] getCqlIndexForHiddenId(Index indexName) {

    switch (indexName) {
      case ESCIDOC_ALL:
        return new String[] {"escidoc.publication.creator.compound.organization-path-identifiers"};
      case ITEM_CONTAINER_ADMIN:
        return new String[] {"\"/md-records/md-record/publication/creator/compound/organization-path-identifiers\""};
    }
    return null;
  }

  @Override
  public String[] getCqlIndexForSearchString(Index indexName) {

    switch (indexName) {
      case ESCIDOC_ALL:
        return new String[] {"escidoc.publication.creator.person.organization.title",
            "escidoc.publication.creator.organization.title"};
      case ITEM_CONTAINER_ADMIN:
        return new String[] {
            "\"/md-records/md-record/publication/creator/person/organization/title\"",
            "\"/md-records/md-record/publication/creator/organization/title\""};
    }
    return null;


  }

  /*
   * @Override public SearchCriterion getSearchCriterion() { return SearchCriterion.ORGUNIT; }
   */



  @Override
  public String toCqlString(Index indexName) throws SearchParseException {
    if (!this.includePredecessorsAndSuccessors) {
      return super.toCqlString(indexName);
    } else {

      try {
        final List<SearchCriterionBase> scList = new ArrayList<SearchCriterionBase>();
        int i = 0;
        scList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
        for (final AffiliationVO aff : this.retrievePredecessorsAndSuccessors(this.getHiddenId())) {
          if (i > 0) {
            scList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
          }

          final OrganizationSearchCriterion ous = new OrganizationSearchCriterion();
          ous.setSearchString(aff.getDefaultMetadata().getName());
          ous.setHiddenId(aff.getReference().getObjectId());
          scList.add(ous);
          i++;
        }
        scList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));

        return SearchCriterionBase.scListToCql(indexName, scList, false);
      } catch (final Exception e) {
        System.out
            .println("Error while retrieving affiliation from id" + e + ": " + e.getMessage());
        // logger.error("Error while retrieving affiliation from id", e);
        return super.toCqlString(indexName);
      }
    }
  }



  @Override
  public String toQueryString() {
    if (!this.includePredecessorsAndSuccessors) {
      return super.toQueryString();
    } else {
      return this.getSearchCriterion().name() + "=\""
          + SearchCriterionBase.escapeForQueryString(this.getSearchString()) + "||"
          + SearchCriterionBase.escapeForQueryString(this.getHiddenId()) + "||"
          + "includePresSuccs" + "\"";
    }


  }

  @Override
  public void parseQueryStringContent(String content) {
    // Split by '|', which have no backslash
    final String[] parts = content.split("(?<!\\\\)\\|\\|");

    this.setSearchString(SearchCriterionBase.unescapeForQueryString(parts[0]));
    if (parts.length > 1) {
      this.setHiddenId(SearchCriterionBase.unescapeForQueryString(parts[1]));
    }

    if (parts.length > 2) {

      if (parts[2].equals("includePresSuccs")) {
        this.includePredecessorsAndSuccessors = true;
      }


    }
  }



  public boolean isIncludePredecessorsAndSuccessors() {
    return this.includePredecessorsAndSuccessors;
  }

  public void setIncludePredecessorsAndSuccessors(boolean includePredecessorsAndSuccessors) {
    this.includePredecessorsAndSuccessors = includePredecessorsAndSuccessors;
  }


  private List<AffiliationVO> retrievePredecessorsAndSuccessors(String id) throws Exception {

    final List<AffiliationVO> allAffs = new ArrayList<AffiliationVO>();

    final AffiliationVO affiliation =
        XmlTransformingService.transformToAffiliation(ServiceLocator.getOrganizationalUnitHandler()
            .retrieve(this.getHiddenId()));
    allAffs.add(affiliation);

    final AffiliationVOPresentation affiliationPres = new AffiliationVOPresentation(affiliation);

    final List<AffiliationVO> sucessorsVO = affiliationPres.getSuccessors();

    for (final AffiliationVO affiliationVO : sucessorsVO) {
      allAffs.add(affiliationVO);
    }

    final List<AffiliationVO> predecessorsVO = affiliationPres.getPredecessors();

    for (final AffiliationVO affiliationVO : predecessorsVO) {
      allAffs.add(affiliationVO);
    }
    return allAffs;


  }

  // TODO: Organization path and predecessor/successor
  @Override
  public ElasticSearchIndexField[] getElasticSearchFieldForHiddenId() {
    return new ElasticSearchIndexField[] {
        new ElasticSearchIndexField("metadata.creators.person.organizations.identifier", true,
            "metadata.creators", "metadata.creators.person.organization"),
        new ElasticSearchIndexField("metadata.creators.organizations.identifier", true,
            "metadata.creators", "metadata.creators.organizations")};
  }

  @Override
  public ElasticSearchIndexField[] getElasticSearchFieldForSearchString() {
    return new ElasticSearchIndexField[] {
        new ElasticSearchIndexField("metadata.creators.person.organizations.name", true,
            "metadata.creators", "metadata.creators.person.organization"),
        new ElasticSearchIndexField("metadata.creators.organizations.name", true,
            "metadata.creators", "metadata.creators.organizations")};
  }

  @Override
  public String getElasticSearchNestedPath() {
    // TODO Auto-generated method stub
    return "metadata.creators";
  }



}
