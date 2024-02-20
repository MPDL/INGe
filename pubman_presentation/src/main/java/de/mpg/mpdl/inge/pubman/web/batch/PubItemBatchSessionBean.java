package de.mpg.mpdl.inge.pubman.web.batch;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessItemVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.xmltransforming.logging.Messages;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.PersonSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.StringOrHiddenIdSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.DisplayTools;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.LanguageChangeObserver;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.service.aa.IpListProvider;
import de.mpg.mpdl.inge.service.pubman.PubItemBatchService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.ManagedProperty;
import jakarta.faces.bean.SessionScoped;
import jakarta.faces.model.SelectItem;

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
public class PubItemBatchSessionBean extends FacesBean implements LanguageChangeObserver {
  private static final Logger logger = LogManager.getLogger(PubItemBatchSessionBean.class);

  @ManagedProperty(value = "#{LoginHelper}")
  private LoginHelper loginHelper;

  @ManagedProperty(value = "#{pubItemBatchServiceImpl}")
  private PubItemBatchService pubItemBatchService;

  private BatchProcessLogDbVO batchProcessLog;
  private InternationalizationHelper internationalizationHelper;
  private PubItemListSessionBean pubItemListSessionBean;

  private Map<String, ItemVersionRO> storedPubItems;

  // Context
  private ArrayList<SelectItem> contextSelectItems;
  private String selectedContextOld;
  private String selectedContextNew;

  // Local tags
  private List<String> localTagsToAdd;
  private String inputChangeLocalTagsReplaceFrom;
  private String inputChangeLocalTagsReplaceTo;

  // Genre
  private ArrayList<SelectItem> changeGenreSelectItems;
  private String changeGenreFrom;
  private String changeGenreTo;
  private ArrayList<SelectItem> changeGenreThesisTypeSelectItems;
  private boolean showThesisType;
  private String changeGenreThesisType;

  // Visibility
  private ArrayList<SelectItem> changeFilesVisibilitySelectItems;
  private String changeFilesVisibilityFrom;
  private String changeFilesVisibilityTo;

  // Content category files
  private ArrayList<SelectItem> changeFilesContentCategorySelectItems;
  private String changeFilesContentCategoryFrom;
  private String changeFilesContentCategoryTo;

  // IP Range
  private ArrayList<SelectItem> changeFilesAudienceSelectItems;
  private List<String> ipRangeToAdd;

  // Content category external references
  private ArrayList<SelectItem> changeExternalReferencesContentCategorySelectItems;
  private String changeExternalReferencesContentCategoryFrom;
  private String changeExternalReferencesContentCategoryTo;

  // Orcid
  private SearchCriterionBase criterion;
  private String orcid;

  // Review
  private ArrayList<SelectItem> changeReviewMethodSelectItems;
  private String changeReviewMethodFrom;
  private String changeReviewMethodTo;

  // Keywords add
  private String changePublicationKeywordsAddInput;

  // Keywords replace
  private List<SelectItem> changePublicationKeywordsReplaceTypeSelectItems;
  private ReplaceType changePublicationKeywordsReplaceType;
  private boolean disabledKeywordInput;
  private String changePublicationKeywordsReplaceFrom;
  private String changePublicationKeywordsReplaceTo;

  // Genre source
  private ArrayList<SelectItem> changeSourceGenreSelectItems;
  private String changeSourceGenreFrom;
  private String changeSourceGenreTo;

  // Output source number
  private List<SelectItem> changeSourceNumberSelectItems;
  private String changeSoureEditionNumber;
  private String inputChangeSourceEdition;

  // Identifier add
  private String changeSoureIdAddNumber;
  private List<SelectItem> changeSourceIdTypeSelectItems;
  private String changeSourceIdTypeAdd;
  private String changeSourceIdAdd;

  // Identifier replace
  private String changeSourceIdReplaceNumber;
  private String changeSourceIdTypeReplace;
  private String changeSourceIdReplaceFrom;
  private String changeSourceIdReplaceTo;

  /**
   * The number that represents the difference between the real number of items in the batch
   * environment and the number that is displayed. These might differ due to the problem that items
   * can change their state and are then not retrieved by the filter any more. In this case, this
   * number is adapted to the number of items retrieved via the filter query.
   */
  private int diffDisplayNumber = 0;

  public PubItemBatchSessionBean() {
    this.init();
    this.initFields();
  }

  public void init() {
    this.pubItemListSessionBean = FacesTools.findBean("PubItemListSessionBean");
    this.internationalizationHelper = this.getI18nHelper();
    this.storedPubItems = new HashMap<>();
  }

