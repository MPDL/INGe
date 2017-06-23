package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface UserAccountService extends GenericService<AccountUserVO> {


  public AccountUserVO get(String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException;

  public String login(String username, String password) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException;

  public AccountUserVO removeGrants(String userId, Date modificationDate, GrantVO[] grants,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException;

  public AccountUserVO addGrants(String userId, Date modificationDate, GrantVO[] grants,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException;

  public void changePassword(String userId, Date modificationDate, String newPassword,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException;

  public AccountUserVO activate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;

  public AccountUserVO deactivate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;
}
