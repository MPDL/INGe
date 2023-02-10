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

package de.mpg.mpdl.inge.model.valueobjects;

import de.mpg.mpdl.inge.model.valueobjects.interfaces.SearchResultElement;

/**
 * This is a container with an extra search result in it.
 * 
 * @author endres
 * 
 */
@SuppressWarnings("serial")
public class AffiliationResultVO extends AffiliationVO implements SearchResultElement {

  /**
   * List of hits. Every hit in files contains the file reference and the text fragments of the
   * search hit.
   */
  private java.util.List<SearchHitVO> searchHitList = new java.util.ArrayList<SearchHitVO>();

  private float score;

  /**
   * Construct an ItemResultVO using the parents copy constructor.
   */
  public AffiliationResultVO(AffiliationVO affiliationVO) {
    super(affiliationVO);
  }

  /**
   * Delivers the list of search hits.
   */
  public java.util.List<SearchHitVO> getSearchHitList() {
    return searchHitList;
  }

  public float getScore() {
    return score;
  }

  public void setScore(float score) {
    this.score = score;
  }
}
