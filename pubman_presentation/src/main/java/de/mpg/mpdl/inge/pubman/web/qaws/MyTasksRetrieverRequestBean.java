package de.mpg.mpdl.inge.pubman.web.qaws;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.mpdl.inge.pubman.web.affiliation.AffiliationTree;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the Quality
 * Assurance Workspace It uses the PubItemListSessionBean as corresponding
 * BasePaginatorListSessionBean and adds additional functionality for filtering the items by their
 * state. It extends the MyItemsRetriever RequestBean because it has a similar behaviour regarding
 * item state filters.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "MyTasksRetrieverRequestBean")
@SuppressWarnings("serial")
public class MyTasksRetrieverRequestBean extends MyItemsRetrieverRequestBean {
  private static final Logger logger = Logger.getLogger(MyTasksRetrieverRequestBean.class);

  public static final String LOAD_QAWS = "loadQAWSPage";

  private int numberOfRecords;

  /**
   * The HTTP GET parameter name for the context filter.
   */
  private static String parameterSelectedContext = "context";

  /**
   * org unit filter.
   */
  private static String parameterSelectedOrgUnit = "orgUnit";

  /**
   * A list with menu entries for the context filter menu.
   */
  private List<SelectItem> contextSelectItems;

  public MyTasksRetrieverRequestBean() {}

  @Override
  public void init() {
    this.checkForLogin();
    this.initSelectionMenu();
  }

  @Override
  public int getTotalNumberOfRecords() {
    return this.numberOfRecords;
  }

