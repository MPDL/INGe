package de.mpg.mpdl.inge.model.valueobjects;

import org.elasticsearch.index.query.QueryBuilder;

@SuppressWarnings("serial")
public class SearchAndExportRetrieveRequestVO extends ValueObject {

  private String exportFormatName;
  private String outputFormatName;
  private String cslConeId;
  private SearchRetrieveRequestVO searchRetrieveRequestVO;

  public SearchAndExportRetrieveRequestVO(String exportFormatName, String outputFormatName, String cslConeId, QueryBuilder queryBuilder,
      int limit, int offset, SearchSortCriteria... sortKeys) {
    this.exportFormatName = exportFormatName;
    this.outputFormatName = outputFormatName;
    this.cslConeId = cslConeId;
    this.searchRetrieveRequestVO = new SearchRetrieveRequestVO(queryBuilder, limit, offset, sortKeys);
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
