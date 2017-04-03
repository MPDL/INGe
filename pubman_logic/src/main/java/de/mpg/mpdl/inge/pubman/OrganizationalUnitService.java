package de.mpg.mpdl.inge.pubman;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.services.IngeServiceException;
import de.mpg.mpdl.inge.services.OrganizationInterface;
import de.mpg.mpdl.inge.services.OrganizationInterfaceConnectorFactory;
import de.mpg.mpdl.inge.services.SearchInterface;
import de.mpg.mpdl.inge.services.SearchInterfaceConnectorFactory;

public class OrganizationalUnitService {
  
  private static final OrganizationalUnitService instance = new OrganizationalUnitService();


  private SearchInterface<QueryBuilder> searchService;
  private OrganizationInterface organizationService;

  private static Logger logger = Logger.getLogger(OrganizationalUnitService.class);

  /**
   * Returns all top-level affiliations.
   * 
   * @return all top-level affiliations
   * @throws Exception if framework access fails
   */

  protected OrganizationalUnitService() {
    try {
      this.searchService = SearchInterfaceConnectorFactory.getInstance();
      this.organizationService = OrganizationInterfaceConnectorFactory.getInstance();
    } catch (Exception e) {
      logger.error("Could not initialize OrganizationalUnitService", e);
    }
  }

  public List<AffiliationVO> searchTopLevelOrganizations() throws IngeServiceException {

    QueryBuilder qb = QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("parentAffiliations"));
    SearchRetrieveRequestVO<QueryBuilder> srr = new SearchRetrieveRequestVO<QueryBuilder>(qb);
    SearchRetrieveResponseVO<AffiliationVO> response = searchService.searchForOrganizations(srr);
    return response.getRecords().stream().map(rec -> rec.getData())
        .collect(Collectors.toList());
  }

  public AffiliationVO getOrganizationalUnit(String organizationId) throws IngeServiceException {
    return organizationService.readOrganization(organizationId);
  }

  /**
   * Returns all child affiliations of a given affiliation.
   * 
   * @param parentAffiliation The parent affiliation
   * 
   * @return all child affiliations
   * @throws Exception if framework access fails
   */
  public List<AffiliationVO> searchChildOrganizations(String parentAffiliationId) throws Exception {


    QueryBuilder qb = QueryBuilders.termQuery("parentAffiliations.objectId", parentAffiliationId);
    SearchRetrieveRequestVO<QueryBuilder> srr = new SearchRetrieveRequestVO<QueryBuilder>(qb);
    SearchRetrieveResponseVO<AffiliationVO> response = searchService.searchForOrganizations(srr);
    return response.getRecords().stream().map(rec -> rec.getData())
        .collect(Collectors.toList());

  }
  
  public SearchRetrieveResponseVO<AffiliationVO> searchOrganizations(SearchRetrieveRequestVO<QueryBuilder> srr) throws Exception {
    return searchService.searchForOrganizations(srr);
  }
  
  public static OrganizationalUnitService getInstance()
  {
    return instance;
  }

}
