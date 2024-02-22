package de.mpg.mpdl.inge.inge_validation.data;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

@SuppressWarnings("serial")
public class ValidationReportItemVO extends ValueObject {
  public enum Severity
  {
    ERROR, WARNING
  }


  private final String content;
  private String element;
  private final Severity severity;

  public ValidationReportItemVO(String content, Severity severity) {
    this.content = content;
    this.severity = severity;
  }

  public final String getContent() {
    return this.content;
  }

  public final String getElement() {
    return this.element;
  }

  public final void setElement(String element) {
    this.element = element;
  }

  public final Severity getSeverity() {
    return this.severity;
  }

  @Override
  public String toString() {
    return "Validation result [content=" + this.content + ", element=" + this.element + ", severity=" + this.severity + "]";
  }

}
