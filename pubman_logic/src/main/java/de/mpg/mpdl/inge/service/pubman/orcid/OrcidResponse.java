package de.mpg.mpdl.inge.service.pubman.orcid;

import org.json.JSONObject;

public class OrcidResponse {

  public enum Type
  {
    WORK, ERROR
  }

  private final Type type;
  private final int index;
  private final JSONObject rawJson;

  private Long putCode;
  private Integer responseCode;
  private Integer errorCode;
  private String userMessage;
  private String developerMessage;
  private String moreInfo;

  public OrcidResponse(Type type, int index, JSONObject rawJson) {
    this.type = type;
    this.index = index;
    this.rawJson = rawJson;
  }

  public boolean isWork() {
    return this.type == Type.WORK;
  }

  public boolean isError() {
    return this.type == Type.ERROR;
  }

  public Type getType() {
    return this.type;
  }

  public int getIndex() {
    return this.index;
  }

  public JSONObject getRawJson() {
    return this.rawJson;
  }

  public Long getPutCode() {
    return this.putCode;
  }

  public void setPutCode(Long putCode) {
    this.putCode = putCode;
  }

  public Integer getResponseCode() {
    return this.responseCode;
  }

  public void setResponseCode(Integer responseCode) {
    this.responseCode = responseCode;
  }

  public Integer getErrorCode() {
    return this.errorCode;
  }

  public void setErrorCode(Integer errorCode) {
    this.errorCode = errorCode;
  }

  public String getUserMessage() {
    return this.userMessage;
  }

  public void setUserMessage(String userMessage) {
    this.userMessage = userMessage;
  }

  public String getDeveloperMessage() {
    return this.developerMessage;
  }

  public void setDeveloperMessage(String developerMessage) {
    this.developerMessage = developerMessage;
  }

  public String getMoreInfo() {
    return this.moreInfo;
  }

  public void setMoreInfo(String moreInfo) {
    this.moreInfo = moreInfo;
  }
}
