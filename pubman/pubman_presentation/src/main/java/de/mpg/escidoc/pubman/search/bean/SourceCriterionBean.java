package de.mpg.escidoc.pubman.search.bean;

import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.SourceCriterionVO;

/**
 * POJO bean to deal with one SourceCriterionVO.
 * 
 * @author Mario Wagner
 */
public class SourceCriterionBean extends CriterionBean
{
	public static final String BEAN_NAME = "SourceCriterionBean";
	
	private SourceCriterionVO sourceCriterionVO;
	
	
    public SourceCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new SourceCriterionVO());
	}

	public SourceCriterionBean(SourceCriterionVO sourceCriterionVO)
	{
		setSourceCriterionVO(sourceCriterionVO);
	}

	@Override
	public CriterionVO getCriterionVO()
	{
		return sourceCriterionVO;
	}

	public SourceCriterionVO getSourceCriterionVO()
	{
		return sourceCriterionVO;
	}

	public void setSourceCriterionVO(SourceCriterionVO sourceCriterionVO)
	{
		this.sourceCriterionVO = sourceCriterionVO;
	}
	
	
	/**
	 * Action navigation call to clear the current part of the form
	 * @return null
	 */
	public String clearCriterion()
	{
		sourceCriterionVO.setSearchString("");
		
		// navigation refresh
		return null;
	}

}
