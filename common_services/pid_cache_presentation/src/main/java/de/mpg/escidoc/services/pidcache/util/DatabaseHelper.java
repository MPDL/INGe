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

package de.mpg.escidoc.services.pidcache.util;

import java.sql.Connection;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class DatabaseHelper
{
	public static final String CREATE_TABLES_STATEMENT = 
	    "CREATE TABLE ESCIDOC_PID_CACHE (identifier VARCHAR NOT NULL PRIMARY KEY, created TIMESTAMP);\n" +
	    "CREATE TABLE ESCIDOC_PID_QUEUE (identifier VARCHAR NOT NULL PRIMARY KEY, url VARCHAR NOT NULL PRIMARY KEY, created TIMESTAMP);";
    public static final String GET_ID_FIRST_ELEMENT_STATEMENT = "SELECT IDENTIFIER FROM ESCIDOC_PID_CACHE";
    public static final String ADD_ELEMENT_STATEMENT = "INSERT INTO ESCIDOC_PID_CACHE VALUES ('XXX_IDENTIFER_XXX', XXX_TIMESTAMP_XXX)";
    public static final String REMOVE_ELEMENT_STATEMENT= "DELETE FROM ESCIDOC_PID_CACHE WHERE IDENTIFIER = 'XXX_IDENTIFER_XXX'";

    
    /**
     * Get the connection to the cache table
     * @return
     * @throws Exception
     */
    public static Connection getConnection() throws Exception
    {
        Context ctx = new InitialContext();
        DataSource dataSource = (DataSource) ctx.lookup("PidCache");
        return dataSource.getConnection();
    }
    
    /**
     * Create the cache table.
     * @throws Exception
     */
    public static void createTable() throws Exception
    {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate(CREATE_TABLES_STATEMENT);
        
        statement.close();
        connection.close();
    }
}
