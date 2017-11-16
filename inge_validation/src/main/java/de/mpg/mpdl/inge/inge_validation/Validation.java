package de.mpg.mpdl.inge.inge_validation;

import java.util.List;

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
import de.mpg.mpdl.inge.inge_validation.validator.ComponentsContentRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.ComponentsDataRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.ComponentsDateFormatValidator;
import de.mpg.mpdl.inge.inge_validation.validator.ComponentsNoSlashesInNameValidator;
import de.mpg.mpdl.inge.inge_validation.validator.ComponentsUriAsLocatorValidator;
import de.mpg.mpdl.inge.inge_validation.validator.CreatorsOrganizationsNameRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.CreatorsRoleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.CreatorsWithOrganisationRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.DateRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.EventTitleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.GenreRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.IdTypeRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.MdsPublicationDateFormatValidator;
import de.mpg.mpdl.inge.inge_validation.validator.SourceCreatorsRoleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.SourceRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.SourcesGenreRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.SourcesTitleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.TitleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.cone.ClassifiedKeywordsValidator;
import de.mpg.mpdl.inge.inge_validation.validator.cone.ComponentsMimeTypeValidator;
import de.mpg.mpdl.inge.inge_validation.validator.cone.LanguageCodeValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.CreatorsPersonNamesRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.CreatorsPersonRoleRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.DateAcceptedRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.EventTitleAndPlaceRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.GenreValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.CreatorsMaxPlanckAffiliationValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.PublishingDateRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourcesCreatorRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourcesCreatorsOrganizationNamesRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourcesCreatorsPersonNamesRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourcesCreatorsRoleValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourcesGenreJournalValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourcesGenreProceedingsOrJournalValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourcesGenreSeriesValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourcesPublisherAndPlaceRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourcesPublisherEditionRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourcesSequenceInfomationValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourcesTotalNumberOfPagesRequiredValidator;
import de.mpg.mpdl.inge.inge_validation.validator.yearbook.SourcesVolumeRequiredValidator;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

public class Validation {

  private static final Logger logger = Logger.getLogger(Validation.class);

  public Validation() {}

  public void validate(final ItemVO itemVO, ValidationPoint validationPoint)
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

        checkResult(resultSave);

        break;

