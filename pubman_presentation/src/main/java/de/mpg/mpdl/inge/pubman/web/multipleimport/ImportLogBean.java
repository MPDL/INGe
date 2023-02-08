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

import jakarta.faces.bean.ManagedBean;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;

/**
 * JSF bean class to hold an import's data.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "ImportLogBean")
@SuppressWarnings("serial")
public class ImportLogBean extends FacesBean {
  private int importId = 0;
  private String userid = null;
  private ImportLog importLog = null;

  public ImportLogBean() {
    final String idString = FacesTools.getExternalContext().getRequestParameterMap().get("id");

    if (idString != null) {
      this.importId = Integer.parseInt(idString);
    }

    if (this.getLoginHelper().getAccountUser() != null) {
      this.userid = this.getLoginHelper().getAccountUser().getObjectId();
    }
  }

  public ImportLog getImport() {
    if (this.importLog == null && this.userid != null) {
      final Connection connection = DbTools.getNewConnection();

      try {
        this.importLog = ImportLog.getImportLog(this.importId, false, connection);
        this.importLog.setUser(this.userid);
      } finally {
        DbTools.closeConnection(connection);
      }
    }

    return this.importLog;
  }

  public String getRemove() {
    this.getImport().remove();

    return "";
  }

  public String getDeleteAll() {
    this.getImport().deleteAll();

    return "";
  }

  public String getSubmitAll() {
    this.getImport().submitAll();

    return "";
  }

  public String getReleaseAll() {
    this.getImport().releaseAll();

    return "";
  }

  public String getSubmitAndReleaseAll() {
    this.getImport().submitAndReleaseAll();

    return "";
  }

  public int getImportId() {
    return this.importId;
  }

  public void setImportId(int importId) {
    this.importId = importId;
  }

  public boolean isSimpleWorkflow() {
    return this.getImport().getSimpleWorkflow();
  }

  public boolean isStandardWorkflow() {
    return this.getImport().getStandardWorkflow();
  }
}
