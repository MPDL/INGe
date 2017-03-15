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
package de.mpg.mpdl.inge.pubman.web.editItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.sun.faces.facelets.component.UIRepeat;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AdminDescriptorVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.SubjectClassification;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.acceptItem.AcceptItem;
import de.mpg.mpdl.inge.pubman.web.acceptItem.AcceptItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.affiliation.AffiliationSessionBean;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.editItem.bean.IdentifierCollection;
import de.mpg.mpdl.inge.pubman.web.editItem.bean.SourceBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.submitItem.SubmitItem;
import de.mpg.mpdl.inge.pubman.web.submitItem.SubmitItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.GenreSpecificItemManager;
import de.mpg.mpdl.inge.pubman.web.util.ListItem;
import de.mpg.mpdl.inge.pubman.web.util.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.PubFileVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.PubItemVOPresentation.WrappedLocalTag;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;
import de.mpg.mpdl.inge.pubman.web.yearbook.YearbookInvalidItemRO;
import de.mpg.mpdl.inge.pubman.web.yearbook.YearbookItemSessionBean;
import de.mpg.mpdl.inge.util.AdminHelper;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ProxyHelper;

/**
 * Fragment class for editing PubItems. This class provides all functionality for editing, saving
 * and submitting a PubItem including methods for depending dynamic UI components.
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT: 09.08.2007
 */
@SuppressWarnings("serial")
public class EditItem extends FacesBean {
  public static final String BEAN_NAME = "EditItem";

  private static final Logger logger = Logger.getLogger(EditItem.class);

  public static final String AUTOPASTE_INNER_DELIMITER = " @@~~@@ ";
  public static final String LOAD_EDITITEM = "loadEditItem";

  private HtmlCommandLink lnkSave = new HtmlCommandLink();
  private HtmlCommandLink lnkSaveAndSubmit = new HtmlCommandLink();
  private HtmlCommandLink lnkDelete = new HtmlCommandLink();
  private HtmlCommandLink lnkAccept = new HtmlCommandLink();
  private HtmlCommandLink lnkRelease = new HtmlCommandLink();
  private HtmlCommandLink lnkReleaseReleasedItem = new HtmlCommandLink();

  private String contextName = null;
  // FIXME delegated internal collections
  private String hiddenAlternativeTitlesField;
  private IdentifierCollection identifierCollection;
  private List<ListItem> languages = null;
  private String locatorUpload;
  private PubItemVOPresentation item = null;
  private boolean fromEasySubmission = false;
  private String suggestConeUrl = null;
  private HtmlSelectOneMenu genreSelect = new HtmlSelectOneMenu();
  // Flag for the binding method to avoid unnecessary binding
  private boolean bindFilesAndLocators = true;
  private UIRepeat fileIterator;

  public EditItem() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation.
   */
  public void init() {
    this.enableLinks();

    try {
      this.initializeItem();
    } catch (Exception e) {
      throw new RuntimeException("Error initializing item", e);
    }

    // if item is currently part of invalid yearbook items, show Validation Messages
    if (getItem() == null) {
      return;
    }

    if (getItem().getVersion() != null && getItem().getVersion().getObjectId() != null
        && getLoginHelper().getIsYearbookEditor()) {
      if (this.getYearbookItemSessionBean().getYearbookItem() != null
          && this.getYearbookItemSessionBean().getInvalidItemMap()
              .get(getItem().getVersion().getObjectId()) != null) {
        try {
          this.getYearbookItemSessionBean().validateItem(getItem());
          YearbookInvalidItemRO invItem =
              this.getYearbookItemSessionBean().getInvalidItemMap()
                  .get(getItem().getVersion().getObjectId());

          if (invItem != null) {
            (this.getPubItem()).setValidationReport(invItem.getValidationReport());
          }
        } catch (Exception e) {
          logger.error("Error in Yaerbook validation", e);
        }
      }
    }

    // FIXME provide access to parts of my VO to specialized POJO's
    this.identifierCollection =
        new IdentifierCollection(this.getPubItem().getMetadata().getIdentifiers());

    this.getAffiliationSessionBean().setBrowseByAffiliation(true);
    this.contextName = this.getContextName();
  }

  public String acceptLocalTags() {
    getPubItem().writeBackLocalTags(null);
    if (getPubItem().getVersion().getState().equals(State.RELEASED)) {
      this.bindFilesAndLocators = false;
      return saveAndAccept();
    } else {
      this.bindFilesAndLocators = false;
      save();
    }

    return null;
  }

  /**
   * Delivers a reference to the currently edited item. This is a shortCut for the method in the
   * ItemController.
   * 
   * @return the item that is currently edited
   */
  public PubItemVOPresentation getPubItem() {
    if (this.item == null) {
      this.item = this.getItemControllerSessionBean().getCurrentPubItem();
    }

    return this.item;
  }

  public String getContextName() {
    if (this.contextName == null && this.getPubItem() != null) {
      try {
        ContextVO context =
            this.getItemControllerSessionBean().retrieveContext(
                this.getPubItem().getContext().getObjectId());
        return context.getName();
      } catch (Exception e) {
        logger.error("Could not retrieve the requested context." + "\n" + e.toString());
        ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
        return ErrorPage.LOAD_ERRORPAGE;
      }
    }

    return this.contextName;
  }

