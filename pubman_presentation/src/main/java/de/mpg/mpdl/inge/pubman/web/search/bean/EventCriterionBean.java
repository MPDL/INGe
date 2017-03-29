package de.mpg.mpdl.inge.pubman.web.search.bean;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.EventCriterion;

/**
 * POJO bean to deal with one EventCriterionVO.
 * 
 * @author Mario Wagner
 */
@SuppressWarnings("serial")
public class EventCriterionBean extends CriterionBean {
  private EventCriterion eventCriterionVO;
  // collapsed by default
  protected boolean collapsed = true;

  public EventCriterionBean() {
    // ensure the parentVO is never null;
    this(new EventCriterion());
  }

  public EventCriterionBean(EventCriterion eventCriterionVO) {
    this.setEventCriterionVO(eventCriterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return this.eventCriterionVO;
  }

  public EventCriterion getEventCriterionVO() {
    return this.eventCriterionVO;
  }

  public void setEventCriterionVO(EventCriterion eventCriterionVO) {
    this.eventCriterionVO = eventCriterionVO;
  }


  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public void clearCriterion() {
    this.eventCriterionVO.setSearchString("");
    this.eventCriterionVO.setInvitationStatus(false);
  }
}
