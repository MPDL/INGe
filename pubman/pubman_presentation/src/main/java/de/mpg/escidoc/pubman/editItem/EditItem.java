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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.pubman.editItem;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import org.ajax4jsf.component.html.HtmlAjaxRepeat;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.detect.DefaultDetector;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.mpg.escidoc.pubman.DepositorWSPage;
import de.mpg.escidoc.pubman.EditItemPage;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.PubManSessionBean;
import de.mpg.escidoc.pubman.acceptItem.AcceptItem;
import de.mpg.escidoc.pubman.acceptItem.AcceptItemSessionBean;
import de.mpg.escidoc.pubman.affiliation.AffiliationSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.escidoc.pubman.editItem.bean.ContentAbstractCollection;
import de.mpg.escidoc.pubman.editItem.bean.ContentSubjectCollection;
import de.mpg.escidoc.pubman.editItem.bean.IdentifierCollection;
import de.mpg.escidoc.pubman.editItem.bean.IdentifierCollection.IdentifierManager;
import de.mpg.escidoc.pubman.editItem.bean.SourceBean;
import de.mpg.escidoc.pubman.editItem.bean.TitleCollection;
import de.mpg.escidoc.pubman.editItem.bean.TitleCollection.AlternativeTitleManager;
import de.mpg.escidoc.pubman.submitItem.SubmitItem;
import de.mpg.escidoc.pubman.submitItem.SubmitItemSessionBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.GenreSpecificItemManager;
import de.mpg.escidoc.pubman.util.ListItem;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation.WrappedLocalTag;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.pubman.viewItem.bean.FileBean;
import de.mpg.escidoc.pubman.yearbook.YearbookInvalidItemRO;
import de.mpg.escidoc.pubman.yearbook.YearbookItemSessionBean;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.AdminDescriptorVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.FormatVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.SubjectClassification;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * Fragment class for editing PubItems. This class provides all functionality for editing, saving and submitting a
 * PubItem including methods for depending dynamic UI components.
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT:
 *           09.08.2007
 */
public class EditItem extends FacesBean
{
    private static final long serialVersionUID = 1L;
    public static final String BEAN_NAME = "EditItem";
    private static Logger logger = Logger.getLogger(EditItem.class);
    public static final String HIDDEN_DELIMITER = " \\|\\|##\\|\\| ";

    public static final String AUTOPASTE_INNER_DELIMITER = " @@~~@@ ";
    public static final String AUTOPASTE_DELIMITER = " ||##|| ";
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
    private HtmlCommandLink lnkReleaseReleasedItem = new HtmlCommandLink();
    /** pub context name. */
    private String contextName = null;
    // FIXME delegated internal collections
    private TitleCollection titleCollection;
    private String hiddenAlternativeTitlesField;

    private TitleCollection eventTitleCollection;
    private ContentAbstractCollection contentAbstractCollection;
    private ContentSubjectCollection contentSubjectCollection;
    private IdentifierCollection identifierCollection;
    private List<ListItem> languages = null;
    private List<UploadItem> uploadedFile;
    private String locatorUpload;
    private HtmlAjaxRepeat fileIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat pubLangIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat identifierIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat sourceIterator = new HtmlAjaxRepeat();
    private HtmlAjaxRepeat sourceIdentifierIterator = new HtmlAjaxRepeat();
    //private CoreTable fileTable = new CoreTable();
    private PubItemVOPresentation item = null;
    private boolean fromEasySubmission = false;
    private String suggestConeUrl = null;
    private HtmlSelectOneMenu genreSelect = new HtmlSelectOneMenu();
    //private CoreInputFile inputFile = new CoreInputFile();
    // Flag for the binding method to avoid unnecessary binding
    private boolean bindFilesAndLocators = true;

