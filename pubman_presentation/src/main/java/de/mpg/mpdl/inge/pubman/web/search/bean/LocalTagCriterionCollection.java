package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.LocalTagCriterion;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;

/**
 * Bean to handle the LocalTagCriterionCollection on a single jsp. A LocalTagCriterionCollection is
 * represented by a List<LocalTagCriterionVO>.
 * 
 */
public class LocalTagCriterionCollection {
  private List<LocalTagCriterion> parentVO;
  private LocalTagCriterionManager localTagCriterionManager;

  /**
   * CTOR to create a new ArrayList<LocalCriterionVO> starting with one empty new
   * LocalTagCriterionVO
   */
  public LocalTagCriterionCollection() {
    // ensure the parentVO is never null;
    final List<LocalTagCriterion> ctorList = new ArrayList<LocalTagCriterion>();
    ctorList.add(new LocalTagCriterion());
    this.setParentVO(ctorList);
  }

  /**
   * CTOR to refine or fill a predefined ArrayList<LocalTagCriterionVO>
   * 
   * @param parentVO
   */
  public LocalTagCriterionCollection(List<LocalTagCriterion> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<LocalTagCriterion> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<LocalTagCriterion> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.localTagCriterionManager = new LocalTagCriterionManager(parentVO);
  }

  /**
   * Specialized DataModelManager to deal with objects of type LocalTagCriterionBean
   * 
   * @author Thomas Endres
   */
  public class LocalTagCriterionManager extends DataModelManager<LocalTagCriterionBean> {
    List<LocalTagCriterion> parentVO;

    public LocalTagCriterionManager(List<LocalTagCriterion> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public LocalTagCriterionBean createNewObject() {
      final LocalTagCriterion newVO = new LocalTagCriterion();
      // create a new wrapper pojo
      final LocalTagCriterionBean localTagCriterionBean = new LocalTagCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      this.parentVO.add(newVO);
      return localTagCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      this.parentVO.remove(i);
    }

    public List<LocalTagCriterionBean> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      // we have to wrap all VO's in a nice LocalTagCriterionBean
      final List<LocalTagCriterionBean> beanList = new ArrayList<LocalTagCriterionBean>();
      for (final LocalTagCriterion localTagCriterionVO : this.parentVO) {
        beanList.add(new LocalTagCriterionBean(localTagCriterionVO));
      }

      return beanList;
    }

    public void setParentVO(List<LocalTagCriterion> parentVO) {
      this.parentVO = parentVO;
      // we have to wrap all VO's into a nice LocalTagCriterionBean
      final List<LocalTagCriterionBean> beanList = new ArrayList<LocalTagCriterionBean>();
      for (final LocalTagCriterion localTagCriterionVO : parentVO) {
        beanList.add(new LocalTagCriterionBean(localTagCriterionVO));
      }
      this.setObjectList(beanList);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public LocalTagCriterionManager getLocalTagCriterionManager() {
    return this.localTagCriterionManager;
  }

  public void setLocalTagCriterionManager(LocalTagCriterionManager localTagCriterionManager) {
    this.localTagCriterionManager = localTagCriterionManager;
  }

  public void clearAllForms() {
    for (final LocalTagCriterionBean gcb : this.localTagCriterionManager.getObjectList()) {
      gcb.clearCriterion();
    }
  }

  public List<LocalTagCriterion> getFilledCriterion() {
    final List<LocalTagCriterion> returnList = new ArrayList<LocalTagCriterion>();
    for (final LocalTagCriterion vo : this.parentVO) {
      if ((vo.getSearchString() != null && vo.getSearchString().length() > 0)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }

}
