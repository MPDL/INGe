package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.dao.OrganizationDao;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.services.IngeServiceException;

@Service
public class OrganizationServiceImpl implements OrganizationService {

  public final static String INDEX_OBJECT_ID = "reference.objectId";

  @Autowired
  private OrganizationDao<QueryBuilder> organizationDao;

  /**
   * Returns all top-level affiliations.
   * 
   * @return all top-level affiliations
   * @throws Exception if framework access fails
   */
  public List<AffiliationVO> searchTopLevelOrganizations() throws IngeServiceException {
    final QueryBuilder qb = QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("parentAffiliations"));
    final SearchRetrieveRequestVO<QueryBuilder> srr = new SearchRetrieveRequestVO<QueryBuilder>(qb);
    final SearchRetrieveResponseVO<AffiliationVO> response = this.organizationDao.search(srr);
    
    return response.getRecords().stream().map(rec -> rec.getData())
        .collect(Collectors.toList());
  }

  @Override
  public AffiliationVO get(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    return this.organizationDao.get(id);
  }

  /**
   * Returns all child affiliations of a given affiliation.
   * 
   * @param parentAffiliation The parent affiliation
   * 
   * @return all child affiliations
   * @throws Exception if framework access fails
   */
  public List<AffiliationVO> searchChildOrganizations(String parentAffiliationId) throws IngeServiceException {
    final QueryBuilder qb = QueryBuilders.termQuery("parentAffiliations.objectId", parentAffiliationId);
    final SearchRetrieveRequestVO<QueryBuilder> srr = new SearchRetrieveRequestVO<QueryBuilder>(qb);
    final SearchRetrieveResponseVO<AffiliationVO> response = this.organizationDao.search(srr);
    
    return response.getRecords().stream().map(rec -> rec.getData())
        .collect(Collectors.toList());
  }

  @Override
  public SearchRetrieveResponseVO<AffiliationVO> search(SearchRetrieveRequestVO<QueryBuilder> srr,
      String authenticationToken) throws IngeServiceException, AaException {
    return this.organizationDao.search(srr);
  }

  @Override
  public AffiliationVO create(AffiliationVO pubItem, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    return null;
  }

  @Override
  public AffiliationVO update(AffiliationVO pubItem, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    return null;
  }

  @Override
  public void delete(String id, String authenticationToken) throws IngeServiceException,
      AaException {}
}
