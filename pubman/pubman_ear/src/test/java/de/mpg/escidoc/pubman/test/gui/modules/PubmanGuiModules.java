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
 * Copyright 2006-20110 Fachinformationszentrum Karlsruhe Gesellschaft
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
        this.typeAndCheck("j_username", username);
        this.typeAndCheck("j_password", password);
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
        selenium.click("form1:Header:lnkSubmission");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.click("lblSubmission_lnkNewSubmission");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.click("iterdepositorContextList2:lnkSelectContext");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT); 
    }
    
    public void fillBasicSection() {
        typeAndCheck("inputTitleText", "test title");
        selenium.click("btnAddTitle");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        typeAndCheck("iterAlternativeTitle:0:inpAlternativeTitle", "alternative title");
        selenium.click("iterAlternativeTitle:0:btnAddAlternativeTitle");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        typeAndCheck("iterAlternativeTitle:1:inpAlternativeTitle", "2nd alternative title"); 
    }
    
    public void fillFileSection() {
        typeAndCheck("fileUploads:0:inpFile", "C:\\Dokumente und Einstellungen\\nek\\Desktop\\EndNote_Import_Despoina.txt");
        selenium.click("fileUploads:0:btnUploadFile");
        selenium.select("fileUploads:0:selFileContentCategory", "label=Preprint");
        selenium.select("fileUploads:0:selFileVisibility", "label=Restricted");
        typeAndCheck("fileUploads:0:fileEmbargoDate", "2009-12-31");
        typeAndCheck("fileUploads:0:inpExtraFileDescription", "Description");
        typeAndCheck("fileUploads:0:inpFileDescription", "Copyright Statment");
        typeAndCheck("fileUploads:0:fileLicenseDate", "2010-10-23");
        typeAndCheck("fileUploads:0:inpLicenseUrl", "cc by");
        selenium.click("fileUploads:0:btnAddFile");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        typeAndCheck("fileUploads:1:inpAddFileFromUrl", "http://edoc.mpg.de/get.epl?fid=60832&did=376632&ver=0");
        selenium.select("fileUploads:1:selFileContentCategory", "label=Any fulltext");
        selenium.click("fileUploads:1:btnUploadFileFromUrl");
        selenium.select("fileUploads:1:selFileVisibility", "label=Private");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        typeAndCheck("fileUploads:1:fileEmbargoDate", "2010");
        typeAndCheck("fileUploads:1:inpExtraFileDescription", "description");
        typeAndCheck("fileUploads:1:inpFileDescription", "copyright");
        typeAndCheck("fileUploads:1:fileLicenseDate", "2011");
        typeAndCheck("fileUploads:1:inpLicenseUrl", "cc by"); 
    }
    
    public void fillLocatorSection() {
        typeAndCheck("locatorUploads:0:inpAddUrl", "http://edoc.mpg.de/376632");
        selenium.click("locatorUploads:0:btnSaveUrl");
        selenium.select("locatorUploads:0:selLocatorContentCategory", "label=Supplementary material");
        typeAndCheck("locatorUploads:0:inpLocatorDescription", "Description");
        typeAndCheck("locatorUploads:0:inpCopyrightStatement", "Copyright Statement");
        typeAndCheck("locatorUploads:0:locatorLicenseDate", "2005");
        typeAndCheck("locatorUploads:0:inpLicensingInfo", "cc by"); 
    }
    
    public void fillContentSection() {
        typeAndCheck("inputFreeKeywords", "testen, testing");
        typeAndCheck("iterContentGroupDDCSubjectList:0:inputSubjectSuggest", "001 - Knowledge");
        selenium.click("iterContentGroupDDCSubjectList:0:btnAddSubject");
        typeAndCheck("iterContentGroupDDCSubjectList:1:inputSubjectSuggest", "002 - Keine Ahnung");
        typeAndCheck("iterContentGroupAbstract:0:inputAbstractValue", "ABstract 1");
        selenium.click("iterContentGroupAbstract:0:btnAddAbstract");
        typeAndCheck("iterContentGroupAbstract:1:inputAbstractValue", "Abstract 2"); 
    }
    
    public void fillDetailsSection() {
        selenium.select("lgTable:0:selectLanguageOfPublication", "label=en - English");
        selenium.click("lgTable:0:btnAddLanguageOfPublication");
        selenium.select("lgTable:1:selectLanguageOfPublication", "label=de - German");
        typeAndCheck("txtDatePublishedInPrint", "2009-10-23");
        typeAndCheck("txtDatePublishedOnline", "2009-10-22");
        typeAndCheck("txtDateAccepted", "2009-10-21");
        typeAndCheck("txtDateSubmitted", "2009-10-19");
        typeAndCheck("txtDateModified", "2008-10-26");
        typeAndCheck("txtDateCreated", "2007-10-26");
        typeAndCheck("txtTotalNoOfPages", "345 p.");
        typeAndCheck("txtaPublisher", "publisher");
        typeAndCheck("txtPlace", "place");
        typeAndCheck("txtaTableOfContent", "1) Einleitung\n2) Hauptteil");
        selenium.select("cboReviewType", "label=Internal");
        selenium.select("iterDetailGroupIdentifier:0:selSelectIdentifierType", "label=ISBN");
        typeAndCheck("iterDetailGroupIdentifier:0:inpIdentifierValue", "3-234-456-X");
        selenium.click("iterDetailGroupIdentifier:0:btnAddDetails");
        selenium.select("iterDetailGroupIdentifier:1:selSelectIdentifierType", "label=DOI");
        typeAndCheck("iterDetailGroupIdentifier:1:inpIdentifierValue", "iuladsfokjaäsdfl");
    }
    
    public void fillEventSection() {
        typeAndCheck("inpEventTitle", "Title of Event");
        selenium.click("btnAddEventTitle");
        typeAndCheck("iterAlternativeEventTitle:0:inpAlternateEventTitle", "Alternative Title 1");
        selenium.click("iterAlternativeEventTitle:0:btnAddAlternateEventTitle");
        typeAndCheck("iterAlternativeEventTitle:1:inpAlternateEventTitle", "Alternative Title 2");
        typeAndCheck("txtEventPlace", "München");
        typeAndCheck("txtEventStartDate", "2009-10-25");
        typeAndCheck("txtEventEndDate", "2009-10-26"); 
    }
    
    public void fillSourceSection() {
        selenium.select("editSource:0:selChooseSourceGenre", "label=Journal");
        typeAndCheck("editSource:0:inpSourceTitle", "Test journal title");
        selenium.click("editSource:0:btnAddSourceTitle");
        typeAndCheck("editSource:0:iterSourceAlternativeTitle:0:inpSourceAlternativeTitle", "Test alternative title 1");
        selenium.click("editSource:0:iterSourceAlternativeTitle:0:btnAddSourceAlternativeTitle");
        typeAndCheck("editSource:0:iterSourceAlternativeTitle:1:inpSourceAlternativeTitle", "Test alternative title 2");
        selenium.select("editSource:0:iterSourceGroupCreator:0:selSourceCreatorRole", "label=Author");
        typeAndCheck("editSource:0:iterSourceGroupCreator:0:inpSourceCreatorNameOptional", "Source");
        typeAndCheck("editSource:0:iterSourceGroupCreator:0:inpSourceCreatorGivenName", "Creator");
        typeAndCheck("editSource:0:iterSourceGroupCreator:0:iterSourceCreatorPersonAffiliation:0:inpSourceCreatorPersonAffiliationName", "MPG");
        selenium.click("editSource:0:iterSourceGroupCreator:0:iterSourceCreatorPersonAffiliation:0:btnAddSourceCreatorPersonAffiliation");
        typeAndCheck("editSource:0:iterSourceGroupCreator:0:iterSourceCreatorPersonAffiliation:1:inpSourceCreatorPersonAffiliationName", "External");
        selenium.click("editSource:0:iterSourceGroupCreator:0:btnRemoveSourceCreatorOrgaAffiliation");
        selenium.select("editSource:0:iterSourceGroupCreator:1:selSourceCreatorRole", "label=Editor");
        selenium.click("editSource:0:iterSourceGroupCreator:1:selSourceCreatorType:1");
        typeAndCheck("editSource:0:iterSourceGroupCreator:1:inpSourceCreatorOrgaAffiliationName", "MPS");
        typeAndCheck("editSource:0:inpSourceDetailVolume", "VII");
        typeAndCheck("editSource:0:inpSourceNumberOfPagesLabel", "259");
        typeAndCheck("editSource:0:inpSourceDetailPublisher", "Publisher");
        typeAndCheck("editSource:0:inpSourceDetailPublisherLabel", "Place");
        selenium.select("editSource:0:iterSourceDetailsIdentifier:0:selSourceDetailIdentifier", "label=ISI");
        typeAndCheck("editSource:0:iterSourceDetailsIdentifier:0:inpSourceDetailIdentifier", "asdfasdfasdf");
        selenium.click("editSource:0:iterSourceDetailsIdentifier:0:btnAddSourceDetailIdentifier");
        selenium.select("editSource:0:iterSourceDetailsIdentifier:1:selSourceDetailIdentifier", "label=PMC");
        typeAndCheck("editSource:0:iterSourceDetailsIdentifier:1:inpSourceDetailIdentifier", "asdfasdfasdf");
        typeAndCheck("editSource:0:inpSourceDetailSourceIssue", "67");
        typeAndCheck("editSource:0:inpSourceDetailStartPage", "589");
        typeAndCheck("editSource:0:inpSourceDetailEndPage", "456");
        typeAndCheck("editSource:0:inpSourceDetailSeqNumber", "58896");
        selenium.click("editSource:0:btnRemoveEditItem_btRemoveSource"); 
    }

    public String doEasySubmission(PubmanItem item)
    {
        selenium.click("form1:Header:lnkSubmission");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.click("lblSubmission_lnkEasySubmission");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        selenium.click("easySubmission:CollectionSelection:iterDepositorContextList:lnkSelectForEasySubmissionFirst");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        
        doEasySubmissionStep1( item );
        
        selenium.click("easySubmission:easySubmissionStep1Manual:lnkNext");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        
        doEasySubmissionStep2(item);
        
        selenium.click("easySubmission:easySubmissionStep2Manual:lnkNext");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        
        doEasySubmissionStep3(item);
        
        selenium.click("easySubmission:easySubmissionStep3Manual:lnkSave");
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        String objectId = selenium.getText("txtObjectIdAndVersion");
        log.info("Created object: " + objectId);
        return objectId;
    }
    
    public void doEasySubmissionStep1(PubmanItem item) {
        selenium.click("easySubmission:easySubmissionStep1Manual:btnChangeGenre");
        selenium.select("easySubmission:easySubmissionStep1Manual:selGenre", "value=" + item.getGenre());
        selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
        typeAndCheck("easySubmission:easySubmissionStep1Manual:inpItemMetadataTitle", item.getBasicList().get(0).title);
        for( int i = 0; i < item.getFileList().size(); i++ ) {
            typeAndCheck("easySubmission:easySubmissionStep1Manual:fileUploads:"+i+":inpFileUploaded",
                        item.getFileList().get(i).uploadUrl);
            selenium.click("easySubmission:easySubmissionStep1Manual:fileUploads:"+i+":btnUploadFile");
            selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
            selenium.select("easySubmission:easySubmissionStep1Manual:fileUploads:"+i+":selContentCategory",
                    "value="+item.getFileList().get(i).contentCategory.toString().toLowerCase());
            typeAndCheck("easySubmission:easySubmissionStep1Manual:fileUploads:"+i+":inpComponentDescription",
                    item.getFileList().get(i).description);
            typeAndCheck("easySubmission:easySubmissionStep1Manual:fileUploads:"+i+":inpComponentFileDefaultMetadataRights",
                    item.getFileList().get(i).copyrightStatement);
            // if we have more data, open another section
            if( (i + 1) < item.getFileList().size() ) {
                selenium.click("easySubmission:easySubmissionStep1Manual:fileUploads:"+i+":btnAddFile");
                selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
            }
        }
        for( int i = 0; i < item.getLocatorsList().size(); i++ ) {
            typeAndCheck("easySubmission:easySubmissionStep1Manual:locatorUploads:"+i+":inpLocatorLocator1",
                    item.getLocatorsList().get(i).uploadUrl);
            selenium.click("easySubmission:easySubmissionStep1Manual:locatorUploads:"+i+":lblEasySubmissionSaveLocator");
            selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
            selenium.click("easySubmission:easySubmissionStep1Manual:locatorUploads:"+i+":selLocatorFileVisibility:0");
            typeAndCheck("easySubmission:easySubmissionStep1Manual:locatorUploads:"+i+":inpLocatorFileDescription",
                item.getLocatorsList().get(i).description);
            typeAndCheck("easySubmission:easySubmissionStep1Manual:locatorUploads:"+i+":inpLocatorFileDefaultMetadataRights",
                item.getLocatorsList().get(i).copyrightStatement);
            typeAndCheck("easySubmission:easySubmissionStep1Manual:locatorUploads:"+i+":locatorLicenseDate", 
                item.getLocatorsList().get(i).copyrightDate);
            // if we have more data, open another section
            if( (i + 1) < item.getLocatorsList().size() ) {
                selenium.click("easySubmission:easySubmissionStep1Manual:locatorUploads:"+i+":btnEasySubmissionAddLocator");
                selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
            }
        }
    }
    public void doEasySubmissionStep2(PubmanItem item) {
        if( item.getPersonOrganizationList().get(0).creatorRole != null ) {
            selenium.select("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:0:selRoleString",
                    "value=" + item.getPersonOrganizationList().get(0).creatorRole.toString());
        }
        for( int i=0; i < item.getPersonOrganizationList().size(); i++ ) {
            if( item.getPersonOrganizationList().get(i).creatorType != null ) {
                selenium.click("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:"+i+":selCreatorTypeString:"+
                        item.getPersonOrganizationList().get(i).creatorType.ordinal());
            }
            typeAndCheck("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:"+i+":inpCreatorPersonFamilyName",
                    item.getPersonOrganizationList().get(i).lastName);
            typeAndCheck("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:"+i+":inpCreatorPersonGivenName",
                    item.getPersonOrganizationList().get(i).firstName);
            for( int u = 0; u < item.getPersonOrganizationList().get(i).getOrganizationList().size(); u++ ) {
                typeAndCheck("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:"+i+":iterCreatorPersonOrganisation:0:inpPersOrgaName",
                        item.getPersonOrganizationList().get(i).getOrganizationList().get(u).orgaName);
                typeAndCheck("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:"+i+":iterCreatorPersonOrganisation:0:inpPersOrgaAddress",
                        item.getPersonOrganizationList().get(i).getOrganizationList().get(u).orgaAddress);
                // if we have more data, open another section
                if( (u + 1) < item.getPersonOrganizationList().get(i).getOrganizationList().size() ) {
                    selenium.click("easySubmission:easySubmissionStep2Manual:iterCreatorCollection:"+i+":iterCreatorPersonOrganisation:"+u+":btnPersonOrganisationManagerAddObject");
                    selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
                }
            }
            // if we have more data, open another section
            if( (i + 1) < item.getLocatorsList().size() ) {
                selenium.click("easySubmission:easySubmissionStep1Manual:locatorUploads:"+i+":btnEasySubmissionAddLocator");
                selenium.waitForPageToLoad(MAX_PAGELOAD_TIMEOUT);
            }
        }
        typeAndCheck("easySubmission:easySubmissionStep2Manual:inpFreeKeywords", item.getContentList().get(0).keywords);
        typeAndCheck("easySubmission:easySubmissionStep2Manual:inpSubject", item.getContentList().get(0).ddcSubject);
        typeAndCheck("easySubmission:easySubmissionStep2Manual:inpAbstract", item.getContentList().get(0).contentAbstract);
    }
    public void doEasySubmissionStep3(PubmanItem item) {
        typeAndCheck("easySubmission:easySubmissionStep3Manual:txtDatePublishedInPrint", 
                item.getDetailsList().get(0).datePublishedInPrint);
        typeAndCheck("easySubmission:easySubmissionStep3Manual:txtDatePublishedOnline", 
                item.getDetailsList().get(0).datePublishedOnline);
        typeAndCheck("easySubmission:easySubmissionStep3Manual:txtDateAccepted",
                item.getDetailsList().get(0).dateAccepted);
        typeAndCheck("easySubmission:easySubmissionStep3Manual:txtDateSubmitted", 
                item.getDetailsList().get(0).dateSubmitted);
        typeAndCheck("easySubmission:easySubmissionStep3Manual:txtDateModified", 
                item.getDetailsList().get(0).dateModified);
        typeAndCheck("easySubmission:easySubmissionStep3Manual:txtDateCreated", 
                item.getDetailsList().get(0).dateCreated);
        selenium.select("easySubmission:easySubmissionStep3Manual:selSourceGenre", "value="+
                item.getSourceList().get(0).genre.toString());
        typeAndCheck("easySubmission:easySubmissionStep3Manual:inpSourceTitle", 
                item.getSourceList().get(0).title);
        typeAndCheck("easySubmission:easySubmissionStep3Manual:inpSourceDetailsVolume", 
                item.getSourceList().get(0).volume);
        typeAndCheck("easySubmission:easySubmissionStep3Manual:inpSourceDetailsIssue", 
                item.getSourceList().get(0).issue);
        typeAndCheck("easySubmission:easySubmissionStep3Manual:inpSourceDetailsStartPage", 
                item.getSourceList().get(0).startPage);
        typeAndCheck("easySubmission:easySubmissionStep3Manual:inpSourceDetailsEndPage", 
                item.getSourceList().get(0).endPage); 
    }
    
    public void checkViewItemPage( PubmanItem item ) {
        
    }
    
    private void typeAndCheck( String target, String value ) {
        if( value != null ) {
            selenium.type(target, value);
        }
    }
}
