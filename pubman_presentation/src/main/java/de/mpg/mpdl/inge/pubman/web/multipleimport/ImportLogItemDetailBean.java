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
import java.util.List;

import jakarta.faces.bean.ManagedBean;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;

/**
 * A JSF bean class to hold data of an import item's details.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "ImportLogItemDetailBean")
@SuppressWarnings("serial")
public class ImportLogItemDetailBean extends FacesBean {
  private int itemId = 0;
  private List<ImportLogItemDetail> importLogItemDetails = null;
  private String userid = null;

  public ImportLogItemDetailBean() {
    final String idString = FacesTools.getExternalContext().getRequestParameterMap().get("id");

    if (idString != null) {
      this.itemId = Integer.parseInt(idString);
    }

    if (this.getLoginHelper().getAccountUser() != null) {
      this.userid = this.getLoginHelper().getAccountUser().getObjectId();
    }
  }

  public int getLength() {
    return this.getDetails().size();
  }

  public List<ImportLogItemDetail> getDetails() {
    if (this.importLogItemDetails == null && this.itemId != 0 && this.userid != null) {
      final Connection connection = DbTools.getNewConnection();
      try {
        this.importLogItemDetails = ImportLog.getImportLogItemDetails(this.itemId, this.userid, connection);
      } finally {
        DbTools.closeConnection(connection);
      }
    }

    return this.importLogItemDetails;
  }
}
