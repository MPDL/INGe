package de.mpg.escidoc.pubman.search.bean;

import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.pubman.search.bean.criterion.SourceCriterion;

/**
 * POJO bean to deal with one SourceCriterionVO.
 * 
 * @author Mario Wagner
 */
public class SourceCriterionBean extends CriterionBean
{
	public static final String BEAN_NAME = "SourceCriterionBean";
	
	private SourceCriterion sourceCriterionVO;
	
	// collapsed by default
	protected boolean collapsed = true;
	
    public SourceCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new SourceCriterion());
	}

	public SourceCriterionBean(SourceCriterion sourceCriterionVO)
	{
		setSourceCriterionVO(sourceCriterionVO);
	}

	@Override
	public Criterion getCriterionVO()
	{
		return sourceCriterionVO;
	}

	public SourceCriterion getSourceCriterionVO()
	{
		return sourceCriterionVO;
	}

	public void setSourceCriterionVO(SourceCriterion sourceCriterionVO)
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
