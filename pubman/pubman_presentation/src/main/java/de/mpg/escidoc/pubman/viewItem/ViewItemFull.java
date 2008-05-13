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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.viewItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.UIXIterator;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.CommonSessionBean;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.RightsManagementSessionBean;
import de.mpg.escidoc.pubman.ViewItemRevisionsPage;
import de.mpg.escidoc.pubman.ViewItemStatisticsPage;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.createItem.CreateItem;
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
import de.mpg.escidoc.pubman.desktop.Login;
import de.mpg.escidoc.pubman.easySubmission.EasySubmission;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.itemLog.ViewItemLog;
import de.mpg.escidoc.pubman.releases.ItemVersionListSessionBean;
import de.mpg.escidoc.pubman.releases.ReleaseHistory;
import de.mpg.escidoc.pubman.revisions.CreateRevision;
import de.mpg.escidoc.pubman.revisions.RelationListSessionBean;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.search.SearchResultListSessionBean;
import de.mpg.escidoc.pubman.submitItem.SubmitItem;
import de.mpg.escidoc.pubman.submitItem.SubmitItemSessionBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.ObjectFormatter;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.pubman.viewItem.bean.FileBean;
import de.mpg.escidoc.pubman.viewItem.bean.SourceBean;
import de.mpg.escidoc.pubman.viewItem.ui.COinSUI;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItem;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItemSessionBean;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * Backing bean for ViewItemFull.jspf (for viewing items in a full context).
 * 
 * @author Tobias Schraut, created 03.09.2007
 * @version: $Revision: 1656 $ $LastChangedDate: 2007-12-10 17:56:58 +0100 (Mo, 10 Dez 2007) $
 */
public class ViewItemFull extends FacesBean
{
    private HtmlPanelGroup panelItemFull = new HtmlPanelGroup();
    private static Logger logger = Logger.getLogger(ViewItemFull.class);
    final public static String BEAN_NAME = "ViewItemFull";
    public static final String PARAMETERNAME_ITEM_ID = "itemId";
    // Faces navigation string
    public final static String LOAD_VIEWITEM = "loadViewItem";
    
    public boolean isDepositor = false;
    public boolean isModerator = false;
    
    // Validation Service
    private ItemValidating itemValidating = null; 
    private PubItemVO pubItem = null;

    private HtmlMessages valMessage = new HtmlMessages();

    // Added by DiT: constant for the function modify and new revision to check the rights and/or if the function has to be disabled (DiT)
    private static final String FUNCTION_MODIFY = "modify";
    private static final String FUNCTION_NEW_REVISION = "new_revision";
    
    private static final String VALIDATION_ERROR_MESSAGE = "depositorWS_NotSuccessfullySubmitted";
    
    private String coins;
    
    private UIXIterator titleIterator = new UIXIterator();
    
    private UIXIterator creatorPersonsIterator = new UIXIterator();
    
    private UIXIterator creatorOrganizationsIterator = new UIXIterator();
    
    private UIXIterator creatorAffiliationsIterator = new UIXIterator();
    
    private UIXIterator languagesIterator = new UIXIterator();
    
    private UIXIterator abstractIterator = new UIXIterator();
    
    private UIXIterator eventAltTitleIterator = new UIXIterator();
    
    private UIXIterator sourceIterator = new UIXIterator();
    
    private UIXIterator sourceTitleIterator = new UIXIterator();
    
    private UIXIterator sourceCreatorPersonsIterator = new UIXIterator();
    
    private UIXIterator sourceCreatorOrganizationsIterator = new UIXIterator(); 
    
    
    private UIXIterator sourceCreatorAffiliationsIterator = new UIXIterator();	
    
    private UIXIterator fileIterator = new UIXIterator();
    
    private UIXIterator locatorIterator = new UIXIterator();
    
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
    private ArrayList<String> creatorArray;
    
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
        
        // Try to get the validation service
        try
        {
            InitialContext initialContext = new InitialContext();
            this.itemValidating = (ItemValidating)initialContext.lookup(ItemValidating.SERVICE_NAME);
        }
        catch (NamingException ne)
        {
            throw new RuntimeException("Validation service not initialized", ne);
        }
        
