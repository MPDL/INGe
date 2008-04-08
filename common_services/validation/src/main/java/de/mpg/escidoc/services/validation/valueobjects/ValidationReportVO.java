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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * @author Full Access
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 126 $ $LastChangedDate: 2007-11-15 11:36:15 +0100 (Thu, 15 Nov 2007) $
 */
public class ValidationReportVO extends ValueObject
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
    private java.util.List<ValidationReportItemVO> items = new java.util.ArrayList<ValidationReportItemVO>();

    private String validationPoint;
    private List<String> rules = new ArrayList<String>();

    /**
     * Default constructor.
     *
     */
    public ValidationReportVO()
    {

    }

    /**
     * Items-getter.
     * @return The validation report items as ArrayList.
     */
    public final java.util.List<ValidationReportItemVO> getItems()
    {
        return items;
    }

    /**
     * Add a single ValidationReportItem to the items ArrayList.
     * @param item The Item.
     */
    public final void addItem(final ValidationReportItemVO item)
    {
        if (this.items == null)
        {
            this.items = new ArrayList<ValidationReportItemVO>();
        }
        this.items.add(item);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if a "items" XML structure has
     * to be created during marshalling.
     *
     * @return boolean true if there is at least one item stored in this report.
     */
    public final boolean hasItems()
    {
        if (this.items == null)
        {
            return false;
        }
        else
        {
            return (this.items.size() >= 1);
        }
    }

    /**
     * Returns whether the item is valid or not. If at least one restrictive rule wasn't validated, false is returned.
     *
     * @return Validity of the tested item.
     */
    public final boolean isValid()
    {
        if (hasItems())
        {
            for (Iterator iter = items.iterator(); iter.hasNext();)
            {
                ValidationReportItemVO element = (ValidationReportItemVO) iter.next();
                if (element.isRestrictive())
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return true;
        }
    }

    /**
     * Getter.
     * @return The validation point
     */
    public final String getValidationPoint()
    {
        return validationPoint;
    }

    /**
     * Setter.
     * @param validationPoint The validation point
     */
    public final void setValidationPoint(final String validationPoint)
    {
        this.validationPoint = validationPoint;
    }

    /**
     * Getter.
     * @return The rules list. Not used actually
     */
    public final List<String> getRules()
    {
        return rules;
    }

    /**
     * Setter.
     * @param rules The rules list. Not used actually
     */
    public final void setRules(final List<String> rules)
    {
        this.rules = rules;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString()
    {
        return "Validation Report (" + items.size() + " item(s)):\n" + items;
    }
}