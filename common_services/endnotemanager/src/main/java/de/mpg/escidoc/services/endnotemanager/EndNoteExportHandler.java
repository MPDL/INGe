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

package de.mpg.escidoc.services.endnotemanager;

/**
 * Interface for retrieving an export file containing and presenting an item list
 * in the structured format "EndNote".
 * @author Galina Stancheva  (initial creation) 
 * @author $Author: vdm $ (last modification)
 * @ $Revision: 63 $
 *  $LastChangedDate: 2007-11-13 15:40:28 +0100 (Tue, 13 Nov 2007) $ 
 */


public interface EndNoteExportHandler {

    /**
     * The name to obtain this service.
     */
    String SERVICE_NAME = "ejb/de/mpg/escidoc/services/endnotemanager/EndNoteExportHandler";
	
    
    /**
	 * Returns data presenting the items in EndNote format.
	 * EndNote 6 format is available at: http://www.endnote.com/support/ensupport.asp
	 * 
	 * @param itemList    An item list for which the output should be taken.
	 * Item list should be formatted in accordance with item-list.xsd
	 * Metadata record for each item in the item list should be specified in
	 * accordance with escidoc_publication_profile.xsd
	 */
	
	byte[] getOutput(String itemList) throws 
		EndNoteExportXSLTNotFoundException, EndNoteManagerException;
	
}
