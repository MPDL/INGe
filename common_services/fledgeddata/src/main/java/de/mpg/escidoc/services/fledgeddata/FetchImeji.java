package de.mpg.escidoc.services.fledgeddata;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.fledgeddata.oai.OAIUtil;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.CannotDisseminateFormatException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.IdDoesNotExistException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.NoItemsMatchException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.OAIInternalServerError;
import de.mpg.escidoc.services.fledgeddata.oai.valueobjects.oaiRecordFactory;




/**
 * 
 * @author kleinfe1
 *
 */
public class FetchImeji 
{

    private static SortedMap nativeMap = null;
    private HashMap resumptionResults=new HashMap();
    private static int maxListSize;
    private ArrayList sets = null;
    private boolean schemaLocationIndexed = false;
    private static oaiRecordFactory oaiRecordFactory;
	
	
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
		//TODO: set
		
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
        
        try
        {
    		URL url = new URL(fetchUrl);
            conn = url.openConnection();
            System.out.println("URL"+url.toExternalForm());
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 200:
                    System.out.println("Fetch xml from export");
                	
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
                    throw new OAIInternalServerError("An error occurred during metadata fetch from repository: "
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
			resultXml = util.craeteOaiHeader(resultXml);
		} 
        catch (Exception e)
        {
			throw new OAIInternalServerError(e.getMessage());
		} 

		return resultXml;
	}
	
	public String quickCreate(Object nativeItem, String schemaURL,
			String metadataPrefix) throws IllegalArgumentException,
			CannotDisseminateFormatException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public boolean isDeleted(Object nativeItem) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public Iterator getSetSpecs(Object nativeItem)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public String getOAIIdentifier(Object nativeItem) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public String getDatestamp(Object nativeItem) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Iterator getAbouts(Object nativeItem) {
		// TODO Auto-generated method stub
		return null;
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
            System.out.println("URL"+url.toExternalForm());
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 200:
                    System.out.println("Fetch xml from export");
                	
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
                    throw new OAIInternalServerError("An error occurred during metadata fetch from repository: "
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
        	System.out.println("Create native format record");
        	resultXml = util.craeteNativeOaiRecord(resultXml, identifier);
        }
        else 
        	if (metadataPrefix.equalsIgnoreCase("oai_dc"))
        	{        		
        		try
        		{
        			System.out.println("Create oai_dc record");
        			resultXml = util.xsltTransform(oaiXslt, resultXml);
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
	
	public static List listSets( Properties properties) throws OAIInternalServerError
	{
		//Properties
		String listSetsURL = properties.getProperty("Repository.oai.listSetsURL", "Property 'Repository.oai.listSetsURL' is undefined.");
		String[] listSetsURLArray = listSetsURL.split("#");
		
		//Variables
		Map <String, String>sets;
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
	                    throw new OAIInternalServerError("An error occurred during the construction of a ListSets request: "
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
	
	public static String listRecords(String metadataPrefix, Properties properties, String from, String until) 
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
        
        try
        {
    		URL url = new URL(fetchUrl);
            conn = url.openConnection();
            System.out.println("URL"+url.toExternalForm());
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 200:
                    System.out.println("Fetch xml from export");
                	
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
                    throw new OAIInternalServerError("An error occurred during metadata fetch from repository: "
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
        		System.out.println("Create native format record");
				resultXml = util.craeteNativeOaiRecords(resultXml);
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
        			System.out.println("Create oai_dc record");
        			resultXml = util.xsltTransform(oaiXslt, resultXml);
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