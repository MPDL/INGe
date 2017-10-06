package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;

import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface YearbookService extends GenericService<YearbookDbVO> {

  /*
   * public List<YearbookDbVO> query(String jpql, List<Object> params, String authenticationToken)
   * throws IngeTechnicalException, AuthenticationException, AuthorizationException,
   * IngeApplicationException;
   */

  public YearbookDbVO submit(String yearbookId, Date lastModificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;

  public YearbookDbVO release(String yearbookId, Date lastModificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;

  public YearbookDbVO revise(String yearbookId, Date lastModificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;



}
