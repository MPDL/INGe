package de.mpg.mpdl.inge.inge_validation.exception;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
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

  public String getMessage() {
    StringBuilder sb = new StringBuilder();
    if (null != this.report) {
      for (ValidationReportItemVO item : this.report.getItems()) {
        sb.append(item.toString());
        sb.append(" ");
      }
    }
    return sb.toString();
  }
}
