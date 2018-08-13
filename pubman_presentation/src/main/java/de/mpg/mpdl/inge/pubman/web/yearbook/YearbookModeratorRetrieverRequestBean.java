package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO.State;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.export.ExportItems;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.pubman.impl.YearbookServiceDbImpl;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the Yearbook
 * workspace. It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean and
 * adds additional functionality for filtering the items by their state.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3780 $ $LastChangedDate: 2010-07-23 10:01:12 +0200 (Fri, 23 Jul 2010) $
 * 
 */
@ManagedBean(name = "YearbookModeratorRetrieverRequestBean")
@SuppressWarnings("serial")
public class YearbookModeratorRetrieverRequestBean
    extends BaseListRetrieverRequestBean<YearbookDbVO, YearbookModeratorListSessionBean.SORT_CRITERIA> {
  private static final Logger logger = Logger.getLogger(YearbookModeratorRetrieverRequestBean.class);

  private static String parameterSelectedOrgUnit = "orgUnit";
  /**
   * The GET parameter name for the item state.
   */
  protected static String parameterSelectedItemState = "itemState";

  /**
   * The menu entries of the item state filtering menu
   */
  private List<SelectItem> itemStateSelectItems;

  private String selectedSortOrder;
  AccountUserVO userVO;
  private int numberOfRecords;

  /**
   * The currently selected item state.
   */
  private String selectedItemState;

  public YearbookModeratorRetrieverRequestBean() {
    super((YearbookModeratorListSessionBean) FacesTools.findBean("YearbookModeratorListSessionBean"), false);
  }

  @Override
  public void init() {
    // no init needed
  }

  @Override
  public int getTotalNumberOfRecords() {
    return this.numberOfRecords;
  }

  /**
   * Reads out the item state parameter from the HTTP GET request and sets an default value if it is
   * null.
   */
  @Override
  public void readOutParameters() {
    final String orgUnit =
        FacesTools.getExternalContext().getRequestParameterMap().get(YearbookModeratorRetrieverRequestBean.parameterSelectedOrgUnit);
    if (orgUnit == null) {
      this.setSelectedOrgUnit(this.getYearbookCandidatesSessionBean().getSelectedOrgUnit());
    } else {
      this.setSelectedOrgUnit(orgUnit);
    }

    final String selectedItemState =
        FacesTools.getExternalContext().getRequestParameterMap().get(YearbookModeratorRetrieverRequestBean.parameterSelectedItemState);
    if (selectedItemState == null) {
      this.setSelectedItemState("all");
    } else {
      this.setSelectedItemState(selectedItemState);
    }
  }

  @Override
  public String getType() {
    return "YearbookModeratorPage";
  }

  @Override
  public String getListPageName() {
    return "YearbookModeratorPage.jsp";
  }

  public List<SelectItem> getOrgUnitSelectItems() {
    return this.getYearbookCandidatesSessionBean().getOrgUnitSelectItems();
  }

  public void setSelectedOrgUnit(String selectedOrgUnit) {
    this.getYearbookCandidatesSessionBean().setSelectedOrgUnit(selectedOrgUnit);
    this.getBasePaginatorListSessionBean().getParameterMap().put(YearbookModeratorRetrieverRequestBean.parameterSelectedOrgUnit,
        selectedOrgUnit);
  }

  public String getSelectedOrgUnit() {
    return this.getYearbookCandidatesSessionBean().getSelectedOrgUnit();
  }

  /**
   * Called by JSF whenever the organizational unit filter menu is changed. Causes a redirect to the
   * page with updated context GET parameter.
   * 
   * @return
   */
  public String changeOrgUnit() {
    try {
      this.getBasePaginatorListSessionBean().setCurrentPageNumber(1);
      this.getBasePaginatorListSessionBean().redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }

    return "";
  }

  public String getSelectedSortOrder() {
    return this.selectedSortOrder;
  }

  public void setSelectedSortOrder(String selectedSortOrder) {
    this.selectedSortOrder = selectedSortOrder;
  }

  @Override
  public List<YearbookDbVO> retrieveList(int offset, int limit, YearbookModeratorListSessionBean.SORT_CRITERIA sc) {
    try {

      BoolQueryBuilder qb = null;
      if (!getLoginHelper().getIsYearbookAdmin()) {
        qb = QueryBuilders.boolQuery();
        for (String orgId : YearbookUtils.getYearbookOrganizationIds(this.getLoginHelper().getAccountUser())) {
          ((BoolQueryBuilder) qb).should(QueryBuilders.termQuery(YearbookServiceDbImpl.INDEX_ORGANIZATION_ID, orgId));
        }


      }

      if (!this.getSelectedItemState().toLowerCase().equals("all")) {
        if (qb == null) {
          qb = QueryBuilders.boolQuery();
        }
        qb.must(QueryBuilders.termQuery(YearbookServiceDbImpl.INDEX_STATE, getSelectedItemState()));
      }



      SearchSortCriteria elsc = new SearchSortCriteria(sc.getIndex(), SortOrder.valueOf(sc.getSortOrder()));

      SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, limit, offset, elsc);
      SearchRetrieveResponseVO<YearbookDbVO> resp =
          ApplicationBean.INSTANCE.getYearbookService().search(srr, getLoginHelper().getAuthenticationToken());

      this.numberOfRecords = resp.getNumberOfRecords();
      List<YearbookDbVO> yearbooks = resp.getRecords().stream().map(i -> i.getData()).collect(Collectors.toList());
      return yearbooks;
    } catch (final Exception e) {
      YearbookModeratorRetrieverRequestBean.logger.error("Error in retrieving items", e);
      this.error("Error in retrieving items");
      this.numberOfRecords = 0;
    }

    return null;
    /*
     * List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();
     * 
     * // define the filter criteria final FilterTaskParamVO filter = new FilterTaskParamVO();
     * 
     * // add all contexts for which the user has moderator rights (except the "all" item of the //
     * menu) for (final ContextDbVO context : this.getContextListSessionBean()
     * .getYearbookModeratorContextList()) { filter.getFilterList().add(filter.new
     * ContextFilter(context.getReference().getObjectId())); } // add views per page limit final
     * Filter f8 = filter.new LimitFilter(String.valueOf(limit)); filter.getFilterList().add(f8);
     * final Filter f9 = filter.new OffsetFilter(String.valueOf(offset));
     * filter.getFilterList().add(f9); final String xmlItemList =
     * ServiceLocator.getItemHandler(this.getLoginHelper().getESciDocUserHandle())
     * .retrieveItems(filter.toMap());
     * 
     * final SearchRetrieveResponseVO<ItemVersionVO> result =
     * XmlTransformingService.transformToSearchRetrieveResponse(xmlItemList);
     * 
     * final List<ItemVersionVO> pubItemList = new ArrayList<ItemVersionVO>(); for (final
     * SearchRetrieveRecordVO<ItemVersionVO> yearbookRecord : result.getRecords()) {
     * pubItemList.add((ItemVersionVO) yearbookRecord.getData()); }
     * 
     * this.numberOfRecords = result.getNumberOfRecords(); returnList =
     * CommonUtils.convertToPubItemVOPresentationList(pubItemList); } catch (final Exception e) {
     * YearbookModeratorRetrieverRequestBean.logger.error("Error in retrieving items", e);
     * FacesBean.error("Error in retrieving items"); this.numberOfRecords = 0; }
     * 
     * return returnList;
     */
  }

  private List<YearbookDbVO> getSelectedYearbooks() {
    List<YearbookDbVO> yearbookList = new ArrayList<YearbookDbVO>();
    for (final Map.Entry<YearbookDbVO, Boolean> entry : getBasePaginatorListSessionBean().getCurrentSelections().entrySet()) {
      if (entry.getValue()) {
        yearbookList.add(entry.getKey());
      }

    }
    return yearbookList;
  }



  public String exportSelectedDownload() {
    try {

      List<YearbookDbVO> selectedYearbooks = getSelectedYearbooks();

      List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();

      //      BoolQueryBuilder bq = QueryBuilders.boolQuery();
      for (YearbookDbVO yb : selectedYearbooks) {

        pubItemList.addAll(YearbookUtils.retrieveAllMembers(yb, getLoginHelper().getAuthenticationToken()));
      }


      if (pubItemList.size() != 0) {
        PubItemListSessionBean.exportAndDownload(pubItemList);

      } else {
        this.error(this.getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED));
      }


    } catch (final Exception e) {
      this.error("Error while exporting");
      YearbookModeratorRetrieverRequestBean.logger.error("Error exporting yearbook", e);
    }

    return "";
  }



  /**
   * releases all selected yearbooks
   * 
   * @return empty String to stay on the page
   */

  public String releaseSelectedYearbooks() {
    try {

      if (getSelectedYearbooks().size() > 0) {
        for (YearbookDbVO yb : getSelectedYearbooks()) {

          if (State.SUBMITTED.equals(yb.getState())) {
            ApplicationBean.INSTANCE.getYearbookService().release(yb.getObjectId(), yb.getLastModificationDate(),
                getLoginHelper().getAuthenticationToken());
          } else {
            this.warn("\"" + yb.getName() + "\"" + this.getMessage("Yearbook_itemNotReleasedWarning"));
          }


        }
        this.info(this.getMessage("Yearbook_ReleasedSuccessfully"));
      } else {

        this.warn(this.getMessage("Yearbook_noItemsSelected"));
      }
    } catch (final Exception e) {
      error(this.getMessage("Yearbook_ReleaseError"));
      YearbookModeratorRetrieverRequestBean.logger.error("Could not release Yearbook Item", e);
    }

    this.getBasePaginatorListSessionBean().redirect();

    return "";
  }


  /**
   * sends all selected yearbooks back for rework
   * 
   * @return empty String to stay on the page
   */

  public String sendBackForRework() {
    try {

      if (getSelectedYearbooks().size() > 0) {
        for (YearbookDbVO yb : getSelectedYearbooks()) {

          if (State.SUBMITTED.equals(yb.getState()) || State.RELEASED.equals(yb.getState())) {
            ApplicationBean.INSTANCE.getYearbookService().revise(yb.getObjectId(), yb.getLastModificationDate(),
                getLoginHelper().getAuthenticationToken());
          } else {
            this.warn("\"" + yb.getName() + "\"" + this.getMessage("Yearbook_itemNotSentBackWarning"));
          }


        }
        this.info(this.getMessage("Yearbook_revisedSuccessfully"));
      } else {

        this.warn(this.getMessage("Yearbook_noItemsSelected"));
      }
    } catch (final Exception e) {
      this.error(this.getMessage("Yearbook_sendBackForReworkError"));
      YearbookModeratorRetrieverRequestBean.logger.error("Could not send back Yearbook Item", e);
    }

    this.getBasePaginatorListSessionBean().redirect();

    return "";
  }



  public String editMembers(YearbookDbVO yearbook) {

    YearbookItemSessionBean yisb = FacesTools.findBean("YearbookItemSessionBean");
    yisb.initYearbook(yearbook.getObjectId());
    return "loadYearbookPage";

  }

  public List<String> convertSetToList(Set<String> set) {
    if (set != null) {
      return new ArrayList<>(set);
    }
    return null;
  }

  public List<SelectItem> getItemStateSelectItems() {
    this.itemStateSelectItems = new ArrayList<SelectItem>();
    this.itemStateSelectItems.add(new SelectItem("all", this.getLabel("ItemList_filterAllExceptWithdrawn")));
    this.itemStateSelectItems.add(new SelectItem(YearbookDbVO.State.CREATED.name(),
        this.getLabel(this.getI18nHelper().convertEnumToString(YearbookDbVO.State.CREATED))));
    this.itemStateSelectItems.add(new SelectItem(YearbookDbVO.State.SUBMITTED.name(),
        this.getLabel(this.getI18nHelper().convertEnumToString(YearbookDbVO.State.SUBMITTED))));
    this.itemStateSelectItems.add(new SelectItem(YearbookDbVO.State.RELEASED.name(),
        this.getLabel(this.getI18nHelper().convertEnumToString(YearbookDbVO.State.RELEASED))));

    return this.itemStateSelectItems;
  }

  /**
   * Sets the selected item state filter
   * 
   * @param selectedItemState
   */
  public void setSelectedItemState(String selectedItemState) {
    this.selectedItemState = selectedItemState;
    this.getBasePaginatorListSessionBean().getParameterMap().put(parameterSelectedItemState, selectedItemState);
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
   * Called by JSF whenever the item state menu is changed.
   * 
   * @return
   */
  public String changeItemState() {
    try {
      this.getBasePaginatorListSessionBean().setCurrentPageNumber(1);
      this.getBasePaginatorListSessionBean().redirect();
    } catch (final Exception e) {
      logger.error("Error during redirection.", e);
      this.error("Could not redirect");
    }

    return "";

  }


  private YearbookCandidatesSessionBean getYearbookCandidatesSessionBean() {
    return (YearbookCandidatesSessionBean) FacesTools.findBean("YearbookCandidatesSessionBean");
  }

}
