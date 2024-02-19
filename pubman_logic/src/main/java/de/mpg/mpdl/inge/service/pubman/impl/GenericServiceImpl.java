package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BasicDbRO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.GenericService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;

public abstract class GenericServiceImpl<ModelObject extends BasicDbRO, Id extends Serializable> extends GenericServiceBaseImpl<ModelObject>
    implements GenericService<ModelObject, Id> {

  @Autowired
  private AuthorizationService aaService;

  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  @Qualifier("queueJmsTemplate")
  private JmsTemplate queueJmsTemplate;

  private static final Logger logger = LogManager.getLogger(GenericServiceImpl.class);

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public ModelObject create(ModelObject object, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = this.aaService.checkLoginRequired(authenticationToken);
    ModelObject objectToCreate = createEmptyDbObject();
    List<Id> reindexList = updateObjectWithValues(object, objectToCreate, principal.getUserAccount(), true);
    updateWithTechnicalMetadata(objectToCreate, principal.getUserAccount(), true);
    checkAa("create", principal, objectToCreate);
    try {
      objectToCreate = getDbRepository().saveAndFlush(objectToCreate);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    if (null != getElasticDao()) {
      getElasticDao().createImmediately(objectToCreate.getObjectId(), objectToCreate);
    }

    if (null != reindexList) {
      // ACHTUNG: siehe Kommentar bei AffiliationDbVO @Formula
      this.entityManager.flush();
      this.entityManager.clear();
      reindex(reindexList);
    }
    return objectToCreate;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public ModelObject update(ModelObject object, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = this.aaService.checkLoginRequired(authenticationToken);
    ModelObject objectToBeUpdated = getDbRepository().findById(getObjectId(object)).orElse(null);
    if (null == objectToBeUpdated) {
      throw new IngeApplicationException("Object with given id not found.");
    }
    checkEqualModificationDate(getModificationDate(object), getModificationDate(objectToBeUpdated));
    List<Id> reindexList = updateObjectWithValues(object, objectToBeUpdated, principal.getUserAccount(), false);
    updateWithTechnicalMetadata(objectToBeUpdated, principal.getUserAccount(), false);

    checkAa("update", principal, objectToBeUpdated);
    try {
      objectToBeUpdated = getDbRepository().saveAndFlush(objectToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    if (null != getElasticDao()) {
      getElasticDao().createImmediately(objectToBeUpdated.getObjectId(), objectToBeUpdated);
    }
    if (null != reindexList) {
      reindex(reindexList);
    }
    return objectToBeUpdated;
  }



  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void delete(Id id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = this.aaService.checkLoginRequired(authenticationToken);
    ModelObject objectToBeDeleted = getDbRepository().findById(id).orElse(null);
    if (null == objectToBeDeleted) {
      throw new IngeApplicationException("Object with given id not found.");
    }
    checkAa("delete", principal, objectToBeDeleted);
    getDbRepository().deleteById(id);
    if (null != getElasticDao()) {
      getElasticDao().deleteImmediatly(getIdForElasticSearch(id));
    }

  }

  @Transactional(readOnly = true)
  @Override
  public ModelObject get(Id id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = null;
    ModelObject object = getDbRepository().findById(id).orElse(null);
    if (null == object) {
      return null;
    }
    if (null != authenticationToken) {
      principal = this.aaService.checkLoginRequired(authenticationToken);
    }

    checkAa("get", principal, object);
    return object;
  }



  protected static void updateWithTechnicalMetadata(BasicDbRO object, AccountUserDbVO userAccount, boolean create) {
    Date currentDate = new Date();
    AccountUserDbRO mod = new AccountUserDbRO();
    //Moved out DSGVO
    //mod.setName(userAccount.getName());
    mod.setObjectId(userAccount.getObjectId());

    if (create) {
      object.setCreationDate(currentDate);
      object.setCreator(mod);
    }

    object.setLastModificationDate(currentDate);
    object.setModifier(mod);
  }



  protected abstract ModelObject createEmptyDbObject();

  protected abstract List<Id> updateObjectWithValues(ModelObject givenObject, ModelObject objectToBeUpdated, AccountUserDbVO userAccount,
      boolean create) throws IngeApplicationException;


  protected abstract JpaRepository<ModelObject, Id> getDbRepository();

  protected abstract GenericDaoEs<ModelObject> getElasticDao();

  protected abstract Id getObjectId(ModelObject object);

  protected abstract Date getModificationDate(ModelObject object);

  protected String getIdForElasticSearch(Id id) {
    return id.toString();
  }

  protected void reindex(List<Id> idList) throws IngeTechnicalException {
    // Reindex old and new Parents
    if (null != getElasticDao()) {
      for (Id id : idList) {
        reindex(id, true);
      }
    }
  }



  @Transactional(readOnly = true)
  protected void reindex(Id id, boolean immediate) throws IngeTechnicalException {
    // Reindex old and new Parents
    if (null != getElasticDao()) {
      ModelObject vo = getDbRepository().findById(id).orElse(null);
      logger.info("Reindexing object " + vo.getObjectId());
      if (immediate) {
        getElasticDao().createImmediately(getIdForElasticSearch(id), vo);
      } else {
        getElasticDao().create(getIdForElasticSearch(id), vo);
      }
    }
  }

  @Override
  public void reindex(Id id, String authenticationToken) throws IngeTechnicalException {
    // TODO AA
    reindex(id, false);
  }



  @Override
  @Transactional(readOnly = true)
  public void reindexAll(String authenticationToken) {

    // TODO AA
    if (null != getElasticDao()) {
      String entityName =
          ((Class<ModelObject>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName();



      Query<Id> query = (Query<Id>) this.entityManager.createQuery("SELECT e.objectId FROM " + entityName + " e");
      query.setReadOnly(true);
      query.setFetchSize(500);
      query.setCacheMode(CacheMode.IGNORE);
      query.setFlushMode(FlushModeType.COMMIT);
      query.setCacheable(false);
      ScrollableResults<Id> results = query.scroll(ScrollMode.FORWARD_ONLY);

      int count = 0;
      while (results.next()) {
        try {
          // Clear entity manager after every 1000 items, otherwise OutOfMemory can occur
          if (0 == count % 1000) {
            logger.info("Clearing entity manager while reindexing");
            this.entityManager.flush();
            this.entityManager.clear();
          }
          count++;

          Id id = results.get();

          this.queueJmsTemplate.convertAndSend("reindex-" + entityName, id);

          //          // Clear entity manager after every 1000 items, otherwise OutOfMemory can occur
          //          if (count % 1000 == 0) {
          //            logger.info("Clearing entity manager while reindexing");
          //            entityManager.flush();
          //            entityManager.clear();
          //          }

        } catch (Exception e) {
          logger.error("Error while reindexing ", e);
        }


      }
    }
  }

  protected void checkEqualModificationDate(Date date1, Date date2) throws IngeApplicationException {
    if (null == date1 || null == date2 || !date1.equals(date2)) {
      throw new IngeApplicationException("Object changed in the meantime: " + date1 + "  does not equal  " + date2);
    }
  }


  protected static void handleDBException(DataAccessException exception) throws IngeApplicationException {

    try {
      throw exception;
    } catch (ObjectRetrievalFailureException ex) {
      throw new IngeApplicationException(ex.getMessage(), ex);
    } catch (DataIntegrityViolationException ex) {
      StringBuilder message = new StringBuilder("Object already exists.");
      // Get message from
      if (null != ex.getCause() && null != ex.getCause().getCause()) {
        message.append(" ").append(ex.getCause().getCause().getMessage());
      }
      throw new IngeApplicationException(message.toString(), ex);
    }

  }



}



// public void reindex() throws IngeServiceException, AaException;


