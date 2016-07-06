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
import de.mpg.mpdl.inge.model.valueobjects.UserGroupVO;
import de.mpg.mpdl.inge.services.UserGroupInterface;
import de.mpg.mpdl.inge.tech.exceptions.NotFoundException;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * @author frank
 * 
 */
public class UserGroupService implements UserGroupInterface {

  private String indexName = null;
  private String indexType = null;
  private ObjectMapper mapper = new ObjectMapper();

  public UserGroupService() {
	init();
}
  
  protected void init() {
	    try {
	      this.indexName = PropertyReader.getProperty("usergroup_index_name");
	      this.indexType = PropertyReader.getProperty("usergroup_index_type");
	    } catch (IOException | URISyntaxException e) {
	      e.printStackTrace();
	    }
	  }
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mpg.mpdl.inge.services.UserGroupInterface#createUserGroup(de.mpg.mpdl.inge.model.valueobjects
   * .UserGroupVO, java.lang.String)
   */
  @Override
  public String createUserGroup(UserGroupVO userGroup, String userGroupId)
      throws AuthenticationException, TechnicalException {
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(userGroup);
      return ElasticSearchTransportClient.INSTANCE.index(indexName, indexType, userGroupId,
          voAsBytes);
    } catch (JsonProcessingException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.UserGroupInterface#readUserGroup(java.lang.String)
   */
  @Override
  public UserGroupVO readUserGroup(String userGroupId) throws TechnicalException,
      NotFoundException, SecurityException {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    byte[] voAsBytes = ElasticSearchTransportClient.INSTANCE.get(indexName, indexType, userGroupId);
    try {
      UserGroupVO group = mapper.readValue(voAsBytes, UserGroupVO.class);
      return group;
    } catch (IOException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mpg.mpdl.inge.services.UserGroupInterface#updateUserGroup(de.mpg.mpdl.inge.model.valueobjects
   * .UserGroupVO, java.lang.String)
   */
  @Override
  public String updateUserGroup(UserGroupVO userGroup, String userGroupId)
      throws AuthenticationException, TechnicalException {
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(userGroup);
      return ElasticSearchTransportClient.INSTANCE.update(indexName, indexType, userGroupId,
          voAsBytes);
    } catch (JsonProcessingException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.UserGroupInterface#deleteUserGroup(java.lang.String)
   */
  @Override
  public String deleteUserGroup(String userGroupId) throws AuthenticationException,
      TechnicalException {
    return ElasticSearchTransportClient.INSTANCE.delete(indexName, indexType, userGroupId);
  }

}
