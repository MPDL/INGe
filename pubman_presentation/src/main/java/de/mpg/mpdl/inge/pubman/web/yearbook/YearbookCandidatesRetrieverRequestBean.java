package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.mpdl.inge.model.valueobjects.ItemRelationVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemResultVO;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.SearchResultElement;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
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
  public void init() {}

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
          || this.getYearbookItemSessionBean().getYearbookItem() == null) {
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
    final List<ItemRO> selected = new ArrayList<ItemRO>();
    for (final PubItemVOPresentation item : ((PubItemListSessionBean) this
        .getBasePaginatorListSessionBean()).getSelectedItems()) {
      selected.add(item.getVersion());
    }
    yisb.addMembers(selected);
    this.getBasePaginatorListSessionBean().update();
    return "";
  }

  public String removeSelectedFromYearbook() {
    final YearbookItemSessionBean yisb =
        (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
    final List<ItemRO> selected = new ArrayList<ItemRO>();
    for (final PubItemVOPresentation item : ((PubItemListSessionBean) this
        .getBasePaginatorListSessionBean()).getSelectedItems()) {
      selected.add(item.getVersion());
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

  private SearchQuery getCandidatesQuery() throws Exception {
    String query = YearbookCandidatesRetrieverRequestBean.getCandidateQuery().getCqlQuery();
    if (this.getSelectedOrgUnit() != null && !this.getSelectedOrgUnit().toLowerCase().equals("all")) {
      query +=
          " AND " + MetadataSearchCriterion.getINDEX_ORGANIZATION_PIDS() + "=\""
              + this.getSelectedOrgUnit() + "\"";
      // mdQuery.addCriterion(new MetadataSearchCriterion(CriterionType.ORGANIZATION_PIDS,
      // getSelectedOrgUnit(), LogicalOperator.AND));
    }

    return new PlainCqlQuery(query);
  }

  public static SearchQuery getCandidateQuery() throws Exception {
    final YearbookItemSessionBean yisb =
        (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
    final ArrayList<String> contentTypes = new ArrayList<String>();
    final String contentTypeIdPublication =
        PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
    contentTypes.add(contentTypeIdPublication);

    final ArrayList<MetadataSearchCriterion> mdsList = new ArrayList<MetadataSearchCriterion>();
    final MetadataSearchCriterion objectTypeMds =
        new MetadataSearchCriterion(CriterionType.OBJECT_TYPE, "item", LogicalOperator.AND);
    mdsList.add(objectTypeMds);

    // MetadataSearchCriterion genremd = new MetadataSearchCriterion(CriterionType.ANY, );

    int i = 0;
    for (final Genre genre : yisb.getYearbookContext().getAdminDescriptor().getAllowedGenres()) {
      if (i == 0) {
        objectTypeMds.addSubCriteria(new MetadataSearchCriterion(CriterionType.GENRE, genre
            .getUri(), LogicalOperator.AND));
      } else {
        objectTypeMds.addSubCriteria(new MetadataSearchCriterion(CriterionType.GENRE, genre
            .getUri(), LogicalOperator.OR));
      }
      i++;
    }

    if (yisb.getNumberOfMembers() > 0) {
      for (final ItemRelationVO rel : yisb.getYearbookItem().getRelations()) {
        mdsList.add(new MetadataSearchCriterion(CriterionType.OBJID, rel.getTargetItemRef()
            .getObjectId(), LogicalOperator.NOT));
      }
    }
    final MetadataSearchQuery mdQuery = new MetadataSearchQuery(contentTypes, mdsList);



    // generate Dates search query
    // date query for date issued & published-online
    final ArrayList<CriterionType> dateTypeList = new ArrayList<CriterionType>();
    dateTypeList.add(CriterionType.DATE_ISSUED);
    dateTypeList.add(CriterionType.DATE_PUBLISHED_ONLINE);
    MetadataDateSearchCriterion mdDate =
        new MetadataDateSearchCriterion(dateTypeList, yisb.getYearbookItem().getYearbookMetadata()
            .getStartDate(), yisb.getYearbookItem().getYearbookMetadata().getEndDate());
    final String datequery1 = mdDate.generateCqlQuery();
    // date query for date accepted (only if publication is of type Thesis)
    dateTypeList.clear();
    dateTypeList.add(CriterionType.DATE_ACCEPTED);
    mdDate =
        new MetadataDateSearchCriterion(dateTypeList, yisb.getYearbookItem().getYearbookMetadata()
            .getStartDate(), yisb.getYearbookItem().getYearbookMetadata().getEndDate());
    final String datequery2 =
        "( " + mdDate.generateCqlQuery() + " ) AND " + MetadataSearchCriterion.getINDEX_GENRE()
            + "=\"http://purl.org/eprint/type/Thesis\"";
    final String datequery = "(( " + datequery1 + ") OR (" + datequery2 + " ))";



    final String orgIndex =
        MetadataSearchCriterion.getINDEX_ORGANIZATION_PIDS()
            + "=\""
            + yisb.getYearbookItem().getYearbookMetadata().getCreators().get(0).getOrganization()
                .getIdentifier() + "\"";
    final String orgQuery = "( " + orgIndex + " )";
    // context query
    String contextQuery = "";
    if (yisb.getYearbookItem().getYearbookMetadata().getIncludedContexts() != null
        && yisb.getYearbookItem().getYearbookMetadata().getIncludedContexts().size() > 0) {
      contextQuery += "(";
      int k = 0;
      for (final String contextId : yisb.getYearbookItem().getYearbookMetadata()
          .getIncludedContexts()) {
        if (!contextId.trim().equals("")) {
          if (k != 0) {
            contextQuery += " OR";
          }
          final String context =
              " " + MetadataSearchCriterion.getINDEX_CONTEXT_OBJECTID() + "=\"" + contextId.trim()
                  + "\"";
          contextQuery += context;
          k++;
        }
      }
      contextQuery += " )";
    }
    final String additionalQuery = datequery + " AND " + orgQuery + " AND " + contextQuery;


    // String additionalQuery = yisb.getYearbookItem().getLocalTags().get(0);
    final PlainCqlQuery query =
        new PlainCqlQuery(mdQuery.getCqlQuery() + " AND " + additionalQuery);

    return query;
  }

  private SearchQuery getNonCandidatesQuery() throws Exception {
    String contextQuery = "";
    final YearbookItemSessionBean yisb = this.getYearbookItemSessionBean();
    if (yisb.getYearbookItem().getYearbookMetadata().getIncludedContexts() != null
        && yisb.getYearbookItem().getYearbookMetadata().getIncludedContexts().size() > 0) {
      contextQuery += "(";
      int k = 0;
      for (final String contextId : yisb.getYearbookItem().getYearbookMetadata()
          .getIncludedContexts()) {
        if (!contextId.trim().equals("")) {
          if (k != 0) {
            contextQuery += " OR";
          }
          final String context =
              " " + MetadataSearchCriterion.getINDEX_CONTEXT_OBJECTID() + "=\"" + contextId.trim()
                  + "\"";
          contextQuery += context;
          k++;
        }
      }
      contextQuery += " )";
    }
    final String contentModel =
        MetadataSearchCriterion.getINDEX_CONTENT_TYPE() + "=\""
            + PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication")
            + "\"";
    final String objectType = MetadataSearchCriterion.getINDEX_OBJECT_TYPE() + "=\"item\"";

    // String orgUnitSelected = MetadataSearchCriterion.getINDEX_ORGANIZATION_PIDS() + "=\"" +
    // getSelectedOrgUnit() + "\"";

    String query =
        objectType + " AND " + contentModel + " AND (" + contextQuery + ") NOT ( "
            + this.getCandidatesQuery().getCqlQuery() + " )";

    // Remove the members
    if (yisb.getNumberOfMembers() > 0) {
      query += " NOT (" + MetadataSearchCriterion.getINDEX_OBJID() + " any ";
      for (final ItemRelationVO rel : yisb.getYearbookItem().getRelations()) {
        query += rel.getTargetItemRef().getObjectId() + " ";
      }
      query += ")";
    }


    // Add the selected org unit filter
    if (!this.getSelectedOrgUnit().toLowerCase().equals("all")) {
      query +=
          " AND " + MetadataSearchCriterion.getINDEX_ORGANIZATION_PIDS() + "=\""
              + this.getSelectedOrgUnit() + "\"";
    }
    return new PlainCqlQuery(query);
  }

  public static MetadataSearchQuery getMemberQuery(PubItemVO yearbookItem) throws Exception {
    final ArrayList<String> contentTypes = new ArrayList<String>();
    final String contentTypeIdPublication =
        PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
    contentTypes.add(contentTypeIdPublication);

    final ArrayList<MetadataSearchCriterion> mdsList = new ArrayList<MetadataSearchCriterion>();
    final MetadataSearchCriterion objectTypeMds =
        new MetadataSearchCriterion(CriterionType.OBJECT_TYPE, "item", LogicalOperator.AND);
    mdsList.add(objectTypeMds);

    int i = 0;
    for (final ItemRelationVO rel : yearbookItem.getRelations()) {
      if (i == 0) {
        objectTypeMds.addSubCriteria(new MetadataSearchCriterion(CriterionType.OBJID, rel
            .getTargetItemRef().getObjectId(), LogicalOperator.AND));
      } else {
        objectTypeMds.addSubCriteria(new MetadataSearchCriterion(CriterionType.OBJID, rel
            .getTargetItemRef().getObjectId(), LogicalOperator.OR));
      }
      i++;

    }
    final MetadataSearchQuery mdQuery = new MetadataSearchQuery(contentTypes, mdsList);
    return mdQuery;
  }

  private SearchQuery getMembersQuery() throws Exception {
    final YearbookItemSessionBean yisb = this.getYearbookItemSessionBean();
    if (yisb.getNumberOfMembers() > 0) {
      final MetadataSearchQuery mdQuery =
          YearbookCandidatesRetrieverRequestBean.getMemberQuery(yisb.getYearbookItem());
      if (!this.getSelectedOrgUnit().toLowerCase().equals("all")) {
        mdQuery.addCriterion(new MetadataSearchCriterion(CriterionType.ORGANIZATION_PIDS, this
            .getSelectedOrgUnit(), LogicalOperator.AND));
      }
      return mdQuery;
    }

    return null;
  }

  private SearchQuery getInvalidMembersQuery() throws Exception {

    final YearbookItemSessionBean yisb =
        (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");

    if (yisb.getInvalidItemMap().size() > 0) {
      final ArrayList<String> contentTypes = new ArrayList<String>();
      final String contentTypeIdPublication =
          PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
      contentTypes.add(contentTypeIdPublication);

      final ArrayList<MetadataSearchCriterion> mdsList = new ArrayList<MetadataSearchCriterion>();
      final MetadataSearchCriterion objectTypeMds =
          new MetadataSearchCriterion(CriterionType.OBJECT_TYPE, "item", LogicalOperator.AND);
      mdsList.add(objectTypeMds);

      int i = 0;
      for (final YearbookInvalidItemRO item : yisb.getInvalidItemMap().values()) {
        if (i == 0) {
          objectTypeMds.addSubCriteria(new MetadataSearchCriterion(CriterionType.OBJID, item
              .getObjectId(), LogicalOperator.AND));
        } else {
          objectTypeMds.addSubCriteria(new MetadataSearchCriterion(CriterionType.OBJID, item
              .getObjectId(), LogicalOperator.OR));
        }
        i++;

      }

      final MetadataSearchQuery query = new MetadataSearchQuery(contentTypes, mdsList);
      return query;



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
    List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    final YearbookItemSessionBean yisb = this.getYearbookItemSessionBean();

    try {
      if (yisb.getYearbookItem() != null) {
        SearchQuery query = null;
        if (yisb.getSelectedWorkspace().equals(YBWORKSPACE.CANDIDATES)) {
          query = this.getCandidatesQuery();
        } else if (yisb.getSelectedWorkspace().equals(YBWORKSPACE.MEMBERS)) {
          query = this.getMembersQuery();
        } else if (yisb.getSelectedWorkspace().equals(YBWORKSPACE.INVALID)) {
          query = this.getInvalidMembersQuery();
        } else if (yisb.getSelectedWorkspace().equals(YBWORKSPACE.NON_CANDIDATES)) {
          query = this.getNonCandidatesQuery();
        }

        if (query != null) {
          query.setStartRecord(new PositiveInteger(String.valueOf(offset + 1)));
          query.setMaximumRecords(new NonNegativeInteger(String.valueOf(limit)));


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

          System.out.println(query.getCqlQuery());
          final ItemContainerSearchResult result = SearchService.searchForItemContainer(query);

          pubItemList = this.extractItemsOfSearchResult(result);
          this.numberOfRecords = Integer.parseInt(result.getTotalNumberOfResults().toString());
        }
      }
    } catch (final Exception e) {
      YearbookCandidatesRetrieverRequestBean.logger.error("Error in retrieving items", e);
      FacesBean.error("Error in retrieving items");
      this.numberOfRecords = 0;
    }

    return pubItemList;
  }


  public ArrayList<PubItemVOPresentation> extractItemsOfSearchResult(
      ItemContainerSearchResult result) {

    final List<SearchResultElement> results = result.getResultList();

    final ArrayList<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    for (int i = 0; i < results.size(); i++) {
      // check if we have found an item
      if (results.get(i) instanceof ItemResultVO) {
        // cast to PubItemResultVO
        final ItemResultVO item = (ItemResultVO) results.get(i);
        final PubItemResultVO pubItemResult =
            new PubItemResultVO(item, item.getSearchHitList(), item.getScore());
        final PubItemVOPresentation pubItemPres = new PubItemVOPresentation(pubItemResult);

        final YearbookItemSessionBean yisb = this.getYearbookItemSessionBean();
        if (yisb.getInvalidItemMap().containsKey(pubItemPres.getVersion().getObjectId())) {
          final YearbookInvalidItemRO itemRO =
              yisb.getInvalidItemMap().get(pubItemPres.getVersion().getObjectId());
          pubItemPres.setValidationReport(itemRO.getValidationReport());
        }
        pubItemList.add(pubItemPres);
      }
    }

    return pubItemList;
  }
}
