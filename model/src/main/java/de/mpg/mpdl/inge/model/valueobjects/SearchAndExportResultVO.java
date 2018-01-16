package de.mpg.mpdl.inge.model.valueobjects;

@SuppressWarnings("serial")
public class SearchAndExportResultVO extends ValueObject {

  private byte[] result;
  private String targetMimeType;

  public SearchAndExportResultVO(byte[] result, String targetMimeType) {
    this.result = result;
    this.targetMimeType = targetMimeType;
  }

  public byte[] getResult() {
    return this.result;
  }

  public String getTargetMimetype() {
    return this.targetMimeType;
  }

}
