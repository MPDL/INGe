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
import de.mpg.mpdl.inge.model.valueobjects.UserGroupVO;
import de.mpg.mpdl.inge.services.UserGroupInterface;
import de.mpg.mpdl.inge.tech.exceptions.IngeServiceException;

/**
 * @author frank
 * 
 */
@Service
public class UserGroupServiceBean implements UserGroupInterface {

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
   * @see
   * de.mpg.mpdl.inge.services.UserGroupInterface#createUserGroup(de.mpg.mpdl.inge.model.valueobjects
   * .UserGroupVO, java.lang.String)
   */
  @Override
  public String createUserGroup(UserGroupVO userGroup, String userGroupId)
      throws IngeServiceException {
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(userGroup);
      return connector.index(indexName, indexType, userGroupId, voAsBytes);
    } catch (JsonProcessingException e) {
      throw new IngeServiceException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.UserGroupInterface#readUserGroup(java.lang.String)
   */
  @Override
  public UserGroupVO readUserGroup(String userGroupId) throws IngeServiceException {
    byte[] voAsBytes = connector.get(indexName, indexType, userGroupId);
    try {
      UserGroupVO group = mapper.readValue(voAsBytes, UserGroupVO.class);
      return group;
    } catch (IOException e) {
      throw new IngeServiceException(e.getMessage(), e.getCause());
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
      throws IngeServiceException {
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(userGroup);
      return connector.update(indexName, indexType, userGroupId, voAsBytes);
    } catch (JsonProcessingException e) {
      throw new IngeServiceException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.UserGroupInterface#deleteUserGroup(java.lang.String)
   */
  @Override
  public String deleteUserGroup(String userGroupId) {
    return connector.delete(indexName, indexType, userGroupId);
  }

}
