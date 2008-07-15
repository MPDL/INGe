/*
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

package de.mpg.escidoc.services.exportmanager;

import java.io.IOException;

/**
 * Interface for the wrapping of the export interfaces like StructuredExportHandler 
 * and CitationStyleHandler 
 * in the structured format.
 * @author Vlad Makarenko  (initial creation) 
 * @author $Author: vdm $ (last modification)
 * @ $Revision: 63 $
 *  $LastChangedDate: 2007-11-13 15:40:28 +0100 (Tue, 13 Nov 2007) $ 
 */


public interface ExportHandler {

    /**
     * The name to obtain this service.
     */
    String SERVICE_NAME = "ejb/de/mpg/escidoc/services/exportmanager/ExportHandler";

    /**
	 * This method provides XML formatted output of the supported export formats 
     * @throws IOException 
     * @throws UnsupportedEncodingException 
	 */
    String explainFormatsXML() throws ExportManagerException, IOException;

	/**
	 * Returns data presenting the items in export format.
	 * 
	 * @param exportFormat - export format of the items 
	 * @param outputFormat - file format (e.g. pdf, odt, rtf etc.) to be exported 
	 * @param archiveFormat - archive format for the export bundle. Can be zip, tar.gz, etc.
	 * @param fetchComponents - is the a for the files' (i.e. components) presence in the export archive.
	 * @param itemList - XML String in the appropriate scheme (PibItemList for the moment) which will be processed by export component
	 * @return export as byte[]
	 * @throws ExportManagerException
	 */
	byte[] getOutput(
			String exportFormat,
			String outputFormat,
			String archiveFormat,
			boolean fetchComponents,
			String itemList 
	) throws  
			ExportManagerException;
	
}
