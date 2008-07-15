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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

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
import de.mpg.escidoc.services.structuredexportmanager.StructuredExport;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportManagerException;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportXSLTNotFoundException;

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
	public static final String XLINK_NS = "http://www.w3.org/1999/xlink";
	public static final String PROPERTIES_NS = "http://escidoc.de/core/01/properties/";
	
	
	
	public static final short BUFFER_SIZE = 1024;
	

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
	 * @see de.mpg.escidoc.services.exportmanager.ExportHandler#getOutput(java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String)
	 */
	public byte[] getOutput(String exportFormat, String outputFormat,
			String archiveFormat, boolean fetchComponents, String itemList)
	throws ExportManagerException 
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
			
		byte[] out = null; 
		if ( exportFormatType == ExportFormatTypes.STRUCTURED )
		{
			StructuredExport se = new StructuredExport();
			try 
			{
				out = se.getOutput(itemList, exportFormat);
			} 
			catch (Exception e) 
			{
				throw new ExportManagerException(e);			
			}
			// archived version has been asked
			// for the moment only for CSV format !
			if ( "CSV".equals(exportFormat)  
				 && ( 
						 ArchiveFormats.valueOf(archiveFormat) == ArchiveFormats.zip
						 || ArchiveFormats.valueOf(archiveFormat) == ArchiveFormats.gzip
					)	 
			)
			{
				out = generateArchive(exportFormat, archiveFormat, fetchComponents, out, itemList);
			}
			
		}
		else if ( exportFormatType == ExportFormatTypes.LAYOUT )
		{
			ProcessCitationStyles pcs = new ProcessCitationStyles();
			try 
			{
				out = pcs.getOutput(exportFormat, outputFormat, itemList);
			} 
			catch (Exception e) 
			{
				throw new ExportManagerException(e);			
			}
			
		}

		return out;
	}	
	
	
	private byte[] generateArchive(String exportFormat, String archiveFormat,
			boolean fetchComponents, byte[] out, String itemList) throws ExportManagerException {

		if ( exportFormat == null || exportFormat.trim().equals("") )
		{
			throw new ExportManagerException("Empty export format");
		}
		if ( itemList == null || itemList.trim().equals("") )
		{
			throw new ExportManagerException("Empty item list");
		}
		if ( out == null )
		{
			throw new ExportManagerException("Empty output");
		}
		if ( archiveFormat == null || archiveFormat.trim().equals("") )
		{
			throw new ExportManagerException("Empty archive format");
		}
		
		
		BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(out));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		
		switch ( ArchiveFormats.valueOf(archiveFormat) ) 
		{
			case zip:
				try 
				{
					ZipOutputStream zos = new ZipOutputStream(bos);
					ZipEntry ze = new ZipEntry( "eSciDoc_export." + exportFormat.toLowerCase());
					ze.setSize(out.length);
					zos.putNextEntry(ze);
					writeFromStreamToStream(bis, zos);
					zos.closeEntry();
					bis.close();
					if ( fetchComponents )
					{
						fetchComponentsDo(zos, itemList); 
					}
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
					TarEntry te = new TarEntry( "eSciDoc_export." + exportFormat.toLowerCase());
					te.setSize(out.length);
					tos.putNextEntry(te);
					writeFromStreamToStream(bis, tos);
					tos.closeEntry();
					bis.close();
					if ( fetchComponents )
					{
						fetchComponentsDo(tos, itemList); 
					}
					tos.close();
					
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
		return baos.toByteArray();
	}


	private void fetchComponentsDo(OutputStream aos, String itemList) throws ExportManagerException {
		// обойти все components и загрузить файлы
		int i = 1;
		try 
		{
		      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		      dbf.setNamespaceAware(true);
		      DocumentBuilder parser = dbf.newDocumentBuilder();

		      // Check for the traversal module
		      DOMImplementation impl = parser.getDOMImplementation();
		      if (!impl.hasFeature("traversal", "2.0")) 
		      {
		    	  throw new ExportManagerException ("A DOM implementation that supports traversal is required.");
		      }
		      Document document = parser.parse(new ByteArrayInputStream(itemList.getBytes()));
		      DocumentTraversal traversable = (DocumentTraversal) document;
		      NodeIterator ni = traversable.createNodeIterator(
		    		  document.getDocumentElement(), 
		    		  NodeFilter.SHOW_ELEMENT,
		    		  new FormattingNodeFilter(), 
		    		  true
		      );
		      Node n;
		      while ((n = ni.nextNode()) != null) {
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
		    	  
		    	  // get mime type
		    	  String mimeType = null;
		    	  String ext = "";
		    	  if ("internal-managed".equals(storageStatus))
		    	  {
		    		  nl = componentElement.getElementsByTagNameNS(COMPONENTS_NS, "properties");
		    		  Element propertiesElement = (Element)nl.item( 0 );
		    		  nl = propertiesElement.getElementsByTagNameNS(PROPERTIES_NS, "mime-type");
		    		  Element mimeTypeElement = (Element)nl.item( 0 );
		    		  mimeType = mimeTypeElement.getTextContent();
		    		  // set file name extension according to the mime-type
		    		  if ("application/pdf".equals(mimeType))
		    		  {
		    			  ext = ".pdf";
		    		  }
		    	  }
		    	  
		    	  
		    	  logger.info("link to the content: " + href);
		    	  logger.info("storage status : " + storageStatus);
		    	  
		    	  if ( aos instanceof ZipOutputStream )
		    	  {
		    		  ZipEntry ze = new ZipEntry( i++ + "test_name" + ext );
					  ze.setSize(href.length());
		    		  ((ZipOutputStream)aos).putNextEntry(ze);
		    		  // here we should take file according to the href
						writeFromStreamToStream(
								new BufferedInputStream(
										new ByteArrayInputStream(
												
								// here should be put the content of fetched file !!! 
												href.getBytes()
												
										) 
								), 
								aos
						);
		    		  ((ZipOutputStream)aos).closeEntry();
		    	  }
		    	  else if ( aos instanceof TarOutputStream )
		    	  {
		    		  TarEntry te = new TarEntry( i++ + "test_name" + ext );
					  te.setSize(href.length());
		    		  ((TarOutputStream)aos).putNextEntry(te);
						writeFromStreamToStream(
								new BufferedInputStream(
										new ByteArrayInputStream(
												
								// here should be put the content of fetched file !!! 
												href.getBytes()
												
										) 
								), 
								aos
						);
		    		  ((TarOutputStream)aos).closeEntry();
		    	  }
		    	  else
		    	  {
			    	  throw new ExportManagerException ("Unsupported archive output stream: " + aos.getClass());
		    	  }
		      }
		}
		catch (Exception e) {
		    	  throw new ExportManagerException (e);
		      }
	}

	private void writeFromStreamToStream(InputStream is, OutputStream os) throws IOException
	{
		byte[] data = new byte[1024];
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


	public static void main(String args[]) throws ExportManagerException, IOException
	{
		Export exp = new Export();
//		logger.info(" result: " + exp.explainFormatsXML());
		FileOutputStream fos = new FileOutputStream("file.zip");
		fos.write(
				exp.generateArchive( "CSV", "zip", true, new String("Tut csv fail").getBytes(), 
						readFile("src/test/resources/item.xml", "UTF-8")		
				)
		);				
		fos.close();
//		FileOutputStream fos = new FileOutputStream("file.tar");
//		fos.write(
//				exp.generateArchive( "CSV", "tar", true, new String("Tut csv fail").getBytes(), 
//						readFile("src/test/resources/item.xml", "UTF-8")		
//				)
//		);				
//		fos.close();
		
	}
	
    /**
     * Reads contents from text file and returns it as String.
     *
     * @param fileName Name of input file
     * @return Entire contents of filename as a String
     */
    public static String readFile(final String fileName, String enc)
    {
        boolean isFileNameNull = (fileName == null);
        StringBuffer fileBuffer;
        String fileString = null;
        String line;
        if (!isFileNameNull)
        {
            try
            {
//                InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
                InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), enc);
                BufferedReader br = new BufferedReader(isr);
                fileBuffer = new StringBuffer();
                while ((line = br.readLine()) != null)
                {
                    fileBuffer.append(line + "\n");
                }
                isr.close();
                fileString = fileBuffer.toString();
            }
            catch (IOException e)
            {
                return null;
            }
        }
        return fileString;
    }
	
	
}

class FormattingNodeFilter implements NodeFilter {
	
	public static final String COMPONENTS_NS = "http://www.escidoc.de/schemas/components/0.7";

	public short acceptNode(Node n) {
		Element e = (Element) n;
		if (e.getNamespaceURI().equals(COMPONENTS_NS) && e.getLocalName().equals("component")) {
			return FILTER_ACCEPT;
		}
		return FILTER_SKIP;
	}
}