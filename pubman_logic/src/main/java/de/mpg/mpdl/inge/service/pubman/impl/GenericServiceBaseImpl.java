package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.dao.impl.ElasticSearchGenericDAOImpl;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.GenericServiceBase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public abstract class GenericServiceBaseImpl<ModelObject> implements GenericServiceBase<ModelObject> {


  @Autowired
  private AuthorizationService aaService;

  @PersistenceContext
  EntityManager entityManager;

  private Map<String, ElasticSearchIndexField> indexFields;

  private static final Logger logger = LogManager.getLogger(GenericServiceBaseImpl.class);

  protected String beanName;

  protected ObjectMapper objectMapper = new ObjectMapper();

  @Scheduled(cron = "${inge.cron.init_search_index_fields}")
  @PostConstruct
  public void initSearchIndexFields() {
    try {
      logger.info("*** CRON (" + PropertyReader.getProperty(PropertyReader.INGE_CRON_INIT_SEARCH_INDEX_FIELDS)
          + "): initSearchIndexFields() started for " + this.getClass().getName() + " ...");
      if (getElasticDao() != null) {
        Map<String, ElasticSearchIndexField> indexFields = getElasticDao().getIndexFields();
        this.indexFields = indexFields;
        if (indexFields != null) {
          logger.info("*** CRON: initSearchIndexFields() finished (" + this.indexFields.size() + " fields found).");
        }
      } else {
        logger.info("*** CRON: initSearchIndexFields() finished. No search index available for " + this.getClass().getName());
      }

    } catch (IngeTechnicalException e) {
      logger.info("*** CRON: initSearchIndexFields() failed!");
    }
  }


  public Map<String, ElasticSearchIndexField> getElasticSearchIndexFields() {
    return this.indexFields;
  }


  public SearchRetrieveResponseVO<ModelObject> search(SearchRetrieveRequestVO srr, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    if (null != getElasticDao()) {

      //ObjectNode queryNode = (ObjectNode) ElasticSearchGenericDAOImpl.toJsonNode(srr.getQueryBuilder());
      Query query = srr.getQueryBuilder();
      if (null != authenticationToken) {
        query = this.aaService.modifyQueryForAa(this.getClass().getCanonicalName(), query,
            this.aaService.checkLoginRequired(authenticationToken));
      } else {
        query = this.aaService.modifyQueryForAa(this.getClass().getCanonicalName(), query, (Object[]) null);
      }

      srr.setQueryBuilder(query);

      return getElasticDao().search(this.indexFields, srr);
    }
    return null;
  }


  @Override
  public ResponseBody<ObjectNode> searchDetailed(SearchRequest ssb, long scrollTime, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    if (null != getElasticDao()) {

      Query query = ssb.query();


      //ObjectNode queryNode = (ObjectNode) searchRequestNode.get("query");
      if (null != authenticationToken) {
        query = this.aaService.modifyQueryForAa(this.getClass().getCanonicalName(), query,
            this.aaService.checkLoginRequired(authenticationToken));
      } else {
        query = this.aaService.modifyQueryForAa(this.getClass().getCanonicalName(), query, (Object[]) null);
      }

      ObjectNode searchRequestNode = (ObjectNode) ElasticSearchGenericDAOImpl.toJsonNode(ssb);
      ObjectNode queryNode = (ObjectNode) ElasticSearchGenericDAOImpl.toJsonNode(query);
      searchRequestNode.set("query", queryNode);
      return getElasticDao().searchDetailed(searchRequestNode, scrollTime);
    }
    return null;
  }

  public ResponseBody<ObjectNode> scrollOn(String scrollId, long scrollTime) throws IngeTechnicalException {
    return getElasticDao().scrollOn(scrollId, scrollTime);
  }


  @Override
  public ResponseBody<ObjectNode> searchDetailed(SearchRequest ssb, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    return searchDetailed(ssb, -1, authenticationToken);
  }


  protected void checkAa(String method, Principal userAccount, Object... objects)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    if (null == objects) {
      objects = new Object[0];
    }
    objects = Stream.concat(Arrays.stream(new Object[] {userAccount}), Arrays.stream(objects)).toArray();
    this.aaService.checkAuthorization(this.getClass().getCanonicalName(), method, objects);
  }



  protected abstract GenericDaoEs<ModelObject> getElasticDao();



  protected void checkEqualModificationDate(Date date1, Date date2) throws IngeApplicationException {
    if (null == date1 || null == date2 || !new Date(date1.getTime()).equals(new Date(date2.getTime()))) {
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


