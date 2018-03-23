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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.json.JSONException;
import org.json.JSONObject;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.exceptions.PubManVersionNotAvailableException;
import de.mpg.mpdl.inge.pubman.web.export.ExportItemsSessionBean;
import de.mpg.mpdl.inge.pubman.web.search.SearchRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.service.pubman.SearchAndExportService;
import de.mpg.mpdl.inge.util.PropertyReader;

@ManagedBean(name = "SearchAndExportPage")
@SessionScoped
@SuppressWarnings("serial")
public class SearchAndExportPage extends BreadcrumbPage {
  private static final Logger logger = Logger.getLogger(SearchAndExportPage.class);

  private final SearchAndExportService saes = ApplicationBean.INSTANCE.getSearchAndExportService();

  private SearchSortCriteria.SortOrder sortOrder;

  private String esQuery;
  private String sortingKey;
  private String limit;
  private String offset;

  private final int maxLimit = Integer.parseInt(PropertyReader.getProperty("inge.search.and.export.max.limit"));

  public SearchAndExportPage() {}

  // Wird bei jedem Aufruf des Beans ausgefuehrt -> SearchAndExportPage.jsp: <f:event type="preRenderView" listener="#{SearchAndExportPage.init}" />
  @Override
  public void init() {
    super.init();

    String oldQuery = this.esQuery;

    if (FacesTools.getCurrentInstance().getRenderResponse()) {
      final HttpServletRequest request = FacesTools.getRequest();
      Map<String, String> paramMap = null;
      try {
        paramMap = CommonUtils.getDecodedUrlParameterMap(request.getQueryString());
      } catch (final UnsupportedEncodingException e) {
        SearchAndExportPage.logger.error("Error during reading GET parameters.", e);
      }

      this.esQuery = paramMap.get(SearchRetrieverRequestBean.parameterElasticSearchQuery);
    }

    if (this.esQuery == null && oldQuery != null) {
      this.esQuery = oldQuery;
    } else if (this.esQuery == null) {
      this.esQuery = PropertyReader.getProperty("inge.search.and.export.default.query");
    }
  }

  // Wird nur 1x während der Lebenszeit des Beans aufgerufen
  @PostConstruct
  public void postConstruct() {
    this.limit = PropertyReader.getProperty("inge.search.and.export.maximum.records");
    this.offset = PropertyReader.getProperty("inge.search.and.export.start.record");
    this.sortOrder = PropertyReader.getProperty("inge.search.and.export.default.sort.order").equalsIgnoreCase("ascending")
        ? SearchSortCriteria.SortOrder.ASC
        : SearchSortCriteria.SortOrder.DESC;
    this.sortingKey = PropertyReader.getProperty("inge.search.and.export.default.sort.key");
  }

  @Override
  public boolean isItemSpecific() {
    return false;
  }

  public void searchAndExport() {
    final SearchAndExportRetrieveRequestVO saerrVO = parseInput();
    final SearchAndExportResultVO searchAndExportResultVO = search(saerrVO);
    createExportResponse(searchAndExportResultVO);
    FacesTools.getCurrentInstance().responseComplete();
  }

  private void createExportResponse(SearchAndExportResultVO searchAndExportResultVO) {
    final String contentType = searchAndExportResultVO.getTargetMimetype();
    FacesTools.getResponse().setContentType(contentType);
    FacesTools.getResponse().setHeader("Content-disposition", "attachment; filename=" + searchAndExportResultVO.getFileName());
    try {
      final OutputStream out = FacesTools.getResponse().getOutputStream();
      out.write(searchAndExportResultVO.getResult());
      out.close();
    } catch (final Exception e) {
      throw new RuntimeException("Cannot put export result in HttpResponse body:", e);
    }
  }

  public void curl() {
    String curl = getCurl();
    createCurlResponse(curl);
    FacesTools.getCurrentInstance().responseComplete();
  }

  private void createCurlResponse(String curl) {
    FacesTools.getResponse().setContentType(FileFormatVO.TXT_MIMETYPE);
    FacesTools.getResponse().setHeader("Content-disposition", "attachment; filename=curl.txt");
    try {
      final OutputStream out = FacesTools.getResponse().getOutputStream();
      out.write(curl.getBytes(StandardCharsets.UTF_8));
      out.close();
    } catch (final Exception e) {
      throw new RuntimeException("Cannot put curl in HttpResponse body:", e);
    }
  }

