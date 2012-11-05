package de.mpg.escidoc.pubman.installer.panels;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.pubman.installer.Configuration;

public class TestConfigurationCreatorPanel
{
    
    
    static Configuration authConfig = null;
    static Configuration pubmanConfig = null;
    
    private Logger logger = Logger.getLogger(TestConfigurationCreatorPanel.class);
    
    @BeforeClass
    public static void init() throws IOException
    {
        authConfig = new Configuration("auth.properties");
        pubmanConfig = new Configuration("pubman.properties");
    }
    
    
    
    @Test
    public void storeXml()
    {
        try
        {
            authConfig.storeXml("conf.xml", "c://tmp//jboss//server//default//conf//conf.xml.new");
        }
        catch (IOException e)
        {            
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void storeProperties()
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
    }
}
