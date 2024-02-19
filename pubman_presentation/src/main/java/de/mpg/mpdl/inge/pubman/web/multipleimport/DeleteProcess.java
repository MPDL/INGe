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

import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;

/**
 * A {@link Thread} that deletes asynchronously all imported items of an import.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class DeleteProcess extends Thread {
  private final ImportLog importLog;
  private final String authenticationToken;
  private Connection connection = null;

  /**
   * Constructor taking an {@link ImportLog}. Reopens the log again and checks user data.
   *
   * @param importLog The {@link ImportLog} whose items should be deleted
   */
  public DeleteProcess(ImportLog importLog, String authenticationToken, Connection connection) {
    this.authenticationToken = authenticationToken;
    this.connection = connection;
    this.importLog = importLog;

    this.importLog.reopen(connection);
    this.importLog.startItem("import_process_delete_items", connection);
    this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_initialize_delete_process", connection);
    this.importLog.finishItem(connection);
    this.importLog.setPercentage(BaseImportLog.PERCENTAGE_DELETE_START, connection);
  }

  /**
   * First schedule all imported items for deletion, then delete them.
   */
  @Override
  public void run() {
    try {
      int itemCount = 0;
      for (final ImportLogItem item : this.importLog.getItems()) {
        if (null != item.getItemId() && !"".equals(item.getItemId())) {
          itemCount++;
          this.importLog.activateItem(item);
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_schedule_delete", this.connection);
          this.importLog.suspendItem(this.connection);
        }
      }

      this.importLog.setPercentage(BaseImportLog.PERCENTAGE_DELETE_SUSPEND, this.connection);

      int counter = 0;
      for (final ImportLogItem item : this.importLog.getItems()) {
        if (null != item.getItemId() && !"".equals(item.getItemId())) {
          this.importLog.activateItem(item);
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_delete_item", this.connection);
          try {
            ApplicationBean.INSTANCE.getPubItemService().delete(item.getItemId(), this.authenticationToken);
            this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_delete_successful", this.connection);
            this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_remove_identifier", this.connection);
            item.setItemId(null);
            this.importLog.finishItem(this.connection);
          } catch (final Exception e) {
            this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING, "import_process_delete_failed", this.connection);
            this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING, e, this.connection);
            this.importLog.finishItem(this.connection);
          }
          counter++;
          this.importLog.setPercentage(BaseImportLog.PERCENTAGE_DELETE_END * counter / itemCount + BaseImportLog.PERCENTAGE_DELETE_SUSPEND,
              this.connection);
        }
      }

      this.importLog.startItem("import_process_delete_finished", this.connection);
      this.importLog.finishItem(this.connection);
      this.importLog.close(this.connection);
    } finally {
      DbTools.closeConnection(this.connection);
    }
  }
}
