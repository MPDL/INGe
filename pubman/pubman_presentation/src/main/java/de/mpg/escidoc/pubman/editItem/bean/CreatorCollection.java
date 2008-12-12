package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Bean to handle the CreatorCollection on a single jsp.
 * A CreatorCollection is represented by a List<CreatorVO>.
 * 
 * @author Mario Wagner
 */
public class CreatorCollection
{
	private static Logger logger = Logger.getLogger(CreatorCollection.class);
	
	private List<CreatorVO> parentVO;
	private CreatorManager creatorManager;
	
	public CreatorCollection()
	{
		// ensure the parentVO is never null;
		this(new ArrayList<CreatorVO>());
	}

	public CreatorCollection(List<CreatorVO> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<CreatorVO> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<CreatorVO> parentVO)
	{
		this.parentVO = parentVO;
		// ensure proper initialization of our DataModelManager
		creatorManager = new CreatorManager(parentVO);
	}
	
	/**
	 * Specialized DataModelManager to deal with objects of type CreatorBean
	 * @author Mario Wagner
	 */
	public class CreatorManager extends DataModelManager<CreatorBean>
	{
		List<CreatorVO> parentVO;
		
		public CreatorManager(List<CreatorVO> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public CreatorBean createNewObject()
		{
		    int i = objectDM.getRowIndex();
			CreatorVO newVO = new CreatorVO();
			newVO.setPerson(new PersonVO());
            // create a new Organization for this person
            OrganizationVO newPersonOrganization = new OrganizationVO();

            newPersonOrganization.setName(new TextVO());
            newVO.getPerson().getOrganizations().add(newPersonOrganization);

			CreatorBean creatorBean = new CreatorBean(newVO);
			// we do not have direct access to the original list
			// so we have to add the new VO on our own
			parentVO.add(i+1,newVO);
			
			return creatorBean;
		}
		
		@Override
		protected void removeObjectAtIndex(int i)
		{
			// due to wrapped data handling
			super.removeObjectAtIndex(i);
			parentVO.remove(i);
		}

		
		//public List<CreatorBean> getDataListFromVO()
		//{
		    /*
			if (parentVO == null) return null;
			// we have to wrap all VO's in a nice CreatorBean
			List<CreatorBean> beanList = new ArrayList<CreatorBean>();
			for (CreatorVO creatorVO : parentVO)
			{
				beanList.add(new CreatorBean(creatorVO));
			}
			return beanList;
			*/
		//}

		
		public void setParentVO(List<CreatorVO> parentVO)
		{
			this.parentVO = parentVO;
			// we have to wrap all VO's into a nice CreatorBean
			List<CreatorBean> beanList = new ArrayList<CreatorBean>();
			for (CreatorVO creatorVO : parentVO)
			{
				beanList.add(new CreatorBean(creatorVO));
			}
			setObjectList(beanList);
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}


	public CreatorManager getCreatorManager()
	{
		return creatorManager;
	}

	public void setCreatorManager(CreatorManager creatorManager)
	{
		this.creatorManager = creatorManager;
	}

	
}
