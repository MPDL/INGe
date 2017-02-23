package de.mpg.mpdl.inge.inge_validation;

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

public class ValidationService {

  public ValidationService() {}

  // public void doValidation(final ItemVO itemVO) throws ValidationException, ItemInvalidException
  // {
  // this.doValidation(itemVO, ValidationPoint.SIMPLE);
  // }

  // public void doValidation(final ItemVO itemVO, String vp)
  // throws ValidationException, ItemInvalidException {
  // ValidationPoint validationPoint = ValidationPoint.valueOf(vp);
  //
  // if (validationPoint == null) {
  // throw new ValidationException("unknown validation point");
  // }
  //
  // this.doValidation(itemVO, validationPoint);
  // }

  public void doValidation(final ItemVO itemVO, ValidationPoint validationPoint)
      throws ValidationException, ItemInvalidException {

    if (itemVO instanceof PubItemVO == false) {
      throw new ValidationException("itemVO instanceof PubItemVO == false");
    }

    PubItemVO pubItemVO = (PubItemVO) itemVO;

    switch (validationPoint) {

      case SAVE:
        FluentValidator vSave =
            FluentValidator.checkAll().failOver()
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator());

        ComplexResult resultSave =
            vSave.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

        System.out.println(resultSave);

        this.checkResult(resultSave);

        break;

      case SIMPLE:
        FluentValidator vSimple =
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

        ComplexResult resultSimple =
            vSimple.doValidate().result(
                com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

        System.out.println(resultSimple);

        this.checkResult(resultSimple);

        break;

      case STANDARD:
        FluentValidator vStandard =
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

        ComplexResult resultStandard =
            vStandard.doValidate().result(
                com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

        System.out.println(resultStandard);

        this.checkResult(resultStandard);

        break;

      case EASY_SUBMISSION_STEP_3:
        FluentValidator vEasy3 =
            FluentValidator.checkAll().failOver()
                .on(pubItemVO.getFiles(), new ComponentMimeTypesValidator())
                .on(pubItemVO.getFiles(), new ComponentContentRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentDataRequiredValidator())
                .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
                .on(pubItemVO.getFiles(), new NoSlashesInFileNameValidator())
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
                .on(pubItemVO.getFiles(), new UriAsLocatorValidator());

        ComplexResult resultEasy3 =
            vEasy3.doValidate()
                .result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

        System.out.println(resultEasy3);

        this.checkResult(resultEasy3);

        break;

      case EASY_SUBMISSION_STEP_4:
        FluentValidator vEasy4 =
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

        ComplexResult resultEasy4 =
            vEasy4.doValidate()
                .result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());

        System.out.println(resultEasy4);

        this.checkResult(resultEasy4);

        break;

      default:
        throw new ValidationException("undefined validation for validation point:"
            + validationPoint);
    }
  }

  private void checkResult(ComplexResult complexResult) throws ItemInvalidException {
    ValidationReportVO v = new ValidationReportVO();

    if (complexResult.isSuccess() == false) {
      for (ValidationError error : complexResult.getErrors()) {
        ValidationReportItemVO item = new ValidationReportItemVO();
        item.setContent(error.getErrorMsg());
        item.setElement(error.getField());
        v.addItem(item);
      }

      throw new ItemInvalidException(v);
    }
  }

}
