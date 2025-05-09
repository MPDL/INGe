package de.mpg.mpdl.inge.es.connector;

import java.io.IOException;
import java.util.Date;

import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.es.spring.AppConfigIngeEsConnector;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO.State;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.types.Coordinates;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO.InvitationStatus;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FundingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FundingOrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FundingProgramVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.LegalCaseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.ProjectInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.ReviewMethod;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.SubjectClassification;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

@ContextConfiguration(classes = AppConfigIngeEsConnector.class)
public class TestBase {
  private static final Date DATE = new Date();


  ObjectMapper mapper = MapperFactory.getObjectMapper();

  public AffiliationDbVO test_ou() {
    AffiliationDbVO vo = new AffiliationDbVO();

    // ChildAffiliations
    /*
    AffiliationDbRO child = new AffiliationDbRO();
    child.setObjectId("testChild");
    child.setName("testTitle");
    vo.get.add(child);
    */

    vo.setCreationDate(DATE);

    // Creator
    AccountUserDbRO creator = new AccountUserDbRO();
    creator.setObjectId("testCreator");
    creator.setName("testTitle");
    vo.setCreator(creator);

    // MdsOrganizationalUnitDetails
    MdsOrganizationalUnitDetailsVO md = new MdsOrganizationalUnitDetailsVO();
    md.getAlternativeNames().add("testAlternativName");
    md.setCity("testCity");
    Coordinates coordinates = new Coordinates(1d, 2d, 3d);
    md.setCoordinates(coordinates);
    md.setCountryCode("testCountryCode");
    md.getDescriptions().add("testDescription");
    IdentifierVO identifier = new IdentifierVO(IdType.ARXIV, "testIdentifier");
    md.getIdentifiers().add(identifier);
    md.setEndDate("testEndDate");
    md.setName("testName");
    md.setStartDate("testStartDate");
    md.setTitle("testTitle");
    md.setType("testType");
    vo.setMetadata(md);

    vo.setLastModificationDate(DATE);

    // Modifier
    AccountUserDbRO modifier = new AccountUserDbRO();
    modifier.setObjectId("testModifier");
    modifier.setName("testModifier");
    vo.setModifier(modifier);

    // ParentAffiliations
    AffiliationDbRO parent = new AffiliationDbRO();
    parent.setObjectId("testParent");
    parent.setName("testTitle");
    vo.setParentAffiliation(parent);

    // PredecessorAffiliations
    AffiliationDbRO predecessor = new AffiliationDbRO();
    predecessor.setObjectId("testPredecessor");
    predecessor.setName("testTitle");
    vo.getPredecessorAffiliations().add(predecessor);

    vo.setPublicStatus(State.OPENED);

    // Reference
    vo.setObjectId("testReference");

    return vo;
  }

  public ContextDbVO test_context() {
    ContextDbVO vo = new ContextDbVO();


    vo.getAllowedGenres().add(Genre.ARTICLE);
    vo.getAllowedSubjectClassifications().add(SubjectClassification.MPIS_GROUPS);
    vo.setContactEmail("testContactEmail");
    vo.setWorkflow(ContextDbVO.Workflow.STANDARD);

    // Creator
    AccountUserDbRO creator = new AccountUserDbRO();
    creator.setObjectId("testCreator");
    creator.setName("testTitle");
    vo.setCreator(creator);

    // Modifier
    AccountUserDbRO modifier = new AccountUserDbRO();
    modifier.setObjectId("testModifier");
    modifier.setName("testModifier");
    vo.setModifier(modifier);
    vo.setDescription("testDescription");
    vo.setName("testName");
    vo.setObjectId("testContext");

    // ResponsibleAffiliations
    AffiliationDbRO responsible = new AffiliationDbRO();
    responsible.setObjectId("testResponsible");
    responsible.setName("testTitle");
    vo.getResponsibleAffiliations().add(responsible);
    vo.setState(ContextDbVO.State.CLOSED);

    return vo;
  }

