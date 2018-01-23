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
  private FormatType formatType;
  private String name;
  private String id;
  private FileFormatVO.FILE_FORMAT fileFormat;

  public enum FormatType
  {
    LAYOUT,
    STRUCTURED
  }

  public ExportFormatVO(FormatType formatType, String name, String fileFormatName) {
    this.formatType = formatType;
    this.name = name;
    this.fileFormat = FileFormatVO.getFileFormat(fileFormatName);
  }

  public ExportFormatVO(FormatType formatType, String name, String fileFormatName, String id) {
    this.formatType = formatType;
    this.name = name;
    this.fileFormat = FileFormatVO.getFileFormat(fileFormatName);
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public FormatType getFormatType() {
    return this.formatType;
  }

  public FileFormatVO.FILE_FORMAT getFileFormat() {
    return this.fileFormat;
  }

  public void setFileFormat(String fileFormatName) {
    this.fileFormat = FileFormatVO.getFileFormat(fileFormatName);
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "ExportFormatVO [formatType=" + formatType + ", name=" + name + ", id=" + id + ", fileFormat=" + fileFormat + "]";
  }

}
