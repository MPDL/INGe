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

package de.mpg.escidoc.pubman;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.createItem.CreateItem;
import de.mpg.escidoc.pubman.desktop.Login;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.pubman.util.RelationVOPresentation;
import de.mpg.escidoc.services.common.DataGathering;
import de.mpg.escidoc.services.common.EmailHandling;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.VersionHistoryEntryVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.interfaces.ItemContainerSearchResultVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.ItemExporting;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.pubman.PubItemPublishing;
import de.mpg.escidoc.services.pubman.PubItemSimpleStatistics;
import de.mpg.escidoc.services.pubman.QualityAssurance;
import de.mpg.escidoc.services.pubman.util.AdminHelper;
import de.mpg.escidoc.services.search.ItemContainerSearch;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
import de.mpg.escidoc.services.search.query.StandardSearchResult;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * Handles all actions on/with items, calls to the framework.
 * 
 * @author: Thomas Diebäcker, created 25.04.2007
 * @version: $Revision: 1691 $ $LastChangedDate: 2007-12-18 09:30:58 +0100 (Di, 18 Dez 2007) $
 * Revised by DiT: 14.08.2007
 */
public class ItemControllerSessionBean extends FacesBean
{

    private static final long serialVersionUID = 8235607890711998557L;

    public static final String BEAN_NAME = "ItemControllerSessionBean";
    private static Logger logger = Logger.getLogger(ItemControllerSessionBean.class);

    private LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
    
    private PubItemDepositing pubItemDepositing = null;
    private PubItemPublishing pubItemPublishing = null;
    private QualityAssurance qualityAssurance = null;
    private ItemContainerSearch itemContainerSearch = null;
    private XmlTransforming xmlTransforming = null;
    private ItemValidating itemValidating = null;
    private ItemExporting itemExporting = null;
    private EmailHandling emailHandling = null;
    private DataGathering dataGathering = null;
    private ValidationReportVO currentItemValidationReport = null;
    private PubItemVO currentPubItem = null;
    private ContextVO currentContext = null;
    private PubItemSimpleStatistics pubItemStatistic = null;
    
    private static final String PROPERTY_CONTENT_MODEL = 
        "escidoc.framework_access.content-model.id.publication";


    /**
     * Public constructor, initializing used Beans.
     */
    public ItemControllerSessionBean()
    {
        try
        {
            InitialContext initialContext = new InitialContext();

            // initialize used Beans
            this.pubItemDepositing = (PubItemDepositing) initialContext.lookup(PubItemDepositing.SERVICE_NAME);
            this.pubItemPublishing = (PubItemPublishing) initialContext.lookup(PubItemPublishing.SERVICE_NAME);
            this.itemContainerSearch = (ItemContainerSearch) initialContext.lookup(ItemContainerSearch.SERVICE_NAME);
            this.xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            this.itemValidating = (ItemValidating) initialContext.lookup(ItemValidating.SERVICE_NAME);
            this.itemExporting = (ItemExporting) initialContext.lookup(ItemExporting.SERVICE_NAME);
            this.emailHandling = (EmailHandling) initialContext.lookup(EmailHandling.SERVICE_NAME);
            this.dataGathering = (DataGathering) initialContext.lookup(DataGathering.SERVICE_NAME);
            this.qualityAssurance = (QualityAssurance) initialContext.lookup(QualityAssurance.SERVICE_NAME);
            this.pubItemStatistic  =
                (PubItemSimpleStatistics) initialContext.lookup(PubItemSimpleStatistics.SERVICE_NAME);
        }
        catch (NamingException e)
        {
            logger.error("ItemControllerSessionBean Initialization Failure", e);
        }

        this.init();

    }

    /**
     * This method is called when this bean is initially added to session scope. Typically, this occurs as a result of
     * evaluating a value binding or method binding expression, which utilizes the managed bean facility to instantiate
     * this bean and store it into session scope.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }

    /**
     * Creates a new PubItem and handles navigation afterwards.
     * @param navigationRuleWhenSuccessful the navigation rule
     * which should be returned when the operation is successful.
     * @param pubContextRO The eSciDoc context.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String createNewPubItem(final String navigationRuleWhenSuccessful, final ContextRO pubContextRO)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Creating a new PubItem.");
        }

        try
        {
            // creating a new item
            PubItemVO newPubItem = this.createNewPubItem(pubContextRO);

            // setting the returned item as new currentItem
            this.setCurrentPubItem(newPubItem);
        }
        catch (Exception e)
        {
            logger.error("Could not create item." + "\n" + e.toString(), e);
            ((ErrorPage) getSessionBean(ErrorPage.class)).setException(e);

            return ErrorPage.LOAD_ERRORPAGE;
        }

        return navigationRuleWhenSuccessful;
    }

    /**
     * Saves a PubItem and handles navigation afterwards.
     * @param navigationRuleWhenSuccessfull the navigation rule which should be returned
     * when the operation is successful.
     * @param ignoreInformativeMessages indicates if the system should save the item
     * even if there are informative validation messages.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String saveCurrentPubItem(final String navigationRuleWhenSuccessfull,
            final boolean ignoreInformativeMessages)
    {
        PubItemVO newPubItem = null;

        try
        {
            if (this.currentPubItem == null)
            {
                TechnicalException technicalException = new TechnicalException("No current PubItem is set.");
                throw technicalException;
            }

            // saving the current item
            newPubItem = this.savePubItem(this.currentPubItem, ignoreInformativeMessages);

            // Item invalid, therefore not saved
            if (newPubItem == this.currentPubItem)
            {
                return null;
            }

            // setting the returned item as new currentItem
            this.setCurrentPubItem(newPubItem);
        }
        catch (Exception e)
        {
            logger.error("Could not save item." + "\n" + e.toString(), e);
            ((ErrorPage) getSessionBean(ErrorPage.class)).setException(e);

            return ErrorPage.LOAD_ERRORPAGE;
        }

        return navigationRuleWhenSuccessfull;
    }

    /**
     * Submits a PubItem and handles navigation afterwards.
     * @param navigationRuleWhenSuccessfull the navigation rule which
     * should be returned when the operation is successful.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String submitOrReleaseCurrentPubItem(
            final String submissionComment,
            final String navigationRuleWhenSuccessfull)
    {
        try
        {
            if (this.currentPubItem == null)
            {
                TechnicalException technicalException = new TechnicalException("No current PubItem is set.");
                throw technicalException;
            }

            // submitting the current item
            ItemRO pubItemRO = this.submitOrReleasePubItem(this.currentPubItem, submissionComment, true);
            
            if (pubItemRO == this.currentPubItem.getVersion())
            {
                return null;
            }
            
        }
        catch (Exception e)
        {
            logger.error("Could not submit item." + "\n" + e.toString());
            logger.error(e);
            ((ErrorPage) getSessionBean(ErrorPage.class)).setException(e);
            
            return ErrorPage.LOAD_ERRORPAGE;
        }
        
        return navigationRuleWhenSuccessfull;
    }
    
    /**
     * Submits a PubItem and handles navigation afterwards.
     * 
     * @param submissionComment A comment
     * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when
     * the operation is successful.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String saveAndSubmitOrReleaseCurrentPubItem(String submissionComment, String navigationRuleWhenSuccessfull)
    {
        try
        {
            if (this.currentPubItem == null)
            {
                TechnicalException technicalException = new TechnicalException("No current PubItem is set.");
                throw technicalException;
            }

            // submitting the current item
            ItemRO pubItemRO = this.saveAndSubmitOrReleasePubItem(this.currentPubItem, submissionComment, true);
            
            if (pubItemRO == this.currentPubItem.getVersion())
            {
                return null;
            }
            
        }
        catch (Exception e)
        {
            logger.error("Could not submit item." + "\n" + e.toString());
            ((ErrorPage) getSessionBean(ErrorPage.class)).setException(e);
            
            return ErrorPage.LOAD_ERRORPAGE;
        }
        
        return navigationRuleWhenSuccessfull;
    }
    
    /**
     * Submits a PubItem and handles navigation afterwards.
     * 
     * @param withdrawalComment A comment
     * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when
     * the operation is successful.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String withdrawCurrentPubItem(String navigationRuleWhenSuccessfull, String withdrawalComment)
    {
        try
        {
            if (this.currentPubItem == null)
            {
                TechnicalException technicalException = new TechnicalException("No current PubItem is set.");
                throw technicalException;
            }

            // submitting the current item
            this.withdrawPubItem(this.currentPubItem, withdrawalComment);
        }
        catch (Exception e)
        {
            logger.error("Could not withdraw item." + "\n" + e.toString());
            ((ErrorPage) getSessionBean(ErrorPage.class)).setException(e);
            
            return ErrorPage.LOAD_ERRORPAGE;
        }
        
        return navigationRuleWhenSuccessfull;
    }
    
    /**
     * Submits a list of PubItems and handles navigation afterwards.
     * @param pubItemList list with pubItems to submit
     * @param submissionComment A comment
     * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when
     * the operation is successful.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    // //TODO NBU: remove this method
    public String submitOrReleasePubItemList(ArrayList<PubItemVO> pubItemList, String submissionComment, String navigationRuleWhenSuccessfull)
    {
        boolean allSubmitted = true;
        if (pubItemList.size() > 0)
        {
            for (int i = 0; i < pubItemList.size(); i++)
            {
                try
                {
                    // submitting the item
                    ItemRO pubItemRO = this.submitOrReleasePubItem(pubItemList.get(i), submissionComment, true);
                    if (pubItemRO == pubItemList.get(i).getVersion()) {
                        allSubmitted = false;
                    }
                }
                catch (Exception e)
                {
                    logger.error("Could not submit item." + "\n" + e.toString());
                    ((ErrorPage) getSessionBean(ErrorPage.class)).setException(e);
                    
                    return ErrorPage.LOAD_ERRORPAGE;
                }
            }
        }
        else
        {
            logger.warn("No item selected.");
        }
        
        if (!allSubmitted) {
            return null;
        }
        
        return navigationRuleWhenSuccessfull;
    }

    /**
     * Deletes a PubItem and handles navigation afterwards.
     * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when the operation is successfull
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String deleteCurrentPubItem(String navigationRuleWhenSuccessfull)
    {
        try
        {
            if (this.currentPubItem == null)
            {
                TechnicalException technicalException = new TechnicalException("No current PubItem is set.");
                throw technicalException;
            }

            if (this.currentPubItem.getVersion() == null)
            {
                // if the item has not been saved before, there is no need to delete it
                logger.warn("Tried to delete an unsaved item. Do nothing instead.");
            }
            else
            {
                // delete the current item
                this.deletePubItem(this.currentPubItem.getVersion());
            }
        }
        catch (Exception e)
        {
            logger.error("Could not delete item." + "\n" + e.toString());
            ((ErrorPage) getSessionBean(ErrorPage.class)).setException(e);
            
            return ErrorPage.LOAD_ERRORPAGE;
        }
        
        // Reload list
        ((ItemListSessionBean) getSessionBean(ItemListSessionBean.class)).init();
        
        return navigationRuleWhenSuccessfull;
    }
    
    /**
     * Deletes a list of PubItems and handles navigation afterwards.
     * @param pubItemList list with pubItems to delete
     * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when the operation is successfull
     * @return string, identifying the page that should be navigated to after this methodcall
     */
  //TODO NBU: remove this method
    public String deletePubItemList(List<PubItemVOPresentation> pubItemList, String navigationRuleWhenSuccessfull)
    {
        if (pubItemList.size() > 0)
        {
            for (int i=0; i<pubItemList.size(); i++)
            {
                try
                {
                    // deleting the item
                    this.deletePubItem(pubItemList.get(i).getVersion());
                }
                catch (Exception e)
                {
                    logger.error("Could not submit item." + "\n" + e.toString());
                    ((ErrorPage) getSessionBean(ErrorPage.class)).setException(e);
                    
                    return ErrorPage.LOAD_ERRORPAGE;
                }
            }
        }
        else
        {
            logger.warn("No item selected.");
        }
        
        return navigationRuleWhenSuccessfull;
    }
    
