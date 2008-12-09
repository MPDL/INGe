package de.mpg.escidoc.pubman.test;


import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.FacesBean;

public abstract class BasePaginatorListSessionBean<ListElementType, FilterType> extends FacesBean
{
    private static String parameterElementsPerPage = "elementsPerPage";
    private static String parameterPageNumber = "pageNumber";
    
    
    public String getParameterElementsPerPage()
    {
        return parameterElementsPerPage;
    }

    public String getParameterPageNumber()
    {
        return parameterPageNumber;
    }

    private List<SelectItem> elementsPerPageSelectItems;
   
    private List<PaginatorPage> paginatorPageList;
    
    private List<ListElementType> currentPartList;
    
    private int elementsPerPage;
    
    private int currentPageNumber;
    
    private String goToPage;
    
    protected BaseListRetrieverRequestBean<ListElementType, FilterType> getPaginatorListRetriever()
    {
        return paginatorListRetriever;
    }

    private PaginatorPage currentPaginatorPage;
    
    private Map<String, String> redirectParameterMap;
    
    private BaseListRetrieverRequestBean<ListElementType, FilterType> paginatorListRetriever;
    private int totalNumberOfElements = 0;
    
    private String pageType;
    private int elementsPerPageBottom;
    private int elementsPerPageTop;
    private String goToPageBottom;
    private String goToPageTop;
    
    public BasePaginatorListSessionBean()
    {
        redirectParameterMap = new HashMap<String, String>();
        
        elementsPerPageSelectItems = new ArrayList<SelectItem>();
        elementsPerPageSelectItems.add(new SelectItem("10","10"));
        elementsPerPageSelectItems.add(new SelectItem("25","25"));
        elementsPerPageSelectItems.add(new SelectItem("50","50"));
        elementsPerPageSelectItems.add(new SelectItem("100","100"));
        elementsPerPageSelectItems.add(new SelectItem("250","250"));
        
        paginatorPageList = new ArrayList<PaginatorPage>();

       
    }
    
    
    public void update()
    {

        
        String elemetsPerP = getExternalContext().getRequestParameterMap().get(parameterElementsPerPage);
        
        if (elemetsPerP!=null)
        {
            setElementsPerPage(Integer.parseInt(elemetsPerP));
        }
        else
        {
            setElementsPerPage(10);
        }
        
        String currentPNumber = getExternalContext().getRequestParameterMap().get(parameterPageNumber);
        if (currentPNumber!=null)
        {
            setCurrentPageNumber(Integer.parseInt(currentPNumber));
        }
        else
        {
            setCurrentPageNumber(1);
        }
        
        readOutParameters();
        
        
        currentPartList = getPaginatorListRetriever().retrieveList(getOffset()-1, elementsPerPage, getAdditionalFilters());
        totalNumberOfElements = getPaginatorListRetriever().getTotalNumberOfRecords();
        
        //reset current page and reload list if list is shorter than the given current page number allows
        if (getTotalNumberOfElements()<= getOffset()-1){
            setCurrentPageNumber(((getTotalNumberOfElements()-1)/getElementsPerPage())+1);
            currentPartList = getPaginatorListRetriever().retrieveList(getOffset()-1, elementsPerPage, getAdditionalFilters());
            totalNumberOfElements = getPaginatorListRetriever().getTotalNumberOfRecords();
        }
        
        paginatorPageList.clear();
        for(int i=0; i<((getTotalNumberOfElements()-1)/elementsPerPage) + 1; i++)
        {
            paginatorPageList.add(new PaginatorPage(i+1));
        }
        
        listUpdated();
        
        
    }
    
    protected abstract void saveState();
    
    protected abstract void readOutParameters();
    
    protected abstract void listUpdated();
    

    public List<ListElementType> getCurrentPartList()
    {
        
        return currentPartList;
       
    }
    
    
    public int getPartListSize()
    {
        return getCurrentPartList().size();
    }
    
    //protected abstract List<ListElementType> getPartList(int offset, int limit);
    
  
    
    public int getTotalNumberOfElements(){
        return totalNumberOfElements ;
    }
    
