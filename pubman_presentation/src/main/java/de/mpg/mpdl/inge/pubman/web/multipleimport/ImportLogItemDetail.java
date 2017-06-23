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

package de.mpg.mpdl.inge.pubman.web.multipleimport;

import java.sql.Connection;
import java.util.Date;
import java.util.MissingResourceException;

import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog.ErrorLevel;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog.Status;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;

public class ImportLogItemDetail {
  private Date startDate;
  private ErrorLevel errorLevel;
  private ImportLogItem parent;
  private Status status;
  private String message;

  public ImportLogItemDetail(ImportLogItem parent, Connection connection) {
    this.setStartDate(new Date());
    this.setStatus(Status.PENDING);
    this.setErrorLevel(ErrorLevel.FINE, connection);

    this.parent = parent;
  }

  public ImportLogItem getParent() {
    return this.parent;
  }

  public void setParent(ImportLogItem parent) {
    this.parent = parent;
  }

  // /**
  // * @return An XML representation of this item
  // */
  // @Override
  // public void toXML(Writer writer) throws Exception {
  // // StringWriter writer = new StringWriter();
  //
  // writer.write("<import-item ");
  // writer.write("status=\"");
  // writer.write(this.getStatus().toString());
  // writer.write("\" error-level=\"");
  // writer.write(this.getErrorLevel().toString());
  // writer.write("\">\n");
  //
  // writer.write("\t<message>");
  // writer.write(this.escape(this.getMessage()));
  // writer.write("</message>\n");
  //
  // if (this.getItemId() != null) {
  // writer.write("\t<escidoc-id>");
  // writer.write(this.getItemId());
  // writer.write("</escidoc-id>\n");
  // }
  //
  //
  // writer.write("\t<start-date>");
  // writer.write(this.getStartDateFormatted());
  // writer.write("</start-date>\n");
  //
  // if (this.getEndDate() != null) {
  // writer.write("\t<end-date>");
  // writer.write(this.getEndDateFormatted());
  // writer.write("</end-date>\n");
  // }
  //
  // writer.write("\t<items>\n");
  // for (final ImportLogItem item : this.getItems()) {
  // item.toXML(writer);
  // }
  // writer.write("\t</items>\n");
  //
  // writer.write("</import-item>\n");
  //
  // }


  /**
   * @return An XML representation of this item
   */
  /*
   * public void toXML(XMLStreamWriter writer) throws XMLStreamException {
   * 
   * 
   * writer.writeStartElement("import-item "); writer.writeAttribute("status",
   * getStatus().toString()); writer.writeAttribute("error-level", getErrorLevel().toString());
   * 
   * 
   * writer.writeStartElement("message"); writer.writeCharacters(getMessage());
   * writer.writeEndElement();
   * 
   * writer.writeStartElement("escidoc-id"); writer.writeCharacters(getItemId());
   * writer.writeEndElement();;
   * 
   * writer.writeStartElement("start-date"); writer.writeCharacters(getStartDateFormatted());
   * writer.writeEndElement();;
   * 
   * if (getEndDate() != null) { writer.writeStartElement("end-date");
   * writer.writeCharacters(getEndDateFormatted()); writer.writeEndElement(); }
   * 
   * writer.writeStartElement("items"); for (ImportLogItem item : getItems()) { item.toXML(writer);
   * } writer.writeEndElement();
   * 
   * writer.writeEndElement(); }
   */

  public Status getStatus() {
    return this.status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public ErrorLevel getErrorLevel() {
    return this.errorLevel;
  }

  public void setErrorLevel(ErrorLevel errorLevel) {
    setErrorLevel(errorLevel, null);
  }

  public void setErrorLevel(ErrorLevel errorLevel, Connection connection) {
    if (this.errorLevel == null
        || errorLevel == ErrorLevel.FATAL
        || (errorLevel == ErrorLevel.ERROR && this.errorLevel != ErrorLevel.FATAL)
        || (errorLevel == ErrorLevel.PROBLEM && this.errorLevel != ErrorLevel.FATAL && this.errorLevel != ErrorLevel.ERROR)
        || (errorLevel == ErrorLevel.WARNING && this.errorLevel != ErrorLevel.FATAL
            && this.errorLevel != ErrorLevel.ERROR && this.errorLevel != ErrorLevel.PROBLEM)) {
      this.errorLevel = errorLevel;
    }

    if (this.parent != null && connection != null) {
      this.parent.setErrorLevel(errorLevel, connection);
    }
  }

  public Date getStartDate() {
    return this.startDate;
  }

  public String getStartDateFormatted() {
    if (this.startDate != null) {
      return ImportLog.DATE_FORMAT.format(this.startDate);
    }

    return "";
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public String getMessage() {
    return this.message;
  }

  public String getLocalizedMessage() {
    try {
      return ((InternationalizationHelper) FacesTools.findBean("InternationalizationHelper"))
          .getMessage(this.getMessage());
    } catch (final MissingResourceException mre) {
      // No message entry for this message, it's probably raw data.
      return this.getMessage();
    }
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getLink() {
    return this.getParent() != null ? this.getParent().getLink() : null;
  }

  public String getItemId() {
    return this.getParent() != null ? this.getParent().getItemId() : null;
  }
}
