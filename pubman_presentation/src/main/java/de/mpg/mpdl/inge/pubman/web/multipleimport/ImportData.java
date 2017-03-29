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
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

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
@ManagedBean(name = "ImportData")
@SuppressWarnings("serial")
public class ImportData extends FacesBean {
  private static final Logger logger = Logger.getLogger(ImportData.class);

  private int importId = 0;
  private String userid = null;
  private String userHandle = null;
  private ImportLog log = null;

  /**
   * Constructor extracting the import's id from the URL and setting user settings.
   */
  public ImportData() {
    final String idString = FacesTools.getExternalContext().getRequestParameterMap().get("id");

    if (idString != null) {
      this.importId = Integer.parseInt(idString);
    }

    if (this.getLoginHelper().getAccountUser() != null
        && this.getLoginHelper().getAccountUser().getReference() != null) {
      this.userid = this.getLoginHelper().getAccountUser().getReference().getObjectId();
      this.userHandle = this.getLoginHelper().getAccountUser().getHandle();
    }
  }

  /**
   * Getter.
   * 
   * @return the import
   */
  public ImportLog getImport() {

    if (this.log == null && this.userid != null) {
      final Connection conn = ImportLog.getConnection();
      this.log = ImportLog.getImportLog(this.importId, false, false, conn);
      this.log.setUser(this.userid);
      this.log.setUserHandle(this.userHandle);

      try {
        conn.close();
      } catch (final SQLException e) {
        ImportData.logger.error("Error closing db connection", e);
      }
    }

    return this.log;
  }

  public void getRemove() {
    this.getImport().remove();
  }

  public void getDelete() {
    this.getImport().deleteAll();
  }

  public void getSubmit() {
    this.getImport().submitAll();
  }

  public void getRelease() {
    this.getImport().submitAndReleaseAll();
  }

  public int getImportId() {
    return this.importId;
  }

  public void setImportId(int importId) {
    this.importId = importId;
  }

  public boolean isSimpleWorkflow() {
    System.out.println("WF: " + this.getImport().getSimpleWorkflow());
    return this.getImport().getSimpleWorkflow();
  }
}
