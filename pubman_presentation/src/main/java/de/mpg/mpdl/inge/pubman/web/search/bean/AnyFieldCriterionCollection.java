package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.AnyFieldCriterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;

/**
 * Bean to handle the AnyFieldCriterionCollection on a single jsp. A AnyFieldCriterionCollection is
 * represented by a List<CriterionVO> which can be AnyFieldCriterionVO, TitleCriterionVO or
 * TopicCriterionVO.
 * 
 * @author Mario Wagner
 */
public class AnyFieldCriterionCollection {
  private AnyFieldCriterionManager anyFieldCriterionManager;

  /**
   * CTOR to create a new ArrayList<CriterionVO> starting with one empty new AnyFieldCriterionVO
   */
  public AnyFieldCriterionCollection() {
    // ensure the parentVO is never null;
    final List<Criterion> ctorList = new ArrayList<Criterion>();
    ctorList.add(new AnyFieldCriterion());
    this.anyFieldCriterionManager = new AnyFieldCriterionManager();
    // setParentVO(ctorList);
  }

  // /**
  // * CTOR to refine or fill a predefined ArrayList<CriterionVO>
  // * @param parentVO
  // */
  // public AnyFieldCriterionCollection(List<CriterionVO> parentVO)
  // {
  // setParentVO(parentVO);
  // }

  // public List<CriterionVO> getParentVO()
  // {
  // return parentVO;
  // }
  //
  // public void setParentVO(List<CriterionVO> parentVO)
  // {
  // this.parentVO = parentVO;
  // // ensure proper initialization of our DataModelManager
  // anyFieldCriterionManager = new AnyFieldCriterionManager(parentVO);
  // }
  //
  public AnyFieldCriterionManager getAnyFieldCriterionManager() {
    return this.anyFieldCriterionManager;
  }

  public void setAnyFieldCriterionManager(AnyFieldCriterionManager anyFieldCriterionManager) {
    this.anyFieldCriterionManager = anyFieldCriterionManager;
  }

  public void clearAllForms() {
    for (final AnyFieldCriterionBean gcb : this.anyFieldCriterionManager.getObjectList()) {
      gcb.clearCriterion();
    }
  }

  public List<Criterion> getFilledCriterion() {
    final List<Criterion> returnList = new ArrayList<Criterion>();
    for (final AnyFieldCriterionBean bean : this.anyFieldCriterionManager.getObjectList()) {
      final Criterion vo = bean.getCriterionVO();
      if ((vo != null && vo.getSearchString() != null && vo.getSearchString().length() > 0)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }

  /**
   * Specialized DataModelManager to deal with objects of type AnyFieldCriterionBean
   * 
   * @author Mario Wagner
   */
  public class AnyFieldCriterionManager extends DataModelManager<AnyFieldCriterionBean> {
    // List<CriterionVO> parentVO;

    // public AnyFieldCriterionManager(List<CriterionVO> parentVO)
    // {
    // setParentVO(parentVO);
    // }

    public AnyFieldCriterionManager() {
      if (this.getSize() == 0) {
        final List<AnyFieldCriterionBean> beanList = new ArrayList<AnyFieldCriterionBean>();
        beanList.add(this.createNewObject());
        this.setObjectList(beanList);
      }
    }

    @Override
    public AnyFieldCriterionBean createNewObject() {
      final AnyFieldCriterion newVO = new AnyFieldCriterion();
      // create a new wrapper pojo
      final AnyFieldCriterionBean anyFieldCriterionBean = new AnyFieldCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      // parentVO.add(newVO);
      return anyFieldCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      // parentVO.remove(i);
    }

    public List<AnyFieldCriterionBean> getDataListFromVO() {
      return this.objectList;
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }
}
