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

package de.mpg.mpdl.inge.pubman.web;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO.State;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import de.mpg.mpdl.inge.util.PropertyReader;
import org.apache.log4j.Logger;

import javax.faces.bean.ManagedBean;
import java.util.List;
import java.util.Map;

import static de.mpg.mpdl.inge.es.dao.impl.ElasticSearchGenericDAOImpl.toJson;

/**
 * BackingBean for HomePage.jsp.
 * 
 * @author: Thomas Diebäcker, created 24.01.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT: 14.08.2007
 */
@ManagedBean(name = "HomePage")
@SuppressWarnings("serial")
public class HomePage extends BreadcrumbPage {
  private static final Logger logger = Logger.getLogger(HomePage.class);

  public static final String LOAD_HOMEPAGE = "loadHomePage";

  public HomePage() {}

  @Override
  public void init() {

    final Map<String, String> parameters = FacesTools.getExternalContext().getRequestParameterMap();
    if (parameters.containsKey("expired")) {
      this.error(this.getMessage("LoginErrorPage_loggedOffFromSystem"));
    } else if (parameters.containsKey("logout")) {
      this.info(this.getMessage("LogoutMessage"));
    }

    super.init();
  }

  /**
   * Reads the blog URL from the properties file. Needed for blogintegration on homepage
   * 
   * @return blodUrl as String
   */
  public String getBlogBaseUrl() {
    String url = "";
    try {
      url = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_BLOG_BASEURL);
    } catch (final Exception e) {
      HomePage.logger.error("Could not read property: 'inge.pubman.blog.baseUrl' from properties file.", e);
    }

    return url;
  }

  public boolean isDepositor() {
    return this.getLoginHelper().getIsDepositor();
  }

  public boolean isModerator() {
    return this.getLoginHelper().getIsModerator();
  }

  public List<PubItemVOPresentation> getLatest() throws Exception {

    PubItemService pi = ApplicationBean.INSTANCE.getPubItemService();
    BoolQuery.Builder bqb = new BoolQuery.Builder();
    bqb.must(SearchUtils.baseElasticSearchQueryBuilder(pi.getElasticSearchIndexFields(), PubItemServiceDbImpl.INDEX_PUBLIC_STATE,
        State.RELEASED.name()));

    SearchRequest.Builder ssb = new SearchRequest.Builder();
    ssb.query(bqb.build()._toQuery());
    ssb.from(0);
    ssb.size(4);
    ssb.sort(SortOptions.of(i -> i.field(SearchUtils.baseElasticSearchSortBuilder(pi.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_LATESTRELEASE_DATE, SortOrder.Desc))));

    SearchRequest sr = ssb.build();
    logger.info(toJson(sr));
    ResponseBody resp = pi.searchDetailed(sr, null);

    List<ItemVersionVO> pubItemList = SearchUtils.getRecordListFromElasticSearchResponse(resp, ItemVersionVO.class);

    return CommonUtils.convertToPubItemVOPresentationList(pubItemList);
  }

  @Override
  public boolean isItemSpecific() {
    return false;
  }
}
