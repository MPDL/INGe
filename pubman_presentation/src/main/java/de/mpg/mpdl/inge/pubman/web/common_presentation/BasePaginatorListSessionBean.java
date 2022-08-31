package de.mpg.mpdl.inge.pubman.web.common_presentation;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;

/**
 * This abstract bean class is used to manage lists with one or two paginators. It can work together
 * with different BaseListRetrieverRequestBeans that are responsible to retrieve the list elements.
 * On a jsp page, at first the BaseListRetrieverRequestBean has to be initialized. It then
 * automatically updates the list in this bean (by calling update) whenever necessary and reads
 * required GET parameters (via readOutParamaters()). This bean has to be managed in the session
 * scope of JSF. The list only refreshes if any GET parameters have changed or new parameters have
 * been added. If you want to refresh the list anyway, please call the hasChanged method.
 * 
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 * @param <ListElementType> The Type of the list elements managed by this bean
 * @param <SortCriteria> The type of filters managed by this bean that are usable for every
 *        ListRetriever, eg. sorting of PubItems.
 */
@SuppressWarnings("serial")
public abstract class BasePaginatorListSessionBean<ListElementType, SortCriteria> extends FacesBean {
  private static final Logger logger = Logger.getLogger(BasePaginatorListSessionBean.class);

  /**
   * The GET parameter name for the elements per page value
   */
  private static String parameterElementsPerPage = "elementsPerPage";

  /**
   * The GET parameter name for the current page number
   */
  private static String parameterPageNumber = "pageNumber";


  public String getParameterElementsPerPage() {
    return BasePaginatorListSessionBean.parameterElementsPerPage;
  }

  public String getParameterPageNumber() {
    return BasePaginatorListSessionBean.parameterPageNumber;
  }

  /**
   * A list that contains the menu entries of the elements per page menu.
   */
  private List<SelectItem> elementsPerPageSelectItems;

  /**
   * A list containing the PaginatorPage objects
   */
  private final List<PaginatorPage> paginatorPageList = new ArrayList<PaginatorPage>();

  /**
   * The list containing the current elements of the displayed list
   */
  private List<ListElementType> currentPartList;


  /**
   * List containing the currently selected items
   */
  private Map<ListElementType, Boolean> currentSelections = new HashMap<>();

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
   * A map that contains the currently used GET parameters by this bean and the corresponding
   * BaseListRetrieverRequestBean.
   */
  private Map<String, String> redirectParameterMap = new HashMap<String, String>();

  /**
   * The current BaseListRetrieverRequestBean
   */
  private BaseListRetrieverRequestBean<ListElementType, SortCriteria> paginatorListRetriever;

  /**
   * The total number of elements that are in the complete list (without any limit or offset
   * filters). corresponding BaseListRetrieverRequestBean.
   */
  private int totalNumberOfElements = 0;

  /**
   * A String that describes the current type of the page. Drawn from the corresponding
   * BaseListRetrieverRequestBean.
   */
  private String pageType;

  /**
   * A String that describes the current type of the page. Drawn from the corresponding
   * BaseListRetrieverRequestBean.
   */
  private String listPageName;


  /**
   * A Map that has stored the GET parameters from the last request
   */
  private Map<String, String> oldRedirectParameterMap = new HashMap<String, String>();

  private boolean listUpdate = true;



  /**
   * Initializes a new BasePaginatorListSessionBean
   */
  public BasePaginatorListSessionBean() {
    this.elementsPerPageSelectItems = new ArrayList<SelectItem>();
    this.elementsPerPageSelectItems.add(new SelectItem("10", "10"));
    this.elementsPerPageSelectItems.add(new SelectItem("25", "25")); // --default: 25
    this.elementsPerPageSelectItems.add(new SelectItem("50", "50"));
    this.elementsPerPageSelectItems.add(new SelectItem("100", "100"));
    this.elementsPerPageSelectItems.add(new SelectItem("250", "250"));
  }

