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
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
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
        FacesTools.getExternalContext().getRequestParameterMap()
            .get(YearbookModeratorRetrieverRequestBean.parameterSelectedOrgUnit);
    if (orgUnit == null) {
      this.setSelectedOrgUnit(this.getYearbookCandidatesSessionBean().getSelectedOrgUnit());
    } else {
      this.setSelectedOrgUnit(orgUnit);
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
    this.getBasePaginatorListSessionBean().getParameterMap()
        .put(YearbookModeratorRetrieverRequestBean.parameterSelectedOrgUnit, selectedOrgUnit);
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
      FacesBean.error("Could not redirect");
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
      final FilterTaskParamVO filter = new FilterTaskParamVO();

      // add all contexts for which the user has moderator rights (except the "all" item of the
      // menu)
      for (final ContextVO context : this.getContextListSessionBean()
          .getYearbookModeratorContextList()) {
        filter.getFilterList().add(filter.new ContextFilter(context.getReference().getObjectId()));
      }
      // add views per page limit
      final Filter f8 = filter.new LimitFilter(String.valueOf(limit));
      filter.getFilterList().add(f8);
      final Filter f9 = filter.new OffsetFilter(String.valueOf(offset));
      filter.getFilterList().add(f9);
      final String xmlItemList =
          ServiceLocator.getItemHandler(this.getLoginHelper().getESciDocUserHandle())
              .retrieveItems(filter.toMap());

      final SearchRetrieveResponseVO<PubItemVO> result =
          XmlTransformingService.transformToSearchRetrieveResponse(xmlItemList);

      final List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
      for (final SearchRetrieveRecordVO<PubItemVO> yearbookRecord : result.getRecords()) {
        pubItemList.add((PubItemVO) yearbookRecord.getData());
      }

      this.numberOfRecords = result.getNumberOfRecords();
      returnList = CommonUtils.convertToPubItemVOPresentationList(pubItemList);
    } catch (final Exception e) {
      YearbookModeratorRetrieverRequestBean.logger.error("Error in retrieving items", e);
      FacesBean.error("Error in retrieving items");
      this.numberOfRecords = 0;
    }

    return returnList;
  }

  public String exportSelectedDownload() {
    try {
      List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();

      String query = "";
      for (final PubItemVO item : ((PubItemListSessionBean) this.getBasePaginatorListSessionBean())
          .getSelectedItems()) {
        if (item.getRelations() != null && item.getRelations().size() > 0) {
          final MetadataSearchQuery mdQuery =
              YearbookCandidatesRetrieverRequestBean.getMemberQuery(item);

          if (!query.equals("")) {
            query += " OR ";
          }

          query += " ( " + mdQuery.getCqlQuery() + " ) ";
        }
      }

      final ItemContainerSearchResult result =
          SearchService.searchForItemContainer(new PlainCqlQuery(query));

      pubItemList = SearchRetrieverRequestBean.extractItemsOfSearchResult(result);
      ((PubItemListSessionBean) this.getBasePaginatorListSessionBean())
          .downloadExportFile(pubItemList);

    } catch (final Exception e) {
      FacesBean.error("Error while exporting");
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
      if (((PubItemListSessionBean) this.getBasePaginatorListSessionBean()).getSelectedItems()
          .size() > 0) {
        for (final PubItemVOPresentation yearbookItem : ((PubItemListSessionBean) this
            .getBasePaginatorListSessionBean()).getSelectedItems()) {
          if (State.SUBMITTED.equals(yearbookItem.getVersion().getState())) {
            PubItemService
                .releasePubItem(yearbookItem.getVersion(), yearbookItem.getModificationDate(),
                    "Releasing pubItem", this.getLoginHelper().getAccountUser());
          } else {
            this.warn("\"" + yearbookItem.getFullTitle() + "\""
                + this.getMessage("Yearbook_itemNotReleasedWarning"));
          }
        }
        this.info(this.getMessage("Yearbook_ReleasedSuccessfully"));
      } else {
        this.warn(this.getMessage("Yearbook_noItemsSelected"));
      }
    } catch (final Exception e) {
      FacesBean.error(this.getMessage("Yearbook_ReleaseError"));
      YearbookModeratorRetrieverRequestBean.logger.error("Could not release Yearbook Item", e);
    }

    ((PubItemListSessionBean) this.getBasePaginatorListSessionBean()).redirect();

    return "";
  }

  /**
   * sends all selected yearbooks back for rework
   * 
   * @return empty String to stay on the page
   */
  public String sendBackForRework() {
    try {
      if (((PubItemListSessionBean) this.getBasePaginatorListSessionBean()).getSelectedItems()
          .size() > 0) {
        final ItemHandler itemHandler =
            ServiceLocator.getItemHandler(this.getLoginHelper().getESciDocUserHandle());
        TaskParamVO param = null;
        String paramXml = null;
        for (final PubItemVOPresentation yearbookItem : ((PubItemListSessionBean) this
            .getBasePaginatorListSessionBean()).getSelectedItems()) {
          if (State.SUBMITTED.equals(yearbookItem.getVersion().getState())) {
            param =
                new TaskParamVO(yearbookItem.getModificationDate(), "Send yearbook back for rework");
            paramXml = XmlTransformingService.transformToTaskParam(param);
            itemHandler.revise(yearbookItem.getVersion().getObjectId(), paramXml);
          } else {
            this.warn("\"" + yearbookItem.getFullTitle() + "\""
                + this.getMessage("Yearbook_itemNotSentBackWarning"));
          }
        }
        this.info(this.getMessage("Yearbook_revisedSuccessfully"));
      } else {
        this.warn(this.getMessage("Yearbook_noItemsSelected"));
      }
    } catch (final Exception e) {
      FacesBean.error(this.getMessage("Yearbook_sendBackForReworkError"));
      YearbookModeratorRetrieverRequestBean.logger.error("Could not send back Yearbook Item", e);
    }

    ((PubItemListSessionBean) this.getBasePaginatorListSessionBean()).update();
    ((PubItemListSessionBean) this.getBasePaginatorListSessionBean()).redirect();

    return "";
  }

  private YearbookCandidatesSessionBean getYearbookCandidatesSessionBean() {
    return (YearbookCandidatesSessionBean) FacesTools.findBean("YearbookCandidatesSessionBean");
  }

  private ContextListSessionBean getContextListSessionBean() {
    return (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
  }
}
