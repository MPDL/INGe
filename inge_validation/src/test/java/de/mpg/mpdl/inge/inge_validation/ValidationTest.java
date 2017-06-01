package de.mpg.mpdl.inge.inge_validation;

import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;

import de.mpg.mpdl.inge.cone_cache.ConeCache;
import de.mpg.mpdl.inge.inge_validation.validator.ComponentContentRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.ComponentDataRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.CreatorRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.DateRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.EventTitleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.GenreRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.IdTypeRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.MdsPublicationDateFormatValidator;
import de.mpg.mpdl.inge.inge_validation.validator.NoSlashesInFileNameValidator;
import de.mpg.mpdl.inge.inge_validation.validator.OrganizationNameRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.PublicationCreatorsRoleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.SourceCreatorsRoleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.SourceGenresRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.SourceRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.SourceTitlesRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.TitleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.UriAsLocatorValidator;
import de.mpg.mpdl.inge.inge_validation.validator.cone.ClassifiedKeywordsValidator;
import de.mpg.mpdl.inge.inge_validation.validator.cone.ComponentMimeTypesValidator;
import de.mpg.mpdl.inge.inge_validation.validator.cone.LanguageCodeValidator;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Storage;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO.InvitationStatus;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ValidationTest {
  private static final Logger logger = Logger.getLogger(ValidationTest.class);

  private PubItemVO pubItemVO;
  private MdsPublicationVO mdsPublicationVO;
  private final ConeCache coneCache = ConeCache.getInstance(); // zur erstmaligen Befüllung des
                                                               // Caches

  @Before
  public void setUp() throws Exception {
    this.pubItemVO = new PubItemVO();
    this.mdsPublicationVO = new MdsPublicationVO();
    this.pubItemVO.setMetadata(this.mdsPublicationVO);
  }

  @After
  public void tearDown() throws Exception {}

  @Ignore
  @Test
  public void testClassifiedKeywords1() throws Exception {
    logger.info("--------------------- STARTING testClassifiedKeywords1 ---------------------");

    final SubjectVO s1 = new SubjectVO();
    s1.setType(ClassifiedKeywordsValidator.DDC);
    s1.setValue("blubb");
    this.mdsPublicationVO.getSubjects().add(s1);

    final SubjectVO s2 = new SubjectVO();
    s2.setType(ClassifiedKeywordsValidator.ISO639_3);
    s2.setValue("blubb");
    this.mdsPublicationVO.getSubjects().add(s2);

    final SubjectVO s3 = new SubjectVO();
    s3.setType(ClassifiedKeywordsValidator.MPIPKS);
    s3.setValue("blubb");
    this.mdsPublicationVO.getSubjects().add(s3);

    final SubjectVO s4 = new SubjectVO();
    s4.setType(ClassifiedKeywordsValidator.MPIRG);
    s4.setValue("blubb");
    this.mdsPublicationVO.getSubjects().add(s4);

    final SubjectVO s5 = new SubjectVO();
    s5.setType(ClassifiedKeywordsValidator.MPIS_GROUPS);
    s5.setValue("blubb");
    this.mdsPublicationVO.getSubjects().add(s5);

    final SubjectVO s6 = new SubjectVO();
    s6.setType(ClassifiedKeywordsValidator.MPIS_PROJECTS);
    s6.setValue("blubb");
    this.mdsPublicationVO.getSubjects().add(s6);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getSubjects(),
            new ClassifiedKeywordsValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testClassifiedKeywords1 ---------------------");
  }

  @Ignore
  @Test
  public void testClassifiedKeywords2() throws Exception {
    logger.info("--------------------- STARTING testClassifiedKeywords2 ---------------------");

    final SubjectVO s1 = new SubjectVO();
    s1.setType(ClassifiedKeywordsValidator.DDC);
    s1.setValue("Computer science, information & general works");
    this.mdsPublicationVO.getSubjects().add(s1);

    final SubjectVO s2 = new SubjectVO();
    s2.setType(ClassifiedKeywordsValidator.ISO639_3);
    s2.setValue("Afar");
    this.mdsPublicationVO.getSubjects().add(s2);

    final SubjectVO s3 = new SubjectVO();
    s3.setType(ClassifiedKeywordsValidator.MPIPKS);
    s3.setValue("Light-matter interaction");
    this.mdsPublicationVO.getSubjects().add(s3);

    final SubjectVO s4 = new SubjectVO();
    s4.setType(ClassifiedKeywordsValidator.MPIRG);
    s4.setValue("Exploring the sources");
    this.mdsPublicationVO.getSubjects().add(s4);

    final SubjectVO s5 = new SubjectVO();
    s5.setType(ClassifiedKeywordsValidator.MPIS_GROUPS);
    s5.setValue("Sun and Heliosphere");
    this.mdsPublicationVO.getSubjects().add(s5);

    final SubjectVO s6 = new SubjectVO();
    s6.setType(ClassifiedKeywordsValidator.MPIS_PROJECTS);
    s6.setValue("AMPTE");
    this.mdsPublicationVO.getSubjects().add(s6);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getSubjects(),
            new ClassifiedKeywordsValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testClassifiedKeywords2 ---------------------");
  }

  @Ignore
  @Test
  public void testComponentContentRequired1() throws Exception {
    logger
        .info("--------------------- STARTING testComponentContentRequired1 ---------------------");

    final FileVO f1 = new FileVO();
    final MdsFileVO m1 = new MdsFileVO();
    m1.setTitle("blubb");
    f1.setDefaultMetadata(m1);
    this.pubItemVO.getFiles().add(f1);

    final FileVO f2 = new FileVO();
    f2.setMimeType("blubb");
    this.pubItemVO.getFiles().add(f2);

    final FileVO f3 = new FileVO();
    f3.setDescription("blubb");
    this.pubItemVO.getFiles().add(f3);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getFiles(),
            new ComponentContentRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger
        .info("--------------------- FINISHED testComponentContentRequired1 ---------------------");
  }

  @Ignore
  @Test
  public void testComponentContentRequired2() throws Exception {
    logger
        .info("--------------------- STARTING testComponentContentRequired2 ---------------------");

    final FileVO f1 = new FileVO();
    final MdsFileVO m1 = new MdsFileVO();
    m1.setTitle("blubb");
    f1.setDefaultMetadata(m1);
    f1.setContent("blubb");
    this.pubItemVO.getFiles().add(f1);

    final FileVO f2 = new FileVO();
    f2.setMimeType("blubb");
    f2.setContent("blubb");
    this.pubItemVO.getFiles().add(f2);

    final FileVO f3 = new FileVO();
    f3.setDescription("blubb");
    f3.setContent("blubb");
    this.pubItemVO.getFiles().add(f3);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getFiles(),
            new ComponentContentRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger
        .info("--------------------- FINISHED testComponentContentRequired2 ---------------------");
  }

  @Ignore
  @Test
  public void testComponentDataRequired1() throws Exception {
    logger.info("--------------------- STARTING testComponentDataRequired1 ---------------------");

    final FileVO f1 = new FileVO();
    final MdsFileVO m1 = new MdsFileVO();
    m1.setTitle("blubb");
    f1.setDefaultMetadata(m1);
    this.pubItemVO.getFiles().add(f1);

    final FileVO f2 = new FileVO();
    f2.setContentCategory("blubb");
    this.pubItemVO.getFiles().add(f2);

    final FileVO f3 = new FileVO();
    f3.setMimeType("blubb");
    this.pubItemVO.getFiles().add(f3);

    final FileVO f4 = new FileVO();
    f4.setVisibility(FileVO.Visibility.PUBLIC);
    this.pubItemVO.getFiles().add(f4);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getFiles(),
            new ComponentDataRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testComponentDataRequired1 ---------------------");
  }

  @Ignore
  @Test
  public void testComponentDataRequired2() throws Exception {
    logger.info("--------------------- STARTING testComponentDataRequired2 ---------------------");

    final FileVO f1 = new FileVO();
    final MdsFileVO m1 = new MdsFileVO();
    m1.setTitle("blubb");
    f1.setDefaultMetadata(m1);
    f1.setContent("blubb");
    this.pubItemVO.getFiles().add(f1);

    final FileVO f2 = new FileVO();
    f2.setContentCategory("blubb");
    f2.setContent("blubb");
    this.pubItemVO.getFiles().add(f2);

    final FileVO f3 = new FileVO();
    f3.setMimeType("blubb");
    f3.setContent("blubb");
    this.pubItemVO.getFiles().add(f3);

    final FileVO f4 = new FileVO();
    f4.setVisibility(FileVO.Visibility.PUBLIC);
    f4.setContent("blubb");
    this.pubItemVO.getFiles().add(f4);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getFiles(),
            new ComponentDataRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testComponentDataRequired2 ---------------------");
  }

  @Ignore
  @Test
  public void testComponentMimeTypes1() throws Exception {
    logger.info("--------------------- STARTING testComponentMimeTypes1 ---------------------");

    final FileVO f1 = new FileVO();
    final MdsFileVO m1 = new MdsFileVO();
    m1.setTitle("blubb");
    final FormatVO fo1 = new FormatVO();
    fo1.setType(ComponentMimeTypesValidator.IMT);
    fo1.setValue("blubb");
    m1.getFormats().add(0, fo1);
    final FormatVO fo2 = new FormatVO();
    fo2.setType("blubb");
    fo2.setValue("blubb");
    m1.getFormats().add(0, fo2);
    f1.setDefaultMetadata(m1);
    f1.setContent("blubb");
    f1.setStorage(Storage.EXTERNAL_URL);
    this.pubItemVO.getFiles().add(f1);

    final FileVO f2 = new FileVO();
    final MdsFileVO m2 = new MdsFileVO();
    m2.setTitle("blubb");
    final FormatVO fo3 = new FormatVO();
    fo3.setType(ComponentMimeTypesValidator.IMT);
    fo3.setValue("blubb");
    m2.getFormats().add(0, fo3);
    final FormatVO fo4 = new FormatVO();
    fo4.setType("blubb");
    fo4.setValue("blubb");
    m2.getFormats().add(0, fo4);
    f2.setDefaultMetadata(m2);
    f2.setContent("blubb");
    f2.setStorage(Storage.INTERNAL_MANAGED);
    this.pubItemVO.getFiles().add(f2);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getFiles(), new ComponentMimeTypesValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testComponentMimeTypes1 ---------------------");
  }

  @Ignore
  @Test
  public void testComponentMimeTypes2() throws Exception {
    logger.info("--------------------- STARTING testComponentMimeTypes2 ---------------------");

    final FileVO f1 = new FileVO();
    final MdsFileVO m1 = new MdsFileVO();
    m1.setTitle("blubb");
    final FormatVO fo1 = new FormatVO();
    fo1.setType(ComponentMimeTypesValidator.IMT);
    fo1.setValue("application/andrew-inset");
    m1.getFormats().add(0, fo1);
    final FormatVO fo2 = new FormatVO();
    fo2.setType("blubb");
    fo2.setValue("application/andrew-inset");
    m1.getFormats().add(0, fo2);
    f1.setDefaultMetadata(m1);
    f1.setContent("blubb");
    f1.setStorage(Storage.EXTERNAL_URL);
    this.pubItemVO.getFiles().add(f1);

    final FileVO f2 = new FileVO();
    final MdsFileVO m2 = new MdsFileVO();
    m2.setTitle("blubb");
    final FormatVO fo3 = new FormatVO();
    fo3.setType(ComponentMimeTypesValidator.IMT);
    fo3.setValue("application/andrew-inset");
    m2.getFormats().add(0, fo3);
    final FormatVO fo4 = new FormatVO();
    fo4.setType("blubb");
    fo4.setValue("application/andrew-inset");
    m2.getFormats().add(0, fo4);
    f2.setDefaultMetadata(m2);
    f2.setContent("blubb");
    f2.setStorage(Storage.INTERNAL_MANAGED);
    this.pubItemVO.getFiles().add(f2);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getFiles(), new ComponentMimeTypesValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testComponentMimeTypes2 ---------------------");
  }

  @Ignore
  @Test
  public void testCreatorRequired1() throws Exception {
    logger.info("--------------------- STARTING testCreatorRequired1 ---------------------");

    final CreatorVO c1 = new CreatorVO();
    c1.setType(CreatorType.ORGANIZATION);
    this.pubItemVO.getMetadata().getCreators().add(c1);

    final CreatorVO c2 = new CreatorVO();
    c2.setType(CreatorType.PERSON);
    this.pubItemVO.getMetadata().getCreators().add(c2);

    final CreatorVO c3 = new CreatorVO();
    c3.setType(CreatorType.PERSON);
    final PersonVO p = new PersonVO();
    p.setFamilyName("blubb");
    final OrganizationVO o = new OrganizationVO();
    p.getOrganizations().add(o);
    c3.setPerson(p);
    this.pubItemVO.getMetadata().getCreators().add(c3);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getCreators(),
            new CreatorRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testCreatorRequired1 ---------------------");
  }

  @Ignore
  @Test
  public void testCreatorRequired2() throws Exception {
    logger.info("--------------------- STARTING testCreatorRequired2 ---------------------");

    final CreatorVO c = new CreatorVO();
    c.setType(CreatorType.ORGANIZATION);
    final OrganizationVO o = new OrganizationVO();
    o.setName("blubb");
    c.setOrganization(o);
    this.pubItemVO.getMetadata().getCreators().add(c);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getCreators(),
            new CreatorRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testCreatorRequired2 ---------------------");
  }

  @Ignore
  @Test
  public void testCreatorRequired3() throws Exception {
    logger.info("--------------------- STARTING testCreatorRequired3 ---------------------");

    final CreatorVO c = new CreatorVO();
    c.setType(CreatorType.PERSON);
    final PersonVO p = new PersonVO();
    p.setFamilyName("blubb");
    final OrganizationVO o = new OrganizationVO();
    o.setName("blubb");
    p.getOrganizations().add(o);
    c.setPerson(p);
    this.pubItemVO.getMetadata().getCreators().add(c);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getCreators(),
            new CreatorRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testCreatorRequired3 ---------------------");
  }

  @Ignore
  @Test
  public void testDateRequired1() throws Exception {
    logger.info("--------------------- STARTING testDateRequired1 ---------------------");

    this.mdsPublicationVO.setGenre(Genre.ARTICLE);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata(), new DateRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testDateRequired1 ---------------------");
  }

  @Ignore
  @Test
  public void testDateRequired2() throws Exception {
    logger.info("--------------------- STARTING testDateRequired2 ---------------------");

    this.mdsPublicationVO.setGenre(Genre.ARTICLE);
    this.mdsPublicationVO.setDateAccepted("01.01.2017");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata(), new DateRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testDateRequired2 ---------------------");
  }

  @Ignore
  @Test
  public void testDateRequired3() throws Exception {
    logger.info("--------------------- STARTING testDateRequired3 ---------------------");

    this.mdsPublicationVO.setGenre(Genre.ARTICLE);
    this.mdsPublicationVO.setDateCreated("01.01.2017");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata(), new DateRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testDateRequired3 ---------------------");
  }

  @Ignore
  @Test
  public void testDateRequired4() throws Exception {
    logger.info("--------------------- STARTING testDateRequired4 ---------------------");

    this.mdsPublicationVO.setGenre(Genre.ARTICLE);
    this.mdsPublicationVO.setDateModified("01.01.2017");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata(), new DateRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testDateRequired4 ---------------------");
  }

  @Ignore
  @Test
  public void testDateRequired5() throws Exception {
    logger.info("--------------------- STARTING testDateRequired5 ---------------------");

    this.mdsPublicationVO.setGenre(Genre.ARTICLE);
    this.mdsPublicationVO.setDatePublishedInPrint("01.01.2017");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata(), new DateRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testDateRequired5 ---------------------");
  }

  @Ignore
  @Test
  public void testDateRequired6() throws Exception {
    logger.info("--------------------- STARTING testDateRequired6 ---------------------");

    this.mdsPublicationVO.setGenre(Genre.ARTICLE);
    this.mdsPublicationVO.setDateSubmitted("01.01.2017");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata(), new DateRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testDateRequired6 ---------------------");
  }

  @Ignore
  @Test
  public void testEventTitleRequired1() throws Exception {
    logger.info("--------------------- STARTING testEventTitleRequired1 ---------------------");

    final EventVO e = new EventVO();
    this.mdsPublicationVO.setEvent(e);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getEvent(),
            new EventTitleRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testEventTitleRequired1 ---------------------");
  }

  @Ignore
  @Test
  public void testEventTitleRequired2() throws Exception {
    logger.info("--------------------- STARTING testEventTitleRequired2 ---------------------");

    final EventVO e = new EventVO();
    e.setEndDate("01.01.2017");
    e.setTitle("blubb");
    this.mdsPublicationVO.setEvent(e);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getEvent(),
            new EventTitleRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testEventTitleRequired2 ---------------------");
  }

  @Ignore
  @Test
  public void testEventTitleRequired3() throws Exception {
    logger.info("--------------------- STARTING testEventTitleRequired3 ---------------------");

    final EventVO e = new EventVO();
    e.setInvitationStatus(InvitationStatus.INVITED);
    e.setTitle("blubb");
    this.mdsPublicationVO.setEvent(e);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getEvent(),
            new EventTitleRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testEventTitleRequired3 ---------------------");
  }

  @Ignore
  @Test
  public void testEventTitleRequired4() throws Exception {
    logger.info("--------------------- STARTING testEventTitleRequired4 ---------------------");

    final EventVO e = new EventVO();
    e.setPlace("blubb");
    e.setTitle("blubb");
    this.mdsPublicationVO.setEvent(e);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getEvent(),
            new EventTitleRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testEventTitleRequired4 ---------------------");
  }

  @Ignore
  @Test
  public void testEventTitleRequired5() throws Exception {
    logger.info("--------------------- STARTING testEventTitleRequired5 ---------------------");

    final EventVO e = new EventVO();
    e.setStartDate("01.01.2017");
    e.setTitle("blubb");
    this.mdsPublicationVO.setEvent(e);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getEvent(),
            new EventTitleRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testEventTitleRequired5 ---------------------");
  }

  @Ignore
  @Test
  public void testGenreRequired1() throws Exception {
    logger.info("--------------------- STARTING testGenreRequired1 ---------------------");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getGenre(),
            new GenreRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testGenreRequired1 ---------------------");
  }

  @Ignore
  @Test
  public void testGenreRequired2() throws Exception {
    logger.info("--------------------- STARTING testGenreRequired2 ---------------------");

    this.mdsPublicationVO.setGenre(Genre.ARTICLE);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getGenre(),
            new GenreRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testGenreRequired2 ---------------------");
  }

  @Ignore
  @Test
  public void testIdTypeRequired1() throws Exception {
    logger.info("--------------------- STARTING testIdTypeRequired1 ---------------------");

    final IdentifierVO i1 = new IdentifierVO();
    i1.setId("blubb");
    this.mdsPublicationVO.getIdentifiers().add(i1);

    final IdentifierVO i2 = new IdentifierVO();
    i2.setId("blubb");
    this.mdsPublicationVO.getIdentifiers().add(i2);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getIdentifiers(),
            new IdTypeRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testIdTypeRequired1 ---------------------");
  }

  @Ignore
  @Test
  public void testIdTypeRequired2() throws Exception {
    logger.info("--------------------- STARTING testIdTypeRequired2 ---------------------");

    final IdentifierVO i = new IdentifierVO();
    i.setId("blubb");
    i.setType(IdType.ARXIV);
    this.mdsPublicationVO.getIdentifiers().add(i);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getIdentifiers(),
            new IdTypeRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testIdTypeRequired2 ---------------------");
  }

  @Ignore
  @Test
  public void testLanguageCode1() throws Exception {
    logger.info("--------------------- STARTING testLanguageCode1 ---------------------");

    this.mdsPublicationVO.getLanguages().add("blubb");
    this.mdsPublicationVO.getLanguages().add("blubber");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getLanguages(),
            new LanguageCodeValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testLanguageCode1 ---------------------");
  }

  @Ignore
  @Test
  public void testLanguageCode2() throws Exception {
    logger.info("--------------------- STARTING testLanguageCode2 ---------------------");

    this.mdsPublicationVO.getLanguages().add("aaa");
    this.mdsPublicationVO.getLanguages().add("bbb");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getLanguages(),
            new LanguageCodeValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testLanguageCode2 ---------------------");
  }

  @Ignore
  @Test
  public void testMdsPublicationDateFormat1() throws Exception {
    logger
        .info("--------------------- STARTING testMdsPublicationDateFormat1 ---------------------");

    this.mdsPublicationVO.setDateAccepted("blubb");
    this.mdsPublicationVO.setDateCreated("blubb");
    this.mdsPublicationVO.setDateModified("blubb");
    this.mdsPublicationVO.setDatePublishedInPrint("blubb");
    this.mdsPublicationVO.setDatePublishedOnline("blubb");
    this.mdsPublicationVO.setDateSubmitted("blubb");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata(),
            new MdsPublicationDateFormatValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger
        .info("--------------------- FINISHED testMdsPublicationDateFormat1 ---------------------");
  }

  @Ignore
  @Test
  public void testMdsPublicationDateFormat2() throws Exception {
    logger
        .info("--------------------- STARTING testMdsPublicationDateFormat2 ---------------------");

    this.mdsPublicationVO.setDateAccepted("2017");
    this.mdsPublicationVO.setDateCreated("2017");
    this.mdsPublicationVO.setDateModified("2017");
    this.mdsPublicationVO.setDatePublishedInPrint("2017");
    this.mdsPublicationVO.setDatePublishedOnline("2017");
    this.mdsPublicationVO.setDateSubmitted("2017");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata(),
            new MdsPublicationDateFormatValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger
        .info("--------------------- FINISHED testMdsPublicationDateFormat2 ---------------------");
  }

  @Ignore
  @Test
  public void testMdsPublicationDateFormat3() throws Exception {
    logger
        .info("--------------------- STARTING testMdsPublicationDateFormat3 ---------------------");

    this.mdsPublicationVO.setDateAccepted("2017-01");
    this.mdsPublicationVO.setDateCreated("2017-01");
    this.mdsPublicationVO.setDateModified("2017-01");
    this.mdsPublicationVO.setDatePublishedInPrint("2017-01");
    this.mdsPublicationVO.setDatePublishedOnline("2017-01");
    this.mdsPublicationVO.setDateSubmitted("2017-01");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata(),
            new MdsPublicationDateFormatValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger
        .info("--------------------- FINISHED testMdsPublicationDateFormat3 ---------------------");
  }

  @Ignore
  @Test
  public void testMdsPublicationDateFormat4() throws Exception {
    logger
        .info("--------------------- STARTING testMdsPublicationDateFormat4 ---------------------");

    this.mdsPublicationVO.setDateAccepted("2017-01-01");
    this.mdsPublicationVO.setDateCreated("2017-01-01");
    this.mdsPublicationVO.setDateModified("2017-01-01");
    this.mdsPublicationVO.setDatePublishedInPrint("2017-01-01");
    this.mdsPublicationVO.setDatePublishedOnline("2017-01-01");
    this.mdsPublicationVO.setDateSubmitted("2017-01-01");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata(),
            new MdsPublicationDateFormatValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger
        .info("--------------------- FINISHED testMdsPublicationDateFormat4 ---------------------");
  }

  @Ignore
  @Test
  public void testNoSlashesInFileName1() throws Exception {
    logger.info("--------------------- STARTING testNoSlashesInFileName1 ---------------------");

    final FileVO f1 = new FileVO();
    f1.setStorage(Storage.INTERNAL_MANAGED);
    f1.setName("blu/bb");

    this.pubItemVO.getFiles().add(f1);

    final FileVO f2 = new FileVO();
    f2.setStorage(Storage.INTERNAL_MANAGED);
    final MdsFileVO m1 = new MdsFileVO();
    m1.setTitle("blu/bb");
    f2.setDefaultMetadata(m1);
    final MdsFileVO m2 = new MdsFileVO();
    m2.setTitle("blu/bb");
    f2.setDefaultMetadata(m2);
    this.pubItemVO.getFiles().add(f2);

    final FluentValidator v =
        FluentValidator.checkAll()
            .on(this.pubItemVO.getFiles(), new NoSlashesInFileNameValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testNoSlashesInFileName1 ---------------------");
  }

  @Ignore
  @Test
  public void testNoSlashesInFileName2() throws Exception {
    logger.info("--------------------- STARTING testNoSlashesInFileName2 ---------------------");

    final FileVO f1 = new FileVO();
    f1.setStorage(Storage.INTERNAL_MANAGED);
    f1.setName("blubb");

    this.pubItemVO.getFiles().add(f1);

    final FileVO f2 = new FileVO();
    f2.setStorage(Storage.INTERNAL_MANAGED);
    final MdsFileVO m1 = new MdsFileVO();
    m1.setTitle("blubb");
    f2.setDefaultMetadata(m1);
    final MdsFileVO m2 = new MdsFileVO();
    m2.setTitle("blubb");
    f2.setDefaultMetadata(m2);
    this.pubItemVO.getFiles().add(f2);

    final FluentValidator v =
        FluentValidator.checkAll()
            .on(this.pubItemVO.getFiles(), new NoSlashesInFileNameValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("--------------------- FINISHED testNoSlashesInFileName2 ---------------------");
  }

  @Ignore
  @Test
  public void testOrganizationNameRequired1() throws Exception {
    logger
        .info("--------------------- STARTING testOrganizationNameRequired1 ---------------------");

    final CreatorVO c1 = new CreatorVO();
    c1.setType(CreatorType.PERSON);
    final PersonVO p1 = new PersonVO();
    final OrganizationVO o1 = new OrganizationVO();
    o1.setAddress("blubb");
    p1.getOrganizations().add(o1);
    final OrganizationVO o2 = new OrganizationVO();
    o2.setAddress("blubb");
    p1.getOrganizations().add(o2);
    c1.setPerson(p1);
    this.pubItemVO.getMetadata().getCreators().add(c1);

    final CreatorVO c2 = new CreatorVO();
    c2.setType(CreatorType.PERSON);
    final PersonVO p2 = new PersonVO();
    final OrganizationVO o3 = new OrganizationVO();
    o3.setAddress("blubb");
    p2.getOrganizations().add(o3);
    final OrganizationVO o4 = new OrganizationVO();
    o4.setAddress("blubb");
    p2.getOrganizations().add(o4);
    c2.setPerson(p2);
    this.pubItemVO.getMetadata().getCreators().add(c2);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getCreators(),
            new OrganizationNameRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger
        .info("--------------------- FINISHED testOrganizationNameRequired1 ---------------------");
  }

  @Ignore
  @Test
  public void testOrganizationNameRequired2() throws Exception {
    logger
        .info("--------------------- STARTING testOrganizationNameRequired2 ---------------------");

    final CreatorVO c1 = new CreatorVO();
    c1.setType(CreatorType.PERSON);
    final PersonVO p1 = new PersonVO();
    final OrganizationVO o1 = new OrganizationVO();
    o1.setName("blubb");
    o1.setAddress("blubb");
    p1.getOrganizations().add(o1);
    final OrganizationVO o2 = new OrganizationVO();
    o2.setName("blubb");
    o2.setAddress("blubb");
    p1.getOrganizations().add(o2);
    c1.setPerson(p1);
    this.pubItemVO.getMetadata().getCreators().add(c1);

    final CreatorVO c2 = new CreatorVO();
    c2.setType(CreatorType.PERSON);
    final PersonVO p2 = new PersonVO();
    final OrganizationVO o3 = new OrganizationVO();
    o3.setName("blubb");
    o3.setAddress("blubb");
    p2.getOrganizations().add(o3);
    final OrganizationVO o4 = new OrganizationVO();
    o4.setName("blubb");
    o4.setAddress("blubb");
    p2.getOrganizations().add(o4);
    c2.setPerson(p2);
    this.pubItemVO.getMetadata().getCreators().add(c2);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getCreators(),
            new OrganizationNameRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger
        .info("--------------------- FINISHED testOrganizationNameRequired2 ---------------------");
  }

  @Ignore
  @Test
  public void testPublicationCreatorsRoleRequired1() throws Exception {
    logger
        .info("----------------- STARTING testPublicationCreatorsRoleRequired1 -----------------");

    final CreatorVO c1 = new CreatorVO();
    c1.setType(CreatorType.ORGANIZATION);
    final OrganizationVO o1 = new OrganizationVO();
    o1.setName("blubb");
    c1.setOrganization(o1);
    this.pubItemVO.getMetadata().getCreators().add(c1);

    final CreatorVO c2 = new CreatorVO();
    c2.setType(CreatorType.ORGANIZATION);
    final OrganizationVO o2 = new OrganizationVO();
    o2.setAddress("blubb");
    c2.setOrganization(o2);
    this.pubItemVO.getMetadata().getCreators().add(c2);

    final CreatorVO c3 = new CreatorVO();
    c3.setType(CreatorType.PERSON);
    final PersonVO p1 = new PersonVO();
    p1.setFamilyName("blubb");
    c3.setPerson(p1);
    this.pubItemVO.getMetadata().getCreators().add(c3);

    final CreatorVO c4 = new CreatorVO();
    c4.setType(CreatorType.PERSON);
    final PersonVO p2 = new PersonVO();
    p2.setGivenName("blubb");
    final OrganizationVO o3 = new OrganizationVO();
    o3.setAddress("blubb");
    p2.getOrganizations().add(o3);
    final OrganizationVO o4 = new OrganizationVO();
    o4.setAddress("blubb");
    p2.getOrganizations().add(o3);
    c4.setPerson(p2);
    this.pubItemVO.getMetadata().getCreators().add(c3);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getCreators(),
            new PublicationCreatorsRoleRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger
        .info("----------------- FINISHED testPublicationCreatorsRoleRequired1 -----------------");
  }

  @Ignore
  @Test
  public void testPublicationCreatorsRoleRequired2() throws Exception {
    logger
        .info("----------------- STARTING testPublicationCreatorsRoleRequired2 -----------------");

    final CreatorVO c1 = new CreatorVO();
    c1.setType(CreatorType.ORGANIZATION);
    final OrganizationVO o1 = new OrganizationVO();
    o1.setName("blubb");
    c1.setOrganization(o1);
    c1.setRole(CreatorRole.ACTOR);
    this.pubItemVO.getMetadata().getCreators().add(c1);

    final CreatorVO c2 = new CreatorVO();
    c2.setType(CreatorType.ORGANIZATION);
    final OrganizationVO o2 = new OrganizationVO();
    o2.setAddress("blubb");
    c2.setOrganization(o2);
    c2.setRole(CreatorRole.ACTOR);
    this.pubItemVO.getMetadata().getCreators().add(c2);

    final CreatorVO c3 = new CreatorVO();
    c3.setType(CreatorType.PERSON);
    final PersonVO p1 = new PersonVO();
    p1.setFamilyName("blubb");
    c3.setPerson(p1);
    c3.setRole(CreatorRole.ACTOR);
    this.pubItemVO.getMetadata().getCreators().add(c3);

    final CreatorVO c4 = new CreatorVO();
    c4.setType(CreatorType.PERSON);
    final PersonVO p2 = new PersonVO();
    p2.setGivenName("blubb");
    final OrganizationVO o3 = new OrganizationVO();
    o3.setAddress("blubb");
    p2.getOrganizations().add(o3);
    final OrganizationVO o4 = new OrganizationVO();
    o4.setAddress("blubb");
    p2.getOrganizations().add(o3);
    c4.setPerson(p2);
    c4.setRole(CreatorRole.ACTOR);
    this.pubItemVO.getMetadata().getCreators().add(c3);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getCreators(),
            new PublicationCreatorsRoleRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger
        .info("----------------- FINISHED testPublicationCreatorsRoleRequired2 -----------------");
  }

  @Ignore
  @Test
  public void testSourceCreatorsRoleRequired1() throws Exception {
    logger.info("------------------- STARTING testSourceCreatorsRoleRequired1 -------------------");

    final CreatorVO c1 = new CreatorVO();
    c1.setType(CreatorType.ORGANIZATION);
    final OrganizationVO o1 = new OrganizationVO();
    o1.setName("blubb");
    c1.setOrganization(o1);
    final SourceVO s1 = new SourceVO();
    s1.getCreators().add(c1);
    this.pubItemVO.getMetadata().getSources().add(s1);

    final CreatorVO c2 = new CreatorVO();
    c2.setType(CreatorType.ORGANIZATION);
    final OrganizationVO o2 = new OrganizationVO();
    o2.setAddress("blubb");
    c2.setOrganization(o2);
    final SourceVO s2 = new SourceVO();
    s2.getCreators().add(c2);
    this.pubItemVO.getMetadata().getSources().add(s2);

    final CreatorVO c3 = new CreatorVO();
    c3.setType(CreatorType.PERSON);
    final PersonVO p1 = new PersonVO();
    p1.setFamilyName("blubb");
    c3.setPerson(p1);
    final SourceVO s3 = new SourceVO();
    s3.getCreators().add(c3);
    this.pubItemVO.getMetadata().getSources().add(s3);

    final CreatorVO c4 = new CreatorVO();
    c4.setType(CreatorType.PERSON);
    final PersonVO p2 = new PersonVO();
    p2.setGivenName("blubb");
    final OrganizationVO o3 = new OrganizationVO();
    o3.setAddress("blubb");
    p2.getOrganizations().add(o3);
    final OrganizationVO o4 = new OrganizationVO();
    o4.setAddress("blubb");
    p2.getOrganizations().add(o3);
    c4.setPerson(p2);
    final SourceVO s4 = new SourceVO();
    s4.getCreators().add(c4);
    this.pubItemVO.getMetadata().getSources().add(s4);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getSources(),
            new SourceCreatorsRoleRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("------------------- FINISHED testSourceCreatorsRoleRequired1 -------------------");
  }

  @Ignore
  @Test
  public void testSourceCreatorsRoleRequired2() throws Exception {
    logger.info("------------------- STARTING testSourceCreatorsRoleRequired2 -------------------");

    final CreatorVO c1 = new CreatorVO();
    c1.setType(CreatorType.ORGANIZATION);
    final OrganizationVO o1 = new OrganizationVO();
    o1.setName("blubb");
    c1.setOrganization(o1);
    final SourceVO s1 = new SourceVO();
    c1.setRole(CreatorRole.ACTOR);
    s1.getCreators().add(c1);
    this.pubItemVO.getMetadata().getSources().add(s1);

    final CreatorVO c2 = new CreatorVO();
    c2.setType(CreatorType.ORGANIZATION);
    final OrganizationVO o2 = new OrganizationVO();
    o2.setAddress("blubb");
    c2.setOrganization(o2);
    final SourceVO s2 = new SourceVO();
    c2.setRole(CreatorRole.ACTOR);
    s2.getCreators().add(c2);
    this.pubItemVO.getMetadata().getSources().add(s2);

    final CreatorVO c3 = new CreatorVO();
    c3.setType(CreatorType.PERSON);
    final PersonVO p1 = new PersonVO();
    p1.setFamilyName("blubb");
    c3.setPerson(p1);
    final SourceVO s3 = new SourceVO();
    c3.setRole(CreatorRole.ACTOR);
    s3.getCreators().add(c3);
    this.pubItemVO.getMetadata().getSources().add(s3);

    final CreatorVO c4 = new CreatorVO();
    c4.setType(CreatorType.PERSON);
    final PersonVO p2 = new PersonVO();
    p2.setGivenName("blubb");
    final OrganizationVO o3 = new OrganizationVO();
    o3.setAddress("blubb");
    p2.getOrganizations().add(o3);
    final OrganizationVO o4 = new OrganizationVO();
    o4.setAddress("blubb");
    p2.getOrganizations().add(o3);
    c4.setPerson(p2);
    final SourceVO s4 = new SourceVO();
    c4.setRole(CreatorRole.ACTOR);
    s4.getCreators().add(c4);
    this.pubItemVO.getMetadata().getSources().add(s4);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getSources(),
            new SourceCreatorsRoleRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("------------------- FINISHED testSourceCreatorsRoleRequired2 -------------------");
  }

  @Ignore
  @Test
  public void testSourceGenresRequired1() throws Exception {
    logger.info("---------------------- STARTING testSourceGenresRequired1 ----------------------");

    final SourceVO s1 = new SourceVO();
    s1.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s1);

    final SourceVO s2 = new SourceVO();
    s2.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s2);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getSources(),
            new SourceGenresRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("---------------------- FINISHED testSourceGenresRequired1 ----------------------");
  }

  @Ignore
  @Test
  public void testSourceGenresRequired2() throws Exception {
    logger.info("---------------------- STARTING testSourceGenresRequired2 ----------------------");

    final SourceVO s1 = new SourceVO();
    s1.setTitle("blubb");
    s1.setGenre(SourceVO.Genre.ENCYCLOPEDIA);
    this.pubItemVO.getMetadata().getSources().add(s1);

    final SourceVO s2 = new SourceVO();
    s2.setTitle("blubb");
    s2.setGenre(SourceVO.Genre.ENCYCLOPEDIA);
    this.pubItemVO.getMetadata().getSources().add(s2);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getSources(),
            new SourceGenresRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("---------------------- FINISHED testSourceGenresRequired2 ----------------------");
  }

  @Ignore
  @Test
  public void testSourceRequired1() throws Exception {
    logger.info("---------------------- STARTING testSourceRequired1 ----------------------");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getSources(),
            new SourceRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("---------------------- FINISHED testSourceRequired1 ----------------------");
  }

  @Ignore
  @Test
  public void testSourceRequired2() throws Exception {
    logger.info("---------------------- STARTING testSourceRequired2 ----------------------");

    final SourceVO s = new SourceVO();
    this.mdsPublicationVO.getSources().add(s);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getSources(),
            new SourceRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("---------------------- FINISHED testSourceRequired2 ----------------------");
  }

  @Ignore
  @Test
  public void testSourceTitlesRequired1() throws Exception {
    logger.info("---------------------- STARTING testSourceTitlesRequired1 ----------------------");

    final SourceVO s1 = new SourceVO();
    s1.setVolume("blubb");
    this.pubItemVO.getMetadata().getSources().add(s1);

    final SourceVO s2 = new SourceVO();
    s2.setTotalNumberOfPages("blubb");
    this.pubItemVO.getMetadata().getSources().add(s2);

    final SourceVO s3 = new SourceVO();
    s3.setStartPage("blubb");
    this.pubItemVO.getMetadata().getSources().add(s3);

    final SourceVO s4 = new SourceVO();
    s4.setSequenceNumber("blubb");
    this.pubItemVO.getMetadata().getSources().add(s4);

    final SourceVO s5 = new SourceVO();
    s5.setPublishingInfo(new PublishingInfoVO());
    this.pubItemVO.getMetadata().getSources().add(s5);

    final SourceVO s6 = new SourceVO();
    s6.setIssue("blubb");
    this.pubItemVO.getMetadata().getSources().add(s6);

    final SourceVO s7 = new SourceVO();
    s7.getIdentifiers().add(new IdentifierVO());
    this.pubItemVO.getMetadata().getSources().add(s7);

    final SourceVO s8 = new SourceVO();
    s8.getSources().add(new SourceVO());
    this.pubItemVO.getMetadata().getSources().add(s8);

    final SourceVO s9 = new SourceVO();
    s9.setGenre((SourceVO.Genre.ENCYCLOPEDIA));
    this.pubItemVO.getMetadata().getSources().add(s9);

    final SourceVO s10 = new SourceVO();
    s10.setEndPage("blubb");
    this.pubItemVO.getMetadata().getSources().add(s10);

    final SourceVO s11 = new SourceVO();
    s11.setDatePublishedInPrint(new Date());
    this.pubItemVO.getMetadata().getSources().add(s11);

    final SourceVO s12 = new SourceVO();
    s12.getCreators().add(new CreatorVO());
    this.pubItemVO.getMetadata().getSources().add(s12);

    final SourceVO s13 = new SourceVO();
    s13.getAlternativeTitles().add(new AlternativeTitleVO());
    this.pubItemVO.getMetadata().getSources().add(s13);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getSources(),
            new SourceTitlesRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("---------------------- FINISHED testSourceTitlesRequired1 ----------------------");
  }

  @Ignore
  @Test
  public void testSourceTitlesRequired2() throws Exception {
    logger.info("---------------------- STARTING testSourceTitlesRequired2 ----------------------");

    final SourceVO s1 = new SourceVO();
    s1.setVolume("blubb");
    s1.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s1);

    final SourceVO s2 = new SourceVO();
    s2.setTotalNumberOfPages("blubb");
    s2.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s2);

    final SourceVO s3 = new SourceVO();
    s3.setStartPage("blubb");
    s3.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s3);

    final SourceVO s4 = new SourceVO();
    s4.setSequenceNumber("blubb");
    s4.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s4);

    final SourceVO s5 = new SourceVO();
    s5.setPublishingInfo(new PublishingInfoVO());
    s5.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s5);

    final SourceVO s6 = new SourceVO();
    s6.setIssue("blubb");
    s6.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s6);

    final SourceVO s7 = new SourceVO();
    s7.getIdentifiers().add(new IdentifierVO());
    s7.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s7);

    final SourceVO s8 = new SourceVO();
    s8.getSources().add(new SourceVO());
    s8.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s8);

    final SourceVO s9 = new SourceVO();
    s9.setGenre((SourceVO.Genre.ENCYCLOPEDIA));
    s9.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s9);

    final SourceVO s10 = new SourceVO();
    s10.setEndPage("blubb");
    s10.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s10);

    final SourceVO s11 = new SourceVO();
    s11.setDatePublishedInPrint(new Date());
    s11.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s11);

    final SourceVO s12 = new SourceVO();
    s12.getCreators().add(new CreatorVO());
    s12.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s12);

    final SourceVO s13 = new SourceVO();
    s13.getAlternativeTitles().add(new AlternativeTitleVO());
    s13.setTitle("blubb");
    this.pubItemVO.getMetadata().getSources().add(s13);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getSources(),
            new SourceTitlesRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("---------------------- FINISHED testSourceTitlesRequired2 ----------------------");
  }

  @Ignore
  @Test
  public void testTitleRequired1() throws Exception {
    logger.info("---------------------- STARTING testTileRequired1 ----------------------");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getTitle(),
            new TitleRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("---------------------- FINISHED testTitleRequired1 ----------------------");
  }

  @Ignore
  @Test
  public void testTitleRequired2() throws Exception {
    logger.info("---------------------- STARTING testTileRequired2 ----------------------");

    this.mdsPublicationVO.setTitle("blubb");

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getMetadata().getTitle(),
            new TitleRequiredValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("---------------------- FINISHED testTitleRequired2 ----------------------");
  }

  @Ignore
  @Test
  public void testUriAsLocator1() throws Exception {
    logger.info("---------------------- STARTING testUriAsLocator1 ----------------------");

    final FileVO f1 = new FileVO();
    f1.setContent("blubb");
    f1.setStorage(Storage.EXTERNAL_URL);
    this.pubItemVO.getFiles().add(f1);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getFiles(), new UriAsLocatorValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertFalse(complexResult.isSuccess());

    logger.info("---------------------- FINISHED testUriAsLocator1 ----------------------");
  }

  @Ignore
  @Test
  public void testUriAsLocator2() throws Exception {
    logger.info("---------------------- STARTING testUriAsLocator2 ----------------------");

    final FileVO f1 = new FileVO();
    f1.setContent("www.google.de");
    f1.setStorage(Storage.EXTERNAL_URL);
    this.pubItemVO.getFiles().add(f1);

    final FileVO f2 = new FileVO();
    f2.setContent("http://www.google.de");
    f2.setStorage(Storage.EXTERNAL_URL);
    this.pubItemVO.getFiles().add(f2);

    final FileVO f3 = new FileVO();
    f3.setContent("https://www.google.de");
    f3.setStorage(Storage.EXTERNAL_URL);
    this.pubItemVO.getFiles().add(f3);

    final FileVO f4 = new FileVO();
    f4.setContent("http://hdl.handle.net/11858/00-001Z-0000-002B-68A6-3");
    f4.setStorage(Storage.EXTERNAL_URL);
    this.pubItemVO.getFiles().add(f4);

    final FileVO f5 = new FileVO();
    f5.setContent("http://www.kyb.tuebingen.mpg.de/fileadmin/user_upload/files/2011/GREAT10.pdf");
    f5.setStorage(Storage.EXTERNAL_URL);
    this.pubItemVO.getFiles().add(f5);

    final FluentValidator v =
        FluentValidator.checkAll().on(this.pubItemVO.getFiles(), new UriAsLocatorValidator());

    final ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

    logger.info(complexResult.toString());
    // LOG.info(this.validationService.convert(complexResult).toString());

    Assert.assertTrue(complexResult.isSuccess());

    logger.info("---------------------- FINISHED testUriAsLocator2 ----------------------");
  }
}
