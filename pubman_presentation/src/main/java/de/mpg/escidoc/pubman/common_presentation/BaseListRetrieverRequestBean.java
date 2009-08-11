package de.mpg.escidoc.pubman.common_presentation;

import java.util.List;

import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;

/**
 * This class is an abstract class for all pages that need to implement and display a paginated list.
 * It requires a suitable BasePaginatorListSessionBean for the same element and filter type.
 * Implementations of this bean must be managed with scope "request" and have to be initialized before any properties of the
 * corresponding BasePaginatorListSessionBean are requested (e.g. by calling the dummy method getBeanName() first).
 * When using own properties in implementing classes which should be passed within a GET request, please ensure to add them to
 * the parameterMap of the corresponding PaginatorListBean and then call redirect() on it.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 * @param <ListElementType> The Type of the list elements managed by this bean
 * @param <FilterType> The type of filters managed by this bean
 */
public abstract class BaseListRetrieverRequestBean<ListElementType, FilterType> extends BreadcrumbPage
{
    private BasePaginatorListSessionBean<ListElementType, FilterType> basePaginatorListSessionBean;
    
    /**
     * This super constructor must be called by any implementation of this class. It automatically sets the implementing class as retriever in
     * the corresponding PaginatorListBean and manages the update of the lists and the retrieval of GET-parameters in the right phase of the
     * JSF lifecycle.
     * @param plb A corresponding PaginatorListBean with the same ListElementType and FilterType as used in the implementation of this class.
     * @param refreshAlways Set this flag to true if the list should be refreshed any time the page is called, not only if a get parameter has changed.
     */
    public BaseListRetrieverRequestBean (BasePaginatorListSessionBean<ListElementType, FilterType> plb, boolean refreshAlways)
    {
        super.init();
        this.setBasePaginatorListSessionBean(plb);
        getBasePaginatorListSessionBean().setPaginatorListRetriever(this);
        getBasePaginatorListSessionBean().setPageType(getType());
        if (refreshAlways)
        {
            getBasePaginatorListSessionBean().setHasChanged();
        }
        init();

        if (getFacesContext().getRenderResponse()){
            readOutParameters();
            getBasePaginatorListSessionBean().update();
        }
        /*
        else
        {
            getBasePaginatorListSessionBean().saveState();
        }
        */
    }
    
    /**
     * When this method is called, the implementations of this bean must read out required parameters from the get request that are needed to retrieve the list.
     * Please use the parameterMap of the corresponding PaginatorListBean to add or update GET-parameters.
     */
    public abstract void readOutParameters();
    
    /**
     * Within this method, implementing subclasses can initialize required objects and values or call initialization methods;
     */
    public abstract void init();
    
    /**
     * This method must return a string that indicates the type of the page. e.g. 'DepositorWS' or 'SearchResult'. It can be used
     * in the jsf views (by calling getListType of the corresponding PaginatorListBean) in order to distinguish between the same list type, filled by different retrievers.
     * 
     * @return A short string that describes the type of the page.
     */
    public abstract String getType();
    
    /**
     * Whenever this method is called, an updated list with elements of type ListelementType has to be returned 
     * @param offset An offset from where the list must start (0 means at the beginning of all records)
     * @param limit The length of the list that has to be returned. If the whole list has less records than this paramter allows, a smaller list can be returned.
     * @param additionalFilters Additional filters that have to be included when retrieving the list.
     * @return
     */
    public abstract List<ListElementType> retrieveList(int offset, int limit, FilterType additionalFilters);
    
    /**
     * Must return the total size of the retrieved list without limit and offset parameters. E.g. for a search the whole number of search records
     * @return The whole number of elements in the list, regardless of limit and offset parameters
     */
    public abstract int getTotalNumberOfRecords();
    
    /**
     * Must return the relative name (and evtl. path) of the corresponding jsp page in order to redirect to this page
     * @return
     */
    public abstract String getListPageName();

    /**
     * Sets the corresponding BasePaginatorListSessionBean
     * @return basePaginatorListSessionBean
     */
    public void setBasePaginatorListSessionBean(BasePaginatorListSessionBean<ListElementType, FilterType> basePaginatorListSessionBean)
    {
        this.basePaginatorListSessionBean = basePaginatorListSessionBean;
    }

    /**
     * Returns the corresponding BasePaginatorListSessionBean
     * @return basePaginatorListSessionBean
     */
    public BasePaginatorListSessionBean<ListElementType, FilterType> getBasePaginatorListSessionBean()
    {
        return basePaginatorListSessionBean;
    }
    
   
    
    
    
    
   
   
    
    
}
