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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO.InvitationStatus;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FundingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FundingOrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FundingProgramVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.ProjectInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.ReviewMethod;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.util.DOMUtilities;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * Base Class for tests in common_logic.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 03.09.2007
 */
public class TestBase {
  protected static final String TEST_FILE_ROOT = "target/test-classes/";
  protected static final String ITEM_FILE = TEST_FILE_ROOT + "schindlmayr-springer.xml";
  protected static final String COMPONENT_FILE = TEST_FILE_ROOT + "schindlmayr-springer.pdf";
  protected static final String MIME_TYPE = "application/pdf";
  protected static final String PUBMAN_TEST_COLLECTION_ID = "escidoc:persistent3";
  protected static final String PUBMAN_TEST_COLLECTION_NAME = "PubMan Test Collection";
  protected static final String PUBMAN_TEST_COLLECTION_DESCRIPTION = "This is the sample collection " + "description of the PubMan Test\n"
      + "collection. Any content can be stored in this collection, which is of relevance\n"
      + "for the users of the system. You can submit relevant bibliographic information\n"
      + "for your publication (metadata) and all relevant files. The MPS is the\n"
      + "responsible affiliation for this collection. Please contact\n" + "u.tschida@zim.mpg.de for any questions.";

  private static Map<String, Schema> schemas = null;

  /**
   * Logger for this class.
   */
  private static final Logger logger = LogManager.getLogger(TestBase.class);



  /**
   * Creates a well-defined PubItemVO without any files attached.
   * 
   * @return pubItem
   */
  protected PubItemVO getPubItemWithoutFiles() {
    PubItemVO item = new PubItemVO();

    item.setBaseUrl("http://myBaseUrl.org");

    // Metadata
    MdsPublicationVO mds = getMdsPublication1();
    item.setMetadata(mds);

    // PubCollectionRef
    ContextRO contextRef = new ContextRO();
    contextRef.setObjectId(PUBMAN_TEST_COLLECTION_ID);
    item.setContext(contextRef);
    try {
      //      String contentModel = PropertyReader.getProperty(PropertyReader.ESCIDOC_FRAMEWORK_ACCESS_CONTENT-MODEL_ID_PUBLICATION);
      item.setContentModel("'dummy-content-model'");
    } catch (Exception e) {
      throw new RuntimeException("Error getting content-model", e);
    }
    ItemRO version = new ItemRO("escidoc:123");
    version.setVersionNumber(1);
    version.setState(ItemVO.State.PENDING);
    version.setModificationDate(new Date());

    item.setVersion(version);


    return item;
  }

  /**
   * Creates another well-defined PubItemVO.
   * 
   * @return pubItem
   */
  /*
  protected PubItemVO getPubItem2() {
    PubItemVO item = new PubItemVO();
  
    // (1) metadata
    MdsPublicationVO mds = getMdsPublication2();
    item.setMetadata(mds);
  
    // (2) pubCollection
    ContextRO collectionRef = new ContextRO();
    collectionRef.setObjectId("escidoc:persistent3");
    item.setContext(collectionRef);
  
    return item;
  }
  */

