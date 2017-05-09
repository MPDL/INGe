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
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.model.SelectItem;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.sun.faces.facelets.component.UIRepeat;

import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.valueobjects.AdminDescriptorVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
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
import de.mpg.mpdl.inge.pubman.web.acceptItem.AcceptItem;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.editItem.IdentifierCollection.IdentifierManager;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.submitItem.SubmitItem;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.GenreSpecificItemManager;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.ListItem;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation.WrappedLocalTag;
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
@ManagedBean(name = "EditItem")
@SuppressWarnings("serial")
public class EditItem extends FacesBean {
  private static final Logger logger = Logger.getLogger(EditItem.class);

  public static final String AUTOPASTE_INNER_DELIMITER = " @@~~@@ ";
  public static final String LOAD_EDITITEM = "loadEditItem";
  public static final String HIDDEN_DELIMITER = " \\|\\|##\\|\\| ";

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

  public void init() {
    this.enableLinks();

    try {
      this.initializeItem();
    } catch (final Exception e) {
      throw new RuntimeException("Error initializing item", e);
    }

    // if item is currently part of invalid yearbook items, show Validation Messages
    if (this.getItem() == null) {
      return;
    }

    if (this.getItem().getVersion() != null && this.getItem().getVersion().getObjectId() != null
        && this.getLoginHelper().getIsYearbookEditor()) {
      if (this.getYearbookItemSessionBean().getYearbookItem() != null
          && this.getYearbookItemSessionBean().getInvalidItemMap()
              .get(this.getItem().getVersion().getObjectId()) != null) {
        try {
          this.getYearbookItemSessionBean().validateItem(this.getItem());
          final YearbookInvalidItemRO invItem =
              this.getYearbookItemSessionBean().getInvalidItemMap()
                  .get(this.getItem().getVersion().getObjectId());

          if (invItem != null) {
            (this.getPubItem()).setValidationReport(invItem.getValidationReport());
          }
        } catch (final Exception e) {
          EditItem.logger.error("Error in Yaerbook validation", e);
        }
      }
    }

    // FIXME provide access to parts of my VO to specialized POJO's
    this.identifierCollection =
        new IdentifierCollection(this.getPubItem().getMetadata().getIdentifiers());

    this.contextName = this.getContextName();
  }

  public String acceptLocalTags() {
    this.getPubItem().writeBackLocalTags(null);
    if (this.getPubItem().getVersion().getState().equals(State.RELEASED)) {
      this.bindFilesAndLocators = false;
      return this.saveAndAccept();
    }

    this.bindFilesAndLocators = false;
    this.save();

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
        final ContextVO context =
            this.getItemControllerSessionBean().retrieveContext(
                this.getPubItem().getContext().getObjectId());
        return context.getName();
      } catch (final Exception e) {
        EditItem.logger.error("Could not retrieve the requested context." + "\n" + e.toString());
        ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
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
    final PubItemVO pubItem = this.getPubItem();
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
        this.bindFiles();
        this.getEditItemSessionBean().setFilesInitialized(true);
      }

      if (this.getEditItemSessionBean().getSources().size() == 0) {
        this.getEditItemSessionBean().bindSourcesToBean(pubItem.getMetadata().getSources());
      }

