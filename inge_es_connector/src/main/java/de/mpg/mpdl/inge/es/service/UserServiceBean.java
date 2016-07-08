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
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.services.UserInterface;
import de.mpg.mpdl.inge.tech.exceptions.NotFoundException;

/**
 * @author frank
 * 
 */
@Service
public class UserServiceBean implements UserInterface {

  @Value("${usergroup_index_name}")
  private String indexName;
  @Value("${usergroup_index_type}")
  private String indexType;

  @Autowired
  private ObjectMapper mapper;
  @Autowired
  private ElasticSearchTransportClientConnector connector;

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.UserInterface#createUser(de.mpg.mpdl.inge.model.valueobjects.
   * AccountUserVO, java.lang.String)
   */
  @Override
  public String createUser(AccountUserVO user, String userId) throws AuthenticationException,
      TechnicalException {
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(user);
      return connector.index(indexName, indexType, userId, voAsBytes);
    } catch (JsonProcessingException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.UserInterface#readUser(java.lang.String)
   */
  @Override
  public AccountUserVO readUser(String userId) throws TechnicalException, NotFoundException,
      SecurityException {
    byte[] voAsBytes = connector.get(indexName, indexType, userId);
    try {
      AccountUserVO userVo = mapper.readValue(voAsBytes, AccountUserVO.class);
      return userVo;
    } catch (IOException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.UserInterface#updateUser(de.mpg.mpdl.inge.model.valueobjects.
   * AccountUserVO, java.lang.String)
   */
  @Override
  public String updateUser(AccountUserVO user, String userId) throws AuthenticationException,
      TechnicalException {
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(user);
      return connector.update(indexName, indexType, userId, voAsBytes);
    } catch (JsonProcessingException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.UserInterface#deleteUser(java.lang.String)
   */
  @Override
  public String deleteUser(String userId) throws AuthenticationException, TechnicalException {
    return connector.delete(indexName, indexType, userId);
  }

}