  /**
   * This method is called by the corresponding BaseListRetrieverRequestBean whenever the list has
   * to be updated. It reads out basic parameters and calls readOutParamters on implementing
   * subclasses. It uses the BaseListRetrieverRequestBean in order to retrieve the new list and
   * finally calls listUpdated on implementing subclasses.
   */
  public void update() {
    final String elementsPerP =
        FacesTools.getExternalContext().getRequestParameterMap().get(BasePaginatorListSessionBean.parameterElementsPerPage);

    if (elementsPerP != null) {
      this.setElementsPerPage(Integer.parseInt(elementsPerP));
    } else {
      this.setElementsPerPage(25);
    }

    final String currentPNumber =
        FacesTools.getExternalContext().getRequestParameterMap().get(BasePaginatorListSessionBean.parameterPageNumber);
    if (currentPNumber != null) {
      this.setCurrentPageNumber(Integer.parseInt(currentPNumber));
      this.setGoToPage(currentPNumber);
    } else {
      this.setCurrentPageNumber(1);
    }

    this.readOutParameters();

    if (this.getListUpdate() && this.getPaginatorListRetriever() != null) {
      this.currentPartList = this.getPaginatorListRetriever().retrieveList(this.getOffset(), this.elementsPerPage, this.getSortCriteria());
      this.totalNumberOfElements = this.getPaginatorListRetriever().getTotalNumberOfRecords();

      // reset current page and reload list if list is shorter than the given current page number
      // allows
      if (this.getTotalNumberOfElements() > 0 && this.getTotalNumberOfElements() <= this.getOffset()) {
        this.setCurrentPageNumber(((this.getTotalNumberOfElements() - 1) / this.getElementsPerPage()) + 1);
        this.currentPartList =
            this.getPaginatorListRetriever().retrieveList(this.getOffset(), this.elementsPerPage, this.getSortCriteria());
        this.totalNumberOfElements = this.getPaginatorListRetriever().getTotalNumberOfRecords();
      }

      this.paginatorPageList.clear();
      for (int i = 0; i < ((this.getTotalNumberOfElements() - 1) / this.elementsPerPage) + 1; i++) {
        this.paginatorPageList.add(new PaginatorPage(i + 1));
      }

      this.currentSelections.clear();
      for (ListElementType e : this.currentPartList) {
        this.currentSelections.put(e, Boolean.FALSE);
      }

      this.listUpdated();
    }

    this.saveOldParameters();
  }

  public void update(final int pageNumber, final int elementsPerP) {
    this.setElementsPerPage(elementsPerP);
    this.setCurrentPageNumber(pageNumber);

    if (this.getListUpdate()) {
      this.currentPartList = this.getPaginatorListRetriever().retrieveList(this.getOffset(), this.elementsPerPage, this.getSortCriteria());
      this.totalNumberOfElements = this.getPaginatorListRetriever().getTotalNumberOfRecords();

      // reset current page and reload list if list is shorter than the given current page number
      // allows
      if (this.getTotalNumberOfElements() > 0 && this.getTotalNumberOfElements() <= this.getOffset()) {
        this.setCurrentPageNumber(((this.getTotalNumberOfElements() - 1) / this.getElementsPerPage()) + 1);
        this.currentPartList =
            this.getPaginatorListRetriever().retrieveList(this.getOffset(), this.elementsPerPage, this.getSortCriteria());
        this.totalNumberOfElements = this.getPaginatorListRetriever().getTotalNumberOfRecords();
      }

      this.paginatorPageList.clear();
      for (int i = 0; i < ((this.getTotalNumberOfElements() - 1) / this.elementsPerPage) + 1; i++) {
        this.paginatorPageList.add(new PaginatorPage(i + 1));
      }

      this.currentSelections.clear();
      for (ListElementType e : this.currentPartList) {
        this.currentSelections.put(e, Boolean.FALSE);
      }

      this.listUpdated();
    }

    this.saveOldParameters();
  }

  /**
   * Implementing subclasses have to read out and set GET parameters within this method. If
   * parameters are set, please do not forget to add them to the parameterMap of this class.
   * Otherwise they won't be in the redirect URL.
   */
  protected abstract void readOutParameters();

