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

import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog.ErrorLevel;
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
  private final ImportLog log;
  private AccountUserVO user;
  private final boolean alsoRelease;
  private final String authenticationToken;

  public SubmitProcess(ImportLog log, boolean alsoRelease, String authenticationToken) {
    this.log = log;
    this.alsoRelease = alsoRelease;
    this.authenticationToken = authenticationToken;

    this.log.reopen();
    this.log.setPercentage(5);
    this.log.startItem("import_process_submit_items");
    this.log.addDetail(ErrorLevel.FINE, "import_process_initialize_submit_process");

    try {
      this.user = new AccountUserVO();
      this.user.setHandle(log.getUserHandle());
      this.user.setUserid(log.getUser());
    } catch (final Exception e) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_initialize_submit_process_error");
      this.log.addDetail(ErrorLevel.FATAL, e);
      this.log.close();
      throw new RuntimeException(e);
    }

    this.log.finishItem();
    this.log.setPercentage(5);
  }

  @Override
  public void run() {
    int itemCount = 0;
    for (final ImportLogItem item : this.log.getItems()) {
      if (item.getItemId() != null && !"".equals(item.getItemId())) {
        itemCount++;
        this.log.activateItem(item);
        this.log.addDetail(ErrorLevel.FINE, "import_process_schedule_submit");
        this.log.suspendItem();
      }
    }

    this.log.setPercentage(10);
    int counter = 0;

    for (final ImportLogItem item : this.log.getItems()) {
      if (item.getItemId() != null && !"".equals(item.getItemId())) {
        this.log.activateItem(item);

        try {
          // this.log.addDetail(ErrorLevel.FINE, "import_process_retrieve_item");
          //
          // final String itemXml = this.itemHandler.retrieve(item.getItemId());
          // final PubItemVO pubItemVO = XmlTransformingService.transformToPubItem(itemXml);
          //
          // try {
          // ItemValidatingService.validate(pubItemVO, ValidationPoint.STANDARD);
          // } catch (final ItemInvalidException e) {
          // this.log.addDetail(ErrorLevel.WARNING, "import_process_release_validation");
          // for (final ValidationReportItemVO v : e.getReport().getItems()) {
          // this.log.addDetail(ErrorLevel.WARNING, v.getContent());
          // }
          // throw e;
          // }

          final PubItemService pubItemService = ApplicationBean.INSTANCE.getPubItemService();
          if (this.alsoRelease) {
            this.log.addDetail(ErrorLevel.FINE, "import_process_submit_release_item");
            // TODO: andere Methode ohne modificationDate verwenden
            pubItemService.releasePubItem(item.getItemId(), null,
                "Batch submit/release from import " + this.log.getMessage(),
                this.authenticationToken);
            this.log.addDetail(ErrorLevel.FINE, "import_process_submit_release_successful");
          } else {
            this.log.addDetail(ErrorLevel.FINE, "import_process_submit_item");
            // TODO: andere Methode ohne modificationDate verwenden
            pubItemService.submitPubItem(item.getItemId(), null, "Batch submit from import "
                + this.log.getMessage(), this.authenticationToken);
            this.log.addDetail(ErrorLevel.FINE, "import_process_submit_successful");
          }

          this.log.finishItem();
        } catch (final Exception e) {
          this.log.addDetail(ErrorLevel.WARNING, "import_process_submit_failed");
          this.log.addDetail(ErrorLevel.WARNING, e);
          this.log.finishItem();
        }

        counter++;
        this.log.setPercentage(85 * counter / itemCount + 10);
      }
    }

    this.log.startItem("import_process_submit_finished");
    this.log.finishItem();
    this.log.close();
  }
}
