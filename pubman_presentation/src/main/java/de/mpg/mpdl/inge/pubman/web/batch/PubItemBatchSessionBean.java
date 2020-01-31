package de.mpg.mpdl.inge.pubman.web.batch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation.WrappedLocalTag;
import de.mpg.mpdl.inge.service.aa.IpListProvider;
import de.mpg.mpdl.inge.service.aa.IpListProvider.IpRange;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
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


  @Autowired
  private BatchItemsRetrieverRequestBean batchIrrB;
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
  private String changeSoureIssueSoure;
  private ArrayList<SelectItem> changeSourceIssueSourceSelectItems;
  private String inputChangeLocalTagsReplaceFrom;
  private String inputChangeLocalTagsReplaceTo;
  private String inputChangeLocalTagsAdd;
  private String inputChangeSourceIssue;
  private List<String> localTagsToAdd;
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

    // Instantiate  and fill source selection
    this.changeSourceIssueSourceSelectItems = new ArrayList<SelectItem>();
    this.changeSourceIssueSourceSelectItems.add(new SelectItem("1", "1"));
    this.changeSourceIssueSourceSelectItems.add(new SelectItem("2", "2"));
  }

  public List<String> getLocalTagsToAdd() {
    return localTagsToAdd;
  }

  public void setLocalTagsToAdd(List<String> localTagsToAdd) {
    this.localTagsToAdd = localTagsToAdd;
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

  public String getChangeSoureIssueSoure() {
    return changeSoureIssueSoure;
  }

  public void setChangeSoureIssueSoure(String changeSoureIssueSoure) {
    this.changeSoureIssueSoure = changeSoureIssueSoure;
  }

  public ArrayList<SelectItem> getChangeSourceIssueSourceSelectItems() {
    return changeSourceIssueSourceSelectItems;
  }

  public void setChangeSourceIssueSourceSelectItems(ArrayList<SelectItem> changeSourceIssueSourceSelectItems) {
    this.changeSourceIssueSourceSelectItems = changeSourceIssueSourceSelectItems;
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

  public String changeContextItemList() {
    logger.info("trying to change context for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.changeContext(pubItemsMap, selectedContextOld, selectedContextNew,
          "batch release " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch context change", e);
      this.error("A technichal error occoured during the batch context change");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch context change failed", e);
      this.error("Authentication for batch context change failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch context change failed", e);
      this.error("Authorization for batch context change failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch context change", e);
      this.error("An application error occoured during the batch context change");
    }
    return null;
  }

  public void addLocalTag() {
    this.localTagsToAdd.add(new String(""));
  }

  public void removeLocalTag(int index) {
    this.localTagsToAdd.remove(index);
  }

  public String addLocalTagsItemList() {
    logger.info("trying to add local tags for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.addLocalTags(pubItemsMap, localTagsToAdd, "batch add local tags " + formatter.format(calendar.getTime()),
          loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch process for adding local tags", e);
      this.error("A technichal error occoured during the batch process for adding local tags");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch adding local tags failed", e);
      this.error("Authentication for batch adding local tags failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch adding local tags failed", e);
      this.error("Authorization for batch adding local tags failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch adding local tags", e);
      this.error("An application error occoured during the batch adding local tags");
    }
    return null;
  }

  public String changeExternalRefereneceContentCategoryItemList() {
    logger.info("trying to change the external reference content category for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.changeExternalRefereneceContentCategory(pubItemsMap, changeExternalReferencesContentCategoryFrom,
          changeExternalReferencesContentCategoryTo,
          "batch change external references content category " + formatter.format(calendar.getTime()),
          loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch process for changing the external references content category", e);
      this.error("A technichal error occoured during the batch process for changing the external references content category");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch changing the external references content category failed", e);
      this.error("Authentication for batch changing the external references content category failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch changing the external references content category failed", e);
      this.error("Authorization for batch changing the external references content category failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch changing external references content category", e);
      this.error("An application error occoured during the batch changing external references content category");
    }
    return null;
  }

  public String changeFileAudienceItemList() {
    logger.info("trying to change the file audience for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.changeFileAudience(pubItemsMap, changeFilesAudienceFrom, changeFilesAudienceTo,
          "batch change file content category " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch process for changing the file audience", e);
      this.error("A technichal error occoured during the batch process for changing the file audience");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch changing the file audience failed", e);
      this.error("Authentication for batch changing the file audience failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch changing the file audience failed", e);
      this.error("Authorization for batch changing the file audience failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch changing file audience", e);
      this.error("An application error occoured during the batch changing file audience");
    }
    return null;
  }

  public String changeFileContentCategoryItemList() {
    logger.info("trying to change the file content category for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.changeFileContentCategory(pubItemsMap, changeFilesContentCategoryFrom, changeFilesContentCategoryTo,
          "batch change file content category " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch process for changing the file content category", e);
      this.error("A technichal error occoured during the batch process for changing the file content category");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch changing the file content category failed", e);
      this.error("Authentication for batch changing the file content category failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch changing the file content category failed", e);
      this.error("Authorization for batch changing the file content category failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch changing file content category", e);
      this.error("An application error occoured during the batch changing file content category");
    }
    return null;
  }

  public String changeFileVisibilityItemList() {
    logger.info("trying to change the file visibility for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.changeFileVisibility(pubItemsMap, Visibility.valueOf(changeFilesVisibilityFrom),
          Visibility.valueOf(changeFilesVisibilityTo), "batch change file visibility " + formatter.format(calendar.getTime()),
          loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch process for changing the file visibility", e);
      this.error("A technichal error occoured during the batch process for changing the file visibility");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch changing the file visibility failed", e);
      this.error("Authentication for batch changing the file visibility failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch changing the file visibility failed", e);
      this.error("Authorization for batch changing the file visibility failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch changing file visibility", e);
      this.error("An application error occoured during the batch changing file visibility");
    }
    return null;
  }

  public String changeGenreItemList() {
    logger.info("trying to change genre for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    Genre genreOld = Genre.valueOf(this.changeGenreFrom);
    Genre genreNew = Genre.valueOf(this.changeGenreTo);
    try {
      pubItemBatchService.changeGenre(pubItemsMap, genreOld, genreNew, "batch change genre " + formatter.format(calendar.getTime()),
          loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch process for changing the genre", e);
      this.error("A technichal error occoured during the batch process for changing the genre");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch changing the genre failed", e);
      this.error("Authentication for batch changing the genre failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch changing the genre failed", e);
      this.error("Authorization for batch changing the genre failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch changing genre", e);
      this.error("An application error occoured during the batch changing genre");
    }
    return null;
  }

  public String changeReviewMethodItemList() {
    logger.info("trying to change review method for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.changeReviewMethod(pubItemsMap, changeReviewMethodFrom, changeReviewMethodTo,
          "batch change review method " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch process for changing the review method", e);
      this.error("A technichal error occoured during the batch process for changing the review method");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch changing the review method failed", e);
      this.error("Authentication for batch changing the review method failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch changing the review method failed", e);
      this.error("Authorization for batch changing the review method failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch changing review method", e);
      this.error("An application error occoured during the batch changing review method");
    }
    return null;
  }

  public String changeSourceGenreItemList() {
    logger.info("trying to change source genre for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.changeSourceGenre(pubItemsMap, SourceVO.Genre.valueOf(this.changeSourceGenreFrom),
          SourceVO.Genre.valueOf(this.changeSourceGenreTo), "batch change source genre " + formatter.format(calendar.getTime()),
          loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch process for changing the source genre", e);
      this.error("A technichal error occoured during the batch process for changing the source genre");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch changing the source genre failed", e);
      this.error("Authentication for batch changing the source genre failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch changing the gsource enre failed", e);
      this.error("Authorization for batch changing the source genre failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch changingsource  genre", e);
      this.error("An application error occoured during the batch changing source genre");
    }
    return null;
  }

  public String changeSourceIssueItemList() {
    logger.info("trying to change source genre for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.changeSourceIssue(pubItemsMap, changeSoureIssueSoure, inputChangeSourceIssue,
          "batch change source genre " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch process for changing the source genre", e);
      this.error("A technichal error occoured during the batch process for changing the source genre");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch changing the source genre failed", e);
      this.error("Authentication for batch changing the source genre failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch changing the gsource enre failed", e);
      this.error("Authorization for batch changing the source genre failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch changingsource  genre", e);
      this.error("An application error occoured during the batch changing source genre");
    }
    return null;
  }

  public String deleteItemList() {
    logger.info("trying to batch delete " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.deletePubItems(pubItemsMap, "batch delete " + formatter.format(calendar.getTime()),
          loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch delete", e);
      this.error("A technichal error occoured during the batch delete");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch delete failed", e);
      this.error("Authentication for batch delete failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch delete failed", e);
      this.error("Authorization for batch delete failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch delete", e);
      this.error("An application error occoured during the batch delete");
    }
    return null;
  }

  public String releaseItemList() {
    logger.info("trying to batch release " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.releasePubItems(pubItemsMap, "batch release " + formatter.format(calendar.getTime()),
          loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch release", e);
      this.error("A technichal error occoured during the batch release");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch release failed", e);
      this.error("Authentication for batch release failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch release failed", e);
      this.error("Authorization for batch release failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch release", e);
      this.error("An application error occoured during the batch release");
    }
    return null;
  }

  public String replaceLocalTagsItemList() {
    logger.info("trying to change context for " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.replaceLocalTags(pubItemsMap, inputChangeLocalTagsReplaceFrom, inputChangeLocalTagsReplaceTo,
          "batch replacing local tags " + formatter.format(calendar.getTime()), loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch process for replacing local tags", e);
      this.error("A technichal error occoured during the batch process for replacing local tags");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch replacing local tags failed", e);
      this.error("Authentication for batch replacing local tags failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch replacing local tags failed", e);
      this.error("Authorization for batch replacing local tags failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch replacing local tags", e);
      this.error("An application error occoured during the batch replacing local tags");
    }
    return null;
  }

  public String reviseItemList() {
    logger.info("trying to batch revise " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.revisePubItems(pubItemsMap, "batch revise " + formatter.format(calendar.getTime()),
          loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch revise", e);
      this.error("A technichal error occoured during the batch revise");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch revise failed", e);
      this.error("Authentication for batch revise failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch revise failed", e);
      this.error("Authorization for batch revise failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch revise", e);
      this.error("An application error occoured during the batch revise");
    }
    return null;
  }

  public String submitItemList() {
    logger.info("trying to batch submit " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.submitPubItems(pubItemsMap, "batch submit " + formatter.format(calendar.getTime()),
          loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch submit", e);
      this.error("A technichal error occoured during the batch submit");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch submit failed", e);
      this.error("Authentication for batch submit failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch submit failed", e);
      this.error("Authorization for batch submit failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch submit", e);
      this.error("An application error occoured during the batch submit");
    }
    return null;
  }

  public String withdrawItemList() {
    logger.info("trying to batch withdraw " + this.getBatchPubItemsSize() + " items");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    System.out.println(formatter.format(calendar.getTime()));
    Map<String, Date> pubItemsMap = new HashMap<String, Date>();
    for (Entry<String, ItemVersionRO> entry : this.storedPubItems.entrySet()) {
      pubItemsMap.put((String) entry.getValue().getObjectId(), (Date) entry.getValue().getModificationDate());
    }
    try {
      pubItemBatchService.withdrawPubItems(pubItemsMap, "batch withdraw " + formatter.format(calendar.getTime()),
          loginHelper.getAuthenticationToken());
    } catch (IngeTechnicalException e) {
      logger.error("A technichal error occoured during the batch withdraw", e);
      this.error("A technichal error occoured during the batch withdraw");
    } catch (AuthenticationException e) {
      logger.error("Authentication for batch withdraw failed", e);
      this.error("Authentication for batch withdraw failed");
    } catch (AuthorizationException e) {
      logger.error("Authorization for batch withdraw failed", e);
      this.error("Authorization for batch withdraw failed");
    } catch (IngeApplicationException e) {
      logger.error("An application error occoured during the batch withdraw", e);
      this.error("An application error occoured during the batch withdraw");
    }
    return null;
  }

}
