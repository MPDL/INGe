package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;

import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.pubman.search.bean.criterion.GenreCriterion;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;

/**
 * POJO bean to deal with one GenreCriterionVO.
 * 
 * @author Mario Wagner
 */
public class GenreCriterionBean extends CriterionBean
{
    public static final String BEAN_NAME = "GenreCriterionBean";
    
    private GenreCriterion genreCriterionVO;
    
    // selection fields for the MdsPublicationVO.Genre enum
    private boolean searchArticle, searchBook, searchBookItem, searchCoursewareLecture, searchConferencePaper, searchConferenceReport;
    private boolean searchIssue, searchJournal, searchManuscript, searchOther, searchPaper, searchPoster;
    private boolean searchProceedings, searchReport, searchSeries, searchTalkAtEvent, searchThesis;
    // JUS
    private boolean searchContributionToCollectedEdition, searchMonograph, searchContributionToCommentary, searchCaseNote;
    private boolean searchBookReview, searchContributionToFestschrift, searchCommentary, searchCollectedEdition, searchFestschrift, searchHandbook;
    private boolean searchContributionToEncyclopedia, searchNewspaperArticle, searchCaseStudy, searchOpinion, searchEditorial, searchContributionToHandbook;
    //NIMS
    private boolean searchPatent;

    public GenreCriterionBean()
    {
        // ensure the parentVO is never null;
        this(new GenreCriterion());
    }

    public GenreCriterionBean(GenreCriterion genreCriterionVO)
    {
        setGenreCriterionVO(genreCriterionVO);
    }

    @Override
    public Criterion getCriterionVO()
    {
        return genreCriterionVO;
    }

    public GenreCriterion getGenreCriterionVO()
    {
        return genreCriterionVO;
    }