      if (pubItem.getMetadata() != null && pubItem.getMetadata().getCreators() != null) {
        for (final CreatorVO creatorVO : pubItem.getMetadata().getCreators()) {
          if (creatorVO.getType() == CreatorType.PERSON && creatorVO.getPerson() == null) {
            creatorVO.setPerson(new PersonVO());
          } else if (creatorVO.getType() == CreatorType.ORGANIZATION
              && creatorVO.getOrganization() == null) {
            creatorVO.setOrganization(new OrganizationVO());
          }
          if (creatorVO.getType() == CreatorType.PERSON
              && creatorVO.getPerson().getOrganizations() != null) {
            for (final OrganizationVO organizationVO : creatorVO.getPerson().getOrganizations()) {
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
      for (final SourceBean sourceBean : this.getEditItemSessionBean().getSources()) {
        final SourceVO source = sourceBean.getSource();
        if (source.getCreators() != null) {
          for (final CreatorVO creatorVO : source.getCreators()) {
            if (creatorVO.getType() == CreatorType.PERSON && creatorVO.getPerson() == null) {
              creatorVO.setPerson(new PersonVO());
            } else if (creatorVO.getType() == CreatorType.ORGANIZATION
                && creatorVO.getOrganization() == null) {
              creatorVO.setOrganization(new OrganizationVO());
            }
            if (creatorVO.getType() == CreatorType.PERSON
                && creatorVO.getPerson().getOrganizations() != null) {
              for (final OrganizationVO organizationVO : creatorVO.getPerson().getOrganizations()) {
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
      EditItem.logger.warn("Current PubItem is NULL!");
    }
  }

  private void bindFiles() {
    final List<PubFileVOPresentation> files = new ArrayList<PubFileVOPresentation>();
    final List<PubFileVOPresentation> locators = new ArrayList<PubFileVOPresentation>();
    int fileCount = 0;
    int locatorCount = 0;

    // add files
    for (final FileVO file : this.item.getFiles()) {
      if (file.getStorage().equals(FileVO.Storage.INTERNAL_MANAGED)) {
        // Add identifierVO if not available yet
        if (file.getDefaultMetadata() != null
            && (file.getDefaultMetadata().getIdentifiers() == null || file.getDefaultMetadata()
                .getIdentifiers().isEmpty())) {
          file.getDefaultMetadata().getIdentifiers().add(new IdentifierVO());
        }
        final PubFileVOPresentation filepres = new PubFileVOPresentation(fileCount, file, false);
        files.add(filepres);
        fileCount++;
      }
    }

    this.getEditItemSessionBean().setFiles(files);

    // add locators
    for (final FileVO file : this.item.getFiles()) {
      if (file.getStorage().equals(FileVO.Storage.EXTERNAL_URL)) {
        final PubFileVOPresentation locatorpres =
            new PubFileVOPresentation(locatorCount, file, true);
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
      final FileVO newLocator = new FileVO();
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
      final PubItemVOPresentation pubItem = this.getPubItem();

      pubItem.getFiles().clear();

      // add the files
      final List<PubFileVOPresentation> files = this.getFiles();

      if (files != null && files.size() > 0) {
        for (int i = 0; i < files.size(); i++) {
          pubItem.getFiles().add(files.get(i).getFile());
        }
      }

      // add the locators
      final List<PubFileVOPresentation> locators = this.getLocators();

      final int lsize = locators.size();

      if (locators != null && lsize > 0) {
        for (final PubFileVOPresentation loc : locators) {
          // add name from content if not available
          final MdsFileVO defaultMetadata = loc.getFile().getDefaultMetadata();
          final String title = defaultMetadata.getTitle();
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
    final String fwUrl = PropertyReader.getFrameworkUrl();
    final PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
    // if(uploadedFile.isTempFile())
    // {
    final InputStream fis = uploadedFile.getInputstream();
    method.setRequestEntity(new InputStreamRequestEntity(fis));
    /*
     * } else { method.setRequestEntity(new InputStreamRequestEntity(new
     * ByteArrayInputStream(uploadedFile.getData()))); }
     */

    method.setRequestHeader("Content-Type", mimetype);
    method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
    // Execute the method with HttpClient.
    final HttpClient client = new HttpClient();
    ProxyHelper.setProxy(client, fwUrl);
    client.executeMethod(method);
    final String response = method.getResponseBodyAsString();
    fis.close();

    return XmlTransformingService.transformUploadResponseToFileURL(response);
  }

  public List<ListItem> getLanguages() throws Exception {
    if (this.languages == null) {
      this.languages = new ArrayList<ListItem>();
      if (this.getPubItem().getMetadata().getLanguages().size() == 0) {
        this.getPubItem().getMetadata().getLanguages().add("");
      }
      int counter = 0;
      for (final Iterator<String> iterator =
          this.getPubItem().getMetadata().getLanguages().iterator(); iterator.hasNext();) {
        final String value = iterator.next();
        final ListItem item = new ListItem();
        item.setValue(value);
        item.setIndex(counter++);
        item.setStringList(this.getPubItem().getMetadata().getLanguages());
        item.setItemList(this.languages);
        this.languages.add(item);
      }
    }

    return this.languages;
  }

  public SelectItem[] getLanguageOptions() {
    return CommonUtils.getLanguageOptions();
  }

  public String validate() {
    if (check() == false) {
      return "";
    }

    cleanUp();

    try {
      ItemValidatingService.validate(new PubItemVO(this.getPubItem()), ValidationPoint.STANDARD);
      final String message = this.getMessage("itemIsValid");
      this.info(message);
    } catch (final ItemInvalidException e) {
      this.showValidationMessages(e.getReport());
      return null;
    } catch (final Exception e) {
      EditItem.logger.error("Could not validate item." + "\n" + e.toString(), e);
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
      return ErrorPage.LOAD_ERRORPAGE;
    }

    return "";
  }

  /**
   * Saves the item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String save() {
    if (check() == false) {
      return "";
    }

    cleanUp();

    String navigateTo = ViewItemFull.LOAD_VIEWITEM;
    String retVal = saveItem(navigateTo);

    if (!navigateTo.equals(retVal)) {
      return retVal;
    }

    // set the current submission method to empty string (for GUI purpose)
    this.getEditItemSessionBean().setCurrentSubmission("");
    this.getPubItemListSessionBean().update();

    try {
      if (this.isFromEasySubmission()) {
        FacesTools.getExternalContext().redirect(
            FacesTools.getRequest().getContextPath()
                + "/faces/ViewItemFullPage.jsp?itemId="
                + this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                    .getObjectId() + "&fromEasySub=true");
      } else {
        FacesTools.getExternalContext().redirect(
            FacesTools.getRequest().getContextPath()
                + "/faces/ViewItemFullPage.jsp?itemId="
                + this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                    .getObjectId());
      }
    } catch (final IOException e) {
      EditItem.logger.error("Could not redirect to View Item Page", e);
    }

    return retVal;
  }

  private boolean restoreVO() {
    // bind the temporary uploaded files to the files in the current item
    this.bindUploadedFilesAndLocators();

    // bind Organizations To Creators
    if (!this.getEditItemSessionBean().bindOrganizationsToCreators()) {
      return false;
    }

    for (final SourceBean sourceBean : this.getEditItemSessionBean().getSources()) {
      if (!sourceBean.bindOrganizationsToCreators()) {
        return false;
      }
    }

    // write creators back to VO
    this.getEditItemSessionBean().bindCreatorsToVO(this.item.getMetadata().getCreators());

    // write source creators back to VO
    for (final SourceBean sourceBean : this.getEditItemSessionBean().getSources()) {
      sourceBean.bindCreatorsToVO(sourceBean.getSource().getCreators());
    }

    // write sources back to VO
    this.getEditItemSessionBean().bindSourcesToVO(this.item.getMetadata().getSources());

    return true;
  }

  public String saveAndRelease() {
    if (check() == false) {
      return "";
    }

    cleanUp();

    final String navigateTo = SubmitItem.LOAD_SUBMITITEM;
    String retVal = checkItemChanged(navigateTo);

    if (!navigateTo.equals(retVal)) {
      return retVal;
    }

    retVal = saveItem(navigateTo);

    if (!navigateTo.equals(retVal)) {
      return retVal;
    }

    this.getPubItemListSessionBean().update();

    return retVal;
  }

  private boolean check() {
    return this.getPubItem() != null && this.restoreVO();
  }

  private void cleanUp() {
    // cleanup item according to genre specific MD specification
    final GenreSpecificItemManager itemManager =
        new GenreSpecificItemManager(this.getPubItem(),
            GenreSpecificItemManager.SUBMISSION_METHOD_FULL);
    try {
      this.item = (PubItemVOPresentation) itemManager.cleanupItem();
    } catch (final Exception e) {
      throw new RuntimeException("Error while cleaning up item genre specificly", e);
    }
  }

  private String checkItemChanged(String navigateTo) {
    final PubItemVO newPubItem = this.getItemControllerSessionBean().getCurrentPubItem();
    PubItemVO oldPubItem = null;
    if (newPubItem.getVersion().getObjectId() != null) {
      try {
        oldPubItem =
            this.getItemControllerSessionBean().retrieveItem(newPubItem.getVersion().getObjectId());
      } catch (final Exception e) {
        EditItem.logger.error("Could not retrieve item." + "\n" + e.toString(), e);
        ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
        return ErrorPage.LOAD_ERRORPAGE;
      }

      if (!this.getItemControllerSessionBean().hasChanged(oldPubItem, newPubItem)) {
        // if (newPubItem.getVersion().getState() != State.RELEASED) {
        // return navigateTo;
        // }
        //
        EditItem.logger.warn("Item has not been changed.");
        // create a validation report
        final ValidationReportVO changedReport = new ValidationReportVO();
        final ValidationReportItemVO changedReportItem = new ValidationReportItemVO();
        // changedReportItem.setInfoLevel(ValidationReportItemVO.InfoLevel.RESTRICTIVE);
        changedReportItem.setContent("itemHasNotBeenChanged");
        changedReport.addItem(changedReportItem);
        // show report and stay on this page
        this.showValidationMessages(changedReport);

        return "";
      }
    }

    return navigateTo;
  }

  private String saveItem(String navigateTo) {
    try {
      return this.getItemControllerSessionBean().saveCurrentPubItem(navigateTo);
    } catch (ItemInvalidException e) {
      this.showValidationMessages(e.getReport());
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
    if (check() == false) {
      return "";
    }

    cleanUp();

    final String navigateTo = SubmitItem.LOAD_SUBMITITEM;
    String retVal = checkItemChanged(navigateTo);

    if (!navigateTo.equals(retVal)) {
      return retVal;
    }

    retVal = saveItem(navigateTo);

    if (!navigateTo.equals(retVal)) {
      return retVal;
    }

    // set the current submission method to empty string (for GUI purpose)
    this.getEditItemSessionBean().setCurrentSubmission("");
    this.getPubItemListSessionBean().update();
    // this.getSubmitItemSessionBean().setNavigationStringToGoBack(
    // MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS);

    return retVal;
  }

  /**
   * Deletes the current item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String delete() {
    final String navigateTo = MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS;

    final String retVal = this.getItemControllerSessionBean().deleteCurrentPubItem(navigateTo);

    if (navigateTo.equals(retVal)) {
      this.info(this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_DELETED));
      this.getPubItemListSessionBean().update();
    }

    return retVal;
  }

  /**
   * Cancels the editing.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String cancel() {
    // examine if the user came from the view Item Page or if he started a new submission
    final String navString = ViewItemFull.LOAD_VIEWITEM;

    // set the current submission method to empty string (for GUI purpose)
    this.getEditItemSessionBean().setCurrentSubmission("");

    this.cleanEditItem();

    if (navString.equals(ViewItemFull.LOAD_VIEWITEM)) {
      try {
        if ("ViewLocalTagsPage.jsp".equals(this.getBreadcrumbItemHistorySessionBean()
            .getPreviousItem().getPage())) {
          final String viewItemPage =
              PropertyReader.getProperty("escidoc.pubman.instance.url")
                  + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                  + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceFirst("\\$1",
                      this.getPubItem().getVersion().getObjectId());
          FacesTools.getExternalContext().redirect(viewItemPage);
        } else if (this.getBreadcrumbItemHistorySessionBean().getPreviousItem().getPage()
            .contains("ViewItemFullPage.jsp")) {
          FacesTools.getExternalContext().redirect(
              FacesTools.getRequest().getContextPath() + "/faces/"
                  + this.getBreadcrumbItemHistorySessionBean().getPreviousItem().getPage());
        } else {
          FacesTools.getExternalContext().redirect("faces/SubmissionPage.jsp");
        }
      } catch (final Exception e) {
        EditItem.logger.error("Could not redirect to the previous page", e);
      }
    } else {
      try {
        FacesTools.getExternalContext().redirect("faces/SubmissionPage.jsp");
      } catch (final Exception e) {
        EditItem.logger
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
    if (check() == false) {
      return "";
    }

    cleanUp();

    final String navigateTo = AcceptItem.LOAD_ACCEPTITEM;
    String retVal = checkItemChanged(navigateTo);

    if (!navigateTo.equals(retVal)) {
      return retVal;
    }

    retVal = saveItem(navigateTo);

    if (!navigateTo.equals(retVal)) {
      return retVal;
    }

    // try {
    // if (this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getState()
    // .equals(State.RELEASED)) {
    // this.getItemControllerSessionBean().saveCurrentPubItem(AcceptItem.LOAD_ACCEPTITEM);
    // retVal = this.getItemControllerSessionBean().submitCurrentPubItem(
    // "Submission during saving released item.", AcceptItem.LOAD_ACCEPTITEM);
    //
    // } else {
    // // only save it
    // retVal = this.getItemControllerSessionBean().saveCurrentPubItem(AcceptItem.LOAD_ACCEPTITEM);
    // }
    // } catch (ItemInvalidException e) {
    // this.showValidationMessages(e.getReport());
    // return null;
    // }

    // set the current submission method to empty string (for GUI purpose)
    this.getEditItemSessionBean().setCurrentSubmission("");
    this.getPubItemListSessionBean().update();

    final String localMessage = this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SAVED);
    this.info(localMessage);

    return retVal;
  }

  public String uploadFile(UploadedFile file) {
    if (file != null && file.getSize() > 0) {
      final String contentURL = this.uploadFileToEscidoc(file);
      final String fixedFileName = CommonUtils.fixURLEncoding(file.getFileName());
      if (contentURL != null && !contentURL.trim().equals("")) {
        final FileVO fileVO = new FileVO();
        final MdsFileVO mdsFileVO = new MdsFileVO();
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

        final Tika tika = new Tika();
        try {
          final InputStream fis = file.getInputstream();
          fileVO.setMimeType(tika.detect(fis, fixedFileName));
          fis.close();
        } catch (final IOException e) {
          EditItem.logger.info("Error while trying to detect mimetype of file " + fixedFileName, e);
        }

        final FormatVO formatVO = new FormatVO();
        formatVO.setType("dcterms:IMT");
        formatVO.setValue(fileVO.getMimeType());
        fileVO.getDefaultMetadata().getFormats().add(formatVO);
        fileVO.setContent(contentURL);
      }
    } else {
      FacesBean.error(this.getMessage("ComponentEmpty"));
    }

    return "";
  }

  public String uploadFileToEscidoc(UploadedFile file) {
    String contentURL = "";
    if (file != null) {
      try {
        // upload the file
        URL url = null;
        if (this.getLoginHelper().getAccountUser().isDepositor()) {
          url =
              this.uploadFile(file, file.getContentType(), this.getLoginHelper()
                  .getESciDocUserHandle());
        }
        // workarround for moderators who can modify released items but do not have the right to
        // upload files
        else {
          url = this.uploadFile(file, file.getContentType(), AdminHelper.getAdminUserHandle());
        }
        if (url != null) {
          contentURL = url.toString();
        }
      } catch (final Exception e) {
        EditItem.logger.error("Could not upload file." + "\n" + e.toString());
        ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
        try {
          FacesTools.getExternalContext().redirect("ErrorPage.jsp");
        } catch (final Exception ex) {
          EditItem.logger.error(e.toString());
        }
        return ErrorPage.LOAD_ERRORPAGE;
      }
    }

    return contentURL;
  }

  public void fileUploaded(FileUploadEvent event) {
    this.uploadFile(event.getFile());
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
      FacesBean.error(this.getMessage("errorLocatorMain").replace("$1", locatorBean.getError()));
    } else {
      this.setLocatorUpload("");
    }
  }

  /**
   * This method adds a locator to the list of locators of the item
   * 
   * @return navigation string (null)
   */
  public void addLocator() {
    if (this.getEditItemSessionBean().getLocators() != null) {
      final FileVO newLocator = new FileVO();
      newLocator.getMetadataSets().add(new MdsFileVO());
      newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
      this.getEditItemSessionBean()
          .getLocators()
          .add(
              new PubFileVOPresentation(this.getEditItemSessionBean().getLocators().size(),
                  newLocator, true));
    }
  }

  /**
   * This method saves the latest locator to the list of files of the item
   * 
   * @return navigation string (null)
   */
  public void saveLocator() {
    final int indexUpload = this.getEditItemSessionBean().getLocators().size() - 1;
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
      final List<PubFileVOPresentation> list = this.getEditItemSessionBean().getLocators();
      final PubFileVOPresentation pubFile = list.get(indexUpload);
      list.set(indexUpload, pubFile);
      this.getEditItemSessionBean().setLocators(list);
    }
  }

  private void showValidationMessages(ValidationReportVO report) {
    this.showValidationMessages(this, report);
  }

  public void showValidationMessages(FacesBean bean, ValidationReportVO report) {
    for (final Iterator<ValidationReportItemVO> iter = report.getItems().iterator(); iter.hasNext();) {
      final ValidationReportItemVO element = iter.next();
      FacesBean.error(bean.getMessage(element.getContent())
          .replaceAll("\\$1", element.getElement()));
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
          (this.getLoginHelper().getAccountUser().getReference() != null ? this.getLoginHelper()
              .getAccountUser().getReference().getObjectId()
              .equals(this.getPubItem().getOwner().getObjectId()) : false);
    }

    boolean isModerator = false;
    if (this.getLoginHelper().getAccountUser() != null && this.getPubItem() != null) {
      isModerator =
          this.getLoginHelper().getAccountUser().isModerator(this.getPubItem().getContext());
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
    } catch (final Exception e) {
      throw new RuntimeException("Previously uncaught exception", e);
    }

    final boolean itemHasID =
        this.getPubItem() != null && this.getPubItem().getVersion() != null
            && this.getPubItem().getVersion().getObjectId() != null;

    final boolean isItem = this.getPubItem() != null;

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
  }

  public boolean getLocalTagEditingAllowed() {
    final ViewItemFull viewItemFull = (ViewItemFull) FacesTools.findBean("ViewItemFull");

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
    final List<AdminDescriptorVO> adminDescriptors =
        this.getItemControllerSessionBean().getCurrentContext().getAdminDescriptors();

    for (final AdminDescriptorVO adminDescriptorVO : adminDescriptors) {
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


    if (this.item.getOwner() != null) {
      if (this.item.getOwner().getTitle() != null && this.item.getOwner().getTitle().trim() != "") {
        return this.item.getOwner().getTitle();
      }

      if (this.item.getOwner().getObjectId() != null && this.item.getOwner().getObjectId() != "") {
        return this.item.getOwner().getObjectId();
      }
    }


    return null;
  }

  public String getCreationDate() {
    if (this.item.getCreationDate() != null) {
      return this.item.getCreationDate().toString();
    }

    return null;
  }

  public String getLastModifier() throws Exception {
    if (this.item.getVersion().getModifiedByRO() != null
        && this.item.getVersion().getModifiedByRO().getTitle() != null) {
      return this.item.getVersion().getModifiedByRO().getTitle();
    } else if (this.item.getVersion().getModifiedByRO() != null
        && this.item.getVersion().getModifiedByRO().getObjectId() != null) {
      return this.item.getVersion().getModifiedByRO().getObjectId();
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
    return this.lnkReleaseReleasedItem;
  }

  public void setLnkReleaseReleasedItem(HtmlCommandLink lnkReleaseReleasedItem) {
    this.lnkReleaseReleasedItem = lnkReleaseReleasedItem;
  }

  public void addCreatorString() {
    try {
      this.getEditItemSessionBean().parseCreatorString(
          this.getEditItemSessionBean().getCreatorParseString(), null,
          this.getEditItemSessionBean().getOverwriteCreators());
    } catch (final Exception e) {
      EditItem.logger.error("Could not parse creator string", e);
      FacesBean.error(this.getMessage("ErrorParsingCreatorString"));
    }
  }

  /**
   * Checks if there are any subject classifications defined for this item.
   * 
   * @return true if ther is at least one subject classification.
   * @throws Exception Any exception.
   */
  public boolean getHasSubjectClassification() throws Exception {
    return this.getSubjectTypes() != null;
  }

  /**
   * Get all allowed subject classifications from the admin descriptor of the context.
   * 
   * @return An array of SelectItem containing the subject classifications.
   * @throws Exception Any exception.
   */
  public SelectItem[] getSubjectTypes() throws Exception {
    final ArrayList<SelectItem> result = new ArrayList<SelectItem>();

    final ContextRO contextRO = this.getPubItem().getContext();
    for (final PubContextVOPresentation context : this.getContextListSessionBean()
        .getDepositorContextList()) {
      if (context.getReference().equals(contextRO)) {
        final PublicationAdminDescriptorVO adminDescriptorVO = context.getAdminDescriptor();
        final List<SubjectClassification> list =
            adminDescriptorVO.getAllowedSubjectClassifications();
        if (list != null) {
          for (final SubjectClassification classification : list) {
            final SelectItem selectItem =
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
  public void changeGenre() {
    String newGenre = this.getItem().getMetadata().getGenre().name();
    final Genre[] possibleGenres = MdsPublicationVO.Genre.values();

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
  }

  /**
   * Adds a new local tag to the PubItemVO and a new wrapped local tag to PubItemVOPresentation.
   * 
   * @return Returns always null.
   */
  public void addLocalTag() {
    final WrappedLocalTag wrappedLocalTag = this.getPubItem().new WrappedLocalTag();
    wrappedLocalTag.setParent(this.getPubItem());
    wrappedLocalTag.setValue("");
    this.getPubItem().getWrappedLocalTags().add(wrappedLocalTag);
    this.getPubItem().writeBackLocalTags(null);
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

  /**
   * Takes the text from the hidden input fields, splits it using the delimiter and adds them to the
   * model. Format of alternative titles: alt title 1 ||##|| alt title 2 ||##|| alt title 3 Format
   * of ids: URN|urn:221441 ||##|| URL|http://www.xwdc.de ||##|| ESCIDOC|escidoc:21431
   * 
   * @return
   */
  public void parseAndSetAlternativeTitles() {
    // clear old alternative titles
    final List<AlternativeTitleVO> altTitleList =
        this.getPubItem().getMetadata().getAlternativeTitles();
    altTitleList.clear();

    // clear old identifiers
    final IdentifierManager idManager = this.getIdentifierCollection().getIdentifierManager();
    idManager.getObjectList().clear();

    if (!this.getHiddenAlternativeTitlesField().trim().equals("")) {
      altTitleList.addAll(this.parseAlternativeTitles(this.getHiddenAlternativeTitlesField()));
    }
  }

  private List<AlternativeTitleVO> parseAlternativeTitles(String titleList) {
    final List<AlternativeTitleVO> list = new ArrayList<AlternativeTitleVO>();
    final String[] alternativeTitles = titleList.split(EditItem.HIDDEN_DELIMITER);

    for (int i = 0; i < alternativeTitles.length; i++) {
      final String[] parts = alternativeTitles[i].trim().split(EditItem.AUTOPASTE_INNER_DELIMITER);
      final String alternativeTitleType = parts[0].trim();
      final String alternativeTitle = parts[1].trim();
      if (!alternativeTitle.equals("")) {
        final AlternativeTitleVO alternativeTitleVO = new AlternativeTitleVO(alternativeTitle);
        alternativeTitleVO.setType(alternativeTitleType);
        list.add(alternativeTitleVO);
      }
    }

    return list;
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  private EditItemSessionBean getEditItemSessionBean() {
    return (EditItemSessionBean) FacesTools.findBean("EditItemSessionBean");
  }

  private YearbookItemSessionBean getYearbookItemSessionBean() {
    return (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
  }

  private PubItemListSessionBean getPubItemListSessionBean() {
    return (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
  }

  private BreadcrumbItemHistorySessionBean getBreadcrumbItemHistorySessionBean() {
    return (BreadcrumbItemHistorySessionBean) FacesTools
        .findBean("BreadcrumbItemHistorySessionBean");
  }

  private ContextListSessionBean getContextListSessionBean() {
    return (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
  }
}
