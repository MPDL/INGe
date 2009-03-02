package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.search.bean.criterion.GenreCriterion;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;

/**
 * Bean to handle the GenreCriterionCollection on a single jsp.
 * A GenreCriterionCollection is represented by a List<GenreCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class GenreCriterionCollection
{
	public static final String BEAN_NAME = "GenreCriterionCollection";
	
	private List<GenreCriterion> parentVO;
	private GenreCriterionManager genreCriterionManager;
	
	/**
	 * CTOR to create a new ArrayList<GenreCriterionVO> 
	 * starting with one empty new GenreCriterionVO
	 */
	public GenreCriterionCollection()
	{
		// ensure the parentVO is never null;
		List<GenreCriterion> ctorList = new ArrayList<GenreCriterion>();
		ctorList.add(new GenreCriterion());
		setParentVO(ctorList);
	}

	/**
	 * CTOR to refine or fill a predefined ArrayList<GenreCriterionVO>
	 * @param parentVO
	 */
	public GenreCriterionCollection(List<GenreCriterion> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<GenreCriterion> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<GenreCriterion> parentVO)
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
		List<GenreCriterion> parentVO;
		
		public GenreCriterionManager(List<GenreCriterion> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public GenreCriterionBean createNewObject()
		{
			GenreCriterion newVO = new GenreCriterion();
			newVO.setGenre(new ArrayList<MdsPublicationVO.Genre>());
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
			for (GenreCriterion genreCriterionVO : parentVO)
			{
				beanList.add(new GenreCriterionBean(genreCriterionVO));
			}
			return beanList;
		}

		public void setParentVO(List<GenreCriterion> parentVO)
		{
			this.parentVO = parentVO;
			// we have to wrap all VO's into a nice GenreCriterionBean
			List<GenreCriterionBean> beanList = new ArrayList<GenreCriterionBean>();
			for (GenreCriterion genreCriterionVO : parentVO)
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

    public List<GenreCriterion> getFilledCriterion()
	{
    	List<GenreCriterion> returnList = new ArrayList<GenreCriterion>();
    	for (GenreCriterion vo : parentVO)
    	{
    		if (vo.getGenre().size() > 0 || (vo.getSearchString() != null && vo.getSearchString().length() > 0))
    		{
    			returnList.add(vo);
    		}
    	}
		return returnList;
	}
	
}