    public void setGenreCriterionVO(GenreCriterion genreCriterionVO)
    {
        this.genreCriterionVO = genreCriterionVO;
        if (genreCriterionVO.getGenre() == null)
        {
            genreCriterionVO.setGenre(new ArrayList<MdsPublicationVO.Genre>());
        }
            
        for (MdsPublicationVO.Genre genre : genreCriterionVO.getGenre())
        {
            if (MdsPublicationVO.Genre.ARTICLE.equals(genre))
                searchArticle = true;
            else if (MdsPublicationVO.Genre.BOOK.equals(genre))
                searchBook = true;
            else if (MdsPublicationVO.Genre.BOOK_ITEM.equals(genre))
                searchBookItem = true;
            else if (MdsPublicationVO.Genre.CONFERENCE_PAPER.equals(genre))
                searchConferencePaper = true;
            else if (MdsPublicationVO.Genre.CONFERENCE_REPORT.equals(genre))
                searchConferenceReport = true;
            else if (MdsPublicationVO.Genre.COURSEWARE_LECTURE.equals(genre))
                searchCoursewareLecture = true;
            else if (MdsPublicationVO.Genre.ISSUE.equals(genre))
                searchIssue = true;
            else if (MdsPublicationVO.Genre.JOURNAL.equals(genre))
                searchJournal = true;
            else if (MdsPublicationVO.Genre.MANUSCRIPT.equals(genre))
                searchManuscript = true;
            else if (MdsPublicationVO.Genre.OTHER.equals(genre))
                searchOther = true;
            else if (MdsPublicationVO.Genre.PAPER.equals(genre))
                searchPaper = true;
            else if (MdsPublicationVO.Genre.POSTER.equals(genre))
                searchPoster = true;
            else if (MdsPublicationVO.Genre.PROCEEDINGS.equals(genre))
                searchProceedings = true;
            else if (MdsPublicationVO.Genre.REPORT.equals(genre))
                searchReport = true;
            else if (MdsPublicationVO.Genre.SERIES.equals(genre))
                searchSeries = true;
            else if (MdsPublicationVO.Genre.TALK_AT_EVENT.equals(genre))
                searchTalkAtEvent = true;
            else if (MdsPublicationVO.Genre.THESIS.equals(genre))
                searchThesis = true;
            //JUS
            else if(MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION.equals(genre))
                searchContributionToCollectedEdition = true;
            else if (MdsPublicationVO.Genre.MONOGRAPH.equals(genre))
                searchMonograph = true;
            else if (MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY.equals(genre))
                  searchContributionToCommentary = true; 
            else if (MdsPublicationVO.Genre.CASE_NOTE.equals(genre))
                  searchCaseNote = true;
            else if (MdsPublicationVO.Genre.BOOK_REVIEW.equals(genre))
                  searchBookReview = true;
            else if (MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT.equals(genre))
                  searchContributionToFestschrift = true;
            else if (MdsPublicationVO.Genre.COMMENTARY.equals(genre))
                  searchCommentary = true;
            else if (MdsPublicationVO.Genre.COLLECTED_EDITION.equals(genre))
                  searchCollectedEdition = true;
            else if (MdsPublicationVO.Genre.FESTSCHRIFT.equals(genre))
                  searchFestschrift = true;
            else if (MdsPublicationVO.Genre.CONTRIBUTION_TO_ENCYCLOPEDIA.equals(genre))
                searchContributionToEncyclopedia = true;
            else if (MdsPublicationVO.Genre.NEWSPAPER_ARTICLE.equals(genre))
                 searchNewspaperArticle = true; 
            else if (MdsPublicationVO.Genre.CASE_STUDY.equals(genre))
                 searchCaseStudy = true; 
            else if (MdsPublicationVO.Genre.OPINION.equals(genre))
                 searchOpinion = true; 
            else if (MdsPublicationVO.Genre.EDITORIAL.equals(genre))
                 searchEditorial = true; 
            else if (MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK.equals(genre))
                 searchContributionToHandbook = true;
            else if (MdsPublicationVO.Genre.HANDBOOK.equals(genre))
                 searchHandbook = true;
            //NIMS
            else if (MdsPublicationVO.Genre.PATENT.equals(genre))
                 searchPatent = true;
        }
    }
    
    
    /**
     * Action navigation call to select all MdsPublicationVO.Genre enums
     * @return null
     */
    public String selectAll()
    {
        genreCriterionVO.getGenre().clear();
        
        setSearchArticle(true);
        setSearchBook(true);
        setSearchBookItem(true);
        setSearchConferencePaper(true);
        setSearchConferenceReport(true);
        setSearchCoursewareLecture(true);
        setSearchIssue(true);
        setSearchJournal(true);
        setSearchManuscript(true);
        setSearchOther(true);
        setSearchPaper(true);
        setSearchPoster(true);
        setSearchProceedings(true);
        setSearchReport(true);
        setSearchSeries(true);
        setSearchTalkAtEvent(true);
        setSearchThesis(true);
        //JUS
        setSearchContributionToCollectedEdition(true);
        setSearchMonograph(true);
        setSearchContributionToCommentary(true);
        setSearchCaseNote(true);
        setSearchBookReview(true); 
        setSearchContributionToFestschrift(true); 
        setSearchCommentary(true); 
        setSearchCollectedEdition(true); 
        setSearchFestschrift(true);
        setSearchContributionToEncyclopedia(true); 
        setSearchNewspaperArticle(true); 
        setSearchCaseStudy(true); 
        setSearchOpinion(true); 
        setSearchEditorial(true); 
        setSearchContributionToHandbook(true);
        

        // navigation refresh
        return null;
    }
    


    /**
     * Action navigation call to clear the current part of the form
     * @return null
     */
    public String clearCriterion()
    {
        setSearchArticle(false);
        setSearchBook(false);
        setSearchBookItem(false);
        setSearchConferencePaper(false);
        setSearchConferenceReport(false);
        setSearchCoursewareLecture(false);
        setSearchIssue(false);
        setSearchJournal(false);
        setSearchManuscript(false);
        setSearchOther(false);
        setSearchPaper(false);
        setSearchPoster(false);
        setSearchProceedings(false);
        setSearchReport(false);
        setSearchSeries(false);
        setSearchTalkAtEvent(false);
        setSearchThesis(false);
        //JUS
        setSearchContributionToCollectedEdition(false);
        setSearchMonograph(false);
        setSearchContributionToCommentary(false);
        setSearchCaseNote(false);
        setSearchBookReview(false); 
        setSearchContributionToFestschrift(false); 
        setSearchCommentary(false); 
        setSearchCollectedEdition(false); 
        setSearchFestschrift(false);
        setSearchContributionToEncyclopedia(false); 
        setSearchNewspaperArticle(false); 
        setSearchCaseStudy(false); 
        setSearchOpinion(false); 
        setSearchEditorial(false); 
        setSearchContributionToHandbook(false);
        setSearchHandbook(false);
        setSearchPatent(false);
        
        genreCriterionVO.getGenre().clear();
        genreCriterionVO.setSearchString("");
        
        // navigation refresh
        return null;
    }


