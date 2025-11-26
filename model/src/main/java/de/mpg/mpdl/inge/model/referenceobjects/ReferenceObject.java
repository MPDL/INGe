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

package de.mpg.mpdl.inge.model.referenceobjects;

import java.io.Serializable;

/**
 * Root Class of all typed references of ValueObjects.
 *
 * @created 18-Jan-2007 15:42:40
 * @revised by MuJ: 27.08.2007
 * @author Full Access
 * @version $Revision$ $LastChangedDate: 2007-07-09 16:4
 * @updated 04-Sep-2007 11:47:55
 */
@SuppressWarnings("serial")
public abstract class ReferenceObject implements Serializable {
  /**
   * Technical objectId-attribute of corresponding ValueOject.
   */
  private String objectId;

  /**
   * An optional title of the reference
   */
  private String title;

  /**
   * Creates a new instance.
   */
  public ReferenceObject() {
    this.objectId = null;
  }

  /**
   * Copy constructor.
   *
   * @author Thomas Diebaecker
   * @param other The instance to copy.
   */
  public ReferenceObject(ReferenceObject other) {
    this.setObjectId(other.objectId);
    this.title = other.title;
  }

  /**
   * Creates a new instance with the given ID.
   *
   * @param objectId
   */
  public ReferenceObject(String objectId) {
    this.objectId = objectId;
  }

  /**
   * Delivers the technical objectId-attribute of corresponding ValueObject.
   */
  public String getObjectId() {
    return this.objectId;
  }

  /**
   * Sets the technical objectId-attribute of corresponding ValueObject.
   *
   * @param objectId
   */
  public void setObjectId(String objectId) {
    if (objectId != null) {
      this.objectId = objectId;
    }

  }

  /**
   * Helper for JiBX input bindings: allows mapping xlink:href attributes without invoking
   * {@link #setObjectId(String)} with null when the attribute is absent. Default behavior extracts
   * the last path segment from the href (if present) and delegates to {@link #setObjectId(String)}.
   *
   * Subclasses may override if they need specialized behavior (see
   * {@link de.mpg.mpdl.inge.model.referenceobjects.ItemRO}).
   */
  public void setHref(String href) {
    if (href == null) {
      return;
    }
    if (href.contains("/")) {
      href = href.substring(href.lastIndexOf('/') + 1);
    }
    this.setObjectId(href);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.objectId) ? 0 : this.objectId.hashCode());
    result = prime * result + ((null == this.title) ? 0 : this.title.hashCode());
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

    ReferenceObject other = (ReferenceObject) obj;

    if (null == this.objectId) {
      if (null != other.objectId)
        return false;

    } else if (!this.objectId.equals(other.objectId))
      return false;

    if (null == this.title) {
      if (null != other.title)
        return false;
    } else if (!this.title.equals(other.title))
      return false;

    return true;
  }

  /**
   * Delivers the name of the class and the objectId, separated by a colon.
   */
  @Override
  public String toString() {
    return getClass().getName() + ':' + this.objectId;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return this.title;
  }

}
