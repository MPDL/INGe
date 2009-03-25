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

package de.mpg.escidoc.pubman.init;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.task.SiteMapTask;
import de.mpg.escidoc.services.pubman.PubItemSimpleStatistics;

public class InitializerServlet extends HttpServlet
{
    
    private static final Logger logger = Logger.getLogger(InitializerServlet.class);

    SiteMapTask siteMapTask;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException
    {

        //initialize report definitions for statistics
        try
        {
            InitialContext initialContext = new InitialContext();
            PubItemSimpleStatistics statistics =
                (PubItemSimpleStatistics) initialContext.lookup(PubItemSimpleStatistics.SERVICE_NAME);
            statistics.initReportDefinitionsInFramework();
            
            siteMapTask = new SiteMapTask();
            siteMapTask.start();
            
        }
        catch (Exception e)
        {
            logger.error("Problem with initializing statistics system", e);
        }
        
    }

    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy()
    {
        super.destroy();
        logger.info("Signalled to terminate Sitemap creation task.");
        siteMapTask.terminate();
    }

}
