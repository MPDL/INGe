/**
 * 
 */
package de.mpg.mpdl.inge.es.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClientConnector;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.services.ItemInterface;
import de.mpg.mpdl.inge.tech.exceptions.IngeServiceException;

/**
 * @author frank
 * 
 */
@Service
public class ItemServiceBean implements ItemInterface {

  @Value("${item_index_name}")
  private String indexName;
  @Value("${item_index_type}")
  private String indexType;

  @Autowired
  private ObjectMapper mapper;
  @Autowired
  private ElasticSearchTransportClientConnector connector;

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
      return connector.index(indexName, indexType, itemId, voAsBytes);
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
    byte[] voAsBytes = connector.get(indexName, indexType, itemId);
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
  public String updateItem(PubItemVO item, String itemId, boolean createNewVersion)
      throws IngeServiceException {
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(item);
      return connector.update(indexName, indexType, itemId, voAsBytes);
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
    return connector.delete(indexName, indexType, itemId);
  }

}
