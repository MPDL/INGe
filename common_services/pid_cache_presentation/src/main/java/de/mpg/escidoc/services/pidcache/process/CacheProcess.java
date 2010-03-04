package de.mpg.escidoc.services.pidcache.process;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.pidcache.xmltransforming;
import de.mpg.escidoc.services.pidcache.gwdg.GwdgPidService;
import de.mpg.escidoc.services.pidcache.init.RefreshTask;
import de.mpg.escidoc.services.pidcache.tables.Cache;
import de.mpg.escidoc.services.pidcache.util.DatabaseHelper;


/**
 * 
 * Process managing the {@link Cache}:
 *  - Check if cache full (i.e. has enough PID available)
 *  - Fill cache with new PID when needed.
 * 
 * @author saquet
 *
 */
public class CacheProcess 
{
	 private static String DUMMY_URL = null;
	 private static final Logger logger = Logger.getLogger(CacheProcess.class);
	
	/**
	 * Manage the cache
	 * @throws Exception
	 */
	public CacheProcess() throws Exception
	{
		DUMMY_URL = PropertyReader.getProperty("escidoc.pid.cache.dummy.url");
	}

	/**
	 * If the cache is not full, fills it with new dummy PID
	 */
	public void fill() throws Exception
	{
		Cache cache = new Cache();
		GwdgPidService gwdgPidService = new GwdgPidService();
		xmltransforming xmltransforming = new xmltransforming();
		long current = 0;
		if (gwdgPidService.available()) 
		{
			while(Cache.SIZE_MAX > cache.size() 
					&& current != new Date().getTime())
			{
				current = new Date().getTime();
				String pidXml = gwdgPidService.create(DUMMY_URL.concat(Long.toString(current)));
				cache.add(xmltransforming.transFormToPid(pidXml));
			}
		}
		else 
		{
			 logger.info("PID manager at GWDG not available.");
		}
		
	}
}
