/**
 * 
 */
package de.mpg.mpdl.inge.connector;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.services.UserInterface;
import de.mpg.mpdl.inge.tech.exceptions.NotFoundException;

/**
 * @author frank
 * 
 */
public class UserService implements UserInterface {

  private final String indexName = "user_accounts";
  private final String indexType = "account";
  private ObjectMapper mapper = new ObjectMapper();

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.UserInterface#createUser(de.mpg.mpdl.inge.model.valueobjects.
   * AccountUserVO, java.lang.String)
   */
  @Override
  public String createUser(AccountUserVO user, String userId) throws AuthenticationException,
      TechnicalException {
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(user);
      return ElasticSearchTransportClient.INSTANCE.index(indexName, indexType, userId, voAsBytes);
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
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    byte[] voAsBytes = ElasticSearchTransportClient.INSTANCE.get(indexName, indexType, userId);
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
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(user);
      return ElasticSearchTransportClient.INSTANCE.update(indexName, indexType, userId, voAsBytes);
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
    return ElasticSearchTransportClient.INSTANCE.delete(indexName, indexType, userId);
  }

}
