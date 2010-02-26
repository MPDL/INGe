package de.mpg.escidoc.services.pidcache;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.pidcache.util.DatabaseHelper;


/**
 * Manage PID Cache
 * 
 * @author saquet
 *
 */
public class PidCacheManager 
{
	private String dummyUrl = null;
	
	private int cacheSize = 0;
	private int cacheMaximumSize = 0;
	
	
	public PidCacheManager() throws Exception
	{
		cacheMaximumSize = Integer.parseInt(PropertyReader.getProperty("escidoc.pid.cache.size.max"));
		dummyUrl = PropertyReader.getProperty("escidoc.pid.cache.dummy.url");
	}
	
	
	/**
	 * Check the cache: 
	 *  
	 *  - Check that the table exists
	 *  - Check that the cache is full
	 * 
	 * @throws Exception 
	 */
	private void checkCache() throws Exception
	{
		try 
		{
			
		} 
		catch (Exception e) 
		{
			DatabaseHelper.createTable();
		}
	}
	
	/**
	 * If the cache is not full, fills it with new dummy PID
	 */
	private void fillCacheWithDummyPid()
	{
		if (cacheSize < cacheMaximumSize) 
		{
			PidHandler pidHandler = new PidHandler();
			
			for (int i = 0; i < cacheMaximumSize - cacheSize; i++) 
			{
				try 
				{
					pidHandler.createPid(dummyUrl.concat(Long.toString(new Date().getTime())));
				} 
				catch (Exception e) 
				{
					throw new RuntimeException("Error Creating an item with GWDG service", e);
				}
			}
		}
	}

}
