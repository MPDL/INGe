package de.mpg.mpdl.inge.es.connector;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ElasticSearchTransportClientConnector
 * 
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@Service
public class ElasticSearchTransportClientConnector {

  @Autowired
  private Client client;

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param voAsBytes
   * @return {@link String}
   */
  public String index(String indexName, String indexType, String id, byte[] voAsBytes) {

    IndexResponse indexResponse =
        client.prepareIndex().setIndex(indexName).setType(indexType).setId(id).setSource(voAsBytes)
            .get();
    return indexResponse.getId();
  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return byte[]
   */
  public byte[] get(String indexName, String indexType, String id) {
    GetResponse getResponse =
        client.prepareGet().setIndex(indexName).setType(indexType).setId(id).get();
    byte[] voAsBytes = getResponse.getSourceAsBytes();
    return voAsBytes;
  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param voAsBytes
   * @return {@link String}
   */
  public String update(String indexName, String indexType, String id, byte[] voAsBytes) {
    UpdateResponse updateResponse =
        client.prepareUpdate().setIndex(indexName).setType(indexType).setId(id).setDoc(voAsBytes)
            .get();
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
        client.prepareDelete().setIndex(indexName).setType(indexType).setId(id).get();
    client.close();
    return deleteResponse.getId();
  }
}
