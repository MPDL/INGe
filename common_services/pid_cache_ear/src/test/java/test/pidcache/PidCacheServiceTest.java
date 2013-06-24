package test.pidcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
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
	private static final int NUM_ITEMS = 7;

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
		InitialContext context = new InitialContext();
    	xmlTransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
    	
		CACHE_PIDSERVICE = PropertyReader.getProperty("escidoc.pid.pidcache.service.url");
		logger.info("escidoc.pid.pidcache.service.url = " + CACHE_PIDSERVICE);
		PIDSERVICE_CREATE = PropertyReader.getProperty("escidoc.pid.service.create.path");
    	PIDSERVICE_VIEW = PropertyReader.getProperty("escidoc.pid.service.view.path");
    	PIDSERVICE_FIND = PropertyReader.getProperty("escidoc.pid.service.search.path");
    	PIDSERVICE_EDIT = PropertyReader.getProperty("escidoc.pid.service.update.path");
    	PIDSERVICE_DELETE = PropertyReader.getProperty("escidoc.pid.service.delete.path");
    	ITEM_TEST_URL = PropertyReader.getProperty("escidoc.pidcache.dummy.url");
   
    	client = new HttpClient();  
    	client.getParams().setAuthenticationPreemptive(true);
	}
	
	@Before
	public void setup() throws Exception
	{
        // Wait until pid cache is surely filled
        do
        {
            Thread.sleep(20000);
        }
        while (getCacheSize() < Integer.parseInt(PropertyReader.getProperty("escidoc.pidcache.cache.size.max")));
        logger.info("Setup finished with cache size " + getCacheSize());
        
        testUrl = ITEM_TEST_URL.concat(Long.toString(new Date().getTime())).concat("/test");
	}
	
	@Test
	public void testAssignAndUpdatePid() throws Exception
	{
		logger.info("TEST ASSIGN PID for url: " + testUrl);
		PostMethod method = new PostMethod(CACHE_PIDSERVICE.concat(PIDSERVICE_CREATE));
		method.setParameter("url", testUrl);
		
		doAuthentication();
		method.setDoAuthentication(true);
		
    	ProxyHelper.executeMethod(client, method);
		PidServiceResponseVO pidServiceResponseVO = xmlTransforming.transformToPidServiceResponse(method.getResponseBodyAsString());
		assertNotNull(method.getResponseHeader("Location").getValue());
		logger.info("Location: " +  method.getResponseHeader("Location").getValue());
		testIdentifier = pidServiceResponseVO.getIdentifier();
		assertNotNull(testIdentifier);
		logger.info("PID"  + pidServiceResponseVO.getIdentifier() + " has been assigned.");
	
		testUrl = testUrl.concat("/edition");
		logger.info("TEST UPDATE PID " + testIdentifier + " with new URL: " + testUrl);
		method = new PostMethod(CACHE_PIDSERVICE.concat(PIDSERVICE_EDIT).concat("?pid=").concat(testIdentifier));
		method.setParameter("url", testUrl);
    	ProxyHelper.executeMethod(client, method);
		pidServiceResponseVO = xmlTransforming.transformToPidServiceResponse(method.getResponseBodyAsString());
		assertNotNull(method.getResponseHeader("Location").getValue());
		testIdentifier = pidServiceResponseVO.getIdentifier();
		logger.info("Location: " +  method.getResponseHeader("Location").getValue());
		assertEquals(testIdentifier, pidServiceResponseVO.getIdentifier());
		assertEquals(testUrl, pidServiceResponseVO.getUrl());
		logger.info("PID"  + pidServiceResponseVO.getIdentifier() + " has been updated with " +  pidServiceResponseVO.getUrl());
		logger.info("done.");
	}
	
	@Test
    public void testEmptyQueueAndFillCache() throws Exception
    {
	    PostMethod method = null;
	
	    //fill queue 
        for (int i = 1; i < NUM_ITEMS; i++)
        {
            method = new PostMethod(CACHE_PIDSERVICE.concat(PIDSERVICE_CREATE));
            method.setParameter("url", testUrl + i);
            logger.info("TEST ASSIGN PID for url: " + testUrl + i);
            if (i == 1)
            {
                doAuthentication();
            }
            method.setDoAuthentication(true);
            
            ProxyHelper.executeMethod(client, method);
            PidServiceResponseVO pidServiceResponseVO = xmlTransforming.transformToPidServiceResponse(method.getResponseBodyAsString());
            assertNotNull(method.getResponseHeader("Location").getValue());
            logger.info("Location: " +  method.getResponseHeader("Location").getValue());
            assertEquals(testUrl + i, pidServiceResponseVO.getUrl());
            logger.info("PID"  + pidServiceResponseVO.getIdentifier() + " has been assigned.");
        }
        
        // check if queue will get empty and cache will be refilled
        int actCacheSize = 0;
        int actQueueSize = 0;
        
        while (actCacheSize < Integer.parseInt(PropertyReader.getProperty("escidoc.pidcache.cache.size.max"))
                || actQueueSize > 0)
        {
            int oldCacheSize = actCacheSize;
            int oldQueueSize = actQueueSize;
            
            Thread.currentThread().sleep(6000);
     
            actCacheSize = getCacheSize();
            assertTrue(oldCacheSize <= actCacheSize);
            
            actQueueSize = getQueueSize();
            assertTrue(oldQueueSize >= actQueueSize || oldQueueSize == 0);
        }
    }
	
	private void doAuthentication() throws IOException, URISyntaxException
    {
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
    }
	
	private int getCacheSize() throws Exception
	{
	    int actCacheSize = 0;
	    GetMethod getCacheSizeMethod = new GetMethod(CACHE_PIDSERVICE.concat("/cache/size"));
	    
        doAuthentication();
        getCacheSizeMethod.setDoAuthentication(true);
        ProxyHelper.executeMethod(client, getCacheSizeMethod);
        String response = getCacheSizeMethod.getResponseBodyAsString();
        Matcher matcher = Pattern.compile("[^0-9]+([0-9]+)[^0-9]+").matcher(response);
        if (matcher.find())
        {
            String someNumberStr = matcher.group(1);
            actCacheSize = Integer.parseInt(someNumberStr);
        }
        logger.info("Actual cache size: " + actCacheSize);
        
	    return actCacheSize;
	}
	
	private int getQueueSize() throws Exception
    {
        int actQueueSize = 0;
        GetMethod getQueueSizeMethod = new GetMethod(CACHE_PIDSERVICE.concat("/queue/size"));
        
        doAuthentication();
        getQueueSizeMethod.setDoAuthentication(true);
        ProxyHelper.executeMethod(client, getQueueSizeMethod);
        String response = getQueueSizeMethod.getResponseBodyAsString();
        Matcher matcher = Pattern.compile("[^0-9]+([0-9]+)[^0-9]+").matcher(response);
        if (matcher.find())
        {
            String someNumberStr = matcher.group(1);
            actQueueSize = Integer.parseInt(someNumberStr);
        }
        logger.info("Actual queue size: " + actQueueSize);
        
        return actQueueSize;
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
