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

import de.mpg.mpdl.inge.model.valueobjects.IgnoreForCleanup;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * This class combines an abstract value with an optional language attribute.
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@JsonInclude(value = Include.NON_EMPTY)
public class AbstractVO extends ValueObject implements Cloneable {
  /**
   * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
   * 'de.mpg.mpdl.inge.model.valueobjects.ItemVO; local class incompatible: stream classdesc
   * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286'
   * that occur after JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to
   * be compiled twice for testing (once for the Application Server, once for the local test).
   */
  private static final long serialVersionUID = 1L;

  @IgnoreForCleanup
  private String language;
  private String value;

  /**
   * Creates a new instance with the given value.
   */
  public AbstractVO(String value) {
    super();
    this.value = value;
  }

  /**
   * Creates a new instance.
   */
  public AbstractVO() {
    super();
  }

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
  public String getLanguage() {
    return language;
  }

  /**
   * Delivers the value of the abstract.
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the language of the abstract.
   * 
   * @param newVal newVal
   */
  public void setLanguage(String newVal) {
    language = newVal;
  }

  /**
   * Sets the value of the abstract.
   * 
   * @param newVal newVal
   */
  public void setValue(String newVal) {
    value = (newVal != null ? newVal.replaceAll(System.getProperty("line.separator"), "<br>") : newVal);
    value = (newVal != null ? newVal.replaceAll("\r", "") : newVal);
  }


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() {
    AbstractVO vo = new AbstractVO();
    vo.setLanguage(getLanguage());
    vo.setValue(getValue());
    return vo;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((language == null) ? 0 : language.hashCode());
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

    AbstractVO other = (AbstractVO) obj;

    if (language == null) {
      if (other.language != null)
        return false;
    } else if (!language.equals(other.language))
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
