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

package de.mpg.escidoc.services.citationmanager.xslt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
*
* Citation Style Executor Engine, XSLT-centric    
*
* @author Initial creation: vmakarenko 
* @author $Author$ (last modification)
* @version $Revision$ $LastChangedDate$
*
*/

public class CitationStyleExecutor implements CitationStyleHandler{

    private static final String PARENT_ELEMENT_NAME = "content-model-specific";
    private static final String SNIPPET_ELEMENT_NAME = "dcterms:bibliographicCitation";
    private static final String SNIPPET_NS = "http://purl.org/dc/terms/";

	
    private static String pubManUrl = null;
    
	private static final Logger logger = Logger.getLogger(CitationStyleExecutor.class);	

//	private static ProcessCitationStyles pcs = new ProcessCitationStyles();

	
	/* 
	 * Explains citation styles and output types for them 
	 * @see de.mpg.escidoc.services.citationmanager.CitationStyleHandler#explainStyles()
	 */
	public String explainStyles() throws CitationStyleManagerException 
	{
		return ResourceUtil.getExplainStyles();
	}

	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.citationmanager.CitationStyleHandler#getOutputFormats(java.lang.String)
	 */	
	public String[] getOutputFormats(String cs) throws CitationStyleManagerException
	{
		return XmlHelper.getOutputFormatsArray(cs); 
	}
	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.citationmanager.CitationStyleHandler#getMimeType(java.lang.String, java.lang.String)
	 */	
	public String getMimeType(String cs, String ouf) throws CitationStyleManagerException
	{
		return XmlHelper.getMimeType(cs, ouf);
	}

	
	
	public byte[] getOutput(String cs, String outputFormat,
			String itemList) throws IOException, JRException,
			CitationStyleManagerException  {

		Utils.checkCondition( !Utils.checkVal(outputFormat), "Output format is not defined");
		
//		Utils.checkCondition( !"snippet".equals(ouputFormat), "The only snippet format is supported for the moment");
		
		Utils.checkCondition( !Utils.checkVal(itemList), "Empty item-list");
		
		
		if ( ! XmlHelper.citationStyleHasOutputFormat(cs, outputFormat) )
		{
			throw new CitationStyleManagerException( "Output format: " + outputFormat + " is not supported for Citation Style: " + cs );
		}		

		byte[] result;
		String snippet;
		long start; 
		Transformer transformer;
		try 
		{
			
			start = System.currentTimeMillis();
			
			StringWriter sw = new StringWriter();
			
			String csXslPath = ResourceUtil.getPathToCitationStyleXSL(cs); 
			
			/* get xslt from the templCache */
			transformer = XmlHelper.tryTemplCache(csXslPath).newTransformer();
			
			//set parameters
			transformer.setParameter("pubman_instance", getPubManUrl());
			
			transformer.transform(new StreamSource(new StringReader(itemList)), new StreamResult(sw));
			
			logger.info("Transformation item-list 2 snippet: " + (System.currentTimeMillis() - start));
			
			snippet = sw.toString(); 
			
			// new edoc md set
			if ("escidoc_snippet".equals(outputFormat))
			{
				result = snippet.getBytes("UTF-8");
			}
			// old edoc md set: back transformation
			else if ("snippet".equals(outputFormat))
			{
		    	 Format in = new Format("escidoc-publication-item-list-v2", "application/xml", "UTF-8");
		    	 Format out = new Format("escidoc-publication-item-list-v1", "application/xml", "UTF-8");
		    	 
		    	 TransformationBean trans = ResourceUtil.getTransformationBean();
		    	 
		    	 byte[] v1 = null;
		    	 try 
		    	 {
					v1 = trans.transform(snippet.getBytes("UTF-8"), in, out, "escidoc");
		    	 }
		    	 catch (Exception e) 
		    	 {
		    		 throw new CitationStyleManagerException("Problems by escidoc v2 to v1 transformation:", e);	
		    	 } 
				result = v1;
			}
			else if ("html_plain".equals(outputFormat) || "html_linked".equals(outputFormat))
			{
				result = generateHtmlOutput(snippet, outputFormat).getBytes("UTF-8");
			}
			else
			{

				start = System.currentTimeMillis();

				String jrds = generateJasperReportDataSource(cs, snippet);
				
				logger.info("Transformation snippet 2 JasperDS: " + (System.currentTimeMillis() - start));
				
				JasperReport jr = null;
				String csj = null;
				try 
				{
					start = System.currentTimeMillis();
					
					//get JasperReport from cache
					jr = XmlHelper.tryJasperCache(cs);
					
//					JRXmlWriter.writeReport(jr, ResourceUtil.getPathToCitationStyles() + "citation-style.jrxml", "UTF-8");
					
//					Document doc = JRXmlUtils.parse(new InputSource(new StringReader(jrds) ));
					Document doc = XmlHelper.createDocument(jrds);
//					Document doc = XmlHelper.createDocument(snippet);
					
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, doc);
					

					JasperPrint jasperPrint= JasperFillManager.fillReport(
							jr,
							params,
							new JRXmlDataSource(doc, jr.getQuery().getText())
					);
					
					
					logger.info("JasperFillManager.fillReportToStream : " + (System.currentTimeMillis() - start));
					
//					jasperPrint

//					JRXmlExporter jrxe = new JRXmlExporter();
//					
//					jrxe.setParameter(JRXmlExporterParameter.JASPER_PRINT, jasperPrint);
//					
//					jrxe.setParameter(JRXmlExporterParameter.OUTPUT_FILE_NAME, "Report.jrpxml");
//					
//					jrxe.exportReport();
					
					
					start = System.currentTimeMillis();

					JRExporter exporter = null;    
					
					if ("pdf".equals(outputFormat))
					{
						exporter = new JRPdfExporter();
					}
					else if ("html_styled".equals(outputFormat))
					{
						exporter = new JRHtmlExporter();
						/* Switch off pagination and null pixel alignment for JRHtmlExporter */
				        exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
				        exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
		                exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);						
					}
					else if ("rtf".equals(outputFormat))
					{
						exporter = new JRRtfExporter();
					}
					else if ("odt".equals(outputFormat))
					{
						exporter = new JROdtExporter();
					}
					else if ("txt".equals(outputFormat))
					{
						exporter = new JRTextExporter();    
				        exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(10));
				        exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(10));
				        exporter.setParameter(JRTextExporterParameter.CHARACTER_ENCODING, "UTF-8");
					}
					else 
						throw new CitationStyleManagerException (
								"Output format " + outputFormat + " is not supported");
					
					
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
					
