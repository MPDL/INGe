package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * Bean to handle the ContentLanguageCollection on a single jsp.
 * A ContentLanguageCollection is represented by a List<String>.
 * 
 * @author Mario Wagner
 */
public class ContentLanguageCollection
{
	private List<String> parentVO;
	private ContentLanguageManager contentLanguageManager;
	
	public ContentLanguageCollection()
	{
		// ensure the parentVO is never null;
		this(new ArrayList<String>());
	}

	public ContentLanguageCollection(List<String> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<String> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<String> parentVO)
	{
		this.parentVO = parentVO;
		// ensure proper initialization of our DataModelManager
		contentLanguageManager = new ContentLanguageManager(parentVO);
	}
	
	public SelectItem[] getLanguageOptions()
    {
    	return CommonUtils.getLanguageOptions();
    }

	/**
	 * Specialized DataModelManager to deal with objects of type String
	 * @author Mario Wagner
	 */
	public class ContentLanguageManager extends DataModelManager<String>
	{
		List<String> parentVO;
		
		public ContentLanguageManager(List<String> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public String createNewObject()
		{
			String newLanguage = new String();
			return newLanguage;
		}
		
		public List<String> getDataListFromVO()
		{
			if (parentVO == null) return null;
			return parentVO;
		}

		public void setParentVO(List<String> parentVO)
		{
			this.parentVO = parentVO;
			setObjectList(parentVO);
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}

	public ContentLanguageManager getContentLanguageManager()
	{
		return contentLanguageManager;
	}

	public void setContentLanguageManager(ContentLanguageManager contentLanguageManager)
	{
		this.contentLanguageManager = contentLanguageManager;
	}

}
