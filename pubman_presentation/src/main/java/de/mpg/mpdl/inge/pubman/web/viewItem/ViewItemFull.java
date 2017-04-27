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
package de.mpg.mpdl.inge.pubman.web.viewItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.DoiRestService;
import de.mpg.mpdl.inge.pubman.ItemExportingService;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.ViewItemRevisionsPage;
import de.mpg.mpdl.inge.pubman.web.ViewItemStatisticsPage;
import de.mpg.mpdl.inge.pubman.web.acceptItem.AcceptItem;
import de.mpg.mpdl.inge.pubman.web.acceptItem.AcceptItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.basket.PubItemStorageSessionBean;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem.SubmissionMethod;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItem;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.export.ExportItems;
import de.mpg.mpdl.inge.pubman.web.export.ExportItemsSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemLog.ViewItemLog;
import de.mpg.mpdl.inge.pubman.web.releases.ItemVersionListSessionBean;
import de.mpg.mpdl.inge.pubman.web.releases.ReleaseHistory;
import de.mpg.mpdl.inge.pubman.web.reviseItem.ReviseItem;
import de.mpg.mpdl.inge.pubman.web.revisions.RelationListSessionBean;
import de.mpg.mpdl.inge.pubman.web.submitItem.SubmitItem;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.ObjectFormatter;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.RightsManagementSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.CreatorDisplay;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemCreators.Type;
import de.mpg.mpdl.inge.pubman.web.withdrawItem.WithdrawItem;
import de.mpg.mpdl.inge.pubman.web.withdrawItem.WithdrawItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.yearbook.YearbookInvalidItemRO;
import de.mpg.mpdl.inge.pubman.web.yearbook.YearbookItemSessionBean;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.impl.ItemTransformingServiceImpl;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Backing bean for ViewItemFull.jspf (for viewing items in a full context).
 * 
 * @author Tobias Schraut, created 03.09.2007
 * @version: $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "ViewItemFull")
@SuppressWarnings("serial")
public class ViewItemFull extends FacesBean {
  private static final Logger logger = Logger.getLogger(ViewItemFull.class);

  public static final String LOAD_VIEWITEM = "loadViewItem";

  public boolean isDepositor = false;
  public boolean isModerator = false;
  public boolean isPrivilegedViewer = false;

  private static final String SSRN_LOCAL_TAG = "Tag: SSRN";
  private static final String RESOLVE_HANDLE_SERVICE = "http://hdl.handle.net/";
  private static final String ALTERNATIVE_MODERATOR_EMAIL = "pubman-support@gwdg.de";
  private static final String ISI_KNOWLEDGE_BASE_LINK =
      "http://gateway.isiknowledge.com/gateway/Gateway.cgi?GWVersion=2&SrcAuth=SFX&SrcApp=SFX&DestLinkType=FullRecord&KeyUT=";
  private static final String ISI_KNOWLEDGE_DEST_APP = "&DestApp=WOS";
  private static final String PARAMETERNAME_ITEM_ID = "itemId";
  private static final String PARAMETERNAME_MENU_VIEW = "view";
  private static final String FUNCTION_MODIFY = "modify";
  private static final String FUNCTION_NEW_REVISION = "new_revision";
  private static final String VALIDATION_ERROR_MESSAGE = "depositorWS_NotSuccessfullySubmitted";


  private ContextVO context = null;
  private PubItemVOPresentation pubItem = null;
  private String languages;
  private YearbookItemSessionBean yisb;
  private int defaultSize = 20;

  /**
   * The list of formatted organzations in an ArrayList.
   */
  private ArrayList<String> organizationArray;

  /**
   * The list of affiliated organizations as VO List.
   */
  private ArrayList<ViewItemOrganization> organizationList;

  /**
   * The list of affiliated organizations in a list.
   */
  private List<OrganizationVO> affiliatedOrganizationsList;

  /**
   * The list of formatted creators which are persons and organizations in an ArrayList.
   */
  private ArrayList<ViewItemCreators> creators;

  private List<SourceBean> sourceList;

  /** Context list, where SSRN-Button will be available */
  private List<String> ssrnContexts;

  /** The url used for the citation */
  private String citationURL;

  /** The url used for the latestVersion */
  private String latestVersionURL;

  /** The url of the Coreservice for file downloads */
  private String fwUrl;

  /** Version and ObjectId of the item */
  private String itemPattern;

  /** unapi */
  private String unapiURLdownload;
  private String unapiURLview;
  private String unapiEscidoc;
  private String unapiEndnote;
  private String unapiBibtex;
  private String unapiApa;

  /** Properties for action links rendering conditions */
  private boolean isStateWithdrawn;
  private boolean isLoggedIn;
  private boolean isLatestVersion;
  private boolean isLatestRelease;
  private boolean isStateSubmitted;
  private boolean isStateReleased;
  private boolean isStatePending;
  private boolean isOwner;
  private boolean isModifyDisabled;
  private boolean isCreateNewRevisionDisabled;
  private boolean isWorkflowStandard;
  private boolean isWorkflowSimple;
  private boolean isStateInRevision;
  private boolean isPublicStateReleased;
  private boolean isMemberOfYearbook;
  private boolean isCandidateOfYearbook;

  // for inclusion into the ViewItemFull page, test if rendering conditions can be made faster
  private boolean canEdit = false;
  private boolean canSubmit = false;
  private boolean canRelease = false;
  private boolean canAccept = false;
  private boolean canRevise = false;
  private boolean canDelete = false;
  private boolean canWithdraw = false;
  private boolean canModify = false;
  private boolean canCreateNewRevision = false;
  private boolean canCreateFromTemplate = false;
  private boolean canAddToBasket = false;
  private boolean canDeleteFromBasket = false;
  private boolean canViewLocalTags = false;
  private boolean canManageAudience = false;
  private boolean canShowItemLog = false;
  private boolean canShowStatistics = false;
  private boolean canShowRevisions = false;
  private boolean canShowReleaseHistory = false;
  private boolean canShowLastMessage = false;
  private boolean isStateWasReleased = false;


  public ViewItemFull() {
    this.init();
  }

  public void init() {
    String itemID = "";

    // populate the core service Url
    this.fwUrl = PropertyReader.getProperty("escidoc.framework_access.framework.url");
    this.defaultSize =
        Integer.parseInt(PropertyReader.getProperty(
            "escidoc.pubman_presentation.viewFullItem.defaultSize", "20"));

    if (this.getLoginHelper() != null) {
      final String viewId = FacesTools.getCurrentInstance().getViewRoot().getViewId();
      if ("/ViewItemOverviewPage.jsp".equals(viewId)) {
        this.getLoginHelper().setDetailedMode(false);
      } else if ("/ViewItemFullPage.jsp".equals(viewId)) {
        this.getLoginHelper().setDetailedMode(true);
      }
    }

    // Try to get a pubitem either via the controller session bean or an URL Parameter
    itemID = FacesTools.getRequest().getParameter(ViewItemFull.PARAMETERNAME_ITEM_ID);
    if (itemID != null) {
      try {
        this.pubItem = this.getItemControllerSessionBean().retrieveItem(itemID);
        // if it is a new item reset ViewItemSessionBean
        if (this.getItemControllerSessionBean().getCurrentPubItem() == null
            || !this.pubItem
                .getVersion()
                .getObjectIdAndVersion()
                .equals(
                    this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                        .getObjectIdAndVersion())) {
          this.getViewItemSessionBean().itemChanged();
        }
        this.getItemControllerSessionBean().setCurrentPubItem(this.pubItem);
      } catch (final AuthorizationException e) {
        if (this.getLoginHelper().isLoggedIn()) {
          FacesBean.error(this.getMessage("ViewItemFull_noPermission"));
        } else {
          // redirect to login
          getLoginHelper().logout();
          // ((Login) FacesTools.findBean("Login")).forceLogout(itemID);
        }
      } catch (final AuthenticationException e) {
        if (this.getLoginHelper().isLoggedIn()) {
          FacesBean.error(this.getMessage("ViewItemFull_noPermission"));
        } else {
          // redirect to login
          getLoginHelper().logout();
          // ((Login) FacesTools.findBean("Login")).forceLogout(itemID);
        }
      } catch (final Exception e) {
        ViewItemFull.logger.error("Could not retrieve release with id " + itemID, e);
        this.error(this.getMessage("ViewItemFull_invalidID").replace("$1", itemID), e.getMessage());
      }
    } else {
      // Cleanup needed if an edit site was loaded inbetween
      // (e.g. local tags --> source without editors --> editors are created in the SourceBean and
      // not removed)
      final ItemControllerSessionBean icsb = this.getItemControllerSessionBean();
      PubItemUtil.cleanUpItem(icsb.getCurrentPubItem());
      this.pubItem = icsb.getCurrentPubItem();
    }

    final String subMenu =
        FacesTools.getRequest().getParameter(ViewItemFull.PARAMETERNAME_MENU_VIEW);
    if (subMenu != null) {
      this.getViewItemSessionBean().setSubMenu(subMenu);
    }

    if (this.pubItem != null) {
      ViewItemFull.logger.info("Initializing view for item: "
          + this.pubItem.getVersion().getObjectIdAndVersion());

      // set citation url
      try {
        String pubmanUrl =
            PropertyReader.getProperty("escidoc.pubman.instance.url")
                + PropertyReader.getProperty("escidoc.pubman.instance.context.path");

        this.itemPattern =
            PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1",
                this.getPubItem().getVersion().getObjectIdAndVersion());
        if (!pubmanUrl.endsWith("/")) {
          pubmanUrl = pubmanUrl + "/";
        }
        if (this.itemPattern.startsWith("/")) {
          this.itemPattern = this.itemPattern.substring(1, this.itemPattern.length());
        }
        // MF: Removed exclusion of pending items here
        this.citationURL = pubmanUrl + this.itemPattern;

        if (this.getPubItem().getLatestVersion() != null
            && this.getPubItem().getLatestVersion().getObjectIdAndVersion() != null) {
          String latestVersionItemPattern =
              PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1",
                  this.getPubItem().getLatestVersion().getObjectIdAndVersion());
          if (latestVersionItemPattern.startsWith("/")) {
            latestVersionItemPattern =
                latestVersionItemPattern.substring(1, latestVersionItemPattern.length());
          }
          this.setLatestVersionURL(pubmanUrl + latestVersionItemPattern);
        }
      } catch (final Exception e) {
        e.printStackTrace();
        this.citationURL = "";
      }

