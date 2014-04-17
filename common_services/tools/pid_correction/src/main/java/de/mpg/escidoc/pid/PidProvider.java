package de.mpg.escidoc.pid;


import javax.naming.NamingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.handler.SrwSearchResponseHandler;
import de.mpg.escidoc.services.framework.PropertyReader;


public class PidProvider
{
    private static Logger logger = Logger.getLogger(PidProvider.class);  
    
    private String location;
    private String user;
    private String password;
    private String server;
    
    private HttpClient httpClient;
    
    public PidProvider() throws Exception
    {
        this.init();
    }
    
    public void init() throws Exception
    {
        logger.debug("init starting");
        
        location = PropertyReader.getProperty("escidoc.pidcache.service.url");
        user = PropertyReader.getProperty("escidoc.pidcache.user.name");
        password = PropertyReader.getProperty("escidoc.pidcache.user.password");
        
        server = PropertyReader.getProperty("escidoc.pidcache.server");
        
        httpClient = getHttpClient();
        httpClient.getParams().setAuthenticationPreemptive(true);
        
        logger.debug("init finished");
    }

    private String getRegisterUrlForItem(String itemId) throws Exception
    {
        String registerUrl =  PropertyReader.getProperty("escidoc.pubman.instance.url") +
                PropertyReader.getProperty("escidoc.pubman.instance.context.path") +
                PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1", itemId);
        return registerUrl;
    }
    
    private String getRegisterUrlForComponent(String componentId, String fileName) 
    {
       return null;
    }
    
    public static HttpClient getHttpClient()
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        return httpClient;
    }

    public void sendRegisterUrl(SrwSearchResponseHandler srwSearchResponseHandler)
    {
        // TODO Auto-generated method stub
        
    }
}
