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
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.affiliation.AffiliationBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.service.pubman.impl.OrganizationServiceDbImpl;
import de.mpg.mpdl.inge.util.PropertyReader;

@SuppressWarnings("serial")
public class AffiliationVOPresentation extends AffiliationDbVO implements Comparable<AffiliationVOPresentation> {
  private static final Logger logger = LogManager.getLogger(AffiliationVOPresentation.class);

  private static final int SHORTENED_NAME_STANDARD_LENGTH = 65;
  private static final int SHORTENED_LEVEL_LENGTH = 5;

  private AffiliationVOPresentation parent = null;

  private List<AffiliationDbVO> predecessors = new ArrayList<>();
  private List<AffiliationDbVO> successors = null;
  private List<AffiliationVOPresentation> children = null;

  private String idPath;
  private String namePath;

  private boolean hasChildren = false;
  private boolean selectedForAuthor = false;

  public AffiliationVOPresentation(AffiliationDbVO affiliation) {
    super(affiliation);
    this.namePath = this.getDetails().getName();
    this.idPath = this.getObjectId();
    this.predecessors = this.getAffiliationVOfromRO(this.getPredecessorAffiliations());
    this.hasChildren = affiliation.getHasChildren();
  }

  public List<AffiliationVOPresentation> getChildren() throws Exception {
    if (null == this.children && this.hasChildren) {
      List<AffiliationDbVO> childOus = (ApplicationBean.INSTANCE.getOrganizationService()).searchChildOrganizations(this.getObjectId());

      this.children = CommonUtils.convertToAffiliationVOPresentationList(childOus);

      for (final AffiliationVOPresentation affiliationVOPresentation : this.children) {
        affiliationVOPresentation.parent = this;
        affiliationVOPresentation.namePath = affiliationVOPresentation.getDetails().getName() + ", " + this.namePath;
        affiliationVOPresentation.idPath = affiliationVOPresentation.getObjectId() + " " + this.idPath;
      }
    }

    return this.children;
  }

  public MdsOrganizationalUnitDetailsVO getDetails() {
    if (null != this.getMetadata() && this.getMetadata() instanceof MdsOrganizationalUnitDetailsVO) {
      return this.getMetadata();
    } else {
      return new MdsOrganizationalUnitDetailsVO();
    }
  }

