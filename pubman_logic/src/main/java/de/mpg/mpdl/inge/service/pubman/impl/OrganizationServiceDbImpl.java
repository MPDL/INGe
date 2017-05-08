package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.dao.OrganizationDao;
import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;
import de.mpg.mpdl.inge.services.IngeServiceException;

@Service
@Primary
public class OrganizationServiceDbImpl implements OrganizationService {

  private final static Logger logger = LogManager.getLogger();

  @Autowired
  private OrganizationDao<QueryBuilder> organizationDao;

  @Autowired
  private OrganizationRepository organizationRepository;

  @PersistenceContext
  EntityManager entityManager;

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
    return EntityTransformer.transformToOld(this.organizationRepository.findOne(id));
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

  public void reindex() {

    Query<de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO> query =
        (Query<de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO>) entityManager
            .createQuery("SELECT ou FROM AffiliationVO ou");
    query.setReadOnly(true);
    query.setFetchSize(1000);
    query.setCacheable(false);
    ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

    while (results.next()) {
      try {
        de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO object =
            (de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO) results.get(0);
        AffiliationVO aff = EntityTransformer.transformToOld(object);
        logger.info("Reindexing ou " + aff.getReference().getObjectId());
        organizationDao.create(aff.getReference().getObjectId(), aff);
      } catch (Exception e) {
        logger.error("Error while reindexing ", e);
      }


    }

  }
}
