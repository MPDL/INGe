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

import java.util.Arrays;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemBean;
import jakarta.faces.event.ValueChangeEvent;

/**
 * Presentation wrapper for OrganizationVO.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public class OrganizationVOPresentation extends OrganizationVO {
  private EditItemBean bean;

  public OrganizationVOPresentation() {
    this.setName("");
  }

  public OrganizationVOPresentation(OrganizationVO organizationVO) {
    this.setAddress(organizationVO.getAddress());
    this.setIdentifier(organizationVO.getIdentifier());
    this.setName(organizationVO.getName());
    if (organizationVO.getIdentifierPath() != null) {
      this.setIdentifierPath(Arrays.copyOf(organizationVO.getIdentifierPath(), organizationVO.getIdentifierPath().length));
    }
  }

  /**
   * Adds an organization to the list after this organization.
   *
   * @return Always empty
   */
  public void add() {
    final OrganizationVOPresentation organizationPresentation = new OrganizationVOPresentation();
    organizationPresentation.setBean(this.bean);
    this.bean.getCreatorOrganizations().add(this.getNumber(), organizationPresentation);

    for (final CreatorVOPresentation creator : this.bean.getCreators()) {
      final int[] ous = creator.getOus();
      String newOuNumbers = "";
      for (int i = 0; i < ous.length; i++) {
        if (ous[i] <= this.getNumber() || ous[i] >= this.getList().size()) {
          if (!newOuNumbers.isEmpty()) {
            newOuNumbers += ",";
          }
          newOuNumbers += ous[i];
        } else if (ous[i] > this.getNumber()) {
          if (!newOuNumbers.isEmpty()) {
            newOuNumbers += ",";
          }
          newOuNumbers += (ous[i] + 1);
        }
      }
      creator.setOuNumbers(newOuNumbers);
    }
  }

  /**
   * Removes this organization from the list.
   *
   * @return Always empty
   */
  public void remove() {
    for (final CreatorVOPresentation creator : this.bean.getCreators()) {
      final int[] ous = creator.getOus();
      String newOuNumbers = "";
      for (int i = 0; i < ous.length; i++) {
        if (ous[i] < this.getNumber()) {
          if (!newOuNumbers.isEmpty()) {
            newOuNumbers += ",";
          }
          newOuNumbers += ous[i];
        } else if (ous[i] > this.getNumber()) {
          if (!newOuNumbers.isEmpty()) {
            newOuNumbers += ",";
          }
          newOuNumbers += (ous[i] - 1);
        }
      }
      creator.setOuNumbers(newOuNumbers);
    }
    this.getList().remove(this);
  }

  /**
   * @return the position in the list, starting with 1.
   */
  public int getNumber() {
    for (int i = 0; i < this.getList().size(); i++) {
      if (this.getList().get(i) == this) {
        return i + 1;
      }
    }
    throw new RuntimeException("Organization is not a member of its own list");
  }

  /**
   * @return the list
   */
  public List<OrganizationVOPresentation> getList() {
    return this.bean.getCreatorOrganizations();
  }

  /**
   * @param list the list to set
   */
  public void setBean(EditItemBean bean) {
    this.bean = bean;
  }

  public void nameListener(ValueChangeEvent event) {
    if (event.getNewValue() != event.getOldValue()) {
      this.setName(event.getNewValue().toString());
    }
  }

  public boolean isLast() {
    return (this.equals(this.getList().get(this.getList().size() - 1)));
  }

  public boolean isEmpty() {
    if (this.getAddress() != null && !"".equals(this.getAddress())) {
      return false;
    } else if (this.getName() != null && !"".equals(this.getName())) {
      return false;
    } else {
      return true;
    }
  }

}
