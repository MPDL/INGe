/*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.importmanager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import de.mpg.escidoc.services.common.MetadataHandler;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.logging.LogMethodDurationInterceptor;
import de.mpg.escidoc.services.common.logging.LogStartEndInterceptor;
import de.mpg.escidoc.services.common.metadata.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.common.util.ResourceUtil;



/**
 * This class provides the ejb implementation of the {@link ImportHandler} interface.
 *
 * @author Friederike Kleinfercher (initial creation)
 */ 

@Remote
@RemoteBinding(jndiBinding = ImportHandler.SERVICE_NAME)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors( { LogStartEndInterceptor.class, LogMethodDurationInterceptor.class })
public class ImportHandlerBean implements ImportHandler {
	
	private final static Logger logger = Logger.getLogger(ImportHandlerBean.class);
	private final String DATA_RETURN_FILETYPE = ".zip";
	private final String DATA_RETURN_MIMETYPE = "application/zip";

	private ImportSourceHandlerBean sourceHandler = new ImportSourceHandlerBean();	
	private int contentLength =0;

	public ImportHandlerBean(){		
	}
	
    /**
     * This method provides XML formated output of the supported import sources
     * @return xml presentation of all available import sources
     */
    public String explainSources ()
    {
    	String explainXML = "";
    	try{
    		explainXML = ResourceUtil.getResourceAsString("resources/import.xml");
    	}
    	catch(IOException e){ e.printStackTrace();}
    	return explainXML;
    }	
    
    /**
     * This operation checks the sources properties for the harvesting protocol and forwards the fetching request.
	 * The parameter format-to is optional. If a format-to is provided the ImportHandler calls the metadataHandler for 
	 * the transformation file and calls XMLTransforming. 
	 * @param sourceName, identifier, formatTo, formatFrom
	 * @return itemXML
	 */
    public String fetchMetadata(String sourceName, String identifier, String formatTo)throws IdentifierNotRecognisedException, SourceNotAvailableException, TechnicalException{
    	
    	String itemXML = null;
    	identifier = this.trimIdentifier(sourceName, identifier);
    	ImportSourceVO importSource = new ImportSourceVO();
    	importSource = this.sourceHandler.getSourceByName(sourceName);  
    	ImportSourceHandlerBean sourcehandler = new ImportSourceHandlerBean();
    	
    	sourcehandler.printSourceXML(importSource);
    	logger.debug("Import from Source: " + sourceName);

    	//Construct request url with current parameter
    	try {
	    	String decoded = java.net.URLDecoder.decode(importSource.getMdUrl().toString(), importSource.getEncoding()); 
	    	importSource.setMdUrl(new URL (decoded));
	    	importSource.setMdUrl(new URL (importSource.getMdUrl().toString().replaceAll("GETID", identifier.trim())));
    	}
    	catch(MalformedURLException e){e.printStackTrace();}
    	catch(UnsupportedEncodingException e){ e.printStackTrace();}

    	//Select harvesting method
    	try{
	    	boolean supported = false;
	    	if (importSource.getHarvestProtocol().toLowerCase().equals("oai-pmh")){
	    			logger.debug("Fetch OAI record from URL: " + importSource.getMdUrl());
					itemXML = fetchOAIRecord (importSource);
					supported = true;
	    	}
	
	    	if (!supported){
	    		logger.warn("Harvesting protocol " +importSource.getHarvestProtocol()+" not supported");
	    		return null;
	    	}
    	}
    	catch(IdentifierNotRecognisedException e){
    		throw new IdentifierNotRecognisedException(e); 
    	}

    	//Transform the itemXML if necessary
    	if (formatTo != null && !importSource.getMdFormat().toLowerCase().trim().equals(formatTo.toLowerCase().trim())
    			&& itemXML!= null && !itemXML.trim().equals("")){
    		InitialContext initialContext = null;
    		MetadataHandler mdHandler;
			try {
	    		initialContext = new InitialContext();
				mdHandler = (MetadataHandler) initialContext.lookup(MetadataHandler.SERVICE_NAME);
				itemXML= mdHandler.transform(importSource.getMdFormat(),formatTo,itemXML);
			} 
			catch (NamingException e) {
				logger.error("Unable to initialize Metadata Handler", e);
				return null;
			}
		    catch (Exception e){
		        throw new IdentifierNotRecognisedException(e);
		    }
    	}
    	
//    	else {
//    		itemXML = this.getMDfromOAI(itemXML);
//    	}
    	
    	return itemXML;
    }
    
