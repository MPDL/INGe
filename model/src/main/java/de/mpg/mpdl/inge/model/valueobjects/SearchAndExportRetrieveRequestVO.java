package de.mpg.mpdl.inge.model.valueobjects;

@SuppressWarnings("serial")
public class SearchAndExportRetrieveRequestVO extends ValueObject {

  private SearchRetrieveRequestVO searchRetrieveRequestVO;
  private String exportFormatName;
  private String outputFormatName;
  private String cslConeId;

  public SearchAndExportRetrieveRequestVO(SearchRetrieveRequestVO searchRetrieveRequestVO, String exportFormatName, String outputFormatName,
      String cslConeId) {
    this.exportFormatName = exportFormatName;
    this.outputFormatName = outputFormatName;
    this.cslConeId = cslConeId;
    this.searchRetrieveRequestVO = searchRetrieveRequestVO;
  }

  public String getExportFormatName() {
    return this.exportFormatName;
  }

  public String getOutputFormat() {
    return this.outputFormatName;
  }

  public String getCslConeId() {
    return this.cslConeId;
  }

  public SearchRetrieveRequestVO getSearchRetrieveRequestVO() {
    return this.searchRetrieveRequestVO;
  }
}
