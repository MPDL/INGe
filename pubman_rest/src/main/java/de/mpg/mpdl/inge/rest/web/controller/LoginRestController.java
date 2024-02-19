package de.mpg.mpdl.inge.rest.web.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@Tag(name = "Login / Logout")
public class LoginRestController {

  private final UserAccountService userSvc;

  @Autowired
  public LoginRestController(UserAccountService userSvc) {
    this.userSvc = userSvc;
  }

  @RequestMapping(path = "login", method = POST, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<?> login(@RequestBody String credentials, HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IngeTechnicalException {
    String[] splittedCredentials = credentials.split(":");

    if (2 != splittedCredentials.length) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    String username = splittedCredentials[0];
    String password = splittedCredentials[1];
    Principal principal = this.userSvc.login(username, password, request, response);
    if (null != principal && !principal.getJwToken().isEmpty()) {
      HttpHeaders headers = new HttpHeaders();
      String TOKEN_HEADER = "Token";
      headers.add(TOKEN_HEADER, principal.getJwToken());
      return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    return null;
  }

  @RequestMapping(path = "login/who", method = GET, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<AccountUserDbVO> getUser(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token)
      throws AuthenticationException {
    AccountUserDbVO user = this.userSvc.get(token);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @RequestMapping(path = "logout", method = GET, produces = APPLICATION_JSON_VALUE)
  public String logout(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, HttpServletRequest request,
      HttpServletResponse response) {
    this.userSvc.logout(token, request, response);

    return "Successfully logged out";
  }

}
