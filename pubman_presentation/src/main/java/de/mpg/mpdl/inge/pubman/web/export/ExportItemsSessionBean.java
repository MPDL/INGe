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

package de.mpg.mpdl.inge.pubman.web.export;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerFactory.CitationTypes;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;

/**
 * Superclass for keeping the attributes used during the session by ExportItems.
 *
 * @author: Galina Stancheva, created 02.08.2007
 * @version: $Revision$ $LastChangedDate$ Revised by StG: 28.09.2007 - Comments for the get- and
 *           set-methods are missing! ToDo StG.
 */
@ManagedBean(name = "ExportItemsSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class ExportItemsSessionBean extends FacesBean {
  private static final Logger logger = LogManager.getLogger(ExportItemsSessionBean.class);

  private String exportDisplayData = "No export data available";

  private String message = null;

  private ExportFormatVO curExportFormatVO = null;


  private boolean enableFileFormats = false;
  private boolean enableExport = true;
  private boolean enableCslAutosuggest = false;

  // email properties
  private File attExportFile = null;
  private String attExportFileName = "ExportFile";
  private String exportEmailTxt = ExportItems.MESSAGE_EXPORT_EMAIL_TEXT;
  private String exportEmailSubject = ExportItems.MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT;
  private String exportEmailReplyToAddr = "";
  private String emailRecipients = "";
  private String emailCCRecipients = "";
  private String emailBCCRecipients = "";
  private String emailSenderProp = "";
  private String emailServernameProp = "";
  private String emailWithAuthProp = "";
  private String emailAuthUserProp = "";
  private String emailAuthPwdProp = "";
  private String citationStyleName = "";

  public ExportItemsSessionBean() {
    this.init();
  }

  public void init() {
    try {
      this.curExportFormatVO = new ExportFormatVO(TransformerFactory.ENDNOTE);
      this.emailSenderProp = PropertyReader.getProperty(PropertyReader.INGE_EMAIL_SENDER);
      this.emailServernameProp = PropertyReader.getProperty(PropertyReader.INGE_EMAIL_MAILSERVERNAME);
      this.emailWithAuthProp = PropertyReader.getProperty(PropertyReader.INGE_EMAIL_WITHAUTHENTICATION);
      this.emailAuthUserProp = PropertyReader.getProperty(PropertyReader.INGE_EMAIL_AUTHENTICATIONUSER);
      this.emailAuthPwdProp = PropertyReader.getProperty(PropertyReader.INGE_EMAIL_AUTHENTICATIONPWD);
    } catch (final Exception e) {
      ExportItemsSessionBean.logger.warn("Propertyfile not readable for emailserver  properties'");
    }
  }

  public ExportFormatVO getCurExportFormatVO() {
    return this.curExportFormatVO;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getExportFormatName() {
    return this.curExportFormatVO.getFormat();
  }

  public void setExportFormatName(String exportFormatName) {
    FORMAT format = TransformerFactory.getFormat(exportFormatName);
    String citations = curExportFormatVO.getCitationName();

    this.curExportFormatVO.setFormat(exportFormatName);

    this.setEnableFileFormats(false);
    this.setEnableCslAutosuggest(false);

    if (TransformerFactory.VALID_CITATION_OUTPUT.contains(format)) {
      this.setEnableFileFormats(true);
      if (CitationTypes.CSL.getCitationName().equals(citations)) {
        this.setEnableCslAutosuggest(true);
      } else {
        this.curExportFormatVO.setId(null);
      }
    } else {
      this.curExportFormatVO.setCitationName(null);
      this.curExportFormatVO.setId(null);
    }
  }

  public String getFileFormat() {
    return this.curExportFormatVO.getCitationName();
  }

  public void setFileFormat(String fileFormatName) {
    this.curExportFormatVO.setCitationName(fileFormatName);
  }


  // ////////////////////////////////////////////////////////////////////////////////////////7
  // next methods are used by EMAIL-ing

  public void setAttExportFile(File attFile) {
    this.attExportFile = new File(attFile.toURI());
  }

  public File getAttExportFile() {
    return this.attExportFile;
  }

  public void setCitationStyleName(String citationStyleName) {
    this.citationStyleName = citationStyleName;
  }

  public String getCitationStyleName() {
    return this.citationStyleName;
  }

  public void setConeCitationStyleId(String citationStyleId) {
    this.curExportFormatVO.setId(citationStyleId);
  }

  public String getConeCitationStyleId() {
    return this.curExportFormatVO.getId();
  }

  public void setAttExportFileName(String fileName) {
    this.attExportFileName = fileName;
  }

  public String getAttExportFileName() {
    return this.attExportFileName;
  }

  public void setExportEmailSubject(String exportEmailSubject) {
    this.exportEmailSubject = exportEmailSubject;
  }

  public String getExportEmailSubject() {
    return this.exportEmailSubject;
  }

  public void setExportEmailTxt(String exportEmailTxt) {
    this.exportEmailTxt = exportEmailTxt;
  }

  public String getExportEmailTxt() {
    return this.exportEmailTxt;
  }

  public void setExportEmailReplyToAddr(String exportEmailReplyToAddr) {
    this.exportEmailReplyToAddr = exportEmailReplyToAddr;
  }

  public String getExportEmailReplyToAddr() {
    return this.exportEmailReplyToAddr;
  }

  public void setEmailRecipients(String emailRecipients) {
    this.emailRecipients = emailRecipients;
  }

  public String getEmailRecipients() {
    return this.emailRecipients;
  }

  public void setEmailSenderProp(String emailSender) {
    this.emailSenderProp = emailSender;
  }

  public String getEmailSenderProp() {
    return this.emailSenderProp;
  }

  public void setEmailServernameProp(String name) {}

  public String getEmailServernameProp() {
    return this.emailServernameProp;
  }

  public void setEmailWithAuthProp(String trueorfalse) {
    this.emailWithAuthProp = trueorfalse;
  }

  public String getEmailWithAuthProp() {
    return this.emailWithAuthProp;
  }

  public void setEmailAuthUserProp(String user) {
    this.emailAuthUserProp = user;
  }

  public String getEmailAuthUserProp() {
    return this.emailAuthUserProp;
  }

  public void setEmailAuthPwdProp(String user) {
    this.emailAuthPwdProp = user;
  }

  public String getEmailAuthPwdProp() {
    return this.emailAuthPwdProp;
  }

  public boolean getEnableCslAutosuggest() {
    return this.enableCslAutosuggest;
  }

  public void setEnableCslAutosuggest(boolean enableCslAutosuggest) {
    this.enableCslAutosuggest = enableCslAutosuggest;
  }

  public void setExportDisplayData(String data) {
    this.exportDisplayData = data;
  }

  public String getExportDisplayData() {
    return this.exportDisplayData;
  }

  public boolean getEnableFileFormats() {
    return this.enableFileFormats;
  }

  public void setEnableFileFormats(boolean enableFileFormats) {
    this.enableFileFormats = enableFileFormats;
  }

  public boolean getEnableExport() {
    return this.enableExport;
  }

  public void setEnableExport(boolean enableExport) {
    this.enableExport = enableExport;
  }

  public String getEmailCCRecipients() {
    return this.emailCCRecipients;
  }

  public void setEmailCCRecipients(String emailCCRecipients) {
    this.emailCCRecipients = emailCCRecipients;
  }

  public String getEmailBCCRecipients() {
    return this.emailBCCRecipients;
  }

  public void setEmailBCCRecipients(String emailBCCRecipients) {
    this.emailBCCRecipients = emailBCCRecipients;
  }

}
