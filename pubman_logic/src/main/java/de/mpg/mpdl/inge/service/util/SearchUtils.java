package de.mpg.mpdl.inge.service.util;

import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.es.dao.impl.ElasticSearchGenericDAOImpl;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchUtils {

  private static final Logger logger = Logger.getLogger(SearchUtils.class);

  public static Query baseElasticSearchQueryBuilder(Map<String, ElasticSearchIndexField> indexMap, String[] indexFields,
      String... searchString) {


    if (indexFields.length == 1) {


      return baseElasticSearchQueryBuilder(indexMap, indexFields[0], searchString);

    } else {

      BoolQuery.Builder bq = new BoolQuery.Builder();

      for (String indexField : indexFields) {
        bq.should(baseElasticSearchQueryBuilder(indexMap, indexField, searchString));
      }
      return bq.build()._toQuery();

    }
  }


  public static Query baseElasticSearchQueryBuilder(Map<String, ElasticSearchIndexField> indexMap, String index, String... value) {

    ElasticSearchIndexField field = indexMap.get(index);

    if (field != null) {
      switch (field.getType()) {
        case TEXT: {
          if (value.length == 1) {
            return checkMatchOrPhraseOrWildcardMatch(index, value[0]);
          } else {
            BoolQuery.Builder bq = new BoolQuery.Builder();
            for (String searchString : value) {
              bq.should(checkMatchOrPhraseOrWildcardMatch(index, searchString));
            }
            return bq.build()._toQuery();
          }

        }
        default: {
          if (value.length == 1) {
            return TermQuery.of(t -> t.field(index).value(value[0]))._toQuery();
          } else {
            List<FieldValue> fvList = new ArrayList<>();
            Arrays.stream(value).map(i -> FieldValue.of(i)).collect(Collectors.toList());
            return TermsQuery.of(t -> t.field(index).terms(te -> te.value(fvList)) )._toQuery();
          }

        }
      }

    } else {
      logger.warn("Index field " + index + " not found");
      return null;
    }

  }


  private static Query checkMatchOrPhraseOrWildcardMatch(String index, String searchString) {
    if (searchString != null && searchString.trim().startsWith("\"") && searchString.trim().endsWith("\"")) {
      return MatchPhraseQuery.of(mp -> mp
              .field(index)
              .query(searchString.trim().substring(1, searchString.length() - 1)))._toQuery();
    } else if (searchString != null && searchString.contains("*")) {
      return WildcardQuery.of(wq -> wq
                  .field(index + ".keyword")
                  .value(searchString))._toQuery();
    } else {
      return MatchQuery.of(i-> i.field(index).query(searchString).operator(Operator.And))._toQuery();
    }

  }

  public static FieldSort baseElasticSearchSortBuilder(Map<String, ElasticSearchIndexField> indexMap, String index,
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

    String finalIndexField = indexField;
    FieldSort fieldSort = FieldSort.of(fs -> fs.field(finalIndexField).order(order));
    return fieldSort;
  }

  public static <E> List<E> getRecordListFromElasticSearchResponse(ResponseBody<E> sr, Class<E> clazz) throws IOException {
    List<E> hitList = new ArrayList<>();
    for (Hit<E> hit : sr.hits().hits()) {

      E itemVO = hit.source();
      //MapperFactory.getObjectMapper().readValue(hit.getSourceAsString(), clazz);
      hitList.add(itemVO);

    }
    return hitList;
  }


  public static <E> SearchRetrieveResponseVO<E> getSearchRetrieveResponseFromElasticSearchResponse(ResponseBody sr, Class<E> clazz)
      throws IOException {
    return ElasticSearchGenericDAOImpl.getSearchRetrieveResponseFromElasticSearchResponse(sr, clazz);
  }

  public static <E> List<E> getRecordListFromSearchRetrieveResponse(SearchRetrieveResponseVO<E> srr, Class<E> clazz) throws IOException {
    return srr.getRecords().stream().map(i -> i.getData()).collect(Collectors.toList());
  }



}
