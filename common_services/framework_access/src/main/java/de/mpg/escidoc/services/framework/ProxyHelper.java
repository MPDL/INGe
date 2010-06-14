/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.framework;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.log4j.Logger;


/**
 *
 * Utility class for pubman logic.
 *
 * @author makarenko (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3675 $ $LastChangedDate: 2010-05-27 16:13:35 +0200 (Do, 27 Mai 2010) $
 *
 */
/**
 * @author vlad
 *
 */
public class ProxyHelper
{
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ProxyHelper.class);

    static String proxyHost = null;
    static String proxyPort = null;
    static String nonProxyHosts = null;    
    static Pattern nonProxyPattern = null;
    
    static boolean flag = false;  

	/**
     * check if proxy has to get used for given url.
     * If yes, set ProxyHost in httpClient
     *
     * @param url url
     *
     * @throws Exception
     */
	public static void setProxy(final HttpClient httpClient, final String url) 
	{
		
		getProxyProperties();
		
		if (proxyHost != null) 
		{
			
			HostConfiguration hc = httpClient.getHostConfiguration();
			
			if ( findUrlInNonProxyHosts( url ) )
			{
				hc.setProxyHost(null);
			}
			else 
			{
				hc.setProxy(proxyHost, Integer.valueOf(proxyPort));
			}
		}
	}
	
    /**
     * Returns <code>java.net.Proxy</code> class for <code>java.net.URL.openConnection</code>
     * creation
     *
     * @param url url
     *
     * @throws Exception
     */	
	public static Proxy getProxy(final String url) 
	{
		Proxy proxy = Proxy.NO_PROXY;
		
		getProxyProperties();
		
		if (proxyHost != null) 
		{
			if ( ! findUrlInNonProxyHosts( url ) )
			{
				SocketAddress proxyAddress = new InetSocketAddress( proxyHost, Integer.valueOf(proxyPort) );
				proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
			}
		}
		
		return proxy;
	}
	
	/**
	 *
	 * Wrapper for executeMethod with Proxy
	 *  
	 * @param client, methopd
	 * @throws IOException 
	 * @throws HttpException 
	 */	
	public static int executeMethod(HttpClient client, HttpMethod method) throws HttpException, IOException  
	{
		setProxy(client, method.getURI().toString());
		return ProxyHelper.executeMethod(client, method);
	}
	
	/**
	 * Returns <code>java.net.URLConnection</code> with the Proxy settings
	 * creation
	 *
	 * @param url url
	 * @throws IOException 
	 *
	 * @throws Exception
	 * @return URLConnection
	 */	
	public static URLConnection openConnection(final URL url) throws IOException 
	{
		
		return url.openConnection(getProxy(url.toString()));
	}	
	
	
	
	/**
	 * Read proxy properties, set nonProxyPattern   
	 */
	private static void getProxyProperties()
	{
		if (flag) return;
		try 
		{
			proxyHost = PropertyReader.getProperty("http.proxyHost");
			proxyPort = PropertyReader.getProperty("http.proxyPort");
			nonProxyHosts = PropertyReader.getProperty("http.nonProxyHosts");
			if (nonProxyHosts != null && !nonProxyHosts.trim().equals(""))
			{
				String nph = nonProxyHosts; 
				nph = nph
						.replaceAll("\\.", "\\\\.")
						.replaceAll("\\*", "")
						.replaceAll("\\?", "\\\\?");
				nonProxyPattern = Pattern.compile(nph);
				
			}
			flag = true;			
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Cannot read proxy configuration:", e);
		}
	}
	

	/**
	 * Find  <code>url</code> in the list of the nonProxyHosts 
	 * @param url
	 * @return <code>true</code> if <code>url</code> is found, <code>false</code> otherwise
	 */
	private static boolean findUrlInNonProxyHosts(String url)
	{
		getProxyProperties();
		
		if (nonProxyPattern != null) 
		{
			Matcher nonProxyMatcher = nonProxyPattern.matcher(url);
			
			return nonProxyMatcher.find(); 
		}	
		else
		{
			return false;
		}
		
	}
	
}	
