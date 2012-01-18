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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.xerces.impl.dv.util.Base64;

import de.mpg.escidoc.util.Util;

/**
 * TODO Description
 * 
 * @author Matthias Walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class Login
{
	private final String FRAMEWORK_URL;
	private HttpClient client;

	public Login(HttpClient httpClient, final String frameworkUrl)
	{
		this.FRAMEWORK_URL = frameworkUrl;
		this.client = httpClient;
	}

	private Cookie passSecurityCheck()
	{
		String loginName = Util.input("Enter Username: ");
		String loginPassword = Util.input("Enter Password: ");
		int delim1 = this.FRAMEWORK_URL.indexOf("//");
		int delim2 = this.FRAMEWORK_URL.indexOf(":", delim1);
		String host;
		int port;
		if (delim2 > 0)
		{
			host = this.FRAMEWORK_URL.substring(delim1 + 2, delim2);
			port = Integer.parseInt(this.FRAMEWORK_URL.substring(delim2 + 1));
		}
		else
		{
			host = this.FRAMEWORK_URL.substring(delim1 + 2);
			port = 80;
		}

		PostMethod post = new PostMethod(this.FRAMEWORK_URL + "/aa/j_spring_security_check");
		post.addParameter("j_username", loginName);
		post.addParameter("j_password", loginPassword);
		try
		{
			this.client.executeMethod(post);
		}
		catch (HttpException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		post.releaseConnection();
		CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
		Cookie[] logoncookies = cookiespec.match(host, port, "/", false, this.client.getState().getCookies());
		Cookie sessionCookie = logoncookies[0];
		return sessionCookie;
	}

	// User login processed. Returning a userHandle for further recognition of
	// the user
	private String passLogin(Cookie securityCookie)
	{
		String userHandle = null;
		PostMethod postMethod = new PostMethod(this.FRAMEWORK_URL + "/aa/login");
		postMethod.addParameter("target", this.FRAMEWORK_URL);
		client.getState().addCookie(securityCookie);
		try
		{
			client.executeMethod(postMethod);

		}
		catch (HttpException e)
		{
			System.out.println("HttpException in login POST request");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("IOException in login POST request");
			e.printStackTrace();
		}
		if (postMethod.getStatusCode() != 303)
		{
			System.out.println("StatusCode: " + postMethod.getStatusCode());
			return userHandle;
		}
		try
		{
			InputStream is = postMethod.getResponseBodyAsStream();
			System.out.println(Util.inputStreamToString(is));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		Header headers[] = postMethod.getResponseHeaders();
		for (int i = 0; i < headers.length; ++i)
		{
			System.out.println(headers[i]);
			if ("Location".equals(headers[i].getName()))
			{
				String location = headers[i].getValue();
				int index = location.indexOf('=');
				userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
				// System.out.println("location: " + location);
				System.out.println("UserHandle: " + userHandle);
			}
		}
		System.out.println(userHandle);
		return userHandle;
	}

	// Login as a specific User
	public String getUserHandle()
	{
		// returns the userHandle retrieved by security check an login
		return this.passLogin(this.passSecurityCheck());
	}
}
