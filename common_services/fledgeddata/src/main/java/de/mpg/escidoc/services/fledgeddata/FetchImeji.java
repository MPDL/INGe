package de.mpg.escidoc.services.fledgeddata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.mpg.escidoc.services.fledgeddata.oai.OAIUtil;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.CannotDisseminateFormatException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.IdDoesNotExistException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.NoItemsMatchException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.OAIInternalServerError;




/**
 * 
 * @author kleinfe1
 *
 */
public class FetchImeji 
{
	private static final Logger LOGGER = Logger.getLogger(FetchImeji.class);
	
	
	/**
	 * 
	 * @param metadataPrefix
	 * @param properties
	 * @param from
	 * @param until
	 * @return
	 * @throws OAIInternalServerError
	 * @throws CannotDisseminateFormatException
	 * @throws NoItemsMatchException
	 */
	public static String listIdentifiers(String from, String until, String set, Properties properties) 
			throws OAIInternalServerError,
				   CannotDisseminateFormatException, 
				   NoItemsMatchException
	{
		//Properties
		String fetchUrl = properties.getProperty("Repository.oai.listRecordsURL", "Property 'Repository.oai.listRecordsURL' is undefined.");
		
		//Variables
		String resultXml = "";
		InputStreamReader isReader;
		BufferedReader bReader;
        URLConnection conn = null;
        OAIUtil util = new OAIUtil();    
        
        if (set != null)
        {
            //Small hack to enable collection and album fetching from imeji
            if (set.contains("collection"))
            {
            	fetchUrl=fetchUrl.replace("image", "collection");
            }
            if (set.contains("album"))
            {
            	fetchUrl=fetchUrl.replace("image", "album");
            }
        }
        
        try
        {
    		URL url = new URL(fetchUrl);
            conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 200:
                	
                    LOGGER.debug("[FDS] Fetch xml from " + url.toExternalForm());                	
                    // Get XML
                    isReader = new InputStreamReader(httpConn.getInputStream(),"UTF-8");
                    bReader = new BufferedReader(isReader);
                    String line = "";
                    while ((line = bReader.readLine()) != null)
                    {
                    	resultXml += line + "\n";
                    }
                    httpConn.disconnect();  
                    break;
                default:
                    throw new OAIInternalServerError("[FDS] An error occurred during metadata fetch from repository: "
                            + responseCode + ": " + httpConn.getResponseMessage() + ".");
            }

        }
        catch (Exception e)
        {
            throw new OAIInternalServerError(e.getMessage());
        }

        //check if result is empty
        if (!resultXml.contains("rdf:about"))
        {
        	throw new NoItemsMatchException();
        }

        try 
        {
        	if (set != null)
        	{
        		resultXml = util.createOaiHeaderFromSet(resultXml, set);
        	}
        	else
        	{
        		resultXml = util.createOaiHeader(resultXml);
        	}
		} 
        catch (Exception e)
        {
			throw new OAIInternalServerError(e.getMessage());
		} 

