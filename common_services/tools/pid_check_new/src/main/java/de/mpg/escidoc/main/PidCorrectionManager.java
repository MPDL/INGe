package de.mpg.escidoc.main;

import gov.loc.www.zing.srw.RecordsType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.io.File;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;

import de.mpg.escidoc.handler.SrwSearchResponseHandler;
import de.mpg.escidoc.pid.PidProvider;
import de.mpg.escidoc.util.HandleUpdateStatistic;
import de.mpg.escidoc.util.Statistic;

/**
 * 
 * This class corrects pids which are more than one time.
 *
 * @author sieders (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class PidCorrectionManager extends AbstractConsistencyCheckManager implements IConsistencyCheckManager
{

    private SrwSearchResponseHandler srwSearchResponseHandler;
    private HandleUpdateStatistic statistic;
    
    public PidCorrectionManager() throws Exception
    {
        super.init();
        statistic = new HandleUpdateStatistic();
    }
    
    public void createOrCorrectSet(Set<String> pids) throws Exception
    {
        PidProvider pidProvider = new PidProvider();
        
        try
        {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            
            for (String pid: pids)
            {    
                try
                {                    
                    RecordsType records = this.searchForPid(pid);
                    
                    // pid is not used in PubMan instance
                    if (records == null) 
                    {
                        pidProvider.updatePid(pid, "", statistic);
                        continue;       
                    }
                    
					for (int i = 0; i < records.getRecord().length; i++)
					{
						statistic.incrementTotal();

						File tmp = FileUtils.getFile(FileUtils.getTempDirectory(), "pid");
						
						String record = records.getRecord()[i].getRecordData().get_any()[0].getAsString();
								
						FileUtils.writeStringToFile(tmp, record, "UTF-8");
								
						srwSearchResponseHandler = new SrwSearchResponseHandler();
						srwSearchResponseHandler.setPidToSearchFor("hdl:" + pid);
						parser.parse(tmp, srwSearchResponseHandler);
						
						if (!isValid(pid))
						{
						    logger.warn("Invalid pid <" + pid + ">");
						    continue;
						}
						
						if (srwSearchResponseHandler.isObjectPid())
						    
						        pidProvider.updatePid(pid,
									srwSearchResponseHandler.getItemUrl(),
									statistic);
						else if (srwSearchResponseHandler.isVersionPid())
						{
							pidProvider.updatePid(pid,
									srwSearchResponseHandler.getVersionUrl(),
									statistic);
						} 
						else if (srwSearchResponseHandler.isComponentPid())
						{
							pidProvider.updatePid(pid,
									srwSearchResponseHandler.getComponentUrl(),
									statistic);
						}
						FileUtils.deleteQuietly(tmp);

						Thread.currentThread().sleep(1000);
					}
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
 
    private RecordsType searchForPid(String pid) throws Exception
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
        
        SearchRetrieveResponseType searchResult = searchHandler.searchRetrieveOperation(searchRetrieveRequest);
        if (searchResult.getDiagnostics() != null)
        {
             // something went wrong
            for (DiagnosticType diagnostic : searchResult.getDiagnostics().getDiagnostic())
            {
                    logger.info("diagnostic <" + diagnostic.getDetails() + ">");
            }
        }
        
		if (searchResult.getNumberOfRecords().intValue() > 0)
		{
			return searchResult.getRecords();
		} else
		{
			return null;
		}
    }
    
    private String getTaskParam(String lastModificationDate, String newPid)
    {
        StringBuffer b = new StringBuffer(1024);
        
        b.append("<param last-modification-date=\"");
        b.append(lastModificationDate);
        b.append("\">");
        b.append("<pid>somePid</pid>".replace("somePid", newPid));
        b.append("</param>");

        return b.toString();
    }

    @Override
    protected void doResolve(String object)
    {
        pidProvider.checkToResolvePid(object, statistic);
        
    }

    @Override
    protected Statistic getStatistic()
    {
        return this.statistic;
    }

    
}
