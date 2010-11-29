package de.mpg.escidoc.pubman.yearbook;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.www.services.om.ContextHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean;
import de.mpg.escidoc.pubman.search.SearchRetrieverRequestBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubItemResultVO;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.ItemRelationVO;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.UserAttributeVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.MarshallingException;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemPublishing;
import de.mpg.escidoc.services.pubman.depositing.PubItemLockedException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemNotFoundException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.search.query.SearchQuery;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

public class YearbookItemSessionBean extends FacesBean
{
    
    enum YBWORKSPACE
    {
        CANDIDATES, MEMBERS, INVALID, NON_CANDIDATES
    }
    
    private YBWORKSPACE selectedWorkspace;
    
    private static Logger logger = Logger.getLogger(YearbookItemSessionBean.class);
    public static String BEAN_NAME = "YearbookItemSessionBean";
    
    private PubItemVO yearbookItem;
    private LoginHelper loginHelper;
    private XmlTransforming xmlTransforming;
    private ItemHandler itemHandler;
    private ContextVO yearbookContext;
    private Search searchService;
    private ItemValidating itemValidating;
    private PubItemListSessionBean pilsb;
    
    private Map<String, YearbookInvalidItemRO> invalidItemMap = new HashMap<String, YearbookInvalidItemRO>();
    private Map<String, YearbookInvalidItemRO> validItemMap = new HashMap<String, YearbookInvalidItemRO>();
    
