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

package de.mpg.escidoc.pubman.editItem;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.core.data.CoreTable;
import org.apache.myfaces.trinidad.model.UploadedFile;

import de.mpg.escidoc.pubman.EditItemPage;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.acceptItem.AcceptItem;
import de.mpg.escidoc.pubman.acceptItem.AcceptItemSessionBean;
import de.mpg.escidoc.pubman.affiliation.AffiliationSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
import de.mpg.escidoc.pubman.editItem.bean.ContentAbstractCollection;
import de.mpg.escidoc.pubman.editItem.bean.CreatorCollection;
import de.mpg.escidoc.pubman.editItem.bean.IdentifierCollection;
import de.mpg.escidoc.pubman.editItem.bean.SourceCollection;
import de.mpg.escidoc.pubman.editItem.bean.TitleCollection;
import de.mpg.escidoc.pubman.home.Home;
import de.mpg.escidoc.pubman.submitItem.SubmitItem;
import de.mpg.escidoc.pubman.submitItem.SubmitItemSessionBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.ListItem;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AdminDescriptorVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.FormatVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * Fragment class for editing PubItems. This class provides all functionality for editing, saving and submitting a
 * PubItem including methods for depending dynamic UI components.
 *
 * @author: Thomas Diebäcker, created 10.01.2007
 * @version: $Revision: 1691 $ $LastChangedDate: 2007-12-18 09:30:58 +0100 (Di, 18 Dez 2007) $
 * Revised by DiT: 09.08.2007
 */
public class EditItem extends FacesBean
{
    public static final String BEAN_NAME = "EditItem";
    private static Logger logger = Logger.getLogger(EditItem.class);

    // Faces navigation string
    public final static String LOAD_EDITITEM = "loadEditItem";

    // Constants for value bindings
    public final static String VALUE_BINDING_PUBITEM_METADATA = "EditItem.pubItem.metadata";
    public final static String VALUE_BINDING_PUBITEM_METADATA_CREATORS = "EditItem.pubItem.metadata.creators";
    public final static String VALUE_BINDING_PUBITEM_METADATA_EVENT = "EditItem.pubItem.metadata.event";
    public final static String VALUE_BINDING_PUBITEM_METADATA_IDENTIFIERS = "EditItem.pubItem.metadata.identifiers";

    // Constants for validation points
    public final static String VALIDATIONPOINT_SUBMIT = "submit_item";
    public final static String VALIDATIONPOINT_ACCEPT = "accept_item";

    // Validation Service
    private ItemValidating itemValidating = null;

    private HtmlMessages valMessage = new HtmlMessages();

    // bindings
    private HtmlCommandLink lnkSave = new HtmlCommandLink();
    private HtmlCommandLink lnkSaveAndSubmit = new HtmlCommandLink();
    private HtmlCommandLink lnkDelete = new HtmlCommandLink();
    private HtmlCommandLink lnkAccept = new HtmlCommandLink();
    private HtmlCommandLink lnkRelease = new HtmlCommandLink();

    // panels for dynamic components
    private HtmlPanelGrid panDynamicFile = new HtmlPanelGrid();

    /** pub context name. */
    private String contextName = null;

//  FIXME delegated internal collections
    private TitleCollection titleCollection;
    private TitleCollection eventTitleCollection;
    private ContentAbstractCollection contentAbstractCollection;
    private CreatorCollection creatorCollection;
    private IdentifierCollection identifierCollection;
    private SourceCollection sourceCollection;

    private List<ListItem> languages = null;
    
    private UploadedFile uploadedFile;
    
    private CoreTable fileTable = new CoreTable();
    
    PubItemVO item = null;
    
    private boolean fromEasySubmission = false;
    private PubItemDepositing pubItemDepositing;
    
    /**
     * Public constructor.
     */
    public EditItem()
    {
        try
        {
            InitialContext initialContext = new InitialContext();
            this.itemValidating = (ItemValidating) initialContext.lookup(ItemValidating.SERVICE_NAME);
            this.pubItemDepositing = (PubItemDepositing) initialContext.lookup(PubItemDepositing.SERVICE_NAME);
        }
        catch (NamingException ne)
        {
            throw new RuntimeException("Validation service not initialized", ne);
        }

        this.init();

    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        
        this.fileTable = new CoreTable();
        
        Map map = FacesContext.getCurrentInstance().getExternalContext().getInitParameterMap();

        // enables the commandlinks
        this.enableLinks();

        // initializes the (new) item if necessary
        this.initializeItem();

//      FIXME provide access to parts of my VO to specialized POJO's
        titleCollection = new TitleCollection(this.getPubItem().getMetadata());
        eventTitleCollection = new TitleCollection(this.getPubItem().getMetadata().getEvent());
        contentAbstractCollection = new ContentAbstractCollection(this.getPubItem().getMetadata().getAbstracts());
        creatorCollection = new CreatorCollection(this.getPubItem().getMetadata().getCreators());
        identifierCollection = new IdentifierCollection(this.getPubItem().getMetadata().getIdentifiers());
        sourceCollection = new SourceCollection(this.getPubItem().getMetadata().getSources());

        if (logger.isDebugEnabled())
        {
            if (this.getPubItem() != null && this.getPubItem().getVersion() != null)
            {
                logger.debug("Item that is being edited: " + this.getPubItem().getVersion().getObjectId());
            }
            else
            {
                logger.debug("Editing a new item.");
            }
        }
        this.getAffiliationSessionBean().setBrowseByAffiliation(true);

        // fetch the name of the pub context
        this.contextName = this.getContextName();
        
        
    }