    /**
     * Public constructor.
     */
    public EditItem()
    {
        try
        {
            InitialContext initialContext = new InitialContext();
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
    @Override
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        //this.fileTable = new CoreTable();
        // enables the commandlinks
        this.enableLinks();
        // initializes the (new) item if necessary
        try
        {
            // this.sourceCollection = new SourceCollection(this.getPubItem().getMetadata().getSources());
            this.initializeItem();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error initializing item", e);
        }

        //if item is currently part of invalid yearbook items, show Validation Messages

        if (getItem()==null) return;
        ContextListSessionBean clsb = (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
        LoginHelper loginHelper = (LoginHelper)this.getSessionBean(LoginHelper.class);
        if(getItem().getVersion()!=null && getItem().getVersion().getObjectId()!=null && loginHelper.getIsYearbookEditor())
        {
            YearbookItemSessionBean yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);
            if(yisb.getYearbookItem() != null && yisb.getInvalidItemMap().get(getItem().getVersion().getObjectId()) != null)
            {

                try {
                    //revalidate
                    yisb.validateItem(getItem());
                    YearbookInvalidItemRO invItem = yisb.getInvalidItemMap().get(getItem().getVersion().getObjectId());
                    if(invItem!=null)
                    {
                        (this.getPubItem()).setValidationReport(invItem.getValidationReport());
                    }

                } catch (Exception e) {
                    logger.error("Error in Yaerbook validation", e);
                }
            }

        }

        //      this.getContentSubjectCollection().getContentSubjectManager().getObjectDM().getRowCount();

        // FIXME provide access to parts of my VO to specialized POJO's
        this.titleCollection = new TitleCollection(this.getPubItem().getMetadata());
        this.eventTitleCollection = new TitleCollection(this.getPubItem().getMetadata().getEvent());
        this.contentAbstractCollection = new ContentAbstractCollection(this.getPubItem().getMetadata().getAbstracts());
        this.contentSubjectCollection = new ContentSubjectCollection(this.getPubItem().getMetadata().getSubjects());
        this.identifierCollection = new IdentifierCollection(this.getPubItem().getMetadata().getIdentifiers());
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

    public String getAttributes()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, Object> attributes = context.getViewRoot().getAttributes();
        String result = "";
        for (String key : attributes.keySet())
        {
            result += key + "=" + attributes.get(key) + "; ";
        }
        return result;
    }

    /**
     * Delivers a reference to the currently edited item. This is a shortCut for the method in the ItemController.
     * 
     * @return the item that is currently edited
     */
    public PubItemVOPresentation getPubItem()
    {
        if (this.item == null)
        {
            this.item = this.getItemControllerSessionBean().getCurrentPubItem();
        }
        return this.item;
    }

    public String getContextName()
    {
        if (this.contextName == null && this.getPubItem()!=null)
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
                ((ErrorPage)getSessionBean(ErrorPage.class)).setException(e);
                return ErrorPage.LOAD_ERRORPAGE;
            }
        }
        return this.contextName;
    }

    /**
     * Adds sub-ValueObjects to an item initially to be able to bind uiComponents to them.
     */
    private void initializeItem() throws Exception
    {
        // get the item that is currently edited
        PubItemVO pubItem = this.getPubItem();
        if (pubItem != null)
        {
            // set the default genre to article
            if (pubItem.getMetadata().getGenre() == null)
            {
                pubItem.getMetadata().setGenre(Genre.ARTICLE);
                this.getEditItemSessionBean().setGenreBundle("Genre_" + Genre.ARTICLE.toString());
            }
            else
                // if(this.getEditItemSessionBean().getGenreBundle().trim().equals(""))
            {
                this.getEditItemSessionBean().setGenreBundle("Genre_" + pubItem.getMetadata().getGenre().name());
            }
            this.getItemControllerSessionBean().initializeItem(pubItem);
            if (!this.getEditItemSessionBean().isFilesInitialized()
                    || this.getEditItemSessionBean().getLocators().size() == 0)
            {
                bindFiles();
                this.getEditItemSessionBean().setFilesInitialized(true);
            }
            if (this.getEditItemSessionBean().getSources().size() == 0)
            {
                this.getEditItemSessionBean().bindSourcesToBean(pubItem.getMetadata().getSources());
            }
            if (pubItem.getMetadata() != null && pubItem.getMetadata().getCreators() != null)
            {
                for (CreatorVO creatorVO : pubItem.getMetadata().getCreators())
                {
                    if (creatorVO.getType() == CreatorType.PERSON && creatorVO.getPerson() == null)
                    {
                        creatorVO.setPerson(new PersonVO());
                    }
                    else if (creatorVO.getType() == CreatorType.ORGANIZATION && creatorVO.getOrganization() == null)
                    {
                        creatorVO.setOrganization(new OrganizationVO());
                    }
                    if (creatorVO.getType() == CreatorType.PERSON && creatorVO.getPerson().getOrganizations() != null)
                    {
                        for (OrganizationVO organizationVO : creatorVO.getPerson().getOrganizations())
                        {
                            if (organizationVO.getName() == null)
                            {
                                organizationVO.setName(new TextVO());
                            }
                        }
                    }
                    else if (creatorVO.getType() == CreatorType.ORGANIZATION && creatorVO.getOrganization() != null
                            && creatorVO.getOrganization().getName() == null)
                    {
                        creatorVO.getOrganization().setName(new TextVO());
                    }
                }
            }
            if (this.getEditItemSessionBean().getCreators().size() == 0)
            {
                this.getEditItemSessionBean().bindCreatorsToBean(pubItem.getMetadata().getCreators());
            }
            if (this.getEditItemSessionBean().getCreatorOrganizations().size() == 0)
            {
                this.getEditItemSessionBean().initOrganizationsFromCreators();
            }
            // Source creators
            for (SourceBean sourceBean : this.getEditItemSessionBean().getSources())
            {
                SourceVO source = sourceBean.getSource();
                if (source.getCreators() != null)
                {
                    for (CreatorVO creatorVO : source.getCreators())
                    {
                        if (creatorVO.getType() == CreatorType.PERSON && creatorVO.getPerson() == null)
                        {
                            creatorVO.setPerson(new PersonVO());
                        }
                        else if (creatorVO.getType() == CreatorType.ORGANIZATION && creatorVO.getOrganization() == null)
                        {
                            creatorVO.setOrganization(new OrganizationVO());
                        }
                        if (creatorVO.getType() == CreatorType.PERSON
                                && creatorVO.getPerson().getOrganizations() != null)
                        {
                            for (OrganizationVO organizationVO : creatorVO.getPerson().getOrganizations())
                            {
                                if (organizationVO.getName() == null)
                                {
                                    organizationVO.setName(new TextVO());
                                }
                            }
                        }
                        else if (creatorVO.getType() == CreatorType.ORGANIZATION && creatorVO.getOrganization() != null
                                && creatorVO.getOrganization().getName() == null)
                        {
                            creatorVO.getOrganization().setName(new TextVO());
                        }
                    }
                }
                if (sourceBean.getCreators().size() == 0)
                {
                    sourceBean.bindCreatorsToBean(source.getCreators());
                }
                if (sourceBean.getCreatorOrganizations().size() == 0)
                {
                    sourceBean.initOrganizationsFromCreators();
                }
            }
        }
        else
        {
            logger.warn("Current PubItem is NULL!");
        }
    }

    void bindFiles()
    {
        List<PubFileVOPresentation> files = new ArrayList<PubFileVOPresentation>();
        List<PubFileVOPresentation> locators = new ArrayList<PubFileVOPresentation>();
        int fileCount = 0;
        int locatorCount = 0;
        // add files
        for (int i = 0; i < this.item.getFiles().size(); i++)
        {
            if (this.item.getFiles().get(i).getStorage().equals(FileVO.Storage.INTERNAL_MANAGED))
            {
                PubFileVOPresentation filepres = new PubFileVOPresentation(fileCount, this.item.getFiles().get(i),
                        false);
                files.add(filepres);
                fileCount++;
            }
        }
        this.getEditItemSessionBean().setFiles(files);
        // add locators
        for (int i = 0; i < this.item.getFiles().size(); i++)
        {
            if (this.item.getFiles().get(i).getStorage().equals(FileVO.Storage.EXTERNAL_URL))
            {
                PubFileVOPresentation locatorpres = new PubFileVOPresentation(locatorCount,
                        this.item.getFiles().get(i), true);
                // This is a small hack for locators generated out of Bibtex files
                if (locatorpres.getLocator() == null && locatorpres.getFile() != null
                        && locatorpres.getFile().getName() != null)
                {
                    locatorpres.setLocator(locatorpres.getFile().getName().trim());
                    locatorpres.getFile().getMetadataSets().add(new MdsFileVO());
                    locatorpres.getFile().getDefaultMetadata().setTitle(new TextVO(locatorpres.getFile().getName()));
                }
                // And here it ends
                locators.add(locatorpres);
                locatorCount++;
            }
        }
        this.getEditItemSessionBean().setLocators(locators);
        // make sure that at least one locator and one file is stored in the EditItemSessionBean
       /*
        if (this.getEditItemSessionBean().getFiles().size() < 1)
        {
            FileVO newFile = new FileVO();
            newFile.getMetadataSets().add(new MdsFileVO());
            newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
            this.getEditItemSessionBean().getFiles().add(new PubFileVOPresentation(0, newFile, false));
        }
        */
        if (this.getEditItemSessionBean().getLocators().size() < 1)
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
        if (this.getEditItemSessionBean().getFiles() != null)
        {
            for (int i = 0; i < this.getEditItemSessionBean().getFiles().size(); i++)
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
        if (this.getEditItemSessionBean().getLocators() != null)
        {
            for (int i = 0; i < this.getEditItemSessionBean().getLocators().size(); i++)
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
        if (this.bindFilesAndLocators == true)
        {
            PubItemVOPresentation pubItem = this.getPubItem();
            
            pubItem.getFiles().clear();
            
            // add the files
            List<PubFileVOPresentation> files = this.getFiles();
            
            if (files != null && files.size() > 0)
            {
                for (int i = 0; i < files.size(); i++)
                {
                    pubItem.getFiles().add(files.get(i).getFile());
                }
            }
            // add the locators
            List<PubFileVOPresentation> locators = this.getLocators();
            
            int lsize = locators.size();
            
            logger.debug("found locator: " + lsize);
            
            if (locators != null && lsize > 0)
            {
                for (PubFileVOPresentation loc : locators)
                {
                    // add name from content if not available
                    MdsFileVO defaultMetadata = loc.getFile().getDefaultMetadata();
                    TextVO title = defaultMetadata.getTitle();
                    if (title == null
                            || title.getValue() == null
                            || title.getValue().trim().equals(""))                          
                    {
                        defaultMetadata.setTitle(new TextVO(loc.getFile().getContent()));                                                             
                    }
                    if (defaultMetadata.getDescription() == null
                            || defaultMetadata.getDescription().equals(""))
                    {
                        defaultMetadata.setDescription(loc.getFile().getDescription());                      
                    }
                    
                    // Visibility PUBLIC is static default value for locators
                    loc.getFile().setVisibility(Visibility.PUBLIC);
                    pubItem.getFiles().add(loc.getFile());
                    
                    logger.debug(loc.getFile().getName() + " | " + loc.getFile().getContent());
                    
                    loc.getFile().setName(loc.getFile().getContent());
                }
            }
        }
        else
        {
            this.bindFilesAndLocators = true;
        }
    }

    /**
     * Returns a reference to the scoped data bean (the SubmitItemSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected SubmitItemSessionBean getSubmitItemSessionBean()
    {
        return (SubmitItemSessionBean)getBean(SubmitItemSessionBean.class);
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
     * Uploads a file to the staging servlet and returns the corresponding URL.
     * 
     * @param uploadedFile The file to upload
     * @param mimetype The mimetype of the file
     * @param userHandle The userhandle to use for upload
     * @return The URL of the uploaded file.
     * @throws Exception If anything goes wrong...
     */
    protected URL uploadFile(UploadItem uploadedFile, String mimetype, String userHandle) throws Exception
    {
        // Prepare the HttpMethod.
        String fwUrl = de.mpg.escidoc.services.framework.ServiceLocator.getFrameworkUrl();
        PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
        if(uploadedFile.isTempFile())
        {
        	method.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(uploadedFile.getFile())));
        }
        else
        {
        	method.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(uploadedFile.getData())));
        }
        
        method.setRequestHeader("Content-Type", mimetype);
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        ProxyHelper.setProxy(client, fwUrl);
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

    public List<ListItem> getLanguages() throws Exception
    {
        if (this.languages == null)
        {
            this.languages = new ArrayList<ListItem>();
            if (getPubItem().getMetadata().getLanguages().size() == 0)
            {
                getPubItem().getMetadata().getLanguages().add("");
            }
            int counter = 0;
            for (Iterator<String> iterator = getPubItem().getMetadata().getLanguages().iterator(); iterator.hasNext();)
            {
                String value = iterator.next();
                ListItem item = new ListItem();
                item.setValue(value);
                item.setIndex(counter++);
                item.setStringList(getPubItem().getMetadata().getLanguages());
                item.setItemList(this.languages);
                this.languages.add(item);
            }
        }
        return this.languages;
    }

    public SelectItem[] getLanguageOptions()
    {
        return CommonUtils.getLanguageOptions();
    }

    /**
     * Validates the item.
     * 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String validate()
    {
        if (this.getPubItem()==null) return "";

        try
        {

            if (!restoreVO())
            {
                return "";
            }

            PubItemVO item = this.getPubItem();
            this.getItemControllerSessionBean().validate(item, EditItem.VALIDATIONPOINT_SUBMIT);
            if (this.getItemControllerSessionBean().getCurrentItemValidationReport().hasItems())
            {
                this.showValidationMessages(this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else
            {
                String message = getMessage("itemIsValid");
                info(message);
                this.valMessage.setRendered(true);
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
     * 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String save()
    {
        if (!restoreVO())
        {
            return "";
        }

        // cleanup item according to genre specific MD specification
        GenreSpecificItemManager itemManager = new GenreSpecificItemManager(getPubItem(),
                GenreSpecificItemManager.SUBMISSION_METHOD_FULL);
        try
        {
            this.item = (PubItemVOPresentation) itemManager.cleanupItem();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error while cleaning up item genre specifcly", e);
        }
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
            String retVal = "";
            try
            {
                retVal = this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false);
            }
            catch (RuntimeException rE)
            {
                logger.error("Error saving item", rE);
                String message = getMessage("itemHasBeenChangedInTheMeantime");
                fatal(message);
                this.valMessage.setRendered(true);
            }
            if (retVal == null)
            {
                this.showValidationMessages(this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else if (ViewItemFull.LOAD_VIEWITEM.equals(retVal))
            {
                // set the current submission method to empty string (for GUI purpose)
                this.getEditItemSessionBean().setCurrentSubmission("");
                // redirect to the view item page afterwards (if no error occured)
                try
                {
                    info(getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SAVED));
                    FacesContext fc = FacesContext.getCurrentInstance();
                    HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
                    if (isFromEasySubmission())
                    {
                        fc.getExternalContext().redirect(
                                request.getContextPath()
                                + "/faces/viewItemFullPage.jsp?itemId="
                                + this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                                .getObjectId() + "&fromEasySub=true");
                    }
                    else
                    {
                        fc.getExternalContext().redirect(
                                request.getContextPath()
                                + "/faces/viewItemFullPage.jsp?itemId="
                                + this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                                .getObjectId());
                    }
                }
                catch (IOException e)
                {
                    logger.error("Could not redirect to View Item Page", e);
                }
            }
            return retVal;
        }
        else if (report.isValid())
        {
            String retVal = this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false);
            if (retVal == null)
            {
                this.showValidationMessages(this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else if (ViewItemFull.LOAD_VIEWITEM.equals(retVal))
            {
                // redirect to the view item page afterwards (if no error occured)
                try
                {
                    FacesContext fc = FacesContext.getCurrentInstance();
                    HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
                    if (isFromEasySubmission())
                    {
                        fc.getExternalContext().redirect(
                                request.getContextPath()
                                + "/faces/viewItemFullPage.jsp?itemId="
                                + this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                                .getObjectId() + "&fromEasySub=true");
                    }
                    else
                    {
                        fc.getExternalContext().redirect(
                                request.getContextPath()
                                + "/faces/viewItemFullPage.jsp?itemId="
                                + this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                                .getObjectId());
                    }
                }
                catch (IOException e)
                {
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
     * 
     */
    private boolean restoreVO()
    {
        // bind the temporary uploaded files to the files in the current item
        bindUploadedFilesAndLocators();
        // bind Organizations To Creators
        if (!this.getEditItemSessionBean().bindOrganizationsToCreators())
        {
            return false;
        }
        for (SourceBean sourceBean : getEditItemSessionBean().getSources())
        {
            if (!sourceBean.bindOrganizationsToCreators())
            {
                return false;
            }
        }
        // write creators back to VO
        this.getEditItemSessionBean().bindCreatorsToVO(item.getMetadata().getCreators());
        // write source creators back to VO
        for (SourceBean sourceBean : getEditItemSessionBean().getSources())
        {
            sourceBean.bindCreatorsToVO(sourceBean.getSource().getCreators());
        }
        // write sources back to VO
        this.getEditItemSessionBean().bindSourcesToVO(item.getMetadata().getSources());
        return true;
    }

    /**
     * Saves the item, even if there are informative validation messages.
     * 
     * @author Michael Franke
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String saveAnyway()
    {
        String retVal = "";
        try
        {
            retVal = this.getItemControllerSessionBean().saveCurrentPubItem(
                    MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS, true);
        }
        catch (RuntimeException rE)
        {
            logger.error("Error saving item", rE);
            String message = getMessage("itemHasBeenChangedInTheMeantime");
            fatal(message);
            retVal = EditItem.LOAD_EDITITEM;
            this.valMessage.setRendered(true);
        }
        if (!(this.getItemControllerSessionBean().getCurrentItemValidationReport().isValid()))
        {
            this.showValidationMessages(this.getItemControllerSessionBean().getCurrentItemValidationReport());
        }
        else if (retVal != null && retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        {
            // set the current submission method to empty string (for GUI purpose)
            this.getEditItemSessionBean().setCurrentSubmission("");
            info(getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SAVED));
        }
        // initialize viewItem
        /*
         * this is only neccessary if viewItem should be called after saving; this should stay here as a comment if this
         * might come back once... de.mpg.escidoc.pubman.viewItem.ViewItem viewItem =
         * (de.mpg.escidoc.pubman.viewItem.ViewItem
         * )this.application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "ViewItem");
         * viewItem.setNavigationStringToGoBack(DepositorWS.LOAD_DEPOSITORWS); viewItem.loadItem();
         */
        return retVal;
    }

    /**
     * Submits the item. Should not be used at the moment.
     * 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String submit()
    {
        logger.error("This is a call to \"EditItem.submit\" which should not happen.");
        String retVal = this.getItemControllerSessionBean().submitOrReleaseCurrentPubItem("",
                MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS);
        if (retVal == null)
        {
            this.showValidationMessages(this.getItemControllerSessionBean().getCurrentItemValidationReport());
        }
        else if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        {
            info(getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SUBMITTED));
        }
        return retVal;
    }

    public String saveAndRelease()
    {
        if (!restoreVO())
        {
            return "";
        }

        // cleanup item according to genre specific MD specification
        GenreSpecificItemManager itemManager = new GenreSpecificItemManager(getPubItem(),
                GenreSpecificItemManager.SUBMISSION_METHOD_FULL);
        try
        {
            this.item = (PubItemVOPresentation) itemManager.cleanupItem();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error while cleaning up item genre specificly", e);
        }
        
        // start: check if the item has been changed
        PubItemVO newPubItem = this.getItemControllerSessionBean().getCurrentPubItem();
        PubItemVO oldPubItem = null;
        if (newPubItem.getVersion().getObjectId() != null)
        {
            try
            {
                oldPubItem = this.getItemControllerSessionBean().retrieveItem(newPubItem.getVersion().getObjectId());
            }
            catch (Exception e)
            {
                logger.error("Could not retrieve item." + "\n" + e.toString(), e);
                ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
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
                if (this.getItemControllerSessionBean().saveCurrentPubItem(SubmitItem.LOAD_SUBMITITEM, false) == null)
                {
                    this.showValidationMessages(this.getItemControllerSessionBean().getCurrentItemValidationReport());
                    return null;
                }
                if (this.getItemControllerSessionBean().saveAndSubmitCurrentPubItem(
                        "Submission during saving released item.", SubmitItem.LOAD_SUBMITITEM) == null)
                {
                    this.showValidationMessages(this.getItemControllerSessionBean().getCurrentItemValidationReport());
                    return null;
                }
                try
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(this.getItemControllerSessionBean().retrieveItem(newPubItem.getVersion().getObjectId()));
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Error retrieving submitted item", e);
                }
                return SubmitItem.LOAD_SUBMITITEM;
            }
        }
        return "";
    }

    /**
     * Saves and submits an item.
     * 
     * @return string, identifying the page that should be navigated to after this methodcall Changed by FrM: Inserted
     *         validation and call to "enter submission comment" page.
     */
    public String saveAndSubmit()
    {
        if (!restoreVO())
        {
            return "";
        }

        // start: check if the item has been changed
        PubItemVO newPubItem = this.getItemControllerSessionBean().getCurrentPubItem();
        PubItemVO oldPubItem = null;
        if (newPubItem.getVersion().getObjectId() != null)
        {
            try
            {
                oldPubItem = this.getItemControllerSessionBean().retrieveItem(newPubItem.getVersion().getObjectId());
            }
            catch (Exception e)
            {
                logger.error("Could not retrieve item." + "\n" + e.toString(), e);
                ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
                return ErrorPage.LOAD_ERRORPAGE;
            }
            if (!this.getItemControllerSessionBean().hasChanged(oldPubItem, newPubItem))
            {
                if (newPubItem.getVersion().getState() == ItemVO.State.RELEASED)
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
                    return SubmitItem.LOAD_SUBMITITEM;
                }
            }
        }
        // end: check if the item has been changed
        // cleanup item according to genre specific MD specification
        GenreSpecificItemManager itemManager = new GenreSpecificItemManager(getPubItem(),
                GenreSpecificItemManager.SUBMISSION_METHOD_FULL);
        try
        {
            this.item = (PubItemVOPresentation) itemManager.cleanupItem();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error while cleaning up item genre specifcly", e);
        }
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
        if (report.isValid() && !report.hasItems())
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Submitting item...");
            }
            String retVal = "";
            try
            {
                retVal = this.getItemControllerSessionBean().saveCurrentPubItem(SubmitItem.LOAD_SUBMITITEM, false);
            }
            catch (RuntimeException rE)
            {
                logger.error("Error saving item", rE);
                String message = getMessage("itemHasBeenChangedInTheMeantime");
                fatal(message);
                retVal = EditItem.LOAD_EDITITEM;
                this.valMessage.setRendered(true);
                return retVal;
            }
            if (retVal == null)
            {
                this.showValidationMessages(this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
            {
                // set the current submission method to empty string (for GUI purpose)
                this.getEditItemSessionBean().setCurrentSubmission("");
                getSubmitItemSessionBean().setNavigationStringToGoBack(MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS);
                String localMessage = getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SAVED);
                info(localMessage);
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
                this.showValidationMessages(this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
            {
                // set the current submission method to empty string (for GUI purpose)
                this.getEditItemSessionBean().setCurrentSubmission("");
                getSubmitItemSessionBean().setNavigationStringToGoBack(MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS);
                String localMessage = getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SAVED);
                info(localMessage);
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
     * 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String delete()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Deleting current item...");
        }
        // delete the currently edited item
        String retVal = this.getItemControllerSessionBean().deleteCurrentPubItem(
                MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS);
        // show message in DepositorWS
        if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        {
            info(getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_DELETED));
        }
        return retVal;
    }

    /**
     * Cancels the editing.
     * 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String cancel()
    {
        // examine if the user came from the view Item Page or if he started a new submission
        String navString = ViewItemFull.LOAD_VIEWITEM;
        // set the current submission method to empty string (for GUI purpose)
        this.getEditItemSessionBean().setCurrentSubmission("");
        cleanEditItem();
        if (navString.equals(ViewItemFull.LOAD_VIEWITEM))
        {
            try
            {
                EditItemPage editItemPage = (EditItemPage)getRequestBean(EditItemPage.class);
                FacesContext fc = FacesContext.getCurrentInstance();
                HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
                if ("ViewLocalTagsPage.jsp".equals(editItemPage.getPreviousPageURI()))
                {
                    String viewItemPage = PropertyReader.getProperty("escidoc.pubman.instance.url")
                    + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                    + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceFirst("\\$1",
                            this.getPubItem().getVersion().getObjectId());
                    FacesContext.getCurrentInstance().getExternalContext().redirect(viewItemPage);
                }
                else
                {
                    fc.getExternalContext().redirect(
                            request.getContextPath() + "/faces/" + editItemPage.getPreviousPageURI());
                }
            }
            catch (Exception e)
            {
                logger.error("Could not redirect to View Item Page", e);
            }
        }
        else
        {
            try
            {
                FacesContext.getCurrentInstance().getExternalContext().redirect("faces/SubmissionPage.jsp");
            }
            catch (Exception e)
            {
                logger.error(
                        "Cancel error: could not find context to redirect to SubmissionPage.jsp in Full Submssion", e);
            }
        }
        return navString;
    }

    /**
     * This method cleans up all the helping constructs like collections etc.
     */
    private void cleanEditItem()
    {
        this.item = null;
        this.titleCollection = null;
        this.eventTitleCollection = null;
        this.contentAbstractCollection = null;
        this.contentSubjectCollection = null;
        this.identifierCollection = null;
        this.languages = null;
        this.uploadedFile = null;
        //this.fileTable = null;
    }

    /**
     * Saves and accepts an item.
     * 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String saveAndAccept()
    {
        if (!restoreVO())
        {
            return "";
        }

        // cleanup item according to genre specific MD specification
        GenreSpecificItemManager itemManager = new GenreSpecificItemManager(getPubItem(),
                GenreSpecificItemManager.SUBMISSION_METHOD_FULL);
        try
        {
            this.item = (PubItemVOPresentation) itemManager.cleanupItem();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error while cleaning up item genre specifcly", e);
        }
        ValidationReportVO report = null;
        try
        {
            report = this.itemValidating.validateItemObject(new PubItemVO(getPubItem()),
                    EditItem.VALIDATIONPOINT_ACCEPT);
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
                ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
                return ErrorPage.LOAD_ERRORPAGE;
            }
            if (!this.getItemControllerSessionBean().hasChanged(oldPubItem, newPubItem))
            {
                if (newPubItem.getVersion().getState() == ItemVO.State.RELEASED)
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
                    return AcceptItem.LOAD_ACCEPTITEM;
                }
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Item was changed.");
                }
            }
            String retVal = "";
            // If item is released, submit it additionally (because it is pending after the save)
            try
            {
                if (this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getState()
                        .equals(ItemVO.State.RELEASED))
                {
                    // save the item first manually due to a change in the saveAndSubmitCurrentPubItem method (save
                    // removed there)
                    this.getItemControllerSessionBean().saveCurrentPubItem(AcceptItem.LOAD_ACCEPTITEM, false);
                    retVal = this.getItemControllerSessionBean().saveAndSubmitCurrentPubItem(
                            "Submission during saving released item.", AcceptItem.LOAD_ACCEPTITEM);
                }
                else
                {
                    // only save it
                    retVal = this.getItemControllerSessionBean().saveCurrentPubItem(AcceptItem.LOAD_ACCEPTITEM, false);
                }
            }
            // handle optimistic locking exception
            catch (RuntimeException rE)
            {
                logger.error("Error saving item", rE);
                String message = getMessage("itemHasBeenChangedInTheMeantime");
                fatal(message);
                retVal = EditItem.LOAD_EDITITEM;
                this.valMessage.setRendered(true);
                return retVal;
            }
            if (retVal == null)
            {
                this.showValidationMessages(this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
            {
                // set the current submission method to empty string (for GUI purpose)
                this.getEditItemSessionBean().setCurrentSubmission("");
                getAcceptItemSessionBean().setNavigationStringToGoBack(ViewItemFull.LOAD_VIEWITEM);
                String localMessage = getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SAVED);
                info(localMessage);
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
                this.showValidationMessages(this.getItemControllerSessionBean().getCurrentItemValidationReport());
            }
            else if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
            {
                // set the current submission method to empty string (for GUI purpose)
                this.getEditItemSessionBean().setCurrentSubmission("");
                getAcceptItemSessionBean().setNavigationStringToGoBack(ViewItemFull.LOAD_VIEWITEM);
                String localMessage = getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_ACCEPTED);
                info(localMessage);
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


public String logUploadComplete()
{
	logger.info("upload complete");
	return "";
}
    
    public String uploadFile()
    {
    	for(UploadItem file: getUploadedFile())
    	{
	        //int indexUpload = this.getEditItemSessionBean().getFiles().size() - 1;
	        
	        String contentURL;
	        if (file != null && file.getFileSize() > 0)
	        {
	            contentURL = uploadFile(file);
	            if (contentURL != null && !contentURL.trim().equals(""))
	            {
	            	 FileVO fileVO = new FileVO();
	            	 fileVO.getMetadataSets().add(new MdsFileVO());
	                 fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);
	                 this.getEditItemSessionBean().getFiles() .add(new PubFileVOPresentation(this.getEditItemSessionBean().getFiles().size(), fileVO, false));
	                fileVO.getDefaultMetadata().setSize((int)file.getFileSize());
	                fileVO.setName(file.getFileName());
	                fileVO.getDefaultMetadata().setTitle(new TextVO(file.getFileName()));
	                
	                
	                
                	Tika tika = new Tika();
                	if(file.isTempFile())
                	{
                		try {
							fileVO.setMimeType(tika.detect(new FileInputStream(file.getFile()), file.getFileName()));
						} catch (IOException e) {
							logger.info("Error while trying to detect mimetype of file " + file.getFileName());
						}
                	}
                	else
                	{
                		fileVO.setMimeType(tika.detect(file.getFileName()));
                	}
	                	
	                
	               
	               
	               
	                // correct several PDF Mime type errors manually
	               /*
	                if (file.getFileName() != null
	                        && (file.getFileName().endsWith(".pdf") || file.getFileName().endsWith(".PDF")))
	                {
	                    fileVO.setMimeType("application/pdf");
	                }
	                */
	                FormatVO formatVO = new FormatVO();
	                formatVO.setType("dcterms:IMT");
	                formatVO.setValue(fileVO.getMimeType());
	                fileVO.getDefaultMetadata().getFormats().add(formatVO);
	                fileVO.setContent(contentURL);
	            }
	            // bindFiles();
	        }
	        else
	        {
	            // show error message
	            error(getMessage("ComponentEmpty"));
	        }
    	}
        return"";

    }

    public String uploadFile(UploadItem file)
    {
        String contentURL = "";
        if (file != null)
        {
            try
            {
                // upload the file
                LoginHelper loginHelper = (LoginHelper)this.getSessionBean(LoginHelper.class);
                URL url = null;
                if (loginHelper.getAccountUser().isDepositor())
                {
                    url = this.uploadFile(file, file.getContentType(), loginHelper.getESciDocUserHandle());
                }
                // workarround for moderators who can modify released items but do not have the right to upload files
                else
                {
                    url = this.uploadFile(file, file.getContentType(), AdminHelper.getAdminUserHandle());
                }
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

    public void fileUploaded(UploadEvent event)
    {
        this.uploadedFile = event.getUploadItems();
        uploadFile();
    }

    /*
    public String fileUploaded()
    {
        int indexUpload = this.getEditItemSessionBean().getFiles().size() - 1;
        UploadedFile file = this.uploadedFile;
        String contentURL;
        if (file != null || file.getLength() == 0)
        {
            contentURL = uploadFile(file);
            if (contentURL != null && !contentURL.trim().equals(""))
            {
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
        }
        else
        {
            // show error message
            error(getMessage("ComponentEmpty"));
        }
        return null;
    }
     */
    /**
     * Uploads a file from a given locator.
     */
    public void uploadLocator()
    {
        LocatorUploadBean locatorBean = new LocatorUploadBean();
        boolean check = locatorBean.checkLocator(this.getLocatorUpload());
        if (check)
        {
            locatorBean.locatorUploaded();
        }
        if (locatorBean.getError() != null)
        {
            error(getMessage("errorLocatorMain").replace("$1", locatorBean.getError()));
        }
    }

    /**
     * Preview method for uploaded files
     */
    public void fileDownloaded()
    {
        int index = this.fileIterator.getRowIndex();
        FileVO fileVO = this.getEditItemSessionBean().getFiles().get(index).getFile();
        try
        {
            fileVO.setContent(fileVO.getContent().replaceFirst(ServiceLocator.getFrameworkUrl(), ""));
        }
        catch (ServiceException e)
        {
            e.printStackTrace();
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        FileBean File = new FileBean(fileVO, this.getPubItem().getPublicStatus());
        File.downloadFile();
    }

    /**
     * This method adds a file to the list of files of the item
     * 
     * @return navigation string (null)
     */
    public String addFile()
    {
        // avoid to upload more than one item before filling the metadata
        if (this.getEditItemSessionBean().getFiles() != null)
        {
            FileVO newFile = new FileVO();
            newFile.getMetadataSets().add(new MdsFileVO());
            newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
            this.getEditItemSessionBean().getFiles()
            .add(new PubFileVOPresentation(this.getEditItemSessionBean().getFiles().size(), newFile, false));
        }
        return null;
    }

    /**
     * This method adds a file to the list of files of the item
     * 
     * @return navigation string (null)
     */
    public void addFile(ActionEvent event)
    {
        // avoid to upload more than one item before filling the metadata
        if (this.getEditItemSessionBean().getFiles() != null)
        {
            FileVO newFile = new FileVO();
            newFile.getMetadataSets().add(new MdsFileVO());
            newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
            this.getEditItemSessionBean().getFiles()
            .add(new PubFileVOPresentation(this.getEditItemSessionBean().getFiles().size(), newFile, false));
        }
    }

    /**
     * This method adds a locator to the list of locators of the item
     * 
     * @return navigation string (null)
     */
    public String addLocator()
    {
        if (this.getEditItemSessionBean().getLocators() != null)
        {
            FileVO newLocator = new FileVO();
            newLocator.getMetadataSets().add(new MdsFileVO());
            newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
            this.getEditItemSessionBean()
            .getLocators()
            .add(new PubFileVOPresentation(this.getEditItemSessionBean().getLocators().size(), newLocator, true));
        }
        return null;
    }

    /**
     * This method saves the latest locator to the list of files of the item
     * 
     * @return navigation string (null)
     */
    public String saveLocator()
    {
        int indexUpload = this.getEditItemSessionBean().getLocators().size() - 1;
        if (this.getEditItemSessionBean().getLocators() != null)
        {
            // Set empty MetadataSet if none exists
            if (this.getEditItemSessionBean().getLocators().get(indexUpload).getFile().getDefaultMetadata() == null)
            {
                this.getEditItemSessionBean().getLocators().get(indexUpload).getFile().getMetadataSets()
                .add(new MdsFileVO());
            }
            // Set file name if not filled
            if (this.getEditItemSessionBean().getLocators().get(indexUpload).getFile().getDefaultMetadata().getTitle() == null
                    || this.getEditItemSessionBean().getLocators().get(indexUpload).getFile().getDefaultMetadata()
                    .getTitle().getValue().trim().equals(""))
            {
                this.getEditItemSessionBean()
                .getLocators()
                .get(indexUpload)
                .getFile()
                .getDefaultMetadata()
                .setTitle(
                        new TextVO(this.getEditItemSessionBean().getLocators().get(indexUpload).getFile()
                                .getContent().trim()));
            }
            List<PubFileVOPresentation> list = this.getEditItemSessionBean().getLocators();
            PubFileVOPresentation pubFile = list.get(indexUpload);
            list.set(indexUpload, pubFile);
            this.getEditItemSessionBean().setLocators(list);
        }
        return null;
    }

    /**
     * Retrieves the description of a context from the framework.
     * 
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
     * Retrieves the description of a context to open it in a popup box. This method removes all carriage returns
     * because javascript throws an error if they are present.
     * 
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
        contextDescription = "<div class=\"affDetails\"><div class=\"formField\">" + contextDescription
        + "</div></div>";
        contextDescription = contextDescription.replaceAll("\r?\n", " ");
        return contextDescription;
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected de.mpg.escidoc.pubman.ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (de.mpg.escidoc.pubman.ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the EditItemSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected de.mpg.escidoc.pubman.editItem.EditItemSessionBean getEditItemSessionBean()
    {
        return (EditItemSessionBean)getSessionBean(EditItemSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the PubManSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected static PubManSessionBean getPubManSessionBean()
    {
        return (PubManSessionBean)getSessionBean(PubManSessionBean.class);
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
     * Displays validation messages.
     * 
     * @author Michael Franke
     * @param report The Validation report object.
     */

    private void showValidationMessages(ValidationReportVO report)
    {
        showValidationMessages(this, report);
        this.valMessage.setRendered(true);
    }


    /**
     * Displays validation messages.
     * 
     * @author Michael Franke
     * @param report The Validation report object.
     */
    public void showValidationMessages(FacesBean bean, ValidationReportVO report)
    {
        for (Iterator<ValidationReportItemVO> iter = report.getItems().iterator(); iter.hasNext();)
        {
            ValidationReportItemVO element = iter.next();
            if (element.isRestrictive())
            {
                FacesBean.error(bean.getMessage(element.getContent()).replaceAll("\\$1", element.getElement()));
            }
            else
            {
                FacesBean.info(bean.getMessage(element.getContent()).replaceAll("\\$1", element.getElement()));
            }
        }
        //this.valMessage.setRendered(true);
    }

    /**
     * Enables/Disables the action links.
     */
    private void enableLinks()
    {
        LoginHelper loginHelper = (LoginHelper)getSessionBean(LoginHelper.class);
        boolean isItem = false;
        boolean isWorkflowStandard = false;
        boolean isWorkflowSimple = true;
        boolean isStatePending = true;
        boolean isStateSubmitted = false;
        boolean isStateReleased = false;
        boolean isStateInRevision = false;
        boolean isPublicStateReleased = false;
        boolean itemHasID = this.getPubItem() != null && this.getPubItem().getVersion() != null
        && this.getPubItem().getVersion().getObjectId() != null;
        if (this.getPubItem() != null) isItem=true;
        if (this.getPubItem() != null && this.getPubItem().getVersion() != null
                && this.getPubItem().getVersion().getState() != null)
        {
            isStatePending = this.getPubItem().getVersion().getState().equals(PubItemVO.State.PENDING);
            isStateSubmitted = this.getPubItem().getVersion().getState().equals(PubItemVO.State.SUBMITTED);
            isStateReleased = this.getPubItem().getVersion().getState().equals(PubItemVO.State.RELEASED);
            isStateInRevision = this.getPubItem().getVersion().getState().equals(PubItemVO.State.IN_REVISION);
            isPublicStateReleased = this.getPubItem().getPublicStatus() == PubItemVO.State.RELEASED;
        }
        boolean isModerator = false;
        if (loginHelper.getAccountUser() != null && this.getPubItem() != null)
        {
            isModerator = loginHelper.getAccountUser().isModerator(this.getPubItem().getContext());
        }
        boolean isOwner = true;
        if (this.getPubItem() != null && this.getPubItem().getOwner() != null)
        {
            isOwner = (loginHelper.getAccountUser().getReference() != null ? loginHelper.getAccountUser()
                    .getReference().getObjectId().equals(this.getPubItem().getOwner().getObjectId()) : false);
        }
        try
        {
            if (getItemControllerSessionBean().getCurrentContext() != null
                    && getItemControllerSessionBean().getCurrentContext().getAdminDescriptor() != null)
            {
                isWorkflowStandard = (getItemControllerSessionBean().getCurrentContext().getAdminDescriptor()
                        .getWorkflow() == PublicationAdminDescriptorVO.Workflow.STANDARD);
                isWorkflowSimple = (getItemControllerSessionBean().getCurrentContext().getAdminDescriptor()
                        .getWorkflow() == PublicationAdminDescriptorVO.Workflow.SIMPLE);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Previously uncaught exception", e);
        }
        this.lnkAccept.setRendered(isItem && (isStateSubmitted || isStateReleased) && (isModerator && !isOwner));
        this.lnkRelease.setRendered(isItem && isOwner && ((isWorkflowSimple && (isStatePending || isStateSubmitted))
                || (isWorkflowStandard && isModerator && (isStateSubmitted))));
        this.lnkReleaseReleasedItem.setRendered(isItem && isOwner && isStateReleased && (isWorkflowSimple || (isWorkflowStandard && isModerator)));
        this.lnkDelete.setRendered(isItem && isStatePending && isOwner && itemHasID && !isPublicStateReleased);
        this.lnkSaveAndSubmit.setRendered(isItem && (isStatePending || isStateInRevision || isStateReleased)
                && isWorkflowStandard && isOwner);
        this.lnkSave.setRendered(isItem && ((isStatePending || isStateInRevision) && isOwner)
                || (isStateSubmitted && isModerator));
        /*
         * this.lnkAccept.setRendered(this.isInModifyMode() &&
         * loginHelper.getAccountUser().isModerator(this.getPubItem().getContext()));
         * this.lnkDelete.setRendered(!this.isInModifyMode() && itemHasID);
         * this.lnkSaveAndSubmit.setRendered(!this.isInModifyMode()); this.lnkSave.setRendered(!this.isInModifyMode());
         */
    }

    public boolean getLocalTagEditingAllowed()
    {
        ViewItemFull viewItemFull = (ViewItemFull)getRequestBean(ViewItemFull.class);
        return !viewItemFull.getIsStateWithdrawn()
        && viewItemFull.getIsLatestVersion()
        && ((viewItemFull.getIsModerator() && !viewItemFull.getIsModifyDisabled() && (viewItemFull
                .getIsStateReleased() || viewItemFull.getIsStateSubmitted())) || (viewItemFull.getIsOwner() && (viewItemFull
                        .getIsStatePending() || viewItemFull.getIsStateReleased() || viewItemFull
                        .getIsStateInRevision())));
    }

    public String acceptLocalTags()
    {
        getPubItem().writeBackLocalTags(null);
        if (getPubItem().getVersion().getState().equals(State.RELEASED))
        {
            this.bindFilesAndLocators = false;
            return saveAndAccept();
            /*
             * try { FacesContext fc = FacesContext.getCurrentInstance(); HttpServletRequest request =
             * (HttpServletRequest) fc.getExternalContext().getRequest();
             * fc.getExternalContext().redirect(request.getContextPath() + "/faces/viewItemFullPage.jsp?itemId=" +
             * this.getPubItem().getVersion().getObjectId()); } catch (IOException e) {
             * logger.error("Could not redirect to View Item Page", e); }
             */
        }
        else
        {
            this.bindFilesAndLocators = false;
            save();
        }
        return null;
    }

    /**
     * Evaluates if the EditItem should be in modify mode.
     * 
     * @return true if modify mode should be on
     */
    private boolean isInModifyMode()
    {
        boolean isModifyMode = this.getPubItem().getVersion().getState() != null
        && (this.getPubItem().getVersion().getState().equals(PubItemVO.State.SUBMITTED) || this.getPubItem()
                .getVersion().getState().equals(PubItemVO.State.RELEASED));
        return isModifyMode;
    }

    /**
     * Returns the AffiliationSessionBean.
     * 
     * @return a reference to the scoped data bean (AffiliationSessionBean)
     */
    protected AffiliationSessionBean getAffiliationSessionBean()
    {
        return (AffiliationSessionBean)getSessionBean(AffiliationSessionBean.class);
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
            }
        }
        if (allowedGenres == null)
        {
            allowedGenres= new ArrayList<MdsPublicationVO.Genre>();
        }
        return this.i18nHelper.getSelectItemsForEnum(false,
                allowedGenres.toArray(new MdsPublicationVO.Genre[] {}));

    }

    /**
     * Returns all options for degreeType.
     * 
     * @return all options for degreeType
     */
    public SelectItem[] getDegreeTypes()
    {
        return this.i18nHelper.getSelectItemsDegreeType(true);
    }

    /**
     * Returns all options for reviewMethod.
     * 
     * @return all options for reviewMethod
     */
    public SelectItem[] getReviewMethods()
    {
        return this.i18nHelper.getSelectItemsReviewMethod(true);
    }

    /**
     * Returns all options for content categories.
     * 
     * @return all options for content c ategories.
     */
    public SelectItem[] getContentCategories()
    {
        return this.i18nHelper.getSelectItemsContentCategory(true);
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
        return this.valMessage;
    }

    public void setValMessage(HtmlMessages valMessage)
    {
        this.valMessage = valMessage;
    }

    /**
     * Invitationstatus of event has to be converted as it's an enum that is supposed to be shown in a checkbox.
     * 
     * @return true if invitationstatus in VO is set, else false
     */
    public boolean getInvited()
    {
        boolean retVal = false;
        // Changed by FrM: Check for event
        if (this.getPubItem().getMetadata().getEvent() != null
                && this.getPubItem().getMetadata().getEvent().getInvitationStatus() != null
                && this.getPubItem().getMetadata().getEvent().getInvitationStatus()
                .equals(EventVO.InvitationStatus.INVITED))
        {
            retVal = true;
        }
        return retVal;
    }

    /**
     * Invitationstatus of event has to be converted as it's an enum that is supposed to be shown in a checkbox.
     * 
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
        return this.lnkAccept;
    }

    public void setLnkAccept(HtmlCommandLink lnkAccept)
    {
        this.lnkAccept = lnkAccept;
    }

    public HtmlCommandLink getLnkDelete()
    {
        return this.lnkDelete;
    }

    public void setLnkDelete(HtmlCommandLink lnkDelete)
    {
        this.lnkDelete = lnkDelete;
    }

    public HtmlCommandLink getLnkSave()
    {
        return this.lnkSave;
    }

    public void setLnkSave(HtmlCommandLink lnkSave)
    {
        this.lnkSave = lnkSave;
    }

    public HtmlCommandLink getLnkSaveAndSubmit()
    {
        return this.lnkSaveAndSubmit;
    }

    public void setLnkSaveAndSubmit(HtmlCommandLink lnkSaveAndSubmit)
    {
        this.lnkSaveAndSubmit = lnkSaveAndSubmit;
    }

    public TitleCollection getEventTitleCollection()
    {
        return this.eventTitleCollection;
    }

    public void setEventTitleCollection(TitleCollection eventTitleCollection)
    {
        this.eventTitleCollection = eventTitleCollection;
    }

    public TitleCollection getTitleCollection()
    {
        return this.titleCollection;
    }

    public void setTitleCollection(TitleCollection titleCollection)
    {
        this.titleCollection = titleCollection;
    }

    public ContentAbstractCollection getContentAbstractCollection()
    {
        return this.contentAbstractCollection;
    }

    public void setContentAbstractCollection(ContentAbstractCollection contentAbstractCollection)
    {
        this.contentAbstractCollection = contentAbstractCollection;
    }

    public ContentSubjectCollection getContentSubjectCollection()
    {
        return contentSubjectCollection;
    }

    public void setContentSubjectCollection(ContentSubjectCollection contentSubjectCollection)
    {
        this.contentSubjectCollection = contentSubjectCollection;
    }

    public IdentifierCollection getIdentifierCollection()
    {
        return this.identifierCollection;
    }

    public void setIdentifierCollection(IdentifierCollection identifierCollection)
    {
        this.identifierCollection = identifierCollection;
    }

    public String getPubCollectionName()
    {
        return this.contextName;
    }

    public void setPubCollectionName(String pubCollection)
    {
        this.contextName = pubCollection;
    }

    public List<PubFileVOPresentation> getFiles()
    {
        return this.getEditItemSessionBean().getFiles();
    }

    public void setFiles(List<PubFileVOPresentation> files)
    {
        this.getEditItemSessionBean().setFiles(files);
    }

    public List<PubFileVOPresentation> getLocators()
    {
        return this.getEditItemSessionBean().getLocators();
        
    }

    public void setLocators(List<PubFileVOPresentation> locators)
    {
        this.getEditItemSessionBean().setLocators(locators);
    }

    public List<UploadItem> getUploadedFile()
    {
        return this.uploadedFile;
    }

    public void setUploadedFile(List<UploadItem> uploadedFile)
    {
        this.uploadedFile = uploadedFile;
    }

    /*
    public CoreTable getFileTable()
    {
        return this.fileTable;
    }

    public void setFileTable(CoreTable fileTable)
    {
        this.fileTable = fileTable;
    }

	*/
    public int getNumberOfFiles()
    {
        int fileNumber = 0;
        if (this.getEditItemSessionBean().getFiles() != null)
        {
            fileNumber = this.getEditItemSessionBean().getFiles().size();
        }
        return fileNumber;
    }

    public int getNumberOfLocators()
    {
        int locatorNumber = 0;
        if (this.getEditItemSessionBean().getLocators() != null)
        {
            locatorNumber = this.getEditItemSessionBean().getLocators().size();
        }
        return locatorNumber;
    }

    public EditItemPage getEditItemPage()
    {
        return (EditItemPage)getRequestBean(EditItemPage.class);
    }

    public PubItemVO getItem()
    {
        return this.item;
    }

    public void setItem(PubItemVOPresentation item)
    {
        this.item = item;
    }
    
    public String getOwner() throws Exception
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        InitialContext initialContext = new InitialContext();
        XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
        UserAccountHandler userAccountHandler = null;
        
        HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
        if (this.item.getOwner() != null && this.item.getOwner().getObjectId() != null)
        {
            filterParams.put("operation", new String[] {"searchRetrieve"});
            filterParams.put("query", new String[] {"\"/id\"=" + this.item.getOwner().getObjectId()});
        }
        else 
        {
            return null;
        }
        userAccountHandler = ServiceLocator.getUserAccountHandler(loginHelper.getESciDocUserHandle());
        String searchResponse = userAccountHandler.retrieveUserAccounts(filterParams);
        SearchRetrieveResponseVO searchedObject = xmlTransforming.transformToSearchRetrieveResponseAccountUser(searchResponse);
        if (searchedObject.getRecords().get(0).getData() != null)
        {
            AccountUserVO owner = (AccountUserVO) searchedObject.getRecords().get(0).getData();
            if (owner.getName() != null && owner.getName().trim() != "")
            {
                return owner.getName();
            }
            else if (owner.getUserid() != null && owner.getUserid() != "")
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
    
    public String getCreationDate() 
    {
        if (this.item.getCreationDate() != null)
        {
            return this.item.getCreationDate().toString();
        }
        else {
            return null;
        }
    }
    
    public String getLastModifier() throws Exception
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        InitialContext initialContext = new InitialContext();
        XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
        UserAccountHandler userAccountHandler = null;
        if (this.item.getVersion().getModifiedByRO() != null && this.item.getVersion().getModifiedByRO().getObjectId() != null)
        {
            HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
            filterParams.put("operation", new String[] {"searchRetrieve"});
            filterParams.put("query", new String[] {"\"/id\"=" + this.item.getVersion().getModifiedByRO().getObjectId()});
            String searchResponse = null;
                    
            userAccountHandler = ServiceLocator.getUserAccountHandler(loginHelper.getESciDocUserHandle());
            searchResponse = userAccountHandler.retrieveUserAccounts(filterParams);
            SearchRetrieveResponseVO searchedObject = xmlTransforming.transformToSearchRetrieveResponseAccountUser(searchResponse);
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
    
    public String getLastModificationDate() 
    {
        if (this.item.getModificationDate() != null)
        {
            return this.item.getModificationDate().toString();
        }
        else {
            return null;
        }
    }

    public boolean isFromEasySubmission()
    {
        return this.fromEasySubmission;
    }

    public void setFromEasySubmission(boolean fromEasySubmission)
    {
        this.fromEasySubmission = fromEasySubmission;
    }

    public HtmlCommandLink getLnkRelease()
    {
        return this.lnkRelease;
    }

    public void setLnkRelease(HtmlCommandLink lnkRelease)
    {
        this.lnkRelease = lnkRelease;
    }

    public HtmlCommandLink getLnkReleaseReleasedItem()
    {
        return lnkReleaseReleasedItem;
    }

    public void setLnkReleaseReleasedItem(HtmlCommandLink lnkReleaseReleasedItem)
    {
        this.lnkReleaseReleasedItem = lnkReleaseReleasedItem;
    }

    public String addCreatorString()
    {
        try
        {
            EditItemSessionBean eisb = getEditItemSessionBean();
            eisb.parseCreatorString(eisb.getCreatorParseString(), null, eisb.getOverwriteCreators());
            eisb.initAuthorCopyPasteCreatorBean();
            return null;
        }
        catch (Exception e)
        {
            logger.error("Could not parse creator string", e);
            error(getMessage("ErrorParsingCreatorString"));
            return null;
        }
    }

    /**
     * Checks if there are any subject classifications defined for this item.
     * 
     * @return true if ther is at least one subject classification.
     * @throws Exception Any exception.
     */
    public boolean getHasSubjectClassification() throws Exception
    {
        return !(getSubjectTypes() == null);
    }

    /**
     * Get all allowed subject classifications from the admin descriptor of the context.
     * 
     * @return An array of SelectItem containing the subject classifications.
     * @throws Exception Any exception.
     */
    public SelectItem[] getSubjectTypes() throws Exception
    {
        ArrayList<SelectItem> result = new ArrayList<SelectItem>();
        ContextRO contextRO = getPubItem().getContext();
        ContextListSessionBean contextListSessionBean = (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
        for (PubContextVOPresentation context : contextListSessionBean.getDepositorContextList())
        {
            if (context.getReference().equals(contextRO))
            {
                PublicationAdminDescriptorVO adminDescriptorVO = context.getAdminDescriptor();
                List<SubjectClassification> list = adminDescriptorVO.getAllowedSubjectClassifications();
                if(list != null)
                {
                    for (SubjectClassification classification : list)
                    {
                        SelectItem selectItem = new SelectItem(classification.name(), classification.name().replace("_","-"));
                        result.add(selectItem);
                    }
                    return result.toArray(new SelectItem[] {});
                }
            }
        }
        return null;
    }

    /**
     * This method changes the Genre and sets the needed property file for genre specific Metadata
     * 
     * @return String null
     */
    public String changeGenre()
    {
        String newGenre = getItem().getMetadata().getGenre().name();
        Genre[] possibleGenres = MdsPublicationVO.Genre.values();
        for (int i = 0; i < possibleGenres.length; i++)
        {
            if (possibleGenres[i].toString().equals(newGenre))
            {
                this.item.getMetadata().setGenre(possibleGenres[i]);
                this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setGenre(possibleGenres[i]);
            }
        }
        if (newGenre != null && newGenre.trim().equals(""))
        {
            newGenre = "ARTICLE";
        }
        this.getEditItemSessionBean().setGenreBundle("Genre_" + newGenre);
        this.init();
        return null;
    }

    /**
     * Adds a new local tag to the PubItemVO and a new wrapped local tag to PubItemVOPresentation.
     * 
     * @return Returns always null.
     */
    public String addLocalTag()
    {
        WrappedLocalTag wrappedLocalTag = this.getPubItem().new WrappedLocalTag();
        wrappedLocalTag.setParent(this.getPubItem());
        wrappedLocalTag.setValue("");
        this.getPubItem().getWrappedLocalTags().add(wrappedLocalTag);
        this.getPubItem().writeBackLocalTags(null);
        return null;
    }

    public String loadEditLocalTags()
    {
        getEditItemSessionBean().clean();

        return "loadEditLocalTags";
    }

    public HtmlAjaxRepeat getFileIterator()
    {
        return this.fileIterator;
    }

    public void setFileIterator(HtmlAjaxRepeat fileIterator)
    {
        this.fileIterator = fileIterator;
    }

    public String getSuggestConeUrl() throws Exception
    {
        if (suggestConeUrl == null)
        {
            suggestConeUrl = PropertyReader.getProperty("escidoc.cone.service.url");
        }
        return suggestConeUrl;
    }

    public void setCcScriptTag(String ccScriptTag)
    {
    }

    public void setSuggestConeUrl(String suggestConeUrl)
    {
        this.suggestConeUrl = suggestConeUrl;
    }

    public HtmlAjaxRepeat getPubLangIterator()
    {
        return pubLangIterator;
    }

    public void setPubLangIterator(HtmlAjaxRepeat pubLangIterator)
    {
        this.pubLangIterator = pubLangIterator;
    }

    public HtmlAjaxRepeat getIdentifierIterator()
    {
        return identifierIterator;
    }

    public void setIdentifierIterator(HtmlAjaxRepeat identifierIterator)
    {
        this.identifierIterator = identifierIterator;
    }

    public HtmlAjaxRepeat getSourceIterator()
    {
        return sourceIterator;
    }

    public void setSourceIterator(HtmlAjaxRepeat sourceIterator)
    {
        this.sourceIterator = sourceIterator;
    }

    public HtmlAjaxRepeat getSourceIdentifierIterator()
    {
        return sourceIdentifierIterator;
    }

    public void setSourceIdentifierIterator(HtmlAjaxRepeat sourceIdentifierIterator)
    {
        this.sourceIdentifierIterator = sourceIdentifierIterator;
    }

    public HtmlSelectOneMenu getGenreSelect()
    {
        return genreSelect;
    }

    public void setGenreSelect(HtmlSelectOneMenu genreSelect)
    {
        this.genreSelect = genreSelect;
    }

    /*
    public CoreInputFile getInputFile()
    {
        return inputFile;
    }

    public void setInputFile(CoreInputFile inputFile)
    {
        this.inputFile = inputFile;
    }
*/
    public String getGenreBundle()
    {
        // return genreBundle;
        return this.getEditItemSessionBean().getGenreBundle();
    }

    public void setGenreBundle(String genreBundle)
    {
        // this.genreBundle = genreBundle;
        this.getEditItemSessionBean().setGenreBundle(genreBundle);
    }

    public String getLocatorUpload()
    {
        return locatorUpload;
    }

    public void setLocatorUpload(String locatorUpload)
    {
        this.locatorUpload = locatorUpload;
    }

    public void setHiddenAlternativeTitlesField(String hiddenAlternativeTitlesField)
    {
        this.hiddenAlternativeTitlesField = hiddenAlternativeTitlesField;
    }

    public String getHiddenAlternativeTitlesField()
    {
        return hiddenAlternativeTitlesField;
    }

    /**
     * Takes the text from the hidden input fields, splits it using the delimiter and adds them to the model. Format of
     * alternative titles: alt title 1 ||##|| alt title 2 ||##|| alt title 3 Format of ids: URN|urn:221441 ||##||
     * URL|http://www.xwdc.de ||##|| ESCIDOC|escidoc:21431
     * 
     * @return
     */
    public String parseAndSetAlternativeTitles()
    {
        //clear old alternative titles
        AlternativeTitleManager altTitleManager = getTitleCollection().getAlternativeTitleManager();
        altTitleManager.getObjectList().clear();

        //clear old identifiers
        IdentifierManager idManager = getIdentifierCollection().getIdentifierManager();
        idManager.getObjectList().clear();

        if (!getHiddenAlternativeTitlesField().trim().equals(""))
        {
            altTitleManager.getObjectList().addAll(parseAlternativeTitles(getHiddenAlternativeTitlesField()));
        }
        return "";
    }

    public static List<TextVO> parseAlternativeTitles(String titleList)
    {
        List<TextVO> list = new ArrayList<TextVO>();
        String[] alternativeTitles = titleList.split(HIDDEN_DELIMITER);
        for (int i = 0; i < alternativeTitles.length; i++)
        {
            String[] parts = alternativeTitles[i].trim().split(AUTOPASTE_INNER_DELIMITER);
            String alternativeTitleType = parts[0].trim();
            String alternativeTitle = parts[1].trim();
            if (!alternativeTitle.equals(""))
            {
                TextVO textVO = new TextVO(alternativeTitle);
                textVO.setType(alternativeTitleType);
                list.add(textVO);
            }
        }
        return list;
    }
}
