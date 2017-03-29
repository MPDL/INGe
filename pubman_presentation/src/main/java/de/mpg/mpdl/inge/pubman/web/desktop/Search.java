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
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;

import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.SearchCriterionBase.Index;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.standard.AnyFieldAndFulltextSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.standard.AnyFieldSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.standard.IdentifierSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;

@ManagedBean(name = "Search")
@SuppressWarnings("serial")
public class Search extends FacesBean {
  private static final Logger logger = Logger.getLogger(Search.class);

  private String searchString;
  private boolean includeFiles;

  public void startSearch() {
    String searchString = this.getSearchString();
    final boolean includeFiles = this.getIncludeFiles();

    // check if the searchString contains useful data
    if (searchString.trim().equals("")) {
      FacesBean.error(this.getMessage("search_NoCriteria"));
      return;
    }

    // Bugfix for pubman PUBMAN-248: Search: error using percent symbol in search
    if (searchString.trim().contains("%")) {
      FacesBean.error(this.getMessage("search_ParseError"));
      return;
    }

    // Bugfix for PUBMAN-2221: Remove Questionmark at the end
    if (searchString.trim().endsWith("?")) {
      searchString = searchString.trim().substring(0, searchString.length() - 1);
    }

    try {
      final QueryBuilder qb = Search.generateElasticSearchRequest(searchString, includeFiles);
      FacesTools.getExternalContext().redirect(
          "SearchResultListPage.jsp?esq=" + URLEncoder.encode(qb.toString(), "UTF-8"));
    } catch (final de.mpg.mpdl.inge.search.parser.ParseException e) {
      Search.logger.error("Search criteria includes some lexical error", e);
      FacesBean.error(this.getMessage("search_ParseError"));
    } catch (final Exception e) {
      Search.logger.error("Technical problem while retrieving the search results", e);
      FacesBean.error(this.getMessage("search_TechnicalError"));
    }
  }

  public static QueryBuilder generateElasticSearchRequest(String searchString, boolean includeFiles)
      throws Exception {

    final List<SearchCriterionBase> criteria = new ArrayList<SearchCriterionBase>();


    if (includeFiles) {
      final AnyFieldAndFulltextSearchCriterion anyFulltext =
          new AnyFieldAndFulltextSearchCriterion();
      anyFulltext.setSearchString(searchString);
      criteria.add(anyFulltext);
    } else {
      final AnyFieldSearchCriterion any = new AnyFieldSearchCriterion();
      any.setSearchString(searchString);
      criteria.add(any);
    }

    criteria.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));

    final IdentifierSearchCriterion identifier = new IdentifierSearchCriterion();
    identifier.setSearchString(searchString);
    criteria.add(identifier);

    /*
     * CollectionSearchCriterion collection = new CollectionSearchCriterion();
     * collection.setSearchString(searchString); criteria.add(collection);
     * 
     * CreatedBySearchCriterion createdBy = new CreatedBySearchCriterion();
     * createdBy.setHiddenId(searchString); criteria.add(createdBy);
     */
    
    final QueryBuilder qb = SearchCriterionBase.scListToElasticSearchQuery(criteria);
    //final String cql = SearchCriterionBase.scListToCql(Index.ESCIDOC_ALL, criteria, true);


    /*
     * 
     * String cql = "";
     * 
     * ArrayList<MetadataSearchCriterion> criteria = new ArrayList<MetadataSearchCriterion>();
     * 
     * if( includeFiles == true ) { criteria.add( new MetadataSearchCriterion(
     * MetadataSearchCriterion.CriterionType.ANY_INCLUDE, searchString ) ); criteria.add( new
     * MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.IDENTIFIER, searchString,
     * MetadataSearchCriterion.LogicalOperator.OR ) ); } else { criteria.add( new
     * MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.ANY, searchString ) );
     * criteria.add( new MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.IDENTIFIER,
     * searchString, MetadataSearchCriterion.LogicalOperator.OR ) ); } criteria.add( new
     * MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.CONTEXT_OBJECTID,
     * searchString, MetadataSearchCriterion.LogicalOperator.NOT ) ); criteria.add( new
     * MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.CREATED_BY_OBJECTID,
     * searchString, MetadataSearchCriterion.LogicalOperator.NOT ) ); criteria.add( new
     * MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.OBJECT_TYPE, "item",
     * MetadataSearchCriterion.LogicalOperator.AND ) );
     * 
     * ArrayList<String> contentTypes = new ArrayList<String>(); String contentTypeIdPublication =
     * PropertyReader.getProperty( PROPERTY_CONTENT_MODEL ); contentTypes.add(
     * contentTypeIdPublication );
     * 
     * MetadataSearchQuery query = new MetadataSearchQuery( contentTypes, criteria ); cql =
     * query.getCqlQuery();
     */
    return qb;
  }

  public String getOpenSearchRequest() {
    final String requestDummy = "dummyTermToBeReplaced";

    try {
      final QueryBuilder qb = Search.generateElasticSearchRequest(requestDummy, false);
      final String openSearchRequest =
          "SearchResultListPage.jsp?esq=" + URLEncoder.encode(qb.toString(), "UTF-8");
      return openSearchRequest.replaceAll(requestDummy, "{searchTerms}");
    } catch (final de.mpg.mpdl.inge.search.parser.ParseException e) {
      Search.logger.error("Search criteria includes some lexical error", e);
      FacesBean.error(this.getMessage("search_ParseError"));
    } catch (final Exception e) {
      Search.logger.error("Technical problem while retrieving the search results", e);
      FacesBean.error(this.getMessage("search_TechnicalError"));
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
