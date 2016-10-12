/*
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.citationmanager.impl;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.docx4j.Docx4J;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.P;

import de.mpg.mpdl.inge.citationmanager.CitationStyleHandler;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.citationmanager.utils.CitationUtil;
import de.mpg.mpdl.inge.citationmanager.utils.Utils;
import de.mpg.mpdl.inge.citationmanager.utils.XmlHelper;
import de.mpg.mpdl.inge.cslmanager.CitationStyleLanguageManagerDefaultImpl;
import de.mpg.mpdl.inge.cslmanager.CitationStyleLanguageManagerInterface;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.transformation.TransformationBean;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * 
 * Citation Style Executor Engine, XSLT-centric
 * 
 * @author Initial creation: vmakarenko
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */

public class CitationStyleExecutor implements CitationStyleHandler {

  private CitationStyleLanguageManagerInterface citationStyleLanguageManager =
      new CitationStyleLanguageManagerDefaultImpl();

  private static String pubManUrl = null;

  private static final Logger logger = Logger.getLogger(CitationStyleExecutor.class);

  // private static ProcessCitationStyles pcs = new ProcessCitationStyles();


  /*
   * Explains citation styles and output types for them
   * 
   * @see de.mpg.mpdl.inge.citationmanager.CitationStyleHandler#explainStyles()
   */
  public String explainStyles() throws CitationStyleManagerException {
    return CitationUtil.getExplainStyles();
  }


  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.citationmanager.CitationStyleHandler#getOutputFormats(java.lang.String)
   */
  public String[] getOutputFormats(String cs) throws CitationStyleManagerException {
    return XmlHelper.getOutputFormatsArray(cs);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.citationmanager.CitationStyleHandler#getMimeType(java.lang.String,
   * java.lang.String)
   */
  public String getMimeType(String cs, String ouf) throws CitationStyleManagerException {
    return XmlHelper.getMimeType(cs, ouf);
  }



