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

public class ImportLogItemDetail extends BaseImportLog {
  private ImportLogItem parent;

  public ImportLogItemDetail(ImportLogItem parent) {
    this.startDate = new Date();
    this.status = BaseImportLog.Status.PENDING;
    this.errorLevel = BaseImportLog.ErrorLevel.FINE;

    this.parent = parent;
  }

  public String getItemId() {
    return null != this.getParent() ? this.getParent().getItemId() : null;
  }

  public String getLink() {
    return null != this.getParent() ? this.getParent().getLink() : null;
  }

  public ImportLogItem getParent() {
    return this.parent;
  }

  public void setErrorLevel(BaseImportLog.ErrorLevel errorLevel, Connection connection) {
    super.setErrorLevel(errorLevel);

    if (null != this.parent && null != connection) {
      this.parent.setErrorLevel(errorLevel, connection);
    }
  }

  public void setParent(ImportLogItem parent) {
    this.parent = parent;
  }
}
