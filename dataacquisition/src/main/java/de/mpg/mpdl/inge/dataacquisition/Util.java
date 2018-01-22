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
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.FullTextVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.MetadataVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.transformation.TransformerCache;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
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

  private static final String METADATA_XSLT_LOCATION = "transformations/thirdParty/xslt";

  // Cone
  private static final String coneMethod = "escidocmimetypes";
  private static final String coneRel1 = "/resource/";
  private static final String coneRel2 = "?format=rdf";

  /**
   * This operation return the Metadata Object of the format to fetch from the source.
   * 
   * @param dataSourceVO
   * @param trgFormatName
   * @param trgFormatType
   * @param trgFormatEndcoding
   * @return Metadata Object of the format to fetch
   * @throws FormatNotAvailableException
   */
  public static MetadataVO getMdObjectToFetch(DataSourceVO dataSourceVO, TransformerFactory.FORMAT format) {
    MetadataVO sourceMd = null;
    //    DataSourceHandlerService sourceHandler = new DataSourceHandlerService();

    // First: check if format can be fetched directly
    for (int i = 0; i < dataSourceVO.getMdFormats().size(); i++) {
      sourceMd = dataSourceVO.getMdFormats().get(i);

      if (!sourceMd.getName().equalsIgnoreCase(format.getName())) {
        continue;
      }

      if (!sourceMd.getMdFormat().equalsIgnoreCase(format.getMimeType())) {
        continue;
      }

      if ((!sourceMd.getEncoding().equals("*")) && (!format.getEncoding().equals("*"))) {
        if (!sourceMd.getEncoding().equalsIgnoreCase(format.getEncoding())) {
          continue;
        }
      }

      return sourceMd;
      //      return sourceHandler.getMdObjectfromSource(dataSourceVO, sourceMd.getName());
    }

    // Second: check which format can be transformed into the given format
    //    TransformerFactory.FORMAT oldFormat = format;
    //    TransformerFactory.FORMAT[] possibleFormats = TransformerCache.getAllSourceFormatsFor(oldFormat);
    TransformerFactory.FORMAT[] possibleFormats = TransformerCache.getAllSourceFormatsFor(format);

    for (int i = 0; i < dataSourceVO.getMdFormats().size(); i++) {
      sourceMd = dataSourceVO.getMdFormats().get(i);
      if (Arrays.asList(possibleFormats).contains(TransformerFactory.getFormat(sourceMd.getName()))) {
        return sourceMd;
        //        return sourceHandler.getMdObjectfromSource(dataSourceVO, sourceMd.getName());
      }
    }

    return null;
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
  public static FullTextVO getFtObjectToFetch(DataSourceVO source, String outputFormat) {
    FullTextVO fullTextVO = null;

    for (int i = 0; i < source.getFtFormats().size(); i++) {
      fullTextVO = source.getFtFormats().get(i);
      boolean fetchMd = true;

      if (!fullTextVO.getName().equalsIgnoreCase(outputFormat)) {
        continue;
      }
      if (!fullTextVO.getFtFormat().equalsIgnoreCase(FileFormatVO.getMimeTypeByName(outputFormat))) {
        continue;
      }
      if ((!"*".equals(fullTextVO.getEncoding())) && (!"*".equals(FileFormatVO.DEFAULT_CHARSET))) {
        if (!fullTextVO.getEncoding().equalsIgnoreCase(FileFormatVO.DEFAULT_CHARSET)) {
          fetchMd = false;
        }
      }

      if (fetchMd) {
        return fullTextVO;
      } else {
        fullTextVO = null;
      }
    }

    return fullTextVO;
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
          identifier = identifier.substring(1);
        }
      }
    }

    return identifier;
  }

  /**
   * Retrieves the fileending for a given mimetype from the cone service.
   * 
   * @param mimeType
   * @return fileending as String
   */
  public static String retrieveFileEndingFromCone(String mimeType) {
    String suffix = null;

    try {
      URL coneUrl = new URL(PropertyReader.getProperty("inge.cone.service.url") + coneMethod + coneRel1 + mimeType + coneRel2);
      URLConnection con = ProxyHelper.openConnection(coneUrl);
      HttpURLConnection httpCon = (HttpURLConnection) con;

      int responseCode = httpCon.getResponseCode();

      switch (responseCode) {
        case 200:
          logger.debug("Cone Service responded with 200.");
          break;

        default:
          throw new RuntimeException("An error occurred while calling Cone Service: " + responseCode);
      }

      InputStreamReader isReader = new InputStreamReader(coneUrl.openStream(), "UTF-8");
      BufferedReader bReader = new BufferedReader(isReader);

      String line = "";
      while ((line = bReader.readLine()) != null) {
        if (line.contains("<escidoc:suffix>")) {
          suffix = line.substring(line.indexOf("<escidoc:suffix>") + "<escidoc:suffix>".length(), line.indexOf("</escidoc:suffix>"));
        }
      }

      httpCon.disconnect();
    } catch (Exception e) {
      logger.warn("Suffix could not be retrieved from cone service (mimetype: " + mimeType + ")", e);
      return null;
    }

    return suffix;
  }

  public static boolean checkXsltTransformation(String formatFrom, String formatTo) {
    String xsltUri = formatFrom.toLowerCase().trim() + "2" + formatTo.toLowerCase().trim() + ".xsl";
    boolean check = false;

    try {
      ResourceUtil.getResourceAsFile(METADATA_XSLT_LOCATION + "/" + xsltUri, Util.class.getClassLoader());
      check = true;
    } catch (FileNotFoundException e) {
      logger.warn("No transformation file from format: " + formatFrom + " to format: " + formatTo);
    }

    return check;
  }
}
