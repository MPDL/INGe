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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.common.MetadataHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.logging.LogMethodDurationInterceptor;
import de.mpg.escidoc.services.common.logging.LogStartEndInterceptor;
import de.mpg.escidoc.services.common.metadata.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.importmanager.exceptions.FormatNotRecognizedException;
import de.mpg.escidoc.services.importmanager.exceptions.SourceNotAvailableException;
import de.mpg.escidoc.services.importmanager.valueobjects.FullTextVO;
import de.mpg.escidoc.services.importmanager.valueobjects.ImportSourceVO;
import de.mpg.escidoc.services.importmanager.valueobjects.MetadataVO;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportHandler;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportManagerException;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportXSLTNotFoundException;



/**
 * This class provides the ejb implementation of the {@link ImportHandler} interface.
 * @author Friederike Kleinfercher (initial creation)
 */ 

@Remote
@RemoteBinding(jndiBinding = ImportHandler.SERVICE_NAME)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors( { LogStartEndInterceptor.class, LogMethodDurationInterceptor.class })

public class ImportHandlerBean implements ImportHandler {
	
	private final static Logger logger = Logger.getLogger(ImportHandlerBean.class);
	
	private final String fetchType_METADATA = 	"METADATA";
	private final String fetchType_FILE 	= 	"FILE";
	private final String fetchType_CITATION = 	"CITATION";
	private final String fetchType_LAYOUT	= 	"LAYOUT";
	private final String fetchType_UNKNOWN 	= 	"UNKNOWN";
	private final String REGEX 				=	"GETID";
	
	//This is tmp till clarification of new transformation design
	private final String fetchType_ENDNOTE 	= 	"ENDNOTE";
	private final String fetchType_BIBTEX 	= 	"BIBTEX";
	private final String fetchType_APA 		= 	"APA";


	private ImportSourceHandlerBean sourceHandler = new ImportSourceHandlerBean();	
	private String contentType;
	private String fileEnding;
	
	public ImportHandlerBean(){}
	
	
	/**
	 * {@inheritDoc}
	 */
    public byte[] doFetch(String sourceName, String identifier)throws FileNotFoundException, 
    																 		IdentifierNotRecognisedException, 
    																 		SourceNotAvailableException, 
    																 		TechnicalException,
    																 		FormatNotRecognizedException{
    	
    	ImportSourceVO source = this.sourceHandler.getSourceByName(sourceName);
    	MetadataVO md = this.sourceHandler.getDefaultMdFormatFromSource(source);
    	return this.doFetch(sourceName, identifier, md.getMdLabel());
    }

    
    /**
     * {@inheritDoc}
     */
    public byte[] doFetch(String sourceName, String identifier, String format)throws FileNotFoundException, 
    																					   IdentifierNotRecognisedException, 
    																					   SourceNotAvailableException, 
    																					   TechnicalException,
    																					   FormatNotRecognizedException{  	
    	byte[] fetchedData = null;
    	
    	try{
        	identifier = this.trimIdentifier(sourceName, identifier);
        	ImportSourceVO importSource = new ImportSourceVO();
        	importSource = this.sourceHandler.getSourceByName(sourceName);         	
	    	String fetchType = this.getFetchingType(importSource, format);  
	    	
	    	if (fetchType.equals(this.fetchType_METADATA)){
	    		fetchedData = this.fetchMetadata(importSource, identifier, format).getBytes();
	    	}
	    	
	    	if (fetchType.equals(this.fetchType_FILE)){
	    		fetchedData = this.fetchData(importSource, identifier, new String [] {format});
	    	}	    	
	    	if (fetchType.equals(this.fetchType_CITATION)){
	    		//TODO
	    	}	   
	    	if (fetchType.equals(this.fetchType_LAYOUT)){
	    		//TODO
	    	}	
	    	if (fetchType.equals(this.fetchType_ENDNOTE)){
	    		//Temp
	    		fetchedData = this.fetchEndnoteTemp(identifier);
	    	}
	    	if (fetchType.equals(this.fetchType_BIBTEX)){
	    		//Temp
	    		fetchedData = this.fetchBibtexTemp(identifier);
	    	}
	    	if (fetchType.equals(this.fetchType_APA)){
	    		//Temp
	    		fetchedData = this.fetchApaTemp(identifier);
	    	}
	    	if (fetchType.equals(this.fetchType_UNKNOWN)){
	    		throw new FormatNotRecognizedException();
	    	}
	    	
	    	System.out.println("Fetched file type: " + fetchType);
    	}
    	catch(IdentifierNotRecognisedException e){throw new IdentifierNotRecognisedException();}
    	catch(SourceNotAvailableException e){throw new SourceNotAvailableException();}
    	catch(TechnicalException e){throw new TechnicalException();}
    	
    	return fetchedData;
    }

