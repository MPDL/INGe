package de.mpg.mpdl.inge.model.valueobjects;

@SuppressWarnings("serial")
public class SearchAndExportResultVO extends ValueObject {

  private byte[] result;
  private String targetFormat;

  public SearchAndExportResultVO(byte[] result, String targetFormat) {
    this.result = result;
    this.targetFormat = targetFormat;
  }

  public byte[] getResult() {
    return this.result;
  }

  public String getTargetFormat() {
    return this.targetFormat;
  }

}
