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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.FullTextVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.MetadataVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.util.PropertyReader;
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
  private static final Logger logger = LogManager.getLogger(Util.class);

  private static final String METADATA_XSLT_LOCATION = "transformations/thirdParty/xslt";

  // Cone
  private static final String coneMethod = "escidocmimetypes";
  private static final String coneRel1 = "/resource/";
  private static final String coneRel2 = "?format=rdf";

  private Util() {}

  public static MetadataVO getMdObjectToFetch(DataSourceVO dataSourceVO, TransformerFactory.FORMAT format) {
    MetadataVO sourceMd = null;

    // First: check if format can be fetched directly
    for (int i = 0; i < dataSourceVO.getMdFormats().size(); i++) {
      sourceMd = dataSourceVO.getMdFormats().get(i);

      if (!sourceMd.getName().equalsIgnoreCase(format.getName())) {
        continue;
      }

      if (!sourceMd.getMdFormat().equalsIgnoreCase(format.getFileFormat().getMimeType())) {
        continue;
      }

      if ((!"*".equals(sourceMd.getEncoding())) && (!"*".equals(format.getFileFormat().getCharSet()))) {
        if (!sourceMd.getEncoding().equalsIgnoreCase(format.getFileFormat().getCharSet())) {
          continue;
        }
      }

      return sourceMd;
    }

    // Second: check which format can be transformed into the given format
    TransformerFactory.FORMAT[] possibleFormats = TransformerFactory.getAllSourceFormatsFor(format);

    for (int i = 0; i < dataSourceVO.getMdFormats().size(); i++) {
      sourceMd = dataSourceVO.getMdFormats().get(i);
      if (Arrays.asList(possibleFormats).contains(TransformerFactory.getFormat(sourceMd.getName()))) {
        return sourceMd;
      }
    }

    return null;
  }

  public static FullTextVO getFtObjectToFetch(DataSourceVO source, String fileFormatName) {
    FullTextVO fullTextVO = null;

    for (int i = 0; i < source.getFtFormats().size(); i++) {
      fullTextVO = source.getFtFormats().get(i);
      boolean fetchMd = true;

      if (!fullTextVO.getName().equalsIgnoreCase(fileFormatName)) {
        continue;
      }

      FileFormatVO.FILE_FORMAT fileFormat = FileFormatVO.getFileFormat(fileFormatName);
      if (!fullTextVO.getFtFormat().equalsIgnoreCase(fileFormat.getMimeType())) {
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

    for (String s : idPrefVec) {
      String idPref = s.toLowerCase();
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
      URL coneUrl = new URL(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + coneMethod + coneRel1 + mimeType + coneRel2);
      URLConnection con = coneUrl.openConnection();
      HttpURLConnection httpCon = (HttpURLConnection) con;

      int responseCode = httpCon.getResponseCode();

      switch (responseCode) {
        case 200:
          logger.debug("Cone Service responded with 200.");
          break;

        default:
          throw new RuntimeException("An error occurred while calling Cone Service: " + responseCode);
      }

      InputStreamReader isReader = new InputStreamReader(coneUrl.openStream(), StandardCharsets.UTF_8);
      BufferedReader bReader = new BufferedReader(isReader);

      String line = "";
      while (null != (line = bReader.readLine())) {
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
