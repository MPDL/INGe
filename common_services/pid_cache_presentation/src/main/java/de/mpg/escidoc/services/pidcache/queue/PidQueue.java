package de.mpg.escidoc.services.pidcache.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import de.mpg.escidoc.services.pidcache.Pid;
import de.mpg.escidoc.services.pidcache.gwdg.PidHandler;
import de.mpg.escidoc.services.pidcache.util.DatabaseHelper;

/**
 * Queue of PID awaiting to be updated in GWDG PID service
 * 
 * @author saquet
 *
 */
public class PidQueue 
{	

	public Pid getFirstPidFromQueue() throws Exception
	{
		Pid pid = new Pid();
    	Connection connection  = DatabaseHelper.getConnection();
    	Statement statement = connection.createStatement();
    	statement.setMaxRows(1);
    	ResultSet resultSet = statement.executeQuery(DatabaseHelper.GET_QUEUE_FIRST_ELEMENT_STATEMENT);
    	if (resultSet.next())
    	{
    		pid.setIdentifier(resultSet.getString("identifier"));
 			pid.setUrl( resultSet.getString("url"));
 	 		connection.close();
        }
        else
        {
        	connection.close();
        	return null;
        }
    	statement.close();
		return pid;
	}
	
	/**
	 * Add a PID to the queue
	 * 
	 * @param pid
	 */
	public void addInQueue(Pid pid) throws Exception
	{
		checkDuplicateInQueue(pid.getUrl());
		PidHandler handler = new PidHandler();
		handler.checkDuplicateAtGwdg(pid.getUrl());
		String sql = DatabaseHelper.ADD_QUEUE_ELEMENT_STATEMENT;
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
	 * Remove a PID from the queue
	 * 
	 * @param pid
	 */
	public void removeFromQueue(Pid pid) throws Exception
	{
		String sql = DatabaseHelper.REMOVE_QUEUE_ELEMENT_STATEMENT;
    	sql = sql.replace("XXX_IDENTIFIER_XXX", pid.getIdentifier());
    	
    	Connection connection  = DatabaseHelper.getConnection();
    	Statement statement = connection.createStatement();
    	statement.executeUpdate(sql);
		
    	statement.close();
        connection.close();
	}
	
	
	/**
	 * Check if that URL has already a PID.
	 * @param pid
	 * @throws Exception
	 */
	private void checkDuplicateInQueue(String url) throws Exception
	{
		String sql = DatabaseHelper.GET_QUEUE_ELEMENT_URL_STATEMENT;
    	sql = sql.replace("XXX_URL_XXX", url);
    	Connection connection  = DatabaseHelper.getConnection();
    	Statement statement = connection.createStatement();
    	ResultSet resultSet = statement.executeQuery(sql);
    	if (resultSet.next()) 
    	{
    		statement.close();
            connection.close();
    		throw new RuntimeException("This URL (" + url + ") has already a PID!");
		}
    	statement.close();
        connection.close();
	}

}
