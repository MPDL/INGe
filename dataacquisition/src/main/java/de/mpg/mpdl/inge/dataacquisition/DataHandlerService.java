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
package de.mpg.mpdl.inge.dataacquisition;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.rmi.AccessException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.FullTextVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.MetadataVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 */
public class DataHandlerService {
  private static final Logger logger = Logger.getLogger(DataHandlerService.class);

  private static final String CROSSREF = "crossref";
  private static final String PROTOCOL_OAI = "oai-pmh";
  private static final String REGEX_CROSSREF_PID = "CROSSREF_PID";
  private static final String REGEX_GETID = "GETID";

  private String contentCategorie;
  private String contentMimeType;
  private String fileEnding;
  private String visibility;

  private FileDbVO componentVO;

  public DataHandlerService() {}

  public byte[] doFetchMetaData(String source, DataSourceVO dataSourceVO, String identifier, TransformerFactory.FORMAT targetFormat)
      throws DataacquisitionException {
    byte[] fetchedData = null;

    try {
      String textualData = this.fetchTextualData(source, identifier, dataSourceVO, targetFormat);

      if (textualData != null) {
        fetchedData = textualData.getBytes();
      }
    } catch (Exception e) {
      throw new DataacquisitionException(e);
    }

    return fetchedData;
  }

  public byte[] doFetchFullText(DataSourceVO dataSourceVO, String identifier, String[] fileFormatNames) throws DataacquisitionException {
    byte[] fetchedData = null;

    try {
      fetchedData = this.fetchFileData(identifier, dataSourceVO, fileFormatNames);
    } catch (Exception e) {
      throw new DataacquisitionException(e);
    }

    return fetchedData;
  }

  private String fetchTextualData(String source, String identifier, DataSourceVO dataSourceVO, TransformerFactory.FORMAT targetFormat)
      throws DataacquisitionException, TransformationException, UnsupportedEncodingException, MalformedURLException {
    MetadataVO metaDataVO = Util.getMdObjectToFetch(dataSourceVO, targetFormat);

    if (metaDataVO == null) {
      logger.warn("Requested metadata for identifier " + identifier + " not supported.");
      throw new DataacquisitionException("Requested metadata for identifier " + identifier + " not supported.");
    }

    String decodedUrl = URLDecoder.decode(metaDataVO.getMdUrl().toString(), dataSourceVO.getEncoding());
    decodedUrl = decodedUrl.replace(REGEX_GETID, identifier.trim());
    if (DataHandlerService.CROSSREF.equalsIgnoreCase(source)) {
      decodedUrl = decodedUrl.replace(REGEX_CROSSREF_PID, PropertyReader.getProperty(PropertyReader.INGE_CROSSREF_PID));
    }
    URL url = new URL(decodedUrl);

    // Select harvesting method
    String item = null;
    boolean supportedProtocol = false;
    if (dataSourceVO.getHarvestProtocol().equalsIgnoreCase(PROTOCOL_OAI)) {
      item = fetchRecord(url, targetFormat.getFileFormat().getCharSet(), dataSourceVO);
      // Check the record for error codes
      ProtocolHandler protocolHandler = new ProtocolHandler();
      protocolHandler.checkOAIRecord(item);
      supportedProtocol = true;
    } else {
      item = fetchRecord(url, targetFormat.getFileFormat().getCharSet(), dataSourceVO);
      supportedProtocol = true;
    }

    if (!supportedProtocol) {
      logger.warn("Harvesting protocol " + dataSourceVO.getHarvestProtocol() + " not supported.");
      throw new DataacquisitionException("Harvesting protocol " + dataSourceVO.getHarvestProtocol() + " not supported.");
    }

    String itemAfterTransformaton = item;

    // Transform the itemXML if necessary
    if (item != null) {

      TransformerFactory.FORMAT metaDataFormat;
      try {
        metaDataFormat = TransformerFactory.getFormat(metaDataVO.getName());
      } catch (IllegalArgumentException e) {
        logger.warn("Requested metadata format " + metaDataVO.getName() + " not supported.");
        throw new DataacquisitionException("Requested metadata format " + metaDataVO.getName() + " not supported.");
      }

      if (!targetFormat.equals(metaDataFormat)) {
        String fetchedItem = item;
        Transformer transformer = TransformerFactory.newTransformer(metaDataFormat, targetFormat);
        StringWriter wr = new StringWriter();

        transformer.transform(
            new TransformerStreamSource(new ByteArrayInputStream(item.getBytes(targetFormat.getFileFormat().getCharSet()))),
            new TransformerStreamResult(wr));

        itemAfterTransformaton = wr.toString();

        try {
          Transformer componentTransformer =
              TransformerFactory.newTransformer(metaDataFormat, TransformerFactory.FORMAT.ESCIDOC_COMPONENT_XML);

          if (componentTransformer != null) {
            wr = new StringWriter();

            componentTransformer.transform(new TransformerStreamSource(new ByteArrayInputStream(fetchedItem.getBytes())),
                new TransformerStreamResult(wr));
            byte[] componentBytes = wr.toString().getBytes();

            if (componentBytes != null) {
              String componentXml = new String(componentBytes);
              this.componentVO = EntityTransformer.transformToNew(XmlTransformingService.transformToFileVO(componentXml));
            }
          }
        } catch (Exception e) {
          logger.info("No component was created from external this.sources metadata");
        }
      }
    }

    this.contentMimeType = targetFormat.getFileFormat().getMimeType();

    return itemAfterTransformaton;
  }


