package de.mpg.escidoc.main;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;

import de.mpg.escidoc.handler.LdhSrwSearchResponseHandler;
import de.mpg.escidoc.util.LocatorCheckStatistic;
import de.mpg.escidoc.util.Statistic;

public class LdhCheckManager extends AbstractConsistencyCheckManager implements IConsistencyCheckManager
{
    private LocatorCheckStatistic statistic;

    private static String queryForLocators = 
            "escidoc.objecttype=\"item\" AND escidoc.content-model.objid=\"escidoc:persistent4\" AND ((escidoc.context.objid=\"escidoc:37005\") AND (escidoc.component.content.storage=\"external-url\") )";
 
    // query for dev
    /*private static String queryForLocators = 
            "escidoc.objecttype=\"item\" AND escidoc.content-model.objid=\"escidoc:2001\" AND ((escidoc.context.objid=\"escidoc:171002\") AND (escidoc.component.content.storage=\"external-url\") )";
*/
    
    public LdhCheckManager() throws Exception
    {
        super.init();
        statistic = new LocatorCheckStatistic();
    }

    @Override
    /**
     * Creates a Set of all used Locators in the LDH context
     */
    public void createOrCorrectSet(Set<String> objects) throws Exception
    {
        objects = this.searchForLocators();
        
        statistic = new LocatorCheckStatistic();
        statistic.setLocatorsTotal(objects.size());
        
        File allLocators = new File("./allLocators.txt");
       
        FileUtils.writeLines(allLocators, objects);      
    }

    @Override
    protected void doResolve(String object)
    {
        pidProvider.checkToResolveLocator(object, (LocatorCheckStatistic)getStatistic());
    }
    
    @Override
    protected Statistic getStatistic()
    {
        return this.statistic;
    }
    
    private Set<String> searchForLocators() throws Exception
    {
        Set<String> locators = new HashSet<String>();
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType(); 
        
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery(queryForLocators);
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
            try
            {
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                LdhSrwSearchResponseHandler ldhSearchresponseHandler = new LdhSrwSearchResponseHandler();
                File tmp = FileUtils.getFile(FileUtils.getTempDirectory(), "srw");
                
                for (int i = 0; i < searchResult.getNumberOfRecords().intValue(); i++)
                { 
                    ldhSearchresponseHandler = new LdhSrwSearchResponseHandler();
                    FileUtils.writeStringToFile(tmp, searchResult.getRecords().getRecord(i).getRecordData().get_any()[0].getAsString(), "UTF-8");
                    parser.parse(tmp, ldhSearchresponseHandler);
                    
                    locators.addAll(ldhSearchresponseHandler.getLocators());
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
       return locators;
    }

}
