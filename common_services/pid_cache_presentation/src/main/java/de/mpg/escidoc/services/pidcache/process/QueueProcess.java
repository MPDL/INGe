/**
 * 
 */
package de.mpg.escidoc.services.pidcache.process;

import org.apache.log4j.Logger;

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
	/**
     * Default constructor
     */
    public QueueProcess()
    {
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
				if (pid.exists()) 
				{
					if (pid.hasFreeUrl()) 
					{
						gwdgPidService.update(pid.getIdentifier(), pid.getUrl());
						queue.remove(pid);
						pid = queue.getFirst();
					} 
					else 
					{
						// We have a problem!!! URL has been already declared for another PID
						// Some Reporting should be done from here
						queue.remove(pid);
						pid = queue.getFirst();
					}
				}
				else
				{
					queue.remove(pid);
					pid = queue.getFirst();
				}
			}
		}
		else
		{
			logger.info("PID manager at GWDG not available.");
		}
	}
}
