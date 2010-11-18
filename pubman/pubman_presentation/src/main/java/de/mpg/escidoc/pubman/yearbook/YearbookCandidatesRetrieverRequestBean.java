package de.mpg.escidoc.pubman.yearbook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.affiliation.AffiliationTree;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.escidoc.pubman.search.SearchRetrieverRequestBean;
import de.mpg.escidoc.pubman.util.PubItemResultVO;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.pubman.yearbook.YearbookItemSessionBean.YBWORKSPACE;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ItemRelationVO;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.search.query.SearchQuery;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;
import de.mpg.escidoc.services.search.query.SearchQuery.SortingOrder;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the Yearbook workspace.
 * It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean and adds additional functionality for filtering the items by their state.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3780 $ $LastChangedDate: 2010-07-23 10:01:12 +0200 (Fri, 23 Jul 2010) $
 *
 */
public class YearbookCandidatesRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA>
{
    private static Logger logger = Logger.getLogger(YearbookCandidatesRetrieverRequestBean.class);
    public static String BEAN_NAME = "YearbookCandidatesRetrieverRequestBean";
    private String selectedSortOrder;
    /**
     * This workspace's user.
     */
    AccountUserVO userVO;
    
    /**
     * The GET parameter name for the item state.
     */
    protected static String parameterSelectedItemState = "itemState";
    
    /**org unit filter.
     */
    private static String parameterSelectedOrgUnit = "orgUnit"; 
    
 
    /**
     * The total number of records
     */
    private int numberOfRecords;
    

    private Search searchService;
    private YearbookItemSessionBean yisb;
    private PubItemListSessionBean pilsb;
    
    public YearbookCandidatesRetrieverRequestBean()
    {
        super((PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class), false);  
        //logger.info("RenderResponse: "+FacesContext.getCurrentInstance().getRenderResponse());
        //logger.info("ResponseComplete: "+FacesContext.getCurrentInstance().getResponseComplete());
        
    }

