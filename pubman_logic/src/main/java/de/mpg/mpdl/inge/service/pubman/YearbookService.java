package de.mpg.mpdl.inge.service.pubman;

import java.util.List;

import org.hibernate.Criteria;

import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface YearbookService extends GenericService<YearbookDbVO> {

  /*
   * public List<YearbookDbVO> query(String jpql, List<Object> params, String authenticationToken)
   * throws IngeTechnicalException, AuthenticationException, AuthorizationException,
   * IngeApplicationException;
   */

  public YearbookDbVO submitYearbook(int yearbookId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;

  public YearbookDbVO releaseYearbook(int yearbookId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;

  public YearbookDbVO reviseYearbook(int yearbookId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;



}
