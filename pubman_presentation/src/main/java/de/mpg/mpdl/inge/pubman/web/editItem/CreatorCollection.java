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

package de.mpg.mpdl.inge.pubman.web.editItem;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;

/**
 * Bean to handle the CreatorCollection on a single jsp. A CreatorCollection is represented by a
 * List&lt;CreatorVO>.
 *
 * @author Mario Wagner
 */
public class CreatorCollection {
  private List<CreatorVO> parentVO;
  private CreatorManager creatorManager;

  public CreatorCollection(List<CreatorVO> list) {
    this.creatorManager = new CreatorManager(list);
  }

  /**
   * Specialized DataModelManager to deal with objects of type CreatorBean
   *
   * @author Mario Wagner
   */
  public class CreatorManager extends DataModelManager<CreatorBean> {

    public CreatorManager(List<CreatorVO> list) {
      this.objectList = new ArrayList<>();
      for (final CreatorVO creatorVO : list) {
        final CreatorBean creatorBean = new CreatorBean(creatorVO);
        this.objectList.add(creatorBean);
      }
    }

    @Override
    public CreatorBean createNewObject() {
      final CreatorVO newVO = new CreatorVO();
      newVO.setPerson(new PersonVO());
      // create a new Organization for this person
      final OrganizationVO newPersonOrganization = new OrganizationVO();

      newPersonOrganization.setName("");
      newVO.getPerson().getOrganizations().add(newPersonOrganization);

      final CreatorBean creatorBean = new CreatorBean(newVO);

      return creatorBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      CreatorCollection.this.parentVO.remove(i);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public CreatorManager getCreatorManager() {
    return this.creatorManager;
  }

  public void setCreatorManager(CreatorManager creatorManager) {
    this.creatorManager = creatorManager;
  }
}
