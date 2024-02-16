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

import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchHitVO;

/**
 * @author endres
 *
 */
@SuppressWarnings("serial")
public class PubItemResultVO extends ItemVersionVO {
  /**
   * List of hits. Every hit in files contains the file reference and the text fragments of the
   * search hit.
   */
  private java.util.List<SearchHitVO> searchHitList;
  // = new java.util.ArrayList<SearchHitVO>();

  private float score;

  /**
   * Delivers the list of search hits.
   */
  public List<SearchHitVO> getSearchHitList() {
    return this.searchHitList;
  }

  public PubItemResultVO(ItemVersionVO itemVO, List<SearchHitVO> searchHits, float score) {
    super(itemVO);
    if (!searchHits.isEmpty()) {
      this.searchHitList = new java.util.ArrayList<SearchHitVO>();
      this.searchHitList = searchHits;
      this.score = score;
    }
  }

  public void setScore(float score) {
    this.score = score;
  }

  public float getScore() {
    return this.score;
  }
}
