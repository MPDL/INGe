/*
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

package de.mpg.mpdl.inge.pubman.web;

import jakarta.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;

/**
 * 
 * Request Bean for affiliation details page (which is a popup taht is opened when clicking an info
 * button on affiliation tree)
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "AffiliationDetailPage")
@SuppressWarnings("serial")
public class AffiliationDetailPage extends FacesBean {
  private static final Logger logger = Logger.getLogger(AffiliationDetailPage.class);

  private AffiliationVOPresentation affiliation;

  public AffiliationDetailPage() {
    try {
      final String affiliationId = FacesTools.getExternalContext().getRequestParameterMap().get("id");

      AffiliationDbVO affVO = ApplicationBean.INSTANCE.getOrganizationService().get(affiliationId, null);
      if (affVO == null) {
        AffiliationDetailPage.logger.warn("Organizational unit not found: " + affiliationId);
        this.error(this.getMessage("AffiliationDetailPage_detailsNotRetrieved"));
      }
      this.affiliation = new AffiliationVOPresentation(affVO);
    } catch (final Exception e) {
      this.error(this.getMessage("AffiliationDetailPage_detailsNotRetrieved"));
      logger.error("Error getting affiliation details", e);
    }
  }

  public void setAffiliation(AffiliationVOPresentation affiliation) {
    this.affiliation = affiliation;
  }

  public AffiliationVOPresentation getAffiliation() {
    return this.affiliation;
  }
}
