/**
 * 
 */
package de.mpg.escidoc.services.pidcache.process;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.pidcache.Pid;
import de.mpg.escidoc.services.pidcache.gwdg.GwdgPidService;
import de.mpg.escidoc.services.pidcache.init.RefreshTask;
import de.mpg.escidoc.services.pidcache.tables.Queue;

/**
 * 
 * Process managing the {@link Queue}
 * 	- Check whether queue has enqueued PID 
 * 	  (i.e waiting for an update at GWDG service)
 * 	- Update enqueued item at the GWDG service.
 * 
 * @author saquet
 *
 */
public class QueueProcess 
{    
	private static final Logger logger = Logger.getLogger(QueueProcess.class);
	private XmlTransforming xmlTransforming = null;
	/**
     * Default constructor
     */
    public QueueProcess() throws Exception
    {
    	InitialContext context = new InitialContext();
    	xmlTransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
    }
	
	/**
	 * Empty the {@link Queue} if:
	 *  - The service at the GWDG is available
	 *  - And if: 	- the PID exists : update the PID with the new URL 
	 *  			- The PID doesn't exist: Some Reporting...
	 * @throws Exception 
	 */
	public void empty() throws Exception
	{
		Queue queue = new Queue();
		Pid pid = queue.getFirst();
		GwdgPidService gwdgPidService = new GwdgPidService();
		if (gwdgPidService.available()) 
		{
			while (pid != null) 
			{
				try 
				{
					String pidXml = gwdgPidService.update(pid.getIdentifier(), pid.getUrl());
					xmlTransforming.transformToPidServiceResponse(pidXml);
				} 
				catch (Exception e) 
				{
					logger.debug("Error, PID can not be updated on GWDG service.");
				}
				queue.remove(pid);
				pid = queue.getFirst();
			}
		}
		else
		{
			logger.debug("PID manager at GWDG not available.");
		}
	}
}
