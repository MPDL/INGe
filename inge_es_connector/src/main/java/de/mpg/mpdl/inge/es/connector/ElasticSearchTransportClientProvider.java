package de.mpg.mpdl.inge.es.connector;

import co.elastic.clients.elasticsearch.indices.get_alias.IndexAliases;
import co.elastic.clients.transport.ElasticsearchTransportConfig;
import de.mpg.mpdl.inge.util.ResourceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.util.PropertyReader;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ElasticSearchTransportClientProvider implements ElasticSearchClientProvider {

  private final ElasticsearchClient client;

  private final String user = PropertyReader.getProperty(PropertyReader.INGE_ES_USER);
  private final String pass = PropertyReader.getProperty(PropertyReader.INGE_ES_PASSWORD);

  private static final Logger logger = LogManager.getLogger(ElasticSearchTransportClientProvider.class);

  public ElasticSearchTransportClientProvider() {

    logger.info("Building Elasticsearch REST client for <" + PropertyReader.getProperty(PropertyReader.INGE_ES_REST_HOST_PORT) + ">");


    ElasticsearchTransportConfig.Builder b = new ElasticsearchTransportConfig.Builder();
    String completeUrl = PropertyReader.getProperty(PropertyReader.INGE_ES_REST_HOST_PORT);
    String pathPrefix = PropertyReader.getProperty(PropertyReader.INGE_ES_REST_PATH_PREFIX);
    if (null != pathPrefix && !pathPrefix.isEmpty()) {
      completeUrl = completeUrl + pathPrefix;
    }
    b.host(completeUrl);
    if (null != this.user && !this.user.isEmpty()) {
      b.usernameAndPassword(this.user, this.pass);
    }
    b.jsonMapper(new JacksonJsonpMapper(MapperFactory.getObjectMapper()));

    this.client = new ElasticsearchClient(b.build());
    initIndices("items", "es_index_items.json");
    initPipeline("attachment", "es_index_items_pipeline_attachment.json");
    initIndices("contexts", "es_index_contexts.json");
    initIndices("ous", "es_index_ous.json");
    initIndices("users", "es_index_users.json");


    /*
    RestClientBuilder restClientBuilder =
      RestClient.builder(HttpHost.create(PropertyReader.getProperty(PropertyReader.INGE_ES_REST_HOST_PORT)));
    if (null != this.user && !this.user.isEmpty()) {
    restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
      CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(this.user, this.pass));
      return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
    });
    }
    
    String pathPrefix = PropertyReader.getProperty(PropertyReader.INGE_ES_REST_PATH_PREFIX);
    if (null != pathPrefix && !pathPrefix.isEmpty()) {
    restClientBuilder.setPathPrefix(pathPrefix);
    }
    
    ElasticsearchTransport transport =
      new RestClientTransport(restClientBuilder.build(), new JacksonJsonpMapper(MapperFactory.getObjectMapper()));
    
    this.client = new ElasticsearchClient(transport);
    
     */
  }

  public ElasticsearchClient getClient() {
    return this.client;
  }

  private String initIndices(String indexAliasName, String mappingFileName) {
    try {
      boolean exists = this.client.indices().exists(b -> b.index(indexAliasName)).value();
      if (!exists) {

        String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String indexName = indexAliasName + "_" + date;
        logger.info("Creating index for <" + indexName + ">");
        try (InputStream is =
            ResourceUtil.getResourceAsStream(mappingFileName, ElasticSearchTransportClientProvider.class.getClassLoader())) {
          boolean success = this.client.indices().create(b -> b.index(indexName).withJson(is)).acknowledged();
          if (success) {
            boolean aliasExists = this.client.indices().existsAlias(b -> b.name(indexAliasName)).value();
            if (aliasExists) {
              Map<String, IndexAliases> aliasMap = this.client.indices().getAlias(b -> b.name(indexAliasName)).aliases();
              for (String indexNameWithAlias : aliasMap.keySet()) {
                logger.info("Deleting alias <" + indexAliasName + "> for <" + indexNameWithAlias + ">");
                this.client.indices().deleteAlias(b -> b.index(indexNameWithAlias).name(indexAliasName)).acknowledged();
              }
            }
            logger.info("Creating alias for <" + indexName + "> with name <" + indexAliasName + ">");
            this.client.indices().putAlias(b -> b.index(indexName).name(indexAliasName)).acknowledged();
          }
          return indexName;
        }

      } else {
        logger.info("Index already exists for <" + indexAliasName + ">");
      }
    } catch (Exception e) {
      logger.error("Could not create index <" + indexAliasName + ">", e);
    }
    return null;

  }

  private void initPipeline(String pipelineId, String pipelineFileName) {
    try {
      try (InputStream is =
          ResourceUtil.getResourceAsStream(pipelineFileName, ElasticSearchTransportClientProvider.class.getClassLoader())) {
        this.client.ingest().putPipeline(b -> b.id(pipelineId).withJson(is));
      }
    } catch (IOException e) {
      logger.error("Could not create pipeline <" + pipelineId + ">", e);
    }
  }

}
