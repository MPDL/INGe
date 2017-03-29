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
  private OrganizationCriterion organizationCriterionVO;


  public OrganizationCriterionBean() {
    // ensure the parentVO is never null;
    this(new OrganizationCriterion());
  }

  public OrganizationCriterionBean(OrganizationCriterion organizationCriterionVO) {
    this.setOrganizationCriterionVO(organizationCriterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return this.organizationCriterionVO;
  }

  public OrganizationCriterion getOrganizationCriterionVO() {
    return this.organizationCriterionVO;
  }

  public void setOrganizationCriterionVO(OrganizationCriterion organizationCriterionVO) {
    this.organizationCriterionVO = organizationCriterionVO;
  }


  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public void clearCriterion() {
    this.organizationCriterionVO.setSearchString("");
    final AffiliationVO affiliationVO = new AffiliationVO();
    affiliationVO.setReference(new AffiliationRO());
    this.organizationCriterionVO.setAffiliation(new AffiliationVOPresentation(affiliationVO));
  }

  /**
   * Action navigation call to select the creator organisation
   * 
   * @return
   */
  public String selectOrganisation() {
    if (this.organizationCriterionVO.getSearchString() == null) {
      this.organizationCriterionVO.setSearchString("");
    }

    // Set this value to let the affiliation tree know where to jump after selection.
    final AffiliationBean affiliationBean = FacesTools.findBean("AffiliationBean");
    affiliationBean.setSource("AdvancedSearch");
    affiliationBean.setCache(this.organizationCriterionVO);

    return "loadAffiliationTree";
  }
}