  private SearchAndExportResultVO search(SearchAndExportRetrieveRequestVO saerrVO) {
    SearchAndExportResultVO searchAndExportResultVO = null;

    try {
      searchAndExportResultVO = saes.searchAndExportItems(saerrVO, this.getLoginHelper().getAuthenticationToken());
    } catch (final Exception e) {
      throw new RuntimeException("Cannot retrieve export data", e);
    }

    return searchAndExportResultVO;
  }

  private SearchAndExportRetrieveRequestVO parseInput() {
    try {
      final ExportItemsSessionBean sb = (ExportItemsSessionBean) FacesTools.findBean("ExportItemsSessionBean");
      final ExportFormatVO curExportFormat = sb.getCurExportFormatVO();

      QueryBuilder queryBuilder = (QueryBuilder) QueryBuilders.wrapperQuery(this.esQuery);

      ArrayList<SearchSortCriteria> sortCriterias = new ArrayList<>();
      if (this.sortingKey != null && this.sortingKey.length() > 0) {
        SearchSortCriteria searchSortCriteria = new SearchSortCriteria(this.sortingKey, this.sortOrder);
        sortCriterias.add(searchSortCriteria);
      }

      int _limit = Integer.parseInt(this.limit);
      int _offset = Integer.parseInt(this.offset);

      SearchAndExportRetrieveRequestVO saerrVO =
          new SearchAndExportRetrieveRequestVO(curExportFormat.getName(), curExportFormat.getFileFormat().getName(),
              curExportFormat.getId(), queryBuilder, _limit, _offset, sortCriterias.toArray(new SearchSortCriteria[sortCriterias.size()]));

      return saerrVO;

    } catch (final Exception e) {
      throw new RuntimeException("Cannot parse input", e);
    }
  }

  public String getEsQuery() {
    return this.esQuery;
  }

  public void setEsQuery(String esQuery) {
    this.esQuery = esQuery;
  }

  public String getLimit() {
    return this.limit;
  }

  public void setLimit(String limit) {
    this.limit = limit;
  }

  public String getOffset() {
    return this.offset;
  }

  public void setOffset(String offset) {
    this.offset = offset;
  }

  public String getSortingKey() {
    return this.sortingKey;
  }

  public void setSortingKey(String sortingKey) {
    this.sortingKey = sortingKey;
  }

  public SelectItem[] getSortOptions() {
    final SelectItem ascending = new SelectItem(SearchSortCriteria.SortOrder.ASC, "ascending");
    final SelectItem descending = new SelectItem(SearchSortCriteria.SortOrder.DESC, "descending");

    final SelectItem[] sortOptions = new SelectItem[] { //
        ascending, //
        descending};

    return sortOptions;
  }

  public SearchSortCriteria.SortOrder getSortOrder() {
    return this.sortOrder;
  }

  public void setSortOrder(SearchSortCriteria.SortOrder sortOption) {
    this.sortOrder = sortOption;
  }

  public int getMaxLimit() {
    return this.maxLimit;
  }

  public String getAtomFeedLink() throws PubManVersionNotAvailableException, UnsupportedEncodingException {
    if (this.esQuery == null) {
      return null;
    }

    return ApplicationBean.INSTANCE.getPubmanInstanceUrl() + "/rest/feed/search?q="
        + URLEncoder.encode(this.esQuery, StandardCharsets.UTF_8.toString());
  }

  private String getCurl() {
    if (this.esQuery == null) {
      return null;
    }

    try {
      StringBuilder sb = new StringBuilder();
      sb.append("curl -X POST ");
      sb.append(ApplicationBean.INSTANCE.getPubmanInstanceUrl());
      sb.append("/rest/items/searchAndExport ");
      sb.append("-H 'Cache-Control: no-cache' -H 'Content-Type: application/json' ");
      sb.append("-d '");
      sb.append(getSearchString());
      sb.append("'");

      return sb.toString();
    } catch (PubManVersionNotAvailableException e) {
      throw new RuntimeException("Cannot get curl", e);
    }
  }

