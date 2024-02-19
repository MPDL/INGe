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

import de.mpg.mpdl.inge.model.referenceobjects.FileRO;

/**
 * Representation of a search hit.
 *
 * @revised by MuJ: 28.08.2007
 * @updated 05-Sep-2007 10:30:53
 */
@SuppressWarnings("serial")
public class SearchHitVO extends ValueObject {
  /**
   * The possible search hit types.
   *
   * @updated 05-Sep-2007 10:30:53
   */
  public enum SearchHitType
  {
    METADATA,
    FULLTEXT
  }

  /**
   * List of text fragments belonging to this search hit.
   */
  private final java.util.List<TextFragmentVO> textFragmentList = new java.util.ArrayList<>();
  private SearchHitType type;
  /**
   * This FileRO points to the File containing the search hit. When the search hit is of type
   * metadata, this property should not be set.
   */
  private FileRO hitReference;

  /**
   * Delivers the list of text fragments belonging to this search hit.
   */
  public java.util.List<TextFragmentVO> getTextFragmentList() {
    return this.textFragmentList;
  }

  /**
   * Delivers the type of the search hit.
   */
  public SearchHitType getType() {
    return this.type;
  }

  /**
   * Sets the type of the search hit.
   *
   * @param newVal
   */
  public void setType(SearchHitType newVal) {
    this.type = newVal;
  }

  /**
   * Delivers the reference to the file containing the search hit. When the search hit is of type
   * metadata, this property should not be set.
   */
  public FileRO getHitReference() {
    return this.hitReference;
  }

  /**
   * Sets the reference to the file containing the search hit. When the search hit is of type
   * metadata, this property should not be set.
   *
   * @param newVal
   */
  public void setHitReference(FileRO newVal) {
    this.hitReference = newVal;
  }
}
