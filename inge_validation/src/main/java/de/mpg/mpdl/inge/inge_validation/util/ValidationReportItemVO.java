package de.mpg.mpdl.inge.inge_validation.util;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

@SuppressWarnings("serial")
public class ValidationReportItemVO extends ValueObject {
  private String content;
  private String element;

  public final String getContent() {
    return content;
  }

  public final void setContent(final String content) {
    this.content = content;
  }

  public final String getElement() {
    return element;
  }

  public final void setElement(final String element) {
    this.element = element;
  }

  @Override
  public String toString() {
    return "ValidationReportItemVO [content=" + content + ", element=" + element + "]";
  }

}
