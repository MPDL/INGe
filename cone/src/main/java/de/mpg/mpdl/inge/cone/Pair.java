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

package de.mpg.mpdl.inge.cone;

import java.io.Serializable;
import java.util.Objects;

/**
 * A key-value pair.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public class Pair<T> implements Describable, Serializable {
  private String key;
  private T value;

  /**
   * Default constructor.
   */
  public Pair() {}

  /**
   * Constructor with fields.
   *
   * @param key The key
   * @param value The value
   */
  public Pair(String key, T value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public T getValue() {
    return this.value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (null == other) {
      return false;
    } else if (!(other instanceof Pair)) {
      return false;
    }
    Pair<?> otherPair = (Pair<?>) other;

    return (Objects.equals(this.key, otherPair.key)) && (Objects.equals(this.value, otherPair.value));
  }


  public int hashCode() {
    return (null == this.key ? 1 : this.key.hashCode()) * (null == this.value ? 1 : this.value.hashCode());
  }

}
