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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.validation;


/**
 * Interface for Validation web service.
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 131 $ $LastChangedDate: 2007-11-21 18:53:43 +0100 (Wed, 21 Nov 2007) $
 *
 */
public interface ItemValidatingWebService
{

    /**
     * Validate the given item xml against the matching validation schema using the default validation point.
     *
     * @param itemXml The item as xml according to the xml schema.
     * (like "submit_item", etcetera). As validation point "default" will be used.
     * @return The validation report as xml.
     * @throws Exception Another exception.
     */
    String validateItemXml(final String itemXml) throws Exception;

    /**
     * Validate the given item xml against the matching validation schema.
     *
     * @param itemXml The item as xml according to the xml schema.
     * @param validationPoint A string representing the current validation point
     * (like "submit_item", etcetera).
     * @return The validation report as xml.
     * @throws Exception Another exception.
     */
    String validateItemXml(
            final String itemXml,
            final String validationPoint) throws Exception;

    /**
     * Validate the given item xml against the matching validation schema.
     *
     * @param itemXml The item as xml according to the xml schema.
     * @param validationPoint A string representing the current validation point
     * (like "submit_item", etcetera).
     * @param validationSchema A string representing the chosen validation schema
     * 
     * @return The validation report as xml.
     * @throws Exception Another exception.
     */
    String validateItemXmlBySchema(
            final String itemXml,
            final String validationPoint,
            final String validationSchema) throws Exception;

    /**
     * Refresh the validation schema cache.
     * @throws Exception Any unmanaged exception.
     */
    void refreshValidationSchemaCache() throws Exception;

}
