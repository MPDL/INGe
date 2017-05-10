package de.mpg.mpdl.inge.pubman.web.depositorWS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;

import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceImpl;

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
public class MyItemsRetrieverRequestBean extends
    BaseListRetrieverRequestBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA> {
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

    // Init imports
    final List<SelectItem> importSelectItems = new ArrayList<SelectItem>();
    importSelectItems.add(new SelectItem("all", this.getLabel("EditItem_NO_ITEM_SET")));

    if (!this.getLoginHelper().isLoggedIn()) {
      return;
    }

    this.userVO = this.getLoginHelper().getAccountUser();

    try {
      final Connection connection = ImportLog.getConnection();
      final String sql =
          "select * from ESCIDOC_IMPORT_LOG where userid = ? order by STARTDATE desc";
      final PreparedStatement statement = connection.prepareStatement(sql);

      statement.setString(1, this.userVO.getReference().getObjectId());

      final ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        final SelectItem selectItem =
            new SelectItem(resultSet.getString("name") + " "
                + ImportLog.DATE_FORMAT.format(resultSet.getTimestamp("startdate")));
        importSelectItems.add(selectItem);
      }

      resultSet.close();
      statement.close();
    } catch (final Exception e) {
      MyItemsRetrieverRequestBean.logger.error("Error getting imports from database", e);
      FacesBean.error("Error getting imports from database");
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
      
      this.checkSortCriterias(sc);
      BoolQueryBuilder bq = QueryBuilders.boolQuery();
      bq.must(QueryBuilders.termQuery(PubItemServiceImpl.INDEX_OWNER_OBJECT_ID, this.getLoginHelper().getAccountUser().getReference().getObjectId()));
      //display only latest versions
      bq.must(QueryBuilders.scriptQuery(new Script("doc['"+ PubItemServiceImpl.INDEX_LATESTVERSION_VERSIONNUMBER + "']==doc['" + PubItemServiceImpl.INDEX_VERSION_VERSIONNUMBER +"']")));
      

      if (this.selectedItemState.toLowerCase().equals("withdrawn")) {
        bq.must(QueryBuilders.termQuery(PubItemServiceImpl.INDEX_PUBLIC_STATE, "WITHDRAWN"));
      }

      else if (this.selectedItemState.toLowerCase().equals("all")) {
        bq.mustNot(QueryBuilders.termQuery(PubItemServiceImpl.INDEX_PUBLIC_STATE, "WITHDRAWN"));
      }
     
      else {
        bq.must(QueryBuilders.termQuery(PubItemServiceImpl.INDEX_VERSION_STATE, State.valueOf(this.selectedItemState).name()));
        bq.mustNot(QueryBuilders.termQuery(PubItemServiceImpl.INDEX_PUBLIC_STATE, "WITHDRAWN"));
        
      }
      if (!this.getSelectedImport().toLowerCase().equals("all")) {
        bq.must(QueryBuilders.termQuery(PubItemServiceImpl.INDEX_LOCAL_TAGS, this.getSelectedImport()));
      }

     //TODO Sorting!!
      SearchSortCriteria ssc = new SearchSortCriteria(PubItemServiceImpl.INDEX_MODIFICATION_DATE, SortOrder.DESC);
      SearchRetrieveRequestVO<QueryBuilder> srr = new SearchRetrieveRequestVO<QueryBuilder>(bq, limit, offset, ssc);


      SearchRetrieveResponseVO<PubItemVO> resp = ApplicationBean.INSTANCE.getPubItemService().search(srr, getLoginHelper().getAuthenticationToken());

      this.numberOfRecords = resp.getNumberOfRecords();
      
      List<PubItemVO> pubItemList = resp.getRecords().stream().map(SearchRetrieveRecordVO::getData).collect(Collectors.toList());
      returnList =
          CommonUtils.convertToPubItemVOPresentationList(pubItemList);
    } catch (final Exception e) {
      MyItemsRetrieverRequestBean.logger.error("Error in retrieving items", e);
      FacesBean.error("Error in retrieving items");
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
    if (sc.getSortPath() == null || sc.getSortPath().equals("")) {
      FacesBean.error(this.getMessage("depositorWS_sortingNotSupported").replace("$1",
          this.getLabel("ENUM_CRITERIA_" + sc.name())));
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
    this.itemStateSelectItems.add(new SelectItem("all", this
        .getLabel("ItemList_filterAllExceptWithdrawn")));
    this.itemStateSelectItems.add(new SelectItem(State.PENDING.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(State.PENDING))));
    this.itemStateSelectItems.add(new SelectItem(State.SUBMITTED.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(State.SUBMITTED))));
    this.itemStateSelectItems.add(new SelectItem(State.RELEASED.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(State.RELEASED))));
    this.itemStateSelectItems.add(new SelectItem(State.WITHDRAWN.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(State.WITHDRAWN))));
    this.itemStateSelectItems.add(new SelectItem(State.IN_REVISION.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(State.IN_REVISION))));

    return this.itemStateSelectItems;
  }

  /**
   * Sets the selected item state filter
   * 
   * @param selectedItemState
   */
  public void setSelectedItemState(String selectedItemState) {
    this.selectedItemState = selectedItemState;
    this.getBasePaginatorListSessionBean().getParameterMap()
        .put(MyItemsRetrieverRequestBean.parameterSelectedItemState, selectedItemState);
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
    this.getBasePaginatorListSessionBean().getParameterMap()
        .put(MyItemsRetrieverRequestBean.parameterSelectedImport, selectedImport);
  }

  /**
   * Returns the label for the currently selected item state.
   * 
   * @return
   */
  public String getSelectedItemStateLabel() {
    String returnString = "";
    if (this.getSelectedItemState() != null && !this.getSelectedItemState().equals("all")) {
      returnString =
          this.getLabel(this.getI18nHelper().convertEnumToString(
              State.valueOf(this.getSelectedItemState())));
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
      FacesBean.error("Could not redirect");
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
      FacesBean.error("Could not redirect");
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
        FacesTools.getExternalContext().getRequestParameterMap()
            .get(MyItemsRetrieverRequestBean.parameterSelectedItemState);
    if (selectedItemState == null) {
      this.setSelectedItemState("all");
    } else {
      this.setSelectedItemState(selectedItemState);
    }

    final String selectedItem =
        FacesTools.getExternalContext().getRequestParameterMap()
            .get(MyItemsRetrieverRequestBean.parameterSelectedImport);
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