  /**
   * Creates a well-defined PubItemVO named "PubMan: The first of all.".
   * 
   * @return pubItem
   * 
   * @throws Exception Any exception
   */
  protected PubItemVO getPubItemNamedTheFirstOfAll() throws Exception {
    PubItemVO item = new PubItemVO();

    item.setBaseUrl("http://myBaseUrl.org");

    // properties of the item
    // PubCollectionRef
    ContextRO collectionRef = new ContextRO();
    collectionRef.setObjectId(PUBMAN_TEST_COLLECTION_ID);
    item.setContext(collectionRef);

    // item metadata
    MdsPublicationVO mds = new MdsPublicationVO();
    // title
    mds.setTitle("PubMan: The first of all.");
    // genre
    mds.setGenre(Genre.BOOK);
    // creator(s)
    // Add a creator[person] that is affiliated to one organization
    CreatorVO creator = new CreatorVO();
    creator.setRole(CreatorRole.AUTHOR);
    PersonVO person = new PersonVO();
    person.setGivenName("Hans");
    person.setFamilyName("Meier");
    //person.setCompleteName("Hans Meier");
    OrganizationVO organizationVO = new OrganizationVO();
    organizationVO.setName("Test Organization");
    organizationVO.setAddress("Max-Planck-Str. 1");
    person.getOrganizations().add(organizationVO);
    creator.setPerson(person);
    mds.getCreators().add(creator);
    mds.getCreators().add((CreatorVO) creator.clone());
    // dates
    mds.setDateCreated("2007");
    mds.setDatePublishedInPrint("2007-01-02");
    mds.setDatePublishedOnline("2007-03-04");
    // source(s)
    SourceVO source = new SourceVO();
    source.setTitle("The title of the source");
    source.setGenre(SourceVO.Genre.JOURNAL);
    // event
    EventVO event = new EventVO();
    event.setStartDate("2007-10-31");
    event.setEndDate("2007-12-31");
    event.setInvitationStatus(InvitationStatus.INVITED);
    event.setPlace("Füssen (nicht Füßen) im schwäbischen Landkreis Ostallgäu.");
    event.setTitle("Un bôn vín fràn\uc3a7ais");
    // subject
    String s1 = "This is the subject. Betreffs fußen auf Gerüchten für Äonen.";
    logger.debug("s1: " + s1.length() + " chars, " + s1.getBytes("UTF-8").length + " bytes, ü = " + (s1.contains("ü")));
    mds.setFreeKeywords(s1);
    // table of contents
    mds.setTableOfContents("I like to test with umlauts. Es grünt ßo grün, wenn Spániäns Blümälain blühn.");

    ProjectInfoVO projectInfo = new ProjectInfoVO();
    projectInfo.setTitle("Test Project Name");
    IdentifierVO grantIdentifier = new IdentifierVO(IdType.GRANT_ID, "grantIdentifier1234");
    projectInfo.setGrantIdentifier(grantIdentifier);

    FundingInfoVO fundingInfo = new FundingInfoVO();
    FundingOrganizationVO fundingOrganization = new FundingOrganizationVO();
    fundingOrganization.setTitle("Test Funding Organization European Council");
    fundingOrganization.getIdentifiers().add(new IdentifierVO(IdType.OPEN_AIRE, "EC"));
    FundingProgramVO fundingProgram = new FundingProgramVO();
    fundingProgram.setTitle("Test funding Horizon2020");
    fundingProgram.getIdentifiers().add(new IdentifierVO(IdType.OPEN_AIRE, "H2020"));


    fundingInfo.setFundingOrganization(fundingOrganization);
    fundingInfo.setFundingProgram(fundingProgram);

    projectInfo.setFundingInfo(fundingInfo);



    mds.getProjectInfo().add(projectInfo);
    item.setMetadata(mds);

    return item;
  }

  /**
   * Creates a well-defined PubItemVO named "PubMan: The first of all.".
   * 
   * @return pubItem
   */
  /*
  protected ItemResultVO getPubItemResultNamedTheFirstOfAll() {
    ItemResultVO itemResult = new ItemResultVO();
  
    // properties of the item
    // PubCollectionRef
    ContextRO collectionRef = new ContextRO();
    collectionRef.setObjectId(PUBMAN_TEST_COLLECTION_ID);
    itemResult.setContext(collectionRef);
  
    // item metadata
    MdsPublicationVO mds = new MdsPublicationVO();
    // title
    mds.setTitle("PubMan: The first of all.");
    // genre
    mds.setGenre(Genre.BOOK);
    // creator(s)
    // Add a creator[person] that is affiliated to one organization
    CreatorVO creator = new CreatorVO();
    creator.setRole(CreatorRole.AUTHOR);
    PersonVO person = new PersonVO();
    person.setGivenName("Hans");
    person.setFamilyName("Meier");
    person.setCompleteName("Hans Meier");
    OrganizationVO organizationVO = new OrganizationVO();
    organizationVO.setName("Test Organization");
    organizationVO.setAddress("Max-Planck-Str. 1");
    person.getOrganizations().add(organizationVO);
    creator.setPerson(person);
    mds.getCreators().add(creator);
    mds.getCreators().add(creator);
    // dates
    mds.setDateCreated("2007");
    mds.setDatePublishedInPrint("2007-01-02");
    mds.setDatePublishedOnline("2007-03-04");
    // source(s)
    SourceVO source = new SourceVO();
    source.setTitle("The title of the source");
    source.setGenre(SourceVO.Genre.JOURNAL);
    itemResult.getMetadataSets().add(mds);
  
    return itemResult;
  }
  */

