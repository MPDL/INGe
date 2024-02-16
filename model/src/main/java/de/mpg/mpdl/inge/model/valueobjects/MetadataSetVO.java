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
 * The super class for the different metadata sets for the different types of items (Publication,
 * Transcription, etc.)
 *
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 11:10:10
 */
@SuppressWarnings("serial")
public class MetadataSetVO extends ValueObject implements Cloneable {
  /**
   * The title of the item.
   */
  private String title;



  /**
   * Creates a new instance.
   */
  public MetadataSetVO() {}

  /**
   * Title constructor.
   *
   * @param other
   */
  public MetadataSetVO(String title) {
    setTitle(title);
  }

  /**
   * Delivers the title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title.
   *
   * @param newVal newVal
   */
  public void setTitle(String newVal) {
    title = newVal;
  }

  public MetadataSetVO clone() {
    try {
      MetadataSetVO clone = (MetadataSetVO) super.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (obj == null)
      return false;

    if (getClass() != obj.getClass())
      return false;

    MetadataSetVO other = (MetadataSetVO) obj;

    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;

    return true;
  }

}
