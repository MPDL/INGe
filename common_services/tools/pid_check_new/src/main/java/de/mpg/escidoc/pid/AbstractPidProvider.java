package de.mpg.escidoc.pid;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.util.Statistic;

public class AbstractPidProvider
{
    protected static Logger logger = Logger.getLogger(PidProvider.class);
    protected Map<String, String> successMap;
    protected Map<String, String> failureMap;
    
    protected String latestSuccessFile;
    protected String latestFailureFile;
    
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

    public String getLatestSuccessFile()
    {
        return latestSuccessFile;
    }

    public String getLatestFailureFile()
    {
        return latestFailureFile;
    }

    public Map<String, String>getFailureMap()
    {
        return this.failureMap;
    }
    
    public Map<String, String>getSuccessMap()
    {
        return this.successMap;
    }

    public void storeResults(Statistic statistic)
    {
        Date today = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        String date = DATE_FORMAT.format(today);
        
        this.latestSuccessFile = "success_" + date;
        this.latestFailureFile = "failure_" + date;      
       
        try
        {
            FileUtils.writeStringToFile(new File(this.latestSuccessFile), "*********************************************************" + System.getProperty("line.separator"), true);
            FileUtils.writeStringToFile(new File(this.latestSuccessFile), date + System.getProperty("line.separator"), true);
            FileUtils.writeStringToFile(new File(this.latestSuccessFile), "*********************************************************" + System.getProperty("line.separator"), true);
            FileUtils.writeStringToFile(new File(this.latestSuccessFile), statistic.toString() + System.getProperty("line.separator"), true);
            
            Iterator<Entry<String, String>> itSuc = successMap.entrySet().iterator();
            while (itSuc.hasNext())
            {
                Entry<String, String> entry = (Entry<String, String>)itSuc.next();
                FileUtils.writeStringToFile(new File(this.latestSuccessFile), entry.toString().replace("=http", " | http") + System.getProperty("line.separator"), true);
            }
            
            FileUtils.writeStringToFile(new File(this.latestFailureFile), "*********************************************************" + System.getProperty("line.separator"), true);
            FileUtils.writeStringToFile(new File(this.latestFailureFile), date + System.getProperty("line.separator"), true);
            FileUtils.writeStringToFile(new File(this.latestFailureFile), "*********************************************************" + System.getProperty("line.separator"), true);
            Iterator<Entry<String, String>> itFail = failureMap.entrySet().iterator();
            while (itFail.hasNext())
            {
                Entry<String, String> entry = (Entry<String, String>)itFail.next();
                FileUtils.writeStringToFile(new File(this.latestFailureFile), entry.toString().replace("=http", " | http") + System.getProperty("line.separator"), true);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } 
    }
}