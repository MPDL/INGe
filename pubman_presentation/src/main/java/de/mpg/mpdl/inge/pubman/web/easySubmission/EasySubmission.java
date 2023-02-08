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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.faces.bean.ManagedBean;
import jakarta.faces.component.html.HtmlSelectOneMenu;
import jakarta.faces.component.html.HtmlSelectOneRadio;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import de.mpg.mpdl.inge.dataacquisition.DataHandlerService;
import de.mpg.mpdl.inge.dataacquisition.DataSourceHandlerService;
import de.mpg.mpdl.inge.dataacquisition.DataaquisitionException;
import de.mpg.mpdl.inge.dataacquisition.Util;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.FullTextVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationServiceException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.StagedFileDbVO;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
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
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItem;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.editItem.IdentifierCollection;
import de.mpg.mpdl.inge.pubman.web.editItem.SourceBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.GenreSpecificItemManager;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Fragment class for the easy submission. This class provides all functionality for editing, saving
 * and submitting a PubItem within the easy submission process.
 * 
 * @author: Tobias Schraut, created 04.04.2008
 * @version: $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "EasySubmission")
@SuppressWarnings("serial")
public class EasySubmission extends FacesBean {
  private static final Logger logger = Logger.getLogger(EasySubmission.class);

  public static final String LOAD_EASYSUBMISSION = "loadEasySubmission";
  public static final String INTERNAL_MD_FORMAT = "eSciDoc-publication-item";

  public static final String REST_SERVICE_URL = PropertyReader.getProperty(PropertyReader.INGE_REST_SERVICE_URL);
  public static final String REST_COMPONENT_PATH = PropertyReader.getProperty(PropertyReader.INGE_REST_FILE_PATH);

  public SelectItem SUBMISSION_METHOD_MANUAL = new SelectItem("MANUAL", this.getLabel("easy_submission_method_manual"));
  public SelectItem SUBMISSION_METHOD_FETCH_IMPORT = new SelectItem("FETCH_IMPORT", this.getLabel("easy_submission_method_fetch_import"));
  public SelectItem[] SUBMISSION_METHOD_OPTIONS = new SelectItem[] {this.SUBMISSION_METHOD_MANUAL, this.SUBMISSION_METHOD_FETCH_IMPORT};

  private final DataSourceHandlerService dataSourceHandler = new DataSourceHandlerService();

  private HtmlSelectOneMenu genreSelect = new HtmlSelectOneMenu();
  private HtmlSelectOneRadio radioSelect;
  private HtmlSelectOneRadio radioSelectFulltext = new HtmlSelectOneRadio();
  private IdentifierCollection identifierCollection;
  private List<DataSourceVO> dataSources = new ArrayList<DataSourceVO>();
  private SelectItem[] locatorVisibilities;
  private String alternativeLanguageName;
  private String contextName = null;
  private String hiddenAlternativeTitlesField;
  private String hiddenIdsField;
  private String locatorUpload;
  private String selectedDate;
  private String serviceID;
  private boolean overwriteCreators;

  public EasySubmission() {
    this.init();
  }

