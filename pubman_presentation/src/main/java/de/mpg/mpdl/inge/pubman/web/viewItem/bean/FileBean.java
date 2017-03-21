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

package de.mpg.mpdl.inge.pubman.web.viewItem.bean;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.faces.event.ValueChangeEvent;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.SearchHitVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchHitVO.SearchHitType;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ProxyHelper;

/**
 * Bean for storing the information of files attached to items.
 * 
 * @author: Tobias Schraut, created 25.03.2008
 * @version: $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class FileBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(FileBean.class);

  private FileVO file;
  private List<SearchHitBean> searchHits = new ArrayList<SearchHitBean>();
  private State itemState;
  private boolean fileAccessGranted = false;

  /**
   * Public constructor with parameters
   * 
   * @param file
   * @param position
   * @param itemState
   */
  public FileBean(FileVO file, State itemState) {
    this.file = file;
    this.itemState = itemState;
    if (getLoginHelper().getLoggedIn() == true) {
      initializeFileAccessGranted();
    }
  }

  /**
   * Second constructor (used if pubitem has fulltext search hits)
   * 
   * @param file
   * @param position
   * @param itemState
   * @param resultitem
   */
  public FileBean(FileVO file, State itemState, List<SearchHitVO> searchHitList) {
    this.file = file;
    this.itemState = itemState;
    initialize(file, itemState, searchHitList);
    if (getLoginHelper().getLoggedIn() == true) {
      initializeFileAccessGranted();
    }
  }

  /**
   * Sets up some extra information concerning full text search hits
   * 
   * @param file
   * @param position
   * @param itemState
   * @param resultitem
   */
  protected void initialize(FileVO file, State itemState, List<SearchHitVO> searchHitList) {
    // set some html elements which cannot be completely constructed in the jsp

    String beforeSearchHitString;
    String searchHitString;
    String afterSearchHitString;

    // browse through the list of files and examine which of the files is the one the search result
    // hits where found in
    for (int i = 0; i < searchHitList.size(); i++) {
      if (searchHitList.get(i).getType() == SearchHitType.FULLTEXT) {
        if (searchHitList.get(i).getHitReference() != null) {
          if (searchHitList.get(i).getHitReference().equals(this.file.getReference())) {
            for (int j = 0; j < searchHitList.get(i).getTextFragmentList().size(); j++) {
              int startPosition = 0;
              int endPosition = 0;

              startPosition =
                  searchHitList.get(i).getTextFragmentList().get(j).getHitwordList().get(0)
                      .getStartIndex();
              endPosition =
                  searchHitList.get(i).getTextFragmentList().get(j).getHitwordList().get(0)
                      .getEndIndex() + 1;

              beforeSearchHitString =
                  "..."
                      + searchHitList.get(i).getTextFragmentList().get(j).getData()
                          .substring(0, startPosition);
              searchHitString =
                  searchHitList.get(i).getTextFragmentList().get(j).getData()
                      .substring(startPosition, endPosition);
              afterSearchHitString =
                  searchHitList.get(i).getTextFragmentList().get(j).getData()
                      .substring(endPosition)
                      + "...";

              this.searchHits.add(new SearchHitBean(beforeSearchHitString, searchHitString,
                  afterSearchHitString));
            }
          }

        }

      }

    }

  }

  private void initializeFileAccessGranted() {
    // examine weather the user holds an audience Grant for the current file or not
    try {
      if (file.getReference() != null && file.getVisibility().equals(FileVO.Visibility.AUDIENCE)) {
        UserAccountHandler uah =
            ServiceLocator.getUserAccountHandler(getLoginHelper().getAccountUser().getHandle());

        FilterTaskParamVO filter = new FilterTaskParamVO();

        Filter accountUserFilter =
            filter.new StandardFilter("http://escidoc.de/core/01/properties/user", getLoginHelper()
                .getAccountUser().getReference().getObjectId(), "=", "AND");
        filter.getFilterList().add(accountUserFilter);

        Filter notAudienceRoleFilter =
            filter.new StandardFilter("/properties/role/id",
                GrantVO.PredefinedRoles.AUDIENCE.frameworkValue(), "=", "AND");
        filter.getFilterList().add(notAudienceRoleFilter);

        Filter assignedOnFilter =
            filter.new StandardFilter("http://escidoc.de/core/01/properties/assigned-on", file
                .getReference().getObjectId(), "=", "AND");
        filter.getFilterList().add(assignedOnFilter);

        Filter notRevokedFilter =
            filter.new StandardFilter("/properties/revocation-date", "\"\"", "=", "AND");
        filter.getFilterList().add(notRevokedFilter);

        String userGrantXML = uah.retrieveGrants(filter.toMap());
        SearchRetrieveResponseVO searchResult =
            XmlTransformingService.transformToSearchRetrieveResponseGrantVO(userGrantXML);
        if (searchResult.getNumberOfRecords() > 0) {
          this.fileAccessGranted = true;
        } else {
          this.fileAccessGranted = false;
        }
      } else {
        this.fileAccessGranted = false;
      }
    } catch (Exception e) {
      logger.error("Problem getting audience-grants for file [" + file.getReference().getObjectId()
          + "]", e);
    }
  }

  public String downloadFile() {
    try {
      String fileLocation = PropertyReader.getFrameworkUrl() + this.file.getContent();
      String filename = this.file.getName(); // Filename suggested in browser Save As dialog
      filename = filename.replace(" ", "_"); // replace empty spaces because they cannot be procesed
                                             // by the http-response (filename will be cutted after
                                             // the first empty space)
      String contentType = this.file.getMimeType(); // For dialog, try
      System.out.println("MIME: " + contentType);

      // application/x-download
      FacesTools.getResponse().setHeader("Content-disposition",
          "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
      if (this.file.getDefaultMetadata() != null) {
        FacesTools.getResponse().setContentLength(this.file.getDefaultMetadata().getSize());
      }

      FacesTools.getResponse().setContentType(contentType);
      System.out.println("MIME: " + FacesTools.getResponse().getContentType());

      byte[] buffer = null;
      if (this.file.getDefaultMetadata() != null) {
        try {
          GetMethod method = new GetMethod(fileLocation);
          method.setFollowRedirects(false);
          if (getLoginHelper().getESciDocUserHandle() != null) {
            // downloading by account user
            addHandleToMethod(method, getLoginHelper().getESciDocUserHandle());
          }

          // Execute the method with HttpClient.
          HttpClient client = new HttpClient();
          ProxyHelper.setProxy(client, fileLocation); // ????
          client.executeMethod(method);
          OutputStream out = FacesTools.getResponse().getOutputStream();
          InputStream input = method.getResponseBodyAsStream();
          try {
            if (this.file.getDefaultMetadata() != null) {
              buffer = new byte[this.file.getDefaultMetadata().getSize()];
              int numRead;
              // long numWritten = 0;
              while ((numRead = input.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
                out.flush();
                // numWritten += numRead;
              }
              FacesTools.getCurrentInstance().responseComplete();
            }
          } catch (IOException e1) {
            logger.debug("Download IO Error: " + e1.toString());
          }
          input.close();
          out.close();
        } catch (FileNotFoundException e) {
          logger.debug("File not found: " + e.toString());
        }
      }
    } catch (Exception e) {
      logger.debug("File Download Error: " + e.toString());
      System.out.println(e.toString());
    }
    return null;
  }

  /**
   * Adds a cookie named "escidocCookie" that holds the eScidoc user handle to the provided http
   * method object.
   * 
   * @author Tobias Schraut
   * @param method The http method to add the cookie to.
   */
  private void addHandleToMethod(final HttpMethod method, String eSciDocUserHandle) {
    // Staging file resource is protected, access needs authentication and
    // authorization. Therefore, the eSciDoc user handle must be provided.
    // Put the handle in the cookie "escidocCookie"
    method.setRequestHeader("Cookie", "escidocCookie=" + eSciDocUserHandle);
  }

  public String getContentCategory() {
    if (this.file.getContentCategory() != null) {
      for (Entry<String, String> contcat : PubFileVOPresentation.getContentCategoryMap().entrySet()) {
        if (contcat.getValue().equals(this.file.getContentCategory())) {
          return getLabel("ENUM_CONTENTCATEGORY_"
              + contcat.getKey().toLowerCase().replace("_", "-"));
        }
      }
    }

    return "";
  }

  public String getVisibility() {
    if (this.file.getVisibility() != null) {
      return getLabel(getI18nHelper().convertEnumToString(this.file.getVisibility()));
    }

    return "";
  }

  public boolean getItemWithdrawn() {
    if (this.itemState.equals(State.WITHDRAWN)) {
      return true;
    }

    return false;
  }

  public boolean getShowSearchHits() {
    if (this.searchHits != null && this.searchHits.size() > 0) {
      return true;
    }

    return false;
  }

  public FileVO getFile() {
    return this.file;
  }

  public String getFileName() {
    if (this.file.getDefaultMetadata() != null && this.file.getDefaultMetadata().getTitle() != null) {
      return this.file.getDefaultMetadata().getTitle();
    }

    return "";
  }

  public String getFileDescription() {
    if (file.getDefaultMetadata() != null
        && this.file.getDefaultMetadata().getDescription() != null) {
      return this.file.getDefaultMetadata().getDescription();
    }

    return "";
  }


  public String getUrlEncodedFileName() {
    return CommonUtils.urlEncode(this.file.getName());
  }

  public String getFileLink() {
    return this.file.getContent();
  }

  public String getLocator() {
    if (this.file.getDefaultMetadata() != null && this.file.getDefaultMetadata().getTitle() != null) {
      return this.file.getDefaultMetadata().getTitle();
    }

    return "";
  }

  public String getLocatorLink() {
    return this.file.getContent();
  }

  public void setFile(FileVO file) {
    this.file = file;
  }

  public String getFileSize() {
    if (this.file.getDefaultMetadata() != null) {
      return computeFileSize(this.file.getDefaultMetadata().getSize());
    }

    return "0";
  }

  public List<SearchHitBean> getSearchHits() {
    return this.searchHits;
  }

  public void setSearchHits(List<SearchHitBean> searchHits) {
    this.searchHits = searchHits;
  }

  public int getNumberOfSearchHits() {
    return this.searchHits.size();
  }

  public boolean getLocatorIsLink() {
    return ((getFile().getStorage() == FileVO.Storage.EXTERNAL_URL) && (getFile().getContent()
        .startsWith("http://") || getFile().getContent().startsWith("https://") || getFile()
        .getContent().startsWith("ftp://")));
  }

  public boolean getIsVisible() {
    if (this.file.getVisibility().equals(FileVO.Visibility.PUBLIC)) {
      return true;
    }

    return false;
  }

  /**
   * This method generates a link to a refering thumbnail image out of a link to a creativecommons
   * licence
   * 
   * @return teh generated link to the refering thumbnail image
   */
  public String getUrlToLicenceImage() {
    try {
      if (file.getDefaultMetadata() != null && file.getDefaultMetadata().getLicense() != null) {
        String licenceURL = file.getDefaultMetadata().getLicense().toLowerCase();

        if (licenceURL != null && !licenceURL.trim().equals("")
            && licenceURL.indexOf("creative") > -1 && licenceURL.indexOf("commons") > -1) {
          String[] splittedURL = licenceURL.split("\\/");
          // Change for dettecting license url in a string
          int start = 0;
          for (int i = 0; i < splittedURL.length; i++) {
            String part = splittedURL[i];
            if (part.startsWith("creativecommons")) {
              start = i;
            }
          }

          String address = splittedURL[start];
          String licenses = "l";
          String type = splittedURL[start + 2];
          String version = splittedURL[start + 3];
          String image = "80x15.png";

          return "http://i." + address + "/" + licenses + "/" + type + "/" + version + "/" + image;
        }
      }
    } catch (Exception e) {
    }

    return "";
  }

  /**
   * This Method evaluates if the embargo date input filed has to be displayed or not (yes, if
   * visibility is set to private or restricted)
   * 
   * @return boolean flag if embargo date input field should be displayed or not
   */
  public boolean getShowEmbargoDate() {
    if (this.file.getVisibility().equals(FileVO.Visibility.PRIVATE)) {
      return true;
    }

    return false;
  }

  public void setUpdateVisibility(ValueChangeEvent event) {
    Visibility newVisibility = (Visibility) event.getNewValue();
    this.file.setVisibility(newVisibility);
  }

  /**
   * Returns the checksum algorithm of the file as string.
   * 
   * @return
   */
  public String getChecksumAlgorithmAsString() {
    if (this.file.getChecksumAlgorithm() != null) {
      return this.file.getChecksumAlgorithm().toString();
    }

    return null;
  }

  /**
   * Sends back an html response of content type text/plain that includes the checksum as UTF-8
   * string.
   * 
   * @return
   */
  public String displayChecksum() {
    if (file.getChecksum() != null && file.getChecksumAlgorithm() != null) {
      FacesTools.getResponse().setContentLength(file.getChecksum().length());
      FacesTools.getResponse().setContentType("text/plain");
      try {
        String filename = this.file.getName();
        if (filename != null) {
          filename = filename.replace(" ", "_");
        } else {
          filename = "";
        }

        FacesTools.getResponse().setHeader(
            "Content-disposition",
            "attachment; filename=" + URLEncoder.encode(filename, "UTF-8") + "."
                + getChecksumAlgorithmAsString().toLowerCase());

        OutputStream out = FacesTools.getResponse().getOutputStream();
        out.write(file.getChecksum().getBytes("UTF-8"));
        out.flush();

        FacesTools.getCurrentInstance().responseComplete();
        out.close();
      } catch (Exception e) {
        error("Could not display checksum of file!");
        logger.error("Could not display checksum of file", e);
      }
    } else {
      error("Could not display checksum of file!");
      logger.error("File checksum is null");
    }

    return "";
  }

  /**
   * Current workaround for the detection if the current user belongs to the audience group of this
   * file. Gets the audience grants of the file object via filter method. Then it retrieves the user
   * groups of the grants, takes the org unit id of its selector and checks if the user belongs to
   * this org unit or if it is a child org unit of him.
   * 
   * @return
   */
  public boolean isFileAccessGranted() {
    return this.fileAccessGranted;
  }

  /**
   * Generate a string for displaying file sizes. Added by FrM to compute a better result for values
   * < 1024.
   * 
   * @param size The size of an uploaded file.
   * @return A string representing the file size in a readable format.
   */
  public String computeFileSize(long size) {
    if (size < 1024) {
      return size + getLabel("ViewItemMedium_lblFileSizeB");
    } else if (size < 1024 * 1024) {
      return ((size - 1) / 1024 + 1) + getLabel("ViewItemMedium_lblFileSizeKB");
    } else {
      return ((size - 1) / (1024 * 1024) + 1) + getLabel("ViewItemMedium_lblFileSizeMB");
    }
  }

  public String getOpenPDFSearchParameter() {
    return getOpenPDFSearchParameter(searchHits);
  }

  public static String getOpenPDFSearchParameter(List<SearchHitBean> shbList) {
    String param = "\"";
    List<String> searchWords = new ArrayList<String>();
    for (SearchHitBean shb : shbList) {
      if (!searchWords.contains(shb.getSearchHitString())) {
        searchWords.add(shb.getSearchHitString());
      }
    }

    for (String word : searchWords) {
      param += word + " ";
    }
    param = param.trim() + "\"";

    return param;
  }

  public boolean getIsLicenseUrl() {
    try {
      new URL(this.file.getDefaultMetadata().getLicense());
      return true;
    } catch (Exception e) {
    }

    return false;
  }
}