  /**
   * Creates a well-defined, complex PubItemVO without files.
   * 
   * @return pubItem
   */
  protected PubItemVO getComplexPubItemWithoutFiles() {
    PubItemVO item = new PubItemVO();

    // Metadata
    MdsPublicationVO mds = getMdsPublication1();
    item.setMetadata(mds);

    // PubCollectionRef
    ContextRO collectionRef = new ContextRO();
    collectionRef.setObjectId(PUBMAN_TEST_COLLECTION_ID);
    item.setContext(collectionRef);

    return item;
  }

  /**
   * Creates a well-defined, complex MdsPublicationVO.
   * 
   * @return The generated MdsPublicationVO.
   */
  protected MdsPublicationVO getMdsPublication1() {
    // Metadata
    MdsPublicationVO mds = new MdsPublicationVO();

    // Genre
    mds.setGenre(Genre.BOOK);

    // Creator
    CreatorVO creator = createCreator1();
    mds.getCreators().add(creator);
    creator = createCreator2();
    mds.getCreators().add(creator);

    // Title
    mds.setTitle("Über den Wölken. The first of all. Das Maß aller Dinge.");

    // Language
    mds.getLanguages().add("de");
    mds.getLanguages().add("en");
    mds.getLanguages().add("fr");

    // Alternative Title
    mds.getAlternativeTitles().add(new AlternativeTitleVO("Die Erste von allen.", "de"));
    mds.getAlternativeTitles().add(new AlternativeTitleVO("Wulewu", "fr"));

    // Identifier
    mds.getIdentifiers().add(new IdentifierVO(IdType.ISI, "0815"));
    mds.getIdentifiers().add(new IdentifierVO(IdType.ISSN, "issn"));

    // Publishing Info
    PublishingInfoVO pubInfo;
    pubInfo = new PublishingInfoVO();
    pubInfo.setPublisher("O'Reilly Media Inc., 1005 Gravenstein Highway North, Sebastopol");
    pubInfo.setEdition("One and a half");
    pubInfo.setPlace("Garching-Itzehoe-Capreton");
    mds.setPublishingInfo(pubInfo);

    // Date
    mds.setDateCreated("2005-02");
    mds.setDateSubmitted("2005-08-31");
    mds.setDateAccepted("2005");
    mds.setDatePublishedInPrint("2006-02-01");
    mds.setDateModified("2007-02-27");

    // Review method
    mds.setReviewMethod(ReviewMethod.INTERNAL);

    // Source
    SourceVO source = createSource();
    mds.getSources().add(source);

    // Event
    EventVO event = createEvent();
    mds.setEvent(event);

    // Total Numeber of Pages
    mds.setTotalNumberOfPages("999");

    // Degree
    mds.setDegree(DegreeType.MASTER);

    // Abstracts
    mds.getAbstracts().add(new AbstractVO("Dies ist die Zusammenfassung der Veröffentlichung.", "de"));
    mds.getAbstracts().add(new AbstractVO("This is the summary of the publication.", "en"));

    // Subject
    mds.setFreeKeywords("wichtig,wissenschaftlich,spannend");

    // Table of Contents
    mds.setTableOfContents("1.Einleitung 2.Inhalt");

    // Location
    mds.setLocation("IPP, Garching");

    return mds;
  }

  /**
   * @return
   */
  private EventVO createEvent() {
    EventVO event = new EventVO();
    // Event.Title
    event.setTitle("Weekly progress meeting");
    // Event.StartDate
    event.setStartDate("2004-11-11");
    // Event.EndDate
    event.setEndDate("2005-02-19");
    // Event.Place
    // Event.InvitationStatus
    event.setInvitationStatus(InvitationStatus.INVITED);
    return event;
  }

