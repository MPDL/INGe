package de.mpg.escidoc.pubman.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.escidoc.services.framework.PropertyReader;

public class PubItemListSessionBean extends BasePaginatorListSessionBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA>
{
    public static String BEAN_NAME = "PubItemListSessionBean";
    
    /*
    public static String[] sortCriteriaNames = new String[]{"TITLE", "EVENT_TITLE", "GENRE",
          "PUBLISHING_INFO", "REVIEW_METHOD", "MODIFICATION_DATE", "CONTEXT", "STATE", "OWNER"};
    
    public static String[] sortCriteriaFilters = new String[]{
        "/md-records/md-record/publication/title",
        "/md-records/md-record/publication/event/title",
        "/md-records/md-record/publication/type",
        "/md-records/md-record/publication/source/publishing-info/publisher",
        "/md-records/md-record/publication/review-method",
        "/properties/version/date",
        "/properties/context/title",
        "/properties/version/status",
        "/properties/created-by/title" 
    };
    
    */
    public static enum SORT_CRITERIA
    {
        TITLE ("escidoc.title", "/md-records/md-record/publication/title"),
        EVENT_TITLE ("escidoc.any-event", "/md-records/md-record/publication/event/title"),
        SOURCE_TITLE ("escidoc.any-source", ""),
        GENRE ("escidoc.genre", "/md-records/md-record/publication/type"),
        DATE ("escidoc.any-dates", ""),
        CREATOR ("escidoc.complete-name", ""),
        PUBLISHING_INFO ("escidoc.publisher", "/md-records/md-record/publication/source/publishing-info/publisher"),
        MODIFICATION_DATE ("escidoc.last-modification-date", "/last-modification-date"),
        STATE("escidoc.version.status", "/properties/version/status");
        
        private String index;
        private String sortPath;
        private String sortOrder;
        
        SORT_CRITERIA(String index, String sortPath)
        {
            this.setIndex(index);
            this.setSortPath(sortPath);
            this.sortOrder="";
        }

        public void setIndex(String index)
        {
            this.index = index;
        }

        public String getIndex()
        {
            return index;
        }

        public void setSortPath(String sortPath)
        {
            this.sortPath = sortPath;
        }

        public String getSortPath()
        {
            return sortPath;
        }

        public void setSortOrder(String sortOrder)
        {
            this.sortOrder = sortOrder;
        }

        public String getSortOrder()
        {
            return sortOrder;
        }
        
        
        
    }
    
    private static String parameterSelectedSortBy = "sortBy";
    
    private static String parameterSelectedSortOrder = "sortOrder";

    private List<SelectItem> sortBySelectItems;
    
    private String selectedSortBy;
    
    private String selectedSortOrder;
    
    private String subMenu = "VIEW";
    
    private String listType = "BIB";

    
    public PubItemListSessionBean()
    {
        super();
        
        sortBySelectItems = new ArrayList<SelectItem>();
        
        for (SORT_CRITERIA sc : SORT_CRITERIA.values())
        {
            sortBySelectItems.add(new SelectItem(sc.name(), getLabel("ENUM_CRITERIA_"+sc.name())));
        }
        
        //sortBySelectItems = Arrays.asList(this.i18nHelper.getSelectItemsItemListSortBy());

        
        
        /*
        for (int i = 0; i < ItemVO.State.values().length; i++)
        {
            itemStateSelectItems.add(new SelectItem(getModifiedLink(parameterSelectedItemState, ItemVO.State.values()[i].toString()), ItemVO.State.values()[i].toString()));
        }
        */
       
    }
    
  
    /*
    public void changeSortBy(ValueChangeEvent event)
    {
        if (event.getOldValue() != null && !event.getOldValue().equals(event.getNewValue()))
        {
            try
            {
                setSelectedSortBy(event.getNewValue().toString());
                resetFilters();
                redirect();
            }
            catch (Exception e)
            {
               error("Could not redirect");
            }
        }
    }
    */
    