    @Override
    public void init()
    {
        pilsb = (PubItemListSessionBean)getBasePaginatorListSessionBean();
        HttpServletRequest requ = (HttpServletRequest)getExternalContext().getRequest();
        
        yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);
        try
        {
            InitialContext initialContext = new InitialContext();
            this.searchService = (Search) initialContext.lookup(Search.SERVICE_NAME);
            
        }
        catch (NamingException e)
        {
            logger.error("Error when trying to find search service.", e);
            error("Did not find Search service");
        }
      
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return numberOfRecords;
    }
    
    /**
     * Reads out the item state parameter from the HTTP GET request and sets an default value if it is null.
     */
    @Override
    public void readOutParameters()
    {
        String orgUnit = getExternalContext().getRequestParameterMap().get(parameterSelectedOrgUnit);
        if (orgUnit==null) 
        {
            if(getSessionBean().getSelectedOrgUnit()!=null || yisb.getYearbookItem()==null)
            {
                setSelectedOrgUnit(getSessionBean().getSelectedOrgUnit());
            }
            else
            {
                setSelectedOrgUnit(yisb.getYearbookItem().getMetadata().getCreators().get(0).getOrganization().getIdentifier()); 
            }
            
        }
        else
        {
            setSelectedOrgUnit(orgUnit);
        }
    }

    @Override
    public String getType()
    {
        return "SearchResult";
    }
    
    @Override
    public String getListPageName()
    {
        return "YearbookCandidatesPage.jsp";
    }

    @Override
    public boolean isItemSpecific() 
    {
        return false;
    } 
 
    public String addSelectedToYearbook()
    {
        YearbookItemSessionBean yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class); 
        List<ItemRO> selected = new ArrayList<ItemRO>();
        for(PubItemVOPresentation item : ((PubItemListSessionBean)getBasePaginatorListSessionBean()).getSelectedItems())
        {
            selected.add(item.getVersion());
        }
        yisb.addMembers(selected);
        this.getBasePaginatorListSessionBean().update();
        return "";
    }
    
    public String removeSelectedFromYearbook()
    {
        YearbookItemSessionBean yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class); 
        List<ItemRO> selected = new ArrayList<ItemRO>();
        for(PubItemVOPresentation item : ((PubItemListSessionBean)getBasePaginatorListSessionBean()).getSelectedItems())
        {
            selected.add(item.getVersion());
        }
        yisb.removeMembers(selected); 
        this.getBasePaginatorListSessionBean().update();
        return "";
    }
    
    public List<SelectItem> getOrgUnitSelectItems()
    {
        return this.getSessionBean().getOrgUnitSelectItems(); 
    }

    public void setSelectedOrgUnit(String selectedOrgUnit)
    {        
        this.getSessionBean().setSelectedOrgUnit(selectedOrgUnit); 
        getBasePaginatorListSessionBean().getParameterMap().put(parameterSelectedOrgUnit, selectedOrgUnit);
    }

    public String getSelectedOrgUnit()
    {
        return this.getSessionBean().getSelectedOrgUnit(); 
    } 
    
    public YearbookCandidatesSessionBean getSessionBean()
    {
        return (YearbookCandidatesSessionBean) getSessionBean(YearbookCandidatesSessionBean.class);
    }
    
    /**
     * Called by JSF whenever the organizational unit filter menu is changed. Causes a redirect to the page with updated context GET parameter.
     * @return
     */
    public String changeOrgUnit()
    {
        try
        { 
            getBasePaginatorListSessionBean().setCurrentPageNumber(1);
            getBasePaginatorListSessionBean().redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
    }

    public String getSelectedSortOrder()
    {
        return selectedSortOrder;
    }

    public void setSelectedSortOrder(String selectedSortOrder)
    {
        this.selectedSortOrder = selectedSortOrder;
    }
    
    private SearchQuery getCandidatesQuery() throws Exception
    {
    	 MetadataSearchQuery mdQuery = getCandidateQuery();
        if (getSelectedOrgUnit()!=null && !getSelectedOrgUnit().toLowerCase().equals("all")) 
        {
        	mdQuery.addCriterion(new MetadataSearchCriterion(CriterionType.ORGANIZATION_PIDS, getSelectedOrgUnit(), LogicalOperator.AND)); 
        }
        String additionalQuery = yisb.getYearbookItem().getLocalTags().get(0);
        PlainCqlQuery query = new PlainCqlQuery(mdQuery.getCqlQuery() + " AND " +  additionalQuery);
        return query;
    }
    
    public static MetadataSearchQuery getCandidateQuery() throws Exception
    {
    	 YearbookItemSessionBean yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);
    	 ArrayList<String> contentTypes = new ArrayList<String>();
         String contentTypeIdPublication = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
         contentTypes.add( contentTypeIdPublication );
             
         ArrayList<MetadataSearchCriterion> mdsList = new ArrayList<MetadataSearchCriterion>();
         MetadataSearchCriterion objectTypeMds = new MetadataSearchCriterion(CriterionType.OBJECT_TYPE, "item", LogicalOperator.AND);
         mdsList.add(objectTypeMds);

         //MetadataSearchCriterion genremd = new MetadataSearchCriterion(CriterionType.ANY, );
         int i =0;
         for(Genre genre : yisb.getYearbookContext().getAdminDescriptor().getAllowedGenres())
         {
             if (i==0)
             {
                 objectTypeMds.addSubCriteria(new MetadataSearchCriterion(CriterionType.GENRE, genre.getUri(), LogicalOperator.AND));
             }
             else
             {
                 objectTypeMds.addSubCriteria(new MetadataSearchCriterion(CriterionType.GENRE, genre.getUri(), LogicalOperator.OR));
             }
             i++;
         }
         if(yisb.getNumberOfMembers()>0)
         {
             for(ItemRelationVO rel : yisb.getYearbookItem().getRelations())
             {
             	mdsList.add(new MetadataSearchCriterion(CriterionType.IDENTIFIER, rel.getTargetItemRef().getObjectId(), LogicalOperator.NOT));
             	}
             }
         MetadataSearchQuery mdQuery = new MetadataSearchQuery( contentTypes, mdsList );
         return mdQuery;
    }
    
    private SearchQuery getNonCandidatesQuery() throws Exception
    {
        ArrayList<String> contentTypes = new ArrayList<String>();
        String contentTypeIdPublication = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
        contentTypes.add( contentTypeIdPublication );
        ArrayList<MetadataSearchCriterion> mdsList = new ArrayList<MetadataSearchCriterion>();
        MetadataSearchCriterion objectTypeMds = new MetadataSearchCriterion(CriterionType.OBJECT_TYPE, "item", LogicalOperator.AND);
        mdsList.add(objectTypeMds);
        //MetadataSearchCriterion genremd = new MetadataSearchCriterion(CriterionType.ANY, );
        if(yisb.getNumberOfMembers()>0)
        {
            for(ItemRelationVO rel : yisb.getYearbookItem().getRelations())
            {
                mdsList.add(new MetadataSearchCriterion(CriterionType.IDENTIFIER, rel.getTargetItemRef().getObjectId(), LogicalOperator.NOT));
            }
        }
        if (!getSelectedOrgUnit().toLowerCase().equals("all")) 
        {
           mdsList.add(new MetadataSearchCriterion(CriterionType.ORGANIZATION_PIDS, getSelectedOrgUnit(), LogicalOperator.AND)); 
        }
        MetadataSearchQuery mdQuery = new MetadataSearchQuery( contentTypes, mdsList );
        String inverseQuery = yisb.getYearbookItem().getLocalTags().get(1);
        PlainCqlQuery query = new PlainCqlQuery(mdQuery.getCqlQuery() + " AND " +  inverseQuery);
        return query;
    }
    
    public static MetadataSearchQuery getMemberQuery() throws Exception
    {
   	 YearbookItemSessionBean yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);     
        ArrayList<String> contentTypes = new ArrayList<String>();
        String contentTypeIdPublication = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
        contentTypes.add( contentTypeIdPublication );
        
        ArrayList<MetadataSearchCriterion> mdsList = new ArrayList<MetadataSearchCriterion>();
        MetadataSearchCriterion objectTypeMds = new MetadataSearchCriterion(CriterionType.OBJECT_TYPE, "item", LogicalOperator.AND);
        mdsList.add(objectTypeMds);

        int i=0;
        for(ItemRelationVO rel : yisb.getYearbookItem().getRelations())
        {
            if(i==0)
            {
                objectTypeMds.addSubCriteria(new MetadataSearchCriterion(CriterionType.IDENTIFIER, rel.getTargetItemRef().getObjectId(), LogicalOperator.AND));
            }
            else
            {
                objectTypeMds.addSubCriteria(new MetadataSearchCriterion(CriterionType.IDENTIFIER, rel.getTargetItemRef().getObjectId(), LogicalOperator.OR));   
            }
            i++;
           
        }
        MetadataSearchQuery mdQuery = new MetadataSearchQuery( contentTypes, mdsList );
        return mdQuery;
    }
    
    private SearchQuery getMembersQuery() throws Exception
    {
        if(yisb.getNumberOfMembers()>0)
        {
        	MetadataSearchQuery mdQuery = getMemberQuery();     
        	if (!getSelectedOrgUnit().toLowerCase().equals("all")) 
            {
                mdQuery.addCriterion(new MetadataSearchCriterion(CriterionType.ORGANIZATION_PIDS, getSelectedOrgUnit(), LogicalOperator.AND)); 
            }
            return mdQuery;
                /*
                ItemValidating itemValidating = (ItemValidating) new InitialContext().lookup(ItemValidating.SERVICE_NAME);
                
                System.out.println("Validate " + pubItemList.size() + "items");
                long start = System.currentTimeMillis();
                for(PubItemVO item : pubItemList)
                {
                    PubItemVO pubitem = new PubItemVO(item);
                    
                    long startSingle=System.currentTimeMillis();
                    ValidationReportVO report = itemValidating.validateItemObject(pubitem);
                    long stopSingle=System.currentTimeMillis();
                    System.out.println(item.getVersion().getObjectId()+ " took " + (stopSingle-startSingle) + "ms");
                }
                long stop = System.currentTimeMillis();
                
                System.out.println("All " + pubItemList.size() +" took " + (stop-start) + "ms");
                */
        }
            return null;
    }

    private SearchQuery getInvalidMembersQuery() throws Exception
    {
      
        YearbookItemSessionBean yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);
        
        if(yisb.getInvalidItemMap().size()>0)
        {
            
                
                
                ArrayList<String> contentTypes = new ArrayList<String>();
                String contentTypeIdPublication = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
                contentTypes.add( contentTypeIdPublication );
                
                ArrayList<MetadataSearchCriterion> mdsList = new ArrayList<MetadataSearchCriterion>();
                MetadataSearchCriterion objectTypeMds = new MetadataSearchCriterion(CriterionType.OBJECT_TYPE, "item", LogicalOperator.AND);
                mdsList.add(objectTypeMds);

                int i=0;
                for(YearbookInvalidItemRO item : yisb.getInvalidItemMap().values())
                {
                    if(i==0)
                    {
                        objectTypeMds.addSubCriteria(new MetadataSearchCriterion(CriterionType.IDENTIFIER, item.getObjectId(), LogicalOperator.AND));
                    }
                    else
                    {
                        objectTypeMds.addSubCriteria(new MetadataSearchCriterion(CriterionType.IDENTIFIER, item.getObjectId(), LogicalOperator.OR));   
                    }
                    i++;
                   
                }

                MetadataSearchQuery query = new MetadataSearchQuery( contentTypes, mdsList );
                return query;
               
              
                
                /*
                
                ItemValidating itemValidating = (ItemValidating) new InitialContext().lookup(ItemValidating.SERVICE_NAME);
                
                System.out.println("Validate " + pubItemList.size() + "items");
                long start = System.currentTimeMillis();
                for(PubItemVO item : pubItemList)
                {
                    PubItemVO pubitem = new PubItemVO(item);
                    
                    long startSingle=System.currentTimeMillis();
                    ValidationReportVO report = itemValidating.validateItemObject(pubitem);
                    long stopSingle=System.currentTimeMillis();
                    System.out.println(item.getVersion().getObjectId()+ " took " + (stopSingle-startSingle) + "ms");
                }
                long stop = System.currentTimeMillis();
                
                System.out.println("All " + pubItemList.size() +" took " + (stop-start) + "ms");
                */
                
               
        }
        
            return null;
    }



    @Override
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc)
    {
        List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();  
        
        try
        {
        	if(yisb.getYearbookItem() != null)
        	{
        		
        	
            SearchQuery query = null; 
            if(yisb.getSelectedWorkspace().equals(YBWORKSPACE.CANDIDATES))
            {
                query = getCandidatesQuery();
            }
            else if (yisb.getSelectedWorkspace().equals(YBWORKSPACE.MEMBERS))
            {
                query = getMembersQuery();
            }
            else if (yisb.getSelectedWorkspace().equals(YBWORKSPACE.INVALID))
            {
                query = getInvalidMembersQuery();
            }
            else if (yisb.getSelectedWorkspace().equals(YBWORKSPACE.NON_CANDIDATES))
            {
                query = getNonCandidatesQuery();
            }
             
            query = new PlainCqlQuery(query.getCqlQuery());
            
            if(query!=null)
            {
                query.setStartRecord(new PositiveInteger(String.valueOf(offset+1)));
                query.setMaximumRecords(new NonNegativeInteger(String.valueOf(limit)));
                
                
                if(sc.getIndex()!=null)
                {
                    query.setSortKeys(sc.getIndex());
                }
        
                if(sc.getIndex() == null || !sc.getIndex().equals(""))
                {
                    if (sc.getSortOrder().equals("descending"))
                    {
                       
                        query.setSortOrder(SortingOrder.DESCENDING);
                    }
                       
                    else
                    {
                        query.setSortOrder(SortingOrder.ASCENDING);
                    } 
                }
                 
                System.out.println(query.getCqlQuery()); 
                ItemContainerSearchResult result = this.searchService.searchForItemContainer(query);
                
                pubItemList =  extractItemsOfSearchResult(result);
                this.numberOfRecords = Integer.parseInt(result.getTotalNumberOfResults().toString());
            }
        	}
        }
        catch (Exception e)
        {
            logger.error("Error in retrieving items", e);
            error("Error in retrieving items");
            numberOfRecords = 0; 
        }
        
        
        return pubItemList;
    }
    
     
    public ArrayList<PubItemVOPresentation> extractItemsOfSearchResult( ItemContainerSearchResult result ) { 
        
        List<SearchResultElement> results = result.getResultList(); 
        
        ArrayList<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
        for( int i = 0; i < results.size(); i++ ) {
            //check if we have found an item
            if( results.get( i ) instanceof ItemResultVO ) {
                // cast to PubItemResultVO
                ItemResultVO item = (ItemResultVO)results.get( i );
                PubItemResultVO pubItemResult = new PubItemResultVO( item, item.getSearchHitList(), item.getScore() ) ; 
                PubItemVOPresentation pubItemPres = new PubItemVOPresentation(pubItemResult);
                   
                if(yisb.getInvalidItemMap().containsKey(pubItemPres.getVersion().getObjectId()))
                { 
                    YearbookInvalidItemRO itemRO = yisb.getInvalidItemMap().get(pubItemPres.getVersion().getObjectId()); 
                    pubItemPres.setValidationMessages(YearbookItemSessionBean.getValidationMessages(this, itemRO.getValidationReport()));
                }
                pubItemList.add( pubItemPres );
            }
        }
        return pubItemList;
    }
    
    
    
    

}
