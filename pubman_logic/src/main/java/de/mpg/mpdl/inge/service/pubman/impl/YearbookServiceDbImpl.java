package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.YearbookRepository;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.dao.YearbookDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO.State;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ReindexListener;
import de.mpg.mpdl.inge.service.pubman.YearbookService;

@Service
public class YearbookServiceDbImpl extends GenericServiceImpl<YearbookDbVO, YearbookDbVO, String>
    implements YearbookService, ReindexListener {

  public final static String INDEX_MODIFICATION_DATE = "lastModificationDate";
  public final static String INDEX_OBJECT_ID = "objectId.keyword";
  public final static String INDEX_ORGANIZATION_ID = "organization.objectId.keyword";
  public final static String INDEX_ORGANIZATION_NAME = "organization.name";
  public final static String INDEX_ORGANIZATION_NAME_KEYWORD = "organization.name.keyword";
  public final static String INDEX_YEAR = "year";
  public final static String INDEX_STATE = "state.keyword";

  @Autowired
  private YearbookDaoEs yearbookDao;

  @Autowired
  private YearbookRepository yearbookRepository;

  @Autowired
  private AuthorizationService aaService;

  // @Autowired
  // private EntityManager entityManager;

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
  @Transactional(rollbackFor = Throwable.class)
  public YearbookDbVO submit(String yearbookId, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    return changeState(yearbookId, modificationDate, authenticationToken, State.SUBMITTED, "submit");
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public YearbookDbVO release(String yearbookId, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(yearbookId, modificationDate, authenticationToken, State.RELEASED, "release");
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public YearbookDbVO revise(String yearbookId, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(yearbookId, modificationDate, authenticationToken, State.CREATED, "revise");
  }

  private YearbookDbVO changeState(String id, Date modificationDate, String authenticationToken, YearbookDbVO.State state,
      String methodName) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    YearbookDbVO yearbookDbToBeUpdated = yearbookRepository.findOne(id);
    if (yearbookDbToBeUpdated == null) {
      throw new IngeApplicationException("Yearbook with given id " + id + " not found.");
    }

    checkEqualModificationDate(modificationDate, yearbookDbToBeUpdated.getLastModificationDate());

    checkAa(methodName, userAccount, yearbookDbToBeUpdated);

    yearbookDbToBeUpdated.setState(state);
    updateWithTechnicalMetadata(yearbookDbToBeUpdated, userAccount, false);

    try {
      yearbookDbToBeUpdated = yearbookRepository.saveAndFlush(yearbookDbToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    getElasticDao().createImmediately(yearbookDbToBeUpdated.getObjectId(), yearbookDbToBeUpdated);
    return yearbookDbToBeUpdated;
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


  protected List<String> updateObjectWithValues(YearbookDbVO givenObject, YearbookDbVO objectToBeUpdated, AccountUserVO userAccount,
      boolean create) throws IngeTechnicalException, IngeApplicationException {

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


  @Override
  @JmsListener(containerFactory = "queueContainerFactory", destination = "reindex-YearbookDbVO")
  public void reindexListener(String id) throws IngeTechnicalException {
    reindex(id, false);

  }



}
