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

import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * Exception thrown when an item breaks restrictive validation rules.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 106 $ $LastChangedDate: 2007-11-07 13:14:06 +0100 (Wed, 07 Nov 2007) $
 */
@SuppressWarnings("serial")
public class ItemInvalidException extends Exception
{
    private ValidationReportVO report;

    /**
     * Constructor with ValidationReportVO.
     *
     * @param report The validation report.
     */
    public ItemInvalidException(final ValidationReportVO report)
    {
        super();
        this.report = report;
    }

    public final ValidationReportVO getReport()
    {
        return report;
    }

    public final void setReport(ValidationReportVO report)
    {
        this.report = report;
    }
}
