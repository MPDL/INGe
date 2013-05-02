/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.pubman.viewItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajax4jsf.component.html.HtmlAjaxRepeat;
import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.www.services.aa.UserAccountHandler;
import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.DepositorWSPage;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.RightsManagementSessionBean;
import de.mpg.escidoc.pubman.ViewItemRevisionsPage;
import de.mpg.escidoc.pubman.ViewItemStatisticsPage;
import de.mpg.escidoc.pubman.acceptItem.AcceptItem;
import de.mpg.escidoc.pubman.acceptItem.AcceptItemSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.basket.PubItemStorageSessionBean;
import de.mpg.escidoc.pubman.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.createItem.CreateItem;
import de.mpg.escidoc.pubman.createItem.CreateItem.SubmissionMethod;
import de.mpg.escidoc.pubman.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.escidoc.pubman.desktop.Login;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.export.ExportItemsSessionBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean;
import de.mpg.escidoc.pubman.itemLog.ViewItemLog;
import de.mpg.escidoc.pubman.releases.ItemVersionListSessionBean;
import de.mpg.escidoc.pubman.releases.ReleaseHistory;
import de.mpg.escidoc.pubman.reviseItem.ReviseItem;
import de.mpg.escidoc.pubman.revisions.RelationListSessionBean;
import de.mpg.escidoc.pubman.search.SearchResultListSessionBean;
import de.mpg.escidoc.pubman.submitItem.SubmitItem;
import de.mpg.escidoc.pubman.submitItem.SubmitItemSessionBean;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.CreatorDisplay;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.ObjectFormatter;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.pubman.viewItem.ViewItemCreators.Type;
import de.mpg.escidoc.pubman.viewItem.bean.SourceBean;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItem;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItemSessionBean;
import de.mpg.escidoc.pubman.yearbook.YearbookInvalidItemRO;
import de.mpg.escidoc.pubman.yearbook.YearbookItemSessionBean;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.ItemAction;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO.AlternativeTitleType;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.ItemExporting;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.pubman.PubItemSimpleStatistics;
import de.mpg.escidoc.services.pubman.statistics.SimpleStatistics;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;




/**
 * Backing bean for ViewItemFull.jspf (for viewing items in a full context).
 * 
 * @author Tobias Schraut, created 03.09.2007
 * @version: $Revision$ $LastChangedDate$
 */
public class ViewItemFull extends FacesBean
{
    private HtmlPanelGroup panelItemFull = new HtmlPanelGroup();
    private static Logger logger = Logger.getLogger(ViewItemFull.class);
    public static final String BEAN_NAME = "ViewItemFull";
    public static final String PARAMETERNAME_ITEM_ID = "itemId";
    public static final String PARAMETERNAME_MENU_VIEW = "view";
    // SSRN local Tag
    private static final String SSRN_LOCAL_TAG = "Tag: SSRN";
    //resolve Handle Service
    private static final String RESOLVE_HANDLE_SERVICE = "http://hdl.handle.net/";
    // Faces navigation string
    public final static String LOAD_VIEWITEM = "loadViewItem";
    public final static String ALTERNATIVE_MODERATOR_EMAIL = "pubman-support@gwdg.de";
    public final static String ISI_KNOWLEDGE_BASE_LINK = "http://gateway.isiknowledge.com/gateway/Gateway.cgi?GWVersion=2&SrcAuth=SFX&SrcApp=SFX&DestLinkType=FullRecord&KeyUT=";
    public final static String ISI_KNOWLEDGE_DEST_APP = "&DestApp=WOS";
    private int defaultSize = 20;
    public boolean isDepositor = false;
    public boolean isModerator = false;
    public boolean isPrivilegedViewer = false;
    // Validation Service
    private ItemValidating itemValidating = null;
    private PubItemVOPresentation pubItem = null;
    private HtmlMessages valMessage = new HtmlMessages();
    // Added by DiT: constant for the function modify and new revision to check the rights and/or if the function has to
    // be disabled (DiT)
    private static final String FUNCTION_MODIFY = "modify";
    private static final String FUNCTION_NEW_REVISION = "new_revision";
    private static final String VALIDATION_ERROR_MESSAGE = "depositorWS_NotSuccessfullySubmitted";
    private HtmlAjaxRepeat titleIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat creatorPersonsIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat creatorOrganizationsIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat creatorAffiliationsIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat abstractIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat eventAltTitleIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat sourceIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat sourceTitleIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat sourceCreatorPersonsIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat sourceCreatorOrganizationsIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat sourceCreatorAffiliationsIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat fileIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat locatorIterator = new HtmlAjaxRepeat();
    private ContextVO context = null;
    private AccountUserVO creator = null;
    
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
    
    //= new ArrayList<SourceBean>();
    private LoginHelper loginHelper;
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
    private boolean isFromEasySubmission;
    private boolean isFromSearchResult;
    private PubItemDepositing pubItemDepositing;
    private boolean isWorkflowStandard;
    private boolean isWorkflowSimple;
    private boolean isStateInRevision;
    // private boolean hasRevision;
    private PubItemSimpleStatistics pubManStatistics;
    private boolean isPublicStateReleased;

    private YearbookItemSessionBean yisb;
    private boolean isMemberOfYearbook;
    private boolean isCandidateOfYearbook;

    //for inclusion into the ViewItemFull page, test if rendering conditions can be made faster
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
    


    /**
     * Public constructor.
     */
    public ViewItemFull()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. Changed by DiT, 15.10.2007: added link for modify
     */
    @Override
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
        String itemID = "";
        this.loginHelper = (LoginHelper)getSessionBean(LoginHelper.class);
        // populate the core service Url
        try
        {
            this.fwUrl = PropertyReader.getProperty("escidoc.framework_access.framework.url");
        }
        catch (IOException ioE)
        {
            throw new RuntimeException(
                    "Could  not read the Property file for property 'escidoc.framework_access.framework.url'", ioE);
        }
        catch (URISyntaxException uE)
        {
            throw new RuntimeException("Syntax of property 'escidoc.framework_access.framework.url' not correct", uE);
        }

        // Try to get the validation service
        try
        {
            InitialContext initialContext = new InitialContext();
            this.pubItemDepositing = (PubItemDepositing)initialContext.lookup(PubItemDepositing.SERVICE_NAME);
            this.itemValidating = (ItemValidating)initialContext.lookup(ItemValidating.SERVICE_NAME);
            this.pubManStatistics = (PubItemSimpleStatistics)initialContext
            .lookup(PubItemSimpleStatistics.SERVICE_NAME);

        }
        catch (NamingException ne)
        {
            throw new RuntimeException("Validation service not initialized", ne);
        }

