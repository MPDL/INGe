package de.mpg.mpdl.inge.es.connector;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.model.exceptions.TechnicalException;

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
   * @throws TechnicalException
   */
  public String index(String indexName, String indexType, String id, byte[] voAsBytes)
      throws TechnicalException {

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
   * @throws TechnicalException
   */
  public byte[] get(String indexName, String indexType, String id) throws TechnicalException {
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
   * @throws TechnicalException
   */
  public String update(String indexName, String indexType, String id, byte[] voAsBytes)
      throws TechnicalException {
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
  public String delete(String indexName, String indexType, String id) throws TechnicalException {
    DeleteResponse deleteResponse =
        client.prepareDelete().setIndex(indexName).setType(indexType).setId(id).get();
    client.close();
    return deleteResponse.getId();
  }
}
