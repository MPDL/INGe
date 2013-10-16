package de.mpg.escidoc.pubman.installer;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.installer.panels.ConfigurationCreatorPanel;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;

public class StartEscidocProcess extends Thread
{
    private ConfigurationCreatorPanel panel;
    private static Logger logger = Logger.getLogger(StartEscidocProcess.class);
    
    public StartEscidocProcess()
    {        
    }
    
    public StartEscidocProcess(ConfigurationCreatorPanel panel)
    {
        this.panel = panel;
        this.setName("StartEscidocProcess");
    }
    
    public int startEscidoc() throws Exception
    {
        this.panel.getTextArea().append("Starting eSciDoc Framework...\n");
        logger.info("Starting eSciDoc Framework");
        
        return waitForFrameworkStarted();      
    }
    
    private int waitForFrameworkStarted() throws HttpException, IOException, URISyntaxException
    {
        GetMethod method = new GetMethod(PropertyReader.getProperty(Configuration.KEY_INSTANCEURL));        
        HttpClient client = new HttpClient();
        
        client.getHttpConnectionManager().getParams().setConnectionTimeout(3*60*1000);
        ProxyHelper.executeMethod(client, method);
        
        return method.getStatusCode();
    }
    
    public void run()
    {
        try
        {
            int code = startEscidoc();
            if (code == 200)
            {
                panel.processFinishedSuccessfully("eSciDoc Framework started successfully!", this.getName());
                logger.info("eSciDoc Framework started successfully!");
            }
            else 
            {
                panel.processFinishedWithError("eSciDoc Framework returned with code <" + code + ">", new Exception(),
                        this.getName());
                logger.error("eSciDoc Framework returned with code <" + code + ">");
            }
        }
        catch (Exception e)
        {
            panel.processFinishedWithError("Error or timeout when starting the eSciDoc Framework!", e,
                    this.getName());
            logger.error("Error during starting eSciDoc Framework", e);
        }
    }
}
