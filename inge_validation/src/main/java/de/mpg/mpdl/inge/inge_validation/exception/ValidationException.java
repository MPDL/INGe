package de.mpg.mpdl.inge.inge_validation.exception;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;

@SuppressWarnings("serial")
public class ValidationException extends Exception {

  private final ValidationReportVO report;

  public ValidationException(ValidationReportVO report) {
    this.report = report;
  }

  public ValidationReportVO getReport() {
    return this.report;
  }
}
