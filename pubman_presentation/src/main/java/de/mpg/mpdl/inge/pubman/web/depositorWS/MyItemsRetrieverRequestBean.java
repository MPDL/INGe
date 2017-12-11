package de.mpg.mpdl.inge.pubman.web.depositorWS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.multipleimport.BaseImportLog;
import de.mpg.mpdl.inge.pubman.web.multipleimport.DbTools;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the My Items
 * workspace. It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean and
 * adds additional functionality for filtering the items by their state.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "MyItemsRetrieverRequestBean")
@SuppressWarnings("serial")
public class MyItemsRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA> {
  private static final Logger logger = Logger.getLogger(MyItemsRetrieverRequestBean.class);

  public static final String LOAD_DEPOSITORWS = "loadDepositorWS";

  /**
   * This workspace's user.
   */
  AccountUserVO userVO;

  /**
   * The GET parameter name for the item state.
   */
  protected static String parameterSelectedItemState = "itemState";

  /**
   * import filter.
   */
  private static String parameterSelectedImport = "import";

  /**
   * The total number of records
   */
  private int numberOfRecords;

  /**
   * The currently selected import tag.
   */
  private String selectedImport;

  /**
   * A list with menu entries for the import filter menu.
   */
  private List<SelectItem> importSelectItems;

  /**
   * The menu entries of the item state filtering menu
   */
  private List<SelectItem> itemStateSelectItems;

  /**
   * The currently selected item state.
   */
  private String selectedItemState;

  public MyItemsRetrieverRequestBean() {
    super((PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean"), false);
  }

  /**
   * Checks if the user is logged in. If not, redirects to the login page.
   */
  @Override
  public void init() {
    this.checkForLogin();

    final List<SelectItem> importSelectItems = new ArrayList<SelectItem>();
    importSelectItems.add(new SelectItem("all", this.getLabel("EditItem_NO_ITEM_SET")));

    if (!this.getLoginHelper().isLoggedIn()) {
      return;
    }

    this.userVO = this.getLoginHelper().getAccountUser();

    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    final String sql = "select * from IMPORT_LOG where userid = ? order by STARTDATE desc";

    try {
      connection = DbTools.getNewConnection();
      ps = connection.prepareStatement(sql);
      ps.setString(1, this.userVO.getReference().getObjectId());
      rs = ps.executeQuery();

      while (rs.next()) {
        final SelectItem selectItem =
            new SelectItem(rs.getString("name") + " " + BaseImportLog.DATE_FORMAT.format(rs.getTimestamp("startdate")));
        importSelectItems.add(selectItem);
      }
    } catch (final Exception e) {
      MyItemsRetrieverRequestBean.logger.error("Error getting imports from database", e);
      this.error("Error getting imports from database");
    } finally {
      // DbTools.closeResultSet(rs);
      // DbTools.closePreparedStatement(ps);
      DbTools.closeConnection(connection);
    }

    this.setImportSelectItems(importSelectItems);
  }

  @Override
  public int getTotalNumberOfRecords() {
    return this.numberOfRecords;
  }

  @Override
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {
    List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();

    // Return empty list if the user is not logged in, needed to avoid exceptions
    if (!this.getLoginHelper().isLoggedIn()) {
      return returnList;
    }

    try {


      BoolQueryBuilder bq = QueryBuilders.boolQuery();

      bq.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_OWNER_OBJECT_ID,
          this.getLoginHelper().getAccountUser().getReference().getObjectId()));

      // display only latest versions
      bq.must(QueryBuilders.scriptQuery(new Script("doc['" + PubItemServiceDbImpl.INDEX_LATESTVERSION_VERSIONNUMBER + "']==doc['"
          + PubItemServiceDbImpl.INDEX_VERSION_VERSIONNUMBER + "']")));

      if (this.selectedItemState.toLowerCase().equals("withdrawn")) {
        bq.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, "WITHDRAWN"));
      }

