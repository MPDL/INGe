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

import java.util.List;

/**
 * Valueobject representing the export format data needed for the export.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 10:42:30
 */
@SuppressWarnings("serial")
public class ExportFormatVO extends ValueObject {
  /**
   * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
   * 'de.mpg.mpdl.inge.model.valueobjects.ItemVO; local class incompatible: stream classdesc
   * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286'
   * that occur after JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to
   * be compiled twice for testing (once for the Application Server, once for the local test).
   * 
   * @author Johannes Mueller
   */

  private List<FileFormatVO> fileFormats;
  private FormatType formatType;
  private String name;
  private List<String> creators;
  private String description;
  private String id;
  private FileFormatVO outputFormat;

  /**
   * The possible export format types.
   * 
   * @version $Revision$ $LastChangedDate$ by $Author$
   * @updated 05-Sep-2007 10:42:30
   */
  public enum FormatType
  {
    LAYOUT,
    STRUCTURED
  }

  public ExportFormatVO() {}

  public ExportFormatVO(FormatType formatType, String name, String outputFormat) {
    this.formatType = formatType;
    this.name = name;
    this.outputFormat = new FileFormatVO();
    this.outputFormat.setName(outputFormat);
    this.outputFormat.setMimeType(FileFormatVO.getMimeTypeByName(outputFormat));
  }

  public ExportFormatVO(FormatType formatType, String name, String outputFormat, String id) {
    this.formatType = formatType;
    this.name = name;
    this.outputFormat = new FileFormatVO();
    this.outputFormat.setName(outputFormat);
    this.outputFormat.setMimeType(FileFormatVO.getMimeTypeByName(outputFormat));
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String newVal) {
    this.name = newVal;
  }

  public List<FileFormatVO> getFileFormats() {
    return this.fileFormats;
  }

  public FormatType getFormatType() {
    return this.formatType;
  }

  public void setFormatType(FormatType newVal) {
    this.formatType = newVal;
  }

  public void setOutputFormat(FileFormatVO newVal) {
    this.outputFormat = newVal;
  }

  public FileFormatVO getOutputFormat() {
    return this.outputFormat;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<String> getCreators() {
    return creators;
  }

  public void setCreators(List<String> creators) {
    this.creators = creators;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "ExportFormatVO [fileFormats=" + fileFormats + ", formatType=" + formatType + ", name=" + name + ", id=" + id
        + ", selectedFileFormat=" + outputFormat + "]";
  }

}
