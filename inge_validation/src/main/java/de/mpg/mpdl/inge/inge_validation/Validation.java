package de.mpg.mpdl.inge.inge_validation;

import org.apache.log4j.Logger;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;
import com.baidu.unbiz.fluentvalidator.ValidationError;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationServiceException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.inge_validation.validator.ComponentContentRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.ComponentDataRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.CreatorWithOrganisationRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.DateRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.EventTitleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.FileDateFormatValidator;
import de.mpg.mpdl.inge.inge_validation.validator.GenreRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.IdTypeRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.MdsPublicationDateFormatValidator;
import de.mpg.mpdl.inge.inge_validation.validator.NoSlashesInFileNameValidator;
import de.mpg.mpdl.inge.inge_validation.validator.OrganizationNameRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.CreatorsRoleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.SourceCreatorsRoleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.SourceGenresRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.SourceRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.SourceTitlesRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.TitleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.UriAsLocatorValidator;
import de.mpg.mpdl.inge.inge_validation.validator.cone.ClassifiedKeywordsValidator;
import de.mpg.mpdl.inge.inge_validation.validator.cone.ComponentMimeTypesValidator;
import de.mpg.mpdl.inge.inge_validation.validator.cone.LanguageCodeValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.GenreValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.PublishingDateRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SequenceInfomationValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourceVolumesRequiredValidator;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

public class Validation {
  private static final Logger logger = Logger.getLogger(Validation.class);

  public static void validate(final ItemVO itemVO, ValidationPoint validationPoint)
      throws ValidationServiceException, ValidationException {

    if (itemVO instanceof PubItemVO == false) {
      throw new ValidationServiceException("itemVO instanceof PubItemVO == false");
    }

    final PubItemVO pubItemVO = (PubItemVO) itemVO;

    switch (validationPoint) {

      case SAVE:
        final FluentValidator vSave =
            FluentValidator.checkAll().failOver()
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator());

        final ComplexResult resultSave = vSave.doValidate().result(ResultCollectors.toComplex());

        logger.info(resultSave);

        Validation.checkResult(resultSave);

        break;

      case SIMPLE:
        final FluentValidator vSimple =
            FluentValidator
                .checkAll()
                .failOver()
                .on(pubItemVO.getMetadata().getSubjects(), new ClassifiedKeywordsValidator())
                .on(pubItemVO.getFiles(), new ComponentMimeTypesValidator())
                .on(pubItemVO.getMetadata().getLanguages(), new LanguageCodeValidator())
                .on(pubItemVO.getFiles(), new ComponentContentRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentDataRequiredValidator())
                .on(pubItemVO.getFiles(), new FileDateFormatValidator())
                .on(pubItemVO.getMetadata().getCreators(),
                    new CreatorWithOrganisationRequiredValidator())
                .on(pubItemVO.getMetadata().getEvent(), new EventTitleRequiredValidator())
                .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
                .on(pubItemVO.getMetadata().getIdentifiers(), new IdTypeRequiredValidator())
                .on(pubItemVO.getMetadata(), new MdsPublicationDateFormatValidator())
                .on(pubItemVO.getFiles(), new NoSlashesInFileNameValidator())
                .on(pubItemVO.getMetadata().getCreators(), new OrganizationNameRequiredValidator())
                .on(pubItemVO.getMetadata().getCreators(), new CreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceGenresRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceTitlesRequiredValidator())
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
                .on(pubItemVO.getFiles(), new UriAsLocatorValidator());

        final ComplexResult resultSimple =
            vSimple.doValidate().result(ResultCollectors.toComplex());

        logger.info(resultSimple);

        Validation.checkResult(resultSimple);

        break;

      case STANDARD:
        final FluentValidator vStandard =
            FluentValidator
                .checkAll()
                .failOver()
                .on(pubItemVO.getMetadata().getSubjects(), new ClassifiedKeywordsValidator())
                .on(pubItemVO.getFiles(), new ComponentMimeTypesValidator())
                .on(pubItemVO.getMetadata().getLanguages(), new LanguageCodeValidator())
                .on(pubItemVO.getFiles(), new ComponentContentRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentDataRequiredValidator())
                .on(pubItemVO.getFiles(), new FileDateFormatValidator())
                .on(pubItemVO.getMetadata().getCreators(),
                    new CreatorWithOrganisationRequiredValidator())
                .on(pubItemVO.getMetadata(), new DateRequiredValidator())
                .when(
                    !MdsPublicationVO.Genre.SERIES.equals(pubItemVO.getMetadata().getGenre())
                        && !MdsPublicationVO.Genre.JOURNAL.equals(pubItemVO.getMetadata()
                            .getGenre())
                        && !MdsPublicationVO.Genre.MANUSCRIPT.equals(pubItemVO.getMetadata()
                            .getGenre())
                        && !MdsPublicationVO.Genre.OTHER.equals(pubItemVO.getMetadata().getGenre()))
                .on(pubItemVO.getMetadata().getEvent(), new EventTitleRequiredValidator())
                .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
                .on(pubItemVO.getMetadata().getIdentifiers(), new IdTypeRequiredValidator())
                .on(pubItemVO.getMetadata(), new MdsPublicationDateFormatValidator())
                .on(pubItemVO.getFiles(), new NoSlashesInFileNameValidator())
                .on(pubItemVO.getMetadata().getCreators(), new OrganizationNameRequiredValidator())
                .on(pubItemVO.getMetadata().getCreators(), new CreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceGenresRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceRequiredValidator())
                .when(
                    MdsPublicationVO.Genre.ARTICLE.equals(pubItemVO.getMetadata().getGenre())
                        || MdsPublicationVO.Genre.BOOK_ITEM.equals(pubItemVO.getMetadata()
                            .getGenre())
                        || MdsPublicationVO.Genre.CONFERENCE_PAPER.equals(pubItemVO.getMetadata()
                            .getGenre())
                        || MdsPublicationVO.Genre.MEETING_ABSTRACT.equals(pubItemVO.getMetadata()
                            .getGenre()))
                .on(pubItemVO.getMetadata().getSources(), new SourceTitlesRequiredValidator())
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
                .on(pubItemVO.getFiles(), new UriAsLocatorValidator());

        final ComplexResult resultStandard =
            vStandard.doValidate().result(ResultCollectors.toComplex());

        logger.info(resultStandard);

        Validation.checkResult(resultStandard);

        break;

      case EASY_SUBMISSION_STEP_3:
        final FluentValidator vEasy3 =
            FluentValidator.checkAll().failOver()
                .on(pubItemVO.getFiles(), new ComponentMimeTypesValidator())
                .on(pubItemVO.getFiles(), new ComponentContentRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentDataRequiredValidator())
                .on(pubItemVO.getFiles(), new FileDateFormatValidator())
                .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
                .on(pubItemVO.getFiles(), new NoSlashesInFileNameValidator())
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
                .on(pubItemVO.getFiles(), new UriAsLocatorValidator());

        final ComplexResult resultEasy3 = vEasy3.doValidate().result(ResultCollectors.toComplex());

        logger.info(resultEasy3);

        Validation.checkResult(resultEasy3);

        break;

      case EASY_SUBMISSION_STEP_4:
        final FluentValidator vEasy4 =
            FluentValidator
                .checkAll()
                .failOver()
                .on(pubItemVO.getFiles(), new ComponentMimeTypesValidator())
                .on(pubItemVO.getFiles(), new ComponentContentRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentDataRequiredValidator())
                .on(pubItemVO.getFiles(), new FileDateFormatValidator())
                .on(pubItemVO.getMetadata().getCreators(),
                    new CreatorWithOrganisationRequiredValidator())
                .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
                .on(pubItemVO.getFiles(), new NoSlashesInFileNameValidator())
                .on(pubItemVO.getMetadata().getCreators(), new CreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
                .on(pubItemVO.getFiles(), new UriAsLocatorValidator());

        final ComplexResult resultEasy4 = vEasy4.doValidate().result(ResultCollectors.toComplex());

        logger.info(resultEasy4);

        Validation.checkResult(resultEasy4);

        break;

      default:
        throw new ValidationServiceException("undefined validation for validation point:"
            + validationPoint);
    }
  }

