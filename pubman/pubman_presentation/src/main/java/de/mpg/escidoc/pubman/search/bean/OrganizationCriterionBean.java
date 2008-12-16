package de.mpg.escidoc.pubman.search.bean;

import de.mpg.escidoc.pubman.affiliation.AffiliationBean;
import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.pubman.search.bean.criterion.OrganizationCriterion;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;

/**
 * POJO bean to deal with one OrganizationCriterionVO.
 * 
 * @author Mario Wagner
 */
public class OrganizationCriterionBean extends CriterionBean
{
	public static final String BEAN_NAME = "OrganizationCriterionBean";
	
	private OrganizationCriterion organizationCriterionVO;
	
	
    public OrganizationCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new OrganizationCriterion());
	}

	public OrganizationCriterionBean(OrganizationCriterion organizationCriterionVO)
	{
		setOrganizationCriterionVO(organizationCriterionVO);
	}

	@Override
	public Criterion getCriterionVO()
	{
		return organizationCriterionVO;
	}

	public OrganizationCriterion getOrganizationCriterionVO()
	{
		return organizationCriterionVO;
	}

	public void setOrganizationCriterionVO(OrganizationCriterion organizationCriterionVO)
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
	
	/**
     * Action navigation call to select the creator organisation
     * @return
     */
    public String selectOrganisation()
    {
        if (organizationCriterionVO.getSearchString() == null)
        {
            organizationCriterionVO.setSearchString("");
        }

        // Set this value to let the affiliation tree know where to jump after selection.
        ((AffiliationBean)getSessionBean(AffiliationBean.class)).setSource("AdvancedSearch");
        ((AffiliationBean)getSessionBean(AffiliationBean.class)).setCache(organizationCriterionVO);
        
        return "loadAffiliationTree";
    }

}
