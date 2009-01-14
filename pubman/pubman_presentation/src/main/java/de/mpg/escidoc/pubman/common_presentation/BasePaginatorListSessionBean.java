package de.mpg.escidoc.pubman.common_presentation;


import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.FacesBean;

/**
 * This abstract bean class is used to manage lists with one or two paginators. It can work together with different BaseListRetrieverRequestBeans that are responsible
 * to retrieve the list elements. On a jsp page, at first the BaseListRetrieverRequestBean has to be initialized. It then automatically updates the list in this bean (by calling update) whenever necessary and reads
 * required GET parameters (via readOutParamaters()). This bean has to be managed in the session scope of JSF.
 * 
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 * @param <ListElementType> The Type of the list elements managed by this bean
 * @param <FilterType> The type of filters managed by this bean that are usable for every ListRetriever, eg. sorting of PubItems.
 */
public abstract class BasePaginatorListSessionBean<ListElementType, FilterType> extends FacesBean
{
    
    /**
     * The GET parameter name for the elements per page value
     */
    private static String parameterElementsPerPage = "elementsPerPage";
    
    /**
     * The GET parameter name for the current page number
     */
    private static String parameterPageNumber = "pageNumber";
    
    
    public String getParameterElementsPerPage()
    {
        return parameterElementsPerPage;
    }

    public String getParameterPageNumber()
    {
        return parameterPageNumber;
    }

    /**
     * A list that contains the menu entries of the elements per page menu.
     */
    private List<SelectItem> elementsPerPageSelectItems;
   
    /**
     * A list containing the PaginatorPage objects
     */
    private List<PaginatorPage> paginatorPageList;
    
    /**
     * The list containing the current elements of the displayed list
     */
    private List<ListElementType> currentPartList;
    
    /**
     * The current number of elements per page 
     */
    private int elementsPerPage;
    
    /**
     * Bound to the selected value of the lower elementsPerPage selection menu 
     */
    private int elementsPerPageBottom;
    
    /**
     * Bound to the selected value of the upper elementsPerPage selection menu 
     */
    private int elementsPerPageTop;
    
    /**
     * The current paginator page number
     */
    private int currentPageNumber;
    
    
    /**
     * The current value of the 'go to' input fields
     */
    private String goToPage;
    
    /**
     * This attribute is bound to the 'go to' input field of the lower paginator
     */
    private String goToPageBottom;
    
    /**
     * This attribute is bound to the 'go to' input field of the upper paginator
     */
    private String goToPageTop;
    
    /**
     * A map that contains the currently used GET parameters by this bean and the corresponding BaseListRetrieverRequestBean.
     */
    private Map<String, String> redirectParameterMap;
    
    /**
     * The current BaseListRetrieverRequestBean
     */
    private BaseListRetrieverRequestBean<ListElementType, FilterType> paginatorListRetriever;
    
    /**
     * The total number of elements that are in the complete list (without any limit or offset filters). corresponding BaseListRetrieverRequestBean.
     */
    private int totalNumberOfElements = 0;
    
    /**
     * A String that describes the current type of the page. Drawn from the corresponding BaseListRetrieverRequestBean.
     */
    private String pageType;
   
    /**
     * A Map that has stored the GET parameters from the last request
     */
    private Map<String, String> oldRedirectParameterMap;

    /**
     * Indicates if the list should be upadated even if no parameters have changed
     */
    private boolean hasChanged;
    
