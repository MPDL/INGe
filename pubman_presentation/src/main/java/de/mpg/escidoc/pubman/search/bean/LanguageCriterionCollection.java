/**
 * 
 */
package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;
import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.services.pubman.valueobjects.LanguageCriterionVO;

/**
 * @author endres
 *
 */
public class LanguageCriterionCollection {

public static final String BEAN_NAME = "LanguageCriterionCollection";
	
	private List<LanguageCriterionVO> parentVO;
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
		List<LanguageCriterionVO> ctorList = new ArrayList<LanguageCriterionVO>();
		ctorList.add(new LanguageCriterionVO());
		setParentVO(ctorList);
	}

	/**
	 * CTOR to refine or fill a predefined ArrayList<LanguageCriterionVO>
	 * @param parentVO
	 */
	public LanguageCriterionCollection(List<LanguageCriterionVO> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<LanguageCriterionVO> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<LanguageCriterionVO> parentVO)
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
		List<LanguageCriterionVO> parentVO;
		
		public LanguageCriterionManager(List<LanguageCriterionVO> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public LanguageCriterionBean createNewObject()
		{
			LanguageCriterionVO newVO = new LanguageCriterionVO();
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
			for (LanguageCriterionVO languageCriterionVO : parentVO)
			{
				beanList.add(new LanguageCriterionBean(languageCriterionVO));
			}
			return beanList;
		}

		public void setParentVO(List<LanguageCriterionVO> parentVO)
		{
			this.parentVO = parentVO;
			// we have to wrap all VO's into a nice SourceCriterionBean
			List<LanguageCriterionBean> beanList = new ArrayList<LanguageCriterionBean>();
			for (LanguageCriterionVO languageCriterionVO : parentVO)
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

    public List<LanguageCriterionVO> getFilledCriterionVO()
	{
    	List<LanguageCriterionVO> returnList = new ArrayList<LanguageCriterionVO>();
    	for (LanguageCriterionVO vo : parentVO)
    	{
    		if ((vo.getSearchString() != null && vo.getSearchString().length() > 0))
    		{
    			returnList.add(vo);
    		}
    	}
		return returnList;
	}
}