  /**
   * @return
   */
  private SourceVO createSource() {
    CreatorVO creator;
    OrganizationVO organization;
    PublishingInfoVO pubInfo;
    SourceVO source = new SourceVO();
    // Source.Title
    source.setTitle("Dies ist die Wurzel allen Übels.");
    // Source.Genre
    // source.setGenre(SourceVO.Genre.SERIES);
    // Source.AlternativeTitle
    source.getAlternativeTitles().add(new AlternativeTitleVO("This is the root of all ???.", "en"));
    source.getAlternativeTitles()
        .add(new AlternativeTitleVO("< and & are illegal characters in XML and therefore have to be escaped.", "en"));
    source.getAlternativeTitles()
        .add(new AlternativeTitleVO("> and ' and ? are problematic characters in XML and therefore should be escaped.", "en"));
    source.getAlternativeTitles().add(new AlternativeTitleVO(
        "What about `, ´, äöüÄÖÜß, áàéèô, and the good old % (not to forget the /, the" + " \\, -, the _, the\n" + "~, the @ and the #)?",
        "en"));
    source.getAlternativeTitles().add(new AlternativeTitleVO("By the way, the Euro sign looks like this: €", "en"));
    // Source.Creator
    creator = new CreatorVO();
    // Source.Creator.Role
    creator.setRole(CreatorRole.AUTHOR);
    // Source.Creator.Organization
    organization = new OrganizationVO();
    // Source.Creator.Organization.Name
    organization.setName("murrrmurr");
    // Source.Creator.Organization.Address
    organization.setAddress("Ümläüte ßind ßchön. à bientôt!");
    // Source.Creator.Organization.Identifier
    organization.setIdentifier("BLA-BLU-BLÄ");
    creator.setOrganization(organization);
    source.getCreators().add(creator);
    // Source.Volume
    source.setVolume("8a");
    // Source.Issue
    source.setIssue("13b");
    // Source.StartPage
    source.setStartPage("-12");
    // Source.EndPage
    source.setEndPage("131313");
    // Source.SequenceNumber
    source.setSequenceNumber("1-3-6");
    // Source.PublishingInfo
    pubInfo = new PublishingInfoVO();
    // Source.PublishingInfo.Publisher
    pubInfo.setPublisher("Martas Druckerei");
    // Source.PublishingInfo.Edition
    pubInfo.setEdition("III");
    // Source.PublishingInfo.Place
    pubInfo.setPlace("Hamburg-München");
    source.setPublishingInfo(pubInfo);
    // Source.Identifier
    source.getIdentifiers().add(new IdentifierVO(IdType.ISBN, "XY-347H-112"));
    // Source.Source
    source.getSources().add(new SourceVO("The source of the source."));
    CreatorVO sourceSourceCreator = new CreatorVO(new OrganizationVO(), CreatorRole.ARTIST);
    sourceSourceCreator.getOrganization().setName("Creator of the Source of the source");
    sourceSourceCreator.getOrganization().setIdentifier("ID-4711-0815");
    source.getSources().get(0).getCreators().add(sourceSourceCreator);
    return source;
  }

  /**
   * @return
   */
  private CreatorVO createCreator2() {
    CreatorVO creator;
    OrganizationVO organization;
    creator = new CreatorVO();
    // Creator.Role
    creator.setRole(CreatorRole.CONTRIBUTOR);
    // Source.Creator.Organization
    organization = new OrganizationVO();
    // Creator.Organization.Name
    organization.setName("MPDL");
    // Creator.Organization.Address
    organization.setAddress("Amalienstraße");
    // Creator.Organization.Identifier
    organization.setIdentifier("1a");
    creator.setOrganization(organization);
    return creator;
  }

  /**
   * @return
   */
  private CreatorVO createCreator1() {
    CreatorVO creator;
    creator = new CreatorVO();
    // Creator.Role
    creator.setRole(CreatorRole.AUTHOR);
    // Creator.Person
    PersonVO person = new PersonVO();
    // Creator.Person.CompleteName
    //person.setCompleteName("Hans Meier");
    // Creator.Person.GivenName
    person.setGivenName("Hans");
    // Creator.Person.FamilyName
    person.setFamilyName("Meier");
    // Creator.Person.AlternativeName
    person.getAlternativeNames().add("Werner");
    person.getAlternativeNames()
        .add("These tokens are escaped and must stay escaped: \"&amp;\", \"&gt;\", " + "\"&lt;\", \"&quot;\", \"&apos;\"");
    person.getAlternativeNames().add("These tokens are escaped and must stay escaped, too: &auml; &Auml; &szlig;");
    // Creator.Person.Title
    person.getTitles().add("Dr. (?)");
    // Creator.Person.Pseudonym
    person.getPseudonyms().add("<b>Shorty</b>");
    person.getPseudonyms().add("<'Dr. Short'>");
    // Creator.Person.Organization
    OrganizationVO organization;
    organization = new OrganizationVO();
    // Creator.Person.Organization.Name
    organization.setName("Vinzenzmurr");
    // Creator.Person.Organization.Address
    organization.setAddress("<a ref=\"www.buxtehude.de\">Irgendwo in Deutschland</a>");
    // Creator.Person.Organization.Identifier
    organization.setIdentifier("ED-84378462846");
    person.getOrganizations().add(organization);
    // Creator.Person.Identifier
    person.setIdentifier(new IdentifierVO(IdType.PND, "HH-XY-2222"));
    creator.setPerson(person);
    return creator;
  }

