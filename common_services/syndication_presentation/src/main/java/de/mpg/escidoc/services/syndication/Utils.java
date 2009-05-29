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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.syndication;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ServiceException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.apache.ws.security.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

 
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


/**
 * Utilities class for eSciDoc syndication manager
 *
 * @author vmakarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */ 
public class Utils {
	
    private static Logger logger = Logger.getLogger(Utils.class);
	
	private static XPath xpath = XPathFactory.newInstance().newXPath();
	
    /**
     * Returns true if val is not null && not empty String 
     * @param val 
     * @return first not null && not empty String
     */
    public static boolean checkVal(String val)
    {
    	return ( val != null && !val.trim().equals("") );
    }

    /**
     * Returns true if val is not null && Length >0 
     * @param val 
     * @return first not null && Length >0
     */
    public static boolean checkLen(String val)
    {
    	return ( val != null && val.length()>0 );
    }

	/**
	 * Returns <code>true</code> if list is not empty
	 * @param l
	 * @return
	 */
	public static <T> boolean checkList(List<T> l)
	{
		return ( l != null && !l.isEmpty() );
	}    
    
    
    /**
     * Throws ExportManagerException true if cond is true 
     * @param cond
     * @param message
     * @throws ExportManagerException
     */
    public static void checkCondition(final boolean cond, final String message) throws SyndicationException
    {
    	if ( cond )
    		throw new SyndicationException(message);
    }
	
	public static void checkName(final String name) throws SyndicationException
	{
		Utils.checkCondition(!checkVal(name), "Empty name");
	}
	
	public static void checkName(final String name, final String message) throws SyndicationException
	{
		Utils.checkCondition(!checkVal(name), message);
	}
	


    /**
     * Find <code>name</code> in <code>a</code> String[]
     * @return <code>true</code> if <code>name</code> has been found 
     */
    public static boolean findInList(final String[] a, final String name){
    	for (String s: a) {
			if (s.equals(name)) 
				return true;
		}
    	return false;
    }      
    

