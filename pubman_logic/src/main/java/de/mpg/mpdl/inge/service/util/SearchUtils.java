package de.mpg.mpdl.inge.service.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import de.mpg.mpdl.inge.es.dao.impl.ElasticSearchGenericDAOImpl;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;

public class SearchUtils {

  private final static Logger logger = Logger.getLogger(SearchUtils.class);

  public static QueryBuilder baseElasticSearchQueryBuilder(Map<String, ElasticSearchIndexField> indexMap, String[] indexFields,
      String... searchString) {


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


  public static QueryBuilder baseElasticSearchQueryBuilder(Map<String, ElasticSearchIndexField> indexMap, String index, String... value) {

    ElasticSearchIndexField field = indexMap.get(index);

    if (field != null) {
      switch (field.getType()) {
        case TEXT: {
          if (value.length == 1) {
            return checkMatchOrPhraseMatch(index, value[0]);
          } else {
            BoolQueryBuilder bq = QueryBuilders.boolQuery();
            for (String searchString : value) {
              bq.should(checkMatchOrPhraseMatch(index, searchString));
            }
            return bq;
          }

        }
        default: {
          if (value.length == 1) {
            return QueryBuilders.termQuery(index, value[0]);
          } else {
            return QueryBuilders.termsQuery(index, value);
          }

        }
      }

    } else {
      logger.warn("Index field " + index + " not found");
      return null;
    }

  }


  private static QueryBuilder checkMatchOrPhraseMatch(String index, String searchString) {
    if (searchString != null && searchString.trim().startsWith("\"") && searchString.trim().endsWith("\"")) {
      return QueryBuilders.matchPhraseQuery(index, searchString.trim().substring(1, searchString.length() - 1));
    } else {
      return QueryBuilders.matchQuery(index, searchString).operator(Operator.AND);
    }

  }

  public static FieldSortBuilder baseElasticSearchSortBuilder(Map<String, ElasticSearchIndexField> indexMap, String index,
      SortOrder order) {

    ElasticSearchIndexField field = indexMap.get(index);
    String indexField = index;

    if (field != null) {
      switch (field.getType()) {
        case TEXT: {
          indexField += ".keyword";
          break;

        }

      }
    } else {
      logger.warn("Index field " + index + " not found");
    }

    return SortBuilders.fieldSort(indexField).order(order);
  }

  public static <E> List<E> getRecordListFromElasticSearchResponse(SearchResponse sr, Class<E> clazz) throws IOException {
    List<E> hitList = new ArrayList<>();
    for (SearchHit hit : sr.getHits().getHits()) {

      E itemVO = MapperFactory.getObjectMapper().readValue(hit.getSourceAsString(), clazz);
      hitList.add(itemVO);

    }
    return hitList;
  }


  public static <E> SearchRetrieveResponseVO<E> getSearchRetrieveResponseFromElasticSearchResponse(SearchResponse sr, Class<E> clazz)
      throws IOException {
    return ElasticSearchGenericDAOImpl.getSearchRetrieveResponseFromElasticSearchResponse(sr, clazz);
  }

  public static <E> List<E> getRecordListFromSearchRetrieveResponse(SearchRetrieveResponseVO<E> srr, Class<E> clazz) throws IOException {
    return srr.getRecords().stream().map(i -> i.getData()).collect(Collectors.toList());
  }



}
