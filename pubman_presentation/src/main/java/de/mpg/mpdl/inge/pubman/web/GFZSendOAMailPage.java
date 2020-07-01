package de.mpg.mpdl.inge.pubman.web;

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.xmltransforming.EmailService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.export.ExportItems;
import de.mpg.mpdl.inge.pubman.web.export.ExportItemsSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;
import de.mpg.mpdl.inge.util.PropertyReader;


@ManagedBean(name = "GFZSendOAMailPage")
@ViewScoped
@SuppressWarnings("serial")
public class GFZSendOAMailPage extends BreadcrumbPage {

  private static final Logger logger = Logger.getLogger(GFZSendOAMailPage.class);

  public static final String MESSAGE_OPENACCESS_EMAIL_SENT = "oaMail_success";
  public static final String MESSAGE_NO_GFZAUTHOR = "oaMail_noGFZAuthors";
  public static final String MESSAGE_NO_RECIPENTS = "oaMail_noRecipents";

  public static final String LOAD_SENDOAMAIL = "loadGfzSendOAMail";

  private boolean hasGFZAuthor = false;
  private String firstGFZAuthorMailAdress;
  private String itemID;
  private String ccAddress;

  private String emailSubject;
  private String replyToAddr;
  private String emailText;
  private String citationHtml;
  private String oaMail;
  private String oaMailTemplate;
  private String oaMailDomain;

  private ExportItemsSessionBean exportItemsSessionBean;

  private VelocityContext velocityContext;
  private VelocityEngine velocityEngine;
  private Template velocityTemplate;

  public String sendOAMailPage() {
    return GFZSendOAMailPage.LOAD_SENDOAMAIL;
  }

  public GFZSendOAMailPage() {}

  @Override
  public boolean isItemSpecific() {
    return false;
  }

