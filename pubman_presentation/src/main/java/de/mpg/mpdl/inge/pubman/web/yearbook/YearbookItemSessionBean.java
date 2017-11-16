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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.YearbookService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.pubman.impl.YearbookServiceDbImpl;
import de.mpg.mpdl.inge.util.PropertyReader;

@ManagedBean(name = "YearbookItemSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class YearbookItemSessionBean extends FacesBean {
  enum YBWORKSPACE {
    CANDIDATES, MEMBERS, INVALID, NON_CANDIDATES
  }

  private static final Logger logger = Logger.getLogger(YearbookItemSessionBean.class);

  private YBWORKSPACE selectedWorkspace;

  private YearbookDbVO yearbook;

  private YearbookDbVO yearbookForView;

  private PubItemListSessionBean pilsb;
  private Map<String, YearbookInvalidItemRO> invalidItemMap =
      new HashMap<String, YearbookInvalidItemRO>();
  private final Map<String, YearbookInvalidItemRO> validItemMap =
      new HashMap<String, YearbookInvalidItemRO>();

  private final YearbookService yearbookService = ApplicationBean.INSTANCE.getYearbookService();
  private final OrganizationService organizationService = ApplicationBean.INSTANCE
      .getOrganizationService();
  private final ItemValidatingService itemValidatingService = ApplicationBean.INSTANCE
      .getItemValidatingService();

  private final String mpgId = PropertyReader.getProperty("escidoc.pubman.root.organisation.id");

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
        List<String> orgIds =
            YearbookUtils.getYearbookOrganizationIds(this.getLoginHelper().getAccountUser());
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();

        bqb.must(QueryBuilders.termQuery(YearbookServiceDbImpl.INDEX_ORGANIZATION_ID, orgIds.get(0)));
        bqb.must(QueryBuilders.termQuery(YearbookServiceDbImpl.INDEX_YEAR, currentYear));

        SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(bqb);
        SearchRetrieveResponseVO<YearbookDbVO> resp = ApplicationBean.INSTANCE.getYearbookService()
            .search(srr, getLoginHelper().getAuthenticationToken());

        List<YearbookDbVO> yearbooks =
            resp.getRecords().stream().map(i -> i.getData()).collect(Collectors.toList());
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


      if(id!=null)
      {
        YearbookDbVO yb = ApplicationBean.INSTANCE.getYearbookService().get(id,
            getLoginHelper().getAuthenticationToken());
        System.out.println("Yearbook: " + yb.getObjectId() + " - " + yb.getLastModificationDate());
        this.setYearbook(yb);
      }


    } catch (final Exception e) {
      this.error("Error initializing yearbook item!");
      YearbookItemSessionBean.logger.error("Error initializing yearbook item!", e);
    }

    /*
     * 
     * final HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
     * filterParams.put("operation", new String[] {"searchRetrieve"}); filterParams.put("version",
     * new String[] {"1.1"}); final String orgId =
     * this.getLoginHelper().getAccountUsersAffiliations().get(0).getReference().getObjectId(); //
     * String orgId = "escidoc:persistent25"; filterParams.put( "query", new String[]
     * {"\"/properties/context/id\"=" +
     * PropertyReader.getProperty("escidoc.pubman.yearbook.context.id") +
     * " and \"/md-records/md-record/yearbook/creator/organization/identifier\"=" + orgId});
     * filterParams.put("maximumRecords", new String[] {this.MAXIMUM_RECORDS}); final String
     * xmlItemList = this.itemHandler.retrieveItems(filterParams); final
     * SearchRetrieveResponseVO<PubItemVO> result =
     * XmlTransformingService.transformToSearchRetrieveResponse(xmlItemList); set current yearbook
     * if already existent if (result.getNumberOfRecords() > 0) { PubItemVO yearbookPubItem = null;
     * final SimpleDateFormat calendarFormat = new SimpleDateFormat("yyyy"); final Calendar calendar
     * = Calendar.getInstance(); final String year = calendarFormat.format(calendar.getTime()); for
     * (final SearchRetrieveRecordVO<PubItemVO> yearbookRecord : result.getRecords()) {
     * yearbookPubItem = (PubItemVO) yearbookRecord.getData(); if (yearbookPubItem != null &&
     * yearbookPubItem.getYearbookMetadata() != null) { if
     * (yearbookPubItem.getYearbookMetadata().getYear() != null &&
     * (yearbookPubItem.getYearbookMetadata().getYear().equals(year) || yearbookPubItem
     * .getYearbookMetadata().getYear() .equals(Integer.toString((Integer.valueOf(year) - 1)))) &&
     * !yearbookPubItem.getPublicStatus().equals(State.RELEASED)) {
     * this.setYearbookItem(yearbookPubItem); final ContextHandler contextHandler =
     * ServiceLocator.getContextHandler(this.getLoginHelper().getESciDocUserHandle()); final String
     * contextXml = contextHandler.retrieve(this.getYearbookItem().getContext().getObjectId());
     * this.yearbookContext = XmlTransformingService.transformToContext(contextXml); } } } }
     */
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

    YearbookDbVO retrievedYearbook =
        yearbookService.get(this.yearbook.getObjectId(), getLoginHelper().getAuthenticationToken());
    retrievedYearbook.getItemIds().addAll(relList);
    this.yearbook =
        yearbookService.update(retrievedYearbook, getLoginHelper().getAuthenticationToken());
    // if (relList.size() > 0) {
    // String updatedItemXml =
    // this.itemHandler.retrieve(this.yearbookItem.getVersion().getObjectId());
    // this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
    // final String taskParam =
    // YearbookItemSessionBean.createRelationTaskParam(relList,
    // this.yearbookItem.getModificationDate());
    // this.itemHandler.addContentRelations(this.yearbookItem.getVersion().getObjectId(),
    // taskParam);
    // updatedItemXml = this.itemHandler.retrieve(this.yearbookItem.getVersion().getObjectId());
    // this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
    // }
  }

  private void removeRelations(List<String> relList) throws Exception {

    YearbookDbVO retrievedYearbook =
        yearbookService.get(this.yearbook.getObjectId(), getLoginHelper().getAuthenticationToken());
    retrievedYearbook.getItemIds().removeAll(relList);
    this.yearbook =
        yearbookService.update(retrievedYearbook, getLoginHelper().getAuthenticationToken());

    // if (relList.size() > 0) {
    // String updatedItemXml =
    // this.itemHandler.retrieve(this.yearbookItem.getVersion().getObjectId());
    // this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
    // final String taskParam =
    // YearbookItemSessionBean.createRelationTaskParam(relList,
    // this.yearbookItem.getModificationDate());
    // this.itemHandler.removeContentRelations(this.yearbookItem.getVersion().getObjectId(),
    // taskParam);
    // updatedItemXml = this.itemHandler.retrieve(this.yearbookItem.getVersion().getObjectId());
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
   * public void setYearbookContext(ContextVO yearbookContext) { this.yearbookContext =
   * yearbookContext; }
   * 
   * public ContextVO getYearbookContext() { return this.yearbookContext; }
   */

  public boolean isCandidate(String id) throws Exception {

    BoolQueryBuilder qb = YearbookUtils.getCandidateQuery();
    qb.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, id));
    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, 0, 0, null);
    SearchRetrieveResponseVO<PubItemVO> resp =
        ApplicationBean.INSTANCE.getPubItemService().search(srr, null);
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
    return YearbookUtils.retrieveAllMembers(yearbook, getLoginHelper().getAuthenticationToken());
  }

  public boolean validateItem(PubItemVO pubItem) throws Exception {
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
    if (this.invalidItemMap.containsKey(pubItem.getVersion().getObjectId())) {
      storedItem = this.invalidItemMap.get(pubItem.getVersion().getObjectId());
    } else if (this.validItemMap.containsKey(pubItem.getVersion().getObjectId())) {
      storedItem = this.validItemMap.get(pubItem.getVersion().getObjectId());
    }
    if (storedItem == null
        || !pubItem.getModificationDate().equals(storedItem.getLastModificationDate())) {
      // revalidate
      System.out.println("Yearbook Validating: " + pubItem.getVersion().getObjectId());
      // TODO maybe a special validationpoint for the yearbook needs to be created
      ValidationReportVO rep = new ValidationReportVO();

      try {
        // TODO: childsOfMPG in OrganizationService auslagern und dort evtl. cachen
        List<String> childsOfMPG = this.organizationService.getChildIdPath(mpgId);
        this.itemValidatingService.validateYearbook(new PubItemVO(pubItem), childsOfMPG);
      } catch (final ValidationException e) {
        rep = e.getReport();
      }
      if (rep.getItems().size() > 0) {
        this.validItemMap.remove(pubItem.getVersion().getObjectId());
        this.invalidItemMap.put(pubItem.getVersion().getObjectId(), new YearbookInvalidItemRO(
            pubItem.getVersion().getObjectId(), rep, pubItem.getModificationDate()));
      } else {
        this.invalidItemMap.remove(pubItem.getVersion().getObjectId());
        this.validItemMap.put(pubItem.getVersion().getObjectId(), new YearbookInvalidItemRO(pubItem
            .getVersion().getObjectId(), rep, pubItem.getModificationDate()));
      }
      return rep.isValid();
    }
    return storedItem.getValidationReport().isValid();
  }

  public String validateYearbook() throws Exception {
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
    final PubItemListSessionBean pilsb =
        (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    this.setSelectedWorkspace(YBWORKSPACE.CANDIDATES);
    pilsb.setCurrentPageNumber(1);
    pilsb.redirect();

    return "";
  }

  public String changeToMembers() {
    final PubItemListSessionBean pilsb =
        (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    this.setSelectedWorkspace(YBWORKSPACE.MEMBERS);
    pilsb.setCurrentPageNumber(1);
    pilsb.redirect();

    return "";
  }

  public String changeToInvalidItems() {
    final PubItemListSessionBean pilsb =
        (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    this.setSelectedWorkspace(YBWORKSPACE.INVALID);
    pilsb.setCurrentPageNumber(1);
    pilsb.redirect();

    return "";
  }

  public String changeToNonCandidates() {
    final PubItemListSessionBean pilsb =
        (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
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
          this.error(this.getMessage("Yearbook_ItemInvalid").replaceAll("\\$1",
              "\"" + pubItem.getMetadata().getTitle() + "\""));
          allValid = false;
        }
      }
      if (!allValid) {
        this.error(this.getMessage("Yearbook_SubmitError"));
      } else {

        YearbookDbVO updatedYb =
            ApplicationBean.INSTANCE.getYearbookService().submit(this.yearbook.getObjectId(),
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

      List<PubItemVOPresentation> result = retrieveAllMembers();

      this.pilsb.downloadExportFile(result);

    } catch (final Exception e) {
      this.error("Error while exporting");
      YearbookItemSessionBean.logger.error("Error exporting yearbook", e);
    }

    return "";
  }

  public YearbookDbVO getYearbookForView() {
    return yearbookForView;
  }

  public void setYearbookForView(YearbookDbVO yearbookForView) {
    this.yearbookForView = yearbookForView;
  }
}
