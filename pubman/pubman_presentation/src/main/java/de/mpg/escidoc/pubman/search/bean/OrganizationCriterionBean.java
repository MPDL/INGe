package de.mpg.escidoc.pubman.search.bean;

import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.OrganizationCriterionVO;

/**
 * POJO bean to deal with one OrganizationCriterionVO.
 * 
 * @author Mario Wagner
 */
public class OrganizationCriterionBean extends CriterionBean
{
	public static final String BEAN_NAME = "OrganizationCriterionBean";
	
	private OrganizationCriterionVO organizationCriterionVO;
	
	
    public OrganizationCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new OrganizationCriterionVO());
	}

	public OrganizationCriterionBean(OrganizationCriterionVO organizationCriterionVO)
	{
		setOrganizationCriterionVO(organizationCriterionVO);
	}

	@Override
	public CriterionVO getCriterionVO()
	{
		return organizationCriterionVO;
	}

	public OrganizationCriterionVO getOrganizationCriterionVO()
	{
		return organizationCriterionVO;
	}

	public void setOrganizationCriterionVO(OrganizationCriterionVO organizationCriterionVO)
	{
		this.organizationCriterionVO = organizationCriterionVO;
	}
	
	
	/**
	 * Action navigation call to clear the current part of the form
	 * @return null
	 */
	public String clearCriterion()
	{
		organizationCriterionVO.setSearchString("");
		
		// navigation refresh
		return null;
	}

}