  public ItemVersionVO test_item() {
    ItemVersionVO vo = new ItemVersionVO();



    // Context
    ContextDbRO context = new ContextDbRO();
    context.setObjectId("testContext");
    context.setName("testTitle");
    vo.getObject().setContext(context);

    vo.getObject().setCreationDate(DATE);

    // LatestRelease
    ItemVersionRO latestRelease = new ItemVersionRO();
    latestRelease.setObjectId("testLatestRelease");
    latestRelease.setModificationDate(DATE);
    AccountUserDbRO modifier = new AccountUserDbRO();
    modifier.setObjectId("testModifier");
    modifier.setName("testTitle");
    latestRelease.setModifier(modifier);
    //latestRelease.setPid("testPid");
    latestRelease.setVersionState(ItemVersionRO.State.PENDING);
    latestRelease.setVersionNumber(5);
    vo.getObject().setLatestRelease(latestRelease);

    // LatestVersion
    ItemVersionRO latestVersion = new ItemVersionRO();
    latestVersion.setObjectId("testLatestVersion");
    latestVersion.setModificationDate(DATE);
    latestVersion.setModifier(modifier);
    //latestVersion.setPid("testPid");
    latestVersion.setVersionState(ItemVersionRO.State.PENDING);
    latestVersion.setVersionNumber(5);
    vo.getObject().setLatestVersion(latestVersion);

    //vo.setLockStatus(LockStatus.LOCKED);

    // MetaData
    MdsPublicationVO mdsPublication = new MdsPublicationVO();
    mdsPublication.setDateAccepted("2018-04");
    mdsPublication.setDateCreated("2016");
    mdsPublication.setDateModified("2017");
    mdsPublication.setDatePublishedInPrint("2018-03-14");
    mdsPublication.setDatePublishedOnline("2018-08-15");
    mdsPublication.setDateSubmitted("2017-04");
    mdsPublication.setDegree(DegreeType.BACHELOR);
    EventVO event = new EventVO();
    event.setEndDate("2018-12-24");
    event.setInvitationStatus(InvitationStatus.INVITED);
    event.setPlace("testPlace");
    event.setStartDate("2018-12-23");
    event.setTitle("testTitle");
    mdsPublication.setEvent(event);
    mdsPublication.setFreeKeywords("testFreeKeywords");
    mdsPublication.setGenre(Genre.ARTICLE);
    LegalCaseVO legalCase = new LegalCaseVO();
    legalCase.setCourtName("testCourtName");
    legalCase.setDatePublished("2017");
    legalCase.setIdentifier("testIdentifier");
    legalCase.setTitle("testTitle");
    mdsPublication.setLegalCase(legalCase);
    mdsPublication.setLocation("testLocation");
    ProjectInfoVO projectInfo = new ProjectInfoVO();
    FundingInfoVO fundingInfo = new FundingInfoVO();
    FundingOrganizationVO fundingOrganization = new FundingOrganizationVO();
    fundingOrganization.setTitle("testTitle");
    IdentifierVO identifier = new IdentifierVO(IdType.ARXIV, "testIdentifier");
    fundingOrganization.getIdentifiers().add(identifier);
    fundingInfo.setFundingOrganization(fundingOrganization);
    FundingProgramVO fundingProgram = new FundingProgramVO();
    fundingProgram.setTitle("testTitle");
    identifier = new IdentifierVO(IdType.ARXIV, "testIdentifier");
    fundingProgram.getIdentifiers().add(identifier);
    fundingInfo.setFundingProgram(fundingProgram);
    projectInfo.setFundingInfo(fundingInfo);
    IdentifierVO grantIdentifier = new IdentifierVO(IdType.ARXIV, "testGrantIdentifier");
    projectInfo.setGrantIdentifier(grantIdentifier);
    projectInfo.setTitle("testTitle");
    mdsPublication.getProjectInfo().add(projectInfo);
    PublishingInfoVO publishingInfo = new PublishingInfoVO();
    publishingInfo.setEdition("testEdition");
    publishingInfo.setPlace("testPlace");
    publishingInfo.setPublisher("testPublisher");
    mdsPublication.setPublishingInfo(publishingInfo);
    mdsPublication.setReviewMethod(ReviewMethod.INTERNAL);
    mdsPublication.setTableOfContents("testTableOfContents");
    mdsPublication.setTitle("testTitle");
    mdsPublication.setTotalNumberOfPages("5");
    AbstractVO abstractVO = new AbstractVO();
    abstractVO.setLanguage("testLanguage");
    abstractVO.setValue("testValue");
    mdsPublication.getAbstracts().add(abstractVO);
    AlternativeTitleVO alternativeTitle = new AlternativeTitleVO();
    alternativeTitle.setLanguage("testLanguage");
    alternativeTitle.setType("testType");
    alternativeTitle.setValue("testValue");
    mdsPublication.getAlternativeTitles().add(alternativeTitle);
    // -PersonCreator
    PersonVO person = new PersonVO();
    //person.setCompleteName("testCompleteName");
    person.setFamilyName("testFamililyName");
    person.setGivenName("testGivenName");
    identifier = new IdentifierVO(IdType.ARXIV, "testIdentifier");
    person.setIdentifier(identifier);
    person.getAlternativeNames().add("testAlternativeName");
    OrganizationVO organization = new OrganizationVO();
    organization.setAddress("testAdress");
    organization.setIdentifier("testIdentifer");
    organization.setName("testName");
    person.getOrganizations().add(organization);
    person.getPseudonyms().add("testPseudonym");
    person.getTitles().add("testTitle");
    CreatorVO creator = new CreatorVO(person, CreatorRole.ACTOR);
    mdsPublication.getCreators().add(creator);
    // -OrganizationCreator
    creator = new CreatorVO(organization, CreatorRole.EDITOR);
    mdsPublication.getCreators().add(creator);
    identifier = new IdentifierVO(IdType.ARXIV, "testIdentifier");
    mdsPublication.getIdentifiers().add(identifier);
    mdsPublication.getLanguages().add("testLanguage");
    SourceVO source = new SourceVO();
    source.setDatePublishedInPrint(DATE);
    source.setEndPage("testEndPage");
    source.setGenre(SourceVO.Genre.BOOK);
    source.setIssue("testIssue");
    publishingInfo = new PublishingInfoVO();
    publishingInfo.setEdition("testEdition");
    publishingInfo.setPlace("testPlace");
    publishingInfo.setPublisher("testPublisher");
    source.setPublishingInfo(publishingInfo);
    source.setSequenceNumber("5");
    source.setStartPage("testStartPage");
    source.setTitle("testTitle");
    source.setTotalNumberOfPages("5");
    source.setVolume("testVolume");
    alternativeTitle = new AlternativeTitleVO();
    alternativeTitle.setLanguage("testLanguage");
    alternativeTitle.setType("testType");
    alternativeTitle.setValue("testValue");
    source.getAlternativeTitles().add(alternativeTitle);
    // -PersonCreator
    person = new PersonVO();
    //person.setCompleteName("testCompleteName");
    person.setFamilyName("testFamililyName");
    person.setGivenName("testGivenName");
    identifier = new IdentifierVO(IdType.ARXIV, "testIdentifier");
    person.setIdentifier(identifier);
    person.getAlternativeNames().add("testAlternativeName");
    organization = new OrganizationVO();
    organization.setAddress("testAdress");
    organization.setIdentifier("testIdentifer");
    organization.setName("testName");
    person.getOrganizations().add(organization);
    person.getPseudonyms().add("testPseudonym");
    person.getTitles().add("testTitle");
    creator = new CreatorVO(person, CreatorRole.ACTOR);
    source.getCreators().add(creator);
    // -OrganizationCreator
    creator = new CreatorVO(organization, CreatorRole.EDITOR);
    source.getCreators().add(creator);
    identifier = new IdentifierVO(IdType.ARXIV, "testIdentifier");
    source.getIdentifiers().add(identifier);
    SourceVO source2 = new SourceVO();
    source2.setDatePublishedInPrint(DATE);
    source2.setEndPage("testEndPage");
    source2.setGenre(SourceVO.Genre.BOOK);
    source2.setIssue("testIssue");
    publishingInfo = new PublishingInfoVO();
    publishingInfo.setEdition("testEdition");
    publishingInfo.setPlace("testPlace");
    publishingInfo.setPublisher("testPublisher");
    source2.setPublishingInfo(publishingInfo);
    source2.setSequenceNumber("5");
    source2.setStartPage("testStartPage");
    source2.setTitle("testTitle");
    source2.setTotalNumberOfPages("5");
    source2.setVolume("testVolume");
    alternativeTitle = new AlternativeTitleVO();
    alternativeTitle.setLanguage("testLanguage");
    alternativeTitle.setType("testType");
    alternativeTitle.setValue("testValue");
    source2.getAlternativeTitles().add(alternativeTitle);
    // -PersonCreator
    person = new PersonVO();
    //person.setCompleteName("testCompleteName");
    person.setFamilyName("testFamililyName");
    person.setGivenName("testGivenName");
    identifier = new IdentifierVO(IdType.ARXIV, "testIdentifier");
    person.setIdentifier(identifier);
    person.getAlternativeNames().add("testAlternativeName");
    organization = new OrganizationVO();
    organization.setAddress("testAdress");
    organization.setIdentifier("testIdentifer");
    organization.setName("testName");
    person.getOrganizations().add(organization);
    person.getPseudonyms().add("testPseudonym");
    person.getTitles().add("testTitle");
    creator = new CreatorVO(person, CreatorRole.ACTOR);
    source2.getCreators().add(creator);
    // -OrganizationCreator
    creator = new CreatorVO(organization, CreatorRole.EDITOR);
    source2.getCreators().add(creator);
    identifier = new IdentifierVO(IdType.ARXIV, "testIdentifier");
    source2.getIdentifiers().add(identifier);
    //source.getSources().add(source2);
    mdsPublication.getSources().add(source);
    mdsPublication.getSources().add(source2);
    SubjectVO subject = new SubjectVO();
    subject.setLanguage("testLanguage");
    subject.setType("testType");
    subject.setValue("testValue");
    mdsPublication.getSubjects().add(subject);
    vo.setMetadata(mdsPublication);

    // Owner
    AccountUserDbRO owner = new AccountUserDbRO();
    owner.setObjectId("testCreator");
    owner.setName("testTitle");
    vo.getObject().setCreator(owner);

    vo.getObject().setObjectPid("testPid");
    vo.getObject().setPublicState(ItemVersionVO.State.RELEASED);

    // Version

    vo.setMessage("testLastMessage");
    vo.setModificationDate(DATE);

    vo.setModifier(modifier);
    vo.setVersionPid("testPid");
    vo.setVersionState(ItemVersionRO.State.RELEASED);
    vo.setVersionNumber(5);

    // Files
    FileDbVO file = new FileDbVO();
    file.setChecksum("testChecksum");
    file.setChecksumAlgorithm(FileDbVO.ChecksumAlgorithm.MD5);
    file.setContent("testContent");
    file.setSize(5);
    // Owner

    file.setCreator(owner);

    file.setCreationDate(DATE);
    MdsFileVO mdsFile = new MdsFileVO();
    mdsFile.setContentCategory("testContentCategory");
    mdsFile.setCopyrightDate("testCopyrightDate");
    mdsFile.setDescription("testDescription");
    mdsFile.setEmbargoUntil("testEmbargoUntil");
    mdsFile.setLicense("testLicense");
    mdsFile.setRights("testRights");
    mdsFile.setSize(5);
    mdsFile.setTitle("testTitle");
    FormatVO format = new FormatVO();
    format.setType("testType");
    format.setValue("testValue");
    mdsFile.getFormats().add(format);
    identifier = new IdentifierVO(IdType.DOI, "testIdentifier");
    mdsFile.getIdentifiers().add(identifier);
    file.setMetadata(mdsFile);
    file.setLastModificationDate(DATE);
    file.setMimeType("testMimeType");
    file.setName("testName");
    file.setPid("testPid");
    file.setObjectId("testReference");


    file.setStorage(FileDbVO.Storage.EXTERNAL_URL);
    file.setVisibility(FileDbVO.Visibility.PRIVATE);
    vo.getFiles().add(file);

    vo.getObject().getLocalTags().add("testTag");

    return vo;
  }

