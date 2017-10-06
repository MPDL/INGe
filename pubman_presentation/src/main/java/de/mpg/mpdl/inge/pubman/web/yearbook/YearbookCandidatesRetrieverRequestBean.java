package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.log4j.Logger;
import org.apache.lucene.search.BooleanQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.valueobjects.ItemRelationVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.SearchResultElement;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.OrganizationSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemResultVO;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.yearbook.YearbookItemSessionBean.YBWORKSPACE;
import de.mpg.mpdl.inge.search.SearchService;
import de.mpg.mpdl.inge.search.query.ItemContainerSearchResult;
import de.mpg.mpdl.inge.search.query.MetadataDateSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.LogicalOperator;
import de.mpg.mpdl.inge.search.query.MetadataSearchQuery;
import de.mpg.mpdl.inge.search.query.PlainCqlQuery;
import de.mpg.mpdl.inge.search.query.SearchQuery;
import de.mpg.mpdl.inge.search.query.SearchQuery.SortingOrder;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.util.PropertyReader;

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
public class YearbookCandidatesRetrieverRequestBean extends
    BaseListRetrieverRequestBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA> {
  private static final Logger logger = Logger
      .getLogger(YearbookCandidatesRetrieverRequestBean.class);

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
        FacesTools.getExternalContext().getRequestParameterMap()
            .get(YearbookCandidatesRetrieverRequestBean.parameterSelectedOrgUnit);
    if (orgUnit == null) {
      if (this.getYearbookCandidatesSessionBean().getSelectedOrgUnit() != null
          || this.getYearbookItemSessionBean().getYearbook() == null) {
        this.setSelectedOrgUnit(this.getYearbookCandidatesSessionBean().getSelectedOrgUnit());
      } else {
        this.setSelectedOrgUnit(this.getYearbookCandidatesSessionBean().getOrgUnitSelectItems()
            .get(0).getValue().toString());
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
    final YearbookItemSessionBean yisb =
        (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
    final List<String> selected = new ArrayList<String>();
    for (final PubItemVOPresentation item : ((PubItemListSessionBean) this
        .getBasePaginatorListSessionBean()).getSelectedItems()) {
      selected.add(item.getVersion().getObjectId());
    }
    yisb.addMembers(selected);
    this.getBasePaginatorListSessionBean().update();
    return "";
  }

  public String removeSelectedFromYearbook() {
    final YearbookItemSessionBean yisb =
        (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
    final List<String> selected = new ArrayList<String>();
    for (final PubItemVOPresentation item : ((PubItemListSessionBean) this
        .getBasePaginatorListSessionBean()).getSelectedItems()) {
      selected.add(item.getVersion().getObjectId());
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
    this.getBasePaginatorListSessionBean().getParameterMap()
        .put(YearbookCandidatesRetrieverRequestBean.parameterSelectedOrgUnit, selectedOrgUnit);
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

    // Contexts
    BoolQueryBuilder contextBoolQuery = QueryBuilders.boolQuery();
    nonCandidateBoolQuery.must(contextBoolQuery);
    for (String contextId : yisb.getYearbook().getContextIds()) {
      contextBoolQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_CONTEXT_OBJECT_ID,
          contextId));
    }


    nonCandidateBoolQuery.mustNot(YearbookUtils.getCandidateQuery());


    // Remove members
    if (yisb.getYearbook().getItemIds().size() > 0) {
      BoolQueryBuilder memberBoolQuery = QueryBuilders.boolQuery();
      nonCandidateBoolQuery.must(memberBoolQuery);
      for (String id : yisb.getYearbook().getItemIds()) {
        memberBoolQuery.mustNot(QueryBuilders.termQuery(
            PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, id));
      }
    }



    if (!this.getSelectedOrgUnit().toLowerCase().equals("all")) {
      List<String> orgWithChildren = new ArrayList<>();
      OrganizationSearchCriterion.fillWithChildOus(orgWithChildren, getSelectedOrgUnit());
      BoolQueryBuilder ouBoolQuery = QueryBuilders.boolQuery();
      nonCandidateBoolQuery.must(ouBoolQuery);
      for (String ouId : orgWithChildren) {
        ouBoolQuery.should(QueryBuilders.termQuery(
            PubItemServiceDbImpl.INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIER, ouId));
        ouBoolQuery.should(QueryBuilders.termQuery(
            PubItemServiceDbImpl.INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIER, ouId));
      }
    }

    return nonCandidateBoolQuery;

  }

  private BoolQueryBuilder getInvalidMembersQuery() throws Exception {

    final YearbookItemSessionBean yisb =
        (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");

    if (yisb.getInvalidItemMap().size() > 0) {

      BoolQueryBuilder bq = QueryBuilders.boolQuery();

      for (final YearbookInvalidItemRO item : yisb.getInvalidItemMap().values()) {
        bq.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID,
            item.getObjectId()));
      }


      return bq;



      /*
       * 
       * ItemValidating itemValidating = (ItemValidating) new
       * InitialContext().lookup(ItemValidating.SERVICE_NAME);
       * 
       * System.out.println("Validate " + pubItemList.size() + "items"); long start =
       * System.currentTimeMillis(); for(PubItemVO item : pubItemList) { PubItemVO pubitem = new
       * PubItemVO(item);
       * 
       * long startSingle=System.currentTimeMillis(); ValidationReportVO report =
       * itemValidating.validateItemObject(pubitem); long stopSingle=System.currentTimeMillis();
       * System.out.println(item.getVersion().getObjectId()+ " took " + (stopSingle-startSingle) +
       * "ms"); } long stop = System.currentTimeMillis();
       * 
       * System.out.println("All " + pubItemList.size() +" took " + (stop-start) + "ms");
       */
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
            List<String> orgWithChildren = new ArrayList<>();
            OrganizationSearchCriterion.fillWithChildOus(orgWithChildren, getSelectedOrgUnit());
            BoolQueryBuilder ouBoolQuery = QueryBuilders.boolQuery();
            query.must(ouBoolQuery);
            for(String ouId : orgWithChildren)
            {
              ouBoolQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIER, ouId));
              ouBoolQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIER, ouId));
            }
          }
          
          
          
          

          /*
          if (sc.getIndex() != null) {
            query.setSortKeys(sc.getIndex());
          }

          if (sc.getIndex() == null || !sc.getIndex().equals("")) {
            if (sc.getSortOrder().equals(OrderFilter.ORDER_DESCENDING)) {

              query.setSortOrder(SortingOrder.DESCENDING);
            }

            else {
              query.setSortOrder(SortingOrder.ASCENDING);
            }
          }
          */
          
          
          
          //TODO Sorting!!!

          
          SearchSortCriteria ssc = new SearchSortCriteria(PubItemServiceDbImpl.INDEX_MODIFICATION_DATE, SortOrder.DESC);
          
          SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(query, limit, offset, ssc);
              
              
          SearchRetrieveResponseVO<PubItemVO> resp = ApplicationBean.INSTANCE.getPubItemService()
              .search(srr, null);

          this.numberOfRecords = resp.getNumberOfRecords();

          List<PubItemVO> resultList = resp.getRecords().stream().map(SearchRetrieveRecordVO::getData)
              .collect(Collectors.toList());

          return CommonUtils.convertToPubItemVOPresentationList(resultList);
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
