package de.mpg.mpdl.inge.service.pubman.impl;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.BasicDbRO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.GenericService;

public abstract class GenericServiceImpl<ModelObject, DbObject extends BasicDbRO> implements
    GenericService<ModelObject> {

  @Autowired
  private AuthorizationService aaService;

  @PersistenceContext
  EntityManager entityManager;

  private final static Logger logger = LogManager.getLogger(GenericServiceImpl.class);

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public ModelObject create(ModelObject object, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    DbObject objectToCreate = createEmptyDbObject();
    List<String> reindexList = updateObjectWithValues(object, objectToCreate, userAccount, true);
    updateWithTechnicalMetadata(objectToCreate, userAccount, true);
    checkAa("create", userAccount, transformToOld(objectToCreate));
    try {
      objectToCreate = getDbRepository().saveAndFlush(objectToCreate);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    ModelObject objectToReturn = transformToOld(objectToCreate);
    if (getElasticDao() != null) {
      getElasticDao().createImmediately(objectToCreate.getObjectId(), objectToReturn);
    }

    if (reindexList != null) {
      reindex(reindexList);;
    }
    return objectToReturn;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public ModelObject update(ModelObject object, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    DbObject objectToBeUpdated = getDbRepository().findOne(getObjectId(object));
    if (objectToBeUpdated == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }
    checkEqualModificationDate(getModificationDate(object),
        getModificationDate(transformToOld(objectToBeUpdated)));
    List<String> reindexList =
        updateObjectWithValues(object, objectToBeUpdated, userAccount, false);
    updateWithTechnicalMetadata(objectToBeUpdated, userAccount, false);

    checkAa("update", userAccount, transformToOld(objectToBeUpdated));
    try {
      objectToBeUpdated = getDbRepository().saveAndFlush(objectToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    ModelObject objectToReturn = transformToOld(objectToBeUpdated);
    if (getElasticDao() != null) {
      getElasticDao().updateImmediately(objectToBeUpdated.getObjectId(), objectToReturn);
    }
    if (reindexList != null) {
      reindex(reindexList);
    }
    return objectToReturn;
  }



  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void delete(String id, String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    DbObject objectToBeDeleted = getDbRepository().findOne(id);
    if (objectToBeDeleted == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }
    checkAa("delete", userAccount, transformToOld(objectToBeDeleted));
    getDbRepository().delete(id);
    if (getElasticDao() != null) {
      getElasticDao().delete(id);
    }

  }

  @Transactional(readOnly = true)
  @Override
  public ModelObject get(String id, String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException {
    AccountUserVO userAccount = null;
    ModelObject object = transformToOld(getDbRepository().findOne(id));
    if (authenticationToken != null) {
      userAccount = aaService.checkLoginRequired(authenticationToken);
    }

    checkAa("get", userAccount, object);
    return object;
  }

  public SearchRetrieveResponseVO<ModelObject> search(SearchRetrieveRequestVO srr,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {

    if (getElasticDao() != null) {
      QueryBuilder qb = srr.getQueryBuilder();
      if (authenticationToken != null) {
        qb =
            aaService.modifyQueryForAa(this.getClass().getCanonicalName(), qb,
                aaService.checkLoginRequired(authenticationToken));
      } else {
        qb = aaService.modifyQueryForAa(this.getClass().getCanonicalName(), qb, null);
      }
      srr.setQueryBuilder(qb);
      if (srr.getQueryBuilder() != null) {
        System.out.println(srr.getQueryBuilder().toString());
      }

      return getElasticDao().search(srr);
    }
    return null;
  }


  protected void checkAa(String method, AccountUserVO userAccount, Object... objects)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    if (objects == null) {
      objects = new Object[0];
    }
    objects =
        Stream.concat(Arrays.stream(new Object[] {userAccount}), Arrays.stream(objects)).toArray();
    aaService.checkAuthorization(this.getClass().getCanonicalName(), method, objects);
  }

  protected void updateWithTechnicalMetadata(DbObject object, AccountUserVO userAccount,
      boolean create) {
    Date currentDate = new Date();
    AccountUserDbRO mod = new AccountUserDbRO();
    mod.setName(userAccount.getName());
    mod.setObjectId(userAccount.getReference().getObjectId());

    if (create) {
      object.setCreationDate(currentDate);
      object.setCreator(mod);
    }

    object.setLastModificationDate(currentDate);
    object.setModifier(mod);
  }

  protected abstract DbObject createEmptyDbObject();

  protected abstract List<String> updateObjectWithValues(ModelObject givenObject,
      DbObject objectToBeUpdated, AccountUserVO userAccount, boolean create)
      throws IngeTechnicalException, IngeApplicationException;

  protected abstract ModelObject transformToOld(DbObject dbObject);

  protected abstract JpaRepository<DbObject, String> getDbRepository();

  protected abstract GenericDaoEs<ModelObject> getElasticDao();

  protected abstract String getObjectId(ModelObject object);

  protected abstract Date getModificationDate(ModelObject object);

  protected void reindex(List<String> idList) throws IngeTechnicalException {
    // Reindex old and new Parents
    if (getElasticDao() != null) {
      for (String id : idList) {
        ModelObject vo = transformToOld(getDbRepository().findOne(id));

        getElasticDao().createImmediately(id, vo);
      }
    }
  }

  protected void checkEqualModificationDate(Date date1, Date date2) throws IngeApplicationException {
    if (date1 == null || date2 == null || !date1.equals(date2)) {
      throw new IngeApplicationException("Object changed in the meantime: " + date1
          + "  does not equal  " + date2);
    }
  }


  protected static void handleDBException(DataAccessException exception)
      throws IngeApplicationException {

    try {
      throw exception;
    } catch (ObjectRetrievalFailureException ex) {
      throw new IngeApplicationException(ex.getMessage(), ex);
    } catch (DataIntegrityViolationException ex) {
      StringBuilder message = new StringBuilder("Object already exists.");
      // Get message from
      if (ex.getCause() != null && ex.getCause().getCause() != null) {
        message.append(" ").append(ex.getCause().getCause().getMessage());
      }
      throw new IngeApplicationException(message.toString(), ex);
    }

  }


  @Transactional(readOnly = true)
  public void reindex() {

    if (getElasticDao() != null) {
      String entityName =
          ((Class<ModelObject>) ((ParameterizedType) getClass().getGenericSuperclass())
              .getActualTypeArguments()[0]).getSimpleName();


      Query<de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO> query =
          (Query<de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO>) entityManager
              .createQuery("SELECT e FROM " + entityName + " e");
      query.setReadOnly(true);
      query.setFetchSize(500);
      query.setCacheMode(CacheMode.IGNORE);
      query.setFlushMode(FlushModeType.COMMIT);
      query.setCacheable(false);
      ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

      int count = 0;
      while (results.next()) {
        try {
          count++;
          DbObject dbObject = (DbObject) results.get(0);
          logger.info("(" + count + ") Reindexing " + entityName + " " + dbObject.getObjectId());
          getElasticDao().create(dbObject.getObjectId(), transformToOld(dbObject));

          // Clear entity manager after every 1000 items, otherwise OutOfMemory can occur
          if (count % 1000 == 0) {
            logger.info("Clearing entity manager while reindexing");
            entityManager.flush();
            entityManager.clear();
          }

        } catch (Exception e) {
          logger.error("Error while reindexing ", e);
        }


      }
    }
  }

}



// public void reindex() throws IngeServiceException, AaException;


