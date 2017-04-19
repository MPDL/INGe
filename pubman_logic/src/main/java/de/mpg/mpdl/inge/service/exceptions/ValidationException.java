package de.mpg.mpdl.inge.service.exceptions;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;

public class ValidationException extends Exception {

  private ValidationReportVO report;

  public ValidationException() {
    super();
    // TODO Auto-generated constructor stub
  }

  public ValidationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    // TODO Auto-generated constructor stub
  }

  public ValidationException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  public ValidationException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  public ValidationException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

  public ValidationException(ValidationReportVO report, Throwable cause) {
    super(cause);
    this.report = report;
  }



}
