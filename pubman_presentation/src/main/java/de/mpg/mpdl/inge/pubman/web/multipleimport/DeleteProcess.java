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

import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog.ErrorLevel;
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
  private final ImportLog log;
  private final String authenticationToken;
  private Connection connection = null;

  /**
   * Constructor taking an {@link ImportLog}. Reopens the log again and checks user data.
   * 
   * @param log The {@link ImportLog} whose items should be deleted
   */
  public DeleteProcess(ImportLog log, String authenticationToken, Connection connection) {
    this.authenticationToken = authenticationToken;
    this.connection = connection;
    this.log = log;

    this.log.reopen(connection);
    this.log.setPercentage(5, connection);
    this.log.startItem("import_process_delete_items", connection);
    this.log.addDetail(ErrorLevel.FINE, "import_process_initialize_delete_process", connection);
    this.log.finishItem(connection);
    this.log.setPercentage(5, connection);
  }

  /**
   * First schedule all imported items for deletion, then delete them.
   */
  @Override
  public void run() {
    try {
      int itemCount = 0;
      for (final ImportLogItem item : this.log.getItems()) {
        if (item.getItemId() != null && !"".equals(item.getItemId())) {
          itemCount++;
          this.log.activateItem(item);
          this.log.addDetail(ErrorLevel.FINE, "import_process_schedule_delete", this.connection);
          this.log.suspendItem(this.connection);
        }
      }

      this.log.setPercentage(10, this.connection);

      int counter = 0;
      for (final ImportLogItem item : this.log.getItems()) {
        if (item.getItemId() != null && !"".equals(item.getItemId())) {
          this.log.activateItem(item);
          this.log.addDetail(ErrorLevel.FINE, "import_process_delete_item", this.connection);
          try {
            ApplicationBean.INSTANCE.getPubItemService().delete(item.getItemId(),
                this.authenticationToken);
            this.log
                .addDetail(ErrorLevel.FINE, "import_process_delete_successful", this.connection);
            this.log
                .addDetail(ErrorLevel.FINE, "import_process_remove_identifier", this.connection);
            item.setItemId(null);
            this.log.finishItem(this.connection);
          } catch (final Exception e) {
            this.log.addDetail(ErrorLevel.WARNING, "import_process_delete_failed", this.connection);
            this.log.addDetail(ErrorLevel.WARNING, e, this.connection);
            this.log.finishItem(this.connection);
          }
          counter++;
          this.log.setPercentage(85 * counter / itemCount + 10, this.connection);
        }
      }

      this.log.startItem("import_process_delete_finished", this.connection);
      this.log.finishItem(this.connection);
      this.log.close(this.connection);
    } finally {
      DbTools.closeConnection(this.connection);
    }
  }
}
