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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ImportDatabaseInitializer
{
    private final static Logger logger = Logger.getLogger(ImportDatabaseInitializer.class);
    
    public ImportDatabaseInitializer() throws Exception
    {

        logger.info("Initializing import database");
        
        Class.forName(PropertyReader.getProperty("escidoc.import.database.driver.class"));
        String connectionUrl = PropertyReader.getProperty("escidoc.import.database.connection.url");
        Connection connection = DriverManager.getConnection(connectionUrl
                .replaceAll("\\$1", PropertyReader.getProperty("escidoc.import.database.server.name"))
                .replaceAll("\\$2", PropertyReader.getProperty("escidoc.import.database.server.port"))
                .replaceAll("\\$3", PropertyReader.getProperty("escidoc.import.database.name")),
                PropertyReader.getProperty("escidoc.import.database.user.name"),
                PropertyReader.getProperty("escidoc.import.database.user.password"));
        
        String dbScript = ResourceUtil.getResourceAsString("import_database.sql");
        
        String[] queries = dbScript.split(";");
        
        try
        {
            for (String query : queries)
            {
                logger.debug("Executing statement: " + query);
                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
                statement.close();
            }
        }
        catch (SQLException e)
        {
            logger.debug("Error description", e);
            logger.info("Import database is set up already");
        }
        logger.info("Import database initialized");
    }
}
