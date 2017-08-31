package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;

import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.mpdl.inge.model.valueobjects.ItemRelationVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the Yearbook
 * workspace. It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean and
 * adds additional functionality for filtering the items by their state.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3780 $ $LastChangedDate: 2010-07-23 10:01:12 +0200 (Fri, 23 Jul 2010) $
 * 
 */
@ManagedBean(name = "YearbookArchiveRetrieverRequestBean")
@SuppressWarnings("serial")
public class YearbookArchiveRetrieverRequestBean extends
    BaseListRetrieverRequestBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA> {
  private static final Logger logger = Logger.getLogger(YearbookArchiveRetrieverRequestBean.class);

  private String selectedSortOrder;
  private static String parameterSelectedOrgUnit = "orgUnit";
  private int numberOfRecords;

  public YearbookArchiveRetrieverRequestBean() {
    super((PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean"), false);
  }

  @Override
  public void init() {
    // no init needed
  }

  @Override
  public int getTotalNumberOfRecords() {
    return this.numberOfRecords;
  }

  /**
   * Reads out the item state parameter from the HTTP GET request and sets an default value if it is
   * null.
   */
  @Override
  public void readOutParameters() {
    final String orgUnit =
        FacesTools.getExternalContext().getRequestParameterMap()
            .get(YearbookArchiveRetrieverRequestBean.parameterSelectedOrgUnit);
    if (orgUnit == null) {

      this.setSelectedOrgUnit(this.getYearbookCandidatesSessionBean().getSelectedOrgUnit());


    } else {
      this.setSelectedOrgUnit(orgUnit);
    }
  }

  @Override
  public String getType() {
    return "SearchResult";
  }

  @Override
  public String getListPageName() {
    return "YearbookArchiveItemViewPage.jsp";
  }

  public List<SelectItem> getOrgUnitSelectItems() {
    return this.getYearbookCandidatesSessionBean().getOrgUnitSelectItems();
  }

  public void setSelectedOrgUnit(String selectedOrgUnit) {
    this.getYearbookCandidatesSessionBean().setSelectedOrgUnit(selectedOrgUnit);
    this.getBasePaginatorListSessionBean().getParameterMap()
        .put(YearbookArchiveRetrieverRequestBean.parameterSelectedOrgUnit, selectedOrgUnit);
  }

  public String getSelectedOrgUnit() {
    return this.getYearbookCandidatesSessionBean().getSelectedOrgUnit();
  }

  private YearbookCandidatesSessionBean getYearbookCandidatesSessionBean() {
    return (YearbookCandidatesSessionBean) FacesTools.findBean("YearbookCandidatesSessionBean");
  }

  /**
   * Called by JSF whenever the organizational unit filter menu is changed. Causes a redirect to the
   * page with updated context GET parameter.
   * 
   * @return
   */
  public void changeOrgUnit() {
    try {
      this.getBasePaginatorListSessionBean().setCurrentPageNumber(1);
      this.getBasePaginatorListSessionBean().redirect();
    } catch (final Exception e) {
      FacesBean.error("Could not redirect");
    }
  }

  public String getSelectedSortOrder() {
    return this.selectedSortOrder;
  }

  public void setSelectedSortOrder(String selectedSortOrder) {
    this.selectedSortOrder = selectedSortOrder;
  }



  @Override
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {
    List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();
    try {
      final YearbookArchiveBean yearbookArchiveBean =
          (YearbookArchiveBean) FacesTools.findBean("YearbookArchiveBean.class");

      
      QueryBuilder qb = YearbookUtils.getMemberQuery(yearbookArchiveBean.getSelectedYearbook());
      
      SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, limit, offset+1, null);
      SearchRetrieveResponseVO<PubItemVO> resp = ApplicationBean.INSTANCE.getPubItemService()
          .search(srr, getLoginHelper().getAuthenticationToken());


      this.numberOfRecords = resp.getNumberOfRecords();
      
      List<PubItemVO> resultList = resp.getRecords().stream().map(SearchRetrieveRecordVO::getData)
          .collect(Collectors.toList());

      return CommonUtils.convertToPubItemVOPresentationList(resultList);

    } catch (final Exception e) {
      YearbookArchiveRetrieverRequestBean.logger.error("Error in retrieving items", e);
      FacesBean.error("Error in retrieving items");
      this.numberOfRecords = 0;
    }

    return returnList;
  }

  public void exportSelectedDownload() {
    final PubItemListSessionBean pilsb =
        (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");

    try {
      pilsb.downloadExportFile(pilsb.getSelectedItems());
    } catch (final Exception e) {
      FacesBean.error("Error while exporting");
      YearbookArchiveRetrieverRequestBean.logger.error("Error exporting yearbook", e);
    }
  }
}