  public byte[] getOutput(String itemList, ExportFormatVO exportFormat)
      throws CitationStyleManagerException {

    Utils.checkCondition(!Utils.checkVal(exportFormat.getSelectedFileFormat().getName()),
        "Output format is not defined");

    Utils.checkCondition(!Utils.checkVal(itemList), "Empty item-list");



    String outputFormat = exportFormat.getSelectedFileFormat().getName();
    byte[] result = null;
    String snippet;

    long start = System.currentTimeMillis();
    try {
      if (!XmlHelper.citationStyleHasOutputFormat(exportFormat.getName(), outputFormat)) {
        throw new CitationStyleManagerException("Output format: " + outputFormat
            + " is not supported for Citation Style: " + exportFormat.getName());
      }

      if ("CSL".equals(exportFormat.getName())) {
        snippet =
            new String(citationStyleLanguageManager.getOutput(exportFormat, itemList), "UTF-8");
      } else {

        StringWriter sw = new StringWriter();
        String csXslPath = CitationUtil.getPathToCitationStyleXSL(exportFormat.getName());

        /* get xslt from the templCache */
        Transformer transformer = XmlHelper.tryTemplCache(csXslPath).newTransformer();

        // set parameters
        transformer.setParameter("pubman_instance", getPubManUrl());

        transformer.transform(new StreamSource(new StringReader(itemList)), new StreamResult(sw));

        logger.debug("Transformation item-list to snippet takes time: "
            + (System.currentTimeMillis() - start));

        snippet = sw.toString();
      }



      // new edoc md set
      if ("escidoc_snippet".equals(outputFormat)) {
        result = snippet.getBytes("UTF-8");
      }
      // old edoc md set: back transformation
      else if ("snippet".equals(outputFormat)) {
        Format in = new Format("escidoc-publication-item-list-v2", "application/xml", "UTF-8");
        Format out = new Format("escidoc-publication-item-list-v1", "application/xml", "UTF-8");

        TransformationBean trans = CitationUtil.getTransformationBean();

        byte[] v1 = null;
        try {
          v1 = trans.transform(snippet.getBytes("UTF-8"), in, out, "escidoc");
        } catch (Exception e) {
          throw new CitationStyleManagerException("Problems by escidoc v2 to v1 transformation:", e);
        }
        result = v1;
      } else if ("html_plain".equals(outputFormat) || "html_linked".equals(outputFormat)) {
        result = generateHtmlOutput(snippet, outputFormat, "html", true).getBytes("UTF-8");
      } else if ("docx".equals(outputFormat) || "pdf".equals(outputFormat)) {
        String htmlResult = generateHtmlOutput(snippet, "html_plain", "xhtml", false);

        WordprocessingMLPackage wordOutputDoc = WordprocessingMLPackage.createPackage();
        XHTMLImporter xhtmlImporter = new XHTMLImporterImpl(wordOutputDoc);
        MainDocumentPart mdp = wordOutputDoc.getMainDocumentPart();
        // mdp.addStyledParagraphOfText("Title", "Citation Style " + cs);

        List<Object> xhtmlObjects = xhtmlImporter.convert(htmlResult, null);


        // Remove line-height information for every paragraph
        for (Object xhtmlObject : xhtmlObjects) {
          try {
            P paragraph = (P) xhtmlObject;
            paragraph.getPPr().setSpacing(null);
          } catch (Exception e) {
            logger.error("Error while removing spacing information during docx export");
          }

        }


        mdp.getContent().addAll(xhtmlObjects);

        // Set global space after each paragraph
        mdp.getStyleDefinitionsPart().getStyleById("DocDefaults").getPPr().getSpacing()
            .setAfter(BigInteger.valueOf(400));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        if ("docx".equals(outputFormat)) {
          wordOutputDoc.save(bos);
        } else if ("pdf".equals(outputFormat)) {
          FOSettings foSettings = Docx4J.createFOSettings();
          foSettings.setWmlPackage(wordOutputDoc);
          Docx4J.toFO(foSettings, bos, Docx4J.FLAG_EXPORT_PREFER_XSL);

        }

        bos.flush();
        result = bos.toByteArray();



      }


      // logger.info( "snippet: " + extractBibliographicCitation(snippet) );

    } catch (Exception e) {
      throw new RuntimeException("Error by transformation:", e);
    }
    //
    return result;
    // return XmlHelper.outputString(itemListDoc).getBytes("UTF-8");

  }



  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.citationmanager.CitationStyleHandler#getStyles()
   */
  public String[] getStyles() throws CitationStyleManagerException {
    try {
      return XmlHelper.getListOfStyles();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new CitationStyleManagerException("Cannot get list of citation styles:", e);
    }
  }

  public boolean isCitationStyle(String cs) throws CitationStyleManagerException {
    return XmlHelper.isCitationStyle(cs);
  }

  /**
   * Generates custom HTML output
   * 
   * @param snippets
   * @param html_format is linked format trigger, <code>false</code> by default
   * @return String
   */
  private String generateHtmlOutput(String snippets, String html_format, String outputMethod,
      boolean indent) {
    StringWriter result = new StringWriter();
    try {
      Transformer transformer =
          XmlHelper.tryTemplCache(
              CitationUtil.getPathToTransformations() + "escidoc-publication-snippet2html.xsl")
              .newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
      transformer.setOutputProperty(OutputKeys.METHOD, outputMethod);

      transformer.setParameter("pubman_instance", getPubManUrl());
      if ("html_linked".equals(html_format)) {
        transformer.setParameter("html_linked", Boolean.TRUE);
      }
      transformer.transform(new StreamSource(new StringReader(snippets)), new StreamResult(result));
    } catch (Exception e) {
      throw new RuntimeException("Cannot transform to html:", e);
    }

    return result.toString();
  }


  /**
   * Resolves PubMan instance url
   * 
   * @return PubMan URL
   */
  private static String getPubManUrl() {
    if (pubManUrl == null) {
      try {
        String contextPath = PropertyReader.getProperty("escidoc.pubman.instance.context.path");
        pubManUrl =
            PropertyReader.getProperty("escidoc.pubman.instance.url")
                + (contextPath == null ? "" : contextPath);
        return pubManUrl;
      } catch (Exception e) {
        throw new RuntimeException("Cannot get property:", e);
      }
    } else {
      return pubManUrl;
    }


  }


}