    public boolean isSearchArticle()
    {
        return searchArticle;
    }

    public void setSearchArticle(boolean searchArticle)
    {
        this.searchArticle = searchArticle;
        if (searchArticle == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.ARTICLE))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.ARTICLE);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.ARTICLE);
        }
    }

    public boolean isSearchBook()
    {
        return searchBook;
    }

    public void setSearchBook(boolean searchBook)
    {
        this.searchBook = searchBook;
        if (searchBook == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.BOOK))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.BOOK);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.BOOK);
        }
    }

    public boolean isSearchBookItem()
    {
        return searchBookItem;
    }

    public void setSearchBookItem(boolean searchBookItem)
    {
        this.searchBookItem = searchBookItem;
        if (searchBookItem == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.BOOK_ITEM))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.BOOK_ITEM);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.BOOK_ITEM);
        }
    }

    public boolean isSearchConferencePaper()
    {
        return searchConferencePaper;
    }

    public void setSearchConferencePaper(boolean searchConferencePaper)
    {
        this.searchConferencePaper = searchConferencePaper;
        if (searchConferencePaper == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CONFERENCE_PAPER))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONFERENCE_PAPER);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONFERENCE_PAPER);
        }
    }

    public boolean isSearchConferenceReport()
    {
        return searchConferenceReport;
    }

    public void setSearchConferenceReport(boolean searchConferenceReport)
    {
        this.searchConferenceReport = searchConferenceReport;
        if (searchConferenceReport == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CONFERENCE_REPORT))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONFERENCE_REPORT);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONFERENCE_REPORT);
        }
    }

    public boolean isSearchCoursewareLecture()
    {
        return searchCoursewareLecture;
    }

    public void setSearchCoursewareLecture(boolean searchCoursewareLecture)
    {
        this.searchCoursewareLecture = searchCoursewareLecture;
        if (searchCoursewareLecture == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.COURSEWARE_LECTURE))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.COURSEWARE_LECTURE);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.COURSEWARE_LECTURE);
        }
    }

    public boolean isSearchIssue()
    {
        return searchIssue;
    }

    public void setSearchIssue(boolean searchIssue)
    {
        this.searchIssue = searchIssue;
        if (searchIssue == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.ISSUE))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.ISSUE);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.ISSUE);
        }
    }

    public boolean isSearchJournal()
    {
        return searchJournal;
    }

    public void setSearchJournal(boolean searchJournal)
    {
        this.searchJournal = searchJournal;
        if (searchJournal == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.JOURNAL))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.JOURNAL);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.JOURNAL);
        }
    }

    public boolean isSearchManuscript()
    {
        return searchManuscript;
    }

    public void setSearchManuscript(boolean searchManuscript)
    {
        this.searchManuscript = searchManuscript;
        if (searchManuscript == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.MANUSCRIPT))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.MANUSCRIPT);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.MANUSCRIPT);
        }
    }

    public boolean isSearchOther()
    {
        return searchOther;
    }

    public void setSearchOther(boolean searchOther)
    {
        this.searchOther = searchOther;
        if (searchOther == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.OTHER))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.OTHER);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.OTHER);
        }
    }

    public boolean isSearchPaper()
    {
        return searchPaper;
    }

    public void setSearchPaper(boolean searchPaper)
    {
        this.searchPaper = searchPaper;
        if (searchPaper == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.PAPER))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.PAPER);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.PAPER);
        }
    }

    public boolean isSearchPoster()
    {
        return searchPoster;
    }

    public void setSearchPoster(boolean searchPoster)
    {
        this.searchPoster = searchPoster;
        if (searchPoster == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.POSTER))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.POSTER);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.POSTER);
        }
    }

    public boolean isSearchProceedings()
    {
        return searchProceedings;
    }

    public void setSearchProceedings(boolean searchProceedings)
    {
        this.searchProceedings = searchProceedings;
        if (searchProceedings == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.PROCEEDINGS))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.PROCEEDINGS);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.PROCEEDINGS);
        }
    }

    public boolean isSearchReport()
    {
        return searchReport;
    }

    public void setSearchReport(boolean searchReport)
    {
        this.searchReport = searchReport;
        if (searchReport == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.REPORT))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.REPORT);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.REPORT);
        }
    }

    public boolean isSearchSeries()
    {
        return searchSeries;
    }

    public void setSearchSeries(boolean searchSeries)
    {
        this.searchSeries = searchSeries;
        if (searchSeries == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.SERIES))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.SERIES);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.SERIES);
        }
    }

    public boolean isSearchTalkAtEvent()
    {
        return searchTalkAtEvent;
    }

    public void setSearchTalkAtEvent(boolean searchTalkAtEvent)
    {
        this.searchTalkAtEvent = searchTalkAtEvent;
        if (searchTalkAtEvent == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.TALK_AT_EVENT))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.TALK_AT_EVENT);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.TALK_AT_EVENT);
        }
    }

    public boolean isSearchThesis()
    {
        return searchThesis;
    }

    public void setSearchThesis(boolean searchThesis)
    {
        this.searchThesis = searchThesis;
        if (searchThesis == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.THESIS))
            {
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.THESIS);
            }
        }
        else
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.THESIS);
        }
    }
    
    //JUS
    public boolean isSearchContributionToCollectedEdition() {
        return searchContributionToCollectedEdition;
    }
    
    public void setSearchContributionToCollectedEdition(boolean searchContributionToCollectedEdition) 
    {
        this.searchContributionToCollectedEdition = searchContributionToCollectedEdition;
        if (searchContributionToCollectedEdition == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONTRIBUTION_TO_COLLECTED_EDITION);
        }
        
    }
    
    public boolean isSearchMonograph() {
        return searchMonograph;
    }
    
    public void setSearchMonograph(boolean searchMonograph) 
    {
        this.searchMonograph = searchMonograph;
        if (searchMonograph == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.MONOGRAPH)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.MONOGRAPH);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.MONOGRAPH);
        }
        
    }
    
    public boolean isSearchContributionToCommentary(){
        return searchContributionToCommentary;
    }
    
    public void setSearchContributionToCommentary(boolean searchContributionToCommentary) 
    {
        this.searchContributionToCommentary = searchContributionToCommentary;
        if (searchContributionToCommentary == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONTRIBUTION_TO_COMMENTARY);
        }
        
    }
    
    public boolean isSearchCaseNote(){
        return searchCaseNote;
    }
    
    public void setSearchCaseNote(boolean searchCaseNote) 
    {
        this.searchCaseNote = searchCaseNote;
        if (searchCaseNote == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CASE_NOTE)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CASE_NOTE);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CASE_NOTE);
        }
        
    }
    
    public boolean isSearchBookReview(){
        return searchBookReview;
    }
    
    public void setSearchBookReview(boolean searchBookReview) 
    {
        this.searchBookReview = searchBookReview;
        if (searchBookReview == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.BOOK_REVIEW)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.BOOK_REVIEW);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.BOOK_REVIEW);
        }
        
    }
    
    public boolean isSearchContributionToFestschrift(){
        return searchContributionToFestschrift;
    }
    
    public void setSearchContributionToFestschrift(boolean searchContributionToFestschrift) 
    {
        this.searchContributionToFestschrift = searchContributionToFestschrift;
        if (searchContributionToFestschrift == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONTRIBUTION_TO_FESTSCHRIFT);
        }
        
    }
    
    public boolean isSearchCommentary(){
        return searchCommentary;
    }
    
    public void setSearchCommentary(boolean searchCommentary) 
    {
        this.searchCommentary = searchCommentary;
        if (searchCommentary == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.COMMENTARY)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.COMMENTARY);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.COMMENTARY);
        }
        
    }
    
    public boolean isSearchCollectedEdition(){
        return searchCollectedEdition;
    }
    
    public void setSearchCollectedEdition(boolean searchCollectedEdition) 
    {
        this.searchCollectedEdition = searchCollectedEdition;
        if (searchCollectedEdition == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.COLLECTED_EDITION)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.COLLECTED_EDITION);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.COLLECTED_EDITION);
        }
        
    }
    
    public boolean isSearchFestschrift(){
        return searchFestschrift;
    }
    
    public void setSearchFestschrift(boolean searchFestschrift) 
    {
        this.searchFestschrift = searchFestschrift;
        if (searchFestschrift == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.FESTSCHRIFT)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.FESTSCHRIFT);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.FESTSCHRIFT);
        }
        
    }
    
    public boolean isSearchContributionToEncyclopedia(){
        return searchContributionToEncyclopedia;
    }
    
    public void setSearchContributionToEncyclopedia(boolean searchContributionToEncyclopedia) 
    {
        this.searchContributionToEncyclopedia = searchContributionToEncyclopedia;
        if (searchContributionToEncyclopedia == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CONTRIBUTION_TO_ENCYCLOPEDIA)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONTRIBUTION_TO_ENCYCLOPEDIA);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONTRIBUTION_TO_ENCYCLOPEDIA);
        }
        
    }
    
    public boolean isSearchNewspaperArticle(){
        return searchNewspaperArticle;
    }
    
    public void setSearchNewspaperArticle(boolean searchNewspaperArticle) 
    {
        this.searchNewspaperArticle = searchNewspaperArticle;
        if (searchNewspaperArticle == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.NEWSPAPER_ARTICLE)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.NEWSPAPER_ARTICLE);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.NEWSPAPER_ARTICLE);
        }
        
    }
    
    public boolean isSearchCaseStudy(){
        return searchCaseStudy;
    }
    
    public void setSearchCaseStudy(boolean searchCaseStudy) 
    {
        this.searchCaseStudy = searchCaseStudy;
        if (searchCaseStudy == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CASE_STUDY)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CASE_STUDY);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CASE_STUDY);
        }
        
    }
    
    public boolean isSearchOpinion(){
        return searchOpinion;
    }
    
    public void setSearchOpinion(boolean searchOpinion) 
    {
        this.searchOpinion = searchOpinion;
        if (searchOpinion == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.OPINION)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.OPINION);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.OPINION);
        }
        
    }
    
    public boolean isSearchEditorial(){
        return searchEditorial;
    }
    
    public void setSearchEditorial(boolean searchEditorial) 
    {
        this.searchEditorial = searchEditorial;
        if (searchEditorial == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.EDITORIAL)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.EDITORIAL);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.EDITORIAL);
        }
        
    }
    
    public boolean isSearchContributionToHandbook(){
        return searchContributionToHandbook;
    }
    
    public void setSearchContributionToHandbook(boolean searchContributionToHandbook) 
    {
        this.searchContributionToHandbook = searchContributionToHandbook;
        if (searchContributionToHandbook == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.CONTRIBUTION_TO_HANDBOOK);
        }
        
    }
    
    public boolean isSearchHandbook(){
        return searchHandbook;
    }
    
    public void setSearchHandbook(boolean searchHandbook) 
    {
        this.searchHandbook = searchHandbook;
        if (searchHandbook == true)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.HANDBOOK)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.HANDBOOK);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.HANDBOOK);
        }
        
    }

    public void setSearchPatent(boolean searchPatent) {
        this.searchPatent = searchPatent;
        if (searchPatent)
        {
            if (!genreCriterionVO.getGenre().contains(MdsPublicationVO.Genre.PATENT)){
                genreCriterionVO.getGenre().add(MdsPublicationVO.Genre.PATENT);
            }
        }
        else 
        {
            genreCriterionVO.getGenre().remove(MdsPublicationVO.Genre.PATENT);
        }
    }

    public boolean isSearchPatent() {
        return searchPatent;
    }
    

}
