package de.mpg.escidoc.pubman.search.bean;

import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.pubman.search.bean.criterion.IdentifierCriterion;

/**
 * POJO bean to deal with one IdentifierCriterionVO.
 * 
 * @author Mario Wagner
 */
public class IdentifierCriterionBean extends CriterionBean
{
	public static final String BEAN_NAME = "IdentifierCriterionBean";
	
	private IdentifierCriterion identifierCriterionVO;
	
	// collapsed by default
	protected boolean collapsed = true;
	
	
    public IdentifierCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new IdentifierCriterion());
	}

	public IdentifierCriterionBean(IdentifierCriterion identifierCriterionVO)
	{
		setIdentifierCriterionVO(identifierCriterionVO);
	}

	@Override
	public Criterion getCriterionVO()
	{
		return identifierCriterionVO;
	}

	public IdentifierCriterion getIdentifierCriterionVO()
	{
		return identifierCriterionVO;
	}

	public void setIdentifierCriterionVO(IdentifierCriterion identifierCriterionVO)
	{
		this.identifierCriterionVO = identifierCriterionVO;
	}
	
	
	/**
	 * Action navigation call to clear the current part of the form
	 * @return null
	 */
	public String clearCriterion()
	{
		identifierCriterionVO.setSearchString("");
		
		// navigation refresh
		return null;
	}

}
