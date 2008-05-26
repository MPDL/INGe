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

package de.mpg.escidoc.pubman.easySubmission;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.UIXIterator;
import org.apache.myfaces.trinidad.model.UploadedFile;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.pubman.editItem.bean.CreatorCollection;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.MetadataHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.metadata.ArxivNotAvailableException;
import de.mpg.escidoc.services.common.metadata.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.common.metadata.MultipleEntriesInBibtexException;
import de.mpg.escidoc.services.common.metadata.NoEntryInBibtexException;
import de.mpg.escidoc.services.common.valueobjects.AdminDescriptorVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * Fragment class for the easy submission. This class provides all functionality for editing, saving and submitting a
 * PubItem within the easy submission process.
 *
 * @author: Tobias Schraut, created 04.04.2008
 * @version: $Revision: 1 $ $LastChangedDate: 2007-12-18 09:30:58 +0100 (Di, 18 Dez 2007) $
 */
public class EasySubmission extends FacesBean
{
    public static final String BEAN_NAME = "EasySubmission";
    private static Logger logger = Logger.getLogger(EasySubmission.class);
    
    // Metadata Service
    private MetadataHandler mdHandler = null;
    
    // XML Transforming Service
    private XmlTransforming xmlTransforming = null;
    
    // Validation Service
    private ItemValidating itemValidating = null;
    
    private HtmlSelectOneRadio radioSelect;
    
    private HtmlSelectOneMenu dateSelect;
    
    // constants for the submission method
    public SelectItem SUBMISSION_METHOD_MANUAL = new SelectItem("MANUAL", getLabel("easy_submission_method_manual"));
    public SelectItem SUBMISSION_METHOD_FETCH_IMPORT = new SelectItem("FETCH_IMPORT", getLabel("easy_submission_method_fetch_import"));
    public SelectItem[] SUBMISSION_METHOD_OPTIONS = new SelectItem[]{SUBMISSION_METHOD_MANUAL, SUBMISSION_METHOD_FETCH_IMPORT};
    
    // constants for Date types
    public SelectItem DATE_CREATED = new SelectItem("DATE_CREATED", getLabel("easy_submission_lblDateCreated"));
    public SelectItem DATE_SUBMITTED = new SelectItem("DATE_SUBMITTED", getLabel("easy_submission_lblDateSubmitted"));
    public SelectItem DATE_ACCEPTED = new SelectItem("DATE_ACCEPTED", getLabel("easy_submission_lblDateAccepted"));
    public SelectItem DATE_PUBLISHED_IN_PRINT = new SelectItem("DATE_PUBLISHED_IN_PRINT", getLabel("easy_submission_lblDatePublishedInPrint"));
    public SelectItem DATE_PUBLISHED_ONLINE = new SelectItem("DATE_PUBLISHED_ONLINE", getLabel("easy_submission_lblDatePublishedOnline"));
    public SelectItem DATE_MODIFIED = new SelectItem("DATE_MODIFIED", getLabel("easy_submission_lblDateModified"));
    public SelectItem[] DATE_TYPE_OPTIONS = new SelectItem[]{DATE_CREATED, DATE_SUBMITTED, DATE_ACCEPTED, DATE_PUBLISHED_IN_PRINT, DATE_PUBLISHED_ONLINE, DATE_MODIFIED};
    
    // constants for external service type
    public SelectItem EXTERNAL_SERVICE_ESCIDOC = new SelectItem("ESCIDOC", getLabel("easy_submission_lblIDTypeEscidoc"));
    public SelectItem EXTERNAL_SERVICE_ARXIV = new SelectItem("ARXIV", getLabel("easy_submission_lblIDTypeArxiv"));
    public SelectItem[] EXTERNAL_SERVICE_OPTIONS = new SelectItem[]{EXTERNAL_SERVICE_ARXIV, EXTERNAL_SERVICE_ESCIDOC};

    // Faces navigation string
    public final static String LOAD_EASYSUBMISSION = "loadEasySubmission";
    
    private UploadedFile uploadedFile;
    
    private UIXIterator fileIterator = new UIXIterator();
    
    private UIXIterator locatorIterator = new UIXIterator();
    
    public SelectItem[] locatorVisibilities;
    
    private CreatorCollection creatorCollection;
    
    private String selectedDate;
    
    private UploadedFile uploadedBibTexFile;
    
    private boolean fromEasySubmission = false;
    
    /**
     * the ID for the object to fetch by the external service
     */
    private String serviceID;
    
