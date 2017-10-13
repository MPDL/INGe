package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
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
import de.mpg.mpdl.inge.service.pubman.GenericServiceBase;
import de.mpg.mpdl.inge.service.pubman.GenericService;

public abstract class GenericServiceBaseImpl<ModelObject> implements
    GenericServiceBase<ModelObject> {


  @Autowired
  private AuthorizationService aaService;

  @PersistenceContext
  EntityManager entityManager;

  private Map<String, ElasticSearchIndexField> indexFields;

  private final static Logger logger = LogManager.getLogger(GenericServiceBaseImpl.class);


  @Scheduled(fixedDelay = 3600000, initialDelay = 0)
  public void initSearchIndexFields() {
    try {
      Map<String, ElasticSearchIndexField> indexFields = getElasticDao().getIndexFields();
      this.indexFields = indexFields;

    } catch (IngeTechnicalException e) {
      logger.error("Error while renewing list of index fields", e);
    }

  }


  public Map<String, ElasticSearchIndexField> getElasticSearchIndexFields() {
    return indexFields;
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

      return getElasticDao().search(srr);
    }
    return null;
  }


  @Override
  public SearchResponse searchDetailed(SearchSourceBuilder ssb, Scroll scroll,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {

    if (getElasticDao() != null) {
      QueryBuilder qb = ssb.query();
      if (authenticationToken != null) {
        qb =
            aaService.modifyQueryForAa(this.getClass().getCanonicalName(), qb,
                aaService.checkLoginRequired(authenticationToken));
      } else {
        qb = aaService.modifyQueryForAa(this.getClass().getCanonicalName(), qb, null);
      }
      ssb.query(qb);
      return getElasticDao().searchDetailed(ssb, scroll);
    }
    return null;
  }

  public SearchResponse scrollOn(String scrollId, Scroll scroll) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException {
    return getElasticDao().scrollOn(scrollId, scroll);
  }


  @Override
  public SearchResponse searchDetailed(SearchSourceBuilder ssb, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {

    return searchDetailed(ssb, null, authenticationToken);
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



  protected abstract GenericDaoEs<ModelObject> getElasticDao();



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

}



// public void reindex() throws IngeServiceException, AaException;


