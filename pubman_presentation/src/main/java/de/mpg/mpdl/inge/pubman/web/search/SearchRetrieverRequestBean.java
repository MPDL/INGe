package de.mpg.mpdl.inge.pubman.web.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.mpdl.inge.model.valueobjects.ItemResultVO;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.SearchResultElement;
import de.mpg.mpdl.inge.pubman.web.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.exceptions.PubManVersionNotAvailableException;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.LoginHelper;
import de.mpg.mpdl.inge.pubman.web.util.PubItemResultVO;
import de.mpg.mpdl.inge.pubman.web.util.PubItemVOPresentation;
import de.mpg.mpdl.inge.search.Search;
import de.mpg.mpdl.inge.search.query.ItemContainerSearchResult;
import de.mpg.mpdl.inge.search.query.PlainCqlQuery;
import de.mpg.mpdl.inge.search.query.SearchQuery.SortingOrder;

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
@SuppressWarnings("serial")
public class SearchRetrieverRequestBean extends
    BaseListRetrieverRequestBean<PubItemVOPresentation, SORT_CRITERIA> {
  public static final String BEAN_NAME = "SearchRetrieverRequestBean";

  private static final Logger logger = Logger.getLogger(SearchRetrieverRequestBean.class);

  /**
   * The HTTP-GET parameter name for the cql query
   */
  public static String parameterCqlQuery = "cql";

  /**
   * The HTTP-GET parameter name for the query
   */
  public static String parameterQuery = "q";

  /**
   * The HTTP-GET parameter name for the search type (advanced, simple, ...)
   */
  public static String parameterSearchType = "searchType";

  /**
   * The current cqlQuery
   */
  private String cqlQuery;

  /**
   * The current internal pubman query;
   */
  private String queryString;

  /**
   * The total number of records from the search request
   */
  private int numberOfRecords;

  /**
   * An instance of the search service.
   */
  @EJB
  private Search searchService;

  // @EJB
  // private XmlTransforming xmlTransforming;

  /**
   * The type of the search (simple, advanced, ...)
   */
  private String searchType;

  // Faces navigation string
  public final static String LOAD_SEARCHRESULTLIST = "showSearchResults";

  public SearchRetrieverRequestBean() {
    super((PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class), false);
  }

  @Override
  public int getTotalNumberOfRecords() {
    return numberOfRecords;
  }

  @Override
  public String getType() {
    return "SearchResult";
  }

  @Override
  public void init() {
    // no init needed
  }

  /**
   * Reads out the qql query and the search type from HTTP-GET parameeters. If cql is null, an error
   * message is shown. If search type is null, an default value is set
   */
  @Override
  public void readOutParameters() {
    HttpServletRequest request = (HttpServletRequest) getExternalContext().getRequest();

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
    } catch (UnsupportedEncodingException e) {
      logger.error("Error during reading GET parameters.", e);
    }



    String query = paramMap.get(parameterQuery);

    if (query != null) {
      setQueryString(query);
    }

    String cql = paramMap.get(parameterCqlQuery);

    if ((cql == null || cql.equals(""))) {
      setCqlQuery("");
      error("You have to call this page with a parameter \"cql\" and a cql query!");

    } else {
      setCqlQuery(cql);
    }



    String searchType = paramMap.get(parameterSearchType);
    if (searchType == null) {
      setSearchType("simple");
    } else {
      setSearchType(searchType);
    }

  }

  /*
   * public List<PubItemVOPresentation> retrieveListGenericSearch(int offset, int limit,
   * SORT_CRITERIA sc) { List<PubItemVOPresentation> pubItemList = null; //new
   * ArrayList<PubItemVOPresentation>(); //checkSortCriterias(sc); try {
   * 
   * 
   * 
   * PlainCqlQuery query = new PlainCqlQuery(getCqlQuery()); query.setStartRecord(new
   * PositiveInteger(String.valueOf(offset+1))); query.setMaximumRecords(new
   * NonNegativeInteger(String.valueOf(limit)));
   * 
   * if(sc.getIndex()!=null) { query.setSortKeys(sc.getIndex()); }
   * 
   * if(sc.getIndex() == null || !sc.getIndex().equals("")) { if
   * (sc.getSortOrder().equals(OrderFilter.ORDER_DESCENDING)) {
   * 
   * query.setSortOrder(SortingOrder.DESCENDING); }
   * 
   * else { query.setSortOrder(SortingOrder.ASCENDING); } } ItemContainerSearchResult result =
   * this.searchService.searchForItemContainer(query);
   * 
   * pubItemList = extractItemsOfSearchResult(result); this.numberOfRecords =
   * Integer.parseInt(result.getTotalNumberOfResults().toString()); } catch (Exception e) {
   * error("Error in search!"); logger.error("Error during search. ", e); }
   * 
   * return pubItemList; }
   */

  /*
   * public List<PubItemVOPresentation> retrieveListAdminSearch(int offset, int limit, SORT_CRITERIA
   * sc) { List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();
   * LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class); try { ItemHandler
   * itemHandler = null;
   * 
   * if(loginHelper.getESciDocUserHandle()!=null) { itemHandler =
   * ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle()); } else { itemHandler =
   * ServiceLocator.getItemHandler(); }
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * checkSortCriterias(sc);
   * 
   * FilterTaskParamVO filter = new FilterTaskParamVO(); Filter f1 = filter.new
   * CqlFilter(getCqlQuery()); filter.getFilterList().add(f1); Filter f2 = filter.new
   * OrderFilter(sc.getSortPath(), sc.getSortOrder()); filter.getFilterList().add(f2); Filter f3 =
   * filter.new LimitFilter(String.valueOf(limit)); filter.getFilterList().add(f3); Filter f4 =
   * filter.new OffsetFilter(String.valueOf(offset)); filter.getFilterList().add(f4);
   * 
   * 
   * String xmlItemList = itemHandler.retrieveItems(filter.toMap());
   * 
   * 
   * ItemVOListWrapper pubItemList =
   * xmlTransforming.transformSearchRetrieveResponseToItemList(xmlItemList);
   * 
   * numberOfRecords = Integer.parseInt(pubItemList.getNumberOfRecords()); returnList =
   * CommonUtils.convertToPubItemVOPresentationList((List<PubItemVO>) pubItemList.getItemVOList());
   * } catch (Exception e) { logger.error("Error in retrieving items", e);
   * error("Error in retrieving items"); numberOfRecords = 0; } return returnList; } /*
   * 
   * 
   * /** Calls the search service and requests the items for the current cql query.
   */
  @Override
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {

    List<PubItemVOPresentation> pubItemList = null; // new ArrayList<PubItemVOPresentation>();
    // checkSortCriterias(sc);
    try {


      PlainCqlQuery query = new PlainCqlQuery(getCqlQuery());
      query.setStartRecord(new PositiveInteger(String.valueOf(offset + 1)));
      query.setMaximumRecords(new NonNegativeInteger(String.valueOf(limit)));

      if (sc.getIndex() != null) {
        if ("admin".equals(getSearchType())) {
          query.setSortKeys(sc.getSortPath());
        } else {
          query.setSortKeys(sc.getIndex());
        }

      }

      if (sc.getIndex() == null || !sc.getIndex().equals("")) {
        if (sc.getSortOrder().equals(OrderFilter.ORDER_DESCENDING)) {

          query.setSortOrder(SortingOrder.DESCENDING);
        }

        else {
          query.setSortOrder(SortingOrder.ASCENDING);
        }
      }
      ItemContainerSearchResult result = null;

      if ("admin".equals(getSearchType())) {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        result =
            this.searchService.searchForItemContainerAdmin(query,
                loginHelper.getESciDocUserHandle());
      } else {
        result = this.searchService.searchForItemContainer(query);
      }

      pubItemList = extractItemsOfSearchResult(result);
      this.numberOfRecords = Integer.parseInt(result.getTotalNumberOfResults().toString());
    } catch (Exception e) {
      error("Error in search!");
      logger.error("Error during search. ", e);
    }

    return pubItemList;
  }


  /**
   * Sets the current cql query
   * 
   * @param cqlQuery
   */
  public void setCqlQuery(String cqlQuery) {
    this.cqlQuery = cqlQuery;
    getBasePaginatorListSessionBean().getParameterMap().put(parameterCqlQuery, cqlQuery);
  }

  /**
   * Returns the current cql query
   * 
   * @return
   */
  public String getCqlQuery() {
    return this.cqlQuery;
  }

  /**
   * Returns the current cql query without blanks
   * 
   * @return
   */
  public String getNormalizedCqlQuery() {
    String ret = this.cqlQuery;
    if (ret != null) {
      return java.net.URLEncoder.encode(ret);
    }

    return "";
  }

  /**
   * @return link to the rss feed for the current search
   * @throws PubManVersionNotAvailableException
   */
  public String getRssFeedLink() throws PubManVersionNotAvailableException {
    return "<link href='" + this.getApplicationBean().getPubmanInstanceUrl()
        + "/syndication/feed/rss_2.0/search?q=" + this.getNormalizedCqlQuery()
        + "' rel='alternate' type='application/rss+xml' title='Current Search | rss 2.0' />";
  }

  /**
   * @return link to the atom feed for the current search
   * @throws PubManVersionNotAvailableException
   */
  public String getAtomFeedLink() throws PubManVersionNotAvailableException {
    return "<link href='" + this.getApplicationBean().getPubmanInstanceUrl()
        + "/syndication/feed/atom_1.0/search?q=" + this.getNormalizedCqlQuery()
        + "' rel='alternate' type='application/atom+xml' title='Current Search | atom 1.0' />";
  }

  /**
   * Helper method that transforms the result of the search into a list of PubItemVOPresentation
   * objects.
   * 
   * @param result
   * @return
   */
  public static ArrayList<PubItemVOPresentation> extractItemsOfSearchResult(
      ItemContainerSearchResult result) {

    List<SearchResultElement> results = result.getResultList();

    ArrayList<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    for (int i = 0; i < results.size(); i++) {
      // check if we have found an item
      if (results.get(i) instanceof ItemResultVO) {
        // cast to PubItemResultVO
        ItemResultVO item = (ItemResultVO) results.get(i);
        PubItemResultVO pubItemResult =
            new PubItemResultVO(item, item.getSearchHitList(), item.getScore());
        PubItemVOPresentation pubItemPres = new PubItemVOPresentation(pubItemResult);
        pubItemList.add(pubItemPres);
      }
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
    getBasePaginatorListSessionBean().getParameterMap().put(parameterSearchType, searchType);
  }

  /**
   * Returns the search type (e.g. advanced, simple, ...) Can be used in the jspf in order to
   * display search type specific elements
   * 
   * @return
   */
  public String getSearchType() {
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
      error(getMessage("depositorWS_sortingNotSupported").replace("$1",
          getLabel("ENUM_CRITERIA_" + sc.name())));
    }
  }

  public String getQueryString() {
    return this.queryString;
  }

  public String getUrlEncodedQueryString() {
    try {
      if (queryString != null) {
        return URLEncoder.encode(queryString, "UTF-8");
      }
    } catch (UnsupportedEncodingException e) {
      logger.error("Could not encode query string", e);
    }

    return "";
  }

  public void setQueryString(String query) {
    this.queryString = query;
    getBasePaginatorListSessionBean().getParameterMap().put(parameterQuery, query);
  }

  protected ApplicationBean getApplicationBean() {
    return (ApplicationBean) getApplicationBean(ApplicationBean.class);
  }
}
