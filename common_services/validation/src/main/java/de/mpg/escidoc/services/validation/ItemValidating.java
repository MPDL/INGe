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

package de.mpg.escidoc.services.validation;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * Interface for Validation EJB.
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 106 $ $LastChangedDate: 2007-11-07 13:14:06 +0100 (Wed, 07 Nov 2007) $
 *
 */
public interface ItemValidating extends ItemValidatingWebService
{

    /**
     * The name to obtain this service.
     */
    String SERVICE_NAME = "ejb/de/mpg/escidoc/services/validation/ItemValidating";

    /**
     * Validate the given item xml against the matching vsalidation schema using the default validation point.
     *
     * @param itemXml The item as xml according to the xml schema.
     * (like "submit_item", etcetera). If no validation point is given, "default will be used.
     * @return The validation report as xml.
     * @throws ValidationSchemaNotFoundException Schema not found in database.
     * @throws TechnicalException Another exception.
     */
    String validateItemXml(final String itemXml) throws
            ValidationSchemaNotFoundException,
            TechnicalException;

    /**
     * Validate the given item xml against the matching vsalidation schema.
     *
     * @param itemXml The item as xml according to the xml schema.
     * @param validationPoint A string representing the current validation point
     * (like "submit_item", etcetera). If no validation point is given, "default will be used.
     * @return The validation report as xml.
     * @throws ValidationSchemaNotFoundException Schema not found in database.
     * @throws TechnicalException Another exception.
     */
    String validateItemXml(
            final String itemXml,
            final String validationPoint) throws
            ValidationSchemaNotFoundException,
            TechnicalException;

    /**
     * Validate the given item vo against the matching validation schema using the default validation point.
     *
     * @param itemVO The item as value object.
     * (like "submit_item", etcetera). If no validation point is given, "default will be used.
     * @return The validation report as xml.
     * @throws ValidationSchemaNotFoundException Schema not found in database.
     * @throws TechnicalException Another exception.
     */
    ValidationReportVO validateItemObject(final PubItemVO itemVO) throws
            ValidationSchemaNotFoundException,
            TechnicalException;

    /**
     * Validate the given item vo against the matching validation schema.
     *
     * @param itemVO The item as value object.
     * @param validationPoint A string representing the current validation point
     * (like "submit_item", etcetera). If no validation point is given, "default will be used.
     * @return The validation report as xml.
     * @throws ValidationSchemaNotFoundException Schema not found in database.
     * @throws TechnicalException Another exception.
     */
    ValidationReportVO validateItemObject(
            final PubItemVO itemVO,
            final String validationPoint) throws
            ValidationSchemaNotFoundException,
            TechnicalException;
    /**
     * Refresh the validation schema cache.
     * @throws TechnicalException Any unmanaged exception.
     */
    void refreshValidationSchemaCache() throws TechnicalException;

}
