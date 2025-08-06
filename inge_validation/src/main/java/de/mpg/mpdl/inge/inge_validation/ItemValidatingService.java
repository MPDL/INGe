package de.mpg.mpdl.inge.inge_validation;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationServiceException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.inge_validation.validator.*;
import de.mpg.mpdl.inge.inge_validation.validator.cone.ClassifiedKeywordsValidator;
import de.mpg.mpdl.inge.inge_validation.validator.cone.LanguageCodeValidator;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ItemValidatingService {
  private static final Logger logger = LogManager.getLogger(ItemValidatingService.class);

  public void validate(ItemVersionVO itemVO, ValidationPoint validationPoint) throws ValidationServiceException, ValidationException {

    try {
      Validation validation = new Validation();
      validation.validate(itemVO, validationPoint);
    } catch (ValidationServiceException e) {
      logger.error("validate:", e);
      throw e;
    } catch (ValidationException e) {
      throw e;
    } catch (Exception e) {
      logger.error("validate: " + itemVO + " " + validationPoint, e);
      throw new ValidationServiceException("validate:", e);
    }
  }

  public ValidationReportVO validateAbstract(AbstractVO abstractVO) {

    List<AbstractVO> abstractVOs = new ArrayList<>();
    abstractVOs.add(abstractVO);

    FluentValidator validator = FluentValidator.checkAll().failOver().on(abstractVOs, new Utf8AbstractValidator());

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());
    ValidationReportVO validationReportVO = Validation.getValidationReportVO(complexResult);

    return validationReportVO;
  }

  public ValidationReportVO validateAlternativeTitle(AlternativeTitleVO alternativeTitleVO) {

    List<AlternativeTitleVO> alternativeTitleVOs = new ArrayList<>();
    alternativeTitleVOs.add(alternativeTitleVO);

    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on(alternativeTitleVOs, new Utf8AlternativeTitleValidator()) //
        .on(alternativeTitleVOs, new AlternativeTitleRequiredValidator());

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());
    ValidationReportVO validationReportVO = Validation.getValidationReportVO(complexResult);

    return validationReportVO;
  }

  public ValidationReportVO validateComponent(FileDbVO fileDbVO) {

    List<FileDbVO> fileDbVOs = new ArrayList<>();
    fileDbVOs.add(fileDbVO);

    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on(fileDbVOs, new ComponentsDataRequiredValidator()) //
        .on(fileDbVOs, new ComponentsDateFormatValidator()) //
        .on(fileDbVOs, new ComponentsIpRangeRequiredValidator()) //
        .on(fileDbVOs, new ComponentsNoSlashesInNameValidator()) //
        .on(fileDbVOs, new ComponentsUriAsLocatorValidator());

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());
    ValidationReportVO validationReportVO = Validation.getValidationReportVO(complexResult);

    return validationReportVO;
  }

  public ValidationReportVO validateCreator(CreatorVO creatorVO) {

    List<CreatorVO> creatorVOs = new ArrayList<>();
    creatorVOs.add(creatorVO);

    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on(creatorVOs, new CreatorsOrcidValidator()) //
        .on(creatorVOs, new CreatorsOrganizationsNameRequiredValidator()) //
        .on(creatorVOs, new CreatorsRoleRequiredValidator()) //
        .on(creatorVOs, new CreatorsWithOrganisationRequiredValidator());

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());
    ValidationReportVO validationReportVO = Validation.getValidationReportVO(complexResult);

    return validationReportVO;
  }

  public ValidationReportVO validateEvent(EventVO eventVO) {

    FluentValidator validator = FluentValidator.checkAll().failOver().on(eventVO, new EventTitleRequiredValidator());

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());
    ValidationReportVO validationReportVO = Validation.getValidationReportVO(complexResult);

    return validationReportVO;
  }

  public ValidationReportVO validateIdentifier(IdentifierVO identifierVO) {

    List<IdentifierVO> identifierVOs = new ArrayList<>();
    identifierVOs.add(identifierVO);

    FluentValidator validator = FluentValidator.checkAll().failOver().on(identifierVOs, new IdTypeRequiredAndFormatValidator());

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());
    ValidationReportVO validationReportVO = Validation.getValidationReportVO(complexResult);

    return validationReportVO;
  }

  public ValidationReportVO validateLanguages(List<String> languages) {

    FluentValidator validator = FluentValidator.checkAll().failOver().on(languages, new LanguageCodeValidator());

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());
    ValidationReportVO validationReportVO = Validation.getValidationReportVO(complexResult);

    return validationReportVO;
  }

  public ValidationReportVO validateMdsPublication(MdsPublicationVO mdsPublicationVO) {

    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on(mdsPublicationVO, new MdsPublicationDateFormatValidator()).on(mdsPublicationVO, new MdsPublikationDateRequiredValidator())
        .on(mdsPublicationVO.getGenre(), new MdsPublicationGenreRequiredValidator());

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());
    ValidationReportVO validationReportVO = Validation.getValidationReportVO(complexResult);

    return validationReportVO;
  }

  public ValidationReportVO validateSource(MdsPublicationVO.Genre genre, SourceVO sourceVO) {

    List<SourceVO> sourceVOs = new ArrayList<>();
    sourceVOs.add(sourceVO);

    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on(sourceVOs, new SourceCreatorsNameRequiredValidator()) //
        .on(sourceVOs, new SourceCreatorsRoleRequiredValidator()) //
        .on(sourceVOs, new SourceGenreRequiredValidator()) //
        .on(sourceVOs, new SourceRequiredValidator()) //
        .when(MdsPublicationVO.Genre.ARTICLE.equals(genre) //
            || MdsPublicationVO.Genre.BOOK_ITEM.equals(genre) //
            || MdsPublicationVO.Genre.CONFERENCE_PAPER.equals(genre) //
            || MdsPublicationVO.Genre.MAGAZINE_ARTICLE.equals(genre) //
            || MdsPublicationVO.Genre.MEETING_ABSTRACT.equals(genre) //
            || MdsPublicationVO.Genre.REVIEW_ARTICLE.equals(genre) //
            || MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION.equals(genre) //
            || MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY.equals(genre) //
            || MdsPublicationVO.Genre.CONTRIBUTION_TO_ENCYCLOPEDIA.equals(genre) //
            || MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT.equals(genre) //
            || MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK.equals(genre) //
            || MdsPublicationVO.Genre.NEWSPAPER_ARTICLE.equals(genre)) //
        .on(sourceVOs, new SourceTitleRequiredValidator());

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());
    ValidationReportVO validationReportVO = Validation.getValidationReportVO(complexResult);

    return validationReportVO;
  }

  public ValidationReportVO validateSubject(SubjectVO subjectVO) {

    List<SubjectVO> subjectVOs = new ArrayList<>();
    subjectVOs.add(subjectVO);

    FluentValidator validator = FluentValidator.checkAll().failOver().on(subjectVOs, new ClassifiedKeywordsValidator());

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());
    ValidationReportVO validationReportVO = Validation.getValidationReportVO(complexResult);

    return validationReportVO;
  }

  public ValidationReportVO validateTitle(String title) {

    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on(title, new TitleRequiredValidator()) //
        .on(title, new Utf8TitleValidator());

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());
    ValidationReportVO validationReportVO = Validation.getValidationReportVO(complexResult);

    return validationReportVO;
  }
}
