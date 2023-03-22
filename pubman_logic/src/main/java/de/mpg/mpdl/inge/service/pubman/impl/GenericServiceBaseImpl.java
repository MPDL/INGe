package de.mpg.mpdl.inge.service.pubman.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

public abstract class GenericServiceBaseImpl<ModelObject> implements GenericServiceBase<ModelObject> {


  @Autowired
  private AuthorizationService aaService;

  @PersistenceContext
  EntityManager entityManager;

  private Map<String, ElasticSearchIndexField> indexFields;

  private static final Logger logger = Logger.getLogger(GenericServiceBaseImpl.class);

  protected String beanName;

  protected ObjectMapper objectMapper = new ObjectMapper();

  @Scheduled(fixedDelay = 3600000, initialDelay = 0)
  public void initSearchIndexFields() {
    try {
      logger.info("CRON: initSearchIndexFields() started...");
      Map<String, ElasticSearchIndexField> indexFields = getElasticDao().getIndexFields();
      this.indexFields = indexFields;
      logger.info("CRON: initSearchIndexFields() finished (" + this.indexFields.size() + ").");
    } catch (IngeTechnicalException e) {
      logger.info("CRON: initSearchIndexFields() failed!");
    }
  }


  public Map<String, ElasticSearchIndexField> getElasticSearchIndexFields() {
    return this.indexFields;
  }


  public SearchRetrieveResponseVO<ModelObject> search(SearchRetrieveRequestVO srr, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    if (getElasticDao() != null) {

      //ObjectNode queryNode = (ObjectNode) ElasticSearchGenericDAOImpl.toJsonNode(srr.getQueryBuilder());
      Query query = srr.getQueryBuilder();
      if (authenticationToken != null) {
        query = aaService.modifyQueryForAa(this.getClass().getCanonicalName(), query, aaService.checkLoginRequired(authenticationToken));
      } else {
        query = aaService.modifyQueryForAa(this.getClass().getCanonicalName(), query, (Object[]) null);
      }

      srr.setQueryBuilder(query);

      return getElasticDao().search(this.indexFields, srr);
    }
    return null;
  }


  @Override
  public ResponseBody<ObjectNode> searchDetailed(SearchRequest ssb, long scrollTime, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    if (getElasticDao() != null) {

      Query query = ssb.query();


      //ObjectNode queryNode = (ObjectNode) searchRequestNode.get("query");
      if (authenticationToken != null) {
        query = aaService.modifyQueryForAa(this.getClass().getCanonicalName(), query, aaService.checkLoginRequired(authenticationToken));
      } else {
        query = aaService.modifyQueryForAa(this.getClass().getCanonicalName(), query, (Object[]) null);
      }

      ObjectNode searchRequestNode = (ObjectNode) ElasticSearchGenericDAOImpl.toJsonNode(ssb);
      ObjectNode queryNode = (ObjectNode) ElasticSearchGenericDAOImpl.toJsonNode(query);
      searchRequestNode.set("query", queryNode);
      return getElasticDao().searchDetailed(searchRequestNode, scrollTime);
    }
    return null;
  }

  public ResponseBody<ObjectNode> scrollOn(String scrollId, long scrollTime)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return getElasticDao().scrollOn(scrollId, scrollTime);
  }


  @Override
  public ResponseBody<ObjectNode> searchDetailed(SearchRequest ssb, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    return searchDetailed(ssb, -1, authenticationToken);
  }


  protected void checkAa(String method, Principal userAccount, Object... objects)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    if (objects == null) {
      objects = new Object[0];
    }
    objects = Stream.concat(Arrays.stream(new Object[] {userAccount}), Arrays.stream(objects)).toArray();
    aaService.checkAuthorization(this.getClass().getCanonicalName(), method, objects);
  }



  protected abstract GenericDaoEs<ModelObject> getElasticDao();



  protected void checkEqualModificationDate(Date date1, Date date2) throws IngeApplicationException {
    if (date1 == null || date2 == null || !new Date(date1.getTime()).equals(new Date(date2.getTime()))) {
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
      if (ex.getCause() != null && ex.getCause().getCause() != null) {
        message.append(" ").append(ex.getCause().getCause().getMessage());
      }
      throw new IngeApplicationException(message.toString(), ex);
    }

  }


}



// public void reindex() throws IngeServiceException, AaException;