      this.isOwner = true;

      if (this.pubItem.getOwner() != null) {
        this.isOwner =
            (this.getLoginHelper().getAccountUser().getReference() != null ? this.getLoginHelper()
                .getAccountUser().getReference().getObjectId()
                .equals(this.getPubItem().getOwner().getObjectId()) : false);

        if (this.getLoginHelper().getAccountUser().getReference() != null
            && this.getLoginHelper().getAccountUser().getGrantsWithoutAudienceGrants() != null) {
          this.isModerator = false;
          this.isPrivilegedViewer = false;
          this.isDepositor = false;


          this.isModerator =
              this.getLoginHelper().getAccountUser().isModerator(this.getPubItem().getContext());
          this.isDepositor = this.getLoginHelper().getIsDepositor();
          this.isPrivilegedViewer =
              this.getLoginHelper().getAccountUser()
                  .isPrivilegedViewer(this.getPubItem().getContext());

          if (!this.isOwner) {
            for (final GrantVO grant : this.getLoginHelper().getAccountUser()
                .getGrantsWithoutAudienceGrants()) {
              if (grant.getRole().equals("escidoc:role-system-administrator")) {
                this.isOwner = true;
                break;
              }
            }
          }
        }
      }

      // @author Markus Haarlaender - setting properties for Action Links
      this.isLoggedIn = this.getLoginHelper().isLoggedIn();
      this.isLatestVersion =
          this.getPubItem().getVersion().getVersionNumber() == this.getPubItem().getLatestVersion()
              .getVersionNumber();
      this.isLatestRelease =
          this.getPubItem().getVersion().getVersionNumber() == this.getPubItem().getLatestRelease()
              .getVersionNumber();
      this.isStateWithdrawn =
          this.getPubItem().getPublicStatus().toString().equals(State.WITHDRAWN.toString());
      this.isStateSubmitted =
          this.getPubItem().getVersion().getState().toString().equals(State.SUBMITTED.toString())
              && !this.isStateWithdrawn;;
      this.isStateReleased =
          this.getPubItem().getVersion().getState().toString().equals(State.RELEASED.toString())
              && !this.isStateWithdrawn;
      this.isStatePending =
          this.getPubItem().getVersion().getState().toString().equals(State.PENDING.toString())
              && !this.isStateWithdrawn;;
      this.isStateInRevision =
          this.getPubItem().getVersion().getState().toString().equals(State.IN_REVISION.toString())
              && !this.isStateWithdrawn;;
      this.isPublicStateReleased = this.getPubItem().getPublicStatus() == State.RELEASED;
      this.isStateWasReleased =
          this.getPubItem().getLatestRelease().getObjectId() != null ? true : false;

      // display a warn message if the item version is not the latest
      if (this.isLatestVersion == false
          && this.getPubItem().getLatestVersion().getVersionNumber() != this.getPubItem()
              .getLatestRelease().getVersionNumber() && this.isLoggedIn) {
        String link = null;
        try {
          link =
              PropertyReader.getProperty("escidoc.pubman.instance.url")
                  + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                  + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll(
                      "\\$1",
                      this.getPubItem().getVersion().getObjectId()
                          + (this.getPubItem().getLatestVersion().getVersionNumber() != 0 ? ":"
                              + this.getPubItem().getLatestVersion().getVersionNumber() : ""));
        } catch (final Exception e) {
          ViewItemFull.logger.error("Error when trying to access a property via PropertyReader", e);
        }
        this.warn(this.getMessage("itemIsNotLatestVersion") + "<br/><a href=\""
            + (link != null ? link : "") + "\" >" + (link != null ? link : "") + "</a>");
      } else if (this.isLatestVersion == false
          && this.getPubItem().getLatestRelease().getVersionNumber() > this.getPubItem()
              .getVersion().getVersionNumber()) {
        String link = null;
        try {
          link =
              PropertyReader.getProperty("escidoc.pubman.instance.url")
                  + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                  + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll(
                      "\\$1",
                      this.getPubItem().getVersion().getObjectId()
                          + (this.getPubItem().getLatestRelease().getVersionNumber() != 0 ? ":"
                              + this.getPubItem().getLatestRelease().getVersionNumber() : ""));
        } catch (final Exception e) {
          ViewItemFull.logger.error("Error when trying to access a property via PropertyReader", e);
        }
        this.warn(this.getMessage("itemIsNotLatestReleasedVersion") + "<br/><a href=\""
            + (link != null ? link : "") + "\" >" + (link != null ? link : "") + "</a>");
      }
      try {
        this.isWorkflowStandard =
            (this.getContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.STANDARD);
        this.isWorkflowSimple =
            (this.getContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.SIMPLE);
      } catch (final Exception e) {
        this.isWorkflowSimple = true;
        this.isWorkflowStandard = false;
      }

      if (this.isStateWithdrawn) {
        this.getViewItemSessionBean().itemChanged();
      }

      // set up some pre-requisites
      // the list of numbered affiliated organizations
      this.createCreatorsList();

      // START - retrieve languages from CoNE
      if (this.getPubItem().getMetadata().getLanguages() != null
          && this.getPubItem().getMetadata().getLanguages().size() > 0) {

        final StringWriter result = new StringWriter();
        for (int i = 0; i < this.getPubItem().getMetadata().getLanguages().size(); i++) {

          if (i > 0) {
            result.append(", ");
          }

          final String language = this.getPubItem().getMetadata().getLanguages().get(i);
          String languageName = null;
          try {
            languageName =
                CommonUtils.getConeLanguageName(language, this.getI18nHelper().getLocale());
          } catch (final Exception e) {
            ViewItemFull.logger.error("Cannot retrieve language information from CoNE", e);
          }
          if (languageName != null) {
            result.append(language);
            if (!"".equals(languageName)) {
              result.append(" - ");
              result.append(languageName);
            }
          }
        }
        this.languages = result.toString();
      }

      // END - retrieve languages from CoNE

      // clear source list first
      if (this.getPubItem().getMetadata().getSources().size() > 0) {
        this.sourceList = new ArrayList<SourceBean>();
        for (int i = 0; i < this.getPubItem().getMetadata().getSources().size(); i++) {
          this.sourceList.add(new SourceBean(this.getPubItem().getMetadata().getSources().get(i)));
        }
      }

      // the list of files
      // Check if the item is also in the search result list
      final List<PubItemVOPresentation> currentPubItemList =
          this.getPubItemListSessionBean().getCurrentPartList();

      // removed unnecessary creation of new array list
      // List<SearchHitVO> searchHitList = new ArrayList<SearchHitVO>();
      if (currentPubItemList != null) {
        for (int i = 0; i < currentPubItemList.size(); i++) {
          if ((this.getPubItem().getVersion().getObjectId().equals(currentPubItemList.get(i)
              .getVersion().getObjectId()))
              &&

              (this.getPubItem().getVersion().getVersionNumber() == currentPubItemList.get(i)
                  .getVersion().getVersionNumber())
              &&

              (currentPubItemList.get(i).getSearchHitList() != null && currentPubItemList.get(i)
                  .getSearchHitList().size() > 0)) {
            this.pubItem.setSearchResult(true);
            this.pubItem.setSearchHitList(currentPubItemList.get(i).getSearchHitList());
            this.pubItem.setScore(currentPubItemList.get(i).getScore());
            this.pubItem.setSearchHitBeanList();
            // this.pubItem = new PubItemVOPresentation(new PubItemResultVO(this.pubItem,
            // currentPubItemList.get(i).getSearchHitList(), currentPubItemList.get(i).getScore()));
          }
        }
      }

      // if item is currently part of invalid yearbook items, show Validation Messages
      // ContextListSessionBean clsb =
      // (ContextListSessionBean)FacesTools.findBean(ContextListSessionBean.class);
      if (this.getLoginHelper().getIsYearbookEditor()) {

        this.yisb = (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");

        if (this.yisb.getYearbookItem() != null) {
          if (this.yisb.getInvalidItemMap().get(this.getPubItem().getVersion().getObjectId()) != null) {
            try {
              // revalidate
              this.yisb.validateItem(this.getPubItem());
              final YearbookInvalidItemRO invItem =
                  this.yisb.getInvalidItemMap().get(this.getPubItem().getVersion().getObjectId());
              if (invItem != null) {
                ((PubItemVOPresentation) this.getPubItem()).setValidationReport(invItem
                    .getValidationReport());
              }
            } catch (final Exception e) {
              ViewItemFull.logger.error("Error in Yearbook validation", e);
            }
          }

          try {
            if (State.PENDING.equals(this.yisb.getYearbookItem().getVersion().getState())
                || State.IN_REVISION.equals(this.yisb.getYearbookItem().getVersion().getState())) {
              this.isCandidateOfYearbook =
                  this.yisb.isCandidate(this.pubItem.getVersion().getObjectId());
              if (!(this.isCandidateOfYearbook) && this.yisb.getNumberOfMembers() > 0) {
                this.isMemberOfYearbook =
                    this.yisb.isMember(this.pubItem.getVersion().getObjectId());
              }
            }
          } catch (final Exception e) {
            e.printStackTrace();
          }
        }
      }
    }

    // Unapi Export
    try {
      this.unapiURLdownload = PropertyReader.getProperty("escidoc.unapi.download.server");
      this.unapiURLview = PropertyReader.getProperty("escidoc.unapi.view.server");
      this.unapiEscidoc = this.unapiURLdownload + "?id=" + itemID + "&format=escidoc";
      this.unapiEndnote = this.unapiURLdownload + "?id=" + itemID + "&format=endnote";
      this.unapiBibtex = this.unapiURLdownload + "?id=" + itemID + "&format=bibtex";
      this.unapiApa = this.unapiURLdownload + "?id=" + itemID + "&format=apa";
    } catch (final Exception e) {
      ViewItemFull.logger.error("Error getting unapi url property", e);
      throw new RuntimeException(e);
    }

    /*
     * if (logViewAction) { logViewAction(); }
     */

    // TODO: remove into separate method, must this be in the initializer?
    // not certain why is this method it always returns null (for languages or not)
    // therefore now it is commented, if needed again to be uncomented and getConeLanguageCode to be
    // fixed

