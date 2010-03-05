package de.mpg.escidoc.services.pidcache.gwdg;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

import de.mpg.escidoc.services.framework.PropertyReader;

public class GwdgClient extends HttpClient 
{
	public static String GWDG_PIDSERVICE_USER = "demo2";
    private static String GWDG_PIDSERVICE_PASS = "Evaluierung";
    
    /**
     * Default constructor
     * @throws URISyntaxException 
     * @throws IOException 
     */
	public GwdgClient() throws Exception
	{
		super();
		GWDG_PIDSERVICE_USER = PropertyReader.getProperty("escidoc.pid.gwdg.user.login");
		GWDG_PIDSERVICE_PASS = PropertyReader.getProperty("escidoc.pid.gwdg.user.password");
		this.getParams().setAuthenticationPreemptive(true);
    	Credentials defaultcreds = new UsernamePasswordCredentials(GWDG_PIDSERVICE_USER, GWDG_PIDSERVICE_PASS);
    	this.getState().setCredentials(new AuthScope(AuthScope.ANY), defaultcreds);    	
	}
	
	/**
	 * Constructor with Specific {@link AuthScope} (Not useful so far)
	 * 
	 * For instance:
	 * - host = handle.gwdg.de
	 * - port =  8080
	 * - realm = pidservice/read/view
	 * 
	 * @param host
	 * @param port
	 * @param realm
	 */
	public GwdgClient(String host, int port, String realm)
	{
		super();
		this.getParams().setAuthenticationPreemptive(true);
    	Credentials defaultcreds = new UsernamePasswordCredentials(GWDG_PIDSERVICE_USER, GWDG_PIDSERVICE_PASS);
    	this.getState().setCredentials(new AuthScope(host, port, realm), defaultcreds);
	}
}
