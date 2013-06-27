package de.mpg.escidoc.services.pidcache.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
    private static final Logger logger = Logger.getLogger(Queue.class);
    private static Queue instance = null;
    
    public synchronized static Queue getInstance()
    {
        if (instance == null)
        {
            instance = new Queue();
        }
        return instance;
    }
	
	private Queue()
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
	
	public List<Pid> getFirstBlock(int size) throws Exception
    {
	    List<Pid> pids = new ArrayList<Pid>();
	    
        Connection connection  = DatabaseHelper.getConnection();
        Statement statement = connection.createStatement();
        statement.setMaxRows(size);
        
        ResultSet resultSet = statement.executeQuery(DatabaseHelper.GET_QUEUE_FIRST_ELEMENT_STATEMENT);
        while (resultSet.next())
        {
            Pid pid = new Pid();
            pid.setIdentifier(resultSet.getString("identifier"));
            pid.setUrl( resultSet.getString("url"));
            
            pids.add(pid);           
        }
        statement.close();
        connection.close();
        logger.debug("getFirstBlock of queue returning " + pids.size() + " pids");
        return pids;
    }
	
	/**
	 * Add a PID to the queue
	 * 
	 * @param pid
	 */
	public void add(Pid pid) throws Exception
	{	    
    	Connection connection  = DatabaseHelper.getConnection();
		
		PreparedStatement stmt = connection.prepareStatement(DatabaseHelper.ADD_QUEUE_ELEMENT_STATEMENT);
		stmt.setString(1, pid.getIdentifier());
		stmt.setString(2, pid.getUrl());
		stmt.setString(3, DatabaseHelper.getTimeStamp());
		
    	stmt.executeUpdate();
    	stmt.close();
        connection.close();
	}
	
	/**
	 * Remove a PID from the queue
	 * 
	 * @param pid
	 */
	public void remove(Pid pid) throws Exception
	{
    	Connection connection  = DatabaseHelper.getConnection();
    	
    	PreparedStatement stmt = connection.prepareStatement(DatabaseHelper.REMOVE_QUEUE_ELEMENT_STATEMENT);
        stmt.setString(1, pid.getIdentifier());
        
        stmt.executeUpdate();
        stmt.close();
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
    	
    	Connection connection  = DatabaseHelper.getConnection();
    	PreparedStatement stmt = connection.prepareStatement(DatabaseHelper.RETRIEVE_QUEUE_ELEMENT_STATEMENT);
        stmt.setString(1, id);
        
    	ResultSet resultSet = stmt.executeQuery();
    	if (resultSet.next()) 
    	{
    		pid = new Pid(resultSet.getString("identifier"), resultSet.getString("url"));
    		stmt.close();
            connection.close();
		}
    	stmt.close();
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

    	Connection connection  = DatabaseHelper.getConnection();
    	
    	PreparedStatement stmt = connection.prepareStatement(DatabaseHelper.GET_QUEUE_ELEMENT_URL_STATEMENT);
        stmt.setString(1, url);
        
    	ResultSet resultSet = stmt.executeQuery();
    	if (resultSet.getFetchSize() > 1) 
    	{
			throw new RuntimeException("Duplicate detected for URI: " + url);
		}
    	if (resultSet.next()) 
    	{
    		pid = new Pid(resultSet.getString("identifier"), resultSet.getString("url"));
    		stmt.close();
            connection.close();
		}
    	stmt.close();
        connection.close();
        return pid;
	}
	
	public boolean isEmpty() throws Exception
	{
	    return this.size() == 0;
	}
	
	/**
     * Current Size of the queue
     * @return
     * @throws Exception
     */
    public int size() throws Exception
    {
        Connection connection  = DatabaseHelper.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(DatabaseHelper.QUEUE_SIZE_STATEMENT);
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
