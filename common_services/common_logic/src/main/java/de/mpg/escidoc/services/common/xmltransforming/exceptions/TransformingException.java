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

package de.mpg.escidoc.services.common.xmltransforming.exceptions;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;

/**
 * This is the abstract super class of all more specific XML transforming exceptions.
 * 
 * @author Johannes Mueller (initial creation)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @revised by MuJ: 03.09.2007
 */
public abstract class TransformingException extends TechnicalException
{    
    /**
     * Constructor forwarding a given exception message to the upper exception chain. 
     * 
     * @param msg The exception message
     */
    protected TransformingException(String msg)
    {
        super(msg);
    }
    
    /**
     * Constructor forwarding a given exception message and a cause to the upper exception chain.
     * 
     * @param msg The exception message
     * @param cause The cause
     */
    protected TransformingException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