    /**
     * Creates a new Revision of a PubItem and handles navigation afterwards.
     * @param navigationRuleWhenSuccessfull the navigation rule which should be returned when the operation is successfull
     * @param pubContextRO the context for the new revision
     * @param pubItem the base item for the new revision
     * @param revisionDescription the description for the new revision
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String createNewRevision(String navigationRuleWhenSuccessfull, ContextRO pubContextRO, PubItemVO pubItem, String revisionDescription)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Creating a new Revision of item with ID: " + pubItem.getVersion().getObjectId()
                    + " for context with ID: " + pubContextRO.getObjectId()
                    + " with revision description: " + revisionDescription);
        }

        try
        {
            // creating a new item
            PubItemVO newRevision = this.createNewRevision(pubContextRO, pubItem, revisionDescription);

            initializeItem(newRevision);
            
            // setting the returned item as new currentItem
            this.setCurrentPubItem(newRevision);
        }
        catch (Exception e)
        {
            logger.error("Could not create revision." + "\n" + e.toString());
            ((ErrorPage) getSessionBean(ErrorPage.class)).setException(e);
            
            return ErrorPage.LOAD_ERRORPAGE;
        }
        
        return navigationRuleWhenSuccessfull;
    }

    /**
     * Creates a new Revision.
     * @param pubContextRO the context for the new revision
     * @param pubItem the base item for the new revision
     * @param revisionDescription the description for the new revision
     * @return the new PubItem
     */
    private PubItemVO createNewRevision(ContextRO pubContextRO, PubItemVO pubItem, String revisionDescription) throws Exception
    {
        this.getItemListSessionBean().setListDirty(true);
        // create the new item
        PubItemVO newRevision = this.pubItemDepositing.createRevisionOfItem(pubItem, revisionDescription, pubContextRO, loginHelper.getAccountUser());
                    
        return newRevision;
    }


