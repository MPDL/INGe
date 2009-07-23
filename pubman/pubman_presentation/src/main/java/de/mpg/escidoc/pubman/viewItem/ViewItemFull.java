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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.viewItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.UIXIterator;

import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.CommonSessionBean;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
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
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
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
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.ObjectFormatter;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.pubman.viewItem.bean.FileBean;
import de.mpg.escidoc.pubman.viewItem.bean.SourceBean;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItem;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItemSessionBean;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.ItemAction;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.escidoc.services.framework.PropertyReader;
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
    final public static String BEAN_NAME = "ViewItemFull";
    public static final String PARAMETERNAME_ITEM_ID = "itemId";
    public static final String PARAMETERNAME_MENU_VIEW = "view";
    // Faces navigation string
    public final static String LOAD_VIEWITEM = "loadViewItem";
    public final static String ALTERNATIVE_MODERATOR_EMAIL = "pubman-support@gwdg.de";
    
    public final static String ISI_KNOWLEDGE_BASE_LINK = "http://gateway.isiknowledge.com/gateway/Gateway.cgi?GWVersion=2&SrcAuth=SFX&SrcApp=SFX&DestLinkType=FullRecord&KeyUT=";
    public final static String ISI_KNOWLEDGE_DEST_APP = "&DestApp=WOS";
    
    public boolean isDepositor = false;
    public boolean isModerator = false;
    public boolean isPrivilegedViewer=false;
    
    // Validation Service
    private ItemValidating itemValidating = null; 
    private PubItemVOPresentation pubItem = null;

    private HtmlMessages valMessage = new HtmlMessages();

    // Added by DiT: constant for the function modify and new revision to check the rights and/or if the function has to be disabled (DiT)
    private static final String FUNCTION_MODIFY = "modify";
    private static final String FUNCTION_NEW_REVISION = "new_revision";
    
    private static final String VALIDATION_ERROR_MESSAGE = "depositorWS_NotSuccessfullySubmitted";
    
    private UIXIterator titleIterator = new UIXIterator();
    
    private UIXIterator creatorPersonsIterator = new UIXIterator();
    
    private UIXIterator creatorOrganizationsIterator = new UIXIterator();
    
    private UIXIterator creatorAffiliationsIterator = new UIXIterator();
    
    private UIXIterator languagesIterator = new UIXIterator();
    
    private UIXIterator abstractIterator = new UIXIterator();
    
    private UIXIterator subjectIterator = new UIXIterator();
    
    private UIXIterator eventAltTitleIterator = new UIXIterator();
    
    private UIXIterator sourceIterator = new UIXIterator();
    
    private UIXIterator sourceTitleIterator = new UIXIterator();
    
    private UIXIterator sourceCreatorPersonsIterator = new UIXIterator();
    
    private UIXIterator sourceCreatorOrganizationsIterator = new UIXIterator(); 
    
    
    private UIXIterator sourceCreatorAffiliationsIterator = new UIXIterator();
    
    private UIXIterator fileIterator = new UIXIterator();
    
    private UIXIterator locatorIterator = new UIXIterator();
    
    private UIXIterator fileSearchHitIterator = new UIXIterator();
    
    private ContextVO context = null;
    
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
     * The list of formatted creators in an ArrayList.
     */
    private ArrayList<CreatorDisplay> creatorArray;
    
    /**
     * The list of formatted creators which are organizations in an ArrayList.
     */
    private ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray;
    
    private List<SourceBean> sourceList = new ArrayList<SourceBean>();
    
    private List<FileBean> fileList = new ArrayList<FileBean>();
    
    private List<FileBean> locatorList = new ArrayList<FileBean>();
    private LoginHelper loginHelper;
    
    /**The url used for the citation*/
    private String citationURL;
    
    /**The url of the Coreservice for file downloads*/
    private String fwUrl;
    
    /**Version and ObjectId of the item*/
    private String itemPattern;

    /**unapi*/
	private String unapiURLdownload;
	private String unapiURLview;
	private String unapiEscidoc;
	private String unapiEndnote;
	private String unapiBibtex;
	private String unapiApa;
    
	/** Properties for action links rendering conditions*/
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
    private PubItemDepositing pubItemDepositing;
    private boolean isWorkflowStandard;
    private boolean isWorkflowSimple;
    private boolean isStateInRevision;
    //private boolean hasRevision;
    private PubItemSimpleStatistics pubManStatistics;
    private boolean isPublicStateReleased;

    /**
     * Public constructor.
     */
    public ViewItemFull()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     * Changed by DiT, 15.10.2007: added link for modify 
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
        String itemID = "";
        this.loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        
        // populate the core service Url
        try {
			this.fwUrl = PropertyReader.getProperty("escidoc.framework_access.framework.url");
		} catch (IOException ioE) {
			throw new RuntimeException("Could  not read the Property file for property 'escidoc.framework_access.framework.url'", ioE);
		} catch (URISyntaxException uE) {
			throw new RuntimeException("Syntax of property 'escidoc.framework_access.framework.url' not correct", uE);
		}
        
        // Try to get the validation service
        try
        {
            InitialContext initialContext = new InitialContext();
            this.pubItemDepositing = (PubItemDepositing) initialContext.lookup(PubItemDepositing.SERVICE_NAME);
            this.itemValidating = (ItemValidating)initialContext.lookup(ItemValidating.SERVICE_NAME);
            this.pubManStatistics = (PubItemSimpleStatistics) initialContext.lookup(PubItemSimpleStatistics.SERVICE_NAME);
        }
        
        catch (NamingException ne)
        {
            throw new RuntimeException("Validation service not initialized", ne);
        }
       
        
        boolean logViewAction = false;
        // Try to get a pubitem either via the controller session bean or an URL Parameter
        
        itemID = request.getParameter(ViewItemFull.PARAMETERNAME_ITEM_ID);
        if(itemID != null)
        {
            try
            {
                this.pubItem = this.getItemControllerSessionBean().retrieveItem(itemID);
                //if it is a new item reset ViewItemSessionBean
                if(getItemControllerSessionBean().getCurrentPubItem()==null || !pubItem.getVersion().getObjectIdAndVersion().equals(getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectIdAndVersion()))
                {
                    HttpSession session = (HttpSession) getFacesContext().getExternalContext().getSession(false);
                    getViewItemSessionBean().itemChanged();
                    //pubManStatistics.logStatisticPubItemEvent(new PubItemVO(pubItem), null, PubItemSimpleStatistics.StatisticItemEventType.retrieval, session.getId(), request.getHeader("referer"), request.getRemoteHost());
                }
                this.getItemControllerSessionBean().setCurrentPubItem(this.pubItem);
                logViewAction = true;
                
            }
            catch (AuthorizationException e)
            {
                Login login = (Login)getSessionBean(Login.class);
                login.forceLogout(itemID);
            }
            catch (AuthenticationException e)
            {
                Login login = (Login)getSessionBean(Login.class);
                login.forceLogout(itemID);
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
        if (subMenu!=null){
            getViewItemSessionBean().setSubMenu(subMenu);
        }
        
        //check if arriving from easy submission
        //EasySubmission easySubmissionRequestBean = (EasySubmission)getRequestBean(EasySubmission.class);
        //this.isFromEasySubmission = easySubmissionRequestBean.getFromEasySubmission();
        
        if(this.pubItem != null)
        {
            
            
            //DiT: multiple new conditions for link-activation added
            this.isModerator = this.loginHelper.getAccountUser().isModerator(this.pubItem.getContext());
            this.isPrivilegedViewer= this.loginHelper.getAccountUser().isPrivilegedViewer(this.pubItem.getContext());
            ContextListSessionBean contextListSessionBean = (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
            this.isDepositor = this.loginHelper.getAccountUser().isDepositor() && contextListSessionBean.getDepositorContextList()!= null && contextListSessionBean.getDepositorContextList().size() > 0;
            //isDepositor = loginHelper.getAccountUser().isDepositor();
            
            /*
            //Check if item has revisions
            try
            {
                List revisions = this.getItemControllerSessionBean().retrieveRevisions(this.pubItem); 
                if (revisions != null && revisions.size() > 0)
                {
                    this.setHasRevision(true);
                }
                else
                {
                    this.setHasRevision(false);
                }
            }
            catch (Exception e1)
            {
                this.setHasRevision(false);
                logger.warn("Could not retrieve list of revisions.", e1);
            }
            */
            
            this.isOwner = true;
            if (this.pubItem.getOwner() != null)
            {
                this.isOwner = (this.loginHelper.getAccountUser().getReference() != null ? this.loginHelper.getAccountUser().getReference().getObjectId().equals(this.pubItem.getOwner().getObjectId()) : false);
            }
            this.isModifyDisabled = this.getRightsManagementSessionBean().isDisabled(RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "." + ViewItemFull.FUNCTION_MODIFY);
            this.isCreateNewRevisionDisabled = this.getRightsManagementSessionBean().isDisabled(RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "." + ViewItemFull.FUNCTION_NEW_REVISION);

            //@author Markus Haarlaender - setting properties for Action Links
            
            this.isLoggedIn = this.loginHelper.isLoggedIn();
            this.isLatestVersion = this.pubItem.getVersion().getVersionNumber() == this.pubItem.getLatestVersion().getVersionNumber();
            this.isLatestRelease = this.pubItem.getVersion().getVersionNumber() == this.pubItem.getLatestRelease().getVersionNumber();
            
            this.isStateWithdrawn = this.pubItem.getPublicStatus().toString().equals(PubItemVO.State.WITHDRAWN.toString());
            
            this.isStateSubmitted = this.pubItem.getVersion().getState().toString().equals(PubItemVO.State.SUBMITTED.toString());
            this.isStateReleased = this.pubItem.getVersion().getState().toString().equals(PubItemVO.State.RELEASED.toString());
            this.isStatePending = this.pubItem.getVersion().getState().toString().equals(PubItemVO.State.PENDING.toString());
            this.isStateInRevision = this.pubItem.getVersion().getState().toString().equals(PubItemVO.State.IN_REVISION.toString());
            this.isPublicStateReleased = this.pubItem.getPublicStatus().toString().equals(PubItemVO.State.RELEASED.toString());
            
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
            
            if(this.isStateWithdrawn)
            {
                getViewItemSessionBean().itemChanged();
            }
            
            //set citation url
            try
            {
                String pubmanUrl = PropertyReader.getProperty("escidoc.pubman.instance.url") + PropertyReader.getProperty("escidoc.pubman.instance.context.path");
                
                this.itemPattern = PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1", getPubItem().getVersion().getObjectIdAndVersion());
                
                
                if(!pubmanUrl.endsWith("/")) pubmanUrl = pubmanUrl + "/";
                if (this.itemPattern.startsWith("/")) this.itemPattern = this.itemPattern.substring(1, this.itemPattern.length());
                
                // MF: Removed exclusion of pending items here
                this.citationURL = pubmanUrl + this.itemPattern;
                
            }
            catch (Exception e)
            {
                e.printStackTrace();
                this.citationURL = "";
            }
            
            // set up some pre-requisites
            // the list of numbered affiliated organizations 
            createAffiliatedOrganizationList();
            
            // the list of creators (persons and organizations)
            createCreatorList();
            
            // the list of sources
           
            //clear source list first
            this.sourceList.clear();
            
            
            for(int i = 0; i < this.pubItem.getMetadata().getSources().size(); i++)
            {
            	this.sourceList.add(new SourceBean(this.pubItem.getMetadata().getSources().get(i)));
            }
            
            // the list of files
            // Check if the item is also in the search result list
            List<PubItemVOPresentation> currentPubItemList = this.getItemListSessionBean().getCurrentPubItemList();
            List<SearchHitVO> searchHitList = new ArrayList<SearchHitVO>();
            for(int i = 0; i < currentPubItemList.size(); i++)
            {
            	if(this.pubItem.getVersion().getObjectId().equals(currentPubItemList.get(i).getVersion().getObjectId()))
            	{
            		if(this.pubItem.getVersion().getVersionNumber() == currentPubItemList.get(i).getVersion().getVersionNumber())
            		{
            			if(currentPubItemList.get(i).getSearchHitList() != null && currentPubItemList.get(i).getSearchHitList().size() > 0)
            			{
            				for(int j = 0; j < currentPubItemList.get(i).getSearchHitList().size(); j++)
            				{
            					searchHitList.add(currentPubItemList.get(i).getSearchHitList().get(j));
            				}
            				
            			}
            		}
            	}
            }
            
          
            // Clear file and locator list first
            this.fileList.clear();
            this.locatorList.clear();
            for(int i = 0; i < this.pubItem.getFiles().size(); i++)
            {
            	if(searchHitList.size() > 0 && !this.pubItem.getVersion().getState().equals(PubItemVO.State.WITHDRAWN))
                {
            		//this.fileList.add(new FileBean(this.pubItem.getFiles().get(i), i, this.pubItem.getVersion().getState(), searchHitList));
            		
            		if(this.pubItem.getFiles().get(i).getStorage() == FileVO.Storage.EXTERNAL_URL)
                    {
                        this.locatorList.add(new FileBean(this.pubItem.getFiles().get(i), this.pubItem.getVersion().getState()));
                        
                    }
                    // add files
                    else
                    {
                        this.fileList.add(new FileBean(this.pubItem.getFiles().get(i), this.pubItem.getVersion().getState(), searchHitList));
                        
                    }
                }
            	else
            	{
            		// add locators
            		if(this.pubItem.getFiles().get(i).getStorage() == FileVO.Storage.EXTERNAL_URL)
            		{
            			this.locatorList.add(new FileBean(this.pubItem.getFiles().get(i), this.pubItem.getVersion().getState()));
            			
            		}
            		// add files
            		else
            		{
            			this.fileList.add(new FileBean(this.pubItem.getFiles().get(i), this.pubItem.getVersion().getState()));
            			
            		}
            	}
            }
            
            //Unapi Export 
            try
            {
                this.unapiURLdownload = PropertyReader.getProperty("escidoc.unapi.download.server");
                this.unapiURLview = PropertyReader.getProperty("escidoc.unapi.view.server");
                this.unapiEscidoc = this.unapiURLdownload+"?id="+itemID+"&format=escidoc";
                this.unapiEndnote = this.unapiURLdownload+"?id="+itemID+"&format=endnote";
                this.unapiBibtex = this.unapiURLdownload+"?id="+itemID+"&format=bibtex";
                this.unapiApa = this.unapiURLdownload+"?id="+itemID+"&format=apa";
            }
            catch (Exception e) {
                logger.error("Error getting unapi url property", e);
                throw new RuntimeException(e);
            }
            
            if(logViewAction)
            {
                logViewAction();
            }

            // TODO ScT: remove this and related methods when the procedure of handling release history button is fully clarified
            // set up the release history of the item
            //createReleaseHistory();
//            
//            // redirect if necessary
//            if(this.getViewItemSessionBean().isHasBeenRedirected() == false)
//            {
//                this.getViewItemSessionBean().setHasBeenRedirected(true);
//                try
//                {
//                    if(this.getSessionBean().isRunAsGUITool())
//                    {
//                    	fc.getExternalContext().redirect("GTViewItemFullPage.jsp?itemId=" + this.pubItem.getVersion().getObjectId()+":"+ this.pubItem.getVersion().getVersionNumber());
//                    }
//                    else
//                    {
//                    	fc.getExternalContext().redirect("viewItemFullPage.jsp?itemId=" + this.pubItem.getVersion().getObjectId()+":"+ this.pubItem.getVersion().getVersionNumber());
//                    }
//                }
//                catch (IOException e)
//                {
//                    logger.error(e);
//                }
//            }
        }
    }
    
    /**
     * Redirects the user to the edit item page
     * 
     * @return Sring nav rule to load the edit item page
     */
    public String editItem()
    {
        // clear the list of  locators and files when start editing an item
    	EditItemSessionBean editItemSessionBean = this.getEditItemSessionBean();
        editItemSessionBean.getFiles().clear();
        editItemSessionBean.getLocators().clear();
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
        withdrawItemSessionBean.setItemListSessionBean(getViewItemSessionBean().getItemListSessionBean());
        return WithdrawItem.LOAD_WITHDRAWITEM;
    }

    /**
     * Redirects the user to the edit item page in modify-mode
     * 
     * @return Sring nav rule to load the editItem item page
     */
    public String modifyItem()
    {
        // clear the list of  locators and files when start modifying an item
    	EditItemSessionBean editItemSessionBean = this.getEditItemSessionBean();
        editItemSessionBean.getFiles().clear();
        editItemSessionBean.getLocators().clear();
    	return EditItem.LOAD_EDITITEM;
    }

    /**
     * Redirects the user to the create new revision page
     * Changed by DiT, 29.11.2007: only show contexts when user has privileges for more than one context
     * 
     * @return Sring nav rule to load the create new revision page
     */
    public String createNewRevision()
    {
        // clear the list of  locators and files when start creating  a new revision
    	EditItemSessionBean editItemSessionBean = this.getEditItemSessionBean();
        editItemSessionBean.getFiles().clear();
        editItemSessionBean.getLocators().clear();
        
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
            
            return this.getItemControllerSessionBean().createNewRevision(EditItem.LOAD_EDITITEM, context.getReference(), this.pubItem, null);
        }
        else
        {            
            ContextVO context = this.getCollectionListSessionBean().getDepositorContextList().get(0);

            // more than one context exists for this user; let him choose the right one
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has privileges for " + this.getCollectionListSessionBean().getDepositorContextList().size() 
                        + " different contexts.");
            }

            this.getRelationListSessionBean().setPubItemVO(this.getItemControllerSessionBean().getCurrentPubItem());
            
            return this.getItemControllerSessionBean().createNewRevision(CreateItem.LOAD_CREATEITEM, context.getReference(), this.pubItem, null);
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
        try
        {
        	this.getRelationListSessionBean().setRelationList(this.getItemControllerSessionBean().retrieveRevisions(this.getItemControllerSessionBean().getCurrentPubItem()));
        }
        catch (Exception e) {
			logger.error("Error setting revision list", e);
		}
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
     * list)
     * Changed by FrM: Inserted validation and call to "enter submission comment" page.
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
            getSubmitItemSessionBean().setNavigationStringToGoBack(getViewItemSessionBean().getNavigationStringToGoBack());
            return SubmitItem.LOAD_SUBMITITEM;
        }
        else if (report.isValid())
        {
            // TODO FrM: Informative messages
            getSubmitItemSessionBean().setNavigationStringToGoBack(getViewItemSessionBean().getNavigationStringToGoBack());
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
        
        if (report.isValid() && !report.hasItems()) {
       
            if (logger.isDebugEnabled())
            {
                logger.debug("Accepting item...");
            }
            getAcceptItemSessionBean().setNavigationStringToGoBack(getViewItemSessionBean().getNavigationStringToGoBack());
            return AcceptItem.LOAD_ACCEPTITEM;
        }
        else if (report.isValid())
        {
            // TODO FrM: Informative messages
            getAcceptItemSessionBean().setNavigationStringToGoBack(getViewItemSessionBean().getNavigationStringToGoBack());
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
        if (getViewItemSessionBean().getNavigationStringToGoBack()==null)
        {
            getViewItemSessionBean().setNavigationStringToGoBack(DepositorWS.LOAD_DEPOSITORWS);
        }
        
        String retVal = this.getItemControllerSessionBean().deleteCurrentPubItem(
                this.getViewItemSessionBean().getNavigationStringToGoBack());
        
        // show message
        if (!retVal.equals(ErrorPage.LOAD_ERRORPAGE))
        {
            info(getMessage(DepositorWS.MESSAGE_SUCCESSFULLY_DELETED));
            
            //redirect to last breadcrumb, if available
            BreadcrumbItemHistorySessionBean bhsb = (BreadcrumbItemHistorySessionBean)getSessionBean(BreadcrumbItemHistorySessionBean.class);
            PubItemListSessionBean pilsb = (PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class);
            pilsb.setHasChanged();
            
            
            try
            {
                for(int i = bhsb.getBreadcrumbItemHistory().size()-1; i > 0; i--)
                {
                	if(bhsb.getBreadcrumbItemHistory().get(i-1).isItemSpecific() == false)
                	{
                		getFacesContext().getExternalContext().redirect(bhsb.getBreadcrumbItemHistory().get(i-1).getPage());
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
    
    /**
     * Adds a cookie named "escidocCookie" that holds the eScidoc user handle to the provided http method object.
     * @author Tobias Schraut
     * @param method The http method to add the cookie to.
     */
    private void addHandleToMethod(final HttpMethod method, String eSciDocUserHandle)
    {
        // Staging file resource is protected, access needs authentication and
        // authorization. Therefore, the eSciDoc user handle must be provided.
        // Put the handle in the cookie "escidocCookie"
        method.setRequestHeader("Cookie", "escidocCookie=" + eSciDocUserHandle);
    }
    
    /**
     * Displays validation messages.
     * 
     * @param report The Validation report object.
     * @author Michael Franke
     */
    private void showValidationMessages(ValidationReportVO report)
    {
        
        info(getMessage(VALIDATION_ERROR_MESSAGE));
        
        for (Iterator<ValidationReportItemVO> iter = report.getItems().iterator(); iter.hasNext();)
        {
            ValidationReportItemVO element = (ValidationReportItemVO)iter.next();
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
     * Generates the affiliated organization list as one string for presenting it in the jsp via the dynamic html component.
     * Doubled organizations will be detected and merged. All organizzations will be numbered. 
     */
    private void createAffiliatedOrganizationList()
    {
        String formattedOrganization = "";
        List<CreatorVO> tempCreatorList;
        List<OrganizationVO> tempOrganizationList = null;
        List<OrganizationVO> sortOrganizationList = null;
        this.organizationArray = new ArrayList<String>();
        this.organizationList = new ArrayList<ViewItemOrganization>();
        tempOrganizationList = new ArrayList<OrganizationVO>();
        sortOrganizationList = new ArrayList<OrganizationVO>();
        tempCreatorList = this.pubItem.getMetadata().getCreators();
        int affiliationPosition = 0;
        for (int i = 0; i < tempCreatorList.size(); i++)
        {
            CreatorVO creator = new CreatorVO();
            creator = tempCreatorList.get(i);
            if (creator.getPerson() != null)
            {
                if (creator.getPerson().getOrganizations().size() > 0)
                {
                    for (int listSize = 0; listSize < creator.getPerson().getOrganizations().size(); listSize++)
                    {
                        tempOrganizationList.add(creator.getPerson().getOrganizations().get(listSize));
                    }
                    for (int j = 0; j < tempOrganizationList.size(); j++)
                    {
                        // if the organization is not in the list already, put
                        // it in.
                        if (!sortOrganizationList.contains(tempOrganizationList.get(j)))
                        {
                            affiliationPosition++;
                            sortOrganizationList.add(tempOrganizationList.get(j));
                            ViewItemOrganization viewOrganization = new ViewItemOrganization();
                            if(tempOrganizationList.get(j).getName() != null)
                            {
                                viewOrganization.setOrganizationName(tempOrganizationList.get(j).getName().getValue());
                            }
                            viewOrganization.setOrganizationAddress(tempOrganizationList.get(j).getAddress());
                            viewOrganization.setOrganizationIdentifier(tempOrganizationList.get(j).getIdentifier());
                            viewOrganization.setPosition(new Integer(affiliationPosition).toString());
                         
                            if(tempOrganizationList.get(j).getName() != null)
                            {
                                viewOrganization.setOrganizationInfoPage(tempOrganizationList.get(j).getName().getValue(),
                                        tempOrganizationList.get(j).getAddress());
                            }
                            this.organizationList.add(viewOrganization);
                        }
                    }
                }
            }
        }
        // save the List in the backing bean for later use.
        this.affiliatedOrganizationsList = sortOrganizationList;
        // generate a 'well-formed' list for presentation in the jsp
        for (int k = 0; k < sortOrganizationList.size(); k++)
        {
        	String name = sortOrganizationList.get(k).getName() != null ? sortOrganizationList.get(k).getName().getValue() : "";
            formattedOrganization = "<p>"+(k + 1) + ": " + name +"</p>" + "<p>" + sortOrganizationList.get(k).getAddress() + "</p>" + "<p>" + sortOrganizationList.get(k).getIdentifier() + "</p>";
            this.organizationArray.add(formattedOrganization);
        }
    }
    
    /**
     * Generates the creator list as list of formatted Strings.
     * 
     * @return String formatted creator list as string
     */
    private void createCreatorList()
    {
        StringBuffer creatorList = new StringBuffer();
        String formattedCreator = "";
        this.creatorArray = new ArrayList<CreatorDisplay>();
        this.creatorOrganizationsArray = new ArrayList<ViewItemCreatorOrganization>();
        // counter for organization array
        int counterOrganization = 0;
        StringBuffer annotation;
        ObjectFormatter formatter = new ObjectFormatter();
        
        
        for (int i = 0; i < this.pubItem.getMetadata().getCreators().size(); i++)
        {
            CreatorVO creator = new CreatorVO();
            creator = this.pubItem.getMetadata().getCreators().get(i);
            annotation = new StringBuffer();
            int organizationsFound = 0;
            for (int j = 0; j < this.affiliatedOrganizationsList.size(); j++)
            {
                if (creator.getPerson() != null)
                {
                    if (creator.getPerson().getOrganizations().contains(this.affiliatedOrganizationsList.get(j)))
                    {
                        if (organizationsFound == 0)
                        {
                            annotation.append("<sup>");
                        }
                        if (organizationsFound > 0 && j < this.affiliatedOrganizationsList.size())
                        {
                            annotation.append(",");
                        }
                        annotation.append(new Integer(j + 1).toString());
                        organizationsFound++;
                    }
                }
            }
            if (annotation.length() > 0)
            {
                annotation.append("</sup>");
            }
            formattedCreator = formatter.formatCreator(creator, annotation.toString());
            if (creator.getPerson() != null)
            {
                CreatorDisplay creatorDisplay = new CreatorDisplay();
                creatorDisplay.setFormattedDisplay(formattedCreator);
                if (creator.getPerson() != null && creator.getPerson().getIdentifier() != null && (creator.getPerson().getIdentifier().getType() == IdType.CONE || creator.getPerson().getIdentifier().getId().startsWith("urn:cone:")))
                {
                    try
                    {
                        creatorDisplay.setPortfolioLink(PropertyReader.getProperty("escidoc.cone.service.url") + "html/persons/" + creator.getPerson().getIdentifier().getId());
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                this.creatorArray.add(creatorDisplay);
            }
            if (creator.getOrganization() != null)
            {
                ViewItemCreatorOrganization creatorOrganization = new ViewItemCreatorOrganization();
                creatorOrganization.setOrganizationName(formattedCreator);
                creatorOrganization.setPosition(new Integer(counterOrganization).toString());
                creatorOrganization.setOrganizationAddress(creator.getOrganization().getAddress());
                creatorOrganization.setOrganizationInfoPage(formattedCreator, creator.getOrganization()
                        .getAddress());
                this.creatorOrganizationsArray.add(creatorOrganization);
                counterOrganization++;
            }
            creatorList.append(formattedCreator);
        }
    }
    
    
    /**
     * Returns the formatted Publishing Info according to filled elements
     * @return String the formatted Publishing Info
     */
    public String getPublishingInfo()
    {
        StringBuffer publishingInfo = new StringBuffer();
        publishingInfo.append("");
        if(this.pubItem.getMetadata().getPublishingInfo() != null)
        {
           
            // Place
            if(this.pubItem.getMetadata().getPublishingInfo().getPlace() != null && !this.pubItem.getMetadata().getPublishingInfo().getPlace().equals(""))
            {
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPlace().trim());
            }
            
            // colon
            if(this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim().equals("") && this.pubItem.getMetadata().getPublishingInfo().getPlace() != null && !this.pubItem.getMetadata().getPublishingInfo().getPlace().trim().equals(""))
            {
                    publishingInfo.append(" : ");
            }
            
            // Publisher
            if(this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().equals(""))
            {
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim());
            }
            
            // Comma
            if((this.pubItem.getMetadata().getPublishingInfo().getEdition() != null && !this.pubItem.getMetadata().getPublishingInfo().getEdition().trim().equals("")) && ((this.pubItem.getMetadata().getPublishingInfo().getPlace() != null && !this.pubItem.getMetadata().getPublishingInfo().getPlace().trim().equals("")) || (this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim().equals(""))))
            {
                    publishingInfo.append(", ");
            }
            
            // Edition
            if(this.pubItem.getMetadata().getPublishingInfo().getEdition() != null)
            {
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getEdition());
            }
            
        }
        return publishingInfo.toString();
    }
    
    /**
     * Returns all Identifiers as formatted String
     * @return String the formatted Identifiers
     */
    public String getIdentifiers()
    {
        StringBuffer identifiers = new StringBuffer();

        if (this.pubItem.getMetadata().getIdentifiers() != null)
        {
            for (int i = 0; i < this.pubItem.getMetadata().getIdentifiers().size(); i++)
            {
                identifiers.append(this.pubItem.getMetadata().getIdentifiers().get(i).getTypeString());
                identifiers.append(": ");
                if (CommonUtils.getisUriValidUrl(this.pubItem.getMetadata().getIdentifiers().get(i)))
                {
                    identifiers.append("<a href='"+this.pubItem.getMetadata().getIdentifiers().get(i).getId()+"'>"+this.pubItem.getMetadata().getIdentifiers().get(i).getId()+"</a>");
                }
                else if (this.pubItem.getMetadata().getIdentifiers().get(i).getType() == IdType.DOI)
                {
                    identifiers.append("<a target='_blank' href='http://dx.doi.org/"+this.pubItem.getMetadata().getIdentifiers().get(i).getId()+"'>"+this.pubItem.getMetadata().getIdentifiers().get(i).getId()+"</a>");
                }
                else if (this.pubItem.getMetadata().getIdentifiers().get(i).getType() == IdType.EDOC)
                {
                    identifiers.append("<a target='_blank' href='http://edoc.mpg.de/"+this.pubItem.getMetadata().getIdentifiers().get(i).getId()+"'>"+this.pubItem.getMetadata().getIdentifiers().get(i).getId()+"</a>");
                }
                else if (this.pubItem.getMetadata().getIdentifiers().get(i).getType() == IdType.ISI)
                {
                    identifiers.append("<a target='_blank' href='" + ISI_KNOWLEDGE_BASE_LINK +this.pubItem.getMetadata().getIdentifiers().get(i).getId()+ ISI_KNOWLEDGE_DEST_APP +"'>"+this.pubItem.getMetadata().getIdentifiers().get(i).getId()+"</a>");
                }
                else
                {
                    identifiers.append(this.pubItem.getMetadata().getIdentifiers().get(i).getId());
                }
                if (i < this.pubItem.getMetadata().getIdentifiers().size() - 1)
                {
                    identifiers.append("<br/>");
                }
            }
        }
        return identifiers.toString();
    }
    
    /**
     * Returns a true or a false according top the existance of specified fields in the details section
     * @return boolean
     */
    public boolean getShowDetails()
    {
    	if(this.pubItem.getMetadata() != null)
        {
            if((this.pubItem.getMetadata().getLanguages() != null && this.pubItem.getMetadata().getLanguages().size() > 0)
                    || (getShowDates())
                    || (this.pubItem.getMetadata().getTotalNumberOfPages() != null && !this.pubItem.getMetadata().getTotalNumberOfPages().trim().equals(""))
                    || (this.pubItem.getMetadata().getPublishingInfo() != null)
                    || (this.pubItem.getMetadata().getTableOfContents() != null && this.pubItem.getMetadata().getTableOfContents().getValue() != null && !this.pubItem.getMetadata().getTableOfContents().getValue().trim().equals(""))
                    || (this.pubItem.getMetadata().getReviewMethod() != null)
                    || (this.pubItem.getMetadata().getIdentifiers() != null && this.pubItem.getMetadata().getIdentifiers().size() > 0)
                    || (this.pubItem.getMetadata().getDegree() != null)
                    || (this.pubItem.getMetadata().getLocation() != null && !this.pubItem.getMetadata().getLocation().trim().equals(""))
                    )
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
    
    /**
     * Returns a true or a false according to the existance of an event in the item
     * @return boolean
     */
    public boolean getShowEvents()
    {
    	if(this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getEvent() != null)
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
     * @return boolean
     */
    public boolean getShowSources()
    {
    	if (this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getSources() != null && this.pubItem.getMetadata().getSources().size() > 0)
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
     * @return boolean
     */
    public boolean getShowFiles()
    {
    	if (this.fileList != null && this.fileList.size() > 0)
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
     * @return int
     */
    public int getAmountOfFiles() {
        if (this.fileList != null && this.fileList.size() > 0)
        {
            return this.fileList.size();
        }
        else
        {
            return 0;
        }
    }
    
    /**
     * Returns a true or a false according to the existance of locators in the item
     * @return boolean
     */
    public boolean getShowLocators()
    {
    	if (this.locatorList != null && this.locatorList.size() > 0)
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
     * @return int
     */
    public int getAmountOfLocators() {
        if (this.locatorList != null && this.locatorList.size() > 0)
        {
            return this.locatorList.size();
        }
        else
        {
            return 0;
        }
    }
    
    /**
     * Returns a true or a false according to the user state (logged in or not)
     * @author Markus Haarlaender
     * @return boolean
     */
    public boolean getShowSystemDetails()
    {
        return this.loginHelper.isLoggedIn();
    }
    
    /**
     * Returns a boolean according to the user item state
     * @author Markus Haarlaender
     * @return boolean
     */
    public boolean getShowCiteItem()
    {
        if(getPubItem().getPublicStatus().equals(State.WITHDRAWN))
        {
        	return false;	
        }
    	return getPubItem().getVersion().getState().equals(PubItemVO.State.RELEASED);
    }
    
    
    
    public String getDates() 
    {
        List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
        pubItemList.add(getPubItem());
        List<PubItemVOPresentation> pubItemPresentationList = CommonUtils.convertToPubItemVOPresentationList(pubItemList);
        PubItemVOPresentation pubItemPresentation = pubItemPresentationList.get(0);
        
        return pubItemPresentation.getDatesAsString();
        
    }
    
    /**
     * Returns false if all dates are empty
     * @author Markus Haarlaender
     * @return boolean
     */
    public boolean getShowDates()
    {
        return
        (
                (this.getPubItem().getMetadata().getDatePublishedInPrint() != null && !this.getPubItem().getMetadata().getDatePublishedInPrint().equals("") ) ||
                (this.getPubItem().getMetadata().getDatePublishedOnline() != null && !this.getPubItem().getMetadata().getDatePublishedOnline().equals("")) ||
                (this.getPubItem().getMetadata().getDateAccepted() != null && !this.getPubItem().getMetadata().getDateAccepted().equals("")) ||
                (this.getPubItem().getMetadata().getDateSubmitted() != null && !this.getPubItem().getMetadata().getDateSubmitted().equals("") ) ||
                (this.getPubItem().getMetadata().getDateModified() != null && !this.getPubItem().getMetadata().getDateModified().equals("")) ||
                (this.getPubItem().getMetadata().getDateCreated() != null && !this.getPubItem().getMetadata().getDateCreated().equals("")) 
                
        );
    }
    
    /**
     * Returns a true or a false according to the invited state of the item
     * @return boolean
     */
    public boolean getInvited()
    {
    	if(this.pubItem.getMetadata().getEvent().getInvitationStatus() != null)
        {
    		if(this.pubItem.getMetadata().getEvent().getInvitationStatus().equals(EventVO.InvitationStatus.INVITED))
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
     * @return boolean
     */
    public boolean getItemIsWithdrawn()
    {
    	if(this.pubItem.getVersion().getState().equals(PubItemVO.State.WITHDRAWN))
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
     * @return String formatted withdrawal date
     */
    public String getWithdrawalDate()
    {
    	String date = "";
    	if(this.pubItem.getPublicStatus().equals(PubItemVO.State.WITHDRAWN))
        {
    		if(this.pubItem.getModificationDate() != null)
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
                this.context = itemControllerSessionBean
                        .retrieveContext(this.pubItem.getContext().getObjectId());
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
     * Returns the Context the item belongs to
     * 
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
                this.context = itemControllerSessionBean
                        .retrieveContext(this.pubItem.getContext().getObjectId());
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
                    affiliationList.add(
                            new AffiliationVOPresentation(itemControllerSessionBean.retrieveAffiliation(affiliationRefList.get(i).getObjectId())));
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
     * @return String the formatted date string
     */
    public String getStartEndDate()
    {
        StringBuffer date = new StringBuffer();
        
        if(this.pubItem.getMetadata().getEvent().getStartDate() != null)
        {
            date.append(this.pubItem.getMetadata().getEvent().getStartDate());
        }
        
        if(this.pubItem.getMetadata().getEvent().getEndDate() != null)
        {
            date.append(" - ");
            date.append(this.pubItem.getMetadata().getEvent().getEndDate());
        }
        return date.toString();
    }
    
    /**
     * Returns the Modification date as formatted String (YYYY-MM-DD)
     * @return String the formatted date of modification
     */
    public String getModificationDate()
    {
    	return CommonUtils.formatTimestamp(this.pubItem.getModificationDate());
    }
    
    /**
     * gets the parameters out of the faces context
     * 
     * @param name name of the parameter in the faces context
     * @return the value of the parameter as string
     */
    public static String getFacesParamValue(String name)
    {
        return (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
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
     * Returns a reference to the scoped data bean (the ItemListSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected ItemListSessionBean getItemListSessionBean()
    {
        return (ItemListSessionBean)getSessionBean(ItemListSessionBean.class);
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
     * Returns the CommonSessionBean.
     * 
     * @return a reference to the scoped data bean (CommonSessionBean)
     */
    protected CommonSessionBean getSessionBean()
    {
        return (CommonSessionBean)getSessionBean(CommonSessionBean.class);
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
        return (ApplicationBean) getApplicationBean(ApplicationBean.class);
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
    
    public PubItemVO getPubItem() {
		return this.pubItem;
	}

	public void setPubItem(PubItemVOPresentation pubItem) {
		this.pubItem = pubItem;
	}

	public ArrayList<TextVO> getAbstracts()
	{
		ArrayList<TextVO> abstracts = new ArrayList<TextVO>();
		if(this.pubItem.getMetadata().getAbstracts() != null)
		{
			for(int i = 0; i < this.pubItem.getMetadata().getAbstracts().size(); i++)
			{
				//abstracts.add(new TextVO(CommonUtils.htmlEscape(this.pubItem.getMetadata().getAbstracts().get(i).getValue())));
				abstracts.add(new TextVO(this.pubItem.getMetadata().getAbstracts().get(i).getValue()));
			}
		}
		return abstracts;
	}
	
	public boolean getHasAbstracts() {
	    return this.pubItem.getMetadata().getAbstracts() != null && this.pubItem.getMetadata().getAbstracts().size() > 0;
	}
	public String getGenre()
    {
    	String genre="";
    	if(this.pubItem.getMetadata().getGenre() != null)
    	{
    		genre = getLabel(this.i18nHelper.convertEnumToString(this.pubItem.getMetadata().getGenre()));
    	}
		return genre;
    }
	
	public String getReviewMethod()
    {
    	String reviewMethod="";
    	if(this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getReviewMethod() != null)
    	{
    		reviewMethod = getLabel(this.i18nHelper.convertEnumToString(this.pubItem.getMetadata().getReviewMethod()));
    	}
		return reviewMethod;
    }
	
	public String getDegreeType()
    {
    	String degreeType="";
    	if(this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getDegree() != null)
    	{
    		degreeType = getLabel(this.i18nHelper.convertEnumToString(this.pubItem.getMetadata().getDegree()));
    	}
		return degreeType;
    }
	
	public String getItemState()
    {
    	
		String itemState="";
		if(this.pubItem.getVersion().getState() != null)
		{
			itemState = getLabel(this.i18nHelper.convertEnumToString(this.pubItem.getVersion().getState()));
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

	public void setAffiliatedOrganizationsList(
			List<OrganizationVO> affiliatedOrganizationsList)
	{
		this.affiliatedOrganizationsList = affiliatedOrganizationsList;
	}

	public ArrayList<CreatorDisplay> getCreatorArray()
	{
		return this.creatorArray;
	}

	public void setCreatorArray(ArrayList<CreatorDisplay> creatorArray)
	{
		this.creatorArray = creatorArray;
	}

	public ArrayList<ViewItemCreatorOrganization> getCreatorOrganizationsArray()
	{
		return this.creatorOrganizationsArray;
	}

	public void setCreatorOrganizationsArray(
			ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray)
	{
		this.creatorOrganizationsArray = creatorOrganizationsArray;
	}

	public UIXIterator getTitleIterator() {
		return this.titleIterator;
	}

	public void setTitleIterator(UIXIterator titleIterator) {
		this.titleIterator = titleIterator;
	}

	public UIXIterator getCreatorPersonsIterator() {
		return this.creatorPersonsIterator;
	}

	public void setCreatorPersonsIterator(UIXIterator creatorPersonsIterator) {
		this.creatorPersonsIterator = creatorPersonsIterator;
	}

	public UIXIterator getCreatorAffiliationsIterator() {
		return this.creatorAffiliationsIterator;
	}

	public void setCreatorAffiliationsIterator(
			UIXIterator creatorAffiliationsIterator) {
		this.creatorAffiliationsIterator = creatorAffiliationsIterator;
	}

	public UIXIterator getLanguagesIterator() {
		return this.languagesIterator;
	}

	public void setLanguagesIterator(UIXIterator languagesIterator) {
		this.languagesIterator = languagesIterator;
	}

	public UIXIterator getAbstractIterator() {
		return this.abstractIterator;
	}

	public void setAbstractIterator(UIXIterator abstractIterator) {
		this.abstractIterator = abstractIterator;
	}

	public UIXIterator getEventAltTitleIterator() {
		return this.eventAltTitleIterator;
	}

	public void setEventAltTitleIterator(UIXIterator eventAltTitleIterator) {
		this.eventAltTitleIterator = eventAltTitleIterator;
	}

	public UIXIterator getSourceIterator() {
		return this.sourceIterator;
	}

	public void setSourceIterator(UIXIterator sourceIterator) {
		this.sourceIterator = sourceIterator;
	}

	public UIXIterator getSourceTitleIterator() {
		return this.sourceTitleIterator;
	}

	public void setSourceTitleIterator(UIXIterator sourceTitleIterator) {
		this.sourceTitleIterator = sourceTitleIterator;
	}

	public UIXIterator getSourceCreatorPersonsIterator() {
		return this.sourceCreatorPersonsIterator;
	}

	public void setSourceCreatorPersonsIterator(
			UIXIterator sourceCreatorPersonsIterator) {
		this.sourceCreatorPersonsIterator = sourceCreatorPersonsIterator;
	}

	public UIXIterator getSourceCreatorAffiliationsIterator() {
		return this.sourceCreatorAffiliationsIterator;
	}

	public void setSourceCreatorAffiliationsIterator(
			UIXIterator sourceCreatorAffiliationsIterator) {
		this.sourceCreatorAffiliationsIterator = sourceCreatorAffiliationsIterator;
	}

	public List<SourceBean> getSourceList() {
		return this.sourceList;
	}

	public void setSourceList(List<SourceBean> sourceList) {
		this.sourceList = sourceList;
	}

	public UIXIterator getFileIterator() {
		return this.fileIterator;
	}

	public void setFileIterator(UIXIterator fileIterator) {
		this.fileIterator = fileIterator;
	}

	public List<FileBean> getFileList() {
		return this.fileList;
	}

	public void setFileList(List<FileBean> fileList) {
		this.fileList = fileList;
	}

	public List<FileBean> getLocatorList() {
		return this.locatorList;
	}

	public void setLocatorList(List<FileBean> locatorList) {
		this.locatorList = locatorList;
	}

	public UIXIterator getLocatorIterator() {
		return this.locatorIterator;
	}

	public void setLocatorIterator(UIXIterator locatorIterator) {
		this.locatorIterator = locatorIterator;
	}

    public UIXIterator getCreatorOrganizationsIterator()
    {
        return this.creatorOrganizationsIterator;
    }

    public void setCreatorOrganizationsIterator(UIXIterator creatorOrganizationsIterator)
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
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();

        if (request.getParameter("fromEasySub")!=null) {
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
        else if (this.pubItem.getPublicStatus() != State.RELEASED || !getIsModerator())
        {
            return false;
        }
        else
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
    }
    
    public UIXIterator getSourceCreatorOrganizationsIterator()
    {
        return this.sourceCreatorOrganizationsIterator;
    }

    public void setSourceCreatorOrganizationsIterator(UIXIterator sourceCreatorOrganizationsIterator)
    {
        this.sourceCreatorOrganizationsIterator = sourceCreatorOrganizationsIterator;
    }

    public UIXIterator getFileSearchHitIterator()
    {
        return this.fileSearchHitIterator;
    }

    public void setFileSearchHitIterator(UIXIterator fileSearchHitIterator)
    {
        this.fileSearchHitIterator = fileSearchHitIterator;
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
        String itemState="";
        if(this.pubItem.getPublicStatus() != null)
        {
            itemState = getLabel(this.i18nHelper.convertEnumToString(this.pubItem.getPublicStatus()));
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

	public UIXIterator getSubjectIterator() {
		return subjectIterator;
	}

	public void setSubjectIterator(UIXIterator subjectIterator) {
		this.subjectIterator = subjectIterator;
	}

	/*
    public boolean getHasRevision()
    {
        return this.hasRevision;
    }

    public void setHasRevision(boolean hasRevision)
    {
        this.hasRevision = hasRevision;
    }
	*/
	public String addToBasket()
	{
	    PubItemStorageSessionBean pubItemStorage = (PubItemStorageSessionBean) getSessionBean(PubItemStorageSessionBean.class);
       
        if (!pubItemStorage.getStoredPubItems().containsKey(this.pubItem.getVersion().getObjectIdAndVersion()))
        {
            pubItemStorage.getStoredPubItems().put(this.pubItem.getVersion().getObjectIdAndVersion(), this.pubItem.getVersion());
            info(getMessage("basket_SingleAddedSuccessfully"));
        }
        else
        {
            error(getMessage("basket_SingleAlreadyInBasket"));
        }
        return "";
	}
	
	public String removeFromBasket()
	{
	    PubItemStorageSessionBean pssb = (PubItemStorageSessionBean)getSessionBean(PubItemStorageSessionBean.class);
	    pssb.getStoredPubItems().remove(this.pubItem.getVersion().getObjectIdAndVersion());
	    info(getMessage("basket_SingleRemovedSuccessfully"));
	    return "";
	}
	
	public boolean getIsInBasket()
	{
	    PubItemStorageSessionBean pubItemStorage = (PubItemStorageSessionBean) getSessionBean(PubItemStorageSessionBean.class);
	    return pubItemStorage.getStoredPubItems().containsKey(this.pubItem.getVersion().getObjectIdAndVersion());
	    
	}

    
    public String getLinkForActionsView()
    {
        String url = "viewItemFullPage.jsp?"+PARAMETERNAME_ITEM_ID+"="+getPubItem().getVersion().getObjectIdAndVersion()+"&"+PARAMETERNAME_MENU_VIEW+"=ACTIONS";
        return url;
        
    }
    
    public String getLinkForExportView()
    {
        return "viewItemFullPage.jsp?"+PARAMETERNAME_ITEM_ID+"="+getPubItem().getVersion().getObjectIdAndVersion()+"&"+PARAMETERNAME_MENU_VIEW+"=EXPORT";
        
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
                    + curExportFormat.getSelectedFileFormat().getName());
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
    	String fileName = "export_" + curExportFormat.getName().toLowerCase() + "." + sb.getFileFormat();
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
     *This method returns the contact email address of the moderator strored in the item's context. If it is empty the pubman support address will be returned.
     * @return the moderator's email address (if available, otherwise pubman support address)
     */
    public String getModeratorContactEmail()
    {
    	String contactEmail = "";
    	contactEmail = this.getContext().getAdminDescriptor().getContactEmail();
    	if(contactEmail == null || contactEmail.trim().equals(""))
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

	public String getFwUrl() {
		return fwUrl;
	}

	public void setFwUrl(String fwUrl) {
		this.fwUrl = fwUrl;
	}

	public String getItemPattern() {
		return itemPattern;
	}

	public void setItemPattern(String itemPattern) {
		this.itemPattern = itemPattern;
	}
	
	private void logViewAction()
	{
	    final String ip = getIP();
	    final String sessId = getSessionId();
	    final String referer = getReferer();
	    new Thread(){
	        public void run()
	        {
	            try
	            {
	                PubItemSimpleStatistics statistics = new SimpleStatistics();
	                statistics.logPubItemAction(getPubItem(), ip, ItemAction.RETRIEVE, sessId, loginHelper.getLoggedIn(), referer);
	            }
	           
	            catch (Exception e)
	            {
	               logger.error("Could not log statistical data", e);
	            }
	        }
	    }.start();
	   
	}
    
	public String getStatisticString()
	{
	    
	    LoginHelper loginHelper = (LoginHelper)getSessionBean(LoginHelper.class);
	    String url = "ViewItemPage";
	    url += "?itemId=" + getPubItem().getVersion().getObjectId();
	    url += "&loggedIn=" + loginHelper.getLoggedIn();
	    
	    List<CreatorVO> creatorList = getPubItem().getMetadata().getCreators();
	    
	    int i=0;
	    
	    for(CreatorVO creator : creatorList)
	    {
	        if (creator.getPerson()!=null && creator.getPerson().getIdentifier()!=null && creator.getPerson().getIdentifier().getId()!=null && !creator.getPerson().getIdentifier().getId().equals(""))
	        {
	            url += "auth" + i++ + "=" + creator.getPerson().getIdentifier().getId();
	        }
	        
	       
	    }
	    
	    return url;
	    
	}
}    
