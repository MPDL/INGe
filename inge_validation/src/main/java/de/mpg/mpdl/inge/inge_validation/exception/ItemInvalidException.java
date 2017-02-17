package de.mpg.mpdl.inge.inge_validation.exception;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;

@SuppressWarnings("serial")
public class ItemInvalidException extends Exception {

  private ValidationReportVO report;

  public ItemInvalidException(ValidationReportVO report) {
    this.report = report;
  }

  public ValidationReportVO getReport() {
    return this.report;
  }
}
