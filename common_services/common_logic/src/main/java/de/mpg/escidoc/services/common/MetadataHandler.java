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

package de.mpg.escidoc.services.common;

import de.mpg.escidoc.services.common.metadata.IdentifierNotRecognisedException;

/**
 * Interface for Metadata Handler EJB.
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 106 $ $LastChangedDate: 2007-11-07 13:14:06 +0100 (Wed, 07 Nov 2007) $
 *
 */
public interface MetadataHandler
{

    /**
     * The name to obtain this service.
     */
    String SERVICE_NAME = "ejb/de/mpg/escidoc/services/metadata/MetadataHandler";

    /**
     * Returns an publication item xml based on a given bibtex input.
     *
     * @param bibtex The input in bibtex format.
     * @return Item xml if the given input was a valid publication resource.
     * @throws Exception Thrown if the identifier does not match a pattern
     * recognized by the service.
     */
    public String bibtex2item(String bibtex) throws Exception;
    
    /**
     * Checks the transformation.xml if the requested transformation is supported and if a xslt uri exists.
     * @param formatFrom The source format.
     * @param formatTo The target format.
     * @return true of transformation is supported, false if not
     */
    public boolean checkTransformation(String formatFrom, String formatTo);
    
    /**
     * Gives back all transformations available for a given format.
     * @param format The source format.
     * @return List of supported transformations for a format
     */
    public String[] getTransformations(String format);
    
    /**
     * This method looks up the right xslt uri for the transformation and calls the transformationHandler.
     * @param formatFrom
     * @param formatTo
     * @param itemXML
     * @return a transformed itemXML
     */
    public String transform(String formatFrom, String formatTo, String itemXML)throws IdentifierNotRecognisedException;

}
