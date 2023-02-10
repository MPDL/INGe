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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;

/**
 * Represents the content relation between two items.
 * 
 * @updated 18-Okt-2007 15:42:32
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class RelationVO extends ValueObject {

  /**
   * The type of a relation between two items.
   */
  public enum RelationType
  {
    ISREVISIONOF,
    HASMEMBER
  }

  /**
   * description of the content relation, e. g. the reason for the relation.
   */
  private String description;
  /**
   * The type of the relation, e. g. "isRevisionOf"
   */
  private RelationType type;
  /**
   * The item ref of the item that is the source/start of the relation.
   */
  private ItemRO sourceItemRef;
  /**
   * Describes the reference of the target item.
   */
  private ItemRO targetItemRef;

  /**
   * Default constructor.
   */
  public RelationVO() {

  }

  /**
   * Clone constructor.
   * 
   * @param relation The relation to be cloned.
   */
  public RelationVO(RelationVO relation) {
    this.description = relation.description;
    this.sourceItemRef = relation.sourceItemRef;
    this.targetItemRef = relation.targetItemRef;
    this.type = relation.type;
  }

  /**
   * description of the content relation, e. g. the reason for the relation.
   */
  public String getDescription() {
    return description;
  }


  /**
   * the item ref of the item that is the source/start of the relation
   */
  public ItemRO getSourceItemRef() {
    return sourceItemRef;
  }

  /**
   * Describes the reference of the target item.
   */
  public ItemRO getTargetItemRef() {
    return targetItemRef;
  }

  /**
   * the item ref of the item that is the source/start of the relation
   * 
   * @param newVal
   */
  public void setSourceItemRef(ItemRO newVal) {
    sourceItemRef = newVal;
  }

  /**
   * Describes the reference of the target item.
   * 
   * @param newVal
   */
  public void setTargetItemRef(ItemRO newVal) {
    targetItemRef = newVal;
  }

  /**
   * Description of the content relation, e. g. the reason for the relation.
   * 
   * @param newVal
   */
  public void setDescription(String newVal) {
    description = newVal;
  }

  /**
   * The type of the relation, e. g. "isRevisionOf"
   */
  public RelationType gettype() {
    return type;
  }

  /**
   * The type of the relation, e. g. "isRevisionOf"
   * 
   * @param newVal
   */
  public void settype(RelationType newVal) {
    type = newVal;
  }
}
