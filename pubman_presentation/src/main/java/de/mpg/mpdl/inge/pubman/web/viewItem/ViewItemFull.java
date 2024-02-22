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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.MissingResourceException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationServiceException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.basket.PubItemStorageSessionBean;
import de.mpg.mpdl.inge.pubman.web.batch.PubItemBatchSessionBean;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItem;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.export.ExportItems;
import de.mpg.mpdl.inge.pubman.web.export.ExportItemsSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemLog.ViewItemLog;
import de.mpg.mpdl.inge.pubman.web.releaseItem.ReleaseItem;
import de.mpg.mpdl.inge.pubman.web.releases.ItemVersionListSessionBean;
import de.mpg.mpdl.inge.pubman.web.releases.ReleaseHistory;
import de.mpg.mpdl.inge.pubman.web.reviseItem.ReviseItem;
import de.mpg.mpdl.inge.pubman.web.submitItem.SubmitItem;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.ObjectFormatter;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.CreatorDisplay;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.withdrawItem.WithdrawItem;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.DoiRestService;
import de.mpg.mpdl.inge.service.pubman.impl.ItemTransformingServiceImpl;
import de.mpg.mpdl.inge.service.util.GrantUtil;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.util.ConeUtils;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.faces.bean.ManagedBean;

/**
 * Backing bean for ViewItemFull.jspf (for viewing items in a full context).
 *
 * @author Tobias Schraut, created 03.09.2007
 * @version: $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "ViewItemFull")
@SuppressWarnings("serial")
public class ViewItemFull extends FacesBean {
  private static final Logger logger = LogManager.getLogger(ViewItemFull.class);

  public static final String LOAD_VIEWITEM = "loadViewItem";

  private static final String SSRN_LOCAL_TAG = "Tag: SSRN";
  private static final String ISI_KNOWLEDGE_BASE_LINK =
      "http://gateway.isiknowledge.com/gateway/Gateway.cgi?GWVersion=2&SrcAuth=SFX&SrcApp=SFX&DestLinkType=FullRecord&KeyUT=";
  private static final String ISI_KNOWLEDGE_DEST_APP = "&DestApp=WOS";
  private static final String PARAMETERNAME_ITEM_ID = "itemId";
  private static final String PARAMETERNAME_MENU_VIEW = "view";
  private static final String VALIDATION_ERROR_MESSAGE = "depositorWS_NotSuccessfullySubmitted";

  private ArrayList<String> organizationArray;
  private ArrayList<ViewItemCreators> creators;
  private ArrayList<ViewItemOrganization> organizationList;

  private ContextDbVO context = null;

  private List<OrganizationVO> affiliatedOrganizationsList;
  private List<SourceBean> sourceList;
  private List<String> ssrnContexts;

  private PubItemVOPresentation pubItem = null;


  private String itemPattern;
  private String citationURL;
  private String citationObjectUrl;


  private String languages;
  private String latestVersionURL;

  // private String unapiURLdownload;
  private String unapiURLview;
  // private String unapiEscidoc;
  // private String unapiEndnote;
  // private String unapiBibtex;
  // private String unapiApa;

  private int defaultSize = 20;

  private boolean canEdit = false;
  private boolean canSubmit = false;
  private boolean canRelease = false;
  // private boolean canAccept = false;
  private boolean canRevise = false;
  private boolean canDelete = false;
  private boolean canWithdraw = false;
  private boolean canModify = false;
  private boolean canCreateFromTemplate = false;
  private boolean canAddToBasket = false;
  private boolean canDeleteFromBasket = false;
  private boolean canViewLocalTags = false;
  // private boolean canManageAudience = false;
  private boolean canShowItemLog = false;
  private boolean canShowReleaseHistory = false;
  private boolean canShowLastMessage = false;

  private boolean isDepositor = false;
  private boolean isLatestRelease = false;
  private boolean isLatestVersion = false;
  private boolean isLoggedIn = false;
  private boolean isModerator = false;
  private boolean isOwner = false;
  private boolean isPublicStateReleased = false;
  private boolean isStateInRevision = false;
  private boolean isStatePending = false;
  private boolean isStateReleased = false;
  private boolean isStateSubmitted = false;
  private boolean isStateWithdrawn = false;
  private boolean isWorkflowSimple = false;

  private AccountUserDbVO owner;
  private AccountUserDbVO modifier;



  // private boolean isWorkflowStandard = false;

  public ViewItemFull() {
    this.init();
  }

  public void init() {
    // DetailedMode
    String viewId = FacesTools.getCurrentInstance().getViewRoot().getViewId();
    if ("/ViewItemOverviewPage.jsp".equals(viewId)) {
      this.getLoginHelper().setDetailedMode(false);
    } else if ("/ViewItemFullPage.jsp".equals(viewId)) {
      this.getLoginHelper().setDetailedMode(true);
    }

    // Try to get a pubitem either via the controller session bean or an URL Parameter
    String itemID = FacesTools.getRequest().getParameter(ViewItemFull.PARAMETERNAME_ITEM_ID);
    if (null != itemID) {
      try {
        this.pubItem = this.getItemControllerSessionBean().retrieveItem(itemID);
        if (null == this.pubItem) {
          this.error(this.getMessage("ViewItemFull_invalidID").replace("$1", itemID));
          return;
        }
        // if it is a new item reset ViewItemSessionBean
        if (null == this.getItemControllerSessionBean().getCurrentPubItem() || !this.pubItem.getObjectIdAndVersion()
            .equals(this.getItemControllerSessionBean().getCurrentPubItem().getObjectIdAndVersion())) {
          this.getViewItemSessionBean().itemChanged();
        }
        this.getItemControllerSessionBean().setCurrentPubItem(this.pubItem);
      } catch (AuthorizationException | AuthenticationException e) {
        if (this.getLoginHelper().isLoggedIn()) {
          this.error(this.getMessage("ViewItemFull_noPermission"));
        } else {
          getLoginHelper().logout();
        }
        return;
      } catch (IngeTechnicalException | IngeApplicationException e) {
        this.error(this.getMessage("ViewItemFull_invalidID").replace("$1", itemID), e.getMessage());
        return;
      }
    } else { // TODO: Dieser ELSE Teil ist aeusserst dubios!!!!
      ItemControllerSessionBean icsb = this.getItemControllerSessionBean();
      if (null == icsb.getCurrentPubItem()) {
        //        ViewItemFull.logger.warn("ViewItemFull: icsb.getCurrentPubItem() == null");
        return;
      }

      // Cleanup needed if an edit site was loaded inbetween
      // (e.g. local tags --> source without editors --> editors are created in the SourceBean and
      // not removed)
      PubItemUtil.cleanUpItem(icsb.getCurrentPubItem());
      this.pubItem = icsb.getCurrentPubItem();
    }

    if (null != this.getPubItem().getObjectIdAndVersion()) {

      // Citation url
      try {
        String pubmanUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
            + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH);
        if (!pubmanUrl.endsWith("/")) {
          pubmanUrl = pubmanUrl + "/";
        }
        this.itemPattern = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN).replaceAll("\\$1",
            this.getPubItem().getObjectIdAndVersion());
        if (this.itemPattern.startsWith("/")) {
          this.itemPattern = this.itemPattern.substring(1);
        }
        // private String fwUrl;
        String itemObjectPattern =
            PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN).replaceAll("\\$1", this.getPubItem().getObjectId());
        if (itemObjectPattern.startsWith("/")) {
          itemObjectPattern = itemObjectPattern.substring(1);
        }
        // MF: Removed exclusion of pending items here
        this.citationURL = pubmanUrl + this.itemPattern;
        this.citationObjectUrl = pubmanUrl + itemObjectPattern;

        if (null != this.getPubItem().getObject().getLatestVersion()
            && null != this.getPubItem().getObject().getLatestVersion().getObjectIdAndVersion()) {
          String latestVersionItemPattern = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN).replaceAll("\\$1",
              this.getPubItem().getObject().getLatestVersion().getObjectIdAndVersion());
          if (latestVersionItemPattern.startsWith("/")) {
            latestVersionItemPattern = latestVersionItemPattern.substring(1);
          }
          this.latestVersionURL = pubmanUrl + latestVersionItemPattern;
        }
      } catch (Exception e) {
        e.printStackTrace();
        this.citationURL = "";
      }

      // Owner
      this.isOwner = true;

      if (null != this.pubItem.getObject().getCreator()) {

        try {
          this.owner = ApplicationBean.INSTANCE.getUserAccountService().get(this.pubItem.getObject().getCreator().getObjectId(),
              this.getLoginHelper().getAuthenticationToken());
        } catch (Exception e) {

        }


        this.isOwner = (null != this.getLoginHelper().getAccountUser()
            ? this.getLoginHelper().getAccountUser().getObjectId().equals(this.getPubItem().getObject().getCreator().getObjectId())
            : false);

        if (null != this.getLoginHelper().getAccountUser() && null != this.getLoginHelper().getAccountUser().getGrantList()) {
          this.isModerator = GrantUtil.hasRole(this.getLoginHelper().getAccountUser(), GrantVO.PredefinedRoles.MODERATOR,
              this.getPubItem().getObject().getContext().getObjectId());
          this.isDepositor = GrantUtil.hasRole(this.getLoginHelper().getAccountUser(), GrantVO.PredefinedRoles.DEPOSITOR);

          if (!this.isOwner) {
            for (GrantVO grant : this.getLoginHelper().getAccountUser().getGrantList()) {
              //              if (grant.getRole().equals("escidoc:role-system-administrator")) {
              if (GrantVO.PredefinedRoles.SYSADMIN.frameworkValue().contentEquals(grant.getRole())) {
                this.isOwner = true;
                break;
              }
            }
          }
        }
      }

      if (null != this.getPubItem().getModifier()) {
        try {
          this.modifier = ApplicationBean.INSTANCE.getUserAccountService().get(this.pubItem.getModifier().getObjectId(),
              this.getLoginHelper().getAuthenticationToken());
        } catch (Exception e) {

        }
      }

      // Setting properties for Action Links
      this.isLoggedIn = this.getLoginHelper().isLoggedIn();

      this.isLatestVersion = this.getPubItem().getVersionNumber() == this.getPubItem().getObject().getLatestVersion().getVersionNumber();

      this.isLatestRelease = null != this.getPubItem().getObject().getLatestRelease()
          && this.getPubItem().getVersionNumber() == this.getPubItem().getObject().getLatestRelease().getVersionNumber();

      this.isPublicStateReleased = ItemVersionRO.State.RELEASED.equals(this.getPubItem().getObject().getPublicState());

      this.isStateWithdrawn = ItemVersionRO.State.WITHDRAWN.equals(this.getPubItem().getObject().getPublicState());
      if (this.isStateWithdrawn) {
        this.getViewItemSessionBean().itemChanged();
      }

      this.isStateSubmitted = ItemVersionRO.State.SUBMITTED.equals(this.getPubItem().getVersionState()) && !this.isStateWithdrawn;

      this.isStateReleased = ItemVersionRO.State.RELEASED.equals(this.getPubItem().getVersionState()) && !this.isStateWithdrawn;

      this.isStatePending = ItemVersionRO.State.PENDING.equals(this.getPubItem().getVersionState()) && !this.isStateWithdrawn;

      this.isStateInRevision = ItemVersionRO.State.IN_REVISION.equals(this.getPubItem().getVersionState()) && !this.isStateWithdrawn;

      // Warn message if the item version is not the latest
      if (false == this.isLatestVersion && this.getPubItem().getObject().getLatestVersion().getVersionNumber() != this.getPubItem()
          .getObject().getLatestRelease().getVersionNumber() && this.isLoggedIn) {
        String link = null;
        try {
          link = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH)
              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN).replaceAll("\\$1",
                  this.getPubItem().getObject().getLatestVersion().getObjectIdAndVersion());
        } catch (Exception e) {
          logger.error("Error when trying to access a property via PropertyReader", e);
        }
        this.warn(this.getMessage("itemIsNotLatestVersion") + "<br/><a href=\"" + (null != link ? link : "") + "\" >"
            + (null != link ? link : "") + "</a>");
      } else if (false == this.isLatestVersion
          && this.getPubItem().getObject().getLatestRelease().getVersionNumber() > this.getPubItem().getVersionNumber()) {
        String link = null;
        try {
          link = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH)
              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN).replaceAll("\\$1",
                  this.getPubItem().getObject().getLatestRelease().getObjectIdAndVersion());
        } catch (Exception e) {
          logger.error("Error when trying to access a property via PropertyReader", e);
        }
        this.warn(this.getMessage("itemIsNotLatestReleasedVersion") + "<br/><a href=\"" + (null != link ? link : "") + "\" >"
            + (null != link ? link : "") + "</a>");
      }

      // Prerequisites
      // Workflow
      try {
        // this.isWorkflowStandard = (this.getContext().getWorkflow() ==
        // ContextDbVO.Workflow.STANDARD);
        this.isWorkflowSimple = (ContextDbVO.Workflow.SIMPLE == this.getContext().getWorkflow());
      } catch (Exception e) {
        this.isWorkflowSimple = true;
        // this.isWorkflowStandard = false;
      }

      // this.fwUrl =
      // PropertyReader.getProperty(PropertyReader.ESCIDOC_FRAMEWORK_ACCESS_FRAMEWORK_URL);
      this.defaultSize =
          Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_PRESENTATION_VIEWFULLITEM_DEFAULTSIZE, "20"));

      // Submenu
      String subMenu = FacesTools.getRequest().getParameter(ViewItemFull.PARAMETERNAME_MENU_VIEW);
      if (null != subMenu) {
        this.getViewItemSessionBean().setSubMenu(subMenu);
      }

      // List of numbered affiliated organizations
      this.createCreatorsList();

      // Languages from CoNE
      if (null != this.getPubItem().getMetadata().getLanguages() && !this.getPubItem().getMetadata().getLanguages().isEmpty()) {

        StringWriter result = new StringWriter();
        for (int i = 0; i < this.getPubItem().getMetadata().getLanguages().size(); i++) {
          if (0 < i) {
            result.append(", ");
          }

          String language = this.getPubItem().getMetadata().getLanguages().get(i);
          String languageName = null;
          try {
            languageName = CommonUtils.getConeLanguageName(language, this.getI18nHelper().getLocale());
          } catch (Exception e) {
            logger.error("Cannot retrieve language information from CoNE", e);
          }
          if (null != languageName) {
            result.append(language);
            if (!languageName.isEmpty()) {
              result.append(" - ");
              result.append(languageName);
            }
          }
        }

        this.languages = result.toString();
      }

      // Source list
      if (!this.getPubItem().getMetadata().getSources().isEmpty()) {
        this.sourceList = new ArrayList<>();
        for (int i = 0; i < this.getPubItem().getMetadata().getSources().size(); i++) {
          this.sourceList.add(new SourceBean(this.getPubItem().getMetadata().getSources().get(i)));
        }
      }

      // List of files
      // Check if the item is also in the search result list
      List<PubItemVOPresentation> currentPubItemList = this.getPubItemListSessionBean().getCurrentPartList();

      if (null != currentPubItemList) {
        for (PubItemVOPresentation pubItemVOPresentation : currentPubItemList) {
          if (this.getPubItem().getObjectId().equals(pubItemVOPresentation.getObjectId())
              && this.getPubItem().getVersionNumber() == pubItemVOPresentation.getVersionNumber()
              && null != pubItemVOPresentation.getSearchHit()) {
            this.pubItem.initSearchHits(pubItemVOPresentation.getSearchHit());
          }
        }
      }

      // Unapi Export
      try {
        // this.unapiURLdownload =
        // PropertyReader.getProperty(PropertyReader.ESCIDOC_UNAPI_DOWNLOAD_SERVER);
        this.unapiURLview = PropertyReader.getProperty(PropertyReader.INGE_UNAPI_SERVICE_URL);
        // this.unapiEscidoc = this.unapiURLdownload + "?id=" + itemID + "&format=escidoc";
        // this.unapiEndnote = this.unapiURLdownload + "?id=" + itemID + "&format=endnote";
        // this.unapiBibtex = this.unapiURLdownload + "?id=" + itemID + "&format=bibtex";
        // this.unapiApa = this.unapiURLdownload + "?id=" + itemID + "&format=apa";
      } catch (Exception e) {
        logger.error("Error getting unapi url property", e);
        throw new RuntimeException(e);
      }

      // SSRN
      try {
        String contexts = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_SSRN_CONTEXTS);
        if (null != contexts && !contexts.isEmpty()) {
          this.ssrnContexts = new ArrayList<>();
          while (contexts.contains(",")) {
            this.ssrnContexts.add(contexts.substring(0, contexts.indexOf(",")));
            contexts = contexts.substring(contexts.indexOf(",") + 1);
          }
          this.ssrnContexts.add(contexts);
        }
      } catch (Exception e) {
        logger.error("couldn't load ssrn context list", e);
      }

      // Links
      this.setLinks();
    }
  }

  public boolean isSsrnContext() {
    if (null != this.ssrnContexts && this.ssrnContexts.contains(this.getPubItem().getObject().getContext().getObjectId())) {
      return true;
    }

    return false;
  }

  public boolean isSsrnTagged() {
    if (this.getPubItem().getObject().getLocalTags().contains(ViewItemFull.SSRN_LOCAL_TAG)) {
      return true;
    }

    return false;
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
   * Redirects the user to the Item Log page.
   *
   * @return String nav rule to load item log page
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
    if (!validate()) {
      return null;
    }

    return SubmitItem.LOAD_SUBMITITEM;
  }



  public String releaseItem() {
    if (!validate()) {
      return null;
    }

    return ReleaseItem.LOAD_RELEASEITEM;
  }

  private boolean validate() {
    try {
      ItemVersionVO itemVO = new ItemVersionVO(this.getPubItem());
      ApplicationBean.INSTANCE.getItemValidatingService().validate(itemVO, ValidationPoint.STANDARD);
    } catch (ValidationException e) {
      this.showValidationMessages(e.getReport());
      return false;
    } catch (ValidationServiceException e) {
      throw new RuntimeException("Validation error", e);
    }

    return true;
  }

  public String deleteItem() {
    final String navigateTo = MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS;

    String retVal = this.getItemControllerSessionBean().deleteCurrentPubItem(navigateTo);

    if (navigateTo.equals(retVal)) {
      this.info(this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_DELETED));
      this.getPubItemListSessionBean().update();

      // redirect to last breadcrumb, if available
      BreadcrumbItemHistorySessionBean bhsb = FacesTools.findBean("BreadcrumbItemHistorySessionBean");
      try {
        for (int i = bhsb.getBreadcrumbs().size() - 1; 0 < i; i--) {
          if (false == bhsb.getBreadcrumbs().get(i - 1).isItemSpecific()
              && false == "CreateItemPage".equalsIgnoreCase(bhsb.getBreadcrumbs().get(i - 1).getDisplayValue())) {
            FacesTools.getExternalContext().redirect(bhsb.getBreadcrumbs().get(i - 1).getPage());
            return retVal;
          }
        }
      } catch (IOException e) {
        logger.error("Could not redirect to last breadcrumb!");
      }
    }

    return retVal;
  }

  // private void showValidationMessages(ValidationReportVO report) {
  // FacesBean.warn(this.getMessage(ViewItemFull.VALIDATION_ERROR_MESSAGE));
  // for (final Iterator<ValidationReportItemVO> iter = report.getItems().iterator();
  // iter.hasNext();) {
  // final ValidationReportItemVO element = iter.next();
  // this.error(this.getMessage(element.getContent()));
  // }
  // }

  private void showValidationMessages(ValidationReportVO report) {
    this.warn(this.getMessage(ViewItemFull.VALIDATION_ERROR_MESSAGE));

    for (ValidationReportItemVO element : report.getItems()) {
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
    sortOrganizationList = new ArrayList<>();
    String formattedCreator = "";
    String formattedOrganization = "";

    this.organizationArray = new ArrayList<>();
    this.organizationList = new ArrayList<>();

    // counter for organization array
    int counterOrganization = 0;
    ObjectFormatter formatter = new ObjectFormatter();

    // temporary list of All creators, retrieved directly from the metadata
    tempCreatorList = this.getPubItem().getMetadata().getCreators();
    // the list of creators is initialized to a new array list
    this.creators = new ArrayList<>();
    // initial affiliation position set to 0
    int affiliationPosition = 0;

    // for each creator in the list
    for (CreatorVO creatorVO : tempCreatorList) {

      // temporary organization list is matched against the sorted for each separate creator
      // therefore for each creator is newly re-set
      tempOrganizationList = new ArrayList<>();

      // put creator in temporary VO
      CreatorVO creator1 = new CreatorVO();
      creator1 = creatorVO;

      // annotation = new StringBuffer();
      // int organizationsFound = 0;
      ViewItemCreators creator = new ViewItemCreators();
      CreatorDisplay creatorDisplay = new CreatorDisplay();

      // if the creator is a person add his organization to the sorted organization list
      if (null != creator1.getPerson()) {
        // if there is affiliated organization for this creator
        if (!creator1.getPerson().getOrganizations().isEmpty()) {
          // add each affiliated organization of the creator to the temporary organization list
          tempOrganizationList.addAll(creator1.getPerson().getOrganizations());

          // for each organizations in the temporary organization list
          for (OrganizationVO organizationVO : tempOrganizationList) {
            // check if the organization in the list is in the sorted organization list
            if (!sortOrganizationList.contains(organizationVO)) {
              affiliationPosition++;
              // if the temporary organization is to be added to the sorted set of organizations
              sortOrganizationList.add(organizationVO);
              // create new Organization view object
              this.organizationList.add(ViewItemFull.formatCreatorOrganization(organizationVO, affiliationPosition));
            }
          }
        }

        formattedCreator = formatter.formatCreator(creator1, ViewItemFull.formatCreatorOrganizationIndex(creator1, sortOrganizationList));
        creatorDisplay.setFormattedDisplay(formattedCreator);

        if (null != creator1.getPerson().getIdentifier() && (IdentifierVO.IdType.CONE == creator1.getPerson().getIdentifier().getType())) {
          try {
            creatorDisplay.setPortfolioLink(ConeUtils.makeConePersonsLinkFull(creator1.getPerson().getIdentifier().getId()));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }

        if (null != creator1.getPerson().getOrcid()) {
          try {
            creatorDisplay.setOrcid(creator1.getPerson().getOrcid());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }

        creator.setCreatorType(ViewItemCreators.Type.PERSON.toString());
        creator.setCreatorObj(creatorDisplay);
        creator.setCreatorRole(creator1.getRoleString());

        this.creators.add(creator);
      } // end if creator is a person

      if (null != creator1.getOrganization()) {
        formattedCreator = formatter.formatCreator(creator1, "");
        creatorDisplay.setFormattedDisplay(formattedCreator);
        ViewItemCreatorOrganization creatorOrganization = new ViewItemCreatorOrganization();
        creatorOrganization.setOrganizationName(formattedCreator);
        creatorOrganization.setPosition(String.valueOf(counterOrganization));
        creatorOrganization.setOrganizationAddress(creator1.getOrganization().getAddress());
        creatorOrganization.setOrganizationInfoPage(formattedCreator, creator1.getOrganization().getAddress());
        creatorOrganization.setIdentifier(creator1.getOrganization().getIdentifier());
        creator.setCreatorType(ViewItemCreators.Type.ORGANIZATION.toString());
        creator.setCreatorObj(creatorOrganization);
        creator.setCreatorRole(creator1.getRoleString());
        this.creators.add(creator);
      }

      counterOrganization++;
      // creatorListString.append(formattedCreator);
      this.affiliatedOrganizationsList = sortOrganizationList;
      // this.affiliatedOrganizationsList = sortOrganizationList;
      // generate a 'well-formed' list for presentation in the jsp
      for (int k = 0; k < sortOrganizationList.size(); k++) {
        String name = null != sortOrganizationList.get(k).getName() ? sortOrganizationList.get(k).getName() : "";
        formattedOrganization = "<p>" + (k + 1) + ": " + name + "</p>" + "<p>" + sortOrganizationList.get(k).getAddress() + "</p>" + "<p>"
            + sortOrganizationList.get(k).getIdentifier() + "</p>";
        this.organizationArray.add(formattedOrganization);
        // this.getOrganizationArray().add(formattedOrganization);
      }
    } // end for each creator in the list

  }

  public static ViewItemOrganization formatCreatorOrganization(OrganizationVO tempOrganizationListInstance, int affiliationPosition) {
    ViewItemOrganization viewOrganization = new ViewItemOrganization();

    // set the organization view object to values from the current temp organization
    if (null != tempOrganizationListInstance.getName()) {
      viewOrganization.setOrganizationName(tempOrganizationListInstance.getName());
      viewOrganization.setOrganizationAddress(tempOrganizationListInstance.getAddress());
      viewOrganization.setOrganizationIdentifier(tempOrganizationListInstance.getIdentifier());
      viewOrganization.setPosition(String.valueOf(affiliationPosition));
      viewOrganization.setOrganizationInfoPage(tempOrganizationListInstance.getName(), tempOrganizationListInstance.getAddress());
      viewOrganization.setOrganizationDescription(tempOrganizationListInstance.getName(), tempOrganizationListInstance.getAddress(),
          tempOrganizationListInstance.getIdentifier());
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
  public static String formatCreatorOrganizationIndex(CreatorVO creator, List<OrganizationVO> sortOrganizationList) {
    int organizationsFound = 0;
    StringBuilder annotation = new StringBuilder();
    // go through known sorted organizations and format the number at the creator
    for (int j = 0; j < sortOrganizationList.size(); j++) {
      if (creator.getPerson().getOrganizations().contains(sortOrganizationList.get(j))) {
        if (0 == organizationsFound) {
          annotation.append("<sup>");
        }
        if (0 < organizationsFound && j < sortOrganizationList.size()) {
          annotation.append(", ");
        }
        annotation.append(j + 1);
        organizationsFound++;
      }
    }

    if (!annotation.isEmpty()) {
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
    StringBuilder publishingInfo = new StringBuilder();
    if (null != this.pubItem.getMetadata().getPublishingInfo()) {
      // Place
      if (null != this.pubItem.getMetadata().getPublishingInfo().getPlace()
          && !this.pubItem.getMetadata().getPublishingInfo().getPlace().isEmpty()) {
        publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPlace().trim());
      }
      // colon
      if (null != this.pubItem.getMetadata().getPublishingInfo().getPublisher()
          && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim().isEmpty()
          && null != this.pubItem.getMetadata().getPublishingInfo().getPlace()
          && !this.pubItem.getMetadata().getPublishingInfo().getPlace().trim().isEmpty()) {
        publishingInfo.append(" : ");
      }
      // Publisher
      if (null != this.pubItem.getMetadata().getPublishingInfo().getPublisher()
          && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().isEmpty()) {
        publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim());
      }
      // Comma
      if ((null != this.pubItem.getMetadata().getPublishingInfo().getEdition()
          && !this.pubItem.getMetadata().getPublishingInfo().getEdition().trim().isEmpty())
          && ((null != this.pubItem.getMetadata().getPublishingInfo().getPlace()
              && !this.pubItem.getMetadata().getPublishingInfo().getPlace().trim().isEmpty())
              || (null != this.pubItem.getMetadata().getPublishingInfo().getPublisher()
                  && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim().isEmpty()))) {
        publishingInfo.append(", ");
      }
      // Edition
      if (null != this.pubItem.getMetadata().getPublishingInfo().getEdition()) {
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
    StringBuilder identifiers = new StringBuilder();
    if (null != idList) {
      for (int i = 0; i < idList.size(); i++) {
        try {
          String labelKey = "ENUM_IDENTIFIERTYPE_" + idList.get(i).getTypeString();
          identifiers.append(getLabelStatic(labelKey));
        } catch (MissingResourceException e) {
          logger.debug("Found no label for identifier type " + idList.get(i).getTypeString());
          identifiers.append(idList.get(i).getTypeString());
        }
        identifiers.append(": ");
        if (IdentifierVO.IdType.BIORXIV == idList.get(i).getType() //
            || IdentifierVO.IdType.CHEMRXIV == idList.get(i).getType() //
            || IdentifierVO.IdType.DOI == idList.get(i).getType() //
            || IdentifierVO.IdType.EARTHARXIV == idList.get(i).getType() //
            || IdentifierVO.IdType.EDARXIV == idList.get(i).getType() //
            || IdentifierVO.IdType.ESS_OPEN_ARCHIVE == idList.get(i).getType() //
            || IdentifierVO.IdType.MEDRXIV == idList.get(i).getType() //
            || IdentifierVO.IdType.PSYARXIV == idList.get(i).getType() //
            || IdentifierVO.IdType.RESEARCH_SQUARE == idList.get(i).getType() //
            || IdentifierVO.IdType.SOCARXIV == idList.get(i).getType()) {
          identifiers.append("<a target='_blank' href='https://doi.org/" + idList.get(i).getId() + "'>" + idList.get(i).getId() + "</a>");
        } else if (IdentifierVO.IdType.ADS == idList.get(i).getType()) {
          identifiers.append("<a target='_blank' href='https://ui.adsabs.harvard.edu/abs/" + idList.get(i).getId() + "'>"
              + idList.get(i).getId() + "</a>");
        } else if (IdentifierVO.IdType.ARXIV == idList.get(i).getType()) {
          identifiers
              .append("<a target='_blank' href='https://arxiv.org/abs/" + idList.get(i).getId() + "'>" + idList.get(i).getId() + "</a>");
        } else if (IdentifierVO.IdType.CONE == idList.get(i).getType()) {
          String coneServiceUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL);
          identifiers.append("<a target='_blank' href='"
              + (coneServiceUrl.endsWith("/") ? coneServiceUrl.substring(0, coneServiceUrl.length() - 1) : coneServiceUrl)
              + idList.get(i).getId() + "'>"
              + (coneServiceUrl.endsWith("/") ? coneServiceUrl.substring(0, coneServiceUrl.length() - 1) : coneServiceUrl)
              + idList.get(i).getId() + "</a>");
        } else if (IdentifierVO.IdType.ISI == idList.get(i).getType()) {
          identifiers.append("<a target='_blank' href='" + ViewItemFull.ISI_KNOWLEDGE_BASE_LINK + idList.get(i).getId()
              + ViewItemFull.ISI_KNOWLEDGE_DEST_APP + "'>" + idList.get(i).getId() + "</a>");
        } else if (IdentifierVO.IdType.PMC == idList.get(i).getType()) {
          identifiers.append("<a target='_blank' href='https://www.ncbi.nlm.nih.gov/pmc/articles/" + idList.get(i).getId() + "'>"
              + idList.get(i).getId() + "</a>");
        } else if (IdentifierVO.IdType.PMID == idList.get(i).getType()) {
          identifiers.append(
              "<a target='_blank' href='https://pubmed.ncbi.nlm.nih.gov/" + idList.get(i).getId() + "'>" + idList.get(i).getId() + "</a>");
        } else if (IdentifierVO.IdType.SSRN == idList.get(i).getType()) {
          identifiers.append(
              "<a target='_blank' href='https://ssrn.com/abstract=" + idList.get(i).getId() + "'>" + idList.get(i).getId() + "</a>");
        } else if (IdentifierVO.IdType.ZDB == idList.get(i).getType()) {
          identifiers.append("<a target='_blank' href='https://ld.zdb-services.de/resource/" + idList.get(i).getId() + "'>"
              + idList.get(i).getId() + "</a>");
        } else if (CommonUtils.getIsUriValidUrl(idList.get(i))) {
          identifiers.append("<a target='_blank' href='" + idList.get(i).getId() + "'>" + idList.get(i).getId() + "</a>");
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
    InternationalizationHelper i18nHelper = FacesTools.findBean("InternationalizationHelper");
    return i18nHelper.getLabel(placeholder);
  }

  /**
   * Returns a true or a false according top the existance of specified fields in the details
   * section
   *
   * @return boolean
   */
  public boolean getShowDetails() {
    if (null != this.pubItem.getMetadata()) {
      if ((null != this.pubItem.getMetadata().getLanguages() && !this.pubItem.getMetadata().getLanguages().isEmpty())
          || (this.getShowDates())
          || (null != this.pubItem.getMetadata().getTotalNumberOfPages()
              && !this.pubItem.getMetadata().getTotalNumberOfPages().trim().isEmpty())
          || (null != this.pubItem.getMetadata().getPublishingInfo())
          || (null != this.pubItem.getMetadata().getTableOfContents() && !this.pubItem.getMetadata().getTableOfContents().trim().isEmpty())
          || (null != this.pubItem.getMetadata().getReviewMethod())
          || (null != this.pubItem.getMetadata().getIdentifiers() && !this.pubItem.getMetadata().getIdentifiers().isEmpty())
          || (null != this.pubItem.getMetadata().getDegree())
          || (null != this.pubItem.getMetadata().getLocation() && !this.pubItem.getMetadata().getLocation().trim().isEmpty())) {
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
    if (null != this.pubItem.getMetadata() && null != this.pubItem.getMetadata().getEvent()) {
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
    if (null != this.pubItem.getMetadata() && null != this.pubItem.getMetadata().getSources()
        && !this.pubItem.getMetadata().getSources().isEmpty()) {
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
    if (null != this.pubItem.getFileBeanList() && !this.pubItem.getFileBeanList().isEmpty()) {
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
    if (null != this.pubItem.getFileBeanList() && !this.pubItem.getFileBeanList().isEmpty()) {
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
    if (null != this.pubItem.getLocatorBeanList() && !this.pubItem.getLocatorBeanList().isEmpty()) {
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
    if (null != this.pubItem.getLocatorBeanList() && !this.pubItem.getLocatorBeanList().isEmpty()) {
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
    if (this.getPubItem().getObject().getPublicState().equals(ItemVersionRO.State.WITHDRAWN)) {
      return false;
    }

    return this.getPubItem().getVersionState().equals(ItemVersionRO.State.RELEASED);
  }

  public String getDates() {
    List<ItemVersionVO> pubItemList = new ArrayList<>();
    pubItemList.add(this.getPubItem());
    List<PubItemVOPresentation> pubItemPresentationList = CommonUtils.convertToPubItemVOPresentationList(pubItemList);
    PubItemVOPresentation pubItemPresentation = pubItemPresentationList.get(0);

    return pubItemPresentation.getDatesAsString();
  }

  /**
   * Returns false if all dates are empty
   *
   * @author Markus Haarlaender
   * @return boolean
   */
  public boolean getShowDates() {
    return ((null != this.getPubItem().getMetadata().getDatePublishedInPrint()
        && !this.getPubItem().getMetadata().getDatePublishedInPrint().isEmpty())
        || (null != this.getPubItem().getMetadata().getDatePublishedOnline()
            && !this.getPubItem().getMetadata().getDatePublishedOnline().isEmpty())
        || (null != this.getPubItem().getMetadata().getDateAccepted() && !this.getPubItem().getMetadata().getDateAccepted().isEmpty())
        || (null != this.getPubItem().getMetadata().getDateSubmitted() && !this.getPubItem().getMetadata().getDateSubmitted().isEmpty())
        || (null != this.getPubItem().getMetadata().getDateModified() && !this.getPubItem().getMetadata().getDateModified().isEmpty())
        || (null != this.getPubItem().getMetadata().getDateCreated() && !this.getPubItem().getMetadata().getDateCreated().isEmpty()));
  }

  /**
   * Returns a true or a false according to the invited state of the item
   *
   * @return boolean
   */
  public boolean getInvited() {
    if (null != this.pubItem.getMetadata().getEvent().getInvitationStatus()) {
      if (EventVO.InvitationStatus.INVITED.equals(this.pubItem.getMetadata().getEvent().getInvitationStatus())) {
        return true;
      }
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
    if (ItemVersionRO.State.WITHDRAWN.equals(this.pubItem.getObject().getPublicState())) {
      if (null != this.pubItem.getModificationDate()) {
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
    if (null == this.context) {
      try {
        this.context = this.getItemControllerSessionBean().retrieveContext(this.pubItem.getObject().getContext().getObjectId());
      } catch (Exception e) {
        logger.error("Error retrieving context", e);
      }
    }

    if (null != this.context) {
      contextName = this.context.getName();
    }

    return contextName;
  }

  /**
   * Returns the Context the item belongs to
   */
  public ContextDbVO getContext() {
    if (null == this.context) {
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
    StringBuilder affiliations = new StringBuilder();
    List<AffiliationDbRO> affiliationRefList = new ArrayList<>();
    List<AffiliationVOPresentation> affiliationList = new ArrayList<>();
    if (null == this.context) {
      try {
        this.context = this.getItemControllerSessionBean().retrieveContext(this.pubItem.getObject().getContext().getObjectId());
      } catch (Exception e) {
        logger.error("Error retrieving collection", e);
      }
    }
    if (null != this.context) {
      affiliationRefList = this.context.getResponsibleAffiliations();
    }
    // first get all affiliations
    if (null != affiliationRefList) {
      for (AffiliationDbRO affiliationDbRO : affiliationRefList) {
        try {
          affiliationList.add(
              new AffiliationVOPresentation(ApplicationBean.INSTANCE.getOrganizationService().get(affiliationDbRO.getObjectId(), null)));
        } catch (Exception e) {
          logger.error("Error retrieving affiliation list", e);
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
   * @return String name of the specified OU (inge.pubman.root.organization.name)
   */
  public String getSpecificOrganization() {
    String rootOrganization = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ROOT_ORGANIZATION_NAME);

    if (null != rootOrganization) {
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
    StringBuilder date = new StringBuilder();
    if (null != this.pubItem.getMetadata().getEvent().getStartDate()) {
      date.append(this.pubItem.getMetadata().getEvent().getStartDate());
    }
    if (null != this.pubItem.getMetadata().getEvent().getEndDate()) {
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

  public AccountUserDbRO getLatestModifier() {
    /*
     * if (this.latestModifier == null && this.pubItem.getVersion().getModifiedByRO() != null &&
     * this.pubItem.getVersion().getModifiedByRO().getObjectId() != null) { try {
     * this.latestModifier = this.getItemControllerSessionBean().retrieveUserAccount(
     * this.pubItem.getVersion().getModifiedByRO().getObjectId()); } catch (final Exception e) {
     * ViewItemFull.logger.error("Error retrieving latest modifier", e); } }
     */

    return this.pubItem.getModifier();
  }

  /**
   * Returns the Creation date as formatted String (YYYY-MM-DD)
   *
   * @return String the formatted date of modification
   */
  public String getCreationDate() {
    return CommonUtils.formatTimestamp(this.pubItem.getObject().getCreationDate());
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

  public ItemVersionVO getPubItem() {
    return this.pubItem;
  }

  public void setPubItem(PubItemVOPresentation pubItem) {
    this.pubItem = pubItem;
  }

  //    public ArrayList<AbstractVO> getAbstracts() {
  //      final ArrayList<AbstractVO> abstracts = new ArrayList<AbstractVO>();
  //      if (this.pubItem.getMetadata().getAbstracts() != null) {
  //        for (int i = 0; i < this.pubItem.getMetadata().getAbstracts().size(); i++) {
  //          abstracts.add(new AbstractVO(this.pubItem.getMetadata().getAbstracts().get(i).getValue()));
  //        }
  //      }
  //      return abstracts;
  //    }

  public boolean getHasAbstracts() {
    return !this.pubItem.getMetadata().getAbstracts().isEmpty();
  }

  public boolean getHasSubjects() {
    boolean hasNotEmptySubjects = false;
    for (SubjectVO subject : this.pubItem.getMetadata().getSubjects()) {
      if (null != subject && null != subject.getValue() && !subject.getValue().trim().isEmpty()) {
        hasNotEmptySubjects = true;
        return hasNotEmptySubjects;
      }
    }
    return hasNotEmptySubjects;
  }

  public boolean getHasFreeKeywords() {
    return null != this.pubItem.getMetadata().getFreeKeywords() && !this.pubItem.getMetadata().getFreeKeywords().isEmpty();
  }

  public String getGenre() {
    String genre = "";
    if (null != this.pubItem.getMetadata().getGenre()) {
      genre = this.getLabel(this.getI18nHelper().convertEnumToString(this.pubItem.getMetadata().getGenre()));
    }

    return genre;
  }

  public String getReviewMethod() {
    String reviewMethod = "";
    if (null != this.pubItem.getMetadata() && null != this.pubItem.getMetadata().getReviewMethod()) {
      reviewMethod = this.getLabel(this.getI18nHelper().convertEnumToString(this.pubItem.getMetadata().getReviewMethod()));
    }

    return reviewMethod;
  }

  public String getDegreeType() {
    String degreeType = "";
    if (null != this.pubItem.getMetadata() && null != this.pubItem.getMetadata().getDegree()) {
      degreeType = this.getLabel(this.getI18nHelper().convertEnumToString(this.pubItem.getMetadata().getDegree()));
    }

    return degreeType;
  }

  public String getItemState() {
    String itemState = "";
    if (null != this.pubItem.getVersionState()) {
      itemState = this.getLabel(this.getI18nHelper().convertEnumToString(this.pubItem.getVersionState()));
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

  public String getCitationObjectUrl() {
    return this.citationObjectUrl;
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
    return this.creators.size();
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

  public void setCitationObjectUrl(String citationObjectUrl) {
    this.citationObjectUrl = citationObjectUrl;
  }


  public boolean getIsStateWithdrawn() {
    return ItemVersionRO.State.WITHDRAWN.equals(this.getPubItem().getObject().getPublicState());
  }

  public void setStateWithdrawn(boolean isStateWithdrawn) {
    this.isStateWithdrawn = isStateWithdrawn;
  }

  public boolean getIsWorkflowSimple() {
    return this.isWorkflowSimple;
  }

  public void setWorkflowSimple(boolean isWorkflowSimple) {
    this.isWorkflowSimple = isWorkflowSimple;
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

  // public boolean getHasAudience() {
  // if (this.pubItem != null &&
  // (ItemVersionRO.State.RELEASED.equals(this.pubItem.getVersionState())
  // || ItemVersionRO.State.SUBMITTED.equals(this.pubItem.getVersionState())) &&
  // (this.getIsModerator() || this.getIsDepositor())) {
  //
  // for (final FileDbVO file : this.pubItem.getFiles()) {
  // if (Visibility.AUDIENCE.equals(file.getVisibility())) {
  // return true;
  // }
  // }
  // }
  //
  // return false;
  // }

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
    if (null != this.pubItem.getObject().getPublicState()) {
      itemState = this.getLabel(this.getI18nHelper().convertEnumToString(this.pubItem.getObject().getPublicState()));
    }

    return itemState;
  }

  // public String getUnapiURLdownload() {
  // return this.unapiURLdownload;
  // }
  //
  // public void setUnapiURLdownload(String unapiURLdownload) {
  // this.unapiURLdownload = unapiURLdownload;
  // }
  //
  // public String getUnapiEscidoc() {
  // return this.unapiEscidoc;
  // }
  //
  // public void setUnapiEscidoc(String unapiEscidoc) {
  // this.unapiEscidoc = unapiEscidoc;
  // }
  //
  // public String getUnapiEndnote() {
  // return this.unapiEndnote;
  // }
  //
  // public void setUnapiEndnote(String unapiEndnote) {
  // this.unapiEndnote = unapiEndnote;
  // }
  //
  // public String getUnapiBibtex() {
  // return this.unapiBibtex;
  // }
  //
  // public void setUnapiBibtex(String unapiBibtex) {
  // this.unapiBibtex = unapiBibtex;
  // }
  //
  // public String getUnapiApa() {
  // return this.unapiApa;
  // }
  //
  // public void setUnapiApa(String unapiApa) {
  // this.unapiApa = unapiApa;
  // }

  public String getUnapiURLview() {
    return this.unapiURLview;
  }

  public void setUnapiURLview(String unapiURLview) {
    this.unapiURLview = unapiURLview;
  }

  public String addToBasket() {
    if (!this.getPubItemStorageSessionBean().getStoredPubItems().containsKey(this.getPubItem().getObjectIdAndVersion())) {
      this.getPubItemStorageSessionBean().getStoredPubItems().put(this.pubItem.getObjectIdAndVersion(), this.pubItem);
      this.info(this.getMessage("basket_SingleAddedSuccessfully"));
    } else {
      this.error(this.getMessage("basket_SingleAlreadyInBasket"));
    }
    this.canAddToBasket = false;
    this.canDeleteFromBasket = true;

    return "";
  }

  public String removeFromBasket() {
    this.getPubItemStorageSessionBean().getStoredPubItems().remove(this.pubItem.getObjectIdAndVersion());
    this.info(this.getMessage("basket_SingleRemovedSuccessfully"));
    this.canAddToBasket = true;
    this.canDeleteFromBasket = false;

    return "";
  }

  public String addToBatch() {
    if (!this.getPubItemBatchSessionBean().getStoredPubItems().containsKey(this.getPubItem().getObjectId())) {
      this.getPubItemBatchSessionBean().getStoredPubItems().put(this.pubItem.getObjectId(), this.pubItem);
      this.info(this.getMessage("batch_SingleAddedSuccessfully"));
    } else {
      this.error(this.getMessage("batch_SingleAlreadyInBasket"));
    }
    //    this.canAddToBatch = false;
    //    this.canDeleteFromBatch = true;

    return "";
  }

  public String removeFromBatch() {
    this.getPubItemBatchSessionBean().getStoredPubItems().remove(this.pubItem.getObjectId());
    this.info(this.getMessage("batch_SingleRemovedSuccessfully"));
    //    this.canAddToBatch = true;
    //    this.canDeleteFromBatch = false;

    return "";
  }

  public boolean getIsInBasket() {
    if (null == this.pubItem) {
      return false;
    }

    return this.getPubItemStorageSessionBean().getStoredPubItems().containsKey(this.pubItem.getObjectIdAndVersion());
  }

  public String getLinkForActionsView() {
    return "ViewItemFullPage.jsp?" + ViewItemFull.PARAMETERNAME_ITEM_ID + "=" + this.getPubItem().getObjectIdAndVersion() + "&"
        + ViewItemFull.PARAMETERNAME_MENU_VIEW + "=ACTIONS";
  }

  public String getLinkForExportView() {
    return "ViewItemFullPage.jsp?" + ViewItemFull.PARAMETERNAME_ITEM_ID + "=" + this.getPubItem().getObjectIdAndVersion() + "&"
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
    List<ItemVersionVO> pubItemList = new ArrayList<>();
    pubItemList.add(this.getPubItem());
    ExportFormatVO curExportFormat = this.getExportItemsSessionBean().getCurExportFormatVO();
    byte[] exportFileData;

    try {
      exportFileData = this.getItemControllerSessionBean().retrieveExportData(curExportFormat, pubItemList);
    } catch (IngeTechnicalException e) {
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
      return ErrorPage.LOAD_ERRORPAGE;
    }

    if ((null == exportFileData) || (new String(exportFileData)).trim().isEmpty()) {
      this.error(this.getMessage(ExportItems.MESSAGE_NO_EXPORTDATA_DELIVERED));
      return "";
    }

    // YEAR + MONTH + DAY_OF_MONTH
    Calendar rightNow = Calendar.getInstance();
    String date = rightNow.get(Calendar.YEAR) + "-" + rightNow.get(Calendar.DAY_OF_MONTH) + "-" + rightNow.get(Calendar.MONTH) + "_";

    // create an attachment temp file from the byte[] stream
    File exportAttFile;
    try {
      exportAttFile = File.createTempFile("MPG.PuRe_Export_" + curExportFormat.getFormat() + "_" + date,
          "." + TransformerFactory.getFormat(curExportFormat.getFormat()).getFileFormat().getExtension());
      FileOutputStream fos = new FileOutputStream(exportAttFile);
      fos.write(exportFileData);
      fos.close();
    } catch (IOException e1) {
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e1);
      return ErrorPage.LOAD_ERRORPAGE;
    }

    this.getExportItemsSessionBean().setExportEmailTxt(this.getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_TEXT));
    this.getExportItemsSessionBean().setAttExportFileName(exportAttFile.getName());
    this.getExportItemsSessionBean().setAttExportFile(exportAttFile);
    this.getExportItemsSessionBean()
        .setExportEmailSubject(this.getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT).replace("$1", curExportFormat.getFormat()));
    // hier call set the values on the exportEmailView - attachment file, subject, ....

    return "displayExportEmailPage";
  }

  /**
   * Downloads the page with the selected item as export. This method is called when the user
   * selects one or more items and then clicks on the Download-Button in the Export-Items Panel.
   *
   * @author: StG
   */
  public String exportDownload() {
    List<ItemVersionVO> pubItemList = new ArrayList<>();
    pubItemList.add(this.getPubItem());
    // export format and file format.
    ExportFormatVO curExportFormat = this.getExportItemsSessionBean().getCurExportFormatVO();
    byte[] exportFileData = null;
    try {
      exportFileData = this.getItemControllerSessionBean().retrieveExportData(curExportFormat, pubItemList);


      String contentType = TransformerFactory.getFormat(curExportFormat.getFormat()).getFileFormat().getMimeType();
      FacesTools.getResponse().setContentType(contentType);
      String fileName = "export_" + curExportFormat.getFormat().toLowerCase() + "."
          + TransformerFactory.getFormat(curExportFormat.getFormat()).getFileFormat().getExtension();
      FacesTools.getResponse().setHeader("Content-disposition", "attachment; filename=" + fileName);
      OutputStream out = FacesTools.getResponse().getOutputStream();
      out.write(exportFileData);
      out.flush();
      out.close();
      FacesTools.getCurrentInstance().responseComplete();
    } catch (Exception e) {
      this.error(this.getMessage("ExportError") + e.getMessage());
    }


    return "";
  }

  /**
   * This method returns the contact email address of the moderator stored in the item's context. If
   * it is empty the pubman support address will be returned.
   *
   * @return the moderator's email address (if available, otherwise pubman support address)
   */
  public String getModeratorContactEmail() {
    String contactEmail = "";
    contactEmail = this.getContext().getContactEmail();
    if (null == contactEmail || contactEmail.trim().isEmpty()) {
      contactEmail = PropertyReader.getProperty(PropertyReader.INGE_EMAIL_ALTERNATIVE_MODERATOR);
    }

    return contactEmail;
  }

  public void setPublicStateReleased(boolean isPublicStateReleased) {
    this.isPublicStateReleased = isPublicStateReleased;
  }

  public boolean getIsPublicStateReleased() {
    return this.isPublicStateReleased;
  }

  // public String getFwUrl() {
  // return this.fwUrl;
  // }
  //
  // public void setFwUrl(String fwUrl) {
  // this.fwUrl = fwUrl;
  // }

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
    if (null != this.pubItem.getMetadata() && null != this.pubItem.getMetadata().getLegalCase()) {
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
    StringBuilder legalCaseString = new StringBuilder();
    if (!"".equals(this.pubItem.getMetadata().getLegalCase().getCourtName())) {
      legalCaseString.append(this.pubItem.getMetadata().getLegalCase().getCourtName());
    }
    if (!"".equals(this.pubItem.getMetadata().getLegalCase().getDatePublished())) {
      if (!legalCaseString.isEmpty()) {
        legalCaseString.append(", ");
      }
      legalCaseString.append(this.pubItem.getMetadata().getLegalCase().getDatePublished());
    }
    if (!"".equals(this.pubItem.getMetadata().getLegalCase().getIdentifier())) {
      if (!legalCaseString.isEmpty()) {
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
      List<ItemVersionVO> pubItemList = new ArrayList<>();
      pubItemList.add(new ItemVersionVO(this.getPubItem()));

      // Use special apa style if language is set to japanese
      boolean isJapanese = false;

      if (null != this.getPubItem().getMetadata().getLanguages()) {
        for (String lang : this.getPubItem().getMetadata().getLanguages()) {
          if ("jpn".equals(lang)) {
            isJapanese = true;
            break;
          }
        }
      }

      ExportFormatVO expFormat;
      if (isJapanese || "ja".equalsIgnoreCase(this.getI18nHelper().getLocale())) {
        expFormat = new ExportFormatVO(FileFormatVO.HTML_PLAIN_NAME, TransformerFactory.CitationTypes.APA_CJK.getCitationName());
      } else {
        expFormat = new ExportFormatVO(FileFormatVO.HTML_PLAIN_NAME, TransformerFactory.CitationTypes.APA6.getCitationName());
      }

      ItemTransformingService itemTransformingService = new ItemTransformingServiceImpl();
      byte[] exportFileData = itemTransformingService.getOutputForExport(expFormat, pubItemList);

      String exportHtml = new String(exportFileData, StandardCharsets.UTF_8);
      try {
        Pattern p = Pattern.compile("(?<=\\<body\\>).*(?=\\<\\/body\\>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(exportHtml);
        m.find();
        String match = m.group();
        if (null != match) {
          return match;
        }
      } catch (Exception e) {
        logger.debug("Match in citation html not found", e);
      }

      return "";
    } catch (Exception e) {
      throw new RuntimeException("Cannot export item:", e);
    }
  }

  public String getResolveHandleService() {
    return PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_URL);
  }

  private void setLinks() {
    try {
      PubItemService pis = ApplicationBean.INSTANCE.getPubItemService();

      this.canEdit = pis.checkAccess(AuthorizationService.AccessType.EDIT, getLoginHelper().getPrincipal(), this.getPubItem());
      this.canModify = false;
      this.canSubmit = pis.checkAccess(AuthorizationService.AccessType.SUBMIT, getLoginHelper().getPrincipal(), this.getPubItem());
      this.canRelease = pis.checkAccess(AuthorizationService.AccessType.RELEASE, getLoginHelper().getPrincipal(), this.getPubItem());
      // this.canAccept = false;
      this.canRevise = pis.checkAccess(AuthorizationService.AccessType.REVISE, getLoginHelper().getPrincipal(), this.getPubItem());
      this.canWithdraw = pis.checkAccess(AuthorizationService.AccessType.WITHDRAW, getLoginHelper().getPrincipal(), this.getPubItem());
      this.canDelete = pis.checkAccess(AuthorizationService.AccessType.DELETE, getLoginHelper().getPrincipal(), this.getPubItem());
    } catch (Exception e) {
      this.error(this.getMessage("AccessInfoError"));
      logger.error("Error while getting access information", e);
    }

    /*
     * if (((this.isStatePending || this.isStateSubmitted && this.isWorkflowSimple ||
     * this.isStateInRevision) && this.isLatestVersion && this.isOwner) || (this.isStateSubmitted &&
     * this.isLatestVersion && this.isModerator)) { this.canEdit = true; }
     *
     * if ((this.isStatePending || this.isStateInRevision) && this.isLatestVersion && this.isOwner
     * && this.isWorkflowStandard) { this.canSubmit = true; }
     *
     * if (this.isOwner && this.isLatestVersion && (((this.isStatePending || this.isStateSubmitted)
     * && this.isWorkflowSimple) || (this.isWorkflowStandard && this.isModerator &&
     * this.isStateSubmitted))) { this.canRelease = true; }
     *
     * if (this.isStateSubmitted && this.isLatestVersion && this.isModerator && !this.isOwner) {
     * this.canAccept = true; }
     *
     * if (this.isStateSubmitted && this.isLatestVersion && this.isModerator &&
     * this.isWorkflowStandard && !this.isPublicStateReleased) { this.canRevise = true; }
     *
     * if (!this.isPublicStateReleased && (this.isStatePending || this.isStateInRevision) &&
     * this.isLatestVersion && this.isOwner) { this.canDelete = true; }
     *
     * if (this.isStateReleased && this.isLatestVersion && (this.isOwner || this.isModerator)) {
     * this.canWithdraw = true; }
     *
     * if (this.isStateReleased && this.isLatestVersion && (this.isModerator || this.isOwner)) {
     * this.canModify = true; }
     *
     */

    if (!this.isStateWithdrawn && this.isLatestVersion && this.isDepositor) {
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

    // if (this.getHasAudience() && !this.isStateWithdrawn) {
    // this.canManageAudience = true;
    // }

    if (this.isLatestVersion && !this.isStateWithdrawn && this.isLoggedIn && (this.isOwner || this.isModerator)) {
      this.canShowItemLog = true;
    }

    if (null != this.pubItem && (!this.isStateWithdrawn && this.isLatestRelease)
        || (this.isStateWithdrawn && 1 < this.pubItem.getVersionNumber())) {
      this.canShowReleaseHistory = true;
    }

    if (null != this.pubItem && null != this.pubItem.getMessage() && !this.pubItem.getMessage().contentEquals("")) {
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

  // public boolean isCanAccept() {
  // return this.canAccept;
  // }

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

  public boolean isCanCreateFromTemplate() {
    return this.canCreateFromTemplate;
  }

  public boolean isCanAddToBasket() {
    return this.canAddToBasket;
  }

  public boolean isCanDeleteFromBasket() {
    return this.canDeleteFromBasket;
  }

  public boolean isCanAddToBatch() {
    if (!this.isLoggedIn && !this.isModerator) {
      return false;
    }
    return !this.getPubItemBatchSessionBean().getStoredPubItems().containsKey(this.getPubItem().getObjectId());
  }

  public boolean isCanDeleteFromBatch() {
    if (!this.isLoggedIn && !this.isModerator) {
      return false;
    }
    return this.getPubItemBatchSessionBean().getStoredPubItems().containsKey(this.getPubItem().getObjectId());
  }

  public boolean isCanViewLocalTags() {
    return this.canViewLocalTags;
  }

  // public boolean isCanManageAudience() {
  // return this.canManageAudience;
  // }

  public boolean isCanShowItemLog() {
    return this.canShowItemLog;
  }

  public boolean isCanShowReleaseHistory() {
    return this.canShowReleaseHistory;
  }

  public boolean isCanShowLastMessage() {
    return this.canShowLastMessage;
  }

  public String getHtmlMetaTags() {
    try {
      String itemXml = XmlTransformingService.transformToItem(EntityTransformer.transformToOld(new ItemVersionVO(this.pubItem)));
      ItemTransformingService itemTransformingService = new ItemTransformingServiceImpl();

      String resHighwire = itemTransformingService.transformFromTo(TransformerFactory.getInternalFormat(),
          TransformerFactory.FORMAT.HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML, itemXml, null);

      String resDC = itemTransformingService.transformFromTo(TransformerFactory.getInternalFormat(),
          TransformerFactory.FORMAT.HTML_METATAGS_DC_XML, itemXml, null);

      String result = resHighwire + resDC;

      return result;
    } catch (Exception e1) {
      logger.error("could not create html metatags", e1);
    }

    return null;
  }

  public String addDoi() {
    final String navigateTo = ViewItemFull.LOAD_VIEWITEM;
    String retVal = "";
    ItemControllerSessionBean icsb = this.getItemControllerSessionBean();

    try {
      // get a new DOI including a consistency check
      String doi = DoiRestService.getNewDoi(this.getPubItem());
      this.getPubItem().getMetadata().getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.DOI, doi));

      retVal = icsb.saveCurrentPubItem(navigateTo);

      if (navigateTo.equals(retVal)) {
        retVal = icsb.releaseCurrentPubItem(navigateTo, "Release during adding DOI");
      }

      if (navigateTo.equals(retVal)) {
        this.info(this.getMessage("ViewItem_doiAddedSuccessfully"));
        this.getPubItemListSessionBean().update();
      } else {
        this.error(this.getMessage("ViewItem_doiAddingProblem"));
      }
    } catch (Exception e) {
      logger.error("Error creating new DOI", e);
      this.error(this.getMessage("ViewItem_doiAddingProblem") + "--\n" + e.getMessage());
    }

    return retVal;
  }

  public String addSsrnTag() {
    this.getPubItem().getObject().getLocalTags().add(ViewItemFull.SSRN_LOCAL_TAG);

    return this.getSsrnReturnValue("Submission during adding SSRN-Tag.", "ViewItem_ssrnAddedSuccessfully", "ViewItem_ssrnAddingProblem");
  }

  public String removeSsrnTag() {
    this.getPubItem().getObject().getLocalTags().remove(ViewItemFull.SSRN_LOCAL_TAG);

    return this.getSsrnReturnValue("Submission during removing SSRN-Tag.", "ViewItem_ssrnRemovedSuccessfully",
        "ViewItem_ssrnRemovingProblem");
  }

  private String getSsrnReturnValue(String messageSubmit, String messageSuccess, String messageError) {
    String navigateTo = ViewItemFull.LOAD_VIEWITEM;
    String retVal = "";
    ItemControllerSessionBean icsb = this.getItemControllerSessionBean();
    ItemVersionRO.State state = this.getPubItem().getVersionState();

    try {
      retVal = icsb.saveCurrentPubItem(navigateTo);
      if (ItemVersionRO.State.RELEASED.equals(state)) {
        if (this.isModerator) {
          navigateTo = ReleaseItem.LOAD_RELEASEITEM;
          if (navigateTo.equals(retVal)) {
            retVal = icsb.submitCurrentPubItem(navigateTo, messageSubmit);
          }
        } else {
          navigateTo = SubmitItem.LOAD_SUBMITITEM;
        }
      }

      if (navigateTo.equals(retVal)) {
        this.info(this.getMessage(messageSuccess));
        this.getPubItemListSessionBean().update();
      } else {
        this.error(this.getMessage(messageError));
      }
    } catch (Exception e) {
      logger.error("Problems with validation", e);
      this.error(this.getMessage("ViewItem_doiAddingProblem") + "--\n" + e.getMessage());
    }

    return retVal;
  }

  private PubItemListSessionBean getPubItemListSessionBean() {
    return FacesTools.findBean("PubItemListSessionBean");
  }

  private PubItemStorageSessionBean getPubItemStorageSessionBean() {
    return FacesTools.findBean("PubItemStorageSessionBean");
  }

  private PubItemBatchSessionBean getPubItemBatchSessionBean() {
    return FacesTools.findBean("PubItemBatchSessionBean");
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return FacesTools.findBean("ItemControllerSessionBean");
  }

  private ViewItemSessionBean getViewItemSessionBean() {
    return FacesTools.findBean("ViewItemSessionBean");
  }

  private EditItemSessionBean getEditItemSessionBean() {
    return FacesTools.findBean("EditItemSessionBean");
  }

  private ItemVersionListSessionBean getItemVersionListSessionBean() {
    return FacesTools.findBean("ItemVersionListSessionBean");
  }

  private ExportItemsSessionBean getExportItemsSessionBean() {
    return FacesTools.findBean("ExportItemsSessionBean");
  }

  public AccountUserDbVO getModifier() {
    return this.modifier;
  }

  public void setModifier(AccountUserDbVO modifier) {
    this.modifier = modifier;
  }

  public void setOwner(AccountUserDbVO owner) {
    this.owner = owner;
  }

  public AccountUserDbVO getOwner() {
    return this.owner;
  }

  public String getUseExtendedConeAttributes() {
    return PropertyReader.getProperty(PropertyReader.INGE_CONE_EXTENDED_ATTRIBUTES_USE);
  }
}
