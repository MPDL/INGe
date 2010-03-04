package de.mpg.escidoc.services.pidcache;

import de.mpg.escidoc.services.pidcache.gwdg.GwdgPidService;
import de.mpg.escidoc.services.pidcache.tables.Cache;
import de.mpg.escidoc.services.pidcache.tables.Queue;

/**
 * Implement the PID cache service
 *  
 * @author saquet
 *
 */
public class PidCacheService 
{
	private Pid pid = null;
	private Cache cache = null;
	private Queue queue = null;
	private GwdgPidService gwdgPidService = null;
	
	/**
	 * Default constructor
	 * @throws Exception
	 */
	public PidCacheService() throws Exception
	{
		cache = new Cache();
		queue = new Queue();
		gwdgPidService = new GwdgPidService();
	}
	
	 /**
     * This method does the following:
     *  - Take a PID from the cache
     *  - Change the URL of the PID
     *  - Put the PID in the queue
     *  - Delete the PID from the cache
     *  - Return the PID
     *  
     *  Notes: 
     *  - The actual editing of the PID in the GWDG service will be proceed from the queue
     *  - The cache will be completed by a new PID generated from {@link CacheProcess}
     * 
     * @param url The URL to be registered.
     * 
     * @return The PID.
     **/
	public String create(String url)throws Exception
	{
		Pid pid = cache.getFirst();
		pid.setUrl(url);
		queue.add(pid);
		cache.remove(pid);
    	return "You have created a message pid=" + pid.getIdentifier() + " and url=" + url;
	}
	
	/**
	 * Retrieve a PID from the GWDG PID service:
	 *  - Check if PID still in queue, if yes, return it
	 *  - Check if GWDG PID service available, if no throw Exception
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String retrieve(String id) throws Exception
	{
		pid = queue.retrieve(id);
		if (pid != null) 
		{
			return pid.asXmlString();
		}
		if (!gwdgPidService.available()) 
		{
			throw new RuntimeException("Service Not available");
		}
		return gwdgPidService.retrieve(id);
	}
	
	/**
	 * Search a PID:
	 * 	- Search first in {@link Queue} if PID still in it
	 *  - Check then if GWDG service available
	 *  - Search with GWDG service.
	 * @param url
	 * @return
	 */
	public String search(String url) throws Exception
	{
		pid = queue.search(url);
		if (pid != null) 
		{
			return pid.asXmlString();
		}
		if (!gwdgPidService.available()) 
		{
			throw new RuntimeException("Service Not available");
		}
		return  gwdgPidService.search(url);
	}
	
	/**
	 * Update a PID
	 * @param id
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String update(String id, String url) throws Exception
	{
		pid.setIdentifier(id);
		pid.setUrl(url);
		queue.add(pid);
		return pid.asXmlString();
	}
	
	/**
	 * Should a PID be removable?
	 * @param id
	 * @return
	 */
	public String delete(String id)
	{
		return "Delete not possble for a PID";
	}
}
