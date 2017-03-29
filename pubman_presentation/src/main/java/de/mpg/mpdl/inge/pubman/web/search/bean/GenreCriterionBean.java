package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.GenreCriterion;

/**
 * POJO bean to deal with one GenreCriterionVO.
 * 
 * @author Mario Wagner
 */
@SuppressWarnings("serial")
public class GenreCriterionBean extends CriterionBean {
  private GenreCriterion genreCriterionVO;

  // selection fields for the MdsPublicationVO.Genre enum
  private boolean searchArticle, searchBook, searchBookItem, searchCoursewareLecture,
      searchConferencePaper, searchMeetingAbstract, searchConferenceReport;
  private boolean searchIssue, searchJournal, searchManuscript, searchOther, searchPaper,
      searchPoster;
  private boolean searchProceedings, searchReport, searchSeries, searchTalkAtEvent, searchThesis;
  // JUS
  private boolean searchContributionToCollectedEdition, searchMonograph,
      searchContributionToCommentary, searchCaseNote;
  private boolean searchBookReview, searchContributionToFestschrift, searchCommentary,
      searchCollectedEdition, searchFestschrift, searchHandbook;
  private boolean searchContributionToEncyclopedia, searchNewspaperArticle, searchCaseStudy,
      searchOpinion, searchEditorial, searchContributionToHandbook;
  // NIMS
  private boolean searchPatent;

  public GenreCriterionBean() {
    // ensure the parentVO is never null;
    this(new GenreCriterion());
  }

