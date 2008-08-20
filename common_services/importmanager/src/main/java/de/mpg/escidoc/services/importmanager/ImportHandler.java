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
import de.mpg.escidoc.services.importmanager.exceptions.SourceNotAvailableException;


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
     * This operation fetches data from the specified source. The format of the requested data will be the default metadata format
     * defined in import.xml
	 * @param sourceName, identifier
	 * @return itemXML
	 */
    String fetchMetadata(String sourceName, String identifier)throws IdentifierNotRecognisedException, SourceNotAvailableException, TechnicalException;
    
    /**
     * This operation fetches data from the specified source and returns it in the requested format.
     * This format can either be the format the external source provides, or a format we can transform from a format the external source provides
	 * @param sourceName, identifier, format
	 * @return itemXML
	 */
    String fetchMetadata(String sourceName, String identifier,String format)throws IdentifierNotRecognisedException, SourceNotAvailableException, TechnicalException;
    
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
    

//    byte[] TestfetchData(String sourceName, String identifier, String FormatFrom, String FormatTo)throws FileNotFoundException, IdentifierNotRecognisedException, SourceNotAvailableException, TechnicalException ;

}
