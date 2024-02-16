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

/**
 * Representation of the text fragment belonging to a search hit. It contains the text fragment
 * itself and a list of hitwords describing the position of the hit words in the text fragment.
 *
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 10:30:53
 */
@SuppressWarnings("serial")
public class TextFragmentVO extends ValueObject {
  /**
   * The text fragment of the search hit.
   */
  private String data;
  /**
   * The list of hitwords.
   */
  private final java.util.List<HitwordVO> hitwordList = new java.util.ArrayList<>();

  /**
   * Delivers the text fragment of the search hit.
   */
  public String getData() {
    return data;
  }

  /**
   * Delivers the list of hitwords.
   */
  public java.util.List<HitwordVO> getHitwordList() {
    return hitwordList;
  }

  /**
   * Sets the text fragment of the search hit.
   *
   * @param newVal newVal
   */
  public void setData(String newVal) {
    data = newVal;
  }
}
