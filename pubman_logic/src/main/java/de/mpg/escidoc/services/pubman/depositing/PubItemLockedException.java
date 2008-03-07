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

package de.mpg.escidoc.services.pubman.depositing;

import de.mpg.escidoc.services.common.referenceobjects.PubItemRO;

/**
 * Exception class used for item which are locked.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 422 $ $LastChangedDate: 2007-11-07 12:15:06 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 19.09.2007
 */
public class PubItemLockedException extends DepositingException
{
    /**
     * The reference of the pubitem that is locked.
     */
    private PubItemRO pubItemRef;

    /**
     * Creates a new instance with the given pubItemRef and cause.
     * 
     * @param pubItemRef The reference of the pubitem that is locked.
     * @param cause The throwable which caused this exception.
     */
    public PubItemLockedException(PubItemRO pubItemRef, Throwable cause)
    {
        super(pubItemRef, cause);
    }

    /**
     * @return The reference of the pubitem that is locked.
     */
    public PubItemRO getPubItemRef()
    {
        return pubItemRef;
    }
}
