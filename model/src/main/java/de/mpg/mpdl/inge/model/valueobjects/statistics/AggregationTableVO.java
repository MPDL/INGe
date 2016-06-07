package de.mpg.mpdl.inge.model.valueobjects.statistics;

import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

public class AggregationTableVO extends ValueObject {


  private String name;

  private List<AggregationFieldVO> aggregationFields;

  private List<AggregationIndexVO> aggregationIndexes;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<AggregationFieldVO> getAggregationFields() {
    return aggregationFields;
  }

  public void setAggregationFields(List<AggregationFieldVO> aggregationFields) {
    this.aggregationFields = aggregationFields;
  }

  public List<AggregationIndexVO> getAggregationIndexes() {
    return aggregationIndexes;
  }

  public void setAggregationIndexes(List<AggregationIndexVO> aggregationIndexes) {
    this.aggregationIndexes = aggregationIndexes;
  }
}
