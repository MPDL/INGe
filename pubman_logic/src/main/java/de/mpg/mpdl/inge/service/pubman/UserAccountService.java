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

  void delete(String userId, String authenticationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthenticationException, AuthorizationException;

  AccountUserDbVO get(String authenticationToken) throws AuthenticationException;

  Principal login(String username, String password) throws IngeTechnicalException, AuthenticationException;

  Principal login(String username, String password, HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException;

  Principal login(HttpServletRequest request, HttpServletResponse response) throws IngeTechnicalException, AuthenticationException;

  Principal loginForPasswordChange(String username, String password) throws IngeTechnicalException, AuthenticationException;

  void logout(String authenticationToken, HttpServletRequest request, HttpServletResponse response);

  AccountUserDbVO removeGrants(String userId, Date modificationDate, GrantVO[] grants, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  AccountUserDbVO addGrants(String userId, Date modificationDate, GrantVO[] grants, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  AccountUserDbVO changePassword(String userId, Date modificationDate, String newPassword, boolean passwordChangeFlag,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  AccountUserDbVO activate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  AccountUserDbVO deactivate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  DecodedJWT verifyToken(String authenticationToken) throws AuthenticationException;

  String generateRandomPassword();

}