    public String changeToSortByState()
    {
        
        try
        {
            setSelectedSortBy("STATE");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeToSortByTitle()
    {
        
        try
        {
            setSelectedSortBy("TITLE");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeToSortByGenre()
    {
        
        try
        {
            setSelectedSortBy("GENRE");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }



    public String changeSortOrder()
    {
        if (selectedSortOrder.equals(OrderFilter.ORDER_ASCENDING))
        {
            setSelectedSortOrder(OrderFilter.ORDER_DESCENDING);
        }
        else
        {
            setSelectedSortOrder(OrderFilter.ORDER_ASCENDING);
        }
        try
        {
            setSelectedSortOrder(selectedSortOrder);
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeSubmenuToView()
    {
        
        try
        {
            setSubMenu("VIEW");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeSubmenuToFilter()
    {
        
        try
        {
            setSubMenu("FILTER");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeSubmenuToSorting()
    {
        
        try
        {
            setSubMenu("SORTING");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeListTypeToBib()
    {
        
        try
        {
            setListType("BIB");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeListTypeToGrid()
    {
        
        try
        {
            setListType("GRID");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public boolean getIsAscending()
    {
        return selectedSortOrder.equals(OrderFilter.ORDER_ASCENDING);
    }


    public void setSortBySelectItems(List<SelectItem> sortBySelectItems)
    {
        this.sortBySelectItems = sortBySelectItems;
    }


    public List<SelectItem> getSortBySelectItems()
    {
        return sortBySelectItems;
    }


    public void setSelectedSortBy(String selectedSortBy)
    {
        this.selectedSortBy = selectedSortBy;
        getParameterMap().put(parameterSelectedSortBy, selectedSortBy);
        
    }


    public String getSelectedSortBy()
    {
        return selectedSortBy;
    }




    public String getSelectedSortOrder()
    {
        return selectedSortOrder;
    }




    public void setSelectedSortOrder(String selectedSortOrder)
    {
        this.selectedSortOrder = selectedSortOrder;
        getParameterMap().put(parameterSelectedSortOrder, selectedSortOrder);
    }



    @Override
    protected void readOutParameters()
    {
        String sortBy = getExternalContext().getRequestParameterMap().get(parameterSelectedSortBy);
        
        if (sortBy!=null)
        {
            setSelectedSortBy(sortBy);
        }
        else
        {
            setSelectedSortBy("TITLE");
        }
        
        String sortOrder = getExternalContext().getRequestParameterMap().get(parameterSelectedSortOrder);
        if (sortOrder!=null)
        {
            setSelectedSortOrder(sortOrder);
        }
        else
        {
            setSelectedSortOrder(OrderFilter.ORDER_ASCENDING);
        }
        
       
        
    }



    @Override
    public SORT_CRITERIA getAdditionalFilters()
    {
        SORT_CRITERIA sc = SORT_CRITERIA.valueOf(getSelectedSortBy());
        sc.setSortOrder(getSelectedSortOrder());
        return sc;
    }


    public void setSubMenu(String subMenu)
    {
        this.subMenu = subMenu;
    }


    public String getSubMenu()
    {
        return subMenu;
    }


    @Override
    protected void pageTypeChanged()
    {
       subMenu = "VIEW";
       getParameterMap().clear();
        
    }


    public void setListType(String listType)
    {
        this.listType = listType;
    }


    public String getListType()
    {
        return listType;
    }

    
    public String addSelectedToCart()
    {
        PubItemStorageSessionBean pubItemStorage = (PubItemStorageSessionBean) getSessionBean(PubItemStorageSessionBean.class);
        int number = 0;
        for (PubItemVOPresentation pubItem : getCurrentPartList())
        {
            if (pubItem.getSelected())
            {
                pubItemStorage.getStoredPubItems().add(pubItem);
                number++;
            }
        }
        info(number + " items were added to the basket.");
        
        redirect();
       
        return "";
    }

    @Override
    protected void saveState()
    {
       
        
    }
    
    public String startExport() {
        setSubMenu("EXPORT");
        redirect();
        return"";
    }


    
   
}
