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

package de.mpg.mpdl.inge.pubman.web.viewItem;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO.OA_STATUS;
import de.mpg.mpdl.inge.model.xmltransforming.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.service.aa.AuthorizationService.AccessType;
import de.mpg.mpdl.inge.service.aa.IpListProvider;
import de.mpg.mpdl.inge.service.aa.IpListProvider.IpRange;

/**
 * Bean for storing the information of files attached to items.
 * 
 * @author: Tobias Schraut, created 25.03.2008
 * @version: $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class FileBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(FileBean.class);

  private FileDbVO file;
  private List<String> searchHits = new ArrayList<String>();
  private final ItemVersionVO item;
  private boolean fileAccessGranted = false;

  /**
   * Public constructor with parameters
   * 
   * @param file
   * @param position
   * @param itemState
   */
  public FileBean(FileDbVO file, ItemVersionVO item) {
    this.file = file;
    this.item = item;
    this.initializeFileAccessGranted();

  }

  /**
   * Second constructor (used if pubitem has fulltext search hits)
   * 
   * @param file
   * @param position
   * @param itemState
   * @param resultitem
   */
  public FileBean(FileDbVO file, ItemVersionVO item, List<String> searchHitList) {
    this.file = file;
    this.item = item;
    this.searchHits = searchHitList;
    //this.initialize(file, item, searchHitList);
    this.initializeFileAccessGranted();

  }

  /**
   * Sets up some extra information concerning full text search hits
   * 
   * @param file
   * @param position
   * @param itemState
   * @param resultitem
   */
  /*
  protected void initialize(FileDbVO file, ItemVersionVO item, List<String> searchHitList) {
    // set some html elements which cannot be completely constructed in the jsp
  
    String beforeSearchHitString;
    String searchHitString;
    String afterSearchHitString;
  
    // browse through the list of files and examine which of the files is the one the search result
    // hits where found in
    for (int i = 0; i < searchHitList.size(); i++) {
      if (searchHitList.get(i).getType() == SearchHitType.FULLTEXT) {
        if (searchHitList.get(i).getHitReference() != null) {
          if (searchHitList.get(i).getHitReference().getObjectId().equals(this.file.getObjectId())) {
            for (int j = 0; j < searchHitList.get(i).getTextFragmentList().size(); j++) {
              int startPosition = 0;
              int endPosition = 0;
  
              startPosition = searchHitList.get(i).getTextFragmentList().get(j).getHitwordList().get(0).getStartIndex();
              endPosition = searchHitList.get(i).getTextFragmentList().get(j).getHitwordList().get(0).getEndIndex() + 1;
  
              beforeSearchHitString = "..." + searchHitList.get(i).getTextFragmentList().get(j).getData().substring(0, startPosition);
              searchHitString = searchHitList.get(i).getTextFragmentList().get(j).getData().substring(startPosition, endPosition);
              afterSearchHitString = searchHitList.get(i).getTextFragmentList().get(j).getData().substring(endPosition) + "...";
  
              this.searchHits.add(new SearchHitBean(beforeSearchHitString, searchHitString, afterSearchHitString));
            }
          }
  
        }
  
      }
  
    }
  
  }
  */

  private void initializeFileAccessGranted() {
    // examine weather the user holds an audience Grant for the current file or not
    //TODO
    if (this.file.getObjectId() != null && this.file.getVisibility().equals(FileDbVO.Visibility.AUDIENCE)) {
      try {
        fileAccessGranted =
            ApplicationBean.INSTANCE.getFileService().checkAccess(AccessType.READ_FILE, getLoginHelper().getPrincipal(), item, file);
      } catch (Exception e) {
        fileAccessGranted = false;
      }


    }

    /*
    
    try {
      if (this.file.getReference() != null && this.file.getVisibility().equals(FileDbVO.Visibility.AUDIENCE)) {
        final UserAccountHandler uah = ServiceLocator.getUserAccountHandler(this.getLoginHelper().getAccountUser().getHandle());
    
        final FilterTaskParamVO filter = new FilterTaskParamVO();
    
        final Filter accountUserFilter = filter.new StandardFilter("http://escidoc.de/core/01/properties/user",
            this.getLoginHelper().getAccountUser().getReference().getObjectId(), "=", "AND");
        filter.getFilterList().add(accountUserFilter);
    
        final Filter notAudienceRoleFilter =
            filter.new StandardFilter("/properties/role/id", GrantVO.PredefinedRoles.AUDIENCE.frameworkValue(), "=", "AND");
        filter.getFilterList().add(notAudienceRoleFilter);
    
        final Filter assignedOnFilter = filter.new StandardFilter("http://escidoc.de/core/01/properties/assigned-on",
            this.file.getReference().getObjectId(), "=", "AND");
        filter.getFilterList().add(assignedOnFilter);
    
        final Filter notRevokedFilter = filter.new StandardFilter("/properties/revocation-date", "\"\"", "=", "AND");
        filter.getFilterList().add(notRevokedFilter);
    
        final String userGrantXML = uah.retrieveGrants(filter.toMap());
        final SearchRetrieveResponseVO searchResult = XmlTransformingService.transformToSearchRetrieveResponseGrantVO(userGrantXML);
        if (searchResult.getNumberOfRecords() > 0) {
          this.fileAccessGranted = true;
        } else {
          this.fileAccessGranted = false;
        }
      } else {
        this.fileAccessGranted = false;
      }
    } catch (final Exception e) {
      FileBean.logger.error("Problem getting audience-grants for file [" + this.file.getReference().getObjectId() + "]", e);
    }
    */
  }

  //  public void downloadFile() {
  //    try {
  //      final String fileLocation = PropertyReader.getFrameworkUrl() + this.file.getContent();
  //      String filename = this.file.getName(); // Filename suggested in browser Save As dialog
  //      filename = filename.replace(" ", "_"); // replace empty spaces because they cannot be procesed
  //                                             // by the http-response (filename will be cutted after
  //                                             // the first empty space)
  //      final String contentType = this.file.getMimeType(); // For dialog, try
  //
  //      // application/x-download
  //      FacesTools.getResponse().setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
  //      if (this.file.getMetadata() != null) {
  //        FacesTools.getResponse().setContentLength(this.file.getMetadata().getSize());
  //      }
  //
  //      FacesTools.getResponse().setContentType(contentType);
  //
  //      byte[] buffer = null;
  //      if (this.file.getMetadata() != null) {
  //        try {
  //          final GetMethod method = new GetMethod(fileLocation);
  //          method.setFollowRedirects(false);
  //          if (this.getLoginHelper().getESciDocUserHandle() != null) {
  //            // downloading by account user
  //            this.addHandleToMethod(method, this.getLoginHelper().getESciDocUserHandle());
  //          }
  //
  //          // Execute the method with HttpClient.
  //          final HttpClient client = new HttpClient();
  //          ProxyHelper.setProxy(client, fileLocation); // ????
  //          client.executeMethod(method);
  //          final OutputStream out = FacesTools.getResponse().getOutputStream();
  //          final InputStream input = method.getResponseBodyAsStream();
  //          try {
  //            if (this.file.getMetadata() != null) {
  //              buffer = new byte[this.file.getMetadata().getSize()];
  //              int numRead;
  //              // long numWritten = 0;
  //              while ((numRead = input.read(buffer)) != -1) {
  //                out.write(buffer, 0, numRead);
  //                out.flush();
  //                // numWritten += numRead;
  //              }
  //              FacesTools.getCurrentInstance().responseComplete();
  //            }
  //          } catch (final IOException e1) {
  //            FileBean.logger.debug("Download IO Error: " + e1.toString());
  //          }
  //          input.close();
  //          out.close();
  //        } catch (final FileNotFoundException e) {
  //          FileBean.logger.debug("File not found: " + e.toString());
  //        }
  //      }
  //    } catch (final Exception e) {
  //      FileBean.logger.debug("File Download Error: " + e.toString());
  //    }
  //  }

  //  /**
  //   * Adds a cookie named "escidocCookie" that holds the eScidoc user handle to the provided http
  //   * method object.
  //   * 
  //   * @author Tobias Schraut
  //   * @param method The http method to add the cookie to.
  //   */
  //  private void addHandleToMethod(final HttpMethod method, String eSciDocUserHandle) {
  //    // Staging file resource is protected, access needs authentication and
  //    // authorization. Therefore, the eSciDoc user handle must be provided.
  //    // Put the handle in the cookie "escidocCookie"
  //    method.setRequestHeader("Cookie", "escidocCookie=" + eSciDocUserHandle);
  //  }

  /**
   * Returns the content category.
   * 
   * @return The internationalized content-category.
   */
  public String getContentCategory() {
    if (this.file.getMetadata().getContentCategory() != null) {
      return file.getMetadata().getContentCategory();
    }

    return "";
  }

  /**
   * Returns an internationalized String for the file's content category.
   * 
   * @return The internationalized content-category.
   */
  public String getContentCategoryLabel() {
    if (this.file.getMetadata().getContentCategory() != null) {
      return this.getLabel("ENUM_CONTENTCATEGORY_" + file.getMetadata().getContentCategory().toLowerCase().replace("_", "-"));
      /*
       * /* for (final Entry<String, String> contcat : PubFileVOPresentation.getContentCategoryMap()
       * .entrySet()) { if (contcat.getValue().equals(this.file.getContentCategory())) { return
       * this.getLabel("ENUM_CONTENTCATEGORY_" + contcat.getKey().toLowerCase().replace("_", "-"));
       * } }
       */
    }

    return "";
  }

  public String getVisibility() {
    if (this.file.getVisibility() != null) {
      return this.getLabel(this.getI18nHelper().convertEnumToString(this.file.getVisibility()));
    }

    return "";
  }

  public boolean getItemWithdrawn() {
    return ItemVersionRO.State.WITHDRAWN.equals(item.getObject().getPublicState());

  }

  public boolean getShowSearchHits() {
    if (this.searchHits != null && this.searchHits.size() > 0) {
      return true;
    }

    return false;
  }

  public FileDbVO getFile() {
    return this.file;
  }

  public String getFileName() {
    return file.getName();
    /*
    if (this.file.getMetadata() != null && this.file.getMetadata().getTitle() != null) {
      return this.file.getMetadata().getTitle();
    }
    
    return "";
    */
  }


  public String getFileDescription() {
    if (this.file.getMetadata() != null && this.file.getMetadata().getDescription() != null) {
      return this.file.getMetadata().getDescription();
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
    if (this.file.getMetadata() != null && this.file.getMetadata().getTitle() != null) {
      return this.file.getMetadata().getTitle();
    }

    return "";
  }

  public String getLocatorLink() {
    return this.file.getContent();
  }

  public void setFile(FileDbVO file) {
    this.file = file;
  }

  public String getFileSize() {
    return this.computeFileSize(file.getSize());
  }

  public List<String> getSearchHits() {
    return this.searchHits;
  }

  public void setSearchHits(List<String> searchHits) {
    this.searchHits = searchHits;
  }

  public int getNumberOfSearchHits() {
    return this.searchHits.size();
  }

  public boolean getLocatorIsLink() {
    return ((this.getFile().getStorage() == FileDbVO.Storage.EXTERNAL_URL) //
        && (this.getFile().getContent().startsWith("http://") || this.getFile().getContent().startsWith("https://")
            || this.getFile().getContent().startsWith("ftp://")));
  }

  public boolean getIsVisible() {
    if (this.file.getVisibility().equals(FileDbVO.Visibility.PUBLIC)) {
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
      if (this.file.getMetadata() != null && this.file.getMetadata().getLicense() != null) {
        final String licenceURL = this.file.getMetadata().getLicense().toLowerCase();

        if (licenceURL != null && !licenceURL.trim().equals("") && licenceURL.indexOf("creative") > -1
            && licenceURL.indexOf("commons") > -1) {
          final String[] splittedURL = licenceURL.split("\\/");
          // Change for dettecting license url in a string
          int start = 0;
          for (int i = 0; i < splittedURL.length; i++) {
            final String part = splittedURL[i];
            if (part.startsWith("creativecommons")) {
              start = i;
            }
          }

          final String address = splittedURL[start];
          final String licenses = "l";
          final String type = splittedURL[start + 2];
          final String version = splittedURL[start + 3];
          final String image = "80x15.png";

          return "https://i." + address + "/" + licenses + "/" + type + "/" + version + "/" + image;
        }
      }
    } catch (final Exception e) {
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
    if (this.file.getVisibility().equals(FileDbVO.Visibility.PRIVATE)) {
      return true;
    }

    return false;
  }

  public void setUpdateVisibility(ValueChangeEvent event) {
    final Visibility newVisibility = (Visibility) event.getNewValue();
    this.file.setVisibility(newVisibility);
  }

  public String getOaStatus() {
    if (this.file.getMetadata() != null && this.file.getMetadata().getOaStatus() != null) {
      return this.getLabel(this.getI18nHelper().convertEnumToString(this.file.getMetadata().getOaStatus()));
    }

    return "";
  }

  public void setUpdateOaStatus(ValueChangeEvent event) {
    final OA_STATUS newOaStatus = (OA_STATUS) event.getNewValue();
    this.file.getMetadata().setOaStatus(newOaStatus);
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
  public void displayChecksum() {
    if (this.file.getChecksum() != null && this.file.getChecksumAlgorithm() != null) {
      FacesTools.getResponse().setContentLength(this.file.getChecksum().length());
      FacesTools.getResponse().setContentType("text/plain");
      try {
        String filename = this.file.getName();
        if (filename != null) {
          filename = filename.replace(" ", "_");
        } else {
          filename = "";
        }

        FacesTools.getResponse().setHeader("Content-disposition",
            "attachment; filename=" + URLEncoder.encode(filename, "UTF-8") + "." + this.getChecksumAlgorithmAsString().toLowerCase());

        final OutputStream out = FacesTools.getResponse().getOutputStream();
        out.write(this.file.getChecksum().getBytes("UTF-8"));
        out.flush();

        FacesTools.getCurrentInstance().responseComplete();
        out.close();
      } catch (final Exception e) {
        this.error(this.getMessage("File_noCheckSum"));
        FileBean.logger.error("Could not display checksum of file", e);
      }
    } else {
      this.error(this.getMessage("File_noCheckSum"));
      FileBean.logger.error("File checksum is null");
    }
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
      return size + this.getLabel("ViewItemMedium_lblFileSizeB");
    } else if (size < 1024 * 1024) {
      return ((size - 1) / 1024 + 1) + this.getLabel("ViewItemMedium_lblFileSizeKB");
    } else {
      return ((size - 1) / (1024 * 1024) + 1) + this.getLabel("ViewItemMedium_lblFileSizeMB");
    }
  }

  public String getOpenPDFSearchParameter() {
    return FileBean.getOpenPDFSearchParameter(this.searchHits);
  }

  public static String getOpenPDFSearchParameter(List<String> shbList) {
    String param = "\"";
    final List<String> searchWords = new ArrayList<String>();
    for (final String shb : shbList) {
      if (!searchWords.contains(shb)) {
        searchWords.add(shb);
      }
    }

    for (final String word : searchWords) {
      param += word + " ";
    }
    param = param.trim() + "\"";

    return param;
  }

  public boolean getIsLicenseUrl() {
    try {
      new URL(this.file.getMetadata().getLicense());
      return true;
    } catch (final Exception e) {
    }

    return false;
  }

  public String getAudienceOrganizations() {
    if (Visibility.AUDIENCE.equals(file.getVisibility())) {
      IpListProvider ipListProvider = ApplicationBean.INSTANCE.getIpListProvider();
      StringBuilder sb = new StringBuilder();
      if (file.getAllowedAudienceIds() != null) {
        for (String audienceId : file.getAllowedAudienceIds()) {
          IpRange ipRange = ipListProvider.get(audienceId);
          if (ipRange != null) {
            sb.append(ipRange.getName());
          } else {
            sb.append("UNKNOWN id " + audienceId);
          }

          sb.append("; ");
        }
        return sb.toString();
      }
    }
    return null;
  }
}
