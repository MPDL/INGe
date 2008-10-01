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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.importmanager.exceptions;

import java.util.Date;

/**
 * Exceptions for import sources.
 * @author kleinfe1
 *
 */
public class SourceNotAvailableException extends Exception
{

	private static final long serialVersionUID = 1L;
	private Date retryAfter = null;
    
    public SourceNotAvailableException()
    {
        
    }

    public SourceNotAvailableException(String message)
    {
        super(message);
    }

    public SourceNotAvailableException(Throwable cause)
    {
        super(cause);
    }

    public SourceNotAvailableException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SourceNotAvailableException(Date retryAfter)
    {
        super();
        this.retryAfter = retryAfter;
    }

    public Date getRetryAfter()
    {
        return this.retryAfter;
    }

    public void setRetryAfter(Date retryAfter)
    {
        this.retryAfter = retryAfter;
    }

}
