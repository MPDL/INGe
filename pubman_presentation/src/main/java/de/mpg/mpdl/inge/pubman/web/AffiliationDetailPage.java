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

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
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
  public static String BEAN_NAME = "AffiliationDetailPage";

  private static final Logger logger = Logger.getLogger(AffiliationDetailPage.class);

  private AffiliationVOPresentation affiliation;

  public AffiliationDetailPage() {
    try {
      String ouXml = null;
      String affiliationId = getExternalContext().getRequestParameterMap().get("id");
      OrganizationalUnitHandler ouHandler = ServiceLocator.getOrganizationalUnitHandler();
      try {
        ouXml = ouHandler.retrieve(affiliationId);
      } catch (OrganizationalUnitNotFoundException onfe) {
        logger.info("Organizational unit not found: " + affiliationId);
        error(getMessage("AffiliationDetailPage_detailsNotRetrieved"));
        return;
      }
      AffiliationVO affVO = XmlTransformingService.transformToAffiliation(ouXml);
      this.affiliation = new AffiliationVOPresentation(affVO);
    } catch (Exception e) {
      error(getMessage("AffiliationDetailPage_detailsNotRetrieved"));
      throw new RuntimeException("Error getting affiliation details", e);
    }
  }

  public void setAffiliation(AffiliationVOPresentation affiliation) {
    this.affiliation = affiliation;
  }

  public AffiliationVOPresentation getAffiliation() {
    return this.affiliation;
  }
}
