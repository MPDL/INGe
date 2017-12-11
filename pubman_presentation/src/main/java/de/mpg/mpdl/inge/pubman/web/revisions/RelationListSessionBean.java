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

package de.mpg.mpdl.inge.pubman.web.revisions;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.RelationVOPresentation;

/**
 * Keeps all attributes that are used for the whole session by the RevisionList.
 * 
 * @author: Thomas Diebäcker, created 22.10.2007
 * @version: $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "RelationListSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class RelationListSessionBean extends FacesBean {
  private List<RelationVOPresentation> relationList = null;
  private PubItemVO pubItemVO = null;
  private String revisionDescription = new String();

  public RelationListSessionBean() {}

  public List<RelationVOPresentation> getRelationList() {
    return this.relationList;
  }

  public void setRelationList(List<RelationVOPresentation> relationList) {
    this.relationList = relationList;
  }

  public PubItemVO getPubItemVO() {
    return this.pubItemVO;
  }

  public void setPubItemVO(PubItemVO pubItemVO) {
    // re-init the lists as this is a new PubItem
    this.setRevisionDescription(null);

    this.pubItemVO = pubItemVO;
  }

  public String getRevisionDescription() {
    return this.revisionDescription;
  }

  public void setRevisionDescription(String revisionDescription) {
    this.revisionDescription = revisionDescription;
  }

  public boolean getShowRelations() {
    boolean showRelations = false;
    if (this.relationList != null && this.relationList.size() > 0) {
      showRelations = true;
    }
    return showRelations;
  }
}
