package de.mpg.escidoc.handler;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import de.mpg.escidoc.main.ComponentPidTransformer;
import de.mpg.escidoc.main.PIDProviderIf;
import de.mpg.escidoc.util.Util;


public class PIDProvider implements PIDProviderIf
{
    private static Logger logger = Logger.getLogger(PIDProvider.class);  
   
    private Properties properties = new Properties();
    private CloseableHttpClient httpClient;
    
    private String location;
    private String user;
    private String password;
    
    private static int totalNumberofPidsUpdated = 0;
       
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
		
		location = properties.getProperty("pid.gwdg.service.url");
		user = properties.getProperty("pid.gwdg.user.login");
		password = properties.getProperty("pid.gwdg.user.password");

        logger.debug("init finished");
    }
    
    public int getTotalNumberOfPidsRequested()
    {
        return totalNumberofPidsUpdated;
    }

	@Override
	public String updateComponentPid(String escidocId, String versionNumber, String componentId, String pid,
			String fileName) throws PIDProviderException, IOException
	{
		logger.debug("getPid starting");
	        
        String newUrl = "";
        String pidCacheUrl = location + "/write/modify";
        
        HttpPost  method = new HttpPost (pidCacheUrl.concat("?pid=").concat(pid));
        
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

        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("url", newUrl));
        method.setEntity((HttpEntity) new UrlEncodedFormEntity(nvps));
        
        long start = System.currentTimeMillis();
        
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY),
                new UsernamePasswordCredentials(user, password));
        
        httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try
        {	            
        	CloseableHttpResponse response = httpClient.execute(method);
            logger.info("pid update returning " + response.getStatusLine());
            
            if (response.getStatusLine().getStatusCode() == 200)
            {
            	totalNumberofPidsUpdated++;
            }	
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
                        .replaceAll("\\$4", URLEncoder.encode(fileName, "UTF-8").replace("+", "%20"));

        logger.info("URL given to PID resolver: <" + registerUrl + ">");
        
        return registerUrl;
    }
}
