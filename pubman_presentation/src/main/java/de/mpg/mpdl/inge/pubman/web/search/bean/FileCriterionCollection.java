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

package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.FileCriterion;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;

/**
 * Bean to handle the FileCriterionCollection on a single jsp. A FileCriterionCollection is
 * represented by a List<FileCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class FileCriterionCollection {
  private List<FileCriterion> parentVO;
  private FileCriterionManager fileCriterionManager;

  /**
   * CTOR to create a new ArrayList<FileCriterionVO> starting with one empty new GenreCriterionVO
   */
  public FileCriterionCollection() {
    // ensure the parentVO is never null;
    final List<FileCriterion> ctorList = new ArrayList<FileCriterion>();
    ctorList.add(new FileCriterion());
    this.setParentVO(ctorList);
  }

  /**
   * CTOR to refine or fill a predefined ArrayList<FileCriterionVO>
   * 
   * @param parentVO
   */
  public FileCriterionCollection(List<FileCriterion> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<FileCriterion> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<FileCriterion> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.fileCriterionManager = new FileCriterionManager(parentVO);
  }

  /**
   * Specialized DataModelManager to deal with objects of type FileCriterionBean
   * 
   * @author Mario Wagner
   */
  public class FileCriterionManager extends DataModelManager<FileCriterionBean> {
    List<FileCriterion> parentVO;

    public FileCriterionManager(List<FileCriterion> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public FileCriterionBean createNewObject() {
      final FileCriterion newVO = new FileCriterion();
      // create a new wrapper pojo
      final FileCriterionBean fileCriterionBean = new FileCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      this.parentVO.add(newVO);
      return fileCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      this.parentVO.remove(i);
    }

    public List<FileCriterionBean> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      // we have to wrap all VO's in a nice FileCriterionBean
      final List<FileCriterionBean> beanList = new ArrayList<FileCriterionBean>();
      for (final FileCriterion fileCriterionVO : this.parentVO) {
        beanList.add(new FileCriterionBean(fileCriterionVO));
      }

      return beanList;
    }

    public void setParentVO(List<FileCriterion> parentVO) {
      this.parentVO = parentVO;
      // we have to wrap all VO's into a nice FileCriterionBean
      final List<FileCriterionBean> beanList = new ArrayList<FileCriterionBean>();
      for (final FileCriterion fileCriterionVO : parentVO) {
        beanList.add(new FileCriterionBean(fileCriterionVO));
      }
      this.setObjectList(beanList);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public FileCriterionManager getFileCriterionManager() {
    return this.fileCriterionManager;
  }

  public void setFileCriterionManager(FileCriterionManager fileCriterionManager) {
    this.fileCriterionManager = fileCriterionManager;
  }

  public void clearAllForms() {
    for (final FileCriterionBean gcb : this.fileCriterionManager.getObjectList()) {
      gcb.clearCriterion();
    }
  }

  public List<FileCriterion> getFilledCriterion() {
    final List<FileCriterion> returnList = new ArrayList<FileCriterion>();
    for (final FileCriterion vo : this.parentVO) {
      if (!(vo.getContentCategory() == null && vo.getComponentVisibility() == null
          && vo.getComponentAvailability() == null && vo.getSearchForEmbargoFiles() == false)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }
}
