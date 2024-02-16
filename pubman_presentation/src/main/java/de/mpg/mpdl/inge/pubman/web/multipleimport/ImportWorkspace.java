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

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import jakarta.faces.bean.ManagedBean;

/**
 * JSF bean class (request) to hold data for the import workspace.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@ManagedBean(name = "ImportWorkspace")
@SuppressWarnings("serial")
public class ImportWorkspace extends BreadcrumbPage {
  /**
   * enum defining possible sorting columns.
   */
  public enum SortColumn
  {
    STARTDATE, ENDDATE, NAME, FORMAT, STATUS, ERRORLEVEL;

  /**
   * @return A representation of the element that is used for storing in a database
   */
  public String toSQL() {
    return super.toString().toLowerCase();
  }}

  /**
   * enum defining sorting directions.
   *
   */
  public enum SortDirection{ASCENDING,DESCENDING;

  /**
   * @return A representation of the element that is used for storing in a database
   */
  public String toSQL() {
    final String value = super.toString();
    return value.replace("ENDING", "").toLowerCase();
  }}

  private ImportWorkspace.SortColumn sortColumn = ImportWorkspace.SortColumn.STARTDATE;
  private ImportWorkspace.SortDirection sortDirection = ImportWorkspace.SortDirection.DESCENDING;

  public ImportWorkspace() {}

  @Override
  public void init() {
    super.init();

    ImportWorkspace.SortColumn currentColumn = null;
    ImportWorkspace.SortDirection currentDirection = null;
    ImportWorkspace.SortColumn newColumn = null;

    final String sortColumnString = FacesTools.getExternalContext().getRequestParameterMap().get("sortColumn");
    if (sortColumnString != null && !sortColumnString.isEmpty()) {
      newColumn = ImportWorkspace.SortColumn.valueOf(sortColumnString);
    }

    final String currentColumnString = FacesTools.getExternalContext().getRequestParameterMap().get("currentColumn");
    if (currentColumnString != null && !currentColumnString.isEmpty()) {
      currentColumn = ImportWorkspace.SortColumn.valueOf(currentColumnString);
    }

    final String currentDirectionString = FacesTools.getExternalContext().getRequestParameterMap().get("currentDirection");

    if (currentDirectionString != null && !currentDirectionString.isEmpty()) {
      currentDirection = ImportWorkspace.SortDirection.valueOf(currentDirectionString);
    }

    if (newColumn != null && newColumn.equals(currentColumn)) {
      this.sortColumn = newColumn;
      if (currentDirection == ImportWorkspace.SortDirection.ASCENDING) {
        this.sortDirection = ImportWorkspace.SortDirection.DESCENDING;
      } else {
        this.sortDirection = ImportWorkspace.SortDirection.ASCENDING;
      }
    } else if (newColumn != null) {
      this.sortColumn = newColumn;
      this.sortDirection = ImportWorkspace.SortDirection.ASCENDING;
    }
  }

  public List<ImportLog> getImports() {
    final AccountUserDbVO user = this.getLoginHelper().getAccountUser();

    if (user != null) {
      final Connection connection = DbTools.getNewConnection();
      try {
        return ImportLog.getImportLogs(user, this.sortColumn, this.sortDirection, false, connection);
      } finally {
        DbTools.closeConnection(connection);
      }
    }

    return null;
  }

  public ImportWorkspace.SortColumn getSortColumn() {
    return this.sortColumn;
  }

  public void setSortColumn(ImportWorkspace.SortColumn sortColumn) {
    this.sortColumn = sortColumn;
  }

  public ImportWorkspace.SortDirection getSortDirection() {
    return this.sortDirection;
  }

  public void setSortDirection(ImportWorkspace.SortDirection sortDirection) {
    this.sortDirection = sortDirection;
  }

  public String getFormatLabel(ImportLog currentImport) {
    String label = "n/a";

    if (currentImport != null && currentImport.getFormat() != null) {
      label = currentImport.getFormat().getName();
      //      switch (currentImport.getFormat()) {
      //        case ARXIV_OAIPMH_XML:
      //          break;
      //        case BIBTEX_STRING:
      //          label = this.getLabel("ENUM_IMPORT_FORMAT_BIBTEX");
      //          break;
      //        case BMC_FULLTEXT_HTML:
      //          break;
      //        case BMC_FULLTEXT_XML:
      //          break;
      //        case BMC_OAIPMH_XML:
      //          break;
      //        case BMC_XML:
      //          label = this.getLabel("ENUM_IMPORT_FORMAT_BMC");
      //          break;
      //        case COINS_STRING:
      //          break;
      //        case DC_XML:
      //          break;
      //        case DOI_METADATA_XML:
      //          break;
      //        case EDOC_XML:
      //          label = this.getLabel("ENUM_IMPORT_FORMAT_EDOC");
      //          break;
      //        case ENDNOTE_STRING:
      //          label = this.getLabel("ENUM_IMPORT_FORMAT_ENDNOTE");
      //          break;
      //        case ENDNOTE_XML:
      //          break;
      //        case ESCIDOC_COMPONENT_XML:
      //          break;
      //        case ESCIDOC_ITEMLIST_V1_XML:
      //          break;
      //        case ESCIDOC_ITEMLIST_V2_XML:
      //          break;
      //        case ESCIDOC_ITEMLIST_V3_XML:
      //          break;
      //        case ESCIDOC_ITEM_V1_XML:
      //          break;
      //        case ESCIDOC_ITEM_V2_XML:
      //          break;
      //        case ESCIDOC_ITEM_V3_XML:
      //          label = this.getLabel("ENUM_IMPORT_FORMAT_ESCIDOC");
      //          break;
      //        case ESCIDOC_ITEM_VO:
      //          break;
      //        case HTML_METATAGS_DC_XML:
      //          break;
      //        case HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML:
      //          break;
      //        case JUS_HTML_XML:
      //          break;
      //        case JUS_INDESIGN_XML:
      //          break;
      //        case JUS_SNIPPET_XML:
      //          break;
      //        case MAB_STRING:
      //          label = this.getLabel("ENUM_IMPORT_FORMAT_MAB");
      //          break;
      //        case MAB_XML:
      //          break;
      //        case MARC_21_STRING:
      //          label = this.getLabel("ENUM_IMPORT_FORMAT_MARC21");
      //          break;
      //        case MARC_XML:
      //          label = this.getLabel("ENUM_IMPORT_FORMAT_MARCXML");
      //          break;
      //        case MODS_XML:
      //          break;
      //        case OAI_DC:
      //          break;
      //        case PEER_TEI_XML:
      //          break;
      //        case PMC_OAIPMH_XML:
      //          break;
      //        case RIS_STRING:
      //          label = this.getLabel("ENUM_IMPORT_FORMAT_RIS");
      //          break;
      //        case RIS_XML:
      //          break;
      //        case SPIRES_XML:
      //          break;
      //        case WOS_STRING:
      //          label = this.getLabel("ENUM_IMPORT_FORMAT_WOS");
      //          break;
      //        case WOS_XML:
      //          break;
      //        case ZFN_TEI_XML:
      //          label = this.getLabel("ENUM_IMPORT_FORMAT_ZFN");
      //          break;
      //        case ZIM_XML:
      //          break;
      //        default:
      //          break;
      //      }
    }

    return label;
  }

  @Override
  public boolean isItemSpecific() {
    return false;
  }
}
