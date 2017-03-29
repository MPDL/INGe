package de.mpg.mpdl.inge.inge_validation.data;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

@SuppressWarnings("serial")
public class ValidationReportItemVO extends ValueObject {
  private String content;
  private String element;

  public final String getContent() {
    return this.content;
  }

  public final void setContent(final String content) {
    this.content = content;
  }

  public final String getElement() {
    return this.element;
  }

  public final void setElement(final String element) {
    this.element = element;
  }

  @Override
  public String toString() {
    return "ValidationReportItemVO [content=" + this.content + ", element=" + this.element + "]";
  }

}
