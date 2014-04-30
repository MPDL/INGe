package de.mpg.escidoc.pid;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.util.HandleUpdateStatistic;

public class AbstractPidProvider
{
    protected static Logger logger = Logger.getLogger(PidProvider.class);
    protected Map<String, String> successMap;
    protected Map<String, String> failureMap;
    
    protected String latestSuccessFile;
    protected String latestFailureFile;
    

    public String getLatestSuccessFile()
    {
        return latestSuccessFile;
    }

    public String getLatestFailureFile()
    {
        return latestFailureFile;
    }

    public AbstractPidProvider()
    {
        super();
    }

    public void init() throws Exception
    {
        logger.debug("init starting");
        
        this.successMap = new HashMap<String, String>();
        this.failureMap = new HashMap<String, String>();
        
        logger.debug("init finished");
    }

    public void storeResults(HandleUpdateStatistic statistic)
    {
        Date today = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        String date = DATE_FORMAT.format(today);
        
        this.latestSuccessFile = "success-" + date;
        this.latestFailureFile = "failure-" + date;      
       
        try
        {
            FileUtils.writeStringToFile(new File(this.latestSuccessFile), statistic.toString() + System.getProperty("line.separator"));
            
            while (successMap.entrySet().iterator().hasNext())
            {
                Entry<String, String> entry = successMap.entrySet().iterator().next();
                FileUtils.writeStringToFile(new File(this.latestSuccessFile), entry + System.getProperty("line.separator"), true);
            }
            
            while (failureMap.entrySet().iterator().hasNext())
            {
                Entry<String, String> entry = failureMap.entrySet().iterator().next();
                FileUtils.writeStringToFile(new File(this.latestFailureFile), entry + System.getProperty("line.separator"), true);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } 
    }
}