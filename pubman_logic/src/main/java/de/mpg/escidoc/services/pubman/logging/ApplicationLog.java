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

package de.mpg.escidoc.services.pubman.logging;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.logging.MessageCreator;
import de.mpg.escidoc.services.common.logging.Messages;

/**
 * ApplicationLog is used for functional logging.
 * 
 * All messages are logged to a defined logger. Only info level is allowed.
 *
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @Revised by BrP: 20.09.2007
 */
public class ApplicationLog
{
    /**
     * Name of the application logger
     */
    public static final String APPLICATION_LOGGER_NAME = "de.mpg.escidoc.pubman.application";
    
    /**
     * Logger instance
     */
    private static Logger logger = Logger.getLogger(APPLICATION_LOGGER_NAME);
    
    /**
     * Logs the message to the application logger with log level INFO.
     * 
     * @param message the log message template
     * @param messageArgs the log message parameters
     */
    public static void info(Messages message, Object[] messageArgs)
    {
        logger.info(MessageCreator.getMessage(message, messageArgs));
    }
}
