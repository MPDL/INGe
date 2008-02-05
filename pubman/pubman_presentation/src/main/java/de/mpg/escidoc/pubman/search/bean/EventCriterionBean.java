package de.mpg.escidoc.pubman.search.bean;

import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.EventCriterionVO;

/**
 * POJO bean to deal with one EventCriterionVO.
 * 
 * @author Mario Wagner
 */
public class EventCriterionBean extends CriterionBean
{
	public static final String BEAN_NAME = "EventCriterionBean";
	
	private EventCriterionVO eventCriterionVO;
	
	
    public EventCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new EventCriterionVO());
	}

	public EventCriterionBean(EventCriterionVO eventCriterionVO)
	{
		setEventCriterionVO(eventCriterionVO);
	}

	@Override
	public CriterionVO getCriterionVO()
	{
		return eventCriterionVO;
	}

	public EventCriterionVO getEventCriterionVO()
	{
		return eventCriterionVO;
	}

	public void setEventCriterionVO(EventCriterionVO eventCriterionVO)
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
