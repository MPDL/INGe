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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.edoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.validation.ItemInvalidException;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.ItemValidatingBean;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class PubManImport
{
    
    Logger logger = Logger.getLogger(PubManImport.class);
    
    private String userHandle = null;
    private List<String> itemIds = new ArrayList<String>();
    
    private ItemValidating validating;
    
    private static final String CORESERVICES_URL = "http://dev-coreservice.mpdl.mpg.de:8080";
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        if (args != null && args.length == 3)
        {
            PubManImport pubManImport = new PubManImport(args[0], args[1], args[2]);
        }
        else
        {
            System.out.println("usage: PubManImport item_xml_filename username password");
        }
        
    }
    
    public PubManImport(String fileName, String username, String password) throws Exception
    {
        userHandle = loginUser(username, password);
        InitialContext context = new InitialContext();
        validating = (ItemValidating) context.lookup(ItemValidating.SERVICE_NAME);
        
        File file = new File(fileName);
        if (!file.exists())
        {
            System.err.println("File \"" + file.getAbsolutePath() + "\" not found!");
            System.exit(1);
        }
        
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        ImportHandler importHandler = new ImportHandler();
        parser.parse(new InputSource(new InputStreamReader(new FileInputStream(file))), importHandler);
        
        List<String> itemXmls = importHandler.getItems();
        
        for (String itemXml : itemXmls)
        {
            validateItem(itemXml);
            importItem(itemXml);
        }
    }
    
    private void validateItem(String itemXml) throws Exception
    {
        String report = validating.validateItemXmlBySchema(itemXml, "submit_item", "simple");
        if (report.contains("failure"))
        {
            throw new RuntimeException(report + "\n\nXML was:\n\n" + itemXml);
        }
    }

    private void importItem(String itemXml) throws Exception
    {
        itemXml = createItem(itemXml);
        String id = getId(itemXml);
        String lastModificationDate = getLastModificationDate(itemXml);
        String result = submitItem(id, lastModificationDate);
        lastModificationDate = getLastModificationDate(result);
        result = assignObjectPid(id, lastModificationDate);
        lastModificationDate = getLastModificationDate(result);
        result = assignVersionPid(id, lastModificationDate);
        lastModificationDate = getLastModificationDate(result);
        result = releaseItem(id, lastModificationDate);
        itemIds.add(id);
        System.out.println("Successfully imported " + id);
    }

    private String releaseItem(String id, String lastModificationDate) throws Exception
    {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(CORESERVICES_URL + "/ir/item/" + id + "/release");
        postMethod.addRequestHeader("Cookie", "escidocCookie=" + userHandle);
        String body = "<param last-modification-date=\"" + lastModificationDate + "\"><comment>Release comment.</comment></param>";

        postMethod.setRequestEntity(new StringRequestEntity(body));
        httpClient.executeMethod(postMethod);
        
        String response = postMethod.getResponseBodyAsString();
        
        if (postMethod.getStatusCode() != 200)
        {
            throw new RuntimeException(response);
        }
        
        return response;
    }

    private String assignObjectPid(String id, String lastModificationDate) throws Exception
    {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(CORESERVICES_URL + "/ir/item/" + id + "/assign-object-pid");
        postMethod.addRequestHeader("Cookie", "escidocCookie=" + userHandle);
        String body = "<param last-modification-date=\"" + lastModificationDate + "\"><url>http://localhost:8080/album/core/ir/item/" + id + "</url></param>";

        postMethod.setRequestEntity(new StringRequestEntity(body));
        httpClient.executeMethod(postMethod);
        
        String response = postMethod.getResponseBodyAsString();
        
        if (postMethod.getStatusCode() != 200)
        {
            throw new RuntimeException(response);
        }
        
        return response;
    }

    private String assignVersionPid(String id, String lastModificationDate) throws Exception
    {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(CORESERVICES_URL + "/ir/item/" + id + "/assign-version-pid");
        postMethod.addRequestHeader("Cookie", "escidocCookie=" + userHandle);
        String body = "<param last-modification-date=\"" + lastModificationDate + "\"><url>http://localhost:8080/album/core/ir/item/" + id + "</url></param>";

        postMethod.setRequestEntity(new StringRequestEntity(body));
        httpClient.executeMethod(postMethod);
        
        String response = postMethod.getResponseBodyAsString();
        
        if (postMethod.getStatusCode() != 200)
        {
            throw new RuntimeException(response);
        }
        
        return response;
    }

    private String submitItem(String id, String lastModificationDate) throws Exception
    {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(CORESERVICES_URL + "/ir/item/" + id + "/submit");
        postMethod.addRequestHeader("Cookie", "escidocCookie=" + userHandle);
        String body = "<param last-modification-date=\"" + lastModificationDate + "\"><comment>Submit comment.</comment></param>";

        postMethod.setRequestEntity(new StringRequestEntity(body));
        httpClient.executeMethod(postMethod);
        
        String response = postMethod.getResponseBodyAsString();
        
        if (postMethod.getStatusCode() != 200)
        {
            throw new RuntimeException(response);
        }
        
        return response;
    }

    private String getLastModificationDate(String result)
    {
        int start = result.indexOf("last-modification-date=\"") + 24;
        int end = result.indexOf("\"", start);
        String lastModified = result.substring(start, end);
        return lastModified;
    }

    private String getId(String itemXml)
    {
        int start = itemXml.indexOf("xlink:href=\"/ir/item/") + 21;
        int end = itemXml.indexOf("\"", start);
        String id = itemXml.substring(start, end);
        return id;
    }

    private String createItem(String itemXml) throws Exception
    {
        HttpClient httpClient = new HttpClient();
        
        // Create
        PutMethod method = new PutMethod(CORESERVICES_URL + "/ir/item");
        
        method.addRequestHeader("Cookie", "escidocCookie=" + userHandle);

        method.setRequestEntity(new StringRequestEntity(itemXml));
        httpClient.executeMethod(method);
        String response = method.getResponseBodyAsString();
        
        if (method.getStatusCode() != 200)
        {
            throw new RuntimeException(response);
        }
        
        return response;
    }

    /**
     * Logs in the given user with the given password.
     * 
     * @param userid The id of the user to log in.
     * @param password The password of the user to log in.
     * @return The handle for the logged in user.
     * @throws HttpException
     * @throws IOException
     * @throws ServiceException
     * @throws URISyntaxException
     */
    protected static String loginUser(String userid, String password) throws HttpException, IOException,
            ServiceException, URISyntaxException
    {
        String frameworkUrl = ServiceLocator.getFrameworkUrl();
        StringTokenizer tokens = new StringTokenizer(frameworkUrl, "//");
        if (tokens.countTokens() != 2)
        {
            throw new IOException("Url in the config file is in the wrong format, needs to be http://<host>:<port>");
        }
        tokens.nextToken();
        StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");
        if (hostPort.countTokens() != 2)
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
        login.releaseConnection();
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(host, port, "/", false, client.getState().getCookies());
        Cookie sessionCookie = logoncookies[0];
        PostMethod postMethod = new PostMethod("/aa/login");
        postMethod.addParameter("target", frameworkUrl);
        client.getState().addCookie(sessionCookie);
        client.executeMethod(postMethod);
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

}