    public int getOffset()
    {
        return ((currentPageNumber-1)*elementsPerPage) + 1;
    }
    
   
    /*
    public abstract String getAdditionalParameterUrl();
    */
    
   
    public void setElementsPerPage(int elementsPerPage)
    {
        this.elementsPerPage = elementsPerPage;
        this.elementsPerPageTop = elementsPerPage;
        this.elementsPerPageBottom = elementsPerPage;
        getParameterMap().put(parameterElementsPerPage, String.valueOf(elementsPerPage));
    }
    

    
    
    /**
     * WARNING: USE THIS METHOD ONLY FROM UPPER PAGINATOR SELECTION MENU IN JSPF
     * @param elementsPerPageTop
     */
    public void setElementsPerPageTop(int elementsPerPageTop)
    {
        this.elementsPerPageTop = elementsPerPageTop;
    }
    
    /**
     * WARNING: USE THIS METHOD ONLY FROM LOWER PAGINATOR SELECTION MENU IN JSPF
     * @param elementsPerPageTop
     */
    public void setElementsPerPageBottom(int elementsPerPageBottom)
    {
        this.elementsPerPageBottom = elementsPerPageBottom;
        
    }

    public int getElementsPerPageTop()
    {
        return elementsPerPageTop;
    }
    
    public int getElementsPerPageBottom()
    {
        return elementsPerPageBottom;
    }
    
    public int getElementsPerPage()
    {
        return elementsPerPage;
    }
    
    
    public String changeElementsPerPageTop() throws Exception
    {
       
        setElementsPerPage(getElementsPerPageTop());
      //set new PageNumber to a number where the first element of the current Page is still displayed
        setCurrentPageNumber(((currentPageNumber-1*elementsPerPage+1)/(elementsPerPage))+1);
        redirect();
        
        return "";  
        
    }
    
    public String changeElementsPerPageBottom() throws Exception
    {
       
        setElementsPerPage(getElementsPerPageBottom());
        //set new PageNumber to a number where the first element of the current Page is still displayed
        setCurrentPageNumber(((currentPageNumber-1*elementsPerPage+1)/(elementsPerPage))+1);
        redirect();
        
        return "";  
        
    }
    
    public String goToPageTop()
    {
        
        try
        {
            int goToPage = Integer.parseInt(getGoToPageTop());
            
            if(goToPage>0 && goToPage<=getPaginatorPageSize())
            {
                setCurrentPageNumber(goToPage);
                setGoToPageBottom(String.valueOf(goToPage));
            }
            else 
            {
            error("The given page number is not valid");
            }
            
        }
        catch (Exception e)
        {
            error("The given page is not valid");
        }
        
      
        redirect();
       
          
       
        
        return "";
    }
    
    public String goToPageBottom()
    {
        
        try
        {
            int goToPage = Integer.parseInt(getGoToPageBottom());
            
            if(goToPage>0 && goToPage<=getPaginatorPageSize())
            {
                setCurrentPageNumber(goToPage);
                setGoToPageTop(String.valueOf(goToPage));
            }
            else 
            {
            error("The given page number is not valid");
            }
            
        }
        catch (Exception e)
        {
            error("The given page is not valid");
        }
        
       
            redirect();
       
        
        return "";
    }

  
    public int getCurrentPageNumber()
    {
        return currentPageNumber;
    }
    
    public void setCurrentPaginatorPage(PaginatorPage currentPaginatorPage)
    {
        this.currentPaginatorPage = currentPaginatorPage;
    }

    public PaginatorPage getCurrentPaginatorPage()
    {
        return currentPaginatorPage;
    }
    
    public List<PaginatorPage> getPaginatorPages()
    {
        return paginatorPageList;
    }
    
    public int getPaginatorPageSize()
    {
        return getPaginatorPages().size();
    }

    public int getFirstPaginatorPageNumber()
    {
        if (getPaginatorPageSize()>7 && currentPageNumber > getPaginatorPageSize()-4)
        {
            return getPaginatorPageSize() - 6;
        }
        else if (getPaginatorPageSize()>7 && currentPageNumber>4)
        {
            return currentPageNumber-3;
        }
        else 
        {
            return 1;
        }
    }

    public void setElementsPerPageSelectItems(List<SelectItem> elementsPerPageSelectItems)
    {
        this.elementsPerPageSelectItems = elementsPerPageSelectItems;
    }

