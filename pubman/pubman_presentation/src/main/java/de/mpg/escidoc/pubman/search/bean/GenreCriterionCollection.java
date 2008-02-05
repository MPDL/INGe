package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.pubman.valueobjects.GenreCriterionVO;

/**
 * Bean to handle the GenreCriterionCollection on a single jsp.
 * A GenreCriterionCollection is represented by a List<GenreCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class GenreCriterionCollection
{
	public static final String BEAN_NAME = "GenreCriterionCollection";
	
	private List<GenreCriterionVO> parentVO;
	private GenreCriterionManager genreCriterionManager;
	
	/**
	 * CTOR to create a new ArrayList<GenreCriterionVO> 
	 * starting with one empty new GenreCriterionVO
	 */
	public GenreCriterionCollection()
	{
		// ensure the parentVO is never null;
		List<GenreCriterionVO> ctorList = new ArrayList<GenreCriterionVO>();
		ctorList.add(new GenreCriterionVO());
		setParentVO(ctorList);
	}

	/**
	 * CTOR to refine or fill a predefined ArrayList<GenreCriterionVO>
	 * @param parentVO
	 */
	public GenreCriterionCollection(List<GenreCriterionVO> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<GenreCriterionVO> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<GenreCriterionVO> parentVO)
	{
		this.parentVO = parentVO;
		// ensure proper initialization of our DataModelManager
		genreCriterionManager = new GenreCriterionManager(parentVO);
	}
	
	/**
	 * Specialized DataModelManager to deal with objects of type GenreCriterionBean
	 * @author Mario Wagner
	 */
	public class GenreCriterionManager extends DataModelManager<GenreCriterionBean>
	{
		List<GenreCriterionVO> parentVO;
		
		public GenreCriterionManager(List<GenreCriterionVO> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public GenreCriterionBean createNewObject()
		{
			GenreCriterionVO newVO = new GenreCriterionVO();
			newVO.setGenre(new ArrayList<Genre>());
			// create a new wrapper pojo
			GenreCriterionBean genreCriterionBean = new GenreCriterionBean(newVO);
			// we do not have direct access to the original list
			// so we have to add the new VO on our own
			parentVO.add(newVO);
			return genreCriterionBean;
		}
		
		@Override
		protected void removeObjectAtIndex(int i)
		{
			// due to wrapped data handling
			super.removeObjectAtIndex(i);
			parentVO.remove(i);
		}

		public List<GenreCriterionBean> getDataListFromVO()
		{
			if (parentVO == null) return null;
			// we have to wrap all VO's in a nice GenreCriterionBean
			List<GenreCriterionBean> beanList = new ArrayList<GenreCriterionBean>();
			for (GenreCriterionVO genreCriterionVO : parentVO)
			{
				beanList.add(new GenreCriterionBean(genreCriterionVO));
			}
			return beanList;
		}

		public void setParentVO(List<GenreCriterionVO> parentVO)
		{
			this.parentVO = parentVO;
			// we have to wrap all VO's into a nice GenreCriterionBean
			List<GenreCriterionBean> beanList = new ArrayList<GenreCriterionBean>();
			for (GenreCriterionVO genreCriterionVO : parentVO)
			{
				beanList.add(new GenreCriterionBean(genreCriterionVO));
			}
			setObjectList(beanList);
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}


	public GenreCriterionManager getGenreCriterionManager()
	{
		return genreCriterionManager;
	}

	public void setGenreCriterionManager(GenreCriterionManager genreCriterionManager)
	{
		this.genreCriterionManager = genreCriterionManager;
	}

    public void clearAllForms()
    {        
    	for (GenreCriterionBean gcb : genreCriterionManager.getObjectList())
    	{
    		gcb.clearCriterion();
    	}
    }

    public List<GenreCriterionVO> getFilledCriterionVO()
	{
    	List<GenreCriterionVO> returnList = new ArrayList<GenreCriterionVO>();
    	for (GenreCriterionVO vo : parentVO)
    	{
    		if (vo.getGenre().size() > 0 || (vo.getSearchString() != null && vo.getSearchString().length() > 0))
    		{
    			returnList.add(vo);
    		}
    	}
		return returnList;
	}
	
}
