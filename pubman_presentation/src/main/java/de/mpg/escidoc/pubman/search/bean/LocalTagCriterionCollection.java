package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.search.bean.criterion.LocalTagCriterion;

/**
 * Bean to handle the LocalTagCriterionCollection on a single jsp.
 * A LocalTagCriterionCollection is represented by a List<LocalTagCriterionVO>.
 * 
 */
public class LocalTagCriterionCollection
{
	public static final String BEAN_NAME = "LocalTagCriterionCollection";
	
	private List<LocalTagCriterion> parentVO;
	private LocalTagCriterionManager localTagCriterionManager;
	
	/**
	 * CTOR to create a new ArrayList<EventCriterionVO> 
	 * starting with one empty new EventCriterionVO
	 */
	public LocalTagCriterionCollection()
	{
		// ensure the parentVO is never null;
		List<LocalTagCriterion> ctorList = new ArrayList<LocalTagCriterion>();
		ctorList.add(new LocalTagCriterion());
		setParentVO(ctorList);
	}

	/**
	 * CTOR to refine or fill a predefined ArrayList<EventCriterionVO>
	 * @param parentVO
	 */
	public LocalTagCriterionCollection(List<LocalTagCriterion> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<LocalTagCriterion> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<LocalTagCriterion> parentVO)
	{
		this.parentVO = parentVO;
		// ensure proper initialization of our DataModelManager
		localTagCriterionManager = new LocalTagCriterionManager(parentVO);
	}
	
	/**
	 * Specialized DataModelManager to deal with objects of type EventCriterionBean
	 * @author Mario Wagner
	 */
	public class LocalTagCriterionManager extends DataModelManager<LocalTagCriterionBean>
	{
		List<LocalTagCriterion> parentVO;
		
		public LocalTagCriterionManager(List<LocalTagCriterion> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public LocalTagCriterionBean createNewObject()
		{
			LocalTagCriterion newVO = new LocalTagCriterion();
			// create a new wrapper pojo
			LocalTagCriterionBean localTagCriterionBean = new LocalTagCriterionBean(newVO);
			// we do not have direct access to the original list
			// so we have to add the new VO on our own
			parentVO.add(newVO);
			return localTagCriterionBean;
		}
		
		@Override
		protected void removeObjectAtIndex(int i)
		{
			// due to wrapped data handling
			super.removeObjectAtIndex(i);
			parentVO.remove(i);
		}

		public List<LocalTagCriterionBean> getDataListFromVO()
		{
			if (parentVO == null) return null;
			// we have to wrap all VO's in a nice EventCriterionBean
			List<LocalTagCriterionBean> beanList = new ArrayList<LocalTagCriterionBean>();
			for (LocalTagCriterion localTagCriterionVO : parentVO)
			{
				beanList.add(new LocalTagCriterionBean(localTagCriterionVO));
			}
			return beanList;
		}

		public void setParentVO(List<LocalTagCriterion> parentVO)
		{
			this.parentVO = parentVO;
			// we have to wrap all VO's into a nice LocalTagCriterionBean
			List<LocalTagCriterionBean> beanList = new ArrayList<LocalTagCriterionBean>();
			for (LocalTagCriterion localTagCriterionVO : parentVO)
			{
				beanList.add(new LocalTagCriterionBean(localTagCriterionVO));
			}
			setObjectList(beanList);
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}


	public LocalTagCriterionManager getLocalTagCriterionManager()
	{
		return localTagCriterionManager;
	}

	public void setEventCriterionManager(LocalTagCriterionManager localTagCriterionManager)
	{
		this.localTagCriterionManager = localTagCriterionManager;
	}

    public void clearAllForms()
    {        
    	for (LocalTagCriterionBean gcb : localTagCriterionManager.getObjectList())
    	{
    		gcb.clearCriterion();
    	}
    }

    public List<LocalTagCriterion> getFilledCriterion()
	{
    	List<LocalTagCriterion> returnList = new ArrayList<LocalTagCriterion>();
    	for (LocalTagCriterion vo : parentVO)
    	{
    		if ((vo.getSearchString() != null && vo.getSearchString().length() > 0))
    		{
    			returnList.add(vo);
    		}
    	}
		return returnList;
	}

}
