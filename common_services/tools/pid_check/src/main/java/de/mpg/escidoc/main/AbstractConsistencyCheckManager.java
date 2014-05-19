package de.mpg.escidoc.main;

import gov.loc.www.zing.srw.service.SRWPort;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.handler.SrwSearchResponseHandler;
import de.mpg.escidoc.pid.PidProvider;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.util.HandleUpdateStatistic;

public abstract class AbstractConsistencyCheckManager
{
    protected static Logger logger = Logger.getLogger(AbstractConsistencyCheckManager.class);
    protected SRWPort searchHandler;
    protected String userHandle;
    protected SrwSearchResponseHandler srwSearchResponseHandler;
    protected HandleUpdateStatistic statistic;

    public static void usage(String message)
    {
        System.out.print("***** " + message + " *****\n");
        System.out.print("Usage: ");
        System.out.println("java <pidFile> update|verify");
        
        System.out.println("  pidFile\tThe file containing the problem pids (one pid per line)");
        
        System.out.println("  -update\t\tSend the corresponing update requests to the Handle Service");
        System.out.println("  -verify\t\tVerify that the former update requests have been successful.");
    
        System.exit(-1);
    }

    

    public void verifyList(List<String> pidsCorrected) throws Exception
    {
        PidProvider pidProvider = new PidProvider();
        statistic = new HandleUpdateStatistic();
        
        try
        {
            for (String pid : pidsCorrected)
            {
            	statistic.incrementHandlesTotal();
                pidProvider.checkToResolvePid(pid, statistic);
                Thread.currentThread().sleep(1*1000);
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        finally
        {
            pidProvider.storeResults(statistic);          
        }
        
    }

    protected  void init()
    {   
        try
        {
            this.userHandle = AdminHelper.loginUser(
                    PropertyReader.getProperty("framework.admin.username"),
                    PropertyReader.getProperty("framework.admin.password"));
            
            searchHandler = ServiceLocator.getSearchHandler("escidoc_all", new URL(ServiceLocator.getFrameworkUrl()), userHandle);
        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }


    abstract protected List<String> getPidsToCorrect(File pids) throws Exception, URISyntaxException;
    
    abstract public void correctList(List<String> pids) throws Exception;
    

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length < 2)
            usage("Wrong number of parameters....");
        
        String pidFileName = args[0];
        String mode = args[1];       
        
        if (pidFileName == null || "".equals(pidFileName))
            usage("pidFileName may not be null or empty or the file does not exists.");
        if (mode == null || (!mode.contains("update") && !mode.contains("verify")))
            usage("Mode should be <update> or <verify>");
        
        String checkClass = PropertyReader.getProperty("escidoc.pid_check.consistencycheck.implementation.class");
        IConsistencyCheckManager manager = (IConsistencyCheckManager)(Class.forName(checkClass)).newInstance();
        
        List<String> pidsToCorrect = manager.getPidsToCorrect(new File(pidFileName));
        
        if (mode.contains("update"))
        {            
            manager.correctList(pidsToCorrect);
        }
        if (mode.contains("verify"))
        {   
            manager.verifyList(pidsToCorrect);
        }
    }

    public AbstractConsistencyCheckManager()
    {
        super();
    }
}