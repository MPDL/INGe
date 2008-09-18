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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
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
import java.net.URISyntaxException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
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
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExport;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportManagerException;
 
/**
 * Structured Export Manager. 
 * Converts PubMan item-list to one of the structured formats.   
 *
 * @author Vlad Makarenko (initial creation)
 * @author $Author: vdm $ (last modification)
 * @version $Revision: 67 $ $LastChangedDate: 2007-12-11 12:39:50 +0100 (Tue, 11 Dec 2007) $
 *
 */ 

public class Export implements ExportHandler {

 
	private final static Logger logger = Logger.getLogger(Export.class);
	
	public static enum ArchiveFormats { zip, tar, gzip };
	
	public static enum ExportFormatTypes { STRUCTURED, LAYOUT };
	
	public static final String COMPONENTS_NS = "http://www.escidoc.de/schemas/components/0.7";
	public static final String MDRECORDS_NS = "http://www.escidoc.de/schemas/metadatarecords/0.4";
	public static final String XLINK_NS = "http://www.w3.org/1999/xlink";
	public static final String PROPERTIES_NS = "http://escidoc.de/core/01/properties/";
	public static final String LICENSE_AGREEMENT_NAME = "Faces_Release_Agreement_Export.pdf";
	private final static String PATH_TO_RESOURCES = "resources/";
	
	
	
	public static final short BUFFER_SIZE = 1024;
	private static final int NUMBER_OF_URL_TOKENS = 2;
	private static final String USER_ID = "roland";
	private static final String PASSWORD = "beethoven";
	
	

