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

package de.mpg.mpdl.inge.model.valueobjects.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * @revised by MuJ: 27.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 12:48:57
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class PartnerVO extends ValueObject implements Cloneable {
  /**
   * The possible partner roles.
   * 
   * @updated 05-Sep-2007 12:48:57
   */
  public enum PartnerRole
  {
    AUTHOR,
    OWNER,
    EDITOR
  }

  private PartnerRole role;
  private OrganizationVO organization;
  private PersonVO person;

  /**
   * Delivers the role in which this creator worked.
   */
  public PartnerRole getRole() {
    return role;
  }

  /**
   * Sets the role in which this creator worked.
   * 
   * @param newVal
   */
  public void setRole(PartnerRole newVal) {
    role = newVal;
  }

  /**
   * Delivers detailed information of the organization.
   */
  public OrganizationVO getOrganization() {
    return organization;
  }

  /**
   * Sets detailed information of the organization.
   * 
   * @param newVal
   */
  public void setOrganization(OrganizationVO newVal) {
    organization = newVal;
  }

  /**
   * Delivers detailed information of the person.
   */
  public PersonVO getPerson() {
    return person;
  }

  /**
   * Sets detailed information of the person.
   * 
   * @param newVal
   */
  public void setPerson(PersonVO newVal) {
    person = newVal;
  }

  public Object clone() {
    PartnerVO vo = new PartnerVO();
    if (getOrganization() != null) {
      vo.setOrganization((OrganizationVO) getOrganization().clone());
    }
    vo.setPerson((PersonVO) getPerson().clone());
    vo.setRole(getRole());
    return vo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof PartnerVO)) {
      return false;
    }
    PartnerVO vo = (PartnerVO) o;
    return equals(getOrganization(), vo.getOrganization()) && equals(getPerson(), vo.getPerson()) && equals(getRole(), vo.getRole());
  }
}
