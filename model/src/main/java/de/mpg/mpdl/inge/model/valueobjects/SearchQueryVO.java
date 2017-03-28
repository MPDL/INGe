package de.mpg.mpdl.inge.model.valueobjects;

import java.util.List;
import java.util.Set;

public class SearchQueryVO<QueryObject> extends ValueObject {

  private QueryObject queryObject;

  private int limit;

  private int offset;

  public SearchQueryVO(QueryObject queryObject, int limit, int offset,
      List<SearchSortCriteria> sortKeys) {
    super();
    this.queryObject = queryObject;
    this.limit = limit;
    this.offset = offset;
    this.sortKeys = sortKeys;
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

  public List<SearchSortCriteria> getSortKeys() {
    return sortKeys;
  }

  public void setSortKeys(List<SearchSortCriteria> sortKeys) {
    this.sortKeys = sortKeys;
  }

  private List<SearchSortCriteria> sortKeys;

  public QueryObject getQueryObject() {
    return queryObject;
  }

  public void setQueryObject(QueryObject queryObject) {
    this.queryObject = queryObject;
  }



}
