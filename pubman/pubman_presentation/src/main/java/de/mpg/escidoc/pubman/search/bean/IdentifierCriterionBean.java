package de.mpg.escidoc.pubman.search.bean;

import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.IdentifierCriterionVO;

/**
 * POJO bean to deal with one IdentifierCriterionVO.
 * 
 * @author Mario Wagner
 */
public class IdentifierCriterionBean extends CriterionBean
{
	public static final String BEAN_NAME = "IdentifierCriterionBean";
	
	private IdentifierCriterionVO identifierCriterionVO;
	
	
    public IdentifierCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new IdentifierCriterionVO());
	}

	public IdentifierCriterionBean(IdentifierCriterionVO identifierCriterionVO)
	{
		setIdentifierCriterionVO(identifierCriterionVO);
	}

	@Override
	public CriterionVO getCriterionVO()
	{
		return identifierCriterionVO;
	}

	public IdentifierCriterionVO getIdentifierCriterionVO()
	{
		return identifierCriterionVO;
	}

	public void setIdentifierCriterionVO(IdentifierCriterionVO identifierCriterionVO)
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
