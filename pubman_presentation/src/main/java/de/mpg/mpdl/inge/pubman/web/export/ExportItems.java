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

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.xmltransforming.EmailService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;

/**
 * Fragment class for item exporting. This class provides all functionality for exporting items
 * according the selected export format (layout or structured) and the selected file format (PDF,
 * TXT, etc..).
 *
 * @author: Galina Stancheva, created 02.08.2007
 * @version: $Revision$ $LastChangedDate$ Revised by StG: 28.09.2007
 */
@ManagedBean(name = "ExportItems")
@SessionScoped
@SuppressWarnings("serial")
public class ExportItems extends FacesBean {
  private static final Logger logger = LogManager.getLogger(ExportItems.class);

  public static final String MESSAGE_EXPORT_EMAIL_RECIPIENTS_ARE_NOT_DEFINED = "exportItems_RecipientsAreNotDefined";
  public static final String MESSAGE_EXPORT_EMAIL_SENT = "exportItems_EmailSent";
  public static final String MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT = "exportItems_EmailSubjectText";
  public static final String MESSAGE_EXPORT_EMAIL_TEXT = "exportItems_EmailText";
  public static final String MESSAGE_EXPORT_EMAIL_UNKNOWN_RECIPIENTS = "exportItems_UnknownRecipients";
  public static final String MESSAGE_NO_EXPORTDATA_DELIVERED = "exportItems_NoDataDelivered";
  public static final String MESSAGE_NO_ITEM_FOREXPORT_SELECTED = "exportItems_NoItemSelected";

  public ExportItems() {}

  public SelectItem[] getEXPORTFORMAT_OPTIONS() {
    // constants for comboBoxes and HtmlSelectOneRadios

    SelectItemGroup exportGroup = new SelectItemGroup(this.getLabel("Export_ExportFormat_BibFormatGroup"));

    final SelectItem EXPORTFORMAT_MARCXML = new SelectItem(TransformerFactory.MARC_XML, this.getLabel("Export_ExportFormat_MARCXML"));
    final SelectItem EXPORTFORMAT_ENDNOTE = new SelectItem(TransformerFactory.ENDNOTE, this.getLabel("Export_ExportFormat_ENDNOTE"));
    final SelectItem EXPORTFORMAT_BIBTEX = new SelectItem(TransformerFactory.BIBTEX, this.getLabel("Export_ExportFormat_BIBTEX"));
    final SelectItem EXPORTFORMAT_ESCIDOC_XML =
        new SelectItem(TransformerFactory.ESCIDOC_ITEMLIST_XML, this.getLabel("Export_ExportFormat_ESCIDOC_XML"));
    final SelectItem EXPORTFORMAT_JSON = new SelectItem(TransformerFactory.JSON, this.getLabel("Export_ExportFormat_JSON"));

    exportGroup.setSelectItems(
        new SelectItem[] {EXPORTFORMAT_ENDNOTE, EXPORTFORMAT_BIBTEX, EXPORTFORMAT_JSON, EXPORTFORMAT_ESCIDOC_XML, EXPORTFORMAT_MARCXML});


    SelectItemGroup citationGroup = new SelectItemGroup(this.getLabel("Export_ExportFormat_CitationGroup"));
    final SelectItem FILEFORMAT_JSON_CITATION =
        new SelectItem(TransformerFactory.JSON_CITATION, this.getLabel("Export_ExportFormat_JSON_CITATION"));
    final SelectItem FILEFORMAT_PDF = new SelectItem(TransformerFactory.PDF, this.getLabel("Export_FileFormat_PDF"));
    final SelectItem FILEFORMAT_DOCX = new SelectItem(TransformerFactory.DOCX, this.getLabel("Export_FileFormat_DOCX"));
    final SelectItem FILEFORMAT_HTML_PLAIN = new SelectItem(TransformerFactory.HTML_PLAIN, this.getLabel("Export_FileFormat_HTML_PLAIN"));
    final SelectItem FILEFORMAT_HTML_LINKED =
        new SelectItem(TransformerFactory.HTML_LINKED, this.getLabel("Export_FileFormat_HTML_LINKED"));
    final SelectItem FILEFORMAT_ESCIDOC_SNIPPET =
        new SelectItem(TransformerFactory.ESCIDOC_SNIPPET, this.getLabel("Export_FileFormat_ESCIDOC_SNIPPET"));

    citationGroup.setSelectItems(new SelectItem[] {FILEFORMAT_PDF, FILEFORMAT_DOCX, FILEFORMAT_HTML_PLAIN, FILEFORMAT_HTML_LINKED,
        FILEFORMAT_JSON_CITATION, FILEFORMAT_ESCIDOC_SNIPPET});

    final SelectItem[] EXPORTFORMAT_OPTIONS = new SelectItem[] { //
        exportGroup, citationGroup};

    return EXPORTFORMAT_OPTIONS;
  }


