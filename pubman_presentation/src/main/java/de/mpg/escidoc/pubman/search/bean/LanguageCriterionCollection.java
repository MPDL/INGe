/**
 * 
 */
package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.search.bean.criterion.LanguageCriterion;

/**
 * @author endres
 *
 */
public class LanguageCriterionCollection {

public static final String BEAN_NAME = "LanguageCriterionCollection";
	
	private List<LanguageCriterion> parentVO;
	private LanguageCriterionManager languageCriterionManager;
	
	// collapsed by default
	protected boolean collapsed = true;
	
	/**
	 * CTOR to create a new ArrayList<LanguageCriterionVO> 
	 * starting with one empty new LanguageCriterionVO
	 */
	public LanguageCriterionCollection()
	{
		// ensure the parentVO is never null;
		List<LanguageCriterion> ctorList = new ArrayList<LanguageCriterion>();
		ctorList.add(new LanguageCriterion());
		setParentVO(ctorList);
	}

	/**
	 * CTOR to refine or fill a predefined ArrayList<LanguageCriterionVO>
	 * @param parentVO
	 */
	public LanguageCriterionCollection(List<LanguageCriterion> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<LanguageCriterion> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<LanguageCriterion> parentVO)
	{
		this.parentVO = parentVO;
		// ensure proper initialization of our DataModelManager
		languageCriterionManager = new LanguageCriterionManager(parentVO);
	}
	
	/**
	 * Specialized DataModelManager to deal with objects of type LanguageCriterionBean
	 * @author Mario Wagner
	 */
	public class LanguageCriterionManager extends DataModelManager<LanguageCriterionBean>
	{
		List<LanguageCriterion> parentVO;
		
		public LanguageCriterionManager(List<LanguageCriterion> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public LanguageCriterionBean createNewObject()
		{
			LanguageCriterion newVO = new LanguageCriterion();
			// create a new wrapper pojo
			LanguageCriterionBean languageCriterionBean = new LanguageCriterionBean(newVO);
			// we do not have direct access to the original list
			// so we have to add the new VO on our own
			parentVO.add(newVO);
			return languageCriterionBean;
		}
		
		@Override
		protected void removeObjectAtIndex(int i)
		{
			// due to wrapped data handling
			super.removeObjectAtIndex(i);
			parentVO.remove(i);
		}

		public List<LanguageCriterionBean> getDataListFromVO()
		{
			if (parentVO == null) return null;
			// we have to wrap all VO's in a nice SourceCriterionBean
			List<LanguageCriterionBean> beanList = new ArrayList<LanguageCriterionBean>();
			for (LanguageCriterion languageCriterionVO : parentVO)
			{
				beanList.add(new LanguageCriterionBean(languageCriterionVO));
			}
			return beanList;
		}

		public void setParentVO(List<LanguageCriterion> parentVO)
		{
			this.parentVO = parentVO;
			// we have to wrap all VO's into a nice SourceCriterionBean
			List<LanguageCriterionBean> beanList = new ArrayList<LanguageCriterionBean>();
			for (LanguageCriterion languageCriterionVO : parentVO)
			{
				beanList.add(new LanguageCriterionBean(languageCriterionVO));
			}
			setObjectList(beanList);
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}


	public LanguageCriterionManager getLanguageCriterionManager()
	{
		return languageCriterionManager;
	}

	public void setLanguageCriterionManager(LanguageCriterionManager languageCriterionManager)
	{
		this.languageCriterionManager = languageCriterionManager;
	}

    public void clearAllForms()
    {        
    	for (LanguageCriterionBean gcb : languageCriterionManager.getObjectList())
    	{
    		gcb.clearCriterion();
    	}
    }

    public List<LanguageCriterion> getFilledCriterion()
	{
    	List<LanguageCriterion> returnList = new ArrayList<LanguageCriterion>();
    	for (LanguageCriterion vo : parentVO)
    	{
    		if ((vo.getSearchString() != null && vo.getSearchString().length() > 0))
    		{
    			returnList.add(vo);
    		}
    	}
		return returnList;
	}
}
