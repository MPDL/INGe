package de.mpg.mpdl.inge.es.es.connector;

import java.util.Date;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.FileRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.types.Coordinates;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO.State;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.ChecksumAlgorithm;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Storage;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.ItemRelationVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.LockStatus;
import de.mpg.mpdl.inge.model.valueobjects.ValidationPointVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.SubjectClassification;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsYearbookVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO.Workflow;

public class TestBase {
  private static final Date DATE = new Date();

  public AffiliationVO test_ou() {
    AffiliationVO vo = new AffiliationVO();

    // ChildAffiliations
    AffiliationRO child = new AffiliationRO("testChild");
    child.setForm("testForm");
    child.setTitle("testTitle");
    vo.getChildAffiliations().add(child);

    vo.setCreationDate(DATE);

    // Creator
    AccountUserRO creator = new AccountUserRO("testCreator");
    creator.setTitle("testTitle");
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
    md.setEndDate("testDate");
    md.setName("testName");
    md.setStartDate("testDate");
    md.setTitle("testTitle");
    md.setType("testType");
    vo.setDefaultMetadata(md);

    vo.setLastModificationDate(DATE);

    // Modifier
    AccountUserRO modifier = new AccountUserRO("testModifier");
    modifier.setTitle("testModifier");
    vo.setModifiedBy(modifier);

    // ParentAffiliations
    AffiliationRO parent = new AffiliationRO("testParent");
    parent.setForm("testForm");
    parent.setTitle("testTitle");
    vo.getParentAffiliations().add(parent);

    // PredecessorAffiliations
    AffiliationRO predecessor = new AffiliationRO("testPredecessor");
    predecessor.setForm("testForm");
    predecessor.setTitle("testTitle");
    vo.getPredecessorAffiliations().add(predecessor);

    vo.setPublicStatus("testPublicStatus");

    // Reference
    AffiliationRO reference = new AffiliationRO("testReference");
    reference.setForm("testForm");
    reference.setTitle("testTitle");
    vo.setReference(reference);

    return vo;
  }

  public ContextVO test_context() {
    ContextVO vo = new ContextVO();

    // AdminDescriptor
    PublicationAdminDescriptorVO admin = new PublicationAdminDescriptorVO();
    admin.getAllowedGenres().add(Genre.ARTICLE);
    admin.getAllowedSubjectClassifications().add(SubjectClassification.DDC);
    admin.setContactEmail("testContactEmail");
    ItemRO templateItem = new ItemRO("testId");
    templateItem.setHref("testHref");
    templateItem.setLastMessage("testLastMessage");
    templateItem.setModificationDate(DATE);
    AccountUserRO modifier = new AccountUserRO("testModifier");
    modifier.setTitle("testTitle");
    templateItem.setModifiedByRO(modifier);
    templateItem.setPid("testPid");
    templateItem.setState(ItemVO.State.PENDING);
    templateItem.setTitle("testTitle");
    templateItem.setVersionNumber(5);
    admin.setTemplateItem(templateItem);
    admin.setValidationSchema("testValidationSchema");
    admin.setVisibilityOfReferences("testVisibility");
    admin.setWorkflow(Workflow.STANDARD);
    vo.setAdminDescriptor(admin);

    // Creator
    AccountUserRO creator = new AccountUserRO("testCreator");
    creator.setTitle("testTitle");
    vo.setCreator(creator);

    // DefaultMetaData
    MdsPublicationVO metadata = new MdsPublicationVO();
    metadata.setTitle("testTitle");
    vo.setDefaultMetadata(metadata);

    vo.setDescription("testDescription");
    vo.setName("testName");

    // Reference
    ContextRO reference = new ContextRO("testContext");
    reference.setTitle("testTitle");
    vo.setReference(reference);

    // ResponsibleAffiliations
    AffiliationRO responsible = new AffiliationRO("testResponsible");
    responsible.setForm("testForm");
    responsible.setTitle("testTitle");
    vo.getResponsibleAffiliations().add(responsible);

    vo.setState(State.CLOSED);
    vo.setType("testType");

    // ValidationPoints
    ValidationPointVO validationPoint = new ValidationPointVO();
    validationPoint.setName("testName");
    vo.getValidationPoints().add(validationPoint);

    return vo;
  }

