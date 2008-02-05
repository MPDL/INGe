package de.mpg.escidoc.pubman.search.bean;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.escidoc.services.pubman.valueobjects.AnyFieldCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.TitleCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.TopicCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO.LogicOperator;

/**
 * POJO bean to deal with one AnyFieldCriterionVO, TitleCriterionVO or TopicCriterionVO.
 * Type change during runtime is supported using a integrated ValueChangeListener.
 * 
 * @author Mario Wagner
 */
public class AnyFieldCriterionBean extends CriterionBean
{
	public static final String BEAN_NAME = "AnyFieldCriterionBean";
	
	private CriterionVO criterionVO;
	
//	private AnyFieldCriterionVO anyFieldCriterionVO;
//	private TitleCriterionVO titleCriterionVO;
//	private TopicCriterionVO topicCriterionVO;
	
	//private String searchText;
	private String selectedType = "Any";
	private boolean includeFiles;
	
    public AnyFieldCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new AnyFieldCriterionVO());
	}

	public AnyFieldCriterionBean(CriterionVO criterionVO)
	{
		setCriterionVO(criterionVO);
	}

	@Override
	public CriterionVO getCriterionVO()
	{
	    return criterionVO;
	    
//		if (selectedType != null && selectedType.equals("Title"))
//		{
//			return titleCriterionVO;
//		}
//		else if (selectedType != null && selectedType.equals("Topic"))
//		{
//			return topicCriterionVO;
//		}
//		else
//		{
//			return anyFieldCriterionVO;
//		}
	}
	
	public void setCriterionVO(CriterionVO criterionVO)
	{
	    
	    this.criterionVO = criterionVO;
	    
//		if (criterionVO instanceof AnyFieldCriterionVO)
//		{
//			setAnyFieldCriterionVO((AnyFieldCriterionVO)criterionVO);
//		}
//		else if (criterionVO instanceof TitleCriterionVO)
//		{
//			setTitleCriterionVO((TitleCriterionVO)criterionVO);
//		}
//		else if (criterionVO instanceof TopicCriterionVO)
//		{
//			setTopicCriterionVO((TopicCriterionVO)criterionVO);
//		}
//		else
//			throw new IllegalArgumentException("CriterionVO type used is not supported");
	}

//	public AnyFieldCriterionVO getAnyFieldCriterionVO()
//	{
//		return anyFieldCriterionVO;
//	}
//
//	public void setAnyFieldCriterionVO(AnyFieldCriterionVO anyFieldCriterionVO)
//	{
//		if (anyFieldCriterionVO != null)
//		{
//			this.titleCriterionVO = null;
//			this.topicCriterionVO = null;
//			searchText = anyFieldCriterionVO.getSearchString();
//			includeFiles = anyFieldCriterionVO.isIncludeFiles();
//			selectedType = "Any";
//		}
//		this.anyFieldCriterionVO = anyFieldCriterionVO;
//		this.includeFiles = anyFieldCriterionVO.isIncludeFiles();
//	}
//	
//	public TitleCriterionVO getTitleCriterionVO()
//	{
//		return titleCriterionVO;
//	}
//
//	public void setTitleCriterionVO(TitleCriterionVO titleCriterionVO)
//	{
//		if (titleCriterionVO != null)
//		{
//			this.anyFieldCriterionVO = null;
//			this.topicCriterionVO = null;
//			searchText = titleCriterionVO.getSearchString();
//			selectedType = "Title";
//		}
//		this.titleCriterionVO = titleCriterionVO;
//	}
//
//	public TopicCriterionVO getTopicCriterionVO()
//	{
//		return topicCriterionVO;
//	}
//
//	public void setTopicCriterionVO(TopicCriterionVO topicCriterionVO)
//	{
//		if (topicCriterionVO != null)
//		{
//			this.anyFieldCriterionVO = null;
//			this.titleCriterionVO = null;
//			searchText = topicCriterionVO.getSearchString();
//			selectedType = "Topic";
//		}
//		this.topicCriterionVO = topicCriterionVO;
//	}

	/**
	 * Action navigation call to clear the current part of the form
	 * @return null
	 */
	public String clearCriterion()
	{
		selectedType = "Any";
		setIncludeFiles(false);
		criterionVO = new AnyFieldCriterionVO();
		// navigation refresh
		return null;
	}

	public SelectItem[] getTypeOptions()
    {
 
        SelectItem TYPE_TITLE = new SelectItem("Title", getLabel("adv_search_lblRgbTitle"));
        SelectItem TYPE_TOPIC = new SelectItem("Topic", getLabel("adv_search_lblRgbTopic"));
        SelectItem TYPE_ANY = new SelectItem("Any", getLabel("adv_search_lblRgbAny"));
        SelectItem[] TYPE_OPTIONS = new SelectItem[]{TYPE_TITLE, TYPE_TOPIC, TYPE_ANY};

    	return TYPE_OPTIONS;
    }

	public String getSelectedType()
	{
		return selectedType;
	}

	public void setSelectedType(String selectedType)
	{
		this.selectedType = selectedType;
	}

	public boolean isIncludeFiles()
	{
		return includeFiles;
	}

	public void setIncludeFiles(boolean includeFiles)
	{
		this.includeFiles = includeFiles;
		if (criterionVO instanceof AnyFieldCriterionVO)
		{
		    ((AnyFieldCriterionVO)criterionVO).setIncludeFiles(includeFiles);
		}
	}

	public boolean isIncludeFilesDisabled()
	{
		return !(criterionVO instanceof AnyFieldCriterionVO);
	}

	/**
	 * ValueChangeListener method to handle changes in the selectedType. 
	 * @param event
	 * @throws AbortProcessingException
	 */
	public void processTypeChanged(ValueChangeEvent event)
	{
		String newVal = (String) event.getNewValue();

		// get current searchString and operator and move it to the new VO
		String searchString = criterionVO.getSearchString();
		LogicOperator logicOperator = criterionVO.getLogicOperator();

		this.selectedType = newVal;
		
		if (selectedType != null && selectedType.equals("Title"))
		{
			TitleCriterionVO titleCriterionVO = new TitleCriterionVO();
			titleCriterionVO.setSearchString(searchString);
			titleCriterionVO.setLogicOperator(logicOperator);
			// Reinitialize this POJO, because the selectedType has been changed.
			criterionVO = titleCriterionVO;
		}
		else if (selectedType != null && selectedType.equals("Topic"))
		{
			TopicCriterionVO topicCriterionVO = new TopicCriterionVO();
			topicCriterionVO.setSearchString(searchString);
			topicCriterionVO.setLogicOperator(logicOperator);
			// Reinitialize this POJO, because the selectedType has been changed.
			criterionVO = topicCriterionVO;
		}
		else
		{
			AnyFieldCriterionVO anyFieldCriterionVO = new AnyFieldCriterionVO();
			anyFieldCriterionVO.setSearchString(searchString);
			anyFieldCriterionVO.setLogicOperator(logicOperator);
			// Reinitialize this POJO, because the selectedType has been changed.
			criterionVO = anyFieldCriterionVO;
		}
		
//		// enforce rendering of the response
//		FacesContext context = FacesContext.getCurrentInstance();
//	    context.renderResponse();
	}

//	public String getSearchText()
//	{
//		return searchText;
//	}
//
//	public void setSearchText(String searchText)
//	{
//		this.searchText = searchText;
//		getCriterionVO().setSearchString(searchText);
//	}
	
}
