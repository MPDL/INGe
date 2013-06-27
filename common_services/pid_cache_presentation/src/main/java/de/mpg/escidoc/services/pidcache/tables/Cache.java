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

package de.mpg.escidoc.services.pidcache.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.pidcache.Pid;
import de.mpg.escidoc.services.pidcache.util.DatabaseHelper;

/**
 * Handle cache management method
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Cache
{    
	public static int SIZE_MAX = 0;
	private static Cache instance = null;
	private static Logger logger = Logger.getLogger(Cache.class);;
	
	public synchronized static Cache getInstance()
	{
	    if(instance == null) {
	         instance = new Cache();
	      }
	      return instance;
	}
	
    private Cache()
    {
    	try
        {
            SIZE_MAX = Integer.parseInt(PropertyReader.getProperty("escidoc.pidcache.cache.size.max"));
        }
        catch (Exception e)
        {
            logger.warn("Error reading property escidoc.pidcache.cache.size.max");
            SIZE_MAX = 10;
        }
	}
    
    

    /**
     * Return the first PID of the cache
     * @return
     * @throws Exception
     */
    public Pid getFirst() throws Exception
	{
		Pid pid = new Pid();
    	Connection connection  = DatabaseHelper.getConnection();
    	Statement statement = connection.createStatement();
    	statement.setMaxRows(1);
    	ResultSet resultSet = statement.executeQuery(DatabaseHelper.GET_CACHE_FIRST_ELEMENT_STATEMENT);
    	if (resultSet.next())
     	{
    		pid.setIdentifier(resultSet.getString("identifier"));
 			connection.close();
     	}
    	else
    	{
    		connection.close();
    		throw new RuntimeException("No more PID in cache");
    	}
    	statement.close();
		return pid;
	}
    
    /**
     * Save a PID into the cache.
     * @param pid
     * @throws Exception
     */
    public void add(Pid pid) throws Exception
    {
    	Connection connection  = DatabaseHelper.getConnection();
    	
    	PreparedStatement stmt = connection.prepareStatement(DatabaseHelper.ADD_ELEMENT_STATEMENT);
        stmt.setString(1, pid.getIdentifier());
        stmt.setString(2, DatabaseHelper.getTimeStamp());       
        stmt.executeUpdate();
        stmt.close();
        
        connection.close();
    }
    
    /**
     * Delete a PID from the cache.
     * 
     * @param pid
     * @throws Exception
     */
    public void remove(Pid pid) throws Exception
    {
    	Connection connection  = DatabaseHelper.getConnection();
    	
    	PreparedStatement stmt = connection.prepareStatement(DatabaseHelper.REMOVE_ELEMENT_STATEMENT);
        stmt.setString(1, pid.getIdentifier());
        stmt.executeUpdate();
        stmt.close();
        
        connection.close();
    }
    
    /**
     * Current Size of the cache
     * @return
     * @throws Exception
     */
    public int size() throws Exception
    {
    	Connection connection  = DatabaseHelper.getConnection();
    	Statement statement = connection.createStatement();
    	ResultSet resultSet = statement.executeQuery(DatabaseHelper.CACHE_SIZE_STATEMENT);
    	int size = 0;
	 	if (resultSet.next())
	 	{	
	 		size = resultSet.getInt("size");
	    }
	 	connection.close();
    	statement.close();
		return size;
    }
}
