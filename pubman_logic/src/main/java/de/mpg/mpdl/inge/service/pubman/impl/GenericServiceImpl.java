package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.BasicDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO.State;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.GenericService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;

public abstract class GenericServiceImpl<ModelObject extends ValueObject, DbObject extends BasicDbRO>
    implements GenericService<ModelObject> {

  @Autowired
  private AuthorizationService aaService;

  @Transactional
  @Override
  public ModelObject create(ModelObject object, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    DbObject objectToCreate = createEmptyDbObject();
    List<String> reindexList = updateObjectWithValues(object, objectToCreate, userAccount, true);
    updateWithTechnicalMetadata(objectToCreate, userAccount, true);
    checkAa("create", userAccount, transformToOld(objectToCreate));
    objectToCreate = getDbRepository().save(objectToCreate);
    ModelObject objectToReturn = transformToOld(objectToCreate);
    getElasticDao().create(objectToCreate.getObjectId(), objectToReturn);
    if (reindexList != null) {
      reindex(reindexList);;
    }
    return objectToReturn;
  }

  @Transactional
  @Override
  public ModelObject update(ModelObject object, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    DbObject objectToBeUpdated = getDbRepository().findOne(getObjectId(object));
    if (objectToBeUpdated == null) {
      throw new IngeServiceException("Object with given id not found.");
    }
    List<String> reindexList =
        updateObjectWithValues(object, objectToBeUpdated, userAccount, false);
    updateWithTechnicalMetadata(objectToBeUpdated, userAccount, false);

    checkAa("update", userAccount, transformToOld(objectToBeUpdated));
    objectToBeUpdated = getDbRepository().save(objectToBeUpdated);

    ModelObject objectToReturn = transformToOld(objectToBeUpdated);
    getElasticDao().update(objectToBeUpdated.getObjectId(), objectToReturn);
    if (reindexList != null) {
      reindex(reindexList);;
    }
    return objectToReturn;
  }



  @Transactional
  @Override
  public void delete(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    DbObject objectToBeDeleted = getDbRepository().findOne(id);
    checkAa("delete", userAccount, transformToOld(objectToBeDeleted));
    getDbRepository().delete(id);
    getElasticDao().delete(id);

  }

  @Transactional(readOnly = true)
  @Override
  public ModelObject get(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    AccountUserVO userAccount = null;
    ModelObject object = transformToOld(getDbRepository().findOne(id));
    if (authenticationToken != null) {
      userAccount = aaService.checkLoginRequired(authenticationToken);
    }

    checkAa("get", userAccount, object);
    return object;
  }

  public SearchRetrieveResponseVO<ModelObject> search(SearchRetrieveRequestVO<QueryBuilder> srr,
      String authenticationToken) throws IngeServiceException, AaException {

    QueryBuilder qb = srr.getQueryObject();
    if (authenticationToken != null) {
      qb =
          aaService.modifyQueryForAa(this.getClass().getCanonicalName(), qb,
              aaService.checkLoginRequired(authenticationToken));
    } else {
      qb = aaService.modifyQueryForAa(this.getClass().getCanonicalName(), qb, null);
    }
    srr.setQueryObject(qb);
    System.out.println(srr.getQueryObject().toString());
    return getElasticDao().search(srr);
  }


  protected void checkAa(String method, AccountUserVO userAccount, Object... objects)
      throws AaException, IngeServiceException {
    aaService.checkAuthorization(this.getClass().getCanonicalName(), method, userAccount, objects);
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
      throws IngeServiceException;

  protected abstract ModelObject transformToOld(DbObject dbObject);

  protected abstract JpaRepository<DbObject, String> getDbRepository();

  protected abstract GenericDaoEs<ModelObject, QueryBuilder> getElasticDao();

  protected abstract String getObjectId(ModelObject object);

  protected void reindex(List<String> idList) throws IngeServiceException {
    // Reindex old and new Parents
    for (String id : idList) {
      ModelObject vo = transformToOld(getDbRepository().findOne(id));
      getElasticDao().create(id, vo);
    }

  }


}



// public void reindex() throws IngeServiceException, AaException;


