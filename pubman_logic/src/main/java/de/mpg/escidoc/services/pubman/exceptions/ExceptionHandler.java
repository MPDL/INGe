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

package de.mpg.escidoc.services.pubman.exceptions;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.missing.MissingParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.logging.CommonLogicMessages;
import de.mpg.escidoc.services.common.logging.MessageCreator;


/**
 * This class handles exceptions that might occur in direct interaction with the framework.
 *
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 422 $ $LastChangedDate: 2007-11-07 12:15:06 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 19.09.2007
 */
public class ExceptionHandler
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(ExceptionHandler.class);

    /**
     * Handles the given exception (by transforming it to a well-defined functional or technical exception and logging it).
     * 
     * @param e The exception to handle.
     * @param methodname The name of the method where the exception occured.
     * @throws TechnicalException
     * @throws SecurityException
     */
    public static void handleException(Exception e, String methodname) throws TechnicalException, SecurityException
    {
        if (e instanceof SecurityException)
        {
            // TODO MUJ: Was tun?
            throw (SecurityException)e;            
        }
        else if (e instanceof SystemException || e instanceof MissingContentException
                || e instanceof ComponentNotFoundException || e instanceof ReadonlyAttributeViolationException
                || e instanceof XmlSchemaValidationException || e instanceof InvalidXmlException || e instanceof MissingParameterException
                || e instanceof MissingMethodParameterException || e instanceof MalformedURLException
                || e instanceof ReadonlyElementViolationException || e instanceof RemoteException || e instanceof ServiceException)

        {
            logger.debug(MessageCreator.getMessage(CommonLogicMessages.FRAMEWORK_SYSTEM_ERROR, new Object[] { methodname }), e);
        }
        throw new TechnicalException(e.toString(), e);
    }
}
