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

package de.mpg.escidoc.services.endnotemanager;

/**
 * Exception thrown by endnote export manager.
 *
 * @author Vlad Makarenko (initial creation)
 * @author $Author: vdm $ (last modification)
 * @version $Revision: 63 $
 *
 */
@SuppressWarnings("serial")
public class EndNoteExportXSLTNotFoundException extends Exception
{

    /**
     * Default constructor.
     */
    public EndNoteExportXSLTNotFoundException()
    {
        super();
    }

    /**
     * Constructor with exception.
     *
     * @param e The exception.
     *
     */
    public EndNoteExportXSLTNotFoundException(final Throwable e)
    {
        super(e);
    }

    /**
     * Constructor with message.
     *
     * @param message The message.
     *
     */
    public EndNoteExportXSLTNotFoundException(final String message)
    {
        super(message);
    }

    /**
     * Constructor with message and exception.
     *
     * @param message The message.
     * @param e The Exception
     */
    public EndNoteExportXSLTNotFoundException(final String message, final Throwable e)
    {
        super(message, e);
    }
}
