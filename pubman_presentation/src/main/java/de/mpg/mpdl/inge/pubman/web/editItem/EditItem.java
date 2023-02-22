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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jakarta.faces.bean.ManagedBean;
import jakarta.faces.component.html.HtmlCommandLink;
import jakarta.faces.component.html.HtmlSelectOneMenu;
import jakarta.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.primefaces.event.FileUploadEvent;

import com.sun.faces.facelets.component.UIRepeat;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO.State;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.StagedFileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.ProjectInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.SubjectClassification;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.editItem.IdentifierCollection.IdentifierManager;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.releaseItem.ReleaseItem;
import de.mpg.mpdl.inge.pubman.web.submitItem.SubmitItem;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.GenreSpecificItemManager;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.ListItem;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation.WrappedLocalTag;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;
import de.mpg.mpdl.inge.service.aa.AuthorizationService.AccessType;
import de.mpg.mpdl.inge.service.aa.IpListProvider.IpRange;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.util.GrantUtil;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.util.PropertyReader;
import org.primefaces.model.file.UploadedFile;

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

  // für Binding in jsp Seite
  private HtmlCommandLink lnkSave = new HtmlCommandLink();
  private HtmlCommandLink lnkSaveAndSubmit = new HtmlCommandLink();
  // private HtmlCommandLink lnkAccept = new HtmlCommandLink();
  private HtmlCommandLink lnkRelease = new HtmlCommandLink();

  private String contextName = null;
  // FIXME delegated internal collections
  private String hiddenAlternativeTitlesField;
  private IdentifierCollection identifierCollection;
  private List<ListItem> languages = null;
  private String locatorUpload;
  private PubItemVOPresentation item = null;
  private boolean fromEasySubmission = false;
  private HtmlSelectOneMenu genreSelect = new HtmlSelectOneMenu();
  // Flag for the binding method to avoid unnecessary binding
  private boolean bindFilesAndLocators = true;
  private UIRepeat fileIterator;

  public static final String REST_SERVICE_URL = PropertyReader.getProperty(PropertyReader.INGE_REST_SERVICE_URL);
  public static final String REST_COMPONENT_PATH = PropertyReader.getProperty(PropertyReader.INGE_REST_FILE_PATH);

  public EditItem() {
    this.init();
  }

  public void init() {
    try {
      this.initializeItem();
    } catch (final Exception e) {
      throw new RuntimeException("Error initializing item", e);
    }

    if (this.getPubItem() == null) {
      return;
    }

    this.enableLinks();

    // FIXME provide access to parts of my VO to specialized POJO's
    this.identifierCollection = new IdentifierCollection(this.getPubItem().getMetadata().getIdentifiers());

    this.contextName = this.getContextName();
  }

  public String acceptLocalTags() {
    this.getPubItem().writeBackLocalTags();
    this.bindFilesAndLocators = false;

    if (this.getPubItem().getVersionState().equals(ItemVersionRO.State.RELEASED)) {
      return this.saveAndRelease();
    }

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
        final ContextDbVO context =
            this.getItemControllerSessionBean().retrieveContext(this.getPubItem().getObject().getContext().getObjectId());
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
    final ItemVersionVO pubItem = this.getPubItem();
    if (pubItem != null) {
      // set the default genre to article
      if (pubItem.getMetadata().getGenre() == null) {
        pubItem.getMetadata().setGenre(Genre.ARTICLE);
        this.getEditItemSessionBean().setGenreBundle("Genre_" + Genre.ARTICLE.toString());
      } else { // if(this.getEditItemSessionBean().getGenreBundle().trim().equals(""))
        this.getEditItemSessionBean().setGenreBundle("Genre_" + pubItem.getMetadata().getGenre().name());
      }

      this.getItemControllerSessionBean().initializeItem(pubItem);

      if (!this.getEditItemSessionBean().isFilesInitialized() || this.getLocators().size() == 0) {
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
          } else if (creatorVO.getType() == CreatorType.ORGANIZATION && creatorVO.getOrganization() == null) {
            creatorVO.setOrganization(new OrganizationVO());
          }
          if (creatorVO.getType() == CreatorType.PERSON && creatorVO.getPerson().getOrganizations() != null) {
            for (final OrganizationVO organizationVO : creatorVO.getPerson().getOrganizations()) {
              if (organizationVO.getName() == null) {
                organizationVO.setName("");
              }
            }
          } else if (creatorVO.getType() == CreatorType.ORGANIZATION && creatorVO.getOrganization() != null
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
            } else if (creatorVO.getType() == CreatorType.ORGANIZATION && creatorVO.getOrganization() == null) {
              creatorVO.setOrganization(new OrganizationVO());
            }
            if (creatorVO.getType() == CreatorType.PERSON && creatorVO.getPerson().getOrganizations() != null) {
              for (final OrganizationVO organizationVO : creatorVO.getPerson().getOrganizations()) {
                if (organizationVO.getName() == null) {
                  organizationVO.setName("");
                }
              }
            } else if (creatorVO.getType() == CreatorType.ORGANIZATION && creatorVO.getOrganization() != null
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
    // add files
    final List<PubFileVOPresentation> files = new ArrayList<PubFileVOPresentation>();
    int fileCount = 0;

    for (final FileDbVO file : this.getPubItem().getFiles()) {
      if (file.getStorage().equals(FileDbVO.Storage.INTERNAL_MANAGED)) {
        // Add identifierVO if not available yet
        if (file.getMetadata() != null //
            && (file.getMetadata().getIdentifiers() == null //
                || file.getMetadata().getIdentifiers().isEmpty())) {
          file.getMetadata().getIdentifiers().add(new IdentifierVO());
        }
        if (file.getAllowedAudienceIds() == null || file.getAllowedAudienceIds().isEmpty()) {
          file.setAllowedAudienceIds(new ArrayList<>());
          file.getAllowedAudienceIds().add(null);
        }

        final PubFileVOPresentation pubFileVOPresentation = new PubFileVOPresentation(fileCount, file, false);
        files.add(pubFileVOPresentation);
        fileCount++;
      }
    }

    this.setFiles(files);

    // add locators
    final List<PubFileVOPresentation> locators = new ArrayList<PubFileVOPresentation>();
    int locatorCount = 0;

    for (final FileDbVO file : this.getPubItem().getFiles()) {
      if (file.getStorage().equals(FileDbVO.Storage.EXTERNAL_URL)) {
        final PubFileVOPresentation pubFileVOPresentation = new PubFileVOPresentation(locatorCount, file, true);

        // This is a small hack for locators generated out of Bibtex files
        if (pubFileVOPresentation.getLocator() == null && pubFileVOPresentation.getFile() != null
            && pubFileVOPresentation.getFile().getName() != null) {
          pubFileVOPresentation.setLocator(pubFileVOPresentation.getFile().getName().trim());
          pubFileVOPresentation.getFile().setMetadata(new MdsFileVO());
          pubFileVOPresentation.getFile().getMetadata().setTitle(pubFileVOPresentation.getFile().getName());
        }

        // And here it ends
        locators.add(pubFileVOPresentation);
        locatorCount++;
      }
    }

    this.setLocators(locators);
    this.getEditItemSessionBean().checkMinAnzLocators();

    //    // make sure that at least one locator and one file is stored in the EditItemSessionBean
    //    // TODO: where is the one file??
    //    if (this.getEditItemSessionBean().getLocators().size() < 1) {
    //      final FileDbVO newLocator = new FileDbVO();
    //      newLocator.setMetadata(new MdsFileVO());
    //      newLocator.setStorage(FileDbVO.Storage.EXTERNAL_URL);
    //      this.getEditItemSessionBean().getLocators().add(new PubFileVOPresentation(0, newLocator, true));
    //    }
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
          final MdsFileVO defaultMetadata = loc.getFile().getMetadata();
          final String title = defaultMetadata.getTitle();
          if (title == null || title.trim().equals("")) {
            defaultMetadata.setTitle(loc.getFile().getContent());
          }

          // Visibility PUBLIC is static default value for locators
          loc.getFile().setVisibility(Visibility.PUBLIC);
          pubItem.getFiles().add(loc.getFile());
        }
      }
    } else {
      this.bindFilesAndLocators = true;
    }
  }


  public List<ListItem> getLanguages() throws Exception {
    if (this.languages == null) {
      this.languages = new ArrayList<ListItem>();
      if (this.getPubItem().getMetadata().getLanguages().size() == 0) {
        this.getPubItem().getMetadata().getLanguages().add("");
      }
      int counter = 0;
      for (final Iterator<String> iterator = this.getPubItem().getMetadata().getLanguages().iterator(); iterator.hasNext();) {
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
      return null;
    }

    try {
      ItemVersionVO itemVO = new ItemVersionVO(this.getPubItem()); // Validierung arbeitet mit Kopie
      PubItemUtil.cleanUpItem(itemVO);
      cleanUp(itemVO);
      ApplicationBean.INSTANCE.getItemValidatingService().validate(itemVO, ValidationPoint.STANDARD);
      this.info(this.getMessage("itemIsValid"));
    } catch (final ValidationException e) {
      this.showValidationMessages(e.getReport());
      return null;
    } catch (final Exception e) {
      EditItem.logger.error("Could not validate item." + "\n" + e.toString(), e);
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
      return ErrorPage.LOAD_ERRORPAGE;
    }

    return "";
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
    this.getEditItemSessionBean().bindCreatorsToVO(this.getPubItem().getMetadata().getCreators());

    // write source creators back to VO
    for (final SourceBean sourceBean : this.getEditItemSessionBean().getSources()) {
      sourceBean.bindCreatorsToVO(sourceBean.getSource().getCreators());
    }

    // write sources back to VO
    this.getEditItemSessionBean().bindSourcesToVO(this.getPubItem().getMetadata().getSources());

    return true;
  }

  private boolean check() {
    return this.getPubItem() != null && this.restoreVO();
  }

  private void cleanUp(ItemVersionVO pubItem) {
    // cleanup item according to genre specific MD specification
    final GenreSpecificItemManager itemManager = new GenreSpecificItemManager(pubItem, GenreSpecificItemManager.SUBMISSION_METHOD_FULL);
    try {
      pubItem = (ItemVersionVO) itemManager.cleanupItem();
    } catch (final Exception e) {
      throw new RuntimeException("Error while cleaning up item genre specificly", e);
    }
  }

  private String checkItemChanged(String navigateTo) {
    final ItemVersionVO newPubItem = this.getItemControllerSessionBean().getCurrentPubItem();
    ItemVersionVO oldPubItem = null;
    if (newPubItem.getObjectId() != null) {
      try {
        oldPubItem = this.getItemControllerSessionBean().retrieveItem(newPubItem.getObjectId());
      } catch (final Exception e) {
        EditItem.logger.error("Could not retrieve item." + "\n" + e.toString(), e);
        ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
        return ErrorPage.LOAD_ERRORPAGE;
      }

      if (!this.getItemControllerSessionBean().hasChanged(oldPubItem, newPubItem)) {
        if (ItemVersionRO.State.RELEASED.equals(newPubItem.getVersionState())) {
          EditItem.logger.warn("Item has not been changed.");
          // create a validation report
          final ValidationReportVO changedReport = new ValidationReportVO();
          final ValidationReportItemVO changedReportItem =
              new ValidationReportItemVO("itemHasNotBeenChanged", ValidationReportItemVO.Severity.ERROR);
          changedReport.addItem(changedReportItem);
          // show report and stay on this page
          this.showValidationMessages(changedReport);

          return "";
        }
      } else if (oldPubItem.getMetadata() != null && oldPubItem.getMetadata().getTitle() != null && newPubItem.getMetadata() != null
          && newPubItem.getMetadata().getTitle() != null
          && !(oldPubItem.getMetadata().getTitle()).equals(newPubItem.getMetadata().getTitle())) {
        this.error(this.getMessage("EditItemTitleHasChanged")
            .replace("$1", oldPubItem.getMetadata().getTitle() + " --> " + newPubItem.getMetadata().getTitle())
            .replace("$2", oldPubItem.getObjectId()));
      }
    }

    return navigateTo;
  }

  private String saveItem(String navigateTo) {
    try {
      return this.getItemControllerSessionBean().saveCurrentPubItem(navigateTo);
    } catch (ValidationException e) {
      this.showValidationMessages(e.getReport());
    }

    return "";
  }

  /**
   * Cancels the editing.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String cancel() {
    // examine if the user came from the view Item Page or if he started a new submission

    this.cleanEditItem();
    // set the current submission method to empty string (for GUI purpose)
    this.getEditItemSessionBean().setCurrentSubmission("");

    try {
      // if
      // ("ViewLocalTagsPage.jsp".equals(this.getBreadcrumbItemHistorySessionBean().getPreviousItem().getPage()))
      // {
      // final String viewItemPage =
      // PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
      // + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH)
      // + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN).replaceFirst("\\$1",
      // this.getPubItem().getObjectId());
      // FacesTools.getExternalContext().redirect(viewItemPage);
      // } else
      if (this.getBreadcrumbItemHistorySessionBean().getPreviousItem().getPage().contains("ViewItemFullPage.jsp")) {
        FacesTools.getExternalContext().redirect(
            FacesTools.getRequest().getContextPath() + "/faces/" + this.getBreadcrumbItemHistorySessionBean().getPreviousItem().getPage());
      } else if (null != this.getPubItem().getObjectId()) {
        FacesTools.getExternalContext()
            .redirect(FacesTools.getRequest().getContextPath() + "/faces/ViewItemFullPage.jsp?itemId=" + this.getPubItem().getObjectId());
      } else {
        FacesTools.getExternalContext().redirect("faces/SubmissionPage.jsp");
      }
    } catch (final Exception e) {
      EditItem.logger.error("Could not redirect to the previous page", e);
    }

    return ViewItemFull.LOAD_VIEWITEM;
  }

  /**
   * This method cleans up all the helping constructs like collections etc.
   */
  private void cleanEditItem() {
    this.item = null;
    this.identifierCollection = null;
    this.languages = null;
  }

  private String saveAndGoto(String navigateTo) {
    if (check() == false) {
      return "";
    }

    cleanUp(this.getPubItem());

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

    if (ViewItemFull.LOAD_VIEWITEM.equals(navigateTo)) {
      try {
        if (this.isFromEasySubmission()) {
          FacesTools.getExternalContext().redirect(FacesTools.getRequest().getContextPath() + "/faces/ViewItemFullPage.jsp?itemId="
              + this.getItemControllerSessionBean().getCurrentPubItem().getObjectId() + "&fromEasySub=true");
        } else {
          FacesTools.getExternalContext().redirect(FacesTools.getRequest().getContextPath() + "/faces/ViewItemFullPage.jsp?itemId="
              + this.getItemControllerSessionBean().getCurrentPubItem().getObjectId());
        }
      } catch (final IOException e) {
        EditItem.logger.error("Could not redirect to View Item Page", e);
      }
    }

    return retVal;
  }

  public String save() {
    return saveAndGoto(ViewItemFull.LOAD_VIEWITEM);
  }

  public String saveAndSubmit() {
    //    if (this.getPubItem().getObjectId() == null) {
    String retVal = this.validate();
    if ("".equals(retVal) == false) {
      return retVal;
    }
    //    }

    return saveAndGoto(SubmitItem.LOAD_SUBMITITEM);
  }

  public String saveAndRelease() {
    //    if (this.getPubItem().getObjectId() == null) {
    String retVal = this.validate();
    if ("".equals(retVal) == false) {
      return retVal;
    }
    //    }

    return saveAndGoto(ReleaseItem.LOAD_RELEASEITEM);
  }

  public void fileUploaded(FileUploadEvent event) {
    this.stageFile(event.getFile());
  }

  private String stageFile(UploadedFile file) {
    if (file != null && file.getSize() > 0) {
      try {
        String path = null;
        try {
          StagedFileDbVO stagedFile = ApplicationBean.INSTANCE.getFileService().createStageFile(file.getInputStream(), file.getFileName(),
              getLoginHelper().getAuthenticationToken());
          path = String.valueOf(stagedFile.getId());
        } catch (Exception e) {
          logger.error("Could not upload staged file [" + path + "]", e);
          this.error(this.getMessage("File_noUpload") + "[" + path + "]");
        }

        String mimeType = null;
        final Tika tika = new Tika();
        try {
          final InputStream fis = file.getInputStream();
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
      } catch (Exception e) {
        e.printStackTrace();
        this.error(this.getMessage("Error uploading file"));
      }
    } else {
      this.error(this.getMessage("ComponentEmpty"));
    }

    return "";
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

  /**
   * This method adds a locator to the list of locators of the item
   * 
   * @return navigation string (null)
   */
  public void addLocator() {
    if (this.getLocators() != null) {
      final FileDbVO newLocator = new FileDbVO();
      newLocator.setMetadata(new MdsFileVO());
      newLocator.setStorage(FileDbVO.Storage.EXTERNAL_URL);
      this.getLocators().add(new PubFileVOPresentation(this.getLocators().size(), newLocator, true));
    }
  }

  /**
   * This method saves the latest locator to the list of files of the item
   * 
   * @return navigation string (null)
   */
  public void saveLocator() {
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
  }

  private void showValidationMessages(ValidationReportVO report) {
    for (final Iterator<ValidationReportItemVO> iter = report.getItems().iterator(); iter.hasNext();) {
      final ValidationReportItemVO element = iter.next();

      switch (element.getSeverity()) {
        case ERROR:
          this.error(this.getMessage(element.getContent()).replaceAll("\\$1", element.getElement()));
          break;

        case WARNING:
          this.warn(this.getMessage(element.getContent()).replaceAll("\\$1", element.getElement()));
          break;

        default:
          break;
      }
    }
  }

  private void enableLinks() {
    /*
     * boolean isStatePending = true; boolean isStateSubmitted = false; boolean isStateReleased =
     * false; // boolean isStateInRevision = false; boolean isStateWithdrawn = false; // boolean
     * isPublicStateReleased = false;
     * 
     * if (this.getPubItem() != null && this.getPubItem().getVersionState() != null) {
     * isStatePending = ItemVersionRO.State.PENDING.equals(this.getPubItem().getVersionState());
     * isStateSubmitted = ItemVersionRO.State.SUBMITTED.equals(this.getPubItem().getVersionState());
     * isStateReleased = ItemVersionRO.State.RELEASED.equals(this.getPubItem().getVersionState());
     * // isStateInRevision =
     * ItemVersionRO.State.IN_REVISION.equals(this.getPubItem().getVersionState()); isStateWithdrawn
     * = ItemVersionRO.State.WITHDRAWN.equals(this.getPubItem().getVersionState()); //
     * isPublicStateReleased =
     * ItemVersionRO.State.RELEASED.equals(this.getPubItem().getObject().getPublicState()); }
     * 
     * boolean isOwner = true; if (this.getPubItem() != null &&
     * this.getPubItem().getObject().getCreator() != null) { isOwner =
     * (this.getLoginHelper().getAccountUser() != null ?
     * this.getLoginHelper().getAccountUser().getObjectId().equals(this.getPubItem().getObject().
     * getCreator().getObjectId()) : false); }
     */
    boolean isModerator = false;
    if (this.getLoginHelper().getAccountUser() != null && this.getPubItem() != null) {
      isModerator = GrantUtil.hasRole(this.getLoginHelper().getAccountUser(), PredefinedRoles.MODERATOR,
          this.getPubItem().getObject().getContext().getObjectId());
    }

    /*
     * boolean isWorkflowStandard = false; boolean isWorkflowSimple = true;
     * 
     * try { if (this.getItemControllerSessionBean().getCurrentContext() != null) {
     * isWorkflowStandard = (ContextDbVO.Workflow.STANDARD ==
     * this.getItemControllerSessionBean().getCurrentContext().getWorkflow()); isWorkflowSimple =
     * (ContextDbVO.Workflow.SIMPLE ==
     * this.getItemControllerSessionBean().getCurrentContext().getWorkflow()); } } catch (final
     * Exception e) { throw new RuntimeException("Previously uncaught exception", e); }
     */

    // this.lnkAccept.setRendered(false);
    this.lnkRelease.setRendered(false);
    this.lnkSaveAndSubmit.setRendered(false);
    this.lnkSave.setRendered(false);

    try {
      PubItemService pis = ApplicationBean.INSTANCE.getPubItemService();

      boolean canEdit = pis.checkAccess(AccessType.EDIT, getLoginHelper().getPrincipal(), this.getPubItem());
      this.lnkSave.setRendered(canEdit);

      ItemVersionVO itemAfterSave = this.getPubItem();
      if (ItemVersionRO.State.RELEASED.equals(this.getPubItem().getVersionState())) {
        itemAfterSave = new ItemVersionVO(this.getPubItem());
        itemAfterSave.setVersionState(isModerator ? State.SUBMITTED : State.PENDING);
      }

      this.lnkRelease.setRendered(canEdit && pis.checkAccess(AccessType.RELEASE, getLoginHelper().getPrincipal(), itemAfterSave));
      this.lnkSaveAndSubmit.setRendered(canEdit && pis.checkAccess(AccessType.SUBMIT, getLoginHelper().getPrincipal(), itemAfterSave));

      // this.lnkAccept.setRendered(false);
    } catch (Exception e) {
      this.error(this.getMessage("AccessInfoError"));
      logger.error("Error while getting access information", e);
    }

    /*
     * this.lnkRelease.setRendered(isOwner && isWorkflowSimple && (isStatePending ||
     * isStateSubmitted || isStateReleased)); this.lnkAccept.setRendered(isModerator && !isOwner &&
     * (isStateSubmitted || isStateReleased)); this.lnkSave.setRendered(isOwner || isModerator);
     * this.lnkSaveAndSubmit.setRendered(isOwner && isWorkflowStandard && !(isStateSubmitted ||
     * isStateReleased || isStateWithdrawn));
     */
  }

  public boolean getLocalTagEditingAllowed() {
    final ViewItemFull viewItemFull = (ViewItemFull) FacesTools.findBean("ViewItemFull");

    boolean isWorkflowSimple = true;

    try {
      if (this.getItemControllerSessionBean().getCurrentContext() != null) {
        isWorkflowSimple = (ContextDbVO.Workflow.SIMPLE == this.getItemControllerSessionBean().getCurrentContext().getWorkflow());
      }
    } catch (final Exception e) {
      throw new RuntimeException("Previously uncaught exception", e);
    }

    return viewItemFull.getIsLatestVersion()
        && ((viewItemFull.getIsStateReleased() || viewItemFull.getIsStateSubmitted()) && viewItemFull.getIsModerator()
            || (viewItemFull.getIsStatePending() || viewItemFull.getIsStateSubmitted() && isWorkflowSimple
                || viewItemFull.getIsStateInRevision()) && viewItemFull.getIsOwner());
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

  public SelectItem[] getOaStatuses() {
    return this.getI18nHelper().getSelectItemsOaStatus(false);
  }

  public SelectItem[] getInvitationStatuses() {
    return this.getI18nHelper().getSelectItemsInvitationStatus(true);
  }

  public String getIpListReady() {
    if (PropertyReader.getProperty(PropertyReader.INGE_AUTH_MPG_IP_LIST_USE).equalsIgnoreCase("true")) {
      return "true";
    }

    return "false";
  }

  public List<SelectItem> getAudienceIpListSelectItems() {

    List<SelectItem> ipRangeSelectItems = new ArrayList<>();

    for (IpRange ipRange : ApplicationBean.INSTANCE.getIpListProvider().getAll()) {
      ipRangeSelectItems.add(new SelectItem(ipRange.getId(), ipRange.getName()));
    }

    Collections.sort(ipRangeSelectItems, (a, b) -> a.getLabel().compareTo(b.getLabel()));
    ipRangeSelectItems.add(0, new SelectItem(null, "-"));
    return ipRangeSelectItems;
  }

  /**
   * Invitationstatus of event has to be converted as it's an enum that is supposed to be shown in a
   * checkbox.
   * 
   * @return true if invitationstatus in VO is set, else false
   */
  public boolean getInvited() {
    // Changed by FrM: Check for event
    if (this.getPubItem().getMetadata().getEvent() != null && this.getPubItem().getMetadata().getEvent().getInvitationStatus() != null
        && this.getPubItem().getMetadata().getEvent().getInvitationStatus().equals(EventVO.InvitationStatus.INVITED)) {
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
      this.getPubItem().getMetadata().getEvent().setInvitationStatus(EventVO.InvitationStatus.INVITED);
    } else {
      this.getPubItem().getMetadata().getEvent().setInvitationStatus(null);
    }
  }

  // public HtmlCommandLink getLnkAccept() {
  // return this.lnkAccept;
  // }
  //
  // public void setLnkAccept(HtmlCommandLink lnkAccept) {
  // this.lnkAccept = lnkAccept;
  // }

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
    if (this.getPubItem().getMetadata().getEvent() != null && this.getPubItem().getMetadata().getEvent().getTitle() != null) {
      return this.getPubItem().getMetadata().getEvent().getTitle();
    }

    return "";
  }

  public void setEventTitle(String title) {
    if (this.getPubItem().getMetadata().getEvent() != null && this.getPubItem().getMetadata().getEvent().getTitle() != null) {
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
    if (this.getFiles() != null) {
      return this.getFiles().size();
    }

    return 0;
  }

  public int getNumberOfLocators() {
    if (this.getLocators() != null) {
      return this.getLocators().size();
    }

    return 0;
  }

  public void setItem(PubItemVOPresentation item) {
    this.item = item;
  }

  public String getOwner() throws Exception {
    if (this.getPubItem().getObject().getCreator() != null) {
      if (this.getPubItem().getObject().getCreator().getName() != null
          && this.getPubItem().getObject().getCreator().getName().trim() != "") {
        return this.getPubItem().getObject().getCreator().getName();
      }

      if (this.getPubItem().getObject().getCreator().getObjectId() != null
          && this.getPubItem().getObject().getCreator().getObjectId() != "") {
        return this.getPubItem().getObject().getCreator().getObjectId();
      }
    }

    return null;
  }

  public String getCreationDate() {
    if (this.getPubItem().getObject().getCreationDate() != null) {
      return this.getPubItem().getObject().getCreationDate().toString();
    }

    return null;
  }

  public String getLastModifier() throws Exception {
    if (this.getPubItem().getModifier() != null && this.getPubItem().getModifier().getName() != null) {
      return this.getPubItem().getModifier().getName();
    } else if (this.getPubItem().getModifier() != null && this.getPubItem().getModifier().getObjectId() != null) {
      return this.getPubItem().getModifier().getObjectId();
    }

    return null;
  }

  public String getLastModificationDate() {
    if (this.getPubItem().getModificationDate() != null) {
      return this.getPubItem().getModificationDate().toString();
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

  public void addCreatorString() {
    try {
      this.getEditItemSessionBean().parseCreatorString(this.getEditItemSessionBean().getCreatorParseString(), null,
          this.getEditItemSessionBean().getOverwriteCreators());
    } catch (final Exception e) {
      EditItem.logger.error("Could not parse creator string", e);
      this.error(this.getMessage("ErrorParsingCreatorString"));
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

    result.add(new SelectItem(null, "-"));
    final ContextDbRO contextRO = this.getPubItem().getObject().getContext();
    ArrayList<PubContextVOPresentation> userContexts = new ArrayList<PubContextVOPresentation>();
    userContexts.addAll(this.getContextListSessionBean().getDepositorContextList());
    userContexts.addAll(this.getContextListSessionBean().getModeratorContextList());
    for (final PubContextVOPresentation context : userContexts) {
      if (context.getObjectId().equals(contextRO.getObjectId())) {
        final List<SubjectClassification> list = context.getAllowedSubjectClassifications();
        if (list != null) {
          for (final SubjectClassification classification : list) {
            final SelectItem selectItem = new SelectItem(classification.name(), classification.name().replace("_", "-"));
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
    String newGenre = this.getPubItem().getMetadata().getGenre().name();
    final Genre[] possibleGenres = MdsPublicationVO.Genre.values();

    for (int i = 0; i < possibleGenres.length; i++) {
      if (possibleGenres[i].toString().equals(newGenre)) {
        this.getPubItem().getMetadata().setGenre(possibleGenres[i]);
        this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setGenre(possibleGenres[i]);
      }
    }

    if (newGenre != null && newGenre.trim().equals("")) {
      newGenre = "ARTICLE";
    }

    this.getEditItemSessionBean().setGenreBundle("Genre_" + newGenre);
    this.init();
  }

  /**
   * Adds a new local tag to the ItemVersionVO and a new wrapped local tag to PubItemVOPresentation.
   * 
   * @return Returns always null.
   */
  public void addLocalTag() {
    final WrappedLocalTag wrappedLocalTag = this.getPubItem().new WrappedLocalTag();
    wrappedLocalTag.setParent(this.getPubItem());
    wrappedLocalTag.setValue("");
    this.getPubItem().getWrappedLocalTags().add(wrappedLocalTag);
    this.getPubItem().writeBackLocalTags();
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
    final List<AlternativeTitleVO> altTitleList = this.getPubItem().getMetadata().getAlternativeTitles();
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

  public void addProjectInfo() {
    this.getPubItem().getMetadata().getProjectInfo().add(new ProjectInfoVO());
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  private EditItemSessionBean getEditItemSessionBean() {
    return (EditItemSessionBean) FacesTools.findBean("EditItemSessionBean");
  }

  private PubItemListSessionBean getPubItemListSessionBean() {
    return (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
  }

  private BreadcrumbItemHistorySessionBean getBreadcrumbItemHistorySessionBean() {
    return (BreadcrumbItemHistorySessionBean) FacesTools.findBean("BreadcrumbItemHistorySessionBean");
  }

  private ContextListSessionBean getContextListSessionBean() {
    return (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
  }
}
