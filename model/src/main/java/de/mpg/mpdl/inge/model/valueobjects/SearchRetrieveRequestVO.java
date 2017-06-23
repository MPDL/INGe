package de.mpg.mpdl.inge.model.valueobjects;

import org.elasticsearch.index.query.QueryBuilder;

public class SearchRetrieveRequestVO extends ValueObject {

  private QueryBuilder queryBuilder;

  // use -1 for limit set by property (currently 10000)
  private int limit = -1;

  private int offset = 0;

  private SearchSortCriteria[] sortKeys;

  public SearchRetrieveRequestVO() {

  }

  public SearchRetrieveRequestVO(QueryBuilder queryBuilder, int limit, int offset,
      SearchSortCriteria... sortKeys) {
    super();
    this.setQueryBuilder(queryBuilder);
    this.limit = limit;
    this.offset = offset;
    this.sortKeys = sortKeys;
  }


  public SearchRetrieveRequestVO(QueryBuilder queryBuilder, SearchSortCriteria... sortKeys) {
    this(queryBuilder, -1, 0, sortKeys);
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public SearchSortCriteria[] getSortKeys() {
    return sortKeys;
  }

  public void setSortKeys(SearchSortCriteria[] sortKeys) {
    this.sortKeys = sortKeys;
  }

  public QueryBuilder getQueryBuilder() {
    return queryBuilder;
  }

  public void setQueryBuilder(QueryBuilder queryBuilder) {
    this.queryBuilder = queryBuilder;
  }



}
