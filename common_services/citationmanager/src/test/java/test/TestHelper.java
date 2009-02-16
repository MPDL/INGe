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

import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRXmlUtils;

import org.apache.axis.encoding.Base64;
import org.apache.axis.message.MessageElement;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.escidoc.www.services.om.ItemHandler;
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

	private static Logger logger = Logger.getLogger(TestHelper.class);
	
	public static final String ITEMS_LIMIT = "50"; 
	public static final String CONTENT_MODEL = "escidoc:persistent4"; 
//	public static final String USER_NAME = "test_dep_scientist"; 
//	public static final String USER_PASSWD = "verdi"; 
	public static final String USER_NAME = "citman_user"; 
	public static final String USER_PASSWD = "citman_user"; 
	public static final String SEARCH_CONTEXT = "escidoc.context.name=%22Citation%20Style%20Testing%20Context%22"; 
	
 
    /**
     * Get itemList from the current Framework instance
     * @param fileName
     * @throws IOException 
     * @throws URISyntaxException 
     * @throws ServiceException 
     */
    public static String getItemListFromFramework() throws IOException, ServiceException, URISyntaxException
    {
    	
    	String userHandle = loginUser(USER_NAME, USER_PASSWD); 
        ItemHandler ch = ServiceLocator.getItemHandler(userHandle);
        // see here for filters: https://zim02.gwdg.de/repos/common/trunk/common_services/common_logic/src/main/java/de/mpg/escidoc/services/common/xmltransforming/JiBXFilterTaskParamVOMarshaller.java
        String filter = 
        	"<param>" +
        		// escidoc content model
        		"<filter name=\"http://escidoc.de/core/01/structural-relations/content-model\">" + CONTENT_MODEL + " </filter>" +
        		// records limit	
        		"<limit>" + ITEMS_LIMIT + "</limit>" +
        	"</param>";
        	
// take one item:        	
//    	"<param>" +
//		//items
//			"<filter name=\"http://purl.org/dc/elements/1.1/identifier\">" +  
//				"<id>escidoc:23004</id>" + 
//			" </filter>" +
//		"</param>";
        
        return ch.retrieveItems(filter);
    
    }
     
    
    public static String getItemsFromFramework_APA() throws Exception {
    	
    	return 
    		getItemsFromFramework(
    			"escidoc.abstract=%22APA:%22%20AND%20" + SEARCH_CONTEXT 
    	);
    }
    
    public static String getItemsFromFramework_AJP() throws Exception  {
    	
    	return  
    	getItemsFromFramework(
    			"escidoc.abstract=%22AJP:%22%20AND%20" + SEARCH_CONTEXT 
    	);
    }
    
    	
    public static String getItemsFromFramework(String cql) throws Exception {
    	
//    	http://localhost:8080/search/SearchAndExport?cqlQuery=escidoc.abstract=%22APA:%22%20AND%20escidoc.context.name=%22Citation%20Style%20Testing%20Context%22&exportFormat=APA_revised&outputFormat=pdf&language=all&sortKeys=&sortOrder=ascending&startRecord=&maximumRecords=
    	
    	
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery(cql);
        searchRetrieveRequest.setRecordPacking("xml");
        
        SearchRetrieveResponseType searchResult = ServiceLocator.getSearchHandler("escidoc_all").searchRetrieveOperation(searchRetrieveRequest);
        if (searchResult.getDiagnostics() != null)
        {
            // something went wrong
            for (DiagnosticType diagnostic : searchResult.getDiagnostics().getDiagnostic())
            {
                    logger.warn(diagnostic.getUri());
                    logger.warn(diagnostic.getMessage());
                    logger.warn(diagnostic.getDetails());
            }
        }

        return transformToItemListAsString(searchResult);
        
    }
    
 
    
    private static String transformToItemListAsString(SearchRetrieveResponseType searchResult) throws Exception
    {
    	String itemStringList = "";
		Pattern p = Pattern.compile("(<\\w+?:item.*?</\\w+?:item>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        if (searchResult.getRecords() != null)
        {
            for (RecordType record : searchResult.getRecords().getRecord())
            {
                StringOrXmlFragment data = record.getRecordData();
                MessageElement[] messages = data.get_any();
                // Data is in the first record
                if (messages.length == 1)
                {
            	    Matcher m = null;
            	    String str = messages[0].getAsString();
					m = p.matcher(str);
            	    if (m.find())  
            	    {
            	    	itemStringList += m.group(1);
					} 
                }
            }
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><il:item-list xmlns:il=\"http://www.escidoc.de/schemas/itemlist/0.7\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">" 
        		+  itemStringList 
        		+ "</il:item-list>"; 
    }
     
    
       
    
    protected static String loginUser(String userid, String password) throws HttpException, IOException, ServiceException, URISyntaxException
    {
    	String frameworkUrl = ServiceLocator.getFrameworkUrl();
    	StringTokenizer tokens = new StringTokenizer( frameworkUrl, "//" );
    	if( tokens.countTokens() != 2 ) {
    		throw new IOException( "Url in the config file is in the wrong format, needs to be http://<host>:<port>" );
    	}
    	tokens.nextToken();
    	StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");

    	if( hostPort.countTokens() != 2 ) {
    		throw new IOException( "Url in the config file is in the wrong format, needs to be http://<host>:<port>" );
    	}
    	String host = hostPort.nextToken();
    	int port = Integer.parseInt( hostPort.nextToken() );

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
    		}
    	}

    	if (userHandle == null)
    	{
    		throw new ServiceException("User not logged in.");
    	}
    	return userHandle;
    }    
    
    protected static void writeToFile(String fileName, byte[] content) throws IOException
    {
    	FileOutputStream fos = new FileOutputStream(fileName);
    	fos.write(content);
    	fos.close();
    }
    
    
    protected static int getItemsNumber(String itemListUri) throws Exception
    {
    	Document doc = JRXmlUtils.parse(itemListUri);
		 XPathFactory factory = XPathFactory.newInstance();
		 XPath xpath = factory.newXPath();
		 Double result = (Double) xpath.evaluate("count(/item-list/item)", doc, XPathConstants.NUMBER);
    	return result.intValue();
    }

}
