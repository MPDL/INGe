package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.web.search.SearchRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
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

  private final List<PubItemVO> archivedYearbooks;
  private PubItemVO selectedYearbook;
  private String yearbookId;

  public YearbookArchiveBean() throws Exception {
    final ItemHandler itemHandler =
        ServiceLocator.getItemHandler(this.getLoginHelper().getESciDocUserHandle());
    // this.activeYearbookItem = this.yearbookItemSessionBean.getYearbookItem();
    this.archivedYearbooks = new ArrayList<PubItemVO>();
    final HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
    final String orgId =
        this.getLoginHelper().getAccountUsersAffiliations().get(0).getReference().getObjectId();
    filterParams.put("operation", new String[] {"searchRetrieve"});
    filterParams.put("version", new String[] {"1.1"});
    filterParams.put(
        "query",
        new String[] {"\"/properties/context/id\"="
            + PropertyReader.getProperty("escidoc.pubman.yearbook.context.id")
            + " and \"/md-records/md-record/yearbook/creator/organization/identifier\"=" + orgId});
    filterParams.put("maximumRecords", new String[] {YearbookArchiveBean.MAXIMUM_RECORDS});
    final String xmlItemList = itemHandler.retrieveItems(filterParams);
    final SearchRetrieveResponseVO<PubItemVO> result =
        XmlTransformingService.transformToSearchRetrieveResponse(xmlItemList);
    // check if years have to be excluded from selection
    if (result.getNumberOfRecords() > 0) {
      PubItemVO recordPubItem = null;
      for (final SearchRetrieveRecordVO<PubItemVO> yearbookRecord : result.getRecords()) {
        recordPubItem = (PubItemVO) yearbookRecord.getData();
        if (recordPubItem != null && recordPubItem.getYearbookMetadata() != null) {
          if (State.RELEASED.equals(recordPubItem.getVersion().getState())) {
            this.archivedYearbooks.add(recordPubItem);
          }
        }
      }
    }
    if (this.getArchivedYearbooks() != null && this.getArchivedYearbooks().size() < 1) {
      this.info(this.getMessage("Yearbook_noArchivedItems"));
    }
  }

  /**
   * @return the archivedYearbooks (List<PubItemVO>)
   */
  public List<PubItemVO> getArchivedYearbooks() {
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
  public PubItemVO getSelectedYearbook() {
    return this.selectedYearbook;
  }

  /**
   * @param selectedYearbook (PubItemVO) the yearbook for the detailed view
   */
  public void setSelectedYearbook(PubItemVO selectedYearbook) {
    this.selectedYearbook = selectedYearbook;
  }

  /**
   * @return all Members of the choosen yearbook
   */
  public List<PubItemVOPresentation> retrieveAllMembers() throws Exception {
    List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    final MetadataSearchQuery mdQuery =
        YearbookCandidatesRetrieverRequestBean.getMemberQuery(this.getSelectedYearbook());
    final ItemContainerSearchResult result = SearchService.searchForItemContainer(mdQuery);
    pubItemList = SearchRetrieverRequestBean.extractItemsOfSearchResult(result);
    return pubItemList;
  }

  public String viewItem() {
    for (final PubItemVO archivedYearbook : this.getArchivedYearbooks()) {
      if (this.getYearbookId().equals(archivedYearbook.getVersion().getObjectId())) {
        this.setSelectedYearbook(archivedYearbook);
      }
    }
    return "loadYearbookArchiveItemViewPage";
  }
}
