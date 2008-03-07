/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.pubman;

import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import java.util.List;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;

/**
 * Interface for retrieving of export formats as well as an export file containing
 * and presenting an item list in accordance of a special export format.
 * Revised by StG: 24.08.2007
 * @created 26-Jun-2007 18:14:02
 * @author Galina Stancheva  (initial creation) $Author:$ (last modification)
 * @version 1.0
 * @updated 14-Sep-2007 12:39:00
 */
public interface ItemExporting {

    /**
     * The service name.
     */
    public static final String SERVICE_NAME = "ejb/de/mpg/escidoc/services/pubman/ItemExporting";

    /**
     * Returns a list of ExportFormatsVO representing the existing export formats (layout and structered)
     * as well as their file formats.
     */
    public List<ExportFormatVO> explainExportFormats() throws TechnicalException;

	/**
     * This method provides the formatted output in the specified export format.
     * 
     * @param exportFormat  This ExportFormatVO contains the export format type, name and file format
     *                      according to which the output is created. 
     * @param pubItemVOList      A XML containing the item list for which the output should be prepared.
     *                      Item list should be formatted in accordance with item-list.xsd
 	 */
	public byte[] getOutput(ExportFormatVO exportFormat, java.util.List<PubItemVO> pubItemVOList) throws TechnicalException;

}