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
 * This exception occurs whenever a String cannot be transformed to <code>java.util.Date</code>. The variable
 * <code>dateString</code> contains the problematic String.
 * 
 * @author Johannes Mueller (initial creation)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @revised by MuJ: 28.08.2007
 */
public class WrongDateException extends UnmarshallingException
{
    /**
     * The problematic String that lead to the exception.
     */
    private String dateString;

    /**
     * This constructor creates a new WrongDateException, forwards the given Throwable to the super class and stores the
     * given String in the according member variable.
     * 
     * @param dateString The date String that caused the problem.
     * @param cause The Throwable.
     */
    public WrongDateException(String dateString, Throwable cause)
    {
        super((dateString == null ? "The date string is null!" : "The following date cannot be transformed: '" + dateString + "'."), cause);
        this.dateString = dateString;
    }

    /**
     * Delivers the problematic String that lead to the exception.
     * 
     * @return the readString
     */
    public String getDateString()
    {
        return dateString;
    }
}
