package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.IdentifierCriterion;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;

/**
 * Bean to handle the IdentifierCriterionCollection on a single jsp. A IdentifierCriterionCollection
 * is represented by a List<IdentifierCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class IdentifierCriterionCollection {
  private List<IdentifierCriterion> parentVO;
  private IdentifierCriterionManager identifierCriterionManager;

  /**
   * CTOR to create a new ArrayList<IdentifierCriterionVO> starting with one empty new
   * IdentifierCriterionVO
   */
  public IdentifierCriterionCollection() {
    // ensure the parentVO is never null;
    final List<IdentifierCriterion> ctorList = new ArrayList<IdentifierCriterion>();
    ctorList.add(new IdentifierCriterion());
    this.setParentVO(ctorList);
  }

  /**
   * CTOR to refine or fill a predefined ArrayList<IdentifierCriterionVO>
   * 
   * @param parentVO
   */
  public IdentifierCriterionCollection(List<IdentifierCriterion> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<IdentifierCriterion> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<IdentifierCriterion> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.identifierCriterionManager = new IdentifierCriterionManager(parentVO);
  }

  /**
   * Specialized DataModelManager to deal with objects of type IdentifierCriterionBean
   * 
   * @author Mario Wagner
   */
  public class IdentifierCriterionManager extends DataModelManager<IdentifierCriterionBean> {
    List<IdentifierCriterion> parentVO;

    public IdentifierCriterionManager(List<IdentifierCriterion> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public IdentifierCriterionBean createNewObject() {
      final IdentifierCriterion newVO = new IdentifierCriterion();
      // create a new wrapper pojo
      final IdentifierCriterionBean identifierCriterionBean = new IdentifierCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      this.parentVO.add(newVO);
      return identifierCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      this.parentVO.remove(i);
    }

    public List<IdentifierCriterionBean> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      // we have to wrap all VO's in a nice IdentifierCriterionBean
      final List<IdentifierCriterionBean> beanList = new ArrayList<IdentifierCriterionBean>();
      for (final IdentifierCriterion identifierCriterionVO : this.parentVO) {
        beanList.add(new IdentifierCriterionBean(identifierCriterionVO));
      }

      return beanList;
    }

    public void setParentVO(List<IdentifierCriterion> parentVO) {
      this.parentVO = parentVO;
      // we have to wrap all VO's into a nice IdentifierCriterionBean
      final List<IdentifierCriterionBean> beanList = new ArrayList<IdentifierCriterionBean>();
      for (final IdentifierCriterion identifierCriterionVO : parentVO) {
        beanList.add(new IdentifierCriterionBean(identifierCriterionVO));
      }
      this.setObjectList(beanList);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public IdentifierCriterionManager getIdentifierCriterionManager() {
    return this.identifierCriterionManager;
  }

  public void setIdentifierCriterionManager(IdentifierCriterionManager identifierCriterionManager) {
    this.identifierCriterionManager = identifierCriterionManager;
  }

  public void clearAllForms() {
    for (final IdentifierCriterionBean gcb : this.identifierCriterionManager.getObjectList()) {
      gcb.clearCriterion();
    }
  }

  public List<IdentifierCriterion> getFilledCriterion() {
    final List<IdentifierCriterion> returnList = new ArrayList<IdentifierCriterion>();
    for (final IdentifierCriterion vo : this.parentVO) {
      if ((vo.getSearchString() != null && vo.getSearchString().length() > 0)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }

}