  public GenreCriterionBean(GenreCriterion genreCriterionVO) {
    this.setGenreCriterionVO(genreCriterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return this.genreCriterionVO;
  }

  public GenreCriterion getGenreCriterionVO() {
    return this.genreCriterionVO;
  }

  public void setGenreCriterionVO(GenreCriterion genreCriterionVO) {
    this.genreCriterionVO = genreCriterionVO;
    if (genreCriterionVO.getGenre() == null) {
      genreCriterionVO.setGenre(new ArrayList<MdsPublicationVO.Genre>());
    }

    for (final MdsPublicationVO.Genre genre : genreCriterionVO.getGenre()) {
      if (MdsPublicationVO.Genre.ARTICLE.equals(genre)) {
        this.searchArticle = true;
      } else if (MdsPublicationVO.Genre.BOOK.equals(genre)) {
        this.searchBook = true;
      } else if (MdsPublicationVO.Genre.BOOK_ITEM.equals(genre)) {
        this.searchBookItem = true;
      } else if (MdsPublicationVO.Genre.CONFERENCE_PAPER.equals(genre)) {
        this.searchConferencePaper = true;
      } else if (MdsPublicationVO.Genre.MEETING_ABSTRACT.equals(genre)) {
        this.searchMeetingAbstract = true;
      } else if (MdsPublicationVO.Genre.CONFERENCE_REPORT.equals(genre)) {
        this.searchConferenceReport = true;
      } else if (MdsPublicationVO.Genre.COURSEWARE_LECTURE.equals(genre)) {
        this.searchCoursewareLecture = true;
      } else if (MdsPublicationVO.Genre.ISSUE.equals(genre)) {
        this.searchIssue = true;
      } else if (MdsPublicationVO.Genre.JOURNAL.equals(genre)) {
        this.searchJournal = true;
      } else if (MdsPublicationVO.Genre.MANUSCRIPT.equals(genre)) {
        this.searchManuscript = true;
      } else if (MdsPublicationVO.Genre.OTHER.equals(genre)) {
        this.searchOther = true;
      } else if (MdsPublicationVO.Genre.PAPER.equals(genre)) {
        this.searchPaper = true;
      } else if (MdsPublicationVO.Genre.POSTER.equals(genre)) {
        this.searchPoster = true;
      } else if (MdsPublicationVO.Genre.PROCEEDINGS.equals(genre)) {
        this.searchProceedings = true;
      } else if (MdsPublicationVO.Genre.REPORT.equals(genre)) {
        this.searchReport = true;
      } else if (MdsPublicationVO.Genre.SERIES.equals(genre)) {
        this.searchSeries = true;
      } else if (MdsPublicationVO.Genre.TALK_AT_EVENT.equals(genre)) {
        this.searchTalkAtEvent = true;
      } else if (MdsPublicationVO.Genre.THESIS.equals(genre)) {
        this.searchThesis = true;
      } else if (MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION.equals(genre)) {
        this.searchContributionToCollectedEdition = true;
      } else if (MdsPublicationVO.Genre.MONOGRAPH.equals(genre)) {
        this.searchMonograph = true;
      } else if (MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY.equals(genre)) {
        this.searchContributionToCommentary = true;
      } else if (MdsPublicationVO.Genre.CASE_NOTE.equals(genre)) {
        this.searchCaseNote = true;
      } else if (MdsPublicationVO.Genre.BOOK_REVIEW.equals(genre)) {
        this.searchBookReview = true;
      } else if (MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT.equals(genre)) {
        this.searchContributionToFestschrift = true;
      } else if (MdsPublicationVO.Genre.COMMENTARY.equals(genre)) {
        this.searchCommentary = true;
      } else if (MdsPublicationVO.Genre.COLLECTED_EDITION.equals(genre)) {
        this.searchCollectedEdition = true;
      } else if (MdsPublicationVO.Genre.FESTSCHRIFT.equals(genre)) {
        this.searchFestschrift = true;
      } else if (MdsPublicationVO.Genre.CONTRIBUTION_TO_ENCYCLOPEDIA.equals(genre)) {
        this.searchContributionToEncyclopedia = true;
      } else if (MdsPublicationVO.Genre.NEWSPAPER_ARTICLE.equals(genre)) {
        this.searchNewspaperArticle = true;
      } else if (MdsPublicationVO.Genre.CASE_STUDY.equals(genre)) {
        this.searchCaseStudy = true;
      } else if (MdsPublicationVO.Genre.OPINION.equals(genre)) {
        this.searchOpinion = true;
      } else if (MdsPublicationVO.Genre.EDITORIAL.equals(genre)) {
        this.searchEditorial = true;
      } else if (MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK.equals(genre)) {
        this.searchContributionToHandbook = true;
      } else if (MdsPublicationVO.Genre.HANDBOOK.equals(genre)) {
        this.searchHandbook = true;
      } else if (MdsPublicationVO.Genre.PATENT.equals(genre)) {
        this.searchPatent = true;
      }
    }
  }


  /**
   * Action navigation call to select all MdsPublicationVO.Genre enums
   * 
   * @return null
   */
  public void selectAll() {
    this.genreCriterionVO.getGenre().clear();

    this.setSearchArticle(true);
    this.setSearchBook(true);
    this.setSearchBookItem(true);
    this.setSearchConferencePaper(true);
    this.setSearchMeetingAbstract(true);
    this.setSearchConferenceReport(true);
    this.setSearchCoursewareLecture(true);
    this.setSearchIssue(true);
    this.setSearchJournal(true);
    this.setSearchManuscript(true);
    this.setSearchOther(true);
    this.setSearchPaper(true);
    this.setSearchPoster(true);
    this.setSearchProceedings(true);
    this.setSearchReport(true);
    this.setSearchSeries(true);
    this.setSearchTalkAtEvent(true);
    this.setSearchThesis(true);
    // JUS
    this.setSearchContributionToCollectedEdition(true);
    this.setSearchMonograph(true);
    this.setSearchContributionToCommentary(true);
    this.setSearchCaseNote(true);
    this.setSearchBookReview(true);
    this.setSearchContributionToFestschrift(true);
    this.setSearchCommentary(true);
    this.setSearchCollectedEdition(true);
    this.setSearchFestschrift(true);
    this.setSearchContributionToEncyclopedia(true);
    this.setSearchNewspaperArticle(true);
    this.setSearchCaseStudy(true);
    this.setSearchOpinion(true);
    this.setSearchEditorial(true);
    this.setSearchContributionToHandbook(true);
  }

  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public void clearCriterion() {
    this.setSearchArticle(false);
    this.setSearchBook(false);
    this.setSearchBookItem(false);
    this.setSearchConferencePaper(false);
    this.setSearchMeetingAbstract(false);
    this.setSearchConferenceReport(false);
    this.setSearchCoursewareLecture(false);
    this.setSearchIssue(false);
    this.setSearchJournal(false);
    this.setSearchManuscript(false);
    this.setSearchOther(false);
    this.setSearchPaper(false);
    this.setSearchPoster(false);
    this.setSearchProceedings(false);
    this.setSearchReport(false);
    this.setSearchSeries(false);
    this.setSearchTalkAtEvent(false);
    this.setSearchThesis(false);
    // JUS
    this.setSearchContributionToCollectedEdition(false);
    this.setSearchMonograph(false);
    this.setSearchContributionToCommentary(false);
    this.setSearchCaseNote(false);
    this.setSearchBookReview(false);
    this.setSearchContributionToFestschrift(false);
    this.setSearchCommentary(false);
    this.setSearchCollectedEdition(false);
    this.setSearchFestschrift(false);
    this.setSearchContributionToEncyclopedia(false);
    this.setSearchNewspaperArticle(false);
    this.setSearchCaseStudy(false);
    this.setSearchOpinion(false);
    this.setSearchEditorial(false);
    this.setSearchContributionToHandbook(false);
    this.setSearchHandbook(false);
    this.setSearchPatent(false);

    this.genreCriterionVO.getGenre().clear();
    this.genreCriterionVO.setSearchString("");
  }


  public boolean isSearchArticle() {
    return this.searchArticle;
  }

  public void setSearchArticle(boolean searchArticle) {
    this.searchArticle = searchArticle;
    if (searchArticle == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.ARTICLE)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.ARTICLE);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.ARTICLE);
    }
  }

  public boolean isSearchBook() {
    return this.searchBook;
  }

  public void setSearchBook(boolean searchBook) {
    this.searchBook = searchBook;
    if (searchBook == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.BOOK)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.BOOK);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.BOOK);
    }
  }

  public boolean isSearchBookItem() {
    return this.searchBookItem;
  }

  public void setSearchBookItem(boolean searchBookItem) {
    this.searchBookItem = searchBookItem;
    if (searchBookItem == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.BOOK_ITEM)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.BOOK_ITEM);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.BOOK_ITEM);
    }
  }

  public boolean isSearchConferencePaper() {
    return this.searchConferencePaper;
  }

  public void setSearchConferencePaper(boolean searchConferencePaper) {
    this.searchConferencePaper = searchConferencePaper;
    if (searchConferencePaper == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CONFERENCE_PAPER)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONFERENCE_PAPER);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONFERENCE_PAPER);
    }
  }

  public boolean isSearchMeetingAbstract() {
    return this.searchMeetingAbstract;
  }

  public void setSearchMeetingAbstract(boolean searchMeetingAbstract) {
    this.searchMeetingAbstract = searchMeetingAbstract;
    if (searchMeetingAbstract) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.MEETING_ABSTRACT)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.MEETING_ABSTRACT);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.MEETING_ABSTRACT);
    }
  }

  public boolean isSearchConferenceReport() {
    return this.searchConferenceReport;
  }

  public void setSearchConferenceReport(boolean searchConferenceReport) {
    this.searchConferenceReport = searchConferenceReport;
    if (searchConferenceReport == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CONFERENCE_REPORT)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONFERENCE_REPORT);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONFERENCE_REPORT);
    }
  }

  public boolean isSearchCoursewareLecture() {
    return this.searchCoursewareLecture;
  }

  public void setSearchCoursewareLecture(boolean searchCoursewareLecture) {
    this.searchCoursewareLecture = searchCoursewareLecture;
    if (searchCoursewareLecture == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.COURSEWARE_LECTURE)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.COURSEWARE_LECTURE);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.COURSEWARE_LECTURE);
    }
  }

  public boolean isSearchIssue() {
    return this.searchIssue;
  }

  public void setSearchIssue(boolean searchIssue) {
    this.searchIssue = searchIssue;
    if (searchIssue == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.ISSUE)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.ISSUE);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.ISSUE);
    }
  }

  public boolean isSearchJournal() {
    return this.searchJournal;
  }

  public void setSearchJournal(boolean searchJournal) {
    this.searchJournal = searchJournal;
    if (searchJournal == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.JOURNAL)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.JOURNAL);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.JOURNAL);
    }
  }

  public boolean isSearchManuscript() {
    return this.searchManuscript;
  }

  public void setSearchManuscript(boolean searchManuscript) {
    this.searchManuscript = searchManuscript;
    if (searchManuscript == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.MANUSCRIPT)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.MANUSCRIPT);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.MANUSCRIPT);
    }
  }

  public boolean isSearchOther() {
    return this.searchOther;
  }

  public void setSearchOther(boolean searchOther) {
    this.searchOther = searchOther;
    if (searchOther == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.OTHER)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.OTHER);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.OTHER);
    }
  }

  public boolean isSearchPaper() {
    return this.searchPaper;
  }

  public void setSearchPaper(boolean searchPaper) {
    this.searchPaper = searchPaper;
    if (searchPaper == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.PAPER)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.PAPER);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.PAPER);
    }
  }

  public boolean isSearchPoster() {
    return this.searchPoster;
  }

  public void setSearchPoster(boolean searchPoster) {
    this.searchPoster = searchPoster;
    if (searchPoster == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.POSTER)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.POSTER);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.POSTER);
    }
  }

  public boolean isSearchProceedings() {
    return this.searchProceedings;
  }

  public void setSearchProceedings(boolean searchProceedings) {
    this.searchProceedings = searchProceedings;
    if (searchProceedings == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.PROCEEDINGS)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.PROCEEDINGS);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.PROCEEDINGS);
    }
  }

  public boolean isSearchReport() {
    return this.searchReport;
  }

  public void setSearchReport(boolean searchReport) {
    this.searchReport = searchReport;
    if (searchReport == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.REPORT)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.REPORT);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.REPORT);
    }
  }

  public boolean isSearchSeries() {
    return this.searchSeries;
  }

  public void setSearchSeries(boolean searchSeries) {
    this.searchSeries = searchSeries;
    if (searchSeries == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.SERIES)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.SERIES);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.SERIES);
    }
  }

  public boolean isSearchTalkAtEvent() {
    return this.searchTalkAtEvent;
  }

  public void setSearchTalkAtEvent(boolean searchTalkAtEvent) {
    this.searchTalkAtEvent = searchTalkAtEvent;
    if (searchTalkAtEvent == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.TALK_AT_EVENT)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.TALK_AT_EVENT);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.TALK_AT_EVENT);
    }
  }

  public boolean isSearchThesis() {
    return this.searchThesis;
  }

  public void setSearchThesis(boolean searchThesis) {
    this.searchThesis = searchThesis;
    if (searchThesis == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.THESIS)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.THESIS);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.THESIS);
    }
  }

  // JUS
  public boolean isSearchContributionToCollectedEdition() {
    return this.searchContributionToCollectedEdition;
  }

  public void setSearchContributionToCollectedEdition(boolean searchContributionToCollectedEdition) {
    this.searchContributionToCollectedEdition = searchContributionToCollectedEdition;
    if (searchContributionToCollectedEdition == true) {
      if (!this.genreCriterionVO.getGenre().contains(
          MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION)) {
        this.genreCriterionVO.getGenre().add(
            MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(
          MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION);
    }

  }

  public boolean isSearchMonograph() {
    return this.searchMonograph;
  }

  public void setSearchMonograph(boolean searchMonograph) {
    this.searchMonograph = searchMonograph;
    if (searchMonograph == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.MONOGRAPH)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.MONOGRAPH);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.MONOGRAPH);
    }

  }

  public boolean isSearchContributionToCommentary() {
    return this.searchContributionToCommentary;
  }

  public void setSearchContributionToCommentary(boolean searchContributionToCommentary) {
    this.searchContributionToCommentary = searchContributionToCommentary;
    if (searchContributionToCommentary == true) {
      if (!this.genreCriterionVO.getGenre().contains(
          MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY);
    }

  }

  public boolean isSearchCaseNote() {
    return this.searchCaseNote;
  }

  public void setSearchCaseNote(boolean searchCaseNote) {
    this.searchCaseNote = searchCaseNote;
    if (searchCaseNote == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CASE_NOTE)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CASE_NOTE);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CASE_NOTE);
    }

  }

  public boolean isSearchBookReview() {
    return this.searchBookReview;
  }

  public void setSearchBookReview(boolean searchBookReview) {
    this.searchBookReview = searchBookReview;
    if (searchBookReview == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.BOOK_REVIEW)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.BOOK_REVIEW);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.BOOK_REVIEW);
    }

  }

  public boolean isSearchContributionToFestschrift() {
    return this.searchContributionToFestschrift;
  }

  public void setSearchContributionToFestschrift(boolean searchContributionToFestschrift) {
    this.searchContributionToFestschrift = searchContributionToFestschrift;
    if (searchContributionToFestschrift == true) {
      if (!this.genreCriterionVO.getGenre().contains(
          MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT);
    }

  }

  public boolean isSearchCommentary() {
    return this.searchCommentary;
  }

  public void setSearchCommentary(boolean searchCommentary) {
    this.searchCommentary = searchCommentary;
    if (searchCommentary == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.COMMENTARY)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.COMMENTARY);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.COMMENTARY);
    }

  }

  public boolean isSearchCollectedEdition() {
    return this.searchCollectedEdition;
  }

  public void setSearchCollectedEdition(boolean searchCollectedEdition) {
    this.searchCollectedEdition = searchCollectedEdition;
    if (searchCollectedEdition == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.COLLECTED_EDITION)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.COLLECTED_EDITION);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.COLLECTED_EDITION);
    }

  }

  public boolean isSearchFestschrift() {
    return this.searchFestschrift;
  }

  public void setSearchFestschrift(boolean searchFestschrift) {
    this.searchFestschrift = searchFestschrift;
    if (searchFestschrift == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.FESTSCHRIFT)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.FESTSCHRIFT);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.FESTSCHRIFT);
    }

  }

  public boolean isSearchContributionToEncyclopedia() {
    return this.searchContributionToEncyclopedia;
  }

  public void setSearchContributionToEncyclopedia(boolean searchContributionToEncyclopedia) {
    this.searchContributionToEncyclopedia = searchContributionToEncyclopedia;
    if (searchContributionToEncyclopedia == true) {
      if (!this.genreCriterionVO.getGenre().contains(
          MdsPublicationVO.Genre.CONTRIBUTION_TO_ENCYCLOPEDIA)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONTRIBUTION_TO_ENCYCLOPEDIA);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONTRIBUTION_TO_ENCYCLOPEDIA);
    }

  }

  public boolean isSearchNewspaperArticle() {
    return this.searchNewspaperArticle;
  }

  public void setSearchNewspaperArticle(boolean searchNewspaperArticle) {
    this.searchNewspaperArticle = searchNewspaperArticle;
    if (searchNewspaperArticle == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.NEWSPAPER_ARTICLE)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.NEWSPAPER_ARTICLE);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.NEWSPAPER_ARTICLE);
    }

  }

  public boolean isSearchCaseStudy() {
    return this.searchCaseStudy;
  }

  public void setSearchCaseStudy(boolean searchCaseStudy) {
    this.searchCaseStudy = searchCaseStudy;
    if (searchCaseStudy == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CASE_STUDY)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CASE_STUDY);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CASE_STUDY);
    }

  }

  public boolean isSearchOpinion() {
    return this.searchOpinion;
  }

  public void setSearchOpinion(boolean searchOpinion) {
    this.searchOpinion = searchOpinion;
    if (searchOpinion == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.OPINION)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.OPINION);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.OPINION);
    }

  }

  public boolean isSearchEditorial() {
    return this.searchEditorial;
  }

  public void setSearchEditorial(boolean searchEditorial) {
    this.searchEditorial = searchEditorial;
    if (searchEditorial == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.EDITORIAL)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.EDITORIAL);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.EDITORIAL);
    }

  }

  public boolean isSearchContributionToHandbook() {
    return this.searchContributionToHandbook;
  }

  public void setSearchContributionToHandbook(boolean searchContributionToHandbook) {
    this.searchContributionToHandbook = searchContributionToHandbook;
    if (searchContributionToHandbook == true) {
      if (!this.genreCriterionVO.getGenre().contains(
          MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK);
    }

  }

  public boolean isSearchHandbook() {
    return this.searchHandbook;
  }

  public void setSearchHandbook(boolean searchHandbook) {
    this.searchHandbook = searchHandbook;
    if (searchHandbook == true) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.HANDBOOK)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.HANDBOOK);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.HANDBOOK);
    }

  }

  public void setSearchPatent(boolean searchPatent) {
    this.searchPatent = searchPatent;
    if (searchPatent) {
      if (!this.genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.PATENT)) {
        this.genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.PATENT);
      }
    } else {
      this.genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.PATENT);
    }
  }

  public boolean isSearchPatent() {
    return this.searchPatent;
  }


}
