package de.mpg.escidoc.pubman.search.bean;

import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.pubman.search.bean.criterion.EventCriterion;

/**
 * POJO bean to deal with one EventCriterionVO.
 * 
 * @author Mario Wagner
 */
public class EventCriterionBean extends CriterionBean
{
	public static final String BEAN_NAME = "EventCriterionBean";
	
	private EventCriterion eventCriterionVO;
	
	// collapsed by default
	protected boolean collapsed = true;
	
    public EventCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new EventCriterion());
	}

	public EventCriterionBean(EventCriterion eventCriterionVO)
	{
		setEventCriterionVO(eventCriterionVO);
	}

	@Override
	public Criterion getCriterionVO()
	{
		return eventCriterionVO;
	}

	public EventCriterion getEventCriterionVO()
	{
		return eventCriterionVO;
	}

	public void setEventCriterionVO(EventCriterion eventCriterionVO)
	{
		this.eventCriterionVO = eventCriterionVO;
	}
	
	
	/**
	 * Action navigation call to clear the current part of the form
	 * @return null
	 */
	public String clearCriterion()
	{
		eventCriterionVO.setSearchString("");
		
		// navigation refresh
		return null;
	}

}