    /**
     * {@inheritDoc}
     */
    public byte[] doFetch(String sourceName, String identifier, String[] formats)throws FileNotFoundException, 
    																						  IdentifierNotRecognisedException, 
    																						  SourceNotAvailableException, 
    																						  TechnicalException{
    	identifier = this.trimIdentifier(sourceName, identifier);
    	ImportSourceVO importSource = new ImportSourceVO();
    	importSource = this.sourceHandler.getSourceByName(sourceName); 
    	return this.fetchData(importSource, identifier, formats);
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

    /**
     * Operation for fetching data of type METADATA
     * @param importSource
     * @param identifier
     * @param format
     * @return itemXML
     * @throws IdentifierNotRecognisedException
     * @throws SourceNotAvailableException
     * @throws TechnicalException
     */
    private String fetchMetadata(ImportSourceVO importSource, String identifier, String format)throws IdentifierNotRecognisedException, 
    																								 SourceNotAvailableException, 
    																								 TechnicalException{   	
    	String itemXML = null; 
    	boolean supportedProtocol = false;
    	InitialContext initialContext = null;
    	MetadataHandler mdHandler;
    	MetadataVO md = this.getMdObjectToFetch(importSource, format);

    	//Replace regex with identifier
    	try {
	    	String decoded = java.net.URLDecoder.decode(md.getMdUrl().toString(), importSource.getEncoding()); 
	    	md.setMdUrl(new URL (decoded));
	    	md.setMdUrl(new URL (md.getMdUrl().toString().replaceAll(this.REGEX, identifier.trim())));
	    	importSource = this.sourceHandler.updateMdEntry(importSource, md);

	    	//Select harvesting method	    	
	    	if (importSource.getHarvestProtocol().toLowerCase().equals("oai-pmh")){
	    		logger.debug("Fetch OAI record from URL: " + md.getMdUrl());
				itemXML = fetchOAIRecord (importSource, md);
				supportedProtocol = true;
	    	}
	    	if (importSource.getHarvestProtocol().toLowerCase().equals("ejb")){
				logger.debug("Fetch record via EJB: ");
				itemXML = this.fetchEsciDocRecord(identifier);
				supportedProtocol = true;							
	    	}
	    	if (!supportedProtocol){
	    		logger.warn("Harvesting protocol " +importSource.getHarvestProtocol()+" not supported");
	    		return null;
	    	}
    	}
    	catch(IdentifierNotRecognisedException e){throw new IdentifierNotRecognisedException(e); }
    	catch(MalformedURLException e){e.printStackTrace();}
    	catch(UnsupportedEncodingException e){ e.printStackTrace();}

	    //Transform the itemXML if necessary
	    if (itemXML!= null && !itemXML.trim().equals("")
	    		&& !format.trim().toLowerCase().equals(md.getMdLabel().toLowerCase())){
			try {
		    	initialContext = new InitialContext();
				mdHandler = (MetadataHandler) initialContext.lookup(MetadataHandler.SERVICE_NAME);
				itemXML= mdHandler.transform(md.getMdLabel(),format,itemXML);
			} 
			catch (NamingException e) {logger.error("Unable to initialize Metadata Handler", e);return null;}
		    catch (Exception e){throw new IdentifierNotRecognisedException(e);}
	   	}
	    
		this.setContentType(md.getMdFormat());
		this.setFileEnding(md.getFileType());
	    
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
	 * Operation for fetching data of type FILE
	 * @param importSource
	 * @param identifier
	 * @param listOfFormats
	 * @return byte[] of the fetched file, zip file if more than one record was fetched
	 */
    private byte[] fetchData(ImportSourceVO importSource, String identifier, String[] listOfFormats){

    	byte [] in = null;   
    	FullTextVO fulltext = new FullTextVO();   		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ZipOutputStream zos = new ZipOutputStream(baos);

       	
		//Call fetch file for every selected format
		for (int i =0; i < listOfFormats.length; i++){
			String format = listOfFormats[i];
			fulltext=this.getFtObjectToFetch(importSource, format);
					
			//Replace regex with identifier
			try {
				 String decoded = java.net.URLDecoder.decode(fulltext.getFtUrl().toString(), importSource.getEncoding()); 
				 fulltext.setFtUrl(new URL (decoded));
				 fulltext.setFtUrl(new URL (fulltext.getFtUrl().toString().replaceAll(this.REGEX, identifier.trim())));						
			} 
			catch (MalformedURLException e) {logger.error("Error when replacing regex in fetching URL"); e.printStackTrace(); }
			catch(UnsupportedEncodingException e){e.printStackTrace();}
			
			logger.debug("Fetch file from URL: " + fulltext.getFtUrl());
			
			try {
				in= this.fetchFile(importSource, fulltext);	
				
				//If only one file => return it in fetched format
				if (listOfFormats.length == 1){
					this.setContentType(fulltext.getFtFormat());
					this.setFileEnding(fulltext.getFileType());
					return in;
				}
				//If more than one file => add it to zip
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
				
				this.setContentType("application/zip");
				this.setFileEnding(".zip");
			} 
			catch (SourceNotAvailableException e) {logger.warn("Import Source not available",e);}
			catch (TechnicalException e) {logger.warn("Technical problems occurred when communication with import source",e);}
			catch (IOException e){ e.printStackTrace();}
		}
		
		try {
			zos.close();
		} 
		catch (IOException e) {e.printStackTrace();}
		
		return baos.toByteArray();
    }


    /**
     * Handlers the http request to fetch a file from an external source
     * @param importSource
     * @param fulltext
     * @return byte[] of the fetched file
     * @throws SourceNotAvailableException
     * @throws TechnicalException
     */
	private byte[] fetchFile(ImportSourceVO importSource, FullTextVO fulltext) throws SourceNotAvailableException, TechnicalException{
    	
    	URLConnection conn = null;
    	Date retryAfter = null;	
    	byte[] input = null;

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
     * Fetches a eSciDoc Record from eSciDoc system
     * @param  identifier of the item
     * @return itemXML as String
     * @throws IdentifierNotRecognisedException
     * @throws SourceNotAvailableException
     */
    private String fetchEsciDocRecord (String identifier) throws IdentifierNotRecognisedException, SourceNotAvailableException{
		try {
			return ServiceLocator.getItemHandler().retrieve(identifier);
		} 
		catch (ItemNotFoundException e) {e.printStackTrace(); throw new IdentifierNotRecognisedException(e);} 
		catch (URISyntaxException e) {e.printStackTrace(); throw new IdentifierNotRecognisedException(e);}
		catch (Exception e) {e.printStackTrace(); throw new SourceNotAvailableException(e);} 
    }
    
    /**
     * For a more flexible interface for handling user input
     * This is the only source specific method, which should be updated when a new source
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
    

    private byte[] fetchEndnoteTemp (String identifier)throws IdentifierNotRecognisedException{
    	byte[] cite = null;
    	String item = null;
    	
    	try {
    		InitialContext initialContext = new InitialContext();
    		XmlTransforming xmlTransforming = (XmlTransforming)initialContext.lookup(XmlTransforming.SERVICE_NAME);
    		StructuredExportHandler structExport = (StructuredExportHandler) initialContext.lookup(StructuredExportHandler.SERVICE_NAME);
    		
			item = this.fetchEsciDocRecord(identifier);
			PubItemVO itemVO = xmlTransforming.transformToPubItem(item);			
			List <PubItemVO>pubitemList = Arrays.asList(itemVO);
			String itemList = xmlTransforming.transformToItemList(pubitemList);
			
			cite = structExport.getOutput(itemList, "ENDNOTE");
			this.setContentType("text/plain");
			this.setFileEnding(".enl");
		} 
    	catch (IdentifierNotRecognisedException e) {throw new IdentifierNotRecognisedException(e);} 
    	catch (SourceNotAvailableException e) {e.printStackTrace();}
    	catch (NamingException e) {e.printStackTrace();}
    	catch (StructuredExportManagerException e) {e.printStackTrace();}
    	catch (StructuredExportXSLTNotFoundException e) {e.printStackTrace();}
    	catch (TechnicalException e) {e.printStackTrace();}

    	return cite;
    }
    
    private byte[] fetchBibtexTemp (String identifier)throws IdentifierNotRecognisedException {
    	byte[] bib = null;
    	String item = null;
    	
    	try {
    		InitialContext initialContext = new InitialContext();
    		XmlTransforming xmlTransforming = (XmlTransforming)initialContext.lookup(XmlTransforming.SERVICE_NAME);
    		StructuredExportHandler structExport = (StructuredExportHandler) initialContext.lookup(StructuredExportHandler.SERVICE_NAME);
    		
			item = this.fetchEsciDocRecord(identifier);
			PubItemVO itemVO = xmlTransforming.transformToPubItem(item);			
			List <PubItemVO>pubitemList = Arrays.asList(itemVO);
			String itemList = xmlTransforming.transformToItemList(pubitemList);
			
			bib = structExport.getOutput(itemList, "BIBTEX");
			this.setContentType("text/plain");
			this.setFileEnding(".bib");
		} 
    	catch (IdentifierNotRecognisedException e) {throw new IdentifierNotRecognisedException(e);} 
    	catch (SourceNotAvailableException e) {e.printStackTrace();}
    	catch (NamingException e) {e.printStackTrace();}
    	catch (StructuredExportManagerException e) {e.printStackTrace();}
    	catch (StructuredExportXSLTNotFoundException e) {e.printStackTrace();}
    	catch (TechnicalException e) {e.printStackTrace();}

    	return bib;
    }
    
    private byte[] fetchApaTemp (String identifier) throws IdentifierNotRecognisedException{
    	byte[] apa = null;
    	String item = null;
    	
    	try {
    		InitialContext initialContext = new InitialContext();
    		XmlTransforming xmlTransforming = (XmlTransforming)initialContext.lookup(XmlTransforming.SERVICE_NAME);
    		CitationStyleHandler citeHandler = (CitationStyleHandler) initialContext.lookup(CitationStyleHandler.SERVICE_NAME);
    		
			item = this.fetchEsciDocRecord(identifier);
			PubItemVO itemVO = xmlTransforming.transformToPubItem(item);			
			List <PubItemVO>pubitemList = Arrays.asList(itemVO);
			String itemList = xmlTransforming.transformToItemList(pubitemList);
			
			apa = citeHandler.getOutput("APA", "rtf", itemList);
			this.setContentType("application/rtf");
			this.setFileEnding(".rtf");
		} 
    	catch (IdentifierNotRecognisedException e) {throw new IdentifierNotRecognisedException(e);} 
    	catch (SourceNotAvailableException e) {e.printStackTrace();}
    	catch (NamingException e) {e.printStackTrace();}
    	catch (CitationStyleManagerException e) {e.printStackTrace();}
    	catch (JRException e) {e.printStackTrace();}
    	catch (IOException e) {e.printStackTrace();}
    	catch (TechnicalException e) {e.printStackTrace();}

    	return apa;
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
		if (possibleMds.size() == 1){
			sourceMd= possibleMds.get(0);
		}
		if (possibleMds.size() == 0){
			sourceMd = null;
		}
    	return sourceMd;
    }
    
    /**
     * This operation return the Fulltext Object of the format to fetch from the source.
     * @param source
     * @param format
     * @return Fulltext Object of the format to fetch
     */
    private FullTextVO getFtObjectToFetch(ImportSourceVO source, String format){
    	FullTextVO Ft = null;
    	
		for (int i=0; i< source.getFtFormats().size(); i++){
			Ft = source.getFtFormats().get(i);
    		if (Ft.getFtLabel().trim().toLowerCase().equals(format.trim().toLowerCase())){
    			return Ft;
        	}
    		else {Ft = null;}
		}   	
    	return Ft;
    }

    /**
     * Decide which kind of data has to be fetched
     * @param source
     * @param format
     * @return type of data to be fetched {METADATA, FILE, CITATION, LAYOUTFORMAT}
     */
    private String getFetchingType (ImportSourceVO source, String format){
    	
    	//tmp, till we clearify the new transformation service design
    	if(format.toLowerCase().equals("endnote")){return this.fetchType_ENDNOTE;}
    	if(format.toLowerCase().equals("bibtex")){return this.fetchType_BIBTEX;}
    	if(format.toLowerCase().equals("apa")){return this.fetchType_APA;}
    	
    	if (this.getMdObjectToFetch(source, format)!= null){return this.fetchType_METADATA;}
    	if (this.getFtObjectToFetch(source, format)!= null){return this.fetchType_FILE;}
    	
    	return this.fetchType_UNKNOWN;
    }
	
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