  public static void validateYearbook(final ItemVO itemVO) throws ValidationServiceException,
      ValidationException {

    if (itemVO instanceof PubItemVO == false) {
      throw new ValidationServiceException("itemVO instanceof PubItemVO == false");
    }

    final PubItemVO pubItemVO = (PubItemVO) itemVO;

    GenreValidator.checkGenre(pubItemVO.getMetadata().getGenre());

    final FluentValidator vYearbook =
        FluentValidator
            .checkAll()
            .failOver()
            // Allgemein
            .on(pubItemVO.getMetadata(), new PublishingDateRequiredValidator())
            .when(
                !MdsPublicationVO.Genre.THESIS.equals(pubItemVO.getMetadata().getGenre())
                    && !MdsPublicationVO.Genre.JOURNAL.equals(pubItemVO.getMetadata().getGenre())
                    && !MdsPublicationVO.Genre.SERIES.equals(pubItemVO.getMetadata().getGenre()))
            // Artikel
            .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
            .when(isArticle(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SequenceInfomationValidator())
            .when(isArticle(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getCreators(),
                new CreatorWithOrganisationRequiredValidator())
            .when(isArticle(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getCreators(), new CreatorsRoleRequiredValidator())
            .when(isArticle(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourceTitlesRequiredValidator())
            .when(isArticle(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourceVolumesRequiredValidator())
            .when(isArticle(pubItemVO.getMetadata().getGenre()))
            // Book Chapter
            .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
            .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SequenceInfomationValidator())
            .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getCreators(),
                new CreatorWithOrganisationRequiredValidator())
            .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getCreators(), new CreatorsRoleRequiredValidator())
            .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourceTitlesRequiredValidator())
            .when(isBookChapter(pubItemVO.getMetadata().getGenre()))

    ;

    final ComplexResult result = vYearbook.doValidate().result(ResultCollectors.toComplex());

    logger.info(result);

    Validation.checkResult(result);
  }

  private static void checkResult(ComplexResult complexResult) throws ValidationException {
    final ValidationReportVO v = new ValidationReportVO();

    if (complexResult.isSuccess() == false) {
      for (final ValidationError error : complexResult.getErrors()) {
        final ValidationReportItemVO item =
            new ValidationReportItemVO(error.getErrorMsg(), ValidationReportItemVO.Severity.ERROR);
        item.setElement(error.getField());
        v.addItem(item);
      }

      throw new ValidationException(v);
    }
  }

  // ### Yearbook Section ########################################
  private static boolean isArticle(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.ARTICLE.equals(genre)
        || MdsPublicationVO.Genre.BOOK_REVIEW.equals(genre)
        || MdsPublicationVO.Genre.CASE_NOTE.equals(genre)
        || MdsPublicationVO.Genre.CASE_STUDY.equals(genre)
        || MdsPublicationVO.Genre.CONFERENCE_REPORT.equals(genre)
        || MdsPublicationVO.Genre.EDITORIAL.equals(genre)
        || MdsPublicationVO.Genre.NEWSPAPER_ARTICLE.equals(genre);
  }

  private static boolean isBookChapter(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.BOOK_ITEM.equals(genre)
        || MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION.equals(genre)
        || MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY.equals(genre)
        || MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK.equals(genre)
        || MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT.equals(genre);
  }

}
