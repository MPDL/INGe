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
package de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.pubman.web.search.SearchParseException;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.Parenthesis;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class OrganizationSearchCriterion extends StringOrHiddenIdSearchCriterion {

  // private Logger logger = Logger.getLogger(OrganizationSearchCriterion.class);

  private final static Logger logger = LogManager.getLogger(OrganizationSearchCriterion.class);
  private boolean includePredecessorsAndSuccessors;
  private boolean includeSource;

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
        ApplicationBean.INSTANCE.getOrganizationService().get(this.getHiddenId(), null);

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
  public String[] getElasticSearchFieldForHiddenId() {
    return new String[] {

    PubItemServiceDbImpl.INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIER,
        PubItemServiceDbImpl.INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIER};
  }

  @Override
  public String[] getElasticSearchFieldForSearchString() {
    return new String[] {

    PubItemServiceDbImpl.INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_NAME,
        PubItemServiceDbImpl.INDEX_METADATA_CREATOR_ORGANIZATION_NAME};
  }



  @Override
  public QueryBuilder toElasticSearchQuery() {
    if (getHiddenId() != null && !getHiddenId().trim().isEmpty()) {
      List<String> idList = new ArrayList<>();


      try {
        idList = ApplicationBean.INSTANCE.getOrganizationService().getChildIdPath(getHiddenId());

      } catch (Exception e) {
        logger.error("Error retrieving id path for organizational unit " + getHiddenId());
      }

      BoolQueryBuilder bq = QueryBuilders.boolQuery();
      bq.should(SearchCriterionBase.baseElasticSearchQueryBuilder(
          PubItemServiceDbImpl.INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIER,
          idList.toArray(new String[] {})));
      bq.should(SearchCriterionBase.baseElasticSearchQueryBuilder(
          PubItemServiceDbImpl.INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIER,
          idList.toArray(new String[] {})));
      if (includeSource) {
        bq.should(SearchCriterionBase.baseElasticSearchQueryBuilder(
            PubItemServiceDbImpl.INDEX_METADATA_SOURCES_CREATOR_PERSON_ORGANIZATION_IDENTIFIER,
            idList.toArray(new String[] {})));
      }


      return bq;
    } else {
      return super.toElasticSearchQuery();
    }
  }

  @Override
  public String getElasticSearchNestedPath() {
    return "metadata.creators";
  }



  public boolean isIncludeSource() {
    return includeSource;
  }

  public void setIncludeSource(boolean includeSource) {
    this.includeSource = includeSource;
  }



}
