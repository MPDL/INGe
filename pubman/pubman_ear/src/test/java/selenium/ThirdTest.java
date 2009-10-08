
package selenium;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.*;

public class ThirdTest extends SeleneseTestCase {
    
    @Before
    public void setUp() throws Exception {
        setUp("http://localhost:8080/", "*opera");
    }
    
    @Test
    public void testSecond() throws Exception {
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