  @Override
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {
    List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();

    if (!this.getLoginHelper().isLoggedIn() || !this.getLoginHelper().getIsModerator()) {
      return returnList;
    }

    try {
      if (this.getLoginHelper().getESciDocUserHandle() == null) {
        return returnList;
      }

      this.checkSortCriterias(sc);
      // define the filter criteria
      final FilterTaskParamVO filter = new FilterTaskParamVO();

      final Filter f2 =
          filter.new FrameworkItemTypeFilter(
              PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
      filter.getFilterList().add(f2);
      final Filter latestVersionFilter = filter.new StandardFilter("/isLatestVersion", "true");
      filter.getFilterList().add(latestVersionFilter);

      if (this.getSelectedItemState().toLowerCase().equals("withdrawn")) {
        // use public status instead of version status here
        final Filter f3 = filter.new ItemPublicStatusFilter(State.WITHDRAWN);
        filter.getFilterList().add(0, f3);
      } else if (this.getSelectedItemState().toLowerCase().equals("all")) {
        final Filter f3 = filter.new ItemStatusFilter(State.SUBMITTED);
        filter.getFilterList().add(0, f3);
        final Filter f12 = filter.new ItemStatusFilter(State.RELEASED);
        filter.getFilterList().add(0, f12);
        final Filter f13 = filter.new ItemStatusFilter(State.IN_REVISION);
        filter.getFilterList().add(0, f13);

        // all public status except withdrawn
        final Filter f4 = filter.new ItemPublicStatusFilter(State.IN_REVISION);
        filter.getFilterList().add(0, f4);
        final Filter f6 = filter.new ItemPublicStatusFilter(State.SUBMITTED);
        filter.getFilterList().add(0, f6);
        final Filter f7 = filter.new ItemPublicStatusFilter(State.RELEASED);
        filter.getFilterList().add(0, f7);
      } else {
        final Filter f3 = filter.new ItemStatusFilter(State.valueOf(this.getSelectedItemState()));
        filter.getFilterList().add(0, f3);

        // all public status except withdrawn
        final Filter f4 = filter.new ItemPublicStatusFilter(State.IN_REVISION);
        filter.getFilterList().add(0, f4);
        final Filter f6 = filter.new ItemPublicStatusFilter(State.SUBMITTED);
        filter.getFilterList().add(0, f6);
        final Filter f7 = filter.new ItemPublicStatusFilter(State.RELEASED);
        filter.getFilterList().add(0, f7);
      }

      if (this.getSelectedContext().toLowerCase().equals("all")) {
        // add all contexts for which the user has moderator rights (except the "all" item of the
        // menu)
        for (int i = 1; i < this.getContextSelectItems().size(); i++) {
          final String contextId = (String) this.getContextSelectItems().get(i).getValue();
          filter.getFilterList().add(filter.new ContextFilter(contextId));
        }
      } else {
        final Filter f10 = filter.new ContextFilter(this.getSelectedContext());
        filter.getFilterList().add(f10);
      }

      if (!this.getSelectedOrgUnit().toLowerCase().equals("all")) {
        final Filter orgUnitFilter =
            filter.new StandardFilter("/any-organization-pids", this.getSelectedOrgUnit(), "=",
                "and");
        filter.getFilterList().add(orgUnitFilter);
      }

      if (!this.getSelectedImport().toLowerCase().equals("all")) {
        final Filter f10 = filter.new LocalTagFilter(this.getSelectedImport());
        filter.getFilterList().add(f10);
      }

      final Filter f11 = filter.new OrderFilter(sc.getSortPath(), sc.getSortOrder());
      filter.getFilterList().add(f11);
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
      MyTasksRetrieverRequestBean.logger.error("Error in retrieving items", e);
      FacesBean.error("Error in retrieving items");
      this.numberOfRecords = 0;
    }

    return returnList;
  }

  /**
   * Reads out the parameters from HTTP-GET request for the selected item state and the selected
   * context filter. Sets default values if they are null.
   */
  @Override
  public void readOutParameters() {
    super.readOutParameters();

    final String context =
        FacesTools.getExternalContext().getRequestParameterMap()
            .get(MyTasksRetrieverRequestBean.parameterSelectedContext);

    if (context == null) {
      // select first context as default, if there's only one
      if (this.getContextSelectItems().size() == 2) {
        this.setSelectedContext((String) this.getContextSelectItems().get(1).getValue());
      } else {
        this.setSelectedContext((String) this.getContextSelectItems().get(0).getValue());
      }
    } else {
      this.setSelectedContext(context);
    }

    final String orgUnit =
        FacesTools.getExternalContext().getRequestParameterMap()
            .get(MyTasksRetrieverRequestBean.parameterSelectedOrgUnit);
    if (orgUnit == null) {
      this.setSelectedOrgUnit("all");
    } else {
      this.setSelectedOrgUnit(orgUnit);
    }
  }

  @Override
  public String getType() {
    return "MyTasks";
  }

  /**
   * Sets the selected context filter
   * 
   * @param selectedContext
   */
  public void setSelectedContext(String selectedContext) {
    this.getQAWSSessionBean().setSelectedContext(selectedContext);
    this.getBasePaginatorListSessionBean().getParameterMap()
        .put(MyTasksRetrieverRequestBean.parameterSelectedContext, selectedContext);
  }

  /**
   * Returns the selected context filter
   * 
   * @return
   */
  public String getSelectedContext() {
    return this.getQAWSSessionBean().getSelectedContext();
  }

  /**
   * Returns a label for the selected context.
   * 
   * @return
   */
  public String getSelectedContextLabel() {
    if (!this.getSelectedContext().equals("all")) {
      final ContextListSessionBean clsb =
          (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
      final List<PubContextVOPresentation> contextVOList = clsb.getModeratorContextList();

      for (final PubContextVOPresentation contextVO : contextVOList) {
        if (contextVO.getReference().getObjectId().equals(this.getSelectedContext())) {
          return contextVO.getName();
        }
      }
    }

    return "";
  }

  /**
   * Returns a label for the selected org unit.
   * 
   * @return
   */
  public String getSelectedOrgUnitLabel() {
    final AffiliationTree affTree = (AffiliationTree) FacesTools.findBean("AffiliationTree");

    return (this.getSelectedOrgUnit() == null ? "" : affTree.getAffiliationMap()
        .get(this.getSelectedOrgUnit()).getNamePath());
  }

  /**
   * Returns a list with menu entries for the item state filter menu.
   */
  @Override
  public List<SelectItem> getItemStateSelectItems() {
    final List<SelectItem> itemStateSelectItems = new ArrayList<SelectItem>();

    itemStateSelectItems.add(new SelectItem("all", this
        .getLabel("ItemList_filterAllExceptPendingWithdrawn")));
    itemStateSelectItems.add(new SelectItem(State.SUBMITTED.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(State.SUBMITTED))));
    itemStateSelectItems.add(new SelectItem(State.RELEASED.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(State.RELEASED))));
    itemStateSelectItems.add(new SelectItem(State.IN_REVISION.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(State.IN_REVISION))));
    itemStateSelectItems.add(new SelectItem(State.WITHDRAWN.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(State.WITHDRAWN))));
    this.setItemStateSelectItems(itemStateSelectItems);

    return itemStateSelectItems;
  }

  /**
   * Initializes the menu for the context filtering.
   */
  private void initSelectionMenu() {

    /*
     * //item states List<SelectItem> itemStateSelectItems = new ArrayList<SelectItem>();
     * itemStateSelectItems.add(new SelectItem("all",getLabel("EditItem_NO_ITEM_SET")));
     * itemStateSelectItems.add(new SelectItem(State.SUBMITTED.name(),
     * getLabel(i18nHelper.convertEnumToString(State.SUBMITTED)))); itemStateSelectItems.add(new
     * SelectItem(State.RELEASED.name(), getLabel(i18nHelper.convertEnumToString(State.RELEASED))));
     * itemStateSelectItems.add(new SelectItem(State.IN_REVISION.name(),
     * getLabel(i18nHelper.convertEnumToString(State.IN_REVISION))));
     * setItemStateSelectItems(itemStateSelectItems);
     */

    // LoginHelper loginHelper = (LoginHelper) FacesTools.findBean(LoginHelper.class);

    // Contexts (Collections)
    final ContextListSessionBean clsb =
        (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    final List<PubContextVOPresentation> contextVOList = clsb.getModeratorContextList();

    this.contextSelectItems = new ArrayList<SelectItem>();
    this.contextSelectItems.add(new SelectItem("all", this.getLabel("EditItem_NO_ITEM_SET")));
    for (int i = 0; i < contextVOList.size(); i++) {
      String workflow = "null";
      if (contextVOList.get(i).getAdminDescriptor().getWorkflow() != null) {
        workflow = contextVOList.get(i).getAdminDescriptor().getWorkflow().toString();
      }
      this.contextSelectItems.add(new SelectItem(contextVOList.get(i).getReference().getObjectId(),
          contextVOList.get(i).getName() + " -- " + workflow));
    }

    String contextString = ",";
    for (final PubContextVOPresentation pubContextVOPresentation : contextVOList) {
      contextString += pubContextVOPresentation.getReference().getObjectId() + ",";
    }

    // Init imports
    final List<SelectItem> importSelectItems = new ArrayList<SelectItem>();
    importSelectItems.add(new SelectItem("all", this.getLabel("EditItem_NO_ITEM_SET")));

    try {
      final Connection connection = ImportLog.getConnection();
      final String sql =
          "SELECT * FROM escidoc_import_log WHERE ? LIKE '%,' || context || ',%' ORDER BY startdate DESC";
      final PreparedStatement statement = connection.prepareStatement(sql);

      statement.setString(1, contextString);

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
      MyTasksRetrieverRequestBean.logger.error("Error getting imports from database", e);
      FacesBean.error("Error getting imports from database");
    }
    this.setImportSelectItems(importSelectItems);
  }

  /**
   * Adds the list of the given affiliations to the filter select
   * 
   * @param affs
   * @param affSelectItems
   * @param level
   * @throws Exception
   */

  /**
   * Sets the current menu items for the context filter menu.
   * 
   * @param contextSelectItems
   */
  public void setContextSelectItems(List<SelectItem> contextSelectItems) {
    this.contextSelectItems = contextSelectItems;
  }

  /**
   * Returns the mneu items for the context filter menu.
   * 
   * @return
   */
  public List<SelectItem> getContextSelectItems() {
    return this.contextSelectItems;
  }

  /**
   * Called by JSF whenever the context filter menu is changed. Causes a redirect to the page with
   * updated context GET parameter.
   * 
   * @return
   */
  public void changeContext() {
    try {
      this.getBasePaginatorListSessionBean().setCurrentPageNumber(1);
      this.getBasePaginatorListSessionBean().redirect();
    } catch (final Exception e) {
      FacesBean.error("Could not redirect");
    }
  }

  /**
   * Called by JSF whenever the organizational unit filter menu is changed. Causes a redirect to the
   * page with updated context GET parameter.
   * 
   * @return
   */
  public void changeOrgUnit() {
    try {
      this.getBasePaginatorListSessionBean().setCurrentPageNumber(1);
      this.getBasePaginatorListSessionBean().redirect();
    } catch (final Exception e) {
      FacesBean.error("Could not redirect");
    }
  }

  private QAWSSessionBean getQAWSSessionBean() {
    return (QAWSSessionBean) FacesTools.findBean("QAWSSessionBean");
  }

  @Override
  public String getListPageName() {
    return "QAWSPage.jsp";
  }

  private void addChildAffiliations(List<AffiliationVOPresentation> affs,
      List<SelectItem> affSelectItems, int level) throws Exception {
    if (affs == null) {
      return;
    }

    String prefix = "";
    for (int i = 0; i < level; i++) {
      // 2 save blanks
      prefix += '\u00A0';
      prefix += '\u00A0';
      prefix += '\u00A0';
    }
    // 1 right angle
    prefix += '\u2514';
    for (final AffiliationVOPresentation aff : affs) {
      affSelectItems.add(new SelectItem(aff.getReference().getObjectId(), prefix + " "
          + aff.getName()));
      final AffiliationTree affTree = (AffiliationTree) FacesTools.findBean("AffiliationTree");
      affTree.getAffiliationMap().put(aff.getReference().getObjectId(), aff);
      if (aff.getChildren() != null) {
        this.addChildAffiliations(aff.getChildren(), affSelectItems, level + 1);
      }
    }
  }

  public List<SelectItem> getOrgUnitSelectItems() {
    final List<SelectItem> userAffiliationsList = new ArrayList<SelectItem>();
    userAffiliationsList.add(new SelectItem("all", this.getLabel("EditItem_NO_ITEM_SET")));
    try {
      final List<AffiliationVOPresentation> affList =
          this.getLoginHelper().getAccountUsersAffiliations();
      Collections.sort(affList);
      this.addChildAffiliations(affList, userAffiliationsList, 0);
    } catch (final Exception e) {
      // TODO
    }
    this.getQAWSSessionBean().setOrgUnitSelectItems(userAffiliationsList);

    return userAffiliationsList;
  }

  public void setSelectedOrgUnit(String selectedOrgUnit) {
    this.getQAWSSessionBean().setSelectedOrgUnit(selectedOrgUnit);
    this.getBasePaginatorListSessionBean().getParameterMap()
        .put(MyTasksRetrieverRequestBean.parameterSelectedOrgUnit, selectedOrgUnit);
  }

  public String getSelectedOrgUnit() {
    return this.getQAWSSessionBean().getSelectedOrgUnit();
  }
}