					exporter.exportReport();
					
					result = baos.toByteArray();

					logger.info("export to " + outputFormat + ": " + (System.currentTimeMillis() - start));					
					
					
				} 
				catch (Exception e) 
				{
					throw new RuntimeException("Cannot load JasperReport: " + csj, e);
				}
				
				
			}
			
			
//			logger.info( "snippet: " + extractBibliographicCitation(snippet) );

		}	
		catch (Exception e) 
		{
	            throw new RuntimeException("Error by transformation:", e);
	    }
		//
		return result;
//		return XmlHelper.outputString(itemListDoc).getBytes("UTF-8");
		
	}
	

	public byte[] getOutput(String citationStyle, String itemList)
			throws IOException, JRException, CitationStyleManagerException {

		return getOutput(citationStyle, "snippet_esidoc", itemList);
	}	
	
	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.citationmanager.CitationStyleHandler#getStyles()
	 */	
	public String[] getStyles() throws CitationStyleManagerException
	{
		try {
			return XmlHelper.getListOfStyles();
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			throw new CitationStyleManagerException("Cannot get list of citation styles:", e);
		}
	}

	public boolean isCitationStyle(String cs)
			throws CitationStyleManagerException {
		return XmlHelper.isCitationStyle(cs);
	}

	/**
	 * Generates JasperReport DataSource
	 * @param snippets
	 * @return String 
	 */
	private String generateJasperReportDataSource (String cs, String snippets)
	{
		StringWriter result = new StringWriter();
		try 
		{
			Transformer transformer = XmlHelper.tryTemplCache(ResourceUtil.getPathToTransformations() + "escidoc-publication-snippet2jasper_DS.xsl").newTransformer();
			transformer.setParameter("cs", cs);
			transformer.transform(new StreamSource(new StringReader(snippets)), new StreamResult(result));
			
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Cannot transform to JasperReport DataSource", e);
		}

		return result.toString();
	}
	
	/**
	 * Generates custom HTML output
	 * @param snippets
	 * @param html_format is linked format trigger, <code>false</code> by default  
	 * @return String 
	 */	
	private String generateHtmlOutput(String snippets, String html_format)
	{
		StringWriter result = new StringWriter();
		try 
		{ 
			Transformer transformer = XmlHelper.tryTemplCache(ResourceUtil.getPathToTransformations() + "escidoc-publication-snippet2html.xsl").newTransformer();
			transformer.setParameter("pubman_instance", getPubManUrl());
			if ("html_linked".equals(html_format))
			{
				transformer.setParameter("html_linked", Boolean.TRUE);
			}
			transformer.transform(new StreamSource(new StringReader(snippets)), new StreamResult(result));
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Cannot transform to html:", e);
		}
		
		return result.toString();
	}
	
	
	/**
	 * Resolves PubMan instance url
	 * @return PubMan URL
	 */
	private static String getPubManUrl()
	{
		if( pubManUrl == null )
		{
			try 
			{
				String contextPath = PropertyReader.getProperty("escidoc.pubman.instance.context.path");
				pubManUrl = 
					PropertyReader.getProperty("escidoc.pubman.instance.url") + 
					(contextPath == null ? "" : contextPath);
				return pubManUrl; 
			} 
			catch (Exception e) 
			{
				throw new RuntimeException("Cannot get property:", e);
			} 
		}	
		else 
		{
			return pubManUrl;
		}
		
		
	}
	

}
