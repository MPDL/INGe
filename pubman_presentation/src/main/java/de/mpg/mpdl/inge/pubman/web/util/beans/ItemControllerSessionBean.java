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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO.Workflow;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO.State;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
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
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.mpdl.inge.model.xmltransforming.DataGatheringService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem.SubmissionMethod;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItem;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.RelationVOPresentation;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.impl.ItemTransformingServiceImpl;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.pubman.impl.SimpleStatisticsService;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.util.AdminHelper;
import de.mpg.mpdl.inge.util.PropertyReader;

@ManagedBean(name = "ItemControllerSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class ItemControllerSessionBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(ItemControllerSessionBean.class);

  private PubItemVOPresentation currentPubItem = null;
  private ContextDbVO currentContext = null;
  private final ItemTransformingService itemTransformingService = new ItemTransformingServiceImpl();

  public ItemControllerSessionBean() {}

  /**
   * Accepts an item.
   * 
   * @param pubItem the item that should be accepted
   * @return string, identifying the page that should be navigated to after this method call
   * @throws Exception if framework access fails
   */
  public String acceptCurrentPubItem(String navigationRuleWhenSuccessfull, String comment) {
    try {
      final ItemVersionVO updatedPubItem =
          ApplicationBean.INSTANCE.getPubItemService().releasePubItem(this.currentPubItem.getObjectId(),
              this.currentPubItem.getModificationDate(), comment, this.getLoginHelper().getAuthenticationToken());

      this.setCurrentPubItem(new PubItemVOPresentation(updatedPubItem));
      return navigationRuleWhenSuccessfull;
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Error while accepting current PubItem", e);
      this.error("Error while accepting current PubItem" + e.getMessage());
    }

    return "";
  }

  /**
   * Redirects the user to the create new revision page Changed by DiT, 29.11.2007: only show
   * contexts when user has privileges for more than one context
   * 
   * @return Sring nav rule to load the create new revision page
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
    newItem.getObject().setCreator(null);
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
    newPubItem.setMetadata(new MdsPublicationVO());
    newPubItem = this.initializeItem(newPubItem);
    this.setCurrentPubItem(new PubItemVOPresentation(newPubItem));

    return navigationRuleWhenSuccessful;
  }

  /**
   * Creates a new Revision of a PubItem and handles navigation afterwards.
   * 
   * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when the
   *        operation is successfull
   * @param pubContextRO the context for the new revision
   * @param pubItem the base item for the new revision
   * @param comment the description for the new revision
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String createNewRevision(String navigationRuleWhenSuccessfull, final ContextRO pubContextRO, final ItemVersionVO pubItem,
      String comment) {
    final ItemVersionVO newRevision =
        PubItemUtil.createRevisionOfPubItem(pubItem, comment, pubContextRO, this.getLoginHelper().getAccountUser());

    // setting the returned item as new currentItem
    this.setCurrentPubItem(new PubItemVOPresentation(this.initializeItem(newRevision)));

    return navigationRuleWhenSuccessfull;
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
      this.error("Authentication/Authorization error while deleting current PubItem: " + e.getMessage());
    } catch (final IngeTechnicalException e) {
      ItemControllerSessionBean.logger.error("Technical Error while deleting current PubItem", e);
      this.error("Technical error while deleting current PubItem");
    } catch (final IngeApplicationException e) {
      ItemControllerSessionBean.logger.error("Application error while deleting current PubItem", e);
      this.error("Application error while deleting current PubItem: " + e.getMessage());
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

  public Workflow getCurrentWorkflow() {
    final Workflow workflow = this.getCurrentContext().getWorkflow();
    if (workflow == null || workflow == Workflow.SIMPLE) {
      return Workflow.SIMPLE;
    } else if (workflow == Workflow.STANDARD) {
      return Workflow.STANDARD;
    }

    return Workflow.SIMPLE;
  }

  private EditItemSessionBean getEditItemSessionBean() {
    return (EditItemSessionBean) FacesTools.findBean("EditItemSessionBean");
  }

  public String getStatisticValue(String reportDefinitionType) throws Exception {
    return SimpleStatisticsService.getNumberOfItemOrFileRequests(reportDefinitionType, this.currentPubItem.getObjectId(),
        this.getLoginHelper().getAccountUser());
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
   * Initializes a new item with ValueObjects. FrM: Changes to be able to initialize an item created
   * as a new revision of an existing item.
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
      newPersonOrganization.setIdentifier(PropertyReader.getProperty("inge.pubman.external.organisation.id"));
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

    if (newPubItem.getMetadata().getProjectInfo() == null) {
      final ProjectInfoVO projectInfo = new ProjectInfoVO();
      newPubItem.getMetadata().setProjectInfo(projectInfo);
    }

    if (newPubItem.getMetadata().getProjectInfo().getGrantIdentifier() == null) {
      newPubItem.getMetadata().getProjectInfo().setGrantIdentifier(new IdentifierVO(IdType.GRANT_ID, null));
    }

    if (newPubItem.getMetadata().getProjectInfo().getFundingInfo() == null) {
      newPubItem.getMetadata().getProjectInfo().setFundingInfo(new FundingInfoVO());
    }

    if (newPubItem.getMetadata().getProjectInfo().getFundingInfo().getFundingOrganization() == null) {
      newPubItem.getMetadata().getProjectInfo().getFundingInfo().setFundingOrganization(new FundingOrganizationVO());
    }

    if (newPubItem.getMetadata().getProjectInfo().getFundingInfo().getFundingOrganization().getIdentifiers().isEmpty()) {
      newPubItem.getMetadata().getProjectInfo().getFundingInfo().getFundingOrganization().getIdentifiers()
          .add(new IdentifierVO(IdType.OPEN_AIRE, ""));
    }

    if (newPubItem.getMetadata().getProjectInfo().getFundingInfo().getFundingProgram() == null) {
      newPubItem.getMetadata().getProjectInfo().getFundingInfo().setFundingProgram(new FundingProgramVO());
    }

    if (newPubItem.getMetadata().getProjectInfo().getFundingInfo().getFundingProgram().getIdentifiers().isEmpty()) {
      newPubItem.getMetadata().getProjectInfo().getFundingInfo().getFundingProgram().getIdentifiers()
          .add(new IdentifierVO(IdType.OPEN_AIRE, ""));
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
      final ItemVersionVO updatedPubItem =
          ApplicationBean.INSTANCE.getPubItemService().submitPubItem(this.currentPubItem.getObjectId(),
              this.currentPubItem.getModificationDate(), comment, this.getLoginHelper().getAuthenticationToken());

      this.setCurrentPubItem(new PubItemVOPresentation(updatedPubItem));
      return navigationRuleWhenSuccessfull;
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Error while submitting current PubItem", e);
      this.error("Error while submitting current PubItem" + e.getMessage());
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
  public byte[] retrieveExportData(final ExportFormatVO exportFormatVO, final List<ItemVersionVO> itemsToExportList) throws TechnicalException {
    final List<ItemVersionVO> pubItemList = new ArrayList<ItemVersionVO>();

    for (final ItemVersionVO pubItem : itemsToExportList) {
      pubItemList.add(new ItemVersionVO(pubItem));
    }

    return this.itemTransformingService.getOutputForExport(exportFormatVO, pubItemList);
  }

  /**
   * Returns a list of releases for a pubitem.
   * 
   * @author Tobias Schraut
   * @param the item id for which releases should be fetched
   * @return the item with the requested id
   * @throws Exception if framework access fails
   */
  public PubItemVOPresentation retrieveItem(String itemID) throws Exception {
    return new PubItemVOPresentation(
        ApplicationBean.INSTANCE.getPubItemService().get(itemID, this.getLoginHelper().getAuthenticationToken()));
  }

  /**
   * Returns all items in a list of item ids.
   * 
   * @param itemRefs a list of item ids of items that should be retrieved.
   * @return all items for a user with the given ids
   * @throws Exception if framework access fails
   */
  private List<ItemVersionVO> retrieveItems(final List<ItemRO> itemRefs) throws Exception {
    if (itemRefs == null || itemRefs.isEmpty()) {
      return new ArrayList<ItemVersionVO>();
    }


    final BoolQueryBuilder bq = QueryBuilders.boolQuery();

    for (final ItemRO id : itemRefs) {
      final BoolQueryBuilder subQuery = QueryBuilders.boolQuery();
      subQuery.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, id.getObjectId()));
      subQuery.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_VERSIONNUMBER, id.getVersionNumber()));
      bq.should(subQuery);
    }

    final SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(bq);


    final SearchRetrieveResponseVO<ItemVersionVO> resp =
        ApplicationBean.INSTANCE.getPubItemService().search(srr, this.getLoginHelper().getAuthenticationToken());


    return resp.getRecords().stream().map(SearchRetrieveRecordVO::getData).collect(Collectors.toList());
  }

  /**
   * @author Markus Haarlaender
   * @param pubItemVO the pubitem (a revision) for which the parent items should be fetched
   * @return a list of wrapped ReleationVOs that contain information about the items from which this
   *         revision was created
   */
  /*
  public List<RelationVOPresentation> retrieveParentsForRevision(ItemVersionVO pubItemVO) throws Exception {
    List<RelationVOPresentation> revisionVOList = new ArrayList<RelationVOPresentation>();

    if (this.getLoginHelper().getESciDocUserHandle() != null) {
      revisionVOList = CommonUtils.convertToRelationVOPresentationList(
          DataGatheringService.findParentItemsOfRevision(this.getLoginHelper().getESciDocUserHandle(), pubItemVO.getVersion()));
    } else {
      final String adminHandle = AdminHelper.getAdminUserHandle();
      // TODO ScT: retrieve as super user (workaround for not logged in users until the framework
      // changes this retrieve method for unauthorized users)
      revisionVOList = CommonUtils
          .convertToRelationVOPresentationList(DataGatheringService.findParentItemsOfRevision(adminHandle, pubItemVO.getVersion()));
    }

    final List<ItemRO> targetItemRefs = new ArrayList<ItemRO>();
    for (final RelationVOPresentation relationVOPresentation : revisionVOList) {
      targetItemRefs.add(relationVOPresentation.getTargetItemRef());
    }

    final List<ItemVersionVO> targetItemList = this.retrieveItems(targetItemRefs);
    for (final RelationVOPresentation revision : revisionVOList) {
      for (final ItemVersionVO pubItem : targetItemList) {
        if (revision.getTargetItemRef().getObjectId().equals(pubItem.getObjectId())) {
          revision.setTargetItem(pubItem);
          break;
        }
      }
    }

    return revisionVOList;
  }
  */

  /**
   * @author Tobias Schraut
   * @param pubItemVO the pubitem for which the revisions should be fetched
   * @return a list of wrapped released ReleationVOs
   */
  /*
  public List<RelationVOPresentation> retrieveRevisions(ItemVersionVO pubItemVO) throws Exception {
    List<RelationVOPresentation> revisionVOList = new ArrayList<RelationVOPresentation>();

    if (this.getLoginHelper().getESciDocUserHandle() != null) {
      revisionVOList = CommonUtils.convertToRelationVOPresentationList(
          DataGatheringService.findRevisionsOfItem(this.getLoginHelper().getESciDocUserHandle(), pubItemVO.getVersion()));
    } else {
      // TODO ScT: retrieve as super user (workaround for not logged in users until the framework
      // changes this retrieve method for unauthorized users)
      revisionVOList = CommonUtils.convertToRelationVOPresentationList(
          DataGatheringService.findRevisionsOfItem(AdminHelper.getAdminUserHandle(), pubItemVO.getVersion()));
    }

    final List<ItemRO> sourceItemRefs = new ArrayList<ItemRO>();
    for (final RelationVOPresentation relationVOPresentation : revisionVOList) {

      sourceItemRefs.add(relationVOPresentation.getSourceItemRef());
    }

    final List<ItemVersionVO> sourceItemList = this.retrieveItems(sourceItemRefs);
    for (final RelationVOPresentation revision : revisionVOList) {
      for (final ItemVersionVO pubItem : sourceItemList) {
        if (revision.getSourceItemRef().getObjectId().equals(pubItem.getObjectId())) {
          revision.setSourceItem(pubItem);
          break;
        }
      }
    }

    return revisionVOList;
  }
  */

  /**
   * Returns an item by its id.
   * 
   * @param the item id which belongs to the item
   * @return the item with the requested id
   * @throws Exception if framework access fails
   */
  public List<VersionHistoryEntryVO> retrieveVersionHistoryForItem(String itemID) throws Exception {

    final List<VersionHistoryEntryVO> versionHistoryList =
        ApplicationBean.INSTANCE.getPubItemService().getVersionHistory(itemID, this.getLoginHelper().getAuthenticationToken());
    return versionHistoryList;
  }

  public String reviseCurrentPubItem(String navigationRuleWhenSuccesfull, String comment) {
    try {
      final ItemVersionVO updatedPubItem =
          ApplicationBean.INSTANCE.getPubItemService().revisePubItem(this.currentPubItem.getObjectId(),
              this.currentPubItem.getModificationDate(), comment, this.getLoginHelper().getAuthenticationToken());

      this.setCurrentPubItem(new PubItemVOPresentation(updatedPubItem));
      return navigationRuleWhenSuccesfull;
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Error while revising current PubItem", e);
      this.error("Error while revising current PubItem" + e.getMessage());
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
      this.error("Authentication error while saving current PubItem: " + e.getMessage());
    } catch (final IngeTechnicalException e) {
      ItemControllerSessionBean.logger.error("Technical Error while saving current PubItem", e);
      this.error("Technical error while saving current PubItem");
    } catch (final IngeApplicationException e) {
      if (e.getCause() instanceof ValidationException) {
        throw (ValidationException) e.getCause();
      } else {
        ItemControllerSessionBean.logger.error("Application Error while saving current PubItem", e);
        this.error("Application error while saving current PubItem: " + e.getMessage());
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
      final ItemVersionVO updatedPubItem =
          ApplicationBean.INSTANCE.getPubItemService().releasePubItem(this.currentPubItem.getObjectId(),
              this.currentPubItem.getModificationDate(), comment, this.getLoginHelper().getAuthenticationToken());

      this.setCurrentPubItem(new PubItemVOPresentation(updatedPubItem));
      return navigationRuleWhenSuccessfull;
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Error while releasing current PubItem", e);
      this.error("Error while releasing current PubItem" + e.getMessage());
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
      final ItemVersionVO updatedPubItem =
          ApplicationBean.INSTANCE.getPubItemService().withdrawPubItem(this.currentPubItem.getObjectId(),
              this.currentPubItem.getModificationDate(), comment, this.getLoginHelper().getAuthenticationToken());

      this.setCurrentPubItem(new PubItemVOPresentation(updatedPubItem));
      return navigationRuleWhenSuccessfull;
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Error while withdrawing current PubItem", e);
      this.error("Error while withdrawing current PubItem");
    }

    return "";
  }
}
