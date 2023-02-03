package de.mpg.mpdl.inge.service.pubman;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

import java.util.Map;

public interface GenericServiceBase<E> {

  public SearchRetrieveResponseVO<E> search(SearchRetrieveRequestVO srr, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public ResponseBody<ObjectNode> searchDetailed(SearchRequest ssb, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public ResponseBody<ObjectNode> searchDetailed(SearchRequest ssb, long scrollTime, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public ResponseBody<ObjectNode> scrollOn(String scrollId, long scrollTime)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public Map<String, ElasticSearchIndexField> getElasticSearchIndexFields();

  public void initSearchIndexFields();

}
