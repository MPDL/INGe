package de.mpg.escidoc.pubman.installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.installer.panels.IConfigurationCreatorPanel;
import de.mpg.escidoc.services.framework.ProxyHelper;

public class StartEscidocProcess extends Thread
{
    private IConfigurationCreatorPanel panel;
    private String installPath;
    private boolean startFinished = false;

    private static Logger logger = Logger.getLogger(StartEscidocProcess.class);
    
    private static final String JBOSS_CONF_PATH = "/jboss/server/default/conf/";
    private static final String INDEX_PROPERTIES = "index.properties";
    
    public StartEscidocProcess()
    {        
    }
    
    public StartEscidocProcess(IConfigurationCreatorPanel panel)
    {
        this.panel = panel;    
        this.installPath = panel.getInstallPath();
        this.setName("StartEscidocProcess");
    }
    
    public int startEscidocAndWaitTillFinished() throws Exception
    {
        int code = 0;
        
        this.panel.getTextArea().append("Starting eSciDoc Framework...\n");
        logger.info("Starting eSciDoc Framework");
        
        // copy stylesheets for indexing to jboss index configuration
        updateIndexConfiguration();
              
        startEscidoc();
        
        // give the StartEscidocProcess thread some time to start eSciDoc
        int count = 0;
        do
        {
            Thread.currentThread().sleep(5000);
            try
            {
                code = waitForFrameworkStarted();
            }
            catch (IOException e)
            {
                logger.info("IOException caught when waiting for eSciDoc start", e);
            }
            count++;
        } while( code != 200 && count < 5);
        
        
        return waitForFrameworkStarted();      
    }
    
    public boolean isStartFinished()
    {
        return startFinished;
    }
    
    private void startEscidoc() throws Exception
    {        
            String osName = System.getProperty("os.name" );
            String[] cmd = new String[3];
            if( osName.startsWith("Windows" ) )
            {
                cmd[0] = "cmd" ;
                cmd[1] = "/C" ;
                cmd[2] = "run.bat";
            }
            else if( osName.startsWith( "Linux" ) )
            {
                cmd[0] = "/bin/sh" ;
                cmd[1] = "/C" ;
                cmd[2] = "run.sh";
            }
            
            ProcessBuilder pb =
                  new ProcessBuilder(cmd);
                  
                  pb.directory(new File("c:/escidoc.pubman/jboss/bin"));
                  File log = new File("c:/escidoc.pubman/jboss/server/default/log/server.log");
                  pb.redirectErrorStream(true);
                  
            Process process = pb.start();
            
            StreamGobbler g = new StreamGobbler(process.getInputStream());
            g.start();       
    }
    
    private int waitForFrameworkStarted() throws HttpException, IOException, URISyntaxException
    {
        GetMethod method = new GetMethod(panel.getInstanceUrl());        
        HttpClient client = new HttpClient();
        
        client.getHttpConnectionManager().getParams().setConnectionTimeout(3*60*1000);
        ProxyHelper.executeMethod(client, method);
        
        return method.getStatusCode();
    }
    
    // TODO catch IOException in method
    void updateIndexConfiguration() throws Exception
    {
        StringBuffer out = new StringBuffer(4096);
        File indexProperties = new File(new StringBuffer(2048).append(installPath).append(JBOSS_CONF_PATH).append("search/config/index/escidoc_all").toString(), 
                                               INDEX_PROPERTIES);
        File indexPropertiesBak = new File(indexProperties.getAbsolutePath() + ".bak");
        FileUtils.copyFile(indexProperties, indexPropertiesBak);
        LineIterator lit = new LineIterator(new FileReader(indexProperties));
        
        while(lit.hasNext())
        {
            String line = lit.nextLine();

            if (line.endsWith("escidocXmlToLucene"))
            {
                line = line.replaceAll("escidocXmlToLucene", "mpdlEscidocXmlToLucene");
            }
            out.append(line);
            out.append("\n");
        }
        
        FileUtils.writeStringToFile(indexProperties, out.toString());       
        FileUtils.forceDelete(indexPropertiesBak);
    }
    
    public void run()
    {
        try
        {
            int code = startEscidocAndWaitTillFinished();
            if (code == 200)
            {
                startFinished = true;
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
    
    // helper class to redirect output from ProcessBuilder
    class StreamGobbler extends Thread
    {
        InputStream is;
               
        StreamGobbler(InputStream is)
        {
            this.is = is;
        }
        
        public void run()
        {
            try
            {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null)
                    System.out.println(line);
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }
}
