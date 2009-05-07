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
 * This exception occurs whenever an unknown enumeration value is read from an XML file. This may occur both while
 * marshalling and while unmarshalling. The member variable <i>enumString</i> contains the String that caused the
 * Exception.
 * 
 * @author Johannes Mueller (initial creation)
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @revised by MuJ: 03.09.2007
 */
public class WrongEnumException extends TransformingException
{
    /**
     * The name of the Enum or the name of the XML value that caused the exception.
     */
    private String enumString;

    /**
     * Constructor forwarding the given name of the Enum/name of the XML value to the upper exception chain. Stores the
     * name of the Enum/name of the XML value in the according member variable.
     * 
     * @param msg The name of the Enum/the name of the XML value that caused the exception
     */
    public WrongEnumException(String msg)
    {
        super(msg);
        this.enumString = msg;
    }

    /**
     * Constructor forwarding the given name of the Enum/name of the XML value and the cause to the upper exception chain. Stores the
     * name of the Enum/name of the XML value in the according member variable.
     * 
     * @param msg The name of the Enum or the name of the XML value that caused the exception
     * @param cause The cause
     */
    public WrongEnumException(String msg, Throwable cause)
    {
        super(msg, cause);
        this.enumString = msg;
    }

    /**
     * Delivers the name of the Enum or the name of the XML value that caused the exception.
     * 
     * @return The name of the Enum or the name of the XML value that caused the exception
     */
    public String getEnumString()
    {
        return enumString;
    }

}
