package de.mpg.escidoc.pubman.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.desktop.Navigation;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.FrameworkItemTypeFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the My Items workspace.
 * It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean and adds additional functionality for filtering the items by their state.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class MyItemsRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, OrderFilter>
{
    private static Logger logger = Logger.getLogger(MyItemsRetrieverRequestBean.class);
    public static String BEAN_NAME = "MyItemsRetrieverRequestBean";
   
    protected static String parameterSelectedItemState = "itemState";
    
    private int numberOfRecords;
    
    private List<SelectItem> itemStateSelectItems;
    
    private String selectedItemState;
    
    public MyItemsRetrieverRequestBean()
    {
        super((PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class));
        logger.info("RenderResponse: "+FacesContext.getCurrentInstance().getRenderResponse());
        logger.info("ResponseComplete: "+FacesContext.getCurrentInstance().getResponseComplete());
        
    }
    
    @Override
    public void init()
    {
        //activate export option in menu
        Navigation nav = (Navigation) getRequestBean(Navigation.class);
        nav.setShowExportMenuOption(true);
        
        
        
        itemStateSelectItems = Arrays.asList(i18nHelper.getSelectItemsItemState());
        
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return numberOfRecords;
    }

    @Override
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, OrderFilter orderFilter)
    {
        try
        {
            LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
            InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
      
            // define the filter criteria
            FilterTaskParamVO filter = new FilterTaskParamVO();
            
            Filter f1 = filter.new OwnerFilter(loginHelper.getAccountUser().getReference());
            filter.getFilterList().add(0,f1);
            Filter f2 = filter.new FrameworkItemTypeFilter(PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
            filter.getFilterList().add(f2);
            
            if (selectedItemState.toLowerCase().equals("withdrawn"))
            {
                Filter f3 = filter.new ItemPublicStatusFilter(PubItemVO.State.WITHDRAWN);
                filter.getFilterList().add(0,f3);
            }
            else
            {
                if (!"all".equals(selectedItemState))
                {
                    Filter f3 = filter.new ItemStatusFilter(PubItemVO.State.valueOf(selectedItemState));
                    filter.getFilterList().add(0,f3);
                }
            
                Filter f4 = filter.new ItemPublicStatusFilter(PubItemVO.State.IN_REVISION);
                filter.getFilterList().add(0,f4);
                Filter f5 = filter.new ItemPublicStatusFilter(PubItemVO.State.PENDING);
                filter.getFilterList().add(0,f5);
                Filter f6 = filter.new ItemPublicStatusFilter(PubItemVO.State.SUBMITTED);
                filter.getFilterList().add(0,f6);
                Filter f7 = filter.new ItemPublicStatusFilter(PubItemVO.State.RELEASED);
                filter.getFilterList().add(0,f7);
            }
            
            
            filter.getFilterList().add(orderFilter);
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
            return CommonUtils.convertToPubItemVOPresentationList(pubItemList);
        }
        catch (Exception e)
        {
           e.printStackTrace();
           return null;
        }

    }
    
    public void setItemStateSelectItems(List<SelectItem> itemStateSelectItem)
    {
        this.itemStateSelectItems = itemStateSelectItem;
    }

    public List<SelectItem> getItemStateSelectItems()
    {
        return itemStateSelectItems;
    }
    
    public void setSelectedItemState(String selectedItemState)
    {
        this.selectedItemState = selectedItemState;
        getBasePaginatorListSessionBean().getParameterMap().put(parameterSelectedItemState, selectedItemState);
    }

    public String getSelectedItemState()
    {
        return selectedItemState;
    }
    
    public String changeItemState()
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
    
    

    @Override
    public void readOutParameters()
    {
        String selectedItemState = getExternalContext().getRequestParameterMap().get(parameterSelectedItemState);
        if (selectedItemState==null)
        {
            setSelectedItemState("PENDING");
        }
        else
        {
            setSelectedItemState(selectedItemState);
        }
        
    }

    @Override
    public String getType()
    {
        return "MyItems";
    }
    
    @Override
    public String getListPageName()
    {
        return "DepositorWSPage.jsp";
    }
    
   
    
  
}
