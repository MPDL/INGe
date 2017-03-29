package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.DateCriterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.DateCriterion.DateType;

/**
 * POJO bean to deal with one DateCriterionVO.
 * 
 * @author Mario Wagner
 */
@SuppressWarnings("serial")
public class DateCriterionBean extends CriterionBean {
  private DateCriterion dateCriterionVO;

  // selection fields for the DateCriterionVO.DateType enum
  // ACCEPTED, CREATED, MODIFIED, PUBLISHED_ONLINE, PUBLISHED_PRINT, SUBMITTED
  private boolean searchAccepted, searchCreated, searchModified;
  private boolean searchPublishedOnline, searchPublishedPrint, searchSubmitted;
  private boolean searchEventStart, searchEventEnd;

  public DateCriterionBean() {
    // ensure the parentVO is never null;
    this(new DateCriterion());
  }

  public DateCriterionBean(DateCriterion dateCriterionVO) {
    this.setDateCriterionVO(dateCriterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return this.dateCriterionVO;
  }

  public DateCriterion getDateCriterionVO() {
    return this.dateCriterionVO;
  }

  public void setDateCriterionVO(DateCriterion dateCriterionVO) {
    this.dateCriterionVO = dateCriterionVO;
    if (dateCriterionVO.getDateType() == null) {
      dateCriterionVO.setDateType(new ArrayList<DateType>());
    }

    for (final DateType date : dateCriterionVO.getDateType()) {
      if (DateType.ACCEPTED.equals(date)) {
        this.searchAccepted = true;
      } else if (DateType.CREATED.equals(date)) {
        this.searchCreated = true;
      } else if (DateType.MODIFIED.equals(date)) {
        this.searchModified = true;
      } else if (DateType.PUBLISHED_ONLINE.equals(date)) {
        this.searchPublishedOnline = true;
      } else if (DateType.PUBLISHED_PRINT.equals(date)) {
        this.searchPublishedPrint = true;
      } else if (DateType.SUBMITTED.equals(date)) {
        this.searchSubmitted = true;
      } else if (DateType.EVENT_START.equals(date)) {
        this.searchEventStart = true;
      } else if (DateType.EVENT_END.equals(date)) {
        this.searchEventEnd = true;
      }

    }
  }


  /**
   * Action navigation call to select all DateType enums
   * 
   * @return null
   */
  public void selectAll() {
    this.dateCriterionVO.getDateType().clear();

    this.setSearchAccepted(true);
    this.setSearchCreated(true);
    this.setSearchModified(true);
    this.setSearchPublishedOnline(true);
    this.setSearchPublishedPrint(true);
    this.setSearchSubmitted(true);
    this.setSearchEventStart(true);
    this.setSearchEventEnd(true);
  }

  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public void clearCriterion() {
    this.setSearchAccepted(false);
    this.setSearchCreated(false);
    this.setSearchModified(false);
    this.setSearchPublishedOnline(false);
    this.setSearchPublishedPrint(false);
    this.setSearchSubmitted(false);
    this.setSearchEventStart(false);
    this.setSearchEventEnd(false);

    this.dateCriterionVO.getDateType().clear();
    this.dateCriterionVO.setSearchString("");
    this.dateCriterionVO.setFrom("");
    this.dateCriterionVO.setTo("");
  }

  public boolean isSearchAccepted() {
    return this.searchAccepted;
  }

  public void setSearchAccepted(boolean searchAccepted) {
    this.searchAccepted = searchAccepted;
    if (searchAccepted == true) {
      if (!this.dateCriterionVO.getDateType().contains(DateType.ACCEPTED)) {
        this.dateCriterionVO.getDateType().add(DateType.ACCEPTED);
      }
    } else {
      this.dateCriterionVO.getDateType().remove(DateType.ACCEPTED);
    }
  }

  public boolean isSearchCreated() {
    return this.searchCreated;
  }

  public void setSearchCreated(boolean searchCreated) {
    this.searchCreated = searchCreated;
    if (searchCreated == true) {
      if (!this.dateCriterionVO.getDateType().contains(DateType.CREATED)) {
        this.dateCriterionVO.getDateType().add(DateType.CREATED);
      }
    } else {
      this.dateCriterionVO.getDateType().remove(DateType.CREATED);
    }
  }

  public boolean isSearchModified() {
    return this.searchModified;
  }

  public void setSearchModified(boolean searchModified) {
    this.searchModified = searchModified;
    if (searchModified == true) {
      if (!this.dateCriterionVO.getDateType().contains(DateType.MODIFIED)) {
        this.dateCriterionVO.getDateType().add(DateType.MODIFIED);
      }
    } else {
      this.dateCriterionVO.getDateType().remove(DateType.MODIFIED);
    }
  }

  public boolean isSearchPublishedOnline() {
    return this.searchPublishedOnline;
  }

  public void setSearchPublishedOnline(boolean searchPublishedOnline) {
    this.searchPublishedOnline = searchPublishedOnline;
    if (searchPublishedOnline == true) {
      if (!this.dateCriterionVO.getDateType().contains(DateType.PUBLISHED_ONLINE)) {
        this.dateCriterionVO.getDateType().add(DateType.PUBLISHED_ONLINE);
      }
    } else {
      this.dateCriterionVO.getDateType().remove(DateType.PUBLISHED_ONLINE);
    }
  }

  public boolean isSearchPublishedPrint() {
    return this.searchPublishedPrint;
  }

  public void setSearchPublishedPrint(boolean searchPublishedPrint) {
    this.searchPublishedPrint = searchPublishedPrint;
    if (searchPublishedPrint == true) {
      if (!this.dateCriterionVO.getDateType().contains(DateType.PUBLISHED_PRINT)) {
        this.dateCriterionVO.getDateType().add(DateType.PUBLISHED_PRINT);
      }
    } else {
      this.dateCriterionVO.getDateType().remove(DateType.PUBLISHED_PRINT);
    }
  }

  public boolean isSearchSubmitted() {
    return this.searchSubmitted;
  }

  public void setSearchSubmitted(boolean searchSubmitted) {
    this.searchSubmitted = searchSubmitted;
    if (searchSubmitted == true) {
      if (!this.dateCriterionVO.getDateType().contains(DateType.SUBMITTED)) {
        this.dateCriterionVO.getDateType().add(DateType.SUBMITTED);
      }
    } else {
      this.dateCriterionVO.getDateType().remove(DateType.SUBMITTED);
    }
  }

  public boolean isSearchEventStart() {
    return this.searchEventStart;
  }

  public void setSearchEventStart(boolean searchEventStart) {
    this.searchEventStart = searchEventStart;
    if (searchEventStart == true) {
      if (!this.dateCriterionVO.getDateType().contains(DateType.EVENT_START)) {
        this.dateCriterionVO.getDateType().add(DateType.EVENT_START);
      }
    } else {
      this.dateCriterionVO.getDateType().remove(DateType.EVENT_START);
    }
  }

  public boolean isSearchEventEnd() {
    return this.searchEventEnd;
  }

  public void setSearchEventEnd(boolean searchEventEnd) {
    this.searchEventEnd = searchEventEnd;
    if (searchEventEnd == true) {
      if (!this.dateCriterionVO.getDateType().contains(DateType.EVENT_END)) {
        this.dateCriterionVO.getDateType().add(DateType.EVENT_END);
      }
    } else {
      this.dateCriterionVO.getDateType().remove(DateType.EVENT_END);
    }
  }

}
