package de.mpg.mpdl.inge.pubman.web.depositorWS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.util.PropertyReader;

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

      // define the filter criteria
      final FilterTaskParamVO filter = new FilterTaskParamVO();

      final Filter f1 =
          filter.new OwnerFilter(this.getLoginHelper().getAccountUser().getReference());
      filter.getFilterList().add(f1);
      final Filter f2 =
          filter.new FrameworkItemTypeFilter(
              PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
      filter.getFilterList().add(f2);
      final Filter latestVersionFilter = filter.new StandardFilter("/isLatestVersion", "true");
      filter.getFilterList().add(latestVersionFilter);

      if (this.selectedItemState.toLowerCase().equals("withdrawn")) {
        // use public status instead of version status here
        final Filter f3 = filter.new ItemPublicStatusFilter(State.WITHDRAWN);
        filter.getFilterList().add(0, f3);
      } else if (this.selectedItemState.toLowerCase().equals("all")) {
        // all public status except withdrawn
        final Filter f4 = filter.new ItemPublicStatusFilter(State.IN_REVISION);
        filter.getFilterList().add(0, f4);
        final Filter f5 = filter.new ItemPublicStatusFilter(State.PENDING);
        filter.getFilterList().add(0, f5);
        final Filter f6 = filter.new ItemPublicStatusFilter(State.SUBMITTED);
        filter.getFilterList().add(0, f6);
        final Filter f7 = filter.new ItemPublicStatusFilter(State.RELEASED);
        filter.getFilterList().add(0, f7);
      } else {
        // the selected version status filter
        final Filter f3 = filter.new ItemStatusFilter(State.valueOf(this.selectedItemState));
        filter.getFilterList().add(0, f3);

        // all public status except withdrawn
        final Filter f4 = filter.new ItemPublicStatusFilter(State.IN_REVISION);
        filter.getFilterList().add(0, f4);
        final Filter f5 = filter.new ItemPublicStatusFilter(State.PENDING);
        filter.getFilterList().add(0, f5);
        final Filter f6 = filter.new ItemPublicStatusFilter(State.SUBMITTED);
        filter.getFilterList().add(0, f6);
        final Filter f7 = filter.new ItemPublicStatusFilter(State.RELEASED);
        filter.getFilterList().add(0, f7);
      }

      if (!this.getSelectedImport().toLowerCase().equals("all")) {
        final Filter f10 = filter.new LocalTagFilter(this.getSelectedImport());
        filter.getFilterList().add(f10);
      }

      final Filter f10 = filter.new OrderFilter(sc.getSortPath(), sc.getSortOrder());
      filter.getFilterList().add(f10);

      final Filter f8 = filter.new LimitFilter(String.valueOf(limit));
      filter.getFilterList().add(f8);
      final Filter f9 = filter.new OffsetFilter(String.valueOf(offset));
      filter.getFilterList().add(f9);

      final String xmlItemList =
          ServiceLocator.getItemHandler(this.getLoginHelper().getESciDocUserHandle())
              .retrieveItems(filter.toMap());

      final ItemVOListWrapper pubItemList =
          XmlTransformingService.transformSearchRetrieveResponseToItemList(xmlItemList);

      this.numberOfRecords = Integer.parseInt(pubItemList.getNumberOfRecords());
      returnList =
          CommonUtils.convertToPubItemVOPresentationList((List<PubItemVO>) pubItemList
              .getItemVOList());
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
