package de.mpg.escidoc.main;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;

import de.mpg.escidoc.handler.SrwSearchResponseHandler;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.util.HandleUpdateStatistic;
import de.mpg.escidoc.util.Statistic;

public class MissingPidsCorrectManager extends AbstractConsistencyCheckManager implements IConsistencyCheckManager
{
    private HandleUpdateStatistic statistic;

    private static String searchForItemsWithFileModifiedSince = " \"/properties/content-model/id\"=\"escidoc:persistent4\" "
            + " AND (( ( ( \"/properties/creation-date/date\">=\"2014-08-13\" ) ) ) OR ( ( ( \"/last-modification-date/date\">=\"2014-08-13\" ) ) ) "
            + " AND (( ( (\"/properties/version/status\"=\"in-revision\") NOT (\"/properties/public-status\"=\"withdrawn\") ) OR ( (\"/properties/version/status\"=\"released\") NOT (\"/properties/public-status\"=\"withdrawn\") ) ) ) AND (\"/components/component/content/storage\"=\"internal-managed\") )";
    
    
    public MissingPidsCorrectManager() throws Exception
    {
        super.init("item_container_admin");
        statistic = new HandleUpdateStatistic();
    }
    
    @Override
    public void createOrCorrectSet(Set<String> objects) throws Exception 
    {
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType(); 
        
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery(searchForItemsWithFileModifiedSince);
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
        
        logger.info(" query found <" + searchResult.getNumberOfRecords().intValue() + "> objects");
        if (searchResult.getNumberOfRecords().intValue() > 0)
        {
            try
            {
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                SrwSearchResponseHandler srwSearchResponseHandler = new SrwSearchResponseHandler();
                File tmp = FileUtils.getFile(FileUtils.getTempDirectory(), "srw");
                if (new File("./allPids.txt").exists())
                {
                    FileUtils.deleteQuietly(new File("./allPids.txt"));
                }
                for (int i = 0; i < searchResult.getNumberOfRecords().intValue(); i++)
                { 
                    srwSearchResponseHandler = new SrwSearchResponseHandler();
                    FileUtils.writeStringToFile(tmp, searchResult.getRecords().getRecord(i).getRecordData().get_any()[0].getAsString(), "UTF-8");
                    parser.parse(tmp, srwSearchResponseHandler);
                    
                    FileUtils.writeStringToFile(new File("./allPids.txt"), 
                            srwSearchResponseHandler.getEscidocId() + " " + srwSearchResponseHandler.getComponentsWithMissingPid() + "\n", true);
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        if (searchResult.getNumberOfRecords().intValue() > 0)
        {
            try
            {
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                SrwSearchResponseHandler srwSearchResponseHandler = new SrwSearchResponseHandler();
                File tmp = FileUtils.getFile(FileUtils.getTempDirectory(), "srw");
                
                for (int i = 0; i < searchResult.getNumberOfRecords().intValue(); i++)
                { 
                    srwSearchResponseHandler = new SrwSearchResponseHandler();
                    FileUtils.writeStringToFile(tmp, searchResult.getRecords().getRecord(i).getRecordData().get_any()[0].getAsString(), "UTF-8");
                    parser.parse(tmp, srwSearchResponseHandler);
                    
                    if (srwSearchResponseHandler.getComponentsWithMissingPid().size() > 0)
                    {
                        doCorrect(srwSearchResponseHandler.getEscidocId(), 
                                srwSearchResponseHandler.getComponentsWithMissingPid(), srwSearchResponseHandler.getLastModificationDate());
                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        pidProvider.storeResults(getStatistic());
    }

    @Override
    protected void doResolve(String pid) throws Exception
    {
    }

    @Override
    protected Statistic getStatistic() 
    {
        return statistic;
    }

    private void doCorrect(String escidocId, Set<String> componentsWithMissingPid, String lastModificationDate)
    {
        logger.info("doCorrect escidocId <" + escidocId 
                + "> componentsWithMissingPid <" + componentsWithMissingPid + "> lastModificationDate <" + lastModificationDate +">");
       
        for (String url : componentsWithMissingPid)
        {
            try
            {
                logger.info("assignContentPid escidocId <" + escidocId + " getComponentId <" + getComponentId(url)
                        + ">" + " getParamXml <" + getParamXml(lastModificationDate, url) + ">");
                itemHandler.assignContentPid(escidocId, getComponentId(url), getParamXml(lastModificationDate, url));
                statistic.incrementHandlesCreated();
                pidProvider.getSuccessMap().put(escidocId, getParamXml(lastModificationDate, url));
            }
            catch (Exception e)
            {
                logger.warn("Component PID assignment for " + escidocId + " failed. ", e);
                statistic.incrementHandlesUpdateError();
                pidProvider.getFailureMap().put(escidocId, getParamXml(lastModificationDate, url));
            }
        }
    }

    private String getParamXml(String lastModificationDate, String url)
    {
        StringBuffer paramXml = new StringBuffer(2048);
        
        paramXml.append("<param last-modification-date=\"");
        paramXml.append(lastModificationDate);
        paramXml.append("\"> ");
        paramXml.append("<url>");
        try
        {
            paramXml.append(PropertyReader.getProperty("escidoc.pubman.instance.url"));
            paramXml.append(PropertyReader.getProperty("escidoc.pubman.instance.context.path"));
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (URISyntaxException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        paramXml.append(url);
        paramXml.append("</url></param>");
        return paramXml.toString();
    }

    private String getComponentId(String url) throws Exception
    {
        if(url == null || "".equals(url))
        {
            throw new Exception("Invalid url");
            
        }
        
        int idx1 = url.indexOf("component") + "component".length() + 1;
        int idx2 = url.lastIndexOf("/");
        
        if (idx1 > idx2)
        {
            throw new Exception("Invalid url " + url);  
        }
        
        return url.substring(idx1, idx2);
    }
}
