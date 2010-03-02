/**
 * 
 */
package de.mpg.escidoc.services.pidcache.queue;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.pidcache.Pid;
import de.mpg.escidoc.services.pidcache.gwdg.PidHandler;
import de.mpg.escidoc.services.pidcache.init.Initializer;

/**
 * 
 * Manage the queue:
 * 	- Check whether queue has enqueued PID 
 * 	  (i.e waiting for an update at GWDG service)
 * 	- Update enqueued item at the GWDG service.
 * 
 * @author saquet
 *
 */
public class QueueManager 
{
	/**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Initializer.class);
	
	/**
	 * Run the manager.
	 * @throws Exception 
	 */
	public static void run() throws Exception
	{
		PidQueue queue = new PidQueue();
		Pid pid = queue.getFirstPidFromQueue();
		while (pid != null) 
		{
			if (update(pid)) 
			{
				queue.removeFromQueue(pid);
				pid = queue.getFirstPidFromQueue();
			}
			else
			{
				pid = null;
			}
		}
	}
	
	/**
	 * Update the {@link Pid} in the GWDG Service
	 * @param pid
	 * @throws Exception 
	 */
	private static boolean update(Pid pid) throws Exception
	{
		PidHandler handler = new PidHandler();
		try 
		{
			handler.updatePid(pid.getIdentifier(), pid.getUrl());
			return true;
		} 
		catch (Exception e) 
		{
			LOGGER.info("PID Manager at GWDG not available!");
		}
		return false;
	}
}
