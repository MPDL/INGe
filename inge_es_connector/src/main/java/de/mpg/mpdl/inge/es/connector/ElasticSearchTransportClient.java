package de.mpg.mpdl.inge.es.connector;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * ElasticSearchTransportClient enables elasticsearch accessibility
 * 
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public enum ElasticSearchTransportClient {

  INSTANCE;

  TransportClient client = null;
  ObjectMapper mapper = null;

  private ElasticSearchTransportClient() {
    initializeClient();
    initializeMapper();
  }


  public ObjectMapper getMapper() {
    return mapper;
  }

  private void initializeMapper() {
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  private void initializeClient() {
    Settings settings =
        Settings.builder().put("cluster.name", PropertyReader.getProperty("es_cluster_name"))
            .put("client.transport.sniff", true).build();
    client = new PreBuiltTransportClient(settings);
    String transportIps = PropertyReader.getProperty("es_transport_ips");
    for (String ip : transportIps.split(" ")) {
      String addr = ip.split(":")[0];
      int port = Integer.valueOf(ip.split(":")[1]);
      try {
        client
            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(addr), port));
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * get a {@link TransportClient} with predefined {@link Settings}
   * 
   * @return {@link TransportClient}
   */
  protected TransportClient getClient() {
    return client;
  }


  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String index(String indexName, String indexType, String id, byte[] voAsBytes) {

    IndexResponse indexResponse =
        getClient().prepareIndex().setIndex(indexName).setType(indexType).setId(id)
            .setSource(voAsBytes).get();
    return indexResponse.getId();

  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link ValueObject}
   */
  public byte[] get(String indexName, String indexType, String id) {
    GetResponse getResponse =
        getClient().prepareGet().setIndex(indexName).setType(indexType).setId(id).get();
    byte[] voAsBytes = getResponse.getSourceAsBytes();
    return voAsBytes;

  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String update(String indexName, String indexType, String id, byte[] voAsBytes) {

    UpdateResponse updateResponse =
        getClient().prepareUpdate().setIndex(indexName).setType(indexType).setId(id)
            .setDoc(voAsBytes).get();
    return Long.toString(updateResponse.getVersion());

  }


  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link String}
   */
  public String delete(String indexName, String indexType, String id) {

    DeleteResponse deleteResponse =
        getClient().prepareDelete().setIndex(indexName).setType(indexType).setId(id).get();
    return deleteResponse.getId();

  }

  public SearchRequestBuilder search(String... indexNames) {
    return getClient().prepareSearch(indexNames);
  }
  
  public MultiGetResponse multiGet(String indexName, String indexType, String... ids) {
    
    return getClient().prepareMultiGet().add(indexName, indexType, ids).get();
  }
}
