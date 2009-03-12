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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.sf.jasperreports.engine.util.JRXmlUtils;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.om.ContextHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test class for citation manager processing component
 * Can be started from eclipse. 
 * The system property <code>citation.style</code> is used to define 
 * citation style to test. 
 * See <code>resource/CitationStyles/<CitationStyle>/test.properties</code> for definition of the tests. 
 * The following fields are obligatory to define the correct test:
 *    
 * @author Vladislav Makarenko (initial)
 * @author $Author$ (last change)
 * @version $Revision$ $LastChangedDate$
 */ 

public class TestCitationStylesSubstantial {

    private Logger logger = Logger.getLogger(getClass());
	
    protected static final String PROPERTY_USERNAME_ADMIN = "framework.admin.username";
    protected static final String PROPERTY_PASSWORD_ADMIN = "framework.admin.password";

	private static final String USER_NAME = "citman_user"; 
	private static final String USER_PASSWD = "citman_user";
	private static final String CONTEXT = "Citation Style Testing Context";
    
	private static final String FILTER_CITATION_USER = 
		"<param>" + 
    		"<filter name=\"user\">" + USER_NAME + "</filter>" +
    	"</param>";

	private static final String FILTER_CITATION_STYLE_TEST_COLLECTION = 
		"<param>" 
		+  "<filter name=\"/properties/context/title\">" + CONTEXT +"</filter>"
	    + "</param>";
	
	private static final String FILTER_CITATION_STYLE_CONTEXT = 
		"<param>" 
		+  "<filter name=\"/properties/name\">" + CONTEXT +"</filter>"
		+ "</param>";
	
	private static final String LMD_FORMAT = "<param last-modification-date=\"%s\"/>";

	private static final String CITATION_STYLE_TEST_XMLS_DIRECTORY = "src/test/resources/backup";
	private static final String CITATION_STYLE_TEST_USER_ACCOUNT_FILE_NAME = "CitationStyleTestUserAccount.xml"; 
	private static final String CITATION_STYLE_TEST_USER_GRANTS_FILE_NAME = "CitationStyleTestUserGrants.xml"; 
	private static final String CITATION_STYLE_TEST_CONTEXTS_FILE_NAME = "CitationStyleTestContexts.xml"; 
	private static final String CITATION_STYLE_TEST_COLLECTION_FILE_NAME = "CitationStyleTestCollection.xml"; 
	
	
	private static String userHandle, adminHandle;   
	private static UserAccountHandler uah_user, uah_admin; 
	
	

	private static CitationStyleHandler pcs;    
	private static XPath xpath;
	 
	 @BeforeClass
	 public static void setUp() throws Exception
	 {
		 pcs = new ProcessCitationStyles();
		 userHandle = TestHelper.loginUser(USER_NAME, USER_PASSWD);
		 uah_user = ServiceLocator.getUserAccountHandler(userHandle);
		 adminHandle = TestHelper.loginUser(PropertyReader.getProperty(PROPERTY_USERNAME_ADMIN), PropertyReader.getProperty(PROPERTY_PASSWORD_ADMIN));
		 uah_admin = ServiceLocator.getUserAccountHandler(adminHandle);
		 XPathFactory factory = XPathFactory.newInstance();
		 xpath = factory.newXPath();
	 }
	 
