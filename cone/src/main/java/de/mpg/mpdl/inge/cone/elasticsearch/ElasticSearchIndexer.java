package de.mpg.mpdl.inge.cone.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.JsonpUtils;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.JsonNode;
import de.mpg.mpdl.inge.cone.ConeException;
import de.mpg.mpdl.inge.cone.ModelList;
import de.mpg.mpdl.inge.cone.SearchEngineIndexer;
import de.mpg.mpdl.inge.cone.TreeFragment;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ElasticSearchIndexer implements SearchEngineIndexer {

  private static final Logger logger = LogManager.getLogger(ElasticSearchIndexer.class);

  private ElasticsearchClient client;

  private JacksonJsonpMapper jacksonJsonpMapper = new JacksonJsonpMapper();

  public ElasticSearchIndexer() {



    final String user = PropertyReader.getProperty(PropertyReader.INGE_ES_USER);
    final String pass = PropertyReader.getProperty(PropertyReader.INGE_ES_PASSWORD);
    final String host = PropertyReader.getProperty(PropertyReader.INGE_ES_REST_HOST_PORT);
    final String pathPrefix = PropertyReader.getProperty(PropertyReader.INGE_ES_REST_PATH_PREFIX);
    logger.info(String.format("Connecting to Elasticsearch at: %s, path prefix: %s", host, pathPrefix));
    RestClientBuilder restClientBuilder = RestClient.builder(HttpHost.create(host));
    if (null != user && !user.isEmpty()) {
      restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pass));
        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
      });
    }


    if (null != pathPrefix && !pathPrefix.isEmpty()) {
      restClientBuilder.setPathPrefix(pathPrefix);
    }

    ElasticsearchTransport transport = new RestClientTransport(restClientBuilder.build(), new JacksonJsonpMapper());

    client = new ElasticsearchClient(transport);

  }

  private static String getIndexName(String modelName) {
    String indexName = "cone-" + modelName;
    return indexName;
  }

  private static String escapeId(String id) {
    return id.replaceAll("/", "_");
  }

  @Override
  public void index(String model, String id, TreeFragment values) throws ConeException {
    try {
      StringReader reader = new StringReader(values.toJson());
      client.index(i -> i.index(getIndexName(model)).id(escapeId(values.getSubject())).withJson(reader));
    } catch (IOException e) {
      throw new ConeException("Error indexing " + values.getSubject(), e);
    }

  }

  @Override
  public void deleteFromIndex(String model, String id) throws ConeException {
    try {
      client.delete(del -> del.index(getIndexName(model)).id(escapeId(id)));
    } catch (IOException e) {
      throw new ConeException("Error deleting from index " + id, e);
    }
  }

  public void resetIndex(ModelList.Model model) throws ConeException {
    //Delete old index
    try {
      String indexName = getIndexName(model.getName());
      boolean indexExists = client.indices().exists(ExistsRequest.of(e -> e.index(indexName))).value();
      if (indexExists) {
        logger.info("Deleting CoNE index " + indexName);
        DeleteIndexResponse delResp = client.indices().delete(dr -> dr.index(indexName));
      }
      createElasticsearchIndex(model, indexName);
    } catch (IOException e) {
      throw new ConeException(e);
    }

  }


  public void initializeIndices(ModelList modelList) throws ConeException {
    try {

      for (ModelList.Model model : modelList.getList()) {
        String modelName = model.getName();
        String indexName = getIndexName(modelName);
        //Check if index exists
        boolean indexExists = client.indices().exists(ExistsRequest.of(e -> e.index(indexName))).value();
        if (!indexExists) {
          createElasticsearchIndex(model, indexName);
        }
      }
    } catch (IOException e) {
      throw new ConeException(e);
    }
  }

  public String simpleSearch(String model, String query, int from, int size) throws ConeException {
    try {
      String indexName = getIndexName(model);
      SearchResponse<JsonNode> resp = client.search(
          sr -> sr.index(indexName).query(q -> q.queryString(qs -> qs.query(query))).from(from).size(size).source(s -> s.fetch(true)),
          JsonNode.class);
      return JsonpUtils.toJsonString(resp, jacksonJsonpMapper);
    } catch (IOException e) {
      throw new ConeException(e);
    }
  }

  private void createElasticsearchIndex(ModelList.Model model, String indexName) throws IOException {
    //Create mapping
    TypeMapping.Builder tmb = buildMapping(model);

    //Create index
    CreateIndexRequest.Builder createIndexRequest = new CreateIndexRequest.Builder();
    createIndexRequest.index(indexName);
    createIndexRequest.mappings(tmb.build());
    logger.info("Creating CoNE index " + indexName);
    client.indices().create(createIndexRequest.build());
  }

  private static TypeMapping.Builder buildMapping(ModelList.Model model) {
    //Create mapping
    LinkedHashMap<String, Property> subPropertyMap = new LinkedHashMap<>();
    subPropertyMap.put("id", Property.of(prop -> prop.keyword(kw -> kw)));
    buildElasticSearchMappingProperties(model.getPredicates(), subPropertyMap);
    TypeMapping.Builder typeMappingBuilder = new TypeMapping.Builder();
    typeMappingBuilder.dynamic(DynamicMapping.False);
    typeMappingBuilder.properties(subPropertyMap);
    return typeMappingBuilder;
  }

  private static void buildElasticSearchMappingProperties(List<ModelList.Predicate> predicates, Map<String, Property> propMap) {
    for (ModelList.Predicate p : predicates) {
      String predicateName = TreeFragment.escapeKeyForJson(p.getId());
      if (p.getPredicates() != null && p.getPredicates().size() > 0) {
        LinkedHashMap<String, Property> subPropertyMap = new LinkedHashMap<>();
        Property subProp = Property.of(prop -> prop.object(ob -> ob.properties(subPropertyMap)));
        propMap.put(predicateName, subProp);
        buildElasticSearchMappingProperties(p.getPredicates(), subPropertyMap);
      } else {
        if (!p.isRestricted()) {
          Property property = Property.of(prop -> prop.text(tp -> tp.fields("keyword", kwp -> kwp.keyword(kw -> kw))));
          propMap.put(predicateName, property);
        }

      }

    }
  }


  public static void main(String[] args) throws Exception {

    ModelList modelList = ModelList.getInstance();
    for (ModelList.Model model : modelList.getList()) {
      String modelName = model.getName();
      LinkedHashMap<String, Property> subPropertyMap = new LinkedHashMap<>();
      buildElasticSearchMappingProperties(model.getPredicates(), subPropertyMap);
      TypeMapping.Builder typeMappingBuilder = new TypeMapping.Builder();
      typeMappingBuilder.dynamic(DynamicMapping.Strict);
      StringWriter sw = new StringWriter();
      JsonpMapper mapper = new JacksonJsonpMapper();
      JsonProvider provider = mapper.jsonProvider();
      JsonGenerator generator = provider.createGenerator(sw);
      mapper.serialize(typeMappingBuilder.build(), generator);
      generator.close();
      System.out.println(sw.toString());
    }

    /*
    TypeMapping.Builder typeMappingBuilder = new TypeMapping.Builder();
    typeMappingBuilder.dynamic(DynamicMapping.Strict);
    typeMappingBuilder.properties("context", prop -> prop
            .object(ObjectProperty.of(n -> n
                            .enabled(false)
                            .properties("karl", Property.of(p->p.text(TextProperty.of(t->t))))
                    )
            )
    
    );
    
    
    StringWriter sw = new StringWriter();
    JsonpMapper mapper = new JacksonJsonpMapper();
    JsonProvider provider = mapper.jsonProvider();
    JsonGenerator generator = provider.createGenerator(sw);
    mapper.serialize(typeMappingBuilder.build(), generator);
    generator.close();
    System.out.println(sw.toString());
    
     */
  }

}
