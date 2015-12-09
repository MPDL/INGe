package de.mpg.escidoc.handler;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.NamingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.main.ComponentPidTransformer;
import de.mpg.escidoc.main.PIDProviderIf;
import de.mpg.escidoc.util.Util;

public class PIDProvider implements PIDProviderIf
{
    private static Logger logger = Logger.getLogger(PIDProvider.class);  
   
    private Properties properties = new Properties();
    private HttpClient httpClient;
    
    private String location;
    private String user;
    private String password;
    private String server;
    
    private static int totalNumberofPidsUpdated = 0;
    
    final static private int port = 80;
       
    public PIDProvider() throws NamingException, IOException
    {
        this.init();
    }
    
    public void init() throws NamingException, IOException
    {
        logger.debug("init starting");
        
        InputStream s = getClass().getClassLoader().getResourceAsStream(ComponentPidTransformer.PROPERTY_FILE_NAME);
		
		if (s != null)
		{
			properties.load(s);
			logger.info(properties.toString());
		}
		else 
		{
			throw new FileNotFoundException("Property file not found " + ComponentPidTransformer.PROPERTY_FILE_NAME);
		}
		
		location = properties.getProperty("pidcache.service.url");
		user = properties.getProperty("pidcache.user.name");
		password = properties.getProperty("pidcache.user.password");

		server = properties.getProperty("pidcache.server");
	        
        httpClient = Util.getHttpClient();
        httpClient.getParams().setAuthenticationPreemptive(true);
        
        logger.debug("init finished");
    }
    
    public int getTotalNumberOfPidsRequested()
    {
        return totalNumberofPidsUpdated;
    }

	@Override
	public String updateComponentPid(String escidocId, String versionNumber, String componentId, String pid,
			String fileName) throws PIDProviderException
	{
	       logger.debug("getPid starting");
	        
	        int code = HttpStatus.SC_OK;
	        String newUrl = "";
	        String pidCacheUrl = location + "/write/modify";
	        
	        PostMethod method = new PostMethod(pidCacheUrl.concat("?pid=").concat(pid));
	        
	        try
	        {
	                newUrl = getRegisterUrlForComponent(escidocId, versionNumber, componentId, fileName);
	        }
	        catch (Exception e)
	        {
	            logger.warn("Error occured when registering Url for <" + escidocId + ">" 
	                                    + ">"  + " and fileName <" + fileName + ">" );
	            throw new PIDProviderException(e.getMessage(), escidocId);
	        }
	        
	        method.setParameter("url", newUrl);
	        
	        long start = System.currentTimeMillis();
	        try
	        {
	            httpClient.getState().setCredentials(new AuthScope(server, port),
	                    new UsernamePasswordCredentials(user, password));
	            
	            code = httpClient.executeMethod(method);
	            
	            if (code == 200)
	            {
	            	totalNumberofPidsUpdated++;
	            }
	            logger.info("pid modify returning " + method.getResponseBodyAsString());
	        }
	        catch (Exception e)
	        {
	            throw new PIDProviderException(e.getMessage(), escidocId);
	        }
	        long end = System.currentTimeMillis();
	        
	        logger.info("Time used for getting pid <" + (end - start) + ">ms");
	       
	        return newUrl;
	}
	
	private String getRegisterUrlForComponent(String itemId, String versionNumber, String componentId, String fileName) throws Exception
    {        
        String registerUrl =  properties.getProperty("pubman.instance.url") +
        		properties.getProperty("pubman.instance.context.path") +
                properties.getProperty("pubman.component.pattern")
                        .replaceAll("\\$1", itemId)
                        .replaceAll("\\$2", versionNumber)
                        .replaceAll("\\$3", Util.getPureComponentId(componentId))
                        .replaceAll("\\$4", fileName);

        logger.info("URL given to PID resolver: <" + registerUrl + ">");
        
        return registerUrl;
    }
}
