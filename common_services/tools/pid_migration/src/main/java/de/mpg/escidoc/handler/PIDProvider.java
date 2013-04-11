package de.mpg.escidoc.handler;

import java.io.IOException;

import javax.naming.NamingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.handler.PreHandler.Type;
import de.mpg.escidoc.main.PIDProviderIf;
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
    public String getPid(String escidocId, Type type) throws HttpException, IOException
    {
        logger.debug("getPid starting");
        
        int code;
        String url = location + "/write/create";
        
        PostMethod method = new PostMethod(url);
        
        //String server = Util.getProperty("escidoc.pubman.instance.url");
        String registerUrl =  Util.getProperty("escidoc.pubman.instance.url") +
                Util.getProperty("escidoc.pubman.instance.context.path") +
                Util.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1", escidocId);
        
        method.setParameter("url", registerUrl);
        method.setDoAuthentication(true);
        httpClient.getState().setCredentials(new AuthScope("dev-pubman.mpdl.mpg.de", 8080),
                new UsernamePasswordCredentials(user, password));
        code = httpClient.executeMethod(method);
        
        String pid = Util.getValueFromXml("<pid>", '<', method.getResponseBodyAsString());
        if (code != HttpStatus.SC_CREATED || "".equals(pid))
        {
            throw new IOException("getPid request returned " + code );
        }
        logger.info("pid create returning " + method.getResponseBodyAsString());
        return pid;
    }
}
