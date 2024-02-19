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

package de.mpg.mpdl.inge.pubman.web.desktop;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.es.dao.impl.ElasticSearchGenericDAOImpl;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.AnyFieldAndFulltextSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.AnyFieldSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.IdentifierSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.JsonUtil;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import jakarta.faces.bean.ManagedBean;

@ManagedBean(name = "Search")
@SuppressWarnings("serial")
public class Search extends FacesBean {
  private static final Logger logger = LogManager.getLogger(Search.class);

  private String searchString;
  private boolean includeFiles;

  public void startSearch() {
    String searchString = this.getSearchString();
    final boolean includeFiles = this.getIncludeFiles();

    // check if the searchString contains useful data
    if (searchString.trim().isEmpty()) {
      this.error(this.getMessage("search_NoCriteria"));
      return;
    }

    // Bugfix for pubman PUBMAN-248: Search: error using percent symbol in search
    if (searchString.trim().contains("%")) {
      this.error(this.getMessage("search_ParseError"));
      return;
    }

    // Bugfix for PUBMAN-2221: Remove Questionmark at the end
    if (searchString.trim().endsWith("?")) {
      searchString = searchString.trim().substring(0, searchString.length() - 1);
    }

    try {
      final Query qb = Search.generateElasticSearchRequest(searchString, includeFiles);
      FacesTools.getExternalContext().redirect("SearchResultListPage.jsp?esq="
          + URLEncoder.encode(JsonUtil.minifyJsonString(ElasticSearchGenericDAOImpl.toJson(qb)), StandardCharsets.UTF_8));
    } catch (final Exception e) {
      logger.error("Technical problem while retrieving the search results", e);
      this.error(this.getMessage("search_TechnicalError"));
    }
  }

  public static Query generateElasticSearchRequest(String searchString, boolean includeFiles) throws Exception {
    final List<SearchCriterionBase> criteria = new ArrayList<>();

    if (includeFiles) {
      final AnyFieldAndFulltextSearchCriterion anyFulltext = new AnyFieldAndFulltextSearchCriterion();
      anyFulltext.setSearchString(searchString);
      criteria.add(anyFulltext);
    } else {
      final AnyFieldSearchCriterion any = new AnyFieldSearchCriterion();
      any.setSearchString(searchString);
      criteria.add(any);
    }

    criteria.add(new LogicalOperator(SearchCriterionBase.SearchCriterion.OR_OPERATOR));

    final IdentifierSearchCriterion identifier = new IdentifierSearchCriterion();
    identifier.setSearchString(searchString);
    criteria.add(identifier);

    BoolQuery.Builder bqb = new BoolQuery.Builder();
    bqb.must(SearchUtils.baseElasticSearchQueryBuilder(ApplicationBean.INSTANCE.getPubItemService().getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_PUBLIC_STATE, de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO.State.RELEASED.name()));
    bqb.must(SearchCriterionBase.scListToElasticSearchQuery(criteria));

    return bqb.build()._toQuery();
  }

  public String getOpenSearchRequest() {
    final String requestDummy = "dummyTermToBeReplaced";

    try {
      final Query qb = Search.generateElasticSearchRequest(requestDummy, false);
      final String openSearchRequest = "SearchResultListPage.jsp?esq=" + URLEncoder.encode(qb.toString(), StandardCharsets.UTF_8);
      return openSearchRequest.replaceAll(requestDummy, "{searchTerms}");
    } catch (final Exception e) {
      logger.error("Technical problem while retrieving the search results", e);
      this.error(this.getMessage("search_TechnicalError"));
    }

    return "";
  }

  public void setSearchString(String searchString) {
    this.searchString = searchString;
  }

  public String getSearchString() {
    return this.searchString;
  }

  public void setIncludeFiles(boolean includeFiles) {
    this.includeFiles = includeFiles;
  }

  public boolean getIncludeFiles() {
    return this.includeFiles;
  }
}
