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
 
package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
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

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

// import de.mpg.escidoc.services.validation.xmltransforming.ValidationTransforming;

/**
 * Helper class for all test classes.
 *
 * @author Johannes M&uuml;ller (initial)
 * @author $Author: vdm $ (last change)
 * @version $Revision: 69 $ $LastChangedDate: 2007-12-11 12:41:58 +0100 (Tue, 11 Dec 2007) $
 */
public class TestHelper
{
	
	public static final String ITEMS_LIMIT = "50"; 
	public static final String CONTENT_MODEL = "escidoc:persistent4"; 
	public static String USER_NAME = null;
	public static String USER_PASSWD = null; 
	
	/**
	 * Initialize user credetials.
	 */
	public TestHelper() throws Exception
	{
	    if (USER_NAME == null)
	    {
            USER_NAME = PropertyReader.getProperty("framework.scientist.username");
            if (USER_NAME == null)
            {
                throw new RuntimeException("Property 'framework.scientist.username' not found.");
            }
	    }
	    if (USER_PASSWD == null)
        {
            USER_PASSWD = PropertyReader.getProperty("framework.scientist.password");
            if (USER_PASSWD == null)
            {
                throw new RuntimeException("Property 'framework.scientist.password' not found.");
            }
	    }
	}
	
    /**
     * Retrieve resource based on a path relative to the classpath.
     * @param fileName The path of the resource.
     * @return The file defined by The given path.
     * @throws FileNotFoundException File not there.
     */
    public final File findFileInClasspath(final String fileName) throws FileNotFoundException
    {
        URL url = getClass().getClassLoader().getResource(fileName);
        if (url == null)
        {
            throw new FileNotFoundException(fileName);
        }
        return new File(url.getFile());
    }

    /**
     * Reads contents from text file and returns it as String.
     *
     * @param fileName Name of input file
     * @return Entire contents of filename as a String
     */
    public static String readFile(final String fileName, String enc)
    {
        boolean isFileNameNull = (fileName == null);
        StringBuffer fileBuffer;
        String fileString = null;
        String line;
        if (!isFileNameNull)
        {
            try
            {
//                InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
                InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), enc);
                BufferedReader br = new BufferedReader(isr);
                fileBuffer = new StringBuffer();
                while ((line = br.readLine()) != null)
                {
                    fileBuffer.append(line + "\n");
                }
                isr.close();
                fileString = fileBuffer.toString();
            }
            catch (IOException e)
            {
                return null;
            }
        }
        return fileString;
    }
    
    /**
     * Reads contents from text file and returns it as array of bytes.
     * @param fileName
     * @return
     */
    public static byte[] readBinFile(final String fileName)
    {
    	boolean isFileNameNull = (fileName == null);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	if (!isFileNameNull)
    	{
    		try {
    			int b;                // the byte read from the file
    			BufferedInputStream is = new BufferedInputStream(new FileInputStream(fileName));
    			BufferedOutputStream os = new BufferedOutputStream(baos);
    			while ((b = is.read( )) != -1) {
    				os.write(b);
    			}
    			is.close( );
    			os.close( );
    		}
    		catch (IOException e)
    		{
    			return null;
    		}
    	}
    	return baos.toByteArray();
    }
    
    /**
     * Reads contents from array of bytes and write to the file .
     * @param fileName
     * @throws IOException 
     */
    public static void writeBinFile(byte[] content, String fileName) throws IOException
    {
    	boolean isFileNameNull = (fileName == null);
    	boolean isEmptyContent = (content.length == 0);
    	if (!isFileNameNull && !isEmptyContent)
    	{
    		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
    		for (byte b : content)
    			bos.write(b);
    		bos.close( );
    	}
    }
    
    public String getItemListFromFramework() throws IOException, ServiceException, URISyntaxException
    {
    	
    	String userHandle = loginUser(USER_NAME, USER_PASSWD); 
        ItemHandler ch = ServiceLocator.getItemHandler(userHandle);
        // see here for filters: https://zim02.gwdg.de/repos/common/trunk/common_services/common_logic/src/main/java/de/mpg/escidoc/services/common/xmltransforming/JiBXFilterTaskParamVOMarshaller.java
        String filter = 
        	"<param>" +
        		// escidoc content model
        		"<filter name=\"http://escidoc.de/core/01/structural-relations/content-model\">" + CONTENT_MODEL + " </filter>" +
        		"<filter name=\"http://escidoc.de/core/01/properties/public-status\">released</filter>" +
        		// records limit	
        		"<limit>" + ITEMS_LIMIT + "</limit>" +
        	"</param>";
        return ch.retrieveItems(filter);
    
    }
    
    
    
    protected String loginUser(String userid, String password) throws HttpException, IOException, ServiceException, URISyntaxException
    {
    	String frameworkUrl = ServiceLocator.getFrameworkUrl();
    	
        int delim1 = frameworkUrl.indexOf("//");
        int delim2 = frameworkUrl.indexOf(":", delim1);
        
        String host;
        int port;
        
        if (delim2 > 0)
        {
            host = frameworkUrl.substring(delim1 + 2, delim2);
            port = Integer.parseInt(frameworkUrl.substring(delim2 + 1));
        }
        else
        {
            host = frameworkUrl.substring(delim1 + 2);
            port = 80;
        }
  
    	HttpClient client = new HttpClient();

    	client.getHostConfiguration().setHost( host, port, "http");
    	client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

    	PostMethod login = new PostMethod( frameworkUrl + "/aa/j_spring_security_check");
    	login.addParameter("j_username", userid);
    	login.addParameter("j_password", password);

    	client.executeMethod(login);

    	login.releaseConnection();
    	CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
    	Cookie[] logoncookies = cookiespec.match(
    			host, port, "/", false, 
    			client.getState().getCookies());

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
    			//System.out.println("location: "+location);
    			//System.out.println("handle: "+userHandle);
    		}
    	}

    	if (userHandle == null)
    	{
    		throw new ServiceException("User not logged in.");
    	}
    	return userHandle;
    }    


}
