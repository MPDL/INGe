package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.search.bean.criterion.DateCriterion;
import de.mpg.escidoc.pubman.search.bean.criterion.DateCriterion.DateType;

/**
 * Bean to handle the DateCriterionCollection on a single jsp.
 * A DateCriterionCollection is represented by a List<DateCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class DateCriterionCollection
{
	public static final String BEAN_NAME = "DateCriterionCollection";
	
	private List<DateCriterion> parentVO;
	private DateCriterionManager dateCriterionManager;
	
	/**
	 * CTOR to create a new ArrayList<DateCriterionVO> 
	 * starting with one empty new DateCriterionVO
	 */
	public DateCriterionCollection()
	{
		// ensure the parentVO is never null;
		List<DateCriterion> ctorList = new ArrayList<DateCriterion>();
		ctorList.add(new DateCriterion());
		setParentVO(ctorList);
	}

	/**
	 * CTOR to refine or fill a predefined ArrayList<DateCriterionVO>
	 * @param parentVO
	 */
	public DateCriterionCollection(List<DateCriterion> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<DateCriterion> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<DateCriterion> parentVO)
	{
		this.parentVO = parentVO;
		// ensure proper initialization of our DataModelManager
		dateCriterionManager = new DateCriterionManager(parentVO);
	}
	
	/**
	 * Specialized DataModelManager to deal with objects of type DateCriterionBean
	 * @author Mario Wagner
	 */
	public class DateCriterionManager extends DataModelManager<DateCriterionBean>
	{
		List<DateCriterion> parentVO;
		
		public DateCriterionManager(List<DateCriterion> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public DateCriterionBean createNewObject()
		{
			DateCriterion newVO = new DateCriterion();
			newVO.setDateType(new ArrayList<DateType>());
			// create a new wrapper pojo
			DateCriterionBean dateCriterionBean = new DateCriterionBean(newVO);
			// we do not have direct access to the original list
			// so we have to add the new VO on our own
			parentVO.add(newVO);
			return dateCriterionBean;
		}
		
		@Override
		protected void removeObjectAtIndex(int i)
		{
			// due to wrapped data handling
			super.removeObjectAtIndex(i);
			parentVO.remove(i);
		}

		public List<DateCriterionBean> getDataListFromVO()
		{
			if (parentVO == null) return null;
			// we have to wrap all VO's in a nice DateCriterionBean
			List<DateCriterionBean> beanList = new ArrayList<DateCriterionBean>();
			for (DateCriterion dateCriterionVO : parentVO)
			{
				beanList.add(new DateCriterionBean(dateCriterionVO));
			}
			return beanList;
		}

		public void setParentVO(List<DateCriterion> parentVO)
		{
			this.parentVO = parentVO;
			// we have to wrap all VO's into a nice DateCriterionBean
			List<DateCriterionBean> beanList = new ArrayList<DateCriterionBean>();
			for (DateCriterion dateCriterionVO : parentVO)
			{
				beanList.add(new DateCriterionBean(dateCriterionVO));
			}
			setObjectList(beanList);
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}


	public DateCriterionManager getDateCriterionManager()
	{
		return dateCriterionManager;
	}

	public void setDateCriterionManager(DateCriterionManager dateCriterionManager)
	{
		this.dateCriterionManager = dateCriterionManager;
	}

    public void clearAllForms()
    {        
    	for (DateCriterionBean gcb : dateCriterionManager.getObjectList())
    	{
    		gcb.clearCriterion();
    	}
    }

    public List<DateCriterion> getFilledCriterion()
	{
    	List<DateCriterion> returnList = new ArrayList<DateCriterion>();
    	for (DateCriterion vo : parentVO)
    	{
    		if (vo.getDateType().size() > 0 || 
    				(vo.getFrom() != null && vo.getFrom().length() > 0) ||
    				(vo.getTo() != null && vo.getTo().length() > 0) ||
    				(vo.getSearchString() != null && vo.getSearchString().length() > 0))
    		{
    			returnList.add(vo);
    		}
    	}
		return returnList;
	}

}
