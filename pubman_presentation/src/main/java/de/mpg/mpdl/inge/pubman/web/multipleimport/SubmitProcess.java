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

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.service.pubman.PubItemService;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class SubmitProcess extends Thread {
  public enum Modus
  {
    SUBMIT,
    SUBMIT_AND_RELEASE,
    RELEASE
  };

  private final ImportLog importLog;
  private final String authenticationToken;
  private Connection connection = null;
  private Modus modus = null;

  public SubmitProcess(ImportLog importLog, Modus modus, String authenticationToken, Connection connection) {
    this.importLog = importLog;
    this.modus = modus;
    this.authenticationToken = authenticationToken;
    this.connection = connection;

    try {
      this.importLog.reopen(connection);
      switch (modus) {
        case SUBMIT:
          this.importLog.startItem("import_process_submit_items", connection);
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_initialize_submit_process", connection);
          break;
        case SUBMIT_AND_RELEASE:
          this.importLog.startItem("import_process_submit_release_items", connection);
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_initialize_submit_release_process", connection);
          break;
        case RELEASE:
          this.importLog.startItem("import_process_release_items", connection);
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_initialize_release_process", connection);
          break;
      }
    } catch (final Exception e) {
      switch (modus) {
        case SUBMIT:
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FATAL, "import_process_initialize_submit_process_error", connection);
          break;
        case SUBMIT_AND_RELEASE:
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FATAL, "import_process_initialize_submitAndRelease_process_error", connection);
          break;
        case RELEASE:
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FATAL, "import_process_initialize_release_process_error", connection);
          break;
      }
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FATAL, e, connection);
      this.importLog.close(connection);
      throw new RuntimeException(e);
    }

    this.importLog.finishItem(connection);
    this.importLog.setPercentage(BaseImportLog.PERCENTAGE_SUBMIT_START, connection);
  }

  @Override
  public void run() {
    try {
      int itemCount = 0;
      for (final ImportLogItem item : this.importLog.getItems()) {
        if (item.getItemId() != null && !"".equals(item.getItemId())) {
          itemCount++;
          this.importLog.activateItem(item);
          switch (modus) {
            case SUBMIT:
              this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_schedule_submit", this.connection);
              break;
            case SUBMIT_AND_RELEASE:
              this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_schedule_submitAndRelease", this.connection);
              break;
            case RELEASE:
              this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_schedule_release", this.connection);
              break;
          }
          this.importLog.suspendItem(this.connection);
        }
      }

      this.importLog.setPercentage(BaseImportLog.PERCENTAGE_SUBMIT_SUSPEND, this.connection);
      int counter = 0;

      for (final ImportLogItem item : this.importLog.getItems()) {
        if (item.getItemId() != null && !"".equals(item.getItemId())) {
          this.importLog.activateItem(item);

          try {
            final PubItemService pubItemService = ApplicationBean.INSTANCE.getPubItemService();
            final ItemVersionVO itemVersionVO = pubItemService.get(item.getItemId(), this.authenticationToken);
            switch (modus) {
              case SUBMIT:
                this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_submit_item", this.connection);
                pubItemService.submitPubItem(item.getItemId(), itemVersionVO.getModificationDate(),
                    "Batch submit from import " + this.importLog.getMessage(), this.authenticationToken);
                this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_submit_successful", this.connection);
                break;
              case SUBMIT_AND_RELEASE:
                this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_submit_release_item", this.connection);
                pubItemService.submitPubItem(item.getItemId(), itemVersionVO.getModificationDate(),
                    "Batch submit (and release) from import " + this.importLog.getMessage(), this.authenticationToken);
                ItemVersionVO itemVersionVO_ = pubItemService.get(item.getItemId(), this.authenticationToken);
                pubItemService.releasePubItem(item.getItemId(), itemVersionVO_.getModificationDate(),
                    "Batch (submit and) release from import " + this.importLog.getMessage(), this.authenticationToken);
                this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_submit_release_successful", this.connection);
                break;
              case RELEASE:
                this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_release_item", this.connection);
                pubItemService.releasePubItem(item.getItemId(), itemVersionVO.getModificationDate(),
                    "Batch release from import " + this.importLog.getMessage(), this.authenticationToken);
                this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_release_successful", this.connection);
                break;
            }
            this.importLog.finishItem(this.connection);
          } catch (final Exception e) {
            switch (modus) {
              case SUBMIT:
                this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING, "import_process_submit_failed", this.connection);
                break;
              case SUBMIT_AND_RELEASE:
                this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING, "import_process_submit_release_failed", this.connection);
                break;
              case RELEASE:
                this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING, "import_process_release_failed", this.connection);
                break;
            }
            this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING, e, this.connection);
            this.importLog.finishItem(this.connection);
          }

          counter++;
          this.importLog.setPercentage(BaseImportLog.PERCENTAGE_SUBMIT_END * counter / itemCount + BaseImportLog.PERCENTAGE_SUBMIT_SUSPEND,
              this.connection);
        }
      }

      switch (modus) {
        case SUBMIT:
          this.importLog.startItem("import_process_submit_finished", this.connection);
          break;
        case SUBMIT_AND_RELEASE:
          this.importLog.startItem("import_process_submit_release_finished", this.connection);
          break;
        case RELEASE:
          this.importLog.startItem("import_process_release_finished", this.connection);
          break;
      }
      this.importLog.finishItem(this.connection);
      this.importLog.close(this.connection);
    } finally {
      DbTools.closeConnection(this.connection);
    }
  }
}
