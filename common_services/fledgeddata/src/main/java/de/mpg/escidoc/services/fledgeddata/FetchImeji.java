package de.mpg.escidoc.services.fledgeddata;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.zip.ZipOutputStream;

import de.mpg.escidoc.services.fledgeddata.oai.exceptions.CannotDisseminateFormatException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.NoItemsMatchException;
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
     * Retrieve a list of Identifiers that satisfy the criteria parameters
     *
     * @param from beginning date in the form of YYYY-MM-DD or null if earliest
     * date is desired
     * @param until ending date in the form of YYYY-MM-DD or null if latest
     * date is desired
     * @param set set name or null if no set is desired
     * @return a Map object containing an optional "resumptionToken" key/value
     * pair and an "identifiers" Map object. The "identifiers" Map contains OAI
     * identifier keys with corresponding values of "true" or null depending on
     * whether the identifier is deleted or not.
     * @exception OAIBadRequestException signals an http status code 400
     *            problem
     */
    public static Map listIdentifiers(String from, String until, String set, String metadataPrefix)
        throws NoItemsMatchException 
    {
        //purge(); // clean out old resumptionTokens
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();
        Iterator iterator = nativeMap.entrySet().iterator();
        int numRows = nativeMap.entrySet().size();
        int count = 0;

        while (count < maxListSize && iterator.hasNext()) 
        {
        	Map.Entry entryNativeMap = (Map.Entry)iterator.next();
            HashMap nativeRecord = (HashMap)entryNativeMap.getValue();
            String recordDate = oaiRecordFactory.getDatestamp(nativeRecord);

            //TODO check metadataprefix muss eigentlich vorher schon passieren
            if (recordDate.compareTo(from) >= 0
                && recordDate.compareTo(until) <= 0) 
            {
            	System.out.println("--- Create header for item: " + nativeRecord.toString());
                String[] header = oaiRecordFactory.createHeader(nativeRecord);
                headers.add(header[0]);
                identifiers.add(header[1]);
                count++;
            }
        }

        if (count == 0)
        {
            throw new NoItemsMatchException();
        }

//        if (iterator.hasNext()) 
//        {        	
//	 	    listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString()));
//        }
        
        listIdentifiersMap.put("headers", headers.iterator());
        listIdentifiersMap.put("identifiers", identifiers.iterator());
        return listIdentifiersMap;
    }
    
    public static String getRecord (String identifier, String metadataPrefix, Properties properties) 
    {
    	String record = "";
    	String extension = "&q=( ID_URI.URI=\""+identifier+"\" )";
    	try 
    	{
    		return getExport(extension, properties);
		} 
    	catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return record;
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
	
	private static String getExport(String extension, Properties properties) throws MalformedURLException
	{
		String resultXml = "";
		String exportUrl = "export?format=rdf"; //Evtl. aus properties lesen
		String baseUrl = properties.getProperty("Repository.baseURL");
		URL url = new URL(baseUrl + "/" + exportUrl + extension); 
		System.out.println("fetch imeji: " + url.toExternalForm());
		
		byte[] input = null;
        URLConnection conn = null;
        Date retryAfter = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        try
        {
            conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 200:
                    System.out.println("Fetch xml from export");
                	resultXml = (String) httpConn.getContent();
                	System.out.println("ResultXml: " + resultXml);
                    httpConn.disconnect();                  
                    break;
                default:
                    throw new RuntimeException("An error occurred during importing from external system: "
                            + responseCode + ": " + httpConn.getResponseMessage() + ".");
            }
        }
        catch (Exception e)
        {
        	//TODO
            throw new RuntimeException(e);
        }

        System.out.println("Create map from xml");
        //TODO
        
		
		return resultXml;
	}

}