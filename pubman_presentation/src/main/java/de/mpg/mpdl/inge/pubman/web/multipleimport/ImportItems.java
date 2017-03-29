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
 * A JSF bean class to hold the data of the items of an import.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "ImportItems")
@SuppressWarnings("serial")
public class ImportItems extends FacesBean {
  private static final Logger logger = Logger.getLogger(ImportItems.class);

  private ImportLog log = null;
  private String userid = null;
  private int importId = 0;
  private int itemsPerPage = 0;
  private int page = 0;

  /**
   * Constructor extracting the import's id and the pagination parameters from the URL and setting
   * user settings.
   */
  public ImportItems() {
    final String idString = FacesTools.getExternalContext().getRequestParameterMap().get("id");
    if (idString != null) {
      this.importId = Integer.parseInt(idString);
    }
    final String pageString = FacesTools.getExternalContext().getRequestParameterMap().get("page");
    if (pageString != null) {
      this.page = Integer.parseInt(pageString);
    }
    final String itemsPerPageString =
        FacesTools.getExternalContext().getRequestParameterMap().get("itemsPerPage");
    if (itemsPerPageString != null) {
      this.itemsPerPage = Integer.parseInt(itemsPerPageString);
    }

    if (this.getLoginHelper().getAccountUser() != null) {
      this.userid = this.getLoginHelper().getAccountUser().getReference().getObjectId();
    }
  }

  /**
   * @return The import including the details
   */

  public ImportLog getImport() {
    if (this.log == null && this.userid != null) {
      final Connection conn = ImportLog.getConnection();
      this.log = ImportLog.getImportLog(this.importId, true, false, conn);
      try {
        conn.close();
      } catch (final SQLException e) {
        ImportItems.logger.error("Error closing db connection", e);
      }
    }
    return this.log;
  }

  /**
   * @return the page
   */
  public int getPage() {
    return this.page;
  }

  /**
   * @param page the page to set
   */
  public void setPage(int page) {
    this.page = page;
  }

  /**
   * @return the itemsPerPage
   */
  public int getItemsPerPage() {
    return this.itemsPerPage;
  }

  /**
   * @param itemsPerPage the itemsPerPage to set
   */
  public void setItemsPerPage(int itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
  }

}
