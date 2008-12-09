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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.pubman.easySubmission;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.AccessException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

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
import de.mpg.escidoc.pubman.editItem.bean.IdentifierCollection;
import de.mpg.escidoc.pubman.editItem.bean.SourceBean;
import de.mpg.escidoc.pubman.editItem.bean.TitleCollection;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.MetadataHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.metadata.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.common.metadata.MultipleEntriesInBibtexException;
import de.mpg.escidoc.services.common.metadata.NoEntryInBibtexException;
import de.mpg.escidoc.services.common.valueobjects.AdminDescriptorVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.FormatVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.escidoc.services.dataacquisition.DataHandlerBean;
import de.mpg.escidoc.services.dataacquisition.DataSourceHandlerBean;
import de.mpg.escidoc.services.dataacquisition.exceptions.FormatNotAvailableException;
import de.mpg.escidoc.services.dataacquisition.exceptions.SourceNotAvailableException;
import de.mpg.escidoc.services.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.FullTextVO;
import de.mpg.escidoc.services.framework.PropertyReader;
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
    

    //Import Service
    private DataSourceHandlerBean dataSourceHandler = new DataSourceHandlerBean();
    private Vector<DataSourceVO> dataSources = new Vector<DataSourceVO>();       
    // Metadata Service
    private MetadataHandler mdHandler = null;
    // XML Transforming Service
    private XmlTransforming xmlTransforming = null;
    // Validation Service
    private ItemValidating itemValidating = null;
    private HtmlSelectOneRadio radioSelect;
    private HtmlSelectOneMenu dateSelect;
    private HtmlSelectOneMenu sourceSelect = new HtmlSelectOneMenu();
    
    private HtmlSelectOneRadio radioSelectFulltext = new HtmlSelectOneRadio();


	// constants for the submission method
    public SelectItem SUBMISSION_METHOD_MANUAL = new SelectItem("MANUAL", getLabel("easy_submission_method_manual"));
    public SelectItem SUBMISSION_METHOD_FETCH_IMPORT = new SelectItem("FETCH_IMPORT",
            getLabel("easy_submission_method_fetch_import"));
    public SelectItem[] SUBMISSION_METHOD_OPTIONS = new SelectItem[] { this.SUBMISSION_METHOD_MANUAL, this.SUBMISSION_METHOD_FETCH_IMPORT };
    // constants for Date types
    public SelectItem DATE_CREATED = new SelectItem("DATE_CREATED", getLabel("easy_submission_lblDateCreated"));
    public SelectItem DATE_SUBMITTED = new SelectItem("DATE_SUBMITTED", getLabel("easy_submission_lblDateSubmitted"));
    public SelectItem DATE_ACCEPTED = new SelectItem("DATE_ACCEPTED", getLabel("easy_submission_lblDateAccepted"));
    public SelectItem DATE_PUBLISHED_IN_PRINT = new SelectItem("DATE_PUBLISHED_IN_PRINT",
            getLabel("easy_submission_lblDatePublishedInPrint"));
    public SelectItem DATE_PUBLISHED_ONLINE = new SelectItem("DATE_PUBLISHED_ONLINE",
            getLabel("easy_submission_lblDatePublishedOnline"));
    public SelectItem DATE_MODIFIED = new SelectItem("DATE_MODIFIED", getLabel("easy_submission_lblDateModified"));
    public SelectItem[] DATE_TYPE_OPTIONS = new SelectItem[]{this.DATE_CREATED, this.DATE_SUBMITTED, this.DATE_ACCEPTED, this.DATE_PUBLISHED_IN_PRINT, 
    														 this.DATE_PUBLISHED_ONLINE, this.DATE_MODIFIED};
        
    public SelectItem[] EXTERNAL_SERVICE_OPTIONS;
    public SelectItem[] FULLTEXT_OPTIONS;

	public final String INTERNAL_MD_FORMAT = "pubItem";

    // Faces navigation string
    public final static String LOAD_EASYSUBMISSION = "loadEasySubmission";
    private UploadedFile uploadedFile;
    private UIXIterator fileIterator = new UIXIterator();
    private UIXIterator locatorIterator = new UIXIterator();
    private UIXIterator creatorIterator = new UIXIterator();
    public SelectItem[] locatorVisibilities;
    private CreatorCollection creatorCollection;
    private IdentifierCollection identifierCollection;
    private String selectedDate;
    private UploadedFile uploadedBibTexFile;
    private boolean fromEasySubmission = false;

    //Import from external service
    private boolean fulltext = true;
    private final String FULLTEXT_NONE = "NONE";
    private final String FULLTEXT_ALL = "ALL";
    private final String FULLTEXT_DEFAULT ="FORMAT";
    private final String pubsys ="eSciDoc";
    
    /**
     * the ID for the object to fetch by the external service
     */
    private String serviceID;
   
    private String creatorParseString;
    private boolean overwriteCreators;

	private HtmlMessages valMessage = new HtmlMessages();
    private boolean autosuggestJournals = false;
    
    private String suggestConeUrl = null;
    
    private String hiddenAlternativeTitlesField;
    
    private String hiddenIdsField;
    
    private TitleCollection eventTitleCollection;
    
    private UIXIterator identifierIterator;
    

    /**
     * Public constructor.
     */
    public EasySubmission()
    {
        try
        {
            InitialContext initialContext = new InitialContext();
            this.mdHandler = (MetadataHandler)initialContext.lookup(MetadataHandler.SERVICE_NAME);
            this.xmlTransforming = (XmlTransforming)initialContext.lookup(XmlTransforming.SERVICE_NAME);
            this.itemValidating = (ItemValidating)initialContext.lookup(ItemValidating.SERVICE_NAME);
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
        SUBMISSION_METHOD_MANUAL = new SelectItem("MANUAL", getLabel("easy_submission_method_manual"));
        SUBMISSION_METHOD_FETCH_IMPORT = new SelectItem("FETCH_IMPORT", getLabel("easy_submission_method_fetch_import"));
        SUBMISSION_METHOD_OPTIONS = new SelectItem[] { this.SUBMISSION_METHOD_MANUAL, this.SUBMISSION_METHOD_FETCH_IMPORT };
        
        this.locatorVisibilities = this.i18nHelper.getSelectItemsVisibility(true);
        // if the user has reached Step 3, an item has already been created and must be set in the
        // EasySubmissionSessionBean for further manipulation
        if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP2))
        {
            //this.getEasySubmissionSessionBean().setCurrentItem(this.getItemControllerSessionBean().getCurrentPubItem()
            // );
            // bindFiles();
            if (this.getEasySubmissionSessionBean().getFiles() == null)
            {
                // add a locator
                FileVO newLocator = new FileVO();
                newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
                newLocator.setContentCategory(PubFileVOPresentation.ContentCategory.SUPPLEMENTARY_MATERIAL.toString());
                newLocator.setVisibility(FileVO.Visibility.PUBLIC);
                newLocator.setDefaultMetadata(new MdsFileVO());
                newLocator.getDefaultMetadata().setTitle(new TextVO());
                this.getEasySubmissionSessionBean().getLocators().add(new PubFileVOPresentation(0, newLocator, true));
                // add a file
                FileVO newFile = new FileVO();
                newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
                newFile.setVisibility(FileVO.Visibility.PUBLIC);
                newFile.setDefaultMetadata(new MdsFileVO());
                newFile.getDefaultMetadata().setTitle(new TextVO());
                this.getEasySubmissionSessionBean().getFiles().add(new PubFileVOPresentation(0, newFile, false));
            }
            if (this.getEasySubmissionSessionBean().getFiles().size() < 1)
            {
                // add a file
                FileVO newFile = new FileVO();
                newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
                newFile.setVisibility(FileVO.Visibility.PUBLIC);
                newFile.setDefaultMetadata(new MdsFileVO());
                newFile.getDefaultMetadata().setTitle(new TextVO());
                this.getEasySubmissionSessionBean().getFiles().add(new PubFileVOPresentation(0, newFile, false));
            }
            if (this.getEasySubmissionSessionBean().getLocators().size() < 1)
            {
                // add a locator
                FileVO newLocator = new FileVO();
                newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
                newLocator.setContentCategory(PubFileVOPresentation.ContentCategory.SUPPLEMENTARY_MATERIAL.toString());
                newLocator.setVisibility(FileVO.Visibility.PUBLIC);
                newLocator.setDefaultMetadata(new MdsFileVO());
                newLocator.getDefaultMetadata().setTitle(new TextVO());
                this.getEasySubmissionSessionBean().getLocators().add(new PubFileVOPresentation(0, newLocator, true));
            }
        }
        if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP4))
        {
            this.creatorCollection = new CreatorCollection(this.getItem().getMetadata().getCreators());
        }
        
        if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP5))
        {
           
            this.identifierCollection = new IdentifierCollection(this.getItem().getMetadata().getIdentifiers());
            this.eventTitleCollection = new TitleCollection(this.getItem().getMetadata().getEvent());
        }
    	
    	//Get informations about import sources if submission method = fetching import
    	EasySubmissionSessionBean essb = this.getEasySubmissionSessionBean();
        if(this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP2)
    			&&this.getEasySubmissionSessionBean().getCurrentSubmissionMethod().equals("FETCH_IMPORT"))
    	{
    		//Call source initialization only once
    		if (!this.getEasySubmissionSessionBean().isImportSourceRefresh()){
    			this.getEasySubmissionSessionBean().setImportSourceRefresh(true);
    			this.setImportSourcesInfo();
    		}
    	}
    	else {this.getEasySubmissionSessionBean().setImportSourceRefresh(false);}
    	
    	if(getItem() != null && getItem().getMetadata()!=null && getSource()!=null && getSource().getGenre() != null && getSource().getGenre().equals(SourceVO.Genre.JOURNAL))
        {
            this.autosuggestJournals = true;
        }
    	
    	
    }

    public String selectSubmissionMethod()
    {
        String submittedValue = CommonUtils.getUIValue(this.radioSelect);
        // set the desired submission method in the session bean
        EasySubmissionSessionBean easySubmissionSessionBean = (EasySubmissionSessionBean)getSessionBean(EasySubmissionSessionBean.class);
        easySubmissionSessionBean.setCurrentSubmissionMethod(submittedValue);
        // select the default context if only one exists
        ContextListSessionBean contextListSessionBean = (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
        if (contextListSessionBean.getDepositorContextList() != null
                && contextListSessionBean.getDepositorContextList().size() == 1)
        {
            contextListSessionBean.getDepositorContextList().get(0).setSelected(false);
            contextListSessionBean.getDepositorContextList().get(0).selectForEasySubmission();
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
        // clean the EasySubmissionSessionBean
        easySubmissionSessionBean.getFiles().clear();
        easySubmissionSessionBean.getLocators().clear();
        easySubmissionSessionBean.setSelectedDate("");
        // also make sure that the EditItemSessionBean is cleaned, too
        this.getEditItemSessionBean().getFiles().clear();
        this.getEditItemSessionBean().getLocators().clear();
        // deselect the selected context
        ContextListSessionBean contextListSessionBean = (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
        if (contextListSessionBean.getDepositorContextList() != null)
        {
            for (int i = 0; i < contextListSessionBean.getDepositorContextList().size(); i++)
            {
                contextListSessionBean.getDepositorContextList().get(i).setSelected(false);
            }
        }
        // set the current submission step to step2
        easySubmissionSessionBean.setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
        // set method to manual
        easySubmissionSessionBean.setCurrentSubmissionMethod(EasySubmissionSessionBean.SUBMISSION_METHOD_MANUAL);
        return "loadNewEasySubmission";
    }
    
    public String newImport()
    {
        // initialize the collection list first
        this.getContextListSessionBean();
        EasySubmissionSessionBean easySubmissionSessionBean = (EasySubmissionSessionBean)getSessionBean(EasySubmissionSessionBean.class);
        this.getItemControllerSessionBean().setCurrentPubItem(null);
        // clean the EasySubmissionSessionBean
        easySubmissionSessionBean.getFiles().clear();
        easySubmissionSessionBean.getLocators().clear();
        easySubmissionSessionBean.setSelectedDate("");
        // also make sure that the EditItemSessionBean is cleaned, too
        this.getEditItemSessionBean().getFiles().clear();
        this.getEditItemSessionBean().getLocators().clear();
        // deselect the selected context
        ContextListSessionBean contextListSessionBean = (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
        if (contextListSessionBean.getDepositorContextList() != null)
        {
            for (int i = 0; i < contextListSessionBean.getDepositorContextList().size(); i++)
            {
                contextListSessionBean.getDepositorContextList().get(i).setSelected(false);
            }
        }
        // set the current submission step to step2
        easySubmissionSessionBean.setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
        // set method to import
        easySubmissionSessionBean.setCurrentSubmissionMethod(EasySubmissionSessionBean.SUBMISSION_METHOD_FETCH_IMPORT);
        return "loadNewEasySubmission";
    }

    /**
     * This method adds a file to the list of files of the item
     * 
     * @return navigation string (null)
     */
    public String addFile()
    {
        // first try to upload the entered file
        upload(true);
        // then try to save the locator
        saveLocator();
        if (this.getEasySubmissionSessionBean().getFiles() != null
                && this.getEasySubmissionSessionBean().getFiles().size() > 0
                && this.getEasySubmissionSessionBean().getFiles().get(
                        this.getEasySubmissionSessionBean().getFiles().size() - 1).getFile().getDefaultMetadata()
                        .getSize() > 0)
        {
            FileVO newFile = new FileVO();
            newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
            newFile.setVisibility(FileVO.Visibility.PUBLIC);
            newFile.setDefaultMetadata(new MdsFileVO());
            newFile.getDefaultMetadata().setTitle(new TextVO());
            this.getEasySubmissionSessionBean().getFiles().add(
                    new PubFileVOPresentation(this.getEasySubmissionSessionBean().getFiles().size(), newFile, false));
        }
        return "loadNewEasySubmission";
    }

    /**
     * This method adds a locator to the list of files of the item
     * 
     * @return navigation string (null)
     */
    public String addLocator()
    {
        // first try to upload the entered file
        upload(true);
        // then try to save the locator
        saveLocator();
        if (this.getEasySubmissionSessionBean().getLocators() != null
                && this.getEasySubmissionSessionBean().getLocators().get(
                        this.getEasySubmissionSessionBean().getLocators().size() - 1).getFile().getContent() != null
                && !this.getEasySubmissionSessionBean().getLocators().get(
                        this.getEasySubmissionSessionBean().getLocators().size() - 1).getFile().getContent().trim()
                        .equals(""))
        {
            PubFileVOPresentation newLocator = new PubFileVOPresentation(this.getEasySubmissionSessionBean()
                    .getLocators().size(), true);
            // set fixed content type
            newLocator.getFile().setContentCategory(
                    PubFileVOPresentation.ContentCategory.SUPPLEMENTARY_MATERIAL.toString());
            newLocator.getFile().setVisibility(FileVO.Visibility.PUBLIC);
            newLocator.getFile().setDefaultMetadata(new MdsFileVO());
            newLocator.getFile().getDefaultMetadata().setTitle(new TextVO());
            this.getEasySubmissionSessionBean().getLocators().add(newLocator);
        }
        return "loadNewEasySubmission";
    }

    private void bindFiles()
    {
        List<PubFileVOPresentation> files = new ArrayList<PubFileVOPresentation>();
        for (int i = 0; i < this.getItemControllerSessionBean().getCurrentPubItem().getFiles().size(); i++)
        {
            PubFileVOPresentation filepres = new PubFileVOPresentation(i, this.getItemControllerSessionBean()
                    .getCurrentPubItem().getFiles().get(i));
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
        if (this.getFiles() != null && this.getFiles().size() > 0)
        {
            for (int i = 0; i < this.getFiles().size(); i++)
            {
                this.getItem().getFiles().add(this.getFiles().get(i).getFile());
            }
        }
        if (this.getLocators() != null && this.getLocators().size() > 0)
        {
            for (int i = 0; i < this.getLocators().size(); i++)
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
        if (this.getLocators().get(this.getLocators().size() - 1).getFile().getDefaultMetadata().getTitle().getValue() == null
                || this.getLocators().get(this.getLocators().size() - 1).getFile().getDefaultMetadata().getTitle()
                        .getValue().trim().equals(""))
        {
            this.getLocators().get(this.getLocators().size() - 1).getFile().getDefaultMetadata().setTitle(
                    new TextVO(this.getLocators().get(this.getLocators().size() - 1).getFile().getContent()));
        }
        // set a dummy file size for rendering purposes
        if (this.getLocators().get(this.getLocators().size() - 1).getFile().getContent() != null
                && !this.getLocators().get(this.getLocators().size() - 1).getFile().getContent().trim().equals(""))
        {
            this.getLocators().get(this.getLocators().size() - 1).getFile().getDefaultMetadata().setSize(11);
        }
        this.locatorIterator = new UIXIterator();
        return "loadNewEasySubmission";
    }

    /**
     * This method reorganizes the index property in PubFileVOPresentation after removing one element of the list.
     */
    public void reorganizeFileIndexes()
    {
        if (this.getEasySubmissionSessionBean().getFiles() != null)
        {
            for (int i = 0; i < this.getEasySubmissionSessionBean().getFiles().size(); i++)
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
        if (this.getEasySubmissionSessionBean().getLocators() != null)
        {
            for (int i = 0; i < this.getEasySubmissionSessionBean().getLocators().size(); i++)
            {
                this.getEasySubmissionSessionBean().getLocators().get(i).setIndex(i);
            }
        }
    }

    /**
     * Saves the item.
     * 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String save()
    {
        //mapSelectedDate();
        // bind the temporary uploaded files to the files in the current item
        bindUploadedFiles();
        parseAndSetAlternativeSourceTitlesAndIds();
        this.setFromEasySubmission(true);
        //info(getMessage("easy_submission_preview_hint"));
        if (validateStep5("validate") == null)
        {
            return null;
        }
        EditItem editItem = (EditItem)getRequestBean(EditItem.class);
        editItem.setFromEasySubmission(true);
        return (editItem.save());
        // /*
        // * FrM: Validation with validation point "default"
        // */
        // ValidationReportVO report = null;
        // try
        // {
        // PubItemVO itemVO = new PubItemVO(this.getItem());
        // report = this.itemValidating.validateItemObject(itemVO, "default");
        // }
        // catch (Exception e)
        // {
        // throw new RuntimeException("Validation error", e);
        // }
        // logger.debug("Validation Report: " + report);
        //
        // if (report.isValid() && !report.hasItems())
        // {
        //
        // if (logger.isDebugEnabled())
        // {
        // logger.debug("Saving item...");
        // }
        //
        // //String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(DepositorWS.LOAD_DEPOSITORWS,
        // false);
        // this.getItemListSessionBean().setListDirty(true);
        // String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false);
        //
        // if (retVal == null)
        // {
        // this.showValidationMessages(
        // this.getItemControllerSessionBean().getCurrentItemValidationReport());
        // }
        // else if (retVal != null && retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        // {
        // this.showMessage(DepositorWS.MESSAGE_SUCCESSFULLY_SAVED);
        // }
        // return retVal;
        // }
        // else if (report.isValid())
        // {
        // // TODO FrM: Informative messages
        // this.getItemListSessionBean().setListDirty(true);
        // String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false);
        //
        // if (retVal == null)
        // {
        // this.showValidationMessages(
        // this.getItemControllerSessionBean().getCurrentItemValidationReport());
        // }
        // else if (retVal != null && retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        // {
        // this.showMessage(DepositorWS.MESSAGE_SUCCESSFULLY_SAVED);
        // }
        // return retVal;
        // }
        // else
        // {
        // // Item is invalid, do not submit anything.
        // this.showValidationMessages(report);
        // return null;
        // }
    }

    /**
     * Returns the ItemListSessionBean.
     * 
     * @return a reference to the scoped data bean (ItemListSessionBean)
     */
    protected ItemListSessionBean getItemListSessionBean()
    {
        return (ItemListSessionBean)getSessionBean(ItemListSessionBean.class);
    }

    /**
     * Displays validation messages.
     * 
     * @author Michael Franke
     * @param report The Validation report object.
     */
    private void showValidationMessages(ValidationReportVO report)
    {
        for (Iterator<ValidationReportItemVO> iter = report.getItems().iterator(); iter.hasNext();)
        {
            ValidationReportItemVO element = (ValidationReportItemVO)iter.next();
            if (element.isRestrictive())
            {
                error(getMessage(element.getContent()).replaceAll("\\$1", element.getElement()));
            }
            else
            {
                info(getMessage(element.getContent()).replaceAll("\\$1", element.getElement()));
            }
        }
        this.valMessage.setRendered(true);
    }

   

    /**
     * Uploads a file
     * 
     * @param event
     */
    public void fileUploaded(ValueChangeEvent event)
    {
        uploadedFile = (UploadedFile)event.getNewValue();
        upload(true);
        /*
        int indexUpload = this.getEasySubmissionSessionBean().getFiles().size() - 1;
        UploadedFile file = (UploadedFile)event.getNewValue();
        String contentURL;
        if (file != null)
        {
            contentURL = uploadFile(file);
            if (contentURL != null && !contentURL.trim().equals(""))
            {
                FileVO fileVO = this.getEasySubmissionSessionBean().getFiles().get(indexUpload).getFile();
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
        }
        */
    }

    /**
     * This method uploads a selected file and gives out error messages if needed
     * 
     * @param needMessages Flag to invoke error messages (set it to false if you invoke the validation service before or
     *            after)
     * @return String navigation string
     * @author schraut
     */
    public String upload(boolean needMessages)
    {
        StringBuffer errorMessage = new StringBuffer();
        int indexUpload = this.getFiles().size() - 1;
        UploadedFile file = this.uploadedFile;
        String contentURL;
        if (file != null)
        {
            // set the file name automatically if it is not filled by the user
            /*
            if (this.getFiles().get(indexUpload).getFile().getDefaultMetadata().getTitle().getValue() == null
                    || this.getFiles().get(indexUpload).getFile().getDefaultMetadata().getTitle().getValue().trim()
                            .equals(""))
            {
                this.getFiles().get(indexUpload).getFile().getDefaultMetadata()
                        .setTitle(new TextVO(file.getFilename()));
            }
            if (this.getFiles().get(this.getFiles().size() - 1).getContentCategory() != null
                    && !this.getFiles().get(this.getFiles().size() - 1).getContentCategory().trim().equals("")
                    && !this.getFiles().get(this.getFiles().size() - 1).getContentCategory().trim().equals("-"))
            {
            */
                contentURL = uploadFile(file);
                if (contentURL != null && !contentURL.trim().equals(""))
                {
                    this.getFiles().get(indexUpload).getFile().getDefaultMetadata().setTitle(new TextVO(file.getFilename()));
                    this.getFiles().get(indexUpload).getFile().getDefaultMetadata().setSize((int)file.getLength());
                    // set the file name automatically if it is not filled by the user
                    /*
                     * if(this.getFiles().get(indexUpload).getFile().getName() == null ||
                     * this.getFiles().get(indexUpload).getFile().getName().trim().equals("")) {
                     * this.getFiles().get(indexUpload).getFile().setName(file.getFilename()); }
                     */
                    this.getFiles().get(indexUpload).getFile().setMimeType(file.getContentType());
                    
                    FormatVO formatVO = new FormatVO();
                    formatVO.setType("dcterms:IMT");
                    formatVO.setValue(file.getContentType());

                    this.getFiles().get(indexUpload).getFile().getDefaultMetadata().getFormats().add(formatVO);
                    this.getFiles().get(indexUpload).getFile().setContent(contentURL);
                }
                this.init();
            
        }/*
            else
            {
                errorMessage.append(getMessage("ComponentContentCategoryNotProvidedEasySubmission"));
            }
           
        }
        
        else
        {
            if (this.getFiles().get(indexUpload).getFile().getDefaultMetadata().getTitle().getValue() != null
                    && !this.getFiles().get(indexUpload).getFile().getDefaultMetadata().getTitle().getValue().trim()
                            .equals(""))
            {
                errorMessage.append(getMessage("ComponentContentNotProvided"));
                if (this.getFiles().get(indexUpload).getContentCategory() != null
                        && !this.getFiles().get(indexUpload).getContentCategory().trim().equals("")
                        && !this.getFiles().get(indexUpload).getContentCategory().trim().equals("-"))
                {
                    errorMessage.append(getMessage("ComponentContentCategoryNotProvidedEasySubmission"));
                }
            }
        }
        */
        if (errorMessage.length() > 0)
        {
            error(errorMessage.toString());
        }
        return "loadNewEasySubmission";
    }

    /**
     * Uploads a file to the FIZ Framework and recieves and returns the location of the file in the FW
     * 
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
                if (url != null)
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

    /**
     * Uploads a file to the staging servlet and returns the corresponding URL.
     * 
     * @param InputStream to upload
     * @param mimetype The mimetype of the file
     * @param userHandle The userhandle to use for upload
     * @return The URL of the uploaded file.
     * @throws Exception If anything goes wrong...
     */
    protected URL uploadFile(InputStream in, String mimetype, String userHandle) throws Exception
    {
        // Prepare the HttpMethod.
        String fwUrl = de.mpg.escidoc.services.framework.ServiceLocator.getFrameworkUrl();
        PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(in));
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.uploadedBibTexFile
                        .getInputStream()));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    content.append(line + "\n");
                }
            }
            catch (NullPointerException npe)
            {
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
        catch (MultipleEntriesInBibtexException meibe)
        {
            logger.error("Error reading bibtex file", meibe);
            warn(getMessage("easy_submission_bibtex_multiple_entries"));
            return null;
        }
        catch (NoEntryInBibtexException neibe)
        {
            logger.error("Error reading bibtex file", neibe);
            warn(getMessage("easy_submission_bibtex_no_entries"));
            return null;
        }
        catch (Exception e)
        {
            logger.error("Error reading bibtex file", e);
            error(getMessage("easy_submission_bibtex_error"));
            return null;
        }
        return "loadNewEasySubmission";
    }

    /**
     * Handles the import from an external ingestion sources
     */
    public String harvestData()
    {
        FileVO fileVO = new FileVO();
        
        if (EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL.equals(this.getEasySubmissionSessionBean().getImportMethod()))
        {
            if (getServiceID() == null || "".equals(getServiceID())){
                warn(getMessage("easy_submission_external_service_no_id"));
                return null;
            }
            
            String fetchedItem = null;
            String service = this.getEasySubmissionSessionBean().getCurrentExternalServiceType();
            PubItemVO itemVO = null;
            
            //Fetching from eSciDoc is special case which has to be handled different
            if ("escidoc".equals(service.trim().toLowerCase()))
            {
                try
                {
                    String result = ServiceLocator.getItemHandler().retrieve(getServiceID());
                    itemVO = this.xmlTransforming.transformToPubItem(result);
                    getItem().setMetadata(itemVO.getMetadata());
                }
                catch (Exception e) {
                    logger.error("Error fetching from escidoc", e);
                    error(getMessage("easy_submission_escidoc_error"));
                    return null;
                }
            }
            
            //Fetch data from external system
            else
            {       
                DataHandlerBean dataHandler = new DataHandlerBean();
    
                try {
                    //Harvest metadata
                    logger.debug("HarvestData: " + this.getEasySubmissionSessionBean().getCurrentExternalServiceType() + ": "+getServiceID());
                    byte[] fetchedItemByte= dataHandler.doFetch(service,getServiceID(),this.INTERNAL_MD_FORMAT);
                    fetchedItem = new String(fetchedItemByte, 0, fetchedItemByte.length, "UTF8");
                    
                    //Harvest full text
                    if (!CommonUtils.getUIValue(this.getEasySubmissionSessionBean().getRadioSelectFulltext()).equals(this.FULLTEXT_NONE)&& this.getEasySubmissionSessionBean().getRadioSelectFulltext() != null
                            &&!fetchedItem.equals(""))
                    {                   
                        DataSourceVO source = this.dataSourceHandler.getSourceByName(service);
                        Vector<FullTextVO>ftFormats = source.getFtFormats();
                        FullTextVO fulltext = new FullTextVO();
                        Vector<String> formats = new Vector<String>();
                        
                        //Get default full text version from source
                        if (CommonUtils.getUIValue(this.getEasySubmissionSessionBean().getRadioSelectFulltext()).equals(this.FULLTEXT_DEFAULT)){
                            for (int x =0; x< ftFormats.size(); x++)
                            {                
                                fulltext = ftFormats.get(x);
                                if (fulltext.isFtDefault())
                                {   
                                    formats.add(fulltext.getFtLabel());
                                    break;
                                }
                            }                           
                        }

                        //Get all full text versions from source
                        if (CommonUtils.getUIValue(this.getEasySubmissionSessionBean().getRadioSelectFulltext()).equals(this.FULLTEXT_ALL)){
                            
                            for (int x =0; x< ftFormats.size(); x++)
                            {
                                fulltext = ftFormats.get(x);
                                formats.add(fulltext.getFtLabel());
                            }   
                        }
                        
                        String[] arrFormats = new String [formats.size()];
                        byte []ba = dataHandler.doFetch(this.getEasySubmissionSessionBean().getCurrentExternalServiceType(),getServiceID(),formats.toArray(arrFormats));
                        LoginHelper loginHelper = (LoginHelper)this.getBean(LoginHelper.class);
                        ByteArrayInputStream in = new ByteArrayInputStream(ba);
                        URL fileURL = this.uploadFile(in, dataHandler.getContentType(), loginHelper.getESciDocUserHandle());    
                        if (fileURL != null && !fileURL.toString().trim().equals(""))
                        {                           
                            fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);

                            if (dataHandler.getVisibility().equals("PUBLIC"))
                            {
                                fileVO.setVisibility(FileVO.Visibility.PUBLIC);
                            }
                            if (dataHandler.getVisibility().equals("PRIVATE"))
                            {
                                fileVO.setVisibility(FileVO.Visibility.PRIVATE);
                            }
                            fileVO.setDefaultMetadata(new MdsFileVO());
                            fileVO.getDefaultMetadata().setTitle(new TextVO(dataHandler.trimIdentifier(service,getServiceID()).trim()+ dataHandler.getFileEnding()));
                            fileVO.setMimeType(dataHandler.getContentType());
                            fileVO.setName(dataHandler.trimIdentifier(service,getServiceID()).trim()+ dataHandler.getFileEnding());
                            
                            FormatVO formatVO = new FormatVO();
                            formatVO.setType("dcterms:IMT");
                            formatVO.setValue(dataHandler.getContentType());
                            
                            fileVO.getDefaultMetadata().getFormats().add(formatVO);
                            fileVO.setContent(fileURL.toString());
                            fileVO.getDefaultMetadata().setSize(ba.length);
                            fileVO.getDefaultMetadata().setDescription("Data downloaded from "+ service + " at " + CommonUtils.currentDate());
                            fileVO.setContentCategory(dataHandler.getContentCategorie());
                        }
                    }
                }
                
                catch (AccessException inre)
                {
                    logger.error("Error fetching from external import source", inre);       
                    error(getMessage("easy_submission_import_from_external_service_access_denied_error") + getServiceID());            
                    return null;
                }
                catch (IdentifierNotRecognisedException inre)
                {
                    logger.error("Error fetching from external import source", inre);       
                    error(getMessage("easy_submission_import_from_external_service_identifier_error") + getServiceID());            
                    return null;
                }
                catch (SourceNotAvailableException anae)
                {
                    logger.error("Import source currently not available", anae);            
                    long millis = anae.getRetryAfter().getTime() - (new Date()).getTime();
                    if (millis < 1)
                    {
                       millis = 1;
                    }
                    error(getMessage("easy_submission_external_source_not_available_error").replace("$1", Math.ceil(millis / 1000) + ""));             
                    return null;
                }
                catch (FormatNotAvailableException e)
                {
                    error(getMessage("formatNotAvailable_FromFetchingSource").replace("$1", e.getMessage()).replace("$2", service)); 
                    this.getEasySubmissionSessionBean().getRadioSelectFulltext().setValue(this.FULLTEXT_NONE);
                }
                catch (Exception e) {
                    logger.error("Error fetching from external import source", e);              
                    error(getMessage("easy_submission_import_from_external_service_error"));                
                    return null;
                }
                
                //Generate item ValueObject
                if (fetchedItem != null && !fetchedItem.trim().equals(""))
                {   
                    try{
                        itemVO = this.xmlTransforming.transformToPubItem(fetchedItem);       
                        itemVO.getFiles().clear();
                        itemVO.setContext(getItem().getContext());
                        if (!CommonUtils.getUIValue(this.getEasySubmissionSessionBean().getRadioSelectFulltext()).equals(this.FULLTEXT_NONE))
                        {                           
                            itemVO.getFiles().add(fileVO);
                        }                       
                        this.getItemControllerSessionBean().setCurrentPubItem(itemVO);
                        this.setItem(itemVO);
                        
                     }
                     catch(TechnicalException e)
                     {
                         logger.warn("Error transforming item to pubItem.");
                         error(getMessage("easy_submission_import_from_external_service_error"));                
                         return null;
                     }
                 }
                 else 
                 {
                    logger.warn("Empty fetched Item.");
                    error(getMessage("easy_submission_import_from_external_service_error"));                
                    return null;
                 }
            }
        }
        

    	//Fetch data from provided file
    	else if (EasySubmissionSessionBean.IMPORT_METHOD_BIBTEX.equals(this.getEasySubmissionSessionBean().getImportMethod()))
    	{
    		String uploadResult = uploadBibtexFile();
    		if (uploadResult == null)
    		{
    			return null;
    		}
    	}
    	
    	//clear editItem
        this.getEditItemSessionBean().getFiles().clear();
        this.getEditItemSessionBean().getLocators().clear();
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
        //parse hidden source information
        parseAndSetAlternativeSourceTitlesAndIds();
        
        // first try to upload the entered file
        upload(false);
        // then try to save the locator
        saveLocator();
        // save the files and locators in the item in the ItemControllerSessionBean
        this.getItemControllerSessionBean().getCurrentPubItem().getFiles().clear();
        // first add the files
        for (int i = 0; i < this.getEasySubmissionSessionBean().getFiles().size(); i++)
        {
            this.getItemControllerSessionBean().getCurrentPubItem().getFiles().add(
                    this.getEasySubmissionSessionBean().getFiles().get(i).getFile());
        }
        // then add the locators
        for (int i = 0; i < this.getEasySubmissionSessionBean().getLocators().size(); i++)
        {
            this.getItemControllerSessionBean().getCurrentPubItem().getFiles().add(
                    this.getEasySubmissionSessionBean().getLocators().get(i).getFile());
        }
        // add an empty file and an empty locator if necessary for display purposes
        if (this.getEasySubmissionSessionBean().getFiles() != null
                && this.getEasySubmissionSessionBean().getFiles().size() > 0)
        {
            if (this.getEasySubmissionSessionBean().getFiles().get(
                    this.getEasySubmissionSessionBean().getFiles().size() - 1).getFile().getDefaultMetadata().getSize() > 0)
            {
                FileVO newFile = new FileVO();
                newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
                newFile.setVisibility(FileVO.Visibility.PUBLIC);
                newFile.setDefaultMetadata(new MdsFileVO());
                newFile.getDefaultMetadata().setTitle(new TextVO());
                this.getEasySubmissionSessionBean().getFiles()
                        .add(
                                new PubFileVOPresentation(this.getEasySubmissionSessionBean().getFiles().size(),
                                        newFile, false));
            }
        }
        if (this.getEasySubmissionSessionBean().getLocators() != null
                && this.getEasySubmissionSessionBean().getLocators().size() > 0)
        {
            if (this.getEasySubmissionSessionBean().getLocators().get(
                    this.getEasySubmissionSessionBean().getLocators().size() - 1).getFile().getDefaultMetadata()
                    .getSize() > 0)
            {
                PubFileVOPresentation newLocator = new PubFileVOPresentation(this.getEasySubmissionSessionBean()
                        .getLocators().size(), true);
                // set fixed content type
                newLocator.getFile().setContentCategory(
                        PubFileVOPresentation.ContentCategory.SUPPLEMENTARY_MATERIAL.toString());
                newLocator.getFile().setVisibility(FileVO.Visibility.PUBLIC);
                newLocator.getFile().setDefaultMetadata(new MdsFileVO());
                newLocator.getFile().getDefaultMetadata().setTitle(new TextVO());
                this.getEasySubmissionSessionBean().getLocators().add(newLocator);
            }
        }
        // additionally map the dates if the user comes from Step5
        /*
        if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP5))
        {
            mapSelectedDate();
        }
        */
        FacesContext fc = FacesContext.getCurrentInstance();
        // validate
        try
        {
            PubItemVO itemVO = this.getItemControllerSessionBean().getCurrentPubItem();
            ValidationReportVO report = this.itemValidating.validateItemObject(new PubItemVO(itemVO),
                    "easy_submission_step_3");
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
        catch (Exception e)
        {
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
            ValidationReportVO report = this.itemValidating.validateItemObject(new PubItemVO(this
                    .getItemControllerSessionBean().getCurrentPubItem()), "easy_submission_step_4");
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
        catch (Exception e)
        {
            logger.error("Validation error", e);
        }
        
        
        
        this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP5);
        this.init();
        return "loadNewEasySubmission";
    }

    public String loadPreview()
    {
        // Map entered date to entered type
        //mapSelectedDate();
        parseAndSetAlternativeSourceTitlesAndIds();
        // validate
        return validateStep5("loadEditItem");
    }

    private String validateStep5(String navigateTo)
    {
        try
        {
            ValidationReportVO report = this.itemValidating.validateItemObject(new PubItemVO(this
                    .getItemControllerSessionBean().getCurrentPubItem()), "easy_submission_step_5");
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
        catch (Exception e)
        {
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
        if (selectedDateType.equals("DATE_CREATED"))
        {
            this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateCreated(
                    this.getEasySubmissionSessionBean().getSelectedDate());
        }
        else if (selectedDateType.equals("DATE_SUBMITTED"))
        {
            this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateSubmitted(
                    this.getEasySubmissionSessionBean().getSelectedDate());
        }
        else if (selectedDateType.equals("DATE_ACCEPTED"))
        {
            this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateAccepted(
                    this.getEasySubmissionSessionBean().getSelectedDate());
        }
        else if (selectedDateType.equals("DATE_PUBLISHED_ONLINE"))
        {
            this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDatePublishedOnline(
                    this.getEasySubmissionSessionBean().getSelectedDate());
        }
        else if (selectedDateType.equals("DATE_MODIFIED"))
        {
            this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateModified(
                    this.getEasySubmissionSessionBean().getSelectedDate());
        }
        else
        {
            this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDatePublishedInPrint(
                    this.getEasySubmissionSessionBean().getSelectedDate());
        }
    }
    
    /**
     * Fill import source values dynamically from importsourceHandler
     */
    private void setImportSourcesInfo(){
        try {           
            this.dataSources = this.dataSourceHandler.getSources( this.INTERNAL_MD_FORMAT);
            Vector <SelectItem> v_serviceOptions = new Vector<SelectItem>();    
            Vector<FullTextVO> ftFormats = new Vector<FullTextVO>();
                                
            for(int i=0; i< this.dataSources.size();i++){
                DataSourceVO source = (DataSourceVO)this.dataSources.get(i);
                v_serviceOptions.add(new SelectItem (source.getName()));
                this.getEasySubmissionSessionBean().setCurrentExternalServiceType(source.getName());
                        
                //Get full text informations from this source                   
                ftFormats = source.getFtFormats();
                for (int x =0; x< ftFormats.size(); x++)
                {
                    this.setFulltext(true);
                    FullTextVO ft = ftFormats.get(x);
                    if (ft.isFtDefault())
                    {   
                        this.getEasySubmissionSessionBean().setCurrentFTLabel(ft.getFtLabel());
                        this.getEasySubmissionSessionBean().getRadioSelectFulltext().setSubmittedValue(this.FULLTEXT_DEFAULT);
                    }
                }
                if (ftFormats.size()<=0)
                {
                    this.setFulltext(false);
                    this.getEasySubmissionSessionBean().setCurrentFTLabel("");
                }
            }
            v_serviceOptions.add(new SelectItem (this.pubsys));
            this.EXTERNAL_SERVICE_OPTIONS = new SelectItem[v_serviceOptions.size()];
            v_serviceOptions.toArray(this.EXTERNAL_SERVICE_OPTIONS);
            this.getEasySubmissionSessionBean().setEXTERNAL_SERVICE_OPTIONS(this.EXTERNAL_SERVICE_OPTIONS);
            
            if (ftFormats.size()>1)
            {
                this.getEasySubmissionSessionBean().setFULLTEXT_OPTIONS(new SelectItem[]{new SelectItem(this.FULLTEXT_DEFAULT,this.getEasySubmissionSessionBean().getCurrentFTLabel()), 
                                                                        new SelectItem(this.FULLTEXT_ALL,getLabel("easy_submission_lblFulltext_all")) , 
                                                                        new SelectItem(this.FULLTEXT_NONE,getLabel("easy_submission_lblFulltext_none"))});
            }
            else
                this.getEasySubmissionSessionBean().setFULLTEXT_OPTIONS(new SelectItem[]{new SelectItem(this.FULLTEXT_DEFAULT,this.getEasySubmissionSessionBean().getCurrentFTLabel()), 
                                                                        new SelectItem(this.FULLTEXT_NONE,getLabel("easy_submission_lblFulltext_none"))});
        }           
        catch(Exception e){e.printStackTrace();}
    }

    /**
     * Triggered when the selection of the external system is changed
     * Updates full text selection
     * @return String navigation string
     */
    public String changeImportSource()
    {   
        DataSourceVO currentSource = null;
        currentSource = this.dataSourceHandler.getSourceByName(this.sourceSelect.getSubmittedValue().toString());   
        
        //Create dummy currentSource, because we not really fetch from pubsys
        if (currentSource == null){
            currentSource = new DataSourceVO();
        }
        this.getEasySubmissionSessionBean().setCurrentExternalServiceType(currentSource.getName());
        
        Vector<FullTextVO> ftFormats = currentSource.getFtFormats();
        if (ftFormats != null && ftFormats.size()>0){
            for (int x =0; x< ftFormats.size(); x++)
            {
                this.setFulltext(true);
                FullTextVO ft = ftFormats.get(x);
                if (ft.isFtDefault())
                {   
                    this.getEasySubmissionSessionBean().setCurrentFTLabel(ft.getFtLabel());
                    this.getEasySubmissionSessionBean().getRadioSelectFulltext().setSubmittedValue(this.FULLTEXT_DEFAULT);
                }
            }
        }
        
        else {
            this.setFulltext(false);
            this.getEasySubmissionSessionBean().setCurrentFTLabel("");
        }

        if (ftFormats.size()>1)
        {
            this.getEasySubmissionSessionBean().setFULLTEXT_OPTIONS(new SelectItem[]{new SelectItem(this.FULLTEXT_DEFAULT,this.getEasySubmissionSessionBean().getCurrentFTLabel()), 
                                                                    new SelectItem(this.FULLTEXT_ALL,getLabel("easy_submission_lblFulltext_all")) , 
                                                                    new SelectItem(this.FULLTEXT_NONE,getLabel("easy_submission_lblFulltext_none"))});
        }
        else
        {
            this.getEasySubmissionSessionBean().setFULLTEXT_OPTIONS(new SelectItem[]{new SelectItem(this.FULLTEXT_DEFAULT,this.getEasySubmissionSessionBean().getCurrentFTLabel()), 
                                                                    new SelectItem(this.FULLTEXT_NONE,getLabel("easy_submission_lblFulltext_none"))});
        }
            
        this.getEasySubmissionSessionBean().setCurrentExternalServiceType(this.sourceSelect.getSubmittedValue().toString());
        return "loadNewEasySubmission";
    }

    
    /**
     * This method selects the import method 'fetch metadata from external systems'
     * @return String navigation string
     */
    public String selectImportExternal()
    {
    	this.sourceSelect.setSubmittedValue(this.getEasySubmissionSessionBean().getCurrentExternalServiceType());
    	this.changeImportSource();
    	this.getEasySubmissionSessionBean().setImportMethod(EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL);
    	return "loadNewEasySubmission";
    }

    /**
     * This method selects the import method 'Upload Bibtex file'
     * @return String navigation string
     */
    public String selectImportBibtex()
    {
    	this.setFulltext(false);  
    	this.getEasySubmissionSessionBean().setImportMethod(EasySubmissionSessionBean.IMPORT_METHOD_BIBTEX);
    	return "loadNewEasySubmission";
    }

    /**
     * returns a flag which sets the fields of the import method 'fetch metadata from external systems' to disabled or
     * not
     * 
     * @return boolean the flag for disabling
     */
    public boolean getDisableExternalFields()
    {
        boolean disable = false;
        if (this.getEasySubmissionSessionBean().getImportMethod()
                .equals(EasySubmissionSessionBean.IMPORT_METHOD_BIBTEX))
        {
            disable = true;
        }
        return disable;
    }

    /**
     * returns a flag which sets the fields of the import method 'Upload Bibtex file' to disabled or not
     * 
     * @return boolean the flag for disabling
     */
    public boolean getDisableBibtexFields()
    {
        boolean disable = false;
        if (this.getEasySubmissionSessionBean().getImportMethod().equals(
                EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL))
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
        return (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
    }

    /**
     * Returns the EasySubmissionSessionBean.
     * 
     * @return a reference to the scoped data bean (EasySubmissionSessionBean)
     */
    protected EasySubmissionSessionBean getEasySubmissionSessionBean()
    {
        return (EasySubmissionSessionBean)getSessionBean(EasySubmissionSessionBean.class);
    }

    /**
     * Returns the EditItemSessionBean.
     * 
     * @return a reference to the scoped data bean (EditItemSessionBean)
     */
    protected EditItemSessionBean getEditItemSessionBean()
    {
        return (EditItemSessionBean)getSessionBean(EditItemSessionBean.class);
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
     * localized creation of SelectItems for the genres available.
     * 
     * @return SelectItem[] with Strings representing genres.
     */
    public SelectItem[] getGenres()
    {
        List<MdsPublicationVO.Genre> allowedGenres = null;
        List<AdminDescriptorVO> adminDescriptors = this.getItemControllerSessionBean().getCurrentContext()
                .getAdminDescriptors();
        for (AdminDescriptorVO adminDescriptorVO : adminDescriptors)
        {
            if (adminDescriptorVO instanceof PublicationAdminDescriptorVO)
            {
                allowedGenres = ((PublicationAdminDescriptorVO)adminDescriptorVO).getAllowedGenres();
                return this.i18nHelper.getSelectItemsForEnum(true, allowedGenres
                        .toArray(new MdsPublicationVO.Genre[] {}));
            }
        }
        return null;
    }

    public SelectItem[] getSUBMISSION_METHOD_OPTIONS()
    {
        return this.SUBMISSION_METHOD_OPTIONS;
    }

    public void setSUBMISSION_METHOD_OPTIONS(SelectItem[] submission_method_options)
    {
    	this.SUBMISSION_METHOD_OPTIONS = submission_method_options;
    }

    public SelectItem[] getDATE_TYPE_OPTIONS()
    {
        return this.DATE_TYPE_OPTIONS;
    }

    public void setDATE_TYPE_OPTIONS(SelectItem[] date_type_options)
    {
    	this.DATE_TYPE_OPTIONS = date_type_options;
    }

    public SelectItem[] getEXTERNAL_SERVICE_OPTIONS()
    {
        return this.EXTERNAL_SERVICE_OPTIONS;
    }

    public void setEXTERNAL_SERVICE_OPTIONS(SelectItem[] external_service_options)
    {
    	this.EXTERNAL_SERVICE_OPTIONS = external_service_options;
    }

    public HtmlSelectOneRadio getRadioSelect()
    {
        return this.radioSelect;
    }

    public void setRadioSelect(HtmlSelectOneRadio radioSelect)
    {
        this.radioSelect = radioSelect;
    }

    /*
     * public PubItemVO getItem() { return this.getEasySubmissionSessionBean().getCurrentItem(); } public void
     * setItem(PubItemVO item) { this.getEasySubmissionSessionBean().setCurrentItem(item); }
     */
    public PubItemVO getItem()
    {
        return this.getItemControllerSessionBean().getCurrentPubItem();
    }

    public void setItem(PubItemVO item)
    {
        this.getItemControllerSessionBean().setCurrentPubItem(item);
    }

    public List<PubFileVOPresentation> getFiles()
    {
        return this.getEasySubmissionSessionBean().getFiles();
    }

    public List<PubFileVOPresentation> getLocators()
    {
        return this.getEasySubmissionSessionBean().getLocators();
    }

    public void setFiles(List<PubFileVOPresentation> files)
    {
        this.getEasySubmissionSessionBean().setFiles(files);
    }

    public void setLocators(List<PubFileVOPresentation> files)
    {
        this.getEasySubmissionSessionBean().setLocators(files);
    }

    public UploadedFile getUploadedFile()
    {
        return this.uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile)
    {
        this.uploadedFile = uploadedFile;
    }

    public UIXIterator getFileIterator()
    {
        return this.fileIterator;
    }

    public void setFileIterator(UIXIterator fileIterator)
    {
        this.fileIterator = fileIterator;
    }

    public UIXIterator getLocatorIterator()
    {
        return this.locatorIterator;
    }

    public void setLocatorIterator(UIXIterator locatorIterator)
    {
        this.locatorIterator = locatorIterator;
    }

    public CreatorCollection getCreatorCollection()
    {
        return this.creatorCollection;
    }

    public void setCreatorCollection(CreatorCollection creatorCollection)
    {
        this.creatorCollection = creatorCollection;
    }

    public String getSelectedDate()
    {
        return this.selectedDate;
    }

    public void setSelectedDate(String selectedDate)
    {
        this.selectedDate = selectedDate;
    }

    public HtmlSelectOneMenu getDateSelect()
    {
        return this.dateSelect;
    }

    public void setDateSelect(HtmlSelectOneMenu dateSelect)
    {
        this.dateSelect = dateSelect;
    }

    public String getServiceID()
    {
        return this.serviceID;
    }

    public void setServiceID(String serviceID)
    {
        this.serviceID = serviceID;
    }

    public UploadedFile getUploadedBibTexFile()
    {
        return this.uploadedBibTexFile;
    }

    public void setUploadedBibTexFile(UploadedFile uploadedBibTexFile)
    {
        this.uploadedBibTexFile = uploadedBibTexFile;
    }

    /**
     * Returns all options for visibility.
     * 
     * @return all options for visibility
     */
    public SelectItem[] getVisibilities()
    {
        return this.i18nHelper.getSelectItemsVisibility(false);
    }

    /**
     * Returns all options for visibility.
     * 
     * @return all options for visibility
     */
    public SelectItem[] getLocatorVisibilities()
    {
        // return ((ApplicationBean) getApplicationBean(ApplicationBean.class)).getSelectItemsVisibility(true);
        return this.locatorVisibilities;
    }

    /**
     * Returns all options for publication language.
     * 
     * @return all options for publication language
     */
    public SelectItem[] getPublicationLanguages()
    {
        return CommonUtils.getLanguageOptions();
    }

    /**
     * returns the first language entry of the publication as String
     * 
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
     * 
     * @return String the value of the first abstract of the publication
     */
    public String getAbstract()
    {
        if (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts() == null
                || this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts().size() < 1)
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
        if (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubject() == null)
        {
            this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setSubject(new TextVO());
        }
        return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubject().getValue();
    }

    public void setSubject(String publicationSubject)
    {
        if (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubject() == null)
        {
            this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setSubject(new TextVO());
        }
        this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubject().setValue(publicationSubject);
    }

    /**
     * Returns all options for content category.
     * 
     * @return all options for content category.
     */
    public SelectItem[] getContentCategories()
    {
        return this.i18nHelper.getSelectItemsContentCategory(true);
    }

    /**
     * Returns the number of files attached to the current item
     * 
     * @return int the number of files
     */
    public int getNumberOfFiles()
    {
        int fileNumber = 0;
        if (this.getEasySubmissionSessionBean().getFiles() != null)
        {
            /*
            for (int i = 0; i < this.getEasySubmissionSessionBean().getFiles().size(); i++)
            {
                if (this.getEasySubmissionSessionBean().getFiles().get(i).getFileType().equals(
                        PubFileVOPresentation.FILE_TYPE_FILE))
                {
                    fileNumber++;
                }
            }
            */
            fileNumber = this.getEasySubmissionSessionBean().getFiles().size();
        }
        return fileNumber;
    }

    /**
     * Returns the number of files attached to the current item
     * 
     * @return int the number of files
     */
    public int getNumberOfLocators()
    {
        int locatorNumber = 0;
        if (this.getEasySubmissionSessionBean().getFiles() != null)
        {
            /*
            for (int i = 0; i < this.getEasySubmissionSessionBean().getFiles().size(); i++)
            {
                if (this.getEasySubmissionSessionBean().getFiles().get(i).getFileType().equals(
                        PubFileVOPresentation.FILE_TYPE_LOCATOR))
                {
                    locatorNumber++;
                }
            }
            */
            locatorNumber = this.getEasySubmissionSessionBean().getLocators().size();
        }
        return locatorNumber;
    }

    /**
     * This method examines if the user has already selected a context for creating an item. If yes, the 'Next' button
     * will be enabled, otherwise disabled
     * 
     * @return boolean Flag if the 'Next' button should be enabled or disabled
     */
    public boolean getDisableNextButton()
    {
        boolean disableButton = true;
        int countSelectedContexts = 0;
        // examine if a context for creating the item has been selected
        if (this.getContextListSessionBean().getDepositorContextList() != null)
        {
            for (int i = 0; i < this.getContextListSessionBean().getDepositorContextList().size(); i++)
            {
                if (this.getContextListSessionBean().getDepositorContextList().get(i).getSelected() == true)
                {
                    countSelectedContexts++;
                }
            }
        }
        if (countSelectedContexts > 0)
        {
            disableButton = false;
        }
        return disableButton;
    }

    public String getSourceTitle()
    {
        String sourceTitle = "";
        if (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources() == null
                || this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().size() < 1)
        {
            this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().add(new SourceVO());
        }
        // return the title value oif the first source
        sourceTitle = this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0)
                .getTitle().getValue();
        return sourceTitle;
    }

    public void setSourceTitle(String title)
    {
        this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0).getTitle().setValue(
                title);
    }
    
   
    
    public String getSourcePublisher()
    {
      //Create new Publishing Info if not available yet
        SourceVO source = this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0);
        PublishingInfoVO pubVO;
        if(source.getPublishingInfo()==null)
        {
           pubVO = new PublishingInfoVO();
           source.setPublishingInfo(pubVO);
        }
        else
        {
            pubVO = source.getPublishingInfo();
        }
        return pubVO.getPublisher();  
    }
    
    public void setSourcePublisher(String publisher)
    {
        this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0).getPublishingInfo().setPublisher(publisher);
        
    }
    
    
    public String getSourcePublisherPlace()
    {
       
        return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0).getPublishingInfo().getPlace();
    }
    
    public void setSourcePublisherPlace(String place)
    {
        this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0).getPublishingInfo().setPlace(place);
        
    }
    
    public String getSourceIdentifier()
    {
       
       if (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0).getIdentifiers().size()==0)
       {
           IdentifierVO identifier = new IdentifierVO();
           this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0).getIdentifiers().add(identifier);
       }
       return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0).getIdentifiers().get(0).getId();
    }
    
    public void setSourceIdentifier(String id)
    {
        PubItemVO pubItem = this.getItemControllerSessionBean().getCurrentPubItem();
        pubItem.getMetadata().getSources().get(0).getIdentifiers().get(0).setId(id);
        if (!id.trim().equals(""))
        {
            pubItem.getMetadata().getSources().get(0).getIdentifiers().get(0).setType(IdType.OTHER); 
        }
        else
        {
            pubItem.getMetadata().getSources().get(0).getIdentifiers().get(0).setType(null);
        }
        
        
    }

    /**
     * localized creation of SelectItems for the source genres available
     * 
     * @return SelectItem[] with Strings representing source genres
     */
    public SelectItem[] getSourceGenreOptions()
    {
        InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
                .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                        InternationalizationHelper.BEAN_NAME);
        ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
        SelectItem NO_ITEM_SET = new SelectItem("", bundleLabel.getString("EditItem_NO_ITEM_SET"));
        SelectItem GENRE_BOOK = new SelectItem(SourceVO.Genre.BOOK, bundleLabel.getString("ENUM_GENRE_BOOK"));
        SelectItem GENRE_ISSUE = new SelectItem(SourceVO.Genre.ISSUE, bundleLabel.getString("ENUM_GENRE_ISSUE"));
        SelectItem GENRE_JOURNAL = new SelectItem(SourceVO.Genre.JOURNAL, bundleLabel.getString("ENUM_GENRE_JOURNAL"));
        SelectItem GENRE_PROCEEDINGS = new SelectItem(SourceVO.Genre.PROCEEDINGS, bundleLabel
                .getString("ENUM_GENRE_PROCEEDINGS"));
        SelectItem GENRE_SERIES = new SelectItem(SourceVO.Genre.SERIES, bundleLabel.getString("ENUM_GENRE_SERIES"));
        return new SelectItem[] { NO_ITEM_SET, GENRE_BOOK, GENRE_ISSUE, GENRE_JOURNAL, GENRE_PROCEEDINGS, GENRE_SERIES };
    }

    public SourceVO getSource()
    {
        SourceVO source = null;
        if (this.getItem().getMetadata().getSources() != null && this.getItem().getMetadata().getSources().size() > 0)
        {
            source = this.getItem().getMetadata().getSources().get(0);
        }
        return source;
    }

    public void setSource(SourceVO source)
    {
        if (this.getItem().getMetadata().getSources() != null && this.getItem().getMetadata().getSources().size() > 0)
        {
            this.getItem().getMetadata().getSources().set(0, source);
        }
    }

    public HtmlMessages getValMessage()
    {
        return this.valMessage;
    }

    public void setValMessage(HtmlMessages valMessage)
    {
        this.valMessage = valMessage;
    }

    public boolean getFromEasySubmission()
    {
        return this.fromEasySubmission;
    }

    public void setFromEasySubmission(boolean fromEasySubmission)
    {
        this.fromEasySubmission = fromEasySubmission;
    }
    
    public void setCreatorParseString(String creatorParseString)
    {
        this.creatorParseString = creatorParseString;
    }

    public String getCreatorParseString()
    {
        return creatorParseString;
    }
    
    public String addCreatorString()
    {
        try
        {
            EditItem.parseCreatorString(getCreatorParseString(), getCreatorCollection(), getOverwriteCreators());
            setCreatorParseString("");

            return "loadNewEasySubmission";
        }
        catch (Exception e)
        {
            error(getMessage("ErrorParsingCreatorString"));
            return "loadNewEasySubmission";
            
        }
    }
    
    
    

	public boolean isFulltext() {
		return this.fulltext;
	}

	public void setFulltext(boolean fulltext) {
		this.fulltext = fulltext;
	}

	public HtmlSelectOneMenu getSourceSelect() {
		return this.sourceSelect;
	}

	public void setSourceSelect(HtmlSelectOneMenu sourceSelect) {
		this.sourceSelect = sourceSelect;
	}
	
    public SelectItem[] getFULLTEXT_OPTIONS() {
		return this.FULLTEXT_OPTIONS;
	}

	public void setFULLTEXT_OPTIONS(SelectItem[] fulltext_options) {
		this.FULLTEXT_OPTIONS = fulltext_options;
	}

	public HtmlSelectOneRadio getRadioSelectFulltext() {
		return this.radioSelectFulltext;
	}

	public void setRadioSelectFulltext(HtmlSelectOneRadio radioSelectFulltext) {
		this.radioSelectFulltext = radioSelectFulltext;
	}
	
	/*
	public void chooseSourceGenre(ValueChangeEvent event)
    {
        String sourceGenre = event.getNewValue().toString();
        //System.out.println(sourceGenre);
        if(sourceGenre.equals(SourceVO.Genre.JOURNAL.toString()))
        {
            this.setAutosuggestJournals(true);
        }
        
        
    }
	*/
	public String chooseSourceGenre()
    {
        if(this.getSource().getGenre() != null && this.getSource().getGenre().equals(SourceVO.Genre.JOURNAL))
        {
            this.autosuggestJournals = true;
        }
        else 
        {
            this.autosuggestJournals = false;
           }
        return "";
    }
	
	/**
	 * This method returns the URL to the cone autosuggest service read from the properties
	 * @author Tobias Schraut
	 * @return String the URL to the cone autosuggest service
	 * @throws Exception
	 */
	public String getSuggestConeUrl() throws Exception
    {
        if (suggestConeUrl == null)
        {
            suggestConeUrl = PropertyReader.getProperty("escidoc.cone.service.url");
        }
        return suggestConeUrl;
    }

    public void setAutosuggestJournals(boolean autosuggestJournals)
    {
        this.autosuggestJournals = autosuggestJournals;
    }

    public boolean isAutosuggestJournals()
    {
        return autosuggestJournals;
    }
    
    /**
     * Returns all options for degreeType.
     * @return all options for degreeType
     */
    public SelectItem[] getDegreeTypes()
    {
        return this.i18nHelper.getSelectItemsDegreeType(true);
    }

    public UIXIterator getCreatorIterator()
    {
        return creatorIterator;
    }

    public void setCreatorIterator(UIXIterator creatorIterator)
    {
        this.creatorIterator = creatorIterator;
    }

    public void setOverwriteCreators(boolean overwriteCreators)
    {
        this.overwriteCreators = overwriteCreators;
    }

    public boolean getOverwriteCreators()
    {
        return overwriteCreators;
    }

    public void setHiddenAlternativeTitlesField(String hiddenAlternativeTitlesField)
    {
        this.hiddenAlternativeTitlesField = hiddenAlternativeTitlesField;
    }

    public String getHiddenAlternativeTitlesField()
    {
        return hiddenAlternativeTitlesField;
    }

    public void setHiddenIdsField(String hiddenIdsField)
    {
        this.hiddenIdsField = hiddenIdsField;
    }

    public String getHiddenIdsField()
    {
        return hiddenIdsField;
    }
    
    /**
     * Takes the text from the hidden input fields, splits it using the delimiter and adds them to the item.
     * Format of alternative titles: alt title 1 ||##|| alt title 2 ||##|| alt title 3
     * Format of ids: URN|urn:221441 ||##|| URL|http://www.xwdc.de ||##|| ESCIDOC|escidoc:21431
     * @return
     */
    public String parseAndSetAlternativeSourceTitlesAndIds()
    {
        if (getHiddenAlternativeTitlesField() != null && !getHiddenAlternativeTitlesField().trim().equals(""))
        {
            SourceVO source = getSource();
            source.getAlternativeTitles().clear();

            source.getAlternativeTitles().addAll(SourceBean.parseAlternativeTitles(getHiddenAlternativeTitlesField()));
        }
        
        
        if (getHiddenIdsField()!=null && !getHiddenIdsField().trim().equals(""))
        {
           List<IdentifierVO> identifiers = getSource().getIdentifiers();
           identifiers.clear();
           identifiers.addAll(SourceBean.parseIdentifiers(getHiddenIdsField()));  
          
        }
        
        return "";
    }

    public void setIdentifierCollection(IdentifierCollection identifierCollection)
    {
        this.identifierCollection = identifierCollection;
    }

    public IdentifierCollection getIdentifierCollection()
    {
        return identifierCollection;
    }
    
    /**
     * Invitationstatus of event has to be converted as it's an enum that is supposed to be shown in a checkbox.
     * @return true if invitationstatus in VO is set, else false
     */
    public boolean getInvited()
    {
        boolean retVal = false;

        // Changed by FrM: Check for event       
        if (this.getItem().getMetadata().getEvent() != null && this.getItem().getMetadata().getEvent().getInvitationStatus() != null
                && this.getItem().getMetadata().getEvent().getInvitationStatus().equals(EventVO.InvitationStatus.INVITED))
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
            this.getItem().getMetadata().getEvent().setInvitationStatus(EventVO.InvitationStatus.INVITED);
        }
        else
        {
            this.getItem().getMetadata().getEvent().setInvitationStatus(null);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Invitationstatus in VO has been set to: '"
                    + this.getItem().getMetadata().getEvent().getInvitationStatus() + "'");
        }
    }

    public void setEventTitleCollection(TitleCollection eventTitleCollection)
    {
        this.eventTitleCollection = eventTitleCollection;
    }

    public TitleCollection getEventTitleCollection()
    {
        return eventTitleCollection;
    }

    public void setIdentifierIterator(UIXIterator identifierIterator)
    {
        this.identifierIterator = identifierIterator;
    }

    public UIXIterator getIdentifierIterator()
    {
        return identifierIterator;
    }
    
    
}
