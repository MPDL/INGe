package de.mpg.mpdl.inge.pubman.web.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.export.ExportItems;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;

/**
 * This bean is the implementation of the BaseListRetrieverRequestBean for the batch list. It uses
 * the PubItemSessionBean as cooresponding BasePaginatorListSessionBean.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "BatchItemsRetrieverRequestBean")
@SuppressWarnings("serial")
public class BatchItemsRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, SORT_CRITERIA> {
  private static final Logger logger = Logger.getLogger(BatchItemsRetrieverRequestBean.class);

  public static final String MESSAGE_NO_ITEM_FOR_DELETION_SELECTED = "deleteItemsFromBatchOrBasket_NoItemSelected";

  private int numberOfRecords;

  public BatchItemsRetrieverRequestBean() {
    // refreshAlways is needed due to workarround (latest-version problem, filter only retrieves
    // latest versions and therefore
    // number of items in the basket could change -> message is displayed to the user.
    super((PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean"), true);
  }

  @Override
  public void init() {
    // no init needed
  }

  @Override
  public int getTotalNumberOfRecords() {
    return this.numberOfRecords;
  }

  @Override
  public String getType() {
    return "CartItems";
  }

  @Override
  public void readOutParameters() {
    // no additional parameters needed
  }

  /**
   * Retrieves the list of item baskets.
   */
  @Override
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {
    List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();


    try {
      final PubItemBatchSessionBean pbsb = (PubItemBatchSessionBean) FacesTools.findBean("PubItemBatchSessionBean");


      if (pbsb.getStoredPubItems().size() > 0) {

        List<String> ids = pbsb.getStoredPubItems().values().stream().map(i -> i.getObjectId()).collect(Collectors.toList());

        BoolQueryBuilder bq = QueryBuilders.boolQuery();

        bq.must(QueryBuilders.termsQuery("objectId", ids));

        // display only latest versions
        bq.must(QueryBuilders.scriptQuery(new Script("doc['" + PubItemServiceDbImpl.INDEX_LATESTVERSION_VERSIONNUMBER + "']==doc['"
            + PubItemServiceDbImpl.INDEX_VERSION_VERSIONNUMBER + "']")));

        PubItemService pis = ApplicationBean.INSTANCE.getPubItemService();
        SearchSourceBuilder ssb = new SearchSourceBuilder();


        ssb.query(bq);
        ssb.from(offset);
        ssb.size(limit);


        for (String index : sc.getIndex()) {
          if (!index.isEmpty()) {
            ssb.sort(SearchUtils.baseElasticSearchSortBuilder(pis.getElasticSearchIndexFields(), index,
                SortOrder.ASC.equals(sc.getSortOrder()) ? org.elasticsearch.search.sort.SortOrder.ASC
                    : org.elasticsearch.search.sort.SortOrder.DESC));
          }
        }

        SearchResponse resp = pis.searchDetailed(ssb, getLoginHelper().getAuthenticationToken());

        this.numberOfRecords = (int) resp.getHits().getTotalHits();

        List<ItemVersionVO> pubItemList = SearchUtils.getRecordListFromElasticSearchResponse(resp, ItemVersionVO.class);

        returnList = CommonUtils.convertToPubItemVOPresentationList(pubItemList);

      } else {
        this.numberOfRecords = 0;
      }

      pbsb.setDiffDisplayNumber(pbsb.getBatchPubItemsSize() - this.numberOfRecords);
      if (pbsb.getDiffDisplayNumber() > 0) {

        this.error(pbsb.getDiffDisplayNumber() + " " + this.getMessage("basketAndBatch_ItemsChanged"));
      }



    } catch (final Exception e) {
      this.error(this.getMessage("ItemsRetrieveError"));
      BatchItemsRetrieverRequestBean.logger.error("Error while retrieving items for batch", e);
    }
    return returnList;
  }

  /**
   * Called from JSF when selected items in the list should be removed from the batch list.
   * 
   * @return
   */
  public void deleteSelected() {
    final PubItemBatchSessionBean pbsb = (PubItemBatchSessionBean) FacesTools.findBean("PubItemBatchSessionBean");
    int countSelected = 0;

    for (final PubItemVOPresentation pubItem : this.getBasePaginatorListSessionBean().getCurrentPartList()) {
      if (pubItem.getSelected()) {
        countSelected++;
        pbsb.getStoredPubItems().remove(pubItem.getObjectId());
      }
    }
    if (countSelected == 0) {
      this.error(this.getMessage(BatchItemsRetrieverRequestBean.MESSAGE_NO_ITEM_FOR_DELETION_SELECTED));
    }

    this.getBasePaginatorListSessionBean().redirect();
  }

  @Override
  public String getListPageName() {
    return "BatchWorkspacePage.jsp";
  }


  /**
   * Called when the export format list should be updated. Workaround. Method needs to be called
   * over this bean and not directly in the ExportItems bean, because it has to be called first in
   * order to save the selections in the list.
   */
  public void updateExportOptions() {
    ((ExportItems) FacesTools.findBean("ExportItems")).updateExportFormats();
  }
}
