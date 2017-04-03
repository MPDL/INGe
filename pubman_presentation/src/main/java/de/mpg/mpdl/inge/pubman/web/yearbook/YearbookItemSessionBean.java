package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ContextHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemRelationVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.TaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.JiBXHelper;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.search.SearchRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.search.SearchService;
import de.mpg.mpdl.inge.search.query.ItemContainerSearchResult;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.LogicalOperator;
import de.mpg.mpdl.inge.search.query.MetadataSearchQuery;
import de.mpg.mpdl.inge.search.query.PlainCqlQuery;
import de.mpg.mpdl.inge.util.PropertyReader;

@ManagedBean(name = "YearbookItemSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class YearbookItemSessionBean extends FacesBean {
  enum YBWORKSPACE {
    CANDIDATES, MEMBERS, INVALID, NON_CANDIDATES
  }

  private static final Logger logger = Logger.getLogger(YearbookItemSessionBean.class);

  private final String MAXIMUM_RECORDS = "5000";

  private YBWORKSPACE selectedWorkspace;
  private PubItemVO yearbookItem;
  private ItemHandler itemHandler;
  private ContextVO yearbookContext;

  private PubItemListSessionBean pilsb;
  private Map<String, YearbookInvalidItemRO> invalidItemMap =
      new HashMap<String, YearbookInvalidItemRO>();
  private final Map<String, YearbookInvalidItemRO> validItemMap =
      new HashMap<String, YearbookInvalidItemRO>();

  public YearbookItemSessionBean() {
    try {
      this.pilsb = (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
      this.itemHandler =
          ServiceLocator.getItemHandler(this.getLoginHelper().getESciDocUserHandle());
      this.selectedWorkspace = YBWORKSPACE.CANDIDATES;
    } catch (final Exception e) {
      FacesBean.error("Error retrieving yearbook item!");
      YearbookItemSessionBean.logger.error("Error retrieving yearbook item!", e);
    }
  }

  @PostConstruct
  public void postConstruct() {
    try {
      if (this.getLoginHelper().getIsYearbookEditor()) {
        this.initYearbook();
      }
    } catch (final Exception e) {
      FacesBean.error("Error initializing yearbook item!");
      YearbookItemSessionBean.logger.error("Error initializing yearbook item!", e);
    }
  }

  public void initYearbook() throws Exception {
    this.setYearbookItem(null);
    final HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
    filterParams.put("operation", new String[] {"searchRetrieve"});
    filterParams.put("version", new String[] {"1.1"});
    final String orgId =
        this.getLoginHelper().getAccountUsersAffiliations().get(0).getReference().getObjectId();
    // String orgId = "escidoc:persistent25";
    filterParams.put(
        "query",
        new String[] {"\"/properties/context/id\"="
            + PropertyReader.getProperty("escidoc.pubman.yearbook.context.id")
            + " and \"/md-records/md-record/yearbook/creator/organization/identifier\"=" + orgId});
    filterParams.put("maximumRecords", new String[] {this.MAXIMUM_RECORDS});
    final String xmlItemList = this.itemHandler.retrieveItems(filterParams);
    final SearchRetrieveResponseVO<PubItemVO> result =
        XmlTransformingService.transformToSearchRetrieveResponse(xmlItemList);
    // set current yearbook if already existent
    if (result.getNumberOfRecords() > 0) {
      PubItemVO yearbookPubItem = null;
      final SimpleDateFormat calendarFormat = new SimpleDateFormat("yyyy");
      final Calendar calendar = Calendar.getInstance();
      final String year = calendarFormat.format(calendar.getTime());
      for (final SearchRetrieveRecordVO<PubItemVO> yearbookRecord : result.getRecords()) {
        yearbookPubItem = (PubItemVO) yearbookRecord.getData();
        if (yearbookPubItem != null && yearbookPubItem.getYearbookMetadata() != null) {
          if (yearbookPubItem.getYearbookMetadata().getYear() != null
              && (yearbookPubItem.getYearbookMetadata().getYear().equals(year) || yearbookPubItem
                  .getYearbookMetadata().getYear()
                  .equals(Integer.toString((Integer.valueOf(year) - 1))))
              && !yearbookPubItem.getPublicStatus().equals(State.RELEASED)) {
            this.setYearbookItem(yearbookPubItem);
            final ContextHandler contextHandler =
                ServiceLocator.getContextHandler(this.getLoginHelper().getESciDocUserHandle());
            final String contextXml =
                contextHandler.retrieve(this.getYearbookItem().getContext().getObjectId());
            this.yearbookContext = XmlTransformingService.transformToContext(contextXml);
          }
        }
      }
    }
  }

  public void setYearbookItem(PubItemVO yearbookItem) {
    this.yearbookItem = yearbookItem;
  }

  public PubItemVO getYearbookItem() {
    return this.yearbookItem;
  }

  public int getNumberOfMembers() {
    if (this.yearbookItem != null && this.yearbookItem.getRelations() != null) {
      return this.yearbookItem.getRelations().size();
    } else {
      return 0;
    }
  }

  public void addMembers(List<ItemRO> itemIds) {
    try {
      final List<ItemRO> newRels = new ArrayList<ItemRO>();
      final List<String> currentRelations = new ArrayList<String>();
      for (final ItemRelationVO rel : this.yearbookItem.getRelations()) {
        currentRelations.add(rel.getTargetItemRef().getObjectId());
      }
      for (final ItemRO id : itemIds) {
        if (currentRelations.contains(id.getObjectId())) {
          this.warn(this.getMessage("Yearbook_ItemAlreadyInYearbook"));
        } else {
          newRels.add(id);
        }
      }
      this.addRelations(newRels);
      this.info(this.getMessage("Yearbook_AddedItemsToYearbook"));
    } catch (final Exception e) {
      FacesBean.error(this.getMessage("Yearbook_ErrorAddingMembers"));
      YearbookItemSessionBean.logger.error("Error adding members to yearbook", e);
    }
  }

  public void removeMembers(List<ItemRO> itemIds) {
    try {
      for (final ItemRO item : itemIds) {
        if (this.invalidItemMap.containsKey(item.getObjectId())) {
          this.invalidItemMap.remove(item.getObjectId());
        }
        if (this.validItemMap.containsKey(item.getObjectId())) {
          this.validItemMap.remove(item.getObjectId());
        }
      }
      this.removeRelations(itemIds);
      this.info(this.getMessage("Yearbook_RemovedItemsFromYearbook"));
    } catch (final Exception e) {
      FacesBean.error(this.getMessage("Yearbook_ErrorRemovingMembers"));
      YearbookItemSessionBean.logger.error("Error removing members from yearbook", e);
    }
  }

  private void addRelations(List<ItemRO> relList) throws Exception {
    if (relList.size() > 0) {
      String updatedItemXml =
          this.itemHandler.retrieve(this.yearbookItem.getVersion().getObjectId());
      this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
      final String taskParam =
          YearbookItemSessionBean.createRelationTaskParam(relList,
              this.yearbookItem.getModificationDate());
      this.itemHandler.addContentRelations(this.yearbookItem.getVersion().getObjectId(), taskParam);
      updatedItemXml = this.itemHandler.retrieve(this.yearbookItem.getVersion().getObjectId());
      this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
    }
  }

  private void removeRelations(List<ItemRO> relList) throws Exception {
    if (relList.size() > 0) {
      String updatedItemXml =
          this.itemHandler.retrieve(this.yearbookItem.getVersion().getObjectId());
      this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
      final String taskParam =
          YearbookItemSessionBean.createRelationTaskParam(relList,
              this.yearbookItem.getModificationDate());
      this.itemHandler.removeContentRelations(this.yearbookItem.getVersion().getObjectId(),
          taskParam);
      updatedItemXml = this.itemHandler.retrieve(this.yearbookItem.getVersion().getObjectId());
      this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
    }
  }

  private static String createRelationTaskParam(List<ItemRO> relList, Date lmd) {
    String filter = "<param last-modification-date=\"" + JiBXHelper.serializeDate(lmd) + "\">";
    for (final ItemRO rel : relList) {
      filter +=
          "<relation><targetId>"
              + rel.getObjectId()
              + "</targetId><predicate>http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasMember</predicate></relation>";
    }
    filter += "</param>";
    return filter;
  }

  public void setYearbookContext(ContextVO yearbookContext) {
    this.yearbookContext = yearbookContext;
  }

  public ContextVO getYearbookContext() {
    return this.yearbookContext;
  }

  public boolean isCandidate(String id) throws Exception {
    String cqlQuery = YearbookCandidatesRetrieverRequestBean.getCandidateQuery().getCqlQuery();
    cqlQuery += " AND " + MetadataSearchCriterion.getINDEX_OBJID() + "=\"" + id + "\"";
    final ItemContainerSearchResult result =
        SearchService.searchForItemContainer(new PlainCqlQuery(cqlQuery));
    return result.getTotalNumberOfResults().shortValue() == 1;
  }

  public boolean isMember(String id) throws Exception {
    final MetadataSearchQuery mdQuery =
        YearbookCandidatesRetrieverRequestBean.getMemberQuery(this.getYearbookItem());
    mdQuery.addCriterion(new MetadataSearchCriterion(CriterionType.OBJID, id, LogicalOperator.AND));
    final ItemContainerSearchResult result = SearchService.searchForItemContainer(mdQuery);
    return result.getTotalNumberOfResults().shortValue() == 1;
  }

  public List<PubItemVOPresentation> retrieveAllMembers() throws Exception {
    List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    final MetadataSearchQuery mdQuery =
        YearbookCandidatesRetrieverRequestBean.getMemberQuery(this.getYearbookItem());
    final ItemContainerSearchResult result = SearchService.searchForItemContainer(mdQuery);
    pubItemList = SearchRetrieverRequestBean.extractItemsOfSearchResult(result);
    return pubItemList;
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
      final ValidationReportVO rep = new ValidationReportVO();
      // ValidationReportVO rep =
      // this.itemValidating.validateItemObject(new PubItemVO(pubItem), ValidationPoint.DEFAULT);
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
          FacesBean.error(this.getMessage("Yearbook_ItemInvalid").replaceAll("\\$1",
              "\"" + pubItem.getMetadata().getTitle() + "\""));
          allValid = false;
        }
      }
      if (!allValid) {
        FacesBean.error(this.getMessage("Yearbook_SubmitError"));
      } else {
        final TaskParamVO param =
            new TaskParamVO(this.getYearbookItem().getModificationDate(), "Submitting yearbook");
        final String paramXml = XmlTransformingService.transformToTaskParam(param);
        this.itemHandler.submit(this.getYearbookItem().getVersion().getObjectId(), paramXml);
        this.info(this.getMessage("Yearbook_SubmittedSuccessfully"));
      }
    } catch (final Exception e) {
      FacesBean.error(this.getMessage("Yearbook_SubmitError"));
      YearbookItemSessionBean.logger.error("Could not submit Yearbook Item", e);
    }
    try {
      final String yearbookXml =
          this.itemHandler.retrieve(this.getYearbookItem().getVersion().getObjectId());
      this.setYearbookItem(XmlTransformingService.transformToPubItem(yearbookXml));
    } catch (final Exception e) {
      FacesBean.error(this.getMessage("Yearbook_reinitializeError"));
      YearbookItemSessionBean.logger.error("Could not reinitialize Yearbook", e);
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
      List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();

      String query = "";
      final PubItemVO item = this.getYearbookItem();

      if (item.getRelations() != null && item.getRelations().size() > 0) {
        query = YearbookCandidatesRetrieverRequestBean.getMemberQuery(item).getCqlQuery();
      }

      final ItemContainerSearchResult result =
          SearchService.searchForItemContainer(new PlainCqlQuery(query));

      pubItemList = SearchRetrieverRequestBean.extractItemsOfSearchResult(result);
      this.pilsb.downloadExportFile(pubItemList);

    } catch (final Exception e) {
      FacesBean.error("Error while exporting");
      YearbookItemSessionBean.logger.error("Error exporting yearbook", e);
    }

    return "";
  }
}
