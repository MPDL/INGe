package test;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.framework.PropertyReader;


/**
 * 
 * Integration JUnit test for PubMan Syndication Service
 * 
 * @author vmakarenko (initial creation)
 * @author $Author:$ (last modification)
 * @version $Revision:$ $LastChangedDate:$
 * 
 */
public class SyndicationServiceTest 
{
    private Logger logger = Logger.getLogger(getClass());
	
    
    @Test
    public void checkGetFeed() throws Exception
    {

    	String uri;
    	long start;
    	String result;
    	
    	
    	String pubman_url = PropertyReader.getProperty("escidoc.pubman.instance.url");
    	pubman_url = pubman_url.substring(0, pubman_url.indexOf("/pubman")  );
    	logger.info("pubman base url:" + pubman_url);

    	//Thread.sleep(1000L * 60);
    	
    	uri = pubman_url + "/syndication/feed/rss_2.0/releases";
    	start = System.currentTimeMillis();
    	result =  performGetFeed( uri ); 
    	assertTrue( result!= null );
    	logger.info("Processing time: " + (System.currentTimeMillis() - start) );
    	logger.info("URI: " + uri + "\n" + "GENERATED FEED:\n" + result );
//    	Utils.writeToFile("result_rss_093.xml", result);    	
    	
    	uri = pubman_url + "/syndication/feed/atom_1.0/publications/organization/escidoc:persistent22";
    	start = System.currentTimeMillis();
    	result =  performGetFeed( uri );
    	assertTrue( result!= null );
		logger.info("Processing time: " + (System.currentTimeMillis() - start) );
    	logger.info("URI: " + uri + "\n" + "GENERATED FEED:\n" + result );
//    	Utils.writeToFile("result_atom_10.xml", result);
    	
    	
    }    
    
    
    /**
     * Call Syndication Cervice via HTTP request  
     * 
     * @param url
     * @return
     * @throws Exception
     */
    private String performGetFeed(String url) throws Exception 
	{
    	
		logger.info("Search URL:" + url);
		Object content;
		URLConnection uconn;
			uconn = new URL(url).openConnection();
			if ( !(uconn instanceof HttpURLConnection) )
	            throw new IllegalArgumentException(
	                "URL protocol must be HTTP." 
	            );
			HttpURLConnection conn = (HttpURLConnection)uconn;
	
			InputStream stream =  conn.getErrorStream( );
	        if ( stream != null )
	        {
	        	conn.disconnect();
	        	logger.error(getInputStreamAsString( stream ));
	        	return null;
	        }
	        else if ( 
	        		(content = conn.getContent( )) != null && content instanceof InputStream 
	        )
	            content = getInputStreamAsString( (InputStream)content );
	        else
	        {
	        	conn.disconnect();	
	        	logger.error("Cannot retrieve content from the HTTP response");
	        	return null;
	        }
	        conn.disconnect();
	        
			return (String)content;
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
    
    
}
