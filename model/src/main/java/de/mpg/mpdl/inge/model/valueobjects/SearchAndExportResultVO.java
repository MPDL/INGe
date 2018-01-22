package de.mpg.mpdl.inge.model.valueobjects;

@SuppressWarnings("serial")
public class SearchAndExportResultVO extends ValueObject {

  private byte[] result;
  private String fileName;
  private String targetMimeType;

  public SearchAndExportResultVO(byte[] result, String fileName, String targetMimeType) {
    this.result = result;
    this.fileName = fileName;
    this.targetMimeType = targetMimeType;
  }

  public byte[] getResult() {
    return this.result;
  }

  public String getFileName() {
    return this.fileName;
  }

  public String getTargetMimetype() {
    return this.targetMimeType;
  }
}
