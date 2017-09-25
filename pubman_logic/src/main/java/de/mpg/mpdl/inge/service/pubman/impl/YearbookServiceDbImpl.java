package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.YearbookRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.dao.YearbookDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO.State;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.YearbookService;

@Service
public class YearbookServiceDbImpl extends GenericServiceImpl<YearbookDbVO, YearbookDbVO> implements
    YearbookService {


  public final static String INDEX_OBJECT_ID = "objectId";
  public final static String INDEX_ORGANIZATION_ID = "organization.objectId";
  public final static String INDEX_YEAR = "year";

  @Autowired
  private YearbookDaoEs yearbookDao;

  @Autowired
  private YearbookRepository yearbookRepository;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private IdentifierProviderServiceImpl idProviderService;


  /*
   * @Override public List<YearbookDbVO> query(String jpql, List<Object> params, String
   * authenticationToken) throws IngeTechnicalException, AuthenticationException,
   * AuthorizationException, IngeApplicationException { Query q = entityManager.createQuery(jpql,
   * YearbookDbVO.class);
   * 
   * if (params != null) { for (int i = 0; i < params.size(); i++) { q.setParameter(i,
   * params.get(i)); } }
   * 
   * List<YearbookDbVO> result = q.getResultList(); return result; }
   */

  @Override
  public YearbookDbVO submitYearbook(int yearbookId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public YearbookDbVO releaseYearbook(int yearbookId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public YearbookDbVO reviseYearbook(int yearbookId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  protected YearbookDbVO createEmptyDbObject() {
    return new YearbookDbVO();
  }

  @Override
  protected YearbookDbVO transformToOld(YearbookDbVO dbObject) {
    return dbObject;
  }

  @Override
  protected JpaRepository<YearbookDbVO, String> getDbRepository() {
    return yearbookRepository;
  }

  @Override
  protected GenericDaoEs<YearbookDbVO> getElasticDao() {
    return yearbookDao;
  }

  @Override
  protected String getObjectId(YearbookDbVO object) {
    return object.getObjectId();
  }

  @Override
  protected Date getModificationDate(YearbookDbVO object) {
    return object.getLastModificationDate();
  }


  protected List<String> updateObjectWithValues(YearbookDbVO givenObject,
      YearbookDbVO objectToBeUpdated, AccountUserVO userAccount, boolean create)
      throws IngeTechnicalException, IngeApplicationException {

    objectToBeUpdated.setContextIds(givenObject.getContextIds());
    objectToBeUpdated.setItemIds(givenObject.getItemIds());
    objectToBeUpdated.setOrganization(givenObject.getOrganization());
    objectToBeUpdated.setYear(givenObject.getYear());

    if (create) {
      objectToBeUpdated.setState(State.CREATED);
      objectToBeUpdated.setObjectId(idProviderService.getNewId(ID_PREFIX.YEARBOOK));
    }

    return null;

  }



}
