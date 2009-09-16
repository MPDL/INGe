package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.search.bean.criterion.EventCriterion;

/**
 * Bean to handle the EventCriterionCollection on a single jsp.
 * A EventCriterionCollection is represented by a List<EventCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class EventCriterionCollection
{
	public static final String BEAN_NAME = "EventCriterionCollection";
	
	private List<EventCriterion> parentVO;
	private EventCriterionManager eventCriterionManager;
	
	/**
	 * CTOR to create a new ArrayList<EventCriterionVO> 
	 * starting with one empty new EventCriterionVO
	 */
	public EventCriterionCollection()
	{
		// ensure the parentVO is never null;
		List<EventCriterion> ctorList = new ArrayList<EventCriterion>();
		ctorList.add(new EventCriterion());
		setParentVO(ctorList);
	}

	/**
	 * CTOR to refine or fill a predefined ArrayList<EventCriterionVO>
	 * @param parentVO
	 */
	public EventCriterionCollection(List<EventCriterion> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<EventCriterion> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<EventCriterion> parentVO)
	{
		this.parentVO = parentVO;
		// ensure proper initialization of our DataModelManager
		eventCriterionManager = new EventCriterionManager(parentVO);
	}
	
	/**
	 * Specialized DataModelManager to deal with objects of type EventCriterionBean
	 * @author Mario Wagner
	 */
	public class EventCriterionManager extends DataModelManager<EventCriterionBean>
	{
		List<EventCriterion> parentVO;
		
		public EventCriterionManager(List<EventCriterion> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public EventCriterionBean createNewObject()
		{
			EventCriterion newVO = new EventCriterion();
			// create a new wrapper pojo
			EventCriterionBean eventCriterionBean = new EventCriterionBean(newVO);
			// we do not have direct access to the original list
			// so we have to add the new VO on our own
			parentVO.add(newVO);
			return eventCriterionBean;
		}
		
		@Override
		protected void removeObjectAtIndex(int i)
		{
			// due to wrapped data handling
			super.removeObjectAtIndex(i);
			parentVO.remove(i);
		}

		public List<EventCriterionBean> getDataListFromVO()
		{
			if (parentVO == null) return null;
			// we have to wrap all VO's in a nice EventCriterionBean
			List<EventCriterionBean> beanList = new ArrayList<EventCriterionBean>();
			for (EventCriterion eventCriterionVO : parentVO)
			{
				beanList.add(new EventCriterionBean(eventCriterionVO));
			}
			return beanList;
		}

		public void setParentVO(List<EventCriterion> parentVO)
		{
			this.parentVO = parentVO;
			// we have to wrap all VO's into a nice EventCriterionBean
			List<EventCriterionBean> beanList = new ArrayList<EventCriterionBean>();
			for (EventCriterion eventCriterionVO : parentVO)
			{
				beanList.add(new EventCriterionBean(eventCriterionVO));
			}
			setObjectList(beanList);
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}


	public EventCriterionManager getEventCriterionManager()
	{
		return eventCriterionManager;
	}

	public void setEventCriterionManager(EventCriterionManager eventCriterionManager)
	{
		this.eventCriterionManager = eventCriterionManager;
	}

    public void clearAllForms()
    {        
    	for (EventCriterionBean gcb : eventCriterionManager.getObjectList())
    	{
    		gcb.clearCriterion();
    	}
    }

    public List<EventCriterion> getFilledCriterion()
	{
    	List<EventCriterion> returnList = new ArrayList<EventCriterion>();
    	for (EventCriterion vo : parentVO)
    	{
    		if ((vo.getSearchString() != null && vo.getSearchString().length() > 0))
    		{
    			returnList.add(vo);
    		}
    	}
		return returnList;
	}

}
