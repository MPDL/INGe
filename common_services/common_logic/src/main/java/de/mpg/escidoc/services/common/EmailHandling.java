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

package de.mpg.escidoc.services.common;


import de.mpg.escidoc.services.common.exceptions.TechnicalException;

/**
 * Interface for creation and sending of emails with attachment. (Used in the
 * export functionalty.)
 * Revised by StG: 24.08.2007
 * @author Galina Stancheva
 * @version 1.0
 * @updated 13-Sep-2007 15:30:17
 */
public interface EmailHandling
{
    /**
     * The name of the service.
     */
    public static String SERVICE_NAME = "ejb/de/mpg/escidoc/services/common/EmailHandling";
 
    /**
     * Method for creating and sending an email with attached file. 
     * The sending requires authentication.
     * 
     * @param smtpHost   the outgoing smpt mail server
     * @param usr        the user authorized to the server
     * @param pwd        the password of the user
     * @param senderAddress    the email address of the sender
     * @param recipientsAddresses    the email address(es) of the recipients
     * @param recipientsCCAddresses    the CC email address(es) of the recipients
     * @param recipientsBCCAddresses    the BCC email address(es) of the recipients
     * @param replytoAddresses    the replyto address(es)
     * @param text    the content text of the email
     * @param subject    the subject of the email
     * @param attachments    the names/paths of the files to be attached
     */
    public String sendMail(String smtpHost,String usr,String pwd,
                         String senderAddress, 
                         String[] recipientsAddresses,
                         String[] recipientsCCAddresses,                         
                         String[] recipientsBCCAddresses,                         
                         String[] replytoAddresses,
                         String subject,String text, String[] attachments) throws TechnicalException ;
} 