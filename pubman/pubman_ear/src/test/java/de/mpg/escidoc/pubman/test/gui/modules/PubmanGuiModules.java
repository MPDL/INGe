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

import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItemBasic;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItemContent;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItemDetails;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItemEvent;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItemPersonOrganizations;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItemSource;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem.CreatorRole;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem.CreatorType;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem.GenreType;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem.IdentifierType;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem.ItemType;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItemDetails.ReviewType;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItemSource.SourceGenre;
import de.mpg.escidoc.pubman.test.gui.modules.login.PubmanUser;

/**
 * @author endres
 * 
 */
public abstract class PubmanGuiModules extends SeleneseTestCase
{

    /** maximum time to wait until a page has loaded */
    protected static final String MAX_PAGELOAD_TIMEOUT = "30000";

    /** logging instance */
    protected final transient Logger log = Logger.getLogger(getClass());

    /**
     * Properties from config file "selenium.properties" Includes all testing
     * relevant data like user accounts etc.
     */
    private Properties properties = null;

    public PubmanGuiModules() throws IOException
    {
        InputStream instream = PubmanGuiModules.class.getClassLoader().getResource("selenium.properties").openStream();
        properties = new Properties();
        properties.load(instream);
    }

    // modules section
    public void loginPubmanForType(PubmanUser.UserType type)
    {
        PubmanUser pubmanUser = new PubmanUser( type, this.properties );
        loginPubman(pubmanUser.getUsername(), pubmanUser.getPassword());
        for( PubmanUser.UserRights right : PubmanUser.UserRights.values() ) {
            if( pubmanUser.isAuthorized(right) ) {
                verifyTrue(selenium.isElementPresent(pubmanUser.getUserRightLinkHomepage(right)));
            }
            else {
                verifyFalse(selenium.isElementPresent(pubmanUser.getUserRightLinkHomepage(right)));
            }
        }
    }

    private void loginPubman(String username, String password)
    {
        this.selenium.open("/pubman/");
        this.selenium.click("form1:Header:lnkLoginLogout");
        this.selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        this.selenium.type("j_username", username);
        this.selenium.type("j_password", password);
        this.selenium.click("Abschicken");
        this.selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        this.selenium.isCookiePresent("escidocCookie");
    }

    public void logoutPubman()
    {
        this.selenium.open("/pubman/");
        this.selenium.click("form1:Header:lnkLoginLogout");
        this.selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        verifyFalse(selenium.isElementPresent("form1:Header:lnkDepWorkspace"));
        verifyFalse(selenium.isElementPresent("form1:Header:lnkQAWorkspace"));
    }

    public PubmanItem createPubItem(ItemType type)
    {
        switch (type)
        {
            case Item:
                PubmanItem item = new PubmanItem(ItemType.Item, GenreType.OTHER);
                PubmanItemBasic basic = new PubmanItemBasic("selenium testitem");
                item.addBasic(basic);
                PubmanItemContent content = new PubmanItemContent("keywords", "011 - Bibliographies", "contentAbstract");
                item.addContent(content);
                PubmanItemEvent event = new PubmanItemEvent("eventTitle", "placeOfEvent", "2008-10-10", "2009-11-11");
                item.addEvent(event);
                PubmanItemDetails  details = new PubmanItemDetails("de", "2000", "2001", "2002", "2003",
                        "2004", "2005", "123", "tableOfContent", ReviewType.INTERNAL, IdentifierType.ESCIDOC, "escidoc:12345");
                item.addDetails(details);
                PubmanItemPersonOrganizations persOrg = new PubmanItemPersonOrganizations(null, false, null, null, CreatorRole.AUTHOR, 
                        "firstName", "lastName", "orgaName", "orgaAdress");
                item.addPersonOrganisation(persOrg);
                PubmanItemSource source = new PubmanItemSource( SourceGenre.BOOK, "testbuch", CreatorRole.ARTIST, CreatorType.Person, "familyName", "givenName", "orgaName",
                       "orgaAddress", "volume", "123", "publisher", "place", IdentifierType.ESCIDOC, "escidoc:65432", "edition", "issue", "1", "4231", "222" );
                item.addSource(source);
                return item;
            case ItemWithFile:
                return null;
            case ItemWithFileAndLocator:
                return null;
            case ItemWithLocator:
                return null;
            default:
                throw new VerifyError("Unknown Pubman item type requested.");
        }
    }

