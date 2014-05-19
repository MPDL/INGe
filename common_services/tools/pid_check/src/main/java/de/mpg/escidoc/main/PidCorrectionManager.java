package de.mpg.escidoc.main;

import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import de.mpg.escidoc.handler.SrwSearchResponseHandler;
import de.mpg.escidoc.pid.PidProvider;
import de.mpg.escidoc.pid.PidProviderMock;
import de.mpg.escidoc.util.HandleUpdateStatistic;


public class PidCorrectionManager extends AbstractConsistencyCheckManager implements IConsistencyCheckManager
{

    public PidCorrectionManager()
    {
        super.init();
    }
    
    
    public List<String> getPidsToCorrect(File pids) throws Exception, URISyntaxException
    {
        List<String> pidsToCorrect = new ArrayList<String>();
        LineIterator lit = FileUtils.lineIterator(pids);
        
        while(lit.hasNext())
        {
            String pid = lit.next();
            if (pid != null && !"".equals(pid.trim()))
                pidsToCorrect.add(pid.trim());
        }
        
        return pidsToCorrect;
    }
    
    public void correctList(List<String> pids) throws Exception
    {
        PidProvider pidProvider = new PidProvider();
        
        statistic = new HandleUpdateStatistic();
        
        try
        {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            
            for (String pid: pids)
            {    
                try
                {
                    statistic.incrementHandlesTotal();
                    
                    RecordType record = this.searchForPid(pid);
                    
                    if (record == null) 
                    {
                        pidProvider.updatePid(pid, "", statistic);
                        continue;       
                    }
                    
                    File tmp = FileUtils.getFile(FileUtils.getTempDirectory(), "pid");
                    FileUtils.writeStringToFile(tmp, record.getRecordData().get_any()[0].getAsString(), "UTF-8");
                    
                    srwSearchResponseHandler = new SrwSearchResponseHandler();
                    srwSearchResponseHandler.setPidToSearchFor(pid);
                    parser.parse(tmp, srwSearchResponseHandler);
                    
                    if (srwSearchResponseHandler.isObjectPid())
                        pidProvider.updatePid(pid, srwSearchResponseHandler.getItemUrl(), statistic); 
                    else if (srwSearchResponseHandler.isVersionPid())
                    {
                        pidProvider.updatePid(pid, srwSearchResponseHandler.getVersionUrl(), statistic); 
                    }
                    else if (srwSearchResponseHandler.isComponentPid())
                    {
                        pidProvider.updatePid(pid, srwSearchResponseHandler.getComponentUrl(), statistic); 
                    } 
                    FileUtils.deleteQuietly(tmp);
                    
                    Thread.currentThread().sleep(5*1000);
                }
                catch (Exception e)
                {
                    statistic.incrementHandlesUpdateError();
                    pidProvider.getFailureMap().put(pid, e.toString());
                    continue;
                }
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

    
}