  /**
   * Called whenever a new list is retrieved and set.
   */
  protected abstract void listUpdated();

  /**
   * Returns the corresponding BaseListRetrieverRequestBean.
   * 
   * @return
   */
  protected BaseListRetrieverRequestBean<ListElementType, SortCriteria> getPaginatorListRetriever() {
    return this.paginatorListRetriever;
  }

  /**
   * Returns the current list with the specified elements
   * 
   * @return
   */
  public List<ListElementType> getCurrentPartList() {
    return this.currentPartList;
  }

  /**
   * Returns the size of the current list
   * 
   * @return
   */
  public int getPartListSize() {
    return this.getCurrentPartList().size();
  }

  /**
   * Returns the total number of elements, without offset and limit filters. Drawn from
   * BaseRetrieverRequestBean
   */
  public int getTotalNumberOfElements() {
    return this.totalNumberOfElements;
  }

  /**
   * Returns the current offset (starting with 0)
   */
  public int getOffset() {
    return ((this.currentPageNumber - 1) * this.elementsPerPage);
  }

  /**
   * Sets the current value for 'element per pages'
   */
  public void setElementsPerPage(int elementsPerPage) {
    this.elementsPerPage = elementsPerPage;
    this.elementsPerPageTop = elementsPerPage;
    this.elementsPerPageBottom = elementsPerPage;
    this.getParameterMap().put(BasePaginatorListSessionBean.parameterElementsPerPage, String.valueOf(elementsPerPage));
  }

  /**
   * WARNING: USE THIS METHOD ONLY FROM UPPER PAGINATOR SELECTION MENU IN JSPF. For setting the
   * value manually, use setElementsPerPage().
   * 
   * @param elementsPerPageTop
   */
  @Deprecated
  public void setElementsPerPageTop(int elementsPerPageTop) {
    this.elementsPerPageTop = elementsPerPageTop;
  }

  /**
   * WARNING: USE THIS METHOD ONLY FROM LOWER PAGINATOR SELECTION MENU IN JSPF. For setting the
   * value manually, use setElementsPerPage().
   * 
   * @param elementsPerPageTop
   */
  @Deprecated
  public void setElementsPerPageBottom(int elementsPerPageBottom) {
    this.elementsPerPageBottom = elementsPerPageBottom;
  }

  /**
   * WARNING: USE THIS METHOD ONLY FROM UPPER PAGINATOR SELECTION MENU IN JSPF. For getting the
   * value manually, use getElementsPerPage().
   * 
   * @param elementsPerPageTop
   */
  @Deprecated
  public int getElementsPerPageTop() {
    return this.elementsPerPageTop;
  }

  /**
   * WARNING: USE THIS METHOD ONLY FROM UPPER PAGINATOR SELECTION MENU IN JSPF. For getting the
   * value manually, use getElementsPerPage().
   * 
   * @param elementsPerPageTop
   */
  @Deprecated
  public int getElementsPerPageBottom() {
    return this.elementsPerPageBottom;
  }

  /**
   * Returns the currently selected number of elements per page
   * 
   * @return
   */
  public int getElementsPerPage() {
    return this.elementsPerPage;
  }

  /**
   * Used as action when the user changes the upper number of elements menu.
   * 
   * @return
   * @throws Exception
   */
  public void changeElementsPerPageTop() throws Exception {
    this.setElementsPerPage(this.getElementsPerPageTop());
    // set new PageNumber to a number where the first element of the current Page is still displayed
    this.setCurrentPageNumber(((this.currentPageNumber - 1 * this.elementsPerPage + 1) / (this.elementsPerPage)) + 1);

    this.redirect();
  }

  /**
   * Used as action when the user changes the lower number of elements menu.
   * 
   * @return
   * @throws Exception
   */
  public void changeElementsPerPageBottom() throws Exception {
    this.setElementsPerPage(this.getElementsPerPageBottom());
    // set new PageNumber to a number where the first element of the current Page is still displayed
    this.setCurrentPageNumber(((this.currentPageNumber - 1 * this.elementsPerPage + 1) / (this.elementsPerPage)) + 1);

    this.redirect();
  }

