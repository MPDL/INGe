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

import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;

/**
 * Represents one content relation to one item. In contrast to RelationVO (which contains subject,
 * predicate and object), this class only contains predicate and object.
 * <p>
 * This class is a workaround for the relations contained in the ItemVO (therefore it has no source,
 * in contrast to RelationVO).
 *
 * @updated 18-Okt-2007 15:42:32
 */
@SuppressWarnings("serial")
public class ItemRelationVO extends ValueObject implements Cloneable {
  /**
   * description of the content relation, e. g. the reason for the relation.
   */
  private String description;

  /**
   * The type of the relation.
   */
  private String type;

  /**
   * Describes the reference of the target item.
   */
  private ItemRO targetItemRef;

  public ItemRelationVO() {}

  /**
   * @param type
   * @param targetItemRef
   */
  public ItemRelationVO(String type, ItemRO targetItemRef) {
    this.type = type;
    this.targetItemRef = targetItemRef;
  }

  public final ItemRelationVO clone() {
    try {
      ItemRelationVO clone = (ItemRelationVO) super.clone();
      if (null != clone.targetItemRef) {
        clone.targetItemRef = this.targetItemRef.clone();
      }
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * description of the content relation, e. g. the reason for the relation.
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Describes the reference of the target item.
   */
  public ItemRO getTargetItemRef() {
    return this.targetItemRef;
  }

  /**
   * Describes the reference of the target item.
   *
   * @param newVal
   */
  public void setTargetItemRef(ItemRO newVal) {
    this.targetItemRef = newVal;
  }

  /**
   * Description of the content relation, e. g. the reason for the relation.
   *
   * @param newVal
   */
  public void setDescription(String newVal) {
    this.description = newVal;
  }

  /**
   * The type of the relation.
   */
  public String getType() {
    return this.type;
  }

  /**
   * The type of the relation.
   *
   * @param newVal
   */
  public void setType(String newVal) {
    this.type = newVal;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.description) ? 0 : this.description.hashCode());
    result = prime * result + ((null == this.targetItemRef) ? 0 : this.targetItemRef.hashCode());
    result = prime * result + ((null == this.type) ? 0 : this.type.hashCode());
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

    ItemRelationVO other = (ItemRelationVO) obj;

    if (null == this.description) {
      if (null != other.description)
        return false;
    } else if (!this.description.equals(other.description))
      return false;

    if (null == this.targetItemRef) {
      if (null != other.targetItemRef)
        return false;
    } else if (!this.targetItemRef.equals(other.targetItemRef))
      return false;

    if (null == this.type) {
      if (null != other.type)
        return false;
    } else if (!this.type.equals(other.type))
      return false;

    return true;
  }

}