  /**
   * Creates a well-defined, complex MdsPublicationVO.
   * 
   * @return The generated MdsPublicationVO.
   */
  /*
  protected MdsPublicationVO getMdsPublication2() {
    // Metadata
    MdsPublicationVO mds = new MdsPublicationVO();
  
    // Title
    mds.setTitle("The title");
  
    // Genre
    mds.setGenre(Genre.BOOK);
  
    // Creators
    CreatorVO creator = new CreatorVO();
    creator.setRole(CreatorRole.AUTHOR);
    PersonVO person = new PersonVO();
    person.setGivenName("Hans");
    person.setFamilyName("Meier");
    person.setCompleteName("Hans Meier");
    creator.setPerson(person);
    mds.getCreators().add(creator);
  
    // Dates
    mds.setDateCreated("2005-2");
    mds.setDateSubmitted("2005-8-31");
    mds.setDateAccepted("2005");
    mds.setDatePublishedInPrint("2006-2-1");
    mds.setDateModified("2007-2-29");
  
    // Identifiers
    List<IdentifierVO> identifierList = mds.getIdentifiers();
    IdentifierVO identifierVO = new IdentifierVO();
    identifierVO.setId("id1");
    identifierVO.setType(IdType.ISBN);
    for (int i = 0; i < 2; i++) {
      identifierList.add(identifierVO);
    }
  
    // Publishing info
    PublishingInfoVO publishingInfoVO = new PublishingInfoVO();
    publishingInfoVO.setEdition("Edition 123");
    publishingInfoVO.setPlace("Place 5");
    publishingInfoVO.setPublisher("Publisher XY");
    mds.setPublishingInfo(publishingInfoVO);
  
    // build the List of SourceVOs...
    List<SourceVO> sourcesList = mds.getSources();
  
    // build one SourceVO instance...
    SourceVO sourceVO = new SourceVO();
    sourceVO.setTitle("The title");
    List<AlternativeTitleVO> alternativeTitleList = sourceVO.getAlternativeTitles();
    for (int i = 0; i < 2; i++) {
      alternativeTitleList.add(new AlternativeTitleVO("The title", "en"));
    }
    List<CreatorVO> creatorList = sourceVO.getCreators();
    for (int i = 0; i < 2; i++) {
      creatorList.add(creator);
    }
    sourceVO.setVolume("Volume 100");
    sourceVO.setIssue("Issue 99");
    sourceVO.setStartPage("StartPage 23");
    sourceVO.setEndPage("Endpage is 54");
    sourceVO.setSequenceNumber("SequenceNumber 12");
    sourceVO.setPublishingInfo(publishingInfoVO);
    List<IdentifierVO> sourceIdentifierList = sourceVO.getIdentifiers();
    for (IdentifierVO id : identifierList) {
      sourceIdentifierList.add(id);
    }
  
    // build another SourceVO instance...
    SourceVO sourceVO2 = new SourceVO();
    sourceVO2.setTitle("The title");
    List<AlternativeTitleVO> alternativeTitleList2 = sourceVO2.getAlternativeTitles();
    for (int i = 0; i < 2; i++) {
      alternativeTitleList2.add(new AlternativeTitleVO("The title", "en"));
    }
    List<CreatorVO> creatorList2 = sourceVO2.getCreators();
    for (int i = 0; i < 2; i++) {
      creatorList2.add(creator);
    }
    sourceVO2.setVolume("Volume 100");
    sourceVO2.setIssue("Issue 99");
    sourceVO2.setStartPage("StartPage 23");
    sourceVO2.setEndPage("Endpage is 54");
    sourceVO2.setSequenceNumber("SequenceNumber 12");
    sourceVO2.setPublishingInfo(publishingInfoVO);
    List<IdentifierVO> sourceIdentifierList2 = sourceVO2.getIdentifiers();
    for (IdentifierVO id : identifierList) {
      sourceIdentifierList2.add(id);
    }
  
    // add several of the "other" SourceVO instances to the first SourceVO instance
    List<SourceVO> sourceSourcesList = sourceVO.getSources();
    for (int i = 0; i < 2; i++) {
      sourceSourcesList.add(sourceVO2);
    }
  
    // add SourceVO several times
    for (int i = 0; i < 2; i++) {
      sourcesList.add(sourceVO);
    }
  
    // Event
    EventVO event = new EventVO();
    // Event.Title
    event.setTitle("Länderübergreifende Änderungsüberlegungen");
    // Event.StartDate
    event.setStartDate("2000-02-29");
    // Event.EndDate
    event.setEndDate("2001-02-28");
    // Event.Place
    event.setPlace("Grevenbröich");
    // Event.InvitationStatus
    event.setInvitationStatus(InvitationStatus.INVITED);
    mds.setEvent(event);
  
    return mds;
  }
  */