    /**
     * Delivers a reference to the currently edited item.
     * This is a shortCut for the method in the ItemController.
     * @return the item that is currently edited
     */
    public PubItemVO getPubItem()
    {
    	
        if (item == null)
        {
            item = this.getItemControllerSessionBean().getCurrentPubItem();
        }
        return item;
    }

    public String getContextName()
    {
    	if (contextName == null)
    	{
	        try
	        {
	            ContextVO context = this.getItemControllerSessionBean().retrieveContext(
	                     this.getPubItem().getContext().getObjectId());
	            return context.getName();
	        }
	        catch (Exception e)
	        {
	            logger.error("Could not retrieve the requested context." + "\n" + e.toString());
	            ((ErrorPage) getSessionBean(ErrorPage.class)).setException(e);
	            return ErrorPage.LOAD_ERRORPAGE;
	        }
    	}
    	return contextName;

    }

    /**
     * Adds sub-ValueObjects to an item initially to be able to bind uiComponents to them.
     */
    private void initializeItem()
    {

        // get the item that is currently edited
        PubItemVO pubItem = this.getPubItem();
        
        EditItemSessionBean eisb = this.getEditItemSessionBean();

        if (pubItem != null)
        {
        	this.getItemControllerSessionBean().initializeItem(pubItem);
        
            if(this.getEditItemSessionBean().getFiles().size() == 0 || this.getEditItemSessionBean().getLocators().size() == 0)
            {
            	bindFiles();
            }
        }
        else
        {
            logger.warn("Current PubItem is NULL!");
        }
       
    }

    private void bindFiles()
    {
    	List<PubFileVOPresentation> files = new ArrayList<PubFileVOPresentation>();
    	List<PubFileVOPresentation> locators = new ArrayList<PubFileVOPresentation>();
    	int fileCount = 0;
    	int locatorCount = 0;
    	// add files
    	for (int i = 0; i < this.item.getFiles().size(); i++)
    	{
			if(this.item.getFiles().get(i).getStorage().equals(FileVO.Storage.INTERNAL_MANAGED))
			{
				PubFileVOPresentation filepres = new PubFileVOPresentation(fileCount, this.item.getFiles().get(i),false);
				files.add(filepres);
				fileCount ++;
			}
		}
    	this.getEditItemSessionBean().setFiles(files);
    	
    	// add locators
    	for (int i = 0; i < this.item.getFiles().size(); i++)
    	{
			if(this.item.getFiles().get(i).getStorage().equals(FileVO.Storage.EXTERNAL_URL))
			{
				PubFileVOPresentation locatorpres = new PubFileVOPresentation(locatorCount, this.item.getFiles().get(i), true);
				locators.add(locatorpres);
				locatorCount ++;
			}
		}
    	this.getEditItemSessionBean().setLocators(locators);
    	
    	// make sure that at least one locator and one file is stored in the  EditItemSessionBean
    	if(this.getEditItemSessionBean().getFiles().size() < 1)
    	{
    	    FileVO newFile = new FileVO();
    	    newFile.getMetadataSets().add(new MdsFileVO());
    	    newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
    		this.getEditItemSessionBean().getFiles().add(new PubFileVOPresentation(0, newFile, false));
    	}
    	if(this.getEditItemSessionBean().getLocators().size() < 1)
    	{
    		FileVO newLocator = new FileVO();
    		newLocator.getMetadataSets().add(new MdsFileVO());
    		newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
    		this.getEditItemSessionBean().getLocators().add(new PubFileVOPresentation(0, newLocator, true));
    	}
    }
    
    /**
     * This method reorganizes the index property in PubFileVOPresentation after removing one element of the list.
     */
    public void reorganizeFileIndexes()
    {
    	if(this.getEditItemSessionBean().getFiles() != null)
    	{
    		for(int i = 0; i < this.getEditItemSessionBean().getFiles().size(); i++)
        	{
        		this.getEditItemSessionBean().getFiles().get(i).setIndex(i);
        	}
    	}
    }
    
    /**
     * This method reorganizes the index property in PubFileVOPresentation after removing one element of the list.
     */
    public void reorganizeLocatorIndexes()
    {
    	if(this.getEditItemSessionBean().getLocators() != null)
    	{
    		for(int i = 0; i < this.getEditItemSessionBean().getLocators().size(); i++)
        	{
        		this.getEditItemSessionBean().getLocators().get(i).setIndex(i);
        	}
    	}
    }
    
    /**
     * This method binds the uploaded files and locators to the files in the PubItem during the save process
     */
    private void bindUploadedFilesAndLocators()
    {
    	// first clear the file list
    	this.getPubItem().getFiles().clear();
		// add the files
    	if(this.getFiles() != null && this.getFiles().size() > 0)
    	{
    		for(int i = 0; i < this.getFiles().size(); i++)
    		{
    			this.getPubItem().getFiles().add(this.getFiles().get(i).getFile());
    		}
    	}
    	// add the locators
		if(this.getLocators() != null && this.getLocators().size() > 0)
    	{
    		for(int i = 0; i < this.getLocators().size(); i++)
    		{
    		    
    		    //add name from content if not available
    		    if(this.getLocators().get(i).getFile().getDefaultMetadata().getTitle() == null || this.getLocators().get(i).getFile().getDefaultMetadata().getTitle().getValue() == null || this.getLocators().get(i).getFile().getDefaultMetadata().getTitle().getValue().trim().equals(""))
                {
                    this.getLocators().get(i).getFile().getDefaultMetadata().setTitle(new TextVO(this.getEditItemSessionBean().getLocators().get(i).getFile().getContent()));
                    //this.getEditItemSessionBean().getLocators().get(this.getEditItemSessionBean().getLocators().size()-1).getFile().setName(this.getEditItemSessionBean().getLocators().get(this.getEditItemSessionBean().getLocators().size()-1).getFile().getContent());
                }
                
    			this.getPubItem().getFiles().add(this.getLocators().get(i).getFile());
    		}
    	}
		// finally clean the session bean
		/*this.getEditItemSessionBean().getFiles().clear();
		this.getEditItemSessionBean().getLocators().clear();*/
    }
    
