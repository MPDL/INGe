package de.mpg.mpdl.inge.service.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.json.util.JsonObjectMapperFactory;

public class SearchUtils {

  public static QueryBuilder baseElasticSearchQueryBuilder(
      Map<String, ElasticSearchIndexField> indexMap, String[] indexFields, String... searchString) {


    if (indexFields.length == 1) {


      return baseElasticSearchQueryBuilder(indexMap, indexFields[0], searchString);

    } else {

      BoolQueryBuilder bq = QueryBuilders.boolQuery();

      for (String indexField : indexFields) {
        bq.must(baseElasticSearchQueryBuilder(indexMap, indexField, searchString));
      }
      return bq;

    }
  }


  public static QueryBuilder baseElasticSearchQueryBuilder(
      Map<String, ElasticSearchIndexField> indexMap, String index, String... value) {

    ElasticSearchIndexField field = indexMap.get(index);

    switch (field.getType()) {
      case TEXT: {
        if (value.length == 1) {
          return QueryBuilders.matchQuery(index, value[0]);
        } else {
          BoolQueryBuilder bq = QueryBuilders.boolQuery();
          for (String searchString : value) {
            bq.should(QueryBuilders.matchQuery(index, searchString));
          }
          return bq;
        }

      }
      default: {
        if (value.length == 1) {
          return QueryBuilders.termQuery(index, value[0]);
        } else {
          BoolQueryBuilder bq = QueryBuilders.boolQuery();
          for (String searchString : value) {
            bq.should(QueryBuilders.termQuery(index, searchString));
          }
          return bq;
        }

      }


    }
  }

  public static <E> List<E> getSearchRetrieveResponseFromElasticSearchResponse(SearchResponse sr,
      Class<E> clazz) throws IOException {
    List<E> hitList = new ArrayList<>();
    for (SearchHit hit : sr.getHits().getHits()) {

      E itemVO =
          JsonObjectMapperFactory.getObjectMapper().readValue(hit.getSourceAsString(), clazz);
      hitList.add(itemVO);

    }
    return hitList;
  }



}
