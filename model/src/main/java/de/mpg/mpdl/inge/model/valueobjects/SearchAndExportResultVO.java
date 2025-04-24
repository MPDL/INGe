package de.mpg.mpdl.inge.model.valueobjects;

@SuppressWarnings("serial")
public class SearchAndExportResultVO extends ValueObject {

  private final byte[] result;
  private final String fileName;
  private final String targetMimeType;
  private final int totalNumberOfRecords;
  private SearchRetrieveResponseVO searchRetrieveResponseVO;


  public SearchAndExportResultVO(byte[] result, String fileName, String targetMimeType, int totalNumberOfRecords) {
    this.result = result;
    this.fileName = fileName;
    this.targetMimeType = targetMimeType;
    this.totalNumberOfRecords = totalNumberOfRecords;
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

  public int getTotalNumberOfRecords() {
    return this.totalNumberOfRecords;
  }

  public SearchRetrieveResponseVO getSearchRetrieveResponseVO() {
    return searchRetrieveResponseVO;
  }

  public void setSearchRetrieveResponseVO(SearchRetrieveResponseVO searchRetrieveResponseVO) {
    this.searchRetrieveResponseVO = searchRetrieveResponseVO;
  }
}