		return resultXml;
	}
	
	
	public boolean isDeleted(Object nativeItem) {
		// TODO
		return false;
	}
	
	public static String getRecord(String identifier, String metadataPrefix, Properties properties) 
			throws OAIInternalServerError,
				   IdDoesNotExistException,
				   CannotDisseminateFormatException
	{
		//Properties
		String nativeFormat = properties.getProperty("Repository.nativeFormat.Name", "Property 'Repository.nativeFormat.Name' is undefined.");
		String oaiXslt = properties.getProperty("Repository.oai.stylesheet", "Property 'Repository.oai.stylesheet' is undefined.");
		String fetchUrl = properties.getProperty("Repository.oai.fetchURL", "Property 'Repository.oai.fetchURL' is undefined.");
		
		//Variables
		String resultXml = "";
		InputStreamReader isReader;
		BufferedReader bReader;
        URLConnection conn = null;
        OAIUtil util = new OAIUtil();
        
        //Small hack to enable collection and album fetching from imeji
        if (identifier.contains("collection"))
        {
        	fetchUrl=fetchUrl.replace("image", "collection");
        }
        if (identifier.contains("album"))
        {
        	fetchUrl=fetchUrl.replace("image", "album");
        }

        
        try
        {
    		URL url = new URL(util.constructFetchUrl(fetchUrl, identifier));
            conn = url.openConnection();
            LOGGER.debug("[FDS] Fetch xml from " + url.toExternalForm()); 
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 200:
                    // Get XML
                    isReader = new InputStreamReader(httpConn.getInputStream(),"UTF-8");
                    bReader = new BufferedReader(isReader);
                    String line = "";
                    while ((line = bReader.readLine()) != null)
                    {
                    	resultXml += line + "\n";
                    }
                    httpConn.disconnect();  
                    System.out.println("ResultXml: " + resultXml);
                    break;
                default:
                    throw new OAIInternalServerError("[FDS] An error occurred during metadata fetch from repository: "
                            + responseCode + ": " + httpConn.getResponseMessage() + ".");
            }

        }
        catch (Exception e)
        {
            throw new OAIInternalServerError(e.getMessage());
        }

        //check if result is empty
        if (!resultXml.contains("rdf:about"))
        {
        	throw new IdDoesNotExistException(identifier);
        }
        
        if (metadataPrefix.equalsIgnoreCase(nativeFormat))
        {
        	LOGGER.debug("[FDS] Create native format record");
        	resultXml = util.createNativeOaiRecord(resultXml, identifier);
        }
        else 
        	if (metadataPrefix.equalsIgnoreCase("oai_dc"))
        	{        		
        		try
        		{
        			LOGGER.debug("[FDS] Create oai_dc record");
        			String type = "";
        			if (identifier.contains("image"))
        			{
        				type = "image";
        			}
        			if (identifier.contains("album"))
        			{
        				type = "album";
        			}
        			if (identifier.contains("collection"))
        			{
        				type = "collection";
        			}
        			resultXml = util.xsltTransform(oaiXslt, resultXml, type);       			
        		}
        		catch (Exception e) 
        		{
					throw new OAIInternalServerError("[FDS] An error occurred during transformation to oai_dc format. " + e.getMessage());
				}
        	}
        	else
        	{
        		throw new CannotDisseminateFormatException(metadataPrefix);
        	}
        
		
		return resultXml;
	}
	
	public static List listSets( Properties properties) throws OAIInternalServerError
	{
		//Properties
		String listSetsURL = properties.getProperty("Repository.oai.listSetsURL", "Property 'Repository.oai.listSetsURL' is undefined.");
		String[] listSetsURLArray = listSetsURL.split("#");
		
		//Variables
		List setList = new ArrayList();
		String resultXml = "";
		InputStreamReader isReader;
		BufferedReader bReader;
        URLConnection conn = null;
        String type ="";

        try
        {
        	for (int i=0; i<listSetsURLArray.length; i++)
        	{
        		if (listSetsURLArray[i].trim().contains("collection")){type="collection";}
        		if (listSetsURLArray[i].trim().contains("album")){type="album";}
        		
	    		URL url = new URL(listSetsURLArray[i].trim());
	            conn = url.openConnection();
	            HttpURLConnection httpConn = (HttpURLConnection) conn;
	            int responseCode = httpConn.getResponseCode();
	            resultXml = "";
	            
	            switch (responseCode)
	            {
	                case 200:
	                    // Get XML
	                    isReader = new InputStreamReader(httpConn.getInputStream(),"UTF-8");
	                    bReader = new BufferedReader(isReader);
	                    String line = "";
	                    while ((line = bReader.readLine()) != null)
	                    {
	                    	resultXml += line + "\n";
	                    }	  
	                    httpConn.disconnect();  
	                    break;
	                default:
	                    throw new OAIInternalServerError("[FDS] An error occurred during the construction of a ListSets request: "
	                            + responseCode + ": " + httpConn.getResponseMessage() + ".");
	            }
	            
	            DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
	            DocumentBuilder bd = docFact.newDocumentBuilder();
	            InputSource is = new InputSource();
	            is.setCharacterStream(new StringReader(resultXml.toLowerCase().trim()));
	            Document doc = bd.parse(is);
	            
	            //Create sets map
	            setList = Util.createSets(doc, setList, type);
        	}
        }
        catch (Exception e)
        {
            throw new OAIInternalServerError(e.getMessage());
        }

		return setList;
	}
	
	public static String listRecords(String metadataPrefix, Properties properties, String from, String until, String set) 
			throws OAIInternalServerError,
				   CannotDisseminateFormatException, 
				   NoItemsMatchException
	{
		//Properties
		String nativeFormat = properties.getProperty("Repository.nativeFormat.Name", "Property 'Repository.nativeFormat.Name' is undefined.");
		String oaiXslt = properties.getProperty("Repository.oai.stylesheet", "Property 'Repository.oai.stylesheet' is undefined.");
		String fetchUrl = properties.getProperty("Repository.oai.listRecordsURL", "Property 'Repository.oai.listRecordsURL' is undefined.");
		
		//Variables
		String resultXml = "";
		InputStreamReader isReader;
		BufferedReader bReader;
        URLConnection conn = null;
        OAIUtil util = new OAIUtil();        
        
        if (set != null)
        {
            //Small hack to enable collection and album fetching from imeji
            if (set.contains("collection"))
            {
            	fetchUrl=fetchUrl.replace("image", "collection");
            }
            if (set.contains("album"))
            {
            	fetchUrl=fetchUrl.replace("image", "album");
            }
            fetchUrl += "&q=%20(ID_URI.URI=\""+set+"\"%20)";
        }
        
        try
        {
    		URL url = new URL(fetchUrl);
            conn = url.openConnection();
            LOGGER.debug("[FDS] Fetch xml from " + url.toExternalForm()); 
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 200:
                    // Get XML
                    isReader = new InputStreamReader(httpConn.getInputStream(),"UTF-8");
                    bReader = new BufferedReader(isReader);
                    String line = "";
                    while ((line = bReader.readLine()) != null)
                    {
                    	resultXml += line + "\n";
                    }
                    httpConn.disconnect();  
                    break;
                default:
                    throw new OAIInternalServerError("[FDS] An error occurred during metadata fetch from repository: "
                            + responseCode + ": " + httpConn.getResponseMessage() + ".");
            }

        }
        catch (Exception e)
        {
            throw new OAIInternalServerError(e.getMessage());
        }

        //check if result is empty
        if (!resultXml.contains("rdf:about"))
        {
        	throw new NoItemsMatchException();
        }
        
        if (metadataPrefix.equalsIgnoreCase(nativeFormat))
        {       	
        	try 
        	{
        		LOGGER.debug("[FDS] Create native format record");
        		if (set != null)
        		{
        			resultXml = util.createNativeOaiRecordsFromSet(resultXml, set, properties);
        		}
        		else
        		{
        			resultXml = util.createNativeOaiRecords(resultXml);
        		}
			} 
        	catch (Exception e)
        	{
				throw new OAIInternalServerError(e.getMessage());
			} 
        }
        else 
        	if (metadataPrefix.equalsIgnoreCase("oai_dc"))
        	{        		
        		try
        		{
        			LOGGER.debug("[FDS] Create oai_dc record");
        			resultXml = util.xsltTransform(oaiXslt, resultXml, "image"); 
        		}
        		catch (Exception e) 
        		{
					throw new OAIInternalServerError("An error occurred during transformation to oai_dc format. " + e.getMessage());
				}
        	}
        	else
        	{
        		throw new CannotDisseminateFormatException(metadataPrefix);
        	}
		return resultXml;
	}

}