	 /**
     * Tests all citation styles substantial 
     * 
     * @throws Exception
     */
    @Test
    public final void testCitationStylesGeneration() throws Exception  {

    	int FAILED = 0;
    	String generatedCit;
    	String expectedCit;
    	StringBuffer failedCits = new StringBuffer();
    	String itemList;
    	
		ItemHandler ih = ServiceLocator.getItemHandler(userHandle);

    	// for all citation styles
    	for (String cs : /*pcs.getStyles()*/ new String[]{"APA"} )    	
    	{

    		logger.info("Citation Style: " + cs);
    		Properties tp = TestHelper.getTestProperties(cs);
    		
    		//get item list from framework
    		boolean IS_IGNORE_MULTIPLY_SPACES = tp.getProperty("ignore.multiply.spaces").equals("yes");
    		//    	
    		String USER = tp.getProperty("data.source.user");
    		String PASSWD = tp.getProperty("data.source.password");
    		String FILTER = tp.getProperty("data.source.filter");
    		String EXPECTED_KEY = tp.getProperty("data.source.expected.key");
    		String EXPECTED_XPATH = tp.getProperty("data.source.expected.xpath");

    		//get items from framework
    		itemList = ih.retrieveItems( FILTER_CITATION_STYLE_TEST_COLLECTION );
//    		itemList = getFileAsString(CITATION_STYLE_TEST_COLLECTION_FILE_NAME);

    		NodeList nodes = xpathNodeList("/item-list/item", itemList);

    		assertFalse("No items have been found", nodes.getLength()==0);
    		
    		for (int i = 0; i < nodes.getLength(); i++) 
    		{
    			Node n = nodes.item(i);
    			String objid = n.getAttributes().getNamedItem("objid") + "";  
    			logger.info(objid);
    			Document tmpDoc = JRXmlUtils.createDocument(n);

    			//generate text citation form the current item
    			generatedCit = new String(pcs.getOutput(cs, "txt", XmlHelper.outputString(tmpDoc)));
    			generatedCit = cleanCit(generatedCit);
    			generatedCit = generatedCit.replaceFirst("^.*" + cs +"\\s+?", "");
    			//    	    logger.info( "generated citation:" + generatedCit );

    			//get expected result from the abstract field 
    			Node checkNode = xpathNode(EXPECTED_XPATH, tmpDoc);
    			String comment = objid + ", xpath:" + EXPECTED_XPATH + ", item:" + XmlHelper.outputString(tmpDoc);
    			assertNotNull("expected citation has not been found for " + comment, checkNode);
    			expectedCit = checkNode.getTextContent();
    			assertNotNull("expected citation element is empty for " + comment, checkNode);
    			expectedCit = cleanCit(expectedCit);
    			expectedCit = expectedCit.replaceFirst("^" + EXPECTED_KEY , "");
    			//    	    logger.info( "expected citation:" + expectedCit );

    			//compare generated and expected items
    			if ( !diffStrings(generatedCit, expectedCit) )
    			{
    				FAILED++;
    				failedCits.append(
    						"\n " + objid  
    						+ "\nThe generated citation:\n"
    						+ "[" +generatedCit + "]"
    						+ "\n does not match expected citation:\n"
    						+ "[" + expectedCit + "]"
    				);
    			}

    		}
    		assertTrue(
    				"There (is/are) " + FAILED + " wrong generated citation(s):" 
    				+ failedCits.toString()
    				, FAILED == 0
    		);

    	}
    }

	/* CITATION STYLE TEST COLLECTION MANAGEMENT BLOCK */
    /**
     * Saves complete bundle of the XMLs related to citation style testing collection:
     * 1. User account 
     * 2. Context 
     * 3. Grants
     * 4. Testing items
     * into the XML files
     * @throws Exception
     */
    @Test
    @Ignore
    public void backupAll() throws Exception
    {
    	backupUser();
    	backupContext();
    	backupItems();
    }
    