    /**
     * fetch data from a given url
	 * @param url
	 * @return byte[]
	 */
    public byte[] fetchMetadatafromURL (URL url)throws SourceNotAvailableException, TechnicalException{
    	
    	byte[] input = null;
	  	URLConnection conn = null;
    	Date retryAfter = null;	

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ZipOutputStream zos = new ZipOutputStream(baos);

	    	try {
		    	conn = url.openConnection();
		    	HttpURLConnection httpConn = (HttpURLConnection) conn;
		    	int responseCode = httpConn.getResponseCode();

		    	switch (responseCode)
		    	{
		    	case 503: 	String retryAfterHeader = conn.getHeaderField("Retry-After");
	            
		    	   			if (retryAfterHeader != null)
		    	   			{
		    	   				SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		    	   				retryAfter = dateFormat.parse(retryAfterHeader);
		    	   				throw new SourceNotAvailableException(retryAfter);
		    	   			}
		    	   			break;
		    	   			
		    	case 302: 	String  alternativeLocation = conn.getHeaderField("Location");
		        			try {
		        				return fetchMetadatafromURL(new URL (alternativeLocation));	        				
		        			}
		        			catch(MalformedURLException e){e.printStackTrace();}
		        			break;	        			

		    	case 200:	logger.info("Http Status 200 OK");
		    				System.out.println("Fetch file from URL: " + url);
		    				
		    				//Fetch file
					        GetMethod method = new GetMethod(url.toString());
					        HttpClient client = new HttpClient();
					        client.executeMethod(method);
					        input = method.getResponseBody();	
					        httpConn.disconnect();	
					        
					        //Create zip file with fetched file
							ZipEntry ze = new ZipEntry( "unapi" );
							ze.setSize(input.length);
							ze.setTime(this.currentDate());  
							CRC32 crc321 = new CRC32();
							crc321.update(input);
							ze.setCrc(crc321.getValue());

							zos.putNextEntry(ze);				
							zos.write(input);
							zos.flush();
							zos.closeEntry();
							zos.close();
		    				break;
		    	
		    	default:	throw new TechnicalException("An error occurred during importing from external system: " + responseCode + ": " + httpConn.getResponseMessage());
		    	}
	    	}	

	    	catch (IOException e){logger.error("Couldn't download file from: " + url, e);}	
	    	catch (ParseException e){logger.error("Couldn't parse response header", e);}
		
		return baos.toByteArray();
    }
    
    /**
     * Fetches the selected format from an external system
     * @param sourceName, identifier, format
	 * @return a file in the given format
     * @throws FileNotFoundException 
     */
    public byte[] fetchData(String sourceName, String identifier, String[] listOfFormats) throws FileNotFoundException{

    	ImportSourceVO importSource = new ImportSourceVO();
    	FullTextVO fulltext = new FullTextVO();
    	Vector<FullTextVO> v_fulltext = new Vector<FullTextVO>();
    	identifier = this.trimIdentifier(sourceName, identifier);
    	String format;
    	//InputStream in = null;
    	byte [] in = null;
    	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ZipOutputStream zos = new ZipOutputStream(baos);
		
    	importSource = this.sourceHandler.getSourceByName(sourceName);
    	v_fulltext = importSource.getFtFormats();
       	
		//Call fetch file for every selected format
		for (int i =0; i < listOfFormats.length; i++){
			format = listOfFormats[i];
			for (int x=0; x< v_fulltext.size();x++){
				if (v_fulltext.get(x).getFtFormat().equals(format)){
					fulltext = v_fulltext.get(x);
					
					//Replace regex with identifier
					try {
				    	String decoded = java.net.URLDecoder.decode(fulltext.getFtUrl().toString(), importSource.getEncoding()); 
				    	fulltext.setFtUrl(new URL (decoded));
				    	fulltext.setFtUrl(new URL (fulltext.getFtUrl().toString().replaceAll("GETID", identifier.trim())));
						
					} 
					catch (MalformedURLException e) {logger.error("Error when replacing regex in fetching URL"); e.printStackTrace(); }
					catch(UnsupportedEncodingException e){e.printStackTrace();}
				}
			}
			
			logger.debug("Fetch file from URL: " + fulltext.getFtUrl());
			System.out.println("Fetch file from URL: " + fulltext.getFtUrl());
			
			try {
				in= this.fetchFile(importSource, fulltext);	

				ZipEntry ze = new ZipEntry( identifier + fulltext.getFileType());
				ze.setSize(in.length);
				ze.setTime(this.currentDate());  
				CRC32 crc321 = new CRC32();
				crc321.update(in);
				ze.setCrc(crc321.getValue());

				zos.putNextEntry(ze);				
				zos.write(in);
				zos.flush();
				zos.closeEntry();
			} 
			catch (SourceNotAvailableException e) {
				logger.warn("Import Source not available",e);
			}
			catch (TechnicalException e) {
				logger.warn("Technical problems occurred when communication with import source",e);
			}
			catch (IOException e){ e.printStackTrace();}
		}

		try {
			zos.close();
		} 
		catch (IOException e) {e.printStackTrace();}

		
		return baos.toByteArray();
    }


