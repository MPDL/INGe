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
package de.mpg.mpdl.inge.pubman.web.util;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.AccessException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;

/**
 * Class to handle the file upload of locators.
 *
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public abstract class FileLocatorUploadBean extends FacesBean {
  private static final Logger logger = LogManager.getLogger(FileLocatorUploadBean.class);

  protected String name; // File Name
  protected String locator; // File Location
  protected String error = null; // Error Message

  private int size;
  private String type; // File MimeType

  public abstract void locatorUploaded();

  //  public abstract void removeEmptyFile();

  public abstract void removeLocator();

  /**
   * Executes a HEAD request to the locator.
   *
   * @param locator
   * @return true if locator is accessible
   */
  public boolean checkLocator(String locator) {
    this.locator = locator;
    if (locator != null) {
      this.locator = this.locator.trim();
    }
    URLConnection conn = null;
    byte[] input = null;
    String mimeType = null;
    String fileName = null;
    URL locatorURL = null;

    try {
      locatorURL = new URL(locator);
      conn = locatorURL.openConnection();
      final HttpURLConnection httpConn = (HttpURLConnection) conn;
      final int responseCode = httpConn.getResponseCode();
      switch (responseCode) {
        case 302, 503:
          this.error = this.getMessage("errorLocatorServiceUnavailable");
          return false;
        case 200:
          logger.debug("Source responded with 200.");
          break;
        case 403:
          this.error = this.getMessage("errorLocatorAccessDenied");
          logger.warn("Access to url " + locator + " is restricted.");
          return false;
        default:
          this.error = this.getMessage("errorLocatorTechnicalException");
          logger.warn(
              "An error occurred during importing from external system: " + responseCode + ": " + httpConn.getResponseMessage() + ".");
          return false;
      }
    } catch (final AccessException e) {
      logger.error("Access denied.", e);
      this.error = this.getMessage("errorLocatorAccessDenied");
      return false;
    } catch (final MalformedURLException e) {
      this.error = this.getMessage("errorLocatorInvalidURL");
      logger.warn("Invalid locator URL:" + locator, e);
      return false;
    } catch (final Exception e) {
      this.error = this.getMessage("errorLocatorTechnicalException");
      return false;
    }

    // Get Content Type
    mimeType = conn.getHeaderField("Content-Type");
    if (mimeType.contains(";")) {
      mimeType = mimeType.substring(0, mimeType.indexOf(";"));
    }
    if (mimeType != null) {
      this.setType(mimeType);
    }
    // Get File Name
    fileName = conn.getHeaderField("file-name");
    if (fileName != null) {
      this.setName(fileName);
    } else {
      this.setName(locatorURL.toString());
    }
    // Get File Length
    try {
      this.setSize(Integer.parseInt(conn.getHeaderField("Content-Length")));
    } catch (final NumberFormatException e) {
      input = this.fetchLocator(locatorURL);
      if (input != null) {
        this.setSize(input.length);
      }
    }
    return true;
  }

  private byte[] fetchLocator(URL locator) {
    byte[] input = null;
    URLConnection conn = null;

    try {
      conn = locator.openConnection();
      final HttpURLConnection httpConn = (HttpURLConnection) conn;
      final int responseCode = httpConn.getResponseCode();
      switch (responseCode) {
        case 200:
          logger.debug("Source responded with 200.");

          // Fetch file
          final GetMethod method = new GetMethod(locator.toString());
          final HttpClient client = new HttpClient();
          client.executeMethod(method);
          input = method.getResponseBody();
          httpConn.disconnect();
          break;
      }
    } catch (final Exception e) {
      this.error = this.getMessage("errorLocatorTechnicalException");
      return null;
    }

    return input;
  }

  public Vector<FileDbVO> getLocators(ItemVersionVO item) {
    final Vector<FileDbVO> locators = new Vector<>();

    final List<FileDbVO> files = item.getFiles();
    for (final FileDbVO currentFile : files) {
      if (currentFile.getStorage() == FileDbVO.Storage.EXTERNAL_URL) {
        locators.add(currentFile);
      }
    }

    return locators;
  }

  public FileDbVO uploadLocatorAsFile(FileDbVO locator) {
    FileDbVO fileVO = null;

    final boolean check = this.checkLocator(locator.getContent());

    if (check) {
      try {
        fileVO = new FileDbVO();
        fileVO.setMetadata(new MdsFileVO());
        fileVO.getMetadata().setSize(this.getSize());
        fileVO.getMetadata().setTitle(this.getFileName(this.getLocator()));
        fileVO.setMimeType(this.getType());
        fileVO.setName(this.getFileName(this.getLocator()));

        final FormatVO formatVO = new FormatVO();
        formatVO.setType("dcterms:IMT");
        formatVO.setValue(this.getType());
        fileVO.getMetadata().getFormats().add(formatVO);
        fileVO.setContent(this.getLocator());
        fileVO.setStorage(FileDbVO.Storage.INTERNAL_MANAGED);

      } catch (final Exception e) {
        logger.error(e);
        this.error = this.getMessage("errorLocatorUploadFW");
      }
    }

    if (this.getError() != null) {
      this.error(this.getMessage("errorLocatorMain").replace("$1", this.getError()));
      return null;
    }

    return fileVO;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getError() {
    return this.error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public int getSize() {
    return this.size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getLocator() {
    return this.locator;
  }

  public void setLocator(String locator) {
    this.locator = locator;
  }

  /**
   * Extracts the filename out of a URL.
   *
   * @return Filename as String
   */
  public String getFileName(String URL) {
    String name = "";
    final String[] names = URL.split("/");
    name = names[names.length - 1];
    return name;
  }
}
