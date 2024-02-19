package de.mpg.mpdl.inge.model.valueobjects;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import co.elastic.clients.elasticsearch.core.search.ResponseBody;

@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class SearchRetrieveResponseVO<T> extends ValueObject {

  private String version;
  private int numberOfRecords;
  private String scrollId;
  private List<SearchRetrieveRecordVO<T>> records;

  @JsonIgnore
  private ResponseBody<T> originalResponse;



  public String getVersion() {
    return this.version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public int getNumberOfRecords() {
    return this.numberOfRecords;
  }

  public void setNumberOfRecords(int numberOfRecords) {
    this.numberOfRecords = numberOfRecords;
  }

  public List<SearchRetrieveRecordVO<T>> getRecords() {
    return this.records;
  }

  public void setRecords(List<SearchRetrieveRecordVO<T>> records) {
    this.records = records;
  }

  public ResponseBody<T> getOriginalResponse() {
    return this.originalResponse;
  }

  public void setOriginalResponse(ResponseBody<T> originalResponse) {
    this.originalResponse = originalResponse;
  }

  public String getScrollId() {
    return this.scrollId;
  }

  public void setScrollId(String scrollId) {
    this.scrollId = scrollId;
  }


}
