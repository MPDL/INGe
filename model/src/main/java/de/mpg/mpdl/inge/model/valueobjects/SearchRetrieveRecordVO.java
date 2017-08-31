package de.mpg.mpdl.inge.model.valueobjects;


public class SearchRetrieveRecordVO<T> extends ValueObject {

  private String schema;
  private String packing;
  private int position;

  private T data;

  private String persistenceId;

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public String getPacking() {
    return packing;
  }

  public void setPacking(String packing) {
    this.packing = packing;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getPersistenceId() {
    return persistenceId;
  }

  public void setPersistenceId(String persistenceId) {
    this.persistenceId = persistenceId;
  }
}
