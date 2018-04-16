package de.mpg.mpdl.inge.model.valueobjects;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;

@SuppressWarnings("serial")
public class SearchAndExportRetrieveRequestVO extends ValueObject {


  private SearchRetrieveRequestVO searchRetrieveRequestVO;
  private SearchRetrieveResponseVO<ItemVersionVO> searchRetrieveReponseVO;
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

  public SearchAndExportRetrieveRequestVO(SearchRetrieveResponseVO<ItemVersionVO> searchRetrieveResponseVO, String exportFormatName,
      String outputFormatName, String cslConeId) {
    this.exportFormatName = exportFormatName;
    this.outputFormatName = outputFormatName;
    this.cslConeId = cslConeId;
    this.searchRetrieveReponseVO = searchRetrieveResponseVO;
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

  public SearchRetrieveResponseVO<ItemVersionVO> getSearchRetrieveReponseVO() {
    return this.searchRetrieveReponseVO;
  }

  public void setSearchRetrieveReponseVO(SearchRetrieveResponseVO<ItemVersionVO> searchRetrieveReponseVO) {
    this.searchRetrieveReponseVO = searchRetrieveReponseVO;
  }
}