    /**
     * Returns a reference to the scoped data bean (the SubmitItemSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected SubmitItemSessionBean getSubmitItemSessionBean()
    {
        return (SubmitItemSessionBean)getBean(SubmitItemSessionBean.class);
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
     * Uploads a file to the staging servlet and returns the corresponding URL.
     * 
     * @param uploadedFile The file to upload
     * @param mimetype The mimetype of the file
     * @param userHandle The userhandle to use for upload
     * @return The URL of the uploaded file.
     * @throws Exception If anything goes wrong...
     */
    protected URL uploadFile(UploadedFile uploadedFile, String mimetype, String userHandle) throws Exception
    {
        // Prepare the HttpMethod.
        String fwUrl = de.mpg.escidoc.services.framework.ServiceLocator.getFrameworkUrl();
        PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(uploadedFile.getInputStream()));
        method.setRequestHeader("Content-Type", mimetype);
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);

        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        String response = method.getResponseBodyAsString();
        InitialContext context = new InitialContext();
        XmlTransforming ctransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
        return ctransforming.transformUploadResponseToFileURL(response);        
    }

    public String addLanguage()
    {
    	getPubItem().getMetadata().getLanguages().add("");
    	return null;
    }

    public String removeLanguage()
    {
    	getPubItem().getMetadata().getLanguages().remove(getPubItem().getMetadata().getLanguages().size() - 1);
    	return null;
    }
    
    public boolean getRenderRemoveLanguage()
    {
    	return (getPubItem().getMetadata().getLanguages().size() > 1);
    }

    public List<ListItem> getLanguages()
    {
    	if (languages == null)
    	{
    		languages = new ArrayList<ListItem>();
    		if (getPubItem().getMetadata().getLanguages().size() == 0)
    		{
    			getPubItem().getMetadata().getLanguages().add("");
    		}
    		int counter = 0;
    		for (Iterator<String> iterator = getPubItem().getMetadata().getLanguages().iterator(); iterator.hasNext();) {
    			String value = (String) iterator.next();
    			ListItem item = new ListItem();
    			item.setValue(value);
    			item.setIndex(counter++);
    			item.setStringList(getPubItem().getMetadata().getLanguages());
    			item.setItemList(languages);
    			languages.add(item);
    		}
    	}
    	return languages;
    }
    
	public SelectItem[] getLanguageOptions()
    {
    	return CommonUtils.getLanguageOptions();
    }
	
	/**
     * Validates the item.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String validate()
    {
        try
        {
            bindUploadedFilesAndLocators();
            PubItemVO item = this.getPubItem();
            this.getItemControllerSessionBean().validate(item, EditItem.VALIDATIONPOINT_SUBMIT);
            if (this.getItemControllerSessionBean().getCurrentItemValidationReport().hasItems())
            {
                this.showValidationMessages(
                        this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else
            {
                String message = getMessage("itemIsValid");
                info(message);
                valMessage.setRendered(true);
            }
        }
        catch (Exception e)
        {
            logger.error("Could not validate item." + "\n" + e.toString(), e);
            ((ErrorPage)getBean(ErrorPage.class)).setException(e);

            return ErrorPage.LOAD_ERRORPAGE;
        }
        return null;
    }

    /**
     * Saves the item.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String save()
    {
    	EditItemSessionBean eisb = this.getEditItemSessionBean();
    	ItemControllerSessionBean icsb = this.getItemControllerSessionBean();
    	// bind the temporary uploaded files to the files in the current item
    	bindUploadedFilesAndLocators();
    	
    	/*
         * FrM: Validation with validation point "default"
         */
        ValidationReportVO report = null;
        try
        {
        	PubItemVO itemVO = new PubItemVO(getPubItem());
            report = this.itemValidating.validateItemObject(itemVO, "default");
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
                logger.debug("Saving item...");
            }

            //String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(DepositorWS.LOAD_DEPOSITORWS, false); 
            this.getItemListSessionBean().setListDirty(true);
            String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false);

            if (retVal == null)
            {
                this.showValidationMessages(
                        this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else if(ViewItemFull.LOAD_VIEWITEM.equals(retVal))
            {
             // redirect to the view item page afterwards (if no error occured)
                try 
                {
                    FacesContext fc = FacesContext.getCurrentInstance();
                    HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
                    if (isFromEasySubmission())
                    {
                        fc.getExternalContext().redirect(request.getContextPath() + "/faces/viewItemFullPage.jsp?itemId=" + this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectId()+"&fromEasySub=true");
                    }
                    else
                    {
                        fc.getExternalContext().redirect(request.getContextPath() + "/faces/viewItemFullPage.jsp?itemId=" + this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectId()); 
                    }
                } 
                catch (IOException e) {
                    logger.error("Could not redirect to View Item Page", e);
                }
            }
            return retVal;
        }
        else if (report.isValid())
        {
            // TODO FrM: Informative messages
            //String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(DepositorWS.LOAD_DEPOSITORWS, false);
        	this.getItemListSessionBean().setListDirty(true);
        	String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false);

            if (retVal == null)
            {
                this.showValidationMessages(
                        this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else if(ViewItemFull.LOAD_VIEWITEM.equals(retVal))
            {
             // redirect to the view item page afterwards (if no error occured)
                try 
                {
                    FacesContext fc = FacesContext.getCurrentInstance();
                    HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
                    if (isFromEasySubmission())
                    {
                        fc.getExternalContext().redirect(request.getContextPath() + "/faces/viewItemFullPage.jsp?itemId=" + this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectId()+"&fromEasySub=true");
                    }
                    else
                    {
                        fc.getExternalContext().redirect(request.getContextPath() + "/faces/viewItemFullPage.jsp?itemId=" + this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectId()); 
                    }
                } 
                catch (IOException e) {
                    logger.error("Could not redirect to View Item Page", e);
                }
            }

            return retVal;
        }
        else
        {           
            // Item is invalid, do not submit anything.
            this.showValidationMessages(report);
            return null;
        }        
    }

    /**
     * Saves the item, even if there are informative validation messages.
     * @author Michael Franke
     * @return string, identifying the page that should be navigated to after this methodcall  
     */
    public String saveAnyway()
    {        
        String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(DepositorWS.LOAD_DEPOSITORWS, true);
        
        if (!(this.getItemControllerSessionBean().getCurrentItemValidationReport().isValid())) 
        {
            this.showValidationMessages(this.getItemControllerSessionBean().getCurrentItemValidationReport());
        }
        else if (retVal != null && retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        {
            this.showMessage(DepositorWS.MESSAGE_SUCCESSFULLY_SAVED);
        }
        
        // initialize viewItem
        /* this is only neccessary if viewItem should be called after saving; this should stay here as a comment if this might come back once...
        de.mpg.escidoc.pubman.viewItem.ViewItem viewItem = (de.mpg.escidoc.pubman.viewItem.ViewItem)this.application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "ViewItem");
        viewItem.setNavigationStringToGoBack(DepositorWS.LOAD_DEPOSITORWS);
        viewItem.loadItem();
        */

        return retVal;
    }

    /**
     * Submits the item. Should not be used at the moment.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String submit()
    {
        logger.error("This is a call to \"EditItem.submit\" which should not happen.");
        
        String retVal = this.getItemControllerSessionBean().submitOrReleaseCurrentPubItem("", DepositorWS.LOAD_DEPOSITORWS); 

        if (retVal == null)
        {
            this.showValidationMessages(
                    this.getItemControllerSessionBean().getCurrentItemValidationReport());
        }
        else if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        {
            this.showMessage(DepositorWS.MESSAGE_SUCCESSFULLY_SUBMITTED);
        }
        
        return retVal;
    }

    /**
     * Saves and submits an item.
     * @return string, identifying the page that should be navigated to after this methodcall
     * Changed by FrM: Inserted validation and call to "enter submission comment" page.
     * 
     */
    public String saveAndSubmit()
    {
        /*
         * FrM: Validation with validation point "submit_item"
         */
        
    	// bind the temporary uploaded files to the files in the current item
    	bindUploadedFilesAndLocators();
    	
        ValidationReportVO report = null;
        try
        {
        	PubItemVO item = new PubItemVO(getPubItem());
        	
            report = this.itemValidating.validateItemObject(item, "submit_item");
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
            
            String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(SubmitItem.LOAD_SUBMITITEM, false); 

            if (retVal == null)
            {
                this.showValidationMessages(
                        this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
            {
                getSubmitItemSessionBean().setNavigationStringToGoBack(DepositorWS.LOAD_DEPOSITORWS);
                String localMessage = getMessage(DepositorWS.MESSAGE_SUCCESSFULLY_SAVED);
                getSubmitItemSessionBean().setMessage(localMessage);
            }
            return retVal;
        }
        else if (report.isValid())
        {
            // TODO FrM: Informative messages
            String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(SubmitItem.LOAD_SUBMITITEM, false); 

            if (retVal == null)
            {
                this.showValidationMessages(
                        this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
            {
                getSubmitItemSessionBean().setNavigationStringToGoBack(DepositorWS.LOAD_DEPOSITORWS);
                String localMessage = getMessage(DepositorWS.MESSAGE_SUCCESSFULLY_SAVED);
                getSubmitItemSessionBean().setMessage(localMessage);
            }
            return retVal;
        }
        else
        {           
            // Item is invalid, do not submit anything.
            this.showValidationMessages(report);
            return null;
        }        

    }

    /**
     * Deletes the current item.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String delete()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Deleting current item...");
        }

        // delete the currently edited item
        String retVal = this.getItemControllerSessionBean().deleteCurrentPubItem(DepositorWS.LOAD_DEPOSITORWS);
        
        // show message in DepositorWS
        if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        {
            this.showMessage(DepositorWS.MESSAGE_SUCCESSFULLY_DELETED);
        }

        return retVal;
    }

    /**
     * Cancels the editing.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String cancel()
    {
    	// examine if the user came from the view Item Page or if he started a new submission
    	String navString = "";
    	if (this.getPubItem() != null && this.getPubItem().getVersion() != null)
        {
            navString = ViewItemFull.LOAD_VIEWITEM;
        }
        else
        {
        	navString = Home.LOAD_HOME;
        }
    	cleanEditItem();
    	
    	if (navString.equals(ViewItemFull.LOAD_VIEWITEM))
    	{
    	    try 
            {
    	        EditItemPage editItemPage = (EditItemPage) getRequestBean(EditItemPage.class); 
                FacesContext fc = FacesContext.getCurrentInstance();
                HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
                fc.getExternalContext().redirect(request.getContextPath() + "/faces/" + editItemPage.getPreviousPageURI());
            } 
            catch (IOException e) {
                logger.error("Could not redirect to View Item Page", e);
            }
    	    
    	}
    	
    	return navString;
    }
    
    /**
     * This method cleans up all the helping constructs like collections etc.
     */
    private void cleanEditItem()
    {
    	item = null;
    	titleCollection = null;
        eventTitleCollection = null;
        contentAbstractCollection = null;
        creatorCollection = null;
        identifierCollection = null;
        sourceCollection = null;
        languages = null;
        uploadedFile = null;
        fileTable = null;
    }
    
    

    /**
     * Saves and accepts an item.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String saveAndAccept()
    {
        /*
         * Copied by DiT from saveAndSubmit(), written by FrM: Validation with validation point "accept_item"
         */
        
    	// bind the temporary uploaded files to the files in the current item
    	bindUploadedFilesAndLocators();
    	
        ValidationReportVO report = null;

        try
        {
            report = this.itemValidating.validateItemObject(getPubItem(), EditItem.VALIDATIONPOINT_ACCEPT);
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
            
            // check if the item has been changed
            PubItemVO newPubItem = this.getItemControllerSessionBean().getCurrentPubItem();
            PubItemVO oldPubItem = null;
            try
            {
                oldPubItem = this.getItemControllerSessionBean().retrieveItem(newPubItem.getVersion().getObjectId());
            }
            catch (Exception e)
            {
                logger.error("Could not retrieve item." + "\n" + e.toString(), e);
                ((ErrorPage)getBean(ErrorPage.class)).setException(e);
                
                return ErrorPage.LOAD_ERRORPAGE;
            }
            
            
            if (!this.getItemControllerSessionBean().hasChanged(oldPubItem, newPubItem))
            {
                logger.warn("Item has not been changed.");

                // create a validation report
                ValidationReportVO changedReport = new ValidationReportVO();
                ValidationReportItemVO changedReportItem = new ValidationReportItemVO();
                changedReportItem.setInfoLevel(ValidationReportItemVO.InfoLevel.RESTRICTIVE);
                changedReportItem.setContent("itemHasNotBeenChanged");                                
                changedReport.addItem(changedReportItem);
                
                // show report and stay on this page
                this.showValidationMessages(changedReport);                
                return null;
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Item was changed.");
                }
            }
            
            String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(AcceptItem.LOAD_ACCEPTITEM, false);

            if (retVal == null)
            {
                this.showValidationMessages(
                        this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
            {
                getAcceptItemSessionBean().setNavigationStringToGoBack(ViewItemFull.LOAD_VIEWITEM);
                String localMessage = getMessage(DepositorWS.MESSAGE_SUCCESSFULLY_SAVED);
                getAcceptItemSessionBean().setMessage(localMessage);
            }
            
            return retVal;
        }
        else if (report.isValid())
        {
            // TODO FrM: Informative messages
            String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(AcceptItem.LOAD_ACCEPTITEM, false); 

            if (retVal == null)
            {
                this.showValidationMessages(
                        this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
            {
                getAcceptItemSessionBean().setNavigationStringToGoBack(ViewItemFull.LOAD_VIEWITEM);
                String localMessage = getMessage(DepositorWS.MESSAGE_SUCCESSFULLY_ACCEPTED);
                getAcceptItemSessionBean().setMessage(localMessage);
            }
            return retVal;
        }
        else
        {           
            // Item is invalid, do not accept anything.
            this.showValidationMessages(report);
            return null;
        }        
    }
    
    public String uploadFile(UploadedFile file)
    {
    	String contentURL = "";
    	if (file != null)
          {  
              try
              {
                  // upload the file
                  LoginHelper loginHelper = (LoginHelper)this.getBean(LoginHelper.class);
                  URL url = this.uploadFile(file, file.getContentType(), loginHelper.getESciDocUserHandle());
                  if(url != null)
                  {
                	  contentURL = url.toString();
                  }
               
              }
              catch (Exception e)
              {
                  logger.error("Could not upload file." + "\n" + e.toString());
                  ((ErrorPage)this.getBean(ErrorPage.class)).setException(e);
  
                  // force JSF to load the ErrorPage
                  try
                  {
                      FacesContext.getCurrentInstance().getExternalContext().redirect("ErrorPage.jsp");
                  }
                  catch (Exception ex)
                  {
                      logger.error(e.toString());
                  }
  
                  return ErrorPage.LOAD_ERRORPAGE;
              }
          }
    	
    	
      return contentURL;
    }
    
    public void fileUploaded(ValueChangeEvent event)
    {
       
    	int indexUpload = this.getEditItemSessionBean().getFiles().size()-1;
        
        UploadedFile file = (UploadedFile) event.getNewValue();
      String contentURL;
      if (file != null || file.getLength()==0)
      {
        contentURL = uploadFile(file);
    	if(contentURL != null && !contentURL.trim().equals(""))
    	{
    		EditItemSessionBean eisb = this.getEditItemSessionBean();
    		
    		FileVO fileVO = this.getEditItemSessionBean().getFiles().get(indexUpload).getFile();
    		
    		fileVO.getDefaultMetadata().setSize((int)file.getLength());
            fileVO.setName(file.getFilename());
            fileVO.getDefaultMetadata().setTitle(new TextVO(file.getFilename()));
            fileVO.setMimeType(file.getContentType());
            FormatVO formatVO = new FormatVO();
            formatVO.setType("dcterms:IMT");
            formatVO.setValue(file.getContentType());
            fileVO.getDefaultMetadata().getFormats().add(formatVO);
            fileVO.setContent(contentURL);
    	}
        //bindFiles();
      }
      else 
      {
          //show error message
          error(getMessage("ComponentEmpty"));
      }
    }
    
    /**
     * This method adds a file to the list of files of the item 
     * @return navigation string (null)
     */
    public String addFile()
    {
    	// avoid to upload more than one item before filling the metadata
    	if(this.getEditItemSessionBean().getFiles() != null)
    	{
    		FileVO newFile = new FileVO();
    		newFile.getMetadataSets().add(new MdsFileVO());
    		newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
    		this.getEditItemSessionBean().getFiles().add(new PubFileVOPresentation(this.getEditItemSessionBean().getFiles().size(), newFile, false));
    	}
    	return "loadEditItem";
    }
    
    /**
     * This method adds a locator to the list of locators of the item 
     * @return navigation string (null)
     */
    public String addLocator()
    {
    	if(this.getEditItemSessionBean().getLocators() != null)
    	{
    		FileVO newLocator = new FileVO();
    		newLocator.getMetadataSets().add(new MdsFileVO());
    		newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
    		this.getEditItemSessionBean().getLocators().add(new PubFileVOPresentation(this.getEditItemSessionBean().getLocators().size(), newLocator, true));
    	}
    	return "loadEditItem";
    }
    
    /**
     * This method saves the latest locator to the list of files of the item 
     * @return navigation string (null)
     */
    public String saveLocator()
    {
    	if(this.getEditItemSessionBean().getLocators() != null)
    	{
    		// set the name if it is not filled
        	if(this.getEditItemSessionBean().getLocators().get(this.getEditItemSessionBean().getLocators().size()-1).getFile().getDefaultMetadata().getTitle() == null || this.getEditItemSessionBean().getLocators().get(this.getEditItemSessionBean().getLocators().size()-1).getFile().getDefaultMetadata().getTitle().getValue().trim().equals(""))
        	{
        	    this.getEditItemSessionBean().getLocators().get(this.getEditItemSessionBean().getLocators().size()-1).getFile().getDefaultMetadata().setTitle(new TextVO(this.getEditItemSessionBean().getLocators().get(this.getEditItemSessionBean().getLocators().size()-1).getFile().getContent()));
        		//this.getEditItemSessionBean().getLocators().get(this.getEditItemSessionBean().getLocators().size()-1).getFile().setName(this.getEditItemSessionBean().getLocators().get(this.getEditItemSessionBean().getLocators().size()-1).getFile().getContent());
        	}
        	
    	}
    	return "loadEditItem";
    }
    
    /**
     * Shows the given Message below the itemList after next Reload of the DepositorWS. 
     * @param message the message to be displayed
     */
    private void showMessage(String message)
    {
        message = getMessage(message);
        this.getItemListSessionBean().setMessage(message);
    }

    /**
     * Retrieves the description of a context from the framework.
     * @return the context description
     */
    public String getContextDescription()
    {
        String contextDescription = "Could not retrieve context description.";
        
        try
        {
            ContextVO context = this.getItemControllerSessionBean().getCurrentContext();
            contextDescription = context.getDescription();
        }
        catch (Exception e)
        {
            logger.error("Could not retrieve context." + "\n" + e.toString());

            ((ErrorPage)getRequestBean(ErrorPage.class)).setException(e);
            return ErrorPage.LOAD_ERRORPAGE;
        }

        return contextDescription;
    }
    
    /**
     * Retrieves the description of a context to open it in a popup box. This method removes all
     * carriage returns because javascript throws an error if they are present.
     * @return the context description without carriage returns
     */
    public String getContextDescriptionForPopup()
    {
        String contextDescription = "Could not retrieve context description.";
        
        try
        {
            ContextVO context = this.getItemControllerSessionBean().getCurrentContext();
            contextDescription = context.getDescription();
        }
        catch (Exception e)
        {
            logger.error("Could not retrieve context." + "\n" + e.toString());

            ((ErrorPage)getRequestBean(ErrorPage.class)).setException(e);
            return ErrorPage.LOAD_ERRORPAGE;
        }
        // replace all carriage returns by whitespaces
        contextDescription = "<div class=\"affDetails\"><div class=\"formField\">"+contextDescription+"</div></div>";
        contextDescription = contextDescription.replaceAll("\r?\n"," ");
        return contextDescription;
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
     * @return a reference to the scoped data bean
     */
    protected de.mpg.escidoc.pubman.ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (de.mpg.escidoc.pubman.ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
    }
    
    /**
     * Returns a reference to the scoped data bean (the EditItemSessionBean).
     * @return a reference to the scoped data bean
     */
    protected de.mpg.escidoc.pubman.editItem.EditItemSessionBean getEditItemSessionBean()
    {
        return (EditItemSessionBean)getSessionBean(EditItemSessionBean.class);
    }

    /**
     * Returns the ContextListSessionBean.
     *
     * @return a reference to the scoped data bean (ContextListSessionBean)
     */
    protected ContextListSessionBean getCollectionListSessionBean()
    {
        return (ContextListSessionBean)getBean(ContextListSessionBean.class);
    }

    /**
     * Returns the ItemListSessionBean.
     * @return a reference to the scoped data bean (ItemListSessionBean)
     */
    protected ItemListSessionBean getItemListSessionBean()
    {
        return (ItemListSessionBean)getSessionBean(ItemListSessionBean.class);
    }

    /**
     * Displays validation messages.
     * @author Michael Franke
     * @param report The Validation report object.
     */
    private void showValidationMessages(ValidationReportVO report)
    {
        for (Iterator<ValidationReportItemVO> iter = report.getItems().iterator(); iter.hasNext();)
        {
            ValidationReportItemVO element = (ValidationReportItemVO) iter.next();
            if (element.isRestrictive())
            {
                error(getMessage(element.getContent()).replaceAll("\\$1", element.getElement()));
            }
            else
            {
                info(getMessage(element.getContent()).replaceAll("\\$1", element.getElement()));
            }
        }

        valMessage.setRendered(true);
    }
    
    
    

    /**
     * Enables/Disables the action links.
     */
    private void enableLinks()
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
     // Try to get the validation service
       
       
        boolean itemHasID = this.getPubItem().getVersion() != null && this.getPubItem().getVersion().getObjectId() != null;
        boolean isWorkflowStandard = false;
        boolean isWorkflowSimple = true;
        
        boolean isStatePending = true;
        boolean isStateSubmitted = false;
        boolean isStateReleased = false;
        boolean isStateInRevision = false;

        if (this.getPubItem() != null && this.getPubItem().getVersion() != null && this.getPubItem().getVersion().getState() != null)
        {
            isStatePending = this.getPubItem().getVersion().getState().equals(PubItemVO.State.PENDING);
            isStateSubmitted = this.getPubItem().getVersion().getState().equals(PubItemVO.State.SUBMITTED);
            isStateReleased = this.getPubItem().getVersion().getState().equals(PubItemVO.State.RELEASED);
            isStateInRevision = this.getPubItem().getVersion().getState().equals(PubItemVO.State.IN_REVISION);
        }
        
        boolean isModerator = loginHelper.getAccountUser().isModerator(this.getPubItem().getContext());
        boolean isDepositor = loginHelper.getAccountUser().isDepositor();
        boolean isOwner = true;
        if (this.getPubItem().getOwner() != null)
        {
            isOwner = (loginHelper.getAccountUser().getReference() != null ? loginHelper.getAccountUser().getReference().getObjectId().equals(this.getPubItem().getOwner().getObjectId()) : false);
        }
        
        try
        {
            isWorkflowStandard = (getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.STANDARD);
            isWorkflowSimple = (getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.SIMPLE);
        }
        catch (Exception e)
        {
            
        }
        
        this.lnkAccept.setRendered((isStateSubmitted || isStateReleased) && isModerator);
        this.lnkRelease.setRendered((isStatePending || isStateSubmitted) && isWorkflowSimple && isOwner);
        this.lnkDelete.setRendered(isStatePending && isOwner && itemHasID);
        this.lnkSaveAndSubmit.setRendered((isStatePending || isStateInRevision) &&  isWorkflowStandard && isOwner);
        this.lnkSave.setRendered(((isStatePending || isStateInRevision)&& isOwner) || (isStateSubmitted && isModerator));
        
        /*
        this.lnkAccept.setRendered(this.isInModifyMode() && loginHelper.getAccountUser().isModerator(this.getPubItem().getContext()));
        this.lnkDelete.setRendered(!this.isInModifyMode() && itemHasID);
        this.lnkSaveAndSubmit.setRendered(!this.isInModifyMode());
        this.lnkSave.setRendered(!this.isInModifyMode());
        */
    }

    /**
     * Evaluates if the EditItem should be in modify mode.
     * @return true if modify mode should be on
     */
    private boolean isInModifyMode()
    {
        boolean isModifyMode = this.getPubItem().getVersion().getState() != null
            && (this.getPubItem().getVersion().getState().equals(PubItemVO.State.SUBMITTED)
                    || this.getPubItem().getVersion().getState().equals(PubItemVO.State.RELEASED));

        return isModifyMode;
    }

    /**
     * Returns the AffiliationSessionBean.
     *
     * @return a reference to the scoped data bean (AffiliationSessionBean)
     */
    protected AffiliationSessionBean getAffiliationSessionBean()
    {
        return (AffiliationSessionBean) getSessionBean(AffiliationSessionBean.class);
    }

    /**
     * localized creation of SelectItems for the genres available.
     * @return SelectItem[] with Strings representing genres.
     */
    public SelectItem[] getGenres()
    {
        List<MdsPublicationVO.Genre> allowedGenres = null;
        List<AdminDescriptorVO> adminDescriptors = this.getItemControllerSessionBean().getCurrentContext().getAdminDescriptors();
        for (AdminDescriptorVO adminDescriptorVO : adminDescriptors)
        {
            if (adminDescriptorVO instanceof PublicationAdminDescriptorVO)
            {
                allowedGenres = ((PublicationAdminDescriptorVO)adminDescriptorVO).getAllowedGenres();
                return this.i18nHelper.getSelectItemsForEnum(true, allowedGenres.toArray(new MdsPublicationVO.Genre[]{}));
            }
        }
        return null;
    }

    /**
     * Returns all options for degreeType.
     * @return all options for degreeType
     */
    public SelectItem[] getDegreeTypes()
    {
        return this.i18nHelper.getSelectItemsDegreeType(true);
    }

    /**
     * Returns all options for reviewMethod.
     * @return all options for reviewMethod
     */
    public SelectItem[] getReviewMethods()
    {
        return this.i18nHelper.getSelectItemsReviewMethod(true);
    }
    
    /**
     * Returns all options for content categories.
     * @return all options for content c ategories.
     */
    public SelectItem[] getContentCategories()
    {
        return this.i18nHelper.getSelectItemsContentCategory(true);
    }
    
    /**
     * Returns all options for visibility.
     * @return all options for visibility
     */
    public SelectItem[] getVisibilities()
    {
        return this.i18nHelper.getSelectItemsVisibility(false);
    }

    public SelectItem[] getInvitationStatuses()
    {
        return this.i18nHelper.getSelectItemsInvitationStatus(true);
    }

    public String loadAffiliationTree()
    {
        return "loadAffiliationTree";
    }

    public HtmlMessages getValMessage()
    {
        return valMessage;
    }

    public void setValMessage(HtmlMessages valMessage)
    {
        this.valMessage = valMessage;
    }

    /**
     * Invitationstatus of event has to be converted as it's an enum that is supposed to be shown in a checkbox.
     * @return true if invitationstatus in VO is set, else false
     */
    public boolean getInvited()
    {
        boolean retVal = false;

        // Changed by FrM: Check for event
        if (this.getPubItem().getMetadata().getEvent() != null && this.getPubItem().getMetadata().getEvent().getInvitationStatus() != null
                && this.getPubItem().getMetadata().getEvent().getInvitationStatus().equals(EventVO.InvitationStatus.INVITED))
        {
            retVal = true;
        }

        return retVal;
    }

    /**
     * Invitationstatus of event has to be converted as it's an enum that is supposed to be shown in a checkbox.
     * @param invited the value of the checkbox
     */
    public void setInvited(boolean invited)
    {
        if (invited)
        {
            this.getPubItem().getMetadata().getEvent().setInvitationStatus(EventVO.InvitationStatus.INVITED);
        }
        else
        {
            this.getPubItem().getMetadata().getEvent().setInvitationStatus(null);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Invitationstatus in VO has been set to: '"
                    + this.getPubItem().getMetadata().getEvent().getInvitationStatus() + "'");
        }
    }

    public HtmlCommandLink getLnkAccept()
    {
        return lnkAccept;
    }

    public void setLnkAccept(HtmlCommandLink lnkAccept)
    {
        this.lnkAccept = lnkAccept;
    }

    public HtmlCommandLink getLnkDelete()
    {
        return lnkDelete;
    }

    public void setLnkDelete(HtmlCommandLink lnkDelete)
    {
        this.lnkDelete = lnkDelete;
    }

    public HtmlCommandLink getLnkSave()
    {
        return lnkSave;
    }

    public void setLnkSave(HtmlCommandLink lnkSave)
    {
        this.lnkSave = lnkSave;
    }

    public HtmlCommandLink getLnkSaveAndSubmit()
    {
        return lnkSaveAndSubmit;
    }

    public void setLnkSaveAndSubmit(HtmlCommandLink lnkSaveAndSubmit)
    {
        this.lnkSaveAndSubmit = lnkSaveAndSubmit;
    }

    public TitleCollection getEventTitleCollection()
    {
        return eventTitleCollection;
    }

    public void setEventTitleCollection(TitleCollection eventTitleCollection)
    {
        this.eventTitleCollection = eventTitleCollection;
    }

    public TitleCollection getTitleCollection()
    {
        return titleCollection;
    }

    public void setTitleCollection(TitleCollection titleCollection)
    {
        this.titleCollection = titleCollection;
    }

    public ContentAbstractCollection getContentAbstractCollection()
    {
        return contentAbstractCollection;
    }

    public void setContentAbstractCollection(ContentAbstractCollection contentAbstractCollection)
    {
        this.contentAbstractCollection = contentAbstractCollection;
    }

    public CreatorCollection getCreatorCollection()
    {
        return creatorCollection;
    }

    public void setCreatorCollection(CreatorCollection creatorCollection)
    {
        this.creatorCollection = creatorCollection;
    }

    public IdentifierCollection getIdentifierCollection()
    {
        return identifierCollection;
    }

    public void setIdentifierCollection(IdentifierCollection identifierCollection)
    {
        this.identifierCollection = identifierCollection;
    }
    
    public String getPubCollectionName()
    {
        return contextName;
    }

    public void setPubCollectionName(String pubCollection)
    {
        this.contextName = pubCollection;
    }

    public SourceCollection getSourceCollection()
    {
        return sourceCollection;
    }

    public void setSourceCollection(SourceCollection sourceCollection)
    {
        this.sourceCollection = sourceCollection;
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

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public CoreTable getFileTable() {
		return fileTable;
	}

	public void setFileTable(CoreTable fileTable) {
		this.fileTable = fileTable;
	}
	
	public int getNumberOfFiles()
	{
		int fileNumber = 0;
		if(this.getEditItemSessionBean().getFiles() != null)
		{
			fileNumber = this.getEditItemSessionBean().getFiles().size();
		}
		return fileNumber;
	}
	
	public int getNumberOfLocators()
	{
		int locatorNumber = 0;
		if(this.getEditItemSessionBean().getLocators() != null)
		{
			locatorNumber = this.getEditItemSessionBean().getLocators().size();
		}
		return locatorNumber;
	}
	
	public EditItemPage getEditItemPage()
	{
	    return (EditItemPage)getBean(EditItemPage.class);
	}

	public PubItemVO getItem() {
		return item;
	}

	public void setItem(PubItemVO item) {
		this.item = item;
	}

    public boolean isFromEasySubmission()
    {
        return fromEasySubmission;
    }

    public void setFromEasySubmission(boolean fromEasySubmission)
    {
        this.fromEasySubmission = fromEasySubmission;
    }

    public HtmlCommandLink getLnkRelease()
    {
        return lnkRelease;
    }

    public void setLnkRelease(HtmlCommandLink lnkRelease)
    {
        this.lnkRelease = lnkRelease;
    }
   

}
