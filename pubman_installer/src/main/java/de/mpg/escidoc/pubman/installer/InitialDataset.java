/*
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
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.pubman.installer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitNameNotUniqueException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * @author endres
 * 
 */
public class InitialDataset
{
    /** logging instance */
    private Logger logger = null;
    /** URL of the framework instance */
    private URL frameworkUrl = null;
    /** user handle from framework */
    private String userHandle = null;
    /** number of tokens needed by the login method */
    private static final int NUMBER_OF_URL_TOKENS = 2;
    
    private static final String OBJECTID_SUBSTITUTE_IDENTIFIER = "template_objectid_substituted_by_installer";
    private static final String CONTEXTID_SUBSTITUTE_IDENTIFIER = "template_contextid_substituted_by_installer";
        
    public InitialDataset() {
        
    }

    public InitialDataset(URL frameworkUrl, String username, String password) throws ServiceException, IOException
    {
        logger = Logger.getLogger(Installer.class);
        this.frameworkUrl = frameworkUrl;
        this.userHandle = loginToCoreservice(username, password);
        logger.info("Connection to coreservice <" + frameworkUrl.toString() +"> established, using handle" +
        		" <" + userHandle + ">.");
    }

    public String loginToCoreservice(String userid, String password) throws ServiceException, IOException
    {
        StringTokenizer tokens = new StringTokenizer(frameworkUrl.toString(), "//");
        if (tokens.countTokens() != NUMBER_OF_URL_TOKENS)
        {
            throw new IOException("Url in the config file is in the wrong format, needs to be http://<host>:<port>");
        }
        tokens.nextToken();
        StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");

        if (hostPort.countTokens() != NUMBER_OF_URL_TOKENS)
        {
            throw new IOException("Url in the config file is in the wrong format, needs to be http://<host>:<port>");
        }
        String host = hostPort.nextToken();
        int port = Integer.parseInt(hostPort.nextToken());

        HttpClient client = new HttpClient();
        client.getHostConfiguration().setHost(host, port, "http");
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

        PostMethod login = new PostMethod(frameworkUrl + "/aa/j_spring_security_check");
        login.addParameter("j_username", userid);
        login.addParameter("j_password", password);

        client.executeMethod(login);
        // System.out.println("Login form post: " +
        // login.getStatusLine().toString());

        login.releaseConnection();
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(host, port, "/", false, client.getState().getCookies());

        // System.out.println("Logon cookies:");
        Cookie sessionCookie = logoncookies[0];

        /*
         * if (logoncookies.length == 0) {
         * 
         * System.out.println("None");
         * 
         * } else { for (int i = 0; i < logoncookies.length; i++) {
         * System.out.println("- " + logoncookies[i].toString()); } }
         */

        PostMethod postMethod = new PostMethod("/aa/login");
        postMethod.addParameter("target", frameworkUrl.toString());
        client.getState().addCookie(sessionCookie);
        client.executeMethod(postMethod);
        // System.out.println("Login second post: " +
        // postMethod.getStatusLine().toString());

        if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + login.getStatusCode());
        }

        String userHandle = null;
        Header headers[] = postMethod.getResponseHeaders();
        for (int i = 0; i < headers.length; ++i)
        {
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
                // System.out.println("location: "+location);
                // System.out.println("handle: "+userHandle);
            }
        }

        if (userHandle == null)
        {
            throw new ServiceException("User not logged in.");
        }
        return userHandle;
    }

    public String getResourceAsXml(final String fileName) throws FileNotFoundException, Exception
    {
        StringBuffer buffer = new StringBuffer();
        InputStream is = null;
        BufferedReader br = null;
        String line;

        try
        {
            is = getClass().getClassLoader().getResourceAsStream(fileName);
            br = new BufferedReader(new InputStreamReader(is));
            while (null != (line = br.readLine()))
            {
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (br != null)
                    br.close();
                if (is != null)
                    is.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return buffer.toString();
    }
    
    public String createAndOpenOrganizationalUnit(String fileName) throws Exception
    {
        String orgXml = getResourceAsXml(fileName);
        String frameworkReturnXml =
            ServiceLocator.getOrganizationalUnitHandler(userHandle, frameworkUrl).create(orgXml);
        if(frameworkReturnXml == null) {
            throw new Exception("org-unit creation error");
        }
        logger.info("Creation data from framework: " + frameworkReturnXml);
        String objectId = getValueFromXml("objid=\"", frameworkReturnXml);
        String lastmodDate = "<param last-modification-date=\""+getValueFromXml("last-modification-date=\"", frameworkReturnXml)+"\"/>";
        logger.info("Created org-unit with last modification-date: " + lastmodDate);     
        logger.info("Created org-unit with objectid: " + objectId);
        
        String openxml = ServiceLocator.getOrganizationalUnitHandler(userHandle, frameworkUrl).open(objectId, lastmodDate);
        
        logger.info("Opened org-unit, returned xml: " + openxml);
        return objectId;
        
    }
    
    public String createAndOpenContext(String fileName, String orgObjectId) throws Exception
    {
        String contextXml = getResourceAsXml(fileName);
        contextXml = contextXml.replaceAll(OBJECTID_SUBSTITUTE_IDENTIFIER, orgObjectId);
       
        String frameworkReturnXml =
            ServiceLocator.getContextHandler(userHandle, frameworkUrl).create(contextXml);
        if(frameworkReturnXml == null) {
            throw new Exception("context creation error");
        }
        logger.info("Creation data from framework: " + frameworkReturnXml);
        String objectId = getValueFromXml("objid=\"", frameworkReturnXml);
        String lastmodDate = "<param last-modification-date=\""+getValueFromXml("last-modification-date=\"", frameworkReturnXml)+"\"/>";
        logger.info("Created context with last modification-date: " + lastmodDate);     
        logger.info("Created context with objectid: " + objectId);
        
        String openxml = ServiceLocator.getContextHandler(userHandle, frameworkUrl).open(objectId, lastmodDate);
        
        logger.info("Opened context, returned xml: " + openxml);
        return objectId;
    }
    
    public String createUser( String fileName, String password, String orgObjectId) throws Exception
    {
        String userXml = getResourceAsXml(fileName);
        userXml = userXml.replaceAll(OBJECTID_SUBSTITUTE_IDENTIFIER, orgObjectId);
       
        String frameworkReturnXml =
            ServiceLocator.getUserAccountHandler(userHandle, frameworkUrl).create(userXml);
        if(frameworkReturnXml == null) {
            throw new Exception("context creation error");
        }
        String objectId = getValueFromXml("objid=\"", frameworkReturnXml);
        String lastmodDate = "<param last-modification-date=\""
            +getValueFromXml("last-modification-date=\"", frameworkReturnXml)
            +"\">"
            +"\n<password>"+password+"</password>"
            +"</param>";
        
        ServiceLocator.getUserAccountHandler(userHandle, frameworkUrl).updatePassword(objectId, lastmodDate);
        
        logger.info("Creation data from framework: " + frameworkReturnXml);
        return objectId;
    }
    
    public String createGrantForUser( String fileName, String userObjectId, String contextId ) throws Exception
    {
        String grantXml = getResourceAsXml(fileName);
        grantXml = grantXml.replaceAll(CONTEXTID_SUBSTITUTE_IDENTIFIER, contextId);
        
        String frameworkReturnXml =
            ServiceLocator.getUserAccountHandler(userHandle, frameworkUrl).createGrant(userObjectId, grantXml);
        if(frameworkReturnXml == null) {
            throw new Exception("context creation error");
        }
        String objectId = getValueFromXml("objid=\"", frameworkReturnXml);
        return objectId;
    }
    
    public String retrieveContentModel(String contentModelId) throws Exception
    {
        String frameworkReturnXml =
            ServiceLocator.getContentModelHandler(userHandle, frameworkUrl).retrieve(contentModelId);
        return frameworkReturnXml;
    }
    
    
    
    /**
     * Search the given String for the first occurence of "objid" and return its value.
     * 
     * @param item A (XML) String
     * @return The objid value
     */
    private String getValueFromXml(String key, String item)
    {
        String result = "";
        String searchString = key;
        int index = item.indexOf(searchString);
        if (index > 0)
        {
            item = item.substring(index + searchString.length());
            index = item.indexOf('\"');
            if (index > 0)
            {
                result = item.substring(0, index);
            }
        }
        return result;
    }  
    
    public String getHandle()
    {
        return userHandle;
    }
}
