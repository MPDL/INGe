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
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.services.OrganizationInterface;
import de.mpg.mpdl.inge.tech.exceptions.IngeServiceException;

/**
 * @author frank
 * 
 */
@Service
public class OrganizationServiceBean implements OrganizationInterface {

  @Value("${organization_index_name}")
  private String indexName;
  @Value("${organization_index_type}")
  private String indexType;

  @Autowired
  private ObjectMapper mapper;
  @Autowired
  private ElasticSearchTransportClientConnector connector;

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.OrganizationInterface#createOrganization(de.mpg.mpdl.inge.model.
   * valueobjects.AffiliationVO)
   */
  @Override
  public String createOrganization(AffiliationVO organization, String organizationId)
      throws IngeServiceException {
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(organization);
      return connector.index(indexName, indexType, organizationId, voAsBytes);
    } catch (JsonProcessingException e) {
      throw new IngeServiceException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.OrganizationInterface#readOrganization(java.lang.String)
   */
  @Override
  public AffiliationVO readOrganization(String organizationId) throws IngeServiceException {
    byte[] voAsBytes = connector.get(indexName, indexType, organizationId);
    try {
      AffiliationVO organization = mapper.readValue(voAsBytes, AffiliationVO.class);
      return organization;
    } catch (IOException e) {
      throw new IngeServiceException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.OrganizationInterface#updateOrganization(de.mpg.mpdl.inge.model.
   * valueobjects.AffiliationVO)
   */
  @Override
  public String updateOrganization(AffiliationVO organization, String organizationId)
      throws IngeServiceException {
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(organization);
      return connector.update(indexName, indexType, organizationId, voAsBytes);
    } catch (JsonProcessingException e) {
      throw new IngeServiceException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.OrganizationInterface#deleteOrganization(java.lang.String)
   */
  @Override
  public String deleteOrganization(String organizationId) {
    return connector.delete(indexName, indexType, organizationId);
  }
}
