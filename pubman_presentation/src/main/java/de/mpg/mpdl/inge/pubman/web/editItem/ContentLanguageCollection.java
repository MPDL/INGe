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

import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;
import jakarta.faces.model.SelectItem;

/**
 * Bean to handle the ContentLanguageCollection on a single jsp. A ContentLanguageCollection is
 * represented by a List<String>.
 *
 * @author Mario Wagner
 */
public class ContentLanguageCollection {
  private List<String> parentVO;
  private ContentLanguageManager contentLanguageManager;

  public ContentLanguageCollection() {
    // ensure the parentVO is never null;
    this(new ArrayList<>());
  }

  public ContentLanguageCollection(List<String> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<String> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<String> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.contentLanguageManager = new ContentLanguageManager(parentVO);
  }

  public SelectItem[] getLanguageOptions() {
    return CommonUtils.getLanguageOptions();
  }

  /**
   * Specialized DataModelManager to deal with objects of type String
   *
   * @author Mario Wagner
   */
  public class ContentLanguageManager extends DataModelManager<String> {
    List<String> parentVO;

    public ContentLanguageManager(List<String> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public String createNewObject() {
      return "";
    }

    public List<String> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      return this.parentVO;
    }

    public void setParentVO(List<String> parentVO) {
      this.parentVO = parentVO;
      this.setObjectList(parentVO);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }

  public ContentLanguageManager getContentLanguageManager() {
    return this.contentLanguageManager;
  }

  public void setContentLanguageManager(ContentLanguageManager contentLanguageManager) {
    this.contentLanguageManager = contentLanguageManager;
  }
}
