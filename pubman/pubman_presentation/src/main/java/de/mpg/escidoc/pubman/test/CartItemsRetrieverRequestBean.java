package de.mpg.escidoc.pubman.test;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.test.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class CartItemsRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, SORT_CRITERIA>
{
    public static String BEAN_NAME = "CartItemsRetrieverRequestBean";
    private int numberOfRecords;

    public CartItemsRetrieverRequestBean()
    {
        super((PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class));
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return numberOfRecords;
    }

    @Override
    public String getType()
    {
        return "CartItems";
    }

    @Override
    public void init()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void readOutParameters()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc)
    {
        List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();
        
        try
        {
            PubItemStorageSessionBean pssb = (PubItemStorageSessionBean)getSessionBean(PubItemStorageSessionBean.class);
            
            LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
            InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
      
            
            List<ItemRO> idList = new ArrayList<ItemRO>();
            for(ItemRO id : pssb.getStoredPubItems().values())
            {
                idList.add(id);
            }
            
            if (idList.size()>0)
            {
                checkSortCriterias(sc);
                
                // define the filter criteria
                FilterTaskParamVO filter = new FilterTaskParamVO();
                
                Filter f1 = filter.new ItemRefFilter(idList);
                filter.getFilterList().add(0,f1);
                
                Filter f10 = filter.new OrderFilter(sc.getSortPath(), sc.getSortOrder());
                filter.getFilterList().add(f10);
                Filter f8 = filter.new LimitFilter(String.valueOf(limit));
                filter.getFilterList().add(f8);
                Filter f9 = filter.new OffsetFilter(String.valueOf(offset));
                filter.getFilterList().add(f9);
               
                String xmlparam = xmlTransforming.transformToFilterTaskParam(filter); 
                
                String xmlItemList = "";
                if (loginHelper.getESciDocUserHandle()!=null)
                  xmlItemList = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle()).retrieveItems(xmlparam);
                else
                  xmlItemList = ServiceLocator.getItemHandler().retrieveItems(xmlparam);
        
                ItemVOListWrapper itemList = (ItemVOListWrapper) xmlTransforming.transformToItemListWrapper(xmlItemList);
        
                List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
                for(ItemVO item : itemList.getItemVOList())
                {
                    pubItemList.add(new PubItemVO(item));
                }
                
                numberOfRecords = Integer.parseInt(itemList.getNumberOfRecords());
                returnList = CommonUtils.convertToPubItemVOPresentationList(pubItemList);
            }
            else
            {
                numberOfRecords = 0;
            }
                      

        }
        catch (Exception e)
        {
          error("Error in retrieving items");
           
        }
        return returnList;

    }

   

    public String deleteSelected()
    {
        PubItemStorageSessionBean pssb = (PubItemStorageSessionBean)getSessionBean(PubItemStorageSessionBean.class);
       
        
        for (PubItemVOPresentation pubItem : getBasePaginatorListSessionBean().getCurrentPartList())
        {
            if (pubItem.getSelected())
                pssb.getStoredPubItems().remove(pubItem.getVersion().getObjectId());
        }
       
        getBasePaginatorListSessionBean().redirect();
       
        return "";
    }

    @Override
    public String getListPageName()
    {
        return "CartItemsPage.jsp";
    }
    
    protected void checkSortCriterias(SORT_CRITERIA sc)
    {
        if  (sc.getSortPath()== null || sc.getSortPath().equals(""))
        {
            error(getMessage("depositorWS_sortingNotSupported").replace("$1", getLabel("ENUM_CRITERIA_"+sc.name())));
            //getBasePaginatorListSessionBean().redirect();
        }
        
    }
    
    
    /**
     * Method needs to be called over this bean, because it has to be called first in order to save the selections in the list
     */
    public void updateExportOptions()
    {
        ExportItems exportItemsBean = (ExportItems)getRequestBean(ExportItems.class);
        exportItemsBean.updateExportFormats();
        
    }
    
    
    
}
