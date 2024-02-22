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

package de.mpg.mpdl.inge.model.valueobjects.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.IgnoreForCleanup;

/**
 * This class combines an abstract value with an optional language attribute.
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class AbstractVO extends ValueObject implements Cloneable {

  @IgnoreForCleanup
  private String language;
  private String value;

  /**
   * Creates a new instance with the given value.
   */
  public AbstractVO(String value) {
    this.value = value;
  }

  /**
   * Creates a new instance.
   */
  public AbstractVO() {}

  /**
   * Creates a new instance with the given value and language.
   *
   * @param value The abstract value
   * @param language The abstract language
   */
  public AbstractVO(String value, String language) {
    this.value = value;
    this.language = language;
  }


  /**
   * Delivers the language of the abstract.
   */
  /**
   * Sets the language of the abstract.
   *
   * @param newVal newVal
   */
  public void setLanguage(String newVal) {
    this.language = newVal;
  }

  public String getLanguage() {
    return this.language;
  }

  /**
   * Sets the value of the abstract.
   *
   * @param newVal newVal
   */
  public void setValue(String newVal) {
    this.value = (null != newVal ? newVal.replaceAll("\r\n", "<br>") : newVal);
  }

  public String getValue() {
    return this.value;
  }


  public final AbstractVO clone() {
    try {
      AbstractVO clone = (AbstractVO) super.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.language) ? 0 : this.language.hashCode());
    result = prime * result + ((null == this.value) ? 0 : this.value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (null == obj)
      return false;

    if (getClass() != obj.getClass())
      return false;

    AbstractVO other = (AbstractVO) obj;

    if (null == this.language) {
      if (null != other.language)
        return false;
    } else if (!this.language.equals(other.language))
      return false;

    if (null == this.value) {
      if (null != other.value)
        return false;
    } else if (!this.value.equals(other.value))
      return false;

    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.value;
  }
}
