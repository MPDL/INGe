package de.mpg.mpdl.inge.service.pubman;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface GenericServiceBase<E> {

  public SearchRetrieveResponseVO<E> search(SearchRetrieveRequestVO srr, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public SearchResponse searchDetailed(SearchSourceBuilder ssb, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public SearchResponse searchDetailed(SearchSourceBuilder ssb, Scroll scroll, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public SearchResponse scrollOn(String scrollId, Scroll scroll)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public Map<String, ElasticSearchIndexField> getElasticSearchIndexFields();

  public void initSearchIndexFields();

}
