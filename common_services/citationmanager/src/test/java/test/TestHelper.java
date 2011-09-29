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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.rpc.ServiceException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.sf.jasperreports.engine.util.JRXmlUtils;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

// import de.mpg.escidoc.services.validation.xmltransforming.ValidationTransforming;

/**  
 * Helper class for all test classes.
 *
 * @author Johannes M&uuml;ller (initial)
 * @author $Author$ (last change)
 * @version $Revision$ $LastChangedDate$
 */
public class TestHelper
{ 
 
	private static final String MAX_RECORDS = "maxRecords";
    private static final String QUERY = "query";
    private static final String SEARCH_RETRIEVE = "searchRetrieve";
    private static final String OPERATION = "operation";
    private static final String VERSION = "version";

    private static Logger logger = Logger.getLogger(TestHelper.class);
	
	public static final String ITEMS_LIMIT = "50"; 
	public static final String PROPERTY_CONTENT_MODEL_PUBLICATION = "escidoc.framework_access.content-model.id.publication"; 
	public static final String USER_NAME = "citman_user"; 
	public static final String USER_PASSWD = "citman_user";
	public static final String CONTEXT = "Citation Style Testing Context";
	public static final String SEARCH_CONTEXT = "escidoc.context.name=%22Citation%20Style%20Testing%20Context%22";
	
	
    public static String getTestItemListFromFramework() throws IOException, ServiceException, URISyntaxException
    {
        HashMap<String, String[]> filter = new HashMap<String, String[]>();
        
        filter.put(QUERY, new String[]{"\"/properties/content-model/id\"=" + PropertyReader.getProperty(PROPERTY_CONTENT_MODEL_PUBLICATION),
                                            "\"/properties/context/title\"=" + CONTEXT,
                                            "\"/properties/public-status\"=pending"});
        
    	return getItemListFromFrameworkBase(USER_NAME, USER_PASSWD, filter);
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

    
    public static Properties getTestProperties(String cs) throws FileNotFoundException, IOException 
    {
    	String path_to_props = 
    		ResourceUtil.getPathToCitationStyleTestResources(cs)
			+ "test.properties"; 
//    	logger.info("path_to_props:" + path_to_props);
    	InputStream is = ResourceUtil.getResourceAsStream(path_to_props); 
    	Properties props = new Properties();
		props.load(is);
		
		return props;
    }
        
    public static String getCitationStyleTestXmlAsString(String fileName) throws IOException
    {
    	return getFileAsString(ResourceUtil.CITATIONSTYLES_DIRECTORY + fileName);
    }

    
    public static String getFileAsString(String fileName) throws IOException
    {
    	logger.info("test XML" +  ResourceUtil.getPathToTestResources() + fileName);
    	return ResourceUtil.getResourceAsString(ResourceUtil.getPathToTestResources() + fileName);
    }

    
    public static String getItemListFromFrameworkBase(String USER, String PASSWD, HashMap<String, String[]> filter) throws IOException, ServiceException, URISyntaxException
    {
    	logger.info("Retrieve USER, PASSWD:" + USER + ", " + PASSWD);
    	String userHandle = AdminHelper.loginUser(USER, PASSWD);
    	logger.info("Retrieve filter:" + filter.entrySet().toString());
    	// see here for filters: https://zim02.gwdg.de/repos/common/trunk/common_services/common_logic/src/main/java/de/mpg/escidoc/services/common/xmltransforming/JiBXFilterTaskParamVOMarshaller.java
    	ItemHandler ch = ServiceLocator.getItemHandler(userHandle);
    	return ch.retrieveItems(filter);
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
    
    /**
     * Get itemList from the current Framework instance on hand of CONTENT_MODEL, CONTEXT, all released
     * and writes to <code>DataSource/fileName</code>  
     * @throws Exception
     */
    public static void getCitationStyleTestCollectionFromFramework(String fileName) throws Exception
    {
        HashMap<String, String[]> filter = new HashMap<String, String[]>();
        
        filter.put(VERSION, new String[]{"1.1"});
        filter.put(OPERATION, new String[]{SEARCH_RETRIEVE});       
        filter.put(QUERY, new String[]{"\"/properties/content-model/id\"=" + PropertyReader.getProperty(PROPERTY_CONTENT_MODEL_PUBLICATION),
                "\"/properties/context/title\"=" + CONTEXT,
                "\"/properties/public-status\"=released"});
        
        String itemList = getItemListFromFrameworkBase(USER_NAME, USER_PASSWD, filter);
        
        writeToFile(ResourceUtil.getPathToDataSources() + fileName, itemList.getBytes());
    }
    

    /**
     * Get itemList from the current Framework instance on hand of CONTENT_MODEL 
     * @param fileName
     * @throws IOException 
     * @throws URISyntaxException 
     * @throws ServiceException 
     */
    public static String getItemListFromFramework() throws IOException, ServiceException, URISyntaxException
    {
        HashMap<String, String[]> filter = new HashMap<String, String[]>();
        
        filter.put(VERSION, new String[]{"1.1"});
        filter.put(OPERATION, new String[]{SEARCH_RETRIEVE});       
        filter.put(QUERY, new String[]{"\"/properties/content-model/id\"=" + PropertyReader.getProperty(PROPERTY_CONTENT_MODEL_PUBLICATION)});
        filter.put(MAX_RECORDS, new String[]{ITEMS_LIMIT});
        
    	return getItemListFromFrameworkBase(USER_NAME, USER_PASSWD, filter);
    }   
    
    /** 
     * Split item-list document (passed as root element)
     * to the the Node array
     * @param root - document root element
     * @return Node[] of the items
     */
    public static Node[] getItemNodes (Element root)
    {

		NodeList itemElements = root.getChildNodes(); 
		int length = itemElements.getLength( ) ;
		
		//remove all text nodes
		int k = 0;
		for ( int i = 0; i < length; i++ )
		{
			Node n = itemElements.item(k);
			if ( n.getNodeType() == Node.TEXT_NODE )
				root.removeChild(n);
			else
				k++;
		}
		
		itemElements = root.getChildNodes(); 
		length = itemElements.getLength( ) ; 
		Node[] itemsArr = new Node[length];
		
		// clean up doc and populate items array
		for ( int i = 0; i < length; i++ )
		{
			itemsArr[i] = root.removeChild(itemElements.item(0));
		}    	
		return itemsArr;
    }    
    
	private static ArrayList<String> extractTag(String xml, String tag)
	{
		Pattern p = Pattern.compile("<(" +tag +")\\s.*?>(.*?)</\\1>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(xml);
		
		ArrayList<String> al = new ArrayList<String>();
		while (m.find())
		{
			al.add(m.group(2));
		}
		return al; 
	}
	
	public static ArrayList<String> extractBibliographicCitations(String xml)
	{
		return extractTag(xml, "dcterms:bibliographicCitation"); 
	}
	
	public static String extractBibliographicCitation(String xml, String match)
	{
		for (String cit: extractTag(xml, "dcterms:bibliographicCitation"))
		{
//			logger.info(cit);
			if (cit.indexOf(match)>0 && cit.indexOf("span class=\"Default\"")==-1)
			{
				return cit;
			}
		}
		return ""; 
	}
	
	public static ArrayList<String> extractAbstract(String xml)
	{
		return extractTag(xml, "dcterms:abstract"); 
	}    
	
	
	
	
}