        try
        {
            this.defaultSize = Integer.parseInt(PropertyReader
                    .getProperty("escidoc.pubman_presentation.viewFullItem.defaultSize"));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Property escidoc.pubman_presentation.viewFullItem.defaultSize size not found",
                    e);
        }


        boolean logViewAction = false;
        // Try to get a pubitem either via the controller session bean or an URL Parameter
        itemID = request.getParameter(ViewItemFull.PARAMETERNAME_ITEM_ID);

        if (itemID != null)
        {
            try
            {
                this.pubItem = this.getItemControllerSessionBean().retrieveItem(itemID);
                // if it is a new item reset ViewItemSessionBean
                if (getItemControllerSessionBean().getCurrentPubItem() == null
                        || !pubItem
                        .getVersion()
                        .getObjectIdAndVersion()
                        .equals(getItemControllerSessionBean().getCurrentPubItem().getVersion()
                                .getObjectIdAndVersion()))
                {
                    getViewItemSessionBean().itemChanged();
                }
                this.getItemControllerSessionBean().setCurrentPubItem(this.pubItem);
                logViewAction = true;
            }
            catch (AuthorizationException e)
            {
                if (loginHelper.isLoggedIn())
                {
                    error(getMessage("ViewItemFull_noPermission"));
                }
                else
                {
                    // redirect to login
                    Login login = (Login)getSessionBean(Login.class);
                    login.forceLogout(itemID);
                }
            }
            catch (AuthenticationException e)
            {
                if (loginHelper.isLoggedIn())
                {
                    error(getMessage("ViewItemFull_noPermission"));
                }
                else
                {
                    // redirect to login
                    Login login = (Login)getSessionBean(Login.class);
                    login.forceLogout(itemID);
                }
            }
            catch (Exception e)
            {
                logger.error("Could not retrieve release with id " + itemID, e);
                error(getMessage("ViewItemFull_invalidID").replace("$1", itemID), e.getMessage());
            }
        }
        else
        {
            this.pubItem = this.getItemControllerSessionBean().getCurrentPubItem();
        }


        String subMenu = request.getParameter(ViewItemFull.PARAMETERNAME_MENU_VIEW);

        if (subMenu != null) getViewItemSessionBean().setSubMenu(subMenu);

        if (this.pubItem != null)
        {

            // set citation url
            try
            {
                String pubmanUrl = PropertyReader.getProperty("escidoc.pubman.instance.url")
                + PropertyReader.getProperty("escidoc.pubman.instance.context.path");

                this.itemPattern = PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1",
                        getPubItem().getVersion().getObjectIdAndVersion());
                if (!pubmanUrl.endsWith("/"))
                    pubmanUrl = pubmanUrl + "/";
                if (this.itemPattern.startsWith("/"))
                    this.itemPattern = this.itemPattern.substring(1, this.itemPattern.length());
                // MF: Removed exclusion of pending items here
                this.citationURL = pubmanUrl + this.itemPattern;

                if(getPubItem().getLatestVersion()!=null && getPubItem().getLatestVersion().getObjectIdAndVersion()!=null)
                {
                    String latestVersionItemPattern = PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1",
                            getPubItem().getLatestVersion().getObjectIdAndVersion());
                    if (latestVersionItemPattern.startsWith("/"))
                        latestVersionItemPattern = latestVersionItemPattern.substring(1, latestVersionItemPattern.length());
                    this.setLatestVersionURL(pubmanUrl + latestVersionItemPattern);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                this.citationURL = "";
            }

            this.isOwner = true;

            if (this.pubItem.getOwner() != null)
            {
                this.isOwner = (this.loginHelper.getAccountUser().getReference() != null ? this.loginHelper
                        .getAccountUser().getReference().getObjectId().equals(this.pubItem.getOwner().getObjectId()) : false);

                if (this.loginHelper.getAccountUser().getReference() != null  && this.loginHelper.getAccountUser().getGrantsWithoutAudienceGrants() != null)
                {
                    this.isModerator = false;
                    this.isPrivilegedViewer=false;
                    this.isDepositor = false;


                    this.isModerator= this.loginHelper.getAccountUser().isModerator(this.pubItem.getContext());
                    this.isDepositor= this.loginHelper.getIsDepositor();
                    this.isPrivilegedViewer = this.loginHelper.getAccountUser().isPrivilegedViewer(this.pubItem.getContext());

                    if (!this.isOwner)
                    {
                        for (GrantVO grant : this.loginHelper.getAccountUser().getGrantsWithoutAudienceGrants())
                        {
                            if (grant.getRole().equals("escidoc:role-system-administrator"))
                            {
                                this.isOwner = true;
                                break;
                            }
                        }
                    }

                }

            }


            // @author Markus Haarlaender - setting properties for Action Links
            this.isLoggedIn = this.loginHelper.isLoggedIn();
            this.isLatestVersion = this.pubItem.getVersion().getVersionNumber() == this.pubItem.getLatestVersion()
            .getVersionNumber();
            this.isLatestRelease = this.pubItem.getVersion().getVersionNumber() == this.pubItem.getLatestRelease()
            .getVersionNumber();
            this.isStateWithdrawn = this.pubItem.getPublicStatus().toString()
            .equals(PubItemVO.State.WITHDRAWN.toString());
            this.isStateSubmitted = this.pubItem.getVersion().getState().toString()
            .equals(PubItemVO.State.SUBMITTED.toString())  && !this.isStateWithdrawn;;
            this.isStateReleased = this.pubItem.getVersion().getState().toString()
            .equals(PubItemVO.State.RELEASED.toString()) && !this.isStateWithdrawn;
            this.isStatePending = this.pubItem.getVersion().getState().toString()
            .equals(PubItemVO.State.PENDING.toString()) && !this.isStateWithdrawn;;
            this.isStateInRevision = this.pubItem.getVersion().getState().toString()
            .equals(PubItemVO.State.IN_REVISION.toString()) && !this.isStateWithdrawn;;
            this.isPublicStateReleased = this.pubItem.getPublicStatus() == PubItemVO.State.RELEASED;
            this.isStateWasReleased  = this.pubItem.getLatestRelease().getObjectId() != null ? true : false;

            // display a warn message if the item version is not the latest
            if (this.isLatestVersion == false)
            {

                warn(getMessage("itemIsNotLatestVersion"));
            }
            try
            {
                this.isWorkflowStandard = (getContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.STANDARD);
                this.isWorkflowSimple = (getContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.SIMPLE);
            }
            catch (Exception e)
            {
                this.isWorkflowSimple = true;
                this.isWorkflowStandard = false;
            }

            if (this.isStateWithdrawn)
            {
                getViewItemSessionBean().itemChanged();
            }
            // set up some pre-requisites
            // the list of numbered affiliated organizations

            createCreatorsList();

            // clear source list first
            if (this.pubItem.getMetadata().getSources().size()>0)
            {
                this.sourceList= new ArrayList<SourceBean>();
                for (int i = 0; i < this.pubItem.getMetadata().getSources().size(); i++)
                {
                    this.sourceList.add(new SourceBean(this.pubItem.getMetadata().getSources().get(i)));
                }
            }

            // the list of files
            // Check if the item is also in the search result list
            PubItemListSessionBean pilsb = (PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class);
            List<PubItemVOPresentation> currentPubItemList = pilsb.getCurrentPartList();

            //removed unnecessary creation of new array list
            //List<SearchHitVO> searchHitList = new ArrayList<SearchHitVO>();
            if (currentPubItemList != null)
            {
                for (int i=0; i<currentPubItemList.size();i++)
                {
                    if ( (this.pubItem.getVersion().getObjectId()
                            .equals(currentPubItemList.get(i).getVersion().getObjectId())) &&

                            (this.pubItem.getVersion().getVersionNumber() == currentPubItemList.get(i).getVersion()
                                    .getVersionNumber()) &&

                                    (currentPubItemList.get(i).getSearchHitList() != null
                                            && currentPubItemList.get(i).getSearchHitList().size() > 0) )
                    {
                        this.pubItem.setSearchResult(true);
                        this.pubItem.setSearchHitList(currentPubItemList.get(i).getSearchHitList());
                        this.pubItem.setScore(currentPubItemList.get(i).getScore());
                        this.pubItem.setSearchHitBeanList();
                        //this.pubItem = new PubItemVOPresentation(new PubItemResultVO(this.pubItem, currentPubItemList.get(i).getSearchHitList(), currentPubItemList.get(i).getScore()));

                    }
                }
            }

        }
        // Unapi Export
        try
        {
            this.unapiURLdownload = PropertyReader.getProperty("escidoc.unapi.download.server");
            this.unapiURLview = PropertyReader.getProperty("escidoc.unapi.view.server");
            this.unapiEscidoc = this.unapiURLdownload + "?id=" + itemID + "&format=escidoc";
            this.unapiEndnote = this.unapiURLdownload + "?id=" + itemID + "&format=endnote";
            this.unapiBibtex = this.unapiURLdownload + "?id=" + itemID + "&format=bibtex";
            this.unapiApa = this.unapiURLdownload + "?id=" + itemID + "&format=apa";
        }
        catch (Exception e)
        {
            logger.error("Error getting unapi url property", e);
            throw new RuntimeException(e);
        }


        /*
        if (logViewAction)
        {
            logViewAction();
        }
        */


        //TODO: remove into separate method, must this be in the initializer?
        //not certain why is this method it always returns null (for languages or not)
        //therefore now it is commented, if needed again to be uncomented and getConeLanguageCode to be fixed

        /*if (this.pubItem.getMetadata().getSubjects().size()>0)
			{
				for (TextVO subject : this.pubItem.getMetadata().getSubjects())
				{
					if (subject.getType() != null && subject.getType().equals(SubjectClassification.ISO639_3.name()))
					{
						try
						{
							subject.setLanguage(CommonUtils.getConeLanguageCode(subject.getValue()));
						}
						catch (Exception e)
						{
							throw new RuntimeException("Error retrieving language code for '" + subject.getValue() + "'", e);
						}
					}
				}
			}*/

        //if item is currently part of invalid yearbook items, show Validation Messages
        //ContextListSessionBean clsb = (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
        if(loginHelper.getIsYearbookEditor())
        {

            yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);

            if(yisb.getYearbookItem() != null)
            {
                if(yisb.getInvalidItemMap().get(getPubItem().getVersion().getObjectId()) != null)
                {
                    try {
                        //revalidate
                        yisb.validateItem(getPubItem());
                        YearbookInvalidItemRO invItem = yisb.getInvalidItemMap().get(getPubItem().getVersion().getObjectId());
                        if(invItem!=null)
                        {
                            ((PubItemVOPresentation)this.getPubItem()).setValidationReport(invItem.getValidationReport());
                        }

                    } catch (Exception e) {
                        logger.error("Error in Yearbook validation", e);
                    }
                }



                try
                {
                    if(ItemVO.State.PENDING.equals(yisb.getYearbookItem().getVersion().getState()))
                    {
                        this.isCandidateOfYearbook = yisb.isCandidate(this.pubItem.getVersion().getObjectId());
                        if(!(this.isCandidateOfYearbook) && yisb.getNumberOfMembers()>0)
                        {
                            this.isMemberOfYearbook = yisb.isMember(this.pubItem.getVersion().getObjectId());
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }
        
        //set SSRN contexts
        try
        {
            String contexts = PropertyReader.getProperty("escidoc.pubman.instance.ssrn_contexts");
            if (contexts != null && !"".equals(contexts))
            {
                this.ssrnContexts = new ArrayList<String> ();
                while (contexts.contains(","))
                {
                    this.ssrnContexts.add(contexts.substring(0, contexts.indexOf(",")));
                    contexts = contexts.substring(contexts.indexOf(",") + 1, contexts.length());
                }
                this.ssrnContexts.add(contexts);
            }
            
        }
        catch (Exception e)
        {
            logger.error("couldn't load ssrn context list", e);
        }
        
        setLinks();

    }
    
    public boolean isSsrnContext() 
    {
        if (this.ssrnContexts != null && this.ssrnContexts.contains(this.getPubItem().getContext().getObjectId())){
            return true;
        }
        else 
        {
            return false;
        }
    }
    
    public boolean isSsrnTagged()
    {
        if (this.getPubItem().getLocalTags().contains(ViewItemFull.SSRN_LOCAL_TAG)){
            return true;
        }
        else 
        {
            return false;
        }
    }
    
    public String addSsrnTag() 
    {
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        String returnValue = "";
        this.getPubItem().getLocalTags().add(ViewItemFull.SSRN_LOCAL_TAG);
        if ((ItemVO.State.PENDING).equals(this.getPubItem().getVersion().getState()) || (ItemVO.State.IN_REVISION).equals(this.getPubItem().getVersion().getState()))
        {
            returnValue = icsb.saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false);
            if (!"".equals(returnValue) && !ErrorPage.LOAD_ERRORPAGE.equals(returnValue))
            {
                info(getMessage("ViewItem_ssrnAddedSuccessfully"));
            }
        }
        else if ((ItemVO.State.SUBMITTED).equals(this.getPubItem().getVersion().getState()) || ((ItemVO.State.RELEASED).equals(this.getPubItem().getVersion().getState()) && !this.canRelease))
        {
            returnValue = icsb.saveAndSubmitCurrentPubItem("Set SSRN-Tag", ViewItemFull.LOAD_VIEWITEM);
            if (!"".equals(returnValue) && !ErrorPage.LOAD_ERRORPAGE.equals(returnValue))
            {
                info(getMessage("ViewItem_ssrnAddedSuccessfully"));
            }
        }
        else if ((ItemVO.State.RELEASED).equals(this.getPubItem().getVersion().getState()) && this.canRelease)
        {
            returnValue = icsb.saveAndSubmitCurrentPubItem("Set SSRN-Tag", ViewItemFull.LOAD_VIEWITEM);
            if (!"".equals(returnValue) && !ErrorPage.LOAD_ERRORPAGE.equals(returnValue))
            {
                info(getMessage("ViewItem_ssrnAddedSuccessfully"));
            }
        }
        else 
        {
            error(getMessage("ViewItem_ssrnAddingProblem"));
        }
        PubItemListSessionBean pubItemListSessionBean = (PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class);
        if (pubItemListSessionBean != null)
        {
            pubItemListSessionBean.update();
        }
        return returnValue;
    }
    
    public String removeSsrnTag ()
    {
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        String returnValue = "";
        this.getPubItem().getLocalTags().remove(ViewItemFull.SSRN_LOCAL_TAG);
        if ((ItemVO.State.PENDING).equals(this.getPubItem().getVersion().getState()) || (ItemVO.State.IN_REVISION).equals(this.getPubItem().getVersion().getState()))
        {
            returnValue =  icsb.saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false);
            if (!"".equals(returnValue) && !ErrorPage.LOAD_ERRORPAGE.equals(returnValue))
            {
                info(getMessage("ViewItem_ssrnRemovedSuccessfully"));
            }
        }
        else if ((ItemVO.State.SUBMITTED).equals(this.getPubItem().getVersion().getState()) || ((ItemVO.State.RELEASED).equals(this.getPubItem().getVersion().getState()) && !this.canRelease))
        {
            returnValue =  icsb.saveAndSubmitCurrentPubItem("Set SSRN-Tag", ViewItemFull.LOAD_VIEWITEM);
            if (!"".equals(returnValue) && !ErrorPage.LOAD_ERRORPAGE.equals(returnValue))
            {
                info(getMessage("ViewItem_ssrnRemovedSuccessfully"));
            }
        }
        else if ((ItemVO.State.RELEASED).equals(this.getPubItem().getVersion().getState()) && this.canRelease)
        {
            returnValue =  icsb.saveAndSubmitCurrentPubItem("Set SSRN-Tag", ViewItemFull.LOAD_VIEWITEM);
            if (!"".equals(returnValue) && !ErrorPage.LOAD_ERRORPAGE.equals(returnValue))
            {
                info(getMessage("ViewItem_ssrnRemovedSuccessfully"));
            }
        }
        else {
            error(getMessage("ViewItem_ssrnRemovingProblem"));
        }
        PubItemListSessionBean pubItemListSessionBean = (PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class);
        if (pubItemListSessionBean != null)
        {
            pubItemListSessionBean.update();
        }
        return returnValue;
    }
    
    public String addToYearbookMember()
    {
        List<ItemRO> selected = new ArrayList<ItemRO>();
        selected.add(this.getPubItem().getVersion());
        yisb.addMembers(selected);
        this.isCandidateOfYearbook = false;
        this.isMemberOfYearbook = true;
        return "";
    }

    public String removeMemberFromYearbook()
    {
        List<ItemRO> selected = new ArrayList<ItemRO>();
        selected.add(this.getPubItem().getVersion());
        yisb.removeMembers(selected);
        this.isMemberOfYearbook = false;
        this.isCandidateOfYearbook = true;
        return "";

    }
    /**
     * Redirects the user to the edit item page
     * 
     * @return Sring nav rule to load the edit item page
     */
    public String editItem()
    {
        // clear the list of locators and files when start editing an item
        EditItemSessionBean editItemSessionBean = this.getEditItemSessionBean();
        editItemSessionBean.clean();
        return EditItem.LOAD_EDITITEM;
    }

    /**
     * Redirects the user to the withdraw item page
     * 
     * @return Sring nav rule to load the withdraw item page
     */
    public String withdrawItem()
    {
        WithdrawItemSessionBean withdrawItemSessionBean = getWithdrawItemSessionBean();
        withdrawItemSessionBean.setNavigationStringToGoBack(getViewItemSessionBean().getNavigationStringToGoBack());
        return WithdrawItem.LOAD_WITHDRAWITEM;
    }

    /**
     * Redirects the user to the edit item page in modify-mode
     * 
     * @return Sring nav rule to load the editItem item page
     */
    public String modifyItem()
    {
        // clear the list of locators and files when start modifying an item
        EditItemSessionBean editItemSessionBean = this.getEditItemSessionBean();
        editItemSessionBean.clean();
        return EditItem.LOAD_EDITITEM;
    }

    /**
     * Redirects the user to the create new revision page Changed by DiT, 29.11.2007: only show contexts when user has
     * privileges for more than one context
     * 
     * @return Sring nav rule to load the create new revision page
     */
    public String createNewRevision()
    {
        // clear the list of locators and files when start creating a new revision
        EditItemSessionBean editItemSessionBean = this.getEditItemSessionBean();
        editItemSessionBean.clean();
        // Changed by DiT, 29.11.2007: only show contexts when user has privileges for more than one context
        // if there is only one context for this user we can skip the CreateItem-Dialog and create the new item directly
        if (this.getCollectionListSessionBean().getDepositorContextList().size() == 0)
        {
            logger.warn("The user does not have privileges for any context.");
            error(getMessage("ViewItemFull_user_has_no_context"));
            return null;
        }
        else if (this.getCollectionListSessionBean().getDepositorContextList().size() == 1)
        {
            ContextVO context = this.getCollectionListSessionBean().getDepositorContextList().get(0);
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has only privileges for one collection (ID: "
                        + context.getReference().getObjectId() + ")");
            }
            return this.getItemControllerSessionBean().createNewRevision(EditItem.LOAD_EDITITEM,
                    context.getReference(), this.pubItem, null);
        }
        else
        {
            ContextVO context = this.getCollectionListSessionBean().getDepositorContextList().get(0);
            // more than one context exists for this user; let him choose the right one
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has privileges for "
                        + this.getCollectionListSessionBean().getDepositorContextList().size() + " different contexts.");
            }
            this.getRelationListSessionBean().setPubItemVO(this.getItemControllerSessionBean().getCurrentPubItem());
            // Set submission method for correct redirect
            CreateItem createItem = (CreateItem)getSessionBean(CreateItem.class);
            createItem.setMethod(SubmissionMethod.FULL_SUBMISSION);
            return this.getItemControllerSessionBean().createNewRevision(CreateItem.LOAD_CREATEITEM,
                    context.getReference(), this.pubItem, null);
        }
    }

    /**
     * Redirects the user to the View revisions page.
     * 
     * @return Sring nav rule to load the create new revision page.
     */
    public String showRevisions()
    {
        this.getRelationListSessionBean().setPubItemVO(this.getItemControllerSessionBean().getCurrentPubItem());
        /*
         * try {
         * this.getRelationListSessionBean().setRelationList(this.getItemControllerSessionBean().retrieveRevisions(
         * this.getItemControllerSessionBean().getCurrentPubItem())); } catch (Exception e) {
         * logger.error("Error setting revision list", e); }
         */
        return ViewItemRevisionsPage.LOAD_VIEWREVISIONS;
    }

    /**
     * Redirects the user to the statistics page.
     * 
     * @return String nav rule to load the create new revision page.
     */
    public String showStatistics()
    {
        return ViewItemStatisticsPage.LOAD_VIEWSTATISTICS;
    }

    /**
     * Redirects the user to the Item Log page.
     * 
     * @return String nav rule to load the create new revision page.
     */
    public String showItemLog()
    {
        this.getItemVersionListSessionBean().resetVersionLists();
        return ViewItemLog.LOAD_ITEM_LOG;
    }

    /**
     * submits the selected item(s) an redirects the user to the page he came from (depositor workspace or search result
     * list) Changed by FrM: Inserted validation and call to "enter submission comment" page.
     * 
     * @return String nav rule to load the page the user came from
     */
    public String submitItem()
    {
        /*
         * FrM: Validation with validation point "submit_item"
         */
        PubItemVO pubItem = new PubItemVO(this.getItemControllerSessionBean().getCurrentPubItem());
        ValidationReportVO report = null;
        try
        {
            report = this.itemValidating.validateItemObject(pubItem, "submit_item");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Validation error", e);
        }
        logger.debug("Validation Report: " + report);
        if (report.isValid() && !report.hasItems())
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Submitting item...");
            }
            getSubmitItemSessionBean().setNavigationStringToGoBack(
                    getViewItemSessionBean().getNavigationStringToGoBack());
            return SubmitItem.LOAD_SUBMITITEM;
        }
        else if (report.isValid())
        {
            // TODO FrM: Informative messages
            getSubmitItemSessionBean().setNavigationStringToGoBack(
                    getViewItemSessionBean().getNavigationStringToGoBack());
            return SubmitItem.LOAD_SUBMITITEM;
        }
        else
        {
            // Item is invalid, do not submit anything.
            this.showValidationMessages(report);
            return null;
        }
    }

    public String acceptItem()
    {
        /*
         * FrM: Validation with validation point "submit_item"
         */
        PubItemVO pubItem = new PubItemVO(this.getItemControllerSessionBean().getCurrentPubItem());
        ValidationReportVO report = null;
        try
        {
            report = this.itemValidating.validateItemObject(pubItem, "accept_item");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Validation error", e);
        }
        logger.debug("Validation Report: " + report);
        if (report.isValid() && !report.hasItems())
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Accepting item...");
            }
            getAcceptItemSessionBean().setNavigationStringToGoBack(
                    getViewItemSessionBean().getNavigationStringToGoBack());
            return AcceptItem.LOAD_ACCEPTITEM;
        }
        else if (report.isValid())
        {
            // TODO FrM: Informative messages
            getAcceptItemSessionBean().setNavigationStringToGoBack(
                    getViewItemSessionBean().getNavigationStringToGoBack());
            return AcceptItem.LOAD_ACCEPTITEM;
        }
        else
        {
            // Item is invalid, do not submit anything.
            this.showValidationMessages(report);
            return null;
        }
    }

    /**
     * Returns a reference to the scoped data bean (the AcceptItemSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected AcceptItemSessionBean getAcceptItemSessionBean()
    {
        return (AcceptItemSessionBean)getBean(AcceptItemSessionBean.class);
    }

    /**
     * deletes the selected item(s) an redirects the user to the page he came from (depositor workspace or search result
     * list)
     * 
     * @return String nav rule to load the page the user came from
     */
    public String deleteItem()
    {
        if (getViewItemSessionBean().getNavigationStringToGoBack() == null)
        {
            getViewItemSessionBean().setNavigationStringToGoBack(MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS);
        }
        String retVal = this.getItemControllerSessionBean().deleteCurrentPubItem(
                this.getViewItemSessionBean().getNavigationStringToGoBack());
        // show message
        if (!retVal.equals(ErrorPage.LOAD_ERRORPAGE))
        {
            info(getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_DELETED));
            // redirect to last breadcrumb, if available
            BreadcrumbItemHistorySessionBean bhsb = (BreadcrumbItemHistorySessionBean)getSessionBean(BreadcrumbItemHistorySessionBean.class);
            try
            {
                for (int i = bhsb.getBreadcrumbs().size() - 1; i > 0; i--)
                {
                    if (bhsb.getBreadcrumbs().get(i - 1).isItemSpecific() == false && bhsb.getBreadcrumbs().get(i-1).getDisplayValue().equalsIgnoreCase("CreateItemPage")==false)
                    {
                        getFacesContext().getExternalContext().redirect(bhsb.getBreadcrumbs().get(i - 1).getPage());
                        return retVal;
                    }
                }
            }
            catch (IOException e)
            {
                logger.error("Could not redirect to last breadcrumb!");
                return "loadHome";
            }
        }
        return retVal;
    }

    //TODO NBU: check if this method is indeed used or not
    /**
     * Adds a cookie named "escidocCookie" that holds the eScidoc user handle to the provided http method object.
     * 
     * @author Tobias Schraut
     * @param method The http method to add the cookie to.
	private void addHandleToMethod(final HttpMethod method, String eSciDocUserHandle)
	{
		// Staging file resource is protected, access needs authentication and
		// authorization. Therefore, the eSciDoc user handle must be provided.
		// Put the handle in the cookie "escidocCookie"
		method.setRequestHeader("Cookie", "escidocCookie=" + eSciDocUserHandle);
	}
     */

    /**
     * Displays validation messages.
     * 
     * @param report The Validation report object.
     * @author Michael Franke
     */
    private void showValidationMessages(ValidationReportVO report)
    {
        warn(getMessage(VALIDATION_ERROR_MESSAGE));
        for (Iterator<ValidationReportItemVO> iter = report.getItems().iterator(); iter.hasNext();)
        {
            ValidationReportItemVO element = iter.next();
            if (element.isRestrictive())
            {
                error(getMessage(element.getContent()));
            }
            else
            {
                info(getMessage(element.getContent()));
            }
        }
        this.valMessage.setRendered(true);
    }

    /**
     * Generates the affiliated organization list as one string for presenting it in the jsp via the dynamic html
     * component. Duplicate affiliated organizations will be detected and merged. All affiliated organizations will be numbered.
     * 
     */
    private void createCreatorsList()
    {

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
        ObjectFormatter formatter = new ObjectFormatter();

        //temporary list of All creators, retrieved directly from the metadata
        tempCreatorList = this.pubItem.getMetadata().getCreators();
        //the list of creators is initialized to a new array list
        this.setCreators(new ArrayList<ViewItemCreators>());
        //initial affiliation position set to 0
        int affiliationPosition = 0;

        //for each creator in the list
        for (int i = 0; i < tempCreatorList.size(); i++)
        {

            //temporary organization list is matched against the sorted for each separate creator
            //therefore for each creator is newly re-set
            tempOrganizationList = new ArrayList<OrganizationVO>();

            //put creator in temporary VO
            CreatorVO creator1 = new CreatorVO();
            creator1 = tempCreatorList.get(i);

            //annotation = new StringBuffer();
            //int organizationsFound = 0;
            ViewItemCreators creator = new ViewItemCreators();
            CreatorDisplay creatorDisplay = new CreatorDisplay();

            //if the creator is a person add his organization to the sorted organization list
            if (creator1.getPerson() != null)
            {
                //if there is affiliated organization for this creator
                if (creator1.getPerson().getOrganizations().size() > 0)
                {
                    //add each affiliated organization of the creator to the temporary organization list
                    for (int listSize = 0; listSize < creator1.getPerson().getOrganizations().size(); listSize++)
                    {
                        tempOrganizationList.add(creator1.getPerson().getOrganizations().get(listSize));
                    }

                    //for each organizations in the temporary organization list
                    for (int j = 0; j < tempOrganizationList.size(); j++)
                    {
                        // check if the organization in the list is in the sorted organization list
                        if (!sortOrganizationList.contains(tempOrganizationList.get(j)))
                        {
                            affiliationPosition++;
                            //if the temporary organization is to be added to the sorted set of organizations
                            sortOrganizationList.add(tempOrganizationList.get(j));
                            //create new Organization view object
                            this.getOrganizationList().add(formatCreatorOrganization(tempOrganizationList.get(j), affiliationPosition));
                        }
                    }
                }

                formattedCreator=formatter.formatCreator(creator1,formatCreatorOrganizationIndex(creator1,sortOrganizationList));
                creatorDisplay.setFormattedDisplay(formattedCreator);

                if (creator1.getPerson().getIdentifier() != null
                        && (creator1.getPerson().getIdentifier().getType() == IdType.CONE))
                {
                    try
                    {
                        creatorDisplay.setPortfolioLink(creator1.getPerson().getIdentifier().getId());
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                creator.setCreatorType(Type.PERSON.toString());
                creator.setCreatorObj(creatorDisplay);
                creator.setCreatorRole(creator1.getRoleString());

                this.creators.add(creator);
            } //end if creator is a person

            if (creator1.getOrganization() != null)
            {
                formattedCreator=formatter.formatCreator(creator1,"");
                creatorDisplay.setFormattedDisplay(formattedCreator);
                ViewItemCreatorOrganization creatorOrganization = new ViewItemCreatorOrganization();
                creatorOrganization.setOrganizationName(formattedCreator);
                creatorOrganization.setPosition(new Integer(counterOrganization).toString());
                creatorOrganization.setOrganizationAddress(creator1.getOrganization().getAddress());
                creatorOrganization.setOrganizationInfoPage(formattedCreator, creator1.getOrganization().getAddress());
                creatorOrganization.setIdentifier(creator1.getOrganization().getIdentifier());
                creator.setCreatorType(Type.ORGANIZATION.toString());
                creator.setCreatorObj(creatorOrganization);
                creator.setCreatorRole(creator1.getRoleString());
                this.creators.add(creator);
            }

            counterOrganization++;
            //creatorListString.append(formattedCreator);
            this.setAffiliatedOrganizationsList(sortOrganizationList);
            //this.affiliatedOrganizationsList = sortOrganizationList;
            // generate a 'well-formed' list for presentation in the jsp
            for (int k = 0; k < sortOrganizationList.size(); k++)
            {
                String name = sortOrganizationList.get(k).getName() != null ? sortOrganizationList.get(k).getName()
                        .getValue() : "";
                        formattedOrganization = "<p>" + (k + 1) + ": " + name + "</p>" + "<p>"
                        + sortOrganizationList.get(k).getAddress() + "</p>" + "<p>"
                        + sortOrganizationList.get(k).getIdentifier() + "</p>";
                        this.organizationArray.add(formattedOrganization);
                        //this.getOrganizationArray().add(formattedOrganization);
            }
        } //end for each creator in the list

    }

    /**
     * Returns the formatted Organization for view item
     * 
     * @return ViewItemOrganization
     * @param  tempOrganizationListInstance List of organizations that need to be sorted
     * @param  int The position of the affiliation in the list of the organizations
     */
    public static ViewItemOrganization formatCreatorOrganization (OrganizationVO tempOrganizationListInstance, int affiliationPosition)
    {
        ViewItemOrganization viewOrganization = new ViewItemOrganization();
        //set the organization view object to values from the current temp organization
        if(tempOrganizationListInstance.getName() != null)
        {
            viewOrganization.setOrganizationName(tempOrganizationListInstance.getName().getValue());
            //}
            viewOrganization.setOrganizationAddress(tempOrganizationListInstance.getAddress() );
            viewOrganization.setOrganizationIdentifier(tempOrganizationListInstance.getIdentifier());
            viewOrganization.setPosition(new Integer(affiliationPosition).toString());
            //if(tempOrganizationList.get(j).getName() != null)
            //{
            viewOrganization.setOrganizationInfoPage(tempOrganizationListInstance.getName().getValue(),
                    tempOrganizationListInstance.getAddress());

            viewOrganization.setOrganizationDescription(tempOrganizationListInstance.getName().getValue(),
                    tempOrganizationListInstance.getAddress(), tempOrganizationListInstance.getIdentifier());

        }
        return viewOrganization;

    }

    /**
     * formats the Organization index of creator
     * @return String
     * @param creator creator object for which the organization index shall be set
     * @param sortOrganizationList sorted list of organizations in the publication item
     */
    public static String formatCreatorOrganizationIndex(CreatorVO creator, List<OrganizationVO> sortOrganizationList)
    {
        int organizationsFound = 0;
        StringBuffer annotation = new StringBuffer();
        //go through known sorted organizations and format the number at the creator
        for (int j = 0; j < sortOrganizationList.size(); j++)
        {
            if (creator.getPerson().getOrganizations().contains(sortOrganizationList.get(j)))
            {
                if (organizationsFound == 0)
                {
                    annotation.append("<sup>");
                }
                if (organizationsFound > 0 && j < sortOrganizationList.size())
                {
                    annotation.append(", ");
                }
                annotation.append(new Integer(j + 1).toString());
                organizationsFound++;
            }
        }

        if (annotation.length() > 0)
        {
            annotation.append("</sup>");
        }

        return annotation.toString();

    }

    /**
     * Returns the formatted Publishing Info according to filled elements
     * 
     * @return String the formatted Publishing Info
     */
    public String getPublishingInfo()
    {
        StringBuffer publishingInfo = new StringBuffer();
        publishingInfo.append("");
        if (this.pubItem.getMetadata().getPublishingInfo() != null)
        {
            // Place
            if (this.pubItem.getMetadata().getPublishingInfo().getPlace() != null
                    && !this.pubItem.getMetadata().getPublishingInfo().getPlace().equals(""))
            {
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPlace().trim());
            }
            // colon
            if (this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null
                    && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim().equals("")
                    && this.pubItem.getMetadata().getPublishingInfo().getPlace() != null
                    && !this.pubItem.getMetadata().getPublishingInfo().getPlace().trim().equals(""))
            {
                publishingInfo.append(" : ");
            }
            // Publisher
            if (this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null
                    && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().equals(""))
            {
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim());
            }
            // Comma
            if ((this.pubItem.getMetadata().getPublishingInfo().getEdition() != null && !this.pubItem.getMetadata()
                    .getPublishingInfo().getEdition().trim().equals(""))
                    && ((this.pubItem.getMetadata().getPublishingInfo().getPlace() != null && !this.pubItem
                            .getMetadata().getPublishingInfo().getPlace().trim().equals("")) || (this.pubItem
                                    .getMetadata().getPublishingInfo().getPublisher() != null && !this.pubItem.getMetadata()
                                    .getPublishingInfo().getPublisher().trim().equals(""))))
            {
                publishingInfo.append(", ");
            }
            // Edition
            if (this.pubItem.getMetadata().getPublishingInfo().getEdition() != null)
            {
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
    public String getIdentifiers()
    {
        return getIdentifierHtmlString(this.pubItem.getMetadata().getIdentifiers());
    }

    public static String getIdentifierHtmlString(List<IdentifierVO> idList)
    {
        StringBuffer identifiers = new StringBuffer();
        if (idList != null)
        {
            for (int i = 0; i < idList.size(); i++)
            {
                try
                {
                    String labelKey = "ENUM_IDENTIFIERTYPE_" + idList.get(i).getTypeString();
                    identifiers.append(getLabelStatic(labelKey));
                }
                catch (MissingResourceException e)
                {
                    logger.debug("Found no label for identifier type " + idList.get(i).getTypeString());
                    identifiers.append(idList.get(i).getTypeString());
                }
                identifiers.append(": ");
                if (CommonUtils.getisUriValidUrl(idList.get(i)))
                {
                    identifiers.append("<a target='_blank' href='" + idList.get(i).getId() + "'>"
                            + idList.get(i).getId() + "</a>");
                }
                else if (idList.get(i).getType() == IdType.DOI)
                {
                    identifiers.append("<a target='_blank' href='http://dx.doi.org/" + idList.get(i).getId() + "'>"
                            + idList.get(i).getId() + "</a>");
                }
                else if (idList.get(i).getType() == IdType.EDOC)
                {
                    identifiers.append("<a target='_blank' href='http://edoc.mpg.de/" + idList.get(i).getId() + "'>"
                            + idList.get(i).getId() + "</a>");
                }
                else if (idList.get(i).getType() == IdType.ISI)
                {
                    identifiers.append("<a target='_blank' href='" + ISI_KNOWLEDGE_BASE_LINK + idList.get(i).getId()
                            + ISI_KNOWLEDGE_DEST_APP + "'>" + idList.get(i).getId() + "</a>");
                }
                else
                {
                    identifiers.append(idList.get(i).getId());
                }
                if (i < idList.size() - 1)
                {
                    identifiers.append("<br/>");
                }
            }
        }
        return identifiers.toString();
    }

    public static String getLabelStatic(String placeholder)
    {
        InternationalizationHelper i18nHelper = (InternationalizationHelper)getSessionBean(InternationalizationHelper.class);
        return ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle()).getString(placeholder);
    }

    /**
     * Returns a true or a false according top the existance of specified fields in the details section
     * 
     * @return boolean
     */
    public boolean getShowDetails()
    {
        if (this.pubItem.getMetadata() != null)
        {
            if ((this.pubItem.getMetadata().getLanguages() != null && this.pubItem.getMetadata().getLanguages().size() > 0)
                    || (getShowDates())
                    || (this.pubItem.getMetadata().getTotalNumberOfPages() != null && !this.pubItem.getMetadata()
                            .getTotalNumberOfPages().trim().equals(""))
                            || (this.pubItem.getMetadata().getPublishingInfo() != null)
                            || (this.pubItem.getMetadata().getTableOfContents() != null
                                    && this.pubItem.getMetadata().getTableOfContents().getValue() != null && !this.pubItem
                                    .getMetadata().getTableOfContents().getValue().trim().equals(""))
                                    || (this.pubItem.getMetadata().getReviewMethod() != null)
                                    || (this.pubItem.getMetadata().getIdentifiers() != null && this.pubItem.getMetadata()
                                            .getIdentifiers().size() > 0)
                                            || (this.pubItem.getMetadata().getDegree() != null)
                                            || (this.pubItem.getMetadata().getLocation() != null && !this.pubItem.getMetadata().getLocation()
                                                    .trim().equals("")))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    public String getLanguages() throws Exception
    {
        if (this.pubItem.getMetadata().getLanguages() == null || this.pubItem.getMetadata().getLanguages().size() == 0)
        {
            return "";
        }
        else
        {
            StringWriter result = new StringWriter();
            for (int i = 0; i < this.pubItem.getMetadata().getLanguages().size(); i++)
            {

                if (i > 0)
                {
                    result.append(", ");
                }

                String language = this.pubItem.getMetadata().getLanguages().get(i);
                InternationalizationHelper internationalizationHelper = (InternationalizationHelper) getSessionBean(InternationalizationHelper.class);
                String languageName = CommonUtils.getConeLanguageName(language, internationalizationHelper.getLocale());
                result.append(language);
                if (languageName != null && !"".equals(languageName))
                {
                    result.append(" - ");
                    result.append(languageName);
                }
            }
            return result.toString();
        }
    }

    /**
     * Returns a true or a false according to the existance of an event in the item
     * 
     * @return boolean
     */
    public boolean getShowEvents()
    {
        if (this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getEvent() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns a true or a false according to the existance of sources in the item
     * 
     * @return boolean
     */
    public boolean getShowSources()
    {
        if (this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getSources() != null
                && this.pubItem.getMetadata().getSources().size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns a true or a false according to the existance of files in the item
     * 
     * @return boolean
     */
    public boolean getShowFiles()
    {
        if (this.pubItem.getFileBeanList() != null && this.pubItem.getFileBeanList().size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the total number of files in the item
     * 
     * @return int
     */
    public int getAmountOfFiles()
    {
        if (this.pubItem.getFileBeanList() != null && this.pubItem.getFileBeanList().size() > 0)
        {
            return this.pubItem.getFileBeanList().size();
        }
        else
        {
            return 0;
        }
    }

    /**
     * Returns a true or a false according to the existance of locators in the item
     * 
     * @return boolean
     */
    public boolean getShowLocators()
    {
        if (this.pubItem.getLocatorBeanList() != null && this.pubItem.getLocatorBeanList().size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the total number of locators in the item
     * 
     * @return int
     */
    public int getAmountOfLocators()
    {
        if (this.pubItem.getLocatorBeanList() != null && this.pubItem.getLocatorBeanList().size() > 0)
        {
            return this.pubItem.getLocatorBeanList().size();
        }
        else
        {
            return 0;
        }
    }

    /**
     * Returns a true or a false according to the user state (logged in or not)
     * 
     * @author Markus Haarlaender
     * @return boolean
     */
    public boolean getShowSystemDetails()
    {
        return this.loginHelper.isLoggedIn();
    }

    /**
     * Returns a boolean according to the user item state
     * 
     * @author Markus Haarlaender
     * @return boolean
     */
    public boolean getShowCiteItem()
    {
        if (getPubItem().getPublicStatus().equals(State.WITHDRAWN))
        {
            return false;
        }
        return getPubItem().getVersion().getState().equals(PubItemVO.State.RELEASED);
    }

    public String getDates()
    {
        List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
        pubItemList.add(getPubItem());
        List<PubItemVOPresentation> pubItemPresentationList = CommonUtils
        .convertToPubItemVOPresentationList(pubItemList);
        PubItemVOPresentation pubItemPresentation = pubItemPresentationList.get(0);
        return pubItemPresentation.getDatesAsString();
    }

    /**
     * Returns false if all dates are empty
     * 
     * @author Markus Haarlaender
     * @return boolean
     */
    public boolean getShowDates()
    {
        return ((this.getPubItem().getMetadata().getDatePublishedInPrint() != null && !this.getPubItem().getMetadata()
                .getDatePublishedInPrint().equals(""))
                || (this.getPubItem().getMetadata().getDatePublishedOnline() != null && !this.getPubItem()
                        .getMetadata().getDatePublishedOnline().equals(""))
                        || (this.getPubItem().getMetadata().getDateAccepted() != null && !this.getPubItem().getMetadata()
                                .getDateAccepted().equals(""))
                                || (this.getPubItem().getMetadata().getDateSubmitted() != null && !this.getPubItem().getMetadata()
                                        .getDateSubmitted().equals(""))
                                        || (this.getPubItem().getMetadata().getDateModified() != null && !this.getPubItem().getMetadata()
                                                .getDateModified().equals("")) || (this.getPubItem().getMetadata().getDateCreated() != null && !this
                                                        .getPubItem().getMetadata().getDateCreated().equals("")));
    }

    /**
     * Returns a true or a false according to the invited state of the item
     * 
     * @return boolean
     */
    public boolean getInvited()
    {
        if (this.pubItem.getMetadata().getEvent().getInvitationStatus() != null)
        {
            if (this.pubItem.getMetadata().getEvent().getInvitationStatus().equals(EventVO.InvitationStatus.INVITED))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns a true or a false according to the state of the current item
     * 
     * @return boolean
     */
    public boolean getItemIsWithdrawn()
    {
        if (this.pubItem.getVersion().getState().equals(PubItemVO.State.WITHDRAWN))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the formatted withdrawal date as string
     * 
     * @return String formatted withdrawal date
     */
    public String getWithdrawalDate()
    {
        String date = "";
        if (this.pubItem.getPublicStatus().equals(PubItemVO.State.WITHDRAWN))
        {
            if (this.pubItem.getModificationDate() != null)
            {
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
    public String getContextName()
    {
        String contextName = "";
        if (this.context == null)
        {
            ItemControllerSessionBean itemControllerSessionBean = getItemControllerSessionBean();
            try
            {
                this.context = itemControllerSessionBean.retrieveContext(this.pubItem.getContext().getObjectId());
            }
            catch (Exception e)
            {
                logger.error("Error retrieving context", e);
            }
        }
        if (this.context != null)
        {
            contextName = this.context.getName();
        }
        return contextName;
    }

    /**
     * Gets the name of the Collection the item belongs to.
     * 
     * @return String formatted Collection name
     */
    public String getCreatorName()
    {
        if (this.creator == null)
        {
            ItemControllerSessionBean itemControllerSessionBean = getItemControllerSessionBean();
            try
            {
                this.creator = itemControllerSessionBean.retrieveCreator(this.pubItem.getOwner().getObjectId());
            }
            catch (Exception e)
            {
                logger.error("Error retrieving context", e);
            }
        }
        return creator.getName();
    }

    /**
     * Returns the Context the item belongs to
     */
    public ContextVO getContext()
    {
        if (this.context == null)
        {
            this.context = getItemControllerSessionBean().getCurrentContext();
        }
        return this.context;
    }

    /**
     * Gets the affiliation of the context the item belongs to.
     * 
     * @return String formatted context name
     */
    public String getAffiliations()
    {
        StringBuffer affiliations = new StringBuffer();
        List<AffiliationRO> affiliationRefList = new ArrayList<AffiliationRO>();
        List<AffiliationVOPresentation> affiliationList = new ArrayList<AffiliationVOPresentation>();
        ItemControllerSessionBean itemControllerSessionBean = getItemControllerSessionBean();
        if (this.context == null)
        {
            try
            {
                this.context = itemControllerSessionBean.retrieveContext(this.pubItem.getContext().getObjectId());
            }
            catch (Exception e)
            {
                logger.error("Error retrieving collection", e);
            }
        }
        if (this.context != null)
        {
            affiliationRefList = this.context.getResponsibleAffiliations();
        }
        // first get all affiliations
        if (affiliationRefList != null)
        {
            for (int i = 0; i < affiliationRefList.size(); i++)
            {
                try
                {
                    affiliationList.add(new AffiliationVOPresentation(ItemControllerSessionBean
                            .retrieveAffiliation(affiliationRefList.get(i).getObjectId())));
                }
                catch (Exception e)
                {
                    logger.error("Error retrieving affiliation list", e);
                }
            }
        }
        // then extract the names and add to StringBuffer
        for (int i = 0; i < affiliationList.size(); i++)
        {
            affiliations.append(affiliationList.get(i).getDetails().getName());
            if (i < affiliationList.size() - 1)
            {
                affiliations.append(", ");
            }
        }
        return affiliations.toString();
    }

    /**
     * Returns a formatted String including the start and the end date of the event
     * 
     * @return String the formatted date string
     */
    public String getStartEndDate()
    {
        StringBuffer date = new StringBuffer();
        if (this.pubItem.getMetadata().getEvent().getStartDate() != null)
        {
            date.append(this.pubItem.getMetadata().getEvent().getStartDate());
        }
        if (this.pubItem.getMetadata().getEvent().getEndDate() != null)
        {
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
    public String getModificationDate()
    {
        return CommonUtils.formatTimestamp(this.pubItem.getModificationDate());
    }
    
    public String getLatestModifier() throws Exception
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        InitialContext initialContext = new InitialContext();
        XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
        UserAccountHandler userAccountHandler = null;
        if (this.pubItem.getVersion().getModifiedByRO() != null && this.pubItem.getVersion().getModifiedByRO().getObjectId() != null)
        {
            HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
            filterParams.put("operation", new String[] {"searchRetrieve"});
            filterParams.put("query", new String[] {"\"/id\"=" + this.pubItem.getVersion().getModifiedByRO().getObjectId()});
            String searchResponse = null;
                    
            userAccountHandler = ServiceLocator.getUserAccountHandler(loginHelper.getESciDocUserHandle());
            searchResponse = userAccountHandler.retrieveUserAccounts(filterParams);
            SearchRetrieveResponseVO searchedObject = xmlTransforming.transformToSearchRetrieveResponseAccountUser(searchResponse);
            
            if (searchedObject != null && searchedObject.getNumberOfRecords() > 0 && !searchedObject.getRecords().isEmpty()) 
            {
                if (searchedObject.getRecords().get(0).getData() != null)
                {
                    AccountUserVO modifier = (AccountUserVO) searchedObject.getRecords().get(0).getData();
                    if (modifier.getName() != null && modifier.getName().trim() != "")
                    {
                        return modifier.getName();
                    }
                    else if (modifier.getUserid() != null && modifier.getUserid() != "")
                    {
                        return modifier.getUserid();
                    }
                    else
                    {
                        return null;
                    }
                }
                else {
                    return null;
                }
            }
            else 
            {
                return null;
            }
        }
        else 
        {
            return null;
        }
        
    }
    
    /**
     * Returns the Creation date as formatted String (YYYY-MM-DD)
     * 
     * @return String the formatted date of modification
     */
    public String getCreationDate()
    {
        return CommonUtils.formatTimestamp(this.pubItem.getCreationDate());
    }
    
    /**
     * Returns the item owner
     * 
     * @return String name or id of the owner
     */
    public String getOwner() throws Exception
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        InitialContext initialContext = new InitialContext();
        XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
        UserAccountHandler userAccountHandler = null;
        
        HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
        if (this.pubItem.getOwner() != null && this.pubItem.getOwner().getObjectId() != null)
        {
            filterParams.put("operation", new String[] {"searchRetrieve"});
            filterParams.put("query", new String[] {"\"/id\"=" + this.pubItem.getOwner().getObjectId()});
        }
        else 
        {
            return null;
        }
        userAccountHandler = ServiceLocator.getUserAccountHandler(loginHelper.getESciDocUserHandle());
        String searchResponse = userAccountHandler.retrieveUserAccounts(filterParams);
        SearchRetrieveResponseVO searchedObject = xmlTransforming.transformToSearchRetrieveResponseAccountUser(searchResponse);
        if (searchedObject != null && searchedObject.getNumberOfRecords() > 0 && !searchedObject.getRecords().isEmpty()) 
        {
            if (searchedObject.getRecords().get(0).getData() != null)
            {
                AccountUserVO owner = (AccountUserVO) searchedObject.getRecords().get(0).getData();
                if (owner.getName() != null && owner.getName().trim() != "")
                {
                    return owner.getName();
                }
                else if (owner.getUserid() != null && owner.getUserid().trim() != "")
                {
                    return owner.getUserid();
                }
                else
                {
                    return null;
                }
            }
            else 
            {
                return null;
            }
            
        }
        else 
        {
            return null;
        }
    }

    /**
     * gets the parameters out of the faces context
     * 
     * @param name name of the parameter in the faces context
     * @return the value of the parameter as string
     */
    public static String getFacesParamValue(String name)
    {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }

    /**
     * Navigates to the release history page.
     * 
     * @return the faces navigation string
     */
    public String showReleaseHistory()
    {
        this.getItemVersionListSessionBean().resetVersionLists();
        return ReleaseHistory.LOAD_RELEASE_HISTORY;
    }

    /**
     * Returns the ItemControllerSessionBean.
     * 
     * @return a reference to the scoped data bean (ItemControllerSessionBean)
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
    }

    /**
     * Returns the ViewItemSessionBean.
     * 
     * @return a reference to the scoped data bean (ViewItemSessionBean)
     */
    protected ViewItemSessionBean getViewItemSessionBean()
    {
        return (ViewItemSessionBean)getSessionBean(ViewItemSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ViewItemSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected WithdrawItemSessionBean getWithdrawItemSessionBean()
    {
        return (WithdrawItemSessionBean)getSessionBean(WithdrawItemSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the EditItemSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected EditItemSessionBean getEditItemSessionBean()
    {
        return (EditItemSessionBean)getSessionBean(EditItemSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the SubmitItemSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected SubmitItemSessionBean getSubmitItemSessionBean()
    {
        return (SubmitItemSessionBean)getSessionBean(SubmitItemSessionBean.class);
    }

    /**
     * Returns the SearchResultListSessionBean.
     * 
     * @return a reference to the scoped data bean (SearchResultListSessionBean)
     */
    protected SearchResultListSessionBean getSearchResultListSessionBean()
    {
        return (SearchResultListSessionBean)getSessionBean(SearchResultListSessionBean.class);
    }

    /**
     * Returns the RightsManagementSessionBean.
     * 
     * @author DiT
     * @return a reference to the scoped data bean (RightsManagementSessionBean)
     */
    protected RightsManagementSessionBean getRightsManagementSessionBean()
    {
        return (RightsManagementSessionBean)getSessionBean(RightsManagementSessionBean.class);
    }

    /**
     * Returns the ReleasesSessionBean.
     * 
     * @return a reference to the scoped data bean (ReleasesSessionBean)
     */
    protected ItemVersionListSessionBean getItemVersionListSessionBean()
    {
        return (ItemVersionListSessionBean)getSessionBean(ItemVersionListSessionBean.class);
    }

    /**
     * Returns the RevisionListSessionBean.
     * 
     * @return a reference to the scoped data bean (RevisionListSessionBean)
     */
    protected RelationListSessionBean getRelationListSessionBean()
    {
        return (RelationListSessionBean)getSessionBean(RelationListSessionBean.class);
    }

    /**
     * Returns the ContextListSessionBean.
     * 
     * @return a reference to the scoped data bean (ContextListSessionBean)
     */
    protected ContextListSessionBean getCollectionListSessionBean()
    {
        return (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
    }

    /**
     * Returns the ApplicationBean.
     * 
     * @return a reference to the scoped data bean (ApplicationBean)
     */
    protected ApplicationBean getApplicationBean()
    {
        return (ApplicationBean)getApplicationBean(ApplicationBean.class);
    }

    /**
     * Returns the ReleaseHistory.
     * 
     * @return a reference to the scoped data bean (RevisionListSessionBean)
     */
    protected ReleaseHistory getReleaseHistory()
    {
        return (ReleaseHistory)getRequestBean(ReleaseHistory.class);
    }

    // Getters and Setters
    public HtmlPanelGroup getPanelItemFull()
    {
        return this.panelItemFull;
    }

    public void setPanelItemFull(HtmlPanelGroup panelItemFull)
    {
        this.panelItemFull = panelItemFull;
    }

    public HtmlMessages getValMessage()
    {
        return this.valMessage;
    }

    public void setValMessage(HtmlMessages valMessage)
    {
        this.valMessage = valMessage;
    }

    public PubItemVO getPubItem()
    {
        return this.pubItem;
    }

    public void setPubItem(PubItemVOPresentation pubItem)
    {
        this.pubItem = pubItem;
    }

    public ArrayList<TextVO> getAbstracts()
    {
        ArrayList<TextVO> abstracts = new ArrayList<TextVO>();
        if (this.pubItem.getMetadata().getAbstracts() != null)
        {
            for (int i = 0; i < this.pubItem.getMetadata().getAbstracts().size(); i++)
            {
                // abstracts.add(new
                // TextVO(CommonUtils.htmlEscape(this.pubItem.getMetadata().getAbstracts().get(i).getValue())));
                abstracts.add(new TextVO(this.pubItem.getMetadata().getAbstracts().get(i).getValue()));
            }
        }
        return abstracts;
    }

    public boolean getHasAbstracts()
    {
        return !this.pubItem.getMetadata().getAbstracts().isEmpty()
        && this.pubItem.getMetadata().getAbstracts().size() > 0;
    }

    public boolean getHasSubjects()
    {
        boolean hasNotEmptySubjects = false;
        for (TextVO subject:this.pubItem.getMetadata().getSubjects())
        {
            if (subject.getValue()!= null && subject.getValue().length()>0)
            {
                hasNotEmptySubjects=true;
                return hasNotEmptySubjects;
            }
        }
        return hasNotEmptySubjects;
    }

    public boolean getHasFreeKeywords()
    {
        return this.pubItem.getMetadata().getFreeKeywords() != null
        && this.pubItem.getMetadata().getFreeKeywords().getValue().length()>0;
    }

    public boolean getHasContentGroup()
    {
        return getHasAbstracts() || getHasFreeKeywords() || getHasSubjects();
    }

    public String getGenre()
    {
        String genre = "";
        if (this.pubItem.getMetadata().getGenre() != null)
        {
            genre = getLabel(this.getI18nHelper().convertEnumToString(this.pubItem.getMetadata().getGenre()));
        }
        return genre;
    }

    public String getReviewMethod()
    {
        String reviewMethod = "";
        if (this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getReviewMethod() != null)
        {
            reviewMethod = getLabel(this.getI18nHelper().convertEnumToString(this.pubItem.getMetadata().getReviewMethod()));
        }
        return reviewMethod;
    }

    public String getDegreeType()
    {
        String degreeType = "";
        if (this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getDegree() != null)
        {
            degreeType = getLabel(this.getI18nHelper().convertEnumToString(this.pubItem.getMetadata().getDegree()));
        }
        return degreeType;
    }

    public String getItemState()
    {
        String itemState = "";
        if (this.pubItem.getVersion().getState() != null)
        {
            itemState = getLabel(this.getI18nHelper().convertEnumToString(this.pubItem.getVersion().getState()));
        }
        return itemState;
    }

    public String getCitationURL()
    {
        return this.citationURL;
    }

    public ArrayList<String> getOrganizationArray()
    {
        return this.organizationArray;
    }

    public void setOrganizationArray(ArrayList<String> organizationArray)
    {
        this.organizationArray = organizationArray;
    }

    public ArrayList<ViewItemOrganization> getOrganizationList()
    {
        return this.organizationList;
    }

    public void setOrganizationList(ArrayList<ViewItemOrganization> organizationList)
    {
        this.organizationList = organizationList;
    }

    public List<OrganizationVO> getAffiliatedOrganizationsList()
    {
        return this.affiliatedOrganizationsList;
    }

    public void setAffiliatedOrganizationsList(List<OrganizationVO> affiliatedOrganizationsList)
    {
        this.affiliatedOrganizationsList = affiliatedOrganizationsList;
    }

    public int getCreatorArraySize()
    {
        //  return this.getCreatorArray().size();
        return this.getCreators().size();
    }

    public HtmlAjaxRepeat getTitleIterator()
    {
        return this.titleIterator;
    }

    public void setTitleIterator(HtmlAjaxRepeat titleIterator)
    {
        this.titleIterator = titleIterator;
    }

    public HtmlAjaxRepeat getCreatorPersonsIterator()
    {
        return this.creatorPersonsIterator;
    }

    public void setCreatorPersonsIterator(HtmlAjaxRepeat creatorPersonsIterator)
    {
        this.creatorPersonsIterator = creatorPersonsIterator;
    }

    public HtmlAjaxRepeat getCreatorAffiliationsIterator()
    {
        return this.creatorAffiliationsIterator;
    }

    public void setCreatorAffiliationsIterator(HtmlAjaxRepeat creatorAffiliationsIterator)
    {
        this.creatorAffiliationsIterator = creatorAffiliationsIterator;
    }

    public HtmlAjaxRepeat getAbstractIterator()
    {
        return this.abstractIterator;
    }

    public void setAbstractIterator(HtmlAjaxRepeat abstractIterator)
    {
        this.abstractIterator = abstractIterator;
    }

    public HtmlAjaxRepeat getEventAltTitleIterator()
    {
        return this.eventAltTitleIterator;
    }

    public void setEventAltTitleIterator(HtmlAjaxRepeat eventAltTitleIterator)
    {
        this.eventAltTitleIterator = eventAltTitleIterator;
    }

    public HtmlAjaxRepeat getSourceIterator()
    {
        return this.sourceIterator;
    }

    public void setSourceIterator(HtmlAjaxRepeat sourceIterator)
    {
        this.sourceIterator = sourceIterator;
    }

    public HtmlAjaxRepeat getSourceTitleIterator()
    {
        return this.sourceTitleIterator;
    }

    public void setSourceTitleIterator(HtmlAjaxRepeat sourceTitleIterator)
    {
        this.sourceTitleIterator = sourceTitleIterator;
    }

    public HtmlAjaxRepeat getSourceCreatorPersonsIterator()
    {
        return this.sourceCreatorPersonsIterator;
    }

    public void setSourceCreatorPersonsIterator(HtmlAjaxRepeat sourceCreatorPersonsIterator)
    {
        this.sourceCreatorPersonsIterator = sourceCreatorPersonsIterator;
    }

    public HtmlAjaxRepeat getSourceCreatorAffiliationsIterator()
    {
        return this.sourceCreatorAffiliationsIterator;
    }

    public void setSourceCreatorAffiliationsIterator(HtmlAjaxRepeat sourceCreatorAffiliationsIterator)
    {
        this.sourceCreatorAffiliationsIterator = sourceCreatorAffiliationsIterator;
    }

    public List<SourceBean> getSourceList()
    {
        return this.sourceList;
    }

    public void setSourceList(List<SourceBean> sourceList)
    {
        this.sourceList = sourceList;
    }

    public HtmlAjaxRepeat getFileIterator()
    {
        return this.fileIterator;
    }

    public void setFileIterator(HtmlAjaxRepeat fileIterator)
    {
        this.fileIterator = fileIterator;
    }

    public HtmlAjaxRepeat getLocatorIterator()
    {
        return this.locatorIterator;
    }

    public void setLocatorIterator(HtmlAjaxRepeat locatorIterator)
    {
        this.locatorIterator = locatorIterator;
    }

    public HtmlAjaxRepeat getCreatorOrganizationsIterator()
    {
        return this.creatorOrganizationsIterator;
    }

    public void setCreatorOrganizationsIterator(HtmlAjaxRepeat creatorOrganizationsIterator)
    {
        this.creatorOrganizationsIterator = creatorOrganizationsIterator;
    }

    public void setCitationURL(String citationURL)
    {
        this.citationURL = citationURL;
    }

    public boolean getIsStateWithdrawn()
    {
        return this.getPubItem().getPublicStatus().equals(State.WITHDRAWN);
    }

    public void setStateWithdrawn(boolean isStateWithdrawn)
    {
        this.isStateWithdrawn = isStateWithdrawn;
    }

    public boolean getIsDepositor()
    {
        return this.isDepositor;
    }

    public void setDepositor(boolean isDepositor)
    {
        this.isDepositor = isDepositor;
    }

    public boolean getIsModerator()
    {
        return this.isModerator;
    }

    public void setModerator(boolean isModerator)
    {
        this.isModerator = isModerator;
    }

    public boolean getisPrivilegedViewer()
    {
        return this.isPrivilegedViewer;
    }

    public void setPrivilegedViewer(boolean isPrivilegedViewer)
    {
        this.isPrivilegedViewer = isPrivilegedViewer;
    }

    public boolean getIsLoggedIn()
    {
        return this.isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn)
    {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean getIsLatestVersion()
    {
        return this.isLatestVersion;
    }

    public void setLatestVersion(boolean isLatestVersion)
    {
        this.isLatestVersion = isLatestVersion;
    }

    public boolean getIsLatestRelease()
    {
        return this.isLatestRelease;
    }

    public void setLatestRelease(boolean isLatestRelease)
    {
        this.isLatestRelease = isLatestRelease;
    }

    public boolean getIsStateSubmitted()
    {
        return this.isStateSubmitted;
    }

    public void setStateSubmitted(boolean isStateSubmitted)
    {
        this.isStateSubmitted = isStateSubmitted;
    }

    public boolean getIsStateReleased()
    {
        return this.isStateReleased;
    }

    public void setStateReleased(boolean isStateReleased)
    {
        this.isStateReleased = isStateReleased;
    }

    public boolean getIsStatePending()
    {
        return this.isStatePending;
    }

    public void setStatePending(boolean isStatePending)
    {
        this.isStatePending = isStatePending;
    }

    public boolean getIsOwner()
    {
        return this.isOwner;
    }

    public void setOwner(boolean isOwner)
    {
        this.isOwner = isOwner;
    }

    public boolean getIsModifyDisabled()
    {
        return this.isModifyDisabled;
    }

    public void setModifyDisabled(boolean isModifyDisabled)
    {
        this.isModifyDisabled = isModifyDisabled;
    }

    public boolean getIsCreateNewRevisionDisabled()
    {
        return this.isCreateNewRevisionDisabled;
    }

    public void setCreateNewRevisionDisabled(boolean isCreateNewRevisionDisabled)
    {
        this.isCreateNewRevisionDisabled = isCreateNewRevisionDisabled;
    }

    public boolean getIsFromEasySubmission()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
        if (request.getParameter("fromEasySub") != null)
        {
            String fromEasySubmission = request.getParameter("fromEasySub");
            return fromEasySubmission.equals("true");
        }
        return false;
    }

    public boolean getHasAudience()
    {
        if (this.pubItem == null)
        {
            return false;
        }
        else if ((this.pubItem.getVersion().getState() == State.RELEASED || this.pubItem.getVersion().getState() == State.SUBMITTED)
                && (getIsModerator() || getIsDepositor()))
        {
            for (FileVO file : this.pubItem.getFiles())
            {
                if (file.getVisibility() == Visibility.AUDIENCE)
                {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public HtmlAjaxRepeat getSourceCreatorOrganizationsIterator()
    {
        return this.sourceCreatorOrganizationsIterator;
    }

    public void setSourceCreatorOrganizationsIterator(HtmlAjaxRepeat sourceCreatorOrganizationsIterator)
    {
        this.sourceCreatorOrganizationsIterator = sourceCreatorOrganizationsIterator;
    }

    public boolean getIsWorkflowStandard()
    {
        return this.isWorkflowStandard;
    }

    public void setWorkflowStandard(boolean isWorkflowStandard)
    {
        this.isWorkflowStandard = isWorkflowStandard;
    }

    public boolean getIsWorkflowSimple()
    {
        return this.isWorkflowSimple;
    }

    public void setWorkflowSimple(boolean isWorkflowSimple)
    {
        this.isWorkflowSimple = isWorkflowSimple;
    }

    public String reviseItem()
    {
        return ReviseItem.LOAD_REVISEITEM;
    }

    public boolean getIsStateInRevision()
    {
        return this.isStateInRevision;
    }

    public void setStateInRevision(boolean isStateInRevision)
    {
        this.isStateInRevision = isStateInRevision;
    }

    public String getItemPublicState()
    {
        String itemState = "";
        if (this.pubItem.getPublicStatus() != null)
        {
            itemState = getLabel(this.getI18nHelper().convertEnumToString(this.pubItem.getPublicStatus()));
        }
        return itemState;
    }

    public String getUnapiURLdownload()
    {
        return this.unapiURLdownload;
    }

    public void setUnapiURLdownload(String unapiURLdownload)
    {
        this.unapiURLdownload = unapiURLdownload;
    }

    public String getUnapiEscidoc()
    {
        return this.unapiEscidoc;
    }

    public void setUnapiEscidoc(String unapiEscidoc)
    {
        this.unapiEscidoc = unapiEscidoc;
    }

    public String getUnapiEndnote()
    {
        return this.unapiEndnote;
    }

    public void setUnapiEndnote(String unapiEndnote)
    {
        this.unapiEndnote = unapiEndnote;
    }

    public String getUnapiBibtex()
    {
        return this.unapiBibtex;
    }

    public void setUnapiBibtex(String unapiBibtex)
    {
        this.unapiBibtex = unapiBibtex;
    }

    public String getUnapiApa()
    {
        return this.unapiApa;
    }

    public void setUnapiApa(String unapiApa)
    {
        this.unapiApa = unapiApa;
    }

    public String getUnapiURLview()
    {
        return this.unapiURLview;
    }

    public void setUnapiURLview(String unapiURLview)
    {
        this.unapiURLview = unapiURLview;
    }

    /*
     * public boolean getHasRevision() { return this.hasRevision; } public void setHasRevision(boolean hasRevision) {
     * this.hasRevision = hasRevision; }
     */
    public String addToBasket()
    {
        PubItemStorageSessionBean pubItemStorage = (PubItemStorageSessionBean)getSessionBean(PubItemStorageSessionBean.class);
        if (!pubItemStorage.getStoredPubItems().containsKey(this.pubItem.getVersion().getObjectIdAndVersion()))
        {
            pubItemStorage.getStoredPubItems().put(this.pubItem.getVersion().getObjectIdAndVersion(),
                    this.pubItem.getVersion());
            info(getMessage("basket_SingleAddedSuccessfully"));
        }
        else
        {
            error(getMessage("basket_SingleAlreadyInBasket"));
        }
        this.canAddToBasket = false;
        this.canDeleteFromBasket = true;
        return "";
    }

    public String removeFromBasket()
    {
        PubItemStorageSessionBean pssb = (PubItemStorageSessionBean)getSessionBean(PubItemStorageSessionBean.class);
        pssb.getStoredPubItems().remove(this.pubItem.getVersion().getObjectIdAndVersion());
        info(getMessage("basket_SingleRemovedSuccessfully"));
        this.canAddToBasket = true;
        this.canDeleteFromBasket = false;
        return "";
    }

    public boolean getIsInBasket()
    {
        if (this.pubItem == null) return false;
        PubItemStorageSessionBean pubItemStorage = (PubItemStorageSessionBean)getSessionBean(PubItemStorageSessionBean.class);
        return pubItemStorage.getStoredPubItems().containsKey(this.pubItem.getVersion().getObjectIdAndVersion());
    }

    public String getLinkForActionsView()
    {
        String url = "viewItemFullPage.jsp?" + PARAMETERNAME_ITEM_ID + "="
        + getPubItem().getVersion().getObjectIdAndVersion() + "&" + PARAMETERNAME_MENU_VIEW + "=ACTIONS";
        return url;
    }

    public String getLinkForExportView()
    {
        return "viewItemFullPage.jsp?" + PARAMETERNAME_ITEM_ID + "="
        + getPubItem().getVersion().getObjectIdAndVersion() + "&" + PARAMETERNAME_MENU_VIEW + "=EXPORT";
    }

    /**
     * Invokes the email service to send per email the the page with the selected items as attachment. This method is
     * called when the user selects one or more items and then clicks on the EMail-Button in the Export-Items Panel.
     * 
     * @author: StG
     */
    public String exportEmail()
    {
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        // this.setSelectedItemsAndCurrentItem();
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
        List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
        pubItemList.add(getPubItem());
        // gets the export format VO that holds the data.
        ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
        byte[] exportFileData;
        try
        {
            exportFileData = icsb.retrieveExportData(curExportFormat, pubItemList);
        }
        catch (TechnicalException e)
        {
            ((ErrorPage)getSessionBean(ErrorPage.class)).setException(e);
            return ErrorPage.LOAD_ERRORPAGE;
        }
        if ((exportFileData == null) || (new String(exportFileData)).trim().equals(""))
        {
            error(getMessage(ExportItems.MESSAGE_NO_EXPORTDATA_DELIVERED));
            return "";
        }
        // YEAR + MONTH + DAY_OF_MONTH
        Calendar rightNow = Calendar.getInstance();
        String date = rightNow.get(Calendar.YEAR) + "-" + rightNow.get(Calendar.DAY_OF_MONTH) + "-"
        + rightNow.get(Calendar.MONTH) + "_";
        // create an attachment temp file from the byte[] stream
        File exportAttFile;
        try
        {
            exportAttFile = File.createTempFile("eSciDoc_Export_" + curExportFormat.getName() + "_" + date, "."
                    + FileFormatVO.getExtensionByName(curExportFormat.getSelectedFileFormat().getName()));
            FileOutputStream fos = new FileOutputStream(exportAttFile);
            fos.write(exportFileData);
            fos.close();
        }
        catch (IOException e1)
        {
            ((ErrorPage)getSessionBean(ErrorPage.class)).setException(e1);
            return ErrorPage.LOAD_ERRORPAGE;
        }
        sb.setExportEmailTxt(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_TEXT));
        sb.setAttExportFileName(exportAttFile.getName());
        sb.setAttExportFile(exportAttFile);
        sb.setExportEmailSubject(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT) + ": "
                + exportAttFile.getName());
        // hier call set the values on the exportEmailView - attachment file, subject, ....
        return "displayExportEmailPage";
    }

    /**
     * Downloads the page with the selected items as export. This method is called when the user selects one or more
     * items and then clicks on the Download-Button in the Export-Items Panel.
     * 
     * @author: StG
     */
    public String exportDownload()
    {
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        // set the currently selected items in the FacesBean
        // this.setSelectedItemsAndCurrentItem();
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
        List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
        pubItemList.add(getPubItem());
        // export format and file format.
        ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
        byte[] exportFileData = null;
        try
        {
            exportFileData = icsb.retrieveExportData(curExportFormat, pubItemList);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot export item:", e);
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
        String contentType = curExportFormat.getSelectedFileFormat().getMimeType();
        response.setContentType(contentType);
        String fileName = "export_" + curExportFormat.getName().toLowerCase() + "."
        + FileFormatVO.getExtensionByName(sb.getFileFormat());
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        try
        {
            OutputStream out = response.getOutputStream();
            out.write(exportFileData);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot put export result in HttpResponse body:", e);
        }
        facesContext.responseComplete();
        return "";
    }

    /**
     * This method returns the contact email address of the moderator strored in the item's context. If it is empty the
     * pubman support address will be returned.
     * 
     * @return the moderator's email address (if available, otherwise pubman support address)
     */
    public String getModeratorContactEmail()
    {
        String contactEmail = "";
        contactEmail = this.getContext().getAdminDescriptor().getContactEmail();
        if (contactEmail == null || contactEmail.trim().equals(""))
        {
            contactEmail = ALTERNATIVE_MODERATOR_EMAIL;
        }
        return contactEmail;
    }

    public void setPublicStateReleased(boolean isPublicStateReleased)
    {
        this.isPublicStateReleased = isPublicStateReleased;
    }

    public boolean getIsPublicStateReleased()
    {
        return isPublicStateReleased;
    }

    public String getFwUrl()
    {
        return fwUrl;
    }

    public void setFwUrl(String fwUrl)
    {
        this.fwUrl = fwUrl;
    }

    public String getItemPattern()
    {
        return itemPattern;
    }

    public void setItemPattern(String itemPattern)
    {
        this.itemPattern = itemPattern;
    }

    /*
    private void logViewAction()
    {
        final String ip = getIP();
        final String sessId = getSessionId();
        final String referer = getReferer();
        final String userAgent = getUserAgent();
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    PubItemSimpleStatistics statistics = new SimpleStatistics();
                    statistics.logPubItemAction(getPubItem(), ip, userAgent, ItemAction.RETRIEVE, sessId,
                            loginHelper.getLoggedIn(), referer);
                }
                catch (Exception e)
                {
                    logger.error("Could not log statistical data", e);
                }
            }
        }.start();
    }
    */

    public String getStatisticString()
    {
        LoginHelper loginHelper = (LoginHelper)getSessionBean(LoginHelper.class);
        String url = "ViewItemPage";
        url += "?itemId=" + getPubItem().getVersion().getObjectId();
        url += "&loggedIn=" + loginHelper.getLoggedIn();
        List<CreatorVO> creatorList = getPubItem().getMetadata().getCreators();
        int i = 0;
        for (CreatorVO creator : creatorList)
        {
            if (creator.getPerson() != null && creator.getPerson().getIdentifier() != null
                    && creator.getPerson().getIdentifier().getId() != null
                    && !creator.getPerson().getIdentifier().getId().equals(""))
            {
                url += "auth" + i++ + "=" + creator.getPerson().getIdentifier().getId();
            }
        }
        return url;
    }

    /**
     * Returns a true or a false according to the existance of an legal case in the item
     * 
     * @return boolean
     */
    public boolean getShowLegalCase()
    {
        if (this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getLegalCase() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns a String with the legal case data according to the existance of an legal case in the item
     * 
     * @return boolean
     */
    public String  getLegalCaseCourtDateId()
    {
        StringBuffer legalCaseString = new StringBuffer();
        if (this.pubItem.getMetadata().getLegalCase().getCourtName() != "")
        {
            legalCaseString.append(this.pubItem.getMetadata().getLegalCase().getCourtName());
        }
        if (this.pubItem.getMetadata().getLegalCase().getDatePublished() != "")
        {
            if (legalCaseString.length() != 0){
                legalCaseString.append(", ");
            }
            legalCaseString.append(this.pubItem.getMetadata().getLegalCase().getDatePublished());
        }
        if (this.pubItem.getMetadata().getLegalCase().getIdentifier() != "")
        {
            if (legalCaseString.length() != 0){
                legalCaseString.append(" - ");
            }
            legalCaseString.append(this.pubItem.getMetadata().getLegalCase().getIdentifier());
        }
        return legalCaseString.toString();
    }

    public void setDefaultSize(int defaultSize)
    {
        this.defaultSize = defaultSize;
    }

    public int getDefaultSize()
    {
        return defaultSize;
    }

    public ArrayList<ViewItemCreators> getCreators() {
        return creators;
    }

    public void setCreators(ArrayList<ViewItemCreators> creators) {
        this.creators = creators;
    }


    public void setLatestVersionURL(String latestVersionURL)
    {
        this.latestVersionURL = latestVersionURL;
    }

    public String getLatestVersionURL()
    {
        return latestVersionURL;
    }

    public String getCitationHtml()
    {
        try
        {
            InitialContext initialContext = new InitialContext();
            ItemExporting itemExporting = (ItemExporting) initialContext.lookup(ItemExporting.SERVICE_NAME);

            List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
            pubItemList.add(new PubItemVO(getPubItem()));

            ExportFormatVO expFormat = new ExportFormatVO();
            expFormat.setFormatType(ExportFormatVO.FormatType.LAYOUT);

            InternationalizationHelper ih = (InternationalizationHelper) getSessionBean(InternationalizationHelper.class);

            //Use special apa style if language is set to japanese
            boolean isJapanese = false;

            if(getPubItem().getMetadata().getLanguages()!=null)
            {
                for (String lang : getPubItem().getMetadata().getLanguages())
                {
                    if ("jpn".equals(lang))
                    {
                        isJapanese = true;
                        break;
                    }
                }
            }


            if(isJapanese || "ja".equalsIgnoreCase(ih.getLocale()))
            {
                expFormat.setName("APA(CJK)");
            }
            else
            {
                expFormat.setName("APA6");
            }


            FileFormatVO fileFormat = new FileFormatVO();
            fileFormat.setMimeType(FileFormatVO.HTML_PLAIN_MIMETYPE);
            fileFormat.setName(FileFormatVO.HTML_PLAIN_NAME);

            expFormat.setSelectedFileFormat(fileFormat);

            byte[] exportFileData = null;

            exportFileData = itemExporting.getOutput(expFormat, pubItemList);

            String exportHtml = new String(exportFileData, "UTF-8");

            try {
                Pattern p = Pattern.compile("(?<=\\<body\\>).*(?=\\<\\/body\\>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                Matcher m = p.matcher(exportHtml);
                m.find();
                String match = m.group();
                if(match!=null)
                {
                    return match;
                }
            } catch (Exception e) {
                logger.debug("Match in citation html not found", e);
            }

            return "";
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot export item:", e);
        }
        // return "";

    }

    public String getResolveHandleService() {
        return RESOLVE_HANDLE_SERVICE;
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

    private void setLinks()
    {
        this.isModifyDisabled = this.getRightsManagementSessionBean().isDisabled(
                RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "."
                + ViewItemFull.FUNCTION_MODIFY);
        this.isCreateNewRevisionDisabled = this.getRightsManagementSessionBean().isDisabled(
                RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "."
                + ViewItemFull.FUNCTION_NEW_REVISION);

        if (!this.isStateWithdrawn &&
                ((this.isStatePending || this.isStateInRevision) && this.isLatestVersion && this.isOwner)
                || (this.isStateSubmitted && this.isLatestVersion && this.isModerator))
        {
            this.canEdit = true;
        }

        if (!this.isStateWithdrawn &&
                (this.isStatePending || this.isStateInRevision) &&
                this.isLatestVersion && this.isOwner && this.isWorkflowStandard)
        {
            this.canSubmit = true;
        }

        if (!this.isStateWithdrawn && this.isOwner && this.isLatestVersion &&
                (((this.isStatePending || this.isStateSubmitted) && this.isWorkflowSimple) ||
                        (this.isWorkflowStandard && this.isModerator && this.isStateSubmitted)))
        {
            this.canRelease = true;

        }

        if (!this.isStateWithdrawn && this.isStateSubmitted && this.isLatestVersion && this.isModerator && !this.isOwner && !this.isModifyDisabled)
        {
            this.canAccept = true;
        }

        if (!this.isStateWithdrawn && (this.isStateSubmitted && this.isLatestVersion && this.isModerator && !this.isModifyDisabled && this.isWorkflowStandard && !this.isPublicStateReleased)){
            this.canRevise = true;
        }

        if (!this.isStateWithdrawn && !this.isPublicStateReleased && (this.isStatePending || this.isStateInRevision) && this.isLatestVersion && this.isOwner)
        {
            this.canDelete = true;
        }
        
        if (!this.isStateWithdrawn && ((this.isStateReleased || this.isStateWasReleased) && this.isLatestVersion)  && (this.isOwner || this.isModerator))
        {
            this.canWithdraw = true;
        }

        if (!this.isStateWithdrawn && this.isStateReleased && this.isLatestVersion && !this.isModifyDisabled && (this.isModerator || this.isOwner))
        {
            this.canModify = true;
        }

        if (!this.isStateWithdrawn && this.isStateReleased && this.isLatestRelease && !this.isCreateNewRevisionDisabled && this.isDepositor)
        {
            this.canCreateNewRevision = true;
        }

        if (!this.isStateWithdrawn && this.isLatestVersion && !this.isCreateNewRevisionDisabled && this.isDepositor)
        {
            this.canCreateFromTemplate = true;
        }

        if (!this.isStateWithdrawn && !this.getIsInBasket() )
        {
            this.canAddToBasket = true;
        }

        if (!this.isStateWithdrawn && this.getIsInBasket())
        {
            this.canDeleteFromBasket = true;
        }

        if (this.isLatestVersion && !this.isStateWithdrawn)
        {
            this.canViewLocalTags = true;
        }

        if (this.getHasAudience() && !this.isStateWithdrawn)
        {
            this.canManageAudience  = true;
        }

        if (this.isLatestVersion && !this.isStateWithdrawn && this.isLoggedIn && (this.isOwner || this.isModerator))
        {
            this.canShowItemLog = true;
        }

        if (this.isLatestRelease && !this.isStateWithdrawn)
        {
            this.canShowStatistics = true;
            this.canShowRevisions = true;
        }

        if (this.pubItem != null && (!this.isStateWithdrawn && this.isLatestRelease) || (this.isStateWithdrawn && this.pubItem.getVersion().getVersionNumber() > 1))
        {
            this.canShowReleaseHistory = true;
        }

        if (this.pubItem != null && this.pubItem.getVersion().getLastMessage() != null && !this.pubItem.getVersion().getLastMessage().contentEquals(""))
        {
            this.canShowLastMessage = true;
        }

    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public boolean isCanSubmit() {
        return canSubmit;
    }

    public boolean isCanRelease() {
        return canRelease;
    }


    public boolean isCanAccept() {
        return canAccept;
    }


    public boolean isCanRevise() {
        return canRevise;
    }


    public boolean isCanDelete() {
        return canDelete;
    }

    public boolean isCanWithdraw() {
        return canWithdraw;
    }


    public boolean isCanModify() {
        return canModify;
    }


    public boolean isCanCreateNewRevision() {
        return canCreateNewRevision;
    }


    public boolean isCanCreateFromTemplate() {
        return canCreateFromTemplate;
    }


    public boolean isCanAddToBasket() {
        return canAddToBasket;
    }


    public boolean isCanDeleteFromBasket() {
        return canDeleteFromBasket;
    }


    public boolean isCanViewLocalTags() {
        return canViewLocalTags;
    }


    public boolean isCanManageAudience() {
        return canManageAudience;
    }


    public boolean isCanShowItemLog() {
        return canShowItemLog;
    }


    public boolean isCanShowStatistics() {
        return canShowStatistics;
    }


    public boolean isCanShowRevisions() {
        return canShowRevisions;
    }


    public boolean isCanShowReleaseHistory() {
        return canShowReleaseHistory;
    }


    public boolean isCanShowLastMessage() {
        return canShowLastMessage;
    }

    /*{
	ABBREVIATION("http://purl.org/escidoc/metadata/terms/0.1/ABBREVIATION"),
	HTML("http://purl.org/escidoc/metadata/terms/0.1/HTML"),
	LATEX("http://purl.org/escidoc/metadata/terms/0.1/LATEX"),
	MATHML("http://purl.org/escidoc/metadata/terms/0.1/MATHML"),
	OTHER("http://purl.org/escidoc/metadata/terms/0.1/OTHER");

	private String uri;

	private AlternativeTitleType(String uri)
	{
		this.uri=uri;
	}

	public String getUri()
	{
		return uri;
	}
}*/

}
