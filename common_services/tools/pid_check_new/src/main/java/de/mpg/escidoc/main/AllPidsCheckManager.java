package de.mpg.escidoc.main;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;

import de.mpg.escidoc.handler.LdhSrwSearchResponseHandler;
import de.mpg.escidoc.util.AllPidsCheckStatistic;
import de.mpg.escidoc.util.Statistic;

public class AllPidsCheckManager extends AbstractConsistencyCheckManager implements IConsistencyCheckManager{

	private AllPidsCheckStatistic statistic;

    private static String queryForPids = 
            "escidoc.objecttype=\"item\" AND escidoc.content-model.objid=\"escidoc:persistent4\"";
 
    public AllPidsCheckManager()
    {
    	super.init();
        statistic = new AllPidsCheckStatistic();
    }
    
	@Override
	public void createOrCorrectList(List<String> objects) throws Exception 
	{
		objects = this.searchForPids();
        
        statistic = new AllPidsCheckStatistic();
        statistic.setObjectsTotal(objects.size());
        
        File allLocators = new File("./allPids.txt");
       
        FileUtils.writeLines(allLocators, objects);      
		
	}

	@Override
	protected void doResolve(String object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Statistic getStatistic() 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	private List<String> searchForPids() throws Exception
	{
        List<String> pids = new ArrayList<String>();
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType(); 
        
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery(queryForPids);
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
        
        if (searchResult.getNumberOfRecords().intValue() > 0)
        {
        	File tmp = FileUtils.getFile(FileUtils.getTempDirectory(), "pids");
            
            try
            {
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                LdhSrwSearchResponseHandler ldhSearchresponseHandler = new LdhSrwSearchResponseHandler();
                
                for (int i = 0; i < searchResult.getNumberOfRecords().intValue(); i++)
                { 
                    ldhSearchresponseHandler = new LdhSrwSearchResponseHandler();
                    FileUtils.writeStringToFile(tmp, searchResult.getRecords().getRecord(i).getRecordData().get_any()[0].getAsString(), "UTF-8");
                    parser.parse(tmp, ldhSearchresponseHandler);
                    
                    pids.addAll(ldhSearchresponseHandler.getLocators());
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
            	FileUtils.deleteQuietly(tmp);
            	
            }
        }
       return pids;
	}

}
