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

package test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.syndication.io.FeedException;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.syndication.Syndication;
import de.mpg.escidoc.services.syndication.SyndicationException;
import de.mpg.escidoc.services.syndication.Utils;

/**
 * JUnit class for eSciDoc syndication manager
 *
 * @author vmakarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */ 
public class SyndicationTest  
{

    private Logger logger = Logger.getLogger(SyndicationTest.class);
	
    private static Syndication sh;
	
	private static XPath xpath;

    /**
     * BeforeClass 
     * @throws SyndicationManagerException 
     * @throws IOException 
     */
    @BeforeClass
    public static void init() throws SyndicationException, IOException
    {
    	sh = new Syndication();
    	XPathFactory factory = XPathFactory.newInstance();
    	xpath = factory.newXPath();
    	
    }
     
    /**
     * Test feeds explain XML
     */
    @Test
    public void checkExplainFeedsXML()
    {
    	assertTrue("Empty explainFeedsXml", Utils.checkVal(sh.explainFeedsXML()));
		logger.info("explainFeedsXML: \n" + sh.explainFeedsXML()); 
    }
    
    /**
     * Check all supported feed formats by feeds in syndication manager
     */
    @Test
    public void checkGetFeedFormatList()
    { 
    	
    	
    	logger.info("feedList---");
    	String[] fl = sh.getFeedList();
    	assertTrue("Empty feed list",  fl != null && fl.length > 0 );
    	for (String f : fl)
    	{
    		logger.info("feed Id: " + f);
        	String[] ffl = sh.getFeedFormatList(f);
        	assertTrue("Empty feed format list",  ffl != null && ffl.length > 0 );
    		for (String ff : ffl) 
    		{
    			logger.info("---format:" + ff);
    		}
    	}
 
    }
    
    /**
     * Check feed generation - Recent Releases
     * 
     * @throws SyndicationException
     * @throws IOException
     * @throws URISyntaxException
     * @throws FeedException
     */
    @Test
    public void checkRecentReleasesFeed() throws Exception
    {

    	String uri;
    	long start;
    	String result;
    	
    	String pubman_url = PropertyReader.getProperty("escidoc.pubman.instance.url");
    	pubman_url = pubman_url.substring(0, pubman_url.indexOf("/pubman")  );
    	logger.info("pubman base url:" + pubman_url);
    	
    	for(String ft: sh.getFeedFormatList(sh.getFeedList()[0]))
    	{
        	uri = pubman_url + "/syndication/feed/" + ft + "/releases";
        	logger.info("URL: " + uri );
        	start = System.currentTimeMillis();
        	result = new String(sh.getFeed(uri));  
        	logger.info("Processing time: " + (System.currentTimeMillis() - start) );
        	assertTrue("Empty Feed",  Utils.checkVal(result) );
        	logger.info("GENERATED FEED:\n" + result );
//        	Utils.writeToFile("result_" + ft + ".xml", result);    	
    	}
    	
    	
    }
    
    /**
     * Check feed generation - Organizational Unit Recent Releases
     * 
     * @throws SyndicationException
     * @throws IOException
     * @throws URISyntaxException
     * @throws FeedException
     */
    @Test
    public void checkOrganizationalUnitFeed() throws Exception
    {
    	
    	String uri;
    	long start;
    	String result;
    	
    	String pubman_url = PropertyReader.getProperty("escidoc.pubman.instance.url");
    	pubman_url = pubman_url.substring(0, pubman_url.indexOf("/pubman")  );
    	logger.info("pubman base url:" + pubman_url);
    	
    	for(String ft: sh.getFeedFormatList(sh.getFeedList()[1]))
    	{
    		uri = pubman_url + "/syndication/feed/" + ft + "/publications/organization/escidoc:persistent22";
    		logger.info("URL: " + uri );
    		start = System.currentTimeMillis();
    		result = new String(sh.getFeed(uri));  
    		logger.info("Processing time: " + (System.currentTimeMillis() - start) );
    		assertTrue("Empty Feed",  Utils.checkVal(result) );
    		logger.info("GENERATED FEED:\n" + result );
//        	Utils.writeToFile("result_" + ft + ".xml", result);    	
    	}
    	
    	
    }
    
    /**
     * Check OrganizationalUnitList retrieval
     * @throws Exception
     */
    @Test
    @Ignore
    public final void testOrganizationalUnitList() throws Exception     
	{ 
    	 TreeMap<String, String> outm = Utils.getOrganizationUnitTree(); 
    		String selOrgUnit="";
    		for( Map.Entry<String, String> entry: outm.entrySet() )
    		{
    			String key = entry.getKey(); 
    			String value = entry.getValue();
    			selOrgUnit += "<option value=\"" + value + "\""
    				+ (
    					key.equals("External Organizations") ? " SELECTED":
    					 ""
    				) 
    				+ " >" + key +"</option>\n";
    		}
    		
    		logger.info(selOrgUnit);
    	
	}


}