    public YearbookItemSessionBean()
    {
        try
        {
        	this.pilsb = (PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class);
            this.loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
            this.itemHandler = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle());
            InitialContext initialContext = new InitialContext();
            this.xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            this.searchService = (Search) initialContext.lookup(Search.SERVICE_NAME);
            this.itemValidating = (ItemValidating) initialContext.lookup(ItemValidating.SERVICE_NAME);
            this.selectedWorkspace = YBWORKSPACE.CANDIDATES;
            
          
            if(loginHelper.getIsYearbookEditor())
            {
                  initYearbook();
            }
            
        }
        catch (Exception e)
        {
            error("Error retrieving yearbook item!");
            logger.error("Error retrieving yearbook item!", e);
        }

    }
    
    public void initYearbook() throws Exception
    {
    	HashMap<String, String[]> filterParams = new HashMap<String, String[]>();  
        filterParams.put("operation", new String[] {"searchRetrieve"});
        filterParams.put("version", new String[] {"1.1"});
        
        String orgId="";
        for(UserAttributeVO attr : loginHelper.getAccountUser().getAttributes())
        {
            if(attr.getName().equals("o"))
            {
                orgId = attr.getValue();
                break;
            }
        }
        //String orgId = "escidoc:persistent25";
        filterParams.put("query", new String[] {"\"/properties/content-model/id\"=" + PropertyReader.getProperty("escidoc.pubman.yearbook.content-model.id") + " and \"/md-records/md-record/publication/creator/organization/identifier\"=" + orgId});
        filterParams.put("maximumRecords", new String[] {"10"});

        String xmlItemList = itemHandler.retrieveItems(filterParams);

        SearchRetrieveResponseVO result = xmlTransforming.transformToSearchRetrieveResponse(xmlItemList);
        

        
        if(result.getNumberOfRecords()!=1)
        {
            //error(result.getNumberOfRecords() + " yearbook items found!");
        }
        else
        {
            this.setYearbookItem(new PubItemVO((ItemVO)result.getRecords().get(0).getData()));
            ContextHandler contextHandler = ServiceLocator.getContextHandler(loginHelper.getESciDocUserHandle());
            String contextXml = contextHandler.retrieve(getYearbookItem().getContext().getObjectId());
            this.yearbookContext = xmlTransforming.transformToContext(contextXml);
            
        }
    }

    public void setYearbookItem(PubItemVO yearbookItem)
    {
        this.yearbookItem = yearbookItem;
    }

    public PubItemVO getYearbookItem() 
    {
        return yearbookItem;    
    }
    
    
    
    public int getNumberOfMembers()
    {
        if(yearbookItem!=null && yearbookItem.getRelations()!=null)
        {
            return yearbookItem.getRelations().size();
        }
        else
        {
            return 0;
        }
       
    }  
    
    public void addMembers(List<ItemRO> itemIds)
    {
        try
        {
            List<ItemRO> newRels = new ArrayList<ItemRO>();
            List<String> currentRelations = new ArrayList<String>();
            for(ItemRelationVO rel : yearbookItem.getRelations())
            {
               currentRelations.add(rel.getTargetItemRef().getObjectId());
            }
            int successful = 0;
            for(ItemRO id : itemIds)
            {
                if(currentRelations.contains(id.getObjectId()))
                {
                    warn(getMessage("Yearbook_ItemAlreadyInYearbook"));
                }
                else
                {
                    newRels.add(id);
                    successful++;
                }
                
            }
            addRelations(newRels);
            info(getMessage("Yearbook_AddedItemsToYearbook"));
        }
        catch (Exception e)
        {
           error(getMessage("Yearbook_ErrorAddingMembers"));
           logger.error("Error adding members to yearbook", e);
        }
    }
    
    public void removeMembers(List<ItemRO> itemIds)
    {
        try
        {
        	for(ItemRO item : itemIds)
        	{
        		if(invalidItemMap.containsKey(item.getObjectId()))
        		{
        			invalidItemMap.remove(item.getObjectId());
        		}
        		if(validItemMap.containsKey(item.getObjectId()))
        		{
        			validItemMap.remove(item.getObjectId());
        		}
        	}
        	
            removeRelations(itemIds); 
            info(getMessage("Yearbook_RemovedItemsFromYearbook"));
        }
        catch (Exception e)
        {
           error(getMessage("Yearbook_ErrorRemovingMembers"));
           logger.error("Error removing members from yearbook", e);
        }
    }
    
    private void addRelations(List<ItemRO> relList) throws Exception
    {
        if(relList.size()>0)
        {
            String updatedItemXml = itemHandler.retrieve(yearbookItem.getVersion().getObjectId());
            this.yearbookItem = xmlTransforming.transformToPubItem(updatedItemXml);           
            String taskParam = createRelationTaskParam(relList, yearbookItem.getModificationDate());
            itemHandler.addContentRelations(yearbookItem.getVersion().getObjectId(), taskParam); 
            updatedItemXml = itemHandler.retrieve(yearbookItem.getVersion().getObjectId());
            this.yearbookItem = xmlTransforming.transformToPubItem(updatedItemXml);
        }
    }
     
    private void removeRelations(List<ItemRO> relList) throws Exception
    {
        if(relList.size()>0)
        {
            String updatedItemXml = itemHandler.retrieve(yearbookItem.getVersion().getObjectId());
            this.yearbookItem = xmlTransforming.transformToPubItem(updatedItemXml);
        	String taskParam = createRelationTaskParam(relList, yearbookItem.getModificationDate());
            itemHandler.removeContentRelations(yearbookItem.getVersion().getObjectId(), taskParam); 
            updatedItemXml = itemHandler.retrieve(yearbookItem.getVersion().getObjectId());
            this.yearbookItem = xmlTransforming.transformToPubItem(updatedItemXml);
        }
    }
    
    private static String createRelationTaskParam(List<ItemRO> relList, Date lmd)
    {
        String filter = "<param last-modification-date=\"" + JiBXHelper.serializeDate(lmd) + "\">";
        
        for(ItemRO rel : relList)
        {
            filter+="<relation><targetId>" + rel.getObjectId() + "</targetId><predicate>http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasMember</predicate></relation>";
        }
        
        filter+="</param>";
        return filter;
    }
    
    private void updateItem(PubItemVO item) throws Exception
    {
        String itemXml = xmlTransforming.transformToItem(item);
        String updatedItemXml = itemHandler.update(item.getVersion().getObjectId(), itemXml);
        this.yearbookItem = xmlTransforming.transformToPubItem(updatedItemXml);
    }

    public void setYearbookContext(ContextVO yearbookContext)
    {
        this.yearbookContext = yearbookContext;
    }

    public ContextVO getYearbookContext()
    {
        return yearbookContext;
    }
    
    public boolean isCandidate(String id) throws Exception
    {
    	MetadataSearchQuery mdQuery = YearbookCandidatesRetrieverRequestBean.getCandidateQuery();
    	mdQuery.addCriterion(new MetadataSearchCriterion(CriterionType.IDENTIFIER, id, LogicalOperator.AND));
        ItemContainerSearchResult result = this.searchService.searchForItemContainer(mdQuery);
    	return result.getTotalNumberOfResults().shortValue() == 1;
    }
    
    public boolean isMember(String id) throws Exception
    {
    	MetadataSearchQuery mdQuery = YearbookCandidatesRetrieverRequestBean.getMemberQuery(getYearbookItem());
    	mdQuery.addCriterion(new MetadataSearchCriterion(CriterionType.IDENTIFIER, id, LogicalOperator.AND));
    	ItemContainerSearchResult result = this.searchService.searchForItemContainer(mdQuery);
    	return result.getTotalNumberOfResults().shortValue() == 1;
    	
    }
    
    public  List<PubItemVOPresentation> retrieveAllMembers() throws Exception
    {
        List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
        MetadataSearchQuery mdQuery = YearbookCandidatesRetrieverRequestBean.getMemberQuery(getYearbookItem());
        ItemContainerSearchResult result = this.searchService.searchForItemContainer(mdQuery);
        
        pubItemList =  SearchRetrieverRequestBean.extractItemsOfSearchResult(result);
        return pubItemList;
    }
    
    
    public boolean validateItem(PubItemVO pubItem) throws Exception
    {
    	YearbookInvalidItemRO storedItem= null;
        if(invalidItemMap.containsKey(pubItem.getVersion().getObjectId()))
        {
             storedItem = invalidItemMap.get(pubItem.getVersion().getObjectId());
        }
        else if(validItemMap.containsKey(pubItem.getVersion().getObjectId()))
        {
            storedItem = validItemMap.get(pubItem.getVersion().getObjectId());
        }
        
        if(storedItem==null || !pubItem.getModificationDate().equals(storedItem.getLastModificationDate()))
        {
            //revalidate
        	System.out.println("Yearbook Validating: " + pubItem.getVersion().getObjectId());
            ValidationReportVO rep = this.itemValidating.validateItemObjectBySchema(new PubItemVO(pubItem), "default", "yearbook");
            if(rep.getItems().size()>0)
            {
                validItemMap.remove(pubItem.getVersion().getObjectId());
                invalidItemMap.put(pubItem.getVersion().getObjectId(), new YearbookInvalidItemRO(pubItem.getVersion().getObjectId(), rep, pubItem.getModificationDate()));
            }
            else
            {
                invalidItemMap.remove(pubItem.getVersion().getObjectId());
                validItemMap.put(pubItem.getVersion().getObjectId(), new YearbookInvalidItemRO(pubItem.getVersion().getObjectId(), rep, pubItem.getModificationDate()));
            }
            
            return rep.isValid();
        }
        return storedItem.getValidationReport().isValid();
        
        
    }
    
    public String validateYearbook() throws Exception
    {

        List<PubItemVOPresentation> pubItemList = retrieveAllMembers();
        
        for(PubItemVOPresentation pubItem : pubItemList)
        {
           validateItem(pubItem);
        }
        
        changeToInvalidItems();
        return "";
        
        
    }

    public void setInvalidItemMap(Map<String, YearbookInvalidItemRO> invalidItemMap)
    {
        this.invalidItemMap = invalidItemMap;
    }

    public Map<String, YearbookInvalidItemRO> getInvalidItemMap()
    {  
        return invalidItemMap;
    }

    public void setSelectedWorkspace(YBWORKSPACE selectedWorkspace)
    {
        this.selectedWorkspace = selectedWorkspace;
    }

    public YBWORKSPACE getSelectedWorkspace()
    {
        return selectedWorkspace;
    }
    
    public String changeToCandidates()
    {
    	PubItemListSessionBean pilsb = (PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class);
        setSelectedWorkspace(YBWORKSPACE.CANDIDATES);
        pilsb.setCurrentPageNumber(1);
        pilsb.redirect();
        return "";
    }
    public String changeToMembers()
    {
    	PubItemListSessionBean pilsb = (PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class);
        setSelectedWorkspace(YBWORKSPACE.MEMBERS);
        pilsb.setCurrentPageNumber(1);
        pilsb.redirect();
        return "";
    }
    public String changeToInvalidItems()
    {
    	PubItemListSessionBean pilsb = (PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class);
        setSelectedWorkspace(YBWORKSPACE.INVALID);
        pilsb.setCurrentPageNumber(1);
        pilsb.redirect();
        return "";
    }
    
    public String changeToNonCandidates()
    {
    	PubItemListSessionBean pilsb = (PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class);
        setSelectedWorkspace(YBWORKSPACE.NON_CANDIDATES);
        pilsb.setCurrentPageNumber(1);
        pilsb.redirect();
        return "";
    }
    
    public String releaseYearbook()
    {
    	try {
    		
    		
    		List<PubItemVOPresentation> pubItemList = retrieveAllMembers();
            boolean allValid = true;
            for(PubItemVOPresentation pubItem : pubItemList)
            {
               boolean valid = validateItem(pubItem);
               if(!valid)
               {
            	   error(getMessage("Yearbook_ItemInvalid").replaceAll("\\$1", "\""+pubItem.getMetadata().getTitle().getValue() + "\""));
            	   allValid = false;
               }
               
            }
            
            if(!allValid)
            {
            	error(getMessage("Yearbook_ReleaseError"));
            }
            else
            {

    			TaskParamVO param = new TaskParamVO(getYearbookItem().getModificationDate(), "Submitting yearbook"); 
    			String paramXml = xmlTransforming.transformToTaskParam(param);
    			
    			itemHandler.submit(getYearbookItem().getVersion().getObjectId(), paramXml);
    			
    			String updatedItemXml = itemHandler.retrieve(getYearbookItem().getVersion().getObjectId());
    			this.yearbookItem = xmlTransforming.transformToPubItem(updatedItemXml);
    			
    			InitialContext initialContext = new InitialContext();
    			PubItemPublishing pubItemPublishing = (PubItemPublishing) initialContext.lookup(PubItemPublishing.SERVICE_NAME);
    			pubItemPublishing.releasePubItem(yearbookItem.getVersion(), yearbookItem.getModificationDate(), "Releasing pubItem", loginHelper.getAccountUser());
    			info(getMessage("Yearbook_ReleasedSuccessfully"));
            }
    		
    		
    		
    		
			
			
		} catch (Exception e) {
			error(getMessage("Yearbook_ReleaseError"));
			logger.error("Could not release Yearbook Item", e);
		}
        
		pilsb.redirect();
    	return "";
    }



}
