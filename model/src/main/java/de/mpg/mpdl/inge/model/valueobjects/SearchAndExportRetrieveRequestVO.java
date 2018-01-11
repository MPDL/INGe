package de.mpg.mpdl.inge.model.valueobjects;

import org.elasticsearch.index.query.QueryBuilder;

@SuppressWarnings("serial")
public class SearchAndExportRetrieveRequestVO extends ValueObject {

  private String exportFormat;
  private String outputFormat;
  private String cslConeId;
  private SearchRetrieveRequestVO searchRetrieveRequestVO;

  public SearchAndExportRetrieveRequestVO(String exportFormat, String outputFormat, String cslConeId, QueryBuilder queryBuilder, int limit,
      int offset, SearchSortCriteria... sortKeys) {
    this.exportFormat = exportFormat;
    this.outputFormat = outputFormat;
    this.cslConeId = cslConeId;
    this.searchRetrieveRequestVO = new SearchRetrieveRequestVO(queryBuilder, limit, offset, sortKeys);
  }

  public String getExportFormat() {
    return this.exportFormat;
  }

  public String getOutputFormat() {
    return this.outputFormat;
  }

  public String getCslConeId() {
    return this.cslConeId;
  }

  public SearchRetrieveRequestVO getSearchRetrieveRequestVO() {
    return this.searchRetrieveRequestVO;
  }
}
