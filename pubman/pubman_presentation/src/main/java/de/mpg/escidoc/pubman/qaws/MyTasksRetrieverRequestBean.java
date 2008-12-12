package de.mpg.escidoc.pubman.qaws;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.escidoc.pubman.desktop.Navigation;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the Quality Assurance Workspace
 * It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean and adds additional functionality for filtering the items by their state.
 * It extends the MyItemsRetriever RequestBean because it has a similar behaviour regarding item state filters.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class MyTasksRetrieverRequestBean extends MyItemsRetrieverRequestBean
{
    public static String BEAN_NAME = "MyTasksRetrieverRequestBean";
    
    private int numberOfRecords;
    
    /**
     * The currently selected context filter.
     */
    private String selectedContext;
    
    /**
     * The HTTP GET parameter name for the context filter.
     */
    private static String parameterSelectedContext = "context"; 
    
    /**
     * A list with menu entries for the context filter menu.
     */
    private List<SelectItem> contextSelectItems;
    
    
    public MyTasksRetrieverRequestBean()
    {
        super();
    }
    
    @Override
    public void init()
    {
        checkLogin();
        initSelectionMenu();
        
    }
    
    @Override
    public int getTotalNumberOfRecords()
    {
        return this.numberOfRecords;
    }

    @Override
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc)
    {
        List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();
        try
        {
            LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
            InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
      
            checkSortCriterias(sc);
            // define the filter criteria
            FilterTaskParamVO filter = new FilterTaskParamVO();
           
            Filter f2 = filter.new FrameworkItemTypeFilter(PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
            filter.getFilterList().add(f2);
            
            if (getSelectedItemState().toLowerCase().equals("all"))
            {
                Filter f3 = filter.new ItemStatusFilter(PubItemVO.State.SUBMITTED);
                filter.getFilterList().add(0,f3);
                Filter f12 = filter.new ItemStatusFilter(PubItemVO.State.RELEASED);
                filter.getFilterList().add(0,f12);
                Filter f13 = filter.new ItemStatusFilter(PubItemVO.State.IN_REVISION);
                filter.getFilterList().add(0,f13);
                
                //all public status except withdrawn
                Filter f4 = filter.new ItemPublicStatusFilter(PubItemVO.State.IN_REVISION);
                filter.getFilterList().add(0,f4);
                Filter f5 = filter.new ItemPublicStatusFilter(PubItemVO.State.PENDING);
                filter.getFilterList().add(0,f5);
                Filter f6 = filter.new ItemPublicStatusFilter(PubItemVO.State.SUBMITTED);
                filter.getFilterList().add(0,f6);
                Filter f7 = filter.new ItemPublicStatusFilter(PubItemVO.State.RELEASED);
                filter.getFilterList().add(0,f7);
            }
            else
            {
                Filter f3 = filter.new ItemStatusFilter(PubItemVO.State.valueOf(getSelectedItemState()));
                filter.getFilterList().add(0,f3);
                
                //all public status except withdrawn
                Filter f4 = filter.new ItemPublicStatusFilter(PubItemVO.State.IN_REVISION);
                filter.getFilterList().add(0,f4);
                Filter f5 = filter.new ItemPublicStatusFilter(PubItemVO.State.PENDING);
                filter.getFilterList().add(0,f5);
                Filter f6 = filter.new ItemPublicStatusFilter(PubItemVO.State.SUBMITTED);
                filter.getFilterList().add(0,f6);
                Filter f7 = filter.new ItemPublicStatusFilter(PubItemVO.State.RELEASED);
                filter.getFilterList().add(0,f7);
            }
            
            
            if (!getSelectedContext().toLowerCase().equals("all"))
            {
                Filter f10 = filter.new ContextFilter(getSelectedContext());
                filter.getFilterList().add(f10);
            }
            
           
            
            Filter f11 = filter.new OrderFilter(sc.getSortPath(), sc.getSortOrder());
            filter.getFilterList().add(f11);
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
            numberOfRecords = 0;
        }
        return returnList;
        

    }
    
    /**
     * Reads out the parameters from HTTP-GET request for the selected item state and the selected context filter. Sets default values if they are null.
     */
    @Override
    public void readOutParameters()
    {
        String selectedItemState = getExternalContext().getRequestParameterMap().get(parameterSelectedItemState);
        if (selectedItemState==null)
        {
            setSelectedItemState("all");
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

    /**
     * Sets the selected context filter
     * @param selectedContext
     */
    public void setSelectedContext(String selectedContext)
    {
        this.selectedContext = selectedContext;
        getBasePaginatorListSessionBean().getParameterMap().put(parameterSelectedContext, selectedContext);
    }

    /**
     * Returns the selected context filter
     * @return
     */
    public String getSelectedContext()
    {
        return selectedContext;
    }
    
    /**
     * Returns a label for the selected context.
     * @return
     */
    public String getSelectedContextLabel()
    {
        String returnString = "";
        
        if (!getSelectedContext().equals("all"))
        {
            ContextListSessionBean clsb = (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
            List<PubContextVOPresentation> contextVOList = clsb.getModeratorContextList();
            
            
            for(PubContextVOPresentation contextVO : contextVOList)
            {
                    if(contextVO.getReference().getObjectId().equals(getSelectedContext()))
                    {
                        returnString = contextVO.getName();
                        break;
                    }
            }
        }
        return returnString;
    }
    
    
    /**
     * Returns a list with menu entries for the item state filter menu.
     */
    public List<SelectItem> getItemStateSelectItems()
    {
        List<SelectItem> itemStateSelectItems = new ArrayList<SelectItem>();
        itemStateSelectItems.add(new SelectItem("all",getLabel("EditItem_NO_ITEM_SET")));
        itemStateSelectItems.add(new SelectItem(PubItemVO.State.SUBMITTED.name(), getLabel(i18nHelper.convertEnumToString(PubItemVO.State.SUBMITTED))));
        itemStateSelectItems.add(new SelectItem(PubItemVO.State.RELEASED.name(), getLabel(i18nHelper.convertEnumToString(PubItemVO.State.RELEASED))));
        itemStateSelectItems.add(new SelectItem(PubItemVO.State.IN_REVISION.name(), getLabel(i18nHelper.convertEnumToString(PubItemVO.State.IN_REVISION))));
        setItemStateSelectItems(itemStateSelectItems);
        
        return itemStateSelectItems;
    }
    
    /**
     * Initializes the menu for the context filtering.
     */
    private void initSelectionMenu()
    {
        
        /*
        //item states
        List<SelectItem> itemStateSelectItems = new ArrayList<SelectItem>();
        itemStateSelectItems.add(new SelectItem("all",getLabel("EditItem_NO_ITEM_SET")));
        itemStateSelectItems.add(new SelectItem(PubItemVO.State.SUBMITTED.name(), getLabel(i18nHelper.convertEnumToString(PubItemVO.State.SUBMITTED))));
        itemStateSelectItems.add(new SelectItem(PubItemVO.State.RELEASED.name(), getLabel(i18nHelper.convertEnumToString(PubItemVO.State.RELEASED))));
        itemStateSelectItems.add(new SelectItem(PubItemVO.State.IN_REVISION.name(), getLabel(i18nHelper.convertEnumToString(PubItemVO.State.IN_REVISION))));
        setItemStateSelectItems(itemStateSelectItems);
        */
        
        //Contexts (Collections)
        ContextListSessionBean clsb = (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
        List<PubContextVOPresentation> contextVOList = clsb.getModeratorContextList();
        
        contextSelectItems = new ArrayList<SelectItem>();
        contextSelectItems.add(new SelectItem("all", getLabel("EditItem_NO_ITEM_SET")));
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

    /**
     * Sets the current menu items for the context filter menu.
     * @param contextSelectItems
     */
    public void setContextSelectItems(List<SelectItem> contextSelectItems)
    {
        this.contextSelectItems = contextSelectItems;
    }

    /**
     * Returns the mneu items for the context filter menu.
     * @return
     */
    public List<SelectItem> getContextSelectItems()
    {
        return contextSelectItems;
    }
    
    /**
     * Called by JSF whenever the context filter menu is changed. Causes a redirect to the page with updated context GET parameter.
     * @return
     */
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
    
    @Override
    public String getListPageName()
    {
        return "QAWSPage.jsp";
    }
}
