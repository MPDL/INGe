/**
 * 
 */
package de.mpg.mpdl.inge.connector;

import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.services.ContextInterface;
import de.mpg.mpdl.inge.tech.exceptions.NotFoundException;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * @author frank
 * 
 */
public class ContextService implements ContextInterface {

  private String indexName = null;
  private String indexType =null;
  private ObjectMapper mapper = new ObjectMapper();

  public ContextService() {
	init();
}
  
  protected void init() {
	    try {
	      this.indexName = PropertyReader.getProperty("context_index_name");
	      this.indexType = PropertyReader.getProperty("context_index_type");
	    } catch (IOException | URISyntaxException e) {
	      e.printStackTrace();
	    }
	  }
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mpg.mpdl.inge.services.ContextInterface#createContext(de.mpg.mpdl.inge.model.valueobjects
   * .ContextVO, java.lang.String)
   */
  @Override
  public String createContext(ContextVO context, String contextId) throws AuthenticationException,
      TechnicalException {
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(context);
      return ElasticSearchTransportClient.INSTANCE
          .index(indexName, indexType, contextId, voAsBytes);
    } catch (JsonProcessingException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.ContextInterface#readContext(java.lang.String)
   */
  @Override
  public ContextVO readContext(String contextId) throws TechnicalException, NotFoundException,
      SecurityException {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    byte[] voAsBytes = ElasticSearchTransportClient.INSTANCE.get(indexName, indexType, contextId);
    try {
      ContextVO context = mapper.readValue(voAsBytes, ContextVO.class);
      return context;
    } catch (IOException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
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
  public String updateContext(ContextVO context, String contextId) throws AuthenticationException,
      TechnicalException {
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(context);
      return ElasticSearchTransportClient.INSTANCE.update(indexName, indexType, contextId,
          voAsBytes);
    } catch (JsonProcessingException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.ContextInterface#deleteContext(java.lang.String)
   */
  @Override
  public String deleteContext(String contextId) throws AuthenticationException, TechnicalException {
    return ElasticSearchTransportClient.INSTANCE.delete(indexName, indexType, contextId);
  }

}
