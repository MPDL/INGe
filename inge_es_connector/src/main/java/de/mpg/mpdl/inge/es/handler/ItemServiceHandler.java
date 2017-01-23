/**
 * 
 */
package de.mpg.mpdl.inge.es.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClient;
import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClientConnector;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.services.IngeServiceException;
import de.mpg.mpdl.inge.services.ItemInterface;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * @author frank
 * 
 */
public class ItemServiceHandler implements ItemInterface {

  private ObjectMapper mapper = new ObjectMapper();
  private final String indexName = PropertyReader.getProperty("item_index_name");
  private final String indexType = PropertyReader.getProperty("item_index_type");


  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mpg.mpdl.inge.services.ItemInterface#createItem(de.mpg.mpdl.inge.model.valueobjects.ItemVO,
   * java.lang.String)
   */
  @Override
  public String createItem(PubItemVO item, String itemId) throws IngeServiceException {
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(item);
      return ElasticSearchTransportClient.INSTANCE.index(indexName, indexType, itemId, voAsBytes);
    } catch (JsonProcessingException e) {
      throw new IngeServiceException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.ItemInterface#readItem(java.lang.String)
   */
  @Override
  public PubItemVO readItem(String itemId) throws IngeServiceException {
    byte[] voAsBytes = ElasticSearchTransportClient.INSTANCE.get(indexName, indexType, itemId);
    try {
      PubItemVO item = mapper.readValue(voAsBytes, PubItemVO.class);
      return item;
    } catch (IOException e) {
      throw new IngeServiceException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mpg.mpdl.inge.services.ItemInterface#updateItem(de.mpg.mpdl.inge.model.valueobjects.ItemVO,
   * java.lang.String, boolean)
   */
  @Override
  public String updateItem(PubItemVO item, String itemId) throws IngeServiceException {
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(item);
      return ElasticSearchTransportClient.INSTANCE.update(indexName, indexType, itemId, voAsBytes);
    } catch (JsonProcessingException e) {
      throw new IngeServiceException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.ItemInterface#deleteItem(java.lang.String)
   */
  @Override
  public String deleteItem(String itemId) {
    return ElasticSearchTransportClient.INSTANCE.delete(indexName, indexType, itemId);
  }

}
