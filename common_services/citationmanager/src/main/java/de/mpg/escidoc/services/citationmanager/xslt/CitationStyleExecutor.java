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

package de.mpg.escidoc.services.citationmanager.xslt;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.log4j.Logger;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles.OutFormats;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;
import de.mpg.escidoc.services.framework.PropertyReader;

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
    
    
    private static final Logger logger = Logger.getLogger(CitationStyleExecutor.class); 
    /**
     * @param args
     */
    private static ProcessCitationStyles pcs = new ProcessCitationStyles();
    
    private static TransformerFactory tf = new net.sf.saxon.TransformerFactoryImpl();
    private HashMap<String, Templates> cache = new HashMap<String, Templates>(20);

    public String explainStyles() throws IllegalArgumentException, IOException,
            CitationStyleManagerException {
        
        return pcs.explainStyles();
    }

    public String getMimeType(String cs, String ouf)
            throws CitationStyleManagerException {
        // TODO Auto-generated method stub
        return pcs.getMimeType(cs, ouf);
    }

    public byte[] getOutput(String cs, String itemList) 
        throws IOException, JRException, CitationStyleManagerException  {
        
        Utils.checkCondition( !Utils.checkVal(itemList), "Empty item-list");

        StringWriter result = new StringWriter();
        
        TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();     
        InputStream stylesheet = ResourceUtil.getResourceAsStream(
                ResourceUtil.CITATIONSTYLES_DIRECTORY 
                + "/" + cs  
                + "/CitationStyle.xsl"
        );

        Document itemListDoc; 
        try 
        {
            Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
            transformer.transform(new StreamSource(new StringReader(itemList)), new StreamResult(result));

            itemListDoc = XmlHelper.createDocument(itemList);
            
            logger.info(result.toString());
                        
            NodeList itemListNodes;
            Object [] snipArr = extractSnippets(result.toString());
            try {
                itemListNodes = XmlHelper.xpathNodeList( "/item-list/item/properties/content-model-specific", itemListDoc);
                
                for (int i = 0; i < itemListNodes.getLength(); i++)         
                {
                    
                    Element snippetElement = itemListDoc.createElement(SNIPPET_ELEMENT_NAME);
                    snippetElement.setAttribute("xmlns:dcterms", SNIPPET_NS);
                    
                    CDATASection snippetCDATASection = itemListDoc.createCDATASection(
                            (String)snipArr[i]
                    );
                    snippetElement.appendChild(snippetCDATASection);
                    itemListNodes.item(i).appendChild(snippetElement);
                }
            } 
            catch (Exception e) 
            {
                throw new RuntimeException("Cannot insert snippet into item-list:", e);
            }       
            
        }   
        catch (Exception e) 
        {
                throw new RuntimeException("Error by transformation:", e);
        }
        //
//      return result.toString().getBytes("UTF-8");
        return XmlHelper.outputString(itemListDoc).getBytes("UTF-8");
        
    }
    
    
    public byte[] getOutput(String cs, String outputFormat,
            String itemList) throws IOException, JRException,
            CitationStyleManagerException  {

        Utils.checkCondition( !Utils.checkVal(outputFormat), "Output format is not defined");
        
//      Utils.checkCondition( !"snippet".equals(ouputFormat), "The only snippet format is supported for the moment");
        
        Utils.checkCondition( !Utils.checkVal(itemList), "Empty item-list");
        
        int slashPos = outputFormat.indexOf( "/" );
        String ouf = slashPos == -1 ? outputFormat : outputFormat.substring( slashPos + 1 );
        
        // TODO: mapping should be taken from explain-styles.xml 
        if (ouf.equals("vnd.oasis.opendocument.text")) 
            ouf = "odt";
         
        try {
            OutFormats.valueOf(ouf);
        } catch (Exception e) {
            throw new CitationStyleManagerException( "Output format: " + outputFormat + " is not supported" );
        }       
        

        byte[] result;
        String snippet;
        long start; 
        Transformer transformer;
        try 
        {
            
            start = System.currentTimeMillis();
            
            StringWriter sw = new StringWriter();
            
            String path = ResourceUtil.getPathToCitationStyles() + cs + "/CitationStyle.xsl"; 
            
            /* get xslt from the cache */
            transformer = tryCache(path).newTransformer();
            
            //set parameters
            String pub_inst = PropertyReader.getProperty("escidoc.pubman.instance.url") + PropertyReader.getProperty("escidoc.pubman.instance.context.path"); 
            transformer.setParameter("pubman_instance", pub_inst);
            transformer.transform(new StreamSource(new StringReader(itemList)), new StreamResult(sw));
            
            logger.info("Transformation item-list 2 snippet: " + (System.currentTimeMillis() - start));
            
            snippet = sw.toString(); 
            
            if ("snippet".equals(outputFormat))
            {
                result = snippet.getBytes("UTF-8");
            }
            else
            {
                //Hier: Call transformation service for transformation to output format from snippet to outputformat
                start = System.currentTimeMillis();

                String jrds = generateJasperReportDataSource(snippet);
                
                logger.info("Transformation snippet 2 JasperDS: " + (System.currentTimeMillis() - start));
//              logger.info ("DS:" + jrds);
                

                
                JasperReport jr = null;
                String csj = null;
                try 
                {
                    start = System.currentTimeMillis();
                    csj = ResourceUtil.getPathToCitationStyles() + "citation-style.jasper";
                    jr = (JasperReport)JRLoader.loadObject(ResourceUtil.getResourceAsStream(csj));
//                  Document doc = JRXmlUtils.parse(new InputSource(new StringReader(jrds) ));
                    Document doc = XmlHelper.createDocument(jrds);
                    
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, doc);
                    

                    JasperPrint jasperPrint= JasperFillManager.fillReport(
                            jr,
                            params,
                            new JRXmlDataSource(doc, jr.getQuery().getText())
//                          new JRXmlDataSource(doc)
                    );
                    
                    logger.info("JasperFillManager.fillReportToStream : " + (System.currentTimeMillis() - start));

                    start = System.currentTimeMillis();

                    JRExporter exporter = null;    
                    
                    if ("pdf".equals(outputFormat))
                    {
                        exporter = new JRPdfExporter();
                    } 
                    else if ("html".equals(outputFormat))
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
                    throw new RuntimeException("Cannot load JasperReport????: " + csj, e);
                }
                
                
                
            }

        }   
        catch (Exception e) 
        {
                throw new RuntimeException("Error by transformation:", e);
        }
        //
        return result;
