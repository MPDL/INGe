package de.mpg.mpdl.inge.model.valueobjects.statistics;

import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

public class AggregationIndexVO extends ValueObject {

  private String name;

  private List<String> fields;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getFields() {
    return fields;
  }

  public void setFields(List<String> fields) {
    this.fields = fields;
  }


}
