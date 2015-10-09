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

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.io.FileUtils;

import de.mpg.escidoc.handler.AllPidsSrwSearchResponseHandler;
import de.mpg.escidoc.handler.SrwSearchResponseHandler;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.util.HandleUpdateStatistic;
import de.mpg.escidoc.util.Statistic;

public class MissingPidsCorrectManager extends AbstractConsistencyCheckManager implements IConsistencyCheckManager
{
    private HandleUpdateStatistic statistic;
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    // live
    private static String searchForItemsWithFileModifiedSince = " \"/properties/content-model/id\"=\"XXXXX\" "
            + " AND (( ( ( \"/properties/creation-date/date\">=\"2014-08-13\" ) ) ) OR ( ( ( \"/last-modification-date/date\">=\"2014-08-13\" ) ) ) )"
            + " AND ( \"/properties/public-status\"=\"released\" AND ( \"/properties/version/status\"=\"in-revision\"  OR  \"/properties/version/status\"=\"released\"  OR  \"/properties/version/status\"=\"submitted\"  OR  \"/properties/version/status\"=\"pending\") ) " 
            + " AND (\"/components/component/content/storage\"=\"internal-managed\")";
   
    // qa teschner
   /*
    private static String searchForItemsWithFileModifiedSince = " \"/properties/content-model/id\"=\"XXXXX\" "
            + " AND (( ( ( \"/properties/creation-date/date\"=\"2011-02-09\" ) ) ) OR ( ( ( \"/last-modification-date/date\"=\"2011-04-26\" ) ) ) )"
            + " AND ( \"/properties/public-status\"=\"released\" AND ( \"/properties/version/status\"=\"in-revision\"  OR  \"/properties/version/status\"=\"released\"  OR  \"/properties/version/status\"=\"submitted\"  OR  \"/properties/version/status\"=\"pending\") ) " 
            + " AND (\"/components/component/content/storage\"=\"internal-managed\")";
    */
    
    
    public MissingPidsCorrectManager() throws Exception
    {
        searchForItemsWithFileModifiedSince = searchForItemsWithFileModifiedSince.replace("XXXXX", 
                PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
        super.init("item_container_admin");
        statistic = new HandleUpdateStatistic();
    }
    
    @Override
    public void createOrCorrectSet(Set<String> objects) throws Exception 
    {
    	long start = System.currentTimeMillis();
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType(); 
        
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery(searchForItemsWithFileModifiedSince);
        searchRetrieveRequest.setRecordPacking("xml");
        searchRetrieveRequest.setMaximumRecords(new NonNegativeInteger("10000"));
        
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
        
        logger.info(" query found <" + searchResult.getNumberOfRecords().intValue() + "> objects");
        saveSearchResult(searchResult);
        
        statistic.setObjectsTotal(searchResult.getNumberOfRecords().intValue());
        
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
                    else
                    {
                        logger.info("no component pids missing for <" + srwSearchResponseHandler.getEscidocId() + ">");
                    }
                        
                    /*if (i >= 10)
                        break;*/
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        pidProvider.storeResults(statistic);
        
        logger.info("Total time used for update <" + (end - start) + ">");
    }

    @Override
    protected void doResolve(String escidocId) throws Exception
    {
        String components = itemHandler.retrieveComponents(escidocId);
        File tmp = FileUtils.getFile(FileUtils.getTempDirectory(), "components");
        FileUtils.writeStringToFile(tmp, components, "UTF-8");
              
        AllPidsSrwSearchResponseHandler handler = new AllPidsSrwSearchResponseHandler();       
        parser.parse(tmp, handler);
        
        if (handler.getNumberOfPidsMissing() > 0)
            pidProvider.getFailureMap().put(escidocId, "number of component pids missing <" + handler.getNumberOfPidsMissing() + ">");
        
        for (String pid : handler.getPids())
        {
            pidProvider.resolvePid(pid.substring(pid.indexOf("hdl:")), statistic);
            Thread.currentThread().sleep(1000);
        }
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
        
        int numberOfPidsMissing = componentsWithMissingPid.size();
        String modifiedItemXml = "";
       
        for (String url : componentsWithMissingPid)
        {
        	if (numberOfPidsMissing > 1)
        	{
         		logger.info("item <" + escidocId + "> has <" + componentsWithMissingPid + " > missing component pids");
        	}
            try
            {
                String componentXml = itemHandler.retrieveComponent(escidocId, getComponentId(url));
                
                // may be component has been modified in the mean time or the tool runs for the second time
                if (hasComponentPid(componentXml))
                {
                    logger.info("already has component pid <" + url + ">"); 
                    numberOfPidsMissing--;
                    continue;
                }
                
                logger.info("assignContentPid escidocId <" + escidocId + " getComponentId <" + getComponentId(url)
                        + ">" + " getParamXml <" + getParamXml(lastModificationDate, url) + ">");
                itemHandler.assignContentPid(escidocId, getComponentId(url), getParamXml(lastModificationDate, url));
                statistic.incrementHandlesCreated();
                pidProvider.getSuccessMap().put(escidocId, getParamXml(lastModificationDate, url));
                
                numberOfPidsMissing--;
                
                if (numberOfPidsMissing > 0)
                {
                	modifiedItemXml = itemHandler.retrieve(escidocId);
                	lastModificationDate = getLastModificationDate(modifiedItemXml);
                	logger.info("lastModificationDate after update <" + lastModificationDate + ">");
                }
            }
            catch (Exception e)
            {
                logger.warn("Component PID assignment for item <" + escidocId + "> failed. ", e);
                numberOfPidsMissing--;
                statistic.incrementHandlesUpdateError();
                pidProvider.getFailureMap().put(escidocId, getParamXml(lastModificationDate, url) + e.getClass().getSimpleName());
            }
        }
    }

	public static String getLastModificationDate(String itemXml)
	{
		String result = "";
		int index = itemXml.indexOf("last-modification-date");
		if (index > 0)
		{
			itemXml = itemXml.trim().substring(index + "last-modification-date".length() + 2);
			index = itemXml.indexOf('\"');
			if (index > 0)
			{
				result = itemXml.substring(0, index);
			}
		}
		return result;
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
    
    private boolean hasComponentPid(String componentXml) throws Exception
    {
        File tmp = FileUtils.getFile(FileUtils.getTempDirectory(), "components");
        FileUtils.writeStringToFile(tmp, componentXml, "UTF-8");
              
        AllPidsSrwSearchResponseHandler handler = new AllPidsSrwSearchResponseHandler();       
        parser.parse(tmp, handler);
        
        return (handler.getNumberOfPidsMissing() == 0 ? true : false);
    }
    
    private void saveSearchResult(SearchRetrieveResponseType searchResult)
    {
        if (new File("./allPids.txt").exists())
        {
            FileUtils.deleteQuietly(new File("./allPids.txt"));
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
                    
                    FileUtils.writeStringToFile(new File("./allPids.txt"), 
                            srwSearchResponseHandler.getEscidocId() + "\n", true);
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