      case SIMPLE:
        final FluentValidator vSimple =
            FluentValidator
                .checkAll()
                .failOver()
                .on(pubItemVO.getMetadata().getSubjects(), new ClassifiedKeywordsValidator())
                .on(pubItemVO.getFiles(), new ComponentsMimeTypeValidator())
                .on(pubItemVO.getMetadata().getLanguages(), new LanguageCodeValidator())
                .on(pubItemVO.getFiles(), new ComponentsContentRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsDataRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsDateFormatValidator())
                .on(pubItemVO.getMetadata().getCreators(),
                    new CreatorsWithOrganisationRequiredValidator())
                .on(pubItemVO.getMetadata().getEvent(), new EventTitleRequiredValidator())
                .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
                .on(pubItemVO.getMetadata().getIdentifiers(), new IdTypeRequiredValidator())
                .on(pubItemVO.getMetadata(), new MdsPublicationDateFormatValidator())
                .on(pubItemVO.getFiles(), new ComponentsNoSlashesInNameValidator())
                .on(pubItemVO.getMetadata().getCreators(),
                    new CreatorsOrganizationsNameRequiredValidator())
                .on(pubItemVO.getMetadata().getCreators(), new CreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourcesGenreRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourcesTitleRequiredValidator())
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsUriAsLocatorValidator());

        final ComplexResult resultSimple =
            vSimple.doValidate().result(ResultCollectors.toComplex());

        logger.info(resultSimple);

        checkResult(resultSimple);

        break;

      case STANDARD:
        final FluentValidator vStandard =
            FluentValidator
                .checkAll()
                .failOver()
                .on(pubItemVO.getMetadata().getSubjects(), new ClassifiedKeywordsValidator())
                .on(pubItemVO.getFiles(), new ComponentsMimeTypeValidator())
                .on(pubItemVO.getMetadata().getLanguages(), new LanguageCodeValidator())
                .on(pubItemVO.getFiles(), new ComponentsContentRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsDataRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsDateFormatValidator())
                .on(pubItemVO.getMetadata().getCreators(),
                    new CreatorsWithOrganisationRequiredValidator())
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
                .on(pubItemVO.getFiles(), new ComponentsNoSlashesInNameValidator())
                .on(pubItemVO.getMetadata().getCreators(),
                    new CreatorsOrganizationsNameRequiredValidator())
                .on(pubItemVO.getMetadata().getCreators(), new CreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourcesGenreRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceRequiredValidator())
                .when(
                    MdsPublicationVO.Genre.ARTICLE.equals(pubItemVO.getMetadata().getGenre())
                        || MdsPublicationVO.Genre.BOOK_ITEM.equals(pubItemVO.getMetadata()
                            .getGenre())
                        || MdsPublicationVO.Genre.CONFERENCE_PAPER.equals(pubItemVO.getMetadata()
                            .getGenre())
                        || MdsPublicationVO.Genre.MEETING_ABSTRACT.equals(pubItemVO.getMetadata()
                            .getGenre()))
                .on(pubItemVO.getMetadata().getSources(), new SourcesTitleRequiredValidator())
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsUriAsLocatorValidator());

        final ComplexResult resultStandard =
            vStandard.doValidate().result(ResultCollectors.toComplex());

        logger.info(resultStandard);

        checkResult(resultStandard);

        break;

      case EASY_SUBMISSION_STEP_3:
        final FluentValidator vEasy3 =
            FluentValidator.checkAll().failOver()
                .on(pubItemVO.getFiles(), new ComponentsMimeTypeValidator())
                .on(pubItemVO.getFiles(), new ComponentsContentRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsDataRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsDateFormatValidator())
                .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsNoSlashesInNameValidator())
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsUriAsLocatorValidator());

        final ComplexResult resultEasy3 = vEasy3.doValidate().result(ResultCollectors.toComplex());

        logger.info(resultEasy3);

        checkResult(resultEasy3);

        break;

      case EASY_SUBMISSION_STEP_4:
        final FluentValidator vEasy4 =
            FluentValidator
                .checkAll()
                .failOver()
                .on(pubItemVO.getFiles(), new ComponentsMimeTypeValidator())
                .on(pubItemVO.getFiles(), new ComponentsContentRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsDataRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsDateFormatValidator())
                .on(pubItemVO.getMetadata().getCreators(),
                    new CreatorsWithOrganisationRequiredValidator())
                .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsNoSlashesInNameValidator())
                .on(pubItemVO.getMetadata().getCreators(), new CreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator())
                .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
                .on(pubItemVO.getFiles(), new ComponentsUriAsLocatorValidator());

        final ComplexResult resultEasy4 = vEasy4.doValidate().result(ResultCollectors.toComplex());

        logger.info(resultEasy4);

        checkResult(resultEasy4);

        break;

      default:
        throw new ValidationServiceException("undefined validation for validation point:"
            + validationPoint);
    }
  }

  public void validateYearbook(final ItemVO itemVO, List<String> childsOfMPG)
      throws ValidationServiceException, ValidationException {

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
            .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
            .on(pubItemVO.getMetadata().getCreators(),
                new CreatorsMaxPlanckAffiliationValidator(childsOfMPG))
            .on(pubItemVO.getMetadata().getCreators(), new CreatorsPersonNamesRequiredValidator())
            .on(pubItemVO.getMetadata().getCreators(), new CreatorsPersonRoleRequiredValidator())
            .on(pubItemVO.getMetadata(), new PublishingDateRequiredValidator())
            .when(!MdsPublicationVO.Genre.THESIS.equals(pubItemVO.getMetadata().getGenre()))

            // Artikel
            .on(pubItemVO.getMetadata().getSources(), new SourcesSequenceInfomationValidator())
            .when(isArticle(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourcesTitleRequiredValidator())
            .when(isArticle(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourcesVolumeRequiredValidator())
            .when(isArticle(pubItemVO.getMetadata().getGenre()))

            // Book Chapter
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesCreatorsOrganizationNamesRequiredValidator())
            .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesCreatorsPersonNamesRequiredValidator())
            .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourcesCreatorRequiredValidator())
            .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourcesCreatorsRoleValidator())
            .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesPublisherAndPlaceRequiredValidator())
            .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourcesSequenceInfomationValidator())
            .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourcesTitleRequiredValidator())
            .when(isBookChapter(pubItemVO.getMetadata().getGenre()))

            // Conference Paper
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesGenreProceedingsOrJournalValidator())
            .when(isConferencePaper(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourcesSequenceInfomationValidator())
            .when(isConferencePaper(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourcesTitleRequiredValidator())
            .when(isConferencePaper(pubItemVO.getMetadata().getGenre()))

            // Proceedings
            .on(pubItemVO.getMetadata().getEvent(), new EventTitleAndPlaceRequiredValidator())
            .when(isProceedings(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesPublisherAndPlaceRequiredValidator())
            .when(isProceedings(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesTotalNumberOfPagesRequiredValidator())
            .when(isProceedings(pubItemVO.getMetadata().getGenre()))

            // Book
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesPublisherAndPlaceRequiredValidator())
            .when(isBook(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesTotalNumberOfPagesRequiredValidator())
            .when(isBook(pubItemVO.getMetadata().getGenre()))

            // Thesis
            .on(pubItemVO.getMetadata(), new DateAcceptedRequiredValidator())
            .when(isThesis(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesPublisherAndPlaceRequiredValidator())
            .when(isThesis(pubItemVO.getMetadata().getGenre()))

            // Issue
            .on(pubItemVO.getMetadata().getSources(), new SourcesGenreJournalValidator())
            .when(isIssue(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourcesTitleRequiredValidator())
            .when(isIssue(pubItemVO.getMetadata().getGenre()))

            // Journal
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesPublisherAndPlaceRequiredValidator())
            .when(isJournal(pubItemVO.getMetadata().getGenre()))

            // Series
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesPublisherAndPlaceRequiredValidator())
            .when(isSeries(pubItemVO.getMetadata().getGenre()))

            // Paper
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesTotalNumberOfPagesRequiredValidator())
            .when(isPaper(pubItemVO.getMetadata().getGenre()))

            // Report
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesPublisherEditionRequiredValidator())
            .when(isReport(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourcesGenreSeriesValidator())
            .when(isReport(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(), new SourcesTitleRequiredValidator())
            .when(isReport(pubItemVO.getMetadata().getGenre()))
            .on(pubItemVO.getMetadata().getSources(),
                new SourcesTotalNumberOfPagesRequiredValidator())
            .when(isReport(pubItemVO.getMetadata().getGenre()));

    final ComplexResult result = vYearbook.doValidate().result(ResultCollectors.toComplex());

    logger.info(result);

    checkResult(result);
  }

  private void checkResult(ComplexResult complexResult) throws ValidationException {
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
  private boolean isArticle(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.ARTICLE.equals(genre)
        || MdsPublicationVO.Genre.BOOK_REVIEW.equals(genre)
        || MdsPublicationVO.Genre.CASE_NOTE.equals(genre)
        || MdsPublicationVO.Genre.CASE_STUDY.equals(genre)
        || MdsPublicationVO.Genre.CONFERENCE_REPORT.equals(genre)
        || MdsPublicationVO.Genre.EDITORIAL.equals(genre)
        || MdsPublicationVO.Genre.NEWSPAPER_ARTICLE.equals(genre);
  }

  private boolean isBookChapter(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.BOOK_ITEM.equals(genre)
        || MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION.equals(genre)
        || MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY.equals(genre)
        || MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK.equals(genre)
        || MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT.equals(genre);
  }

  private boolean isConferencePaper(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.CONFERENCE_PAPER.equals(genre)
        || MdsPublicationVO.Genre.MEETING_ABSTRACT.equals(genre);
  }

  private boolean isProceedings(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.PROCEEDINGS.equals(genre);
  }

  private boolean isBook(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.BOOK.equals(genre)
        || MdsPublicationVO.Genre.COLLECTED_EDITION.equals(genre)
        || MdsPublicationVO.Genre.COMMENTARY.equals(genre)
        || MdsPublicationVO.Genre.FESTSCHRIFT.equals(genre)
        || MdsPublicationVO.Genre.HANDBOOK.equals(genre)
        || MdsPublicationVO.Genre.MONOGRAPH.equals(genre);
  }

  private boolean isThesis(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.THESIS.equals(genre);
  }

  private boolean isIssue(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.ISSUE.equals(genre);
  }

  private boolean isJournal(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.JOURNAL.equals(genre);
  }

  private boolean isSeries(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.SERIES.equals(genre);
  }

  private boolean isPaper(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.PAPER.equals(genre)
        || MdsPublicationVO.Genre.OPINION.equals(genre);
  }

  private boolean isReport(MdsPublicationVO.Genre genre) {
    return MdsPublicationVO.Genre.REPORT.equals(genre);
  }

}