    public void createDetailedItem()
    {
        selenium.open("/pubman/faces/HomePage.jsp?eSciDocUserHandle=RVNDSURPQy0xMjU2Mjg0ODQxMzgx");
        selenium.click("form1:Header:lnkSubmission");
        selenium.waitForPageToLoad("30000");
        selenium.click("lblSubmission_lnkNewSubmission");
        selenium.waitForPageToLoad("30000");
        selenium.click("iterdepositorContextList2:lnkSelectContext");
        selenium.waitForPageToLoad("30000"); 
    }
    
    public void fillBasicSection() {
        selenium.type("inputTitleText", "test title");
        selenium.click("btnAddTitle");
        selenium.waitForPageToLoad("30000");
        selenium.type("iterAlternativeTitle:0:inpAlternativeTitle", "alternative title");
        selenium.click("iterAlternativeTitle:0:btnAddAlternativeTitle");
        selenium.waitForPageToLoad("30000");
        selenium.type("iterAlternativeTitle:1:inpAlternativeTitle", "2nd alternative title"); 
    }
    
    public void fillFileSection() {
        selenium.type("fileUploads:0:inpFile", "C:\\Dokumente und Einstellungen\\nek\\Desktop\\EndNote_Import_Despoina.txt");
        selenium.click("fileUploads:0:btnUploadFile");
        selenium.select("fileUploads:0:selFileContentCategory", "label=Preprint");
        selenium.select("fileUploads:0:selFileVisibility", "label=Restricted");
        selenium.type("fileUploads:0:fileEmbargoDate", "2009-12-31");
        selenium.type("fileUploads:0:inpExtraFileDescription", "Description");
        selenium.type("fileUploads:0:inpFileDescription", "Copyright Statment");
        selenium.type("fileUploads:0:fileLicenseDate", "2010-10-23");
        selenium.type("fileUploads:0:inpLicenseUrl", "cc by");
        selenium.click("fileUploads:0:btnAddFile");
        selenium.waitForPageToLoad("30000");
        selenium.type("fileUploads:1:inpAddFileFromUrl", "http://edoc.mpg.de/get.epl?fid=60832&did=376632&ver=0");
        selenium.select("fileUploads:1:selFileContentCategory", "label=Any fulltext");
        selenium.click("fileUploads:1:btnUploadFileFromUrl");
        selenium.select("fileUploads:1:selFileVisibility", "label=Private");
        selenium.waitForPageToLoad("30000");
        selenium.type("fileUploads:1:fileEmbargoDate", "2010");
        selenium.type("fileUploads:1:inpExtraFileDescription", "description");
        selenium.type("fileUploads:1:inpFileDescription", "copyright");
        selenium.type("fileUploads:1:fileLicenseDate", "2011");
        selenium.type("fileUploads:1:inpLicenseUrl", "cc by"); 
    }
    
    public void fillLocatorSection() {
        selenium.type("locatorUploads:0:inpAddUrl", "http://edoc.mpg.de/376632");
        selenium.click("locatorUploads:0:btnSaveUrl");
        selenium.select("locatorUploads:0:selLocatorContentCategory", "label=Supplementary material");
        selenium.type("locatorUploads:0:inpLocatorDescription", "Description");
        selenium.type("locatorUploads:0:j_id_jsp_1055636224_268", "Copyright Statement");
        selenium.type("locatorUploads:0:locatorLicenseDate", "2005");
        selenium.type("locatorUploads:0:inpLicensingInfo", "cc by"); 
    }
    
    public void fillContentSection() {
        selenium.open("/pubman/faces/EditItemPage.jsp");
        selenium.type("inputFreeKeywords", "testen, testing");
        selenium.type("iterContentGroupDDCSubjectList:0:inputSubjectSuggest", "001 - Knowledge");
        selenium.click("iterContentGroupDDCSubjectList:0:btnAddSubject");
        selenium.type("iterContentGroupDDCSubjectList:1:inputSubjectSuggest", "002 - Keine Ahnung");
        selenium.type("iterContentGroupAbstract:0:inputAbstractValue", "ABstract 1");
        selenium.click("iterContentGroupAbstract:0:btnAddAbstract");
        selenium.type("iterContentGroupAbstract:1:inputAbstractValue", "Abstract 2"); 
    }