    public List<SelectItem> getElementsPerPageSelectItems()
    {
        return elementsPerPageSelectItems;
    }


    public class PaginatorPage
    {
        private int number;
        
        private boolean selected;
        
        public PaginatorPage(int number)
        {
            this.number = number;
        }
        
        private String select()
        {
            return "";
        }


        public void setNumber(int number)
        {
            this.number = number;
        }


        public int getNumber()
        {
            return number;
        }


        public void setIsSelected(boolean selected)
        {
            this.selected = selected;
        }


        public boolean getIsSelected()
        {
            return selected;
        }
        
        public String getLink()
        {
            return getModifiedLink(parameterPageNumber, String.valueOf(number));
        }
    }
    
    public void redirect()
    {
        //update();
        beforeRedirect();
        try
        {
            getExternalContext().redirect(getRedirectUrl());
        }
        catch (IOException e)
        {
           error("Could not redirect!");
        }
    }

    protected abstract void beforeRedirect();
   

    private String getUrlParameterString()
    {
        
        String parameterUrl = "?";
        
        for (Entry<String, String> entrySet: redirectParameterMap.entrySet())
        {
            parameterUrl = parameterUrl + URLEncoder.encode(entrySet.getKey()) + "=" + URLEncoder.encode(entrySet.getValue()) + "&";
        }
        
        parameterUrl = parameterUrl.substring(0, parameterUrl.length()-1);
       
        return parameterUrl;
    }

    
    public String getRedirectUrl()
    {
        //ApplicationBean app = (ApplicationBean)getApplicationBean(ApplicationBean.class);
        //Sring appContext = app.getAppContext().substring(0,, endIndex)
        //return  getExternalContext().getRequestPathInfo().replaceAll("/", "") + getUrlParameterString();
        return  getPaginatorListRetriever().getListPageName() + getUrlParameterString();
    }
    
    protected Map<String, String> getParameterMap()
    {
        return redirectParameterMap;
    }
    

    protected String getModifiedLink(String key, String value)
    {
        String oldValue = redirectParameterMap.get(key);
        redirectParameterMap.put(key, value);
        String linkUrl =  getRedirectUrl();
        redirectParameterMap.put(key, oldValue);
        return linkUrl;
    }
    
    public String getLinkForNextPage()
    {
        return getModifiedLink(parameterPageNumber, String.valueOf(currentPageNumber+1));
    }
    
    public String getLinkForPreviousPage()
    {
        return getModifiedLink(parameterPageNumber, String.valueOf(currentPageNumber-1));
    }
    
    
    public String getLinkForFirstPage()
    {
        return getModifiedLink(parameterPageNumber, String.valueOf(1));
    }
    
    public String getLinkForLastPage()
    {
        return getModifiedLink(parameterPageNumber, String.valueOf(getPaginatorPageSize()));
    }

    public void setCurrentPageNumber(int currentPageNumber)
    {
        this.currentPageNumber = currentPageNumber;
        getParameterMap().put(parameterPageNumber, String.valueOf(currentPageNumber));
    }

    public void setPaginatorListRetriever(BaseListRetrieverRequestBean<ListElementType, FilterType> paginatorListRetriever)
    {
        this.paginatorListRetriever = paginatorListRetriever;
    }

    public abstract FilterType getAdditionalFilters();
   
   
    public String getPageType()
    {
        return this.pageType;
    }

    public void setPageType(String pageType)
    {
        if (!pageType.equals(this.pageType))
        {
            pageTypeChanged();
            setGoToPage("");
        }
        this.pageType = pageType;
    }

    protected abstract void pageTypeChanged();

    public void setGoToPageTop(String goToPage)
    {
        this.goToPageTop = goToPage;
    }

    public String getGoToPageTop()
    {
        return this.goToPageTop;
    }
    
    public void setGoToPageBottom(String goToPage)
    {
        this.goToPageBottom = goToPage;
    }

    public String getGoToPageBottom()
    {
        return this.goToPageBottom;
    }
    
    public void setGoToPage(String goToPage)
    {
        this.goToPage = goToPage;
        this.goToPageTop = goToPage;
        this.goToPageBottom = goToPage;
    }

    public String getGoToPage()
    {
        return goToPage;
    }
    
}