  public PubItemVO create_item() throws JsonParseException, JsonMappingException, IOException {
    String src =
        "{\"files\":[{\"reference\":{\"objectId\":\"item_1383643\"},\"name\":\"1Dreview.pdf\",\"visibility\":\"PUBLIC\",\"description\":null,\"createdByRO\":{\"objectId\":null,\"title\":null},\"creationDate\":\"2012-02-29T14:05:34.343+0000\",\"lastModificationDate\":null,\"pid\":null,\"content\":\"/rest/items/item_1351618/components/item_1383643/content\",\"storage\":\"INTERNAL_MANAGED\",\"contentCategory\":\"any-fulltext\",\"checksum\":\"a3d04c9e8b348890c8c8b80b2520708e\",\"checksumAlgorithm\":\"MD5\",\"mimeType\":\"application/pdf\",\"defaultMetadata\":{\"title\":\"1Dreview.pdf\",\"description\":\"\",\"identifiers\":[],\"formats\":[{\"value\":\"application/pdf\",\"type\":\"dcterms:IMT\"}],\"size\":1487794,\"copyrightDate\":\"2011\",\"rights\":\"Elsevier\",\"license\":\"\"},\"contentCategoryString\":\"any-fulltext\",\"visibilityString\":\"PUBLIC\",\"storageString\":\"INTERNAL_MANAGED\"}],\"localTags\":[\"\"],\"owner\":{\"objectId\":\"user_653557\",\"title\":\"THDepositor\"},\"pid\":\"hdl:11858/00-001M-0000-000F-3FDA-3\",\"version\":{\"objectId\":\"item_1351618\",\"title\":\"ThisVersion\",\"versionNumber\":4,\"modificationDate\":\"2012-02-29T14:16:10.491+0000\",\"lastMessage\":\"\",\"state\":\"RELEASED\",\"modifiedByRO\":{\"objectId\":\"user_653571\",\"title\":\"UtaSiebeky\"},\"pid\":\"hdl:11858/00-001M-0000-000F-3FDD-E\",\"versionNumberForXml\":4,\"modificationDateForXml\":\"2012-02-29T14:16:10.491+0000\",\"stateForXml\":\"RELEASED\",\"modifiedByForXml\":{\"objectId\":\"user_653571\",\"title\":\"UtaSiebeky\"},\"lastMessageForXml\":\"\"},\"latestVersion\":{\"objectId\":\"user_1351618\",\"title\":\"LatestVersion\",\"versionNumber\":4,\"modificationDate\":\"2012-02-29T14:16:10.491+0000\",\"versionNumberForXml\":4,\"modificationDateForXml\":\"2012-02-29T14:16:10.491+0000\",\"stateForXml\":\"PENDING\",\"modifiedByForXml\":{\"objectId\":null,\"title\":null},\"lastMessageForXml\":\"\"},\"latestRelease\":{\"objectId\":\"item_1351618\",\"title\":\"Latestpublicversion\",\"versionNumber\":4,\"modificationDate\":\"2012-02-29T14:16:10.491+0000\",\"pid\":\"hdl:11858/00-001M-0000-000F-3FDD-E\",\"versionNumberForXml\":4,\"modificationDateForXml\":\"2012-02-29T14:16:10.491+0000\",\"stateForXml\":\"PENDING\",\"modifiedByForXml\":{\"objectId\":null,\"title\":null},\"lastMessageForXml\":\"\"},\"relations\":[],\"creationDate\":\"2012-02-10T16:02:52.910+0000\",\"lockStatus\":\"UNLOCKED\",\"publicStatus\":\"RELEASED\",\"publicStatusComment\":\"\",\"metadata\":{\"title\":\"Time-dependentdensity-functionalandreduceddensity-matrixmethodsforfewelectrons:Exactversusadiabaticapproximations\",\"alternativeTitles\":[],\"creators\":[{\"person\":{\"givenName\":\"N.\",\"familyName\":\"Helbig\",\"alternativeNames\":[],\"titles\":[],\"pseudonyms\":[],\"organizations\":[{\"address\":\"Av.Tolosa72,E-20018SanSebastián,Spain\",\"identifier\":\"ou_persistent22\",\"name\":\"Nano-BioSpectroscopygroup,Dpto.FísicadeMateriales,UniversidaddelPaísVasco,CentrodeFísicadeMaterialesCSIC-UPV/EHU-MPCandDIPC,\"},{\"address\":\"\",\"identifier\":\"ou_persistent22\",\"name\":\"EuropeanTheoreticalSpectroscopyFacility\"}],\"organizationsSize\":2},\"role\":\"AUTHOR\",\"type\":\"PERSON\",\"roleString\":\"AUTHOR\",\"typeString\":\"PERSON\"},{\"person\":{\"givenName\":\"J.I\",\"familyName\":\"Fuks\",\"alternativeNames\":[],\"titles\":[],\"pseudonyms\":[],\"organizations\":[{\"address\":\"Av.Tolosa72,E-20018SanSebastián,Spain\",\"identifier\":\"ou_persistent22\",\"name\":\"Nano-BioSpectroscopygroup,Dpto.FísicadeMateriales,UniversidaddelPaísVasco,CentrodeFísicadeMaterialesCSIC-UPV/EHU-MPCandDIPC,\"},{\"address\":\"\",\"identifier\":\"ou_persistent22\",\"name\":\"EuropeanTheoreticalSpectroscopyFacility\"}],\"organizationsSize\":2},\"role\":\"AUTHOR\",\"type\":\"PERSON\",\"roleString\":\"AUTHOR\",\"typeString\":\"PERSON\"},{\"person\":{\"givenName\":\"I.V.\",\"familyName\":\"Tokatly\",\"alternativeNames\":[],\"titles\":[],\"pseudonyms\":[],\"organizations\":[{\"address\":\"Av.Tolosa72,E-20018SanSebastián,Spain\",\"identifier\":\"ou_persistent22\",\"name\":\"Nano-BioSpectroscopygroup,Dpto.FísicadeMateriales,UniversidaddelPaísVasco,CentrodeFísicadeMaterialesCSIC-UPV/EHU-MPCandDIPC,\"},{\"address\":\"\",\"identifier\":\"ou_persistent22\",\"name\":\"EuropeanTheoreticalSpectroscopyFacility\"},{\"address\":\"E-48011Bilbao,Spain\",\"identifier\":\"ou_persistent22\",\"name\":\"IKERBASQUE,BasqueFoundationforScience,\"}],\"organizationsSize\":3},\"role\":\"AUTHOR\",\"type\":\"PERSON\",\"roleString\":\"AUTHOR\",\"typeString\":\"PERSON\"},{\"person\":{\"givenName\":\"Heiko\",\"familyName\":\"Appel\",\"alternativeNames\":[],\"titles\":[],\"pseudonyms\":[],\"organizations\":[{\"address\":\"\",\"identifier\":\"ou_persistent22\",\"name\":\"EuropeanTheoreticalSpectroscopyFacility\"},{\"address\":\"Faradayweg4-6,D-14195Berlin,Germany\",\"identifier\":\"ou_634547\",\"name\":\"Theory,FritzHaberInstitute,MaxPlanckSociety\"}],\"identifier\":{\"id\":\"http://pubman.mpdl.mpg.de/cone/persons/resource/persons21304\",\"type\":\"CONE\",\"typeString\":\"CONE\"},\"organizationsSize\":2},\"role\":\"AUTHOR\",\"type\":\"PERSON\",\"roleString\":\"AUTHOR\",\"typeString\":\"PERSON\"},{\"person\":{\"givenName\":\"E.K.U\",\"familyName\":\"Gross\",\"alternativeNames\":[],\"titles\":[],\"pseudonyms\":[],\"organizations\":[{\"address\":\"\",\"identifier\":\"ou_persistent22\",\"name\":\"EuropeanTheoreticalSpectroscopyFacility\"},{\"address\":\"Weinberg2,D-06120Halle,Germany\",\"identifier\":\"ou_persistent22\",\"name\":\"Max-Planck-InstitutfürMikrostrukturphysik,\"}],\"organizationsSize\":2},\"role\":\"AUTHOR\",\"type\":\"PERSON\",\"roleString\":\"AUTHOR\",\"typeString\":\"PERSON\"},{\"person\":{\"givenName\":\"Angel\",\"familyName\":\"Rubio\",\"alternativeNames\":[],\"titles\":[],\"pseudonyms\":[],\"organizations\":[{\"address\":\"Av.Tolosa72,E-20018SanSebastián,Spain\",\"identifier\":\"ou_persistent22\",\"name\":\"Nano-BioSpectroscopygroup,Dpto.FísicadeMateriales,UniversidaddelPaísVasco,CentrodeFísicadeMaterialesCSIC-UPV/EHU-MPCandDIPC,\"},{\"address\":\"\",\"identifier\":\"ou_persistent22\",\"name\":\"EuropeanTheoreticalSpectroscopyFacility\"},{\"address\":\"Faradayweg4-6,D-14195Berlin,Germany\",\"identifier\":\"ou_634547\",\"name\":\"Theory,FritzHaberInstitute,MaxPlanckSociety\"}],\"identifier\":{\"id\":\"http://pubman.mpdl.mpg.de/cone/persons/resource/persons22028\",\"type\":\"CONE\",\"typeString\":\"CONE\"},\"organizationsSize\":3},\"role\":\"AUTHOR\",\"type\":\"PERSON\",\"roleString\":\"AUTHOR\",\"typeString\":\"PERSON\"}],\"datePublishedInPrint\":\"2011-06-28\",\"genre\":\"ARTICLE\",\"identifiers\":[{\"id\":\"10.1016/j.chemphys.2011.06.010\",\"type\":\"DOI\",\"typeString\":\"DOI\"}],\"languages\":[\"eng\"],\"reviewMethod\":\"PEER\",\"sources\":[{\"title\":\"ChemicalPhysics\",\"alternativeTitles\":[{\"value\":\"Chem.Phys.\",\"type\":\"OTHER\"}],\"creators\":[],\"volume\":\"391\",\"issue\":\"1\",\"startPage\":\"1\",\"endPage\":\"10\",\"sequenceNumber\":\"\",\"publishingInfo\":{\"place\":\"Amsterdam\",\"publisher\":\"North-Holland\"},\"identifiers\":[{\"id\":\"0301-0104\",\"type\":\"ISSN\",\"typeString\":\"ISSN\"},{\"id\":\"http://pubman.mpdl.mpg.de/cone/journals/resource/954925509371\",\"type\":\"CONE\",\"typeString\":\"CONE\"}],\"sources\":[],\"genre\":\"JOURNAL\",\"totalNumberOfPages\":\"\"}],\"freeKeywords\":\"\",\"subjects\":[{\"value\":\"\",\"type\":\"DDC\"}],\"tableOfContents\":\"\",\"totalNumberOfPages\":\"\",\"abstracts\":[]},\"context\":{\"objectId\":\"ctx_23049\",\"title\":\"PublicationsoftheFritzHaberInstitute\"},\"modificationDate\":\"2012-02-29T14:16:10.491+0000\"}";
    PubItemVO item = mapper.readValue(src, PubItemVO.class);
    return item;
  }
}