  @Override
  public void init() {
    try {
      super.init();

      ViewItemFull item = (ViewItemFull) FacesTools.findBean("ViewItemFull");
      this.velocityEngine = new VelocityEngine();
      this.velocityEngine.setProperty("resource.loader", "class");
      this.velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
      this.velocityEngine.setProperty("output.encoding", "UTF-8");
      this.velocityEngine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");

      this.velocityEngine.init();
      this.velocityContext = new VelocityContext();

      //Reset Session Values
      this.hasGFZAuthor = false;
      this.firstGFZAuthorMailAdress = null;

      this.itemID = item.getPubItem().getVersionPid();
      this.exportItemsSessionBean = (ExportItemsSessionBean) FacesTools.findBean("ExportItemsSessionBean");

      this.oaMail = PropertyReader.getProperty(PropertyReader.GFZ_OA_MAIL_ADRESS);
      this.emailSubject = PropertyReader.getProperty(PropertyReader.GFZ_OA_MAIL_SUBJECT);
      this.oaMailTemplate = PropertyReader.getProperty(PropertyReader.GFZ_OA_MAIL_TEMPLATE);
      this.oaMailDomain = PropertyReader.getProperty(PropertyReader.GFZ_OA_MAIL_DOMAIN);
      this.replyToAddr = oaMail;
      this.ccAddress = oaMail;

      for (CreatorVO c : item.getPubItem().getMetadata().getCreators()) {
        if (c.getPerson() != null && c.getPerson().getIdentifier() != null && c.getPerson().getIdentifier().getType() != null
            && c.getPerson().getIdentifier().getType().equals(IdType.CONE)) {
          this.hasGFZAuthor = true;
          this.firstGFZAuthorMailAdress = getGFZEmailAddress(c.getPerson().getIdentifier().getId());
          break;
        }
      }

      citationHtml = item.getCitationHtml();
      setEmailText(formEmailText());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*Taken from ExportItems.sendEMail()*/
  public String sendEMail() {
    GFZSendOAMailPage.logger.info("Sending OpenAccess Email...");

    if (hasGFZAuthor) {
      String status = "not sent";
      String smtpHost = exportItemsSessionBean.getEmailServernameProp();
      String withAuth = exportItemsSessionBean.getEmailWithAuthProp();
      String usr = exportItemsSessionBean.getEmailAuthUserProp();
      String pwd = exportItemsSessionBean.getEmailAuthPwdProp();
      String senderAddress = exportItemsSessionBean.getEmailSenderProp();
      String subject = getEmailSubject();
      String text = getEmailText();
      String[] replyToAddresses = new String[] {getReplyToAddr()};
      String recipientsAddressesStr = getFirstGFZAuthorMailAdress();
      String recipientsCCAddressesStr = getCcAddress();

      String[] recipientsAddresses = null;
      boolean OK = false;
      if (recipientsAddressesStr != null && !recipientsAddressesStr.trim().equals("")) {
        recipientsAddresses = recipientsAddressesStr.split(",");
        FOR: for (String ra : recipientsAddresses) {
          if (!ra.trim().equals("")) {
            OK = true;
            break FOR;
          }
        }
      }

      if (!OK) {
        this.error(getMessage(MESSAGE_NO_RECIPENTS));
        return null;
      }

      String[] recipientsCCAddresses = recipientsCCAddressesStr.split(",");

      try {
        status = EmailService.sendHtmlMail(smtpHost, withAuth, usr, pwd, senderAddress, recipientsAddresses, recipientsCCAddresses, null,
            replyToAddresses, subject, text);
        GFZSendOAMailPage.logger.info("Sent OpenAccess Email.");
      } catch (TechnicalException e) {
        GFZSendOAMailPage.logger.error("Could not send OpenAccess Email." + "\n" + e.toString());
        //normal 
        Throwable ecc = e.getCause().getCause();
        if (ecc != null && ecc instanceof com.sun.mail.smtp.SMTPAddressFailedException) {
          this.error(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_UNKNOWN_RECIPIENTS));
          return null;
        }

        ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
        return ErrorPage.LOAD_ERRORPAGE;
      }

      if (status.equals("sent")) {
        this.info(getMessage(MESSAGE_OPENACCESS_EMAIL_SENT));
        //redirect to last breadcrumb
        BreadcrumbItemHistorySessionBean bhsb = (BreadcrumbItemHistorySessionBean) FacesTools.findBean("BreadcrumbItemHistorySessionBean");
        try {
          FacesTools.getExternalContext().redirect(FacesTools.getRequest().getContextPath() + "/faces/" + bhsb.getPreviousItem().getPage());
        } catch (IOException e) {
          this.error("Could not redirect!");
        }
        return "";
      }
      return status;
    } else {
      this.error(getMessage(MESSAGE_NO_GFZAUTHOR));
      return "";
    }
  }

  public String getEmailText() {
    return this.emailText;
  }

  private String formEmailText() {
    this.velocityTemplate = velocityEngine.getTemplate(this.oaMailTemplate, "UTF-8");
    this.velocityContext.put("citation", citationHtml);
    StringWriter writer = new StringWriter();
    this.velocityTemplate.merge(velocityContext, writer);

    return writer.toString();
  }

  public void setEmailText(String emailText) {
    this.emailText = emailText;
  }

  private String getGFZEmailAddress(String coneIdentifier) {
    return coneIdentifier.substring(coneIdentifier.lastIndexOf('/') + 1, coneIdentifier.length()).concat(this.oaMailDomain);
  }

  public boolean isHasGFZAuthor() {
    return this.hasGFZAuthor;
  }

  public void setHasGFZAuthor(boolean hasGFZAuthor) {
    this.hasGFZAuthor = hasGFZAuthor;
  }

  public String getFirstGFZAuthorMailAdress() {
    return this.firstGFZAuthorMailAdress;
  }

  public void setFirstGFZAuthorMailAdress(String firstGFZAuthorMailAdress) {
    this.firstGFZAuthorMailAdress = firstGFZAuthorMailAdress;
  }

  public String getEmailSubject() {
    return this.emailSubject;
  }

  public void setEmailSubject(String emailSubject) {
    this.emailSubject = emailSubject;
  }

  public String getCcAddress() {
    return this.ccAddress;
  }

  public void setCcAddress(String ccAddress) {
    this.ccAddress = ccAddress;
  }

  public String getItemID() {
    return this.itemID;
  }

  public void setItemID(String itemID) {
    this.itemID = itemID;
  }

  public String getReplyToAddr() {
    return this.replyToAddr;
  }

  public String getOaMail() {
    return this.oaMail;
  }

  public void setOaMail(String oaMail) {
    this.oaMail = oaMail;
  }
}
