package de.mpg.escidoc.services.pidcache.util;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

public class GwdgClient extends HttpClient 
{
	private static final String GWDG_PIDSERVICE_USER = "demo2";
    private static final String GWDG_PIDSERVICE_PASS = "Evaluierung";
    
    /**
     * Default constructor
     */
	public GwdgClient() 
	{
		super();
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
