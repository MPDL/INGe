package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.yearbook.YearbookItemSessionBean.YBWORKSPACE;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;

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
@ManagedBean(name = "YearbookCandidatesRetrieverRequestBean")
@SuppressWarnings("serial")
public class YearbookCandidatesRetrieverRequestBean
    extends BaseListRetrieverRequestBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA> {
  private static final Logger logger = Logger.getLogger(YearbookCandidatesRetrieverRequestBean.class);

  private String selectedSortOrder;

  /**
   * This workspace's user.
   */
  AccountUserVO userVO;

  /**
   * The GET parameter name for the item state.
   */
  protected static String parameterSelectedItemState = "itemState";

  /**
   * org unit filter.
   */
  private static String parameterSelectedOrgUnit = "orgUnit";

  /**
   * The total number of records
   */
  private int numberOfRecords;

  public YearbookCandidatesRetrieverRequestBean() {
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
        FacesTools.getExternalContext().getRequestParameterMap().get(YearbookCandidatesRetrieverRequestBean.parameterSelectedOrgUnit);
    if (orgUnit == null) {
      if (this.getYearbookCandidatesSessionBean().getSelectedOrgUnit() != null || this.getYearbookItemSessionBean().getYearbook() == null) {
        this.setSelectedOrgUnit(this.getYearbookCandidatesSessionBean().getSelectedOrgUnit());
      } else {
        this.setSelectedOrgUnit(this.getYearbookCandidatesSessionBean().getOrgUnitSelectItems().get(0).getValue().toString());
      }

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
    return "YearbookPage.jsp";
  }

  public String addSelectedToYearbook() {
    final YearbookItemSessionBean yisb = (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
    final List<String> selected = new ArrayList<String>();
    for (final PubItemVOPresentation item : ((PubItemListSessionBean) this.getBasePaginatorListSessionBean()).getSelectedItems()) {
      selected.add(item.getObjectId());
    }
    yisb.addMembers(selected);
    this.getBasePaginatorListSessionBean().update();
    return "";
  }

  public String removeSelectedFromYearbook() {
    final YearbookItemSessionBean yisb = (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
    final List<String> selected = new ArrayList<String>();
    for (final PubItemVOPresentation item : ((PubItemListSessionBean) this.getBasePaginatorListSessionBean()).getSelectedItems()) {
      selected.add(item.getObjectId());
    }
    yisb.removeMembers(selected);
    this.getBasePaginatorListSessionBean().update();
    return "";
  }

  public List<SelectItem> getOrgUnitSelectItems() {
    return this.getYearbookCandidatesSessionBean().getOrgUnitSelectItems();
  }

  public void setSelectedOrgUnit(String selectedOrgUnit) {
    this.getYearbookCandidatesSessionBean().setSelectedOrgUnit(selectedOrgUnit);
    this.getBasePaginatorListSessionBean().getParameterMap().put(YearbookCandidatesRetrieverRequestBean.parameterSelectedOrgUnit,
        selectedOrgUnit);
  }

  public String getSelectedOrgUnit() {
    return this.getYearbookCandidatesSessionBean().getSelectedOrgUnit();
  }

  private YearbookCandidatesSessionBean getYearbookCandidatesSessionBean() {
    return (YearbookCandidatesSessionBean) FacesTools.findBean("YearbookCandidatesSessionBean");
  }

  private YearbookItemSessionBean getYearbookItemSessionBean() {
    return (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
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



  private BoolQueryBuilder getNonCandidatesQuery() throws Exception {
    final YearbookItemSessionBean yisb = this.getYearbookItemSessionBean();

    BoolQueryBuilder nonCandidateBoolQuery = QueryBuilders.boolQuery();
    nonCandidateBoolQuery.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, "RELEASED"));

    // Contexts
    BoolQueryBuilder contextBoolQuery = QueryBuilders.boolQuery();
    nonCandidateBoolQuery.must(contextBoolQuery);
    for (String contextId : yisb.getYearbook().getContextIds()) {
      contextBoolQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_CONTEXT_OBJECT_ID, contextId));
    }


    nonCandidateBoolQuery.mustNot(YearbookUtils.getCandidateQuery());


    // Remove members
    if (yisb.getYearbook().getItemIds().size() > 0) {
      BoolQueryBuilder memberBoolQuery = QueryBuilders.boolQuery();
      nonCandidateBoolQuery.must(memberBoolQuery);
      for (String id : yisb.getYearbook().getItemIds()) {
        memberBoolQuery.mustNot(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, id));
      }
    }



    if (!this.getSelectedOrgUnit().toLowerCase().equals("all")) {
      BoolQueryBuilder ouBoolQuery = QueryBuilders.boolQuery();
      nonCandidateBoolQuery.must(ouBoolQuery);
      ouBoolQuery.should(
          QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIERPATH, getSelectedOrgUnit()));
      ouBoolQuery
          .should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIERPATH, getSelectedOrgUnit()));
    }

    return nonCandidateBoolQuery;

  }

  private BoolQueryBuilder getInvalidMembersQuery() throws Exception {

    final YearbookItemSessionBean yisb = (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
    BoolQueryBuilder bq = QueryBuilders.boolQuery();
    if (yisb.getInvalidItemMap().size() > 0) {



      for (final YearbookInvalidItemRO item : yisb.getInvalidItemMap().values()) {
        bq.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, item.getObjectId()));
      }


      return bq;
    }
    return null;


  }



  @Override
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {
    final YearbookItemSessionBean yisb = this.getYearbookItemSessionBean();

    try {
      if (yisb.getYearbook() != null) {
        BoolQueryBuilder query = null;
        if (yisb.getSelectedWorkspace().equals(YBWORKSPACE.CANDIDATES)) {
          query = YearbookUtils.getCandidateQuery();
        } else if (yisb.getSelectedWorkspace().equals(YBWORKSPACE.MEMBERS)) {
          query = YearbookUtils.getMemberQuery(yisb.getYearbook());
        } else if (yisb.getSelectedWorkspace().equals(YBWORKSPACE.INVALID)) {
          query = this.getInvalidMembersQuery();
        } else if (yisb.getSelectedWorkspace().equals(YBWORKSPACE.NON_CANDIDATES)) {
          query = this.getNonCandidatesQuery();
        }

        if (query != null) {

          if (!this.getSelectedOrgUnit().toLowerCase().equals("all")) {

            BoolQueryBuilder ouBoolQuery = QueryBuilders.boolQuery();
            query.must(ouBoolQuery);
            ouBoolQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIERPATH,
                getSelectedOrgUnit()));
            ouBoolQuery.should(
                QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIERPATH, getSelectedOrgUnit()));

          }



          PubItemService pis = ApplicationBean.INSTANCE.getPubItemService();
          SearchSourceBuilder ssb = new SearchSourceBuilder();
          ssb.query(query);
          ssb.from(offset);
          ssb.size(limit);


          for (String index : sc.getIndex()) {
            if (!index.isEmpty()) {
              ssb.sort(SearchUtils.baseElasticSearchSortBuilder(pis.getElasticSearchIndexFields(), index,
                  SortOrder.ASC.equals(sc.getSortOrder()) ? org.elasticsearch.search.sort.SortOrder.ASC
                      : org.elasticsearch.search.sort.SortOrder.DESC));
            }
          }

          SearchResponse resp = pis.searchDetailed(ssb, null);

          this.numberOfRecords = (int) resp.getHits().getTotalHits();

          List<ItemVersionVO> pubItemList = SearchUtils.getRecordListFromElasticSearchResponse(resp, ItemVersionVO.class);

          return CommonUtils.convertToPubItemVOPresentationList(pubItemList);
        } else {
          this.numberOfRecords = 0;
          return new ArrayList<>();
        }
      }
    } catch (final Exception e) {
      YearbookCandidatesRetrieverRequestBean.logger.error("Error in retrieving items", e);
      this.error("Error in retrieving items");
      this.numberOfRecords = 0;
    }

    return null;
  }
}
