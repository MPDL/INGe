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

package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.pubman.web.search.AdvancedSearchEdit;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.ContextCriterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;

/**
 * context criterion vo for the advanced search.
 */
@SuppressWarnings("serial")
public class ContextCriterionBean extends CriterionBean {
  private List<ContextVO> contexts;
  private ContextCriterion contextCriterionVO;
  private String context;

  public ContextCriterionBean(List<ContextVO> contexts) {
    this(new ContextCriterion());
    this.contexts = contexts;

    if (contexts != null && contexts.size() > 0) {
      this.contextCriterionVO.setSearchString("");
    }
  }

  public ContextCriterionBean(ContextCriterion contextCriterionVO) {
    setContextCriterionVO(contextCriterionVO);
  }

  public String getContext() {
    this.context = FacesTools.getRequest().getParameter("collection");
    if (this.context != null && context.length() > 0) {
      for (ContextVO vo : this.contexts) {
        if (vo.getReference().getObjectId().equals(this.context)) {
          this.contextCriterionVO.setSearchString(vo.getReference().getObjectId());
        }
      }
    }

    return this.context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public String clearCriterion() {
    this.contextCriterionVO.setSearchString("");
    this.contextCriterionVO = new ContextCriterion();

    return null;
  }

  public Criterion getContextCriterionVO() {
    return this.contextCriterionVO;
  }

  public void setContextCriterionVO(ContextCriterion contextCriterionVO) {
    this.contextCriterionVO = contextCriterionVO;
  }

  public List<ContextVO> getContexts() {
    return this.contexts;
  }

  public void setContexts(List<ContextVO> contexts) {
    this.contexts = contexts;
  }

  public Criterion getCriterionVO() {
    return this.contextCriterionVO;
  }

  public String getContextName() throws Exception {
    AdvancedSearchEdit advancedSearchEdit =
        (AdvancedSearchEdit) FacesTools.findBean("AdvancedSearchEdit");

    for (SelectItem contextItem : advancedSearchEdit.getContextCriterionCollection()
        .getContextList()) {
      if (contextItem.getValue().equals(this.contextCriterionVO.getSearchString())) {
        return contextItem.getLabel();
      }
    }

    return null;
  }
}