  public boolean getMps() {
    try {
      final String rootAffiliationMPG = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ROOT_ORGANISATION_ID);

      return this.getObjectId().equals(rootAffiliationMPG);
    } catch (final Exception e) {
      logger.error("Error reading Properties", e);
      return false;
    }
  }

  public boolean getTopLevel() {
    return (null == this.parent);
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
    return ((AffiliationBean) FacesTools.findBean("AffiliationBean")).startSearch();
  }

  public AffiliationVOPresentation getParent() {
    return this.parent;
  }

  public void setParent(AffiliationVOPresentation parent) {
    this.parent = parent;
  }

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
    if ("closed".equals(this.getPublicStatus().toString())) {
      return "3" + this.getName().toLowerCase();
    } else if (this.getMps() && "opened".equals(this.getPublicStatus().toString())) {
      return "0" + this.getName().toLowerCase();
    } else if ("opened".equals(this.getPublicStatus().toString())) {
      return "1" + this.getName().toLowerCase();
    } else if ("created".equals(this.getPublicStatus().toString())) {
      return "2" + this.getName().toLowerCase();
    } else {
      return "9" + this.getName().toLowerCase();
    }
  }

  public String getName() {
    if (null != this.getMetadata() && this.getMetadata() instanceof MdsOrganizationalUnitDetailsVO) {
      return this.getMetadata().getName();
    }

    return null;
  }

  public String getShortenedName() {
    AffiliationVOPresentation aff = this;
    int level = 0;

    while (!aff.getTopLevel()) {
      aff = aff.parent;
      level++;
    }

    if (null != this.getMetadata() && this.getMetadata() instanceof MdsOrganizationalUnitDetailsVO) {
      if (this.getMetadata().getName().length() > (AffiliationVOPresentation.SHORTENED_NAME_STANDARD_LENGTH
          - (level * AffiliationVOPresentation.SHORTENED_LEVEL_LENGTH))) {
        return this.getMetadata().getName().substring(0,
            (AffiliationVOPresentation.SHORTENED_NAME_STANDARD_LENGTH - (level * AffiliationVOPresentation.SHORTENED_LEVEL_LENGTH)))
            + "...";
      } else {
        return this.getMetadata().getName();
      }
    }

    return null;
  }

  public List<String> getUris() {
    final List<IdentifierVO> identifiers = this.getMetadata().getIdentifiers();
    final List<String> uriList = new ArrayList<>();

    for (final IdentifierVO identifier : identifiers) {
      if (null != identifier.getType() && identifier.getType().equals(IdentifierVO.IdType.URI)) {
        uriList.add(identifier.getId());
      }
    }

    return uriList;
  }

  public boolean getIsClosed() {
    return AffiliationDbVO.State.CLOSED.equals(this.getPublicStatus());
  }

  @Override
  public int compareTo(AffiliationVOPresentation other) {
    return this.getSortOrder().compareTo(other.getSortOrder());
  }

  private List<AffiliationDbVO> getAffiliationVOfromRO(List<AffiliationDbRO> affiliations) {
    return this.retrieveAllOrganizationalUnits(affiliations);
  }

  /**
   * @Retrieves list of all contexts for which user has granted privileges @see
   *            LoginHelper.getUserGrants
   * @throws SecurityException
   * @throws TechnicalException
   */
  private List<AffiliationDbVO> retrieveAllOrganizationalUnits(List<AffiliationDbRO> affiliations) {

    List<AffiliationDbVO> transformedAffs = new ArrayList<>();

    if (affiliations.isEmpty()) {
      return transformedAffs;
    }
    try {
      BoolQuery.Builder bq = new BoolQuery.Builder();
      for (final AffiliationDbRO id : affiliations) {
        bq.should(TermQuery.of(i -> i.field(OrganizationServiceDbImpl.INDEX_OBJECT_ID).value(id.getObjectId()))._toQuery());
      }

      SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(bq.build()._toQuery());
      SearchRetrieveResponseVO<AffiliationDbVO> resp = ApplicationBean.INSTANCE.getOrganizationService().search(srr, null);
      transformedAffs = resp.getRecords().stream().map(SearchRetrieveRecordVO::getData).collect(Collectors.toList());



    } catch (final Exception e) {
    }

    return transformedAffs;
  }

  /**
   * @return the predecessors
   */
  public List<AffiliationDbVO> getPredecessors() {
    return this.predecessors;
  }

  /**
   * @param predecessors the predecessors to set
   */
  public void setPredecessors(List<AffiliationDbVO> predecessors) {
    this.predecessors = predecessors;
  }

  /**
   * @return the successors
   */
  public List<AffiliationDbVO> getSuccessors() {
    this.fetchSuccessors();
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
    if (null == this.successors) {
      try {
        // TODO tendres: This admin login is neccessary because of bug
        // http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=597
        // If the org tree structure is fetched via search, this is obsolete
        this.successors = ApplicationBean.INSTANCE.getOrganizationService().searchSuccessors(this.getObjectId());

      } catch (final Exception e) {
        this.successors = new ArrayList<>();
      }
    }
  }

  /**
   * Are predecessors available.
   *
   * @return true if predecessors are available
   */
  public boolean getHasSuccessors() {
    this.fetchSuccessors();

    return (!this.successors.isEmpty());
  }

  /**
   * @return the hasChildren
   */
  public boolean isHasChildren() {
    return this.hasChildren;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof AffiliationVOPresentation && compareTo((AffiliationVOPresentation) o) == 0;
  }
}