  public PubItemVO test_item() {
    PubItemVO vo = new PubItemVO();

    vo.setBaseUrl("testBaseUrl");
    vo.setContentModel("testContenModel");
    vo.setContentModelHref("testContenModelHRef");
    
    // Context
    ContextRO context = new ContextRO("testContext");
    context.setTitle("testTitle");
    vo.setContext(context);
    
    vo.setCreationDate(DATE);
    
    // LatestRelease
    ItemRO latestRelease = new ItemRO("testLatestRelease");
    latestRelease.setHref("testHref");
    latestRelease.setLastMessage("testLastMessage");
    latestRelease.setModificationDate(DATE);
    AccountUserRO modifier = new AccountUserRO("testModifier");
    modifier.setTitle("testTitle");
    latestRelease.setModifiedByRO(modifier);
    latestRelease.setPid("testPid");
    latestRelease.setState(ItemVO.State.PENDING);
    latestRelease.setTitle("testTitle");
    latestRelease.setVersionNumber(5);
    vo.setLatestRelease(latestRelease);

    // LatestVersion
    ItemRO latestVersion = new ItemRO("testLatestVersion");
    latestVersion.setHref("testHref");
    latestVersion.setLastMessage("testLastMessage");
    latestVersion.setModificationDate(DATE);
    modifier = new AccountUserRO("testModifier");
    modifier.setTitle("testTitle");
    latestVersion.setModifiedByRO(modifier);
    latestVersion.setPid("testPid");
    latestVersion.setState(ItemVO.State.PENDING);
    latestVersion.setTitle("testTitle");
    latestVersion.setVersionNumber(5);
    vo.setLatestVersion(latestVersion);
    
    vo.setLockStatus(LockStatus.LOCKED);
    
    // MetaData
    // vo.setMetadata(mdsPublicationVO);
    
    // Owner
    AccountUserRO owner = new AccountUserRO("testOwner");
    owner.setTitle("testTitle");
    vo.setOwner(owner);
    
    vo.setPid("testPid");
    vo.setPublicStatus(ItemVO.State.RELEASED);
    vo.setPublicStatusComment("testPublicStatusComment");
    
    // Version
    ItemRO version = new ItemRO("testVersion");
    version.setHref("testHref");
    version.setLastMessage("testLastMessage");
    version.setModificationDate(DATE);
    modifier = new AccountUserRO("testModifier");
    modifier.setTitle("testTitle");
    version.setModifiedByRO(modifier);
    version.setPid("testPid");
    version.setState(ItemVO.State.RELEASED);
    version.setTitle("testTitle");
    version.setVersionNumber(5);
    vo.setVersion(version);
    
    // Yearbook
    MdsYearbookVO yearbook = new MdsYearbookVO();
    yearbook.setEndDate("2017");
    yearbook.setStartDate("2017");
    yearbook.setTitle("testTitle");
    yearbook.setYear("testYear");
    // -PersonCreator
    PersonVO person = new PersonVO();
    person.setCompleteName("testCompleteName");
    person.setFamilyName("testFamililyName");
    person.setGivenName("testGivenName");
    IdentifierVO identifier = new IdentifierVO(IdType.ARXIV, "testIdentifier");
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
    yearbook.getCreators().add(creator);
    // -OrganizationCreator
    creator = new CreatorVO(organization, CreatorRole.EDITOR);
    yearbook.getCreators().add(creator);
    yearbook.getIncludedContexts().add("testContext");
    vo.setYearbookMetadata(yearbook);
    
    // Files
    FileVO file = new FileVO();
    file.setChecksum("testChecksum");
    file.setChecksumAlgorithm(ChecksumAlgorithm.MD5);
    file.setContent("testContent");
    file.setContentCategory("testContentCategory");
    file.setContentCategoryString("testContentCategoryString");
    AccountUserRO creator2 = new AccountUserRO("testCreator");
    creator2.setTitle("testTitle");
    file.setCreatedByRO(creator2);
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
    file.setDefaultMetadata(mdsFile);
    file.setDescription("testDescription");
    file.setLastModificationDate(DATE);
    file.setMimeType("testMimeType");
    file.setName("testName");
    file.setPid("testPid");
    FileRO reference = new FileRO("testReference");
    reference.setTitle("testTitle");
    file.setReference(reference);
    file.setStorage(Storage.EXTERNAL_MANAGED);
    file.setVisibility(Visibility.PRIVATE);
    vo.getFiles().add(file);
    
    vo.getLocalTags().add("testTag");

    // Relations
    ItemRO targetItemRef = new ItemRO("testTargetItemRef");
    targetItemRef.setHref("testHref");
    targetItemRef.setLastMessage("testLastMessage");
    targetItemRef.setModificationDate(DATE);
    modifier = new AccountUserRO("testModifier");
    modifier.setTitle("testTitle");
    targetItemRef.setModifiedByRO(modifier);
    targetItemRef.setPid("testPid");
    targetItemRef.setState(ItemVO.State.PENDING);
    targetItemRef.setTitle("testTitle");
    targetItemRef.setVersionNumber(5);
    ItemRelationVO relation = new ItemRelationVO("testType", targetItemRef);
    relation.setDescription("testDescription");
    targetItemRef = new ItemRO("testItem");
    targetItemRef.setHref("testHref");
    targetItemRef.setLastMessage("testLastMessage");
    targetItemRef.setModificationDate(DATE);
    modifier = new AccountUserRO("testModifier");
    modifier.setTitle("testTitle");
    targetItemRef.setModifiedByRO(modifier);
    targetItemRef.setPid("testPid");
    targetItemRef.setState(ItemVO.State.PENDING);
    targetItemRef.setTitle("testTitle");
    targetItemRef.setVersionNumber(5);
    relation.setTargetItemRef(targetItemRef);
    relation.setType("testType");
    vo.getRelations().add(relation);

    return vo;
  }
}
