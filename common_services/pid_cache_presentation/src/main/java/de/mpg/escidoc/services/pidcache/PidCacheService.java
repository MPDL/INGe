package de.mpg.escidoc.services.pidcache;

import javax.naming.InitialContext;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.PidServiceResponseVO;
import de.mpg.escidoc.services.pidcache.gwdg.GwdgClient;
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
	private InitialContext context = null;
	private XmlTransforming xmlTransforming = null;
	
	/**
	 * Default constructor
	 * @throws Exception
	 */
	public PidCacheService() throws Exception
	{
		cache = new Cache();
		queue = new Queue();
		gwdgPidService = new GwdgPidService();
		context = new InitialContext();
		xmlTransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
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
    	return transformToPidServiceResponse(pid, "create");
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
			return  transformToPidServiceResponse(pid, "view");
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
			return transformToPidServiceResponse(pid, "search");
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
		Pid pid = new Pid(id, url);
		queue.add(pid);
		return transformToPidServiceResponse(pid, "modify");
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
	
	private String transformToPidServiceResponse(Pid pid, String action) throws TechnicalException
	{
		PidServiceResponseVO pidServiceResponseVO = new PidServiceResponseVO();
		pidServiceResponseVO.setAction(action);
		pidServiceResponseVO.setCreator(GwdgClient.GWDG_PIDSERVICE_USER);
		pidServiceResponseVO.setIdentifier(pid.getIdentifier());
		pidServiceResponseVO.setUrl(pid.getUrl());
		pidServiceResponseVO.setUserUid("anonymous");
		pidServiceResponseVO.setInstitute("institute");
		pid.setContact("jon@doe.xx");
		pidServiceResponseVO.setMessage(action + "  PID " + pid.getIdentifier() + " with URL " + pid.getUrl());
		return xmlTransforming.transformToPidServiceResponse(pidServiceResponseVO);
	}
}
