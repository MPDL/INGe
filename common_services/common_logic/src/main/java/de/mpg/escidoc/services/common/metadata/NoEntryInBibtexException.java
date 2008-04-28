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

package de.mpg.escidoc.services.common.metadata;

/**
 * Exception thrown if a given BibTeX file contains no BibTeX entry.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 146 $ $LastChangedDate: 2007-12-18 14:42:42 +0100 (Tue, 18 Dec 2007) $
 */
public class NoEntryInBibtexException extends Exception
{

    /**
     * Default constructor.
     */
    public NoEntryInBibtexException()
    {
    }

    /**
     * Default constructor.
     */
    public NoEntryInBibtexException(String message)
    {
        super(message);
    }

    /**
     * Default constructor.
     */
    public NoEntryInBibtexException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Default constructor.
     */
    public NoEntryInBibtexException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
