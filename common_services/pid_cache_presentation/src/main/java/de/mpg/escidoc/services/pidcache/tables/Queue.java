package de.mpg.escidoc.services.pidcache.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import de.mpg.escidoc.services.pidcache.Pid;
import de.mpg.escidoc.services.pidcache.util.DatabaseHelper;

/**
 * Queue of PID awaiting to be updated in GWDG PID service
 * 
 * @author saquet
 *
 */
public class Queue 
{	
	/**
	 * Default constructor
	 */
	public Queue() throws Exception
	{
	}
	
	public Pid getFirst() throws Exception
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
	public void add(Pid pid) throws Exception
	{
		if (!pid.hasFreeUrl()) 
		{
			throw new RuntimeException("This URL (" + pid.getUrl() + ") has already a PID!");
		}
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
	public void remove(Pid pid) throws Exception
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
	 * Retrieve a PID from the queue.
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Pid retrieve(String id) throws Exception
	{
		Pid pid = null;
		String sql = DatabaseHelper.RETRIEVE_QUEUE_ELEMENT_STATEMENT;
    	sql = sql.replace("XXX_IDENTIFIER_XXX", id);
    	Connection connection  = DatabaseHelper.getConnection();
		Statement statement = connection.createStatement();
    	ResultSet resultSet = statement.executeQuery(sql);
    	if (resultSet.next()) 
    	{
    		pid = new Pid(resultSet.getString("identifier"), resultSet.getString("url"));
    		statement.close();
            connection.close();
		}
    	statement.close();
        connection.close();
        return pid;
	}
	
	/**
	 * Search a PID from his URL in the {@link Queue}:
	 * 	- return the PID if found
	 *  - return null if not found
	 *  
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public Pid search(String url) throws Exception
	{
		Pid pid = null;
		String sql = DatabaseHelper.GET_QUEUE_ELEMENT_URL_STATEMENT;
    	sql = sql.replace("XXX_URL_XXX", url);
    	Connection connection  = DatabaseHelper.getConnection();
		Statement statement = connection.createStatement();
    	ResultSet resultSet = statement.executeQuery(sql);
    	if (resultSet.getFetchSize() > 1) 
    	{
			throw new RuntimeException("Duplicate detected for URI: " + url);
		}
    	if (resultSet.next()) 
    	{
    		pid = new Pid(resultSet.getString("identifier"), resultSet.getString("url"));
    		statement.close();
            connection.close();
		}
    	statement.close();
        connection.close();
        return pid;
	}
}
