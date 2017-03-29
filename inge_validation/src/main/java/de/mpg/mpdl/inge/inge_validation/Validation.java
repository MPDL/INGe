package de.mpg.mpdl.inge.inge_validation;

import org.apache.log4j.Logger;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ValidationError;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
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
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

public class Validation {
  private static final Logger LOG = Logger.getLogger(Validation.class);

  public static void doValidation(final ItemVO itemVO, ValidationPoint validationPoint)
      throws ValidationException, ItemInvalidException {

    if (itemVO instanceof PubItemVO == false) {
      throw new ValidationException("itemVO instanceof PubItemVO == false");
    }

    final PubItemVO pubItemVO = (PubItemVO) itemVO;

    switch (validationPoint) {

      case SAVE:
        final FluentValidator vSave =
            FluentValidator.checkAll().failOver()
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator());

        final ComplexResult resultSave =
            vSave.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

        Validation.LOG.info(resultSave);

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
                .on(pubItemVO.getMetadata().getCreators(), new CreatorRequiredValidator())
                .on(pubItemVO.getMetadata().getEvent(), new EventTitleRequiredValidator())
                .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
                .on(pubItemVO.getMetadata().getIdentifiers(), new IdTypeRequiredValidator())
                .on(pubItemVO.getMetadata(), new MdsPublicationDateFormatValidator())
                .on(pubItemVO.getFiles(), new NoSlashesInFileNameValidator())
                .on(pubItemVO.getMetadata().getCreators(), new OrganizationNameRequiredValidator())
                .on(pubItemVO.getMetadata().getCreators(),
                    new PublicationCreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceGenresRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceTitlesRequiredValidator())
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
                .on(pubItemVO.getFiles(), new UriAsLocatorValidator());

        final ComplexResult resultSimple =
            vSimple.doValidate().result(
                com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

        Validation.LOG.info(resultSimple);

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
                .on(pubItemVO.getMetadata().getCreators(), new CreatorRequiredValidator())
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
                .on(pubItemVO.getMetadata().getCreators(),
                    new PublicationCreatorsRoleRequiredValidator())
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
            vStandard.doValidate().result(
                com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

        Validation.LOG.info(resultStandard);

        Validation.checkResult(resultStandard);

        break;

      case EASY_SUBMISSION_STEP_3:
        final FluentValidator vEasy3 =
            FluentValidator.checkAll().failOver()
                .on(pubItemVO.getFiles(), new ComponentMimeTypesValidator())
                .on(pubItemVO.getFiles(), new ComponentContentRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentDataRequiredValidator())
                .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
                .on(pubItemVO.getFiles(), new NoSlashesInFileNameValidator())
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
                .on(pubItemVO.getFiles(), new UriAsLocatorValidator());

        final ComplexResult resultEasy3 =
            vEasy3.doValidate()
                .result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

        Validation.LOG.info(resultEasy3);

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
                .on(pubItemVO.getMetadata().getCreators(), new CreatorRequiredValidator())
                .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
                .on(pubItemVO.getFiles(), new NoSlashesInFileNameValidator())
                .on(pubItemVO.getMetadata().getCreators(),
                    new PublicationCreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
                .on(pubItemVO.getFiles(), new UriAsLocatorValidator());

        final ComplexResult resultEasy4 =
            vEasy4.doValidate()
                .result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

        Validation.LOG.info(resultEasy4);

        Validation.checkResult(resultEasy4);

        break;

      default:
        throw new ValidationException("undefined validation for validation point:"
            + validationPoint);
    }
  }

  private static void checkResult(ComplexResult complexResult) throws ItemInvalidException {
    final ValidationReportVO v = new ValidationReportVO();

    if (complexResult.isSuccess() == false) {
      for (final ValidationError error : complexResult.getErrors()) {
        final ValidationReportItemVO item = new ValidationReportItemVO();
        item.setContent(error.getErrorMsg());
        item.setElement(error.getField());
        v.addItem(item);
      }

      throw new ItemInvalidException(v);
    }
  }
}
