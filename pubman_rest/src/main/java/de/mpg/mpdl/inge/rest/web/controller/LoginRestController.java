package de.mpg.mpdl.inge.rest.web.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import io.swagger.annotations.Api;

@RestController
@Api(tags="Login / Logout")
public class LoginRestController {

  private final String TOKEN_HEADER = "Token";
  private final String AUTHZ_HEADER = "Authorization";

  private static final Logger logger = LogManager.getLogger(LoginRestController.class);

  private UserAccountService userSvc;

  @Autowired
  public LoginRestController(UserAccountService userSvc) {
    this.userSvc = userSvc;
  }

  @RequestMapping(path = "login", method = POST, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<?> login(@RequestBody String credentials, HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    String username = credentials.split(":")[0];
    String password = credentials.split(":")[1];
    Principal principal = userSvc.login(username, password, request, response);
    if (principal != null && !principal.getJwToken().isEmpty()) {
      HttpHeaders headers = new HttpHeaders();
      headers.add(TOKEN_HEADER, principal.getJwToken());
      return new ResponseEntity<>(headers, HttpStatus.OK);
    }
    return null;
  }

  @RequestMapping(path = "login/who", method = GET, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<AccountUserDbVO> getUser(@RequestHeader(value = AUTHZ_HEADER) String token)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AccountUserDbVO user = userSvc.get(token);
    return new ResponseEntity<AccountUserDbVO>(user, HttpStatus.OK);
  }

  @RequestMapping(path = "logout", method = GET, produces = APPLICATION_JSON_VALUE)
  public String logout(@RequestHeader(value = AUTHZ_HEADER) String token, HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    userSvc.logout(token, request, response);

    return "Successfully logged out";
  }

}