    /**
     * Initializes a new BasePaginatorListSessionBean
     */
    public BasePaginatorListSessionBean()
    {
        redirectParameterMap = new HashMap<String, String>();
        oldRedirectParameterMap = new HashMap<String, String>();
        
        
        elementsPerPageSelectItems = new ArrayList<SelectItem>();
        elementsPerPageSelectItems.add(new SelectItem("10","10"));
        elementsPerPageSelectItems.add(new SelectItem("25","25"));
        elementsPerPageSelectItems.add(new SelectItem("50","50"));
        elementsPerPageSelectItems.add(new SelectItem("100","100"));
        elementsPerPageSelectItems.add(new SelectItem("250","250"));
        
        paginatorPageList = new ArrayList<PaginatorPage>();

       
    }
    
    
    /**
     * This method is called by the corresponding BaseListRetrieverRequestBean whenever the list has to be updated. It reads out basic parameters
     * and calls readOutParamters on implementing subclasses. It uses the BaseListRetrieverRequestBean in order to retrieve the new list and
     * finally calls listUpdated on implementing subclasses.
     */
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
        
        
        if (parametersChanged() || getHasChanged())
        {
            
            currentPartList = getPaginatorListRetriever().retrieveList(getOffset(), elementsPerPage, getAdditionalFilters());
            totalNumberOfElements = getPaginatorListRetriever().getTotalNumberOfRecords();
            
            //reset current page and reload list if list is shorter than the given current page number allows
            if (getTotalNumberOfElements()<= getOffset()){
                setCurrentPageNumber(((getTotalNumberOfElements()-1)/getElementsPerPage())+1);
                currentPartList = getPaginatorListRetriever().retrieveList(getOffset(), elementsPerPage, getAdditionalFilters());
                totalNumberOfElements = getPaginatorListRetriever().getTotalNumberOfRecords();
            }
            
            paginatorPageList.clear();
            for(int i=0; i<((getTotalNumberOfElements()-1)/elementsPerPage) + 1; i++)
            {
                paginatorPageList.add(new PaginatorPage(i+1));
            }
            
            listUpdated();
        }
       
