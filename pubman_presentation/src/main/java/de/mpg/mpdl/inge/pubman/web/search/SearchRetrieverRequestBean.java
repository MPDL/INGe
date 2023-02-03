package de.mpg.mpdl.inge.pubman.web.search;

import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO.State;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.exceptions.PubManVersionNotAvailableException;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.JsonUtil;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import org.apache.log4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the Search result
 * list. It executes the Search whenever the page is called with a GET cql parameter and a valid cql
 * query. It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "SearchRetrieverRequestBean")
@SuppressWarnings("serial")
public class SearchRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, SORT_CRITERIA> {
  private static final Logger logger = Logger.getLogger(SearchRetrieverRequestBean.class);

  //  /**
  //   * The HTTP-GET parameter name for the cql query
  //   */
  //  public static String parameterCqlQuery = "cql";

  /**
   * The HTTP-GET parameter name for the query
   */
  public static String parameterQuery = "q";

  /**
   * The HTTP-GET parameter name for the elastic search query query
   */
  public static String parameterElasticSearchQuery = "esq";

  /**
   * The HTTP-GET parameter name for the search type (advanced, simple, ...)
   */
  public static String parameterSearchType = "searchType";

  //  /**
  //   * The current cqlQuery
  //   */
  //  private String cqlQuery;

  /**
   * The current internal pubman query;
   */
  private String queryStringUrlParam;

  private String elasticSearchQueryUrlParam;


  /**
   * The total number of records from the search request
   */
  private int numberOfRecords;

  /**
   * The type of the search (simple, advanced, ...)
   */
  private String searchType;

  public static final String LOAD_SEARCHRESULTLIST = "showSearchResults";


  private Query elasticSearchQueryBuilder;

