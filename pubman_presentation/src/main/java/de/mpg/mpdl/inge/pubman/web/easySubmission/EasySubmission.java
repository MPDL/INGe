/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.inge.pubman.web.easySubmission;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.rmi.AccessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import de.mpg.mpdl.inge.dataacquisition.DataHandlerService;
import de.mpg.mpdl.inge.dataacquisition.DataSourceHandlerService;
import de.mpg.mpdl.inge.dataacquisition.DataaquisitionException;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.FullTextVO;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.valueobjects.AdminDescriptorVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItem;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.editItem.bean.IdentifierCollection;
import de.mpg.mpdl.inge.pubman.web.editItem.bean.SourceBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.PubFileVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;
import de.mpg.mpdl.inge.transformation.TransformationService;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ProxyHelper;

/**
 * Fragment class for the easy submission. This class provides all functionality for editing, saving
 * and submitting a PubItem within the easy submission process.
 * 
 * @author: Tobias Schraut, created 04.04.2008
 * @version: $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class EasySubmission extends FacesBean {
  public static final String BEAN_NAME = "EasySubmission";

  private static final Logger logger = Logger.getLogger(EasySubmission.class);

  public static final String LOAD_EASYSUBMISSION = "loadEasySubmission";
  public static final String INTERNAL_MD_FORMAT = "eSciDoc-publication-item";

  public SelectItem SUBMISSION_METHOD_MANUAL =
      new SelectItem("MANUAL", getLabel("easy_submission_method_manual"));
  public SelectItem SUBMISSION_METHOD_FETCH_IMPORT =
      new SelectItem("FETCH_IMPORT", getLabel("easy_submission_method_fetch_import"));
  public SelectItem[] SUBMISSION_METHOD_OPTIONS =
      new SelectItem[] {this.SUBMISSION_METHOD_MANUAL, this.SUBMISSION_METHOD_FETCH_IMPORT};
  public SelectItem DATE_CREATED =
      new SelectItem("DATE_CREATED", getLabel("easy_submission_lblDateCreated"));
  public SelectItem DATE_SUBMITTED =
      new SelectItem("DATE_SUBMITTED", getLabel("easy_submission_lblDateSubmitted"));
  public SelectItem DATE_ACCEPTED =
      new SelectItem("DATE_ACCEPTED", getLabel("easy_submission_lblDateAccepted"));
  public SelectItem DATE_PUBLISHED_IN_PRINT = new SelectItem("DATE_PUBLISHED_IN_PRINT",
      getLabel("easy_submission_lblDatePublishedInPrint"));
  public SelectItem DATE_PUBLISHED_ONLINE =
      new SelectItem("DATE_PUBLISHED_ONLINE", getLabel("easy_submission_lblDatePublishedOnline"));
  public SelectItem DATE_MODIFIED =
      new SelectItem("DATE_MODIFIED", getLabel("easy_submission_lblDateModified"));
  public SelectItem[] DATE_TYPE_OPTIONS =
      new SelectItem[] {this.DATE_CREATED, this.DATE_SUBMITTED, this.DATE_ACCEPTED,
          this.DATE_PUBLISHED_IN_PRINT, this.DATE_PUBLISHED_ONLINE, this.DATE_MODIFIED};

  private DataSourceHandlerService dataSourceHandler = new DataSourceHandlerService();
  private HtmlSelectOneMenu dateSelect;
  private HtmlSelectOneMenu genreSelect = new HtmlSelectOneMenu();
  private HtmlSelectOneRadio radioSelect;
  private HtmlSelectOneRadio radioSelectFulltext = new HtmlSelectOneRadio();
  private IdentifierCollection identifierCollection;
  private List<DataSourceVO> dataSources = new ArrayList<DataSourceVO>();
  private SelectItem[] EXTERNAL_SERVICE_OPTIONS;
  private SelectItem[] FULLTEXT_OPTIONS;
  private SelectItem[] REFERENCE_OPTIONS;
  private SelectItem[] locatorVisibilities;
  private String alternativeLanguageName;
  private String contextName = null;
  private String hiddenAlternativeTitlesField;
  private String hiddenIdsField;
  private String locatorUpload;
  private String selectedDate;
  private String serviceID;
  private String suggestConeUrl = null;
  private UploadedFile uploadedFile;
  private boolean overwriteCreators;

  public EasySubmission() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation.
   */
  public void init() {
    this.SUBMISSION_METHOD_MANUAL =
        new SelectItem("MANUAL", getLabel("easy_submission_method_manual"));
    this.SUBMISSION_METHOD_FETCH_IMPORT =
        new SelectItem("FETCH_IMPORT", getLabel("easy_submission_method_fetch_import"));
    this.SUBMISSION_METHOD_OPTIONS =
        new SelectItem[] {this.SUBMISSION_METHOD_MANUAL, this.SUBMISSION_METHOD_FETCH_IMPORT};
    this.locatorVisibilities = getI18nHelper().getSelectItemsVisibility(true);

    // if the user has reached Step 3, an item has already been created and must be set in the
    // EasySubmissionSessionBean for further manipulation
    if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep()
        .equals(EasySubmissionSessionBean.ES_STEP2)
        || this.getEasySubmissionSessionBean().getCurrentSubmissionStep()
            .equals(EasySubmissionSessionBean.ES_STEP3)) {

      if (this.getLocators() == null || this.getLocators().size() == 0) {
        String contentCategory = null;
        if (PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL") != null) {
          contentCategory = PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL");
        } else {
          Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
          if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
            contentCategory = contentCategoryMap.values().iterator().next();
          } else {
            error("There is no content category available.");
            Logger.getLogger(PubFileVOPresentation.class)
                .warn("WARNING: no content-category has been defined in Genres.xml");
          }
        }

        FileVO newLocator = new FileVO();
        newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
        newLocator.setContentCategory(contentCategory);
        newLocator.setVisibility(FileVO.Visibility.PUBLIC);
        newLocator.setDefaultMetadata(new MdsFileVO());
        this.getLocators().add(new PubFileVOPresentation(0, newLocator, true));
      }
    }

    if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep()
        .equals(EasySubmissionSessionBean.ES_STEP4)) {
      if (getItem().getMetadata() != null && getItem().getMetadata().getCreators() != null) {
        for (CreatorVO creatorVO : getItem().getMetadata().getCreators()) {
          if (creatorVO.getType() == CreatorType.PERSON && creatorVO.getPerson() == null) {
            creatorVO.setPerson(new PersonVO());
          } else if (creatorVO.getType() == CreatorType.ORGANIZATION
              && creatorVO.getOrganization() == null) {
            creatorVO.setOrganization(new OrganizationVO());
          }
        }
      }

      if (this.getEasySubmissionSessionBean().getCreators().size() == 0) {
        this.getEasySubmissionSessionBean()
            .bindCreatorsToBean(getItem().getMetadata().getCreators());
      }

      if (this.getEasySubmissionSessionBean().getCreatorOrganizations().size() == 0) {
        this.getEasySubmissionSessionBean().initOrganizationsFromCreators();
      }
    }

    if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep()
        .equals(EasySubmissionSessionBean.ES_STEP5)) {
      this.identifierCollection =
          new IdentifierCollection(this.getItem().getMetadata().getIdentifiers());
    }

    // Get informations about import sources if submission method = fetching import
    if ((this.getEasySubmissionSessionBean().getCurrentSubmissionStep()
        .equals(EasySubmissionSessionBean.ES_STEP2)
        || this.getEasySubmissionSessionBean().getCurrentSubmissionStep()
            .equals(EasySubmissionSessionBean.ES_STEP3))
        && this.getEasySubmissionSessionBean().getCurrentSubmissionMethod()
            .equals("FETCH_IMPORT")) {

      // Call source initialization only once
      if (!this.getEasySubmissionSessionBean().isImportSourceRefresh()) {
        this.getEasySubmissionSessionBean().setImportSourceRefresh(true);
        this.setImportSourcesInfo();
      } else if (this.getServiceID() != null
          && this.getServiceID().toLowerCase().equals("escidoc")) {
        this.getEasySubmissionSessionBean();
        this.getEasySubmissionSessionBean()
            .setRadioSelectFulltext(EasySubmissionSessionBean.FULLTEXT_ALL);
      }
    } else {
      this.getEasySubmissionSessionBean().setImportSourceRefresh(false);
    }

    if (getItem() != null && getItem().getMetadata() != null
        && getItem().getMetadata().getGenre() == null) {
      getItem().getMetadata().setGenre(Genre.ARTICLE);
    }
  }

  public String selectSubmissionMethod() {
    String submittedValue = CommonUtils.getUIValue(this.radioSelect);

    this.getEasySubmissionSessionBean().setCurrentSubmissionMethod(submittedValue);

    // select the default context if only one exists
    List<PubContextVOPresentation> depositorContextList = this.getDepositorContextList();

    if (depositorContextList != null && depositorContextList.size() == 1) {
      depositorContextList.get(0).setSelected(false);
      depositorContextList.get(0).selectForEasySubmission();
    }

    // set the current submission step to step2
    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);

    return null;
  }

  public String newEasySubmission() {
    this.setItem(null);

    this.getEasySubmissionSessionBean().cleanup();

    // also make sure that the EditItemSessionBean is cleaned, too
    this.getFiles().clear();
    this.getLocators().clear();

    // deselect the selected context
    List<PubContextVOPresentation> depositorContextList = this.getDepositorContextList();
    if (depositorContextList != null) {
      for (int i = 0; i < depositorContextList.size(); i++) {
        depositorContextList.get(i).setSelected(false);
      }
    }

    // set the current submission step to step2
    if (depositorContextList != null && depositorContextList.size() > 1) {
      // create a dummy item in the first context to avoid an empty item
      depositorContextList.get(0).selectForEasySubmission();
      this.getEasySubmissionSessionBean()
          .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
    } else { // Skip Collection selection for Import & Easy Sub if only one Collection
      depositorContextList.get(0).selectForEasySubmission();
      this.getEasySubmissionSessionBean()
          .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);
      this.init();
    }

    // set method to manual
    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionMethod(EasySubmissionSessionBean.SUBMISSION_METHOD_MANUAL);

    // set the current submission method for edit item to easy submission (for GUI purpose)
    this.getEditItemSessionBean()
        .setCurrentSubmission(EditItemSessionBean.SUBMISSION_METHOD_EASY_SUBMISSION);

    return "loadNewEasySubmission";
  }

  public String newImport() {
    this.setItem(null);

    this.getEasySubmissionSessionBean().cleanup();

    // also make sure that the EditItemSessionBean is cleaned, too
    this.getFiles().clear();
    this.getLocators().clear();

    // deselect the selected context
    List<PubContextVOPresentation> depositorContextList = this.getDepositorContextList();

    if (depositorContextList != null) {
      for (int i = 0; i < depositorContextList.size(); i++) {
        depositorContextList.get(i).setSelected(false);
      }
    }

    // set method to import
    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionMethod(EasySubmissionSessionBean.SUBMISSION_METHOD_FETCH_IMPORT);

    // set the current submission step to step2
    if (depositorContextList != null && depositorContextList.size() > 1) {
      this.getEasySubmissionSessionBean()
          .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
      // set the current submission method for edit item to import (for GUI purpose)
      this.getEditItemSessionBean()
          .setCurrentSubmission(EditItemSessionBean.SUBMISSION_METHOD_IMPORT);

      return "loadNewFetchMetadata";
    } else { // Skip Collection selection for Import & Easy Sub if only one Collection
      depositorContextList.get(0).selectForEasySubmission();
      this.getEasySubmissionSessionBean()
          .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);
      // set the current submission method for edit item to import (for GUI purpose)
      this.getEditItemSessionBean()
          .setCurrentSubmission(EditItemSessionBean.SUBMISSION_METHOD_IMPORT);

      this.init();

      return "loadNewFetchMetadata";
    }
  }

  /**
   * This method adds a file to the list of files of the item
   * 
   * @return navigation string (null)
   */
  public String addFile() {
    upload(true);
    saveLocator();

    List<PubFileVOPresentation> files = this.getFiles();

    if (files != null && files.size() > 0
        && files.get(files.size() - 1).getFile().getDefaultMetadata().getSize() > 0) {

      FileVO newFile = new FileVO();
      newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
      newFile.setVisibility(FileVO.Visibility.PUBLIC);
      newFile.setDefaultMetadata(new MdsFileVO());

      files.add(new PubFileVOPresentation(files.size(), newFile, false));
    }

    return "loadNewEasySubmission";
  }

  /**
   * This method adds a locator to the list of files of the item
   * 
   * @return navigation string (null)
   */
  public String addLocator() {
    upload(true);
    saveLocator();

    List<PubFileVOPresentation> locators = this.getLocators();

    if (locators != null && locators.get(locators.size() - 1).getFile().getContent() != null
        && !locators.get(locators.size() - 1).getFile().getContent().trim().equals("")) {

      String contentCategory = null;
      if (PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL") != null) {
        contentCategory = PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL");
      } else {
        Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
        if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
          contentCategory = contentCategoryMap.values().iterator().next();
        } else {
          error("There is no content category available.");
          Logger.getLogger(PubFileVOPresentation.class)
              .warn("WARNING: no content-category has been defined in Genres.xml");
        }
      }

      PubFileVOPresentation newLocator = new PubFileVOPresentation(locators.size(), true);
      newLocator.getFile().setContentCategory(contentCategory);
      newLocator.getFile().setVisibility(FileVO.Visibility.PUBLIC);
      newLocator.getFile().setDefaultMetadata(new MdsFileVO());
      locators.add(newLocator);
    }

    return "loadNewEasySubmission";
  }

  /**
   * This method binds the uploaded files to the files in the PubItem during the save process
   */
  private void bindUploadedFiles() {
    this.getItem().getFiles().clear();

    List<PubFileVOPresentation> files = this.getFiles();

    if (files != null && files.size() > 0) {
      for (int i = 0; i < files.size(); i++) {
        this.getItem().getFiles().add(files.get(i).getFile());
      }
    }

    List<PubFileVOPresentation> locators = this.getLocators();

    if (locators != null && locators.size() > 0) {
      for (int i = 0; i < locators.size(); i++) {
        this.getItem().getFiles().add(locators.get(i).getFile());
      }
    }
  }

  public String saveLocator() {
    List<PubFileVOPresentation> locators = this.getLocators();

    if (locators.get(locators.size() - 1).getFile().getDefaultMetadata().getTitle() == null
        || locators.get(locators.size() - 1).getFile().getDefaultMetadata().getTitle().trim()
            .equals("")) {
      locators.get(locators.size() - 1).getFile().getDefaultMetadata()
          .setTitle(locators.get(locators.size() - 1).getFile().getContent());
    }

    // set a dummy file size for rendering purposes
    if (locators.get(locators.size() - 1).getFile().getContent() != null
        && !locators.get(locators.size() - 1).getFile().getContent().trim().equals("")) {
      locators.get(locators.size() - 1).getFile().getDefaultMetadata().setSize(11);
    }

    // Visibility PUBLIC is static default value for locators
    locators.get(locators.size() - 1).getFile().setVisibility(Visibility.PUBLIC);

    return "loadNewEasySubmission";
  }

  /**
   * This method reorganizes the index property in PubFileVOPresentation after removing one element
   * of the list.
   */
  public void reorganizeFileIndexes() {
    List<PubFileVOPresentation> files = this.getFiles();

    if (files != null) {
      for (int i = 0; i < files.size(); i++) {
        files.get(i).setIndex(i);
      }
    }
  }

  /**
   * This method reorganizes the index property in PubFileVOPresentation after removing one element
   * of the list.
   */
  public void reorganizeLocatorIndexes() {
    List<PubFileVOPresentation> locators = this.getLocators();

    if (locators != null) {
      for (int i = 0; i < locators.size(); i++) {
        locators.get(i).setIndex(i);
      }
    }
  }

  /**
   * Saves the item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String save() {
    // bind the temporary uploaded files to the files in the current item
    bindUploadedFiles();
    parseAndSetAlternativeSourceTitlesAndIds();

    if (validate(ValidationPoint.STANDARD, "validate") == null) {
      return null;
    }

    ((EditItem) getRequestBean(EditItem.class)).setFromEasySubmission(true);

    String returnValue =
        this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM);

    if (returnValue != null && !"".equals(returnValue)) {
      this.getEasySubmissionSessionBean().cleanup();
    }

    this.getPubItemListSessionBean().update();

    return returnValue;
  }

  /**
   * Uploads a file
   * 
   * @param event
   */
  public void fileUploaded(FileUploadEvent event) {
    uploadedFile = event.getFile();
    upload(true);
  }

  public void bibtexFileUploaded(FileUploadEvent event) {
    this.getEasySubmissionSessionBean().setUploadedBibtexFile(event.getFile());
  }

  /**
   * This method uploads a selected file and gives out error messages if needed
   * 
   * @param needMessages Flag to invoke error messages (set it to false if you invoke the validation
   *        service before or after)
   * @return String navigation string
   * @author schraut
   */
  public String upload(boolean needMessages) {
    if (uploadedFile != null) {
      UploadedFile file = uploadedFile;
      StringBuffer errorMessage = new StringBuffer();
      if (file != null) {
        String contentURL = uploadFile(file);
        String fixedFileName = CommonUtils.fixURLEncoding(file.getFileName());
        if (contentURL != null && !contentURL.trim().equals("")) {
          FileVO newFile = new FileVO();
          newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
          newFile.setVisibility(FileVO.Visibility.PUBLIC);
          newFile.setDefaultMetadata(new MdsFileVO());

          this.getFiles().add(new PubFileVOPresentation(this.getFiles().size(), newFile, false));

          newFile.getDefaultMetadata().setTitle(fixedFileName);
          newFile.setName(fixedFileName);
          newFile.getDefaultMetadata().setSize((int) file.getSize());

          Tika tika = new Tika();
          try {
            InputStream fis = file.getInputstream();
            newFile.setMimeType(tika.detect(fis, fixedFileName));
            fis.close();
          } catch (IOException e) {
            logger.info("Error while trying to detect mimetype of file " + fixedFileName, e);
          }

          FormatVO formatVO = new FormatVO();
          formatVO.setType("dcterms:IMT");
          formatVO.setValue(newFile.getMimeType());
          newFile.getDefaultMetadata().getFormats().add(formatVO);
          newFile.setContent(contentURL);
        }

        this.init();
      }

      if (errorMessage.length() > 0) {
        error(errorMessage.toString());
      }
    }

    return "loadNewEasySubmission";
  }

  /**
   * Uploads a file to the FIZ Framework and recieves and returns the location of the file in the FW
   * 
   * @param file
   * @return
   */
  public String uploadFile(UploadedFile file) {
    String contentURL = "";

    if (file != null && file.getSize() > 0) {
      try {
        URL url =
            this.uploadFile(file, file.getContentType(), getLoginHelper().getESciDocUserHandle());
        if (url != null) {
          contentURL = url.toString();
        }
      } catch (Exception e) {
        logger.error("Could not upload file." + "\n" + e.toString());
        ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
        try {
          getExternalContext().redirect("ErrorPage.jsp");
        } catch (Exception ex) {
          logger.error(e.toString());
        }
        return ErrorPage.LOAD_ERRORPAGE;
      }
    }

    return contentURL;
  }

  /**
   * Uploads a file to the staging servlet and returns the corresponding URL.
   * 
   * @param uploadedFile The file to upload
   * @param mimetype The mimetype of the file
   * @param userHandle The userhandle to use for upload
   * @return The URL of the uploaded file.
   * @throws Exception If anything goes wrong...
   */
  protected URL uploadFile(UploadedFile uploadedFile, String mimetype, String userHandle)
      throws Exception {
    String fwUrl = PropertyReader.getFrameworkUrl();

    InputStream fis = uploadedFile.getInputstream();
    PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
    method.setRequestEntity(new InputStreamRequestEntity(fis));
    method.setRequestHeader("Content-Type", mimetype);
    method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);

    HttpClient client = new HttpClient();
    ProxyHelper.setProxy(client, fwUrl);
    client.executeMethod(method);

    String response = method.getResponseBodyAsString();

    fis.close();

    return XmlTransformingService.transformUploadResponseToFileURL(response);
  }

  /**
   * Uploads a file to the staging servlet and returns the corresponding URL.
   * 
   * @param InputStream to upload
   * @param mimetype The mimetype of the file
   * @param userHandle The userhandle to use for upload
   * @return The URL of the uploaded file.
   * @throws Exception If anything goes wrong...
   */
  protected URL uploadFile(InputStream in, String mimetype, String userHandle) throws Exception {
    String fwUrl = PropertyReader.getFrameworkUrl();

    PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
    method.setRequestEntity(new InputStreamRequestEntity(in));
    method.setRequestHeader("Content-Type", mimetype);
    method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);

    HttpClient client = new HttpClient();
    client.executeMethod(method);

    String response = method.getResponseBodyAsString();

    return XmlTransformingService.transformUploadResponseToFileURL(response);
  }

  public String uploadBibtexFile() {
    try {
      StringBuffer content = new StringBuffer();

      try {
        UploadedFile uploadedBibTexFile =
            this.getEasySubmissionSessionBean().getUploadedBibtexFile();

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(uploadedBibTexFile.getInputstream()));

        String line;
        while ((line = reader.readLine()) != null) {
          content.append(line + "\n");
        }
      } catch (NullPointerException npe) {
        logger.error("Error reading bibtex file", npe);
        warn(getMessage("easy_submission_bibtex_empty_file"));
        return null;
      }

      // Transform from bibtex to escidoc pubItem
      Format source = new Format("eSciDoc-publication-item", "application/xml", "*");
      Format target = new Format("html-meta-tags-highwire-press-citation", "text/html", "UTF-8");
      byte[] result = new TransformationService().transform(content.toString().getBytes("UTF-8"),
          source, target, "escidoc");

      PubItemVO itemVO = XmlTransformingService.transformToPubItem(new String(result));
      itemVO.setContext(getItem().getContext());

      // Check if reference has to be uploaded as file
      if (this.getEasySubmissionSessionBean().getRadioSelectReferenceValue()
          .equals(this.getEasySubmissionSessionBean().getREFERENCE_FILE())) {
        LocatorUploadBean locatorBean = new LocatorUploadBean();
        List<FileVO> locators = locatorBean.getLocators(itemVO);
        // Check if item has locators
        if (locators != null && locators.size() > 0) {
          // Upload the locators as file
          for (int i = 0; i < locators.size(); i++) {
            // Add files to item
            FileVO uploadedLocator = locatorBean.uploadLocatorAsFile(locators.get(i));
            if (uploadedLocator != null) {
              // remove locator
              itemVO.getFiles().remove(i);
              // add file
              itemVO.getFiles().add(uploadedLocator);
            }
          }
        }
      }

      PubItemVOPresentation pubItemPres = new PubItemVOPresentation(itemVO);
      this.setItem(pubItemPres);
    } catch (Exception e) {
      logger.error("Error reading bibtex file", e);
      error(getMessage("easy_submission_bibtex_error"));
      return null;
    }

    return "loadNewEasySubmission";
  }

  /**
   * Handles the import from an external ingestion sources.
   * 
   * @return navigation String
   */
  public String harvestData() {
    // Fetch data from external system
    if (EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL
        .equals(this.getEasySubmissionSessionBean().getImportMethod())) {
      if (getServiceID() == null || "".equals(getServiceID())) {
        warn(getMessage("easy_submission_external_service_no_id"));
        return null;
      }

      DataHandlerService dataHandler = new DataHandlerService();
      PubItemVO itemVO = null;
      String service = this.getEasySubmissionSessionBean().getCurrentExternalServiceType();
      List<FileVO> fileVOs = new ArrayList<FileVO>();
      String fetchedItem = null;
      try {
        // Harvest metadata
        byte[] fetchedItemByte = dataHandler.doFetch(service, getServiceID(), INTERNAL_MD_FORMAT);
        fetchedItem = new String(fetchedItemByte, 0, fetchedItemByte.length, "UTF8");

        // Harvest full text
        if (this.getEasySubmissionSessionBean().isFulltext()
            && ((!this.getEasySubmissionSessionBean().getRadioSelectFulltext()
                .equals(EasySubmissionSessionBean.FULLTEXT_NONE)) && !fetchedItem.equals(""))
            && !service.equalsIgnoreCase("escidoc")) {
          DataSourceVO source = this.dataSourceHandler.getSourceByName(service);
          List<FullTextVO> ftFormats = source.getFtFormats();
          FullTextVO fulltext = new FullTextVO();
          List<String> formats = new ArrayList<String>();

          // Get DEFAULT full text version from source
          if (this.getEasySubmissionSessionBean().getRadioSelectFulltext()
              .equals(EasySubmissionSessionBean.FULLTEXT_DEFAULT)) {
            for (int x = 0; x < ftFormats.size(); x++) {
              fulltext = ftFormats.get(x);
              if (fulltext.isFtDefault()) {
                formats.add(fulltext.getName());
                break;
              }
            }
          }

          // Get ALL full text versions from source
          if (this.getEasySubmissionSessionBean().getRadioSelectFulltext()
              .equals(EasySubmissionSessionBean.FULLTEXT_ALL)) {
            for (int x = 0; x < ftFormats.size(); x++) {
              fulltext = ftFormats.get(x);
              formats.add(fulltext.getName());
            }
          }

          String[] arrFormats = new String[formats.size()];
          byte[] ba = dataHandler.doFetch(
              this.getEasySubmissionSessionBean().getCurrentExternalServiceType(), getServiceID(),
              formats.toArray(arrFormats));
          ByteArrayInputStream in = new ByteArrayInputStream(ba);
          URL fileURL = this.uploadFile(in, dataHandler.getContentType(),
              getLoginHelper().getESciDocUserHandle());

          if (fileURL != null && !fileURL.toString().trim().equals("")) {
            FileVO fileVO = dataHandler.getComponentVO();
            MdsFileVO fileMd = fileVO.getDefaultMetadata();
            fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);
            fileVO.setVisibility(dataHandler.getVisibility());
            fileVO.setDefaultMetadata(fileMd);
            fileVO.getDefaultMetadata()
                .setTitle(this.replaceSlashes(getServiceID().trim() + dataHandler.getFileEnding()));
            fileVO.setMimeType(dataHandler.getContentType());
            fileVO
                .setName(this.replaceSlashes(getServiceID().trim() + dataHandler.getFileEnding()));
            FormatVO formatVO = new FormatVO();
            formatVO.setType("dcterms:IMT");
            formatVO.setValue(dataHandler.getContentType());
            fileVO.getDefaultMetadata().getFormats().add(formatVO);
            fileVO.setContent(fileURL.toString());
            fileVO.getDefaultMetadata().setSize(ba.length);
            fileVO.getDefaultMetadata().setDescription(
                "File downloaded from " + service + " at " + CommonUtils.currentDate());
            fileVO.setContentCategory(dataHandler.getContentCategory());
            fileVOs.add(fileVO);
          }
        }
      } catch (AccessException inre) {
        logger.error("Error fetching from external import source", inre);
        error(getMessage("easy_submission_import_from_external_service_access_denied_error")
            + getServiceID());
        return null;
      } catch (DataaquisitionException inre) {
        logger.error(inre.getMessage(), inre);
        error(getMessage("easy_submission_import_from_external_service_identifier_error")
            + getServiceID());
        return null;
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        error(getMessage("easy_submission_import_from_external_service_identifier_error")
            + getServiceID());
        return null;
      }

      // Generate item ValueObject
      if (fetchedItem != null && !fetchedItem.trim().equals("")) {
        try {
          itemVO = XmlTransformingService.transformToPubItem(fetchedItem);

          // Upload fulltexts from other escidoc repositories to current repository
          if (this.getEasySubmissionSessionBean().isFulltext()
              && this.getEasySubmissionSessionBean().getRadioSelectFulltext() != null
              && this.getEasySubmissionSessionBean().getRadioSelectFulltext()
                  .equals(EasySubmissionSessionBean.FULLTEXT_ALL)
              && service.equalsIgnoreCase("escidoc")) {
            boolean hasFile = false;
            List<FileVO> fetchedFileList = itemVO.getFiles();

            for (int i = 0; i < fetchedFileList.size(); i++) {
              FileVO file = fetchedFileList.get(i);

              if (file.getStorage().equals(FileVO.Storage.INTERNAL_MANAGED)) {
                try {
                  FileVO newFile = new FileVO();
                  byte[] content =
                      dataHandler.retrieveComponentContent(this.getServiceID(), file.getContent());
                  ByteArrayInputStream in = new ByteArrayInputStream(content);
                  URL fileURL;
                  fileURL = this.uploadFile(in, dataHandler.getContentType(),
                      getLoginHelper().getESciDocUserHandle());

                  if (fileURL != null && !fileURL.toString().trim().equals("")
                      && file.getVisibility().equals(FileVO.Visibility.PUBLIC)) {
                    hasFile = true;
                    newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
                    newFile.setVisibility(file.getVisibility());
                    newFile.setDefaultMetadata(new MdsFileVO());
                    newFile.getDefaultMetadata()
                        .setTitle(this.replaceSlashes(file.getDefaultMetadata().getTitle()));
                    newFile.setMimeType(file.getMimeType());
                    newFile.setName(this.replaceSlashes(file.getName()));
                    FormatVO formatVO = new FormatVO();
                    formatVO.setType("dcterms:IMT");
                    formatVO.setValue(file.getMimeType());
                    newFile.getDefaultMetadata().getFormats().add(formatVO);
                    newFile.setContent(fileURL.toString());
                    newFile.getDefaultMetadata().setSize(content.length);
                    if (file.getDescription() != null) {
                      newFile.getDefaultMetadata()
                          .setDescription(file.getDescription() + " File downloaded from " + service
                              + " at " + CommonUtils.currentDate());
                    } else {
                      newFile.getDefaultMetadata().setDescription(
                          "File downloaded from " + service + " at " + CommonUtils.currentDate());
                    }
                    newFile.setContentCategory(file.getContentCategory());
                    fileVOs.add(newFile);
                  }
                } catch (Exception e) {
                  logger.error("Error fetching file from coreservice", e);
                }
              } else if (file.getStorage().equals(FileVO.Storage.EXTERNAL_URL)
                  && file.getVisibility().equals(FileVO.Visibility.PUBLIC)) {
                // Locator is just added as is
                fileVOs.add(file);
              }
            }

            if (!hasFile) {
              info(getMessage("easy_submission_import_from_external_service_identifier_info"));
            }
          }

          itemVO.getFiles().clear();
          itemVO.setContext(getItem().getContext());

          if (dataHandler.getItemUrl() != null) {
            IdentifierVO id = new IdentifierVO();
            id.setType(IdType.URI);
            try {
              id.setId(java.net.URLDecoder.decode(dataHandler.getItemUrl().toString(), "UTF-8"));
              itemVO.getMetadata().getIdentifiers().add(id);
            } catch (UnsupportedEncodingException e) {
              logger.warn("Item URL could not be decoded");
            }
          }

          if (this.getEasySubmissionSessionBean().isFulltext()
              && !this.getEasySubmissionSessionBean().getRadioSelectFulltext()
                  .equals(EasySubmissionSessionBean.FULLTEXT_NONE)) {
            for (int i = 0; i < fileVOs.size(); i++) {
              FileVO tmp = fileVOs.get(i);
              itemVO.getFiles().add(tmp);
            }

            fileVOs.clear();
          }

          this.getItem().setMetadata(itemVO.getMetadata());
          this.getItem().getFiles().clear();
          this.getItem().getFiles().addAll(itemVO.getFiles());
        } catch (TechnicalException e) {
          logger.warn("Error transforming item to pubItem.");
          error(getMessage("easy_submission_import_from_external_service_error"));
          return null;
        }
      } else {
        logger.warn("Empty fetched Item.");
        error(getMessage("easy_submission_import_from_external_service_error"));
        return null;
      }
    }
    // Fetch data from provided file
    else if (EasySubmissionSessionBean.IMPORT_METHOD_BIBTEX
        .equals(this.getEasySubmissionSessionBean().getImportMethod())) {
      String uploadResult = uploadBibtexFile();
      if (uploadResult == null) {
        return null;
      }
    }

    this.getEditItemSessionBean().clean();

    return "loadEditItem";
  }

  /**
   * This method replaces forward and backslases in a given String (e.g. in a filename) with an
   * underscore
   * 
   * @param fileName
   * @return String the cleaned String
   */
  private String replaceSlashes(String fileName) {
    if (fileName != null) {
      // replace forward slahes
      String newFileName = fileName.replaceAll("\\/", "_");
      // replace back slashes
      return newFileName.replaceAll("\\\\", "_");
    }

    return "";
  }

  public String cancel() {
    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP1);

    try {
      getExternalContext().redirect("faces/SubmissionPage.jsp");
    } catch (Exception e) {
      logger.error(
          "Cancel error: could not find context to redirect to SubmissionPage.jsp in Full Submssion",
          e);
    }

    return null;
  }

  public String loadStep1() {
    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP1);

    return "loadNewEasySubmission";
  }

  public String loadStep2() {
    List<PubContextVOPresentation> depositorContextList = this.getDepositorContextList();

    if (depositorContextList != null && depositorContextList.size() > 1) {
      this.getEasySubmissionSessionBean()
          .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
    } else {
      try {
        getExternalContext().redirect("faces/SubmissionPage.jsp");
      } catch (Exception e) {
        logger.error("could not find context to redirect to SubmissionPage.jsp in Easy Submssion",
            e);
      }
    }

    return "loadNewEasySubmission";
  }

  public String validateAndLoadStep3Manual() {
    if (validate(ValidationPoint.EASY_SUBMISSION_STEP_4, "loadNewEasySubmission") == null) {
      return "";
    }

    return loadStep3Manual();
  }

  public String loadStep3Manual() {
    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);
    this.init();

    return "loadNewEasySubmission";
  }

  public String loadStep4Manual() {
    parseAndSetAlternativeSourceTitlesAndIds();
    saveLocator();

    List<PubFileVOPresentation> locators = this.getLocators();
    List<PubFileVOPresentation> files = this.getFiles();

    // save the files and locators in the item in the ItemControllerSessionBean
    this.getItem().getFiles().clear();

    // first add the files
    for (int i = 0; i < files.size(); i++) {
      this.getItem().getFiles().add(files.get(i).getFile());
    }

    // then add the locators
    for (int i = 0; i < locators.size(); i++) {
      this.getItem().getFiles().add(locators.get(i).getFile());
    }

    // add an empty file and an empty locator if necessary for display purposes
    if (files != null && files.size() > 0) {
      if (files.get(files.size() - 1).getFile().getDefaultMetadata().getSize() > 0) {
        FileVO newFile = new FileVO();
        newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
        newFile.setVisibility(FileVO.Visibility.PUBLIC);
        newFile.setDefaultMetadata(new MdsFileVO());
        files.add(new PubFileVOPresentation(files.size(), newFile, false));
      }
    }

    if (locators != null && locators.size() > 0) {
      if (locators.get(locators.size() - 1).getFile().getDefaultMetadata().getSize() > 0) {
        String contentCategory = null;
        if (PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL") != null) {
          contentCategory = PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL");
        } else {
          Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
          if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
            contentCategory = contentCategoryMap.values().iterator().next();
          } else {
            error("There is no content category available.");
            Logger.getLogger(PubFileVOPresentation.class)
                .warn("WARNING: no content-category has been defined in Genres.xml");
          }
        }

        PubFileVOPresentation newLocator = new PubFileVOPresentation(locators.size(), true);
        newLocator.getFile().setContentCategory(contentCategory);
        newLocator.getFile().setVisibility(FileVO.Visibility.PUBLIC);
        newLocator.getFile().setDefaultMetadata(new MdsFileVO());
        locators.add(newLocator);
      }
    }

    // validate
    if (validate(ValidationPoint.EASY_SUBMISSION_STEP_3, "loadNewEasySubmission") == null) {
      return "";
    }

    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP4);

    this.init();

    return "loadNewEasySubmission";
  }

  public String loadStep5Manual() {
    if (validate(ValidationPoint.EASY_SUBMISSION_STEP_4, "loadNewEasySubmission") == null) {
      return "";
    }

    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP5);

    this.init();

    return "loadNewEasySubmission";
  }

  public String loadPreview() {
    parseAndSetAlternativeSourceTitlesAndIds();

    if (validate(ValidationPoint.STANDARD, "loadEditItem") != null) {
      this.getEasySubmissionSessionBean().cleanup();
      this.getEditItemSessionBean().clean();
      return "loadEditItem";
    }

    return "";
  }

  public String validate(ValidationPoint validationPoint, String navigateTo) {
    try {
      // bind Organizations To Creators
      if (!this.getEasySubmissionSessionBean().bindOrganizationsToCreators()) {
        return null;
      }

      PubItemVO pubItem = this.getItem();

      // write creators back to VO
      if (this.getEasySubmissionSessionBean()
          .getCurrentSubmissionStep() == EasySubmissionSessionBean.ES_STEP4) {
        this.getEasySubmissionSessionBean().bindCreatorsToVO(pubItem.getMetadata().getCreators());
      }

      PubItemVO itemVO = new PubItemVO(pubItem);

      // // cleanup item according to genre specific MD specification
      // GenreSpecificItemManager itemManager =
      // new GenreSpecificItemManager(itemVO, GenreSpecificItemManager.SUBMISSION_METHOD_EASY);
      // try {
      // itemVO = (PubItemVO) itemManager.cleanupItem();
      // } catch (Exception e) {
      // throw new RuntimeException("Error while cleaning up item genre specificly", e);
      // }

      try {
        ItemValidatingService.validateItemObject(itemVO, validationPoint);
      } catch (ItemInvalidException e) {
        for (ValidationReportItemVO item : e.getReport().getItems()) {
          error(getMessage(item.getContent()));
        }
        return null;
      } catch (ValidationException e) {
        throw new RuntimeException("Validation error", e);
      }
    } catch (Exception e) {
      logger.error("Validation error", e);
    }

    return navigateTo;
  }

  /**
   * Fill import source values dynamically from importsourceHandler
   */
  private void setImportSourcesInfo() {
    try {
      this.dataSources = this.dataSourceHandler.getSources(INTERNAL_MD_FORMAT);

      List<SelectItem> v_serviceOptions = new ArrayList<SelectItem>();
      List<FullTextVO> ftFormats = new ArrayList<FullTextVO>();

      String currentSource = "";
      for (int i = 0; i < this.dataSources.size(); i++) {
        DataSourceVO source = (DataSourceVO) this.dataSources.get(i);
        v_serviceOptions.add(new SelectItem(source.getName()));
        this.getEasySubmissionSessionBean().setCurrentExternalServiceType(source.getName());
        currentSource = source.getName();
        // Get full text informations from this source
        ftFormats = source.getFtFormats();

        for (int x = 0; x < ftFormats.size(); x++) {
          this.getEasySubmissionSessionBean().setFulltext(true);
          FullTextVO ft = ftFormats.get(x);
          if (ft.isFtDefault()) {
            this.getEasySubmissionSessionBean().setCurrentFTLabel(ft.getFtLabel());
            this.getEasySubmissionSessionBean()
                .setRadioSelectFulltext(EasySubmissionSessionBean.FULLTEXT_DEFAULT);
          }
        }

        if (ftFormats.size() <= 0) {
          this.getEasySubmissionSessionBean().setFulltext(false);
          this.getEasySubmissionSessionBean().setCurrentFTLabel("");
        }
      }

      this.EXTERNAL_SERVICE_OPTIONS = new SelectItem[v_serviceOptions.size()];
      v_serviceOptions.toArray(this.EXTERNAL_SERVICE_OPTIONS);
      this.getEasySubmissionSessionBean()
          .setEXTERNAL_SERVICE_OPTIONS(this.EXTERNAL_SERVICE_OPTIONS);

      if (currentSource.toLowerCase().equals("escidoc")) {
        this.getEasySubmissionSessionBean()
            .setFULLTEXT_OPTIONS(new SelectItem[] {
                new SelectItem(EasySubmissionSessionBean.FULLTEXT_ALL,
                    getLabel("easy_submission_lblFulltext_all")),
                new SelectItem(EasySubmissionSessionBean.FULLTEXT_NONE,
                    getLabel("easy_submission_lblFulltext_none"))});
        this.getEasySubmissionSessionBean()
            .setRadioSelectFulltext(EasySubmissionSessionBean.FULLTEXT_ALL);
      } else {
        if (ftFormats.size() > 1) {
          this.getEasySubmissionSessionBean()
              .setFULLTEXT_OPTIONS(new SelectItem[] {
                  new SelectItem(EasySubmissionSessionBean.FULLTEXT_DEFAULT,
                      this.getEasySubmissionSessionBean().getCurrentFTLabel()),
                  new SelectItem(EasySubmissionSessionBean.FULLTEXT_ALL,
                      getLabel("easy_submission_lblFulltext_all")),
                  new SelectItem(EasySubmissionSessionBean.FULLTEXT_NONE,
                      getLabel("easy_submission_lblFulltext_none"))});
        } else
          this.getEasySubmissionSessionBean()
              .setFULLTEXT_OPTIONS(new SelectItem[] {
                  new SelectItem(EasySubmissionSessionBean.FULLTEXT_DEFAULT,
                      this.getEasySubmissionSessionBean().getCurrentFTLabel()),
                  new SelectItem(EasySubmissionSessionBean.FULLTEXT_NONE,
                      getLabel("easy_submission_lblFulltext_none"))});
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Triggered when the selection of the external system is changed Updates full text selection
   * 
   * @return String navigation string
   */
  public void changeImportSource(String newImportSource) {
    DataSourceVO currentSource = this.dataSourceHandler.getSourceByName(newImportSource);
    if (currentSource == null) {
      currentSource = new DataSourceVO();
    }

    this.getEasySubmissionSessionBean().setCurrentExternalServiceType(currentSource.getName());
    List<FullTextVO> ftFormats = currentSource.getFtFormats();
    if (ftFormats != null && ftFormats.size() > 0) {
      for (int x = 0; x < ftFormats.size(); x++) {
        this.getEasySubmissionSessionBean().setFulltext(true);
        FullTextVO ft = ftFormats.get(x);
        if (ft.isFtDefault()) {
          this.getEasySubmissionSessionBean().setCurrentFTLabel(ft.getFtLabel());
          this.getEasySubmissionSessionBean()
              .setRadioSelectFulltext(EasySubmissionSessionBean.FULLTEXT_DEFAULT);
        }
      }
    } else {
      this.getEasySubmissionSessionBean().setFulltext(false);
      this.getEasySubmissionSessionBean().setCurrentFTLabel("");
    }

    // This has to be set, because escidoc does not have a default fetching format for full texts
    if (currentSource.getName().toLowerCase().equals("escidoc")) {
      this.getEasySubmissionSessionBean()
          .setFULLTEXT_OPTIONS(new SelectItem[] {
              new SelectItem(EasySubmissionSessionBean.FULLTEXT_ALL,
                  getLabel("easy_submission_lblFulltext_all")),
              new SelectItem(EasySubmissionSessionBean.FULLTEXT_NONE,
                  getLabel("easy_submission_lblFulltext_none"))});
      this.getEasySubmissionSessionBean()
          .setRadioSelectFulltext(EasySubmissionSessionBean.FULLTEXT_ALL);
    } else {
      if (ftFormats.size() > 1) {
        this.getEasySubmissionSessionBean()
            .setFULLTEXT_OPTIONS(new SelectItem[] {
                new SelectItem(EasySubmissionSessionBean.FULLTEXT_DEFAULT,
                    this.getEasySubmissionSessionBean().getCurrentFTLabel()),
                new SelectItem(EasySubmissionSessionBean.FULLTEXT_ALL,
                    getLabel("easy_submission_lblFulltext_all")),
                new SelectItem(EasySubmissionSessionBean.FULLTEXT_NONE,
                    getLabel("easy_submission_lblFulltext_none"))});
        this.getEasySubmissionSessionBean()
            .setRadioSelectFulltext(EasySubmissionSessionBean.FULLTEXT_DEFAULT);
      }

      if (ftFormats.size() == 1) {
        this.getEasySubmissionSessionBean()
            .setFULLTEXT_OPTIONS(new SelectItem[] {
                new SelectItem(EasySubmissionSessionBean.FULLTEXT_DEFAULT,
                    this.getEasySubmissionSessionBean().getCurrentFTLabel()),
                new SelectItem(EasySubmissionSessionBean.FULLTEXT_NONE,
                    getLabel("easy_submission_lblFulltext_none"))});
        this.getEasySubmissionSessionBean()
            .setRadioSelectFulltext(EasySubmissionSessionBean.FULLTEXT_DEFAULT);
      }

      if (ftFormats.size() <= 0) {
        this.getEasySubmissionSessionBean()
            .setRadioSelectFulltext(EasySubmissionSessionBean.FULLTEXT_NONE);
        this.getEasySubmissionSessionBean().setFulltext(false);
      }
    }

    this.getEasySubmissionSessionBean().setCurrentExternalServiceType(newImportSource);
  }

  public void changeImportSourceListener(ValueChangeEvent evt) {
    if (evt.getNewValue() != null) {
      changeImportSource((String) evt.getNewValue());
    }
  }

  /**
   * This method selects the import method 'fetch metadata from external systems'
   * 
   * @return String navigation string
   */
  public String selectImportExternal() {
    this.changeImportSource(this.getEasySubmissionSessionBean().getCurrentExternalServiceType());
    this.getEasySubmissionSessionBean()
        .setImportMethod(EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL);

    return "loadNewEasySubmission";
  }

  /**
   * This method selects the import method 'Upload Bibtex file'
   * 
   * @return String navigation string
   */
  public String selectImportBibtex() {
    this.getEasySubmissionSessionBean()
        .setImportMethod(EasySubmissionSessionBean.IMPORT_METHOD_BIBTEX);

    return "loadNewEasySubmission";
  }

  /**
   * returns a flag which sets the fields of the import method 'fetch metadata from external
   * systems' to disabled or not
   * 
   * @return boolean the flag for disabling
   */
  public boolean getDisableExternalFields() {
    if (this.getEasySubmissionSessionBean().getImportMethod()
        .equals(EasySubmissionSessionBean.IMPORT_METHOD_BIBTEX)) {
      return true;
    }

    return false;
  }

  /**
   * returns a flag which sets the fields of the import method 'Upload Bibtex file' to disabled or
   * not
   * 
   * @return boolean the flag for disabling
   */
  public boolean getDisableBibtexFields() {
    if (this.getEasySubmissionSessionBean().getImportMethod()
        .equals(EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL)) {
      return true;
    }

    return false;
  }

  /**
   * localized creation of SelectItems for the genres available.
   * 
   * @return SelectItem[] with Strings representing genres.
   */
  public SelectItem[] getGenres() {
    List<MdsPublicationVO.Genre> allowedGenres = null;
    List<AdminDescriptorVO> adminDescriptors =
        this.getItemControllerSessionBean().getCurrentContext().getAdminDescriptors();

    for (AdminDescriptorVO adminDescriptorVO : adminDescriptors) {
      if (adminDescriptorVO instanceof PublicationAdminDescriptorVO) {
        allowedGenres = ((PublicationAdminDescriptorVO) adminDescriptorVO).getAllowedGenres();
        return getI18nHelper().getSelectItemsForEnum(false,
            allowedGenres.toArray(new MdsPublicationVO.Genre[] {}));
      }
    }

    return null;
  }

  /**
   * This method changes the Genre and sets the needed property file for genre specific Metadata
   * 
   * @return String null
   */
  public String changeGenre() {
    String newGenre = this.genreSelect.getSubmittedValue().toString();

    if (newGenre != null && newGenre.trim().equals("")) {
      newGenre = "ARTICLE";
      getItem().getMetadata().setGenre(Genre.ARTICLE);
    }

    this.getEasySubmissionSessionBean().setGenreBundle("Genre_" + newGenre);

    this.init();

    return null;
  }

  public SelectItem[] getSUBMISSION_METHOD_OPTIONS() {
    return this.SUBMISSION_METHOD_OPTIONS;
  }

  public void setSUBMISSION_METHOD_OPTIONS(SelectItem[] submission_method_options) {
    this.SUBMISSION_METHOD_OPTIONS = submission_method_options;
  }

  public SelectItem[] getDATE_TYPE_OPTIONS() {
    return this.DATE_TYPE_OPTIONS;
  }

  public void setDATE_TYPE_OPTIONS(SelectItem[] date_type_options) {
    this.DATE_TYPE_OPTIONS = date_type_options;
  }

  public SelectItem[] getEXTERNAL_SERVICE_OPTIONS() {
    return this.EXTERNAL_SERVICE_OPTIONS;
  }

  public void setEXTERNAL_SERVICE_OPTIONS(SelectItem[] external_service_options) {
    this.EXTERNAL_SERVICE_OPTIONS = external_service_options;
  }

  public HtmlSelectOneRadio getRadioSelect() {
    return this.radioSelect;
  }

  public void setRadioSelect(HtmlSelectOneRadio radioSelect) {
    this.radioSelect = radioSelect;
  }

  public PubItemVO getItem() {
    return this.getItem();
  }

  public void setItem(PubItemVOPresentation item) {
    this.getItemControllerSessionBean().setCurrentPubItem(item);
  }

  public List<PubFileVOPresentation> getFiles() {
    return this.getEasySubmissionSessionBean().getFiles();
  }

  public List<PubFileVOPresentation> getLocators() {
    return this.getEasySubmissionSessionBean().getLocators();
  }

  private List<PubContextVOPresentation> getDepositorContextList() {
    return this.getContextListSessionBean().getDepositorContextList();
  }

  public void setFiles(List<PubFileVOPresentation> files) {
    this.getEasySubmissionSessionBean().setFiles(files);
  }

  public void setLocators(List<PubFileVOPresentation> files) {
    this.getEasySubmissionSessionBean().setLocators(files);
  }

  public UploadedFile getUploadedFile() {
    return this.uploadedFile;
  }

  public void setUploadedFile(UploadedFile uploadedFile) {
    this.uploadedFile = uploadedFile;
  }

  public String getSelectedDate() {
    return this.selectedDate;
  }

  public void setSelectedDate(String selectedDate) {
    this.selectedDate = selectedDate;
  }

  public HtmlSelectOneMenu getDateSelect() {
    return this.dateSelect;
  }

  public void setDateSelect(HtmlSelectOneMenu dateSelect) {
    this.dateSelect = dateSelect;
  }

  public String getServiceID() {
    return this.serviceID;
  }

  public void setServiceID(String serviceID) {
    this.serviceID = serviceID;
  }

  /**
   * Returns all options for visibility.
   * 
   * @return all options for visibility
   */
  public SelectItem[] getVisibilities() {
    return getI18nHelper().getSelectItemsVisibility(false);
  }

  /**
   * Returns all options for visibility.
   * 
   * @return all options for visibility
   */
  public SelectItem[] getLocatorVisibilities() {
    return this.locatorVisibilities;
  }

  /**
   * Returns all options for publication language.
   * 
   * @return all options for publication language
   */
  public SelectItem[] getPublicationLanguages() {
    return CommonUtils.getLanguageOptions();
  }

  /**
   * returns the first language entry of the publication as String
   * 
   * @return String the first language entry of the publication as String
   */
  public String getPublicationLanguage() {
    return this.getItem().getMetadata().getLanguages().get(0);
  }

  public void setPublicationLanguage(String language) {
    this.getItem().getMetadata().getLanguages().clear();

    if (language != null) {
      this.getItem().getMetadata().getLanguages().add(language);
    } else {
      this.getItem().getMetadata().getLanguages().add("");
    }
  }

  /**
   * returns the value of the first abstract of the publication
   * 
   * @return String the value of the first abstract of the publication
   */
  public String getAbstract() {
    if (this.getItem().getMetadata().getAbstracts() == null
        || this.getItem().getMetadata().getAbstracts().size() < 1) {
      AbstractVO newAbstract = new AbstractVO();
      this.getItem().getMetadata().getAbstracts().add(newAbstract);
    }
    return this.getItem().getMetadata().getAbstracts().get(0).getValue();
  }

  public void setAbstract(String publicationAbstract) {
    AbstractVO newAbstract = new AbstractVO();
    newAbstract.setValue(publicationAbstract);
    this.getItem().getMetadata().getAbstracts().clear();
    this.getItem().getMetadata().getAbstracts().add(newAbstract);
  }

  /**
   * returns the value of the first subject of the publication
   * 
   * @return String the value of the first subject of the publication
   */
  public String getSubject() {
    if (this.getItem().getMetadata().getSubjects() == null
        || this.getItem().getMetadata().getSubjects().size() < 1) {
      this.getItem().getMetadata().getSubjects().add(new SubjectVO());
    }

    return this.getItem().getMetadata().getSubjects().get(0).getValue();
  }

  public void setSubject(String publicationSubject) {
    SubjectVO newSubject = new SubjectVO(publicationSubject);
    this.getItem().getMetadata().getSubjects().clear();
    this.getItem().getMetadata().getSubjects().add(newSubject);
  }

  public String getFreeKeywords() {
    if (this.getItem().getMetadata().getFreeKeywords() == null) {
      this.getItem().getMetadata().setFreeKeywords("");
    }

    return this.getItem().getMetadata().getFreeKeywords();
  }

  public void setFreeKeywords(String publicationSubject) {
    if (this.getItem().getMetadata().getFreeKeywords() == null) {
      this.getItem().getMetadata().setFreeKeywords("");
    }
    this.getItem().getMetadata().getFreeKeywords();
  }

  /**
   * Returns all options for content category.
   * 
   * @return all options for content category.
   */
  public SelectItem[] getContentCategories() {
    return getI18nHelper().getSelectItemsContentCategory(true);
  }

  /**
   * Returns the number of files attached to the current item
   * 
   * @return int the number of files
   */
  public int getNumberOfFiles() {
    if (this.getFiles() != null) {
      return this.getFiles().size();
    }

    return 0;
  }

  /**
   * Returns the number of files attached to the current item
   * 
   * @return int the number of files
   */
  public int getNumberOfLocators() {
    if (this.getLocators() != null) {
      return this.getLocators().size();
    }

    return 0;
  }

  /**
   * This method examines if the user has already selected a context for creating an item. If yes,
   * the 'Next' button will be enabled, otherwise disabled
   * 
   * @return boolean Flag if the 'Next' button should be enabled or disabled
   */
  public boolean getDisableNextButton() {
    boolean disableButton = true;
    int countSelectedContexts = 0;

    List<PubContextVOPresentation> depositorContextList = this.getDepositorContextList();

    // examine if a context for creating the item has been selected
    if (depositorContextList != null) {
      for (int i = 0; i < depositorContextList.size(); i++) {
        if (depositorContextList.get(i).getSelected() == true) {
          countSelectedContexts++;
        }
      }
    }

    if (countSelectedContexts > 0) {
      disableButton = false;
    }

    return disableButton;
  }

  public String getSourceTitle() {
    String sourceTitle = "";
    if (this.getItem().getMetadata().getSources() == null
        || this.getItem().getMetadata().getSources().size() < 1) {
      this.getItem().getMetadata().getSources().add(new SourceVO());
    }
    // return the title value oif the first source
    sourceTitle = this.getItem().getMetadata().getSources().get(0).getTitle();
    return sourceTitle;
  }

  public void setSourceTitle(String title) {
    this.getItem().getMetadata().getSources().get(0).setTitle(title);
  }

  public String getSourcePublisher() {
    // Create new Publishing Info if not available yet
    SourceVO source = this.getItem().getMetadata().getSources().get(0);
    PublishingInfoVO pubVO;
    if (source.getPublishingInfo() == null) {
      pubVO = new PublishingInfoVO();
      source.setPublishingInfo(pubVO);
    } else {
      pubVO = source.getPublishingInfo();
    }
    return pubVO.getPublisher();
  }

  public void setSourcePublisher(String publisher) {
    this.getItem().getMetadata().getSources().get(0).getPublishingInfo().setPublisher(publisher);
  }

  public String getSourcePublisherPlace() {
    return this.getItem().getMetadata().getSources().get(0).getPublishingInfo().getPlace();
  }

  public void setSourcePublisherPlace(String place) {
    this.getItem().getMetadata().getSources().get(0).getPublishingInfo().setPlace(place);
  }

  public String getSourceIdentifier() {
    if (this.getItem().getMetadata().getSources().get(0).getIdentifiers().size() == 0) {
      IdentifierVO identifier = new IdentifierVO();
      this.getItem().getMetadata().getSources().get(0).getIdentifiers().add(identifier);
    }

    return this.getItem().getMetadata().getSources().get(0).getIdentifiers().get(0).getId();
  }

  public void setSourceIdentifier(String id) {
    PubItemVO pubItem = this.getItem();
    pubItem.getMetadata().getSources().get(0).getIdentifiers().get(0).setId(id);
    if (!id.trim().equals("")) {
      pubItem.getMetadata().getSources().get(0).getIdentifiers().get(0).setType(IdType.OTHER);
    }
  }

  // source identifier
  public void setSourceIdentifierType(String typeString) {
    if (typeString != null) {
      this.getItem().getMetadata().getSources().get(0).getIdentifiers().get(0)
          .setTypeString(typeString);
    }
  }

  public String getSourceIdentifierType() {
    if (this.getItem().getMetadata().getSources().get(0).getIdentifiers().size() == 0) {
      IdentifierVO identifier = new IdentifierVO();
      this.getItem().getMetadata().getSources().get(0).getIdentifiers().add(identifier);
    }

    return this.getItem().getMetadata().getSources().get(0).getIdentifiers().get(0).getTypeString();
  }

  /**
   * localized creation of SelectItems for the source genres available
   * 
   * @return SelectItem[] with Strings representing source genres
   */
  public SelectItem[] getSourceGenreOptions() {
    Map<String, String> excludedSourceGenres =
        ((ApplicationBean) getApplicationBean(ApplicationBean.class)).getExcludedSourceGenreMap();

    List<SelectItem> sourceGenres = new ArrayList<SelectItem>();
    sourceGenres.add(new SelectItem("", getLabel("EditItem_NO_ITEM_SET")));
    for (SourceVO.Genre value : SourceVO.Genre.values()) {
      sourceGenres.add(new SelectItem(value, getLabel("ENUM_GENRE_" + value.name())));
    }

    int i = 0;
    while (i < sourceGenres.size()) {
      String uri = "";
      if (sourceGenres.get(i).getValue() != null && !("").equals(sourceGenres.get(i).getValue())) {
        uri = ((SourceVO.Genre) sourceGenres.get(i).getValue()).getUri();
      }

      if (excludedSourceGenres.containsValue(uri)) {
        sourceGenres.remove(i);
      } else {
        i++;
      }
    }

    return sourceGenres.toArray(new SelectItem[sourceGenres.size()]);
  }

  public SourceVO getSource() {
    if (this.getItem().getMetadata().getSources() != null
        && this.getItem().getMetadata().getSources().size() > 0) {
      return this.getItem().getMetadata().getSources().get(0);
    }

    return null;
  }

  public void setSource(SourceVO source) {
    if (this.getItem().getMetadata().getSources() != null
        && this.getItem().getMetadata().getSources().size() > 0) {
      this.getItem().getMetadata().getSources().set(0, source);
    }
  }

  public void setCreatorParseString(String creatorParseString) {
    this.getEasySubmissionSessionBean().setCreatorParseString(creatorParseString);
  }

  public String getCreatorParseString() {
    return this.getEasySubmissionSessionBean().getCreatorParseString();
  }

  public String addCreatorString() {
    try {
      this.getEasySubmissionSessionBean().parseCreatorString(getCreatorParseString(), null,
          this.getEasySubmissionSessionBean().getOverwriteCreators());
      setCreatorParseString("");
      this.getEasySubmissionSessionBean().initAuthorCopyPasteCreatorBean();
      return "loadNewEasySubmission";
    } catch (Exception e) {
      error(getMessage("ErrorParsingCreatorString"));
      return "loadNewEasySubmission";
    }
  }

  public SelectItem[] getFULLTEXT_OPTIONS() {
    return this.FULLTEXT_OPTIONS;
  }

  public void setFULLTEXT_OPTIONS(SelectItem[] fulltext_options) {
    this.FULLTEXT_OPTIONS = fulltext_options;
  }

  public HtmlSelectOneRadio getRadioSelectFulltext() {
    return this.radioSelectFulltext;
  }

  public void setRadioSelectFulltext(HtmlSelectOneRadio radioSelectFulltext) {
    this.radioSelectFulltext = radioSelectFulltext;
  }

  public SelectItem[] getREFERENCE_OPTIONS() {
    return this.REFERENCE_OPTIONS;
  }

  public void setREFERENCE_OPTIONS(SelectItem[] reference_options) {
    this.REFERENCE_OPTIONS = reference_options;
  }

  /**
   * This method returns the URL to the cone autosuggest service read from the properties
   * 
   * @author Tobias Schraut
   * @return String the URL to the cone autosuggest service
   * @throws Exception
   */
  public String getSuggestConeUrl() throws Exception {
    if (this.suggestConeUrl == null) {
      this.suggestConeUrl = PropertyReader.getProperty("escidoc.cone.service.url");
    }

    return this.suggestConeUrl;
  }

  /**
   * Returns all options for degreeType.
   * 
   * @return all options for degreeType
   */
  public SelectItem[] getDegreeTypes() {
    return getI18nHelper().getSelectItemsDegreeType(true);
  }

  public void setOverwriteCreators(boolean overwriteCreators) {
    this.overwriteCreators = overwriteCreators;
  }

  public boolean getOverwriteCreators() {
    return this.overwriteCreators;
  }

  public void setHiddenAlternativeTitlesField(String hiddenAlternativeTitlesField) {
    this.hiddenAlternativeTitlesField = hiddenAlternativeTitlesField;
  }

  public String getHiddenAlternativeTitlesField() {
    return this.hiddenAlternativeTitlesField;
  }

  public void setHiddenIdsField(String hiddenIdsField) {
    this.hiddenIdsField = hiddenIdsField;
  }

  public String getHiddenIdsField() {
    return this.hiddenIdsField;
  }

  /**
   * Takes the text from the hidden input fields, splits it using the delimiter and adds them to the
   * item. Format of alternative titles: alt title 1 ||##|| alt title 2 ||##|| alt title 3 Format of
   * ids: URN|urn:221441 ||##|| URL|http://www.xwdc.de ||##|| ESCIDOC|escidoc:21431
   * 
   * @return
   */
  public String parseAndSetAlternativeSourceTitlesAndIds() {
    if (getHiddenAlternativeTitlesField() != null
        && !getHiddenAlternativeTitlesField().trim().equals("")) {
      SourceVO source = getSource();
      source.getAlternativeTitles().clear();
      source.getAlternativeTitles()
          .addAll(SourceBean.parseAlternativeTitles(getHiddenAlternativeTitlesField()));
    }

    if (getHiddenIdsField() != null && !getHiddenIdsField().trim().equals("")) {
      List<IdentifierVO> identifiers = getSource().getIdentifiers();
      identifiers.clear();
      identifiers.addAll(SourceBean.parseIdentifiers(getHiddenIdsField()));
    }

    return "";
  }

  public void setIdentifierCollection(IdentifierCollection identifierCollection) {
    this.identifierCollection = identifierCollection;
  }

  public IdentifierCollection getIdentifierCollection() {
    return this.identifierCollection;
  }

  /**
   * Invitationstatus of event has to be converted as it's an enum that is supposed to be shown in a
   * checkbox.
   * 
   * @return true if invitationstatus in VO is set, else false
   */
  public boolean getInvited() {
    if (this.getItem().getMetadata().getEvent() != null
        && this.getItem().getMetadata().getEvent().getInvitationStatus() != null
        && this.getItem().getMetadata().getEvent().getInvitationStatus()
            .equals(EventVO.InvitationStatus.INVITED)) {
      return true;
    }

    return false;
  }

  /**
   * Invitationstatus of event has to be converted as it's an enum that is supposed to be shown in a
   * checkbox.
   * 
   * @param invited the value of the checkbox
   */
  public void setInvited(boolean invited) {
    if (invited) {
      this.getItem().getMetadata().getEvent().setInvitationStatus(EventVO.InvitationStatus.INVITED);
    } else {
      this.getItem().getMetadata().getEvent().setInvitationStatus(null);
    }
  }

  public HtmlSelectOneMenu getGenreSelect() {
    return this.genreSelect;
  }

  public void setGenreSelect(HtmlSelectOneMenu genreSelect) {
    this.genreSelect = genreSelect;
  }

  public String getContextName() {
    if (this.contextName == null) {
      try {
        ContextVO context = this.getItemControllerSessionBean()
            .retrieveContext(this.getItem().getContext().getObjectId());
        this.contextName = context.getName();
        return this.contextName;
      } catch (Exception e) {
        logger.error("Could not retrieve the requested context." + "\n" + e.toString());
        ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
        return ErrorPage.LOAD_ERRORPAGE;
      }
    }

    return this.contextName;
  }

  public void setContextName(String contextName) {
    this.contextName = contextName;
  }

  /**
   * Uploads a file from a given locator.
   */
  public void uploadLocator() {
    LocatorUploadBean locatorBean = new LocatorUploadBean();
    boolean check = locatorBean.checkLocator(this.getLocatorUpload());

    if (check) {
      locatorBean.locatorUploaded();
    }

    if (locatorBean.getError() != null) {
      error(getMessage("errorLocatorMain").replace("$1", locatorBean.getError()));
    } else {
      setLocatorUpload("");
    }
  }

  public String getLocatorUpload() {
    return this.locatorUpload;
  }

  public void setLocatorUpload(String locatorUpload) {
    this.locatorUpload = locatorUpload;
  }

  /**
   * @return the alternativeLanguageName
   */
  public String getAlternativeLanguageName() {
    return this.alternativeLanguageName;
  }

  /**
   * @param alternativeLanguageName the alternativeLanguageName to set
   */
  public void setAlternativeLanguageName(String alternativeLanguageName) {
    this.alternativeLanguageName = alternativeLanguageName;
  }

  private ContextListSessionBean getContextListSessionBean() {
    return (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
  }

  private EasySubmissionSessionBean getEasySubmissionSessionBean() {
    return (EasySubmissionSessionBean) getSessionBean(EasySubmissionSessionBean.class);
  }

  private EditItemSessionBean getEditItemSessionBean() {
    return (EditItemSessionBean) getSessionBean(EditItemSessionBean.class);
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
  }

  private PubItemListSessionBean getPubItemListSessionBean() {
    return (PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class);
  }
}
