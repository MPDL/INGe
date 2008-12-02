package de.mpg.escidoc.pubman.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletResponse;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.export.ExportItemsSessionBean;
import de.mpg.escidoc.pubman.test.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.FrameworkItemTypeFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.LimitFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OffsetFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OwnerFilter;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.escidoc.services.framework.PropertyReader;
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
            
            String xmlItemList = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle()).retrieveItems(xmlparam);
    
            ItemVOListWrapper itemList = (ItemVOListWrapper) xmlTransforming.transformToItemListWrapper(xmlItemList);
    
            List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
            for(ItemVO item : itemList.getItemVOList())
            {
                pubItemList.add(new PubItemVO(item));
            }
            
            numberOfRecords = Integer.parseInt(itemList.getNumberOfRecords());
            returnList = CommonUtils.convertToPubItemVOPresentationList(pubItemList);
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
                pssb.getStoredPubItems().remove(pubItem.getVersion().getObjectIdAndVersion());
        }
       
        getBasePaginatorListSessionBean().redirect();
       
        return "";
    }

    @Override
    public String getListPageName()
    {
        return "CartItemsPage.jsp";
    }
    
    
}
