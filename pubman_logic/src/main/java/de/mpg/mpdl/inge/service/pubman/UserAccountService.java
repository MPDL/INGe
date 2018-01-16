package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface UserAccountService extends GenericService<AccountUserDbVO, String> {


  public AccountUserDbVO get(String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public String login(String username, String password)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public String login(String username, String password, HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public void logout(String authenticationToken, HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AccountUserDbVO removeGrants(String userId, Date modificationDate, GrantVO[] grants, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AccountUserDbVO addGrants(String userId, Date modificationDate, GrantVO[] grants, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AccountUserDbVO changePassword(String userId, Date modificationDate, String newPassword, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AccountUserDbVO activate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AccountUserDbVO deactivate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;
}
