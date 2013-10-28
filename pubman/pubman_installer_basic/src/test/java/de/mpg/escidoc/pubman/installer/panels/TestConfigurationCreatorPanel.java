package de.mpg.escidoc.pubman.installer.panels;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.izforge.izpack.installer.InstallData;

import de.mpg.escidoc.pubman.installer.Configuration;


public class TestConfigurationCreatorPanel
{
    
    
    private static final String HTTP_N107_MPDL_MPG_DE = "http://10.20.2.75:8080";
    private static final String HTTP_LOCALHOST = "http://localhost:8080";
    
    private static Map<String, String> properties = new HashMap<String, String>();
    private static Configuration authConfig = null;
    private static Configuration pubmanConfig = null;
    
    private Logger logger = Logger.getLogger(TestConfigurationCreatorPanel.class);
    
    @BeforeClass
    public static void init() throws IOException
    {
        
        properties.put(Configuration.KEY_CORESERVICE_URL, HTTP_LOCALHOST);
        properties.put(Configuration.KEY_CORESERVICE_LOGIN_URL, HTTP_LOCALHOST);
        properties.put(Configuration.KEY_AUTH_INSTANCE_URL, HTTP_LOCALHOST);
        
        authConfig = new Configuration("auth.properties");
        authConfig.setProperties(properties);
        
        pubmanConfig = new Configuration("pubman.properties");
        pubmanConfig.setProperties(properties);
    }
    
    @AfterClass
    public static void clean() throws IOException
    {
        authConfig.removeProperties(properties);
        pubmanConfig.removeProperties(properties);
    }
    
    
    
    @Test
    @Ignore
    public void storeXml()
    {
        
        try
        {
            authConfig.storeXml("conf.xml", "c://tmp//jboss//server//default//conf//conf.xml");
        }
        catch (IOException e)
        {            
            e.printStackTrace();
            fail();
        }
        
        InputStream inStream = null;
        try
        {
            int ch = 0;
            StringBuffer b = new StringBuffer();
            inStream = new FileInputStream("c://tmp//jboss//server//default//conf//conf.xml");
            assertTrue(inStream != null);
            while( (ch = inStream.read()) != -1)
                b.append((char)ch);
            inStream.close();
            assertTrue(b.toString().contains(HTTP_N107_MPDL_MPG_DE));
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    @Ignore
    public void storePubmanProperties()
    {
        
        try
        {
            pubmanConfig.storeProperties("pubman.properties", "c://tmp//jboss//server//default//conf//pubman.properties");
        }
        catch (IOException e)
        {            
            e.printStackTrace();
            fail();
        }
        
        InputStream inStream = null;
        Properties inProps = new Properties();
        try
        {
            inStream = new FileInputStream("c://tmp//jboss//server//default//conf//pubman.properties");
            inProps.load(inStream);
            
            assertTrue(inProps.get(Configuration.KEY_CORESERVICE_URL).equals(HTTP_N107_MPDL_MPG_DE));
            assertTrue(inProps.get(Configuration.KEY_CORESERVICE_LOGIN_URL).equals(HTTP_N107_MPDL_MPG_DE));
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    @Ignore
    public void storeAuthProperties()
    {
        
        try
        {
            authConfig.storeProperties("auth.properties", "c://tmp//jboss//server//default//conf//auth.properties");
        }
        catch (IOException e)
        {            
            e.printStackTrace();
            fail();
        }
        
        InputStream inStream = null;
        Properties inProps = new Properties();
        try
        {
            inStream = new FileInputStream("c://tmp//jboss//server//default//conf//auth.properties");
            inProps.load(inStream);
            
            assertTrue(inProps.get(Configuration.KEY_CORESERVICE_URL).equals(HTTP_N107_MPDL_MPG_DE));
            assertTrue(inProps.get(Configuration.KEY_CORESERVICE_LOGIN_URL).equals(HTTP_N107_MPDL_MPG_DE));
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }
}
