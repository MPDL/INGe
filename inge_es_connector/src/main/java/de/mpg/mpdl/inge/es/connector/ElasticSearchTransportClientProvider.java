package de.mpg.mpdl.inge.es.connector;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.util.PropertyReader;

public class ElasticSearchTransportClientProvider implements ElasticSearchClientProvider {

  private final ElasticsearchClient client;

  private final String user = PropertyReader.getProperty(PropertyReader.INGE_ES_USER);
  private final String pass = PropertyReader.getProperty(PropertyReader.INGE_ES_PASSWORD);

  private static final Logger logger = Logger.getLogger(ElasticSearchTransportClientProvider.class);

  public ElasticSearchTransportClientProvider() {

    logger.info("Building Elasticsearch REST client for <" + PropertyReader.getProperty(PropertyReader.INGE_ES_REST_HOST_PORT) + ">");


    RestClientBuilder restClientBuilder =
        RestClient.builder(HttpHost.create(PropertyReader.getProperty(PropertyReader.INGE_ES_REST_HOST_PORT)));
    if (user != null && !user.isEmpty()) {
      restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pass));
        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
      });
    }

    String pathPrefix = PropertyReader.getProperty(PropertyReader.INGE_ES_REST_PATH_PREFIX);
    if (pathPrefix != null && !pathPrefix.isEmpty()) {
      restClientBuilder.setPathPrefix(pathPrefix);
    }

    ElasticsearchTransport transport =
        new RestClientTransport(restClientBuilder.build(), new JacksonJsonpMapper(MapperFactory.getObjectMapper()));

    client = new ElasticsearchClient(transport);
  }

  public ElasticsearchClient getClient() {
    return client;
  }

}