  /**
   * Searches the Java classpath for a given file name and gives back the file (or a
   * FileNotFoundException).
   * 
   * @param fileName The name of the file
   * @return The file
   * @throws FileNotFoundException Thrown if the file was not found.
   */
  /*
  public static File findFileInClasspath(String fileName) throws FileNotFoundException {
    URL url = TestBase.class.getClassLoader().getResource(fileName);
    if (url == null) {
      throw new FileNotFoundException(fileName);
    }
    return new File(url.getFile());
  }
  */

  /**
   * Reads contents from text file and returns it as String.
   * 
   * @param fileName Name of input file
   * @return Entire contents of filename as a String
   * @throws IOException i/o exception
   */
  protected static String readFile(String fileName) throws IOException {
    return ResourceUtil.getResourceAsString(fileName, TestBase.class.getClassLoader());
  }

  /**
   * Search the given String for the first occurence of "objid" and return its value.
   * 
   * @param item A (XML) String
   * @return The objid value
   */
  protected static String getObjid(String item) {
    String result = "";
    String searchString = "objid=\"";
    int index = item.indexOf(searchString);
    if (index > 0) {
      item = item.substring(index + searchString.length());
      index = item.indexOf('\"');
      if (index > 0) {
        result = item.substring(0, index);
      }
    }
    return result;
  }

  /**
   * Search the given String for the first occurence of "last-modification-date" and return its
   * value.
   * 
   * @param item A (XML) String
   * @return The last-modification-date value
   */
  /*
  protected static String getLastModificationDate(String item) {
    String result = "";
    String searchString = "last-modification-date=\"";
    int index = item.indexOf(searchString);
    if (index > 0) {
      item = item.substring(index + searchString.length());
      index = item.indexOf('\"');
      if (index > 0) {
        result = item.substring(0, index);
      }
    }
    return result;
  }
  */

  /**
   * Assert that the Element/Attribute selected by the xPath exists.
   * 
   * @param message The message printed if assertion fails.
   * @param node The Node.
   * @param xPath The xPath.
   * @throws Exception If anything fails.
   */
  /*
  public static void assertXMLExist(final String message, final Node node, final String xPath) throws Exception {
    if (message == null) {
      throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":assertXMLExist:message is null");
    }
    if (node == null) {
      throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":assertXMLExist:node is null");
    }
    if (xPath == null) {
      throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":assertXMLExist:xPath is null");
    }
    NodeList nodes = DOMUtilities.selectNodeList(node, xPath);
    assertTrue(message, nodes.getLength() > 0);
  }
  */

  /**
   * Assert that the XML is valid to the schema.
   * 
   * @param xmlData The XML as string
   * @throws Exception Any exception
   */
  public static void assertXMLValid(final String xmlData) throws Exception {

    if (xmlData == null) {
      throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":assertXMLValid:xmlData is null");
    }

    if (schemas == null) {
      initializeSchemas();
    }

    String nameSpace = getNameSpaceFromXml(xmlData);

    logger.debug("Looking up namespace '" + nameSpace + "'");

    Schema schema = schemas.get(nameSpace);

    logger.info("Schema: " + schema);

