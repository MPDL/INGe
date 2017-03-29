package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.SourceCriterion;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;

/**
 * Bean to handle the SourceCriterionCollection on a single jsp. A SourceCriterionCollection is
 * represented by a List<SourceCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class SourceCriterionCollection {
  private List<SourceCriterion> parentVO;
  private SourceCriterionManager sourceCriterionManager;

  // collapsed by default
  protected boolean collapsed = true;

  /**
   * CTOR to create a new ArrayList<SourceCriterionVO> starting with one empty new SourceCriterionVO
   */
  public SourceCriterionCollection() {
    // ensure the parentVO is never null;
    final List<SourceCriterion> ctorList = new ArrayList<SourceCriterion>();
    ctorList.add(new SourceCriterion());
    this.setParentVO(ctorList);
  }

  /**
   * CTOR to refine or fill a predefined ArrayList<SourceCriterionVO>
   * 
   * @param parentVO
   */
  public SourceCriterionCollection(List<SourceCriterion> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<SourceCriterion> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<SourceCriterion> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.sourceCriterionManager = new SourceCriterionManager(parentVO);
  }

  /**
   * Specialized DataModelManager to deal with objects of type SourceCriterionBean
   * 
   * @author Mario Wagner
   */
  public class SourceCriterionManager extends DataModelManager<SourceCriterionBean> {
    List<SourceCriterion> parentVO;

    public SourceCriterionManager(List<SourceCriterion> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public SourceCriterionBean createNewObject() {
      final SourceCriterion newVO = new SourceCriterion();
      // create a new wrapper pojo
      final SourceCriterionBean sourceCriterionBean = new SourceCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      this.parentVO.add(newVO);
      return sourceCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      this.parentVO.remove(i);
    }

    public List<SourceCriterionBean> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      // we have to wrap all VO's in a nice SourceCriterionBean
      final List<SourceCriterionBean> beanList = new ArrayList<SourceCriterionBean>();
      for (final SourceCriterion sourceCriterionVO : this.parentVO) {
        beanList.add(new SourceCriterionBean(sourceCriterionVO));
      }

      return beanList;
    }

    public void setParentVO(List<SourceCriterion> parentVO) {
      this.parentVO = parentVO;
      // we have to wrap all VO's into a nice SourceCriterionBean
      final List<SourceCriterionBean> beanList = new ArrayList<SourceCriterionBean>();
      for (final SourceCriterion sourceCriterionVO : parentVO) {
        beanList.add(new SourceCriterionBean(sourceCriterionVO));
      }
      this.setObjectList(beanList);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public SourceCriterionManager getSourceCriterionManager() {
    return this.sourceCriterionManager;
  }

  public void setSourceCriterionManager(SourceCriterionManager sourceCriterionManager) {
    this.sourceCriterionManager = sourceCriterionManager;
  }

  public void clearAllForms() {
    for (final SourceCriterionBean gcb : this.sourceCriterionManager.getObjectList()) {
      gcb.clearCriterion();
    }
  }

  public List<SourceCriterion> getFilledCriterion() {
    final List<SourceCriterion> returnList = new ArrayList<SourceCriterion>();
    for (final SourceCriterion vo : this.parentVO) {
      if ((vo.getSearchString() != null && vo.getSearchString().length() > 0)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }

}
