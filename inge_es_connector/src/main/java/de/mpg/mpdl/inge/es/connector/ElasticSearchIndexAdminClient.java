package de.mpg.mpdl.inge.es.connector;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * ElasticSearchTransportClient enables elasticsearch accessibility
 * 
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public enum ElasticSearchIndexAdminClient {

  INSTANCE;

  /**
   * Create a new index
   * 
   * @param indexName
   */
  public boolean createIndex(String indexName) {
    Client client = getClient();

    IndicesExistsResponse insecExistsResponse =
        client.admin().indices().prepareExists(indexName).execute().actionGet();
    if (insecExistsResponse.isExists()) {
      deleteIndex(indexName);
    }

    CreateIndexRequestBuilder createIndexRequest =
        client.admin().indices().prepareCreate(indexName);
    CreateIndexResponse createIndexResponse = createIndexRequest.execute().actionGet();

    client.close();
    return createIndexResponse.isAcknowledged();
  }

  /**
   * delete an existing index
   * 
   * @param index
   * @return boolean
   */
  public boolean deleteIndex(String index) {
    Client client = getClient();
    AcknowledgedResponse deleteIndexResponse =
        client.admin().indices().prepareDelete(index).execute().actionGet();
    client.close();
    return deleteIndexResponse.isAcknowledged();
  }

  /**
   * Add a json mapping to an existing index
   * 
   * @param index
   * @param type
   * @param path2jsonFile
   * @return boolean
   */
  public boolean addMapping(String index, String type, String path2jsonFile) {
    java.nio.file.Path path = Paths.get(path2jsonFile);
    byte[] mapping = null;
    try {
      mapping = Files.readAllBytes(path);
    } catch (IOException e) {
      e.printStackTrace();
    }

    Client client = getClient();
    PutMappingRequestBuilder mappingRequest = client.admin().indices().preparePutMapping(index);
    mappingRequest.setType(type);
    mappingRequest.setSource(new String(mapping, StandardCharsets.UTF_8));

    AcknowledgedResponse mappingResponse = mappingRequest.execute().actionGet();
    client.close();
    return mappingResponse.isAcknowledged();
  }

  /**
   * create an alias for an existing index
   * 
   * @param index
   * @param alias
   * @return boolean
   */
  public boolean addAlias(String index, String alias) {
    Client client = getClient();
    AcknowledgedResponse indicesAliasResponse =
        client.admin().indices().prepareAliases().addAlias(index, alias).execute().actionGet();
    client.close();
    return indicesAliasResponse.isAcknowledged();
  }

  /**
   * get a {@link TransportClient} with predefined {@link Settings}
   * 
   * @return {@link TransportClient}
   */
  protected TransportClient getClient() {

    TransportClient client = null;
    try {
      Settings settings =
          Settings.builder()
              .put("cluster.name", PropertyReader.getProperty(PropertyReader.INGE_ES_CLUSTER_NAME))
              .put("client.transport.sniff", true).build();
      client = new PreBuiltTransportClient(settings);
      for (String ip : PropertyReader.getProperty(PropertyReader.INGE_ES_TRANSPORT_IPS).split(" ")) {
        client.addTransportAddress(new TransportAddress(InetAddress.getByName(ip), 9300));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return client;
  }
}
