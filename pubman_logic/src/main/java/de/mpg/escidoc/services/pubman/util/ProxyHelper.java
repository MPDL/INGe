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

package de.mpg.escidoc.services.pubman.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 *
 * Utility class for pubman logic.
 *
 * @author makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ProxyHelper
{
    private static String adminUserHandle = null;
    private static Date loginTime = null;

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ProxyHelper.class);

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
		String proxyHost;
		String proxyPort;
		String nonProxyHosts;
		try {
			proxyHost = PropertyReader.getProperty("http.proxyHost");
			proxyPort = PropertyReader.getProperty("http.proxyPort");
			nonProxyHosts = PropertyReader.getProperty("http.nonProxyHosts");
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Cannot read proxy configuration:", e);
		}
		
		if (proxyHost != null) 
		{
			
			HostConfiguration hc = httpClient.getHostConfiguration();
			if (nonProxyHosts != null && !nonProxyHosts.trim().equals("")) 
			{
				nonProxyHosts = nonProxyHosts.replaceAll("\\.", "\\\\.");
				nonProxyHosts = nonProxyHosts.replaceAll("\\*", "");
				nonProxyHosts = nonProxyHosts.replaceAll("\\?", "\\\\?");
				Pattern nonProxyPattern = Pattern.compile(nonProxyHosts);
				Matcher nonProxyMatcher = nonProxyPattern.matcher(url);
				if (nonProxyMatcher.find()) 
				{
					hc.setProxyHost(null);
				} 
				else 
				{
					hc.setProxy(proxyHost, Integer.valueOf(proxyPort));
				}
			} 
			else 
			{
				hc.setProxy(proxyHost, Integer.valueOf(proxyPort));
			}
		}
	}    	
}
