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

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.PubItemService;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog.ErrorLevel;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class SubmitProcess extends Thread {
  private ImportLog log;
  private ItemHandler itemHandler;
  private AccountUserVO user;
  private boolean alsoRelease;

  public SubmitProcess(ImportLog log, boolean alsoRelease) {
    this.log = log;
    this.alsoRelease = alsoRelease;

    this.log.reopen();
    this.log.setPercentage(5);
    this.log.startItem("import_process_submit_items");
    this.log.addDetail(ErrorLevel.FINE, "import_process_initialize_submit_process");

    try {
      user = new AccountUserVO();
      user.setHandle(log.getUserHandle());
      user.setUserid(log.getUser());
      this.itemHandler = ServiceLocator.getItemHandler(this.user.getHandle());
    } catch (Exception e) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_initialize_submit_process_error");
      this.log.addDetail(ErrorLevel.FATAL, e);
      this.log.close();
      throw new RuntimeException(e);
    }

    this.log.finishItem();
    this.log.setPercentage(5);
  }

  public void run() {
    int itemCount = 0;
    for (ImportLogItem item : log.getItems()) {
      if (item.getItemId() != null && !"".equals(item.getItemId())) {
        itemCount++;
        log.activateItem(item);
        log.addDetail(ErrorLevel.FINE, "import_process_schedule_submit");
        log.suspendItem();
      }
    }

    this.log.setPercentage(10);
    int counter = 0;

    for (ImportLogItem item : log.getItems()) {
      if (item.getItemId() != null && !"".equals(item.getItemId())) {
        log.activateItem(item);

        try {
          log.addDetail(ErrorLevel.FINE, "import_process_retrieve_item");

          String itemXml = this.itemHandler.retrieve(item.getItemId());
          PubItemVO pubItemVO = XmlTransformingService.transformToPubItem(itemXml);

          try {
            ItemValidatingService.validateItemObject(pubItemVO, ValidationPoint.STANDARD);
          } catch (ItemInvalidException e) {
            this.log.addDetail(ErrorLevel.WARNING, "import_process_release_validation");
            for (ValidationReportItemVO v : e.getReport().getItems()) {
              this.log.addDetail(ErrorLevel.WARNING, v.getContent());
            }
            throw e;
          }

          if (this.alsoRelease) {
            log.addDetail(ErrorLevel.FINE, "import_process_submit_release_item");
            pubItemVO =
                PubItemService.submitPubItem(pubItemVO,
                    "Batch submit/release from import " + log.getMessage(), user);
            PubItemService.releasePubItem(pubItemVO.getVersion(), pubItemVO.getModificationDate(),
                "Batch submit/release from import " + log.getMessage(), user);
            log.addDetail(ErrorLevel.FINE, "import_process_submit_release_successful");
          } else {
            log.addDetail(ErrorLevel.FINE, "import_process_submit_item");
            PubItemService.submitPubItem(pubItemVO, "Batch submit from import " + log.getMessage(),
                user);
            log.addDetail(ErrorLevel.FINE, "import_process_submit_successful");
          }

          log.finishItem();
        } catch (Exception e) {
          log.addDetail(ErrorLevel.WARNING, "import_process_submit_failed");
          log.addDetail(ErrorLevel.WARNING, e);
          log.finishItem();
        }

        counter++;
        log.setPercentage(85 * counter / itemCount + 10);
      }
    }

    log.startItem("import_process_submit_finished");
    log.finishItem();
    log.close();
  }
}
