package de.mpg.escidoc.pubman.yearbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.escidoc.pubman.desktop.Login;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.escidoc.pubman.multipleimport.ImportLog;
import de.mpg.escidoc.pubman.search.SearchRetrieverRequestBean;
import de.mpg.escidoc.pubman.search.bean.criterion.ObjectCriterion;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemRelationVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.BooleanOperator;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;
import de.mpg.escidoc.services.search.query.SearchQuery.SortingOrder;
import de.mpg.escidoc.services.validation.ItemValidating;
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
public class YearbookInvalidItemsRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA>
{
    private static Logger logger = Logger.getLogger(YearbookInvalidItemsRetrieverRequestBean.class);
    public static String BEAN_NAME = "YearbookMembersRetrieverRequestBean";
   
    /**
     * This workspace's user.
     */
    AccountUserVO userVO;
    
    /**
     * The total number of records
     */
    private int numberOfRecords;
    
    private Search searchService;
    
    public YearbookInvalidItemsRetrieverRequestBean()
    {
        super((PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class), false); 
        //logger.info("RenderResponse: "+FacesContext.getCurrentInstance().getRenderResponse());
        //logger.info("ResponseComplete: "+FacesContext.getCurrentInstance().getResponseComplete());
       
        
    }
    
   

    @Override
    public void init()
    {
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

    @Override
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc)
    { 
        List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
        YearbookItemSessionBean yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);
        
        if(yisb.getInvalidItemMap().size()>0)
        {
            
        
            try
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
             
                query.setStartRecord(new PositiveInteger(String.valueOf(offset+1)));
                query.setMaximumRecords(new NonNegativeInteger(String.valueOf(20)));
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
                ItemContainerSearchResult result = this.searchService.searchForItemContainer(query);
                
                
                
                pubItemList =  SearchRetrieverRequestBean.extractItemsOfSearchResult(result);
                this.numberOfRecords = Integer.parseInt(result.getTotalNumberOfResults().toString());
                
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
                catch (Exception e)
                {
                    logger.error("Error in retrieving items", e);
                    error("Error in retrieving items");
                    numberOfRecords = 0; 
                }
        }
        
            return pubItemList;

    }

   

  
 

   
    
  
   

    /**
     * Reads out the item state parameter from the HTTP GET request and sets an default value if it is null.
     */
    @Override
    public void readOutParameters()
    {
      
        
      
    }

    @Override
    public String getType()
    {
        return "SearchResult";
    }
    
    @Override
    public String getListPageName()
    {
        return "YearbookMembersPage.jsp";
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



    @Override
    public boolean isItemSpecific()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
