package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Bean to handle the ContentAbstractCollection on a single jsp.
 * A ContentAbstractCollection is represented by a List<TextVO>.
 * 
 * @author Mario Wagner
 */
public class ContentAbstractCollection
{
	private List<TextVO> parentVO;
	private ContentAbstractManager contentAbstractManager;
	
	public ContentAbstractCollection()
	{
		// ensure the parentVO is never null;
		this(new ArrayList<TextVO>());
	}

	public ContentAbstractCollection(List<TextVO> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<TextVO> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<TextVO> parentVO)
	{
		this.parentVO = parentVO;
		// ensure proper initialization of our DataModelManager
		contentAbstractManager = new ContentAbstractManager(parentVO);
	}
	
	public SelectItem[] getLanguageOptions()
    {
    	return CommonUtils.getLanguageOptions();
    }

	/**
	 * Specialized DataModelManager to deal with objects of type TextVO
	 * @author Mario Wagner
	 */
	public class ContentAbstractManager extends DataModelManager<TextVO>
	{
		List<TextVO> parentVO;
		
		public ContentAbstractManager(List<TextVO> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public TextVO createNewObject()
		{
			TextVO newTitle = new TextVO();
			return newTitle;
		}
		
		public List<TextVO> getDataListFromVO()
		{
			if (parentVO == null) return null;
			return parentVO;
		}

		public void setParentVO(List<TextVO> parentVO)
		{
			this.parentVO = parentVO;
			setObjectList(parentVO);
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}


	public ContentAbstractManager getContentAbstractManager()
	{
		return contentAbstractManager;
	}

	public void setContentAbstractManager(ContentAbstractManager contentAbstractManager)
	{
		this.contentAbstractManager = contentAbstractManager;
	}

	
}
