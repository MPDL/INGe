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

package de.mpg.mpdl.inge.pubman.web.util.beans;

import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO.State;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FundingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FundingOrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FundingProgramVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.LegalCaseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.ProjectInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem.SubmissionMethod;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItem;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.impl.ItemTransformingServiceImpl;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;

@ManagedBean(name = "ItemControllerSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class ItemControllerSessionBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(ItemControllerSessionBean.class);

  private PubItemVOPresentation currentPubItem = null;
  private ContextDbVO currentContext = null;
  private final ItemTransformingService itemTransformingService = new ItemTransformingServiceImpl();

  public ItemControllerSessionBean() {}

  //  /**
  //   * Accepts an item.
  //   *
  //   * @param pubItem the item that should be accepted
  //   * @return string, identifying the page that should be navigated to after this method call
  //   * @throws Exception if framework access fails
  //   */
  //  public String acceptCurrentPubItem(String navigationRuleWhenSuccessfull, String comment) {
  //    try {
  //      final ItemVersionVO updatedPubItem = ApplicationBean.INSTANCE.getPubItemService().releasePubItem(this.currentPubItem.getObjectId(),
  //          this.currentPubItem.getModificationDate(), comment, this.getLoginHelper().getAuthenticationToken());
  //
  //      this.setCurrentPubItem(new PubItemVOPresentation(updatedPubItem));
  //      return navigationRuleWhenSuccessfull;
  //    } catch (final Exception e) {
  //      ItemControllerSessionBean.logger.error("Error while accepting current PubItem", e);
  //      this.error("Error while accepting current PubItem" + e.getMessage());
  //    }
  //
  //    return "";
  //  }

  /**
   * use an old item as template to creat a new one
   *
   * @return String navigation to edit Item
   */
  public String createItemFromTemplate() {
    // clear the list of locators and files when start creating a new revision
    final EditItemSessionBean editItemSessionBean = this.getEditItemSessionBean();
    editItemSessionBean.clean();

    // Changed by DiT, 29.11.2007: only show contexts when user has privileges for more than one
    // context
    // if there is only one context for this user we can skip the CreateItem-Dialog and create the
    // new item directly
    if (this.getContextListSessionBean().getDepositorContextList().isEmpty()) {
      ItemControllerSessionBean.logger.warn("The user does not have privileges for any context.");
      this.error(this.getMessage("ViewItemFull_user_has_no_context"));
      return null;
    }

    final ItemVersionVO newItem = new ItemVersionVO(this.currentPubItem);
    newItem.setObjectId(null);
    newItem.getObject().setObjectPid(null);
    newItem.setVersionNumber(0);
    newItem.setVersionState(State.PENDING);
    newItem.setVersionPid(null);
    newItem.getObject().setPublicState(State.PENDING);
    AccountUserDbRO creator = new AccountUserDbRO();
    creator.setObjectId(getLoginHelper().getAccountUser().getObjectId());
    newItem.getObject().setCreator(creator);
    newItem.getFiles().clear();
    // clear local tags [PUBMAN-2478]
    newItem.getObject().getLocalTags().clear();
    // clear the relation list according to PUBMAN-357
    /*
    if (newItem.getRelations() != null) {
      newItem.getRelations().clear();
    }
    */

    this.setCurrentPubItem(new PubItemVOPresentation(newItem));

    if (this.getContextListSessionBean().getDepositorContextList().size() == 1) {
      final ContextDbVO context = this.getContextListSessionBean().getDepositorContextList().get(0);
      newItem.getObject().setContext(context);

      this.setCurrentPubItem(new PubItemVOPresentation(newItem));

      editItemSessionBean.initEmptyComponents();
      return EditItem.LOAD_EDITITEM;
    } else {
      // more than one context exists for this user; let him choose the right one
      newItem.getObject().setContext(null);

      this.setCurrentPubItem(new PubItemVOPresentation(newItem));

      // Set submission method for correct redirect
      ((CreateItem) FacesTools.findBean("CreateItem")).setMethod(SubmissionMethod.FULL_SUBMISSION);

      editItemSessionBean.initEmptyComponents();
      return CreateItem.LOAD_CREATEITEM;
    }
  }

  /**
   * Creates a new PubItem and handles navigation afterwards.
   *
   * @param navigationRuleWhenSuccessful the navigation rule which should be returned when the
   *        operation is successful.
   * @param pubContextRO The eSciDoc context.
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String createNewPubItem(final String navigationRuleWhenSuccessful, final ContextDbRO pubContextRO) {
    ItemVersionVO newPubItem = new ItemVersionVO();
    newPubItem.getObject().setContext(pubContextRO);
    AccountUserDbRO creator = new AccountUserDbRO();
    creator.setObjectId(getLoginHelper().getAccountUser().getObjectId());
    newPubItem.getObject().setCreator(creator);
    newPubItem.setMetadata(new MdsPublicationVO());
    newPubItem = this.initializeItem(newPubItem);
    this.setCurrentPubItem(new PubItemVOPresentation(newPubItem));

    return navigationRuleWhenSuccessful;
  }

  /**
   * Deletes a PubItem and handles navigation afterwards.
   *
   * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when the
   *        operation is successfull
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String deleteCurrentPubItem(String navigationRuleWhenSuccessfull) {
    try {
      ApplicationBean.INSTANCE.getPubItemService().delete(this.currentPubItem.getObjectId(),
          this.getLoginHelper().getAuthenticationToken());
      this.setCurrentPubItem(null);
      return navigationRuleWhenSuccessfull;
    } catch (final AuthenticationException | AuthorizationException e) {
      ItemControllerSessionBean.logger.error("Authentication/Authorization error while deleting current PubItem", e);
      this.error(this.getMessage("ItemControllerSessionBean_noPermissionDelete") + e.getMessage());
    } catch (final IngeTechnicalException e) {
      ItemControllerSessionBean.logger.error("Technical Error while deleting current PubItem", e);
      this.error(this.getMessage("ItemControllerSessionBean_errorDelete") + e.getMessage());
    } catch (final IngeApplicationException e) {
      ItemControllerSessionBean.logger.error("Application error while deleting current PubItem", e);
      this.error(this.getMessage("ItemControllerSessionBean_applicationErrorDelete") + e.getMessage());
    }

    return "";
  }

  private ContextListSessionBean getContextListSessionBean() {
    return (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
  }

  public ContextDbVO getCurrentContext() {
    // retrieve current context newly if the current item has changed or if the context has not been
    // retrieved so far
    if (this.currentPubItem != null) {
      if (this.currentContext == null
          || !(this.currentContext.getObjectId().equals(this.currentPubItem.getObject().getContext().getObjectId()))) {
        final ContextDbVO context = this.retrieveContext(this.currentPubItem.getObject().getContext().getObjectId());
        this.setCurrentCollection(context);
      }
    }

    return this.currentContext;
  }

  public PubItemVOPresentation getCurrentPubItem() {
    return this.currentPubItem;
  }

  public ContextDbVO.Workflow getCurrentWorkflow() {
    final ContextDbVO.Workflow workflow = this.getCurrentContext().getWorkflow();
    if (workflow == null || workflow == ContextDbVO.Workflow.SIMPLE) {
      return ContextDbVO.Workflow.SIMPLE;
    } else if (workflow == ContextDbVO.Workflow.STANDARD) {
      return ContextDbVO.Workflow.STANDARD;
    }

    return ContextDbVO.Workflow.SIMPLE;
  }

  private EditItemSessionBean getEditItemSessionBean() {
    return (EditItemSessionBean) FacesTools.findBean("EditItemSessionBean");
  }

  /**
   * Tests if the metadata of the two items have changed.
   *
   * @param oldPubItem the old pubItem
   * @param newPubItem the new (maybe changed) pubItem
   * @return true if the metadata of the new item has changed
   */
  public boolean hasChanged(ItemVersionVO oldPubItem, ItemVersionVO newPubItem) {
    // clone both items
    final ItemVersionVO oldPubItemClone = new ItemVersionVO(oldPubItem);
    final ItemVersionVO newPubItemClone = (new ItemVersionVO(newPubItem));

    // clean both items up from unused sub-VOs
    PubItemUtil.cleanUpItem(oldPubItemClone);
    PubItemUtil.cleanUpItem(newPubItemClone);

    // compare the metadata and files of the two items
    final boolean metadataChanged = !(oldPubItemClone.getMetadata().equals(newPubItemClone.getMetadata()));
    final boolean fileChanged = !(oldPubItemClone.getFiles().equals(newPubItemClone.getFiles()));
    final boolean localTagsChanged = !(oldPubItemClone.getObject().getLocalTags().equals(newPubItemClone.getObject().getLocalTags()));

    return (metadataChanged || fileChanged || localTagsChanged);
  }

  /**
   * Initializes a new item with ValueObjects.
   *
   * @return the initialized item.
   */
  public ItemVersionVO initializeItem(ItemVersionVO newPubItem) {
    // version
    /*
    if (newPubItem.getVersion() == null) {
      final ItemRO version = new ItemRO();
      newPubItem.setVersion(version);
    }
    */


    // Status
    if (newPubItem.getVersionState() == null) {
      newPubItem.setVersionState(State.PENDING);
    }

    // Status
    if (newPubItem.getObject().getPublicState() == null) {
      newPubItem.getObject().setPublicState(State.PENDING);
    }

    // Title
    if (newPubItem.getMetadata().getTitle() == null) {
      newPubItem.getMetadata().setTitle("");
    }

    // Genre
    if (newPubItem.getMetadata().getGenre() == null) {
      final ContextDbVO contextVO = this.retrieveContext(newPubItem.getObject().getContext().getObjectId());

      if (contextVO.getAllowedGenres().contains(Genre.ARTICLE)) {
        newPubItem.getMetadata().setGenre(Genre.ARTICLE);
      } else if (!contextVO.getAllowedGenres().isEmpty()) {
        newPubItem.getMetadata().setGenre(contextVO.getAllowedGenres().get(0));
      }

    }

    // Creator
    if (newPubItem.getMetadata().getCreators().isEmpty()) {
      final CreatorVO newCreator = new CreatorVO();

      newCreator.setType(CreatorType.PERSON);
      newCreator.setRole(CreatorRole.AUTHOR);
      // create a new Organization for this person
      final PersonVO newPerson = new PersonVO();
      newPerson.setIdentifier(new IdentifierVO());
      newPerson.getIdentifier().setType(IdType.CONE);
      final OrganizationVO newPersonOrganization = new OrganizationVO();
      newPersonOrganization.setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));
      newPerson.getOrganizations().add(newPersonOrganization);
      newCreator.setPerson(newPerson);
      newPubItem.getMetadata().getCreators().add(newCreator);
    }

    // Publishing info
    if (newPubItem.getMetadata().getPublishingInfo() == null) {
      newPubItem.getMetadata().setPublishingInfo(new PublishingInfoVO());
    }

    // Identifiers
    if (newPubItem.getMetadata().getIdentifiers().isEmpty()) {
      newPubItem.getMetadata().getIdentifiers().add(new IdentifierVO());
    }

    // Abstracts
    if (newPubItem.getMetadata().getAbstracts().isEmpty()) {
      newPubItem.getMetadata().getAbstracts().add(new AbstractVO());
    }

    // Subjects
    if (newPubItem.getMetadata().getSubjects().isEmpty()) {
      newPubItem.getMetadata().getSubjects().add(new SubjectVO());
    }

    // Language
    if (newPubItem.getMetadata().getLanguages().isEmpty()) {
      newPubItem.getMetadata().getLanguages().add("");
    }
    // Source
    if (newPubItem.getMetadata().getSources().isEmpty()) {
      final SourceVO newSource = new SourceVO();
      newPubItem.getMetadata().getSources().add(newSource);
    }

    for (final SourceVO source : newPubItem.getMetadata().getSources()) {
      if (source.getTitle() == null) {
        source.setTitle("");
      }
      if (source.getPublishingInfo() == null) {
        final PublishingInfoVO newSourcePublishingInfo = new PublishingInfoVO();
        source.setPublishingInfo(newSourcePublishingInfo);
      }
      if (source.getCreators().isEmpty()) {
        final CreatorVO newSourceCreator = new CreatorVO();
        // create a new Organization for this person
        final PersonVO newPerson = new PersonVO();
        newPerson.setIdentifier(new IdentifierVO());
        final OrganizationVO newPersonOrganization = new OrganizationVO();
        newPersonOrganization.setName("");
        newPerson.getOrganizations().add(newPersonOrganization);
        newSourceCreator.setOrganization(null);
        newSourceCreator.setPerson(newPerson);
        source.getCreators().add(newSourceCreator);
      }
      if (source.getIdentifiers().isEmpty()) {
        source.getIdentifiers().add(new IdentifierVO());
      }
    }

    // Event
    // add Event if needed to be able to bind uiComponents to it
    if (newPubItem.getMetadata().getEvent() == null) {
      final EventVO eventVO = new EventVO();
      newPubItem.getMetadata().setEvent(eventVO);
    }
    if (newPubItem.getMetadata().getEvent().getTitle() == null) {
      newPubItem.getMetadata().getEvent().setTitle("");
    }
    if (newPubItem.getMetadata().getEvent().getPlace() == null) {
      newPubItem.getMetadata().getEvent().setPlace("");
    }

    // LegalCase
    // add LegalCase to be able to bind uiCompontents to it
    if (newPubItem.getMetadata().getLegalCase() == null) {
      final LegalCaseVO legalCaseVO = new LegalCaseVO();
      newPubItem.getMetadata().setLegalCase(legalCaseVO);
    }

    // add subject if needed to be able to bind uiComponents to it
    if (newPubItem.getMetadata().getFreeKeywords() == null) {
      newPubItem.getMetadata().setFreeKeywords("");
    }

    // add TOC if needed to be able to bind uiComponents to it
    if (newPubItem.getMetadata().getTableOfContents() == null) {
      newPubItem.getMetadata().setTableOfContents("");
    }

    if (newPubItem.getMetadata().getProjectInfo().isEmpty()) {
      ProjectInfoVO projectInfo = new ProjectInfoVO();
      newPubItem.getMetadata().getProjectInfo().add(projectInfo);
    }

    for (ProjectInfoVO projectInfo : newPubItem.getMetadata().getProjectInfo()) {
      if (projectInfo.getGrantIdentifier() == null) {
        projectInfo.setGrantIdentifier(new IdentifierVO(IdType.GRANT_ID, null));
      }

      if (projectInfo.getFundingInfo() == null) {
        projectInfo.setFundingInfo(new FundingInfoVO());
      }

      if (projectInfo.getFundingInfo().getFundingOrganization() == null) {
        projectInfo.getFundingInfo().setFundingOrganization(new FundingOrganizationVO());
      }

      if (projectInfo.getFundingInfo().getFundingOrganization().getIdentifiers().isEmpty()) {
        projectInfo.getFundingInfo().getFundingOrganization().getIdentifiers().add(new IdentifierVO(IdType.OPEN_AIRE, ""));
      }

      if (projectInfo.getFundingInfo().getFundingProgram() == null) {
        projectInfo.getFundingInfo().setFundingProgram(new FundingProgramVO());
      }

      if (projectInfo.getFundingInfo().getFundingProgram().getIdentifiers().isEmpty()) {
        projectInfo.getFundingInfo().getFundingProgram().getIdentifiers().add(new IdentifierVO(IdType.OPEN_AIRE, ""));
      }
    }

    return newPubItem;
  }

  /**
   * submits a pub item
   *
   * @param comment A comment
   * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when the
   *        operation is successful.
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String submitCurrentPubItem(String navigationRuleWhenSuccessfull, String comment) {
    try {
      final ItemVersionVO updatedPubItem = ApplicationBean.INSTANCE.getPubItemService().submitPubItem(this.currentPubItem.getObjectId(),
          this.currentPubItem.getModificationDate(), comment, this.getLoginHelper().getAuthenticationToken());

      this.setCurrentPubItem(new PubItemVOPresentation(updatedPubItem));
      return navigationRuleWhenSuccessfull;
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Error while submitting current PubItem", e);
      this.error(this.getMessage("ItemControllerSessionBean_errorSubmit") + e.getMessage());
    }

    return "";
  }

  /**
   * Returns all items for a user depending on the selected itemState.
   *
   * @param contextID the ID of the context that should be retrieved
   * @return the context with the given ID
   */
  public ContextDbVO retrieveContext(final String contextID) {
    ContextDbVO context = null;

    try {
      context = ApplicationBean.INSTANCE.getContextService().get(contextID, null);
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.debug(e.toString());
      this.getLoginHelper().logout();
    }

    return context;
  }

  /**
   * Returns the export data stream with the selected items in the proper export format
   *
   * @author: StG
   * @param exportFormatVO is containing the selected export format and the file format
   * @param itemsToExportList is the list of selected items to be exported
   * @return the export data stream as array of bytes
   */
  public byte[] retrieveExportData(final ExportFormatVO exportFormatVO, final List<ItemVersionVO> itemsToExportList)
      throws IngeTechnicalException {
    //    final List<ItemVersionVO> pubItemList = new ArrayList<ItemVersionVO>();
    //
    //    for (final ItemVersionVO pubItem : itemsToExportList) {
    //      pubItemList.add(new ItemVersionVO(pubItem));
    //    }

    return this.itemTransformingService.getOutputForExport(exportFormatVO, itemsToExportList);
  }

  /**
   * Returns a list of releases for a pubitem.
   *
   * @author Tobias Schraut
   * @param the item id for which releases should be fetched
   * @return the item with the requested id
   * @throws IngeApplicationException
   * @throws AuthorizationException
   * @throws AuthenticationException
   * @throws IngeTechnicalException
   * @throws Exception if framework access fails
   */
  public PubItemVOPresentation retrieveItem(String itemID)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    ItemVersionVO itemVersionVO = ApplicationBean.INSTANCE.getPubItemService().get(itemID, this.getLoginHelper().getAuthenticationToken());
    if (itemVersionVO != null) {
      return new PubItemVOPresentation(itemVersionVO);
    }

    return null;
  }

  //  /**
  //   * Returns all items in a list of item ids.
  //   *
  //   * @param itemRefs a list of item ids of items that should be retrieved.
  //   * @return all items for a user with the given ids
  //   * @throws Exception if framework access fails
  //   */
  //  private List<ItemVersionVO> retrieveItems(final List<ItemRO> itemRefs) throws Exception {
  //    if (itemRefs == null || itemRefs.isEmpty()) {
  //      return new ArrayList<ItemVersionVO>();
  //    }
  //
  //
  //    final BoolQueryBuilder bq = QueryBuilders.boolQuery();
  //
  //    for (final ItemRO id : itemRefs) {
  //      final BoolQueryBuilder subQuery = QueryBuilders.boolQuery();
  //      subQuery.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, id.getObjectId()));
  //      subQuery.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_VERSIONNUMBER, id.getVersionNumber()));
  //      bq.should(subQuery);
  //    }
  //
  //    final SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(bq);
  //
  //
  //    final SearchRetrieveResponseVO<ItemVersionVO> resp =
  //        ApplicationBean.INSTANCE.getPubItemService().search(srr, this.getLoginHelper().getAuthenticationToken());
  //
  //
  //    return resp.getRecords().stream().map(SearchRetrieveRecordVO::getData).collect(Collectors.toList());
  //  }



  /**
   * Returns an item by its id.
   *
   * @param the item id which belongs to the item
   * @return the item with the requested id
   * @throws Exception if framework access fails
   */
  public List<VersionHistoryEntryVO> retrieveVersionHistoryForItem(String itemID) throws Exception {

    final List<AuditDbVO> versionHistoryList =
        ApplicationBean.INSTANCE.getPubItemService().getVersionHistory(itemID, this.getLoginHelper().getAuthenticationToken());
    return EntityTransformer.transformToVersionHistory(versionHistoryList);
  }

  public String reviseCurrentPubItem(String navigationRuleWhenSuccesfull, String comment) {
    try {
      final ItemVersionVO updatedPubItem = ApplicationBean.INSTANCE.getPubItemService().revisePubItem(this.currentPubItem.getObjectId(),
          this.currentPubItem.getModificationDate(), comment, this.getLoginHelper().getAuthenticationToken());

      this.setCurrentPubItem(new PubItemVOPresentation(updatedPubItem));
      return navigationRuleWhenSuccesfull;
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Error while revising current PubItem", e);
      this.error(this.getMessage("ItemControllerSessionBean_errorRevise") + e.getMessage());
    }

    return "";
  }

  /**
   * Saves a PubItem and handles navigation afterwards.
   *
   * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when the
   *        operation is successful.
   * @return string, identifying the page that should be navigated to after this methodcall
   * @throws ValidationException
   */
  public String saveCurrentPubItem(String navigationRuleWhenSuccessfull) throws ValidationException {
    try {

      ItemVersionVO updatedPubItem = null;

      if (this.currentPubItem.getObjectId() == null) {
        updatedPubItem = ApplicationBean.INSTANCE.getPubItemService().create(new ItemVersionVO(this.currentPubItem),
            this.getLoginHelper().getAuthenticationToken());
      } else {
        updatedPubItem = ApplicationBean.INSTANCE.getPubItemService().update(new ItemVersionVO(this.currentPubItem),
            this.getLoginHelper().getAuthenticationToken());
      }

      this.setCurrentPubItem(new PubItemVOPresentation(updatedPubItem));
      this.info(this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SAVED));
      return navigationRuleWhenSuccessfull;
    } catch (final AuthenticationException | AuthorizationException e) {
      // TODO Auto-generated catch block
      ItemControllerSessionBean.logger.error("Authentication error while saving current PubItem", e);
      this.error(this.getMessage("ItemControllerSessionBean_noPermissionSave") + e.getMessage());
    } catch (final IngeTechnicalException e) {
      ItemControllerSessionBean.logger.error("Technical Error while saving current PubItem", e);
      this.error(this.getMessage("ItemControllerSessionBean_errorSave") + e.getMessage());
    } catch (final IngeApplicationException e) {
      if (e.getCause() instanceof ValidationException) {
        throw (ValidationException) e.getCause();
      } else {
        ItemControllerSessionBean.logger.error("Application Error while saving current PubItem", e);
        this.error(this.getMessage("ItemControllerSessionBean_applicationErrorSave") + e.getMessage());
      }

    }
    return "";
  }

  public void setCurrentCollection(ContextDbVO currentCollection) {
    this.currentContext = currentCollection;
  }

  public void setCurrentPubItem(PubItemVOPresentation currentPubItem) {
    this.currentPubItem = currentPubItem;
  }

  /**
   * Releases a PubItem and handles navigation afterwards.
   *
   * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when the
   *        operation is successful.
   * @param comment Optional comment.
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String releaseCurrentPubItem(String navigationRuleWhenSuccessfull, String comment) {
    try {
      final ItemVersionVO updatedPubItem = ApplicationBean.INSTANCE.getPubItemService().releasePubItem(this.currentPubItem.getObjectId(),
          this.currentPubItem.getModificationDate(), comment, this.getLoginHelper().getAuthenticationToken());

      this.setCurrentPubItem(new PubItemVOPresentation(updatedPubItem));
      return navigationRuleWhenSuccessfull;
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Error while releasing current PubItem", e);
      this.error(this.getMessage("ItemControllerSessionBean_errorRelease") + e.getMessage());
    }

    return "";
  }

  /**
   * Submits a PubItem and handles navigation afterwards.
   *
   * @param comment A comment
   * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when the
   *        operation is successful.
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String withdrawCurrentPubItem(String navigationRuleWhenSuccessfull, String comment) {
    try {
      final ItemVersionVO updatedPubItem = ApplicationBean.INSTANCE.getPubItemService().withdrawPubItem(this.currentPubItem.getObjectId(),
          this.currentPubItem.getModificationDate(), comment, this.getLoginHelper().getAuthenticationToken());

      this.setCurrentPubItem(new PubItemVOPresentation(updatedPubItem));
      return navigationRuleWhenSuccessfull;
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Error while withdrawing current PubItem", e);
      this.error(this.getMessage("ItemControllerSessionBean_errorWithdraw") + e.getMessage());
    }

    return "";
  }
}
