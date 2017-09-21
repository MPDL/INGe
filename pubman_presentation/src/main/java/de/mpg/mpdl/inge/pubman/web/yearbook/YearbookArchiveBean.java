package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO.State;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.web.search.SearchRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.search.SearchService;
import de.mpg.mpdl.inge.search.query.ItemContainerSearchResult;
import de.mpg.mpdl.inge.search.query.MetadataSearchQuery;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Bean for archived Yearbook-Items
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "YearbookArchiveBean")
@SessionScoped
@SuppressWarnings("serial")
public class YearbookArchiveBean extends FacesBean {
  private static final String MAXIMUM_RECORDS = "5000";

  private final List<YearbookDbVO> archivedYearbooks;
  private YearbookDbVO selectedYearbook;
  private String yearbookId;

  public YearbookArchiveBean() throws Exception {

    String query = "SELECT y FROM YearbookDbVO y WHERE y.organization.objectId=?";
    List<Object> params = new ArrayList<>();
    params.add(YearbookUtils.getYearbookOrganizationId(this.getLoginHelper().getAccountUser()));
    this.archivedYearbooks =
        ApplicationBean.INSTANCE.getYearbookService().query(query, params,
            getLoginHelper().getAuthenticationToken());


    if (this.getArchivedYearbooks() != null && this.getArchivedYearbooks().size() < 1) {
      this.info(this.getMessage("Yearbook_noArchivedItems"));
    }
  }

  /**
   * @return the archivedYearbooks (List<PubItemVO>)
   */
  public List<YearbookDbVO> getArchivedYearbooks() {
    return this.archivedYearbooks;
  }

  /**
   * @return the yearbook-ID for item to be displayed in the detailed view
   */
  public String getYearbookId() {
    return this.yearbookId;
  }

  /**
   * @param yearbookId (String) the yearbook-ID for item to be displayed in the detailed view
   */
  public void setYearbookId(String yearbookId) {
    this.yearbookId = yearbookId;
  }

  /**
   * @return the yearbook for the detailed view
   */
  public YearbookDbVO getSelectedYearbook() {
    return this.selectedYearbook;
  }

  /**
   * @param selectedYearbook (PubItemVO) the yearbook for the detailed view
   */
  public void setSelectedYearbook(YearbookDbVO selectedYearbook) {
    this.selectedYearbook = selectedYearbook;
  }

  /**
   * @return all Members of the choosen yearbook
   */
  public List<PubItemVOPresentation> retrieveAllMembers() throws Exception {

    return YearbookUtils.retrieveAllMembers(this.getSelectedYearbook(), getLoginHelper()
        .getAuthenticationToken());
  }

  public String viewItem() {
    for (final YearbookDbVO archivedYearbook : this.getArchivedYearbooks()) {
      if (this.getYearbookId().equals(archivedYearbook.getObjectId())) {
        this.setSelectedYearbook(archivedYearbook);
      }
    }
    return "loadYearbookArchiveItemViewPage";
  }
}
