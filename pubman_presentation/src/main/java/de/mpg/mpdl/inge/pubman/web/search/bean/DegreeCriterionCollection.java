package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.DegreeCriterion;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;

/**
 * Bean to handle the DegreeCriterionCollection on a single jsp. A DegreeCriterionCollection is
 * represented by a List<DegreeCriterionVO>.
 * 
 * @author Friederike Kleinfercher
 */
public class DegreeCriterionCollection {
  private List<DegreeCriterion> parentVO;
  private DegreeCriterionManager degreeCriterionManager;

  /**
   * CTOR to create a new ArrayList<DegreeCriterionVO> starting with one empty new DegreeCriterionVO
   */
  public DegreeCriterionCollection() {
    // ensure the parentVO is never null;
    final List<DegreeCriterion> ctorList = new ArrayList<DegreeCriterion>();
    ctorList.add(new DegreeCriterion());
    this.setParentVO(ctorList);
  }

  /**
   * CTOR to refine or fill a predefined ArrayList<DegreeCriterionVO>
   * 
   * @param parentVO
   */
  public DegreeCriterionCollection(List<DegreeCriterion> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<DegreeCriterion> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<DegreeCriterion> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.degreeCriterionManager = new DegreeCriterionManager(parentVO);
  }

  public class DegreeCriterionManager extends DataModelManager<DegreeCriterionBean> {
    List<DegreeCriterion> parentVO;

    public DegreeCriterionManager(List<DegreeCriterion> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public DegreeCriterionBean createNewObject() {
      final DegreeCriterion newVO = new DegreeCriterion();
      newVO.setDegree(new ArrayList<MdsPublicationVO.DegreeType>());
      // create a new wrapper pojo
      final DegreeCriterionBean degreeCriterionBean = new DegreeCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      this.parentVO.add(newVO);
      return degreeCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      this.parentVO.remove(i);
    }

    public List<DegreeCriterionBean> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      // we have to wrap all VO's in a nice DegreeCriterionBean
      final List<DegreeCriterionBean> beanList = new ArrayList<DegreeCriterionBean>();
      for (final DegreeCriterion degreeCriterionVO : this.parentVO) {
        beanList.add(new DegreeCriterionBean(degreeCriterionVO));
      }

      return beanList;
    }

    public void setParentVO(List<DegreeCriterion> parentVO) {
      this.parentVO = parentVO;
      // we have to wrap all VO's into a nice DegreeCriterionBean
      final List<DegreeCriterionBean> beanList = new ArrayList<DegreeCriterionBean>();
      for (final DegreeCriterion degreeCriterionVO : parentVO) {
        beanList.add(new DegreeCriterionBean(degreeCriterionVO));
      }
      this.setObjectList(beanList);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public DegreeCriterionManager getDegreeCriterionManager() {
    return this.degreeCriterionManager;
  }

  public void setDegreeCriterionManager(DegreeCriterionManager degreeCriterionManager) {
    this.degreeCriterionManager = degreeCriterionManager;
  }

  public void clearAllForms() {
    for (final DegreeCriterionBean gcb : this.degreeCriterionManager.getObjectList()) {
      gcb.clearCriterion();
    }
  }

  public List<DegreeCriterion> getFilledCriterion() {
    final List<DegreeCriterion> returnList = new ArrayList<DegreeCriterion>();
    for (final DegreeCriterion vo : this.parentVO) {
      if (vo.getDegree().size() > 0
          || (vo.getSearchString() != null && vo.getSearchString().length() > 0)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }

}
