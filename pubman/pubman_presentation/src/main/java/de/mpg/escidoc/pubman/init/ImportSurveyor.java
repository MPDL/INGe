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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.multipleimport.ImportLog;
import de.mpg.escidoc.pubman.multipleimport.ImportLogItem;
import de.mpg.escidoc.pubman.multipleimport.ImportLog.ErrorLevel;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ImportSurveyor extends Thread
{
    private static final Logger logger = Logger.getLogger(ImportSurveyor.class);

    private boolean signal = false;
    private long interval;
    
    public ImportSurveyor()
    {
        try
        {
            interval = Long.parseLong(PropertyReader.getProperty("escidoc.import.surveyor.interval"));
        }
        catch (Exception e) {
            throw new RuntimeException("Error initializing import surveyor");
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run()
    {
        do
        {
            try
            {
                Thread.sleep(interval * 60 * 1000);
            }
            catch (InterruptedException e) {
                logger.info("Import surveyor interrupted");
                return;
            }
            
            if (signal)
            {
                logger.info("Import surveyor interrupted");
                return;
            }
            
            Connection connection = ImportLog.getConnection();
            String query = "select id from escidoc_import_log where "
            		+ "(status = 'PENDING' or status = 'ROLLBACK') "
            		+ "and id not in (select parent from escidoc_import_log_item where "
            		+ "datediff('minute', startdate, now()) <= 5)";
            ResultSet resultSet = null;
            PreparedStatement statement = null;
            try
            {
                statement = connection.prepareStatement(query);
                resultSet = statement.executeQuery();
                while (resultSet.next())
                {
                    int id = resultSet.getInt("id");
                    logger.warn("Unfinished import detected (" + id + "). Finishing it with status FATAL.");
                    ImportLog log = ImportLog.getImportLog(id, true, true);
                    log.setConnection(connection);
                    
                    for (ImportLogItem item : log.getItems())
                    {
                        if (item.getEndDate() == null)
                        {
                            log.activateItem(item);
                            log.addDetail(ErrorLevel.WARNING, "import_process_terminate_item");
                            log.finishItem();
                        }
                    }
                    
                    log.startItem("import_process_aborted_unexpectedly");
                    log.addDetail(ErrorLevel.FATAL, "import_process_failed");
                    log.finishItem();
                    log.close();
                }
            }
            catch (Exception e)
            {
                logger.error("Error checking database for unfinished imports", e);
                
            }
            finally
            {
                try
                {
                    resultSet.close();
                    statement.close();
                }
                catch (Exception e2)
                {}
            }
        }
        while (!signal);
        logger.info("Import surveyor interrupted");
    }

    /**
     * Signals this thread to finish itself.
     */
    public void terminate()
    {
        logger.info("Sitemap creation task signalled to terminate.");
        signal = true;
    }

}
