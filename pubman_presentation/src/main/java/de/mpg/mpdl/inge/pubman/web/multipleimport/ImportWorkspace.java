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

import java.util.List;

import javax.faces.bean.ManagedBean;

import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog.SortColumn;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog.SortDirection;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;

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
  private ImportLog.SortColumn sortColumn = SortColumn.STARTDATE;
  private ImportLog.SortDirection sortDirection = SortDirection.DESCENDING;

  public ImportWorkspace() {}

  @Override
  public void init() {
    super.init();

    ImportLog.SortColumn currentColumn = null;
    ImportLog.SortDirection currentDirection = null;
    ImportLog.SortColumn newColumn = null;

    final String sortColumnString =
        FacesTools.getExternalContext().getRequestParameterMap().get("sortColumn");
    if (sortColumnString != null && !"".equals(sortColumnString)) {
      newColumn = SortColumn.valueOf(sortColumnString);
    }

    final String currentColumnString =
        FacesTools.getExternalContext().getRequestParameterMap().get("currentColumn");
    if (currentColumnString != null && !"".equals(currentColumnString)) {
      currentColumn = SortColumn.valueOf(currentColumnString);
    }

    final String currentDirectionString =
        FacesTools.getExternalContext().getRequestParameterMap().get("currentDirection");

    if (currentDirectionString != null && !"".equals(currentDirectionString)) {
      currentDirection = SortDirection.valueOf(currentDirectionString);
    }

    if (newColumn != null && newColumn.equals(currentColumn)) {
      this.sortColumn = newColumn;
      if (currentDirection == SortDirection.ASCENDING) {
        this.sortDirection = SortDirection.DESCENDING;
      } else {
        this.sortDirection = SortDirection.ASCENDING;
      }
    } else if (newColumn != null) {
      this.sortColumn = newColumn;
      this.sortDirection = SortDirection.ASCENDING;
    }
  }

  /**
   * @return the imports
   */
  public List<ImportLog> getImports() {
    final AccountUserVO user = this.getLoginHelper().getAccountUser();

    if (user != null) {
      return ImportLog.getImportLogs("import", user, this.sortColumn, this.sortDirection, true,
          false);
    }

    return null;
  }

  /**
   * @return the sortColumn
   */
  public ImportLog.SortColumn getSortColumn() {
    return this.sortColumn;
  }

  /**
   * @param sortColumn the sortColumn to set
   */
  public void setSortColumn(ImportLog.SortColumn sortColumn) {
    this.sortColumn = sortColumn;
  }

  /**
   * @return the sortDirection
   */
  public ImportLog.SortDirection getSortDirection() {
    return this.sortDirection;
  }

  /**
   * @param sortDirection the sortDirection to set
   */
  public void setSortDirection(ImportLog.SortDirection sortDirection) {
    this.sortDirection = sortDirection;
  }

  public String getFormatLabel(ImportLog currentImport) {
    String label = "n/a";

    if (currentImport != null) {
      switch (currentImport.getFormat()) {
        case ARXIV_OAIPMH_XML:
          break;
        case BIBTEX_STRING:
          label = this.getLabel("ENUM_IMPORT_FORMAT_BIBTEX");
          break;
        case BMC_FULLTEXT_HTML:
          break;
        case BMC_FULLTEXT_XML:
          break;
        case BMC_OAIPMH_XML:
          break;
        case BMC_XML:
          label = this.getLabel("ENUM_IMPORT_FORMAT_BMC");
          break;
        case COINS_STRING:
          break;
        case DC_XML:
          break;
        case DOI_METADATA_XML:
          break;
        case EDOC_XML:
          label = this.getLabel("ENUM_IMPORT_FORMAT_EDOC");
          break;
        case ENDNOTE_STRING:
          label = this.getLabel("ENUM_IMPORT_FORMAT_ENDNOTE");
          break;
        case ENDNOTE_XML:
          label = this.getLabel("ENUM_IMPORT_FORMAT_ENDNOTE");
          break;
        case ESCIDOC_COMPONENT_XML:
          break;
        case ESCIDOC_ITEMLIST_V1_XML:
          break;
        case ESCIDOC_ITEMLIST_V2_XML:
          break;
        case ESCIDOC_ITEMLIST_V3_XML:
          label = this.getLabel("ENUM_IMPORT_FORMAT_ESCIDOC");
          break;
        case ESCIDOC_ITEM_V1_XML:
          break;
        case ESCIDOC_ITEM_V2_XML:
          break;
        case ESCIDOC_ITEM_V3_XML:
          break;
        case ESCIDOC_ITEM_VO:
          break;
        case HTML_METATAGS_DC_XML:
          break;
        case HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML:
          break;
        case JUS_HTML_XML:
          break;
        case JUS_INDESIGN_XML:
          break;
        case JUS_SNIPPET_XML:
          break;
        case MAB_STRING:
          label = this.getLabel("ENUM_IMPORT_FORMAT_MAB");
          break;
        case MAB_XML:
          label = this.getLabel("ENUM_IMPORT_FORMAT_MAB");
          break;
        case MARC_21_STRING:
          label = this.getLabel("ENUM_IMPORT_FORMAT_MARC21");
          break;
        case MARC_XML:
          label = this.getLabel("ENUM_IMPORT_FORMAT_MARCXML");
          break;
        case MODS_XML:
          break;
        case OAI_DC:
          break;
        case PEER_TEI_XML:
          break;
        case PMC_OAIPMH_XML:
          break;
        case RIS_STRING:
          label = this.getLabel("ENUM_IMPORT_FORMAT_RIS");
          break;
        case RIS_XML:
          label = this.getLabel("ENUM_IMPORT_FORMAT_RIS");
          break;
        case SPIRES_XML:
          break;
        case WOS_STRING:
          label = this.getLabel("ENUM_IMPORT_FORMAT_WOS");
          break;
        case WOS_XML:
          label = this.getLabel("ENUM_IMPORT_FORMAT_WOS");
          break;
        case ZFN_TEI_XML:
          label = this.getLabel("ENUM_IMPORT_FORMAT_ZFN");
          break;
        case ZIM_XML:
          break;
        default:
          break;
      }
    }

    return label;
  }

  @Override
  public boolean isItemSpecific() {
    return false;
  }
}
