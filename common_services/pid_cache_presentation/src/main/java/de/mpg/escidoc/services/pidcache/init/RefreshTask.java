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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.pidcache.init;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.pidcache.process.CacheProcess;
import de.mpg.escidoc.services.pidcache.process.QueueProcess;

/**
 * Thread running continuously in background.
 * Calls {@link QueueProcess} and {@link CacheProcess} run method
 * 
 * @author saquet
 *
 */
public class RefreshTask extends Thread
{
    private static final Logger logger = Logger.getLogger(RefreshTask.class);
    private boolean signal = false;
    
    /**
     * {@inheritDoc}
     */
    public void run()
    {
        try
        {
            int timeout = Integer.parseInt(PropertyReader.getProperty("escidoc.pidcache.refresh.interval"));
            timeout = timeout * 1 * 1000;
            CacheProcess cacheProcess = new CacheProcess();
            QueueProcess queueProcess = new QueueProcess();
            while (!signal)
            {
            	Thread.sleep(Long.parseLong(Integer.toString(timeout)));
                logger.debug("Starting refresh of pid cache databases.");
                try 
                {
                	 queueProcess.empty();
                     cacheProcess.fill();
				} 
                catch (Exception e) 
                {
                	logger.error("Error during refresh task", e);
				}
                logger.debug("Finished refresh of pid cache databases.");
            }        
        }
        catch (Exception e)
        {
            logger.error("Error initializing refresh task", e);
        }
        logger.warn("Refresh task terminated.");

    }
    
    public void terminate()
    {
        logger.warn("Refresh task signalled to terminate.");
        signal = true;
    }
}
