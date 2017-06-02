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

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.pubman.web.search.SearchParseException;
import de.mpg.mpdl.inge.pubman.web.search.criterions.ElasticSearchIndexField;

@SuppressWarnings("serial")
public class PersonSearchCriterion extends StringOrHiddenIdSearchCriterion {


  // private static String PERSON_ROLE_INDEX = "escidoc.publication.creator.role";

  private String[] cqlIndexForHiddenId;
  private String[] cqlIndexForSearchString;

  private String[] cqlIndexForHiddenIdAdmin;
  private String[] cqlIndexForSearchStringAdmin;


  private CreatorRole selectedRole;

  public PersonSearchCriterion(SearchCriterion role) {
    this.searchCriterion = role;
  }

  @Override
  public String[] getCqlIndexForHiddenId(Index indexName) {

    switch (indexName) {
      case ESCIDOC_ALL:
        return this.cqlIndexForHiddenId;
      case ITEM_CONTAINER_ADMIN:
        return this.cqlIndexForHiddenIdAdmin;
    }

    return null;
  }

  @Override
  public String[] getCqlIndexForSearchString(Index indexName) {
    switch (indexName) {
      case ESCIDOC_ALL:
        return this.cqlIndexForSearchString;
      case ITEM_CONTAINER_ADMIN:
        return this.cqlIndexForSearchStringAdmin;
    }

    return null;
  }



  @Override
  public String toCqlString(Index indexName) throws SearchParseException {

    if (selectedRole == null) {
      this.cqlIndexForHiddenId = new String[] {"escidoc.publication.creator.person.identifier"};
      this.cqlIndexForSearchString =
          new String[] {"escidoc.publication.creator.person.compound.person-complete-name"};

      this.cqlIndexForHiddenIdAdmin =
          new String[] {"\"/md-records/md-record/publication/creator/person/identifier\""};
      this.cqlIndexForSearchStringAdmin =
          new String[] {"\"/md-records/md-record/publication/creator/person/compound/person-complete-name\""};

      return super.toCqlString(indexName);
    } else {
      final String roleUri =
          CreatorVO.CreatorRole.valueOf(this.getSearchCriterion().name()).getUri();
      final String roleAbbr = roleUri.substring(roleUri.lastIndexOf('/') + 1, roleUri.length());

      this.cqlIndexForHiddenId =
          new String[] {"escidoc.publication.creator.compound.role-person." + roleAbbr};
      this.cqlIndexForSearchString =
          new String[] {"escidoc.publication.creator.compound.role-person." + roleAbbr};

      this.cqlIndexForHiddenIdAdmin =
          new String[] {"\"/md-records/md-record/publication/creator/person/compound/role-person/"
              + roleAbbr + "\""};
      this.cqlIndexForSearchStringAdmin =
          new String[] {"\"/md-records/md-record/publication/creator/person/compound/role-person/"
              + roleAbbr + "\""};

      /*
       * StringBuilder sb = new StringBuilder(); sb.append("("); sb.append(superQuery);
       * sb.append(" and "); sb.append(PERSON_ROLE_INDEX); sb.append("=\"");
       * sb.append(escapeForCql(roleUri) + "\")");
       */
      return super.toCqlString(indexName);
    }



  }

  @Override
  public String toQueryString() {

    return getSearchCriterion().name() + "=\"" + escapeForQueryString(getSearchString()) + "||"
        + escapeForQueryString(getHiddenId()) + "||"
        + escapeForQueryString(selectedRole == null ? "" : selectedRole.name()) + "\"";


  }

  @Override
  public void parseQueryStringContent(String content) {
    // Split by '|', which have no backslash
    String[] parts = content.split("(?<!\\\\)\\|\\|");

    setSearchString(unescapeForQueryString(parts[0]));
    if (parts.length > 1) {
      setHiddenId(unescapeForQueryString(parts[1]));
    }
    if (parts.length > 2) {
      String role = parts[2];


      setSelectedRole(role.isEmpty() ? null : CreatorRole.valueOf(role));
    }
  }

  @Override
  public QueryBuilder toElasticSearchQuery() {



    if (selectedRole == null) {
      if (this.getHiddenId() != null && !this.getHiddenId().trim().isEmpty()) {
        return this.baseElasticSearchQueryBuilder(this.getElasticSearchFieldForHiddenId(),
            this.getHiddenId());
      } else {
        return this.baseElasticSearchQueryBuilder(this.getElasticSearchFieldForSearchString(),
            this.getSearchString(), MultiMatchQueryBuilder.Type.CROSS_FIELDS);
      }

    } else {
      final String roleUri = selectedRole.getUri();
      BoolQueryBuilder bq =
          QueryBuilders.boolQuery().must(
              QueryBuilders.matchQuery("metadata.creators.role", roleUri));

      if (this.getHiddenId() != null && !this.getHiddenId().trim().isEmpty()) {
        bq =
            bq.must(this.baseElasticSearchQueryBuilder(this.getElasticSearchFieldForHiddenId(),
                this.getHiddenId()));
      } else {
        bq =
            bq.must(this.baseElasticSearchQueryBuilder(this.getElasticSearchFieldForSearchString(),
                this.getSearchString(), MultiMatchQueryBuilder.Type.CROSS_FIELDS));
      }
      return bq;
    }

  }

  @Override
  public ElasticSearchIndexField[] getElasticSearchFieldForHiddenId() {
    return new ElasticSearchIndexField[] {new ElasticSearchIndexField(
        "metadata.creators.person.identifier.id", true, "metadata.creators")};
  }

  @Override
  public ElasticSearchIndexField[] getElasticSearchFieldForSearchString() {
    return new ElasticSearchIndexField[] {
        new ElasticSearchIndexField("metadata.creators.person.familyName", true,
            "metadata.creators"),
        new ElasticSearchIndexField("metadata.creators.person.givenName", true, "metadata.creators")};
  }

  @Override
  public String getElasticSearchNestedPath() {
    return "metadata.creators";
  }

  public CreatorRole getSelectedRole() {
    return selectedRole;
  }


  public void setSelectedRole(CreatorRole selectedRole) {
    this.selectedRole = selectedRole;
  }
  /*
   * @Override public SearchCriterion getSearchCriterion() { return searchCriterion;
   * 
   * }
   */



}
