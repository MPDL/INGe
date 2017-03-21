/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web.util.vos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.AffiliationRefFilter;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.affiliation.AffiliationBean;
import de.mpg.mpdl.inge.pubman.web.search.AffiliationDetail;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.util.AdminHelper;
import de.mpg.mpdl.inge.util.PropertyReader;

@SuppressWarnings("serial")
public class AffiliationVOPresentation extends AffiliationVO implements
    Comparable<AffiliationVOPresentation> {
  private static final Logger logger = Logger.getLogger(AffiliationVOPresentation.class);

  private static final int SHORTENED_NAME_STANDARD_LENGTH = 65;
  private static final int SHORTENED_LEVEL_LENGTH = 5;

  // private final InternationalizationHelper i18nHelper = (InternationalizationHelper) FacesContext
  // .getCurrentInstance().FacesTools.getExternalContext().getSessionMap()
  // .get(InternationalizationHelper.BEAN_NAME);

  private AffiliationVOPresentation parent = null;

  private List<AffiliationVO> predecessors = new ArrayList<AffiliationVO>();
  private List<AffiliationVO> successors = null;
  private List<AffiliationVOPresentation> children = null;

  private String idPath;
  private String namePath;

  private boolean hasChildren = false;
  private boolean selectedForAuthor = false;

  public AffiliationVOPresentation(AffiliationVO affiliation) {
    super(affiliation);
    this.namePath = getDetails().getName();
    this.idPath = getReference().getObjectId();
    this.predecessors = getAffiliationVOfromRO(getPredecessorAffiliations());
    this.hasChildren = affiliation.getHasChildren();
  }

  public List<AffiliationVOPresentation> getChildren() throws Exception {
    if (children == null && isHasChildren()) {
      children =
          ((ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean"))
              .searchChildAffiliations(this);
    }

    return children;
  }

  public MdsOrganizationalUnitDetailsVO getDetails() {
    if (getMetadataSets().size() > 0
        && getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO) {
      return (MdsOrganizationalUnitDetailsVO) getMetadataSets().get(0);
    } else {
      return new MdsOrganizationalUnitDetailsVO();
    }
  }

  public boolean getMps() {
    try {
      String rootAffiliationMPG = PropertyReader.getProperty("escidoc.pubman.root.organisation.id");

      return getReference().getObjectId().equals(rootAffiliationMPG);
    } catch (Exception e) {
      logger.error("Error reading Properties", e);
      return false;
    }
  }

  public boolean getTopLevel() {
    return (this.parent == null);
  }

  // /**
  // * This returns a description of the affiliation in a html form.
  // *
  // * @return html description
  // */
  // public String getHtmlDescription() {
  // StringBuffer html = new StringBuffer();
  //
  // html.append("<html><head></head><body>");
  // html.append("<div class=\"affDetails\"><h1>"
  // + this.i18nHelper.getLabel("AffiliationTree_txtHeadlineDetails") + "</h1>");
  // html.append("<div class=\"formField\">");
  //
  // if (getDetails().getDescriptions().size() > 0
  // && !"".equals(getDetails().getDescriptions().get(0))) {
  // html.append("<div>");
  // html.append(getDetails().getDescriptions().get(0));
  // html.append("</div><br/>");
  // }
  //
  // for (IdentifierVO identifier : getDetails().getIdentifiers()) {
  // if (!identifier.getId().trim().equals("")) {
  // html.append("<span>, &nbsp;");
  // html.append(identifier.getId());
  // html.append("</span>");
  // }
  // }
  //
  // html.append("</div></div>");
  // html.append("</body></html>");
  //
  // return html.toString();
  // }

  public String startSearch() {
    ((AffiliationBean) FacesTools.findBean("AffiliationBean")).setSelectedAffiliation(this);
    ((AffiliationDetail) FacesTools.findBean("AffiliationDetail")).setAffiliationVO(this);
    return ((AffiliationBean) FacesTools.findBean("AffiliationBean")).startSearch();
  }

  public AffiliationVOPresentation getParent() {
    return this.parent;
  }

  public void setParent(AffiliationVOPresentation parent) {
    this.parent = parent;
  }

  // /**
  // * Return any bean stored in session scope under the specified name.
  // *
  // * @param cls The bean class.
  // * @return the actual or new bean instance
  // */
  // private static synchronized Object findSessionBean(final Class<?> cls) {
  // String name = null;
  // try {
  // name = (String) cls.getField("BEAN_NAME").get(new String());
  // } catch (Exception e) {
  // throw new RuntimeException("Error getting bean name of " + cls, e);
  // }
  // Object result =
  // FacesTools.getCurrentInstance().FacesTools.getExternalContext().getSessionMap().get(name);
  // if (result == null) {
  // try {
  // Object newBean = cls.newInstance();
  // FacesTools.getCurrentInstance().FacesTools.getExternalContext().getSessionMap().put(name,
  // newBean);
  // return newBean;
  // } catch (Exception e) {
  // throw new RuntimeException("Error creating new bean of type " + cls, e);
  // }
  // } else {
  // return result;
  // }
  // }

  /**
   * Returns the complete path to this affiliation as a string with the name of the affiliations.
   */
  public String getNamePath() {
    return this.namePath;
  }

  public void setNamePath(String path) {
    this.namePath = path;
  }

  /** Returns the complete path to this affiliation as a string with the ids of the affiliations */
  public String getIdPath() {
    return this.idPath;
  }

  public void setIdPath(String idPath) {
    this.idPath = idPath;
  }

  public String getSortOrder() {
    if ("closed".equals(this.getPublicStatus())) {
      return "3" + getName().toLowerCase();
    } else if (getMps() && "opened".equals(this.getPublicStatus())) {
      return "0" + getName().toLowerCase();
    } else if ("opened".equals(this.getPublicStatus())) {
      return "1" + getName().toLowerCase();
    } else if ("created".equals(this.getPublicStatus())) {
      return "2" + getName().toLowerCase();
    } else {
      return "9" + getName().toLowerCase();
    }
  }

  public String getName() {
    if (getMetadataSets().size() > 0
        && getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO) {
      return ((MdsOrganizationalUnitDetailsVO) getMetadataSets().get(0)).getName();
    }

    return null;
  }

  public String getShortenedName() {
    AffiliationVOPresentation aff = this;
    int level = 0;

    while (!aff.getTopLevel()) {
      aff = aff.getParent();
      level++;
    }

    if (getMetadataSets().size() > 0
        && getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO) {
      if (((MdsOrganizationalUnitDetailsVO) getMetadataSets().get(0)).getName().length() > (SHORTENED_NAME_STANDARD_LENGTH - (level * SHORTENED_LEVEL_LENGTH))) {
        return ((MdsOrganizationalUnitDetailsVO) getMetadataSets().get(0)).getName().substring(0,
            (SHORTENED_NAME_STANDARD_LENGTH - (level * SHORTENED_LEVEL_LENGTH)))
            + "...";
      } else {
        return ((MdsOrganizationalUnitDetailsVO) getMetadataSets().get(0)).getName();
      }
    }

    return null;
  }

  public List<String> getUris() {
    List<IdentifierVO> identifiers = getDefaultMetadata().getIdentifiers();
    List<String> uriList = new ArrayList<String>();

    for (IdentifierVO identifier : identifiers) {
      if (identifier.getType() != null && identifier.getType().equals(IdentifierVO.IdType.URI)) {
        uriList.add(identifier.getId());
      }
    }

    return uriList;
  }

  public boolean getIsClosed() {
    return getPublicStatus().equals("closed");
  }

  @Override
  public int compareTo(AffiliationVOPresentation other) {
    return getSortOrder().compareTo(other.getSortOrder());
  }

  private List<AffiliationVO> getAffiliationVOfromRO(List<AffiliationRO> affiliations) { /*
                                                                                          * List<
                                                                                          * AffiliationVO
                                                                                          * >
                                                                                          * transformedAffs
                                                                                          * = new
                                                                                          * ArrayList
                                                                                          * <
                                                                                          * AffiliationVO
                                                                                          * >();
                                                                                          * InitialContext
                                                                                          * initialContext
                                                                                          * = null;
                                                                                          * XmlTransforming
                                                                                          * xmlTransforming
                                                                                          * = null;
                                                                                          * if(
                                                                                          * affiliations
                                                                                          * .size()
                                                                                          * == 0 ) {
                                                                                          * return
                                                                                          * transformedAffs
                                                                                          * ; } try
                                                                                          * {
                                                                                          * initialContext
                                                                                          * = new
                                                                                          * InitialContext
                                                                                          * ();
                                                                                          * xmlTransforming
                                                                                          * = (
                                                                                          * XmlTransforming
                                                                                          * )
                                                                                          * initialContext
                                                                                          * .lookup(
                                                                                          * XmlTransforming
                                                                                          * .
                                                                                          * SERVICE_NAME
                                                                                          * ); for(
                                                                                          * AffiliationRO
                                                                                          * affiliation
                                                                                          * :
                                                                                          * affiliations
                                                                                          * ) {
                                                                                          * OrganizationalUnitHandler
                                                                                          * ouHandler
                                                                                          * =
                                                                                          * ServiceLocator
                                                                                          * .
                                                                                          * getOrganizationalUnitHandler
                                                                                          * ();
                                                                                          * String
                                                                                          * ouXml =
                                                                                          * ouHandler
                                                                                          * .
                                                                                          * retrieve
                                                                                          * (
                                                                                          * affiliation
                                                                                          * .
                                                                                          * getObjectId
                                                                                          * ());
                                                                                          * AffiliationVO
                                                                                          * affVO =
                                                                                          * xmlTransforming
                                                                                          * .
                                                                                          * transformToAffiliation
                                                                                          * (ouXml);
                                                                                          * transformedAffs
                                                                                          * .
                                                                                          * add(affVO
                                                                                          * ); }
                                                                                          * return
                                                                                          * transformedAffs
                                                                                          * ; }
                                                                                          * catch
                                                                                          * (Exception
                                                                                          * e) {
                                                                                          * return
                                                                                          * transformedAffs
                                                                                          * ; }
                                                                                          */
    return retrieveAllOrganizationalUnits(affiliations);
  }

  /**
   * @Retrieves list of all contexts for which user has granted privileges @see
   *            LoginHelper.getUserGrants
   * @throws SecurityException
   * @throws TechnicalException
   */
  private List<AffiliationVO> retrieveAllOrganizationalUnits(List<AffiliationRO> affiliations) {

    List<AffiliationVO> transformedAffs = new ArrayList<AffiliationVO>();

    if (affiliations.size() == 0)
      return transformedAffs;
    try {
      OrganizationalUnitHandler ouHandler = ServiceLocator.getOrganizationalUnitHandler();

      if (affiliations.size() == 1) {

        String ouXml = ouHandler.retrieve(affiliations.get(0).getObjectId());
        AffiliationVO affVO = XmlTransformingService.transformToAffiliation(ouXml);
        transformedAffs.add(affVO);
        return transformedAffs;
      } else {
        FilterTaskParamVO filter = new FilterTaskParamVO();

        AffiliationRefFilter affiliationFilter = filter.new AffiliationRefFilter();
        filter.getFilterList().add(affiliationFilter);

        for (AffiliationRO affiliation : affiliations) {
          affiliationFilter.getIdList().add(affiliation);
        }

        String ouXml = ouHandler.retrieveOrganizationalUnits(filter.toMap());
        transformedAffs = XmlTransformingService.transformToAffiliationList(ouXml);

      }
    } catch (Exception e) {
    }

    return transformedAffs;
  }

  /**
   * @return the predecessors
   */
  public List<AffiliationVO> getPredecessors() {
    return this.predecessors;
  }

  /**
   * @param predecessors the predecessors to set
   */
  public void setPredecessors(List<AffiliationVO> predecessors) {
    this.predecessors = predecessors;
  }

  /**
   * @return the successors
   */
  public List<AffiliationVO> getSuccessors() {
    fetchSuccessors();
    return this.successors;
  }

  /**
   * @return the selectedForAuthor
   */
  public boolean getSelectedForAuthor() {
    return this.selectedForAuthor;
  }

  /**
   * @param selectedForAuthor the selectedForAuthor to set
   */
  public void setSelectedForAuthor(boolean selectedForAuthor) {
    this.selectedForAuthor = selectedForAuthor;
  }

  private void fetchSuccessors() {
    if (this.successors == null) {
      try {
        // TODO tendres: This admin login is neccessary because of bug
        // http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=597
        // If the org tree structure is fetched via search, this is obsolete
        String userHandle = AdminHelper.getAdminUserHandle();
        OrganizationalUnitHandler ouHandler =
            ServiceLocator.getOrganizationalUnitHandler(userHandle);
        String ouXml = ouHandler.retrieveSuccessors(reference.getObjectId());
        Logger logger = Logger.getLogger(AffiliationVOPresentation.class);
        logger.debug(ouXml);
        List<AffiliationRO> affROs =
            XmlTransformingService.transformToSuccessorAffiliationList(ouXml);
        this.successors = new ArrayList<AffiliationVO>();
        if (affROs != null && affROs.size() > 0) {
          List<AffiliationVO> affVOs = getAffiliationVOfromRO(affROs);
          this.successors = affVOs;
        }
      } catch (Exception e) {
        this.successors = new ArrayList<AffiliationVO>();
      }
    }
  }

  /**
   * Are predecessors available.
   * 
   * @return true if predecessors are available
   */
  public boolean getHasSuccessors() {
    fetchSuccessors();

    return (this.successors.size() != 0);
  }

  /**
   * @return the hasChildren
   */
  public boolean isHasChildren() {
    return this.hasChildren;
  }
}
