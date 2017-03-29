package de.mpg.mpdl.inge.pubman.web.revisions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;

import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.RelationVOPresentation;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the Item Revisions
 * list. It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "RevisionsRetrieverRequestBean")
@SuppressWarnings("serial")
public class RevisionsRetrieverRequestBean extends
    BaseListRetrieverRequestBean<PubItemVOPresentation, SORT_CRITERIA> {

  private int numberOfRecords;

  public static final String LOAD_REVISION_LIST = "loadRevisionList";

  public RevisionsRetrieverRequestBean() {
    super((RevisionItemListSessionBean) FacesTools.findBean("RevisionItemListSessionBean"), true);
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
    return "RevisionList";
  }

  @Override
  public void readOutParameters() {
    // No parameters needed
  }

  /**
   * Retrieves the revisions and ignores limit and offset and sorting because there is no paginator
   * and no sorting mechanism for this list
   */
  @Override
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {

    // limit and offset is ignored because no paginator is used
    final List<PubItemVO> pubItemVOList = new ArrayList<PubItemVO>();


    try {
      final ItemControllerSessionBean icsb =
          (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
      // get Revisions
      final List<RelationVOPresentation> relationVOList =
          icsb.retrieveRevisions(icsb.getCurrentPubItem());

      for (final RelationVOPresentation relationVO : relationVOList) {
        final PubItemVO sourceItem = relationVO.getSourceItem();

        if (sourceItem != null
            && sourceItem.getVersion().getState().toString().equals(State.RELEASED.toString())) {
          pubItemVOList.add(sourceItem);
        }

      }

      // get ParentItems

      final List<RelationVOPresentation> relationVOList2 =
          icsb.retrieveParentsForRevision(icsb.getCurrentPubItem());

      for (final RelationVOPresentation relationVO : relationVOList2) {
        final PubItemVO targetItem = relationVO.getTargetItem();
        if (targetItem != null
            && targetItem.getVersion().getState().toString().equals(State.RELEASED.toString())) {
          pubItemVOList.add(targetItem);
        }

      }
    } catch (final Exception e) {
      FacesBean.error("Error with retrieving revisions");
    }

    this.numberOfRecords = pubItemVOList.size();
    return CommonUtils.convertToPubItemVOPresentationList(pubItemVOList);
  }

  @Override
  public String getListPageName() {
    return "ViewItemRevisionsPage.jsp";
  }

}
