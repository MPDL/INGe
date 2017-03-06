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

package de.mpg.mpdl.inge.pubman;

import java.io.IOException;
import java.util.List;

import de.mpg.mpdl.inge.citationmanager.CitationStyleExecutorService;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO.FormatType;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.structuredexportmanager.StructuredExportManagerException;
import de.mpg.mpdl.inge.structuredexportmanager.StructuredExportService;
import de.mpg.mpdl.inge.structuredexportmanager.StructuredExportXSLTNotFoundException;
import net.sf.jasperreports.engine.JRException;

/**
 * This class provides the ejb implementation of the {@link ItemExporting} interface.
 * 
 * @author Galina Stancheva (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate: 2014-08-20 10:31:05 +0200 (Mi, 20 Aug 2014) $ Revised by
 *          StG: 24.08.2007
 */
public class ItemExportingService {
  /**
   * {@inheritDoc}
   */
  public static byte[] getOutput(ExportFormatVO exportFormat, List<PubItemVO> pubItemVOList)
      throws TechnicalException {
    String itemList = XmlTransformingService.transformToItemList(pubItemVOList);

    byte[] exportData = null;
    try {
      exportData = getOutput(itemList, exportFormat);
    } catch (Exception e) {
      throw new TechnicalException(e);
    }

    return exportData;
  }

  /**
   * Output wrapper for structuredExportHandler.getOutput and citationStyleHandler.getOutput.
   * Parameters should be controlled in the colling methods!
   * 
   * @param exportFormat - export format
   * @param formatType - export format type
   * @param outputFormat - output format type
   * @param itemList - xml item list in item-list.xsd schema
   * @return generated export
   * @throws TechnicalException
   * @throws StructuredExportXSLTNotFoundException
   * @throws StructuredExportManagerException
   * @throws IOException
   * @throws JRException
   * @throws CitationStyleManagerException
   */
  private static byte[] getOutput(String itemList, ExportFormatVO exportFormat)
      throws TechnicalException, StructuredExportXSLTNotFoundException,
      StructuredExportManagerException, IOException, JRException, CitationStyleManagerException {

    byte[] exportData = null;

    if (exportFormat.getFormatType() == FormatType.LAYOUT) {
      exportData = CitationStyleExecutorService.getOutput(itemList, exportFormat);
    } else if (exportFormat.getFormatType() == FormatType.STRUCTURED) {
      exportData = StructuredExportService.getOutput(itemList, exportFormat.getName());
    } else
      throw new TechnicalException("format Type: " + exportFormat.getFormatType()
          + " is not supported");

    return exportData;
  }
}
