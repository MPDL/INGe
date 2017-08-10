/*
 * 
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

package de.mpg.mpdl.inge.dataacquisition;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.FullTextVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.MetadataVO;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerCache;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ProxyHelper;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * 
 * Helper methods for the DataAcquisition Service.
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class Util {
  private static final Logger logger = Logger.getLogger(Util.class);

  private static final String internalFormat = TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML.name();
  private static final String dummyFormat = "unknown";
  private static final String METADATA_XSLT_LOCATION = "transformations/thirdParty/xslt";

  // Cone
  private static final String coneMethod = "escidocmimetypes";
  private static final String coneRel1 = "/resource/";
  private static final String coneRel2 = "?format=rdf";

  /**
   * Retrieves the default encoding ("UTF-8").
   * 
   * @param formatName
   * @return default encoding
   */
  public static String getDefaultEncoding(String formatName) {
    if (formatName.equalsIgnoreCase(getInternalFormat())) {
      return "UTF-8";
    }

    return "*";
  }

  /**
   * @param formatName
   * @return default mimetype
   */
  public static String getDefaultMimeType(String formatName) {
    if ("apa".equalsIgnoreCase(formatName)) {
      return "text/html";
    }
    if ("ajp".equalsIgnoreCase(formatName)) {
      return "text/html";
    }
    if ("endnote".equalsIgnoreCase(formatName)) {
      return "text/plain";
    }
    if ("bibtex".equalsIgnoreCase(formatName)) {
      return "text/plain";
    }
    if ("coins".equalsIgnoreCase(formatName)) {
      return "text/plain";
    }
    if ("pdf".equalsIgnoreCase(formatName)) {
      return "application/pdf";
    }
    if ("ps".equalsIgnoreCase(formatName)) {
      return "application/gzip";
    }
    if ("bmcarticleFullTextHtml".equalsIgnoreCase(formatName)) {
      return "text/html";
    }
    if (formatName.startsWith("html-meta-tags")) {
      return "text/html";
    }

    return "application/xml";
  }

  /**
   * This operation return the Metadata Object of the format to fetch from the source.
   * 
   * @param source
   * @param trgFormatName
   * @param trgFormatType
   * @param trgFormatEndcoding
   * @return Metadata Object of the format to fetch
   * @throws FormatNotAvailableException
   */
  public static MetadataVO getMdObjectToFetch(DataSourceVO source, String trgFormatName,
      String trgFormatType, String trgFormatEndcoding) {
    MetadataVO sourceMd = null;
    DataSourceHandlerService sourceHandler = new DataSourceHandlerService();

    // First: check if format can be fetched directly
    for (int i = 0; i < source.getMdFormats().size(); i++) {
      sourceMd = source.getMdFormats().get(i);

      if (!sourceMd.getName().equalsIgnoreCase(trgFormatName)) {
        continue;
      }

      if (!sourceMd.getMdFormat().equalsIgnoreCase(trgFormatType)) {
        continue;
      }

      if ((!sourceMd.getEncoding().equals("*")) && (!trgFormatEndcoding.equals("*"))) {
        if (!sourceMd.getEncoding().equalsIgnoreCase(trgFormatEndcoding)) {
          continue;
        }
      }

      return sourceHandler.getMdObjectfromSource(source, sourceMd.getName());
    }

    // Second: check which format can be transformed into the given format
    TransformerFactory.FORMAT oldFormat = getFORMAT(trgFormatName);
    TransformerFactory.FORMAT[] possibleFormats =
        TransformerCache.getAllSourceFormatsFor(oldFormat);

    for (int i = 0; i < source.getMdFormats().size(); i++) {
      sourceMd = source.getMdFormats().get(i);
      if (Arrays.asList(possibleFormats).contains(getFORMAT(sourceMd.getName()))) {
        return sourceHandler.getMdObjectfromSource(source, sourceMd.getName());
      }
    }

    return null;
  }

  public static TransformerFactory.FORMAT getFORMAT(String formatName) {
    switch (formatName) {

      case "arXiv":
        return TransformerFactory.FORMAT.ARXIV_OAIPMH_XML;
      case "bmc":
        return TransformerFactory.FORMAT.BMC_OAIPMH_XML;
      case "bmcarticle":
      case "bmcreferences":
      case "bmcbibl":
        return TransformerFactory.FORMAT.BMC_OAIPMH_XML;
      case "bmcarticleFullTextXml":
        return TransformerFactory.FORMAT.BMC_FULLTEXT_XML;
      case "bmcarticleFullTextHtml":
        return TransformerFactory.FORMAT.BMC_FULLTEXT_XML;
      case "eSciDoc-publication-item":
        return TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML;
      case "esidoc-fulltext":
        return TransformerFactory.FORMAT.ESCIDOC_COMPONENT_XML;
      case "pmc":
        return TransformerFactory.FORMAT.PMC_OAIPMH_XML;
      case "spires":
        return TransformerFactory.FORMAT.SPIRES_XML;
      default:
        return TransformerFactory.FORMAT.valueOf(formatName);
    }
  }

  /**
   * Checks if a target format can be transformed from escidoc format. Will be more dynamic in
   * future when transformation service can handle transformation queuing
   * 
   * @param trgFormatName
   * @param trgFormatType
   * @param trgFormatEncoding
   * @return true if transformation is provided, else false
   */
  public static boolean checkEscidocTransform(String trgFormatName, String trgFormatType,
      String trgFormatEncoding) {

    if (TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML.equals(trgFormatName))
      return true;

    Transformer t = null;

    try {
      t =
          TransformerCache.getTransformer(TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML,
              TransformerFactory.FORMAT.valueOf(trgFormatName));
    } catch (TransformationException e) {
      logger.warn("No transformation found from <" + TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML
          + "> to < " + trgFormatName + ">");
      return false;
    }

    return t != null;
  }

  /**
   * This operation return the Fulltext Object of the format to fetch from the source.
   * 
   * @param source
   * @param formatName
   * @param formatType
   * @param formatEncoding
   * @return Fulltext Object of the format to fetch
   */
  public static FullTextVO getFtObjectToFetch(DataSourceVO source, String formatName,
      String formatType, String formatEncoding) {
    FullTextVO ft = null;

    for (int i = 0; i < source.getFtFormats().size(); i++) {
      ft = source.getFtFormats().get(i);
      boolean fetchMd = true;

      if (!ft.getName().equalsIgnoreCase(formatName)) {
        continue;
      }
      if (!ft.getFtFormat().equalsIgnoreCase(formatType)) {
        continue;
      }
      if ((!"*".equals(ft.getEncoding())) && (!"*".equals(formatEncoding))) {
        if (!ft.getEncoding().equalsIgnoreCase(formatEncoding)) {
          fetchMd = false;
        }
      }

      if (fetchMd) {
        return ft;
      } else {
        ft = null;
      }
    }

    return ft;
  }

  /**
   * Trims the given identifier according to description in source.xml, for a more flexible user
   * input handling.
   * 
   * @param source
   * @param identifier
   * @return a trimed identifier
   */
  public static String trimIdentifier(DataSourceVO source, String identifier) {
    List<String> idPrefVec = source.getIdentifier();

    for (int i = 0; i < idPrefVec.size(); i++) {
      String idPref = idPrefVec.get(i).toLowerCase();
      if (identifier.toLowerCase().startsWith(idPref)) {
        identifier = identifier.substring(idPref.length());
        if (identifier.startsWith(":")) {
          // because of the delimiter (:)
          identifier = identifier.substring(1);
        }
      }
    }

    // SPIRES is special case
    if ("spires".equalsIgnoreCase(source.getName())) {
      // If identifier is DOI, the identifier has to be enhanced
      if ((!"arxiv".startsWith(identifier.toLowerCase()))
          && (!"hep".startsWith(identifier.toLowerCase()))
          && (!"cond".startsWith(identifier.toLowerCase()))) {
        identifier = "FIND+DOI+" + identifier;
      }
    }

    return identifier;
  }

  /**
   * This method retrieves all formats a given format can be transformed into.
   * 
   * @param fetchFormats
   * @return List of Metadata Value Objects
   */
  public static List<MetadataVO> getTransformFormats(List<MetadataVO> fetchFormats) {
    List<MetadataVO> allFormats = new ArrayList<MetadataVO>();

    for (int i = 0; i < fetchFormats.size(); i++) {
      MetadataVO md = fetchFormats.get(i);
      // Format format = new Format(md.getName(), md.getMdFormat(), md.getEncoding());
      TransformerFactory.FORMAT[] formats =
          TransformerCache.getAllTargetFormatsFor(TransformerFactory.FORMAT.valueOf(md.getName()));
      // formats = this.handleDuplicateFormatNames(formats);
      // Create MetadataVO
      for (int ii = 0; ii < formats.length; ii++) {
        TransformerFactory.FORMAT formatTrans = formats[ii];
        MetadataVO mdTrans = new MetadataVO();
        mdTrans.setName(formats[ii].name());
        mdTrans.setMdFormat(formatTrans.getType());
        mdTrans.setEncoding("UTF-8");
        allFormats.add(mdTrans);
      }
    }

    return allFormats;
  }

  // /**
  // * Checks if a format can use escidoc as transition format.
  // *
  // * @param metadataV
  // * @return true if escidoc format can be transition format, else false
  // */
  // public static boolean checkEscidocTransition(List<MetadataVO> metadataV, String identifier) {
  // if (identifier.toLowerCase().contains(getInternalFormat()))
  // // Transition not possible for escidoc source
  // return false;
  //
  // for (int i = 0; i < metadataV.size(); i++) {
  // MetadataVO md = metadataV.get(i);
  // String format = md.getMdFormat();
  // FORMAT[] trgFormats = TransformerCache.getAllTargetFormatsFor(FORMAT.valueOf(format));
  //
  // if (Arrays.asList(trgFormats).contains(FORMAT.valueOf(getInternalFormat())))
  // return true;
  // }
  //
  // return false;
  // }

  /**
   * Eliminates duplicates in a List.
   * 
   * @param metadataV as MetadataVO List
   * @return List with unique entries
   */
  public static List<MetadataVO> getRidOfDuplicatesInVector(List<MetadataVO> metadataV) {
    List<MetadataVO> cleanVector = new ArrayList<MetadataVO>();
    MetadataVO format1;
    MetadataVO format2;


    for (int i = 0; i < metadataV.size(); i++) {
      boolean duplicate = false;
      format1 = (MetadataVO) metadataV.get(i);
      for (int x = i + 1; x < metadataV.size(); x++) {
        format2 = (MetadataVO) metadataV.get(x);
        if (isMdFormatEqual(format1, format2)) {
          duplicate = true;
        }
      }
      if (!duplicate) {
        cleanVector.add(format1);
      }
    }

    return cleanVector;
  }

  /**
   * Checks if the format of two MetadataVO Objects are equal.
   * 
   * @param src1
   * @param src2
   * @return true if equal, else false
   */
  public static boolean isMdFormatEqual(MetadataVO src1, MetadataVO src2) {
    if (!src1.getName().equalsIgnoreCase(src2.getName())) {
      return false;
    }

    if (!src1.getMdFormat().equalsIgnoreCase(src2.getMdFormat())) {
      return false;
    }

    if (src1.getEncoding().equals("*") || src2.getEncoding().equals("*")) {
      return true;
    } else {
      if (!src1.getEncoding().equalsIgnoreCase(src2.getEncoding())) {
        return false;
      }
    }

    return true;
  }

  /**
   * Creates the source description xml.
   * 
   * @return xml as byte[]
   */
  public static byte[] createUnapiSourcesXml() {
    // TODO: Rewrite ohne Datasources
    // byte[] xml = null;
    //
    // List<DataSourceVO> sources;
    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // DataSourceHandlerService sourceHandler = new DataSourceHandlerService();
    //
    // try {
    // sources = sourceHandler.getSources(null);
    // SourcesDocument xmlSourceDoc = SourcesDocument.Factory.newInstance();
    // SourcesType xmlSources = xmlSourceDoc.addNewSources();
    // for (int i = 0; i < sources.size(); i++) {
    // DataSourceVO source = sources.get(i);
    // SourceType xmlSource = xmlSources.addNewSource();
    // // Name
    // SimpleLiteral name = xmlSource.addNewName();
    // XmlString sourceName = XmlString.Factory.newInstance();
    // sourceName.setStringValue(source.getName());
    // name.set(sourceName);
    // // Base url
    // SimpleLiteral url = xmlSource.addNewIdentifier();
    // XmlString sourceUrl = XmlString.Factory.newInstance();
    // sourceUrl.setStringValue(source.getUrl().toExternalForm());
    // url.set(sourceUrl);
    // // Description
    // SimpleLiteral desc = xmlSource.addNewDescription();
    // XmlString sourceDesc = XmlString.Factory.newInstance();
    // sourceDesc.setStringValue(source.getDescription());
    // desc.set(sourceDesc);
    // // Identifier prefix
    // List<String> idPreVec = source.getIdentifier();
    // for (int x = 0; x < idPreVec.size(); x++) {
    // SimpleLiteral idPreSimp = xmlSource.addNewIdentifierPrefix();
    // XmlString sourceidPre = XmlString.Factory.newInstance();
    // sourceidPre.setStringValue(idPreVec.get(x));
    // idPreSimp.set(sourceidPre);
    // }
    // // Identifier delimiter
    // SimpleLiteral idDel = xmlSource.addNewIdentifierDelimiter();
    // XmlString sourceidDel = XmlString.Factory.newInstance();
    // sourceidDel.setStringValue(":");
    // idDel.set(sourceidDel);
    // // Identifier example
    // List<String> examples = source.getIdentifierExample();
    // if (examples != null) {
    // for (String example : examples) {
    // SimpleLiteral idEx = xmlSource.addNewIdentifierExample();
    // XmlString sourceidEx = XmlString.Factory.newInstance();
    // sourceidEx.setStringValue(example);
    // idEx.set(sourceidEx);
    // }
    // }
    // }
    // XmlOptions xOpts = new XmlOptions();
    // xOpts.setSavePrettyPrint();
    // xOpts.setSavePrettyPrintIndent(4);
    // xOpts.setUseDefaultNamespace();
    // xmlSourceDoc.save(baos, xOpts);
    // } catch (IOException e) {
    // logger.error("Error when creating outputXml.", e);
    // throw new RuntimeException(e);
    // }
    //
    // xml = baos.toByteArray();
    // return xml;
    return null;
  }

  // /**
  // * Extracts out of a url the escidoc import source name.
  // *
  // * @param sourceName
  // * @param identifier
  // * @return trimmed sourceName as String
  // */
  // public static String trimSourceName(String sourceName, String identifier) {
  // if (identifier.startsWith("http://dev-pubman")) {
  // sourceName = "escidocdev";
  // }
  // if (identifier.startsWith("http://qa-pubman")) {
  // sourceName = "escidocqa";
  // }
  // if (identifier.startsWith("http://test-pubman")) {
  // sourceName = "escidoctest";
  // }
  // if (identifier.startsWith("http://pubman")) {
  // sourceName = "escidocprod";
  // }
  // return sourceName;
  // }

  // /**
  // * EsciDoc Identifier can consist of the citation URL, like:
  // * http://pubman.mpdl.mpg.de:8080/pubman/item/escidoc:1048:3. This method extracts the
  // identifier
  // * from the URL
  // *
  // * @param identifier
  // * @return escidoc identifier as String
  // */
  // public static String setEsciDocIdentifier(String identifier) {
  // if (identifier.contains("/")) {
  // String[] extracts = identifier.split("/");
  // return extracts[extracts.length - 1];
  // } else {
  // if (!identifier.toLowerCase().startsWith("escidoc:")) {
  // return "escidoc" + ":" + identifier;
  // } else {
  // return identifier;
  // }
  // }
  // }

  /**
   * Retrieves the fileending for a given mimetype from the cone service.
   * 
   * @param mimeType
   * @return fileending as String
   */
  public static String retrieveFileEndingFromCone(String mimeType) {
    String suffix = null;
    URLConnection conn;
    InputStreamReader isReader;
    BufferedReader bReader;

    try {

      URL coneUrl =
          new URL(PropertyReader.getProperty("escidoc.cone.service.url") + coneMethod + coneRel1
              + mimeType + coneRel2);
      conn = ProxyHelper.openConnection(coneUrl);
      HttpURLConnection httpConn = (HttpURLConnection) conn;
      int responseCode = httpConn.getResponseCode();
      switch (responseCode) {
        case 200:
          logger.debug("Cone Service responded with 200.");
          break;
        default:
          throw new RuntimeException("An error occurred while calling Cone Service: "
              + responseCode);
      }
      isReader = new InputStreamReader(coneUrl.openStream(), "UTF-8");
      bReader = new BufferedReader(isReader);
      String line = "";
      while ((line = bReader.readLine()) != null) {
        if (line.contains("<escidoc:suffix>")) {
          suffix =
              line.substring(line.indexOf("<escidoc:suffix>") + "<escidoc:suffix>".length(),
                  line.indexOf("</escidoc:suffix>"));
        }
      }
      httpConn.disconnect();
    } catch (Exception e) {
      logger
          .warn("Suffix could not be retrieved from cone service (mimetype: " + mimeType + ")", e);
      return null;
    }

    return suffix;
  }

  public static String getInternalFormat() {
    return internalFormat;
  }

  public static String getDummyFormat() {
    return dummyFormat;
  }

  public static boolean checkXsltTransformation(String formatFrom, String formatTo) {
    String xsltUri = formatFrom.toLowerCase().trim() + "2" + formatTo.toLowerCase().trim() + ".xsl";
    boolean check = false;

    try {
      ResourceUtil.getResourceAsFile(METADATA_XSLT_LOCATION + "/" + xsltUri,
          Util.class.getClassLoader());
      check = true;

    } catch (FileNotFoundException e) {
      logger.warn("No transformation file from format: " + formatFrom + " to format: " + formatTo);
    }

    return check;
  }
}