  /**
   * Used as action when the user sends an value from the upper go to input field
   * 
   * @return
   * @throws Exception
   */
  public void doGoToPageTop() {
    try {
      final int goToPage = Integer.parseInt(this.getGoToPageTop());

      if (goToPage > 0 && goToPage <= this.getPaginatorPageSize()) {
        this.setCurrentPageNumber(goToPage);
        this.setGoToPageBottom(String.valueOf(goToPage));
      } else {
        this.error(this.getMessage("listError_goTo"));
      }
    } catch (final Exception e) {
      this.error(this.getMessage("listError_goTo"));
    }

    this.redirect();
  }

  /**
   * Used as action when the user sends an value from the lower go to input field
   * 
   * @return
   * @throws Exception
   */
  public void doGoToPageBottom() {
    try {
      final int goToPage = Integer.parseInt(this.getGoToPageBottom());
      if (goToPage > 0 && goToPage <= this.getPaginatorPageSize()) {
        this.setCurrentPageNumber(goToPage);
        this.setGoToPageTop(String.valueOf(goToPage));
      } else {
        this.error(this.getMessage("listError_goTo"));
      }
    } catch (final Exception e) {
      this.error(this.getMessage("listError_goTo"));
    }

    this.redirect();
  }

  /**
   * Returns the current page number of the paginator
   * 
   * @return
   */
  public int getCurrentPageNumber() {
    return this.currentPageNumber;
  }

  /**
   * Returns a list with the paginator pages. Used from jsf to iterate over the numbers
   * 
   * @return
   */
  public List<PaginatorPage> getPaginatorPages() {
    return this.paginatorPageList;
  }

  /**
   * Returns the number of all paginator pages, not only the visible ones
   * 
   * @return
   */
  public int getPaginatorPageSize() {
    return this.getPaginatorPages().size();
  }

  /**
   * Returns the number of the paginator page button that should be displayed as first button of the
   * paginator in order to always display exactly seven paginator page buttons
   * 
   * @return
   */
  public int getFirstPaginatorPageNumber() {
    if (this.getPaginatorPageSize() > 7 && this.currentPageNumber > this.getPaginatorPageSize() - 4) {
      return this.getPaginatorPageSize() - 6;
    } else if (this.getPaginatorPageSize() > 7 && this.currentPageNumber > 4) {
      return this.currentPageNumber - 3;
    } else {
      return 1;
    }
  }

  /**
   * Sets the menu entries of the elements per page menu
   * 
   * @param elementsPerPageSelectItems
   */
  public void setElementsPerPageSelectItems(List<SelectItem> elementsPerPageSelectItems) {
    this.elementsPerPageSelectItems = elementsPerPageSelectItems;
  }

