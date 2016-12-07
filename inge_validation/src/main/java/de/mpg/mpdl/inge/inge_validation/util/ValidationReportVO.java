package de.mpg.mpdl.inge.inge_validation.util;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

@SuppressWarnings("serial")
public class ValidationReportVO extends ValueObject {
  private List<ValidationReportItemVO> items = new ArrayList<ValidationReportItemVO>();

  public ValidationReportVO() {}

  public java.util.List<ValidationReportItemVO> getItems() {
    return this.items;
  }

  public void addItem(final ValidationReportItemVO item) {
    this.items.add(item);
  }

  public boolean hasItems() {
    return this.items.isEmpty() == false;
  }

  public boolean isValid() {
    return hasItems();
  }

  @Override
  public String toString() {
    return "ValidationReportVO [items=" + items + "]";
  }

}
