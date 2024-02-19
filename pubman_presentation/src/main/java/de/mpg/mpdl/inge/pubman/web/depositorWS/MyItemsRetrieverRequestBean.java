package de.mpg.mpdl.inge.pubman.web.depositorWS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.InlineScript;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.ScriptQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
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
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.model.SelectItem;

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
  private static final Logger logger = LogManager.getLogger(MyItemsRetrieverRequestBean.class);

  public static final String LOAD_DEPOSITORWS = "loadDepositorWS";

  /**
   * This workspace's user.
   */
  AccountUserDbVO userAccountDbVO;

  /**
   * The GET parameter name for the item state.
   */
  protected static final String parameterSelectedItemState = "itemState";

  /**
   * import filter.
   */
  private static final String parameterSelectedImport = "import";

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
    super(FacesTools.findBean("PubItemListSessionBean"), false);
  }

  /**
   * Checks if the user is logged in. If not, redirects to the login page.
   */
  @Override
  public void init() {
    this.checkForLogin();

    final List<SelectItem> importSelectItems = new ArrayList<>();
    importSelectItems.add(new SelectItem("all", this.getLabel("EditItem_NO_ITEM_SET")));

    if (!this.getLoginHelper().isLoggedIn()) {
      return;
    }

    this.userAccountDbVO = this.getLoginHelper().getAccountUser();

    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    final String sql = "select * from IMPORT_LOG where userid = ? order by STARTDATE desc";

    try {
      connection = DbTools.getNewConnection();
      ps = connection.prepareStatement(sql);
      ps.setString(1, this.userAccountDbVO.getObjectId());
      rs = ps.executeQuery();

      while (rs.next()) {
        final SelectItem selectItem =
            new SelectItem(rs.getString("name") + " " + BaseImportLog.DATE_FORMAT.format(rs.getTimestamp("startdate")));
        importSelectItems.add(selectItem);
      }
    } catch (final Exception e) {
      MyItemsRetrieverRequestBean.logger.error("Error getting imports from database", e);
      this.error(this.getMessage("ImportDatabaseError"));
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
    List<PubItemVOPresentation> returnList = new ArrayList<>();

    // Return empty list if the user is not logged in, needed to avoid exceptions
    if (!this.getLoginHelper().isLoggedIn()) {
      return returnList;
    }

    try {


      BoolQuery.Builder bq = new BoolQuery.Builder();

      bq.must(
          TermQuery.of(t -> t.field(PubItemServiceDbImpl.INDEX_OWNER_OBJECT_ID).value(this.getLoginHelper().getAccountUser().getObjectId()))
              ._toQuery());

      // display only latest versions
      InlineScript is = InlineScript.of(i -> i.source("doc['" + PubItemServiceDbImpl.INDEX_LATESTVERSION_VERSIONNUMBER + "']==doc['"
          + PubItemServiceDbImpl.INDEX_VERSION_VERSIONNUMBER + "']"));
      bq.must(ScriptQuery.of(sq -> sq.script(Script.of(s -> s.inline(is))))._toQuery());

      if (this.selectedItemState.equalsIgnoreCase("withdrawn")) {
        bq.must(TermQuery.of(t -> t.field(PubItemServiceDbImpl.INDEX_PUBLIC_STATE).value("WITHDRAWN"))._toQuery());
      }

      else if (this.selectedItemState.equalsIgnoreCase("all")) {
        bq.mustNot(TermQuery.of(t -> t.field(PubItemServiceDbImpl.INDEX_PUBLIC_STATE).value("WITHDRAWN"))._toQuery());
      }

      else {
        bq.must(TermQuery
            .of(t -> t.field(PubItemServiceDbImpl.INDEX_VERSION_STATE).value(ItemVersionRO.State.valueOf(this.selectedItemState).name()))
            ._toQuery());
        bq.mustNot(TermQuery.of(t -> t.field(PubItemServiceDbImpl.INDEX_PUBLIC_STATE).value("WITHDRAWN"))._toQuery());
      }

      if (!this.getSelectedImport().equalsIgnoreCase("all")) {
        bq.must(MatchQuery.of(t -> t.field(PubItemServiceDbImpl.INDEX_LOCAL_TAGS).query(this.getSelectedImport()).operator(Operator.And))
            ._toQuery());
      }

      PubItemService pis = ApplicationBean.INSTANCE.getPubItemService();
      SearchRequest.Builder srb = new SearchRequest.Builder().query(bq.build()._toQuery()).from(offset).size(limit);


      for (String index : sc.getIndex()) {
        if (!index.isEmpty()) {
          FieldSort fs = SearchUtils.baseElasticSearchSortBuilder(pis.getElasticSearchIndexFields(), index,
              SortOrder.ASC.equals(sc.getSortOrder()) ? co.elastic.clients.elasticsearch._types.SortOrder.Asc
                  : co.elastic.clients.elasticsearch._types.SortOrder.Desc);
          srb.sort(SortOptions.of(so -> so.field(fs)));
        }
      }


      // SearchSortCriteria ssc = new
      // SearchSortCriteria(PubItemServiceDbImpl.INDEX_MODIFICATION_DATE, SortOrder.DESC);
      // SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(bq, limit, offset, ssc);

      ResponseBody resp = pis.searchDetailed(srb.build(), getLoginHelper().getAuthenticationToken());

      this.numberOfRecords = (int) resp.hits().total().value();

      List<ItemVersionVO> pubItemList = SearchUtils.getRecordListFromElasticSearchResponse(resp, ItemVersionVO.class);

      returnList = CommonUtils.convertToPubItemVOPresentationList(pubItemList);
    } catch (final Exception e) {
      MyItemsRetrieverRequestBean.logger.error("Error in retrieving items", e);
      this.error(this.getMessage("ItemsRetrieveError"));
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
    if (sc.getIndex() == null || sc.getIndex().length == 0) {
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
    this.itemStateSelectItems = new ArrayList<>();
    this.itemStateSelectItems.add(new SelectItem("all", this.getLabel("ItemList_filterAllExceptWithdrawn")));
    this.itemStateSelectItems.add(new SelectItem(ItemVersionRO.State.PENDING.name(),
        this.getLabel(this.getI18nHelper().convertEnumToString(ItemVersionRO.State.PENDING))));
    this.itemStateSelectItems.add(new SelectItem(ItemVersionRO.State.SUBMITTED.name(),
        this.getLabel(this.getI18nHelper().convertEnumToString(ItemVersionRO.State.SUBMITTED))));
    this.itemStateSelectItems.add(new SelectItem(ItemVersionRO.State.RELEASED.name(),
        this.getLabel(this.getI18nHelper().convertEnumToString(ItemVersionRO.State.RELEASED))));
    this.itemStateSelectItems.add(new SelectItem(ItemVersionRO.State.WITHDRAWN.name(),
        this.getLabel(this.getI18nHelper().convertEnumToString(ItemVersionRO.State.WITHDRAWN))));
    this.itemStateSelectItems.add(new SelectItem(ItemVersionRO.State.IN_REVISION.name(),
        this.getLabel(this.getI18nHelper().convertEnumToString(ItemVersionRO.State.IN_REVISION))));

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
      returnString = this.getLabel(this.getI18nHelper().convertEnumToString(ItemVersionRO.State.valueOf(this.getSelectedItemState())));
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
      this.error(this.getMessage("NoRedirect"));
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
      this.error(this.getMessage("NoRedirect"));
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
    this.setSelectedItemState(Objects.requireNonNullElse(selectedItemState, "all"));

    final String selectedItem =
        FacesTools.getExternalContext().getRequestParameterMap().get(MyItemsRetrieverRequestBean.parameterSelectedImport);
    this.setSelectedImport(Objects.requireNonNullElse(selectedItem, "all"));
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
