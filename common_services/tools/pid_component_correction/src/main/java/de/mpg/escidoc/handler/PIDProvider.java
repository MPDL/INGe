package de.mpg.escidoc.handler;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.NamingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.log4j.Logger;

import de.mpg.escidoc.main.PIDProviderIf;
import de.mpg.escidoc.util.Util;

public class PIDProvider implements PIDProviderIf
{
    private static Logger logger = Logger.getLogger(PIDProvider.class);  
   
    private Properties properties = new Properties();
    private HttpClient httpClient;
    
    private static int totalNumberofPidsRequested = 0;
    private static String propFileName = "pidProvider.properties";
    
    public PIDProvider() throws NamingException, IOException
    {
        this.init();
    }
    
    public void init() throws NamingException, IOException
    {
        logger.debug("init starting");
        
        InputStream s = getClass().getClassLoader().getResourceAsStream(propFileName);
		
		if (s != null)
		{
			properties.load(s);
			logger.info(properties.toString());
		}
		else 
		{
			throw new FileNotFoundException("Not found " + propFileName);
		}

        httpClient = Util.getHttpClient();
        httpClient.getParams().setAuthenticationPreemptive(true);
        
        logger.debug("init finished");
    }
    
    public int getTotalNumberOfPidsRequested()
    {
        return totalNumberofPidsRequested;
    }

	@Override
	public String updateComponentPid(String escidocId, String versionNumber, String componentId, String pid,
			String fileName) throws PIDProviderException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
