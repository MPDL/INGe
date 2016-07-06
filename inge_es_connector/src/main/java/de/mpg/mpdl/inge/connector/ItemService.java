/**
 * 
 */
package de.mpg.mpdl.inge.connector;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.services.ItemInterface;
import de.mpg.mpdl.inge.tech.exceptions.NotFoundException;

/**
 * @author frank
 * 
 */
public class ItemService implements ItemInterface {

  private final String indexName = "pure";
  private final String indexType = "pubitem";
  private ObjectMapper mapper = new ObjectMapper();

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mpg.mpdl.inge.services.ItemInterface#createItem(de.mpg.mpdl.inge.model.valueobjects.ItemVO,
   * java.lang.String)
   */
  @Override
  public String createItem(PubItemVO item, String itemId) throws TechnicalException,
      SecurityException {
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(item);
      return ElasticSearchTransportClient.INSTANCE.index(indexName, indexType, itemId, voAsBytes);
    } catch (JsonProcessingException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.ItemInterface#readItem(java.lang.String)
   */
  @Override
  public PubItemVO readItem(String itemId) throws TechnicalException, NotFoundException,
      SecurityException {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    byte[] voAsBytes = ElasticSearchTransportClient.INSTANCE.get(indexName, indexType, itemId);
    try {
      PubItemVO item = mapper.readValue(voAsBytes, PubItemVO.class);
      return item;
    } catch (IOException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
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
  public String updateItem(PubItemVO item, String itemId, boolean createNewVersion)
      throws TechnicalException, SecurityException, NotFoundException {
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(item);
      return ElasticSearchTransportClient.INSTANCE.update(indexName, indexType, itemId, voAsBytes);
    } catch (JsonProcessingException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.ItemInterface#deleteItem(java.lang.String)
   */
  @Override
  public String deleteItem(String itemId) throws TechnicalException, SecurityException,
      NotFoundException {
    return ElasticSearchTransportClient.INSTANCE.delete(indexName, indexType, itemId);
  }

}
