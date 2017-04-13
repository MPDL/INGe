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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
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
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.mpdl.inge.model.xmltransforming.DataGatheringService;
import de.mpg.mpdl.inge.model.xmltransforming.EmailService;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.ItemExportingService;
import de.mpg.mpdl.inge.pubman.PubItemService;
import de.mpg.mpdl.inge.pubman.SimpleStatisticsService;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem.SubmissionMethod;
import de.mpg.mpdl.inge.pubman.web.desktop.Login;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItem;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.RelationVOPresentation;
import de.mpg.mpdl.inge.services.UserInterfaceConnectorFactory;
import de.mpg.mpdl.inge.util.AdminHelper;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Handles all actions on/with items, calls to the framework.
 * 
 * @author: Thomas Diebäcker, created 25.04.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT: 14.08.2007
 */
@ManagedBean(name = "ItemControllerSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class ItemControllerSessionBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(ItemControllerSessionBean.class);

  private PubItemVOPresentation currentPubItem = null;
  private ContextVO currentContext = null;

  public ItemControllerSessionBean() {}


  /**
   * Accepts an item.
   * 
   * @param pubItem the item that should be accepted
   * @return string, identifying the page that should be navigated to after this method call
   * @throws Exception if framework access fails
   */
  public String acceptCurrentPubItem(String comment, String navigationRuleWhenSuccessfull) {
    try {
      if (this.currentPubItem == null) {
        final TechnicalException technicalException =
            new TechnicalException("No current PubItem is set.");
        throw technicalException;
      }

      final PubItemVO pubItem = new PubItemVO(this.currentPubItem);
      this.cleanUpItem(pubItem);

      final PubItemVO acceptedPubItem =
          PubItemService.releasePubItem(pubItem.getVersion(), pubItem.getModificationDate(),
              comment, this.getLoginHelper().getAccountUser());

      final ItemRO pubItemRO = acceptedPubItem.getVersion();

      if (pubItemRO == this.currentPubItem.getVersion()) {
        return null;
      }
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Could not accept item." + "\n" + e.toString(), e);
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);

      return ErrorPage.LOAD_ERRORPAGE;
    }

    return navigationRuleWhenSuccessfull;
  }

  /**
   * Cleans up the ValueObject for saving/submitting from unused sub-VOs.
   * 
   * @param pubItem the PubItem to clean up
   */
  public void cleanUpItem(final PubItemVO pubItem) {
    try {
      pubItem.getMetadata().cleanup();

      // delete unfilled file
      if (pubItem.getFiles() != null) {
        for (int i = (pubItem.getFiles().size() - 1); i >= 0; i--) {
          // Cleanup MD
          pubItem.getFiles().get(i).getDefaultMetadata().cleanup();
          if ((pubItem.getFiles().get(i).getName() == null || pubItem.getFiles().get(i).getName()
              .length() == 0)
              && (pubItem.getFiles().get(i).getContent() == null || pubItem.getFiles().get(i)
                  .getContent().length() == 0)) {
            pubItem.getFiles().remove(i);
          }
        }
      }
    } catch (final Exception e1) {
      throw new RuntimeException("Error while cleaning up  item", e1);
    }

    // TODO MF: Check specification for this behaviour: Always when an organization does not have an
    // identifier, make it "external".
    // assign the external org id to default organisation
    try {
      for (final CreatorVO creator : pubItem.getMetadata().getCreators()) {
        if (creator.getPerson() != null) {
          for (final OrganizationVO organization : creator.getPerson().getOrganizations()) {
            if (organization.getIdentifier() == null || organization.getIdentifier().equals("")) {
              organization.setIdentifier(PropertyReader
                  .getProperty("escidoc.pubman.external.organisation.id"));
            }
          }
        } else {
          if (creator.getOrganization() != null
              && (creator.getOrganization().getIdentifier() == null || creator.getOrganization()
                  .getIdentifier().equals(""))) {
            creator.getOrganization().setIdentifier(
                PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
          }
        }
      }

      if (pubItem.getMetadata().getSources() != null) {
        for (final SourceVO source : pubItem.getMetadata().getSources()) {
          for (final CreatorVO creator : source.getCreators()) {
            if (creator.getPerson() != null) {
              for (final OrganizationVO organization : creator.getPerson().getOrganizations()) {
                if (organization.getIdentifier() == null || organization.getIdentifier().equals("")) {
                  organization.setIdentifier(PropertyReader
                      .getProperty("escidoc.pubman.external.organisation.id"));
                }
              }
            } else {
              if (creator.getOrganization() != null
                  && (creator.getOrganization().getIdentifier() == null || creator
                      .getOrganization().getIdentifier().equals(""))) {
                creator.getOrganization().setIdentifier(
                    PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
              }
            }
          }
        }
      }

      // remove empty tags
      if (pubItem.getLocalTags() != null) {
        final List<String> emptyTags = new ArrayList<String>();
        for (final String tag : pubItem.getLocalTags()) {
          if (tag == null || "".equals(tag)) {
            emptyTags.add(tag);
          }
        }
        for (final String tag : emptyTags) {
          pubItem.getLocalTags().remove(tag);
        }
      }

    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Error getting external org id", e);
    }
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
      FacesBean.error(this.getMessage("ViewItemFull_user_has_no_context"));
      return null;
    }

    final PubItemVO newItem = new PubItemVO(this.currentPubItem);
    newItem.getVersion().setObjectId(null);
    newItem.setPid(null);
    newItem.getVersion().setVersionNumber(0);
    newItem.getVersion().setState(State.PENDING);
    newItem.getVersion().setPid(null);
    newItem.setPublicStatus(State.PENDING);
    newItem.setOwner(null);
    newItem.getFiles().clear();
    // clear local tags [PUBMAN-2478]
    newItem.getLocalTags().clear();
    // clear the relation list according to PUBMAN-357
    if (newItem.getRelations() != null) {
      newItem.getRelations().clear();
    }

    this.setCurrentPubItem(new PubItemVOPresentation(newItem));

    if (this.getContextListSessionBean().getDepositorContextList().size() == 1) {
      final ContextVO context = this.getContextListSessionBean().getDepositorContextList().get(0);
      newItem.setContext(context.getReference());

      this.setCurrentPubItem(new PubItemVOPresentation(newItem));

      editItemSessionBean.initEmptyComponents();
      return EditItem.LOAD_EDITITEM;
    } else {
      // more than one context exists for this user; let him choose the right one
      newItem.setContext(null);

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
  public String createNewPubItem(String navigationRuleWhenSuccessful, final ContextRO pubContextRO) {
    try {
      final PubItemVO newPubItem =
          PubItemService.INSTANCE.createPubItem(pubContextRO, this.getLoginHelper()
              .getAccountUser());
      this.setCurrentPubItem(new PubItemVOPresentation(this.initializeItem(newPubItem)));
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Could not create item." + "\n" + e.toString(), e);
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);

      return ErrorPage.LOAD_ERRORPAGE;
    }

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
  public String createNewRevision(String navigationRuleWhenSuccessfull,
      final ContextRO pubContextRO, final PubItemVO pubItem, String comment) {

    try {
      final PubItemVO newRevision =
          PubItemService.createRevisionOfPubItem(pubItem, comment, pubContextRO, this
              .getLoginHelper().getAccountUser());

      // setting the returned item as new currentItem
      this.setCurrentPubItem(new PubItemVOPresentation(this.initializeItem(newRevision)));
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Could not create revision." + "\n" + e.toString());
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);

      return ErrorPage.LOAD_ERRORPAGE;
    }

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
      if (this.currentPubItem == null) {
        final TechnicalException technicalException =
            new TechnicalException("No current PubItem is set.");
        throw technicalException;
      }

      if (this.currentPubItem.getVersion() == null) {
        // if the item has not been saved before, there is no need to delete it
        ItemControllerSessionBean.logger
            .warn("Tried to delete an unsaved item. Do nothing instead.");
      } else {
        PubItemService.INSTANCE.deletePubItem(this.currentPubItem.getVersion(), this
            .getLoginHelper().getAccountUser());
      }
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Could not delete item." + "\n" + e.toString());
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);

      return ErrorPage.LOAD_ERRORPAGE;
    }

    return navigationRuleWhenSuccessfull;
  }

  private ContextListSessionBean getContextListSessionBean() {
    return (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
  }

  public ContextVO getCurrentContext() {
    // retrieve current context newly if the current item has changed or if the context has not been
    // retrieved so far
    if (this.currentPubItem != null) {
      if (this.currentContext == null
          || !(this.currentContext.getReference().getObjectId().equals(this.currentPubItem
              .getContext().getObjectId()))) {
        final ContextVO context =
            this.retrieveContext(this.currentPubItem.getContext().getObjectId());
        this.setCurrentCollection(context);
      }
    }

    return this.currentContext;
  }

  public PubItemVOPresentation getCurrentPubItem() {
    return this.currentPubItem;
  }

  public String getCurrentWorkflow() {
    final PublicationAdminDescriptorVO.Workflow workflow =
        this.getCurrentContext().getAdminDescriptor().getWorkflow();
    if (workflow == null || workflow == PublicationAdminDescriptorVO.Workflow.SIMPLE) {
      return PubItemService.WORKFLOW_SIMPLE;
    } else if (workflow == PublicationAdminDescriptorVO.Workflow.STANDARD) {
      return PubItemService.WORKFLOW_STANDARD;
    }

    return PubItemService.WORKFLOW_SIMPLE;
  }

  private EditItemSessionBean getEditItemSessionBean() {
    return (EditItemSessionBean) FacesTools.findBean("EditItemSessionBean");
  }

  public String getStatisticValue(String reportDefinitionType) throws Exception {
    return SimpleStatisticsService.getNumberOfItemOrFileRequests(reportDefinitionType,
        this.currentPubItem.getVersion().getObjectId(), this.getLoginHelper().getAccountUser());
  }

  /**
   * Tests if the metadata of the two items have changed.
   * 
   * @param oldPubItem the old pubItem
   * @param newPubItem the new (maybe changed) pubItem
   * @return true if the metadata of the new item has changed
   */
  public boolean hasChanged(PubItemVO oldPubItem, PubItemVO newPubItem) {
    // clone both items
    final PubItemVO oldPubItemClone = (PubItemVO) oldPubItem.clone();
    final PubItemVO newPubItemClone = (PubItemVO) newPubItem.clone();

    // clean both items up from unused sub-VOs
    this.cleanUpItem(oldPubItemClone);
    this.cleanUpItem(newPubItemClone);

    // compare the metadata and files of the two items
    final boolean metadataChanged =
        !(oldPubItemClone.getMetadata().equals(newPubItemClone.getMetadata()));
    final boolean fileChanged = !(oldPubItemClone.getFiles().equals(newPubItemClone.getFiles()));
    final boolean localTagsChanged =
        !(oldPubItemClone.getLocalTags().equals(newPubItemClone.getLocalTags()));

    return (metadataChanged || fileChanged || localTagsChanged);
  }

  /**
   * Initializes a new item with ValueObjects. FrM: Changes to be able to initialize an item created
   * as a new revision of an existing item.
   * 
   * @return the initialized item.
   */
  public PubItemVO initializeItem(PubItemVO newPubItem) throws Exception {
    // version
    if (newPubItem.getVersion() == null) {
      final ItemRO version = new ItemRO();
      newPubItem.setVersion(version);
    }

    // Status
    if (newPubItem.getVersion().getState() == null) {
      newPubItem.getVersion().setState(State.PENDING);
    }

    // Title
    if (newPubItem.getMetadata().getTitle() == null) {
      newPubItem.getMetadata().setTitle("");
    }

    // Genre
    if (newPubItem.getMetadata().getGenre() == null) {
      final ContextVO contextVO = this.retrieveContext(newPubItem.getContext().getObjectId());
      final PublicationAdminDescriptorVO adminDescriptorVO = contextVO.getAdminDescriptor();
      if (adminDescriptorVO.getAllowedGenres().contains(Genre.ARTICLE)) {
        newPubItem.getMetadata().setGenre(Genre.ARTICLE);
      } else if (!adminDescriptorVO.getAllowedGenres().isEmpty()) {
        newPubItem.getMetadata().setGenre(adminDescriptorVO.getAllowedGenres().get(0));
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
      newPersonOrganization.setIdentifier(PropertyReader
          .getProperty("escidoc.pubman.external.organisation.id"));
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
      newPubItem.getMetadata().getProjectInfo()
          .setGrantIdentifier(new IdentifierVO(IdType.GRANT_ID, null));
    }

    if (newPubItem.getMetadata().getProjectInfo().getFundingInfo() == null) {
      newPubItem.getMetadata().getProjectInfo().setFundingInfo(new FundingInfoVO());
    }

    if (newPubItem.getMetadata().getProjectInfo().getFundingInfo().getFundingOrganization() == null) {
      newPubItem.getMetadata().getProjectInfo().getFundingInfo()
          .setFundingOrganization(new FundingOrganizationVO());
    }

    if (newPubItem.getMetadata().getProjectInfo().getFundingInfo().getFundingOrganization()
        .getIdentifiers().isEmpty()) {
      newPubItem.getMetadata().getProjectInfo().getFundingInfo().getFundingOrganization()
          .getIdentifiers().add(new IdentifierVO(IdType.OPEN_AIRE, ""));
    }

    if (newPubItem.getMetadata().getProjectInfo().getFundingInfo().getFundingProgram() == null) {
      newPubItem.getMetadata().getProjectInfo().getFundingInfo()
          .setFundingProgram(new FundingProgramVO());
    }

    if (newPubItem.getMetadata().getProjectInfo().getFundingInfo().getFundingProgram()
        .getIdentifiers().isEmpty()) {
      newPubItem.getMetadata().getProjectInfo().getFundingInfo().getFundingProgram()
          .getIdentifiers().add(new IdentifierVO(IdType.OPEN_AIRE, ""));
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
  public String submitCurrentPubItem(String comment, String navigationRuleWhenSuccessfull) {
    try {
      if (this.currentPubItem == null) {
        final TechnicalException technicalException =
            new TechnicalException("No current PubItem is set.");
        throw technicalException;
      }

      final PubItemVO pubItem = new PubItemVO(this.currentPubItem);
      this.cleanUpItem(pubItem);

      PubItemService.INSTANCE.submitPubItem(pubItem, comment, this.getLoginHelper()
          .getAccountUser());
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Could not submit item." + "\n" + e.toString());
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);

      return ErrorPage.LOAD_ERRORPAGE;
    }

    return navigationRuleWhenSuccessfull;
  }

  /**
   * Returns all items for a user depending on the selected itemState.
   * 
   * @param contextID the ID of the context that should be retrieved
   * @return the context with the given ID
   */
  public ContextVO retrieveContext(final String contextID) {
    ContextVO context = null;

    try {
      context = ApplicationBean.INSTANCE.getContextService().get(contextID, null);
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.debug(e.toString());
      ((Login) FacesTools.findBean("Login")).forceLogout();
    }

    return context;
  }

  /**
   * Returns the export data stream with the selelcted items in the proper export format
   * 
   * @author: StG
   * @param exportFormatVO is containing the selected export format and the file format
   * @param itemsToExportList is the list of selected items to be exported
   * @return the export data stream as array of bytes
   */
  public byte[] retrieveExportData(final ExportFormatVO exportFormatVO,
      final List<PubItemVO> itemsToExportList) throws TechnicalException {
    final List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();

    for (final PubItemVO pubItem : itemsToExportList) {
      pubItemList.add(new PubItemVO(pubItem));
    }

    return ItemExportingService.getOutput(exportFormatVO, pubItemList);
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
    return new PubItemVOPresentation(ApplicationBean.INSTANCE.getPubItemService().get(itemID, null));
  }

  /**
   * Returns all items in a list of item ids.
   * 
   * @param itemRefs a list of item ids of items that should be retrieved.
   * @return all items for a user with the given ids
   * @throws Exception if framework access fails
   */
  private ArrayList<PubItemVO> retrieveItems(final List<ItemRO> itemRefs) throws Exception {
    if (itemRefs == null || itemRefs.isEmpty()) {
      return new ArrayList<PubItemVO>();
    }

    // define the filter criteria
    final FilterTaskParamVO filter = new FilterTaskParamVO();
    final FilterTaskParamVO.ItemRefFilter f1 = filter.new ItemRefFilter(itemRefs);
    filter.getFilterList().add(f1);

    // retrieve the items applying the filter criteria
    String xmlItemList = "";
    try {
      if (this.getLoginHelper().getESciDocUserHandle() != null) {
        xmlItemList =
            ServiceLocator.getItemHandler(this.getLoginHelper().getESciDocUserHandle())
                .retrieveItems(filter.toMap());
      } else {
        xmlItemList = ServiceLocator.getItemHandler().retrieveItems(filter.toMap());
      }
    } catch (final AuthenticationException e) {
      ItemControllerSessionBean.logger.debug(e.toString());
      ((Login) FacesTools.findBean("Login")).forceLogout();
      throw e;
    }

    // transform the itemList
    final ArrayList<PubItemVO> itemList =
        (ArrayList<PubItemVO>) XmlTransformingService.transformSearchRetrieveResponseToItemList(
            xmlItemList).getItemVOList();

    return itemList;
  }

  /**
   * @author Markus Haarlaender
   * @param pubItemVO the pubitem (a revision) for which the parent items should be fetched
   * @return a list of wrapped ReleationVOs that contain information about the items from which this
   *         revision was created
   */
  public List<RelationVOPresentation> retrieveParentsForRevision(PubItemVO pubItemVO)
      throws Exception {
    List<RelationVOPresentation> revisionVOList = new ArrayList<RelationVOPresentation>();

    if (this.getLoginHelper().getESciDocUserHandle() != null) {
      revisionVOList =
          CommonUtils.convertToRelationVOPresentationList(DataGatheringService
              .findParentItemsOfRevision(this.getLoginHelper().getESciDocUserHandle(),
                  pubItemVO.getVersion()));
    } else {
      final String adminHandle = AdminHelper.getAdminUserHandle();
      // TODO ScT: retrieve as super user (workaround for not logged in users until the framework
      // changes this retrieve method for unauthorized users)
      revisionVOList =
          CommonUtils.convertToRelationVOPresentationList(DataGatheringService
              .findParentItemsOfRevision(adminHandle, pubItemVO.getVersion()));
    }

    final List<ItemRO> targetItemRefs = new ArrayList<ItemRO>();
    for (final RelationVOPresentation relationVOPresentation : revisionVOList) {
      targetItemRefs.add(relationVOPresentation.getTargetItemRef());
    }

    final List<PubItemVO> targetItemList = this.retrieveItems(targetItemRefs);
    for (final RelationVOPresentation revision : revisionVOList) {
      for (final PubItemVO pubItem : targetItemList) {
        if (revision.getTargetItemRef().getObjectId().equals(pubItem.getVersion().getObjectId())) {
          revision.setTargetItem(pubItem);
          break;
        }
      }
    }

    return revisionVOList;
  }

  /**
   * @author Tobias Schraut
   * @param pubItemVO the pubitem for which the revisions should be fetched
   * @return a list of wrapped released ReleationVOs
   */
  public List<RelationVOPresentation> retrieveRevisions(PubItemVO pubItemVO) throws Exception {
    List<RelationVOPresentation> revisionVOList = new ArrayList<RelationVOPresentation>();

    if (this.getLoginHelper().getESciDocUserHandle() != null) {
      revisionVOList =
          CommonUtils.convertToRelationVOPresentationList(DataGatheringService.findRevisionsOfItem(
              this.getLoginHelper().getESciDocUserHandle(), pubItemVO.getVersion()));
    } else {
      // TODO ScT: retrieve as super user (workaround for not logged in users until the framework
      // changes this retrieve method for unauthorized users)
      revisionVOList =
          CommonUtils.convertToRelationVOPresentationList(DataGatheringService.findRevisionsOfItem(
              AdminHelper.getAdminUserHandle(), pubItemVO.getVersion()));
    }

    final List<ItemRO> sourceItemRefs = new ArrayList<ItemRO>();
    for (final RelationVOPresentation relationVOPresentation : revisionVOList) {

      sourceItemRefs.add(relationVOPresentation.getSourceItemRef());
    }

    final List<PubItemVO> sourceItemList = this.retrieveItems(sourceItemRefs);
    for (final RelationVOPresentation revision : revisionVOList) {
      for (final PubItemVO pubItem : sourceItemList) {
        if (revision.getSourceItemRef().getObjectId().equals(pubItem.getVersion().getObjectId())) {
          revision.setSourceItem(pubItem);
          break;
        }
      }
    }

    return revisionVOList;
  }

  /**
   * Return the value object of the owner of the item.
   */
  public AccountUserVO retrieveUserAccount(String userId) {
    AccountUserVO userAccount = null;
    try {
      userAccount = UserInterfaceConnectorFactory.getInstance().readUser(userId);
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Error retrieving user account", e);
      ItemControllerSessionBean.logger.error("Returning null");
    }

    return userAccount;
  }

  /**
   * Returns an item by its id.
   * 
   * @param the item id which belongs to the item
   * @return the item with the requested id
   * @throws Exception if framework access fails
   */
  public List<VersionHistoryEntryVO> retrieveVersionHistoryForItem(String itemID) throws Exception {
    List<VersionHistoryEntryVO> versionHistoryList = new ArrayList<VersionHistoryEntryVO>();
    String xmlVersionHistoryList = "";

    // login with escidoc user handle
    if (this.getLoginHelper().getESciDocUserHandle() != null) {
      try {
        xmlVersionHistoryList =
            ServiceLocator.getItemHandler(this.getLoginHelper().getESciDocUserHandle())
                .retrieveVersionHistory(itemID);
      } catch (final AuthenticationException e) {
        ItemControllerSessionBean.logger.debug(e.toString());
        ((Login) FacesTools.findBean("Login")).forceLogout();
        throw e;
      }
    }
    // anonymous login
    else {
      try {
        xmlVersionHistoryList = ServiceLocator.getItemHandler().retrieveVersionHistory(itemID);
      } catch (final AuthenticationException e) {
        ItemControllerSessionBean.logger.debug(e.toString());
        ((Login) FacesTools.findBean("Login")).forceLogout();
        throw e;
      }
    }

    versionHistoryList = XmlTransformingService.transformToEventVOList(xmlVersionHistoryList);

    return versionHistoryList;
  }

  public String reviseCurrentPubItem(String reviseComment, String navigationStringToGoBack) {
    try {
      if (this.currentPubItem == null) {
        final TechnicalException technicalException =
            new TechnicalException("No current PubItem is set.");
        throw technicalException;
      }

      PubItemService.revisePubItem(this.currentPubItem.getVersion(), reviseComment, this
          .getLoginHelper().getAccountUser());
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Could not revise item." + "\n" + e.toString());
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);

      return ErrorPage.LOAD_ERRORPAGE;
    }

    return navigationStringToGoBack;
  }

  /**
   * Saves a PubItem and handles navigation afterwards.
   * 
   * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when the
   *        operation is successful.
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String saveCurrentPubItem(String navigationRuleWhenSuccessfull) {
    try {
      if (this.currentPubItem == null) {
        throw new TechnicalException("No current PubItem is set.");
      }

      PubItemVO pubItem = new PubItemVO(this.currentPubItem);
      this.cleanUpItem(pubItem);

      pubItem =
          PubItemService.INSTANCE.savePubItem(pubItem, this.getLoginHelper().getAccountUser());

      this.setCurrentPubItem(new PubItemVOPresentation(pubItem));
    } catch (final TechnicalException tE) {
      if (tE.getCause() instanceof OptimisticLockingException) {
        ItemControllerSessionBean.logger.error(
            "Could not save item because it has been changed by another user in the meantime."
                + "\n" + tE.toString(), tE);
        throw new RuntimeException(
            "Could not save item because it has been changed by another user in the meantime.", tE);
      } else {
        throw new RuntimeException("Technical exception during the saving of the item", tE);
      }
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Could not save item." + "\n" + e.toString(), e);
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
      return ErrorPage.LOAD_ERRORPAGE;
    }

    return navigationRuleWhenSuccessfull;
  }



  /**
   * Method for sending an email with attached file. The sending requires authentication.
   * 
   * @author: StG
   * @param smtpHost the outgoing smpt mail server
   * @param withAuth use authentication (true/false)
   * @param usr the user authorized to the server
   * @param pwd the password of the user
   * @param senderAddress the email address of the sender
   * @param recipientsAddresses the email address(es) of the recipients
   * @param recipientsCCAddresses the email address(es) of the recipients
   * @param replyToAddresses the reply to address(es)
   * @param text the content text of the email
   * @param subject the subject of the email
   * @param attachments the names/paths of the files to be attached
   * @throws Exception if wrong pws or user or emailing data
   */
  public String sendEmail(String smtpHost, String withAuth, String usr, String pwd,
      String senderAddress, String[] recipientsAddresses, String[] recipientsCCAddresses,
      String[] recipientsBCCAddresses, String[] replyToAddresses, String subject, String text,
      String[] attachments) throws TechnicalException {

    String status = "not sent";
    status =
        EmailService.sendMail(smtpHost, withAuth, usr, pwd, senderAddress, recipientsAddresses,
            recipientsCCAddresses, recipientsBCCAddresses, replyToAddresses, subject, text,
            attachments);

    return status;
  }

  public void setCurrentCollection(ContextVO currentCollection) {
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
  public String releaseCurrentPubItem(String comment, String navigationRuleWhenSuccessfull) {
    try {
      if (this.currentPubItem == null) {
        throw new TechnicalException("No current PubItem is set.");
      }

      // TODO
      if (this.currentPubItem.getVersion().getState() != State.SUBMITTED) {
        throw new TechnicalException("Invalid state.");
      }

      final PubItemVO pubItem = new PubItemVO(this.currentPubItem);

      this.cleanUpItem(pubItem);

      ItemRO pubItemRO = null;
      PubItemService.releasePubItem(pubItem.getVersion(), pubItem.getModificationDate(), comment,
          this.getLoginHelper().getAccountUser());
      pubItemRO = new PubItemVO().getVersion();

      if (pubItemRO == this.currentPubItem.getVersion()) {
        return null;
      }
    } catch (final TechnicalException tE) {
      if (tE.getCause() instanceof OptimisticLockingException) {
        ItemControllerSessionBean.logger.error(
            "Could not submit or release item because it has been changed by another user in the meantime."
                + "\n" + tE.toString(), tE);
        throw new RuntimeException(
            "Could not submit or release item because it has been changed by another user in the meantime.",
            tE);
      }
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Could not release item.", e);
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);

      return ErrorPage.LOAD_ERRORPAGE;
    }

    return navigationRuleWhenSuccessfull;
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
      if (this.currentPubItem == null) {
        final TechnicalException technicalException =
            new TechnicalException("No current PubItem is set.");
        throw technicalException;
      }

      final PubItemVO pubItem = new PubItemVO(this.currentPubItem);

      PubItemService.withdrawPubItem(pubItem, pubItem.getModificationDate(), comment, this
          .getLoginHelper().getAccountUser());
    } catch (final Exception e) {
      ItemControllerSessionBean.logger.error("Could not withdraw item." + "\n" + e.toString());
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);

      return ErrorPage.LOAD_ERRORPAGE;
    }

    return navigationRuleWhenSuccessfull;
  }
}
