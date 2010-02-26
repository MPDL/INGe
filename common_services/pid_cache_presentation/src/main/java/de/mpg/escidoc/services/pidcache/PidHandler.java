package de.mpg.escidoc.services.pidcache;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import de.mpg.escidoc.services.pidcache.util.GwdgClient;

/**
 * Handler for the PID Manager at the GWDG
 * 
 * @author saquet
 *
 */
public class PidHandler 
{
	public static final String GWDG_PIDSERVICE_CREATE = "http://handle.gwdg.de:8080/pidservice/write/create";
    public static final String GWDG_PIDSERVICE_VIEW = "http://handle.gwdg.de:8080/pidservice/read/view";
    public static final String GWDG_PIDSERVICE_FIND = "http://handle.gwdg.de:8080/pidservice/read/search";
    
    
	public PidHandler() 
	{
		
	}
	
	/**
	 * Calls GWDG PID manager interface:
	 * 
	 * 	- http://handle.gwdg.de:8080/pidservice/write/create
	 * 
	 * @param url: 
	 * @return
	 */
	public String createPid(String url) throws Exception
	{
		PostMethod create = new PostMethod(GWDG_PIDSERVICE_CREATE);
    	create.setParameter("url", url);
    	
    	GwdgClient client = new GwdgClient();
    	client.executeMethod(create);
    	
    	return create.getResponseBodyAsString();
	}
	
	/**
	 * Calls GWDG PID manager interface:
	 * 
	 * 	- http://handle.gwdg.de:8080/pidservice/read/view
	 * 
	 * @return
	 */
	public String retrievePid(String id) throws Exception
	{
		GetMethod retrieve = new GetMethod(GWDG_PIDSERVICE_VIEW.concat("?pid=").concat(id));
    	
    	GwdgClient client = new GwdgClient();
    	
    	client.executeMethod(retrieve);
    	
    	return retrieve.getResponseBodyAsString();
	}
	
	/**
	 * Calls GWDG PID manager interface:
	 * 
	 * 	- http://handle.gwdg.de:8080/pidservice/read/search
	 * 
	 * @param url
	 * @return
	 */
	public String searchPid(String url)
	{
		return null;
	}
	
	/**
	 * Calls GWDG PID manager interface:
	 * 
	 * 	- http://handle.gwdg.de:8080/pidservice/
	 * 
	 * @return
	 */
	public boolean deletePid(String identifier)
	{
		return false;
	}
	
	/**
	 * Calls GWDG PID manager interface:
	 * 
	 * 	- http://handle.gwdg.de:8080/pidservice/
	 * 
	 * @return
	 */
	public String updatePid(String pidXml)
	{
		return null;
	}

}
