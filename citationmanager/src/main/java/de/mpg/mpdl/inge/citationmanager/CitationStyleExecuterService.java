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

package de.mpg.mpdl.inge.citationmanager;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.mpg.mpdl.inge.citationmanager.utils.CitationUtil;
import de.mpg.mpdl.inge.citationmanager.utils.XmlHelper;
import de.mpg.mpdl.inge.cslmanager.CitationStyleLanguageManagerService;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.util.EscidocNamespaceContextImpl;
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
public class CitationStyleExecuterService {
  private static final Logger logger = Logger.getLogger(CitationStyleExecuterService.class);

  public static String explainStyles() throws CitationStyleManagerException {
    return CitationUtil.getExplainStyles();
  }

  public static String getMimeType(String cs, String ouf) throws CitationStyleManagerException {
    return XmlHelper.getMimeType(cs, ouf);
  }

  public static List<String> getOutput(List<ItemVersionVO> itemList, ExportFormatVO exportFormat) throws CitationStyleManagerException {

    if (itemList == null || itemList.isEmpty())
      return new ArrayList<>();

    try {

      List<PubItemVO> transformedList = EntityTransformer.transformToOld(itemList);
      String escidocXmlList = XmlTransformingService.transformToItemList(transformedList);

      long start = System.currentTimeMillis();

      if (XmlHelper.CSL.equals(exportFormat.getCitationName())) {
        if (exportFormat.getId() == null || exportFormat.getId().isEmpty()) {
          throw new CitationStyleManagerException("CSL id is required!");
        }
        return CitationStyleLanguageManagerService.getOutput(exportFormat, escidocXmlList);
      } else {

        StringWriter sw = new StringWriter();
        String csXslPath = CitationUtil.getPathToCitationStyleXSL(exportFormat.getCitationName());

        /* get xslt from the templCache */
        Transformer transformer = XmlHelper.tryTemplCache(csXslPath).newTransformer();

        // set parameters
        transformer.setParameter("pubman_instance", getPubManUrl());
        transformer.transform(new StreamSource(new StringReader(escidocXmlList)), new StreamResult(sw));

        logger.debug("Transformation item-list to snippet takes time: " + (System.currentTimeMillis() - start));

        String snippet = sw.toString();

        return transformSnippetToCitationList(snippet);
      }

      /*
      
      // new edoc md set
      if (XmlHelper.ESCIDOC_SNIPPET.equals(outputFormat)) {
        result = snippet.getBytes(UTF_8);
      
      } else if (XmlHelper.SNIPPET.equals(outputFormat)) { // old edoc md set: back transformation
        de.mpg.mpdl.inge.transformation.Transformer trans =
            TransformerCache.getTransformer(FORMAT.ESCIDOC_ITEMLIST_V2_XML, FORMAT.ESCIDOC_ITEMLIST_V1_XML);
        StringWriter wr = new StringWriter();
        try {
          trans.transform(new TransformerStreamSource(new ByteArrayInputStream(snippet.getBytes(UTF_8))), new TransformerStreamResult(wr));
        } catch (Exception e) {
          throw new CitationStyleManagerException("Problems by escidoc v2 to v1 transformation:", e);
        }
        result = wr.toString().getBytes(UTF_8);
      
      } else if (XmlHelper.HTML_PLAIN.equals(outputFormat) || XmlHelper.HTML_LINKED.equals(outputFormat)) {
        result = generateHtmlOutput(snippet, outputFormat, HTML, true).getBytes(UTF_8);
      
      } else if (XmlHelper.TXT.equals(outputFormat)) {
        result = snippet.getBytes(UTF_8);
      
      } else if (XmlHelper.DOCX.equals(outputFormat) || XmlHelper.PDF.equals(outputFormat)) {
        String htmlResult = generateHtmlOutput(snippet, XmlHelper.HTML_PLAIN, XHTML, false);
        WordprocessingMLPackage wordOutputDoc = WordprocessingMLPackage.createPackage();
        XHTMLImporter xhtmlImporter = new XHTMLImporterImpl(wordOutputDoc);
        MainDocumentPart mdp = wordOutputDoc.getMainDocumentPart();
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
      
        // Set global space after each paragrap
        PPr ppr = new PPr();
        Spacing spacing = new Spacing();
        spacing.setAfter(BigInteger.valueOf(400));
        ppr.setSpacing(spacing);
        mdp.getStyleDefinitionsPart().getDefaultParagraphStyle().setPPr(ppr);;
      
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
      
        if (XmlHelper.DOCX.equals(outputFormat)) {
          wordOutputDoc.save(bos);
        } else if (XmlHelper.PDF.equals(outputFormat)) {
          FOSettings foSettings = Docx4J.createFOSettings();
          foSettings.setWmlPackage(wordOutputDoc);
          Docx4J.toFO(foSettings, bos, Docx4J.FLAG_EXPORT_PREFER_XSL);
        }
      
        bos.flush();
        result = bos.toByteArray();
      }
      else if (XmlHelper.JSON.equals(outputFormat)) {
        
        
      
      }
      
      */

    } catch (Exception e) {
      throw new CitationStyleManagerException("Error by transformation:", e);
    }

  }


  public static List<String> transformSnippetToCitationList(String snippet) throws CitationStyleManagerException {
    try {
      List<String> citationList = new ArrayList<>();
      XPathFactory xPathFactory = XPathFactory.newInstance();
      NamespaceContext nsContext = new EscidocNamespaceContextImpl();
      XPath xPath = xPathFactory.newXPath();
      xPath.setNamespaceContext(nsContext);
      XPathExpression exp = xPath.compile("//dcterms:bibliographicCitation");
      NodeList nl = (NodeList) exp.evaluate(new InputSource(new StringReader(snippet)), XPathConstants.NODESET);
      if (nl != null) {
        for (int i = 0; i < nl.getLength(); i++) {
          if (nl.item(i) != null) {
            citationList.add(nl.item(i).getTextContent());
          } else {
            citationList.add("");
          }

        }
      }
      return citationList;
    } catch (Exception e) {
      throw new CitationStyleManagerException("Error while parsing bibliographic citaation from escidoc snippet", e);
    }


  }

  public static boolean isCitationStyle(String cs) throws CitationStyleManagerException {
    return XmlHelper.isCitationStyle(cs);
  }



  private static String getPubManUrl() {
    try {
      String contextPath = PropertyReader.getProperty("inge.pubman.instance.context.path");
      return PropertyReader.getProperty("inge.pubman.instance.url") + (contextPath == null ? "" : contextPath);
    } catch (Exception e) {
      throw new RuntimeException("Cannot get property:", e);
    }
  }

  public static String[] getOutputFormats(String cs) throws CitationStyleManagerException {
    return XmlHelper.getOutputFormatsArray(cs);
  }

  public static String[] getStyles() throws CitationStyleManagerException {
    try {
      return XmlHelper.getListOfStyles();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new CitationStyleManagerException("Cannot get list of citation styles:", e);
    }
  }



}
