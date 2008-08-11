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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.importmanager;

import java.io.FileNotFoundException;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.metadata.IdentifierNotRecognisedException;


/**
 * Interface for handling the import of items from external systems
 * @author Friederike Kleinfercher (initial creation) 
 */


public interface ImportHandler{


    String SERVICE_NAME = "ejb/de/mpg/escidoc/services/importmanager/ImportHandler";
    
    /**
     * This method provides XML formated output of the supported import sources
     * @return xml presentation of all available import sources
     */
    String explainSources ();
    
    /**
     * This operation checks the sources properties for the harvesting protocol and forwards the fetching request.
	 * The parameter format-to is optional. If a format-to is provided the ImportHandler calls the metadataHandler for 
	 * the transformation file and calls XMLTransforming. 
	 * @param sourceName, identifier, formatTo, formatFrom
	 * @return itemXML
	 */
    String fetchMetadata(String sourceName, String identifier,String formatTo)throws IdentifierNotRecognisedException, SourceNotAvailableException, TechnicalException;
    
    /**
     * Fetches the selected format from an external system
     * @param sourceName, identifier, format
	 * @return a file in the given format
     */
    byte[] fetchData(String sourceName, String identifier, String[] listOfFormats)throws FileNotFoundException;
    
    /**
     * This is the only source specific method, which has to be updated when a new source
     * is specified for import
     */
    String trimIdentifier(String sourceName, String identifier);

}
