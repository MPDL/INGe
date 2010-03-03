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

package de.mpg.escidoc.services.pidcache.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import de.mpg.escidoc.services.pidcache.Pid;
import de.mpg.escidoc.services.pidcache.queue.PidQueue;
import de.mpg.escidoc.services.pidcache.util.DatabaseHelper;

/**
 * Handle cache management method
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class PidCache
{    
    public PidCache() 
    {
	}
    
    /**
     * This method does the following:
     *  - Take a PID from the cache
     *  - Change the URL of the PID
     *  - Put the PID in the queue
     *  - Delete the PID from the cache
     *  - Return the PID
     *  
     *  Notes: 
     *  - The actual editing of the PID in the GWDG service will proceed from the queue
     *  - The cache will be completed by a new PID generated from {@link PidCacheManager}
     * 
     * @param url The URL to be registered.
     * 
     * @return The PID.
     */
    public String assignPid(String url) throws Exception
    {
    	Pid pid = getFirstPidFromCache();
		String pidXml = editPid(pid.getIdentifier(),url);
		deletePidFromCache(pid);
    	return "You have created a message pid=" + pid.getIdentifier() + " and url=" + url;
    }
    
    /**
     * This method does the following:	
     *  - Create a {@link Pid} with new values
     *  - Add a PID in the queue to update it.
     * 	- Return the updated PID.
     * 
     *  Note: 
     *  - The actual editing of the PID in the GWDG service will proceed from the queue.
     * 
     * @param id
     * @param url
     * @return
     * @throws Exception
     */
    public String editPid(String id, String url) throws Exception
    {
    	Pid pid = new Pid(id, url);
    	PidQueue queue = new PidQueue();
    	queue.addInQueue(pid);
    	return "You have edited a message pid=" + pid.getIdentifier() + " and url=" + pid.getUrl();
    }
    
    /**
     * Return the first PID of the cache
     * @return
     * @throws Exception
     */
    public Pid getFirstPidFromCache() throws Exception
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
    public void savePidInCache(Pid pid) throws Exception
    {
    	String sql = DatabaseHelper.ADD_ELEMENT_STATEMENT;
    	sql = sql.replace("XXX_IDENTIFIER_XXX", pid.getIdentifier());
    	sql = sql.replace("XXX_URL_XXX", pid.getUrl());
    	sql = sql.replace("XXX_TIMESTAMP_XXX", DatabaseHelper.getTimeStamp());
    	Connection connection  = DatabaseHelper.getConnection();
    	Statement statement = connection.createStatement();
    	statement.executeUpdate(sql);
    	statement.close();
        connection.close();
    }
    
    /**
     * Delete a PID from the cache.
     * 
     * @param pid
     * @throws Exception
     */
    private void deletePidFromCache(Pid pid) throws Exception
    {
    	String sql = DatabaseHelper.REMOVE_ELEMENT_STATEMENT;
    	sql = sql.replace("XXX_IDENTIFIER_XXX", pid.getIdentifier());
    	Connection connection  = DatabaseHelper.getConnection();
    	Statement statement = connection.createStatement();
    	statement.executeUpdate(sql);
    	statement.close();
        connection.close();
    }
}