	private byte[] fetchFile(ImportSourceVO importSource, FullTextVO fulltext) throws SourceNotAvailableException, TechnicalException{
    	
    	URLConnection conn = null;
    	Date retryAfter = null;	
    	byte[] input = null;
    	
    	//Fetch Data
    	try {
	    	conn = fulltext.getFtUrl().openConnection();
	    	HttpURLConnection httpConn = (HttpURLConnection) conn;
	    	int responseCode = httpConn.getResponseCode();

	    	switch (responseCode)
	    	{
	    	case 503: 	String retryAfterHeader = conn.getHeaderField("Retry-After");
            
	    	   			if (retryAfterHeader != null)
	    	   			{
	    	   				SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	    	   				retryAfter = dateFormat.parse(retryAfterHeader);
	    	   				
	    	   				logger.debug("Retry after " + retryAfter);
	    	   				throw new SourceNotAvailableException(retryAfter);
	    	   			}
	    	   			else
	    	   			{
	    	   				logger.debug("Retry after " + importSource.getRetryAfter());
	    	   				throw new TechnicalException("Import source returned 503 without 'Retry-After' header.");
	    	   			}
	    	   			
	    	case 302: 	String  alternativeLocation = conn.getHeaderField("Location");
	        			try {
	        				fulltext.setFtUrl(new URL(alternativeLocation));
	        				return fetchFile(importSource, fulltext);	        				
	        			}
	        			catch(MalformedURLException e){e.printStackTrace();}
	        			break;	        			

	    	case 200:	logger.info("Http Status 200 OK");

	    		        GetMethod method = new GetMethod(fulltext.getFtUrl().toString());
	    		        HttpClient client = new HttpClient();
	    		        client.executeMethod(method);
	    		        input = method.getResponseBody();	
	    		        httpConn.disconnect();
	    				break;
	    	
	    	default:	throw new TechnicalException("An error occurred during importing from external system: " + responseCode + ": " + httpConn.getResponseMessage());
	    	}
    	}	
    	catch (IOException e){logger.error("Couldn't download file from: " + importSource.getName(), e);}	
    	catch (ParseException e){logger.error("Couldn't parse response header", e);}

    	return input;
    }
    
