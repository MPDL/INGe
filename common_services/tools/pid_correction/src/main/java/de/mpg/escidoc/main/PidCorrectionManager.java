package de.mpg.escidoc.main;

import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import gov.loc.www.zing.srw.service.SRWPort;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import de.mpg.escidoc.handler.SrwSearchResponseHandler;
import de.mpg.escidoc.pid.PidProviderMock;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;


public class PidCorrectionManager
{
    /*
    static String[] pidsToCorrect = {"11858/00-001Z-0000-0023-673A-F",  //Version pid of escidoc:6728221:1
        "11858/00-001M-0000-0013-B522-3",
        "11858/00-001M-0000-0013-B25E-6",
        "11858/00-001M-0000-0013-B448-8",
        "11858/00-001M-0000-0013-B2AE-3"
    };
    */
    
    private static Logger logger = Logger.getLogger(PidCorrectionManager.class);  
    
    private SRWPort searchHandler;
    private String userHandle;
    private SrwSearchResponseHandler srwSearchResponseHandler;
    
    public void correctList(List<String> pids) throws Exception
    {
        PidProviderMock pidProvider = new PidProviderMock();
        try
        {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            
            for (String pid: pids)
            {            
                RecordType record = this.searchForPid(pid);
                
                if (record == null) 
                {
                    pidProvider.updatePid(pid, "");
                    continue;       
                }
                
                File tmp = FileUtils.getFile(FileUtils.getTempDirectory(), "pid");
                FileUtils.writeStringToFile(tmp, record.getRecordData().get_any()[0].getAsString(), "UTF-8");
                
                srwSearchResponseHandler = new SrwSearchResponseHandler();
                srwSearchResponseHandler.setPidToSearchFor(pid);
                parser.parse(tmp, srwSearchResponseHandler);
                
                if (srwSearchResponseHandler.isObjectPid())
                    pidProvider.updatePid(pid, srwSearchResponseHandler.getItemUrl()); 
                else if (srwSearchResponseHandler.isVersionPid())
                {
                    pidProvider.updatePid(pid, srwSearchResponseHandler.getVersionUrl()); 
                }
                else if (srwSearchResponseHandler.isComponentPid())
                {
                    pidProvider.updatePid(pid, srwSearchResponseHandler.getComponentUrl()); 
                } 
                FileUtils.deleteQuietly(tmp);
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        finally
        {
            pidProvider.storeResults();          
        }
    } 

    private void init(String url)
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

    private RecordType searchForPid(String pid) throws Exception
    {
        StringBuffer cql = new StringBuffer("escidoc.metadata=");
        
        cql.append("\"hdl:");
        cql.append(pid);
        cql.append("\"");
                
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType(); 
        
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery(cql.toString());
        searchRetrieveRequest.setRecordPacking("xml");
        
        logger.info("searchRetrieveRequest query <" + searchRetrieveRequest.getQuery() + ">");
        
        searchHandler.searchRetrieveOperation(searchRetrieveRequest);
        
        SearchRetrieveResponseType searchResult = searchHandler.searchRetrieveOperation(searchRetrieveRequest);
        if (searchResult.getDiagnostics() != null)
        {
             // something went wrong
            for (DiagnosticType diagnostic : searchResult.getDiagnostics().getDiagnostic())
            {
                    logger.info("diagnostic <" + diagnostic.getDetails() + ">");
            }
        }
        
        switch (searchResult.getNumberOfRecords().intValue())
        {
            case 1:
                return searchResult.getRecords().getRecord(0);
            case 0:
            default:
                return null;
        }  
    }
    
    private List<String> getPidsToCorrect(File pids) throws Exception, URISyntaxException
    {
        List<String> pidsToCorrect = new ArrayList<String>();
        LineIterator lit = FileUtils.lineIterator(pids);
        
        while(lit.hasNext())
        {
            String pid = lit.next();
            if (pid != null && !"".equals(pid))
                pidsToCorrect.add(pid.trim());
        }
        
        return pidsToCorrect;
    }
    
    static public void usage(String message)
    {
        System.out.print("***** " + message + " *****\n");
        System.out.print("Usage: ");
        System.out.println("java <pidFile> update|verify");
        
        System.out.println("  pidFile\tThe file containing the problem pids (one pid per line)");
        
        System.out.println("  -update\t\tSend the corresponing update requests to the Handle Service");
        System.out.println("  -verify\t\tVerify that the former update requests have been successful.");
    
        System.exit(-1);
    }
    
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length != 2)
            usage("Wrong number of parameters....");
        
        String pidFileName = args[0];
        String mode = args[1];       
        
        if (pidFileName == null || "".equals(pidFileName) || !new File(pidFileName).exists())
            usage("pidFileName may not be null or empty or the file does not exists.");
        if (mode == null || (!mode.contains("update") && !mode.contains("verify")))
            usage("Mode should be <update> or <verify>");
        
        String frameworkUrl = PropertyReader.getProperty("escidoc.framework_access.framework.url");
        
        PidCorrectionManager manager = new PidCorrectionManager();
        
        manager.init(frameworkUrl);
        
        List<String>pidsToCorrect = manager.getPidsToCorrect(new File(pidFileName));
        
        manager.correctList(pidsToCorrect);        
    }

    

    

    
}
