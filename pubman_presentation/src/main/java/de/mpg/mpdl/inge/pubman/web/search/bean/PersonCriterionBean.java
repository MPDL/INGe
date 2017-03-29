package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;

import javax.faces.event.ActionEvent;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.PersonCriterion;

/**
 * POJO bean to deal with one PersonCriterionVO.
 * 
 * @author Mario Wagner
 */
@SuppressWarnings("serial")
public class PersonCriterionBean extends CriterionBean {
  private PersonCriterion personCriterionVO;

  // selection fields for the CreatorVO.CreatorRole enum
  private boolean searchAuthor, searchEditor, searchAdvisor, searchArtist, searchCommentator,
      searchContributor;
  private boolean searchIllustrator, searchPainter, searchPhotographer, searchTranscriber,
      searchTranslator, searchReferee, searchHonoree;
  private boolean searchInventor, searchApplicant;

  public PersonCriterionBean() {
    // ensure the parentVO is never null;
    this(new PersonCriterion());
  }

  public PersonCriterionBean(PersonCriterion personCriterionVO) {
    this.setPersonCriterionVO(personCriterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return this.personCriterionVO;
  }

  public PersonCriterion getPersonCriterionVO() {
    return this.personCriterionVO;
  }

  public void setPersonCriterionVO(PersonCriterion personCriterionVO) {
    this.personCriterionVO = personCriterionVO;
    if (personCriterionVO.getCreatorRole() == null) {
      personCriterionVO.setCreatorRole(new ArrayList<CreatorRole>());
    }

    for (final CreatorRole role : personCriterionVO.getCreatorRole()) {
      if (CreatorVO.CreatorRole.ADVISOR.equals(role)) {
        this.searchAdvisor = true;
      } else if (CreatorVO.CreatorRole.ARTIST.equals(role)) {
        this.searchArtist = true;
      } else if (CreatorVO.CreatorRole.AUTHOR.equals(role)) {
        this.searchAuthor = true;
      } else if (CreatorVO.CreatorRole.COMMENTATOR.equals(role)) {
        this.searchCommentator = true;
      } else if (CreatorVO.CreatorRole.CONTRIBUTOR.equals(role)) {
        this.searchContributor = true;
      } else if (CreatorVO.CreatorRole.EDITOR.equals(role)) {
        this.searchEditor = true;
      } else if (CreatorVO.CreatorRole.ILLUSTRATOR.equals(role)) {
        this.searchIllustrator = true;
      } else if (CreatorVO.CreatorRole.PAINTER.equals(role)) {
        this.searchPainter = true;
      } else if (CreatorVO.CreatorRole.PHOTOGRAPHER.equals(role)) {
        this.searchPhotographer = true;
      } else if (CreatorVO.CreatorRole.TRANSCRIBER.equals(role)) {
        this.searchTranscriber = true;
      } else if (CreatorVO.CreatorRole.TRANSLATOR.equals(role)) {
        this.searchTranslator = true;
      } else if (CreatorVO.CreatorRole.REFEREE.equals(role)) {
        this.searchReferee = true;
      } else if (CreatorVO.CreatorRole.HONOREE.equals(role)) {
        this.searchHonoree = true;
      } else if (CreatorVO.CreatorRole.INVENTOR.equals(role)) {
        this.searchInventor = true;
      } else if (CreatorVO.CreatorRole.APPLICANT.equals(role)) {
        this.searchApplicant = true;
      }
    }
  }

  /**
   * Action navigation call to select all CreatorVO.CreatorRole enums
   * 
   * @return null
   */
  public void selectAll() {
    this.setSearchAuthor(true);
    this.setSearchEditor(true);
    this.setSearchAdvisor(true);
    this.setSearchArtist(true);
    this.setSearchCommentator(true);
    this.setSearchContributor(true);
    this.setSearchIllustrator(true);
    this.setSearchPainter(true);
    this.setSearchPhotographer(true);
    this.setSearchTranscriber(true);
    this.setSearchTranslator(true);
    this.setSearchHonoree(true);
    this.setSearchReferee(true);
  }

  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public void clearCriterion() {
    this.setSearchAuthor(false);
    this.setSearchEditor(false);
    this.setSearchAdvisor(false);
    this.setSearchArtist(false);
    this.setSearchCommentator(false);
    this.setSearchContributor(false);
    this.setSearchIllustrator(false);
    this.setSearchPainter(false);
    this.setSearchPhotographer(false);
    this.setSearchTranscriber(false);
    this.setSearchTranslator(false);
    this.setSearchHonoree(false);
    this.setSearchReferee(false);
    this.setSearchInventor(false);
    this.setSearchApplicant(false);

    this.personCriterionVO.getCreatorRole().clear();
    this.personCriterionVO.setSearchString("");
    this.personCriterionVO.setIdentifier(null);
  }

  public boolean isSearchAdvisor() {
    return this.searchAdvisor;
  }

  public void setSearchAdvisor(boolean searchAdvisor) {
    this.searchAdvisor = searchAdvisor;
    if (searchAdvisor == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.ADVISOR)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.ADVISOR);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.ADVISOR);
    }
  }

  public boolean isSearchArtist() {
    return this.searchArtist;
  }

  public void setSearchArtist(boolean searchArtist) {
    this.searchArtist = searchArtist;
    if (searchArtist == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.ARTIST)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.ARTIST);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.ARTIST);
    }
  }

  public boolean isSearchAuthor() {
    return this.searchAuthor;
  }

  public void setSearchAuthor(boolean searchAuthor) {
    this.searchAuthor = searchAuthor;
    if (searchAuthor == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.AUTHOR)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.AUTHOR);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.AUTHOR);
    }
  }

  public boolean isSearchCommentator() {
    return this.searchCommentator;
  }

  public void setSearchCommentator(boolean searchCommentator) {
    this.searchCommentator = searchCommentator;
    if (searchCommentator == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.COMMENTATOR)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.COMMENTATOR);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.COMMENTATOR);
    }
  }

  public boolean isSearchContributor() {
    return this.searchContributor;
  }

  public void setSearchContributor(boolean searchContributor) {
    this.searchContributor = searchContributor;
    if (searchContributor == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.CONTRIBUTOR)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.CONTRIBUTOR);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.CONTRIBUTOR);
    }
  }

  public boolean isSearchEditor() {
    return this.searchEditor;
  }

  public void setSearchEditor(boolean searchEditor) {
    this.searchEditor = searchEditor;
    if (searchEditor == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.EDITOR)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.EDITOR);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.EDITOR);
    }
  }

  public boolean isSearchIllustrator() {
    return this.searchIllustrator;
  }

  public void setSearchIllustrator(boolean searchIllustrator) {
    this.searchIllustrator = searchIllustrator;
    if (searchIllustrator == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.ILLUSTRATOR)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.ILLUSTRATOR);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.ILLUSTRATOR);
    }
  }

  public boolean isSearchPainter() {
    return this.searchPainter;
  }

  public void setSearchPainter(boolean searchPainter) {
    this.searchPainter = searchPainter;
    if (searchPainter == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.PAINTER)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.PAINTER);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.PAINTER);
    }
  }

  public boolean isSearchPhotographer() {
    return this.searchPhotographer;
  }

  public void setSearchPhotographer(boolean searchPhotographer) {
    this.searchPhotographer = searchPhotographer;
    if (searchPhotographer == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.PHOTOGRAPHER)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.PHOTOGRAPHER);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.PHOTOGRAPHER);
    }
  }

  public boolean isSearchTranscriber() {
    return this.searchTranscriber;
  }

  public void setSearchTranscriber(boolean searchTranscriber) {
    this.searchTranscriber = searchTranscriber;
    if (searchTranscriber == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.TRANSCRIBER)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.TRANSCRIBER);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.TRANSCRIBER);
    }
  }

  public boolean isSearchTranslator() {
    return this.searchTranslator;
  }

  public void setSearchTranslator(boolean searchTranslator) {
    this.searchTranslator = searchTranslator;
    if (searchTranslator == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.TRANSLATOR)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.TRANSLATOR);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.TRANSLATOR);
    }
  }


  public boolean isSearchReferee() {
    return this.searchReferee;
  }

  public void setSearchReferee(boolean searchReferee) {
    this.searchReferee = searchReferee;
    if (searchReferee == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.REFEREE)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.REFEREE);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.REFEREE);
    }
  }

  public boolean isSearchHonoree() {
    return this.searchHonoree;
  }

  public void setSearchHonoree(boolean searchHonoree) {
    this.searchHonoree = searchHonoree;
    if (searchHonoree == true) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.HONOREE)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.HONOREE);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.HONOREE);
    }
  }

  public void setSearchInventor(boolean searchInventor) {
    this.searchInventor = searchInventor;
    if (searchInventor) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.INVENTOR)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.INVENTOR);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.INVENTOR);
    }
  }

  public boolean isSearchInventor() {
    return this.searchInventor;
  }

  public void setSearchApplicant(boolean searchApplicant) {
    this.searchApplicant = searchApplicant;
    if (searchApplicant) {
      if (!this.personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.APPLICANT)) {
        this.personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.APPLICANT);
      }
    } else {
      this.personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.APPLICANT);
    }
  }

  public boolean isSearchApplicant() {
    return this.searchApplicant;
  }

  public void removeAutoSuggestValues(ActionEvent e) {
    this.personCriterionVO.setIdentifier(null);
    this.personCriterionVO.setSearchString(null);
  }

}
