/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.common.emailhandling;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import sun.security.krb5.internal.rcache.ReplayCache;

import de.mpg.escidoc.services.common.EmailHandling;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;


/**
 * EJB implementation of interface {@link EmailHandling}.
 * 
 * @author Galina Stancheva (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 355 $ $LastChangedDate: 2007-07-17 12:19:12 +0200 (Di, 17 Jul 2007) $
 * Revised by StG: 24.08.2007
 */

@Stateless
@Remote
@RemoteBinding(jndiBinding = EmailHandling.SERVICE_NAME)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EmailHandlingBean implements EmailHandling
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(EmailHandlingBean.class);

    
    /**
     * {@inheritDoc}
     */
    public String sendMail(String smtpHost,String usr,String pwd,
                String senderAddress, 
                String[] recipientsAddresses,
                String[] recipientsCCAddresses,
                String[] recipientsBCCAddresses,
                String[] replytoAddresses,
                String subject,String text, String[] attachments) throws  TechnicalException    
    {
        logger.debug("EmailHandlingBean sendMail...");
        String status = "not sent";
        try
        { 
            logger.debug("Email: smtpHost, usr, pwd (" + smtpHost + ", " + usr + ", " + pwd + ")");                 
            logger.debug("Email: subject, text, attachments[0] (" + subject + ", " + text + ", " + attachments[0] + ")");
            logger.debug("Email: sender, recipients[0], replytoAddresses ("+ senderAddress + ", " + recipientsAddresses[0] + ", " + replytoAddresses[0] + ")");
                                            
           // Setup mail server
             Properties props = System.getProperties();
            props.put("mail.smtp.host", smtpHost); 
           props.put("mail.smtp.auth", "true");
            //props.put("mail.transport.protocol", "smtp");
            //props.put("mail.from", senderAddress);
            //props.put("mail.user", usr);
            //props.put("mail.smtp.allow8bitmime", "true");
            //props.put("mail.smtp.socketFactory.port",     "25");
            //props.put("mail.smtp.socketFactory.class",    "javax.net.ssl.SSLSocketFactory");
            //props.put("mail.smtp.socketFactory.fallback", "false");
            //props.put("mail.smtp.starttls",        "true");
            //props.put("mail.smtp.ssl",                    "true");
    //                 String keyStore = System.getProperty("javax.net.ssl.keyStore");
    //                 if(keyStore == null)
    //                     System.out.println("javax.net.ssl.trustStore is not defined");
    //                 else
    //                     System.out.println("javax.net.ssl.keyStore is : " + keyStore);
    //
    //                 logger.debug("System Properties  "+ props.toString());                
            
            // Get a mail session with authentication
            MailAuthenticator authenticator = new MailAuthenticator(usr, pwd);
            Session mailSession = Session.getInstance(props, authenticator);
                            
            // Define a new mail message
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(senderAddress));
            
            //add TO recipients
            for ( String ra : recipientsAddresses )
            {
                if ( ra != null && !ra.trim().equals("") )
                {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(ra));
                    logger.debug(">>> recipientTO: "+ ra);
                }
            }
            
            //add CC recipients
            if ( recipientsCCAddresses != null )
                for ( String racc : recipientsCCAddresses )
                {
                    if ( racc != null && !racc.trim().equals("") )
                    {
                        message.addRecipient(Message.RecipientType.CC, new InternetAddress(racc));
                        logger.debug(">>> recipientCC  "+ racc);
                    }
                }
            
            //add BCC recipients
            if ( recipientsBCCAddresses != null )
                for ( String rabcc : recipientsBCCAddresses )
                {
                    if ( rabcc != null && !rabcc.trim().equals("") )
                    {
                        message.addRecipient(Message.RecipientType.BCC, new InternetAddress(rabcc));
                        logger.debug(">>> recipientBCC  "+ rabcc);
                    }
                }
    
           //add replyTo 
            if ( replytoAddresses != null )
            {
                InternetAddress[] adresses = new InternetAddress[ recipientsAddresses.length ];
                int i = 0;
                for ( String a : replytoAddresses )
                {
                    if ( a != null && !a.trim().equals("") )
                    {
                        adresses[i] = new InternetAddress(a);
                        i++;
                        logger.debug(">>> replyToaddress  "+ a);
                    }     
                }
                if ( i > 0 )
                    message.setReplyTo(adresses);
            }
            
            message.setSubject(subject);
            Date date = new Date();
            message.setSentDate(date);
            
            // Create a message part to represent the body text
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(text);
            
            //use a MimeMultipart as we need to handle the file attachments
            Multipart multipart = new MimeMultipart();
            
            //add the message body to the mime message
            multipart.addBodyPart(messageBodyPart);
                                     
            // add any file attachments to the message
            addAtachments(attachments , multipart);
            
            // Put all message parts in the message
            message.setContent(multipart);
    
            logger.debug("Transport will send now....  ");
           
            /*Transport tr = mailSession.getTransport("smtp");
            tr.connect(smtpHost, 25, usr, "null");
            message.saveChanges();
            tr.sendMessage(message, message.getAllRecipients());
            tr.close(); */
            
            // Send the message
            Transport.send(message);      
            
            status = "sent";
            logger.debug("Email sent!");
        }
        catch (MessagingException e) 
        {             
            logger.error("Error in sendMail(...)", e);
            throw new TechnicalException(e);      
        }
        return status;
    }


    /*
     * A method to ass attachment files to a message
     */ 

    protected void addAtachments(String[] attachments, Multipart multipart)
    throws MessagingException, AddressException
    {
        for( String filename : attachments )
        {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
             
            //use a JAF FileDataSource as it does MIME type detection
            DataSource source = new FileDataSource(filename);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            
            attachmentBodyPart.setFileName( new File(filename).getName() );
            
            //add the attachment
            multipart.addBodyPart(attachmentBodyPart);
        }
    }

    /*
     * A class for authentication on the mail server when sending emails. It opnes a 
     * registration popup window.
     */ 
    public class MailAuthenticator extends Authenticator
    {

        private final String user;
        private final String password;
       
        /**
         * Public constructor.
         */
        public MailAuthenticator(String usr, String pwd)
        {
            this.user = usr;
            this.password = pwd;
        }
        
        protected PasswordAuthentication getPasswordAuthentication()
        {
            PasswordAuthentication pwdAut = new PasswordAuthentication(this.user, this.password);
            return pwdAut;            
        }
    }    
}
