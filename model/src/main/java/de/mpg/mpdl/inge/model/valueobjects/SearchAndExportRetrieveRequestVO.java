package de.mpg.mpdl.inge.model.valueobjects;

@SuppressWarnings("serial")
public class SearchAndExportRetrieveRequestVO extends ValueObject {


  private SearchRetrieveRequestVO searchRetrieveRequestVO;
  //private SearchRetrieveResponseVO<ItemVersionVO> searchRetrieveReponseVO;
  private ExportFormatVO exportFormat;

  public SearchAndExportRetrieveRequestVO(SearchRetrieveRequestVO searchRetrieveRequestVO, ExportFormatVO exportFormat) {
    this.exportFormat = exportFormat;
    this.searchRetrieveRequestVO = searchRetrieveRequestVO;
  }

  /*
  public SearchAndExportRetrieveRequestVO(SearchRetrieveResponseVO<ItemVersionVO> searchRetrieveResponseVO, ExportFormatVO exportFormat) {
    this.exportFormat = exportFormat;
    this.searchRetrieveReponseVO = searchRetrieveResponseVO;
  }

   */


  public SearchRetrieveRequestVO getSearchRetrieveRequestVO() {
    return this.searchRetrieveRequestVO;
  }

  /*
  public SearchRetrieveResponseVO<ItemVersionVO> getSearchRetrieveReponseVO() {
    return this.searchRetrieveReponseVO;
  }

  public void setSearchRetrieveReponseVO(SearchRetrieveResponseVO<ItemVersionVO> searchRetrieveReponseVO) {
    this.searchRetrieveReponseVO = searchRetrieveReponseVO;
  }

   */

  public ExportFormatVO getExportFormat() {
    return this.exportFormat;
  }

  public void setExportFormat(ExportFormatVO exportFormat) {
    this.exportFormat = exportFormat;
  }
}