	private String generateTmpFileName() {
		Random r = new Random();
		return Long.toString(Math.abs(r.nextLong()), 36);
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
		if ( itemList == null || itemList.trim().equals("") )
		{
			throw new ExportManagerException("Empty item list");
		}
		
		ExportFormatTypes exportFormatType = exportFormatType(exportFormat);
		
		if ( exportFormatType  == null )
		{
			throw new ExportManagerException("Export format is not defined:" + exportFormat);
		}
		
		boolean generateArchive = false;
		if ( archiveFormat != null && !"".equals(archiveFormat.trim()) )
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
				generateArchiveBase(exportFormat, archiveFormat, ba, itemList, bos);
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
		
		generateArchiveBase(exportFormat, archiveFormat, exportOut, itemList, bos);
		
		bos.close();
		fos.close();
		
		return tmpFile;
	}
	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#generateArchive(String, String, byte[], String) 
	) 
	 */
	public byte[] generateArchive(String exportFormat, String archiveFormat,
			byte[] exportOut, String itemList) throws ExportManagerException, IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		
		generateArchiveBase(exportFormat, archiveFormat, exportOut, itemList, bos);
		
		bos.close();
		
		return baos.toByteArray();
	}

	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#generateArchive(String, String, byte[], String) 
	) 
	 */
	private void generateArchiveBase(String exportFormat, String archiveFormat,
			 byte[] exportOut, String itemList, BufferedOutputStream bos) throws ExportManagerException, IOException {

		
		if ( exportFormat == null || exportFormat.trim().equals("") )
		{
			throw new ExportManagerException("Empty export format");
		}
		if ( itemList == null || itemList.trim().equals("") )
		{
			throw new ExportManagerException("Empty item list");
		}
		if ( exportOut == null )
		{
			throw new ExportManagerException("Empty output");
		}
		if ( archiveFormat == null || archiveFormat.trim().equals("") )
		{
			throw new ExportManagerException("Empty archive format");
		}
		
		BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(exportOut));
		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		switch ( ArchiveFormats.valueOf(archiveFormat) ) 
		{
			case zip:
				try 
				{
					ZipOutputStream zos = new ZipOutputStream(bos);
					//add export result entry
					ZipEntry ze = new ZipEntry( "eSciDoc_export." + exportFormat.toLowerCase());
					ze.setSize(exportOut.length);
					zos.putNextEntry(ze);
					writeFromStreamToStream(bis, zos);
					zos.closeEntry();
					bis.close();
					
					//add LICENSE AGREEMENT entry
					addLicenseAgreement(zos);
					
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
					TarEntry te = new TarEntry( "eSciDoc_export." + exportFormat.toLowerCase());
					te.setSize(exportOut.length);
					tos.putNextEntry(te);
					writeFromStreamToStream(bis, tos);
					tos.closeEntry();
					bis.close();
					 
					//add LICENSE AGREEMENT entry
					addLicenseAgreement(tos);
					
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
						bis = new BufferedInputStream(new FileInputStream(tar));
						GZIPOutputStream gzos = new GZIPOutputStream(bos); 
						writeFromStreamToStream(bis, gzos);
						bis.close();
						gzos.close();
						tar.delete();
					}
					else
					{
						byte[] tar = generateArchive(exportFormat, ArchiveFormats.tar.toString(), exportOut, itemList);
						bis = new BufferedInputStream(new ByteArrayInputStream(tar));
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
	 */
	private void addLicenseAgreement(OutputStream os) throws IOException 
	{
		BufferedInputStream bis = new BufferedInputStream(getResource(LICENSE_AGREEMENT_NAME));
		if (os instanceof TarOutputStream)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(baos);
			writeFromStreamToStream(bis, baos);
			bis.close();
			byte[] file = baos.toByteArray();
			bos.close();
			     
			TarEntry te = new TarEntry(LICENSE_AGREEMENT_NAME);
			te.setSize(file.length);
			TarOutputStream tos = (TarOutputStream)os;
			tos.putNextEntry(te);
			tos.write(file);
			tos.closeEntry();
		} 
		else if (os instanceof ZipOutputStream)
		{
			ZipEntry ze = new ZipEntry(LICENSE_AGREEMENT_NAME);
			ZipOutputStream zos = (ZipOutputStream)os;
			zos.putNextEntry(ze);
			writeFromStreamToStream(bis, zos);
			bis.close();
			zos.closeEntry();
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
		      String userHandle = loginUser(USER_ID, PASSWORD);

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
		          client.executeMethod(method);
		          
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
		catch (Exception e) {
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

    /**
     * Logs in the given user with the given password.
     * 
     * @param userid The id of the user to log in.
     * @param password The password of the user to log in.
     * @return The handle for the logged in user.
     * @throws HttpException
     * @throws IOException
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    protected static String loginUser(String userid, String password) throws HttpException, IOException, ServiceException, URISyntaxException
    {
    	String frameworkUrl = ServiceLocator.getFrameworkUrl();
    	StringTokenizer tokens = new StringTokenizer( frameworkUrl, "//" );
    	if( tokens.countTokens() != NUMBER_OF_URL_TOKENS ) {
    		throw new IOException( "Url in the config file is in the wrong format, needs to be http://<host>:<port>" );
    	}
    	tokens.nextToken();
    	StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");

    	if( hostPort.countTokens() != NUMBER_OF_URL_TOKENS ) {
    		throw new IOException( "Url in the config file is in the wrong format, needs to be http://<host>:<port>" );
    	}
    	String host = hostPort.nextToken();
    	int port = Integer.parseInt( hostPort.nextToken() );

    	HttpClient client = new HttpClient();

    	client.getHostConfiguration().setHost( host, port, "http");
    	client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

    	PostMethod login = new PostMethod( frameworkUrl + "/aa/j_spring_security_check");
    	login.addParameter("j_username", userid);
    	login.addParameter("j_password", password);

    	client.executeMethod(login);

    	login.releaseConnection();
    	CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
    	Cookie[] logoncookies = cookiespec.match(
    			host, port, "/", false, 
    			client.getState().getCookies());

    	Cookie sessionCookie = logoncookies[0];

    	PostMethod postMethod = new PostMethod("/aa/login");
    	postMethod.addParameter("target", frameworkUrl);
    	client.getState().addCookie(sessionCookie);
    	client.executeMethod(postMethod);

    	if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
    	{
    		throw new HttpException("Wrong status code: " + login.getStatusCode());
    	}

    	String userHandle = null;
    	Header headers[] = postMethod.getResponseHeaders();
    	for (int i = 0; i < headers.length; ++i)
    	{
    		if ("Location".equals(headers[i].getName()))
    		{
    			String location = headers[i].getValue();
    			int index = location.indexOf('=');
    			userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
    			//System.out.println("location: "+location);
    			//System.out.println("handle: "+userHandle);
    		}
    	}

    	if (userHandle == null)
    	{
    		throw new ServiceException("User not logged in.");
    	}
    	return userHandle;
    }
    
	/**
	 * Gets resources according to an execution environment
	 * @param fileName
	 * @return InputStream of resource
	 * @throws IOException
	 */
	private InputStream getResource(final String fileName) throws IOException
	{
		String path = PATH_TO_RESOURCES + fileName;
		InputStream fileIn = getClass()
								.getClassLoader()
								.getResourceAsStream(path);
		if (fileIn == null)
		{
			fileIn = new FileInputStream(path);
		}
		return fileIn;
	}
    

}

// NodeFilters for XML Traversing 
class ComponentNodeFilter implements NodeFilter {
	
	public static final String COMPONENTS_NS = "http://www.escidoc.de/schemas/components/0.7";

	public short acceptNode(Node n) {
		Element e = (Element) n;
		//System.out.println(e.getNodeName());
		if (COMPONENTS_NS.equals(e.getNamespaceURI()) && "component".equals(e.getLocalName())) {
//			System.out.println("component--->" + e.getNodeName());
			return FILTER_ACCEPT;
		}
		return FILTER_SKIP;
	}
}

class FileNameNodeFilter implements NodeFilter {
	
	public static final String FILE_NS = "http://escidoc.mpg.de/metadataprofile/schema/0.1/file";
	public static final String DC_NS = "http://purl.org/dc/elements/1.1/";

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

class FileSizeNodeFilter implements NodeFilter {
		
		public static final String DCTERMS_NS = "http://purl.org/dc/terms/";
		
		public short acceptNode(Node n) {
			Element e = (Element) n;
			if (
					DCTERMS_NS.equals(e.getNamespaceURI()) &&   
					"extent".equals(e.getLocalName()) 
			)		
			{
				//System.out.println("accepted: " + e.getLocalName() + ":" + e.getTextContent());
				return FILTER_ACCEPT; 
			}
			return FILTER_SKIP;
		}
		
}

