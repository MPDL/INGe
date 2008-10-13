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

package de.mpg.escidoc.services.validation.xmltransforming;


import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.xmltransforming.exceptions.WrongEnumException;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;

/**
 * Class with helper methods for the JiBX-based XML-2-Java-Transforming.
 *
 * @author Johannes Mueller (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 126 $ $LastChangedDate: 2007-11-15 11:36:15 +0100 (Thu, 15 Nov 2007) $
 */
public final class JiBXHelper
{

    static final String DCTERMS_NAMESPACE_PREFIX = "dcterms:";
    static final String IDTYPES_NAMESPACE_PREFIX = "eidt:";

    /**
     * Hide default constructor.
     */
    private JiBXHelper()
    {

    }

    /**
     * Factory method to create a <code>java.util.ArrayList&lt;ValidationReportItemVO></code>
     * as the implementation of a <code>java.util.List</code>.
     *
     * @return new <code>java.util.ArrayList&lt;ValidationReportItemVO></code>
     */
    public static List<ValidationReportItemVO> validationReportItemsFactory()
    {
        return new ArrayList<ValidationReportItemVO>();
    }

    /**
     * Factory method to create a <code>java.util.ArrayList&lt;ValidationReportItemVO></code> as the implementation of a
     * <code>java.util.List</code>.
     *
     * @return new <code>java.util.ArrayList&lt;ValidationReportItemVO></code>
     */
    public static List<String> validationReportRulesFactory()
    {
        return new ArrayList<String>();
    }

    /**
     * Deserializes a given info level string ("informative", "restricted" to the according object.
     * @param text The info level as string.
     * @return InfoLevel object
     * @throws WrongEnumException Thrown if info level is null.
     */
    public static ValidationReportItemVO.InfoLevel deserializeInfoLevel(final String text)
        throws WrongEnumException
    {
        ValidationReportItemVO.InfoLevel level = null;
        if (text == null)
        {
            throw new WrongEnumException("info level is null.");
        }
        else
        {
            String upperCaseText = text.trim().toUpperCase();
            try
            {
                level = ValidationReportItemVO.InfoLevel.valueOf(upperCaseText);
            }
            catch (IllegalArgumentException e)
            {
                throw new WrongEnumException("info level is '" + text + "'.", e);
            }
        }
        return level;
    }

    /**
     * Serializes a given info level object to the according string.
     * @param level The info level as object
     * @return The info level as string
     */
    public static String serializeInfoLevel(final ValidationReportItemVO.InfoLevel level)
    {
        return level.toString();
    }

}
