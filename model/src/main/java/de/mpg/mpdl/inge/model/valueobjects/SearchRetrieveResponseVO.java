package de.mpg.mpdl.inge.model.valueobjects;

import java.util.List;

import org.elasticsearch.action.search.SearchResponse;


public class SearchRetrieveResponseVO<T extends ValueObject> extends ValueObject {

  private String version;
  private int numberOfRecords;
  private List<SearchRetrieveRecordVO<T>> records;
  private SearchResponse originalResponse;



  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public int getNumberOfRecords() {
    return numberOfRecords;
  }

  public void setNumberOfRecords(int numberOfRecords) {
    this.numberOfRecords = numberOfRecords;
  }

  public List<SearchRetrieveRecordVO<T>> getRecords() {
    return records;
  }

  public void setRecords(List<SearchRetrieveRecordVO<T>> records) {
    this.records = records;
  }

  public SearchResponse getOriginalResponse() {
    return originalResponse;
  }

  public void setOriginalResponse(SearchResponse originalResponse) {
    this.originalResponse = originalResponse;
  }


}