  /**
   * Returns the menu entries of the elements per page menu
   * 
   * @return
   */
  public List<SelectItem> getElementsPerPageSelectItems() {
    return this.elementsPerPageSelectItems;
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
  public class PaginatorPage implements Serializable {
    /**
     * The page number of the paginator button
     */
    private int number;

    public PaginatorPage(int number) {
      this.number = number;
    }

    /**
     * Sets the page number of the paginator button
     * 
     * @param number
     */
    public void setNumber(int number) {
      this.number = number;
    }

    /**
     * Returns the page number of the paginator button
     * 
     * @return
     */
    public int getNumber() {
      return this.number;
    }

    /**
     * Returns the link that is used as output link of the paginator page button
     * 
     * @return
     */
    public String getLink() {
      return BasePaginatorListSessionBean.this.getModifiedLink(BasePaginatorListSessionBean.parameterPageNumber,
          String.valueOf(this.number));
    }
  }

  /**
   * Redirects to the current list page using all parameters from the parameterMap and the path to
   * the jsp page (drawn form BaseListretrieverRequestBean=
   */
  public void redirect() {
    this.beforeRedirect();
    try {
      BasePaginatorListSessionBean.logger.debug("redirectURL :" + this.getRedirectUrl());
      FacesTools.getExternalContext().redirect(this.getRedirectUrl());
    } catch (final IOException e) {
      this.error(this.getMessage("NoRedirect"));
    }
  }

  /**
   * Method is called before a redirection. Subclasses can save states here.
   */
  protected abstract void beforeRedirect();

  /**
   * Returns the GET parameters from the parameter map as string that can be appended to the URL
   * 
   * @return
   */
  private String getUrlParameterString() {

    String parameterUrl = "?";

    for (final Entry<String, String> entrySet : this.getParameterMap().entrySet()) {
      try {
        if (entrySet.getValue() != null) {
          parameterUrl =
              parameterUrl + URLEncoder.encode(entrySet.getKey(), "UTF-8") + "=" + URLEncoder.encode(entrySet.getValue(), "UTF-8") + "&";
        }
      } catch (final UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }

    parameterUrl = parameterUrl.substring(0, parameterUrl.length() - 1);

    BasePaginatorListSessionBean.logger.debug("parameterUrl: " + parameterUrl);

    return parameterUrl;
  }

  /**
   * Returns the url to redirect, including the page path (drawn from BaseListRetrieverRequestBean
   * and all get parameters from the parameterMap.
   * 
   * @return
   */
  public String getRedirectUrl() {
    return this.getPaginatorListRetriever().getListPageName() + this.getUrlParameterString();
  }

  /**
   * Returns a map that contains all HTTP GET parameters as key-value pairs. If you want to redirect
   * please add your parameters here first.
   * 
   * @return
   */
  public Map<String, String> getParameterMap() {
    return this.redirectParameterMap;
  }

  /**
   * Returns a link of which one GET parameter is modified
   * 
   * @param key The key of the parameter
   * @param value The value that should be contained in the URL
   * @return
   */
  protected String getModifiedLink(String key, String value) {
    final String oldValue = this.getParameterMap().get(key);
    this.getParameterMap().put(key, value);
    final String linkUrl = this.getRedirectUrl();
    this.getParameterMap().put(key, oldValue);
    return linkUrl;
  }

  /**
   * Returns the link for the "Next"-Button of the Paginator
   * 
   * @return
   */
  public String getLinkForNextPage() {
    return this.getModifiedLink(BasePaginatorListSessionBean.parameterPageNumber, String.valueOf(this.currentPageNumber + 1));
  }

  /**
   * Returns the link for the "Previous"-Button of the Paginator
   * 
   * @return
   */
  public String getLinkForPreviousPage() {
    return this.getModifiedLink(BasePaginatorListSessionBean.parameterPageNumber, String.valueOf(this.currentPageNumber - 1));
  }

  /**
   * Returns the link for the "First Page"-Button of the Paginator
   * 
   * @return
   */
  public String getLinkForFirstPage() {
    return this.getModifiedLink(BasePaginatorListSessionBean.parameterPageNumber, String.valueOf(1));
  }

  /**
   * Returns the link for the "Last Page"-Button of the Paginator
   * 
   * @return
   */
  public String getLinkForLastPage() {
    return this.getModifiedLink(BasePaginatorListSessionBean.parameterPageNumber, String.valueOf(this.getPaginatorPageSize()));
  }

  /**
   * Sets the current paginator page number
   * 
   * @param currentPageNumber
   */
  public void setCurrentPageNumber(int currentPageNumber) {
    this.currentPageNumber = currentPageNumber;
    this.getParameterMap().put(BasePaginatorListSessionBean.parameterPageNumber, String.valueOf(currentPageNumber));
  }

  /**
   * Sets the current BaseListRetrieverRequestBean of this SessionBean
   * 
   * @param paginatorListRetriever
   */
  public void setPaginatorListRetriever(BaseListRetrieverRequestBean<ListElementType, SortCriteria> paginatorListRetriever) {
    this.paginatorListRetriever = paginatorListRetriever;
  }

  /**
   * When calling this method, implementing subclasses can return additional filters that are
   * typical for the list elements and that should be always passed to the
   * BaseListRetrieverRequestBean when retrieving a new list, e.g. for sorting
   * 
   * @return
   */
  public abstract SortCriteria getSortCriteria();

  /**
   * Returns the pageType, a String that describes the current page with which this list is used.
   * 
   * @return
   */
  public String getPageType() {
    return this.pageType;
  }

  /**
   * Sets the pageType and, whenever a new pageType is used, calls pageTypeChanged(), clears the
   * parameter map and the input fields of go to boxes.
   * 
   * @param pageType
   */
  public void setPageType(String pageType) {
    this.pageType = pageType;
  }

  /**
   * This method is called whenever a new pageType is used. You can reset session-specific variables
   * here, e.g.
   */
  protected abstract void pageTypeChanged();

  public void setListPageName(String listPageName) {
    final String oldPageName = this.listPageName;
    this.listPageName = listPageName;

    if (!listPageName.equals(oldPageName)) {
      this.pageTypeChanged();
      this.setGoToPage("1");
      this.getParameterMap().clear();
      this.getOldRedirectParameterMap().clear();
    }
  }

  public String getListPageName() {
    return this.listPageName;
  }

  /**
   * WARNING: USE THIS METHOD ONLY FROM UPPER GO TO INPUT FIELD MENU IN JSPF. For setting the value
   * manually, use setGoToPage().
   * 
   * @param elementsPerPageTop
   */
  @Deprecated
  public void setGoToPageTop(String goToPage) {
    this.goToPageTop = goToPage;
  }

  /**
   * WARNING: USE THIS METHOD ONLY FROM UPPER GO TO INPUT FIELD MENU IN JSPF. For getting the value
   * manually, use getGoToPage().
   * 
   * @param elementsPerPageTop
   */
  @Deprecated
  public String getGoToPageTop() {
    return this.goToPageTop;
  }

  /**
   * WARNING: USE THIS METHOD ONLY FROM LOWER GO TO INPUT FIELD MENU IN JSPF. For setting the value
   * manually, use setGoToPage().
   * 
   * @param elementsPerPageTop
   */
  @Deprecated
  public void setGoToPageBottom(String goToPage) {
    this.goToPageBottom = goToPage;
  }

  /**
   * WARNING: USE THIS METHOD ONLY FROM LOWER GO TO INPUT FIELD MENU IN JSPF. For getting the value
   * manually, use getGoToPage().
   * 
   * @param elementsPerPageTop
   */
  @Deprecated
  public String getGoToPageBottom() {
    return this.goToPageBottom;
  }

  /**
   * Sets the value of the go to input fields.
   * 
   * @param goToPage
   */
  public void setGoToPage(String goToPage) {
    this.goToPage = goToPage;
    this.goToPageTop = goToPage;
    this.goToPageBottom = goToPage;
  }

  /**
   * Returns the value of the go to input fields.
   * 
   * @return
   */
  public String getGoToPage() {
    return this.goToPage;
  }

  /**
   * Copies the current parameters in the parameter store
   */
  public void saveOldParameters() {
    this.getOldRedirectParameterMap().clear();
    this.getOldRedirectParameterMap().putAll(this.getParameterMap());
  }

  /**
   * Set this method if during the next call of the retriever request bean the list should not be
   * updated;
   */
  public void setListUpdate(boolean listUpdate) {
    this.listUpdate = listUpdate;
  }

  /**
   * Returns the value of listUpdate and resets it to true.
   * 
   * @return
   */
  private boolean getListUpdate() {
    final boolean returnVal = this.listUpdate;
    this.listUpdate = true;

    return returnVal;
  }

  public void setOldRedirectParameterMap(Map<String, String> oldRedirectParameterMap) {
    this.oldRedirectParameterMap = oldRedirectParameterMap;
  }

  public Map<String, String> getOldRedirectParameterMap() {
    return this.oldRedirectParameterMap;
  }

  public void setParameterMap(Map<String, String> redirectParameterMap) {
    this.redirectParameterMap = redirectParameterMap;
  }

  public Map<ListElementType, Boolean> getCurrentSelections() {
    return currentSelections;
  }

  public void setCurrentSelections(Map<ListElementType, Boolean> currentSelections) {
    this.currentSelections = currentSelections;
  }
}
