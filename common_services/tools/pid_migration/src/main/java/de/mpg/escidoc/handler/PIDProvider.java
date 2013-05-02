package de.mpg.escidoc.handler;


import javax.naming.NamingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.handler.PreHandler.Type;
import de.mpg.escidoc.main.PIDProviderIf;
import de.mpg.escidoc.util.SQLQuerier;
import de.mpg.escidoc.util.Util;

public class PIDProvider implements PIDProviderIf
{
    private static Logger logger = Logger.getLogger(PIDProvider.class);  
    
    private static String location;
    private static String user;
    private static String password;
    
    private static HttpClient httpClient;
    
    public PIDProvider() throws NamingException
    {
        this.init();
    }
    
    public void init() throws NamingException
    {
        logger.debug("init starting");
        
        location = Util.getProperty("escidoc.pidcache.service.url");
        user = Util.getProperty("escidoc.pidcache.user.name");
        password = Util.getProperty("escidoc.pidcache.user.password");
        
        httpClient = Util.getHttpClient();
        
        logger.debug("init finished");
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.main.PIDProviderIf#getPid()
     */
    @Override
    public String getPid(String escidocId, Type type, String fileName) throws PIDProviderException
    {
        logger.debug("getPid starting");
        
        int code;
        String registerUrl = "";
        String pidCacheUrl = location + "/write/create";
        
        PostMethod method = new PostMethod(pidCacheUrl);
        
        try
        {
            if (type.equals(Type.ITEM))
            {
                registerUrl = getRegisterUrlForItem(escidocId);
            }
            else if (type.equals(Type.COMPONENT))
            {
                registerUrl = getRegisterUrlForComponent(escidocId, fileName);
            }
        }
        catch (Exception e)
        {
            logger.warn("Error occured when registering Url for <" + escidocId + ">" 
                                    + " of type <" + type + ">"  + " and fileName <" + fileName + ">" );
            throw new PIDProviderException(e);
        }
        
        method.setParameter("url", registerUrl);
        method.setDoAuthentication(true);
        
        String pid;
        try
        {
            httpClient.getState().setCredentials(new AuthScope(Util.getProperty("escidoc.pidcache.server"), 8080),
                    new UsernamePasswordCredentials(user, password));
            
            code = httpClient.executeMethod(method);
            
            pid = Util.getValueFromXml("<pid>", '<', method.getResponseBodyAsString());
            if (code != HttpStatus.SC_CREATED || "".equals(pid))
      
            logger.info("pid create returning " + method.getResponseBodyAsString());
        }
        catch (Exception e)
        {
            throw new PIDProviderException(e.getMessage(), e);
        }
       
        return pid;
    }

    private String getRegisterUrlForItem(String itemId)
    {
        String registerUrl =  Util.getProperty("escidoc.pubman.instance.url") +
                Util.getProperty("escidoc.pubman.instance.context.path") +
                Util.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1", itemId);
        return registerUrl;
    }
    
    private String getRegisterUrlForComponent(String componentId, String fileName) throws Exception
    {
        SQLQuerier querier = new  SQLQuerier();
        String itemId = querier.getItemIdForComponent(componentId);
        
     // todo if (itemId == null) ...
                    
        String registerUrl =  Util.getProperty("escidoc.pubman.instance.url") +
                Util.getProperty("escidoc.pubman.instance.context.path") +
                Util.getProperty("escidoc.pubman.component.pattern")
                        .replaceAll("\\$1", itemId)
                        .replaceAll("\\$2", componentId)
                        .replaceAll("\\$3", fileName);

        logger.debug("URL given to PID resolver: " + registerUrl);
        
        return registerUrl;
    }
}
