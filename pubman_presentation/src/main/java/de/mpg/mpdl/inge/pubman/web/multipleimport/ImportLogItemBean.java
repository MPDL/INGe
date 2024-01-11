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

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import jakarta.faces.bean.ManagedBean;

/**
 * A JSF bean class to hold the data of the items of an import.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "ImportLogItemBean")
@SuppressWarnings("serial")
public class ImportLogItemBean extends FacesBean {
  private ImportLog importLog = null;
  private String userid = null;
  private int importId = 0;
  private int itemsPerPage = 0;
  private int page = 0;

  public ImportLogItemBean() {
    final String idString = FacesTools.getExternalContext().getRequestParameterMap().get("id");
    if (idString != null) {
      this.importId = Integer.parseInt(idString);
    }

    final String pageString = FacesTools.getExternalContext().getRequestParameterMap().get("page");
    if (pageString != null) {
      this.page = Integer.parseInt(pageString);
    }

    final String itemsPerPageString = FacesTools.getExternalContext().getRequestParameterMap().get("itemsPerPage");
    if (itemsPerPageString != null) {
      this.itemsPerPage = Integer.parseInt(itemsPerPageString);
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
      } finally {
        DbTools.closeConnection(connection);
      }
    }

    return this.importLog;
  }

  public int getPage() {
    return this.page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getItemsPerPage() {
    return this.itemsPerPage;
  }

  public void setItemsPerPage(int itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
  }
}
