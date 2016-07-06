/**
 * 
 */
package de.mpg.mpdl.inge.connector;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.services.OrganizationInterface;
import de.mpg.mpdl.inge.tech.exceptions.NotFoundException;

/**
 * @author frank
 * 
 */
public class OrganizationService implements OrganizationInterface {

  private final String indexName = "organizational_units";
  private final String indexType = "organization";
  private ObjectMapper mapper = new ObjectMapper();

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.OrganizationInterface#createOrganization(de.mpg.mpdl.inge.model.
   * valueobjects.AffiliationVO)
   */
  @Override
  public String createOrganization(AffiliationVO organization, String organizationId)
      throws SecurityException, NotFoundException, TechnicalException {
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(organization);
      return ElasticSearchTransportClient.INSTANCE.index(indexName, indexType, organizationId,
          voAsBytes);
    } catch (JsonProcessingException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.OrganizationInterface#readOrganization(java.lang.String)
   */
  @Override
  public AffiliationVO readOrganization(String organizationId) throws TechnicalException,
      NotFoundException, SecurityException {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    byte[] voAsBytes =
        ElasticSearchTransportClient.INSTANCE.get(indexName, indexType, organizationId);
    try {
      AffiliationVO organization = mapper.readValue(voAsBytes, AffiliationVO.class);
      return organization;
    } catch (IOException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
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
      throws SecurityException, NotFoundException, TechnicalException {
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    byte[] voAsBytes;
    try {
      voAsBytes = mapper.writeValueAsBytes(organization);
      return ElasticSearchTransportClient.INSTANCE.update(indexName, indexType, organizationId,
          voAsBytes);
    } catch (JsonProcessingException e) {
      throw new TechnicalException(e.getMessage(), e.getCause());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.OrganizationInterface#deleteOrganization(java.lang.String)
   */
  @Override
  public String deleteOrganization(String organizationId) throws SecurityException,
      NotFoundException, TechnicalException {

    return ElasticSearchTransportClient.INSTANCE.delete(indexName, indexType, organizationId);
  }
}
