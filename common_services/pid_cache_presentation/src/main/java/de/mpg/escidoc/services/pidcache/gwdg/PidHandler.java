package de.mpg.escidoc.services.pidcache.gwdg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import javax.naming.InitialContext;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.PidTaskParamVO;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Handler for the PID Manager at the GWDG
 * 
 * @author saquet
 *
 */
public class PidHandler 
{
	public static String GWDG_PIDSERVICE_CREATE = null;
    public static String GWDG_PIDSERVICE_VIEW = null;
    public static String GWDG_PIDSERVICE_FIND = null;
    public static String GWDG_PIDSERVICE = null;
	public static String GWDG_PIDSERVICE_EDIT = null;
    
    
	public PidHandler() 
	{
		try 
		{
			GWDG_PIDSERVICE = PropertyReader.getProperty("escidoc.pid.gwdg.service.url");
	    	GWDG_PIDSERVICE_CREATE = PropertyReader.getProperty("escidoc.pid.gwdg.create.path");
	    	GWDG_PIDSERVICE_VIEW = PropertyReader.getProperty("escidoc.pid.gwdg.view.path");
	    	GWDG_PIDSERVICE_FIND = PropertyReader.getProperty("escidoc.pid.gwdg.search.path");
	    	GWDG_PIDSERVICE_EDIT = PropertyReader.getProperty("escidoc.pid.gwdg.update.path");
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
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
		PostMethod create = new PostMethod(GWDG_PIDSERVICE.concat(GWDG_PIDSERVICE_CREATE));
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
		GetMethod retrieve = new GetMethod(GWDG_PIDSERVICE.concat(GWDG_PIDSERVICE_VIEW).concat("?pid=").concat(id));
    	
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
	 * 	- http://handle.gwdg.de:8080/pidservice/write/edit
	 * 
	 * @return
	 * @throws Exception 
	 */
	public String updatePid(String id, String url) throws Exception 
	{
		PostMethod update = new PostMethod(GWDG_PIDSERVICE.concat(GWDG_PIDSERVICE_EDIT).concat("?pid=").concat(id));
    	update.setParameter("url", url);
    	
    	GwdgClient client = new GwdgClient();
    	client.executeMethod(update);
    	
    	return update.getResponseBodyAsString();
	}

}
