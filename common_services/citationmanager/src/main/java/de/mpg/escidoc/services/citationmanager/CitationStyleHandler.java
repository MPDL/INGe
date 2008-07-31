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

package de.mpg.escidoc.services.citationmanager;

import java.io.IOException;

import net.sf.jasperreports.engine.JRException;

/**
* Interface for retrieving an export file containing and presenting an item list
* in accordance of the special layout format defined by the citation style.  At
* present CitationStyle handler provides only the following operations:
* explainStyles and getOutput. Further releases may add some methods for
* management i.e. create/update/retrieve of particular citation style definitions.
* 
* Revised by StG: 24.08.2007
* @created 26-Jun-2007 18:14:02
* @author Galina Stancheva  (initial creation) $Author$ (last modification)
* @version 1.0, $Revision$
* @updated 13-Sep-2007 18:09:40, $LastChangedDate$ 
*
**/

public interface CitationStyleHandler {
    /**
     * The name to obtain this service.
     */
    String SERVICE_NAME = "ejb/de/mpg/escidoc/services/citationmanager/CitationStyleHandler";
	
  /**
	 * This method provides XML formatted output of the citation styles (layout
	 * formats) with their supported output file formats.
	 * The XML formatted output of the citation styles is created in accordance with
	 * the explain_styles.xsd. 
	 */
    String explainStyles() throws IllegalArgumentException, IOException, CitationStyleManagerException;

    /**
     * This method returns <code>true</code> if citationStyle is in the list of the 
     * citation styles, <code>false</code> otherwise.
     * @throws StructuredExportManagerException 
     */
    boolean isCitationStyle(String citationStyle) throws CitationStyleManagerException;
    
	/**
	 * This method provides the formatted output in the desired citation style.
	 * 
	 * @param citationStyle  Identifier (i.e. the name) of the citation style.
	 * @param outputFormat   Identifier (i.e. the name) of the output file format.
	 * @param itemList       A XML containing the item list for which the output should be taken.
	 * Item list should be formatted in accordance with item-list.xsd
	 * Metadata record for each item in the item list should be specified in
	 * accordance with escidoc_publication_profile.xsd
	 */
    
    
	byte[] getOutput(String citationStyle, String ouputFormat, String itemList)
		throws IOException, JRException, CitationStyleManagerException;
	
}