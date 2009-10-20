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
package de.mpg.escidoc.pubman.test.gui.modules;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.thoughtworks.selenium.SeleneseTestCase;

/**
 * @author endres
 *
 */
public abstract class PubmanGuiModules extends SeleneseTestCase {

    /** maximum time to wait until a page has loaded */
    protected static final String MAX_PAGELOAD_TIMEOUT = "30000";
    
    /** logging instance */
    protected final transient Logger log = Logger.getLogger( getClass() ); 
    
    /** Properties from config file "selenium.properties" Includes all testing relevant data like user accounts etc. */
    private Properties properties = null; 
    
    public PubmanGuiModules() throws IOException {
        InputStream instream = PubmanGuiModules.class.getClassLoader().getResource("selenium.properties").openStream();
        properties = new Properties();
        properties.load(instream);
    }
    
    // modules section
    public void loginPubmanForType( PubmanTypesDefinitions.LoginType type ) {
        switch( type ) {
            case DepositorModeratorSimpleWF:
                loginPubman( properties.getProperty("selenium.depositorModerator.simpleWF.username"), 
                        properties.getProperty("selenium.depositorModerator.simpleWF.password"));
                verifyTrue(selenium.isElementPresent("form1:Header:lnkDepWorkspace"));
                verifyTrue(selenium.isElementPresent("form1:Header:lnkQAWorkspace"));
                break;
            case ModeratorSimpleWF:
                loginPubman( properties.getProperty("selenium.moderator.simpleWF.username"), 
                        properties.getProperty("selenium.moderator.simpleWF.password"));
                verifyFalse(selenium.isElementPresent("form1:Header:lnkDepWorkspace"));
                verifyTrue(selenium.isElementPresent("form1:Header:lnkQAWorkspace"));
                break;
            case DepositorSimpleWF:
                loginPubman( properties.getProperty("selenium.depositor.simpleWF.username"), 
                        properties.getProperty("selenium.depositor.simpleWF.password"));
                verifyTrue(selenium.isElementPresent("form1:Header:lnkDepWorkspace"));
                verifyFalse(selenium.isElementPresent("form1:Header:lnkQAWorkspace"));
                break;
            case DepositorModeratorStandardWF:
                loginPubman( properties.getProperty("selenium.depositorModerator.standardWF.username"), 
                        properties.getProperty("selenium.depositorModerator.standardWF.password"));
                verifyTrue(selenium.isElementPresent("form1:Header:lnkDepWorkspace"));
                verifyTrue(selenium.isElementPresent("form1:Header:lnkQAWorkspace"));
                break;
            case ModeratorStandardWF:
                loginPubman( properties.getProperty("selenium.moderator.standardWF.username"), 
                        properties.getProperty("selenium.moderator.standardWF.password"));
                verifyFalse(selenium.isElementPresent("form1:Header:lnkDepWorkspace"));
                verifyTrue(selenium.isElementPresent("form1:Header:lnkQAWorkspace"));
                break;
            case DepositorStandardWF:
                loginPubman( properties.getProperty("selenium.depositor.standardWF.username"), 
                        properties.getProperty("selenium.depositor.standardWF.password"));
                verifyTrue(selenium.isElementPresent("form1:Header:lnkDepWorkspace"));
                verifyFalse(selenium.isElementPresent("form1:Header:lnkQAWorkspace"));
                break;
            case DepositorModeratorSimpleStandardWF:
                loginPubman( properties.getProperty("selenium.depositorModerator.simpleStandardWF.username"), 
                        properties.getProperty("selenium.depositorModerator.simpleStandardWF.password"));
                verifyTrue(selenium.isElementPresent("form1:Header:lnkDepWorkspace"));
                verifyTrue(selenium.isElementPresent("form1:Header:lnkQAWorkspace"));
                break;
            case ModeratorSimpleStandardWF:
                loginPubman( properties.getProperty("selenium.moderator.simpleStandardWF.username"), 
                        properties.getProperty("selenium.moderator.simpleStandardWF.password"));
                verifyFalse(selenium.isElementPresent("form1:Header:lnkDepWorkspace"));
                verifyTrue(selenium.isElementPresent("form1:Header:lnkQAWorkspace"));
                break;
            case DepositorSimpleStandardWF:
                loginPubman( properties.getProperty("selenium.depositor.simpleStandardWF.username"), 
                        properties.getProperty("selenium.depositor.simpleStandardWF.password"));
                verifyTrue(selenium.isElementPresent("form1:Header:lnkDepWorkspace"));
                verifyFalse(selenium.isElementPresent("form1:Header:lnkQAWorkspace"));
                break;
            default:
                throw new VerifyError("Unknown login type requested.");
        }
    }
    private void loginPubman( String username, String password ) {
        this.selenium.open("/pubman/");
        this.selenium.click("form1:Header:lnkLoginLogout");
        this.selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        this.selenium.type("j_username", username);
        this.selenium.type("j_password", password);
        this.selenium.click("Abschicken");
        this.selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        this.selenium.isCookiePresent("escidocCookie");
    }
    public void logoutPubman() {
        this.selenium.open("/pubman/");
        selenium.click("form1:Header:lnkLoginLogout");
        this.selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        verifyFalse(selenium.isElementPresent("form1:Header:lnkDepWorkspace"));
        verifyFalse(selenium.isElementPresent("form1:Header:lnkQAWorkspace"));
    }
    
