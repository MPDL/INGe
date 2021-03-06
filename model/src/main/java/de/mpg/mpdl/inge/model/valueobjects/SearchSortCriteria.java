package de.mpg.mpdl.inge.model.valueobjects;

@SuppressWarnings("serial")
public class SearchSortCriteria extends ValueObject {

  public enum SortOrder
  {
    ASC,
    DESC
  }

  private String indexField;
  private SortOrder sortOrder;

  public SearchSortCriteria(String indexField, SortOrder sortOrder) {
    super();
    this.indexField = indexField;
    this.sortOrder = sortOrder;
  }

  public String getIndexField() {
    return indexField;
  }

  public void setIndexField(String indexField) {
    this.indexField = indexField;
  }

  public SortOrder getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(SortOrder sortOrder) {
    this.sortOrder = sortOrder;
  }

}
