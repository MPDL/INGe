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

package de.mpg.mpdl.inge.model.valueobjects;

import de.mpg.mpdl.inge.model.valueobjects.interfaces.SearchResultElement;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.Searchable;

/**
 * Representation of an PubItem search result record. This class is used only for JiBX
 * transformations of search results.
 *
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 10:30:52
 */
@SuppressWarnings("serial")
public class SearchResultVO extends ValueObject implements SearchResultElement {
  /**
   * List of hits. Every hit in files contains the file reference and the text fragments of the
   * search hit.
   */
  private final java.util.List<SearchHitVO> searchHitList = new java.util.ArrayList<>();
  private Searchable resultVO;

  private float score;

  public java.util.List<SearchHitVO> getSearchHitList() {
    return this.searchHitList;
  }

  public Searchable getResultVO() {
    return this.resultVO;
  }

  public void setResultVO(Searchable resultVO) {
    this.resultVO = resultVO;
  }

  /**
   * @return the score
   */
  public float getScore() {
    return this.score;
  }

  /**
   * @param score the score to set
   */
  public void setScore(float score) {
    this.score = score;
  }

}
