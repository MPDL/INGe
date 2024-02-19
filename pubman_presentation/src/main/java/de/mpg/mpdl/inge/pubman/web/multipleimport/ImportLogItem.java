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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.util.PropertyReader;

public class ImportLogItem extends BaseImportLog {
  private static String link = null;

  private List<ImportLogItemDetail> importLogItemDetails = new ArrayList<>();
  private String itemId;
  private ItemVersionVO itemVO;
  private ImportLog parent;

  public ImportLogItem(ImportLog parent) {
    this.startDate = new Date();
    this.status = BaseImportLog.Status.PENDING;
    this.errorLevel = BaseImportLog.ErrorLevel.FINE;

    this.parent = parent;
  }

  public String getDetailsLink() {
    return "ImportLogItemDetails.jsp?id=" + this.getId();
  }

  public String getItemId() {
    return this.itemId;
  }

  public List<ImportLogItemDetail> getItems() {
    return this.importLogItemDetails;
  }

  public ItemVersionVO getItemVO() {
    return this.itemVO;
  }

  public String getLink() {
    if (null == ImportLogItem.link) {
      try {
        ImportLogItem.link = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
            + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH)
            + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN);
      } catch (final Exception e) {
        throw new RuntimeException(e);
      }
    }

    return ImportLogItem.link.replaceAll("\\$1", this.itemId);
  }

  public BaseImportLog getParent() {
    return this.parent;
  }

  public void setErrorLevel(BaseImportLog.ErrorLevel errorLevel, Connection connection) {
    super.setErrorLevel(errorLevel);

    if (null != this.parent && null != connection) {
      this.parent.setErrorLevel(errorLevel, connection);
    }
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public void setItems(List<ImportLogItemDetail> items) {
    this.importLogItemDetails = items;
  }

  public void setItemVO(ItemVersionVO itemVO) {
    this.itemVO = itemVO;
  }

  public void setParent(ImportLog parent) {
    this.parent = parent;
  }
}
