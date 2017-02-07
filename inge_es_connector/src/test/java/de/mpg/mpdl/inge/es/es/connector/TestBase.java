package de.mpg.mpdl.inge.es.es.connector;

import java.util.Date;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.types.Coordinates;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO.State;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.ValidationPointVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.SubjectClassification;
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
    Coordinates coordinates = new Coordinates(0d, 0d, 0d);
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
    AccountUserRO modified = new AccountUserRO("testAccountUser");
    modified.setTitle("testTitle");
    templateItem.setModifiedByRO(modified);
    templateItem.setPid("testPid");
    templateItem.setState(ItemVO.State.PENDING);
    templateItem.setTitle("testTitle");
    templateItem.setVersionNumber(0);
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

    return vo;
  }
}