    // FIXME tendres: fix this here that it will run on the build-server!
    // try
    // {
    // Validator validator = schema.newValidator();
    // InputStream in = new ByteArrayInputStream(xmlData.getBytes("UTF-8"));
    //
    // logger.info("Validator: " + validator);
    //
    // //validator.validate(new SAXSource(new InputSource(in)));
    // }
    // catch (SAXParseException e)
    // {
    // StringBuffer sb = new StringBuffer();
    // sb.append("XML invalid at line:" + e.getLineNumber() + ", column:" + e.getColumnNumber() +
    // "\n");
    // sb.append("SAXParseException message: " + e.getMessage() + "\n");
    // sb.append("Affected XML: \n" + xmlData);
    // fail(sb.toString());
    // }

  }

  public static void main(String[] args) throws Exception {
    String xml = args[0];
    assertXMLValid(xml);
  }

  /**
   * @param xmlData
   * @return
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   * @throws UnsupportedEncodingException
   */
  private static String getNameSpaceFromXml(final String xmlData)
      throws ParserConfigurationException, SAXException, IOException, UnsupportedEncodingException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    DefaultHandler handler = new DefaultHandler() {
      private String nameSpace = null;
      private boolean first = true;

      public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (first) {
          if (qName.contains(":")) {
            String prefix = qName.substring(0, qName.indexOf(":"));
            String attributeName = "xmlns:" + prefix;
            nameSpace = attributes.getValue(attributeName);
          } else {
            nameSpace = attributes.getValue("xmlns");
          }
          first = false;
        }

      }

      public String toString() {
        return nameSpace;
      }
    };
    parser.parse(new ByteArrayInputStream(xmlData.getBytes("UTF-8")), handler);
    String nameSpace = handler.toString();
    return nameSpace;
  }

  /**
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  private static void initializeSchemas() throws IOException, SAXException, ParserConfigurationException {
    File[] schemaFiles = ResourceUtil.getFilenamesInDirectory("xsd/", TestBase.class.getClassLoader());
    schemas = new HashMap<String, Schema>();
    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    for (File file : schemaFiles) {
      try {
        Schema schema = sf.newSchema(file);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        DefaultHandler handler = new DefaultHandler() {
          private String nameSpace = null;
          private boolean found = false;

          public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (!found) {
              String tagName = null;
              int ix = qName.indexOf(":");
              if (ix >= 0) {
                tagName = qName.substring(ix + 1);
              } else {
                tagName = qName;
              }
              if ("schema".equals(tagName)) {
                nameSpace = attributes.getValue("targetNamespace");
                found = true;
              }
            }
          }

          public String toString() {
            return nameSpace;
          }
        };
        parser.parse(file, handler);
        if (handler.toString() != null) {
          schemas.put(handler.toString(), schema);
        } else {
          logger.warn("Error reading xml schema: " + file);
        }

      } catch (Exception e) {
        logger.warn("Invalid xml schema " + file);
        logger.debug("Stacktrace: ", e);
      }

    }
  }

  /**
   * Return the text value of the selected attribute.
   * 
   * @param node The node.
   * @param xPath The xpath to select the node containint the attribute,
   * @param attributeName The name of the attribute.
   * @return The text value of the selected attribute.
   * @throws Exception If anything fails.
   */
  public static String getAttributeValue(final Node node, final String xPath, final String attributeName) throws Exception {
    if (node == null) {
      throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":getAttributeValue:node is null");
    }
    if (xPath == null) {
      throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":getAttributeValue:xPath is null");
    }
    if (attributeName == null) {
      throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":getAttributeValue:attributeName is null");
    }
    String result = null;
    Node attribute = DOMUtilities.selectSingleNode(node, xPath);
    if (attribute.hasAttributes()) {
      result = attribute.getAttributes().getNamedItem(attributeName).getTextContent();
    }
    return result;
  }

  /**
   * Serialize the given Dom Object to a String.
   * 
   * @param xml The Xml Node to serialize.
   * @param omitXMLDeclaration Indicates if XML declaration will be omitted.
   * @return The String representation of the Xml Node.
   * @throws Exception If anything fails.
   */
  /*
  protected static String toString(final Node xml, final boolean omitXMLDeclaration) throws Exception {
    if (xml == null) {
      throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":toString:xml is null");
    }
    String result = null;
  
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  
    // serialize
    DOMImplementation implementation = DOMImplementationRegistry.newInstance().getDOMImplementation("XML 3.0");
    DOMImplementationLS feature = (DOMImplementationLS) implementation.getFeature("LS", "3.0");
    LSSerializer serial = feature.createLSSerializer();
    LSOutput output = feature.createLSOutput();
    output.setByteStream(outputStream);
    serial.write(xml, output);
  
    result = output.toString();
  
    return result;
  }
  */


}
