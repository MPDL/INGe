/**
 * 
 */
package de.mpg.mpdl.inge.es.handler;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClient;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.services.ContextInterface;
import de.mpg.mpdl.inge.services.IngeServiceException;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * @author frank
 * 
 */
public class ContextServiceHandler implements ContextInterface {

  private final String indexName = PropertyReader.getProperty("context_index_name");
  private final String indexType = PropertyReader.getProperty("context_index_type");
  private ObjectMapper mapper = ElasticSearchTransportClient.INSTANCE.getMapper();

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mpg.mpdl.inge.services.ContextInterface#createContext(de.mpg.mpdl.inge.model.valueobjects
   * .ContextVO, java.lang.String)
   */
  @Override
  public String createContext(ContextVO context, String contextId) throws IngeServiceException {
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(context);
      return ElasticSearchTransportClient.INSTANCE
          .index(indexName, indexType, contextId, voAsBytes);
    } catch (JsonProcessingException e) {
      throw new IngeServiceException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.ContextInterface#readContext(java.lang.String)
   */
  @Override
  public ContextVO readContext(String contextId) throws IngeServiceException {
    byte[] voAsBytes =
        ElasticSearchTransportClient.INSTANCE.get(indexName, indexType,
            contextId.replace("escidoc:", "pure_"));
    try {
      ContextVO context = mapper.readValue(voAsBytes, ContextVO.class);
      return context;
    } catch (IOException e) {
      throw new IngeServiceException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mpg.mpdl.inge.services.ContextInterface#updateContext(de.mpg.mpdl.inge.model.valueobjects
   * .ContextVO, java.lang.String)
   */
  @Override
  public String updateContext(ContextVO context, String contextId) throws IngeServiceException {
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(context);
      return ElasticSearchTransportClient.INSTANCE.update(indexName, indexType, contextId,
          voAsBytes);
    } catch (JsonProcessingException e) {
      throw new IngeServiceException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.ContextInterface#deleteContext(java.lang.String)
   */
  @Override
  public String deleteContext(String contextId) {
    return ElasticSearchTransportClient.INSTANCE.delete(indexName, indexType, contextId);
  }

}
