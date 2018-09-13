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

@SuppressWarnings("serial")
public class ExportFormatVO extends ValueObject {
  private String format;
  private String citationName;
  private String id;

  public ExportFormatVO(String format, String citationName, String id) {
    this.citationName = citationName;
    this.format = format;
    this.id = id;
  }

  public ExportFormatVO(String format, String citationName) {
    this(format, citationName, null);
  }

  public ExportFormatVO(String format) {
    this(format, null, null);
  }

  public String getCitationName() {
    return this.citationName;
  }

  public void setCitationName(String citationName) {
    this.citationName = citationName;
  }

  public String getFormat() {
    return this.format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "ExportFormatVO [format=" + this.format + ", citationName=" + this.citationName + ", id=" + this.id + " ]";
  }

}
