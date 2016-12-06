package de.mpg.mpdl.inge.inge_validation;

import de.mpg.mpdl.inge.inge_validation.util.ValidationReportVO;

@SuppressWarnings("serial")
public class ItemInvalidException extends Exception {
  
  private ValidationReportVO report;

  public ItemInvalidException(final ValidationReportVO report) {
    this.report = report;
  }

  public final ValidationReportVO getReport() {
    return this.report;
  }

  public final void setReport(ValidationReportVO report) {
    this.report = report;
  }

  @Override
  public String getMessage() {
    return this.report.toString();
  }

}