//      return XmlHelper.outputString(itemListDoc).getBytes("UTF-8");
        
    }
    

    public String[] getOutputFormats(String cs)
            throws CitationStyleManagerException {
        return pcs.getOutputFormats(cs);
    }

    public String[] getStyles() throws CitationStyleManagerException {
        // TODO Auto-generated method stub
        return pcs.getStyles();
    }

    public boolean isCitationStyle(String cs)
            throws CitationStyleManagerException {
        // TODO Auto-generated method stub
        return pcs.isCitationStyle(cs);
    }

    private String generateJasperReportDataSource (String snippets)
    {
        return null;
    }
    
    private Object[] extractSnippets(String snippetsXml)
    {
        Pattern p = Pattern.compile("<snippet:snippet\\s.*?>(.*?)</snippet:snippet>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(snippetsXml);
        
        ArrayList<String> al = new ArrayList<String>();
        while (m.find())
        {
            al.add(m.group(1));
        }
        return al.toArray(); 
    }

    
    
    public static void main(String[] args) throws Exception {
        
        CitationStyleExecutor cse = new CitationStyleExecutor();
        
        
        //logger.info(pcst.explainStyles());
        logger.info(
                new String (
                        cse.getOutput("APA_new", ResourceUtil.getResourceAsString("DataSources/export_xml.xml")
                        )));
        logger.info(
                new String (
                        pcs.getOutput("APA", ResourceUtil.getResourceAsString("DataSources/export_xml.xml")
                        )));

    }
    
    /**
     * Maintain prepared stylesheets in memory for reuse
     */
    private Templates tryCache(String path) throws TransformerException, FileNotFoundException, CitationStyleManagerException 
    {
        Utils.checkName(path, "Empty XSLT name.");

        InputStream is = ResourceUtil.getResourceAsStream(path);
        
         Templates x = cache.get(path);
         if (x==null) {
             x = tf.newTemplates(new StreamSource(is));
             cache.put(path, x);
         }
         return x;
     }
}
