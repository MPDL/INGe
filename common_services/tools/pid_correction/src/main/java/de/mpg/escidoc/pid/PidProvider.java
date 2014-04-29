package de.mpg.escidoc.pid;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;



public class PidProvider
{
    private static Logger logger = Logger.getLogger(PidProvider.class);  
    
    private String location;
    private String user;
    private String password;
    private String server;
    
    private HttpClient httpClient;
    
    private Map<String, String> successMap;
    private Map<String, String> failureMap;
    
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
        
        this.successMap = new HashMap<String, String>();
        this.failureMap = new HashMap<String, String>();
        
        logger.debug("init finished");
    }

    private String getRegisterUrl(String itemId) throws Exception
    {
        String registerUrl =  PropertyReader.getProperty("escidoc.pubman.instance.url") +
                PropertyReader.getProperty("escidoc.pubman.instance.context.path") + itemId;
                
        return registerUrl;
    }
    
    public static HttpClient getHttpClient()
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        return httpClient;
    }

    public int updatePid(String pid, String irItemId)
    {
        logger.debug("updatePid starting");
        
        if ("".equals(irItemId))
        {
            successMap.put(irItemId, "");
        }
        
        int code = HttpStatus.SC_OK;
        String newUrl = "";
        String pidCacheUrl = location + "/write/modify";
        
        PostMethod method = null;
        method = new PostMethod(pidCacheUrl.concat("?pid=").concat(pid));
        
        try
        {
            newUrl = getRegisterUrl(irItemId);
            logger.info("Register Url for <" + pid + "> " + " <"+ irItemId + ">");
        }
        catch (Exception e)
        {
            logger.warn("Error occured when registering Url for <" + irItemId + ">");
        }
        
        method.setParameter("url", newUrl);
        
        long start = System.currentTimeMillis();
        try
        {
            httpClient.getState().setCredentials(new AuthScope(server, 8080),
                    new UsernamePasswordCredentials(user, password));
            
            code = httpClient.executeMethod(method);

            if (code != HttpStatus.SC_OK)
            {
                logger.warn("Problem updating a pid <" + pid + ">" + "with newUrl <" + newUrl + ">");
                failureMap.put(pid, newUrl);
            }
            else
            {
                successMap.put(pid, newUrl);
            }
   
            logger.info("pid update returning code <" + code + ">" + method.getResponseBodyAsString());
        }
        catch (Exception e)
        {
            logger.warn("Error occured when registering Url for <" + irItemId + ">" );
        }
        
        long end = System.currentTimeMillis();
        
        logger.info("Time used for updating pid <" + (end - start) + ">ms");
        
        return code;
    }
    
    public int checkToResolvePid(String pid)
    {
        logger.debug("updatePid starting");
        
        //String pidCacheUrl = location + "/read/view";
        
        StringBuffer b = new StringBuffer("http://hdl.handle.net/");
        b.append(pid);

        int code = HttpStatus.SC_OK;
        
        GetMethod method =  new GetMethod(b.toString());
        method.setFollowRedirects(true);
        
        long start = System.currentTimeMillis();
        try
        {
            httpClient.getState().setCredentials(new AuthScope(server, 8080),
                    new UsernamePasswordCredentials(user, password));
         
            code = httpClient.executeMethod(method);

            if (code != HttpStatus.SC_OK)
            {
                logger.warn("Problem when resolving <" + pid + ">");
                failureMap.put(pid, "");
            }
            else
            {
                String response = method.getResponseBodyAsString();
                    successMap.put(pid, response);
            }
   
            logger.info("pid update returning code <" + code + ">" + method.getResponseBodyAsString());
        }
        catch (Exception e)
        {
            logger.warn("Error occured when resolving Url for <" + pid + ">" );
        }
        
        long end = System.currentTimeMillis();
        
        logger.info("Time used for updating pid <" + (end - start) + ">ms");
        
        return code;
        
    }

    public void storeResults()
    {
        try
        {
            FileUtils.writeStringToFile(new File("success"), successMap.toString());
            FileUtils.writeStringToFile(new File("failure"), failureMap.toString());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    
}