  public void init() {
    this.SUBMISSION_METHOD_MANUAL = new SelectItem("MANUAL", this.getLabel("easy_submission_method_manual"));
    this.SUBMISSION_METHOD_FETCH_IMPORT = new SelectItem("FETCH_IMPORT", this.getLabel("easy_submission_method_fetch_import"));
    this.SUBMISSION_METHOD_OPTIONS = new SelectItem[] {this.SUBMISSION_METHOD_MANUAL, this.SUBMISSION_METHOD_FETCH_IMPORT};
    this.locatorVisibilities = this.getI18nHelper().getSelectItemsVisibility(true);

    // if the user has reached Step 3, an item has already been created and must be set in the
    // EasySubmissionSessionBean for further manipulation
    if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP2)
        || this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP3)) {
      this.getEasySubmissionSessionBean().checkMinAnzLocators();
      //      if (this.getLocators() == null || this.getLocators().size() == 0) {
      ////        String contentCategory = null;
      ////        if (PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL") != null) {
      ////          contentCategory = PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL");
      ////        } else {
      ////          final Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
      ////          if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
      ////            contentCategory = contentCategoryMap.values().iterator().next();
      ////          } else {
      ////            this.error(this.getMessage("NoContentCategory"));
      ////            Logger.getLogger(PubFileVOPresentation.class).warn("WARNING: no content-category has been defined in Genres.xml");
      ////          }
      ////        }
      //
      //        final FileDbVO newLocator = new FileDbVO();
      //        newLocator.setStorage(FileDbVO.Storage.EXTERNAL_URL);
      //        newLocator.setVisibility(FileDbVO.Visibility.PUBLIC);
      //        newLocator.setMetadata(new MdsFileVO());
      ////        newLocator.getMetadata().setContentCategory(contentCategory);
      //        this.getLocators().add(new PubFileVOPresentation(0, newLocator, true));
      //      }
    }

    if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP4)) {
      if (this.getItem().getMetadata() != null && this.getItem().getMetadata().getCreators() != null) {
        for (final CreatorVO creatorVO : this.getItem().getMetadata().getCreators()) {
          if (creatorVO.getType() == CreatorType.PERSON && creatorVO.getPerson() == null) {
            creatorVO.setPerson(new PersonVO());
          } else if (creatorVO.getType() == CreatorType.ORGANIZATION && creatorVO.getOrganization() == null) {
            creatorVO.setOrganization(new OrganizationVO());
          }
        }
      }

      if (this.getEasySubmissionSessionBean().getCreators().size() == 0) {
        this.getEasySubmissionSessionBean().bindCreatorsToBean(this.getItem().getMetadata().getCreators());
      }

      if (this.getEasySubmissionSessionBean().getCreatorOrganizations().size() == 0) {
        this.getEasySubmissionSessionBean().initOrganizationsFromCreators();
      }
    }

    if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP5)) {
      this.identifierCollection = new IdentifierCollection(this.getItem().getMetadata().getIdentifiers());
    }

    // Get informations about import sources if submission method = fetching import
    if ((this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP2)
        || this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP3))
        && this.getEasySubmissionSessionBean().getCurrentSubmissionMethod().equals("FETCH_IMPORT")) {

      // Call source initialization only once
      if (!this.getEasySubmissionSessionBean().isImportSourceRefresh()) {
        this.getEasySubmissionSessionBean().setImportSourceRefresh(true);
        this.setImportSourcesInfo();
      }
    } else {
      this.getEasySubmissionSessionBean().setImportSourceRefresh(false);
    }

    if (this.getItem() != null && this.getItem().getMetadata() != null && this.getItem().getMetadata().getGenre() == null) {
      this.getItem().getMetadata().setGenre(Genre.ARTICLE);
    }
  }

  public void selectSubmissionMethod() {
    final String submittedValue = CommonUtils.getUIValue(this.radioSelect);

    this.getEasySubmissionSessionBean().setCurrentSubmissionMethod(submittedValue);

    // select the default context if only one exists
    final List<PubContextVOPresentation> depositorContextList = this.getDepositorContextList();

    if (depositorContextList != null && depositorContextList.size() == 1) {
      depositorContextList.get(0).setSelected(false);
      depositorContextList.get(0).selectForEasySubmission();
    }

    // set the current submission step to step2
    this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
  }

  public String newEasySubmission() {
    this.setItem(null);

    this.getEasySubmissionSessionBean().cleanup();

    //    // also make sure that the EditItemSessionBean is cleaned, too
    //    this.getFiles().clear();
    //    this.getLocators().clear();

    // deselect the selected context
    final List<PubContextVOPresentation> depositorContextList = this.getDepositorContextList();
    if (depositorContextList != null) {
      for (int i = 0; i < depositorContextList.size(); i++) {
        depositorContextList.get(i).setSelected(false);
      }
    }

    // set the current submission step to step2
    if (depositorContextList != null && depositorContextList.size() > 1) {
      // create a dummy item in the first context to avoid an empty item
      depositorContextList.get(0).selectForEasySubmission();
      this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
    } else { // Skip Collection selection for Import & Easy Sub if only one Collection
      depositorContextList.get(0).selectForEasySubmission();
      this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);
      this.init();
    }

    // set method to manual
    this.getEasySubmissionSessionBean().setCurrentSubmissionMethod(EasySubmissionSessionBean.SUBMISSION_METHOD_MANUAL);

    // set the current submission method for edit item to easy submission (for GUI purpose)
    this.getEditItemSessionBean().setCurrentSubmission(EditItemSessionBean.SUBMISSION_METHOD_EASY_SUBMISSION);

    return "loadNewEasySubmission";
  }

  public String newImport() {
    this.setItem(null);

    this.getEasySubmissionSessionBean().cleanup();

    //    // also make sure that the EditItemSessionBean is cleaned, too
    //    this.getFiles().clear();
    //    this.getLocators().clear();

    // deselect the selected context
    final List<PubContextVOPresentation> depositorContextList = this.getDepositorContextList();

    if (depositorContextList != null) {
      for (int i = 0; i < depositorContextList.size(); i++) {
        depositorContextList.get(i).setSelected(false);
      }
    }

    // set method to import
    this.getEasySubmissionSessionBean().setCurrentSubmissionMethod(EasySubmissionSessionBean.SUBMISSION_METHOD_FETCH_IMPORT);

    // set the current submission step to step2
    if (depositorContextList != null && depositorContextList.size() > 1) {
      this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
      // set the current submission method for edit item to import (for GUI purpose)
      this.getEditItemSessionBean().setCurrentSubmission(EditItemSessionBean.SUBMISSION_METHOD_IMPORT);

      return "loadNewFetchMetadata";

    } else { // Skip Collection selection for Import & Easy Sub if only one Collection
      depositorContextList.get(0).selectForEasySubmission();
      this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);
      // set the current submission method for edit item to import (for GUI purpose)
      this.getEditItemSessionBean().setCurrentSubmission(EditItemSessionBean.SUBMISSION_METHOD_IMPORT);

      this.init();

      return "loadNewFetchMetadata";
    }
  }

  //  /**
  //   * This method adds a file to the list of files of the item
  //   * 
  //   * @return navigation string (null)
  //   */
  //  public String addFile() {
  //    this.upload(true);
  //    this.saveLocator();
  //
  //    final List<PubFileVOPresentation> files = this.getFiles();
  //
  //    if (files != null && files.size() > 0 && files.get(files.size() - 1).getFile().getSize() > 0) {
  //
  //      final FileDbVO newFile = new FileDbVO();
  //      newFile.setStorage(FileDbVO.Storage.INTERNAL_MANAGED);
  //      newFile.setVisibility(FileDbVO.Visibility.PUBLIC);
  //      newFile.setMetadata(new MdsFileVO());
  //
  //      files.add(new PubFileVOPresentation(files.size(), newFile, false));
  //    }
  //
  //    return "loadNewEasySubmission";
  //  }

  /**
   * This method adds a locator to the list of locators of the item
   * 
   * @return navigation string
   */
  public String addLocator() {
    if (this.getLocators() != null) {
      final FileDbVO newLocator = new FileDbVO();
      newLocator.setMetadata(new MdsFileVO());
      newLocator.setStorage(FileDbVO.Storage.EXTERNAL_URL);
      this.getLocators().add(new PubFileVOPresentation(this.getLocators().size(), newLocator, true));
    }

    return "loadNewEasySubmission";

  }

  //  /**
  //   * This method adds a locator to the list of files of the item
  //   * 
  //   * @return navigation string (null)
  //   */
  //  public String addLocator() {
  //    this.upload(true);
  //    this.saveLocator();
  //
  //    final List<PubFileVOPresentation> locators = this.getLocators();
  //
  //    if (locators != null && locators.get(locators.size() - 1).getFile().getContent() != null
  //        && !locators.get(locators.size() - 1).getFile().getContent().trim().equals("")) {
  //
  //      String contentCategory = null;
  //      if (PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL") != null) {
  //        contentCategory = PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL");
  //      } else {
  //        final Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
  //        if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
  //          contentCategory = contentCategoryMap.values().iterator().next();
  //        } else {
  //          this.error(this.getMessage("NoContentCategory"));
  //          Logger.getLogger(PubFileVOPresentation.class).warn("WARNING: no content-category has been defined in Genres.xml");
  //        }
  //      }
  //
  //      final PubFileVOPresentation newLocator = new PubFileVOPresentation(locators.size(), true);
  //
  //      newLocator.getFile().setVisibility(FileDbVO.Visibility.PUBLIC);
  //      newLocator.getFile().setMetadata(new MdsFileVO());
  //      newLocator.getFile().getMetadata().setContentCategory(contentCategory);
  //      locators.add(newLocator);
  //    }
  //
  //    return "loadNewEasySubmission";
  //  }

  /**
   * This method binds the uploaded files to the files in the PubItem during the save process
   */
  private void bindUploadedFiles() {
    this.getItem().getFiles().clear();

    final List<PubFileVOPresentation> files = this.getFiles();

    if (files != null && files.size() > 0) {
      for (int i = 0; i < files.size(); i++) {
        this.getItem().getFiles().add(files.get(i).getFile());
      }
    }

    final List<PubFileVOPresentation> locators = this.getLocators();

    if (locators != null && locators.size() > 0) {
      for (int i = 0; i < locators.size(); i++) {
        this.getItem().getFiles().add(locators.get(i).getFile());
      }
    }
  }

  /**
   * This method saves the latest locator to the list of files of the item
   * 
   * @return navigation string
   */
  public String saveLocator() {
    final int indexUpload = this.getLocators().size() - 1;
    if (this.getLocators() != null) {
      // Set empty MetadataSet if none exists
      if (this.getLocators().get(indexUpload).getFile().getMetadata() == null) {
        this.getLocators().get(indexUpload).getFile().setMetadata(new MdsFileVO());
      }
      // Set file name if not filled
      if (this.getLocators().get(indexUpload).getFile().getMetadata().getTitle() == null
          || this.getLocators().get(indexUpload).getFile().getMetadata().getTitle().trim().equals("")) {
        this.getLocators().get(indexUpload).getFile().getMetadata()
            .setTitle(this.getLocators().get(indexUpload).getFile().getContent().trim());
      }
      final List<PubFileVOPresentation> list = this.getLocators();
      final PubFileVOPresentation pubFile = list.get(indexUpload);
      list.set(indexUpload, pubFile);
      this.setLocators(list);
    }

    return "loadNewEasySubmission";
  }

  //  public String saveLocator() {
  //    final List<PubFileVOPresentation> locators = this.getLocators();
  //
  //    if (locators.get(locators.size() - 1).getFile().getMetadata().getTitle() == null
  //        || locators.get(locators.size() - 1).getFile().getMetadata().getTitle().trim().equals("")) {
  //      locators.get(locators.size() - 1).getFile().getMetadata().setTitle(locators.get(locators.size() - 1).getFile().getContent());
  //    }
  //
  //    //TODO
  //    // set a dummy file size for rendering purposes
  //    if (locators.get(locators.size() - 1).getFile().getContent() != null
  //        && !locators.get(locators.size() - 1).getFile().getContent().trim().equals("")) {
  //      locators.get(locators.size() - 1).getFile().setSize(11);
  //    }
  //
  //    // Visibility PUBLIC is static default value for locators
  //    locators.get(locators.size() - 1).getFile().setVisibility(Visibility.PUBLIC);
  //
  //    return "loadNewEasySubmission";
  //  }

  /**
   * This method reorganizes the index property in PubFileVOPresentation after removing one element
   * of the list.
   */
  public void reorganizeFileIndexes() {
    final List<PubFileVOPresentation> files = this.getFiles();

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
    final List<PubFileVOPresentation> locators = this.getLocators();

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
    this.bindUploadedFiles();
    this.parseAndSetAlternativeSourceTitlesAndIds();

    ((EditItem) FacesTools.findBean("EditItem")).setFromEasySubmission(true);

    if (this.validate(ValidationPoint.STANDARD, ViewItemFull.LOAD_VIEWITEM) == null) {
      return "";
    }

    String returnValue;
    try {
      returnValue = this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM);

      if (returnValue != null && !"".equals(returnValue)) {
        this.getEasySubmissionSessionBean().cleanup();
      }
      this.getPubItemListSessionBean().update();
      return returnValue;
    } catch (ValidationException e) {
      for (final ValidationReportItemVO item : e.getReport().getItems()) {
        this.error(this.getMessage(item.getContent()).replaceAll("\\$1", item.getElement()));
      }
    }

    return "";
  }

  /**
   * Uploads a file
   * 
   * @param event
   */
  public void fileUploaded(FileUploadEvent event) {
    this.stageFile(event.getFile());
  }

  private String stageFile(UploadedFile file) {
    if (file != null && file.getSize() > 0) {
      try {
        String path = null;
        try {
          StagedFileDbVO stagedFile = ApplicationBean.INSTANCE.getFileService().createStageFile(file.getInputstream(), file.getFileName(),
              getLoginHelper().getAuthenticationToken());
          path = String.valueOf(stagedFile.getId());
        } catch (Exception e) {
          logger.error("Could not upload staged file [" + path + "]", e);
          this.error(this.getMessage("File_noUpload") + "[" + path + "]");
        }

        String mimeType = null;
        final Tika tika = new Tika();
        try {
          final InputStream fis = file.getInputstream();
          mimeType = tika.detect(fis, file.getFileName());
          fis.close();
        } catch (final IOException e) {
          logger.error("Error while trying to detect mimetype of file " + file.getFileName(), e);
        }

        final FormatVO formatVO = new FormatVO();
        formatVO.setType("dcterms:IMT");
        formatVO.setValue(mimeType);

        final MdsFileVO mdsFileVO = new MdsFileVO();
        mdsFileVO.getFormats().add(formatVO);
        mdsFileVO.getIdentifiers().add(new IdentifierVO());
        mdsFileVO.setTitle(file.getFileName());

        final FileDbVO fileVO = new FileDbVO();
        fileVO.setMetadata(mdsFileVO);

        fileVO.getAllowedAudienceIds().add(null);
        fileVO.setContent(path);
        fileVO.setMimeType(mimeType);
        fileVO.setName(file.getFileName());
        fileVO.setSize((int) file.getSize());
        fileVO.setStorage(FileDbVO.Storage.INTERNAL_MANAGED);
        fileVO.setVisibility(FileDbVO.Visibility.PUBLIC);

        this.getFiles().add(new PubFileVOPresentation(this.getFiles().size(), fileVO, false));
        this.init();
      } catch (Exception e) {
        e.printStackTrace();
        this.error(this.getMessage("Error uploading file"));
      }
    } else {
      this.error(this.getMessage("ComponentEmpty"));
    }

    return "loadNewEasySubmission";
  }

  //  /**
  //   * This method uploads a selected file and gives out error messages if needed
  //   * 
  //   * @param needMessages Flag to invoke error messages (set it to false if you invoke the validation
  //   *        service before or after)
  //   * @return String navigation string
  //   * @author schraut
  //   */
  //  public String upload(boolean needMessages) {
  //    if (this.uploadedFile != null) {
  //      final UploadedFile file = this.uploadedFile;
  //      final StringBuffer errorMessage = new StringBuffer();
  //      if (file != null) {
  //
  //        final String contentURL = this.uploadFile(file);
  //        final String fixedFileName = CommonUtils.fixURLEncoding(file.getFileName());
  //        if (contentURL != null && !contentURL.trim().equals("")) {
  //          final FileDbVO newFile = new FileDbVO();
  //          newFile.setStorage(FileDbVO.Storage.INTERNAL_MANAGED);
  //          newFile.setVisibility(FileDbVO.Visibility.PUBLIC);
  //          newFile.setMetadata(new MdsFileVO());
  //
  //          this.getFiles().add(new PubFileVOPresentation(this.getFiles().size(), newFile, false));
  //
  //          newFile.getMetadata().setTitle(fixedFileName);
  //          newFile.setName(fixedFileName);
  //          newFile.setMimeType(file.getContentType());
  //          newFile.setSize((int) file.getSize());
  //          final FormatVO formatVO = new FormatVO();
  //          formatVO.setType("dcterms:IMT");
  //          formatVO.setValue(newFile.getMimeType());
  //          newFile.getMetadata().getFormats().add(formatVO);
  //          newFile.setContent(contentURL);
  //        }
  //
  //        this.init();
  //      }
  //
  //      if (errorMessage.length() > 0) {
  //        this.error(errorMessage.toString());
  //      }
  //    }
  //
  //    return "loadNewEasySubmission";
  //  }

  //  /**
  //   * Uploads a file to the FIZ Framework and recieves and returns the location of the file in the FW
  //   * 
  //   * @param file
  //   * @return
  //   */
  //  private String uploadFile(UploadedFile file) {
  //
  //    String path = null;
  //    try {
  //      StagedFileDbVO stagedFile = ApplicationBean.INSTANCE.getFileService().createStageFile(file.getInputstream(), file.getFileName(),
  //          getLoginHelper().getAuthenticationToken());
  //      path = String.valueOf(stagedFile.getId());
  //    } catch (Exception e) {
  //      logger.error("Could not upload staged file [" + path + "]", e);
  //      this.error((this.getMessage("File_noUpload") + "[" + path + "]"));
  //    }
  //    return path;
  //  }


  /**
   * Handles the import from an external ingestion sources.
   * 
   * @return navigation String
   */
  public String harvestData() {
    if (this.getServiceID() == null || "".equals(this.getServiceID())) {
      this.warn(this.getMessage("easy_submission_external_service_no_id"));
      return null;
    }

    final DataHandlerService dataHandler = new DataHandlerService();
    final List<FileDbVO> fileVOs = new ArrayList<FileDbVO>();
    String fetchedItem = null;

    try {
      // Im Moment nur arXiv + Crossref -> definiert in sources.xml
      final String source = this.getEasySubmissionSessionBean().getCurrentExternalServiceType();

      final DataSourceVO dataSourceVO = this.dataSourceHandler.getSourceByName(source);
      final String identifier = Util.trimIdentifier(dataSourceVO, this.getServiceID());

      // Harvest metadata
      final byte[] fetchedItemByte = dataHandler.doFetchMetaData(source, dataSourceVO, identifier, TransformerFactory.getInternalFormat());

      fetchedItem = new String(fetchedItemByte, 0, fetchedItemByte.length);

      // Harvest full text
      if (this.getEasySubmissionSessionBean().isFulltext() && !fetchedItem.equals("")
          && !EasySubmissionSessionBean.FULLTEXT_NONE.equals(this.getEasySubmissionSessionBean().getRadioSelectFulltext())) {

        final List<FullTextVO> ftFormats = dataSourceVO.getFtFormats();
        final List<String> fullTextFormats = new ArrayList<String>();

        if (EasySubmissionSessionBean.FULLTEXT_DEFAULT.equals(this.getEasySubmissionSessionBean().getRadioSelectFulltext())) {
          for (FullTextVO fulltextVO : ftFormats) {
            if (fulltextVO.isFtDefault()) {
              FileFormatVO.FILE_FORMAT fileFormat = FileFormatVO.getFileFormat(fulltextVO.getName());
              fullTextFormats.add(fileFormat.getExtension());
              break;
            }
          }
        } else if (EasySubmissionSessionBean.FULLTEXT_ALL.equals(this.getEasySubmissionSessionBean().getRadioSelectFulltext())) {
          for (FullTextVO fulltextVO : ftFormats) {
            FileFormatVO.FILE_FORMAT fileFormat = FileFormatVO.getFileFormat(fulltextVO.getName());
            fullTextFormats.add(fileFormat.getExtension());
          }
        }

        final byte[] ba =
            dataHandler.doFetchFullText(dataSourceVO, identifier, fullTextFormats.toArray(new String[fullTextFormats.size()]));

        final ByteArrayInputStream in = new ByteArrayInputStream(ba);
        String fileId = null;
        String fileName = this.getServiceID().trim() + dataHandler.getFileEnding();
        try {
          StagedFileDbVO stagedFile =
              ApplicationBean.INSTANCE.getFileService().createStageFile(in, fileName, getLoginHelper().getAuthenticationToken());
          fileId = String.valueOf(stagedFile.getId());
        } catch (Exception e) {
          logger.error("Could not upload staged file [" + fileId + "]", e);
          this.error(this.getMessage("File_noUpload") + "[" + fileId + "]");
        }

        if (fileId != null && !fileId.trim().equals("")) {
          final FileDbVO fileVO = dataHandler.getComponentVO(dataSourceVO);
          final MdsFileVO fileMd = fileVO.getMetadata();
          fileVO.setStorage(FileDbVO.Storage.INTERNAL_MANAGED);
          fileVO.setVisibility(dataHandler.getVisibility());
          fileVO.setMetadata(fileMd);
          fileVO.getMetadata().setTitle(this.replaceSlashes(this.getServiceID().trim() + dataHandler.getFileEnding()));
          fileVO.setMimeType(dataHandler.getContentType());
          fileVO.setName(this.replaceSlashes(this.getServiceID().trim() + dataHandler.getFileEnding()));
          final FormatVO formatVO = new FormatVO();
          formatVO.setType("dcterms:IMT");
          formatVO.setValue(dataHandler.getContentType());
          fileVO.getMetadata().getFormats().add(formatVO);
          fileVO.setContent(fileId);
          fileVO.setSize(ba.length);
          fileVO.getMetadata().setDescription("File downloaded from " + source + " at " + CommonUtils.currentDate());
          fileVO.getMetadata().setContentCategory(dataHandler.getContentCategory());
          fileVOs.add(fileVO);
        }
      }
    } catch (final DataaquisitionException inre) {
      EasySubmission.logger.error(inre.getMessage(), inre);
      this.error(this.getMessage("easy_submission_import_from_external_service_identifier_error") + this.getServiceID());
      return null;
    } catch (final Exception e) {
      EasySubmission.logger.error(e.getMessage(), e);
      this.error(this.getMessage("easy_submission_import_from_external_service_identifier_error") + this.getServiceID());
      return null;
    }

    // Generate item ValueObject
    if (fetchedItem != null && !fetchedItem.trim().equals("")) {
      try {
        ItemVersionVO itemVO = EntityTransformer.transformToNew(XmlTransformingService.transformToPubItem(fetchedItem));
        itemVO.getFiles().clear();
        itemVO.getObject().setContext(this.getItem().getObject().getContext());

        //        if (dataHandler.getItemUrl() != null) {
        //          final IdentifierVO id = new IdentifierVO();
        //          id.setType(IdType.URI);
        //          try {
        //            id.setId(java.net.URLDecoder.decode(dataHandler.getItemUrl().toString(), "UTF-8"));
        //            itemVO.getMetadata().getIdentifiers().add(id);
        //          } catch (final UnsupportedEncodingException e) {
        //            EasySubmission.logger.warn("Item URL could not be decoded");
        //          }
        //        }

        if (this.getEasySubmissionSessionBean().isFulltext()
            && !this.getEasySubmissionSessionBean().getRadioSelectFulltext().equals(EasySubmissionSessionBean.FULLTEXT_NONE)) {
          for (int i = 0; i < fileVOs.size(); i++) {
            final FileDbVO tmp = fileVOs.get(i);
            itemVO.getFiles().add(tmp);
          }

          fileVOs.clear();
        }

        this.getItem().setMetadata(itemVO.getMetadata());
        this.getItem().getFiles().clear();
        this.getItem().getFiles().addAll(itemVO.getFiles());
      } catch (final TechnicalException e) {
        EasySubmission.logger.warn("Error transforming item to pubItem.");
        this.error(this.getMessage("easy_submission_import_from_external_service_error"));
        return null;
      }
    } else {
      EasySubmission.logger.warn("Empty fetched Item.");
      this.error(this.getMessage("easy_submission_import_from_external_service_error"));
      return null;
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
      final String newFileName = fileName.replaceAll("\\/", "_");
      // replace back slashes
      return newFileName.replaceAll("\\\\", "_");
    }

    return "";
  }

  public void cancel() {
    this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP1);

    try {
      FacesTools.getExternalContext().redirect("faces/SubmissionPage.jsp");
    } catch (final Exception e) {
      EasySubmission.logger.error("Cancel error: could not find context to redirect to SubmissionPage.jsp in Full Submssion", e);
    }
  }

  public String loadStep2() {
    final List<PubContextVOPresentation> depositorContextList = this.getDepositorContextList();

    if (depositorContextList != null && depositorContextList.size() > 1) {
      this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
    } else {
      try {
        FacesTools.getExternalContext().redirect("faces/SubmissionPage.jsp");
      } catch (final Exception e) {
        EasySubmission.logger.error("could not find context to redirect to SubmissionPage.jsp in Easy Submssion", e);
      }
    }

    return "loadNewEasySubmission";
  }

  public String validateAndLoadStep3Manual() {
    if (this.validate(ValidationPoint.EASY_SUBMISSION_STEP_4, "loadNewEasySubmission") == null) {
      return "";
    }

    return this.loadStep3Manual();
  }

  public String loadStep3Manual() {
    this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);
    this.init();

    return "loadNewEasySubmission";
  }

  public String loadStep4Manual() {
    this.parseAndSetAlternativeSourceTitlesAndIds();
    this.saveLocator();

    final List<PubFileVOPresentation> locators = this.getLocators();
    final List<PubFileVOPresentation> files = this.getFiles();

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

    this.getEasySubmissionSessionBean().checkMinAnzLocators();

    // add an empty file and an empty locator if necessary for display purposes
    // TODO: das passiert an tausend Stellen und ist teilweise unterschiedlich. WARUM????
    // -> Suche nach Konstruktor PubFileVOPresentation(int fileIndex, FileDbVO file, boolean
    // isLocator)
    //    if (files != null && files.size() > 0) {
    //      if (files.get(files.size() - 1).getFile().getSize() > 0) {
    //        final FileDbVO newFile = new FileDbVO();
    //        newFile.setStorage(FileDbVO.Storage.INTERNAL_MANAGED);
    //        newFile.setVisibility(FileDbVO.Visibility.PUBLIC);
    //        newFile.setMetadata(new MdsFileVO());
    //        files.add(new PubFileVOPresentation(files.size(), newFile, false));
    //      }
    //    }

    //    if (locators != null && locators.size() > 0) {
    //      if (locators.get(locators.size() - 1).getFile().getSize() > 0) {
    ////        String contentCategory = null;
    ////        if (PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL") != null) {
    ////          contentCategory = PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL");
    ////        } else {
    ////          final Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
    ////          if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
    ////            contentCategory = contentCategoryMap.values().iterator().next();
    ////          } else {
    ////            this.error(this.getMessage("NoContentCategory"));
    ////            Logger.getLogger(PubFileVOPresentation.class).warn("WARNING: no content-category has been defined in Genres.xml");
    ////          }
    ////        }
    //
    //        final PubFileVOPresentation newLocator = new PubFileVOPresentation(locators.size(), true);
    //
    //        newLocator.getFile().setVisibility(FileDbVO.Visibility.PUBLIC);
    //        newLocator.getFile().setMetadata(new MdsFileVO());
    ////        newLocator.getFile().getMetadata().setContentCategory(contentCategory);
    //        locators.add(newLocator);
    //      }
    //    }

    // validate
    if (this.validate(ValidationPoint.EASY_SUBMISSION_STEP_3, "loadNewEasySubmission") == null) {
      return "";
    }

    this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP4);

    this.init();

    return "loadNewEasySubmission";
  }

  public String loadStep5Manual() {
    if (this.validate(ValidationPoint.EASY_SUBMISSION_STEP_4, "loadNewEasySubmission") == null) {
      return "";
    }

    this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP5);

    this.init();

    return "loadNewEasySubmission";
  }

  public String loadPreview() {
    this.parseAndSetAlternativeSourceTitlesAndIds();

    if (this.validate(ValidationPoint.STANDARD, "loadEditItem") != null) {
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

      final ItemVersionVO pubItem = this.getItem();

      // write creators back to VO
      if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep() == EasySubmissionSessionBean.ES_STEP4) {
        this.getEasySubmissionSessionBean().bindCreatorsToVO(pubItem.getMetadata().getCreators());
      }

      ItemVersionVO itemVO = new ItemVersionVO(pubItem);
      PubItemUtil.cleanUpItem(itemVO);

      // cleanup item according to genre specific MD specification
      final GenreSpecificItemManager itemManager = new GenreSpecificItemManager(itemVO, GenreSpecificItemManager.SUBMISSION_METHOD_EASY);
      try {
        itemVO = itemManager.cleanupItem();
      } catch (final Exception e) {
        throw new RuntimeException("Error while cleaning up item genre specificly", e);
      }

      try {
        ApplicationBean.INSTANCE.getItemValidatingService().validate(itemVO, validationPoint);
      } catch (final ValidationException e) {
        for (final ValidationReportItemVO item : e.getReport().getItems()) {
          this.error(this.getMessage(item.getContent()).replaceAll("\\$1", item.getElement()));
        }
        return null;
      } catch (final ValidationServiceException e) {
        throw new RuntimeException("Validation error", e);
      }
    } catch (final Exception e) {
      EasySubmission.logger.error("Validation error", e);
    }

    return navigateTo;
  }

  /**
   * Fill import source values dynamically from importsourceHandler
   */
  private void setImportSourcesInfo() {
    try {
      this.dataSources = this.dataSourceHandler.getSources(EasySubmission.INTERNAL_MD_FORMAT, DataSourceHandlerService.PUBLISHED);

      final List<SelectItem> v_serviceOptions = new ArrayList<SelectItem>();

      for (int i = 0; i < this.dataSources.size(); i++) {
        final DataSourceVO source = this.dataSources.get(i);
        v_serviceOptions.add(new SelectItem(source.getName()));
        this.getEasySubmissionSessionBean().setCurrentExternalServiceType(source.getName());

        SelectItem[] EXTERNAL_SERVICE_OPTIONS = new SelectItem[v_serviceOptions.size()];
        v_serviceOptions.toArray(EXTERNAL_SERVICE_OPTIONS);
        this.getEasySubmissionSessionBean().setEXTERNAL_SERVICE_OPTIONS(EXTERNAL_SERVICE_OPTIONS);

        SelectItem[] FULLTEXT_OPTIONS = new SelectItem[] {};
        this.getEasySubmissionSessionBean().setFULLTEXT_OPTIONS(FULLTEXT_OPTIONS);
        this.getEasySubmissionSessionBean().setCurrentFTLabel("");

        List<FullTextVO> ftFormats = source.getFtFormats();
        if (ftFormats.size() > 1) {
          this.getEasySubmissionSessionBean()
              .setFULLTEXT_OPTIONS(new SelectItem[] {
                  new SelectItem(EasySubmissionSessionBean.FULLTEXT_DEFAULT, this.getEasySubmissionSessionBean().getCurrentFTLabel()),
                  new SelectItem(EasySubmissionSessionBean.FULLTEXT_ALL, this.getLabel("easy_submission_lblFulltext_all")),
                  new SelectItem(EasySubmissionSessionBean.FULLTEXT_NONE, this.getLabel("easy_submission_lblFulltext_none"))});
          this.getEasySubmissionSessionBean().setRadioSelectFulltext(EasySubmissionSessionBean.FULLTEXT_DEFAULT);
        } else if (ftFormats.size() == 1) {
          this.getEasySubmissionSessionBean()
              .setFULLTEXT_OPTIONS(new SelectItem[] {
                  new SelectItem(EasySubmissionSessionBean.FULLTEXT_DEFAULT, this.getEasySubmissionSessionBean().getCurrentFTLabel()),
                  new SelectItem(EasySubmissionSessionBean.FULLTEXT_NONE, this.getLabel("easy_submission_lblFulltext_none"))});
          this.getEasySubmissionSessionBean().setRadioSelectFulltext(EasySubmissionSessionBean.FULLTEXT_DEFAULT);
        }

        if (ftFormats != null && ftFormats.size() > 0) {
          this.getEasySubmissionSessionBean().setFulltext(true);
          for (int x = 0; x < ftFormats.size(); x++) {
            final FullTextVO ft = ftFormats.get(x);
            if (ft.isFtDefault()) {
              this.getEasySubmissionSessionBean().setCurrentFTLabel(ft.getFtLabel());
            }
          }
        } else {
          this.getEasySubmissionSessionBean().setFulltext(false);
          this.getEasySubmissionSessionBean().setCurrentFTLabel("");
        }
      }
    } catch (final Exception e) {
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
    this.getEasySubmissionSessionBean().setCurrentExternalServiceType(currentSource.getName());

    final List<FullTextVO> ftFormats = currentSource.getFtFormats();

    if (ftFormats.size() > 1) {
      this.getEasySubmissionSessionBean()
          .setFULLTEXT_OPTIONS(new SelectItem[] {
              new SelectItem(EasySubmissionSessionBean.FULLTEXT_DEFAULT, this.getEasySubmissionSessionBean().getCurrentFTLabel()),
              new SelectItem(EasySubmissionSessionBean.FULLTEXT_ALL, this.getLabel("easy_submission_lblFulltext_all")),
              new SelectItem(EasySubmissionSessionBean.FULLTEXT_NONE, this.getLabel("easy_submission_lblFulltext_none"))});
      this.getEasySubmissionSessionBean().setRadioSelectFulltext(EasySubmissionSessionBean.FULLTEXT_DEFAULT);
    } else if (ftFormats.size() == 1) {
      this.getEasySubmissionSessionBean()
          .setFULLTEXT_OPTIONS(new SelectItem[] {
              new SelectItem(EasySubmissionSessionBean.FULLTEXT_DEFAULT, this.getEasySubmissionSessionBean().getCurrentFTLabel()),
              new SelectItem(EasySubmissionSessionBean.FULLTEXT_NONE, this.getLabel("easy_submission_lblFulltext_none"))});
      this.getEasySubmissionSessionBean().setRadioSelectFulltext(EasySubmissionSessionBean.FULLTEXT_DEFAULT);
    }

    if (ftFormats != null && ftFormats.size() > 0) {
      this.getEasySubmissionSessionBean().setFulltext(true);
      for (int x = 0; x < ftFormats.size(); x++) {
        final FullTextVO ft = ftFormats.get(x);
        if (ft.isFtDefault()) {
          this.getEasySubmissionSessionBean().setCurrentFTLabel(ft.getFtLabel());
        }
      }
    } else {
      this.getEasySubmissionSessionBean().setFulltext(false);
      this.getEasySubmissionSessionBean().setCurrentFTLabel("");
    }

    this.getEasySubmissionSessionBean().setCurrentExternalServiceType(newImportSource);
  }

  public void changeImportSourceListener(ValueChangeEvent evt) {
    if (evt.getNewValue() != null) {
      this.changeImportSource((String) evt.getNewValue());
    }
  }

  /**
   * localized creation of SelectItems for the genres available.
   * 
   * @return SelectItem[] with Strings representing genres.
   */
  public SelectItem[] getGenres() {
    return this.getI18nHelper().getSelectItemsForEnum(false,
        this.getItemControllerSessionBean().getCurrentContext().getAllowedGenres().toArray(new MdsPublicationVO.Genre[] {}));
  }

  /**
   * This method changes the Genre and sets the needed property file for genre specific Metadata
   * 
   * @return String null
   */
  public void changeGenre() {
    String newGenre = this.genreSelect.getSubmittedValue().toString();
    if (newGenre != null && newGenre.trim().equals("")) {
      newGenre = "ARTICLE";
      this.getItem().getMetadata().setGenre(Genre.ARTICLE);
    }

    this.getEasySubmissionSessionBean().setGenreBundle("Genre_" + newGenre);
    this.init();
  }

  public SelectItem[] getSUBMISSION_METHOD_OPTIONS() {
    return this.SUBMISSION_METHOD_OPTIONS;
  }

  public void setSUBMISSION_METHOD_OPTIONS(SelectItem[] submission_method_options) {
    this.SUBMISSION_METHOD_OPTIONS = submission_method_options;
  }

  public HtmlSelectOneRadio getRadioSelect() {
    return this.radioSelect;
  }

  public void setRadioSelect(HtmlSelectOneRadio radioSelect) {
    this.radioSelect = radioSelect;
  }

  public PubItemVOPresentation getItem() {
    return this.getItemControllerSessionBean().getCurrentPubItem();
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

  //  public UploadedFile getUploadedFile() {
  //    return this.uploadedFile;
  //  }
  //
  //  public void setUploadedFile(UploadedFile uploadedFile) {
  //    this.uploadedFile = uploadedFile;
  //  }

  public String getSelectedDate() {
    return this.selectedDate;
  }

  public void setSelectedDate(String selectedDate) {
    this.selectedDate = selectedDate;
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
    return this.getI18nHelper().getSelectItemsVisibility(false);
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
    if (this.getItem().getMetadata().getAbstracts() == null || this.getItem().getMetadata().getAbstracts().size() < 1) {
      final AbstractVO newAbstract = new AbstractVO();
      this.getItem().getMetadata().getAbstracts().add(newAbstract);
    }
    return this.getItem().getMetadata().getAbstracts().get(0).getValue();
  }

  public void setAbstract(String publicationAbstract) {
    final AbstractVO newAbstract = new AbstractVO();
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
    if (this.getItem().getMetadata().getSubjects() == null || this.getItem().getMetadata().getSubjects().size() < 1) {
      this.getItem().getMetadata().getSubjects().add(new SubjectVO());
    }

    return this.getItem().getMetadata().getSubjects().get(0).getValue();
  }

  public void setSubject(String publicationSubject) {
    final SubjectVO newSubject = new SubjectVO(publicationSubject);
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
    this.getItem().getMetadata().setFreeKeywords(publicationSubject);
  }

  /**
   * Returns all options for content category.
   * 
   * @return all options for content category.
   */
  public SelectItem[] getContentCategories() {
    return this.getI18nHelper().getSelectItemsContentCategory(true);
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

    final List<PubContextVOPresentation> depositorContextList = this.getDepositorContextList();

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
    if (this.getItem().getMetadata().getSources() == null || this.getItem().getMetadata().getSources().size() < 1) {
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
    final SourceVO source = this.getItem().getMetadata().getSources().get(0);
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
      final IdentifierVO identifier = new IdentifierVO();
      this.getItem().getMetadata().getSources().get(0).getIdentifiers().add(identifier);
    }

    return this.getItem().getMetadata().getSources().get(0).getIdentifiers().get(0).getId();
  }

  public void setSourceIdentifier(String id) {
    final ItemVersionVO pubItem = this.getItem();
    pubItem.getMetadata().getSources().get(0).getIdentifiers().get(0).setId(id);
    if (!id.trim().equals("")) {
      pubItem.getMetadata().getSources().get(0).getIdentifiers().get(0).setType(IdType.OTHER);
    }
  }

  // source identifier
  public void setSourceIdentifierType(String typeString) {
    if (typeString != null) {
      this.getItem().getMetadata().getSources().get(0).getIdentifiers().get(0).setTypeString(typeString);
    }
  }

  public String getSourceIdentifierType() {
    if (this.getItem().getMetadata().getSources().get(0).getIdentifiers().size() == 0) {
      final IdentifierVO identifier = new IdentifierVO();
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
    final Map<String, String> excludedSourceGenres = ApplicationBean.INSTANCE.getExcludedSourceGenreMap();

    final List<SelectItem> sourceGenres = new ArrayList<SelectItem>();
    sourceGenres.add(new SelectItem("", this.getLabel("EditItem_NO_ITEM_SET")));
    for (final SourceVO.Genre value : SourceVO.Genre.values()) {
      sourceGenres.add(new SelectItem(value, this.getLabel("ENUM_GENRE_" + value.name())));
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
    if (this.getItem().getMetadata().getSources() != null && this.getItem().getMetadata().getSources().size() > 0) {
      return this.getItem().getMetadata().getSources().get(0);
    }

    return null;
  }

  public void setSource(SourceVO source) {
    if (this.getItem().getMetadata().getSources() != null && this.getItem().getMetadata().getSources().size() > 0) {
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
      this.getEasySubmissionSessionBean().parseCreatorString(this.getCreatorParseString(), null,
          this.getEasySubmissionSessionBean().getOverwriteCreators());
      this.setCreatorParseString("");
      this.getEasySubmissionSessionBean().initAuthorCopyPasteCreatorBean();
      return "loadNewEasySubmission";
    } catch (final Exception e) {
      this.error(this.getMessage("ErrorParsingCreatorString"));
      return "loadNewEasySubmission";
    }
  }

  public HtmlSelectOneRadio getRadioSelectFulltext() {
    return this.radioSelectFulltext;
  }

  public void setRadioSelectFulltext(HtmlSelectOneRadio radioSelectFulltext) {
    this.radioSelectFulltext = radioSelectFulltext;
  }

  /**
   * Returns all options for degreeType.
   * 
   * @return all options for degreeType
   */
  public SelectItem[] getDegreeTypes() {
    return this.getI18nHelper().getSelectItemsDegreeType(true);
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
    if (this.getHiddenAlternativeTitlesField() != null && !this.getHiddenAlternativeTitlesField().trim().equals("")) {
      final SourceVO source = this.getSource();
      source.getAlternativeTitles().clear();
      source.getAlternativeTitles().addAll(SourceBean.parseAlternativeTitles(this.getHiddenAlternativeTitlesField()));
    }

    if (this.getHiddenIdsField() != null && !this.getHiddenIdsField().trim().equals("")) {
      final List<IdentifierVO> identifiers = this.getSource().getIdentifiers();
      identifiers.clear();
      identifiers.addAll(SourceBean.parseIdentifiers(this.getHiddenIdsField()));
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
    if (this.getItem().getMetadata().getEvent() != null && this.getItem().getMetadata().getEvent().getInvitationStatus() != null
        && this.getItem().getMetadata().getEvent().getInvitationStatus().equals(EventVO.InvitationStatus.INVITED)) {
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
        final ContextDbVO context =
            this.getItemControllerSessionBean().retrieveContext(this.getItem().getObject().getContext().getObjectId());
        this.contextName = context.getName();
        return this.contextName;
      } catch (final Exception e) {
        EasySubmission.logger.error("Could not retrieve the requested context." + "\n" + e.toString());
        ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
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
    final LocatorUploadBean locatorBean = new LocatorUploadBean();
    final boolean check = locatorBean.checkLocator(this.getLocatorUpload());

    if (check) {
      locatorBean.locatorUploaded();
    }

    if (locatorBean.getError() != null) {
      this.error(this.getMessage("errorLocatorMain").replace("$1", locatorBean.getError()));
    } else {
      this.setLocatorUpload("");
    }
  }

  public String getLocatorUpload() {
    return this.locatorUpload;
  }

  public void setLocatorUpload(String locatorUpload) {
    this.locatorUpload = locatorUpload;
  }

  public String getAlternativeLanguageName() {
    return this.alternativeLanguageName;
  }

  public void setAlternativeLanguageName(String alternativeLanguageName) {
    this.alternativeLanguageName = alternativeLanguageName;
  }

  private ContextListSessionBean getContextListSessionBean() {
    return (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
  }

  private EasySubmissionSessionBean getEasySubmissionSessionBean() {
    return (EasySubmissionSessionBean) FacesTools.findBean("EasySubmissionSessionBean");
  }

  private EditItemSessionBean getEditItemSessionBean() {
    return (EditItemSessionBean) FacesTools.findBean("EditItemSessionBean");
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  private PubItemListSessionBean getPubItemListSessionBean() {
    return (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
  }
}
