package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.elasticsearch.discovery.local.LocalDiscovery;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO.State;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.pubman.impl.YearbookServiceDbImpl;

/**
 * Bean for archived Yearbook-Items
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "YearbookArchiveBean")
@ViewScoped
@SuppressWarnings("serial")
public class YearbookArchiveBean extends FacesBean {

  private final List<YearbookDbVO> archivedYearbooks;
  private YearbookDbVO selectedYearbook;
  private String yearbookId;

  public YearbookArchiveBean() throws Exception {

    String orgId = YearbookUtils.getYearbookOrganizationId(this.getLoginHelper().getAccountUser());
    QueryBuilder qb = QueryBuilders.termQuery(YearbookServiceDbImpl.INDEX_ORGANIZATION_ID, orgId);
    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb);
    SearchRetrieveResponseVO<YearbookDbVO> resp = ApplicationBean.INSTANCE.getYearbookService().search(srr,
            getLoginHelper().getAuthenticationToken());
    
    this.archivedYearbooks = resp.getRecords().stream().map(i -> i.getData()).collect(Collectors.toList());


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


  public String editMembers(YearbookDbVO yearbook) {

    YearbookItemSessionBean yisb = FacesTools.findBean("YearbookItemSessionBean");
    yisb.initYearbook(yearbook.getObjectId());
    return "loadYearbookPage";

  }
  
  public String viewMembers(YearbookDbVO yearbook) {

    YearbookItemSessionBean yisb = FacesTools.findBean("YearbookItemSessionBean");
    yisb.setYearbookForView(yearbook);
    return "loadYearbookArchiveItemViewPage";

  }

  public String editYearbookData(YearbookDbVO yearbook) {

    YearbookItemSessionBean yisb = FacesTools.findBean("YearbookItemSessionBean");
    yisb.initYearbook(yearbook.getObjectId());
    return "loadYearbookItemEditPage";

  }

  public String viewItem(YearbookDbVO yearbook) {


    for (final YearbookDbVO archivedYearbook : this.getArchivedYearbooks()) {
      if (this.getYearbookId().equals(archivedYearbook.getObjectId())) {
        this.setSelectedYearbook(archivedYearbook);
      }
    }
    return "loadYearbookArchiveItemViewPage";
  }

  public List<String> convertSetToList(Set<String> set) {
    if (set != null) {
      return new ArrayList<>(set);
    }
    return null;
  }
}
