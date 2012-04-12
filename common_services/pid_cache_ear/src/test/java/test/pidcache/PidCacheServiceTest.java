package test.pidcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.PidServiceResponseVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;



/**
 * Test class for Pid Cache Service
 * @author saquet
 *
 */
public class PidCacheServiceTest 
{
	private static final Logger logger = Logger.getLogger(PidCacheServiceTest.class);
	
	private static HttpClient client;
	private static XmlTransforming xmlTransforming = null;
	
	public static String PIDSERVICE_CREATE;
    public static String PIDSERVICE_VIEW ;
    public static String PIDSERVICE_FIND ;
    public static String CACHE_PIDSERVICE ;
	public static String PIDSERVICE_EDIT ;
	public static String PIDSERVICE_DELETE ;
	public static String ITEM_TEST_URL;
	
	
	public String testUrl= null;
	public String testIdentifier = null;

	
	@BeforeClass
	public static void setProperties() throws Exception
	{
		System.out.println("before");
		InitialContext context = new InitialContext();
    	xmlTransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
    	
		CACHE_PIDSERVICE = PropertyReader.getProperty("escidoc.pid.pidcache.service.url");
		PIDSERVICE_CREATE = PropertyReader.getProperty("escidoc.pid.service.create.path");
    	PIDSERVICE_VIEW = PropertyReader.getProperty("escidoc.pid.service.view.path");
    	PIDSERVICE_FIND = PropertyReader.getProperty("escidoc.pid.service.search.path");
    	PIDSERVICE_EDIT = PropertyReader.getProperty("escidoc.pid.service.update.path");
    	PIDSERVICE_DELETE = PropertyReader.getProperty("escidoc.pid.service.delete.path");
    	ITEM_TEST_URL = PropertyReader.getProperty("escidoc.pidcache.dummy.url");
   
    	client = new HttpClient();  	
	}
	
	@Test
	public void init() throws Exception
	{	
	    // Wait until pid cache is surely filled
	    Thread.sleep(20000);
		testUrl = ITEM_TEST_URL.concat(Long.toString(new Date().getTime())).concat("/test");
		this.testAssignPid();
		this.testUpdatePid();
	}
	
	public void testAssignPid() throws Exception
	{
		logger.info("TEST ASSIGN PID for url: " + testUrl);
		PostMethod method = new PostMethod(CACHE_PIDSERVICE.concat(PIDSERVICE_CREATE));
		method.setParameter("url", testUrl);
		
		// Basic authentication
		int port = 80;
		String domain;
		Pattern pattern = Pattern.compile("[^:/]+:/*([^:/]+)(:(\\d+))?(/.*)?");
		Matcher matcher = pattern.matcher(CACHE_PIDSERVICE);
		if (matcher.find())
		{
			domain = matcher.group(1);
			try
			{
				port = Integer.parseInt(matcher.group(3));
			}
			catch (NumberFormatException nfe)
			{
				port = 80;
			}
			logger.info("Pointing to '" + domain + "' at port " + port);
		}
		else
		{
			throw new RuntimeException("PID cache URL '" + CACHE_PIDSERVICE + "' not parsable.");
		}

		client.getState().setCredentials(new AuthScope(domain, port), new UsernamePasswordCredentials(PropertyReader.getProperty("escidoc.pidcache.user.name"), PropertyReader.getProperty("escidoc.pidcache.user.password")));
		method.setDoAuthentication(true);
		
    	ProxyHelper.executeMethod(client, method);
		PidServiceResponseVO pidServiceResponseVO = xmlTransforming.transformToPidServiceResponse(method.getResponseBodyAsString());
		assertNotNull(method.getResponseHeader("Location").getValue());
		logger.info("Location: " +  method.getResponseHeader("Location").getValue());
		testIdentifier = pidServiceResponseVO.getIdentifier();
		assertNotNull(testIdentifier);
		logger.info("PID"  + pidServiceResponseVO.getIdentifier() + " has been assigned.");
		logger.info("done.");
	}
	
	public void testUpdatePid() throws Exception
	{
		testUrl = testUrl.concat("/edition");
		logger.info("TEST UPDATE PID " + testIdentifier + " with new URL: " + testUrl);
		PostMethod method = new PostMethod(CACHE_PIDSERVICE.concat(PIDSERVICE_EDIT).concat("?pid=").concat(testIdentifier));
		method.setParameter("url", testUrl);
    	ProxyHelper.executeMethod(client, method);
		PidServiceResponseVO pidServiceResponseVO = xmlTransforming.transformToPidServiceResponse(method.getResponseBodyAsString());
		assertNotNull(method.getResponseHeader("Location").getValue());
		logger.info("Location: " +  method.getResponseHeader("Location").getValue());
		assertEquals(testIdentifier, pidServiceResponseVO.getIdentifier());
		assertEquals(testUrl, pidServiceResponseVO.getUrl());
		logger.info("PID"  + pidServiceResponseVO.getIdentifier() + " has been updated with " +  pidServiceResponseVO.getUrl());
		logger.info("done.");
	}
	
	public void testAssignPidGwdgNonAvailable()
	{
		logger.info("GWDG not available: Test Create PID with url: " + testUrl + ".");
	}
	
	public void testUpdatePidGwdgNonAvailable() throws Exception
	{
		logger.info("GWDG not available: Test Update PID " + testIdentifier + " with url: " + testUrl + ".");
	}

}
