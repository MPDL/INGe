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

package de.mpg.escidoc.services.common.valueobjects;

import java.io.Serializable;

/**
 * The super class of all value objects.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision: 627 $ $LastChangedDate: 2007-11-19 17:27:42 +0100 (Mon, 19 Nov 2007) $ by $Author: tdiebaec $
 * @updated 05-Sep-2007 10:30:54
 */
public abstract class ValueObject implements Serializable
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.PubItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
    private static final long serialVersionUID = 1L;

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    protected boolean equals(Object obj1, Object obj2)
    {
        // added by DiT, 19.11.2007: replace windows-line breaks
        if (obj1 instanceof String)
        {
            obj1 = ((String)obj1).replace("\r", "");
        }
        if (obj2 instanceof String)
        {
            obj2 = ((String)obj2).replace("\r", "");
        }
        
        if (obj1 != null)
        {
            if (!obj1.equals(obj2))
            {
                return false;
            }
        }
        else if (obj2 != null)
        {
            return false;
        }
        return true;
    }
}