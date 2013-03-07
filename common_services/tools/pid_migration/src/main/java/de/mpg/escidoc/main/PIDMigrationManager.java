package de.mpg.escidoc.main;

import java.io.File;
import java.io.IOException;

import javax.naming.NamingException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.util.Util;


public class PIDMigrationManager
{
    private static Logger logger = Logger.getLogger(PIDMigrationManager.class);   
    
    private static String location;
    private static String user;
    private static String password;
    
    private static HttpClient httpClient;
    
    void init() throws NamingException
    {
        logger.debug("init starting");
        
        location = Util.getProperty("escidoc.pid.pidcache.service.url");
        user = Util.getProperty("escidoc.pidcache.user.name");
        password = Util.getProperty("escidoc.pidcache.user.password");
        httpClient = Util.getHttpClient();
        
        logger.debug("init finished");
    }
    
    String getPid() throws HttpException, IOException
    {
        logger.debug("getPid starting");
        
        int code;
        String url = location + "/write/create";
        
        PostMethod method = new PostMethod(url);
        method.setParameter("url", url);
        method.setDoAuthentication(true);
        httpClient.getState().setCredentials(new AuthScope("localhost", 8080),
                new UsernamePasswordCredentials(user, password));
        code = httpClient.executeMethod(method);
        
        String pid = Util.getValueFromXml("<pid>", '<', method.getResponseBodyAsString());
        
        logger.debug("getPid finished returning " + pid);
        return pid;
    }
    
    public void transform(File file) throws Exception
    {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        PreHandler preHandler = new PreHandler();
        PIDHandler handler = new PIDHandler(preHandler);
        
        File tempFile = File.createTempFile("xxx", "yyy", file.getParentFile());
        
        parser.parse(file, preHandler);
        parser.parse(file, handler);
        
        String result = handler.getResult();
        FileUtils.writeStringToFile(tempFile, result);
        
        File oldFile = new File(file.getAbsolutePath());
        File bakFile = new File(file.getAbsolutePath() + ".bak");
        file.renameTo(bakFile);
        tempFile.renameTo(oldFile);
        bakFile.delete();    
    }
    
    public static void main(String[] args)
    {
        PIDMigrationManager pidMigr = new PIDMigrationManager();
        
        try
        {
            pidMigr.init();
        }
        catch (NamingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
