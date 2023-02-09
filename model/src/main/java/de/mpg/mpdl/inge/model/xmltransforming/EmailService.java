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

package de.mpg.mpdl.inge.model.xmltransforming;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;

/**
 * @author Galina Stancheva (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$ Revised by StG: 24.08.2007
 */
public class EmailService {
  private static final Logger logger = Logger.getLogger(EmailService.class);

  /**
   * {@inheritDoc}
   */
  public static String sendMail(String smtpHost, String withAuth, String usr, String pwd, String senderAddress,
      String[] recipientsAddresses, String[] recipientsCCAddresses, String[] recipientsBCCAddresses, String[] replytoAddresses,
      String subject, String text, String[] attachments) throws TechnicalException {
    String status = "not sent";
    try {
      // Setup mail server
      Properties props = System.getProperties();
      props.put("mail.smtp.host", smtpHost);
      props.put("mail.smtp.auth", withAuth);
      props.put("mail.smtp.starttls.enable", "true");

      // Get a mail session with authentication
      MailAuthenticator authenticator = MailAuthenticator.createMailAuthenticator(usr, pwd);
      Session mailSession = Session.getInstance(props, authenticator);

      // Define a new mail message
      Message message = new MimeMessage(mailSession);
      message.setFrom(new InternetAddress(senderAddress));

      // add TO recipients
      for (String ra : recipientsAddresses) {
        if (ra != null && !ra.trim().equals("")) {
          message.addRecipient(Message.RecipientType.TO, new InternetAddress(ra));
        }
      }

      // add CC recipients
      if (recipientsCCAddresses != null)
        for (String racc : recipientsCCAddresses) {
          if (racc != null && !racc.trim().equals("")) {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(racc));
          }
        }

      // add BCC recipients
      if (recipientsBCCAddresses != null)
        for (String rabcc : recipientsBCCAddresses) {
          if (rabcc != null && !rabcc.trim().equals("")) {
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(rabcc));
          }
        }

      // add replyTo
      if (replytoAddresses != null) {
        InternetAddress[] adresses = new InternetAddress[recipientsAddresses.length];
        int i = 0;
        for (String a : replytoAddresses) {
          if (a != null && !a.trim().equals("")) {
            adresses[i] = new InternetAddress(a);
            i++;
          }
        }
        if (i > 0)
          message.setReplyTo(adresses);
      }

      message.setSubject(subject);
      Date date = new Date();
      message.setSentDate(date);

      // Create a message part to represent the body text
      BodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setText(text);

      // use a MimeMultipart as we need to handle the file attachments
      Multipart multipart = new MimeMultipart();

      // add the message body to the mime message
      multipart.addBodyPart(messageBodyPart);

      // add any file attachments to the message
      addAtachments(attachments, multipart);

      // Put all message parts in the message
      message.setContent(multipart);

      // Send the message
      Transport.send(message);

      status = "sent";
    } catch (MessagingException e) {
      logger.error("Error in sendMail(...)", e);
      throw new TechnicalException(e);
    }

    return status;
  }

  private static void addAtachments(String[] attachments, Multipart multipart) throws MessagingException, AddressException {
    for (String filename : attachments) {
      MimeBodyPart attachmentBodyPart = new MimeBodyPart();

      // use a JAF FileDataSource as it does MIME type detection
      DataSource source = new FileDataSource(filename);
      attachmentBodyPart.setDataHandler(new DataHandler(source));

      attachmentBodyPart.setFileName(new File(filename).getName());

      // add the attachment
      multipart.addBodyPart(attachmentBodyPart);
    }
  }

  /*
   * A class for authentication on the mail server when sending emails. It opnes a registration
   * popup window.
   */
  public static class MailAuthenticator extends Authenticator {
    private final String user;
    private final String password;

    private MailAuthenticator(String usr, String pwd) {
      this.user = usr;
      this.password = pwd;
    }

    public static MailAuthenticator createMailAuthenticator(String usr, String pwd) {
      return new MailAuthenticator(usr, pwd);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
      PasswordAuthentication pwdAut = new PasswordAuthentication(this.user, this.password);
      return pwdAut;
    }
  }

  public static String sendHtmlMail(String smtpHost, String withAuth, String usr, String pwd, String senderAddress,
      String[] recipientsAddresses, String[] recipientsCCAddresses, String[] recipientsBCCAddresses, String[] replytoAddresses,
      String subject, String text) throws TechnicalException {
    String status = "not sent";
    try {
      // Setup mail server
      Properties props = System.getProperties();
      props.put("mail.smtp.host", smtpHost);
      props.put("mail.smtp.auth", withAuth);
      props.put("mail.smtp.starttls.enable", "true");

      // Get a mail session with authentication
      MailAuthenticator authenticator = MailAuthenticator.createMailAuthenticator(usr, pwd);
      Session mailSession = Session.getInstance(props, authenticator);

      // Define a new mail message
      Message message = new MimeMessage(mailSession);
      message.setFrom(new InternetAddress(senderAddress));

      // add TO recipients
      for (String ra : recipientsAddresses) {
        if (ra != null && !ra.trim().equals("")) {
          message.addRecipient(Message.RecipientType.TO, new InternetAddress(ra));
        }
      }

      // add CC recipients
      if (recipientsCCAddresses != null)
        for (String racc : recipientsCCAddresses) {
          if (racc != null && !racc.trim().equals("")) {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(racc));
          }
        }

      // add BCC recipients
      if (recipientsBCCAddresses != null)
        for (String rabcc : recipientsBCCAddresses) {
          if (rabcc != null && !rabcc.trim().equals("")) {
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(rabcc));
          }
        }

      // add replyTo
      if (replytoAddresses != null) {
        InternetAddress[] adresses = new InternetAddress[recipientsAddresses.length];
        int i = 0;
        for (String a : replytoAddresses) {
          if (a != null && !a.trim().equals("")) {
            adresses[i] = new InternetAddress(a);
            i++;
          }
        }
        if (i > 0)
          message.setReplyTo(adresses);
      }

      message.setSubject(subject);
      Date date = new Date();
      message.setSentDate(date);

      // Create a message part to represent the body text
      BodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setContent(text, "text/html; charset=utf-8");

      // use a MimeMultipart as we need to handle the file attachments
      Multipart multipart = new MimeMultipart();

      // add the message body to the mime message
      multipart.addBodyPart(messageBodyPart);

      // Put all message parts in the message
      message.setContent(multipart);

      // Send the message
      Transport.send(message);

      status = "sent";
    } catch (MessagingException e) {
      logger.error("Error in sendMail(...)", e);
      throw new TechnicalException(e);
    }

    return status;
  }

}