  private byte[] fetchFileData(String identifier, DataSourceVO dataSourceVO, String[] fileFormatNames) throws DataacquisitionException {
    byte[] in = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    try {
      // Call fetch file for every given format
      for (int i = 0; i < fileFormatNames.length; i++) {
        String fileFormatName = fileFormatNames[i];
        FullTextVO fulltextVO = Util.getFtObjectToFetch(dataSourceVO, fileFormatName);
        // Replace regex with identifier
        String decoded = java.net.URLDecoder.decode(fulltextVO.getFtUrl().toString(), dataSourceVO.getEncoding());
        fulltextVO.setFtUrl(new URL(decoded));
        fulltextVO.setFtUrl(new URL(fulltextVO.getFtUrl().toString().replace(REGEX_GETID, identifier.trim())));

        in = this.fetchFile(fulltextVO, dataSourceVO);

        this.setFileProperties(fulltextVO);

        // If only one file => return it in fetched format
        if (fileFormatNames.length == 1) {
          return in;
        }

        // If more than one file => add it to zip
        // If cone service is not available (we do not get a fileEnding)
        // we have to make sure that the zip entries differ in name.
        String fileName = identifier;

        if ("".equals(this.getFileEnding())) {
          fileName = fileName + "_" + i;
        }

        ZipEntry ze = new ZipEntry(fileName + this.getFileEnding());
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

      this.contentMimeType = "application/zip";
      this.fileEnding = ".zip";

      zos.close();
    } catch (DataacquisitionException e) {
      logger.error("Import this.source " + dataSourceVO + " not available.", e);
      throw new DataacquisitionException("Import this.source " + dataSourceVO + " not available.", e);
    } catch (IOException e) {
      throw new DataacquisitionException(e);
    }

    return baos.toByteArray();
  }

  private byte[] fetchFile(FullTextVO fulltext, DataSourceVO dataSourceVO) throws DataacquisitionException {
    byte[] input = null;

    try {
      logger.info("Url: " + fulltext.getFtUrl());
      URLConnection con = fulltext.getFtUrl().openConnection();
      HttpURLConnection httpCon = (HttpURLConnection) con;

      int responseCode = httpCon.getResponseCode();

      switch (responseCode) {
        case 200:
          logger.info("Source responded with 200.");
          GetMethod method = new GetMethod(fulltext.getFtUrl().toString());
          HttpClient client = new HttpClient();
          client.executeMethod(method);
          input = method.getResponseBody();
          httpCon.disconnect();
          break;

        case 302:
          String alternativeLocation = con.getHeaderField("Location");
          fulltext.setFtUrl(new URL(alternativeLocation));

          return fetchFile(fulltext, dataSourceVO);

        case 403:
          throw new DataacquisitionException("Access to url " + dataSourceVO.getName() + " is restricted.");

        case 503:
          // request was not processed by this.source
          logger.warn("Import this.source " + dataSourceVO.getName() + "did not provide data in format " + fulltext.getFtLabel());
          throw new DataacquisitionException(
              "Import this.source " + dataSourceVO.getName() + "did not provide data in format " + fulltext.getFtLabel());

        default:
          throw new DataacquisitionException(
              "An error occurred during importing from external system: " + responseCode + ": " + httpCon.getResponseMessage());
      }
    } catch (AccessException e) {
      logger.error("Access denied.", e);
      throw new DataacquisitionException(dataSourceVO.getName());
    } catch (IOException e) {
      throw new DataacquisitionException(e);
    }

    return input;
  }

  /**
   * Fetches a record for given record identifier.
   * 
   * @param this.sourceURL
   * @return itemXML
   * @throws DataacquisitionException
   */
  private String fetchRecord(URL url, String encoding, DataSourceVO dataSourceVO) throws DataacquisitionException {
    StringBuffer itemXML = new StringBuffer();

    try {
      logger.info("Url: " + url.toString());
      URLConnection con = url.openConnection();
      HttpURLConnection httpCon = (HttpURLConnection) con;

      int responseCode = httpCon.getResponseCode();

      switch (responseCode) {
        case 200:
          logger.info("Source responded with 200");
          break;

        case 403:
          throw new AccessException("Access to url " + dataSourceVO.getName() + " is restricted.");

        case 503:
          String retryAfterHeader = con.getHeaderField("Retry-After");
          if (retryAfterHeader != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            Date retryAfter = dateFormat.parse(retryAfterHeader);
            logger.debug("Source responded with 503, retry after " + retryAfter + ".");
            throw new DataacquisitionException("Source responded with 503, retry after " + retryAfter + ".");
          } else {
            logger.debug("Source responded with 503, retry after " + dataSourceVO.getRetryAfter() + ".");
            throw new DataacquisitionException("Source responded with 503, retry after " + dataSourceVO.getRetryAfter() + ".");
          }

        default:
          throw new DataacquisitionException(
              "An error occurred during importing from external system: " + responseCode + ": " + httpCon.getResponseMessage());
      }

      // Get itemXML
      InputStreamReader isReader = new InputStreamReader(url.openStream(), encoding);
      BufferedReader bReader = new BufferedReader(isReader);

      String line = "";
      while ((line = bReader.readLine()) != null) {
        itemXML.append(line + "\n");
      }

      httpCon.disconnect();
    } catch (AccessException e) {
      logger.error("Access denied.", e);
      throw new DataacquisitionException("Access denied " + dataSourceVO.getName(), e);
    } catch (Exception e) {
      throw new DataacquisitionException(e);
    }

    return itemXML.toString();
  }

  private void setFileProperties(FullTextVO fulltext) {
    this.visibility = fulltext.getVisibility();
    this.contentCategorie = fulltext.getContentCategory();
    this.contentMimeType = fulltext.getFtFormat();
    this.fileEnding = Util.retrieveFileEndingFromCone(fulltext.getFtFormat());
  }

  private long currentDate() {
    Date today = new Date();
    return today.getTime();
  }

  public String getContentType() {
    return this.contentMimeType;
  }

  public String getFileEnding() {
    if (this.fileEnding == null) {
      return "";
    } else {
      return this.fileEnding;
    }
  }

  public String getContentCategory() {
    return this.contentCategorie;
  }

  public Visibility getVisibility() {
    if (FileDbVO.Visibility.PUBLIC.name().equals(this.visibility)) {
      return FileDbVO.Visibility.PUBLIC;
    }

    return FileDbVO.Visibility.PRIVATE;
  }

  public FileDbVO getComponentVO(DataSourceVO dataSourceVO) {
    if (this.componentVO != null) {
      if (this.componentVO.getMetadata().getRights() == null || this.componentVO.getMetadata().getRights().equals("")) {
        this.componentVO.getMetadata().setRights(dataSourceVO.getCopyright());
      }

      if (this.componentVO.getMetadata().getLicense() == null || this.componentVO.getMetadata().getLicense().equals("")) {
        this.componentVO.getMetadata().setLicense(dataSourceVO.getLicense());
      }

      return this.componentVO;
    }

    FileDbVO file = new FileDbVO();
    MdsFileVO md = new MdsFileVO();
    md.setLicense(dataSourceVO.getLicense());
    md.setRights(dataSourceVO.getCopyright());
    file.setMetadata(md);

    return file;
  }
}
