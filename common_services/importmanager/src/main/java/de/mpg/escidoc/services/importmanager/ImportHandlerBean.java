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
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
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
import javax.xml.rpc.ServiceException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.mpg.escidoc.services.common.MetadataHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.logging.LogMethodDurationInterceptor;
import de.mpg.escidoc.services.common.logging.LogStartEndInterceptor;
import de.mpg.escidoc.services.common.metadata.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.importmanager.exceptions.SourceNotAvailableException;
import de.mpg.escidoc.services.importmanager.valueobjects.FullTextVO;
import de.mpg.escidoc.services.importmanager.valueobjects.ImportSourceVO;
import de.mpg.escidoc.services.importmanager.valueobjects.MetadataVO;



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
	
	private final String fetchType_METADATA = "METADATA";
	private final String fetchType_FILE = "FILE";
	private final String fetchType_CITATION = "CITATION";
	private final String fetchType_LAYOUT = "LAYOUT";
	
	private final String REGEX ="GETID";

	private ImportSourceHandlerBean sourceHandler = new ImportSourceHandlerBean();	
	private String contentType;
	private String fileEnding;

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
    		explainXML = ResourceUtil.getResourceAsString("resources/sources.xml");
    	}
    	catch(IOException e){ e.printStackTrace();}
    	return explainXML;
    }	

    public String fetchMetadata(String sourceName, String identifier)throws IdentifierNotRecognisedException, SourceNotAvailableException, TechnicalException{
    	ImportSourceVO source = this.sourceHandler.getSourceByName(sourceName);
    	MetadataVO md = this.sourceHandler.getDefaultMdFormatFromSource(source);
    	return this.fetchMetadata(sourceName, identifier, md.getMdLabel());
    }

    public String fetchMetadata(String sourceName, String identifier, String format)throws IdentifierNotRecognisedException, SourceNotAvailableException, TechnicalException{
    	
    	String itemXML = null;
    	identifier = this.trimIdentifier(sourceName, identifier);
    	ImportSourceVO importSource = new ImportSourceVO();
    	importSource = this.sourceHandler.getSourceByName(sourceName);  
    	MetadataVO md = this.getMdObjectToFetch(importSource, format);
    	
    	logger.debug("Import from Source: " + sourceName);

    	//Construct request url with current parameter
    	try {
	    	String decoded = java.net.URLDecoder.decode(md.getMdUrl().toString(), importSource.getEncoding()); 
	    	md.setMdUrl(new URL (decoded));
	    	md.setMdUrl(new URL (md.getMdUrl().toString().replaceAll(this.REGEX, identifier.trim())));
	    	importSource = this.sourceHandler.updateMdEntry(importSource, md);
    	}
    	catch(MalformedURLException e){e.printStackTrace();}
    	catch(UnsupportedEncodingException e){ e.printStackTrace();}

    	//Select harvesting method
    	try{
	    	boolean supported = false;
	    	if (importSource.getHarvestProtocol().toLowerCase().equals("oai-pmh")){
	    			logger.debug("Fetch OAI record from URL: " + md.getMdUrl());
					itemXML = fetchOAIRecord (importSource, md);
					supported = true;
	    	}
	    	if (importSource.getHarvestProtocol().toLowerCase().equals("ejb")){
				try {
					itemXML = ServiceLocator.getItemHandler().retrieve("escidoc:"+identifier);
				} 
				catch (MissingMethodParameterException e) {e.printStackTrace(); throw new IdentifierNotRecognisedException(e);} 
				catch (ItemNotFoundException e) {e.printStackTrace(); throw new IdentifierNotRecognisedException(e);} 
				catch (AuthenticationException e) {e.printStackTrace(); throw new IdentifierNotRecognisedException(e);} 
				catch (AuthorizationException e) {e.printStackTrace(); throw new IdentifierNotRecognisedException(e);} 
				catch (SystemException e) {e.printStackTrace(); throw new IdentifierNotRecognisedException(e);} 
				catch (RemoteException e) {e.printStackTrace(); throw new IdentifierNotRecognisedException(e);} 
				catch (ServiceException e) {e.printStackTrace(); throw new IdentifierNotRecognisedException(e);} 
				catch (URISyntaxException e) {e.printStackTrace(); throw new IdentifierNotRecognisedException(e);}
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
	    if (format != null && itemXML!= null && !itemXML.trim().equals("")
	    		&& !format.trim().toLowerCase().equals(md.getMdLabel().toLowerCase())){
	    	InitialContext initialContext = null;
	    	MetadataHandler mdHandler;
			try {
		    	initialContext = new InitialContext();
				mdHandler = (MetadataHandler) initialContext.lookup(MetadataHandler.SERVICE_NAME);
				itemXML= mdHandler.transform(md.getMdFormat(),format,itemXML);
			} 
			catch (NamingException e) {
				logger.error("Unable to initialize Metadata Handler", e);
				return null;
			}
		    catch (Exception e){
		        throw new IdentifierNotRecognisedException(e);
		    }
	   	}
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
    	byte [] in = null;
    	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ZipOutputStream zos = new ZipOutputStream(baos);
		
    	importSource = this.sourceHandler.getSourceByName(sourceName);
    	v_fulltext = importSource.getFtFormats();
       	
		//Call fetch file for every selected format
		for (int i =0; i < listOfFormats.length; i++){
			format = listOfFormats[i];
			for (int x=0; x< v_fulltext.size();x++){
				if (v_fulltext.get(x).getFtLabel().toLowerCase().equals(format.toLowerCase())){
					fulltext = v_fulltext.get(x);
					
					//Replace regex with identifier
					try {
				    	String decoded = java.net.URLDecoder.decode(fulltext.getFtUrl().toString(), importSource.getEncoding()); 
				    	fulltext.setFtUrl(new URL (decoded));
				    	fulltext.setFtUrl(new URL (fulltext.getFtUrl().toString().replaceAll(this.REGEX, identifier.trim())));
						
					} 
					catch (MalformedURLException e) {logger.error("Error when replacing regex in fetching URL"); e.printStackTrace(); }
					catch(UnsupportedEncodingException e){e.printStackTrace();}
				}
			}
			
			logger.debug("Fetch file from URL: " + fulltext.getFtUrl());
			System.out.println("Fetch file from URL: " + fulltext.getFtUrl());
			
			try {
				in= this.fetchFile(importSource, fulltext);	
				
				if (listOfFormats.length == 1){
					this.setContentType(fulltext.getFtFormat());
					this.setFileEnding(fulltext.getFileType());
					return in;
				}
				else{
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
			this.setContentType("application/zip");
			this.setFileEnding(".zip");
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
    private String fetchOAIRecord (ImportSourceVO importSource, MetadataVO md) throws SourceNotAvailableException, TechnicalException, IdentifierNotRecognisedException {
    	
    	String itemXML= "";
    	URLConnection conn; 
    	Date retryAfter;
    	String charset = importSource.getEncoding();
    	InputStreamReader ISreader;
    	BufferedReader Breader;

    	try {
	    	conn = md.getMdUrl().openConnection();
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
	        				md.setMdUrl(new URL(alternativeLocation));
	        				importSource = this.sourceHandler.updateMdEntry(importSource, md);
	        				return fetchOAIRecord(importSource, md);
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
    		ISreader = new InputStreamReader(md.getMdUrl().openStream(), charset);      	
        	Breader = new BufferedReader(ISreader);
        	String line = "";

    		while ((line = Breader.readLine()) != null)
    		{
    			itemXML += line + "\n";
    			
    		}
    	}
    	catch(UnsupportedEncodingException e){e.printStackTrace();}
    	catch(IOException e){throw new IdentifierNotRecognisedException();}
    	
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
     * This operation return the Metadata Object of the format to fetch from the source.
     * @param source
     * @param format
     * @return Metadata Object of the format to fetch
     */
    private MetadataVO getMdObjectToFetch(ImportSourceVO source, String format){
    	MetadataVO sourceMd = null;
    	MetadataVO transformMd = null;
    	
    	//First: check if format can be fetched directly
		for (int i=0; i< source.getMdFormats().size(); i++){
			sourceMd = source.getMdFormats().get(i);
    		if (sourceMd.getMdLabel().trim().toLowerCase().equals(format.trim().toLowerCase())){
    			return sourceMd;
        	}
		}
    	
    	//Second: check which format can be transformed into the given format 
    	Vector <String> possibleFormats = this.sourceHandler.getFormatsForTransformation(format);
    	Vector <MetadataVO> possibleMds = new Vector <MetadataVO>();
    	
		for (int i=0; i< source.getMdFormats().size(); i++){
			transformMd = source.getMdFormats().get(i);
    		for (int x =0; x< possibleFormats.size(); x++){
    			String possibleFormat = possibleFormats.get(x);
	    		if (transformMd.getMdLabel().trim().toLowerCase().equals(possibleFormat)){
	    			possibleMds.add(transformMd);
	        	}
    		}
		}
		
		//More than one format from this source can be transformed into the requested format
		if (possibleMds.size() > 1){
			for (int y=0; y< possibleMds.size(); y++){
				transformMd = possibleMds.get(y);
				if (transformMd.isMdDefault()){
					return sourceMd = this.sourceHandler.getMdObjectfromSource(source,transformMd.getMdLabel());
				}
				//If no default format was declared, one random like metadata set is returned
				else {
					sourceMd = this.sourceHandler.getMdObjectfromSource(source,transformMd.getMdLabel());
				}
			}
		}		
		else{
			sourceMd= possibleMds.get(0);
		}
    	return sourceMd;
    }
    
    
//    public byte[] TestfetchData(String sourceName, String identifier, String FormatFrom, String FormatTo)throws FileNotFoundException, IdentifierNotRecognisedException, SourceNotAvailableException, TechnicalException {
//    	
//    	byte[] fetchedData = null;
//    	String type = this.getFetchingType(FormatFrom);
//    	
//    	if (type.equals(this.fetchType_METADATA)){fetchedData = this.fetchMetadata(sourceName, identifier, FormatFrom, FormatTo).getBytes();}
//    	if (type.equals(this.fetchType_FILE)){fetchedData = this.fetchData(sourceName, identifier, new String[]{FormatTo});}
//    	if (type.equals(this.fetchType_CITATION)){};
//    	if (type.equals(this.fetchType_LAYOUT)){};
//    	
//    	return fetchedData;
//    	
//    }
//    
//    private String getFetchingType (String formatFrom){
//    	String type = "";
//    	//TODO: get fetching type from sources.xml or transformation.xml
//    	type = this.fetchType_FILE;
//    	return type;
//    }
	
    public long currentDate() {
        Date today = new Date();
        return today.getTime(); 
      }
    
	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getFileEnding() {
		return this.fileEnding;
	}

	public void setFileEnding(String fileEnding) {
		this.fileEnding = fileEnding;
	}
}
