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

  private final EmailService emailService;

  @Autowired
  public OrcidEmailServiceImpl(EmailService emailService) {
    this.emailService = emailService;
  }

  @Override
  public String createOrcidEmailText(String nameAuthor, String orcidAuthor, String emailLink) throws TechnicalException {
    try {
      String template = ResourceUtil.getResourceAsString("orcid_email.txt", OrcidEmailServiceImpl.class.getClassLoader());
      template = template.replace("$NAME_AUTHOR", nameAuthor != null ? nameAuthor : "");
      template = template.replace("$ORCID_AUTHOR", orcidAuthor != null ? orcidAuthor : "");
      template = template.replace("$EMAIL_LINK", emailLink != null ? emailLink : "");
      return template;
    } catch (IOException e) {
      logger.error("Error reading orcid_email.txt", e);
      throw new TechnicalException("Error reading orcid_email.txt", e);
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
