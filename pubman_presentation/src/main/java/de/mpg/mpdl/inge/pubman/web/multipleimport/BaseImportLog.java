package de.mpg.mpdl.inge.pubman.web.multipleimport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;

import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;

public class BaseImportLog {
  /**
   * enum to describe if something went wrong with this element.
   * <p>
   * - FINE: everything is alright - WARNING: import worked, but something could have been done
   * better - PROBLEM: some item was not imported because validation failed - ERROR: some items were
   * not imported because there were system errors during the import - FATAL: the import was
   * interrupted completely due to system errors
   */
  public enum ErrorLevel
  {
    ERROR, FATAL, FINE, PROBLEM, WARNING
  }

  public enum Status
  {
    FINISHED, PENDING, ROLLBACK, SUSPENDED
  }

  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  public static final int PERCENTAGE_COMPLETED = 100;
  public static final int PERCENTAGE_DELETE_END = 89;
  public static final int PERCENTAGE_DELETE_START = 5;
  public static final int PERCENTAGE_DELETE_SUSPEND = 10;
  public static final int PERCENTAGE_IMPORT_END = 29;
  public static final int PERCENTAGE_IMPORT_PREPARE = 65;
  public static final int PERCENTAGE_IMPORT_START = 5;
  public static final int PERCENTAGE_SUBMIT_END = 89;
  public static final int PERCENTAGE_SUBMIT_START = 5;
  public static final int PERCENTAGE_SUBMIT_SUSPEND = 10;

  protected Date endDate;
  protected BaseImportLog.ErrorLevel errorLevel;
  protected int id;
  protected String message;
  protected Date startDate;
  protected BaseImportLog.Status status;

  public BaseImportLog() {}

  public Date getEndDate() {
    return this.endDate;
  }

  public synchronized String getEndDateFormatted() {
    if (null != this.endDate) {
      return BaseImportLog.DATE_FORMAT.format(this.endDate);
    }

    return "";
  }

  public BaseImportLog.ErrorLevel getErrorLevel() {
    return this.errorLevel;
  }

  public int getId() {
    return this.id;
  }

  public String getLocalizedMessage() {
    try {
      return ((InternationalizationHelper) FacesTools.findBean("InternationalizationHelper")).getMessage(this.message);
    } catch (MissingResourceException mre) {
      // No message entry for this message, it's probably raw data.
      return this.message;
    }
  }

  public String getMessage() {
    return this.message;
  }

  public Date getStartDate() {
    return this.startDate;
  }

  public synchronized String getStartDateFormatted() {
    if (null != this.startDate) {
      String startDateFormatted = BaseImportLog.DATE_FORMAT.format(this.startDate);
      return startDateFormatted;
    }

    return "";
  }

  public BaseImportLog.Status getStatus() {
    return this.status;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public void setErrorLevel(BaseImportLog.ErrorLevel errorLevel) {
    if (null == this.errorLevel || ErrorLevel.FATAL == errorLevel || (ErrorLevel.ERROR == errorLevel && ErrorLevel.FATAL != this.errorLevel)
        || (ErrorLevel.PROBLEM == errorLevel && ErrorLevel.FATAL != this.errorLevel && ErrorLevel.ERROR != this.errorLevel)
        || (ErrorLevel.WARNING == errorLevel && ErrorLevel.FATAL != this.errorLevel && ErrorLevel.ERROR != this.errorLevel
            && ErrorLevel.PROBLEM != this.errorLevel)) {
      this.errorLevel = errorLevel;
    }
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public void setStatus(BaseImportLog.Status status) {
    this.status = status;
  }
}
