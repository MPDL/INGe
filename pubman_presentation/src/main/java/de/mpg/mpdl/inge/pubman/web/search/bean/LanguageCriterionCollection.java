/**
 * 
 */
package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.LanguageCriterion;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;

/**
 * @author endres
 * 
 */
public class LanguageCriterionCollection {
  private List<LanguageCriterion> parentVO;
  private LanguageCriterionManager languageCriterionManager;

  // collapsed by default
  protected boolean collapsed = true;

  /**
   * CTOR to create a new ArrayList<LanguageCriterionVO> starting with one empty new
   * LanguageCriterionVO
   */
  public LanguageCriterionCollection() {
    // ensure the parentVO is never null;
    final List<LanguageCriterion> ctorList = new ArrayList<LanguageCriterion>();
    ctorList.add(new LanguageCriterion());
    this.setParentVO(ctorList);
  }

  /**
   * CTOR to refine or fill a predefined ArrayList<LanguageCriterionVO>
   * 
   * @param parentVO
   */
  public LanguageCriterionCollection(List<LanguageCriterion> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<LanguageCriterion> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<LanguageCriterion> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.languageCriterionManager = new LanguageCriterionManager(parentVO);
  }

  /**
   * Specialized DataModelManager to deal with objects of type LanguageCriterionBean
   * 
   * @author Mario Wagner
   */
  public class LanguageCriterionManager extends DataModelManager<LanguageCriterionBean> {
    List<LanguageCriterion> parentVO;

    public LanguageCriterionManager(List<LanguageCriterion> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public LanguageCriterionBean createNewObject() {
      final LanguageCriterion newVO = new LanguageCriterion();
      // create a new wrapper pojo
      final LanguageCriterionBean languageCriterionBean = new LanguageCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      this.parentVO.add(newVO);
      return languageCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      this.parentVO.remove(i);
    }

    public List<LanguageCriterionBean> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      // we have to wrap all VO's in a nice SourceCriterionBean
      final List<LanguageCriterionBean> beanList = new ArrayList<LanguageCriterionBean>();
      for (final LanguageCriterion languageCriterionVO : this.parentVO) {
        beanList.add(new LanguageCriterionBean(languageCriterionVO));
      }

      return beanList;
    }

    public void setParentVO(List<LanguageCriterion> parentVO) {
      this.parentVO = parentVO;
      // we have to wrap all VO's into a nice SourceCriterionBean
      final List<LanguageCriterionBean> beanList = new ArrayList<LanguageCriterionBean>();
      for (final LanguageCriterion languageCriterionVO : parentVO) {
        beanList.add(new LanguageCriterionBean(languageCriterionVO));
      }
      this.setObjectList(beanList);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public LanguageCriterionManager getLanguageCriterionManager() {
    return this.languageCriterionManager;
  }

  public void setLanguageCriterionManager(LanguageCriterionManager languageCriterionManager) {
    this.languageCriterionManager = languageCriterionManager;
  }

  public void clearAllForms() {
    for (final LanguageCriterionBean gcb : this.languageCriterionManager.getObjectList()) {
      gcb.clearCriterion();
    }
  }

  public List<LanguageCriterion> getFilledCriterion() {
    final List<LanguageCriterion> returnList = new ArrayList<LanguageCriterion>();
    for (final LanguageCriterion vo : this.parentVO) {
      if ((vo.getSearchString() != null && vo.getSearchString().length() > 0)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }
}
