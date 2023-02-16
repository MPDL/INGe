package de.mpg.mpdl.inge.es.connector;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.util.PropertyReader;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;

public class ElasticSearchTransportClientProvider implements ElasticSearchClientProvider {

  private ElasticsearchClient client;
  private String user = PropertyReader.getProperty("inge.es.user");
  private String pass = PropertyReader.getProperty("inge.es.password");

  //private RestHighLevelClient restHighLevelClient;

  private static final Logger logger = LogManager.getLogger(ElasticSearchTransportClientProvider.class);

  public ElasticSearchTransportClientProvider() {

    logger.info("Building Elasticsearch REST client for <" + PropertyReader.getProperty(PropertyReader.INGE_ES_REST_HOST_PORT) + ">");

    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pass));
    RestClient restClient =
        RestClient.builder(new HttpHost("localhost", 9200, "https")).setHttpClientConfigCallback(new HttpClientConfigCallback() {
          @Override
          public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
          }
        }).build();
    ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(MapperFactory.getObjectMapper()));
    // ElasticsearchClient esClient = new ElasticsearchClient(transport);
    // return esClient;
    // Create the low-level client
    // RestClient restClient = RestClient.builder(HttpHost.create(PropertyReader.getProperty(PropertyReader.INGE_ES_REST_HOST_PORT))).build();


    // Create the HLRC
    /*
    RestHighLevelClient hlrc = new RestHighLevelClientBuilder(restClient)
            .setApiCompatibilityMode(true)
            .build();
    */

    // Create the transport with a Jackson mapper
    // ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(MapperFactory.getObjectMapper()));


    client = new ElasticsearchClient(transport);

    /*
    this.client = new PreBuiltTransportClient(Settings.builder()
        .put("cluster.name", PropertyReader.getProperty(PropertyReader.INGE_ES_CLUSTER_NAME)).put("client.transport.sniff", true).build());
    
    logger.info("Building TransportClient for <" + PropertyReader.getProperty(PropertyReader.INGE_ES_CLUSTER_NAME) + ">" + " and <"
        + PropertyReader.getProperty(PropertyReader.INGE_ES_TRANSPORT_IPS) + "> ");
    String transportIps = PropertyReader.getProperty(PropertyReader.INGE_ES_TRANSPORT_IPS);
    
    for (String ip : transportIps.split(" ")) {
      String addr = ip.split(":")[0];
      int port = Integer.valueOf(ip.split(":")[1]);
      try {
        this.client.addTransportAddress(new TransportAddress(InetAddress.getByName(addr), port));
    
        String nodeName = this.client.nodeName();
        logger.info("Nodename <" + nodeName + ">");
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
    }
    */
  }

  public ElasticsearchClient getClient() {
    return client;
  }

  /*
  public RestHighLevelClient getRestHighLevelClient() {
    return restHighLevelClient;
  }
   */

}
