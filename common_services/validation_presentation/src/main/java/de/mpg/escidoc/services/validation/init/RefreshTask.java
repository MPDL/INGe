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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.validation.init;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.validation.ItemValidating;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class RefreshTask extends Thread
{

    private static final Logger logger = Logger.getLogger(RefreshTask.class);
    
    private boolean signal = false;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        try
        {
            int timeout = Integer.parseInt(PropertyReader.getProperty("escidoc.validation.refresh.interval"));
            while (!signal)
            {
                Thread.sleep(timeout * 60 * 1000);
                logger.info("Starting refresh of validation database.");
                Context ctx = new InitialContext();
                ItemValidating itemValidating = (ItemValidating) ctx.lookup(ItemValidating.SERVICE_NAME);
                itemValidating.refreshValidationSchemaCache();
                logger.info("Finished refresh of validation database.");
                
            }
            logger.info("Refresh task terminated.");
        }
        catch (Exception e)
        {
            logger.error("Error initializing refresh task", e);
        }
    }
    
    public void terminate()
    {
        logger.info("Refresh task signalled to terminate.");
        signal = true;
    }
    
}
