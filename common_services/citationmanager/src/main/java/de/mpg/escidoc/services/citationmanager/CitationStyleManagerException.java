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

package de.mpg.escidoc.services.citationmanager;

/**
 * Citation Style Processing Engine specific exception. 
 *
 * @author makarenko (initial creation)
 * @author $Author: vdm $ (last modification)
 * @version $Revision: 146 $ $LastChangedDate: 2007-11-12 20:58:08 +0100 (Mon, 12 Nov 2007) $
 *
 */


@SuppressWarnings("serial")
public class CitationStyleManagerException extends Exception
{

    /**
     * Default constructor.
     */
    public CitationStyleManagerException()
    {
        super();
    }

    /**
     * Constructor with exception.
     *
     * @param e The exception.
     *
     */
    public CitationStyleManagerException(final Throwable e)
    {
        super(e);
    }

    /**
     * Constructor with message.
     *
     * @param message The message.
     *
     */
    public CitationStyleManagerException(final String message)
    {
        super(message);
    }

    /**
     * Constructor with message and exception.
     *
     * @param message The message.
     * @param e The Exception
     */
    public CitationStyleManagerException(final String message, final Throwable e)
    {
        super(message, e);
    }
}
