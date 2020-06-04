package de.mpg.mpdl.inge.pubman.web.batch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.DisplayTools;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.service.aa.IpListProvider.IpRange;
import de.mpg.mpdl.inge.service.pubman.PubItemBatchService;

/**
 * SessionBean for batch operations on PubItems
 * 
 * @author walter
 *
 */
/**
 * @author walter
 *
 */
@ManagedBean(name = "PubItemBatchSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class PubItemBatchSessionBean extends FacesBean {
  private static final Logger logger = LogManager.getLogger(PubItemBatchSessionBean.class);

  @ManagedProperty(value = "#{LoginHelper}")
  private LoginHelper loginHelper;

  @ManagedProperty(value = "#{pubItemBatchServiceImpl}")
  private PubItemBatchService pubItemBatchService;

  private BatchProcessLogDbVO batchProcessLog;
  private PubItemListSessionBean pubItemListSessionBean;

  private String changeExternalReferencesContentCategoryFrom;
  private ArrayList<SelectItem> changeExternalReferencesContentCategorySelectItems;
  private String changeExternalReferencesContentCategoryTo;
  private String changeFilesAudienceFrom;
  private ArrayList<SelectItem> changeFilesAudienceSelectItems;
  private String changeFilesAudienceTo;
  private String changeFilesContentCategoryFrom;
  private ArrayList<SelectItem> changeFilesContentCategorySelectItems;
  private String changeFilesContentCategoryTo;
  private String changeFilesVisibilityFrom;
  private ArrayList<SelectItem> changeFilesVisibilitySelectItems;
  private String changeFilesVisibilityTo;
  private String changeGenreFrom;
  private ArrayList<SelectItem> changeGenreSelectItems;
  private String changeGenreTo;
  private ArrayList<SelectItem> contextSelectItems;
  private String changeReviewMethodFrom;
  private String changeReviewMethodTo;
  private ArrayList<SelectItem> changeReviewMethodSelectItems;
  private String changeSourceGenreFrom;
  private ArrayList<SelectItem> changeSourceGenreSelectItems;
  private String changeSourceGenreTo;
  private String changeSourceIdAdd;
  private String changeSoureIdAddNumber;
  private String changeSourceIdReplaceFrom;
  private String changeSourceIdReplaceTo;
  private String changeSourceIdReplaceNumber;
  private String changeSourceIdTypeAdd;
  private String changeSourceIdTypeReplace;
  private List<SelectItem> changeSourceIdTypeSelectItems;
  private String changeSoureIssueNumber;
  private List<SelectItem> changeSourceNumberSelectItems;
  private boolean disabledKeywordInput;
  private String inputChangeLocalTagsReplaceFrom;
  private String inputChangeLocalTagsReplaceTo;
  private String inputChangeLocalTagsAdd;
  private String inputChangeSourceIssue;
  private List<String> localTagsToAdd;
  private String changePublicationKeywordsAddInput;
  private replaceType changePublicationKeywordsReplaceType;
  private List<SelectItem> changePublicationKeywordsReplaceTypeSelectItems;
  private String changePublicationKeywordsReplaceFrom;
  private String changePublicationKeywordsReplaceTo;
  private String selectedContextNew;
  private String selectedContextOld;
  private Map<String, ItemVersionRO> storedPubItems;

  /**
   * The number that represents the difference between the real number of items in the batch
   * environment and the number that is displayed. These might differ due to the problem that items
   * can change their state and are then not retrieved by the filter any more. In this case, this
   * number is adapted to the number of items retrieved via the filter query.
   */
  private int diffDisplayNumber = 0;


  public PubItemBatchSessionBean() {
    this.batchProcessLog = null;
    this.pubItemListSessionBean = (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    this.storedPubItems = new HashMap<String, ItemVersionRO>();
    // Contexts (Collections) and depending Genres
    final ContextListSessionBean clsb = (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    final List<PubContextVOPresentation> contextVOList = clsb.getModeratorContextList();
    this.contextSelectItems = new ArrayList<SelectItem>();
    List<Genre> allowedGenresForContext = new ArrayList<Genre>();
    List<Genre> allowedGenres = new ArrayList<Genre>();
    Genre genreToCheck = null;
    this.changeGenreSelectItems = new ArrayList<SelectItem>();
    for (int i = 0; i < contextVOList.size(); i++) {
      String workflow = "null";
      if (contextVOList.get(i).getWorkflow() != null) {
        workflow = contextVOList.get(i).getWorkflow().toString();
      }
      this.contextSelectItems.add(new SelectItem(contextVOList.get(i).getObjectId(), contextVOList.get(i).getName() + " -- " + workflow));
      allowedGenresForContext = contextVOList.get(i).getAllowedGenres();
      for (int j = 0; j < allowedGenresForContext.size(); j++) {
        genreToCheck = allowedGenresForContext.get(j);
        if (!allowedGenres.contains(genreToCheck)) {
          allowedGenres.add(genreToCheck);
        }
      }
    }
    if (!allowedGenres.isEmpty()) {
      this.changeGenreSelectItems
          .addAll(Arrays.asList(this.getI18nHelper().getSelectItemsForEnum(false, allowedGenres.toArray(new MdsPublicationVO.Genre[] {}))));
    } else {
      this.changeGenreSelectItems.add(new SelectItem(null, " --- "));
    }

    // SelectItems for file visibility
    this.changeFilesVisibilitySelectItems = new ArrayList<SelectItem>(Arrays.asList(this.getI18nHelper().getSelectItemsVisibility(false)));

    // SeletItems for file content category
    this.changeFilesContentCategorySelectItems =
        new ArrayList<SelectItem>(Arrays.asList(this.getI18nHelper().getSelectItemsContentCategory(true)));

    // SelectItems for file audience
    this.changeFilesAudienceSelectItems = new ArrayList<SelectItem>();
    for (IpRange ipRange : ApplicationBean.INSTANCE.getIpListProvider().getAll()) {
      changeFilesAudienceSelectItems.add(new SelectItem(ipRange.getId(), ipRange.getName()));
    }

    // SelectItems for external references content category
    this.changeExternalReferencesContentCategorySelectItems =
        new ArrayList<SelectItem>(Arrays.asList(this.getI18nHelper().getSelectItemsContentCategory(true)));

    // SelectItems for publication review methode
    this.changeReviewMethodSelectItems = new ArrayList<SelectItem>(Arrays.asList(this.getI18nHelper().getSelectItemsReviewMethod(true)));

    // SelectItems for source genre
    final Map<String, String> excludedSourceGenres = ApplicationBean.INSTANCE.getExcludedSourceGenreMap();
    changeSourceGenreSelectItems = new ArrayList<SelectItem>();
    changeSourceGenreSelectItems.add(new SelectItem("", this.getLabel("BatchWorkspace_lblNoItemsSet")));
    for (final SourceVO.Genre value : SourceVO.Genre.values()) {
      changeSourceGenreSelectItems.add(new SelectItem(value, this.getLabel("ENUM_GENRE_" + value.name())));
    }
    String uri = "";
    int i = 0;
    while (i < changeSourceGenreSelectItems.size()) {
      if (changeSourceGenreSelectItems.get(i).getValue() != null && !("").equals(changeSourceGenreSelectItems.get(i).getValue())) {
        uri = ((SourceVO.Genre) changeSourceGenreSelectItems.get(i).getValue()).getUri();
      }
      if (excludedSourceGenres.containsValue(uri)) {
        changeSourceGenreSelectItems.remove(i);
      } else {
        i++;
      }
    }
    changeSourceGenreSelectItems.toArray(new SelectItem[changeSourceGenreSelectItems.size()]);

    // Instantiate localTagsToAdd
    this.localTagsToAdd = new ArrayList<String>();
    this.localTagsToAdd.add("");

    // Instantiate and fill source selection
    this.changeSourceNumberSelectItems = new ArrayList<SelectItem>();
    this.changeSourceNumberSelectItems.add(new SelectItem("1", "1"));
    this.changeSourceNumberSelectItems.add(new SelectItem("2", "2"));

    // Instantiate and fill source selection
    this.changeSourceIdTypeSelectItems = Arrays.asList(getIdentifierTypes());

    // Instantiate and fill changePublicationKeywordsReplaceTypeSelectItems
    this.changePublicationKeywordsReplaceTypeSelectItems = new ArrayList<SelectItem>();
    this.changePublicationKeywordsReplaceTypeSelectItems.add(new SelectItem(replaceType.REPLACE_ALL, "ALL"));
    this.changePublicationKeywordsReplaceTypeSelectItems.add(new SelectItem(replaceType.REPLACE_BY_VALUE, "BY VALUE"));

    // Instantiate and fill disableKeywordInput
    this.disabledKeywordInput = true;

  }

  @PostConstruct
  private void init() {
    this.batchProcessLog = pubItemBatchService.getBatchProcessLogForCurrentUser(loginHelper.getAccountUser());
  }

  public BatchProcessLogDbVO getBatchProcessLog() {
    return batchProcessLog;
  }

  public void setBatchProcessLog(BatchProcessLogDbVO batchProcessLog) {
    this.batchProcessLog = batchProcessLog;
  }

  public int getBatchPubItemsSize() {
    return this.storedPubItems.size();
  }

  public String getChangeExternalReferencesContentCategoryFrom() {
    return changeExternalReferencesContentCategoryFrom;
  }

  public void setChangeExternalReferencesContentCategoryFrom(String changeExternalReferencesContentCategoryFrom) {
    this.changeExternalReferencesContentCategoryFrom = changeExternalReferencesContentCategoryFrom;
  }

  public ArrayList<SelectItem> getChangeExternalReferencesContentCategorySelectItems() {
    return changeExternalReferencesContentCategorySelectItems;
  }

  public void setChangeExternalReferencesContentCategorySelectItems(
      ArrayList<SelectItem> changeExternalReferencesContentCategorySelectItems) {
    this.changeExternalReferencesContentCategorySelectItems = changeExternalReferencesContentCategorySelectItems;
  }

  public String getChangeExternalReferencesContentCategoryTo() {
    return changeExternalReferencesContentCategoryTo;
  }

  public void setChangeExternalReferencesContentCategoryTo(String changeExternalReferencesContentCategoryTo) {
    this.changeExternalReferencesContentCategoryTo = changeExternalReferencesContentCategoryTo;
  }

  public String getChangeFilesAudienceFrom() {
    return changeFilesAudienceFrom;
  }

  public void setChangeFilesAudienceFrom(String changeFilesAudienceFrom) {
    this.changeFilesAudienceFrom = changeFilesAudienceFrom;
  }

  public ArrayList<SelectItem> getChangeFilesAudienceSelectItems() {
    return changeFilesAudienceSelectItems;
  }

  public void setChangeFilesAudienceSelectItems(ArrayList<SelectItem> changeFilesAudienceSelectItems) {
    this.changeFilesAudienceSelectItems = changeFilesAudienceSelectItems;
  }

  public String getChangeFilesAudienceTo() {
    return changeFilesAudienceTo;
  }

  public void setChangeFilesAudienceTo(String changeFilesAudienceTo) {
    this.changeFilesAudienceTo = changeFilesAudienceTo;
  }

  public String getChangeFilesContentCategoryFrom() {
    return changeFilesContentCategoryFrom;
  }

  public void setChangeFilesContentCategoryFrom(String changeFilesContentCategoryFrom) {
    this.changeFilesContentCategoryFrom = changeFilesContentCategoryFrom;
  }

  public String getChangeFilesContentCategoryTo() {
    return changeFilesContentCategoryTo;
  }

  public void setChangeFilesContentCategoryTo(String changeFilesContentCategoryTo) {
    this.changeFilesContentCategoryTo = changeFilesContentCategoryTo;
  }

  public ArrayList<SelectItem> getChangeFilesContentCategorySelectItems() {
    return changeFilesContentCategorySelectItems;
  }

  public void setChangeFilesContentCategorySelectItems(ArrayList<SelectItem> changeFilesContentCategorySelectItems) {
    this.changeFilesContentCategorySelectItems = changeFilesContentCategorySelectItems;
  }

  public String getChangeFilesVisibilityFrom() {
    return changeFilesVisibilityFrom;
  }

  public void setChangeFilesVisibilityFrom(String changeFilesVisibilityFrom) {
    this.changeFilesVisibilityFrom = changeFilesVisibilityFrom;
  }

  public String getChangeFilesVisibilityTo() {
    return changeFilesVisibilityTo;
  }

  public void setChangeFilesVisibilityTo(String changeFilesVisibilityTo) {
    this.changeFilesVisibilityTo = changeFilesVisibilityTo;
  }

  public ArrayList<SelectItem> getChangeFilesVisibilitySelectItems() {
    return changeFilesVisibilitySelectItems;
  }

  public void setChangeFilesVisibilitySelectItems(ArrayList<SelectItem> changeFilesVisibilitySelectItems) {
    this.changeFilesVisibilitySelectItems = changeFilesVisibilitySelectItems;
  }

  public String getChangeGenreFrom() {
    return changeGenreFrom;
  }

  public void setChangeGenreFrom(String changeGenreFrom) {
    this.changeGenreFrom = changeGenreFrom;
  }

  public ArrayList<SelectItem> getChangeGenreSelectItems() {
    return changeGenreSelectItems;
  }

  public void setChangeGenreSelectItems(ArrayList<SelectItem> changeGenreSelectItems) {
    this.changeGenreSelectItems = changeGenreSelectItems;
  }

  public String getChangeGenreTo() {
    return changeGenreTo;
  }

  public replaceType getChangePublicationKeywordsReplaceType() {
    return changePublicationKeywordsReplaceType;
  }

  public void setChangePublicationKeywordsReplaceType(replaceType changePublicationKeywordsReplaceType) {
    this.changePublicationKeywordsReplaceType = changePublicationKeywordsReplaceType;
  }

  public List<SelectItem> getChangePublicationKeywordsReplaceTypeSelectItems() {
    return changePublicationKeywordsReplaceTypeSelectItems;
  }

  public void setChangePublicationKeywordsReplaceTypeSelectItems(List<SelectItem> changePublicationKeywordsReplaceTypeSelectItems) {
    this.changePublicationKeywordsReplaceTypeSelectItems = changePublicationKeywordsReplaceTypeSelectItems;
  }

  public String getChangePublicationKeywordsAddInput() {
    return changePublicationKeywordsAddInput;
  }

  public void setChangePublicationKeywordsAddInput(String changePublicationKeywordsAddInput) {
    this.changePublicationKeywordsAddInput = changePublicationKeywordsAddInput;
  }

  public String getChangePublicationKeywordsReplaceFrom() {
    return changePublicationKeywordsReplaceFrom;
  }

  public void setChangePublicationKeywordsReplaceFrom(String changePublicationKeywordsReplaceFrom) {
    this.changePublicationKeywordsReplaceFrom = changePublicationKeywordsReplaceFrom;
  }

  public String getChangePublicationKeywordsReplaceTo() {
    return changePublicationKeywordsReplaceTo;
  }

  public void setChangePublicationKeywordsReplaceTo(String changePublicationKeywordsReplaceTo) {
    this.changePublicationKeywordsReplaceTo = changePublicationKeywordsReplaceTo;
  }

  public String getChangeReviewMethodFrom() {
    return changeReviewMethodFrom;
  }

  public void setChangeReviewMethodFrom(String changeReviewMethodFrom) {
    this.changeReviewMethodFrom = changeReviewMethodFrom;
  }

  public ArrayList<SelectItem> getChangeReviewMethodSelectItems() {
    return changeReviewMethodSelectItems;
  }

  public void setChangeReviewMethodSelectItems(ArrayList<SelectItem> changeReviewMethodSelectItems) {
    this.changeReviewMethodSelectItems = changeReviewMethodSelectItems;
  }

  public String getChangeReviewMethodTo() {
    return changeReviewMethodTo;
  }

  public void setChangeReviewMethodTo(String changeReviewMethodTo) {
    this.changeReviewMethodTo = changeReviewMethodTo;
  }

  public String getChangeSourceGenreFrom() {
    return changeSourceGenreFrom;
  }

  public void setChangeSourceGenreFrom(String changeSourceGenreFrom) {
    this.changeSourceGenreFrom = changeSourceGenreFrom;
  }

  public String getInputChangeSourceIssue() {
    return inputChangeSourceIssue;
  }

  public boolean getDisabledKeywordInput() {
    return disabledKeywordInput;
  }

  public void setDisabledKeywordInput(boolean disabledKeywordInput) {
    this.disabledKeywordInput = disabledKeywordInput;
  }

  public void setInputChangeSourceIssue(String inputChangeSourceIssue) {
    this.inputChangeSourceIssue = inputChangeSourceIssue;
  }

  public ArrayList<SelectItem> getChangeSourceGenreSelectItems() {
    return changeSourceGenreSelectItems;
  }

  public void setChangeSourceGenreSelectItems(ArrayList<SelectItem> changeSourceGenreSelectItems) {
    this.changeSourceGenreSelectItems = changeSourceGenreSelectItems;
  }

  public String getChangeSourceGenreTo() {
    return changeSourceGenreTo;
  }

  public void setChangeSourceGenreTo(String changeSourceGenreTo) {
    this.changeSourceGenreTo = changeSourceGenreTo;
  }

  public void setChangeGenreTo(String changeGenreTo) {
    this.changeGenreTo = changeGenreTo;
  }

  public String getChangeSoureIdAddNumber() {
    return changeSoureIdAddNumber;
  }

  public void setChangeSoureIdAddNumber(String changeSoureIdAddNumber) {
    this.changeSoureIdAddNumber = changeSoureIdAddNumber;
  }

  public String getChangeSourceIdAdd() {
    return changeSourceIdAdd;
  }

  public void setChangeSourceIdAdd(String changeSourceIdAdd) {
    this.changeSourceIdAdd = changeSourceIdAdd;
  }

  public String getChangeSourceIdReplaceFrom() {
    return changeSourceIdReplaceFrom;
  }

  public void setChangeSourceIdReplaceFrom(String changeSourceIdReplaceFrom) {
    this.changeSourceIdReplaceFrom = changeSourceIdReplaceFrom;
  }

  public String getChangeSourceIdReplaceTo() {
    return changeSourceIdReplaceTo;
  }

  public void setChangeSourceIdReplaceTo(String changeSourceIdReplaceTo) {
    this.changeSourceIdReplaceTo = changeSourceIdReplaceTo;
  }

  public String getChangeSourceIdReplaceNumber() {
    return changeSourceIdReplaceNumber;
  }

  public void setChangeSourceIdReplaceNumber(String changeSourceIdReplaceNumber) {
    this.changeSourceIdReplaceNumber = changeSourceIdReplaceNumber;
  }

  public String getChangeSourceIdTypeReplace() {
    return changeSourceIdTypeReplace;
  }

  public void setChangeSourceIdTypeReplace(String changeSourceIdTypeReplace) {
    this.changeSourceIdTypeReplace = changeSourceIdTypeReplace;
  }

  public String getChangeSourceIdTypeAdd() {
    return changeSourceIdTypeAdd;
  }

  public void setChangeSourceIdTypeAdd(String changeSourceIdTypeAdd) {
    this.changeSourceIdTypeAdd = changeSourceIdTypeAdd;
  }

  public List<SelectItem> getChangeSourceIdTypeSelectItems() {
    return changeSourceIdTypeSelectItems;
  }

  public void setChangeSourceIdTypeSelectItems(List<SelectItem> changeSourceIdTypeSelectItems) {
    this.changeSourceIdTypeSelectItems = changeSourceIdTypeSelectItems;
  }

  public String getChangeSoureIssueNumber() {
    return changeSoureIssueNumber;
  }

  public void setChangeSoureIssueNumber(String changeSoureIssueSoure) {
    this.changeSoureIssueNumber = changeSoureIssueSoure;
  }

  public List<SelectItem> getChangeSourceNumberSelectItems() {
    return changeSourceNumberSelectItems;
  }

  public void setChangeSourceNumberSelectItems(List<SelectItem> changeSourceIssueSourceSelectItems) {
    this.changeSourceNumberSelectItems = changeSourceIssueSourceSelectItems;
  }

  public ArrayList<SelectItem> getContextSelectItems() {
    return contextSelectItems;
  }

  public void setContextSelectItems(ArrayList<SelectItem> contextSelectItems) {
    this.contextSelectItems = contextSelectItems;
  }

  public void setDiffDisplayNumber(int diffDisplayNumber) {
    this.diffDisplayNumber = diffDisplayNumber;
  }

  public int getDiffDisplayNumber() {
    return this.diffDisplayNumber;
  }

  public int getDisplayNumber() {
    return this.getBatchPubItemsSize() - this.diffDisplayNumber;
  }

  public String getInputChangeLocalTagsAdd() {
    return inputChangeLocalTagsAdd;
  }

  public void setInputChangeLocalTagsAdd(String inputChangeLocalTagsAdd) {
    this.inputChangeLocalTagsAdd = inputChangeLocalTagsAdd;
  }

  public String getInputChangeLocalTagsReplaceFrom() {
    return inputChangeLocalTagsReplaceFrom;
  }

  public void setInputChangeLocalTagsReplaceFrom(String inputChangeLocalTagsReplaceFrom) {
    this.inputChangeLocalTagsReplaceFrom = inputChangeLocalTagsReplaceFrom;
  }

  public String getInputChangeLocalTagsReplaceTo() {
    return inputChangeLocalTagsReplaceTo;
  }

  public void setInputChangeLocalTagsReplaceTo(String inputChangeLocalTagsReplaceTo) {
    this.inputChangeLocalTagsReplaceTo = inputChangeLocalTagsReplaceTo;
  }

  public String getItemLink(String itemId, int itemVersion) {
    try {
      return CommonUtils.getGenericItemLink(itemId, itemVersion);
    } catch (Exception e) {
      logger.error("Error getting log item link", e);
    }
    return "";
  }

  public List<String> getLocalTagsToAdd() {
    return localTagsToAdd;
  }

  public void setLocalTagsToAdd(List<String> localTagsToAdd) {
    this.localTagsToAdd = localTagsToAdd;
  }

  public LoginHelper getLoginHelper() {
    return loginHelper;
  }

  public void setLoginHelper(LoginHelper loginHelper) {
    this.loginHelper = loginHelper;
  }

  public PubItemBatchService getPubItemBatchService() {
    return pubItemBatchService;
  }

  public void setPubItemBatchService(PubItemBatchService pubItemBatchService) {
    this.pubItemBatchService = pubItemBatchService;
  }

  public String getSelectedContextNew() {
    return selectedContextNew;
  }

  public void setSelectedContextNew(String selectedContextNew) {
    this.selectedContextNew = selectedContextNew;
  }

  public String getSelectedContextOld() {
    return selectedContextOld;
  }

  public void setSelectedContextOld(String selectedContextOld) {
    this.selectedContextOld = selectedContextOld;
  }

  public InternationalizationHelper getI18nHelper() {
    return (InternationalizationHelper) FacesTools.findBean("InternationalizationHelper");
  }

  /**
   * Sets the map with the current reference objects of the batch list items. The key is the object
   * id with version.
   */
  public void setStoredPubItems(Map<String, ItemVersionRO> storedPubItems) {
    this.storedPubItems = storedPubItems;
  }

  /**
   * Returns the map with the current reference objects of the batch list items. The key is the
   * object id with version.
   */
  public Map<String, ItemVersionRO> getStoredPubItems() {
    return this.storedPubItems;
  }

  /**
   * localized creation of SelectItems for the identifier types available
   * 
   * @return SelectItem[] with Strings representing identifier types
   */
  public SelectItem[] getIdentifierTypes() {
    final ArrayList<SelectItem> selectItemList = new ArrayList<SelectItem>();

    // constants for comboBoxes
    selectItemList.add(new SelectItem(null, this.getLabel("EditItem_NO_ITEM_SET")));

    for (final IdentifierVO.IdType type : DisplayTools.getIdTypesToDisplay()) {
      selectItemList.add(new SelectItem(type.toString(), this.getLabel("ENUM_IDENTIFIERTYPE_" + type.toString())));
    }

    // Sort identifiers alphabetically
    Collections.sort(selectItemList, new Comparator<SelectItem>() {
      @Override
      public int compare(SelectItem o1, SelectItem o2) {
        return o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase());
      }
    });

    return selectItemList.toArray(new SelectItem[] {});
  }



  /**
   * adding a local tag to the local tag presentation list
   */
  public void addLocalTag() {
    this.localTagsToAdd.add(new String(""));
  }

  /**
   * remove local tag within the local tag presentation list
   * 
   * @param index
   */
  public void removeLocalTag(int index) {
    this.localTagsToAdd.remove(index);
  }

  public String addLocalTagsItemList() {
    logger.info("trying to add local tags for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    // try {
    this.batchProcessLog = pubItemBatchService.addLocalTags(pubItemObjectIdList, localTagsToAdd,
        "batch add local tags " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    // } catch (IngeTechnicalException e) {
    // logger.error("A technichal error occoured during the batch process for adding local tags",
    // e);
    // this.error("A technichal error occoured during the batch process for adding local tags");
    // } catch (AuthenticationException e) {
    // logger.error("Authentication for batch adding local tags failed", e);
    // this.error("Authentication for batch adding local tags failed");
    // } catch (AuthorizationException e) {
    // logger.error("Authorization for batch adding local tags failed", e);
    // this.error("Authorization for batch adding local tags failed");
    // } catch (IngeApplicationException e) {
    // logger.error("An application error occoured during the batch adding local tags", e);
    // this.error("An application error occoured during the batch adding local tags");
    // }
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String changeContextItemList() {
    logger.info("trying to change context for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.changeContext(pubItemObjectIdList, selectedContextOld, selectedContextNew,
        "batch release " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String changeExternalRefereneceContentCategoryItemList() {
    logger.info("trying to change the external reference content category for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.changeExternalRefereneceContentCategory(pubItemObjectIdList,
        changeExternalReferencesContentCategoryFrom, changeExternalReferencesContentCategoryTo,
        "batch change external references content category " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken(),
        loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String changeFileAudienceItemList() {
    logger.info("trying to change the file audience for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.changeFileAudience(pubItemObjectIdList, changeFilesAudienceFrom, changeFilesAudienceTo,
        "batch change file content category " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken(),
        loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String changeFileContentCategoryItemList() {
    logger.info("trying to change the file content category for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.changeFileContentCategory(pubItemObjectIdList, changeFilesContentCategoryFrom,
        changeFilesContentCategoryTo, "batch change file content category " + formatter.format(calendar.getTime()),
        loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String changeFileVisibilityItemList() {
    logger.info("trying to change the file visibility for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.changeFileVisibility(pubItemObjectIdList, Visibility.valueOf(changeFilesVisibilityFrom),
        Visibility.valueOf(changeFilesVisibilityTo), "batch change file visibility " + formatter.format(calendar.getTime()),
        loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String changeGenreItemList() {
    logger.info("trying to change genre for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    Genre genreOld = Genre.valueOf(this.changeGenreFrom);
    Genre genreNew = Genre.valueOf(this.changeGenreTo);
    this.batchProcessLog = pubItemBatchService.changeGenre(pubItemObjectIdList, genreOld, genreNew,
        "batch change genre " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String changePublicationKeywordsAddItemList() {
    logger.info("trying to add keywords for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    // try {
    this.batchProcessLog = pubItemBatchService.addKeywords(pubItemObjectIdList, changePublicationKeywordsAddInput,
        "batch add keywords method " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken(),
        loginHelper.getAccountUser());
    // } catch (IngeTechnicalException e) {
    // logger.error("A technichal error occoured during the batch process for adding keywords", e);
    // this.error("A technichal error occoured during the batch process for adding keywords");
    // } catch (AuthenticationException e) {
    // logger.error("Authentication for batch adding keywords failed", e);
    // this.error("Authentication for batch adding keywords failed");
    // } catch (AuthorizationException e) {
    // logger.error("Authorization for batch adding keywords failed", e);
    // this.error("Authorization for batch adding keywords failed");
    // } catch (IngeApplicationException e) {
    // logger.error("An application error occoured during the batch adding keywords", e);
    // this.error("An application error occoured during the batchadding keywords");
    // }
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String changePublicationKeywordsReplaceItemList() {
    logger.info("trying to Keywords method for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    if (replaceType.REPLACE_BY_VALUE.equals(changePublicationKeywordsReplaceType)) {
      this.batchProcessLog = pubItemBatchService.changeKeywords(pubItemObjectIdList, changePublicationKeywordsReplaceFrom,
          changePublicationKeywordsReplaceTo, "batch change keywords " + formatter.format(calendar.getTime()),
          loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    } else {
      this.batchProcessLog = pubItemBatchService.replaceAllKeywords(pubItemObjectIdList, changePublicationKeywordsReplaceTo,
          "batch replace all keywords " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken(),
          loginHelper.getAccountUser());
    }
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String changeReviewMethodItemList() {
    logger.info("trying to change review method for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.changeReviewMethod(pubItemObjectIdList, changeReviewMethodFrom, changeReviewMethodTo,
        "batch change review method " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken(),
        loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String changeSourceGenreItemList() {
    logger.info("trying to change source genre for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.changeSourceGenre(pubItemObjectIdList, SourceVO.Genre.valueOf(this.changeSourceGenreFrom),
        SourceVO.Genre.valueOf(this.changeSourceGenreTo), "batch change source genre " + formatter.format(calendar.getTime()),
        loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String addSourceIdItemList() {
    logger.info("trying to replacing the source id for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.addSourceId(pubItemObjectIdList, this.changeSoureIdAddNumber,
        IdentifierVO.IdType.valueOf(this.changeSourceIdTypeAdd), this.changeSourceIdAdd,
        "batch replacing the source id " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken(),
        loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String changeSourceIdReplaceItemList() {
    logger.info("trying to replace source id for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.changeSourceIdReplace(pubItemObjectIdList, this.changeSourceIdReplaceNumber,
        IdentifierVO.IdType.valueOf(this.changeSourceIdTypeReplace), this.changeSourceIdReplaceFrom, this.changeSourceIdReplaceTo,
        "batch replace source id " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken(),
        loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String changeSourceIssueItemList() {
    logger.info("trying to change source genre for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.changeSourceIssue(pubItemObjectIdList, changeSoureIssueNumber, inputChangeSourceIssue,
        "batch change source genre " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken(),
        loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String deleteItemList() {
    logger.info("trying to batch delete " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.deletePubItems(pubItemObjectIdList, "batch delete " + formatter.format(calendar.getTime()),
        loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String releaseItemList() {
    logger.info("trying to batch release " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.releasePubItems(pubItemObjectIdList, "batch release " + formatter.format(calendar.getTime()),
        loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String replaceLocalTagsItemList() {
    logger.info("trying to change context for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.replaceLocalTags(pubItemObjectIdList, inputChangeLocalTagsReplaceFrom,
        inputChangeLocalTagsReplaceTo, "batch replacing local tags " + formatter.format(calendar.getTime()),
        loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String reviseItemList() {
    logger.info("trying to batch revise " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.revisePubItems(pubItemObjectIdList, "batch revise " + formatter.format(calendar.getTime()),
        loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String submitItemList() {
    logger.info("trying to batch submit " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.submitPubItems(pubItemObjectIdList, "batch submit " + formatter.format(calendar.getTime()),
        loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public String withdrawItemList() {
    logger.info("trying to batch withdraw " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<String>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = pubItemBatchService.withdrawPubItems(pubItemObjectIdList,
        "batch withdraw " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken(), loginHelper.getAccountUser());
    pubItemListSessionBean.changeSubmenuToProcessLog();
    return null;
  }

  public void handleKeywordReplaceTypeChange() {
    if (replaceType.REPLACE_ALL.equals(this.changePublicationKeywordsReplaceType)) {
      this.disabledKeywordInput = true;
    } else {
      this.disabledKeywordInput = false;
    }
  }

  private enum replaceType
  {
    REPLACE_ALL, REPLACE_BY_VALUE;
  }
}
