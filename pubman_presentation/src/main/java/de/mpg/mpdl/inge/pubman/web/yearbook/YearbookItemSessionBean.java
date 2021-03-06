package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.YearbookService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.pubman.impl.YearbookServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;

@ManagedBean(name = "YearbookItemSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class YearbookItemSessionBean extends FacesBean {
  enum YBWORKSPACE
  {
    CANDIDATES, MEMBERS, INVALID, NON_CANDIDATES
  }

  private static final Logger logger = Logger.getLogger(YearbookItemSessionBean.class);

  private YBWORKSPACE selectedWorkspace;

  private YearbookDbVO yearbook;


  private PubItemListSessionBean pilsb;
  private Map<String, YearbookInvalidItemRO> invalidItemMap = new HashMap<String, YearbookInvalidItemRO>();
  private Map<String, YearbookInvalidItemRO> validItemMap = new HashMap<String, YearbookInvalidItemRO>();

  private final YearbookService yearbookService = ApplicationBean.INSTANCE.getYearbookService();
  private final OrganizationService organizationService = ApplicationBean.INSTANCE.getOrganizationService();
  private final ItemValidatingService itemValidatingService = ApplicationBean.INSTANCE.getItemValidatingService();
  private final PubItemService pubItemService = ApplicationBean.INSTANCE.getPubItemService();

  public YearbookItemSessionBean() {
    try {
      this.pilsb = (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
      this.selectedWorkspace = YBWORKSPACE.CANDIDATES;
    } catch (final Exception e) {
      this.error("Error retrieving yearbook item!");
      YearbookItemSessionBean.logger.error("Error retrieving yearbook item!", e);
    }
  }

  @PostConstruct
  public void postConstruct() {
    if (this.getLoginHelper().getIsYearbookEditor()) {
      this.initYearbook(null);
    }
  }

  public void initYearbook(String id) {
    try {
      this.setYearbook(null);

      if (id == null) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> orgIds = YearbookUtils.getYearbookOrganizationIds(this.getLoginHelper().getAccountUser());
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();

        bqb.must(QueryBuilders.termQuery(YearbookServiceDbImpl.INDEX_ORGANIZATION_ID, orgIds.get(0)));
        bqb.must(QueryBuilders.termQuery(YearbookServiceDbImpl.INDEX_YEAR, currentYear));

        SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(bqb);
        SearchRetrieveResponseVO<YearbookDbVO> resp =
            ApplicationBean.INSTANCE.getYearbookService().search(srr, getLoginHelper().getAuthenticationToken());

        List<YearbookDbVO> yearbooks = resp.getRecords().stream().map(i -> i.getData()).collect(Collectors.toList());
        if (yearbooks.size() == 1) {
          id = yearbooks.get(0).getObjectId();
        }


        /*
         * if (yearbooks.size() == 1 && (yearbooks.get(0).getYear() == currentYear ||
         * yearbooks.get(0).getYear() == currentYear - 1) &&
         * yearbooks.get(0).getState().equals(YearbookDbVO.State.CREATED)) {
         * this.setYearbook(yearbooks.get(0));
         * 
         * }
         * 
         */
      }


      if (id != null) {
        YearbookDbVO yb = ApplicationBean.INSTANCE.getYearbookService().get(id, getLoginHelper().getAuthenticationToken());
        if (yb == null) {
          throw new IngeApplicationException("Yearbook with id " + id + " not found");
        }
        this.setYearbook(yb);
        if (!YearbookDbVO.State.CREATED.equals(this.yearbook.getState())) {
          this.selectedWorkspace = YBWORKSPACE.MEMBERS;
        }
      }



    } catch (final Exception e) {
      this.error(this.getMessage("YearBook_errorInitialize"));
      YearbookItemSessionBean.logger.error("Error initializing yearbook item!", e);
    }
  }

  public void setYearbook(YearbookDbVO yearbookItem) {
    this.yearbook = yearbookItem;
  }

  public YearbookDbVO getYearbook() {
    return this.yearbook;
  }

  public int getNumberOfMembers() {
    if (this.yearbook != null && this.yearbook.getItemIds() != null) {
      return this.yearbook.getItemIds().size();
    } else {
      return 0;
    }
  }

  public void addMembers(List<String> itemIds) {


    try {
      final List<String> newRels = new ArrayList<String>();

      for (final String id : itemIds) {
        if (yearbook.getItemIds().contains(id)) {
          this.warn(this.getMessage("Yearbook_ItemAlreadyInYearbook"));
        } else {
          newRels.add(id);
        }
      }
      this.addRelations(newRels);
      this.info(this.getMessage("Yearbook_AddedItemsToYearbook"));
    } catch (final Exception e) {
      this.error(this.getMessage("Yearbook_ErrorAddingMembers"));
      YearbookItemSessionBean.logger.error("Error adding members to yearbook", e);
    }
  }

  public void removeMembers(List<String> itemIds) {
    try {
      for (final String item : itemIds) {
        if (this.invalidItemMap.containsKey(item)) {
          this.invalidItemMap.remove(item);
        }
        if (this.validItemMap.containsKey(item)) {
          this.validItemMap.remove(item);
        }
      }
      this.removeRelations(itemIds);
      this.info(this.getMessage("Yearbook_RemovedItemsFromYearbook"));
    } catch (final Exception e) {
      this.error(this.getMessage("Yearbook_ErrorRemovingMembers"));
      YearbookItemSessionBean.logger.error("Error removing members from yearbook", e);
    }
  }

  private void addRelations(List<String> relList) throws Exception {

    YearbookDbVO retrievedYearbook = yearbookService.get(this.yearbook.getObjectId(), getLoginHelper().getAuthenticationToken());
    retrievedYearbook.getItemIds().addAll(relList);
    this.yearbook = yearbookService.update(retrievedYearbook, getLoginHelper().getAuthenticationToken());
    // if (relList.size() > 0) {
    // String updatedItemXml =
    // this.itemHandler.retrieve(this.yearbookItem.getObjectId());
    // this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
    // final String taskParam =
    // YearbookItemSessionBean.createRelationTaskParam(relList,
    // this.yearbookItem.getModificationDate());
    // this.itemHandler.addContentRelations(this.yearbookItem.getObjectId(),
    // taskParam);
    // updatedItemXml = this.itemHandler.retrieve(this.yearbookItem.getObjectId());
    // this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
    // }
  }

  private void removeRelations(List<String> relList) throws Exception {

    YearbookDbVO retrievedYearbook = yearbookService.get(this.yearbook.getObjectId(), getLoginHelper().getAuthenticationToken());
    retrievedYearbook.getItemIds().removeAll(relList);
    this.yearbook = yearbookService.update(retrievedYearbook, getLoginHelper().getAuthenticationToken());

    // if (relList.size() > 0) {
    // String updatedItemXml =
    // this.itemHandler.retrieve(this.yearbookItem.getObjectId());
    // this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
    // final String taskParam =
    // YearbookItemSessionBean.createRelationTaskParam(relList,
    // this.yearbookItem.getModificationDate());
    // this.itemHandler.removeContentRelations(this.yearbookItem.getObjectId(),
    // taskParam);
    // updatedItemXml = this.itemHandler.retrieve(this.yearbookItem.getObjectId());
    // this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
    // }
  }

  // private static String createRelationTaskParam(List<ItemRO> relList, Date lmd) {
  // String filter = "<param last-modification-date=\"" + JiBXHelper.serializeDate(lmd) + "\">";
  // for (final ItemRO rel : relList) {
  // filter +=
  // "<relation><targetId>"
  // + rel.getObjectId()
  // +
  // "</targetId><predicate>http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasMember</predicate></relation>";
  // }
  // filter += "</param>";
  // return filter;
  // }

  /*
   * public void setYearbookContext(ContextDbVO yearbookContext) { this.yearbookContext =
   * yearbookContext; }
   * 
   * public ContextDbVO getYearbookContext() { return this.yearbookContext; }
   */

  public boolean isCandidate(String id) throws Exception {

    BoolQueryBuilder qb = YearbookUtils.getCandidateQuery();
    qb.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, id));
    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, 0, 0); // Limit 0, da nur Gesamtzahl interessieert
    SearchRetrieveResponseVO<ItemVersionVO> resp = ApplicationBean.INSTANCE.getPubItemService().search(srr, null);
    return resp.getNumberOfRecords() > 0;
  }

  public boolean isMember(String id) throws Exception {

    return this.yearbook.getItemIds() != null && this.yearbook.getItemIds().contains(id);
    /*
     * final MetadataSearchQuery mdQuery =
     * YearbookCandidatesRetrieverRequestBean.getMemberQuery(this.getYearbookItem());
     * mdQuery.addCriterion(new MetadataSearchCriterion(CriterionType.OBJID, id,
     * LogicalOperator.AND)); final ItemContainerSearchResult result =
     * SearchService.searchForItemContainer(mdQuery); return
     * result.getTotalNumberOfResults().shortValue() == 1;
     */
  }

  public List<PubItemVOPresentation> retrieveAllMembers() throws Exception {
    return YearbookUtils.retrieveAllMembers(this.yearbook, getLoginHelper().getAuthenticationToken());
  }

  public boolean validateItem(ItemVersionVO pubItem) throws Exception {
    YearbookInvalidItemRO storedItem = null;
    for (final CreatorVO creator : pubItem.getMetadata().getCreators()) {
      if (creator.getOrganization() != null && creator.getOrganization().getIdentifier() != null) {
        creator.getOrganization().setIdentifier(creator.getOrganization().getIdentifier().trim());
      } else if (creator.getPerson() != null) {
        for (final OrganizationVO organization : creator.getPerson().getOrganizations()) {
          if (organization.getIdentifier() != null) {
            organization.setIdentifier(organization.getIdentifier().trim());
          }

        }
      }
    }
    if (this.invalidItemMap.containsKey(pubItem.getObjectId())) {
      storedItem = this.invalidItemMap.get(pubItem.getObjectId());
    } else if (this.validItemMap.containsKey(pubItem.getObjectId())) {
      storedItem = this.validItemMap.get(pubItem.getObjectId());
    }
    if (storedItem == null || !pubItem.getModificationDate().equals(storedItem.getLastModificationDate())) {
      // revalidate
      // TODO maybe a special validationpoint for the yearbook needs to be created
      ValidationReportVO rep = new ValidationReportVO();

      try {
        List<String> childsOfMPG = this.organizationService.getAllChildrenOfMpg();
        this.itemValidatingService.validateYearbook(new ItemVersionVO(pubItem), childsOfMPG);
      } catch (final ValidationException e) {
        rep = e.getReport();
      }
      if (rep.getItems().size() > 0) {
        this.validItemMap.remove(pubItem.getObjectId());
        this.invalidItemMap.put(pubItem.getObjectId(),
            new YearbookInvalidItemRO(pubItem.getObjectId(), rep, pubItem.getModificationDate()));
      } else {
        this.invalidItemMap.remove(pubItem.getObjectId());
        this.validItemMap.put(pubItem.getObjectId(), new YearbookInvalidItemRO(pubItem.getObjectId(), rep, pubItem.getModificationDate()));
      }
      return rep.isValid();
    }
    return storedItem.getValidationReport().isValid();
  }

  public String validateYearbook() throws Exception {
    this.validItemMap = new HashMap<String, YearbookInvalidItemRO>();
    this.invalidItemMap = new HashMap<String, YearbookInvalidItemRO>();

    final List<PubItemVOPresentation> pubItemList = this.retrieveAllMembers();
    for (final PubItemVOPresentation pubItem : pubItemList) {
      this.validateItem(pubItem);
    }
    if (this.invalidItemMap.size() == 0) {
      this.info(this.getMessage("Yearbook_allItemsValid"));
    }
    this.changeToInvalidItems();
    return "";
  }

  public void setInvalidItemMap(Map<String, YearbookInvalidItemRO> invalidItemMap) {
    this.invalidItemMap = invalidItemMap;
  }

  public Map<String, YearbookInvalidItemRO> getInvalidItemMap() {
    return this.invalidItemMap;
  }

  public void setSelectedWorkspace(YBWORKSPACE selectedWorkspace) {
    this.selectedWorkspace = selectedWorkspace;
  }

  public YBWORKSPACE getSelectedWorkspace() {
    return this.selectedWorkspace;
  }

  public String changeToCandidates() {
    final PubItemListSessionBean pilsb = (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    this.setSelectedWorkspace(YBWORKSPACE.CANDIDATES);
    pilsb.setCurrentPageNumber(1);
    pilsb.redirect();

    return "";
  }

  public String changeToMembers() {
    final PubItemListSessionBean pilsb = (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    this.setSelectedWorkspace(YBWORKSPACE.MEMBERS);
    pilsb.setCurrentPageNumber(1);
    pilsb.redirect();

    return "";
  }

  public String changeToInvalidItems() {
    final PubItemListSessionBean pilsb = (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    this.setSelectedWorkspace(YBWORKSPACE.INVALID);
    pilsb.setCurrentPageNumber(1);
    pilsb.redirect();

    return "";
  }

  public String changeToNonCandidates() {
    final PubItemListSessionBean pilsb = (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    this.setSelectedWorkspace(YBWORKSPACE.NON_CANDIDATES);
    pilsb.setCurrentPageNumber(1);
    pilsb.redirect();

    return "";
  }

  public String submitYearbook() {
    try {
      final List<PubItemVOPresentation> pubItemList = this.retrieveAllMembers();
      boolean allValid = true;
      for (final PubItemVOPresentation pubItem : pubItemList) {
        final boolean valid = this.validateItem(pubItem);
        if (!valid) {
          final YearbookInvalidItemRO invItem = this.invalidItemMap.get(pubItem.getObjectId());
          for (ValidationReportItemVO validationReportItemVO : invItem.getValidationReport().getItems()) {
            if (validationReportItemVO.getSeverity() == ValidationReportItemVO.Severity.ERROR) {
              this.error(this.getMessage("Yearbook_ItemInvalid").replaceAll("\\$1", "\"" + pubItem.getMetadata().getTitle() + "\""));
              allValid = false;
            }
          }
        }
      }

      if (!allValid) {
        this.error(this.getMessage("Yearbook_SubmitError"));
      } else {
        YearbookDbVO updatedYb = ApplicationBean.INSTANCE.getYearbookService().submit(this.yearbook.getObjectId(),
            this.yearbook.getLastModificationDate(), getLoginHelper().getAuthenticationToken());
        this.setYearbook(updatedYb);
        this.info(this.getMessage("Yearbook_SubmittedSuccessfully"));

        return "loadYearbookModeratorPage";
      }
    } catch (final Exception e) {
      this.error(this.getMessage("Yearbook_SubmitError"));
      YearbookItemSessionBean.logger.error("Could not submit Yearbook Item", e);
    }

    return "";
  }

  /**
   * exports the yearbook members
   * 
   * @return empty String for no page change
   */
  public String exportYearbook() {
    try {
      SearchSourceBuilder ssb = new SearchSourceBuilder();
      ssb.query(YearbookUtils.getMemberQuery(this.yearbook));
      ssb.from(0);
      ssb.size(5000);

      PubItemListSessionBean.SORT_CRITERIA sc = this.pilsb.getSortCriteria();
      for (String index : sc.getIndex()) {
        if (!index.isEmpty()) {
          ssb.sort(SearchUtils.baseElasticSearchSortBuilder(this.pubItemService.getElasticSearchIndexFields(), index,
              SortOrder.ASC.equals(sc.getSortOrder()) ? org.elasticsearch.search.sort.SortOrder.ASC
                  : org.elasticsearch.search.sort.SortOrder.DESC));
        }
      }

      SearchResponse resp = this.pubItemService.searchDetailed(ssb, null);

      List<ItemVersionVO> pubItemList = SearchUtils.getRecordListFromElasticSearchResponse(resp, ItemVersionVO.class);
      List<PubItemVOPresentation> resultList = new ArrayList<>();

      for (ItemVersionVO pubItem : pubItemList) {
        PubItemVOPresentation pubItemPres = new PubItemVOPresentation(pubItem);
        resultList.add(pubItemPres);
      }

      this.pilsb.downloadExportFile(resultList);
    } catch (final Exception e) {
      this.error(this.getMessage("ExportError") + e.getMessage());
      YearbookItemSessionBean.logger.error("Error exporting yearbook", e);
    }

    return "";
  }

}
