package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.search.bean.criterion.SourceCriterion;

/**
 * Bean to handle the SourceCriterionCollection on a single jsp.
 * A SourceCriterionCollection is represented by a List<SourceCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class SourceCriterionCollection
{
	public static final String BEAN_NAME = "SourceCriterionCollection";
	
	private List<SourceCriterion> parentVO;
	private SourceCriterionManager sourceCriterionManager;
	
	// collapsed by default
	protected boolean collapsed = true;
	
	/**
	 * CTOR to create a new ArrayList<SourceCriterionVO> 
	 * starting with one empty new SourceCriterionVO
	 */
	public SourceCriterionCollection()
	{
		// ensure the parentVO is never null;
		List<SourceCriterion> ctorList = new ArrayList<SourceCriterion>();
		ctorList.add(new SourceCriterion());
		setParentVO(ctorList);
	}

	/**
	 * CTOR to refine or fill a predefined ArrayList<SourceCriterionVO>
	 * @param parentVO
	 */
	public SourceCriterionCollection(List<SourceCriterion> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<SourceCriterion> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<SourceCriterion> parentVO)
	{
		this.parentVO = parentVO;
		// ensure proper initialization of our DataModelManager
		sourceCriterionManager = new SourceCriterionManager(parentVO);
	}
	
	/**
	 * Specialized DataModelManager to deal with objects of type SourceCriterionBean
	 * @author Mario Wagner
	 */
	public class SourceCriterionManager extends DataModelManager<SourceCriterionBean>
	{
		List<SourceCriterion> parentVO;
		
		public SourceCriterionManager(List<SourceCriterion> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public SourceCriterionBean createNewObject()
		{
			SourceCriterion newVO = new SourceCriterion();
			// create a new wrapper pojo
			SourceCriterionBean sourceCriterionBean = new SourceCriterionBean(newVO);
			// we do not have direct access to the original list
			// so we have to add the new VO on our own
			parentVO.add(newVO);
			return sourceCriterionBean;
		}
		
		@Override
		protected void removeObjectAtIndex(int i)
		{
			// due to wrapped data handling
			super.removeObjectAtIndex(i);
			parentVO.remove(i);
		}

		public List<SourceCriterionBean> getDataListFromVO()
		{
			if (parentVO == null) return null;
			// we have to wrap all VO's in a nice SourceCriterionBean
			List<SourceCriterionBean> beanList = new ArrayList<SourceCriterionBean>();
			for (SourceCriterion sourceCriterionVO : parentVO)
			{
				beanList.add(new SourceCriterionBean(sourceCriterionVO));
			}
			return beanList;
		}

		public void setParentVO(List<SourceCriterion> parentVO)
		{
			this.parentVO = parentVO;
			// we have to wrap all VO's into a nice SourceCriterionBean
			List<SourceCriterionBean> beanList = new ArrayList<SourceCriterionBean>();
			for (SourceCriterion sourceCriterionVO : parentVO)
			{
				beanList.add(new SourceCriterionBean(sourceCriterionVO));
			}
			setObjectList(beanList);
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}


	public SourceCriterionManager getSourceCriterionManager()
	{
		return sourceCriterionManager;
	}

	public void setSourceCriterionManager(SourceCriterionManager sourceCriterionManager)
	{
		this.sourceCriterionManager = sourceCriterionManager;
	}

    public void clearAllForms()
    {        
    	for (SourceCriterionBean gcb : sourceCriterionManager.getObjectList())
    	{
    		gcb.clearCriterion();
    	}
    }

    public List<SourceCriterion> getFilledCriterion()
	{
    	List<SourceCriterion> returnList = new ArrayList<SourceCriterion>();
    	for (SourceCriterion vo : parentVO)
    	{
    		if ((vo.getSearchString() != null && vo.getSearchString().length() > 0))
    		{
    			returnList.add(vo);
    		}
    	}
		return returnList;
	}

}
