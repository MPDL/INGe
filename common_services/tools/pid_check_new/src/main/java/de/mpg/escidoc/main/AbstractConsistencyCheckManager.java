package de.mpg.escidoc.main;

import gov.loc.www.zing.srw.service.SRWPort;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.pid.PidProvider;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.util.Statistic;

public abstract class AbstractConsistencyCheckManager
{
    protected static Logger logger = Logger.getLogger(AbstractConsistencyCheckManager.class);
    protected SRWPort searchHandler;
    protected ItemHandler itemHandler;
    protected String userHandle;
    protected Statistic statistic;
    protected PidProvider pidProvider;
    
    
    protected static String gwdgUserLogin = null;
    

    public AbstractConsistencyCheckManager() throws IOException, URISyntaxException
    {
        super();
        
        gwdgUserLogin = PropertyReader.getProperty("escidoc.pid.gwdg.user.login");
    }
    
    protected  void init() throws Exception
    {  
    	this.init("escidoc_all");
    }  
    
    protected  void init(String searchIndex) throws Exception
    {  
        pidProvider = new PidProvider(); 
        
        try
        {
            this.userHandle = AdminHelper.loginUser(
                    PropertyReader.getProperty("framework.admin.username"),
                    PropertyReader.getProperty("framework.admin.password"));
            
            searchHandler = ServiceLocator.getSearchHandler(searchIndex, new URL(PropertyReader.getFrameworkUrl()), userHandle);
            
            itemHandler = ServiceLocator.getItemHandler(userHandle);
        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }  
    
    public Set<String> getObjectsToCorrect(File objects) throws Exception
    {
        Set<String> objectsToCorrect = new HashSet<String>();
        
        if (!objects.exists())
        {
            return objectsToCorrect;
        }
        LineIterator lit = FileUtils.lineIterator(objects);
        
        while(lit.hasNext())
        {
            String object = lit.next();
            if (object != null && !"".equals(object.trim()))
                objectsToCorrect.add(object.trim());
        }
        
        return objectsToCorrect;
    }

    public void verifySet(Set<String> objects) throws Exception
    {    
        pidProvider = new PidProvider();
        
        getStatistic().setObjectsTotal(objects.size());
        
        try
        {
            for (String object : objects)
            {
            	doResolve(object);
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        finally
        {
            pidProvider.storeResults(getStatistic());          
        }
        
    } 
    
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
    
    protected boolean isValid(String pid)
    {
        if (pid == null || "".equals(pid))
            return false;
        
        if (!pid.contains(gwdgUserLogin))
            return false;
        
        return true;
    }


    /**
     * abstract methods - subclass responsibility
     */    
//    abstract public void createOrCorrectSet(Set<String> pids) throws Exception;
    
    abstract protected void doResolve(String object) throws Exception;
    
    abstract protected Statistic getStatistic();

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
        
        /*if (pidFileName == null || "".equals(pidFileName))
            usage("pidFileName may not be null or empty or the file does not exists.");*/
        if (mode == null || (!mode.contains("update") && !mode.contains("verify")))
            usage("Mode should be <update> or <verify>");
        
        String checkClass = PropertyReader.getProperty("escidoc.pid_check.consistencycheck.implementation.class");
        IConsistencyCheckManager manager = (IConsistencyCheckManager)(Class.forName(checkClass)).newInstance();
        
        Set<String> pids = manager.getObjectsToCorrect(new File(pidFileName));
        
        if (mode.contains("update"))
        {            
            manager.createOrCorrectSet(pids);
        }
        if (mode.contains("verify"))
        {   
            manager.verifySet(pids);
        }
    }
}