        // Try to get a pubitem either via the controller session bean or an URL Parameter
        itemID = request.getParameter(ViewItemFull.PARAMETERNAME_ITEM_ID);
        if(itemID != null)
        {
            try
            {
                this.pubItem = this.getItemControllerSessionBean().retrieveItem(itemID);
                this.getItemControllerSessionBean().setCurrentPubItem(this.pubItem);
            }
            catch (Exception e)
            {
                logger.error("Could not retrieve release with id " + itemID, e);
                Login login = (Login)getSessionBean(Login.class);
                login.forceLogout();
                // TODO: Error handling
            }
        }
        else
        {
            this.pubItem = this.getItemControllerSessionBean().getCurrentPubItem();
        }
        
        
        //check if arriving from easy submission
        EasySubmission easySubmissionRequestBean = (EasySubmission)getRequestBean(EasySubmission.class);
        this.isFromEasySubmission = easySubmissionRequestBean.getFromEasySubmission();
        
        if(this.pubItem != null)
        {
            //set citation url
            try
            {
                String pubmanUrl = PropertyReader.getProperty("escidoc.pubman.instance.url");
                
                String itemPattern = PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1", getPubItem().getVersion().getObjectIdAndVersion());
                
                
                if(!pubmanUrl.endsWith("/")) pubmanUrl = pubmanUrl + "/";
                if (itemPattern.startsWith("/")) itemPattern = itemPattern.substring(1, itemPattern.length()-1);
                
                citationURL = pubmanUrl + itemPattern;
                
            }
            catch (IOException e)
            {
                e.printStackTrace();
                citationURL = "";
            }
            
            loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
            
            //DiT: multiple new conditions for link-activation added
            isModerator = loginHelper.getAccountUser().isModerator(this.pubItem.getContext());
            isDepositor = loginHelper.getAccountUser().isDepositor();
            
            isOwner = true;
            if (this.pubItem.getOwner() != null)
            {
            	isOwner = (loginHelper.getAccountUser().getReference() != null ? loginHelper.getAccountUser().getReference().getObjectId().equals(this.pubItem.getOwner().getObjectId()) : false);
            }
            isModifyDisabled = this.getRightsManagementSessionBean().isDisabled(RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "." + ViewItemFull.FUNCTION_MODIFY);
            isCreateNewRevisionDisabled = this.getRightsManagementSessionBean().isDisabled(RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "." + ViewItemFull.FUNCTION_NEW_REVISION);

            //@author Markus Haarlaender - setting properties for Action Links
            
            isLoggedIn = loginHelper.isLoggedIn();
            isLatestVersion = this.pubItem.getVersion().getVersionNumber() == this.pubItem.getLatestVersion().getVersionNumber();
            isLatestRelease = this.pubItem.getVersion().getVersionNumber() == this.pubItem.getLatestRelease().getVersionNumber();
            isStateWithdrawn = this.pubItem.getVersion().getState().toString().equals(PubItemVO.State.WITHDRAWN.toString());
            isStateSubmitted = this.pubItem.getVersion().getState().toString().equals(PubItemVO.State.SUBMITTED.toString());
            isStateReleased = this.pubItem.getVersion().getState().toString().equals(PubItemVO.State.RELEASED.toString());
            isStatePending = this.pubItem.getVersion().getState().toString().equals(PubItemVO.State.PENDING.toString());
            
            
            // set up some pre-requisites
            // the list of numbered affiliated organizations 
            createAffiliatedOrganizationList();
            
            // the list of creators (persons and organizations)
            createCreatorList();
            
            // create the COinS information
            COinSUI coins = new COinSUI();
            this.coins = coins.getCOinSString(this.pubItem);
            
            // the list of sources
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
            
            int countFiles = 0;
            int countLocators = 0;
            
            for(int i = 0; i < this.pubItem.getFiles().size(); i++)
            {
            	if(searchHitList.size() > 0 && !this.pubItem.getVersion().getState().equals(PubItemVO.State.WITHDRAWN))
                {
            		//this.fileList.add(new FileBean(this.pubItem.getFiles().get(i), i, this.pubItem.getVersion().getState(), searchHitList));
            		
            		if(this.pubItem.getFiles().get(i).getLocator() != null && !this.pubItem.getFiles().get(i).getLocator().trim().equals(""))
                    {
                        this.locatorList.add(new FileBean(this.pubItem.getFiles().get(i), countLocators, this.pubItem.getVersion().getState()));
                        countLocators ++;
                    }
                    // add files
                    else
                    {
                        this.fileList.add(new FileBean(this.pubItem.getFiles().get(i), countFiles, this.pubItem.getVersion().getState(), searchHitList));
                        countFiles ++;
                    }
                }
            	else
            	{
            		// add locators
            		if(this.pubItem.getFiles().get(i).getLocator() != null && !this.pubItem.getFiles().get(i).getLocator().trim().equals(""))
            		{
            			this.locatorList.add(new FileBean(this.pubItem.getFiles().get(i), countLocators, this.pubItem.getVersion().getState()));
            			countLocators ++;
            		}
            		// add files
            		else
            		{
            			this.fileList.add(new FileBean(this.pubItem.getFiles().get(i), countFiles, this.pubItem.getVersion().getState()));
            			countFiles ++;
            		}
            	}
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
        // Changed by DiT, 29.11.2007: only show contexts when user has privileges for more than one context
        // if there is only one context for this user we can skip the CreateItem-Dialog and create the new item directly
        if (this.getCollectionListSessionBean().getContextList().size() == 0)
        {
            logger.warn("The user does not have privileges for any context.");
            error(getMessage("ViewItemFull_user_has_no_context"));
            return null;
        }
        else if (this.getCollectionListSessionBean().getContextList().size() == 1)
        {            
            ContextVO context = this.getCollectionListSessionBean().getContextList().get(0);
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has only privileges for one collection (ID: " 
                        + context.getReference().getObjectId() + ")");
            }
            
            return this.getItemControllerSessionBean().createNewRevision(EditItem.LOAD_EDITITEM, context.getReference(), this.pubItem, null);
        }
        else
        {            
            ContextVO context = this.getCollectionListSessionBean().getContextList().get(0);

            // more than one context exists for this user; let him choose the right one
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has privileges for " + this.getCollectionListSessionBean().getContextList().size() 
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
        
        ValidationReportVO report = null;
        try
        {
            report = this.itemValidating.validateItemObject(this.getItemControllerSessionBean().getCurrentPubItem(), "submit_item");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Validation error", e);
        }
        logger.debug("Validation Report: " + report);
        
        if (report.isValid() && !report.hasItems()) {
       
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
    
    public String submitItem2()
    {
        return submitItem();
    }

    /**
     * deletes the selected item(s) an redirects the user to the page he came from (depositor workspace or search result
     * list)
     * 
     * @return String nav rule to load the page the user came from
     */
    public String deleteItem()
    {
        String retVal = this.getItemControllerSessionBean().deleteCurrentPubItem(
                this.getViewItemSessionBean().getNavigationStringToGoBack());
        // show message
        if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        {
            if (this.getViewItemSessionBean().getNavigationStringToGoBack().equals(DepositorWS.LOAD_DEPOSITORWS))
            {
                this.showMessageDepositorWS(DepositorWS.MESSAGE_SUCCESSFULLY_DELETED);
            }
            else if (this.getViewItemSessionBean().getNavigationStringToGoBack().equals(
                    SearchResultList.LOAD_SEARCHRESULTLIST))
            {
                this.showMessageSearchResultList(SearchResultList.MESSAGE_SUCCESSFULLY_DELETED);
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
        valMessage.setRendered(true);
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
        this.creatorArray = new ArrayList<String>();
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
                            annotation.append("   [");
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
                annotation.append("]");
            }
            formattedCreator = formatter.formatCreator(creator) + annotation.toString();
            if (creator.getPerson() != null)
            {
                this.creatorArray.add(formattedCreator);
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
            // Edition
            if(this.pubItem.getMetadata().getPublishingInfo().getEdition() != null)
            {
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getEdition());
            }
            
            // Comma
            if((this.pubItem.getMetadata().getPublishingInfo().getEdition() != null && !this.pubItem.getMetadata().getPublishingInfo().getEdition().trim().equals("")) && ((this.pubItem.getMetadata().getPublishingInfo().getPlace() != null && !this.pubItem.getMetadata().getPublishingInfo().getPlace().trim().equals("")) || (this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim().equals(""))))
            {
                    publishingInfo.append(". ");
            }
            
            // Place
            if(this.pubItem.getMetadata().getPublishingInfo().getPlace() != null)
            {
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPlace().trim());
            }
            
            // colon
            if(this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim().equals("") && this.pubItem.getMetadata().getPublishingInfo().getPlace() != null && !this.pubItem.getMetadata().getPublishingInfo().getPlace().trim().equals(""))
            {
                    publishingInfo.append(" : ");
            }
            
            // Publisher
            if(this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null)
            {
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim());
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
                identifiers.append(this.pubItem.getMetadata().getIdentifiers().get(i).getId());
                if (i < this.pubItem.getMetadata().getIdentifiers().size() - 1)
                {
                    identifiers.append(", ");
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
            if((this.pubItem.getMetadata().getAbstracts() != null && this.pubItem.getMetadata().getAbstracts().size() > 0)
                    || (this.pubItem.getMetadata().getTableOfContents() != null && this.pubItem.getMetadata().getTableOfContents().getValue() != null && !this.pubItem.getMetadata().getTableOfContents().getValue().trim().equals(""))
                    || this.pubItem.getMetadata().getPublishingInfo() != null
                    || (this.pubItem.getMetadata().getTotalNumberOfPages() != null && !this.pubItem.getMetadata().getTotalNumberOfPages().trim().equals(""))
                    || this.pubItem.getMetadata().getDegree() != null
                    || (this.pubItem.getMetadata().getLocation() != null && !this.pubItem.getMetadata().getLocation().trim().equals(""))
                    || (this.pubItem.getMetadata().getIdentifiers() != null && this.pubItem.getMetadata().getIdentifiers().size() > 0))
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
     * Returns a true or a false according to the user state (logged in or not)
     * @author Markus Haarlaender
     * @return boolean
     */
    public boolean getShowSystemDetails()
    {
        return loginHelper.isLoggedIn();
    }
    
    /**
     * Returns a boolean according to the user item state
     * @author Markus Haarlaender
     * @return boolean
     */
    public boolean getShowCiteItem()
    {
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
    	if(this.pubItem.getVersion().getState().equals(PubItemVO.State.WITHDRAWN))
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
     * Gets the affiliation of the context the item belongs to.
     *
     * @return String formatted context name
     */
    public String getAffiliations()
    {
        StringBuffer affiliations = new StringBuffer();
        List<AffiliationRO> affiliationRefList = new ArrayList<AffiliationRO>();
        List<AffiliationVO> affiliationList = new ArrayList<AffiliationVO>();
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
                            itemControllerSessionBean.retrieveAffiliation(affiliationRefList.get(i).getObjectId()));
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
            affiliations.append(affiliationList.get(i).getName());
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
    	return CommonUtils.format(this.pubItem.getModificationDate());
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
     * Shows the given Message below the itemList after next Reload of the DepositorWS.
     * 
     * @param message the message to be displayed
     * @param keepMessage stores this message in SessionBean and displays it once (e.g. for a reload)
     */
    private void showMessageDepositorWS(String message)
    {
        message = this.getMessage(message);
        this.getItemListSessionBean().setMessage(message);
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
     * Returns a reference to the scoped data bean (the SubmitItemSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected SubmitItemSessionBean getSubmitItemSessionBean()
    {
        return (SubmitItemSessionBean)getSessionBean(SubmitItemSessionBean.class);
    }
    
    /**
     * Shows the given Message below the itemList after next Reload of the SerachResultList.
     * 
     * @param message the message to be displayed
     * @param keepMessage stores this message in SessionBean and displays it once (e.g. for a reload)
     */
    private void showMessageSearchResultList(String message)
    {
        message = this.getMessage(message);
        this.getItemListSessionBean().setMessage(message);
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
        return panelItemFull;
    }

    public void setPanelItemFull(HtmlPanelGroup panelItemFull)
    {
        this.panelItemFull = panelItemFull;
    }

    public HtmlMessages getValMessage()
    {
        return valMessage;
    }

    public void setValMessage(HtmlMessages valMessage)
    {
        this.valMessage = valMessage;
    }
    
    public PubItemVO getPubItem() {
		return pubItem;
	}

	public void setPubItem(PubItemVO pubItem) {
		this.pubItem = pubItem;
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
	   return citationURL;
	    
	}
	
	public ArrayList<String> getOrganizationArray() {
		return organizationArray;
	}

	public void setOrganizationArray(ArrayList<String> organizationArray) {
		this.organizationArray = organizationArray;
	}

	public ArrayList<ViewItemOrganization> getOrganizationList() {
		return organizationList;
	}

	public void setOrganizationList(ArrayList<ViewItemOrganization> organizationList) {
		this.organizationList = organizationList;
	}

	public List<OrganizationVO> getAffiliatedOrganizationsList() {
		return affiliatedOrganizationsList;
	}

	public void setAffiliatedOrganizationsList(
			List<OrganizationVO> affiliatedOrganizationsList) {
		this.affiliatedOrganizationsList = affiliatedOrganizationsList;
	}

	public ArrayList<String> getCreatorArray() {
		return creatorArray;
	}

	public void setCreatorArray(ArrayList<String> creatorArray) {
		this.creatorArray = creatorArray;
	}

	public ArrayList<ViewItemCreatorOrganization> getCreatorOrganizationsArray() {
		return creatorOrganizationsArray;
	}

	public void setCreatorOrganizationsArray(
			ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray) {
		this.creatorOrganizationsArray = creatorOrganizationsArray;
	}

	public UIXIterator getTitleIterator() {
		return titleIterator;
	}

	public void setTitleIterator(UIXIterator titleIterator) {
		this.titleIterator = titleIterator;
	}

	public UIXIterator getCreatorPersonsIterator() {
		return creatorPersonsIterator;
	}

	public void setCreatorPersonsIterator(UIXIterator creatorPersonsIterator) {
		this.creatorPersonsIterator = creatorPersonsIterator;
	}

	public UIXIterator getCreatorAffiliationsIterator() {
		return creatorAffiliationsIterator;
	}

	public void setCreatorAffiliationsIterator(
			UIXIterator creatorAffiliationsIterator) {
		this.creatorAffiliationsIterator = creatorAffiliationsIterator;
	}

	public UIXIterator getLanguagesIterator() {
		return languagesIterator;
	}

	public void setLanguagesIterator(UIXIterator languagesIterator) {
		this.languagesIterator = languagesIterator;
	}

	public UIXIterator getAbstractIterator() {
		return abstractIterator;
	}

	public void setAbstractIterator(UIXIterator abstractIterator) {
		this.abstractIterator = abstractIterator;
	}

	public UIXIterator getEventAltTitleIterator() {
		return eventAltTitleIterator;
	}

	public void setEventAltTitleIterator(UIXIterator eventAltTitleIterator) {
		this.eventAltTitleIterator = eventAltTitleIterator;
	}

	public UIXIterator getSourceIterator() {
		return sourceIterator;
	}

	public void setSourceIterator(UIXIterator sourceIterator) {
		this.sourceIterator = sourceIterator;
	}

	public UIXIterator getSourceTitleIterator() {
		return sourceTitleIterator;
	}

	public void setSourceTitleIterator(UIXIterator sourceTitleIterator) {
		this.sourceTitleIterator = sourceTitleIterator;
	}

	public UIXIterator getSourceCreatorPersonsIterator() {
		return sourceCreatorPersonsIterator;
	}

	public void setSourceCreatorPersonsIterator(
			UIXIterator sourceCreatorPersonsIterator) {
		this.sourceCreatorPersonsIterator = sourceCreatorPersonsIterator;
	}

	public UIXIterator getSourceCreatorAffiliationsIterator() {
		return sourceCreatorAffiliationsIterator;
	}

	public void setSourceCreatorAffiliationsIterator(
			UIXIterator sourceCreatorAffiliationsIterator) {
		this.sourceCreatorAffiliationsIterator = sourceCreatorAffiliationsIterator;
	}

	public List<SourceBean> getSourceList() {
		return sourceList;
	}

	public void setSourceList(List<SourceBean> sourceList) {
		this.sourceList = sourceList;
	}

	public UIXIterator getFileIterator() {
		return fileIterator;
	}

	public void setFileIterator(UIXIterator fileIterator) {
		this.fileIterator = fileIterator;
	}

	public List<FileBean> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileBean> fileList) {
		this.fileList = fileList;
	}

	public String getCoins() {
		return coins;
	}

	public void setCoins(String oinS) {
		this.coins = oinS;
	}

	public List<FileBean> getLocatorList() {
		return locatorList;
	}

	public void setLocatorList(List<FileBean> locatorList) {
		this.locatorList = locatorList;
	}

	public UIXIterator getLocatorIterator() {
		return locatorIterator;
	}

	public void setLocatorIterator(UIXIterator locatorIterator) {
		this.locatorIterator = locatorIterator;
	}

    public UIXIterator getCreatorOrganizationsIterator()
    {
        return creatorOrganizationsIterator;
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
        return isStateWithdrawn;
    }

    public void setStateWithdrawn(boolean isStateWithdrawn)
    {
        this.isStateWithdrawn = isStateWithdrawn;
    }

	public boolean getIsDepositor()
	{
		return isDepositor;
	}

	public void setDepositor(boolean isDepositor)
	{
		this.isDepositor = isDepositor;
	}

	public boolean getIsModerator()
	{
		return isModerator;
	}

	public void setModerator(boolean isModerator)
	{
		this.isModerator = isModerator;
	}

    public boolean getIsLoggedIn()
    {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn)
    {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean getIsLatestVersion()
    {
        return isLatestVersion;
    }

    public void setLatestVersion(boolean isLatestVersion)
    {
        this.isLatestVersion = isLatestVersion;
    }

    public boolean getIsLatestRelease()
    {
        return isLatestRelease;
    }

    public void setLatestRelease(boolean isLatestRelease)
    {
        this.isLatestRelease = isLatestRelease;
    }

    public boolean getIsStateSubmitted()
    {
        return isStateSubmitted;
    }

    public void setStateSubmitted(boolean isStateSubmitted)
    {
        this.isStateSubmitted = isStateSubmitted;
    }

    public boolean getIsStateReleased()
    {
        return isStateReleased;
    }

    public void setStateReleased(boolean isStateReleased)
    {
        this.isStateReleased = isStateReleased;
    }

    public boolean getIsStatePending()
    {
        return isStatePending;
    }

    public void setStatePending(boolean isStatePending)
    {
        this.isStatePending = isStatePending;
    }

    public boolean getIsOwner()
    {
        return isOwner;
    }

    public void setOwner(boolean isOwner)
    {
        this.isOwner = isOwner;
    }

    public boolean getIsModifyDisabled()
    {
        return isModifyDisabled;
    }

    public void setModifyDisabled(boolean isModifyDisabled)
    {
        this.isModifyDisabled = isModifyDisabled;
    }

    public boolean getIsCreateNewRevisionDisabled()
    {
        return isCreateNewRevisionDisabled;
    }

    public void setCreateNewRevisionDisabled(boolean isCreateNewRevisionDisabled)
    {
        this.isCreateNewRevisionDisabled = isCreateNewRevisionDisabled;
    }

    public boolean getIsFromEasySubmission()
    {
        if(getPubItem() instanceof PubItemVOPresentation)
        {
            return ((PubItemVOPresentation)getPubItem()).getIsFromEasySubmission();
        }
        else 
        {
            return false;
        }
    }

    public UIXIterator getSourceCreatorOrganizationsIterator()
    {
        return sourceCreatorOrganizationsIterator;
    }

    public void setSourceCreatorOrganizationsIterator(UIXIterator sourceCreatorOrganizationsIterator)
    {
        this.sourceCreatorOrganizationsIterator = sourceCreatorOrganizationsIterator;
    }
}