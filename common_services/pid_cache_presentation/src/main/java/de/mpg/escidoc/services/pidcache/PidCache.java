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

package de.mpg.escidoc.services.pidcache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Timer;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import de.mpg.escidoc.services.pidcache.util.DatabaseHelper;
import de.mpg.escidoc.services.pidcache.util.GwdgClient;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class PidCache
{
    public static final String GWDG_PIDSERVICE_CREATE = "http://handle.gwdg.de:8080/pidservice/write/create";
    public static final String GWDG_PIDSERVICE_VIEW = "http://handle.gwdg.de:8080/pidservice/read/view";
    public static final String GWDG_PIDSERVICE_FIND = "http://handle.gwdg.de:8080/pidservice/read/search";
	

    /**
     * This method does the following:
     *  - Take a Pid from the cache
     *  - Invoke the following (asynchroous) workflow:
     *      - Change the URL of the Pid in the Handle system.
     *      - Add a new PID to the cache.
     *  - Return the Pid
     * 
     * @param url The URL to be registered.
     * 
     * @return The Pid.
     */
    public Pid assignPid(String url) throws Exception
    {
    	Pid pid = new Pid();
    	pid.setUrl(url);	
    	pid.setIdentifier("dummy/1/to/REMOVE");
		//pid = this.createPid(pid);
		
		this.savePidInCache(pid);
		
		Connection connection  = DatabaseHelper.getConnection();
		
		PreparedStatement pst = connection.prepareStatement(DatabaseHelper.GET_ID_FIRST_ELEMENT_STATEMENT);
		pst.setMaxRows(1);
		
		pst.executeQuery();
		
		ResultSet resultSet = pst.getResultSet();

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
		
		pst.close();
		
    	return pid;
    }
        
    /**
     * This method calls the PID Handler service of the GWDG.
     * 
     * @param pidString
     * @return
     */
    public String retrievePid(String pidString) throws Exception
    {
    	GetMethod retrieve = new GetMethod(GWDG_PIDSERVICE_CREATE.concat("?pid=").concat(pidString));
    	
    	GwdgClient client = new GwdgClient();
    	
    	client.executeMethod(retrieve);
    	
    	return retrieve.getResponseBodyAsString();
    }
    
    /**
     * Save a PID into the cache.
     * @param pid
     * @throws Exception
     */
    private void savePidInCache(Pid pid) throws Exception
    {
    	String sql = DatabaseHelper.ADD_ELEMENT_STATEMENT;
    	sql = sql.replace("XXX_IDENTIFER_XXX", pid.getIdentifier());
    	sql = sql.replace("XXX_TIMESTAMP_XXX", "2010");
    	
    	Connection connection  = DatabaseHelper.getConnection();
    	Statement statement = connection.createStatement();
    	statement.executeUpdate(sql);
		
    	statement.close();
        connection.close();
    }
    
    private void deletePidFromCache(Pid pid)
    {
    	
    }
    
    /**
     * Create a PID:
     * 
     *  - Calls the PID Handler Service of the GWDG
     *  - Return the Identifier of the PID created
     *  
     * @param pid not created.
     * @return pid created
     * @throws Exception
     */
    private Pid createPid(Pid pid) throws Exception
    {
    	PostMethod create = new PostMethod(GWDG_PIDSERVICE_CREATE);
    	create.setParameter("url", pid.getUrl());
    	
    	GwdgClient client = new GwdgClient();
    	client.executeMethod(create);
    	
    	String pidXml = create.getResponseBodyAsString();
    	
    	String id = pidXml.split("<pid>")[1].split("</pid>")[0];
    	
    	pid.setIdentifier(id);
    	
    	return pid;
    }
}
