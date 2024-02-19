/*
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

import java.util.ArrayList;
import java.util.List;

/**
 * Object representing a toc
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public class TocDivVO extends ValueObject {

  private List<TocDivVO> tocDivList = new ArrayList<>();

  private List<TocPtrVO> tocPtrList = new ArrayList<>();

  private String id;

  private String label;

  private String orderLabel;

  private int order = -1;

  public List<TocDivVO> getTocDivList() {
    return this.tocDivList;
  }

  public void setTocDivList(List<TocDivVO> tocDivList) {
    this.tocDivList = tocDivList;
  }

  public List<TocPtrVO> getTocPtrList() {
    return this.tocPtrList;
  }

  public void setTocPtrList(List<TocPtrVO> tocPtrList) {
    this.tocPtrList = tocPtrList;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getOrderLabel() {
    return this.orderLabel;
  }

  public void setOrderLabel(String orderLabel) {
    this.orderLabel = orderLabel;
  }

  public int getOrder() {
    return this.order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getVisible() {
    return this.visible;
  }

  public void setVisible(String visible) {
    this.visible = visible;
  }

  private String type;

  private String visible;



}