    /**
     * Restores the bundle of the XML files needed 
     * to create citation style testing collection 
     * in the current eSciDoc framework  
     * 
     * @throws Exception
     */
    @Test
    @Ignore
    public void restoreAll() throws Exception
    {
    	restoreUser();
    	restoreContext();
    	restoreGrants();
    	restoreItems();
    }
    
    
	/**
     * Creates User Account
     * @return <code>true</code> if successful, <code>false</code> otherwise   
     * @throws Exception
     */
    private boolean restoreUser() throws Exception
    {
    	String userXml = null;
    	//check user
    	
    	try 
    	{
    		userXml = uah_user.retrieve(USER_NAME);
    	}
    	catch (Exception e) 
    	{
		}
    	if (userXml != null ){
    		logger.info("The user:" + USER_NAME +" already exists.");
    		return false;
    	}
    	
    	//user does not exist, create them
    	//get saved xml 
    	userXml = getFileAsString(CITATION_STYLE_TEST_USER_ACCOUNT_FILE_NAME); 
    	if ( !Utils.checkVal(userXml) )
    	{
    		logger.info("empty user account xml");
    		return false;
    	}
    		
    	//remove objd
    	userXml = Pattern.compile("objid=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(userXml).replaceFirst("");
    	
    	//create user
    	uah_admin.create( userXml );
    	
    	return true;
	}
    
    private boolean restoreItems() throws Exception 
    {
		//load from file
    	String itemList = getFileAsString(CITATION_STYLE_TEST_COLLECTION_FILE_NAME);
    	if ( !Utils.checkVal(itemList) )
    	{
    		//file is empty
    		logger.error("Empty itemList xml");
    		return false;
    	}
    	
    	ItemHandler ih = ServiceLocator.getItemHandler(userHandle);    	

    	String context_id = getContextId();
    	
    	if ( !Utils.checkVal(context_id)  )
    	{
    		logger.error("No context has been found");
    		return false;
    	}
    	
    	NodeList nodes = xpathNodeList("/item-list/item", itemList);
    	
    	if ( nodes == null || nodes.getLength()==0  )
    	{
    		logger.error("No items have been found in itemList");
    		return false;
    	}

    	
    	for (int i = 0; i < nodes.getLength(); i++) 
    	{
    		Node n = (Node) nodes.item(i);
    		
    		//remove old objid of the item
    		((Element)n).removeAttribute("objid");
    		Document tmpDoc = JRXmlUtils.createDocument(n);
    		
    		//replace props
    		for ( String expr : new String[] {
    				"latest-release",
    				"created-by",
    				"public-status",
    				"latest-version",
    				"version"
    		} )
    		{
        		Node nnn = xpathNode("/item/properties/" + expr, tmpDoc);
        		nnn.getParentNode().removeChild(nnn);
    		}
    		
    		//set context_id
    		xpathNode("/item/properties/context/@objid", tmpDoc).setNodeValue(context_id);
    		
    		String itemXml = XmlHelper.outputString(tmpDoc);
    		logger.info("item to be created: " + itemXml);

    		//create item
    		String createdItemXml = ih.create(itemXml);
    		logger.info("created item: " + createdItemXml);

    		String item_id = xpathString("/item/@objid", createdItemXml);
    		String last_modification_date = xpathString("/item/@last-modification-date", createdItemXml);
    		//submit item
    		last_modification_date = ih.submit(item_id, String.format(LMD_FORMAT, last_modification_date));
    		last_modification_date = xpathString("/result/@last-modification-date", last_modification_date);
    		
    		//assignVersionPid
    		last_modification_date = ih.assignVersionPid(item_id + ":1", "<param last-modification-date=\"" + last_modification_date + "\">" + "<url>http://localhost</url>" + "</param>");
    		last_modification_date = xpathString("/result/@last-modification-date", last_modification_date);
    		
    		//release item
    		last_modification_date = ih.release(item_id, String.format(LMD_FORMAT, last_modification_date));
    		
    	}    	
    	
    	return true;
		
	}

	/**
     * Restores Context
     * @return <code>true</code> if successful, <code>false</code> otherwise   
     * @throws Exception
     */
	private boolean restoreContext() throws Exception 
    {
//    	//check test context
    	ContextHandler ch = ServiceLocator.getContextHandler(userHandle);
    	String contextList = ch.retrieveContexts(FILTER_CITATION_STYLE_CONTEXT);
    	Double count =  xpathNumber("/context-list/@number-of-records", contextList);
    	if ( count > 0 )
    	{
    		//already exists, go away
    		logger.info("context " + CONTEXT + " already exists.");
    		return false;
    	}
    	
    	//context does not exist, create it 
    	String contextXml = getFileAsString(CITATION_STYLE_TEST_CONTEXTS_FILE_NAME);
    	if ( !Utils.checkVal(contextXml) )
    	{
    		//file is empty
    		logger.error("empty context xml");
    		return false;
    	}
    	
    	Document doc = JRXmlUtils.createDocument(xpathNode("/context-list/context", contextXml));
    	
    	//clean up xml for creation 
    	Element root = doc.getDocumentElement();  
    	root.removeAttribute("objid");
    	root.removeAttribute("last-modification-date");
    	
    	//replace props
		for ( String expr : new String[] {
				"creation-date",
				"created-by",
				"modified-by",
				"public-status",
				"public-status-comment",
		})
		{
    		Node nnn = xpathNode("/context/properties/" + expr, doc);
    		nnn.getParentNode().removeChild(nnn);
		}    	
    	
		contextXml = XmlHelper.outputString(doc);
//		contextXml = Utils.replaceAllTotal(contextXml, "<(([\\w]+:)?creation-date).*?\\1>", "");
////		contextXml = Utils.replaceAllTotal(contextXml, "<[\\w]+:?created-by.*?>", "");
//		contextXml = Utils.replaceAllTotal(contextXml, "<([\\w]+:)?modified-by.*?>", "");
//		contextXml = Utils.replaceAllTotal(contextXml, "<(([\\w]+:)?public-status-comment).*?\\1>", "");

    	ch = ServiceLocator.getContextHandler(adminHandle);
    	
    	//create new context with admin account
    	contextXml = ch.create(contextXml);
    	
    	doc = createDocument(contextXml);
    	String new_objid = xpathString("/context/@objid", doc);
    	String last_modification_date = xpathString("/context/@last-modification-date", doc);

    	//open context
		String result = ch.open(new_objid, String.format(LMD_FORMAT, last_modification_date));
    	logger.info( "open context result: " + result );
    	
    	return true;
		
	}

    
	/**
	 * restores Grants for the Context on the User
	 * @return <code>true</code> if successful, <code>false</code> otherwise   
	 * @throws Exception
	 */
	private boolean restoreGrants() throws Exception
	{
		//set grants
		//get user account xml 
		String userXml = uah_user.retrieve(USER_NAME);
		if ( !Utils.checkVal(userXml) )
		{
			logger.info("Cannot find created user account");
			return false;
		}

		String user_id = getUserId(userXml);
		logger.info("user_name:" + USER_NAME + ", user_id: " + user_id);

		String user_creator_id = getCreatorOfUserId(userXml);

		String context_id = getContextId();
		logger.info("context_name:" + CONTEXT + ", context_id: " + context_id);


		String grantsXml = getFileAsString(CITATION_STYLE_TEST_USER_GRANTS_FILE_NAME);

		Document document = createDocument(grantsXml);

		//get namespaces from the root element
		NamedNodeMap nnm = document.getDocumentElement().getAttributes();

		NodeList nodes = xpathNodeList("/current-grants/grant", document);

		for (int i = 0; i < nodes.getLength(); i++) 
		{
			Node n = (Node) nodes.item(i);
			//remove old objid of the grant
			((Element)n).removeAttribute("objid");
			Document tmpDoc = JRXmlUtils.createDocument(n);
			//set namespaces taken from root element
			Element re = tmpDoc.getDocumentElement(); 
			for (int j = 0; j < nnm.getLength(); j++) 
			{
				Node nn = nnm.item(j);
				re.setAttribute(nn.getNodeName(), nn.getNodeValue());
			}

			//set new user_id
			xpathNode("/grant/properties/assigned-on/@objid", tmpDoc).setNodeValue(context_id);
			//set user creator id
			xpathNode("/grant/properties/created-by/@objid", tmpDoc).setNodeValue(user_creator_id);

			String grantXml = XmlHelper.outputString(tmpDoc);
			//		grantXml = Utils.replaceAllTotal(grantXml, "(<([\\w]+:)?assigned-on\\s+objid=\")[^\"]+(\"\\s*/>)", "$1" + context_id + "$3");
			//		grantXml = Utils.replaceAllTotal(grantXml, "(<([\\w]+:)?created-by\\s+objid=\")[^\"]+(\"\\s*/>)", "$1" + user_creator_id + "$3");

			logger.info("grant: " + grantXml);

			//grants the permission for user_id
			uah_admin.createGrant(user_id, grantXml);
		}
		return true;
	}


	/**
     * Saves Items of the Context into the file  
     * @throws Exception
     */
	public void backupItems() throws Exception
    {
    	String itemList = ServiceLocator.getItemHandler(userHandle).retrieveItems( FILTER_CITATION_STYLE_TEST_COLLECTION );    	
    	writeToFile(CITATION_STYLE_TEST_COLLECTION_FILE_NAME, itemList);
    }
    
	/**
     * Saves Context into the file  
     * @throws Exception
     */
    public void backupContext() throws Exception
    {
    	ContextHandler ch = ServiceLocator.getContextHandler(userHandle);
    	String contextList = ch.retrieveContexts(FILTER_CITATION_STYLE_CONTEXT); 
    	writeToFile(CITATION_STYLE_TEST_CONTEXTS_FILE_NAME, contextList);
    }

    
	/**
     * Saves User Account and Grants assigned to the user into the file  
     * @throws Exception
     */
    public void backupUser() throws Exception
    {
    	//GET USER ACCOUNT INFO 
    	String userXml = uah_user.retrieve(USER_NAME);
    	writeToFile(CITATION_STYLE_TEST_USER_ACCOUNT_FILE_NAME, userXml);
    	
    	//GET GRANTS FOR THE USER 
    	//get user_id for citman_user
        String user_id = getUserId(userXml);
        
        String grants = uah_admin.retrieveCurrentGrants(user_id);
    	writeToFile(CITATION_STYLE_TEST_USER_GRANTS_FILE_NAME, grants);
    }
    
    
	/**
     * Get <code>objid</code> of the Context against the name of it   
     * @return objid of the Context   
     * @throws Exception
     */
    private String getContextId() throws Exception 
    {
    	ContextHandler ch = ServiceLocator.getContextHandler(userHandle);
    	String contextList = ch.retrieveContexts(FILTER_CITATION_STYLE_CONTEXT);
    	return xpathString("/context-list/context/@objid", contextList);
	}
    
	/**
     * Get <code>objid</code> of the User   
     * @param userXml is User Account Xml
     * @return objid of the User   
     * @throws Exception
     */
    private String getUserId(String userXml) throws Exception
    {
        return xpathString("//user-account[1]/@objid", userXml); 
    }
    
	/**
     * Get <code>objid</code> of the Creator of the User   
     * @param userXml is User Account Xml
     * @return objid of the Creator of the User    
     * @throws Exception
     */
    private String getCreatorOfUserId(String userXml) throws Exception
    {
        return xpathString("//user-account[1]/properties/created-by/@objid", userXml); 
    }
    
    
    private Document createDocument(String xml) throws Exception
    {
    	return JRXmlUtils.parse(new ByteArrayInputStream(xml.getBytes()));
    }
    
    
    private String xpathString(String expr, String xml) throws Exception
    {
    	return xpathString(expr, createDocument(xml)); 
    }
    
    private String xpathString(String expr, Document doc) throws Exception
    {
    	return (String) xpath.evaluate(
    				expr, 
    				doc, 
    				XPathConstants.STRING
    			);
    }
    
    private Double xpathNumber(String expr, String xml) throws Exception
    {
    	return xpathNumber(expr, createDocument(xml)); 
    }
    
    private Double xpathNumber(String expr, Document doc) throws Exception
    {
    	return (Double) xpath.evaluate(
    			expr, 
    			doc, 
    			XPathConstants.NUMBER
    	);
    }
    
    private NodeList xpathNodeList(String expr, String xml) throws Exception
    {
    	return xpathNodeList(expr, createDocument(xml)); 
    }
    
    private NodeList xpathNodeList(String expr, Document doc) throws Exception
    {
    	return (NodeList) xpath.evaluate(
    			expr, 
    			doc, 
    			XPathConstants.NODESET
    	);
    }
    
    private Node xpathNode(String expr, String xml) throws Exception
    {
    	return xpathNode(expr, createDocument(xml)); 
    }
    
    private Node xpathNode(String expr, Document doc) throws Exception
    {
    	return (Node) xpath.evaluate(
    			expr, 
    			doc, 
    			XPathConstants.NODE
    	);
    }
    
    private String getFileAsString(String fileName) throws IOException
    {
    	return ResourceUtil.getResourceAsString(getPathToTestResources() + fileName);
    }
    
    private void writeToFile(String fileName, String content) throws IOException
    {
    	TestHelper.writeToFile(
    			getPathToTestResources() + 
    			fileName, 
    			content.getBytes()
    	);     	
    }
    
    private String getPathToTestResources()
    {
    	return CITATION_STYLE_TEST_XMLS_DIRECTORY + "/";
    }
    
    /**
     * Compares two strings 
     * @param str1
     * @param str2
     * @return
     */
    private boolean diffStrings(String str1, String str2)
    {
    	if ( str1.length() != str2.length())
    	{
    		logger.info("strings have different lengths. str1:" + str1.length() + ",str2:" + str2.length());
    		return false;
    	}
    	int i=0;
    	for(char ch1 : str1.toCharArray())
    	{
    		char ch2 = str2.charAt(i);
    		if (ch1 != ch2)
    		{
    			logger.info(
    					"difference at index: " + (i+1)
    					+ ", str1:[" + ch1 + ",int(" + (int)ch1 + ")], str2:[" + ch2 + ",int(" + (int)ch2 + ")]" 		
    			);
    			return false;
    		}
    		i++;
    	}
    	logger.info("strings are equal");
    	return true;
    }
	
    private static String cleanCit(String str) {
    	if (Utils.checkVal(str)) 
    		str = str.replaceAll("[\\s\t\r\n]+", " ");
	    	str = str.replaceAll("\\s+$", "");
    	return str;
    }    

}
