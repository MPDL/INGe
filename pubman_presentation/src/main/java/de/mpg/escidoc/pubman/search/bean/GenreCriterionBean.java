package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;

import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.GenreCriterionVO;

/**
 * POJO bean to deal with one GenreCriterionVO.
 * 
 * @author Mario Wagner
 */
public class GenreCriterionBean extends CriterionBean
{
	public static final String BEAN_NAME = "GenreCriterionBean";
	
	private GenreCriterionVO genreCriterionVO;
	
	// selection fields for the MdsPublicationVO.Genre enum
	private boolean searchArticle, searchBook, searchBookItem, searchCoursewareLecture, searchConferencePaper, searchConferenceReport;
	private boolean searchIssue, searchJournal, searchManuscript, searchOther, searchPaper, searchPoster;
	private boolean searchProceedings, searchReport, searchSeries, searchTalkAtEvent, searchThesis;
	
    public GenreCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new GenreCriterionVO());
	}

	public GenreCriterionBean(GenreCriterionVO genreCriterionVO)
	{
		setGenreCriterionVO(genreCriterionVO);
	}

	@Override
	public CriterionVO getCriterionVO()
	{
		return genreCriterionVO;
	}

	public GenreCriterionVO getGenreCriterionVO()
	{
		return genreCriterionVO;
	}

	public void setGenreCriterionVO(GenreCriterionVO genreCriterionVO)
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

}
