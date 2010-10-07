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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.pidcache.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

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
	// Initial statement to create tables
	public static final String CREATE_TABLES_STATEMENT = 
	    "CREATE TABLE ESCIDOC_PID_CACHE (identifier VARCHAR NOT NULL PRIMARY KEY, created TIMESTAMP);\n" +
	    "CREATE TABLE ESCIDOC_PID_QUEUE (identifier VARCHAR NOT NULL, url VARCHAR NOT NULL, created TIMESTAMP);";
    
	// Statements for pid cache table
	public static final String GET_CACHE_FIRST_ELEMENT_STATEMENT = 
		"SELECT * FROM ESCIDOC_PID_CACHE";
    public static final String ADD_ELEMENT_STATEMENT = 
    	"INSERT INTO ESCIDOC_PID_CACHE VALUES ('XXX_IDENTIFIER_XXX', 'XXX_TIMESTAMP_XXX')";
    public static final String REMOVE_ELEMENT_STATEMENT= 
    	"DELETE FROM ESCIDOC_PID_CACHE WHERE IDENTIFIER = 'XXX_IDENTIFIER_XXX'";
    public static final String CACHE_SIZE_STATEMENT =
    	"SELECT COUNT(*) AS SIZE FROM ESCIDOC_PID_CACHE";
    
    // Statements for pid queue table
    public static final String GET_QUEUE_FIRST_ELEMENT_STATEMENT =
    	"SELECT * FROM ESCIDOC_PID_QUEUE";
    public static final String ADD_QUEUE_ELEMENT_STATEMENT = 
    	"INSERT INTO ESCIDOC_PID_QUEUE VALUES ('XXX_IDENTIFIER_XXX', 'XXX_URL_XXX', 'XXX_TIMESTAMP_XXX')";
    public static final String REMOVE_QUEUE_ELEMENT_STATEMENT = 
    	"DELETE FROM ESCIDOC_PID_QUEUE WHERE IDENTIFIER = 'XXX_IDENTIFIER_XXX'";
    public static final String GET_QUEUE_ELEMENT_URL_STATEMENT = 
    	"SELECT * FROM ESCIDOC_PID_QUEUE WHERE URL = 'XXX_URL_XXX'";
    public static final String RETRIEVE_QUEUE_ELEMENT_STATEMENT = 
    	"SELECT * FROM ESCIDOC_PID_QUEUE WHERE IDENTIFIER = 'XXX_IDENTIFIER_XXX'";
    
    private static Logger logger = Logger.getLogger(DatabaseHelper.class);
    
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
     * @throws SQLException 
     * @throws Exception
     */
    public static void createTable() throws SQLException
    {
        Connection connection = null;
        Statement statement = null;
        try 
		{
			connection = getConnection();
			statement = connection.createStatement();
	        statement.executeUpdate(CREATE_TABLES_STATEMENT);
		} 
        catch (Exception e) 
		{
            logger.debug("Error while trying to create table, it probably already exists", e);
        	statement.close();
            connection.close();
            //throw new RuntimeException(e);
		}        
        statement.close();
        connection.close();
    }
    
    /**
     * Return a formatted timestamp of the current time
     *
     * @return
     */
    public static String getTimeStamp()
    {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    	Date date = new Date(new Date().getTime());
    	return dateFormat.format(date);
    }
}
