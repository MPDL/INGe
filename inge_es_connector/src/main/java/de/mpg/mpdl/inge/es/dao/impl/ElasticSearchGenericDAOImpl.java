package de.mpg.mpdl.inge.es.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.NestedSortValue;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.ClearScrollRequest;
import co.elastic.clients.elasticsearch.core.ClearScrollResponse;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.SourceFilter;
import co.elastic.clients.elasticsearch.core.search.TrackHits;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.elasticsearch.indices.get_alias.IndexAliases;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.JsonpSerializable;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import de.mpg.mpdl.inge.es.connector.ElasticSearchClientProvider;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;

/**
 * ElasticSearchClient enables elasticsearch accessibility
 *
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class ElasticSearchGenericDAOImpl<E> implements GenericDaoEs<E> {

  private static final Logger logger = LogManager.getLogger(ElasticSearchGenericDAOImpl.class);

  @Autowired
  protected ElasticSearchClientProvider client;

  protected final ObjectMapper mapper = MapperFactory.getObjectMapper();

  protected final String indexName;

  protected final String indexType;

  protected final Class<E> typeParameterClass;

  private static final int DEFAULT_SEARCH_SIZE = 100;
  private static final int MAX_SEARCH_SIZE = 10000;

  private final ObjectMapper objectMapper = new ObjectMapper();


  public ElasticSearchGenericDAOImpl(String indexName, String indexType, Class<E> typeParameterClass) {
    this.indexName = indexName;
    this.indexType = indexType;
    this.typeParameterClass = typeParameterClass;
  }


  protected JsonNode applyCustomValues(E entity) {
    JsonNode node = this.mapper.valueToTree(entity);
    return node;
  }

  protected abstract String[] getSourceExclusions();



  public void create(String id, E entity) throws IngeTechnicalException {
    try {

      IndexResponse indexResponse = this.client.getClient().index(i -> i.index(this.indexName).id(id).document(applyCustomValues(entity))

      );

    } catch (IOException e) {
      throw new IngeTechnicalException(e);
    }

  }


  public String createImmediately(String id, E entity) throws IngeTechnicalException {
    try {

      IndexResponse indexResponse =
          this.client.getClient().index(i -> i.index(this.indexName).id(id).refresh(Refresh.True).document(applyCustomValues(entity))

          );

      return indexResponse.id();

    } catch (IOException e) {
      throw new IngeTechnicalException(e);
    }

  }


  public E get(String id) throws IngeTechnicalException {

    try {
      GetResponse<E> getResponse = this.client.getClient().get(g -> g.index(this.indexName).id(id), this.typeParameterClass);
      return getResponse.source();

    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }
  }

  public String updateImmediately(String id, E entity) throws IngeTechnicalException {

    try {
      UpdateResponse updateResponse = this.client.getClient()
          .update(u -> u.index(this.indexName).id(id).refresh(Refresh.True).doc(applyCustomValues(entity)), this.typeParameterClass

      );
      return Long.toString(updateResponse.version());
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }
  }

  public String update(String id, E entity) throws IngeTechnicalException {

    try {
      UpdateResponse updateResponse =
          this.client.getClient().update(u -> u.index(this.indexName).id(id).doc(applyCustomValues(entity)), this.typeParameterClass

          );
      return Long.toString(updateResponse.version());
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }
  }


  /**
   * @param id
   * @return {@link String}
   */
  public String deleteImmediatly(String id) throws IngeTechnicalException {
    try {
      DeleteResponse deleteResponse = this.client.getClient().delete(d -> d.index(this.indexName).refresh(Refresh.True).id(id));
      return deleteResponse.id();
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }


  }

  public void delete(String id) throws IngeTechnicalException {

    try {
      DeleteResponse deleteResponse = this.client.getClient().delete(d -> d.index(this.indexName).id(id));
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }

  }


  public long deleteByQuery(Query query) throws IngeTechnicalException {

    try {
      DeleteByQueryResponse deleteResponse = this.client.getClient().deleteByQuery(d -> d.index(this.indexName).query(query));
      return deleteResponse.deleted();
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }
  }

  /**
   * Use maxDocs <=1000 in order to disable scrolling for delete-by-query
   */
  public void deleteByQuery(Query query, int maxDocs) throws IngeTechnicalException {

    try {
      DeleteByQueryResponse deleteResponse =
          this.client.getClient().deleteByQuery(d -> d.index(this.indexName).query(query).maxDocs((long) maxDocs));
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }
  }


  public void clearScroll(String scrollId) throws IngeTechnicalException {

    try {
      ClearScrollResponse resp = this.client.getClient().clearScroll(ClearScrollRequest.of(cs -> cs.scrollId(scrollId)));

    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }
  }


  public SearchRetrieveResponseVO<E> search(Map<String, ElasticSearchIndexField> indexMap, SearchRetrieveRequestVO searchQuery)
      throws IngeTechnicalException {

    SearchRetrieveResponseVO<E> srrVO;
    try {
      SearchRequest.Builder sr = new SearchRequest.Builder();

      //Set track_total_hits to true in order to retrieve correct total numbers > 10000
      sr.trackTotalHits(TrackHits.of(i -> i.enabled(true)));

      sr.index(this.indexName);

      if (null != searchQuery.getQueryBuilder()) {
        sr.query(searchQuery.getQueryBuilder());
      }

      if (null != searchQuery.getAggregationBuilders()) {
        int i = 0;
        for (Aggregation aggBuilder : searchQuery.getAggregationBuilders()) {
          sr.aggregations("agg" + i, aggBuilder);
        }
      }


      if (0 != searchQuery.getOffset()) {
        sr.from(searchQuery.getOffset());
      }

      if (-2 == searchQuery.getLimit()) {
        sr.size(ElasticSearchGenericDAOImpl.MAX_SEARCH_SIZE);
      } else if (-1 == searchQuery.getLimit()) {
        sr.size(ElasticSearchGenericDAOImpl.DEFAULT_SEARCH_SIZE);
      } else {
        sr.size(searchQuery.getLimit());
      }

      if (null != searchQuery.getSortKeys()) {
        FieldSort fieldSort = null;
        for (SearchSortCriteria sc : searchQuery.getSortKeys()) {
          //          FieldSort fs = FieldSort.of(f -> f.field(sc.getIndexField())
          //              .order(sc.getSortOrder().equals(SearchSortCriteria.SortOrder.DESC) ? SortOrder.Desc : SortOrder.Asc));
          //          sr.sort(SortOptions.of(so -> so.field(fs)));
          ElasticSearchIndexField field = indexMap.get(sc.getIndexField());
          if (null == field) {
            throw new IngeTechnicalException("Index field " + sc.getIndexField() + " not found");
          }

          List<String> nestedPaths = field.getNestedPaths();
          if (null == nestedPaths) {
            fieldSort = FieldSort.of(f -> f.field(sc.getIndexField())
                .order(sc.getSortOrder().equals(SearchSortCriteria.SortOrder.DESC) ? SortOrder.Desc : SortOrder.Asc));
          } else {
            NestedSortValue nestedSortValue = NestedSortValue.of(nsv -> nsv.path(String.join(".", nestedPaths)));
            fieldSort = FieldSort.of(f -> f.field(sc.getIndexField())
                .order(sc.getSortOrder().equals(SearchSortCriteria.SortOrder.DESC) ? SortOrder.Desc : SortOrder.Asc)
                .nested(nestedSortValue));
          }
          FieldSort finalFieldSort = fieldSort;
          sr.sort(SortOptions.of(so -> so.field(finalFieldSort)));
        }
      }

      if (null != getSourceExclusions()) {
        SourceConfig sc = SourceConfig.of(s -> s.filter(SourceFilter.of(sf -> sf.excludes(Arrays.asList(getSourceExclusions())))));
        sr.source(sc);
      }


      if (-1 != searchQuery.getScrollTime()) {
        sr.scroll(Time.of(i -> i.time(searchQuery.getScrollTime() + "ms")));
        //srb.setScroll(new Scroll(new TimeValue()));
      }


      //logger.debug(sr.toString());
      SearchRequest srr = sr.build();
      logger.debug(toJson(srr));
      SearchResponse<E> srb = this.client.getClient().search(srr, this.typeParameterClass);

      srrVO = getSearchRetrieveResponseFromElasticSearchResponse(srb, this.typeParameterClass);
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }


    return srrVO;

  }

  public ResponseBody<ObjectNode> searchDetailed(JsonNode searchRequest, long scrollTime) throws IngeTechnicalException {

    ObjectNode root = (ObjectNode) searchRequest;
    try {
      /*
      if (scrollTime != -1) {
        root.put("scroll", scrollTime);
      }
      */


      //Set track_total_hits to true in order to retrieve correct total numbers > 10000
      root.put("track_total_hits", true);

      if (null != getSourceExclusions() && 0 < getSourceExclusions().length) {
        ArrayNode sourceExclusions = this.objectMapper.valueToTree(getSourceExclusions());
        JsonNode sourceNode = root.get("_source");
        if (null == sourceNode) {
          root.putObject("_source").putArray("excludes").addAll(sourceExclusions);
          //SourceConfig sc = SourceConfig.of(s -> s.filter(SourceFilter.of(sf -> sf.excludes(Arrays.asList(getSourceExclusions())))));
          //srb.source(sc);
        } else {
          if (sourceNode.isObject()) {
            if (null != sourceNode.get("excludes")) {
              ((ArrayNode) sourceNode.get("excludes")).addAll(sourceExclusions);
            } else {
              ((ObjectNode) sourceNode).putArray("excludes").addAll(sourceExclusions);
            }
          }
          //_source is not an object
          else {
            if (sourceNode.isTextual()) {
              root.putObject("_source").putArray("includes").add(sourceNode);
            } else if (sourceNode.isArray()) {
              root.putObject("_source").putArray("includes").addAll((ArrayNode) sourceNode);
            }
            ((ObjectNode) sourceNode).putArray("excludes").addAll(sourceExclusions);
          }
        }


      }

      ByteArrayInputStream bis = new ByteArrayInputStream(this.objectMapper.writeValueAsBytes(root));
      SearchRequest.Builder srb = new SearchRequest.Builder().withJson(bis);
      if (-1 != scrollTime) {
        srb.scroll(Time.of(t -> t.time(scrollTime + "ms")));
      }
      srb.index(this.indexName);
      SearchRequest sr = srb.build();
      logger.debug(toJson(sr));
      SearchResponse<ObjectNode> resp = this.client.getClient().search(sr, ObjectNode.class);
      return resp;
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }


  }

  public ResponseBody<ObjectNode> searchDetailed(JsonNode searchRequest) throws IngeTechnicalException {

    return searchDetailed(searchRequest, -1);


  }

  public ResponseBody<ObjectNode> scrollOn(String scrollId, long scrollTime) throws IngeTechnicalException {

    try {
      return this.client.getClient().scroll(i -> i.scrollId(scrollId).scroll(Time.of(t -> t.time(scrollTime + "ms"))), ObjectNode.class);
      //return client.getClient().prepareSearchScroll(scrollId).setScroll(new Scroll(new TimeValue(scrollTime))).get();
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }


  }


  public static <E> SearchRetrieveResponseVO<E> getSearchRetrieveResponseFromElasticSearchResponse(ResponseBody<E> sr, Class<E> clazz)
      throws IOException {
    SearchRetrieveResponseVO<E> srrVO = new SearchRetrieveResponseVO<>();
    srrVO.setOriginalResponse(sr);
    srrVO.setNumberOfRecords((int) sr.hits().total().value());
    srrVO.setScrollId(sr.scrollId());

    List<SearchRetrieveRecordVO<E>> hitList = new ArrayList<>();
    srrVO.setRecords(hitList);
    for (Hit hit : sr.hits().hits()) {
      SearchRetrieveRecordVO<E> srr = new SearchRetrieveRecordVO<>();
      hitList.add(srr);

      E vo = getVoFromResponseObject(hit.source(), clazz);
      /*
      if (clazz.isAssignableFrom(JsonNode.class)) {
        vo = MapperFactory.getObjectMapper().treeToValue((JsonNode) hit.source(), clazz);
      
      } else {
        vo = (E) hit.source();
      }
      
       */

      srr.setData(vo);
      srr.setPersistenceId(hit.id());
    }


    return srrVO;
  }

  public static <E> E getVoFromResponseObject(Object object, Class<E> clazz) throws IOException {

    E vo;
    if (JsonNode.class.isAssignableFrom(object.getClass())) {
      vo = MapperFactory.getObjectMapper().treeToValue((JsonNode) object, clazz);

    } else {
      vo = (E) object;
    }
    return vo;
  }

  //SP: Alias-Suche funktioniert in ES 6.1 nicht mehr wie erwartet
  public Map<String, ElasticSearchIndexField> getIndexFields() throws IngeTechnicalException {

    try {
      String realIndexName = this.indexName;

      try {
        Map<String, IndexAliases> aliasResponse = this.client.getClient().indices().getAlias(m -> m.name(this.indexName)).result();
        //use first available alias
        realIndexName = aliasResponse.keySet().iterator().next();

      } catch (ElasticsearchException e) {
      }

      final String finalIndexName = realIndexName;

      GetMappingResponse resp = this.client.getClient().indices().getMapping(m -> m.index(finalIndexName));

      if (!resp.result().isEmpty()) { // SP: avoiding NullPointerException
        Map<String, Property> resultMap = resp.result().get(finalIndexName).mappings().properties();

        Map<String, ElasticSearchIndexField> map = ElasticSearchIndexField.Factory.createIndexMapFromElasticsearch(resultMap);
        ElasticSearchIndexField allField = new ElasticSearchIndexField();
        allField.setIndexName("_all");
        allField.setType(ElasticSearchIndexField.Type.TEXT);
        map.put("_all", allField);
        return map;
      }
    } catch (IOException e) {
      logger.error("Error retrieving elasticsearch mapping", e);
      throw new IngeTechnicalException(e);
    }

    return new HashMap<>();
  }


  public static <T extends JsonpSerializable> String toJson(T value) {
    StringWriter sw = new StringWriter();
    JsonpMapper mapper = new JacksonJsonpMapper();
    JsonProvider provider = mapper.jsonProvider();
    JsonGenerator generator = provider.createGenerator(sw);
    mapper.serialize(value, generator);
    generator.close();
    return sw.toString();
  }

  public static <T extends JsonpSerializable> JsonNode toJsonNode(T value) throws IngeTechnicalException {
    try {
      StringWriter sw = new StringWriter();
      JsonpMapper mapper = new JacksonJsonpMapper();
      JsonProvider provider = mapper.jsonProvider();
      JsonGenerator generator = provider.createGenerator(sw);
      mapper.serialize(value, generator);
      generator.close();
      ObjectMapper om = new ObjectMapper();
      JsonNode jsonNode = om.readTree(sw.toString());
      return jsonNode;
    } catch (JsonProcessingException e) {
      throw new IngeTechnicalException(e);
    }
  }



}