        saveOldParameters();
        
    }
    
    /**
     * Compares the parameters from the current request with the ones from the last request.
     * Returns true if parameters have changed or if there are more/less parameters since the last request.
     * @return
     */
    private boolean parametersChanged()
    {
        if (oldRedirectParameterMap.isEmpty() || oldRedirectParameterMap.size()!=getParameterMap().size()) 
        {
            return true;
        }
        else
        {
            for(String key : oldRedirectParameterMap.keySet())
            {
                if (!redirectParameterMap.containsKey(key) || !redirectParameterMap.get(key).equals(oldRedirectParameterMap.get(key)))
                {
                    return true;
                }
            }
            return false;
        }
       
    }
    

    /**
     * Implementing subclasses have to read out and set GET parameters within this method. If parameters are set, please do not
     * forget to add them to the parameterMap of this class. Otherwise they won't be in the redirect URL.
     */
    protected abstract void readOutParameters();
    
    
    /**
     * Called whenever a new list is retrieved and set. 
     */
    protected abstract void listUpdated();
    

    
    /**
     * Returns the corresponding BaseListRetrieverRequestBean.
     * @return
     */
    protected BaseListRetrieverRequestBean<ListElementType, FilterType> getPaginatorListRetriever()
    {
        return paginatorListRetriever;
    }
    
    /**
     * Returns the current list with the specified elements
     * @return
     */
    public List<ListElementType> getCurrentPartList()
    {
        
        return currentPartList;
       
    }
    
    /**
     * Returns the size of the current list
     * @return
     */
    public int getPartListSize()
    {
        return getCurrentPartList().size();
    }
    
    //protected abstract List<ListElementType> getPartList(int offset, int limit);
    
  
    /**
     * Returns the total number of elements, without offset and limit filters. Drawn from BaseRetrieverRequestBean
     */
    public int getTotalNumberOfElements(){
        return totalNumberOfElements ;
    }
    
    /**
     * Returns the current offset (starting with 0)
     */
    public int getOffset()
    {
        return ((currentPageNumber-1)*elementsPerPage);
    }
    
   
    /*
    public abstract String getAdditionalParameterUrl();
    */
    
   
    /**
     * Sets the current value for 'element per pages'
     */
    public void setElementsPerPage(int elementsPerPage)
    {
        this.elementsPerPage = elementsPerPage;
        this.elementsPerPageTop = elementsPerPage;
        this.elementsPerPageBottom = elementsPerPage;
        getParameterMap().put(parameterElementsPerPage, String.valueOf(elementsPerPage));
    }
    

    
    
    /**
     * WARNING: USE THIS METHOD ONLY FROM UPPER PAGINATOR SELECTION MENU IN JSPF. For setting the value manually, use setElementsPerPage().
     * @param elementsPerPageTop
     */
    @Deprecated
    public void setElementsPerPageTop(int elementsPerPageTop)
    {
        this.elementsPerPageTop = elementsPerPageTop;
    }
    
    /**
     * WARNING: USE THIS METHOD ONLY FROM LOWER PAGINATOR SELECTION MENU IN JSPF. For setting the value manually, use setElementsPerPage().
     * @param elementsPerPageTop
     */
    @Deprecated
    public void setElementsPerPageBottom(int elementsPerPageBottom)
    {
        this.elementsPerPageBottom = elementsPerPageBottom;
        
    }

    /**
     * WARNING: USE THIS METHOD ONLY FROM UPPER PAGINATOR SELECTION MENU IN JSPF. For getting the value manually, use getElementsPerPage().
     * @param elementsPerPageTop
     */
    @Deprecated
    public int getElementsPerPageTop()
    {
        return elementsPerPageTop;
    }
    
    /**
     * WARNING: USE THIS METHOD ONLY FROM UPPER PAGINATOR SELECTION MENU IN JSPF. For getting the value manually, use getElementsPerPage().
     * @param elementsPerPageTop
     */
    @Deprecated
    public int getElementsPerPageBottom()
    {
        return elementsPerPageBottom;
    }
    
    /**
     * Returns the currently selected number of elements per page
     * @return
     */
    public int getElementsPerPage()
    {
        return elementsPerPage;
    }
    
    
    /**
     * Used as action when the user changes the upper number of elements menu.
     * @return
     * @throws Exception
     */
    public String changeElementsPerPageTop() throws Exception
    {
       
        setElementsPerPage(getElementsPerPageTop());
      //set new PageNumber to a number where the first element of the current Page is still displayed
        setCurrentPageNumber(((currentPageNumber-1*elementsPerPage+1)/(elementsPerPage))+1);
        redirect();
        
        return "";  
        
    }
    
    /**
     * Used as action when the user changes the lower number of elements menu.
     * @return
     * @throws Exception
     */
    public String changeElementsPerPageBottom() throws Exception
    {
       
        setElementsPerPage(getElementsPerPageBottom());
        //set new PageNumber to a number where the first element of the current Page is still displayed
        setCurrentPageNumber(((currentPageNumber-1*elementsPerPage+1)/(elementsPerPage))+1);
        redirect();
        
        return "";  
        
    }
    
    /**
     * Used as action when the user sends an value from the upper go to input field
     * @return
     * @throws Exception
     */
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
    
    /**
     * Used as action when the user sends an value from the lower go to input field
     * @return
     * @throws Exception
     */
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

  
    /**
     * Returns the current page number of the paginator
     * @return
     */
    public int getCurrentPageNumber()
    {
        return currentPageNumber;
    }
    
    
    /**
     * Returns a list with the paginator pages. Used from jsf to iterate over the numbers
     * @return
     */
    public List<PaginatorPage> getPaginatorPages()
    {
        return paginatorPageList;
    }
    
    
    /**
     * Returns the number of all paginator pages, not only the visible ones
     * @return
     */
    public int getPaginatorPageSize()
    {
        return getPaginatorPages().size();
    }

    /**
     * Returns the number of the paginator page button that should be displayed as first button of the paginator in order to always display
     * exactly seven paginator page buttons
     * @return
     */
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

    /**
     * Sets the menu entries of the elements per page menu
     * @param elementsPerPageSelectItems
     */
    public void setElementsPerPageSelectItems(List<SelectItem> elementsPerPageSelectItems)
    {
        this.elementsPerPageSelectItems = elementsPerPageSelectItems;
    }

    /**
     * Returns the menu entries of the elements per page menu
     * @return
     */
    public List<SelectItem> getElementsPerPageSelectItems()
    {
        return elementsPerPageSelectItems;
    }


    /**
     * 
     * Inner class pf which an instance represents an paginator button. Used by the iterator in jsf.
     *
     * @author Markus Haarlaender (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public class PaginatorPage
    {
        /**
         * The page number of the paginator button
         */
        private int number;
        
       
        
        public PaginatorPage(int number)
        {
            this.number = number;
        }
        

        /**
         * Sets the page number of the paginator button
         * @param number
         */
        public void setNumber(int number)
        {
            this.number = number;
        }


        /**
         * Returns the page number of the paginator button
         * @return
         */
        public int getNumber()
        {
            return number;
        }


        /**
         * Returns the link that is used as output link of the paginator page button
         * @return
         */
        public String getLink()
        {
            return getModifiedLink(parameterPageNumber, String.valueOf(number));
        }
    }
    
    /**
     * Redirects to the current list page using all parameters from the parameterMap and the path to the jsp page (drawn form BaseListretrieverRequestBean=
     */
    public void redirect()
    {
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

    
    /**
     * Method is called before a redirection. Subclasses can save states here.
     */
    protected abstract void beforeRedirect();
   

   
    /**
     * Returns the GET parameters from the parameter map as string that can be appended to the URL
     * @return
     */
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

    /**
     * Returns the url to redirect, including the page path (drawn from BaseListRetrieverRequestBean and all get parameters from the parameterMap.
     * @return
     */
    public String getRedirectUrl()
    {
        //ApplicationBean app = (ApplicationBean)getApplicationBean(ApplicationBean.class);
        //Sring appContext = app.getAppContext().substring(0,, endIndex)
        //return  getExternalContext().getRequestPathInfo().replaceAll("/", "") + getUrlParameterString();
        return  getPaginatorListRetriever().getListPageName() + getUrlParameterString();
    }
    
    /**
     * Returns a map that contains all HTTP GET parameters as key-value pairs. If you want to redirect please add your parameters here first.
     * @return
     */
    public Map<String, String> getParameterMap()
    {
        return redirectParameterMap;
    }
    

    /**
     * Returns a link of which one GET parameter is modified
     * @param key The key of the parameter
     * @param value The value that should be contained in the URL
     * @return
     */
    protected String getModifiedLink(String key, String value)
    {
        String oldValue = redirectParameterMap.get(key);
        redirectParameterMap.put(key, value);
        String linkUrl =  getRedirectUrl();
        redirectParameterMap.put(key, oldValue);
        return linkUrl;
    }
    
    /**
     * Returns the link for the "Next"-Button of the Paginator
     * @return
     */
    public String getLinkForNextPage()
    {
        return getModifiedLink(parameterPageNumber, String.valueOf(currentPageNumber+1));
    }
    
    /**
     * Returns the link for the "Previous"-Button of the Paginator
     * @return
     */
    public String getLinkForPreviousPage()
    {
        return getModifiedLink(parameterPageNumber, String.valueOf(currentPageNumber-1));
    }
    
    /**
     * Returns the link for the "First Page"-Button of the Paginator
     * @return
     */
    public String getLinkForFirstPage()
    {
        return getModifiedLink(parameterPageNumber, String.valueOf(1));
    }
    
    /**
     * Returns the link for the "Last Page"-Button of the Paginator
     * @return
     */
    public String getLinkForLastPage()
    {
        return getModifiedLink(parameterPageNumber, String.valueOf(getPaginatorPageSize()));
    }

    
    /**
     * Sets the current paginator page number
     * @param currentPageNumber
     */
    public void setCurrentPageNumber(int currentPageNumber)
    {
        this.currentPageNumber = currentPageNumber;
        getParameterMap().put(parameterPageNumber, String.valueOf(currentPageNumber));
    }

    /**
     * Sets the current BaseListRetrieverRequestBean of this SessionBean
     * @param paginatorListRetriever
     */
    public void setPaginatorListRetriever(BaseListRetrieverRequestBean<ListElementType, FilterType> paginatorListRetriever)
    {
        this.paginatorListRetriever = paginatorListRetriever;
    }

    /**
     * When calling this method, implementing subclasses can return additional filters that are typical for the list elements and that should be
     * always passed to the BaseListRetrieverRequestBean when retrieving a new list, e.g. for sorting
     * @return
     */
    public abstract FilterType getAdditionalFilters();
   
   
    /**
     * Returns the pageType, a String that describes the current page with which this list is used.
     * @return
     */
    public String getPageType()
    {
        return this.pageType;
    }

    /**
     * Sets the pageType and, whenever a new pageType is used, calls pageTypeChanged(), clears the parameter map and the input fields of go to boxes.
     * @param pageType
     */
    public void setPageType(String pageType)
    {
        String oldPageType = this.pageType;
        this.pageType = pageType;
        
        if (!pageType.equals(oldPageType))
        {
            pageTypeChanged();
            setGoToPage("");
            getParameterMap().clear();
            oldRedirectParameterMap.clear();
        }
        
    }

    /**
     * This method is called whenever a new pageType is used. You can reset session-specific variables here, e.g.
     */
    protected abstract void pageTypeChanged();

    
    
    /**
     * WARNING: USE THIS METHOD ONLY FROM UPPER GO TO INPUT FIELD MENU IN JSPF. For setting the value manually, use setGoToPage().
     * @param elementsPerPageTop
     */
    @Deprecated
    public void setGoToPageTop(String goToPage)
    {
        this.goToPageTop = goToPage;
    }

    /**
     * WARNING: USE THIS METHOD ONLY FROM UPPER GO TO INPUT FIELD MENU IN JSPF. For getting the value manually, use getGoToPage().
     * @param elementsPerPageTop
     */
    @Deprecated
    public String getGoToPageTop()
    {
        return this.goToPageTop;
    }
    
    /**
     * WARNING: USE THIS METHOD ONLY FROM LOWER GO TO INPUT FIELD MENU IN JSPF. For setting the value manually, use setGoToPage().
     * @param elementsPerPageTop
     */
    @Deprecated
    public void setGoToPageBottom(String goToPage)
    {
        this.goToPageBottom = goToPage;
    }

    /**
     * WARNING: USE THIS METHOD ONLY FROM LOWER GO TO INPUT FIELD MENU IN JSPF. For getting the value manually, use getGoToPage().
     * @param elementsPerPageTop
     */
    @Deprecated
    public String getGoToPageBottom()
    {
        return this.goToPageBottom;
    }
    
    /**
     * Sets the value of the go to input fields.
     * @param goToPage
     */
    public void setGoToPage(String goToPage)
    {
        this.goToPage = goToPage;
        this.goToPageTop = goToPage;
        this.goToPageBottom = goToPage;
    }

    /**
     * Returns the value of the go to input fields.
     * @return
     */
    public String getGoToPage()
    {
        return goToPage;
    }

    /**
     * Copies the current parameters in the parameter store
     */
    public void saveOldParameters()
    {
        oldRedirectParameterMap.clear();
        oldRedirectParameterMap.putAll(redirectParameterMap);
    }
    
    /**
     * Set this method from outside if the list has to be updated even if no GET parameters have changed;
     */
    public void setHasChanged()
    {
        hasChanged=true;
    }
    
    /**
     * Returns the value of hasChanged and resets it to false.
     * @return
     */
    private boolean getHasChanged()
    {
        boolean returnVal = hasChanged;
        hasChanged=false;
        return returnVal;
    }
    
}
