package de.mpg.mpdl.inge.inge_validation;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.Result;

import de.mpg.mpdl.inge.inge_validation.util.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.inge_validation.validator.ClassifiedKeywordsValidator;
import de.mpg.mpdl.inge.inge_validation.validator.ComponentContentRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.ComponentDataRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.ComponentMimeTypesValidator;
import de.mpg.mpdl.inge.inge_validation.validator.CreatorRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.DateRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.EventTitleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.GenreRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.IdTypeRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.LanguageCodeValidator;
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
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.ValidationReportVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

public class ValidationService {

  public ValidationService() {}

  public ValidationReportVO doValidation(final ItemVO itemVO) throws ValidationException {
    return doValidation(itemVO, ValidationPoint.DEFAULT);
  }

  public ValidationReportVO doValidation(final ItemVO itemVO, String vp) throws ValidationException {
    ValidationPoint validationPoint = ValidationPoint.valueOf(vp);
    
    if (validationPoint == null) {
      throw new ValidationException("unknown validation point");
    }
    
    return doValidation(itemVO, validationPoint);
  }
  
  public ValidationReportVO doValidation(final ItemVO itemVO, ValidationPoint validationPoint) throws ValidationException {
    if (itemVO instanceof PubItemVO == false) {
      throw new ValidationException("itemVO instanceof PubItemVO == false");
    }
    
    PubItemVO pubItemVO = (PubItemVO) itemVO;
    
    switch (validationPoint) {
      
      case DEFAULT:
        FluentValidator v = FluentValidator.checkAll()
            .on(pubItemVO.getFiles(), new ComponentContentRequiredValidator())
            .on(pubItemVO.getFiles(), new ComponentDataRequiredValidator())
            .on(pubItemVO.getFiles(), new ComponentMimeTypesValidator())
            .on(pubItemVO.getFiles(), new NoSlashesInFileNameValidator())
            .on(pubItemVO.getFiles(), new UriAsLocatorValidator())
            .on(pubItemVO.getMetadata(), new DateRequiredValidator())
            .when(!MdsPublicationVO.Genre.SERIES.equals(pubItemVO.getMetadata().getGenre())
                && !MdsPublicationVO.Genre.JOURNAL.equals(pubItemVO.getMetadata().getGenre())
                && !MdsPublicationVO.Genre.MANUSCRIPT.equals(pubItemVO.getMetadata().getGenre())
                && !MdsPublicationVO.Genre.OTHER.equals(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata(), new MdsPublicationDateFormatValidator())
            .on(pubItemVO.getMetadata().getCreators(), new CreatorRequiredValidator())
            .on(pubItemVO.getMetadata().getCreators(), new OrganizationNameRequiredValidator())
            .on(pubItemVO.getMetadata().getCreators(), new PublicationCreatorsRoleRequiredValidator())
            .on(pubItemVO.getMetadata().getEvent(), new EventTitleRequiredValidator())
            .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
            .on(pubItemVO.getMetadata().getIdentifiers(), new IdTypeRequiredValidator())
            .on(pubItemVO.getMetadata().getLanguages(), new LanguageCodeValidator())
            .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator())
            .on(pubItemVO.getMetadata().getSources(), new SourceGenresRequiredValidator())
            .on(pubItemVO.getMetadata().getSources(), new SourceRequiredValidator())
            .when(MdsPublicationVO.Genre.ARTICLE.equals(pubItemVO.getMetadata().getGenre())
                || MdsPublicationVO.Genre.BOOK_ITEM.equals(pubItemVO.getMetadata().getGenre())
                || MdsPublicationVO.Genre.CONFERENCE_PAPER.equals(pubItemVO.getMetadata().getGenre())
                || MdsPublicationVO.Genre.MEETING_ABSTRACT.equals(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourceTitlesRequiredValidator())
            .on(pubItemVO.getMetadata().getSubjects(), new ClassifiedKeywordsValidator())
            .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator());

        Result result =
            v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toSimple());
        
        System.out.println(result);

        ComplexResult complexResult =
            v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());
        
        System.out.println(complexResult);
        
        return convert(complexResult);
        
      case ACCEPT_ITEM:
        return null;
        
      case SUBMIT_ITEM:
        return null;
        
      default:
        throw new ValidationException("undefined validation for validation point:" + validationPoint);
    }
  }

  private ValidationReportVO convert(ComplexResult complexResult) {
    return new ValidationReportVO();
  }
  
  public static void main(String[] args) {
    System.out.println("Start");

    PubItemVO pubItemVO = new PubItemVO();
    MdsPublicationVO mdsPublicationVO = new MdsPublicationVO();
    pubItemVO.setMetadata(mdsPublicationVO);

    FluentValidator v = FluentValidator.checkAll()

        .on(pubItemVO.getFiles(), new ComponentContentRequiredValidator())
        .on(pubItemVO.getFiles(), new ComponentDataRequiredValidator())
        .on(pubItemVO.getFiles(), new ComponentMimeTypesValidator())
        .on(pubItemVO.getFiles(), new NoSlashesInFileNameValidator())
        .on(pubItemVO.getFiles(), new UriAsLocatorValidator())
        .on(pubItemVO.getMetadata(), new DateRequiredValidator())
        .when(!MdsPublicationVO.Genre.SERIES.equals(pubItemVO.getMetadata().getGenre())
            && !MdsPublicationVO.Genre.JOURNAL.equals(pubItemVO.getMetadata().getGenre())
            && !MdsPublicationVO.Genre.MANUSCRIPT.equals(pubItemVO.getMetadata().getGenre())
            && !MdsPublicationVO.Genre.OTHER.equals(pubItemVO.getMetadata().getGenre()))
        .on(pubItemVO.getMetadata(), new MdsPublicationDateFormatValidator())
        .on(pubItemVO.getMetadata().getCreators(), new CreatorRequiredValidator())
        .on(pubItemVO.getMetadata().getCreators(), new OrganizationNameRequiredValidator())
        .on(pubItemVO.getMetadata().getCreators(), new PublicationCreatorsRoleRequiredValidator())
        .on(pubItemVO.getMetadata().getEvent(), new EventTitleRequiredValidator())
        .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
        .on(pubItemVO.getMetadata().getIdentifiers(), new IdTypeRequiredValidator())
        .on(pubItemVO.getMetadata().getLanguages(), new LanguageCodeValidator())
        .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator())
        .on(pubItemVO.getMetadata().getSources(), new SourceGenresRequiredValidator())
        .on(pubItemVO.getMetadata().getSources(), new SourceRequiredValidator())
        .when(MdsPublicationVO.Genre.ARTICLE.equals(pubItemVO.getMetadata().getGenre())
            || MdsPublicationVO.Genre.BOOK_ITEM.equals(pubItemVO.getMetadata().getGenre())
            || MdsPublicationVO.Genre.CONFERENCE_PAPER.equals(pubItemVO.getMetadata().getGenre())
            || MdsPublicationVO.Genre.MEETING_ABSTRACT.equals(pubItemVO.getMetadata().getGenre()))
        .on(pubItemVO.getMetadata().getSources(), new SourceTitlesRequiredValidator())
        .on(pubItemVO.getMetadata().getSubjects(), new ClassifiedKeywordsValidator())
        .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator());

    Result result =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toSimple());
    System.out.println(result);

    ComplexResult complexResult =
        v.doValidate().result(com.baidu.unbiz.fluentvalidator.ResultCollectors.toComplex());
    System.out.println(complexResult);

    System.out.println("Ende");
  }

}
