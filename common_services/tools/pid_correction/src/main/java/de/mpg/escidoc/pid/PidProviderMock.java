package de.mpg.escidoc.pid;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;


// Mock object - writes the pids to be updated with its new registerUrl to a file.
public class PidProviderMock
{
    private static Logger logger = Logger.getLogger(PidProvider.class);  

    private Map<String, String> successMap;
    private Map<String, String> failureMap;
    
    public PidProviderMock() throws Exception
    {
        this.init();
    }
    
    public void init() throws Exception
    {
        logger.debug("init starting");
        
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
    
    public int updatePid(String pid, String itemId)
    {
        logger.debug("updatePid starting");
        
        if ("".equals(itemId))
        {
            successMap.put(pid, "NOT USED");
            return 0;
        }
        
        String newUrl = "";
        try
        {
            newUrl = getRegisterUrl(itemId);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        successMap.put(pid, newUrl);

        return 0;
    }

    public void storeResults()
    {
        File success = new File("success");
        File failure = new File("failure");
        try
        {         
                FileUtils.writeLines(success, successMap.entrySet(), false);
                FileUtils.writeLines(failure, failureMap.entrySet(), false);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}

