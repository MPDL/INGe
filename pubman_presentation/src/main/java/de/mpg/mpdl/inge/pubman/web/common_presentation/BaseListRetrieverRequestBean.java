package de.mpg.mpdl.inge.pubman.web.common_presentation;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.annotation.PostConstruct;

/**
 * This class is an abstract class for all pages that need to implement and display a paginated
 * list. It requires a suitable BasePaginatorListSessionBean for the same element and filter type.
 * Implementations of this bean must be managed with scope "request" and have to be initialized
 * before any properties of the corresponding BasePaginatorListSessionBean are requested (e.g. by
 * calling the dummy method getBeanName() first). When using own properties in implementing classes
 * which should be passed within a GET request, please ensure to add them to the parameterMap of the
 * corresponding PaginatorListBean and then call redirect() on it.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 * @param <ListElementType> The Type of the list elements managed by this bean
 * @param <SortCriteria> Sortcriterias managed by this bean
 */
@SuppressWarnings("serial")
public abstract class BaseListRetrieverRequestBean<ListElementType, SortCriteria> extends FacesBean {
  private static final Logger logger = LogManager.getLogger(BaseListRetrieverRequestBean.class);

  private final BasePaginatorListSessionBean<ListElementType, SortCriteria> basePaginatorListSessionBean;
  private String unapiURLview;

  /**
   * This super constructor must be called by any implementation of this class. It automatically
   * sets the implementing class as retriever in the corresponding PaginatorListBean and manages the
   * update of the lists and the retrieval of GET-parameters in the right phase of the JSF
   * lifecycle.
   *
   * @param plb A corresponding PaginatorListBean with the same ListElementType and SortCriteria as
   *        used in the implementation of this class.
   * @param refreshAlways Set this flag to true if the list should be refreshed any time the page is
   *        called, not only if a get parameter has changed.
   */
  public BaseListRetrieverRequestBean(BasePaginatorListSessionBean<ListElementType, SortCriteria> plb, boolean refreshAlways) {
    try {
      this.unapiURLview = PropertyReader.getProperty(PropertyReader.INGE_UNAPI_SERVICE_URL);
    } catch (final Exception e) {
      logger.warn("Reading in unAPI server URL from properties failed.", e);
    }

    this.basePaginatorListSessionBean = plb;
    this.basePaginatorListSessionBean.setPaginatorListRetriever(this);
    this.basePaginatorListSessionBean.setPageType(this.getType());
    this.basePaginatorListSessionBean.setListPageName(this.getListPageName());
    if (refreshAlways) {
      this.basePaginatorListSessionBean.setListUpdate(true);
    }

    this.init();
  }

  @PostConstruct
  public void postConstruct() {
    if (FacesTools.getCurrentInstance().getRenderResponse()) {
      this.readOutParameters();
      this.basePaginatorListSessionBean.update();
    }
  }

  /**
   * When this method is called, the implementations of this bean must read out required parameters
   * from the get request that are needed to retrieve the list. Please use the parameterMap of the
   * corresponding PaginatorListBean to add or update GET-parameters.
   */
  public abstract void readOutParameters();

  /**
   * Within this method, implementing subclasses can initialize required objects and values or call
   * initialization methods;
   */
  public abstract void init();

  /**
   * This method must return a string that indicates the type of the page. e.g. 'DepositorWS' or
   * 'SearchResult'. It can be used in the jsf views (by calling getListType of the corresponding
   * PaginatorListBean) in order to distinguish between the same list type, filled by different
   * retrievers.
   *
   * @return A short string that describes the type of the page.
   */
  public abstract String getType();

  /**
   * Whenever this method is called, an updated list with elements of type ListelementType has to be
   * returned
   *
   * @param offset An offset from where the list must start (0 means at the beginning of all
   *        records)
   * @param limit The length of the list that has to be returned. If the whole list has less records
   *        than this paramter allows, a smaller list can be returned.
   * @param sortCriteria Additional filters that have to be included when retrieving the list.
   * @return
   */
  public abstract List<ListElementType> retrieveList(int offset, int limit, SortCriteria sortCriteria);

  /**
   * Must return the total size of the retrieved list without limit and offset parameters. E.g. for
   * a search the whole number of search records
   *
   * @return The whole number of elements in the list, regardless of limit and offset parameters
   */
  public abstract int getTotalNumberOfRecords();

  /**
   * Must return the relative name (and evtl. path) of the corresponding jsp page in order to
   * redirect to this page
   *
   * @return
   */
  public abstract String getListPageName();

  public void setUnapiURLview(String unapiURLview) {
    this.unapiURLview = unapiURLview;
  }

  public String getUnapiURLview() {
    return this.unapiURLview;
  }

  public BasePaginatorListSessionBean<ListElementType, SortCriteria> getBasePaginatorListSessionBean() {
    return this.basePaginatorListSessionBean;
  }
}
