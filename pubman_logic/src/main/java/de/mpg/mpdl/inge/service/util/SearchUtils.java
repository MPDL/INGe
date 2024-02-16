package de.mpg.mpdl.inge.service.util;

import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.NestedSortValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.es.dao.impl.ElasticSearchGenericDAOImpl;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;

public class SearchUtils {

  private static final Logger logger = Logger.getLogger(SearchUtils.class);

  public static Query baseElasticSearchQueryBuilder(Map<String, ElasticSearchIndexField> indexMap, String[] indexFields,
      String... searchString) throws IngeTechnicalException {


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


  public static Query baseElasticSearchQueryBuilder(Map<String, ElasticSearchIndexField> indexMap, String index, String... value)
      throws IngeTechnicalException {

    ElasticSearchIndexField field = indexMap.get(index);

    if (null == field) {
      throw new IngeTechnicalException("Index field " + index + " not found");
    }

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
          Arrays.stream(value).map(FieldValue::of).collect(Collectors.toList());
          return TermsQuery.of(t -> t.field(index).terms(te -> te.value(fvList)))._toQuery();
        }
      }
    }
  }

  private static Query checkMatchOrPhraseOrWildcardMatch(String index, String searchString) {
    if (searchString != null && searchString.trim().startsWith("\"") && searchString.trim().endsWith("\"")) {
      return MatchPhraseQuery.of(mp -> mp.field(index).query(searchString.trim().substring(1, searchString.length() - 1)))._toQuery();
    } else if (searchString != null && searchString.contains("*")) {
      return WildcardQuery.of(wq -> wq.field(index + ".keyword").value(searchString))._toQuery();
    } else {
      return MatchQuery.of(i -> i.field(index).query(searchString).operator(Operator.And))._toQuery();
    }
  }

  public static FieldSort baseElasticSearchSortBuilder(Map<String, ElasticSearchIndexField> indexMap, String index, SortOrder order)
      throws IngeTechnicalException {
    ElasticSearchIndexField field = indexMap.get(index);

    if (null == field) {
      throw new IngeTechnicalException("Index field " + index + " not found");
    }

    String indexField = index;
    switch (field.getType()) {
      case TEXT: {
        indexField += ".keyword";
        break;
      }
    }

    FieldSort fieldSort = null;
    String finalIndexField = indexField;
    List<String> nestedPaths = field.getNestedPaths();

    if (nestedPaths == null) {
      fieldSort = FieldSort.of(fs -> fs.field(finalIndexField).order(order));
    } else {
      NestedSortValue nestedSortValue = NestedSortValue.of(nsv -> nsv.path(String.join(".", nestedPaths)));
      fieldSort = FieldSort.of(fs -> fs.field(finalIndexField).order(order).nested(nestedSortValue));
    }

    return fieldSort;
  }

  public static <E> List<E> getRecordListFromElasticSearchResponse(ResponseBody<E> sr, Class<E> clazz) throws IOException {
    List<E> hitList = new ArrayList<>();
    for (Hit<E> hit : sr.hits().hits()) {
      //E itemVO = hit.source();
      //MapperFactory.getObjectMapper().readValue(hit.getSourceAsString(), clazz);
      hitList.add(ElasticSearchGenericDAOImpl.getVoFromResponseObject(hit.source(), clazz));
    }

    return hitList;
  }


  public static <E> SearchRetrieveResponseVO<E> getSearchRetrieveResponseFromElasticSearchResponse(ResponseBody sr, Class<E> clazz)
      throws IOException {
    return ElasticSearchGenericDAOImpl.getSearchRetrieveResponseFromElasticSearchResponse(sr, clazz);
  }

  public static <E> List<E> getRecordListFromSearchRetrieveResponse(SearchRetrieveResponseVO<E> srr, Class<E> clazz) {
    return srr.getRecords().stream().map(SearchRetrieveRecordVO::getData).collect(Collectors.toList());
  }
}
