/*
*
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

package de.mpg.escidoc.services.common.exceptions;

import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;

/**
 * Exception class used to indicate that a certain affiliation could not be found.
 *
 * @version $Revision$ $LastChangedDate$ by $Author$
 * Revised by MuJ: 03.09.2007
 */
public class AffiliationNotFoundException extends BusinessException
{
    /**
     * The reference of the affiliation that could not be found.
     */
    private AffiliationRO affiliationRef;
    
    /**
     * Creates a new instance with the given affiliation reference.
     * 
     * @param affiliationRef The affiliation reference
     */
    public AffiliationNotFoundException(AffiliationRO affiliationRef)
    {
        super();
        if (affiliationRef == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":AffiliationNotFoundException(AffiliationRO):affiliationRef is null");
        }
        this.affiliationRef = affiliationRef;
    }
    
    /**
     * Creates a new instance with the given affiliation reference and cause.
     * 
     * @param affiliationRef The affiliation reference
     * @param cause The cause
     */
    public AffiliationNotFoundException(AffiliationRO affiliationRef, Throwable cause)
    {
        super(cause);
        if (affiliationRef == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":AffiliationNotFoundException(AffiliationRO, Throwable):affiliationRef is null");
        }
        if (cause == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":AffiliationNotFoundException(AffiliationRO, Throwable):cause is null");
        }
        this.affiliationRef = affiliationRef;       
    }

    /**
     * Delivers the reference to the affiliation that was not found.
     * 
     * @return The affiliationRef
     */
    public AffiliationRO getAffiliationRef()
    {
        return affiliationRef;
    }



}
