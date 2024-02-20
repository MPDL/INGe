package de.mpg.mpdl.inge.model.valueobjects;

import java.util.ArrayList;
import java.util.List;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

@SuppressWarnings("serial")
public class SearchRetrieveRequestVO extends ValueObject {

  private Query queryBuilder;

  private final List<Aggregation> aggregationBuilders = new ArrayList<>();

  // use -1 for default limit set by property (currently 100)
  // use -2 for max limit set by property (currently 10000)
  private int limit = -1;

  private int offset = 0;

  private SearchSortCriteria[] sortKeys;

  //The scrolltime in ms. -1 for no scrolling
  private long scrollTime = -1;

  public SearchRetrieveRequestVO() {}

  //  public SearchRetrieveRequestVO(AggregationBuilder aggBuilder) {
  //    this.aggregationBuilders.add(aggBuilder);
  //  }

  public SearchRetrieveRequestVO(Query queryBuilder, int limit, int offset, SearchSortCriteria... sortKeys) {
    this.queryBuilder = queryBuilder;
    this.limit = limit;
    this.offset = offset;
    this.sortKeys = sortKeys;
  }

  public SearchRetrieveRequestVO(Query queryBuilder, SearchSortCriteria... sortKeys) {
    this(queryBuilder, -1, 0, sortKeys);
  }

  public int getLimit() {
    return this.limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getOffset() {
    return this.offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public SearchSortCriteria[] getSortKeys() {
    return this.sortKeys;
  }

  public void setSortKeys(SearchSortCriteria[] sortKeys) {
    this.sortKeys = sortKeys;
  }

  public Query getQueryBuilder() {
    return this.queryBuilder;
  }

  public void setQueryBuilder(Query queryBuilder) {
    this.queryBuilder = queryBuilder;
  }

  public List<Aggregation> getAggregationBuilders() {
    return this.aggregationBuilders;
  }

  //  public void setAggregationBuilders(List<AggregationBuilder> aggregationBuilders) {
  //    this.aggregationBuilders = aggregationBuilders;
  //  }

  public long getScrollTime() {
    return this.scrollTime;
  }

  public void setScrollTime(long scrollTime) {
    this.scrollTime = scrollTime;
  }

}
