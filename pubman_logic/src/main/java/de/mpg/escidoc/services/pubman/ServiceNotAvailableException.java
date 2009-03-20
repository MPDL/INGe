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

package de.mpg.escidoc.services.pubman;

/**
 * Exception type for service lookup.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$ 
 * @revised by MuJ: 19.09.2007
 */
public class ServiceNotAvailableException extends RuntimeException
{
	
	/** serial for the serializable interface*/
	private static final long serialVersionUID = 1L;
    /**
     * The name of the service which could not be retrieved.
     */
    private String serviceName;

    /**
     * Creates a new Exception with the specified detail message.
     * 
     * @param serviceName The error message
     */
    public ServiceNotAvailableException(String serviceName)
    {
        this.serviceName = serviceName;
    }

    /**
     * Creates a new Exception with the specified detail message and cause.
     * 
     * @param serviceName 
     * @param cause The cause
     */
    public ServiceNotAvailableException(String serviceName, Throwable cause)
    {
        super(cause);
        this.serviceName = serviceName;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName()
    {
        return serviceName;
    }

}