  /**
   * Adds sub-ValueObjects to an item initially to be able to bind uiComponents to them.
   */
  private void initializeItem() throws Exception {
    // get the item that is currently edited
    PubItemVO pubItem = this.getPubItem();
    if (pubItem != null) {
      // set the default genre to article
      if (pubItem.getMetadata().getGenre() == null) {
        pubItem.getMetadata().setGenre(Genre.ARTICLE);
        this.getEditItemSessionBean().setGenreBundle("Genre_" + Genre.ARTICLE.toString());
      } else { // if(this.getEditItemSessionBean().getGenreBundle().trim().equals(""))
        this.getEditItemSessionBean().setGenreBundle(
            "Genre_" + pubItem.getMetadata().getGenre().name());
      }

      this.getItemControllerSessionBean().initializeItem(pubItem);

      if (!this.getEditItemSessionBean().isFilesInitialized()
          || this.getEditItemSessionBean().getLocators().size() == 0) {
        bindFiles();
        this.getEditItemSessionBean().setFilesInitialized(true);
      }

      if (this.getEditItemSessionBean().getSources().size() == 0) {
        this.getEditItemSessionBean().bindSourcesToBean(pubItem.getMetadata().getSources());
      }

      if (pubItem.getMetadata() != null && pubItem.getMetadata().getCreators() != null) {
        for (CreatorVO creatorVO : pubItem.getMetadata().getCreators()) {
          if (creatorVO.getType() == CreatorType.PERSON && creatorVO.getPerson() == null) {
            creatorVO.setPerson(new PersonVO());
          } else if (creatorVO.getType() == CreatorType.ORGANIZATION
              && creatorVO.getOrganization() == null) {
            creatorVO.setOrganization(new OrganizationVO());
          }
          if (creatorVO.getType() == CreatorType.PERSON
              && creatorVO.getPerson().getOrganizations() != null) {
            for (OrganizationVO organizationVO : creatorVO.getPerson().getOrganizations()) {
              if (organizationVO.getName() == null) {
                organizationVO.setName("");
              }
            }
          } else if (creatorVO.getType() == CreatorType.ORGANIZATION
              && creatorVO.getOrganization() != null
              && creatorVO.getOrganization().getName() == null) {
            creatorVO.getOrganization().setName("");
          }
        }
      }

      if (this.getEditItemSessionBean().getCreators().size() == 0) {
        this.getEditItemSessionBean().bindCreatorsToBean(pubItem.getMetadata().getCreators());
      }

      if (this.getEditItemSessionBean().getCreatorOrganizations().size() == 0) {
        this.getEditItemSessionBean().initOrganizationsFromCreators();
      }

      // Source creators
      for (SourceBean sourceBean : this.getEditItemSessionBean().getSources()) {
        SourceVO source = sourceBean.getSource();
        if (source.getCreators() != null) {
          for (CreatorVO creatorVO : source.getCreators()) {
            if (creatorVO.getType() == CreatorType.PERSON && creatorVO.getPerson() == null) {
              creatorVO.setPerson(new PersonVO());
            } else if (creatorVO.getType() == CreatorType.ORGANIZATION
                && creatorVO.getOrganization() == null) {
              creatorVO.setOrganization(new OrganizationVO());
            }
            if (creatorVO.getType() == CreatorType.PERSON
                && creatorVO.getPerson().getOrganizations() != null) {
              for (OrganizationVO organizationVO : creatorVO.getPerson().getOrganizations()) {
                if (organizationVO.getName() == null) {
                  organizationVO.setName("");
                }
              }
            } else if (creatorVO.getType() == CreatorType.ORGANIZATION
                && creatorVO.getOrganization() != null
                && creatorVO.getOrganization().getName() == null) {
              creatorVO.getOrganization().setName("");
            }
          }
        }

        if (sourceBean.getCreators().size() == 0) {
          sourceBean.bindCreatorsToBean(source.getCreators());
        }

        if (sourceBean.getCreatorOrganizations().size() == 0) {
          sourceBean.initOrganizationsFromCreators();
        }
      }
    } else {
      logger.warn("Current PubItem is NULL!");
    }
  }

  private void bindFiles() {
    List<PubFileVOPresentation> files = new ArrayList<PubFileVOPresentation>();
    List<PubFileVOPresentation> locators = new ArrayList<PubFileVOPresentation>();
    int fileCount = 0;
    int locatorCount = 0;

    // add files
    for (FileVO file : this.item.getFiles()) {
      if (file.getStorage().equals(FileVO.Storage.INTERNAL_MANAGED)) {
        // Add identifierVO if not available yet
        if (file.getDefaultMetadata() != null
            && (file.getDefaultMetadata().getIdentifiers() == null || file.getDefaultMetadata()
                .getIdentifiers().isEmpty())) {
          file.getDefaultMetadata().getIdentifiers().add(new IdentifierVO());
        }
        PubFileVOPresentation filepres = new PubFileVOPresentation(fileCount, file, false);
        files.add(filepres);
        fileCount++;
      }
    }

    this.getEditItemSessionBean().setFiles(files);

    // add locators
    for (FileVO file : this.item.getFiles()) {
      if (file.getStorage().equals(FileVO.Storage.EXTERNAL_URL)) {
        PubFileVOPresentation locatorpres = new PubFileVOPresentation(locatorCount, file, true);
        // This is a small hack for locators generated out of Bibtex files
        if (locatorpres.getLocator() == null && locatorpres.getFile() != null
            && locatorpres.getFile().getName() != null) {
          locatorpres.setLocator(locatorpres.getFile().getName().trim());
          locatorpres.getFile().getMetadataSets().add(new MdsFileVO());
          locatorpres.getFile().getDefaultMetadata().setTitle(locatorpres.getFile().getName());
        }
        // And here it ends
        locators.add(locatorpres);
        locatorCount++;
      }
    }

    this.getEditItemSessionBean().setLocators(locators);

    // make sure that at least one locator and one file is stored in the EditItemSessionBean
    /*
     * if (this.getEditItemSessionBean().getFiles().size() < 1) { FileVO newFile = new FileVO();
     * newFile.getMetadataSets().add(new MdsFileVO());
     * newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
     * this.getEditItemSessionBean().getFiles().add(new PubFileVOPresentation(0, newFile, false)); }
     */
    if (this.getEditItemSessionBean().getLocators().size() < 1) {
      FileVO newLocator = new FileVO();
      newLocator.getMetadataSets().add(new MdsFileVO());
      newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
      this.getEditItemSessionBean().getLocators()
          .add(new PubFileVOPresentation(0, newLocator, true));
    }
  }

  /**
   * This method binds the uploaded files and locators to the files in the PubItem during the save
   * process
   */
  private void bindUploadedFilesAndLocators() {
    // first clear the file list
    if (this.bindFilesAndLocators == true) {
      PubItemVOPresentation pubItem = this.getPubItem();

      pubItem.getFiles().clear();

      // add the files
      List<PubFileVOPresentation> files = this.getFiles();

      if (files != null && files.size() > 0) {
        for (int i = 0; i < files.size(); i++) {
          pubItem.getFiles().add(files.get(i).getFile());
        }
      }

      // add the locators
      List<PubFileVOPresentation> locators = this.getLocators();

      int lsize = locators.size();

      if (locators != null && lsize > 0) {
        for (PubFileVOPresentation loc : locators) {
          // add name from content if not available
          MdsFileVO defaultMetadata = loc.getFile().getDefaultMetadata();
          String title = defaultMetadata.getTitle();
          if (title == null || title.trim().equals("")) {
            defaultMetadata.setTitle(loc.getFile().getContent());
          }
          /*
           * if (defaultMetadata.getDescription() == null ||
           * defaultMetadata.getDescription().equals("")) {
           * defaultMetadata.setDescription(loc.getFile().getDescription()); }
           */

          // Visibility PUBLIC is static default value for locators
          loc.getFile().setVisibility(Visibility.PUBLIC);
          pubItem.getFiles().add(loc.getFile());
        }
      }
    } else {
      this.bindFilesAndLocators = true;
    }
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
    // Prepare the HttpMethod.
    String fwUrl = PropertyReader.getFrameworkUrl();
    PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
    // if(uploadedFile.isTempFile())
    // {
    InputStream fis = uploadedFile.getInputstream();
    method.setRequestEntity(new InputStreamRequestEntity(fis));
    /*
     * } else { method.setRequestEntity(new InputStreamRequestEntity(new
     * ByteArrayInputStream(uploadedFile.getData()))); }
     */

    method.setRequestHeader("Content-Type", mimetype);
    method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
    // Execute the method with HttpClient.
    HttpClient client = new HttpClient();
    ProxyHelper.setProxy(client, fwUrl);
    client.executeMethod(method);
    String response = method.getResponseBodyAsString();
    fis.close();

    return XmlTransformingService.transformUploadResponseToFileURL(response);
  }

