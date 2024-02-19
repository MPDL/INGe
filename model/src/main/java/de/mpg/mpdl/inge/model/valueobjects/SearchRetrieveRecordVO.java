package de.mpg.mpdl.inge.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class SearchRetrieveRecordVO<T> extends ValueObject {

  private String schema;
  private String packing;

  @JsonIgnore
  private int position;

  private T data;

  private String persistenceId;

  public String getSchema() {
    return this.schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public String getPacking() {
    return this.packing;
  }

  public void setPacking(String packing) {
    this.packing = packing;
  }

  public int getPosition() {
    return this.position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public T getData() {
    return this.data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getPersistenceId() {
    return this.persistenceId;
  }

  public void setPersistenceId(String persistenceId) {
    this.persistenceId = persistenceId;
  }
}
