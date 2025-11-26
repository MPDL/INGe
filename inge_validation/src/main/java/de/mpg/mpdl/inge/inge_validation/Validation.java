package de.mpg.mpdl.inge.inge_validation;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;
import com.baidu.unbiz.fluentvalidator.ValidationError;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationServiceException;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.inge_validation.validator.*;
import de.mpg.mpdl.inge.inge_validation.validator.cone.ClassifiedKeywordsValidator;
import de.mpg.mpdl.inge.inge_validation.validator.cone.LanguageCodeValidator;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Validation {

  private static final Logger logger = LogManager.getLogger(Validation.class);

  public Validation() {}

  public static ValidationReportVO getValidationReportVO(ComplexResult complexResult) {

    ValidationReportVO v = new ValidationReportVO();

    if (!complexResult.isSuccess()) {
      for (ValidationError error : complexResult.getErrors()) {
        ValidationReportItemVO item = new ValidationReportItemVO(error.getErrorMsg(),
            (ErrorMessages.WARNING == error.getErrorCode() ? ValidationReportItemVO.Severity.WARNING
                : ValidationReportItemVO.Severity.ERROR));
        item.setElement(error.getField());
        v.addItem(item);
      }
    }

    return v;
  }

  //  public void validateYearbook(final ItemVersionVO pubItemVO, List<String> childsOfMPG)
  //      throws ValidationServiceException, ValidationException {
  //
  //    if (pubItemVO instanceof ItemVersionVO == false) {
  //      throw new ValidationServiceException("itemVO instanceof PubItemVO == false");
  //    }
  //
  //
  //    GenreValidator.checkGenre(pubItemVO.getMetadata().getGenre());
  //
  //    final FluentValidator vYearbook = FluentValidator.checkAll().failOver()
  //        // Allgemein
  //        .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator())
  //        .on(pubItemVO.getMetadata().getTitle(), new Utf8TitleValidator()) //
  //        .on(pubItemVO.getMetadata().getAlternativeTitles(), new Utf8AlternativeTitleValidator()) //
  //        .on(pubItemVO.getMetadata().getAbstracts(), new Utf8AbstractValidator()) //
  //        .on(pubItemVO.getMetadata().getCreators(), new CreatorsMaxPlanckAffiliationValidator(childsOfMPG))
  //        .on(pubItemVO.getMetadata().getCreators(), new CreatorsPersonNamesRequiredValidator())
  //        .on(pubItemVO.getMetadata().getCreators(), new CreatorsPersonRoleRequiredValidator())
  //        .on(pubItemVO.getMetadata(), new PublishingDateRequiredValidator())
  //        .when(!MdsPublicationVO.Genre.THESIS.equals(pubItemVO.getMetadata().getGenre()))
  //
  //        // Book
  //        .on(pubItemVO.getMetadata().getPublishingInfo(), new PublisherAndPlaceRequiredValidator())
  //        .when(isBook(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getTotalNumberOfPages(), new TotalNumberOfPagesRequiredValidator())
  //        .when(isBook(pubItemVO.getMetadata().getGenre()))
  //
  //        // Book Chapter
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesCreatorsOrganizationNamesRequiredValidator())
  //        .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesCreatorsPersonNamesRequiredValidator())
  //        .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesCreatorRequiredValidator())
  //        .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesCreatorsRoleValidator())
  //        .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesPublisherAndPlaceRequiredValidator())
  //        .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesSequenceInfomationValidator())
  //        .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesTitleRequiredValidator())
  //        .when(isBookChapter(pubItemVO.getMetadata().getGenre()))
  //
  //        // Conference Paper
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesGenreProceedingsOrJournalValidator())
  //        .when(isConferencePaper(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesSequenceInfomationValidator())
  //        .when(isConferencePaper(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesTitleRequiredValidator())
  //        .when(isConferencePaper(pubItemVO.getMetadata().getGenre()))
  //
  //        // Conference Report
  //        .on(pubItemVO.getMetadata().getEvent(), new EventTitleAndPlaceRequiredValidator())
  //        .when(isConferenceReport(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getEvent(), new EventDatesRequiredValidator())
  //        .when(isConferenceReport(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesGenreSeriesOrJournalValidator())
  //        .when(isConferenceReport(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesTitleRequiredValidator())
  //        .when(isConferenceReport(pubItemVO.getMetadata().getGenre()))
  //
  //        // Issue
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesGenreJournalValidator()) //
  //        .when(isIssue(pubItemVO.getMetadata().getGenre())) //
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesTitleRequiredValidator()) //
  //        .when(isIssue(pubItemVO.getMetadata().getGenre()))
  //
  //        // Journal
  //        .on(pubItemVO.getMetadata().getPublishingInfo(), new PublisherAndPlaceRequiredValidator())
  //        .when(isJournal(pubItemVO.getMetadata().getGenre()))
  //
  //        // Journal-Article
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesGenreJournalValidator()) //
  //        .when(isJournalArticle(pubItemVO.getMetadata().getGenre())) //
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesSequenceInfomationValidator())
  //        .when(isJournalArticle(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getSources(), new SourcesTitleRequiredValidator())
  //        .when(isJournalArticle(pubItemVO.getMetadata().getGenre())).on(pubItemVO.getMetadata().getSources())
  //
  //        // Proceedings
  //        .on(pubItemVO.getMetadata().getEvent(), new EventTitleAndPlaceRequiredValidator())
  //        .when(isProceedings(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getPublishingInfo(), new PublisherAndPlaceRequiredValidator())
  //        .when(isProceedings(pubItemVO.getMetadata().getGenre()))
  //        .on(pubItemVO.getMetadata().getTotalNumberOfPages(), new TotalNumberOfPagesRequiredValidator())
  //        .when(isProceedings(pubItemVO.getMetadata().getGenre()))
  //
  //        // Thesis
  //        .on(pubItemVO.getMetadata(), new DateAcceptedRequiredValidator()) //
  //        .when(isThesis(pubItemVO.getMetadata().getGenre())) //
  //        .on(pubItemVO.getMetadata().getPublishingInfo(), new PublisherAndPlaceRequiredValidator())
  //        .when(isThesis(pubItemVO.getMetadata().getGenre()))
  //
  //        // Paper
  //        .on(pubItemVO.getMetadata().getTotalNumberOfPages(), new TotalNumberOfPagesRequiredValidator())
  //        .when(isPaper(pubItemVO.getMetadata().getGenre()))
  //
  //        // Series
  //        .on(pubItemVO.getMetadata().getPublishingInfo(), new PublisherAndPlaceRequiredValidator())
  //        .when(isSeries(pubItemVO.getMetadata().getGenre()));
  //
  //    final ComplexResult result = vYearbook.doValidate().result(ResultCollectors.toComplex());
  //
  //    //    logger.info(result);
  //
  //    checkResult(result);
  //  }

  public void validate(ItemVersionVO pubItemVO, ValidationPoint validationPoint) throws ValidationServiceException, ValidationException {

    if (!(pubItemVO instanceof ItemVersionVO)) {
      throw new ValidationServiceException("itemVO instanceof PubItemVO == false");
    }


    switch (validationPoint) {

      case SAVE:
        FluentValidator vSave = FluentValidator.checkAll().failOver() //
            .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getTitle(), new Utf8TitleValidator()) //
            .on(pubItemVO.getMetadata().getAlternativeTitles(), new AlternativeTitleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getAlternativeTitles(), new Utf8AlternativeTitleValidator()) //
            .on(pubItemVO.getMetadata().getAbstracts(), new Utf8AbstractValidator()) //
            .on(pubItemVO.getMetadata(), new MdsPublicationDateFormatValidator()) //
            .on(pubItemVO.getFiles(), new ComponentsDateFormatValidator());

        ComplexResult resultSave = vSave.doValidate().result(ResultCollectors.toComplex());

        //        logger.info(resultSave);

        checkResult(resultSave);

        break;

      case SIMPLE:
        FluentValidator vSimple = FluentValidator.checkAll().failOver() //
            .on(pubItemVO.getMetadata().getSubjects(), new ClassifiedKeywordsValidator()) //
            .on(pubItemVO.getMetadata().getLanguages(), new LanguageCodeValidator()) //
            .on(pubItemVO.getFiles(), new ComponentsDataRequiredValidator()) //
            .on(pubItemVO.getFiles(), new ComponentsDateFormatValidator()) //
            .on(pubItemVO.getFiles(), new ComponentsIpRangeRequiredValidator()) //
            .on(pubItemVO.getMetadata().getCreators(), new CreatorsWithOrganisationRequiredValidator()) //
            .on(pubItemVO.getMetadata().getEvent(), new EventTitleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getIdentifiers(), new IdTypeRequiredAndFormatValidator()) //
            .on(pubItemVO.getMetadata(), new MdsPublicationGenreRequiredValidator()) //
            .on(pubItemVO.getMetadata(), new MdsPublicationDateFormatValidator()) //
            .on(pubItemVO.getFiles(), new ComponentsNoSlashesInNameValidator()) //
            .on(pubItemVO.getMetadata().getCreators(), new CreatorsOrganizationsNameRequiredValidator()) //
            .on(pubItemVO.getMetadata().getCreators(), new CreatorsRoleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getCreators(), new CreatorsOrcidValidator()) //
            .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsOrcidValidator()) //
            .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsNameRequiredValidator()) //
            .on(pubItemVO.getMetadata().getSources(), new SourceGenreRequiredValidator()) //
            .on(pubItemVO.getMetadata().getSources(), new SourceTitleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getTitle(), new Utf8TitleValidator()) //
            .on(pubItemVO.getMetadata().getAlternativeTitles(), new AlternativeTitleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getAlternativeTitles(), new Utf8AlternativeTitleValidator()) //
            .on(pubItemVO.getMetadata().getAbstracts(), new Utf8AbstractValidator()) //
            .on(pubItemVO.getFiles(), new ComponentsUriAsLocatorValidator());

        ComplexResult resultSimple = vSimple.doValidate().result(ResultCollectors.toComplex());

        //        logger.info(resultSimple);

        checkResult(resultSimple);

        break;

      case STANDARD:
        FluentValidator vStandard = FluentValidator.checkAll().failOver() //
            .on(pubItemVO.getFiles(), new ComponentsDataRequiredValidator()) //
            .on(pubItemVO.getFiles(), new ComponentsDateFormatValidator()) //
            .on(pubItemVO.getFiles(), new ComponentsIpRangeRequiredValidator()) //
            .on(pubItemVO.getFiles(), new ComponentsNoSlashesInNameValidator()) //
            .on(pubItemVO.getFiles(), new ComponentsUriAsLocatorValidator()) //
            .on(pubItemVO.getMetadata(), new MdsPublicationGenreRequiredValidator()) //
            .on(pubItemVO.getMetadata(), new MdsPublicationDateFormatValidator()) //
            .on(pubItemVO.getMetadata(), new MdsPublikationDateRequiredValidator()) //
            .on(pubItemVO.getMetadata().getAbstracts(), new Utf8AbstractValidator()) //
            .on(pubItemVO.getMetadata().getAlternativeTitles(), new AlternativeTitleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getAlternativeTitles(), new Utf8AlternativeTitleValidator()) //
            .on(pubItemVO.getMetadata().getCreators(), new CreatorsOrcidValidator()) //
            .on(pubItemVO.getMetadata().getCreators(), new CreatorsOrganizationsNameRequiredValidator()) //
            .on(pubItemVO.getMetadata().getCreators(), new CreatorsRoleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getCreators(), new CreatorsWithOrganisationRequiredValidator()) //
            .on(pubItemVO.getMetadata().getEvent(), new EventTitleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getIdentifiers(), new IdTypeRequiredAndFormatValidator()) //
            .on(pubItemVO.getMetadata().getLanguages(), new LanguageCodeValidator()) //
            .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsNameRequiredValidator()) //
            .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsOrcidValidator()) //
            .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getSources(), new SourceGenreRequiredValidator()) //
            .on(pubItemVO.getMetadata().getSources(), new SourceRequiredValidator()) //
            .when(MdsPublicationVO.Genre.ARTICLE.equals(pubItemVO.getMetadata().getGenre()) //
                || MdsPublicationVO.Genre.BOOK_ITEM.equals(pubItemVO.getMetadata().getGenre()) //
                || MdsPublicationVO.Genre.CONFERENCE_PAPER.equals(pubItemVO.getMetadata().getGenre()) //
                || MdsPublicationVO.Genre.MAGAZINE_ARTICLE.equals(pubItemVO.getMetadata().getGenre()) //
                || MdsPublicationVO.Genre.MEETING_ABSTRACT.equals(pubItemVO.getMetadata().getGenre()) //
                || MdsPublicationVO.Genre.REVIEW_ARTICLE.equals(pubItemVO.getMetadata().getGenre()) //
                || MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION.equals(pubItemVO.getMetadata().getGenre()) //
                || MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY.equals(pubItemVO.getMetadata().getGenre()) //
                || MdsPublicationVO.Genre.CONTRIBUTION_TO_ENCYCLOPEDIA.equals(pubItemVO.getMetadata().getGenre()) //
                || MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT.equals(pubItemVO.getMetadata().getGenre()) //
                || MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK.equals(pubItemVO.getMetadata().getGenre()) //
                || MdsPublicationVO.Genre.NEWSPAPER_ARTICLE.equals(pubItemVO.getMetadata().getGenre())) //
            .on(pubItemVO.getMetadata().getSources(), new SourceTitleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getSubjects(), new ClassifiedKeywordsValidator()) //
            .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator()) //
            .on(pubItemVO.getMetadata().getTitle(), new Utf8TitleValidator());

        ComplexResult resultStandard = vStandard.doValidate().result(ResultCollectors.toComplex());

        //        logger.info(resultStandard);

        checkResult(resultStandard);

        break;

      //      case EASY_SUBMISSION_STEP_3:
      //        FluentValidator vEasy3 = FluentValidator.checkAll().failOver() //
      //            .on(pubItemVO.getFiles(), new ComponentsDataRequiredValidator()) //
      //            .on(pubItemVO.getFiles(), new ComponentsDateFormatValidator()) //
      //            .on(pubItemVO.getFiles(), new ComponentsIpRangeRequiredValidator()) //
      //            .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator()) //
      //            .on(pubItemVO.getFiles(), new ComponentsNoSlashesInNameValidator()) //
      //            .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator()) //
      //            .on(pubItemVO.getMetadata().getTitle(), new Utf8TitleValidator()) //
      //            .on(pubItemVO.getMetadata().getAlternativeTitles(), new Utf8AlternativeTitleValidator()) //
      //            .on(pubItemVO.getMetadata().getAbstracts(), new Utf8AbstractValidator()) //
      //            .on(pubItemVO.getFiles(), new ComponentsUriAsLocatorValidator());
      //
      //        ComplexResult resultEasy3 = vEasy3.doValidate().result(ResultCollectors.toComplex());
      //
      //        //        logger.info(resultEasy3);
      //
      //        checkResult(resultEasy3);
      //
      //        break;
      //
      //      case EASY_SUBMISSION_STEP_4:
      //        FluentValidator vEasy4 = FluentValidator.checkAll().failOver() //
      //            .on(pubItemVO.getFiles(), new ComponentsDataRequiredValidator()) //
      //            .on(pubItemVO.getFiles(), new ComponentsDateFormatValidator()) //
      //            .on(pubItemVO.getFiles(), new ComponentsIpRangeRequiredValidator()) //
      //            .on(pubItemVO.getMetadata().getCreators(), new CreatorsWithOrganisationRequiredValidator()) //
      //            .on(pubItemVO.getMetadata().getGenre(), new GenreRequiredValidator()) //
      //            .on(pubItemVO.getFiles(), new ComponentsNoSlashesInNameValidator()) //
      //            .on(pubItemVO.getMetadata().getCreators(), new CreatorsRoleRequiredValidator()) //
      //            .on(pubItemVO.getMetadata().getCreators(), new CreatorsOrcidValidator()) //
      //            .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsOrcidValidator()) //
      //            .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsRoleRequiredValidator()) //
      //            .on(pubItemVO.getMetadata().getSources(), new SourceCreatorsNameRequiredValidator()) //
      //            .on(pubItemVO.getMetadata().getTitle(), new TitleRequiredValidator()) //
      //            .on(pubItemVO.getMetadata().getTitle(), new Utf8TitleValidator()) //
      //            .on(pubItemVO.getMetadata().getAlternativeTitles(), new Utf8AlternativeTitleValidator()) //
      //            .on(pubItemVO.getMetadata().getAbstracts(), new Utf8AbstractValidator()) //
      //            .on(pubItemVO.getFiles(), new ComponentsUriAsLocatorValidator());
      //
      //        ComplexResult resultEasy4 = vEasy4.doValidate().result(ResultCollectors.toComplex());
      //
      //        //        logger.info(resultEasy4);
      //
      //        checkResult(resultEasy4);
      //
      //        break;

      default:
        throw new ValidationServiceException("undefined validation for validation point:" + validationPoint);
    }
  }

  private void checkResult(ComplexResult complexResult) throws ValidationException {

    if (!complexResult.isSuccess()) {
      ValidationReportVO v = Validation.getValidationReportVO(complexResult);

      logger.warn(complexResult);

      throw new ValidationException(v);
    }
  }

  // ### Yearbook Section ########################################

  //  private boolean isBook(MdsPublicationVO.Genre genre) {
  //    return (MdsPublicationVO.Genre.BOOK.equals(genre) //
  //        || MdsPublicationVO.Genre.COLLECTED_EDITION.equals(genre) //
  //        || MdsPublicationVO.Genre.COMMENTARY.equals(genre) //
  //        || MdsPublicationVO.Genre.FESTSCHRIFT.equals(genre) //
  //        || MdsPublicationVO.Genre.HANDBOOK.equals(genre) //
  //        || MdsPublicationVO.Genre.MONOGRAPH.equals(genre));
  //  }
  //
  //  private boolean isBookChapter(MdsPublicationVO.Genre genre) {
  //    return (MdsPublicationVO.Genre.BOOK_ITEM.equals(genre) //
  //        || MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION.equals(genre) //
  //        || MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY.equals(genre) //
  //        || MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK.equals(genre) //
  //        || MdsPublicationVO.Genre.CONTRIBUTION_TO_ENCYCLOPEDIA.equals(genre) //
  //        || MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT.equals(genre));
  //  }
  //
  //  private boolean isConferencePaper(MdsPublicationVO.Genre genre) {
  //    return MdsPublicationVO.Genre.CONFERENCE_PAPER.equals(genre);
  //  }
  //
  //  private boolean isConferenceReport(MdsPublicationVO.Genre genre) {
  //    return MdsPublicationVO.Genre.CONFERENCE_REPORT.equals(genre);
  //  }
  //
  //  private boolean isIssue(MdsPublicationVO.Genre genre) {
  //    return MdsPublicationVO.Genre.ISSUE.equals(genre);
  //  }
  //
  //  private boolean isJournal(MdsPublicationVO.Genre genre) {
  //    return MdsPublicationVO.Genre.JOURNAL.equals(genre);
  //  }
  //
  //  private boolean isJournalArticle(MdsPublicationVO.Genre genre) {
  //    return (MdsPublicationVO.Genre.ARTICLE.equals(genre) //
  //        || MdsPublicationVO.Genre.BOOK_REVIEW.equals(genre) //
  //        || MdsPublicationVO.Genre.CASE_NOTE.equals(genre) //
  //        || MdsPublicationVO.Genre.CASE_STUDY.equals(genre) //
  //        || MdsPublicationVO.Genre.EDITORIAL.equals(genre) //
  //        || MdsPublicationVO.Genre.NEWSPAPER_ARTICLE.equals(genre) //
  //        || MdsPublicationVO.Genre.OPINION.equals(genre));
  //  }
  //
  //  private boolean isSeries(MdsPublicationVO.Genre genre) {
  //    return MdsPublicationVO.Genre.SERIES.equals(genre);
  //  }
  //
  //  private boolean isPaper(MdsPublicationVO.Genre genre) {
  //    return MdsPublicationVO.Genre.PAPER.equals(genre);
  //  }
  //
  //  private boolean isProceedings(MdsPublicationVO.Genre genre) {
  //    return MdsPublicationVO.Genre.PROCEEDINGS.equals(genre);
  //  }
  //
  //  private boolean isThesis(MdsPublicationVO.Genre genre) {
  //    return MdsPublicationVO.Genre.THESIS.equals(genre);
  //  }

}