    public void createDetailedItem() {
        
    }
    
    public String doEasySubmission() {
        selenium.open("/pubman/faces/HomePage.jsp");
        selenium.click("form1:Header:lnkSubmission");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.click("lblSubmission_lnkEasySubmission");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);                      
        selenium.click("easySubmission:CollectionSelection:iterDepositorContextList:lnkSelectForEasySubmissionFirst");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.click("easySubmission:easySubmissionStep1Manual:btnChangeGenre");
        selenium.select("easySubmission:easySubmissionStep1Manual:selGenre", "value=BOOK");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.type("easySubmission:easySubmissionStep1Manual:inpItemMetadataTitle", "testbuch");
        selenium.type("easySubmission:easySubmissionStep1Manual:fileUploads:0:inpFileUploaded", "/home/endres/projects/escidoc/workdir/solutions/pubman/pubman_ear/src/test/resources/uploadData/ABC_Pt_Mor_May_2009_final.ppt");
        selenium.click("easySubmission:easySubmissionStep1Manual:fileUploads:0:btnUploadFile");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.select("easySubmission:easySubmissionStep1Manual:fileUploads:0:selContentCategory", "value=any-fulltext");
        selenium.type("easySubmission:easySubmissionStep1Manual:fileUploads:0:inpComponentDescription", "description_uploadFile");
        selenium.type("easySubmission:easySubmissionStep1Manual:fileUploads:0:inpComponentFileDefaultMetadataRights", "copyright_info");
        selenium.type("easySubmission:easySubmissionStep1Manual:locatorUploads:0:inpLocatorLocator1", "http://external.org");
        selenium.click("easySubmission:easySubmissionStep1Manual:locatorUploads:0:lblEasySubmissionSaveLocator");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.click("easySubmission:easySubmissionStep1Manual:locatorUploads:0:selLocatorFileVisibility:0");
        selenium.type("easySubmission:easySubmissionStep1Manual:locatorUploads:0:inpLocatorFileDescription", "external_locator_info");
        selenium.type("easySubmission:easySubmissionStep1Manual:locatorUploads:0:inpLocatorFileDefaultMetadataRights", "external_locator_info");
        selenium.type("easySubmission:easySubmissionStep1Manual:locatorUploads:0:locatorLicenseDate", "2009-10-14");
        selenium.click("easySubmission:easySubmissionStep1Manual:lnkNext");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.select("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:selRoleString", "value=AUTHOR");
        selenium.click("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:selCreatorTypeString:0");
        selenium.type("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:inpCreatorPersonFamilyName", "creator_family_name");
        selenium.type("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:inpCreatorPersonGivenName", "creator_first_name");
        selenium.type("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:iterCreatorPersonOrganisation:0:inpPersOrgaName", "creator_orga");
        selenium.type("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:iterCreatorPersonOrganisation:0:inpPersOrgaAddress", "creator_orga_address");
        selenium.type("easySubmission:easySubmissionStep2Manual:inpFreeKeywords", "contents_keyword");
        selenium.type("easySubmission:easySubmissionStep2Manual:inpSubject", "011 - Bibliographies");
        selenium.type("easySubmission:easySubmissionStep2Manual:inpAbstract", "contents_abstract");
        selenium.click("easySubmission:easySubmissionStep2Manual:lnkNext");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.type("easySubmission:easySubmissionStep3Manual:txtDatePublishedInPrint", "2008-10-10");
        selenium.select("easySubmission:easySubmissionStep3Manual:selSourceGenre", "value=BOOK");
        selenium.type("easySubmission:easySubmissionStep3Manual:inpSourceTitle", "quelle_titel");
        selenium.type("easySubmission:easySubmissionStep3Manual:inpSourceDetailsVolume", "12");
        selenium.type("easySubmission:easySubmissionStep3Manual:inpSourceDetailsIssue", "122");
        selenium.type("easySubmission:easySubmissionStep3Manual:inpSourceDetailsStartPage", "23");
        selenium.type("easySubmission:easySubmissionStep3Manual:inpSourceDetailsEndPage", "233");
        selenium.click("easySubmission:easySubmissionStep3Manual:lnkSave");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        String objectId = selenium.getText("txtObjectIdAndVersion");
        log.info("Created object: " + objectId ); 
        return objectId;
    }
}
