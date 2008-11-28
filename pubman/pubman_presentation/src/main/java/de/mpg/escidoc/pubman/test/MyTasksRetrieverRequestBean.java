package de.mpg.escidoc.pubman.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.desktop.Navigation;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ContextFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.FrameworkItemTypeFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemPublicStatusFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemStatusFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.LimitFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OffsetFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OwnerFilter;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class MyTasksRetrieverRequestBean extends MyItemsRetrieverRequestBean
{
    public static String BEAN_NAME = "MyTasksRetrieverRequestBean";
    
    private int numberOfRecords;
    
    private String selectedContext;
    
    private static String parameterSelectedContext = "context"; 
    
    private List<SelectItem> contextSelectItems;
    
    @Override
    public void init()
    {
        //super.init();
        Navigation nav = (Navigation) getRequestBean(Navigation.class);
        nav.setShowExportMenuOption(true);
        
        initContextMenu();
        setItemStateSelectItems(Arrays.asList(this.i18nHelper.getSelectItemsForEnum(false, new PubItemVO.State[]{PubItemVO.State.SUBMITTED, PubItemVO.State.RELEASED, PubItemVO.State.IN_REVISION})));
    }
    
    @Override
    public int getTotalNumberOfRecords()
    {
        return this.numberOfRecords;
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
            
            if (getSelectedItemState().toLowerCase().equals("withdrawn"))
            {
                Filter f3 = filter.new ItemPublicStatusFilter(PubItemVO.State.WITHDRAWN);
                filter.getFilterList().add(0,f3);
            }
            else
            {
                if (!"all".equals(getSelectedItemState()))
                {
                    Filter f3 = filter.new ItemStatusFilter(PubItemVO.State.valueOf(getSelectedItemState()));
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
            
            Filter f10 = filter.new ContextFilter(getSelectedContext());
            filter.getFilterList().add(f10);
            
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
    
    @Override
    public void readOutParameters()
    {
        String selectedItemState = getExternalContext().getRequestParameterMap().get(parameterSelectedItemState);
        if (selectedItemState==null)
        {
            setSelectedItemState("SUBMITTED");
        }
        else
        {
            setSelectedItemState(selectedItemState);
        }
        
        String context = getExternalContext().getRequestParameterMap().get(parameterSelectedContext);
        if (context==null)
        {
            setSelectedContext((String)getContextSelectItems().get(0).getValue());
        }
        else
        {
            setSelectedContext(context);
        }
        
    }

    @Override
    public String getType()
    {
        return "MyTasks";
    }

    public void setSelectedContext(String selectedContext)
    {
        this.selectedContext = selectedContext;
        getBasePaginatorListSessionBean().getParameterMap().put(parameterSelectedContext, selectedContext);
    }

    public String getSelectedContext()
    {
        return selectedContext;
    }
    
    private void initContextMenu()
    {
        //Contexts (Collections)
        ContextListSessionBean clsb = (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
        List<PubContextVOPresentation> contextVOList = clsb.getModeratorContextList();
        
        contextSelectItems = new ArrayList<SelectItem>();
        
        for(int i=0; i<contextVOList.size(); i++)
        {
            String workflow = "null";
            if (contextVOList.get(i).getAdminDescriptor().getWorkflow()!= null)
            {
                workflow = contextVOList.get(i).getAdminDescriptor().getWorkflow().toString();
            }
            contextSelectItems.add(new SelectItem(contextVOList.get(i).getReference().getObjectId(), contextVOList.get(i).getName()+" -- "+workflow));
  
        }
          
               
        
       
    }

    public void setContextSelectItems(List<SelectItem> contextSelectItems)
    {
        this.contextSelectItems = contextSelectItems;
    }

    public List<SelectItem> getContextSelectItems()
    {
        return contextSelectItems;
    }
    
    public String changeContext()
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
}
