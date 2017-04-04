package de.mpg.mpdl.inge.model.valueobjects;

import java.util.List;
import java.util.Set;

public class SearchRetrieveRequestVO<QueryObject> extends ValueObject {

  private QueryObject queryObject;

  // use -1 for limit set by property (currently 10000)
  private int limit = -1;

  private int offset = 0;

  private SearchSortCriteria[] sortKeys;

  public SearchRetrieveRequestVO(QueryObject queryObject, int limit, int offset,
      SearchSortCriteria... sortKeys) {
    super();
    this.queryObject = queryObject;
    this.limit = limit;
    this.offset = offset;
    this.sortKeys = sortKeys;
  }


  public SearchRetrieveRequestVO(QueryObject queryObject, SearchSortCriteria... sortKeys) {
    this(queryObject, -1, 0, sortKeys);
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



  public QueryObject getQueryObject() {
    return queryObject;
  }

  public void setQueryObject(QueryObject queryObject) {
    this.queryObject = queryObject;
  }



}
