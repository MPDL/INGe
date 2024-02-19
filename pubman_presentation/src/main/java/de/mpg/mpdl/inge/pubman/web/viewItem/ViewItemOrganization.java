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

package de.mpg.mpdl.inge.pubman.web.viewItem;

import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;

/**
 * ViewItemOrganization.java stores information about an organization that is affiliated to a
 * creator. This information will be presented in a popup window Created on 20. Februar 2007, 12:17
 *
 * @author: Tobias Schraut
 * @version: $Revision$ $LastChangedDate$ Revised by ScT: 20.08.2007
 */
public class ViewItemOrganization {
  // the position of the organization within the list
  private String position;
  // The Name of the organization
  private String organizationName;
  // the address of the organization
  private String organizationAddress;
  // the identifier of the organization
  private String organizationIdentifier;
  // the html code of the organization info page popup window
  private String organizationInfoPage;
  /** the link to the corresponding AffiliationDetailPage */
  private String organizationDescription;

  /** the display in View Item pages */

  public String getOrganizationAddress() {
    return this.organizationAddress;
  }

  public void setOrganizationAddress(String organizationAddress) {
    this.organizationAddress = organizationAddress;
  }

  public String getOrganizationName() {
    return this.organizationName;
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  public String getPosition() {
    return this.position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getOrganizationIdentifier() {
    return this.organizationIdentifier;
  }

  public void setOrganizationIdentifier(String organizationIdentifier) {
    this.organizationIdentifier = organizationIdentifier;
  }

  public String getOrganizationInfoPage() {
    return this.organizationInfoPage;
  }

  // the information page (popup window)
  public void setOrganizationInfoPage(String organizationName, String organizationAddress) {
    String addr = "";
    if (organizationAddress != null) {
      addr = organizationAddress;
    }
    this.organizationInfoPage =
        "'<html><head><title>Organisationp</title></head><body scroll=no bgcolor=#FFFFFC><br/><p style=font-family:verdana,arial;font-size:12px>"
            + CommonUtils.htmlEscape(organizationName) + "</p><p style=font-family:verdana,arial;font-size:12px>"
            + CommonUtils.htmlEscape(addr) + "</p></body></html>'";
  }

  public boolean getHasOrganizationalIdentifier() {
    if (this.organizationIdentifier == null || this.organizationIdentifier.isEmpty()) {
      return false;
    }

    return true;
  }

  public void setOrganizationDescription(String organizationName, String organizationAddress, String organizationIdentifier) {
    String addr = organizationName;
    if (organizationAddress != null && !organizationAddress.equals("")) {
      addr = addr + ", " + organizationAddress;
    }

    if (organizationIdentifier != null && !organizationIdentifier.equals("")) {
      addr = addr + ", " + organizationIdentifier;
    }

    this.organizationDescription = addr;
  }

  public String getOrganizationDescription() {
    return this.organizationDescription;
  }
}
