/*
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


package selenium;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.*;

public class FirstTest extends SeleneseTestCase {
    
    @Before
    public void setUp() throws Exception {
        setUp("http://localhost:8080/", "*firefox /usr/lib/firefox-3.5.3/firefox-3.5");
    }
    
    @Test
    public void testFirst() throws Exception {
        selenium.open("/pubman/");
        selenium.click("form1:Header:lnkLoginLogout");
        selenium.waitForPageToLoad("30000");
        selenium.type("j_username", "demo");
        selenium.type("j_password", "demo");
        selenium.click("Abschicken");
        selenium.waitForPageToLoad("30000");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isElementPresent("form1:Header:lnkSubmission"));
        verifyTrue(selenium.isElementPresent("form1:Header:lnkQAWorkspace"));
        selenium.click("form1:Header:lnkSubmission");
        selenium.waitForPageToLoad("30000");
        selenium.click("lnkNewSubmission");
        selenium.waitForPageToLoad("30000");
        selenium.click("Header:lnkLoginLogout");
        selenium.waitForPageToLoad("30000");
    }
}

