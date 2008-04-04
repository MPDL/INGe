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

package de.mpg.escidoc.services.structuredexportmanager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.mpg.escidoc.services.structuredexportmanager.StructuredExportXSLTNotFoundException;


/**
 * Interface for retrieving an export file containing and presenting an item list
 * in the structured format.
 * @author Galina Stancheva  (initial creation) 
 * @author $Author: vdm $ (last modification)
 * @ $Revision: 63 $
 *  $LastChangedDate: 2007-11-13 15:40:28 +0100 (Tue, 13 Nov 2007) $ 
 */


public interface StructuredExportHandler {

    /**
     * The name to obtain this service.
     */
    String SERVICE_NAME = "ejb/de/mpg/escidoc/services/structuredexportmanager/StructuredExportHandler";

    /**
	 * This method provides XML formatted output of the supported export formats (structured
	 * formats).
	 * The XML formatted output of the citation styles is created in accordance with
	 * the explain_formats.xsd. 
     * @throws IOException 
     * @throws UnsupportedEncodingException 
	 */
    String explainFormats() throws StructuredExportManagerException, IOException;

    /**
	 * This method provides list of structured output formats.
     * @throws IOException 
     * @throws SAXException 
     * @throws UnsupportedEncodingException 
     * @throws ParserConfigurationException 
	 */
    String[] getFormatsList() throws StructuredExportManagerException;
    
    
    /**
	 * Returns data presenting the items in structured format.
	 * 
	 * @param itemList    An item list for which the output should be taken.
	 * Item list should be formatted in accordance with item-list.xsd
	 * Metadata record for each item in the item list should be specified in
	 * accordance with escidoc_publication_profile.xsd
	 */
	
	byte[] getOutput(String itemList, String exportFormat) throws 
		StructuredExportXSLTNotFoundException, StructuredExportManagerException;
	
}
