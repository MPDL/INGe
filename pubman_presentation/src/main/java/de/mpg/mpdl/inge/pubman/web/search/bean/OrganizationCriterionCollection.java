package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.OrganizationCriterion;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;

/**
 * Bean to handle the OrganizationCriterionCollection on a single jsp. A
 * OrganizationCriterionCollection is represented by a List<OrganizationCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class OrganizationCriterionCollection {
  private static final Logger logger = Logger.getLogger(OrganizationCriterionCollection.class);

  private List<OrganizationCriterion> parentVO;
  private OrganizationCriterionManager organizationCriterionManager;

  /**
   * CTOR to create a new ArrayList<OrganizationCriterionVO> starting with one empty new
   * OrganizationCriterionVO
   */
  public OrganizationCriterionCollection() {
    // ensure the parentVO is never null;
    final List<OrganizationCriterion> ctorList = new ArrayList<OrganizationCriterion>();
    ctorList.add(new OrganizationCriterion());
    this.setParentVO(ctorList);
  }

  /**
   * CTOR to refine or fill a predefined ArrayList<OrganizationCriterionVO>
   * 
   * @param parentVO
   */
  public OrganizationCriterionCollection(List<OrganizationCriterion> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<OrganizationCriterion> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<OrganizationCriterion> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.organizationCriterionManager = new OrganizationCriterionManager(parentVO);
  }

  private List<OrganizationCriterion> resolveIncludes(List<OrganizationCriterion> inVO) {
    final List<OrganizationCriterion> resolved = new ArrayList<OrganizationCriterion>();

    for (final OrganizationCriterion criterion : inVO) {
      resolved.add(criterion);

      AffiliationVO affiliation;
      try {
        affiliation =
            XmlTransformingService.transformToAffiliation(ServiceLocator
                .getOrganizationalUnitHandler().retrieve(
                    criterion.getAffiliation().getReference().getObjectId()));

        final AffiliationVOPresentation affiliationPres = new AffiliationVOPresentation(affiliation);

        // AffiliationVOPresentation affiliation = criterion.getAffiliation();
        OrganizationCriterionCollection.logger.debug("Adding " + affiliation.toString());

        if (criterion.getIncludePredecessorsAndSuccessors()) {
          final List<AffiliationVO> sucessorsVO = affiliationPres.getSuccessors();

          for (final AffiliationVO affiliationVO : sucessorsVO) {
            final OrganizationCriterion organizationCriterion = new OrganizationCriterion();
            organizationCriterion.setAffiliation(new AffiliationVOPresentation(affiliationVO));
            resolved.add(organizationCriterion);
            OrganizationCriterionCollection.logger.debug("Adding sucessor " + organizationCriterion.getAffiliation().toString());
          }

          final List<AffiliationVO> predecessorsVO = affiliationPres.getPredecessors();

          for (final AffiliationVO affiliationVO : predecessorsVO) {
            final OrganizationCriterion organizationCriterion = new OrganizationCriterion();
            organizationCriterion.setAffiliation(new AffiliationVOPresentation(affiliationVO));
            resolved.add(organizationCriterion);
            OrganizationCriterionCollection.logger.debug("Adding predecessor " + organizationCriterion.getAffiliation().toString());
          }
        }
      } catch (final Exception e) {
        OrganizationCriterionCollection.logger.error("Error while retrieving affiliation from id", e);
      }
    }
    return resolved;
  }

  /**
   * Specialized DataModelManager to deal with objects of type OrganizationCriterionBean
   * 
   * @author Mario Wagner
   */
  public class OrganizationCriterionManager extends DataModelManager<OrganizationCriterionBean> {
    List<OrganizationCriterion> parentVO;

    public OrganizationCriterionManager(List<OrganizationCriterion> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public OrganizationCriterionBean createNewObject() {
      final OrganizationCriterion newVO = new OrganizationCriterion();
      // create a new wrapper pojo
      final OrganizationCriterionBean organizationCriterionBean = new OrganizationCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      this.parentVO.add(newVO);
      return organizationCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      this.parentVO.remove(i);
    }

    public List<OrganizationCriterionBean> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      // we have to wrap all VO's in a nice OrganizationCriterionBean
      final List<OrganizationCriterionBean> beanList = new ArrayList<OrganizationCriterionBean>();
      for (final OrganizationCriterion organizationCriterionVO : this.parentVO) {
        beanList.add(new OrganizationCriterionBean(organizationCriterionVO));
      }

      return beanList;
    }

    public void setParentVO(List<OrganizationCriterion> parentVO) {
      this.parentVO = parentVO;
      // we have to wrap all VO's into a nice OrganizationCriterionBean
      final List<OrganizationCriterionBean> beanList = new ArrayList<OrganizationCriterionBean>();
      for (final OrganizationCriterion organizationCriterionVO : parentVO) {
        beanList.add(new OrganizationCriterionBean(organizationCriterionVO));
      }
      this.setObjectList(beanList);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public OrganizationCriterionManager getOrganizationCriterionManager() {
    return this.organizationCriterionManager;
  }

  public void setOrganizationCriterionManager(
      OrganizationCriterionManager organizationCriterionManager) {
    this.organizationCriterionManager = organizationCriterionManager;
  }

  public void clearAllForms() {
    for (final OrganizationCriterionBean gcb : this.organizationCriterionManager.getObjectList()) {
      gcb.clearCriterion();
    }
  }

  public List<OrganizationCriterion> getFilledCriterion() {
    final List<OrganizationCriterion> returnList = new ArrayList<OrganizationCriterion>();
    for (final OrganizationCriterion vo : this.parentVO) {
      if (((vo.getSearchString() != null && vo.getSearchString().length() > 0) || (vo
          .getAffiliation() != null && vo.getAffiliation().getReference().getObjectId() != null && !""
            .equals(vo.getAffiliation().getReference().getObjectId())))) {
        returnList.add(vo);
      }
    }
    return this.resolveIncludes(returnList);
  }

}
