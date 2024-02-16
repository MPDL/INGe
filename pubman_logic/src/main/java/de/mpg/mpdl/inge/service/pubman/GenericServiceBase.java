package de.mpg.mpdl.inge.service.pubman;

import java.util.Map;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface GenericServiceBase<E> {

  SearchRetrieveResponseVO<E> search(SearchRetrieveRequestVO srr, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  ResponseBody searchDetailed(SearchRequest ssb, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  ResponseBody searchDetailed(SearchRequest ssb, long scrollTime, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  ResponseBody scrollOn(String scrollId, long scrollTime)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  Map<String, ElasticSearchIndexField> getElasticSearchIndexFields();

  void initSearchIndexFields();

}
