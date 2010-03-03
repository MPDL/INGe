package de.mpg.escidoc.services.pidcache.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.pidcache.xmltransforming;
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
	private int cacheMaximumSize = 0;
	
	/**
	 * Manage the cache
	 * @throws Exception
	 */
	public PidCacheManager() throws Exception
	{
		cacheMaximumSize = Integer.parseInt(PropertyReader.getProperty("escidoc.pid.cache.size.max"));
		dummyUrl = PropertyReader.getProperty("escidoc.pid.cache.dummy.url");
	}

	/**
	 * If the cache is not full, fills it with new dummy PID
	 */
	public void fill() throws Exception
	{
		PidHandler pidHandler = new PidHandler();
		PidCache cache = new PidCache();
		xmltransforming xmltransforming = new xmltransforming();
		long current = 0;
		while(cacheMaximumSize > cacheSize() && current != new Date().getTime())
		{
			current = new Date().getTime();
			String pidXml = pidHandler.createPid(dummyUrl.concat(Long.toString(current)));
			cache.savePidInCache(xmltransforming.transFormToPid(pidXml));
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
	 		size = resultSet.getInt("size");
	    }
	 	connection.close();
    	statement.close();
		return size;
	}

}
