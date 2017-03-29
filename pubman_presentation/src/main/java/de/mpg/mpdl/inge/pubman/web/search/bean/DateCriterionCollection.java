package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.DateCriterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.DateCriterion.DateType;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;

/**
 * Bean to handle the DateCriterionCollection on a single jsp. A DateCriterionCollection is
 * represented by a List<DateCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class DateCriterionCollection {
  private List<DateCriterion> parentVO;
  private DateCriterionManager dateCriterionManager;

  /**
   * CTOR to create a new ArrayList<DateCriterionVO> starting with one empty new DateCriterionVO
   */
  public DateCriterionCollection() {
    // ensure the parentVO is never null;
    final List<DateCriterion> ctorList = new ArrayList<DateCriterion>();
    ctorList.add(new DateCriterion());
    this.setParentVO(ctorList);
  }

  /**
   * CTOR to refine or fill a predefined ArrayList<DateCriterionVO>
   * 
   * @param parentVO
   */
  public DateCriterionCollection(List<DateCriterion> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<DateCriterion> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<DateCriterion> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.dateCriterionManager = new DateCriterionManager(parentVO);
  }

  /**
   * Specialized DataModelManager to deal with objects of type DateCriterionBean
   * 
   * @author Mario Wagner
   */
  public class DateCriterionManager extends DataModelManager<DateCriterionBean> {
    List<DateCriterion> parentVO;

    public DateCriterionManager(List<DateCriterion> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public DateCriterionBean createNewObject() {
      final DateCriterion newVO = new DateCriterion();
      newVO.setDateType(new ArrayList<DateType>());
      // create a new wrapper pojo
      final DateCriterionBean dateCriterionBean = new DateCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      this.parentVO.add(newVO);

      return dateCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      this.parentVO.remove(i);
    }

    public List<DateCriterionBean> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      // we have to wrap all VO's in a nice DateCriterionBean
      final List<DateCriterionBean> beanList = new ArrayList<DateCriterionBean>();
      for (final DateCriterion dateCriterionVO : this.parentVO) {
        beanList.add(new DateCriterionBean(dateCriterionVO));
      }

      return beanList;
    }

    public void setParentVO(List<DateCriterion> parentVO) {
      this.parentVO = parentVO;
      // we have to wrap all VO's into a nice DateCriterionBean
      final List<DateCriterionBean> beanList = new ArrayList<DateCriterionBean>();
      for (final DateCriterion dateCriterionVO : parentVO) {
        beanList.add(new DateCriterionBean(dateCriterionVO));
      }
      this.setObjectList(beanList);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public DateCriterionManager getDateCriterionManager() {
    return this.dateCriterionManager;
  }

  public void setDateCriterionManager(DateCriterionManager dateCriterionManager) {
    this.dateCriterionManager = dateCriterionManager;
  }

  public void clearAllForms() {
    for (final DateCriterionBean gcb : this.dateCriterionManager.getObjectList()) {
      gcb.clearCriterion();
    }
  }

  public List<DateCriterion> getFilledCriterion() {
    final List<DateCriterion> returnList = new ArrayList<DateCriterion>();
    for (final DateCriterion vo : this.parentVO) {
      if (vo.getDateType().size() > 0 || (vo.getFrom() != null && vo.getFrom().length() > 0)
          || (vo.getTo() != null && vo.getTo().length() > 0)
          || (vo.getSearchString() != null && vo.getSearchString().length() > 0)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }

}
