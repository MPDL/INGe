package de.mpg.mpdl.inge.pubman.web.qaws;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.affiliation.AffiliationBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.multipleimport.DbTools;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

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

    if (!this.getLoginHelper().isLoggedIn() || !this.getLoginHelper().getIsModerator()
        || this.getLoginHelper().getAuthenticationToken() == null) {
      return returnList;
    }

    try {
      this.checkSortCriterias(sc);
      
      BoolQueryBuilder bq = QueryBuilders.boolQuery();

      if (getSelectedItemState().toLowerCase().equals("withdrawn")) {
        bq.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_STATE, "WITHDRAWN"));
      }

      else if (getSelectedItemState().toLowerCase().equals("all")) {
        BoolQueryBuilder stateQueryBuilder = QueryBuilders.boolQuery();
        stateQueryBuilder.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_STATE, "SUBMITTED"));
        stateQueryBuilder.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_STATE, "RELEASED"));
        stateQueryBuilder.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_STATE, "IN_REVISION"));
        bq.must(stateQueryBuilder);
        bq.mustNot(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, "WITHDRAWN"));
      }

      else {
        bq.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_STATE,
            ItemVO.State.valueOf(getSelectedItemState()).name()));
        bq.mustNot(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, "WITHDRAWN"));
      }
      
      if (!this.getSelectedImport().toLowerCase().equals("all")) {
        bq.must(QueryBuilders.matchQuery(PubItemServiceDbImpl.INDEX_LOCAL_TAGS, this.getSelectedImport()).operator(Operator.AND));
      }

      if (this.getSelectedContext().toLowerCase().equals("all")) {
        // add all contexts for which the user has moderator rights (except the "all" item of the
        // menu)
        BoolQueryBuilder contextQueryBuilder = QueryBuilders.boolQuery();
        bq.must(contextQueryBuilder);
        for (int i = 1; i < this.getContextSelectItems().size(); i++) {
          final String contextId = (String) this.getContextSelectItems().get(i).getValue();
          contextQueryBuilder.should(
              QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_CONTEXT_OBJECT_ID, contextId));
        }
      } else {
        bq.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_CONTEXT_OBJECT_ID,
            getSelectedContext()));
      }

      if (!this.getSelectedOrgUnit().toLowerCase().equals("all")) {
        // TODO org unit filter!!
      }

      // TODO Sorting!!
      SearchSortCriteria ssc = new SearchSortCriteria(PubItemServiceDbImpl.INDEX_MODIFICATION_DATE, SortOrder.DESC);
      SearchRetrieveRequestVO<QueryBuilder> srr =
          new SearchRetrieveRequestVO<QueryBuilder>(bq, limit, offset, ssc);

      SearchRetrieveResponseVO<PubItemVO> resp = ApplicationBean.INSTANCE.getPubItemService()
          .search(srr, getLoginHelper().getAuthenticationToken());

      this.numberOfRecords = resp.getNumberOfRecords();
      
      List<PubItemVO> pubItemList = resp.getRecords().stream().map(SearchRetrieveRecordVO::getData)
          .collect(Collectors.toList());
      
      returnList = CommonUtils.convertToPubItemVOPresentationList(pubItemList);
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
    final AffiliationBean affTree = (AffiliationBean) FacesTools.findBean("AffiliationBean");

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
    itemStateSelectItems.add(new SelectItem(ItemVO.State.SUBMITTED.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(ItemVO.State.SUBMITTED))));
    itemStateSelectItems.add(new SelectItem(ItemVO.State.RELEASED.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(ItemVO.State.RELEASED))));
    itemStateSelectItems.add(new SelectItem(ItemVO.State.IN_REVISION.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(ItemVO.State.IN_REVISION))));
    itemStateSelectItems.add(new SelectItem(ItemVO.State.WITHDRAWN.name(), this.getLabel(this
        .getI18nHelper().convertEnumToString(ItemVO.State.WITHDRAWN))));
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

    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    final String sql =
        "SELECT * FROM import_log WHERE ? LIKE '%,' || context || ',%' ORDER BY startdate DESC";

    try {
      connection = DbTools.getNewConnection();
      ps = connection.prepareStatement(sql);
      ps.setString(1, contextString);
      rs = ps.executeQuery();

      while (rs.next()) {
        final SelectItem selectItem =
            new SelectItem(rs.getString("name") + " "
                + ImportLog.DATE_FORMAT.format(rs.getTimestamp("startdate")));
        importSelectItems.add(selectItem);
      }
    } catch (final Exception e) {
      MyTasksRetrieverRequestBean.logger.error("Error getting imports from database", e);
      FacesBean.error("Error getting imports from database");
    } finally {
      DbTools.closeResultSet(rs);
      DbTools.closePreparedStatement(ps);
      DbTools.closeConnection(connection);
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
      final AffiliationBean affTree = (AffiliationBean) FacesTools.findBean("AffiliationBean");
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
