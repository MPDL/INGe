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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.exportmanager;
 
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExport;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportManagerException;
 
/**
 * Structured Export Manager. 
 * Converts PubMan item-list to one of the structured formats.   
 *
 * @author Vlad Makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */ 

public class Export implements ExportHandler {

 
	private final static Logger logger = Logger.getLogger(Export.class);
	
	public static enum ArchiveFormats { zip, tar, gzip };
	
	public static enum ExportFormatTypes { STRUCTURED, LAYOUT };
	
	public static String COMPONENTS_NS;
	public static String MDRECORDS_NS;
	public static String FILE_NS;
	public static String DC_NS;
	public static String DCTERMS_NS;
	public static String PROPERTIES_NS;

	public static final String XLINK_NS = "http://www.w3.org/1999/xlink";
	
	
	public static final short BUFFER_SIZE = 1024;
	private static final int NUMBER_OF_URL_TOKENS = 2;
	
	private static String USER_ID;
	private static String PASSWORD;
    private static final String PROPERTY_USER_ID = "framework.admin.username";
    private static final String PROPERTY_PASSWORD = "framework.admin.password";
	

	private String generateTmpFileName() {
		Random r = new Random();
		return Long.toString(Math.abs(r.nextLong()), 36);
	}

	public Export()
	{
	    try
	    {
	    	USER_ID = PropertyReader.getProperty(PROPERTY_USER_ID);
	    	PASSWORD =  PropertyReader.getProperty(PROPERTY_PASSWORD);
	    	COMPONENTS_NS = PropertyReader.getProperty("xsd.soap.item.components");
	    	MDRECORDS_NS = PropertyReader.getProperty("xsd.soap.common.mdrecords");
	    	FILE_NS = PropertyReader.getProperty("xsd.metadata.file");
	    	DCTERMS_NS = PropertyReader.getProperty("xsd.metadata.dcterms");
	    	DC_NS = PropertyReader.getProperty("xsd.metadata.dc");
	    	PROPERTIES_NS = PropertyReader.getProperty("xsd.soap.common.prop");
	    }
	    catch (Exception e) {
            logger.error("Error getting properties", e);
        }
	}
	

	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#explainFormatsXML()
	 */
	public String explainFormatsXML() throws ExportManagerException 
	{
		String citStyles;
		try {
			citStyles = new ProcessCitationStyles().explainStyles();
		} catch (CitationStyleManagerException e) {
			throw new ExportManagerException(e);
		}

		String structured;
		try {
			structured = new StructuredExport().explainFormats();
		} catch (StructuredExportManagerException e) {
			throw new ExportManagerException(e);
		}

		String result;
		// get export-format elements
		String regexp = "<export-formats.*?>(.*?)</export-formats>";
		Matcher m = Pattern
			.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
			.matcher(citStyles);
		m.find();
		result = m.group(1);
		m = Pattern
			.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
			.matcher(structured);
		m.find();
		result += m.group(1);
		
		m = Pattern
			.compile(
					"<export-format\\s+.*</export-format>"
					, Pattern.CASE_INSENSITIVE | Pattern.DOTALL
			)
			.matcher(structured);
		m.find();
		result = m.replaceAll(result);
		
		
		// replace comments
//		m = Pattern
//			.compile(
//					"<!--.*?-->"
//					, Pattern.CASE_INSENSITIVE | Pattern.DOTALL
//			)
//			.matcher(result);
//		m.find();
//		result = m.replaceAll("");
		
        return result;
	}


	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#getOutput(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	
	public byte[] getOutput(String exportFormat, String outputFormat, 
			String archiveFormat, String itemList)
	throws ExportManagerException, IOException 
	{
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		
		getOutputBase(exportFormat, outputFormat, archiveFormat, itemList, bos);
		
		bos.close();

		return baos.toByteArray();
		
	}
	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#getOutputFile(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public File getOutputFile(String exportFormat, String outputFormat, 
			String archiveFormat, String itemList)
	throws ExportManagerException, IOException 
	{
		FileOutputStream fos = null;
		File tmpFile = File.createTempFile(generateTmpFileName(), "." +	getFileExt(archiveFormat)); 
		try {
			fos = new FileOutputStream(tmpFile);
		} catch (FileNotFoundException e1) {
			throw new ExportManagerException("Cannot create temp file", e1);			
		}
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
		getOutputBase(exportFormat, outputFormat, archiveFormat, itemList, bos);
		
		bos.close();
		fos.close();
		
		return tmpFile; 
	}		

	
	/**
	 * Base method for getOutput* methods
	 */
	private void getOutputBase(String exportFormat, String outputFormat, 
			String archiveFormat, String itemList, BufferedOutputStream bos)
	throws ExportManagerException, IOException 
	{
		//
		Utils.checkCondition(!Utils.checkVal(itemList), "Empty item list");
		
		ExportFormatTypes exportFormatType = exportFormatType(exportFormat);
		Utils.checkCondition( exportFormatType == null, "Export format is not defined:" + exportFormat);
		
		boolean generateArchive = false;
		if ( Utils.checkVal(archiveFormat) )
		{
			try 
			{
				ArchiveFormats.valueOf(archiveFormat);
				generateArchive = true;
			}
			catch (Exception e) 
			{
				throw new ExportManagerException( "Archive format: " + archiveFormat + " is not supported" );
			}
			
		}
			
		byte[] ba = null;
		if ( exportFormatType == ExportFormatTypes.STRUCTURED )
		{
			StructuredExport se = new StructuredExport();
			try 
			{
				ba = se.getOutput(itemList, exportFormat);
			} 
			catch (Exception e) 
			{
				throw new ExportManagerException(e);			
			}
			// archived version has been asked
			// for the moment only for CSV format !
		}
		else if ( exportFormatType == ExportFormatTypes.LAYOUT )
		{
			ProcessCitationStyles pcs = new ProcessCitationStyles();
			try 
			{
				ba = pcs.getOutput(exportFormat, outputFormat, itemList);
			} 
			catch (Exception e) 
			{
				throw new ExportManagerException(e);			
			}
			
		}

		// generate archive
		if ( generateArchive )
		{
			//only CSV for the moment
			if ("CSV".equals(exportFormat))
				generateArchiveBase(exportFormat, archiveFormat, ba, itemList, null, bos);
		}
		else
		{
			ByteArrayInputStream bais = new ByteArrayInputStream (ba);
			BufferedInputStream bis = new BufferedInputStream(bais);
			
			writeFromStreamToStream(bis, bos);
			
			bis.close();
			bais.close();
		}

	}		
	 
	/**
	 * Returns file extension on hand of archive format 
	 * @param af is archive format
	 * @return file extension
	 */
	private String getFileExt(String af)
	{
		return af.equals(ArchiveFormats.gzip.toString()) ? "tar.gz" : af;
	}
	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#generateArchive(String, String, byte[], String) 
	) 
	 */
	public File generateArchiveFile(String exportFormat, String archiveFormat,
			 byte[] exportOut, String itemList) throws ExportManagerException, IOException {
		
		FileOutputStream fos = null;
			
		File tmpFile = File.createTempFile(generateTmpFileName(), "." +	getFileExt(archiveFormat)); 
		try {
			fos = new FileOutputStream(tmpFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
		generateArchiveBase(exportFormat, archiveFormat, exportOut, itemList, null, bos);
		
		bos.close();
		fos.close();
		
		return tmpFile;
	}
	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#generateArchive(String, String, byte[], String, File) 
	) 
	 */
	public File generateArchiveFile(String exportFormat, String archiveFormat,
			byte[] exportOut, String itemList, File license) throws ExportManagerException, IOException {
		
		FileOutputStream fos = null;
		
		File tmpFile = File.createTempFile(generateTmpFileName(), "." +	getFileExt(archiveFormat)); 
		try {
			fos = new FileOutputStream(tmpFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
		generateArchiveBase(exportFormat, archiveFormat, exportOut, itemList, license, bos);
		
		bos.close();
		fos.close();
		
		return tmpFile;
	}
	

	
	private void addDescriptionEnrty(byte[] exportOut, String exportFormat, OutputStream aos) throws IOException, ExportManagerException
	{
		if (exportOut == null || exportOut.length == 0)
			return;
		
		Utils.checkCondition(aos == null, "Archive OutputStream is null");
		
		Utils.checkCondition(!Utils.checkVal(exportFormat), "Empty export format");
		String entryName =  "eSciDoc_export." + exportFormat.toLowerCase(); 
		 
		BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(exportOut));
		if ( aos instanceof ZipOutputStream )
		{
			ZipEntry ze = new ZipEntry(entryName);
			ze.setSize(exportOut.length);
			((ZipOutputStream)aos).putNextEntry(ze);
			writeFromStreamToStream(bis, aos);
			((ZipOutputStream)aos).closeEntry();
			bis.close();
		}
		else if ( aos instanceof TarOutputStream )
		{
			TarEntry te = new TarEntry(entryName);
			te.setSize(exportOut.length);
			((TarOutputStream)aos).putNextEntry(te);
			writeFromStreamToStream(bis, aos);
			((TarOutputStream)aos).closeEntry();
			bis.close();
		}
		else
		{
			throw new ExportManagerException("Wrong archive OutputStream: " + aos);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#generateArchive(String, String, byte[], String) 
	) 
	 */
	public byte[] generateArchive(String exportFormat, String archiveFormat,
			byte[] exportOut, String itemList) throws ExportManagerException, IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		
		generateArchiveBase(exportFormat, archiveFormat, exportOut, itemList, null, bos);
		
		bos.close();
		
		return baos.toByteArray();
	}
	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#generateArchive(String, String, byte[], String, File) 
	) 
	 */
	public byte[] generateArchive(String exportFormat, String archiveFormat,
			byte[] description, String itemListFiltered, File license)
			throws ExportManagerException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		
		generateArchiveBase(exportFormat, archiveFormat, description, itemListFiltered, license, bos);
		
		bos.close();
		
		return baos.toByteArray();
	}

	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#generateArchive(String, String) 
	) 
	 */
	public byte[] generateArchive(String archiveFormat, String itemListFiltered)
	throws ExportManagerException, IOException 
	{
		return generateArchive(null, archiveFormat, null, itemListFiltered);
	}	
	
	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#generateArchive(String, String, File) 
	) 
	 */
	public byte[] generateArchive(String archiveFormat,
			String itemListFiltered, File license)
			throws ExportManagerException, IOException {
		return generateArchive(null, archiveFormat, null, itemListFiltered, license);
	}
	

	
	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#generateArchive(String, String, byte[], String) 
	) 
	 */
	private void generateArchiveBase(String exportFormat, String archiveFormat,
			 byte[] exportOut, String itemList, File license, 
			 BufferedOutputStream bos) throws ExportManagerException, IOException {

		Utils.checkCondition(
				!Utils.checkVal(exportFormat) 
				&& !(exportOut == null || exportOut.length==0), 
				"Empty export format"
		);
		
		Utils.checkCondition(!Utils.checkVal(itemList), "Empty item list");

		Utils.checkCondition(!Utils.checkVal(archiveFormat), "Empty archive format");
	
	
		switch ( ArchiveFormats.valueOf(archiveFormat) ) 
		{
			case zip:
				try 
				{
					ZipOutputStream zos = new ZipOutputStream(bos);
					
					//add export result entry
					addDescriptionEnrty(exportOut, exportFormat, zos);
					
					//add LICENSE AGREEMENT entry
					addLicenseAgreement(license, zos);
					
					//add files to the zip
					fetchComponentsDo(zos, itemList);
					
					zos.close();
				} 
				catch (IOException e) 
				{
					throw new ExportManagerException(e);
				}
				break;
			case tar:
				try 
				{
					TarOutputStream tos = new TarOutputStream(bos);
					
					//add export result entry
					addDescriptionEnrty(exportOut, exportFormat, tos);
					 
					//add LICENSE AGREEMENT entry
					addLicenseAgreement(license, tos);
					
					//add files to the tar
					fetchComponentsDo(tos, itemList); 
//			        logger.info("heapSize after  = " + Runtime.getRuntime().totalMemory());
//			        logger.info("heapFreeSize after = " + Runtime.getRuntime().freeMemory());
					
					tos.close();
					
				} 
				catch (IOException e) 
				{
					throw new ExportManagerException(e);
				}
				break;
			case gzip:
				try 
				{
					long ilfs = calculateItemListFileSizes(itemList); 
					long mem = Runtime.getRuntime().freeMemory()/2;
					if ( ilfs > mem) 
					{
						logger.info("Generate tar.gz output in tmp file: files' size = " + ilfs + " > Runtime.getRuntime().freeMemory()/2: " + mem);
						File tar = generateArchiveFile(exportFormat, ArchiveFormats.tar.toString(), exportOut, itemList);
						BufferedInputStream bis = new BufferedInputStream(new FileInputStream(tar));
						GZIPOutputStream gzos = new GZIPOutputStream(bos); 
						writeFromStreamToStream(bis, gzos);
						bis.close();
						gzos.close();
						tar.delete();
					}
					else
					{
						byte[] tar = generateArchive(exportFormat, ArchiveFormats.tar.toString(), exportOut, itemList);
						BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(tar));
						GZIPOutputStream gzos = new GZIPOutputStream(bos); 
						writeFromStreamToStream(bis, gzos);
						bis.close();
						gzos.close();
					}
					
					
				} 
				catch (IOException e) 
				{
					throw new ExportManagerException(e);
				}
				break;
			default: 	
				throw new ExportManagerException (
						"Archive format " + archiveFormat + " is not supported");
		}
		
		
	}	

	/**
	 * Write License Agreement file to the archive OutputStream
	 * @param af is Archive Format
	 * @param os - archive OutputStream
	 * @throws IOException
	 * @throws ExportManagerException 
	 */
	private void addLicenseAgreement(File license, OutputStream os) throws IOException, ExportManagerException 
	{

		if (license != null) 
		{
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(license));
			if (os instanceof TarOutputStream)
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(baos);
				writeFromStreamToStream(bis, baos);
				bis.close();
				byte[] file = baos.toByteArray();
				bos.close();
				TarEntry te = new TarEntry(license.getName());
				te.setSize(file.length);
				TarOutputStream tos = (TarOutputStream)os;
				tos.putNextEntry(te);
				tos.write(file);
				tos.closeEntry();
			} 
			else if (os instanceof ZipOutputStream)
			{
				ZipEntry ze = new ZipEntry(license.getName());
				ZipOutputStream zos = (ZipOutputStream)os;
				zos.putNextEntry(ze);
				writeFromStreamToStream(bis, zos);
				bis.close();
				zos.closeEntry();
			}
			else
			{
				throw new ExportManagerException("Wrong archive OutputStream: " + os);
			}
		}
	}

	/**
	 * Parses <code>itemList</code> XML to <code>org.w3c.dom.Document</code>.
	 * @param itemList
	 * @return <code>org.w3c.dom.Document</code>
	 * @throws ExportManagerException
	 */
	private Document parseDocument(String itemList) throws ExportManagerException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder parser;
		try {
			parser = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ExportManagerException("Cannot create DocumentBuilder:", e);
		}

		// Check for the traversal module
		DOMImplementation impl = parser.getDOMImplementation();
		if (!impl.hasFeature("traversal", "2.0")) 
		{
			throw new ExportManagerException ("A DOM implementation that supports traversal is required.");
		}
		Document doc;
		try {
			doc = parser.parse(new ByteArrayInputStream(itemList.getBytes()));
		} catch (Exception e) {
			throw new ExportManagerException ("Cannot parse itemList to w3c document");
		}
		return doc;
	}

	
	/**
	 * Returns <code>org.w3c.dom.traversal.NodeIterator</code> for org.w3c.dom.Document traversing
	 *  
	 * @throws ExportManagerException
	 */
	private NodeIterator getFilteredNodes(NodeFilter nodeFilter, Document doc) throws ExportManagerException  
	{
		NodeIterator ni = ((DocumentTraversal) doc).createNodeIterator(
				doc.getDocumentElement(), 
				NodeFilter.SHOW_ELEMENT,
				nodeFilter, 
				true
		);	
		return ni;
	} 

	/* (non-Javadoc) 
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#calculateItemListFileSizes(String)
	 */
	public long calculateItemListFileSizes(String itemList) throws ExportManagerException 
	{
		  Document doc = parseDocument(itemList);	 
	      NodeIterator ni = getFilteredNodes(new FileSizeNodeFilter(), doc); 
	      
	      long size = 0;
	      String stringSize;
	      Node n;
	      while ((n = ni.nextNode()) != null)
	      {
	    	  stringSize = ((Element) n).getTextContent();
	    	  size += Long.parseLong(stringSize);
	      }
	      
	      return size;
	}
	
	/**
	 * Walk around the itemList XML, fetch all files from components via URIs
	 *  and put them into the archive {@link OutputStream} aos  
	 * @param aos - array {@link OutputStream}
	 * @param itemList - XML with the files to be fetched, see NS: http://www.escidoc.de/schemas/components/0.7 
	 * @throws ExportManagerException
	 */
	private void fetchComponentsDo(OutputStream aos, String itemList) throws ExportManagerException {
		try 
		{
			  Document doc = parseDocument(itemList);	
		      NodeIterator ni = getFilteredNodes(new ComponentNodeFilter(), doc) ; 
		      
		      //login only once
		      String userHandle = AdminHelper.loginUser(USER_ID, PASSWORD);

		      String fileName;
		      Node n;
		      while ((n = ni.nextNode()) != null) 
		      {

		    	  Element componentElement = (Element) n;
		    	  NodeList nl = componentElement.getElementsByTagNameNS(COMPONENTS_NS, "content");
		    	  Element contentElement = (Element)nl.item( 0 );
		    	  if ( contentElement == null )
		    	  {
		    		  throw new ExportManagerException("Wrong item XML: {" 
		    				  + COMPONENTS_NS + "}component element doesn't contain content element. " 
		    				  + "Component id: " + componentElement.getAttributeNS(XLINK_NS, "href") 
		    		  );
		    	  }
		    	  String href = contentElement.getAttributeNS(XLINK_NS, "href");
		    	  String storageStatus = contentElement.getAttribute("storage");
		    	  
		    	  // get file name
		    	  if ("internal-managed".equals(storageStatus))
		    	  {
		    		  NodeIterator nif = ((DocumentTraversal)doc).createNodeIterator(
		    		    		  componentElement, 
		    		    		  NodeFilter.SHOW_ELEMENT,
		    		    		  new FileNameNodeFilter(), 
		    		    		  true
		    		      );
		    		  Node nf;

		    		  if ((nf = nif.nextNode()) != null) 
		    		  {
		    			  fileName = ((Element) nf).getTextContent();

		    			  // names of files for
		    			  Matcher m = Pattern.compile("^([\\w.]+?)(\\s+|$)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(fileName);
		    			  m.find();
		    			  fileName = m.group(1);
		    		  }
		    		  else 
		    		  {
			    		  throw new ExportManagerException("Missed file property: {" 
			    				  + COMPONENTS_NS + "}component element doesn't contain file-name element (md-records/md-record/file:file/dc:title). " 
			    				  + "Component id: " + componentElement.getAttributeNS(XLINK_NS, "href")
			    		  ); 
		    		  }
		    	  }
		    	  // TODO: the external-managed will be processed later
		    	  else
		    	  {
		    		  throw new ExportManagerException("Missed internal-managed file in {" 
		    				  + COMPONENTS_NS + "}component: components/component/content[@storage=\"internal-managed\"]" 
		    				  + "Component id: " + componentElement.getAttributeNS(XLINK_NS, "href")
		    		  ); 
		    	  }
		    		  
		    	  logger.info("link to the content: " + href);
		    	  logger.info("storage status: " + storageStatus);
		    	  logger.info("fileName: " + fileName);

		    	  // get file via URI
		          String url = ServiceLocator.getFrameworkUrl() + href;
		          logger.info("url=" + url);
		          GetMethod method = new GetMethod(url);
		            
		          method.setFollowRedirects(false);
		          method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);            
		    
		          // Execute the method with HttpClient.
		          HttpClient client = new HttpClient();
		          ProxyHelper.executeMethod(client, method);
		          
		          int status = method.getStatusCode();
		          logger.info("Status=" + status);
		          		          
		          if ( status != 200 )
		        	  fileName += ".error" + status;
		          
		          byte[] responseBody = method.getResponseBody();
		          InputStream bis = new BufferedInputStream(new ByteArrayInputStream(responseBody));
		          
		          
		          
		    	  if ( aos instanceof ZipOutputStream )
		    	  {
		    		  ZipEntry ze = new ZipEntry( fileName );
					  ze.setSize(responseBody.length);
		    		  ((ZipOutputStream)aos).putNextEntry(ze);
		    		  writeFromStreamToStream(bis, aos);
		    		  ((ZipOutputStream)aos).closeEntry();
		    	  }
		    	  else if ( aos instanceof TarOutputStream )
		    	  {
		    		  TarEntry te = new TarEntry( fileName );
					  te.setSize(responseBody.length);
		    		  ((TarOutputStream)aos).putNextEntry(te);
		    		  writeFromStreamToStream(bis, aos);
		    		  ((TarOutputStream)aos).closeEntry();
		    	  }
		    	  else
		    	  {
			    	  throw new ExportManagerException ("Unsupported archive output stream: " + aos.getClass());
		    	  }
		    	  bis.close();
		      }
		}
		catch (Exception e)
		{
		    throw new ExportManagerException (e);
		}

	}

	private void writeFromStreamToStream(InputStream is, OutputStream os) throws IOException
	{
		byte[] data = new byte[BUFFER_SIZE];
		int byteCount;
		while ((byteCount = is.read(data)) > -1){
			os.write(data, 0, byteCount);
		} 
	}

	/**
	 * Returns type of the <code>exportFormat</code>: 
	 * <code>LAYOUT</code> for citation style 
	 * and <code>STRUCTURED</code> for structured export formats. 
	 * @param exportFormat
	 * @return type of the export formats
	 * @throws ExportManagerException
	 */
	private ExportFormatTypes exportFormatType(String exportFormat) throws ExportManagerException 
	{
		if ( exportFormat == null || exportFormat.trim().equals("") )
		{
			throw new ExportManagerException("Empty export format");
		}
		try 
		{
			if ( new ProcessCitationStyles().isCitationStyle(exportFormat) )
			{
				return ExportFormatTypes.LAYOUT;
			}	
			else if ( new StructuredExport().isStructuredFormat(exportFormat) ) 
			{
				return ExportFormatTypes.STRUCTURED;
			}
		} 
		catch (Exception e) 
		{
			throw new ExportManagerException(e);
		}
			
		return null;
		
	}



	
	// NodeFilters for XML Traversing 
	class FileNameNodeFilter implements NodeFilter {
		
		public short acceptNode(Node n) {
			Element e = (Element) n;
			Element parent = (Element)e.getParentNode();
			boolean parentIsFile = parent != null && FILE_NS.equals(parent.getNamespaceURI()) && "file".equals(parent.getLocalName());
			if (
					parentIsFile && 
					DC_NS.equals(e.getNamespaceURI()) && 
					(
							"title".equals(e.getLocalName()) 
					)
			)		
			{
				return FILTER_ACCEPT;
			}
			return FILTER_SKIP;
		}
	}
	
	
	class ComponentNodeFilter implements NodeFilter {


		public short acceptNode(Node n) {
			Element e = (Element) n;
			//System.out.println(e.getNodeName());
			
			try
			{
	    		
	    		if (COMPONENTS_NS.equals(e.getNamespaceURI()) && "component".equals(e.getLocalName())) {
	    			return FILTER_ACCEPT;
	    		}
			}
			catch (Exception ex) {
	            throw new RuntimeException("Error evaluating export filter", ex);
	        }
			return FILTER_SKIP;
		}
	}


	class FileSizeNodeFilter implements NodeFilter {
			
			public short acceptNode(Node n) {
				Element e = (Element) n;
				if (
						DCTERMS_NS.equals(e.getNamespaceURI()) &&   
						"extent".equals(e.getLocalName()) 
				)		
				{
					return FILTER_ACCEPT; 
				}
				return FILTER_SKIP;
			}
			
	}



}