  public void initFields() {
    // Contexts
    this.contextSelectItems = new ArrayList<>();
    this.selectedContextOld = null;
    this.selectedContextNew = null;

    // Local tags
    this.localTagsToAdd = new ArrayList<>();
    this.localTagsToAdd.add("");
    this.inputChangeLocalTagsReplaceFrom = null;
    this.inputChangeLocalTagsReplaceTo = null;

    // Genres
    final ContextListSessionBean clsb = FacesTools.findBean("ContextListSessionBean");
    final List<PubContextVOPresentation> contextVOList = clsb.getModeratorContextList();
    List<MdsPublicationVO.Genre> allowedGenresForContext = new ArrayList<>();
    List<MdsPublicationVO.Genre> allowedGenres = new ArrayList<>();
    MdsPublicationVO.Genre genreToCheck = null;
    this.changeGenreSelectItems = new ArrayList<>();
    for (PubContextVOPresentation pubContextVOPresentation : contextVOList) {
      String workflow = "null";
      if (null != pubContextVOPresentation.getWorkflow()) {
        workflow = pubContextVOPresentation.getWorkflow().toString();
      }
      this.contextSelectItems
          .add(new SelectItem(pubContextVOPresentation.getObjectId(), pubContextVOPresentation.getName() + " -- " + workflow));
      allowedGenresForContext = pubContextVOPresentation.getAllowedGenres();
      for (MdsPublicationVO.Genre genre : allowedGenresForContext) {
        genreToCheck = genre;
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
    this.changeGenreFrom = null;
    this.changeGenreTo = null;

    this.changeGenreThesisTypeSelectItems = new ArrayList<>(Arrays.asList(this.getI18nHelper().getSelectItemsDegreeType(true)));
    this.changeGenreThesisType = null;
    this.showThesisType = false;

    // Visibility
    this.changeFilesVisibilitySelectItems = new ArrayList<>(Arrays.asList(this.getI18nHelper().getSelectItemsVisibility(false)));
    this.changeFilesVisibilityFrom = null;
    this.changeFilesVisibilityTo = null;

    // Content category files
    this.changeFilesContentCategorySelectItems = new ArrayList<>(Arrays.asList(this.getI18nHelper().getSelectItemsContentCategory(true)));
    this.changeFilesContentCategoryFrom = null;
    this.changeFilesContentCategoryTo = null;

    // IP Range
    this.changeFilesAudienceSelectItems = new ArrayList<>();
    for (IpListProvider.IpRange ipRange : ApplicationBean.INSTANCE.getIpListProvider().getAll()) {
      this.changeFilesAudienceSelectItems.add(new SelectItem(ipRange.getId(), ipRange.getName()));
    }

    this.changeFilesAudienceSelectItems.sort(Comparator.comparing(SelectItem::getLabel));

    this.ipRangeToAdd = new ArrayList<>();
    this.ipRangeToAdd.add("");

    // Content category external references
    this.changeExternalReferencesContentCategorySelectItems =
        new ArrayList<>(Arrays.asList(this.getI18nHelper().getSelectItemsContentCategory(true)));
    this.changeExternalReferencesContentCategoryFrom = null;
    this.changeExternalReferencesContentCategoryTo = null;

    // Orcid
    this.criterion = new PersonSearchCriterion(SearchCriterionBase.SearchCriterion.ANYPERSON);
    this.orcid = null;

    // Review
    this.changeReviewMethodSelectItems = new ArrayList<>(Arrays.asList(this.getI18nHelper().getSelectItemsReviewMethod(true)));
    this.changeReviewMethodFrom = null;
    this.changeReviewMethodTo = null;

    // Keywords add
    this.changePublicationKeywordsAddInput = null;

    // Keywords replace
    this.changePublicationKeywordsReplaceTypeSelectItems = new ArrayList<>();
    this.changePublicationKeywordsReplaceTypeSelectItems.add(new SelectItem(ReplaceType.REPLACE_ALL, "ALL"));
    this.changePublicationKeywordsReplaceTypeSelectItems.add(new SelectItem(ReplaceType.REPLACE_BY_VALUE, "SPECIFIC VALUE"));
    this.changePublicationKeywordsReplaceType = null;
    this.changePublicationKeywordsReplaceFrom = null;
    this.changePublicationKeywordsReplaceTo = null;
    this.disabledKeywordInput = true;

    // Genre source
    final Map<String, String> excludedSourceGenres = ApplicationBean.INSTANCE.getExcludedSourceGenreMap();
    this.changeSourceGenreSelectItems = new ArrayList<>();
    this.changeSourceGenreSelectItems.add(new SelectItem("", this.getLabel("BatchWorkspace_lblNoItemsSet")));
    for (final SourceVO.Genre value : SourceVO.Genre.values()) {
      this.changeSourceGenreSelectItems.add(new SelectItem(value, this.getLabel("ENUM_GENRE_" + value.name())));
    }
    String uri = "";
    int i = 0;
    while (i < this.changeSourceGenreSelectItems.size()) {
      if (null != this.changeSourceGenreSelectItems.get(i).getValue()
          && !("").equals(this.changeSourceGenreSelectItems.get(i).getValue())) {
        uri = ((SourceVO.Genre) this.changeSourceGenreSelectItems.get(i).getValue()).getUri();
      }
      if (excludedSourceGenres.containsValue(uri)) {
        this.changeSourceGenreSelectItems.remove(i);
      } else {
        i++;
      }
    }
    this.changeSourceGenreSelectItems.toArray(new SelectItem[0]);
    this.changeSourceGenreFrom = null;
    this.changeSourceGenreTo = null;

    // Output source number
    this.changeSourceNumberSelectItems = new ArrayList<>();
    this.changeSourceNumberSelectItems.add(new SelectItem("1", "1"));
    this.changeSourceNumberSelectItems.add(new SelectItem("2", "2"));
    this.changeSoureEditionNumber = null;
    this.inputChangeSourceEdition = null;

    // Identifier
    this.changeSourceIdTypeSelectItems = Arrays.asList(getIdentifierTypes());

    // Identifier add
    this.changeSoureIdAddNumber = null;
    this.changeSourceIdTypeAdd = null;
    this.changeSourceIdAdd = null;

    // Identifier replace
    this.changeSourceIdReplaceNumber = null;
    this.changeSourceIdTypeReplace = null;
    this.changeSourceIdReplaceFrom = null;
    this.changeSourceIdReplaceTo = null;
  }

  @PreDestroy
  public void preDestroy() {
    this.getI18nHelper().removeLanguageChangeObserver(this);
  }

  @PostConstruct
  private void postConstruct() {
    this.batchProcessLog = this.pubItemBatchService.getBatchProcessLogForCurrentUser(this.loginHelper.getAccountUser());
    this.getI18nHelper().addLanguageChangeObserver(this);
  }

  @Override
  public void languageChanged(String oldLang, String newLang) {
    this.initFields();
  }

  public BatchProcessLogDbVO getBatchProcessLog() {
    return this.batchProcessLog;
  }

  public void setBatchProcessLog(BatchProcessLogDbVO batchProcessLog) {
    this.batchProcessLog = batchProcessLog;
  }

  public int getBatchPubItemsSize() {
    return this.storedPubItems.size();
  }

  public String getChangeExternalReferencesContentCategoryFrom() {
    return this.changeExternalReferencesContentCategoryFrom;
  }

  public void setChangeExternalReferencesContentCategoryFrom(String changeExternalReferencesContentCategoryFrom) {
    this.changeExternalReferencesContentCategoryFrom = changeExternalReferencesContentCategoryFrom;
  }

  public ArrayList<SelectItem> getChangeExternalReferencesContentCategorySelectItems() {
    return this.changeExternalReferencesContentCategorySelectItems;
  }

  public void setChangeExternalReferencesContentCategorySelectItems(
      ArrayList<SelectItem> changeExternalReferencesContentCategorySelectItems) {
    this.changeExternalReferencesContentCategorySelectItems = changeExternalReferencesContentCategorySelectItems;
  }

  public String getChangeExternalReferencesContentCategoryTo() {
    return this.changeExternalReferencesContentCategoryTo;
  }

  public void setChangeExternalReferencesContentCategoryTo(String changeExternalReferencesContentCategoryTo) {
    this.changeExternalReferencesContentCategoryTo = changeExternalReferencesContentCategoryTo;
  }

  public ArrayList<SelectItem> getChangeFilesAudienceSelectItems() {
    return this.changeFilesAudienceSelectItems;
  }

  public void setChangeFilesAudienceSelectItems(ArrayList<SelectItem> changeFilesAudienceSelectItems) {
    this.changeFilesAudienceSelectItems = changeFilesAudienceSelectItems;
  }

  public String getChangeFilesContentCategoryFrom() {
    return this.changeFilesContentCategoryFrom;
  }

  public void setChangeFilesContentCategoryFrom(String changeFilesContentCategoryFrom) {
    this.changeFilesContentCategoryFrom = changeFilesContentCategoryFrom;
  }

  public String getChangeFilesContentCategoryTo() {
    return this.changeFilesContentCategoryTo;
  }

  public void setChangeFilesContentCategoryTo(String changeFilesContentCategoryTo) {
    this.changeFilesContentCategoryTo = changeFilesContentCategoryTo;
  }

  public ArrayList<SelectItem> getChangeFilesContentCategorySelectItems() {
    return this.changeFilesContentCategorySelectItems;
  }

  public void setChangeFilesContentCategorySelectItems(ArrayList<SelectItem> changeFilesContentCategorySelectItems) {
    this.changeFilesContentCategorySelectItems = changeFilesContentCategorySelectItems;
  }

  public String getChangeFilesVisibilityFrom() {
    return this.changeFilesVisibilityFrom;
  }

  public void setChangeFilesVisibilityFrom(String changeFilesVisibilityFrom) {
    this.changeFilesVisibilityFrom = changeFilesVisibilityFrom;
  }

  public String getChangeFilesVisibilityTo() {
    return this.changeFilesVisibilityTo;
  }

  public void setChangeFilesVisibilityTo(String changeFilesVisibilityTo) {
    this.changeFilesVisibilityTo = changeFilesVisibilityTo;
  }

  public ArrayList<SelectItem> getChangeFilesVisibilitySelectItems() {
    return this.changeFilesVisibilitySelectItems;
  }

  public void setChangeFilesVisibilitySelectItems(ArrayList<SelectItem> changeFilesVisibilitySelectItems) {
    this.changeFilesVisibilitySelectItems = changeFilesVisibilitySelectItems;
  }

  public String getChangeGenreFrom() {
    return this.changeGenreFrom;
  }

  public void setChangeGenreFrom(String changeGenreFrom) {
    this.changeGenreFrom = changeGenreFrom;
  }

  public ArrayList<SelectItem> getChangeGenreSelectItems() {
    return this.changeGenreSelectItems;
  }

  public void setChangeGenreSelectItems(ArrayList<SelectItem> changeGenreSelectItems) {
    this.changeGenreSelectItems = changeGenreSelectItems;
  }

  public ArrayList<SelectItem> getChangeGenreThesisTypeSelectItems() {
    return this.changeGenreThesisTypeSelectItems;
  }

  public void setChangeGenreThesisTypeSelectItems(ArrayList<SelectItem> changeGenreThesisTypeSelectItems) {
    this.changeGenreThesisTypeSelectItems = changeGenreThesisTypeSelectItems;
  }

  public String getChangeGenreThesisType() {
    return this.changeGenreThesisType;
  }

  public void setChangeGenreThesisType(String changeGenreThesisType) {
    this.changeGenreThesisType = changeGenreThesisType;
  }

  public String getChangeGenreTo() {
    return this.changeGenreTo;
  }

  public ReplaceType getChangePublicationKeywordsReplaceType() {
    return this.changePublicationKeywordsReplaceType;
  }

  public void setChangePublicationKeywordsReplaceType(ReplaceType changePublicationKeywordsReplaceType) {
    this.changePublicationKeywordsReplaceType = changePublicationKeywordsReplaceType;
  }

  public List<SelectItem> getChangePublicationKeywordsReplaceTypeSelectItems() {
    return this.changePublicationKeywordsReplaceTypeSelectItems;
  }

  public void setChangePublicationKeywordsReplaceTypeSelectItems(List<SelectItem> changePublicationKeywordsReplaceTypeSelectItems) {
    this.changePublicationKeywordsReplaceTypeSelectItems = changePublicationKeywordsReplaceTypeSelectItems;
  }

  public String getChangePublicationKeywordsAddInput() {
    return this.changePublicationKeywordsAddInput;
  }

  public void setChangePublicationKeywordsAddInput(String changePublicationKeywordsAddInput) {
    this.changePublicationKeywordsAddInput = changePublicationKeywordsAddInput;
  }

  public String getChangePublicationKeywordsReplaceFrom() {
    return this.changePublicationKeywordsReplaceFrom;
  }

  public void setChangePublicationKeywordsReplaceFrom(String changePublicationKeywordsReplaceFrom) {
    this.changePublicationKeywordsReplaceFrom = changePublicationKeywordsReplaceFrom;
  }

  public String getChangePublicationKeywordsReplaceTo() {
    return this.changePublicationKeywordsReplaceTo;
  }

  public void setChangePublicationKeywordsReplaceTo(String changePublicationKeywordsReplaceTo) {
    this.changePublicationKeywordsReplaceTo = changePublicationKeywordsReplaceTo;
  }

  public String getChangeReviewMethodFrom() {
    return this.changeReviewMethodFrom;
  }

  public void setChangeReviewMethodFrom(String changeReviewMethodFrom) {
    this.changeReviewMethodFrom = changeReviewMethodFrom;
  }

  public ArrayList<SelectItem> getChangeReviewMethodSelectItems() {
    return this.changeReviewMethodSelectItems;
  }

  public void setChangeReviewMethodSelectItems(ArrayList<SelectItem> changeReviewMethodSelectItems) {
    this.changeReviewMethodSelectItems = changeReviewMethodSelectItems;
  }

  public String getChangeReviewMethodTo() {
    return this.changeReviewMethodTo;
  }

  public void setChangeReviewMethodTo(String changeReviewMethodTo) {
    this.changeReviewMethodTo = changeReviewMethodTo;
  }

  public String getChangeSourceGenreFrom() {
    return this.changeSourceGenreFrom;
  }

  public void setChangeSourceGenreFrom(String changeSourceGenreFrom) {
    this.changeSourceGenreFrom = changeSourceGenreFrom;
  }

  public String getInputChangeSourceEdition() {
    return this.inputChangeSourceEdition;
  }

  public boolean getDisabledKeywordInput() {
    return this.disabledKeywordInput;
  }

  public void setDisabledKeywordInput(boolean disabledKeywordInput) {
    this.disabledKeywordInput = disabledKeywordInput;
  }

  public void setInputChangeSourceEdition(String inputChangeSourceEdition) {
    this.inputChangeSourceEdition = inputChangeSourceEdition;
  }

  public ArrayList<SelectItem> getChangeSourceGenreSelectItems() {
    return this.changeSourceGenreSelectItems;
  }

  public void setChangeSourceGenreSelectItems(ArrayList<SelectItem> changeSourceGenreSelectItems) {
    this.changeSourceGenreSelectItems = changeSourceGenreSelectItems;
  }

  public String getChangeSourceGenreTo() {
    return this.changeSourceGenreTo;
  }

  public void setChangeSourceGenreTo(String changeSourceGenreTo) {
    this.changeSourceGenreTo = changeSourceGenreTo;
  }

  public void setChangeGenreTo(String changeGenreTo) {
    this.changeGenreTo = changeGenreTo;
  }

  public String getChangeSoureIdAddNumber() {
    return this.changeSoureIdAddNumber;
  }

  public void setChangeSoureIdAddNumber(String changeSoureIdAddNumber) {
    this.changeSoureIdAddNumber = changeSoureIdAddNumber;
  }

  public String getChangeSourceIdAdd() {
    return this.changeSourceIdAdd;
  }

  public void setChangeSourceIdAdd(String changeSourceIdAdd) {
    this.changeSourceIdAdd = changeSourceIdAdd;
  }

  public String getChangeSourceIdReplaceFrom() {
    return this.changeSourceIdReplaceFrom;
  }

  public void setChangeSourceIdReplaceFrom(String changeSourceIdReplaceFrom) {
    this.changeSourceIdReplaceFrom = changeSourceIdReplaceFrom;
  }

  public String getChangeSourceIdReplaceTo() {
    return this.changeSourceIdReplaceTo;
  }

  public void setChangeSourceIdReplaceTo(String changeSourceIdReplaceTo) {
    this.changeSourceIdReplaceTo = changeSourceIdReplaceTo;
  }

  public String getChangeSourceIdReplaceNumber() {
    return this.changeSourceIdReplaceNumber;
  }

  public void setChangeSourceIdReplaceNumber(String changeSourceIdReplaceNumber) {
    this.changeSourceIdReplaceNumber = changeSourceIdReplaceNumber;
  }

  public String getChangeSourceIdTypeReplace() {
    return this.changeSourceIdTypeReplace;
  }

  public void setChangeSourceIdTypeReplace(String changeSourceIdTypeReplace) {
    this.changeSourceIdTypeReplace = changeSourceIdTypeReplace;
  }

  public String getChangeSourceIdTypeAdd() {
    return this.changeSourceIdTypeAdd;
  }

  public void setChangeSourceIdTypeAdd(String changeSourceIdTypeAdd) {
    this.changeSourceIdTypeAdd = changeSourceIdTypeAdd;
  }

  public List<SelectItem> getChangeSourceIdTypeSelectItems() {
    return this.changeSourceIdTypeSelectItems;
  }

  public void setChangeSourceIdTypeSelectItems(List<SelectItem> changeSourceIdTypeSelectItems) {
    this.changeSourceIdTypeSelectItems = changeSourceIdTypeSelectItems;
  }

  public String getChangeSoureEditionNumber() {
    return this.changeSoureEditionNumber;
  }

  public void setChangeSoureEditionNumber(String changeSoureEditionSoure) {
    this.changeSoureEditionNumber = changeSoureEditionSoure;
  }

  public List<SelectItem> getChangeSourceNumberSelectItems() {
    return this.changeSourceNumberSelectItems;
  }

  public void setChangeSourceNumberSelectItems(List<SelectItem> changeSourceEditionSourceSelectItems) {
    this.changeSourceNumberSelectItems = changeSourceEditionSourceSelectItems;
  }

  public ArrayList<SelectItem> getContextSelectItems() {
    return this.contextSelectItems;
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

  public String getInputChangeLocalTagsReplaceFrom() {
    return this.inputChangeLocalTagsReplaceFrom;
  }

  public void setInputChangeLocalTagsReplaceFrom(String inputChangeLocalTagsReplaceFrom) {
    this.inputChangeLocalTagsReplaceFrom = inputChangeLocalTagsReplaceFrom;
  }

  public String getInputChangeLocalTagsReplaceTo() {
    return this.inputChangeLocalTagsReplaceTo;
  }

  public void setInputChangeLocalTagsReplaceTo(String inputChangeLocalTagsReplaceTo) {
    this.inputChangeLocalTagsReplaceTo = inputChangeLocalTagsReplaceTo;
  }


  public List<String> getIpRangeToAdd() {
    return this.ipRangeToAdd;
  }

  public void setIpRangeToAdd(List<String> localTagsToAdd) {
    this.ipRangeToAdd = localTagsToAdd;
  }

  public String getItemLink(String itemId, int itemVersion) {
    try {
      return CommonUtils.getGenericItemLink(itemId, itemVersion);
    } catch (Exception e) {
      logger.error("Error getting log item link", e);
    }
    return "";
  }

  public SearchCriterionBase getCriterion() {
    return this.criterion;
  }

  public String getOrcid() {
    return this.orcid;
  }

  public void setOrcid(String orcid) {
    this.orcid = orcid;
  }

  public List<String> getLocalTagsToAdd() {
    return this.localTagsToAdd;
  }

  public void setLocalTagsToAdd(List<String> localTagsToAdd) {
    this.localTagsToAdd = localTagsToAdd;
  }

  public LoginHelper getLoginHelper() {
    return this.loginHelper;
  }

  public void setLoginHelper(LoginHelper loginHelper) {
    this.loginHelper = loginHelper;
  }

  public int getProcessLogErrorCount() {
    int batchErrorCount = 0;
    for (BatchProcessItemVO batchItem : this.batchProcessLog.getBatchProcessLogItemList()) {
      if (BatchProcessItemVO.BatchProcessMessagesTypes.ERROR.equals(batchItem.batchProcessMessageType)) {
        batchErrorCount++;
      }
    }
    return batchErrorCount;
  }

  public int getProcessLogSuccessCount() {
    int batchSuccessCount = 0;
    for (BatchProcessItemVO batchItem : this.batchProcessLog.getBatchProcessLogItemList()) {
      if (BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS.equals(batchItem.batchProcessMessageType)) {
        batchSuccessCount++;
      }
    }
    return batchSuccessCount;
  }


  public int getProcessLogWarningCount() {
    int batchSuccessCount = 0;
    for (BatchProcessItemVO batchItem : this.batchProcessLog.getBatchProcessLogItemList()) {
      if (BatchProcessItemVO.BatchProcessMessagesTypes.WARNING.equals(batchItem.batchProcessMessageType)) {
        batchSuccessCount++;
      }
    }
    return batchSuccessCount;
  }

  public int getProcessLogTotalCount() {
    return this.batchProcessLog.getBatchProcessLogItemList().size();
  }

  public PubItemBatchService getPubItemBatchService() {
    return this.pubItemBatchService;
  }

  public void setPubItemBatchService(PubItemBatchService pubItemBatchService) {
    this.pubItemBatchService = pubItemBatchService;
  }

  public String getSelectedContextNew() {
    return this.selectedContextNew;
  }

  public void setSelectedContextNew(String selectedContextNew) {
    this.selectedContextNew = selectedContextNew;
  }

  public String getSelectedContextOld() {
    return this.selectedContextOld;
  }

  public void setSelectedContextOld(String selectedContextOld) {
    this.selectedContextOld = selectedContextOld;
  }

  public InternationalizationHelper getI18nHelper() {
    return FacesTools.findBean("InternationalizationHelper");
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
    final ArrayList<SelectItem> selectItemList = new ArrayList<>();

    // constants for comboBoxes
    selectItemList.add(new SelectItem(null, this.getLabel("EditItem_NO_ITEM_SET")));

    for (final IdentifierVO.IdType type : DisplayTools.getIdTypesToDisplay()) {
      selectItemList.add(new SelectItem(type.toString(), this.getLabel("ENUM_IDENTIFIERTYPE_" + type)));
    }

    // Sort identifiers alphabetically
    selectItemList.sort(Comparator.comparing(o -> o.getLabel().toLowerCase()));

    return selectItemList.toArray(new SelectItem[] {});
  }

  /**
   * adding an IP range in the IP range presentation list
   */
  public void addIpRange() {
    this.ipRangeToAdd.add("");
  }

  /**
   * remove an IP range in the IP range presentation list
   *
   * @param index
   */
  public void removeIpRange(int index) {
    this.ipRangeToAdd.remove(index);
  }


  /**
   * adding a local tag to the local tag presentation list
   */
  public void addLocalTag() {
    this.localTagsToAdd.add("");
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

    if (null != this.localTagsToAdd && !this.localTagsToAdd.isEmpty()) {
      List<String> pubItemObjectIdList = new ArrayList<>();
      for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
        pubItemObjectIdList.add(entry.getValue().getObjectId());
      }
      this.batchProcessLog = this.pubItemBatchService.addLocalTags(pubItemObjectIdList, this.localTagsToAdd,
          "batch add local tags " + formatter.format(calendar.getTime()), this.loginHelper.getAuthenticationToken(),
          this.loginHelper.getAccountUser());
      writeSuccessAndErrorMessages();
      this.pubItemListSessionBean.changeSubmenuToProcessLog();
    } else {
      warn(this.internationalizationHelper.getMessage(BatchMessages.NO_VALUE_SET.getMessage()));
    }

    return null;
  }

  public String changeContextItemList() {
    logger.info("trying to change context for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = this.pubItemBatchService.changeContext(pubItemObjectIdList, this.selectedContextOld, this.selectedContextNew,
        "batch change context " + formatter.format(calendar.getTime()), this.loginHelper.getAuthenticationToken(),
        this.loginHelper.getAccountUser());
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String changeExternalReferenceContentCategoryItemList() {
    logger.info("trying to change the external reference content category for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    if (null != this.changeExternalReferencesContentCategoryFrom && null != this.changeExternalReferencesContentCategoryTo) {
      for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
        pubItemObjectIdList.add(entry.getValue().getObjectId());
      }
      this.batchProcessLog = this.pubItemBatchService.changeExternalReferenceContentCategory(pubItemObjectIdList,
          this.changeExternalReferencesContentCategoryFrom, this.changeExternalReferencesContentCategoryTo,
          "batch change external references content category " + formatter.format(calendar.getTime()),
          this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
    } else {
      error(this.internationalizationHelper.getMessage("batch_ErrorMissingValues"));
      return null;
    }
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String changeOrcid() {
    logger.info("trying to change the ORCID ID " + this.getBatchPubItemsSize() + " items");
    final SearchCriterionBase sc = this.criterion;
    final StringOrHiddenIdSearchCriterion hiddenSc = (StringOrHiddenIdSearchCriterion) sc;
    if (ValidationTools.isEmpty(hiddenSc.getHiddenId())) {
      error(this.internationalizationHelper.getMessage("batch_ErrorMissingValues"));
      return null;
    }
    if (ValidationTools.isEmpty(this.orcid)) {
      error(this.internationalizationHelper.getMessage("batch_ErrorNoOrcid").replace("$2", hiddenSc.getSearchString()));
      return null;
    }
    if (!this.orcid.startsWith(ValidationTools.ORCID_HTTPS)
        || (!this.orcid.substring(ValidationTools.ORCID_HTTPS.length()).matches(ValidationTools.ORCID_REGEX))) {
      error(this.internationalizationHelper.getMessage("batch_ErrorInvalidOrcid").replace("$1", this.orcid).replace("$2",
          hiddenSc.getSearchString()));
      return null;
    }

    List<String> pubItemObjectIdList = new ArrayList<>();

    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }

    this.batchProcessLog = this.pubItemBatchService.changeOrcid(pubItemObjectIdList, hiddenSc.getHiddenId(), this.orcid,
        "batch change orcid ", this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());

    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String changeFileAudienceItemList() {
    logger.info("trying to change the file audience for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = this.pubItemBatchService.changeFileAudience(pubItemObjectIdList, this.ipRangeToAdd,
        "batch change file audience " + formatter.format(calendar.getTime()), this.loginHelper.getAuthenticationToken(),
        this.loginHelper.getAccountUser());
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String changeFileContentCategoryItemList() {
    logger.info("trying to change the file content category for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    if (null != this.changeFilesContentCategoryFrom && null != this.changeFilesContentCategoryTo) {
      for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
        pubItemObjectIdList.add(entry.getValue().getObjectId());
      }
      this.batchProcessLog = this.pubItemBatchService.changeFileContentCategory(pubItemObjectIdList, this.changeFilesContentCategoryFrom,
          this.changeFilesContentCategoryTo, "batch change file content category " + formatter.format(calendar.getTime()),
          this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
    } else {
      error(this.internationalizationHelper.getMessage("batch_ErrorMissingValues"));
      return null;
    }
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String changeFileVisibilityItemList() {
    logger.info("trying to change the file visibility for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = this.pubItemBatchService.changeFileVisibility(pubItemObjectIdList,
        FileVO.Visibility.valueOf(this.changeFilesVisibilityFrom), FileVO.Visibility.valueOf(this.changeFilesVisibilityTo),
        this.loginHelper.getCurrentIp(), "batch change file visibility " + formatter.format(calendar.getTime()),
        this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String changeGenreItemList() {
    logger.info("trying to change genre for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    MdsPublicationVO.Genre genreOld = MdsPublicationVO.Genre.valueOf(this.changeGenreFrom);
    MdsPublicationVO.Genre genreNew = MdsPublicationVO.Genre.valueOf(this.changeGenreTo);
    MdsPublicationVO.DegreeType degree = null;
    if (null != this.changeGenreThesisType) {
      degree = MdsPublicationVO.DegreeType.valueOf(this.changeGenreThesisType);
    }
    this.batchProcessLog = this.pubItemBatchService.changeGenre(pubItemObjectIdList, genreOld, genreNew, degree,
        "batch change genre " + formatter.format(calendar.getTime()), this.loginHelper.getAuthenticationToken(),
        this.loginHelper.getAccountUser());
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String changePublicationKeywordsAddItemList() {
    logger.info("trying to add keywords for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    if (null != this.changePublicationKeywordsAddInput && !this.changePublicationKeywordsAddInput.trim().isEmpty()) {
      List<String> pubItemObjectIdList = new ArrayList<>();
      for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
        pubItemObjectIdList.add(entry.getValue().getObjectId());
      }
      this.batchProcessLog = this.pubItemBatchService.addKeywords(pubItemObjectIdList, this.changePublicationKeywordsAddInput,
          "batch add keywords method " + formatter.format(calendar.getTime()), this.loginHelper.getAuthenticationToken(),
          this.loginHelper.getAccountUser());
      writeSuccessAndErrorMessages();
      this.pubItemListSessionBean.changeSubmenuToProcessLog();
    } else {
      warn(this.internationalizationHelper.getMessage(BatchMessages.NO_VALUE_SET.getMessage()));
    }

    return null;
  }

  public String changePublicationKeywordsReplaceItemList() {
    logger.info("trying to Keywords method for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    if (ReplaceType.REPLACE_BY_VALUE.equals(this.changePublicationKeywordsReplaceType)) {
      this.batchProcessLog = this.pubItemBatchService.changeKeywords(pubItemObjectIdList, this.changePublicationKeywordsReplaceFrom,
          this.changePublicationKeywordsReplaceTo, "batch change keywords " + formatter.format(calendar.getTime()),
          this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
    } else {
      this.batchProcessLog = this.pubItemBatchService.replaceAllKeywords(pubItemObjectIdList, this.changePublicationKeywordsReplaceTo,
          "batch replace keywords " + formatter.format(calendar.getTime()), this.loginHelper.getAuthenticationToken(),
          this.loginHelper.getAccountUser());
    }
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String changeReviewMethodItemList() {
    logger.info("trying to change review method for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = this.pubItemBatchService.changeReviewMethod(pubItemObjectIdList, this.changeReviewMethodFrom,
        this.changeReviewMethodTo, "batch change review method " + formatter.format(calendar.getTime()),
        this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String changeSourceGenreItemList() {
    logger.info("trying to change source genre for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    if (null != this.changeSourceGenreFrom && null != this.changeSourceGenreTo) {
      for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
        pubItemObjectIdList.add(entry.getValue().getObjectId());
      }
      this.batchProcessLog =
          this.pubItemBatchService.changeSourceGenre(pubItemObjectIdList, SourceVO.Genre.valueOf(this.changeSourceGenreFrom),
              SourceVO.Genre.valueOf(this.changeSourceGenreTo), "batch change source genre " + formatter.format(calendar.getTime()),
              this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
    } else {
      error(this.internationalizationHelper.getMessage("batch_ErrorMissingValues"));
      return null;
    }
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String addSourceIdItemList() {
    logger.info("trying to replacing the source id for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    if (null != this.changeSourceIdAdd && !this.changeSourceIdAdd.trim().isEmpty() && null != this.changeSourceIdTypeAdd) {
      List<String> pubItemObjectIdList = new ArrayList<>();
      for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
        pubItemObjectIdList.add(entry.getValue().getObjectId());
      }
      this.batchProcessLog = this.pubItemBatchService.addSourceId(pubItemObjectIdList, this.changeSoureIdAddNumber,
          IdentifierVO.IdType.valueOf(this.changeSourceIdTypeAdd), this.changeSourceIdAdd,
          "batch add source id " + formatter.format(calendar.getTime()), this.loginHelper.getAuthenticationToken(),
          this.loginHelper.getAccountUser());
      writeSuccessAndErrorMessages();
      this.pubItemListSessionBean.changeSubmenuToProcessLog();
    } else {
      warn(this.internationalizationHelper.getMessage(BatchMessages.NO_VALUE_SET.getMessage()));
    }

    return null;
  }

  public String changeSourceIdReplaceItemList() {
    logger.info("trying to replace source id for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    if (null != this.changeSourceIdReplaceFrom && !this.changeSourceIdReplaceFrom.trim().isEmpty()
        && null != this.changeSourceIdTypeReplace) {
      List<String> pubItemObjectIdList = new ArrayList<>();
      for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
        pubItemObjectIdList.add(entry.getValue().getObjectId());
      }
      this.batchProcessLog = this.pubItemBatchService.changeSourceIdReplace(pubItemObjectIdList, this.changeSourceIdReplaceNumber,
          IdentifierVO.IdType.valueOf(this.changeSourceIdTypeReplace), this.changeSourceIdReplaceFrom, this.changeSourceIdReplaceTo,
          "batch replace source id " + formatter.format(calendar.getTime()), this.loginHelper.getAuthenticationToken(),
          this.loginHelper.getAccountUser());
      writeSuccessAndErrorMessages();
      this.pubItemListSessionBean.changeSubmenuToProcessLog();
    } else {
      warn(this.internationalizationHelper.getMessage(BatchMessages.NO_VALUE_SET.getMessage()));
    }

    return null;
  }

  public String changeSourceEditionItemList() {
    logger.info("trying to change source genre for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog = this.pubItemBatchService.changeSourceEdition(pubItemObjectIdList, this.changeSoureEditionNumber,
        this.inputChangeSourceEdition, "batch change source edition " + formatter.format(calendar.getTime()),
        this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String deleteItemList() {
    logger.info("trying to batch delete " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog =
        this.pubItemBatchService.deletePubItems(pubItemObjectIdList, "batch delete " + formatter.format(calendar.getTime()),
            this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String releaseItemList() {
    logger.info("trying to batch release " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog =
        this.pubItemBatchService.releasePubItems(pubItemObjectIdList, "batch release " + formatter.format(calendar.getTime()),
            this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String replaceLocalTagsItemList() {
    logger.info("trying to change context for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    if (null != this.inputChangeLocalTagsReplaceFrom && !this.inputChangeLocalTagsReplaceFrom.trim().isEmpty()) {
      List<String> pubItemObjectIdList = new ArrayList<>();
      for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
        pubItemObjectIdList.add(entry.getValue().getObjectId());
      }
      this.batchProcessLog = this.pubItemBatchService.replaceLocalTags(pubItemObjectIdList, this.inputChangeLocalTagsReplaceFrom,
          this.inputChangeLocalTagsReplaceTo, "batch replacing local tags " + formatter.format(calendar.getTime()),
          this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
      this.pubItemListSessionBean.changeSubmenuToProcessLog();
      writeSuccessAndErrorMessages();
    } else {
      warn(this.internationalizationHelper.getMessage(BatchMessages.NO_VALUE_SET.getMessage()));
    }

    return null;
  }

  public String reviseItemList() {
    logger.info("trying to batch revise " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog =
        this.pubItemBatchService.revisePubItems(pubItemObjectIdList, "batch revise " + formatter.format(calendar.getTime()),
            this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String submitItemList() {
    logger.info("trying to batch submit " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog =
        this.pubItemBatchService.submitPubItems(pubItemObjectIdList, "batch submit " + formatter.format(calendar.getTime()),
            this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public String withdrawItemList() {
    logger.info("trying to batch withdraw " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    List<String> pubItemObjectIdList = new ArrayList<>();
    for (Map.Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemObjectIdList.add(entry.getValue().getObjectId());
    }
    this.batchProcessLog =
        this.pubItemBatchService.withdrawPubItems(pubItemObjectIdList, "batch withdraw " + formatter.format(calendar.getTime()),
            this.loginHelper.getAuthenticationToken(), this.loginHelper.getAccountUser());
    writeSuccessAndErrorMessages();
    this.pubItemListSessionBean.changeSubmenuToProcessLog();

    return null;
  }

  public void handleGenreThesisTypeChange() {
    if (MdsPublicationVO.Genre.THESIS.equals(MdsPublicationVO.Genre.valueOf(this.changeGenreTo))) {
      this.showThesisType = true;
    } else {
      this.showThesisType = false;
    }
  }

  public boolean getShowThesisType() {
    return this.showThesisType;
  }

  public void setShowThesisType(boolean showThesisType) {
    this.showThesisType = showThesisType;
  }

  public void handleKeywordReplaceTypeChange() {
    if (ReplaceType.REPLACE_ALL.equals(this.changePublicationKeywordsReplaceType)) {
      this.disabledKeywordInput = true;
    } else {
      this.disabledKeywordInput = false;
    }
  }

  public void writeSuccessAndErrorMessages() {
    if (0 < getProcessLogSuccessCount()) {
      info((this.internationalizationHelper.getMessage("batch_SuccesCount")).replace("$1", Integer.toString(getProcessLogSuccessCount())));
    }
    if (0 < getProcessLogErrorCount()) {
      error((this.internationalizationHelper.getMessage("batch_ErrorCount")).replace("$1", Integer.toString(getProcessLogErrorCount())));
    }
    if (0 < getProcessLogWarningCount()) {
      warn((this.internationalizationHelper.getMessage("batch_WarningCount")).replace("$1", Integer.toString(getProcessLogWarningCount())));
    }
  }

  public void removeAutoSuggestValues() {
    final SearchCriterionBase sc = this.criterion;
    final StringOrHiddenIdSearchCriterion hiddenSc = (StringOrHiddenIdSearchCriterion) sc;
    hiddenSc.setHiddenId(null);
    hiddenSc.setSearchString(null);
    this.orcid = null;
  }

  public enum ReplaceType
  {
    REPLACE_ALL,
    REPLACE_BY_VALUE
  }

  private enum BatchMessages implements Messages
  {
    NO_VALUE_SET("batch_ProcessLog_MetadataNoNewValueSet");

  /**
     * The message pattern. For syntax definition see {@link MessageFormat}.
     */
    private final String message;

  /**
     * Creates a new instance with the given message template.
     *
     * @param messageTemplate The message template
     */
    BatchMessages(String messageTemplate) {
      this.message = messageTemplate;
    }

  @Override
  public String getMessage() {
    return this.message;
  }
}}
