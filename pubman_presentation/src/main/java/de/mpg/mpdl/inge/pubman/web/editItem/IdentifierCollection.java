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
import java.util.Comparator;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;
import de.mpg.mpdl.inge.pubman.web.util.DisplayTools;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import jakarta.faces.model.SelectItem;

@SuppressWarnings("serial")
public class IdentifierCollection extends FacesBean {
  private List<IdentifierVO> parentVO;
  private IdentifierManager identifierManager;

  public IdentifierCollection() {
    // ensure the parentVO is never null;
    this(new ArrayList<>());
  }

  public IdentifierCollection(List<IdentifierVO> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<IdentifierVO> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<IdentifierVO> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.identifierManager = new IdentifierManager(parentVO);
  }

  /**
   * localized creation of SelectItems for the identifier types available
   *
   * @return SelectItem[] with Strings representing identifier types
   */
  public SelectItem[] getIdentifierTypes() {
    final ArrayList<SelectItem> selectItemList = new ArrayList<>();

    // constants for comboBoxes
    selectItemList.add(new SelectItem(null, this.getLabel("EditItem_NO_ITEM_SET")));

    for (final IdentifierVO.IdType type : DisplayTools.getIdTypesToDisplay()) {
      selectItemList.add(new SelectItem(type.toString(), this.getLabel("ENUM_IDENTIFIERTYPE_" + type)));
    }

    // Sort identifiers alphabetically
    selectItemList.sort(Comparator.comparing(o -> o.getLabel().toLowerCase()));

    return selectItemList.toArray(new SelectItem[] {});
  }

  /**
   * Specialized DataModelManager to deal with objects of type IdentifierVO
   *
   * @author Mario Wagner
   */
  public static class IdentifierManager extends DataModelManager<IdentifierVO> {
    List<IdentifierVO> parentVO;

    public IdentifierManager(List<IdentifierVO> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public IdentifierVO createNewObject() {
      final IdentifierVO newIdentifier = new IdentifierVO();
      return newIdentifier;
    }

    public List<IdentifierVO> getDataListFromVO() {
      return this.parentVO;
    }

    public void setParentVO(List<IdentifierVO> parentVO) {
      this.parentVO = parentVO;
      this.setObjectList(parentVO);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }

  public IdentifierManager getIdentifierManager() {
    return this.identifierManager;
  }

  public void setIdentifierManager(IdentifierManager identifierManager) {
    this.identifierManager = identifierManager;
  }
}
