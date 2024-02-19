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

@SuppressWarnings("serial")
public class TocPtrVO extends ValueObject {

  private String id;

  private String loctype = "URL";

  private String use;

  private String mimetype;

  private String linkType;

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLoctype() {
    return this.loctype;
  }

  public void setLoctype(String loctype) {
    this.loctype = loctype;
  }

  public String getUse() {
    return this.use;
  }

  public void setUse(String use) {
    this.use = use;
  }

  public String getMimetype() {
    return this.mimetype;
  }

  public void setMimetype(String mimetype) {
    this.mimetype = mimetype;
  }

  public String getLinkType() {
    return this.linkType;
  }

  public void setLinkType(String linkType) {
    this.linkType = linkType;
  }

  public String getLinkRef() {
    return this.linkRef;
  }

  public void setLinkRef(String linkRef) {
    this.linkRef = linkRef;
  }

  public String getLinkTitle() {
    return this.linkTitle;
  }

  public void setLinkTitle(String linkTitle) {
    this.linkTitle = linkTitle;
  }

  private String linkRef;

  private String linkTitle;
}
