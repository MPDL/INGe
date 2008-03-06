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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.CommonSessionBean;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.RightsManagementSessionBean;
import de.mpg.escidoc.pubman.ViewItemRevisionsPage;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.collectionList.CollectionListSessionBean;
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
import de.mpg.escidoc.pubman.desktop.Login;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.releases.ItemVersionListSessionBean;
import de.mpg.escidoc.pubman.releases.ReleaseHistory;
import de.mpg.escidoc.pubman.revisions.CreateRevision;
import de.mpg.escidoc.pubman.revisions.RelationListSessionBean;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.search.SearchResultListSessionBean;
import de.mpg.escidoc.pubman.submitItem.SubmitItem;
import de.mpg.escidoc.pubman.submitItem.SubmitItemSessionBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.viewItem.ui.ViewItemFullUI;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItem;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItemSessionBean;
import de.mpg.escidoc.services.common.valueobjects.PubCollectionVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
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
    // Validation Service
    private ItemValidating itemValidating = null; 
    private PubItemVO pubItem = null;

    private HtmlMessages valMessage = new HtmlMessages();

    // Added by DiT: constant for the function modify and new revision to check the rights and/or if the function has to be disabled (DiT)
    private static final String FUNCTION_MODIFY = "modify";
    private static final String FUNCTION_NEW_REVISION = "new_revision";
    
    private static final String VALIDATION_ERROR_MESSAGE = "depositorWS_NotSuccessfullySubmitted";
    
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
                Login login = (Login)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "Login");
                login.forceLogout();
            }
        }
        else
        {
            this.pubItem = this.getItemControllerSessionBean().getCurrentPubItem();
        }
        
        if(this.pubItem != null)
        {
            LoginHelper loginHelper = (LoginHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver()
            .resolveVariable(FacesContext.getCurrentInstance(), "LoginHelper");
            
            //DiT: multiple new conditions for link-activation added
            boolean isModerator = loginHelper.getAccountUser().isModerator(this.pubItem.getPubCollection());
            boolean isDepositor = loginHelper.getAccountUser().isDepositor();
            boolean isOwner = (loginHelper.getAccountUser().getReference() != null ? loginHelper.getAccountUser().getReference().getObjectId().equals(this.pubItem.getOwner().getObjectId()) : false);
            boolean isModifyDisabled = this.getRightsManagementSessionBean().isDisabled(RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "." + ViewItemFull.FUNCTION_MODIFY);
            boolean isCreateNewRevisionDisabled = this.getRightsManagementSessionBean().isDisabled(RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "." + ViewItemFull.FUNCTION_NEW_REVISION);
            
            // enable or disable the action links according to the login state
            if (loginHelper.getESciDocUserHandle() != null)
            {
                this.getViewItemSessionBean().getLnkNewSubmission().setRendered(true);
                this.getViewItemSessionBean().getLnkEdit().setRendered(true);
                this.getViewItemSessionBean().getLnkSubmit().setRendered(true);
                this.getViewItemSessionBean().getLnkDelete().setRendered(true);
                this.getViewItemSessionBean().getLnkWithdraw().setRendered(isOwner);
                this.getViewItemSessionBean().getLnkModify().setRendered(!isModifyDisabled && isModerator);
                this.getViewItemSessionBean().getLnkCreateNewRevision().setRendered(!isCreateNewRevisionDisabled && isDepositor);
            }
            else
            {
                this.getViewItemSessionBean().getLnkNewSubmission().setRendered(false);
                this.getViewItemSessionBean().getLnkEdit().setRendered(false);
                this.getViewItemSessionBean().getLnkSubmit().setRendered(false);
                this.getViewItemSessionBean().getLnkDelete().setRendered(false);
                this.getViewItemSessionBean().getLnkWithdraw().setRendered(false);
                this.getViewItemSessionBean().getLnkModify().setRendered(false);
                this.getViewItemSessionBean().getLnkCreateNewRevision().setRendered(false);
            }
            
            // set the action links in the action menu according to the item state
            if (this.pubItem.getState().toString().equals(PubItemVO.State.RELEASED.toString()))
            {
                this.getViewItemSessionBean().getLnkDelete().setRendered(false);
                this.getViewItemSessionBean().getLnkEdit().setRendered(false);
                this.getViewItemSessionBean().getLnkSubmit().setRendered(false);
                if (loginHelper.getESciDocUserHandle() != null)
                {
                    this.getViewItemSessionBean().getLnkWithdraw().setRendered(isOwner);
                    this.getViewItemSessionBean().getLnkModify().setRendered(!isModifyDisabled && isModerator);
                    this.getViewItemSessionBean().getLnkCreateNewRevision().setRendered(!isCreateNewRevisionDisabled && isDepositor);
                }
                else
                {
                    this.getViewItemSessionBean().getLnkWithdraw().setRendered(false);
                    this.getViewItemSessionBean().getLnkModify().setRendered(false);
                    this.getViewItemSessionBean().getLnkCreateNewRevision().setRendered(false);
                }
            }
            else if (this.pubItem.getState().toString().equals(PubItemVO.State.SUBMITTED.toString())
                    || this.pubItem.getState().toString().equals(PubItemVO.State.WITHDRAWN.toString()))
            {
                this.getViewItemSessionBean().getLnkDelete().setRendered(false);
                this.getViewItemSessionBean().getLnkEdit().setRendered(false);
                this.getViewItemSessionBean().getLnkSubmit().setRendered(false);
                this.getViewItemSessionBean().getLnkWithdraw().setRendered(false);
                this.getViewItemSessionBean().getLnkModify().setRendered(false);
                this.getViewItemSessionBean().getLnkCreateNewRevision().setRendered(false);
            }
            else
            {
                this.getViewItemSessionBean().getLnkDelete().setRendered(true);
                this.getViewItemSessionBean().getLnkEdit().setRendered(true);
                this.getViewItemSessionBean().getLnkSubmit().setRendered(true);
                this.getViewItemSessionBean().getLnkWithdraw().setRendered(false);
                this.getViewItemSessionBean().getLnkModify().setRendered(false);
                this.getViewItemSessionBean().getLnkCreateNewRevision().setRendered(false);
            }
            
            // set the action links in the action menu according to the item version state 
            // (disable all links except 'create new revision' if the item is not the last version)
            // TODO ScT: Vergleich zwischen aktueller Versionsnummer und getLastVersionNumber() einbauen, wenn common_logic 0.15.9 da ist!
            
            if(this.pubItem.getReference().getVersionNumber() != this.pubItem.getLatestVersionNumber())
            {
                this.getViewItemSessionBean().getLnkDelete().setRendered(false);
                this.getViewItemSessionBean().getLnkEdit().setRendered(false);
                this.getViewItemSessionBean().getLnkSubmit().setRendered(false);
                this.getViewItemSessionBean().getLnkWithdraw().setRendered(false);
                this.getViewItemSessionBean().getLnkModify().setRendered(false);
                this.getViewItemSessionBean().getLnkCreateNewRevision().setRendered(true);
            }
            
            
            // create a full view item UI
            createViewItemFull(this.pubItem);
            
            // TODO ScT: remove this and related methods when the procedure of handling release history button is fully clarified
            // set up the release history of the item
            //createReleaseHistory();
            
            // redirect if necessary
            if(this.getViewItemSessionBean().isHasBeenRedirected() == false)
            {
                this.getViewItemSessionBean().setHasBeenRedirected(true);
                try
                {
                    if(this.getSessionBean().isRunAsGUITool())
                    {
                    	fc.getExternalContext().redirect("GTViewItemFullPage.jsp?itemId=" + this.pubItem.getReference().getObjectId()+":"+ this.pubItem.getReference().getVersionNumber());
                    }
                    else
                    {
                    	fc.getExternalContext().redirect("viewItemFullPage.jsp?itemId=" + this.pubItem.getReference().getObjectId()+":"+ this.pubItem.getReference().getVersionNumber());
                    }
                }
                catch (IOException e)
                {
                    logger.error(e);
                }
            }
        }
    }

    /**
     * Creates a full view item UI
     */
    public void createViewItemFull(PubItemVO pubItem)
    {
        this.panelItemFull.getChildren().clear();
        if (pubItem != null)
        {
            this.panelItemFull.getChildren().add(new ViewItemFullUI(pubItem));
        }
    }
    
    /**
     * Prepares the file the user wants to download
     * @author Tobias Schraut
     * @throws IOException
     * @throws Exception
     */
    public void downloadFile(int itemPosition, int filePosition) throws IOException, Exception
    {
        LoginHelper loginHelper = (LoginHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "LoginHelper");

        // extract the location of the file
        String fileLocation = ServiceLocator.getFrameworkUrl() + this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(filePosition).getContent();
        String filename = this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(filePosition).getName(); // Filename suggested in browser Save As dialog
        filename = filename.replace(" ", "_"); // replace empty spaces because they cannot be procesed by the http-response (filename will be cutted after the first empty space)
        String contentType = this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(filePosition).getMimeType(); // For dialog, try
        
        // application/x-download
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
        response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setContentLength(new Long(this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(filePosition).getSize()).intValue());
        response.setContentType(contentType);

        byte[] buffer = null;
        if (filePosition != -1)
        {
            try
            {
                GetMethod method = new GetMethod(fileLocation);
                method.setFollowRedirects(false);
                if (loginHelper.getESciDocUserHandle() != null)
                {
                    // downloading by account user
                    addHandleToMethod(method, loginHelper.getESciDocUserHandle());
                }
                
                // Execute the method with HttpClient.
                HttpClient client = new HttpClient();
                client.executeMethod(method);
                OutputStream out = response.getOutputStream();
                InputStream input = method.getResponseBodyAsStream();
                try
                {
                    buffer = new byte[new Long(this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(filePosition).getSize()).intValue()];
                    int numRead;
                    long numWritten = 0;
                    while ((numRead = input.read(buffer)) != -1) {
                        out.write(buffer, 0, numRead);
                        out.flush();
                        numWritten += numRead;
                    }
                    facesContext.responseComplete();
                }
                catch (IOException e1)
                {
                    logger.debug("Download IO Error: " + e1.toString());
                }
                input.close();
                out.close();
            }
            catch (FileNotFoundException e)
            {
                logger.debug("File not found: " + e.toString());
            }
        }
    }

    /**
     * Method is called in jsp. Triggers download action
     * @author Tobias Schraut
     * @return String nav rule
     * @throws Exception
     */
    public String handleDownloadAction(ActionEvent event) throws Exception
    {
        //find the index of the the button the user has clicked
        HtmlOutputText result = (HtmlOutputText)((HtmlCommandButton)event.getSource()).getParent();
        int indexButton = result.getChildren().indexOf(event.getSource());
        
        // then find the indexes of the item and the file the clicked button belongs to
        int indexFile = new Integer(((HtmlOutputText)result.getChildren().get(indexButton-1)).getValue().toString());
        int indexItem = new Integer(((HtmlOutputText)result.getChildren().get(indexButton-2)).getValue().toString());
        try
        {
            this.downloadFile(indexItem, indexFile);
        }
        catch (IOException e)
        {
            logger.debug("File Download Error: " + e.toString());
        }
        
        return "";
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
     * Changed by DiT, 29.11.2007: only show collections when user has privileges for more than one collection
     * 
     * @return Sring nav rule to load the create new revision page
     */
    public String createNewRevision()
    {
        // Changed by DiT, 29.11.2007: only show collections when user has privileges for more than one collection
        // if there is only one collection for this user we can skip the CreateItem-Dialog and create the new item directly
        if (this.getCollectionListSessionBean().getCollectionList().size() == 0)
        {
            logger.warn("The user does not have privileges for any collection.");
            return null;
        }
        if (this.getCollectionListSessionBean().getCollectionList().size() == 1)
        {            
            PubCollectionVO pubCollectionVO = this.getCollectionListSessionBean().getCollectionList().get(0);
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has only privileges for one collection (ID: " 
                        + pubCollectionVO.getReference().getObjectId() + ")");
            }
            
            return this.getItemControllerSessionBean().createNewRevision(EditItem.LOAD_EDITITEM, pubCollectionVO.getReference(), this.pubItem, null);
        }
        else
        {
            // more than one collection exists for this user; let him choose the right one
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has privileges for " + this.getCollectionListSessionBean().getCollectionList().size() 
                        + " different collections.");
            }

            this.getRelationListSessionBean().setPubItemVO(this.getItemControllerSessionBean().getCurrentPubItem());
            
            return CreateRevision.LOAD_CREATEREVISION;
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
        try
        {
        	this.getRelationListSessionBean().setRelationList(this.getItemControllerSessionBean().retrieveRevisions(this.getItemControllerSessionBean().getCurrentPubItem()));
        }
        catch (Exception e) {
			logger.error("Error setting revision list", e);
		}
        return ViewItemRevisionsPage.LOAD_VIEWREVISIONS;
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
        this.getItemVersionListSessionBean().setVersionList(null);
        
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
     * Returns the CollectionListSessionBean.
     * 
     * @return a reference to the scoped data bean (CollectionListSessionBean)
     */
    protected CollectionListSessionBean getCollectionListSessionBean()
    {
        return (CollectionListSessionBean)getSessionBean(CollectionListSessionBean.class);
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
    
}