    /**
     * Redirects the user to the create new revision page
     * Changed by DiT, 29.11.2007: only show contexts when user has privileges for more than one context
     * 
     * @return Sring nav rule to load the create new revision page
     */
    public String createItemFromTemplate()
    {
        // clear the list of  locators and files when start creating  a new revision
    	EditItemSessionBean editItemSessionBean = this.getEditItemSessionBean();
        editItemSessionBean.getFiles().clear();
        editItemSessionBean.getLocators().clear();
        
    	// Changed by DiT, 29.11.2007: only show contexts when user has privileges for more than one context
        // if there is only one context for this user we can skip the CreateItem-Dialog and create the new item directly
        if (this.getContextListSessionBean().getDepositorContextList().size() == 0)
        {
            logger.warn("The user does not have privileges for any context.");
            error(getMessage("ViewItemFull_user_has_no_context"));
            return null;
        }
        
        PubItemVO newItem = new PubItemVO(this.getCurrentPubItem());
        newItem.getVersion().setObjectId(null);
        newItem.getVersion().setVersionNumber(0);
        newItem.getVersion().setState(ItemVO.State.PENDING);
        newItem.getFiles().clear();
        //clear the relation list according to PUBMAN-357
        if(newItem.getRelations() != null)
        {
        	newItem.getRelations().clear();
        }
        
        
        this.setCurrentPubItem(newItem);
        
        if (this.getContextListSessionBean().getDepositorContextList().size() == 1)
        {            
            ContextVO context = this.getContextListSessionBean().getDepositorContextList().get(0);
            
            newItem.setContext(context.getReference());
            
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has only privileges for one collection (ID: " 
                        + context.getReference().getObjectId() + ")");
            }
            
            return EditItem.LOAD_EDITITEM;
        }
        else
        {
            // more than one context exists for this user; let him choose the right one
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has privileges for " + this.getContextListSessionBean().getDepositorContextList().size() 
                        + " different contexts.");
            }

            newItem.setContext(null);
            
            return CreateItem.LOAD_CREATEITEM;
        }
    }
    
    private ContextListSessionBean getContextListSessionBean() {
		return (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
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
     * Creates a new PubItem.
     * @return the new PubItem
     */
    private PubItemVO createNewPubItem(ContextRO pubContextRO) throws Exception
    {
        if (logger.isDebugEnabled())
        {
           logger.debug("Creating a new PubItem for Collection with ID: " + pubContextRO.getObjectId());
        }
        
        this.getItemListSessionBean().setListDirty(true);
        
        // create the new item
        PubItemVO newPubItem = this.pubItemDepositing.createPubItem(pubContextRO, loginHelper.getAccountUser());
                    
        // initialize the new item
        newPubItem = this.initializeItem(newPubItem);
        
        return new PubItemVOPresentation(newPubItem);
    }
   
    /** 
     * Initializes a new item with ValueObjects.
     * FrM: Changes to be able to initialize an item
     * created as a new revision of an existing item.
     * 
     * @return the initialized item.
     */
    public PubItemVO initializeItem(PubItemVO newPubItem)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize the item...");
        }
        
        // version
        if (newPubItem.getVersion() == null)
        {
        	ItemRO version = new ItemRO();
        	newPubItem.setVersion(version);
        }
        
        // Status
        if (newPubItem.getVersion().getState() == null)
        {
        	newPubItem.getVersion().setState(ItemVO.State.PENDING);
        }
        
        // Title
        if (newPubItem.getMetadata().getTitle() == null)
        {
        	newPubItem.getMetadata().setTitle(new TextVO());
        }
        // File
//        if (newPubItem.getFiles().size() == 0)
//        {
//        	newPubItem.getFiles().add(new FileVO());
//        }
        // Creator
        if (newPubItem.getMetadata().getCreators().size() == 0)
        {
        	CreatorVO newCreator = new CreatorVO();
            // create a new Organization for this person
            PersonVO newPerson = new PersonVO();
            OrganizationVO newPersonOrganization = new OrganizationVO();
            newPerson.getOrganizations().add(newPersonOrganization);
            
            newCreator.setOrganization(null);
            newCreator.setPerson(newPerson);
            newPubItem.getMetadata().getCreators().add(newCreator);
        }
        // Publishing info
        if (newPubItem.getMetadata().getPublishingInfo() == null)
        {
        	newPubItem.getMetadata().setPublishingInfo(new PublishingInfoVO());
        }
        
        // Identifiers
        if (newPubItem.getMetadata().getIdentifiers().size() == 0)
        {
        	newPubItem.getMetadata().getIdentifiers().add(new IdentifierVO());
        }
        
        // Abstracts
        if (newPubItem.getMetadata().getAbstracts().size() == 0)
        {
        	newPubItem.getMetadata().getAbstracts().add(new TextVO());
        }

        // Language
        if (newPubItem.getMetadata().getLanguages().size() == 0)
        {
        	newPubItem.getMetadata().getLanguages().add("");
        }
        // Source
        if (newPubItem.getMetadata().getSources().size() == 0)
        {
        	SourceVO newSource = new SourceVO();
        	newPubItem.getMetadata().getSources().add(newSource);
        }
        for (SourceVO source : newPubItem.getMetadata().getSources())
        {
			
	        if (source.getTitle() == null)
	        {
	        	TextVO newSourceTitle = new TextVO();
	        	source.setTitle(newSourceTitle);
	        }
	        if (source.getPublishingInfo() == null)
	        {
	        	PublishingInfoVO newSourcePublishingInfo = new PublishingInfoVO(); 
	        	source.setPublishingInfo(newSourcePublishingInfo);
	        }
	        if (source.getCreators().size() == 0)
	        {
	        	CreatorVO newSourceCreator = new CreatorVO();
	            // create a new Organization for this person
	            PersonVO newPerson = new PersonVO();
	            OrganizationVO newPersonOrganization = new OrganizationVO();
	            newPersonOrganization.setName(new TextVO());
	            newPerson.getOrganizations().add(newPersonOrganization);
	            
	            newSourceCreator.setOrganization(null);
	            newSourceCreator.setPerson(newPerson);
	
		        source.getCreators().add(newSourceCreator);
			}
	        if (source.getIdentifiers().size() == 0)
	        {
	        	source.getIdentifiers().add(new IdentifierVO());
	        }
        }
        // Event
        // add Event if needed to be able to bind uiComponents to it
        if (newPubItem.getMetadata().getEvent() == null)
        {
            EventVO eventVO = new EventVO();
            newPubItem.getMetadata().setEvent(eventVO);
        }
        if (newPubItem.getMetadata().getEvent().getTitle() == null)
        {
        	newPubItem.getMetadata().getEvent().setTitle(new TextVO());
        }
        if (newPubItem.getMetadata().getEvent().getPlace() == null)
        {
        	newPubItem.getMetadata().getEvent().setPlace(new TextVO());
        }
        // add subject if needed to be able to bind uiComponents to it
        if (newPubItem.getMetadata().getSubject() == null)
        {
        	newPubItem.getMetadata().setSubject(new TextVO());
        }
        //add TOC if needed to be able to bind uiComponents to it
        if (newPubItem.getMetadata().getTableOfContents() == null)
        {
        	newPubItem.getMetadata().setTableOfContents(new TextVO());
        }
        
        return newPubItem;
    }
    
    /**
     * Saves a PubItem to the framework.
     * @param pubItem the PubItem to save
     * @param ignoreInformativeMessages indicates, if the system should save the item even if there are informative validation messages.
     * @return the PubItem returned by the framework
     */
    private PubItemVO savePubItem(PubItemVO pubItemToSave, boolean ignoreInformativeMessages) throws Exception
    {
        // work with a clone of the metadata so the item can be cleaned up before saving and the item in 
        // EditItem stays in the same (uncleaned) state so we can continue editing when saving fails for 
        // some reason
        PubItemVO pubItem = (PubItemVO)pubItemToSave.clone();
        
        if (logger.isDebugEnabled())
        {
            if (pubItem != null && pubItem.getVersion() != null && pubItem.getVersion().getObjectId() != null)
            {
                logger.debug("Saving PubItem: " + pubItem.getVersion().getObjectId());
            }
            else
            {
                logger.debug("Saving a new PubItem.");
            }
        }        
        
        /*
         * FrM: Validation with validation point "default"
         */
        ValidationReportVO report = this.itemValidating.validateItemObject(pubItem);
        currentItemValidationReport = report;

        logger.debug("Validation Report: " + report);
        
        if (report.isValid() && !report.hasItems())
        {
        
            this.getItemListSessionBean().setListDirty(true);
            // Item is totally valid, save immediately.
            
            // clean up the item from unused sub-VOs
            this.cleanUpItem(pubItem);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Saving item...");
            }
    
            // save the item
            PubItemVO newPubItem = this.pubItemDepositing.savePubItem(pubItem, loginHelper.getAccountUser());
    
            return newPubItem;            
        }
        else if (report.isValid())
        {
            // Item is valid, but has informative messages.
            this.getItemListSessionBean().setListDirty(true);
            
            if (ignoreInformativeMessages)
            {
                // clean up the item from unused sub-VOs
                this.cleanUpItem(pubItem);
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Saving item...");
                }
        
                // save the item
                PubItemVO newPubItem = this.pubItemDepositing.savePubItem(pubItem, loginHelper.getAccountUser());
        
                return newPubItem;
            }
            
            return pubItem;         
        }
        else
        {           
            // Item is invalid, do not save anything.
            currentItemValidationReport = report;
            return pubItem;         
        }
    }

    /**
     * Submits a PubItem to the framework.
     * @param pubItem the PubItem to submit
     * @return a reference to the PubItem returned by the framework
     */
    private ItemRO submitOrReleasePubItem(PubItemVO pubItem, String submissionComment, boolean ignoreInformativeMessages) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            if (pubItem != null && pubItem.getVersion() != null)
            {
                logger.debug("Submitting PubItem: " + pubItem.getVersion().getObjectId());
            }
            else
            {
                logger.debug("Submitting a new PubItem.");
            }
        }
        
        /*
         * FrM: Validation with validation point "submit_item"
         */
        ValidationReportVO report = this.itemValidating.validateItemObject(pubItem, "submit_item");
        currentItemValidationReport = report;

        logger.debug("Validation Report: " + report);
        
        if (report.isValid() && !report.hasItems()) {
       
            this.getItemListSessionBean().setListDirty(true);
            // clean up the item from unused sub-VOs
            this.cleanUpItem(pubItem);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Submitting item...");
            }
    
            // submit the item
            ItemRO submittedPubItem = submitOrSubmitAndReleasePubItem(pubItem, submissionComment, loginHelper.getAccountUser()).getVersion();
    
            return submittedPubItem;
        }
        else if (report.isValid())
        {
            // Item is valid, but has informative messages.
            this.getItemListSessionBean().setListDirty(true);
            if (ignoreInformativeMessages) {
                // clean up the item from unused sub-VOs
                this.cleanUpItem(pubItem);
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Submitting item...");
                }
        
                // submit the item
                ItemRO submittedPubItem = submitOrSubmitAndReleasePubItem(pubItem, submissionComment, loginHelper.getAccountUser()).getVersion();
        
                return submittedPubItem;
            }
            
            return pubItem.getVersion();          
        }
        else
        {           
            // Item is invalid, do not submit anything.
            return pubItem.getVersion();          
        }        
    }

    /**
     * Submits a PubItem to the framework.
     * @param pubItem the PubItem to submit
     * @return a reference to the PubItem returned by the framework
     */
    private ItemRO saveAndSubmitOrReleasePubItem(PubItemVO pubItem, String submissionComment, boolean ignoreInformativeMessages) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            if (pubItem != null && pubItem.getVersion() != null)
            {
                logger.debug("Saving/submitting PubItem: " + pubItem.getVersion().getObjectId());
            }
            else
            {
                logger.debug("Saving/submitting a new PubItem.");
            }
        }
        
        /*
         * FrM: Validation with validation point "submit_item"
         */
        ValidationReportVO report = this.itemValidating.validateItemObject(pubItem, "submit_item");
        currentItemValidationReport = report;

        logger.debug("Validation Report: " + report);
        
        if (report.isValid() && !report.hasItems()) {
       
            this.getItemListSessionBean().setListDirty(true);
            // clean up the item from unused sub-VOs
            this.cleanUpItem(pubItem);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Submitting item...");
            }
    
            // submit the item
           
            PubItemVO submittedPubItem = submitOrSubmitAndReleasePubItem(pubItem, submissionComment, loginHelper.getAccountUser());
    
            return submittedPubItem.getVersion();
        }
        else if (report.isValid())
        {
            this.getItemListSessionBean().setListDirty(true);
            // Item is valid, but has informative messages.
            
            if (ignoreInformativeMessages) {
                // clean up the item from unused sub-VOs
                this.cleanUpItem(pubItem);
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Submitting item...");
                }
        
                // submit the item
                PubItemVO submittedPubItem = submitOrSubmitAndReleasePubItem(pubItem, submissionComment, loginHelper.getAccountUser());
        
                return submittedPubItem.getVersion();
            }
            
            return pubItem.getVersion();          
        }
        else
        {           
            // Item is invalid, do not submit anything.
            return pubItem.getVersion();          
        }        
    }

    /**
     * Submits a PubItem to the framework.
     * @author Michael Franke
     * @param pubItem the PubItem to submit
     * @return a reference to the PubItem returned by the framework
     */
    private void withdrawPubItem(PubItemVO pubItem, String withdrawalComment) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            if (pubItem != null && pubItem.getVersion() != null)
            {
                logger.debug("Withdrawing PubItem: " + pubItem.getVersion().getObjectId());
            }
            else
            {
                logger.error("Withdrawing a new PubItem??????");
            }
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Withdrawing item...");
        }

        this.getItemListSessionBean().setListDirty(true);
        Date lastModificationDate = pubItem.getModificationDate();
        //withdrawalComment = pubItem.getWithdrawalComment();

        // withdraw the item
        this.pubItemPublishing.withdrawPubItem(pubItem, lastModificationDate, withdrawalComment, loginHelper.getAccountUser());

    }


    /**
     * Validates the item.
     * @author Michael Franke
     * Changed by DiT, 17.10.2007: new parameter: validation point
     * @param pubItem the item to validate
     * @param validationPoint the validation point for the validation
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String validate(PubItemVO pubItem, String validationPoint) throws Exception
    {
            if (pubItem != null)
            {
                PubItemVO itemVO = new PubItemVO(pubItem);
                ValidationReportVO report = this.itemValidating.validateItemObject(itemVO, validationPoint);
                currentItemValidationReport = report;
            }
        return null;
    }

    /**
     * Deletes a PubItem from the framework.
     * @param pubItem the PubItem to delete
     * @return a reference to the PubItem returned by the framework
     */
    private void deletePubItem(ItemRO pubItem) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Deleting PubItem: " + pubItem.getObjectId());
            logger.debug("Deleting item...");
        }
        
        this.getItemListSessionBean().setListDirty(true);
        // delete the item
        this.pubItemDepositing.deletePubItem(pubItem, loginHelper.getAccountUser());
    }

    /**
     * Cleans up the ValueObject for saving/submitting from unused sub-VOs.
     * @param pubItem the PubItem to clean up
     */
    public void cleanUpItem(PubItemVO pubItem)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Cleaning up PubItem...");
        }
        
        for (int i = 0; i < pubItem.getMetadata().getCreators().size(); i++)
        {
            CreatorVO creator = pubItem.getMetadata().getCreators().get(i);
            
            // build completeName out of givenName and familyName
            if (creator.getPerson() != null && creator.getPerson().getCompleteName() == null)
            {
                String completeName = new String();
                if (creator.getPerson().getGivenName() != null)
                {
                    completeName.concat(creator.getPerson().getGivenName() + " ");
                }
                completeName.concat(creator.getPerson().getFamilyName());
                creator.getPerson().setCompleteName(completeName);
            }
            
            // delete unfilled PersonOrganizations
            if (creator.getPerson() != null && creator.getPerson().getOrganizations() != null)
            {
                for (int j = (creator.getPerson().getOrganizations().size() - 1); j >= 0; j--)
                {
                    if (creator.getPerson() != null
                            && (creator.getPerson().getOrganizations().get(j).getName() == null || creator.getPerson()
                                    .getOrganizations().get(j).getName().getValue().length() == 0))
                    {
                        creator.getPerson().getOrganizations().remove(j);
                    }
                }
            }
        }

        // delete unfilled publishingInfo
        if (pubItem.getMetadata().getPublishingInfo() != null)
        {
            if ((pubItem.getMetadata().getPublishingInfo().getPublisher() == null
                    || pubItem.getMetadata().getPublishingInfo().getPublisher().length() == 0)
                    && (pubItem.getMetadata().getPublishingInfo().getEdition() == null
                    || pubItem.getMetadata().getPublishingInfo().getEdition().length() == 0)
                    && (pubItem.getMetadata().getPublishingInfo().getPlace() == null
                    || pubItem.getMetadata().getPublishingInfo().getPlace().length() == 0))
            {
                pubItem.getMetadata().setPublishingInfo(null);
            }
        }
        
        // delete unfilled file
        if (pubItem.getFiles() != null)
        {
            for (int i = (pubItem.getFiles().size() - 1); i >= 0; i--)
            {
                if ((pubItem.getFiles().get(i).getName() == null
                        || pubItem.getFiles().get(i).getName().length() == 0) && (pubItem.getFiles().get(i).getContent() == null
                        || pubItem.getFiles().get(i).getContent().length() == 0))
                {
                    pubItem.getFiles().remove(i);
                }
            }
        }
        
        // delete unfilled ContentLanguage
        if (pubItem.getMetadata().getLanguages() != null)
        {
            for (int i = (pubItem.getMetadata().getLanguages().size() - 1); i >= 0; i--)
            {
                if (pubItem.getMetadata().getLanguages().get(i) == null
                        || pubItem.getMetadata().getLanguages().get(i).length() == 0)
                {
                    pubItem.getMetadata().getLanguages().remove(i);
                }
            }
        }

        // delete unfilled Sources
        if (pubItem.getMetadata().getSources() != null)
        {
            for (int i = (pubItem.getMetadata().getSources().size() - 1); i >= 0; i--)
            {
                // delete source if title is not filled
                if (pubItem.getMetadata().getSources().get(i) == null
                        || pubItem.getMetadata().getSources().get(i).getTitle() == null
                        || pubItem.getMetadata().getSources().get(i).getTitle().getValue() == null || pubItem.getMetadata().getSources().get(i).getTitle().getValue().trim().equals(""))
                {
                    pubItem.getMetadata().getSources().remove(i);
                }
                else
                {
                    // delete unfilled publishingInfo
                    if (pubItem.getMetadata().getSources().get(i).getPublishingInfo() != null)
                    {
                        if (pubItem.getMetadata().getSources().get(i).getPublishingInfo().getPublisher() == null
                                || pubItem.getMetadata().getSources().get(i).getPublishingInfo().getPublisher().length() == 0)
                        {
                            pubItem.getMetadata().getSources().get(i).setPublishingInfo(null);
                        }
                    }
                    
                    // delete unfilled identifiers
                    if (pubItem.getMetadata().getSources().get(i).getIdentifiers() != null)
                    {
                        for (int j = 0; j < pubItem.getMetadata().getSources().get(i).getIdentifiers().size(); j++)
                        {
                            IdentifierVO identifier = pubItem.getMetadata().getSources().get(i).getIdentifiers().get(j);
                            if (identifier.getId() == null
                                    || identifier.getId().length() == 0
                                    || identifier.getTypeString() == null
                                    || identifier.getTypeString().length() == 0)
                            {
                                pubItem.getMetadata().getSources().get(i).getIdentifiers().remove(j);
                            }
                        }                
                    }

                    // delete unfilled source creators
                    if (pubItem.getMetadata().getSources().get(i).getCreators() != null)
                    {
                        for (int j = 0; j < pubItem.getMetadata().getSources().get(i).getCreators().size(); j++)
                        {
                            CreatorVO creator = pubItem.getMetadata().getSources().get(i).getCreators().get(j);
                            if (creator.getRoleString() == null
                                    || creator.getRoleString().length() == 0
                                    || creator.getTypeString() == null
                                    || creator.getTypeString().length() == 0
                                    || (creator.getPerson() != null
                                            && (creator.getPerson().getFamilyName() == null
                                            || creator.getPerson().getFamilyName().length() == 0))
                                    || (creator.getOrganization() != null
                                            && (creator.getOrganization().getName() == null
                                            		|| creator.getOrganization().getName().getValue() == null 
                                                    || creator.getOrganization().getName().getValue().length() == 0)))
                            {
                                pubItem.getMetadata().getSources().get(i).getCreators().remove(j);
                            }
                            else
                            {
                                this.cleanUpCreator(creator);
                            }
                        }                
                    }
                }
            }
        }
        
        // delete unfilled Event
        if (pubItem.getMetadata().getEvent() != null
                && (pubItem.getMetadata().getEvent().getTitle() == null
                        || pubItem.getMetadata().getEvent().getTitle().getValue() == null
                        || pubItem.getMetadata().getEvent().getTitle().getValue().length() == 0))
        {
            pubItem.getMetadata().setEvent(null);
        }

        // delete unfilled Identifier
        if (pubItem.getMetadata().getIdentifiers() != null)
        {
            for (int i = (pubItem.getMetadata().getIdentifiers().size() - 1); i >= 0; i--)
            {
                if (pubItem.getMetadata().getIdentifiers().get(i) == null
                        || pubItem.getMetadata().getIdentifiers().get(i).getId() == null
                        || pubItem.getMetadata().getIdentifiers().get(i).getId().length() == 0)
                {
                    pubItem.getMetadata().getIdentifiers().remove(i);
                }
            }
        }

        // delete unfilled Abstract
        if (pubItem.getMetadata().getAbstracts() != null)
        {
            for (int i = (pubItem.getMetadata().getAbstracts().size() - 1); i >= 0; i--)
            {
                if (pubItem.getMetadata().getAbstracts().get(i) == null
                        || pubItem.getMetadata().getAbstracts().get(i).getValue() == null
                        || pubItem.getMetadata().getAbstracts().get(i).getValue().length() == 0)
                {
                    pubItem.getMetadata().getAbstracts().remove(i);
                }
            }
        }
        
        
        // assign the external org id to default organisation
        try
        {
        	for (CreatorVO creator : pubItem.getMetadata().getCreators()) {
				if (creator.getPerson() != null)
				{
					for (OrganizationVO organization : creator.getPerson().getOrganizations()) {
						if (organization.getIdentifier() == null)
						{
							organization.setIdentifier(PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
						}
					}
				}
				else
				{
					if (creator.getOrganization() != null && creator.getOrganization().getIdentifier() == null)
					{
						creator.getOrganization().setIdentifier(PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
					}
				}
			}
        	for (SourceVO source : pubItem.getMetadata().getSources()) {
	        	for (CreatorVO creator : source.getCreators()) {
					if (creator.getPerson() != null)
					{
						for (OrganizationVO organization : creator.getPerson().getOrganizations()) {
							if (organization.getIdentifier() == null)
							{
								organization.setIdentifier(PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
							}
						}
					}
					else
					{
						if (creator.getOrganization() != null && creator.getOrganization().getIdentifier() == null)
						{
							creator.getOrganization().setIdentifier(PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
						}
					}
				}
        	}
        }
        catch (Exception e) {
			logger.error("Error getting external org id", e);
		}

        
    }

    /**
     * Cleans up the CreatorVO from unused sub-VOs.
     * @param creator the creator that has to be cleaned up
     */
    private void cleanUpCreator(CreatorVO creator)
    {
        // build completeName out of givenName and familyName
        if (creator.getPerson() != null && creator.getPerson().getCompleteName() == null)
        {
            String completeName = new String();
            if (creator.getPerson().getGivenName() != null)
            {
                completeName.concat(creator.getPerson().getGivenName() + " ");
            }
            completeName.concat(creator.getPerson().getFamilyName());
            creator.getPerson().setCompleteName(completeName);
        }

        // delete unfilled PersonOrganizations
        if (creator.getPerson() != null && creator.getPerson().getOrganizations() != null)
        {
            for (int j = (creator.getPerson().getOrganizations().size() - 1); j >= 0; j--)
            {
                if (creator.getPerson() != null
                        && (creator.getPerson().getOrganizations().get(j).getName() == null
                                || creator.getPerson().getOrganizations().get(j).getName().getValue() == null
                                || creator.getPerson().getOrganizations().get(j).getName().getValue().length() == 0))
                {
                    creator.getPerson().getOrganizations().remove(j);
                }
            }
        }
    }

    /**
     * Returns all items for a user depending on the selected itemState.
     * @param selectedItemState the item state for which the items should be returned
     * @return all items for a user in the selected state
     * @throws Exception if framework access fails
     */
    public ArrayList<PubItemVO> retrieveItems(final String selectedItemState) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving Items for state: " + selectedItemState);
        }

        // define the filter criteria
        FilterTaskParamVO filter = new FilterTaskParamVO();
        
       
        
        Filter f1 = filter.new OwnerFilter(loginHelper.getAccountUser().getReference());
        filter.getFilterList().add(f1);
        

        Filter f2 = filter.new FrameworkItemTypeFilter(PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
        filter.getFilterList().add(f2);
        
        if (selectedItemState.toLowerCase().equals("withdrawn"))
        {
            Filter f3 = filter.new ItemPublicStatusFilter(PubItemVO.State.WITHDRAWN);
            filter.getFilterList().add(f3);
        }
        else
        {
            if (!"all".equals(selectedItemState))
            {
                Filter f3 = filter.new ItemStatusFilter(PubItemVO.State.valueOf(selectedItemState));
                filter.getFilterList().add(f3);
            }
        
            Filter f4 = filter.new ItemPublicStatusFilter(PubItemVO.State.IN_REVISION);
            filter.getFilterList().add(f4);
            Filter f5 = filter.new ItemPublicStatusFilter(PubItemVO.State.PENDING);
            filter.getFilterList().add(f5);
            Filter f6 = filter.new ItemPublicStatusFilter(PubItemVO.State.SUBMITTED);
            filter.getFilterList().add(f6);
            Filter f7 = filter.new ItemPublicStatusFilter(PubItemVO.State.RELEASED);
            filter.getFilterList().add(f7);
        }
        
        Filter f8 = filter.new LimitFilter("0");
        filter.getFilterList().add(f8);
       
        
       
        
        // transform the filter criteria
        if (logger.isDebugEnabled())
        {
            logger.debug("Transforming filters...");
        }
        String xmlparam = this.xmlTransforming.transformToFilterTaskParam(filter);

        // retrieve the items applying the filter criteria
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving items...");
        }
        String xmlItemList = "";
        try
        {
            xmlItemList = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle()).retrieveItems(xmlparam);
        }
        catch (AuthenticationException e)
        {
            logger.debug(e.toString());
            Login login = (Login) getSessionBean(Login.class);
            login.forceLogout();
            throw e;
        }

        // transform the itemList
        if (logger.isDebugEnabled())
        {
            logger.debug("Transforming items...");
        }
        ArrayList<PubItemVO> itemList = (ArrayList<PubItemVO>) this.xmlTransforming.transformToPubItemList(xmlItemList);

        return itemList;
    }
    
    /**
     * Returns all items in a list of item ids.
     * @param itemRefs a list of item ids of items that should be retrieved.
     * @return all items for a user with the given ids
     * @throws Exception if framework access fails
     */
    public ArrayList<PubItemVO> retrieveItems(final List<ItemRO> itemRefs) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving Item list: ");
        }

        if (itemRefs == null || itemRefs.isEmpty())
        {
        	return new ArrayList<PubItemVO>();
        }
        
        
        
        //workarround due to filter bug - create filters manually without whitespaces
        String filter = "<param><filter name=\"http://purl.org/dc/elements/1.1/identifier\">";
        for(ItemRO itemRO : itemRefs)
        {
            filter+="<id>"+itemRO.getObjectId()+"</id>";
        }
        filter+="</filter></param>";
        logger.debug("Filter: \n" + filter);
        /*
        // define the filter criteria
        FilterTaskParamVO filter = new FilterTaskParamVO();
        FilterTaskParamVO.ItemRefFilter f1 = filter.new ItemRefFilter(itemRefs);
        filter.getFilterList().add(f1);
        
        // transform the filter criteria
        if (logger.isDebugEnabled())
        {
            logger.debug("Transforming filters...");
        }
        String xmlparam = this.xmlTransforming.transformToFilterTaskParam(filter);
        */
        // retrieve the items applying the filter criteria
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving items...");
        }
        String xmlItemList = "";
        try
        {
            if(loginHelper.getESciDocUserHandle() != null)
            {
                xmlItemList = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle()).retrieveItems(filter);
            }
            else
            {
                xmlItemList = ServiceLocator.getItemHandler().retrieveItems(filter);
            }
        }
        catch (AuthenticationException e)
        {
            logger.debug(e.toString());
            Login login = (Login) getSessionBean(Login.class);
            login.forceLogout();
            throw e;
        }

        // transform the itemList
        if (logger.isDebugEnabled())
        {
            logger.debug("Transforming items...");
        }
        ArrayList<PubItemVO> itemList = (ArrayList<PubItemVO>) this.xmlTransforming.transformToPubItemList(xmlItemList);

        return itemList;
    }

    /**
     * Returns an item by its id.
     * @param the item id which belongs to the item
     * @return the item with the requested id
     * @throws Exception if framework access fails
     */
    public List<VersionHistoryEntryVO> retrieveVersionHistoryForItem(String itemID) throws Exception
    {
        List<VersionHistoryEntryVO> versionHistoryList = new ArrayList<VersionHistoryEntryVO>();

        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving releases for Item with id: " + itemID);
        }
        
        String xmlVersionHistoryList = "";
        //login with escidoc user handle
        if(loginHelper.getESciDocUserHandle() != null)
        {
            try
            {
                xmlVersionHistoryList = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle()).retrieveVersionHistory(itemID);
            }
            catch (AuthenticationException e)
            {
                logger.debug(e.toString());
                Login login = (Login)getSessionBean(Login.class);
                login.forceLogout();
                throw e;
            }
        }
        //anonymous login
        else
        {
            try
            {
                xmlVersionHistoryList = ServiceLocator.getItemHandler().retrieveVersionHistory(itemID);
            }
            catch (AuthenticationException e)
            {
                logger.debug(e.toString());
                Login login = (Login) getSessionBean(Login.class);
                login.forceLogout();
                throw e;
            }
        }
        
        // transform the xml item
        if (logger.isDebugEnabled())
        {
            logger.debug("Transforming items...");
        }
        versionHistoryList = this.xmlTransforming.transformToEventVOList(xmlVersionHistoryList);

        
        return versionHistoryList;
    }
    
    
    /**
     * Returns a list of releases for a pubitem.
     * @author Tobias Schraut
     * @param the item id for which releases should be fetched
     * @return the item with the requested id
     * @throws Exception if framework access fails
     */
    public PubItemVO retrieveItem(String itemID) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving Items with id: " + itemID);
        }
        
        String xmlItem = "";        
        //login with escidoc user handle 
        if(loginHelper.getESciDocUserHandle() != null)
        {
            try
            {
                xmlItem = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle()).retrieve(itemID);
            }
            catch (AuthenticationException e)
            {
                logger.debug(e.toString());
                Login login = (Login) getSessionBean(Login.class);
                login.forceLogout();
                throw e;
            }
        }
        //anonymous login
        else
        {
            try
            {
                xmlItem = ServiceLocator.getItemHandler().retrieve(itemID);
            }
            catch (AuthenticationException e)
            {
                logger.debug(e.toString());
                Login login = (Login) getSessionBean(Login.class);
                login.forceLogout();
                throw e;
            }
        }
        
        // transform the xml item
        if (logger.isDebugEnabled())
        {
            logger.debug("Transforming items...");
        }
        PubItemVO item = this.xmlTransforming.transformToPubItem(xmlItem);

        return item;
    }

    /**
     * Returns all items which contain the searchString as List.
     * @param searchString the string which should be searched for
     * @param includeFiles determines if the search should include the files of the items
     * @return all items which contain the searchString
     * @throws Exception if framework access fails
     */
    public StandardSearchResult searchItems( ArrayList<MetadataSearchCriterion> criteria ) throws Exception
    {
    	ArrayList<String> contentTypes = new ArrayList<String>();
    	String contentTypeIdPublication = PropertyReader.getProperty( PROPERTY_CONTENT_MODEL );
    	contentTypes.add( contentTypeIdPublication );
    	
    	MetadataSearchQuery query = new MetadataSearchQuery( contentTypes, criteria );
    	// we get items and containers from the search service
    	StandardSearchResult result = this.itemContainerSearch.search( query );
    	return result;
    }

    /**
     * Returns all top-level affiliations.
     * @return all top-level affiliations
     * @throws Exception if framework access fails
     */
    public ArrayList<AffiliationVO> retrieveTopLevelAffiliations() throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving top-level affiliations");
        }

        // define the filter criteria
        FilterTaskParamVO filter = new FilterTaskParamVO();
        Filter f1 = filter.new TopLevelAffiliationFilter();
        filter.getFilterList().add(f1);
        
        // transform the filter criteria        
        if (logger.isDebugEnabled())
        {
            logger.debug("Transforming filters...");
        }
        String xmlparam = this.xmlTransforming.transformToFilterTaskParam(filter);
        
        // retrieve the affiliations applying the filter criteria
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving affiliations...");
        }
        String xmlAffiliationList = "";        
        try
        {
            xmlAffiliationList = ServiceLocator.getOrganizationalUnitHandler().retrieveOrganizationalUnits(xmlparam);
        }
        catch (AuthenticationException e)
        {
            logger.debug(e.toString());
            Login login = (Login) getSessionBean(Login.class);
            login.forceLogout();
            throw e;
        }
        
        // transform the affiliationList
        if (logger.isDebugEnabled())
        {
            logger.debug("Transforming affiliations...");
        }
        ArrayList<AffiliationVO> affiliationList =
            (ArrayList<AffiliationVO>) this.xmlTransforming.transformToAffiliationList(xmlAffiliationList);
        ArrayList<AffiliationVO> cleanedItemList = new ArrayList<AffiliationVO>();
        for (AffiliationVO affiliationVO : affiliationList)
        {
            if (!"created".equals(affiliationVO.getPublicStatus()))
            {
                cleanedItemList.add(affiliationVO);
            }
        }
        

        return cleanedItemList;
    }

    /**
     * Method for sending an email with attached file. The sending requires authentication.
     * 
     * @author:  StG
     * @param smtpHost   the outgoing smpt mail server
     * @param usr        the user authorized to the server
     * @param pwd        the password of the user
     * @param senderAddress    the email address of the sender
     * @param recipientsAddresses    the email address(es) of the recipients
     * @param recipientsCCAddresses    the email address(es) of the recipients
     * @param replytoAddresses    the replyto address(es)
     * @param text    the content text of the email
     * @param subject    the subject of the email
     * @param attachments    the names/paths of the files to be attached
     * @throws Exception if wrong pws or user or emailing data 
    */
   public String  sendEmail(String smtpHost,String usr,String pwd,
           String senderAddress,String[] recipientsAddresses,
           String[] recipientsCCAddresses,
           String[] recipientsBCCAddresses,
           String[] replyToAddresses,
           String subject,String text, String[] attachments) throws TechnicalException{
 
       logger.debug("sendEmail....");        
       String status = "not sent";
       status = emailHandling.sendMail(smtpHost, usr, pwd, senderAddress, recipientsAddresses, recipientsCCAddresses, recipientsBCCAddresses, replyToAddresses, 
                              subject, text, attachments);
       logger.debug("status " + status);        
      
        return status;
  }
        
    /**
     * Returns the available export formats (structured formats and citation layout styles) 
     * contained in exportFrrmatVO. It calls the proper external services for the retrieval of the export formats. 
     * 
     * @author:  StG
     * @param searchString the string which should be serached for
     * @param includeFiles determines if the search should include the files of the items
     * @return all items which contain the searchString
     * @throws Exception if interface didnt work
     */
   public List<ExportFormatVO> retrieveExportFormats() throws TechnicalException{
       
       // retrieve the export formats calling the interface method
       List<ExportFormatVO> exportFormatList = this.itemExporting.explainExportFormats();

      /* for (ExportFormatVO formatVO : exportFormatList)
       {
           logger.debug(formatVO);
       }*/
       return exportFormatList;
   }
    
   /**
    * Returns the export data stream with the selelcted items in the proper export format
    * 
    * @author:  StG
    * @param exportFormatVO is containing the selected export format and the file format 
    * @param itemsToExportList is the list of selected items to be exported
    * @return the export data stream as array of bytes 
    */
  public byte[] retrieveExportData(ExportFormatVO exportFormatVO, List<PubItemVO> itemsToExportList) 
      throws TechnicalException{
      if (logger.isDebugEnabled())
      {
          logger.debug("retrieveExportData...");
      }

      if (this.itemExporting == null) {
          if (logger.isDebugEnabled())
          {
              logger.debug("this.itemExporting interface is null");
          }
      }     
      byte[] res = null;
      // retrieve the export data calling the interface method
      res = this.itemExporting.getOutput(exportFormatVO, itemsToExportList);
  
      if ( (res == null) || (new String(res)).trim().equals("") ){ 
          logger.debug("Empty OR NULL string came from external export service!");
          return res;
      }
      if (logger.isDebugEnabled())
      {
          logger.debug("retrieveExportData result: " + new String(res) );
      }        
      return res;
     }

    
    /**
     * Returns all child affiliations for a parent affiliation.
     * @return all child affiliations
     * @throws Exception if framework access fails
     */
    public List<AffiliationVOPresentation> retrieveChildAffiliations(
            AffiliationVOPresentation parentAffiliation) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving child affiliations for affiliation: " + parentAffiliation.getDetails().getName());
        }

        String xmlChildAffiliationList = "";        
        try
        {
            // TODO FrM: Remove userHandle when Bug: 
            // http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=597
            // is fixed
            String userHandle = AdminHelper.getAdminUserHandle();
            xmlChildAffiliationList = ServiceLocator
                    .getOrganizationalUnitHandler(userHandle)
                    .retrieveChildObjects(parentAffiliation.getReference().getObjectId());
        }
        catch (AuthenticationException e)
        {
            logger.debug(e.toString());
            Login login = (Login) getSessionBean(Login.class);
            login.forceLogout();
            throw e;
        }
        
        // transform the affiliationList
        if (logger.isDebugEnabled())
        {
            logger.debug("Transforming child affiliations...");
        }
        List<AffiliationVO> affiliationList =
            (List<AffiliationVO>) this.xmlTransforming.transformToAffiliationList(xmlChildAffiliationList);

        List<AffiliationVO> cleanedAffiliationList = new ArrayList<AffiliationVO>();
        
        // Remove opened affiliations
        for (AffiliationVO affiliationVO : affiliationList)
        {
            if (!"created".equals(affiliationVO.getPublicStatus()))
            {
                cleanedAffiliationList.add(affiliationVO);
            }
        }
        
        List<AffiliationVOPresentation> wrappedAffiliationList =
            CommonUtils.convertToAffiliationVOPresentationList(cleanedAffiliationList);
        
        for (AffiliationVOPresentation affiliationVOPresentation : wrappedAffiliationList)
        {
        	affiliationVOPresentation.setParent(parentAffiliation);
        	affiliationVOPresentation.setNamePath(affiliationVOPresentation.getDetails().getName()+", "+parentAffiliation.getNamePath());
        	affiliationVOPresentation.setIdPath(affiliationVOPresentation.getReference().getObjectId() +" "+parentAffiliation.getIdPath());
		}
        return wrappedAffiliationList;
    }
    
    /**
     * Returns an affiliation retrieved by its ID.
     * @return the requested affiliation
     * @throws Exception if framework access fails
     */
    public AffiliationVO retrieveAffiliation(String affiliationId) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving affiliation by ID: " + affiliationId);
        }

        String xmlAffiliation = "";        
        try
        {
            xmlAffiliation = ServiceLocator.getOrganizationalUnitHandler().retrieve(affiliationId);
        }
        catch (AuthenticationException e)
        {
            logger.debug(e.toString());
            Login login = (Login) getSessionBean(Login.class);
            login.forceLogout();
            throw e;
        }
        
        // transform the affiliation
        if (logger.isDebugEnabled())
        {
            logger.debug("Transforming the affiliation...");
        }
        AffiliationVO affiliation = this.xmlTransforming.transformToAffiliation(xmlAffiliation);

        return affiliation;
    }

    /**
     * Returns all items for a user depending on the selected itemState.
     * @param contextID the ID of the context that should be retrieved
     * @return the context with the given ID
     */
    public ContextVO retrieveContext(final String contextID)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving context for ID: " + contextID);
        }
        ContextVO context = null;

        String xmlContext = "";
        try
        {
            xmlContext = ServiceLocator.getContextHandler().retrieve(contextID);
            context = this.xmlTransforming.transformToContext(xmlContext);
        }
        catch (Exception e)
        {
            logger.debug(e.toString());
            Login login = (Login) getSessionBean(Login.class);
            try
            {
                login.forceLogout();
            }
            catch (Exception e2) {
                logger.error("Error logging out user", e2);
            }
            throw new RuntimeException("Error retrieving context", e);
        }

        return context;
    }

    /**
     * Returns all contexts for a user.
     * @return the list of contexts
     * @throws Exception if framework access fails
     */
    public List<ContextVO> retrieveCollections() throws Exception
    {
        List<ContextVO> allCollections = new ArrayList<ContextVO>();

        try
        {
            if(loginHelper.getAccountUser() != null 
            		&& loginHelper.getAccountUser().getReference() != null 
            		&& loginHelper.getAccountUser().getReference().getObjectId()!= null 
            		&& !loginHelper.getAccountUser().getReference().getObjectId().trim().equals(""))
            {
            	allCollections = this.pubItemDepositing.getPubCollectionListForDepositing(loginHelper.getAccountUser());
            }
        }
        catch (Exception e)
        {
            logger.debug(e.toString());
            Login login = (Login) getSessionBean(Login.class);
            login.forceLogout();
            throw e;
        }
        
        return allCollections;        
    }
    
    /**
     * Accepts an item.
     * @param pubItem the item that should be accepted
     * @return string, identifying the page that should be navigated to after this method call
     * @throws Exception if framework access fails
     */
    public String acceptCurrentPubItem(String acceptanceComment, String navigationRuleWhenSuccessfull)
    {
        try
        {
            if (this.currentPubItem == null)
            {
                TechnicalException technicalException = new TechnicalException("No current PubItem is set.");
                throw technicalException;
            }

            // accepting the current item
            ItemRO pubItemRO = this.acceptPubItem(this.currentPubItem, acceptanceComment, true);
            
            if (pubItemRO == this.currentPubItem.getVersion())
            {
                return null;
            }
            
        }
        catch (Exception e)
        {
            logger.error("Could not accept item." + "\n" + e.toString(), e);
            ((ErrorPage)this.getBean(ErrorPage.class)).setException(e);
            
            return ErrorPage.LOAD_ERRORPAGE;
        }
        
        return navigationRuleWhenSuccessfull;
    }

    /**
     * Accepts an item.
     * @param pubItem the item that should be updated
     * @param acceptanceComment a comment for the acceptance
     * @param ignoreInformativeMessages ignore informative messages from the validation report 
     * @return reference to the accepted item
     * @throws Exception if framework access fails
     */
    private ItemRO acceptPubItem(PubItemVO pubItem, String acceptanceComment, boolean ignoreInformativeMessages) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Accepting PubItem: " + pubItem.getVersion().getObjectId());
        }
        
        /*
         * Copied by DiT from submitPubItem() by FrM: Validation with validation point "accept_item"
         */
        ValidationReportVO report = this.itemValidating.validateItemObject(pubItem, EditItem.VALIDATIONPOINT_ACCEPT);
        currentItemValidationReport = report;

        logger.debug("Validation Report: " + report);
        
        if (report.isValid() && !report.hasItems()) 
        {       
            this.getItemListSessionBean().setListDirty(true);
            // clean up the item from unused sub-VOs
            this.cleanUpItem(pubItem);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Submitting item...");
            }
    
            // accept the item
            ItemRO acceptedPubItem = this.pubItemDepositing.acceptPubItem(pubItem, acceptanceComment, loginHelper.getAccountUser()).getVersion();
    
            return acceptedPubItem;
        }
        else if (report.isValid())
        {
            this.getItemListSessionBean().setListDirty(true);
            // Item is valid, but has informative messages.            
            if (ignoreInformativeMessages) 
            {
                // clean up the item from unused sub-VOs
                this.cleanUpItem(pubItem);
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Submitting item...");
                }
        
                // accept the item
                ItemRO acceptedPubItem = this.pubItemDepositing.acceptPubItem(pubItem, acceptanceComment, loginHelper.getAccountUser()).getVersion();
        
                return acceptedPubItem;
            }
            
            return pubItem.getVersion();          
        }
        else
        {           
            // Item is invalid, do not accept anything.
            return pubItem.getVersion();          
        }        
    }
    
    /**
     * @author Tobias Schraut
     * @param pubItemVO the pubitem for which the revisions should be fetched
     * @return a list of wrapped released ReleationVOs
     */
    public List<RelationVOPresentation> retrieveRevisions(PubItemVO pubItemVO) throws Exception
    {
        List<RelationVOPresentation> revisionVOList = new ArrayList<RelationVOPresentation>();
        
        if(loginHelper.getESciDocUserHandle() != null)
        {
            revisionVOList = CommonUtils.convertToRelationVOPresentationList(this.dataGathering.findRevisionsOfItem(loginHelper.getESciDocUserHandle(), pubItemVO.getVersion()));
        }
        else
        {
            // TODO ScT: retrieve as super user (workaround for not logged in users until the framework changes this retrieve method for unauthorized users)
            revisionVOList = CommonUtils.convertToRelationVOPresentationList(this.dataGathering.findRevisionsOfItem(AdminHelper.getAdminUserHandle(), pubItemVO.getVersion()));
        }
            
        
        
        List<ItemRO> sourceItemRefs = new ArrayList<ItemRO>();
        for (RelationVOPresentation relationVOPresentation : revisionVOList) {
			
            sourceItemRefs.add(relationVOPresentation.getSourceItemRef());
		}
        
        List<PubItemVO> sourceItemList = retrieveItems(sourceItemRefs);

        for (RelationVOPresentation revision : revisionVOList) {
			for (PubItemVO pubItem : sourceItemList) {
				if (revision.getSourceItemRef().getObjectId().equals(pubItem.getVersion().getObjectId()))
				{
					revision.setSourceItem(pubItem);
					break;
				}
			}
		}
        
        
        return revisionVOList;
    }
    
    /**
     * @author Markus Haarlaender
     * @param pubItemVO the pubitem (a revision) for which the parent items should be fetched
     * @return a list of wrapped ReleationVOs that contain information about the items from which this revision was created
     */
    public List<RelationVOPresentation> retrieveParentsForRevision(PubItemVO pubItemVO) throws Exception
    {
        List<RelationVOPresentation> revisionVOList = new ArrayList<RelationVOPresentation>();
        
        if(loginHelper.getESciDocUserHandle() != null)
        {
            revisionVOList = CommonUtils.convertToRelationVOPresentationList(this.dataGathering.findParentItemsOfRevision(loginHelper.getESciDocUserHandle(), pubItemVO.getVersion()));
        }
        else
        {
            String adminHandle = AdminHelper.getAdminUserHandle();
            // TODO ScT: retrieve as super user (workaround for not logged in users until the framework changes this retrieve method for unauthorized users)
            revisionVOList = CommonUtils.convertToRelationVOPresentationList(this.dataGathering.findParentItemsOfRevision(adminHandle, pubItemVO.getVersion()));
        }
            
        List<ItemRO> targetItemRefs = new ArrayList<ItemRO>();
        for (RelationVOPresentation relationVOPresentation : revisionVOList) {
            targetItemRefs.add(relationVOPresentation.getTargetItemRef());
        }
        List<PubItemVO> targetItemList = retrieveItems(targetItemRefs);

        for (RelationVOPresentation revision : revisionVOList) {
            for (PubItemVO pubItem : targetItemList) {
                if (revision.getTargetItemRef().getObjectId().equals(pubItem.getVersion().getObjectId()))
                {
                    revision.setTargetItem(pubItem);
                    break;
                }
            }
        }
        
        
        return revisionVOList;
    }


    /**
     * Tests if the metadata of the two items have changed.
     * @param oldPubItem the old pubItem
     * @param newPubItem the new (maybe changed) pubItem
     * @return true if the metadata of the new item has changed
     */
    public boolean hasChanged(PubItemVO oldPubItem, PubItemVO newPubItem)
    {
        // clone both items
        PubItemVO oldPubItemClone = (PubItemVO)oldPubItem.clone();
        PubItemVO newPubItemClone = (PubItemVO)newPubItem.clone();
        
        // clean both items up from unused sub-VOs
        this.cleanUpItem(oldPubItemClone);
        this.cleanUpItem(newPubItemClone);
        
        // compare the metadata and files of the two items
        boolean metadataChanged = !(oldPubItemClone.getMetadata().equals(newPubItemClone.getMetadata()));
        boolean fileChanged = !(oldPubItemClone.getFiles().equals(newPubItemClone.getFiles()));
        
        return (metadataChanged || fileChanged);
    }

    public PubItemVO getCurrentPubItem()
    {
        return currentPubItem;
    }

    public void setCurrentPubItem(PubItemVO currentPubItem)
    {
        this.currentPubItem = currentPubItem;
    }
    
    public ValidationReportVO getCurrentItemValidationReport() 
    {
        return currentItemValidationReport;
    }

    public void setCurrentItemValidationReport(ValidationReportVO currentItemValidationReport) 
    {
        this.currentItemValidationReport = currentItemValidationReport;
    }

    public ContextVO getCurrentContext()
    {
        // retrieve current context newly if the current item has changed or if the context has not been retrieved so far
        if (this.getCurrentPubItem() != null)
        {            
            if (this.currentContext == null 
                    || !(this.currentContext.getReference().getObjectId().equals(this.getCurrentPubItem().getContext().getObjectId())))
            {
                ContextVO context = this.retrieveContext(this.getCurrentPubItem().getContext().getObjectId());
                this.setCurrentCollection(context);
            }
        }
        
        return currentContext;
    }

    public void setCurrentCollection(ContextVO currentCollection)
    {
        this.currentContext = currentCollection;
    }
    
    /**
     * Returns the ItemListSessionBean.
     * @return a reference to the scoped data bean (ItemListSessionBean)
     */
    protected ItemListSessionBean getItemListSessionBean()
    {
        return (ItemListSessionBean)getSessionBean(ItemListSessionBean.class);
    }
    
    private PubItemVO submitOrSubmitAndReleasePubItem(PubItemVO pubItem, String submissionComment, AccountUserVO user) throws Exception
    {
        
        if (getCurrentWorkflow().equals(PubItemDepositing.WORKFLOW_SIMPLE))
        {
             return this.pubItemDepositing.submitAndReleasePubItem(pubItem, submissionComment, user);
        }
        else if (getCurrentWorkflow().equals(PubItemDepositing.WORKFLOW_STANDARD))
        {
            return this.pubItemDepositing.submitPubItem(pubItem, submissionComment, user);
        }
        
        return null;
                
    }
    
    public String getCurrentWorkflow()
    {
        PublicationAdminDescriptorVO.Workflow workflow = getCurrentContext().getAdminDescriptor().getWorkflow();
        if (workflow == null || workflow == PublicationAdminDescriptorVO.Workflow.SIMPLE)
        {
            return PubItemDepositing.WORKFLOW_SIMPLE;
        }
        else if (workflow == PublicationAdminDescriptorVO.Workflow.STANDARD)
        {
            return PubItemDepositing.WORKFLOW_STANDARD;
        
        }
        
        return PubItemDepositing.WORKFLOW_SIMPLE;
    }

    public String reviseCurrentPubItem(String reviseComment, String navigationStringToGoBack)
    {
        try
        {
            if (this.currentPubItem == null)
            {
                TechnicalException technicalException = new TechnicalException("No current PubItem is set.");
                throw technicalException;
            }

           
            if (logger.isDebugEnabled())
            {
                logger.debug("Revising PubItem: " + currentPubItem.getVersion().getObjectId());
                logger.debug("Revising item...");
            }
            
            this.getItemListSessionBean().setListDirty(true);
            // delete the item
            this.qualityAssurance.revisePubItem(currentPubItem.getVersion(), reviseComment, loginHelper.getAccountUser());
            
        }
        catch (Exception e)
        {
            logger.error("Could not revise item." + "\n" + e.toString());
            ((ErrorPage) getSessionBean(ErrorPage.class)).setException(e);
            
            return ErrorPage.LOAD_ERRORPAGE;
        }
        
        // Reload list
        ((ItemListSessionBean) getSessionBean(ItemListSessionBean.class)).init();
        
        return navigationStringToGoBack;
    }
    
    public String getStatisticValue(String reportDefinitionType) throws Exception
    {
        return pubItemStatistic.getNumberOfItemOrFileRequests(reportDefinitionType, this.getCurrentPubItem().getVersion().getObjectId(), loginHelper.getAccountUser());
    }
}
