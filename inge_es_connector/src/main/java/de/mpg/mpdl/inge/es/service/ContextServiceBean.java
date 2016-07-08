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

import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClientConnector;
import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.services.ContextInterface;
import de.mpg.mpdl.inge.tech.exceptions.NotFoundException;

/**
 * @author frank
 * 
 */
@Service
public class ContextServiceBean implements ContextInterface {

  @Value("${context_index_name}")
  private String indexName;
  @Value("${context_index_type}")
  private String indexType;

  @Autowired
  private ObjectMapper mapper;
  @Autowired
  private ElasticSearchTransportClientConnector connector;


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
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(context);
      return connector.index(indexName, indexType, contextId, voAsBytes);
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
    byte[] voAsBytes = connector.get(indexName, indexType, contextId);
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
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(context);
      return connector.update(indexName, indexType, contextId, voAsBytes);
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
    return connector.delete(indexName, indexType, contextId);
  }

}
