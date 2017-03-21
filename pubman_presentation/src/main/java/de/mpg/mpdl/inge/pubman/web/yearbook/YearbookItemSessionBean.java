package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

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

@SuppressWarnings("serial")
public class YearbookItemSessionBean extends FacesBean {
  enum YBWORKSPACE {
    CANDIDATES, MEMBERS, INVALID, NON_CANDIDATES
  }

  private static final Logger logger = Logger.getLogger(YearbookItemSessionBean.class);
  private final String MAXIMUM_RECORDS = "5000";
  public static final String BEAN_NAME = "YearbookItemSessionBean";

  private YBWORKSPACE selectedWorkspace;
  private PubItemVO yearbookItem;
  private ItemHandler itemHandler;
  private ContextVO yearbookContext;

  private PubItemListSessionBean pilsb;
  private Map<String, YearbookInvalidItemRO> invalidItemMap =
      new HashMap<String, YearbookInvalidItemRO>();
  private Map<String, YearbookInvalidItemRO> validItemMap =
      new HashMap<String, YearbookInvalidItemRO>();

  public YearbookItemSessionBean() {
    try {
      this.pilsb = (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
      this.itemHandler = ServiceLocator.getItemHandler(getLoginHelper().getESciDocUserHandle());
      this.selectedWorkspace = YBWORKSPACE.CANDIDATES;
    } catch (Exception e) {
      error("Error retrieving yearbook item!");
      logger.error("Error retrieving yearbook item!", e);
    }
  }

  @PostConstruct
  public void postConstruct() {
    try {
      if (getLoginHelper().getIsYearbookEditor()) {
        initYearbook();
      }
    } catch (Exception e) {
      error("Error initializing yearbook item!");
      logger.error("Error initializing yearbook item!", e);
    }
  }

  public void initYearbook() throws Exception {
    this.setYearbookItem(null);
    HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
    filterParams.put("operation", new String[] {"searchRetrieve"});
    filterParams.put("version", new String[] {"1.1"});
    String orgId =
        getLoginHelper().getAccountUsersAffiliations().get(0).getReference().getObjectId();
    // String orgId = "escidoc:persistent25";
    filterParams.put(
        "query",
        new String[] {"\"/properties/context/id\"="
            + PropertyReader.getProperty("escidoc.pubman.yearbook.context.id")
            + " and \"/md-records/md-record/yearbook/creator/organization/identifier\"=" + orgId});
    filterParams.put("maximumRecords", new String[] {this.MAXIMUM_RECORDS});
    String xmlItemList = itemHandler.retrieveItems(filterParams);
    SearchRetrieveResponseVO result =
        XmlTransformingService.transformToSearchRetrieveResponse(xmlItemList);
    // set current yearbook if already existent
    if (result.getNumberOfRecords() > 0) {
      PubItemVO yearbookPubItem = null;
      SimpleDateFormat calendarFormat = new SimpleDateFormat("yyyy");
      Calendar calendar = Calendar.getInstance();
      String year = calendarFormat.format(calendar.getTime());
      for (SearchRetrieveRecordVO yearbookRecord : result.getRecords()) {
        yearbookPubItem = (PubItemVO) yearbookRecord.getData();
        if (yearbookPubItem != null && yearbookPubItem.getYearbookMetadata() != null) {
          if (yearbookPubItem.getYearbookMetadata().getYear() != null
              && (yearbookPubItem.getYearbookMetadata().getYear().equals(year) || yearbookPubItem
                  .getYearbookMetadata().getYear()
                  .equals(Integer.toString((Integer.valueOf(year) - 1))))
              && !yearbookPubItem.getPublicStatus().equals(State.RELEASED)) {
            this.setYearbookItem(yearbookPubItem);
            ContextHandler contextHandler =
                ServiceLocator.getContextHandler(getLoginHelper().getESciDocUserHandle());
            String contextXml =
                contextHandler.retrieve(getYearbookItem().getContext().getObjectId());
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
    return yearbookItem;
  }

  public int getNumberOfMembers() {
    if (yearbookItem != null && yearbookItem.getRelations() != null) {
      return yearbookItem.getRelations().size();
    } else {
      return 0;
    }
  }

  public void addMembers(List<ItemRO> itemIds) {
    try {
      List<ItemRO> newRels = new ArrayList<ItemRO>();
      List<String> currentRelations = new ArrayList<String>();
      for (ItemRelationVO rel : yearbookItem.getRelations()) {
        currentRelations.add(rel.getTargetItemRef().getObjectId());
      }
      for (ItemRO id : itemIds) {
        if (currentRelations.contains(id.getObjectId())) {
          warn(getMessage("Yearbook_ItemAlreadyInYearbook"));
        } else {
          newRels.add(id);
        }
      }
      addRelations(newRels);
      info(getMessage("Yearbook_AddedItemsToYearbook"));
    } catch (Exception e) {
      error(getMessage("Yearbook_ErrorAddingMembers"));
      logger.error("Error adding members to yearbook", e);
    }
  }

  public void removeMembers(List<ItemRO> itemIds) {
    try {
      for (ItemRO item : itemIds) {
        if (invalidItemMap.containsKey(item.getObjectId())) {
          invalidItemMap.remove(item.getObjectId());
        }
        if (validItemMap.containsKey(item.getObjectId())) {
          validItemMap.remove(item.getObjectId());
        }
      }
      removeRelations(itemIds);
      info(getMessage("Yearbook_RemovedItemsFromYearbook"));
    } catch (Exception e) {
      error(getMessage("Yearbook_ErrorRemovingMembers"));
      logger.error("Error removing members from yearbook", e);
    }
  }

  private void addRelations(List<ItemRO> relList) throws Exception {
    if (relList.size() > 0) {
      String updatedItemXml = itemHandler.retrieve(yearbookItem.getVersion().getObjectId());
      this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
      String taskParam = createRelationTaskParam(relList, yearbookItem.getModificationDate());
      itemHandler.addContentRelations(yearbookItem.getVersion().getObjectId(), taskParam);
      updatedItemXml = itemHandler.retrieve(yearbookItem.getVersion().getObjectId());
      this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
    }
  }

  private void removeRelations(List<ItemRO> relList) throws Exception {
    if (relList.size() > 0) {
      String updatedItemXml = itemHandler.retrieve(yearbookItem.getVersion().getObjectId());
      this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
      String taskParam = createRelationTaskParam(relList, yearbookItem.getModificationDate());
      itemHandler.removeContentRelations(yearbookItem.getVersion().getObjectId(), taskParam);
      updatedItemXml = itemHandler.retrieve(yearbookItem.getVersion().getObjectId());
      this.yearbookItem = XmlTransformingService.transformToPubItem(updatedItemXml);
    }
  }

  private static String createRelationTaskParam(List<ItemRO> relList, Date lmd) {
    String filter = "<param last-modification-date=\"" + JiBXHelper.serializeDate(lmd) + "\">";
    for (ItemRO rel : relList) {
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
    return yearbookContext;
  }

  public boolean isCandidate(String id) throws Exception {
    String cqlQuery = YearbookCandidatesRetrieverRequestBean.getCandidateQuery().getCqlQuery();
    cqlQuery += " AND " + MetadataSearchCriterion.getINDEX_OBJID() + "=\"" + id + "\"";
    ItemContainerSearchResult result =
        SearchService.searchForItemContainer(new PlainCqlQuery(cqlQuery));
    return result.getTotalNumberOfResults().shortValue() == 1;
  }

  public boolean isMember(String id) throws Exception {
    MetadataSearchQuery mdQuery =
        YearbookCandidatesRetrieverRequestBean.getMemberQuery(getYearbookItem());
    mdQuery.addCriterion(new MetadataSearchCriterion(CriterionType.OBJID, id, LogicalOperator.AND));
    ItemContainerSearchResult result = SearchService.searchForItemContainer(mdQuery);
    return result.getTotalNumberOfResults().shortValue() == 1;
  }

  public List<PubItemVOPresentation> retrieveAllMembers() throws Exception {
    List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    MetadataSearchQuery mdQuery =
        YearbookCandidatesRetrieverRequestBean.getMemberQuery(getYearbookItem());
    ItemContainerSearchResult result = SearchService.searchForItemContainer(mdQuery);
    pubItemList = SearchRetrieverRequestBean.extractItemsOfSearchResult(result);
    return pubItemList;
  }

  public boolean validateItem(PubItemVO pubItem) throws Exception {
    YearbookInvalidItemRO storedItem = null;
    for (CreatorVO creator : pubItem.getMetadata().getCreators()) {
      if (creator.getOrganization() != null && creator.getOrganization().getIdentifier() != null) {
        creator.getOrganization().setIdentifier(creator.getOrganization().getIdentifier().trim());
      } else if (creator.getPerson() != null) {
        for (OrganizationVO organization : creator.getPerson().getOrganizations()) {
          if (organization.getIdentifier() != null) {
            organization.setIdentifier(organization.getIdentifier().trim());
          }

        }
      }
    }
    if (invalidItemMap.containsKey(pubItem.getVersion().getObjectId())) {
      storedItem = invalidItemMap.get(pubItem.getVersion().getObjectId());
    } else if (validItemMap.containsKey(pubItem.getVersion().getObjectId())) {
      storedItem = validItemMap.get(pubItem.getVersion().getObjectId());
    }
    if (storedItem == null
        || !pubItem.getModificationDate().equals(storedItem.getLastModificationDate())) {
      // revalidate
      System.out.println("Yearbook Validating: " + pubItem.getVersion().getObjectId());
      // TODO maybe a special validationpoint for the yearbook needs to be created
      ValidationReportVO rep = new ValidationReportVO();
      // ValidationReportVO rep =
      // this.itemValidating.validateItemObject(new PubItemVO(pubItem), ValidationPoint.DEFAULT);
      if (rep.getItems().size() > 0) {
        validItemMap.remove(pubItem.getVersion().getObjectId());
        invalidItemMap.put(pubItem.getVersion().getObjectId(), new YearbookInvalidItemRO(pubItem
            .getVersion().getObjectId(), rep, pubItem.getModificationDate()));
      } else {
        invalidItemMap.remove(pubItem.getVersion().getObjectId());
        validItemMap.put(pubItem.getVersion().getObjectId(), new YearbookInvalidItemRO(pubItem
            .getVersion().getObjectId(), rep, pubItem.getModificationDate()));
      }
      return rep.isValid();
    }
    return storedItem.getValidationReport().isValid();
  }

  public String validateYearbook() throws Exception {
    List<PubItemVOPresentation> pubItemList = retrieveAllMembers();
    for (PubItemVOPresentation pubItem : pubItemList) {
      validateItem(pubItem);
    }
    if (invalidItemMap.size() == 0) {
      info(getMessage("Yearbook_allItemsValid"));
    }
    changeToInvalidItems();
    return "";
  }

  public void setInvalidItemMap(Map<String, YearbookInvalidItemRO> invalidItemMap) {
    this.invalidItemMap = invalidItemMap;
  }

  public Map<String, YearbookInvalidItemRO> getInvalidItemMap() {
    return invalidItemMap;
  }

  public void setSelectedWorkspace(YBWORKSPACE selectedWorkspace) {
    this.selectedWorkspace = selectedWorkspace;
  }

  public YBWORKSPACE getSelectedWorkspace() {
    return selectedWorkspace;
  }

  public String changeToCandidates() {
    PubItemListSessionBean pilsb =
        (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    setSelectedWorkspace(YBWORKSPACE.CANDIDATES);
    pilsb.setCurrentPageNumber(1);
    pilsb.redirect();
    return "";
  }

  public String changeToMembers() {
    PubItemListSessionBean pilsb =
        (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    setSelectedWorkspace(YBWORKSPACE.MEMBERS);
    pilsb.setCurrentPageNumber(1);
    pilsb.redirect();
    return "";
  }

  public String changeToInvalidItems() {
    PubItemListSessionBean pilsb =
        (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    setSelectedWorkspace(YBWORKSPACE.INVALID);
    pilsb.setCurrentPageNumber(1);
    pilsb.redirect();
    return "";
  }

  public String changeToNonCandidates() {
    PubItemListSessionBean pilsb =
        (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    setSelectedWorkspace(YBWORKSPACE.NON_CANDIDATES);
    pilsb.setCurrentPageNumber(1);
    pilsb.redirect();
    return "";
  }

  public String submitYearbook() {
    try {
      List<PubItemVOPresentation> pubItemList = retrieveAllMembers();
      boolean allValid = true;
      for (PubItemVOPresentation pubItem : pubItemList) {
        boolean valid = validateItem(pubItem);
        if (!valid) {
          error(getMessage("Yearbook_ItemInvalid").replaceAll("\\$1",
              "\"" + pubItem.getMetadata().getTitle() + "\""));
          allValid = false;
        }
      }
      if (!allValid) {
        error(getMessage("Yearbook_SubmitError"));
      } else {
        TaskParamVO param =
            new TaskParamVO(getYearbookItem().getModificationDate(), "Submitting yearbook");
        String paramXml = XmlTransformingService.transformToTaskParam(param);
        itemHandler.submit(getYearbookItem().getVersion().getObjectId(), paramXml);
        info(getMessage("Yearbook_SubmittedSuccessfully"));
      }
    } catch (Exception e) {
      error(getMessage("Yearbook_SubmitError"));
      logger.error("Could not submit Yearbook Item", e);
    }
    try {
      String yearbookXml = itemHandler.retrieve(getYearbookItem().getVersion().getObjectId());
      this.setYearbookItem((PubItemVO) XmlTransformingService.transformToPubItem(yearbookXml));
    } catch (Exception e) {
      error(getMessage("Yearbook_reinitializeError"));
      logger.error("Could not reinitialize Yearbook", e);
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
      PubItemVO item = this.getYearbookItem();

      if (item.getRelations() != null && item.getRelations().size() > 0) {
        query = YearbookCandidatesRetrieverRequestBean.getMemberQuery(item).getCqlQuery();
      }

      ItemContainerSearchResult result =
          SearchService.searchForItemContainer(new PlainCqlQuery(query));

      pubItemList = SearchRetrieverRequestBean.extractItemsOfSearchResult(result);
      pilsb.downloadExportFile(pubItemList);

    } catch (Exception e) {
      error("Error while exporting");
      logger.error("Error exporting yearbook", e);
    }

    return "";
  }
}
