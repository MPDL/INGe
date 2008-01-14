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

/**
 * This exception occurs whenever something goes wrong during marshalling. The member variable <i>marshalledObjectType</i>
 * contains information about the object type that should have been marshalled.
 * 
 * @author Johannes Mueller (initial creation)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @revised by MuJ: 03.09.2007
 */
public class MarshallingException extends TransformingException
{
    private String m_marshalledObjectType;

    /**
     * This constructor creates a new MarshallingException and stores the given name of the object type that should have
     * been marshalled in the according member variable.
     * 
     * @param marshalledObjectType The name of the object type that should have been marshalled
     * @param cause The Throwable
     */
    public MarshallingException(String marshalledObjectType, Throwable cause)
    {
        super(marshalledObjectType, cause);
        m_marshalledObjectType = marshalledObjectType;
    }

    /**
     * Delivers the name of the object type that should have been marshalled.
     * 
     * @return The name of the object type
     */
    public String getMarshalledObjectType()
    {
        return m_marshalledObjectType;
    }
}
