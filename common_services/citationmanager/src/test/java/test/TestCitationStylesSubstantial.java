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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.adm.AdminHandler;
import de.escidoc.www.services.om.ContextHandler;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;
import de.mpg.escidoc.services.citationmanager.xslt.CitationStyleExecutor;
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

	private static final String USER_NAME = "citman_user"; 
	private static final String USER_PASSWD = "citman_user";
	private static final String CONTEXT = "Citation Style Testing Context";
    
	
	   //     
		/*"<param>" 
		+  "<filter name=\"/properties/context/title\">" + CONTEXT +"</filter>"
	    + "</param>";*/
	
	/*private static final String FILTER_CITATION_STYLE_CONTEXT = 
		"<param>" 
		+  "<filter name=\"/properties/name\">" + CONTEXT +"</filter>"
		+ "</param>";*/

	private static final String CITATION_STYLE_TEST_USER_ACCOUNT_FILE_NAME = "backup/CitationStyleTestUserAccount.xml"; 
	private static final String CITATION_STYLE_TEST_USER_GRANTS_FILE_NAME = "backup/CitationStyleTestUserGrants.xml"; 
	private static final String CITATION_STYLE_TEST_CONTEXTS_FILE_NAME = "backup/CitationStyleTestContexts.xml"; 
	private static final String CITATION_STYLE_TEST_COLLECTION_FILE_NAME = "backup/CitationStyleTestCollection.xml"; 
	
	
	private static String userHandle, adminHandle;   
	private static UserAccountHandler uah_user, uah_admin; 
	
	private static HashMap<String, String[]> filterMap = new HashMap<String, String[]>();
	   
	private static CitationStyleExecutor cse;

	 
	 @BeforeClass
	 public static void setUp() throws Exception
	 {
	     filterMap.clear();
		 cse = new CitationStyleExecutor();
	 }
	 
	 /**
     * Tests all citation styles snippets from file 
     * 
     * @throws Exception
     */
	 @Test
	 @Ignore
	 public final void testCitationStylesSnippetGeneration() throws Exception  {

		 for ( String cs: cse.getStyles() ) 
		 {
			 testCitationStyleSnippetGeneration(cs);
		 }
	 }

	 
	 @Test
	 @Ignore
	 public final void testCitationStyleTestSnippetGeneration() throws Exception  {

		 testCitationStyleSnippetGeneration("Test");

	 }
	 
	 
    public final void testCitationStyleSnippetGeneration(String cs) throws Exception  {

    	int FAILED = 0;
    	String generatedCit;
    	String expectedCit;
    	StringBuffer failedCits = new StringBuffer();
    	String itemList;
    	

    	logger.info("Citation Style: " + cs);
    	Properties tp = TestHelper.getTestProperties(cs);
    	
    	if ("false".equalsIgnoreCase(tp.getProperty("substantial.skip.test")))
    	{
    		String FILE_NAME = tp.getProperty("substantial.test.xml");
    		String EXPECTED_KEY = tp.getProperty("substantial.expected.key");
    		String EXPECTED_XPATH = tp.getProperty("substantial.expected.xpath");
    		String SNIPPET_XPATH = tp.getProperty("substantial.snippet.placeholder.xpath");

    		//get items from file
    		itemList = TestHelper.getCitationStyleTestXmlAsString(FILE_NAME);

    		Document doc = XmlHelper.createDocument(itemList);
    		Element root = doc.getDocumentElement();

    		Node[] itemsArr = TestHelper.getItemNodes(root);

    		assertFalse("No items have been found", itemsArr.length==0);

    		for ( int i = 0; i < itemsArr.length; i++ )
    		{
    			root.appendChild(itemsArr[i]);
    			Node n = itemsArr[i];
    			String objid = n.getAttributes().getNamedItem("objid") + "";  
    			logger.info("item: " + i + ", " + objid);

    			//generate text citation form the current item
    			//    			logger.info( "item:" + XmlHelper.outputString(doc));

    			String snippet = new String(cse.getOutput(cs, "escidoc_snippet", XmlHelper.outputString(doc)));
    			logger.info( "snippet:" + snippet);

    			Node snippetNode = XmlHelper.xpathNode(SNIPPET_XPATH, snippet); 

    			generatedCit = snippetNode.getTextContent();
    			logger.info( "generated citation:" + generatedCit );

    			//get expected result from the abstract field 
    			Node checkNode = XmlHelper.xpathNode(EXPECTED_XPATH, doc);
    			String comment = objid + ", xpath:" + EXPECTED_XPATH + ", item:" + XmlHelper.outputString(doc);
    			assertNotNull("expected citation has not been found for " + comment, checkNode);
    			expectedCit = checkNode.getTextContent();
    			assertNotNull("expected citation element is empty for " + comment, checkNode);
    			expectedCit = expectedCit.replaceFirst("^" + EXPECTED_KEY , "");
    			//    			 logger.info( "expected citation:" + expectedCit );

    			//compare generated and expected items
    			if ( !diffStrings(generatedCit, expectedCit) )
    			{
    				FAILED++;
    				failedCits.append(
    						"\n" + "Item: " + (i + 1) + ", " + objid  
    						+ "\nGenerated citation:\n"
    						+ "[" +generatedCit + "]"
    						+ "\n does not match expected citation:\n"
    						+ "[" + expectedCit + "]"
    				);
    			}

    			root.removeChild(itemsArr[i]);
    		}
    		if (FAILED != 0)
    		{
    			logger.info("I found " + FAILED + " wrong generated citation(s):" 
    					+ failedCits.toString());
    			Assert.fail();
    		}
    	} 
    	else 
    	{
    		logger.info("Ignore substantial test for citation style: " + cs);
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
//    	backupUser();
//    	backupContext();
//    	backupItems();
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
//    	restoreContext();
//    	restoreGrants();
////    	for (int i = 1; i <= 35; i++) {
//    		restoreItems();	
//		}
        
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
    		userXml = uah_admin.retrieve(USER_NAME);
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
    	userXml = TestHelper.getFileAsString(CITATION_STYLE_TEST_USER_ACCOUNT_FILE_NAME); 
    	if ( !Utils.checkVal(userXml) )
    	{
    		logger.info("empty user account xml");
    		return false;
    	}
    		
    	//remove objd
    	userXml = Pattern.compile("objid=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(userXml).replaceFirst("");

    	
//    	logger.info("user:" + userXml);
    	
    	//create user
    	uah_admin.create( userXml );
    	

    	
    	//change password
    	userXml = uah_admin.retrieve(USER_NAME);
    	String user_id = XmlHelper.xpathString("/user-account/@objid", userXml);
    	String ldm = XmlHelper.xpathString("/user-account/@last-modification-date", userXml);;
    	
//    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'");
//    	df.setTimeZone(TimeZone.getTimeZone("GMT"));
//    	String ldm = df.format(new Date(System.currentTimeMillis()));
    	
    	uah_admin.updatePassword(
    			user_id,
    		"<param last-modification-date=\"" + ldm + "\">"
    	+ 		"<password>" + USER_PASSWD + "</password>"
    	+	"</param>"
    			
    	);
    	
    	return true;
	}
    


	/**
     * Saves Items of the Context into the file  
     * @throws Exception
     */
	
	public void backupItems() throws Exception
    {
	    filterMap.put("query", new String[]{"\"/properties/title\"=" + CONTEXT});
	    
    	String itemList = ServiceLocator.getItemHandler(userHandle).retrieveItems( filterMap );    	
    	writeToFile(CITATION_STYLE_TEST_COLLECTION_FILE_NAME, itemList);
    }
    
	/**
     * Saves Context into the file  
     * @throws Exception
     */
    public void backupContext() throws Exception
    {
        filterMap.put("query", new String[]{"\"/properties/title\"=" + CONTEXT});
        
    	ContextHandler ch = ServiceLocator.getContextHandler(userHandle);
    	String contextList = ch.retrieveContexts(filterMap); 
    	writeToFile(CITATION_STYLE_TEST_CONTEXTS_FILE_NAME+ ".xml", contextList);
    }

    
	/**
     * Saves User Account and Grants assigned to the user into the file  
     * @throws Exception
     */
    public void backupUser() throws Exception
    { 
    	//GET USER ACCOUNT INFO 
    	String userXml = uah_user.retrieve(USER_NAME);
    	writeToFile(CITATION_STYLE_TEST_USER_ACCOUNT_FILE_NAME + ".xml", userXml);
    	
    	//GET GRANTS FOR THE USER 
    	//get user_id for citman_user
        String user_id = getUserId(userXml);
        
        String grants = uah_admin.retrieveCurrentGrants(user_id);
    	writeToFile(CITATION_STYLE_TEST_USER_GRANTS_FILE_NAME + ".xml", grants);
    }
    
    /*
private static void administration(String userHandle) throws Exception
   {
       File f = new File("/home/frank/data/escidoc/VIRR/ingestion/cleanup/ids.txt");
       AdminHandler admHandler = ServiceLocator.getAdminHandler(userHandle);
                 String param = createTaskParam(f);
           //admHandler.deleteObjects(param);
           System.out.println(admHandler.getPurgeStatus());
         }
     private static String createTaskParam(File file) throws Exception
   {
       BufferedReader reader = new BufferedReader(new FileReader(file));
       StringBuffer ids = new StringBuffer("<param>");
       String line;
       while ((line = reader.readLine()) != null)
       {
           ids.append("<id>" + line + "</id>");
       }
       ids.append("</param>");
       System.out.println(ids.toString());
       return ids.toString();
   }     * 
     */
    
    public void purgeItems() throws ServiceException, URISyntaxException, InvalidXmlException, SystemException, RemoteException
    {
    	AdminHandler ah = ServiceLocator.getAdminHandler(adminHandle);
    	String param = //"<param><id>escidoc:25202</id></param>";
//"<param><id>escidoc:25207</id><id>escidoc:25201</id><id>escidoc:25200</id><id>escidoc:25199</id><id>escidoc:25198</id><id>escidoc:25197</id><id>escidoc:25196</id><id>escidoc:25195</id><id>escidoc:25194</id><id>escidoc:25193</id><id>escidoc:25192</id><id>escidoc:25191</id><id>escidoc:25190</id><id>escidoc:25189</id><id>escidoc:25188</id><id>escidoc:25187</id><id>escidoc:25186</id><id>escidoc:25185</id><id>escidoc:25184</id><id>escidoc:25183</id><id>escidoc:25182</id><id>escidoc:25181</id><id>escidoc:25180</id><id>escidoc:25179</id><id>escidoc:25178</id><id>escidoc:25177</id><id>escidoc:25176</id><id>escidoc:25175</id><id>escidoc:25174</id><id>escidoc:25173</id><id>escidoc:25172</id><id>escidoc:25171</id><id>escidoc:25170</id><id>escidoc:25169</id><id>escidoc:25168</id><id>escidoc:25167</id><id>escidoc:25166</id><id>escidoc:25165</id><id>escidoc:25164</id><id>escidoc:25163</id><id>escidoc:25162</id><id>escidoc:25161</id><id>escidoc:25160</id><id>escidoc:25159</id><id>escidoc:25158</id><id>escidoc:25157</id><id>escidoc:25156</id><id>escidoc:25155</id><id>escidoc:25154</id><id>escidoc:25153</id><id>escidoc:25152</id><id>escidoc:25151</id><id>escidoc:25150</id><id>escidoc:25149</id><id>escidoc:25148</id><id>escidoc:25147</id><id>escidoc:25146</id><id>escidoc:25145</id><id>escidoc:25144</id><id>escidoc:25143</id><id>escidoc:25142</id><id>escidoc:25141</id><id>escidoc:25140</id><id>escidoc:25139</id><id>escidoc:25138</id><id>escidoc:25137</id><id>escidoc:25136</id><id>escidoc:25135</id><id>escidoc:25134</id><id>escidoc:25133</id><id>escidoc:25132</id><id>escidoc:25131</id><id>escidoc:25130</id><id>escidoc:25129</id><id>escidoc:25128</id><id>escidoc:25127</id><id>escidoc:25126</id><id>escidoc:25125</id><id>escidoc:25124</id><id>escidoc:25123</id><id>escidoc:25122</id><id>escidoc:25121</id><id>escidoc:25120</id><id>escidoc:25119</id><id>escidoc:25118</id><id>escidoc:25117</id><id>escidoc:25116</id><id>escidoc:25115</id><id>escidoc:25114</id><id>escidoc:25113</id><id>escidoc:25112</id><id>escidoc:25111</id><id>escidoc:25110</id><id>escidoc:25109</id><id>escidoc:25108</id><id>escidoc:25107</id><id>escidoc:25106</id><id>escidoc:25105</id><id>escidoc:25104</id><id>escidoc:25103</id></param>";    	
    	"<param><id>escidoc:24655</id></param>";    	
    	ah.deleteObjects(param);
    	logger.info("deletion status:" + ah.getPurgeStatus());
    }
    
    
	/**
     * Get <code>objid</code> of the User   
     * @param userXml is User Account Xml
     * @return objid of the User   
     * @throws Exception
     */
    private String getUserId(String userXml) throws Exception
    {
        return XmlHelper.xpathString("//user-account[1]/@objid", userXml); 
    }
    
    private void writeToFile(String fileName, String content) throws IOException
    {
    	TestHelper.writeToFile(
    			ResourceUtil.getPathToTestResources() + 
    			fileName, 
    			content.getBytes()
    	);     	
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

}
