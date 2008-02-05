package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.services.pubman.valueobjects.OrganizationCriterionVO;

/**
 * Bean to handle the OrganizationCriterionCollection on a single jsp.
 * A OrganizationCriterionCollection is represented by a List<OrganizationCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class OrganizationCriterionCollection
{
	public static final String BEAN_NAME = "OrganizationCriterionCollection";
	
	private List<OrganizationCriterionVO> parentVO;
	private OrganizationCriterionManager organizationCriterionManager;
	
	/**
	 * CTOR to create a new ArrayList<OrganizationCriterionVO> 
	 * starting with one empty new OrganizationCriterionVO
	 */
	public OrganizationCriterionCollection()
	{
		// ensure the parentVO is never null;
		List<OrganizationCriterionVO> ctorList = new ArrayList<OrganizationCriterionVO>();
		ctorList.add(new OrganizationCriterionVO());
		setParentVO(ctorList);
	}

	/**
	 * CTOR to refine or fill a predefined ArrayList<OrganizationCriterionVO>
	 * @param parentVO
	 */
	public OrganizationCriterionCollection(List<OrganizationCriterionVO> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<OrganizationCriterionVO> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<OrganizationCriterionVO> parentVO)
	{
		this.parentVO = parentVO;
		// ensure proper initialization of our DataModelManager
		organizationCriterionManager = new OrganizationCriterionManager(parentVO);
	}
	
	/**
	 * Specialized DataModelManager to deal with objects of type OrganizationCriterionBean
	 * @author Mario Wagner
	 */
	public class OrganizationCriterionManager extends DataModelManager<OrganizationCriterionBean>
	{
		List<OrganizationCriterionVO> parentVO;
		
		public OrganizationCriterionManager(List<OrganizationCriterionVO> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public OrganizationCriterionBean createNewObject()
		{
			OrganizationCriterionVO newVO = new OrganizationCriterionVO();
			// create a new wrapper pojo
			OrganizationCriterionBean organizationCriterionBean = new OrganizationCriterionBean(newVO);
			// we do not have direct access to the original list
			// so we have to add the new VO on our own
			parentVO.add(newVO);
			return organizationCriterionBean;
		}
		
		@Override
		protected void removeObjectAtIndex(int i)
		{
			// due to wrapped data handling
			super.removeObjectAtIndex(i);
			parentVO.remove(i);
		}

		public List<OrganizationCriterionBean> getDataListFromVO()
		{
			if (parentVO == null) return null;
			// we have to wrap all VO's in a nice OrganizationCriterionBean
			List<OrganizationCriterionBean> beanList = new ArrayList<OrganizationCriterionBean>();
			for (OrganizationCriterionVO organizationCriterionVO : parentVO)
			{
				beanList.add(new OrganizationCriterionBean(organizationCriterionVO));
			}
			return beanList;
		}

		public void setParentVO(List<OrganizationCriterionVO> parentVO)
		{
			this.parentVO = parentVO;
			// we have to wrap all VO's into a nice OrganizationCriterionBean
			List<OrganizationCriterionBean> beanList = new ArrayList<OrganizationCriterionBean>();
			for (OrganizationCriterionVO organizationCriterionVO : parentVO)
			{
				beanList.add(new OrganizationCriterionBean(organizationCriterionVO));
			}
			setObjectList(beanList);
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}


	public OrganizationCriterionManager getOrganizationCriterionManager()
	{
		return organizationCriterionManager;
	}

	public void setOrganizationCriterionManager(OrganizationCriterionManager organizationCriterionManager)
	{
		this.organizationCriterionManager = organizationCriterionManager;
	}

    public void clearAllForms()
    {        
    	for (OrganizationCriterionBean gcb : organizationCriterionManager.getObjectList())
    	{
    		gcb.clearCriterion();
    	}
    }

    public List<OrganizationCriterionVO> getFilledCriterionVO()
	{
    	List<OrganizationCriterionVO> returnList = new ArrayList<OrganizationCriterionVO>();
    	for (OrganizationCriterionVO vo : parentVO)
    	{
    		if ((vo.getSearchString() != null && vo.getSearchString().length() > 0))
    		{
    			returnList.add(vo);
    		}
    	}
		return returnList;
	}

}
