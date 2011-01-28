package de.mpg.escidoc.pubman.test.gui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;

public class FullSubmissionTest extends SeleneseTestCase {
	@Before
	public void setUp() throws Exception {
//		selenium = new DefaultSelenium("localhost", 4444, "*chrome", "http://dev-pubman.mpdl.mpg.de/");
		selenium = new DefaultSelenium("localhost", 4444, "*firefox3 C:/Program Files (x86)/Mozilla Firefox/firefox.exe", "http://localhost:8080/");
		selenium.start();
		selenium.open("/pubman/");
		selenium.click("form1:Header:lnkLoginLogout");
		selenium.waitForPageToLoad("30000");
		selenium.type("j_username", "demo");
		selenium.type("j_password", "demo");
		selenium.click("Abschicken");
		selenium.waitForPageToLoad("30000");
		selenium.select("form1:Header:selSelectLocale", "label=English");
		selenium.waitForPageToLoad("30000");
	}

	@Test
	public void testFull_Submission_testing_submit() throws Exception {
		selenium.click("form1:Header:lnkSubmission");
		selenium.waitForPageToLoad("30000");
		selenium.click("lnkNewSubmission");
		selenium.waitForPageToLoad("30000");
		selenium.click("//a[text()='PubMan Test Collection']");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("cboGenre")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.select("cboGenre", "label=Paper");
		selenium.waitForPageToLoad("30000");
		selenium.type("inputTitleText", "full submission paper submit");
		selenium.select("iterCreatorOrganisationAuthors:0:selCreatorRoleString", "label=Editor");
		selenium.mouseDown("iterCreatorOrganisationAuthors:0:inpcreator_persons_person_family_name_optional");
		selenium.keyPress("iterCreatorOrganisationAuthors:0:inpcreator_persons_person_family_name_optional", "\\109");
		selenium.keyPress("iterCreatorOrganisationAuthors:0:inpcreator_persons_person_family_name_optional", "\\97");
		Thread.sleep(3000);
		selenium.mouseOver("//ul[3]/li[2]");
		selenium.click("//ul[3]/li[2]");
		Thread.sleep(3000);
		selenium.click("lnkValidate");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("If genre is not equal to \"Series\" or \"Journal\" or \"Other\" or \"Manuscript\" at least one date has to be provided."));
		selenium.click("//input[@type='button']");
		selenium.type("txtDatePublishedInPrint", "2011-01-21");
		selenium.click("lnkValidate");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("The Item is valid."));
		selenium.click("//input[@type='button']");
		selenium.click("lnkSave");
		selenium.waitForPageToLoad("30000");
		selenium.click("Header:lnkDepWorkspace");
		selenium.waitForPageToLoad("30000");
		selenium.click("//li//*[.='full submission paper submit']");
		selenium.waitForPageToLoad("30000");
		selenium.click("lnkEdit");
		selenium.waitForPageToLoad("30000");
		selenium.type("fileUploads:0:inpAddFileFromUrl", "http://www.atsdr.cdc.gov/tfacts92.pdf");
		selenium.click("fileUploads:0:btnUploadFileFromUrl");
		selenium.waitForPageToLoad("30000");
		selenium.type("locatorUploads:0:inpAddUrl", "http://de.wikipedia.org/wiki/Selenium");
		selenium.click("locatorUploads:0:btnSaveUrl");
		selenium.waitForPageToLoad("30000");
		selenium.select("fileUploads:0:selFileContentCategory", "label=Any fulltext");
		selenium.select("locatorUploads:0:selLocatorContentCategory", "label=Any fulltext");
		selenium.click("lnkValidate");
		selenium.waitForPageToLoad("30000");
		selenium.click("//input[@type='button']");
		selenium.click("lnkSaveAndSubmit");
		selenium.waitForPageToLoad("30000");
		selenium.type("//div[@id='content']/div[2]/div[1]/div[2]/div/div[3]/span/textarea", "a test case with MD");
		selenium.click("lnkSave");
		selenium.waitForPageToLoad("30000");
		selenium.type("Header:quickSearchString", "full submission paper");
		selenium.click("Header:btnQuickSearchStart");
		selenium.waitForPageToLoad("30000");
		selenium.type("Header:quickSearchString", "full submission paper");
		selenium.click("Header:quickSearchCheckBox");
		selenium.click("Header:btnQuickSearchStart");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("full submission paper submit"));
	}
	
	@Test
	public void testFull_Submission_testing_release() throws Exception {
		selenium.click("form1:Header:lnkSubmission");
		selenium.waitForPageToLoad("30000");
		selenium.click("lnkNewSubmission");
		selenium.waitForPageToLoad("30000");
		selenium.click("//a[text()='PubMan Test Collection']");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("cboGenre")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.select("cboGenre", "label=Paper");
		selenium.waitForPageToLoad("30000");
		selenium.type("inputTitleText", "full submission paper release");
		selenium.select("iterCreatorOrganisationAuthors:0:selCreatorRoleString", "label=Editor");
		selenium.mouseDown("iterCreatorOrganisationAuthors:0:inpcreator_persons_person_family_name_optional");
		selenium.keyPress("iterCreatorOrganisationAuthors:0:inpcreator_persons_person_family_name_optional", "\\109");
		selenium.keyPress("iterCreatorOrganisationAuthors:0:inpcreator_persons_person_family_name_optional", "\\97");
		Thread.sleep(3000);
		selenium.mouseOver("//ul[3]/li[2]");
		selenium.click("//ul[3]/li[2]");
		Thread.sleep(3000);
		selenium.click("lnkValidate");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("If genre is not equal to \"Series\" or \"Journal\" or \"Other\" or \"Manuscript\" at least one date has to be provided."));
		selenium.click("//input[@type='button']");
		selenium.type("txtDatePublishedInPrint", "2011-01-21");
		selenium.click("lnkValidate");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("The Item is valid."));
		selenium.click("//input[@type='button']");
		selenium.click("lnkSaveAndSubmit");
		selenium.waitForPageToLoad("30000");
		selenium.click("lnkSave");
		selenium.waitForPageToLoad("30000");
		selenium.click("lnkRelease");
		selenium.waitForPageToLoad("30000");
		selenium.type("//div[@id='content']/div[2]/div[1]/div[2]/div/div[3]/span/textarea", "the item is released");
		selenium.click("lnkRelease");
		selenium.waitForPageToLoad("30000");
		selenium.click("Header:lnkDepWorkspace");
		selenium.waitForPageToLoad("30000");
		selenium.click("//li//*[.='full submission paper release']");
		selenium.waitForPageToLoad("30000");
		selenium.click("lnkModify");
		selenium.waitForPageToLoad("30000");
		selenium.type("fileUploads:0:inpAddFileFromUrl", "http://www.atsdr.cdc.gov/tfacts92.pdf");
		selenium.click("fileUploads:0:btnUploadFileFromUrl");
		selenium.waitForPageToLoad("30000");
		selenium.type("locatorUploads:0:inpAddUrl", "http://de.wikipedia.org/wiki/Selenium");
		selenium.click("locatorUploads:0:btnSaveUrl");
		selenium.waitForPageToLoad("30000");
		selenium.select("fileUploads:0:selFileContentCategory", "label=Any fulltext");
		selenium.select("locatorUploads:0:selLocatorContentCategory", "label=Any fulltext");
		selenium.click("lnkValidate");
		selenium.waitForPageToLoad("30000");
		selenium.click("//input[@type='button']");
		selenium.click("lnkSaveAndSubmit");
		selenium.waitForPageToLoad("30000");
		selenium.click("lnkSave");
		selenium.waitForPageToLoad("30000");
		selenium.click("lnkRelease");
		selenium.waitForPageToLoad("30000");
		selenium.click("lnkRelease");
		selenium.waitForPageToLoad("30000");
		selenium.type("Header:quickSearchString", "full submission");
		selenium.click("Header:btnQuickSearchStart");
		selenium.waitForPageToLoad("30000");
		selenium.type("Header:quickSearchString", "full submission");
		selenium.click("Header:quickSearchCheckBox");
		selenium.click("Header:btnQuickSearchStart");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("full submission paper release"));
	}
	
	@Test
	public void testFull_Submission_testing_submit_and_release() throws Exception {
		selenium.click("form1:Header:lnkSubmission");
		selenium.waitForPageToLoad("30000");
		selenium.click("lnkNewSubmission");
		selenium.waitForPageToLoad("30000");
		selenium.click("//a[text()='PubMan Test Collection']");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("cboGenre")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.select("cboGenre", "label=Paper");
		selenium.waitForPageToLoad("30000");
		selenium.type("inputTitleText", "full submission paper submit and release");
		selenium.select("iterCreatorOrganisationAuthors:0:selCreatorRoleString", "label=Editor");
		selenium.mouseDown("iterCreatorOrganisationAuthors:0:inpcreator_persons_person_family_name_optional");
		selenium.keyPress("iterCreatorOrganisationAuthors:0:inpcreator_persons_person_family_name_optional", "\\109");
		selenium.keyPress("iterCreatorOrganisationAuthors:0:inpcreator_persons_person_family_name_optional", "\\97");
		Thread.sleep(3000);
		selenium.mouseOver("//ul[3]/li[2]");
		selenium.click("//ul[3]/li[2]");
		Thread.sleep(3000);
		selenium.click("lnkValidate");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("If genre is not equal to \"Series\" or \"Journal\" or \"Other\" or \"Manuscript\" at least one date has to be provided."));
		selenium.click("//input[@type='button']");
		selenium.type("txtDatePublishedInPrint", "2011-01-21");
		selenium.click("lnkValidate");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("The Item is valid."));
		selenium.click("//input[@type='button']");
		selenium.click("lnkSave");
		selenium.waitForPageToLoad("30000");
		selenium.click("Header:lnkDepWorkspace");
		selenium.waitForPageToLoad("30000");
		selenium.click("//li//*[.='full submission paper submit and release']");
		selenium.waitForPageToLoad("30000");
		selenium.click("lnkEdit");
		selenium.waitForPageToLoad("30000");
		selenium.type("fileUploads:0:inpAddFileFromUrl", "http://www.atsdr.cdc.gov/tfacts92.pdf");
		selenium.click("fileUploads:0:btnUploadFileFromUrl");
		selenium.waitForPageToLoad("30000");
		selenium.type("locatorUploads:0:inpAddUrl", "http://de.wikipedia.org/wiki/Selenium");
		selenium.click("locatorUploads:0:btnSaveUrl");
		selenium.waitForPageToLoad("30000");
		selenium.select("fileUploads:0:selFileContentCategory", "label=Any fulltext");
		selenium.select("locatorUploads:0:selLocatorContentCategory", "label=Any fulltext");
		selenium.click("lnkValidate");
		selenium.waitForPageToLoad("30000");
		selenium.click("//input[@type='button']");
		selenium.click("lnkSaveAndSubmit");
		selenium.waitForPageToLoad("30000");
		selenium.type("//div[@id='content']/div[2]/div[1]/div[2]/div/div[3]/span/textarea", "a test case with MD");
		selenium.click("lnkSave");
		selenium.waitForPageToLoad("30000");
		selenium.click("lnkRelease");
		selenium.waitForPageToLoad("30000");
		selenium.type("//div[@id='content']/div[2]/div[1]/div[2]/div/div[3]/span/textarea", "the item is released");
		selenium.click("lnkRelease");
		selenium.waitForPageToLoad("30000");
		selenium.type("Header:quickSearchString", "full submission");
		selenium.click("Header:btnQuickSearchStart");
		selenium.waitForPageToLoad("30000");
		selenium.type("Header:quickSearchString", "full submission");
		selenium.click("Header:quickSearchCheckBox");
		selenium.click("Header:btnQuickSearchStart");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("full submission paper submit and release"));
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
