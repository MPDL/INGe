package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;

import com.auth0.jwt.interfaces.DecodedJWT;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserAccountService extends GenericService<AccountUserDbVO, String> {

  public void delete(String userId, String authenticationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthenticationException, AuthorizationException;

  public AccountUserDbVO get(String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public Principal login(String username, String password)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public Principal login(String username, String password, HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public Principal login(HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public Principal loginForPasswordChange(String username, String password) throws IngeTechnicalException, AuthenticationException;

  public void logout(String authenticationToken, HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AccountUserDbVO removeGrants(String userId, Date modificationDate, GrantVO[] grants, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AccountUserDbVO addGrants(String userId, Date modificationDate, GrantVO[] grants, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AccountUserDbVO changePassword(String userId, Date modificationDate, String newPassword, boolean passwordChangeFlag,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AccountUserDbVO activate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AccountUserDbVO deactivate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public DecodedJWT verifyToken(String authenticationToken) throws AuthenticationException;

  public String generateRandomPassword();

}
