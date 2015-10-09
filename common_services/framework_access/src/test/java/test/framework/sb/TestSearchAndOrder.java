package test.framework.sb;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.StringOrXmlFragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import test.framework.TestBase;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class TestSearchAndOrder extends TestBase
{
    private Logger logger = Logger.getLogger(TestSearchAndOrder.class);

    @Test
    public void sortByType() throws Exception
    {
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery("escidoc.objecttype=\"item\" and escidoc.context.objid=\""
                + PropertyReader.getProperty(PROPERTY_TEST_CONTEXT_ID) + "\"");
        searchRetrieveRequest.setSortKeys("sort.escidoc.publication.type sort.escidoc.publication.degree");
        searchRetrieveRequest.setMaximumRecords(new NonNegativeInteger("100"));

        searchRetrieveRequest.setRecordPacking("xml");
        
        SearchRetrieveResponseType searchResult = null;
        List<String> types = null;

        try
        {
            logger.info("Cql search string: <" + searchRetrieveRequest.getQuery() + ">");
            logger.info("Cql sorting key(s): <" + searchRetrieveRequest.getSortKeys() + ">");
            searchResult = ServiceLocator.getSearchHandler("escidoc_all").searchRetrieveOperation(searchRetrieveRequest);
            
            assertTrue(searchResult != null);
            logger.info("Search result: " + searchResult.getNumberOfRecords() + " item(s) or container(s)");
           
            types = getTypes(searchResult);
            assertTrue(types != null);
            logger.info(types);
            
//            assertTrue(isAscending(types));
        } 
        catch (Exception e)
        {
            fail("Exception caught");
        }
        
        logger.info("############################################################################################");
        
        for (String type : types)
        {
            logger.info(type);
        }
        logger.info("############################################################################################");
    }
    
    @Test
    @Ignore
    public void sortByTitle()
    {
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery("escidoc.objecttype=\"item\" and escidoc.context.objid=\"escidoc:1002\"");
        searchRetrieveRequest.setSortKeys("sort.escidoc.publication.title");
        searchRetrieveRequest.setMaximumRecords(new NonNegativeInteger("100"));

        searchRetrieveRequest.setRecordPacking("xml");
        
        SearchRetrieveResponseType searchResult = null;
        List<String> titles = null;

        try
        {
            logger.info("Cql search string: <" + searchRetrieveRequest.getQuery() + ">");
            logger.info("Cql sorting key(s): <" + searchRetrieveRequest.getSortKeys() + ">");
            searchResult = ServiceLocator.getSearchHandler("escidoc_all").searchRetrieveOperation(searchRetrieveRequest);
            logger.info("Search result: " + searchResult.getNumberOfRecords() + " item(s) or container(s)");
            
            titles = getTitles(searchResult);
            
            logger.info(titles);
            
            assertTrue(isAscending(titles));
        } 
        catch (Exception e)
        {
            fail("Exception caught");
        }
        
        logger.info("############################################################################################");
        
        for (String title : titles)
        {
            logger.info(title);
        }
        logger.info("############################################################################################");
    }
    
   
    private List<String> getTypes(
            SearchRetrieveResponseType searchResult) throws Exception
    {

        ArrayList<String> resultTypes = new ArrayList<String>();
        if (searchResult.getRecords() != null)
        {
            for (RecordType record : searchResult.getRecords().getRecord())
            {
                StringOrXmlFragment data = record.getRecordData();
                MessageElement[] messages = data.get_any();
                // Data is in the first record
                if (messages.length == 1)
                {
                    String searchResultItem = null;
                    try
                    {
                        searchResultItem = messages[0].getAsString();
                    }
                    catch (Exception e) 
                    {
                        throw new Exception("Error getting search result message.", e);
                    }
                    logger.info("Search result: " + searchResultItem);
                    
                    int idx1 = searchResultItem.indexOf("publication:publication type");
                    int idx2 = searchResultItem.substring(idx1+1).indexOf("xmlns:publication") + idx1;
                    
                    resultTypes.add(searchResultItem.substring(idx1-1, idx2+1));
                    
                }
            }
        }
        return resultTypes;
    }
    
    private List<String> getTitles(SearchRetrieveResponseType searchResult) throws Exception
    {
        ArrayList<String> resultTitles = new ArrayList<String>();
        if (searchResult.getRecords() != null)
        {
            for (RecordType record : searchResult.getRecords().getRecord())
            {
                StringOrXmlFragment data = record.getRecordData();
                MessageElement[] messages = data.get_any();
                // Data is in the first record
                if (messages.length == 1)
                {
                    String searchResultItem = null;
                    try
                    {
                        searchResultItem = messages[0].getAsString();
                    }
                    catch (Exception e) 
                    {
                        throw new Exception("Error getting search result message.", e);
                    }
                    logger.info("Search result: " + searchResultItem);
                    
                    int idx1 = searchResultItem.indexOf("</eterms:creator>");
                    int idx2 = searchResultItem.substring(idx1).indexOf("dc:title") + idx1;
                    int idx3 = searchResultItem.substring(idx2).indexOf("/dc:title>") + idx2;
                    
                    resultTitles.add(searchResultItem.substring(idx2-1, idx3+1));
                    
                }
            }
        }
        return resultTitles;
    }
    
    private boolean isAscending(List<String> results)
    {
        return doCompare(results, new Character('<'));
    }
    
    private boolean isDescending(List<String> results)
    {
        return doCompare(results, new Character('>'));
    }

    private boolean doCompare(List<String> results, Character c)
    {
        String last = null;
        LexComparator lexComp = new LexComparator();
        
        for (String s : results)
        {           
            if (last != null && s != null)
            {
                if (!lexComp.doCompare(last, s, c))
                {
                    logger.info("Failure when comparing " + s + " " + last);
                    return false;
                }
            }
            last = s;
        }
        return true;
    }
    
    class LexComparator
    {
        public boolean doCompare(String s1, String s2, Character c)
        {
            if (c.equals('<'))
            {
                logger.info(s1.compareTo(s2));
                return (s1.compareTo(s2) >= 0);
            }
            else if (c.equals('>'))
            {
                logger.info(s1.compareTo(s2));
                return (s1.compareTo(s2) <= 0);
            }
            return false;
        }
        
    }

}