    public String doEasySubmission(PubmanItem item)
    {
        selenium.open("/pubman/faces/HomePage.jsp");
        selenium.click("form1:Header:lnkSubmission");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.click("lblSubmission_lnkEasySubmission");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.click("easySubmission:CollectionSelection:iterDepositorContextList:lnkSelectForEasySubmissionFirst");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.click("easySubmission:easySubmissionStep1Manual:btnChangeGenre");
        selenium.select("easySubmission:easySubmissionStep1Manual:selGenre", "value=" + item.getGenre());
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.type("easySubmission:easySubmissionStep1Manual:inpItemMetadataTitle", item.getBasicList().get(0).title);
//        selenium.type("easySubmission:easySubmissionStep1Manual:fileUploads:0:inpFileUploaded",
//                        "/home/endres/projects/escidoc/workdir/solutions/pubman/pubman_ear/src/test/resources/uploadData/ABC_Pt_Mor_May_2009_final.ppt");
//        selenium.click("easySubmission:easySubmissionStep1Manual:fileUploads:0:btnUploadFile");
//        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
//        selenium.select("easySubmission:easySubmissionStep1Manual:fileUploads:0:selContentCategory",
//                "value=any-fulltext");
//        selenium.type("easySubmission:easySubmissionStep1Manual:fileUploads:0:inpComponentDescription",
//                "description_uploadFile");
//        selenium.type("easySubmission:easySubmissionStep1Manual:fileUploads:0:inpComponentFileDefaultMetadataRights",
//                "copyright_info");
//        selenium.type("easySubmission:easySubmissionStep1Manual:locatorUploads:0:inpLocatorLocator1",
//                "http://external.org");
//        selenium.click("easySubmission:easySubmissionStep1Manual:locatorUploads:0:lblEasySubmissionSaveLocator");
//        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
//        selenium.click("easySubmission:easySubmissionStep1Manual:locatorUploads:0:selLocatorFileVisibility:0");
//        selenium.type("easySubmission:easySubmissionStep1Manual:locatorUploads:0:inpLocatorFileDescription",
//                "external_locator_info");
//        selenium.type("easySubmission:easySubmissionStep1Manual:locatorUploads:0:inpLocatorFileDefaultMetadataRights",
//                "external_locator_info");
//        selenium.type("easySubmission:easySubmissionStep1Manual:locatorUploads:0:locatorLicenseDate", "2009-10-14");
// 
        selenium.click("easySubmission:easySubmissionStep1Manual:lnkNext");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.select("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:selRoleString",
                "value=" + item.getPersonOrganizationList().get(0).creatorRole.toString());
        selenium.click("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:selCreatorTypeString:0");
        selenium.type("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:inpCreatorPersonFamilyName",
                item.getPersonOrganizationList().get(0).lastName);
        selenium.type("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:inpCreatorPersonGivenName",
                item.getPersonOrganizationList().get(0).firstName);
        selenium.type("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:iterCreatorPersonOrganisation:0:inpPersOrgaName",
                item.getPersonOrganizationList().get(0).orgaName);
        selenium.type("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:iterCreatorPersonOrganisation:0:inpPersOrgaAddress",
                item.getPersonOrganizationList().get(0).orgaAddress);
        selenium.type("easySubmission:easySubmissionStep2Manual:inpFreeKeywords", item.getContentList().get(0).keywords);
        selenium.type("easySubmission:easySubmissionStep2Manual:inpSubject", item.getContentList().get(0).ddcSubject);
        selenium.type("easySubmission:easySubmissionStep2Manual:inpAbstract", item.getContentList().get(0).contentAbstract);
        selenium.click("easySubmission:easySubmissionStep2Manual:lnkNext");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.type("easySubmission:easySubmissionStep3Manual:txtDatePublishedInPrint", 
                item.getDetailsList().get(0).datePublishedInPrint);
        selenium.select("easySubmission:easySubmissionStep3Manual:selSourceGenre", "value="+
                item.getSourceList().get(0).genre.toString());
        selenium.type("easySubmission:easySubmissionStep3Manual:inpSourceTitle", item.getSourceList().get(0).title);
        selenium.type("easySubmission:easySubmissionStep3Manual:inpSourceDetailsVolume", item.getSourceList().get(0).volume);
        selenium.type("easySubmission:easySubmissionStep3Manual:inpSourceDetailsIssue", item.getSourceList().get(0).issue);
        selenium.type("easySubmission:easySubmissionStep3Manual:inpSourceDetailsStartPage", item.getSourceList().get(0).startPage);
        selenium.type("easySubmission:easySubmissionStep3Manual:inpSourceDetailsEndPage", item.getSourceList().get(0).endPage);
        selenium.click("easySubmission:easySubmissionStep3Manual:lnkSave");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.captureScreenshot("/tmp/error.png");
        String objectId = selenium.getText("txtObjectIdAndVersion");
        log.info("Created object: " + objectId);
        return objectId;
    }
}
