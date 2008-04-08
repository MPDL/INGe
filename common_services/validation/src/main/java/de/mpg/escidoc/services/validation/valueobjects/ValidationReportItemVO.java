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

package de.mpg.escidoc.services.validation.valueobjects;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 *
 * Validation report item.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 126 $ $LastChangedDate: 2007-11-15 11:36:15 +0100 (Thu, 15 Nov 2007) $
 *
 */
public class ValidationReportItemVO extends ValueObject
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     *
     * @author Johannes Mueller
     */
    private static final long serialVersionUID = 1L;
    /**
     *
     * Type enumeration.
     *
     * @author franke (initial creation)
     * @author $Author: mfranke $ (last modification)
     * @version $Revision: 126 $ $LastChangedDate: 2007-11-15 11:36:15 +0100 (Thu, 15 Nov 2007) $
     *
     */
    public enum InfoLevel
    {
        RESTRICTIVE,
        INFORMATIVE
    }

    private String content;
    private String element;

    /**
     * The type declares whether this item is restrictive,
     * that means the action must not be continued or this item is only informative.
     */
    private InfoLevel level;
    /**
     * Content-getter.
     * @return The content.
     */
    public final String getContent()
    {
        return content;
    }

    /**
     * Content-setter.
     * @param content The content.
     */
    public final void setContent(final String content)
    {
        this.content = content;
    }

    /**
     * Type-getter.
     * @return The level.
     */
    public final InfoLevel getInfoLevel()
    {
        return level;
    }

    /**
     * Type-setter.
     * @param level The level.
     */
    public final void setInfoLevel(final InfoLevel level)
    {
        this.level = level;
    }

    public final String getElement()
    {
        return element;
    }

    public final void setElement(final String element)
    {
        this.element = element;
    }

    /**
     * JiBX helper method.
     * @return true if the item is restrictive
     */
    public final boolean isRestrictive()
    {
        return (level == InfoLevel.RESTRICTIVE);
    }

    /**
     * JiBX helper method.
     * @return true if the item is restrictive
     */
    public final boolean isInformative()
    {
        return (level == InfoLevel.INFORMATIVE);
    }

    /**
     * Override.
     * @return String
     */
    @Override
    public final String toString()
    {
        return content + " (" + level + ")";
    }
}