  public SearchRetrieverRequestBean() {
    super((PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean"), false);
  }

  @Override
  public void init() {
    // no init needed
  }

  @Override
  public int getTotalNumberOfRecords() {
    return this.numberOfRecords;
  }

  @Override
  public String getType() {
    return "SearchResult";
  }

  /**
   * Reads out the qql query and the search type from HTTP-GET parameeters. If cql is null, an error
   * message is shown. If search type is null, an default value is set
   */
  @Override
  public void readOutParameters() {
    final HttpServletRequest request = FacesTools.getRequest();

    // the following procedure is necessary because of the strange decoding in tomcat, when you
    // fetch the
    // parameters with the getParameter method. Japanese characters are decoded to a ISO format and
    // this
    // messes up the characters. Therefore we take the complete query string, which is not decoded,
    // extract the cql parameter
    // and decode it with UrlDecode.
    Map<String, String> paramMap = null;
    try {
      paramMap = CommonUtils.getDecodedUrlParameterMap(request.getQueryString());
    } catch (final UnsupportedEncodingException e) {
      SearchRetrieverRequestBean.logger.error("Error during reading GET parameters.", e);
    }



    final String query = paramMap.get(SearchRetrieverRequestBean.parameterQuery);
    final String elasticSearchQuery = paramMap.get(SearchRetrieverRequestBean.parameterElasticSearchQuery);

    if (query != null || elasticSearchQuery != null) {
      this.setQueryStringUrlParam(query);
      this.setElasticSearchQueryUrlParam(elasticSearchQuery);
    } else {
      this.error(this.getMessage("SearchQueryError"));
    }


    final String searchType = paramMap.get(SearchRetrieverRequestBean.parameterSearchType);
    if (searchType == null) {
      this.setSearchType("simple");
    } else {
      this.setSearchType(searchType);
    }

    if ((elasticSearchQuery == null || elasticSearchQuery.isEmpty()) && (query == null || query.isEmpty())) {
      this.error(this.getMessage("SearchQueryError"));

    }

  }


  @Override
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {

    List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    // checkSortCriterias(sc);
    try {


      PubItemService pis = ApplicationBean.INSTANCE.getPubItemService();

      SearchRequest.Builder srb  = new SearchRequest.Builder().from(offset).size(limit);

      for (String index : sc.getIndex()) {
        if (!index.isEmpty()) {
          if (!index.isEmpty()) {
            FieldSort fs = SearchUtils.baseElasticSearchSortBuilder(pis.getElasticSearchIndexFields(), index,
                    SortOrder.ASC.equals(sc.getSortOrder()) ? co.elastic.clients.elasticsearch._types.SortOrder.Asc
                            : co.elastic.clients.elasticsearch._types.SortOrder.Desc);
            srb.sort(SortOptions.of(so -> so.field(fs)));
          }
        }
      }

      Query escQueryBuilder = null;
      if (getElasticSearchQueryUrlParam() == null) {
        List<SearchCriterionBase> allCriterions = SearchCriterionBase.queryStringToScList(getQueryString());
        escQueryBuilder = SearchCriterionBase.scListToElasticSearchQuery(allCriterions);

        if (!"admin".equals(getSearchType())) {
          //Search only for released items
          BoolQuery.Builder bqb = new BoolQuery.Builder();
          bqb.must(SearchUtils.baseElasticSearchQueryBuilder(ApplicationBean.INSTANCE.getPubItemService().getElasticSearchIndexFields(),
              PubItemServiceDbImpl.INDEX_PUBLIC_STATE, State.RELEASED.name()));
          bqb.must(SearchUtils.baseElasticSearchQueryBuilder(ApplicationBean.INSTANCE.getPubItemService().getElasticSearchIndexFields(),
              PubItemServiceDbImpl.INDEX_VERSION_STATE, State.RELEASED.name()));
          bqb.must(escQueryBuilder);
          escQueryBuilder = bqb.build()._toQuery();
        }
      } else {
        escQueryBuilder = Query.of(q->q.withJson(new StringReader(this.getElasticSearchQueryUrlParam())));
      }

      this.elasticSearchQueryBuilder = escQueryBuilder;
      srb.query(this.elasticSearchQueryBuilder);

      ResponseBody resp;
      if ("admin".equals(getSearchType())) {
        resp = pis.searchDetailed(srb.build(), getLoginHelper().getAuthenticationToken());
      } else {
        resp = pis.searchDetailed(srb.build(), null);
      }
      this.numberOfRecords = (int) resp.hits().total().value();

      List<ItemVersionVO> itemList = SearchUtils.getRecordListFromElasticSearchResponse(resp, ItemVersionVO.class);
      pubItemList.addAll(CommonUtils.convertToPubItemVOPresentationList(itemList));

    } catch (final Exception e) {
      this.error(this.getMessage("ItemsRetrieveError"));
      SearchRetrieverRequestBean.logger.error("Error in retrieving items", e);
    }

    return pubItemList;
  }

  //  /**
  //   * Sets the current cql query
  //   * 
  //   * @param cqlQuery
  //   */
  //  public void setCqlQuery(String cqlQuery) {
  //    this.cqlQuery = cqlQuery;
  //    this.getBasePaginatorListSessionBean().getParameterMap().put(SearchRetrieverRequestBean.parameterCqlQuery, cqlQuery);
  //  }
  //
  //  /**
  //   * Returns the current cql query
  //   * 
  //   * @return
  //   */
  //  public String getCqlQuery() {
  //    return this.cqlQuery;
  //  }
  //
  //  /**
  //   * Returns the current cql query without blanks
  //   * 
  //   * @return
  //   */
  //  public String getNormalizedCqlQuery() {
  //    final String ret = this.cqlQuery;
  //    if (ret != null) {
  //      return URLEncoder.encode(ret);
  //    }
  //
  //    return "";
  //  }



  /**
   * @return link to the atom feed for the current search
   * @throws PubManVersionNotAvailableException
   * @throws UnsupportedEncodingException
   */
  public String getAtomFeedLink() throws PubManVersionNotAvailableException, UnsupportedEncodingException {
    if (this.getElasticSearchQueryUrlParam() == null) {
      return null;
    }

    return "<link href='" + ApplicationBean.INSTANCE.getPubmanInstanceUrl() + "/rest/feed/search?q="
        + URLEncoder.encode(this.getElasticSearchQueryUrlParam(), "UTF-8")
        + "' rel='alternate' type='application/atom+xml' title='Current Search | atom 1.0' />";
  }



  public static ArrayList<PubItemVOPresentation> extractItemsOfSearchResult(SearchRetrieveResponseVO result) {

    final List<SearchRetrieveRecordVO> results = result.getRecords();

    final ArrayList<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    for (int i = 0; i < results.size(); i++) {
      // check if we have found an item

      final SearchRetrieveRecordVO record = results.get(i);
      final PubItemVOPresentation pubItemPres = new PubItemVOPresentation((ItemVersionVO) record.getData());
      pubItemList.add(pubItemPres);

    }

    return pubItemList;
  }

  @Override
  public String getListPageName() {
    return "SearchResultListPage.jsp";
  }

  /**
   * Sets the search type (e.g. advanced, simple, ...) Can be used in the jspf in order to display
   * search type specific elements.
   * 
   * @param searchType
   */
  public void setSearchType(String searchType) {
    this.searchType = searchType;
    this.getBasePaginatorListSessionBean().getParameterMap().put(SearchRetrieverRequestBean.parameterSearchType, searchType);
  }

  /**
   * Returns the search type (e.g. advanced, simple, ...) Can be used in the jspf in order to
   * display search type specific elements
   * 
   * @return
   */
  public String getSearchType() {
    if (this.searchType == null) {
      this.searchType = this.getBasePaginatorListSessionBean().getParameterMap().get(SearchRetrieverRequestBean.parameterSearchType);
    }

    return this.searchType;
  }

  /**
   * Checks if the selected sorting criteria is currently available. If not (empty string), it
   * displays a warning message to the user.
   * 
   * @param sc The sorting criteria to be checked
   */
  protected void checkSortCriterias(SORT_CRITERIA sc) {
    if (sc.getIndex() == null || sc.getIndex().equals("")) {
      this.error(this.getMessage("depositorWS_sortingNotSupported").replace("$1", this.getLabel("ENUM_CRITERIA_" + sc.name())));
    }
  }

  public String getQueryString() {
    if (this.queryStringUrlParam == null) {
      this.queryStringUrlParam = this.getBasePaginatorListSessionBean().getParameterMap().get(SearchRetrieverRequestBean.parameterQuery);
    }
    return this.queryStringUrlParam;
  }

  public String getUrlEncodedQueryString() {
    try {
      if (this.queryStringUrlParam != null) {
        return URLEncoder.encode(this.queryStringUrlParam, "UTF-8");
      }
    } catch (final UnsupportedEncodingException e) {
      SearchRetrieverRequestBean.logger.error("Could not encode query string", e);
    }

    return "";
  }

  public void setQueryStringUrlParam(String query) {
    this.queryStringUrlParam = query;
    this.getBasePaginatorListSessionBean().getParameterMap().put(SearchRetrieverRequestBean.parameterQuery, query);
  }

  public String getElasticSearchQueryUrlParam() {
    if (this.elasticSearchQueryUrlParam == null) {
      this.elasticSearchQueryUrlParam =
          this.getBasePaginatorListSessionBean().getParameterMap().get(SearchRetrieverRequestBean.parameterElasticSearchQuery);
    }
    return this.elasticSearchQueryUrlParam;
  }

  public void setElasticSearchQueryUrlParam(String elasticSearchQuery) {
    this.elasticSearchQueryUrlParam = elasticSearchQuery;
    this.getBasePaginatorListSessionBean().getParameterMap().put(SearchRetrieverRequestBean.parameterElasticSearchQuery,
        elasticSearchQuery);
  }

  public String getPrettyElasticSearchQuery() {
    try {
      if (this.elasticSearchQueryUrlParam != null) {
        return JsonUtil.prettifyJsonString(this.elasticSearchQueryUrlParam);
      } else {
        return this.elasticSearchQueryBuilder != null ? this.elasticSearchQueryBuilder.toString() : "";
      }
    } catch (Exception e) {
      logger.error("Cannot parse Json String " + getElasticSearchQueryUrlParam());
      return "";
    }
  }

  public String getMinifiedUrlEncodedElasticSearchQuery() {
    try {
      String json = null;
      if (this.elasticSearchQueryUrlParam != null) {
        json = this.elasticSearchQueryUrlParam;
      } else {
        json = this.elasticSearchQueryBuilder != null ? this.elasticSearchQueryBuilder.toString() : null;
      }
      return json != null ? URLEncoder.encode(JsonUtil.minifyJsonString(json), StandardCharsets.UTF_8.displayName()) : "";
    } catch (Exception e) {
      logger.error("Cannot parse Json String " + getElasticSearchQueryUrlParam());
      return "";
    }
  }


}