    /**
     * Fetches an OAI record for given record identifier
     * @param sourceURL
	 * @return itemXML
     * @throws SourceNotAvailableException 
     * @throws TechnicalException 
     */
    private String fetchOAIRecord (ImportSourceVO importSource) throws SourceNotAvailableException, TechnicalException, IdentifierNotRecognisedException {
    	
    	String itemXML= "";
    	URLConnection conn; 
    	Date retryAfter;
    	String charset = importSource.getEncoding();
    	InputStreamReader ISreader;
    	BufferedReader Breader;

    	try {
	    	conn = importSource.getMdUrl().openConnection();
	    	HttpURLConnection httpConn = (HttpURLConnection) conn;
	    	int responseCode = httpConn.getResponseCode();
	    	
	    	switch (responseCode)
	    	{
	    	case 503: 	String retryAfterHeader = conn.getHeaderField("Retry-After");
            
	    	   			if (retryAfterHeader != null)
	    	   			{
	    	   				SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	    	   				retryAfter = dateFormat.parse(retryAfterHeader);	    	                    
	    	   				logger.debug("Retry after " + retryAfter);
	    	   				throw new SourceNotAvailableException(retryAfter);
	    	   			}
	    	   			else
	    	   			{
	    	   				logger.debug("Retry after " + importSource.getRetryAfter());
	    	   				throw new SourceNotAvailableException(importSource.getRetryAfter());
	    	   			}
	    	   			
	    	case 302: 	String  alternativeLocation = conn.getHeaderField("Location");
	        			try {
	        				importSource.setMdUrl(new URL(alternativeLocation));
	        				return fetchOAIRecord(importSource);
	        			}
	        			catch(MalformedURLException e){e.printStackTrace();}
	        			break;
	        			

	    	case 200:	logger.info("Http Status 200 OK");
	    				break;
	    	
	    	default:	throw new TechnicalException("An error occurred during importing from external system: " + responseCode + ": " + httpConn.getResponseMessage());
	    	}
	    	
	    	
    		String contentTypeHeader = conn.getHeaderField("Content-Type");
    		String contentType = contentTypeHeader;
    		
    		if (contentType.contains(";"))
    		{
    			contentType = contentType.substring(0, contentType.indexOf(";"));
    			if (contentTypeHeader.contains("encoding="))
    			{
    				charset = contentTypeHeader.substring(contentTypeHeader.indexOf("encoding=") + 9);
    				logger.debug("Charset found: " + charset);
    			}
    		}
    	}
    	
    	catch (IOException e){e.printStackTrace();}	
    	catch (ParseException e){logger.error("Couldn't parse response header", e);}
    		
    	//Get itemXML
    	try {
    		ISreader = new InputStreamReader(importSource.getMdUrl().openStream(), charset);      	
        	Breader = new BufferedReader(ISreader);
        	String line = "";

    		while ((line = Breader.readLine()) != null)
    		{
    			itemXML += line + "\n";
    			
    		}
    	}
    	catch(UnsupportedEncodingException e){e.printStackTrace();}
    	catch(IOException e){throw new IdentifierNotRecognisedException();}

//    	System.out.println(itemXML);   	
//    	if (this.OAIRecordError(itemXML)){throw new IdentifierNotRecognisedException ();}
    	return itemXML;
    }
    
    /**
     * This is the only source specific method, which has to be updated when a new source
     * is specified for import
     */
    public String trimIdentifier(String sourceName, String identifier)
    {
    	//Trim the identifier source arXiv
    	if (sourceName.trim().toLowerCase().equals("arxiv")||sourceName.trim().toLowerCase().equals("arxiv(oai_dc)"))
    	{
    		if (identifier.toLowerCase().startsWith("oai:arxiv.org:", 0))
            {
                identifier = identifier.substring(14);
            }
            if (identifier.toLowerCase().startsWith("arxiv:", 0))
            {
                identifier = identifier.substring(6);
            }
    	}
    	return identifier.trim();
    }
    
    /**
     * Extracts the Metadata from the OAI-PMH xml
     * @param itemXML
     * @return Metadata part of the OAI-PMH xml
     */
    private String getMDfromOAI(String itemXML){
    	String itemMD =null;
    	String [] tmp;

    	tmp = itemXML.split("<metadata>");
    	if (tmp != null){
    		if (tmp.length == 2){
    			itemMD = tmp [1];
    			tmp = itemMD.split("</metadata>");
    			if (tmp != null){
    				if (tmp.length ==2){
    					itemMD=tmp[0];
    				}
    			}
    		}
    	}
    	return itemMD;
    }

    private boolean OAIRecordError(String record){

    	//TODO More intelligent and correct way to check the oai record by reading in the record
    	//as a object (oai-pmh.jar already compiled via xmlbeans)
    	System.out.println("OAI Record returned with error tag");
    	return record.contains("<error code=");
    }
    
	public String getDATA_RETURN_FILETYPE() {
		return this.DATA_RETURN_FILETYPE;
	}

	public String getDATA_RETURN_MIMETYPE() {
		return this.DATA_RETURN_MIMETYPE;
	}
	
    public long currentDate() {
        Date today = new Date();
        return today.getTime(); 
      }
    
    
    public int getContentLength() {
		return this.contentLength;
	}


	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
}
