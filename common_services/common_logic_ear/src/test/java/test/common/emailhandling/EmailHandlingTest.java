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

package test.common.emailhandling;

import static org.junit.Assert.assertNotNull;
import java.io.File;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import test.common.TestBase;
import de.mpg.escidoc.services.common.EmailHandling;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;

/**
 * Test class for {@link de.mpg.escidoc.services.common.emailhandling.EmailHandlingBean}
 * 
 * @author Galina Stancheva (initial creation)
 * @author $Author: StG $ (last modification)
 * @version $Revision:  $ $LastChangedDate: $
 * @revised by StG: 
 */
public class EmailHandlingTest extends TestBase
{

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(EmailHandlingTest.class);

    private static EmailHandling emailHandling;
    private static String username;
    private static String password ;
    private static String smtpHost;
    //which on is the pubman one? ToDo StG.
    private static String senderAddress;//someone@web.de
    private static String[] recipientsAddresses; //somereceiver@web.de
    private static String subject;
    private static String text;
    private static String[] replyToAddresses;
    private static String[] attachments;
        
    /**
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws  NamingException
    {
        // sets the handler
        emailHandling = getEmailHandling();
        username = "pubman";
        password = "cGVSeP";
        smtpHost = "mail.mpisoc.mpg.de";        
        recipientsAddresses = new String[] {"stancheva@mpisoc.mpg.de"}; 
        replyToAddresses = new String[]{"pubman@mpisoc.mpg.de"};
        subject = "Testing Email sending";
        text = "Text of the email...... ";
        senderAddress ="pubman@mpisoc.mpg.de";//someone@web.de
        attachments = null;
    }
    
    /**
     * Test method for
     * {@link de.mpg.escidoc.services.common.exporthandling.EmailHandlingBean#sendMail()}.
     */
    @Test
    public void testSendMail() throws TechnicalException
    {
        logger.debug("### testSendMail ###" );        
 
        File file = new File("src/test/resources/emailhandling/APA.html");
        assertNotNull(file);
        file.getAbsolutePath();
        attachments = new String[]{file.getAbsolutePath()};
        emailHandling.sendMail(smtpHost, username, password, senderAddress, recipientsAddresses, replyToAddresses, 
                               subject, text, attachments);
    }

    /**
     * Helper method to retrieve DataGathering instance.
     * 
     * @return instance of EmailHandling
     * @throws NamingException
     */
    private static EmailHandling getEmailHandling() throws NamingException
    {
        InitialContext context = new InitialContext();
        EmailHandling emailHandling = (EmailHandling)context.lookup(EmailHandling.SERVICE_NAME);
        assertNotNull(emailHandling);
        return emailHandling;
    }
    


}
