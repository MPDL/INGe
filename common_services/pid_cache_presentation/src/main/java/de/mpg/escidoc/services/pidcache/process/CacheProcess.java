package de.mpg.escidoc.services.pidcache.process;

import java.util.Date;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.PidServiceResponseVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.pidcache.Pid;
import de.mpg.escidoc.services.pidcache.gwdg.GwdgPidService;
import de.mpg.escidoc.services.pidcache.tables.Cache;


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
	 private InitialContext context = null;
	 private XmlTransforming xmlTransforming = null;
	
	/**
	 * Manage the cache
	 * @throws Exception
	 */
	public CacheProcess() throws Exception
	{
		DUMMY_URL = PropertyReader.getProperty("escidoc.pidcache.dummy.url");
		context = new InitialContext();
		xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
	}

	/**
	 * If the cache is not full, fills it with new dummy PID
	 */
	public void fill() throws Exception
	{
		this.fill(1);
	}

    public void fill(int number) throws Exception
    {
        Cache cache = new Cache();
        GwdgPidService gwdgPidService = new GwdgPidService();
        long current = 0;
        if (gwdgPidService.available()) 
        {
            int i = 0;
            while(Cache.SIZE_MAX > cache.size() 
                    && current != new Date().getTime() 
                    && i < number)
            {
                current = new Date().getTime();
                String pidXml = gwdgPidService.create(DUMMY_URL.concat(Long.toString(current)));
                PidServiceResponseVO pidServiceResponseVO = xmlTransforming.transformToPidServiceResponse(pidXml);
                Pid pid = new Pid(pidServiceResponseVO.getIdentifier(), pidServiceResponseVO.getUrl());
                cache.add(pid);
                i++;
            }
        }
        else 
        {
             logger.info("PID manager at GWDG not available.");
        }
        
    }

    public boolean isFull() throws Exception
    {
        Cache cache = new Cache();
        return (Cache.SIZE_MAX == cache.size());
    }
}