    /**
     * Version of the <code>String.replaceAll(what, expr, replacement)</code>
     * which ignores new line breaks and case sensitivity  
     * @param what is string to be replaced
     * @param expr is RegExp
     * @param replacement 
     * @return replaced <code>what</code>
     */
    public static String replaceAllTotal(String what, String expr, String replacement)
    {
	    return 
	    	Pattern
	    		.compile(expr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
	    		.matcher(what)
	    		.replaceAll(replacement);
    }
    
    
    /**
     * Quote <code>{}</code>
     * @param str
     * @return quoted <code>str</code>
     */
    public static String quoteReplacement(String str)
    {
    	return 
    		Matcher.quoteReplacement(str)
    		.replace("{","\\{" )
    		.replace("}","\\}" );
    }
    
    
    /**
     * Get a resource as InputStream.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as InputStream.
     * @throws FileNotFoundException Thrown if the resource cannot be located.
     */
    public static InputStream getResourceAsStream(final String fileName) throws FileNotFoundException
    {
        InputStream fileIn = null;
        
        File file = new File(fileName);
        if (file.exists())
        {
        	fileIn = new FileInputStream(fileName);
        }
        else
	    {
	    	fileIn = Feeds.class.getClassLoader().getResourceAsStream(fileName);
	    }
	    return fileIn;
        
    }	
	/**
     * Get a resource as String.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as String.
     * @throws IOException Thrown if the resource cannot be located.
     */
    public static String getResourceAsString(final String fileName) throws IOException
    {
        return getInputStreamAsString(getResourceAsStream(fileName));
    }
    
    /**
     * Get an InputStream as String.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as String.
     * @throws IOException Thrown if the resource cannot be located.
     */
    public static String getInputStreamAsString(InputStream is) throws IOException
    {
    	BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    	String line = null;
    	StringBuffer result = new StringBuffer();
    	while ((line = br.readLine()) != null)
    	{
    		result.append(line).append("\n");
    	}
    	return result.toString();
    }    
    
    /**
     * Writes <code>content</code> to the file
     * @param fileName
     * @param content 
     * @throws IOException
     */
    public static void writeToFile(String fileName, String content) throws IOException
    {
    	FileWriter fw = new FileWriter(fileName);
    	BufferedWriter out = new BufferedWriter(fw);
    	out.write(content);
    	out.close();
    }    
    
    /**
     * Join elements of any collection with delimiter
     * @param <T>
     * @param objs 
     * @param delimiter
     * @return a joined string
     */
    public static <T> String join(final Collection<T> objs, String delimiter) 
    {
    	if ( objs == null || objs.isEmpty()) return "";
    	if (!checkVal(delimiter)) delimiter = "";
    	Iterator<T> iter = objs.iterator();
    	StringBuffer buffer = new StringBuffer();
    	while (iter.hasNext())
    	{
    		Object o = iter.next();
    		String str="";
    		if ( o != null )
    		{
	    		str = o.toString();
	    		//empty strings will be omitted!
	    		if (checkVal(str))
	    			buffer.append(str).append(delimiter);
    		}
    	}	
    	String result = buffer.toString();
    	result = result.substring(0, result.length() - delimiter.length());
    	return result;
    }

	/**
	 * Cut <code>string</code> and appends it with postfix 
	 * @param str - input String
	 * @param maxLen - max length of the result to be returned  
	 * @param postfx - postfix of the string which indicates that string is cut.  
	 * @return 
	 */
	public static String cutString(String str, int maxLen, String postfx) 
	{
		if(str == null || str.length() < maxLen || maxLen<= 0)
			return str;
		if (postfx == null || postfx.length() >= maxLen)
			postfx = "";
		return str.substring(0, maxLen-postfx.length()) + postfx;
	}
	
	/**
	 * Cut <code>string</code> and appends it with postfix id <code>cond</code> is <code>true</code> 
	 * @param cond - boolean condition
	 * @param str - input String
	 * @param maxLen - max length of the result to be returned  
	 * @param postfx - postfix of the string which indicates that string is cut.  
	 * @return 
	 */
	public static String checkAndCutString(boolean cond, String str, int maxLen, String postfx) 
	{
		return cond ? cutString(str, maxLen, postfx) : str;
	}    
    
	
	/**
	 * Get sorted Organizational Unit Tree.
	 * @return <code>TreeMap<String, String></code>: 
	 * <code>keys</code> are unit names, <code>values</code> are objids   
	 * @throws Exception
	 */
	public static TreeMap<String, String> getOrganizationUnitTree() throws SyndicationException
	{
		
//    	long start = System.currentTimeMillis();
//		String result = performOragnizationalItemsSearch("escidoc.public-status=opened or escidoc.public-status=closed", "10000");
//		logger.info("time1:" + (System.currentTimeMillis() - start));
		
//    	start = System.currentTimeMillis();
		
//        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
//        searchRetrieveRequest.setVersion("1.1");
//        searchRetrieveRequest.setQuery("escidoc.public-status=opened or escidoc.public-status=closed");
//        searchRetrieveRequest.setRecordPacking("xml");
//        searchRetrieveRequest.setMaximumRecords(new NonNegativeInteger("10000"));
//
//        SearchRetrieveResponseType searchResult = ServiceLocator.getSearchHandler("escidocou_all").searchRetrieveOperation(searchRetrieveRequest);
////		logger.info("time2:" + (System.currentTimeMillis() - start));
//
////        String list = "";
////    	start = System.currentTimeMillis();
//        TreeMap<String, String> outm = new TreeMap<String, String>();        
//        RecordsType records = searchResult.getRecords(); 
//        if (records != null)
//        {
//            for (RecordType record : records.getRecord())
//            {
//                MessageElement[] messages = record.getRecordData().get_any();
//                for (int i = 0; i < messages.length; i++)
//                {
//        			Document tmpDoc = createDocument(messages[i].getAsString());
//        			String objid = Utils.xpathString("/search-result-record/organizational-unit/@objid", tmpDoc);
//        			String name = Utils.xpathString("/search-result-record/organizational-unit/properties/name", tmpDoc);
//        			outm.put(name, objid);
//                }
//            }
//        }
//		logger.info("time3:" + (System.currentTimeMillis() - start));
//				
    	long start = System.currentTimeMillis(); 
		String adminHandle;
		try 
		{
			adminHandle = Utils.loginUser(PropertyReader.getProperty("framework.admin.username"), PropertyReader.getProperty("framework.admin.password"));
		}
		catch (Exception e) 
		{
			throw new SyndicationException("Cannot get UserHandle:", e);
		}
		OrganizationalUnitHandler ouh;
		try 
		{
			ouh = ServiceLocator.getOrganizationalUnitHandler(adminHandle);
		}
		catch (Exception e) 
		{
			throw new SyndicationException("Cannot get OrganizationalUnitHandler:", e);
		}
		logger.info("Organizational Unit List retrieval time: " + (System.currentTimeMillis() - start));
        
		String filter = 
			"<param>" + 
    			"<filter name=\"/properties/public-status\">opened</filter>" +
    			"<filter name=\"/properties/public-status\">closed</filter>" +
    		"</param>";
		
		String orgUnitList;
		try 
		{
			orgUnitList = ouh.retrieveOrganizationalUnits(filter);
		}
		catch (Exception e) 
		{
			throw new SyndicationException("Cannot retrieve Organizational Unit List:", e);
		}
		
		TreeMap<String, String> outm = new TreeMap<String, String>(); 
		NodeList nodes;
		try {
			nodes = Utils.xpathNodeList( "/organizational-unit-list/organizational-unit", orgUnitList );
			for (int i = 0; i < nodes.getLength(); i++) 		
			{
				Node node = nodes.item(i);
				Element e = (Element)node;
				String objid = e.getAttribute("objid");
				String name = Utils.xpathString("/organizational-unit/properties/name", createDocument(node));
				outm.put(name, objid);
			}
		} 
		catch (Exception e) 
		{
			throw new SyndicationException(e);
		}		
		
		return outm;		
	}
	

//	private static String performOragnizationalItemsSearch(String query, String maximumRecords) throws SyndicationException 
//	{
//		
//		URL url;
//		http://dev-coreservice.mpdl.mpg.de:8080/srw/search/escidocou_all?operation=searchRetrieve&query=escidoc.public-status=opened%20or%20escidoc.public-status=closed&maximumRecords=10000		
//		try {
//			url = new URL(
//					"http://dev-coreservice.mpdl.mpg.de:8080" + "/srw/search/escidocou_all?operation=searchRetrieve&" +
//					"query=" + URLEncoder.encode(query, "UTF-8") + 
//					"&maximumRecords=" + URLEncoder.encode(maximumRecords, "UTF-8")  
//			);
//		} 
//		catch (Exception e) 
//		{
//			throw new SyndicationException("Wrong URL:", e);
//		}
//
//		logger.info("Search URL:" + url.toString());
//		Object content;
//		URLConnection uconn;
//		try 
//		{
//			uconn = url.openConnection();
//			if ( !(uconn instanceof HttpURLConnection) )
//	            throw new IllegalArgumentException(
//	                "URL protocol must be HTTP." 
//	            );
//			HttpURLConnection conn = (HttpURLConnection)uconn;
//	
//			InputStream stream =  conn.getErrorStream( );
//	        if ( stream != null )
//	        {
//	        	conn.disconnect();	
//	        	throw new SyndicationException(Utils.getInputStreamAsString( stream ));
//	        }
//	        else if ( (content = conn.getContent( )) != null && content instanceof InputStream )
//	            content = Utils.getInputStreamAsString( (InputStream)content );
//	        else
//	        {
//	        	conn.disconnect();	
//	        	throw new SyndicationException("Cannot retrieve content from the HTTP response");
//	        }
//	        conn.disconnect();
//	        
//			return (String)content;
//		}
//		catch (Exception e) 
//		{
//			throw new SyndicationException(e);
//		}
//		
//	}	
//	
	
	
	/***************/
	/** XML Utils **/
	/***************/
    public static NodeList xpathNodeList(String expr, String xml) throws Exception
    {
    	return xpathNodeList(expr, createDocument(xml)); 
    }
    
    public static NodeList xpathNodeList(String expr, Document doc) throws Exception
    {
    	return (NodeList) xpath.evaluate(
    			expr, 
    			doc, 
    			XPathConstants.NODESET
    	);
    }    
    
    public static String xpathString(String expr, Document doc) throws Exception
    {
    	return (String) xpath.evaluate(
    				expr, 
    				doc, 
    				XPathConstants.STRING
    	);
    } 
    
	public static DocumentBuilder createDocumentBuilder() throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setIgnoringComments(true);
		return dbf.newDocumentBuilder();
	}
	
    public static Document createDocument(String xml) throws Exception
    {
        DocumentBuilder db = createDocumentBuilder();
    	return db.parse(
    			new ByteArrayInputStream(xml.getBytes("UTF-8")), "UTF-8" 
    	); 
    }    
    
	public static Document createDocument(Node sourceNode) throws Exception
	{
		Document doc = createDocumentBuilder().newDocument();
		Node source;
		if (sourceNode.getNodeType() == Node.DOCUMENT_NODE) {
			source = ((Document) sourceNode).getDocumentElement();
		} else {
			source = sourceNode;
		}

		Node node = doc.importNode(source, true);
		doc.appendChild(node);
		
		return doc;
	}
	
    public static String loginUser(String userid, String password) throws HttpException, IOException, ServiceException, URISyntaxException
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
	
}