  public SelectItem[] getCITATION_OPTIONS() {

    final SelectItem EXPORTFORMAT_APA =
        new SelectItem(TransformerFactory.CitationTypes.APA.getCitationName(), this.getLabel("Export_ExportFormat_APA"));
    final SelectItem EXPORTFORMAT_APA_CJK =
        new SelectItem(TransformerFactory.CitationTypes.APA_CJK.getCitationName(), this.getLabel("Export_ExportFormat_APA_CJK"));
    final SelectItem EXPORTFORMAT_AJP =
        new SelectItem(TransformerFactory.CitationTypes.AJP.getCitationName(), this.getLabel("Export_ExportFormat_AJP"));
    final SelectItem EXPORTFORMAT_JUS =
        new SelectItem(TransformerFactory.CitationTypes.JUS.getCitationName(), this.getLabel("Export_ExportFormat_JUS"));
    final SelectItem EXPORTFORMAT_CSL =
        new SelectItem(TransformerFactory.CitationTypes.CSL.getCitationName(), this.getLabel("Export_ExportFormat_CSL"));

    final SelectItem[] FILEFORMAT_OPTIONS;

    FILEFORMAT_OPTIONS = new SelectItem[] { //
        EXPORTFORMAT_APA, //
        EXPORTFORMAT_APA_CJK, //
        EXPORTFORMAT_AJP, //
        EXPORTFORMAT_JUS, //
        EXPORTFORMAT_CSL};

    return FILEFORMAT_OPTIONS;
  }

  private ExportItemsSessionBean getExportItemsSessionBean() {
    return FacesTools.findBean("ExportItemsSessionBean");
  }

  /*
   * Updates the GUI relatively the selected export format.
   */
  public void updateExportFormats() {
    final ExportItemsSessionBean sb = this.getExportItemsSessionBean();

    final ExportFormatVO exportFormat = sb.getCurExportFormatVO();
    //    final String selExportFormat = sb.getExportFormatName();
    sb.setExportFormatName(exportFormat.getFormat());


  }

  // /////////////////////////////////////////////////////////////////////////////////////
  // /////// next methods are used by EMailing

  /**
   * Clean up some fields on the Email interface
   */
  private void cleanUpEmailFields() {
    final ExportItemsSessionBean sb = this.getExportItemsSessionBean();
    // To
    sb.setEmailRecipients(null);
    // CC
    sb.setEmailCCRecipients(null);
    // ReplyTo
    sb.setExportEmailReplyToAddr(null);
  }

  public String sendEMail() {
    String status = "not sent";
    final String smtpHost = this.getExportItemsSessionBean().getEmailServernameProp();
    final String withAuth = this.getExportItemsSessionBean().getEmailWithAuthProp();
    final String usr = this.getExportItemsSessionBean().getEmailAuthUserProp();
    final String pwd = this.getExportItemsSessionBean().getEmailAuthPwdProp();
    final String senderAddress = this.getExportItemsSessionBean().getEmailSenderProp();// someone@web.de
    final String subject = this.getExportItemsSessionBean().getExportEmailSubject();
    final String text = this.getExportItemsSessionBean().getExportEmailTxt();
    final String[] replyToAddresses = new String[] {this.getExportItemsSessionBean().getExportEmailReplyToAddr()};
    final String[] attachments = new String[] {this.getExportItemsSessionBean().getAttExportFile().getPath()};
    final String recipientsAddressesStr = this.getExportItemsSessionBean().getEmailRecipients();
    final String recipientsCCAddressesStr = this.getExportItemsSessionBean().getEmailCCRecipients();

    String[] recipientsAddresses = null;
    boolean OK = false;
    if (recipientsAddressesStr != null && !recipientsAddressesStr.trim().isEmpty()) {
      recipientsAddresses = recipientsAddressesStr.split(",");
      for (final String ra : recipientsAddresses) {
        if (!ra.trim().isEmpty()) {
          OK = true;
          break;
        }
      }
    }

    if (!OK) {
      this.error(this.getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_RECIPIENTS_ARE_NOT_DEFINED));
      return null;
    }

    final String[] recipientsCCAddresses = recipientsCCAddressesStr.split(",");

    try {
      status = EmailService.sendMail(smtpHost, withAuth, usr, pwd, senderAddress, recipientsAddresses, recipientsCCAddresses, null,
          replyToAddresses, subject, text, attachments);
      this.cleanUpEmailFields();
    } catch (final TechnicalException e) {
      ExportItems.logger.error("Could not send the export formats." + "\n" + e);
      // normal
      final Throwable ecc = e.getCause().getCause();
      /*
      if (ecc != null && ecc instanceof com.sun.mail.smtp.SMTPAddressFailedException) {
        this.error(this.getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_UNKNOWN_RECIPIENTS));
        return null;
      }
      */


      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
      return ErrorPage.LOAD_ERRORPAGE;
    }

    if (status.equals("sent")) {
      ExportItems.logger.debug(ExportItems.MESSAGE_EXPORT_EMAIL_SENT);
      this.info(this.getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_SENT));

      // redirect to last breadcrumb
      final BreadcrumbItemHistorySessionBean bhsb = FacesTools.findBean("BreadcrumbItemHistorySessionBean");
      try {
        FacesTools.getExternalContext().redirect(bhsb.getPreviousItem().getPage());
      } catch (final IOException e) {
        this.error(this.getMessage("NoRedirect"));
      }

      return "";
    }

    return status;
  }
}
