package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.pubman.EmailService;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidEmailService;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrcidEmailServiceImpl implements OrcidEmailService {
  private static final Logger logger = LogManager.getLogger(OrcidEmailServiceImpl.class);
  public static final String ORCID_EMAIL_AUTHOR_TEXT = "orcid_email_author.txt";
  public static final String ORCID_EMAIL_BIBO_TEXT = "orcid_email_bibo.txt";
  public static final String ORCID_EMAIL_CONSENT_AUTHOR_TEXT = "orcid_email_consent_author.txt";
  public static final String ORCID_EMAIL_CONSENT_BIBO_TEXT = "orcid_email_consent_bibo.txt";
  public static final String ORCID_EMAIL_NO_CONSENT_AUTHOR_TEXT = "orcid_email_no_consent_author.txt";
  public static final String ORCID_EMAIL_NO_CONSENT_BIBO_TEXT = "orcid_email_no_consent_bibo.txt";

  private final EmailService emailService;

  @Autowired
  public OrcidEmailServiceImpl(EmailService emailService) {
    this.emailService = emailService;
  }

  @Override
  public String createOrcidEmailAuthorText(String nameAuthor, String orcidAuthor, String emailLink) throws TechnicalException {
    try {
      String template = ResourceUtil.getResourceAsString(ORCID_EMAIL_AUTHOR_TEXT, OrcidEmailServiceImpl.class.getClassLoader());
      template = template.replace("$NAME_AUTHOR", nameAuthor != null ? nameAuthor : "");
      template = template.replace("$ORCID_AUTHOR", orcidAuthor != null ? orcidAuthor : "");
      template = template.replace("$EMAIL_LINK", emailLink != null ? emailLink : "");
      return template;
    } catch (IOException e) {
      logger.error("Error reading " + ORCID_EMAIL_AUTHOR_TEXT, e);
      throw new TechnicalException("Error reading " + ORCID_EMAIL_AUTHOR_TEXT, e);
    }
  }

  @Override
  public String createOrcidEmailBiboText() throws TechnicalException {
    try {
      String template = ResourceUtil.getResourceAsString(ORCID_EMAIL_BIBO_TEXT, OrcidEmailServiceImpl.class.getClassLoader());
      return template;
    } catch (IOException e) {
      logger.error("Error reading " + ORCID_EMAIL_BIBO_TEXT, e);
      throw new TechnicalException("Error reading " + ORCID_EMAIL_BIBO_TEXT, e);
    }
  }

  @Override
  public String createOrcidEmailConsentAuthorText(String nameAuthor) throws TechnicalException {
    try {
      String template = ResourceUtil.getResourceAsString(ORCID_EMAIL_CONSENT_AUTHOR_TEXT, OrcidEmailServiceImpl.class.getClassLoader());
      template = template.replace("$NAME_AUTHOR", nameAuthor != null ? nameAuthor : "");
      return template;
    } catch (IOException e) {
      logger.error("Error reading " + ORCID_EMAIL_CONSENT_AUTHOR_TEXT, e);
      throw new TechnicalException("Error reading " + ORCID_EMAIL_CONSENT_AUTHOR_TEXT, e);
    }
  }

  @Override
  public String createOrcidEmailConsentBiboText(String nameAuthor) throws TechnicalException {
    try {
      String template = ResourceUtil.getResourceAsString(ORCID_EMAIL_CONSENT_BIBO_TEXT, OrcidEmailServiceImpl.class.getClassLoader());
      template = template.replace("$NAME_AUTHOR", nameAuthor != null ? nameAuthor : "");
      return template;
    } catch (IOException e) {
      logger.error("Error reading " + ORCID_EMAIL_CONSENT_BIBO_TEXT, e);
      throw new TechnicalException("Error reading " + ORCID_EMAIL_CONSENT_BIBO_TEXT, e);
    }
  }

  @Override
  public String createOrcidEmailNoConsentAuthorText(String nameAuthor, String orcidAuthor, String emailLink) throws TechnicalException {
    try {
      String template = ResourceUtil.getResourceAsString(ORCID_EMAIL_NO_CONSENT_AUTHOR_TEXT, OrcidEmailServiceImpl.class.getClassLoader());
      template = template.replace("$NAME_AUTHOR", nameAuthor != null ? nameAuthor : "");
      template = template.replace("$EMAIL_LINK", emailLink != null ? emailLink : "");
      return template;
    } catch (IOException e) {
      logger.error("Error reading " + ORCID_EMAIL_NO_CONSENT_AUTHOR_TEXT, e);
      throw new TechnicalException("Error reading " + ORCID_EMAIL_NO_CONSENT_AUTHOR_TEXT, e);
    }
  }

  @Override
  public String createOrcidEmailNoConsentBiboText(String nameAuthor) throws TechnicalException {
    try {
      String template = ResourceUtil.getResourceAsString(ORCID_EMAIL_NO_CONSENT_BIBO_TEXT, OrcidEmailServiceImpl.class.getClassLoader());
      template = template.replace("$NAME_AUTHOR", nameAuthor != null ? nameAuthor : "");
      return template;
    } catch (IOException e) {
      logger.error("Error reading " + ORCID_EMAIL_NO_CONSENT_BIBO_TEXT, e);
      throw new TechnicalException("Error reading " + ORCID_EMAIL_NO_CONSENT_BIBO_TEXT, e);
    }
  }

  @Override
  public void sendEmail(String recipientAddress, String senderAddress, String replyToAddress, String subject, String text)
      throws TechnicalException {
    String smtpHost = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_MAILSERVERNAME);
    String withAuth = "false";
    String usr = null;
    String pwd = null;
    String sender = senderAddress;
    String[] recipientsAddresses = new String[] {recipientAddress};
    String[] recipientsCCAddresses = null;
    String[] recipientsBCCAddresses = null;
    String[] replyToAddresses = (replyToAddress != null ? new String[] {replyToAddress} : null);

    this.emailService.sendHtmlMail(smtpHost, withAuth, usr, pwd, sender, recipientsAddresses, recipientsCCAddresses, recipientsBCCAddresses,
        replyToAddresses, subject, text);
  }

}
