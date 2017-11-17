package de.mpg.mpdl.inge.service.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.json.util.JsonObjectMapperFactory;

public class SearchUtils {

  private final static Logger logger = Logger.getLogger(SearchUtils.class);

  public static QueryBuilder baseElasticSearchQueryBuilder(
      Map<String, ElasticSearchIndexField> indexMap, String[] indexFields, String... searchString) {


    if (indexFields.length == 1) {


      return baseElasticSearchQueryBuilder(indexMap, indexFields[0], searchString);

    } else {

      BoolQueryBuilder bq = QueryBuilders.boolQuery();

      for (String indexField : indexFields) {
        bq.should(baseElasticSearchQueryBuilder(indexMap, indexField, searchString));
      }
      return bq;

    }
  }


  public static QueryBuilder baseElasticSearchQueryBuilder(
      Map<String, ElasticSearchIndexField> indexMap, String index, String... value) {

    ElasticSearchIndexField field = indexMap.get(index);

    if (field != null) {
      switch (field.getType()) {
        case TEXT: {
          if (value.length == 1) {
            return QueryBuilders.matchQuery(index, value[0]);
          } else {
            BoolQueryBuilder bq = QueryBuilders.boolQuery();
            for (String searchString : value) {
              bq.should(QueryBuilders.matchQuery(index, searchString).operator(Operator.AND));
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

    } else {
      logger.warn("Index field " + index + " not found");
      return null;
    }

  }

  public static FieldSortBuilder baseElasticSearchSortBuilder(
      Map<String, ElasticSearchIndexField> indexMap, String index, SortOrder order) {

    ElasticSearchIndexField field = indexMap.get(index);
    String indexField = index;

    if (field != null) {
      switch (field.getType()) {
        case TEXT: {
          indexField += ".sorted";
          break;

        }

      }
    } else {
      logger.warn("Index field " + index + " not found");
    }

    return SortBuilders.fieldSort(indexField).order(order);
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
