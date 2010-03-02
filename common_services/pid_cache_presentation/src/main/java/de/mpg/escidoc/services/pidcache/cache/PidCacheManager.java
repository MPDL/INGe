package de.mpg.escidoc.services.pidcache.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.pidcache.gwdg.PidHandler;
import de.mpg.escidoc.services.pidcache.util.DatabaseHelper;


/**
 * 
 * Manage PID Cache:
 *  - Check if cache full (i.e. has enough PID available)
 *  - Fill cache with new PID when needed.
 * 
 * @author saquet
 *
 */
public class PidCacheManager 
{
	private static String dummyUrl = null;
	private static int cacheMaximumSize = 0;
	private static int cacheSize = 0;
	
	/**
	 * Manage the cache
	 * @throws Exception
	 */
	public static void run() throws Exception
	{
		cacheMaximumSize = Integer.parseInt(PropertyReader.getProperty("escidoc.pid.cache.size.max"));
		dummyUrl = PropertyReader.getProperty("escidoc.pid.cache.dummy.url");
		System.out.println(cacheSize());
//		if (!cacheFull()) 
//		{
//			fillCacheWithDummyPid();
//		}
	}
	
	/**
	 * Check the cache: 
	 *  
	 *  - Check that the cache is full
	 * 
	 * @throws Exception 
	 */
	private static boolean cacheFull() throws Exception
	{
		PidCache cache = new PidCache();
		if (cache.getFirstPidFromCache() != null) 
		{
			return true;
		}
		return false;
	}
	
	/**
	 * If the cache is not full, fills it with new dummy PID
	 */
	private static void fillCacheWithDummyPid() throws Exception
	{
		PidHandler pidHandler = new PidHandler();
		long current = new Date().getTime();
		while(cacheMaximumSize > cacheSize && current != new Date().getTime())
		{
			current = new Date().getTime();
			pidHandler.createPid(dummyUrl.concat(Long.toString(current)));
		}
	}
	
	/**
	 * Return cache size.
	 * @return int
	 */
	private static int cacheSize() throws Exception
	{
    	Connection connection  = DatabaseHelper.getConnection();
    	Statement statement = connection.createStatement();
    	ResultSet resultSet = statement.executeQuery(DatabaseHelper.CACHE_SIZE_STATEMENT);
    	int size = 0;
	 	if (resultSet.next())
	 	{	
	 		size = resultSet.getInt(0);
	    }
	 	connection.close();
    	statement.close();
		return size;
	}

}