  public List<ListItem> getLanguages() throws Exception {
    if (this.languages == null) {
      this.languages = new ArrayList<ListItem>();
      if (getPubItem().getMetadata().getLanguages().size() == 0) {
        getPubItem().getMetadata().getLanguages().add("");
      }
      int counter = 0;
      for (Iterator<String> iterator = getPubItem().getMetadata().getLanguages().iterator(); iterator
          .hasNext();) {
        String value = iterator.next();
        ListItem item = new ListItem();
        item.setValue(value);
        item.setIndex(counter++);
        item.setStringList(getPubItem().getMetadata().getLanguages());
        item.setItemList(this.languages);
        this.languages.add(item);
      }
    }

    return this.languages;
  }

  public SelectItem[] getLanguageOptions() {
    return CommonUtils.getLanguageOptions();
  }

  /**
   * Validates the item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String validate() {
    if (getPubItem() == null) {
      return "";
    }

    if (!restoreVO()) {
      return "";
    }

    // cleanup item according to genre specific MD specification
    GenreSpecificItemManager itemManager =
        new GenreSpecificItemManager(getPubItem(), GenreSpecificItemManager.SUBMISSION_METHOD_FULL);
    try {
      this.item = (PubItemVOPresentation) itemManager.cleanupItem();
    } catch (Exception e) {
      throw new RuntimeException("Error while cleaning up item genre specificly", e);
    }

    try {
      ItemValidatingService.validateItemObject(new PubItemVO(getPubItem()),
          ValidationPoint.STANDARD);
      String message = getMessage("itemIsValid");
      info(message);
    } catch (ItemInvalidException e) {
      this.showValidationMessages(e.getReport());
      return null;
    } catch (Exception e) {
      logger.error("Could not validate item." + "\n" + e.toString(), e);
      ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
      return ErrorPage.LOAD_ERRORPAGE;
    }

    return null;
  }

  /**
   * Saves the item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String save() {
    if (this.getPubItem() == null) {
      return "";
    }

    if (!restoreVO()) {
      return "";
    }

    // cleanup item according to genre specific MD specification
    GenreSpecificItemManager itemManager =
        new GenreSpecificItemManager(this.getPubItem(),
            GenreSpecificItemManager.SUBMISSION_METHOD_FULL);
    try {
      this.item = (PubItemVOPresentation) itemManager.cleanupItem();
    } catch (Exception e) {
      throw new RuntimeException("Error while cleaning up item genre specificly", e);
    }

    try {
      ItemValidatingService.validateItemObject(new PubItemVO(this.getPubItem()),
          ValidationPoint.SAVE);
    } catch (ItemInvalidException e) {
      this.showValidationMessages(e.getReport());
      return null;
    } catch (ValidationException e) {
      throw new RuntimeException("Validation error", e);
    }

    String retVal = "";
    try {
      retVal = this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM);
    } catch (RuntimeException rE) {
      logger.error("Error saving item", rE);
      String message = getMessage("itemHasBeenChangedInTheMeantime");
      fatal(message);
    }

    if (ViewItemFull.LOAD_VIEWITEM.equals(retVal)) {
      // set the current submission method to empty string (for GUI purpose)
      this.getEditItemSessionBean().setCurrentSubmission("");
      try {
        info(getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SAVED));
        if (isFromEasySubmission()) {
          getExternalContext().redirect(
              getRequest().getContextPath()
                  + "/faces/ViewItemFullPage.jsp?itemId="
                  + this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                      .getObjectId() + "&fromEasySub=true");
        } else {
          getExternalContext().redirect(
              getRequest().getContextPath()
                  + "/faces/ViewItemFullPage.jsp?itemId="
                  + this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                      .getObjectId());
        }
      } catch (IOException e) {
        logger.error("Could not redirect to View Item Page", e);
      }
    }

    this.getPubItemListSessionBean().update();

    return retVal;
  }

  private boolean restoreVO() {
    // bind the temporary uploaded files to the files in the current item
    bindUploadedFilesAndLocators();

    // bind Organizations To Creators
    if (!this.getEditItemSessionBean().bindOrganizationsToCreators()) {
      return false;
    }

    for (SourceBean sourceBean : this.getEditItemSessionBean().getSources()) {
      if (!sourceBean.bindOrganizationsToCreators()) {
        return false;
      }
    }

    // write creators back to VO
    this.getEditItemSessionBean().bindCreatorsToVO(item.getMetadata().getCreators());

    // write source creators back to VO
    for (SourceBean sourceBean : this.getEditItemSessionBean().getSources()) {
      sourceBean.bindCreatorsToVO(sourceBean.getSource().getCreators());
    }

    // write sources back to VO
    this.getEditItemSessionBean().bindSourcesToVO(item.getMetadata().getSources());

    return true;
  }

  public String saveAndRelease() {
    if (this.getPubItem() == null) {
      return "";
    }

    if (!restoreVO()) {
      return "";
    }

    // cleanup item according to genre specific MD specification
    GenreSpecificItemManager itemManager =
        new GenreSpecificItemManager(this.getPubItem(),
            GenreSpecificItemManager.SUBMISSION_METHOD_FULL);
    try {
      this.item = (PubItemVOPresentation) itemManager.cleanupItem();
    } catch (Exception e) {
      throw new RuntimeException("Error while cleaning up item genre specificly", e);
    }

    try {
      ItemValidatingService.validateItemObject(new PubItemVO(this.getPubItem()),
          ValidationPoint.STANDARD);
    } catch (ItemInvalidException e) {
      this.showValidationMessages(e.getReport());
      return null;
    } catch (ValidationException e) {
      throw new RuntimeException("Validation error", e);
    }

    // start: check if the item has been changed
    PubItemVO newPubItem = this.getItemControllerSessionBean().getCurrentPubItem();
    PubItemVO oldPubItem = null;
    if (newPubItem.getVersion().getObjectId() != null) {
      try {
        oldPubItem =
            this.getItemControllerSessionBean().retrieveItem(newPubItem.getVersion().getObjectId());
      } catch (Exception e) {
        logger.error("Could not retrieve item." + "\n" + e.toString(), e);
        ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
        return ErrorPage.LOAD_ERRORPAGE;
      }

      if (!this.getItemControllerSessionBean().hasChanged(oldPubItem, newPubItem)) {
        logger.warn("Item has not been changed.");
        // create a validation report
        ValidationReportVO changedReport = new ValidationReportVO();
        ValidationReportItemVO changedReportItem = new ValidationReportItemVO();
        // changedReportItem.setInfoLevel(ValidationReportItemVO.InfoLevel.RESTRICTIVE);
        changedReportItem.setContent("itemHasNotBeenChanged");
        changedReport.addItem(changedReportItem);
        // show report and stay on this page
        this.showValidationMessages(changedReport);
        return null;
      } else {
        // save the item first manually due to a change in the saveAndSubmitCurrentPubItem method
        // (save removed there)
        this.getItemControllerSessionBean().saveCurrentPubItem(SubmitItem.LOAD_SUBMITITEM);
        this.getItemControllerSessionBean().submitCurrentPubItem(
            "Submission during saving released item.", SubmitItem.LOAD_SUBMITITEM);
        try {
          this.getItemControllerSessionBean().setCurrentPubItem(
              this.getItemControllerSessionBean().retrieveItem(
                  newPubItem.getVersion().getObjectId()));
        } catch (Exception e) {
          throw new RuntimeException("Error retrieving submitted item", e);
        }

        this.getPubItemListSessionBean().update();

        return SubmitItem.LOAD_SUBMITITEM;
      }
    }

    return "";
  }

  /**
   * Saves and submits an item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall Changed
   *         by FrM: Inserted validation and call to "enter submission comment" page.
   */
  public String saveAndSubmit() {
    if (this.getPubItem() == null) {
      return "";
    }

    if (!restoreVO()) {
      return "";
    }

    // cleanup item according to genre specific MD specification
    GenreSpecificItemManager itemManager =
        new GenreSpecificItemManager(this.getPubItem(),
            GenreSpecificItemManager.SUBMISSION_METHOD_FULL);
    try {
      this.item = (PubItemVOPresentation) itemManager.cleanupItem();
    } catch (Exception e) {
      throw new RuntimeException("Error while cleaning up item genre specificly", e);
    }

    try {
      ItemValidatingService.validateItemObject(new PubItemVO(this.getPubItem()),
          ValidationPoint.STANDARD);
    } catch (ItemInvalidException e) {
      this.showValidationMessages(e.getReport());
      return null;
    } catch (ValidationException e) {
      throw new RuntimeException("Validation error", e);
    }

    // start: check if the item has been changed
    PubItemVO newPubItem = this.getItemControllerSessionBean().getCurrentPubItem();
    PubItemVO oldPubItem = null;
    if (newPubItem.getVersion().getObjectId() != null) {
      try {
        oldPubItem =
            this.getItemControllerSessionBean().retrieveItem(newPubItem.getVersion().getObjectId());
      } catch (Exception e) {
        logger.error("Could not retrieve item." + "\n" + e.toString(), e);
        ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
        return ErrorPage.LOAD_ERRORPAGE;
      }
      if (!this.getItemControllerSessionBean().hasChanged(oldPubItem, newPubItem)) {
        if (newPubItem.getVersion().getState() == State.RELEASED) {
          logger.warn("Item has not been changed.");
          // create a validation report
          ValidationReportVO changedReport = new ValidationReportVO();
          ValidationReportItemVO changedReportItem = new ValidationReportItemVO();
          // changedReportItem.setInfoLevel(ValidationReportItemVO.InfoLevel.RESTRICTIVE);
          changedReportItem.setContent("itemHasNotBeenChanged");
          changedReport.addItem(changedReportItem);
          // show report and stay on this page
          this.showValidationMessages(changedReport);
          return null;
        } else {
          return SubmitItem.LOAD_SUBMITITEM;
        }
      }
    }

    String retVal = "";
    try {
      retVal = this.getItemControllerSessionBean().saveCurrentPubItem(SubmitItem.LOAD_SUBMITITEM);
    } catch (RuntimeException rE) {
      logger.error("Error saving item", rE);
      String message = getMessage("itemHasBeenChangedInTheMeantime");
      fatal(message);
      retVal = EditItem.LOAD_EDITITEM;
      return retVal;
    }

    if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0) {
      // set the current submission method to empty string (for GUI purpose)
      this.getEditItemSessionBean().setCurrentSubmission("");
      this.getSubmitItemSessionBean().setNavigationStringToGoBack(
          MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS);
      String localMessage = getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SAVED);
      info(localMessage);
      this.getSubmitItemSessionBean().setMessage(localMessage);
    }

    this.getPubItemListSessionBean().update();

    return retVal;
  }

  /**
   * Deletes the current item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String delete() {
    String retVal =
        this.getItemControllerSessionBean().deleteCurrentPubItem(
            MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS);

    if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0) {
      info(getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_DELETED));
    }

    this.getPubItemListSessionBean().update();

    return retVal;
  }

  /**
   * Cancels the editing.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String cancel() {
    // examine if the user came from the view Item Page or if he started a new submission
    String navString = ViewItemFull.LOAD_VIEWITEM;

    // set the current submission method to empty string (for GUI purpose)
    this.getEditItemSessionBean().setCurrentSubmission("");

    cleanEditItem();

    if (navString.equals(ViewItemFull.LOAD_VIEWITEM)) {
      try {
        if ("ViewLocalTagsPage.jsp".equals(this.getBreadcrumbItemHistorySessionBean()
            .getPreviousItem().getPage())) {
          String viewItemPage =
              PropertyReader.getProperty("escidoc.pubman.instance.url")
                  + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                  + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceFirst("\\$1",
                      this.getPubItem().getVersion().getObjectId());
          getExternalContext().redirect(viewItemPage);
        } else if (this.getBreadcrumbItemHistorySessionBean().getPreviousItem().getPage()
            .contains("ViewItemFullPage.jsp")) {
          getExternalContext().redirect(
              getRequest().getContextPath() + "/faces/"
                  + this.getBreadcrumbItemHistorySessionBean().getPreviousItem().getPage());
        } else {
          getExternalContext().redirect("faces/SubmissionPage.jsp");
        }
      } catch (Exception e) {
        logger.error("Could not redirect to the previous page", e);
      }
    } else {
      try {
        getExternalContext().redirect("faces/SubmissionPage.jsp");
      } catch (Exception e) {
        logger
            .error(
                "Cancel error: could not find context to redirect to SubmissionPage.jsp in Full Submssion",
                e);
      }
    }

    return navString;
  }

  /**
   * This method cleans up all the helping constructs like collections etc.
   */
  private void cleanEditItem() {
    this.item = null;
    this.identifierCollection = null;
    this.languages = null;
  }

  /**
   * Saves and accepts an item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String saveAndAccept() {
    if (this.getPubItem() == null) {
      return "";
    }

    if (!restoreVO()) {
      return "";
    }

    // cleanup item according to genre specific MD specification
    GenreSpecificItemManager itemManager =
        new GenreSpecificItemManager(this.getPubItem(),
            GenreSpecificItemManager.SUBMISSION_METHOD_FULL);
    try {
      this.item = (PubItemVOPresentation) itemManager.cleanupItem();
    } catch (Exception e) {
      throw new RuntimeException("Error while cleaning up item genre specificly", e);
    }

    try {
      ItemValidatingService.validateItemObject(new PubItemVO(this.getPubItem()),
          ValidationPoint.STANDARD);
    } catch (ItemInvalidException e) {
      this.showValidationMessages(e.getReport());
      return null;
    } catch (ValidationException e) {
      throw new RuntimeException("Validation error", e);
    }

    // check if the item has been changed
    PubItemVO newPubItem = this.getItemControllerSessionBean().getCurrentPubItem();
    PubItemVO oldPubItem = null;
    try {
      oldPubItem =
          this.getItemControllerSessionBean().retrieveItem(newPubItem.getVersion().getObjectId());
    } catch (Exception e) {
      logger.error("Could not retrieve item." + "\n" + e.toString(), e);
      ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
      return ErrorPage.LOAD_ERRORPAGE;
    }

    if (!this.getItemControllerSessionBean().hasChanged(oldPubItem, newPubItem)) {
      if (newPubItem.getVersion().getState() == State.RELEASED) {
        logger.warn("Item has not been changed.");
        // create a validation report
        ValidationReportVO changedReport = new ValidationReportVO();
        ValidationReportItemVO changedReportItem = new ValidationReportItemVO();
        // changedReportItem.setInfoLevel(ValidationReportItemVO.InfoLevel.RESTRICTIVE);
        changedReportItem.setContent("itemHasNotBeenChanged");
        changedReport.addItem(changedReportItem);
        // show report and stay on this page
        this.showValidationMessages(changedReport);
        return null;
      } else {
        return AcceptItem.LOAD_ACCEPTITEM;
      }
    }

    String retVal = "";
    // If item is released, submit it additionally (because it is pending after the save)
    try {
      if (this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getState()
          .equals(State.RELEASED)) {
        // save the item first manually due to a change in the saveAndSubmitCurrentPubItem method
        // (save removed there)
        this.getItemControllerSessionBean().saveCurrentPubItem(AcceptItem.LOAD_ACCEPTITEM);
        retVal =
            this.getItemControllerSessionBean().submitCurrentPubItem(
                "Submission during saving released item.", AcceptItem.LOAD_ACCEPTITEM);
      } else {
        // only save it
        retVal = this.getItemControllerSessionBean().saveCurrentPubItem(AcceptItem.LOAD_ACCEPTITEM);
      }
    } catch (RuntimeException rE) {
      logger.error("Error saving item", rE);
      String message = getMessage("itemHasBeenChangedInTheMeantime");
      fatal(message);
      retVal = EditItem.LOAD_EDITITEM;
      return retVal;
    }

    if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0) {
      // set the current submission method to empty string (for GUI purpose)
      this.getEditItemSessionBean().setCurrentSubmission("");
      this.getAcceptItemSessionBean().setNavigationStringToGoBack(ViewItemFull.LOAD_VIEWITEM);
      String localMessage = getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SAVED);
      info(localMessage);
      this.getAcceptItemSessionBean().setMessage(localMessage);
    }

    this.getPubItemListSessionBean().update();

    return retVal;
  }

  public String uploadFile(UploadedFile file) {
    if (file != null && file.getSize() > 0) {
      String contentURL = uploadFileToEscidoc(file);
      String fixedFileName = CommonUtils.fixURLEncoding(file.getFileName());
      if (contentURL != null && !contentURL.trim().equals("")) {
        FileVO fileVO = new FileVO();
        MdsFileVO mdsFileVO = new MdsFileVO();
        mdsFileVO.getIdentifiers().add(new IdentifierVO());
        fileVO.getMetadataSets().add(mdsFileVO);
        fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);
        this.getEditItemSessionBean()
            .getFiles()
            .add(
                new PubFileVOPresentation(this.getEditItemSessionBean().getFiles().size(), fileVO,
                    false));
        fileVO.getDefaultMetadata().setSize((int) file.getSize());
        fileVO.setName(fixedFileName);
        fileVO.getDefaultMetadata().setTitle(fixedFileName);

        Tika tika = new Tika();
        try {
          InputStream fis = file.getInputstream();
          fileVO.setMimeType(tika.detect(fis, fixedFileName));
          fis.close();
        } catch (IOException e) {
          logger.info("Error while trying to detect mimetype of file " + fixedFileName, e);
        }

        FormatVO formatVO = new FormatVO();
        formatVO.setType("dcterms:IMT");
        formatVO.setValue(fileVO.getMimeType());
        fileVO.getDefaultMetadata().getFormats().add(formatVO);
        fileVO.setContent(contentURL);
      }
    } else {
      error(getMessage("ComponentEmpty"));
    }

    return "";
  }

  public String uploadFileToEscidoc(UploadedFile file) {
    String contentURL = "";
    if (file != null) {
      try {
        // upload the file
        URL url = null;
        if (getLoginHelper().getAccountUser().isDepositor()) {
          url =
              this.uploadFile(file, file.getContentType(), getLoginHelper().getESciDocUserHandle());
        }
        // workarround for moderators who can modify released items but do not have the right to
        // upload files
        else {
          url = this.uploadFile(file, file.getContentType(), AdminHelper.getAdminUserHandle());
        }
        if (url != null) {
          contentURL = url.toString();
        }
      } catch (Exception e) {
        logger.error("Could not upload file." + "\n" + e.toString());
        ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
        try {
          FacesContext.getCurrentInstance().getExternalContext().redirect("ErrorPage.jsp");
        } catch (Exception ex) {
          logger.error(e.toString());
        }
        return ErrorPage.LOAD_ERRORPAGE;
      }
    }

    return contentURL;
  }

  public void fileUploaded(FileUploadEvent event) {
    uploadFile(event.getFile());
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

  /**
   * This method adds a locator to the list of locators of the item
   * 
   * @return navigation string (null)
   */
  public String addLocator() {
    if (this.getEditItemSessionBean().getLocators() != null) {
      FileVO newLocator = new FileVO();
      newLocator.getMetadataSets().add(new MdsFileVO());
      newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
      this.getEditItemSessionBean()
          .getLocators()
          .add(
              new PubFileVOPresentation(this.getEditItemSessionBean().getLocators().size(),
                  newLocator, true));
    }

    return null;
  }

  /**
   * This method saves the latest locator to the list of files of the item
   * 
   * @return navigation string (null)
   */
  public String saveLocator() {
    int indexUpload = this.getEditItemSessionBean().getLocators().size() - 1;
    if (this.getEditItemSessionBean().getLocators() != null) {
      // Set empty MetadataSet if none exists
      if (this.getEditItemSessionBean().getLocators().get(indexUpload).getFile()
          .getDefaultMetadata() == null) {
        this.getEditItemSessionBean().getLocators().get(indexUpload).getFile().getMetadataSets()
            .add(new MdsFileVO());
      }
      // Set file name if not filled
      if (this.getEditItemSessionBean().getLocators().get(indexUpload).getFile()
          .getDefaultMetadata().getTitle() == null
          || this.getEditItemSessionBean().getLocators().get(indexUpload).getFile()
              .getDefaultMetadata().getTitle().trim().equals("")) {
        this.getEditItemSessionBean()
            .getLocators()
            .get(indexUpload)
            .getFile()
            .getDefaultMetadata()
            .setTitle(
                this.getEditItemSessionBean().getLocators().get(indexUpload).getFile().getContent()
                    .trim());
      }
      List<PubFileVOPresentation> list = this.getEditItemSessionBean().getLocators();
      PubFileVOPresentation pubFile = list.get(indexUpload);
      list.set(indexUpload, pubFile);
      this.getEditItemSessionBean().setLocators(list);
    }

    return null;
  }

  private void showValidationMessages(ValidationReportVO report) {
    showValidationMessages(this, report);
  }

  public void showValidationMessages(FacesBean bean, ValidationReportVO report) {
    for (Iterator<ValidationReportItemVO> iter = report.getItems().iterator(); iter.hasNext();) {
      ValidationReportItemVO element = iter.next();
      error(bean.getMessage(element.getContent()).replaceAll("\\$1", element.getElement()));
    }
  }

  private void enableLinks() {
    boolean isStatePending = true;
    boolean isStateSubmitted = false;
    boolean isStateReleased = false;
    boolean isStateInRevision = false;
    boolean isPublicStateReleased = false;

    if (this.getPubItem() != null && this.getPubItem().getVersion() != null
        && this.getPubItem().getVersion().getState() != null) {
      isStatePending = this.getPubItem().getVersion().getState().equals(State.PENDING);
      isStateSubmitted = this.getPubItem().getVersion().getState().equals(State.SUBMITTED);
      isStateReleased = this.getPubItem().getVersion().getState().equals(State.RELEASED);
      isStateInRevision = this.getPubItem().getVersion().getState().equals(State.IN_REVISION);
      isPublicStateReleased = this.getPubItem().getPublicStatus() == State.RELEASED;
    }

    boolean isOwner = true;
    if (this.getPubItem() != null && this.getPubItem().getOwner() != null) {
      isOwner =
          (getLoginHelper().getAccountUser().getReference() != null ? getLoginHelper()
              .getAccountUser().getReference().getObjectId()
              .equals(this.getPubItem().getOwner().getObjectId()) : false);
    }

    boolean isModerator = false;
    if (getLoginHelper().getAccountUser() != null && this.getPubItem() != null) {
      isModerator = getLoginHelper().getAccountUser().isModerator(this.getPubItem().getContext());
    }

    boolean isWorkflowStandard = false;
    boolean isWorkflowSimple = true;

    try {
      if (this.getItemControllerSessionBean().getCurrentContext() != null
          && this.getItemControllerSessionBean().getCurrentContext().getAdminDescriptor() != null) {
        isWorkflowStandard =
            (this.getItemControllerSessionBean().getCurrentContext().getAdminDescriptor()
                .getWorkflow() == PublicationAdminDescriptorVO.Workflow.STANDARD);
        isWorkflowSimple =
            (this.getItemControllerSessionBean().getCurrentContext().getAdminDescriptor()
                .getWorkflow() == PublicationAdminDescriptorVO.Workflow.SIMPLE);
      }
    } catch (Exception e) {
      throw new RuntimeException("Previously uncaught exception", e);
    }

    boolean itemHasID =
        this.getPubItem() != null && this.getPubItem().getVersion() != null
            && this.getPubItem().getVersion().getObjectId() != null;

    boolean isItem = this.getPubItem() != null;

    if (!isItem) {
      this.lnkAccept.setRendered(false);
      this.lnkRelease.setRendered(false);
      this.lnkReleaseReleasedItem.setRendered(false);
      this.lnkDelete.setRendered(false);
      this.lnkSaveAndSubmit.setRendered(false);
      this.lnkSave.setRendered(false);
    } else {
      this.lnkAccept.setRendered(isModerator && !isOwner && (isStateSubmitted || isStateReleased));
      this.lnkRelease.setRendered(isOwner && isWorkflowSimple
          && (isStatePending || isStateSubmitted)
          || (isModerator && isWorkflowStandard && isStateSubmitted));
      this.lnkReleaseReleasedItem.setRendered(isOwner && isStateReleased && isWorkflowSimple
          || isOwner && isModerator && isWorkflowStandard && isStateReleased);
      this.lnkDelete.setRendered(isOwner && isStatePending && !isPublicStateReleased && itemHasID);
      this.lnkSaveAndSubmit.setRendered(isOwner && isWorkflowStandard
          && (isStatePending || isStateInRevision || isStateReleased));
      this.lnkSave.setRendered((isOwner && (isStatePending || isStateInRevision)) || isModerator
          && isStateSubmitted);
    }
    /*
     * this.lnkAccept.setRendered(this.isInModifyMode() &&
     * loginHelper.getAccountUser().isModerator(this.getPubItem().getContext()));
     * this.lnkDelete.setRendered(!this.isInModifyMode() && itemHasID);
     * this.lnkSaveAndSubmit.setRendered(!this.isInModifyMode());
     * this.lnkSave.setRendered(!this.isInModifyMode());
     */
  }

  public boolean getLocalTagEditingAllowed() {
    ViewItemFull viewItemFull = (ViewItemFull) getRequestBean(ViewItemFull.class);

    return !viewItemFull.getIsStateWithdrawn()
        && viewItemFull.getIsLatestVersion()
        && ((viewItemFull.getIsModerator() && !viewItemFull.getIsModifyDisabled() //
        && (viewItemFull.getIsStateReleased() || viewItemFull.getIsStateSubmitted())) || (viewItemFull
            .getIsOwner() //
        && (viewItemFull.getIsStatePending() || viewItemFull.getIsStateReleased() || viewItemFull
            .getIsStateInRevision())));
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
      }
    }

    if (allowedGenres == null) {
      allowedGenres = new ArrayList<MdsPublicationVO.Genre>();
    }

    return this.getI18nHelper().getSelectItemsForEnum(false,
        allowedGenres.toArray(new MdsPublicationVO.Genre[] {}));
  }

  /**
   * Returns all options for degreeType.
   * 
   * @return all options for degreeType
   */
  public SelectItem[] getDegreeTypes() {
    return this.getI18nHelper().getSelectItemsDegreeType(true);
  }

  /**
   * Returns all options for reviewMethod.
   * 
   * @return all options for reviewMethod
   */
  public SelectItem[] getReviewMethods() {
    return this.getI18nHelper().getSelectItemsReviewMethod(true);
  }

  /**
   * Returns all options for content categories.
   * 
   * @return all options for content c ategories.
   */
  public SelectItem[] getContentCategories() {
    return this.getI18nHelper().getSelectItemsContentCategory(true);
  }

  /**
   * Returns all options for visibility.
   * 
   * @return all options for visibility
   */
  public SelectItem[] getVisibilities() {
    return this.getI18nHelper().getSelectItemsVisibility(false);
  }

  public SelectItem[] getInvitationStatuses() {
    return this.getI18nHelper().getSelectItemsInvitationStatus(true);
  }

  /**
   * Invitationstatus of event has to be converted as it's an enum that is supposed to be shown in a
   * checkbox.
   * 
   * @return true if invitationstatus in VO is set, else false
   */
  public boolean getInvited() {
    // Changed by FrM: Check for event
    if (this.getPubItem().getMetadata().getEvent() != null
        && this.getPubItem().getMetadata().getEvent().getInvitationStatus() != null
        && this.getPubItem().getMetadata().getEvent().getInvitationStatus()
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
      this.getPubItem().getMetadata().getEvent()
          .setInvitationStatus(EventVO.InvitationStatus.INVITED);
    } else {
      this.getPubItem().getMetadata().getEvent().setInvitationStatus(null);
    }
  }

  public HtmlCommandLink getLnkAccept() {
    return this.lnkAccept;
  }

  public void setLnkAccept(HtmlCommandLink lnkAccept) {
    this.lnkAccept = lnkAccept;
  }

  public HtmlCommandLink getLnkDelete() {
    return this.lnkDelete;
  }

  public void setLnkDelete(HtmlCommandLink lnkDelete) {
    this.lnkDelete = lnkDelete;
  }

  public HtmlCommandLink getLnkSave() {
    return this.lnkSave;
  }

  public void setLnkSave(HtmlCommandLink lnkSave) {
    this.lnkSave = lnkSave;
  }

  public HtmlCommandLink getLnkSaveAndSubmit() {
    return this.lnkSaveAndSubmit;
  }

  public void setLnkSaveAndSubmit(HtmlCommandLink lnkSaveAndSubmit) {
    this.lnkSaveAndSubmit = lnkSaveAndSubmit;
  }

  public String getEventTitle() {
    if (this.getPubItem().getMetadata().getEvent() != null
        && this.getPubItem().getMetadata().getEvent().getTitle() != null) {
      return this.getPubItem().getMetadata().getEvent().getTitle();
    }

    return "";
  }

  public void setEventTitle(String title) {
    if (this.getPubItem().getMetadata().getEvent() != null
        && this.getPubItem().getMetadata().getEvent().getTitle() != null) {
      this.getPubItem().getMetadata().getEvent().setTitle(title);
    }
  }

  public IdentifierCollection getIdentifierCollection() {
    return this.identifierCollection;
  }

  public void setIdentifierCollection(IdentifierCollection identifierCollection) {
    this.identifierCollection = identifierCollection;
  }

  public List<PubFileVOPresentation> getFiles() {
    return this.getEditItemSessionBean().getFiles();
  }

  public void setFiles(List<PubFileVOPresentation> files) {
    this.getEditItemSessionBean().setFiles(files);
  }

  public List<PubFileVOPresentation> getLocators() {
    return this.getEditItemSessionBean().getLocators();
  }

  public void setLocators(List<PubFileVOPresentation> locators) {
    this.getEditItemSessionBean().setLocators(locators);
  }

  public int getNumberOfFiles() {
    if (this.getEditItemSessionBean().getFiles() != null) {
      return this.getEditItemSessionBean().getFiles().size();
    }

    return 0;
  }

  public int getNumberOfLocators() {
    if (this.getEditItemSessionBean().getLocators() != null) {
      return this.getEditItemSessionBean().getLocators().size();
    }

    return 0;
  }

  public PubItemVO getItem() {
    return this.item;
  }

  public void setItem(PubItemVOPresentation item) {
    this.item = item;
  }

  public String getOwner() throws Exception {
    UserAccountHandler userAccountHandler = null;

    HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
    if (this.item.getOwner() != null && this.item.getOwner().getObjectId() != null) {
      filterParams.put("operation", new String[] {"searchRetrieve"});
      filterParams.put("query", new String[] {"\"/id\"=" + this.item.getOwner().getObjectId()});
    } else {
      return null;
    }

    userAccountHandler =
        ServiceLocator.getUserAccountHandler(getLoginHelper().getESciDocUserHandle());
    String searchResponse = userAccountHandler.retrieveUserAccounts(filterParams);

    SearchRetrieveResponseVO searchedObject =
        XmlTransformingService.transformToSearchRetrieveResponseAccountUser(searchResponse);
    if (searchedObject == null || searchedObject.getRecords() == null
        || searchedObject.getRecords().get(0) == null
        || searchedObject.getRecords().get(0).getData() == null) {
      return null;
    }

    AccountUserVO owner = (AccountUserVO) searchedObject.getRecords().get(0).getData();
    if (owner.getName() != null && owner.getName().trim() != "") {
      return owner.getName();
    } else if (owner.getUserid() != null && owner.getUserid() != "") {
      return owner.getUserid();
    } else {
      return null;
    }
  }

  public String getCreationDate() {
    if (this.item.getCreationDate() != null) {
      return this.item.getCreationDate().toString();
    }

    return null;
  }

  public String getLastModifier() throws Exception {
    UserAccountHandler userAccountHandler = null;

    if (this.item.getVersion().getModifiedByRO() != null
        && this.item.getVersion().getModifiedByRO().getObjectId() != null) {
      HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
      filterParams.put("operation", new String[] {"searchRetrieve"});
      filterParams.put("query", new String[] {"\"/id\"="
          + this.item.getVersion().getModifiedByRO().getObjectId()});

      String searchResponse = null;
      userAccountHandler =
          ServiceLocator.getUserAccountHandler(getLoginHelper().getESciDocUserHandle());
      searchResponse = userAccountHandler.retrieveUserAccounts(filterParams);
      SearchRetrieveResponseVO searchedObject =
          XmlTransformingService.transformToSearchRetrieveResponseAccountUser(searchResponse);

      if (searchedObject == null || searchedObject.getRecords() == null
          || searchedObject.getRecords().get(0) == null
          || searchedObject.getRecords().get(0).getData() == null) {
        return null;
      }

      AccountUserVO modifier = (AccountUserVO) searchedObject.getRecords().get(0).getData();
      if (modifier.getName() != null && modifier.getName().trim() != "") {
        return modifier.getName();
      } else if (modifier.getUserid() != null && modifier.getUserid() != "") {
        return modifier.getUserid();
      }
    }

    return null;
  }

  public String getLastModificationDate() {
    if (this.item.getModificationDate() != null) {
      return this.item.getModificationDate().toString();
    }

    return null;
  }

  public boolean isFromEasySubmission() {
    return this.fromEasySubmission;
  }

  public void setFromEasySubmission(boolean fromEasySubmission) {
    this.fromEasySubmission = fromEasySubmission;
  }

  public HtmlCommandLink getLnkRelease() {
    return this.lnkRelease;
  }

  public void setLnkRelease(HtmlCommandLink lnkRelease) {
    this.lnkRelease = lnkRelease;
  }

  public HtmlCommandLink getLnkReleaseReleasedItem() {
    return lnkReleaseReleasedItem;
  }

  public void setLnkReleaseReleasedItem(HtmlCommandLink lnkReleaseReleasedItem) {
    this.lnkReleaseReleasedItem = lnkReleaseReleasedItem;
  }

  public String addCreatorString() {
    try {
      this.getEditItemSessionBean().parseCreatorString(
          this.getEditItemSessionBean().getCreatorParseString(), null,
          this.getEditItemSessionBean().getOverwriteCreators());
    } catch (Exception e) {
      logger.error("Could not parse creator string", e);
      error(getMessage("ErrorParsingCreatorString"));
    }

    return null;
  }

  /**
   * Checks if there are any subject classifications defined for this item.
   * 
   * @return true if ther is at least one subject classification.
   * @throws Exception Any exception.
   */
  public boolean getHasSubjectClassification() throws Exception {
    return getSubjectTypes() != null;
  }

  /**
   * Get all allowed subject classifications from the admin descriptor of the context.
   * 
   * @return An array of SelectItem containing the subject classifications.
   * @throws Exception Any exception.
   */
  public SelectItem[] getSubjectTypes() throws Exception {
    ArrayList<SelectItem> result = new ArrayList<SelectItem>();

    ContextRO contextRO = getPubItem().getContext();
    for (PubContextVOPresentation context : this.getContextListSessionBean()
        .getDepositorContextList()) {
      if (context.getReference().equals(contextRO)) {
        PublicationAdminDescriptorVO adminDescriptorVO = context.getAdminDescriptor();
        List<SubjectClassification> list = adminDescriptorVO.getAllowedSubjectClassifications();
        if (list != null) {
          for (SubjectClassification classification : list) {
            SelectItem selectItem =
                new SelectItem(classification.name(), classification.name().replace("_", "-"));
            result.add(selectItem);
          }
          return result.toArray(new SelectItem[] {});
        }
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
    String newGenre = getItem().getMetadata().getGenre().name();
    Genre[] possibleGenres = MdsPublicationVO.Genre.values();

    for (int i = 0; i < possibleGenres.length; i++) {
      if (possibleGenres[i].toString().equals(newGenre)) {
        this.item.getMetadata().setGenre(possibleGenres[i]);
        this.getItemControllerSessionBean().getCurrentPubItem().getMetadata()
            .setGenre(possibleGenres[i]);
      }
    }

    if (newGenre != null && newGenre.trim().equals("")) {
      newGenre = "ARTICLE";
    }

    this.getEditItemSessionBean().setGenreBundle("Genre_" + newGenre);
    this.init();

    return null;
  }

  /**
   * Adds a new local tag to the PubItemVO and a new wrapped local tag to PubItemVOPresentation.
   * 
   * @return Returns always null.
   */
  public String addLocalTag() {
    WrappedLocalTag wrappedLocalTag = this.getPubItem().new WrappedLocalTag();
    wrappedLocalTag.setParent(this.getPubItem());
    wrappedLocalTag.setValue("");
    this.getPubItem().getWrappedLocalTags().add(wrappedLocalTag);
    this.getPubItem().writeBackLocalTags(null);

    return null;
  }

  public String loadEditLocalTags() {
    this.getEditItemSessionBean().clean();

    return "loadEditLocalTags";
  }

  public UIRepeat getFileIterator() {
    return this.fileIterator;
  }

  public void setFileIterator(UIRepeat fileIterator) {
    this.fileIterator = fileIterator;
  }

  public String getSuggestConeUrl() throws Exception {
    if (this.suggestConeUrl == null) {
      this.suggestConeUrl = PropertyReader.getProperty("escidoc.cone.service.url");
    }

    return this.suggestConeUrl;
  }

  public void setSuggestConeUrl(String suggestConeUrl) {
    this.suggestConeUrl = suggestConeUrl;
  }

  public HtmlSelectOneMenu getGenreSelect() {
    return this.genreSelect;
  }

  public void setGenreSelect(HtmlSelectOneMenu genreSelect) {
    this.genreSelect = genreSelect;
  }

  public String getGenreBundle() {
    return this.getEditItemSessionBean().getGenreBundle();
  }

  public void setGenreBundle(String genreBundle) {
    this.getEditItemSessionBean().setGenreBundle(genreBundle);
  }

  public String getLocatorUpload() {
    return this.locatorUpload;
  }

  public void setLocatorUpload(String locatorUpload) {
    this.locatorUpload = locatorUpload;
  }

  public void setHiddenAlternativeTitlesField(String hiddenAlternativeTitlesField) {
    this.hiddenAlternativeTitlesField = hiddenAlternativeTitlesField;
  }

  public String getHiddenAlternativeTitlesField() {
    return this.hiddenAlternativeTitlesField;
  }

  public void addNewIdentifier(List<IdentifierVO> idList, int pos) {
    idList.add(pos, new IdentifierVO());
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
  }

  private EditItemSessionBean getEditItemSessionBean() {
    return (EditItemSessionBean) getSessionBean(EditItemSessionBean.class);
  }

  private YearbookItemSessionBean getYearbookItemSessionBean() {
    return (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);
  }

  private SubmitItemSessionBean getSubmitItemSessionBean() {
    return (SubmitItemSessionBean) getSessionBean(SubmitItemSessionBean.class);
  }

  private AcceptItemSessionBean getAcceptItemSessionBean() {
    return (AcceptItemSessionBean) getSessionBean(AcceptItemSessionBean.class);
  }

  private PubItemListSessionBean getPubItemListSessionBean() {
    return (PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class);
  }

  private BreadcrumbItemHistorySessionBean getBreadcrumbItemHistorySessionBean() {
    return (BreadcrumbItemHistorySessionBean) getSessionBean(BreadcrumbItemHistorySessionBean.class);
  }

  private AffiliationSessionBean getAffiliationSessionBean() {
    return (AffiliationSessionBean) getSessionBean(AffiliationSessionBean.class);
  }

  private ContextListSessionBean getContextListSessionBean() {
    return (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
  }
}
