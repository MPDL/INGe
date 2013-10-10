package de.mpg.escidoc.pubman.installer;

import org.apache.commons.httpclient.HttpClient;
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
    
    public void startEscidoc() throws Exception
    {
        this.panel.getTextArea().append("Starting eSciDoc Framework...\n");
        logger.info("Starting eSciDoc Framework");
        try
        {
            waitForFrameworkStarted();
        }
        catch (Exception e)
        {
            logger.error("Error while starting the eSciDoc Framework!", e);
        }
    }
    
    private void waitForFrameworkStarted() throws Exception
    {
        GetMethod method = new GetMethod(PropertyReader.getProperty(Configuration.KEY_INSTANCEURL));
        HttpClient client = new HttpClient();
        
        do
        {
//            client.getHttpConnectionManager().getParams().setConnectionTimeout(20);
            ProxyHelper.executeMethod(client, method);
            Thread.currentThread().sleep(5000);
        }
        while (method.getStatusCode() != 200);
        if (method.getStatusCode() == 200)
        {
            return;
        }
        return;
    }
    
    public void run()
    {
        synchronized (this)
        {
            try
            {
                startEscidoc();
                panel.processFinishedSuccessfully("eSciDoc Framework started successfully!", this.getName());
                logger.info("eSciDoc Framework started successfully!");
            }
            catch (Exception e)
            {
                panel.processFinishedWithError("Error or timeout when starting the eSciDoc Framework!", e,
                        this.getName());
                logger.error("Error during starting eSciDoc Framework", e);
            }
            notify();
        }
    }
}
