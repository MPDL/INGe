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
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.IgnoreForCleanup;

/**
 * This class combines an alternative title value with optional language and type attributes.
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class AlternativeTitleVO extends ValueObject implements Cloneable {
  @IgnoreForCleanup
  private String language;

  private String value;
  private String type;

  /**
   * Creates a new instance with the given value.
   */
  public AlternativeTitleVO(String value) {
    this.value = value;
  }

  /**
   * Creates a new instance.
   */
  public AlternativeTitleVO() {}

  /**
   * Creates a new instance with the given value and language.
   *
   * @param value The alternative title value
   * @param language The alternative title language
   */
  public AlternativeTitleVO(String value, String language) {
    this.value = value;
    this.language = language;
  }

  /**
   * Creates a new instance with the given value and language.
   *
   * @param value The text value
   * @param language The text language
   * @param type The type of the text
   */
  public AlternativeTitleVO(String value, String language, String type) {
    this.value = value;
    this.language = language;
    this.type = type;
  }

  /**
   * Delivers the language of the alternative title.
   */
  public String getLanguage() {
    return language;
  }

  /**
   * Delivers the value of the alternative title.
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the language of the alternative title.
   *
   * @param newVal newVal
   */
  public void setLanguage(String newVal) {
    language = newVal;
  }

  /**
   * Sets the value of the alternative title.
   *
   * @param newVal newVal
   */
  public void setValue(String newVal) {
    value = newVal;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  public AlternativeTitleVO clone() {
    try {
      AlternativeTitleVO clone = (AlternativeTitleVO) super.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((language == null) ? 0 : language.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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

    AlternativeTitleVO other = (AlternativeTitleVO) obj;

    if (language == null) {
      if (other.language != null)
        return false;
    } else if (!language.equals(other.language))
      return false;

    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;

    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
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
    return value;
  }
}
