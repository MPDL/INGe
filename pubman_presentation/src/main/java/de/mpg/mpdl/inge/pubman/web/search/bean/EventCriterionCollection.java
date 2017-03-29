package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.EventCriterion;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;

/**
 * Bean to handle the EventCriterionCollection on a single jsp. A EventCriterionCollection is
 * represented by a List<EventCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class EventCriterionCollection {
  private List<EventCriterion> parentVO;
  private EventCriterionManager eventCriterionManager;

  /**
   * CTOR to create a new ArrayList<EventCriterionVO> starting with one empty new EventCriterionVO
   */
  public EventCriterionCollection() {
    // ensure the parentVO is never null;
    final List<EventCriterion> ctorList = new ArrayList<EventCriterion>();
    ctorList.add(new EventCriterion());
    this.setParentVO(ctorList);
  }

  /**
   * CTOR to refine or fill a predefined ArrayList<EventCriterionVO>
   * 
   * @param parentVO
   */
  public EventCriterionCollection(List<EventCriterion> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<EventCriterion> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<EventCriterion> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.eventCriterionManager = new EventCriterionManager(parentVO);
  }

  /**
   * Specialized DataModelManager to deal with objects of type EventCriterionBean
   * 
   * @author Mario Wagner
   */
  public class EventCriterionManager extends DataModelManager<EventCriterionBean> {
    List<EventCriterion> parentVO;

    public EventCriterionManager(List<EventCriterion> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public EventCriterionBean createNewObject() {
      final EventCriterion newVO = new EventCriterion();
      // create a new wrapper pojo
      final EventCriterionBean eventCriterionBean = new EventCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      this.parentVO.add(newVO);
      return eventCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      this.parentVO.remove(i);
    }

    public List<EventCriterionBean> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      // we have to wrap all VO's in a nice EventCriterionBean
      final List<EventCriterionBean> beanList = new ArrayList<EventCriterionBean>();
      for (final EventCriterion eventCriterionVO : this.parentVO) {
        beanList.add(new EventCriterionBean(eventCriterionVO));
      }
      return beanList;
    }

    public void setParentVO(List<EventCriterion> parentVO) {
      this.parentVO = parentVO;
      // we have to wrap all VO's into a nice EventCriterionBean
      final List<EventCriterionBean> beanList = new ArrayList<EventCriterionBean>();
      for (final EventCriterion eventCriterionVO : parentVO) {
        beanList.add(new EventCriterionBean(eventCriterionVO));
      }
      this.setObjectList(beanList);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public EventCriterionManager getEventCriterionManager() {
    return this.eventCriterionManager;
  }

  public void setEventCriterionManager(EventCriterionManager eventCriterionManager) {
    this.eventCriterionManager = eventCriterionManager;
  }

  public void clearAllForms() {
    for (final EventCriterionBean gcb : this.eventCriterionManager.getObjectList()) {
      gcb.clearCriterion();
    }
  }

  public List<EventCriterion> getFilledCriterion() {
    final List<EventCriterion> returnList = new ArrayList<EventCriterion>();
    for (final EventCriterion vo : this.parentVO) {
      if ((vo.getSearchString() != null && vo.getSearchString().length() > 0)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }

}