    private HtmlMessages valMessage = new HtmlMessages();
    
    
    /**
     * Public constructor.
     */
    public EasySubmission()
    {
        try
        {
            InitialContext initialContext = new InitialContext();
            this.mdHandler = (MetadataHandler) initialContext.lookup(MetadataHandler.SERVICE_NAME);
            this.xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            this.itemValidating = (ItemValidating) initialContext.lookup(ItemValidating.SERVICE_NAME);
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
    	super.init();
    	
    	this.locatorVisibilities = this.i18nHelper.getSelectItemsVisibility(true);
    	
    	// if the user has reached Step 3, an item has already been created and must be set in the EasySubmissionSessionBean for further manipulation
    	if(this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP3))
    	{
    		//this.getEasySubmissionSessionBean().setCurrentItem(this.getItemControllerSessionBean().getCurrentPubItem());
    		//bindFiles();
    		if(this.getEasySubmissionSessionBean().getFiles() == null)
    		{
    			// add a locator
    			FileVO newLocator = new FileVO();
        		newLocator.setContentCategory(JiBXHelper.serializeRegularEnumToString(PubFileVOPresentation.ContentCategory.SUPPLEMENTARY_MATERIAL));
        		newLocator.setVisibility(FileVO.Visibility.PUBLIC);
        		this.getEasySubmissionSessionBean().getLocators().add(new PubFileVOPresentation(0, newLocator, true));
    			// add a file
    			FileVO newFile = new FileVO();
    			newFile.setVisibility(FileVO.Visibility.PUBLIC);
    			this.getEasySubmissionSessionBean().getFiles().add(new PubFileVOPresentation(0, newFile, false));
    		}
    		if(this.getEasySubmissionSessionBean().getFiles().size() < 1)
    		{
    			// add a file
    			FileVO newFile = new FileVO();
    			newFile.setVisibility(FileVO.Visibility.PUBLIC);
    			this.getEasySubmissionSessionBean().getFiles().add(new PubFileVOPresentation(0, newFile, false));
    		}
    		if(this.getEasySubmissionSessionBean().getLocators().size() < 1)
    		{
    			//add a locator
    			FileVO newLocator = new FileVO();
        		newLocator.setContentCategory(JiBXHelper.serializeRegularEnumToString(PubFileVOPresentation.ContentCategory.SUPPLEMENTARY_MATERIAL));
        		newLocator.setVisibility(FileVO.Visibility.PUBLIC);
        		this.getEasySubmissionSessionBean().getLocators().add(new PubFileVOPresentation(0, newLocator, true));
    		}
    	}
    	
    	if(this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP4))
    	{
    		this.creatorCollection = new CreatorCollection(this.getItem().getMetadata().getCreators());
    	}
    }
 
    
    public String selectSubmissionMethod()
    {
    	String submittedValue = CommonUtils.getUIValue(this.radioSelect);
    	
    	// set the desired submission method in the session bean
    	EasySubmissionSessionBean easySubmissionSessionBean = (EasySubmissionSessionBean)getSessionBean(EasySubmissionSessionBean.class);
    	easySubmissionSessionBean.setCurrentSubmissionMethod(submittedValue);
    	
    	// select the default context if only one exists
    	ContextListSessionBean contextListSessionBean = (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    	if(contextListSessionBean.getContextList() != null && contextListSessionBean.getContextList().size() == 1)
    	{
    		contextListSessionBean.getContextList().get(0).setSelected(false);
    		contextListSessionBean.getContextList().get(0).selectForEasySubmission();
    	}
    	
    	// set the current submission step to step2
    	easySubmissionSessionBean.setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
    	
    	return null;
    }
    
    public String newEasySubmission()
    {
    	// initialize the collection list first
    	this.getContextListSessionBean();
    	
    	EasySubmissionSessionBean easySubmissionSessionBean = (EasySubmissionSessionBean)getSessionBean(EasySubmissionSessionBean.class);
    	this.getItemControllerSessionBean().setCurrentPubItem(null);
    	//clean the EasySubmissionSessionBean
    	easySubmissionSessionBean.getFiles().clear();
    	easySubmissionSessionBean.getLocators().clear();
    	easySubmissionSessionBean.setSelectedDate("");
    	
    	// also make sure that the EditItemSessionBean is cleaned, too
    	this.getEditItemSessionBean().getFiles().clear();
    	this.getEditItemSessionBean().getLocators().clear();
    	
    	
    	// deselect the  selected context
    	ContextListSessionBean contextListSessionBean = (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    	if(contextListSessionBean.getContextList() != null)
    	{
    		for(int i = 0; i < contextListSessionBean.getContextList().size(); i++)
        	{
        		contextListSessionBean.getContextList().get(i).setSelected(false);
        	}
    	}
    	
    	// set the current submission step to step1
    	easySubmissionSessionBean.setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP1);
    	
    	return "loadNewEasySubmission";
    }
    
    /**
     * This method adds a file to the list of files of the item 
     * @return navigation string (null)
     */
    public String addFile()
    {
    	// first try to upload the entered file
    	upload();
    	
    	// then try to save the locator
    	saveLocator();
    	
    	if(this.getEasySubmissionSessionBean().getFiles() != null && this.getEasySubmissionSessionBean().getFiles().size() > 0 && this.getEasySubmissionSessionBean().getFiles().get(this.getEasySubmissionSessionBean().getFiles().size()-1).getFile().getSize() > 0)
    	{
    		FileVO newFile = new FileVO();
			newFile.setVisibility(FileVO.Visibility.PUBLIC);
    		this.getEasySubmissionSessionBean().getFiles().add(new PubFileVOPresentation(this.getEasySubmissionSessionBean().getFiles().size(), newFile, false));
    	}
    	return "loadNewEasySubmission";
    }
    
    /**
     * This method adds a locator to the list of files of the item 
     * @return navigation string (null)
     */
    public String addLocator()
    {
    	//  first try to upload the entered file
    	upload();
    	
    	// then try to save the locator
    	saveLocator();
    	
    	if(this.getEasySubmissionSessionBean().getLocators() != null 
    			&& this.getEasySubmissionSessionBean().getLocators().get(this.getEasySubmissionSessionBean().getLocators().size()-1).getFile().getLocator() != null 
    			&& !this.getEasySubmissionSessionBean().getLocators().get(this.getEasySubmissionSessionBean().getLocators().size()-1).getFile().getLocator().trim().equals(""))
    	{
    		PubFileVOPresentation newLocator = new PubFileVOPresentation(this.getEasySubmissionSessionBean().getLocators().size(), true);
    		// set fixed content type
    		newLocator.getFile().setContentCategory(JiBXHelper.serializeRegularEnumToString(PubFileVOPresentation.ContentCategory.SUPPLEMENTARY_MATERIAL));
    		newLocator.getFile().setVisibility(FileVO.Visibility.PUBLIC);
    		this.getEasySubmissionSessionBean().getLocators().add(newLocator);
    	}
    	return "loadNewEasySubmission";
    }
    
    private void bindFiles()
    {
    	List<PubFileVOPresentation> files = new ArrayList<PubFileVOPresentation>();
    	for (int i = 0; i < this.getItemControllerSessionBean().getCurrentPubItem().getFiles().size(); i++)
    	{
			PubFileVOPresentation filepres = new PubFileVOPresentation(i, this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(i));
			files.add(filepres);
		}
    	this.getEasySubmissionSessionBean().setFiles(files);
    }
    
    /**
     * This method binds the uploaded files to the files in the PubItem during the save process
     */
    private void bindUploadedFiles()
    {
    	this.getItem().getFiles().clear();
    	if(this.getFiles() != null && this.getFiles().size() > 0)
    	{
    		for(int i = 0; i < this.getFiles().size(); i++)
    		{
    			this.getItem().getFiles().add(this.getFiles().get(i).getFile());
    		}
    	}
    	if(this.getLocators() != null && this.getLocators().size() > 0)
    	{
    		for(int i = 0; i < this.getLocators().size(); i++)
    		{
    			this.getItem().getFiles().add(this.getLocators().get(i).getFile());
    		}
    	}
    }
    
    public String saveValues()
    {
    	return null;
    }
    
    public String saveLocator()
    {
    	EasySubmissionSessionBean essb = this.getEasySubmissionSessionBean();
    	// set the name if it is not filled
    	if(this.getLocators().get(this.getLocators().size()-1).getFile().getName() == null || this.getLocators().get(this.getLocators().size()-1).getFile().getName().trim().equals(""))
    	{
    		this.getLocators().get(this.getLocators().size()-1).getFile().setName(this.getLocators().get(this.getLocators().size()-1).getFile().getLocator());
    	}
    	// set a dummy file size for rendering purposes
    	if(this.getLocators().get(this.getLocators().size()-1).getFile().getLocator() != null && !this.getLocators().get(this.getLocators().size()-1).getFile().getLocator().trim().equals(""))
    	{
    		this.getLocators().get(this.getLocators().size()-1).getFile().setSize(new Long(11));
    	}
    	this.locatorIterator = new UIXIterator();
    	return "loadNewEasySubmission";
    }
    
    /**
     * This method reorganizes the index property in PubFileVOPresentation after removing one element of the list.
     */
    public void reorganizeFileIndexes()
    {
    	if(this.getEasySubmissionSessionBean().getFiles() != null)
    	{
    		for(int i = 0; i < this.getEasySubmissionSessionBean().getFiles().size(); i++)
        	{
        		this.getEasySubmissionSessionBean().getFiles().get(i).setIndex(i);
        	}
    	}
    }
    
    /**
     * This method reorganizes the index property in PubFileVOPresentation after removing one element of the list.
     */
    public void reorganizeLocatorIndexes()
    {
    	if(this.getEasySubmissionSessionBean().getLocators() != null)
    	{
    		for(int i = 0; i < this.getEasySubmissionSessionBean().getLocators().size(); i++)
        	{
        		this.getEasySubmissionSessionBean().getLocators().get(i).setIndex(i);
        	}
    	}
    }
    
    /**
     * Saves the item.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String save()
    {
    	mapSelectedDate();
    	// bind the temporary uploaded files to the files in the current item
    	bindUploadedFiles();
    	
    	this.setFromEasySubmission(true);
    	
    	
    	
    	if (validateStep5("validate") == null)
    	{
    		return null;
    	}
    	EditItem editItem = (EditItem)getRequestBean(EditItem.class);
    	editItem.setFromEasySubmission(true);
    	
    	return (editItem.save());
    	
//    	/*
//         * FrM: Validation with validation point "default"
//         */
//        ValidationReportVO report = null;
//        try
//        {
//        	PubItemVO itemVO = new PubItemVO(this.getItem());
//            report = this.itemValidating.validateItemObject(itemVO, "default");
//        }
//        catch (Exception e)
//        {
//            throw new RuntimeException("Validation error", e);
//        }
//        logger.debug("Validation Report: " + report);
//
//        if (report.isValid() && !report.hasItems())
//        {
//
//            if (logger.isDebugEnabled())
//            {
//                logger.debug("Saving item...");
//            }
//
//            //String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(DepositorWS.LOAD_DEPOSITORWS, false); 
//            this.getItemListSessionBean().setListDirty(true);
//            String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false);
//
//            if (retVal == null)
//            {
//                this.showValidationMessages(
//                        this.getItemControllerSessionBean().getCurrentItemValidationReport());
//            }
//            else if (retVal != null && retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
//            {
//                this.showMessage(DepositorWS.MESSAGE_SUCCESSFULLY_SAVED);
//            }
//            return retVal;
//        }
//        else if (report.isValid())
//        {
//            // TODO FrM: Informative messages
//            this.getItemListSessionBean().setListDirty(true);
//        	String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false);
//
//            if (retVal == null)
//            {
//                this.showValidationMessages(
//                        this.getItemControllerSessionBean().getCurrentItemValidationReport());
//            }
//            else if (retVal != null && retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
//            {
//                this.showMessage(DepositorWS.MESSAGE_SUCCESSFULLY_SAVED);
//            }
//            return retVal;
//        }
//        else
//        {           
//            // Item is invalid, do not submit anything.
//            this.showValidationMessages(report);
//            return null;
//        }        
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
     * Shows the given Message below the itemList after next Reload of the DepositorWS. 
     * @param message the message to be displayed
     */
    private void showMessage(String message)
    {
        message = getMessage(message);
        this.getItemListSessionBean().setMessage(message);
    }
    
    /**
     * Uploads a file
     * @param event
     */
    public void fileUploaded(ValueChangeEvent event)
    {
       
    	int indexUpload = this.getItem().getFiles().size()-1;
    	
    	 UploadedFile file = (UploadedFile) event.getNewValue();
      String contentURL;
      if (file != null)
      {
        contentURL = uploadFile(file);
    	if(contentURL != null && !contentURL.trim().equals(""))
    	{
    		this.getItem().getFiles().get(indexUpload).setSize(new Long(file.getLength()));
            this.getItem().getFiles().get(indexUpload).setName(file.getFilename());
            this.getItem().getFiles().get(indexUpload).setMimeType(file.getContentType());
            this.getItem().getFiles().get(indexUpload).setContent(contentURL);
    	}
      }
    }
    
    public String upload()
    {
       
    	int indexUpload = this.getFiles().size()-1;
    	
    	UploadedFile file = this.uploadedFile;
      String contentURL;
      if (file != null)
      {
        contentURL = uploadFile(file);
    	if(contentURL != null && !contentURL.trim().equals(""))
    	{
    		this.getFiles().get(indexUpload).getFile().setSize(new Long(file.getLength()));
    		// set the file name automatically if it is not filled by the user
    		if(this.getFiles().get(indexUpload).getFile().getName() == null || this.getFiles().get(indexUpload).getFile().getName().trim().equals(""))
    		{
    			this.getFiles().get(indexUpload).getFile().setName(file.getFilename());
    		}
            this.getFiles().get(indexUpload).getFile().setMimeType(file.getContentType());
            this.getFiles().get(indexUpload).getFile().setContent(contentURL);
    	}
    	this.init();
      }
      return "loadNewEasySubmission";
    }
    
    /**
     * Uploads a file to the FIZ Framework and recieves and returns the location of the file in the FW
     * @param file
     * @return
     */
    public String uploadFile(UploadedFile file)
    {
    	String contentURL = "";
    	if (file != null && file.getLength() > 0)
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
    
    public String uploadBibtexFile()
    {
    	try
    	{
    		StringBuffer content = new StringBuffer();
    		try
    		{
    			BufferedReader reader = new BufferedReader(new InputStreamReader(this.uploadedBibTexFile.getInputStream()));
	    		String line;
	    		
	    		while ((line = reader.readLine()) != null)
	    		{
	    			content.append(line + "\n");
	    		}
    		}
    		catch (NullPointerException npe) {
    			logger.error("Error reading bibtex file", npe);
    			warn(getMessage("easy_submission_bibtex_empty_file"));
    			return null;
			}
    		String result = mdHandler.bibtex2item(content.toString());
    		PubItemVO itemVO = xmlTransforming.transformToPubItem(result);
    		itemVO.setContext(getItem().getContext());
    		this.getItemControllerSessionBean().setCurrentPubItem(itemVO);
    		this.setItem(itemVO);
    	}
    	catch (MultipleEntriesInBibtexException meibe) {
    		logger.error("Error reading bibtex file", meibe);
			warn(getMessage("easy_submission_bibtex_multiple_entries"));
			return null;
		}
    	catch (NoEntryInBibtexException neibe) {
    		logger.error("Error reading bibtex file", neibe);
			warn(getMessage("easy_submission_bibtex_no_entries"));
			return null;
		}
    	catch (Exception e) {
			logger.error("Error reading bibtex file", e);
			error(getMessage("easy_submission_bibtex_error"));
			return null;
		}
    	return "loadNewEasySubmission";
    }
    
    public String fetchMetadata()
    {
    	if (EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL.equals(this.getEasySubmissionSessionBean().getImportMethod()))
    	{
	    	if (getServiceID() != null && !"".equals(getServiceID()))
	    	{
	    		PubItemVO itemVO = null;
	    		String service = this.getEasySubmissionSessionBean().getCurrentExternalServiceType();
	    		if ("ARXIV".equals(service))
	    		{
	    			try
	    			{
		    			String result = mdHandler.fetchOAIRecord(getServiceID(), "http://export.arxiv.org/oai2?verb=GetRecord&identifier=oai:arXiv.org:", "arXiv");
		    			itemVO = xmlTransforming.transformToPubItem(result);
		    			getItem().setMetadata(itemVO.getMetadata());
	    			}
                    catch (IdentifierNotRecognisedException inre)
                    {
                        logger.error("Error fetching from arxiv", inre);
                        
                        error(getMessage("easy_submission_arxiv_identifier_error") + "\n" + inre.getMessage());
                        
                        return null;
                    }
                    catch (ArxivNotAvailableException anae)
                    {
                        logger.error("arxiv currently not available", anae);
                        
                        long millis = anae.getRetryAfter().getTime() - (new Date()).getTime();
                        if (millis < 1)
                        {
                            millis = 1;
                        }
                        
                        error(getMessage("easy_submission_arxiv_not_available_error").replace("$1", Math.ceil(millis / 1000) + ""));
                        
                        return null;
                    }
	    			catch (Exception e) {
	    				logger.error("Error fetching from arxiv", e);
						
	    				error(getMessage("easy_submission_arxiv_error"));
	    				
	    				return null;
					}
	    		}
	    		else if ("ESCIDOC".equals(service))
	    		{
	    			try
	    			{
		    			String result = ServiceLocator.getItemHandler().retrieve(getServiceID());
		    			itemVO = xmlTransforming.transformToPubItem(result);
		    			getItem().setMetadata(itemVO.getMetadata());
	    			}
	    			catch (Exception e) {
	    				logger.error("Error fetching from escidoc", e);
	    				error(getMessage("easy_submission_escidoc_error"));
	    				return null;
					}
	    		}
	    	}
	    	else
    		{
    			warn(getMessage("easy_submission_arxiv_no_id"));
				return null;
    		}
    	}
    	else if (EasySubmissionSessionBean.IMPORT_METHOD_BIBTEX.equals(this.getEasySubmissionSessionBean().getImportMethod()))
    	{
    		String uploadResult = uploadBibtexFile();
    		if (uploadResult == null)
    		{
    			return null;
    		}
    	}
    	
    	return "loadEditItem";
    }
    
    public String cancelEasySubmission()
    {
    	this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP1);
    	return "loadHome";
    }
    
    public String loadStep1()
    {
    	this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP1);
    	return "loadNewEasySubmission";
    }
    
    public String loadStep2()
    {
    	this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
    	return "loadNewEasySubmission";
    }
    
    
    public String loadStep3Manual()
    {
    	this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);
    	this.init();
    	return "loadNewEasySubmission";
    }
    
    public String loadStep4Manual()
    {
    	// first try to upload the entered file
    	upload();
    	
    	// then try to save the locator
    	saveLocator();
    	
    	// save the files and locators in the item in the ItemControllerSessionBean
    	this.getItemControllerSessionBean().getCurrentPubItem().getFiles().clear();
    	// first add the files
    	for(int i = 0; i < this.getEasySubmissionSessionBean().getFiles().size(); i++)
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getFiles().add(this.getEasySubmissionSessionBean().getFiles().get(i).getFile());
    	}
    	// then add the locators
    	for(int i = 0; i < this.getEasySubmissionSessionBean().getLocators().size(); i++)
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getFiles().add(this.getEasySubmissionSessionBean().getLocators().get(i).getFile());
    	}
    	
    	// add an empty file and an empty locator if necessary for display purposes
    	if(this.getEasySubmissionSessionBean().getFiles() != null && this.getEasySubmissionSessionBean().getFiles().size() > 0)
    	{
    		if(this.getEasySubmissionSessionBean().getFiles().get(this.getEasySubmissionSessionBean().getFiles().size()-1).getFile().getSize() > 0)
    		{
    			FileVO newFile = new FileVO();
    			newFile.setVisibility(FileVO.Visibility.PUBLIC);
    			this.getEasySubmissionSessionBean().getFiles().add(new PubFileVOPresentation(this.getEasySubmissionSessionBean().getFiles().size(), newFile, false));
    		}
    	}
    	
    	if(this.getEasySubmissionSessionBean().getLocators() != null && this.getEasySubmissionSessionBean().getLocators().size() > 0)
    	{
    		if(this.getEasySubmissionSessionBean().getLocators().get(this.getEasySubmissionSessionBean().getLocators().size()-1).getFile().getSize() > 0)
    		{
    			PubFileVOPresentation newLocator = new PubFileVOPresentation(this.getEasySubmissionSessionBean().getLocators().size(), true);
        		// set fixed content type
        		newLocator.getFile().setContentCategory(JiBXHelper.serializeRegularEnumToString(PubFileVOPresentation.ContentCategory.SUPPLEMENTARY_MATERIAL));
        		newLocator.getFile().setVisibility(FileVO.Visibility.PUBLIC);
        		this.getEasySubmissionSessionBean().getLocators().add(newLocator);
    		}
    	}
    	
    	// additionally map the dates if the user comes from  Step5
    	if(this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP5))
    	{
    		mapSelectedDate();
    	}
    	
    	// validate
    	try
    	{
    		ValidationReportVO report = itemValidating.validateItemObject(new PubItemVO(this.getItemControllerSessionBean().getCurrentPubItem()), "easy_submission_step_3");
    		if (!report.isValid())
    		{
    			for (ValidationReportItemVO item : report.getItems()) {
					if (item.isRestrictive())
					{
						error(getMessage(item.getContent()));
					}
					else
					{
						warn(getMessage(item.getContent()));
					}
				}
    			return null;
    		}
    		
    	}
    	catch (Exception e) {
			logger.error("Validation error", e);
		}
    	
    	this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP4);
    	this.init();
    	return "loadNewEasySubmission";
    }
    
    public String loadStep5Manual()
    {
    	// validate
    	try
    	{
    		ValidationReportVO report = itemValidating.validateItemObject(new PubItemVO(this.getItemControllerSessionBean().getCurrentPubItem()), "easy_submission_step_4");
    		if (!report.isValid())
    		{
    			for (ValidationReportItemVO item : report.getItems()) {
					if (item.isRestrictive())
					{
						error(getMessage(item.getContent()));
					}
					else
					{
						warn(getMessage(item.getContent()));
					}
				}
    			return null;
    		}
    		
    	}
    	catch (Exception e) {
			logger.error("Validation error", e);
		}

    	this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP5);
    	return "loadNewEasySubmission";
    }
    
    public String loadPreview()
    {

    	// Map entered date to entered type
    	mapSelectedDate();
    	// validate
    	
    	return validateStep5("loadEditItem");
    }

	private String validateStep5(String navigateTo) {
		try
    	{
    		ValidationReportVO report = itemValidating.validateItemObject(new PubItemVO(this.getItemControllerSessionBean().getCurrentPubItem()), "easy_submission_step_5");
    		if (!report.isValid())
    		{
    			for (ValidationReportItemVO item : report.getItems())
    			{
					if (item.isRestrictive())
					{
						error(getMessage(item.getContent()));
					}
					else
					{
						warn(getMessage(item.getContent()));
					}
				}
    			return null;
    		}
    		
    	}
    	catch (Exception e) {
			logger.error("Validation error", e);
		}
    	return navigateTo;
	}
    
    /**
     * This method maps the entered date into the MD record of the item according to the selected type
     */
    private void mapSelectedDate()
    {
    	String selectedDateType = CommonUtils.getUIValue(this.dateSelect);
    	
    	// first delete all previously entered dates
    	this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateCreated("");
    	this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateSubmitted("");
    	this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateAccepted("");
    	this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDatePublishedOnline("");
    	this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateModified("");
    	this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDatePublishedInPrint("");
    	// map the selected date type to the referring metadata property
    	if(selectedDateType.equals("DATE_CREATED"))
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateCreated(this.getEasySubmissionSessionBean().getSelectedDate());
    	}
    	else if(selectedDateType.equals("DATE_SUBMITTED"))
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateSubmitted(this.getEasySubmissionSessionBean().getSelectedDate());
    	}
    	else if(selectedDateType.equals("DATE_ACCEPTED"))
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateAccepted(this.getEasySubmissionSessionBean().getSelectedDate());
    	}
    	else if(selectedDateType.equals("DATE_PUBLISHED_ONLINE"))
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDatePublishedOnline(this.getEasySubmissionSessionBean().getSelectedDate());
    	}
    	else if(selectedDateType.equals("DATE_MODIFIED"))
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateModified(this.getEasySubmissionSessionBean().getSelectedDate());
    	}
    	else
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDatePublishedInPrint(this.getEasySubmissionSessionBean().getSelectedDate());
    	}
    }
    
    
    /**
     * This method selects the import method 'fetch metadata from external systems'
     * @return String naigation string
     */
    public String selectImportExternal()
    {
    	this.getEasySubmissionSessionBean().setImportMethod(EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL);
    	return "loadNewEasySubmission";
    }
    
    /**
     * This method selects the import method 'Upload Bibtex file'
     * @return String naigation string
     */
    public String selectImportBibtex()
    {
    	this.getEasySubmissionSessionBean().setImportMethod(EasySubmissionSessionBean.IMPORT_METHOD_BIBTEX);
    	return "loadNewEasySubmission";
    }
    
    /**
     * returns a flag which sets the fields of the import method 'fetch metadata from external systems' to disabled or not
     * @return boolean the flag for disabling
     */
    public boolean getDisableExternalFields()
    {
    	boolean disable = false;
    	if(this.getEasySubmissionSessionBean().getImportMethod().equals(EasySubmissionSessionBean.IMPORT_METHOD_BIBTEX))
    	{
    		disable = true;
    	}
    	return disable;
    }
    
    /**
     * returns a flag which sets the fields of the import method 'Upload Bibtex file' to disabled or not
     * @return boolean the flag for disabling
     */
    public boolean getDisableBibtexFields()
    {
    	boolean disable = false;
    	if(this.getEasySubmissionSessionBean().getImportMethod().equals(EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL))
    	{
    		disable = true;
    	}
    	return disable;
    }

    /**
     * Returns the CollectionListSessionBean.
     *
     * @return a reference to the scoped data bean (CollectionListSessionBean)
     */
    protected ContextListSessionBean getContextListSessionBean()
    {
    	return (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    }
    
    /**
     * Returns the EasySubmissionSessionBean.
     *
     * @return a reference to the scoped data bean (EasySubmissionSessionBean)
     */
    protected EasySubmissionSessionBean getEasySubmissionSessionBean()
    {
    	return (EasySubmissionSessionBean) getSessionBean(EasySubmissionSessionBean.class);
    }
    
    /**
     * Returns the EditItemSessionBean.
     *
     * @return a reference to the scoped data bean (EditItemSessionBean)
     */
    protected EditItemSessionBean getEditItemSessionBean()
    {
    	return (EditItemSessionBean) getSessionBean(EditItemSessionBean.class);
    }
    
    /**
     * Returns the ItemControllerSessionBean.
     *
     * @return a reference to the scoped data bean (ItemControllerSessionBean)
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
    	return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
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

    public SelectItem[] getSUBMISSION_METHOD_OPTIONS() {
		return SUBMISSION_METHOD_OPTIONS;
	}

	public void setSUBMISSION_METHOD_OPTIONS(SelectItem[] submission_method_options) {
		SUBMISSION_METHOD_OPTIONS = submission_method_options;
	}
	
	

	public SelectItem[] getDATE_TYPE_OPTIONS() {
		return DATE_TYPE_OPTIONS;
	}

	public void setDATE_TYPE_OPTIONS(SelectItem[] date_type_options) {
		DATE_TYPE_OPTIONS = date_type_options;
	}
	
	

	public SelectItem[] getEXTERNAL_SERVICE_OPTIONS() {
		return EXTERNAL_SERVICE_OPTIONS;
	}

	public void setEXTERNAL_SERVICE_OPTIONS(SelectItem[] external_service_options) {
		EXTERNAL_SERVICE_OPTIONS = external_service_options;
	}

	public HtmlSelectOneRadio getRadioSelect() {
		return radioSelect;
	}

	public void setRadioSelect(HtmlSelectOneRadio radioSelect) {
		this.radioSelect = radioSelect;
	}

	/*public PubItemVO getItem() {
		return this.getEasySubmissionSessionBean().getCurrentItem();
	}

	public void setItem(PubItemVO item) {
		this.getEasySubmissionSessionBean().setCurrentItem(item);
	}*/
	
	public PubItemVO getItem() {
		return this.getItemControllerSessionBean().getCurrentPubItem();
	}

	public void setItem(PubItemVO item) {
		this.getItemControllerSessionBean().setCurrentPubItem(item);
	}
	
	public List<PubFileVOPresentation> getFiles() {
		return this.getEasySubmissionSessionBean().getFiles();
	}
	
	public List<PubFileVOPresentation> getLocators() {
		return this.getEasySubmissionSessionBean().getLocators();
	}
	
	public void setFiles(List<PubFileVOPresentation> files) {
		this.getEasySubmissionSessionBean().setFiles(files);
	}
	
	public void setLocators(List<PubFileVOPresentation> files) {
		this.getEasySubmissionSessionBean().setLocators(files);
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}
	
	
	
	public UIXIterator getFileIterator() {
		return fileIterator;
	}

	public void setFileIterator(UIXIterator fileIterator) {
		this.fileIterator = fileIterator;
	}

	public UIXIterator getLocatorIterator() {
		return locatorIterator;
	}

	public void setLocatorIterator(UIXIterator locatorIterator) {
		this.locatorIterator = locatorIterator;
	}
	
	

	public CreatorCollection getCreatorCollection() {
		return creatorCollection;
	}

	public void setCreatorCollection(CreatorCollection creatorCollection) {
		this.creatorCollection = creatorCollection;
	}
	
	

	public String getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(String selectedDate) {
		this.selectedDate = selectedDate;
	}
	
	
	public HtmlSelectOneMenu getDateSelect() {
		return dateSelect;
	}

	public void setDateSelect(HtmlSelectOneMenu dateSelect) {
		this.dateSelect = dateSelect;
	}

	public String getServiceID() {
		return serviceID;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}
	
	

	public UploadedFile getUploadedBibTexFile() {
		return uploadedBibTexFile;
	}

	public void setUploadedBibTexFile(UploadedFile uploadedBibTexFile) {
		this.uploadedBibTexFile = uploadedBibTexFile;
	}

	/**
     * Returns all options for visibility.
     * @return all options for visibility
     */
    public SelectItem[] getVisibilities()
    {
        return this.i18nHelper.getSelectItemsVisibility(false);
    }
    
    /**
     * Returns all options for visibility.
     * @return all options for visibility
     */
    public SelectItem[] getLocatorVisibilities()
    {
        //return ((ApplicationBean) getApplicationBean(ApplicationBean.class)).getSelectItemsVisibility(true);
    	return this.locatorVisibilities;
    }
    
    /**
     * Returns all options for publication language.
     * @return all options for publication language
     */
    public SelectItem[] getPublicationLanguages()
    {
    	return CommonUtils.getLanguageOptions();
    }
    
    /**
     * returns the first language entry of the publication as String
     * @return String the first language entry of the publication as String
     */
    public String getPublicationLanguage()
    {
    	return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLanguages().get(0);
    }
    
    public void setPublicationLanguage(String language)
    {
    	this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLanguages().clear();
    	if (language != null)
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLanguages().add(language);
    	}
    	else
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLanguages().add("");
    	}
    }
    
    /**
     * returns the value of the first abstract of the publication
     * @return String the value of the first abstract of the publication
     */
    public String getAbstract()
    {
    	if(this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts() == null || this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts().size() < 1)
    	{
    		TextVO newAbstract = new TextVO();
    		this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts().add(newAbstract);
    	}
    	return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts().get(0).getValue();
    }
    
    public void setAbstract(String publicationAbstract)
    {
    	TextVO newAbstract = new TextVO();
    	newAbstract.setValue(publicationAbstract);
    	this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts().clear();
    	this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts().add(newAbstract);
    }
    
    public String getSubject()
    {
    	if(this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubject() == null)
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setSubject(new TextVO());
    	}
    	return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubject().getValue();
    }
    
    public void setSubject(String publicationSubject)
    {
    	if(this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubject() == null)
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setSubject(new TextVO());
    	}
    	this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubject().setValue(publicationSubject);
    }
    
    /**
     * Returns all options for content category.
     * @return all options for content category.
     */
    public SelectItem[] getContentCategories()
    {
        return this.i18nHelper.getSelectItemsContentCategory(true);
    }
    
    /**
     * Returns the number of files attached to the current item
     * @return int the number of files
     */
    public int getNumberOfFiles()
	{
		int fileNumber = 0;
		if(this.getEasySubmissionSessionBean().getFiles() != null)
		{
			for(int i = 0; i < this.getEasySubmissionSessionBean().getFiles().size(); i++)
			{
				if(this.getEasySubmissionSessionBean().getFiles().get(i).getFileType().equals(PubFileVOPresentation.FILE_TYPE_FILE))
				{
					fileNumber ++;
				}
			}
		}
		return fileNumber;
	}
    
    /**
     * Returns the number of files attached to the current item
     * @return int the number of files
     */
    public int getNumberOfLocators()
	{
		int locatorNumber = 0;
		if(this.getEasySubmissionSessionBean().getFiles() != null)
		{
			for(int i = 0; i < this.getEasySubmissionSessionBean().getFiles().size(); i++)
			{
				if(this.getEasySubmissionSessionBean().getFiles().get(i).getFileType().equals(PubFileVOPresentation.FILE_TYPE_LOCATOR))
				{
					locatorNumber ++;
				}
			}
		}
		return locatorNumber;
	}
    
    /**
     * This method examines if the user has already selected a context for creating an item. If yes, the 'Next' button will be enabled, otherwise disabled
     * @return boolean Flag if the 'Next' button should be enabled or disabled
     */
    public boolean getDisableNextButton()
    {
    	boolean disableButton = true;
    	int countSelectedContexts = 0;
    	// examine if a context for creating the item has been selected
    	if(this.getContextListSessionBean().getContextList() != null)
    	{
    		for(int i = 0; i < this.getContextListSessionBean().getContextList().size(); i++)
        	{
        		if(this.getContextListSessionBean().getContextList().get(i).getSelected() == true)
        		{
        			countSelectedContexts ++;
        		}
        	}
    	}
    	if(countSelectedContexts > 0)
    	{
    		disableButton = false;
    	}
    	return disableButton;
    }
    public String getSourceTitle()
    {
    	String sourceTitle = "";
    	if(this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources() == null || this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().size() < 1)
    	{
    		this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().add(new SourceVO());
    	}
    	// return the title value oif the first source 
    	sourceTitle = this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0).getTitle().getValue();
    	return sourceTitle;
    }
    
    public void setSourceTitle(String title)
    {
    	this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0).getTitle().setValue(title);
    }
    
    /**
	 * localized creation of SelectItems for the source genres available
	 * @return SelectItem[] with Strings representing source genres
	 */
	public SelectItem[] getSourceGenreOptions()
	{
	    InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
	    ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());

	    SelectItem NO_ITEM_SET = new SelectItem("", bundleLabel.getString("EditItem_NO_ITEM_SET"));
	    SelectItem GENRE_BOOK = new SelectItem(SourceVO.Genre.BOOK, bundleLabel.getString("ENUM_GENRE_BOOK"));
	    SelectItem GENRE_ISSUE = new SelectItem(SourceVO.Genre.ISSUE, bundleLabel.getString("ENUM_GENRE_ISSUE"));
	    SelectItem GENRE_JOURNAL = new SelectItem(SourceVO.Genre.JOURNAL, bundleLabel.getString("ENUM_GENRE_JOURNAL"));
	    SelectItem GENRE_PROCEEDINGS = new SelectItem(SourceVO.Genre.PROCEEDINGS, bundleLabel.getString("ENUM_GENRE_PROCEEDINGS"));
	    SelectItem GENRE_SERIES = new SelectItem(SourceVO.Genre.SERIES, bundleLabel.getString("ENUM_GENRE_SERIES"));
	    return new SelectItem[] { NO_ITEM_SET, GENRE_BOOK, GENRE_ISSUE, GENRE_JOURNAL, GENRE_PROCEEDINGS, GENRE_SERIES };
	}
	
    public SourceVO getSource()
    {
    	SourceVO source = null; 
    	if(this.getItem().getMetadata().getSources() != null && this.getItem().getMetadata().getSources().size() > 0)
    	{
    		source = this.getItem().getMetadata().getSources().get(0);
    	}
    	return source; 
    }
    
    public void setSource(SourceVO source)
    {
    	if(this.getItem().getMetadata().getSources() != null && this.getItem().getMetadata().getSources().size() > 0)
    	{
    		this.getItem().getMetadata().getSources().set(0, source);
    	}
    }

	public HtmlMessages getValMessage()
	{
		return valMessage;
	}

	public void setValMessage(HtmlMessages valMessage)
	{
		this.valMessage = valMessage;
	}

	public boolean getFromEasySubmission()
	{
		return fromEasySubmission;
	}

	public void setFromEasySubmission(boolean fromEasySubmission)
	{
		this.fromEasySubmission = fromEasySubmission;
	}
    
    
}