  public String getSearchString() {
    if (this.esQuery == null) {
      return null;
    }

    SearchAndExportRetrieveRequestVO searchAndExportRetrieveRequestVO = parseInput();

    StringBuilder sb = new StringBuilder();

    sb.append("{");

    sb.append("\"query\" : ");
    sb.append(this.esQuery);

    if (this.sortingKey != null) {
      sb.append(",");
      sb.append("\"sort\" : ");
      sb.append("[{\"" + this.sortingKey + "\" : {\"order\" : \"" + this.sortOrder.name() + "\"}}]");
    }

    sb.append(",");
    sb.append("\"size\" : ");
    sb.append("\"");
    sb.append(searchAndExportRetrieveRequestVO.getSearchRetrieveRequestVO().getLimit());
    sb.append("\"");
    sb.append(",");

    sb.append("\"from\" : ");
    sb.append("\"");
    sb.append(searchAndExportRetrieveRequestVO.getSearchRetrieveRequestVO().getOffset());
    sb.append("\"");

    sb.append(",");
    sb.append("\"exportFormat\" : ");
    sb.append("\"");
    sb.append(searchAndExportRetrieveRequestVO.getExportFormatName());
    sb.append("\"");

    sb.append(",");
    sb.append("\"outputFormat\" : ");
    sb.append("\"");
    sb.append(searchAndExportRetrieveRequestVO.getOutputFormat());
    sb.append("\"");

    if (searchAndExportRetrieveRequestVO.getCslConeId() != null) {
      sb.append(",");
      sb.append("\"cslConeId\" : ");
      sb.append("\"");
      sb.append(searchAndExportRetrieveRequestVO.getCslConeId());
      sb.append("\"");
    }

    sb.append("}");

    return sb.toString();
  }

  public void updatePage() {
    // reloads page
  }

  public void validateOffset(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    int offset_;
    String msg;

    // Test auf Zahl
    try {
      offset_ = Integer.parseInt((String) value);
    } catch (NumberFormatException ex) {
      msg = getMessage("searchAndExport_error_number");
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }

    // Test auf korrektes Intervall
    UIComponent temp1 = FacesTools.findComponent("limitId");
    HtmlInputText temp2 = (HtmlInputText) temp1;
    int limit_;
    try {
      limit_ = Integer.parseInt((String) temp2.getSubmittedValue());
      // obere Grenze eingegeben und gueltig
      if (offset_ < 0 || offset_ >= limit_) {
        msg = getMessage("searchAndExport_error_numberBetween_GE_LT").replace("$1", "0").replace("$2",
            "" + (limit_ > 0 && limit_ <= this.maxLimit ? limit_ : this.maxLimit));
        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
      }
    } catch (NumberFormatException ex) {
      // obere Grenze ungueltig 
      if (offset_ < 0 || offset_ >= this.maxLimit) {
        msg = getMessage("searchAndExport_error_numberBetween_GE_LT").replace("$1", "0").replace("$2", "" + this.maxLimit);
        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
      }
    }
  }

  public void validateLimit(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    int limit_;
    String msg;

    // Test auf Zahl
    try {
      limit_ = Integer.parseInt((String) value);
    } catch (NumberFormatException ex) {
      msg = getMessage("searchAndExport_error_number");
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }

    // Test auf korrektes Intervall
    UIComponent temp1 = FacesTools.findComponent("offsetId");
    HtmlInputText temp2 = (HtmlInputText) temp1;
    int offset_;
    try {
      offset_ = Integer.parseInt((String) temp2.getSubmittedValue());
      // untere Grenze eingegeben und gueltig
      if (limit_ <= 0 || limit_ <= offset_ || limit_ > this.maxLimit) {
        msg = getMessage("searchAndExport_error_numberBetween_GT_LE").replace("$1", (offset_ >= 0 ? "" + offset_ : "0")).replace("$2",
            "" + this.maxLimit);
        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
      }
    } catch (NumberFormatException ex) {
      // untere Grenze ungueltig 
      if (limit_ <= 0 || limit_ > this.maxLimit) {
        msg = getMessage("searchAndExport_error_numberBetween_GT_LE").replace("$1", "0").replace("$2", "" + this.maxLimit);
        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
      }
    }
  }

  public void validateQuery(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String msg;

    try {
      new JSONObject((String) value);
    } catch (JSONException e) {
      msg = getMessage("searchAndExport_error_validateQuery");
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }
  }

}
