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

package de.mpg.mpdl.inge.pubman.web.search.bean.criterion;

import java.util.ArrayList;

import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.CriterionType;

/**
 * organization criterion vo for the advanced search.
 * 
 * @created 15-Mai-2007 15:45:25
 * @author NiH
 * @version 1.0 Revised by NiH: 13.09.2007
 */
public class OrganizationCriterion extends Criterion {
  /**
   * constructor.
   */

  AffiliationVOPresentation affiliation = null;
  private boolean includePredecessorsAndSuccessors = false;

  public OrganizationCriterion() {
    final AffiliationVO affiliationVO = new AffiliationVO();
    affiliationVO.setReference(new AffiliationRO());
    this.affiliation = new AffiliationVOPresentation(affiliationVO);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
    final ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();
    if (this.getAffiliation() != null && this.getAffiliation().getReference().getObjectId() != null
        && !"".equals(this.getAffiliation().getReference().getObjectId())) {
      final MetadataSearchCriterion criterion =
          new MetadataSearchCriterion(CriterionType.CREATOR_ORGANIZATION_IDS_WITH_PATH, this
              .getAffiliation().getReference().getObjectId());
      criterions.add(criterion);
    } else if (this.isSearchStringEmpty() != true) {
      final MetadataSearchCriterion criterion =
          new MetadataSearchCriterion(CriterionType.CREATOR_ORGANIZATION, this.getSearchString());
      criterions.add(criterion);
    }
    return criterions;
  }

  /**
   * @return the affiliation
   */
  public AffiliationVOPresentation getAffiliation() {
    return this.affiliation;
  }

  public String getAffiliationName() {
    if (this.affiliation == null) {
      return "";
    }

    return this.affiliation.getName();
  }

  public boolean getAffiliationEmpty() {
    return (this.affiliation == null);
  }

  /**
   * @param affiliation the affiliation to set
   */
  public void setAffiliation(AffiliationVOPresentation affiliation) {
    this.affiliation = affiliation;
  }

  public void setIncludePredecessorsAndSuccessors(boolean include) {
    this.includePredecessorsAndSuccessors = include;
  }

  public boolean getIncludePredecessorsAndSuccessors() {
    return this.includePredecessorsAndSuccessors;
  }
}
