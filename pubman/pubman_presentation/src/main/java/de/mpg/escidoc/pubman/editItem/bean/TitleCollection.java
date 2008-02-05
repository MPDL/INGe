package de.mpg.escidoc.pubman.editItem.bean;

import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.TitleIF;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Bean to deal with one title and many alternative titles on a single jsp.
 * 
 * @author Mario Wagner
 */
public class TitleCollection
{
	private TitleIF titleIF;
	private AlternativeTitleManager alternativeTitleManager;
	
	public TitleCollection()
	{
		// ensure the parentVO is never null;
		this(new MdsPublicationVO());
	}

	public TitleCollection(TitleIF titleIF)
	{
		setTitleIF(titleIF);
	}

	public String removeTitle()
	{
		// when the title is removed, the first alternative title will become the title
		TextVO newTitle = getTitleIF().getAlternativeTitles().remove(0);
		getTitleIF().setTitle(newTitle);
		return null;
	}

	public String addTitle()
	{
		// a new title will be added at the end of the list of alternative titles
		TextVO newTitle = new TextVO();
		this.titleIF.getAlternativeTitles().add(newTitle);
		return null;
	}
	
	public TitleIF getTitleIF()
	{
		return titleIF;
	}

	public void setTitleIF(TitleIF titleIF)
	{
		this.titleIF = titleIF;
		if (titleIF.getTitle() == null)
		{
			titleIF.setTitle(new TextVO());
		}
		// ensure proper initialization of our DataModelManager
		alternativeTitleManager = new AlternativeTitleManager(titleIF);
	}
	
    public SelectItem[] getLanguageOptions()
    {
    	return CommonUtils.getLanguageOptions();
    }

	/**
	 * Specialized DataModelManager to deal with objects of type TextVO
	 * @author Mario Wagner
	 */
	public class AlternativeTitleManager extends DataModelManager<TextVO>
	{
		TitleIF titleIFimpl;
		
		public AlternativeTitleManager(TitleIF parentVO)
		{
			setTitleIF(parentVO);
		}
		
		public TextVO createNewObject()
		{
			TextVO newTitle = new TextVO();
			return newTitle;
		}
		
		public List<TextVO> getDataListFromVO()
		{
			if (titleIFimpl == null) return null;
			return titleIFimpl.getAlternativeTitles();
		}

		public void setTitleIF(TitleIF titleIFimpl)
		{
			this.titleIFimpl = titleIFimpl;
			setObjectList(titleIF.getAlternativeTitles());
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}

	public AlternativeTitleManager getAlternativeTitleManager()
	{
		return alternativeTitleManager;
	}

	public void setAlternativeTitleManager(AlternativeTitleManager alternativeTitleManager)
	{
		this.alternativeTitleManager = alternativeTitleManager;
	}
	
}
