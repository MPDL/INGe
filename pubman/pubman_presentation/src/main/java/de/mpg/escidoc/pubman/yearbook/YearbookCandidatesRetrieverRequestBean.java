package de.mpg.escidoc.pubman.yearbook;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.affiliation.AffiliationTree;
import de.mpg.escidoc.pubman.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.escidoc.pubman.search.SearchRetrieverRequestBean;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;
import de.mpg.escidoc.services.search.query.SearchQuery.SortingOrder;

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
    
    /**
     * The menu entries of the item state filtering menu
     */
    private List<SelectItem> itemStateSelectItems;
    
    /**
     * The currently selected item state.
     */
    private String selectedItemState;
    private Search searchService;
    private YearbookItemSessionBean  yisb;
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

    @Override
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc)
    {
        
        List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
        
        
        try
        {
            
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
            
            
            if (!getSelectedOrgUnit().toLowerCase().equals("all")) 
            {
                   mdsList.add(new MetadataSearchCriterion(CriterionType.ORGANIZATION_PIDS, getSelectedOrgUnit(), LogicalOperator.AND)); 
            }
            
            MetadataSearchQuery mdQuery = new MetadataSearchQuery( contentTypes, mdsList );
            String additionalQuery = yisb.getYearbookItem().getLocalTags().get(0);
            
            
            PlainCqlQuery query = new PlainCqlQuery(mdQuery.getCqlQuery() + " AND " +  additionalQuery);
           
         
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
            ItemContainerSearchResult result = this.searchService.searchForItemContainer(query);
            
            pubItemList =  SearchRetrieverRequestBean.extractItemsOfSearchResult(result);
            this.numberOfRecords = Integer.parseInt(result.getTotalNumberOfResults().toString());
        
        
        
        
        
        
        
        
        /*
        

            
            
            LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
            InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
      
            checkSortCriterias(sc);
            
            // define the filter criteria
            FilterTaskParamVO filter = new FilterTaskParamVO();
            
            Filter f2 = filter.new FrameworkItemTypeFilter(PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
            filter.getFilterList().add(f2);
            
           
            Filter f7 = filter.new ItemPublicStatusFilter(PubItemVO.State.RELEASED);
            filter.getFilterList().add(0,f7);
            
               
           
              
            Filter f10 = filter.new OrderFilter(sc.getSortPath(), sc.getSortOrder());
            filter.getFilterList().add(f10);

            Filter f8 = filter.new LimitFilter(String.valueOf(limit));
            filter.getFilterList().add(f8);
            Filter f9 = filter.new OffsetFilter(String.valueOf(offset));
            filter.getFilterList().add(f9);
            
           
           
      
            
            String xmlparam = xmlTransforming.transformToFilterTaskParam(filter); 
          
            String xmlItemList = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle()).retrieveItems(xmlparam);

            ItemVOListWrapper itemList = (ItemVOListWrapper) xmlTransforming.transformToItemListWrapper(xmlItemList);

            List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
            for(ItemVO item : itemList.getItemVOList())
            {
                pubItemList.add(new PubItemVO(item));
            }
            
            numberOfRecords = Integer.parseInt(itemList.getNumberOfRecords());
            returnList = CommonUtils.convertToPubItemVOPresentationList(pubItemList);
        */
        }
        catch (Exception e)
        {
            logger.error("Error in retrieving items", e);
            error("Error in retrieving items");
            numberOfRecords = 0; 
        }
        
        return pubItemList;

    }

   

  
    /**
     * Sets the current item state filter
     * @param itemStateSelectItem
     */
    public void setItemStateSelectItems(List<SelectItem> itemStateSelectItem)
    {
        this.itemStateSelectItems = itemStateSelectItem;
    }

    /**
     * Sets and returns the menu entries of the item state filter menu.
     * @return
     */
    public List<SelectItem> getItemStateSelectItems()
    {
        itemStateSelectItems = new ArrayList<SelectItem>();
        itemStateSelectItems.add(new SelectItem("all", getLabel("ItemList_filterAllExceptWithdrawn")));
        itemStateSelectItems.add(new SelectItem(PubItemVO.State.PENDING.name(), getLabel(i18nHelper.convertEnumToString(PubItemVO.State.PENDING))));
        itemStateSelectItems.add(new SelectItem(PubItemVO.State.SUBMITTED.name(), getLabel(i18nHelper.convertEnumToString(PubItemVO.State.SUBMITTED))));
        itemStateSelectItems.add(new SelectItem(PubItemVO.State.RELEASED.name(), getLabel(i18nHelper.convertEnumToString(PubItemVO.State.RELEASED))));
        itemStateSelectItems.add(new SelectItem(PubItemVO.State.WITHDRAWN.name(), getLabel(i18nHelper.convertEnumToString(PubItemVO.State.WITHDRAWN))));
        itemStateSelectItems.add(new SelectItem(PubItemVO.State.IN_REVISION.name(), getLabel(i18nHelper.convertEnumToString(PubItemVO.State.IN_REVISION))));
        
        return itemStateSelectItems;
    }
    
    /**
     * Sets the selected item state filter
     * @param selectedItemState
     */
    public void setSelectedItemState(String selectedItemState)
    {
        this.selectedItemState = selectedItemState;
        getBasePaginatorListSessionBean().getParameterMap().put(parameterSelectedItemState, selectedItemState);
    }

    /**
     * Returns the currently selected item state filter
     * @return
     */
    public String getSelectedItemState()
    {
        return selectedItemState;
    }

 

    /**
     * Returns the label for the currently selected item state.
     * @return
     */
    public String getSelectedItemStateLabel()
    {
        String returnString = "";
        if (getSelectedItemState()!=null && !getSelectedItemState().equals("all"))
        {
            returnString =  getLabel(i18nHelper.convertEnumToString(PubItemVO.State.valueOf(getSelectedItemState())));
        }
        return returnString;
        
    }
    
    /**
     * Called by JSF whenever the item state menu is changed. 
     * @return
     */
    public String changeItemState()
    {
            try
            {
               
                getBasePaginatorListSessionBean().setCurrentPageNumber(1);
                getBasePaginatorListSessionBean().redirect();
            }
            catch (Exception e)
            {
               logger.error("Error during redirection.",e);
               error("Could not redirect");
            }
            return "";
        
    }
    
    /**
     * Called by JSF whenever the context filter menu is changed. Causes a redirect to the page with updated import GET parameter.
     * @return
     */
    public String changeImport()
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

    /**
     * Reads out the item state parameter from the HTTP GET request and sets an default value if it is null.
     */
    @Override
    public void readOutParameters()
    {
        String orgUnit = getExternalContext().getRequestParameterMap().get(parameterSelectedOrgUnit);
        if (orgUnit==null) 
        {
            if(getSessionBean().getSelectedOrgUnit()!=null)
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

}
