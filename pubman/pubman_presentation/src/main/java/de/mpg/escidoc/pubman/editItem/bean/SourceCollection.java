package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;

/**
 * Bean to handle the SourceCollection on a single jsp.
 * A SourceCollection is represented by a List<SourceVO>.
 * 
 * @author Mario Wagner
 */
public class SourceCollection
{
	private List<SourceVO> parentVO;
	private SourceManager sourceManager;
	
	public SourceCollection()
	{
		// ensure the parentVO is never null;
		this(new ArrayList<SourceVO>());
	}

	public SourceCollection(List<SourceVO> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<SourceVO> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<SourceVO> parentVO)
	{
		this.parentVO = parentVO;
		// ensure proper initialization of our DataModelManager
		sourceManager = new SourceManager(parentVO);
	}
	
	/**
	 * Specialized DataModelManager to deal with objects of type SourceBean
	 * @author Mario Wagner
	 */
	public class SourceManager extends DataModelManager<SourceBean>
	{
		List<SourceVO> parentVO;
		
		public SourceManager(List<SourceVO> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public SourceBean createNewObject()
		{
			SourceVO newVO = new SourceVO();
			SourceBean sourceBean = new SourceBean(newVO);
			// we do not have direct access to the original list
			// so we have to add the new VO on our own
			parentVO.add(newVO);
			return sourceBean;
		}
		
		@Override
		protected void removeObjectAtIndex(int i)
		{
			// due to wrapped data handling
			super.removeObjectAtIndex(i);
			parentVO.remove(i);
		}

		public List<SourceBean> getDataListFromVO()
		{
			if (parentVO == null) return null;
			// we have to wrap all VO's in a nice SourceBean
			List<SourceBean> beanList = new ArrayList<SourceBean>();
			for (SourceVO sourceVO : parentVO)
			{
				beanList.add(new SourceBean(sourceVO));
			}
			return beanList;
		}

		public void setParentVO(List<SourceVO> parentVO)
		{
			this.parentVO = parentVO;
			// we have to wrap all VO's into a nice SourceBean
			List<SourceBean> beanList = new ArrayList<SourceBean>();
			for (SourceVO sourceVO : parentVO)
			{
				beanList.add(new SourceBean(sourceVO));
			}
			setObjectList(beanList);
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}


	public SourceManager getSourceManager()
	{
		return sourceManager;
	}

	public void setSourceManager(SourceManager sourceManager)
	{
		this.sourceManager = sourceManager;
	}

	
}
