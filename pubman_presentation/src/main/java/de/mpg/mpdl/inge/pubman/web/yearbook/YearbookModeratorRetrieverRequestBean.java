package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.TaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.PubItemService;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.search.SearchRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.search.SearchService;
import de.mpg.mpdl.inge.search.query.ItemContainerSearchResult;
import de.mpg.mpdl.inge.search.query.MetadataSearchQuery;
import de.mpg.mpdl.inge.search.query.PlainCqlQuery;

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
public class YearbookModeratorRetrieverRequestBean extends
    BaseListRetrieverRequestBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA> {
  public static final String BEAN_NAME = "YearbookModeratorRetrieverRequestBean";

  private static final Logger logger = Logger
      .getLogger(YearbookModeratorRetrieverRequestBean.class);

  private static String parameterSelectedOrgUnit = "orgUnit";

  private String selectedSortOrder;
  AccountUserVO userVO;
  private int numberOfRecords;

  public YearbookModeratorRetrieverRequestBean() {
    super((PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean"), false);
  }

  @Override
  public void init() {}

  @Override
  public int getTotalNumberOfRecords() {
    return numberOfRecords;
  }

  /**
   * Reads out the item state parameter from the HTTP GET request and sets an default value if it is
   * null.
   */
  @Override
  public void readOutParameters() {
    String orgUnit =
        FacesTools.getExternalContext().getRequestParameterMap().get(parameterSelectedOrgUnit);
    if (orgUnit == null) {
      setSelectedOrgUnit(this.getYearbookCandidatesSessionBean().getSelectedOrgUnit());
    } else {
      setSelectedOrgUnit(orgUnit);
    }
  }

  @Override
  public String getType() {
    return "SearchResult";
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
    getBasePaginatorListSessionBean().getParameterMap().put(parameterSelectedOrgUnit,
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
      getBasePaginatorListSessionBean().setCurrentPageNumber(1);
      getBasePaginatorListSessionBean().redirect();
    } catch (Exception e) {
      error("Could not redirect");
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
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {
    List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();
    try {
      // define the filter criteria
      FilterTaskParamVO filter = new FilterTaskParamVO();

      // add all contexts for which the user has moderator rights (except the "all" item of the
      // menu)
      for (ContextVO context : this.getContextListSessionBean().getYearbookModeratorContextList()) {
        filter.getFilterList().add(filter.new ContextFilter(context.getReference().getObjectId()));
      }
      // add views per page limit
      Filter f8 = filter.new LimitFilter(String.valueOf(limit));
      filter.getFilterList().add(f8);
      Filter f9 = filter.new OffsetFilter(String.valueOf(offset));
      filter.getFilterList().add(f9);
      String xmlItemList =
          ServiceLocator.getItemHandler(getLoginHelper().getESciDocUserHandle()).retrieveItems(
              filter.toMap());

      SearchRetrieveResponseVO result =
          XmlTransformingService.transformToSearchRetrieveResponse(xmlItemList);

      List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
      for (SearchRetrieveRecordVO yearbookRecord : result.getRecords()) {
        pubItemList.add((PubItemVO) yearbookRecord.getData());
      }

      numberOfRecords = result.getNumberOfRecords();
      returnList = CommonUtils.convertToPubItemVOPresentationList(pubItemList);
    } catch (Exception e) {
      logger.error("Error in retrieving items", e);
      error("Error in retrieving items");
      numberOfRecords = 0;
    }

    return returnList;
  }

  public String exportSelectedDownload() {
    try {
      List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();

      String query = "";
      for (PubItemVO item : ((PubItemListSessionBean) getBasePaginatorListSessionBean())
          .getSelectedItems()) {
        if (item.getRelations() != null && item.getRelations().size() > 0) {
          MetadataSearchQuery mdQuery = YearbookCandidatesRetrieverRequestBean.getMemberQuery(item);

          if (!query.equals("")) {
            query += " OR ";
          }

          query += " ( " + mdQuery.getCqlQuery() + " ) ";
        }
      }

      ItemContainerSearchResult result =
          SearchService.searchForItemContainer(new PlainCqlQuery(query));

      pubItemList = SearchRetrieverRequestBean.extractItemsOfSearchResult(result);
      ((PubItemListSessionBean) getBasePaginatorListSessionBean()).downloadExportFile(pubItemList);

    } catch (Exception e) {
      error("Error while exporting");
      logger.error("Error exporting yearbook", e);
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
      if (((PubItemListSessionBean) getBasePaginatorListSessionBean()).getSelectedItems().size() > 0) {
        for (PubItemVOPresentation yearbookItem : ((PubItemListSessionBean) getBasePaginatorListSessionBean())
            .getSelectedItems()) {
          if (State.SUBMITTED.equals(yearbookItem.getVersion().getState())) {
            PubItemService.releasePubItem(yearbookItem.getVersion(), yearbookItem
                .getModificationDate(), "Releasing pubItem", getLoginHelper().getAccountUser());
          } else {
            warn("\"" + yearbookItem.getFullTitle() + "\""
                + getMessage("Yearbook_itemNotReleasedWarning"));
          }
        }
        info(getMessage("Yearbook_ReleasedSuccessfully"));
      } else {
        warn(getMessage("Yearbook_noItemsSelected"));
      }
    } catch (Exception e) {
      error(getMessage("Yearbook_ReleaseError"));
      logger.error("Could not release Yearbook Item", e);
    }

    ((PubItemListSessionBean) getBasePaginatorListSessionBean()).redirect();

    return "";
  }

  /**
   * sends all selected yearbooks back for rework
   * 
   * @return empty String to stay on the page
   */
  public String sendBackForRework() {
    try {
      if (((PubItemListSessionBean) getBasePaginatorListSessionBean()).getSelectedItems().size() > 0) {
        ItemHandler itemHandler =
            ServiceLocator.getItemHandler(getLoginHelper().getESciDocUserHandle());
        TaskParamVO param = null;
        String paramXml = null;
        for (PubItemVOPresentation yearbookItem : ((PubItemListSessionBean) getBasePaginatorListSessionBean())
            .getSelectedItems()) {
          if (State.SUBMITTED.equals(yearbookItem.getVersion().getState())) {
            param =
                new TaskParamVO(yearbookItem.getModificationDate(), "Send yearbook back for rework");
            paramXml = XmlTransformingService.transformToTaskParam(param);
            itemHandler.revise(yearbookItem.getVersion().getObjectId(), paramXml);
          } else {
            warn("\"" + yearbookItem.getFullTitle() + "\""
                + getMessage("Yearbook_itemNotSentBackWarning"));
          }
        }
        info(getMessage("Yearbook_revisedSuccessfully"));
      } else {
        warn(getMessage("Yearbook_noItemsSelected"));
      }
    } catch (Exception e) {
      error(getMessage("Yearbook_sendBackForReworkError"));
      logger.error("Could not send back Yearbook Item", e);
    }

    ((PubItemListSessionBean) getBasePaginatorListSessionBean()).update();
    ((PubItemListSessionBean) getBasePaginatorListSessionBean()).redirect();

    return "";
  }

  private YearbookCandidatesSessionBean getYearbookCandidatesSessionBean() {
    return (YearbookCandidatesSessionBean) FacesTools.findBean("YearbookCandidatesSessionBean");
  }

  private ContextListSessionBean getContextListSessionBean() {
    return (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
  }
}
