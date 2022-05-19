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

/**
 * 
 * Bean class for creator persons.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class CreatorDisplay {
  private String formattedDisplay;
  private String portfolioLink;
  private String orcid;

  public String getFormattedDisplay() {
    return this.formattedDisplay;
  }

  public void setFormattedDisplay(String formattedDisplay) {
    this.formattedDisplay = formattedDisplay;
  }

  public String getPortfolioLink() {
    return this.portfolioLink;
  }

  public void setPortfolioLink(String portfolioLink) {
    this.portfolioLink = portfolioLink;
  }

  public String getOrcid() {
    return this.orcid;
  }

  public void setOrcid(String orcid) {
    this.orcid = orcid;
  }
}
