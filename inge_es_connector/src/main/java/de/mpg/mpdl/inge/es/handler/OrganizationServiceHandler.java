/**
 * 
 */
package de.mpg.mpdl.inge.es.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClient;
import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClientConnector;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.services.IngeServiceException;
import de.mpg.mpdl.inge.services.OrganizationInterface;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * @author frank
 * 
 */
public class OrganizationServiceHandler implements OrganizationInterface {

  private ObjectMapper mapper = ElasticSearchTransportClient.INSTANCE.getMapper();
  private final String indexName = PropertyReader.getProperty("organization_index_name");
  private final String indexType = PropertyReader.getProperty("organization_index_type");

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
      return ElasticSearchTransportClient.INSTANCE.index(indexName, indexType, organizationId,
          voAsBytes);
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
    byte[] voAsBytes =
        ElasticSearchTransportClient.INSTANCE.get(indexName, indexType, organizationId);
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
      return ElasticSearchTransportClient.INSTANCE.update(indexName, indexType, organizationId,
          voAsBytes);
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
    return ElasticSearchTransportClient.INSTANCE.delete(indexName, indexType, organizationId);
  }
}
