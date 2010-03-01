package de.mpg.escidoc.services.pidcache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import de.mpg.escidoc.services.pidcache.util.DatabaseHelper;

/**
 * Queue of PID awaiting to be updated in GWDG PID service
 * 
 * @author saquet
 *
 */
public class PidQueue 
{	
	/**
	 * Add a PID to the queue
	 * 
	 * @param pid
	 */
	public void addInQueue(Pid pid) throws Exception
	{
		String sql = DatabaseHelper.ADD_QUEUE_ELEMENT_STATEMENT;
    	sql = sql.replace("XXX_IDENTIFER_XXX", pid.getIdentifier());
    	sql = sql.replace("XXX_URL_XXX", pid.getUrl());
    	sql = sql.replace("XXX_TIMESTAMP_XXX", DatabaseHelper.getTimeStamp());
    	
    	Connection connection  = DatabaseHelper.getConnection();
    	Statement statement = connection.createStatement();
    	statement.executeUpdate(sql);
		
    	statement.close();
        connection.close();
	}
	
	/**
	 * Remove a PID from the queue
	 * 
	 * @param pid
	 */
	public void removeFromQueue(Pid pid) throws Exception
	{
		String sql = DatabaseHelper.REMOVE_QUEUE_ELEMENT_STATEMENT;
    	sql = sql.replace("XXX_IDENTIFER_XXX", pid.getIdentifier());
    	
    	Connection connection  = DatabaseHelper.getConnection();
    	Statement statement = connection.createStatement();
    	statement.executeUpdate(sql);
		
    	statement.close();
        connection.close();
	}

}
