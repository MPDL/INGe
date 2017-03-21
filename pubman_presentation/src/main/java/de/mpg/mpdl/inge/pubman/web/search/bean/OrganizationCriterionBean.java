package de.mpg.mpdl.inge.pubman.web.search.bean;

import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.pubman.web.affiliation.AffiliationBean;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.OrganizationCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;

/**
 * POJO bean to deal with one OrganizationCriterionVO.
 * 
 * @author Mario Wagner
 */
@SuppressWarnings("serial")
public class OrganizationCriterionBean extends CriterionBean {
  public static final String BEAN_NAME = "OrganizationCriterionBean";

  private OrganizationCriterion organizationCriterionVO;


  public OrganizationCriterionBean() {
    // ensure the parentVO is never null;
    this(new OrganizationCriterion());
  }

  public OrganizationCriterionBean(OrganizationCriterion organizationCriterionVO) {
    setOrganizationCriterionVO(organizationCriterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return organizationCriterionVO;
  }

  public OrganizationCriterion getOrganizationCriterionVO() {
    return organizationCriterionVO;
  }

  public void setOrganizationCriterionVO(OrganizationCriterion organizationCriterionVO) {
    this.organizationCriterionVO = organizationCriterionVO;
  }


  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public String clearCriterion() {
    organizationCriterionVO.setSearchString("");
    AffiliationVO affiliationVO = new AffiliationVO();
    affiliationVO.setReference(new AffiliationRO());
    organizationCriterionVO.setAffiliation(new AffiliationVOPresentation(affiliationVO));

    // navigation refresh
    return null;
  }

  /**
   * Action navigation call to select the creator organisation
   * 
   * @return
   */
  public String selectOrganisation() {
    if (organizationCriterionVO.getSearchString() == null) {
      organizationCriterionVO.setSearchString("");
    }

    // Set this value to let the affiliation tree know where to jump after selection.
    AffiliationBean affiliationBean = FacesTools.findBean("AffiliationBean");
    affiliationBean.setSource("AdvancedSearch");
    affiliationBean.setCache(organizationCriterionVO);

    return "loadAffiliationTree";
  }

}