    /*
     * if (this.pubItem.getMetadata().getSubjects().size()>0) { for (TextVO subject :
     * this.pubItem.getMetadata().getSubjects()) { if (subject.getType() != null &&
     * subject.getType().equals(SubjectClassification.ISO639_3.name())) { try {
     * subject.setLanguage(CommonUtils.getConeLanguageCode(subject.getValue())); } catch (Exception
     * e) { throw new RuntimeException("Error retrieving language code for '" + subject.getValue() +
     * "'", e); } } } }
     */

    // set SSRN contexts
    try {
      String contexts = PropertyReader.getProperty("escidoc.pubman.instance.ssrn_contexts");
      if (contexts != null && !"".equals(contexts)) {
        this.ssrnContexts = new ArrayList<String>();
        while (contexts.contains(",")) {
          this.ssrnContexts.add(contexts.substring(0, contexts.indexOf(",")));
          contexts = contexts.substring(contexts.indexOf(",") + 1, contexts.length());
        }
        this.ssrnContexts.add(contexts);
      }

    } catch (final Exception e) {
      ViewItemFull.logger.error("couldn't load ssrn context list", e);
    }

    this.setLinks();
  }

  public boolean isSsrnContext() {
    if (this.ssrnContexts != null
        && this.ssrnContexts.contains(this.getPubItem().getContext().getObjectId())) {
      return true;
    }

    return false;
  }

  public boolean isSsrnTagged() {
    if (this.getPubItem().getLocalTags().contains(ViewItemFull.SSRN_LOCAL_TAG)) {
      return true;
    }

    return false;
  }

  public String addToYearbookMember() {
    final List<ItemRO> selected = new ArrayList<ItemRO>();
    selected.add(this.getPubItem().getVersion());
    this.yisb.addMembers(selected);
    this.isCandidateOfYearbook = false;
    this.isMemberOfYearbook = true;

    return "";
  }

  public String removeMemberFromYearbook() {
    final List<ItemRO> selected = new ArrayList<ItemRO>();
    selected.add(this.getPubItem().getVersion());
    this.yisb.removeMembers(selected);
    this.isMemberOfYearbook = false;
    this.isCandidateOfYearbook = true;

    return "";
  }

  /**
   * Redirects the user to the edit item page
   * 
   * @return Sring nav rule to load the edit item page
   */
  public String editItem() {
    // clear the list of locators and files when start editing an item
    this.getEditItemSessionBean().clean();

    return EditItem.LOAD_EDITITEM;
  }

  /**
   * Redirects the user to the withdraw item page
   * 
   * @return Sring nav rule to load the withdraw item page
   */
  public String withdrawItem() {
    this.getWithdrawItemSessionBean().setNavigationStringToGoBack(
        this.getViewItemSessionBean().getNavigationStringToGoBack());

    return WithdrawItem.LOAD_WITHDRAWITEM;
  }

  /**
   * Redirects the user to the edit item page in modify-mode
   * 
   * @return Sring nav rule to load the editItem item page
   */
  public String modifyItem() {
    // clear the list of locators and files when start modifying an item
    this.getEditItemSessionBean().clean();

    return EditItem.LOAD_EDITITEM;
  }

  /**
   * Redirects the user to the create new revision page Changed by DiT, 29.11.2007: only show
   * contexts when user has privileges for more than one context
   * 
   * @return Sring nav rule to load the create new revision page
   */
  public String createNewRevision() {
    // clear the list of locators and files when start creating a new revision
    this.getEditItemSessionBean().clean();

    // Changed by DiT, 29.11.2007: only show contexts when user has privileges for more than one
    // context
    // if there is only one context for this user we can skip the CreateItem-Dialog and create the
    // new item directly
    if (this.getCollectionListSessionBean().getDepositorContextList().size() == 0) {
      ViewItemFull.logger.warn("The user does not have privileges for any context.");
      FacesBean.error(this.getMessage("ViewItemFull_user_has_no_context"));
      return null;
    }

    if (this.getCollectionListSessionBean().getDepositorContextList().size() == 1) {
      final ContextVO context =
          this.getCollectionListSessionBean().getDepositorContextList().get(0);
      return this.getItemControllerSessionBean().createNewRevision(EditItem.LOAD_EDITITEM,
          context.getReference(), this.getPubItem(), null);
    }

    final ContextVO context = this.getCollectionListSessionBean().getDepositorContextList().get(0);
    // more than one context exists for this user; let him choose the right one
    this.getRelationListSessionBean().setPubItemVO(
        this.getItemControllerSessionBean().getCurrentPubItem());
    // Set submission method for correct redirect
    ((CreateItem) FacesTools.findBean("CreateItem")).setMethod(SubmissionMethod.FULL_SUBMISSION);

    return this.getItemControllerSessionBean().createNewRevision(CreateItem.LOAD_CREATEITEM,
        context.getReference(), this.getPubItem(), null);
  }

  /**
   * Redirects the user to the View revisions page.
   * 
   * @return Sring nav rule to load the create new revision page.
   */
  public String showRevisions() {
    this.getRelationListSessionBean().setPubItemVO(
        this.getItemControllerSessionBean().getCurrentPubItem());

    return ViewItemRevisionsPage.LOAD_VIEWREVISIONS;
  }

  /**
   * Redirects the user to the statistics page.
   * 
   * @return String nav rule to load the create new revision page.
   */
  public String showStatistics() {
    return ViewItemStatisticsPage.LOAD_VIEWSTATISTICS;
  }

  /**
   * Redirects the user to the Item Log page.
   * 
   * @return String nav rule to load the create new revision page.
   */
  public String showItemLog() {
    this.getItemVersionListSessionBean().resetVersionLists();

    return ViewItemLog.LOAD_ITEM_LOG;
  }

  /**
   * submits the selected item(s) an redirects the user to the page he came from (depositor
   * workspace or search result list) Changed by FrM: Inserted validation and call to "enter
   * submission comment" page.
   * 
   * @return String nav rule to load the page the user came from
   */
  public String submitItem() {
    try {
      ItemValidatingService.validate(new PubItemVO(this.getPubItem()), ValidationPoint.STANDARD);
    } catch (final ItemInvalidException e) {
      this.showValidationMessages(e.getReport());
      return null;
    } catch (final ValidationException e) {
      throw new RuntimeException("Validation error", e);
    }

//    this.getSubmitItemSessionBean().setNavigationStringToGoBack(
//        this.getViewItemSessionBean().getNavigationStringToGoBack());

    return SubmitItem.LOAD_SUBMITITEM;
  }

  public String acceptItem() {
    try {
      ItemValidatingService.validate(new PubItemVO(this.getPubItem()), ValidationPoint.STANDARD);
    } catch (final ItemInvalidException e) {
      this.showValidationMessages(e.getReport());
      return null;
    } catch (final ValidationException e) {
      throw new RuntimeException("Validation error", e);
    }

    this.getAcceptItemSessionBean().setNavigationStringToGoBack(
        this.getViewItemSessionBean().getNavigationStringToGoBack());

    return AcceptItem.LOAD_ACCEPTITEM;
  }

  /**
   * deletes the selected item(s) an redirects the user to the page he came from (depositor
   * workspace or search result list)
   * 
   * @return String nav rule to load the page the user came from
   */
  public String deleteItem() {
    if (this.getViewItemSessionBean().getNavigationStringToGoBack() == null) {
      this.getViewItemSessionBean().setNavigationStringToGoBack(
          MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS);
    }
    final String retVal =
        this.getItemControllerSessionBean().deleteCurrentPubItem(
            this.getViewItemSessionBean().getNavigationStringToGoBack());
    // show message
    if (!retVal.equals(ErrorPage.LOAD_ERRORPAGE)) {
      this.info(this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_DELETED));
      // redirect to last breadcrumb, if available
      final BreadcrumbItemHistorySessionBean bhsb =
          (BreadcrumbItemHistorySessionBean) FacesTools
              .findBean("BreadcrumbItemHistorySessionBean");
      try {
        for (int i = bhsb.getBreadcrumbs().size() - 1; i > 0; i--) {
          if (bhsb.getBreadcrumbs().get(i - 1).isItemSpecific() == false
              && bhsb.getBreadcrumbs().get(i - 1).getDisplayValue()
                  .equalsIgnoreCase("CreateItemPage") == false) {
            FacesTools.getExternalContext().redirect(bhsb.getBreadcrumbs().get(i - 1).getPage());
            return retVal;
          }
        }
      } catch (final IOException e) {
        ViewItemFull.logger.error("Could not redirect to last breadcrumb!");
        return "loadHome";
      }
    }

    return retVal;
  }

  private void showValidationMessages(ValidationReportVO report) {
    this.warn(this.getMessage(ViewItemFull.VALIDATION_ERROR_MESSAGE));
    for (final Iterator<ValidationReportItemVO> iter = report.getItems().iterator(); iter.hasNext();) {
      final ValidationReportItemVO element = iter.next();
      FacesBean.error(this.getMessage(element.getContent()));
    }
  }

  /**
   * Generates the affiliated organization list as one string for presenting it in the jsp via the
   * dynamic html component. Duplicate affiliated organizations will be detected and merged. All
   * affiliated organizations will be numbered.
   * 
   */
  private void createCreatorsList() {
    List<CreatorVO> tempCreatorList;
    List<OrganizationVO> tempOrganizationList = null;
    List<OrganizationVO> sortOrganizationList = null;
    sortOrganizationList = new ArrayList<OrganizationVO>();
    String formattedCreator = "";
    String formattedOrganization = "";

    this.setOrganizationArray(new ArrayList<String>());
    this.setOrganizationList(new ArrayList<ViewItemOrganization>());

    // counter for organization array
    int counterOrganization = 0;
    final ObjectFormatter formatter = new ObjectFormatter();

    // temporary list of All creators, retrieved directly from the metadata
    tempCreatorList = this.getPubItem().getMetadata().getCreators();
    // the list of creators is initialized to a new array list
    this.setCreators(new ArrayList<ViewItemCreators>());
    // initial affiliation position set to 0
    int affiliationPosition = 0;

    // for each creator in the list
    for (int i = 0; i < tempCreatorList.size(); i++) {

      // temporary organization list is matched against the sorted for each separate creator
      // therefore for each creator is newly re-set
      tempOrganizationList = new ArrayList<OrganizationVO>();

      // put creator in temporary VO
      CreatorVO creator1 = new CreatorVO();
      creator1 = tempCreatorList.get(i);

      // annotation = new StringBuffer();
      // int organizationsFound = 0;
      final ViewItemCreators creator = new ViewItemCreators();
      final CreatorDisplay creatorDisplay = new CreatorDisplay();

      // if the creator is a person add his organization to the sorted organization list
      if (creator1.getPerson() != null) {
        // if there is affiliated organization for this creator
        if (creator1.getPerson().getOrganizations().size() > 0) {
          // add each affiliated organization of the creator to the temporary organization list
          for (int listSize = 0; listSize < creator1.getPerson().getOrganizations().size(); listSize++) {
            tempOrganizationList.add(creator1.getPerson().getOrganizations().get(listSize));
          }

          // for each organizations in the temporary organization list
          for (int j = 0; j < tempOrganizationList.size(); j++) {
            // check if the organization in the list is in the sorted organization list
            if (!sortOrganizationList.contains(tempOrganizationList.get(j))) {
              affiliationPosition++;
              // if the temporary organization is to be added to the sorted set of organizations
              sortOrganizationList.add(tempOrganizationList.get(j));
              // create new Organization view object
              this.getOrganizationList().add(
                  ViewItemFull.formatCreatorOrganization(tempOrganizationList.get(j),
                      affiliationPosition));
            }
          }
        }

        formattedCreator =
            formatter.formatCreator(creator1,
                ViewItemFull.formatCreatorOrganizationIndex(creator1, sortOrganizationList));
        creatorDisplay.setFormattedDisplay(formattedCreator);

        if (creator1.getPerson().getIdentifier() != null
            && (creator1.getPerson().getIdentifier().getType() == IdType.CONE)) {
          try {
            creatorDisplay.setPortfolioLink(creator1.getPerson().getIdentifier().getId());
          } catch (final Exception e) {
            throw new RuntimeException(e);
          }
        }
        creator.setCreatorType(Type.PERSON.toString());
        creator.setCreatorObj(creatorDisplay);
        creator.setCreatorRole(creator1.getRoleString());

        this.creators.add(creator);
      } // end if creator is a person

      if (creator1.getOrganization() != null) {
        formattedCreator = formatter.formatCreator(creator1, "");
        creatorDisplay.setFormattedDisplay(formattedCreator);
        final ViewItemCreatorOrganization creatorOrganization = new ViewItemCreatorOrganization();
        creatorOrganization.setOrganizationName(formattedCreator);
        creatorOrganization.setPosition(new Integer(counterOrganization).toString());
        creatorOrganization.setOrganizationAddress(creator1.getOrganization().getAddress());
        creatorOrganization.setOrganizationInfoPage(formattedCreator, creator1.getOrganization()
            .getAddress());
        creatorOrganization.setIdentifier(creator1.getOrganization().getIdentifier());
        creator.setCreatorType(Type.ORGANIZATION.toString());
        creator.setCreatorObj(creatorOrganization);
        creator.setCreatorRole(creator1.getRoleString());
        this.creators.add(creator);
      }

      counterOrganization++;
      // creatorListString.append(formattedCreator);
      this.setAffiliatedOrganizationsList(sortOrganizationList);
      // this.affiliatedOrganizationsList = sortOrganizationList;
      // generate a 'well-formed' list for presentation in the jsp
      for (int k = 0; k < sortOrganizationList.size(); k++) {
        final String name =
            sortOrganizationList.get(k).getName() != null ? sortOrganizationList.get(k).getName()
                : "";
        formattedOrganization =
            "<p>" + (k + 1) + ": " + name + "</p>" + "<p>"
                + sortOrganizationList.get(k).getAddress() + "</p>" + "<p>"
                + sortOrganizationList.get(k).getIdentifier() + "</p>";
        this.organizationArray.add(formattedOrganization);
        // this.getOrganizationArray().add(formattedOrganization);
      }
    } // end for each creator in the list

  }

  /**
   * Returns the formatted Organization for view item
   * 
   * @return ViewItemOrganization
   * @param tempOrganizationListInstance List of organizations that need to be sorted
   * @param int The position of the affiliation in the list of the organizations
   */
  public static ViewItemOrganization formatCreatorOrganization(
      OrganizationVO tempOrganizationListInstance, int affiliationPosition) {
    final ViewItemOrganization viewOrganization = new ViewItemOrganization();

    // set the organization view object to values from the current temp organization
    if (tempOrganizationListInstance.getName() != null) {
      viewOrganization.setOrganizationName(tempOrganizationListInstance.getName());
      viewOrganization.setOrganizationAddress(tempOrganizationListInstance.getAddress());
      viewOrganization.setOrganizationIdentifier(tempOrganizationListInstance.getIdentifier());
      viewOrganization.setPosition(new Integer(affiliationPosition).toString());
      viewOrganization.setOrganizationInfoPage(tempOrganizationListInstance.getName(),
          tempOrganizationListInstance.getAddress());
      viewOrganization.setOrganizationDescription(tempOrganizationListInstance.getName(),
          tempOrganizationListInstance.getAddress(), tempOrganizationListInstance.getIdentifier());
    }

    return viewOrganization;
  }

  /**
   * formats the Organization index of creator
   * 
   * @return String
   * @param creator creator object for which the organization index shall be set
   * @param sortOrganizationList sorted list of organizations in the publication item
   */
  public static String formatCreatorOrganizationIndex(CreatorVO creator,
      List<OrganizationVO> sortOrganizationList) {
    int organizationsFound = 0;
    final StringBuffer annotation = new StringBuffer();
    // go through known sorted organizations and format the number at the creator
    for (int j = 0; j < sortOrganizationList.size(); j++) {
      if (creator.getPerson().getOrganizations().contains(sortOrganizationList.get(j))) {
        if (organizationsFound == 0) {
          annotation.append("<sup>");
        }
        if (organizationsFound > 0 && j < sortOrganizationList.size()) {
          annotation.append(", ");
        }
        annotation.append(new Integer(j + 1).toString());
        organizationsFound++;
      }
    }

    if (annotation.length() > 0) {
      annotation.append("</sup>");
    }

    return annotation.toString();
  }

  /**
   * Returns the formatted Publishing Info according to filled elements
   * 
   * @return String the formatted Publishing Info
   */
  public String getPublishingInfo() {
    final StringBuffer publishingInfo = new StringBuffer();
    publishingInfo.append("");
    if (this.pubItem.getMetadata().getPublishingInfo() != null) {
      // Place
      if (this.pubItem.getMetadata().getPublishingInfo().getPlace() != null
          && !this.pubItem.getMetadata().getPublishingInfo().getPlace().equals("")) {
        publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPlace().trim());
      }
      // colon
      if (this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null
          && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim().equals("")
          && this.pubItem.getMetadata().getPublishingInfo().getPlace() != null
          && !this.pubItem.getMetadata().getPublishingInfo().getPlace().trim().equals("")) {
        publishingInfo.append(" : ");
      }
      // Publisher
      if (this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null
          && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().equals("")) {
        publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim());
      }
      // Comma
      if ((this.pubItem.getMetadata().getPublishingInfo().getEdition() != null && !this.pubItem
          .getMetadata().getPublishingInfo().getEdition().trim().equals(""))
          && ((this.pubItem.getMetadata().getPublishingInfo().getPlace() != null && !this.pubItem
              .getMetadata().getPublishingInfo().getPlace().trim().equals("")) || (this.pubItem
              .getMetadata().getPublishingInfo().getPublisher() != null && !this.pubItem
              .getMetadata().getPublishingInfo().getPublisher().trim().equals("")))) {
        publishingInfo.append(", ");
      }
      // Edition
      if (this.pubItem.getMetadata().getPublishingInfo().getEdition() != null) {
        publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getEdition());
      }
    }

    return publishingInfo.toString();
  }

  /**
   * Returns all Identifiers as formatted String
   * 
   * @return String the formatted Identifiers
   */
  public String getIdentifiers() {
    return getIdentifierHtmlString(this.pubItem.getMetadata().getIdentifiers());
  }

  public static String getIdentifierHtmlString(List<IdentifierVO> idList) {
    final StringBuffer identifiers = new StringBuffer();
    if (idList != null) {
      for (int i = 0; i < idList.size(); i++) {
        try {
          final String labelKey = "ENUM_IDENTIFIERTYPE_" + idList.get(i).getTypeString();
          identifiers.append(getLabelStatic(labelKey));
        } catch (final MissingResourceException e) {
          ViewItemFull.logger.debug("Found no label for identifier type "
              + idList.get(i).getTypeString());
          identifiers.append(idList.get(i).getTypeString());
        }
        identifiers.append(": ");
        if (CommonUtils.getIsUriValidUrl(idList.get(i))) {
          identifiers.append("<a target='_blank' href='" + idList.get(i).getId() + "'>"
              + idList.get(i).getId() + "</a>");
        } else if (idList.get(i).getType() == IdType.DOI) {
          identifiers.append("<a target='_blank' href='http://dx.doi.org/" + idList.get(i).getId()
              + "'>" + idList.get(i).getId() + "</a>");
        } else if (idList.get(i).getType() == IdType.EDOC) {
          identifiers.append("<a target='_blank' href='http://edoc.mpg.de/" + idList.get(i).getId()
              + "'>" + idList.get(i).getId() + "</a>");
        } else if (idList.get(i).getType() == IdType.ISI) {
          identifiers.append("<a target='_blank' href='" + ViewItemFull.ISI_KNOWLEDGE_BASE_LINK
              + idList.get(i).getId() + ViewItemFull.ISI_KNOWLEDGE_DEST_APP + "'>"
              + idList.get(i).getId() + "</a>");
        } else {
          identifiers.append(idList.get(i).getId());
        }
        if (i < idList.size() - 1) {
          identifiers.append("<br/>");
        }
      }
    }
    return identifiers.toString();
  }

  public static String getLabelStatic(String placeholder) {
    InternationalizationHelper i18nHelper =
        (InternationalizationHelper) FacesTools.findBean("InternationalizationHelper");
    return i18nHelper.getLabel(placeholder);
  }

  /**
   * Returns a true or a false according top the existance of specified fields in the details
   * section
   * 
   * @return boolean
   */
  public boolean getShowDetails() {
    if (this.pubItem.getMetadata() != null) {
      if ((this.pubItem.getMetadata().getLanguages() != null && this.pubItem.getMetadata()
          .getLanguages().size() > 0)
          || (this.getShowDates())
          || (this.pubItem.getMetadata().getTotalNumberOfPages() != null && !this.pubItem
              .getMetadata().getTotalNumberOfPages().trim().equals(""))
          || (this.pubItem.getMetadata().getPublishingInfo() != null)
          || (this.pubItem.getMetadata().getTableOfContents() != null && !this.pubItem
              .getMetadata().getTableOfContents().trim().equals(""))
          || (this.pubItem.getMetadata().getReviewMethod() != null)
          || (this.pubItem.getMetadata().getIdentifiers() != null && this.pubItem.getMetadata()
              .getIdentifiers().size() > 0)
          || (this.pubItem.getMetadata().getDegree() != null)
          || (this.pubItem.getMetadata().getLocation() != null && !this.pubItem.getMetadata()
              .getLocation().trim().equals(""))) {
        return true;
      }
    }

    return false;
  }

  public String getLanguages() {
    return this.languages;
  }

  /**
   * Returns a true or a false according to the existance of an event in the item
   * 
   * @return boolean
   */
  public boolean getShowEvents() {
    if (this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getEvent() != null) {
      return true;
    }

    return false;
  }

  /**
   * Returns a true or a false according to the existance of sources in the item
   * 
   * @return boolean
   */
  public boolean getShowSources() {
    if (this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getSources() != null
        && this.pubItem.getMetadata().getSources().size() > 0) {
      return true;
    }

    return false;
  }

  /**
   * Returns a true or a false according to the existance of files in the item
   * 
   * @return boolean
   */
  public boolean getShowFiles() {
    if (this.pubItem.getFileBeanList() != null && this.pubItem.getFileBeanList().size() > 0) {
      return true;
    }

    return false;
  }

  /**
   * Returns the total number of files in the item
   * 
   * @return int
   */
  public int getAmountOfFiles() {
    if (this.pubItem.getFileBeanList() != null && this.pubItem.getFileBeanList().size() > 0) {
      return this.pubItem.getFileBeanList().size();
    }

    return 0;
  }

  /**
   * Returns a true or a false according to the existance of locators in the item
   * 
   * @return boolean
   */
  public boolean getShowLocators() {
    if (this.pubItem.getLocatorBeanList() != null && this.pubItem.getLocatorBeanList().size() > 0) {
      return true;
    }

    return false;
  }

  /**
   * Returns the total number of locators in the item
   * 
   * @return int
   */
  public int getAmountOfLocators() {
    if (this.pubItem.getLocatorBeanList() != null && this.pubItem.getLocatorBeanList().size() > 0) {
      return this.pubItem.getLocatorBeanList().size();
    }

    return 0;
  }

  /**
   * Returns a true or a false according to the user state (logged in or not)
   * 
   * @author Markus Haarlaender
   * @return boolean
   */
  public boolean getShowSystemDetails() {
    return this.getLoginHelper().isLoggedIn();
  }

  /**
   * Returns a boolean according to the user item state
   * 
   * @author Markus Haarlaender
   * @return boolean
   */
  public boolean getShowCiteItem() {
    if (this.getPubItem().getPublicStatus().equals(State.WITHDRAWN)) {
      return false;
    }

    return this.getPubItem().getVersion().getState().equals(State.RELEASED);
  }

  public String getDates() {
    final List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
    pubItemList.add(this.getPubItem());
    final List<PubItemVOPresentation> pubItemPresentationList =
        CommonUtils.convertToPubItemVOPresentationList(pubItemList);
    final PubItemVOPresentation pubItemPresentation = pubItemPresentationList.get(0);

    return pubItemPresentation.getDatesAsString();
  }

  /**
   * Returns false if all dates are empty
   * 
   * @author Markus Haarlaender
   * @return boolean
   */
  public boolean getShowDates() {
    return ((this.getPubItem().getMetadata().getDatePublishedInPrint() != null && !this
        .getPubItem().getMetadata().getDatePublishedInPrint().equals(""))
        || (this.getPubItem().getMetadata().getDatePublishedOnline() != null && !this.getPubItem()
            .getMetadata().getDatePublishedOnline().equals(""))
        || (this.getPubItem().getMetadata().getDateAccepted() != null && !this.getPubItem()
            .getMetadata().getDateAccepted().equals(""))
        || (this.getPubItem().getMetadata().getDateSubmitted() != null && !this.getPubItem()
            .getMetadata().getDateSubmitted().equals(""))
        || (this.getPubItem().getMetadata().getDateModified() != null && !this.getPubItem()
            .getMetadata().getDateModified().equals("")) || (this.getPubItem().getMetadata()
        .getDateCreated() != null && !this.getPubItem().getMetadata().getDateCreated().equals("")));
  }

  /**
   * Returns a true or a false according to the invited state of the item
   * 
   * @return boolean
   */
  public boolean getInvited() {
    if (this.pubItem.getMetadata().getEvent().getInvitationStatus() != null) {
      if (this.pubItem.getMetadata().getEvent().getInvitationStatus()
          .equals(EventVO.InvitationStatus.INVITED)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns a true or a false according to the state of the current item
   * 
   * @return boolean
   */
  public boolean getItemIsWithdrawn() {
    if (this.pubItem.getVersion().getState().equals(State.WITHDRAWN)) {
      return true;
    }

    return false;
  }

  /**
   * Returns the formatted withdrawal date as string
   * 
   * @return String formatted withdrawal date
   */
  public String getWithdrawalDate() {
    String date = "";
    if (this.pubItem.getPublicStatus().equals(State.WITHDRAWN)) {
      if (this.pubItem.getModificationDate() != null) {
        date = CommonUtils.format(this.pubItem.getModificationDate());
      }
    }

    return date;
  }

  /**
   * Gets the name of the Collection the item belongs to.
   * 
   * @return String formatted Collection name
   */
  public String getContextName() {
    String contextName = "";
    if (this.context == null) {
      try {
        this.context =
            this.getItemControllerSessionBean().retrieveContext(
                this.pubItem.getContext().getObjectId());
      } catch (final Exception e) {
        ViewItemFull.logger.error("Error retrieving context", e);
      }
    }

    if (this.context != null) {
      contextName = this.context.getName();
    }

    return contextName;
  }

  public AccountUserRO getOwner() {

    // System.out.println(this.pubItem.getOwner().getTitle());
    // System.out.println(this.pubItem.getOwner().getObjectId());
    return this.pubItem.getOwner();
  }

  /**
   * Returns the Context the item belongs to
   */
  public ContextVO getContext() {
    if (this.context == null) {
      this.context = this.getItemControllerSessionBean().getCurrentContext();
    }

    return this.context;
  }

  /**
   * Gets the affiliation of the context the item belongs to.
   * 
   * @return String formatted context name
   */
  public String getAffiliations() {
    final StringBuffer affiliations = new StringBuffer();
    List<AffiliationRO> affiliationRefList = new ArrayList<AffiliationRO>();
    final List<AffiliationVOPresentation> affiliationList =
        new ArrayList<AffiliationVOPresentation>();
    if (this.context == null) {
      try {
        this.context =
            this.getItemControllerSessionBean().retrieveContext(
                this.pubItem.getContext().getObjectId());
      } catch (final Exception e) {
        ViewItemFull.logger.error("Error retrieving collection", e);
      }
    }
    if (this.context != null) {
      affiliationRefList = this.context.getResponsibleAffiliations();
    }
    // first get all affiliations
    if (affiliationRefList != null) {
      for (int i = 0; i < affiliationRefList.size(); i++) {
        try {
          affiliationList.add(new AffiliationVOPresentation(ApplicationBean.INSTANCE
              .getOrganizationService().get(affiliationRefList.get(i).getObjectId(), null)));
        } catch (final Exception e) {
          ViewItemFull.logger.error("Error retrieving affiliation list", e);
        }
      }
    }
    // then extract the names and add to StringBuffer
    for (int i = 0; i < affiliationList.size(); i++) {
      affiliations.append(affiliationList.get(i).getDetails().getName());
      if (i < affiliationList.size() - 1) {
        affiliations.append(", ");
      }
    }

    return affiliations.toString();
  }

  /**
   * Returns the name of the specified OU its authors will be shown
   * 
   * @return String name of the specified OU (escidoc.pubman_presentation.overview_page.authors_ou)
   */
  public String getSpecificOrganization() {
    final String rootOrganization =
        PropertyReader.getProperty("escidoc.pubman_presentation.overview_page.authors_ou").trim();

    if (rootOrganization != null) {
      return rootOrganization;
    }

    return "";
  }

  /**
   * Returns a formatted String including the start and the end date of the event
   * 
   * @return String the formatted date string
   */
  public String getStartEndDate() {
    final StringBuffer date = new StringBuffer();
    if (this.pubItem.getMetadata().getEvent().getStartDate() != null) {
      date.append(this.pubItem.getMetadata().getEvent().getStartDate());
    }
    if (this.pubItem.getMetadata().getEvent().getEndDate() != null) {
      date.append(" - ");
      date.append(this.pubItem.getMetadata().getEvent().getEndDate());
    }

    return date.toString();
  }

  /**
   * Returns the item modifier (last)
   * 
   * @return String name or id of the owner
   */
  public String getModificationDate() {
    return CommonUtils.formatTimestamp(this.pubItem.getModificationDate());
  }

  public AccountUserRO getLatestModifier() throws Exception {
    /*
     * if (this.latestModifier == null && this.pubItem.getVersion().getModifiedByRO() != null &&
     * this.pubItem.getVersion().getModifiedByRO().getObjectId() != null) { try {
     * this.latestModifier = this.getItemControllerSessionBean().retrieveUserAccount(
     * this.pubItem.getVersion().getModifiedByRO().getObjectId()); } catch (final Exception e) {
     * ViewItemFull.logger.error("Error retrieving latest modifier", e); } }
     */

    return this.pubItem.getVersion().getModifiedByRO();
  }

  /**
   * Returns the Creation date as formatted String (YYYY-MM-DD)
   * 
   * @return String the formatted date of modification
   */
  public String getCreationDate() {
    return CommonUtils.formatTimestamp(this.pubItem.getCreationDate());
  }

  /**
   * Navigates to the release history page.
   * 
   * @return the faces navigation string
   */
  public String showReleaseHistory() {
    this.getItemVersionListSessionBean().resetVersionLists();

    return ReleaseHistory.LOAD_RELEASE_HISTORY;
  }

  public PubItemVO getPubItem() {
    return this.pubItem;
  }

  public void setPubItem(PubItemVOPresentation pubItem) {
    this.pubItem = pubItem;
  }

  public ArrayList<AbstractVO> getAbstracts() {
    final ArrayList<AbstractVO> abstracts = new ArrayList<AbstractVO>();
    if (this.pubItem.getMetadata().getAbstracts() != null) {
      for (int i = 0; i < this.pubItem.getMetadata().getAbstracts().size(); i++) {
        abstracts.add(new AbstractVO(this.pubItem.getMetadata().getAbstracts().get(i).getValue()));
      }
    }
    return abstracts;
  }

  public boolean getHasAbstracts() {
    return !this.pubItem.getMetadata().getAbstracts().isEmpty()
        && this.pubItem.getMetadata().getAbstracts().size() > 0;
  }

  public boolean getHasSubjects() {
    boolean hasNotEmptySubjects = false;
    for (final SubjectVO subject : this.pubItem.getMetadata().getSubjects()) {
      if (subject != null && subject.getValue() != null && !("").equals(subject.getValue().trim())) {
        hasNotEmptySubjects = true;
        return hasNotEmptySubjects;
      }
    }
    return hasNotEmptySubjects;
  }

  public boolean getHasFreeKeywords() {
    return this.pubItem.getMetadata().getFreeKeywords() != null
        && this.pubItem.getMetadata().getFreeKeywords().length() > 0;
  }

  public String getGenre() {
    String genre = "";
    if (this.pubItem.getMetadata().getGenre() != null) {
      genre =
          this.getLabel(this.getI18nHelper().convertEnumToString(
              this.pubItem.getMetadata().getGenre()));
    }

    return genre;
  }

  public String getReviewMethod() {
    String reviewMethod = "";
    if (this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getReviewMethod() != null) {
      reviewMethod =
          this.getLabel(this.getI18nHelper().convertEnumToString(
              this.pubItem.getMetadata().getReviewMethod()));
    }

    return reviewMethod;
  }

  public String getDegreeType() {
    String degreeType = "";
    if (this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getDegree() != null) {
      degreeType =
          this.getLabel(this.getI18nHelper().convertEnumToString(
              this.pubItem.getMetadata().getDegree()));
    }

    return degreeType;
  }

  public String getItemState() {
    String itemState = "";
    if (this.pubItem.getVersion().getState() != null) {
      itemState =
          this.getLabel(this.getI18nHelper().convertEnumToString(
              this.pubItem.getVersion().getState()));
    }

    return itemState;
  }

  /**
   * checks if the current item and user are cappable for creating a DOI (moderator, released and
   * some needed Metadata)
   * 
   * @return if a doi can be created for this item
   */
  public boolean isDoiCappable() {
    return (this.isModerator && DoiRestService.isDoiReady(this.getPubItem()));
  }

  public String getCitationURL() {
    return this.citationURL;
  }

  public ArrayList<String> getOrganizationArray() {
    return this.organizationArray;
  }

  public void setOrganizationArray(ArrayList<String> organizationArray) {
    this.organizationArray = organizationArray;
  }

  public ArrayList<ViewItemOrganization> getOrganizationList() {
    return this.organizationList;
  }

  public void setOrganizationList(ArrayList<ViewItemOrganization> organizationList) {
    this.organizationList = organizationList;
  }

  public List<OrganizationVO> getAffiliatedOrganizationsList() {
    return this.affiliatedOrganizationsList;
  }

  public void setAffiliatedOrganizationsList(List<OrganizationVO> affiliatedOrganizationsList) {
    this.affiliatedOrganizationsList = affiliatedOrganizationsList;
  }

  public int getCreatorArraySize() {
    return this.getCreators().size();
  }

  public List<SourceBean> getSourceList() {
    return this.sourceList;
  }

  public void setSourceList(List<SourceBean> sourceList) {
    this.sourceList = sourceList;
  }

  public void setCitationURL(String citationURL) {
    this.citationURL = citationURL;
  }

  public boolean getIsStateWithdrawn() {
    return this.getPubItem().getPublicStatus().equals(State.WITHDRAWN);
  }

  public void setStateWithdrawn(boolean isStateWithdrawn) {
    this.isStateWithdrawn = isStateWithdrawn;
  }

  public boolean getIsDepositor() {
    return this.isDepositor;
  }

  public void setDepositor(boolean isDepositor) {
    this.isDepositor = isDepositor;
  }

  public boolean getIsModerator() {
    return this.isModerator;
  }

  public void setModerator(boolean isModerator) {
    this.isModerator = isModerator;
  }

  public boolean getIsPrivilegedViewer() {
    return this.isPrivilegedViewer;
  }

  public void setPrivilegedViewer(boolean isPrivilegedViewer) {
    this.isPrivilegedViewer = isPrivilegedViewer;
  }

  public boolean getIsLoggedIn() {
    return this.isLoggedIn;
  }

  public void setLoggedIn(boolean isLoggedIn) {
    this.isLoggedIn = isLoggedIn;
  }

  public boolean getIsLatestVersion() {
    return this.isLatestVersion;
  }

  public void setLatestVersion(boolean isLatestVersion) {
    this.isLatestVersion = isLatestVersion;
  }

  public boolean getIsLatestRelease() {
    return this.isLatestRelease;
  }

  public void setLatestRelease(boolean isLatestRelease) {
    this.isLatestRelease = isLatestRelease;
  }

  public boolean getIsStateSubmitted() {
    return this.isStateSubmitted;
  }

  public void setStateSubmitted(boolean isStateSubmitted) {
    this.isStateSubmitted = isStateSubmitted;
  }

  public boolean getIsStateReleased() {
    return this.isStateReleased;
  }

  public void setStateReleased(boolean isStateReleased) {
    this.isStateReleased = isStateReleased;
  }

  public boolean getIsStatePending() {
    return this.isStatePending;
  }

  public void setStatePending(boolean isStatePending) {
    this.isStatePending = isStatePending;
  }

  public boolean getIsOwner() {
    return this.isOwner;
  }

  public void setOwner(boolean isOwner) {
    this.isOwner = isOwner;
  }

  public boolean getIsModifyDisabled() {
    return this.isModifyDisabled;
  }

  public void setModifyDisabled(boolean isModifyDisabled) {
    this.isModifyDisabled = isModifyDisabled;
  }

  public boolean getIsCreateNewRevisionDisabled() {
    return this.isCreateNewRevisionDisabled;
  }

  public void setCreateNewRevisionDisabled(boolean isCreateNewRevisionDisabled) {
    this.isCreateNewRevisionDisabled = isCreateNewRevisionDisabled;
  }

  public boolean getHasAudience() {
    if (this.pubItem != null
        && (this.pubItem.getVersion().getState() == State.RELEASED || this.pubItem.getVersion()
            .getState() == State.SUBMITTED) && (this.getIsModerator() || this.getIsDepositor())) {

      for (final FileVO file : this.pubItem.getFiles()) {
        if (file.getVisibility() == Visibility.AUDIENCE) {
          return true;
        }
      }
    }

    return false;
  }

  public String reviseItem() {
    return ReviseItem.LOAD_REVISEITEM;
  }

  public boolean getIsStateInRevision() {
    return this.isStateInRevision;
  }

  public void setStateInRevision(boolean isStateInRevision) {
    this.isStateInRevision = isStateInRevision;
  }

  public String getItemPublicState() {
    String itemState = "";
    if (this.pubItem.getPublicStatus() != null) {
      itemState =
          this.getLabel(this.getI18nHelper().convertEnumToString(this.pubItem.getPublicStatus()));
    }

    return itemState;
  }

  public String getUnapiURLdownload() {
    return this.unapiURLdownload;
  }

  public void setUnapiURLdownload(String unapiURLdownload) {
    this.unapiURLdownload = unapiURLdownload;
  }

  public String getUnapiEscidoc() {
    return this.unapiEscidoc;
  }

  public void setUnapiEscidoc(String unapiEscidoc) {
    this.unapiEscidoc = unapiEscidoc;
  }

  public String getUnapiEndnote() {
    return this.unapiEndnote;
  }

  public void setUnapiEndnote(String unapiEndnote) {
    this.unapiEndnote = unapiEndnote;
  }

  public String getUnapiBibtex() {
    return this.unapiBibtex;
  }

  public void setUnapiBibtex(String unapiBibtex) {
    this.unapiBibtex = unapiBibtex;
  }

  public String getUnapiApa() {
    return this.unapiApa;
  }

  public void setUnapiApa(String unapiApa) {
    this.unapiApa = unapiApa;
  }

  public String getUnapiURLview() {
    return this.unapiURLview;
  }

  public void setUnapiURLview(String unapiURLview) {
    this.unapiURLview = unapiURLview;
  }

  public String addToBasket() {
    if (!this.getPubItemStorageSessionBean().getStoredPubItems()
        .containsKey(this.getPubItem().getVersion().getObjectIdAndVersion())) {
      this.getPubItemStorageSessionBean().getStoredPubItems()
          .put(this.pubItem.getVersion().getObjectIdAndVersion(), this.pubItem.getVersion());
      this.info(this.getMessage("basket_SingleAddedSuccessfully"));
    } else {
      FacesBean.error(this.getMessage("basket_SingleAlreadyInBasket"));
    }
    this.canAddToBasket = false;
    this.canDeleteFromBasket = true;

    return "";
  }

  public String removeFromBasket() {
    this.getPubItemStorageSessionBean().getStoredPubItems()
        .remove(this.pubItem.getVersion().getObjectIdAndVersion());
    this.info(this.getMessage("basket_SingleRemovedSuccessfully"));
    this.canAddToBasket = true;
    this.canDeleteFromBasket = false;

    return "";
  }

  public boolean getIsInBasket() {
    if (this.pubItem == null) {
      return false;
    }

    return this.getPubItemStorageSessionBean().getStoredPubItems()
        .containsKey(this.pubItem.getVersion().getObjectIdAndVersion());
  }

  public String getLinkForActionsView() {
    return "ViewItemFullPage.jsp?" + ViewItemFull.PARAMETERNAME_ITEM_ID + "="
        + this.getPubItem().getVersion().getObjectIdAndVersion() + "&"
        + ViewItemFull.PARAMETERNAME_MENU_VIEW + "=ACTIONS";
  }

  public String getLinkForExportView() {
    return "ViewItemFullPage.jsp?" + ViewItemFull.PARAMETERNAME_ITEM_ID + "="
        + this.getPubItem().getVersion().getObjectIdAndVersion() + "&"
        + ViewItemFull.PARAMETERNAME_MENU_VIEW + "=EXPORT";
  }

  /**
   * Invokes the email service to send per email the the page with the selected items as attachment.
   * This method is called when the user selects one or more items and then clicks on the
   * EMail-Button in the Export-Items Panel.
   * 
   * @author: StG
   */
  public String exportEmail() {
    final List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
    pubItemList.add(this.getPubItem());
    final ExportFormatVO curExportFormat = this.getExportItemsSessionBean().getCurExportFormatVO();
    byte[] exportFileData;

    try {
      exportFileData =
          this.getItemControllerSessionBean().retrieveExportData(curExportFormat, pubItemList);
    } catch (final TechnicalException e) {
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
      return ErrorPage.LOAD_ERRORPAGE;
    }

    if ((exportFileData == null) || (new String(exportFileData)).trim().equals("")) {
      FacesBean.error(this.getMessage(ExportItems.MESSAGE_NO_EXPORTDATA_DELIVERED));
      return "";
    }

    // YEAR + MONTH + DAY_OF_MONTH
    final Calendar rightNow = Calendar.getInstance();
    final String date =
        rightNow.get(Calendar.YEAR) + "-" + rightNow.get(Calendar.DAY_OF_MONTH) + "-"
            + rightNow.get(Calendar.MONTH) + "_";

    // create an attachment temp file from the byte[] stream
    File exportAttFile;
    try {
      exportAttFile =
          File.createTempFile("eSciDoc_Export_" + curExportFormat.getName() + "_" + date, "."
              + FileFormatVO.getExtensionByName(curExportFormat.getSelectedFileFormat().getName()));
      final FileOutputStream fos = new FileOutputStream(exportAttFile);
      fos.write(exportFileData);
      fos.close();
    } catch (final IOException e1) {
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e1);
      return ErrorPage.LOAD_ERRORPAGE;
    }

    this.getExportItemsSessionBean().setExportEmailTxt(
        this.getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_TEXT));
    this.getExportItemsSessionBean().setAttExportFileName(exportAttFile.getName());
    this.getExportItemsSessionBean().setAttExportFile(exportAttFile);
    this.getExportItemsSessionBean().setExportEmailSubject(
        this.getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT) + ": "
            + exportAttFile.getName());
    // hier call set the values on the exportEmailView - attachment file, subject, ....

    return "displayExportEmailPage";
  }

  /**
   * Downloads the page with the selected items as export. This method is called when the user
   * selects one or more items and then clicks on the Download-Button in the Export-Items Panel.
   * 
   * @author: StG
   */
  public String exportDownload() {
    final List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
    pubItemList.add(this.getPubItem());
    // export format and file format.
    final ExportFormatVO curExportFormat = this.getExportItemsSessionBean().getCurExportFormatVO();
    byte[] exportFileData = null;
    try {
      exportFileData =
          this.getItemControllerSessionBean().retrieveExportData(curExportFormat, pubItemList);
    } catch (final Exception e) {
      throw new RuntimeException("Cannot export item:", e);
    }

    final String contentType = curExportFormat.getSelectedFileFormat().getMimeType();
    FacesTools.getResponse().setContentType(contentType);
    final String fileName =
        "export_" + curExportFormat.getName().toLowerCase() + "."
            + FileFormatVO.getExtensionByName(this.getExportItemsSessionBean().getFileFormat());
    FacesTools.getResponse().setHeader("Content-disposition", "attachment; filename=" + fileName);
    try {
      final OutputStream out = FacesTools.getResponse().getOutputStream();
      out.write(exportFileData);
      out.flush();
      out.close();
    } catch (final Exception e) {
      throw new RuntimeException("Cannot put export result in HttpResponse body:", e);
    }
    FacesTools.getCurrentInstance().responseComplete();

    return "";
  }

  /**
   * This method returns the contact email address of the moderator strored in the item's context.
   * If it is empty the pubman support address will be returned.
   * 
   * @return the moderator's email address (if available, otherwise pubman support address)
   */
  public String getModeratorContactEmail() {
    String contactEmail = "";
    contactEmail = this.getContext().getAdminDescriptor().getContactEmail();
    if (contactEmail == null || contactEmail.trim().equals("")) {
      contactEmail = ViewItemFull.ALTERNATIVE_MODERATOR_EMAIL;
    }

    return contactEmail;
  }

  public void setPublicStateReleased(boolean isPublicStateReleased) {
    this.isPublicStateReleased = isPublicStateReleased;
  }

  public boolean getIsPublicStateReleased() {
    return this.isPublicStateReleased;
  }

  public String getFwUrl() {
    return this.fwUrl;
  }

  public void setFwUrl(String fwUrl) {
    this.fwUrl = fwUrl;
  }

  public String getItemPattern() {
    return this.itemPattern;
  }

  public void setItemPattern(String itemPattern) {
    this.itemPattern = itemPattern;
  }

  /**
   * Returns a true or a false according to the existance of an legal case in the item
   * 
   * @return boolean
   */
  public boolean getShowLegalCase() {
    if (this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getLegalCase() != null) {
      return true;
    }

    return false;
  }

  /**
   * Returns a String with the legal case data according to the existance of an legal case in the
   * item
   * 
   * @return boolean
   */
  public String getLegalCaseCourtDateId() {
    final StringBuffer legalCaseString = new StringBuffer();
    if (this.pubItem.getMetadata().getLegalCase().getCourtName() != "") {
      legalCaseString.append(this.pubItem.getMetadata().getLegalCase().getCourtName());
    }
    if (this.pubItem.getMetadata().getLegalCase().getDatePublished() != "") {
      if (legalCaseString.length() != 0) {
        legalCaseString.append(", ");
      }
      legalCaseString.append(this.pubItem.getMetadata().getLegalCase().getDatePublished());
    }
    if (this.pubItem.getMetadata().getLegalCase().getIdentifier() != "") {
      if (legalCaseString.length() != 0) {
        legalCaseString.append(" - ");
      }
      legalCaseString.append(this.pubItem.getMetadata().getLegalCase().getIdentifier());
    }

    return legalCaseString.toString();
  }

  public void setDefaultSize(int defaultSize) {
    this.defaultSize = defaultSize;
  }

  public int getDefaultSize() {
    return this.defaultSize;
  }

  public ArrayList<ViewItemCreators> getCreators() {
    return this.creators;
  }

  public void setCreators(ArrayList<ViewItemCreators> creators) {
    this.creators = creators;
  }


  public void setLatestVersionURL(String latestVersionURL) {
    this.latestVersionURL = latestVersionURL;
  }

  public String getLatestVersionURL() {
    return this.latestVersionURL;
  }

  public String getCitationHtml() {
    try {
      final List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
      pubItemList.add(new PubItemVO(this.getPubItem()));

      final ExportFormatVO expFormat = new ExportFormatVO();
      expFormat.setFormatType(ExportFormatVO.FormatType.LAYOUT);

      // Use special apa style if language is set to japanese
      boolean isJapanese = false;

      if (this.getPubItem().getMetadata().getLanguages() != null) {
        for (final String lang : this.getPubItem().getMetadata().getLanguages()) {
          if ("jpn".equals(lang)) {
            isJapanese = true;
            break;
          }
        }
      }

      if (isJapanese || "ja".equalsIgnoreCase(this.getI18nHelper().getLocale())) {
        expFormat.setName("APA(CJK)");
      } else {
        expFormat.setName("APA6");
      }

      final FileFormatVO fileFormat = new FileFormatVO();
      fileFormat.setMimeType(FileFormatVO.HTML_PLAIN_MIMETYPE);
      fileFormat.setName(FileFormatVO.HTML_PLAIN_NAME);

      expFormat.setSelectedFileFormat(fileFormat);

      byte[] exportFileData = null;
      exportFileData = ItemExportingService.getOutput(expFormat, pubItemList);

      final String exportHtml = new String(exportFileData, "UTF-8");
      try {
        final Pattern p =
            Pattern.compile("(?<=\\<body\\>).*(?=\\<\\/body\\>)", Pattern.CASE_INSENSITIVE
                | Pattern.DOTALL);
        final Matcher m = p.matcher(exportHtml);
        m.find();
        final String match = m.group();
        if (match != null) {
          return match;
        }
      } catch (final Exception e) {
        ViewItemFull.logger.debug("Match in citation html not found", e);
      }

      return "";
    } catch (final Exception e) {
      throw new RuntimeException("Cannot export item:", e);
    }
  }

  public String getResolveHandleService() {
    return ViewItemFull.RESOLVE_HANDLE_SERVICE;
  }

  public boolean getIsMemberOfYearbook() {
    return this.isMemberOfYearbook;
  }

  public boolean getIsCandidateOfYearbook() {
    return this.isCandidateOfYearbook;
  }

  public void setMemberOfYearbook(boolean isMemberOfYearbook) {
    this.isMemberOfYearbook = isMemberOfYearbook;
  }

  public void setCandidateOfYearbook(boolean isCandidateOfYearbook) {
    this.isCandidateOfYearbook = isCandidateOfYearbook;
  }

  private void setLinks() {
    this.isModifyDisabled =
        this.getRightsManagementSessionBean().isDisabled(
            RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "."
                + ViewItemFull.FUNCTION_MODIFY);
    this.isCreateNewRevisionDisabled =
        this.getRightsManagementSessionBean().isDisabled(
            RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "."
                + ViewItemFull.FUNCTION_NEW_REVISION);

    if (((this.isStatePending || this.isStateInRevision) && this.isLatestVersion && this.isOwner)
        || (this.isStateSubmitted && this.isLatestVersion && this.isModerator)) {
      this.canEdit = true;
    }

    if ((this.isStatePending || this.isStateInRevision)
        && this.isLatestVersion && this.isOwner && this.isWorkflowStandard) {
      this.canSubmit = true;
    }

    if (this.isOwner
        && this.isLatestVersion
        && (((this.isStatePending || this.isStateSubmitted) && this.isWorkflowSimple) || (this.isWorkflowStandard
            && this.isModerator && this.isStateSubmitted))) {
      this.canRelease = true;

    }

    if (this.isStateSubmitted && this.isLatestVersion && this.isModerator
        && !this.isOwner && !this.isModifyDisabled) {
      this.canAccept = true;
    }

    if (this.isStateSubmitted && this.isLatestVersion && this.isModerator
            && !this.isModifyDisabled && this.isWorkflowStandard && !this.isPublicStateReleased) {
      this.canRevise = true;
    }

    if (!this.isPublicStateReleased
        && (this.isStatePending || this.isStateInRevision) && this.isLatestVersion && this.isOwner) {
      this.canDelete = true;
    }

    if (((this.isStateReleased || this.isStateWasReleased) && this.isLatestVersion)
        && (this.isOwner || this.isModerator)) {
      this.canWithdraw = true;
    }

    if (this.isStateReleased && this.isLatestVersion
        && !this.isModifyDisabled && (this.isModerator || this.isOwner)) {
      this.canModify = true;
    }

    if (this.isStateReleased && this.isLatestRelease
        && !this.isCreateNewRevisionDisabled && this.isDepositor) {
      this.canCreateNewRevision = true;
    }

    if (!this.isStateWithdrawn && this.isLatestVersion && !this.isCreateNewRevisionDisabled
        && this.isDepositor) {
      this.canCreateFromTemplate = true;
    }

    if (!this.isStateWithdrawn && !this.getIsInBasket()) {
      this.canAddToBasket = true;
    }

    if (!this.isStateWithdrawn && this.getIsInBasket()) {
      this.canDeleteFromBasket = true;
    }

    if (this.isLatestVersion && !this.isStateWithdrawn) {
      this.canViewLocalTags = true;
    }

    if (this.getHasAudience() && !this.isStateWithdrawn) {
      this.canManageAudience = true;
    }

    if (this.isLatestVersion && !this.isStateWithdrawn && this.isLoggedIn
        && (this.isOwner || this.isModerator)) {
      this.canShowItemLog = true;
    }

    if (this.isLatestRelease && !this.isStateWithdrawn) {
      this.canShowStatistics = true;
      this.canShowRevisions = true;
    }

    if (this.pubItem != null && (!this.isStateWithdrawn && this.isLatestRelease)
        || (this.isStateWithdrawn && this.pubItem.getVersion().getVersionNumber() > 1)) {
      this.canShowReleaseHistory = true;
    }

    if (this.pubItem != null && this.pubItem.getVersion().getLastMessage() != null
        && !this.pubItem.getVersion().getLastMessage().contentEquals("")) {
      this.canShowLastMessage = true;
    }
  }

  public boolean isCanEdit() {
    return this.canEdit;
  }

  public boolean isCanSubmit() {
    return this.canSubmit;
  }

  public boolean isCanRelease() {
    return this.canRelease;
  }

  public boolean isCanAccept() {
    return this.canAccept;
  }

  public boolean isCanRevise() {
    return this.canRevise;
  }

  public boolean isCanDelete() {
    return this.canDelete;
  }

  public boolean isCanWithdraw() {
    return this.canWithdraw;
  }

  public boolean isCanModify() {
    return this.canModify;
  }

  public boolean isCanCreateNewRevision() {
    return this.canCreateNewRevision;
  }

  public boolean isCanCreateFromTemplate() {
    return this.canCreateFromTemplate;
  }

  public boolean isCanAddToBasket() {
    return this.canAddToBasket;
  }

  public boolean isCanDeleteFromBasket() {
    return this.canDeleteFromBasket;
  }

  public boolean isCanViewLocalTags() {
    return this.canViewLocalTags;
  }

  public boolean isCanManageAudience() {
    return this.canManageAudience;
  }

  public boolean isCanShowItemLog() {
    return this.canShowItemLog;
  }

  public boolean isCanShowStatistics() {
    return this.canShowStatistics;
  }

  public boolean isCanShowRevisions() {
    return this.canShowRevisions;
  }

  public boolean isCanShowReleaseHistory() {
    return this.canShowReleaseHistory;
  }

  public boolean isCanShowLastMessage() {
    return this.canShowLastMessage;
  }

  public String getHtmlMetaTags() {
    try {
      final String itemXml = XmlTransformingService.transformToItem(new PubItemVO(this.pubItem));
      ItemTransformingService itemTransformingService = new ItemTransformingServiceImpl();

      final String resHighwire =
          itemTransformingService.transformFromTo(FORMAT.ESCIDOC_ITEM_V3_XML,
              FORMAT.HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML, itemXml);

      final String resDC =
          itemTransformingService.transformFromTo(FORMAT.ESCIDOC_ITEM_V3_XML,
              FORMAT.HTML_METATAGS_DC_XML, itemXml);

      final String result = resHighwire + resDC;

      return result;
    } catch (final Exception e1) {
      ViewItemFull.logger.error("could not create html metatags", e1);
    }

    return null;
  }

  public String addDoi() {
    String navigationRuleWhenSucces = ViewItemFull.LOAD_VIEWITEM;
    String retVal = "";

    try {
      // get a new DOI including a consistency check
      final String doi = DoiRestService.getNewDoi(this.getPubItem());
      this.getPubItem().getMetadata().getIdentifiers().add(new IdentifierVO(IdType.DOI, doi));

      final ItemControllerSessionBean icsb = this.getItemControllerSessionBean();
      retVal = icsb.saveCurrentPubItem(navigationRuleWhenSucces);

      if (retVal.equals(navigationRuleWhenSucces)) {
        retVal =
            icsb.submitCurrentPubItem("Submission during adding DOI.", ViewItemFull.LOAD_VIEWITEM);

        if (retVal.equals(navigationRuleWhenSucces)) {
          retVal =
              icsb.acceptCurrentPubItem("Release during adding DOI", ViewItemFull.LOAD_VIEWITEM);
        }
      }

      if (retVal.equals(navigationRuleWhenSucces)) {
        this.info(this.getMessage("ViewItem_doiAddedSuccessfully"));
      } else {
        FacesBean.error(this.getMessage("ViewItem_doiAddingProblem"));
      }

      this.getPubItemListSessionBean().update();
    } catch (final Exception e) {
      ViewItemFull.logger.error("Error creating new DOI", e);
      FacesBean.error(this.getMessage("ViewItem_doiAddingProblem") + "--\n" + e.getMessage());
    }

    return retVal;
  }

  public String addSsrnTag() {
    this.getPubItem().getLocalTags().add(ViewItemFull.SSRN_LOCAL_TAG);

    return this.getSsrnReturnValue("Submission during adding SSRN-Tag.",
        "ViewItem_ssrnAddedSuccessfully", "ViewItem_ssrnAddingProblem");
  }

  public String removeSsrnTag() {
    this.getPubItem().getLocalTags().remove(ViewItemFull.SSRN_LOCAL_TAG);

    return this.getSsrnReturnValue("Submission during removing SSRN-Tag.",
        "ViewItem_ssrnRemovedSuccessfully", "ViewItem_ssrnRemovingProblem");
  }

  private String getSsrnReturnValue(String messageSubmit, String messageSuccess, String messageError) {
    String navigationRuleWhenSucces = ViewItemFull.LOAD_VIEWITEM;

    final State state = this.getPubItem().getVersion().getState();

    if (this.isModerator && State.RELEASED.equals(state)) {
      navigationRuleWhenSucces = AcceptItem.LOAD_ACCEPTITEM;
    } else if (State.SUBMITTED.equals(state) || State.RELEASED.equals(state)) {
      navigationRuleWhenSucces = SubmitItem.LOAD_SUBMITITEM;
    }

    final ItemControllerSessionBean icsb = this.getItemControllerSessionBean();
    String retVal = "";
    try {
      retVal = icsb.saveCurrentPubItem(navigationRuleWhenSucces);

      if (AcceptItem.LOAD_ACCEPTITEM.equals(retVal)
          && AcceptItem.LOAD_ACCEPTITEM.equals(navigationRuleWhenSucces)) {
        retVal = icsb.submitCurrentPubItem(messageSubmit, navigationRuleWhenSucces);
      }

      if (retVal.equals(navigationRuleWhenSucces)) {
        this.info(this.getMessage(messageSuccess));
      } else {
        FacesBean.error(this.getMessage(messageError));
      }

      this.getPubItemListSessionBean().update();
    } catch (final Exception e) {
      ViewItemFull.logger.error("Problems with validation", e);
      FacesBean.error(this.getMessage("ViewItem_doiAddingProblem") + "--\n" + e.getMessage());
    }

    return retVal;
  }

  private AcceptItemSessionBean getAcceptItemSessionBean() {
    return (AcceptItemSessionBean) FacesTools.findBean("AcceptItemSessionBean");
  }

  private PubItemListSessionBean getPubItemListSessionBean() {
    return (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
  }

  private PubItemStorageSessionBean getPubItemStorageSessionBean() {
    return (PubItemStorageSessionBean) FacesTools.findBean("PubItemStorageSessionBean");
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  private ViewItemSessionBean getViewItemSessionBean() {
    return (ViewItemSessionBean) FacesTools.findBean("ViewItemSessionBean");
  }

  private WithdrawItemSessionBean getWithdrawItemSessionBean() {
    return (WithdrawItemSessionBean) FacesTools.findBean("WithdrawItemSessionBean");
  }

  private EditItemSessionBean getEditItemSessionBean() {
    return (EditItemSessionBean) FacesTools.findBean("EditItemSessionBean");
  }

  private RightsManagementSessionBean getRightsManagementSessionBean() {
    return (RightsManagementSessionBean) FacesTools.findBean("RightsManagementSessionBean");
  }

  private ItemVersionListSessionBean getItemVersionListSessionBean() {
    return (ItemVersionListSessionBean) FacesTools.findBean("ItemVersionListSessionBean");
  }

  private RelationListSessionBean getRelationListSessionBean() {
    return (RelationListSessionBean) FacesTools.findBean("RelationListSessionBean");
  }

  private ContextListSessionBean getCollectionListSessionBean() {
    return (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
  }

  private ExportItemsSessionBean getExportItemsSessionBean() {
    return (ExportItemsSessionBean) FacesTools.findBean("ExportItemsSessionBean");
  }
}