      else if (this.selectedItemState.toLowerCase().equals("all")) {
        bq.mustNot(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, "WITHDRAWN"));
      }

      else {
        bq.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_STATE, ItemVO.State.valueOf(this.selectedItemState).name()));
        bq.mustNot(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, "WITHDRAWN"));
      }

      if (!this.getSelectedImport().toLowerCase().equals("all")) {
        bq.must(QueryBuilders.matchQuery(PubItemServiceDbImpl.INDEX_LOCAL_TAGS, this.getSelectedImport()).operator(Operator.AND));
      }

      PubItemService pis = ApplicationBean.INSTANCE.getPubItemService();
      SearchSourceBuilder ssb = new SearchSourceBuilder();
      ssb.query(bq);
      ssb.from(offset);
      ssb.size(limit);


      for (String index : sc.getIndex()) {
        if (!index.isEmpty()) {
          ssb.sort(SearchUtils.baseElasticSearchSortBuilder(pis.getElasticSearchIndexFields(), index,
              SortOrder.ASC.equals(sc.getSortOrder()) ? org.elasticsearch.search.sort.SortOrder.ASC
                  : org.elasticsearch.search.sort.SortOrder.DESC));
        }
      }


      // SearchSortCriteria ssc = new
      // SearchSortCriteria(PubItemServiceDbImpl.INDEX_MODIFICATION_DATE, SortOrder.DESC);
      // SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(bq, limit, offset, ssc);

      SearchResponse resp = pis.searchDetailed(ssb, getLoginHelper().getAuthenticationToken());

      this.numberOfRecords = (int) resp.getHits().getTotalHits();

      List<PubItemVO> pubItemList = SearchUtils.getSearchRetrieveResponseFromElasticSearchResponse(resp, PubItemVO.class);

      returnList = CommonUtils.convertToPubItemVOPresentationList(pubItemList);
    } catch (final Exception e) {
      MyItemsRetrieverRequestBean.logger.error("Error in retrieving items", e);
      this.error("Error in retrieving items");
      this.numberOfRecords = 0;
    }

    return returnList;
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
      // getBasePaginatorListSessionBean().redirect();
    }

  }


  /**
   * Sets the current item state filter
   * 
   * @param itemStateSelectItem
   */
  public void setItemStateSelectItems(List<SelectItem> itemStateSelectItem) {
    this.itemStateSelectItems = itemStateSelectItem;
  }

  /**
   * Sets and returns the menu entries of the item state filter menu.
   * 
   * @return
   */
  public List<SelectItem> getItemStateSelectItems() {
    this.itemStateSelectItems = new ArrayList<SelectItem>();
    this.itemStateSelectItems.add(new SelectItem("all", this.getLabel("ItemList_filterAllExceptWithdrawn")));
    this.itemStateSelectItems
        .add(new SelectItem(ItemVO.State.PENDING.name(), this.getLabel(this.getI18nHelper().convertEnumToString(ItemVO.State.PENDING))));
    this.itemStateSelectItems.add(
        new SelectItem(ItemVO.State.SUBMITTED.name(), this.getLabel(this.getI18nHelper().convertEnumToString(ItemVO.State.SUBMITTED))));
    this.itemStateSelectItems
        .add(new SelectItem(ItemVO.State.RELEASED.name(), this.getLabel(this.getI18nHelper().convertEnumToString(ItemVO.State.RELEASED))));
    this.itemStateSelectItems.add(
        new SelectItem(ItemVO.State.WITHDRAWN.name(), this.getLabel(this.getI18nHelper().convertEnumToString(ItemVO.State.WITHDRAWN))));
    this.itemStateSelectItems.add(
        new SelectItem(ItemVO.State.IN_REVISION.name(), this.getLabel(this.getI18nHelper().convertEnumToString(ItemVO.State.IN_REVISION))));

    return this.itemStateSelectItems;
  }

  /**
   * Sets the selected item state filter
   * 
   * @param selectedItemState
   */
  public void setSelectedItemState(String selectedItemState) {
    this.selectedItemState = selectedItemState;
    this.getBasePaginatorListSessionBean().getParameterMap().put(MyItemsRetrieverRequestBean.parameterSelectedItemState, selectedItemState);
  }

  /**
   * Returns the currently selected item state filter
   * 
   * @return
   */
  public String getSelectedItemState() {
    return this.selectedItemState;
  }

  /**
   * @return the selectedImport
   */
  public String getSelectedImport() {
    return this.selectedImport;
  }

  /**
   * @param selectedImport the selectedImport to set
   */
  public void setSelectedImport(String selectedImport) {
    this.selectedImport = selectedImport;
    this.getBasePaginatorListSessionBean().getParameterMap().put(MyItemsRetrieverRequestBean.parameterSelectedImport, selectedImport);
  }

  /**
   * Returns the label for the currently selected item state.
   * 
   * @return
   */
  public String getSelectedItemStateLabel() {
    String returnString = "";
    if (this.getSelectedItemState() != null && !this.getSelectedItemState().equals("all")) {
      returnString = this.getLabel(this.getI18nHelper().convertEnumToString(ItemVO.State.valueOf(this.getSelectedItemState())));
    }
    return returnString;

  }

  /**
   * Called by JSF whenever the item state menu is changed.
   * 
   * @return
   */
  public String changeItemState() {
    try {
      this.getBasePaginatorListSessionBean().setCurrentPageNumber(1);
      this.getBasePaginatorListSessionBean().redirect();
    } catch (final Exception e) {
      MyItemsRetrieverRequestBean.logger.error("Error during redirection.", e);
      this.error("Could not redirect");
    }

    return "";

  }

  /**
   * Called by JSF whenever the context filter menu is changed. Causes a redirect to the page with
   * updated import GET parameter.
   * 
   * @return
   */
  public String changeImport() {
    try {
      this.getBasePaginatorListSessionBean().setCurrentPageNumber(1);
      this.getBasePaginatorListSessionBean().redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }

    return "";

  }

  /**
   * Reads out the item state parameter from the HTTP GET request and sets an default value if it is
   * null.
   */
  @Override
  public void readOutParameters() {
    final String selectedItemState =
        FacesTools.getExternalContext().getRequestParameterMap().get(MyItemsRetrieverRequestBean.parameterSelectedItemState);
    if (selectedItemState == null) {
      this.setSelectedItemState("all");
    } else {
      this.setSelectedItemState(selectedItemState);
    }

    final String selectedItem =
        FacesTools.getExternalContext().getRequestParameterMap().get(MyItemsRetrieverRequestBean.parameterSelectedImport);
    if (selectedItem == null) {
      this.setSelectedImport("all");
    } else {
      this.setSelectedImport(selectedItem);
    }
  }

  @Override
  public String getType() {
    return "MyItems";
  }

  @Override
  public String getListPageName() {
    return "DepositorWSPage.jsp";
  }

  /**
   * @return the importSelectItems
   */
  public List<SelectItem> getImportSelectItems() {
    return this.importSelectItems;
  }

  /**
   * @param importSelectItems the importSelectItems to set
   */
  public void setImportSelectItems(List<SelectItem> importSelectItems) {
    this.importSelectItems = importSelectItems;
  }
}
