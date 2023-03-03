package de.mpg.mpdl.inge.es.dao.impl;

import co.elastic.clients.elasticsearch._types.*;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.elasticsearch.indices.get_alias.IndexAliases;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.JsonpSerializable;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.mpg.mpdl.inge.es.connector.ElasticSearchClientProvider;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField.Type;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * ElasticSearchClient enables elasticsearch accessibility
 *
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class ElasticSearchGenericDAOImpl<E> implements GenericDaoEs<E> {

  private static final Logger logger = Logger.getLogger(ElasticSearchGenericDAOImpl.class);

  @Autowired
  protected ElasticSearchClientProvider client;

  protected ObjectMapper mapper = MapperFactory.getObjectMapper();

  protected String indexName;

  protected String indexType;

  protected Class<E> typeParameterClass;

  private static final int DEFAULT_SEARCH_SIZE = 100;
  private static final int MAX_SEARCH_SIZE = 10000;

  private ObjectMapper objectMapper = new ObjectMapper();


  public ElasticSearchGenericDAOImpl(String indexName, String indexType, Class<E> typeParameterClass) {
    this.indexName = indexName;
    this.indexType = indexType;
    this.typeParameterClass = typeParameterClass;
  }


  protected JsonNode applyCustomValues(E entity) {
    JsonNode node = mapper.valueToTree(entity);
    return node;
  }

  protected abstract String[] getSourceExclusions();



  public String create(String id, E entity) throws IngeTechnicalException {
    try {

      IndexResponse indexResponse = client.getClient().index(i -> i.index(indexName).id(id).document(applyCustomValues(entity))

      );

      return indexResponse.id();

    } catch (IOException e) {
      throw new IngeTechnicalException(e);
    }

  }


  public String createImmediately(String id, E entity) throws IngeTechnicalException {
    try {

      IndexResponse indexResponse =
          client.getClient().index(i -> i.index(indexName).id(id).refresh(Refresh.True).document(applyCustomValues(entity))

          );

      return indexResponse.id();

    } catch (IOException e) {
      throw new IngeTechnicalException(e);
    }

  }


  public E get(String id) throws IngeTechnicalException {

    try {
      GetResponse<E> getResponse = client.getClient().get(g -> g.index(indexName).id(id), typeParameterClass);
      return getResponse.source();

    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }
  }

  public String updateImmediately(String id, E entity) throws IngeTechnicalException {

    try {
      UpdateResponse updateResponse =
          client.getClient().update(u -> u.index(indexName).id(id).refresh(Refresh.True).doc(applyCustomValues(entity)), typeParameterClass

          );
      return Long.toString(updateResponse.version());
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }
  }

  public String update(String id, E entity) throws IngeTechnicalException {

    try {
      UpdateResponse updateResponse =
          client.getClient().update(u -> u.index(indexName).id(id).doc(applyCustomValues(entity)), typeParameterClass

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
      DeleteResponse deleteResponse = client.getClient().delete(d -> d.index(indexName).refresh(Refresh.True).id(id));
      return deleteResponse.id();
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }


  }

  public String delete(String id) throws IngeTechnicalException {

    try {
      DeleteResponse deleteResponse = client.getClient().delete(d -> d.index(indexName).id(id));
      return deleteResponse.id();
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }

  }


  public long deleteByQuery(Query query) throws IngeTechnicalException {

    try {
      DeleteByQueryResponse deleteResponse = client.getClient().deleteByQuery(d -> d.index(indexName).query(query));
      return deleteResponse.deleted();
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }
  }

  /**
   * Use maxDocs <=1000 in order to disable scrolling for delete-by-query
   */
  public long deleteByQuery(Query query, int maxDocs) throws IngeTechnicalException {

    try {
      DeleteByQueryResponse deleteResponse =
          client.getClient().deleteByQuery(d -> d.index(indexName).query(query).maxDocs(Long.valueOf(maxDocs)));
      return deleteResponse.deleted();
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }
  }


  public boolean clearScroll(String scrollId) throws IngeTechnicalException {

    try {
      ClearScrollResponse resp = client.getClient().clearScroll(ClearScrollRequest.of(cs -> cs.scrollId(scrollId)));

      return resp.succeeded();
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }
  }


  public SearchRetrieveResponseVO<E> search(SearchRetrieveRequestVO searchQuery) throws IngeTechnicalException {

    SearchRetrieveResponseVO<E> srrVO;
    try {
      SearchRequest.Builder sr = new SearchRequest.Builder();

      //Set track_total_hits to true in order to retrieve correct total numbers > 10000
      sr.trackTotalHits(TrackHits.of(i -> i.enabled(true)));

      sr.index(indexName);

      if (searchQuery.getQueryBuilder() != null) {
        sr.query(searchQuery.getQueryBuilder());
      }

      if (searchQuery.getAggregationBuilders() != null) {
        int i = 0;
        for (Aggregation aggBuilder : searchQuery.getAggregationBuilders()) {
          sr.aggregations("agg" + i, aggBuilder);
        }
      }


      if (searchQuery.getOffset() != 0) {
        sr.from(searchQuery.getOffset());
      }

      if (searchQuery.getLimit() == -2) {
        sr.size(ElasticSearchGenericDAOImpl.MAX_SEARCH_SIZE);
      } else if (searchQuery.getLimit() == -1) {
        sr.size(ElasticSearchGenericDAOImpl.DEFAULT_SEARCH_SIZE);
      } else {
        sr.size(searchQuery.getLimit());
      }

      if (searchQuery.getSortKeys() != null) {
        for (SearchSortCriteria sc : searchQuery.getSortKeys()) {
          FieldSort fs = FieldSort.of(f -> f.field(sc.getIndexField())
              .order(sc.getSortOrder().equals(SearchSortCriteria.SortOrder.DESC) ? SortOrder.Desc : SortOrder.Asc));
          sr.sort(SortOptions.of(so -> so.field(fs)));
        }
      }

      if (getSourceExclusions() != null) {
        SourceConfig sc = SourceConfig.of(s -> s.filter(SourceFilter.of(sf -> sf.excludes(Arrays.asList(getSourceExclusions())))));
        sr.source(sc);
      }


      if (searchQuery.getScrollTime() != -1) {
        sr.scroll(Time.of(i -> i.time(searchQuery.getScrollTime() + "ms")));
        //srb.setScroll(new Scroll(new TimeValue()));
      }


      //logger.debug(sr.toString());
      SearchRequest srr = sr.build();
      logger.debug(toJson(srr));
      SearchResponse<E> srb = client.getClient().search(srr, typeParameterClass);

      srrVO = getSearchRetrieveResponseFromElasticSearchResponse(srb, typeParameterClass);
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

      if (getSourceExclusions() != null && getSourceExclusions().length > 0) {
        ArrayNode sourceExclusions = objectMapper.valueToTree(getSourceExclusions());
        JsonNode sourceNode = root.get("_source");
        if (sourceNode == null) {
          root.putObject("_source").putArray("excludes").addAll(sourceExclusions);
          //SourceConfig sc = SourceConfig.of(s -> s.filter(SourceFilter.of(sf -> sf.excludes(Arrays.asList(getSourceExclusions())))));
          //srb.source(sc);
        } else {
          if (sourceNode.isObject()) {
            if (sourceNode.get("excludes") != null) {
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

      ByteArrayInputStream bis = new ByteArrayInputStream(objectMapper.writeValueAsBytes(root));
      SearchRequest.Builder srb = new SearchRequest.Builder().withJson(bis);
      if (scrollTime != -1) {
        srb.scroll(Time.of(t -> t.time(scrollTime + "ms")));
      }
      srb.index(indexName);
      SearchRequest sr = srb.build();
      logger.debug(toJson(sr));
      SearchResponse<ObjectNode> resp = client.getClient().search(sr, ObjectNode.class);
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
      return client.getClient().scroll(i -> i.scrollId(scrollId).scroll(Time.of(t -> t.time(scrollTime + "ms"))), ObjectNode.class);
      //return client.getClient().prepareSearchScroll(scrollId).setScroll(new Scroll(new TimeValue(scrollTime))).get();
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }


  }


  public static <E> SearchRetrieveResponseVO<E> getSearchRetrieveResponseFromElasticSearchResponse(ResponseBody<E> sr, Class<E> clazz)
      throws IOException {
    SearchRetrieveResponseVO<E> srrVO = new SearchRetrieveResponseVO<E>();
    srrVO.setOriginalResponse(sr);
    srrVO.setNumberOfRecords((int) sr.hits().total().value());
    srrVO.setScrollId(sr.scrollId());

    List<SearchRetrieveRecordVO<E>> hitList = new ArrayList<>();
    srrVO.setRecords(hitList);
    for (Hit hit : sr.hits().hits()) {
      SearchRetrieveRecordVO<E> srr = new SearchRetrieveRecordVO<E>();
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
      String realIndexName = indexName;

      try {
        Map<String, IndexAliases> aliasResponse = this.client.getClient().indices().getAlias(m -> m.name(this.indexName)).result();
        //use first available alias
        realIndexName = aliasResponse.keySet().iterator().next();

      } catch (ElasticsearchException e) {
      }

      final String finalIndexName = realIndexName;

      GetMappingResponse resp = this.client.getClient().indices().getMapping(m -> m.index(finalIndexName));
      //GetMappingsResponse resp = this.client.getClient().admin().indices().prepareGetMappings(this.indexName).addTypes(this.indexType).get();

      if (!resp.result().isEmpty()) { // SP: avoiding NullPointerException
        Map<String, Property> resultMap = resp.result().get(finalIndexName).mappings().properties();

        //((MappingMetaData mmd = resp.getMappings().iterator().next().value.get(this.indexType);

        Map<String, ElasticSearchIndexField> map = ElasticSearchIndexField.Factory.createIndexMapFromElasticsearch(resultMap);
        ElasticSearchIndexField allField = new ElasticSearchIndexField();
        allField.setIndexName("_all");
        allField.setType(Type.TEXT);
        map.put("_all", allField);
        return map;
      }
    } catch (IOException e) {
      throw new IngeTechnicalException(e);
    }

    return new HashMap<String, ElasticSearchIndexField>();
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
